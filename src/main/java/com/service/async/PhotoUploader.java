package com.service.async;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.service.dao.PhotoDao;
import com.service.jms.RedisJMSClient;
import com.service.models.Photo;
import com.utils.logging.MyLogger;
import com.utils.wsutils.ServiceException;

public class PhotoUploader {

	
	private AWSCredentials awsCredentials ;
	private AmazonS3Client s3Client;
	private PhotoDao photoDao;
	private MyLogger logger = new MyLogger(PhotoUploader.class);
	public RedisJMSClient jmsClient;
	
	public PhotoUploader (){
	 awsCredentials = new BasicAWSCredentials("AKIAIMSLOTXTVYAZM4IQ", "7cAU84rU+A3tOHZQdUL47VFmfEg226y3uSCoBd+M");
	 s3Client = new AmazonS3Client(awsCredentials);
	}
	
	public synchronized void upload (){
		try {
				List<Photo> list = photoDao.listPendingUploads();
				for (Photo photo : list){
				String id = photo.getPid().toString();
				String urlImage = photo.getUrl();
				String bucketName = "nakama-user-id-"+ photo.getUserId().toString();
				URL url = new URL(urlImage);
				String filename = basename(urlImage);
				BufferedImage image = ImageIO.read(url);
				File imageFile = new File(filename);
				ImageIO.write(image, "jpg", imageFile);
				logger.logInfo("creating bucket name. "+bucketName);
				s3Client.createBucket( bucketName );
				logger.logInfo("transfering file");
				PutObjectRequest por = new PutObjectRequest(bucketName, id, imageFile );
				s3Client.putObject( por );
				Date expiration = new Date( System.currentTimeMillis() + 3600000 );
				String newUrl = generageUrl(bucketName, id, expiration);
				photo.setUrl(newUrl);
				photo.setUploadStatus(2);
				photo.setBucket(bucketName);
				photo.setExpire(expiration.getTime());
				photoDao.update(photo);
				imageFile.delete();
				}
			//	callback.send(channel, msg);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	

	public String buildProgress (BigInteger userId, long pending, String service){
		try {
			JSONObject progress = new JSONObject();
			progress.put("processing", pending);
			progress.put("userId", userId);
			progress.put("service", service);
			JSONObject object = new JSONObject();
			object.put("fetch", progress);
		return object.toString();
		} catch (JSONException e) {
			return null;
		}
	}
	public synchronized void update() {
		try {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		calendar.getTime();
		List<Photo> photoList = photoDao.listAboutToExpire(calendar.getTimeInMillis());
		logger.logInfo("updating "+photoList.size() + " about to expire photos");
		for (Photo photo : photoList){
			logger.logInfo("updating s3 photo url id. "+photo.getPid());
			Date expiration = new Date( System.currentTimeMillis() + 3600000 );	
			String bucketName = photo.getBucket();
			String id = photo.getPid().toString();
			String newUrl = generageUrl(bucketName, id, expiration);
			photo.setUrl(newUrl);
			photo.setExpire(expiration.getTime());
			photoDao.update(photo);
		}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	
	public String generageUrl(String bucketName, String key, Date expiration){
		GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest( bucketName,key );
		// Added an hour's worth of milliseconds to the current time.
		urlRequest.setExpiration(expiration );
		URL url = s3Client.generatePresignedUrl( urlRequest );
		return url.toString();
	}

	private String basename(String path) {
	    String[] pathparts = path.split("/");
	    String filename = pathparts[pathparts.length - 1];
	    return filename;
	}

	public PhotoDao getPhotoDao() {
		return photoDao;
	}

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public RedisJMSClient getJmsClient() {
		return jmsClient;
	}

	public void setJmsClient(RedisJMSClient jmsClient) {
		this.jmsClient = jmsClient;
	}

	
	
	
	
   
}
