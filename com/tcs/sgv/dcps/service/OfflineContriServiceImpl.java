/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAO;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.valueobject.HstDcpsContribution;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

public class OfflineContriServiceImpl extends ServiceImpl implements
OfflineContriService {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			session = request.getSession();
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}
	private void setSessionInfoSchdlr(Map inputMap) {

		try {
			//request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			//session = (Session)inputMap.get("currentSession");
			Map  SchedlrLoginMap =(HashMap)inputMap.get("baseLoginMap");
			gStrPostId = String.valueOf(SchedlrLoginMap.get("postId"));
			gLngPostId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("postId")));
			gStrLocationCode =String.valueOf(SchedlrLoginMap.get("locationCode"));
			gLngUserId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("userId")));
			gStrUserId = String.valueOf(SchedlrLoginMap.get("userId"));
			gLngDBId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("dbId")));
			gDtCurDate = new Date();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	public ResultObject loadOfflineDCPSForm(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		List BillGroupList = null;
		Long monthId = null;
		Long yearId = null;
		String lStrMissingCreditOrNot = null;
		String FirstTimeMissingCreditLoad = null;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			// Gets Element Id and puts in the Map
			String hidElementId = StringUtility.getParameter("elementId",request);//700145
			
			gLogger.info("hidElementId ::"+hidElementId);//chk
			
			inputMap.put("hidElementId", hidElementId);

			/* Initializes the DAOs */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Get All the Bill Groups under selected DDO (As of now shown all) */
			//	List lLstBillGroups = lObjDcpsCommonDAO.getBillGroups();

			/* Get Months */
			List lLstMonths = lObjDcpsCommonDAO.getMonths();

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();
			
			
			List lLstYearsForMissing = lObjDcpsCommonDAO.getFinyear();

			List lLstDelayedYears = lObjDcpsCommonDAO.getFinyearsForDelayedType();

			/* Get User(ATO or TO) and Use type */
			String lStrUserType = StringUtility.getParameter("User", request).trim();//DDOAsst

			String lStrUseType = StringUtility.getParameter("Use", request).trim();//ViewAll

			String lStrContiType = null;

			if (!StringUtility.getParameter("Type", request).equalsIgnoreCase("")&& StringUtility.getParameter("Type", request) != null) {
				lStrContiType = StringUtility.getParameter("Type", request);//online
			}

			inputMap.put("ContriType", lStrContiType);

			List treasuries = null;
			List ddonames = null;

			if (lStrUserType.equals("DDOAsst")) {
				lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
				treasuries = lObjOfflineContriDAO.getTreasuryForDDO(lStrDdoCode);
				ddonames = lObjOfflineContriDAO.getDdoNameFromDdoCode(lStrDdoCode);
				if (lStrUseType.equals("ViewRejected")) {//lStrUserType=ViewAll
					BillGroupList = lObjOfflineContriDAO
					.getBillGroupsRejectedForDdo(lStrDdoCode);
				} else {
					BillGroupList = lObjOfflineContriDAO
					.getBillGroupsRegularForDdoInDDOAsstLogin(lStrDdoCode);
				}
			} else if (lStrUserType.equals("DDO")) {

				lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
				treasuries = lObjOfflineContriDAO.getTreasuryForDDO(lStrDdoCode);
				ddonames = lObjOfflineContriDAO.getDdoNameFromDdoCode(lStrDdoCode);
				if (lStrUseType.equals("ViewApproved")) {
					BillGroupList = lObjOfflineContriDAO
					.getApprovedBillGroupsForDdoInDDOLogin(lStrDdoCode);
				} else {
					BillGroupList = lObjOfflineContriDAO
					.getBillGroupsForDdoInDDOLogin(lStrDdoCode);
				}

			} else {
				treasuries = lObjOfflineContriDAO.getCurrentTreasury(gStrLocationCode);
				if (lStrUserType.equals("TO") && lStrUseType.equals("MissingCreditEntry") ) {
					ddonames = lObjOfflineContriDAO.getAllDDOWOBrackets(gStrLocationCode);
				} 
				else if(lStrUserType.equals("TO") && lStrUseType.equals("ViewApprovedByTO")) {
					ddonames = lObjOfflineContriDAO.getAllDDOForApprovedContriInTO(gStrLocationCode);
				}
				else {
					ddonames = lObjOfflineContriDAO
					.getAllDDOForContriForwardedToTO(gStrLocationCode);
				}
			}

			inputMap.put("TREASURIES", treasuries);
			inputMap.put("DDONAMES", ddonames);

			/*
			 * Checks if request is sent by click of GO button or the page is
			 * loaded for the first time.
			 */

			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

			String schemeCode = null;
			String schemename = null;

			/* Get All Types Of Payment From Lookup */
			List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues("TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap),inputMap);

			//listTypeOfPayment.add();
//			Iterator it = listTypeOfPayment.iterator();
//            while (it.hasNext())
//            {
//              Object[] tuple = (Object[])it.next();
//              System.out.println("-->"+tuple[0].toString());
//            }

			

			//listTypeOfPayment=Regular,Delayed,DA Arrear,Pay Arrear

			// Regular Payment type removed for DDO Assistant and DDO

			if (lStrUserType.equals("DDOAsst"))
			{
				listTypeOfPayment.remove(0);
				
			}
			//gLogger.info("listTypeOfPayment.remove(0) ::"+) noob;
			String lStrTypeOfPaymentMaster = StringUtility.getParameter("typeOfPaymentMaster", request).trim();
			if ("".equals(lStrTypeOfPaymentMaster)) {
				lStrTypeOfPaymentMaster = "700046"; // By default Regular Type
			}
			inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster.trim());//700046

			//Set the Month Id and Year Id for the current month and year from the system date for the first time load.
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String lStrCurDate = null;
			if(StringUtility.getParameter("cmbMonth", request).trim().equals(""))//setParameter("cmbMonth") ??
			{                                                                    //jsp page line 631 setParameter("cmbMonth")          
				lStrCurDate = sdf.format(gDtCurDate);
				if(lStrCurDate != null && !"".equals(lStrCurDate))
				{
					monthId = Long.valueOf(lStrCurDate.substring(3, 5));
					// month Id hard-coded to April temporarily so that DDOs dont forward May's contributions by mistake instead of April Month 
					monthId = 4l;
				}
			}
			if(StringUtility.getParameter("cmbYear", request).trim().equals(""))
			{
				if(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate) != null && !"".equals(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate)))
				{
					yearId = Long.valueOf(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate));
				}
			}

			if (lStrUserType.equals("TO") && lStrUseType.equals("MissingCreditEntry")) {
				BillGroupList = new ArrayList<Object>();
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
				lObjComboValuesVO.setDesc("Missing Credit");
				BillGroupList.add(lObjComboValuesVO);
				lStrMissingCreditOrNot = "Yes";
				FirstTimeMissingCreditLoad = "Yes";
			}

			inputMap.put("FirstTimeMissingCreditLoad", FirstTimeMissingCreditLoad);

			if (StringUtility.getParameter("cmbBillGroup", request) != null
					&& !StringUtility.getParameter("cmbBillGroup", request)
					.equalsIgnoreCase("")) {

				/* Get the DDO Code */
				lStrDdoCode = StringUtility.getParameter("cmbDDOCode", request).trim();

				if (lStrUserType.equals("DDO")) {
					if (lStrUseType.equals("ViewApproved")) {
						BillGroupList = lObjOfflineContriDAO
						.getApprovedBillGroupsForDdoInDDOLogin(lStrDdoCode);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInDDOLogin(lStrDdoCode);
					}
				} else if (lStrUserType.equals("TO")) {
					if (lStrUseType.equals("ViewReverted")) {
						String lStrBillGroupIdFromReversion = StringUtility
						.getParameter("cmbBillGroup", request);
						String lStrBillGroupDescFromReversion = StringUtility
						.getParameter("cmbBillGroupDesc", request);
						ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(lStrBillGroupIdFromReversion);
						lObjComboValuesVO.setDesc(lStrBillGroupDescFromReversion);
						BillGroupList = new ArrayList<Object>();
						BillGroupList.add(lObjComboValuesVO);
					} 
					else if (lStrUseType.equals("MissingCreditEntry")) {
						BillGroupList = new ArrayList<Object>();
						ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId("-1");
						lObjComboValuesVO.setDesc("--Select--");
						lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
						lObjComboValuesVO.setDesc("Missing Credit");
						BillGroupList.add(lObjComboValuesVO);
						lStrMissingCreditOrNot = "Yes";
					}
					else if(lStrUseType.equals("ViewApprovedByTO"))
					{
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInTOLoginInclMisngCrdt(lStrDdoCode);
					}
					else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInTOLogin(lStrDdoCode);
					}
				} else if (lStrUserType.equals("DDOAsst")) {

					if (lStrUseType.equals("ViewRejected")) {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsRejectedForDdo(lStrDdoCode);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdo(lStrDdoCode);
					}
				} else if (lStrUserType.equals("ATO")) {
					if (lStrUseType.equals("ViewRejected")) {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsRejectedForDdoInATOLogin(lStrDdoCode);
					} 
					else
					{}
				}

				/*
				 * Get Month Id,Year Id,Bill Group Id,Treasury Code,Scheme
				 * Name,Scheme Code from Request
				 */
				monthId = Long.parseLong(StringUtility.getParameter("cmbMonth", request));
				yearId = Long.parseLong(StringUtility.getParameter("cmbYear", request));
				Long lLongbillGroupId = Long.valueOf(StringUtility.getParameter("cmbBillGroup", request));
				Long treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request));

				if (!StringUtility.getParameter("schemeName", request).trim().equalsIgnoreCase("")
						&& StringUtility.getParameter("schemeName", request) != null) {
					schemename = StringUtility.getParameter("schemeName",request).trim();
				}
				if (!StringUtility.getParameter("schemeCode", request).trim()
						.equalsIgnoreCase("")
						&& StringUtility.getParameter("schemeCode", request) != null) {
					schemeCode = StringUtility.getParameter(
							"schemeCode", request).toString().trim();
				}

				// Code Added for checking previous month's contribution entry

				DcpsCommonDAO lObjDcpsCommonDAOForFinYear = new DcpsCommonDAOImpl(
						SgvcFinYearMst.class, serv.getSessionFactory());
				SgvcFinYearMst lObjSgvcFinYearMst = null;
				lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAOForFinYear.read(yearId);

				Long previousMonthId = null;
				Long previousYearId = null;
				if (monthId == 1) {
					previousMonthId = 12l;
				} else {
					previousMonthId = monthId - 1;
				}

				if (monthId == 4) {
					previousYearId = lObjSgvcFinYearMst.getPrevFinYearId();
				} else {
					previousYearId = yearId;
				}

				String contributionsForPrvsMonth = "NO";
				String voucherDtlsForPrvsMonth = "NO";

				MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtlsForPrvsYear = lObjOfflineContriDAO.checkContributionsForPrvsMonth(previousYearId,
						previousMonthId, lStrDdoCode, lLongbillGroupId);

				if (lObjMstDcpsContriVoucherDtlsForPrvsYear != null) {
					contributionsForPrvsMonth = "YES";
				}

				if (lObjMstDcpsContriVoucherDtlsForPrvsYear != null) {
					if (lObjMstDcpsContriVoucherDtlsForPrvsYear.getVoucherNo() != null
							&& lObjMstDcpsContriVoucherDtlsForPrvsYear
							.getVoucherDate() != null) {
						voucherDtlsForPrvsMonth = "YES";
					}
				}

				inputMap.put("contributionsForPrvsMonth",contributionsForPrvsMonth);
				inputMap.put("voucherDtlsForPrvsMonth", voucherDtlsForPrvsMonth);

				inputMap.put("previousMonthId", previousMonthId);
				inputMap.put("previousYearId", previousYearId);

				// Code overs for checking previous month's contribution entry

				/* Get Year code from Year Id */
				String yearCode = lObjDcpsCommonDAO
				.getYearCodeForYearId(yearId);

				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearCode);

				if (lIntMonth == 1 || lIntMonth == 2 || lIntMonth == 3) {
					lIntYear += 1;
				}

				/*
				 * Get First Date and Last Date of Month For Selected Year and
				 * Month
				 */
				Date lDtLastDate = null;
				Date lDtFirstDate = null;

				Integer lIntDelayedMonth = null;
				Long lLongDelayedYearId = null;
				Integer lIntDelayedYear = null;
				Date lDtLastDateDelayed = null;
				Date lDtFirstDateDelayed = null;
				Long delayedMonthId = null;
				Long delayedYearId = null;

				if (lStrTypeOfPaymentMaster.equals("700047")) // Delayed Type
					// payment
				{
					if ((lStrUserType.equals("DDOAsst") || lStrUserType
							.equals("ATO"))
							&& (lStrUseType.equals("ViewAll"))) {
						lIntDelayedMonth = Integer
						.parseInt(StringUtility.getParameter(
								"cmbDelayedMonth", request).trim());
						lLongDelayedYearId = Long
						.parseLong(StringUtility.getParameter(
								"cmbDelayedYear", request).trim());
						lIntDelayedYear = Integer
						.parseInt(lObjDcpsCommonDAO
								.getYearCodeForYearId(lLongDelayedYearId));

						delayedMonthId = Long.valueOf(StringUtility.getParameter(
								"cmbDelayedMonth", request).trim());
						delayedYearId = Long.valueOf(StringUtility.getParameter(
								"cmbDelayedYear", request).trim());

						inputMap.put("delayedmonthId", lIntDelayedMonth);
						inputMap.put("delayedyearId", lLongDelayedYearId);

						if (lIntDelayedMonth == 1 || lIntDelayedMonth == 2
								|| lIntDelayedMonth == 3) {
							lIntDelayedYear += 1;
						}

						lDtLastDateDelayed = lObjDcpsCommonDAO
						.getLastDate(lIntDelayedMonth - 1,
								lIntDelayedYear);
						lDtFirstDateDelayed = lObjDcpsCommonDAO
						.getFirstDate(lIntDelayedMonth - 1,
								lIntDelayedYear);

						inputMap.put("FirstDateDelayed", lDtFirstDateDelayed);
						inputMap.put("LastDateDelayed", lDtLastDateDelayed);

						if (lIntDelayedMonth == 1) {
							lIntDelayedYear--;
						}
					}
				}

				lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1,
						lIntYear);
				lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 1,
						lIntYear);

				Boolean lBlFlagBillGenerated = false;

				if (lStrUserType.equals("DDOAsst")) 
				{
					lBlFlagBillGenerated = lObjOfflineContriDAO.checkIfBillAlreadyGenerated(lLongbillGroupId, monthId,Long.valueOf(lIntYear.toString()));
				}
				inputMap.put("lBlFlagBillGenerated", lBlFlagBillGenerated);

				// Below code commented as seems of no use
				/*
					if (lIntMonth == 1) {
						lIntYear--;
					}

					Date lDtDelFirstDate = lObjDcpsCommonDAO.getFirstDate(
							lIntMonth - 2, lIntYear);
					Date lDtDelLastDate = lObjDcpsCommonDAO.getLastDate(
							lIntMonth - 2, lIntYear);
				 */

				/* Get All Pay Commissions from Lookup */
				List<CmnLookupMst> listPayCommissionOld = IFMSCommonServiceImpl.getLookupValues("PayCommissionDCPS", SessionHelper.getLangId(inputMap), inputMap);

				List<CmnLookupMst> listPayCommission = new ArrayList<CmnLookupMst>();
				CmnLookupMst tempCmnLookupMst = null;

				for (CmnLookupMst lObjCommonLookupMst : listPayCommissionOld) {

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals("700015")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700015l);
						tempCmnLookupMst.setLookupDesc("5 PC");
						listPayCommission.add(tempCmnLookupMst);
					}
					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals("700016")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700016l);
						tempCmnLookupMst.setLookupDesc("6 PC");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals("700338")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700338l);
						tempCmnLookupMst.setLookupDesc("NonGovt");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals("700339")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700339l);
						tempCmnLookupMst.setLookupDesc("Padmanabhan");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals("700340")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700340l);
						tempCmnLookupMst.setLookupDesc("Fourth(IV)");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals("700345")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700345l);
						tempCmnLookupMst.setLookupDesc("Shetty");
						listPayCommission.add(tempCmnLookupMst);
					}
					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals("700349")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700349l);
						tempCmnLookupMst.setLookupDesc("7 PC");
						listPayCommission.add(tempCmnLookupMst);
					}

				}

				inputMap.put("listPayCommission", listPayCommission);

				/* Puts the Edit form option's provision. */

				List UserList = null;

				if (lStrUserType.equals("ATO")
						|| lStrUserType.equals("DDOAsst")
						|| (lStrUserType.equals("TO") && (lStrUseType.equals("ViewReverted")|| lStrUseType.equals("MissingCreditEntry")))) {
					inputMap.put("EditForm", "Y");
					// UserList = getHierarchyUsers(inputMap, lStrUserType);

				} else if ((lStrUserType.equals("TO") || lStrUserType
						.equals("DDO"))
						&& !lStrUseType.equals("ViewReverted")) {
					inputMap.put("EditForm", "N");
				}

				if (lStrUserType.equals("ATO")
						|| lStrUserType.equals("DDOAsst")
						|| lStrUserType.equals("DDO")) {
					UserList = getHierarchyUsers(inputMap, lStrUserType);
				}

				inputMap.put("UserList", UserList);

				/*
				 * Gets the Employees' contribution List for selected month and
				 * year and puts in the Map
				 */

				Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

				List empList = null;

				SimpleDateFormat sdf1 = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
				String lStrFirstDate = null;
				String lStrFirstDateMainMonth = null;

				if(lDtFirstDateDelayed != null) // It will not be null when Delayed Type Payment is selected by DDO Asst or ATO.
				{
					lStrFirstDate = sdf1.format(lDtFirstDateDelayed);
				}
				else
				{
					lStrFirstDate = sdf1.format(lDtFirstDate);
				}

				lStrFirstDateMainMonth = sdf1.format(lDtFirstDate);

				String lStrLastDate = null;
				if(lDtLastDateDelayed != null) // It will not be null when Delayed Type Payment is selected by DDO Asst or ATO.
				{
					lStrLastDate = sdf1.format(lDtLastDateDelayed);
				}
				else
				{
					lStrLastDate = sdf1.format(lDtLastDate);
				}

				empList = lObjOfflineContriDAO.getEmpListForContributionFinal(
						lStrDdoCode, lLongbillGroupId, monthId, yearId,
						lStrUserType, lStrUseType, gStrPostId, displayTag,
						lStrFirstDate, lStrTypeOfPaymentMaster,delayedMonthId,delayedYearId,lStrLastDate,lStrFirstDateMainMonth,schemeCode);
				inputMap.put("totalRecords", empList.size());

				if (empList.size() == 0) {
					empList = null;
				}

				// Gets voucherDtlsVO

				lObjMstDcpsContriVoucherDtls = lObjOfflineContriDAO
				.getContriVoucherVOForInputDtls(yearId, monthId,
						lStrDdoCode, lLongbillGroupId);
				inputMap.put("lObjMstDcpsContriVoucherDtls",
						lObjMstDcpsContriVoucherDtls);

				List lListVoucherNoAndDate = lObjOfflineContriDAO.getVoucherNoAndDateForInputDtls(yearId, monthId, lStrDdoCode, lLongbillGroupId);
				Long lLongVoucherNo = null;
				Date lDateVoucherDate = null;

				if(lListVoucherNoAndDate != null)
				{
					if(lListVoucherNoAndDate.size() != 0)
					{
						Object[] lObjVoucherNoAndDate = null;
						lObjVoucherNoAndDate = (Object[]) lListVoucherNoAndDate.get(0);
						if(lObjVoucherNoAndDate[0] != null)
						{
							if(!"".equals(lObjVoucherNoAndDate[0].toString().trim()))
								lLongVoucherNo = Long.valueOf(lObjVoucherNoAndDate[0].toString().trim());
						}
						if(lObjVoucherNoAndDate[1] != null)
						{
							if(!"".equals(lObjVoucherNoAndDate[1].toString().trim()))
								lDateVoucherDate = sdf1.parse(lObjVoucherNoAndDate[1].toString().trim());
						}
					}
				}

				inputMap.put("voucherNo", lLongVoucherNo);
				inputMap.put("voucherDate", lDateVoucherDate);

				/* Puts All Above Values in InputMap */

				inputMap.put("lLongbillGroupId", lLongbillGroupId);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("schemename", schemename);
				inputMap.put("schemeCode", schemeCode);
				inputMap.put("FirstDate", lDtFirstDate);
				inputMap.put("LastDate", lDtLastDate);

				inputMap.put("schdlStartDate", sdf1.format(lDtFirstDate));
				inputMap.put("schdlEndDate", sdf1.format(lDtLastDate));

				// Below code commented as seems of no use.

				/*
				inputMap.put("DelFirstDate", lDtDelFirstDate);
				inputMap.put("DelLastDate", lDtDelLastDate);
				 */
				String elementId= StringUtility.getParameter("elementId",request).trim();
				gLogger.info(" elementId is : " +elementId);
				inputMap.put("elementId", elementId);
				inputMap.put("empList", empList);
				inputMap.put("GoPressed", "Y");

			}

			// Adds Contribution Through Challan as a bill group

			/*
			String lStrContriThruChallanOrNot = StringUtility.getParameter(
					"contriThruChallanOrNot", request);
			inputMap.put("ContriThruChallanOrNot", lStrContriThruChallanOrNot);
			inputMap.put("hidBGIdForContriThruChallan", gObjRsrcBndle
					.getString("DCPS.BGIdForContriThruChallan"));
			if (BillGroupList == null) {
				BillGroupList = new ArrayList<Object>();
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				BillGroupList.add(lObjComboValuesVO);
			}
			/*
			// Contribution thru challan removed
			/*ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForContriThruChallan"));
			lObjComboValuesVO.setDesc("Contribution Through Challan");
			BillGroupList.add(lObjComboValuesVO);
			 */

			/*
			if(lStrMissingCreditOrNot == null)
			{
				lStrMissingCreditOrNot = StringUtility.getParameter("MissingCreditOrNot", request).trim();
			}
			 */
			inputMap.put("MissingCreditOrNot", lStrMissingCreditOrNot);
			inputMap.put("hidBGIdForMissingCredit", gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));

			inputMap.put("BillGroupList", BillGroupList);

			inputMap.put("monthId", monthId);
			inputMap.put("yearId", yearId);
			inputMap.put("lStrDDOCode", lStrDdoCode);
			inputMap.put("YEARS", lLstYears);
			inputMap.put("YEARSMISSING", lLstYearsForMissing);
			inputMap.put("DELAYEDYEARS", lLstDelayedYears);
			//	inputMap.put("BILLGROUPS", lLstBillGroups);
			inputMap.put("MONTHS", lLstMonths);
			inputMap.put("lStrUser", lStrUserType);
			inputMap.put("lStrUse", lStrUseType);

			inputMap.put("listTypeOfPayment", listTypeOfPayment);

			resObj.setResultValue(inputMap);

			//	if(inputMap.get("fromPayBillScheduler")==null) // For Scheuler only 
			resObj.setViewName("DCPSOfflineEntryForm");//dcpsOfflineEntry.jsp and OfflineEntry.jsp


		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject loadOfflineDCPSFormSchdlr(Map inputMap) throws Exception
	{

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		List BillGroupList = null;
		Long monthId = null;
		Long yearId = null;

		try {

			/* Sets the Session Information */
			setSessionInfoSchdlr(inputMap);

			// Gets Element Id and puts in the Map
			/*String hidElementId = StringUtility.getParameter("elementId",request);
		inputMap.put("hidElementId", hidElementId);
			 */
			/* Initializes the DAOs */

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			/* Get All the Bill Groups under selected DDO (As of now shown all) */
			//	List lLstBillGroups = lObjDcpsCommonDAO.getBillGroups();

			/* Get Months */
			List lLstMonths = lObjDcpsCommonDAO.getMonths();

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();

			/* Get User(ATO or TO) and Use type */
			//String lStrUserType = StringUtility.getParameter("User", request).trim();

			String lStrUserType = inputMap.get("User") != null ? inputMap.get("User").toString():"";

			//String lStrUseType = StringUtility.getParameter("Use", request).trim();

			String lStrUseType = inputMap.get("Use") != null ? inputMap.get("Use").toString():"";

			String lStrContiType = null;

			lStrContiType = inputMap.get("Type") != null ? inputMap.get("Type").toString():"";

			/*
		if (!StringUtility.getParameter("Type", request).equalsIgnoreCase(
				"")
				&& StringUtility.getParameter("Type", request) != null) {
			lStrContiType = StringUtility.getParameter("Type", request);
		}
			 */

			inputMap.put("ContriType", lStrContiType);

			List treasuries = null;
			List ddonames = null;

			if (lStrUserType.equals("DDOAsst")) {
				lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
				treasuries = lObjOfflineContriDAO
				.getTreasuryForDDO(lStrDdoCode);
				ddonames = lObjOfflineContriDAO
				.getDdoNameFromDdoCode(lStrDdoCode);
				if (lStrUseType.equals("ViewRejected")) {
					BillGroupList = lObjOfflineContriDAO
					.getBillGroupsRejectedForDdo(lStrDdoCode);
				} else {
					BillGroupList = lObjOfflineContriDAO
					.getBillGroupsRegularForDdoInDDOAsstLogin(lStrDdoCode);
				}
			} else if (lStrUserType.equals("DDO")) {

				lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
				treasuries = lObjOfflineContriDAO
				.getTreasuryForDDO(lStrDdoCode);
				ddonames = lObjOfflineContriDAO
				.getDdoNameFromDdoCode(lStrDdoCode);
				if (lStrUseType.equals("ViewApproved")) {
					BillGroupList = lObjOfflineContriDAO
					.getApprovedBillGroupsForDdoInDDOLogin(lStrDdoCode);
				} else {
					BillGroupList = lObjOfflineContriDAO
					.getBillGroupsForDdoInDDOLogin(lStrDdoCode);
				}

			} else {
				treasuries = lObjOfflineContriDAO
				.getCurrentTreasury(gStrLocationCode);
				if (lStrUserType.equals("ATO")) {
					ddonames = lObjOfflineContriDAO.getAllDDO(gStrLocationCode);
				} else {
					ddonames = lObjOfflineContriDAO
					.getAllDDOForContriForwardedToTO(gStrLocationCode);
				}
			}

			inputMap.put("TREASURIES", treasuries);
			inputMap.put("DDONAMES", ddonames);

			/*
			 * Checks if request is sent by click of GO button or the page is
			 * loaded for the first time.
			 */

			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

			String schemeCode = null;
			String schemename = null;

			/* Get All Types Of Payment From Lookup */
			/*Amish List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues("TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap),
				inputMap);
			 */
			List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues("TypeOfPaymentDCPS", 1L,inputMap);
			//String lStrTypeOfPaymentMaster = StringUtility.getParameter("typeOfPaymentMaster", request).trim();

			String lStrTypeOfPaymentMaster = "700046";
			/*
		if ("".equals(lStrTypeOfPaymentMaster)) {
			lStrTypeOfPaymentMaster = "700046"; // By default Regular Type
		}
			 */
			inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster.trim());

			//Set the Month Id and Year Id for the current month and year from the system date for the first time load.
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String lStrCurDate = null;

			/*
		if(StringUtility.getParameter("cmbMonth", request).trim().equals(""))
		{
			lStrCurDate = sdf.format(gDtCurDate);
			if(lStrCurDate != null && !"".equals(lStrCurDate))
			{
				monthId = Long.valueOf(lStrCurDate.substring(3, 5));
				// month Id hard-coded to April temporarily so that DDOs dont forward May's contributions by mistake instead of April Month 
				monthId = 4l;
			}
		}

		if(StringUtility.getParameter("cmbYear", request).trim().equals(""))
		{
			if(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate) != null && !"".equals(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate)))
			{
				yearId = Long.valueOf(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate));
			}
		}

			 */

			String lStrBillGroupId = inputMap.get("cmbBillGroup") != null ? inputMap.get("cmbBillGroup").toString():"";

			if (lStrBillGroupId != null	&& !lStrBillGroupId.equalsIgnoreCase("")) {

				/* Get the DDO Code */
				lStrDdoCode = inputMap.get("cmbDDOCode") != null ? inputMap.get("cmbDDOCode").toString():"";

				if (lStrUserType.equals("DDO")) {
					if (lStrUseType.equals("ViewApproved")) {
						BillGroupList = lObjOfflineContriDAO
						.getApprovedBillGroupsForDdoInDDOLogin(lStrDdoCode);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInDDOLogin(lStrDdoCode);
					}
				} else if (lStrUserType.equals("TO")) {
					if (lStrUseType.equals("ViewReverted")) {
						String lStrBillGroupIdFromReversion = StringUtility
						.getParameter("cmbBillGroup", request);
						String lStrBillGroupDescFromReversion = StringUtility
						.getParameter("cmbBillGroupDesc", request);
						ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(lStrBillGroupIdFromReversion);
						lObjComboValuesVO
						.setDesc(lStrBillGroupDescFromReversion);
						BillGroupList = new ArrayList<Object>();
						BillGroupList.add(lObjComboValuesVO);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInTOLogin(lStrDdoCode);
					}
				} else if (lStrUserType.equals("DDOAsst")) {

					if (lStrUseType.equals("ViewRejected")) {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsRejectedForDdo(lStrDdoCode);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdo(lStrDdoCode);
					}
				} else if (lStrUserType.equals("ATO")) {
					if (lStrUseType.equals("ViewRejected")) {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsRejectedForDdoInATOLogin(lStrDdoCode);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdo(lStrDdoCode);
					}
				}

				/*
				 * Get Month Id,Year Id,Bill Group Id,Treasury Code,Scheme
				 * Name,Scheme Code from Request
				 */
				//monthId = Long.parseLong(StringUtility.getParameter("cmbMonth", request));

				String lStrMonthId = inputMap.get("cmbMonth") != null ? inputMap.get("cmbMonth").toString().trim():"";
				monthId = Long.parseLong(lStrMonthId);

				//yearId = Long.parseLong(StringUtility.getParameter("cmbYear", request));

				String lStrYearId = inputMap.get("cmbYear") != null ? inputMap.get("cmbYear").toString():"";
				yearId = Long.parseLong(lStrYearId);

				Long lLongbillGroupId = Long.valueOf(lStrBillGroupId);

				String lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDdoCode).trim();
				Long treasuryCode = Long.valueOf(lStrTreasuryCode);

				/*

			if (!StringUtility.getParameter("schemeName", request)
					.equalsIgnoreCase("")
					&& StringUtility.getParameter("schemeName", request) != null) {
				schemename = StringUtility.getParameter("schemeName",
						request).trim();
			}
			if (!StringUtility.getParameter("schemeCode", request)
					.equalsIgnoreCase("")
					&& StringUtility.getParameter("schemeCode", request) != null) {
				schemeCode = StringUtility.getParameter(
						"schemeCode", request).toString().trim();
			}

				 */

				// Code Added for checking previous month's contribution entry

				DcpsCommonDAO lObjDcpsCommonDAOForFinYear = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
				SgvcFinYearMst lObjSgvcFinYearMst = null;
				lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAOForFinYear.read(yearId);

				Long previousMonthId = null;
				Long previousYearId = null;
				if (monthId == 1) {
					previousMonthId = 12l;
				} else {
					previousMonthId = monthId - 1;
				}

				if (monthId == 4) {
					previousYearId = lObjSgvcFinYearMst.getPrevFinYearId();
				} else {
					previousYearId = yearId;
				}

				String contributionsForPrvsMonth = "NO";
				String voucherDtlsForPrvsMonth = "NO";

				MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtlsForPrvsYear = lObjOfflineContriDAO
				.checkContributionsForPrvsMonth(previousYearId,
						previousMonthId, lStrDdoCode, lLongbillGroupId);

				if (lObjMstDcpsContriVoucherDtlsForPrvsYear != null) {
					contributionsForPrvsMonth = "YES";
				}

				if (lObjMstDcpsContriVoucherDtlsForPrvsYear != null) {
					if (lObjMstDcpsContriVoucherDtlsForPrvsYear.getVoucherNo() != null
							&& lObjMstDcpsContriVoucherDtlsForPrvsYear
							.getVoucherDate() != null) {
						voucherDtlsForPrvsMonth = "YES";
					}
				}

				inputMap.put("contributionsForPrvsMonth",contributionsForPrvsMonth);
				inputMap.put("voucherDtlsForPrvsMonth", voucherDtlsForPrvsMonth);

				inputMap.put("previousMonthId", previousMonthId);
				inputMap.put("previousYearId", previousYearId);

				// Code overs for checking previous month's contribution entry

				/* Get Year code from Year Id */
				String yearCode = lObjDcpsCommonDAO
				.getYearCodeForYearId(yearId);

				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearCode);

				if (lIntMonth == 1 || lIntMonth == 2 || lIntMonth == 3) {
					lIntYear += 1;
				}

				/*
				 * Get First Date and Last Date of Month For Selected Year and
				 * Month
				 */
				Date lDtLastDate = null;
				Date lDtFirstDate = null;

				Integer lIntDelayedMonth = null;
				Long lLongDelayedYearId = null;
				Integer lIntDelayedYear = null;
				Date lDtLastDateDelayed = null;
				Date lDtFirstDateDelayed = null;
				Long delayedMonthId = null;
				Long delayedYearId = null;

				if (lStrTypeOfPaymentMaster.equals("700047")) // Delayed Type
					// payment
				{
					if ((lStrUserType.equals("DDOAsst") || lStrUserType
							.equals("ATO"))
							&& (lStrUseType.equals("ViewAll"))) {
						lIntDelayedMonth = Integer
						.parseInt(StringUtility.getParameter(
								"cmbDelayedMonth", request).trim());
						lLongDelayedYearId = Long
						.parseLong(StringUtility.getParameter(
								"cmbDelayedYear", request).trim());
						lIntDelayedYear = Integer
						.parseInt(lObjDcpsCommonDAO
								.getYearCodeForYearId(lLongDelayedYearId));

						delayedMonthId = Long.valueOf(StringUtility.getParameter(
								"cmbDelayedMonth", request).trim());
						delayedYearId = Long.valueOf(StringUtility.getParameter(
								"cmbDelayedYear", request).trim());

						inputMap.put("delayedmonthId", lIntDelayedMonth);
						inputMap.put("delayedyearId", lLongDelayedYearId);

						if (lIntDelayedMonth == 1 || lIntDelayedMonth == 2
								|| lIntDelayedMonth == 3) {
							lIntDelayedYear += 1;
						}

						lDtLastDateDelayed = lObjDcpsCommonDAO
						.getLastDate(lIntDelayedMonth - 1,
								lIntDelayedYear);
						lDtFirstDateDelayed = lObjDcpsCommonDAO
						.getFirstDate(lIntDelayedMonth - 1,
								lIntDelayedYear);

						inputMap.put("FirstDateDelayed", lDtFirstDateDelayed);
						inputMap.put("LastDateDelayed", lDtLastDateDelayed);

						if (lIntDelayedMonth == 1) {
							lIntDelayedYear--;
						}
					}
				}

				lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1,
						lIntYear);
				lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 1,
						lIntYear);

				// Below code checks if the bill is already generated for given details

				Boolean lBlFlagBillGenerated = false;

				if (lStrUserType.equals("DDOAsst")) 
				{
					lBlFlagBillGenerated = lObjOfflineContriDAO.checkIfBillAlreadyGenerated(lLongbillGroupId, monthId,Long.valueOf(lIntYear.toString()));
				}
				inputMap.put("lBlFlagBillGenerated", lBlFlagBillGenerated);

				//Code commented as seems of no use

				/*
			if (lIntMonth == 1) {
				lIntYear--;
			}

			Date lDtDelFirstDate = lObjDcpsCommonDAO.getFirstDate(
					lIntMonth - 2, lIntYear);
			Date lDtDelLastDate = lObjDcpsCommonDAO.getLastDate(
					lIntMonth - 2, lIntYear);

			/*

			/* Get All Pay Commissions from Lookup */
				List<CmnLookupMst> listPayCommissionOld = IFMSCommonServiceImpl.getLookupValues("PayCommissionDCPS", 1L, inputMap);

				List<CmnLookupMst> listPayCommission = new ArrayList<CmnLookupMst>();
				CmnLookupMst tempCmnLookupMst = null;

				for (CmnLookupMst lObjCommonLookupMst : listPayCommissionOld) {

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700015")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700015l);
						tempCmnLookupMst.setLookupDesc("5 PC");
						listPayCommission.add(tempCmnLookupMst);
					}
					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700016")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700016l);
						tempCmnLookupMst.setLookupDesc("6 PC");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700338")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700338l);
						tempCmnLookupMst.setLookupDesc("NonGovt");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700339")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700339l);
						tempCmnLookupMst.setLookupDesc("Padmanabhan");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700340")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700340l);
						tempCmnLookupMst.setLookupDesc("Fourth(IV)");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700345")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700345l);
						tempCmnLookupMst.setLookupDesc("Shetty");
						listPayCommission.add(tempCmnLookupMst);
					}
					
					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(//added by shweta - 14-06-2019
							"700349")) {
								tempCmnLookupMst = new CmnLookupMst();
								tempCmnLookupMst.setLookupId(700349l);
								tempCmnLookupMst.setLookupDesc("7 PC");
								listPayCommission.add(tempCmnLookupMst);
							}
				}
				
				inputMap.put("listPayCommission", listPayCommission);

				/* Puts the Edit form option's provision. */

				List UserList = null;

				if (lStrUserType.equals("ATO")
						|| lStrUserType.equals("DDOAsst")
						|| (lStrUserType.equals("TO") && lStrUseType
								.equals("ViewReverted"))) {
					inputMap.put("EditForm", "Y");
					// UserList = getHierarchyUsers(inputMap, lStrUserType);

				} else if ((lStrUserType.equals("TO") || lStrUserType
						.equals("DDO"))
						&& !lStrUseType.equals("ViewReverted")) {
					inputMap.put("EditForm", "N");
				}

				if (lStrUserType.equals("ATO")
						|| lStrUserType.equals("DDOAsst")
						|| lStrUserType.equals("DDO")) {
					UserList = getHierarchyUsersSchdlrForDDOAsst(inputMap, lStrUserType);
				}

				inputMap.put("UserList", UserList);

				/*
				 * Gets the Employees' contribution List for selected month and
				 * year and puts in the Map
				 */

				//Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

				List empList = null;

				SimpleDateFormat sdf1 = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
				String lStrFirstDate = null;
				if(lDtFirstDateDelayed != null) // It will not be null when Delayed Type Payment is selected by DDO Asst or ATO.
				{
					lStrFirstDate = sdf1.format(lDtFirstDateDelayed);
				}
				else
				{
					lStrFirstDate = sdf1.format(lDtFirstDate);
				}

				empList = lObjOfflineContriDAO.getEmpListForContributionSchdlr(
						lStrDdoCode, lLongbillGroupId, monthId, yearId,
						lStrUserType, lStrUseType, gStrPostId, 
						lStrFirstDate, lStrTypeOfPaymentMaster,delayedMonthId,delayedYearId);
				inputMap.put("totalRecords", empList.size());

				if (empList.size() == 0) {
					empList = null;
				}

				// Gets voucherDtlsVO

				lObjMstDcpsContriVoucherDtls = lObjOfflineContriDAO
				.getContriVoucherVOForInputDtls(yearId, monthId,
						lStrDdoCode, lLongbillGroupId);
				inputMap.put("lObjMstDcpsContriVoucherDtls",
						lObjMstDcpsContriVoucherDtls);
				if(lObjMstDcpsContriVoucherDtls != null && lObjMstDcpsContriVoucherDtls.getVoucherStatus() != null)
				{
					inputMap.put("dcpsVoucherStatus", lObjMstDcpsContriVoucherDtls.getVoucherStatus());
				}
				else
				{
					inputMap.put("dcpsVoucherStatus", 0l);
				}

				/* Puts All Above Values in InputMap */

				inputMap.put("lLongbillGroupId", lLongbillGroupId);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("schemename", schemename);
				inputMap.put("schemeCode", schemeCode);
				inputMap.put("FirstDate", lDtFirstDate);
				inputMap.put("LastDate", lDtLastDate);

				inputMap.put("schdlStartDate", sdf1.format(lDtFirstDate));
				inputMap.put("schdlEndDate", sdf1.format(lDtLastDate));

				// Code commented as seems of no use
				/*
			inputMap.put("DelFirstDate", lDtDelFirstDate);
			inputMap.put("DelLastDate", lDtDelLastDate);
				 */

				inputMap.put("empList", empList);
				inputMap.put("GoPressed", "Y");

			}

			// Adds Contribution Through Challan as a bill group
			/*String lStrContriThruChallanOrNot = StringUtility.getParameter(
				"contriThruChallanOrNot", request);
		inputMap.put("ContriThruChallanOrNot", lStrContriThruChallanOrNot);*/
			inputMap.put("hidBGIdForContriThruChallan", gObjRsrcBndle
					.getString("DCPS.BGIdForContriThruChallan"));
			if (BillGroupList == null) {
				BillGroupList = new ArrayList<Object>();
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				BillGroupList.add(lObjComboValuesVO);
			}
			// Contribution thru challan removed
			/*ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForContriThruChallan"));
		lObjComboValuesVO.setDesc("Contribution Through Challan");
		BillGroupList.add(lObjComboValuesVO);
			 */
			inputMap.put("BillGroupList", BillGroupList);

			inputMap.put("monthId", monthId);
			inputMap.put("yearId", yearId);
			inputMap.put("lStrDDOCode", lStrDdoCode);
			inputMap.put("YEARS", lLstYears);
			//	inputMap.put("BILLGROUPS", lLstBillGroups);
			inputMap.put("MONTHS", lLstMonths);
			inputMap.put("lStrUser", lStrUserType);
			inputMap.put("lStrUse", lStrUseType);

			inputMap.put("listTypeOfPayment", listTypeOfPayment);

			resObj.setResultValue(inputMap);

			//	if(inputMap.get("fromPayBillScheduler")==null) // For Scheuler only 
			//	resObj.setViewName("DCPSOfflineEntryForm");


		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject saveContributions(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long regStatus = null;
		Long lLngContriVoucherIdPassed = null;
		Integer savedFlag = null;
		Boolean successFlag = false;
		Integer lIntContinueFlag = 0;
		Long lLongContriIdForDelete = null;
		Long lLongIndexOfDeletedContri = null;
		TrnDcpsContribution lObjTrnDcpsContributionforDelete = null;

		Session saveTrnContriSession = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes DAO and variables */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			resObj = serv.executeService("saveVoucherDtlsForContri", inputMap);

			String lStrContriVoucherIdPassed = inputMap.get(
			"lLngContriVoucherIdToBePassed").toString().trim();

			if (!"".equals(lStrContriVoucherIdPassed)) {
				lLngContriVoucherIdPassed = Long
				.valueOf(lStrContriVoucherIdPassed);
			}
			gLogger.info("lStrContriVoucherIdPassed is *********"+lStrContriVoucherIdPassed);
			// Below is a wrapper for taking care that when a voucher is forwarded through scheduler it does not allow to send forward the contributions from UI at the same time when scheduler is running.
			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) inputMap.get("voucherVOToCheckThruSchdlr");
			Boolean lBlGoInsideSaveContri = true;

			if(lObjMstDcpsContriVoucherDtls != null)
			{
				if(lObjMstDcpsContriVoucherDtls.getVoucherStatus() != null)
				{
					if(lObjMstDcpsContriVoucherDtls.getVoucherStatus() == 3)
					{
						lBlGoInsideSaveContri = false;
					}
				}
			}

			//if(lBlGoInsideSaveContri) {

			/* Creates entry of HstDcpsContribution in table */

			if (inputMap.containsKey("lObjHstDcpsContribution")) {
				HstDcpsContribution lObjHstDcpsContribution = (HstDcpsContribution) inputMap
				.get("lObjHstDcpsContribution");

				Long lLongHstContributionId = IFMSCommonServiceImpl
				.getNextSeqNum("HST_DCPS_CONTRIBUTION", inputMap);

				lObjHstDcpsContribution
				.setDcpsContributionHstId(lLongHstContributionId);

				lObjOfflineContriDAO.create(lObjHstDcpsContribution);
			}

			/* Gets the Contribution Ids from request */
			String lStrTotalRecords = StringUtility.getParameter("hdnCounter",
					request).trim();
			Integer lIntTotalRecords = Integer.parseInt(lStrTotalRecords);
			String lStrTotalRecords1 = StringUtility.getParameter("hdnCounter1",
					request).trim();
			
			
			if(lStrTotalRecords1!=null && Integer.parseInt(lStrTotalRecords1)>0)
			{
				lIntTotalRecords=Integer.parseInt(lStrTotalRecords1)+1;
			}
			String lStrDeletedContributionIndexes = StringUtility.getParameter(
					"deletedContributionIndexes", request).trim();

			String[] lStrArrDeletedContributionIndexes = null;

			if (!"".equals(lStrDeletedContributionIndexes)) {
				lStrArrDeletedContributionIndexes = lStrDeletedContributionIndexes
				.split("~");
			}
			gLogger.info("Indexes are *******"+lStrDeletedContributionIndexes);
			String[] lStrArrDeletedContributionIdPks = null;
			String lStrDeletedContributionIdPks = StringUtility.getParameter(
					"deletedContributionIdPks", request).trim();

			if (!"".equals(lStrDeletedContributionIndexes)) {
				lStrArrDeletedContributionIdPks = lStrDeletedContributionIdPks
				.split("~");
			}


			gLogger.info("lStrDeletedContributionIndexes are *******"+lStrDeletedContributionIndexes);

			/*
			 * String lStrdcpsContributionIds = inputMap
			 * .get("dcpsContributionIds").toString().trim(); String[]
			 * lStrArrdcpsContributionIds = lStrdcpsContributionIds .split("~");
			 * 
			 * Long[] lLongArrdcpsContributionIds = new
			 * Long[lStrArrdcpsContributionIds.length];
			 * 
			 * for (Integer lInt = 0; lInt < lStrArrdcpsContributionIds.length;
			 * lInt++) { lLongArrdcpsContributionIds[lInt] = Long
			 * .valueOf(lStrArrdcpsContributionIds[lInt]); }
			 */

			StringBuffer lStrContriIdsForForward = new StringBuffer();
			Long lLongTempdcpsContributionId = null;
			Integer saveOrForwardFlag = Integer.parseInt(StringUtility
					.getParameter("saveOrForwardFlag", request).trim());

			/* Creates or Updates the record in TrnDcpsContribution Table */
			TrnDcpsContribution[] lArrTrnDcpsContributions = (TrnDcpsContribution[]) inputMap
			.get("lArrTrnDcpsContributions");

			Long lLongContributionId = null;

			String lStrUser = StringUtility.getParameter("User", request).trim();
			String lStrUse = StringUtility.getParameter("Use", request).trim();

			// Code added later for Missing Credits
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			Long lLongMonthId = Long.valueOf(StringUtility.getParameter("cmbMonth", request).trim());
			Long lLongYearId = Long.valueOf(StringUtility.getParameter("cmbYear", request).trim());
			Long lLongBillGroupId = Long.valueOf(StringUtility.getParameter("cmbBillGroup",request).trim());
			String lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
			String lStrSchemeCode = StringUtility.getParameter("schemeCode", request).trim();

			Long lLongVoucherNo = null;
			String lStrVoucherDate = null;
			Date lDateVoucherDate = null;

			if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
			{
				lLongVoucherNo = Long.valueOf(StringUtility.getParameter("txtVoucherNo",request).trim());
				lStrVoucherDate = StringUtility.getParameter("txtVoucherDate",request).trim();
				lDateVoucherDate = simpleDateFormat.parse(lStrVoucherDate.trim());
			}
			//Code added later for Missing Credits ends

			for (Integer lInt = 1; lInt <= lIntTotalRecords; lInt++) {

				lIntContinueFlag = 0;

				if (lStrArrDeletedContributionIndexes != null) {
					for (Integer lIntDelete = 0; lIntDelete < lStrArrDeletedContributionIndexes.length; lIntDelete++) {
						if (Integer
								.parseInt(lStrArrDeletedContributionIndexes[lIntDelete].trim()) == lInt) {
							lIntContinueFlag = 1;
						}
					}

					if (lIntContinueFlag == 1) {
						continue;
					}
				}
				if(StringUtility.getParameter("checkbox" + lInt, request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("checkbox" + lInt, request).trim()))
				{
					lLongContributionId = Long.parseLong(StringUtility
							.getParameter("checkbox" + lInt, request).trim());

					if (lLongContributionId == 0l) {
						lLongTempdcpsContributionId = IFMSCommonServiceImpl
						.getNextSeqNum("TRN_DCPS_CONTRIBUTION", inputMap);
						lArrTrnDcpsContributions[lInt - 1].setDcpsContributionId(lLongTempdcpsContributionId);
						lArrTrnDcpsContributions[lInt - 1].setRltContriVoucherId(lLngContriVoucherIdPassed);

						if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
						{
							lArrTrnDcpsContributions[lInt - 1].setVoucherNo(lLongVoucherNo);
							lArrTrnDcpsContributions[lInt - 1].setVoucherDate(lDateVoucherDate);
							lArrTrnDcpsContributions[lInt - 1].setIsMissingCredit('Y');
							lArrTrnDcpsContributions[lInt - 1].setMissingCreditApprovalDate(gDtCurDate);
							Float DARate = lObjOfflineContriDAO
							.getDARateForPayCommission(lArrTrnDcpsContributions[lInt - 1].getPayCommission());
							double DARate1=DARate*0.01;
							gLogger.info("DARate1 is ***********"+DARate1);
							List l=lObjOfflineContriDAO.getBsicPayDetails(lArrTrnDcpsContributions[lInt - 1].getDcpsEmpId());
							double currentBasic=0d;
							if(l!=null && l.size()>0)
							{
								double a=0d;
								double b=0d;
								Object[] obj=(Object[]) l.get(0);
								if(obj[0]!=null)
								{
									a=Double.parseDouble(obj[0].toString());
								}
								if(obj[1]!=null)
								{
									b=Double.parseDouble(obj[1].toString());
								}
								if(b>0)
								{
									currentBasic=b;
								}
								else
								{
									currentBasic=a;
								}
							}
							gLogger.info("currentBasic is ***********"+currentBasic);
							double DP=0;
							if (lArrTrnDcpsContributions[lInt - 1].getPayCommission().equalsIgnoreCase( "700015") || lArrTrnDcpsContributions[lInt - 1].getPayCommission().equalsIgnoreCase( "700345") ) {
								
								DP =currentBasic / 2;
							} else {
								DP = 0;
							}
							gLogger.info("DP is ***********"+DP);
							double DA=(currentBasic+DP)*DARate1;
							gLogger.info("DA is ***********"+DA);
							DecimalFormat df = new DecimalFormat("0.00");
							DA = Double.valueOf(df.format(DA));
							gLogger.info("DA"+DA);
							double contrib=lArrTrnDcpsContributions[lInt - 1].getContribution();
							gLogger.info("contrib"+contrib);
							//$t 2019
							double contribEmplr=lArrTrnDcpsContributions[lInt - 1].getContributionEmplr();
							gLogger.info("contribEmplr"+contribEmplr);
							double bsicAllowed=(currentBasic+DA)*0.1;
							gLogger.info("bsicAllowed"+bsicAllowed);
							bsicAllowed=Math.ceil(bsicAllowed);
							
							if(contrib >bsicAllowed )
							{
								lArrTrnDcpsContributions[lInt - 1].setStatus('S');
							}
						}
						gLogger.info("lLongTempdcpsContributionId is ***********"+lLongTempdcpsContributionId);
						lObjOfflineContriDAO
						.create(lArrTrnDcpsContributions[lInt - 1]);


						if (saveOrForwardFlag == 2) {
							lStrContriIdsForForward
							.append(lLongTempdcpsContributionId);
							lStrContriIdsForForward.append("~");
						}

					} else {

						lArrTrnDcpsContributions[lInt - 1].setDcpsContributionId(lLongContributionId);
						
						lArrTrnDcpsContributions[lInt - 1].setRltContriVoucherId(lLngContriVoucherIdPassed);

						// Below If is redundant so removed.
						/*
								if ((lObjOfflineContriDAO.getRegStatusForContriId(lLongContributionId
												.toString())) != null) {
									regStatus = Long.valueOf(lObjOfflineContriDAO.getRegStatusForContriId(lLongContributionId.toString()).toString());
									lArrTrnDcpsContributions[lInt - 1].setRegStatus(regStatus);
								}
						 */

						if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
						{
							lArrTrnDcpsContributions[lInt - 1].setVoucherNo(lLongVoucherNo);
							lArrTrnDcpsContributions[lInt - 1].setVoucherDate(lDateVoucherDate);
							lArrTrnDcpsContributions[lInt - 1].setIsMissingCredit('Y');
							lArrTrnDcpsContributions[lInt - 1].setMissingCreditApprovalDate(gDtCurDate);
							if(lArrTrnDcpsContributions[lInt - 1].getStatus().toString().equalsIgnoreCase("F"))
							{
								Float DARate = lObjOfflineContriDAO
								.getDARateForPayCommission(lArrTrnDcpsContributions[lInt - 1].getPayCommission());
								double DARate1=DARate*0.01;
								gLogger.info("DARate1 is ***********"+DARate1);
								List l=lObjOfflineContriDAO.getBsicPayDetails(lArrTrnDcpsContributions[lInt - 1].getDcpsEmpId());
								double currentBasic=0d;
								if(l!=null && l.size()>0)
								{
									double a=0d;
									double b=0d;
									Object[] obj=(Object[]) l.get(0);
									if(obj[0]!=null)
									{
										a=Double.parseDouble(obj[0].toString());
									}
									if(obj[1]!=null)
									{
										b=Double.parseDouble(obj[1].toString());
									}
									if(b>0)
									{
										currentBasic=b;
									}
									else
									{
										currentBasic=a;
									}
								}
								gLogger.info("currentBasic is ***********"+currentBasic);
								double DP=0;
								if (lArrTrnDcpsContributions[lInt - 1].getPayCommission().equalsIgnoreCase( "700015") || lArrTrnDcpsContributions[lInt - 1].getPayCommission().equalsIgnoreCase( "700345") ) {
									
									DP =currentBasic / 2;
								} else {
									DP = 0;
								}
								gLogger.info("DP is ***********"+DP);
								double DA=(currentBasic+DP)*DARate1;
								gLogger.info("DA is ***********"+DA);
								DecimalFormat df = new DecimalFormat("0.00");
								DA = Double.valueOf(df.format(DA));
								gLogger.info("DA"+DA);
								double contrib=lArrTrnDcpsContributions[lInt - 1].getContribution();
								gLogger.info("contrib"+contrib);
								//$t 2019
								double contribEmplr=lArrTrnDcpsContributions[lInt - 1].getContributionEmplr();
								gLogger.info("contribEmplr"+contribEmplr);
								double bsicAllowed=(currentBasic+DA)*0.1;
								gLogger.info("bsicAllowed"+bsicAllowed);
								if(contrib >bsicAllowed )
								{
									lArrTrnDcpsContributions[lInt - 1].setStatus('S');
								}
							}
							
						}
						gLogger.info("lLongTempdcpsContributionId is ***********"+lLongTempdcpsContributionId);
						lObjOfflineContriDAO.update(lArrTrnDcpsContributions[lInt - 1]);

						if (saveOrForwardFlag == 2) {
							lStrContriIdsForForward.append(lLongContributionId);
							lStrContriIdsForForward.append("~");
						}
					}
				}

				

			}
			gLogger.info("lLongContributionId is ***********"+lLongContributionId);
			if (saveOrForwardFlag == 1) {

				savedFlag = 1;

			} else if (saveOrForwardFlag == 2) {

				savedFlag = 2;
				inputMap
				.put("lStrContriIdsForForward", lStrContriIdsForForward);

				// Below lines commented for manual contributions
				/*
						if (lStrUser.equals("ATO")) // Manual Contribution
						{
							resObj = forwardRequestToTO(inputMap);
						} else if (lStrUser.equals("DDOAsst")) // Online Contribution
						{
							resObj = FwdContriToDDO(inputMap);
						}
				 */
				if (lStrUser.equals("DDOAsst")) // Online Contribution
				{
					resObj = FwdContriToDDO(inputMap);
				}
			}

			inputMap.put("savedFlag", savedFlag);
			
			
			 String elementId=StringUtility.getParameter("elementId", request);
            gLogger.info("elementId is.... "+elementId);
			 inputMap.put("elementId",elementId);
			 
			// Code to delete deleted contributions
			Boolean lBlFlagEntryInTrnForRlt = true;
			if(lStrArrDeletedContributionIdPks != null)
			{
				for(Integer lInt=0;lInt<lStrArrDeletedContributionIdPks.length;lInt++)
				{
					lLongContriIdForDelete = Long.valueOf(lStrArrDeletedContributionIdPks[lInt].trim());
					if(lLongContriIdForDelete != 0L)
					{
						lObjTrnDcpsContributionforDelete = (TrnDcpsContribution) lObjOfflineContriDAO.read(lLongContriIdForDelete);
						lObjOfflineContriDAO.delete(lObjTrnDcpsContributionforDelete);
					}
				}

				if(!"".equals(lStrContriVoucherIdPassed))
				{
					lBlFlagEntryInTrnForRlt = lObjOfflineContriDAO.checkEntryInTrnForRltContriVoucherId(Long.valueOf(lStrContriVoucherIdPassed));
				}
				if(!lBlFlagEntryInTrnForRlt)
				{
					if(!"".equals(lStrContriVoucherIdPassed))
					{
						lObjOfflineContriDAO.deleteVoucherForNoContris(Long.valueOf(lStrContriVoucherIdPassed));
					}
				}
			}

			saveTrnContriSession = serv.getSessionFactory().getCurrentSession();
			saveTrnContriSession.flush();

			//Code to update voucher amount of the voucher added from missing credit screen.

			if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
			{
				/*
						Double voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLongMonthId,lLongYearId,
							lLongBillGroupId,lStrDDOCode,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
				 */
				Double voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLngContriVoucherIdPassed);
				lObjOfflineContriDAO.updateVoucherAmountInMstContriForMissingCreditVoucher(lLngContriVoucherIdPassed,voucherAmount);
			}

			//	}

			// Code to delete deleted contributions overs

			successFlag = true;

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		/*
		 * Sends XML response for sending the Contribution Ids using AJAX and
		 * the success flag
		 */

		inputMap.put("successFlag", successFlag);
		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSOfflineEntryForm");
		return resObj;
	}


	/*public ResultObject saveContributionsSchdlr(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long regStatus = null;
		Long lLngContriVoucherIdPassed = null;
		Integer savedFlag = null;
		Boolean successFlag = false;
		Integer lIntContinueFlag = 0;
		Long lLongContriIdForDelete = null;
		Long lLongIndexOfDeletedContri = null;
		TrnDcpsContribution lObjTrnDcpsContributionforDelete = null;
		Session currSession =(Session)inputMap.get("currentSessionForDcps");	
			 Initializes DAO and variables 
			setSessionInfoSchdlr(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			resObj = saveVoucherDtlsForContriSchdlr(inputMap);

			 Sets the Session Information 

			Session hibSession2 = serv.getSessionFactory().openSession();
			hibSession2.getTransaction().begin();

			String lStrContriVoucherIdPassed = inputMap.get(
					"lLngContriVoucherIdToBePassed").toString().trim();

			if (!"".equals(lStrContriVoucherIdPassed)) {
				lLngContriVoucherIdPassed = Long
						.valueOf(lStrContriVoucherIdPassed);
			}

			 Creates entry of HstDcpsContribution in table 

			if (inputMap.containsKey("lObjHstDcpsContribution")) {
				HstDcpsContribution lObjHstDcpsContribution = (HstDcpsContribution) inputMap
						.get("lObjHstDcpsContribution");

				Long lLongHstContributionId = IFMSCommonServiceImpl
						.getNextSeqNum("HST_DCPS_CONTRIBUTION", inputMap);

				lObjHstDcpsContribution
						.setDcpsContributionHstId(lLongHstContributionId);

				//lObjOfflineContriDAO.create(lObjHstDcpsContribution);
				currSession.save(lObjHstDcpsContribution);
			}

			 Gets the Contribution Ids from request 
			String lStrTotalRecords = inputMap.get("hdnCounter").toString().trim();
			Integer lIntTotalRecords = Integer.parseInt(lStrTotalRecords);
			//String lStrDeletedContributionIndexes = StringUtility.getParameter("deletedContributionIndexes", request).trim();
			String[] lStrArrDeletedContributionIndexes = null;
			String[] lStrArrDeletedContributionIdPks = null;


		if (!"".equals(lStrDeletedContributionIndexes)) {
				lStrArrDeletedContributionIndexes = lStrDeletedContributionIndexes
						.split("~");
			}


			String lStrDeletedContributionIdPks = StringUtility.getParameter(
					"deletedContributionIdPks", request).trim();

			if (!"".equals(lStrDeletedContributionIndexes)) {
				lStrArrDeletedContributionIdPks = lStrDeletedContributionIdPks
						.split("~");
			}




	 * String lStrdcpsContributionIds = inputMap
	 * .get("dcpsContributionIds").toString().trim(); String[]
	 * lStrArrdcpsContributionIds = lStrdcpsContributionIds .split("~");
	 * 
	 * Long[] lLongArrdcpsContributionIds = new
	 * Long[lStrArrdcpsContributionIds.length];
	 * 
	 * for (Integer lInt = 0; lInt < lStrArrdcpsContributionIds.length;
	 * lInt++) { lLongArrdcpsContributionIds[lInt] = Long
	 * .valueOf(lStrArrdcpsContributionIds[lInt]); }


			StringBuffer lStrContriIdsForForward = new StringBuffer();
			Long lLongTempdcpsContributionId = null;
			//Integer saveOrForwardFlag = Integer.parseInt(StringUtility.getParameter("saveOrForwardFlag", request).trim());
			Integer saveOrForwardFlag = 2 ;

			 Creates or Updates the record in TrnDcpsContribution Table 
			TrnDcpsContribution[] lArrTrnDcpsContributions = (TrnDcpsContribution[]) inputMap
					.get("lArrTrnDcpsContributions");

			Long lLongContributionId = null;

			for (Integer lInt = 1; lInt <= lIntTotalRecords; lInt++) {

				lIntContinueFlag = 0;

				if (lStrArrDeletedContributionIndexes != null) {
					for (Integer lIntDelete = 0; lIntDelete < lStrArrDeletedContributionIndexes.length; lIntDelete++) {
						if (Integer
								.parseInt(lStrArrDeletedContributionIndexes[lIntDelete].trim()) == lInt) {
							lIntContinueFlag = 1;
						}
					}

					if (lIntContinueFlag == 1) {
						continue;
					}
				}

			//	lLongContributionId = Long.parseLong(StringUtility.getParameter("checkbox" + lInt, request).trim());
				lLongContributionId = 0l;

				if (lLongContributionId == 0l) {
					lLongTempdcpsContributionId = IFMSCommonServiceImpl
							.getNextSeqNum("TRN_DCPS_CONTRIBUTION", inputMap);
					lArrTrnDcpsContributions[lInt - 1]
							.setDcpsContributionId(lLongTempdcpsContributionId);
					lArrTrnDcpsContributions[lInt - 1]
							.setRltContriVoucherId(lLngContriVoucherIdPassed);
					lObjOfflineContriDAO
							.create(lArrTrnDcpsContributions[lInt - 1]);
					//hibSession2.save(lArrTrnDcpsContributions[lInt - 1]);
					currSession.save(lArrTrnDcpsContributions[lInt - 1]);


					if (saveOrForwardFlag == 2) {
						lStrContriIdsForForward
								.append(lLongTempdcpsContributionId);
						lStrContriIdsForForward.append("~");
					}

				} else {

					lArrTrnDcpsContributions[lInt - 1].setDcpsContributionId(lLongContributionId);
					lArrTrnDcpsContributions[lInt - 1].setRltContriVoucherId(lLngContriVoucherIdPassed);

					// Below If is redundant so removed.

					if ((lObjOfflineContriDAO.getRegStatusForContriId(lLongContributionId
									.toString())) != null) {
						regStatus = Long.valueOf(lObjOfflineContriDAO.getRegStatusForContriId(lLongContributionId.toString()).toString());
						lArrTrnDcpsContributions[lInt - 1].setRegStatus(regStatus);
					}


					lObjOfflineContriDAO.update(lArrTrnDcpsContributions[lInt - 1]);
					if (saveOrForwardFlag == 2) {
						lStrContriIdsForForward.append(lLongContributionId);
						lStrContriIdsForForward.append("~");
					}
				}

			}

			//String lStrUser = StringUtility.getParameter("User", request).trim();

			String lStrUser = inputMap.get("User") != null ? inputMap.get("User").toString().trim():"";

			if (saveOrForwardFlag == 1) {

				savedFlag = 1;

			} else if (saveOrForwardFlag == 2) {

				savedFlag = 2;
				inputMap.put("lStrContriIdsForForward", lStrContriIdsForForward);

				// Below lines commented for manual contributions

				if (lStrUser.equals("ATO")) // Manual Contribution
				{
					resObj = forwardRequestToTO(inputMap);
				} else if (lStrUser.equals("DDOAsst")) // Online Contribution
				{
					resObj = FwdContriToDDO(inputMap);
				}

				if (lStrUser.equals("DDOAsst")) // Online Contribution
				{
					resObj = FwdContriToDDOSchdlr(inputMap);

					//Commit scheduler code here
					 //Transaction existingTran = (Transaction)inputMap.get("transaction");
					 //existingTran.commit();

					inputMap = UpdateLoginDtlsAfterFirstLvlFwd(inputMap);

					resObj = FwdContriToTOSchdlr(inputMap);
					// Commit scheduler code here
				}

			}

			inputMap.put("savedFlag", savedFlag);


			// Code to delete deleted contributions

			if(lStrArrDeletedContributionIdPks != null)
			{
				for(Integer lInt=0;lInt<lStrArrDeletedContributionIdPks.length;lInt++)
				{
					lLongContriIdForDelete = Long.valueOf(lStrArrDeletedContributionIdPks[lInt].trim());
					if(lLongContriIdForDelete != 0L)
					{
						lObjTrnDcpsContributionforDelete = (TrnDcpsContribution) lObjOfflineContriDAO.read(lLongContriIdForDelete);
						lObjOfflineContriDAO.delete(lObjTrnDcpsContributionforDelete);
					}
				}
			}

			// Code to delete deleted contributions overs

			successFlag = true;



	 * Sends XML response for sending the Contribution Ids using AJAX and
	 * the success flag


		inputMap.put("successFlag", successFlag);
		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSOfflineEntryForm");
		return resObj;
	}*/


	public ResultObject deleteContributions(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag;

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes variables and DAOs */
			lBlFlag = false;
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			/* Gets Contribution Ids from request */
			String lStrdcpsContributionIds = StringUtility.getParameter(
					"dcpsContributionIds", request);
			String[] lStrArrdcpsContributionIds = lStrdcpsContributionIds
			.split("~");

			/* Converts the Contribution Ids from String type to Long type */
			Long[] lLongArrdcpsContributionIds = new Long[lStrArrdcpsContributionIds.length];

			for (Integer lInt = 0; lInt < lStrArrdcpsContributionIds.length; lInt++) {
				lLongArrdcpsContributionIds[lInt] = Long
				.valueOf(lStrArrdcpsContributionIds[lInt]);
			}

			/* Deletes the Contributions for the selected primary key */
			for (Integer lInt = 0; lInt < lLongArrdcpsContributionIds.length; lInt++) {
				if (lLongArrdcpsContributionIds[lInt] != 0l) {
					lObjOfflineContriDAO.delete(lObjOfflineContriDAO
							.read(lLongArrdcpsContributionIds[lInt]));
					lBlFlag = true;
				}
			}

		} catch (Exception ex) {
			resObj.setResultValue(null);
			gLogger.error(" Error is : " + ex, ex);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	// Manual Forward so Commented.

	/*
	public ResultObject forwardRequestToTO(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		TrnDcpsContribution lObjTrnDcpsContribution = null;
		Long lLngVoucherContriId = null;

		try {
			// Sets the Session Information 
			setSessionInfo(objectArgs);

			// Initializes variables and DAOs 
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			OfflineContriDAO lObjOfflineContriDAOForVoucher = new OfflineContriDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

			String lStrVoucherContriId = objectArgs.get(
					"lLngContriVoucherIdToBePassed").toString().trim();

			if (!"".equals(lStrVoucherContriId)) {
				lLngVoucherContriId = Long.valueOf(lStrVoucherContriId);
			}

			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) lObjOfflineContriDAOForVoucher
					.read(lLngVoucherContriId);
			lObjMstDcpsContriVoucherDtls.setRemarksForRejection(null);
			lObjMstDcpsContriVoucherDtls.setVoucherStatus(4l); // Manual
			lObjOfflineContriDAOForVoucher.update(lObjMstDcpsContriVoucherDtls);

			//  Gets the PostId and Contribution Id from request to forward the
			  contribution to

			String toPost = StringUtility
					.getParameter("ForwardToPost", request).toString();

			// Gets the level Id from resource bundle 
			String toLevel = gObjRsrcBndle.getString("DCPS.TO");

			 Puts above values into Map to create WorkFlow 
			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);

			objectArgs.put("jobTitle", gObjRsrcBndle
					.getString("DCPS.Contribution"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.ContributionID")));

			//
			  Gets the Contribution Ids from request to forward the
			  contribution to

			String strPKValue = objectArgs.get("lStrContriIdsForForward")
					.toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			//
			  Creates WorkFlow and Forwards the Contributions to TO and also
			  sets the RegStatus to 4.

			for (Integer index = 0; index < strArrPKValue.length; index++) {

				objectArgs.put("Pkvalue", strArrPKValue[index].trim());
				if (lObjOfflineContriDAO
						.getRegStatusForContriId(strArrPKValue[index]) == null) {
					createWF(objectArgs);
				}
				WorkFlowDelegate.forward(objectArgs);

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO
						.read(Long.valueOf(strArrPKValue[index]));
				lObjTrnDcpsContribution.setRegStatus(4l);
				lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
				// RegStatus Set to 4
				// For Manual
				// Contribution
			}

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			throw ex;
		}

		resObj.setResultValue(objectArgs);

		return resObj;
	}

	 */

	public ResultObject approveContributions(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag;
		TrnDcpsContribution lObjTrnDcpsContribution = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(objectArgs);

			/* Initializes variables and DAOs */
			lBlFlag = false;
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serv.getSessionFactory());

			String strPKValue = StringUtility.getParameter(
					"dcpsContributionIds", request).toString().trim();

			String[] strArrPKValue = strPKValue.split("~");

			/*
			 * Approves the Contributions and sets the RegStatus to 1.
			 */
			for (Integer index = 0; index < strArrPKValue.length; index++) {

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO
				.read(Long.valueOf(strArrPKValue[index].trim()));
				lObjTrnDcpsContribution.setRegStatus(1l);
				lObjTrnDcpsContribution.setStatus('A');
				lObjOfflineContriDAO.update(lObjTrnDcpsContribution);

			}

			resObj = serv
			.executeService("saveVoucherDtlsForContri", objectArgs);

			// Below code added to update voucher details in paybillHeadMpg

			String lStrVoucherNo = StringUtility.getParameter("txtVoucherNo",request).trim();
			String lStrVoucherDate = StringUtility.getParameter("txtVoucherDate",request).trim();
			String lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
			String lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode", request).trim();
			String lStrMonthId = StringUtility.getParameter("cmbMonth", request).trim();
			String lStrYearId = StringUtility.getParameter("cmbYear", request).trim();
			String lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",request).trim();

			Long lLongMonthId = Long.valueOf(lStrMonthId.trim());

			String lStrYearCode = lObjDcpsCommonDAO.getYearCodeForYearId(Long.valueOf(lStrYearId));
			Long lLongYearCode = Long.valueOf(lStrYearCode);
			if(lLongMonthId == 1l || lLongMonthId == 2l || lLongMonthId == 3l)
			{
				lLongYearCode = lLongYearCode + 1;
				lStrYearCode = lLongYearCode.toString();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date lDateVoucherDate = null ;

			lObjOfflineContriDAO.updateVoucherDetailsInPayBillHeadMpg(Long.valueOf(lStrBillGroupId), lLongMonthId, lLongYearCode, Long.valueOf(lStrVoucherNo), sdf.parse(lStrVoucherDate));

			lBlFlag = true;

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		resObj.setResultValue(objectArgs);

		return resObj;
	}

	public ResultObject getSchemeforBillGroup(Map<String, Object> inputMap)
	throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAO */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Gets the Bill Group Id from request */
			Long billgroupId = Long.valueOf(StringUtility.getParameter("billGroupId", request).trim());

			/* Gets the Scheme Code and Scheme Name */
			Object[] schemeNameAndCode = lObjDcpsCommonDAO.getSchemeNameFromBillGroup(billgroupId);

			String schemeName = ((String) schemeNameAndCode[0]);
			String schemeCode = (String) schemeNameAndCode[1];
			
			schemeName=schemeName.replace("%", "PERCENT");

			/* Sends the Scheme Name and Scheme Code using AJAX. */
			String lSBScheme = getResponseXMLDocForSchemeFromBillGroup(schemeName, schemeCode).toString();

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBScheme).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {

			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	private List getHierarchyUsers(Map inputMap, String lStrUser) {

		List UserList = null;
		String subjectName = null;

		try {
			setSessionInfo(inputMap);

			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();

			// Gets Hierarchy for Manual Contribution

			if (lStrUser.equals("ATO")) {
				subjectName = gObjRsrcBndle.getString("DCPS.Contribution");
			}

			// Gets Hierarchy for Online Contribution

			if (lStrUser.equals("DDOAsst") || lStrUser.equals("DDO")) {
				subjectName = gObjRsrcBndle
				.getString("DCPS.OnlineContribution");
			}

			Long lLngHierRefId = WorkFlowHelper
			.getHierarchyByPostIDAndDescription(gStrPostId,
					subjectName, inputMap);

			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId,
					lLngHierRefId, inputMap);

			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId,
					lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}
		return UserList;
	}

	private List getHierarchyUsersSchdlrForDDOAsst(Map inputMap, String lStrUser) {

		List UserList = null;
		String subjectName = null;

		try {
			setSessionInfoSchdlr(inputMap);

			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();

			// Gets Hierarchy for Manual Contribution

			/*
			if (lStrUser.equals("ATO")) {
				subjectName = gObjRsrcBndle.getString("DCPS.Contribution");
			}
			 */
			// Gets Hierarchy for Online Contribution

			if (lStrUser.equals("DDOAsst") || lStrUser.equals("DDO")) {
				subjectName = gObjRsrcBndle
				.getString("DCPS.OnlineContribution");
			}

			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId,subjectName, inputMap);

			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId,lLngHierRefId, inputMap);

			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId,lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}
		return UserList;
	}

	private List getHierarchyUsersSchdlrForDDO(Map inputMap, String lStrUser) {

		List UserList = null;
		String subjectName = null;

		try {
			setSessionInfoSchdlr(inputMap);

			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();

			// Gets Hierarchy for Manual Contribution

			/*
			if (lStrUser.equals("ATO")) {
				subjectName = gObjRsrcBndle.getString("DCPS.Contribution");
			}
			 */
			// Gets Hierarchy for Online Contribution

			if (lStrUser.equals("DDOAsst") || lStrUser.equals("DDO")) {
				subjectName = gObjRsrcBndle.getString("DCPS.OnlineContribution");
			}

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			String lStrDDOAsstPostId = String.valueOf(inputMap.get("DdoAsstPostId"));
			Long lLongDDOAsstPostId = Long.valueOf(lStrDDOAsstPostId);
			Long lLongDDOPostId = lObjDcpsCommonDAO.getDDOPostIdForDDOAsst(lLongDDOAsstPostId);
			String lStrDDOPostId = lLongDDOPostId.toString();

			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrDDOPostId,subjectName, inputMap);

			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(lStrDDOPostId,lLngHierRefId, inputMap);

			List rsltList = WorkFlowHelper.getUpperPost(lStrDDOPostId,lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}
		return UserList;
	}

	private void createWF(Map inputMap) {

		try {
			setSessionInfo(inputMap);

			Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString());
			String subjectName = gObjRsrcBndle.getString("DCPS.Contribution");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper
			.getHierarchyByPostIDAndDescription(lStrPostId,
					subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.ContributionID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle
					.getString("DCPS.Contribution"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
	}

	private void createWFForOnlineContri(Map inputMap) {

		try {

			setSessionInfo(inputMap);

			Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString());
			String subjectName = gObjRsrcBndle.getString("DCPS.OnlineContribution");

			String lStrPostId = null;
			lStrPostId = SessionHelper.getPostId(inputMap).toString();

			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId,subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.OnlineContributionID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("DCPS.OnlineContribution"));

			WorkFlowDelegate.create(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
	}

	private void createWFForOnlineContriSchdlr(Map inputMap) {

		try {

			setSessionInfoSchdlr(inputMap);

			Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString().trim());
			String subjectName = gObjRsrcBndle.getString("DCPS.OnlineContribution");
			String lStrPostId = null;

			Map loginMap =(HashMap) inputMap.get("baseLoginMap");
			lStrPostId = String.valueOf((loginMap.get("postId").toString()));
			//System.out.println("Post Id "+lStrPostId);	
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId,subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.OnlineContributionID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("DCPS.OnlineContribution"));
			inputMap.put("strflag", "Offline");

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
	}

	private StringBuilder getResponseXMLDocForSchemeFromBillGroup(
			String schemeName, String schemeCode) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<schemename>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(schemeName.trim());
		lStrBldXML.append("]]>");
		lStrBldXML.append("</schemename>");
		lStrBldXML.append("<schemecode>");
		lStrBldXML.append(schemeCode.trim());
		lStrBldXML.append("</schemecode>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
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

	private StringBuilder getResponseXMLDocForRejectContri(Boolean flag,
			Integer regStatus) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<RegStatus>");
		lStrBldXML.append(regStatus);
		lStrBldXML.append("</RegStatus>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject getBillGroupsForDdo(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);

			String lStrDdoCode = StringUtility.getParameter("cmbDDOCode",
					request).trim();

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			String lStrUser = StringUtility.getParameter("User", request).trim();
			String lStrUseType = StringUtility.getParameter("Use", request).trim();

			if (lStrUser.equals("TO")) {
				if(lStrUseType.equals("MissingCreditEntry"))
				{
					finalList = new ArrayList<Object>();
					ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("--Select--");
					finalList.add(lObjComboValuesVO);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
					lObjComboValuesVO.setDesc("Missing Credit");
					finalList.add(lObjComboValuesVO);
				}
				else if(lStrUseType.equals("ViewApprovedByTO"))
				{
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdoInTOLoginInclMisngCrdt(lStrDdoCode);	
				}
				else
				{
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdoInTOLogin(lStrDdoCode);
				}

			} else if (lStrUser.equals("DDO")) {

				if (lStrUseType.equals("ViewApproved")) {
					finalList = lObjOfflineContriDAO
					.getApprovedBillGroupsForDdoInDDOLogin(lStrDdoCode);
				} else {
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdoInDDOLogin(lStrDdoCode);
				}

			} else if (lStrUser.equals("ATO")) {

				if (lStrUseType.equals("ViewRejected")) {
					finalList = lObjOfflineContriDAO
					.getBillGroupsRejectedForDdoInATOLogin(lStrDdoCode);
				} else {
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdo(lStrDdoCode);
				}
			} else {
				finalList = lObjOfflineContriDAO
				.getBillGroupsForDdo(lStrDdoCode);
			}

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	public ResultObject getApprovedBillGroupsForDdo(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);

			String lStrDdoCode = StringUtility.getParameter("cmbDDOCode",
					request).trim();

			Long lLongMonthId = Long.valueOf(StringUtility.getParameter(
					"cmbMonth", request).trim());

			Long lLongYearId = Long.valueOf(StringUtility.getParameter(
					"cmbYear", request).trim());

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			finalList = lObjOfflineContriDAO.getApprovedBillGroupsForDdo(
					lStrDdoCode, lLongMonthId, lLongYearId);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	public ResultObject getDDOsForTreasury(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);

			String lStrTreasuryCode = StringUtility.getParameter(
					"cmbTreasuryCode", request).trim();

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			//finalList = lObjOfflineContriDAO.getAllDDO(lStrTreasuryCode);
			finalList = lObjOfflineContriDAO.getAllDDOCodeASC(lStrTreasuryCode);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	public ResultObject FwdContriToDDO(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		TrnDcpsContribution lObjTrnDcpsContribution = null;
		Long lLngVoucherContriId = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(objectArgs);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			OfflineContriDAO lObjOfflineContriDAOForVoucher = new OfflineContriDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

			String lStrVoucherContriId = objectArgs.get(
			"lLngContriVoucherIdToBePassed").toString().trim();

			if (!"".equals(lStrVoucherContriId)) {
				lLngVoucherContriId = Long.valueOf(lStrVoucherContriId);
			}

			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) lObjOfflineContriDAOForVoucher
			.read(lLngVoucherContriId);
			lObjMstDcpsContriVoucherDtls.setRemarksForRejection(null);
			lObjMstDcpsContriVoucherDtls.setVoucherStatus(5l);
			lObjOfflineContriDAOForVoucher.update(lObjMstDcpsContriVoucherDtls);

			/*
			 * Gets the PostId and Contribution Id from request to forward the
			 * contribution to
			 */
			String toPost = StringUtility
			.getParameter("ForwardToPost", request).toString().trim();

			/* Gets the level Id from resource bundle */
			String toLevel = gObjRsrcBndle.getString("DCPS.DDO").trim();

			/* Puts above values into Map to create WorkFlow */
			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);

			objectArgs.put("jobTitle", gObjRsrcBndle.getString(
			"DCPS.OnlineContribution").trim());
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString(
			"DCPS.OnlineContributionID").trim()));

			/*
			 * Gets the Contribution Ids from request to forward the
			 * contribution to
			 */
			String strPKValue = objectArgs.get("lStrContriIdsForForward")
			.toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			/*
			 * Creates WorkFlow and Forwards the Contributions to TO and also
			 * sets the RegStatus to 1.
			 */
			for (Integer index = 0; index < strArrPKValue.length; index++) {


				if(!strArrPKValue[index].trim().equals(""))
				{

					objectArgs.put("Pkvalue", strArrPKValue[index].trim());

					if (lObjOfflineContriDAO
							.getRegStatusForContriId(strArrPKValue[index].trim()) == null) {
						createWFForOnlineContri(objectArgs);
					}

					WorkFlowDelegate.forward(objectArgs);

					lObjTrnDcpsContribution = new TrnDcpsContribution();
					lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO
					.read(Long.valueOf(strArrPKValue[index].trim()));
					lObjTrnDcpsContribution.setRegStatus(2l);
					lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
					lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
					lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
					lObjOfflineContriDAO.update(lObjTrnDcpsContribution);
				}
			}

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			throw ex;
		}

		/* Generates the XML response and sends the success flag */
		/*
		 * String lSBStatus = getResponseXMLDoc(lBlFlag).toString(); String
		 * lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		 * .toString();
		 * 
		 * objectArgs.put("ajaxKey", lStrResult);
		 * 
		 * resObj.setViewName("ajaxData");
		 */

		resObj.setResultValue(objectArgs);
		return resObj;
	}

	public ResultObject FwdContriToDDOSchdlr(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		TrnDcpsContribution lObjTrnDcpsContribution = null;
		Long lLngVoucherContriId = null;

		/* Sets the Session Information */
		setSessionInfoSchdlr(objectArgs);
		Session currSession =(Session)objectArgs.get("currentSessionForDcps");
		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
				TrnDcpsContribution.class, serv.getSessionFactory());
		OfflineContriDAO lObjOfflineContriDAOForVoucher = new OfflineContriDAOImpl(
				MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

		String lStrVoucherContriId = objectArgs.get("lLngContriVoucherIdToBePassed").toString().trim();

		/*Session hibSession1 = serv.getSessionFactory().openSession();
			hibSession1.getTransaction().begin();*/

		if (!"".equals(lStrVoucherContriId)) {
			lLngVoucherContriId = Long.valueOf(lStrVoucherContriId);
		}
		//System.out.println("Id to fetch lObjMstDcpsContriVoucherDtls "+lLngVoucherContriId);
		lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) lObjOfflineContriDAOForVoucher.fetchMstContriVoucherDtls(lLngVoucherContriId,objectArgs);
		lObjMstDcpsContriVoucherDtls.setRemarksForRejection(null);
		lObjMstDcpsContriVoucherDtls.setVoucherStatus(5l);
		//lObjOfflineContriDAOForVoucher.update(lObjMstDcpsContriVoucherDtls);
		currSession.saveOrUpdate(lObjMstDcpsContriVoucherDtls);

		/*
		 * Gets the PostId and Contribution Id from request to forward the
		 * contribution to
		 */
		//String toPost = StringUtility.getParameter("ForwardToPost", request).toString().trim();

		String lStrUserType = objectArgs.get("User") != null ? objectArgs.get("User").toString().trim():"";
		List UserList = getHierarchyUsersSchdlrForDDOAsst(objectArgs, lStrUserType);
		String toPost = UserList.get(0).toString().trim();

		/* Gets the level Id from resource bundle */
		String toLevel = gObjRsrcBndle.getString("DCPS.DDO").trim();

		/* Puts above values into Map to create WorkFlow */
		objectArgs.put("toPost", toPost);
		objectArgs.put("toPostId", toPost);
		objectArgs.put("toLevel", toLevel);

		objectArgs.put("jobTitle", gObjRsrcBndle.getString(
		"DCPS.OnlineContribution").trim());
		objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString(
		"DCPS.OnlineContributionID").trim()));

		/*
		 * Gets the Contribution Ids from request to forward the
		 * contribution to
		 */
		String strPKValue = objectArgs.get("lStrContriIdsForForward").toString().trim();
		String[] strArrPKValue = strPKValue.split("~");

		/*
		 * Creates WorkFlow and Forwards the Contributions to TO and also
		 * sets the RegStatus to 1.
		 */
		for (Integer index = 0; index < strArrPKValue.length; index++) {

			objectArgs.put("Pkvalue", strArrPKValue[index].trim());

			if (lObjOfflineContriDAO.getRegStatusForContriId(strArrPKValue[index].trim()) == null) {
				createWFForOnlineContriSchdlr(objectArgs);
			}

			WorkFlowDelegate.forward(objectArgs);

			lObjTrnDcpsContribution = new TrnDcpsContribution();
			//lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.read(Long.valueOf(strArrPKValue[index]));
			lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.fetchTrnDcpsContribution(Long.valueOf(strArrPKValue[index].trim()), objectArgs);
			//System.out.println("TRNDCPSCONRI READ-->"+lObjTrnDcpsContribution.getDcpsContributionId());
			lObjTrnDcpsContribution.setRegStatus(2l);
			lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
			lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
			lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
			currSession.saveOrUpdate(lObjTrnDcpsContribution);
		}


		/*hibSession1.getTransaction().commit();
			hibSession1.close();*/
		/* Generates the XML response and sends the success flag */
		/*
		 * String lSBStatus = getResponseXMLDoc(lBlFlag).toString(); String
		 * lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		 * .toString();
		 * 
		 * objectArgs.put("ajaxKey", lStrResult);
		 * 
		 * resObj.setViewName("ajaxData");
		 */

		resObj.setResultValue(objectArgs);
		return resObj;
	}

	public ResultObject FwdContriToTO(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag;
		TrnDcpsContribution lObjTrnDcpsContribution = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(objectArgs);

			/* Initializes variables and DAOs */
			lBlFlag = false;
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			/*
			 * Gets the PostId and Contribution Id from request to forward the
			 * contribution to
			 */
			String toPost = StringUtility
			.getParameter("ForwardToPost", request).toString().trim();

			/* Gets the level Id from resource bundle */
			String toLevel = gObjRsrcBndle.getString("DCPS.TO").trim();

			/* Puts above values into Map to create WorkFlow */
			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);

			objectArgs.put("jobTitle", gObjRsrcBndle.getString(
			"DCPS.OnlineContribution").trim());
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString(
			"DCPS.OnlineContributionID").trim()));

			/*
			 * Gets the Contribution Ids from request to forward the
			 * contribution to
			 */
			String strPKValue = StringUtility.getParameter(
					"dcpsContributionIds", request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			/*
			 * Creates WorkFlow and Forwards the Contributions to TO and also
			 * sets the RegStatus to 1.
			 */
			for (Integer index = 0; index < strArrPKValue.length; index++) {

				objectArgs.put("Pkvalue", strArrPKValue[index].trim());
				// createWF(objectArgs);
				WorkFlowDelegate.forward(objectArgs);

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.read(Long.valueOf(strArrPKValue[index].trim()));

				lObjTrnDcpsContribution.setRegStatus(3l);
				lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
				lObjOfflineContriDAO.update(lObjTrnDcpsContribution);
				// RegStatus Set to 3
				// For Online
				// Contribution

			}

			resObj = serv
			.executeService("saveVoucherDtlsForContri", objectArgs);
			lBlFlag = true;

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setResultValue(objectArgs);
		resObj.setViewName("ajaxData");

		return resObj;
	}

	/*public ResultObject FwdContriToTOSchdlr(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag;
		TrnDcpsContribution lObjTrnDcpsContribution = null;

			 Sets the Session Information 
			setSessionInfoSchdlr(objectArgs);
			Session currSession =(Session)objectArgs.get("currentSessionForDcps");
			Session hibSession3 = serv.getSessionFactory().openSession();
			hibSession3.getTransaction().begin();


			 Initializes variables and DAOs 
			lBlFlag = false;
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());


	 * Gets the PostId and Contribution Id from request to forward the
	 * contribution to

			//String toPost = StringUtility.getParameter("ForwardToPost", request).toString().trim();

			// ToPost getting method changed for scheduler.
			String lStrUserType = "DDO";
			List UserList = getHierarchyUsersSchdlrForDDO(objectArgs, lStrUserType);
			String toPost = UserList.get(0).toString();

			 Gets the level Id from resource bundle 
			String toLevel = gObjRsrcBndle.getString("DCPS.TO").trim();

			 Puts above values into Map to create WorkFlow 
			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);

			objectArgs.put("jobTitle", gObjRsrcBndle.getString(
					"DCPS.OnlineContribution").trim());
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString(
					"DCPS.OnlineContributionID").trim()));


	 * Gets the Contribution Ids from request to forward the
	 * contribution to


			//String strPKValue = StringUtility.getParameter("dcpsContributionIds", request).toString().trim();
			//String[] strArrPKValue = strPKValue.split("~");

			// Pks which are coming from JSP to be forwarded are got from object Map instead 

			String strPKValue = objectArgs.get("lStrContriIdsForForward").toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("strflag", "Offline");

	 * Creates WorkFlow and Forwards the Contributions to TO and also
	 * sets the RegStatus to 1.

			for (Integer index = 0; index < strArrPKValue.length; index++) {

				objectArgs.put("Pkvalue", strArrPKValue[index].trim());
				// createWF(objectArgs);

				try
				{
					WorkFlowDelegate.forward(objectArgs);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				//lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.read(Long.valueOf(strArrPKValue[index]));
				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.fetchTrnDcpsContribution(Long.valueOf(strArrPKValue[index].trim()), objectArgs);				
				lObjTrnDcpsContribution.setRegStatus(3l);
				lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
				currSession.saveOrUpdate(lObjTrnDcpsContribution);
				// RegStatus Set to 3
				// For Online
				// Contribution

			}

			hibSession3.getTransaction().commit();
			hibSession3.close();

			resObj = saveVoucherDtlsForContriSchdlr(objectArgs);
			lBlFlag = true;

		 Generates the XML response and sends the success flag 
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setResultValue(objectArgs);
		resObj.setViewName("ajaxData");

		return resObj;
	}*/

	public ResultObject AprContriByTO(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag;
		TrnDcpsContribution lObjTrnDcpsContribution = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(objectArgs);

			/* Initializes variables and DAOs */
			lBlFlag = false;
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			String strPKValue = StringUtility.getParameter(
					"dcpsContributionIds", request).toString().trim();

			String[] strArrPKValue = strPKValue.split("~");

			/*
			 * Approves the Contributions and sets the RegStatus to 1.
			 */
			for (Integer index = 0; index < strArrPKValue.length; index++) {

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO
				.read(Long.valueOf(strArrPKValue[index]));
				lObjTrnDcpsContribution.setRegStatus(1l);
				lObjOfflineContriDAO.update(lObjTrnDcpsContribution);
				lBlFlag = true;
			}

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		resObj.setResultValue(objectArgs);

		return resObj;
	}

	/*
	public ResultObject RjtContriToDDOAsst(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag;
		TrnDcpsContribution lObjTrnDcpsContribution = null;
		String toPost = null;
		String toLevel = null;
		Integer regStatus = null;

		try {
			// Sets the Session Information 
			setSessionInfo(objectArgs);

			// Initializes variables and DAOs 
			lBlFlag = false;
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			OfflineContriDAO lObjOfflineContriDAOForVoucher = new OfflineContriDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

			// Puts Post Id,JobTitle and Doc Id in the Map to reject request 

			// Gets the Contribution Ids from request to forward the contribution to

			String strPKValue = StringUtility.getParameter(
					"dcpsContributionIds", request).toString().trim();

			String[] strArrPKValue = strPKValue.split("~");

			// Rejects the contributions back to ATO and sets the RegStatus to  * -3 or -4.

			for (Integer index = 0; index < strArrPKValue.length; index++) {

				regStatus = lObjOfflineContriDAO
						.getRegStatusForContriId(strArrPKValue[index]);
				if (regStatus == 3 || regStatus == 2) // Online Contribution
				{
					toPost = lObjOfflineContriDAO
							.getInitUnitPostIdForContriIdOnline(strArrPKValue[0]);
					toLevel = gObjRsrcBndle.getString("DCPS.DDOASST");

					objectArgs.put("toPost", toPost);
					objectArgs.put("toPostId", toPost);
					objectArgs.put("toLevel", toLevel);
					objectArgs.put("FromPostId", gStrPostId);
					objectArgs.put("jobTitle", gObjRsrcBndle
							.getString("DCPS.OnlineContribution"));
					objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
							.getString("DCPS.OnlineContributionID")));
					objectArgs.put("Pkvalue", strArrPKValue[index]);
					WorkFlowDelegate.forward(objectArgs);

					lObjTrnDcpsContribution = new TrnDcpsContribution();
					lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO
							.read(Long.valueOf(strArrPKValue[index]));
					lObjTrnDcpsContribution.setRegStatus(-3l);
					lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
					lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
					lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
					lObjOfflineContriDAO.update(lObjTrnDcpsContribution);
				}
				if (regStatus == 4) // Manual Contribution
				{
					toPost = lObjOfflineContriDAO
							.getInitUnitPostIdForContriIdManual(strArrPKValue[0]);
					toLevel = gObjRsrcBndle.getString("DCPS.ATO");

					objectArgs.put("toPost", toPost);
					objectArgs.put("toPostId", toPost);
					objectArgs.put("toLevel", toLevel);
					objectArgs.put("FromPostId", gStrPostId);
					objectArgs.put("jobTitle", gObjRsrcBndle
							.getString("DCPS.Contribution"));
					objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
							.getString("DCPS.ContributionID")));
					objectArgs.put("Pkvalue", strArrPKValue[index]);
					WorkFlowDelegate.forward(objectArgs);

					lObjTrnDcpsContribution = new TrnDcpsContribution();
					lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO
							.read(Long.valueOf(strArrPKValue[index]));
					lObjTrnDcpsContribution.setRegStatus(-4l);
					lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
					lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
					lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
					lObjOfflineContriDAO.update(lObjTrnDcpsContribution);
				}

			}

			String lStrRemarksForRejection = StringUtility.getParameter(
					"remarksForRejection", request);

			String lStrVoucherContriId = StringUtility.getParameter(
					"voucherContriId", request);

			Long lLngVoucherContriId = null;
			if (!"".equals(lStrVoucherContriId)) {
				lLngVoucherContriId = Long.valueOf(lStrVoucherContriId);
			}

			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) lObjOfflineContriDAOForVoucher
					.read(lLngVoucherContriId);
			lObjMstDcpsContriVoucherDtls
					.setRemarksForRejection(lStrRemarksForRejection);
			if (regStatus == 4) {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(-4l); // From
				// Manual
			} else {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(-3l); // From
				// Online
			}
			lObjMstDcpsContriVoucherDtls.setUpdatedUserId(gLngUserId);
			lObjMstDcpsContriVoucherDtls.setUpdatedPostId(gLngPostId);
			lObjMstDcpsContriVoucherDtls.setUpdatedDate(gDtCurDate);
			lObjOfflineContriDAOForVoucher.update(lObjMstDcpsContriVoucherDtls);

			lBlFlag = true;

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		// Generates the XML response and sends the success flag 
		String lSBStatus = getResponseXMLDocForRejectContri(lBlFlag, regStatus)
				.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		resObj.setResultValue(objectArgs);

		return resObj;
	}

	 */

	// Above was the old method for rejecting both online and manual contributions. Below one is for online only.

	public ResultObject RjtContriToDDOAsst(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag;
		TrnDcpsContribution lObjTrnDcpsContribution = null;
		String toPost = null;
		String toLevel = null;
		Integer regStatus = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(objectArgs);

			/* Initializes variables and DAOs */
			lBlFlag = false;
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			OfflineContriDAO lObjOfflineContriDAOForVoucher = new OfflineContriDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

			/* Puts Post Id,JobTitle and Doc Id in the Map to reject request */

			/*
			 * Gets the Contribution Ids from request to forward the
			 * contribution to
			 */
			String strPKValue = StringUtility.getParameter(
					"dcpsContributionIds", request).toString().trim();

			String[] strArrPKValue = strPKValue.split("~");

			/*
			 * Rejects the contributions back to ATO and sets the RegStatus to
			 * -3 or -4.
			 */

			toPost = lObjOfflineContriDAO.getInitUnitPostIdForContriIdOnline(strArrPKValue[0].trim());
			toLevel = gObjRsrcBndle.getString("DCPS.DDOASST");
			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);
			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("jobTitle", gObjRsrcBndle.getString("DCPS.OnlineContribution"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.OnlineContributionID")));

			for (Integer index = 0; index < strArrPKValue.length; index++) {

				objectArgs.put("Pkvalue", strArrPKValue[index].trim());
				WorkFlowDelegate.forward(objectArgs);

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO
				.read(Long.valueOf(strArrPKValue[index].trim()));
				lObjTrnDcpsContribution.setRegStatus(-3l);
				lObjTrnDcpsContribution.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsContribution.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsContribution.setUpdatedDate(gDtCurDate);
				lObjOfflineContriDAO.update(lObjTrnDcpsContribution);

			}

			String lStrRemarksForRejection = StringUtility.getParameter(
					"remarksForRejection", request).trim();

			String lStrVoucherContriId = StringUtility.getParameter(
					"voucherContriId", request).trim();

			Long lLngVoucherContriId = null;
			if (!"".equals(lStrVoucherContriId)) {
				lLngVoucherContriId = Long.valueOf(lStrVoucherContriId);
			}

			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) lObjOfflineContriDAOForVoucher
			.read(lLngVoucherContriId);
			lObjMstDcpsContriVoucherDtls
			.setRemarksForRejection(lStrRemarksForRejection);
			/*
			if (regStatus == 4) {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(-4l); // From Manual
			} else {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(-3l); // From Online
			}
			 */
			// Rejection from Online only
			lObjMstDcpsContriVoucherDtls.setVoucherStatus(-3l); // From Online

			lObjMstDcpsContriVoucherDtls.setUpdatedUserId(gLngUserId);
			lObjMstDcpsContriVoucherDtls.setUpdatedPostId(gLngPostId);
			lObjMstDcpsContriVoucherDtls.setUpdatedDate(gDtCurDate);
			lObjOfflineContriDAOForVoucher.update(lObjMstDcpsContriVoucherDtls);

			lBlFlag = true;

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDocForRejectContri(lBlFlag, regStatus)
		.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		resObj.setResultValue(objectArgs);

		return resObj;
	}

	// Old Method Before Workflow
	/*
	public ResultObject saveVoucherDtlsForContri(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		resObj.setResultCode(-1);
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		MstDcpsContriVoucherDtls lObjPreviousMstDcpsContriVoucherDtls = null;
		Boolean lBlFlag = null;
		Long lLngDcpsContriVoucherId = null;
		Long lLongVoucherNo = null;
		Date lDateVoucherDate = null;
		Long lLongTreasuryCode = null;
		Double voucherAmount = null;
		Long lLngContriVoucherIdToBePassed = null;

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy");

			String lStrUser = StringUtility.getParameter("User", request)
					.trim();
			inputMap.put("RLUser", lStrUser);

			String lStrUse = StringUtility.getParameter("Use", request).trim();
			inputMap.put("RLUse", lStrUse);

			String lStrTypeOfPaymentMaster = StringUtility.getParameter(
					"cmbTypeOfPaymentMaster", request).trim();
			inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster);

			String lStrDelayedMonth = "";
			String lStrDelayedYear = "";

			if (lStrTypeOfPaymentMaster.equals("700047")) {
				lStrDelayedMonth = StringUtility.getParameter(
						"cmbDelayedMonth", request).trim();
				lStrDelayedYear = StringUtility.getParameter("cmbDelayedYear",
						request).trim();
			}

			inputMap.put("delayedMonth", lStrDelayedMonth);
			inputMap.put("delayedYear", lStrDelayedYear);

			String lStrApprovedFlag = "";

			if (!"".equalsIgnoreCase(StringUtility.getParameter("approvedFlag",
					request).trim())) {
				lStrApprovedFlag = StringUtility.getParameter("approvedFlag",
						request).trim();
			}

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrFreezeFlag = null;
			if (inputMap.containsKey("FreezeFlag")) {
				lStrFreezeFlag = inputMap.get("FreezeFlag").toString().trim();
			}
			Integer lIntFreezeFlag = 0;
			String lStrDDOCode = null;
			String lStrTreasuryCode = null;
			String lStrMonthId = null;
			Long lLongMonthId = 0l;
			String lStrYearId = null;
			Long lLongYearId = 0l;
			String lStrBillGroupId = null;
			String lStrVoucherDate = null;
			String lStrVoucherNo = null;
			if (lStrFreezeFlag != null) {
				lIntFreezeFlag = Integer.parseInt(lStrFreezeFlag);

			}
			if (lIntFreezeFlag == 1) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId).trim();
				lStrTreasuryCode = lObjDcpsCommonDAO
						.getTreasuryCodeForDDO(lStrDDOCode.trim()).trim();
				lStrMonthId = inputMap.get("cmbMonth").toString().trim();
				lStrYearId = inputMap.get("cmbYear").toString().trim();

				String lStrStartDate = "01/" + lStrMonthId + "/" + lStrYearId;

				Date lDtStartDate = simpleDateFormat.parse(lStrStartDate.trim());

				lStrYearId = lObjDcpsCommonDAO
						.getFinYearIdForDate(lDtStartDate);

				lStrVoucherNo = inputMap.get("txtVoucherNo").toString().trim();
				lStrVoucherDate = inputMap.get("txtVoucherDt").toString().trim();

				lStrBillGroupId = inputMap.get("cmbBillGroup").toString().trim();

				insertRegularContributions(inputMap);
				//resObj = serv.executeService("insertRegularContributions", inputMap);

			} else {

				lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
				lStrTreasuryCode = StringUtility.getParameter(
						"cmbTreasuryCode", request).trim();
				lStrMonthId = StringUtility.getParameter("cmbMonth", request).trim();

				lStrYearId = StringUtility.getParameter("cmbYear", request).trim();

				lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",
						request).trim();
				lStrVoucherNo = StringUtility.getParameter("txtVoucherNo",
						request).trim();
				lStrVoucherDate = StringUtility.getParameter("txtVoucherDate",
						request).trim();

			}
			lLongMonthId = Long.valueOf(lStrMonthId);
			lLongYearId = Long.valueOf(lStrYearId);

			if (lStrTreasuryCode != null
					&& !"".equalsIgnoreCase(lStrTreasuryCode)) {
				lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);
			}

			Long lLongBillGroupId = Long.valueOf(lStrBillGroupId);

			if (lStrVoucherNo != null && !"".equalsIgnoreCase(lStrVoucherNo)) {
				lLongVoucherNo = Long.valueOf(lStrVoucherNo.trim());
			}

			if (lStrVoucherDate != null && !"".equals(lStrVoucherDate.trim())) {
				lDateVoucherDate = simpleDateFormat.parse(lStrVoucherDate.trim());
			}

			lObjPreviousMstDcpsContriVoucherDtls = lObjOfflineContriDAO
					.getContriVoucherVOForInputDtls(lLongYearId, lLongMonthId,
							lStrDDOCode, lLongBillGroupId);

			if (lObjPreviousMstDcpsContriVoucherDtls == null) {
				// Create
				lLngDcpsContriVoucherId = IFMSCommonServiceImpl.getNextSeqNum(
						"mst_dcps_contri_voucher_dtls", inputMap);
				lObjMstDcpsContriVoucherDtls = new MstDcpsContriVoucherDtls();
				lObjMstDcpsContriVoucherDtls
						.setDcpsContriVoucherDtlsId(lLngDcpsContriVoucherId);
				lObjMstDcpsContriVoucherDtls.setBillGroupId(lLongBillGroupId);
				lObjMstDcpsContriVoucherDtls.setCreatedDate(gDtCurDate);
				lObjMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
				lObjMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);
				lObjMstDcpsContriVoucherDtls.setPostId(gLngPostId);
				lObjMstDcpsContriVoucherDtls.setUserId(gLngUserId);

				if (lStrVoucherDate != null
						&& !"".equals(lStrVoucherDate.trim())) {
					lObjMstDcpsContriVoucherDtls
							.setVoucherDate(lDateVoucherDate);
				} else {
					lObjMstDcpsContriVoucherDtls.setVoucherDate(null);
				}

				if (lStrVoucherNo != null
						&& !"".equalsIgnoreCase(lStrVoucherNo)) {
					lObjMstDcpsContriVoucherDtls.setVoucherNo(lLongVoucherNo);
				} else {
					lObjMstDcpsContriVoucherDtls.setVoucherNo(null);
				}

				lObjMstDcpsContriVoucherDtls.setTreasuryCode(lLongTreasuryCode);
				lObjMstDcpsContriVoucherDtls.setYearId(lLongYearId);

				if (lStrUser.equals("ATO")) {
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(null);
				}
				if (lStrUser.equals("TO")) {
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					voucherAmount = lObjOfflineContriDAO
							.getTotalVoucherAmountForGivenVoucher(lLongMonthId,
									lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls
							.setVoucherAmount(voucherAmount);
					lObjMstDcpsContriVoucherDtls.setStatus('A');
				}
				if (lStrUser.equals("DDO")) {
					if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {

						lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
						lObjMstDcpsContriVoucherDtls.setStatus('A');
						voucherAmount = lObjOfflineContriDAO
								.getTotalVoucherAmountForGivenVoucher(
										lLongMonthId, lLongYearId,
										lLongBillGroupId, lStrDDOCode);
						lObjMstDcpsContriVoucherDtls
								.setVoucherAmount(voucherAmount);
					} else {
						lObjMstDcpsContriVoucherDtls.setVoucherStatus(3l); // Since
						// Forwarded
						// by
						// DDO
						// in
						// Online
					}
				}

				if (lIntFreezeFlag == 1) {

					voucherAmount = lObjOfflineContriDAO
							.getTotalVoucherAmountForGivenVoucher(lLongMonthId,
									lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls
							.setVoucherAmount(voucherAmount);

				}
				lObjMstDcpsContriVoucherDtls.setManuallyMatched(0l);
				lObjMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
				lObjMstDcpsContriVoucherDtls.setReversionFlag(0l);

				lObjOfflineContriDAO.create(lObjMstDcpsContriVoucherDtls);

				lLngContriVoucherIdToBePassed = lLngDcpsContriVoucherId;

				inputMap.put("voucherVOToCheckThruSchdlr", lObjMstDcpsContriVoucherDtls);

			} else {
				// Updates
				lObjPreviousMstDcpsContriVoucherDtls
						.setBillGroupId(lLongBillGroupId);
				lObjPreviousMstDcpsContriVoucherDtls.setUpdatedDate(gDtCurDate);
				lObjPreviousMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
				lObjPreviousMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);

				if (lStrVoucherDate != null
						&& !"".equals(lStrVoucherDate.trim())) {
					lObjPreviousMstDcpsContriVoucherDtls
							.setVoucherDate(lDateVoucherDate);
				} else {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherDate(null);
				}

				if (lStrVoucherNo != null
						&& !"".equalsIgnoreCase(lStrVoucherNo)) {
					lObjPreviousMstDcpsContriVoucherDtls
							.setVoucherNo(lLongVoucherNo);
				} else {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherNo(null);
				}

				lObjPreviousMstDcpsContriVoucherDtls
						.setTreasuryCode(lLongTreasuryCode);
				lObjPreviousMstDcpsContriVoucherDtls.setYearId(lLongYearId);
				lObjPreviousMstDcpsContriVoucherDtls
						.setUpdatedPostId(gLngPostId);
				lObjPreviousMstDcpsContriVoucherDtls
						.setUpdatedUserId(gLngUserId);

				if (lStrUser.equals("ATO")) {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(null);
				}
				if (lStrUser.equals("TO")) {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					lObjPreviousMstDcpsContriVoucherDtls.setStatus('A');
					voucherAmount = lObjOfflineContriDAO
							.getTotalVoucherAmountForGivenVoucher(lLongMonthId,
									lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjPreviousMstDcpsContriVoucherDtls
							.setVoucherAmount(voucherAmount);
				}
				if (lStrUser.equals("DDO")) {
					if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {
						lObjPreviousMstDcpsContriVoucherDtls
								.setVoucherStatus(1l);
						voucherAmount = lObjOfflineContriDAO
								.getTotalVoucherAmountForGivenVoucher(
										lLongMonthId, lLongYearId,
										lLongBillGroupId, lStrDDOCode);
						lObjPreviousMstDcpsContriVoucherDtls
								.setVoucherAmount(voucherAmount);
					} else {
						lObjPreviousMstDcpsContriVoucherDtls
								.setVoucherStatus(3l); // Since Forwarded by DDO
						// in Online
					}
				}

				lObjPreviousMstDcpsContriVoucherDtls.setManuallyMatched(0l);
				lObjPreviousMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
				lObjPreviousMstDcpsContriVoucherDtls.setReversionFlag(0l);

				lObjOfflineContriDAO.update(lObjPreviousMstDcpsContriVoucherDtls);

				lLngContriVoucherIdToBePassed = lObjPreviousMstDcpsContriVoucherDtls
						.getDcpsContriVoucherDtlsId();

				inputMap.put("voucherVOToCheckThruSchdlr", lObjPreviousMstDcpsContriVoucherDtls);
			}

		inputMap.put("lLngContriVoucherIdToBePassed",lLngContriVoucherIdToBePassed);

		lBlFlag = true;

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		resObj.setResultCode(1);
		return resObj;
	}

	 */

	/*public ResultObject saveVoucherDtlsForContriSchdlr(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		resObj.setResultCode(-1);
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		MstDcpsContriVoucherDtls lObjPreviousMstDcpsContriVoucherDtls = null;
		Boolean lBlFlag = null;
		Long lLngDcpsContriVoucherId = null;
		Long lLongVoucherNo = null;
		Date lDateVoucherDate = null;
		Long lLongTreasuryCode = null;
		Double voucherAmount = null;
		Long lLngContriVoucherIdToBePassed = null;
		Session currSession =(Session)inputMap.get("currentSessionForDcps");

			setSessionInfoSchdlr(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
			Session hibSession1 = serv.getSessionFactorySlave().openSession();
			hibSession1.getTransaction().begin();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			String lStrUser = inputMap.get("User") != null ? inputMap.get("User").toString().trim():"";
			String lStrUse = inputMap.get("Use") != null ? inputMap.get("Use").toString().trim():"";
			//String lStrUser = StringUtility.getParameter("User", request).trim();
			inputMap.put("RLUser", lStrUser);

			//String lStrUse = StringUtility.getParameter("Use", request).trim();
			inputMap.put("RLUse", lStrUse);

			//String lStrTypeOfPaymentMaster = StringUtility.getParameter("cmbTypeOfPaymentMaster", request).trim();
			String lStrTypeOfPaymentMaster = "700046";
			inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster);


			String lStrDelayedMonth = "";
			String lStrDelayedYear = "";

			if (lStrTypeOfPaymentMaster.equals("700047")) {
				lStrDelayedMonth = StringUtility.getParameter("cmbDelayedMonth", request).trim();
				lStrDelayedYear = StringUtility.getParameter("cmbDelayedYear",request).trim();
			}

			inputMap.put("delayedMonth", lStrDelayedMonth);
			inputMap.put("delayedYear", lStrDelayedYear);


			String lStrApprovedFlag = "";


			if (!"".equalsIgnoreCase(StringUtility.getParameter("approvedFlag",
					request).trim())) {
				lStrApprovedFlag = StringUtility.getParameter("approvedFlag",
						request).trim();
			}


			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrFreezeFlag = null;
			if (inputMap.containsKey("FreezeFlag")) {
				lStrFreezeFlag = inputMap.get("FreezeFlag").toString().trim();
			}
			Integer lIntFreezeFlag = 0;
			String lStrDDOCode = null;
			String lStrTreasuryCode = null;
			String lStrMonthId = null;
			Long lLongMonthId = 0l;
			String lStrYearId = null;
			Long lLongYearId = 0l;
			String lStrBillGroupId = null;
			String lStrVoucherDate = null;
			String lStrVoucherNo = null;
			if (lStrFreezeFlag != null) {
				lIntFreezeFlag = Integer.parseInt(lStrFreezeFlag);

			}
			if (lIntFreezeFlag == 1) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
				lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDDOCode.trim()).trim();
				lStrMonthId = inputMap.get("cmbMonth").toString().trim();
				lStrYearId = inputMap.get("cmbYear").toString().trim();

				String lStrStartDate = "01/" + lStrMonthId + "/" + lStrYearId;
				Date lDtStartDate = new Date();
				try{
					lDtStartDate = simpleDateFormat.parse(lStrStartDate);
				}catch(Exception e){
					//e.printStackTrace();
					gLogger.error("Error is :" + e, e);
					throw e;
				}

				lStrYearId = lObjDcpsCommonDAO
						.getFinYearIdForDate(lDtStartDate);

				lStrVoucherNo = inputMap.get("txtVoucherNo").toString().trim();
				lStrVoucherDate = inputMap.get("txtVoucherDt").toString().trim();

				lStrBillGroupId = inputMap.get("cmbBillGroup").toString().trim();
			} else {

				//lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
				//lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode", request).trim();
				//lStrMonthId = StringUtility.getParameter("cmbMonth", request).trim();
				//lStrYearId = StringUtility.getParameter("cmbYear", request).trim();
				//lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",request).trim();

				//lStrVoucherNo = StringUtility.getParameter("txtVoucherNo",request).trim();
				//lStrVoucherDate = StringUtility.getParameter("txtVoucherDate",request).trim();

				lStrDDOCode = inputMap.get("cmbDDOCode") != null ? inputMap.get("cmbDDOCode").toString().trim():"";
				lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDDOCode.trim()).trim();
				lStrMonthId = inputMap.get("cmbMonth") != null ? inputMap.get("cmbMonth").toString().trim():"";
				lStrYearId = inputMap.get("cmbYear") != null ? inputMap.get("cmbYear").toString().trim():"";
				lStrBillGroupId = inputMap.get("cmbBillGroup") != null ? inputMap.get("cmbBillGroup").toString().trim():"";
				lStrVoucherNo = "";
				lStrVoucherDate = "";

			}

			lLongMonthId = Long.valueOf(lStrMonthId);
			lLongYearId = Long.valueOf(lStrYearId);

			if (lStrTreasuryCode != null && !"".equalsIgnoreCase(lStrTreasuryCode)) {
				lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);
			}

			Long lLongBillGroupId = Long.valueOf(lStrBillGroupId);

			if (lStrVoucherNo != null && !"".equalsIgnoreCase(lStrVoucherNo)) {
				lLongVoucherNo = Long.valueOf(lStrVoucherNo);
			}

			if (lStrVoucherDate != null && !"".equals(lStrVoucherDate.trim())) {
				try{
					lDateVoucherDate = simpleDateFormat.parse(lStrVoucherDate.trim());
				}catch(Exception e){
					//e.printStackTrace();
					gLogger.error("Error is :" + e, e);
					throw e;
				}
			}

			lObjPreviousMstDcpsContriVoucherDtls = lObjOfflineContriDAO.getContriVoucherVOForInputDtlsSchdlr(lLongYearId, lLongMonthId,lStrDDOCode, lLongBillGroupId,inputMap);

			Map loginMap =(HashMap) inputMap.get("baseLoginMap");

			if (lObjPreviousMstDcpsContriVoucherDtls == null) {
				// Create
				try{
				lLngDcpsContriVoucherId = IFMSCommonServiceImpl.getNextSeqNum(
						"mst_dcps_contri_voucher_dtls", inputMap);
				}catch(Exception e){
					e.printStackTrace();
					gLogger.error("Error is :" + e, e);
					throw e;
				}
				lObjMstDcpsContriVoucherDtls = new MstDcpsContriVoucherDtls();
				lObjMstDcpsContriVoucherDtls.setDcpsContriVoucherDtlsId(lLngDcpsContriVoucherId);
				lObjMstDcpsContriVoucherDtls.setBillGroupId(lLongBillGroupId);

				Date lDtCurrDt = new Date();
				lObjMstDcpsContriVoucherDtls.setCreatedDate(lDtCurrDt);

				lObjMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
				lObjMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);

				Long lLocalPostId = Long.valueOf((loginMap.get("postId").toString().trim()));
				Long lLocalUserId = Long.valueOf((loginMap.get("userId").toString().trim()));
				lObjMstDcpsContriVoucherDtls.setPostId(lLocalPostId);
				lObjMstDcpsContriVoucherDtls.setUserId(lLocalUserId);
				lObjMstDcpsContriVoucherDtls.setSchedulerUsed('Y');

				if (lStrVoucherDate != null
						&& !"".equals(lStrVoucherDate.trim())) {
					lObjMstDcpsContriVoucherDtls
							.setVoucherDate(lDateVoucherDate);
				} else {
					lObjMstDcpsContriVoucherDtls.setVoucherDate(null);
				}

				if (lStrVoucherNo != null
						&& !"".equalsIgnoreCase(lStrVoucherNo)) {
					lObjMstDcpsContriVoucherDtls.setVoucherNo(lLongVoucherNo);
				} else {
					lObjMstDcpsContriVoucherDtls.setVoucherNo(null);
				}

				lObjMstDcpsContriVoucherDtls.setTreasuryCode(lLongTreasuryCode);
				lObjMstDcpsContriVoucherDtls.setYearId(lLongYearId);

				if (lStrUser.equals("ATO")) {
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(null);
				}
				if (lStrUser.equals("TO")) {
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					lObjMstDcpsContriVoucherDtls.setStatus('A');
				}
				if (lStrUser.equals("DDO")) {
					if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {

						lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
						lObjMstDcpsContriVoucherDtls.setStatus('A');
						voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId, lLongYearId,lLongBillGroupId, lStrDDOCode);
						lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					} else {
						lObjMstDcpsContriVoucherDtls.setVoucherStatus(3l); // Since
						// Forwarded
						// by
						// DDO
						// in
						// Online
					}
				}

				if (lIntFreezeFlag == 1) {

					voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);

				}
				lObjMstDcpsContriVoucherDtls.setManuallyMatched(0l);
				lObjMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
				lObjMstDcpsContriVoucherDtls.setReversionFlag(0l);

				//lObjOfflineContriDAO.create(lObjMstDcpsContriVoucherDtls);
				currSession.save(lObjMstDcpsContriVoucherDtls);

				lLngContriVoucherIdToBePassed = lLngDcpsContriVoucherId;

			} else {
				// Updates
				lObjPreviousMstDcpsContriVoucherDtls.setBillGroupId(lLongBillGroupId);
				lObjPreviousMstDcpsContriVoucherDtls.setUpdatedDate(gDtCurDate);
				lObjPreviousMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
				lObjPreviousMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);
				lObjPreviousMstDcpsContriVoucherDtls.setSchedulerUsed('Y');

				if (lStrVoucherDate != null
						&& !"".equals(lStrVoucherDate.trim())) {
					lObjPreviousMstDcpsContriVoucherDtls
							.setVoucherDate(lDateVoucherDate);
				} else {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherDate(null);
				}

				if (lStrVoucherNo != null
						&& !"".equalsIgnoreCase(lStrVoucherNo)) {
					lObjPreviousMstDcpsContriVoucherDtls
							.setVoucherNo(lLongVoucherNo);
				} else {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherNo(null);
				}

				lObjPreviousMstDcpsContriVoucherDtls
						.setTreasuryCode(lLongTreasuryCode);
				lObjPreviousMstDcpsContriVoucherDtls.setYearId(lLongYearId);
				lObjPreviousMstDcpsContriVoucherDtls
						.setUpdatedPostId(gLngPostId);
				lObjPreviousMstDcpsContriVoucherDtls
						.setUpdatedUserId(gLngUserId);

				if (lStrUser.equals("ATO")) {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(null);
				}
				if (lStrUser.equals("TO")) {
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					lObjPreviousMstDcpsContriVoucherDtls.setStatus('A');
					voucherAmount = lObjOfflineContriDAO
							.getTotalVoucherAmountForGivenVoucher(lLongMonthId,
									lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjPreviousMstDcpsContriVoucherDtls
							.setVoucherAmount(voucherAmount);
				}
				if (lStrUser.equals("DDO")) {
					if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {
						lObjPreviousMstDcpsContriVoucherDtls
								.setVoucherStatus(1l);
						voucherAmount = lObjOfflineContriDAO
								.getTotalVoucherAmountForGivenVoucher(
										lLongMonthId, lLongYearId,
										lLongBillGroupId, lStrDDOCode);
						lObjPreviousMstDcpsContriVoucherDtls
								.setVoucherAmount(voucherAmount);
					} else {
						lObjPreviousMstDcpsContriVoucherDtls
								.setVoucherStatus(3l); // Since Forwarded by DDO
						// in Online
					}
				}

				lObjPreviousMstDcpsContriVoucherDtls.setManuallyMatched(0l);
				lObjPreviousMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
				lObjPreviousMstDcpsContriVoucherDtls.setReversionFlag(0l);

				//lObjOfflineContriDAO.update(lObjPreviousMstDcpsContriVoucherDtls);
				currSession.update(lObjPreviousMstDcpsContriVoucherDtls);

				lLngContriVoucherIdToBePassed = lObjPreviousMstDcpsContriVoucherDtls
						.getDcpsContriVoucherDtlsId();
			}

			inputMap.put("lLngContriVoucherIdToBePassed",
					lLngContriVoucherIdToBePassed);

			lBlFlag = true;
			hibSession1.getTransaction().commit();
			hibSession1.close();

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		resObj.setResultCode(1);
		return resObj;
	}*/

	public ResultObject revertAcceptedContri(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Long voucherNo = null;
		Long month = null;
		Long year = null;
		Long treasuryCode = null;
		String reasonForRevert = null;
		Long billGroupId = null;
		List treasuries = null;
		List ddonames = null;
		Boolean lBlFlag = null;
		List lLstBillGroups = null;
		List lLstMonths = null;
		List lLstYears = null;
		List listReversionRequests = null;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			// Checks whether page is loaded for the first time or request for
			// reversion is sent from the same page

			if (StringUtility.getParameter("revertRequest", request).equals(
			"yes")) {
				if (!StringUtility.getParameter("voucherNo", request)
						.equalsIgnoreCase("")) {
					voucherNo = Long.valueOf(StringUtility.getParameter(
							"voucherNo", request).trim());
				}

				if (!StringUtility.getParameter("cmbTreasuryCode", request)
						.equalsIgnoreCase("")) {
					treasuryCode = Long.valueOf(StringUtility.getParameter(
							"cmbTreasuryCode", request).trim());
				}

				if (!StringUtility.getParameter("cmbMonth", request)
						.equalsIgnoreCase("")) {
					month = Long.valueOf(StringUtility.getParameter("cmbMonth",
							request).trim());
				}

				if (!StringUtility.getParameter("cmbYear", request)
						.equalsIgnoreCase("")) {
					year = Long.valueOf(StringUtility.getParameter("cmbYear",
							request).trim());
				}

				if (!StringUtility.getParameter("txtReasonForRevert", request)
						.equalsIgnoreCase("")) {
					reasonForRevert = StringUtility.getParameter(
							"txtReasonForRevert", request).trim();
				}

				if (!StringUtility.getParameter("cmbBillGroup", request)
						.equalsIgnoreCase("")) {
					billGroupId = Long.valueOf(StringUtility.getParameter(
							"cmbBillGroup", request).trim());
				}

				lObjOfflineContriDAO.revertRequestAndUpdtVoucherVO(year, month,
						billGroupId, treasuryCode, voucherNo, reasonForRevert);

				// Sets voucher_status to -1 on TO's requesting for reversion

				lBlFlag = true;

				String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
						lSBStatus).toString();

				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");

			} else {
				/*
				 * Get All the Bill Groups under selected DDO (As of now shown
				 * all)
				 */
				//lLstBillGroups = lObjDcpsCommonDAO.getBillGroups();

				/* Get Months */
				lLstMonths = lObjDcpsCommonDAO.getMonths();

				/* Get Years */
				lLstYears = lObjDcpsCommonDAO.getFinyears();

				// treasuries = lObjOfflineContriDAO.getAllTreasuries();

				treasuries = lObjOfflineContriDAO
				.getCurrentTreasury(gStrLocationCode);

				ddonames = lObjOfflineContriDAO.getAllDDO(gStrLocationCode);

				listReversionRequests = lObjOfflineContriDAO
				.getAllRevertRequestsForTreasury(gStrLocationCode);

				inputMap.put("listReversionRequests", listReversionRequests);
				inputMap.put("TREASURIES", treasuries);
				inputMap.put("DDONAMES", ddonames);
				inputMap.put("YEARS", lLstYears);
				//inputMap.put("BILLGROUPS", lLstBillGroups);
				inputMap.put("MONTHS", lLstMonths);
				inputMap.put("treasuryCode", gStrLocationCode);

				resObj.setViewName("RevertAcceptedData");
			}

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject popUpVoucherDtls(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstDcpsContriVoucherDtls lLObjMstContriVoucherDtls = null;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			String lStrDDOCode = StringUtility.getParameter("cmbDDOCode",
					request);
			Long monthId = Long.parseLong(StringUtility.getParameter(
					"cmbMonth", request));
			Long yearId = Long.parseLong(StringUtility.getParameter("cmbYear",
					request));
			Long lLongbillGroupId = Long.valueOf(StringUtility.getParameter(
					"cmbBillGroup", request));
			Long treasuryCode = Long.valueOf(StringUtility.getParameter(
					"cmbTreasuryCode", request));

			lLObjMstContriVoucherDtls = lObjOfflineContriDAO
			.getContriVoucherVOForInputDtlsForPopup(yearId, monthId,
					lStrDDOCode, lLongbillGroupId, treasuryCode);

			String lSBStatus = getResponseXMLDocForPopupVoucherDtls(
					lLObjMstContriVoucherDtls).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	public ResultObject approveRevertRequests(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List listReversionRequests = null;
		String lStrVoucherPKs = null;
		String[] lStrArrVoucherPKs = null;
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Boolean lBlFlag = null;
		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());

			if (StringUtility.getParameter("revertRequest", request).equals(
			"yes")) {

				if (!StringUtility.getParameter("voucherPKs", request)
						.equalsIgnoreCase("")
						&& StringUtility.getParameter("voucherPKs", request) != null) {
					lStrVoucherPKs = StringUtility.getParameter("voucherPKs",
							request);
				}

				lStrVoucherPKs = StringUtility.getParameter("voucherPKs",
						request);

				lStrArrVoucherPKs = new String[lStrVoucherPKs.split("~").length];

				lStrArrVoucherPKs = lStrVoucherPKs.split("~");

				if (StringUtility.getParameter("requestType", request).equals(
				"Approve")) {

					for (Integer lInt = 0; lInt < lStrArrVoucherPKs.length; lInt++) {
						if (lStrArrVoucherPKs[lInt] != null
								&& !lStrArrVoucherPKs[lInt]
								                      .equalsIgnoreCase("")) {
							lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) lObjOfflineContriDAO
							.read(Long.valueOf(lStrArrVoucherPKs[lInt]));
							lObjMstDcpsContriVoucherDtls.setReversionFlag(2l);
							lObjOfflineContriDAO
							.update(lObjMstDcpsContriVoucherDtls);
						}
					}

				}
				if (StringUtility.getParameter("requestType", request).equals(
				"Reject")) {

					for (Integer lInt = 0; lInt < lStrArrVoucherPKs.length; lInt++) {
						if (lStrArrVoucherPKs[lInt] != null
								&& !lStrArrVoucherPKs[lInt]
								                      .equalsIgnoreCase("")) {
							lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) lObjOfflineContriDAO
							.read(Long.valueOf(lStrArrVoucherPKs[lInt]));
							lObjMstDcpsContriVoucherDtls.setReversionFlag(-2l);

							// sets voucher_status to -2 on rejecting reversion
							// request
							lObjOfflineContriDAO
							.update(lObjMstDcpsContriVoucherDtls);
						}
					}

				}

				lBlFlag = true;

				String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
						lSBStatus).toString();

				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
			}

			else {

				listReversionRequests = lObjOfflineContriDAO
				.getAllRevertRequestsForSRKA();
				inputMap.put("listReversionRequests", listReversionRequests);
				resObj.setViewName("approveRevertRequests");

			}

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	private StringBuilder getResponseXMLDocForPopupVoucherDtls(
			MstDcpsContriVoucherDtls lLObjMstContriVoucherDtls) {

		StringBuilder lStrBldXML = new StringBuilder();

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(null,
				serv.getSessionFactory());
		MstDcpsBillGroup lObjMstDcpsBillGroup = null;
		String lStrVoucherDate = "";

		try {
			if (lLObjMstContriVoucherDtls != null) {
				if(lLObjMstContriVoucherDtls.getVoucherDate() != null)
				{
					lStrVoucherDate = sdf2.format(sdf1
							.parse(lLObjMstContriVoucherDtls.getVoucherDate()
									.toString()));
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (lLObjMstContriVoucherDtls != null) {
			lObjMstDcpsBillGroup = lObjDdoBillGroupDAO
			.getBillGroupDtlsForBillGroupId(lLObjMstContriVoucherDtls
					.getBillGroupId());
		}

		lStrBldXML.append("<XMLDOC>");

		if (lLObjMstContriVoucherDtls != null) {
			lStrBldXML.append("<schemeName>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(lObjMstDcpsBillGroup.getDcpsDdoBillSchemeName());
			lStrBldXML.append("]]>");
			lStrBldXML.append("</schemeName>");
		} else {
			lStrBldXML.append("<schemeName>");
			lStrBldXML.append("");
			lStrBldXML.append("</schemeName>");
		}

		if (lLObjMstContriVoucherDtls != null && lLObjMstContriVoucherDtls.getVoucherNo() != null) {
			lStrBldXML.append("<voucherNo>");
			lStrBldXML.append(lLObjMstContriVoucherDtls.getVoucherNo());
			lStrBldXML.append("</voucherNo>");
		} else {
			lStrBldXML.append("<voucherNo>");
			lStrBldXML.append("");
			lStrBldXML.append("</voucherNo>");
		}

		if (lLObjMstContriVoucherDtls != null) {
			lStrBldXML.append("<voucherDate>");
			lStrBldXML.append(lStrVoucherDate);
			lStrBldXML.append("</voucherDate>");
		} else {
			lStrBldXML.append("<voucherDate>");
			lStrBldXML.append("");
			lStrBldXML.append("</voucherDate>");
		}

		if (lLObjMstContriVoucherDtls != null) {
			lStrBldXML.append("<reasonForReversion>");
			if (lLObjMstContriVoucherDtls.getReasonForReversion() != null
					&& !lLObjMstContriVoucherDtls.getReasonForReversion()
					.equalsIgnoreCase("")) {
				lStrBldXML.append(lLObjMstContriVoucherDtls
						.getReasonForReversion());
			}
			lStrBldXML.append("</reasonForReversion>");
		} else {
			lStrBldXML.append("<reasonForReversion>");
			lStrBldXML.append("");
			lStrBldXML.append("</reasonForReversion>");
		}

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;

	}

	public ResultObject checkContriOfEmpForSelectedPeriod(
			Map<String, Object> inputMap) {
		gLogger.info("checkContriOfEmpForSelectedPeriod is *********");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = null;
		Boolean lBlFlagForDiffArrear = null;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String lStrDcpsEmpId = StringUtility.getParameter("dcpsEmpId",
					request);
			String lStrContriStartDate = StringUtility.getParameter(
					"contriStartDate", request);
			String lStrContriEndDate = StringUtility.getParameter(
					"contriEndDate", request);
			String lStrTypeOfPayment = StringUtility.getParameter(
					"typeOfPayment", request);
			/*String lStrPayCommission = StringUtility.getParameter(
					"payCommission", request);*/
			gLogger.info("lStrDcpsEmpId is *********"+lStrDcpsEmpId);
			gLogger.info("contriStartDate is *********"+lStrContriStartDate);
			gLogger.info("contriEndDate is *********"+lStrContriEndDate);
			gLogger.info("typeOfPayment is *********"+lStrTypeOfPayment);

			Long lLngDcpsEmpId = null;
			Date lDateContriStartDate = null;
			Date lDateContriEndDate = null;
			String voucherNo="";
			String voucherDate="";
			Double Contribution=null;
			//$t 2019
			Double contributionEmplr=null;
			//$t 17Jan2022
			String startDate="";
			String endDate="";
			String lSBStatus =null;
			String status = null;
			if (!"".equals(lStrDcpsEmpId)) {
				lLngDcpsEmpId = Long.valueOf(lStrDcpsEmpId.trim());
			}
			if (!"".equals(lStrContriStartDate)) {
				lDateContriStartDate = sdf.parse(lStrContriStartDate.trim());
			}
			if (!"".equals(lStrContriEndDate)) {
				lDateContriEndDate = sdf.parse(lStrContriEndDate.trim());
			}

			gLogger.info("lLngDcpsEmpId is *********"+lLngDcpsEmpId);
			gLogger.info("lDateContriStartDate is *********"+lDateContriStartDate);
			gLogger.info("lDateContriEndDate is *********"+lDateContriEndDate);
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,serv.getSessionFactory());

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			MstEmp mstEmp = (MstEmp) lObjNewRegDdoDAO.read(lLngDcpsEmpId);
			gLogger.info("mstEmp is *********"+mstEmp);
			String payComm = mstEmp.getPayCommission();
			gLogger.info("payComm is *********"+payComm);
		//	lObjOfflineContriDAO
			lBlFlag = lObjOfflineContriDAO.checkContriOfEmpForSelectedPeriod_new(
					lLngDcpsEmpId, lDateContriStartDate, lDateContriEndDate,
					lStrTypeOfPayment,lStrDdoCode,payComm);
			
		
			//lBlFlag=true -no data 
			//lBlFlag=false - data available 
			if(!lBlFlag){
				
				if(lStrTypeOfPayment.equalsIgnoreCase("700080"))
				{
					lBlFlag = null;
					lBlFlag = lObjOfflineContriDAO.checkContriOfEmpForSelectedPeriod_newARREAR(
							lLngDcpsEmpId, lDateContriStartDate, lDateContriEndDate,
							lStrTypeOfPayment,lStrDdoCode,payComm);
				}
				
				List lEmployeeRecords=lObjOfflineContriDAO.getContriOfEmpForSelectedPeriod(
						lLngDcpsEmpId, lDateContriStartDate, lDateContriEndDate,
						lStrTypeOfPayment,lStrDdoCode);
				if(lEmployeeRecords!=null && lEmployeeRecords.size()>0){
					Iterator it = lEmployeeRecords.iterator();
					Object [] tuple=null;
					while (it.hasNext())
					{
						tuple = (Object[]) it.next();
						if(tuple[0]!=null)
						{
							voucherNo =tuple[0].toString();
						}
						if(tuple[1]!=null)
						{
							voucherDate =tuple[1].toString();
						}
						
						Contribution =Double.parseDouble(tuple[2].toString());
						status =tuple[3].toString();
						gLogger.info("status is *********"+status);
						//$t 2019
						contributionEmplr =Double.parseDouble(tuple[4].toString());
						gLogger.info("contributionEmplr is *********"+contributionEmplr);
						//$t 2019
						startDate=tuple[5].toString();
						endDate=tuple[6].toString();
						 lSBStatus = getResponseXMLDocForOffline(lBlFlag,voucherNo,voucherDate,Contribution,status,contributionEmplr,startDate,endDate).toString();
					}
				}
				
				
			}
			else{
				//$t 2019
				lSBStatus= getResponseXMLDocForOffline(lBlFlag,"NA","NA",0.0,"NA",0.0,"NA","NA").toString();
			}

			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	public ResultObject displayEmployeeDtls(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstEmp lObjMstEmp = null;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(MstEmp.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			String lStrDcpsId = StringUtility.getParameter("dcpsId", request).trim();
			//String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			//lObjMstEmp = lObjNewRegDdoDAO.getEmpVOForDCPSId(lStrDcpsId,lStrDdoCode);
			lObjMstEmp = lObjOfflineContriDAO.getEmpVOForDCPSId(lStrDcpsId);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
		}

		/*
		 * Generates XML response for all schemes found whose scheme code starts
		 * with given scheme code
		 */
	    ////$t 17-2-2021
		String lSBStatus=null;
		if(lObjMstEmp!=null)
		lSBStatus = getResponseXMLDocToDisplayEmpDtls(lObjMstEmp).toString();
		else
		lSBStatus=getResponseXMLDocToDisplayEmpDtlsSingleEntry().toString();
	     ////$t 
		
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	//////$t 17-2-2021 for single entry
	private StringBuilder getResponseXMLDocToDisplayEmpDtlsSingleEntry() {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<name>");
		lStrBldXML.append("NA");
		lStrBldXML.append("</name>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	
	
	private StringBuilder getResponseXMLDocToDisplayEmpDtls(MstEmp lObjMstEmp) {

		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
				TrnDcpsContribution.class, serv.getSessionFactory());

		//if(lObjMstEmp != null)
		Float DARate = lObjOfflineContriDAO.getDARateForPayCommission(lObjMstEmp.getPayCommission());

		StringBuilder lStrBldXML = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<name>");
		lStrBldXML.append(lObjMstEmp.getName());
		lStrBldXML.append("</name>");

		lStrBldXML.append("<payCommission>");
		lStrBldXML.append(lObjMstEmp.getPayCommission());
		lStrBldXML.append("</payCommission>");

		lStrBldXML.append("<basic>");
		lStrBldXML.append(lObjMstEmp.getBasicPay());
		lStrBldXML.append("</basic>");

		lStrBldXML.append("<doj>");
		lStrBldXML.append(sdf.format(lObjMstEmp.getDoj()));
		lStrBldXML.append("</doj>");

		lStrBldXML.append("<dcpsEmpId>");
		lStrBldXML.append(lObjMstEmp.getDcpsEmpId());
		lStrBldXML.append("</dcpsEmpId>");

		lStrBldXML.append("<daRate>");
		lStrBldXML.append(DARate);
		lStrBldXML.append("</daRate>");

		lStrBldXML.append("<RegStatus>");
		lStrBldXML.append(lObjMstEmp.getRegStatus());
		lStrBldXML.append("</RegStatus>");
		
		lStrBldXML.append("<SevenBasicPay>");
		lStrBldXML.append(lObjMstEmp.getSevenBasicPay());
		lStrBldXML.append("</SevenBasicPay>");
		
		lStrBldXML.append("<DDO>");
		lStrBldXML.append(lObjMstEmp.getDdoCode());
		lStrBldXML.append("</DDO>");
		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject getDARateForGivenPrd(Map<String, Object> inputMap) {
		//System.out.println("IMPL getDARateForGivenPrd(Date contriStartDate,");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Double lDoubleDARate = 0d;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			StringUtility.getParameter("dcpsEmpId", request);
			String lStrContriStartDate = StringUtility.getParameter(
					"contriStartDate", request);
			String lStrContriEndDate = StringUtility.getParameter(
					"contriEndDate", request);
			String lStrPayCommission = StringUtility.getParameter(
					"payCommission", request);

			Date lDateContriStartDate = null;
			Date lDateContriEndDate = null;

			if (!"".equals(lStrContriStartDate)) {
				lDateContriStartDate = sdf.parse(lStrContriStartDate.trim());
			}
			if (!"".equals(lStrContriEndDate)) {
				lDateContriEndDate = sdf.parse(lStrContriEndDate.trim());
			}

			lDoubleDARate = lObjOfflineContriDAO
			.getDARateForGivenPrd(lDateContriStartDate,
					lDateContriEndDate, lStrPayCommission);

			String lSBStatus = getResponseXMLDocForDARate(lDoubleDARate)
			.toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	private StringBuilder getResponseXMLDocForDARate(Double lDoubleDARate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(lDoubleDARate.toString().trim());
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private Map UpdateLoginDtlsAfterFirstLvlFwd(Map inputMap)
	{
		Map baseLoginMap = (Map) inputMap.get("baseLoginMap");
		long locId = Long.valueOf(baseLoginMap.get("locationId").toString());
		long ddoCode = Long.valueOf(inputMap.get("ddoCode").toString());

		setSessionInfoSchdlr(inputMap);

		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

		Long postIdDDO = lObjDcpsCommonDAO.getDDOPostIdForDDO(String.valueOf(ddoCode).trim());
		Long userIdDDO = lObjDcpsCommonDAO.getUserIdForPostId(postIdDDO);

		OrgPostMst orgPostMst = new OrgPostMst();
		orgPostMst.setPostId(postIdDDO);
		OrgUserMst orgUserMst = new OrgUserMst();
		orgUserMst.setUserId(userIdDDO);
		CmnLocationMst cmnLocationMst = new CmnLocationMst();
		cmnLocationMst.setLocationCode(String.valueOf(locId));
		cmnLocationMst.setLocId(locId);
		LoginDetails baseLoginVOForDDO = new LoginDetails();
		baseLoginVOForDDO.setDbId(99L);
		baseLoginVOForDDO.setLoggedInPost(orgPostMst);
		baseLoginVOForDDO.setLangId(1L);
		baseLoginVOForDDO.setUser(orgUserMst);
		baseLoginVOForDDO.setLocation(cmnLocationMst);

		Map BaseLoginMapDDO=(Map)inputMap.get("baseLoginMap");
		BaseLoginMapDDO.put("locationId",locId);
		BaseLoginMapDDO.put("locationCode",locId);
		BaseLoginMapDDO.put("userId",userIdDDO);
		BaseLoginMapDDO.put("postId",postIdDDO);
		BaseLoginMapDDO.put("CREATED_BY_USER",userIdDDO);
		BaseLoginMapDDO.put("CREATED_BY_POST",postIdDDO);


		inputMap.remove("CREATED_BY_USER");
		inputMap.remove("CREATED_BY_POST");
		inputMap.remove("baseLoginMap");
		inputMap.remove("baseLoginVO");
		inputMap.remove("User");
		inputMap.remove("Use");


		inputMap.put("CREATED_BY_USER", userIdDDO);
		inputMap.put("CREATED_BY_POST", postIdDDO);
		inputMap.put("baseLoginMap", BaseLoginMapDDO);
		inputMap.put("baseLoginVO", baseLoginVOForDDO);

		inputMap.put("User", "DDO");
		inputMap.put("Use", "ViewForwarded");
		return inputMap;
	}

	// Below Service to insert contributions

	/*
	private ResultObject insertRegularContributionsService(Map<String, Object> inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false; 
		String lStrTreasuryCode = null;
		String lStrMonthId = null;
		Long lLongMonthId = 0l;
		String lStrYearId = null;
		Long lLongYearId = 0l;
		String lStrBillGroupId = null;
		String lStrVoucherDate = null;
		String lStrVoucherNo = null;

		setSessionInfo(inputMap);

		lStrMonthId = inputMap.get("cmbMonth").toString().trim();
		lStrYearId = inputMap.get("cmbYear").toString().trim();
		lStrVoucherNo = inputMap.get("txtVoucherNo").toString().trim();
		lStrVoucherDate = inputMap.get("txtVoucherDt").toString().trim();

		lStrBillGroupId = inputMap.get("cmbBillGroup").toString().trim();

		lBlFlag = true;
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
				lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		resObj.setResultCode(1);
		return resObj;
	}

	 */

	// Below method to insert contributions

	private void insertRegularContributions(Map<String, Object> inputMap) throws Exception
	{
		Boolean lBlFlag = false; 
		String lStrTreasuryCode = null;
		String lStrMonthId = null;
		Long lLongMonthId = 0l;
		Integer lIntMonthIdCal = 0;
		String lStrYearId = null;
		String lStrYearIdFromPayroll = null;
		Long lLongYearIdFromPayroll = 0l;
		Long lLongYearId = 0l;
		Integer lIntYearIdCal = 0;
		String lStrBillGroupId = null;
		Long lLongBillGroupId = null;
		String lStrVoucherDate = null;
		String lStrVoucherNo = null;
		Long lLongVoucherNo = null;
		String lStrDDOCode = null;
		String lStrStartDate = null;
		String lStrEndDate = null;
		String lStrEndDateDay = null;
		Date lDtStartDate = null;
		Date lDtEndDate = null;
		String lStrPayCommission = null;

		List lListRegularContributionsFromPayroll = null;
		Long lLngContriVoucherIdToBePassed = null;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Long lLongTreasuryCode = null;
		String lStrSchemeCode = null;

		List lListDelayedTypeEmpListFromPayroll = null;
		List lListPayArrearTypeEmpListFromPayroll = null;
		List lListDAArrearTypeEmpListFromPayroll = null;

		try {
			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, serv.getSessionFactory());

			lStrMonthId = inputMap.get("cmbMonth").toString().trim();
			lStrYearId = inputMap.get("cmbYear").toString().trim();
			lStrYearIdFromPayroll = lStrYearId;
			
			   if (inputMap.get("txtVoucherNo") != null) {
			        lStrVoucherNo = inputMap.get("txtVoucherNo").toString().trim();
			        lLongVoucherNo = Long.valueOf(lStrVoucherNo);
			      }
			      if (inputMap.get("txtVoucherDt") != null) {
			        lStrVoucherDate = inputMap.get("txtVoucherDt").toString().trim();
			      }
			      
			

			lStrBillGroupId = inputMap.get("cmbBillGroup").toString().trim();

			lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId).trim();
			lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDDOCode.trim()).trim();
			lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);

			lStrStartDate = "01/" + lStrMonthId + "/" + lStrYearId;

			lLongMonthId = Long.valueOf(lStrMonthId);

			lIntMonthIdCal = Integer.valueOf(lLongMonthId.toString()) - 1;
			lIntYearIdCal = Integer.valueOf(lStrYearId); 

			lLongYearIdFromPayroll = Long.valueOf(lStrYearIdFromPayroll);

			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(lIntYearIdCal, lIntMonthIdCal , 1);

			int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			lStrEndDateDay = Integer.valueOf(days).toString().trim();

			lStrEndDate = lStrEndDateDay + "/" + lStrMonthId + "/" + lStrYearId;

			lDtStartDate = simpleDateFormat.parse(lStrStartDate.trim());
			lDtEndDate = simpleDateFormat.parse(lStrEndDate.trim());

			lStrYearId = lObjDcpsCommonDAO.getFinYearIdForDate(lDtStartDate);
			lLongYearId = Long.valueOf(lStrYearId);
			lLongBillGroupId = Long.valueOf(lStrBillGroupId);

			lListRegularContributionsFromPayroll = lObjOfflineContriDAO.getRegularContributionsFromPayroll(lLongYearIdFromPayroll,lLongMonthId,lLongBillGroupId);

			lLngContriVoucherIdToBePassed = Long.valueOf(inputMap.get("lLngContriVoucherIdToBePassed").toString());

			lStrSchemeCode = lObjOfflineContriDAO.getSchemeCodeForBillGroup(lLongBillGroupId);

			if(lLongMonthId != 0l && lLongYearId != 0l && lLongBillGroupId != null && lStrDDOCode != null)
			{
				lObjOfflineContriDAO.deleteRegularContributionsIfExist(lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
			}
			lObjOfflineContriDAO.insertRegularContributions(lListRegularContributionsFromPayroll,lLongMonthId,lLongYearId,lLongBillGroupId,lStrDDOCode,lLongTreasuryCode,lStrSchemeCode,lDtStartDate,lDtEndDate,gDtCurDate,gLngPostId,Long.valueOf(gStrLocationCode),lLngContriVoucherIdToBePassed,inputMap);

			lObjOfflineContriDAO.updateNonRegularTypeContriStatusInTrn(lLongYearId,lLongMonthId,lLongBillGroupId);

			lListDelayedTypeEmpListFromPayroll = lObjOfflineContriDAO.getEmpListForDelayedTypesInMonthAndBG(lLongYearIdFromPayroll, lLongMonthId, lLongBillGroupId);

			if(lListDelayedTypeEmpListFromPayroll != null)
			{
				if(lListDelayedTypeEmpListFromPayroll.size() != 0)
				{
					lObjOfflineContriDAO.updateDelayedContriStatusInTrn(lListDelayedTypeEmpListFromPayroll, lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
				}
			}

			lListPayArrearTypeEmpListFromPayroll = lObjOfflineContriDAO.getEmpListForPayArrearTypesInMonthAndBG(lLongYearIdFromPayroll, lLongMonthId, lLongBillGroupId);

			if(lListPayArrearTypeEmpListFromPayroll != null)
			{
				if(lListPayArrearTypeEmpListFromPayroll.size() != 0)
				{
					lObjOfflineContriDAO.updatePayArrearContriStatusInTrn(lListPayArrearTypeEmpListFromPayroll, lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
				}
			}

			lListDAArrearTypeEmpListFromPayroll = lObjOfflineContriDAO.getEmpListForDAArrearTypesInMonthAndBG(lLongYearIdFromPayroll, lLongMonthId, lLongBillGroupId);

			if(lListDAArrearTypeEmpListFromPayroll != null)
			{
				if(lListDAArrearTypeEmpListFromPayroll.size() != 0)
				{
					lObjOfflineContriDAO.updateDAArrearContriStatusInTrn(lListDAArrearTypeEmpListFromPayroll, lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
				}
			}

			//Flushing the session so that the above transactions commit before updating

			//Session saveTrnAndMstContriSession = serv.getSessionFactory().getCurrentSession();
			//saveTrnAndMstContriSession.flush();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			 Date voucherDate = null;
		      if (lStrVoucherDate != null)
		        voucherDate = sdf.parse(lStrVoucherDate);
		      lObjOfflineContriDAO.updateVoucherDetailsInTrnDcpsContri(lLongBillGroupId, lLongMonthId, lLongYearId, lStrDDOCode, lLongVoucherNo, voucherDate);

		      
			// Code to update voucher_amount,voucher_status,status in mst_dcps_contri_voucher_dtls

			Double lDoubleVoucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
			inputMap.put("voucherAmount", lDoubleVoucherAmount);
			//lObjOfflineContriDAO.updateVoucherDetailsInMstContriOnApproval(lLngContriVoucherIdToBePassed,lDoubleVoucherAmount);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			gLogger.error(" Error in insertRegularContributions " + e, e);
			throw e;
		}

	}

	public ResultObject saveVoucherDtlsForContri(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		resObj.setResultCode(-1);
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		MstDcpsContriVoucherDtls lObjPreviousMstDcpsContriVoucherDtls = null;
		Boolean lBlFlag = null;
		Long lLngDcpsContriVoucherId = null;
		Long lLongVoucherNo = null;
		Date lDateVoucherDate = null;
		Long lLongTreasuryCode = null;
		Double voucherAmount = null;
		Long lLngContriVoucherIdToBePassed = null;

		setSessionInfo(inputMap);

		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
				MstDcpsContriVoucherDtls.class, serv.getSessionFactory());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"dd/MM/yyyy");

		String lStrUser = StringUtility.getParameter("User", request)
		.trim();
		inputMap.put("RLUser", lStrUser);

		String lStrUse = StringUtility.getParameter("Use", request).trim();
		inputMap.put("RLUse", lStrUse);

		String lStrTypeOfPaymentMaster = StringUtility.getParameter(
				"cmbTypeOfPaymentMaster", request).trim();
		inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster);

		String lStrDelayedMonth = "";
		String lStrDelayedYear = "";

		if (lStrTypeOfPaymentMaster.equals("700047")) {
			lStrDelayedMonth = StringUtility.getParameter(
					"cmbDelayedMonth", request).trim();
			lStrDelayedYear = StringUtility.getParameter("cmbDelayedYear",
					request).trim();
		}

		inputMap.put("delayedMonth", lStrDelayedMonth);
		inputMap.put("delayedYear", lStrDelayedYear);

		String lStrApprovedFlag = "";

		if (!"".equalsIgnoreCase(StringUtility.getParameter("approvedFlag",
				request).trim())) {
			lStrApprovedFlag = StringUtility.getParameter("approvedFlag",
					request).trim();
		}

		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
				.getSessionFactory());

		String lStrFreezeFlag = null;
		if (inputMap.containsKey("FreezeFlag")) {
			lStrFreezeFlag = inputMap.get("FreezeFlag").toString().trim();
		}
		Integer lIntFreezeFlag = 0;
		String lStrDDOCode = null;
		String lStrTreasuryCode = null;
		String lStrMonthId = null;
		Long lLongMonthId = 0l;
		String lStrYearId = null;
		Long lLongYearId = 0l;
		String lStrBillGroupId = null;
		String lStrVoucherDate = null;
		String lStrVoucherNo = null;
		if (lStrFreezeFlag != null) {
			lIntFreezeFlag = Integer.parseInt(lStrFreezeFlag);

		}
		if (lIntFreezeFlag == 1) {
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId).trim();
			lStrTreasuryCode = lObjDcpsCommonDAO
			.getTreasuryCodeForDDO(lStrDDOCode.trim()).trim();
			lStrMonthId = inputMap.get("cmbMonth").toString().trim();
			lStrYearId = inputMap.get("cmbYear").toString().trim();

			String lStrStartDate = "01/" + lStrMonthId + "/" + lStrYearId;

			Date lDtStartDate = simpleDateFormat.parse(lStrStartDate.trim());

			lStrYearId = lObjDcpsCommonDAO
			.getFinYearIdForDate(lDtStartDate);

			 if (inputMap.get("txtVoucherNo") != null)
			        lStrVoucherNo = inputMap.get("txtVoucherNo").toString().trim();

			      if (inputMap.get("txtVoucherDt") != null) {
			        lStrVoucherDate = inputMap.get("txtVoucherDt").toString().trim();
			      }


			lStrBillGroupId = inputMap.get("cmbBillGroup").toString().trim();

			//insertRegularContributions(inputMap);
			//resObj = serv.executeService("insertRegularContributions", inputMap);

		} else {

			lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
			lStrTreasuryCode = StringUtility.getParameter(
					"cmbTreasuryCode", request).trim();
			lStrMonthId = StringUtility.getParameter("cmbMonth", request).trim();

			lStrYearId = StringUtility.getParameter("cmbYear", request).trim();

			lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",
					request).trim();
			lStrVoucherNo = StringUtility.getParameter("txtVoucherNo",
					request).trim();
			lStrVoucherDate = StringUtility.getParameter("txtVoucherDate",
					request).trim();

		}

		lLongMonthId = Long.valueOf(lStrMonthId);
		lLongYearId = Long.valueOf(lStrYearId);

		if (lStrTreasuryCode != null
				&& !"".equalsIgnoreCase(lStrTreasuryCode)) {
			lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);
		}

		Long lLongBillGroupId = Long.valueOf(lStrBillGroupId);

		// Scheme code got for bill-group

		String lStrSchemeCode = null;

		if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
		{
			lStrSchemeCode = StringUtility.getParameter("schemeCode", request).trim();
		}
		else
		{
			lStrSchemeCode = lObjOfflineContriDAO.getSchemeCodeForBillGroup(lLongBillGroupId).trim();
		}

		if (lStrVoucherNo != null && !"".equalsIgnoreCase(lStrVoucherNo)) {
			lLongVoucherNo = Long.valueOf(lStrVoucherNo.trim());
		}

		if (lStrVoucherDate != null && !"".equals(lStrVoucherDate.trim())) {
			lDateVoucherDate = simpleDateFormat.parse(lStrVoucherDate.trim());
		}

		if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
		{
			lObjPreviousMstDcpsContriVoucherDtls = lObjOfflineContriDAO.getContriVoucherVOForInputDtlsMissingCredit(lLongYearId,lLongMonthId,
					lStrDDOCode,lLongBillGroupId,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
		}
		else
		{
			lObjPreviousMstDcpsContriVoucherDtls = lObjOfflineContriDAO.getContriVoucherVOForInputDtls(lLongYearId, lLongMonthId,
					lStrDDOCode, lLongBillGroupId);
		}

		if (lObjPreviousMstDcpsContriVoucherDtls == null) {
			// Create
			lLngDcpsContriVoucherId = IFMSCommonServiceImpl.getNextSeqNum(
					"mst_dcps_contri_voucher_dtls", inputMap);
			lObjMstDcpsContriVoucherDtls = new MstDcpsContriVoucherDtls();
			lObjMstDcpsContriVoucherDtls
			.setDcpsContriVoucherDtlsId(lLngDcpsContriVoucherId);
			lObjMstDcpsContriVoucherDtls.setBillGroupId(lLongBillGroupId);
			lObjMstDcpsContriVoucherDtls.setCreatedDate(gDtCurDate);
			lObjMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
			lObjMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);
			lObjMstDcpsContriVoucherDtls.setPostId(gLngPostId);
			lObjMstDcpsContriVoucherDtls.setUserId(gLngUserId);

			// scheme code updated

			lObjMstDcpsContriVoucherDtls.setSchemeCode(lStrSchemeCode);

			if (lStrVoucherDate != null
					&& !"".equals(lStrVoucherDate.trim())) {
				lObjMstDcpsContriVoucherDtls
				.setVoucherDate(lDateVoucherDate);
			} else {
				lObjMstDcpsContriVoucherDtls.setVoucherDate(null);
			}

			if (lStrVoucherNo != null
					&& !"".equalsIgnoreCase(lStrVoucherNo)) {
				lObjMstDcpsContriVoucherDtls.setVoucherNo(lLongVoucherNo);
			} else {
				lObjMstDcpsContriVoucherDtls.setVoucherNo(null);
			}

			lObjMstDcpsContriVoucherDtls.setTreasuryCode(lLongTreasuryCode);
			lObjMstDcpsContriVoucherDtls.setYearId(lLongYearId);

			if (lStrUser.equals("ATO")) {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(null);
			}
			if (lStrUser.equals("TO")) {

				if(lStrUse.equals("MissingCreditEntry"))
				{
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					//voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLongMonthId,lLongYearId,lLongBillGroupId,lStrDDOCode,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
					lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					lObjMstDcpsContriVoucherDtls.setStatus('F');
					lObjMstDcpsContriVoucherDtls.setIsMissingCredit('Y');
					lObjMstDcpsContriVoucherDtls.setMissingCreditApprovalDate(gDtCurDate);
				}
				else
				{
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					lObjMstDcpsContriVoucherDtls.setStatus('A');
				}

			}
			if (lStrUser.equals("DDO")) {
				if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {

					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					lObjMstDcpsContriVoucherDtls.setStatus('A');
					voucherAmount = lObjOfflineContriDAO
					.getTotalVoucherAmountForGivenVoucher(
							lLongMonthId, lLongYearId,
							lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls
					.setVoucherAmount(voucherAmount);
				} else {
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(3l); // Since
					// Forwarded
					// by
					// DDO
					// in
					// Online
				}
			}

			if (lIntFreezeFlag == 1) {

				// Below code uncommented as voucher amount will be inserted only when approved by treasury 
				//voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
				//lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
				// Changed after last check in
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(3l);

			}
			lObjMstDcpsContriVoucherDtls.setManuallyMatched(0l);
			lObjMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
			lObjMstDcpsContriVoucherDtls.setReversionFlag(0l);

			if (lIntFreezeFlag != 1)
			{
				lObjOfflineContriDAO.create(lObjMstDcpsContriVoucherDtls);
			}

			lLngContriVoucherIdToBePassed = lLngDcpsContriVoucherId;

			inputMap.put("voucherVOToCheckThruSchdlr", lObjMstDcpsContriVoucherDtls);

		} else {
			// Updates
			lObjPreviousMstDcpsContriVoucherDtls
			.setBillGroupId(lLongBillGroupId);
			lObjPreviousMstDcpsContriVoucherDtls.setUpdatedDate(gDtCurDate);
			lObjPreviousMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
			lObjPreviousMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);

			lObjPreviousMstDcpsContriVoucherDtls.setSchemeCode(lStrSchemeCode);

			if (lStrVoucherDate != null
					&& !"".equals(lStrVoucherDate.trim())) {
				lObjPreviousMstDcpsContriVoucherDtls
				.setVoucherDate(lDateVoucherDate);
			} else {
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherDate(null);
			}

			if (lStrVoucherNo != null
					&& !"".equalsIgnoreCase(lStrVoucherNo)) {
				lObjPreviousMstDcpsContriVoucherDtls
				.setVoucherNo(lLongVoucherNo);
			} else {
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherNo(null);
			}

			lObjPreviousMstDcpsContriVoucherDtls
			.setTreasuryCode(lLongTreasuryCode);
			lObjPreviousMstDcpsContriVoucherDtls.setYearId(lLongYearId);
			lObjPreviousMstDcpsContriVoucherDtls
			.setUpdatedPostId(gLngPostId);
			lObjPreviousMstDcpsContriVoucherDtls
			.setUpdatedUserId(gLngUserId);

			if (lStrUser.equals("ATO")) {
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(null);
			}
			if (lStrUser.equals("TO")) {

				if(lStrUse.equals("MissingCreditEntry"))
				{
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					//voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLongMonthId,lLongYearId,lLongBillGroupId,lStrDDOCode,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					lObjPreviousMstDcpsContriVoucherDtls.setStatus('F');
					lObjPreviousMstDcpsContriVoucherDtls.setIsMissingCredit('Y');
					lObjPreviousMstDcpsContriVoucherDtls.setMissingCreditApprovalDate(gDtCurDate);
				}
				else
				{
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					lObjPreviousMstDcpsContriVoucherDtls.setStatus('A');
					voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
				}

			}
			if (lStrUser.equals("DDO")) {
				if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {
					lObjPreviousMstDcpsContriVoucherDtls
					.setVoucherStatus(1l);
					voucherAmount = lObjOfflineContriDAO
					.getTotalVoucherAmountForGivenVoucher(
							lLongMonthId, lLongYearId,
							lLongBillGroupId, lStrDDOCode);
					lObjPreviousMstDcpsContriVoucherDtls
					.setVoucherAmount(voucherAmount);
				} else {
					lObjPreviousMstDcpsContriVoucherDtls
					.setVoucherStatus(3l); // Since Forwarded by DDO
					// in Online
				}
			}

			if (lIntFreezeFlag == 1) {

				lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(3l);

			}

			lObjPreviousMstDcpsContriVoucherDtls.setManuallyMatched(0l);
			lObjPreviousMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
			lObjPreviousMstDcpsContriVoucherDtls.setReversionFlag(0l);

			if (lIntFreezeFlag != 1)
			{
				lObjOfflineContriDAO.update(lObjPreviousMstDcpsContriVoucherDtls);
			}

			lLngContriVoucherIdToBePassed = lObjPreviousMstDcpsContriVoucherDtls
			.getDcpsContriVoucherDtlsId();

			inputMap.put("voucherVOToCheckThruSchdlr", lObjPreviousMstDcpsContriVoucherDtls);
		}

		inputMap.put("lLngContriVoucherIdToBePassed",lLngContriVoucherIdToBePassed);

		// Code added to insert data in TrnDcpsContribution

		if (lIntFreezeFlag == 1) {
			insertRegularContributions(inputMap);
			Double lDoubleVoucherAmount = 0d;

			if(inputMap.get("voucherAmount") != null)
			{
				lDoubleVoucherAmount = Double.parseDouble(inputMap.get("voucherAmount").toString().trim());
			}

			if (lObjPreviousMstDcpsContriVoucherDtls == null) {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(1L);
				lObjMstDcpsContriVoucherDtls.setStatus('A');
				lObjMstDcpsContriVoucherDtls.setVoucherAmount(lDoubleVoucherAmount);
				lObjOfflineContriDAO.create(lObjMstDcpsContriVoucherDtls);
			}
			else
			{
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1L);
				lObjPreviousMstDcpsContriVoucherDtls.setStatus('A');
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherAmount(lDoubleVoucherAmount);
				lObjOfflineContriDAO.update(lObjPreviousMstDcpsContriVoucherDtls);
			}
			//lObjOfflineContriDAO.updateVoucherDetailsInMstContriOnApproval(lLngContriVoucherIdToBePassed,lDoubleVoucherAmount);
		}

		lBlFlag = true;

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		resObj.setResultCode(1);
		return resObj;
	}

	public ResultObject displaySchemeNameForCodeInMissingCredit(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrSchemeCode = null;
		List<MstScheme> lListSchemes = null;
		Integer lNoOfSchemes = 0;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(MstDcpsContriVoucherDtls.class, serv.getSessionFactory());

			/* Gets SchemeCode from request */
			lStrSchemeCode = StringUtility.getParameter("txtSchemeCode", request).trim();

			/*
			 * Gets the all the schemeNames whose scheme code starts with given
			 * scheme code and finds total schemes
			 */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			String lStrDdoCode = null;
			// Below list of schemes does not depend on ddo code
			lListSchemes = lObjOfflineContriDAO.getSchemeNamesFromCodeInMissingCredit(lStrSchemeCode, lStrDdoCode);
			//lNoOfSchemes = lListSchemes.size();

			String lStrTempResult = null;
			if (lListSchemes != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lListSchemes,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
			return resObj;
		}

		/*
		 * Generates XML response for all schemes found whose scheme code starts
		 * with given scheme code
		 */

	}

	private StringBuilder getResponseXMLDocToDisplaySchemeName(Integer lNoOfSchemes, List lListSchemes) {

		StringBuilder lStrBldXML = new StringBuilder();
		Object[] lObjSchemes = null;
		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<lNoOfSchemes>");
		lStrBldXML.append(lNoOfSchemes);
		lStrBldXML.append("</lNoOfSchemes>");

		for (int lInt = 0; lInt < lListSchemes.size(); lInt++) {
			lObjSchemes = (Object[]) lListSchemes.get(lInt);
			lStrBldXML.append("<SchemeName" + lInt + ">");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(lObjSchemes[1].toString().trim());
			lStrBldXML.append("]]>");

			lStrBldXML.append("</SchemeName" + lInt + ">");
			lStrBldXML.append("<SchemeCode" + lInt + ">");
			lStrBldXML.append(lObjSchemes[0].toString().trim());
			lStrBldXML.append("</SchemeCode" + lInt + ">");
		}

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
//Added by ashish for displaying voucher_no,date and contribution
	private StringBuilder getResponseXMLDocForOffline(Boolean flag,String voucherno,String voucherdate,Double amount,String status,Double amount1,String startdate,String enddate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
	

		lStrBldXML.append("<VNO>");
		lStrBldXML.append(voucherno);
		lStrBldXML.append("</VNO>");

		lStrBldXML.append("<VDATE>");
		lStrBldXML.append(voucherdate);
		lStrBldXML.append("</VDATE>");

		lStrBldXML.append("<AMT>");
		lStrBldXML.append(amount);
		lStrBldXML.append("</AMT>");
		
		lStrBldXML.append("<STATUS>");
		lStrBldXML.append(status);
		lStrBldXML.append("</STATUS>");
		//$t 2019
		lStrBldXML.append("<AMTE>");
		lStrBldXML.append(amount1);
		lStrBldXML.append("</AMTE>");
		////$t 17Jan2022
		lStrBldXML.append("<SDATE>");
		lStrBldXML.append(startdate);
		lStrBldXML.append("</SDATE>");
		
		lStrBldXML.append("<EDATE>");
		lStrBldXML.append(enddate);
		lStrBldXML.append("</EDATE>");
		
		
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
	//Ended by ashish for displaying voucher_no,date and contribution
	
	
	 public ResultObject checkDORMonths(Map objectArgs)
	  {
	    ResultObject resultObject = new ResultObject(0);
	    try {
	      this.gLogger.info("Coming into the Service method getPaybillParaGrades");
	      ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	      ServiceLocator serviceLocator = (ServiceLocator)objectArgs.get("serviceLocator");
	      HttpServletRequest request = (HttpServletRequest)objectArgs.get("requestObj");

	      Map loginDetailsMap = (Map)objectArgs.get("baseLoginMap");
	      long langId = StringUtility.convertToLong(loginDetailsMap.get("langId").toString()).longValue();
	      String bill_no = StringUtility.getParameter("bill_no", request).trim();
	      String month = StringUtility.getParameter("month", request).trim();
	      String year = StringUtility.getParameter("year", request).trim();
	      String dcpsId = StringUtility.getParameter("dcpsId", request).trim();

	      OfflineContriDAOImpl PayBillDAO = new OfflineContriDAOImpl(null, serv.getSessionFactory());
	      List flag = PayBillDAO.getDORDetails(bill_no, month, year, dcpsId);
	      this.gLogger.info("flag.size****************" + flag.size());

	      String data = "";
	      this.gLogger.info("here1****************");
	      for (Iterator iter = flag.iterator(); iter.hasNext(); )
	      {
	        this.gLogger.info("here2****************");
	        data = data + iter.next().toString() + ',';
	        this.gLogger.info("data****************" + data);
	      }

	      StringBuffer sb = new StringBuffer();

	      sb.append("<XMLDOC>");
	      sb.append("<lBlFlag>").append(data).append("</lBlFlag>");
	      sb.append("</XMLDOC>");
	      String response = sb.toString();

	      String lStrCodeResult = new AjaxXmlBuilder().addItem("ajax_key", response).toString();
	      this.gLogger.info("response****************" + response);
	      objectArgs.put("ajaxKey", lStrCodeResult);

	      resultObject.setResultValue(objectArgs);
	      resultObject.setResultCode(0);
	      resultObject.setViewName("ajaxData");
	    }
	    catch (Exception e)
	    {
	      this.gLogger.info("Exception Ocuures...insertBankMasterDtls");
	      this.gLogger.error("Error is: " + e.getMessage());
	      objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
	      resultObject.setResultValue(objectArgs);
	      resultObject.setViewName("errorInsert");
	    }
	    return resultObject;
	  }
	 public ResultObject loadApproveMissingCreditSRKA(Map inputMap) {

			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			List empList = null;
			int sizeOfList=0;

			try {

				setSessionInfo(inputMap);

				OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
						MstEmp.class, serv.getSessionFactory());


			
				//String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

				//lObjMstEmp = lObjNewRegDdoDAO.getEmpVOForDCPSId(lStrDcpsId,lStrDdoCode);
				empList = lObjOfflineContriDAO.getMissingCreditForApprovalSRKA();
				if(empList!=null)
				{
					sizeOfList=empList.size();
				}

			} catch (Exception e) {
				e.printStackTrace();
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
				gLogger.error(" Error is " + e, e);
			}

			/*
			 * Generates XML response for all schemes found whose scheme code starts
			 * with given scheme code
			 */
		
			inputMap.put("empList", empList);
			inputMap.put("sizeOfList", sizeOfList);
			resObj.setResultValue(inputMap);

			//	if(inputMap.get("fromPayBillScheduler")==null) // For Scheuler only 
			resObj.setViewName("approveMissingCreditSRKA");
			return resObj;
		}
	 
	 public ResultObject approveMissingCreditSRKA(Map inputMap) {

			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			List empList = null;
			int sizeOfList=0;
			int lIntTotalEmps=0;
			String lStrDcpsId="";
			Long lLongPKValue=0l;
			boolean lBlFlag=false;
			try {

				setSessionInfo(inputMap);

				OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
						TrnDcpsContribution.class, serv.getSessionFactory());
                      

				TrnDcpsContribution objTrnDcpsContribution;
				if(StringUtility.getParameter("totalEmployeesSelected", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("totalEmployeesSelected", request).trim()))
				{
					lIntTotalEmps = Integer.parseInt(StringUtility.getParameter("totalEmployeesSelected", request).trim());
					lStrDcpsId = StringUtility.getParameter("dcpsEmpID", request).trim();
					String[] lStrArrDcpsId = new String[lIntTotalEmps];
					lStrArrDcpsId = lStrDcpsId.split("~");
					for (Integer lInt = 0; lInt < lStrArrDcpsId.length; lInt++) 
					{
						lLongPKValue = Long.valueOf(lStrArrDcpsId[lInt]);
						objTrnDcpsContribution=(TrnDcpsContribution) lObjOfflineContriDAO.read(lLongPKValue);
						if(objTrnDcpsContribution!=null)
						{
							objTrnDcpsContribution.setStatus('F');
							objTrnDcpsContribution.setIsApprovedBySrka('Y');
							Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
							objTrnDcpsContribution.setSrkaApprovalDate(gDtCurrDt);
							lObjOfflineContriDAO.update(objTrnDcpsContribution);
							lBlFlag = true;
						}
					}
				}
				
				//String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
				
				

				//lObjMstEmp = lObjNewRegDdoDAO.getEmpVOForDCPSId(lStrDcpsId,lStrDdoCode);
				

			} catch (Exception e) {
				e.printStackTrace();
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
				gLogger.error(" Error is " + e, e);
			}

			/*
			 * Generates XML response for all schemes found whose scheme code starts
			 * with given scheme code
			 */
		
		

			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
			.toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			resObj.setResultCode(1);
			return resObj;
		}
	 
	 public ResultObject rejectMissingCreditSRKA(Map inputMap) {

			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			List empList = null;
			int sizeOfList=0;
			int lIntTotalEmps=0;
			String lStrDcpsId="";
			Long lLongPKValue=0l;
			boolean lBlFlag=false;
			try {

				setSessionInfo(inputMap);

				OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
						TrnDcpsContribution.class, serv.getSessionFactory());


				TrnDcpsContribution objTrnDcpsContribution;
				if(StringUtility.getParameter("totalEmployeesSelected", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("totalEmployeesSelected", request).trim()))
				{
					lIntTotalEmps = Integer.parseInt(StringUtility.getParameter("totalEmployeesSelected", request).trim());
					lStrDcpsId = StringUtility.getParameter("dcpsEmpID", request).trim();
					String[] lStrArrDcpsId = new String[lIntTotalEmps];
					lStrArrDcpsId = lStrDcpsId.split("~");
					for (Integer lInt = 0; lInt < lStrArrDcpsId.length; lInt++) 
					{
						lLongPKValue = Long.valueOf(lStrArrDcpsId[lInt]);
						objTrnDcpsContribution=(TrnDcpsContribution) lObjOfflineContriDAO.read(lLongPKValue);
						if(objTrnDcpsContribution!=null)
						{
							objTrnDcpsContribution.setStatus('R');
							lObjOfflineContriDAO.update(objTrnDcpsContribution);
							lBlFlag = true;
						}
					}
				}
				
				//String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
				
				

				//lObjMstEmp = lObjNewRegDdoDAO.getEmpVOForDCPSId(lStrDcpsId,lStrDdoCode);
				

			} catch (Exception e) {
				e.printStackTrace();
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
				gLogger.error(" Error is " + e, e);
			}

			/*
			 * Generates XML response for all schemes found whose scheme code starts
			 * with given scheme code
			 */
		
		

			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
			.toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			resObj.setResultCode(1);
			return resObj;
		}
	
}

