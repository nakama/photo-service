package com.service.async;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.utils.logging.MyLogger;


@Component
public class PhotoUploaderJob extends QuartzJobBean {

	private MyLogger logger = new MyLogger(PhotoUploaderJob.class);
	
	private PhotoUploader photoUploader;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.logInfo("Executing job uploads");
		photoUploader.upload();
		photoUploader.update();
		
	}

	public PhotoUploader getPhotoUploader() {
		return photoUploader;
	}

	public void setPhotoUploader(PhotoUploader photoUploader) {
		this.photoUploader = photoUploader;
	}
	
	
}
