/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 9, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.ChangeEmpDeptDAO;
import com.tcs.sgv.dcps.dao.ChangeEmpDeptDAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Jun 9, 2011
 */
public class MatchContriEntryServiceImpl extends ServiceImpl implements
MatchContriEntryService {

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

	public ResultObject loadMatchContriEntryForm(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesMatchEntries = null;
		Long yearId = null;
		Long totalSAmount=0L;
		Long totalTAmount=0L;
		Long AcMain=null;
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			MatchContriEntryDAOTO lObjMatchEntryDAOTO = new MatchContriEntryDAOTOImpl(
					null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();
			List lActMain=lObjMatchEntryDAO.getAcMainDetails();

			if (!StringUtility.getParameter("yearId", request).equalsIgnoreCase("")&& StringUtility.getParameter("yearId", request) != null
					&& !StringUtility.getParameter("acmaintBy", request).equalsIgnoreCase("") && StringUtility.getParameter("acmaintBy", request) != null)	
			 {

				yearId = Long.valueOf(StringUtility.getParameter("yearId",request));
				lStrTempFromDate = StringUtility.getParameter("fromDate",request);
				lStrTempToDate = StringUtility.getParameter("toDate", request);
				AcMain=Long.valueOf(StringUtility.getParameter("acmaintBy", request));
				
				
				Long lRoleOfLngUsr = lObjMatchEntryDAO.getRoleOfUserFrmPostId(gLngPostId);
				String userType = null;
				List lLsttYears=null;
				if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700004l){
					userType = "srka";
					lLsttYears = lObjDcpsCommonDAO.getFinyear();
				}
				if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700003l){
					userType = "to";
					lLsttYears = lObjMatchEntryDAO.getFinyears();
				}



				List lLstTreasury = lObjDcpsCommonDAO.getAllTreasuries();

				List lLstSubTreasury = null;

				if(userType != null && userType.equals("srka"))
					lLstSubTreasury = lObjMatchEntryDAO.getAllSubTreasuries(null);

				if(userType != null && userType.equals("to"))
					lLstSubTreasury = lObjMatchEntryDAO.getAllSubTreasuries(gStrLocationCode);
				gLogger.info("Treasury List size is"+lLstSubTreasury);
				
				lStrFromDate = sdf2.format(sdf1.parse(lStrTempFromDate));
				lStrToDate = sdf2.format(sdf1.parse(lStrTempToDate));

				lDateFromDate = sdf1.parse(lStrTempFromDate);
				lDateToDate = sdf1.parse(lStrTempToDate);
				
				if(userType != null && userType.equals("srka"))
				lListTreasuriesMatchEntries = lObjMatchEntryDAO
				.getAllTreasuriesForMatchedEntries(lStrFromDate,
						lStrToDate,AcMain);

				if(userType != null && userType.equals("to")){

				Long	lSubTreasury = lObjMatchEntryDAOTO.getSubTreasuryList(gStrLocationCode);
					String treasurycd=lSubTreasury+"%";
					
					gLogger.info("treasurycd is ::::::"+treasurycd);
					lListTreasuriesMatchEntries = lObjMatchEntryDAOTO
					.getAllTreasuriesForMatchedEntries(lStrFromDate,
							lStrToDate,treasurycd,AcMain);
				}
				//   gLogger.info("totalSAmount is ***"+totalSAmount);
				inputMap.put("totalRecords", lListTreasuriesMatchEntries.size());
				inputMap.put("selectedYear", yearId);
				inputMap.put("fromDate", lDateFromDate);
				inputMap.put("toDate", lDateToDate);
				inputMap.put("selectedAcc", AcMain);


/*				Object[] tuple = null;


				Iterator it = lListTreasuriesMatchEntries.iterator();

				while (it.hasNext())
				{
					tuple = (Object[]) it.next();

					totalSAmount = totalSAmount+Long.valueOf(tuple[2].toString());
					totalTAmount = totalTAmount+Long.valueOf(tuple[3].toString());

				}
				gLogger.info("totalSAmount is ***"+totalSAmount);
				gLogger.info("totalTAmount is ***"+totalTAmount);*/
			}


			inputMap.put("YEARS", lLstYears);
			inputMap.put("AccMain", lActMain);
			inputMap.put("ListTreasuriesMatchEntries",
					lListTreasuriesMatchEntries);

			/*inputMap.put("totalSAmount", totalSAmount);
			inputMap.put("totalTAmount", totalTAmount);*/

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSMatchedEntries");

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

	public ResultObject loadUnMatchContriEntryForm(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesUnMatchEntries = null;
		Long yearId = null;
	    String treasuryCode=null;

		List lListTreasuriesUnMatchEntriesMstContri = null;
		List lListTreasuriesUnMatchEntriesTreasuryNet = null;

		List lListTreasuriesUnMatchEntriesMstContriFinal = null;
		List lListTreasuriesUnMatchEntriesTreasuryNetFinal = null;

		Long lLongTreasuryCode = null;
		Object[] tuple = null;
		Object[] tupleMstContri = null;
		Object[] tupleMstTreasuryNet = null;

		Object[] tupleFinal = null;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			String userType = null;
			
			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();

			if (!StringUtility.getParameter("yearId", request)
					.equalsIgnoreCase("")
					&& StringUtility.getParameter("yearId", request) != null) {

				lListTreasuriesUnMatchEntries =  new ArrayList();

				yearId = Long.valueOf(StringUtility.getParameter("yearId",
						request));
				lStrTempFromDate = StringUtility.getParameter("fromDate",
						request);
				lStrTempToDate = StringUtility.getParameter("toDate", request);
				
				Long lRoleOfLngUsr = lObjMatchEntryDAO.getRoleOfUserFrmPostId(gLngPostId);

				lStrFromDate = sdf2.format(sdf1.parse(lStrTempFromDate));
				lStrToDate = sdf2.format(sdf1.parse(lStrTempToDate));

				lDateFromDate = sdf1.parse(lStrTempFromDate);
				lDateToDate = sdf1.parse(lStrTempToDate);

				//lListTreasuriesUnMatchEntries = lObjMatchEntryDAO.getAllTreasuriesForUnMatchedEntries(lStrFromDate,lStrToDate);
				if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700004l){
					userType = "srka";
	
				}
				if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700003l){
					userType = "to";
					
				}
				if(userType != null && userType.equals("to")){
					treasuryCode=gStrLocationCode;
				}
				
				lListTreasuriesUnMatchEntriesMstContri = lObjMatchEntryDAO.getAllTreasuriesForUnMatchedEntriesMstcontri(treasuryCode,lStrFromDate,lStrToDate);

				// Map Creation for mst_dcps_contri_voucher_dtls table starts

				Map lMapMstContriDtls = new HashMap();

				if (lListTreasuriesUnMatchEntriesMstContri != null && !lListTreasuriesUnMatchEntriesMstContri.isEmpty())
				{
					Iterator it = lListTreasuriesUnMatchEntriesMstContri.iterator();

					while (it.hasNext())
					{
						tuple = (Object[]) it.next();
						lLongTreasuryCode = Long.valueOf(tuple[0].toString());
						lListTreasuriesUnMatchEntriesMstContriFinal = (List) lMapMstContriDtls.get(lLongTreasuryCode);
						if(lListTreasuriesUnMatchEntriesMstContriFinal != null)
						{
							lListTreasuriesUnMatchEntriesMstContriFinal.add(tuple);
						}
						else
						{
							lListTreasuriesUnMatchEntriesMstContriFinal = new ArrayList();
							lListTreasuriesUnMatchEntriesMstContriFinal.add(tuple);
						}
						lMapMstContriDtls.put(lLongTreasuryCode, lListTreasuriesUnMatchEntriesMstContriFinal);
					}
				}

				// Map creation for mst_dcps_contri_voucher_dtls table ends

				// Map Creation for mst_dcps_treasury_net_data table starts

				lListTreasuriesUnMatchEntriesTreasuryNet = lObjMatchEntryDAO.getAllTreasuriesForUnMatchedEntriesTreasuryNet(treasuryCode,lStrFromDate,lStrToDate);
				Map lMapTreasurNetDtls = new HashMap();

				if (lListTreasuriesUnMatchEntriesTreasuryNet != null && !lListTreasuriesUnMatchEntriesTreasuryNet.isEmpty())
				{
					Iterator it = lListTreasuriesUnMatchEntriesTreasuryNet.iterator();

					while (it.hasNext())
					{
						tuple = (Object[]) it.next();
						lLongTreasuryCode = Long.valueOf(tuple[0].toString());
						lListTreasuriesUnMatchEntriesTreasuryNetFinal = (List) lMapTreasurNetDtls.get(lLongTreasuryCode);
						if(lListTreasuriesUnMatchEntriesTreasuryNetFinal != null)
						{
							lListTreasuriesUnMatchEntriesTreasuryNetFinal.add(tuple);
						}
						else
						{
							lListTreasuriesUnMatchEntriesTreasuryNetFinal = new ArrayList();
							lListTreasuriesUnMatchEntriesTreasuryNetFinal.add(tuple);
						}
						lMapTreasurNetDtls.put(lLongTreasuryCode, lListTreasuriesUnMatchEntriesTreasuryNetFinal);
					}
				}

				// Map creation for mst_dcps_contri_voucher_dtls table ends

				// Final List prepared from above two maps

				if (lListTreasuriesUnMatchEntriesMstContri != null && !lListTreasuriesUnMatchEntriesMstContri.isEmpty())
				{
					Iterator it = lListTreasuriesUnMatchEntriesMstContri.iterator();

					while (it.hasNext())
					{
						tuple = (Object[]) it.next();
						lLongTreasuryCode = Long.valueOf(tuple[0].toString());
						lListTreasuriesUnMatchEntriesMstContriFinal = (List) lMapMstContriDtls.get(lLongTreasuryCode);
						lListTreasuriesUnMatchEntriesTreasuryNetFinal = (List) lMapTreasurNetDtls.get(lLongTreasuryCode);
						tupleFinal = new Object[4];
					
						if(lListTreasuriesUnMatchEntriesMstContriFinal != null)
						{
							tupleMstContri = (Object[]) lListTreasuriesUnMatchEntriesMstContriFinal.get(0);
						}
						if(lListTreasuriesUnMatchEntriesTreasuryNetFinal != null)
						{
							tupleMstTreasuryNet = (Object[]) lListTreasuriesUnMatchEntriesTreasuryNetFinal.get(0);
						}

						if(tupleMstContri != null)
						{
							tupleFinal[0] = tupleMstContri[0];
							tupleFinal[1] = tupleMstContri[1];
							tupleFinal[2] = tupleMstContri[2];
						}
						else
						{
							tupleFinal[0] = "";
							tupleFinal[1] = "";
							tupleFinal[2] = "";
						}

						if(tupleMstTreasuryNet != null)
						{
							tupleFinal[3] = tupleMstTreasuryNet[2];	
							tupleMstTreasuryNet=null;
						}
						else
						{
							tupleFinal[3] = "";
						}
						lListTreasuriesUnMatchEntries.add(tupleFinal);
					}
				}

				inputMap.put("totalRecords", lListTreasuriesUnMatchEntries.size());
				inputMap.put("selectedYear", yearId);
				inputMap.put("fromDate", lDateFromDate);
				inputMap.put("toDate", lDateToDate);
			

			}

			inputMap.put("YEARS", lLstYears);
			inputMap.put("ListTreasuriesUnMatchEntries",lListTreasuriesUnMatchEntries);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSUnMatchedEntries");

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


	public ResultObject loadManualMatch(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListUnMatchedVouchers = null;
		Long yearId = null;
		String lStrTreasuryCode = null;
		Long lLongTreasuryCode = null;
		Long rltContridtls=0L;

		List lListUnMatchedVouchersFromMstContri = null;
		List lListUnMatchedVouchersFromTreasuryNet = null;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			/* Get Years */

			Long lRoleOfLngUsr = lObjMatchEntryDAO.getRoleOfUserFrmPostId(gLngPostId);
			String userType = null;
			List lLstYears=null;
			if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700004l){
				userType = "srka";
				lLstYears = lObjDcpsCommonDAO.getFinyear();
			}
			if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700003l){
				userType = "to";
				lLstYears = lObjMatchEntryDAO.getFinyears();
			}



			List lLstTreasury = lObjDcpsCommonDAO.getAllTreasuries();

			List lLstSubTreasury = null;

			if(userType != null && userType.equals("srka"))
				lLstSubTreasury = lObjMatchEntryDAO.getAllSubTreasuries(null);

			if(userType != null && userType.equals("to"))
				lLstSubTreasury = lObjMatchEntryDAO.getAllSubTreasuries(gStrLocationCode);

			Boolean lBlFirstTimeLoadFlag = true;
			String rltControVouc=null;
			if (StringUtility.getParameter("requestForMatching", request).trim().equalsIgnoreCase("Yes")) {

				yearId = Long.valueOf(StringUtility.getParameter("yearId",request).trim());
				lStrTreasuryCode = StringUtility.getParameter("treasuryCode", request).trim() ;
				lStrTempFromDate = StringUtility.getParameter("fromDate",request).trim();
				lStrTempToDate = StringUtility.getParameter("toDate", request).trim();
				gLogger.info("lStrTempFromDate *****"+lStrTempFromDate);
				gLogger.info("lStrTempToDate *****"+lStrTempToDate);

				lStrFromDate = sdf2.format(sdf1.parse(lStrTempFromDate));
				lStrToDate = sdf2.format(sdf1.parse(lStrTempToDate));

				lDateFromDate = sdf1.parse(lStrTempFromDate);
				lDateToDate = sdf1.parse(lStrTempToDate);

				if(!"".equals(lStrTreasuryCode) && !"".equals(lStrFromDate) && !"".equals(lStrToDate))
				{
					lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);
					//lListUnMatchedVouchers = lObjMatchEntryDAO.getUnMatchedVouchersForMatching(lStrFromDate,lStrToDate,lLongTreasuryCode);
					//lListUnMatchedVouchers = lObjMatchEntryDAO.getUnMatchedVouchersAllForMatching(lStrFromDate,lStrToDate,lLongTreasuryCode);

					lListUnMatchedVouchersFromMstContri = lObjMatchEntryDAO.getUnMatchedVouchersForMatchingFromMstContriVoucherDtls(lStrFromDate,lStrToDate,lLongTreasuryCode);
					lListUnMatchedVouchersFromTreasuryNet = lObjMatchEntryDAO.getUnMatchedVouchersForMatchingFromTreasuryNetData(lStrFromDate,lStrToDate,lLongTreasuryCode);
				}

				inputMap.put("selectedTreasury", lStrTreasuryCode);
				inputMap.put("selectedYear", yearId);
				inputMap.put("fromDate", lDateFromDate);
				inputMap.put("toDate", lDateToDate);

				lBlFirstTimeLoadFlag = false;

			}






			// For MstContriVoucherDtls List

			if(lListUnMatchedVouchersFromMstContri == null || lListUnMatchedVouchersFromMstContri.size()==0)
			{
				inputMap.put("totalRecordsMstContri", 0);
			}
			else
			{
				inputMap.put("totalRecordsMstContri", lListUnMatchedVouchersFromMstContri.size());
			}
			inputMap.put("lListUnMatchedVouchersFromMstContri",lListUnMatchedVouchersFromMstContri);

			/*if(lListUnMatchedVouchersFromMstContri != null || lListUnMatchedVouchersFromMstContri.size()!=0){
				rltContridtls=Long.parseLong(lListUnMatchedVouchersFromMstContri.get(8).toString());	
			}*/


			// For Treasury Net Data List
			/*if(lListUnMatchedVouchersFromMstContri != null || lListUnMatchedVouchersFromMstContri.size()!=0){
              inputMap.put("rltContridtls",rltContridtls);
			}*/

			if(lListUnMatchedVouchersFromTreasuryNet == null || lListUnMatchedVouchersFromTreasuryNet.size()==0)
			{
				inputMap.put("totalRecordsTrsryNet", 0);
			}
			else
			{
				inputMap.put("totalRecordsTrsryNet", lListUnMatchedVouchersFromTreasuryNet.size());
			}
			inputMap.put("lListUnMatchedVouchersFromTreasuryNet",lListUnMatchedVouchersFromTreasuryNet);


			inputMap.put("YEARS", lLstYears);
			inputMap.put("TreasuryList", lLstTreasury);
			inputMap.put("lBlFirstTimeLoadFlag", lBlFirstTimeLoadFlag);
			inputMap.put("lLstSubTreasury", lLstSubTreasury);
			inputMap.put("userType", userType);
			/*inputMap.put("rltControVouc", rltControVouc);*/

			resObj.setResultValue(inputMap);
			resObj.setViewName("MatchContriManually");

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

	public ResultObject matchVouchersWithTreasuryNet(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;
		
		Date CkVouhdate=null;
		List<Object []>  matchlist =new ArrayList<Object []>(); 
		List<Object []>  templist =new ArrayList<Object []>();
		try {

			setSessionInfo(inputMap);

			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());

			String lStrVoucherIdPks = StringUtility.getParameter("contriVoucherIdPks",
					request);
			
			gLogger.info("lStrVoucherIdPks is"+lStrVoucherIdPks);
			
			String LStrNewVchrNo = request.getParameter("voucherNo") != null && !request.getParameter("voucherNo").equals("") ? 
				request.getParameter("voucherNo").toString().trim() : null;


				gLogger.info("LStrNewVchrNo is"+LStrNewVchrNo);
				
			String LStrNewSchemeCode =request.getParameter("schemeCode") != null && !request.getParameter("schemeCode").equals("") ? 
				request.getParameter("schemeCode").toString().trim() : null;


				gLogger.info("LStrNewSchemeCode is"+LStrNewSchemeCode);
				
				
			String LStrNewVchrDate =request.getParameter("voucherDate") != null && !request.getParameter("voucherDate").equals("") ? 
				request.getParameter("voucherDate").toString().trim() : null ;
			

				gLogger.info("LStrNewVchrDate is"+LStrNewVchrDate);
				
		   String LStrVoucheramt =request.getParameter("Voucheramt") != null && !request.getParameter("Voucheramt").equals("") ? 
				request.getParameter("Voucheramt").toString().trim() : null ;
					

				gLogger.info("LStrVoucheramt is"+LStrVoucheramt);
				
			String LStrDdoCode =request.getParameter("ddoCode") != null && !request.getParameter("ddoCode").equals("") ? 
					request.getParameter("ddoCode").toString().trim() : null ;
			
			

					gLogger.info("LStrDdoCode is "+LStrDdoCode);
					
			String Remark = StringUtility.getParameter("Remark",
					request);
			

			gLogger.info("Remark is "+Remark);

			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			if(!"".equals(lStrVoucherIdPks)){

				String[] lStrArrVoucherIdPks = lStrVoucherIdPks.split("~");
				String[] lStrArrRemark = Remark.split("~");
				Long[]  lLongArrVoucherIdPks = new Long[lStrArrVoucherIdPks.length];
				String[] lLongArrRemark = new String[lStrArrRemark.length];
				String[] lStrArrLStrNewVchrDate = LStrNewVchrDate.split("~");
				
				Date [] lDtNewVchrDate=new Date[lStrArrVoucherIdPks.length];
				gLogger.info("lStrArrLStrNewVchrDate is"+lStrArrLStrNewVchrDate[0]);
				gLogger.info("lLongArrVoucherIdPks.length"+lLongArrVoucherIdPks.length);
				gLogger.info("lLongArrRemark.length"+lLongArrRemark.length);
				for (Integer lInt = 0; lInt < lLongArrVoucherIdPks.length; lInt++) {
					
					
						gLogger.info("Inside for lStrArrLStrNewVchrDate is"+lStrArrLStrNewVchrDate[0]);
						if(lStrArrLStrNewVchrDate[lInt]!=null){
							gLogger.info("***"+lInt);
							
						lDtNewVchrDate[lInt] = sdf3.parse(lStrArrLStrNewVchrDate[lInt]);
						gLogger.info("Inside for lStrArrLStrNewVchrDate is"+lStrArrLStrNewVchrDate[0]);
					}
				}	
				String[] lStrArrLStrNewVchrNo = LStrNewVchrNo.split("~");
				String[] lStrArrLStrNewSchemeCode = LStrNewSchemeCode.split("~");
			    String[] lStrArrLStrVoucheramt = LStrVoucheramt.split("~");
				String[] lStrArrLStrDdoCode=  LStrDdoCode.split("~");
				String[] StrArrLStrNewVchrNo = new String[lStrArrLStrNewVchrNo.length];
				String[] StrArrLStrNewSchemeCode = new String[lStrArrLStrNewSchemeCode.length];
				Date [] StrArrLStrNewVchrDate = new Date[lDtNewVchrDate.length];
				String[] StrArrLStrVoucheramt = new String[lStrArrLStrVoucheramt.length];
				String[] StrArrLStrDdoCode = new String[lStrArrLStrDdoCode.length];
				
				
				
				for (Integer lInt = 0; lInt < lLongArrVoucherIdPks.length; lInt++) {
					
					StrArrLStrNewVchrNo[lInt] = lStrArrLStrNewVchrNo[lInt];
					StrArrLStrNewSchemeCode[lInt]=lStrArrLStrNewSchemeCode[lInt];
					StrArrLStrNewVchrDate[lInt]=lDtNewVchrDate[lInt];
					StrArrLStrVoucheramt[lInt]=lStrArrLStrVoucheramt[lInt];
					StrArrLStrDdoCode[lInt]=lStrArrLStrDdoCode[lInt];
					
					matchlist=lObjMatchEntryDAO.checkStatusOfMatch(StrArrLStrDdoCode[lInt],StrArrLStrNewVchrNo[lInt],StrArrLStrNewSchemeCode[lInt],StrArrLStrNewVchrDate[lInt],StrArrLStrVoucheramt[lInt]);
					templist.addAll(matchlist);
				}

			}

                 gLogger.info("matchlist size is "+templist.size());

			if(!"".equals(lStrVoucherIdPks) && templist.size()==0 )
			{
				String[] trArrVoucherIdPks = lStrVoucherIdPks.split("~");
				String[] lStrArrRemark = Remark.split("~");
			    Long [] lLongArrVoucherIdPks = new Long[trArrVoucherIdPks.length];
			    String[]  lLongArrRemark = new String[lStrArrRemark.length];
				for (Integer lInt = 0; lInt < lLongArrVoucherIdPks.length; lInt++) {
					lLongArrVoucherIdPks[lInt] = Long.valueOf(trArrVoucherIdPks[lInt]);
					lLongArrRemark[lInt] = lStrArrRemark[lInt];
					lObjMatchEntryDAO.updateVouchersManuallyMatched(lLongArrVoucherIdPks[lInt],lLongArrRemark[lInt]);
				}
				lBFlag = true;
			}

			}

		 catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		String lSBStatus = getResponseXMLDoc1(templist,lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}
	
	public ResultObject updateVoucherDtlsManually(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;
		List lDcpsIdByTrn=null;
		List lPaybillId=null;
		Object[] tuple = null;
		Object [] tuple1 = null;

		Long billGrpId=null;
		Long monthId=null;
		String yearDesc=null;
		String[] yearDesc1=null;
		Long payBillId=null;
		try {

			setSessionInfo(inputMap);
			gLogger.info(" in updateVoucherDtlsManually" );
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			Date lDtNewVchrDate = null;

			String lStrVoucherIdPks = request.getParameter("contriVoucherIdPk") != null && !request.getParameter("contriVoucherIdPk").equals("") ? 
					request.getParameter("contriVoucherIdPk").toString().trim() : null;
			String LStrNewVchrNo = request.getParameter("txtNewVoucherNo") != null && !request.getParameter("txtNewVoucherNo").equals("") ? 
					request.getParameter("txtNewVoucherNo").toString().trim() : null;

			String LStrNewSchemeCode =request.getParameter("txtNewSchemeCode") != null && !request.getParameter("txtNewSchemeCode").equals("") ? 
					request.getParameter("txtNewSchemeCode").toString().trim() : null;

			String LStrNewVchrDate =request.getParameter("txtNewVoucherDate") != null && !request.getParameter("txtNewVoucherDate").equals("") ? 
					request.getParameter("txtNewVoucherDate").toString().trim() : null ;

			gLogger.info(" lStrVoucherIdPks "+lStrVoucherIdPks+" "+LStrNewVchrNo);
			if(LStrNewVchrDate!= null && !LStrNewVchrDate.equals("")){
					lDtNewVchrDate = sdf1.parse(LStrNewVchrDate);
				}


				String Manualcount =request.getParameter("count") != null && !request.getParameter("count").equals("") ? 
				request.getParameter("count").toString().trim() : null ;	

				System.out.println("Manualcountrecord  "+Manualcount);


				if(lStrVoucherIdPks != null && !"".equals(lStrVoucherIdPks))
				{
				Long Manualcountrecord=Long.parseLong(Manualcount);		
				System.out.println("Manualcountrecord count is "+Manualcountrecord);
				Long LStrNewVchrNos=null;
				MstDcpsContriVoucherDtls lObjMstDcpsContriVchrDtls = (MstDcpsContriVoucherDtls) lObjMatchEntryDAO.read(Long.parseLong(lStrVoucherIdPks));
				
				if(LStrNewVchrNo != null)
				lObjMstDcpsContriVchrDtls.setVoucherNo(Long.parseLong(LStrNewVchrNo));
				
				if(LStrNewVchrDate != null)
				lObjMstDcpsContriVchrDtls.setVoucherDate(lDtNewVchrDate);
				
				if(LStrNewSchemeCode != null)
				lObjMstDcpsContriVchrDtls.setSchemeCode(LStrNewSchemeCode);
				lObjMstDcpsContriVchrDtls.setUpdatedDate(new Date());
                if(LStrNewVchrNo != null || LStrNewVchrDate != null ||LStrNewSchemeCode != null){
				++Manualcountrecord;
				lObjMstDcpsContriVchrDtls.setManualChangeCount(Manualcountrecord);
				}

				lObjMatchEntryDAO.update(lObjMstDcpsContriVchrDtls);

				if(LStrNewVchrNo != null){
				LStrNewVchrNos=Long.parseLong(LStrNewVchrNo);
				}

                if(lStrVoucherIdPks != null && !"".equals(lStrVoucherIdPks))
				{

					if(LStrNewVchrNo!=null){
				LStrNewVchrNos=Long.parseLong(LStrNewVchrNo);	
				}

				Long lStrVoucherIdsPks=Long.parseLong(lStrVoucherIdPks);
				lObjMatchEntryDAO.updateVouchersManuallyTrnsMatched(lStrVoucherIdsPks,LStrNewVchrNos,LStrNewSchemeCode,lDtNewVchrDate);
				lDcpsIdByTrn=lObjMatchEntryDAO.selectDcpsIdForVoucherInTrn(lStrVoucherIdsPks);
				}

				if (lDcpsIdByTrn != null && !lDcpsIdByTrn.isEmpty())
				{
				Iterator it = lDcpsIdByTrn.iterator();

					while (it.hasNext())
				{
				tuple = (Object[]) it.next();
				billGrpId = Long.valueOf(tuple[0].toString());
				monthId= Long.valueOf(tuple[1].toString());
				yearDesc= tuple[2].toString();

				yearDesc1=yearDesc.split("-");
				int ListSize=lDcpsIdByTrn.size();

				gLogger.info("billGrpId is ******"+billGrpId);
				gLogger.info("monthId is *****"+monthId);
				gLogger.info("yearDesc is *****"+yearDesc);

				if(monthId>3 && monthId<=12)  
				{
					yearDesc= yearDesc1[0];

				}

				else if(monthId<=3 && monthId>=1)
				{
					yearDesc=yearDesc1[1];
				}
					gLogger.info("yearDesc is here*****"+yearDesc);

					if(LStrNewVchrNos!=null || lDtNewVchrDate!=null )
				{

					lObjMatchEntryDAO.updateVouchersManuallyPayMatched(LStrNewVchrNos,lDtNewVchrDate,billGrpId,monthId,yearDesc);
				}
				}
				}
				lBFlag = true;
				}
				else {
					lBFlag = false;
			}


		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}



	/*	public ResultObject updateVoucherDtlsTrnManually(Map<String, Object> inputMap, String lStrVoucherIdPks, Long strNewVchrNo, String strNewSchemeCode, String strNewVchrDate) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;
		Object[] tuple = null;
		Object [] tuple1 = null;

		Long dcpsId=null;
		Long monthId=null;
		String yearDesc=null;
		String[] yearDesc1=null;
		Long payBillId=null;

		try {

			setSessionInfo(inputMap);
			gLogger.info(" in updateVoucherDtlsTrnManually" );
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			Date lDtNewVchrDate = null;
			Long LStrNewVchrNos=null;
			lStrVoucherIdPks = request.getParameter("contriVoucherIdPk") != null && !request.getParameter("contriVoucherIdPk").equals("") ? 
					request.getParameter("contriVoucherIdPk").toString().trim() : null;
					String LStrNewVchrNo = request.getParameter("txtNewVoucherNo") != null && !request.getParameter("txtNewVoucherNo").equals("") ? 
							request.getParameter("txtNewVoucherNo").toString().trim() : null;

							String LStrNewSchemeCode =request.getParameter("txtNewSchemeCode") != null && !request.getParameter("txtNewSchemeCode").equals("") ? 
									request.getParameter("txtNewSchemeCode").toString().trim() : null;

									String LStrNewVchrDate =request.getParameter("txtNewVoucherDate") != null && !request.getParameter("txtNewVoucherDate").equals("") ? 
											request.getParameter("txtNewVoucherDate").toString().trim() : null ;
											if(LStrNewVchrDate!= null && !LStrNewVchrDate.equals("")){
												lDtNewVchrDate = sdf1.parse(LStrNewVchrDate);

											}

											List lDcpsIdByTrn=null;
											List lPaybillId=null;
											if(lStrVoucherIdPks != null && !"".equals(lStrVoucherIdPks))
											{

												if(LStrNewVchrNo!=null){
													 LStrNewVchrNos=Long.parseLong(LStrNewVchrNo);	
												}

												Long lStrVoucherIdsPks=Long.parseLong(lStrVoucherIdPks);
												lObjMatchEntryDAO.updateVouchersManuallyTrnsMatched(lStrVoucherIdsPks,LStrNewVchrNos,LStrNewSchemeCode,lDtNewVchrDate);
												lDcpsIdByTrn=lObjMatchEntryDAO.selectDcpsIdForVoucherInTrn(lStrVoucherIdsPks);
											}
											if (lDcpsIdByTrn != null && !lDcpsIdByTrn.isEmpty())
											{
												Iterator it = lDcpsIdByTrn.iterator();

												while (it.hasNext())
												{
													tuple = (Object[]) it.next();
													dcpsId = Long.valueOf(tuple[0].toString());
													monthId= Long.valueOf(tuple[1].toString());
													yearDesc= tuple[2].toString();

													yearDesc1=yearDesc.split("-");
													int ListSize=lDcpsIdByTrn.size();

													gLogger.info("dcpsEmpid is ******"+dcpsId);
													gLogger.info("monthId is *****"+monthId);
													gLogger.info("yearDesc is *****"+yearDesc);

													if(monthId>3 && monthId<=12)  
													{
														yearDesc= yearDesc1[0];

													}

													else if(monthId<=3 && monthId>=1)
													{
														yearDesc=yearDesc1[1];
													}
													gLogger.info("yearDesc is here*****"+yearDesc);

													lDcpsIdByTrn=new ArrayList();
													lPaybillId=new ArrayList();

													lPaybillId =lObjMatchEntryDAO.getPayBillId(dcpsId, monthId,yearDesc);

												}
											}

											if (lPaybillId != null && !lPaybillId.isEmpty())
											{

												for(int i=0;i<lPaybillId.size();i++)
												{

													if(LStrNewVchrNo!=null){
														LStrNewVchrNos=Long.parseLong(LStrNewVchrNo);
													}



													payBillId = Long.valueOf(lPaybillId.get(i).toString());
													gLogger.info("payBillId is *****"+payBillId);
													lObjMatchEntryDAO.updateVouchersManuallyPayMatched(LStrNewVchrNos,lDtNewVchrDate,payBillId);
												}


											}




		}
		catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}*/

	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}




	private StringBuilder getResponseXMLDoc1(List<Object[]> matchlist,boolean status) {


gLogger.info("status is "+status);
		StringBuilder lSBStatus=new StringBuilder();
		if(matchlist.size()>0){

			lSBStatus.append("<XMLDOC>");

			for (Object[] lObjArr : matchlist) {
				long Voucher_no = Long.parseLong((lObjArr[0]).toString());
				Date Voucher_date = (Date) lObjArr[1];
				System.out.println(Voucher_no + "\t" + Voucher_date );

				lSBStatus.append("<DETAIL>");

				lSBStatus.append("<Voucher_No>");

				lSBStatus.append(Voucher_no);

				lSBStatus.append("</Voucher_No>");
				lSBStatus.append("<Voucher_Date>");

				lSBStatus.append(Voucher_date);

				lSBStatus.append("</Voucher_Date>");


				lSBStatus.append("</DETAIL>");


			

			}
			
			lSBStatus.append("<Flag>");
			lSBStatus.append(status);
			lSBStatus.append("</Flag>");
			lSBStatus.append("</XMLDOC>");
            gLogger.info(lSBStatus.toString());
		}


		else {
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<Flag>");
			lSBStatus.append(status);
			lSBStatus.append("</Flag>");
			lSBStatus.append("</XMLDOC>");

		}
		return lSBStatus;
	}

	public ResultObject getDDOcodesFromTresury(Map<String, Object> inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		try {

			setSessionInfo(inputMap);
			gLogger.info(" in getDDOcodesFromTresury" );
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					MstDcpsContriVoucherDtls.class, serv.getSessionFactory());


			String tresuryCode = request.getParameter("treasuryCode") != null && !request.getParameter("treasuryCode").equals("") ? 
					request.getParameter("treasuryCode").toString().trim() : null;

					gLogger.info(" tresuryCode "+tresuryCode);
					List lDDOcode=lObjMatchEntryDAO.getAllDDOForTreasury(tresuryCode);

					String lSBStatus = getDDOResponseXMLDoc(lDDOcode).toString();
					String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
					.toString();

					inputMap.put("ajaxKey", lStrResult);
					resObj.setResultValue(inputMap);
					resObj.setViewName("ajaxData");


		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private StringBuilder getDDOResponseXMLDoc(List ddoCode) {

		StringBuilder lStrBldXML = new StringBuilder();

		Object[] tuple=null;
		Iterator it = ddoCode.iterator();

		while (it.hasNext())
		{
			tuple = (Object[]) it.next();
			lStrBldXML.append("<ddoList>");
			lStrBldXML.append("<ddoCode>");
			lStrBldXML.append(tuple[0].toString());
			lStrBldXML.append("</ddoCode>");

			lStrBldXML.append("<ddoName>");
			lStrBldXML.append("<![CDATA[("+tuple[0].toString()+")"+tuple[1].toString()+"]]>");

			lStrBldXML.append("</ddoName>");
			lStrBldXML.append("</ddoList>");
		}


		return lStrBldXML;
	}

	// Added by Ashish to check updation validation od voucher_no,voucher_date and scheme code

	public ResultObject checkUpdateStatus(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean Status = false;
		Date Nvoucherdate=null;
		Date  Ovoucherdate1=null;
		try {

			setSessionInfo(inputMap);

			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());

			String lStrVoucherIdPks = StringUtility.getParameter("contriVoucherIdPks",
					request);
			String LStrNewVchrNo = request.getParameter("txtNewVoucherNo") != null && !request.getParameter("txtNewVoucherNo").equals("") ? 
					request.getParameter("txtNewVoucherNo").toString().trim() : null;

					String LStrNewSchemeCode =request.getParameter("txtNewSchemeCode") != null && !request.getParameter("txtNewSchemeCode").equals("") ? 
							request.getParameter("txtNewSchemeCode").toString().trim() : null;

							String LStrNewVchrDate =request.getParameter("txtNewVoucherDate") != null && !request.getParameter("txtNewVoucherDate").equals("") ? 
									request.getParameter("txtNewVoucherDate").toString().trim() : "No" ;


									String LDddoCode =request.getParameter("ddoCode") != null && !request.getParameter("ddoCode").equals("") ? 
											request.getParameter("ddoCode").toString().trim() : null ;

											String LStrOldVchrNo = request.getParameter("Voucherno") != null && !request.getParameter("Voucherno").equals("") ? 
													request.getParameter("Voucherno").toString().trim() : null;	 


													String LStrOldVchrDate =request.getParameter("Voucherdate") != null && !request.getParameter("Voucherdate").equals("") ? 
															request.getParameter("Voucherdate").toString().trim() : "Noo" ;	

															System.out.println("LStrOldVchrDate is "+LStrOldVchrDate);
															System.out.println("LStrNewVchrDate is "+LStrNewVchrDate);

															String LStrOldSchemeCode =request.getParameter("Schemcode") != null && !request.getParameter("Schemcode").equals("") ? 
																	request.getParameter("Schemcode").toString().trim() : null ;		

																	SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
																	SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

																	if(!LStrNewVchrDate.equals("No")){
																		Nvoucherdate=sdf2.parse(LStrNewVchrDate);
																	}
																	//Date Ovoucherdate=sdf2.parse(LStrOldVchrDate);
																	if(!LStrOldVchrDate.equals("Noo")){
																		Ovoucherdate1=sdf3.parse(LStrOldVchrDate);
																	}
																	//	Date Ovoucherdate=sdf2.parse(Ovoucherdate1);

																	if(LStrNewVchrNo==null ){
																		LStrNewVchrNo=LStrOldVchrNo;
																	}
																	if(LStrNewSchemeCode==null ){
																		LStrNewSchemeCode=LStrOldSchemeCode;
																	}
																	if(LStrNewVchrDate==null ){
																		Nvoucherdate=Ovoucherdate1;
																	}
																	System.out.println("LStrOldVchrDate before parse is "+Ovoucherdate1);
																	System.out.println("LStrOldVchrDate is "+Ovoucherdate1);
																	System.out.println("LStrNewVchrDate is "+Nvoucherdate);


																	System.out.println("Voucher_date is after parse***"+Nvoucherdate);		
																	Status=lObjMatchEntryDAO.checkUpdateStatusDtls(LDddoCode,LStrNewVchrNo,Nvoucherdate,LStrNewSchemeCode);





		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		String lSBStatus = getResponseXMLDoc(Status).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	//ended by ashish	


}
