package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface GPFFinalWithdrawalDAO extends GenericDao {

	public List getFinalWithdrawalToDEOApprover(String lStrGpfAccNo, Long lLngFinalWithdrawalId, Long lLngPostId);

	Boolean requestDataAlreadyExists(String strGpfAccNo);
}
