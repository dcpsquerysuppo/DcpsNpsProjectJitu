package com.tcs.sgv.dcps.report;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;


public class  MissingCreditReportByTresury extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(MissingCreditReportByTresury.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");
 
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	private HttpServletRequest request = null;
	
	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {
	
	String langId = report.getLangId();
	String locId = report.getLocId();
	Connection con = null;
	criteria.getClass();
	Statement lStmt = null;
	PreparedStatement psmt= null;
	ResultSet rs = null;
	ReportsDAO reportsDao = new ReportsDAOImpl();
	ArrayList rowList = new ArrayList();
	ArrayList dataList = new ArrayList();
	ArrayList tr = null;
	ArrayList td = null;
	ArrayList rptList1 = null;
	TabularData rptTd = null;
	ReportVO RptVo = null;
	ReportVO RptVoForHiddenColumns = null;
	TabularData td1 = null;
	TabularData td2 = null;
	con = DBConnection.getConnection(  );

	try {
	requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
	serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
	lObjSessionFactory = serviceLocator.getSessionFactorySlave();

	final Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
	final Map serviceMap = (Map) requestAttributes.get("serviceMap");
	final Map baseLoginMap = (Map) serviceMap.get("baseLoginMap");
	request = (HttpServletRequest) serviceMap.get("requestObj");

	ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
	SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
	con = lObjSessionFactory.getCurrentSession().connection();
	Statement smt = con.createStatement();	
	StringBuffer lStrForthLastLineInTable = null;	
	ArrayList dataListForTableWithHiddenColumns = new ArrayList();
	Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
	Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
	Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
	Long locationId = (Long) loginDetail.get("locationId");

	StyleVO[] rowsFontsVO = new StyleVO[5];
	rowsFontsVO[0] = new StyleVO();
	rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
	rowsFontsVO[0].setStyleValue("26");
	rowsFontsVO[1] = new StyleVO();
	rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
	rowsFontsVO[1].setStyleValue("10");
	rowsFontsVO[2] = new StyleVO();
	rowsFontsVO[2].setStyleId(IReportConstants.BACKGROUNDCOLOR);
	rowsFontsVO[2].setStyleValue("white");
	rowsFontsVO[3] = new StyleVO();
	rowsFontsVO[3].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
	rowsFontsVO[3].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
	rowsFontsVO[4] = new StyleVO();
	rowsFontsVO[4].setStyleId(IReportConstants.BORDER);
	rowsFontsVO[4].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

	StyleVO[] normalFontRightAlign = new StyleVO[2];
	normalFontRightAlign[0] = new StyleVO();
	normalFontRightAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
	normalFontRightAlign[0].setStyleValue("10");
	normalFontRightAlign[1] = new StyleVO();
	normalFontRightAlign[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
	normalFontRightAlign[1].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

	StyleVO[] boldAndBigFontCenterAlign = new StyleVO[3];
	boldAndBigFontCenterAlign[0] = new StyleVO();
	boldAndBigFontCenterAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
	boldAndBigFontCenterAlign[0].setStyleValue("14");
	boldAndBigFontCenterAlign[1] = new StyleVO();
	boldAndBigFontCenterAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
	boldAndBigFontCenterAlign[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
	boldAndBigFontCenterAlign[2] = new StyleVO();
	boldAndBigFontCenterAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
	boldAndBigFontCenterAlign[2].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);

	StyleVO[] boldFontLeftAlign = new StyleVO[3];
	boldFontLeftAlign[0] = new StyleVO();
	boldFontLeftAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
	boldFontLeftAlign[0].setStyleValue("10");
	boldFontLeftAlign[1] = new StyleVO();
	boldFontLeftAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
	boldFontLeftAlign[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
	boldFontLeftAlign[2] = new StyleVO();
	boldFontLeftAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
	boldFontLeftAlign[2].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

	StyleVO[] boldFontRightAlign = new StyleVO[3];
	boldFontRightAlign[0] = new StyleVO();
	boldFontRightAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
	boldFontRightAlign[0].setStyleValue("10");
	boldFontRightAlign[1] = new StyleVO();
	boldFontRightAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
	boldFontRightAlign[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
	boldFontRightAlign[2] = new StyleVO();
	boldFontRightAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
	boldFontRightAlign[2].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

	StyleVO[] noBorder = new StyleVO[1];
	noBorder[0] = new StyleVO();
	noBorder[0].setStyleId(IReportConstants.BORDER);
	noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
	
	int urlFin=Integer.parseInt(request.getParameter("FinYear"));
	String urlTr=request.getParameter("treasury");
	String urlDDO=request.getParameter("ddoCode");

	if (report.getReportCode().equals("8000121")) {
		
		DateFormat dateFormat11 = new SimpleDateFormat("dd/MM/yyyy");
		Date date1 = new Date();
		Date sqlDate1 = new java.util.Date(date1.getTime());
		String curDate1 =dateFormat11.format(date1);	
		
		List lheader = null;		
		StringBuffer lSheader = new StringBuffer();
		lSheader= new StringBuffer("SELECT FIN_YEAR_DESC FROM SGVC_FIN_YEAR_MST where FIN_YEAR_ID="+urlFin);
		psmt = con.prepareStatement(lSheader.toString() );
		rs = psmt.executeQuery();	
		String  finYearHeader=null;
		while(rs.next())
		{
			finYearHeader=rs.getString(1);
		}
		String additionalHeader=null;
		
		List lLstFinal = null;
		int urlFinYearId=Integer.parseInt(request.getParameter("FinYear"));
		String urlTresury=request.getParameter("treasury").toString();
		String urlDDOCode=request.getParameter("ddoCode").toString();
	
		if(urlDDOCode.equalsIgnoreCase("-1") )
		{		
			 additionalHeader = "<b><center><font size=\"3px\"> "
					+ "Missing Credits(Form 2 entries) as on  "+curDate1 +" for year "+finYearHeader
					+ "</font></center><br/><font size=\"2px\"> "
					+ "Tresury Code:-"+urlTr
					+ "</font></center></b>";
			 lLstFinal=getTresuryWiseEmp(urlFinYearId,urlTresury);
		}
		else
		{	
			
			 additionalHeader = "<b><center><font size=\"3px\"> "
					+ "Missing Credits(Form 2 entries) as on  "+curDate1 +" for year "+finYearHeader
					+ "</font></center><br/><font size=\"2px\"> "
					+ "Tresury Code:-"+urlTr+"<br/>"+ "DDO Code  :-"+urlDDO
					+ "</font></center></b>";
			 lLstFinal=getTresuryAndDDoWiseEmp(urlFinYearId,urlTresury,urlDDOCode);	
		}
		
		
		report.setAdditionalHeader(additionalHeader);

		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		List lArrReportData = null;
		LoginDetails lObjLoginVO = null;
		String gStrLocCode = null;
		Long gLngLangId = null;
		Long gLngPostId = null;
	
		lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);			
		lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
		gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
		gLngLangId = lObjLoginVO.getLangId();
		gLngPostId = lObjLoginVO.getLoggedInPost().getPostId();
	
	    lStrForthLastLineInTable = new StringBuffer("Sr.no");
	    StringBuffer str=new StringBuffer("Employee Name");
	    StringBuffer str1=new StringBuffer("Employee ID");
	    StringBuffer str2=new StringBuffer("Designation");
	    StringBuffer str3=new StringBuffer("Date of Joining ");
	    StringBuffer str4=new StringBuffer("DDO Code ");
	    StringBuffer str5=new StringBuffer("Missing Month ");
		int counter=1;
		
		td = new ArrayList();
		td.add("<B><center><font size=\"2px\" >"+new StyledData(lStrForthLastLineInTable, rowsFontsVO)+"</B></center></font>");
		td.add("<B><center><font size=\"2px\" >"+new StyledData(str, rowsFontsVO)+"</B></center></font>");
		td.add("<B><center><font size=\"2px\" >"+new StyledData(str1, rowsFontsVO)+"</B></center></font>");
		td.add("<B><center><font size=\"2px\" >"+new StyledData(str2, rowsFontsVO)+"</B></center></font>");
		td.add("<B><center><font size=\"2px\" >"+new StyledData(str3, rowsFontsVO)+"</B></center></font>");
		td.add("<B><center><font size=\"2px\" >"+new StyledData(str5, rowsFontsVO)+"</B></center></font>");
		
		dataListForTableWithHiddenColumns.add(td);

		Iterator IT = lLstFinal.iterator();
		
		while (IT.hasNext()) 
		{			
		td = new ArrayList();
		ArrayList rowlist=new ArrayList(); 
		Object[] lObj = (Object[]) IT.next();
		String Missing_Month="";
		td.add(counter);
		if (lObj[1] != null) 
		{
			td.add(new StyledData(lObj[1].toString(), rowsFontsVO));
		} 
		else 
		{
			td.add("");
		}
		if (lObj[6] != null) 
		{
			td.add(new StyledData(lObj[6].toString(), rowsFontsVO));
		} 
		else 
		{
			td.add("");
		}
		if (lObj[2] != null) 
		{	
			td.add(new StyledData(lObj[2].toString(), rowsFontsVO));
		} 
		else 
		{
			td.add("");
		}
		if (lObj[3] != null)
		{	
			td.add(new StyledData(lObj[3].toString(), rowsFontsVO));
		}
		else 
		{
			td.add("");
		}
		int joinMonth=(Integer)lObj[5]; 	
		
		List lLstMissing = null;		
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT MONTH(min(STARTDATE)),MONTH(max(ENDDATE)) FROM TRN_DCPS_CONTRIBUTION where DCPS_EMP_ID ="+lObj[0]+" and FIN_YEAR_ID="+urlFinYearId);
		SQLQuery Query =  lObjSession.createSQLQuery(lSBQuery.toString());	
		lLstMissing = Query.list();

		Iterator I = lLstMissing.iterator();
		int spaceCount=0;			
		
		while(I.hasNext())
		{
		Object[] l = (Object[]) I.next();
		int minYear=0;
		StringBuffer bls = new StringBuffer(  );
		bls = new StringBuffer("SELECT sf.FIN_YEAR_ID,YEAR(me.DOJ) FROM MST_DCPS_EMP me INNER JOIN SGVC_FIN_YEAR_MST sf ON me.DOJ BETWEEN  sf.FROM_DATE  AND sf.TO_DATE where me.DCPS_EMP_ID="+lObj[0]);
		psmt = con.prepareStatement(bls.toString() );
	    rs = psmt.executeQuery();		            
	    int dojFinYear=0;
	    int dojYear=0; 
	    
	    while(rs.next())
	    {		            	
	    	dojFinYear =rs.getInt(1);
	    	dojYear=rs.getInt(2);
	    }
		int startMonth=(Integer)l[0];
		int endMonth=(Integer)l[1]; 
		
		int dummyStartMonth=startMonth;
		int dummyjoinMonth=joinMonth;
		int dummyEndMonth=endMonth;
		
		//startmonth
		if(startMonth==1)
		{
			dummyStartMonth=13;
		}else if(startMonth==2)
		{
			dummyStartMonth=14;
		}else if(startMonth==3)
		{
			dummyStartMonth=15;
		}
		
		//endmonth
		if(endMonth==1)
		{
			dummyEndMonth=13;
		}else if(endMonth==2)
		{
			dummyEndMonth=14;
		}else if(endMonth==3)
		{
			dummyEndMonth=15;
		}
		
		//endmonth
		if(joinMonth==1)
		{
			dummyjoinMonth=13;
		}else if(joinMonth==2)
		{
			dummyjoinMonth=14;
		}else if(joinMonth==3)
		{
			dummyjoinMonth=15;
		}
		
		ArrayList listmiss=null;
		StringBuffer lSBQu = new StringBuffer(  );
		lSBQu.append("SELECT MONTH(td.STARTDATE) FROM MST_DCPS_EMP me ");
		lSBQu.append("INNER JOIN TRN_DCPS_CONTRIBUTION td  ");
		lSBQu.append("ON me.DCPS_EMP_ID=td.DCPS_EMP_ID ");
		lSBQu.append("INNER JOIN SGVC_FIN_YEAR_MST sf ");
		lSBQu.append("ON sf.FIN_YEAR_ID=td.FIN_YEAR_ID ");
		lSBQu.append("where me.DCPS_EMP_ID="+lObj[0] );
		lSBQu.append(" and td.FIN_YEAR_ID="+urlFinYearId+" and me.DOJ<td.ENDDATE and me.DOJ<td.STARTDATE  ");
		lSBQu.append("AND td.STARTDATE BETWEEN sf.FROM_DATE AND sf.TO_DATE  ORDER BY td.STARTDATE");
	
		SQLQuery Qu =  lObjSession.createSQLQuery(lSBQu.toString());
		listmiss = (ArrayList) Qu.list();
		
		if(urlFinYearId==dojFinYear)
	    {
		StringBuffer lsb = new StringBuffer(  );
		lsb = new StringBuffer("SELECT MIN(YEAR(td.ENDDATE)) FROM TRN_DCPS_CONTRIBUTION td,SGVC_FIN_YEAR_MST sf where td.DCPS_EMP_ID ="+lObj[0]+" and sf.FIN_YEAR_ID="+urlFinYearId+" AND  td.STARTDATE BETWEEN sf.FROM_DATE AND sf.TO_DATE ");
		psmt = con.prepareStatement( lsb.toString() );
		rs = psmt.executeQuery();
		while(rs.next())
		{			            	
			minYear =rs.getInt(1);
		}
		for(int tw=0;dummyjoinMonth<dummyStartMonth;dummyjoinMonth++)
		{
			Iterator t=listmiss.iterator();
			long r =Long.parseLong(t.next()+"");
		    int	count=0;
		  
		for(int j=0;t.hasNext();j++)
		{
		count++;	
		if(dummyjoinMonth==13)
		{
			joinMonth=1;
			minYear=minYear+1;//year increment
		}
		else if(dummyjoinMonth==14)
		{
			joinMonth=2;
		}
		else if(dummyjoinMonth==15)
		{
			joinMonth=3;
		}
		if(joinMonth==r)
		{
			r =Long.parseLong(t.next()+"");
			break;
		}
	    else 
		{
			spaceCount++;
			if(spaceCount==5)
			{
			spaceCount=0;
			if(joinMonth<10)
			{
			 Missing_Month=Missing_Month+"0"+joinMonth+"/"+minYear+"<br/>";
			}
			else
			{
				Missing_Month=Missing_Month+joinMonth+"/"+minYear+"<br/>";	 
			}
			}
			else
			{
			if(joinMonth<10)
			{
				Missing_Month=Missing_Month+"0"+joinMonth+"/"+minYear+"&nbsp;&nbsp;&nbsp;&nbsp;";	
			}
			else
			{
				Missing_Month=Missing_Month+joinMonth+"/"+minYear+"&nbsp;&nbsp;&nbsp;&nbsp;";	
			} 
			}
	 
			r =Long.parseLong(t.next()+"");
			break;
			
		}				  
	   }joinMonth++;
	  }
		Long a=0l;
		int size=listmiss.size();
		for(int w=0;dummyStartMonth<dummyEndMonth;dummyStartMonth++)
		{
		if(dummyStartMonth==13)
		{
			startMonth=1;
		}else if(dummyStartMonth==14)
		{
			startMonth=2;
		}else if(dummyStartMonth==15)
		{
			startMonth=3;					
		}
		for(int ii=0;ii<size;ii++)
		{					
			a= Long.parseLong(listmiss.get(w)+"");
		if(startMonth ==a)
		{
			startMonth++;
			break;
		}
		else
		{
		 spaceCount++;
		 if(spaceCount==5)
		 {
			spaceCount=0;
			if(startMonth<10)
			{
			Missing_Month=Missing_Month+"0"+startMonth+"/"+minYear+"<br/>";
			}
			else
			{
			 Missing_Month=Missing_Month+startMonth+"/"+minYear+"<br/>";	 
			}
		 }
		 else
		 {
			 if(startMonth<10)
			 {
			 Missing_Month=Missing_Month+"0"+startMonth+"/"+minYear+"&nbsp;&nbsp;&nbsp;&nbsp;";	
			 }
			 else
			 {
			 Missing_Month=Missing_Month+startMonth+"/"+minYear+"&nbsp;&nbsp;&nbsp;&nbsp;";	
			 } 
		   }
		     startMonth++;
		 }
		if(startMonth==13)
		{
			startMonth=1;
			minYear++;
		}
		else if(startMonth==14)
		{
			startMonth=2;
		}
		else if(startMonth==15)
		{
			startMonth=3;
		}
		}w++;
		if(size>w){continue;}
		else{break;}
		}
	    
	    }
	    
		else
		{
		StringBuffer lsb = new StringBuffer(  );
		int min=0;
		lsb = new StringBuffer("SELECT MIN(YEAR(td.ENDDATE)) FROM TRN_DCPS_CONTRIBUTION td,SGVC_FIN_YEAR_MST sf where td.DCPS_EMP_ID ="+lObj[0]+" and sf.FIN_YEAR_ID="+urlFinYearId+" AND  td.STARTDATE BETWEEN sf.FROM_DATE AND sf.TO_DATE");
		psmt = con.prepareStatement( lsb.toString() );
		rs = psmt.executeQuery();
		while(rs.next())
		{			            	
			min =rs.getInt(1);
		}
		Long a=0l;
		int size=listmiss.size();
		for(int w=0;dummyStartMonth<dummyEndMonth;dummyStartMonth++)
		{
		if(dummyStartMonth==13)
		{
			startMonth=1;
		}else if(dummyStartMonth==14)
		{
			startMonth=2;
		}else if(dummyStartMonth==15)
		{
			startMonth=3;					
		}
		a= Long.parseLong(listmiss.get(w)+"");
		for(int ii=0;ii<size;ii++)
		{	
		if(startMonth ==a)
		{
			startMonth++;
			break;
		}else
		{
			 spaceCount++;
			 if(spaceCount==5)
			 {
			 spaceCount=0;
			 if(startMonth<10)
			 {
			 Missing_Month=Missing_Month+"0"+startMonth+"/"+min+"<br/>";
			 }
			 else
			 {
			 Missing_Month=Missing_Month+startMonth+"/"+min+"<br/>";	 
			 }
			 }
			 else
			 {
			 if(startMonth<10)
			 {
			 Missing_Month=Missing_Month+"0"+startMonth+"/"+min+"&nbsp;&nbsp;&nbsp;&nbsp;";	
			 }
			 else
			 {
			 Missing_Month=Missing_Month+startMonth+"/"+min+"&nbsp;&nbsp;&nbsp;&nbsp;";	
			 } 
			 }
			startMonth++;
		}
		if(startMonth==13)
		{
			startMonth=1;
			min++;
		}
		else if(startMonth==14)
		{
			startMonth=2;
		}
		else if(startMonth==15)
		{
			startMonth=3;
		}
		}w++;
		if(size>w){continue;}
		else{break;}
		}
	   }
	  }
		td.add(new StyledData(Missing_Month, rowsFontsVO));
		dataListForTableWithHiddenColumns.add(td);
		counter++;
	  }

		td2 = new TabularData(dataListForTableWithHiddenColumns);
		(td2).setRelatedReport(RptVoForHiddenColumns);
		rowList.add(td2);
		dataList.add(rowList);
		} 
		}catch (Exception e) {
		e.printStackTrace();
		gLogger.error("Exception :" + e, e);
		} finally {
		try {
			if (lStmt != null) {
				lStmt.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (con != null) {
				con.close();
			}
			lStmt = null;
			rs = null;
			con = null;
		} catch (Exception e1) {
			e1.printStackTrace();
			gLogger.error("Exception :" + e1, e1);
			}
		}
		return dataList;
	}
	
		public List getTresuryAndDDoWiseEmp(int urlFinYearId,String urlTresury,String urlDDOCode) {
			
			List<Object> lListEmp = new ArrayList<Object>();
			List lLstResult=null;
			try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuilder lSBQuery = new StringBuilder();
			
			lSBQuery.append("SELECT distinct(td.DCPS_EMP_ID),me.EMP_NAME,od.DSGN_NAME,SUBSTR(me.DOJ,1,10),me.DDO_CODE,MONTH(me.DOJ),me.DCPS_ID FROM MST_DCPS_EMP me ");
			lSBQuery.append("INNER JOIN ORG_DESIGNATION_MST od ");
			lSBQuery.append("ON od.DSGN_ID=me.DESIGNATION ");
			lSBQuery.append("INNER JOIN TRN_DCPS_CONTRIBUTION td ");
			lSBQuery.append("ON td.DCPS_EMP_ID=me.DCPS_EMP_ID ");
			lSBQuery.append("INNER JOIN SGVC_FIN_YEAR_MST sf ");
			lSBQuery.append("ON sf.FIN_YEAR_ID=td.FIN_YEAR_ID ");
			lSBQuery.append("where td.FIN_YEAR_ID="+urlFinYearId+" AND me.DDO_CODE like '"+urlTresury+"%' AND me.DDO_CODE="+urlDDOCode+" AND td.STARTDATE BETWEEN sf.FROM_DATE AND sf.TO_DATE  ");

			System.out.println(""+lSBQuery);
			SQLQuery Query = lObjSession.createSQLQuery(lSBQuery.toString());
			
			lLstResult = Query.list();
			System.out.println(""+lLstResult);
			} catch (Exception e) {
				gLogger.error("Error is : " + e, e);
			}
			
				return lLstResult;
			}

		
		public List getTresuryWiseEmp(int urlFinYearId,String urlTresury) {
		
			List<Object> lListEmp = new ArrayList<Object>();
			List lLstResult=null;
			try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuilder lSBQuery = new StringBuilder();
			
			lSBQuery.append("SELECT distinct(td.DCPS_EMP_ID),me.EMP_NAME,od.DSGN_NAME,SUBSTR(me.DOJ,1,10),me.DDO_CODE,MONTH(me.DOJ),me.DCPS_ID FROM MST_DCPS_EMP me ");
			lSBQuery.append("INNER JOIN ORG_DESIGNATION_MST od ");
			lSBQuery.append("ON od.DSGN_ID=me.DESIGNATION ");
			lSBQuery.append("INNER JOIN TRN_DCPS_CONTRIBUTION td ");
			lSBQuery.append("ON td.DCPS_EMP_ID=me.DCPS_EMP_ID ");
			lSBQuery.append("INNER JOIN SGVC_FIN_YEAR_MST sf ");
			lSBQuery.append("ON sf.FIN_YEAR_ID=td.FIN_YEAR_ID ");
			lSBQuery.append("where td.FIN_YEAR_ID="+urlFinYearId+" AND me.DDO_CODE like '"+urlTresury+"%'  AND td.STARTDATE BETWEEN sf.FROM_DATE AND sf.TO_DATE  ");

			System.out.println(""+lSBQuery);
			SQLQuery Query = lObjSession.createSQLQuery(lSBQuery.toString());
			
			lLstResult = Query.list();
			System.out.println(""+lLstResult);
			} catch (Exception e) {
				gLogger.error("Error is : " + e, e);
			}
			
				return lLstResult;
			}
			
}
