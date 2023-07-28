package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface FeedbackDAO extends GenericDao{
	
	public List getFeedbacksForSRKA() throws Exception ;
	
}
