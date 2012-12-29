package com.service.dao;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import com.service.model.Thing;
import com.utils.wsutils.ServiceException;

public interface ThingDao {

	public void add (Object object) throws ServiceException;
	public void update (Thing photo) throws ServiceException;
	public void delete (String id) throws ServiceException;
	public void deleteByPropertyType (String userId, String key, Object value);
	public void delete (Thing thing) throws ServiceException;
	public boolean isPhotoFetched (String userId, String name) throws ServiceException;
	public List<Thing> list (String userId, String skip, int limit);
	public Thing get (String id) throws ServiceException;
	public List<Thing> list(String userId, String parentId, String skip, int limit);
	public List<Thing> listPendingUploads ();
	public long totalPendingUpload();
	public List<Thing> listAboutToExpire(Long time);
	public void deleteAll(String id);
	public Thing getThingByPropertyType (String userId, String key, Object value);
	public Thing getThingByTag (String userId, String tagType, String tagName);
	public Thing getCollection (String userId, String name);
	public Thing getOrCreateCollection (String userId, String name);
	public List<Thing> findThingbyTag (String userId, String id, Object... object) throws ServiceException, SolrServerException ;
}
