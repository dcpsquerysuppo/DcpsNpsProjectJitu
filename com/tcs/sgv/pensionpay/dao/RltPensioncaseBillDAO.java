package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;


public interface RltPensioncaseBillDAO extends GenericDao<RltPensioncaseBill, Long> {

	List getPKForRltPensioncaseBill(String lStrPPONo, String lStrBillType, String lStrStatus, String lStrLocCode) throws Exception;

	void updateBillStatusByPPONoAndStatusAndBillType(String lStrPPONo, String lStrStatus, List lBillType, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception;

	ArrayList getPKPnsnCseBlbypaymode(String lstrPPONo, String lStrLocCode) throws Exception;

	void deleteCVPDCRGRowsforUpdation(String ppoNo, String billType, String status, String patyMode, String lStrLocCd) throws Exception;

	void updateBillStatusByPKValuesList(List<Long> lLstPk, String lStrStatus, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception;

	void updateRltPensioncaseBillForMonthlyByPPONo(String ppoNo, String lStrStatus, List<Long> lLstPkList, String lStrBillType, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate)
			throws Exception;

	Double getAmountFromRltCaseBillByBillTypeAndMode(String ppoNo, String lStrStatus, String lStrBillType, String lStrPaymode, String lLocCode) throws Exception;

	void updateRltPensioncaseBillStatusAndBillNoForByPPONo(String ppoNo, String lStrStatus, List<Long> lLstPkList, String lStrBillType, Long lLngBillNo, String lStrLocCd, Long gLngPostId,
			Long gLngUserId, Date gDate) throws Exception;

	void updateRltPensioncaseBillTokenForByBillNo(Long lLngCaseBillNo, Long lLngTokenno, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception;

	Long getBillNo(String lStrPPONo, String lStrLocCd) throws Exception;
}
