package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.dao.GPFApprovedRequestDAO;
import com.tcs.sgv.gpf.dao.GPFApprovedRequestDAOImpl;
import com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO;
import com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAOImpl;
import com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAO;
import com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfEmpSubscription;
import com.tcs.sgv.gpf.valueobject.TrnEmpGpfAcc;
import com.tcs.sgv.gpf.valueobject.TrnGpfFinalWithdrawal;

public class GPFRequestProcessServiceImpl extends ServiceImpl implements GPFRequestProcessService {
	Log glogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private Date gDtCurDate = null; /* CURRENT DATE */
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

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GPFConstants");

	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			glogger.error("Error is :" + e, e);
		}

	}

	public ResultObject loadGPFRequestProcess(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		new SimpleDateFormat("MM");
		Date lDtcurrDate = SessionHelper.getCurDate();

		try {
			setSessionInfo(inputMap);
			List gpfEmpList = new ArrayList();
			String alertMessage = "";
			String lStrNewOrderRefId = "";
			Boolean lBlOneRequestExists = false;
			Boolean lBlHierarchyUser = false;
			Long lLngDPOrGP = 0l;
			Long lLngEmpId = null;
			Double lDblRegSubscription = null;
			String regularSubscription = "";
			// get the data from search employee screen
			String lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request);
			lStrSevaarthId = lStrSevaarthId.toUpperCase();
			String lStrEmpName = StringUtility.getParameter("txtEmployeeName", request);
			String criteria = StringUtility.getParameter("criteria", request);
			String requestType = StringUtility.getParameter("requestType", request);

			/*
			 * get the user(DEO,DEOAPP,HO) who opens the request DEO- Data Entry
			 * Operator DEOAPP - Verifier HO - Head Officer
			 */

			String lStrUser = StringUtility.getParameter("userType", request);

			// get the primary key to open the record once created
			String lStrPKValue = StringUtility.getParameter("pkValue", request);

			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(null, serv
					.getSessionFactory());
			lStrNewOrderRefId = lObjGPFApprovedRequestDAO.getNewOrderRefId(gStrLocationCode);

			inputMap.put("SevaarthId", lStrSevaarthId);
			inputMap.put("EmpName", lStrEmpName);
			inputMap.put("requestType", requestType);
			inputMap.put("userType", lStrUser);
			inputMap.put("OrderNo", lStrNewOrderRefId);

			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serv.getSessionFactory());
			ScheduleGenerationDAO lObjScheduleGenerationDAOImpl = new ScheduleGenerationDAOImpl(null, serv
					.getSessionFactory());
			Integer lIntCurrFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();
			String lStrCurrFinYearCode = lObjScheduleGenerationDAOImpl.getFinYearCodeForFinYearId(lIntCurrFinYearId
					.longValue());
			lStrCurrFinYearCode = lStrCurrFinYearCode.substring(0, 4);
			inputMap.put("FinYearCode", lStrCurrFinYearCode);

			GPFRequestProcessDAO lObjGPFRequestProcess = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			String lStrDdoCode = "";
			if (lStrUser.equals("DEO")) {
				lStrDdoCode = lObjGPFRequestProcess.getDdoCodeForDEO(gStrLocationCode);
			}
			// String lStrLocationCode =
			// lObjGPFRequestProcess.getLocationCodeOfUser(gLngPostId);
			gpfEmpList = lObjGPFRequestProcess.getEmployeeDetail(lStrSevaarthId, lStrEmpName, criteria, lStrDdoCode,
					lStrUser);
			regularSubscription = lObjGPFRequestProcess.getRegularSubscription(lStrSevaarthId,lStrDdoCode );
			Boolean RAeligiblity = false;
			Boolean finalEligibility = false;
			Boolean changeSubscriptionEligibility = false;
			if (gpfEmpList != null && gpfEmpList.size() > 0) {
				Object[] GPFEmpVO = (Object[]) gpfEmpList.get(0);
				String strGpfAccNo = GPFEmpVO[6].toString();
				inputMap.put("GPFEmpVO", GPFEmpVO);
				lLngEmpId = (Long) GPFEmpVO[19];

				if (GPFEmpVO[17].toString().equals("5th Pay Commission")) {
					Double lDblDPOrGP = ((Double) GPFEmpVO[3] * (50) / 100);
					lLngDPOrGP = lDblDPOrGP.longValue();
				} else {
					// lLngDPOrGP = lObjGPFRequestProcess.getDPOrGP(lLngEmpId,
					// GPFEmpVO[17].toString());
				}
				inputMap.put("DPOrGP", lLngDPOrGP);
				String lStrEmpAddress = "";
				if (GPFEmpVO[8] != null) {
					lStrEmpAddress += GPFEmpVO[8].toString() + ",";
				}
				if (GPFEmpVO[9] != null) {
					lStrEmpAddress += GPFEmpVO[9].toString() + ",";
				}
				if (GPFEmpVO[10] != null) {
					lStrEmpAddress += GPFEmpVO[10].toString() + ",";
				}
				if (GPFEmpVO[11] != null) {
					String lStrDistrict = lObjGPFRequestProcess.getDistrictNameForId(Long.parseLong(GPFEmpVO[11]
							.toString()));
					lStrEmpAddress += lStrDistrict + ",";
				}
				if (GPFEmpVO[12] != null) {
					String lStrState = lObjGPFRequestProcess.getStateNameForId(Long.parseLong(GPFEmpVO[12].toString()));
					lStrEmpAddress += lStrState;
				}
				if (GPFEmpVO[13] != null) {
					lStrEmpAddress = " - " + lStrEmpAddress + GPFEmpVO[13].toString();
				}
				inputMap.put("EmpAddress", lStrEmpAddress);
				if (requestType.equals("CS")) {
					changeSubscriptionEligibility = checkChangeSubscriptionEligibility((Date) GPFEmpVO[16],
							(Date) GPFEmpVO[7]);
					lBlOneRequestExists = loadChangeSubscription(strGpfAccNo, lStrUser, inputMap);
				} else if (requestType.equals("RA")) {
					RAeligiblity = checkRefundableAdvanceEligibility((Date) GPFEmpVO[16], (Date) GPFEmpVO[7]);
					lBlOneRequestExists = loadRefundableAdvance(strGpfAccNo, lStrUser, inputMap);
				} else if (requestType.equals("NRA")) {
					lBlOneRequestExists = loadNonRefundableAdvance(strGpfAccNo, lStrUser, inputMap);
				} else if (requestType.equals("FW")) {

					lBlOneRequestExists = loadFinalWithdrawal(strGpfAccNo, lStrUser, inputMap);
				}
				inputMap.put("lBlOneRequestExists", lBlOneRequestExists);
				finalEligibility = checkFinalEligibility((Date) GPFEmpVO[16]);
				inputMap.put("FinalEligibility", finalEligibility);
				Double lDblOpeningBal = lObjGPFRequestProcess.getOpeningBalForCurrYear(strGpfAccNo, lIntCurrFinYearId
						.longValue());
				inputMap.put("OpeningBalance", lDblOpeningBal);
				List accountBalance = lObjGPFRequestProcess.getGPFAccountBalance(strGpfAccNo, lIntCurrFinYearId
						.longValue());
				if (accountBalance != null && accountBalance.size() > 0) {
					Object[] objGPFAccountBal = (Object[]) accountBalance.get(0);
					inputMap.put("TillDateCredit", objGPFAccountBal);
				}
				Double lDblAdvanceSanc = 0d;
				Double lDblWithdrawalSanc = 0d;
				List advanceHistoryDtls = lObjGPFRequestProcess.getAdvanceHistory(strGpfAccNo, lIntCurrFinYearId
						.longValue());
				if (advanceHistoryDtls != null && advanceHistoryDtls.size() > 0) {
					Object[] historyObj = (Object[]) advanceHistoryDtls.get(0);
					if (historyObj[0].equals("RA")) {
						lDblAdvanceSanc = (Double) historyObj[1];
					} else {
						lDblWithdrawalSanc = (Double) historyObj[1];
					}
					if (advanceHistoryDtls.size() > 1) {
						historyObj = (Object[]) advanceHistoryDtls.get(1);
						lDblAdvanceSanc = (Double) historyObj[1];
					}
				}
				inputMap.put("AdvanceSanctioned", lDblAdvanceSanc);
				inputMap.put("WithdrawalSanctioned", lDblWithdrawalSanc);
				inputMap.put("lDtCurrDate", lObjDateFormat.format(lDtcurrDate));

				lDblRegSubscription = lObjGPFRequestProcess.getLatestSubscription(strGpfAccNo);
				inputMap.put("LatestSubscription", lDblRegSubscription);
				
				// Double lDblMonthlySubs =
				// lObjGPFRequestProcess.getMonthlySubscription(strGpfAccNo,
				// lIntCurrMonth, lIntCurrFinYearId);
				// inputMap.put("MonthlySubs", lDblMonthlySubs);

				// To get the payscale data from payroll module

				HrEisScaleMst lObjHrEisScaleMst = null;
				lObjHrEisScaleMst = lObjGPFRequestProcess.getPayScaleData(lLngEmpId);

				if (lObjHrEisScaleMst != null) {

					StringBuffer scaleDisp = new StringBuffer("");
					if (lObjHrEisScaleMst.getHrPayCommissionMst().getId() == 2500341) {

						scaleDisp.append(lObjHrEisScaleMst.getScaleStartAmt());

						scaleDisp.append("-");
						scaleDisp.append(lObjHrEisScaleMst.getScaleEndAmt());
						scaleDisp.append(" (");
						scaleDisp.append(lObjHrEisScaleMst.getScaleGradePay());
						scaleDisp.append(")");

					} else {
						scaleDisp.append(lObjHrEisScaleMst.getScaleStartAmt());
						scaleDisp.append("-");
						scaleDisp.append(lObjHrEisScaleMst.getScaleIncrAmt());
						scaleDisp.append("-");
						scaleDisp.append(lObjHrEisScaleMst.getScaleEndAmt());
						if (lObjHrEisScaleMst.getScaleHigherIncrAmt() > 0
								&& lObjHrEisScaleMst.getScaleHigherEndAmt() > 0) {
							scaleDisp.append("-");
							scaleDisp.append(lObjHrEisScaleMst.getScaleHigherIncrAmt());
							scaleDisp.append("-");
							scaleDisp.append(lObjHrEisScaleMst.getScaleHigherEndAmt());
							if (lObjHrEisScaleMst.getScaleSecondHigherIncrAmt() > 0
									&& lObjHrEisScaleMst.getScaleSecondHigherEndAmt() > 0) {
								scaleDisp.append("-");
								scaleDisp.append(lObjHrEisScaleMst.getScaleSecondHigherIncrAmt());
								scaleDisp.append("-");
								scaleDisp.append(lObjHrEisScaleMst.getScaleSecondHigherEndAmt());

								if (lObjHrEisScaleMst.getScaleThirdHigherIncrAmt() > 0
										&& lObjHrEisScaleMst.getScaleThirdHigherEndAmt() > 0) {
									scaleDisp.append("-");
									scaleDisp.append(lObjHrEisScaleMst.getScaleThirdHigherIncrAmt());
									scaleDisp.append("-");
									scaleDisp.append(lObjHrEisScaleMst.getScaleThirdHigherEndAmt());
								}
							}
						}
					}
					inputMap.put("payScale", scaleDisp);
				}

				String GPFNextPostId = "";
				if (!lStrUser.equals("HO") && !lStrUser.equals("DDO")) {
					List UserList = getHierarchyUsers(inputMap);
					if (UserList.size() > 0) {
						GPFNextPostId = UserList.get(0).toString();
					} else {
						lBlHierarchyUser = true;
					}
				}
				inputMap.put("GPFPostIdForDEO", GPFNextPostId);
				inputMap.put("PostId", gStrPostId);
				inputMap.put("regularSubscription", regularSubscription);
				
				if (lBlHierarchyUser) {
					alertMessage = "HierarchyUser";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					resObj.setViewName("GPFEmpSearch");
				} 
				//to remove validation for multiple NRA in a financial year
				/*else if (requestType.equals("NRA")
						&& lObjGPFRequestProcess.withdrawalExistsForFinYear(strGpfAccNo, lIntCurrFinYearId.longValue())
						&& !lStrUser.equals("DDO")) {					
					alertMessage = "WithdrawalExists";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					resObj.setViewName("GPFEmpSearch");
				}*/ else if (lBlOneRequestExists && (lStrPKValue.equals(null) || lStrPKValue.equals(""))) {
					alertMessage = "OneReqExists";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					resObj.setViewName("GPFEmpSearch");
				} else if (RAeligiblity) {
					alertMessage = "NotEligibleRA";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					resObj.setViewName("GPFEmpSearch");
					// } else if (!finalEligibility && requestType.equals("FW"))
					// {
					// alertMessage = "NotEligibleFW";
					// inputMap.put("alertMessage", alertMessage);
					// resObj.setResultValue(inputMap);
					// resObj.setViewName("GPFEmpSearch");
				} else if (changeSubscriptionEligibility) {
					alertMessage = "NotEligibleCS";
					inputMap.put("alertMessage", alertMessage);
					resObj.setResultValue(inputMap);
					resObj.setViewName("GPFEmpSearch");
				} else {
					lStrSevaarthId = GPFEmpVO[22].toString();
					lStrEmpName = GPFEmpVO[0].toString();
					inputMap.put("SevaarthId", lStrSevaarthId);
					inputMap.put("EmpName", lStrEmpName);
					resObj.setResultValue(inputMap);
					resObj.setViewName("GPFRequestProcessing");
				}
			} else {
				alertMessage = "InvalidEmp";
				inputMap.put("alertMessage", alertMessage);
				resObj.setResultValue(inputMap);
				resObj.setViewName("GPFEmpSearch");
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
		}
		return resObj;

	}

	private Boolean loadChangeSubscription(String strGpfAccNo, String userType, Map inputMap) throws Exception {
		List changeSubAmountList = null;
		Object changeSubAmount = null;
		GPFChangeSubscriptionDAO lObjChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(null, serv
				.getSessionFactory());

		// if (userType.equals("DEO")) {
		// changeSubAmountList =
		// lObjChangeSubscriptionDAO.getChangeSubscription(strGpfAccNo);
		// }

		String lStrChangeSubId = StringUtility.getParameter("pkValue", request);
		if (!lStrChangeSubId.equals("") && !lStrChangeSubId.equals(null)) {
			Long lLngChangeSubId = Long.parseLong(lStrChangeSubId);
			changeSubAmountList = lObjChangeSubscriptionDAO.getChangeSubscriptionToDEOApprover(strGpfAccNo,
					lLngChangeSubId, gLngPostId);

		}
		if (changeSubAmountList != null && changeSubAmountList.size() > 0) {
			changeSubAmount = changeSubAmountList.get(0);
			inputMap.put("changeSubList", changeSubAmount);
		}
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serv.getSessionFactory());
		Long lLngCurrFinYearId = new Integer(lObjFinancialYearDAO.getFinYearIdByCurDate()).longValue();

		Long lLngLastScheduleGenMonth = 0l;
		List lLstLastScheduleGenData = lObjChangeSubscriptionDAO.getLastScheduleData(strGpfAccNo, lLngCurrFinYearId);
		if (lLstLastScheduleGenData != null && lLstLastScheduleGenData.size() > 0) {
			Object[] lArrObj = (Object[]) lLstLastScheduleGenData.get(0);
			lLngLastScheduleGenMonth = (Long) lArrObj[0];
		}
		inputMap.put("LastSchedule", lLngLastScheduleGenMonth);

		List lMonthList = lObjChangeSubscriptionDAO.getMonths(lLngCurrFinYearId);
		inputMap.put("MonthList", lMonthList);

		if (lObjChangeSubscriptionDAO.requestDataAlreadyExists(strGpfAccNo) && userType.equals("DEO")) {
			return true;
		} else {
			return false;
		}
	}

	private List getHierarchyUsers(Map inputMap) {

		List UserList = null;

		try {

			setSessionInfo(inputMap);
			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();

			// Get the Subject Name
			String subjectName = gObjRsrcBndle.getString("GPF.ChangeSubRequest");

			// Get the Hierarchy Id
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId, subjectName, inputMap);

			// Get the From level Id
			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId, lLngHierRefId, inputMap);
			
			///to resolve the issue of multiple role to single user, passing level id on user type
			String userType = null;
			if(inputMap.get("userType") != null ){
				userType = inputMap.get("userType").toString();
				if(userType.equals("DEOAPP")){
					llFromLevelId = 20;
				}
				if(userType.equals("DEO")){
					llFromLevelId = 10;
				}
			}

			// Get the List of Post ID of the users at the next Level
			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId, lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			glogger.error("Error is :" + e, e);
		}
		return UserList;
	}

	private Boolean loadRefundableAdvance(String strGpfAccNo, String strUser, Map inputMap) {
		MstGpfAdvance savedRARequest = null;
		Long lngGpfAdvanceId = null;
		Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
		Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
		CmnAttachmentMpg cmnAttachmentMpg = null;
		gLngPostId = SessionHelper.getPostId(inputMap);
		Long lLngAttachmentId = null;
		try {

			GPFAdvanceProcessDAO lObjGPFAdvanceProcess = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());

			// if (strUser.equals("DEO")) {
			// lstAdvance = lObjGPFAdvanceProcess.getSavedAdvance(strGpfAccNo,
			// "RA", gStrPostId);
			// }
			String lStrAdvancePK = StringUtility.getParameter("pkValue", request);

			// if (!lStrAdvancePK.equals("") && !lStrAdvancePK.equals(null)) {
			// Long lLngAdvancePK = Long.parseLong(lStrAdvancePK);
			// lstAdvance =
			// lObjGPFAdvanceProcess.getAdvanceRequestForAdvanceId(strGpfAccNo,
			// lLngAdvancePK, "RA",
			// gStrPostId);
			//
			// }
			if (!lStrAdvancePK.equals("") && !lStrAdvancePK.equals(null)) {
				lngGpfAdvanceId = Long.parseLong(lStrAdvancePK);
				// } else if (lstAdvance != null && lstAdvance.size() > 0) {
				// lngGpfAdvanceId = (Long) lstAdvance.get(0);
				// }
				// if (lngGpfAdvanceId != null) {
				savedRARequest = (MstGpfAdvance) lObjGPFAdvanceProcess.read(lngGpfAdvanceId);

				// Added for viewing attachment
				CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv
						.getSessionFactory());
				CmnAttachmentMst lObjCmnAttachmentMst = null;

				if (savedRARequest.getAttachmentId() != null && savedRARequest.getAttachmentId().doubleValue() > 0) {
					lObjCmnAttachmentMst = new CmnAttachmentMst();
					lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(savedRARequest
							.getAttachmentId().toString()));

					cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

					if (lObjCmnAttachmentMst != null) {
						lLngAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
					}
					if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
						cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
					}
					cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();
					Long srNo = 0L;
					for (Integer lInt = 0; lInt < cmnAttachmentMpgs.size(); lInt++) {
						cmnAttachmentMpg = cmnAttachmentMpgIterator.next();

						srNo = cmnAttachmentMpg.getSrNo();
						inputMap.put("Proof", lObjCmnAttachmentMst);
						inputMap.put("ProofId", lLngAttachmentId);
						inputMap.put("ProofSrNo", srNo);

					}
				}

				inputMap.put("savedAdvanceRA", savedRARequest);
			}
			List lstPurposeCat = IFMSCommonServiceImpl.getLookupValues("Purpose Category", SessionHelper
					.getLangId(inputMap), inputMap);
			inputMap.put("lstPurposeCat", lstPurposeCat);
			List lstAdvanceNotRecovered = lObjGPFAdvanceProcess.getAdvanceNotRecovered(strGpfAccNo);
			inputMap.put("AdvanceNotRecovered", lstAdvanceNotRecovered);
			List lstOddInstallNo = IFMSCommonServiceImpl.getLookupValues("Odd Installment No", SessionHelper
					.getLangId(inputMap), inputMap);
			inputMap.put("OddInstallNo", lstOddInstallNo);
			if (lObjGPFAdvanceProcess.requestDataAlreadyExists(strGpfAccNo) && strUser.equals("DEO")) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			glogger.error(" Error in loadRefundableAdvance" + e, e);
		}
		return false;
	}

	private Boolean loadNonRefundableAdvance(String strGpfAccNo, String strUser, Map inputMap) {
		try {
			MstGpfAdvance savedRARequest = null;
			// Long lngGpfAdvanceId = null;
			gLngPostId = SessionHelper.getPostId(inputMap);
			Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
			Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
			CmnAttachmentMpg cmnAttachmentMpg = null;
			Long lLngAttachmentId = null;
			GPFAdvanceProcessDAO lObjGPFAdvanceProcess = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());

			// if (strUser.equals("DEO")) {
			// lstAdvance = lObjGPFAdvanceProcess.getSavedAdvance(strGpfAccNo,
			// "NRA", gStrPostId);
			// }
			String lStrAdvancePK = StringUtility.getParameter("pkValue", request);

			if (!lStrAdvancePK.equals("") && !lStrAdvancePK.equals(null)) {
				Long lLngAdvancePK = Long.parseLong(lStrAdvancePK);
				// lstAdvance =
				// lObjGPFAdvanceProcess.getAdvanceRequestForAdvanceId(strGpfAccNo,
				// lLngAdvancePK, "NRA",
				// gStrPostId);
				//
				// }
				//
				// if (lstAdvance != null && lstAdvance.size() > 0) {
				// lngGpfAdvanceId = (Long) lstAdvance.get(0);
				savedRARequest = (MstGpfAdvance) lObjGPFAdvanceProcess.read(lLngAdvancePK);
				Long lLongPurposeId = savedRARequest.getPurposeCategory();
				if (lLongPurposeId != null && (lLongPurposeId == 800024 || lLongPurposeId == 800025)) {
					List lLstSubtype = lObjGPFAdvanceProcess.getSubTypesForPurpose(lLongPurposeId);
					inputMap.put("lLstSubtype", lLstSubtype);
				}
				// Added for viewing added attachment
				CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv
						.getSessionFactory());
				CmnAttachmentMst lObjCmnAttachmentMst = null;

				if (savedRARequest.getAttachmentId() != null && savedRARequest.getAttachmentId().doubleValue() > 0) {
					lObjCmnAttachmentMst = new CmnAttachmentMst();
					lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(savedRARequest
							.getAttachmentId().toString()));

					cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

					if (lObjCmnAttachmentMst != null) {
						lLngAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
					}
					if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
						cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
					}
					cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();
					Long srNo = 0L;
					for (Integer lInt = 0; lInt < cmnAttachmentMpgs.size(); lInt++) {
						cmnAttachmentMpg = cmnAttachmentMpgIterator.next();

						srNo = cmnAttachmentMpg.getSrNo();
						inputMap.put("Proof1", lObjCmnAttachmentMst);
						inputMap.put("Proof1Id", lLngAttachmentId);
						inputMap.put("Proof1SrNo", srNo);

					}
				}
				inputMap.put("savedAdvanceRA", savedRARequest);
			}
			List lstPurposeCat = IFMSCommonServiceImpl.getLookupValues("Purpose Category NRA", SessionHelper
					.getLangId(inputMap), inputMap);
			inputMap.put("lstPurposeCat", lstPurposeCat);

			if (lObjGPFAdvanceProcess.requestDataAlreadyExists(strGpfAccNo) && strUser.equals("DEO")) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {

			glogger.error(" Error in loadRefundableAdvance" + e, e);
		}
		return false;
	}

	private Boolean loadFinalWithdrawal(String strGpfAccNo, String userType, Map inputMap) throws Exception {
		List finalWithDrawalList = null;
		Long lngGpfFinalWithdrawalId = 0L;
		TrnGpfFinalWithdrawal lObjFinalWithdrawal = null;
		Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
		Long lLngAttachmentId = null;
		Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
		CmnAttachmentMpg cmnAttachmentMpg = null;
		GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class, serv
				.getSessionFactory());

		// if (userType.equals("DEO")) {
		// finalWithDrawalList =
		// gpfFinalWithdrawalDAO.getFinalWithdrawal(strGpfAccNo);
		//
		// }
		String lStrFinalPK = StringUtility.getParameter("pkValue", request);
		if (!lStrFinalPK.equals("") && !lStrFinalPK.equals(null)) {
			Long lLngFinalWithdrawalId = Long.parseLong(lStrFinalPK);
			finalWithDrawalList = gpfFinalWithdrawalDAO.getFinalWithdrawalToDEOApprover(strGpfAccNo,
					lLngFinalWithdrawalId, gLngPostId);
		}
		if (finalWithDrawalList != null && finalWithDrawalList.size() > 0) {

			lngGpfFinalWithdrawalId = (Long) finalWithDrawalList.get(0);

			lObjFinalWithdrawal = (TrnGpfFinalWithdrawal) gpfFinalWithdrawalDAO.read(lngGpfFinalWithdrawalId);
			inputMap.put("finalWithDrawal", lObjFinalWithdrawal);
			CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv
					.getSessionFactory());
			CmnAttachmentMst lObjCmnAttachmentMst = null;

			if (lObjFinalWithdrawal.getAttachmentId() != null
					&& lObjFinalWithdrawal.getAttachmentId().doubleValue() > 0) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjFinalWithdrawal
						.getAttachmentId().toString()));

				cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

				if (lObjCmnAttachmentMst != null) {
					lLngAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
				}
				if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
					cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
				}
				cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();
				Long srNo = 0L;
				for (Integer lInt = 0; lInt < cmnAttachmentMpgs.size(); lInt++) {
					cmnAttachmentMpg = cmnAttachmentMpgIterator.next();

					srNo = cmnAttachmentMpg.getSrNo();
					inputMap.put("Proof2", lObjCmnAttachmentMst);
					inputMap.put("Proof2Id", lLngAttachmentId);
					inputMap.put("Proof2SrNo", srNo);

				}
			}
		}
		List lstFinalReasons = IFMSCommonServiceImpl.getLookupValues("Final Payment Reason", SessionHelper
				.getLangId(inputMap), inputMap);
		inputMap.put("lstFinalReasons", lstFinalReasons);

		if (gpfFinalWithdrawalDAO.requestDataAlreadyExists(strGpfAccNo) && userType.equals("DEO")) {
			return true;
		} else {
			return false;
		}
	}

	public ResultObject loadGPFRequestList(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			setSessionInfo(inputMap);
			String lStrUser = StringUtility.getParameter("userType", request);
			String lStrCriteria = StringUtility.getParameter("criteria", request);
			String lStrName = StringUtility.getParameter("name", request);
			String lStrDate = StringUtility.getParameter("date", request);
			inputMap.put("UserType", lStrUser);
			Date lDtSaveDate = null;
			if (!lStrDate.equals("")) {
				lDtSaveDate = sdf.parse(lStrDate);
			}
			GPFRequestProcessDAO lObjGPFReqProcess = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			String lStrLocationCode = lObjGPFReqProcess.getLocationCodeOfUser(gLngPostId);
			List empListForDeoAppover = lObjGPFReqProcess.getGPFRequestList(lStrUser, gStrPostId, lStrLocationCode,
					lStrCriteria, lStrName, lDtSaveDate);
			inputMap.put("CaseList", empListForDeoAppover);

			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFWorklist");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}

		return resObj;
	}

	public ResultObject loadDraftRequestList(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstDraftReq = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			setSessionInfo(inputMap);

			GPFRequestProcessDAO lObjGPFReqProcess = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			String lStrCriteria = StringUtility.getParameter("criteria", request);
			String lStrName = StringUtility.getParameter("name", request);
			String lStrDate = StringUtility.getParameter("date", request);
			String lStrDdoCode = lObjGPFReqProcess.getDdoCode(gStrLocationCode);
			Date lDtSaveDate = null;
			if (!lStrDate.equals("")) {
				lDtSaveDate = sdf.parse(lStrDate);
			}

			lLstDraftReq = lObjGPFReqProcess.getDraftRequestList(gStrPostId, lStrDdoCode, lStrCriteria, lStrName,
					lDtSaveDate);

			inputMap.put("lLstDraftReq", lLstDraftReq);

			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFDraftRequests");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}

		return resObj;
	}

	public ResultObject discardGPFRequest(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;

		try {
			setSessionInfo(inputMap);

			String lStrReqTypes = StringUtility.getParameter("reqTypes", request);
			String lStrReqPKs = StringUtility.getParameter("reqPKs", request);
			String[] strArrReqType = lStrReqTypes.split("~");
			String[] strArrReqPK = lStrReqPKs.split("~");

			String lStrReqType = null;
			Long lLngReqPK = null;

			GPFRequestProcessDAO lObjGPFRequestProcessDAO = null;
			for (int index = 0; index < strArrReqType.length; index++) {

				lStrReqType = strArrReqType[index];
				lLngReqPK = Long.parseLong(strArrReqPK[index]);

				if (lStrReqType.equals("CS")) {

					lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfEmpSubscription.class, serv
							.getSessionFactory());
					MstGpfEmpSubscription lObjMstGpfEmpSubscription = (MstGpfEmpSubscription) lObjGPFRequestProcessDAO
							.read(lLngReqPK);

					lObjMstGpfEmpSubscription.setStatusFlag("X");
					lObjMstGpfEmpSubscription.setUpdatedUserId(gLngUserId);
					lObjMstGpfEmpSubscription.setUpdatedPostId(gLngPostId);
					lObjMstGpfEmpSubscription.setUpdatedDate(gDtCurDate);

					lObjGPFRequestProcessDAO.update(lObjMstGpfEmpSubscription);

				} else if (lStrReqType.equals("RA") || lStrReqType.equals("NRA")) {
					lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfAdvance.class, serv
							.getSessionFactory());
					MstGpfAdvance lObjMstGpfAdvance = (MstGpfAdvance) lObjGPFRequestProcessDAO.read(lLngReqPK);

					lObjMstGpfAdvance.setStatusFlag("X");
					lObjMstGpfAdvance.setUpdatedUserId(gLngUserId);
					lObjMstGpfAdvance.setUpdatedPostId(gLngPostId);
					lObjMstGpfAdvance.setUpdatedDate(gDtCurDate);

					lObjGPFRequestProcessDAO.update(lObjMstGpfAdvance);

				} else if (lStrReqType.equals("FW")) {
					lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(TrnGpfFinalWithdrawal.class, serv
							.getSessionFactory());
					TrnGpfFinalWithdrawal lObjTrnGpfFinalWithdrawal = (TrnGpfFinalWithdrawal) lObjGPFRequestProcessDAO
							.read(lLngReqPK);

					lObjTrnGpfFinalWithdrawal.setReqStatus("X");
					lObjTrnGpfFinalWithdrawal.setUpdatedUserId(gLngUserId);
					lObjTrnGpfFinalWithdrawal.setUpdatedPostId(gLngPostId);
					lObjTrnGpfFinalWithdrawal.setUpdatedDate(gDtCurDate);

					lObjGPFRequestProcessDAO.update(lObjTrnGpfFinalWithdrawal);

				}
			}

			lBlFlag = true;

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, ex, "Error is: ");
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject loadWithdrawalDetailList(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request);
			String lStrAdvanceType = StringUtility.getParameter("advanceType", request);
			GPFRequestProcessDAO lObjGPFReqProcess = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			List withdrawalList = lObjGPFReqProcess.getWithdrawalDetail(lStrGpfAccNo, lStrAdvanceType);
			inputMap.put("WithdrawalDetail", withdrawalList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("WithdrawalDetail");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}

		return resObj;

	}

	public ResultObject loadAdvanceDetailList(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request);
			String lStrAdvanceType = StringUtility.getParameter("advanceType", request);
			GPFRequestProcessDAO lObjGPFReqProcess = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			List advanceList = lObjGPFReqProcess.getAdvanceDetail(lStrGpfAccNo, lStrAdvanceType);
			inputMap.put("AdvanceDetail", advanceList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("AdvanceDetail");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}

		return resObj;

	}

	private Boolean checkFinalEligibility(Date lDtSuperAnnDate) {

		Boolean result = false;
		new SimpleDateFormat("dd/MM/yyyy");
		if (lDtSuperAnnDate != null) {
			try {
				Calendar lCalSuperAnnDateMinus1Year = Calendar.getInstance();
				Calendar lCalToday = Calendar.getInstance();
				lCalSuperAnnDateMinus1Year.setTime(lDtSuperAnnDate);
				lCalSuperAnnDateMinus1Year.add(Calendar.YEAR, -1);

				if (lCalToday.after(lCalSuperAnnDateMinus1Year)) {
					result = true;
				}

			} catch (Exception e) {
				glogger.error("Error is :" + e, e);
			}
		}
		return result;
	}

	private Boolean checkRefundableAdvanceEligibility(Date lDtSuperAnnDate, Date lDtJoiningDate) {

		Boolean result = false;
		if (lDtSuperAnnDate != null) {
			try {
				Calendar lCalSuperAnnDateMinus1Year = Calendar.getInstance();
				Calendar lCalJoiningDateMinus1Year = Calendar.getInstance();
				Calendar lCalToday = Calendar.getInstance();
				lCalSuperAnnDateMinus1Year.setTime(lDtSuperAnnDate);
				lCalSuperAnnDateMinus1Year.add(Calendar.YEAR, -1);
				// lCalSuperAnnDateMinus1Year.add(Calendar.MONTH, -3);

				lCalJoiningDateMinus1Year.setTime(lDtJoiningDate);
				lCalJoiningDateMinus1Year.add(Calendar.YEAR, 1);

				if (lCalToday.after(lCalSuperAnnDateMinus1Year) || lCalToday.before(lCalJoiningDateMinus1Year)) {
					result = true;
				}

			} catch (Exception e) {
				glogger.error("Error is :" + e, e);
			}
		}
		return result;
	}

	private Boolean checkChangeSubscriptionEligibility(Date lDtSuperAnnDate, Date lDtJoiningDate) {

		Boolean result = false;
		if (lDtSuperAnnDate != null) {
			try {
				Calendar lCalSuperAnnDateMinus1Year = Calendar.getInstance();
				Calendar lCalJoiningDateMinus1Year = Calendar.getInstance();
				Calendar lCalToday = Calendar.getInstance();
				lCalSuperAnnDateMinus1Year.setTime(lDtSuperAnnDate);
				lCalSuperAnnDateMinus1Year.add(Calendar.MONTH, -3);

				lCalJoiningDateMinus1Year.setTime(lDtJoiningDate);
				lCalJoiningDateMinus1Year.add(Calendar.YEAR, 1);

				if (lCalToday.after(lCalSuperAnnDateMinus1Year) || lCalToday.before(lCalJoiningDateMinus1Year)) {
					result = true;
				}

			} catch (Exception e) {
				glogger.error("Error is :" + e, e);
			}
		}
		return result;
	}

	public ResultObject populateNonRefundSubType(Map<String, Object> inputMap) {
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstNonRefundType = null;

		try {
			setSessionInfo(inputMap);
			GPFAdvanceProcessDAO lObjAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(null, serv.getSessionFactory());

			Long lLngNonRefundType = Long.parseLong(StringUtility.getParameter("cmbNonRefundType", request));

			lLstNonRefundType = lObjAdvanceProcessDAO.getSubType(lLngNonRefundType);

			String lStrTempResult = null;
			if (lLstNonRefundType != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lLstNonRefundType, "desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;
	}

	public ResultObject getEmpNameForAutoComplete(Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String lStrEmpName = null;
		String lStrUserType = null;
		String lStrDdoCode = null;
		try {
			setSessionInfo(inputMap);

			lStrEmpName = StringUtility.getParameter("searchKey", request).trim();
			lStrUserType = StringUtility.getParameter("userType", request).trim();

			GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());

			if (lStrUserType.equals("") || lStrUserType == null) {
				lStrDdoCode = lObjGPFRequestProcessDAO.getDdoCode(gStrLocationCode);
			} else if (lStrUserType.equals("DDO")) {
				lStrDdoCode = lObjGPFRequestProcessDAO.getDdoCodeForDDO(gLngPostId);
			} else {
				Long lLngDdoPostId = lObjGPFRequestProcessDAO.getDDOPostIdForVerifierHo(gLngPostId, lStrUserType);
				lStrDdoCode = lObjGPFRequestProcessDAO.getDdoCodeForDDO(lLngDdoPostId);
			}
			finalList = lObjGPFRequestProcessDAO.getEmpNameForAutoComplete(lStrEmpName, lStrDdoCode);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList, "desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, objRes, ex, "Error is: ");
			return objRes;
		}

		return objRes;

	}

	public ResultObject checkForDataEntry(Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;

		try {
			setSessionInfo(inputMap);
			GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(TrnEmpGpfAcc.class, serv
					.getSessionFactory());
			String lStrSevaarthId = StringUtility.getParameter("EmpCode", request);
			String lStrName = StringUtility.getParameter("Name", request);
			
			if(lStrSevaarthId != null && lStrSevaarthId.length() > 0){
				if (lObjGPFRequestProcessDAO.isDataEntryComplete(lStrSevaarthId)) {
					lBlFlag = true;
				}
			}else if(lStrName != null && lStrName.length() > 0){
				lStrSevaarthId = lObjGPFRequestProcessDAO.getSevaarthIdFromName(lStrName);
				if(!lStrSevaarthId.equals("")){
					if (lObjGPFRequestProcessDAO.isDataEntryComplete(lStrSevaarthId)) {
						lBlFlag = true;
					}
				}
			}
			
			
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, objRes, ex, "Error is: ");
			return objRes;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
		inputMap.put("ajaxKey", lStrResult);

		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;
	}

}
