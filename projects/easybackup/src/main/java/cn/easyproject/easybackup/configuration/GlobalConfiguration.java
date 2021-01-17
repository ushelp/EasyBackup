package cn.easyproject.easybackup.configuration;

import java.util.Date;

/**
 * Global BackupConfiguration
 * 
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class GlobalConfiguration {
	
	


	
	
	/**
	 * Turn EasyBackup ON or OFF
	 */
	protected Boolean enable;
	/**
	 * Backup trigger Cron-Expressions; default is '0/10 * * * * ?'
	 */
	protected String cronExpression;
	
	/**
	 * Directory to store the backup file
	 */
	protected String[] dir;
	
	/**
	 * backup file name under 'easybackup.backup.dir' directory
	 * If not specified, the automatically generated file name is 'filename-yyyyMMddHHmmss.zip|tar|tar.gz'
	 */
	protected String file;
	
	/**
	 * Whether backup compression, default is ON
	 */
	protected Boolean compress;
	
	/**
	 * Compression format: TAR(*.tar), ZIP(*.zip), GZIP(*.tar.gz), default is ZIP
	 */
	protected String compressType;
	
	/**
	 * Compression encoding
	 */
	protected String compressEncoding;
	
	
	/**
	 * Backup Mail receiver
	 */
	protected String[] mailReceiver;
	
	/**
	 * Backup Mail sender
	 */
	protected String mailSender;
	/**
	 * Backup Mail sender host(smtp.xxx.xxx)
	 */
	protected String mailSenderHost;
	
	/**
	 * Backup Mail sender port, default is 25
	 */
	protected Integer mailSenderPort=25;
	
	/**
	 * Backup Mail sender whether ssl, default is false
	 */
	protected Boolean mailSenderSsl=false;
	/**
	 * Backup Mail sender password
	 */
	protected String mailSenderPassword;
	/**
	 * Backup Mail sender title
	 */
	protected String mailSenderTitle;
	/**
	 * Backup Mail sender template
	 */
	protected String mailSenderTemplate;
	
	/**
	 * Auto delete when send email complete ON or OFF
	 */
	protected Boolean mailDeleteBackup;
	
	
	/**
	 * Execute Command when backup before
	 */
	protected String[] cmdBefore;
	/**
	 * Execute Command when backup after
	 */
	protected String[] cmdAfter;
	
	/**
	 * Execute Command when backup before
	 */
	protected String[] beforeClass;
	/**
	 * Execute Command when backup after
	 */
	protected String[] afterClass;
	

	/**
	 * Whether delete targetFile after backup complete.
	 */
	protected boolean deleteTargetFile;
	
	/**
	 * The time of last backup 
	 */
	protected Date lastBackupTime;
	
	/**
	 * The time of last sender notify
	 */
	protected Date lastSenderTime;
	
	/**
	 * The name of last backup file
	 */
	protected String lastBackupFileName;
	
	/**
	 * The result of last backup file
	 */
	protected boolean lastBackupResult;
	
	

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}



	public String[] getDir() {
		return dir;
	}

	public void setDir(String[] dir) {
		this.dir = dir;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Boolean getCompress() {
		return compress;
	}

	public void setCompress(Boolean compress) {
		this.compress = compress;
	}

	public String getCompressType() {
		return compressType;
	}

	public void setCompressType(String compressType) {
		this.compressType = compressType;
	}

	public String[] getMailReceiver() {
		return mailReceiver;
	}

	public void setMailReceiver(String[] mailReceiver) {
		this.mailReceiver = mailReceiver;
	}

	public String getMailSender() {
		return mailSender;
	}

	public void setMailSender(String mailSender) {
		this.mailSender = mailSender;
	}

	public String getMailSenderHost() {
		return mailSenderHost;
	}

	public void setMailSenderHost(String mailSenderHost) {
		this.mailSenderHost = mailSenderHost;
	}

	public Integer getMailSenderPort() {
		return mailSenderPort;
	}

	public void setMailSenderPort(Integer mailSenderPort) {
		this.mailSenderPort = mailSenderPort;
	}

	public Boolean getMailSenderSsl() {
		return mailSenderSsl;
	}

	public void setMailSenderSsl(Boolean mailSenderSsl) {
		this.mailSenderSsl = mailSenderSsl;
	}

	public String getMailSenderPassword() {
		return mailSenderPassword;
	}

	public void setMailSenderPassword(String mailSenderPassword) {
		this.mailSenderPassword = mailSenderPassword;
	}

	public String getMailSenderTitle() {
		return mailSenderTitle;
	}

	public void setMailSenderTitle(String mailSenderTitle) {
		this.mailSenderTitle = mailSenderTitle;
	}

	public String getMailSenderTemplate() {
		return mailSenderTemplate;
	}

	public void setMailSenderTemplate(String mailSenderTemplate) {
		this.mailSenderTemplate = mailSenderTemplate;
	}

	public Boolean getMailDeleteBackup() {
		return mailDeleteBackup;
	}

	public void setMailDeleteBackup(Boolean mailDeleteBackup) {
		this.mailDeleteBackup = mailDeleteBackup;
	}

	public String[] getCmdBefore() {
		return cmdBefore;
	}

	public void setCmdBefore(String[] cmdBefore) {
		this.cmdBefore = cmdBefore;
	}

	public String[] getCmdAfter() {
		return cmdAfter;
	}

	public void setCmdAfter(String[] cmdAfter) {
		this.cmdAfter = cmdAfter;
	}

	public String[] getBeforeClass() {
		return beforeClass;
	}

	public void setBeforeClass(String[] beforeClass) {
		this.beforeClass = beforeClass;
	}

	public String[] getAfterClass() {
		return afterClass;
	}

	public void setAfterClass(String[] afterClass) {
		this.afterClass = afterClass;
	}

	

	public Date getLastBackupTime() {
		return lastBackupTime;
	}

	public void setLastBackupTime(Date lastBackupTime) {
		this.lastBackupTime = lastBackupTime;
	}

	public Date getLastSenderTime() {
		return lastSenderTime;
	}

	public void setLastSenderTime(Date lastSenderTime) {
		this.lastSenderTime = lastSenderTime;
	}

	public String getLastBackupFileName() {
		return lastBackupFileName;
	}

	public void setLastBackupFileName(String lastBackupFileName) {
		this.lastBackupFileName = lastBackupFileName;
	}

	public boolean isLastBackupResult() {
		return lastBackupResult;
	}

	public void setLastBackupResult(boolean lastBackupResult) {
		this.lastBackupResult = lastBackupResult;
	}

	public String getCompressEncoding() {
		return compressEncoding;
	}

	public void setCompressEncoding(String compressEncoding) {
		this.compressEncoding = compressEncoding;
	}

	public boolean isDeleteTargetFile() {
		return deleteTargetFile;
	}

	public void setDeleteTargetFile(boolean deleteTargetFile) {
		this.deleteTargetFile = deleteTargetFile;
	}

	


	
	
}
