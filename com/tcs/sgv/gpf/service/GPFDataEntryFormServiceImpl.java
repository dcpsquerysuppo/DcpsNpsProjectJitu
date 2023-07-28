/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Nov 29, 2011		Jayraj Chudasama								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.gpf.dao.GPFDataEntryFormDAO;
import com.tcs.sgv.gpf.dao.GPFDataEntryFormDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstEmpGpfAcc;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfChallanDtls;
import com.tcs.sgv.gpf.valueobject.MstGpfEmpSubscription;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;
import com.tcs.sgv.gpf.valueobject.MstGpfReq;
import com.tcs.sgv.gpf.valueobject.MstGpfYearly;
import com.tcs.sgv.gpf.valueobject.TrnEmpGpfAcc;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Nov 29, 2011
 */
public class GPFDataEntryFormServiceImpl extends ServiceImpl implements GPFDataEntryFormService {
	private final Log gLogger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
		}
	}

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GPFConstants");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.GPFDataEntryFormService#loadGPFDataEntryForm(
	 * java.util.Map)
	 */
	public ResultObject loadGPFDataEntryForm(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		List lLstMonths = null;
		List lLstYears = null;
		List lLstPurposeCatRA = null;
		List lLstPurposeCatNRA = null;

		String lStrUserType = "";
		String lStrDdoCode = "";
		List lLstEmpList = null;
		Integer lIntFinYear = null;
		String lStrFinYearCode = "";
		String lStrEmpCode = "";
		List lLstSubDetails = new ArrayList();
		List lLstPrevSubDetails = new ArrayList();
		List lLstSubscription = null;
		List lLstPrevSubscription = null;
		Object[] lObjAdvanceData = null;
		Object[] lObjPrevAdvanceData = null;
		List lLstRecoveryData = new ArrayList();
		String lStrVoucherDtlsDsplyOrNot = "none";
		List<String> displayOrNot = new ArrayList<String>();
		List lLstPrevRecoveryData=new ArrayList();
		Date lObjDate = new Date();
		List lLstEmpDtls = null;
		String lStrName = "";
		Double lDblBasicPay = 0d;
		String lStrStatusFlag = "";
		Integer lIntInstRecoveredStart = 0;
		Integer lIntPrevInstRecoveredStart = 0;
		Integer lIntOutstndgAsOn = null;
		Integer lIntPrevOutstndgAsOn = null;
		DateFormatSymbols dfs = new DateFormatSymbols();
		try {
			GPFDataEntryFormDAO lObjDataEntryFormDAO = new GPFDataEntryFormDAOImpl(TrnEmpGpfAcc.class, serv
					.getSessionFactory());
			lStrUserType = StringUtility.getParameter("userType", request);
			lStrEmpCode = StringUtility.getParameter("EmpCode", request);
			inputMap.put("userType", lStrUserType);

			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			lIntFinYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
			
			lStrFinYearCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(Long.parseLong(lIntFinYear
					.toString()));
			inputMap.put("finYearCode", lStrFinYearCode.substring(0, 4));
			inputMap.put("finYearId", lIntFinYear);

			Integer lIntCurMonth = lObjDate.getMonth();

			//if (lStrUserType.equals("DEO"))
			//edited for workflow change by shailesh
			if (lStrUserType.equals("DDO")) {
				DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
				lLstMonths = lObjCommomDao.getMonths();
				lLstYears = lObjCommomDao.getYears();
				lLstPurposeCatRA = IFMSCommonServiceImpl.getLookupValues("Purpose Category", SessionHelper
						.getLangId(inputMap), inputMap);
				lLstPurposeCatNRA = IFMSCommonServiceImpl.getLookupValues("Purpose Category NRA", SessionHelper
						.getLangId(inputMap), inputMap);
				inputMap.put("lstPurposeCatNRA", lLstPurposeCatNRA);
				inputMap.put("lstPurposeCatRA", lLstPurposeCatRA);
				inputMap.put("lLstMonths", lLstMonths);
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("SevaarthId", lStrEmpCode);
				if (lStrEmpCode != null && !lStrEmpCode.equals("")) {
					lLstEmpDtls = lObjDataEntryFormDAO.getEmployeeNameAndPay(lStrEmpCode, gStrLocationCode);
					if (lLstEmpDtls != null && lLstEmpDtls.size() > 0) {
						Object[] lObj = (Object[]) lLstEmpDtls.get(0);
						lStrName = lObj[0].toString();
						lDblBasicPay = (Double) lObj[1];
						lStrStatusFlag = lObjDataEntryFormDAO.chkEntryForSevaarthId(lStrEmpCode);
						inputMap.put("Name", lStrName);
						inputMap.put("BasicPay", lDblBasicPay);
						inputMap.put("StatusFlagDataEntry", lStrStatusFlag);
						lLstSubscription = lObjDataEntryFormDAO.getSubscriptionDataFromPayroll(lStrFinYearCode,
								lIntCurMonth, lStrEmpCode);
						
						if (lLstSubscription != null && lLstSubscription.size() > 0) {
							Long lLngSubsCurr = 0l;
							Long lLngRecovery = 0l;
							Long lLngSubsPrev = 0l;
							Long lLngStartSubs = 0l;
							Integer lIntMonthId = 0;
							Object[] lObjData = null;
							Object[] lObjSubData = null;
							Object[] lObjRecoveryData = null;
							Integer lIntSubsSrNo = 1;
							for (int lIntCnt = 0; lIntCnt < lLstSubscription.size(); lIntCnt++) {
								lObjData = (Object[]) lLstSubscription.get(lIntCnt);
								lLngSubsCurr = ((BigInteger) lObjData[1]).longValue();
								lLngRecovery = ((BigInteger) lObjData[2]).longValue();
								lIntMonthId = ((BigInteger) lObjData[4]).intValue();
								lIntFinYear = ((BigInteger) lObjData[7]).intValue();
								// extract subscription data from
								// lLstSubscription

								if (lIntCnt != 0 && !lLngSubsPrev.equals(lLngSubsCurr)) {
									lObjSubData = new Object[6];
									lObjSubData[0] = lLngSubsCurr;
									lObjSubData[1] = dfs.getMonths()[Integer.parseInt(lObjData[4].toString())];
									lObjSubData[2] = lStrFinYearCode.substring(0, 4);
									lObjSubData[3] = "";
									lObjSubData[4] = lObjData[4];
									lObjSubData[5] = lIntFinYear;
									lLstSubDetails.add(lObjSubData);
									lIntSubsSrNo++;
								} else if (lIntCnt == 0) {
									lLngStartSubs = lLngSubsCurr;
								}
								lLngSubsPrev = ((BigInteger) lObjData[1]).longValue();

								// extract advance recovery data from
								// lLstSubscription
								//if (!lLngRecovery.equals(0l)) {
									lObjRecoveryData = new Object[12];
									lObjRecoveryData[0] = "Voucher";
									lObjRecoveryData[1] = dfs.getMonths()[lIntMonthId];
									lObjRecoveryData[2] = lStrFinYearCode;
									lObjRecoveryData[3] = lObjData[5];
									lObjRecoveryData[4] = lObjData[6];
									lObjRecoveryData[5] = lLngRecovery;
									lObjRecoveryData[6] = "";
									lObjRecoveryData[7] = "";
									lObjRecoveryData[8] = lIntMonthId;
									lObjRecoveryData[9] = lIntFinYear;
									lObjRecoveryData[10] = lLngSubsCurr; /// for monthly subscription
									if (!lLngRecovery.equals(0l)) {
										displayOrNot.add("style='display:block'"); 
									}
									else displayOrNot.add( "style='display:none'");
									lLstRecoveryData.add(lObjRecoveryData);
								//}
									
									//added by shailesh	
								//to hide or display voucher details
							   // employee doesn't have advance(gpf_grp_adv_f) dont display voucher dtls									
									
							}

							if ((BigInteger) lObjData[2] != new BigInteger("0")) {
								lObjAdvanceData = lObjDataEntryFormDAO.getAdvanceDataFromPayroll(lStrEmpCode);
							}
							if (!lLngRecovery.equals(0l) && lLstRecoveryData != null && lLstRecoveryData.size() > 0 && lObjAdvanceData != null) {
								lObjAdvanceData[4] = ((BigInteger) lObjAdvanceData[4]).intValue()
										- lLstRecoveryData.size();
								lIntInstRecoveredStart = (Integer) lObjAdvanceData[4];
								lIntOutstndgAsOn = Integer.parseInt(lObjAdvanceData[2].toString()) - lIntInstRecoveredStart;
								for (Integer i = 0; i < lLstRecoveryData.size(); i++) {
									lObjRecoveryData = (Object[]) lLstRecoveryData.get(i);
									lObjRecoveryData[6] = ++lIntInstRecoveredStart;
								}
							}
							inputMap.put("OutInstlmntCur", lIntOutstndgAsOn);
							inputMap.put("SubDtls", lLstSubDetails);
							inputMap.put("lStrVoucherDtlsDsplyOrNot", lStrVoucherDtlsDsplyOrNot);
							inputMap.put("displayOrNot", displayOrNot);
							
							inputMap.put("ojbCur", lObjAdvanceData);
							inputMap.put("ScheduleDtls", lLstRecoveryData);
							inputMap.put("scheduleSize", lLstRecoveryData.size());
							inputMap.put("MonthlySub", lLngStartSubs);
						}
				
						//added by aditya
						
						lLstPrevSubscription = lObjDataEntryFormDAO.getPreviousSubscriptionDataFromPayroll(lStrFinYearCode,
						lIntCurMonth, lStrEmpCode);
						if (lLstPrevSubscription != null && lLstPrevSubscription.size() > 0) {
							Long lLngPrevSubsCurr = 0l;
							Long lLngPrevRecovery = 0l;
							Long lLngPrevSubsPrev = 0l;
							Long lLngPrevStartSubs = 0l;
							Integer lIntPrevMonthId = 0;
							Object[] lObjPrevData = null;
							Object[] lObjPrevSubData = null;
							Object[] lObjPrevRecoveryData = null;
							Integer lIntPrevSubsSrNo = 1;
							for (int lIntCnt = 0; lIntCnt < lLstPrevSubscription.size(); lIntCnt++) {
								lObjPrevData = (Object[]) lLstPrevSubscription.get(lIntCnt);
								lLngPrevSubsCurr = ((BigInteger) lObjPrevData[1]).longValue();
								lLngPrevRecovery = ((BigInteger) lObjPrevData[2]).longValue();
								lIntPrevMonthId = ((BigInteger) lObjPrevData[4]).intValue();

								// extract subscription data from
								// lLstSubscription

								if (lIntCnt != 0 && !lLngPrevSubsPrev.equals(lLngPrevSubsCurr)) {
									lObjPrevSubData = new Object[6];
									lObjPrevSubData[0] = lLngPrevSubsCurr;
									lObjPrevSubData[1] = dfs.getMonths()[Integer.parseInt(lObjPrevData[4].toString())];
									lObjPrevSubData[2] = lStrFinYearCode.substring(0, 4);
									lObjPrevSubData[3] = "";
									lObjPrevSubData[4] = lObjPrevData[4];
									lObjPrevSubData[5] = lIntFinYear;
									lLstPrevSubDetails.add(lObjPrevSubData);
									lIntPrevSubsSrNo++;
								} else if (lIntCnt == 0) {
									lLngPrevStartSubs = lLngPrevSubsCurr;
								}
								lLngPrevSubsPrev = ((BigInteger) lObjPrevData[1]).longValue();

								// extract advance recovery data from
								// lLstSubscription
								if (!lLngPrevRecovery.equals(0l)) {
									lObjPrevRecoveryData = new Object[10];
									lObjPrevRecoveryData[0] = "Voucher";
									lObjPrevRecoveryData[1] = dfs.getMonths()[lIntPrevMonthId];
									lObjPrevRecoveryData[2] = lStrFinYearCode;
									lObjPrevRecoveryData[3] = "";
									lObjPrevRecoveryData[4] = "";
									lObjPrevRecoveryData[5] = lLngPrevRecovery;
									lObjPrevRecoveryData[6] = "";
									lObjPrevRecoveryData[7] = "";
									lObjPrevRecoveryData[8] = lIntPrevMonthId;
									lObjPrevRecoveryData[9] = lIntFinYear;
									lLstPrevRecoveryData.add(lObjPrevRecoveryData);
								}
							}

							if ((BigInteger) lObjPrevData[2] != new BigInteger("0")) {
								lObjPrevAdvanceData = lObjDataEntryFormDAO.getAdvanceDataFromPayroll(lStrEmpCode);
							}
							if (!lLngPrevRecovery.equals(0l) && lLstPrevRecoveryData != null && lLstPrevRecoveryData.size() > 0 && lObjPrevAdvanceData != null) {
								lObjPrevAdvanceData[4] = ((BigInteger) lObjPrevAdvanceData[4]).intValue()
										- lLstPrevRecoveryData.size();
								lIntPrevInstRecoveredStart = (Integer) lObjPrevAdvanceData[4];
								lIntPrevOutstndgAsOn = Integer.parseInt(lObjPrevAdvanceData[2].toString()) - lIntPrevInstRecoveredStart;
								for (Integer i = 0; i < lLstPrevRecoveryData.size(); i++) {
									lObjPrevRecoveryData = (Object[]) lLstPrevRecoveryData.get(i);
									lObjPrevRecoveryData[6] = ++lIntPrevInstRecoveredStart;
								}
							}
							inputMap.put("PrevOutInstlmntCur", lIntPrevOutstndgAsOn);
							inputMap.put("PrevSubDtls", lLstPrevSubDetails);
							inputMap.put("PrevojbCur", lObjPrevAdvanceData);
							inputMap.put("PrevScheduleDtls", lLstPrevRecoveryData);
							inputMap.put("PrevscheduleSize", lLstPrevRecoveryData.size());
							inputMap.put("PrevMonthlySub", lLngPrevStartSubs);
						}
			//added by aditya			
					} else {
						//lStrName = "Invalid";
						inputMap.put("ValidateCode", "Invalid");
					}
				}
				resObj.setResultValue(inputMap);
				resObj.setViewName("GPFDataEntryForm");
			}

			//else if (lStrUserType.equals("DDO")) 
			//edited for workflow change by shailesh
			else if (lStrUserType.equals("DEO")) {
				lStrDdoCode = lObjDataEntryFormDAO.getDdoCodeForDDO(gLngPostId);
				
				lLstEmpList = lObjDataEntryFormDAO.getEmpListForVerification(gStrLocationCode);
				inputMap.put("EmpList", lLstEmpList);
				inputMap.put("userType", "DEO");
				resObj.setResultValue(inputMap);
				resObj.setViewName("GPFDataEntryWorklist");
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFDataEntryForm");
		}
		return resObj;
	}

	public ResultObject loadDataEntryDraftRequest(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");

		List lLstEmpList = null;

		try {
			GPFDataEntryFormDAO lObjDataEntryFormDAO = new GPFDataEntryFormDAOImpl(TrnEmpGpfAcc.class, serv
					.getSessionFactory());
			lLstEmpList = lObjDataEntryFormDAO.getEmpListForDraftReq(gStrLocationCode);
			inputMap.put("EmpList", lLstEmpList);
			inputMap.put("userType", "DDO"); ///edited for workflow change by shailesh
			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFDataEntryWorklist");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadDataEntryDraftRequest");
		}
		return resObj;
	}

	public ResultObject loadDataEntryCase(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");

		List lLstMonths = null;
		List lLstYears = null;
		List lLstRADetailsHis = null;
		List lLstRADetailsCur = null;
		List lLstNRADetails = null;
		List lLstSubDetails = null;
		List lLstPurposeCatRA = null;
		List lLstPurposeCatNRA = null;
		List lLstScheduleDtls = null;
		Integer lIntScheduleSize = 0;
		Integer lIntPrevOutInstlmnt = null;
		Object[] obj = null;

		String lStrGpfAccNo = "";
		String lStrUserType = "";
		String lStrSevaarthId = "";
		String lStrName = "";
		String lStrMonthlySub = "";
		String lStrOpeningBalc = "";
		String lStrDeoRemark = "";
		String lStrReqType = "";
		String lStrTrnAccPk = "";
		Integer lIntFinYear = null;
		String lStrFinYearCode = "";
		Double lDblBasicPay = null;

		String lStrVoucherDtlsDsplyOrNot = "none";		
		List<String> displayOrNot = new ArrayList<String>();

		try {
			GPFDataEntryFormDAO lObjDataEntryFormDAO = new GPFDataEntryFormDAOImpl(TrnEmpGpfAcc.class, serv
					.getSessionFactory());
			DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			lStrUserType = StringUtility.getParameter("userType", request);
			inputMap.put("userType", lStrUserType);
			lStrSevaarthId = StringUtility.getParameter("sevaarthId", request);
			lStrName = StringUtility.getParameter("name", request);
			lStrMonthlySub = StringUtility.getParameter("montlySub", request);
			lStrOpeningBalc = StringUtility.getParameter("openingBalc", request);
			lStrDeoRemark = StringUtility.getParameter("deoRemark", request);
			lStrReqType=StringUtility.getParameter("type", request);
			lStrGpfAccNo = lObjDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
			lIntFinYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
			lStrFinYearCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(Long.parseLong(lIntFinYear
					.toString()));
			inputMap.put("finYearCode", lStrFinYearCode.substring(0, 4));
			inputMap.put("finYearId", lIntFinYear);
			lLstMonths = lObjCommomDao.getMonths();
			lLstYears = lObjCommomDao.getYears();

			//if (lStrUserType.equals("DEO"))
			//edite for workflow change by shailesh
			if (lStrUserType.equals("DDO")) {
				if (lStrReqType.equals("Reject")){
					lStrTrnAccPk = lObjDataEntryFormDAO.getTrnEmpGpfAccID(lStrSevaarthId, lStrReqType);
					lLstRADetailsHis = lObjDataEntryFormDAO.getRADetailsHis(lStrGpfAccNo, lStrReqType);
					lLstRADetailsCur = lObjDataEntryFormDAO.getRADetailsCur(lStrGpfAccNo, lStrReqType);
					lLstNRADetails = lObjDataEntryFormDAO.getNRADetails(lStrGpfAccNo, lStrReqType);
					lLstSubDetails = lObjDataEntryFormDAO.getSubscriptionDetails(lStrGpfAccNo, lStrReqType);
					lLstScheduleDtls = lObjDataEntryFormDAO.getScheduleDetails(lStrGpfAccNo, lStrReqType);
					if (lLstScheduleDtls != null && lLstScheduleDtls.size() > 0) {
						for(int i =0; i< lLstScheduleDtls.size();i++){
						Object obj1[] =  (Object[])lLstScheduleDtls.get(i);
						Double advncrecovery = (Double)obj1[5];
						if(obj1[5] != null && !advncrecovery.equals(0.0) )
							displayOrNot.add("style='display:block'");
						else displayOrNot.add("style='display:none'");
						}
						
						lIntScheduleSize = lLstScheduleDtls.size();
					}
				}
				else
				{
				lStrReqType = "Draft";
				lStrTrnAccPk = lObjDataEntryFormDAO.getTrnEmpGpfAccID(lStrSevaarthId, lStrReqType);
				lLstRADetailsHis = lObjDataEntryFormDAO.getRADetailsHis(lStrGpfAccNo, lStrReqType);
				lLstRADetailsCur = lObjDataEntryFormDAO.getRADetailsCur(lStrGpfAccNo, lStrReqType);
				lLstNRADetails = lObjDataEntryFormDAO.getNRADetails(lStrGpfAccNo, lStrReqType);
				lLstSubDetails = lObjDataEntryFormDAO.getSubscriptionDetails(lStrGpfAccNo, lStrReqType);
				lLstScheduleDtls = lObjDataEntryFormDAO.getScheduleDetails(lStrGpfAccNo, lStrReqType);
				if (lLstScheduleDtls != null && lLstScheduleDtls.size() > 0) {
					for(int i =0; i< lLstScheduleDtls.size();i++){
						Object obj1[] =  (Object[])lLstScheduleDtls.get(i);
						Double advncrecovery = (Double)obj1[5];
						if(obj1[5] != null && !advncrecovery.equals(0.0) )
							displayOrNot.add("style='display:block'");
						else displayOrNot.add( "style='display:none'");
					}
					lIntScheduleSize = lLstScheduleDtls.size();
				}
				}
			
			}
			
			//edite for workflow change by shailesh
			//else if (lStrUserType.equals("DDO")) {
			else if (lStrUserType.equals("DEO")) {
				lStrReqType = "Forward";
				lStrTrnAccPk = lObjDataEntryFormDAO.getTrnEmpGpfAccID(lStrSevaarthId, lStrReqType);
				lLstRADetailsHis = lObjDataEntryFormDAO.getRADetailsHis(lStrGpfAccNo, lStrReqType);
				lLstRADetailsCur = lObjDataEntryFormDAO.getRADetailsCur(lStrGpfAccNo, lStrReqType);
				lLstNRADetails = lObjDataEntryFormDAO.getNRADetails(lStrGpfAccNo, lStrReqType);
				lLstSubDetails = lObjDataEntryFormDAO.getSubscriptionDetails(lStrGpfAccNo, lStrReqType);
				lLstScheduleDtls = lObjDataEntryFormDAO.getScheduleDetails(lStrGpfAccNo, lStrReqType);
				if (lLstScheduleDtls != null && lLstScheduleDtls.size() > 0) {
					for(int i =0; i< lLstScheduleDtls.size();i++){
						Object obj1[] =  (Object[])lLstScheduleDtls.get(i);
						Double advncrecovery = (Double)obj1[5];
						if(obj1[5] != null && !advncrecovery.equals(0.0) )
							displayOrNot.add("style='display:block'");
						else displayOrNot.add("style='display:none'");
					}
					lIntScheduleSize = lLstScheduleDtls.size();
				}
			}
			
			/*else if (lStrUserType.equals("HO")) {
				lStrReqType = "Reject";
				lStrTrnAccPk = lObjDataEntryFormDAO.getTrnEmpGpfAccID(lStrSevaarthId, lStrReqType);
				lLstRADetailsHis = lObjDataEntryFormDAO.getRADetailsHis(lStrGpfAccNo, lStrReqType);
				lLstRADetailsCur = lObjDataEntryFormDAO.getRADetailsCur(lStrGpfAccNo, lStrReqType);
				lLstNRADetails = lObjDataEntryFormDAO.getNRADetails(lStrGpfAccNo, lStrReqType);
				lLstSubDetails = lObjDataEntryFormDAO.getSubscriptionDetails(lStrGpfAccNo, lStrReqType);
				lLstScheduleDtls = lObjDataEntryFormDAO.getScheduleDetails(lStrGpfAccNo, lStrReqType);
				if (lLstScheduleDtls != null && lLstScheduleDtls.size() > 0) {
					lIntScheduleSize = lLstScheduleDtls.size();
				}
			}*/

			List lLstEmpDtls = lObjDataEntryFormDAO.getEmployeeNameAndPay(lStrSevaarthId, gStrLocationCode);
			if (lLstEmpDtls != null && lLstEmpDtls.size() > 0) {
				Object[] lObj = (Object[]) lLstEmpDtls.get(0);				
				lDblBasicPay = (Double) lObj[1];				
				inputMap.put("BasicPay", lDblBasicPay);
			}
			
			lLstPurposeCatRA = IFMSCommonServiceImpl.getLookupValues("Purpose Category", SessionHelper
					.getLangId(inputMap), inputMap);
			lLstPurposeCatNRA = IFMSCommonServiceImpl.getLookupValues("Purpose Category NRA", SessionHelper
					.getLangId(inputMap), inputMap);

			if (!lLstRADetailsCur.isEmpty()) {
				obj = (Object[]) lLstRADetailsCur.get(0);
				if(obj[13]!=null)
				{
					lIntPrevOutInstlmnt = Integer.parseInt(obj[13].toString());
				}
				else
				{	
					lIntPrevOutInstlmnt=null;
				}
				
			}

			inputMap.put("OutInstlmntCur",lIntPrevOutInstlmnt);
			inputMap.put("SevaarthId", lStrSevaarthId);
			inputMap.put("Name", lStrName);
			inputMap.put("MonthlySub", lStrMonthlySub);
			inputMap.put("TrnAccPk", lStrTrnAccPk);
			inputMap.put("OpeningBalc", lStrOpeningBalc);
			inputMap.put("RAHistory", lLstRADetailsHis);
			inputMap.put("ojbCur", obj);
			inputMap.put("NRADtls", lLstNRADetails);
			inputMap.put("SubDtls", lLstSubDetails);
			inputMap.put("DdoRemark", lStrDeoRemark);
			inputMap.put("lstPurposeCatRA", lLstPurposeCatRA);
			inputMap.put("lstPurposeCatNRA", lLstPurposeCatNRA);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("ScheduleDtls", lLstScheduleDtls);
			inputMap.put("scheduleSize", lIntScheduleSize);
			inputMap.put("lStrVoucherDtlsDsplyOrNot", lStrVoucherDtlsDsplyOrNot);
			inputMap.put("displayOrNot", displayOrNot);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFDataEntryForm");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadDataEntryCase");
		}

		return resObj;
	}

	public ResultObject getEmployeeNameFromEmpCode(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrName = "";
		Double lDblBasicPay = null;
		String lStrEmpCode = "";
		String lSBStatus = "";
		String lStrStatusFlag = "";
		List lLstEmpDtls = null;

		GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstEmp.class, serv
				.getSessionFactory());

		try {
			lStrEmpCode = StringUtility.getParameter("EmpCode", request);

			if (lStrEmpCode != "") {
				lLstEmpDtls = lObjGpfDataEntryFormDAO.getEmployeeNameAndPay(lStrEmpCode, gStrLocationCode);
				if (lLstEmpDtls != null && lLstEmpDtls.size() > 0) {
					Object[] lObj = (Object[]) lLstEmpDtls.get(0);
					lStrName = lObj[0].toString();
					lDblBasicPay = (Double) lObj[1];

					lStrStatusFlag = lObjGpfDataEntryFormDAO.chkEntryForSevaarthId(lStrEmpCode);
				} else {
					lStrName = "Invalid";
				}
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getEmployeeCode");
		}

		lSBStatus = getResponseXMLDocEmployeeCode(lStrName, lDblBasicPay, lStrStatusFlag).toString();

		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocEmployeeCode(String lStrEmployeeCode, Double lDblBasicPay,
			String lStrStatusFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<EMPCODE>");
		lStrBldXML.append(lStrEmployeeCode);
		lStrBldXML.append("</EMPCODE>");
		lStrBldXML.append("<BASICPAY>");
		lStrBldXML.append(lDblBasicPay);
		lStrBldXML.append("</BASICPAY>");
		lStrBldXML.append("<STATUS>");
		lStrBldXML.append(lStrStatusFlag);
		lStrBldXML.append("</STATUS>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject saveData(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		List<MstGpfAdvance> lLstAdvanceDetailsNRA = null;
		List<MstGpfAdvance> lLstAdvanceDetailsRA = null;
		List<MstGpfEmpSubscription> lLstSubscriptionDetails = null;
		List<MstGpfMonthly> lLstVoucherDetails = null;
		List<MstGpfChallanDtls> lLstChallanDetails = null;
		MstGpfAdvance lObjMstGpfAdvance = null;
		MstGpfEmpSubscription lObjMstGpfEmpSubscription = null;
		MstGpfMonthly lObjMstGpfMonthly = null;
		MstGpfChallanDtls lObjMstGpfChallanDtls = null;
		TrnEmpGpfAcc lObjTrnEmpGpfAcc = null;
		MstGpfReq lObjMstGpfReq = null;
		MstGpfYearly lObjMstGpfYearly = null;
		Long lLngAdvanceId = null;
		Long lLngEmpSubId = null;
		Long lLngTrnEmpGpfId = null;
		Long lLngMonthlyId = null;
		Long lLngChallanDtlsId = null;
		Long lLngBillGrpId = null;
		Long lLngGpfReqId = null;
		Long lLngMstGpfYearlyId = null;
		Integer lIntFinYear = null;
		String[] iSaveOrUpdate = null;
		String lStrTransactionId = "";
		String lStrMonthlySub = "";
		String lStrFlag = "false";
		String lStrSevaarthId = "";
		String lStrGpfAccNo = "";
		String lStrDEORemarks = "";
		String lStrOpeningBalc = "";
		String lStrEmpName = "";
		String lStrSaveOrUpdate = "";
		String lStrSaveOrUpdateCur = "";
		String lStrSaveOrUpdateVchr = "";
		String lStrSaveOrUpdateChlln = "";
		String lStrTrnAccPk = "";

		try {
			GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			GPFRequestProcessDAO lObjRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfReq.class, serv
					.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			GPFDataEntryFormDAO lObjDataEntryYearlyDAO = new GPFDataEntryFormDAOImpl(MstGpfYearly.class, serv
					.getSessionFactory());
			lLstAdvanceDetailsNRA = (List<MstGpfAdvance>) inputMap.get("AdvanceDetailsNRA");
			lLstAdvanceDetailsRA = (List<MstGpfAdvance>) inputMap.get("AdvanceDetailsRA");
			lLstSubscriptionDetails = (List<MstGpfEmpSubscription>) inputMap.get("SubscriptionDetails");
			lScheduleVOMap = (Map<String, List>) inputMap.get("ScheduleDtls");
			MstGpfAdvance lObjMstGpfAdvanceCur = new MstGpfAdvance();
			lObjMstGpfAdvanceCur = (MstGpfAdvance) inputMap.get("AdvanceDetailsRACur");

			lLstChallanDetails = lScheduleVOMap.get("lLstChallanDtls");
			lLstVoucherDetails = lScheduleVOMap.get("lLstVoucherDtls");

			lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			lStrDEORemarks = StringUtility.getParameter("txtDeoRemarks", request);
			lStrOpeningBalc = StringUtility.getParameter("txtAmount", request);
			lStrEmpName = StringUtility.getParameter("txtEmployeeName", request);
			lStrTrnAccPk = StringUtility.getParameter("hidTrnAccId", request);

			if (lLstAdvanceDetailsNRA != null && !lLstAdvanceDetailsNRA.isEmpty()) {
				lStrSaveOrUpdate = (String) inputMap.get("isSaveOrUpdateNRA");
				iSaveOrUpdate = lStrSaveOrUpdate.split(",");
				for (int lIntCnt = 0; lIntCnt < lLstAdvanceDetailsNRA.size(); lIntCnt++) {
					lObjMstGpfAdvance = new MstGpfAdvance();
					lObjMstGpfAdvance = lLstAdvanceDetailsNRA.get(lIntCnt);

					lObjMstGpfAdvance.setStatusFlag("D");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
						lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
						lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
						lObjMstGpfAdvance.setTransactionId(lStrTransactionId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfAdvance.setGpfAccNo(lStrGpfAccNo);
						
						lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
						lObjGpfDataEntryFormDAO.create(lObjMstGpfAdvance);

						lObjMstGpfReq = new MstGpfReq();
						lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
						lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
						lObjMstGpfReq.setTransactionId(lStrTransactionId);
						lObjMstGpfReq.setReqDtlId(lObjMstGpfAdvance.getMstGpfAdvanceId());
						lObjMstGpfReq.setReqType(lObjMstGpfAdvance.getAdvanceType());
						lObjRequestProcessDAO.create(lObjMstGpfReq);

					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvance);
					}
				}
			}

			if (lLstAdvanceDetailsRA != null && !lLstAdvanceDetailsRA.isEmpty()) {
				lStrSaveOrUpdate = (String) inputMap.get("isSaveOrUpdateRAHis");
				iSaveOrUpdate = lStrSaveOrUpdate.split(",");
				for (int lIntCnt = 0; lIntCnt < lLstAdvanceDetailsRA.size(); lIntCnt++) {
					lObjMstGpfAdvance = new MstGpfAdvance();
					lObjMstGpfAdvance = lLstAdvanceDetailsRA.get(lIntCnt);

					lObjMstGpfAdvance.setStatusFlag("D");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
						lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
						lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfAdvance.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfAdvance.setTransactionId(lStrTransactionId);
						lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
						lObjGpfDataEntryFormDAO.create(lObjMstGpfAdvance);

						lObjMstGpfReq = new MstGpfReq();
						lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
						lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
						lObjMstGpfReq.setTransactionId(lStrTransactionId);
						lObjMstGpfReq.setReqDtlId(lObjMstGpfAdvance.getMstGpfAdvanceId());
						lObjMstGpfReq.setReqType(lObjMstGpfAdvance.getAdvanceType());
						lObjRequestProcessDAO.create(lObjMstGpfReq);
					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvance);
					}
				}
			}

			if (lObjMstGpfAdvanceCur != null) {
				lStrSaveOrUpdateCur = (String) inputMap.get("isSaveOrUpdateRACur");
				lObjMstGpfAdvance = new MstGpfAdvance();
				lObjMstGpfAdvance = lObjMstGpfAdvanceCur;

				lObjMstGpfAdvance.setStatusFlag("DC");

				if (lStrSaveOrUpdateCur.equals("1")) {
					lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
					lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
					lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
					lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
					lObjMstGpfAdvance.setGpfAccNo(lStrGpfAccNo);
					lObjMstGpfAdvance.setTransactionId(lStrTransactionId);
					lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
					lObjGpfDataEntryFormDAO.create(lObjMstGpfAdvance);

					lObjMstGpfReq = new MstGpfReq();
					lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
					lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
					lObjMstGpfReq.setTransactionId(lStrTransactionId);
					lObjMstGpfReq.setReqDtlId(lObjMstGpfAdvance.getMstGpfAdvanceId());
					lObjMstGpfReq.setReqType(lObjMstGpfAdvance.getAdvanceType());
					lObjRequestProcessDAO.create(lObjMstGpfReq);
				} else if (lStrSaveOrUpdateCur.equals("2")) {
					lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvance);
				}
			}

			if (lLstVoucherDetails != null && !lLstVoucherDetails.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstVoucherDetails.size(); lIntCnt++) {
					lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchr");
					iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
					lObjMstGpfMonthly = new MstGpfMonthly();
					lObjMstGpfMonthly = lLstVoucherDetails.get(lIntCnt);

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
						lObjMstGpfMonthly.setMstGpfMonthlyId(lLngMonthlyId);
						lLngBillGrpId = Long.parseLong(lObjGpfDataEntryFormDAO.getBillGroupId(lStrSevaarthId));
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lStrTransactionId = lObjGpfDataEntryFormDAO.getTranIdForRAAdvance(lStrGpfAccNo, "Draft");
						lObjMstGpfMonthly.setBillgroupId(lLngBillGrpId);
						lObjMstGpfMonthly.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfMonthly.setAdvanceTrnId(lStrTransactionId);
						lObjMstGpfMonthly.setStatus("D");
						lObjMstGpfMonthly.setDataEntry("1");
						lObjGpfDataEntryFormDAO.create(lObjMstGpfMonthly);
					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfMonthly);
					}
				}
			}

			if (lLstChallanDetails != null && !lLstChallanDetails.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstChallanDetails.size(); lIntCnt++) {
					lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChln");
					iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
					lObjMstGpfChallanDtls = new MstGpfChallanDtls();
					lObjMstGpfChallanDtls = lLstChallanDetails.get(lIntCnt);

					lObjMstGpfChallanDtls.setStatusFlag("D");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngChallanDtlsId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_CHALLAN_DTLS", inputMap);
						lObjMstGpfChallanDtls.setMstGpfChallanDtlsId(lLngChallanDtlsId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lStrTransactionId = lObjGpfDataEntryFormDAO.getTranIdForRAAdvance(lStrGpfAccNo, "Draft");
						lObjMstGpfChallanDtls.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfChallanDtls.setAdvanceTrnId(lStrTransactionId);
						lObjGpfDataEntryFormDAO.create(lObjMstGpfChallanDtls);
					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfChallanDtls);
					}
				}
			}

			if (lLstSubscriptionDetails != null && !lLstSubscriptionDetails.isEmpty()) {
				lStrSaveOrUpdate = (String) inputMap.get("isSaveOrUpdateSub");
				iSaveOrUpdate = lStrSaveOrUpdate.split(",");
				for (int lIntCnt = 0; lIntCnt < lLstSubscriptionDetails.size(); lIntCnt++) {
					lObjMstGpfEmpSubscription = new MstGpfEmpSubscription();
					lObjMstGpfEmpSubscription = lLstSubscriptionDetails.get(lIntCnt);

					lObjMstGpfEmpSubscription.setStatusFlag("D");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngEmpSubId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_EMP_SUBSCRIPTION", inputMap);
						lObjMstGpfEmpSubscription.setGpfEmpSubscriptionId(lLngEmpSubId);
						lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfEmpSubscription.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfEmpSubscription.setTransactionId(lStrTransactionId);
						lObjGpfDataEntryFormDAO.create(lObjMstGpfEmpSubscription);

						lObjMstGpfReq = new MstGpfReq();
						lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
						lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
						lObjMstGpfReq.setTransactionId(lStrTransactionId);
						lObjMstGpfReq.setReqDtlId(lObjMstGpfEmpSubscription.getGpfEmpSubscriptionId());
						lObjMstGpfReq.setReqType("CS");
						lObjRequestProcessDAO.create(lObjMstGpfReq);
					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfEmpSubscription);
					}
				}
			}

			if (lStrTrnAccPk.equals("")) {
				lObjTrnEmpGpfAcc = new TrnEmpGpfAcc();
				lLngTrnEmpGpfId = IFMSCommonServiceImpl.getNextSeqNum("TRN_EMP_GPF_ACC", inputMap);
				lObjTrnEmpGpfAcc.setTrnEmpGpfAccId(lLngTrnEmpGpfId);
				lObjTrnEmpGpfAcc.setSevaarthId(lStrSevaarthId);
				lObjTrnEmpGpfAcc.setGpfAccNo(lStrGpfAccNo);
				lObjTrnEmpGpfAcc.setName(lStrEmpName);
				lObjTrnEmpGpfAcc.setStatusFlag("D");
				lObjTrnEmpGpfAcc.setDeoRemarks(lStrDEORemarks);
				lObjGpfDataEntryFormDAO.create(lObjTrnEmpGpfAcc);
			} else {
				lObjGpfDataEntryFormDAO.updateTrnEmpGpfAcc(lStrTrnAccPk, lStrDEORemarks, "", "Save");
			}

			lStrMonthlySub = StringUtility.getParameter("txtPrevSubAmt", request);

			GPFDataEntryFormDAO lObjGpfDataEntryFormDAOAcc = new GPFDataEntryFormDAOImpl(MstEmpGpfAcc.class, serv
					.getSessionFactory());
			String lstrMonthly="0";
			lObjGpfDataEntryFormDAOAcc.updateMstEmpGpfAcc(lStrSevaarthId, lStrOpeningBalc, lstrMonthly);
			
			
			if(lObjGpfDataEntryFormDAO.chkEntryForGpfYearly(lStrGpfAccNo).equals("N")){
				lObjMstGpfYearly = new MstGpfYearly();
				lLngMstGpfYearlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_YEARLY", inputMap);
				lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
				lIntFinYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
				lObjMstGpfYearly.setMstGpfYearlyId(lLngMstGpfYearlyId);
				lObjMstGpfYearly.setGpfAccNo(lStrGpfAccNo);
				lObjMstGpfYearly.setOpeningBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setClosingBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setFinYearId((lIntFinYear.longValue()) - 1);				
				lObjDataEntryYearlyDAO.create(lObjMstGpfYearly);
			}else{
				Long lLngMstgpfYearlyId = Long.parseLong(lObjGpfDataEntryFormDAO.chkEntryForGpfYearly(lStrGpfAccNo));
				lObjMstGpfYearly = new MstGpfYearly();
				lObjMstGpfYearly = (MstGpfYearly) lObjDataEntryYearlyDAO.read(lLngMstgpfYearlyId);
				lObjMstGpfYearly.setOpeningBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setClosingBalance(Double.parseDouble(lStrOpeningBalc));
				lObjDataEntryYearlyDAO.update(lObjMstGpfYearly);
			}
			
			//edited by aditya
			Date lObjDate = new Date();
			Double lDblRegularSubscription = null;
			Double lDblOpeningBalance = null;
			Double lDblClosingBalance = null;
			Double lDblAdvanceSanctioned = null;
			Double lDblPrePayOfAdvance = null;
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			String lStrFinCode = "";
			
			Integer lIntMon = 4;
			Integer lIntCurMonth = lObjDate.getMonth() + 1;
			Integer lIntFinancialYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
			Integer lIntNoOfMonth = null;

			if (lIntCurMonth >= 4 && lIntCurMonth <= 12) {
				lIntNoOfMonth = lIntCurMonth - 4;
			} else {
				lIntNoOfMonth = lIntCurMonth + 8;
			}
			for (int lIntCnt = 0; lIntCnt < lIntNoOfMonth; lIntCnt++) {
				lLngMonthlyId = lObjGpfDataEntryFormDAO.getMonthlyIDForMonth(lStrGpfAccNo, lIntMon, lIntFinancialYear
						.longValue());
				if (lLngMonthlyId == null) {
					lObjMstGpfMonthly = new MstGpfMonthly();
					lLngMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
					lObjMstGpfMonthly.setMstGpfMonthlyId(lLngMonthlyId);
					
					String lStrBillgrpId = lObjGpfDataEntryFormDAO.getBillGroupId(lStrSevaarthId);
					if(lStrBillgrpId !=null && !lStrBillgrpId.equals(""))
					lLngBillGrpId = Long.parseLong(lStrBillgrpId );
					else lLngBillGrpId = null;
					
					lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
					lObjMstGpfMonthly.setBillgroupId(lLngBillGrpId);
					lObjMstGpfMonthly.setGpfAccNo(lStrGpfAccNo);
					lDblRegularSubscription = lObjGpfDataEntryFormDAO.getMonthlySubscription(lIntMon.longValue(),
							lIntFinancialYear.longValue(), lStrGpfAccNo);
					lObjMstGpfMonthly.setRegularSubscription(lDblRegularSubscription);
					lObjMstGpfMonthly.setMonthId(lIntMon.longValue());
					lObjMstGpfMonthly.setFinYearId(lIntFinancialYear.longValue());
					lObjMstGpfMonthly.setStatus("D");
					lObjGpfDataEntryFormDAO.create(lObjMstGpfMonthly);
				}

				lObjMstGpfMonthly = new MstGpfMonthly();
				lObjMstGpfMonthly = (MstGpfMonthly) lObjScheduleGenerationDAO.read(lLngMonthlyId);
				lDblRegularSubscription = lObjGpfDataEntryFormDAO.getMonthlySubscription(lIntMon.longValue(),
						lIntFinancialYear.longValue(), lStrGpfAccNo);
				lObjMstGpfMonthly.setRegularSubscription(lDblRegularSubscription);
				lStrFinCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lIntFinancialYear.longValue());
				lDblOpeningBalance = lObjScheduleGenerationDAO.getOpeningBalForCurrMonth(lStrGpfAccNo, lIntMon
						.longValue(), lIntFinancialYear.longValue());
				lDblAdvanceSanctioned = lObjScheduleGenerationDAO.getAdvanceSanctionedForMonth(lStrGpfAccNo,
						lIntMon, lStrFinCode);
				lDblPrePayOfAdvance = lObjGpfDataEntryFormDAO.getChallanPaidForMonth(lStrGpfAccNo, lIntMon,
						lStrFinCode, Long.parseLong(gObjRsrcBndle.getString("GPF.PREPAYOFADVANCE")));
				lDblClosingBalance = lDblOpeningBalance + lDblPrePayOfAdvance
						+ lObjMstGpfMonthly.getRegularSubscription() - lDblAdvanceSanctioned;
				if (lObjMstGpfMonthly.getAdvanceRecovery() != null) {
					lDblClosingBalance += lObjMstGpfMonthly.getAdvanceRecovery();
				}

				lObjMstGpfMonthly.setOpeningBalance(lDblOpeningBalance);
				lObjMstGpfMonthly.setAdvanceSanctioned(lDblAdvanceSanctioned);
				lObjMstGpfMonthly.setPrePayOfAdvance(lDblPrePayOfAdvance);
				lObjMstGpfMonthly.setClosingBalance(lDblClosingBalance);
				lObjScheduleGenerationDAO.update(lObjMstGpfMonthly);

				lIntMon++;
				if (lIntMon == 13) {
					lIntMon = 1;
					lIntFinancialYear++;
				}
			}
			//edited by aditya
			
			
			lStrFlag = "true";

			String lSBStatus = getResponseXMLDocSaveData(lStrFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in saveData");
		}
		return resObj;
	}

	public ResultObject forwardData(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		List<MstGpfAdvance> lLstAdvanceDetailsNRA = null;
		List<MstGpfAdvance> lLstAdvanceDetailsRA = null;
		List<MstGpfEmpSubscription> lLstSubscriptionDetails = null;
		List<MstGpfMonthly> lLstVoucherDetails = null;
		List<MstGpfChallanDtls> lLstChallanDetails = null;
		List lLstPrvsData = null;
		MstGpfAdvance lObjMstGpfAdvance = null;
		MstGpfEmpSubscription lObjMstGpfEmpSubscription = null;
		MstGpfMonthly lObjMstGpfMonthly = null;
		MstGpfChallanDtls lObjMstGpfChallanDtls = null;
		TrnEmpGpfAcc lObjTrnEmpGpfAcc = null;
		MstGpfReq lObjMstGpfReq = null;
		Long lLngAdvanceId = null;
		Long lLngEmpSubId = null;
		Long lLngTrnEmpGpfId = null;
		Long lLngMonthlyId = null;
		Long lLngChallanDtlsId = null;
		Long lLngBillGrpId = null;
		Long lLngGpfReqId = null;
		String[] iSaveOrUpdate = null;
		String lStrTransactionId = "";
		String lStrMonthlySub = "";
		String lStrFlag = "false";
		String lStrSevaarthId = "";
		String lStrGpfAccNo = "";
		String lStrDEORemarks = "";
		String lStrOpeningBalc = "";
		String lStrEmpName = "";
		String lStrSaveOrUpdate = "";
		String lStrSaveOrUpdateCur = "";
		String lStrSaveOrUpdateVchr = "";
		String lStrSaveOrUpdateChlln = "";
		String lStrTrnAccPk = "";
		String lStrFinCode = "";
		String lStrStatus = "";
		MstGpfYearly lObjMstGpfYearly = null;
		Long lLngMstGpfYearlyId = null;
		Integer lIntFinYear = null;
		Double lDblRegularSubscription = null;
		Double lDblOpeningBalance = null;
		Double lDblClosingBalance = null;
		Double lDblAdvanceSanctioned = null;
		Double lDblPrePayOfAdvance = null;

		try {
			
			GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			GPFRequestProcessDAO lObjRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfReq.class, serv
					.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			GPFDataEntryFormDAO lObjDataEntryYearlyDAO = new GPFDataEntryFormDAOImpl(MstGpfYearly.class, serv
					.getSessionFactory());
			Date lObjDate = new Date();
			lIntFinYear = lObjFinancialYearDAO.getFinYearIdByCurDate();

			lLstAdvanceDetailsNRA = (List<MstGpfAdvance>) inputMap.get("AdvanceDetailsNRA");
			lLstAdvanceDetailsRA = (List<MstGpfAdvance>) inputMap.get("AdvanceDetailsRA");
			lLstSubscriptionDetails = (List<MstGpfEmpSubscription>) inputMap.get("SubscriptionDetails");
			lScheduleVOMap = (Map<String, List>) inputMap.get("ScheduleDtls");
			MstGpfAdvance lObjMstGpfAdvanceCur = new MstGpfAdvance();
			lObjMstGpfAdvanceCur = (MstGpfAdvance) inputMap.get("AdvanceDetailsRACur");

			lLstChallanDetails = lScheduleVOMap.get("lLstChallanDtls");
			lLstVoucherDetails = lScheduleVOMap.get("lLstVoucherDtls");

			lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			lStrDEORemarks = StringUtility.getParameter("txtDeoRemarks", request);
			lStrOpeningBalc = StringUtility.getParameter("txtAmount", request);
			lStrEmpName = StringUtility.getParameter("txtEmployeeName", request);
			lStrTrnAccPk = StringUtility.getParameter("hidTrnAccId", request);

			if(lObjGpfDataEntryFormDAO.chkEntryForSevaarthId(lStrSevaarthId).equals("Pending")){
				lStrStatus = "New";
			}else{
				lStrStatus = "Update";
			}
			
			if (lLstAdvanceDetailsNRA != null && !lLstAdvanceDetailsNRA.isEmpty()) {
				lStrSaveOrUpdate = (String) inputMap.get("isSaveOrUpdateNRA");
				iSaveOrUpdate = lStrSaveOrUpdate.split(",");
				for (int lIntCnt = 0; lIntCnt < lLstAdvanceDetailsNRA.size(); lIntCnt++) {
					lObjMstGpfAdvance = new MstGpfAdvance();
					lObjMstGpfAdvance = lLstAdvanceDetailsNRA.get(lIntCnt);

					lObjMstGpfAdvance.setStatusFlag("F");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
						lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
						lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfAdvance.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfAdvance.setTransactionId(lStrTransactionId);
						lObjMstGpfAdvance.setDataEntry("1");
						lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
						lObjGpfDataEntryFormDAO.create(lObjMstGpfAdvance);

						lObjMstGpfReq = new MstGpfReq();
						lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
						lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
						lObjMstGpfReq.setTransactionId(lStrTransactionId);
						lObjMstGpfReq.setReqDtlId(lObjMstGpfAdvance.getMstGpfAdvanceId());
						lObjMstGpfReq.setReqType(lObjMstGpfAdvance.getAdvanceType());
						lObjRequestProcessDAO.create(lObjMstGpfReq);

					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvance);
					}

				}
			}
			
			if (lLstAdvanceDetailsRA != null && !lLstAdvanceDetailsRA.isEmpty()) {
				lStrSaveOrUpdate = (String) inputMap.get("isSaveOrUpdateRAHis");
				iSaveOrUpdate = lStrSaveOrUpdate.split(",");
				for (int lIntCnt = 0; lIntCnt < lLstAdvanceDetailsRA.size(); lIntCnt++) {
					lObjMstGpfAdvance = new MstGpfAdvance();
					lObjMstGpfAdvance = lLstAdvanceDetailsRA.get(lIntCnt);

					lObjMstGpfAdvance.setStatusFlag("F");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
						lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
						lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfAdvance.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfAdvance.setTransactionId(lStrTransactionId);
						lObjMstGpfAdvance.setDataEntry("1");
						lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
						lObjGpfDataEntryFormDAO.create(lObjMstGpfAdvance);

						lObjMstGpfReq = new MstGpfReq();
						lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
						lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
						lObjMstGpfReq.setTransactionId(lStrTransactionId);
						lObjMstGpfReq.setReqDtlId(lObjMstGpfAdvance.getMstGpfAdvanceId());
						lObjMstGpfReq.setReqType(lObjMstGpfAdvance.getAdvanceType());
						lObjRequestProcessDAO.create(lObjMstGpfReq);

					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvance);
					}
				}
			}

			if (lObjMstGpfAdvanceCur != null) {
				lStrSaveOrUpdateCur = (String) inputMap.get("isSaveOrUpdateRACur");
				lObjMstGpfAdvance = new MstGpfAdvance();
				lObjMstGpfAdvance = lObjMstGpfAdvanceCur;

				lObjMstGpfAdvance.setStatusFlag("AC");

				if (lStrSaveOrUpdateCur.equals("1")) {
					lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
					lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
					lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
					lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
					lObjMstGpfAdvance.setGpfAccNo(lStrGpfAccNo);
					lObjMstGpfAdvance.setTransactionId(lStrTransactionId);
					lObjMstGpfAdvance.setDataEntry("1");
					lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
					lObjGpfDataEntryFormDAO.create(lObjMstGpfAdvance);

					lObjMstGpfReq = new MstGpfReq();
					lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
					lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
					lObjMstGpfReq.setTransactionId(lStrTransactionId);
					lObjMstGpfReq.setReqDtlId(lObjMstGpfAdvance.getMstGpfAdvanceId());
					lObjMstGpfReq.setReqType(lObjMstGpfAdvance.getAdvanceType());
					lObjRequestProcessDAO.create(lObjMstGpfReq);

				} else if (lStrSaveOrUpdateCur.equals("2")) {
					lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvance);
				}
			}
			
			if (lLstChallanDetails != null && !lLstChallanDetails.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstChallanDetails.size(); lIntCnt++) {
					lStrSaveOrUpdateChlln = (String) inputMap.get("isSaveOrUpdateChln");
					iSaveOrUpdate = lStrSaveOrUpdateChlln.split(",");
					lObjMstGpfChallanDtls = new MstGpfChallanDtls();
					lObjMstGpfChallanDtls = lLstChallanDetails.get(lIntCnt);

					lObjMstGpfChallanDtls.setStatusFlag("F");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngChallanDtlsId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_CHALLAN_DTLS", inputMap);
						lObjMstGpfChallanDtls.setMstGpfChallanDtlsId(lLngChallanDtlsId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lStrTransactionId = lObjGpfDataEntryFormDAO.getTranIdForRAAdvance(lStrGpfAccNo, "Forward");
						lObjMstGpfChallanDtls.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfChallanDtls.setAdvanceTrnId(lStrTransactionId);
						lObjMstGpfChallanDtls.setDataEntry("1");
						lObjGpfDataEntryFormDAO.create(lObjMstGpfChallanDtls);
					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfChallanDtls);
					}
				}
			}
			
			if (lLstSubscriptionDetails != null && !lLstSubscriptionDetails.isEmpty()) {
				lStrSaveOrUpdate = (String) inputMap.get("isSaveOrUpdateSub");
				iSaveOrUpdate = lStrSaveOrUpdate.split(",");
				for (int lIntCnt = 0; lIntCnt < lLstSubscriptionDetails.size(); lIntCnt++) {
					lObjMstGpfEmpSubscription = new MstGpfEmpSubscription();
					lObjMstGpfEmpSubscription = lLstSubscriptionDetails.get(lIntCnt);

					lObjMstGpfEmpSubscription.setStatusFlag("F");

					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngEmpSubId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_EMP_SUBSCRIPTION", inputMap);
						lObjMstGpfEmpSubscription.setGpfEmpSubscriptionId(lLngEmpSubId);
						lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfEmpSubscription.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfEmpSubscription.setTransactionId(lStrTransactionId);
						lObjMstGpfEmpSubscription.setDataEntry("1");
						lObjGpfDataEntryFormDAO.create(lObjMstGpfEmpSubscription);

						lObjMstGpfReq = new MstGpfReq();
						lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
						lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
						lObjMstGpfReq.setTransactionId(lStrTransactionId);
						lObjMstGpfReq.setReqDtlId(lObjMstGpfEmpSubscription.getGpfEmpSubscriptionId());
						lObjMstGpfReq.setReqType("CS");
						lObjRequestProcessDAO.create(lObjMstGpfReq);

					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfEmpSubscription);
					}
				}
			}
			
			if (lStrTrnAccPk.equals("")) {
				lObjTrnEmpGpfAcc = new TrnEmpGpfAcc();
				lLngTrnEmpGpfId = IFMSCommonServiceImpl.getNextSeqNum("TRN_EMP_GPF_ACC", inputMap);
				lObjTrnEmpGpfAcc.setTrnEmpGpfAccId(lLngTrnEmpGpfId);
				lObjTrnEmpGpfAcc.setSevaarthId(lStrSevaarthId);
				lObjTrnEmpGpfAcc.setGpfAccNo(lStrGpfAccNo);
				lObjTrnEmpGpfAcc.setName(lStrEmpName);
				lObjTrnEmpGpfAcc.setStatusFlag("F");
				lObjTrnEmpGpfAcc.setDdoRemarks(lStrDEORemarks);
				lObjGpfDataEntryFormDAO.create(lObjTrnEmpGpfAcc);
			} else {
				
				lObjGpfDataEntryFormDAO.updateTrnEmpGpfAcc(lStrTrnAccPk, "", lStrDEORemarks, "Forward");
			}

			lStrMonthlySub = StringUtility.getParameter("txtPrevSubAmt", request);
			String lstrMonthly="0";
			lObjGpfDataEntryFormDAO.updateMstEmpGpfAcc(lStrSevaarthId, lStrOpeningBalc, lstrMonthly);			
			lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
			if(lObjGpfDataEntryFormDAO.chkEntryForGpfYearly(lStrGpfAccNo).equals("N")){
				lObjMstGpfYearly = new MstGpfYearly();
				lLngMstGpfYearlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_YEARLY", inputMap);
				
				lIntFinYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
				lObjMstGpfYearly.setMstGpfYearlyId(lLngMstGpfYearlyId);
				lObjMstGpfYearly.setGpfAccNo(lStrGpfAccNo);
				lObjMstGpfYearly.setOpeningBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setClosingBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setFinYearId((lIntFinYear.longValue()) - 1);
				
				lObjMstGpfYearly.setCreatedDate(new Date());
				lObjDataEntryYearlyDAO.create(lObjMstGpfYearly);
			}else{
				Long lLngMstgpfYearlyId = Long.parseLong(lObjGpfDataEntryFormDAO.chkEntryForGpfYearly(lStrGpfAccNo));
				lObjMstGpfYearly = new MstGpfYearly();
				lObjMstGpfYearly = (MstGpfYearly) lObjDataEntryYearlyDAO.read(lLngMstgpfYearlyId);
				lObjMstGpfYearly.setOpeningBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setClosingBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setUpdatedDate(new Date());
				lObjDataEntryYearlyDAO.update(lObjMstGpfYearly);
			}
			
			if (lLstVoucherDetails != null && !lLstVoucherDetails.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstVoucherDetails.size(); lIntCnt++) {
					lStrSaveOrUpdateVchr = (String) inputMap.get("isSaveOrUpdateVchr");
					iSaveOrUpdate = lStrSaveOrUpdateVchr.split(",");
					lObjMstGpfMonthly = new MstGpfMonthly();
					lObjMstGpfMonthly = lLstVoucherDetails.get(lIntCnt);

					lObjMstGpfMonthly.setStatus("F");
					
					if (iSaveOrUpdate[lIntCnt].equals("1")) {
						lLngMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
						lObjMstGpfMonthly.setMstGpfMonthlyId(lLngMonthlyId);
						lLngBillGrpId = Long.parseLong(lObjGpfDataEntryFormDAO.getBillGroupId(lStrSevaarthId));
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lStrTransactionId = lObjGpfDataEntryFormDAO.getTranIdForRAAdvance(lStrGpfAccNo, "Forward");
						lObjMstGpfMonthly.setBillgroupId(lLngBillGrpId);
						lObjMstGpfMonthly.setGpfAccNo(lStrGpfAccNo);
						lObjMstGpfMonthly.setAdvanceTrnId(lStrTransactionId);
						lObjMstGpfMonthly.setDataEntry("1");
						lObjGpfDataEntryFormDAO.create(lObjMstGpfMonthly);						
					} else if (iSaveOrUpdate[lIntCnt].equals("2")) {
						lObjGpfDataEntryFormDAO.update(lObjMstGpfMonthly);						
					}					
				}
			}			
			
			
			Integer lIntMon = 4;
			Integer lIntCurMonth = lObjDate.getMonth() + 1;
			Integer lIntFinancialYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
			Integer lIntNoOfMonth = null;

			if (lIntCurMonth >= 4 && lIntCurMonth <= 12) {
				lIntNoOfMonth = lIntCurMonth - 4;
			} else {
				lIntNoOfMonth = lIntCurMonth + 8;
			}

			if(lStrStatus.equals("Update")){
				for (int lIntCnt = 0; lIntCnt < lIntNoOfMonth; lIntCnt++) {
					lLngMonthlyId = lObjGpfDataEntryFormDAO.getMonthlyIDForMonth(lStrGpfAccNo, lIntMon, lIntFinancialYear
							.longValue());
					if (lLngMonthlyId == null) {
						lObjMstGpfMonthly = new MstGpfMonthly();
						lLngMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
						lObjMstGpfMonthly.setMstGpfMonthlyId(lLngMonthlyId);
						lLngBillGrpId = Long.parseLong(lObjGpfDataEntryFormDAO.getBillGroupId(lStrSevaarthId));
						lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfMonthly.setBillgroupId(lLngBillGrpId);
						lObjMstGpfMonthly.setGpfAccNo(lStrGpfAccNo);
						lDblRegularSubscription = lObjGpfDataEntryFormDAO.getMonthlySubscription(lIntMon.longValue(),
								lIntFinancialYear.longValue(), lStrGpfAccNo);
						lObjMstGpfMonthly.setRegularSubscription(lDblRegularSubscription);
						lObjMstGpfMonthly.setMonthId(lIntMon.longValue());
						lObjMstGpfMonthly.setFinYearId(lIntFinancialYear.longValue());
						lObjMstGpfMonthly.setStatus("F");
						lObjGpfDataEntryFormDAO.create(lObjMstGpfMonthly);
					}
	
					lObjMstGpfMonthly = new MstGpfMonthly();
					lObjMstGpfMonthly = (MstGpfMonthly) lObjScheduleGenerationDAO.read(lLngMonthlyId);
					lDblRegularSubscription = lObjGpfDataEntryFormDAO.getMonthlySubscription(lIntMon.longValue(),
							lIntFinancialYear.longValue(), lStrGpfAccNo);
					lObjMstGpfMonthly.setRegularSubscription(lDblRegularSubscription);
					lStrFinCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lIntFinancialYear.longValue());
					lDblOpeningBalance = lObjScheduleGenerationDAO.getOpeningBalForCurrMonth(lStrGpfAccNo, lIntMon
							.longValue(), lIntFinancialYear.longValue());
					lDblAdvanceSanctioned = lObjScheduleGenerationDAO.getAdvanceSanctionedForMonth(lStrGpfAccNo,
							lIntMon, lStrFinCode);
					lDblPrePayOfAdvance = lObjGpfDataEntryFormDAO.getChallanPaidForMonth(lStrGpfAccNo, lIntMon,
							lStrFinCode, Long.parseLong(gObjRsrcBndle.getString("GPF.PREPAYOFADVANCE")));
					lDblClosingBalance = lDblOpeningBalance + lDblPrePayOfAdvance
							+ lObjMstGpfMonthly.getRegularSubscription() - lDblAdvanceSanctioned;
					if (lObjMstGpfMonthly.getAdvanceRecovery() != null) {
						lDblClosingBalance += lObjMstGpfMonthly.getAdvanceRecovery();
					}
	
					lObjMstGpfMonthly.setOpeningBalance(lDblOpeningBalance);
					lObjMstGpfMonthly.setAdvanceSanctioned(lDblAdvanceSanctioned);
					lObjMstGpfMonthly.setPrePayOfAdvance(lDblPrePayOfAdvance);
					lObjMstGpfMonthly.setClosingBalance(lDblClosingBalance);
					lObjScheduleGenerationDAO.update(lObjMstGpfMonthly);
	
					lIntMon++;
					if (lIntMon == 13) {
						lIntMon = 1;
						lIntFinancialYear++;
					}
				}
			}
			
			lStrFlag = "true";
			
			String lSBStatus = getResponseXMLDocSaveData(lStrFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in forwardData");
		}
		return resObj;
	}

	public ResultObject approveRequest(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List lLstSubscription = null;
		List lLstAdvance = null;
		List lLstMonthlyDtls = null;
		List lLstChallanDtls = null;
		Object[] obj = null;
		String lStrSevaarthId = "";
		String lStrDdoRemarks = "";
		String lStrFlag = "";
		String lStrGpfAccNo = "";
		String lStrOpeningBalc = "";
		String lStrFinCode = "";
		String lStrType = "";
		String lStrTrnAccPk = "";
		Long lLngMstGpfYearlyId = null;
		Long lLngMonthlyId = null;
		Long lLngBillGrpId = null;
		Integer lIntNoOfMonth = null;
		Double lDblRegularSubscription = null;
		Double lDblOpeningBalance = null;
		Double lDblClosingBalance = null;
		Double lDblAdvanceSanctioned = null;
		Double lDblPrePayOfAdvance = null;
		Integer lIntFinYear = null;
		MstGpfYearly lObjMstGpfYearly = null;
		MstGpfAdvance lObjMstGpfAdvance = null;
		MstGpfMonthly lObjMstGpfMonthly = null;
		Date lObjDate = new Date();

		try {
			GPFDataEntryFormDAO lObjDataEntryFormDAO = new GPFDataEntryFormDAOImpl(TrnEmpGpfAcc.class, serv
					.getSessionFactory());
			GPFDataEntryFormDAO lObjDataEntryYearlyDAO = new GPFDataEntryFormDAOImpl(MstGpfYearly.class, serv
					.getSessionFactory());
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			lStrDdoRemarks = StringUtility.getParameter("txtDdoRemarks", request);
			lStrOpeningBalc = StringUtility.getParameter("txtAmount", request);
			lStrGpfAccNo = lObjDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
			lStrType = StringUtility.getParameter("type", request);
			lStrTrnAccPk = StringUtility.getParameter("hidTrnAccId", request);
			StringUtility.getParameter("txtPrevSubAmt", request);

			if (lStrType.equals("Approve")) {
				lObjDataEntryFormDAO.updateTrnEmpGpfAcc(lStrTrnAccPk, "", lStrDdoRemarks, "Approve");

				lStrGpfAccNo = lObjDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
				if(lObjDataEntryYearlyDAO.chkEntryForGpfYearly(lStrGpfAccNo).equals("N")){
					lObjMstGpfYearly = new MstGpfYearly();
					lLngMstGpfYearlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_YEARLY", inputMap);
					
					lIntFinYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
					lObjMstGpfYearly.setMstGpfYearlyId(lLngMstGpfYearlyId);
					lObjMstGpfYearly.setGpfAccNo(lStrGpfAccNo);
					lObjMstGpfYearly.setOpeningBalance(Double.parseDouble(lStrOpeningBalc));
					lObjMstGpfYearly.setClosingBalance(Double.parseDouble(lStrOpeningBalc));
					lObjMstGpfYearly.setFinYearId((lIntFinYear.longValue()) - 1);
					
					lObjMstGpfYearly.setCreatedDate(new Date());
					lObjDataEntryYearlyDAO.create(lObjMstGpfYearly);
				}else{
					Long lLngMstgpfYearlyId = Long.parseLong(lObjDataEntryYearlyDAO.chkEntryForGpfYearly(lStrGpfAccNo));
					lObjMstGpfYearly = new MstGpfYearly();
					lObjMstGpfYearly = (MstGpfYearly) lObjDataEntryYearlyDAO.read(lLngMstgpfYearlyId);
					lObjMstGpfYearly.setOpeningBalance(Double.parseDouble(lStrOpeningBalc));
					lObjMstGpfYearly.setClosingBalance(Double.parseDouble(lStrOpeningBalc));
					lObjMstGpfYearly.setUpdatedDate(new Date());
					lObjDataEntryYearlyDAO.update(lObjMstGpfYearly);
				}
				
				/*
				lObjMstGpfYearly = new MstGpfYearly();
				lLngMstGpfYearlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_YEARLY", inputMap);
				lStrGpfAccNo = lObjDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
				lIntFinYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
				lObjMstGpfYearly.setMstGpfYearlyId(lLngMstGpfYearlyId);
				lObjMstGpfYearly.setGpfAccNo(lStrGpfAccNo);
				lObjMstGpfYearly.setOpeningBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setClosingBalance(Double.parseDouble(lStrOpeningBalc));
				lObjMstGpfYearly.setFinYearId((lIntFinYear.longValue()) - 1);
				lObjDataEntryFormDAO.create(lObjMstGpfYearly);*/

				lStrFlag = "Approve";
			} else if (lStrType.equals("Reject")) {
				lObjDataEntryFormDAO.updateTrnEmpGpfAcc(lStrTrnAccPk, "", lStrDdoRemarks, "Reject");
				lObjDataEntryFormDAO.updateMstEmpGpfAcc(lStrSevaarthId, "0", "0");
				lStrFlag = "Reject";
			}

			lLstSubscription = lObjDataEntryFormDAO.getSubscriptionDetails(lStrGpfAccNo, "Forward");
			if (lLstSubscription != null && !lLstSubscription.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstSubscription.size(); lIntCnt++) {
					obj = (Object[]) lLstSubscription.get(lIntCnt);
					if (lStrType.equals("Approve")) {
						lObjDataEntryFormDAO.updateEmpSubscription(obj[3].toString(), "Approve");
					} else if (lStrType.equals("Reject")) {
						lObjDataEntryFormDAO.updateEmpSubscription(obj[3].toString(), "Discard");
					}
				}
			}

			lLstAdvance = lObjDataEntryFormDAO.getAdvanceDetailsNew(lStrGpfAccNo);
			if (lLstAdvance != null && !lLstAdvance.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstAdvance.size(); lIntCnt++) {
					lObjMstGpfAdvance = new MstGpfAdvance();
					lObjMstGpfAdvance = (MstGpfAdvance) lLstAdvance.get(lIntCnt);
					if (lStrType.equals("Approve")) {
						lObjMstGpfAdvance.setStatusFlag("A");
					} else if (lStrType.equals("Reject")) {
						lObjMstGpfAdvance.setStatusFlag("X");
					}
					lObjDataEntryFormDAO.update(lObjMstGpfAdvance);
				}
			}

			lLstMonthlyDtls = lObjDataEntryFormDAO.getScheduleDetails(lStrGpfAccNo, "Forward");
			Integer lIntFinancialYear = lObjFinancialYearDAO.getFinYearIdByCurDate();
			if (lLstMonthlyDtls != null && !lLstMonthlyDtls.isEmpty()) {				
				for (int lIntCnt = 0; lIntCnt < lLstMonthlyDtls.size(); lIntCnt++) {
					obj = (Object[]) lLstMonthlyDtls.get(lIntCnt);
					if (obj[0].toString().equals("Voucher")) {
						if (lStrType.equals("Approve")) {							
							
							lObjMstGpfMonthly = new MstGpfMonthly();
							lObjMstGpfMonthly = (MstGpfMonthly) lObjScheduleGenerationDAO.read(Long.parseLong(obj[7].toString()));
							lStrFinCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lIntFinancialYear.longValue());
							lDblOpeningBalance = lObjScheduleGenerationDAO.getOpeningBalForCurrMonth(lStrGpfAccNo, Long
									.parseLong(obj[8].toString()), lIntFinancialYear.longValue());
							lDblAdvanceSanctioned = lObjScheduleGenerationDAO.getAdvanceSanctionedForMonth(lStrGpfAccNo,
									Integer.parseInt(obj[8].toString()), lStrFinCode);
							lDblPrePayOfAdvance = lObjDataEntryFormDAO.getChallanPaidForMonth(lStrGpfAccNo, Integer.parseInt(obj[8].toString()),
									lStrFinCode, Long.parseLong(gObjRsrcBndle.getString("GPF.PREPAYOFADVANCE")));
							lDblClosingBalance = lDblOpeningBalance + lDblPrePayOfAdvance
									+ lObjMstGpfMonthly.getRegularSubscription() - lDblAdvanceSanctioned;
							if (lObjMstGpfMonthly.getAdvanceRecovery() != null) {
								lDblClosingBalance += lObjMstGpfMonthly.getAdvanceRecovery();
							}

							lObjMstGpfMonthly.setOpeningBalance(lDblOpeningBalance);
							lObjMstGpfMonthly.setAdvanceSanctioned(lDblAdvanceSanctioned);
							lObjMstGpfMonthly.setPrePayOfAdvance(lDblPrePayOfAdvance);
							lObjMstGpfMonthly.setClosingBalance(lDblClosingBalance);
							lObjMstGpfMonthly.setStatus("A");
							lObjScheduleGenerationDAO.update(lObjMstGpfMonthly);
							
							
							/*lObjDataEntryFormDAO.updateGpfMonthly(Long.parseLong(obj[7].toString()), Long
									.parseLong(obj[8].toString()), Long.parseLong(obj[9].toString()), lStrGpfAccNo);*/
						} else if (lStrType.equals("Reject")) {
							lObjDataEntryFormDAO.discardGPFMonthly(obj[7].toString());
						}
					}
				}
			}

			/*if (lStrType.equals("Approve")) {
				Integer lIntMon = 4;
				Integer lIntCurMonth = lObjDate.getMonth() + 1;
				Integer lIntFinancialYear = lObjFinancialYearDAO.getFinYearIdByCurDate();

				if (lIntCurMonth >= 4 && lIntCurMonth <= 12) {
					lIntNoOfMonth = lIntCurMonth - 4;
				} else {
					lIntNoOfMonth = lIntCurMonth + 8;
				}

				for (int lIntCnt = 0; lIntCnt < lIntNoOfMonth; lIntCnt++) {
					lLngMonthlyId = lObjDataEntryFormDAO.getMonthlyIDForMonth(lStrGpfAccNo, lIntMon, lIntFinancialYear
							.longValue());
					if (lLngMonthlyId == null) {
						lObjMstGpfMonthly = new MstGpfMonthly();
						lLngMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
						lObjMstGpfMonthly.setMstGpfMonthlyId(lLngMonthlyId);
						lLngBillGrpId = Long.parseLong(lObjDataEntryFormDAO.getBillGroupId(lStrSevaarthId));
						lStrGpfAccNo = lObjDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
						lObjMstGpfMonthly.setBillgroupId(lLngBillGrpId);
						lObjMstGpfMonthly.setGpfAccNo(lStrGpfAccNo);
						lDblRegularSubscription = lObjDataEntryFormDAO.getMonthlySubscription(lIntMon.longValue(),
								lIntFinancialYear.longValue(), lStrGpfAccNo);
						lObjMstGpfMonthly.setRegularSubscription(lDblRegularSubscription);
						lObjMstGpfMonthly.setMonthId(lIntMon.longValue());
						lObjMstGpfMonthly.setFinYearId(lIntFinancialYear.longValue());
						lObjMstGpfMonthly.setStatus("A");
						lObjDataEntryFormDAO.create(lObjMstGpfMonthly);
					}

					lObjMstGpfMonthly = new MstGpfMonthly();
					lObjMstGpfMonthly = (MstGpfMonthly) lObjScheduleGenerationDAO.read(lLngMonthlyId);
					lStrFinCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lIntFinancialYear.longValue());
					lDblOpeningBalance = lObjScheduleGenerationDAO.getOpeningBalForCurrMonth(lStrGpfAccNo, lIntMon
							.longValue(), lIntFinancialYear.longValue());
					lDblAdvanceSanctioned = lObjScheduleGenerationDAO.getAdvanceSanctionedForMonth(lStrGpfAccNo,
							lIntMon, lStrFinCode);
					lDblPrePayOfAdvance = lObjDataEntryFormDAO.getChallanPaidForMonth(lStrGpfAccNo, lIntMon,
							lStrFinCode, Long.parseLong(gObjRsrcBndle.getString("GPF.PREPAYOFADVANCE")));
					lDblClosingBalance = lDblOpeningBalance + lDblPrePayOfAdvance
							+ lObjMstGpfMonthly.getRegularSubscription() - lDblAdvanceSanctioned;
					if (lObjMstGpfMonthly.getAdvanceRecovery() != null) {
						lDblClosingBalance += lObjMstGpfMonthly.getAdvanceRecovery();
					}

					lObjMstGpfMonthly.setOpeningBalance(lDblOpeningBalance);
					lObjMstGpfMonthly.setAdvanceSanctioned(lDblAdvanceSanctioned);
					lObjMstGpfMonthly.setPrePayOfAdvance(lDblPrePayOfAdvance);
					lObjMstGpfMonthly.setClosingBalance(lDblClosingBalance);
					lObjScheduleGenerationDAO.update(lObjMstGpfMonthly);

					lIntMon++;
					if (lIntMon == 13) {
						lIntMon = 1;
						lIntFinancialYear++;
					}
				}
			}*/
			
			
			lLstChallanDtls = lObjDataEntryFormDAO.getScheduleDetails(lStrGpfAccNo, "Forward");
			if (lLstChallanDtls != null && !lLstChallanDtls.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstChallanDtls.size(); lIntCnt++) {
					obj = (Object[]) lLstChallanDtls.get(lIntCnt);
					if (obj[0].toString().equals("Challan")) {
						if (lStrType.equals("Approve")) {
							lObjDataEntryFormDAO.updateChallanDetails(obj[6].toString(), "Approve");
						} else if (lStrType.equals("Reject")) {
							lObjDataEntryFormDAO.updateChallanDetails(obj[6].toString(), "Reject");
						}
					}
				}
			}

			String lSBStatus = getResponseXMLDocSaveData(lStrFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in approveRequest");
		}
		return resObj;
	}

	private StringBuilder getResponseXMLDocSaveData(String lStrFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
