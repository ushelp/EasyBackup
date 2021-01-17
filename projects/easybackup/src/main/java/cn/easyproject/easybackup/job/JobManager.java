package cn.easyproject.easybackup.job;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.easyproject.easybackup.configuration.BackupConfiguration;

/**
 * Easy Job Manager
 * 
 * @author easyproject.cn
 *
 * @since 1.0.0
 */
public class JobManager {

	static Logger logger = LoggerFactory.getLogger(JobManager.class);

	// Scheduler
	Scheduler scheduler;
	
	
	private int reloadIntervalInSeconds=3;
	/**
	 * 配置文件热加载任务
	 */
	public void reloadConfigurationJob() {
		String jobGroup = "ReloadConfiguraion";
		String jobName = "EasyBackup";

		// JobDetail
		JobKey jobKey = new JobKey(jobName, jobGroup);
		try {
			if (scheduler.checkExists(jobKey)) {
				scheduler.deleteJob(jobKey);
			}
		} catch (SchedulerException e1) {
			logger.error("delete job [" + jobKey + "] error.", e1);
		}

		try {
			if (scheduler.checkExists(jobKey)) {
				jobKey = new JobKey(jobName + "_" + new Date().getTime(), jobGroup);
			}
		} catch (SchedulerException e1) {
			jobKey = new JobKey(jobName + "_" + new Date().getTime(), jobGroup);
		}

		// JobDetail
		JobDetail jobDetail = JobBuilder.newJob(ReloadConfigurationJob.class).withIdentity(jobKey).build();

		TriggerKey triggerKey = new TriggerKey(jobKey.getName(), jobKey.getGroup());

		// Trigger
		// Trigger the job to run now, and then repeat every 40 seconds
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(reloadIntervalInSeconds).repeatForever()).build();

		// Tell quartz to schedule the job using our trigger
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			logger.error("ReloadConfiguration Job can not fire!", e);
		}
	}

	/**
	 * 根据监控配置添加新的监控
	 * 
	 * @param configuration
	 *            监控配置对象
	 */
	public void addJob(BackupConfiguration configuration) {
		if (configuration.getEnable()) {

			String jobGroup = configuration.getType().name();
			String jobName = jobGroup + "_" + configuration.getName();

			// JobDetail
			JobKey jobKey = new JobKey(jobName, jobGroup);
			try {
				if (scheduler.checkExists(jobKey)) {
					scheduler.deleteJob(jobKey);
				}
			} catch (SchedulerException e1) {
				logger.error("delete job [" + jobKey + "] error.", e1);
			}

			try {
				if (scheduler.checkExists(jobKey)) {
					jobKey = new JobKey(jobName + "_" + new Date().getTime(), jobGroup);
				}
			} catch (SchedulerException e1) {
				jobKey = new JobKey(jobName + "_" + new Date().getTime(), jobGroup);
			}

			JobDetail jobDetail = JobBuilder.newJob(BackupJob.class).withIdentity(jobKey).build();

			TriggerKey triggerKey = new TriggerKey(jobKey.getName(), jobKey.getGroup());

			// Trigger 定义触发器
			CronTrigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerKey).withSchedule(CronScheduleBuilder
							.cronSchedule(configuration.getCronExpression()).withMisfireHandlingInstructionDoNothing())
					.build();

			// JobDataMap
			jobDetail.getJobDataMap().put("configuration", configuration);

			try {
				scheduler.scheduleJob(jobDetail, trigger);
			} catch (SchedulerException e) {
				logger.error("Your Job can not fire! [" + configuration + "]", e);
			}

		}
	}

	/**
	 * Pause Job
	 * 
	 * @param context
	 *            JobExecutionContext
	 */
	public void pauseJob(JobExecutionContext context) {
		// 暂停定时任务
		JobKey key = context.getJobDetail().getKey();
		// 暂停任务
		try {
			context.getScheduler().pauseJob(key);
		} catch (SchedulerException e) {
			logger.error("Your Job pause failure! [" + key + "]", e);
		}
	}

	/**
	 * Resume Job
	 * 
	 * @param context
	 *            JobExecutionContext
	 */
	public void resumeJob(JobExecutionContext context) {
		// 暂停定时任务
		JobKey key = context.getJobDetail().getKey();
		// 暂停任务
		try {
			context.getScheduler().resumeJob(key);
		} catch (SchedulerException e) {
			logger.error("Your Job pause failure! [" + key + "]", e);
		}
	}

	/**
	 * Delete Job
	 * 
	 * @param context
	 *            JobExecutionContext
	 */
	public void deleteJob(JobExecutionContext context) {
		// 暂停定时任务
		JobKey key = context.getJobDetail().getKey();
		// 暂停任务
		try {
			context.getScheduler().deleteJob(key);
		} catch (SchedulerException e) {
			logger.error("Your Job delete failure! [" + key + "]", e);
		}
	}

	/**
	 * Clear all Job
	 */
	public void clear() {
		try {
			scheduler.clear();
		} catch (SchedulerException e) {
			logger.error("Can not reload properties, clear job failure!", e);
		}

	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

}
