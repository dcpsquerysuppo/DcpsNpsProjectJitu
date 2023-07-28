package com.tcs.sgv.pensionpay.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillReceipt;


public interface TrnPensionBillReceiptDAO extends GenericDao<TrnPensionBillReceipt, Long> {

	List<TrnPensionBillReceipt> getRcptDtlFromHdrId(Long lPensionBillHdrID) throws Exception;
	Map<Long,Object> getMapOfRcptDtlFromHdrIds(List lListPensionBillNo,String lStrLocCode) throws Exception; 
}
