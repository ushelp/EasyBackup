package cn.easyproject.easybackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import cn.easyproject.easybackup.backup.BackupEngine;
import cn.easyproject.easybackup.backup.StdBackupEngine;
import cn.easyproject.easybackup.job.ReloadConfigurationJob;

/**
 * EasyBackup Run enter
 * 
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class EasyBackup {

	static Logger logger = LoggerFactory.getLogger(EasyBackup.class);
	
	/*
	 * Properties information
	 */
	private static Properties properties=null;
	private static File propertiesFile;
	
	private static File userPropertiesFile;
	
	
	/**
	 * 启动 EasyBackup
	 */
	public void start() {
		if (!BackupRuntime.started) {
			
			loadProperties(); // 加载初始化配置
			
			if(propertiesFile!=null){
				BackupEngine stdBackup = new StdBackupEngine();
				stdBackup.start();
			}
			
		}else{
			logger.info("EasyBackup is already started!");
		}
	}

	/**
	 * 默认配置文件
	 */
	private void loadProperties() {
		// 使用用户自定义 properties  配置文件
		if(EasyBackup.userPropertiesFile!=null){
			// 使用用户自定义 properties  配置文件
			try {
				EasyBackup.properties = new Properties();
				EasyBackup.properties.load(new FileInputStream(userPropertiesFile));
				EasyBackup.propertiesFile=userPropertiesFile;
				ReloadConfigurationJob.propertiesLastModify = propertiesFile.lastModified();
			} catch (IOException e) {
				logger.error("Read file:easybackup.properties error.", e);
			}
			return;
		}
		
		// 搜索默认配置文件(运行相对目录；classpath)
		ResourceLoader rl = new PathMatchingResourcePatternResolver();
		Resource res = rl.getResource("file:easybackup.properties");
		Resource res2 = rl.getResource("classpath:easybackup.properties");

		if((!res.exists()) && (!res2.exists())){
			logger.error("easybackup.properties not found.");
			return;
		}else{
			boolean loadProperties=false;
			if (res.exists()) {
				try {
					EasyBackup.properties = new Properties();
					EasyBackup.properties.load(res.getInputStream());
					EasyBackup.propertiesFile=res.getFile();
					ReloadConfigurationJob.propertiesLastModify = propertiesFile.lastModified();
					loadProperties=true;
				} catch (IOException e) {
					logger.error("Read file:easybackup.properties error.", e);
				}
			}
			
			if(!loadProperties){
				try {
					EasyBackup.properties = new Properties();
					EasyBackup.properties.load(res2.getInputStream());
					EasyBackup.propertiesFile=res2.getFile();
					ReloadConfigurationJob.propertiesLastModify = propertiesFile.lastModified();
				} catch (IOException e) {
					logger.error("Read classpath:easybackup.properties error.", e);
				}
			}
			
		}
	}
	
	
	

	/**
	 * Set your properties file
	 * @param userPropertiesFile easybackup.properties File
	 */
	public static void setPropertiesFile(File userPropertiesFile) {
		EasyBackup.userPropertiesFile = userPropertiesFile;
	}

	public static File getPropertiesFile() {
		return userPropertiesFile!=null?userPropertiesFile:propertiesFile;
	}
	
	
	
	public static Properties getProperties() {
		return properties;
	}
	
//	public static void setProperties(Properties properties) {
//		EasyBackup.properties = properties;
//	}



	public static void main(String[] args) {
		new EasyBackup().start();
	}
}
