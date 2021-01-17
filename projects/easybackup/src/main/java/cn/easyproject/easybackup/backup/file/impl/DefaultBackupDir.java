package cn.easyproject.easybackup.backup.file.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.easyproject.easybackup.backup.file.BackupDir;
import cn.easyproject.easybackup.configuration.BackupConfiguration;
/**
 * Default get BackupFile directory
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class DefaultBackupDir implements BackupDir {

	public List<File> getDir(BackupConfiguration configuration) {
		
		List<File> list=new ArrayList<File>();
		
		for (String dir : configuration.getDir()) {
			try {
				File backupDir=new File(dir);
				
				if(!backupDir.exists()){
					backupDir.mkdirs();
				}
				
				list.add(backupDir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		return list;
	}

}
