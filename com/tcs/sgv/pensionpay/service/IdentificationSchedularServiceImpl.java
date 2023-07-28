/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 11, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.util.StringUtils;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CmnHolidayMstDAO;
import com.tcs.sgv.pensionpay.dao.CmnHolidayMstDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.IdentificationSchedularDAO;
import com.tcs.sgv.pensionpay.dao.IdentificationSchedularDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPensionerDtlsDAO;
import com.tcs.sgv.pensionpay.dao.MstPensionerDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPnsnpmntSlotsDAO;
import com.tcs.sgv.pensionpay.dao.MstPnsnpmntSlotsDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAO;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.CmnHolidayMst;
import com.tcs.sgv.pensionpay.valueobject.MstPensionChecklist;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPnsnpmntSlots;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnFinYearLetterNo;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerChecklistDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsnpmntSchedular;


/**
 * Class Description - This class is used to get and set both call date and call
 * time for the pensioner.
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 11, 2011
 */
public class IdentificationSchedularServiceImpl extends ServiceImpl implements IdentificationSchedularService {

	/* Global Variable for Logger Class */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator servLoc = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Location Code */

	Long gLngLocId = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private final ResourceBundle gObjRsrcBndleMarathi = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants_ma");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			servLoc = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gLngLocId = SessionHelper.getLocationId(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = DBUtility.getCurrentDateFromDB();

		} catch (Exception e) {
			gLogger.error("Error in setSessionInfo :" + e);
			throw (e);
		}

	}

	/**
	 * Method to schedule the identification process for a pensioner.
	 * 
	 * @param Map
	 * @return ResultObject
	 */
	public ResultObject scheduleIdentification(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String[] lArrPpoNo = null;
		// Creating Holiday List

		List<Date> lLstHolidays = new ArrayList<Date>();
		HashMap<Integer, MstPnsnpmntSlots> lMapSlots = new HashMap<Integer, MstPnsnpmntSlots>();
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date lDtNext = null;
		Integer lIntNextSlot = null;
		List<String> lLstCallDate = null;
		List<String> lLstCallTime = null;
		List<String> lLstSlotNo = null;
		StringBuilder lSBResp = null;
		String lStrRowNums = null;
		String lStrPnsrCode = null;		
		String[] lArrRowNums = null;
		String[] lArrPnsrCode = null;		
		String lStrPensionerCode = "";
		TrnPnsnpmntSchedular lObjTrnPnsnpmntSchedular = null;
		List<String> lLstPnsrCode = new ArrayList<String>();
		List lLstExistingPensnrCode = null;
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		TrnFinYearLetterNo lObjTrnFinYearLetterNo = null;
		Long lLngTrnFinYearLetterNoId = null;
		String lStrPpoNo = "";		
		Long lLngLetterNo = null;
		Integer lIntFinYearId = null;		
		Long lLngPkCntTrnFinYearLetter = null;
		
		try {
			setSessionInfo(inputMap);
			lStrRowNums = StringUtility.getParameter("rowNum", request);
			lStrPnsrCode = StringUtility.getParameter("pnsrCode", request);
			lStrPpoNo = StringUtility.getParameter("ppoNo",request);

			if (lStrRowNums != null && !lStrRowNums.equals("")) {
				lArrRowNums = lStrRowNums.split("~");
			}

			if (lStrPnsrCode != null && !lStrPnsrCode.equals("")) {
				lArrPnsrCode = lStrPnsrCode.split("~");
			}
			
			if(lStrPpoNo != null && !lStrPpoNo.equals("")){
				lArrPpoNo = lStrPpoNo.split("~");
			}
			Map<String,String> lMapPnsrCodePpoNo = new HashMap<String,String>();
			for(int i = 0;i < lArrPpoNo.length ; i++)
			{
				lMapPnsrCodePpoNo.put(lArrPnsrCode[i], lArrPpoNo[i]);
			}
			/*
			 * For getting slot number and slot time from MstPnsnpmntSlots
			 */
			MstPnsnpmntSlotsDAO lObjMstPnsnpmntSlotsDAO = new MstPnsnpmntSlotsDAOImpl(MstPnsnpmntSlots.class, servLoc.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAOImpl = new FinancialYearDAOImpl(SgvcFinYearMst.class, servLoc.getSessionFactory());
			CommonPensionDAO lObjCommonPensionDAOImpl = new CommonPensionDAOImpl(servLoc.getSessionFactory());

			lMapSlots = lObjMstPnsnpmntSlotsDAO.getMaxSlotNumber();
			Object[] lArrSlotNos = lMapSlots.keySet().toArray();

			getAllHolidayList(inputMap);
			/*
			 * Making list of callDate and callTime for selected pensioner
			 */
			lDtNext = (Date) inputMap.get("lDtCurrent");
			lIntNextSlot = (Integer) inputMap.get("lIntNextSlot");
			lLstHolidays = (List<Date>) inputMap.get("lLstHolidays");
			lLstCallDate = new ArrayList<String>();
			lLstCallTime = new ArrayList<String>();
			lLstSlotNo = new ArrayList<String>();
			lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
			
			if (lArrRowNums != null && lArrPnsrCode != null) {
				for (int index = 0; index < lArrRowNums.length; index++) {
					/*
					 * If lLngNextSlot is max slot value then set nextslot to 1
					 * and compute next valid date , otherwise increment slot
					 * value by 1 and keep date as it is.
					 */
					getValidDateTime(lLstHolidays, lDtNext, lIntNextSlot, lArrSlotNos, inputMap);
					lDtNext = (Date) inputMap.get("lDtNextValid");
					lIntNextSlot = (Integer) inputMap.get("lIntValidSlot");

					/*
					 * For saving schedule details in TrnPnsnPmntSchedular
					 */
					Long lLngSchedularId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnpmnt_schedular", inputMap);
					lObjTrnPnsnpmntSchedular = new TrnPnsnpmntSchedular();
					lObjTrnPnsnpmntSchedular.setSchedularId(lLngSchedularId);
					lObjTrnPnsnpmntSchedular.setPensionerCode(Long.valueOf(lArrPnsrCode[index]));
					lObjTrnPnsnpmntSchedular.setCallDate(lDtNext);
					lObjTrnPnsnpmntSchedular.setSlotNo(Long.valueOf(lIntNextSlot.toString()));
					lObjTrnPnsnpmntSchedular.setSlotTime(lMapSlots.get(lIntNextSlot).getSlotTime());
					lObjTrnPnsnpmntSchedular.setScheduleStatus(gObjRsrcBndle.getString("IDENT.AWAITED"));
					lObjTrnPnsnpmntSchedular.setCreatedUserId(gLngUserId);
					lObjTrnPnsnpmntSchedular.setCreatedPostId(gLngPostId);
					lObjTrnPnsnpmntSchedular.setCreatedDate(gDtCurDate);
					lObjTrnPnsnpmntSchedular.setLocationCode(gLngLocId);
					lObjTrnPnsnpmntSchedular.setDbId(gLngDBId);
					lObjIdentificationSchedularDAO.create(lObjTrnPnsnpmntSchedular);

					lLstCallDate.add(lObjSimpleDateFormat.format(lDtNext));
					lLstCallTime.add(lMapSlots.get(lIntNextSlot).getSlotTime());
					lLstSlotNo.add(lIntNextSlot.toString());
					lLstPnsrCode.add(lArrPnsrCode[index]);

				}
			}
			
			String lStrLetterType = "Identification";
			lLstExistingPensnrCode = lObjIdentificationSchedularDAO.getPensionerCode(lArrPnsrCode, gLngLocId.intValue(),lStrLetterType);
			lLstPnsrCode.removeAll(lLstExistingPensnrCode);
			lLngPkCntTrnFinYearLetter = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("TRN_FINYEAR_LETTER_NO", inputMap, lLstPnsrCode.size());
			lIntFinYearId = lObjFinancialYearDAOImpl.getFinYearIdByCurDate();			
			
			for(int index = 0; index < lLstPnsrCode.size(); index++)
			{
				lObjTrnFinYearLetterNo = new TrnFinYearLetterNo();
				lStrPensionerCode = lLstPnsrCode.get(index);
				lStrPpoNo = lMapPnsrCodePpoNo.get(lStrPensionerCode);
				lLngTrnFinYearLetterNoId = ++lLngPkCntTrnFinYearLetter;
				lLngTrnFinYearLetterNoId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngTrnFinYearLetterNoId, inputMap);
				lObjTrnFinYearLetterNo.setTrnFinYearLetterNoId(++lLngTrnFinYearLetterNoId);
				lObjTrnFinYearLetterNo.setPensionerCode(lStrPensionerCode);
				lObjTrnFinYearLetterNo.setPpoNo(lStrPpoNo);
				lObjTrnFinYearLetterNo.setLetterType(lStrLetterType);
				lLngLetterNo = lObjCommonPensionDAOImpl.getMaxLetterNo(gLngLocId.intValue(),lIntFinYearId,lStrLetterType);
				lObjTrnFinYearLetterNo.setLetterNo(Long.parseLong(String.format("%05d", lLngLetterNo)));
				lObjTrnFinYearLetterNo.setLocationCode(gLngLocId.intValue());
				lObjTrnFinYearLetterNo.setFinYearId(lIntFinYearId);
				lObjTrnFinYearLetterNo.setCreatedDate(gDtCurDate);
				lObjTrnFinYearLetterNo.setCreatedPostId(gLngPostId);
				lObjTrnFinYearLetterNo.setCreatedUserId(gLngUserId);
				lObjIdentificationSchedularDAO.create(lObjTrnFinYearLetterNo);
			}
			
			lSBResp = getResponseXMLDocForSchedular(lArrRowNums, lLstCallDate, lLstCallTime, lLstSlotNo);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error in scheduleIdentification :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject saveScheduleDtls(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		MstPensionerDtls lObjMstPensionerDtls = null;
		TrnPnsnpmntSchedular lObjTrnPnsnpmntSchedular = null;
		String lStrPensionerDtlId = null;
		MstPensionerDtlsDAO lObjMstPensionerDtlsDAO = null;
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = null;
		String lStrBankCode = null;
		String lStrBankBranchCode = null;
		String lStrAccountNo = null;
		String lStrCallDate = null;
		String lStrCallTime = null;
		String lStrSlotNo = null;
		String lStrPnsrCode = null;
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		List lLstTrnPnsnpmntSchedularObj = null;
		String lStrSaveUpdtFlag = null;
		String lStrAudiPostId = null;
		String lStrPnsnRqstId = null;
		String lStrRowNums = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		String lStrParaPensionerCode = null;
		MstPnsnpmntSlotsDAO lObjMstPnsnpmntSlotsDAO = null;
		HashMap<Integer, MstPnsnpmntSlots> lMapSlots = new HashMap<Integer, MstPnsnpmntSlots>();
		try {
			setSessionInfo(inputMap);

			lStrPensionerDtlId = StringUtility.getParameter("pensionerDtlId", request);
			lStrParaPensionerCode = StringUtility.getParameter("pensionerId", request);
			lStrRowNums = StringUtility.getParameter("rowNum", request);
			String[] lArrPensionerDtlId = lStrPensionerDtlId.split("~");
			String[] lArrRowNums = lStrRowNums.split("~");
			String[] lArrPensionerCode = lStrParaPensionerCode.split("~");
			lObjMstPensionerDtlsDAO = new MstPensionerDtlsDAOImpl(MstPensionerDtls.class, servLoc.getSessionFactory());
			lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
			lObjMstPnsnpmntSlotsDAO = new MstPnsnpmntSlotsDAOImpl(MstPnsnpmntSlots.class, servLoc.getSessionFactory());
			lMapSlots = new HashMap<Integer, MstPnsnpmntSlots>();
			lMapSlots = lObjMstPnsnpmntSlotsDAO.getMaxSlotNumber();
			for (int i = 0; i < lArrRowNums.length; i++) {
				/*
				 * Entry in MstPensionerDtls starts
				 */
				lObjMstPensionerDtls = new MstPensionerDtls();
				lObjMstPensionerDtls = (MstPensionerDtls) lObjMstPensionerDtlsDAO.read(Long.parseLong(lArrPensionerDtlId[i]));
				lStrBankCode = StringUtility.getParameter("cmbBankName" + lArrRowNums[i], request).trim();
				lStrBankBranchCode = StringUtility.getParameter("cmbBankBrnchName" + lArrRowNums[i], request).trim();
				lStrAccountNo = StringUtility.getParameter("txtAccountNo" + lArrRowNums[i], request).trim();

				lStrBankCode = (lStrBankCode.length() > 0 && !"-1".equals(lStrBankCode)) ? lStrBankCode : null;
				lStrBankBranchCode = (lStrBankBranchCode.length() > 0 && !"-1".equals(lStrBankBranchCode)) ? lStrBankBranchCode : null;
				lStrAccountNo = (lStrAccountNo.length() > 0) ? lStrAccountNo : null;
				/*
				 * if ((!"".equals(lStrBankCode) && !"-1".equals(lStrBankCode))
				 * && (!"".equals(lStrBankBranchCode) &&
				 * !"-1".equals(lStrBankBranchCode))) {
				 */
				lObjMstPensionerDtls.setBankCode(lStrBankCode);
				lObjMstPensionerDtls.setBranchCode(lStrBankBranchCode);
				lObjMstPensionerDtls.setAccountNo(lStrAccountNo);
				lObjMstPensionerDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
				lObjMstPensionerDtls.setUpdatedDate(gDtCurDate);
				lObjMstPensionerDtlsDAO.update(lObjMstPensionerDtls);

				/*
				 * Entry in MstPensionerDtls ends
				 */

				/*
				 * Entry in TrnPnsnpmntSchedular starts
				 */

				lStrPnsrCode = lArrPensionerCode[i];
				lStrCallDate = StringUtility.getParameter("txtCalledDate" + lArrRowNums[i], request).trim();
				lStrSlotNo = StringUtility.getParameter("cmbCallTime" + lArrRowNums[i], request).trim();
				// lStrSlotNo = StringUtility.getParameter("hidSlotNo" +
				// lArrRowNums[i], request).trim();

				lLstTrnPnsnpmntSchedularObj = new ArrayList();
				lLstTrnPnsnpmntSchedularObj = lObjIdentificationSchedularDAO.getSchedularObjFromPsnrCode(Long.valueOf(lStrPnsrCode), gLngLocId);
				if (lLstTrnPnsnpmntSchedularObj != null && lLstTrnPnsnpmntSchedularObj.size() > 0) {
					lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
					lObjTrnPnsnpmntSchedular = (TrnPnsnpmntSchedular) lLstTrnPnsnpmntSchedularObj.get(0);
					// lStrSaveUpdtFlag = "update";

					// else {
					// lObjTrnPnsnpmntSchedular = new TrnPnsnpmntSchedular();
					// lStrSaveUpdtFlag = "save";
					// }
					if ((!"".equals(lStrSlotNo) && !"-1".equals(lStrSlotNo)) && (!"".equals(lStrCallDate))) {
						lStrCallTime = lMapSlots.get(Integer.valueOf(lStrSlotNo)).getSlotTime();
						lObjTrnPnsnpmntSchedular.setSlotNo(Long.valueOf(lStrSlotNo));
						lObjTrnPnsnpmntSchedular.setSlotTime(lStrCallTime);
						lObjTrnPnsnpmntSchedular.setCallDate(lSdf.parse(lStrCallDate));
						lObjTrnPnsnpmntSchedular.setScheduleStatus(gObjRsrcBndle.getString("IDENT.AWAITED"));
					}
					lObjTrnPnsnpmntSchedular.setUpdatedDate(gDtCurDate);
					lObjTrnPnsnpmntSchedular.setUpdatedPostId(gLngPostId);
					lObjTrnPnsnpmntSchedular.setUpdatedUserId(gLngUserId);
					lObjIdentificationSchedularDAO.update(lObjTrnPnsnpmntSchedular);

					// if ("save".equals(lStrSaveUpdtFlag)) {
					// Long lLngSchedularId =
					// IFMSCommonServiceImpl.getNextSeqNum("TRN_PNSNPMNT_SCHEDULAR",
					// inputMap);
					// lObjTrnPnsnpmntSchedular.setSchedularId(lLngSchedularId);
					// lObjTrnPnsnpmntSchedular.setCreatedUserId(gLngUserId);
					// lObjTrnPnsnpmntSchedular.setCreatedPostId(gLngPostId);
					// lObjTrnPnsnpmntSchedular.setCreatedDate(gDtCurDate);
					// lObjTrnPnsnpmntSchedular.setLocationCode(gLngLocId);
					// lObjTrnPnsnpmntSchedular.setDbId(gLngDBId);
					// lObjIdentificationSchedularDAO.create(lObjTrnPnsnpmntSchedular);
					// }
					// else {
					// lObjTrnPnsnpmntSchedular.setUpdatedDate(gDtCurDate);
					// lObjTrnPnsnpmntSchedular.setUpdatedPostId(gLngPostId);
					// lObjTrnPnsnpmntSchedular.setUpdatedUserId(gLngUserId);
					// lObjIdentificationSchedularDAO.update(lObjTrnPnsnpmntSchedular);
					// }
				}
				/*
				 * Entry in TrnPnsnpmntSchedular ends
				 */

				/*
				 * To save case_owner as auditor post id in trn_pension_rqst_hdr
				 */
				lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, servLoc.getSessionFactory());
				lStrPnsnRqstId = StringUtility.getParameter("hdnpnsnrqstid" + lArrRowNums[i], request).trim();
				lStrAudiPostId = StringUtility.getParameter("hdnAuditorPostId" + lArrRowNums[i], request).trim();
				if (lStrPnsnRqstId != null && !lStrPnsnRqstId.equals("")) {
					lObjTrnPensionRqstHdr = lObjTrnPensionRqstHdrDAO.read(Long.valueOf(lStrPnsnRqstId));
					if (lStrAudiPostId != null && !lStrAudiPostId.equals("")) {
						Long lLngAudiPostId = Long.valueOf(lStrAudiPostId);
						lObjTrnPensionRqstHdr.setCaseOwner(BigDecimal.valueOf(lLngAudiPostId));
					}
					lObjTrnPensionRqstHdr.setUpdatedDate(gDtCurDate);
					lObjTrnPensionRqstHdr.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionRqstHdr.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);
				}
			}
			String lSBResp = "";
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error in saveScheduleDtls :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;

	}

	public StringBuilder getResponseXMLDocForSchedular(String[] lArrRowNums, List<String> paraLstCallDate, List<String> paraLstCallTime, List<String> paraLstSlotNo) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		for (String lStrRowNums : lArrRowNums) {
			lStrBldXML.append("<ROWNO>");
			lStrBldXML.append(lStrRowNums);
			lStrBldXML.append("</ROWNO>");
		}
		for (String lStrCallDate : paraLstCallDate) {
			lStrBldXML.append("<CALLDATE>");
			lStrBldXML.append(lStrCallDate);
			lStrBldXML.append("</CALLDATE>");
		}
		for (String lStrCallTime : paraLstCallTime) {
			lStrBldXML.append("<CALLTIME>");
			lStrBldXML.append(lStrCallTime);
			lStrBldXML.append("</CALLTIME>");
		}

		for (String lStrSlotNo : paraLstSlotNo) {
			lStrBldXML.append("<SLOTNO>");
			lStrBldXML.append(lStrSlotNo);
			lStrBldXML.append("</SLOTNO>");
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;

	}

	/*
	 * This method will check whether call date is before current date. and it
	 * will change scheduleStatus accordingly.
	 */
	public ResultObject updateScheduleStatus(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		// List lLstAllScheduleDtl = null;
		Object[] lArrCallDateDtl = null;
		Date lDtCallDate = null;
		Date lDtCurrDate = new Date();
		Long lLngSchedulerId = null;
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		TrnPnsnpmntSchedular lObjTrnPnsnpmntSchedular = null;
		try {
			setSessionInfo(inputMap);
			// lLstAllScheduleDtl = new ArrayList();
			lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
			// lLstAllScheduleDtl =
			// lObjIdentificationSchedularDAO.getScheduleDtls();
			lObjIdentificationSchedularDAO.updateScheduleStatus(gLngLocId, gDtCurDate);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error in scheduleIdentification :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;

	}

	public ResultObject showReschedule(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Map<Integer, MstPnsnpmntSlots> lMapSlots = new HashMap<Integer, MstPnsnpmntSlots>();
		Date lDtCurrent = null;
		Integer lIntCurrSlot = null;
		Date lDtNext = null;
		Integer lIntNextSlot = null;
		List<MstPnsnpmntSlots> lLstSlotDtls = null;
		List<Date> lLstHolidays = new ArrayList<Date>();
		Integer lIntMaxSlotNo = null;
		String lStrRowNum = null;

		try {
			setSessionInfo(inputMap);
			lStrRowNum = StringUtility.getParameter("rowNum", request);
			/*
			 * For getting slot number and slot time from MstPnsnpmntSlots
			 */
			MstPnsnpmntSlotsDAO lObjMstPnsnpmntSlotsDAO = new MstPnsnpmntSlotsDAOImpl(MstPnsnpmntSlots.class, servLoc.getSessionFactory());

			lMapSlots = lObjMstPnsnpmntSlotsDAO.getMaxSlotNumber();
			Object[] lArrSlotNos = lMapSlots.keySet().toArray();

			getAllHolidayList(inputMap);
			lDtCurrent = (Date) inputMap.get("lDtCurrent");
			lIntCurrSlot = (Integer) inputMap.get("lIntNextSlot");
			lLstHolidays = (List<Date>) inputMap.get("lLstHolidays");
			getValidDateTime(lLstHolidays, lDtCurrent, lIntCurrSlot, lArrSlotNos, inputMap);
			lDtNext = (Date) inputMap.get("lDtNextValid");
			lIntNextSlot = (Integer) inputMap.get("lIntValidSlot");
			lLstSlotDtls = new ArrayList<MstPnsnpmntSlots>();
			lIntMaxSlotNo = (Integer) lArrSlotNos[(lArrSlotNos.length - 1)];
			for (int slotIndex = lIntNextSlot.intValue(); slotIndex <= lIntMaxSlotNo.intValue(); slotIndex++) {
				lLstSlotDtls.add(lMapSlots.get(slotIndex));
			}
			inputMap.put("lDtCurrent", lDtCurrent);
			inputMap.put("lIntCurrSlot", lIntCurrSlot);
			inputMap.put("lDtNext", lDtNext);
			inputMap.put("lIntNextSlot", lIntNextSlot.intValue());
			inputMap.put("lLstSlotDtls", lLstSlotDtls);
			inputMap.put("lStrRowNum", lStrRowNum);

			resObj.setResultValue(inputMap);
			resObj.setViewName("ReschedulePopup");
		} catch (Exception e) {
			gLogger.error("Error in scheduleIdentification :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * This method is used to get all holiday list(public holiday+weekends as
	 * per govt. rule) from current date to next 2 years. Other outputs:last
	 * assigned calldate,last assigned slot no
	 */
	public void getAllHolidayList(Map inputMap) throws Exception {

		List<TrnPnsnpmntSchedular> lLstLastAssignedSlot = null;
		Calendar lObjCalendar = null;
		Date lDtCurrent = null;
		Date lDtAf3Year = null;
		TrnPnsnpmntSchedular lObjTrnPnsnpmntSchedular = null;
		List<Date> lLstHolidays = new ArrayList<Date>();
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		Integer lIntNextSlot = null;
		try {
			/*
			 * For getting last assigned slot details
			 */
			lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
			lLstLastAssignedSlot = new ArrayList<TrnPnsnpmntSchedular>();

			lLstLastAssignedSlot = lObjIdentificationSchedularDAO.getLastAssignedSlotDetail(gLngLocId);

			/*
			 * If TrnPnsnpmntSchedular has any entry then getting last slot
			 * date(lDtCurrent) and slot No,else current date as slot
			 * date(lDtCurrent) and 1 as slot no
			 */
			lObjCalendar = new GregorianCalendar();
			if (lLstLastAssignedSlot != null && lLstLastAssignedSlot.size() > 0) {
				lObjTrnPnsnpmntSchedular = new TrnPnsnpmntSchedular();
				lObjTrnPnsnpmntSchedular = lLstLastAssignedSlot.get(0);
				lDtCurrent = new Date(lObjTrnPnsnpmntSchedular.getCallDate().getTime());
				lIntNextSlot = lObjTrnPnsnpmntSchedular.getSlotNo().intValue();
				lObjCalendar.setTime(lDtCurrent);
				// -----If last assigned date is before current date than
				// assigning current date as next date and slot as 1.

				if ((lDtCurrent.compareTo(new Date()) < 0)) {
					lDtCurrent = new Date();
					lObjCalendar.setTime(lDtCurrent);
					lObjCalendar.add(Calendar.DATE, 1); // buffer days from
					// current date to
					// start the
					// schedule
					lDtCurrent = lObjCalendar.getTime();
					lIntNextSlot = 0;
				}

			} else {
				lDtCurrent = new Date();
				lObjCalendar.setTime(lDtCurrent);
				lObjCalendar.add(Calendar.DATE, 1); // buffer days from current
				// date to start the
				// schedule
				lDtCurrent = lObjCalendar.getTime();
				lIntNextSlot = 0;
			}
			lObjCalendar.add(Calendar.YEAR, 2); // getting date after 2
			// years.
			lObjCalendar.set(Calendar.MONTH, 11);
			lObjCalendar.set(Calendar.DATE, 31);
			lDtAf3Year = lObjCalendar.getTime();

			/*
			 * For getting list of public holidays from CmnHolidayMst for next 2
			 * years
			 */
			CmnHolidayMstDAO lObjCmnHolidayMstDAO = new CmnHolidayMstDAOImpl(CmnHolidayMst.class, servLoc.getSessionFactory());

			lLstHolidays = lObjCmnHolidayMstDAO.getHolidayList(lDtCurrent, lDtAf3Year);

			CommonFunctionsService lObjCommonFunctionsService = new CommonFunctionsServiceImpl();

			/*
			 * Making list of holidays including alternate Saturdays and all
			 * Sundays for next 2 years
			 */
			lLstHolidays = lObjCommonFunctionsService.getWeekendHolidayList(lDtCurrent, lLstHolidays);
			inputMap.put("lDtCurrent", lDtCurrent);
			inputMap.put("lLstHolidays", lLstHolidays);
			inputMap.put("lIntNextSlot", lIntNextSlot);
		} catch (Exception e) {
			gLogger.error("Error in getAllHolidayList :" + e);
			throw e;
		}
	}

	public void getValidDateTime(List<Date> argLstHolidays, Date argNextDt, Integer argIntNextSlot, Object[] arglArrSlotNos, Map inputMap) {

		boolean isHoliday;
		Calendar lObjCalendar = null;
		if (argIntNextSlot.equals(arglArrSlotNos[(arglArrSlotNos.length - 1)])) {
			argIntNextSlot = 1;
			// Getting next valid date
			isHoliday = false;
			lObjCalendar = new GregorianCalendar();
			lObjCalendar.setTime(argNextDt);
			lObjCalendar.add(Calendar.DATE, 1);
			argNextDt = lObjCalendar.getTime();
			isHoliday = argLstHolidays.contains(argNextDt);
			while (isHoliday) {
				lObjCalendar.add(Calendar.DATE, 1);
				argNextDt = lObjCalendar.getTime();
				isHoliday = argLstHolidays.contains(argNextDt);
				if (isHoliday == false) {
					break;
				}
			}
		} else {
			argIntNextSlot = argIntNextSlot + 1;
		}
		inputMap.put("lDtNextValid", argNextDt);
		inputMap.put("lIntValidSlot", argIntNextSlot);

	}

	public ResultObject validateCallDate(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrCallDate = null;
		Date lDtCallDate = null;
		Date lDtLastCallDate = null;
		Integer lIntCurrSlot = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<Date> lLstHolidays = new ArrayList<Date>();
		Date lDtCurr = new Date();
		String lStrAlert = null;
		Calendar lObjCalendar = null;
		List<Integer> lLstSlotNo = null;
		List<String> lLstSlotTime = null;
		Map<Integer, MstPnsnpmntSlots> lMapSlots = null;
		StringBuilder lSBResp = null;
		try {
			setSessionInfo(inputMap);
			lStrCallDate = StringUtility.getParameter("callDate", request);
			if (lStrCallDate != null && !lStrCallDate.equals("")) {
				lDtCallDate = lObjSimpleDateFormat.parse(lStrCallDate);
				getAllHolidayList(inputMap);
				lLstHolidays = (List<Date>) inputMap.get("lLstHolidays");
				lDtLastCallDate = (Date) inputMap.get("lDtCurrent");
				lObjCalendar = new GregorianCalendar();
				lObjCalendar.setTime(lDtLastCallDate);
				lObjCalendar.add(Calendar.DATE, -1);
				lIntCurrSlot = (Integer) inputMap.get("lIntNextSlot");
				MstPnsnpmntSlotsDAO lObjMstPnsnpmntSlotsDAO = new MstPnsnpmntSlotsDAOImpl(MstPnsnpmntSlots.class, servLoc.getSessionFactory());
				lMapSlots = new HashMap<Integer, MstPnsnpmntSlots>();
				lMapSlots = lObjMstPnsnpmntSlotsDAO.getMaxSlotNumber();
				Object[] lArrSlotNos = lMapSlots.keySet().toArray();

				// if (lDtCallDate.before(lDtLastCallDate)) {
				// lStrAlert = "Please select call date after " +
				// lObjSimpleDateFormat.format(lObjCalendar.getTime());
				// if ((lIntCurrSlot.equals(lArrSlotNos[(lArrSlotNos.length -
				// 1)]))) {
				// lObjCalendar.add(Calendar.DATE, 1);
				// lStrAlert = "Please select call date after " +
				// lObjSimpleDateFormat.format(lObjCalendar.getTime());
				// }
				// } else if (lDtCallDate.equals(lDtLastCallDate) &&
				// (lIntCurrSlot.equals(lArrSlotNos[(lArrSlotNos.length - 1)])))
				// {
				// lObjCalendar.add(Calendar.DATE, 1);
				// lStrAlert = "Please select call date after " +
				// lObjSimpleDateFormat.format(lObjCalendar.getTime());
				// }
				if (lLstHolidays.contains(lDtCallDate)) {
					lStrAlert = "This date is not available for schedule.Please select another date";
				} else {
					/*
					 * If call date is on or after last assigned date.
					 */

					/*
					 * If call date is same as last assigned call date then
					 * fetching no of available slots for that day. Else putting
					 * all slots configured in Mst_pnsn_pmnt_slots table.
					 */
					lLstSlotNo = new ArrayList<Integer>();
					lLstSlotTime = new ArrayList<String>();
					if (lDtLastCallDate.compareTo(lDtCallDate) == 0) {
						lIntCurrSlot = (Integer) inputMap.get("lIntNextSlot");
						lIntCurrSlot = lIntCurrSlot + 1;
						for (int slotIndx = lIntCurrSlot; slotIndx <= (Integer) lArrSlotNos[(lArrSlotNos.length - 1)]; slotIndx++) {
							lLstSlotNo.add(lMapSlots.get(slotIndx).getSlotNo());
							String lStrSlotTime = lMapSlots.get(slotIndx).getSlotTime();
							lStrSlotTime = lStrSlotTime.replaceAll(" ", "");
							lLstSlotTime.add(lStrSlotTime);
						}
					} else {
						lIntCurrSlot = 1;
						for (int slotIndx = lIntCurrSlot; slotIndx <= (Integer) lArrSlotNos[(lArrSlotNos.length - 1)]; slotIndx++) {
							lLstSlotNo.add(lMapSlots.get(slotIndx).getSlotNo());
							String lStrSlotTime = lMapSlots.get(slotIndx).getSlotTime();
							lStrSlotTime = lStrSlotTime.replaceAll(" ", "");
							lLstSlotTime.add(lStrSlotTime);
						}
					}

				}
				lSBResp = getResponseXMLDocForValidateSlot(lStrAlert, lLstSlotNo, lLstSlotTime);
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
			}
		} catch (Exception e) {
			gLogger.error("Error in validateCallDate :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;

	}

	public StringBuilder getResponseXMLDocForValidateSlot(String argStrAlert, List<Integer> argLstSlotNo, List<String> argLstSlotTime) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		if (argStrAlert != null) {
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append(argStrAlert);
			lStrBldXML.append("</MESSAGE>");
		}
		if (argLstSlotNo != null && argLstSlotTime != null) {
			for (Integer lIntSlotNo : argLstSlotNo) {
				lStrBldXML.append("<SLOTNO>");
				lStrBldXML.append(lIntSlotNo);
				lStrBldXML.append("</SLOTNO>");
			}
			for (String lStrSlotTime : argLstSlotTime) {
				lStrBldXML.append("<SLOTTIME>");
				lStrBldXML.append(lStrSlotTime);
				lStrBldXML.append("</SLOTTIME>");
			}
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;

	}

	public ResultObject saveReschedule(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrCallDate = null;
		Date lDtCallDate = null;
		String lStrSlotNo = null;
		String lStrCallTime = null;
		String lStrPnsrCode = null;
		String lStrReSchCallDate = null; // used to show confirmation message
		String lStrReSchCallTime = null; // used to show confirmation message
		String lStrScheduleStatus = null;
		List lLstTrnPnsnpmntSchedularObj = null;
		TrnPnsnpmntSchedular lObjTrnPnsnpmntSchedular = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Map<Integer, MstPnsnpmntSlots> lMapSlots = null;
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		StringBuilder lSBResp = null;
		try {
			setSessionInfo(inputMap);
			lStrCallDate = StringUtility.getParameter("callDate", request);
			lStrSlotNo = StringUtility.getParameter("slotNo", request);
			lStrPnsrCode = StringUtility.getParameter("pnsrCode", request);
			if (lStrPnsrCode != null && !lStrPnsrCode.equals("")) {
				lLstTrnPnsnpmntSchedularObj = new ArrayList();
				lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
				lLstTrnPnsnpmntSchedularObj = lObjIdentificationSchedularDAO.getSchedularObjFromPsnrCode(Long.valueOf(lStrPnsrCode), gLngLocId);
				if (lLstTrnPnsnpmntSchedularObj != null && lLstTrnPnsnpmntSchedularObj.size() > 0) {
					lObjTrnPnsnpmntSchedular = (TrnPnsnpmntSchedular) lLstTrnPnsnpmntSchedularObj.get(0);
					if (lStrCallDate != null && !lStrCallDate.equals("")) {
						lStrReSchCallDate = lStrCallDate;
						lDtCallDate = lObjSimpleDateFormat.parse(lStrCallDate);
						lObjTrnPnsnpmntSchedular.setCallDate(lDtCallDate);
					}
					if (lStrSlotNo != null && !lStrSlotNo.equals("")) {
						MstPnsnpmntSlotsDAO lObjMstPnsnpmntSlotsDAO = new MstPnsnpmntSlotsDAOImpl(MstPnsnpmntSlots.class, servLoc.getSessionFactory());
						lMapSlots = new HashMap<Integer, MstPnsnpmntSlots>();
						lMapSlots = lObjMstPnsnpmntSlotsDAO.getMaxSlotNumber();
						lStrCallTime = lMapSlots.get(Integer.valueOf(lStrSlotNo)).getSlotTime();
						lStrReSchCallTime = lStrCallTime;
						lObjTrnPnsnpmntSchedular.setSlotNo(Long.valueOf(lStrSlotNo));
						lObjTrnPnsnpmntSchedular.setSlotTime(lStrCallTime);
						lStrScheduleStatus = gObjRsrcBndle.getString("IDENT.AWAITED");
					}
					lObjTrnPnsnpmntSchedular.setScheduleStatus(gObjRsrcBndle.getString("IDENT.AWAITED"));
					lObjTrnPnsnpmntSchedular.setUpdatedDate(gDtCurDate);
					lObjTrnPnsnpmntSchedular.setUpdatedPostId(gLngPostId);
					lObjTrnPnsnpmntSchedular.setUpdatedUserId(gLngUserId);
					lObjIdentificationSchedularDAO.update(lObjTrnPnsnpmntSchedular);
				}
			}
			lSBResp = getResponseXMLDocForReschedular(lStrReSchCallDate, lStrReSchCallTime, lStrSlotNo, lStrScheduleStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error in validateCallDate :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public StringBuilder getResponseXMLDocForReschedular(String argStrReSchCallDate, String argStrReSchCallTime, String argStrSlotNo, String argStrScheduleStatus) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		if (argStrReSchCallDate != null && argStrReSchCallTime != null) {
			lStrBldXML.append("<CALLDATE>");
			lStrBldXML.append(argStrReSchCallDate);
			lStrBldXML.append("</CALLDATE>");
			lStrBldXML.append("<CALLTIME>");
			lStrBldXML.append(argStrReSchCallTime);
			lStrBldXML.append("</CALLTIME>");
			lStrBldXML.append("<SLOTNO>");
			lStrBldXML.append(argStrSlotNo);
			lStrBldXML.append("</SLOTNO>");
			lStrBldXML.append("<STATUS>");
			lStrBldXML.append(argStrScheduleStatus);
			lStrBldXML.append("</STATUS>");
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject approveSchedule(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		StringBuilder lSBResp = null;
		String lStrPnsrCode = null;
		String lStrBrnchCode = null;
		String lStrBankName = null;
		String lStrBrnchName = null;
		String lStrAcNo = null;
		String lStrPnsnRqstId = null;
		Boolean lBoolvalidateFlag = true;
		String lStremptyField = null;
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		List lLstTrnPnsnpmntSchedularObj = null;
		TrnPnsnpmntSchedular lObjTrnPnsnpmntSchedular = null;
		MstPensionerDtlsDAO lObjMstPensionerDtlsDAO = null;
		MstPensionerDtls lObjMstPensionerDtls = null;
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		Long lRltPensioncaseBillId = null;
		RltPensioncaseBill lObjRltPensioncaseBill = null;
		RltPensioncaseBillDAO lObjBillCasDao = null;
		Date lDtCurDate = new Date();
		Timestamp lTSCurrent = new Timestamp(lDtCurDate.getTime());

		try {
			setSessionInfo(inputMap);
			lStrPnsrCode = StringUtility.getParameter("pnsrCode", request);
			lStrBrnchCode = StringUtility.getParameter("branchCode", request);
			lStrBankName = StringUtility.getParameter("bankName", request);
			lStrBrnchName = StringUtility.getParameter("branchName", request);
			lStrAcNo = StringUtility.getParameter("acNo", request);
			lStrPnsnRqstId = StringUtility.getParameter("pnsnRqstId", request);
			String[] lArrPnsnRqstId = lStrPnsnRqstId.split("~"); // this should
			// have only
			// one
			// element.
			if (lBoolvalidateFlag && "".equals(lStrPnsrCode)) {
				lBoolvalidateFlag = false;
				lStremptyField = "Pensioner Code";
			}
			if (lBoolvalidateFlag && "".equals(lStrBrnchCode)) {
				lBoolvalidateFlag = false;
				lStremptyField = "Bank Branch Code";
			}
			if (lBoolvalidateFlag && "".equals(lStrBankName)) {
				lBoolvalidateFlag = false;
				lStremptyField = "Bank Name";
			}
			if (lBoolvalidateFlag && "".equals(lStrBrnchName)) {
				lBoolvalidateFlag = false;
				lStremptyField = "Bank Branch Name";
			}
			if (lBoolvalidateFlag && "".equals(lStrAcNo)) {
				lBoolvalidateFlag = false;
				lStremptyField = "Bank Account No.";
			}
			if (lBoolvalidateFlag) {
				if (!"".equals(lStrPnsrCode)) {
					Long lLngPnsrCode = Long.valueOf(lStrPnsrCode);

					/*
					 * Updating scheduleStatus to identified
					 */
					lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
					lLstTrnPnsnpmntSchedularObj = lObjIdentificationSchedularDAO.getSchedularObjFromPsnrCode(Long.valueOf(lStrPnsrCode), gLngLocId);
					if (lLstTrnPnsnpmntSchedularObj != null && lLstTrnPnsnpmntSchedularObj.size() > 0) {
						lObjTrnPnsnpmntSchedular = (TrnPnsnpmntSchedular) lLstTrnPnsnpmntSchedularObj.get(0);
						lObjTrnPnsnpmntSchedular.setScheduleStatus(gObjRsrcBndle.getString("IDENT.IDENTIFIED"));
						lObjTrnPnsnpmntSchedular.setUpdatedDate(gDtCurDate);
						lObjTrnPnsnpmntSchedular.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnpmntSchedular.setUpdatedUserId(gLngUserId);
						lObjIdentificationSchedularDAO.update(lObjTrnPnsnpmntSchedular);
					}

					/*
					 * Updating identification flag to Y and inserting
					 * identification date and time.
					 */
					lObjMstPensionerDtlsDAO = new MstPensionerDtlsDAOImpl(MstPensionerDtls.class, servLoc.getSessionFactory());
					lObjMstPensionerDtls = lObjMstPensionerDtlsDAO.getMstPensionerDtls(lStrPnsrCode);
					lObjMstPensionerDtls.setIdentificationFlag("Y");
					lObjMstPensionerDtls.setIdentificationDate(lTSCurrent);
					lObjMstPensionerDtls.setUpdatedDate(gDtCurDate);
					lObjMstPensionerDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionerDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjMstPensionerDtlsDAO.update(lObjMstPensionerDtls);

					/*
					 * Updating Case Status to Identified in
					 * Trn_pension_rqst_hdr
					 */
					lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, servLoc.getSessionFactory());
					lObjTrnPensionRqstHdr = lObjTrnPensionRqstHdrDAO.read(Long.valueOf(lArrPnsnRqstId[0]));
					lObjTrnPensionRqstHdr.setCaseStatus(gObjRsrcBndle.getString("STATFLG.IDENTIFIED"));
					lObjTrnPensionRqstHdr.setUpdatedDate(gDtCurDate);
					lObjTrnPensionRqstHdr.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionRqstHdr.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);

					/*
					 * Creating Entries of CVP,DCRG,PENSION bills in
					 * rlt_pensioncase_bill
					 */
					lObjBillCasDao = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, servLoc.getSessionFactory());
					lObjRltPensioncaseBill = new RltPensioncaseBill();
					lRltPensioncaseBillId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pensioncase_bill", inputMap);
					lObjRltPensioncaseBill.setRltPensioncaseBillId(lRltPensioncaseBillId);
					lObjRltPensioncaseBill.setPpoNo(lObjTrnPensionRqstHdr.getPpoNo());
					lObjRltPensioncaseBill.setBillType("CVP");
					lObjRltPensioncaseBill.setStatus("N");
					lObjRltPensioncaseBill.setCreatedDate(gDtCurDate);
					lObjRltPensioncaseBill.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjRltPensioncaseBill.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjRltPensioncaseBill.setLocationCode(gLngLocId.toString());
					lObjBillCasDao.create(lObjRltPensioncaseBill);

					lObjRltPensioncaseBill = new RltPensioncaseBill();
					lRltPensioncaseBillId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pensioncase_bill", inputMap);
					lObjRltPensioncaseBill.setRltPensioncaseBillId(lRltPensioncaseBillId);
					lObjRltPensioncaseBill.setPpoNo(lObjTrnPensionRqstHdr.getPpoNo());
					lObjRltPensioncaseBill.setBillType("DCRG");
					lObjRltPensioncaseBill.setStatus("N");
					lObjRltPensioncaseBill.setCreatedDate(gDtCurDate);
					lObjRltPensioncaseBill.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjRltPensioncaseBill.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjRltPensioncaseBill.setLocationCode(gLngLocId.toString());
					lObjBillCasDao.create(lObjRltPensioncaseBill);

					lObjRltPensioncaseBill = new RltPensioncaseBill();
					lRltPensioncaseBillId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pensioncase_bill", inputMap);
					lObjRltPensioncaseBill.setRltPensioncaseBillId(lRltPensioncaseBillId);
					lObjRltPensioncaseBill.setPpoNo(lObjTrnPensionRqstHdr.getPpoNo());
					lObjRltPensioncaseBill.setBillType("PENSION");
					lObjRltPensioncaseBill.setStatus("N");
					lObjRltPensioncaseBill.setCreatedDate(gDtCurDate);
					lObjRltPensioncaseBill.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjRltPensioncaseBill.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjRltPensioncaseBill.setLocationCode(gLngLocId.toString());
					lObjBillCasDao.create(lObjRltPensioncaseBill);

				}

			}
			lSBResp = getResponseXMLDocForApprove(lStremptyField);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error in validateCallDate :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public StringBuilder getResponseXMLDocForApprove(String argStremptyField) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		if (argStremptyField != null) {
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append(argStremptyField);
			lStrBldXML.append("</MESSAGE>");
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject generateIdentificationLetter(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSdf1 = new SimpleDateFormat("h:m");
		List<Object[]> lLstIdentLetterDtl = new ArrayList<Object[]>();
		List lLstSelectedCheckList = null;
		List lLstDistrict = null;
		List lLstState = null;
		StringBuffer lSbDisplayString = new StringBuffer();
		StringBuffer lSbPrintString = new StringBuffer();
		String lStrNewLine = StringUtils.getLineSeparator();
		String lStrAtoName = null;
		String lStrPnsrCode = null;
		String[] lArrPnsrCode = null;
		Long lLngSerialNo = null;
		String lStrPpoNo = null;
		String lStrName = null;
		Date lDtIdentificationDate = null;
		String lStrStateName = "";
		String lStrDistrictName = "";
		//String lStrPnsnrAddrBuilding = "";
		//String lStrPnsnrAddrStreet = null;
		//String lStrPnsnrAddrLandMark = null;
		//String lStrPnsrAddrLocality = null;
		String lStrPnsnrAddr1 = "";
		String lStrPnsnrAddr2 = "";
		String lStrPnsnrAddrTown = "";
		String lStrFlagVal = null;
		String lStrTresName = null;
		String lStrOfficeShortName = null;
		String lStrTresAddr1 = null;
		String lStrTresAddr2 = null;
		//String lStrAudiName = "";
		String lStrAuthorityName = "";
		String lStrSlotTime = null;
		String lStrCallTime = null;
		Date lDtCallTime = null;
		String lStrAmPm = "";
		Date lDtDOD = null;
		Map<String, String> lMapFamilyDtls = null;
		Map<String, List<String>> lMapCheckLstDtls = null;
		Map<String,String> lMapDistrict_Eng = new HashMap<String, String>();
		Map<String,String> lMapDistrict_Ma = new HashMap<String, String>();
		Map<String,String> lMapState_Eng = new HashMap<String, String>();
		Map<String,String> lMapState_Ma = new HashMap<String, String>();
		Map<String,String> lMapTemp = new HashMap<String, String>();
		Map<Integer,Map<String,String>> lMapDistrict_Final = new HashMap<Integer, Map<String,String>>();
		Map<Integer,Map<String,String>> lMapState_Final = new HashMap<Integer, Map<String,String>>();
		List<String> lLstDocDesc = null;
		int maxLength = 75;
		String lStrLetter = "";
		TrnFinYearLetterNo lObjTrnFinYearLetterNo = null;
		Long lLngTrnFinYearLetterNoId = null;
		Long lLngLetterNo = null;
		Integer lIntFinYearId = null;
		Object []lObj = null;
		
		try {
			setSessionInfo(inputMap);
			lStrFlagVal = StringUtility.getParameter("gFlag", request);

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(servLoc.getSessionFactory());
			IdentificationSchedularDAO lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, servLoc.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAOImpl = new FinancialYearDAOImpl(SgvcFinYearMst.class, servLoc.getSessionFactory());
			
			// lStrPnsrCode = StringUtility.getParameter("hdnSelectedPnsrId",
			// request);
			lStrPnsrCode = request.getParameter("hdnSelectedPnsrId");
			if (lStrPnsrCode != null && !lStrPnsrCode.equals("")) {
				lArrPnsrCode = lStrPnsrCode.split("~");
			}
			
			lLstDistrict = lObjIdentificationSchedularDAO.getAllDistrictName("15");
			lLstState = lObjIdentificationSchedularDAO.getAllStateName();
			
			for(int lIntCnt = 0;lIntCnt < lLstDistrict.size();lIntCnt++)
			{
				lObj = (Object[]) lLstDistrict.get(lIntCnt);
				if(lObj[2].toString().equals("1")){
					lMapDistrict_Eng.put(lObj[1].toString(), lObj[0].toString());
				}else if(lObj[2].toString().equals("2")){
					lMapDistrict_Ma.put(lObj[1].toString(), lObj[0].toString());
				}
			}
			lMapDistrict_Final.put(1, lMapDistrict_Eng);
			lMapDistrict_Final.put(2, lMapDistrict_Ma);
			
			for(int lIntCnt = 0;lIntCnt < lLstState.size();lIntCnt++)
			{
				lObj = (Object[]) lLstState.get(lIntCnt);
				if(lObj[2].toString().equals("1")){
					lMapState_Eng.put(lObj[1].toString(), lObj[0].toString());
				}else if(lObj[2].toString().equals("2")){
					lMapState_Ma.put(lObj[1].toString(), lObj[0].toString());
				}
			}
			lMapState_Final.put(1, lMapState_Eng);
			lMapState_Final.put(2, lMapState_Ma);
			
			
			lLstIdentLetterDtl = lObjIdentificationSchedularDAO.getIdentificationLetterDtls(lArrPnsrCode, gLngLocId.toString());
			lMapFamilyDtls = lObjIdentificationSchedularDAO.getValidFamilyMemberName(lArrPnsrCode);
			lMapCheckLstDtls = lObjIdentificationSchedularDAO.getSelectedDocsName(lArrPnsrCode);
			lStrAtoName = lObjCommonPensionDAO.getAtoNameFromLocationCode(gLngLocId.toString());
			if (lLstIdentLetterDtl != null && lLstIdentLetterDtl.size() > 0) {
				inputMap.put("lLstIdentLetterDtl", lLstIdentLetterDtl);
			}
			String lPnsrCode = null;
			lSbPrintString.append("<div><pre>");
			String lStrSrNo = "";
			String lStrLetterType = "Identification";
			for (Object[] lArrIdentLetter : lLstIdentLetterDtl) {
				lStrSrNo = "";
				//lStrPnsnrAddrBuilding = "";
				lPnsrCode = (lArrIdentLetter[14] != null) ? lArrIdentLetter[14].toString() : "";
				lStrPpoNo = (lArrIdentLetter[0] != null) ? lArrIdentLetter[0].toString() : "";
				lLngSerialNo = lObjIdentificationSchedularDAO.getLetterNo(lPnsrCode, gLngLocId.intValue(),lStrLetterType);
				lIntFinYearId = lObjFinancialYearDAOImpl.getFinYearIdByCurDate();
				/*if(lLngSerialNo == null){
					lObjTrnFinYearLetterNo = new TrnFinYearLetterNo();										
					lLngTrnFinYearLetterNoId = IFMSCommonServiceImpl.getNextSeqNum("TRN_FINYEAR_LETTER_NO", inputMap);
					lObjTrnFinYearLetterNo.setTrnFinYearLetterNoId(lLngTrnFinYearLetterNoId);
					lObjTrnFinYearLetterNo.setPensionerCode(lPnsrCode);
					lObjTrnFinYearLetterNo.setPpoNo(lStrPpoNo);
					lObjTrnFinYearLetterNo.setLetterType(lStrLetterType);
					lLngLetterNo = lObjCommonPensionDAO.getMaxLetterNo(gLngLocId.intValue(),lIntFinYearId,lStrLetterType);
					lObjTrnFinYearLetterNo.setLetterNo(Long.parseLong(String.format("%05d", lLngLetterNo)));
					lObjTrnFinYearLetterNo.setLocationCode(gLngLocId.intValue());
					lObjTrnFinYearLetterNo.setFinYearId(lIntFinYearId);
					lObjTrnFinYearLetterNo.setCreatedDate(gDtCurDate);
					lObjTrnFinYearLetterNo.setCreatedPostId(gLngPostId);
					lObjTrnFinYearLetterNo.setCreatedUserId(gLngUserId);
					lObjIdentificationSchedularDAO.create(lObjTrnFinYearLetterNo);
					
					lLngSerialNo = lLngLetterNo;
				}*/
				lStrAuthorityName = lObjIdentificationSchedularDAO.getAuthorityName(gLngUserId);
				// ---lArrIdentLetter[15] contains pensioner's date of death
				// ..if pensioner is dead then get family member's name
				if (lArrIdentLetter[15] != null) {
					lStrName = (lMapFamilyDtls.get(lPnsrCode) != null) ? lMapFamilyDtls.get(lPnsrCode) : "";
				} else {
					lStrName = (lArrIdentLetter[1] != null) ? lArrIdentLetter[1].toString() : "";
				}
				
				lDtIdentificationDate = (lArrIdentLetter[2] != null) ? (Date) lArrIdentLetter[2] : null;
				
				if(lArrIdentLetter[16] != null || lArrIdentLetter[17] != null || lArrIdentLetter[18] != null){
					lStrPnsnrAddr1 = (lArrIdentLetter[16] != null && !lArrIdentLetter[16].equals("")) ? lArrIdentLetter[16].toString() + "," : "";
					lStrPnsnrAddr2 = (lArrIdentLetter[17] != null && !lArrIdentLetter[17].equals("")) ? lArrIdentLetter[17].toString() + "," : "";
					lStrPnsnrAddrTown = (lArrIdentLetter[18] != null && !lArrIdentLetter[18].equals("")) ? lArrIdentLetter[18].toString() + "," : "";
					lMapTemp = lMapState_Final.get(2);
					lStrStateName = lMapTemp.get(lArrIdentLetter[19].toString());
					lMapTemp = lMapDistrict_Final.get(2);
					lStrDistrictName = lMapTemp.get(lArrIdentLetter[20].toString());
				}else{
					lStrPnsnrAddr1 = (lArrIdentLetter[5] != null && !lArrIdentLetter[5].equals("")) ? lArrIdentLetter[5].toString() + "," : "";
					lStrPnsnrAddr2 = (lArrIdentLetter[6] != null && !lArrIdentLetter[6].equals("")) ? lArrIdentLetter[6].toString() + "," : "";
					lStrPnsnrAddrTown = (lArrIdentLetter[7] != null && !lArrIdentLetter[7].equals("")) ? lArrIdentLetter[7].toString() + "," : "";
					//lStrPnsrAddrLocality = (lArrIdentLetter[8] != null && !lArrIdentLetter[8].equals("")) ? lArrIdentLetter[8].toString() + "," : "";
					lMapTemp = lMapState_Final.get(1);
					lStrStateName = lMapTemp.get(lArrIdentLetter[19].toString());
					lMapTemp = lMapDistrict_Final.get(1);
					lStrDistrictName = lMapTemp.get(lArrIdentLetter[20].toString());
				}
				lStrTresName = (lArrIdentLetter[9] != null) ? lArrIdentLetter[9].toString() : "";
				lStrOfficeShortName = (lArrIdentLetter[10] != null) ? lArrIdentLetter[10].toString() : "";
				lStrTresAddr1 = (lArrIdentLetter[11] != null) ? lArrIdentLetter[11].toString() : "";
				lStrTresAddr2 = (lArrIdentLetter[12] != null) ? lArrIdentLetter[12].toString() : "";
				// lStrAudiName = (lArrIdentLetter[13] != null) ?
				// lArrIdentLetter[13].toString() : "";
				lStrSlotTime = (lArrIdentLetter[13] != null) ? lArrIdentLetter[13].toString() : "";
				lStrCallTime = (!lStrSlotTime.equals("")) ? lStrSlotTime.substring(0, 5) : "";
				if (!lStrCallTime.equals("")) {
					lDtCallTime = lObjSdf1.parse(lStrCallTime);
					lStrAmPm = (lDtCallTime.after(lObjSdf1.parse("08:00")) && lDtCallTime.before(lObjSdf1.parse("11:59"))) ? "am" : "pm";
				}

				/*
				 * Display String Starts
				 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				 */
				lSbDisplayString.append("\n");
				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					// lSbDisplayString.append(space(58) +
					// gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR15"));
					lSbDisplayString.append(getCenterAlign(lStrTresName, maxLength) + "\n"); // treasury
																								// name
					lSbDisplayString.append(getCenterAlign(lStrTresAddr1, maxLength) + "\n");
					lSbDisplayString.append(getCenterAlign(lStrTresAddr2, maxLength) + "\n");

				} else {
					lSbDisplayString.append(getCenterAlign(lStrTresName, maxLength) + "\n"); // treasury
					// name
					lSbDisplayString.append(getCenterAlign(lStrTresAddr1, maxLength) + "\n");
					lSbDisplayString.append(getCenterAlign(lStrTresAddr2, maxLength) + "\n");
				}
				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					// lSbDisplayString.append("\n");
					lStrSrNo = space(40) +gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR1") + space(1);
					if(lLngSerialNo != null){
						lStrSrNo += String.format("%05d", lLngSerialNo);
					}						
					lSbDisplayString.append(getRightAlign(lStrSrNo, maxLength));
					lSbDisplayString.append("\n");
					//int prevLinwLeftSpace = maxLength - lStrSrNo.length();
					lSbDisplayString.append(space(40) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR26")+ "\n");
					lSbDisplayString.append(space(40) + lStrTresName + ",\n");
					if(!lStrTresAddr1.equals("")){
						lSbDisplayString.append(space(40) + lStrTresAddr1 + ",\n");
					}
					if(!lStrTresAddr2.equals("")){
						lSbDisplayString.append(space(40) + lStrTresAddr2 + ",\n");
					}
					lSbDisplayString.append(space(40) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR2") + space(1) + lObjSimpleDateFormat.format(gDtCurDate) + "\n");
					lSbDisplayString.append(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR3"));
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(3) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR4") + space(1) + lStrName);
					lSbDisplayString.append("\n");
				} else {					
					lSbDisplayString.append(getRightAlign(String.format("%05d", lLngSerialNo), maxLength));
					lSbDisplayString.append("\n");
					int prevLinwLeftSpace = maxLength - lLngSerialNo.toString().length();
					lSbDisplayString.append(space(prevLinwLeftSpace) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR2") + space(1) + lObjSimpleDateFormat.format(gDtCurDate) + "\n");
					lSbDisplayString.append("\n\n\n");
					lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR1"));
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(3) + lStrName);
					lSbDisplayString.append("\n");
				}

				if (!lStrPnsnrAddr1.equals("")) {
					lSbDisplayString.append(space(3) + lStrPnsnrAddr1);
					lSbDisplayString.append("\n");
				}
				if (!"".equals(lStrPnsnrAddr2)){ 
					lSbDisplayString.append(space(3) + lStrPnsnrAddr2);
					lSbDisplayString.append("\n");
				}
				if(!"".equals(lStrPnsnrAddrTown)) {
					lSbDisplayString.append(space(3) + lStrPnsnrAddrTown);
					lSbDisplayString.append("\n");
				}
				/*if (!"".equals(lStrPnsrAddrLocality)) {
					lSbDisplayString.append(space(3) + lStrPnsrAddrLocality);
					lSbDisplayString.append("\n");
				}*/
				if (!"".equals(lStrDistrictName) || !"".equals(lStrStateName)) {					
					lSbDisplayString.append(space(3) + lStrDistrictName + ", " + lStrStateName + ".");					
					lSbDisplayString.append("\n");
				}

				lSbDisplayString.append("\n\n");
				
				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					lSbDisplayString.append(getCenterAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR5"), maxLength));
					lSbDisplayString.append("\n\n");
					lSbDisplayString.append(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR6"));
					lSbDisplayString.append("\n");
					lStrLetter = space(12) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR7") + space(1) + lObjSimpleDateFormat.format(lDtIdentificationDate) + space(1)
							+ gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR8") + space(1) + lStrCallTime + lStrAmPm + space(1) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR9")
							+ space(1) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR29") + space(1) + lStrAuthorityName + space(1) 
							+ gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR30");
					lSbDisplayString.append(getLeftALign(lStrLetter, maxLength, 0));
					lSbDisplayString.append("\n");
					
					lLstSelectedCheckList = lObjIdentificationSchedularDAO.getSelectedCheckList(lPnsrCode);
					
					if(lLstSelectedCheckList.size() > 0)
					{
						for(Integer lIntCnt=0;lIntCnt < lLstSelectedCheckList.size();lIntCnt++){
							TrnPensionerChecklistDtls lObjTrnPensionerChecklistDtls =  (TrnPensionerChecklistDtls) lLstSelectedCheckList.get(lIntCnt);
							if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("1")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR10"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("2")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR11"), maxLength, 3));								
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("3")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR12"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("4")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR13"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("5")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR14"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("6")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR15"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("7")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR16"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("8")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR17"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("9")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR18"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("10")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR19"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("11")){
								lSbDisplayString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR20"), maxLength, 3));
							}
						}
						lSbDisplayString.append("\n\n");
						lSbDisplayString.append(getLeftALign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR24"), maxLength, 3));
					}
				} else {
					lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR3"));
					lSbDisplayString.append("\n\n");
					lSbDisplayString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR4") + space(1));
					lSbDisplayString.append(space(1) + lObjSimpleDateFormat.format(lDtIdentificationDate) + space(1));
					lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR5") + space(1));
					lSbDisplayString.append(space(1) + lStrCallTime + space(1) + lStrAmPm);
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR6") + space(1) + lStrAuthorityName);
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR7"));
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR8"));
					lSbDisplayString.append("\n\n");
				}
				lLstDocDesc = lMapCheckLstDtls.get(lPnsrCode);
				/*if (lLstDocDesc != null) {
					Integer lIntSrNo = 1;
					for (String lStrDocDesc : lLstDocDesc) {
						lSbDisplayString.append(space(10) + lIntSrNo.toString() + "." + space(1) + lStrDocDesc);
						lSbDisplayString.append("\n");
						lIntSrNo++;
					}
				}*/
				lSbDisplayString.append("\n\n\n\n");
				// lSbDisplayString.append(getRightAlign(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13")
				// + space((50 -
				// gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13").length()) /
				// 2), maxLength));
				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					lSbDisplayString.append(getRightAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR25") + space((50 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR25").length()) / 3)
							+ space(2), maxLength));
					lSbDisplayString.append("\n");
					lSbDisplayString.append(getRightAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR31")+ space((50 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR31").length()) / 3)+ "\n", maxLength));
					lSbDisplayString.append(getRightAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR32")+ space((10 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR32").length()) / 3) + "\n", maxLength));
					lSbDisplayString.append(getRightAlign(lStrAtoName + space((40 - lStrAtoName.length()) / 2), maxLength) + "\n");
					// lSbPrintString.append(getRightAlign(lStrTresName +
					// space((50
					// - lStrTresName.length()) / 2), maxLength));
					lSbDisplayString.append("\n\n\n\n\n");
				} else {
					lSbDisplayString.append("\n\n\n");
					// lSbDisplayString.append(getRightAlign(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13")
					// + space((50 -
					// gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13").length())
					// /
					// 2), maxLength));
					lSbDisplayString.append("\n");
					lSbDisplayString.append(getRightAlign(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR14") + space((50 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR14").length()) / 3)
							+ space(2), maxLength));
					lSbDisplayString.append("\n");
					lSbDisplayString.append(getRightAlign(lStrAtoName + space((50 - lStrAtoName.length()) / 2), maxLength));
					lSbDisplayString.append("\n");
				}
				/*
				 * Display String Ends
				 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				 */

				/*
				 * Print String Starts
				 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				 */

				lSbPrintString.append(lStrNewLine);
				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					lSbPrintString.append(getCenterAlign(lStrTresName, maxLength) + lStrNewLine); // treasury
																									// name
					lSbPrintString.append(getCenterAlign(lStrTresAddr1, maxLength) + lStrNewLine);
					lSbPrintString.append(getCenterAlign(lStrTresAddr2, maxLength) + lStrNewLine);
				} else {
					lSbPrintString.append(space(69) + lStrTresName + space(37));
				}
				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					lSbPrintString.append(lStrNewLine);
					// lSbDisplayString.append("\n");
					lStrSrNo = space(40) +gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR1") + space(1);
					if(lLngSerialNo != null){
						lStrSrNo += String.format("%05d", lLngSerialNo);
					}
					lSbPrintString.append(getRightAlign(lStrSrNo, maxLength));
					lSbPrintString.append(lStrNewLine);
					//int prevLinwLeftSpace = maxLength - lStrSrNo.length();
					lSbPrintString.append(space(40) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR26")+ "\n");
					lSbPrintString.append(space(40) + lStrTresName + ",\n");
					if(!lStrTresAddr1.equals("")){
						lSbPrintString.append(space(40) + lStrTresAddr1 + ",\n");
					}
					if(!lStrTresAddr2.equals("")){
						lSbPrintString.append(space(40) + lStrTresAddr2 + ",\n");
					}
					lSbPrintString.append(space(40) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR2") + space(1) + lObjSimpleDateFormat.format(gDtCurDate) + lStrNewLine);
					lSbPrintString.append(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR3"));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(3) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR4") + space(1) + lStrName);
					lSbPrintString.append(lStrNewLine);
				} else {					
					lSbPrintString.append(getRightAlign(String.format("%05d", lLngSerialNo), maxLength));
					lSbPrintString.append(lStrNewLine);
					int prevLinwLeftSpace = maxLength - lLngSerialNo.toString().length();
					lSbPrintString.append(space(prevLinwLeftSpace) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR2") + space(1) + lObjSimpleDateFormat.format(gDtCurDate) + lStrNewLine);
					lSbPrintString.append(lStrNewLine + lStrNewLine + lStrNewLine);
					lSbPrintString.append(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR1"));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(3) + lStrName);
					lSbPrintString.append(lStrNewLine);
				}

				if (!"".equals(lStrPnsnrAddr1)) {
					lSbPrintString.append(space(3) + lStrPnsnrAddr1);
					lSbPrintString.append(lStrNewLine);
				}
				if (!"".equals(lStrPnsnrAddr2)) {
					lSbPrintString.append(space(3) + lStrPnsnrAddr2 + lStrNewLine);
				}
				if(!"".equals(lStrPnsnrAddrTown)) {
					lSbPrintString.append(space(3) + lStrPnsnrAddrTown + lStrNewLine);
				}
				/*if (!"".equals(lStrPnsrAddrLocality)) {
					lSbPrintString.append(space(3) + lStrPnsrAddrLocality);
					lSbPrintString.append(lStrNewLine);
				}*/
				if (!"".equals(lStrDistrictName) || !"".equals(lStrStateName)) {
					lSbPrintString.append(space(3) + lStrDistrictName + ", " + lStrStateName + ".");
					lSbPrintString.append(lStrNewLine);
				}

				lSbPrintString.append(lStrNewLine + lStrNewLine);
				
				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					lSbPrintString.append(getCenterAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR5"), maxLength));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR6"));
					lSbPrintString.append(lStrNewLine + lStrNewLine);
					lStrLetter = space(12) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR7") + space(1) + lObjSimpleDateFormat.format(lDtIdentificationDate) + space(1)
							+ gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR8") + space(1) + lStrCallTime + lStrAmPm + space(1) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR9")
							+ space(1) + gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR29") + space(1) + lStrAuthorityName + space(1) 
							+ gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR30");
					lSbPrintString.append(getLeftALign(lStrLetter, maxLength, 0));
					lSbPrintString.append(lStrNewLine);
					
					
					if(lLstSelectedCheckList.size() > 0)
					{
						for(Integer lIntCnt=0;lIntCnt < lLstSelectedCheckList.size();lIntCnt++){
							TrnPensionerChecklistDtls lObjTrnPensionerChecklistDtls =  (TrnPensionerChecklistDtls) lLstSelectedCheckList.get(lIntCnt);
							if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("1")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR10"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("2")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR11"), maxLength, 3));								
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("3")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR12"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("4")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR13"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("5")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR14"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("6")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR15"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("7")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR16"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("8")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR17"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("9")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR18"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("10")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR19"), maxLength, 3));
							}
							else if(lObjTrnPensionerChecklistDtls.getDocId().toString().equals("11")){
								lSbPrintString.append(getLeftALign("("+(lIntCnt+1)+")"+space(1)+gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR20"), maxLength, 3));
							}
						}
						lSbPrintString.append(lStrNewLine);
						lSbPrintString.append(lStrNewLine);
						lSbPrintString.append(getLeftALign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR24"), maxLength, 3));
					}
				} else {
					lSbPrintString.append(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR3"));
					lSbPrintString.append(lStrNewLine + lStrNewLine);
					lSbPrintString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR4") + space(1));
					lSbPrintString.append(space(1) + lObjSimpleDateFormat.format(lDtIdentificationDate) + space(1));
					lSbPrintString.append(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR5") + space(1));
					lSbPrintString.append(space(1) + lStrCallTime + space(1) + lStrAmPm);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR6") + space(1) + lStrAuthorityName);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR7"));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(10) + gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR8"));
					lSbPrintString.append(lStrNewLine + lStrNewLine);
				}
				// if (lLstDocDesc != null) {
				// Integer lIntPrintSrNo = 1;
				// for (String lStrDocDesc : lLstDocDesc) {
				// lSbPrintString.append(space(10) + lIntPrintSrNo.toString() +
				// "." + space(1) + lStrDocDesc);
				// lSbPrintString.append(lStrNewLine);
				// lIntPrintSrNo++;
				// }
				// }

				if (!"".equals(lStrFlagVal) && lStrFlagVal.trim().equals("M")) {
					lSbPrintString.append(lStrNewLine + lStrNewLine + lStrNewLine);
					// lSbPrintString.append(getRightAlign(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13")
					// + space((50 -
					// gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13").length())
					// /
					// 2), maxLength));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(getRightAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR25") + space((50 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR25").length()) / 3)
							+ space(2), maxLength));
					lSbPrintString.append("\n");
					lSbPrintString.append(getRightAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR31")+ space((50 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR31").length()) / 3)+ "\n", maxLength));
					lSbPrintString.append(getRightAlign(gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR32")+ space((10 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR32").length()) / 3) + "\n", maxLength));					
					lSbPrintString.append(getRightAlign(lStrAtoName + space((40 - lStrAtoName.length()) / 2), maxLength));
					lSbPrintString.append(lStrNewLine);
				} else {
					lSbPrintString.append(lStrNewLine + lStrNewLine + lStrNewLine);
					// lSbPrintString.append(getRightAlign(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13")
					// + space((50 -
					// gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR13").length())
					// /
					// 2), maxLength));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(getRightAlign(gObjRsrcBndle.getString("PPMT.IDENTLETTERSTR14") + space((50 - gObjRsrcBndleMarathi.getString("PPMT.IDENTLETTERSTR14").length()) / 3)
							+ space(2), maxLength));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(getRightAlign(lStrAtoName + space((50 - lStrAtoName.length()) / 2), maxLength));
					lSbPrintString.append(lStrNewLine);
				}
				lSbPrintString.append((char) 12);
				// lSbPrintString.append(getRightAlign(lStrTresName + space((50
				// - lStrTresName.length()) / 2), maxLength));

				/*
				 * Print String ends
				 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				 * >>>>>>>>>>>>>>>>>>>>>>>>
				 */
			}
			lSbPrintString.append("</pre></div>");
			inputMap.put("DisplayIdentLetterString", lSbDisplayString);
			inputMap.put("PrintIdentLetterString", lSbPrintString);
			resObj.setResultValue(inputMap);
			resObj.setViewName("printIdentLetter");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	public ResultObject loadChecklist(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		List<MstPensionChecklist> lLstAllChecklistVO = null;
		String lStrPnsrCode = null;
		List<Long> lLstSelectedCheckList = null;
		String lStrShowReadOnly = null;
		try {
			setSessionInfo(inputMap);
			lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPensionerChecklistDtls.class, servLoc.getSessionFactory());
			lStrPnsrCode = StringUtility.getParameter("pnsrCode", request);
			lStrShowReadOnly = StringUtility.getParameter("showReadOnly", request);
			lLstAllChecklistVO = lObjIdentificationSchedularDAO.getAllCheckList();
			lLstSelectedCheckList = lObjIdentificationSchedularDAO.getListByColumnAndValue("pensionerCode", lStrPnsrCode);
			inputMap.put("lLstAllChecklistVO", lLstAllChecklistVO);
			inputMap.put("lLstSelectedCheckList", lLstSelectedCheckList);
			inputMap.put("pensionerCode", lStrPnsrCode);
			inputMap.put("lStrShowReadOnly", lStrShowReadOnly);
			resObj.setResultValue(inputMap);
			resObj.setViewName("PnsnChecklist");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject saveChecklist(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		String lStrPnsrCode = null;
		String[] lArrSelectedDocIds = null;
		String lStrSelectedDocIds = null;
		TrnPensionerChecklistDtls lObjTrnPensionerChecklistDtls = null;
		StringBuilder lSBResp = null;
		try {
			setSessionInfo(inputMap);
			lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPensionerChecklistDtls.class, servLoc.getSessionFactory());
			lStrPnsrCode = StringUtility.getParameter("pnsrCode", request).trim();
			lStrSelectedDocIds = StringUtility.getParameter("lStrSelectedDocIds", request).trim();
			lArrSelectedDocIds = lStrSelectedDocIds.split("~");
			// ---Deleting entries of doc checklist of pensioner.
			lObjIdentificationSchedularDAO.deletePnsrCheckList(lStrPnsrCode);

			// ----Inserting selected document ids for pensioner..
			if (!"".equals(lStrSelectedDocIds)) {
				for (int cnt = 0; cnt < lArrSelectedDocIds.length; cnt++) {
					lObjTrnPensionerChecklistDtls = new TrnPensionerChecklistDtls();
					Long lLngPnsrCheckListId = IFMSCommonServiceImpl.getNextSeqNum("trn_pensioner_checklist_dtls", inputMap);
					lObjTrnPensionerChecklistDtls.setPnsnrChecklistId(lLngPnsrCheckListId);
					lObjTrnPensionerChecklistDtls.setPensionerCode(lStrPnsrCode);
					lObjTrnPensionerChecklistDtls.setDocId(Long.valueOf(lArrSelectedDocIds[cnt]));
					lObjTrnPensionerChecklistDtls.setCreatedDate(gDtCurDate);
					lObjTrnPensionerChecklistDtls.setCreatedPostId(gLngPostId);
					lObjTrnPensionerChecklistDtls.setCreatedUserId(gLngUserId);
					lObjTrnPensionerChecklistDtls.setDbId(gLngDBId.intValue());
					lObjIdentificationSchedularDAO.create(lObjTrnPensionerChecklistDtls);
				}
			}
			lSBResp = getResponseXMLDocForSaveChecklist("Success");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			//e.printStackTrace();
			lSBResp = getResponseXMLDocForSaveChecklist("Fail");
			resObj.setResultValue(null);
			resObj.setViewName("errorPage");
		}
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocForSaveChecklist(String lStrMessage) {

		StringBuilder lSB = new StringBuilder();
		lSB.append("<XMLDOC>");
		lSB.append("<MESSAGE>");
		lSB.append(lStrMessage);
		lSB.append("</MESSAGE>");
		lSB.append("</XMLDOC>");
		return lSB;
	}

	private static String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}

	public ResultObject saveArchivedCase(Map inputMap) // :-:-: IT CHANGES ONLY
														// STATAUS OF PPO CASE
														// :-:-:
	{

		gLogger.info("In saveArchivedCase method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		String lStrTotalPnsnr = null;
		String[] lStrArrTotalPnsnr = null;
		String lStrPnsnrCode = null;
		String lSBStatus = null;
		try {
			setSessionInfo(inputMap);
			lStrTotalPnsnr = StringUtility.getParameter("totalPensioner", request);
			lStrArrTotalPnsnr = lStrTotalPnsnr.split("~");
			lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPensionRqstHdr.class, servLoc.getSessionFactory());
			for (int lIntCnt = 0; lIntCnt < lStrArrTotalPnsnr.length; lIntCnt++) {
				lStrPnsnrCode = lStrArrTotalPnsnr[lIntCnt];
				List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjIdentificationSchedularDAO.getListByColumnAndValue("pensionerCode", lStrPnsnrCode.trim());
				if (!lLstTrnPensionRqstHdr.isEmpty()) {
					TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
					lObjTrnPensionRqstHdr.setCaseStatus(gObjRsrcBndle.getString("STATFLG.ARCHIVED"));
				}
				lSBStatus = getResponseXMLDocForArchivedCase("Success").toString();
			}
			resObj.setResultCode(ErrorConstants.SUCCESS);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			lSBStatus = getResponseXMLDocForArchivedCase("Fail").toString();
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocForArchivedCase(String lStrMessage) {

		StringBuilder lSB = new StringBuilder();
		lSB.append("<XMLDOCARCHIVED>");
		lSB.append("<MESSAGE>");
		lSB.append(lStrMessage);
		lSB.append("</MESSAGE>");
		lSB.append("</XMLDOCARCHIVED>");
		return lSB;
	}

	private static String getCenterAlign(String sValue, int length) {

		StringBuffer sb = new StringBuffer(length);
		int nStartPos = (length - sValue.length()) / 2;
		for (int i = 0; i < nStartPos; i++) {
			sb.append(space(1));
		}
		sb.append(sValue);
		for (int i = sb.length(); i < length; i++) {
			sb.append(space(1));
		}
		return sb.toString();
	}

	private static String getRightAlign(String sValue, int length) {

		StringBuffer sb = new StringBuffer(length);
		int nStartPos = length - sValue.length();
		for (int i = 0; i < nStartPos; i++) {
			sb.append(space(1));
		}
		sb.append(sValue);
		return sb.toString();
	}

	private static String getLeftALign(String strValue, int Width, int adjustmentSpaces) {

		List<String> al = new ArrayList<String>();
		StringTokenizer oTokenizer = new StringTokenizer(strValue, " ");
		String LastLine = "";
		StringBuilder lSBOutput = new StringBuilder();
		while (oTokenizer.hasMoreElements()) {
			String strToken = oTokenizer.nextToken();
			if (strToken.length() >= Width) {
				List oSpl = null;
				if (LastLine.equals("")) {
					oSpl = getSplitedString(strToken, Width);
				} else {
					oSpl = getSplitedString(LastLine + space(1) + strToken, Width);
				}
				for (int i = 0; i < oSpl.size() - 1; i++) {
					al.add((String) oSpl.get(i));
				}

				LastLine = oSpl.get(oSpl.size() - 1).toString().trim();
			} else {
				if ((LastLine.length() + strToken.length()) < Width) {
					if (!"".equals(LastLine)) {
						LastLine = LastLine + space(1) + strToken;
					} else {
						LastLine = strToken;
					}

				} else {
					al.add(String.format("%-" + Width + "s", LastLine.trim()));
					LastLine = strToken;
				}
			}
		}
		if (!LastLine.equals("")) {
			al.add(String.format("%-" + Width + "s", LastLine));
		}
		int rowCount = 1;
		for (String lStr : al) {
			if (rowCount > 1) {
				lSBOutput.append(space(adjustmentSpaces) + lStr + "\n");
			} else {
				lSBOutput.append(lStr + "\n");
			}
			rowCount++;
		}
		return lSBOutput.toString();
	}

	private static List getSplitedString(String strValue, int Width) {

		List al = new ArrayList();
		int beginIndex = 0, endIndex = Width;
		strValue = strValue.trim();
		int sLength = strValue.length();
		String sFormat = "%-" + Width + "s";

		if (sLength <= Width) {
			al.add(String.format(sFormat, strValue));
			return al;
		}
		while (endIndex < sLength) {
			String token = strValue.substring(beginIndex, endIndex);
			al.add(String.format(sFormat, token));
			beginIndex = endIndex;
			endIndex = beginIndex + Width;
		}
		if (beginIndex < sLength) {
			al.add(String.format(sFormat, strValue.substring(beginIndex)));
		}

		return al;
	}
}
