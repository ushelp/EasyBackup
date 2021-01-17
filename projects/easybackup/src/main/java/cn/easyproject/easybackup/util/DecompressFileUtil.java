package cn.easyproject.easybackup.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * DecompressFile Util
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class DecompressFileUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DecompressFileUtil.class);

	/**
	 * Decompres 
	 * @param compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)
	 * @param compressFile compress file
	 * @param outDir output directory
	 * @param encoding encoding
	 * @return decompress result
	 */
	public static boolean decompress(String compressType,File compressFile,File outDir,  String encoding){
		compressType=compressType.toUpperCase();
		if(compressType.equals("ZIP")){
			if(outDir!=null){
				return decompressZip(compressFile, outDir);
			}else{
				return decompressZip(compressFile);
			}
			
		}else if(compressType.equals("TAR")){
			if(outDir!=null){
				return decompressTar(compressFile, outDir, encoding);
			}else{
				return decompressTar(compressFile, encoding);
			}
		}else if(compressType.equals("GZIP")||compressType.equals("TAR.GZ")){
			if(outDir!=null){
				return decompressGzip(compressFile, outDir, encoding);
			}else{
				return decompressGzip(compressFile, encoding);
			}
		}else{
			throw new RuntimeException("compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)");
		}
	}
	/**
	 * Decompres 
	 * @param compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)
	 * @param compressFile compress file
	 * @param outDir output directory
	 * @return decompress result
	 */
	public static boolean decompress(String compressType,File compressFile,File outDir){
		return decompress(compressType, compressFile, outDir, null);
	}
	
	/**
	 * Decompres 
	 * @param compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)
	 * @param compressFile compress file
	 * @param encoding encoding
	 * @return decompress result
	 */
	public static boolean decompress(String compressType,File compressFile, String encoding){
		return decompress(compressType, compressFile, null, encoding);
	}
	
	/**
	 * Decompres 
	 * @param compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)
	 * @param compressFile compress file
	 * @return decompress result
	 */
	public static boolean decompress(String compressType,File compressFile){
		return decompress(compressType, compressFile, null, null);
	}
	
	
	/**
	 * Decompress gzip file
	 * @param gzipFile gzip file
	 * @return whether success
	 */
	public static boolean decompressGzip(File gzipFile) {
		return decompressGzip(gzipFile, "");
	}
	/**
	 * decompress gzip file
	 * @param gzipFile gzipFile
	 * @param outDir output directory
	 * @return whether success 
	 */
	public static boolean decompressGzip(File gzipFile, File outDir) {
		return decompressGzip(gzipFile, outDir, "");
	}

	/**
	 * decompress gzip file
	 * @param gzipFile Compression File
	 * @param outDir output directory
	 * @param encoding compression encoding
	 * @return whether success
	 */
	@SuppressWarnings("unused")
	public static boolean decompressGzip(File gzipFile, File outDir, String encoding) {
		boolean res = false;
		// 获取压缩文件名称，不带后缀
		String outFileName = gzipFile.getName();
		if (gzipFile.getName().lastIndexOf(".") != -1) {
			outFileName = gzipFile.getName().substring(0, gzipFile.getName().lastIndexOf("."));
		}
		
		// 判断目录是否有文件存在，冲突
		if(outDir.isFile()){
			logger.error(outDir.getAbsolutePath()+" exists a file!");
			return false;
		}

		// 创建解压目录
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		TarArchiveInputStream in = null;
		try {
			// 1、获得解压文件输入流
			if (encoding == null || encoding.equals("")) {
				in = new TarArchiveInputStream(new BufferedInputStream(new GzipCompressorInputStream(new FileInputStream(gzipFile))));
			} else {
				in = new TarArchiveInputStream(new BufferedInputStream(new GzipCompressorInputStream(new FileInputStream(gzipFile))), encoding);
			}
			TarArchiveEntry zae = null;
			while ((zae = in.getNextTarEntry()) != null) { // 2、循环提取待解压的压缩条目
				File outFile = new File(outDir, zae.getName()); // 创建写出文件

				if (zae.isDirectory()) { // 如果是目录则无需写操作
					outFile.mkdirs();
					continue;
				}

				outFile.getParentFile().mkdirs(); // 保证父目录创建
				OutputStream out = new FileOutputStream(outFile);
				IOUtils.copy(in, new BufferedOutputStream(out)); // 3、写出文件
				out.close();
			}
			res=true;
		} catch (Exception e) {
			logger.error("decompressTar error.", e); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("decompressTar error.", e); //$NON-NLS-1$

				}
			}
		}
		return res;
	}
	/**
	 * Decompress gzip file
	 * @param gzipFile Compression File
	 * @param encoding compression encoding
	 * @return whether success
	 */
	public static boolean decompressGzip(File gzipFile, String encoding) {
		boolean res = false;

		TarArchiveInputStream in = null;
		try {
			// 1、获得解压文件输入流

			if (encoding == null || encoding.equals("")) {
				in = new TarArchiveInputStream(new BufferedInputStream(new GzipCompressorInputStream(new FileInputStream(gzipFile))));
			} else {
				in = new TarArchiveInputStream(new BufferedInputStream(new GzipCompressorInputStream(new FileInputStream(gzipFile))), encoding);
			}
			TarArchiveEntry zae = null;
			while ((zae = in.getNextTarEntry()) != null) { // 2、循环提取待解压的压缩条目

				File outFile = new File(gzipFile.getParent(), zae.getName()); // 创建写出文件

				if (zae.isDirectory()) { // 如果是目录则无需写操作
					outFile.mkdirs();
					continue;
				}

				outFile.getParentFile().mkdirs(); // 保证父目录创建
				OutputStream out = new FileOutputStream(outFile);
				IOUtils.copy(in, new BufferedOutputStream(out)); // 3、写出文件
				out.close();
			}
			res=true;
		} catch (Exception e) {
			logger.error("decompress error. " + gzipFile.getAbsolutePath(), e); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("decompress error. ", e); //$NON-NLS-1$
				}
			}
		}

		return res;
	}

	/**
	 * decompress tar file
	 * @param tarFile Compression File
	 * @return whether success
	 */
	public static boolean decompressTar(File tarFile) {
		return decompressTar(tarFile, "");
	}
	/**
	 * decompress tar file
	 * @param tarFile Compression File
	 * @param outDir output directory
	 * @return Whether success
	 */
	public static boolean decompressTar(File tarFile, File outDir) {
		return decompressTar(tarFile, outDir, "");
	}
	
	
	
	
	
	
	
	
	/**
	 * decompress tar file
	 * @param tarFile Compression File
	 * @param outDir output directory
	 * @param encoding compression encoding
	 * @return whether success
	 */
	@SuppressWarnings("unused")
	public static boolean decompressTar(File tarFile, File outDir, String encoding) {
		boolean res = false;
		// 获取压缩文件名称，不带后缀
		String outFileName = tarFile.getName();
		if (tarFile.getName().lastIndexOf(".") != -1) {
			outFileName = tarFile.getName().substring(0, tarFile.getName().lastIndexOf("."));
		}
		
		// 判断目录是否有文件存在，冲突
		if(outDir.isFile()){
			logger.error(outDir.getAbsolutePath()+" exists a file!");
			return false;
		}

		// 创建解压目录
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		TarArchiveInputStream in = null;
		try {
			// 1、获得解压文件输入流
			if (encoding == null || encoding.equals("")) {
				in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
			} else {
				in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)), encoding);
			}
			TarArchiveEntry zae = null;
			while ((zae = in.getNextTarEntry()) != null) { // 2、循环提取待解压的压缩条目
				File outFile = new File(outDir, zae.getName()); // 创建写出文件

				if (zae.isDirectory()) { // 如果是目录则无需写操作
					outFile.mkdirs();
					continue;
				}

				outFile.getParentFile().mkdirs(); // 保证父目录创建
				OutputStream out = new FileOutputStream(outFile);
				IOUtils.copy(in, out); // 3、写出文件
				out.close();
			}
			res=true;
		} catch (Exception e) {
			logger.error("decompressTar error.", e); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("decompressTar error.", e); //$NON-NLS-1$

				}
			}
		}
		return res;
	}
	/**
	 * decompress tar file
	 * @param tarFile Compression File
	 * @param encoding compression encoding
	 * @return whether success
	 */
	public static boolean decompressTar(File tarFile, String encoding) {
		boolean res = false;

		TarArchiveInputStream in = null;
		try {
			// 1、获得解压文件输入流

			if (encoding == null || encoding.equals("")) {
				in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
			} else {
				in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)), encoding);
			}
			TarArchiveEntry zae = null;
			while ((zae = in.getNextTarEntry()) != null) { // 2、循环提取待解压的压缩条目

				File outFile = new File(tarFile.getParent(), zae.getName()); // 创建写出文件

				if (zae.isDirectory()) { // 如果是目录则无需写操作
					outFile.mkdirs();
					continue;
				}

				outFile.getParentFile().mkdirs(); // 保证父目录创建
				OutputStream out = new FileOutputStream(outFile);
				IOUtils.copy(in, out); // 3、写出文件
				out.close();
			}
			res=true;
		} catch (Exception e) {
			logger.error("decompress error. " + tarFile.getAbsolutePath(), e); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("decompress error. ", e); //$NON-NLS-1$
				}
			}
		}

		return res;
	}

	/**
	 * decompress zip file
	 * @param zipFile Compression File
	 * @return whether success
	 */
	public static boolean decompressZip(File zipFile) {
		boolean res = false;

		ArchiveInputStream in = null;
		try {
			// 1、获得解压文件输入流
			in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP,
					 new BufferedInputStream(new FileInputStream(zipFile)));
			ZipArchiveEntry zae = null;
			while ((zae = (ZipArchiveEntry) in.getNextEntry()) != null) { // 2、循环提取待解压的压缩条目

				File outFile = new File(zipFile.getParent(), zae.getName()); // 创建写出文件

				if (zae.isDirectory()) { // 如果是目录则无需写操作
					outFile.mkdirs();
					continue;
				}

				outFile.getParentFile().mkdirs(); // 保证父目录创建
				OutputStream out = new FileOutputStream(outFile);
				IOUtils.copy(in, new BufferedOutputStream(out)); // 3、写出文件
				out.close();
			}
			res=true;
		} catch (Exception e) {
			logger.error("decompressZip error.", e); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("decompressZip error.", e); //$NON-NLS-1$
				}
			}
		}

		return res;
	}

	/**
	 * decompress zip file
	 * @param zipFile Compression File
	 * @param outDir output directory
	 * @return whether success
	 */
	@SuppressWarnings("unused")
	public static boolean decompressZip(File zipFile, File outDir) {
		boolean res = false;

		// 获取压缩文件名称，不带后缀
		String outFileName = zipFile.getName();
		if (zipFile.getName().lastIndexOf(".") != -1) {
			outFileName = zipFile.getName().substring(0, zipFile.getName().lastIndexOf("."));
		}

		// 判断目录是否有文件存在，冲突
		if(outDir.isFile()){
			logger.error(outDir.getAbsolutePath()+" exists a file!");
			return false;
		}
		
		// 创建解压目录
		if(!outDir.exists()){
			outDir.mkdirs();
		}

		ArchiveInputStream in = null;
		try {
			// 1、获得解压文件输入流
			in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP,
					new BufferedInputStream(new FileInputStream(zipFile)));
			ZipArchiveEntry zae = null;
			while ((zae = (ZipArchiveEntry) in.getNextEntry()) != null) { // 2、循环提取待解压的压缩条目
				File outFile = new File(outDir, zae.getName()); // 创建写出文件

				if (zae.isDirectory()) { // 如果是目录则无需写操作
					outFile.mkdirs();
					continue;
				}

				outFile.getParentFile().mkdirs(); // 保证父目录创建
				OutputStream out = new FileOutputStream(outFile);
				IOUtils.copy(in, new BufferedOutputStream(out)); // 3、写出文件
				out.close();
			}
			res=true;
		} catch (Exception e) {
			logger.error("decompressZip error.", e); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("decompressZip error.", e); //$NON-NLS-1$
				}
			}
		}
		return res;
	}

	

}
