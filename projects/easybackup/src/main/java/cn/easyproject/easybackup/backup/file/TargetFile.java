package cn.easyproject.easybackup.backup.file;

import java.io.File;

import cn.easyproject.easybackup.configuration.BackupConfiguration;

/**
 * Get TargetFile 
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public interface TargetFile {
	public File getFile(BackupConfiguration configuration);
}
