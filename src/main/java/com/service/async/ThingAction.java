package com.service.async;

import java.util.Map;
import com.service.model.PropertyType;
import com.utils.activemq.JmsSender;
import com.utils.json.JSONMapper;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceResponse;

public class ThingAction extends Action {

	private MyLogger logger = new MyLogger(ThingAction.class);
	private JmsSender jmsSender;
	@Override
	public void run() {
		try {
			logger.logInfo("handling request. "+message);
			@SuppressWarnings("unchecked")
			Map<String,Object> msg = (Map<String, Object>) JSONMapper.toObject(message, Map.class);
			String action = (String) msg.get(PropertyType.action);
			logger.logInfo("executing action. "+action);
			@SuppressWarnings("unchecked")
			Map<String, Object> request = (Map<String, Object>) msg.get(PropertyType.request);
			String cb = (String) msg.get(PropertyType.callback);
			ServiceResponse response = super.service.executeAction(thing, action, request);
			logger.logInfo("sending response. "+response);
			super.jmsClient.send(cb, response.toString());
			if (response.isSuccess() && hasThingChanged(action)) {
				msg.put("request", response.getObject());
				jmsSender.sendTextMessage(JSONMapper.toString(msg));
			}
		} catch (Exception e) {
			logger.logError(e.getMessage(), e);
		}   
		
	}
	public JmsSender getJmsSender() {
		return jmsSender;
	}
	public void setJmsSender(JmsSender jmsSender) {
		this.jmsSender = jmsSender;
	}
	
	public boolean hasThingChanged (String action){
		   return action.equals("add") ? true : 
			   action.equals("delete") ? true :
			   action.equals("deleteAll") ? true :
			   action.equals("update") ? true :
			   action.equals("list") ? false :
			   action.equals("get") ? false:
			   action.equals("addtags") ? true :
			   action.equals("removetags") ? true :
			   action.equals("search") ? false :
			   action.equals("fetch") ? false :  false;
	}
	
	
}

