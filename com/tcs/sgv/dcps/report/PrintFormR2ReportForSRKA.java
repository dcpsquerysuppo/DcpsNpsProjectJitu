package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jfree.util.StringUtils;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.ui.reports.ReportColumnsTableModel;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

public class PrintFormR2ReportForSRKA extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger
		.getLogger(PrintFormR2ConsolidatedReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");
	
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	
	public Collection findReportData(ReportVO report, Object criteria)
		throws ReportException {
	
	String langId = report.getLangId();
	
	String locId = report.getLocId();
	
	Connection con = null;
	
	criteria.getClass();
	
	Statement smt = null;
	ResultSet rs = null;
	String empId = null;
	
	ReportsDAO reportsDao = new ReportsDAOImpl();
	ArrayList dataList = new ArrayList();
	ArrayList tr = null;
	ArrayList td = null;
	ArrayList rptList1 = null;
	TabularData rptTd = null;
	TabularData tdForEmpDtls = null;
	TabularData tdForDisplayingDDOWiseTotal = null;
	TabularData tdForDisplayingTreasuryWiseTotal = null;
	ReportVO RptVo = null;
	ReportVO RptVoForEmpDtls = null;
	ReportVO RptVoForDisplayingDDOWiseTotal = null;
	ReportVO RptVoForDisplayingTreasuryWiseTotal = null;
	
	
	try {
		requestAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.REQUEST_ATTRIBUTES);
		serviceLocator = (ServiceLocator) requestAttributes
				.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();
	
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
	
		Long locationId = (Long) loginDetail.get("locationId");
	
		StringBuffer sql = new StringBuffer();
		String StrSqlQuery = "";
	
		StyleVO[] rowsFontsVO = new StyleVO[4];
		rowsFontsVO[0] = new StyleVO();
		rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
		rowsFontsVO[0].setStyleValue("26");
		rowsFontsVO[1] = new StyleVO();
		rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rowsFontsVO[1].setStyleValue("14");
		rowsFontsVO[2] = new StyleVO();
		rowsFontsVO[2].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
		rowsFontsVO[2].setStyleValue("Shruti");
		rowsFontsVO[3] = new StyleVO();
		rowsFontsVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
		rowsFontsVO[3].setStyleValue("white");
		
		StyleVO[] LeftAlignVO = new StyleVO[2];
		LeftAlignVO[0] = new StyleVO();
		LeftAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		LeftAlignVO[0].setStyleValue("8");
		LeftAlignVO[1] = new StyleVO();
		LeftAlignVO[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		LeftAlignVO[1]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		
		StyleVO[] CenterAlignVO = new StyleVO[2];
		CenterAlignVO[0] = new StyleVO();
		CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		CenterAlignVO[0].setStyleValue("8");
		CenterAlignVO[1] = new StyleVO();
		CenterAlignVO[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		CenterAlignVO[1]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		
		StyleVO[] RightAlignVO = new StyleVO[2];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		RightAlignVO[0].setStyleValue("8");
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[1]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		
		StyleVO[] boldFontLeftAlign = new StyleVO[3];
		boldFontLeftAlign[0] = new StyleVO();
		boldFontLeftAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldFontLeftAlign[0].setStyleValue("8");
		boldFontLeftAlign[1] = new StyleVO();
		boldFontLeftAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldFontLeftAlign[1]
				.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldFontLeftAlign[2] = new StyleVO();
		boldFontLeftAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		boldFontLeftAlign[2]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		
		StyleVO[] boldFontRightAlign = new StyleVO[3];
		boldFontRightAlign[0] = new StyleVO();
		boldFontRightAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldFontRightAlign[0].setStyleValue("8");
		boldFontRightAlign[1] = new StyleVO();
		boldFontRightAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldFontRightAlign[1]
				.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldFontRightAlign[2] = new StyleVO();
		boldFontRightAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		boldFontRightAlign[2]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		
		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		report.setStyleList(rowsFontsVO);
		
		
		if (report.getReportCode().equals("700040")) {
	
			//report.setStyleList(noBorder);
			//report.setStyleList(rowsFontsVO);
			
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        Date date = new Date();
	        String curDate = dateFormat.format(date);
	        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        String lStrNewLine = StringUtils.getLineSeparator();
	        
			td = new ArrayList();
				
			Map lMapRequestAttributes = null;
			Map lMapSessionAttributes = null;
			List lArrReportData = null;
			LoginDetails lObjLoginVO = null;
			String gStrLocCode = null;
			Long gLngLangId = null;
			
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
					serviceLocator.getSessionFactory());
			
			String lStrYearId = (String) report.getParameterValue("yearId");
	        String lStrMonthId = (String) report.getParameterValue("monthId");
	        String lStrTreasuryCode = (String) report.getParameterValue("treasuryCode");
	        
	        String lStrYear = lObjDcpsCommonDAO.getYearCodeForYearId(Long.valueOf(lStrYearId));
	        String lStrMonthName = lObjDcpsCommonDAO.getMonthForId(Long.valueOf(lStrMonthId));
	        String lStrTreasuryName = lObjDcpsCommonDAO.getLocNameforLocId(Long.valueOf(gStrLocCode));
	        
			StringBuffer lSBQuery = new StringBuffer();
			
//			lSBQuery.append(" SELECT EM.emp_name,EM.dcps_id,TR.scheme_code,VC.voucher_no,VC.voucher_date,TR.contribution,TR.TYPE_OF_PAYMENT,TR.ddo_code,TR.dcps_emp_id,TR.dcps_emp_id");
			lSBQuery.append(" SELECT EM.emp_name,EM.dcps_id,TR.scheme_code,VC.voucher_no,VC.voucher_date,TR.contribution,TR.TYPE_OF_PAYMENT,TR.ddo_code,TR.dcps_emp_id,TR.EMPLOYER_CONTRI_FLAG");
			lSBQuery.append(" FROM trn_dcps_contribution TR,mst_dcps_contri_voucher_dtls VC,mst_dcps_emp EM");
			lSBQuery.append(" WHERE TR.rlt_contri_voucher_id=VC.mst_dcps_contri_voucher_dtls");
			lSBQuery.append(" AND TR.dcps_emp_id = EM.dcps_emp_id ");
			lSBQuery.append(" AND TR.month_id = :month");
			lSBQuery.append(" AND TR.FIN_YEAR_ID = :year");
			lSBQuery.append(" AND TR.treasury_code = :treasuryCode");
			lSBQuery.append(" AND TR.REG_STATUS = 1");
			lSBQuery.append(" AND EM.REG_STATUS = 1");
			lSBQuery.append(" AND EM.DCPS_ID IS NOT NULL");
			
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
			Query.setLong("month",Long.valueOf(lStrMonthId));
			Query.setLong("year",Long.valueOf(lStrYearId));
			Query.setLong("treasuryCode",Long.valueOf(lStrTreasuryCode));
			
			List lLstFinal = Query.list();
			Long lLongSRNo = 1l;
			String lStrDDOName = "";
			Long lLongGrandTotal = 0l;
			String lStrDdoCode = null;
			String lStrPreviousDDOCode = "";
			String lStrTypeOfPayment = "";
			String lStrTypeOfContribution = "";
			String lStrEmpId = "" ; 
			Long lLongEmpId = null;
			Map lMapEmpsForDDO = new HashMap();
			Map lMapEmpDetailsForEmp = new HashMap();
			Set lSetOfEmpsUnderGivenDDO = null;
			List lListEmpDetailsForGivenEmp = null;
			List rowList = null;
			
			// Added for generating two Maps
			if (lLstFinal != null && !lLstFinal.isEmpty())
			{
				Iterator it = lLstFinal.iterator();
				
				while (it.hasNext())
				{
					Object[] tuple = (Object[]) it.next();
					
					lStrDdoCode = tuple[7].toString();
					lLongEmpId = Long.valueOf(tuple[8].toString());
					
					//Generates Map for DDO and his Employees
					lSetOfEmpsUnderGivenDDO = (Set) lMapEmpsForDDO.get(lStrDdoCode);
					if(lSetOfEmpsUnderGivenDDO != null)
					{
						lSetOfEmpsUnderGivenDDO.add(lLongEmpId);
					}
					else
					{
						lSetOfEmpsUnderGivenDDO = new HashSet();
						lSetOfEmpsUnderGivenDDO.add(lLongEmpId);
					}
					lMapEmpsForDDO.put(lStrDdoCode, lSetOfEmpsUnderGivenDDO);
					
					//Generates Map for Employee and his details 
					lListEmpDetailsForGivenEmp = (List) lMapEmpDetailsForEmp.get(lLongEmpId);
					if(lListEmpDetailsForGivenEmp != null)
					{
						lListEmpDetailsForGivenEmp.add(tuple);
					}
					else
					{
						lListEmpDetailsForGivenEmp = new ArrayList();
						lListEmpDetailsForGivenEmp.add(tuple);
					}
					lMapEmpDetailsForEmp.put(lLongEmpId, lListEmpDetailsForGivenEmp);
					
				}
			}
			
			// Added for generating two Maps overs
		
			// Getting data from two Maps
			String lStrDDOCodeAfter = null;
			String lStrDDONameAfter = null;
			Set lSetEmpIdsForDDOAfter = null;
			Long lLongEmpIdAfter = null;
			List lListEmpDetailsAfter = null;
			Iterator ItForMapEmpsForDDO = lMapEmpsForDDO.keySet().iterator() ;
			Long lLongEmployeeWiseTotalOfEmployeeContri = 0l;
			Long lLongEmployeeWiseTotalOfEmployerContri = 0l;
			Long lLongDDOWiseTotalOfEmployeeContri = 0l;
			Long lLongDDOWiseTotalOfEmployerContri = 0l;
			Long lLongTreasuryWiseTotalOfEmployeeContri = 0l;
			Long lLongTreasuryWiseTotalOfEmployerContri = 0l;
			
			List dataListForEmpDtls = null;
			List dataListforDisplayingDDOWiseSubTotal = null;
			List dataListforDisplayingTreasuryWiseTotal = null;
			
			while(ItForMapEmpsForDDO.hasNext())
			{
				lStrDDOCodeAfter = ItForMapEmpsForDDO.next().toString() ;
				lSetEmpIdsForDDOAfter = (Set) lMapEmpsForDDO.get(lStrDDOCodeAfter);
				lStrDDONameAfter = lObjDcpsCommonDAO.getDdoNameForCode(lStrDDOCodeAfter);
				lLongDDOWiseTotalOfEmployeeContri = 0l;
				lLongDDOWiseTotalOfEmployerContri = 0l;
				lLongSRNo = 1l;
				
				rowList = new ArrayList();
				rowList.add(lStrDDONameAfter);
				dataList.add(rowList);
				
				Iterator ItOfEmpIds =  lSetEmpIdsForDDOAfter.iterator() ;
				
				dataListForEmpDtls = new ArrayList();
				
				//for(Integer lInt=0;lInt<lSetEmpIdsForDDOAfter.size();lInt++)
				while(ItOfEmpIds.hasNext())
				{
					lLongEmpIdAfter = Long.valueOf(ItOfEmpIds.next().toString());
					lListEmpDetailsAfter = (List) lMapEmpDetailsForEmp.get(lLongEmpIdAfter);
					lLongEmployeeWiseTotalOfEmployeeContri = 0l;
					lLongEmployeeWiseTotalOfEmployerContri = 0l;
					Double lDoubleEmployerContri = 0d;
					
					
					for(Integer lIntInner=0;lIntInner<lListEmpDetailsAfter.size();lIntInner++)
					{
						
						Object[] tupleForEmp = (Object[]) lListEmpDetailsAfter.get(lIntInner);
						td = new ArrayList();
						td.add(lLongSRNo);  // SR No
						lLongSRNo++;
						
						if(tupleForEmp[0] != null) // name 
						{
							td.add(new StyledData(tupleForEmp[0].toString(),LeftAlignVO));
						}
						else
						{
							td.add("");
						}
						if(tupleForEmp[1] != null) // DCPS ID 
						{
							td.add(new StyledData(tupleForEmp[1].toString(),CenterAlignVO));
						}
						else
						{
							td.add("");
						}
						if(tupleForEmp[2] != null) // Major Head 
						{
							lStrTypeOfPayment = tupleForEmp[6].toString();
							
							//Code for displaying month and year for NonRegular entries is pending.
							
							if(!lStrTypeOfPayment.equals(""))
							{
								lStrTypeOfContribution =  lObjDcpsCommonDAO.getCmnLookupNameFromId(Long.valueOf(lStrTypeOfPayment)) ;
							}
							td.add(new StyledData(tupleForEmp[2].toString() +  lStrNewLine + lStrTypeOfContribution + ":" + space(2) + lStrMonthName + space(3) + lStrYear,LeftAlignVO));
						}
						else
						{
							td.add("");
						}
						
						if(tupleForEmp[3] != null && tupleForEmp[4] != null && tupleForEmp[6] != null) // Voucher No and Date
						{
							
							td.add(new StyledData(tupleForEmp[3].toString()+ space(2) +  dateFormat.format(dateFormat1.parse(tupleForEmp[4].toString())),RightAlignVO));
						}
						else
						{
							td.add("");
						}
						
						if(tupleForEmp[5] != null) // Employee's contribution
						{
							td.add(new StyledData(Double.valueOf(tupleForEmp[5].toString()).longValue(),RightAlignVO));
							lLongEmployeeWiseTotalOfEmployeeContri = lLongEmployeeWiseTotalOfEmployeeContri + Double.valueOf(tupleForEmp[5].toString()).longValue() ;
						}
						else
						{
							td.add("");
						}
						
						if(tupleForEmp[5] != null && tupleForEmp[9] != null) // Employer's contribution. Both are same as of now.
						{
							if(tupleForEmp[9].toString().equals("Y"))
							{
								lDoubleEmployerContri = Double.parseDouble(tupleForEmp[5].toString());
								td.add(new StyledData(lDoubleEmployerContri.longValue(),RightAlignVO));
							}
							else
							{
								td.add("0");
							}
							lLongEmployeeWiseTotalOfEmployerContri = lLongEmployeeWiseTotalOfEmployerContri + lDoubleEmployerContri.longValue() ;
						}
						else
						{
							td.add("");
						}
						
						dataListForEmpDtls.add(td);
						
					}
					
					td = new ArrayList();
					td.add("");
					td.add(new StyledData("Employee-wise sub Total",boldFontLeftAlign));
					td.add("");
					td.add("");
					td.add("");
					td.add(new StyledData(lLongEmployeeWiseTotalOfEmployeeContri,RightAlignVO));
					td.add(new StyledData(lLongEmployeeWiseTotalOfEmployerContri,RightAlignVO));
					dataListForEmpDtls.add(td);
					
					lLongDDOWiseTotalOfEmployeeContri = lLongDDOWiseTotalOfEmployeeContri + lLongEmployeeWiseTotalOfEmployeeContri ;
					lLongDDOWiseTotalOfEmployerContri = lLongDDOWiseTotalOfEmployerContri + lLongEmployeeWiseTotalOfEmployerContri ;
				
				}
				
				lLongTreasuryWiseTotalOfEmployeeContri = lLongTreasuryWiseTotalOfEmployeeContri + lLongDDOWiseTotalOfEmployeeContri ;
				lLongTreasuryWiseTotalOfEmployerContri = lLongTreasuryWiseTotalOfEmployerContri + lLongDDOWiseTotalOfEmployerContri ;
				
				tdForEmpDtls = new TabularData(dataListForEmpDtls);
				RptVoForEmpDtls = reportsDao.getReport("700041", report.getLangId(),
						report.getLocId()); //
				(tdForEmpDtls).setRelatedReport(RptVoForEmpDtls);
				
				rowList = new ArrayList();
				rowList.add(tdForEmpDtls);
				dataList.add(rowList);
				
				dataListforDisplayingDDOWiseSubTotal = new ArrayList();
				
				rowList = new ArrayList();
				rowList.add(new StyledData("DDO-wise sub Total", boldFontLeftAlign));
				rowList.add(new StyledData(lLongDDOWiseTotalOfEmployeeContri, boldFontRightAlign));
				rowList.add(new StyledData(lLongDDOWiseTotalOfEmployerContri, boldFontRightAlign));
				dataListforDisplayingDDOWiseSubTotal.add(rowList);
				
				tdForDisplayingDDOWiseTotal = new TabularData(dataListforDisplayingDDOWiseSubTotal);
				RptVoForDisplayingDDOWiseTotal = reportsDao.getReport("700042", report.getLangId(),
						report.getLocId()); //
				ReportColumnVO[] lObjReportColumnVO = (ReportColumnVO[]) RptVoForDisplayingDDOWiseTotal.getReportColumns();
				lObjReportColumnVO[0].setColumnWidth(64);
				lObjReportColumnVO[1].setColumnWidth(18);
				lObjReportColumnVO[2].setColumnWidth(18);
				
				(tdForDisplayingDDOWiseTotal).setRelatedReport(RptVoForDisplayingDDOWiseTotal);
				
				rowList = new ArrayList();
				rowList.add(tdForDisplayingDDOWiseTotal);
				dataList.add(rowList);
				
			}
			
				//For the last two lines
						
				dataListforDisplayingTreasuryWiseTotal = new ArrayList();
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Treasury-wise sub Total", boldFontLeftAlign));
				rowList.add(new StyledData(lLongTreasuryWiseTotalOfEmployeeContri, boldFontRightAlign));
				rowList.add(new StyledData(lLongTreasuryWiseTotalOfEmployerContri, boldFontRightAlign));
				dataListforDisplayingTreasuryWiseTotal.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData(space(3) + "Grand Total", boldFontLeftAlign));
				rowList.add(new StyledData(lLongTreasuryWiseTotalOfEmployeeContri, boldFontRightAlign));
				rowList.add(new StyledData(lLongTreasuryWiseTotalOfEmployerContri, boldFontRightAlign));
				dataListforDisplayingTreasuryWiseTotal.add(rowList);
			
				tdForDisplayingTreasuryWiseTotal = new TabularData(dataListforDisplayingTreasuryWiseTotal);
				RptVoForDisplayingTreasuryWiseTotal = reportsDao.getReport("700042", report.getLangId(),
						report.getLocId()); //
				ReportColumnVO[] lObjReportColumnVO = (ReportColumnVO[]) RptVoForDisplayingTreasuryWiseTotal.getReportColumns();
				lObjReportColumnVO[0].setColumnWidth(64);
				lObjReportColumnVO[1].setColumnWidth(18);
				lObjReportColumnVO[2].setColumnWidth(18);
				
				(tdForDisplayingTreasuryWiseTotal).setRelatedReport(RptVoForDisplayingTreasuryWiseTotal);
				
				rowList = new ArrayList();
				rowList.add(tdForDisplayingTreasuryWiseTotal);
				dataList.add(rowList);
			
			   	String header1 = "<p><font size=\"1px\"> "	+ "(As referred to in para no.17 of Government Resolution, Finance Department,No.CPS 1007/18/SER-4,dated 7 July,2007)" + "</font></p>";
		        String header2 = "<p><b>" + "<font size=\"1.5px\"> "	+ "CONSOLIDATED RECEIPT-CUM-SCHEDULE FOR 8342-OTHER DEPOSITS-120 MISCELLANEOUS DEPOSITS" + "</font></b></br>";
		        String header3 = "<b>" +  "<font size=\"1.5px\"> "+ "Rs . " + lLongGrandTotal + " /- " + space(20) + "For the Month of " + lStrMonthName + space(2) + lStrYear + "</font></b></br>";
		        String header4 = "<b>" + "<font size=\"1px\"> " + "Name of Treasury / Sub-Treasury : " + lStrTreasuryName + " / " + gStrLocCode + space(5) + "Date :- " + space(2)  + curDate + "</font>" + "</b>" + "</p>"  ;			
					        
				String additionalHeader =  header1 + header2 + header3 + header4 +  "<br>" ;
				report.setAdditionalHeader(additionalHeader);
				
		}
	}
	
	catch (Exception e) {
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
	
		 public List getMonth(String lStrLangId, String lStrLocCode) 
		    {
		        List<Object> lArrMonths = new ArrayList<Object>();
		        try
		        {
		            Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
	
		            String lStrBufLang = "SELECT monthId, monthName FROM SgvaMonthMst WHERE langId = :langId ORDER BY monthNo";
		            
		            Query lObjQuery = lObjSession.createQuery(lStrBufLang);
		            lObjQuery.setString("langId", lStrLangId);
		            
		        	List lLstResult = lObjQuery.list();
		            ComboValuesVO lObjComboValuesVO = null;
		            Object[] lArrData = null;
		            
		            if(lLstResult != null && !lLstResult.isEmpty())
		            {
		            	for(int lIntCtr=0; lIntCtr  < lLstResult.size();lIntCtr++)
		            	{
		            		lObjComboValuesVO = new ComboValuesVO();
		            		lArrData=(Object[])lLstResult.get(lIntCtr);
		            		lObjComboValuesVO.setId(lArrData[0].toString());
		            		lObjComboValuesVO.setDesc(lArrData[1].toString());
		            		lArrMonths.add(lObjComboValuesVO);
		            	}
		            }
		        }
		        catch (Exception e)
		        {
		            gLogger.error("Error is : " + e, e);
		        }
		        
		        return lArrMonths;
		    }
		
	public List getYear(String lStrLangId, String lStrLocId) {
	
		 List<Object> lArrYears = new ArrayList<Object>();
	  try
	  {
	      Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
	
	      String lStrBufLang = "SELECT finYearId, finYearDesc FROM SgvcFinYearMst WHERE langId = :langId and finYearCode BETWEEN '2007' AND '2015' ORDER BY finYearCode";
	      
	      Query lObjQuery = lObjSession.createQuery(lStrBufLang);
	      lObjQuery.setString("langId", lStrLangId);
	      
	  	List lLstResult = lObjQuery.list();
	      ComboValuesVO lObjComboValuesVO = null;
	      Object[] lArrData = null;
	      
	      if(lLstResult != null && !lLstResult.isEmpty())
	      {
	      	for(int lIntCtr=0; lIntCtr  < lLstResult.size();lIntCtr++)
	      	{
	      		lObjComboValuesVO = new ComboValuesVO();
	      		lArrData=(Object[])lLstResult.get(lIntCtr);
	      		lObjComboValuesVO.setId(lArrData[0].toString());
	      		lObjComboValuesVO.setDesc(lArrData[1].toString());
	      		lArrYears.add(lObjComboValuesVO);
	      	}
	      }
	  }
	  catch (Exception e)
	  {
	      gLogger.error("Error is : " + e, e);
	  }
  
	  return lArrYears;
	}
	
	public List getAllTreasuries(String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"select loc_Id , loc_Name from Cmn_Location_Mst CM where department_Id in (100003,100006)  and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111) order by CM.loc_id ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc("("+treasury_id+")"+treasury_name);
				arrTreasury.add(vo);
			}

		} catch (Exception e) {

		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}
	
	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
	
	public String getNewline() {
		String getNewline = "";
		getNewline = "\\u000d\\u000a";
		return getNewline;
	}
}
