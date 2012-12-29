package com.service.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.utils.DateUtil;


@Document
public class Thing  {
	
	@Id
	private String _id;
	private String userId;
	private Map <String, Object> properties = new HashMap<String,Object>();
	private Map <String, Map <String, Object>> tags = new HashMap<String, Map <String, Object>>();
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public Map<String, Map<String, Object>> getTags() {
		return tags;
	}
	public void setTags(Map<String, Map<String, Object>> tags) {
		this.tags = tags;
	}
	public void addProperty (String key, Object value){
		if (value == null) return;
		this.properties.put(key, value);
	}
	
	public void addUpdateTag (String tag, String type){
		Map<String, Object> mTags = new HashMap<String, Object>();
		if (tags.containsKey(type)){
			mTags = tags.get(type);
			mTags.put(tag, DateUtil.getUTCDateAndTime());
		} else {
			mTags.put(tag, DateUtil.getUTCDateAndTime());
		}
		tags.put(type, mTags);
	}
	
	public void removeTag (String tag, String type){
		Map<String, Object> m = tags.get(type);
		m.remove(tag);
		tags.put(type, m);
	}
	
	public void addCircularFifoProperty (String key, String value) {
		if (value == null) return;
		CircularFifoBuffer fifo = null;
		if (!this.properties.containsKey(key)) this.properties.put(key, new  CircularFifoBuffer(10));
		fifo = (CircularFifoBuffer) this.properties.get(key);
		if (fifo == null) {
			fifo = new CircularFifoBuffer(10); 
		}
		fifo.add(value);
		properties.put(key, fifo);
	}
	
}
