package com.service.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.NoRepositoryBean;
import com.service.model.PropertyType;
import com.service.model.TagType;
import com.service.model.Thing;
import com.utils.wsutils.ServiceException;

@NoRepositoryBean
public class ThingDaoImpl  implements ThingDao  {

	private MongoTemplate mongoTemplate;
	private SolrServer solrServer; 
	
	

	@Override
	public List<Thing> findThingbyTag(String userId, String id, Object... tags) throws ServiceException, SolrServerException {
		StringBuffer buffer = new StringBuffer();
	    buffer.append("tag:(");
		  for (Object t : tags) {
			  buffer.append(t.toString()).append(" ").append("AND").append(" ");
		  }
		  buffer.delete((buffer.length() -5), (buffer.length()));
		  buffer.append(") AND userId").append(":").append(userId);
	    SolrQuery query = new  SolrQuery().setQuery(buffer.toString()).setFacet(true).setFacetMinCount(1).setFacetLimit(20);
        QueryResponse queryResponse = solrServer.query(query);
	    SolrDocumentList results =  queryResponse.getResults();
	    List<Thing> list = new ArrayList<Thing>();
	    for (SolrDocument doc :  results) {
		    String thingid = doc.get("id").toString();
		    if (thingid != null){
		    	Thing t = this.get(thingid);
		    	if (t != null)
		    		list.add(t);
		    }
		   }
	    return list;
	 }

	
	
		@Override
	public void add(Object object) throws ServiceException {
		mongoTemplate.save(object);
	}

	@Override
	public void delete(Thing thing) {
		mongoTemplate.remove(thing);
	}
	
	@Override
	public void delete(String id) {
		mongoTemplate.remove(
				new Query(Criteria.where("_id").is(id)
						), Thing.class
						);
	}
	
	@Override
	public void deleteAll(String id) {
		mongoTemplate.remove(
				new Query(Criteria.where("userId").is(id)
						), Thing.class
						);
	}
	
	@Override
	public void deleteByPropertyType(String userId, String key, Object value) {
		mongoTemplate.remove(
				new Query(Criteria.where("userId").is(userId).and("properties."+key).is(value)
						), Thing.class
						);
	}
	

	@Override
	public Thing get(String id) throws ServiceException {
		Thing thing = mongoTemplate.findOne(
				new Query(Criteria.where("_id").is(id)
		                ), Thing.class
		          );
		return thing;
	}

	@Override
	public List<Thing> list(String userId,String skip, int limit)  {
		Query q = new Query(Criteria.where("userId").is(userId).and("properties."+PropertyType.isCollection).is(true)).skip(Integer.parseInt(skip)).limit(limit);
        List<Thing> list = mongoTemplate.find(q, Thing.class);
		return list;
	}
	
	
	
	@Override
	public List<Thing> list(String userId, String parentId, String skip, int limit)  {
		Query q = new Query(Criteria.where("userId").is(userId).and("properties."+PropertyType.isPhoto).is(true).and("properties."+PropertyType.parentId).is(parentId)).skip(Integer.parseInt(skip)).limit(limit);
		List<Thing> list = mongoTemplate.find(q, Thing.class);
		return list;
	}
	
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public boolean isPhotoFetched(String userId, String photoId) throws ServiceException {
		Thing thing = mongoTemplate.findOne(
				new Query(Criteria.where("userId").is(userId).and("properties."+PropertyType.photoId).is(photoId)
		                ), Thing.class
		          );
		return (thing != null);
		
	}
	
	@Override
	public List<Thing> listPendingUploads() {
		List<Thing> list = (List<Thing>) mongoTemplate.find(new Query(Criteria.where("uploadStatus").is(0)
                ).limit(100), Thing.class);
		for (Thing p: list) { 
			//p.setUploadStatus(1); 
			mongoTemplate.save(p);
			}
		return list;
	}
	
	@Override
	public long totalPendingUpload() {
		long list = mongoTemplate.count(
				new Query(Criteria.where("uploadStatus").is(0)
		                ), Thing.class
		          );
		return list;
	}


	@Override
	public List<Thing> listAboutToExpire(Long time) {
		List<Thing> list = mongoTemplate.find(
				new Query(Criteria.where("expire").lte(time)
		                ).limit(10), Thing.class
		          );
		return list;
	}
	
	
	
	@Override
	public void update(Thing photo) throws ServiceException {
		mongoTemplate.save(photo);
	}



	@Override
	public Thing getThingByPropertyType(String userId, String key, Object value) {
		Thing thing = mongoTemplate.findOne(
				new Query(Criteria.where("properties."+key).is(value).and("userId").is(userId)
		                ), Thing.class
		          );
		return thing;
	}

	

	@Override
	public Thing getThingByTag(String userId, String tagType, String tagName) {
		Thing thing = mongoTemplate.findOne(
				new Query(Criteria.where("tags.name").is(tagName).and("userId").is(userId)
		                ), Thing.class
		          );
		return thing;
	
	}
	
	@Override
	public Thing getCollection(String userId, String name) {
		Thing thing = mongoTemplate.findOne(
				new Query(Criteria.where("properties."+PropertyType.isCollection).is(true).and("tags.title."+name).exists(true).and("userId").is(userId)
		                ), Thing.class
		          );
		return thing;
	}


	@Override
	public Thing getOrCreateCollection(String userId, String name) {
		Thing thing = mongoTemplate.findOne(
				new Query(Criteria.where("properties."+PropertyType.isCollection).is(true).and("tags.title."+name).exists(true).and("userId").is(userId)
		                ), Thing.class
		          );
		if (thing == null) thing = buildCollection(userId, name);
		return thing;
	}

	public Thing buildCollection (String userId, String name){
		Thing thing = new Thing();
		thing.setUserId(userId);
		thing.addProperty(PropertyType.isCollection, true);
		thing.addUpdateTag(name, TagType.title);
		mongoTemplate.save(thing);
		return thing;
	}



	public SolrServer getSolrServer() {
		return solrServer;
	}



	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}


	
	


}
