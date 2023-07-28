
package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jfree.util.StringUtils;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.PostEmpContriDAO;
import com.tcs.sgv.dcps.dao.PostEmpContriDAOImpl;
import com.tcs.sgv.dcps.dao.SancBudgetDAO;
import com.tcs.sgv.dcps.dao.SancBudgetDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;
import java.text.DateFormatSymbols;
/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Feb 20, 2014
 */
public class NsdlEmpWiseReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
	.getLogger(DCPSEmployeeDetailReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	static ResourceBundle lBudConstantsBundle = ResourceBundle.getBundle(
			"resources/dcps/dcpsLabels", Locale.getDefault());
	Session ghibSession = null;	

	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException {

		report.getLangId();

		report.getLocId();
		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		TabularData td = null;
		TabularData td1 = null;
		TabularData td2 = null;
		TabularData td3 = null;
		TabularData td4 = null;
		TabularData td5 = null;

		ArrayList dataList = new ArrayList();
		ReportVO RptVo = null;
		ReportsDAO reportsDao = null;
		reportsDao = new ReportsDAOImpl();

		StyleVO[] normalFont = new StyleVO[2];
		normalFont[0] = new StyleVO();
		normalFont[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		normalFont[0].setStyleValue("8");
		normalFont[1] = new StyleVO();
		normalFont[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		normalFont[1].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

		StyleVO[] smallItalic = new StyleVO[3];
		smallItalic[0] = new StyleVO();
		smallItalic[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		smallItalic[0].setStyleValue("6");
		smallItalic[1] = new StyleVO();
		smallItalic[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		smallItalic[1].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		smallItalic[2] = new StyleVO();
		smallItalic[2].setStyleId(IReportConstants.STYLE_FONT_STYLE);
		smallItalic[2].setStyleValue(IReportConstants.VALUE_FONT_STYLE_ITALIC);

		StyleVO[] boldFontLeftAlign = new StyleVO[3];
		boldFontLeftAlign[0] = new StyleVO();
		boldFontLeftAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldFontLeftAlign[0].setStyleValue("8");
		boldFontLeftAlign[1] = new StyleVO();
		boldFontLeftAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldFontLeftAlign[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldFontLeftAlign[2] = new StyleVO();
		boldFontLeftAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		boldFontLeftAlign[2] .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

		StyleVO[] boldFontRightAlign = new StyleVO[3];
		boldFontRightAlign[0] = new StyleVO();
		boldFontRightAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldFontRightAlign[0].setStyleValue("8");
		boldFontRightAlign[1] = new StyleVO();
		boldFontRightAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldFontRightAlign[1] .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldFontRightAlign[2] = new StyleVO();
		boldFontRightAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		boldFontRightAlign[2]
		                   .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

		StyleVO[] boldAndBigFont = new StyleVO[2];
		boldAndBigFont[0] = new StyleVO();
		boldAndBigFont[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldAndBigFont[0].setStyleValue("12");
		boldAndBigFont[1] = new StyleVO();
		boldAndBigFont[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldAndBigFont[1] .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);

		StyleVO[] boldAndBigFontCenterAlign = new StyleVO[3];
		boldAndBigFontCenterAlign[0] = new StyleVO();
		boldAndBigFontCenterAlign[0]
		                          .setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldAndBigFontCenterAlign[0].setStyleValue("12");
		boldAndBigFontCenterAlign[1] = new StyleVO();
		boldAndBigFontCenterAlign[1]
		                          .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldAndBigFontCenterAlign[1]
		                          .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldAndBigFontCenterAlign[2] = new StyleVO();
		boldAndBigFontCenterAlign[2]
		                          .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		boldAndBigFontCenterAlign[2]
		                          .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);

		StyleVO[] boldFontCenterAlign = new StyleVO[3];
		boldFontCenterAlign[0] = new StyleVO();
		boldFontCenterAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldFontCenterAlign[0].setStyleValue("8");
		boldFontCenterAlign[1] = new StyleVO();
		boldFontCenterAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldFontCenterAlign[1]
		                    .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldFontCenterAlign[2] = new StyleVO();
		boldFontCenterAlign[2]
		                    .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		boldFontCenterAlign[2]
		                    .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);

		StyleVO[] normalFontCenterAlign = new StyleVO[2];
		normalFontCenterAlign[0] = new StyleVO();
		normalFontCenterAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		normalFontCenterAlign[0].setStyleValue("8");
		normalFontCenterAlign[1] = new StyleVO();
		normalFontCenterAlign[1]
		                      .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		normalFontCenterAlign[1]
		                      .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);

		StyleVO[] normalFontRightAlign = new StyleVO[2];
		normalFontRightAlign[0] = new StyleVO();
		normalFontRightAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		normalFontRightAlign[0].setStyleValue("8");
		normalFontRightAlign[1] = new StyleVO();
		normalFontRightAlign[1]
		                     .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		normalFontRightAlign[1]
		                     .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

		StyleVO[] noBorder = new StyleVO[4];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		noBorder[1] = new StyleVO();
		noBorder[1].setStyleId(IReportConstants.ROWS_PER_PAGE);
		noBorder[1].setStyleValue("40");
		noBorder[2] = new StyleVO();
		noBorder[2].setStyleId(IReportConstants.NO_HEADER);
		noBorder[2].setStyleValue(IReportConstants.VALUE_YES);
		noBorder[3] = new StyleVO();
		noBorder[3].setStyleId(IReportConstants.NO_FOOTER);
		noBorder[3].setStyleValue(IReportConstants.VALUE_YES);

		StyleVO[] noBorderAndSelfClose = new StyleVO[5];
		noBorderAndSelfClose[0] = new StyleVO();
		noBorderAndSelfClose[0].setStyleId(IReportConstants.BORDER);
		noBorderAndSelfClose[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		noBorderAndSelfClose[1] = new StyleVO();
		noBorderAndSelfClose[1].setStyleId(IReportConstants.ROWS_PER_PAGE);
		noBorderAndSelfClose[1].setStyleValue("70");
		noBorderAndSelfClose[2] = new StyleVO();
		noBorderAndSelfClose[2].setStyleId(IReportConstants.NO_HEADER);
		noBorderAndSelfClose[2].setStyleValue(IReportConstants.VALUE_YES);
		noBorderAndSelfClose[3] = new StyleVO();
		noBorderAndSelfClose[3].setStyleId(IReportConstants.NO_FOOTER);
		noBorderAndSelfClose[3].setStyleValue(IReportConstants.VALUE_YES);
		noBorderAndSelfClose[4] = new StyleVO();
		noBorderAndSelfClose[4].setStyleId(26);
		noBorderAndSelfClose[4].setStyleValue("JavaScript:self.close()");

		String lStrNewLine = StringUtils.getLineSeparator();

		try {

			requestAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes
			.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			ghibSession = lObjSessionFactory.getCurrentSession();

			Map requestAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes
			.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator
			.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			loginDetail.get("locationId");

			new StringBuffer();
			if (report.getReportCode().equals("8000083")) {

				report.setStyleList(noBorderAndSelfClose);

				String lStrPostEmpContriId = null;
				Long lLongPostEmpContriId = null;
				String strAcDcpsMntndBy=null;

				// Added by ashish for showing accountmaintained by and bill generation date

				lStrPostEmpContriId = (String) report
				.getParameterValue("postEmpContriPkId");

				strAcDcpsMntndBy =  (String) report.getParameterValue("dcpsAcntMntndBy");

				if (!"".equals(lStrPostEmpContriId)) {
					lLongPostEmpContriId = Long.valueOf(lStrPostEmpContriId);
				}
				String finYear =null;

				Date createdDt=null;

				PostEmpContriDAO objPostEmpContriDAO = new PostEmpContriDAOImpl(
						PostEmpContri.class, serviceLocator.getSessionFactory());
				SancBudgetDAO lObjSancBudgetDAO = new SancBudgetDAOImpl(null,
						serviceLocator.getSessionFactory());

				PostEmpContri lObjPostEmpContri = (PostEmpContri) objPostEmpContriDAO
				.read(lLongPostEmpContriId);

				finYear=objPostEmpContriDAO.getFinYear(lStrPostEmpContriId,strAcDcpsMntndBy);

				createdDt=objPostEmpContriDAO.getBillGendt(lStrPostEmpContriId,strAcDcpsMntndBy);

				// Ended by ashish for showing accountmaintained by and bill generation date				

				ArrayList rowList = new ArrayList();

				/*
				ArrayList firstThreeRowsLeft = new ArrayList();


				rowList = new ArrayList();
				rowList.add(new StyledData("TreasuryCode", boldFontLeftAlign));
				rowList.add(new StyledData(": 7101", boldFontLeftAlign));
				firstThreeRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("DDO Code", boldFontLeftAlign));
				rowList.add(new StyledData(": 7101003272", boldFontLeftAlign));
				firstThreeRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Scheme Code", boldFontLeftAlign));
				rowList.add(new StyledData(": 27010642", boldFontLeftAlign));
				firstThreeRowsLeft.add(rowList);


				td = new TabularData(firstThreeRowsLeft);
				RptVo = reportsDao.getReport("700033", report.getLangId(),
						report.getLocId()); // A 2 column Report
				ReportColumnVO[] lArrReportColumnVO = RptVo.getReportColumns();
				lArrReportColumnVO[0].setColumnWidth(20);
				lArrReportColumnVO[1].setColumnWidth(80);
				(td).setRelatedReport(RptVo);
				(td).setStyles(noBorder);
				rowList = new ArrayList();
				rowList.add(td);
				dataList.add(rowList);

				 */

				ReportColumnVO[] lArrReportColumnVO = null;


				rowList = new ArrayList();
				rowList.add(new StyledData("(FORM M.T.R. 45-A)",boldFontCenterAlign));
				dataList.add(rowList);


				rowList = new ArrayList();
				rowList.add(new StyledData("(See Rule 406-A of M.T.R.)",boldFontCenterAlign));
				dataList.add(rowList);



				rowList = new ArrayList();
				rowList.add(new StyledData("Simple Receipt",
						boldFontCenterAlign));
				dataList.add(rowList);


				if(strAcDcpsMntndBy.equals("700240"))
				{
					rowList = new ArrayList();
					rowList.add(new StyledData("FORM M.T.R. 45-A for IAS",boldAndBigFontCenterAlign));
					dataList.add(rowList);
				}
				if(strAcDcpsMntndBy.equals("700241"))
				{
					rowList = new ArrayList();
					rowList.add(new StyledData("FORM M.T.R. 45-A for IPS",boldAndBigFontCenterAlign));
					dataList.add(rowList);
				}
				if(strAcDcpsMntndBy.equals("700242"))
				{
					rowList = new ArrayList();
					rowList.add(new StyledData("FORM M.T.R. 45-A for IFS",boldAndBigFontCenterAlign));
					dataList.add(rowList);
				}

				/*	rowList = new ArrayList();
				rowList.add(new StyledData("(See Rule 406-A)",
						boldFontCenterAlign));
				dataList.add(rowList);
				 */






				ArrayList fourRowsInMiddle = new ArrayList();

				rowList = new ArrayList();
				rowList.add(new StyledData(" --------------------------------------- ",boldFontLeftAlign));
				rowList.add(new StyledData(" <b> Name of Office </b>: State Recoed Keeping Agenecy, Defined Contribution Pension Scheme ",normalFont));
				rowList.add(new StyledData(" Directorate of Accounts and Tresuries,Maharashtra State,Mumbai. ",normalFont));
				rowList.add(new StyledData(" --------------------------------------- ",boldFontLeftAlign));
				fourRowsInMiddle.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Treasury Name", boldFontLeftAlign));
				rowList.add(new StyledData(": Pay & Accounts Office Mumbai-7101",boldFontLeftAlign));
				rowList.add(new StyledData("Bill No", boldFontRightAlign));
				rowList.add(new StyledData(": "+ lObjPostEmpContri.getBillNo(),boldFontLeftAlign));
				fourRowsInMiddle.add(rowList);

				String lStrCurrDate = null;
				if(createdDt!=null ){
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					lStrCurrDate=sdf.format(createdDt);

				}
				else
				{
					Date lDtCurrdate = SessionHelper.getCurDate();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

					if (lDtCurrdate != null) {
						lStrCurrDate = sdf.format(lDtCurrdate);
					}
				}



				rowList = new ArrayList();
				rowList.add(new StyledData("Token No", boldFontLeftAlign));
				rowList.add(new StyledData(": ", boldFontLeftAlign));
				rowList.add(new StyledData("Date", boldFontRightAlign));
				rowList.add(new StyledData(": " + lStrCurrDate,boldFontLeftAlign));
				fourRowsInMiddle.add(rowList);








				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
				String lStrYearCode = lObjDcpsCommonDAO
				.getYearCodeForYearId(lObjPostEmpContri.getFinYear());
				//String lStrMonth = lObjDcpsCommonDAO.getMonthForId(Long.valueOf(lObjPostEmpContri.getContriMonth()));

				//Long lLongMonth = Long.valueOf(lObjPostEmpContri.getContriMonth());
				Long lLongYearCode = Long.valueOf(lStrYearCode);

				/*
				if(lLongMonth == 1 || lLongMonth == 2 || lLongMonth == 3)
				{
					lLongYearCode = lLongYearCode + 1;
				}
				 */

				lStrYearCode = lLongYearCode.toString();

				String [] yearSpl=lStrCurrDate.split("/");
				String ddFormat=yearSpl[0];
				String monFormat=yearSpl[1];
				String yrFormat=yearSpl[2];



				String mon= new DateFormatSymbols().getMonths()[Integer.parseInt(monFormat)-1];


				rowList = new ArrayList();
				rowList.add(new StyledData("Voucher No", boldFontLeftAlign));
				rowList.add(new StyledData(": ", boldFontLeftAlign));
				rowList.add(new StyledData("Month/Year", boldFontRightAlign));
				rowList.add(new StyledData(": " + space(2)+ mon+"/"+yrFormat, boldFontLeftAlign));
				fourRowsInMiddle.add(rowList);

				//------Start by ashish for month and year-----------				

				rowList = new ArrayList();
				rowList.add(new StyledData("Voucher Date", boldFontLeftAlign));
				rowList.add(new StyledData(": ", boldFontLeftAlign));
				fourRowsInMiddle.add(rowList);

				//------ended by ashish for month and year-----------				

				td = new TabularData(fourRowsInMiddle);
				RptVo = reportsDao.getReport("8000084", report.getLangId(),
						report.getLocId()); // A 4 column Report
				lArrReportColumnVO = RptVo.getReportColumns();
				lArrReportColumnVO[0].setColumnWidth(20);
				lArrReportColumnVO[1].setColumnWidth(50);
				lArrReportColumnVO[2].setColumnWidth(15);
				lArrReportColumnVO[3].setColumnWidth(15);
				(td).setRelatedReport(RptVo);
				(td).setStyles(noBorder);
				rowList = new ArrayList();
				rowList.add(td);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("HEAD OF ACCOUNT",boldAndBigFontCenterAlign));
				dataList.add(rowList);

				ArrayList sevenRowsLeft = new ArrayList();

				rowList = new ArrayList();
				rowList.add(new StyledData("Administrative Department",boldFontLeftAlign));
				rowList.add(new StyledData(" K DEPOSITS AND ADVANCES", boldFontRightAlign));
				sevenRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Demand No", boldFontLeftAlign));
				//rowList.add(new StyledData(": G-6", boldFontLeftAlign));
				sevenRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Major Head", boldFontLeftAlign));
				rowList.add(new StyledData(": 8342 - OTHER DEPOSITS",boldFontRightAlign));
				sevenRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Minor Head", boldFontLeftAlign));
				rowList.add(new StyledData(": 00, 117 Defined Contribution Pension Scheme", boldFontRightAlign));
				sevenRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Sub Head", boldFontLeftAlign));
				rowList.add(new StyledData(": (04) (01) Defined Contribution Pension Scheme",boldFontRightAlign));
				sevenRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Detailed Head", boldFontLeftAlign));
				rowList.add(new StyledData(":  Government Servants Contribution(IAS)Tier 1 (83420103) ",boldFontRightAlign));
				sevenRowsLeft.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("", boldFontLeftAlign));



				/*	rowList.add(new StyledData(
						": (01)(04) Pensionary Charges (20710642)",
						normalFont));
				sevenRowsLeft.add(rowList);*/

				td = new TabularData(sevenRowsLeft);
				RptVo = reportsDao.getReport("8000085", report.getLangId(),
						report.getLocId()); // A 2 column Report
				lArrReportColumnVO = RptVo.getReportColumns();
				lArrReportColumnVO[0].setColumnWidth(25);
				lArrReportColumnVO[1].setColumnWidth(75);
				(td).setRelatedReport(RptVo);
				(td).setStyles(noBorder);
				rowList = new ArrayList();
				rowList.add(td);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						" Object of expenditure :  Disbursement of Grants on Account of Employees Contribution ",normalFont));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"(For Government Employees)(AIS) Under Tier 1 Payable to Trustee Bank ",normalFont));
				dataList.add(rowList);

				Long lFloatBillAmount = lObjPostEmpContri.getBillAmount();
				String lStrBillAmountInWords = EnglishDecimalFormat
				.convertWithSpace(new BigDecimal(lFloatBillAmount));

				rowList = new ArrayList();
				rowList.add(new StyledData(
						" Received Rs. "
						+ lFloatBillAmount
						+ "/-", boldFontLeftAlign));

				rowList.add(new StyledData("(Rupees In words: "
						+ lStrBillAmountInWords + " )", normalFont));
				dataList.add(rowList);




				rowList = new ArrayList();
				rowList.add(new StyledData("Sanctioned by State Record Keeping Agency,Defined Contribution Pension Scheme",normalFontCenterAlign));
				dataList.add(rowList);

				ArrayList threeRowsAndTwoColumnsInTheEnd = new ArrayList();





				// Long lLongSancBudget = lObjSancBudgetDAO.getTotalBudget(lObjPostEmpContri.getFinYear(),dcpsSancBudgetOrgId);
				//Long lLongExpenditure = objPostEmpContriDAO.getExpenditure(lObjPostEmpContri.getFinYear(),strAcDcpsMntndBy);

				//Long lLongBalance = lLongSancBudget - lLongExpenditure;

				//----ended by ashish for distinguish account maintained by 	

				/*	String lStrBalanceSign = " ";
				if(lLongBalance < 0l)
				{
					lStrBalanceSign = "(-)";
					lLongBalance = -lLongBalance;
				}

				rowList = new ArrayList();
				rowList.add(new StyledData("Sanctioned Budget(Rs.)",normalFont));
				rowList.add(new StyledData(": " + lLongSancBudget + "/-"+ space(10) + "Amount (Plan/Non-Plan)",boldFontLeftAlign));
				threeRowsAndTwoColumnsInTheEnd.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Expenditure including this bill(Rs.)", normalFont));
				rowList.add(new StyledData(": " + lLongExpenditure + "/-",
						boldFontLeftAlign));
				threeRowsAndTwoColumnsInTheEnd.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Balance Grant(Rs.)", normalFont));
				rowList.add(new StyledData(": " + lStrBalanceSign + lLongBalance + "/-",
						boldFontLeftAlign));
				threeRowsAndTwoColumnsInTheEnd.add(rowList);*/

				td = new TabularData(threeRowsAndTwoColumnsInTheEnd);
				RptVo = reportsDao.getReport("8000085", report.getLangId(),
						report.getLocId()); // A 2 column Report
				lArrReportColumnVO = RptVo.getReportColumns();
				lArrReportColumnVO[0].setColumnWidth(30);
				lArrReportColumnVO[1].setColumnWidth(70);
				(td).setRelatedReport(RptVo);
				(td).setStyles(noBorder);
				rowList = new ArrayList();
				rowList.add(td);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"On Account  of Disbursement of Grants on Account of Employees Contribution (For Government Employees)(AIS)",
						normalFont));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Under Tier 1 payable  to trustee Bank.In accordance with authority given by the Government of Maharashtra,",
						normalFont));
				dataList.add(rowList);




				rowList = new ArrayList();
				rowList.add(new StyledData(
						"General Administrative Department,GR No. IAS/2506/C.R 360/06/09 Dated 15/02/2013",
						normalFont));
				dataList.add(rowList);



				rowList = new ArrayList();
				rowList.add(new StyledData(
						" Received Rs. "
						+ lFloatBillAmount
						+ "/-", boldFontLeftAlign));

				rowList.add(new StyledData("(Rupees In words: "
						+ lStrBillAmountInWords + " )", normalFont));
				dataList.add(rowList);


				rowList = new ArrayList();
				rowList.add(new StyledData("CERTIFICATE",boldFontCenterAlign));
				dataList.add(rowList);



				rowList = new ArrayList();
				rowList.add(new StyledData(
						"As per Sr.No. 22 of Government of Maharashtra,General Administration Department,GR"
						+"No. IAS/2506/C.R. 360/06/09 Dated 15/02/2013,it is certified that the amount of"
						+"Disbursement of Grants on Account of Employees's Contribution(For Government Employees)(AIS)"
						+"Under Tier 1 payable to Trustee Bank under New Pension Scheme is Rs. "
						+ lFloatBillAmount
						+ "/-"						
						+"which is correct/Exact as per  this office  Record.",normalFont));
				dataList.add(rowList);


				rowList = new ArrayList();
				rowList.add(new StyledData("Please pay electronically to:",
						normalFontRightAlign));
				dataList.add(rowList);

				
				rowList = new ArrayList();
				rowList.add(new StyledData("Axis Bank :",
						normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("IFSC Code :",
						normalFontRightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Account Number:",
						normalFontRightAlign));
				dataList.add(rowList);
				
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Transaction Id:",
						normalFontRightAlign));
				dataList.add(rowList);


				rowList = new ArrayList();
				rowList.add(new StyledData("Signature & Designation",
						normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Deputy Director",
						normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("State Record Keeping Agency",
						normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Defined Contribution Pension Scheme",
						normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Maharashtra State,Mumbai",
						normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("FOR USE IN TREASURY",
						boldAndBigFontCenterAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Pay Rs.: " + space(100)
						+ "In Word Rs.-", normalFont));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Date:", normalFont));
				dataList.add(rowList);

			}
		} catch (Exception e) {
			e.printStackTrace();
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
				e1.printStackTrace();
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

	

}
