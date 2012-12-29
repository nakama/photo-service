package com.service.controller;

import java.util.Map;

import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;

public interface ActionHandler   {

	public ServiceResponse executeAction (String thing, String action, Map<String,Object> request) throws ServiceException ;
	
}
