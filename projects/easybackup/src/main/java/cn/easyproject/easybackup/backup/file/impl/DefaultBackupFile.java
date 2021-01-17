package cn.easyproject.easybackup.backup.file.impl;


import cn.easyproject.easybackup.backup.file.BackupFile;
import cn.easyproject.easybackup.configuration.BackupConfiguration;
import cn.easyproject.easybackup.util.EasyUtil;
/**
 * Default get BackupFile name
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class DefaultBackupFile implements BackupFile{

	
	
	
	public String getFile(BackupConfiguration configuration) {
		if(EasyUtil.isNotEmpty(configuration.getFile())){
			return configuration.getFile();
		}
		return null;
	}
	
}
