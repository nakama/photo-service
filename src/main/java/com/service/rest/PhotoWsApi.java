package com.service.rest;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.service.controller.PhotoService;
import com.utils.json.JSONMapper;
import com.utils.json.JSONMapperException;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;

@Path("ws")
public class PhotoWsApi {
	
	private static MyLogger logger = new MyLogger(PhotoWsApi.class);
	private PhotoService restPhotoService;
	
	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String add (String paylaod)  {
		logger.logInfo("add photo");
		try {
			Map<String,Object> request = (Map<String, Object>) JSONMapper.toObject(paylaod, Map.class);
			return (String) restPhotoService.executeAction("add", request);
		} catch (ServiceException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null); 
		} catch (JSONMapperException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null); 
		}
	}
	
	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String update (String paylaod)  {
		logger.logInfo("update photo");
		try {
			Map<String,Object> request = (Map<String, Object>) JSONMapper.toObject(paylaod, Map.class);
			return (String) restPhotoService.executeAction("update", request);
		} catch (ServiceException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null); 
		} catch (JSONMapperException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null); 
		}
	}
	
	@POST
	@Path("fetch")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String fetch (String paylaod)  {
		logger.logInfo("fetch photos");
		try {
			Map<String,Object> request = (Map<String, Object>) JSONMapper.toObject(paylaod, Map.class);
			return (String) restPhotoService.executeAction("fetch", request);
		} catch (ServiceException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null); 
		} catch (JSONMapperException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null); 
		}
	}
	
	@GET
	@Path("autocomplete/{id}/{word}")
	@Produces(MediaType.APPLICATION_JSON) 
	public String get (@PathParam("id") String id, @PathParam("word") String word)  {
		logger.logInfo("get user");
		try {
			Map<String,Object> request = new HashMap<String,Object>();
			request.put("userId", id);
			request.put("word", word);
			return (String) restPhotoService.executeAction("autocomplete", request);
		} catch (ServiceException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null); 
		}
	}
	

	
	


	public static MyLogger getLogger() {
		return logger;
	}

	public static void setLogger(MyLogger logger) {
		PhotoWsApi.logger = logger;
	}

	public PhotoService getRestPhotoService() {
		return restPhotoService;
	}

	public void setRestPhotoService(PhotoService restPhotoService) {
		this.restPhotoService = restPhotoService;
	}

	
	
}
