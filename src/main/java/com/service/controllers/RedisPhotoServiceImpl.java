package com.service.controllers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.MediaOperations;
import org.springframework.social.facebook.api.Tag;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.instagram.api.Caption;
import org.springframework.social.instagram.api.Image;
import org.springframework.social.instagram.api.Media;
import org.springframework.social.instagram.api.PagedMediaList;
import org.springframework.social.instagram.api.impl.InstagramTemplate;
import com.service.async.PhotoUploader;
import com.service.dao.PhotoDao;
import com.service.models.Photo;
import com.utils.DateUtil;
import com.utils.json.JSONMapper;
import com.utils.json.JSONMapperException;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;
import edu.emory.mathcs.backport.java.util.Arrays;

public class RedisPhotoServiceImpl extends PhotoService {

	private PhotoDao photoDao;
	private MyLogger logger = new MyLogger(RedisPhotoServiceImpl.class);
	
	public Object add (Map<String, Object> request) {
		try {
			if (request == null) return ServiceResponse.generateServiceResponse(false, "invalid request", null);
			Map userMap = (Map) request.get("user");
			Map photoMap = (Map) request.get("photo");
			if (userMap == null) return ServiceResponse.generateServiceResponse(false, "invalid user object", null);
			String userIdStr = (String) userMap.get("id");
			BigInteger userId = new BigInteger(userIdStr);
			String name = (String) photoMap.get("name");
			if (name == null) return ServiceResponse.generateServiceResponse(false, "invalid photo name", null);;
			String tagsStr = (String) photoMap.get("tags");
			String url = (String) photoMap.get("url");
			if (url == null) return ServiceResponse.generateServiceResponse(false, "invalid photo url", null);;
			if (tagsStr == null) return ServiceResponse.generateServiceResponse(false, "invalid photo tags", null);;
			String[] tags = tagsStr.split(","); 
			if (tags == null) return ServiceResponse.generateServiceResponse(false, "invalid tags", null);;
			Photo photo = new Photo();
		    Long time = DateUtil.getGMTDateAndTime();
		    photo.setDateCreated(time);
		    photo.setLastUpdated(time);
		    photo.setName(name);
		    photo.setUserId(userId);
		    photo.setTags(Arrays.asList(tags));
		    photo.setSystemTime(new Date());
		    photo.setUrl(url);
		    photo.setUploadStatus(0);
		    photoDao.add(photo);
			return ServiceResponse.generateSuccessServiceResponse(photo);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object update (Map<String, Object> request) {
		try {
			if (request == null) return ServiceResponse.generateServiceResponse(false, "invalid request", null);
			Map photoMap = (Map) request.get("photo");
			if (photoMap == null) return ServiceResponse.generateServiceResponse(false, "photo object not found", null);
			String idStr = (String) photoMap.get("id");
			if (idStr == null) return ServiceResponse.generateServiceResponse(false, "invalid photo id", null);
			BigInteger id = new BigInteger(idStr);
			String name = (String) photoMap.get("name");
			if (name == null) return ServiceResponse.generateServiceResponse(false, "invalid photo name", null);;
			String tagsStr = (String) photoMap.get("tags");
			if (tagsStr == null) return ServiceResponse.generateServiceResponse(false, "invalid photo tags", null);;
			String[] tags = tagsStr.split(","); 
			if (tags == null) return ServiceResponse.generateServiceResponse(false, "invalid tags", null);;
			
			Photo photo = photoDao.get(id);
			if (photo == null) return ServiceResponse.generateServiceResponse(false, "photo not found", null);
			photo = new Photo();
		    Long time = DateUtil.getGMTDateAndTime();
		    photo.setLastUpdated(time);
		    photo.setName(name);
		    photo.setTags(Arrays.asList(tags));
		    photoDao.add(photo);
			return ServiceResponse.generateSuccessServiceResponse(photo);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object delete (Map<String, Object> request) {
		try {
			if (request == null) return ServiceResponse.generateServiceResponse(false, "invalid request", null);
			Map photoMap = (Map) request.get("photo");
			if (photoMap == null) return ServiceResponse.generateServiceResponse(false, "photo object not found", null);
			String idStr = (String) photoMap.get("id");
			if (idStr == null) return ServiceResponse.generateServiceResponse(false, "invalid id", null);
			BigInteger id = new BigInteger(idStr);
			photoDao.delete(id);
			return ServiceResponse.generateSuccessServiceResponse(null);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} 
	}
	
	public Object get (Map<String, Object> request) {
		try {
			if (request == null) return ServiceResponse.generateServiceResponse(false, "invalid request", null);
			Map photoMap = (Map) request.get("photo");
			if (photoMap == null) return ServiceResponse.generateServiceResponse(false, "photo object not found", null);
			String idStr = (String) photoMap.get("id");
			if (idStr == null) return ServiceResponse.generateServiceResponse(false, "invalid id", null);
			BigInteger id = new BigInteger(idStr);
			Photo photo = photoDao.get(id);
			if (photo == null) return ServiceResponse.generateServiceResponse(false, "photo not found", null);
			return ServiceResponse.generateSuccessServiceResponse(photo);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object list (Map<String, Object> request) {
		if (request == null) return ServiceResponse.generateServiceResponse(false, "invalid request", null);
			Map userMap = (Map) request.get("user");
			if (userMap == null) return ServiceResponse.generateServiceResponse(false, "invalid user object", null);
			String userId = (String) userMap.get("id");
			BigInteger id = new BigInteger(userId);
			String index = (String) userMap.get("index");
			if (index == null) index = "0";
			BigInteger indx = null;
			if (index != null) indx = new BigInteger(index);
			Integer limit = (Integer) userMap.get("limit");
			if (limit == null) limit  = 100;
			List<Photo> list = photoDao.list(id, indx, limit);
			if (list.isEmpty()) return ServiceResponse.generateServiceResponse(false, "no photo found", null);
			return ServiceResponse.generateSuccessServiceResponse(list);
	}

	public Object find (Map<String, Object> request) {
		if (request == null) return ServiceResponse.generateServiceResponse(false, "invalid request", null);
		Map userMap = (Map) request.get("user");
		if (userMap == null) return ServiceResponse.generateServiceResponse(false, "invalid user object", null);
		String userId = (String) userMap.get("id");
		BigInteger id = new BigInteger(userId);
		String tagStr = (String) userMap.get("tags");
		String[] tags = tagStr.split(",");
		if (tags.length == 0) return ServiceResponse.generateServiceResponse(false, "invalid tags", null);
		List<Photo> list = photoDao.find(id, tags);
		if (list.isEmpty()) return ServiceResponse.generateServiceResponse(false, "no photo found", null);
		return ServiceResponse.generateSuccessServiceResponse(list);
}
	
	public Object fetch (Map<String, Object> request, String callback) {	
		if (request == null) return ServiceResponse.generateServiceResponse(false, "invalid request", null);
		Map userMap = (Map) request.get("user");
		if (userMap == null) return ServiceResponse.generateServiceResponse(false, "invalid user object", null);
		String userId = (String) userMap.get("id");
		BigInteger accId = new BigInteger(userId);
		Map services = (Map) userMap.get("services");
		if (services == null) return ServiceResponse.generateServiceResponse(false, "invalid services object", null);
		Map service = null;
		
		if (services.containsKey("instagram")){
			service = (Map) services.get("instagram");
			String instagramAppId = "181bd8727613437f9f50e5b4aea03572";
			String token = (String) service.get("auth_token");
			String id = (String) service.get("id");
			fetchInstagram(instagramAppId, accId, token, Long.parseLong(id), callback);
		}
		if (services.containsKey("facebook")){
			service = (Map) services.get("facebook");
			String token = (String) service.get("auth_token");
			String id = (String) service.get("id");
			fetchFacebook(accId, token, callback);
		}
		
		return ServiceResponse.generateSuccessServiceResponse("photos processed usrId."+accId);
		
	}
	
	
	public void fetchInstagram (String appId, BigInteger accid, String accessToken, Long userId, String cb){
		//InstagramTemplate instagram = new InstagramTemplate("181bd8727613437f9f50e5b4aea03572", "18632811.6692e4c.5c9a339173dc4fe9ae0faff04c363a56");
		//PagedMediaList out = instagram.userOperations().getRecentMedia(18632811);
		
		InstagramTemplate instagram = new InstagramTemplate(appId, accessToken);
		PagedMediaList out = instagram.userOperations().getRecentMedia(userId);
		for (Media ma: out.getList()){
			Caption caption = ma.getCaption();
			if (caption == null) continue;
			Map<String, Image> images = ma.getImages();
			String url = images.get("standard_resolution").getUrl();
			Photo photo = new Photo();
			photo.setAtt(null);
			photo.setLastUpdated(new Date().getTime());
			photo.setDateCreated(new Date().getTime());
			String text = caption.getText();
			if (text == null) text = "n/a";
			photo.setName(text);
			photo.setUrl(url);
			photo.setUploadStatus(0);
			photo.setSource("instagram");
			photo.setSystemTime(new Date());
			List<String> tags = ma.getTags();
			tags.add("instagram");
			photo.setTags(tags);
			photo.setUserId(accid);
			photo.setPhotoId(ma.getId());
			try {
				boolean isPhotoFetched = photoDao.isPhotoFetched(accid, ma.getId());
				logger.logInfo(ma.getId()+"is photo fetched. "+isPhotoFetched);
				if (!isPhotoFetched) {
					photoDao.add(photo);
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void fetchFacebook(BigInteger accid, String accessToken,String cb){
		FacebookTemplate facebook = new FacebookTemplate(accessToken);
		MediaOperations mediaOperations = facebook.mediaOperations();
		List<Album> albums = mediaOperations.getAlbums();
		for (Album album : albums){
			List<org.springframework.social.facebook.api.Photo> photos = mediaOperations.getPhotos(album.getId());
			for (org.springframework.social.facebook.api.Photo fphoto : photos){
				Photo photo = new Photo();
				photo.setAtt(null);
				photo.setLastUpdated(fphoto.getUpdatedTime().getTime());
				photo.setDateCreated(fphoto.getCreatedTime().getTime());
				String name = fphoto.getName();
				if (name == null) name = DateUtil.getDate(photo.getLastUpdated());
				photo.setName(name);
				photo.setUrl(fphoto.getSourceImage().getSource());
				photo.setUploadStatus(0);
				photo.setSource("facebook");
				photo.setSystemTime(new Date());
				List<Tag>tags = fphoto.getTags();
				Tag facebookTag = new Tag(fphoto.getId(), "facebook", null, null, new Date());
				tags.add(facebookTag);
				List<String> tagList = new ArrayList<String>();
				if (tags != null){
				for (Tag tag :tags) {
					tagList.add(tag.getName());
				}
				}
				photo.setTags(tagList);
				photo.setUserId(accid);
				photo.setName(fphoto.getName());
				photo.setPhotoId(fphoto.getId());
				try {
					boolean isPhotoFetched = photoDao.isPhotoFetched(accid, fphoto.getId());
					logger.logInfo(fphoto.getId()+"is photo fetched. "+isPhotoFetched);
					if (!isPhotoFetched)
					if (!photoDao.isPhotoFetched(accid, fphoto.getId()))
						photoDao.add(photo);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				break;
				
			}
		}
	}
	
	@Override
	public void executeAction(String message) throws ServiceException {
		try {
			Map<String,Object> msg = (Map<String, Object>) JSONMapper.toObject(message, Map.class);
			String action = (String) msg.get("action");
			Map<String, Object> request = (Map<String, Object>) msg.get("request");
			String cb = (String) msg.get("callback");
			   Object out =  action.equals("add") ? add(request) : 
			   action.equals("delete") ? delete(request) :
			   action.equals("update") ? update(request) :
			   action.equals("list") ? list(request) :
			   action.equals("find") ? find(request) :
			   action.equals("get") ? get(request) :
			   action.equals("fetch") ? fetch(request, cb) :  "action not found";
		} catch (JSONMapperException e) {
			logger.logInfo("invalid message structure");
		}   
			   
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
