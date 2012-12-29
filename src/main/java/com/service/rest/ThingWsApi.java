package com.service.rest;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.service.controller.ActionHandler;
import com.utils.json.JSONMapper;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;

@Path("ws")
public class ThingWsApi {
	
	private static MyLogger logger = new MyLogger(ThingWsApi.class);
	private ActionHandler restThingService;
	
	
	@GET
	@Path("autocomplete/{id}/{word}")
	@Produces(MediaType.APPLICATION_JSON) 
	public String get (@PathParam("id") String id, @PathParam("word") String word)  {
		logger.logInfo("get user");
		try {
			Map<String,Object> request = new HashMap<String,Object>();
			request.put("userId", id);
			request.put("word", word);
			return JSONMapper.toString(restThingService.executeAction("photo", "autocomplete", request).getObject());
		} catch (ServiceException e) {
			return JSONMapper.toString(ServiceResponse.generateServiceResponse(false, e.getMessage(), null)); 
		}
	}
	
	@GET
	@Path("fetch/facebook/{userId}/{token}")
	public String fetchFacebook (@PathParam("userId") String userId, @PathParam("token") String token)  {
		logger.logInfo("fetchFacebook");
		try {
			Map<String,Object> request = new HashMap<String,Object>();
			request.put("userId", userId);
			request.put("token", token);
			return JSONMapper.toString(restThingService.executeAction("photo", "fetchfacebook", request).getObject());
		} catch (ServiceException e) {
			return JSONMapper.toString(ServiceResponse.generateServiceResponse(false, e.getMessage(), null)); 
		}
	}
	
	@GET
	@Path("fetch/instagram/{userId}/{socialUserId}/{token}")
	public String fetchInstagram (@PathParam("userId") String userId, @PathParam("socialUserId") String socialUserId, @PathParam("token") String token)  {
		logger.logInfo("fetchFacebook");
		try {
			Map<String,Object> request = new HashMap<String,Object>();
			request.put("userId", userId);
			request.put("token", token);
			request.put("socialUserId", socialUserId);
			return JSONMapper.toString(restThingService.executeAction("photo", "fetchinstagram", request).getObject());
		} catch (ServiceException e) {
			return JSONMapper.toString(ServiceResponse.generateServiceResponse(false, e.getMessage(), null)); 
		}
	}
	
	
	@GET
	@Path("listS3Pending")
	@Produces(MediaType.APPLICATION_JSON) 
	public String listS3Pending ()  {
		logger.logInfo("listS3Pending");
		try {
			return JSONMapper.toString(restThingService.executeAction("photo", "listS3Pending", null).getObject());
		} catch (ServiceException e) {
			return JSONMapper.toString(ServiceResponse.generateServiceResponse(false, e.getMessage(), null)); 
		}
	}
	

	public static MyLogger getLogger() {
		return logger;
	}

	public static void setLogger(MyLogger logger) {
		ThingWsApi.logger = logger;
	}

	public ActionHandler getRestThingService() {
		return restThingService;
	}

	public void setRestThingService(ActionHandler restThingService) {
		this.restThingService = restThingService;
	}

	
	
}
