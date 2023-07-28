/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 25, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.util.StringUtils;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAO;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAOImpl;
import com.tcs.sgv.web.jsp.tags.DateUtilities;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Aug 25, 2011
 */
public class CVPRestorationLetterServiceImpl extends ServiceImpl implements CVPRestorationLetterService {

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator servLoc = null; /* SERVICE LOCATOR */

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			servLoc = (ServiceLocator) inputMap.get("serviceLocator");
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gDtCurDate = DBUtility.getCurrentDateFromDB();

		} catch (Exception e) {
			gLogger.error("Error in setSessionInfo :" + e);
			throw (e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.CVPRestorationLetterService#
	 * generateCVPRestorationLetter(java.util.Map)
	 */

	public ResultObject generateCVPRestorationLetter(Map<String, Object> inputMap) {

		gLogger.info("In generateCVPRestorationLetter method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrFromDate = null;
		String lStrToDate = null;
		CVPRestorationLetterDAO lObjCVPRestorationLetterDAO = null;
		List lLstCVPRestorationDtls = null;
		List<String> lLstCvpOrderNo = new ArrayList<String>();
		List<Date> lLstDateOfRetirement = new ArrayList<Date>();
		List<String> lLstPnsnrNames = new ArrayList<String>();
		List<String> lLstPpoNo = new ArrayList<String>();
		List<BigDecimal> lLstBasicPnsnAmnt = new ArrayList<BigDecimal>();
		List<String> lLstPensionerCode = new ArrayList<String>();
		List<String> lLstFinalPensionerCode = new ArrayList<String>();
		List<Integer> lLstFinalYearMonth = new ArrayList<Integer>();
		List<BigDecimal> lLstCvpAmnt = new ArrayList<BigDecimal>();
		List<BigDecimal> lLstFinalCvpAmnt = new ArrayList<BigDecimal>();
		List<Date> lLstCvpEffctvDate = new ArrayList<Date>();
		List<Date> lLstFinalCvpEffctvDate = new ArrayList<Date>();
		List<Date> lLstCvpApplnRecvdDate = new ArrayList<Date>();
		List<Date> lLstFinalCvpApplnRecvdDate = new ArrayList<Date>();
		List<Integer> lLstYearMonth = new ArrayList<Integer>();
		List lLstRstrnLetterDtls = null;
		List<BigDecimal> lLstGrossAmnt = new ArrayList<BigDecimal>();
		List<BigDecimal> lLstArrearAmnt = new ArrayList<BigDecimal>();
		List<BigDecimal> lLstTiArrearAmnt = new ArrayList<BigDecimal>();
		List<String> lLstPnsnrAddr = new ArrayList<String>();
		Long lLngPnsnrAmntAftrRstrn = 0l;
		BigDecimal lBDCommutedAmnt = BigDecimal.ZERO;
		String lStrUniqueLetterNo = null;
		String lStrSrNo = null;
		StringBuffer lSbDisplayString = new StringBuffer();
		StringBuffer lSbPrintString = new StringBuffer();
		String lStrNewLine = StringUtils.getLineSeparator();
		String lStrOffAddr = null;
		List<Long> lLstPnsnrAmntAftrRstrn = new ArrayList<Long>();
		Boolean lBlFlag = false;
		// int cnt = 1;
		// CmnTableSeqMst lObjCmnTableSeqMst = null;
		try {
			setSessionInfo(inputMap);
			lStrFromDate = StringUtility.getParameter("fromDate", request);
			lStrToDate = StringUtility.getParameter("toDate", request);
			lObjCVPRestorationLetterDAO = new CVPRestorationLetterDAOImpl(null, servLoc.getSessionFactory());
			lLstCVPRestorationDtls = lObjCVPRestorationLetterDAO.getCVPRestorationDtls(lStrFromDate, lStrToDate, gStrLocationCode);
			// prepare cvp details
			Iterator it = lLstCVPRestorationDtls.iterator();
			Object[] lObjArr = null;
			while (it.hasNext()) {
				lObjArr = (Object[]) it.next();
				if (lObjArr[0] != null) {
					lLstPensionerCode.add((String) lObjArr[0]);
				}
				if (lObjArr[1] != null) {
					lLstCvpAmnt.add((BigDecimal) lObjArr[1]);
				}
				if (lObjArr[2] != null) {
					
					String lStrTempDate = DateUtilities.stringFromDate((Date) lObjArr[2]);
					String[] lStrArrTempDate = lStrTempDate.split("/");
					String lStrTempDateMonth = lStrArrTempDate[1].trim();
					String lStrTempDateYear = lStrArrTempDate[2].trim();
					Integer lIntTempYear = null;
					Integer lIntMonth = null;
					if (Integer.valueOf(lStrTempDateMonth) == 1) {
						lIntMonth = 12;
						lIntTempYear = Integer.valueOf(lStrTempDateYear) - 1;
					} else {
						lIntMonth = Integer.valueOf(lStrTempDateMonth) - 1;
						lIntTempYear = Integer.valueOf(lStrTempDateYear);
					}
					String lStrYearMonth = null;
					if (lIntMonth < 10) {
						lStrYearMonth = String.valueOf(lIntTempYear).concat("0" + String.valueOf(lIntMonth));
					} else {
						lStrYearMonth = String.valueOf(lIntTempYear).concat(String.valueOf(lIntMonth));
					}

					lLstYearMonth.add(Integer.valueOf(lStrYearMonth));
				}
				if (lObjArr[3] != null) {
					lLstCvpEffctvDate.add((Date) lObjArr[3]);
				}
				if(lObjArr[4] != null) {
					lLstCvpApplnRecvdDate.add((Date) lObjArr[4]);
				}
			}
			for (int lIntCnt = 0; lIntCnt < lLstPensionerCode.size(); lIntCnt++) {
				lBlFlag = lObjCVPRestorationLetterDAO.checkBillIsExistsOrNot(lLstPensionerCode.get(lIntCnt), lLstYearMonth.get(lIntCnt));
				if (lBlFlag == true) {
					lLstFinalPensionerCode.add(lLstPensionerCode.get(lIntCnt));
					lLstFinalYearMonth.add(lLstYearMonth.get(lIntCnt));
					lLstFinalCvpAmnt.add(lLstCvpAmnt.get(lIntCnt));
					lLstFinalCvpApplnRecvdDate.add(lLstCvpApplnRecvdDate.get(lIntCnt));
					lLstFinalCvpEffctvDate.add(lLstCvpEffctvDate.get(lIntCnt));
				}
			}
			for (int lIntCnt = 0; lIntCnt < lLstFinalPensionerCode.size(); lIntCnt++) {
				lLstRstrnLetterDtls = lObjCVPRestorationLetterDAO.getRstrnLetterDtls(lLstFinalPensionerCode.get(lIntCnt), lLstFinalYearMonth.get(lIntCnt));
				Iterator iterator = lLstRstrnLetterDtls.iterator();
				Object[] tuple = null;
				// prepare other details
				while (iterator.hasNext()) {
					tuple = (Object[]) iterator.next();
					if (tuple[5] != null) // pensioner code
					{
						if (tuple[0] != null) {
							lLstCvpOrderNo.add((String) tuple[0]);
						} else {
							lLstCvpOrderNo.add("");
						}
						if (tuple[1] != null) {
							lLstDateOfRetirement.add((Date) tuple[1]);
						} else {
							lLstDateOfRetirement.add(null);
						}
						if (tuple[2] != null) {
							lLstPnsnrNames.add((String) tuple[2]);
						} else {
							lLstPnsnrNames.add("");
						}
						if (tuple[3] != null) {
							lLstPpoNo.add((String) tuple[3]);
						} else {
							lLstPpoNo.add("");
						}
						if (tuple[4] != null) {
							lLstBasicPnsnAmnt.add((BigDecimal) tuple[4]);
						} else {
							lLstBasicPnsnAmnt.add(BigDecimal.ZERO);
						}
						if (tuple[6] != null) {
							lLstGrossAmnt.add((BigDecimal) tuple[6]);
						} else {
							lLstGrossAmnt.add(BigDecimal.ZERO);
						}
						if (tuple[7] != null) {
							lLstArrearAmnt.add((BigDecimal) tuple[7]);
						} else {
							lLstArrearAmnt.add(BigDecimal.ZERO);
						}
						if (tuple[8] != null) {
							lLstTiArrearAmnt.add((BigDecimal) tuple[8]);
						} else {
							lLstTiArrearAmnt.add(BigDecimal.ZERO);
						}
						if (tuple[9] != null) {
							lLstPnsnrAddr.add((String) tuple[9]);
						} else {
							lLstPnsnrAddr.add("");
						}

					}
				}
				String lStrCurrDate = DateUtilities.stringFromDate(gDtCurDate);
				String[] lArrStrCurrDate = lStrCurrDate.split("/");

				if (DateUtility.getCurrentYear().equals(lArrStrCurrDate[2].trim())) {

					if (lArrStrCurrDate[1].trim().equals("12")) {
						if (lArrStrCurrDate[0].trim().equals("31")) {
							/*
							 * if(cnt == 1) { lObjCVPRestorationLetterDAO = new
							 * CVPRestorationLetterDAOImpl
							 * (CmnTableSeqMst.class,servLoc
							 * .getSessionFactory()); String[]
							 * lArrStrCmnTableSeqMst =
							 * {"tableName","locationCode"}; Object[]
							 * lArrObjCmnTableSeqMst =
							 * {"rstrtnletter_sr_no",gStrLocationCode};
							 * List<CmnTableSeqMst> lLstCmnTableSeqMst =
							 * lObjCVPRestorationLetterDAO
							 * .getListByColumnAndValue
							 * (lArrStrCmnTableSeqMst,lArrObjCmnTableSeqMst);
							 * if(!lLstCmnTableSeqMst.isEmpty()) {
							 * lObjCmnTableSeqMst = lLstCmnTableSeqMst.get(0);
							 * lObjCmnTableSeqMst.setGeneratedId(1L);
							 * lObjCVPRestorationLetterDAO
							 * .update(lObjCmnTableSeqMst);
							 * servLoc.getSessionFactory
							 * ().getCurrentSession().flush(); cnt = cnt + 1; }
							 * } else { lStrSrNo =
							 * IDGenerateDelegate.getNextIdWODbidLocationId
							 * ("rstrtnletter_sr_no", inputMap); }
							 */
							// System.out.println("1 year complete!");
						} else {
							lStrSrNo = IDGenerateDelegate.getNextIdWODbidLocationId("rstrtnletter_sr_no", inputMap);
						}
					} else {
						lStrSrNo = IDGenerateDelegate.getNextIdWODbidLocationId("rstrtnletter_sr_no", inputMap);

					}
				}

				// lObjCVPRestorationLetterDAO = new
				// CVPRestorationLetterDAOImpl(null,servLoc.getSessionFactory());
				lStrUniqueLetterNo = SessionHelper.getLocationVO(inputMap).getLocShortName().concat("/Pension Restoration").concat("/" + DateUtility.getCurrentDate()).concat("/" + lStrSrNo);
				lBDCommutedAmnt = lObjCVPRestorationLetterDAO.getCommutedAmnt(lLstFinalPensionerCode.get(lIntCnt));
				lLngPnsnrAmntAftrRstrn = lLstGrossAmnt.get(lIntCnt).longValue() - (lLstArrearAmnt.get(lIntCnt).longValue() + lLstTiArrearAmnt.get(lIntCnt).longValue())
						+ lLstFinalCvpAmnt.get(lIntCnt).longValue();
				if (lLngPnsnrAmntAftrRstrn > 0l) {
					lLstPnsnrAmntAftrRstrn.add(lLngPnsnrAmntAftrRstrn);
				} else {
					lLstPnsnrAmntAftrRstrn.add(0l);
				}
				lStrOffAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(SessionHelper.getLocationCode(inputMap));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(32) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(47) + SessionHelper.getLocationName(inputMap));
				lSbDisplayString.append("\n");
				if (lStrOffAddr != null && lStrOffAddr.length() > 0) {
					lSbDisplayString.append(space(40) + lStrOffAddr);
				} else {
					lSbDisplayString.append(space(40));
				}
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(lStrUniqueLetterNo);
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR1"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR2"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR3"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR4"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR5"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(20) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR6"));
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR7"));
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR22") + space(1) + lLstCvpOrderNo.get(lIntCnt) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR8") + DateUtilities.stringFromDate(lLstFinalCvpEffctvDate.get(lIntCnt)) + ".");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(32) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR9"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR10") + space(25) + " : " + lLstPnsnrNames.get(lIntCnt));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR11") + space(24) + " : " + DateUtilities.stringFromDate(lLstDateOfRetirement.get(lIntCnt)));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR12") + space(17) + " : " + lLstPpoNo.get(lIntCnt));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR13") + space(16) + " : " + lLstBasicPnsnAmnt.get(lIntCnt));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR14") + space(24) + " : " + lBDCommutedAmnt);
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR15") + space(16) + " : " + lLstFinalCvpAmnt.get(lIntCnt));
				lSbDisplayString.append("\n");
				Date lDtDateOfRestn = lLstFinalCvpApplnRecvdDate.get(lIntCnt);
				Calendar lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtDateOfRestn);
				lObjCalendar.add(Calendar.DATE, 1);
				lDtDateOfRestn = lObjCalendar.getTime();
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR16") + space(13) + " : " + DateUtilities.stringFromDate(lDtDateOfRestn));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR17") + space(7) + " : " + (lLstBasicPnsnAmnt.get(lIntCnt).doubleValue() + lLstFinalCvpAmnt.get(lIntCnt).doubleValue()));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR18") + space(17) + " : ");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(81) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR19"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(lStrUniqueLetterNo);
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR21") + space(2) + lLstPnsnrNames.get(lIntCnt) + space(2) + lLstPnsnrAddr.get(lIntCnt) + space(2)
						+ gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR20") + "\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(81) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR19"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append((char) 12);

				lSbPrintString.append("<div><pre><font size='3px'>");
				lSbPrintString.append(space(43) + " <strong> " + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR") + " </strong> ");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(47) + SessionHelper.getLocationName(inputMap));
				lSbPrintString.append(lStrNewLine);
				if (lStrOffAddr != null && lStrOffAddr.length() > 0) {
					lSbPrintString.append(space(33) + lStrOffAddr);
				} else {
					lSbPrintString.append(space(33));
				}
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrUniqueLetterNo);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR1"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR2"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR3"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR4"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR5"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(20) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR6"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR7"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR22") + space(1) + lLstCvpOrderNo.get(lIntCnt) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR8") + DateUtilities.stringFromDate(lLstFinalCvpEffctvDate.get(lIntCnt)) + ".");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(40) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR9"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR10") + space(25) + " : " + lLstPnsnrNames.get(lIntCnt));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR11") + space(24) + " : " + DateUtilities.stringFromDate(lLstDateOfRetirement.get(lIntCnt)));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR12") + space(17) + " : " + lLstPpoNo.get(lIntCnt));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR13") + space(16) + " : " + lLstBasicPnsnAmnt.get(lIntCnt));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR14") + space(24) + " : " + lBDCommutedAmnt);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR15") + space(16) + " : " + lLstFinalCvpAmnt.get(lIntCnt));
				lSbPrintString.append(lStrNewLine);
				lDtDateOfRestn = lLstFinalCvpApplnRecvdDate.get(lIntCnt);
				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtDateOfRestn);
				lObjCalendar.add(Calendar.DATE, 1);
				lDtDateOfRestn = lObjCalendar.getTime();
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR16") + space(13) + " : " + DateUtilities.stringFromDate(lDtDateOfRestn));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR17") + space(7) + " : " + (lLstBasicPnsnAmnt.get(lIntCnt).doubleValue() + lLstFinalCvpAmnt.get(lIntCnt).doubleValue()));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(17) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR18") + space(17) + " : ");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(87) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR19"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrUniqueLetterNo);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR21") + space(2) + lLstPnsnrNames.get(lIntCnt) + space(2) + lLstPnsnrAddr.get(lIntCnt) + space(2)
						+ gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR20") + lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(87) + gObjRsrcBndle.getString("PPMT.CMTNRSTRNLTR19"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append("</font></pre></div>");
				lSbPrintString.append((char) 12);

			}
			inputMap.put("DisplayRstrnLetterString", lSbDisplayString);
			inputMap.put("PrintRstrnLetterString", lSbPrintString);
			inputMap.put("PopUpScreen", "PopUp");
			objRes.setResultValue(inputMap);
			objRes.setViewName("printRestorationLetterPopUp");

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
}
