package cn.easyproject.easybackup.mysql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.easyproject.easybackup.backup.file.TargetFile;
import cn.easyproject.easybackup.configuration.BackupConfiguration;

/**
 * MySQL database backup TargetFile
 * The plugin for EasyBakcup
 * 
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class MySQLTargetFile implements TargetFile {
	
	private static Logger logger = LoggerFactory.getLogger(MySQLTargetFile.class);
	
	public File getFile(BackupConfiguration configuration) {
		try {
			// 备份完成删除 SQL 源文件
			configuration.setDeleteTargetFile(true);
			// 导出文件
			String mysqldump=configuration.getValue();
			int fileStartIndex=mysqldump.indexOf(" ", mysqldump.indexOf("-p")+2);
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssS");
//			String sqlFileName=mysqldump.substring(fileStartIndex+1).trim().replace(" ", "_")+"-"+sdf.format(new Date())+".sql";
			String sqlFileName=mysqldump.substring(fileStartIndex+1).trim().replace(" ", "_")+".sql";
			
			File backDir=new File(configuration.getDir()[0]);
			
			if(!backDir.exists()){
				backDir.mkdirs();
			}
			
			File sqlTargetFile=new File(backDir,sqlFileName); //SQL文件
			
			BufferedReader br=null;
			BufferedWriter bw=null;
			try{
				
				/* * 直接备份，执行过程为异步，java代码继续执行
				 * Runtime.getRuntime().exec("cmd /c mysqldump -uroot -proot dbname>e:/bak/db_bak.sql");
				 * 
				
				 *同步备份代码 */
				 
				Process process=Runtime.getRuntime().exec(configuration.getValue()); //执行备份脚本
				br=new BufferedReader(new InputStreamReader(process.getInputStream()));
				bw=new BufferedWriter(new FileWriter(sqlTargetFile));
				String s=null;
				
				while((s=br.readLine())!=null){
					bw.write(s);
					bw.newLine();
				}
			
			}catch(Exception e){
				logger.error("Backup MySQL database error,"
						+ "please check your backup command: ["+configuration.getValue()+"]",e);
			}finally{
				if(br!=null){
					try {
						br.close();
					} catch (Exception e2) {
						logger.error("BufferedReader close exception. ["+configuration.getValue()+"]",e2);
					}
				}
				if(bw!=null){
					try {
						bw.flush();
					} catch (Exception e2) {
						logger.error("BufferedWriter flush exception. ["+configuration.getValue()+"]",e2);
					}
				}
				if(bw!=null){
					try {
						bw.close();
					} catch (Exception e2) {
						logger.error("BufferedWriter close exception. ["+configuration.getValue()+"]",e2);
					}
				}
			}
			
			return sqlTargetFile;
		} catch (Exception e) {
			logger.error("Backup MySQL database error, "
					+ "["+configuration.getValue()+"]",e);

		}
		return null;
	}

}
