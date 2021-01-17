package cn.easyproject.easybackup.backup.file;

import java.io.File;
import java.util.List;

import cn.easyproject.easybackup.configuration.BackupConfiguration;
/**
 * Get BackupFile directory
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public interface BackupDir {
	public List<File> getDir(BackupConfiguration configuration);
}
