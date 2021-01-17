package cn.easyproject.easybackup.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.easyproject.easybackup.BackupType;
import cn.easyproject.easybackup.backup.file.BackupDir;
import cn.easyproject.easybackup.backup.file.BackupFile;
import cn.easyproject.easybackup.backup.file.TargetFile;
import cn.easyproject.easybackup.backup.impls.Backup;
import cn.easyproject.easybackup.backup.interceptor.BackupAfter;
import cn.easyproject.easybackup.backup.interceptor.BackupBefore;
import cn.easyproject.easybackup.sender.Sender;

/**
 * BackupConfiguration
 * 
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class BackupConfiguration extends GlobalConfiguration implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Backup Name
	 */
	protected String name;
	/**
	 * Backup Type: BackupType
	 */
	protected BackupType type;
	
	/**
	 * Backup value
	 */
	protected String value;

	/**
	 * User Sender implements
	 */
	protected List<Sender> senders=new  ArrayList<Sender>();
	

	
	/**
	 * Backup implements 
	 */
	protected Backup backup;
	
	/**
	 * BackupDir object
	 */
	protected BackupDir backupDir;
	
	/**
	 * BackupFile object
	 */
	protected BackupFile backupFile;
	
	/**
	 * TargetFile object
	 */
	protected TargetFile targetFile;
	
	/**
	 * The targeFileClass
	 */
	protected String targetFileClass;
	
	/**
	 * The backupClass
	 */
	protected String backupClass;

	/**
	 * The dirClass
	 */
	protected String dirClass;
	
	/**
	 * The fileClass
	 */
	protected String fileClass;
	
	/**
	 * Backup before interceptors
	 */
	protected List<BackupBefore> before=new ArrayList<BackupBefore>();
	
	/**
	 * Backup after interceptors
	 */
	protected List<BackupAfter> after=new ArrayList<BackupAfter>();
	
	protected String sendersString;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BackupType getType() {
		return type;
	}

	public void setType(BackupType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Sender> getSenders() {
		return senders;
	}

	public void setSenders(List<Sender> senders) {
		this.senders = senders;
	}

	public Backup getBackup() {
		return backup;
	}

	public void setBackup(Backup backup) {
		this.backup = backup;
	}

	public BackupDir getBackupDir() {
		return backupDir;
	}

	public void setBackupDir(BackupDir backupDir) {
		this.backupDir = backupDir;
	}

	public BackupFile getBackupFile() {
		return backupFile;
	}

	public void setBackupFile(BackupFile backupFile) {
		this.backupFile = backupFile;
	}

	public TargetFile getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(TargetFile targetFile) {
		this.targetFile = targetFile;
	}

	public String getTargetFileClass() {
		return targetFileClass;
	}

	public void setTargetFileClass(String targetFileClass) {
		this.targetFileClass = targetFileClass;
	}

	public String getBackupClass() {
		return backupClass;
	}

	public void setBackupClass(String backupClass) {
		this.backupClass = backupClass;
	}

	public String getDirClass() {
		return dirClass;
	}

	public void setDirClass(String dirClass) {
		this.dirClass = dirClass;
	}

	public String getFileClass() {
		return fileClass;
	}

	public void setFileClass(String fileClass) {
		this.fileClass = fileClass;
	}

	

	public List<BackupBefore> getBefore() {
		return before;
	}

	public void setBefore(List<BackupBefore> before) {
		this.before = before;
	}

	public List<BackupAfter> getAfter() {
		return after;
	}

	public void setAfter(List<BackupAfter> after) {
		this.after = after;
	}

	public String getSendersString() {
		return sendersString;
	}

	public void setSendersString(String sendersString) {
		this.sendersString = sendersString;
	}

	@Override
	public String toString() {
		return "BackupConfiguration [name=" + name + ", type=" + type + ", value=" + value + ", senders=" + senders
				+ ", backup=" + backup + ", backupDir=" + backupDir + ", backupFile=" + backupFile + ", targetFile="
				+ targetFile + ", targetFileClass=" + targetFileClass + ", backupClass=" + backupClass + ", dirClass="
				+ dirClass + ", fileClass=" + fileClass + ", before=" + before + ", after=" + after + ", sendersString="
				+ sendersString + ", enable=" + enable + ", cronExpression=" + cronExpression + ", dir="
				+ Arrays.toString(dir) + ", file=" + file + ", compress=" + compress + ", compressType=" + compressType
				+ ", compressEncoding=" + compressEncoding + ", mailReceiver=" + Arrays.toString(mailReceiver)
				+ ", mailSender=" + mailSender + ", mailSenderHost=" + mailSenderHost + ", mailSenderPort="
				+ mailSenderPort + ", mailSenderSsl=" + mailSenderSsl + ", mailSenderPassword=" + mailSenderPassword
				+ ", mailSenderTitle=" + mailSenderTitle + ", mailSenderTemplate=" + mailSenderTemplate
				+ ", mailDeleteBackup=" + mailDeleteBackup + ", cmdBefore=" + Arrays.toString(cmdBefore) + ", cmdAfter="
				+ Arrays.toString(cmdAfter) + ", beforeClass=" + Arrays.toString(beforeClass) + ", afterClass="
				+ Arrays.toString(afterClass) + ", deleteTargetFile=" + deleteTargetFile + ", lastBackupTime="
				+ lastBackupTime + ", lastSenderTime=" + lastSenderTime + ", lastBackupFileName=" + lastBackupFileName
				+ ", lastBackupResult=" + lastBackupResult + "]";
	}

	
	
	

	



	

	
	

	
	
	
	
}
