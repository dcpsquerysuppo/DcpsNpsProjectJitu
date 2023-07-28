package com.tcs.sgv.lna.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface LNAComputerAdvanceDAO extends GenericDao {

	public List getComputerAdvance(String lStrSevaarthId, Long lLngRequestType);

	public List getComAdvanceToDEOApprover(Long lLngComAdvnId);

	public Boolean requestDataAlreadyExists(String lStrSevaarthId);

	public Boolean requestPendingStatus(String lStrSevaarthId);
	public List<Long> getAllHierarchyRefIdsForLocation(Long LocationCode);
	public Boolean checkEntryInWfHierachyPostMpg(Long lLongHierarchyRefId,Long lLongPostId);
	
	public void insertWfHierachyPostMpg(Long lLongHierarchySeqId ,Long lLongHierarchyRefId ,Long lLongPostId,
			Long lLongCreatedByUserId,Date gDtCurDate,Long LocId ) throws Exception;
}
