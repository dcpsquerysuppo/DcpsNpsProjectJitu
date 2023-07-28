package com.tcs.sgv.lna.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.service.ServiceLocator;

public class LoanBillReport extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(LoanBillReport.class);
	public static String newline = System.getProperty("line.separator");

	Map lMapRequestAttributes = null;
	Map lMapSessionAttributes = null;
	LoginDetails lObjLoginVO = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Session ghibSession = null;
	String gStrLocCode = null;
	CmnLocationMst CmnLocationMstDst = null;
	Date gDtCurrDate = null;
	Long gLngUserId = null;

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {

		lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
		lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
		serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
		CmnLocationMstDst = (CmnLocationMst) lMapRequestAttributes.get("CmnLocationMstDst");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();
		gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
		ghibSession = lObjSessionFactory.getCurrentSession();
		gDtCurrDate = DBUtility.getCurrentDateFromDB();
		gLngUserId = lObjLoginVO.getUser().getUserId();
			
		ArrayList<Object> dataList = new ArrayList<Object>();
		ArrayList<Object> rowList = null;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		ArrayList<Object> subReport = new ArrayList<Object>();
		try {
		
			StyleVO[] rightAlign = new StyleVO[2];
			rightAlign[0] = new StyleVO();
			rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			rightAlign[1] = new StyleVO();
			rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightAlign[1].setStyleValue("12");	
			
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
			
			StyleVO[] centerAlign = new StyleVO[2];
			centerAlign[0] = new StyleVO();
			centerAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerAlign[1] = new StyleVO();
			centerAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerAlign[1].setStyleValue("14");
			
			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			
			String lStrSubHeading = "<center>(See Rule 532)</center>";			
			
			String lStrBillAmt = lObjReport.getParameterValue("billAmount").toString();
			
			lObjReport.setAdditionalHeader(lStrSubHeading);
			lObjReport.setStyleList(noBorder);
			
			String lStrOfficeName = "";
			String lStrDsngName = "";
			Object lObj[] = null;
			
			Long lLngBillAmt = Long.parseLong(lStrBillAmt);
			
			String lStrAmtInWords = EnglishDecimalFormat.convert(lLngBillAmt);
						
			rowList = new ArrayList<Object>();			
			rowList.add(new StyledData("BILL NO."+space(50), rightAlign));
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(space(15)+"DATE:- ");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Bill for Loans and Advances other than Revenue Advance "+space(50)+"Plan/ Non-Plan");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("No.");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Head of Account: ");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);
			dataList.add(rowList);			

			rowList = new ArrayList<Object>();			
			rowList.add(newline);
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Received the sum of Rs. "+lLngBillAmt+"(In Words Rs. "+lStrAmtInWords+" )");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Being for the loan /advance  "+space(95)+"sanctioned by");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("In his letter/G.R. No.");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Dated:-");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("(Copy enclosed)");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Dated:-");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(new StyledData("Signature  Designation",rightAlign));			
			dataList.add(rowList);			
			
			rowList = new ArrayList<Object>();			
			rowList.add("Allotment of 2011-2012");
			rowList.add("Rs.");
			subReport.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Expenditure including");
			rowList.add("Rs.");			
			subReport.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Balance available");
			rowList.add("Rs.");			
			subReport.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Countersigned for");
			rowList.add("Rs.");			
			subReport.add(rowList);
			
			
			ReportVO RptVo = null;
			ReportsDAO reportsDao = new ReportsDAOImpl();
			TabularData td = new TabularData(subReport);
			RptVo = reportsDao.getReport("8000065", lObjReport.getLangId(), lObjReport.getLocId());
			(td).setRelatedReport(RptVo);

			rowList = new ArrayList();
			rowList.add(td);
			dataList.add(rowList);

			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(new StyledData("Signature",rightAlign));			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("(In Words Rs. "+lStrAmtInWords+" )");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Dated:-");
			dataList.add(rowList);
			
			List<Object[]> lLstDdoDtls = getDdoDsngOfficeName(gLngUserId);
			
			if(!lLstDdoDtls.isEmpty()){
				lObj = (Object[]) lLstDdoDtls.get(0);
				lStrOfficeName = (String) lObj[0];
				lStrDsngName = (String) lObj[1];
				
			}
			
			rowList = new ArrayList<Object>();			
			rowList.add("Cheque may please be issued in favour of. "+lStrDsngName+"  "+lStrOfficeName);
			dataList.add(rowList);

			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(new StyledData("Signature  Designation",rightAlign));			
			dataList.add(rowList);
						
			rowList = new ArrayList<Object>();			
			rowList.add(new StyledData("(For use in Treasury )",centerUnderlineBold));			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Examined Pay Rs. "+lLngBillAmt+space(10)+" (In word Rs. "+lStrAmtInWords+" )");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Accountant");
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add(newline);			
			dataList.add(rowList);
						
			rowList = new ArrayList<Object>();			
			rowList.add(new StyledData("Treasury officer, "+space(15),rightAlign));			
			dataList.add(rowList);
			
			rowList = new ArrayList<Object>();			
			rowList.add("Dated:-");
			dataList.add(rowList);
			
		}catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		}

		return dataList;
	}
	public List<Object[]> getDdoDsngOfficeName(Long lLngUserId) {

		List<Object[]> lLstOfficeDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT mdo.OFF_NAME,dsm.DSGN_NAME FROM MST_DCPS_DDO_OFFICE mdo,ORG_DESIGNATION_MST dsm,ORG_POST_MST opm, ");
			lSBQuery.append("mst_dcps_emp dcps,org_emp_mst emp,ORG_USERPOST_RLT post ");
			lSBQuery.append("where emp.USER_ID = :UserId and dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
			lSBQuery.append("and dcps.DDO_CODE = mdo.DDO_CODE and mdo.DDO_OFFICE = 'Yes' ");
			lSBQuery.append("and post.USER_ID = emp.USER_ID and opm.POST_ID = post.POST_ID and opm.DSGN_CODE = dsm.DSGN_ID ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("UserId", lLngUserId);	
			lLstOfficeDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstOfficeDtls;
	}
	
	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
}
