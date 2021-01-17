package cn.easyproject.easybackup.backup.interceptor;

import cn.easyproject.easybackup.configuration.BackupConfiguration;

/**
 * Backup after interceptor
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public interface BackupAfter {
	public void execute(BackupConfiguration configuration);
}
