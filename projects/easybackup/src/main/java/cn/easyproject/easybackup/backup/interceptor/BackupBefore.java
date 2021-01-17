package cn.easyproject.easybackup.backup.interceptor;

import cn.easyproject.easybackup.configuration.BackupConfiguration;
/**
 * Backup before interceptor
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public interface BackupBefore {
	/**
	 * if return false, stop backup
	 * @param configuration BackupConfiguration
	 * @return whether execute backup
	 */
	public boolean execute(BackupConfiguration configuration);
}
