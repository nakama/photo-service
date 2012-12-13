package com.service.rest;

import java.math.BigInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.service.controllers.PhotoService;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;

@Path("photo")
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
			return (String) restPhotoService.executeAction("add", paylaod);
		} catch (ServiceException e) {
			return null;
		}
	}
	
	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String update (String paylaod)  {
		logger.logInfo("update photo");
		try {
			return (String) restPhotoService.executeAction("update", paylaod);
		} catch (ServiceException e) {
			return null;
		}
	}
	
	@POST
	@Path("fetch")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String fetch (String paylaod)  {
		logger.logInfo("fetch photos");
		try {
			return (String) restPhotoService.executeAction("fetch", paylaod);
		} catch (ServiceException e) {
			return null;
		}
	}
	
	@GET
	@Path("get/id/{id}")
	@Produces(MediaType.APPLICATION_JSON) 
	public String get (@PathParam("id") BigInteger id)  {
		logger.logInfo("get user");
		try {
			return (String) restPhotoService.executeAction("get", id);
		} catch (ServiceException e) {
			return null;
		}
	}
	
	@GET
	@Path("list/{id}")
	@Produces(MediaType.APPLICATION_JSON) 
	public String list (@PathParam("id") String id)  {
		logger.logInfo("get user");
		try {
			return (String) restPhotoService.executeAction("list", id);
		} catch (ServiceException e) {
			return null;
		}
	}
	
	
	@POST
	@Path("delete")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String delete (String payload)  {
		logger.logInfo("delete user");
		try {
			return (String) restPhotoService.executeAction("delete", payload);
		} catch (ServiceException e) {
			return null;
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
