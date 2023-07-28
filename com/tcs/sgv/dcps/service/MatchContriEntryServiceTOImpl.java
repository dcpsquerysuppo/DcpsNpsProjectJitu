/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 12, 2013		Ashish Sharma						
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
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;

/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Dec 12, 2013
 */
public class MatchContriEntryServiceTOImpl extends ServiceImpl implements
   MatchContriEntryServiceTO {

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

	public ResultObject loadMatchContriEntryFormTO(Map inputMap) {

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
		Long lLstSubTreasury =null;
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			MatchContriEntryDAOTO lObjMatchEntryDAO = new MatchContriEntryDAOTOImpl(
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
				lStrFromDate = sdf2.format(sdf1.parse(lStrTempFromDate));
				lStrToDate = sdf2.format(sdf1.parse(lStrTempToDate));

				lDateFromDate = sdf1.parse(lStrTempFromDate);
				lDateToDate = sdf1.parse(lStrTempToDate);
				
				gLogger.info("Treasury code  is"+gStrLocationCode);
			
				lLstSubTreasury = lObjMatchEntryDAO.getSubTreasuryList(gStrLocationCode);
				String treasurycd=lLstSubTreasury+"%";
				
				gLogger.info("Treasury List size is"+lLstSubTreasury);
				lListTreasuriesMatchEntries = lObjMatchEntryDAO
				.getAllTreasuriesForMatchedEntries(lStrFromDate,
						lStrToDate,treasurycd,AcMain);



				//   gLogger.info("totalSAmount is ***"+totalSAmount);
				inputMap.put("totalRecords", lListTreasuriesMatchEntries.size());
				inputMap.put("selectedYear", yearId);
				inputMap.put("fromDate", lDateFromDate);
				inputMap.put("toDate", lDateToDate);
				inputMap.put("selectedAcc", AcMain);


	/*			Object[] tuple = null;


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
			inputMap.put("ListTreasuriesMatchEntries",
					lListTreasuriesMatchEntries);

			inputMap.put("AccMain", lActMain);

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

	public ResultObject loadUnMatchContriEntryFormTO(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesUnMatchEntries = null;
		Long yearId = null;

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

				lStrFromDate = sdf2.format(sdf1.parse(lStrTempFromDate));
				lStrToDate = sdf2.format(sdf1.parse(lStrTempToDate));

				lDateFromDate = sdf1.parse(lStrTempFromDate);
				lDateToDate = sdf1.parse(lStrTempToDate);

				//lListTreasuriesUnMatchEntries = lObjMatchEntryDAO.getAllTreasuriesForUnMatchedEntries(lStrFromDate,lStrToDate);

				lListTreasuriesUnMatchEntriesMstContri = lObjMatchEntryDAO.getAllTreasuriesForUnMatchedEntriesMstcontri(gStrLocationCode,lStrFromDate,lStrToDate);

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

				lListTreasuriesUnMatchEntriesTreasuryNet = lObjMatchEntryDAO.getAllTreasuriesForUnMatchedEntriesTreasuryNet(gStrLocationCode,lStrFromDate,lStrToDate);
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
	

	public ResultObject loadManualMatchTO(Map inputMap) {

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
		
			try {
				
				setSessionInfo(inputMap);
				
				MatchContriEntryDAOTO lObjMatchEntryDAO = new MatchContriEntryDAOTOImpl(
						null, serv.getSessionFactory());
				
				String lStrVoucherIdPks = StringUtility.getParameter("contriVoucherIdPks",
						request);
				
				if(!"".equals(lStrVoucherIdPks))
				{
					String[] lStrArrVoucherIdPks = lStrVoucherIdPks.split("~");
					Long[] lLongArrVoucherIdPks = new Long[lStrArrVoucherIdPks.length];
					for (Integer lInt = 0; lInt < lLongArrVoucherIdPks.length; lInt++) {
						lLongArrVoucherIdPks[lInt] = Long.valueOf(lStrArrVoucherIdPks[lInt]);
						lObjMatchEntryDAO.updateVouchersManuallyMatched(lLongArrVoucherIdPks[lInt]);
					}
				}
			
				lBFlag = true;
			
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
	
	public ResultObject updateVoucherDtlsManually(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;
		
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
				
				if(lStrVoucherIdPks != null && !"".equals(lStrVoucherIdPks))
				{
					MstDcpsContriVoucherDtls lObjMstDcpsContriVchrDtls = (MstDcpsContriVoucherDtls) lObjMatchEntryDAO.read(Long.parseLong(lStrVoucherIdPks));
					if(LStrNewVchrNo != null)
					lObjMstDcpsContriVchrDtls.setVoucherNo(Long.parseLong(LStrNewVchrNo));
					
					if(LStrNewVchrDate != null)
					lObjMstDcpsContriVchrDtls.setVoucherDate(lDtNewVchrDate);
					
					if(LStrNewSchemeCode != null)
					lObjMstDcpsContriVchrDtls.setSchemeCode(LStrNewSchemeCode);
					
					lObjMatchEntryDAO.update(lObjMstDcpsContriVchrDtls);
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
	
	
	
	
	
	private StringBuilder getResponseXMLDoc(boolean flag) {
	
			StringBuilder lStrBldXML = new StringBuilder();
	
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<Flag>");
			lStrBldXML.append(flag);
			lStrBldXML.append("</Flag>");
			lStrBldXML.append("</XMLDOC>");
	
			return lStrBldXML;
		}
}
