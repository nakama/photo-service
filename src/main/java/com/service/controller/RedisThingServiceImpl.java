package com.service.controller;

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
import com.service.dao.ThingDao;
import com.service.model.PropertyType;
import com.service.model.TagType;
import com.service.model.Thing;
import com.utils.StringUtil;
import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;

public class RedisThingServiceImpl extends ThingCRUD implements ActionHandler {

	
	public Object fetch (Map<String, Object> request) {	
		try {
			logger.logInfo("executing fetch action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id, PropertyType.services);
			String userId = (String) userMap.get(PropertyType.id);
			String accId = new String(userId);
			Map services = (Map) userMap.get(PropertyType.services);
			Map service = null;
		
		if (services.containsKey(PropertyType.instagram)){
			service = (Map) services.get(PropertyType.instagram);
			StringUtil.validateField(service, PropertyType.auth_token, PropertyType.id);
			String token = (String) service.get(PropertyType.auth_token);
			String id = (String) service.get(PropertyType.id);
			StringUtil.validateNull(token,PropertyType.auth_token );
			StringUtil.validateNull(id,PropertyType.id );
			fetchInstagram(this.instagramApiId, accId, token, Long.parseLong(id));
		}
		else if (services.containsKey(PropertyType.facebook)){
			service = (Map) services.get(PropertyType.facebook);
			StringUtil.validateField(service, PropertyType.auth_token);
			String token = (String) service.get(PropertyType.auth_token);
			StringUtil.validateNull(token,PropertyType.auth_token );
			fetchFacebook(accId, token);
		}
		request.put("skip", "0");
		request.put("limit", "100");
		return list(request, true);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
		
	}
	
	
	public void fetchInstagram (String appId, String userId, String accessToken, Long socialUserId) throws ServiceException {
				logger.logInfo("executing fetch instagram ");
				InstagramTemplate instagram = new InstagramTemplate(appId, accessToken);
				PagedMediaList out = instagram.userOperations().getRecentMedia(socialUserId);
				Thing parent = thingDao.getOrCreateCollection(userId, PropertyType.instagram);
				for (Media media: out.getList()){
					logger.logInfo("fetched photo: " +media.getId()); 
					boolean isPhotoFetched = thingDao.isPhotoFetched(userId, media.getId());
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
					}
	}
	
	public void fetchFacebook(String userId, String accessToken) throws ServiceException {
		logger.logInfo("executing facebook instagram ");
		FacebookTemplate facebook = new FacebookTemplate(accessToken);
		MediaOperations mediaOperations = facebook.mediaOperations();
		Thing parent = thingDao.getOrCreateCollection(userId, PropertyType.facebook);
		List<Album> albums = mediaOperations.getAlbums();
		for (Album album : albums){
			logger.logInfo("fetched album: " +album.getName()); 
			List<org.springframework.social.facebook.api.Photo> photos = mediaOperations.getPhotos(album.getId());
			for (org.springframework.social.facebook.api.Photo media : photos){
				logger.logInfo("fetched photo: " +media.getId()); 
				boolean isPhotoFetched = thingDao.isPhotoFetched(userId, media.getId());
				logger.logInfo("is photo already uploaded :"+isPhotoFetched);
				if (isPhotoFetched) continue;
				String photoUrl = media.getSourceImage().getSource();
				// create thing object
				Thing thing = new Thing();
				thing.addProperty(PropertyType.photoId,media.getId());
				thing.addProperty(PropertyType.photoUrl, photoUrl);
				thing.addProperty(PropertyType.isPhoto, photoUrl);
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
					
			}
		}
	}
	
	@Override
	public ServiceResponse executeAction(String thing, String action, Map<String, Object> request) throws ServiceException {
		   boolean isCollection = thing.equals("collection");
		   StringUtil.validateNull(request, "request");
			   Object out =  action.equals("add") ? add(request, isCollection) : 
			   action.equals("delete") ? delete(request, isCollection) :
			   action.equals("deleteAll") ? deleteAll(request, isCollection) :
			   action.equals("update") ? update(request, isCollection ) :
			   action.equals("list") ? list(request,isCollection) :
			   action.equals("get") ? get(request, isCollection) :
			   action.equals("addtags") ? addTags(request, isCollection) :
			   action.equals("removetags") ? removeTags(request, isCollection) :
			   action.equals("search") ? search(request) :
			   action.equals("fetch") ? fetch(request) :  "action not found";
			   return (ServiceResponse) out;
	}

	


	public ThingDao getThingDao() {
		return thingDao;
	}

	public void setThingDao(ThingDao thingDao) {
		this.thingDao = thingDao;
	}

	public String getInstagramApiId() {
		return instagramApiId;
	}

	public void setInstagramApiId(String instagramApiId) {
		this.instagramApiId = instagramApiId;
	}
		
	
}
