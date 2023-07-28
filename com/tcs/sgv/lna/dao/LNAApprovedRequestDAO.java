package com.tcs.sgv.lna.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface LNAApprovedRequestDAO extends GenericDao {

	public List getLNAApprovedRequestList(String lStrLocCode,String lStrPostId,String lStrUserType);

}
