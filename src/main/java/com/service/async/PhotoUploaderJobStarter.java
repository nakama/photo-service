package com.service.async;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.utils.logging.MyLogger;


@Component
public class PhotoUploaderJobStarter implements InitializingBean, DisposableBean {

	private MyLogger logger = new MyLogger(PhotoUploaderJobStarter.class);
	

	
	@Autowired
	@Qualifier("photoUploaderJobDetail")
	private JobDetail photoUploaderJobDetail;
	
	// by default 30 seconds
	private static final String DEFAULT_CRON_EXP = "0/60 * * * * ?";
	
	private Scheduler sched;
	
	public void start() {
		
		try {
			
			logger.logInfo("NotificationJobStarter ... starting scheduler ");
			
			String cronExpression = DEFAULT_CRON_EXP;
			SchedulerFactory sf = new StdSchedulerFactory();
			sched = sf.getScheduler();
				
			CronTrigger trigger = new CronTrigger("notification_trigger", "notification_group", 
					"notification_job", "notification_group", cronExpression);
			
			sched.scheduleJob(photoUploaderJobDetail, trigger);
			
	        sched.start();
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}

	@Override
	public void destroy() throws Exception {
		shutdown();
	}

	private void shutdown() {
		try {
			if (sched != null) {
				logger.logInfo("NotificationJobStarter ... shutting down scheduler ");
				sched.shutdown();
			}
		} catch (Exception e) {
			
		}
	}

}
