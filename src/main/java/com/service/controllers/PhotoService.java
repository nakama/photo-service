package com.service.controllers;

import com.service.jms.RedisJMSClient;
import com.utils.wsutils.ServiceException;

public abstract class PhotoService   {

	public RedisJMSClient jmsClient;
	public abstract void executeAction (String request) throws ServiceException ;
	public abstract String executeAction (String action, Object request) throws ServiceException ;
	
}
