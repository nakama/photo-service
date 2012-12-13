package com.service.dao;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;

import com.service.models.Photo;
import com.utils.wsutils.ServiceException;

public interface PhotoDao {

	public void add (Object object) throws ServiceException;
	public void update (Photo photo) throws ServiceException;
	public void delete (BigInteger id) throws ServiceException;
	public boolean isPhotoFetched (BigInteger userId, String name) throws ServiceException;
	public Photo get (BigInteger id) throws ServiceException;
	public List<Photo> list(BigInteger userId, BigInteger index, int limit);
	public List<Photo> listPendingUploads ();
	public long totalPendingUpload();
	public List<Photo> listAboutToExpire(Long time);
	public List<Photo> find (BigInteger userId, String[] tags);
	public Page<Photo> findByTags(String[] tags);
	
}
