package com.tcs.sgv.dcps.dao;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface PostDeletionDAO extends GenericDao{
	
	
	void getDetailsforDeletePostHistory(Long postId,String postType,Long orderid,String remark,Long gLngUserId,Long gLngPostId,java.util.Date gDtCurDate,int len ) throws ParseException;
	List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn,Long designationId,Long BillGroupId);
	List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn);
}
