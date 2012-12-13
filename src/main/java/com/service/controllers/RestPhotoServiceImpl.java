package com.service.controllers;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.instagram.api.Caption;
import org.springframework.social.instagram.api.Image;
import org.springframework.social.instagram.api.Media;
import org.springframework.social.instagram.api.PagedMediaList;
import org.springframework.social.instagram.api.impl.InstagramTemplate;

import com.service.dao.PhotoDao;
import com.service.models.Photo;
import com.utils.json.JSONMapper;
import com.utils.json.JSONMapperException;
import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;

public class RestPhotoServiceImpl extends PhotoService {

	private PhotoDao photoDao;
	
	
	@Override
	public void executeAction(String reqeust) {
	
	}

	private String fetch(Object map) {
		try {
		Map<String, Object> req = (Map<String, Object>) JSONMapper.toObject(map.toString(), Map.class);
		if (req == null) return (String) ServiceResponse.generateServiceResponse(false, "invalid request", null);
		String idStr = (String) req.get("user_id");
		if (idStr == null) return (String) ServiceResponse.generateServiceResponse(false, "invalid user id", null);
		BigInteger accId = new BigInteger(idStr);
		Map service = (Map) req.get("service");
		String name = (String) service.get("name");
		String appId = (String) service.get("app_id");
		String token = (String) service.get("auth_token");
		String id = (String) service.get("user_id");
		if (name.toLowerCase().equals("instagram")) {
			fetchInstagram(appId, accId, token, Long.parseLong(id));
			return (String) ServiceResponse.generateSuccessServiceResponse("instagram photos processed usrId."+accId);
		} 
		if (name.toLowerCase().equals("facebook")) {
			fetchFacebook();
			return (String) ServiceResponse.generateSuccessServiceResponse("facebook photos processed usrId."+accId);
		} 
		return (String) ServiceResponse.generateSuccessServiceResponse("no photos to process");
		} catch (JSONMapperException e) {
			return (String) ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}

	public void fetchInstagram (String appId, BigInteger accid, String accessToken, Long userId){
		//InstagramTemplate instagram = new InstagramTemplate("181bd8727613437f9f50e5b4aea03572", "18632811.6692e4c.5c9a339173dc4fe9ae0faff04c363a56");
		//PagedMediaList out = instagram.userOperations().getRecentMedia(18632811);
		
		InstagramTemplate instagram = new InstagramTemplate(appId, accessToken);
		PagedMediaList out = instagram.userOperations().getRecentMedia(userId);
		for (Media ma: out.getList()){
			Caption caption = ma.getCaption();
			Map<String, Image> images = ma.getImages();
			String url = images.get("standard_resolution").getUrl();
			Photo photo = new Photo();
			photo.setAtt(null);
			photo.setLastUpdated(new Date().getTime());
			photo.setDateCreated(new Date().getTime());
			photo.setName(caption.getText());
			photo.setUrl(url);
			photo.setUploadStatus(0);
			photo.setSource("instagram");
			photo.setSystemTime(new Date());
			photo.setTags(ma.getTags());
			photo.setUserId(accid);
			photo.setName(ma.getId());
			try {
				if (!photoDao.isPhotoFetched(accid, ma.getId()))
					photoDao.add(photo);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void fetchFacebook(){
		
		
	}
	private Object get(Object map) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object list(Object userId) {
		String idStr = (String) userId;
		if (idStr == null) return ServiceResponse.generateServiceResponse(false, "invalid user Id", null);
		BigInteger id = new BigInteger(idStr);
		List<Photo> list = photoDao.list(id, id, 0);
		if (list.isEmpty()) return ServiceResponse.generateServiceResponse(false, "not photo found", null);
		
		return ServiceResponse.generateSuccessServiceResponse(list);
	}

	private Object update(Object map) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object delete(Object map) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object add(Object request) {
		// TODO Auto-generated method stub
		return null;
	}

	public PhotoDao getPhotoDao() {
		return photoDao;
	}

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	@Override
	public String executeAction(String action, Object request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
