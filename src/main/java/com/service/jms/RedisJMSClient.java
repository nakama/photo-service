package com.service.jms;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


public class RedisJMSClient {

	   private RedisTemplate<String, String> template;	
	 
	   public void send(String channel, Object message) {
		  template.convertAndSend(channel, message);
	   }
	 
	   public void addValue (String key, String value){
		   template.opsForValue().set(key, value);
	   }
	   public String getValue(String key) {
	      return template.opsForValue().get(key);
	   }
	 
	   public void delete(String key) {
	      template.opsForValue().getOperations().delete(key);
	   }

	   public RedisTemplate<String, String> getTemplate() {
		return template;
	   }

	   public void setTemplate(RedisTemplate<String, String> template) {
		this.template = template;
		StringRedisSerializer s = new StringRedisSerializer();
		template.setDefaultSerializer(s);
		template.setHashKeySerializer(s);
		template.setKeySerializer(s);
		template.setStringSerializer(s);
		template.setValueSerializer(s);
	     
	   }
	   
	   
	   
}
