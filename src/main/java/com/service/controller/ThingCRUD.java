package com.service.controller;

import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrServerException;
import com.service.dao.ThingDao;
import com.service.model.PropertyType;
import com.service.model.TagType;
import com.service.model.Thing;
import com.utils.StringUtil;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;
import com.utils.wsutils.ServiceResponse;

public abstract class ThingCRUD {

	protected ThingDao thingDao;
	protected MyLogger logger = new MyLogger(RedisThingServiceImpl.class);
	protected String instagramApiId;

	
	public Thing getOrCreateUploadCollection (String userId) throws ServiceException {
		Thing thing = thingDao.getCollection(userId, PropertyType.uploads);
		if (thing == null) {
			thing = new Thing();
			thing.addProperty(PropertyType.isCollection, true);
			thing.addUpdateTag(PropertyType.uploads, TagType.title);
			thing.setUserId(userId);
			thingDao.add(thing);
		}
		return thing;
	}
	public Object add (Map<String, Object> request, boolean isCollection) {
		try {
			if (isCollection) return addCollection(request);
			logger.logInfo("executing add action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user, PropertyType.thing);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id);
			Map thingMap = (Map) request.get(PropertyType.thing);
			StringUtil.validateField(thingMap, PropertyType.properties);
			String userId = (String) userMap.get(PropertyType.id);
			Map<String,Object> properties = (Map<String, Object>) thingMap.get(PropertyType.properties);
			StringUtil.validateField(properties, PropertyType.photoUrl);
			String photoUrl = (String) properties.get(PropertyType.photoUrl);
			String parentId = (String) properties.get(PropertyType.parentId);
			if (parentId == null)  parentId = this.getOrCreateUploadCollection(userId).get_id();
			List<Map<String,Object>> tags =  (List<Map<String, Object>>) thingMap.get(PropertyType.tags);
			StringUtil.validateNull(tags, PropertyType.tags);
			Thing thing = new Thing();
			thing.setProperties(properties);
			thing.addProperty(PropertyType.photoUrl, photoUrl);
			for (Map<String,Object> t : tags){
				String type = (String) t.get(PropertyType.type);
				String name = (String) t.get(PropertyType.name);
				thing.addUpdateTag(name, type);
			}
			thing.addProperty(PropertyType.parentId, parentId);
			thing.addProperty(PropertyType.isPhoto, true);
			thing.setUserId(userId);
		    thingDao.add(thing);
		   return ServiceResponse.generateSuccessServiceResponse(thing);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object addCollection (Map<String, Object> request) {
		try {
			logger.logInfo("executing add action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user, PropertyType.thing);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id);
			Map thingMap = (Map) request.get(PropertyType.thing);
			String userId = (String) userMap.get(PropertyType.id);
			List<Map<String,Object>> tags =  (List<Map<String, Object>>) thingMap.get(PropertyType.tags);
			StringUtil.validateNull(tags, PropertyType.tags);
			Thing thing = new Thing();
			for (Map<String,Object> t : tags){
				String type = (String) t.get(PropertyType.type);
				String name = (String) t.get(PropertyType.name);
				thing.addUpdateTag(name, type);
			}
			thing.addProperty(PropertyType.isCollection, true);
			thing.setUserId(userId);
		    thingDao.add(thing);
		   return ServiceResponse.generateSuccessServiceResponse(thing);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object update (Map<String, Object> request, boolean isCollection) {
		try {
			logger.logInfo("executing add action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user, PropertyType.thing);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id);
			Map thingMap = (Map) request.get(PropertyType.thing);
			String thingId = (String) userMap.get(PropertyType.id);
			StringUtil.validateField(thingMap, PropertyType.id);
			Thing thing = thingDao.get(thingId);
			StringUtil.validateNull(thing, PropertyType.id);
			Map<String,Object> properties = (Map<String, Object>) thingMap.get(PropertyType.properties);
			if (properties != null){
				for (Map.Entry<String, Object> m : properties.entrySet()){
					thing.addProperty(m.getKey(), m.getValue());
				}
			}
			List<Map<String,Object>> tags =  (List<Map<String, Object>>) thingMap.get(PropertyType.tags);
			for (Map<String,Object> t : tags){
				String type = (String) t.get(PropertyType.type);
				String name = (String) t.get(PropertyType.name);
				thing.addUpdateTag(name, type);
			}
			thingDao.update(thing);
			return ServiceResponse.generateSuccessServiceResponse(thing);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object addTags (Map<String, Object> request, boolean isCollection) {
		try {
			logger.logInfo("executing add action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user, PropertyType.thing);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id);
			Map thingMap = (Map) request.get(PropertyType.thing);
			String thingId = (String) thingMap.get(PropertyType.id);
			StringUtil.validateField(thingMap, PropertyType.id);
			Thing thing = thingDao.get(thingId);
			StringUtil.validateNull(thing, PropertyType.id);
			List<Map<String,Object>> tags =  (List<Map<String, Object>>) thingMap.get(PropertyType.tags);
			for (Map<String,Object> t : tags){
				String type = (String) t.get(PropertyType.type);
				String name = (String) t.get(PropertyType.name);
				thing.addUpdateTag(name, type);
			}
			thingDao.update(thing);
			return ServiceResponse.generateSuccessServiceResponse(thing);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object removeTags (Map<String, Object> request, boolean isCollection) {
		try {
			logger.logInfo("executing add action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user, PropertyType.thing);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id);
			Map thingMap = (Map) request.get(PropertyType.thing);
			String thingId = (String) thingMap.get(PropertyType.id);
			StringUtil.validateField(thingMap, PropertyType.id);
			Thing thing = thingDao.get(thingId);
			StringUtil.validateNull(thing, PropertyType.id);
			List<Map<String,Object>> tags =  (List<Map<String, Object>>) thingMap.get(PropertyType.tags);
			for (Map<String,Object> t : tags){
				String type = (String) t.get(PropertyType.type);
				String name = (String) t.get(PropertyType.name);
				thing.removeTag(name, type);
			}
			thingDao.update(thing);
			return ServiceResponse.generateSuccessServiceResponse(thing);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object delete (Map<String, Object> request, boolean isCollection) {
		try {
			logger.logInfo("executing delete action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request,PropertyType.thing);
			Map thingMap = (Map) request.get(PropertyType.thing);
			StringUtil.validateField(thingMap, PropertyType.id);
			String thingId = (String) thingMap.get(PropertyType.id);
			Thing thing = thingDao.get(thingId);
			StringUtil.validateNull(thing, "thing");
			thingDao.delete(thing);
			if (isCollection) //  delete photos in the album
				thingDao.deleteByPropertyType(thing.getUserId(), PropertyType.parentId, thing.get_id());
			return ServiceResponse.generateSuccessServiceResponse(thing);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} 
	}
	
	public Object deleteAll (Map<String, Object> request, boolean isCollection) {
		try {
			logger.logInfo("executing delete all action");
			StringUtil.validateNull(request, PropertyType.request);
			Map userMap = (Map) request.get(PropertyType.user);
			String idStr = (String) userMap.get(PropertyType.id);
			logger.logInfo("deleting all things for user, "+idStr);
			StringUtil.validateNull(idStr, PropertyType.id);
			thingDao.deleteAll(idStr);
			return ServiceResponse.generateSuccessServiceResponse(null);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} 
	}
	
	public Object get (Map<String, Object> request, boolean isCollection) {
		try {
			logger.logInfo("executing get action");
			StringUtil.validateNull(request, PropertyType.request);
			Map thingMap = (Map) request.get(PropertyType.thing);
			StringUtil.validateField(thingMap, PropertyType.id);
			String thingId = (String) thingMap.get(PropertyType.id);
			Thing thing = thingDao.get(thingId);
			StringUtil.validateNull(thing, "thing");
			return ServiceResponse.generateSuccessServiceResponse(thing);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}
	
	public Object list (Map<String, Object> request, boolean isCollection) {
		try {
			logger.logInfo("executing list action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id);
			String userId = (String) userMap.get(PropertyType.id);
			StringUtil.validateField(request, PropertyType.skip, PropertyType.limit);
			String skip= (String) request.get(PropertyType.skip);
			String limitStr = (String) request.get(PropertyType.limit);
			Map collection = (Map) request.get(PropertyType.parentId);
			String collectionId  = null;
			if (collection != null)
				collectionId = (String)collection.get(PropertyType.id);;
			int limit = Integer.parseInt(limitStr);
			if (limit > 100) limit  = 100;
			List<Thing> list = null;
			if (collectionId == null) {
				logger.logInfo("list collections");
				list = thingDao.list(userId, skip , limit);
			}
			else {
				logger.logInfo("list photos");
				list = thingDao.list(userId, collectionId, skip, limit);
			}
			
			if (list.isEmpty()) return ServiceResponse.generateServiceResponse(false, "nothing found", null);
			return ServiceResponse.generateSuccessServiceResponse(list);
		} catch (ServiceException e) {
			e.printStackTrace();
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} catch (ClassCastException e){
			e.printStackTrace();
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} catch (NumberFormatException e){
			e.printStackTrace();
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
	}

	
	public Object search (Map<String, Object> request) {
		try {
			logger.logInfo("executing search action");
			StringUtil.validateNull(request, PropertyType.request);
			StringUtil.validateField(request, PropertyType.user, PropertyType.thing);
			Map userMap = (Map) request.get(PropertyType.user);
			StringUtil.validateField(userMap, PropertyType.id);
			String userId = (String) userMap.get(PropertyType.id);
			String id = new String(userId);
			Map thingMap = (Map) request.get(PropertyType.thing);
			List<String> tags =  (List<String>) thingMap.get(PropertyType.tags);
			if (tags == null) return ServiceResponse.generateServiceResponse(false, "invalid tags", null);
			List<Thing> list = thingDao.findThingbyTag(userId, id, tags.toArray());
			if (list.isEmpty()) return ServiceResponse.generateServiceResponse(false, "nothing found", null);
			return ServiceResponse.generateSuccessServiceResponse(list);
		} catch (ServiceException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} catch (ClassCastException e){
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} catch (NumberFormatException e){
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		} catch (SolrServerException e) {
			return ServiceResponse.generateServiceResponse(false, e.getMessage(), null);
		}
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
