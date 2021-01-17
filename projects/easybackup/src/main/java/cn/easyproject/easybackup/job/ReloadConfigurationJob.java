package cn.easyproject.easybackup.job;

import java.io.FileInputStream;
import java.io.IOException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import cn.easyproject.easybackup.EasyBackup;
import cn.easyproject.easybackup.backup.StdBackupEngine;
import cn.easyproject.easybackup.util.SpringUtil;


/**
 * Auto reload the backup job when easybackup.properties update
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class ReloadConfigurationJob extends QuartzJobBean {

	static Logger logger=LoggerFactory.getLogger(ReloadConfigurationJob.class);
	
	public static long propertiesLastModify;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// 检测配置文件是否修改
		
		if(EasyBackup.getPropertiesFile()!=null){
			try {
				long lastModify=EasyBackup.getPropertiesFile().lastModified();
				// 如果配置文件被修改，则停止所有任务，重新加载		
				if(lastModify!=ReloadConfigurationJob.propertiesLastModify){
					EasyBackup.getProperties().load(new FileInputStream(EasyBackup.getPropertiesFile()));
					
					ReloadConfigurationJob.propertiesLastModify=lastModify;
					
					JobManager jobManager = SpringUtil.get("jobManager");
					jobManager.clear();
					
					StdBackupEngine stdBackup=new StdBackupEngine();
					stdBackup.start();
					logger.info("the EasyBackup properties file ["+EasyBackup.getPropertiesFile().getName()+"] already reload." );
				}
				
			} catch (IOException e) {
				logger.error("Read EasyBackup properties file ["+EasyBackup.getPropertiesFile().getName()+"]  error.",e );
			}
		}else{
			logger.error("easybackup.properties file not found.");
		}
		
	}

}
