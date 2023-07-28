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
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.lna.dao.LNARequestProcessDAO;
import com.tcs.sgv.lna.dao.LNARequestProcessDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaHodDtls;
import com.tcs.sgv.lna.valueobject.MstLnaOrderReq;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ComputerOrderReport extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(ComputerOrderReport.class);
	public static String newline = System.getProperty("line.separator");

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/ComputerOrderReport");
	
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
			StyleVO[] rowsFontsVO = new StyleVO[4];
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
			rowsFontsVO[3].setStyleId(IReportConstants.REPORT_PAGINATION);
			rowsFontsVO[3].setStyleValue("NO");

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

			if (lObjReport.getReportCode().equals("8000061")) {

				String lStrFinYearDesc = getFinYearDescFromCurrDate();
				String lStrSevaarthId = lObjReport.getParameterValue("sevaarthId").toString();
				String lStrTranId = lObjReport.getParameterValue("tranId").toString();
				String lStrArrSevaarthId[] = lStrSevaarthId.split(",");
				String lStrArrTranId[] = lStrTranId.split(",");

				List<String> lLstEmpSevaarthId = Arrays.asList(lStrArrSevaarthId);
				List<String> lLstTransId = Arrays.asList(lStrArrTranId);

				List lLstEmpDtls = null;
				Integer lIntRowNum = 1;
				Object[] tuple = null;
				lLstEmpDtls = getEmpLoanDtls(lLstEmpSevaarthId, lLstTransId);
				ArrayList<Object> subReport = new ArrayList<Object>();
				List lLstLocName = null;
				LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(null,serviceLocator.getSessionFactory());
				
				String lStrOrderNo = lObjRequestProcessDAO.generateOrderNo();
				List lLstOfficeDtls = getOfficeDtls(gStrLocCode);
				
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

					List<MstLnaHodDtls> lLstHodDtls = getHodDtls(gStrLocCode);
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
				
					
					updateOrderNo(lStrOrderNo,lLstEmpSevaarthId,lLstTransId);
					
					inputMap.put("billAmt", lLngTotalSanctionAmt);
					inputMap.put("advanceType", 800028L);
					inputMap.put("orderNo", lStrOrderNo);
					inputMap.put("lLstEmpSevaarthId", lLstEmpSevaarthId);
					inputMap.put("lLstTransId", lLstTransId);
					inputMap.put("lObjLoginVO",lObjLoginVO);
					inputMap.put("CmnLocationMstDst", CmnLocationMstDst);
					
					ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
					inputMap.put("serviceLocator", serviceLocator);
					
					resultObject = serviceLocator.executeService("generateLoanBill",inputMap);				
				
					
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

	public List<Object> getEmpLoanDtls(List<String> lLstEmpSevaarthId, List<String> lLstTransId) {

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
	
	public void updateOrderNo(String lStrOrderNo,List<String> lLstSevaarthId,List<String> lLstTrnsId){
			
		StringBuilder lSBQuery = new StringBuilder();
		Date lDtCurrDate = DBUtility.getCurrentDateFromDB();
		try {
			lSBQuery.append("UPDATE MstLnaCompAdvance SET orderNo = :OrderNo,orderDate = :currDate ");			
			lSBQuery.append("WHERE sevaarthId IN (:EmpSevaarthId) and transactionId IN (:TransId) and statusFlag = 'A'");			
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			
			lQuery.setParameterList("EmpSevaarthId", lLstSevaarthId);
			lQuery.setParameterList("TransId", lLstTrnsId);
			lQuery.setParameter("currDate", lDtCurrDate);
			lQuery.setParameter("OrderNo", lStrOrderNo);
			
			lQuery.executeUpdate();

			
			Map inputMap = new HashMap();
			Map baseLoginMap = new HashMap();
			
			baseLoginMap.put("userId", lObjLoginVO.getUser().getUserId());
			baseLoginMap.put("langId", lObjLoginVO.getLangId());;
			inputMap.put("baseLoginMap", baseLoginMap);
			inputMap.put("lObjLoginVO",lObjLoginVO);
			inputMap.put("CmnLocationMstDst", CmnLocationMstDst);
			inputMap.put("serviceLocator", serviceLocator);
			
			MstLnaOrderReq lObjMstLnaOrderReq = new MstLnaOrderReq();
			
			Long lLngOrderId = IFMSCommonServiceImpl.getNextSeqNum("MST_LNA_ORDER_REQ", inputMap);
			
			lObjMstLnaOrderReq.setOrderId(lLngOrderId);
			lObjMstLnaOrderReq.setLocationCode(gStrLocCode);
			lObjMstLnaOrderReq.setOrderNo(lStrOrderNo);			
			lObjMstLnaOrderReq.setCreatedPostId(lObjLoginVO.getPrimaryPost().getPostId());
			lObjMstLnaOrderReq.setCreatedUserId(lObjLoginVO.getUser().getUserId());
			lObjMstLnaOrderReq.setCreatedDate(lDtCurrDate);
			ghibSession.save(lObjMstLnaOrderReq);			
			ghibSession.flush();
			
		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
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
	public List<MstLnaHodDtls> getHodDtls(String lStrLocationCode) {

		List<MstLnaHodDtls> lLstHodDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("FROM MstLnaHodDtls where locationCode = :lStrLocationCode ");
						
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lStrLocationCode", lStrLocationCode);	
			lLstHodDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("Exception occured in" + e);
		}
		return lLstHodDtls;
	}	
}

