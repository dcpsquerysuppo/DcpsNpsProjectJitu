package com.tcs.sgv.dcps.report;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.lang.*;
import java.math.BigDecimal;
import java.util.*;
import java.io.*;
import java.math.*;

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

import antlr.StringUtils;

import com.ibm.icu.text.DateFormat;
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
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.RltLevelRole;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.dao.GPFWorkFlowConfigDAO;
import com.tcs.sgv.gpf.dao.GPFWorkFlowConfigDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import java.text.MessageFormat;
/*import org.joda.time.LocalDateTime;
import org.joda.time.Period;dis

import org.joda.time.PeriodType;*/
public class TerminalForm2Report extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(TerminalForm2Report.class);
	public static String newline = System.getProperty("line.separator");
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	Map lMapSessionAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Session ghibSession = null;
	LoginDetails lObjLoginVO = null;
	String gStrLocCode = null;

	private ServiceLocator serv = null;

	private ResourceBundle gObjRsrcBndleForRA = ResourceBundle.getBundle("resources/gpf/RefundableOrderMarathi");


	private ResourceBundle gObjRsrcBndleForNRA = ResourceBundle.getBundle("resources/gpf/NonRefundableOrderMarathi");

	private ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/gpf/90PercentWithdrawalOrderMarathi");


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


		/*lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
		serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
		CmnLocationMstDst = (CmnLocationMst) lMapRequestAttributes.get("CmnLocationMstDst");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();*/
		gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
		/*ghibSession = lObjSessionFactory.getCurrentSession();
		gDtCurrDate = DBUtility.getCurrentDateFromDB();*/


		List<OrgPostMst> lLngPostIdList = (List) loginDetail.get("postIdList");
		OrgPostMst lObjPostMst = lLngPostIdList.get(0);
		Long lLngPostId = lObjPostMst.getPostId();

		Statement smt = null;
		ResultSet rs = null;
		TabularData td = null;
		ReportVO RptVo = null;
		MessageFormat msgFormatter = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();
		String SpecialCase90="";
		String lStrEmpNameMarathi="";
		int AG=0;
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
			
			
			
			
			StyleVO[] leftUnderlineBold = new StyleVO[4];
			leftUnderlineBold[0] = new StyleVO();
			leftUnderlineBold[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftUnderlineBold[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			leftUnderlineBold[1] = new StyleVO();
			leftUnderlineBold[1].setStyleId(IReportConstants.STYLE_TEXT_DECORATION);
			leftUnderlineBold[1].setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
			leftUnderlineBold[2] = new StyleVO();
			leftUnderlineBold[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			leftUnderlineBold[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			leftUnderlineBold[3] = new StyleVO();
			leftUnderlineBold[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			leftUnderlineBold[3].setStyleValue("14");

			
			//for
			StyleVO[] rightAlign = new StyleVO[2];
			rightAlign[0] = new StyleVO();
			rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			rightAlign[1] = new StyleVO();
			rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightAlign[1].setStyleValue("12");

			StyleVO[] leftAlign = new StyleVO[2];
			leftAlign[0] = new StyleVO();
			leftAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			leftAlign[1] = new StyleVO();
			leftAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			leftAlign[1].setStyleValue("12");

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


			
			StyleVO[] leftBoldAlignVO = new StyleVO[4];
			leftBoldAlignVO[0] = new StyleVO();
			leftBoldAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftBoldAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			leftBoldAlignVO[1] = new StyleVO();
			leftBoldAlignVO[1].setStyleId(IReportConstants.BORDER);
			leftBoldAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			leftBoldAlignVO[2] = new StyleVO();
			//CenterBoldAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			//	CenterBoldAlignVO[2].setStyleValue("12");
			leftBoldAlignVO[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			leftBoldAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			leftBoldAlignVO[3] = new StyleVO();
			leftBoldAlignVO[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			leftBoldAlignVO[3].setStyleValue("14");
			
			StyleVO[] rightBoldAlignVO = new StyleVO[4];
			rightBoldAlignVO[0] = new StyleVO();
			rightBoldAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightBoldAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			rightBoldAlignVO[1] = new StyleVO();
			rightBoldAlignVO[1].setStyleId(IReportConstants.BORDER);
			rightBoldAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			rightBoldAlignVO[2] = new StyleVO();
			rightBoldAlignVO[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			rightBoldAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			rightBoldAlignVO[3] = new StyleVO();
			rightBoldAlignVO[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightBoldAlignVO[3].setStyleValue("14");
			
			
			
			//ADDED BY Kiranvir
			StyleVO[] AadeshVO = new StyleVO[4];
			AadeshVO[0] = new StyleVO();
			AadeshVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			AadeshVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			AadeshVO[1] = new StyleVO();
			AadeshVO[1].setStyleId(IReportConstants.BORDER);
			AadeshVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			AadeshVO[3] = new StyleVO();
			AadeshVO[3].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			AadeshVO[3].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			AadeshVO[2] = new StyleVO();
			AadeshVO[2].setStyleId(IReportConstants.FONT_SIZE);
			AadeshVO[2].setStyleValue("20");



			StyleVO[] SixMonthsVO = new StyleVO[4];
			SixMonthsVO[0] = new StyleVO();
			SixMonthsVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			SixMonthsVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);


			StyleVO[] leftHeader = new StyleVO[3];
			leftHeader[0] = new StyleVO();
			leftHeader[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			leftHeader[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD); 
			leftHeader[1] = new StyleVO();
			leftHeader[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			leftHeader[1].setStyleValue("11"); 
			leftHeader[2] = new StyleVO();
			leftHeader[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftHeader[2].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

			StyleVO[] rightHead = new StyleVO[3];
			rightHead[0] = new StyleVO();
			rightHead[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			rightHead[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD); 
			rightHead[1] = new StyleVO();
			rightHead[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightHead[1].setStyleValue("11"); 
			rightHead[2] = new StyleVO();
			rightHead[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightHead[2].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

			
			StyleVO[] boldStyleVO = new StyleVO[1];
			boldStyleVO[0] = new StyleVO();
			boldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldStyleVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER); 
			
			
			if (report.getReportCode().equals("9000354")) {
				gLogger.error("inside 9000353 NRA");
				String reqType = (String) report.getParameterValue("reqType");



				gLogger.error("reqType:::::::"+reqType);
				gLogger.error("inside 9000353 NRA");

				GPFRequestProcessDAO lObjGPFRequestProcess = new GPFRequestProcessDAOImpl(null, serviceLocator.getSessionFactory());
				GPFAdvanceProcessDAOImpl lObjGPFAdvanceProcessDAOImpl = new GPFAdvanceProcessDAOImpl(null, serviceLocator.getSessionFactory());

				String gpfAccNo = (String) report.getParameterValue("gpfAccNo");
				String lStrPurposeOfNra = (String) request.getParameter("purposeOfNra");

				int lStrDisbursementNumber=0;

				String lStrTransactionId = (String) report.getParameterValue("transactionId");
				String lStrDDOCODE123 = (String) report.getParameterValue("ddocode");
				String lStrDisbursementNo = "1";

				if (reqType.startsWith("Non-Refundable")) {
					lStrDisbursementNo = reqType.substring(3);
					reqType = "NRA";
					SpecialCase90 = (String) report.getParameterValue("specialcase90");
					gLogger.error("SpecialCase90:::::::"+SpecialCase90+"ggggggggg:::::::::"+lStrPurposeOfNra);

					lStrDisbursementNumber=lObjGPFAdvanceProcessDAOImpl.getDisNumberRemaingingForParticulrSubtypeNew(gpfAccNo,lStrTransactionId);

					gLogger.error("lStrDisbursementNumber***************"+lStrDisbursementNumber);


				}
				SpecialCase90 = (String) report.getParameterValue("specialcase90");
				gLogger.error("SpecialCase90:::::::"+SpecialCase90);
				if (reqType.equalsIgnoreCase("Refundable")) {

					reqType = "RA";
				}

				gLogger.error("reqType***************"+reqType);




				//gLogger.error("lStrTransactionId"+lStrTransactionId);
				String lStrOrderId = (String) report.getParameterValue("orderNo");
				gLogger.error("lStrOrderId"+lStrOrderId);


				String lStrSevaarthId = (String) report.getParameterValue("sevaarthId");
				gLogger.error("lStrSevaarthId"+lStrSevaarthId);


				String lStrGender=lObjGPFRequestProcess.getGenderOfEmployee(lStrSevaarthId);
				gLogger.error("lStrGender is::::::::::"+lStrGender);

				if(lStrGender.equalsIgnoreCase("M"))
				{
					gLogger.error("lStrGender is::::::::::"+lStrGender);
				}

				lStrTransactionId = (String) report.getParameterValue("transactionId");
				lStrDDOCODE123 = (String) report.getParameterValue("ddocode");

				gLogger.error("lStrDDOCODE*****************"+lStrDDOCODE123);




				Long sancAmount = 0L;
				if (report.getParameterValue("sancAmount") != "" && report.getParameterValue("sancAmount") != null) {
					sancAmount = Math.round(Double.parseDouble((String) report.getParameterValue("sancAmount")));
					gLogger.error("sancAmount1"+sancAmount);



				}


				Double d = Double.parseDouble((String) report.getParameterValue("sancAmount"));
				int sanctionedAmount = (int)d.doubleValue();

				gLogger.error("sm"+sanctionedAmount);
				String empName = (String) report.getParameterValue("empName");

				String designName  = "";

				designName = lObjGPFRequestProcess.getDesignName(lLngPostId);

				//code added for employee initials by kiranvir on may 21
				List loanApproveDtAdvType=null;
				String typeOfAdvance ="";
				String strEmpIntls = "";
				String lStrFirstname = "";
				String lStrMiddlename = "";
				String lStrLastname = "";
				String puposeOfAdv="";
				String ruleID="";	
				String loanApprovedDate="";
				Object Obj1[]=null;

				if(reqType.equalsIgnoreCase("Final") || reqType.equalsIgnoreCase("FW") )
				{
					gLogger.info("in Req Final:::::::");
					loanApproveDtAdvType = lObjGPFRequestProcess.gettypeOfAdv1(lStrTransactionId);
				}
				else
				{
					loanApproveDtAdvType = lObjGPFRequestProcess.gettypeOfAdv(lStrTransactionId);
				}

				if(loanApproveDtAdvType!=null && !loanApproveDtAdvType.isEmpty()){
					Iterator it =  loanApproveDtAdvType.iterator();
					while (it.hasNext()) {
						Obj1 = (Object[]) it.next();
						typeOfAdvance=(Obj1[0].toString());	
						loanApprovedDate = (Obj1[1].toString());
					}
				}
				gLogger.info("typeOfAdvance:"+typeOfAdvance);
				//typeOfAdvance= "Illness (Members of family & Actually depandent)~ 13 (1) (a)";
				String[] typeOfAdvanceArr = typeOfAdvance.split("~");
				gLogger.info("typeOfAdvanceArr length:"+typeOfAdvanceArr.length);
				puposeOfAdv = typeOfAdvanceArr[0];  
				gLogger.info("puposeOfAdv"+puposeOfAdv);
				ruleID = typeOfAdvanceArr[1];
				gLogger.info("ruleID"+ruleID);
				List initials = lObjGPFRequestProcess.getInitials(lStrSevaarthId);

				if (initials != null && initials.size() > 0) {

					Object[] lArrObj = (Object[]) initials.get(0);

					if(lArrObj[0] != null){
						lStrFirstname = lArrObj[0].toString();
					}
					if(lArrObj[1] != null){
						lStrMiddlename = lArrObj[1].toString();
					}
					if(lArrObj[2] != null){
						lStrLastname = lArrObj[2].toString();
					}
				}

				if(lStrFirstname != "" && lStrMiddlename != "" && lStrLastname != ""){
					strEmpIntls = lStrFirstname.substring(0,1) + lStrMiddlename.substring(0,1) + lStrLastname.substring(0,1);
				}
				if(lStrFirstname != "" && lStrMiddlename == "" && lStrLastname != ""){
					strEmpIntls = lStrFirstname.substring(0,1) + lStrLastname.substring(0,1);
				}
				if(lStrFirstname != "" && lStrMiddlename != "" && lStrLastname == ""){
					strEmpIntls = lStrFirstname.substring(0,1) + lStrMiddlename.substring(0,1);
				}
				if(lStrFirstname != "" && lStrMiddlename == "" && lStrLastname == ""){
					strEmpIntls = lStrFirstname.substring(0,1);
				}
				if(lStrFirstname == "" && lStrMiddlename != "" && lStrLastname != ""){
					strEmpIntls =  lStrMiddlename.substring(0,1) + lStrLastname.substring(0,1);
				}
				if(lStrFirstname == "" && lStrMiddlename == "" && lStrLastname == ""){
					strEmpIntls = "";
				}


				gLogger.error("lStrFirstname"+lStrFirstname);
				gLogger.error("lStrMiddlename"+lStrMiddlename);
				gLogger.error("lStrLastname"+lStrLastname);
				gLogger.error("strEmpIntls"+strEmpIntls);

				SimpleDateFormat lObjDateFormate = new SimpleDateFormat("dd/MM/yyyy");
				//Date today = new Date();

				Date today = new Date();

				int currmonth = today.getMonth() + 1;
				int curryear = 1900 + today.getYear();
				int prevyear = curryear - 1;

				Long lLngCurrMonth = Long.parseLong(String.valueOf(currmonth));
				Long lLngCurrYr = Long.parseLong(String.valueOf(curryear)); 
				Long lLngPrevYr = Long.parseLong(String.valueOf(prevyear)); 


				String lStrJoiningDate = (String)report.getParameterValue("joiningDate");
				String lStrOrderDate = (String) report.getParameterValue("orderDate");
				String lStrRetirementDate = (String) report.getParameterValue("superAnnDate");


				Integer retirementMonths = null ;
				Integer serviceMonths = null;


				gLogger.info("lStrJoiningDate********"+lStrJoiningDate);
				gLogger.info("lStrOrderDate********"+lStrOrderDate);

				Date joiningDate = lObjDateFormate.parse(lStrJoiningDate);
				Date retirementDate = lObjDateFormate.parse(lStrRetirementDate);

				/*SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
				    Date date = inputFormat.parse(dateString);*/

				gLogger.info("retirementDate********"+retirementDate);


				//getDateDifference(joiningDate, retirementDate);
				int a = Math.abs(retirementDate.getMonth());
				gLogger.error("A"+a);

				int b = today.getMonth();
				gLogger.error("B"+b);

				int noOfMonthLeft = 0;

				if(a > b)
				{
					noOfMonthLeft = ((12 - b) - (12 - a));
					gLogger.error("noOfMonthLeft"+noOfMonthLeft);
				}





				else if(b > a)
				{
					noOfMonthLeft = ((12 - a) - (12 - b));
					gLogger.error("noOfMonthLeft"+noOfMonthLeft);
				}

				//gokarna
				int noOfYearsLeft=0;


				if(noOfMonthLeft<12)
				{

					noOfYearsLeft=0;


				}

				else if( noOfMonthLeft>12 )

				{
					noOfYearsLeft = retirementDate.getYear() - today.getYear();


				}
				else if(noOfMonthLeft==12 )

				{
					noOfYearsLeft=1;


				}
				//if()

				gLogger.error("noOfYearsLeft"+noOfYearsLeft);

				int noOfMonthServed = Math.abs(today.getMonth() - joiningDate.getMonth());
				gLogger.error("noOfMonthServed"+noOfMonthServed);

				int noOfYearsServed = today.getYear() - joiningDate.getYear();
				gLogger.error("noOfYearsServed"+noOfYearsServed);


				Long lLongnoOfYearsServed = Long.parseLong(String.valueOf(noOfYearsServed));
				Long lLongnoOfMonthServed = Long.parseLong(String.valueOf(noOfMonthServed));


				FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serviceLocator.getSessionFactory());





				Integer lIntFinYrId = lObjFinancialYearDAO.getFinYearIdByCurDate();


				Long lLngPreFinYearId = lIntFinYrId.longValue() - 1;
				Long lLngCurrFinYearId = lIntFinYrId.longValue();

				Double OpeningBalance = lObjGPFRequestProcess.getOpeningBalForCurrYear(gpfAccNo, lIntFinYrId.longValue());
				gLogger.error("OpeningBalance"+OpeningBalance);



				Integer PayableYrSixPay0910 = 27;
				Integer PayableYrSixPay1011 = 28;
				Integer PayableYrSixPay1112 = 29;
				Integer PayableYrSixPay1213 = 30;
				Integer PayableYrSixPay1314 = 31;


				String lStrSixPay0910 = "";
				String lStrSixPay1011 = "";
				String lStrSixPay1112 = "";
				String lStrSixPay1213 = "";
				String lStrSixPay1314 = "";


				List SixPayAmounts = lObjGPFRequestProcess.getSixPayAmounts(gpfAccNo,lIntFinYrId.longValue());

				if (SixPayAmounts != null && SixPayAmounts.size() > 0) {

					Object[] lArrObj = (Object[]) SixPayAmounts.get(0);

					if(lArrObj[0] != null){
						lStrSixPay0910 = lArrObj[0].toString();
					}

					if(lArrObj[1] != null){
						lStrSixPay1011 = lArrObj[1].toString();
					}

					if(lArrObj[2] != null){
						lStrSixPay1112 = lArrObj[2].toString();
					}

					if(lArrObj[3] != null){
						lStrSixPay1213 = lArrObj[3].toString();
					}

					if(lArrObj[4] != null){
						lStrSixPay1314 = lArrObj[4].toString();
					}

				}

				if(!(reqType.equalsIgnoreCase("FW") || reqType.equalsIgnoreCase("Final"))){
					if(lIntFinYrId == PayableYrSixPay0910){
						if(lStrSixPay0910 != null && lStrSixPay0910 != ""){
							OpeningBalance = OpeningBalance + Double.valueOf(lStrSixPay0910);
						}
					}

					if(lIntFinYrId == PayableYrSixPay1011){
						if(lStrSixPay1011 != null && lStrSixPay1011 != ""){
							OpeningBalance = OpeningBalance + Double.valueOf(lStrSixPay1011) + Double.valueOf(lStrSixPay0910);
						}
					}

					if(lIntFinYrId == PayableYrSixPay1112){
						if(lStrSixPay1112 != null && lStrSixPay1112 != ""){
							OpeningBalance = OpeningBalance + Double.valueOf(lStrSixPay1112) + Double.valueOf(lStrSixPay1011) + Double.valueOf(lStrSixPay0910);
						}
					}

					if(lIntFinYrId == PayableYrSixPay1213){
						if(lStrSixPay1213 != null && lStrSixPay1213 != ""){
							OpeningBalance = OpeningBalance + Double.valueOf(lStrSixPay1213) + Double.valueOf(lStrSixPay1112) + Double.valueOf(lStrSixPay1011) + Double.valueOf(lStrSixPay0910);
						}
					}

					if(lIntFinYrId == PayableYrSixPay1314){
						if(lStrSixPay1314 != null && lStrSixPay1314 != ""){
							OpeningBalance = OpeningBalance + Double.valueOf(lStrSixPay1314) + Double.valueOf(lStrSixPay1213) + Double.valueOf(lStrSixPay1112) + Double.valueOf(lStrSixPay1011) + Double.valueOf(lStrSixPay0910);
						}
					}
				}

				if(reqType.equalsIgnoreCase("FW") || reqType.equalsIgnoreCase("Final") ){
					OpeningBalance = OpeningBalance + Double.valueOf(lStrSixPay1314) + Double.valueOf(lStrSixPay1213) + Double.valueOf(lStrSixPay1112) + Double.valueOf(lStrSixPay1011) + Double.valueOf(lStrSixPay0910);
				}

				Double lDblAmtSanctioned = 0d;
				Date lDtLoanStartDate = null;
				Long lDblLoanPrinAmt = 0l;

				List sanction = lObjGPFRequestProcess.getLoanDetails(lStrSevaarthId,curryear,(curryear+1));

				if (sanction != null && sanction.size() > 0) {

					Object[] lArrObj = (Object[]) sanction.get(0);

					lDtLoanStartDate = (Date) lArrObj[0];
					lDblLoanPrinAmt = Long.parseLong((lArrObj[1]).toString());
					gLogger.error("lDtLoanStartDate"+lDtLoanStartDate);
					gLogger.error("lDblLoanPrinAmt"+lDblLoanPrinAmt);
				}




				int loanyear = 0;
				if(lDtLoanStartDate != null){
					loanyear = lDtLoanStartDate.getYear() + 1900;
				}




				if(loanyear == curryear){

					lDblAmtSanctioned = Double.valueOf(lDblLoanPrinAmt.toString());
					gLogger.error("lDblAmtSanctioned"+lDblAmtSanctioned);
				}

				String temp = lObjGPFRequestProcess.getRegularSubscription(lStrSevaarthId,lLngCurrMonth,lLngCurrYr);

				Double lDblRegSub = 0d;
				if(temp != null && temp != ""){
					lDblRegSub = Double.valueOf(temp);
					gLogger.error("lDblRegSub"+lDblRegSub);
				}


				Double lDblWithAmt = 0d;
				Integer lIntCurrFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();


				//gokarna for correct amounts in order on 24/09/2015	

				Double lDbltemp = lObjGPFRequestProcess.getWithdrawalSanc(gpfAccNo, lIntCurrFinYearId.longValue());
				gLogger.error("lDbltemp"+lDbltemp);
				if(lDbltemp != null ){
					lDblWithAmt = lDbltemp;
					gLogger.error("lDbltemp"+lDbltemp);
				}


				Long lLngPaybillMonth = 0l;

				Long month = lObjGPFRequestProcess.getPaybillMonth(lStrSevaarthId,lLngCurrYr);

				if(month != 0){
					lLngPaybillMonth = month;

				}

				Long presentGPFSheduleRecover=0l;
				Double lDblRecAmt = Double.valueOf(lObjGPFRequestProcess.getRecoveryAmt(gpfAccNo,lLngCurrYr,lStrSevaarthId,lLngPaybillMonth));


				presentGPFSheduleRecover = lObjGPFRequestProcess.getCurrentRecoveryAmt(gpfAccNo,lLngCurrYr,lStrSevaarthId,lLngCurrMonth);

				if(presentGPFSheduleRecover!=0l && presentGPFSheduleRecover!=null)
				{
					presentGPFSheduleRecover= lObjGPFRequestProcess.getCurrentRecoveryAmt(gpfAccNo,lLngCurrYr,lStrSevaarthId,lLngCurrMonth);
				}
				else
				{
					presentGPFSheduleRecover=0l;
				}

				gLogger.error("OpeningBalance"+OpeningBalance);
				gLogger.error("lDblRegSub"+lDblRegSub);
				gLogger.error("lDblRecAmt"+lDblRecAmt);
				gLogger.error("lDblAmtSanctioned"+lDblAmtSanctioned);
				gLogger.error("lDblWithAmt"+lDblWithAmt);


				Double NetBalance = OpeningBalance + lDblRegSub + lDblRecAmt - lDblAmtSanctioned - lDblWithAmt ;
				gLogger.error("NetBalance"+NetBalance);

				Double lDblAmtSancGpf = getAmtSanctioned(lStrTransactionId,reqType);
				gLogger.error("lDblAmtSancGpf"+lDblAmtSancGpf);



				ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serviceLocator.getSessionFactory());


				String preFinYear = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lLngPreFinYearId);
				String CurrFinYear = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lLngCurrFinYearId);

				String lStrcurrFinYearCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lIntFinYrId.longValue());


				lStrcurrFinYearCode = lStrcurrFinYearCode.substring(0, 4);


				Date startDate = lObjScheduleGenerationDAO.getStartDateOfFinancialYear1(lIntFinYrId.longValue());
				gLogger.error("NetBalance"+NetBalance);
				gLogger.error("lDblAmtSancGpf"+lDblAmtSancGpf);
				gLogger.error("preFinYear"+preFinYear);
				gLogger.error("CurrFinYear"+CurrFinYear);
				gLogger.error("lStrcurrFinYearCode"+lStrcurrFinYearCode);
				gLogger.error("startDate"+startDate);



				List lLstDsgnAndOfficeName = lObjGPFRequestProcess.getDsgnAndOfficeName(lStrSevaarthId);
				gLogger.info("lLstDsgnAndOfficeName size:"+lLstDsgnAndOfficeName.size());
				List lstDDODeptDtls = lObjGPFRequestProcess.getDDODeptDetails(lStrSevaarthId);

				String deptName = "";
				String ddoOfcAdrs = "";
				String ddoOfcEmail = "";
				String ddoOfcTelNo = "";

				if(lstDDODeptDtls != null && !lstDDODeptDtls.isEmpty()){
					Object[] obj = (Object[])lstDDODeptDtls.get(0);

					if(obj[1] != null){
						deptName = obj[1].toString();
					}

					if(obj[4] != null){
						ddoOfcAdrs = obj[4].toString();
					}

					if(obj[5]!=null)
						ddoOfcEmail = obj[5].toString();
					if(obj[6]!=null)
						ddoOfcTelNo = obj[6].toString();
				}

				Object[] lObjDsgnAndOfficeName = null;
				String lStrDsgnName = "";
				String lStrOfficeName = "";
				String lStrPayScale = "";
				Long lStrBasicPay = 0l;
				Long  lStrGradePay = 0l;




				if (!lLstDsgnAndOfficeName.isEmpty()) {
					lObjDsgnAndOfficeName = (Object[]) lLstDsgnAndOfficeName.get(0);
					lStrDsgnName = (String) lObjDsgnAndOfficeName[0];
					gLogger.info("lStrDsgnName of emplotyee"+lStrDsgnName);
					lStrOfficeName = (String) lObjDsgnAndOfficeName[1];
					lStrPayScale = (String) lObjDsgnAndOfficeName[2];
					lStrEmpNameMarathi = (String) lObjDsgnAndOfficeName[5];


					gLogger.info("lStrEmpNameMarathi of emplotyee"+lStrEmpNameMarathi);
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

				/*if(lStrDisbursementNumber==0)
				{
				List lLstAdvanceHistoryDtls = lObjGPFRequestProcess
				.getAdvanceHistory(gpfAccNo, lIntFinYrId.longValue());
				if (lLstAdvanceHistoryDtls != null && lLstAdvanceHistoryDtls.size() > 0) {
					for (Integer lIntcnt = 0; lIntcnt < lLstAdvanceHistoryDtls.size(); lIntcnt++) {
						lObjhistory = (Object[]) lLstAdvanceHistoryDtls.get(lIntcnt);
						lDblWithdrawalSanc= lDblWithdrawalSanc + ((Double) lObjhistory[1]).doubleValue();;
					}
				}
				}*/
				Double lDbClosingBalance = lObjGPFRequestProcess.getClosingBalance(gpfAccNo);
				List<Object[]> advanceList=null;
				if(!reqType.equalsIgnoreCase("Final") || !reqType.equalsIgnoreCase("FW"))
				{				 advanceList = lObjGPFRequestProcess.getAdvanceDetail(gpfAccNo, reqType);

				}
				else
				{
					advanceList = lObjGPFRequestProcess.getAdvanceDetail1(gpfAccNo, reqType);
				}
				Object lObjAdvDtls[] = new Object[9];
				if (!advanceList.isEmpty()) {
					lObjAdvDtls = advanceList.get(0);
				}

				String lStrDDOCodeOfLoggedInDDO = lObjGPFRequestProcess.getDdoCodeForDDO(lLngPostId);
				String ddocode = "7101";

				String officeofDDOWhereBillGenerated=lObjGPFRequestProcess.getEmployerOfficeName(lStrDDOCODE123); 
				String sub = lStrDDOCodeOfLoggedInDDO.substring(0, 4);
				gLogger.error("lStrDDOCodeOfLoggedInDDO"+lStrDDOCodeOfLoggedInDDO);

				gLogger.error("sub"+sub);


				String lStrEmployerOfficeName = lObjGPFRequestProcess.getEmployerOfficeName(lStrDDOCodeOfLoggedInDDO);

				gLogger.error("lStrEmployerOfficeName"+lStrEmployerOfficeName);

				//split string to get Initial letter of string
				String split[]= lStrEmployerOfficeName.split("\\s+");
/*
				gLogger.error("emplyeeOfficerName is:::::"+split[0].substring(0, 1));
				gLogger.error("length is:::::"+split[0].length());

				gLogger.error("emplyeeOfficerName is:::::"+split[1].substring(0, 1));
				gLogger.error("length is:::::"+split[1].length());

				gLogger.error("emplyeeOfficerName is:::::"+split[2].substring(0, 1));
				gLogger.error("length is:::::"+split[2].length());*/


				int size = split.length;
				gLogger.error("size is:::::"+size);
				String var[];
				String Short="";
				String ShortName="";
				for(int j=0;j<size;j++)
				{
					// split("\\s+");
					gLogger.error(" split is:::::"+ split[j].subSequence(0, 1));
					Short=Short+split[j].subSequence(0, 1);
					ShortName=Short.toUpperCase();
					gLogger.error(" String is:::::"+Short);
				}



				String lStrEmployerDsgnName = lObjGPFRequestProcess.getEmployerDsgnName(lLngPostId);
				String emplyeeOfficerName = lObjGPFRequestProcess.getEmployerNameFrmPostId(lLngPostId.toString());

				gLogger.error("emplyeeOfficerName is:::::"+emplyeeOfficerName);
				String [] emplyeeOfficerName1=emplyeeOfficerName.split("");
				gLogger.error("emplyeeOfficerName1 is:::::"+emplyeeOfficerName1);
				/*emplyeeOfficerName1[0].substring(0,1);
				gLogger.error("emplyeeOfficerName2 is:::::"+emplyeeOfficerName1);
				 */

				String lStrTreasuryName = lObjGPFRequestProcess.getTreasuryNameOfEmp(lStrDDOCodeOfLoggedInDDO);
				String lStrTreasuryNameofBillGenDDO = lObjGPFRequestProcess.getTreasuryNameOfEmp(lStrDDOCODE123);


				//String lStrGradePay = lObjGPFRequestProcess.getGradePayFrmSevaarthId(lStrSevaarthId);
				String lStrPurposeOfRequest = lObjGPFRequestProcess.getPrpsFrmTransactionId(lStrTransactionId);
				String lStrBeneficiaryName = lObjGPFRequestProcess.getPrpsFrmTransactionId(lStrTransactionId);
				String lStrOtherPurpose = lObjGPFRequestProcess.getOtherPurpose(lStrTransactionId);
				gLogger.error("lStrOtherPurpose"+lStrOtherPurpose);

				gLogger.error("lStrEmployerDsgnName"+lStrEmployerDsgnName);




				String lStrPurposeName = "";
				if(lStrPurposeOfNra!= null && !lStrPurposeOfNra.equals(""))
					lStrPurposeName = lObjGPFRequestProcess.getPurposeNameFrmId(lStrPurposeOfNra); 


				startDate.getMonth();
				startDate.getYear();

				Integer currDate = today.getDate();
				Integer currMonth = today.getMonth() + 1;
				Integer currYear = today.getYear() + 1900;



				String lStrInstAmt = "0";
				String lStrTotalInst = "0";
				String recoverableAmt="0";

				if (reqType.equals("RA")) {
					List inst = lObjGPFRequestProcess.getInstallmentvalues(lStrTransactionId);

					if (inst != null && inst.size() > 0) {

						Object[] lArrObj = (Object[]) inst.get(0);

						if(lArrObj[0] != null){
							lStrTotalInst = lArrObj[0].toString();
							lStrInstAmt = lArrObj[1].toString();
							recoverableAmt=lArrObj[5] != null ? lArrObj[5].toString() : "0";
						}
					}

					gLogger.error("lStrTotalInst"+lStrTotalInst);
					gLogger.error("lStrInstAmt"+lStrInstAmt);
				}



				gLogger.error("reqType$$$$$$$$$$"+reqType);
				int Count =0;

				//if (reqType.equals("RA") || reqType.equals("NRA") || reqType.equals("FW") || reqType.equals("Non-Refundable") ){
				if (reqType.equals("NRA") || reqType.equals("Final") || reqType.equals("FW") )
				{
					List amnt=lObjGPFRequestProcess.getRecoveredAndOutstandingAmt(gpfAccNo);

					String outStanding=null;
					String recoveredAmt=null;

					if (amnt != null && amnt.size() > 0) {

						Object[] lArrObj = (Object[]) amnt.get(0);

						if(lArrObj[0] != null){
							outStanding = lArrObj[0].toString();
							recoveredAmt = lArrObj[1].toString();
						}
					}




					gLogger.info("******RARARARA*******");
					if(lStrDDOCODE123!=null)
					{
						gLogger.info("******5555555555555*******");
						StringBuilder lSBQuery1 = new StringBuilder();
						String ddocode123="";
						List lstrList=null;


						lSBQuery1.append( " SELECT LOCATION_CODE FROM ORG_DDO_MST where DDO_CODE='"+lStrDDOCODE123+"'" ); 


						Query lQuery = ghibSession.createSQLQuery(lSBQuery1.toString());


						gLogger.info("Query is:::::::::"+lSBQuery1.toString());

						lstrList = lQuery.list();
						if (lstrList.size() != 0) {

							ddocode123 = lstrList.get(0).toString();

							gLogger.info("ddocode123::::::::"+ddocode123);

						}

					}	

					gLogger.error("inside regular ");
					Date lDtApplicDate = lObjGPFRequestProcess.getApplicationDateFrmTranId(lStrTransactionId);
					String lDtSancDate1="";
					String lDtSancDate="";

					lDtSancDate = lObjGPFRequestProcess.getSanctionedDateFrmTranId(lStrTransactionId);
					if (!reqType.equals("Final") || !reqType.equals("FW"))
					{
						lDtSancDate = lObjGPFRequestProcess.getSanctionedDateFrmTranId(lStrTransactionId);
						lDtSancDate1 = lObjGPFRequestProcess.getSanctionedDateFrmTranId1(lStrTransactionId);
						gLogger.error("inside lDtSancDate1:::::::::: "+lDtSancDate1);
					}
					else
					{lDtSancDate1 = lObjGPFRequestProcess.getSanctionedDateFrmTranId1Final(lStrTransactionId);
					lDtSancDate = lObjGPFRequestProcess.getSanctionedDateFrmTranIdFinalWithdrwal(lStrTransactionId);
					gLogger.error("inside lDtSancDate1:final::::::::: "+lDtSancDate1);
					}


					/*if(lDtSancDate1.equalsIgnoreCase("") && lDtSancDate1==null)
					{
						 lDtSancDate1 = lObjGPFRequestProcess.getSanctionedDateFrmTranId1Final(lStrTransactionId);

					}*/
					int dt = 0;
					int mn = 0;
					int yr = 0;
					if(lDtApplicDate != null){
						dt = lDtApplicDate.getDate();
						mn = lDtApplicDate.getMonth() + 1;
						yr = lDtApplicDate.getYear() + 1900;
					}

					Double total = Math.round(OpeningBalance) + Double.parseDouble(lObjGPFAccountBal[0].toString()) + Double.parseDouble(lObjGPFAccountBal[1].toString());


					report.setStyleList(noBorder);
					report.setStyleList(rowsFontsVO);
					ArrayList<Object> rowList = new ArrayList<Object>();

					rowList.add("" + space(1));
					dataList.add(rowList);
					gLogger.info("in SpecialCase90"+SpecialCase90);
					if(SpecialCase90.equalsIgnoreCase("Y"))
					{
						gLogger.info("In :::SpecialCase90:::NRA::");

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA1") ,leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA2"),lStrOfficeName,"---------") ,leftAlign));
						dataList.add(rowList);


						//FDF
						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5a"),empName,lStrDsgnName,"--------"),
									leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5b"),empName,lStrDsgnName,"--------"),
									leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5c"),empName,lStrDsgnName,"--------"),
									leftAlign));
							dataList.add(rowList);
						}
						//shortDesgn+ShrtOff+fin_year

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA7"),ShortName+"/"+lStrEmployerDsgnName+"/"+"NRA"+"/"+lDtSancDate 
						),
						leftAlign));
						dataList.add(rowList);

						//des+officenameemploy+'”dd/mmyyy'
						gLogger.info("san off desgn"+emplyeeOfficerName+"desgggggggg"+lStrEmployerDsgnName);
						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA8"),""),
								rightAlign));
						dataList.add(rowList);
*/
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName+","+lDtSancDate1  ,rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);


						rowList = new ArrayList<Object>(); 
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA9"),empName,gpfAccNo,lStrDsgnName),
								CenterAlignVO));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA10"),empName,gpfAccNo,lStrDsgnName),
								CenterAlignVO));
						dataList.add(rowList);


						//ghhj
						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11a"),empName,lStrDsgnName,gpfAccNo),
									CenterAlignVO));
							dataList.add(rowList);  
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11b"),empName,lStrDsgnName,gpfAccNo),
									CenterAlignVO));
							dataList.add(rowList);  
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11c"),empName,lStrDsgnName,gpfAccNo),
									CenterAlignVO));
							dataList.add(rowList);  
						}


						//done
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA12"),empName,gpfAccNo,lStrDsgnName),
								leftUnderlineBold));
						dataList.add(rowList); 
						//sdd//

						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA13a"),lStrEmployerOfficeName,empName,lStrDsgnName,lStrRetirementDate,sancAmount), 
									leftAlign));
							dataList.add(rowList); 
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA13b"),lStrEmployerOfficeName,empName,lStrDsgnName,lStrRetirementDate,sancAmount), 
									leftAlign));
							dataList.add(rowList); 
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA13c"),lStrEmployerOfficeName,empName,lStrDsgnName,lStrRetirementDate,sancAmount), 
									leftAlign));
							dataList.add(rowList); 
						}


						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA14"),sancAmount),
								leftAlign));
						dataList.add(rowList); */
						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA15a"),empName,lStrEmployerDsgnName+","+lStrEmployerOfficeName),
									leftAlign));
							dataList.add(rowList); 

						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA15b"),empName,lStrEmployerDsgnName+","+lStrEmployerOfficeName),
									leftAlign));
							dataList.add(rowList); 

						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA15c"),empName,lStrEmployerDsgnName+","+lStrEmployerOfficeName),
									leftAlign));
							dataList.add(rowList); 

						}
						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA16a"),empName,lStrDsgnName,gpfAccNo,sancAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(sancAmount))),
									leftAlign));
							dataList.add(rowList); 
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA16b"),empName,lStrDsgnName,gpfAccNo,sancAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(sancAmount))),
									leftAlign));
							dataList.add(rowList); 
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA16c"),empName,lStrDsgnName,gpfAccNo,sancAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(sancAmount))),
									leftAlign));
							dataList.add(rowList); 
						}
						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);


						/*	rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA18"),""),
								rightAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName,rightAlign));
						dataList.add(rowList);
						 */

						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA8"),""),
								rightAlign));
						dataList.add(rowList);*/

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName+","+lDtSancDate1  ,rightAlign));
						dataList.add(rowList);



						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA19"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA20"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList);  
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA23"),lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
								leftAlign));
						dataList.add(rowList); 

						//GPF.NinetyPerNRA25=
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25"),empName,lStrDsgnName,lStrOfficeName),
								leftAlign));
						dataList.add(rowList); 

					}
					List lstrList1=null;
					List lstrList2=null;
					List lstrList3=null;
					Object[] obj2=null;
					if(SpecialCase90.equalsIgnoreCase("N") && reqType.equals("NRA") && lStrDisbursementNumber>0)
					{

						// gpfAccNo lStrOrderId lStrSevaarthId lStrTransactionId lStrPurposeOfNra

						gLogger.info(":::::::In :::N:::NRA::");
						gLogger.info(":Transaction id and Sevarth id gpfaccno purposetype:"+gpfAccNo+lStrOrderId+lStrSevaarthId+lStrTransactionId+lStrPurposeOfNra);

						//
						StringBuilder lSBQuery4 = new StringBuilder();
						String MST_GPFADV="";
						String Purpose="";

						lSBQuery4.append( " SELECT MST_GPF_ADVANCE_ID,PURPOSE_CATEGORY FROM  MST_GPF_ADVANCE where TRANSACTION_ID='"+lStrTransactionId+"'" ); 
						Query lQuery2 = ghibSession.createSQLQuery(lSBQuery4.toString());
						gLogger.info("Query is:::::::::"+lSBQuery4.toString());
						lstrList1 = lQuery2.list();

						gLogger.info(":::::::Size is:::::::::"+lstrList1.size());
						Object[] lobjList=null;

						if (!lstrList1.isEmpty()) {

							lobjList =(Object[]) lstrList1.get(0);
							MST_GPFADV =lobjList[0].toString();
							Purpose =lobjList[1].toString(); 
							gLogger.info("MST_GPF_ADV::::::::"+MST_GPFADV);
							gLogger.info("Purpose::::::::"+Purpose);
							//}
						}

						StringBuilder lSBQuery6 = new StringBuilder();

						lSBQuery6.append(" SELECT COUNT(1) as cnt FROM MST_GPF_ADVANCE  ADV ");
						lSBQuery6.append(" INNER JOIN MST_GPF_BILL_DTLS BILL ON  ADV.ORDER_NO= BILL.ORDER_NO AND BILL.STATUS_FLAG= 1  ");
						lSBQuery6.append(" WHERE ADV.GPF_ACC_NO='"+gpfAccNo+"'  AND ADV.STATUS_FLAG= 'A' and ADV.ADVANCE_TYPE= 'NRA' and  ADV.PURPOSE_CATEGORY='"+Purpose+"'  ");

						Query lQuery6 = ghibSession.createSQLQuery(lSBQuery6.toString());
						gLogger.info("Query is:::::::::"+lSBQuery6.toString());
						lstrList3 = lQuery6.list();
						if (lstrList3.size() != 0) {
							Count = Integer.parseInt(lstrList3.get(0).toString());
							gLogger.info("No of Disbursment Already taken::::::"+Count);
						}

List lstrList9=null;
Long Count1=0l;
						StringBuilder lSBQuery9 = new StringBuilder();

						lSBQuery9.append(" SELECT cast(ADV.MST_GPF_ADVANCE_ID as BIGINT)  FROM MST_GPF_ADVANCE  ADV ");
						lSBQuery9.append(" INNER JOIN MST_GPF_BILL_DTLS BILL ON  ADV.ORDER_NO= BILL.ORDER_NO AND BILL.STATUS_FLAG= 1  ");
						lSBQuery9.append(" WHERE ADV.GPF_ACC_NO='"+gpfAccNo+"'  AND ADV.STATUS_FLAG= 'A' and ADV.ADVANCE_TYPE= 'NRA' and  ADV.PURPOSE_CATEGORY='"+Purpose+"' order by  CAST(ADV.MST_GPF_ADVANCE_ID AS BIGINT)  ");

						Query lQuery9 = ghibSession.createSQLQuery(lSBQuery9.toString());
						gLogger.info("Query is:::::::::"+lSBQuery9.toString());
						lstrList9 = lQuery9.list();
						if (lstrList9.size() != 0) {
							Count1 = Long.parseLong((lstrList9.get(0).toString()));
							gLogger.info("No of Disbursment Already taken::::::"+Count1);
						}

						
						
						
						
						
						
						
						
						
						
						//and DIS_NUMBER='"+Count+"'

						//No of disbuesement
						StringBuilder lSBQuery5 = new StringBuilder();
						//String Count="";
						String val1="";
						String DisNumber="";
						String Amount="";
						int DisNumber1=0;
						int Amount1=0;
						//int va
						lSBQuery5.append( " SELECT DIS_NUMBER,DIS_AMOUNT  FROM IFMS.MST_GPF_DISBURSE_DETAILS WHERE MST_GPF_ADV_ID= '"+Count1+"'  " ); 
						Query lQuery3 = ghibSession.createSQLQuery(lSBQuery5.toString());
						gLogger.info("Query is:::::::::"+lSBQuery5.toString());
						lstrList2 = lQuery3.list();
						gLogger.info("Query is::::All Disbyursement Values:::::"+lstrList2.size());

						/*if(sizeOfLst != 0){
							gLogger.error("loans taken");

							for(int j = 0; j<sizeOfLst;j++ ){
								obj = (Object[])TableDataSevarth.get(j);*/

						if (lstrList2.size()!=0) {
							for(int i =0;i<lstrList2.size();i++)
							{
								gLogger.info("i:::::::::"+i);
								obj2 = (Object[]) lstrList2.get(i);
								DisNumber=obj2[0].toString();
								Amount=obj2[1].toString();
								val1= val1+DisNumber+"#"+Amount+"#";
								gLogger.info("DisNumber is:::::::::"+DisNumber+"::::Amount:::::"+Amount);
							}
						}
						gLogger.info("val1::::::::"+val1);
						gLogger.info("DisNumber is:::::::::"+DisNumber+"::::Amount:::::"+Amount);


						String[] value1=val1.split("#");
						gLogger.info("values are::::::::"+value1[0]);


						gLogger.info("values are::::::::"+value1[1]);
						gLogger.info("values are::::::::"+value1.length);

						//No of dis completed
						//getc current disbursement VAlues	
						StringBuilder lSBQuery7 = new StringBuilder();
						List lstrList4=null;
						int CurentDisAmount=0;
						Count=Count+1;
						lSBQuery7.append("  SELECT DIS_AMOUNT  FROM IFMS.MST_GPF_DISBURSE_DETAILS WHERE MST_GPF_ADV_ID= '"+Count1+"' and DIS_NUMBER= '"+Count+"' ");
						/*lSBQuery6.append(" INNER JOIN MST_GPF_BILL_DTLS BILL ON  ADV.ORDER_NO= BILL.ORDER_NO AND BILL.STATUS_FLAG= 1  ");
						lSBQuery6.append(" WHERE ADV.GPF_ACC_NO='"+gpfAccNo+"'  AND ADV.STATUS_FLAG= 'A' and ADV.ADVANCE_TYPE= 'NRA' and  ADV.PURPOSE_CATEGORY='"+Purpose+"'  ");
						 */
						Query lQuery7 = ghibSession.createSQLQuery(lSBQuery7.toString());
						gLogger.info("Query is:::::::::"+lSBQuery7.toString());
						lstrList4 = lQuery7.list();
						if (lstrList4.size() != 0) {
							CurentDisAmount = Integer.parseInt(lstrList4.get(0).toString());
							gLogger.info("CurentDisAmount::::::"+CurentDisAmount);
						}
						///

						String Emp_name_of_HO ="";
						GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serviceLocator.getSessionFactory());
						List lLstHoDtls = lObjGpfWorkFlowConfig.getActiveHOLst(gStrLocCode);
						if(lLstHoDtls != null && !lLstHoDtls.isEmpty())
						{
							Object[] obj = (Object[])lLstHoDtls.get(0);
							Emp_name_of_HO = obj[1].toString();
							gLogger.info("Emp_name_of_HO"+Emp_name_of_HO);
						}

						//Dis Number and value



						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement1"),lStrOfficeName,"---------") ,leftAlign));
						dataList.add(rowList);*/
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.Disbursement1a") ,leftBoldAlignVO));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( space(10)+gObjRsrcBndleForSPL90.getString("GPF.Disbursement1b"),leftAlign));
						dataList.add(rowList);


						//order number of 1st disbursement which is seen in sanctinong authorities login.

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement2"),lStrOrderId), 
								leftAlign));
						dataList.add(rowList);



						//sddd
						//Name of HO of employee who is taking loan 2)blank


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement3"),lStrEmployerOfficeName,"----------" 
						),
						leftAlign));
						dataList.add(rowList);

						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement4aa"),empName,lStrDsgnName,"---------"
							),
							leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement4bb"),empName,lStrDsgnName,"---------"
							),
							leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement4cc"),empName,lStrDsgnName,"---------"
							),
							leftAlign));
							dataList.add(rowList);
						}

						///GHGJLH

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA7"),ShortName+"/"+lStrEmployerDsgnName+"/"+"NRA"+"/"+lDtSancDate 
						),
						leftAlign));
						dataList.add(rowList);

						
						

						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement5"),"----------" 
						),
						leftAlign));
						dataList.add(rowList);*/

						//des+officenameemploy+'”dd/mmyyy'
						gLogger.info("san off desgn"+emplyeeOfficerName+"desgggggggg"+lStrEmployerDsgnName);
						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA8"),""),
								rightAlign));
						dataList.add(rowList);*/

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName+","+lDtSancDate1  ,rightAlign));
						dataList.add(rowList);

						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lDtSancDate1 +space(30) ,rightAlign));
						dataList.add(rowList);
						
						
						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);


						rowList = new ArrayList<Object>(); 
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement10"),""), //R should be blank
								CenterBoldAlignVO));
						dataList.add(rowList);






						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11"),empName,lStrDsgnName,gpfAccNo),
								CenterAlignVO));
						dataList.add(rowList); */ 

						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11a"),empName,lStrDsgnName,gpfAccNo),
									CenterAlignVO));
							dataList.add(rowList);  
						}


						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(space(10)+ MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11b"),empName,lStrDsgnName,gpfAccNo),
									CenterAlignVO));
							dataList.add(rowList);  
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(space(10)+ MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11c"),empName,lStrDsgnName,gpfAccNo),
									CenterAlignVO));
							dataList.add(rowList);  
						}



						//done
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA12"),empName,gpfAccNo,lStrDsgnName),
								leftUnderlineBold));
						dataList.add(rowList); 



						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA7"),ShortName+"/"+lStrEmployerDsgnName+"/"+"NRA"+"/"+lDtSancDate 
						),
						leftAlign));
						dataList.add(rowList);*/

						//Treasuty name to which bill belongs.
						//Designation and office name of sanctioning authority.
						//Designation and office name of HO of employee
						//Name of employee who is taking loan.
						//Designation of empoyee who is taking loan.

						// officeofDDOWhereBillGenerated,1,lStrEmployerDsgnName+","+lStrEmployerOfficeName,empName,lStrDsgnName
						//222222
						GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig1 = new GPFWorkFlowConfigDAOImpl(null, serviceLocator.getSessionFactory());
						List lLstHoDtls1 = lObjGpfWorkFlowConfig1.getActiveHOLst(gStrLocCode);

						Long post_id_Ho=0l;
						if(lLstHoDtls1 != null && !lLstHoDtls1.isEmpty())
						{
							Object[] obj = (Object[])lLstHoDtls.get(0);
							post_id_Ho = Long.parseLong(obj[2].toString());
							gLogger.info("Emp_name_of_HO"+Emp_name_of_HO);
						}

						String lStrEmployerDsgnNamehHO = lObjGPFRequestProcess.getEmployerDsgnName(post_id_Ho);     
						String emplyeeOfficerNameHO = lObjGPFRequestProcess.getEmployerNameFrmPostId(post_id_Ho.toString());

						//hjh
						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement6a"),officeofDDOWhereBillGenerated,lStrEmployerDsgnNamehHO,emplyeeOfficerNameHO+","+lStrEmployerOfficeName,empName,lStrDsgnName,puposeOfAdv),  //1 2 
									leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement6b"),officeofDDOWhereBillGenerated,lStrEmployerDsgnNamehHO,emplyeeOfficerNameHO+","+lStrEmployerOfficeName,empName,lStrDsgnName,puposeOfAdv),  //1 2 
									leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement6c"),officeofDDOWhereBillGenerated,lStrEmployerDsgnNamehHO,emplyeeOfficerNameHO+","+lStrEmployerOfficeName,empName,lStrDsgnName,puposeOfAdv),  //1 2 
									leftAlign));
							dataList.add(rowList);
						}

						// Sub type selected while disbursing 1 st disbursement.
						// GPF Account number of employee who is taking loan.
						// Disbursement amount of 1st disbursement in numbers.
						// Disbursement amount of 1st disbursement in words.
						// Number of disbursements in which he will he disburse the total loan in numbers.

						// "Sub" ,gpfAccNo, "Disbursement amount","words","totalno of dis" puposeOfAdv  ruleID                                                                           value1[Count]
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement7"),gpfAccNo,Integer.parseInt(value1[1]),EnglishDecimalFormat.convertWithSpace(new BigDecimal(Integer.parseInt(value1[Count]))),Count),
								leftAlign));
						dataList.add(rowList); 

						//22 – Rule number for the 1st disbursement sub type of loan.

						//23 - Disbursement amount of 1st disbursement in numbers. (Doubt - 1st disbursement or recent disbursement )

						//24 - Disbursement amount of 1st disbursement in words. (Doubt - 1st disbursement or recent disbursement )

						// "Rule number","Disbursement amount1","words"
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement8"), ruleID,Integer.parseInt(value1[1]),EnglishDecimalFormat.convertWithSpace(new BigDecimal(Integer.parseInt(value1[1])))),
								leftAlign));
						dataList.add(rowList); 



						//
						//25 – Name of employee who is taking loan.

						//26 – Current disbursement value in numbers.

						//27 -  Current disbursement value in words.

						//gggggggggg

						gLogger.info("Total Disbursement::::::::"+Count);
						gLogger.info("values are::::::::"+value1[0]);


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Disbursement9"),empName,CurentDisAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(CurentDisAmount))),
								leftAlign));
						dataList.add(rowList); 


						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);


						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA18"),""),
								rightAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName,rightAlign));
						dataList.add(rowList);

						 */
						/*rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA8"),""),
								rightAlign));
						dataList.add(rowList);*/

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName  ,rightAlign));
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);



						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA19"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 
						
						//For AG circle
						StringBuilder lSBQuery2 = new StringBuilder();
						String AgCircle="";
						List lstrList=null;


						lSBQuery2.append( " SELECT rlt.AC_MAINTAINED_BY FROM  ORG_USER_mst mst ");
						lSBQuery2.append( "inner join ORG_USERPOST_RLT user on mst.USER_ID=user.USER_ID ");
						lSBQuery2.append( "inner join RLT_DCPS_PAYROLL_EMP rlt on rlt.POST_ID=  user.POST_ID ");
						lSBQuery2.append( "where mst.USER_NAME ='"+lStrSevaarthId+"'" ); 


						Query lQuery = ghibSession.createSQLQuery(lSBQuery2.toString());


						gLogger.info("Query is:::::::::"+lSBQuery2.toString());

						lstrList = lQuery.list();
						//int AG=0;
						if (lstrList.size() != 0) {

							AgCircle = lstrList.get(0).toString();

							gLogger.info("AgCircle is ::::::"+AgCircle);
							if(AgCircle.equalsIgnoreCase("700092")) //A.G.Mumbai
							{
								 AG=1; 
							}
							if(AgCircle.equalsIgnoreCase("700093")) //nagpur
							{ 
								AG=2;
							}
									
							

						}			
						
						if(AG==1)
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21aa"),
									leftAlign));
							dataList.add(rowList);  
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22aa"),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24aa"),officeofDDOWhereBillGenerated),
									leftAlign));
							dataList.add(rowList); 
							
							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
									leftAlign));
							dataList.add(rowList); 
							gLogger.info("in report end::::::");*/
							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25aa"),empName,lStrDsgnName,lStrOfficeName),
									leftAlign));
							dataList.add(rowList);
							
						}
						else if(AG==2)
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( gObjRsrcBndleForSPL90.getString("GPF.GPF.NinetyPerNRA23bb"),
									leftAlign));
							dataList.add(rowList);  
							
							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24bb"),
									leftAlign));
							dataList.add(rowList); 
*/
							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24bb"),officeofDDOWhereBillGenerated),
									leftAlign));
							dataList.add(rowList); 
							gLogger.info("in report end::::::");
							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25aa"),empName,lStrDsgnName,lStrOfficeName),
									leftAlign));
							dataList.add(rowList);
						}
						else
						{
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA20"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList);  
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA23"),lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
								leftAlign));
						dataList.add(rowList); 
						gLogger.info("in report end::::::");
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25"),empName,lStrDsgnName,lStrOfficeName),
								leftAlign));
						dataList.add(rowList);
						
						}
						
/*
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA20"),lStrTreasuryNameofBillGenDDO),
								leftAlign));
						dataList.add(rowList); 
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22"),empName,gpfAccNo,lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA23"),lStrDsgnName),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
								leftAlign));
						dataList.add(rowList); 

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(empName,leftAlign));
						dataList.add(rowList); 
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25"),empName,lStrDsgnName,lStrOfficeName),
								leftAlign));
						dataList.add(rowList);*/

					}


					else if((reqType.equalsIgnoreCase("Final") || reqType.equalsIgnoreCase("FW")) && !SpecialCase90.equalsIgnoreCase("N") )
					{

						gLogger.info("in Final::::::::::::");
						//For Retirement
						if(SpecialCase90.equalsIgnoreCase("10001198394"))
						{
							gLogger.info("in Final::::::10001198394::::::");


							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.RETIRE1") ,leftBoldAlignVO ));
							dataList.add(rowList);

							
							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(space(10)+gObjRsrcBndleForSPL90.getString("GPF.RETIRE1a"),leftAlign));
							dataList.add(rowList);
							
							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA2"),lStrOfficeName,"---------") ,leftAlign));
							dataList.add(rowList);



							lStrGender=lObjGPFRequestProcess.getGenderOfEmployee(lStrSevaarthId);
							gLogger.error("lStrGender is::::::::::"+lStrGender);

							if(lStrGender.equalsIgnoreCase("M"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(space(10)+new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5a"),empName,lStrDsgnName,"--------"),
										leftAlign));
								dataList.add(rowList);
							}

							if(lStrGender.equalsIgnoreCase("F"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(space(10)+new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5b"),empName,lStrDsgnName,"--------"),
										leftAlign));
								dataList.add(rowList);
							}
							if(lStrGender.equalsIgnoreCase("T"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(space(10)+new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5c"),empName,lStrDsgnName,"--------"),
										leftAlign));
								dataList.add(rowList);
							}

							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA1c"),
									leftAlign));
							dataList.add(rowList);
							
							 
							//shortDesgn+ShrtOff+fin_year

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA7"),ShortName+"/"+"Final Withdrawal"+"/"+lDtSancDate 
							),
							leftAlign));
							dataList.add(rowList);

							
							

							//gghj

							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA8"),""),
									rightAlign));



							dataList.add(rowList);*/
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName ,rightAlign));
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(lDtSancDate1  ,rightAlign));
							dataList.add(rowList);
							
							
							//des+officenameemploy+'”dd/mmyyy'
							gLogger.info("san off desgn"+emplyeeOfficerName+"desgggggggg"+lStrEmployerDsgnName);
							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA18"),""),
									rightAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							//rowList.add(new StyledData(ShortName+"/"+lStrEmployerDsgnName + "/" + lDtSancDate ,rightAlign));
							rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName,rightAlign));

							dataList.add(rowList);*/




							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);



							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss1"),empName,gpfAccNo,lStrDsgnName),
									CenterBoldAlignVO));
							dataList.add(rowList);  

							if(lStrGender.equalsIgnoreCase("M"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData(space(10)+ MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11a"),empName,lStrDsgnName,gpfAccNo),
										CenterAlignVO));
								dataList.add(rowList);  
							}


							if(lStrGender.equalsIgnoreCase("F"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11b"),empName,lStrDsgnName,gpfAccNo),
										CenterAlignVO));
								dataList.add(rowList);  
							}
							if(lStrGender.equalsIgnoreCase("T"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData(space(10)+ MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11c"),empName,lStrDsgnName,gpfAccNo),
										CenterAlignVO));
								dataList.add(rowList);  
							}
							//done
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA12"),empName,gpfAccNo,lStrDsgnName),
									leftUnderlineBold));
							dataList.add(rowList); 
							//sds
							if(lStrGender.equalsIgnoreCase("M"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Retirement1a"),lStrOfficeName,empName,lStrDsgnName,lStrRetirementDate,gpfAccNo),
										leftAlign));
								dataList.add(rowList);  
							}
							if(lStrGender.equalsIgnoreCase("F"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Retirement1b"),lStrOfficeName,empName,lStrDsgnName,lStrRetirementDate,gpfAccNo),
										leftAlign));
								dataList.add(rowList);  
							}
							if(lStrGender.equalsIgnoreCase("T"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Retirement1c"),lStrOfficeName,empName,lStrDsgnName,lStrRetirementDate,gpfAccNo),
										leftAlign));
								dataList.add(rowList);  
							}

							if(lStrGender.equalsIgnoreCase("M"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Retirement2a"),lStrEmployerOfficeName+"/"+lStrEmployerDsgnName ,empName,lStrDsgnName,gpfAccNo,lStrRetirementDate,sancAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(sancAmount))),
										leftAlign));

								dataList.add(rowList);  
							}
							if(lStrGender.equalsIgnoreCase("F"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Retirement2b"),lStrEmployerOfficeName+"/"+lStrEmployerDsgnName ,empName,lStrDsgnName,gpfAccNo,lStrRetirementDate,sancAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(sancAmount))),
										leftAlign));

								dataList.add(rowList);  
							}
							if(lStrGender.equalsIgnoreCase("T"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Retirement2c"),lStrEmployerOfficeName+"/"+lStrEmployerDsgnName ,empName,lStrDsgnName,gpfAccNo,lStrRetirementDate,sancAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(sancAmount))),
										leftAlign));

								dataList.add(rowList);  
							}

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);


							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA18"),""),
									rightAlign));
							dataList.add(rowList); */

							rowList = new ArrayList<Object>();
							//rowList.add(new StyledData(ShortName+"/"+lStrEmployerDsgnName + "/" + lDtSancDate ,rightAlign));
							rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName,rightBoldAlignVO));

							dataList.add(rowList);



							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);


							//GPF.NinetyPerNRA18 last part

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA19"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 
							
							
							StringBuilder lSBQuery2 = new StringBuilder();
							String AgCircle="";
							List lstrList=null;


							lSBQuery2.append( " SELECT rlt.AC_MAINTAINED_BY FROM  ORG_USER_mst mst ");
							lSBQuery2.append( "inner join ORG_USERPOST_RLT user on mst.USER_ID=user.USER_ID ");
							lSBQuery2.append( "inner join RLT_DCPS_PAYROLL_EMP rlt on rlt.POST_ID=  user.POST_ID ");
							lSBQuery2.append( "where mst.USER_NAME ='"+lStrSevaarthId+"'" ); 


							Query lQuery = ghibSession.createSQLQuery(lSBQuery2.toString());


							gLogger.info("Query is:::::::::"+lSBQuery2.toString());

							lstrList = lQuery.list();
							if (lstrList.size() != 0) {

								AgCircle = lstrList.get(0).toString();

								gLogger.info("AgCircle is ::::::"+AgCircle);
								if(AgCircle.equalsIgnoreCase("700092")) //A.G.Mumbai
								{
									 AG=1; 
								}
								if(AgCircle.equalsIgnoreCase("700093")) //nagpur
								{ 
									AG=2;
								}
										
								

							}			
							
							
							if(AG==1)
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21aa"),
										leftAlign));
								dataList.add(rowList);  
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22aa"),
										leftAlign));
								dataList.add(rowList); 

								rowList = new ArrayList<Object>();
								rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24aa"),officeofDDOWhereBillGenerated),
										leftAlign));
								dataList.add(rowList); 
								
								/*rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
										leftAlign));
								dataList.add(rowList); 
								gLogger.info("in report end::::::");*/
								
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25aa"),empName,lStrDsgnName,lStrOfficeName),
										leftAlign));
								dataList.add(rowList);
								
							}
							else if(AG==2)
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( gObjRsrcBndleForSPL90.getString("GPF.GPF.NinetyPerNRA23bb"),
										leftAlign));
								dataList.add(rowList);  
								
								/*rowList = new ArrayList<Object>();
								rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24bb"),
										leftAlign));
								dataList.add(rowList); 
	*/
								
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24bb"),officeofDDOWhereBillGenerated),
										leftAlign));
								dataList.add(rowList); 
								gLogger.info("in report end::::::");
								
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25aa"),empName,lStrDsgnName,lStrOfficeName),
										leftAlign));
								dataList.add(rowList);
							}
							else
							{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA20"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList);  
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA23"),lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
									leftAlign));
							dataList.add(rowList); 
							gLogger.info("in report end::::::");
							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25"),empName,lStrDsgnName,lStrOfficeName),
									leftAlign));
							dataList.add(rowList);
							
							}
							
							

							
			/*				rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA20"),lStrTreasuryNameofBillGenDDO),
									leftAlign));
							dataList.add(rowList); 
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA23"),lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
									leftAlign));

							dataList.add(rowList); 


							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(empName,leftAlign));
							dataList.add(rowList); 
							 
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25"),empName,lStrDsgnName,lStrOfficeName),
									leftAlign));
							dataList.add(rowList);*/
						}

						else if(SpecialCase90.equalsIgnoreCase("800065") )
						{
							gLogger.info("In :::Final::resign dismiss:");

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							//+space(10)+new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA1b") ,leftAlign)
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA1a"),leftBoldAlignVO));
							dataList.add(rowList);
							
							rowList = new ArrayList<Object>();
							rowList.add(space(10)+new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA1b"),leftAlign));
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA2"),lStrOfficeName,"---------") ,leftAlign));
							dataList.add(rowList);

							//rowList.addAll((Collection<? extends Object>) new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA1b"),leftAlign));

							if(lStrGender.equalsIgnoreCase("M"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5a"),empName,lStrDsgnName,"--------"),
										leftAlign));
								dataList.add(rowList);
							}

							if(lStrGender.equalsIgnoreCase("F"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5b"),empName,lStrDsgnName,"--------"),
										leftAlign));
								dataList.add(rowList);
							}
							if(lStrGender.equalsIgnoreCase("T"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( space(10)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA5c"),empName,lStrDsgnName,"--------"),
										leftAlign));
								dataList.add(rowList);
							}

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA1c"),
							leftAlign));
							dataList.add(rowList);
							//shortDesgn+ShrtOff+fin_year

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA7"),ShortName+"/"+lStrEmployerDsgnName+"/"+"Final Withdrawal"+"/"+lDtSancDate 
							),
							leftAlign));
							dataList.add(rowList);

							
							
							

							//des+officenameemploy+'”dd/mmyyy'
							/*gLogger.info("san off desgn"+emplyeeOfficerName+"desgggggggg"+lStrEmployerDsgnName);
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA8"),""),
									rightAlign));



							dataList.add(rowList);
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName+","+lDtSancDate1  ,rightAlign));
							dataList.add(rowList);*/

							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA8"),""),
									rightAlign));



							dataList.add(rowList);*/
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName  ,rightAlign));
							dataList.add(rowList);



							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(lDtSancDate1  ,rightAlign));
							dataList.add(rowList);



							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);


							rowList = new ArrayList<Object>(); 
							//rowList.add(space(30)+new StyledData(gObjRsrcBndleForSPL90.getString("GPF.Dismiss1a"),CenterBoldAlignVO));
							//dataList.add(rowList);
							
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss1")," "+space(35)),
									CenterBoldAlignVO));
							
							dataList.add(rowList);
							
							
							/*rowList = new ArrayList<Object>(); 
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss1b")," "+space(35)),
									CenterAlignVO)+space(10));
							
							dataList.add(rowList);*/
							/*rowList = new ArrayList<Object>(); 
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss1b"),""),
									CenterAlignVO));
							dataList.add(rowList);*/


							if(lStrGender.equalsIgnoreCase("M"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData(space(5)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11a"),empName,lStrDsgnName,gpfAccNo),
										CenterAlignVO));
								dataList.add(rowList);  
							}
							if(lStrGender.equalsIgnoreCase("F"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData(space(5)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11b"),empName,lStrDsgnName,gpfAccNo),
										CenterAlignVO));
								dataList.add(rowList);  
							}
							if(lStrGender.equalsIgnoreCase("T"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData(space(5)+MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA11c"),empName,lStrDsgnName,gpfAccNo),
										CenterAlignVO));
								dataList.add(rowList);  
							}


							//doneBOLD
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA12"),empName,gpfAccNo,lStrDsgnName),
									leftUnderlineBold));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);


							//lStrEmployerOfficeName+","+lStrEmployerDsgnName
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss2"),lStrEmployerOfficeName+","+lStrEmployerDsgnName),
									leftAlign));
							dataList.add(rowList); 


							//done
							if(lStrGender.equalsIgnoreCase("M"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss3a"),empName,lStrDsgnName,gpfAccNo,lStrRetirementDate),
										leftAlign));
								dataList.add(rowList); 
							}
							if(lStrGender.equalsIgnoreCase("F"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss3b"),empName,lStrDsgnName,gpfAccNo,lStrRetirementDate),
										leftAlign));
								dataList.add(rowList); 
							}
							if(lStrGender.equalsIgnoreCase("T"))
							{
								rowList = new ArrayList<Object>();
								rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss3c"),empName,lStrDsgnName,gpfAccNo,lStrRetirementDate),
										leftAlign));
								dataList.add(rowList); 
							}

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.Dismiss4"),sancAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(sancAmount))),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							/*
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA18"),empName,gpfAccNo,lStrDsgnName),
									rightAlign));
							dataList.add(rowList); 


							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(lStrEmployerOfficeName+"/"+lStrEmployerDsgnName + "/" + lDtSancDate ,rightAlign));
							dataList.add(rowList);*/

							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA18"),""),
									rightAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							//rowList.add(new StyledData(ShortName+"/"+lStrEmployerDsgnName + "/" + lDtSancDate ,rightAlign));
							rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName,rightAlign));

							dataList.add(rowList);*/


							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA18"),""),
									rightAlign));
							dataList.add(rowList); */

							
							
							/*ArrayList row6 = new ArrayList();


							boldStyleVO = new StyleVO[2];
							boldStyleVO[0] = new StyleVO();
							boldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
							boldStyleVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER); 
							boldStyleVO[1] = new StyleVO();
							boldStyleVO[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
							
							boldStyleVO[1].setStyleValue("Left"); 
							styledHeader = new StyledData();
							styledHeader.setStyles(boldStyleVO);
							styledHeader.setData(gObjRsrcBndle.getString("GPFREPORT.NOTE"));
							//dataStyle2.addStyle(IReportConstants.STYLE_FONT_FAMILY, "Rupee Foradian");
							styledHeader.setColspan(9);
							row6.add(styledHeader);
							DataList.add(row6);	*/
							
							
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(lStrEmployerDsgnName+","+lStrEmployerOfficeName,rightBoldAlignVO));
							dataList.add(rowList);
							
							

							/*rowList = new ArrayList<Object>();
							
							boldStyleVO = new StyleVO[2];
							boldStyleVO[0] = new StyleVO();
							boldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
							boldStyleVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER); 
							boldStyleVO[1] = new StyleVO();
							boldStyleVO[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
							
							boldStyleVO[1].setStyleValue("Left"); 
							styledHeader = new StyledData();
							styledHeader.setStyles(boldStyleVO);
							styledHeader.setData(lStrEmployerDsgnName+","+lStrEmployerOfficeName);
							//dataStyle2.addStyle(IReportConstants.STYLE_FONT_FAMILY, "Rupee Foradian");
							styledHeader.setColspan(9);
							rowList.add(styledHeader);*/
							

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(newline);
							dataList.add(rowList);

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA19"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA20"),""),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA21"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA22"),empName,gpfAccNo,lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA23"),lStrDsgnName),
									leftAlign));
							dataList.add(rowList); 

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA24"),officeofDDOWhereBillGenerated),
									leftAlign));

							dataList.add(rowList); 

							/*rowList = new ArrayList<Object>();
							rowList.add(new StyledData(empName,leftAlign));
							dataList.add(rowList);*/

							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForSPL90.getString("GPF.NinetyPerNRA25"),empName,","+lStrDsgnName,","+lStrOfficeName),
									leftAlign));
							dataList.add(rowList);
							
							//addedc by gokarna 12345
							/*rowList = new ArrayList<Object>();
							StyledData dataStyle7 = new StyledData();
	                       	  dataStyle7.setData(ShortName);
	                       	  dataStyle7.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
	                       	  dataStyle7.addStyle(IReportConstants.STYLE_FONT_WEIGHT, IReportConstants.VALUE_FONT_WEIGHT_BOLD);
	                       	 // int colSpan2=2+(noOfSlabs*2);
	                       	  dataStyle7.setColspan(1);
	                       	rowList.add(dataStyle7);
	                       	dataList.add(rowList);*/
							


						}

					}


					else if(lStrDisbursementNumber == 0)
					{
						//in rowlist
						gLogger.info("in else  deptName::order for First Disbursement::::"+deptName);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(deptName, CenterBoldAlignVO));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE68"), designName,currYear+"",strEmpIntls,
								lStrTransactionId,loanApprovedDate) ,CenterAlignVO));
						dataList.add(rowList);


						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINENRAVISHAYa"),empName,gpfAccNo,lStrDsgnName),
									CenterAlignVO));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINENRAVISHAYb"),empName,gpfAccNo,lStrDsgnName),
									CenterAlignVO));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINENRAVISHAYc"),empName,gpfAccNo,lStrDsgnName),
									CenterAlignVO));
							dataList.add(rowList);
						}

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);


						Double lDblTemp = OpeningBalance + lDblRegSub + lDblRecAmt;
						gLogger.error("lDblTemp"+lDblTemp);

						Long lLngTemp = lDblTemp.longValue();
						gLogger.error("lLngTemp"+lLngTemp);


						rowList = new ArrayList();
						gLogger.info("lStrPurposeName $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+lStrPurposeName);
						if (!reqType.equals("Final") || !reqType.equals("FW"))
						{
							if(Integer.parseInt(lObjAdvDtls[8].toString())==12 || Integer.parseInt(lObjAdvDtls[8].toString())== 11)
							{
								if(lStrGender.equalsIgnoreCase("M"))
								{
									rowList.add(
											MessageFormat.format(this.gObjRsrcBndleForRA.getString("GPF.ORDERLINE446a"), new Object[] { 
												lStrTreasuryName, empName,puposeOfAdv,gpfAccNo,lDblAmtSancGpf ,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDblAmtSancGpf.longValue())),ruleID,lStrDsgnName }));
								}
								if(lStrGender.equalsIgnoreCase("F"))
								{
									rowList.add(
											MessageFormat.format(this.gObjRsrcBndleForRA.getString("GPF.ORDERLINE446b"), new Object[] { 
												lStrTreasuryName, empName,puposeOfAdv,gpfAccNo,lDblAmtSancGpf ,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDblAmtSancGpf.longValue())),ruleID,lStrDsgnName }));
								}
								if(lStrGender.equalsIgnoreCase("T"))
								{
									rowList.add(
											MessageFormat.format(this.gObjRsrcBndleForRA.getString("GPF.ORDERLINE446c"), new Object[] { 
												lStrTreasuryName, empName,puposeOfAdv,gpfAccNo,lDblAmtSancGpf ,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDblAmtSancGpf.longValue())),ruleID,lStrDsgnName }));
								}

							}

							else
							{
								if(lStrGender.equalsIgnoreCase("M"))
								{
									rowList.add(

											MessageFormat.format(this.gObjRsrcBndleForRA.getString("GPF.ORDERLINE446a"), new Object[] { 

												lStrTreasuryName, empName,puposeOfAdv,gpfAccNo,lDblAmtSancGpf ,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDblAmtSancGpf.longValue())),ruleID,lStrDsgnName }));
								}
								if(lStrGender.equalsIgnoreCase("F"))
								{
									rowList.add(

											MessageFormat.format(this.gObjRsrcBndleForRA.getString("GPF.ORDERLINE446b"), new Object[] { 

												lStrTreasuryName, empName,puposeOfAdv,gpfAccNo,lDblAmtSancGpf ,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDblAmtSancGpf.longValue())),ruleID,lStrDsgnName }));
								}
								if(lStrGender.equalsIgnoreCase("T"))
								{
									rowList.add(

											MessageFormat.format(this.gObjRsrcBndleForRA.getString("GPF.ORDERLINE446c"), new Object[] { 

												lStrTreasuryName, empName,puposeOfAdv,gpfAccNo,lDblAmtSancGpf ,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDblAmtSancGpf.longValue())),ruleID,lStrDsgnName }));
								}

							}


							dataList.add(rowList);

						}
						rowList = new ArrayList<Object>();

						rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE431"),empName,lStrDsgnName));
						dataList.add(rowList);

						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINENRATHIRDPOINTa"),empName,lStrBasicPay,lStrDsgnName));
							dataList.add(rowList);
						}

						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINENRATHIRDPOINTb"),empName,lStrBasicPay,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINENRATHIRDPOINTc"),empName,lStrBasicPay,lStrDsgnName));
							dataList.add(rowList);
						}

						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINEFOURTHPOINTa"),empName,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINEFOURTHPOINTb"),empName,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINEFOURTHPOINTc"),empName,lStrDsgnName));
							dataList.add(rowList);
						}

						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINEFIFTHPOINTa"),empName,loanApprovedDate,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINEFIFTHPOINTb"),empName,loanApprovedDate,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINEFIFTHPOINTc"),empName,loanApprovedDate,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINESIXPOINTa"),empName,loanApprovedDate,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINESIXPOINTb"),empName,loanApprovedDate,lStrDsgnName));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINESIXPOINTc"),empName,loanApprovedDate,lStrDsgnName));
							dataList.add(rowList);
						}

						rowList = new ArrayList<Object>();
						rowList.add("" + space(1));
						dataList.add(rowList);


						//Correct Values of dashboard
						
						

						String lStrIntSixPay0910 = "0";
						String lStrIntSixPay1011 = "0";
						String lStrIntSixPay1112 = "0";
						String lStrIntSixPay1213 = "0";
						String lStrIntSixPay1314 = "0";


						 SixPayAmounts = lObjGPFRequestProcess.getSixPayAmounts(gpfAccNo,lIntFinYrId.longValue());
						 size = SixPayAmounts.size();
						gLogger.info("size"+size);


						if (SixPayAmounts != null && SixPayAmounts.size() > 0) {

							Object[] lArrObj = (Object[]) SixPayAmounts.get(0);

							if(lArrObj[0] != null){
								lStrSixPay0910 = lArrObj[0].toString();
								gLogger.info("lStrSixPay0910:::::::::"+lStrSixPay0910);
							}

							if(lArrObj[1] != null){
								lStrSixPay1011 = lArrObj[1].toString();
								gLogger.info("lStrSixPay1011:::::::::"+lStrSixPay1011);
							}

							if(lArrObj[2] != null){
								lStrSixPay1112 = lArrObj[2].toString();
								gLogger.info("lStrSixPay1112:::::::::"+lStrSixPay1112);
							}

							if(lArrObj[3] != null){
								lStrSixPay1213 = lArrObj[3].toString();
								gLogger.info("lStrSixPay1213:::::::::"+lStrSixPay1213);
							}

							if(lArrObj[4] != null){
								lStrSixPay1314 = lArrObj[4].toString();
								gLogger.info("lStrSixPay1314:::::::::"+lStrSixPay1314);
							}

						}


						List SixPayIntAmounts = lObjGPFRequestProcess.getSixPayAmountsNew(gpfAccNo,lIntFinYrId.longValue());
						int s = SixPayAmounts.size();
						gLogger.info("size"+size);


						if (SixPayIntAmounts != null && SixPayIntAmounts.size() > 0) {

							Object[] lArrIntObj = (Object[]) SixPayIntAmounts.get(0);

							if(lArrIntObj[0] != null){
								lStrIntSixPay0910 = lArrIntObj[0].toString();
								gLogger.info("lStrIntSixPay0910:::::::::"+lStrIntSixPay0910);
							}

							if(lArrIntObj[1] != null){
								lStrIntSixPay1011 = lArrIntObj[1].toString();
								gLogger.info("lStrIntSixPay1011:::::::::"+lStrIntSixPay1011);
							}

							if(lArrIntObj[2] != null){
								lStrIntSixPay1112 = lArrIntObj[2].toString();
								gLogger.info("lStrIntSixPay1112:::::::::"+lStrIntSixPay1112);
							}

							if(lArrIntObj[3] != null){
								lStrIntSixPay1213 = lArrIntObj[3].toString();
								gLogger.info("lStrIntSixPay1213:::::::::"+lStrIntSixPay1213);
							}

							if(lArrIntObj[4] != null){
								lStrIntSixPay1314 = lArrIntObj[4].toString();
								gLogger.info("lStrIntSirxPay1314:::::::::"+lStrIntSixPay1314);
							}

						}

						Double lDblOpeningBal = lObjGPFRequestProcess.getOpeningBalForCurrYearNew(gpfAccNo, lIntFinYrId.longValue());

						gLogger.info("lDblOpeningBal::::::::::"+lDblOpeningBal);
						if(lDblOpeningBal==0.0)
						{
							gLogger.info("lDblOpeningBal::::zero::::::"+lDblOpeningBal);
							lDblOpeningBal=lObjGPFRequestProcess.getOpeningBalance(gpfAccNo, lIntFinYrId.longValue());
							if(lIntFinYrId == PayableYrSixPay0910){
								if(lStrIntSixPay0910 != null && lStrIntSixPay0910 != ""){
									lDblOpeningBal = lDblOpeningBal + Double.valueOf(lStrIntSixPay0910);
								}
							}

							if(lIntFinYrId == PayableYrSixPay1011){
								if(lStrIntSixPay1011 != null && lStrIntSixPay1011 != ""){
									lDblOpeningBal = lDblOpeningBal + Double.valueOf(lStrIntSixPay1011);
								}
							}

							if(lIntFinYrId == PayableYrSixPay1112){
								if(lStrIntSixPay1112 != null && lStrIntSixPay1112 != ""){
									lDblOpeningBal = lDblOpeningBal + Double.valueOf(lStrIntSixPay1112);
								}
							}

							if(lIntFinYrId == PayableYrSixPay1213){
								if(lStrIntSixPay1213 != null && lStrIntSixPay1213 != ""){
									lDblOpeningBal = lDblOpeningBal + Double.valueOf(lStrIntSixPay1213);
								}
							}

							if(lIntFinYrId == PayableYrSixPay1314){
								if(lStrIntSixPay1314 != null && lStrIntSixPay1314 != ""){
									lDblOpeningBal = lDblOpeningBal + Double.valueOf(lStrIntSixPay1314);
								}
							}
						}
						gLogger.info("lDblOpeningBal::ww::zero::::::"+lDblOpeningBal);
						if(reqType.equalsIgnoreCase("FW")){

							lDblOpeningBal = lDblOpeningBal + Double.valueOf(lStrSixPay1314) + Double.valueOf(lStrSixPay1213) + Double.valueOf(lStrSixPay1112) + Double.valueOf(lStrSixPay1011) + Double.valueOf(lStrSixPay0910);
						}
						
						
						// 2 .
						 temp = lObjGPFRequestProcess.getRegularSubscription(lStrSevaarthId,lLngCurrMonth,lLngCurrYr);

						 lDblRegSub = 0d;
						if(temp != null && temp != ""){
							lDblRegSub = Double.valueOf(temp);
							gLogger.error("lDblRegSub"+lDblRegSub);
						}

						 lLngPaybillMonth = 0l;
						Long lDblRegSubFrCurrMonth=0l;

						 month = lObjGPFRequestProcess.getPaybillMonth(lStrSevaarthId,lLngCurrYr);

						if(month != 0){
							lLngPaybillMonth = month;

						}


						 lDblRecAmt = Double.valueOf(lObjGPFRequestProcess.getRecoveryAmt(gpfAccNo,lLngCurrYr,lStrSevaarthId,lLngPaybillMonth));
						lDblRegSubFrCurrMonth=lObjGPFRequestProcess.getregsub(lStrSevaarthId,lLngCurrYr,lLngPaybillMonth);
						
						String advancedSanctioned="0";
						String withdrawlSanctioned="0";
						int advancedSanctionedN=0;
						//gpfAccNo, lIntCurrFinYearId.longValue()
						List lstAdvWithSanction=lObjGPFRequestProcess.getAdvWithSanctionedAmnt(gpfAccNo, lIntFinYrId.longValue());
						if(lstAdvWithSanction!=null && lstAdvWithSanction.size()>0)
						{
							Object[] advWithObje=(Object[]) lstAdvWithSanction.get(0);
							if(advWithObje!=null && advWithObje[0]!=null)
							{
								advancedSanctioned=advWithObje[0].toString();
							}
							if(advWithObje!=null && advWithObje[1]!=null)
							{
								withdrawlSanctioned=advWithObje[1].toString();
							}
						}

						gLogger.info("Advanced SAnctioned From Initial data Entry screen is ::::::::"+advancedSanctioned);
						gLogger.info("withdrawlSanctioned From Initial data Entry screen is ::::::::"+withdrawlSanctioned);

						
						
						Double WithdrawalSancDouble=0d;
						WithdrawalSancDouble = lObjGPFRequestProcess.getWithdrawalSanc(gpfAccNo,lIntFinYrId.longValue());
						gLogger.error("lDblWithdrawalSanc"+lDblWithdrawalSanc);


						
						
						String WithdrawalSanctioned = "0";
						Double WithdrawalSanctionedN=0d;
						if(WithdrawalSancDouble != null){
							gLogger.error("WithdrawalSancDouble not null::::::::::"+WithdrawalSancDouble);
							WithdrawalSanctionedN = Double.parseDouble(withdrawlSanctioned.toString())+WithdrawalSancDouble;
						}
						else
						{
							WithdrawalSanctionedN=Double.parseDouble(withdrawlSanctioned.toString());
						}


						gLogger.info("Advanced SAnctioned From Currently approved loan is::::::::"+advancedSanctionedN);
						gLogger.info("withdrawlSanctioned From Currently approved loan is ::::::::"+WithdrawalSanctionedN);
						
						
						/*if(WithdrawalSanctionedN==0.0){}
						{
							
						}*/
						
						List advanceHistoryDtls = lObjGPFRequestProcess.getAdvanceHistory(gpfAccNo, lIntFinYrId.longValue());


						if (advanceHistoryDtls != null && advanceHistoryDtls.size() > 0) {
							Object[] historyObj = (Object[]) advanceHistoryDtls.get(0);
							if (historyObj[0].equals("RA")) {
								advancedSanctionedN = Integer.parseInt(advancedSanctioned)+Integer.parseInt(historyObj[2].toString());
							} 
							else
							{
								advancedSanctionedN=Integer.parseInt(advancedSanctioned);
							}

							/*else {
								lDblWithdrawalSanc = historyObj[1].toString();
							}*/


							if (advanceHistoryDtls.size() > 1) {
								historyObj = (Object[]) advanceHistoryDtls.get(1);
								if (historyObj[0].equals("RA")) {
									gLogger.error("****************************************");
								advancedSanctionedN = advancedSanctionedN +Integer.parseInt(historyObj[2].toString());
								
								gLogger.error("advancedSanctionedN:::::::::::"+advancedSanctionedN);
								}
								else
								{
									advancedSanctionedN = advancedSanctionedN +Integer.parseInt(historyObj[1].toString());
								}
							}
						}
						
						if(lstAdvWithSanction!=null && lstAdvWithSanction.size()>0 && advanceHistoryDtls.size()==0)
						{
							gLogger.error("in ifffffffff");
							inputMap.put("WithdrawalSanctioned", withdrawlSanctioned);
							inputMap.put("lStrAmtSanctioned", advancedSanctioned); 

						}
						else 
						{
							gLogger.error("in elseeeee");
						inputMap.put("WithdrawalSanctioned", WithdrawalSanctionedN);
						inputMap.put("lStrAmtSanctioned", advancedSanctionedN);
						}

						
						
						
						
						
						
						//
						
						

						ArrayList<Object> sixRowsLeft = new ArrayList<Object>();

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndleForNRA.getString("GFP.SrNo"));
						rowList.add(gObjRsrcBndleForNRA.getString("GFP.Particulars"));
						rowList.add(gObjRsrcBndleForNRA.getString("GFP.Amount"));
						sixRowsLeft.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add("1.");
						rowList.add(new StyledData( preFinYear
								+ " " + gObjRsrcBndleForRA.getString("GPF.ORDERLINE436"), rowsFontsVO));
						rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + lDblOpeningBal+"/-");
						sixRowsLeft.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add("2.");
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE26")+ " "+startDate+gObjRsrcBndleForRA.getString("GPF.ORDERLINE430")+""+loanApprovedDate+""+gObjRsrcBndleForRA.getString("GPF.ORDERLINE443")+""+gObjRsrcBndleForRA.getString("GPF.ORDERLINE444")+lDblRegSubFrCurrMonth+gObjRsrcBndleForRA.getString("GPF.ORDERLINE433"),rowsFontsVO));  
						rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + lDblRegSub +"/-");
						sixRowsLeft.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add("3.");
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE439") + " "+startDate+""+gObjRsrcBndleForRA.getString("GPF.ORDERLINE430")+""+loanApprovedDate+gObjRsrcBndleForRA.getString("GPF.ORDERLINE444")+lDblRecAmt+gObjRsrcBndleForRA.getString("GPF.ORDERLINE437"),rowsFontsVO)); 
						rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + lDblRecAmt +"/-");
						sixRowsLeft.add(rowList);



						rowList = new ArrayList<Object>();
						rowList.add("4.");
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE30"), rowsFontsVO));
						rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (lDblRecAmt + lDblRegSub + lDblOpeningBal)+"/-");
						sixRowsLeft.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add("5.");

						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE439")+""+startDate+gObjRsrcBndleForRA.getString("GPF.ORDERLINE442")+loanApprovedDate+gObjRsrcBndleForRA.getString("GPF.ORDERLINE445"),rowsFontsVO));


						//rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + lDblWithAmt+"/-");
						if(lstAdvWithSanction!=null && lstAdvWithSanction.size()>0)
						{
						//rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (Double.parseDouble(advancedSanctioned) + Double.parseDouble(withdrawlSanctioned.toString() +"/-"));
						
						rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (Double.parseDouble(advancedSanctioned) + Double.parseDouble(withdrawlSanctioned.toString()) +"/-"));
						}
						else
						{
							rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (advancedSanctionedN + WithdrawalSanctionedN) +"/-");
						}
						
						
						
						sixRowsLeft.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add("6.");

						if(lstAdvWithSanction!=null && lstAdvWithSanction.size()>0)
						{
						rowList = new ArrayList<Object>();
						rowList.add("6.");
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE35"), rowsFontsVO));
						//rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (total - lDblWithdrawalSanc)+"/-");
						//commented by gokarna on 24/09/2015
						rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (lDblRecAmt + lDblRegSub + lDblOpeningBal - Double.parseDouble(advancedSanctioned)- Double.parseDouble(withdrawlSanctioned.toString()) + "/-" ));
						//rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " +(  (lDblRecAmt + lDblRegSub + OpeningBalance ) - (lDblWithAmt + lDblLoanPrinAmt ) )+"/-");
						}
						
						else
						{
						rowList = new ArrayList<Object>();
						rowList.add("6.");
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE35"), rowsFontsVO));
						//rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (total - lDblWithdrawalSanc)+"/-");
						//commented by gokarna on 24/09/2015
						rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (lDblRecAmt + lDblRegSub + lDblOpeningBal - (advancedSanctionedN)- (WithdrawalSanctionedN.doubleValue()) + "/-" ));
						
						
						}
						
						
						//commented by gokarna on 24/09/2015
						//rowList.add(gObjRsrcBndleForRA.getString("GPF.ORDERLINE47") + " " + (lDblRecAmt + lDblRegSub + OpeningBalance - (lDblWithAmt)+"/-" ));

						sixRowsLeft.add(rowList);

						td = new TabularData(sixRowsLeft);
						rowList = new ArrayList<Object>();
						rowList.add(MessageFormat.format(gObjRsrcBndleForRA.getString("GPF.ORDERLINE435"),empName,loanApprovedDate));
						dataList.add(rowList);
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

						rowList = null;

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE401"), leftAlign));
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE403"), leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(lStrTreasuryName);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE404"), leftAlign));
						dataList.add(rowList);
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE404"), leftAlign));
						dataList.add(rowList);
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE404"), leftAlign));
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE407"),leftAlign));
						dataList.add(rowList);
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE408"),leftAlign));
						dataList.add(rowList);
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE409"),leftAlign));
						dataList.add(rowList);


						//gh
						if(lStrGender.equalsIgnoreCase("M"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.Point3a"),leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("F"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.Point3b"),leftAlign));
							dataList.add(rowList);
						}
						if(lStrGender.equalsIgnoreCase("T"))
						{
							rowList = new ArrayList<Object>();
							rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.Point3c"),leftAlign));
							dataList.add(rowList);
						}


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrEmployerDsgnName, leftAlign));
						dataList.add(rowList);
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(empName+','+lStrDsgnName,leftAlign));
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);
						rowList = new ArrayList<Object>();
						rowList.add(newline);
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
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.ORDERLINE415"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE1"),leftAlign));
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE2"),leftAlign));
						dataList.add(rowList);


						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE3"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE4"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE5"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE6"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE7"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE8"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE9"),leftAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndleForRA.getString("GPF.NRANOTE10"),leftAlign));
						dataList.add(rowList);

					}

				}


				if(chkForGeneratedBill(gpfAccNo, lStrTransactionId) == "N")
				{	


					gLogger.info("************chkForGeneratedBill*********"+chkForGeneratedBill(gpfAccNo, lStrTransactionId) );
					String lStrddocodetowhichbillforward = (String) report.getParameterValue("ddocode");
					gLogger.info("************lStrddocodetowhichbillforward*********"+lStrddocodetowhichbillforward);
					String locationcode = getLocationCode(lStrddocodetowhichbillforward);

					updateLoanBillID(lStrTransactionId);


					inputMap.put("gpfAccNo", gpfAccNo);
					inputMap.put("transactionId", lStrTransactionId);

					inputMap.put("AdvanceType", reqType);
					inputMap.put("orderNo", lStrOrderId);
					inputMap.put("orderDate", lStrOrderDate);
					inputMap.put("openingBalc", OpeningBalance);
					inputMap.put("regularSub", Double.parseDouble(lObjGPFAccountBal[0].toString()));
					inputMap.put("advanceRecovery", Double.parseDouble(lObjGPFAccountBal[1].toString()));
					inputMap.put("advanceSanctioned", lDblWithdrawalSanc);
					//gokarna
					inputMap.put("locationcode", locationcode);

					inputMap.put("lObjLoginVO", lObjLoginVO);

					ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
					inputMap.put("serviceLocator", serviceLocator);

					resultObject = serviceLocator.executeService("GenerateBillDataGPF",inputMap);					
				}


			} 
		}
		catch (Exception e) {
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
	public String getLocationCode(String lstrddocode) throws Exception {

		gLogger.info("lstrddocode"+lstrddocode);
		//gLogger.info("lLngYearId"+lLngYearId);

		String lDblOpeningBal = null;
		List lLstOpeningBal = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		try {
			lSBQuery
			.append(" SELECT LOCATION_CODE FROM ORG_DDO_MST where DDO_CODE='"+lstrddocode+"' ");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstOpeningBal = lQuery.list();
			if (lLstOpeningBal != null && lLstOpeningBal.size() > 0) {
				lDblOpeningBal =(lLstOpeningBal.get(0).toString());
				gLogger.info("lDblOpeningBal"+lDblOpeningBal);
			} else {
				lDblOpeningBal = "";
			}
		} catch (Exception e) {
			gLogger.error("Exception in getOpeningBalForCurrYear of GPFRequestProcessDAOImpl  : ", e);			
		}
		return lDblOpeningBal;
	}
	public String chkForGeneratedBill(String lStrGpfAccNo, String lStrTransactionId)
	{
		gLogger.info("chkForGeneratedBill**"+lStrGpfAccNo+lStrTransactionId);
		String lStrChkBill = "";
		List lLstData = null;

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT BILL_DTLS_ID FROM MST_GPF_BILL_DTLS ");
			lSBQuery.append("WHERE GPF_ACC_NO = '"+lStrGpfAccNo+"' AND STATUS_FLAG <>'2' AND TRANSACTION_ID ='"+lStrTransactionId+"' ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());


			lLstData = lQuery.list();

			if(lLstData != null && lLstData.size() > 0){
				lStrChkBill = "Y";
			}else{
				lStrChkBill = "N";
			}

			gLogger.error("chkForGeneratedBill************"+lSBQuery.toString());
		}catch (Exception e) {
			gLogger.error("Exception in chkForGeneratedBill:" + e, e);
		}

		return lStrChkBill;
	}

	public Double getAmtSanctioned(String lStrTransactionId, String reqType){

		gLogger.info("getAmtSanctioned"+lStrTransactionId+"reqType******"+reqType);


		List sanction = null;
		Double lDblSanctionedAmt = 0d;

		StringBuilder lSBQuery = new StringBuilder();

		reqType="NRA";

		gLogger.info("reqType********"+reqType);
		try {

			if(reqType.equalsIgnoreCase("NRA") || reqType.equalsIgnoreCase("FW") || reqType.equalsIgnoreCase("Non-Refundable Advance")){
				lSBQuery.append( " SELECT nvl(advance.amount_Sanctioned,0) " ); 
				lSBQuery.append( " FROM MST_GPF_ADVANCE advance where advance.TRANSACTION_ID=:lStrTransactionId " );
			}


			if(reqType.equalsIgnoreCase("RA")){
				lSBQuery.append( " SELECT nvl(advance.payable_Amount,0)" ); 
				lSBQuery.append( " FROM MST_GPF_ADVANCE advance where advance.TRANSACTION_ID=:lStrTransactionId " );

			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("lStrTransactionId", lStrTransactionId);

			gLogger.info("Query is:::::::::"+lSBQuery.toString());

			sanction = lQuery.list();
			if (sanction.size() != 0) {

				lDblSanctionedAmt = (Double)sanction.get(0);
			}

			gLogger.info("lDblSanctionedAmt"+lDblSanctionedAmt+"sanction"+sanction.size());

		} catch (Exception e) {
			gLogger.error("Exception in getAmtSanctioned : ", e);
		}
		return lDblSanctionedAmt;

	}

	public void updateLoanBillID(String trnascationid) {


		gLogger.info("**********in ***********updated*********");
		StringBuilder lSb = new StringBuilder();

		lSb.append("update MST_GPF_ADVANCE set LOAN_BILL_ID  = '"+trnascationid+"' where TRANSACTION_ID='"+trnascationid+"'");

		Query lQuery = ghibSession.createSQLQuery(lSb.toString());

		lQuery.executeUpdate();

		gLogger.info("updated*********");
	}



}
