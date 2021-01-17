package cn.easyproject.easybackup.sender;

import java.io.File;
import java.util.List;

import cn.easyproject.easybackup.configuration.BackupConfiguration;

/**
 * EasyBackup Sender
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public interface Sender {
	public void send(File targetFile,List<File> backupFiles, BackupConfiguration configuration);
}
