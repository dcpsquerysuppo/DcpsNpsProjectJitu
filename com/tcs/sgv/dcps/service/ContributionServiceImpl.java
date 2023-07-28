/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 2, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class ContributionServiceImpl extends ServiceImpl implements
		ContributionService {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
		}
	}

	public ResultObject loadContributions(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			/* Get All the Bill Groups under selected DDO (As of now shown all) */
			/* List lLstBillGroups = lObjDcpsCommonDAO.getBillGroups(); */

			/* Get Months */
			List lLstMonths = lObjDcpsCommonDAO.getMonths();

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getYears();

			/* Get User(ATO or TO) and Use type */
			String lStrUserType = StringUtility.getParameter("User", request);

			String lStrUseType = StringUtility.getParameter("Use", request);

			/*
			 * Checks if request is sent by click of GO button or the page is
			 * loaded for the first time.
			 */

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);

			List lLstTreasuries = lObjOfflineContriDAO
					.getTreasuryForDDO(lStrDdoCode);
			inputMap.put("TREASURIES", lLstTreasuries);

			List lLstDDO = lObjOfflineContriDAO
					.getDdoNameFromDdoCode(lStrDdoCode);
			inputMap.put("DDONAMES", lLstDDO);

			List BillGroupList = lObjOfflineContriDAO
					.getBillGroupsForDdo(lStrDdoCode);
			inputMap.put("BillGroupList", BillGroupList);

			inputMap.put("YEARS", lLstYears);
			// inputMap.put("BILLGROUPS", lLstBillGroups);
			inputMap.put("MONTHS", lLstMonths);
			inputMap.put("lStrUser", lStrUserType);
			inputMap.put("lStrUse", lStrUseType);

			resObj.setResultValue(inputMap);
			resObj.setViewName("OfflineEntryFormDDOAsst");

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

	public ResultObject getSchemeforBillGroup(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			Long billgroupId = Long.valueOf(StringUtility.getParameter(
					"billGroupId", request));

			Object[] schemeNameAndCode = lObjDcpsCommonDAO
					.getSchemeNameFromBillGroup(billgroupId);

			String schemeName = (String) schemeNameAndCode[0];
			String schemeCode = (String) schemeNameAndCode[1];

			String lSBScheme = getResponseXMLDocForSchemeFromBillGroup(
					schemeName, schemeCode).toString();

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBScheme).toString();

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

	private StringBuilder getResponseXMLDocForSchemeFromBillGroup(
			String schemeName, String schemeCode) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("  <schemename>");
		lStrBldXML.append(schemeName);
		lStrBldXML.append("  </schemename>");
		lStrBldXML.append("  <schemecode>");
		lStrBldXML.append(schemeCode);
		lStrBldXML.append("  </schemecode>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject loadZipFile(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		String relativeWebPath = "/WEB-INF/jsp/dcps";
		String absoluteFilePath = request.getSession().getServletContext()
				.getRealPath(relativeWebPath);
		try {

			HttpServletRequest request = (HttpServletRequest) inputMap
					.get("requestObj");
			HttpServletResponse response = (HttpServletResponse) inputMap
					.get("responseObj");

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			List lLstMonths = lObjDcpsCommonDAO.getMonths();
			inputMap.put("MONTHS", lLstMonths);

			List lLstYears = lObjDcpsCommonDAO.getYears();
			inputMap.put("YEARS", lLstYears);

			String lStrUserType = StringUtility.getParameter("User", request);
			inputMap.put("lStrUser", lStrUserType);

			String lStrUseType = StringUtility.getParameter("Use", request);
			inputMap.put("lStrUse", lStrUseType);

			if (StringUtility.getParameter("cmbBillGroup", request) != null
					&& !StringUtility.getParameter("cmbBillGroup", request)
							.equalsIgnoreCase("")) {

				String lStrDDOCode = lObjDcpsCommonDAO
						.getDdoCodeForDDO(gLngPostId);
				Long monthId = Long.parseLong(StringUtility.getParameter(
						"cmbMonth", request));
				Long yearId = Long.parseLong(StringUtility.getParameter(
						"cmbYear", request));
				Long lLongbillGroupId = Long.valueOf(StringUtility
						.getParameter("cmbBillGroup", request));
				String treasuryCode = StringUtility.getParameter(
						"treasuryCode", request);
				String schemename = StringUtility.getParameter("txtSchemeName",
						request);
				Long schemeCode = Long.valueOf(StringUtility.getParameter(
						"schemeCode", request));

				String yearCode = lObjDcpsCommonDAO
						.getYearCodeForYearId(yearId);

				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearCode);

				Date lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1,
						lIntYear);
				Date lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(
						lIntMonth - 1, lIntYear);

				if (lIntMonth == 1) {
					lIntYear--;
				}
				String treasuryName = StringUtility.getParameter(
						"TreasuryName", request);
				String lStrDDOName = StringUtility.getParameter("DDOName",
						request);
				String lLongbillGroupName = StringUtility.getParameter(
						"BillGroup", request);

				Date lDtDelFirstDate = lObjDcpsCommonDAO.getFirstDate(
						lIntMonth - 2, lIntYear);
				Date lDtDelLastDate = lObjDcpsCommonDAO.getLastDate(
						lIntMonth - 2, lIntYear);

				inputMap.put("lStrDDOCode", lStrDDOCode);
				inputMap.put("monthId", monthId);
				inputMap.put("yearId", yearId);
				inputMap.put("lLongbillGroupId", lLongbillGroupId);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("schemename", schemename);
				inputMap.put("schemeCode", schemeCode);
				inputMap.put("FirstDate", lDtFirstDate);
				inputMap.put("LastDate", lDtLastDate);
				inputMap.put("DelFirstDate", lDtDelFirstDate);
				inputMap.put("DelLastDate", lDtDelLastDate);

				List listPayCommission = IFMSCommonServiceImpl.getLookupValues(
						"PayCommissionDCPS", SessionHelper.getLangId(inputMap),
						inputMap);
				inputMap.put("listPayCommission", listPayCommission);

				List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues(
						"TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap),
						inputMap);
				inputMap.put("listTypeOfPayment", listTypeOfPayment);

				// String lStrUser = StringUtility.getParameter("User",
				// request);

				if (lStrUserType.equals("ATO")) {
					inputMap.put("EditForm", "Y");
					List UserList = null;
					inputMap.put("UserList", UserList);
				} else if (lStrUserType.equals("TO")) {
					inputMap.put("EditForm", "N");
				}
				String strYear = "";
				for (int index = 0; index < lLstYears.size(); index++) {
					new ArrayList();
					ComboValuesVO lObjComboValuesVO = (ComboValuesVO) lLstYears
							.get(index);
					if (Long.parseLong(lObjComboValuesVO.getId()) == yearId) {
						strYear = lObjComboValuesVO.getDesc();
					}

				}
				String strMonth = "";
				for (int index = 0; index < lLstMonths.size(); index++) {
					new ArrayList();
					ComboValuesVO lObjComboValuesVO = (ComboValuesVO) lLstMonths
							.get(index);
					if (Long.parseLong(lObjComboValuesVO.getId()) == monthId) {
						strMonth = lObjComboValuesVO.getDesc();
					}

				}

				List empList = null;

				// String lStrUse = StringUtility.getParameter("Use",
				// request);

				SimpleDateFormat sdf1 = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				String lStrFirstDate = sdf1.format(lDtFirstDate);
				
				String lStrLastDate = sdf1.format(lDtLastDate);
				

				Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
				
				String lStrTypeOfPaymentMaster = StringUtility.getParameter("typeOfPaymentMaster", request).trim();
				if("".equals(lStrTypeOfPaymentMaster))
				{
					lStrTypeOfPaymentMaster = "700046"; // By default Regular Type
				}
				inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster.trim());
				
				Long delayedMonthId = null;
				Long delayedYearId = null;
				
				if (lStrTypeOfPaymentMaster.equals("700047"))
				{

					delayedMonthId = Long.valueOf(StringUtility.getParameter(
									"cmbDelayedMonth", request).trim());
					delayedYearId = Long.valueOf(StringUtility.getParameter(
									"cmbDelayedYear", request).trim());
				}

				empList = lObjOfflineContriDAO.getEmpListForContribution(
						lStrDDOCode, lLongbillGroupId, monthId, yearId,
						lStrUserType, lStrUseType, gStrPostId, displayTag,
						lStrFirstDate,lStrTypeOfPaymentMaster,delayedMonthId,delayedYearId,lStrLastDate);

				if (empList != null) {

					WorkbookSettings ws = new WorkbookSettings();

					String tempFilePath = absoluteFilePath
							+ "\\OfflineContribution\\ContributionData_"
							+ lStrDDOCode + ".xls";
					File xlsFile = new File(tempFilePath);

					try {
						WritableWorkbook workbook = Workbook.createWorkbook(
								xlsFile, ws);

						WritableSheet workSheet = null;
						workSheet = workbook.createSheet(
								"Offline Contribution", 0);
						workSheet.getSettings();

						WritableFont normalFont = new WritableFont(WritableFont
								.createFont("MS Sans Serif"),
								WritableFont.DEFAULT_POINT_SIZE,
								WritableFont.NO_BOLD, false,
								UnderlineStyle.NO_UNDERLINE);

						WritableFont boldFont = new WritableFont(WritableFont
								.createFont("MS Sans Serif"),
								WritableFont.DEFAULT_POINT_SIZE,
								WritableFont.BOLD, false,
								UnderlineStyle.NO_UNDERLINE);

						WritableCellFormat normalFormat = new WritableCellFormat(
								normalFont);
						normalFormat.setWrap(true);
						normalFormat.setAlignment(jxl.format.Alignment.CENTRE);
						normalFormat
								.setVerticalAlignment(VerticalAlignment.CENTRE);

						WritableCellFormat BoldFormat = new WritableCellFormat(
								boldFont);
						BoldFormat.setWrap(true);
						BoldFormat.setAlignment(jxl.format.Alignment.CENTRE);
						BoldFormat
								.setVerticalAlignment(VerticalAlignment.CENTRE);

						workSheet.addCell(new jxl.write.Label(0, 0, "Treasury",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 1, "DDO Name",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 2,
								"BillGroup", BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 3, "Scheme",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 4, "Month",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 5, "MonthID",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 6, "Year",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 7,
								"StartDate", BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 8, "EndDate",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(0, 9,
								"No. Of Emp", BoldFormat));

						SimpleDateFormat sdf = new SimpleDateFormat("dd");
						new SimpleDateFormat("dd/MM/yyyy");
						SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
						SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

						workSheet.addCell(new jxl.write.Label(1, 0,
								treasuryName, BoldFormat));
						workSheet.addCell(new jxl.write.Label(2, 0,
								treasuryCode, BoldFormat));
						workSheet.addCell(new jxl.write.Label(1, 1,
								lStrDDOName, BoldFormat));
						workSheet.addCell(new jxl.write.Label(2, 1,
								lStrDDOCode, BoldFormat));
						workSheet.addCell(new jxl.write.Label(1, 2,
								lLongbillGroupName.toString(), BoldFormat));
						workSheet.addCell(new jxl.write.Label(2, 2,
								lLongbillGroupId.toString(), BoldFormat));
						workSheet.addCell(new jxl.write.Label(1, 3, schemename,
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(2, 3, schemeCode
								.toString(), BoldFormat));
						workSheet.addCell(new jxl.write.Label(1, 4, strMonth,
								BoldFormat));
						workSheet.addCell(new jxl.write.Number(1, 5, monthId,
								BoldFormat));
						workSheet.addCell(new jxl.write.Number(1, 6, Integer
								.parseInt(strYear), BoldFormat));
						workSheet.addCell(new jxl.write.Label(2, 6, yearId
								.toString(), BoldFormat));
						workSheet
								.addCell(new jxl.write.Number(1, 7, Integer
										.parseInt(sdf.format(lDtFirstDate)),
										BoldFormat));
						workSheet
								.addCell(new jxl.write.Number(1, 8, Integer
										.parseInt(sdf.format(lDtLastDate)),
										BoldFormat));
						workSheet.addCell(new jxl.write.Number(1, 9, empList
								.size(), BoldFormat));

						workSheet.addCell(new jxl.write.Label(0, 11, "Name",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(1, 11, "DCPSID",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(2, 11, "PayComm",
								BoldFormat));

						workSheet.addCell(new jxl.write.Label(3, 11, "Basic",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(4, 11, "Type",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(5, 11, "DP",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(6, 11, "DA",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(7, 11,
								"Contribution", BoldFormat));
						workSheet.addCell(new jxl.write.Label(8, 11, "Emp ID",
								BoldFormat));
						workSheet.addCell(new jxl.write.Label(9, 11,
								"Start Date", BoldFormat));
						workSheet.addCell(new jxl.write.Label(10, 11,
								"Start Month", BoldFormat));
						workSheet.addCell(new jxl.write.Label(11, 11,
								"Start Year", BoldFormat));
						workSheet.addCell(new jxl.write.Label(12, 11,
								"End Date", BoldFormat));
						workSheet.addCell(new jxl.write.Label(13, 11,
								"End Month", BoldFormat));
						workSheet.addCell(new jxl.write.Label(14, 11,
								"End Year", BoldFormat));
						workSheet.addCell(new jxl.write.Label(15, 11,
								"DA Rate", BoldFormat));
						workSheet.addCell(new jxl.write.Label(16, 11,
								"Joining Date", BoldFormat));

						for (int index = 0; index < empList.size(); index++) {
							new ArrayList();
							Object row[] = (Object[]) empList.get(index);
							Long a = Long.parseLong(row[0].toString());
							String b = (String) row[1];
							String c = (String) row[2];
							String d = (String) row[3];
							Double e = Double.parseDouble((String) row[4]);
							String f = (String) row[6];
							Integer g = (Integer) row[15];
							Integer h = (Integer) row[16];
							Integer i = (Integer) row[17];
							Double j = (Double) row[18];
							String k = row[11].toString().trim();

							String strPayComm = "";
							for (int index2 = 0; index2 < listPayCommission
									.size(); index2++) {
								new ArrayList();
								CmnLookupMst tempList = (CmnLookupMst) listPayCommission
										.get(index2);

								if (tempList.getLookupId() == Long.parseLong(d)) {
									strPayComm = tempList.getLookupDesc();
								}

							}
							String strPaymentType = "";
							for (int index3 = 0; index3 < listTypeOfPayment
									.size(); index3++) {
								new ArrayList();
								CmnLookupMst tempList = (CmnLookupMst) listTypeOfPayment
										.get(index3);

								if (tempList.getLookupId() == Long.parseLong(f)) {
									strPaymentType = tempList.getLookupDesc();
								}

							}
							int excelRow = index + 12;
							workSheet.addCell(new jxl.write.Label(0, excelRow,
									c, normalFormat));
							workSheet.addCell(new jxl.write.Label(1, excelRow,
									b, normalFormat));
							workSheet.addCell(new jxl.write.Label(2, excelRow,
									strPayComm, normalFormat));
							workSheet.addCell(new jxl.write.Number(3, excelRow,
									e, normalFormat));
							workSheet.addCell(new jxl.write.Label(4, excelRow,
									strPaymentType, normalFormat));
							workSheet.addCell(new jxl.write.Number(5, excelRow,
									g, normalFormat));
							workSheet.addCell(new jxl.write.Number(6, excelRow,
									h, normalFormat));
							workSheet.addCell(new jxl.write.Number(7, excelRow,
									i, normalFormat));
							workSheet.addCell(new jxl.write.Number(8, excelRow,
									a, normalFormat));
							workSheet.addCell(new jxl.write.Label(9, excelRow,
									sdf.format(lDtFirstDate), normalFormat));
							workSheet
									.addCell(new jxl.write.Label(10, excelRow,
											sdfMonth.format(lDtFirstDate),
											normalFormat));
							workSheet
									.addCell(new jxl.write.Label(11, excelRow,
											sdfYear.format(lDtFirstDate),
											normalFormat));
							workSheet.addCell(new jxl.write.Label(12, excelRow,
									sdf.format(lDtLastDate), normalFormat));
							workSheet
									.addCell(new jxl.write.Label(13, excelRow,
											sdfMonth.format(lDtLastDate),
											normalFormat));
							workSheet.addCell(new jxl.write.Label(14, excelRow,
									sdfYear.format(lDtLastDate), normalFormat));
							workSheet.addCell(new jxl.write.Number(15,
									excelRow, j, normalFormat));
							workSheet.addCell(new jxl.write.Label(16, excelRow,
									k, normalFormat));

						}

						CellView cell = new CellView();
						for (int x = 0; x < 10; x++) {
							cell = workSheet.getColumnView(x);
							cell.setAutosize(true);
							workSheet.setColumnView(x, cell);
						}

						workbook.write();
						workbook.close();

					} catch (Exception e) {
						e.printStackTrace();
						gLogger.error(" Error in  Excel is : " + e, e);
					}

					CreateZip(absoluteFilePath, lStrDDOCode);

					File zipFile = new File(absoluteFilePath
							+ "\\OfflinePackages\\ContributionData_"
							+ lStrDDOCode + ".zip");
					FileInputStream fis = new FileInputStream(zipFile);
					BufferedInputStream bInputStr = new BufferedInputStream(fis);
					byte[] byteBuffer = new byte[(int) zipFile.length()];
					bInputStr.read(byteBuffer);

					bInputStr.close();
					zipFile.delete();
					request.setAttribute("ContentType", "application/zip");
					request
							.setAttribute("ExportedReportBytesArray",
									byteBuffer);
					response.addHeader("Content-disposition",
							"attachment; filename=ContributionData_"
									+ lStrDDOCode + ".zip");
					response.setCharacterEncoding("UTF-8");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);

		}

		resObj.setViewName("ExportReportPage");

		return resObj;
	}

	public void CreateZip(String inPath, String lStrDDOCode) {

		try {

			File inFolder = new File(inPath + "\\OfflineContribution");
			File outFolder = new File(inPath
					+ "\\OfflinePackages\\ContributionData_" + lStrDDOCode
					+ ".zip");

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(outFolder)));

			BufferedInputStream in = null;
			byte[] data = new byte[1000];
			String files[] = inFolder.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].equals("images")) {

					File subFolder = new File(inPath
							+ "\\OfflineContribution\\images");

					String subFiles[] = subFolder.list();
					for (int j = 0; j < subFiles.length; j++) {
						in = new BufferedInputStream(new FileInputStream(
								subFolder.getPath() + "/" + subFiles[j]), 1000);
						out
								.putNextEntry(new ZipEntry("images\\"
										+ subFiles[j]));
						int count;
						while ((count = in.read(data, 0, 1000)) != -1) {
							out.write(data, 0, count);
						}
					}

				} else {
					in = new BufferedInputStream(new FileInputStream(inFolder
							.getPath()
							+ "/" + files[i]), 1000);
					if (files[i].endsWith("xls")) {
						if (files[i].endsWith(lStrDDOCode + ".xls")) {
							out.putNextEntry(new ZipEntry(
									"ContributionData.xls"));
						} else {
							continue;
						}
					} else {
						out.putNextEntry(new ZipEntry(files[i]));
					}
					int count;
					while ((count = in.read(data, 0, 1000)) != -1) {
						out.write(data, 0, count);
					}
					if (files[i].endsWith("xls")) {
						File file = new File(files[i]);
						file.delete();
					}
				}
				out.closeEntry();
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] getBytesFromFile(File file) throws IOException {

		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes (bytes, offset, bytes.length - offset
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

}
