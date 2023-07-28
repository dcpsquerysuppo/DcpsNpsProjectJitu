package com.tcs.sgv.dcps.dao;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface PostConversionDAO extends GenericDao{

	void getDetailsforPostHistory(Long postId,String postType,String newPost,String endDate,Long orderid,Long gLngUserId,Long gLngPostId,java.util.Date gDtCurDate,int len ) throws ParseException;
	List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn,Long designationId,Long BillGroupId,Long postTypeid);
	List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn,Long postTypeid);
}
