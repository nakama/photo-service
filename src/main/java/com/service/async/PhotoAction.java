package com.service.async;

import java.util.Map;

import com.service.model.PropertyType;
import com.utils.json.JSONMapper;
import com.utils.json.JSONMapperException;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;

public class PhotoAction extends Action {

	private MyLogger logger = new MyLogger(PhotoAction.class);
	
	@Override
	public void run() {
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> msg = (Map<String, Object>) JSONMapper.toObject(message, Map.class);
			String action = (String) msg.get(PropertyType.action);
			logger.logInfo("executing action. "+action);
			@SuppressWarnings("unchecked")
			Map<String, Object> request = (Map<String, Object>) msg.get(PropertyType.request);
			String cb = (String) msg.get(PropertyType.callback);
			String response = super.service.executeAction(action, request);
			logger.logInfo("sending response. "+response);
			super.jmsClient.send(cb, response);
		} catch (JSONMapperException e) {
			logger.logInfo(e.getMessage());
		} catch (ServiceException e) {
			logger.logInfo(e.getMessage());
		}   
		
	}
}

