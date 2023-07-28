/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 16, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.RltAuditorBank;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;


/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * May 16, 2011
 */
public interface PensionConfigVOGenerator 
{
	ResultObject generateMap(Map inputMap);
	List<RltAuditorBank> generateRltAuditorBankVO(Map inputMap);
	MstPensionHeadcode generateMstPensionHeadcodeVO(Map inputMap);
	List<RltPensionHeadcodeChargable> generateRltPensionHeadcodeChargableVO(Map inputMap);
	ResultObject generateHstPnsnPmntPpoNoVO(Map inputMap);
	ResultObject generateMstPensionMainCategoryVO(Map inputMap);
	 
}
