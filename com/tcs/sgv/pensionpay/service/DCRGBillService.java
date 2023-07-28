package com.tcs.sgv.pensionpay.service;

import java.util.Map;
import com.tcs.sgv.core.valueobject.ResultObject;

public interface DCRGBillService 
{
	//	To Show DCRG Bill Specific Data
	public ResultObject getDCRGBillData(Map<String,Object> iMapInput);
	
	//	To save DCRG Bill Specific Data
	//public ResultObject saveDCRGBill(Map<String,Object> inputMap);
	
	// To view DCRG Bill After Saved
	//public ResultObject viewDCRGBillData(Map<String,Object> iMapInput);
}
