package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;


public interface MstPensionerHdrDAO {

	String getName(String lStrPensionerCode) throws Exception;

	List getNameAndDesignation(String lStrPensionerCode, String strDate, String lStrLocCode) throws Exception;

	String getPANNo(String lStrPensionerCode) throws Exception;

	Long getPensionerIdfromPensnrCode(String lStrPnsnrCode, String lStrCaseStatus) throws Exception;

	List<MstPensionerHdr> getMstPensionerHdrDiff(String lStrPnsnrCode) throws Exception;

	MstPensionerHdr getMstPensionerHdrDtls(String lStrPensionerCode) throws Exception;

	// String getDesigName(long lStrDesignation) throws Exception;
	// List getMRRelatedInfoSaved(String lStrPPONO, String lStrCaseStatus,
	// String gStrLocationCode, String lStrMedRemId, String Scheme) throws
	// Exception;

	// String getScheme(String lStrPPONo) throws Exception;
	List getITRelatedInfo(String lStrPPONO, String lStrCaseStatus, String lStrLocationCode, String lStrLangId) throws Exception;

	BigDecimal getITGrossIncome(String lStrPPONO, int lIntFinYr, String locCode) throws Exception;

	String getFamilyAliveInfo(String lStrPnsnrCode) throws Exception;

	MstPensionerHdr getMstPnsnrHdrWithStatus(String lStrPensionerCode, String lStrCaseStatus) throws Exception;

	String getApprovedScheme(String lStrPPONo, String lStrLocCode) throws Exception;

	void updateDeathDateByPensinoerCodeAndStatus(String lStrPensinoerCode, Date DeathDate, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception;

	void updateFMDeathDateByPensinoerCodeAndStatus(String lStrPensinoerCode, Date DeathDate, String lStrIsUpdate, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception;

	public String getPensionerName(String lStrPnsnCode) throws Exception;
}
