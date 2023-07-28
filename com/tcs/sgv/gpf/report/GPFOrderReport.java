package com.tcs.sgv.gpf.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import java.text.MessageFormat;
/*import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;*/


public class GPFOrderReport extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(GPFOrderReport.class);
	public static String newline = System.getProperty("line.separator");
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	Map lMapSessionAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Session ghibSession = null;
	LoginDetails lObjLoginVO = null;
	private ResourceBundle gObjRsrcBndleForRA = ResourceBundle.getBundle("resources/gpf/RefundableOrderMarathi");
	private ResourceBundle gObjRsrcBndleForNRA = ResourceBundle.getBundle("resources/gpf/NonRefundableOrderMarathi");
	private ResourceBundle gObjRsrcBndleForFW = ResourceBundle.getBundle("resources/gpf/FinalPayOrderMarathi");

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		Connection con = null;

		requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();
		lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
		Map serviceMap = (Map)requestAttributes.get("serviceMap");
		HttpServletRequest request = (HttpServletRequest) serviceMap.get("requestObj");
		Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
		Map inputMap = new HashMap();

		List<OrgPostMst> lLngPostIdList = (List) loginDetail.get("postIdList");
		OrgPostMst lObjPostMst = lLngPostIdList.get(0);
		Long lLngPostId = lObjPostMst.getPostId();

		Statement smt = null;
		ResultSet rs = null;
		TabularData td = null;
		ReportVO RptVo = null;
		MessageFormat msgFormatter = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();

		ArrayList<Object> dataList = new ArrayList<Object>();
		try {

			con = lObjSessionFactory.getCurrentSession().connection();
			ghibSession = lObjSessionFactory.getCurrentSession();
			smt = con.createStatement();
			StyleVO[] rowsFontsVO = new StyleVO[5];
			rowsFontsVO[0] = new StyleVO();
			rowsFontsVO[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rowsFontsVO[0].setStyleValue("12");
			rowsFontsVO[1] = new StyleVO();
			rowsFontsVO[1].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			rowsFontsVO[1].setStyleValue("white");
			rowsFontsVO[2] = new StyleVO();
			rowsFontsVO[2].setStyleId(IReportConstants.BORDER);
			rowsFontsVO[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			rowsFontsVO[3] = new StyleVO();
			rowsFontsVO[3].setStyleId(26);
			rowsFontsVO[3].setStyleValue("JavaScript:self.close()");
			rowsFontsVO[4] = new StyleVO();
			rowsFontsVO[4].setStyleId(IReportConstants.REPORT_PAGINATION);
			rowsFontsVO[4].setStyleValue("NO");

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();
			report.setStyleList(rowsFontsVO);

			StyleVO[] centerUnderlineBold = new StyleVO[4];
			centerUnderlineBold[0] = new StyleVO();
			centerUnderlineBold[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerUnderlineBold[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerUnderlineBold[1] = new StyleVO();
			centerUnderlineBold[1].setStyleId(IReportConstants.STYLE_TEXT_DECORATION);
			centerUnderlineBold[1].setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
			centerUnderlineBold[2] = new StyleVO();
			centerUnderlineBold[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerUnderlineBold[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			centerUnderlineBold[3] = new StyleVO();
			centerUnderlineBold[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerUnderlineBold[3].setStyleValue("14");

			StyleVO[] rightAlign = new StyleVO[2];
			rightAlign[0] = new StyleVO();
			rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			rightAlign[1] = new StyleVO();
			rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightAlign[1].setStyleValue("12");

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("14");

			// for Center Alignment format
			StyleVO[] CenterAlignVO = new StyleVO[3];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			CenterAlignVO[2] = new StyleVO();
			CenterAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			CenterAlignVO[2].setStyleValue("12");

			StyleVO[] CenterBoldAlignVO = new StyleVO[4];
			CenterBoldAlignVO[0] = new StyleVO();
			CenterBoldAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterBoldAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterBoldAlignVO[1] = new StyleVO();
			CenterBoldAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterBoldAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			CenterBoldAlignVO[2] = new StyleVO();
			//CenterBoldAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			//	CenterBoldAlignVO[2].setStyleValue("12");
			CenterBoldAlignVO[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			CenterBoldAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			CenterBoldAlignVO[3] = new StyleVO();
			CenterBoldAlignVO[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			CenterBoldAlignVO[3].setStyleValue("14");

			if (report.getReportCode().equals("800007")) {

				String reqType = (String) report.getParameterValue("reqType");
				String lStrDisbursementNo = "1";
				if (reqType.startsWith("NRA")) {
					lStrDisbursementNo = reqType.substring(3);
					reqType = "NRA";
				}
				String gpfAccNo = (String) report.getParameterValue("gpfAccNo");
				String lStrOrderId = (String) report.getParameterValue("orderNo");
				String lStrOrderDate = (String) report.getParameterValue("orderDate");
				String lStrSevaarthId = (String) report.getParameterValue("sevaarthId");
				String lStrTransactionId = (String) report.getParameterValue("transactionId");
				String lStrPurposeOfNra = (String) request.getParameter("purposeOfNra");

				Long sancAmount = 0L;
				if (report.getParameterValue("sancAmount") != "" && report.getParameterValue("sancAmount") != null) {
					sancAmount = Math.round(Double.parseDouble((String) report.getParameterValue("sancAmount")));
				}

				String empName = (String) report.getParameterValue("empName");
				StringBuffer strEmpIntls = null;
				if(empName!=null && !empName.isEmpty()){
					String[] strNameAry = empName.split(" ");
					strEmpIntls = new StringBuffer();
					for(int i=0;i<strNameAry.length;i++)
						strEmpIntls.append(strNameAry[i].charAt(0));
				}
				Long basicAmount = Math.round(Double.valueOf((String) report.getParameterValue("basicSalary")));

				SimpleDateFormat lObjDateFormate = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				String lStrJoiningDate = (String)
				report.getParameterValue("joiningDate");
				String lStrRetirementDate = (String)
				report.getParameterValue("superAnnDate");

				Integer retirementMonths = null ;
				Integer serviceMonths = null;




				Date joiningDate = lObjDateFormate.parse(lStrJoiningDate);
				Date retirementDate = lObjDateFormate.parse(lStrRetirementDate);
				//getDateDifference(joiningDate, retirementDate);
				//int noOfMonthLeft = Math.abs(retirementDate.getMonth() - today.getMonth());
				
				int a = Math.abs(retirementDate.getMonth());
				int b = today.getMonth();
				
				int noOfMonthLeft = 0;
				
				if(a > b)
					noOfMonthLeft = ((12 - b) - (12 - a));
				
				
				else if(b > a)
					 noOfMonthLeft = ((12 - a) - (12 - b));
				
				int noOfYearsLeft = retirementDate.getYear() - today.getYear();

				//int noOfMonthServed = Math.abs(today.getMonth() - joiningDate.getMonth());
				
				int c = Math.abs(joiningDate.getMonth());
			
				
				int noOfMonthServed = 0;
				
				if(c > b)
					noOfMonthServed = ((12 - b) - (12 - c));
				
				
				else if(b > c)
					noOfMonthServed = ((12 - c) - (12 - b));
				
				int noOfYearsServed = today.getYear() - joiningDate.getYear();



				FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serviceLocator
						.getSessionFactory());
				GPFRequestProcessDAO lObjGPFRequestProcess = new GPFRequestProcessDAOImpl(null, serviceLocator
						.getSessionFactory());
				Integer lIntFinYrId = lObjFinancialYearDAO.getFinYearIdByCurDate();
				Long lLngPreFinYearId = lIntFinYrId.longValue() - 1;
				Double OpeningBalance = lObjGPFRequestProcess.getOpeningBalForCurrYear(gpfAccNo, lIntFinYrId
						.longValue());
				ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serviceLocator
						.getSessionFactory());
				String preFinYear = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lLngPreFinYearId);
				String lStrcurrFinYearCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lIntFinYrId
						.longValue());
				lStrcurrFinYearCode = lStrcurrFinYearCode.substring(0, 4);
				Date startDate = lObjScheduleGenerationDAO.getStartDateOfFinancialYear(lIntFinYrId.longValue());
				List lLstDsgnAndOfficeName = lObjGPFRequestProcess.getDsgnAndOfficeName(lStrSevaarthId);

				List lstDDODeptDtls = lObjGPFRequestProcess.getDDODeptDetails(lStrSevaarthId);

				String deptName = null;
				String ddoOfcAdrs = null;
				String ddoOfcEmail = null;
				String ddoOfcTelNo = null;
				if(lstDDODeptDtls != null && !lstDDODeptDtls.isEmpty()){
					Object[] obj = (Object[])lstDDODeptDtls.get(0);
					deptName = obj[1].toString();
					ddoOfcAdrs = obj[4].toString();
					if(obj[5]!=null)
						ddoOfcEmail = obj[5].toString();
					if(obj[6]!=null)
						ddoOfcTelNo = obj[6].toString();
				}

				Object[] lObjDsgnAndOfficeName = null;
				String lStrDsgnName = "";
				String lStrOfficeName = "";
				String lStrPayScale = "";
				Long lStrBasicPay = null;
				Long  lStrGradePay = 0l;
				if (!lLstDsgnAndOfficeName.isEmpty()) {
					lObjDsgnAndOfficeName = (Object[]) lLstDsgnAndOfficeName.get(0);
					lStrDsgnName = (String) lObjDsgnAndOfficeName[0];
					lStrOfficeName = (String) lObjDsgnAndOfficeName[1];
					lStrPayScale = (String) lObjDsgnAndOfficeName[2];
					Object obj =  lObjDsgnAndOfficeName[3];
					lStrBasicPay = Long.parseLong(obj.toString());
					if(lObjDsgnAndOfficeName[4] != null)
						lStrGradePay = Long.parseLong(obj.toString());
				}
				List lLstaccountBalance = lObjGPFRequestProcess.getGPFAccountBalance(gpfAccNo, lIntFinYrId.longValue());
				Object[] lObjGPFAccountBal = null;
				if (lLstaccountBalance != null && lLstaccountBalance.size() > 0) {
					lObjGPFAccountBal = (Object[]) lLstaccountBalance.get(0);
				}

				Double lDblWithdrawalSanc = 0d;
				Object[] lObjhistory = null;
				List lLstAdvanceHistoryDtls = lObjGPFRequestProcess
				.getAdvanceHistory(gpfAccNo, lIntFinYrId.longValue());
				if (lLstAdvanceHistoryDtls != null && lLstAdvanceHistoryDtls.size() > 0) {
					for (Integer lIntcnt = 0; lIntcnt < lLstAdvanceHistoryDtls.size(); lIntcnt++) {
						lObjhistory = (Object[]) lLstAdvanceHistoryDtls.get(lIntcnt);
						lDblWithdrawalSanc += (Double) lObjhistory[1];
					}
				}
				Double lDbClosingBalance = lObjGPFRequestProcess.getClosingBalance(gpfAccNo);
				List<Object[]> advanceList = lObjGPFRequestProcess.getAdvanceDetail(gpfAccNo, reqType);
				String specialCaseOrNt = "N";
				Double outStndBalance = null;
				Object lObjAdvDtls[] = new Object[10];
				if (!advanceList.isEmpty()) {
					lObjAdvDtls = advanceList.get(0);
					specialCaseOrNt = lObjAdvDtls[9].toString();
					outStndBalance = Double.parseDouble(lObjAdvDtls[10].toString());
					
				}

				String lStrDDOCodeOfLoggedInDDO = lObjGPFRequestProcess.getDdoCodeForDDO(lLngPostId);
				String lStrEmployerOfficeName = lObjGPFRequestProcess.getEmployerOfficeName(lStrDDOCodeOfLoggedInDDO);
				String lStrEmployerDsgnName = lObjGPFRequestProcess.getEmployerDsgnName(lLngPostId);
				String emplyeeOfficerName = lObjGPFRequestProcess.getEmployerNameFrmPostId(lLngPostId.toString());
				String lStrTreasuryName = lObjGPFRequestProcess.getTreasuryNameOfEmp(lStrDDOCodeOfLoggedInDDO);
				//String lStrGradePay = lObjGPFRequestProcess.getGradePayFrmSevaarthId(lStrSevaarthId);
				String lStrPurposeOfRequest = lObjGPFRequestProcess.getPrpsFrmTransactionId(lStrTransactionId);				
				String lStrPurposeName = lObjGPFRequestProcess.getPurposeNameFrmId(lStrPurposeOfNra); 

				Date lDtApplicDate = lObjGPFRequestProcess.getApplicationDateFrmTranId(lStrTransactionId);
				startDate.getMonth();
				startDate.getYear();

				Integer currDate = today.getDate();
				Integer currMonth = today.getMonth() + 1;
				Integer currYear = today.getYear() + 1900;

				if (reqType.equals("RA")) {

					Double total = Math.round(OpeningBalance) + Double.parseDouble(lObjGPFAccountBal[0].toString())
					+ Double.parseDouble(lObjGPFAccountBal[1].toString());


					report.setStyleList(noBorder);
					report.setStyleList(rowsFontsVO);
					ArrayList<Object> rowList = new ArrayList<Object>();

					rowList.add("" + space(1));
					dataList.add(rowList);

					/*rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE1") + " " + lStrOrderId,
							boldVO));
					dataList.add(rowList);
					 */

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GFP.ADDRESS1") ,
							CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(deptName, CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(lStrOfficeName, CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(ddoOfcAdrs, CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					if(ddoOfcTelNo != null)
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE66")+ ddoOfcTelNo, CenterBoldAlignVO));
					else 
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE66")+ "____________________", CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					if(ddoOfcEmail != null)
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE67")+ ddoOfcEmail, CenterBoldAlignVO) );
					else 
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE67")+ "_____________________", CenterBoldAlignVO) );
					dataList.add(rowList);


					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);


					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE68"), lStrDDOCodeOfLoggedInDDO,currYear+"",strEmpIntls,
							lStrTransactionId,currDate+"/"+currMonth+"/"+currYear) ,CenterAlignVO));
					dataList.add(rowList);




					rowList = new ArrayList<Object>();
					rowList.add(new StyledData( gObjRsrcBndleForRA.getString("GPF.ORDERLINE2"),
							CenterAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE69"),empName, lStrDsgnName,currDate+"/"+currMonth+"/"+currYear),CenterAlignVO));
					dataList.add(rowList);


					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE4"), centerUnderlineBold));
					dataList.add(rowList);

					if(specialCaseOrNt.equals("Y")){
						Double totalamonut = outStndBalance + sancAmount;
						rowList = new ArrayList<Object>();
						rowList.add(space(40)+MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE79"),
								"______","_____",lStrEmployerDsgnName,empName,lStrDsgnName, (lStrBasicPay + lStrGradePay),gpfAccNo,lStrPurposeName,
								(total - lDblWithdrawalSanc),sancAmount,EnglishDecimalFormat.convert(sancAmount),outStndBalance,totalamonut,lObjAdvDtls[5],lObjAdvDtls[4],((Date)lObjAdvDtls[8] ).getMonth()+2,"___")); /// +2 in month to get month next to sanctioned month 
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add("2. " + space(38) + MessageFormat.format(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose47"),empName , lStrDsgnName,lStrOfficeName,currDate+"/"+currMonth+"/"+currYear));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add("3. " + space(38) + gObjRsrcBndleForRA.getString("GPF.ORDERLINE81") +" "+ empName+" "				
								+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE82") + " "+noOfYearsLeft +" "
								+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE83") + " "+noOfMonthLeft+" "
								+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE84") +noOfYearsServed+ " "+gObjRsrcBndleForRA.getString("GPF.ORDERLINE83") +noOfMonthServed+" "
								+gObjRsrcBndleForRA.getString("GPF.ORDERLINE85"));				
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add("4."+space(38)+MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE71"),empName , lStrDsgnName,lStrOfficeName,currDate+"/"+currMonth+"/"+currYear));
						dataList.add(rowList);
					}
					else {
					rowList = new ArrayList<Object>();
					rowList.add(space(40)+MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE70"),
							"______","_____",lStrEmployerDsgnName,empName,lStrDsgnName, (lStrBasicPay + lStrGradePay),gpfAccNo,lStrPurposeName,
							(total - lDblWithdrawalSanc),sancAmount,EnglishDecimalFormat.convert(sancAmount),lObjAdvDtls[5],lObjAdvDtls[4],((Date)lObjAdvDtls[8] ).getMonth()+2,"___")); /// +2 in month to get month next to sanctioned month 
					dataList.add(rowList);

					/*rowList = new ArrayList<Object>();
					rowList.add(space(40) + gObjRsrcBndleForRA.getString("GPF.ORDERLINE5") + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE6") +" "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE48") + " " + lStrDsgnName + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE49") +" "+ lStrOfficeName + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE7") + " "
							+ lStrPayScale + " " 
							//+gObjRsrcBndleForRA.getString("GPF.ORDERLINE8")+" "
							+lStrGradePay+"/- "+  gObjRsrcBndleForRA.getString("GPF.ORDERLINE9") + " " + gpfAccNo
							+ " " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE10") + " " +lStrPurposeOfRequest+" "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE50") +" "+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE11") +" "+ lDbClosingBalance + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE12") + " " + sancAmount +"/- "+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE51")+" "
							+ EnglishDecimalFormat.convert(sancAmount) +" "+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE52")+" "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE13") + " ________ " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE53")+" "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE14") + " " + sancAmount + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE16") ///+" " (sancAmount + ) 
							+" " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE17") + " " + lObjAdvDtls[5] + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE18") + " " + lObjAdvDtls[4] + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE19") + "_________"
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE20") + "___________"
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE21"));
					dataList.add(rowList);*/

					rowList = new ArrayList<Object>();
					/*rowList.add(space(40) + ((lObjAdvDtls[7] == null) ? "" : lObjAdvDtls[7]) + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE22") + " "
							+ lObjDateFormate.format(lObjAdvDtls[8]) + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE23"));*/
					rowList.add("2."+space(38)+MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE71"),empName , lStrDsgnName,lStrOfficeName,currDate+"/"+currMonth+"/"+currYear));
					dataList.add(rowList);
					}

					/*rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);*/

					/*
					 * rowList = new ArrayList<Object>(); rowList.add(new
					 * StyledData
					 * ("Through amount approved for withdrawal to Mr./Ms. " +
					 * empName +
					 * " is more than their 6 months salary, it is not more " +
					 * "than 90% of amount accumumlated in their A/C. Their Basic Monthly salary is Rs. "
					 * + basicSalary + "/- .", rowsFontsVO));
					 * dataList.add(rowList);
					 */

					// rowList = new ArrayList();
					// rowList.add(new StyledData("This is to certify that " +
					// retirementMonths
					// + " months are there for retirement of Mr./Ms./Mrs. " +
					// empName + " and he/she has "
					// + "completed " + serviceYears + " years  and " +
					// serviceMonths
					// +
					// " months of service. Following is the account balance as on "
					// + today + " in the "
					// + "account of Mr./Ms./Mrs. " + empName, rowsFontsVO));
					// dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("" + space(1));
					dataList.add(rowList);

					ArrayList<Object> sixRowsLeft = new ArrayList<Object>();

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GFP.SrNo"));
					rowList.add(gObjRsrcBndleForNRA.getString("GFP.Particulars"));
					rowList.add(gObjRsrcBndleForNRA.getString("GFP.Amount"));
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("1.");
					rowList.add(new StyledData(space(20)+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE24") + preFinYear
							+ " " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE25"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + OpeningBalance+"/-");
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("2.");
					rowList.add(new StyledData(space(20) + gObjRsrcBndleForRA.getString("GPF.ORDERLINE26") + " "
							+ lStrcurrFinYearCode + " "+gObjRsrcBndleForRA.getString("GPF.ORDERLINE27") + new DateFormatSymbols().getMonths()[lDtApplicDate.getMonth()]+
							" " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE28"),
							rowsFontsVO));
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + lObjGPFAccountBal[0]+"/-");
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("3.");
					rowList.add(new StyledData(space(20)+gObjRsrcBndleForRA.getString("GPF.ORDERLINE26") + " "
							+ lStrcurrFinYearCode + " "+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE27") + new DateFormatSymbols().getMonths()[lDtApplicDate.getMonth()]
							                                                                                                                   + " " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE29"),
							                                                                                                                   rowsFontsVO));
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + lObjGPFAccountBal[1]+"/-");
					sixRowsLeft.add(rowList);



					rowList = new ArrayList<Object>();
					rowList.add("4.");
					rowList.add(new StyledData(space(20)+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE30"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + total+"/-");
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("5.");
					rowList.add(new StyledData(space(20)+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE33")
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE36") + lStrcurrFinYearCode + " "
							+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE27") + new DateFormatSymbols().getMonths()[lDtApplicDate.getMonth()]
							                                                                                        + " " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE34"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + lDblWithdrawalSanc+"/-");
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("6.");
					rowList.add(new StyledData(space(20)+gObjRsrcBndleForRA.getString("GPF.ORDERLINE35"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (total - lDblWithdrawalSanc)+"/-");
					sixRowsLeft.add(rowList);

					td = new TabularData(sixRowsLeft);
					RptVo = reportsDao.getReport("800009", report.getLangId(), report.getLocId());
					ReportColumnVO[] lArrReportColumnVO = RptVo.getReportColumns();
					lArrReportColumnVO[0].setColumnWidth(10);
					lArrReportColumnVO[1].setColumnWidth(55);
					lArrReportColumnVO[2].setColumnWidth(35);
					(td).setRelatedReport(RptVo);
					//(td).setStyles(noBorder);
					rowList = new ArrayList<Object>();
					rowList.add(td);
					dataList.add(rowList);

					/*rowList = new ArrayList<Object>();
					rowList.add(space(40) + empName + " , " + lStrDsgnName+ " "+gObjRsrcBndleForRA.getString("GPF.ORDERLINE54") + " "
										+lStrOfficeName+" "+ gObjRsrcBndleForRA.getString("GPF.ORDERLINE55")+" _____________________________________ " 
										 +gObjRsrcBndleForRA.getString("GPF.ORDERLINE57")+" ______________________ " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE58") );
					dataList.add(rowList);*/
					rowList = null;

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.signature"), rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(emplyeeOfficerName, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(lStrEmployerDsgnName, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.MahaState")+lStrEmployerOfficeName, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(ddoOfcAdrs, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					/*rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);*/

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE40"));
					dataList.add(rowList);
					/*
					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE41"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE42")+"_____________________"+gObjRsrcBndleForRA.getString("GPF.ORDERLINE59")+",__________________");
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE60"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE44")+" ___________________ "+gObjRsrcBndleForRA.getString("GPF.ORDERLINE65"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE45")+" ______________________________________________ ");
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("_____________________________________________"+gObjRsrcBndleForRA.getString("GPF.ORDERLINE46"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE61")+" __________________________, "+gObjRsrcBndleForRA.getString("GPF.ORDERLINE62")+"__________"
							+gObjRsrcBndleForRA.getString("GPF.ORDERLINE63"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE64"));
					dataList.add(rowList);
					 */

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE72"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE73"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE74"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE75"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE76"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE77"));
					dataList.add(rowList);
					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE78"));
					dataList.add(rowList);


				} else if (reqType.equals("NRA")) {

					Long reqSactndAmount = sancAmount;
					sancAmount = lObjGPFRequestProcess.getAdvanceHistoryByAdvanceType(gpfAccNo,lIntFinYrId.longValue(),"NRA");

					Double total = Math.round(OpeningBalance) + Double.parseDouble(lObjGPFAccountBal[0].toString())
					+ Double.parseDouble(lObjGPFAccountBal[1].toString());

					if(sancAmount == null)
						sancAmount = 0l;
					gLogger.info("lStrPurposeOfNra "+lStrPurposeOfNra);
					//if(lStrPurposeOfNra != null && !lStrPurposeOfNra.equals("") && lStrPurposeOfNra.equals("800023")){

					report.setStyleList(noBorder);
					report.setStyleList(rowsFontsVO);
					ArrayList<Object> rowList = new ArrayList<Object>();

					rowList.add("" + space(1));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose1") ,
							CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(deptName, CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(lStrOfficeName, CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(ddoOfcAdrs, CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					if(ddoOfcTelNo != null)
						rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose6")+ ddoOfcTelNo, CenterBoldAlignVO));
					else 
						rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose6")+ "____________________", CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					if(ddoOfcEmail != null)
						rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose7")+ ddoOfcEmail, CenterBoldAlignVO) );
					else 
						rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose7")+ "_____________________", CenterBoldAlignVO) );
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose9"),CenterAlignVO));
					dataList.add(rowList);
					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose10")+" "+lStrDDOCodeOfLoggedInDDO+"/"+
							gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose45")+"-"+currYear+"/"+strEmpIntls+"/"+lStrTransactionId+", "
							+gObjRsrcBndleForNRA.getString("GPF.ORDERLINE6")+ currDate+"/"+currMonth+"/"+currYear ,CenterAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose11"),CenterAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose46"),empName, lStrDsgnName,currDate+"/"+currMonth+"/"+currYear),CenterAlignVO));
					dataList.add(rowList);



					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose12"),CenterBoldAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(space(40)+MessageFormat.format(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose13"),
							"______","_____",lStrEmployerDsgnName,empName,lStrDsgnName, (lStrBasicPay + lStrGradePay),gpfAccNo,lStrPurposeName,
							(total - lDblWithdrawalSanc),reqSactndAmount,EnglishDecimalFormat.convert(reqSactndAmount) ));


					/*+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose14")
								+"                "+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose17")+" "+
								lStrPurposeName+" "+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose18")+" "+gpfAccNo+" "
								+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose19")+reqSactndAmount+" "+
								gObjRsrcBndleForNRA.getString("GPF.ORDERLINE16")+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose20")
								+EnglishDecimalFormat.convert(reqSactndAmount)+" "+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose21")+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose22"));*/
					dataList.add(rowList);

					Double lDbInstallmentAmt = 0d;

					if(lObjAdvDtls[5] != null){
						lDbInstallmentAmt = Double.parseDouble(lObjAdvDtls[5].toString());
					}


					rowList = new ArrayList<Object>();
					rowList.add("2. " + space(38) + MessageFormat.format(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose47"),empName , lStrDsgnName,lStrOfficeName,currDate+"/"+currMonth+"/"+currYear));
					dataList.add(rowList);
					/*						rowList.add("2. " + space(38) + empName + " , " + lStrDsgnName +  " "
								+ gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose23") + "              " +
								 gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose24")+" "+basicAmount + " " +lStrGradePay
								+ gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose25")+" "+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose26"));
						dataList.add(rowList);
					 */
					rowList = new ArrayList<Object>();
					rowList.add("3. " + space(38) + gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose27") +" "+ empName+" "				
							+ gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose28") + " "+noOfYearsLeft +" "
							+ gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose29") + " "+noOfMonthLeft+" "
							+ gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose30") +noOfYearsServed+ " "+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose29") +noOfMonthServed+" "
							+gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose31"));


					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("4. " + space(38) + empName + " , " + lStrDsgnName +", " +lStrOfficeName+" "
							+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE24") + " "
							+ lObjDateFormate.format(lObjAdvDtls[8]) + " "
							+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE25"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					/*
					 * rowList = new ArrayList<Object>(); rowList.add(new
					 * StyledData
					 * ("Through amount approved for withdrawal to Mr./Ms. " +
					 * empName +
					 * " is more than their 6 months salary, it is not more " +
					 * "than 90% of amount accumumlated in their A/C. Their Basic Monthly salary is Rs. "
					 * + basicSalary + "/- .", rowsFontsVO));
					 * dataList.add(rowList);
					 */

					// rowList = new ArrayList();
					// rowList.add(new StyledData("This is to certify that " +
					// retirementMonths
					// + " months are there for retirement of Mr./Ms./Mrs. " +
					// empName + " and he/she has "
					// + "completed " + serviceYears + " years  and " +
					// serviceMonths
					// +
					// " months of service. Following is the account balance as on "
					// + today + " in the "
					// + "account of Mr./Ms./Mrs. " + empName, rowsFontsVO));
					// dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("" + space(1));
					dataList.add(rowList);

					ArrayList<Object> sixRowsLeft = new ArrayList<Object>();

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GFP.SrNo"));
					rowList.add(gObjRsrcBndleForNRA.getString("GFP.Particulars"));
					rowList.add(gObjRsrcBndleForNRA.getString("GFP.Amount"));
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("1. " );
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE26") + preFinYear
							+ " " + gObjRsrcBndleForNRA.getString("GPF.ORDERLINE27"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + OpeningBalance);
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("2. " );
					/*rowList.add(new StyledData( gObjRsrcBndleForNRA.getString("GPF.ORDERLINE28") + " " + lStrcurrFinYearCode + " "
								+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE29") + currDate + "/" + currMonth
								+ "/" + currYear + " " + gObjRsrcBndleForNRA.getString("GPF.ORDERLINE30"),
								rowsFontsVO));
						rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + lObjGPFAccountBal[0]);*/
					rowList.add(new StyledData( gObjRsrcBndleForNRA.getString("GPF.ORDERLINE28") + " " + lStrcurrFinYearCode + " "
							+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE29") +  new DateFormatSymbols().getMonths()[lDtApplicDate.getMonth()]
							                                                                                          +" " + gObjRsrcBndleForNRA.getString("GPF.ORDERLINE30"),
							                                                                                          rowsFontsVO));
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + lObjGPFAccountBal[0]);

					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("3. " );
					/*rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE31") + " " + lStrcurrFinYearCode + " "
								+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE29") + currDate + "/" + currMonth
								+ "/" + currYear + " " + gObjRsrcBndleForNRA.getString("GPF.ORDERLINE32"),
								rowsFontsVO));
						rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + lObjGPFAccountBal[1]);
					 */
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE31") + " " + lStrcurrFinYearCode + " "
							+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE29") + " "+new DateFormatSymbols().getMonths()[lDtApplicDate.getMonth()]+ " "+gObjRsrcBndleForNRA.getString("GPF.ORDERLINE32"),
							rowsFontsVO));
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + lObjGPFAccountBal[1]);

					sixRowsLeft.add(rowList);



					rowList = new ArrayList<Object>();
					rowList.add("4. " );
					rowList.add(new StyledData( gObjRsrcBndleForNRA.getString("GPF.ORDERLINE33"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + total);
					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("5. " );
					/*rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE31") + " "
								+ lStrcurrFinYearCode + " " + gObjRsrcBndleForNRA.getString("GPF.ORDERLINE29") + " "
								+ currDate + "/" + +currMonth + "/" + currYear + " "
								+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE34"), rowsFontsVO));
						rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + (lDblWithdrawalSanc - sancAmount));
					 */
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE31") + " "
							+ lStrcurrFinYearCode + " " + gObjRsrcBndleForNRA.getString("GPF.ORDERLINE29") + " "
							+new DateFormatSymbols().getMonths()[lDtApplicDate.getMonth() ]	+ " "
							+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE34"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + (lDblWithdrawalSanc - sancAmount));

					sixRowsLeft.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add("6. " );
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE35"), rowsFontsVO));
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE45") + " " + (total - lDblWithdrawalSanc));
					sixRowsLeft.add(rowList);

					td = new TabularData(sixRowsLeft);
					RptVo = reportsDao.getReport("800009", report.getLangId(), report.getLocId());
					ReportColumnVO[] lArrReportColumnVO = RptVo.getReportColumns();
					lArrReportColumnVO[0].setColumnWidth(5);
					lArrReportColumnVO[1].setColumnWidth(65);
					lArrReportColumnVO[2].setColumnWidth(35);
					(td).setRelatedReport(RptVo);
					(td).setStyles(noBorder);
					rowList = new ArrayList<Object>();
					rowList.add(td);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					/*rowList = new ArrayList<Object>();
						rowList.add("5. " + space(40) + empName + " , " + lStrDsgnName + " , " + lStrOfficeName + " "
								+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE10") + " " + lStrEmployerOfficeName + " "
								+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE36") + " " + lStrTreasuryName + " "
								+ gObjRsrcBndleForNRA.getString("GPF.ORDERLINE37"));
						dataList.add(rowList);
					 */
					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);


					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.signature"), rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(emplyeeOfficerName, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(lStrEmployerDsgnName, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForNRA.getString("GPF.MahaState")+lStrEmployerOfficeName, rightAlign));
					dataList.add(rowList);
					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(ddoOfcAdrs, rightAlign));
					dataList.add(rowList);
					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GFP.MarriagePurpose36"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE46"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE47"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE48"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE49"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE50"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE51"));
					dataList.add(rowList);
					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForNRA.getString("GPF.ORDERLINE52"));
					dataList.add(rowList);

					/*} else if (reqType.equals("NRA") && lStrDisbursementNo.equals("2")) {

						} else if (reqType.equals("NRA") && lStrDisbursementNo.equals("3")) {

						} else if (reqType.equals("NRA") && lStrDisbursementNo.equals("4")) {*/

					//}


				} else if (reqType.equals("FW")) {
					Date lDtDOR = lObjGPFRequestProcess.getEmpRetirmentDate(lStrSevaarthId);

					report.setStyleList(noBorder);
					report.setStyleList(rowsFontsVO);
					ArrayList<Object> rowList = new ArrayList<Object>();

					rowList.add("" + space(1));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForFW.getString("GPF.ORDERLINE1") + " " + lStrOrderId,
							boldVO));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE2"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE3"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE4"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE5"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE6") + " " + currDate + "/" + currMonth + "/"
							+ currYear + " ");
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(space(40) + empName + " , " + lStrDsgnName + " , " + lStrOfficeName + " "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE7") + " "
							+ ((lDtDOR == null ? "" : lObjDateFormate.format(lDtDOR))) + " "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE8") + " " + gpfAccNo + " "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE9") + "____" + " "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE10") + " " + sancAmount + " ("
							+ EnglishDecimalFormat.convert(sancAmount) + ") "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE11"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(space(40) + gObjRsrcBndleForFW.getString("GPF.ORDERLINE12") + " " + lStrDsgnName
							+ " , " + gObjRsrcBndleForFW.getString("GPF.ORDERLINE13") + " " + empName + " "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE14") + "____" + " "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE15"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(space(40) + gObjRsrcBndleForFW.getString("GPF.ORDERLINE16") + " Scheme Code ____" + " "
							+ gObjRsrcBndleForFW.getString("GPF.ORDERLINE17"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(lStrEmployerDsgnName, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(lStrEmployerOfficeName, rightAlign));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(newline);
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE18"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE19"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE20"));
					dataList.add(rowList);

					rowList = new ArrayList<Object>();
					rowList.add(gObjRsrcBndleForFW.getString("GPF.ORDERLINE21"));
					dataList.add(rowList);
				}

				if(chkForGeneratedBill(gpfAccNo, lStrTransactionId) == "N")
				{	
					inputMap.put("gpfAccNo", gpfAccNo);
					inputMap.put("transactionId", lStrTransactionId);
					inputMap.put("AdvanceType", reqType);
					inputMap.put("orderNo", lStrOrderId);
					inputMap.put("orderDate", lStrOrderDate);
					inputMap.put("openingBalc", OpeningBalance);
					inputMap.put("regularSub", Double.parseDouble(lObjGPFAccountBal[0].toString()));
					inputMap.put("advanceRecovery", Double.parseDouble(lObjGPFAccountBal[1].toString()));
					inputMap.put("advanceSanctioned", lDblWithdrawalSanc);
					inputMap.put("lObjLoginVO", lObjLoginVO);

					ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
					inputMap.put("serviceLocator", serviceLocator);

					resultObject = serviceLocator.executeService("GenerateBillDataGPF",inputMap);					
				}
			}

		} catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		} finally {
			try {
				if (smt != null) {
					smt.close();
				}

				if (rs != null) {
					rs.close();
				}

				if (con != null) {
					con.close();
				}

				smt = null;
				rs = null;
				con = null;

			} catch (Exception e1) {
				gLogger.error("Exception :" + e1, e1);

			}
		}
		return dataList;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public String chkForGeneratedBill(String lStrGpfAccNo, String lStrTransactionId)
	{
		String lStrChkBill = "";
		List lLstData = null;

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT BILL_DTLS_ID FROM MST_GPF_BILL_DTLS ");
			lSBQuery.append("WHERE GPF_ACC_NO = :gpfAccNo AND TRANSACTION_ID = :transactionId ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("transactionId", lStrTransactionId);
			lLstData = lQuery.list();

			if(lLstData != null && lLstData.size() > 0){
				lStrChkBill = "Y";
			}else{
				lStrChkBill = "N";
			}
		}catch (Exception e) {
			gLogger.error("Exception in chkForGeneratedBill:" + e, e);
		}

		return lStrChkBill;
	}

	/*public Date getDateDifference(Date date1, Date date2){
		Date date = new Date();
		long timeInMillA = 0;
		long timeInMillB = 0;
		Date convertedDateA;
		Date convertedDateB;

		Calendar cal = Calendar.getInstance();		          
		cal.setTime(date1);
		timeInMillA = cal.getTimeInMillis();

		cal.setTime(date);
		timeInMillB = cal.getTimeInMillis();

		 LocalDateTime startA = new LocalDateTime(timeInMillA);
		    LocalDateTime startB = new LocalDateTime(timeInMillB);

		    Period difference = new Period(startA, startB, PeriodType.days());
		    int day = difference.getDays();

		    difference = new Period(startA, startB, PeriodType.months());
		    int month = difference.getMonths();

		    difference = new Period(startA, startB, PeriodType.years());
		    int year = difference.getYears();

		    difference = new Period(startA, startB, PeriodType.weeks());
		    int week = difference.getWeeks();

		    difference = new Period(startA, startB, PeriodType.hours());
		    int hour = difference.getHours();

		    difference = new Period(startA, startB, PeriodType.minutes());
		    long min = difference.getMinutes();

		    difference = new Period(startA, startB, PeriodType.seconds());
		    long sec = difference.getSeconds();

		    //difference = new Period(startA, startB, PeriodType.millis());
		    long mili = timeInMillB - timeInMillA; 
		return date;
	}*/

}
