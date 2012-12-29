package com.service.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.MediaOperations;
import org.springframework.social.facebook.api.Tag;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.instagram.api.Caption;
import org.springframework.social.instagram.api.Image;
import org.springframework.social.instagram.api.Media;
import org.springframework.social.instagram.api.PagedMediaList;
import org.springframework.social.instagram.api.impl.InstagramTemplate;
import redis.clients.jedis.Tuple;
import com.service.dao.ThingDao;
import com.service.jms.RedisJMSClient;
import com.service.model.PropertyType;
import com.service.model.TagType;
import com.service.model.Thing;
import com.utils.StringUtil;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;

public class RestThingServiceImpl extends ThingCRUD implements ActionHandler {

	private ThingDao thingDao;
	private RedisJMSClient redisClient;
	private MyLogger logger = new MyLogger(RestThingServiceImpl.class);
	
	@Override
	public ServiceResponse executeAction(String thing, String action, Map<String, Object> request) throws ServiceException {
		   StringUtil.validateNull(request, "request");
		   Object out =  action.equals("autocomplete") ? autocomplete(request) : 
			   		action.equals("fetchinstagram") ? fetchinstagram(request) :
				    action.equals("fetchfacebook") ? fetchfacebook(request) :
				   ServiceResponse.generateServiceResponse(false, "action not found", null);
			   return (ServiceResponse) out;
		}
	
	public ServiceResponse fetchinstagram (Map<String, Object> request) {
		logger.logInfo("executing fetch instagram ");
		String accessToken = (String) request.get("token");
		String userId = (String) request.get("userId");
		String socialUserId = (String) request.get("socialUserId");
		String appId = this.instagramApiId;
		InstagramTemplate instagram = new InstagramTemplate(appId, accessToken);
		PagedMediaList out = instagram.userOperations().getRecentMedia(Long.parseLong(socialUserId));
		Thing parent = thingDao.getOrCreateCollection(userId, PropertyType.instagram);
		for (Media media: out.getList()){
			logger.logInfo("fetched photo: " +media.getId()); 
			boolean isPhotoFetched;
			try {
			isPhotoFetched = thingDao.isPhotoFetched(userId, media.getId());
			logger.logInfo("is photo already uploaded :"+isPhotoFetched);
			if (isPhotoFetched) {
				logger.logInfo("photo already added. skip "+ media.getId());
				continue;
			} 
			Caption caption = media.getCaption();
			if (caption == null) continue;
			Map<String, Image> images = media.getImages();
			String photoUrl = images.get(PropertyType.standardResolution).getUrl();
			// create thing object
			Thing thing = new Thing();
			thing.addProperty(PropertyType.id,media.getId());
			thing.addProperty(PropertyType.photoUrl, photoUrl);
			thing.addProperty(PropertyType.isPhoto, true);
			thing.addProperty(PropertyType.parentId, parent.get_id());
			thing.setUserId(userId);
				// add tags
			List<String> tags  = media.getTags();
			if (tags != null){
				for (String t : tags)
					thing.addUpdateTag(t, TagType.other);
			}
				
				thing.addUpdateTag(PropertyType.instagram, TagType.other);
				thingDao.add(thing);
				parent.addCircularFifoProperty(PropertyType.photos, photoUrl);
				thingDao.update(parent);
			} catch (ServiceException e) {
				logger.logError(e.getMessage(), e);
			}
		}
		return ServiceResponse.generateServiceResponse(true, "success", null);
	}
	public ServiceResponse fetchfacebook (Map<String, Object> request) {
		logger.logInfo("executing facebook instagram ");
		String accessToken = (String) request.get("token");
		String userId = (String) request.get("userId");
		FacebookTemplate facebook = new FacebookTemplate(accessToken);
		MediaOperations mediaOperations = facebook.mediaOperations();
		Thing parent = thingDao.getOrCreateCollection(userId, PropertyType.facebook);
		List<Album> albums = mediaOperations.getAlbums();
		for (Album album : albums){
			logger.logInfo("fetched album: " +album.getName()); 
			List<org.springframework.social.facebook.api.Photo> photos = mediaOperations.getPhotos(album.getId());
			for (org.springframework.social.facebook.api.Photo media : photos){
				logger.logInfo("fetched photo: " +media.getId()); 
				boolean isPhotoFetched;
				try {
				isPhotoFetched = thingDao.isPhotoFetched(userId, media.getId());
				logger.logInfo("is photo already uploaded :"+isPhotoFetched);
				if (isPhotoFetched) continue;
				String photoUrl = media.getSourceImage().getSource();
				// create thing object
				Thing thing = new Thing();
				thing.addProperty(PropertyType.photoId,media.getId());
				thing.addProperty(PropertyType.photoUrl, photoUrl);
				thing.addProperty(PropertyType.isPhoto, true);
				thing.addProperty(PropertyType.parentId, parent.get_id());
				thing.setUserId(userId);
					// add tags
				List<Tag> tags  = media.getTags();
				if (tags != null){
					for (Tag t : media.getTags())
						thing.addUpdateTag(t.getName(), TagType.other);
				}
						
				thing.addUpdateTag(PropertyType.facebook, TagType.other);
					thingDao.add(thing);
					parent.addCircularFifoProperty(PropertyType.photos, photoUrl);
					thingDao.update(parent);
				} catch (ServiceException e) {
					logger.logError(e.getMessage(), e);
				}	
			}
		}
		return ServiceResponse.generateServiceResponse(true, "success", null);
	}

	public ServiceResponse autocomplete (Map<String, Object> request){
		System.out.println("auto completE");
		String userId = request.get("userId").toString();
		String word = request.get("word").toString().toLowerCase();
		if (userId == null) return ServiceResponse.generateServiceResponse(false, "invalid user id", null);
		if (word == null) return ServiceResponse.generateServiceResponse(false, "invalid word", null);
		Long start = redisClient.zrank(userId, word);
		System.out.println("start "+start);
		if (start == null) return ServiceResponse.generateServiceResponse(true , "success",Collections.EMPTY_LIST);
		Set<Tuple> list = redisClient.zrange(userId, start);
		System.out.println(list.size());
		if (list == null) return ServiceResponse.generateServiceResponse(true , "success",Collections.EMPTY_LIST);
		List<String> response = new ArrayList<String>();
		for (Tuple t : list ) {
			String ele = t.getElement();
			if (!ele.contains(word)) {
				System.out.println("ele "+ele +" does not contain "+ word);
				break;
			}
			logger.logInfo("autocomplete : "+ele);
			if (ele.endsWith("*")) {
				ele = ele.substring(0, ele.length() - 1);
				response.add(ele);
			}
		}
		 return ServiceResponse.generateServiceResponse(true , "success",response);
	}
	public ThingDao getThingDao() {
		return thingDao;
	}


	public void setThingDao(ThingDao thingDao) {
		this.thingDao = thingDao;
	}

	public RedisJMSClient getRedisClient() {
		return redisClient;
	}

	public void setRedisClient(RedisJMSClient redisClient) {
		this.redisClient = redisClient;
	}


	
	
	
}
