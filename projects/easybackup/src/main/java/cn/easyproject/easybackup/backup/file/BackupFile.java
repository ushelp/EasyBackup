package cn.easyproject.easybackup.backup.file;


import cn.easyproject.easybackup.configuration.BackupConfiguration;
/**
 * Get BackupFile name
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public interface BackupFile {
	public String getFile(BackupConfiguration configuration);
}
