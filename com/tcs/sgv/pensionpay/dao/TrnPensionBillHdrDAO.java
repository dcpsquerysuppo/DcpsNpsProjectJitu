package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Date;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;


public interface TrnPensionBillHdrDAO extends GenericDao<TrnPensionBillHdr, Long> {

	int getBillGenerationMonth(String lStrPensionerCode) throws Exception;

	TrnPensionBillHdr getTrnPensionBillHdr(Long lLngBillNo, String lStrBillType) throws Exception;

	TrnPensionBillHdr getUniqueTrnPensionBillHdr(Long lLngBillHdrId) throws Exception;

	ArrayList<TrnPensionBillHdr> getTrnPensionBillHdrList(Long lLngBillNo, String lStrBillType) throws Exception;
	
	void updateTrnPensionBillHdrRejectStatus(Long lLngBillNo,Long gLngPostId,Long gLngUserId,Date gDate) throws Exception;
	
	void updateRecoveryDtilsFromRejection(Long lLngBillNo,Long gLngPostId,Long gLngUserId,Date gDate) throws Exception;
}
