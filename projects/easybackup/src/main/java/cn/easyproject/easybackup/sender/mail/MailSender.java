package cn.easyproject.easybackup.sender.mail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.FileSystemUtils;

import cn.easyproject.easybackup.configuration.BackupConfiguration;
import cn.easyproject.easybackup.sender.Sender;
import cn.easyproject.easybackup.util.CompressFileUtil;
import cn.easyproject.easybackup.util.EasyUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
/**
 * EasyBackup MailSender
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class MailSender implements Sender {

	static Logger logger = LoggerFactory.getLogger(MailSender.class);

	private static Configuration freemarkerConfiguration ;
	
	
	/**
	 * 加载默认的 Freemarker 配置文件
	 */
	private void loadFreemarkerConfiguration(){
		// 加载默认的 Freemarker 配置文件
		if(freemarkerConfiguration==null){
			freemarkerConfiguration= new Configuration(Configuration.VERSION_2_3_23);
			freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			freemarkerConfiguration.setDefaultEncoding("UTF-8");
			try {
				FileTemplateLoader ftl1 = new FileTemplateLoader(new File("template"));
				ClassTemplateLoader ctl = new ClassTemplateLoader(MailSender.class, "/template");
				TemplateLoader[] loaders = new TemplateLoader[] { ftl1,  ctl };
				MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
				freemarkerConfiguration.setTemplateLoader(mtl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	

	private JavaMailSenderImpl sender = new JavaMailSenderImpl();
	
	public void send(File targetFile,List<File> backupFiles, BackupConfiguration configuration) {
		loadFreemarkerConfiguration();
		System.out.println(configuration.getMailSender());
		System.out.println(Arrays.toString(configuration.getMailReceiver()));
		System.out.println(configuration.getMailReceiver().length);
		
		if(!EasyUtil.isNotEmpty(configuration.getMailSender())||configuration.getMailReceiver()==null||configuration.getMailReceiver().length==0){
			logger.warn("You are not configuration Mail Sender or Receiver. Please Check your mail sender and recevier.");
			return;
		}
		
		
		try {
			
			logger.info(configuration.getType().name() + "-" + configuration.getName()
					+ ", start send backup file ["+backupFiles+"] to mail[" + Arrays.toString(configuration.getMailReceiver()) + "]");
			sender.setUsername(configuration.getMailSender());
			sender.setPassword(configuration.getMailSenderPassword());
			sender.setHost(configuration.getMailSenderHost());
			sender.setPort(configuration.getMailSenderPort());

			Properties senderProperties = new Properties();
			senderProperties.setProperty("mail.smtp.auth", "true");
			if (configuration.getMailSenderSsl()) {
				senderProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			}
			sender.setJavaMailProperties(senderProperties);

			// SimpleMailMessage smm=new SimpleMailMessage();
			MimeMessage mm = sender.createMimeMessage();
			MimeMessageHelper smm = new MimeMessageHelper(mm, true,"utf-8");// 设置true，multipart消息
			
			smm.setFrom(configuration.getMailSender()); // 发件人，必须和Spring中配置的sender的名字保持一致
			smm.setTo(configuration.getMailReceiver()); // 收件人
			smm.setReplyTo(configuration.getMailSender()); // 回复地址，当点击回复的时候会回复到该地址
			smm.setSubject(configuration.getMailSenderTitle()); // 主题

			
			Template template = freemarkerConfiguration.getTemplate(configuration.getMailSenderTemplate());

			
			
			Map<String,Object> data = new HashMap<String,Object>(); // 通过Map传递动态数据
			// 动态数据的名字和模板标签中指定属性相匹配
			data.put("targetFileName", targetFile.getName());
			data.put("backupFileName",configuration.getLastBackupFileName());
			data.put("type", configuration.getType().name());
			data.put("name", configuration.getName());
			data.put("value", configuration.getValue());
			data.put("backuptime", configuration.getLastBackupTime());
			data.put("backupConfiguration", configuration);

			// 解析模板并替换动态数据，产生最终的内容
			String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);
			smm.setText(text, true); // 内容

			File backupFile=backupFiles.get(0);
			
			if(backupFile.isFile()){
				smm.addAttachment(backupFile.getName(), backupFile);
				sender.send(mm); //发送
			}else{
				File zipFile=new File(backupFile.getParent(),backupFile.getName()+".zip");
				CompressFileUtil.compressToZip(backupFile, zipFile);
				smm.addAttachment(zipFile.getName(), zipFile);
				sender.send(mm); //发送
				zipFile.delete();
			}
			
			
			// mail.delete.backup
			if(configuration.getMailDeleteBackup()){
				logger.info(configuration.getType().name() 
						+ 
						"-" 
						+ configuration.getName()
						+ " send over, delete backup file."
						);
				FileSystemUtils.deleteRecursively(backupFile);
			}
			
			
			logger.info(configuration.getType().name() 
					+ 
					"-" 
					+ configuration.getName()
					+ " already send mail to [" + Arrays.toString(configuration.getMailReceiver())+"."
					+ " last backup file name: "+configuration.getLastBackupFileName()
					+ ", last backup file time: "+configuration.getLastBackupTime()
					+ ", last backup file result: "+configuration.isLastBackupResult()
					);
		} catch (MailException e) {
			logger.error(configuration.getType().name() + "-" + configuration.getName()
					+ " send mail error, please check you sender configuration", e);
		} catch (MessagingException e) {
			logger.error(configuration.getType().name() + "-" + configuration.getName()
					+ " send mail error, please check you sender configuration", e);
		} catch (IOException e) {
			logger.error(configuration.getType().name() + "-" + configuration.getName()
					+ " send mail error, please check you sender configuration", e);
		} catch (TemplateException e) {
			logger.error(configuration.getType().name() + "-" + configuration.getName()
					+ " send mail template error, please check you mail.tpl configuration", e);
		} catch (Exception e) {
			logger.error(configuration.getType().name() + "-" + configuration.getName()
			+ " send mail error, please check you sender configuration", e);
		}
		
	}
	/**
	 * 设置默认的 freemarker 模板配置文件 
	 * @param freemarkerConfiguration 设置默认的 freemarker 模板配置文件 
	 */
	public static void setFreemarkerConfiguration(Configuration freemarkerConfiguration) {
		MailSender.freemarkerConfiguration = freemarkerConfiguration;
	}
	

}
