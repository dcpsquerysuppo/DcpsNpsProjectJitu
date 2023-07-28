/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.HstReEmploymentDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCaseMvmnt;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalPensionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalVoucherDtls;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Apr 28, 2011
 */
public interface PhysicalCaseInwardVOGenerator {

	ResultObject generateMap(Map inputMap);
	MstPensionerHdr generateMstPensionerHdrVO(Map<String, Object> inputMap) throws Exception;
	TrnPensionRqstHdr generateTrnPensionRqstHdrVO(Map<String, Object> inputMap) throws Exception;
	MstPensionerDtls generateMstPensionerDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<TrnProvisionalPensionDtls> generateTrnProvisionalPensionDtlsVO(Map<String, Object> inputMap) throws Exception;
	//List<TrnProvisionalVoucherDtls> generateTrnProvisionalVoucherDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<MstPensionerFamilyDtls> generateMstPensionerFamilyDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<MstPensionerNomineeDtls> generateMstPensionerNomineeDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<TrnPensionRecoveryDtls> generateTrnPensionRecoveryDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<TrnCvpRestorationDtls> generateTrnCvpRestorationDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<HstReEmploymentDtls> generateHstReEmploymentDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<HstCommutationDtls> generateHstCommutationDtlsVO(Map<String, Object> inputMap) throws Exception;
	//List<HstPnsnPmntDcrgDtls> generateHstPnsnPmntDcrgDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<TrnPensionCaseMvmnt> generateTrnPensionCaseMvmntVO(Map<String, Object> inputMap) throws Exception;
	List<TrnPensionCutDtls> generateTrnPensionCutDtlsVO(Map<String, Object> inputMap) throws Exception;
	void generateCorrectionDtlsVO(Integer lIntRowNumber, Object lObjApprovedObject, Object lObjCurrentObject, String lStrFieldType, Integer lIntApprovalStatus) throws Exception;
	void generateCorrectionDtlsVOForAmount(Integer lIntRowNumber, Object lObjApprovedObject, Object lObjCurrentObject, String lStrFieldType, Integer lIntApprovalStatus) throws Exception;
	void generateCorrectionDtlsVOForDate(Integer lIntRowNumber, Object lObjApprovedObject, Object lObjCurrentObject, String lStrFieldType, Integer lIntApprovalStatus) throws Exception;
	ResultObject generateHstPnsnPmntDcrgDtlsVO(Map<String, Object> inputMap) throws Exception;
	List<TrnPensionArrearDtls> generateTrnPensionArrearDtlsVO(Map<String, Object> inputMap) throws Exception;
}
