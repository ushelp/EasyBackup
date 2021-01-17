package cn.easyproject.easybackup.backup.file.impl;

import java.io.File;

import cn.easyproject.easybackup.backup.file.TargetFile;
import cn.easyproject.easybackup.configuration.BackupConfiguration;
/**
 * Default get TargetFile 
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class DefaultTargtFile implements TargetFile {

	public File getFile(BackupConfiguration configuration) {
		try {
			File file=new File(configuration.getValue());
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
