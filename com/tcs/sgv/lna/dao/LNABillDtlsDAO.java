package com.tcs.sgv.lna.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.lna.valueobject.MstLnaBillDtls;
import com.tcs.sgv.lna.valueobject.MstLnaPayrollLoanTypeMpg;

public interface LNABillDtlsDAO  extends GenericDao{

	public void updateBillNoForCompAdv(List<String> lLstSevaarthId,List<String> lLstTrnsId,Long lLngBillId);
	
	public void updateBillNoForMotorAdv(List<String> lLstSevaarthId,List<String> lLstTrnsId,Long lLngBillId);
	
	public void updateBillNoForHouseAdv(List<String> lLstSevaarthId,List<String> lLstTrnsId,Long lLngBillId);
	
	public List<MstLnaBillDtls> getLoanBillDtls(String lStrLocation);
	
	public List<MstLnaPayrollLoanTypeMpg> getPayrollLoanId(Long lLngLoanId);
	
	public List<Object> getEmpCompLoanDtls(Long lLngLoanBillId);
	
	public List<Object> getEmpHouseLoanDtls(Long lLngLoanBillId);
	
	public List<Object> getEmpMotorLoanDtls(Long lLngLoanBillId);
	
	public void rejectBillForAdv(String lStrTableName,Long lLngBillId);
	
	public Boolean isLoanBillPending(Long lLngLocationCode);
	
	public List<MstLnaBillDtls> getLoanOrderDtls(String lStrLocation);
}
