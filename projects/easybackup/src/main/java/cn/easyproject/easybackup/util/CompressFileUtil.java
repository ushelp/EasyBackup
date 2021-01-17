package cn.easyproject.easybackup.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CompressFile Util
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class CompressFileUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CompressFileUtil.class);

	
	/**
	 * Compress File
	 * @param compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)
	 * @param srcFile source file
	 * @param compressFile compress file
	 * @param encoding encoding
	 * @return compress result
	 */
	public static boolean compress(String compressType,File srcFile, File compressFile, String encoding){
		compressType=compressType.toUpperCase();
		if(compressType.equals("ZIP")){
			return compressToZip(srcFile, compressFile);
		}else if(compressType.equals("TAR")){
			return compressToTar(srcFile, compressFile, encoding);
		}else if(compressType.equals("GZIP")||compressType.equals("TAR.GZ")){
			return compressToGzip(srcFile, compressFile, encoding);
		}else{
			throw new RuntimeException("compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)");
		}
	}
	/**
	 * Compress File
	 * @param compressType must in: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz)
	 * @param srcFile source file
	 * @param compressFile compress file
	 * @return compress result
	 */
	public static boolean compress(String compressType,File srcFile, File compressFile){
		return compress(compressType, srcFile, compressFile, null);
	}
	
	
	/**
	 * 将新文件添加进压缩的TAR文件
	 * 
	 * @param newFile
	 *            新文件
	 * @param tarFile
	 *            压缩TAR文件
	 * @return 是否压缩成功
	 */
	public static boolean addNewFileToGzip(File newFile, File tarFile) {
		return addNewFileToGzip(newFile, tarFile, null);
	}
		
	/**
	 * 将新文件添加进压缩的TAR文件
	 * 
	 * @param newFile
	 *            新文件
	 * @param gzipFile
	 *            压缩GZIP文件
	 * @param encoding
	 *            压缩编码
	 * @return 是否压缩成功
	 */
	public static boolean addNewFileToGzip(File newFile, File gzipFile, String encoding) {
		boolean res = false;

		// 创建压缩临时文件
		File temp_file = new File(gzipFile.getParent(), gzipFile.getName()+".temp" );

		while (temp_file.exists()) { // 存在同名文件则重新创建
			String temp_file_name = temp_file.getName()+".temp";
			temp_file = new File(gzipFile.getParent(), temp_file_name);
		}

		TarArchiveOutputStream tos = null;
		TarArchiveInputStream in = null;
		BufferedInputStream bis=null;
		boolean flag = false;
		try {

			if (encoding != null && (!encoding.equals(""))) {
				tos = new TarArchiveOutputStream(new BufferedOutputStream(new GzipCompressorOutputStream(new FileOutputStream(temp_file))), encoding);
				in = new TarArchiveInputStream(new BufferedInputStream(new GzipCompressorInputStream(new FileInputStream(gzipFile))), encoding);
			} else {
				tos = new TarArchiveOutputStream(new BufferedOutputStream(new GzipCompressorOutputStream(new FileOutputStream(temp_file))));
				in = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(gzipFile))));
			}
			tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

			TarArchiveEntry zae = null;

			while ((zae = in.getNextTarEntry()) != null) {

				tos.putArchiveEntry(zae);
				if (!zae.isDirectory()) {

					IOUtils.copy(in, tos); // 3写入
				}
				tos.closeArchiveEntry();
			}

			/*
			 * 追加压缩文件
			 */
			if (newFile.isDirectory()) { // 压缩目录
				tarDirectory(tos, newFile, newFile.getAbsolutePath());
			} else { // 压缩文件
				zae = new TarArchiveEntry(newFile.getName());// 1创建
				zae.setSize(newFile.length());
				tos.putArchiveEntry(zae); // 2放入
				bis=new BufferedInputStream(new FileInputStream(newFile));
				IOUtils.copy(bis, tos); // 3写入
				tos.closeArchiveEntry(); // 4关闭
				bis.close();
			}
			flag = true;
		} catch (Exception e) {
			logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
		} finally {
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}
			if (in != null) {
				try {
					in.close(); // 关闭流
				} catch (Exception e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}
			if (tos != null) {
				try {
					tos.close(); // 关闭流
				} catch (Exception e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}

		}

		if (flag) {
			gzipFile.delete(); // 删除源压缩文件
			temp_file.renameTo(gzipFile); // 重命名回原压缩文件名
			res = true;
		}else{
			temp_file.delete();
		}
		return res;
	}
	
	
	/**
	 * 将新文件添加进压缩的TAR文件
	 * 
	 * @param newFile
	 *            新文件
	 * @param tarFile
	 *            压缩TAR文件
	 * @return 是否压缩成功
	 */
	public static boolean addNewFileToTar(File newFile, File tarFile) {
		return addNewFileToTar(newFile, tarFile, null);
	}
	
	
	/**
	 * 将新文件添加进压缩的TAR文件
	 * 
	 * @param newFile
	 *            新文件
	 * @param tarFile
	 *            压缩TAR文件
	 * @param encoding
	 *            压缩编码
	 * @return 是否压缩成功
	 */
	public static boolean addNewFileToTar(File newFile, File tarFile, String encoding) {
		boolean res = false;

		// 创建压缩临时文件
		File temp_file = new File(tarFile.getParent(), tarFile.getName()+".temp" );

		while (temp_file.exists()) { // 存在同名文件则重新创建
			String temp_file_name = temp_file.getName()+".temp";
			temp_file = new File(tarFile.getParent(), temp_file_name);
		}

		TarArchiveOutputStream tos = null;
		TarArchiveInputStream in = null;
		BufferedInputStream bis=null;
		boolean flag = false;
		try {

			if (encoding != null && (!encoding.equals(""))) {
				tos = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(temp_file)), encoding);
				in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)), encoding);
			} else {
				tos = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(temp_file)));
				in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
			}
			tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

			TarArchiveEntry zae = null;

			while ((zae = in.getNextTarEntry()) != null) {

				tos.putArchiveEntry(zae);
				if (!zae.isDirectory()) {

					IOUtils.copy(in, tos); // 3写入
				}
				tos.closeArchiveEntry();
			}

			/*
			 * 追加压缩文件
			 */
			if (newFile.isDirectory()) { // 压缩目录
				tarDirectory(tos, newFile, newFile.getAbsolutePath());
			} else { // 压缩文件
				zae = new TarArchiveEntry(newFile.getName());// 1创建
				zae.setSize(newFile.length());
				tos.putArchiveEntry(zae); // 2放入
				bis=new BufferedInputStream(new FileInputStream(newFile));
				IOUtils.copy(bis, tos); // 3写入
				tos.closeArchiveEntry(); // 4关闭
			}

			flag = true;
		} catch (Exception e) {
			logger.error("addNewFileToTar error.", e); //$NON-NLS-1$
		} finally {
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}
			if (in != null) {
				try {
					in.close(); // 关闭流
				} catch (Exception e) {
					logger.error("addNewFileToTar error.", e); //$NON-NLS-1$
				}
			}
			if (tos != null) {
				try {
					tos.close(); // 关闭流
				} catch (Exception e) {
					logger.error("addNewFileToTar error.", e); //$NON-NLS-1$
				}
			}

		}

		if (flag) {
			tarFile.delete(); // 删除源压缩文件
			temp_file.renameTo(tarFile); // 重命名回原压缩文件名
			res = true;
		}else{
			temp_file.delete();
		}
		return res;
	}

	/**
	 * 将新文件添加进压缩的ZIP文件
	 * 
	 * @param newFile
	 *            新文件
	 * @param zipFile
	 *            压缩ZIP文件
	 * @return whether success
	 */
	public static boolean addNewFileToZip(File newFile, File zipFile) {
		boolean res=false;
		// 创建压缩临时文件
		File temp_file = new File(zipFile.getParent(),zipFile.getName()+".temp");

		while (temp_file.exists()) { // 存在同名文件则重新创建
			String temp_file_name = temp_file.getName()+".temp";
			temp_file = new File(zipFile.getParent(), temp_file_name);
		}
		ZipFile zf = null; //ZipFile对象用来提取文件

		ZipArchiveOutputStream zos = null; //压缩输出
		BufferedInputStream bis=null;
		boolean flag=false;
		try {
			zf = new ZipFile(zipFile); // 获得zip文件
			zos = new ZipArchiveOutputStream(temp_file);
			Enumeration<ZipArchiveEntry> ze = zf.getEntries(); //获得所有压缩选项

			/*
			 * 写入源压缩条目
			 */
			while (ze.hasMoreElements()) { //循环写出压缩文件
				ZipArchiveEntry zae = ze.nextElement();
				zos.putArchiveEntry(zae);
				if (!zae.isDirectory()) {
					IOUtils.copy(zf.getInputStream(zae), zos); // 3写入
				}
				zos.closeArchiveEntry(); // 4关闭
			}
			/*
			 * 追加压缩文件
			 */
			if (newFile.isDirectory()) { // 压缩目录
				zipDirectory(zos, newFile, newFile.getAbsolutePath());
			} else { // 压缩文件
				ZipArchiveEntry zae = new ZipArchiveEntry(newFile.getName());// 1创建
				zos.putArchiveEntry(zae); // 2放入
				bis=new BufferedInputStream(new FileInputStream(newFile));
				IOUtils.copy(bis, zos); // 3写入
				zos.closeArchiveEntry(); // 4关闭
			}
			
			flag=true;
		} catch (Exception e) {
			logger.error("addNewFileToZip error.", e); //$NON-NLS-1$
		} finally {
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}
			if(zf!=null){
				try{
					zf.close(); // 关闭流
				}catch (Exception e) {
					logger.error("addNewFileToZip error.", e); //$NON-NLS-1$
				}
			}
			if(zos!=null){
				try{
					zos.close(); // 关闭流
				}catch (Exception e) {
					logger.error("addNewFileToZip error.", e); //$NON-NLS-1$
				}
			}
			
		}
		
		if(flag){
			zipFile.delete(); // 删除源压缩文件
			temp_file.renameTo(zipFile); // 重命名回原压缩文件名
			res=true;
		}else{
			temp_file.delete();
		}
		return res;
	}


	
	/**
	 * 将文件或目录压缩为TAR文件，输出到tarfile指定的压缩文件
	 * 
	 * @param srcFile
	 *            源文件或目录路径
	 * @param gzipFile
	 *            压缩后的文件
	 * 
	 * @return 是否压缩成功
	 */
	public static boolean compressToGzip(File srcFile, File gzipFile) {
		return compressToGzip(srcFile, gzipFile, null);
	}
	
	
	/**
	 * 将文件或目录压缩为TAR文件，输出到tarfile指定的压缩文件
	 * 
	 * @param srcFile
	 *            源文件或目录路径
	 * @param gzipFile
	 *            压缩后的文件
	 * @param encoding
	 *            压缩编码
	 * 
	 * @return 是否压缩成功
	 */
	public static boolean compressToGzip(File srcFile, File gzipFile, String encoding) {
		boolean res = false;
		// 如果没有设置压缩文件名则使用文件或目录名作为压缩名
		TarArchiveOutputStream tos = null;
		BufferedInputStream bis=null;
		try {
			// 自动创建压缩文件目录
			if (!gzipFile.getParentFile().exists()) {
				gzipFile.getParentFile().mkdirs();
			}
			if (encoding != null && (!encoding.equals(""))) {
				tos = new TarArchiveOutputStream(new BufferedOutputStream(new GzipCompressorOutputStream(new FileOutputStream(gzipFile))), encoding);
			} else {
				tos = new TarArchiveOutputStream(new BufferedOutputStream(new GzipCompressorOutputStream(new FileOutputStream(gzipFile))));
			}

			tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			if (srcFile.isDirectory()) { // 压缩目录
				tarDirectory(tos, srcFile, srcFile.getAbsolutePath());
			} else { // 压缩文件
				TarArchiveEntry zae = new TarArchiveEntry(srcFile.getName());// 1创建
				zae.setSize(srcFile.length());
				tos.putArchiveEntry(zae); // 2放入
				bis=new BufferedInputStream(new FileInputStream(srcFile));	
				
				IOUtils.copy(bis, tos); // 3写入

				tos.closeArchiveEntry(); // 4关闭
			}
			res = true;

		} catch (Exception e) {
			logger.error("compressToGzip error.", e); //$NON-NLS-1$
		} finally {
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}
			if (tos != null) {

				try {
					tos.close();
				} catch (Exception e2) {
						logger.error("compressToGzip error."); //$NON-NLS-1$
				}
			}
		}

		return res;
		
	}

	/**
	 * 将文件或目录压缩为TAR文件
	 * 
	 * @param srcFile
	 *            源文件或目录路径
	 * @param tarFile
	 *            压缩后的文件
	 * 
	 * @return 是否压缩成功
	 */
	public static boolean compressToTar(File srcFile, File tarFile) {
		return compressToTar(srcFile, tarFile, null);
	}

	/**
	 * 将文件或目录压缩为TAR文件，输出到tarfile指定的压缩文件
	 * 
	 * @param srcFile
	 *            源文件或目录路径
	 * @param tarFile
	 *            压缩后的文件
	 * @param encoding
	 *            压缩编码
	 * 
	 * @return 是否压缩成功
	 */
	public static boolean compressToTar(File srcFile, File tarFile, String encoding) {
		boolean res = false;
		// 如果没有设置压缩文件名则使用文件或目录名作为压缩名
		TarArchiveOutputStream tos = null;
		BufferedInputStream bis=null;			
		
		try {
			// 自动创建压缩文件目录
			if (!tarFile.getParentFile().exists()) {
				tarFile.getParentFile().mkdirs();
			}
			if (encoding != null && (!encoding.equals(""))) {
				tos = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(tarFile)), encoding);
			} else {

				tos = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(tarFile)));
			}

			tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			if (srcFile.isDirectory()) { // 压缩目录
				tarDirectory(tos, srcFile, srcFile.getAbsolutePath());
			} else { // 压缩文件
				TarArchiveEntry zae = new TarArchiveEntry(srcFile.getName());// 1创建
				zae.setSize(srcFile.length());
				tos.putArchiveEntry(zae); // 2放入
				bis=new BufferedInputStream(new FileInputStream(srcFile));	
				
				IOUtils.copy(bis, tos); // 3写入

				tos.closeArchiveEntry(); // 4关闭
			}
			res = true;

		} catch (Exception e) {
			logger.error("compressToTar error.", e); //$NON-NLS-1$
		} finally {
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}
			if (tos != null) {

				try {
					tos.close();
				} catch (Exception e2) {
						logger.error("compressToTar error."); //$NON-NLS-1$
				}
			}
		}

		return res;
	}
	
	
	
	
	
	/**
	 * 将文件或目录压缩为ZIP文件
	 * @param srcFile 源文件或目录路径
	 * @param zipFile 压缩后的文件
	 * @return 是否压缩成功
	 */
	public static boolean compressToZip(File srcFile, File zipFile) {
		boolean res = false;

		// 如果没有设置压缩文件名则使用文件或目录名作为压缩名

		ZipArchiveOutputStream zos = null;
		BufferedInputStream bis=null;			
		
		try {
			
			// 自动创建压缩文件目录
			if (!zipFile.getParentFile().exists()) {
				zipFile.getParentFile().mkdirs();
			}
			
			zos = (ZipArchiveOutputStream) new ArchiveStreamFactory()
					.createArchiveOutputStream(ArchiveStreamFactory.ZIP, new BufferedOutputStream(new FileOutputStream(zipFile)));
		
			if (srcFile.isDirectory()) { // 压缩目录
				zipDirectory(zos, srcFile, srcFile.getAbsolutePath());
			} else { // 压缩文件
				ZipArchiveEntry zae = new ZipArchiveEntry(srcFile.getName());// 1创建
				zos.putArchiveEntry(zae); // 2放入
				bis=new BufferedInputStream(new FileInputStream(srcFile));	
				
				IOUtils.copy(bis, zos); // 3写入
				
				zos.closeArchiveEntry(); // 4关闭
			}

			res=true;
		} catch (Exception e) {
			logger.error("compressToZip error.", e); //$NON-NLS-1$
		} finally {
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("addNewFileToGzip error.", e); //$NON-NLS-1$
				}
			}
			if (zos != null) {

				try {
					zos.close();
				} catch (Exception e2) {
						logger.error("compressToZip error."); //$NON-NLS-1$
				}
			}
		}

		return res;
	}

	/**
	 * 内部调用——将目录压缩文件为TAR格式（递归方法）
	 * 
	 * @param zos
	 *            压缩输出流对象
	 * @param file
	 *            压缩的文件或目录
	 * @param rootpath
	 *            被压缩的根目录路径（绝对路径）
	 * @throws IOException
	 *             异常
	 */
	private static void tarDirectory(TarArchiveOutputStream zos, File file, String rootpath) throws IOException {
		if (file.isDirectory()) { // 如果是目录继续递归
			/*
			 * 如果不需要将空目录写出，则无需下面的写出代码部分（写出文件时会自动创建相应目录）
			 */
			String nowfileName = file.getAbsolutePath().replace(rootpath, "");
			if (!nowfileName.equals("")) {// 操作非根目录，根目录不需要写
				String entryName = nowfileName.substring(1) + File.separator;
				TarArchiveEntry tae = new TarArchiveEntry(file, entryName);// 1创建(必须加入文件对象，否则会当做空文件写入)
				try {

					tae.setSize(file.length());
					zos.putArchiveEntry(tae); // 2放入
					// 3创建目录无需数据写入操作
					zos.closeArchiveEntry(); // 4关闭
				} catch (Exception e) {
					logger.error("tarDirectory error.", e); //$NON-NLS-1$
				}
			}
			String[] list = file.list();
			for (String f : list) {
				tarDirectory(zos, new File(file, f), rootpath);
			}
		} else { // 如果是文件夹，压缩进压缩文件输出流
			String entryName = file.getAbsolutePath().replace(rootpath + File.separator, "");
			TarArchiveEntry tae = new TarArchiveEntry(entryName);// 1创建

			tae.setSize(file.length());
			zos.putArchiveEntry(tae); // 2放入
			BufferedInputStream bis=null;
			try {
				bis=new BufferedInputStream(new FileInputStream(file));	
				IOUtils.copy(bis, zos); // 3写入
			} catch (IOException e) {
				throw e;
			}finally {
				if(bis!=null){
					bis.close();
				}
			}
			
			zos.closeArchiveEntry(); // 4关闭
		}
	}


	/**
	 * 内部调用——将目录或文件压缩文件为zip格式（递归方法）
	 * 
	 * @param zos
	 *            压缩输出流对象
	 * @param file
	 *            压缩的文件或目录
	 * @param rootpath
	 *            被压缩的根目录路径（绝对路径）
	 */
	private static void zipDirectory(ZipArchiveOutputStream zos, File file,
			String rootpath) {
		if (file.isDirectory()) { // 如果是目录继续递归
			/*
			 * 如果不需要将空目录写出，则无需下面的写出代码部分（写出文件时会自动创建相应目录）
			 */
			String nowfileName = file.getAbsolutePath().replace(rootpath, "");
			if (!nowfileName.equals("")) {// 操作非根目录，根目录不需要写
				String entryName = nowfileName.substring(1) + File.separator;
				ZipArchiveEntry zae = new ZipArchiveEntry(file, entryName);// 1创建(必须加入文件对象，否则会当做空文件写入)
				try {
					zos.putArchiveEntry(zae); // 2放入
					// 3创建目录无需数据写入操作
					zos.closeArchiveEntry(); // 4关闭
				} catch (Exception e) {
					logger.error("tarDirectory error.", e); //$NON-NLS-1$
				}
			}
			String[] list = file.list();
			for (String f : list) {
				zipDirectory(zos, new File(file, f), rootpath);
			}
		} else { // 如果是文件夹，压缩进压缩文件输出流
			String entryName = file.getAbsolutePath().replace(
					rootpath + File.separator, "");
			ZipArchiveEntry zae = new ZipArchiveEntry(entryName);// 1创建
			try {
				zos.putArchiveEntry(zae); // 2放入
				BufferedInputStream bis=null;
				try {
					bis=new BufferedInputStream(new FileInputStream(file));	
					IOUtils.copy(bis, zos); // 3写入
				} catch (IOException e) {
					throw e;
				}finally {
					if(bis!=null){
						bis.close();
					}
				}
				
				zos.closeArchiveEntry(); // 4关闭
			} catch (Exception e) {
				logger.error("tarDirectory error.", e); //$NON-NLS-1$
			}
		}
	}
}
