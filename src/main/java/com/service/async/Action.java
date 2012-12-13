package com.service.async;


import com.service.controllers.PhotoService;
import com.service.jms.RedisJMSClient;


public abstract class Action implements Runnable {
	
	public String name;
	public String message;
	public PhotoService service;
	public RedisJMSClient jmsClient;
	
	public PhotoService getService() {
		return service;
	}
	public void setService(PhotoService service) {
		this.service = service;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public RedisJMSClient getJmsClient() {
		return jmsClient;
	}
	public void setJmsClient(RedisJMSClient jmsClient) {
		this.jmsClient = jmsClient;
	}
	
	
}
