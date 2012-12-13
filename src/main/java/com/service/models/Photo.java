package com.service.models;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.service.solr.SearchablePhoto;

@Document
public class Photo implements SearchablePhoto {
	
	@Id
	private BigInteger pid;
	private BigInteger userId;
	private String name;
	@Field(TAG_FIELD)
	private List<String> tags;
	private List<Comment> comments;
	private Map<String,String> att;
	private String url;
	private String bucket;
	private Long expire;
	private int uploadStatus; // 0 to be uploaded, 1 in progress, 2 complete, 3 failed
	
	private String photoId;
	public String getPhotoId() {
		return photoId;
	}


	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}


	private String source;
	private Long lastUpdated;
	private Long dateCreated;
	private Date systemTime;
	
	
	
	public BigInteger getPid() {
		return pid;
	}


	public void setPid(BigInteger pid) {
		this.pid = pid;
	}


	public BigInteger getUserId() {
		return userId;
	}


	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}


	public List<String> getTags() {
		return tags;
	}


	public void setTags(List<String> tags) {
		this.tags = tags;
	}


	public Map<String, String> getAtt() {
		return att;
	}


	public void setAtt(Map<String, String> att) {
		this.att = att;
	}


	public Long getLastUpdated() {
		return lastUpdated;
	}


	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


	public Long getDateCreated() {
		return dateCreated;
	}


	public void setDateCreated(Long dateCreated) {
		this.dateCreated = dateCreated;
	}


	public Date getSystemTime() {
		return systemTime;
	}


	public void setSystemTime(Date systemTime) {
		this.systemTime = systemTime;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

	



	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<Comment> getComments() {
		return comments;
	}


	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}


	public String getBucket() {
		return bucket;
	}


	public void setBucket(String bucket) {
		this.bucket = bucket;
	}


	public Long getExpire() {
		return expire;
	}


	public void setExpire(Long expire) {
		this.expire = expire;
	}


	public int getUploadStatus() {
		return uploadStatus;
	}


	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}


	
	
}
