package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

public class PrintFormR2ConsolidatedReport extends DefaultReportDataFinder{
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
	ReportVO RptVo = null;
	
	
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

		
		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		report.setStyleList(rowsFontsVO);
		
		StyleVO[] redFontVO = new StyleVO[1];
		
		redFontVO[0] = new StyleVO();
		redFontVO[0].setStyleId(IReportConstants.COLOR);
		redFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_COLOR_RED);
		//redFontVO[0].setStyleValue("#E34234");
		
		if (report.getReportCode().equals("700026")) {
	
			//report.setStyleList(noBorder);
			//report.setStyleList(rowsFontsVO);
			
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        Date date = new Date();
	        String curDate = dateFormat.format(date);
	        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        
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
	        
	        String lStrYear = lObjDcpsCommonDAO.getYearCodeForYearId(Long.valueOf(lStrYearId));
	        String lStrMonthName = lObjDcpsCommonDAO.getMonthForId(Long.valueOf(lStrMonthId));
	        String lStrTreasuryName = lObjDcpsCommonDAO.getLocNameforLocId(Long.valueOf(gStrLocCode));
	        
			StringBuffer lSBQuery = new StringBuffer();
			
			lSBQuery.append(" SELECT EM.emp_name,EM.dcps_id,TR.scheme_code,VC.voucher_no,VC.voucher_date,TR.contribution,TR.ddo_code,TR.EMPLOYER_CONTRI_FLAG,VC.IS_CHALLAN");
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
			Query.setLong("treasuryCode",Long.valueOf(gStrLocCode));
			
			List lLstFinal = Query.list();
			Long lLongSRNo = 1l;
			String lStrDDOName = "";
			Double lDoubleGrandTotal = 0d;
			Double totalOfGrandTotal = 0d;
			String lStrPreviousDDOCode = "";
			Double lDoubleEmployerContri = 0d;
			
			if (lLstFinal != null && !lLstFinal.isEmpty())
			{
				Iterator it = lLstFinal.iterator();
				
				while (it.hasNext())
				{
					lDoubleEmployerContri = 0d;
					Object[] tuple = (Object[]) it.next();
					td = new ArrayList();
					
					if(!lStrPreviousDDOCode.equals(tuple[6].toString()))
					{
						lLongSRNo = 1l; 
					}
					lStrPreviousDDOCode = tuple[6].toString();
					
					td.add(lLongSRNo);  // SR No
					
					if(tuple[0] != null) // name 
					{
						td.add(tuple[0].toString());
					}
					else
					{
						td.add("");
					}
					if(tuple[1] != null) // DCPS ID 
					{
						td.add(tuple[1].toString());
					}
					else
					{
						td.add("");
					}
					if(tuple[2] != null) // Major Head 
					{
						td.add(tuple[2].toString());
					}
					else
					{
						td.add("");
					}
					
					if(tuple[3] != null && tuple[4] != null ) // Voucher No and Date
					{
						// Voucher Or Challan identified and distinguished by different colour
						
						if(tuple[8] != null)
						{
							if(tuple[8].toString().toUpperCase().equals("Y"))
							{
								//Give different colour for challan 
								td.add("<font style='color:red;'>"+tuple[3].toString()+ space(2) +  dateFormat.format(dateFormat1.parse(tuple[4].toString()))+"</font>");
							}
							else
							{
								td.add(tuple[3].toString()+ space(2) +  dateFormat.format(dateFormat1.parse(tuple[4].toString())));
							}
						}
						else
						{
							td.add(tuple[3].toString()+ space(2) +  dateFormat.format(dateFormat1.parse(tuple[4].toString())));
						}
					}
					else
					{
						td.add("");
					}
					
					if(tuple[5] != null) // Employee's contribution
					{
						td.add(Double.parseDouble(tuple[5].toString()));
					}
					else
					{
						td.add("");
					}
					
					if(tuple[5] != null && tuple[7] != null) // Employer's contribution. Both are same as of now.
					{
						if(tuple[7].toString().equals("Y"))
						{
							lDoubleEmployerContri = Double.parseDouble(tuple[5].toString());
							td.add(lDoubleEmployerContri);
						}
						else
						{
							td.add("0");
						}
					}
					else
					{
						td.add("0");
					}
					
					if(tuple[5] != null)
					{
						lDoubleGrandTotal = Double.parseDouble(tuple[5].toString()) +  lDoubleEmployerContri ;
					}
					td.add(lDoubleGrandTotal);
					
					lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(tuple[6].toString());
					
					
					if(!"".equals(lStrDDOName) && lStrDDOName != null)  // DDO name
					{
						td.add(lStrDDOName);
					}
					else
					{
						td.add("");
					}
					
					dataList.add(td);
					
					totalOfGrandTotal = totalOfGrandTotal + lDoubleGrandTotal ;
					lLongSRNo ++;
				}
			}
			
			   	String header1 = "<p><font size=\"1px\"> "	+ "(As referred to in para no.17 of Government Resolution, Finance Department,No.CPS 1007/18/SER-4,dated 7 July,2007)" + "</font></p>";
		        String header2 = "<p><b>" + "<font size=\"1.5px\"> "	+ "CONSOLIDATED RECEIPT-CUM-SCHEDULE FOR 8342-OTHER DEPOSITS-120 MISCELLANEOUS DEPOSITS" + "</font></b></br>";
		        String header3 = "<b>" +  "<font size=\"1.5px\"> "+ "Rs . " + totalOfGrandTotal + " /- " + space(20) + "For the Month of " + lStrMonthName + space(2) + lStrYear + "</font></b></br>";
		        String header4 = "<b>" + "<font size=\"1px\"> " + "Name of Treasury / Sub-Treasury : " + lStrTreasuryName + " / " + gStrLocCode + space(5) + "Date :- " + space(2)  + curDate + "</font>" + "</b>" + "</p>"  ;			
					        
				String additionalHeader =  header1 + header2 + header3 + header4 + "<br>" ;
				report.setAdditionalHeader(additionalHeader);
				
				ReportAttributeVO[] ravo = new ReportAttributeVO[1];
				
				ravo[0] = new ReportAttributeVO();
				ravo[0].setAttributeType(IReportConstants.ATTRIB_OTHER);
				ravo[0].setLocation(IReportConstants.LOCATION_FOOTER);
				ravo[0].setAlignment(IReportConstants.ALIGN_RIGHT);
				ravo[0].setAttributeValue("<br>" + "<br>"+ "<br>" + "<strong> TREASURY OFFICER </strong>");
				
				report.setReportAttributes(ravo);
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
