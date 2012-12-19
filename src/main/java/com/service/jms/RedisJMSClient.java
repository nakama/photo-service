package com.service.jms;

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;


public class RedisJMSClient implements InitializingBean  {
	
	
	
	   private RedisTemplate<String, String> template;	
	   private Jedis jedis;
	
	   public void zadd (String key, String value){
		   jedis.zadd(key, 0, value);
	   }
	   
	   public Set<Tuple> zrange (String key , Long start){
		   return jedis.zrangeWithScores(key, start, 100);
	   }
	   
	   public Long zrank (String key, String  value){
		  return  jedis.zrank(key, value);
	   }
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

	@Override
	public void afterPropertiesSet() throws Exception {
		jedis.connect();
		
	}

	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	
	
	   
	   
	   
}
