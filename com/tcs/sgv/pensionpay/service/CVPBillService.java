package com.tcs.sgv.pensionpay.service;

import java.util.Map;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * CVP Bill Specific Srvice Interface.
 * 
 * @author Soumya Adhikari
 * @version 1.0
 */

public interface CVPBillService 
{
	//To save CVP Bill Specific Data
	//ResultObject saveCVPBill(Map<String,Object> inputMap);
	
	//To Show CVP Bill Specific Data
	ResultObject getCVPBillData(Map<String,Object> iMapInput);
	
	//To Show saved bill
	//ResultObject viewCVPBillData(Map<String,Object> inputMap);
}
