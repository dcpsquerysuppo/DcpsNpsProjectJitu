package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.RltPensionRevisedPayment;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsncaseRopRlt;


public interface NewPensionBillDAO {

	TrnPensionRqstHdr getPensionRqstHdrDtls(String lStrPnsnerCode, String lStrStatus, String lStrLocCode) throws Exception;

	Double getTrnPensionRecoveryDtls(String lStrPensionerCode, String lStrStatus) throws Exception;

	Double getTotalRecoveryForMonth(String lStrPensionerCode, int ForMonth) throws Exception;

	ArrayList getTrnPensionItCutDtls(String lStrPensionerCode) throws Exception;

	Double getTotalPensionCutForMonth(String lStrPensionerCode, int ForMonth) throws Exception;

	Double getTotalOtherBenefitsForMonth(String lStrPensionerCode, int ForMonth) throws Exception;

	Double getTotalSpecialCutForMonth(String lStrPensionerCode, int ForMonth) throws Exception;

	Double getTotalITCutForMonth(String lStrPensionerCode, int ForMonth) throws Exception;

	List getRltPensioncaseBillPK(String lStrPPONO, String lStrBillType, String lStrStatus, String lStrLocCode) throws Exception;

	TrnPensionBillDtls getTrnPensionBillDtls(Long TrnPensionBillHdrPK) throws Exception;

	List<TrnPensionArrearDtls> getTrnPensionArrearDtls(String lStrPensionerCode, Long lLngBillNo) throws Exception;

	List<TrnPensionBillDtls> getPrvBillArrearsDtls(String lStrPensionerCode, Long lBillHdrPK) throws Exception;

	List<TrnPnsncaseRopRlt> getROPAppliedToPensner(String lStrPPONO) throws Exception;

	Double getNewBasicFromROPMst(String lStrROP, String lStrColumnNo, Double lStrOldBasic) throws Exception;

	RltPensionHeadcodeRate getRateFromHeadcodeRateRlt(Long lHeadcode, String lStrFieldType, Double lStrPnsnBasic, Date lForDate) throws Exception;

	Double getBasicFromHeadcodeSpecialRlt(Long lHeadcode, Double lStrOldPnsnBasic, Date lForDate) throws Exception;

	Long getPKOfPnsncaseROPRlt(String lStrPPONO, String lStrROP) throws Exception;

	RltPensionRevisedPayment getPaymentMnthDtls(long lPensionRatePk, String lStrFieldType, String lStrForDate) throws Exception;

	public List<RltPensionHeadcodeRate> getRateLstFromHeadcode(Long lHeadcode, List<String> lLstFieldType) throws Exception;

	Map getRateFromHeadcodeRateRlt(Long lLngHeadcode) throws Exception;
}
