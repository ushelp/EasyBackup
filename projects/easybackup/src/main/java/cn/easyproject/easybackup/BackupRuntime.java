package cn.easyproject.easybackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import cn.easyproject.easybackup.backup.StdBackupEngine;
import cn.easyproject.easybackup.backup.file.BackupDir;
import cn.easyproject.easybackup.backup.file.BackupFile;
import cn.easyproject.easybackup.backup.file.TargetFile;
import cn.easyproject.easybackup.backup.file.impl.DefaultBackupDir;
import cn.easyproject.easybackup.backup.file.impl.DefaultBackupFile;
import cn.easyproject.easybackup.backup.file.impl.DefaultTargtFile;
import cn.easyproject.easybackup.backup.impls.Backup;
import cn.easyproject.easybackup.backup.impls.FileBackup;
import cn.easyproject.easybackup.backup.interceptor.BackupAfter;
import cn.easyproject.easybackup.backup.interceptor.BackupBefore;
import cn.easyproject.easybackup.configuration.BackupConfiguration;
import cn.easyproject.easybackup.configuration.GlobalConfiguration;
import cn.easyproject.easybackup.job.JobManager;
import cn.easyproject.easybackup.sender.Sender;
import cn.easyproject.easybackup.sender.mail.MailSender;
import cn.easyproject.easybackup.util.EasyUtil;
import cn.easyproject.easybackup.util.SpringUtil;

/**
 * 备份运行时信息
 * @author easyproject.cn
 *
 * @since 2.3.0
 */
public class BackupRuntime {
	
	static Logger logger = LoggerFactory.getLogger(BackupRuntime.class);
	
	public static boolean started = false;
	
	/**
	 * 备份启动后，所有监控 Monitor 配置
	 */
	public static List<BackupConfiguration> allBackupsOnStartup=new ArrayList<BackupConfiguration>();
	
	
	/**
	 * 备份启动后，运行中的监控 Monitor 配置
	 */
	public static List<BackupConfiguration> runningBackupsOnStartup=new ArrayList<BackupConfiguration>();
	
	
	/**
	 * 备份启动后，任务创建错误的监控 Monitor 配置
	 */
	public static List<BackupConfiguration> errorJobBackupsOnStartup=new ArrayList<BackupConfiguration>();
	
	/**
	 * 获得所有备份服务名
	 * @return 所有备份服务名
	 */
	@SuppressWarnings("rawtypes")
	public static Set<String> getBackupNames(){
		Set<String> list=null;
		Properties p=getProperties();
		if(p!=null){
			/*
			 * Get Config Names
			 */
			Set<String> configNames=new HashSet<String>();
			Enumeration names=p.propertyNames();
			while(names.hasMoreElements()){
			    String key = (String) names.nextElement();
			    /*
				 * Judgment Config TYPE
				 */
			    if(key.startsWith("easybackup.file.")){
			    	key=key.substring("easybackup.file.".length());
			    	if(key.indexOf(".")==-1){
			    		configNames.add("FILE:"+key);
			    	}
			    }else if(key.startsWith("easybackup.user.")){
			    	key=key.substring("easybackup.user.".length());
			    	if(key.indexOf(".")==-1){
			    		configNames.add("USER:"+key);
			    	}
			    }
			} 
			list=configNames;
		}
		return list;
	}
	

	/**
	 * 停止备份服务
	 */
	public static void stop(){
		JobManager jobManager=SpringUtil.get("jobManager");
		jobManager.clear();
		
		
		BackupRuntime.started=false;
		
		BackupRuntime.allBackupsOnStartup.clear();
		BackupRuntime.errorJobBackupsOnStartup.clear();
		BackupRuntime.runningBackupsOnStartup.clear();
		
	}
	
	/**
	 * 启动服务
	 */
	public static void start(){
		new EasyBackup().start();
	}
	
	/**
	 * 获取 easybackup.properties
	 * @return backup properties object
	 */
	public static Properties getProperties(){
		Properties properties=null;
		// 使用用户自定义 properties  配置文件
				if(EasyBackup.getPropertiesFile()!=null){
					// 使用用户自定义 properties  配置文件
					try {
						properties = new Properties();
						properties.load(new FileInputStream(EasyBackup.getPropertiesFile()));
					} catch (IOException e) {
						logger.error("Read file:easybackup.properties error.", e);
					}
			
				}else{
					// 搜索默认配置文件(运行相对目录；classpath)
					ResourceLoader rl = new PathMatchingResourcePatternResolver();
					Resource res = rl.getResource("file:easybackup.properties");
					Resource res2 = rl.getResource("classpath:easybackup.properties");

					if((!res.exists()) && (!res2.exists())){
						logger.error("easybackup.properties not found.");
					}else{
						boolean loadProperties=false;
						if (res.exists()) {
							try {
								properties = new Properties();
								properties.load(res.getInputStream());
								loadProperties=true;
							} catch (IOException e) {
								logger.error("Read file:easybackup.properties error.", e);
							}
						}
						
						if(!loadProperties){
							try {
								properties = new Properties();
								properties.load(res2.getInputStream());
							} catch (IOException e) {
								logger.error("Read classpath:easybackup.properties error.", e);
							}
						}
						
					}
				}
				
				
		
		return properties;
	}
	
	
	
	/**
	 * 获得配置文件对象
	 * @return Properties File
	 */
	public static File getPropertiesFile(){
		
		File propertiesFile=null;
		
		
		
		// 使用用户自定义 properties  配置文件
				if(EasyBackup.getPropertiesFile()!=null){
					propertiesFile=EasyBackup.getPropertiesFile();
				}else{
					
					// 搜索默认配置文件(运行相对目录；classpath)
					ResourceLoader rl = new PathMatchingResourcePatternResolver();
					Resource res = rl.getResource("file:easybackup.properties");
					Resource res2 = rl.getResource("classpath:easybackup.properties");

					if((!res.exists()) && (!res2.exists())){
						logger.error("easybackup.properties not found.");
					}else{
						boolean loadProperties=false;
						if (res.exists()) {
							try {
								propertiesFile=res.getFile();
								loadProperties=true;
							} catch (IOException e) {
								logger.error("Read file:easybackup.properties error.", e);
							}
						}
						
						if(!loadProperties){
							try {
								propertiesFile=res2.getFile();
							} catch (IOException e) {
								logger.error("Read classpath:easybackup.properties error.", e);
							}
						}
						
					}
			
					
				}
				
		
		return propertiesFile;
	}
	
	
	/**
	 * 获取全局配置示例
	 * @return GlobalConfiguration
	 */
	public static GlobalConfiguration getGlobalBackupsConfiguration(){
		
		GlobalConfiguration globalConfig=new GlobalConfiguration();
		
		Properties p=getProperties();
		if (p!=null) {
				/*
				 * GlobalConfig init
				 */
				String globalEnable=p.getProperty("easybackup.enable", "ON");
				String globalDeleteTargetFile=p.getProperty("easybackup.deleteTargetFile", "OFF");
				String globalBackupDir=p.getProperty("easybackup.dir", null);
				String globalBackupFile=p.getProperty("easybackup.file", null);
				String globalCompress=p.getProperty("easybackup.compress", "OFF");
				String globalCompressType=p.getProperty("easybackup.compressType", "ZIP");
				String globalCompressEncoding=p.getProperty("easybackup.compressEncoding", null);
				String globalCronExpression=p.getProperty("easybackup.cronexpression", "0 30 * * * ?");
				
				String globalCmdBefore=p.getProperty("easymonitor.cmd.before",null);
				String globalCmdAfter=p.getProperty("easymonitor.cmd.after",null);
				
				String globalBeforeClass=p.getProperty("easymonitor.beforeClass",null);
				String globalAfterClass=p.getProperty("easymonitor.afterClass",null);
				
				String globalMailReceiver=p.getProperty("easybackup.mail.receiver",null);
				String globalMailSender=p.getProperty("easybackup.mail.sender",null);
				String globalMailSenderPassword=p.getProperty("easybackup.mail.sender.passowrd",null);
				String globalMailSenderHost=p.getProperty("easybackup.mail.sender.host",null);
				String globalMailSenderSsl=p.getProperty("easybackup.mail.sender.ssl",null);
				String globalMailSenderTitle=p.getProperty("easybackup.mail.sender.title",null);
				String globalMailSenderTemplate=p.getProperty("easybackup.mail.sender.template",null);
				String globalDeleteBackup=p.getProperty("easybackup.mail.deleteBackup", "OFF");

				Integer globalMailPort=25;
				try {
					globalMailPort = Integer.valueOf(p.getProperty("easybackup.mail.sender.port", "25"));
				} catch (NumberFormatException e) {
					logger.warn("'easybackup.mail.sender.port' is not a integer, use default 25.");
				}
				

				globalConfig.setEnable(EasyUtil.isTrue(globalEnable));
				globalConfig.setCronExpression(globalCronExpression);
				globalConfig.setDir(EasyUtil.isNotEmpty(globalBackupDir)?globalBackupDir.split(";"):null);
				globalConfig.setFile(globalBackupFile);
				globalConfig.setCompress(EasyUtil.isTrue(globalCompress));
				globalConfig.setCompressType(StdBackupEngine.getCompressType(globalCompressType));
				globalConfig.setCompressEncoding(globalCompressEncoding);
				globalConfig.setDeleteTargetFile(EasyUtil.isTrue(globalDeleteTargetFile));
				
				globalConfig.setCmdBefore(EasyUtil.isNotEmpty(globalCmdBefore)?globalCmdBefore.split(";"):null);
				globalConfig.setCmdAfter(EasyUtil.isNotEmpty(globalCmdAfter)?globalCmdAfter.split(";"):null);
				globalConfig.setBeforeClass(EasyUtil.isNotEmpty(globalBeforeClass)?globalBeforeClass.split(";"):null);
				globalConfig.setAfterClass(EasyUtil.isNotEmpty(globalAfterClass)?globalAfterClass.split(";"):null);
				
				globalConfig.setMailReceiver(EasyUtil.isNotEmpty(globalMailReceiver)?globalMailReceiver.split(";"):null);
				globalConfig.setMailSender(globalMailSender);
				globalConfig.setMailSenderPassword(globalMailSenderPassword);
				globalConfig.setMailSenderHost(globalMailSenderHost);
				globalConfig.setMailSenderPort(globalMailPort);
				globalConfig.setMailSenderSsl(EasyUtil.isTrue(globalMailSenderSsl));
				globalConfig.setMailSenderTitle(globalMailSenderTitle);
				globalConfig.setMailSenderTemplate(globalMailSenderTemplate);
				
				globalConfig.setMailDeleteBackup(EasyUtil.isTrue(globalDeleteBackup));
			
		} else {
			logger.error("easybackup.properties not found.");
		}
		return globalConfig;
		
	}
	
	/**
	 * 从 easybackup.properties 获取所有 设置为Enable 备份配置
	 * @return 备份配置
	 */
	public static List<BackupConfiguration> getEnableBackupsConfigurations(){
		List<BackupConfiguration> all=getBackupsConfigurations();
		for (int i = all.size()-1; i >=0; i--) {
			if(!all.get(i).getEnable()){
				all.remove(i);
			}
		}
		return all;
	}
	
	
	/**
	 * 从 easybackup.properties 获取所有备份配置
	 * @return 备份配置
	 */
	@SuppressWarnings("rawtypes")
	public static List<BackupConfiguration> getBackupsConfigurations(){
		List<BackupConfiguration> list=new ArrayList<BackupConfiguration>();
		
		Properties p=getProperties();

		if (p!=null) {
				/*
				 * GlobalConfig init
				 */
				GlobalConfiguration globalConfig=new GlobalConfiguration();
				
				String globalEnable=p.getProperty("easybackup.enable", "ON");
				String globalDeleteTargetFile=p.getProperty("easybackup.deleteTargetFile", "OFF");
				String globalBackupDir=p.getProperty("easybackup.dir", null);
				String globalBackupFile=p.getProperty("easybackup.file", null);
				String globalCompress=p.getProperty("easybackup.compress", "OFF");
				String globalCompressType=p.getProperty("easybackup.compressType", "ZIP");
				String globalCompressEncoding=p.getProperty("easybackup.compressEncoding", null);
				String globalCronExpression=p.getProperty("easybackup.cronexpression", "0 30 * * * ?");
				
				String globalCmdBefore=p.getProperty("easymonitor.cmd.before",null);
				String globalCmdAfter=p.getProperty("easymonitor.cmd.after",null);
				
				String globalBeforeClass=p.getProperty("easymonitor.cmd.before",null);
				String globalAfterClass=p.getProperty("easymonitor.cmd.after",null);
				
				String globalMailReceiver=p.getProperty("easybackup.mail.receiver",null);
				String globalMailSender=p.getProperty("easybackup.mail.sender",null);
				String globalMailSenderPassword=p.getProperty("easybackup.mail.sender.passowrd",null);
				String globalMailSenderHost=p.getProperty("easybackup.mail.sender.host",null);
				String globalMailSenderSsl=p.getProperty("easybackup.mail.sender.ssl",null);
				String globalMailSenderTitle=p.getProperty("easybackup.mail.sender.title",null);
				String globalMailSenderTemplate=p.getProperty("easybackup.mail.sender.template",null);
				String globalDeleteBackup=p.getProperty("easybackup.mail.deleteBackup", "OFF");

				Integer globalMailPort=25;
				try {
					globalMailPort = Integer.valueOf(p.getProperty("easybackup.mail.sender.port", "25"));
				} catch (NumberFormatException e) {
					logger.warn("'easybackup.mail.sender.port' is not a integer, use default 25.");
				}
				

				globalConfig.setEnable(EasyUtil.isTrue(globalEnable));
				globalConfig.setCronExpression(globalCronExpression);
				globalConfig.setDir(EasyUtil.isNotEmpty(globalBackupDir)?globalBackupDir.split(";"):null);
				globalConfig.setFile(globalBackupFile);
				globalConfig.setCompress(EasyUtil.isTrue(globalCompress));
				globalConfig.setCompressType(StdBackupEngine.getCompressType(globalCompressType));
				globalConfig.setCompressEncoding(globalCompressEncoding);
				globalConfig.setDeleteTargetFile(EasyUtil.isTrue(globalDeleteTargetFile));
				
				globalConfig.setCmdBefore(EasyUtil.isNotEmpty(globalCmdBefore)?globalCmdBefore.split(";"):null);
				globalConfig.setCmdAfter(EasyUtil.isNotEmpty(globalCmdAfter)?globalCmdAfter.split(";"):null);
				globalConfig.setBeforeClass(EasyUtil.isNotEmpty(globalBeforeClass)?globalBeforeClass.split(";"):null);
				globalConfig.setAfterClass(EasyUtil.isNotEmpty(globalAfterClass)?globalAfterClass.split(";"):null);
				
				globalConfig.setMailReceiver(EasyUtil.isNotEmpty(globalMailReceiver)?globalMailReceiver.split(";"):null);
				globalConfig.setMailSender(globalMailSender);
				globalConfig.setMailSenderPassword(globalMailSenderPassword);
				globalConfig.setMailSenderHost(globalMailSenderHost);
				globalConfig.setMailSenderPort(globalMailPort);
				globalConfig.setMailSenderSsl(EasyUtil.isTrue(globalMailSenderSsl));
				globalConfig.setMailSenderTitle(globalMailSenderTitle);
				globalConfig.setMailSenderTemplate(globalMailSenderTemplate);
				
				globalConfig.setMailDeleteBackup(EasyUtil.isTrue(globalDeleteBackup));
				
				/*
				 * Get Config Names
				 */
				Set<String> configNames=new HashSet<String>();
				Enumeration names=p.propertyNames();
				while(names.hasMoreElements()){
				    String key = (String) names.nextElement();
				    /*
					 * Judgment Config TYPE
					 */
				    if(key.startsWith("easybackup.file.")){
				    	key=key.substring("easybackup.file.".length());
				    	if(key.indexOf(".")==-1){
				    		configNames.add("FILE:"+key);
				    	}
				    }else if(key.startsWith("easybackup.user.")){
				    	key=key.substring("easybackup.user.".length());
				    	if(key.indexOf(".")==-1){
				    		configNames.add("USER:"+key);
				    	}
				    }
				} 
				logger.debug("configNames: "+ configNames);
				
				
				/*
				 * Init BackupConfig
				 */
				for (String name : configNames) {
					BackupConfiguration configuration=new BackupConfiguration();
					/*
					 * Judgment Config TYPE
					 */
					String startWith="easybackup.url.";
					if(name.startsWith("FILE:")){
						startWith="easybackup.file.";
						name=name.substring("FILE:".length());
						configuration.setType(BackupType.FILE);
						configuration.setBackup(new FileBackup());
						
						
					}else if(name.startsWith("USER:")){
						startWith="easybackup.user.";
						name=name.substring("USER:".length());
						configuration.setType(BackupType.USER);
//						configuration.setBackup(new UserBackup());
					}
					
					
					
					String value=startWith+name;
					String backupClass=startWith+name+".backupClass";
					String enable=startWith+name+".enable";
					String dir=startWith+name+".dir";
					String file=startWith+name+".file";
					String targetFileClass=startWith+name+".targetFileClass";
					String dirClass=startWith+name+".dirClass";
					String fileClass=startWith+name+".fileClass";
					String compress=startWith+name+".compress";
					String compressType=startWith+name+".compressType";
					String compressEncoding=startWith+name+".compressEncoding";
					String cronexpression=startWith+name+".cronexpression";
					String deleteTargetFile=startWith+name+".deleteTargetFile";
					
					String cmdBefore=startWith+name+".cmd.before";
					String cmdAfter=startWith+name+".cmd.after";
					String beforeClass=startWith+name+".beforeClass";
					String afterClass=startWith+name+".afterClass";
					
					String receiver=startWith+name+".mail.receiver";
					String sender=startWith+name+".mail.sender";
					String senderPassowrd=startWith+name+".mail.sender.passowrd";
					String senderHost=startWith+name+".mail.sender.host";
					String senderPort=startWith+name+".mail.sender.port";
					String senderSsl=startWith+name+".mail.sender.ssl";
					String senderTitle=startWith+name+".mail.sender.title";
					String senderTemplate=startWith+name+".mail.sender.template";
					String deleteBackup=startWith+name+".mail.deleteBackup";
					String senders=startWith+name+".senders";
			
					value=p.getProperty(value,null);
					backupClass=p.getProperty(backupClass,null);
					enable=p.getProperty(enable,null);
					dir=p.getProperty(dir,null);
					file=p.getProperty(file,null);
					targetFileClass=p.getProperty(targetFileClass,null);
					dirClass=p.getProperty(dirClass,null);
					fileClass=p.getProperty(fileClass,null);
					deleteTargetFile=p.getProperty(deleteTargetFile,null);
					
					compress=p.getProperty(compress,null);
					compressType=p.getProperty(compressType,null);
					compressEncoding=p.getProperty(compressEncoding,null);
					cronexpression=p.getProperty(cronexpression,null);
					
					cmdBefore=p.getProperty(cmdBefore,null);
					cmdAfter=p.getProperty(cmdAfter,null);
					beforeClass=p.getProperty(beforeClass,null);
					afterClass=p.getProperty(afterClass,null);
					
					receiver=p.getProperty(receiver,null);
					sender=p.getProperty(sender,null);
					senderPassowrd=p.getProperty(senderPassowrd,null);
					senderHost=p.getProperty(senderHost,null);
					senderPort=p.getProperty(senderPort,null);
					senderSsl=p.getProperty(senderSsl,null);
					senderTitle=p.getProperty(senderTitle,null);
					senderTemplate=p.getProperty(senderTemplate,null);
					deleteBackup=p.getProperty(deleteBackup,null);
					senders=p.getProperty(senders,null);
					
					// init
					
					Integer backupMailPort=globalConfig.getMailSenderPort();
					if(senderPort!=null){
						try {
							backupMailPort = Integer.valueOf(senderPort);
						} catch (NumberFormatException e) {
							logger.warn("'"+startWith+name+".mail.sender.interval' is not a integer, use default "+backupMailPort+".",e);
						}
					}
					if(backupMailPort==null){
						backupMailPort=25; // default is 25
					}
	
					
					
					configuration.setName(name);
					configuration.setValue(value);
					configuration.setEnable(enable!=null?EasyUtil.isTrue(enable):globalConfig.getEnable());
					configuration.setDeleteTargetFile(deleteTargetFile!=null?EasyUtil.isTrue(deleteTargetFile):globalConfig.isDeleteTargetFile());
					configuration.setDir(dir!=null?dir.split(";"):globalConfig.getDir());
					configuration.setFile(file!=null?file:globalConfig.getFile());
					configuration.setCompress(compress!=null?EasyUtil.isTrue(compress):globalConfig.getCompress());
					configuration.setCompressType(compressType!=null?StdBackupEngine.getCompressType(compressType):globalConfig.getCompressType());
					configuration.setCompressEncoding(compressEncoding!=null?compressEncoding:globalCompressEncoding);
					configuration.setCronExpression(cronexpression!=null?cronexpression:globalConfig.getCronExpression());; //cronexpression
					
					
					configuration.setMailReceiver(receiver!=null?receiver.split(";"):globalConfig.getMailReceiver()); //mailReceiver
					configuration.setMailSender(sender!=null?sender:globalConfig.getMailSender()); //mailSender
					configuration.setMailSenderPassword(senderPassowrd!=null?senderPassowrd:globalConfig.getMailSenderPassword()); //password
					configuration.setMailSenderHost(senderHost!=null?senderHost:globalConfig.getMailSenderHost()); //host
					configuration.setMailSenderPort(backupMailPort); //mailSender port
					configuration.setMailSenderSsl(senderSsl!=null?EasyUtil.isTrue(senderSsl):globalConfig.getMailSenderSsl()); //ssl
					configuration.setMailSenderTitle(senderTitle!=null?senderTitle:globalConfig.getMailSenderTitle()); //title
					configuration.setMailSenderTemplate(senderTemplate!=null?senderTemplate:globalConfig.getMailSenderTemplate()); //template
					configuration.setMailDeleteBackup(deleteBackup!=null?EasyUtil.isTrue(deleteBackup):globalConfig.getMailDeleteBackup()); //ssl
					
					configuration.setCmdBefore(cmdBefore!=null?cmdBefore.split(";"):globalConfig.getCmdBefore());
					configuration.setCmdAfter(cmdAfter!=null?cmdAfter.split(";"):globalConfig.getCmdAfter());
					configuration.setBeforeClass(beforeClass!=null?beforeClass.split(";"):globalConfig.getBeforeClass());
					configuration.setAfterClass(afterClass!=null?afterClass.split(";"):globalConfig.getAfterClass());
					
			
					
					// before&after interceptor
					if(configuration.getBeforeClass()!=null&& configuration.getBeforeClass().length>0){
						for (String cls : configuration.getBeforeClass()) {
							try {
								BackupBefore before=(BackupBefore) Class.forName(cls.trim()).newInstance();
								configuration.getBefore().add(before);
							} catch (InstantiationException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the beforeClass ["+cls+"] is not implements 'cn.easyproject.easybackup.backup.interceptor.BackupBefore' interface.",
										e);
							} catch (IllegalAccessException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the beforeClass ["+cls+"] is IllegalAccessException.",
										e);
							} catch (ClassNotFoundException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the beforeClass ["+cls+"] is not found.",
										e);
							}
						}
					}
					
					if(configuration.getAfterClass()!=null&& configuration.getAfterClass().length>0){
						for (String cls : configuration.getAfterClass()) {
							try {
								BackupAfter after=(BackupAfter) Class.forName(cls.trim()).newInstance();
								configuration.getAfter().add(after);
							} catch (InstantiationException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the afterClass ["+cls+"] is not implements 'cn.easyproject.easybackup.backup.interceptor.BackupAfter' interface.",
										e);
							} catch (IllegalAccessException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the afterClass ["+cls+"] is IllegalAccessException.",
										e);
							} catch (ClassNotFoundException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the afterClass ["+cls+"] is not found.",
										e);
							}
						}
					}
					
					
					if(configuration.getType()==BackupType.FILE){
						configuration.setTargetFile(new DefaultTargtFile());
						configuration.setBackupDir(new DefaultBackupDir());
						configuration.setBackupFile(new DefaultBackupFile());
						configuration.setBackup(new FileBackup());
						
						// 备份目录不存在，直接删除
						if((!EasyUtil.isNotEmpty(configuration.getValue())) ){
							logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
									+configuration.getValue()
									+"Backup file value is empty, the backup is cancel.",new RuntimeException("Backup file value is empty!"));
							configuration.setTargetFile(null); // value不存在，目标备份设置为空		
						//	continue;
						}
					}else{
						
						// 备份目录不存在，并且直接删除
						if((!EasyUtil.isNotEmpty(configuration.getValue())) && (!EasyUtil.isNotEmpty(configuration.getFileClass()))  ){
							logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
									+configuration.getValue()
									+"Backup file value is empty, the backup is cancel.",new RuntimeException("Backup file value is empty!"));
//									continue;
						}
						
						
						//USER
						// targetFileClass
						if(EasyUtil.isNotEmpty(targetFileClass)){
							configuration.setTargetFileClass(targetFileClass);
							boolean flag=false;
							Exception e2=null;
							try {
								TargetFile targetFile=(TargetFile) Class.forName(targetFileClass.trim()).newInstance();
								configuration.setTargetFile(targetFile);
								flag=true;
							} catch (InstantiationException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the targeFileClass ["+targetFileClass+"] is not implements 'cn.easyproject.easybackup.backup.file.TargetFile' interface.",
										e);
								e2=e;
							} catch (IllegalAccessException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the targeFileClass ["+targetFileClass+"] is IllegalAccessException.",
										e);
								e2=e;
							} catch (ClassNotFoundException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the targeFileClass ["+targetFileClass+"] is not found.",
										e);
								e2=e;
							}
							
							if(!flag){
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"' targeFileClass ["+targetFileClass+"] error, the backup is cancel.",e2);
//										continue;
							}
						}else{
							configuration.setTargetFile(new DefaultTargtFile());
						}
						
						// backupClass
						if(EasyUtil.isNotEmpty(backupClass)){
							configuration.setBackupClass(backupClass);
							boolean flag=false;
							Exception e2=null;
							try {
								Backup backup=(Backup) Class.forName(backupClass.trim()).newInstance();
								configuration.setBackup(backup);
								flag=true;
							} catch (InstantiationException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the backupClass ["+backupClass+"] is not implements 'cn.easyproject.easybackup.backup.file.TargetFile' interface.",
										e);
								e2=e;
							} catch (IllegalAccessException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the backupClass ["+backupClass+"] is IllegalAccessException.",
										e);
								e2=e;
							} catch (ClassNotFoundException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the backupClass ["+backupClass+"] is not found.",
										e);
								e2=e;
							}
							
							if(!flag){
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"' backupClass ["+backupClass+"] error, the backup is cancel.",e2);
//										continue;
							}
						}else{
							configuration.setBackup(new FileBackup());
						}
						
						// dirClass
						if(EasyUtil.isNotEmpty(dirClass)){
							configuration.setDirClass(dirClass);
							boolean flag=false;
							Exception e2=null;
							try {
								BackupDir backupDir=(BackupDir) Class.forName(dirClass.trim()).newInstance();
								configuration.setBackupDir(backupDir);
								flag=true;
							} catch (InstantiationException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the dirClass ["+dirClass+"] is not implements 'cn.easyproject.easybackup.backup.file.TargetFile' interface.",
										e);
								e2=e;
							} catch (IllegalAccessException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the dirClass ["+dirClass+"] is IllegalAccessException.",
										e);
								e2=e;
							} catch (ClassNotFoundException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the dirClass ["+dirClass+"] is not found.",
										e);
								e2=e;
							}
							
							if(!flag){
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"' dirClass ["+dirClass+"] error, the backup is cancel.",e2);
//										continue;
							}
						}else{
							configuration.setBackupDir(new DefaultBackupDir());
						}
						
						// fileClass
						if(EasyUtil.isNotEmpty(fileClass)){
							configuration.setFileClass(fileClass);
							boolean flag=false;
							Exception e2=null;
							try {
								BackupFile backupFile=(BackupFile) Class.forName(fileClass.trim()).newInstance();
								configuration.setBackupFile(backupFile);
								flag=true;
							} catch (InstantiationException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the fileClass ["+fileClass+"] is not implements 'cn.easyproject.easybackup.backup.file.TargetFile' interface.",
										e);
								e2=e;
							} catch (IllegalAccessException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the fileClass ["+fileClass+"] is IllegalAccessException.",
										e);
								e2=e;
							} catch (ClassNotFoundException e) {
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"', the fileClass ["+fileClass+"] is not found.",
										e);
								e2=e;
							}
							
							if(!flag){
								logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
										+configuration.getValue()
										+"' fileClass ["+fileClass+"] error, the backup is cancel.",e2);
//										continue;
							}
						}else{
							configuration.setBackupFile(new DefaultBackupFile());
						}
						
						
					}
			
					
					// 邮件模板检测，如果不存在默认加载mail.tpl
					if(!EasyUtil.isNotEmpty(configuration.getMailSenderTemplate())){
						logger.warn("Not configuration '"+startWith+name+".mail.sender.template'. Use default 'mail.tpl'");
						configuration.setMailSenderTemplate("mail.tpl");
					}
					
					 
					// 自定义Sender
					if(senders!=null){
						configuration.setSendersString(senders);
						 String[] senderImpls=senders.split(";");
						 if(senderImpls.length>0){
							 for (String cls : senderImpls) {
								if(!"".equals(cls)){
									// 加载自定义Sender
									try {
										Sender userSender=(Sender) Class.forName(cls).newInstance();
										configuration.getSenders().add(userSender);
									} catch (InstantiationException e) {
										logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
												+configuration.getValue()
												+"', the Sender class ["+cls+"] is not implements 'cn.easyproject.easybackup.sender.Sender' interface.",
												e);
									} catch (IllegalAccessException e) {
										logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
												+configuration.getValue()
												+"', the Sender class ["+cls+"] is IllegalAccessException.",
												e);
									} catch (ClassNotFoundException e) {
										logger.error(configuration.getType().name()+"-"+configuration.getName()+": '"
												+configuration.getValue()
												+"', the Sender class ["+cls+"] is not found.",
												e);
									}
									
								}
							}
						 }
					}
					
					// MailSender
					if(configuration.getMailSender()!=null&&configuration.getMailReceiver()!=null&&configuration.getMailReceiver().length!=0){
						configuration.getSenders().add(new MailSender());
					}
					
					list.add(configuration);
					
				
				
					Collections.sort(list, new Comparator<BackupConfiguration>() {

						public int compare(BackupConfiguration o1, BackupConfiguration o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});
				
					
				}

		} else {
			logger.error("easybackup.properties not found.");
		}
		return list;
	}
	
	
	
	
}
