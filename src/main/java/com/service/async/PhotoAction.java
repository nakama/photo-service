package com.service.async;

import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;

public class PhotoAction extends Action {

	private MyLogger logger = new MyLogger(PhotoAction.class);
	
	@Override
	public void run() {
		try {
			logger.logInfo("executing action");
			super.service.executeAction(message);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
	}
}

