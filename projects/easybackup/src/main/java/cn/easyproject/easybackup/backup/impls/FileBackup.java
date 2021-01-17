package cn.easyproject.easybackup.backup.impls;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileSystemUtils;

import cn.easyproject.easybackup.configuration.BackupConfiguration;
import cn.easyproject.easybackup.util.CompressFileUtil;

/**
 * File implements
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class FileBackup implements Backup {

	public boolean execute(File targetFile,File backupFile, BackupConfiguration configuration){
		boolean res=false;
		// Need to compress
		if(configuration.getCompress()){
			res=CompressFileUtil.compress(
					configuration.getCompressType(), 
					targetFile, 
					backupFile, 
					configuration.getCompressEncoding()
					);
			
		}else{
			try {
				FileSystemUtils.copyRecursively(targetFile, backupFile);
				res=true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
}
