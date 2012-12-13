package com.service.dao;

import java.math.BigInteger;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.NoRepositoryBean;
import com.service.models.Photo;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;

import edu.emory.mathcs.backport.java.util.Collections;

import org.springframework.data.solr.repository.support.SimpleSolrRepository;

@NoRepositoryBean
public class PhotoDaoImpl extends SimpleSolrRepository<Photo>  implements PhotoDao  {

	private MongoTemplate mongoTemplate;
	private MyLogger logger = new MyLogger(PhotoDaoImpl.class);
	
	@Override
	public Page<Photo> findByTags(String[] tags) {
		SolrQuery query = new SolrQuery();
	    return null;
		//query.setQuery( "tags" ).;
	   // query.addSortField( "tags", SolrQuery.ORDER.asc );
	}
	
	public void indexPhotoTags (Photo photo){
		return;/*
		SolrServer server = super.getSolrOperations().getSolrServer();
		try {
			server.addBean(photo);
			server.commit();
		} catch (IOException e) {
			logger.logError(e.getMessage(), e);
		} catch (SolrServerException e) {
			logger.logError(e.getMessage(), e);
		}*/
	}
	
	@Override
	public void add(Object object) throws ServiceException {
		mongoTemplate.save(object);
		indexPhotoTags((Photo) object);
	}

	
	@Override
	public void delete(BigInteger id) throws ServiceException {
		mongoTemplate.remove(
				new Query(Criteria.where("id").is(id)
						), Photo.class
						);
	}

	@Override
	public Photo get(BigInteger id) throws ServiceException {
		Photo photo = mongoTemplate.findOne(
				new Query(Criteria.where("id").is(id)
		                ), Photo.class
		          );
		return photo;
	}

	@Override
	public List<Photo> list(BigInteger userId, BigInteger index, int limit)  {
		List<Photo> list = mongoTemplate.find(
				new Query(Criteria.where("userId").is(userId).and("id").gt(index)
		                ).limit(limit), Photo.class,"photo"
		          );
		return list;
	}
	
	@Override
	public List<Photo> find(BigInteger userId, String[] tags) {
		List<Photo> list = mongoTemplate.find(
				new Query(Criteria.where("tags").in(tags).and("userId").is(userId).size(100)
		                ).limit(100), Photo.class,"photo"
		          );
		return list;
	}
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public boolean isPhotoFetched(BigInteger userId, String photoId) throws ServiceException {
		List<Photo> list = mongoTemplate.find(
				new Query(Criteria.where("userId").is(userId).and("photoId").is(photoId)
		                ), Photo.class,"photo"
		          );
		return (!list.isEmpty());
		
	}
	
	@Override
	public List<Photo> listPendingUploads() {
		List<Photo> list = (List<Photo>) mongoTemplate.find(new Query(Criteria.where("uploadStatus").is(0)
                ).limit(100), Photo.class);
		for (Photo p: list) { 
			p.setUploadStatus(1); 
			mongoTemplate.save(p);
			}
		return list;
	}
	
	@Override
	public long totalPendingUpload() {
		long list = mongoTemplate.count(
				new Query(Criteria.where("uploadStatus").is(0)
		                ), Photo.class
		          );
		return list;
	}


	@Override
	public List<Photo> listAboutToExpire(Long time) {
		List<Photo> list = mongoTemplate.find(
				new Query(Criteria.where("expire").lte(time)
		                ).limit(10), Photo.class,"photo"
		          );
		return list;
	}
	
	@Override
	public void update(Photo photo) throws ServiceException {
		mongoTemplate.save(photo);
		indexPhotoTags(photo);
		
	}




}
