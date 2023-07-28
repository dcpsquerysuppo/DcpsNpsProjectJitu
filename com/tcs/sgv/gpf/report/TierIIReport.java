
package com.tcs.sgv.gpf.report;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;
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
public class TierIIReport extends DefaultReportDataFinder implements ReportDataFinder {

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
	String gStrLocCode = null;

	private ServiceLocator serv = null;

/*	private ResourceBundle gObjRsrcBndleForRA = ResourceBundle.getBundle("resources/gpf/RefundableOrderMarathi");


	private ResourceBundle gObjRsrcBndleForNRA = ResourceBundle.getBundle("resources/gpf/NonRefundableOrderMarathi");

	private ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/gpf/90PercentWithdrawalOrderMarathi");


	private ResourceBundle gObjRsrcBndleForFW = ResourceBundle.getBundle("resources/gpf/FinalPayOrderMarathi");

*/



	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		Connection con = null;

		requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();
		lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
		System.out.print("lObjLoginVO-->"+lObjLoginVO);
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
		ArrayList<Object> dataList11 = new ArrayList<Object>();
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
			

			//ArrayList dataList11 = new ArrayList();
            ArrayList rowList = new ArrayList();
            
			if (report.getReportCode().equals("80000700")) {
            ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/dcps/dcpsLabels");
			String DdoCOde = (String)report.getParameterValue("ddoCode");
	        String orderId = (String)report.getParameterValue("OrderId");
	        String year = (String)report.getParameterValue("year");
	        String month = (String)report.getParameterValue("month");
	        double PayableTotal1 = Double.parseDouble((String)report.getParameterValue("amount"));
	        String Type = (String)report.getParameterValue("type");
				
				String amountWord = EnglishDecimalFormat.convertWithSpace(new BigDecimal(PayableTotal1));
                String sevaarthId = "NA";
                String url = "NA";
                String EmployeeDetails = "NA";
                String DesignationName = "NA";
                List lstDDoDetails = this.getDDODetails(DdoCOde);

                for(int J = 0; J < lstDDoDetails.size(); ++J) {
                   Object[] tupleSub1 = (Object[])lstDDoDetails.get(J);
                   if (tupleSub1[0] != null) {
                      url = tupleSub1[0].toString();
                   }

                   if (tupleSub1[1] != null) {
                      EmployeeDetails = tupleSub1[1].toString();
                   }

                   if (tupleSub1[2] != null) {
                      sevaarthId = tupleSub1[2].toString();
                   }

                   if (tupleSub1[3] != null) {
                      DesignationName = tupleSub1[3].toString();
                   }
                }

                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                formatter.format(date);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.header"),"") ,CenterBoldAlignVO));
				dataList11.add(rowList);
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.Header1"),"") ,CenterBoldAlignVO));
				dataList11.add(rowList);
				rowList = new ArrayList();
                //String RIGHT1 = gObjRsrcBndleForSPL90.getString("FF.RIGHT") + ".";
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.RIGHT"),"") ,rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(url + ".",""), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(EmployeeDetails + ".",""), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.RIGHT1"),""), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.PRATI"),""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.PRATI1"),""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.LINE"),""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(sevaarthId,""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.VISHAY"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.VISHAY1"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.INITAL"),""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                dataList11.add(rowList);
                
              //rowList.add(new StyledData(space(10)+gObjRsrcBndleForSPL90.getString("GPF.Death2b"),leftAlign));
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(space(20)+gObjRsrcBndleForSPL90.getString("FF.CENTER"),""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.CENTER1")," ")+sevaarthId+" "+ gObjRsrcBndleForSPL90.getString("FF.CENTER1.1"), leftAlign));
                dataList11.add(rowList);
                /*rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(sevaarthId + " "+gObjRsrcBndleForSPL90.getString("FF.CENTER1.1"),""), leftAlign));
                dataList11.add(rowList);*/
                
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(space(20)+gObjRsrcBndleForSPL90.getString("FF.CENTER2"),""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.CENTER2.0"),"")+ new BigDecimal(PayableTotal1)+gObjRsrcBndleForSPL90.getString("FF.CENTER2.2")+" "+amountWord+" "+gObjRsrcBndleForSPL90.getString("FF.CENTER2.3"), leftAlign));
                dataList11.add(rowList);
                /*rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(amountWord+" "+gObjRsrcBndleForSPL90.getString("FF.CENTER2.3"),""), leftAlign));
                dataList11.add(rowList);*/
                /*rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.CENTER1"),""), leftAlign));
                dataList11.add(rowList);*/
                /*rowList = new ArrayList();
                String employee_name = gObjRsrcBndleForSPL90.getString("FF.CENTER1.1") + sevaarthId + " " + gObjRsrcBndleForSPL90.getString("FF.CENTER1.2");
                rowList.add(new StyledData(MessageFormat.format(employee_name,""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(" ",""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(" ",""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.CENTER2"),""), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                Object Sevaarth_id = gObjRsrcBndleForSPL90.getString("FF.CENTER2.1") + new BigDecimal(PayableTotal1) + gObjRsrcBndleForSPL90.getString("FF.CENTER2.2") + amountWord;
                rowList.add(new StyledData(Sevaarth_id, leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.CENTER2.3"), leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.CENTER2.4"), leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(" ", leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(" ", leftboldStyleVO));*/
                //dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(space(20)+gObjRsrcBndleForSPL90.getString("FF.CENTER3"), leftAlign));
                dataList11.add(rowList);
                /*
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.CENTER3.1"), leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.CENTER3.2"), leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.CENTER3.3"), leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData("   ", leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData("   ", leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData("   ", leftboldStyleVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData("   ", leftboldStyleVO));
                dataList11.add(rowList);*/
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM"), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData("   ", rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData("   ", rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM1"), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(DesignationName, rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM2"), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM3"), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                url = "( DDO CODE :" + DdoCOde + " )";
                rowList.add(new StyledData(url, rightAlign));
                dataList11.add(rowList);
				
			}else if(report.getReportCode().equals("80000703")) {
				ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/dcps/dcpsLabels");
	            String DdoCOde2;
	            String [] data;
	            DdoCOde2 = this.getDDODetails1(gStrLocCode);
	            data=DdoCOde2.split("-");
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header1"),"") ,CenterBoldAlignVO));
				dataList11.add(rowList);
				rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header2"),"") ,rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header3"),"") ,rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header4"),"") ,rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header5"),"") ,rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header6"),"") ,rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header7"),"") ,rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header8"),"") ,CenterBoldAlignVO));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("ZZ.Header9"),"") ,leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format("1.   "+gObjRsrcBndleForSPL90.getString("ZZ.Header10"),"") ,leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format("2.   "+gObjRsrcBndleForSPL90.getString("ZZ.Header15")," ")+data[1]+" "+gObjRsrcBndleForSPL90.getString("ZZ.Header16"), leftAlign));
                //rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.CENTER2.0"),"")+ new BigDecimal(PayableTotal1)+gObjRsrcBndleForSPL90.getString("FF.CENTER2.2")+" "+amountWord+" "+gObjRsrcBndleForSPL90.getString("FF.CENTER2.3"), leftAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format("3.   "+gObjRsrcBndleForSPL90.getString("ZZ.Header22"),"") ,leftAlign));
                dataList11.add(rowList);
                
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.BOTTOM1"),""), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.BOTTOM2"),""), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.BOTTOM3"),""), rightAlign));
                dataList11.add(rowList);
                rowList = new ArrayList();
                String url = "( DDO CODE :" + data[0] + " )";
                rowList.add(new StyledData(url, rightAlign));
                dataList11.add(rowList);
                
              }else if (report.getReportCode().equals("80000704")) {
                  ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/dcps/dcpsLabels");
      			String DdoCOde = (String)report.getParameterValue("ddoCode");
      	        String orderId = (String)report.getParameterValue("OrderId");
      	        String year = (String)report.getParameterValue("year");
      	        String month = (String)report.getParameterValue("month");
      	        double PayableTotal1 = Double.parseDouble((String)report.getParameterValue("amount"));
      	        String Type = (String)report.getParameterValue("type");
      				
      				String amountWord = EnglishDecimalFormat.convertWithSpace(new BigDecimal(PayableTotal1));
                      String sevaarthId = "NA";
                      String url = "NA";
                      String EmployeeDetails = "NA";
                      String DesignationName = "NA";
                      List lstDDoDetails = this.getDDODetails(DdoCOde);

                      for(int J = 0; J < lstDDoDetails.size(); ++J) {
                         Object[] tupleSub1 = (Object[])lstDDoDetails.get(J);
                         if (tupleSub1[0] != null) {
                            url = tupleSub1[0].toString();
                         }

                         if (tupleSub1[1] != null) {
                            EmployeeDetails = tupleSub1[1].toString();
                         }

                         if (tupleSub1[2] != null) {
                            sevaarthId = tupleSub1[2].toString();
                         }

                         if (tupleSub1[3] != null) {
                            DesignationName = tupleSub1[3].toString();
                         }
                      }

                      Date date = new Date();
                      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                      formatter.format(date);
                      
      				rowList = new ArrayList<Object>();
      				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.header1"),"") ,CenterBoldAlignVO));
      				dataList11.add(rowList);
      				rowList = new ArrayList<Object>();
      				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.header2"),"") ,CenterBoldAlignVO));
      				dataList11.add(rowList);
      				rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.RIGHT1"),"") ,rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.RIGHT2"),""), rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(url + ".",""), rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(EmployeeDetails + ".",""), rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.RIGHT3"),""), rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.PRATI1"),""), leftAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.PRATI2"),""), leftAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.PRATI3"),""), leftAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.PRATI4"),""), leftAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.VISHAY1"),""), CenterBoldAlignVO));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.VISHAY2"),""),leftAlign)+space(150)+new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.VISHAY3"),""),rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.INITAL"),""), leftAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(space(20)+gObjRsrcBndleForSPL90.getString("FFTO.CENTER1"),"")+gObjRsrcBndleForSPL90.getString("FFTO.CENTER1.1")+ new BigDecimal(PayableTotal1)+gObjRsrcBndleForSPL90.getString("FFTO.CENTER1.2")+" "+amountWord+" "+gObjRsrcBndleForSPL90.getString("FFTO.CENTER1.3"), leftAlign));
                    dataList11.add(rowList);
                   /* rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.CENTER1.1"),"")+ new BigDecimal(PayableTotal1)+gObjRsrcBndleForSPL90.getString("FFTO.CENTER1.2")+" "+amountWord+" "+gObjRsrcBndleForSPL90.getString("FFTO.CENTER1.3"), leftAlign));
                    dataList11.add(rowList);*/
                    rowList = new ArrayList();
                    rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FFTO.BOTTOM1"), rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FFTO.BOTTOM2"), rightAlign));
                    dataList11.add(rowList);
                    rowList = new ArrayList();
                    rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FFTO.BOTTOM3"), rightAlign));
                    dataList11.add(rowList);
                    
                    /*rowList = new ArrayList();
                    rowList.add(new StyledData(MessageFormat.format(space(20)+gObjRsrcBndleForSPL90.getString("FFTO.CENTER1.4"),""), leftAlign));
                    dataList11.add(rowList);*/
      				/*rowList = new ArrayList<Object>();
      				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.header3"),"") ,CenterBoldAlignVO));
      				dataList11.add(rowList);
      				rowList = new ArrayList<Object>();
      				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FFTO.header4"),"") ,CenterBoldAlignVO));
      				dataList11.add(rowList);*/
      				/*rowList = new ArrayList<Object>();
      				rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.Header4"),"") ,CenterBoldAlignVO));
      				dataList11.add(rowList);
      				rowList = new ArrayList();
                      //String RIGHT1 = gObjRsrcBndleForSPL90.getString("FF.RIGHT") + ".";
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.RIGHT"),"") ,rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(url + ".",""), rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(EmployeeDetails + ".",""), rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.RIGHT1"),""), rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.PRATI"),""), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.PRATI1"),""), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.LINE"),""), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(sevaarthId,""), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.VISHAY"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.VISHAY1"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.INITAL"),""), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.NEWLINE"),""), CenterBoldAlignVO));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(space(20)+gObjRsrcBndleForSPL90.getString("FF.CENTER"),""), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.CENTER1")," ")+sevaarthId+" "+ gObjRsrcBndleForSPL90.getString("FF.CENTER1.1"), leftAlign));
                      dataList11.add(rowList);   
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(space(20)+gObjRsrcBndleForSPL90.getString("FF.CENTER2"),""), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(MessageFormat.format(gObjRsrcBndleForSPL90.getString("FF.CENTER2.0"),"")+ new BigDecimal(PayableTotal1)+gObjRsrcBndleForSPL90.getString("FF.CENTER2.2")+" "+amountWord+" "+gObjRsrcBndleForSPL90.getString("FF.CENTER2.3"), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(space(20)+gObjRsrcBndleForSPL90.getString("FF.CENTER3"), leftAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM"), rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData("   ", rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData("   ", rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM1"), rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(DesignationName, rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM2"), rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("FF.BOTTOM3"), rightAlign));
                      dataList11.add(rowList);
                      rowList = new ArrayList();
                      url = "( DDO CODE :" + DdoCOde + " )";
                      rowList.add(new StyledData(url, rightAlign));
                      dataList11.add(rowList);*/
      				
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
		return dataList11;
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
			lSBQuery.append("WHERE GPF_ACC_NO = '"+lStrGpfAccNo+"' AND STATUS_FLAG <> 2 AND TRANSACTION_ID ='"+lStrTransactionId+"' ");

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
	//public Double getSixthPayArrear(String gpfAccNo,int finYear)
	
	public Double getSixthPayArrear(String gpfAccNo,int finYear,String lStrSevaarthId,String isSevenpc)  //swt 09/06/2020
	{
		Double sixthPC = 0d;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		List sixthPayList = null;
		gLogger.info("gpfAccNo in getSixthPayArrear"+gpfAccNo);
		gLogger.info("finYear in getSixthPayArrear"+finYear);
		gLogger.info("SevaarthId in getSixthPayArrear"+lStrSevaarthId);
		try
		{
			if (isSevenpc.equalsIgnoreCase("Y")) {
				lSBQuery.append(" SELECT SEVENPAY_ARREARS FROM MST_GPF_7PC_INTEREST_DTLS where Sevaarth_Id = '"+lStrSevaarthId+"' and GPF_ACC_NO = '"+gpfAccNo+"' and month = 6 and FIN_YEAR_ID = "+finYear+" ");
				lSBQuery.append(" AND STATUS IS NULL ");
			}
			else {
				lSBQuery.append(" SELECT SIXPAY_ARREARS FROM MST_GPF_INTEREST_DTLS where Sevaarth_Id = '"+lStrSevaarthId+"' and GPF_ACC_NO = '"+gpfAccNo+"' and month = 6 and FIN_YEAR_ID = "+finYear+" ");
				lSBQuery.append(" AND STATUS IS NULL ");
			}
			//
			gLogger.info("Query is "+ lSBQuery.toString());
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			sixthPayList = lQuery.list();
			gLogger.info("Size sixthPayList.size"+ sixthPayList.size());
			if(sixthPayList.size()>0)
			{
				if(sixthPayList.get(0) != null)
				{
				sixthPC = Double.parseDouble(sixthPayList.get(0).toString());
				}
			}
			gLogger.info("sixthPC in getSixthPayArrear"+sixthPC);
		}
		catch(Exception e)
		{
			gLogger.info("Exception in Order Report getSixthPayArrear");
		}
		return sixthPC;
	}
	//Added by Sooraj 15/05/2018
	public String getEmployeeName(String SevId)
	{
		List nameList = null;
		String empName = "";
		StringBuilder lSBQuery = new StringBuilder();
		try
		{
			lSBQuery.append("SELECT EMP_NAME FROM MST_DCPS_EMP where SEVARTH_ID ='"+SevId+"'");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			nameList =lQuery.list();
			if(nameList.size()>0 && nameList != null & nameList.get(0) != null)
			{
				empName = nameList.get(0).toString();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			gLogger.info(" Exception in getEmployeeName, GPFOrderReport");
		}
		return empName;
	}
	public void updateLoanBillID(String trnascationid) {


		gLogger.info("**********in ***********updated*********");
		StringBuilder lSb = new StringBuilder();

		lSb.append("update MST_GPF_ADVANCE set LOAN_BILL_ID  = '"+trnascationid+"' where TRANSACTION_ID='"+trnascationid+"'");

		Query lQuery = ghibSession.createSQLQuery(lSb.toString());

		lQuery.executeUpdate();

		gLogger.info("updated*********");
	}
	public String getEndServiceDate(String sevaarthId)
    {
        String endServiceDate = null;
        gLogger.info("Service End Date");
        StringBuffer sb = new StringBuffer();
        List endDate = null;
        try
        {
            sb.append((new StringBuilder("SELECT to_char(EMP_SERVEND_DT,'dd/mm/yyyy') FROM MST_DCPS_EMP where SEVARTH_ID ='"+sevaarthId+"' ")));
            Query lQuery = ghibSession.createSQLQuery(sb.toString());
            endDate = lQuery.list();
            if(endDate.size() > 0 && endDate != null && endDate.get(0) != null)
            {
                endServiceDate = endDate.get(0).toString();
                gLogger.info((new StringBuilder("Service end date is ")).append(endServiceDate).toString());
            }
        }
        catch(Exception exception) { }
        return endServiceDate;
    }
    public String supAnnDate(String sevaarthId)
    {
        String endServiceDate = null;
        gLogger.info("Service End Date");
        StringBuffer sb = new StringBuffer();
        List endDate = null;
        try
        {
            sb.append((new StringBuilder("SELECT to_char(super_ann_date,'dd/mm/yyyy') FROM MST_DCPS_EMP where SEVARTH_ID ='"+sevaarthId+"' ")));
            Query lQuery = ghibSession.createSQLQuery(sb.toString());
            endDate = lQuery.list();
            if(endDate.size() > 0 && endDate != null && endDate.get(0) != null)
            {
                endServiceDate = endDate.get(0).toString();
                gLogger.info((new StringBuilder("Service end date is ")).append(endServiceDate).toString());
            }
        }
        catch(Exception exception) { }
        return endServiceDate;
    }
    
    public String getLocationName(String locCode)
    {
        String locationNameEmp = null;
        gLogger.info("Location Name ");
        StringBuffer sb = new StringBuffer();
        List locationName1 = null;
        try
        {
            sb.append((new StringBuilder("SELECT LOC_NAME FROM CMN_LOCATION_MST where LOC_ID ='"+locCode+"' ")));
            Query lQuery = ghibSession.createSQLQuery(sb.toString());
            locationName1 = lQuery.list();
            if(locationName1.size() > 0 && locationName1 != null && locationName1.get(0) != null)
            {
            	locationNameEmp = locationName1.get(0).toString();
                gLogger.info((new StringBuilder("Service end date is ")).append(locationNameEmp).toString());
            }
        }
        catch(Exception exception) { }
        return locationNameEmp;
    }
    
    
    	public List getRegEmiAndMonthlySub(String strSevaarthId) {
		List lstRegEmiAndMonthlySub = null;

		gLogger.info("strSevaarthId"+strSevaarthId);

		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append( "SELECT nvl(pay.GPF_ADV_GRP_D,0),nvl(pay.GPF_GRP_D,0) FROM HR_PAY_PAYBILL pay inner join PAYBILL_HEAD_MPG head on pay.PAYBILL_GRP_ID = head.PAYBILL_ID " ); 
			lSBQuery.append( " inner join HR_EIS_EMP_MST eis on eis.EMP_ID = pay.EMP_ID " ); 
			lSBQuery.append( " inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID " ); 
			lSBQuery.append( " where emp.SEVARTH_ID = '"+strSevaarthId+"'  and head.APPROVE_FLAG = 1 and head.BILL_CATEGORY <> 3 " ); 
			lSBQuery.append( " and head.PAYBILL_MONTH = (SELECT max(head.PAYBILL_MONTH) FROM HR_PAY_PAYBILL pay inner join PAYBILL_HEAD_MPG head " );
			lSBQuery.append( " on pay.PAYBILL_GRP_ID = head.PAYBILL_ID inner join HR_EIS_EMP_MST eis on eis.EMP_ID = pay.EMP_ID " );
			lSBQuery.append( " inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID " );
			lSBQuery.append( " where emp.SEVARTH_ID = '"+strSevaarthId+"'  and head.APPROVE_FLAG = 1 and head.BILL_CATEGORY <> 3 " );
			lSBQuery.append( " and head.PAYBILL_YEAR = (SELECT max(head.PAYBILL_YEAR) FROM HR_PAY_PAYBILL pay " );
			lSBQuery.append( " inner join PAYBILL_HEAD_MPG head on pay.PAYBILL_GRP_ID = head.PAYBILL_ID " );
			lSBQuery.append( " inner join HR_EIS_EMP_MST eis on eis.EMP_ID = pay.EMP_ID inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID    " );
			lSBQuery.append( " where emp.SEVARTH_ID = '"+strSevaarthId+"'  and head.APPROVE_FLAG = 1 and head.BILL_CATEGORY <> 3 ) ) " );
			lSBQuery.append( "  and head.PAYBILL_YEAR = (SELECT max(head.PAYBILL_YEAR) FROM HR_PAY_PAYBILL pay " );
			lSBQuery.append( " inner join PAYBILL_HEAD_MPG head on pay.PAYBILL_GRP_ID = head.PAYBILL_ID " );
			lSBQuery.append( " inner join HR_EIS_EMP_MST eis on eis.EMP_ID = pay.EMP_ID  " );
			lSBQuery.append( " inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID   " );
			lSBQuery.append( " where emp.SEVARTH_ID = '"+strSevaarthId+"'  and head.APPROVE_FLAG = 1 and head.BILL_CATEGORY <> 3) " );
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lstRegEmiAndMonthlySub = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception in getLocationCode : ", e);
		}
		return lstRegEmiAndMonthlySub;

	}
    	
    	public Double getOpeningBalForCurrYear(String lStrGpfAccNo, Long lLngYearId) throws Exception {

    		Category logger = null;
			logger.info("lStrGpfAccNo"+lStrGpfAccNo);
    		logger.info("lLngYearId"+lLngYearId);

    		Double lDblOpeningBal = null;
    		List lLstOpeningBal = new ArrayList();
    		StringBuilder lSBQuery = new StringBuilder();
    		Query lQuery = null;
    		try {
    			lSBQuery
    			.append("SELECT closing_Balance FROM Mst_Gpf_Yearly WHERE gpf_Acc_No = :gpfAccNo AND fin_Year_Id = :finYearId order by updated_date DESC fetch first rows only");

    			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
    			lQuery.setParameter("finYearId", lLngYearId);

    			lLstOpeningBal = lQuery.list();
    			if (lLstOpeningBal != null && lLstOpeningBal.size() > 0) {
    				lDblOpeningBal = Double.parseDouble((lLstOpeningBal.get(0).toString()));
    				logger.info("lDblOpeningBal"+lDblOpeningBal);
    			} else {
    				lDblOpeningBal = 0d;
    			}
    		} catch (Exception e) {
    			logger.error("Exception in getOpeningBalForCurrYear of GPFRequestProcessDAOImpl  : ", e);			
    		}
    		return lDblOpeningBal;
    	}
    	
    	
        //public String getInterestAmount(String gpfAccNo)
        public String getInterestAmount(String gpfAccNo,String lStrSevaarthId)  //swt 09/06/2020
        {
            String intreAmount = null;
            gLogger.info("******** lStrGpfAccNo"+gpfAccNo);
            StringBuffer sb = new StringBuffer(); 
            List intAmt = null;
            try
            {
                sb.append(("SELECT DISTINCT INTERESTAMOUNT FROM TRN_GPF_FINAL_WITHDRAWAL where GPF_ACC_NO ='"+gpfAccNo+"' and Sevaarth_Id ='"+lStrSevaarthId+"' "));
                Query lQuery = ghibSession.createSQLQuery(sb.toString());
                intAmt = lQuery.list();
                if(intAmt.size() > 0 && intAmt != null && intAmt.get(0) != null)
                {
                	intreAmount = intAmt.get(0).toString();
                	  gLogger.info("Intrest Amount&&&&&&"+intreAmount);
                    gLogger.info((new StringBuilder("Intrest Amount ")).append(intreAmount).toString());
                }
            }
            catch(Exception exception) { }
            return intreAmount;
        }
        
        
    	public String getEmpTresaryName(String locPin)


        {
            String locDistict = null;
            gLogger.info("******** lStrGpfAccNo"+locPin);
            StringBuffer sb = new StringBuffer(); 
            List locDis = null;
            try
            {
                sb.append(("SELECT LOC_DISTRICT FROM MST_TREASURY_DTLS where LOC_PIN='"+locPin+"' "));
                Query lQuery = ghibSession.createSQLQuery(sb.toString());
                locDis = lQuery.list();
                if(locDis.size() > 0 && locDis != null && locDis.get(0) != null)
                {
                	locDistict = locDis.get(0).toString();
                	  gLogger.info("Loc District&&&&&&"+locDistict);
                    gLogger.info((new StringBuilder("Treasury Dist ")).append(locDistict).toString());
                }
            }
            catch(Exception exception) { }
            return locDistict;
        }

        
      	
        //public Double getInterestAmount1(String gpfAccNo)
        public Double getInterestAmount1(String gpfAccNo,String lstrsevaarthid) //swt 09/06/2020
        {
            Double intreAmount1 = null;
            gLogger.info("******** lStrGpfAccNo1111"+gpfAccNo);
            gLogger.info("******** lstrsevaarthid"+lstrsevaarthid);
        	List lLntreAmount1l = new ArrayList();
    		StringBuilder lSBQuery = new StringBuilder();
    		Query lQuery = null;
            try
            {
            	
            	lSBQuery
    			.append(("SELECT DISTINCT INTERESTAMOUNT FROM TRN_GPF_FINAL_WITHDRAWAL where GPF_ACC_NO ='"+gpfAccNo+"' and sevaarth_id ='"+lstrsevaarthid+"' "));



    			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    			lQuery.setParameter("gpfAccNo", gpfAccNo);
    			
    			lLntreAmount1l = lQuery.list();
    			if (lLntreAmount1l != null && lLntreAmount1l.size() > 0) {
    				intreAmount1 = Double.parseDouble((lLntreAmount1l.get(0).toString()));
    			
    				 gLogger.info("intreAmount1*************"+intreAmount1);
    			} else {
    				intreAmount1 = 0d;
    			}
    			


            }
            catch(Exception exception) { }
            return intreAmount1;
        }
        
        //added by brijoy 16012019
        public List getRetireMentDateandDOJ(String strSevId) throws Exception {
    		gLogger.info("lstrddocode"+strSevId);
    		List lLstRetireMentDate = new ArrayList();
    		StringBuilder lSBQuery = new StringBuilder();
    		Query lQuery = null;
    		try {
    			//lSBQuery.append(" SELECT to_char(a.SUPER_ANN_DATE,'dd/mm/yyyy'),to_char(a.doj,'dd/mm/yyyy'),to_char(sysdate,'dd/mm/yyyy') FROM MST_DCPS_EMP a where a.SEVARTH_ID = '"+strSevId+"'");
    			lSBQuery.append(" SELECT to_char(b.EMP_SRVC_EXP,'dd/mm/yyyy'),to_char(a.doj,'dd/mm/yyyy'),to_char(sysdate,'dd/mm/yyyy') ");
    			lSBQuery.append(" FROM MST_DCPS_EMP a inner join org_emp_mst b  on a.ORG_EMP_MST_ID = b.EMP_ID where a.SEVARTH_ID = '"+strSevId+"' ");
    			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    			lLstRetireMentDate = lQuery.list();

    		} catch (Exception e) {
    			gLogger.error("Exception in getOpeningBalForCurrYear of GPFRequestProcessDAOImpl  : ", e);			
    		}
    		return lLstRetireMentDate;
    	}
        
        
        public int getDisNumber(String purpose){
        	gLogger.error("getDisNumber****************:::::::::::"+purpose);

    		List lLstJoining = new ArrayList();
    		int lStrREmain = 0;
    		String temp = "";

    		StringBuilder lSBQuery = new StringBuilder();

    		lSBQuery.append(" SELECT CURRENT_DIS_NUMBER FROM MST_GPF_ADVANCE where CURRENT_DIS_NUMBER > 1 and STATUS_FLAG = 'A' and ADVANCE_TYPE ='NRA'  and TRANSACTION_ID = '"+purpose+"' ");
    		


    		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());


    		lLstJoining = lQuery.list();

    		if(lLstJoining != null && lLstJoining != null && lLstJoining.size() > 0 && lLstJoining.get(0)!=null){				
    			temp =lLstJoining.get(0).toString();
    			lStrREmain = Integer.parseInt(temp);

    		}
    		gLogger.error("********Query to check number of disbursement Remaining for the concerned subtype****"+lSBQuery.toString() +"******size*****"+lStrREmain);			

    		return lStrREmain;
    	}
        
        
        public int getDisNumberOnlyOne(String purpose){
        	gLogger.error("getDisNumberOnlyOne****************:::::::::::"+purpose);

    		List lLstJoining = new ArrayList();
    		int lStrREmain = 0;
    		String temp = "";

    		StringBuilder lSBQuery = new StringBuilder();

    		lSBQuery.append(" SELECT CURRENT_DIS_NUMBER FROM MST_GPF_ADVANCE where CURRENT_DIS_NUMBER = 1 and STATUS_FLAG = 'A' and ADVANCE_TYPE ='NRA'  and TRANSACTION_ID = '"+purpose+"' ");
    		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

    		lLstJoining = lQuery.list();

    		if(lLstJoining != null && lLstJoining != null && lLstJoining.size() > 0 && lLstJoining.get(0)!=null){				
    			temp =lLstJoining.get(0).toString();
    			lStrREmain = Integer.parseInt(temp);
    		}
    		gLogger.error("********getDisNumberOnlyOne****"+lSBQuery.toString() +"******size*****"+lStrREmain);			

    		return lStrREmain;
    	}
        /* added by brijoy */
        //public Double getOpeningBalForCurrYearAsPerOrder(String lStrGpfAccNo, int lLngYear, String lStrTransactionId) throws Exception {
        //swt 09/06/2020 (SevaarthId added)
        public Double getOpeningBalForCurrYearAsPerOrder(String lStrGpfAccNo, int lLngYear, String lStrTransactionId,String lStrSevaarthId) throws Exception {
        	gLogger.info("lStrGpfAccNo"+lStrGpfAccNo);
        	gLogger.info("OrderYears"+lLngYear);
        	gLogger.info("lStrTransactionId"+lStrTransactionId);

    		Double lDblOpeningBal = null;
    		int yearId = 0; 
    		List lLstOpeningBal = new ArrayList();
    		List lLstOpeningBal1 = new ArrayList();
    		StringBuilder lSBQuery = new StringBuilder();
    		StringBuilder lSBQuery1 = new StringBuilder();
    		Query lQuery = null;
    		Query lQuery1 = null;
    		try {
    			lSBQuery.append("SELECT FIN_YEAR_ID FROM MST_GPF_ADVANCE where TRANSACTION_ID = '"+lStrTransactionId+"'");
    			
    			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    			gLogger.info("******"+lSBQuery.toString());
    			/*lQuery.setParameter("lLngYear", lLngYear);*/
    			
    			lLstOpeningBal = lQuery.list();
    			if (lLstOpeningBal != null && lLstOpeningBal.size() > 0) {
    				yearId = Integer.parseInt((lLstOpeningBal.get(0).toString()));
    				gLogger.info("yearId"+yearId);
    				
    				lSBQuery1
        			.append("SELECT OPENING_BALANCE,max(MST_GPF_INTEREST_ID) FROM MST_GPF_INTEREST_DTLS where GPF_ACC_NO = '"+lStrGpfAccNo+"' and Sevaarth_Id = '"+lStrSevaarthId+"' and fin_year_ID = "+yearId+" AND OPENING_BALANCE is not null ");
        			lSBQuery1.append(" AND STATUS IS NULL ");
        			lSBQuery1.append(" group by OPENING_BALANCE ");
        			lQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());
        			gLogger.info("******"+lSBQuery1.toString());
        			
        			
        			lLstOpeningBal1 = lQuery1.list();
        			
        			Iterator it1 = lLstOpeningBal1.iterator();
    				while (it1.hasNext()) {
    					Object[] tuple = (Object[]) it1.next();
    					if(!tuple[0].toString().equalsIgnoreCase(null)){
    						lDblOpeningBal = Double.parseDouble(tuple[0].toString());
    						gLogger.error("lDblOpeningBal***************** "+lDblOpeningBal);
    						
    					}
    					else 
    					{
    						lDblOpeningBal = 0d;
    						gLogger.error("lDblOpeningBal*****************else "+lDblOpeningBal);
    					}
    				}
    			} else {
    				lDblOpeningBal = 0d;
    			}
    			
    		} catch (Exception e) {
    			gLogger.error("Exception in getOpeningBalForCurrYear of GPFRequestProcessDAOImpl  : ", e);			
    		}
    		return lDblOpeningBal;
    	}
        
        //public Double getWithdrawalSancAmt(String lStrGpfAccno,Long lLngFinyrId,String lStrTransactionId){
        //swt 09/06/2020
        public Double getWithdrawalSancAmt(String lStrGpfAccno,Long lLngFinyrId,String lStrTransactionId,String lStrSevaarthId){

    		List with = null;
    		Double lDblWithAmt = 0d;

    		StringBuilder lSBQuery = new StringBuilder();

    		try {
    			lSBQuery.append(" SELECT cast(sum(DIS_AMOUNT) as double) FROM IFMS.MST_GPF_DISBURSE_DETAILS dis inner join  ");
    			lSBQuery.append(" (SELECT COUNT(1) as cnt,MIN(MST_GPF_ADVANCE_ID) as gadv_id FROM IFMS.MST_GPF_ADVANCE  ADV ");
    			lSBQuery.append(" INNER JOIN MST_GPF_BILL_DTLS BILL ON  ADV.ORDER_NO= BILL.ORDER_NO AND BILL.STATUS_FLAG= 1 ");
    			lSBQuery.append(" WHERE ADV.GPF_ACC_NO='"+lStrGpfAccno+"'  AND ADV.Sevaarth_Id='"+lStrSevaarthId+"' AND ADV.STATUS_FLAG= 'A' AND ADV.ADVANCE_TYPE='NRA' and ADV.TRANSACTION_ID ='"+lStrTransactionId+"' GROUP BY ADV.PURPOSE_CATEGORY  ) tmp on dis.MST_GPF_ADV_ID =tmp.gadv_id and dis.DIS_NUMBER between 1 and tmp.cnt  ");

    			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

    			gLogger.info("Query is::::::::"+lSBQuery.toString());


    			with = lQuery.list();
    			if (with.size() != 0) {
    				lDblWithAmt = (Double)with.get(0);
    			}
    		} catch (Exception e) {
    			gLogger.error("Exception in getWithdrawalSanc : ", e);
    		}
    		return lDblWithAmt;

    	}
        
        //swt 11/10/2020
        public Double getSevenPcBasic(String strSevaarthId) {
        	List with = null;
    		Double lStrSevenBasic = 0d;
    		gLogger.info("getGenderOfEmployee::::::::"+strSevaarthId);
    		try {
    			StringBuilder lSBQuery = new StringBuilder();

    			lSBQuery.append(" SELECT cast(nvl(SEVEN_PC_BASIC,0) as double) FROM mst_dcps_emp where SEVARTH_ID ='"+strSevaarthId+"' ");

    			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    			List size1=lQuery.list();

               gLogger.info("lSBQuery:::::"+lSBQuery.toString()+"SEVEN_PC_BASIC"+lStrSevenBasic);
    		
               with = lQuery.list();
    			if (with.size() != 0) {
    				lStrSevenBasic = (Double)with.get(0);
    			}

    		} catch (Exception e) {
    			gLogger.error("Exception in getSevenPcBasic  : ", e);			
    		}
    		return lStrSevenBasic;
    	}     
        public Double getOpeningBalForCurrYear(String lStrGpfAccNo, Long lLngYearId,String lStrSevaarthId,String isSevenpc) throws Exception {

        	gLogger.info("lStrGpfAccNo"+lStrGpfAccNo);
        	gLogger.info("lLngYearId"+lLngYearId);
        	gLogger.info("lstrsevaarthId"+lStrSevaarthId);

    		Double lDblOpeningBal = null;
    		List lLstOpeningBal = new ArrayList();
    		StringBuilder lSBQuery = new StringBuilder();
    		Query lQuery = null;
    		try {
    			lSBQuery
    			.append("SELECT closing_Balance FROM Mst_Gpf_Yearly WHERE gpf_Acc_No = :gpfAccNo AND sevaarth_Id=:sevaarthId AND fin_Year_Id = :finYearId AND is_SevenPC=:isSevenpc ");/* commented by Brijoy 19012019*/
    			/*lSBQuery
    			.append("SELECT closing_Balance FROM Mst_Gpf_Yearly WHERE gpf_Acc_No = '"+lStrGpfAccNo+"' ");*/

    			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
    			lQuery.setParameter("sevaarthId", lStrSevaarthId);
    			lQuery.setParameter("finYearId", lLngYearId);
    			lQuery.setParameter("isSevenpc", isSevenpc);

    			lLstOpeningBal = lQuery.list();
    			if (lLstOpeningBal != null && lLstOpeningBal.size() > 0) {
    				lDblOpeningBal = Double.parseDouble((lLstOpeningBal.get(0).toString()));
    				gLogger.info("lDblOpeningBal"+lDblOpeningBal);
    			} else {
    				lDblOpeningBal = 0d;
    			}
    		} catch (Exception e) {
    			gLogger.error("Exception in getOpeningBalForCurrYear of GPFRequestProcessDAOImpl  : ", e);			
    		}
    		return lDblOpeningBal;
    	} 
        public List getDDODetails(final String DDOCode) {
            List lLstEmpFiveInst = null;
            StringBuilder sb = null;
            try {
                final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
                sb = new StringBuilder();
                sb.append("select off.OFF_name,case when off.address1 is null then off.ADDRESS1_NEW||','|| off.ADDRESS2_NEW||','|| off.ADDRESS3_NEW else address1 end ,loc.loc_name ,design.DSGN_NAME ");
                sb.append("from MST_DCPS_DDO_OFFICE off ");
                sb.append("join cmn_location_mst loc on substr(loc.location_code,0,2)=substr(off.ddo_code,0,2) and loc.department_id=100003 ");
                sb.append("join ORG_DDO_MST ddo on ddo.ddo_code=off.ddo_code ");
                sb.append("join ORG_POST_DETAILS_RLT post on post.POST_ID=ddo.post_id ");
                sb.append("join ORG_DESIGNATION_MST design on post.DSGN_ID=design.DSGN_ID ");
                sb.append("where off.DDO_CODE = '" + DDOCode + "' and off.ddo_office='Yes' ");
                final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                lLstEmpFiveInst = stQuery.list();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return lLstEmpFiveInst;
        }
        private String getDDODetails1(String gStrLocCode2) {
        	 List lLstEmpFiveInst = null;
             StringBuilder sb = null;
             String data="";
             try {
                 final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
                 sb = new StringBuilder();
                 sb.append(" SELECT ddo_Code FROM ORG_DDO_MST where LOCATION_CODE='"+gStrLocCode2+"' ");
                 
                 Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                 lLstEmpFiveInst = stQuery.list();
                 data=lLstEmpFiveInst.get(0).toString();
                 
                 sb = new StringBuilder();
                 sb.append(" SELECT loc_name FROM CMN_LOCATION_MST where LOC_id='"+lLstEmpFiveInst.get(0).toString().substring(0, 4)+"' ");
                 stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                 lLstEmpFiveInst = stQuery.list();
                 data=data+"-"+lLstEmpFiveInst.get(0).toString();
             }
             catch (Exception e) {
                 e.printStackTrace();
             }
             return data;
        }
}
