package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;


public interface ConsolidateDcpsR2BroadSheetDAO extends GenericDao{

	List getAcMainDetails() throws Exception;

	List getAllTreasuriesForConsolidateEnteries(Long yearId, Long acMain);



}
