package cn.easyproject.easybackup.job;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import cn.easyproject.easybackup.backup.interceptor.BackupAfter;
import cn.easyproject.easybackup.backup.interceptor.BackupBefore;
import cn.easyproject.easybackup.configuration.BackupConfiguration;
import cn.easyproject.easybackup.sender.Sender;
import cn.easyproject.easybackup.util.FreemarkerUtil;
import cn.easyproject.easybackup.util.SpringUtil;

/**
 * EasyMonitor Job execute
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
@DisallowConcurrentExecution
public class BackupJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BackupJob.class);


	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		long start=System.currentTimeMillis();
		// 配置信息
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		BackupConfiguration configuration = (BackupConfiguration) jobDataMap.get("configuration");
		// 必须是有效配置
		if(configuration==null){
			return;
		}
		
		// 如果备份对象不完全，取消备份
		if(configuration.getTargetFile()==null 
				||  configuration.getBackupDir()==null
				||  configuration.getBackupFile()==null
				||  configuration.getBackup()==null
				){
			return;
		}
		
		// 任务对象
		JobManager jobManager = SpringUtil.get("jobManager");
		
		// 准备备份，暂停定时任务
		jobManager.pauseJob(context);
		
		// Command&Interceptor before
		commandExecute(configuration, configuration.getBeforeClass());
		
		if(configuration.getBefore().size()>0){
			for (BackupBefore before : configuration.getBefore()) {
				logger.debug(configuration.getType().name() 
							+ 
							"-" 
							+ configuration.getName()
							+ " execute before interceptor ["+before.getClass().getName()+"].");
				boolean res=before.execute(configuration);
				if(!res){
					logger.info(
							configuration.getType().name() 
							+ 
							"-" 
							+ configuration.getName() 
							+ " BackupBefore interceptor class ["+before.getClass().getName()+"] "
									+ " return false, the backup cancel.");
					// 恢复定时任务
					jobManager.resumeJob(context);
					
					return;
				}
			}
		}
		

		
		File targetFile=configuration.getTargetFile().getFile(configuration);
		
		if(targetFile==null){
			logger.error(configuration.getType().name() 
					+ 
					"-" 
					+ configuration.getName()
					+ " cancel backup, the file ["+targetFile+"] you want backup is null.");
			// 恢复定时任务
			jobManager.resumeJob(context);
			return;
		}
		if(!targetFile.exists()){
			logger.warn(configuration.getType().name() 
					+ 
					"-" 
					+ configuration.getName()
					+ " cancel backup, he file ["+targetFile+"] you want backup is not exists.");
			// 恢复定时任务
			jobManager.resumeJob(context);
			return;
		}
		
		
		
	
		
		
		// 获得备份文件和目录
		List<File> backupDirs=configuration.getBackupDir().getDir(configuration);
		
		
		if(backupDirs.size()==0){
			logger.error(configuration.getType().name() 
					+ 
					"-" 
					+ configuration.getName()
					+ " backup dir ["+configuration.getDir()+"] is empty.");
			// 恢复定时任务
			jobManager.resumeJob(context);
			return;
		}
		
		
		
		String backupFileName=configuration.getBackupFile().getFile(configuration);
		
		// 如果文件名为null，则自动生成文件名
		if(backupFileName==null){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssS");
			backupFileName="${targetFileName}-"+sdf.format(new Date());
		}
		
		// 如果存在压缩需求，则备份为压缩文件
		if(configuration.getCompress()){
			if(configuration.getCompressType().equalsIgnoreCase("gzip")){
				backupFileName+=".tar.gz";
			}else{
				backupFileName+="."+configuration.getCompressType().toLowerCase();
			}
		}
		
		Map<String, Object> data=new HashMap<String, Object>();
		data.put("targetFileName", targetFile.getName());
		data.put("name", configuration.getName());
		data.put("type", configuration.getType());
		data.put("value", configuration.getValue());
		data.put("backuptime", new Date());
		data.put("backupConfiguration", configuration);
		
		backupFileName=FreemarkerUtil.formatTemplate(backupFileName, data);
		
		data.put("backupFileName", backupFileName);
		
		// Backup
		// 记录最后备份时间
		configuration.setLastBackupTime(new Date());
		// 记录最后备份文件名
		configuration.setLastBackupFileName(backupFileName);
		
		// start backup
		
		boolean backupRes=true;
		
		List<File> backupFiles=new ArrayList<File>();
		
		for (File backupDir : backupDirs) {
			File backupFile=new File(backupDir, backupFileName);
			backupFiles.add(backupFile);
		}
		
		
		for (File backupFile : backupFiles) {
			if(targetFile.equals(backupFile)){
				logger.warn(configuration.getType().name() 
						+ 
						"-" 
						+ configuration.getName()
						+ " Your targetFile equals backupFile, "
						+ "backup success."
						);
				configuration.setLastBackupResult(true);
			}else{
				boolean res=configuration.getBackup().execute(targetFile, backupFile, configuration);
				backupRes=backupRes&&res;
			}
		}
		
		configuration.setLastBackupResult(backupRes);
		
		if(configuration.isLastBackupResult()){
			logger.info(configuration.getType().name() 
						+ 
						"-" 
						+ configuration.getName()
						+ " backup success."
						+ " last backup file name: "+configuration.getLastBackupFileName()
						+ ", last backup file time: "+configuration.getLastBackupTime()
						+ ", last backup file result: "+configuration.isLastBackupResult()
						);
			
			if(configuration.isDeleteTargetFile()){
				
				// 检测备份文件是否与原文件相同
				boolean isDelete=true;
				for (File backupFile : backupFiles) {
					// 相同，不删除
					if(targetFile.equals(backupFile)){
						isDelete=false;
						break;
					}
				}
				
				if(isDelete){
					logger.info(configuration.getType().name() 
							+ 
							"-" 
							+ configuration.getName()
							+ " delete targetFile"
							);
					
						boolean delRes=targetFile.delete();
						if(!delRes){
							logger.info(configuration.getType().name() 
									+ 
									"-" 
									+ configuration.getName()
									+ " delete targetFile failure. maybe the file is locked, please delete manual."
									);
						}
				}
					
			}
			
		}else{
			logger.warn(configuration.getType().name() 
					+ 
					"-" 
					+ configuration.getName()
					+ " backup failure."
					+ " last backup file name: "+configuration.getLastBackupFileName()
					+ ", last backup file time: "+configuration.getLastBackupTime()
					+ ", last backup file result: "+configuration.isLastBackupResult()
					);
		}
	
		
		// Command&Interceptor after
		commandExecute(configuration, configuration.getBeforeClass());
		
		if(configuration.getAfter().size()>0){
			for (BackupAfter after : configuration.getAfter()) {
				logger.debug(configuration.getType().name() 
						+ 
						"-" 
						+ configuration.getName()
						+ " execute after interceptor ["+after.getClass().getName()+"].");
				after.execute(configuration);
			}
		}
		
		// Senders
		if (configuration.getSenders().size()>0) {
			// 邮件标题
			configuration.setMailSenderTitle(FreemarkerUtil.formatTemplate(configuration.getMailSenderTitle(), data));

			// 记录最后发送时间
			configuration.setLastBackupTime(new Date());
		}
		for (Sender sender : configuration.getSenders()) {
			sender.send(targetFile, backupFiles, configuration);
		}
		
		
		long end=System.currentTimeMillis();
		// 记录备份时长
		logger.info(configuration.getType().name() 
				+ 
				"-" 
				+ configuration.getName()
				+ " backup completed, cost in "+(end-start)+" ms");
		
		// 恢复任务
		jobManager.resumeJob(context);
	}
	

	/**
	 * 执行命令
	 * @param configuration 配置对象
	 * @return 是否成功
	 */
	private boolean commandExecute(BackupConfiguration configuration, String[] cmds){
		boolean res=false;
		if(cmds==null || cmds.length==0){
			return true;
		}
		
		for (String cmd : cmds) {
			try {
				logger.debug(configuration.getType().name() 
						+ 
						"-" 
						+ configuration.getName()
						+ " execute command ["+cmds+"].");
				Process proc = Runtime.getRuntime().exec(cmd);
				proc.waitFor();
			} catch (IOException e) {
				logger.error(
						configuration.getType().name() + "-" + configuration.getName()
						+ " execute command ["+cmd+"] error.",
						e);
			} catch (InterruptedException e) {
				logger.error(
						configuration.getType().name() + "-" + configuration.getName()
						+ " execute command ["+cmd+"] error.",
						e);
			}
		}
		
		return res;
	}
	

	


}
