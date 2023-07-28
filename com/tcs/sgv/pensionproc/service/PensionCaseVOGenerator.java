/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 1, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAssesdDues;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcCheckList;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRecovery;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAvgPayCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocEventdtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocNomineedtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrpay;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrservcbreak;


/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 1, 2011
 */
public interface PensionCaseVOGenerator 
{
	ResultObject generateMap(Map inputMap);
	TrnPnsnProcInwardPension generateTrnPnsnProcInwardPensionVO(Map inputMap);
	TrnPnsnProcPnsnrDtls generateTrnPnsnProcPnsnrDtlsVO(Map inputMap);
	TrnPnsnProcPnsnCalc generateTrnPnsnProcPnsnCalcVO(Map inputMap);
	TrnPnsnprocPnsnrpay generateTrnPnsnprocPnsnrpayVO(Map inputMap);
	TrnPnsnProcRecovery generateTrnPnsnProcRecoveryVO(Map inputMap);
	List<TrnPnsnprocEventdtls> generateTrnPnsnprocEventdtlsVO(Map inputMap);
	List<TrnPnsnprocPnsnrservcbreak> generateTrnPnsnprocPnsnrservcbreakVO(Map inputMap);
	List<TrnPnsnProcAdvnceBal> generateTrnPnsnProcAdvnceBalVO(Map inputMap);
	List<TrnPnsnProcAssesdDues> generateTrnPnsnProcAssesdDuesVO(Map inputMap);
	List<TrnPnsnProcCheckList> generateTrnPnsnProcCheckListVO(Map inputMap);
	List<TrnPnsnprocFamilydtls> generateTrnPnsnProcPnsnFamilydtlsVO(Map inputMap);
	List<TrnPnsnprocNomineedtls> generateTrnPnsnProcPnsnNomineedtlsVO(Map inputMap);
	List<TrnPnsnprocAvgPayCalc> generateTrnPnsnprocAvgPayCalcVO(Map inputMap);
}

