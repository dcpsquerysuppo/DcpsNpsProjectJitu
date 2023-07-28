package com.tcs.sgv.common.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;


/**
 * BptmCommonServicesDAO This interface contains method declaration to get
 * challan id from code and vice versa, to get Bill Control No from Bill No and
 * vice versa, to get Receipt VO, to get Budget Head Structure VO, to get
 * LookupId from Name, to get Emp Name from user Id, to get Lookup Text and
 * description, to get Bill type, to get bill control no, to get max of
 * reference number, to get Total Gross Amount. It also contains utility methods
 * for checking validity of Previous Bill No, to check validity of Receipt No,
 * to check bill(physical or online). This interface and its methods are
 * implemented in BptmCommonServicesDAOImpl
 * 
 * Date of Creation : 13th July 2007 Author : Hiral Shah
 * 
 * Revision History ===================== Hiral 24-Oct-2007 For making changes
 * for code formating
 * 
 */

public interface BptmCommonServicesDAO {

	public List getBillType(Long langId) throws Exception;

	public List getBillTypeDesc(String lStrBillDesc, Long lLngLangId, Short lShrtActvFlag, Long lDbId) throws Exception;

	public List getBillTypeCode(String lStrBillCode, Long lLngLangId, Short lShrtActvFlag, Long lDbId) throws Exception;

	public Long getBillCtrlNo(Map inputMap);

	public String getBillCtrlNoForOBPM(Map inputMap);

	public String getLookUpText(String lookupName, Long langId) throws Exception;

	public String getUserIdFromPost(String postId, Long langId);

	public List getUserFromPost(String[] postId, String[] levelId, Long langId);

	public long checkReceiptNo(String lStrRcptNo) throws Exception;// , String
																	// lStrBillNo);

	public BigDecimal getTotalGrossAmount(Map mp) throws Exception;

	public String getEmpNameFrmUserId(String lStrUserId, Long lLngLangId) throws Exception;

	public String getLookupIdFromName(String lookupName, Long langId) throws Exception;

	public boolean isPhyBill(Long billNo) throws Exception;

	public Long getSubIdFromBillType(String lStrBillType) throws Exception;

	public String getBillTypeFromSubId(Long lLngSubjectId) throws Exception;

	public List<CmnLookupMst> getAllChildrenByLookUpNameAndLang(String lStrLookupName, Long lLngLangId) throws Exception;

	public List getOtherUserList(Long lLngHierRefId, Long lLngBillNo, Long lLngLevelId, Long lLngLangId, String auditFlg) throws Exception;

	public List getCurrLevelFrmStatus(String lStrCurrStatus, String category) throws Exception;

	public TrnBillMvmnt getLastBillMvmntFrmBillNo(Long lLngBillNo) throws Exception;

	public Long getBillControlNo(Map inputMap) throws Exception;
	// public Map<Long, String> getEmpNamesFrmUserIds(String lStrUserId, Long
	// lLngLangId);
}