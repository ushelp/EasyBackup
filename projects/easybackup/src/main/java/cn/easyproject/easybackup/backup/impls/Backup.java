package cn.easyproject.easybackup.backup.impls;

import java.io.File;

import cn.easyproject.easybackup.configuration.BackupConfiguration;

/**
 * Backup implements
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public interface Backup {
	public boolean execute(File targetFile,File backupFile, BackupConfiguration configuration);
}
