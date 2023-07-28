package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.common.valueobject.MstEdp;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;


/**
 * TrnPensionRqstHdr specific Service Interface
 * 
 * @author Rupsa Mukherjee
 * @version 1.0
 */
public interface TrnPensionRqstHdrDAO extends GenericDao<TrnPensionRqstHdr, Long> {

	Long getPensionRqstHdrIdByPnsnrCode(String pensionerCode, String lStrLocCode) throws Exception;

	int getLastPaidMonthForPnsnrCode(String pensionerCode) throws Exception;

	List getPksForTrnPensionRqstHdr(String lStrStatus, String lStrPpoNo, String locCode) throws Exception;

	String getPPONo(String lStrPensionerCode, String locCode) throws Exception;

	TrnPensionRqstHdr getTrnPensionRqstHdrVO(long lBDPenReqId, String lStrPenCode, String locCode) throws Exception;

	String validatePPONumberForScheme(String PPONO, String locode) throws Exception;

	String validatePPONumberForBank(String PPONO, String branchCode, String locCode) throws Exception;

	// Map getPaymentDetails(String PPONO,String locCode) throws Exception;

	List getPensionRqstHdrCodeId(String lStrPPONo, String lStrStatus, String locCode) throws Exception;

	TrnPensionRqstHdr getTrnPensionRqstHdrDtlsFromPPONo(String lStrPPONO, String lStrStatus, String lStrCaseStatus, String locCode) throws Exception;

	TrnPensionRqstHdr getTrnPensionRqstHdrDtlsFromPnsnerCode(String lStrPnsrCode, String lStrStatus, String lStrCaseStatus, String locCode) throws Exception;

	TrnPensionRqstHdr getTrnPensionRqstHdrVO(String lStrPPONo, String locCode) throws Exception;

	MstEdp getMstEdpVO(String lStrEdpCode, Long gLngLangId, Integer lFinYr) throws Exception;

	Map getActualPaymentDetails(long lLngPensionReqId, String lStrPensionerCode) throws Exception;

	BigDecimal getHeadCode(String PPONO, String lStrstatus, String locCode) throws Exception;

	List getDtlsForCVPDCRGBill(String status, String caseStatus, String RecvryFlag, String ppoNo, Long lLangID, String locCode) throws Exception;

	Date getLastPaidDateByPensionerCodeAndCaseStatus(String lStrPPONO, String lStrStatus) throws Exception;

	void updateTrnPensionRqstHdrSeenFlag(String lStrPensionerCode, String seenFlag, Long gLngPostId, Long gLngUserId, Date gDate, String lLocCode) throws Exception;

	void updateTrnPensionRqstHdrLastPaidDate(String lStrPensionerCode, Date lDateLastPaidDate, Long gLngPostId, Long gLngUserId, Date gDate, String LStrLocCode) throws Exception;

	int getFamilyMemberCountByPensinoerCode(String lStrPensionerCode) throws Exception;

	List getInwdRegisterVitoDtls(String lStrPenCdArr, String lStrLocCode, Long lLngLangId) throws Exception;

	public String getPensionerName(String lStrPPONo) throws Exception;
}
