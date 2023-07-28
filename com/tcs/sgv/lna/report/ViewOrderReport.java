package com.tcs.sgv.lna.report;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
import com.tcs.sgv.lna.valueobject.MstLnaHodDtls;

public class ViewOrderReport extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(ViewOrderReport.class);
	public static String newline = System.getProperty("line.separator");

	private ResourceBundle gObjRsrcBndle;
	
	private ResourceBundle gObjRsrcBndleCons = ResourceBundle.getBundle("resources/lna/LNAConstants");

	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map lMapRequestAttributes = null;
	Map lMapSessionAttributes = null;
	LoginDetails lObjLoginVO = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Session ghibSession = null;
	String gStrLocCode = null;
	CmnLocationMst CmnLocationMstDst = null;
	Date gDtCurrDate = null;

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
					
		Map inputMap = new HashMap();
		
		ArrayList<Object> dataList = new ArrayList<Object>();
		ArrayList<Object> rowList = null;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		
		try {
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

			lObjReport.setStyleList(rowsFontsVO);
			lObjReport.initializeDynamicTreeModel();
			lObjReport.initializeTreeModel();
			lObjReport.setStyleList(rowsFontsVO);

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

			if (lObjReport.getReportCode().equals("8000067")) {

				String lStrFinYearDesc = getFinYearDescFromCurrDate();
				String lStrLoanType = lObjReport.getParameterValue("loanType").toString();
				String lStrBillId = lObjReport.getParameterValue("loanBillId").toString();
				String lStrUserType = lObjReport.getParameterValue("userType").toString();
				String lStrOrderNo = "";
				Long lLngBillId = null;
				Long lLngLoanType = null;
				if(!"".equals(lStrBillId))
					lLngBillId = Long.parseLong(lStrBillId);
				if(!"".equals(lStrLoanType))
					lLngLoanType = Long.parseLong(lStrLoanType);
				
				List<String> lLstEmpSevaarthId = getEmpSevaarthId(lLngBillId, lLngLoanType);
				List<String> lLstTransId = getEmpTrnsId(lLngBillId, lLngLoanType);
				List<String> lLstOrderNo = getEmpOrderNo(lLngBillId, lLngLoanType);
				if(!lLstOrderNo.isEmpty()){
					lStrOrderNo = lLstOrderNo.get(0);
				}
				List lLstEmpDtls = null;
				Integer lIntRowNum = 1;
				Object[] tuple = null;
				ArrayList<Object> subReport = new ArrayList<Object>();
				List lLstLocName = null;	
				List lLstOfficeDtls = getOfficeDtls(gStrLocCode);
				
				
				if(lStrLoanType.equals("800028")){
					lObjReport.setAdditionalHeader("<center><b>Computer Advance Order Report</b></center>");
					gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/ComputerOrderReport");
				
					lLstEmpDtls = getEmpCompLoanDtls(lLstEmpSevaarthId, lLstTransId);
					
					if (lLstEmpDtls != null && !lLstEmpDtls.isEmpty()) {
						
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE75"));					
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE76"));					
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();						
						rowList.add(newline);
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE77"));					
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();						
						rowList.add(newline);
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE78"));					
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE1"));
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE2"));
						rowList.add(newline);
						dataList.add(rowList);
	
						/*rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE3"));
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE4"));
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE5"));
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE6"));
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE7"));
						rowList.add(newline);
						dataList.add(rowList);*/
						
						lLstLocName = getLocShortName(Long.parseLong(gStrLocCode));
						String lStrLocName = "";
						String lStrLocShortName = "";
						
						if(!lLstLocName.isEmpty()){
							Object[] lObj = (Object[]) lLstLocName.get(0);
							lStrLocName = (String) lObj[0];
							lStrLocShortName = (String) lObj[1];
						}
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrLocShortName +"/CA/" + lStrFinYearDesc+"/" + gObjRsrcBndle.getString("LNA.ORDERLINE8.1")+lStrOrderNo, CenterAlignVO));
						dataList.add(rowList);
	
						Object[] lObjOffDtls = null;
						//String lStrOffName = "";
						String lStrOffAdd = "";
						String lStrOffTown = "";
						BigInteger lBIOffPin = null;
						if(!lLstOfficeDtls.isEmpty()){
							lObjOffDtls = (Object[]) lLstOfficeDtls.get(0);
							//lStrOffName = (String) lObjOffDtls[0];
							lStrOffAdd = (String) lObjOffDtls[1];
							lStrOffTown = (String) lObjOffDtls[2];
							lBIOffPin = (BigInteger) lObjOffDtls[3];
						}
						
						Iterator<Object> it = lLstEmpDtls.iterator();				
						Long lLngTotalSanctionAmt = 0L;
						while (it.hasNext()) {
							tuple = (Object[]) it.next();
							rowList = new ArrayList<Object>();
							rowList.add(lIntRowNum);
							rowList.add(tuple[0].toString() + " , " + tuple[1].toString() + " , " + tuple[2].toString());
							rowList.add(tuple[3].toString() + " / " + lObjDateFormat.format(tuple[4]));
							rowList.add(new StyledData(tuple[5].toString(), rightAlign));
							lLngTotalSanctionAmt = lLngTotalSanctionAmt + (Long) tuple[5];
							rowList.add(tuple[6].toString() + " / " + tuple[7].toString());
							subReport.add(rowList);
							lIntRowNum++;
						}
	
						
						rowList = new ArrayList<Object>();
						//rowList.add(new StyledData(lStrOffName, rightAlign));
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrOffAdd, rightAlign));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrOffTown +"-"+((lBIOffPin == null)?"":lBIOffPin), rightAlign));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE13") + lObjDateFormat.format(gDtCurrDate), rightAlign));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE14"), centerUnderlineBold));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE15") + lStrFinYearDesc, CenterAlignVO));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE16"), CenterAlignVO));
						dataList.add(rowList);
	
						String lStrAmtInWords = EnglishDecimalFormat.convert(lLngTotalSanctionAmt);					
						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE17") + lStrLocName+" " + gObjRsrcBndle.getString("LNA.ORDERLINE19")
								+lLngTotalSanctionAmt+" " +lStrAmtInWords+" "+ gObjRsrcBndle.getString("LNA.ORDERLINE19.1")
								+ gObjRsrcBndle.getString("LNA.ORDERLINE20") + gObjRsrcBndle.getString("LNA.ORDERLINE21") + gObjRsrcBndle.getString("LNA.ORDERLINE22")
								+ gObjRsrcBndle.getString("LNA.ORDERLINE23") + lStrLocName+" " + gObjRsrcBndle.getString("LNA.ORDERLINE24") + gObjRsrcBndle.getString("LNA.ORDERLINE25"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE26"), centerUnderlineBold));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE15") + lStrFinYearDesc, CenterAlignVO));
						dataList.add(rowList);
	
						
						ReportVO RptVo = null;
						ReportsDAO reportsDao = new ReportsDAOImpl();
						TabularData td = new TabularData(subReport);
						RptVo = reportsDao.getReport("8000062", lObjReport.getLangId(), lObjReport.getLocId());
						(td).setRelatedReport(RptVo);
	
						rowList = new ArrayList();
						rowList.add(td);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE27")+"April-"+lStrFinYearDesc.substring(0, 4)
								+ gObjRsrcBndle.getString("LNA.ORDERLINE27.1")+"May-"+lStrFinYearDesc.substring(5, 9)+" "
								+ gObjRsrcBndle.getString("LNA.ORDERLINE27.2")+gObjRsrcBndle.getString("LNA.ORDERLINE28"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE29") + lStrFinYearDesc + " " + gObjRsrcBndle.getString("LNA.ORDERLINE29.1"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE30") + lStrFinYearDesc + " " + gObjRsrcBndle.getString("LNA.ORDERLINE30.1"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE31"), CenterAlignVO));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE32"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE33"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE34"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE35"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE36"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE37"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE38"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE39"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE40"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE41"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE42"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE43"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE44"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE45"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE46"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE47"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE48"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE49"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE50"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);
	
						List<MstLnaHodDtls> lLstHodDtls = getHodDtls(gStrLocCode,lStrUserType);
						MstLnaHodDtls lObjHodDtls = new MstLnaHodDtls();
						String lStrHodName = "";
						String lStrHodDsgn = "";
						if(lLstHodDtls != null && !lLstHodDtls.isEmpty()){
							lObjHodDtls = lLstHodDtls.get(0);
							lStrHodName = lObjHodDtls.getHodName();
							lStrHodDsgn = lObjHodDtls.getHodDsgn();
							
						}
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrHodName, rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrHodDsgn, rightAlign));
						dataList.add(rowList);
	
						/*rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE53"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE54"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE55"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE56"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE57"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE58"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE59"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE60"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE61"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE62"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE63"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE64"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE65"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE66"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE67"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE68"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE69"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE70"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE71"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE72"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE73"));
						dataList.add(rowList);
	
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE74"));
						dataList.add(rowList);*/
					
					
				}
				}else if(lStrLoanType.equals("800030")){
					gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/VehicleOrderReport");
					lObjReport.setAdditionalHeader("<center><b>Vehicle Advance Order Report</b></center>");
					lLstEmpDtls = getEmpVehicleLoanDtls(lLstEmpSevaarthId, lLstTransId);
					if (lLstEmpDtls != null && !lLstEmpDtls.isEmpty()) {

						
						Iterator<Object> it = lLstEmpDtls.iterator();
						Long lLngTotalSanctionAmt = 0L;
						while (it.hasNext()) {
							tuple = (Object[]) it.next();
							rowList = new ArrayList<Object>();
							rowList.add(lIntRowNum);
							rowList.add(tuple[0].toString() + " , " + tuple[1].toString() + " , " + tuple[2].toString());
							rowList.add(tuple[3].toString() + " / " + lObjDateFormat.format(tuple[4]));
							rowList.add(new StyledData(tuple[5].toString(), rightAlign));
							lLngTotalSanctionAmt = lLngTotalSanctionAmt + (Long) tuple[5];
							rowList.add(tuple[6].toString() + " / " + tuple[7].toString());
							subReport.add(rowList);
							lIntRowNum++;
						}

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE71"));					
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE72"));					
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();						
						rowList.add(newline);
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE73"));					
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();						
						rowList.add(newline);
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE74"));					
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE3"));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE4"));
						rowList.add(newline);
						dataList.add(rowList);

						/*rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE5"));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE6"));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE7"));
						rowList.add(newline);
						dataList.add(rowList);*/

						lLstLocName = getLocShortName(Long.parseLong(gStrLocCode));
						String lStrLocName = "";
						String lStrLocShortName = "";
						
						if(!lLstLocName.isEmpty()){
							Object[] lObj = (Object[]) lLstLocName.get(0);
							lStrLocName = (String) lObj[0];
							lStrLocShortName = (String) lObj[1];
						}
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE8") +"  "+lStrLocShortName+gObjRsrcBndle.getString("LNA.ORDERLINE8.2") + lStrFinYearDesc + gObjRsrcBndle.getString("LNA.ORDERLINE8.1")+lStrOrderNo, CenterAlignVO));
						dataList.add(rowList);
						
						Object[] lObjOffDtls = null;
						//String lStrOffName = "";
						String lStrOffAdd = "";
						String lStrOffTown = "";
						BigInteger lBIOffPin = null;
						if(!lLstOfficeDtls.isEmpty()){
							lObjOffDtls = (Object[]) lLstOfficeDtls.get(0);
							//lStrOffName = (String) lObjOffDtls[0];
							lStrOffAdd = (String) lObjOffDtls[1];
							lStrOffTown = (String) lObjOffDtls[2];
							lBIOffPin = (BigInteger) lObjOffDtls[3];
						}

						rowList = new ArrayList<Object>();
						//rowList.add(new StyledData(lStrOffName, rightAlign));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrOffAdd, rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrOffTown +"-"+((lBIOffPin == null)?"":lBIOffPin), rightAlign));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE13") + lObjDateFormat.format(gDtCurrDate), rightAlign));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE14"), centerUnderlineBold));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE15") + lStrFinYearDesc, CenterAlignVO));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE16"), CenterAlignVO));
						dataList.add(rowList);

						String lStrAmtInWords = EnglishDecimalFormat.convert(lLngTotalSanctionAmt);		
						
						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE17")+ lStrLocName+" " + gObjRsrcBndle.getString("LNA.ORDERLINE18")+ 
								lLngTotalSanctionAmt+" "+ lStrAmtInWords+" " + gObjRsrcBndle.getString("LNA.ORDERLINE19"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE20")+lStrLocName+" " + gObjRsrcBndle.getString("LNA.ORDERLINE21")+lStrLocName+" "
								+ gObjRsrcBndle.getString("LNA.ORDERLINE22"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE23") + lStrFinYearDesc, CenterAlignVO));
						dataList.add(rowList);
						
						ReportVO RptVo = null;
						ReportsDAO reportsDao = new ReportsDAOImpl();
						TabularData td = new TabularData(subReport);
						RptVo = reportsDao.getReport("8000062", lObjReport.getLangId(), lObjReport.getLocId());
						(td).setRelatedReport(RptVo);

						rowList = new ArrayList();
						rowList.add(td);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE24"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE27"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(space(10) +gObjRsrcBndle.getString("LNA.ORDERLINE28"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE29")+lStrFinYearDesc+gObjRsrcBndle.getString("LNA.ORDERLINE29.1"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add( gObjRsrcBndle.getString("LNA.ORDERLINE30")+lStrFinYearDesc+gObjRsrcBndle.getString("LNA.ORDERLINE31"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE32"), centerUnderlineBold));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE33"), centerUnderlineBold));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE35"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE36"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE37"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE38"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE39"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE40"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE41"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE42"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE43"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE44"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE45"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE46"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE47"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE48")+lStrFinYearDesc+gObjRsrcBndle.getString("LNA.ORDERLINE49"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE50"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE51"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE52"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE54"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);
						
						List<MstLnaHodDtls> lLstHodDtls = getHodDtls(gStrLocCode,lStrUserType);
						MstLnaHodDtls lObjHodDtls = new MstLnaHodDtls();
						String lStrHodName = "";
						String lStrHodDsgn = "";
						if(lLstHodDtls != null && !lLstHodDtls.isEmpty()){
							lObjHodDtls = lLstHodDtls.get(0);
							lStrHodName = lObjHodDtls.getHodName();
							lStrHodDsgn = lObjHodDtls.getHodDsgn();
							
						}
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrHodName, rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrHodDsgn, rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE55"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE56"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE57"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE58"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE59"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE60"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE61"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE62"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE63"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE64"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE65"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE66"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE67"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE68"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(20) + gObjRsrcBndle.getString("LNA.ORDERLINE69"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE70"));
						dataList.add(rowList);
					}
					
				}else if(lStrLoanType.equals("800029")){
					gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/HouseOrderReport");
					lObjReport.setAdditionalHeader("<center><b>House Building Advance Order Report</b></center>");
					lLstEmpDtls = getEmpHouseLoanDtls(lLstEmpSevaarthId, lLstTransId);
					
					if (lLstEmpDtls != null && !lLstEmpDtls.isEmpty()) {

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE35"));					
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE36"));					
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();						
						rowList.add(newline);
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE37"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();						
						rowList.add(newline);
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE38"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE3"));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE4"));
						rowList.add(newline);
						dataList.add(rowList);

						/*rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE5"));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE6"));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						//rowList.add(gObjRsrcBndle.getString("LNA.ORDERLINE7"));
						rowList.add(newline);
						dataList.add(rowList);*/
						
						lLstLocName = getLocShortName(Long.parseLong(gStrLocCode));
						String lStrLocName = "";
						String lStrLocShortName = "";
						
						if(!lLstLocName.isEmpty()){
							Object[] lObj = (Object[]) lLstLocName.get(0);
							lStrLocName = (String) lObj[0];
							lStrLocShortName = (String) lObj[1];
						}
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrLocShortName +"/HBA/" + lStrFinYearDesc+"/" + gObjRsrcBndle.getString("LNA.ORDERLINE8.1")+lStrOrderNo, CenterAlignVO));
						dataList.add(rowList);

						Object[] lObjOffDtls = null;
						//String lStrOffName = "";
						String lStrOffAdd = "";
						String lStrOffTown = "";
						BigInteger lBIOffPin = null;
						if(!lLstOfficeDtls.isEmpty()){
							lObjOffDtls = (Object[]) lLstOfficeDtls.get(0);
							//lStrOffName = (String) lObjOffDtls[0];
							lStrOffAdd = (String) lObjOffDtls[1];
							lStrOffTown = (String) lObjOffDtls[2];
							lBIOffPin = (BigInteger) lObjOffDtls[3];
						}
						
						Iterator<Object> it = lLstEmpDtls.iterator();				
						Long lLngTotalSanctionAmt = 0L;
						while (it.hasNext()) {
							tuple = (Object[]) it.next();
							rowList = new ArrayList<Object>();
							rowList.add(lIntRowNum);
							rowList.add(tuple[0].toString() + " , " + tuple[1].toString() + " , " + tuple[2].toString());
							rowList.add(tuple[3].toString() + " / " + lObjDateFormat.format(tuple[4]));
							gLogger.info("sub type is "+tuple[8]);
							if(tuple[8].toString().equals("800038") || tuple[8].toString().equals("800058")){
								rowList.add(new StyledData(tuple[9].toString(), rightAlign));
								lLngTotalSanctionAmt = lLngTotalSanctionAmt + (long)Double.parseDouble(tuple[9].toString());
							}
							else {
								rowList.add(new StyledData(tuple[5].toString(), rightAlign));
								lLngTotalSanctionAmt = lLngTotalSanctionAmt + (Long) tuple[5];
							}
							rowList.add(tuple[6].toString() + " / " + tuple[7].toString());
							subReport.add(rowList);
							lIntRowNum++;
						}

						
						rowList = new ArrayList<Object>();
						//rowList.add(new StyledData(lStrOffName, rightAlign));
						rowList.add(newline);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrOffAdd, rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrOffTown +"-"+((lBIOffPin == null)?"":lBIOffPin), rightAlign));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE13") + lObjDateFormat.format(gDtCurrDate), rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE14"), centerUnderlineBold));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE15") + lStrFinYearDesc, CenterAlignVO));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE16"), CenterAlignVO));
						dataList.add(rowList);

						String lStrAmtInWords = EnglishDecimalFormat.convert(lLngTotalSanctionAmt);					
						rowList = new ArrayList<Object>();
						rowList.add(space(10) + gObjRsrcBndle.getString("LNA.ORDERLINE17") + lStrLocName+" " + gObjRsrcBndle.getString("LNA.ORDERLINE19")
								+lLngTotalSanctionAmt+" " +lStrAmtInWords+" "+ gObjRsrcBndle.getString("LNA.ORDERLINE19.1")
								+ gObjRsrcBndle.getString("LNA.ORDERLINE20"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10)+ gObjRsrcBndle.getString("LNA.ORDERLINE21") + gObjRsrcBndle.getString("LNA.ORDERLINE22")
								+ gObjRsrcBndle.getString("LNA.ORDERLINE23"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE24"), centerUnderlineBold));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(gObjRsrcBndle.getString("LNA.ORDERLINE15") + lStrFinYearDesc, CenterAlignVO));
						dataList.add(rowList);

						
						ReportVO RptVo = null;
						ReportsDAO reportsDao = new ReportsDAOImpl();
						TabularData td = new TabularData(subReport);
						RptVo = reportsDao.getReport("8000062", lObjReport.getLangId(), lObjReport.getLocId());
						(td).setRelatedReport(RptVo);

						rowList = new ArrayList();
						rowList.add(td);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10)+gObjRsrcBndle.getString("LNA.ORDERLINE25") + lStrFinYearDesc +" "+ gObjRsrcBndle.getString("LNA.ORDERLINE26"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(space(10)+gObjRsrcBndle.getString("LNA.ORDERLINE27")+"April-"+lStrFinYearDesc.substring(0, 4)
								+ gObjRsrcBndle.getString("LNA.ORDERLINE27.1")+"May-"+lStrFinYearDesc.substring(5, 9)+" "
								+ gObjRsrcBndle.getString("LNA.ORDERLINE27.2"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10)+gObjRsrcBndle.getString("LNA.ORDERLINE28"));
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(space(10)+gObjRsrcBndle.getString("LNA.ORDERLINE29") + lStrFinYearDesc + " " + gObjRsrcBndle.getString("LNA.ORDERLINE29.1"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10)+gObjRsrcBndle.getString("LNA.ORDERLINE30") + lStrFinYearDesc);
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10)+gObjRsrcBndle.getString("LNA.ORDERLINE31"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(space(10)+gObjRsrcBndle.getString("LNA.ORDERLINE32"));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);
						
						rowList = new ArrayList<Object>();
						rowList.add(newline);
						dataList.add(rowList);
						
						List<MstLnaHodDtls> lLstHodDtls = getHodDtls(gStrLocCode,lStrUserType);
						MstLnaHodDtls lObjHodDtls = new MstLnaHodDtls();
						String lStrHodName = "";
						String lStrHodDsgn = "";
						if(lLstHodDtls != null && !lLstHodDtls.isEmpty()){
							lObjHodDtls = lLstHodDtls.get(0);
							lStrHodName = lObjHodDtls.getHodName();
							lStrHodDsgn = lObjHodDtls.getHodDsgn();
							
						}
						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrHodName, rightAlign));
						dataList.add(rowList);

						rowList = new ArrayList<Object>();
						rowList.add(new StyledData(lStrHodDsgn, rightAlign));
						dataList.add(rowList);
					}
				}
				
			}
		} catch (Exception e) {

			gLogger.error("Exception :" + e, e);
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

	public String getFinYearDescFromCurrDate() {

		String lStrFinYearDesc = "";
		try {
			Date lDCurrDate = DBUtility.getCurrentDateFromDB();
			Query sqlQuery = ghibSession.createQuery("select fym.finYearDesc from SgvcFinYearMst fym where fym.fromDate<= :currDate and fym.toDate>=:currDate");
			sqlQuery.setParameter("currDate", lDCurrDate);
			sqlQuery.setCacheable(true);
			sqlQuery.setCacheRegion("ecache_lookup");

			lStrFinYearDesc = (String) sqlQuery.uniqueResult();

		} catch (Exception e) {
			gLogger.error("Exception occured in getFinYearDescFromCurrDate # \n" + e);
		}
		return lStrFinYearDesc;
	}

	public List<Object> getEmpCompLoanDtls(List<String> lLstEmpSevaarthId, List<String> lLstTransId) {

		List<Object> lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT ME.name,ODM.dsgnName,DO.dcpsDdoOfficeName,ME.basicPay,OEM.empSrvcExp,MCA.amountSanctioned,MCA.installmentAmount,MCA.sancInstallments ");
			lSBQuery.append("FROM MstLnaCompAdvance MCA,MstEmp ME,DdoOffice DO,OrgDesignationMst ODM,OrgEmpMst OEM ");
			lSBQuery.append("WHERE ME.sevarthId = MCA.sevaarthId and ME.currOff = DO.dcpsDdoOfficeIdPk and ME.designation = ODM.dsgnId ");
			lSBQuery.append("and MCA.transactionId IN (:TransId) and ME.orgEmpMstId = OEM.empId and MCA.sevaarthId in (:EmpSevaarthId) and MCA.statusFlag = 'A' ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("EmpSevaarthId", lLstEmpSevaarthId);
			lQuery.setParameterList("TransId", lLstTransId);
			lLstEmpDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstEmpDtls;
	}	
	
		
	public List getLocShortName(Long lLngLocationCode) {

		List lLstLocName = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT CLM.locName,CLM.locShortName FROM CmnLocationMst CLM Where CLM.locId = :lLngLocationCode ");			
			Query sqlQuery = ghibSession.createQuery(lSBQuery.toString());
			sqlQuery.setParameter("lLngLocationCode", lLngLocationCode);
			
			lLstLocName = (List) sqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in getLocShortNameFromPostId # \n" + e);
		}
		return lLstLocName;
	}
	
	public List<Object> getOfficeDtls(String lStrLocationCode) {

		List<Object> lLstOfficeDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT mdo.OFF_NAME,mdo.ADDRESS1,mdo.TOWN,mdo.OFFICE_PIN ");
			lSBQuery.append("FROM MST_DCPS_DDO_OFFICE MDO,ORG_DDO_MST ODM ");
			lSBQuery.append("where odm.LOCATION_CODE = :lStrLocationCode and odm.DDO_CODE = mdo.DDO_CODE and mdo.DDO_OFFICE = 'Yes' ");
			lSBQuery.append("ORDER by mdo.DCPS_DDO_OFFICE_MST_ID ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lStrLocationCode", lStrLocationCode);	
			lLstOfficeDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstOfficeDtls;
	}
	public List<Object> getEmpVehicleLoanDtls(List<String> lLstEmpSevaarthId, List<String> lLstTransId) {

		List<Object> lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT ME.name,ODM.dsgnName,DO.dcpsDdoOfficeName,ME.basicPay,OEM.empSrvcExp,MCA.amountSanctioned,MCA.cappitalInstAmtMonth,MCA.sancCapitalInst ");
			lSBQuery.append("FROM MstLnaMotorAdvance MCA,MstEmp ME,DdoOffice DO,OrgDesignationMst ODM,OrgEmpMst OEM ");
			lSBQuery.append("WHERE ME.sevarthId = MCA.sevaarthId and ME.currOff = DO.dcpsDdoOfficeIdPk and ME.designation = ODM.dsgnId ");
			lSBQuery.append("and MCA.transactionId IN (:TransId) and ME.orgEmpMstId = OEM.empId and MCA.sevaarthId in (:EmpSevaarthId) and MCA.statusFlag = 'A' ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("EmpSevaarthId", lLstEmpSevaarthId);
			lQuery.setParameterList("TransId", lLstTransId);
			lLstEmpDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstEmpDtls;
	}
	public List<Object> getEmpHouseLoanDtls(List<String> lLstEmpSevaarthId, List<String> lLstTransId) {

		List<Object> lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT ME.name,ODM.dsgnName,DO.dcpsDdoOfficeName,ME.basicPay,OEM.empSrvcExp,HBA.amountSanctioned,HBA.principalInstAmtMonth,HBA.sancPrinInst,HBA.advanceSubType,HBA.disbursementOne ");
			lSBQuery.append("FROM MstLnaHouseAdvance HBA,MstEmp ME,DdoOffice DO,OrgDesignationMst ODM,OrgEmpMst OEM ");
			lSBQuery.append("WHERE ME.sevarthId = HBA.sevaarthId and ME.currOff = DO.dcpsDdoOfficeIdPk and ME.designation = ODM.dsgnId ");
			lSBQuery.append("and HBA.transactionId IN (:TransId) and ME.orgEmpMstId = OEM.empId and HBA.sevaarthId in (:EmpSevaarthId) and HBA.statusFlag IN ('A','A1') ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("EmpSevaarthId", lLstEmpSevaarthId);
			lQuery.setParameterList("TransId", lLstTransId);
			lLstEmpDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstEmpDtls;
	}
	
	public List<String> getEmpSevaarthId(Long lLngBillId,Long lLngLoanType) {

		List<String> lLstSevaarthId = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT sevaarthId ");
			
			if(lLngLoanType == 800028)
				lSBQuery.append("FROM MstLnaCompAdvance ");
			else if(lLngLoanType == 800029)
				lSBQuery.append("FROM MstLnaHouseAdvance ");
			else if(lLngLoanType == 800030)
				lSBQuery.append("FROM MstLnaMotorAdvance ");
			
			lSBQuery.append("WHERE lnaBillId = :lLngBillId ");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLngBillId", lLngBillId);
			
			lLstSevaarthId = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstSevaarthId;
	}
	public List<String> getEmpTrnsId(Long lLngBillId,Long lLngLoanType) {

		List<String> lLstTrnsId = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT transactionId ");
			
			if(lLngLoanType == 800028)
				lSBQuery.append("FROM MstLnaCompAdvance ");
			else if(lLngLoanType == 800029)
				lSBQuery.append("FROM MstLnaHouseAdvance ");
			else if(lLngLoanType == 800030)
				lSBQuery.append("FROM MstLnaMotorAdvance ");
			
			lSBQuery.append("WHERE lnaBillId = :lLngBillId ");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLngBillId", lLngBillId);
			
			lLstTrnsId = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstTrnsId;
	}
	
	public List<String> getEmpOrderNo(Long lLngBillId,Long lLngLoanType) {

		List<String> lLstOrderNo = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT orderNo ");
			if(lLngLoanType == 800028)
				lSBQuery.append("FROM MstLnaCompAdvance ");
			else if(lLngLoanType == 800029)
				lSBQuery.append("FROM MstLnaHouseAdvance ");
			else if(lLngLoanType == 800030)
				lSBQuery.append("FROM MstLnaMotorAdvance ");
			
			lSBQuery.append("WHERE lnaBillId = :lLngBillId ");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLngBillId", lLngBillId);
			
			lLstOrderNo = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstOrderNo;
	}
	public List<MstLnaHodDtls> getHodDtls(String lStrLocationCode,String lStrUserType) {

		List<MstLnaHodDtls> lLstHodDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {			
			if(lStrUserType.equals("hod")){
				lSBQuery.append("SELECT HOD FROM MstLnaHodDtls HOD where HOD.locationCode = :lStrLocation ");				
			}else{
				lSBQuery.append("SELECT HOD FROM MstLnaHodDtls HOD,OrgDdoMst DDO where DDO.locationCode = :lStrLocation ");
				lSBQuery.append(" AND DDO.hodLocCode = HOD.locationCode");
			}
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lStrLocation", lStrLocationCode);
			lLstHodDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstHodDtls;
	}		
}
