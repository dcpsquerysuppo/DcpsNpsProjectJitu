package com.tcs.sgv.dcps.report;

import java.math.BigInteger;
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
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class MissingCreditReportByName extends DefaultReportDataFinder implements ReportDataFinder{
	private static final Logger gLogger = Logger.getLogger(MissingCreditReportByName.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");
	 
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	private HttpServletRequest request = null;
	private  StyleVO[] selfCloseVO=null; 
	
	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

	String langId = report.getLangId();
	String locId = report.getLocId();
	Connection con = null;
	criteria.getClass();
	Statement smt = null;
	PreparedStatement psmt= null;
	ResultSet rs = null;
	String empId = null;
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
	
	String urlDcpsEmpId=request.getParameter("dcpsEmpId");
	
	
	ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
	SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
	con = lObjSessionFactory.getCurrentSession().connection();
	smt = con.createStatement();
	
	StringBuffer lStrForthLastLineInTable = null;	
	ArrayList dataListForTableWithHiddenColumns = new ArrayList();
	Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
	Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
	Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
	Long locationId = (Long) loginDetail.get("locationId");

	StringBuffer sql = new StringBuffer();
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
	 

	if (report.getReportCode().equals("8000122")) 
		{
		
		DateFormat dateFormat11 = new SimpleDateFormat("dd/MM/yyyy");
		Date date1 = new Date();
		Date sqlDate1 = new java.util.Date(date1.getTime());
		String curDate1 =dateFormat11.format(date1);	
		
		String additionalHeader = "<b><center><font size=\"3px\"> "
				+ "Missing Credits(Form 2 entries) as on  "+curDate1
				+ "</font></center></b>";
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

	DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
	OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, serviceLocator.getSessionFactory());
	DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
	
	List lLstFinal = null;
	lLstFinal=getEmpForMissingMonth(urlDcpsEmpId);
	Iterator IT = lLstFinal.iterator();
	
	BigInteger stype=null;
	BigInteger [] month=null; 
		
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
	} else {
		td.add("");
	}
	if (lObj[6] != null) 
	{
		td.add(new StyledData(lObj[6].toString(), rowsFontsVO));
	} else {
		td.add("");
	}
	if (lObj[2] != null) 
	{	
		td.add(new StyledData(lObj[2].toString(), rowsFontsVO));
	} else {
		td.add("");
	}
	if (lObj[3] != null)
	{	
		td.add(new StyledData(lObj[3].toString(), rowsFontsVO));
	} else {
		td.add("");
	}

	List lLstMissing = null;		
	StringBuilder lSBQuery = new StringBuilder();	
	lSBQuery.append("SELECT MONTH(min(STARTDATE)),YEAR(MIN(STARTDATE)),MIN(FIN_YEAR_ID) FROM TRN_DCPS_CONTRIBUTION where DCPS_EMP_ID ="+lObj[0]);	
	SQLQuery Query =  lObjSession.createSQLQuery(lSBQuery.toString());
	lLstMissing = Query.list();
	Iterator I = lLstMissing.iterator();

	Object[] l = (Object[]) I.next();
	int startMonth=(Integer)l[0]; 
	int startYear=(Integer)l[1];
	long startFinYearId=Long.parseLong(l[2]+"");
	
	Date doj=(Date) lObj[3];
	int joinMonth=(Integer)lObj[5];
	int dojYear=(Integer)lObj[7];	
	
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	Date date = new Date();
	Date sqlDate = new java.util.Date(date.getTime());
	String curDate =dateFormat.format(date);	
	String[] part=curDate.split("/");
	int endMonth=Integer.parseInt(part[1]);
	int CurrentFinYear= Integer.parseInt(part[2]);
	int currFinYearId=0;
						
	List lLstRemainMissing = null;		
	StringBuilder lQuery = new StringBuilder();	
	lQuery.append("SELECT MONTH(MAX(STARTDATE)),YEAR(MAX(STARTDATE)),MIN(FIN_YEAR_ID) FROM TRN_DCPS_CONTRIBUTION where DCPS_EMP_ID ="+lObj[0]);	
	SQLQuery query =  lObjSession.createSQLQuery(lQuery.toString());
	lLstRemainMissing = query.list();
	Iterator ti = lLstRemainMissing.iterator();
	Object[] Ob = (Object[]) ti.next();
	
	int maxLastMonth=(Integer)Ob[0]+1;
	int maxLastYear=(Integer)Ob[1];
	
	String maxm=Ob[0].toString();
	String maxy=Ob[1].toString();
	
	ArrayList listmiss=null;
	StringBuffer lSBQu = new StringBuffer(  );	
	lSBQu.append("SELECT MONTH(td.STARTDATE),YEAR(td.STARTDATE) FROM MST_DCPS_EMP me ");
	lSBQu.append("INNER JOIN TRN_DCPS_CONTRIBUTION td  ");
	lSBQu.append("ON me.DCPS_EMP_ID=td.DCPS_EMP_ID ");
	lSBQu.append("INNER JOIN SGVC_FIN_YEAR_MST sf ");
	lSBQu.append("ON sf.FIN_YEAR_ID=td.FIN_YEAR_ID ");
	lSBQu.append("where me.DCPS_EMP_ID="+lObj[0] );
	lSBQu.append(" and me.DOJ<td.ENDDATE and me.DOJ<td.STARTDATE  ");
	lSBQu.append("AND td.STARTDATE BETWEEN sf.FROM_DATE AND sf.TO_DATE  ORDER BY td.STARTDATE");	
	SQLQuery Qu =  lObjSession.createSQLQuery(lSBQu.toString());	
	listmiss = (ArrayList) Qu.list();
	Iterator u=listmiss.iterator();

	int diff[]=diff(joinMonth+"/"+dojYear,endMonth+"/"+CurrentFinYear);
	
	int monthCount=diff[1]+diff[0];
	int spaceCount=0;
	while(u.hasNext())
	{
		Object[] ob = (Object[]) u.next();
		if(joinMonth==13)
		{	
			dojYear++;
			joinMonth=1;
		}
		else if(joinMonth==14)
		{
			joinMonth=2;
		}
		else if(joinMonth==15)
		{
			joinMonth=3;
		}	
		
		for(int countmonth=0;countmonth<monthCount;countmonth++)
		{			
		if(joinMonth==(Integer)ob[0] && dojYear ==(Integer)ob[1])
		{
			 joinMonth++;
			 break;
		 }
		 else 
		 {  
			 spaceCount++;
			 if(spaceCount==11)
			 {
				 spaceCount=0;
			if(joinMonth<10)
			{ 
			 Missing_Month=Missing_Month+"0"+joinMonth+"/"+dojYear+"<br/>";
			}
			else
			{
		    Missing_Month=Missing_Month+joinMonth+"/"+dojYear+"<br/>";
			}
			 }
			 else
			 {
				if(joinMonth<10)	 
				 Missing_Month=Missing_Month+"0"+joinMonth+"/"+dojYear+"&nbsp;&nbsp;&nbsp;&nbsp;";	
			
				else
				 Missing_Month=Missing_Month+joinMonth+"/"+dojYear+"&nbsp;&nbsp;&nbsp;&nbsp;";	
				
			 }	
			 joinMonth++;				 
		 }
		if(joinMonth==13)
		{	dojYear++;
		joinMonth=1;
		}
		else if(joinMonth==14)
		{
			joinMonth=2;
		}
		else if(joinMonth==15)
		{
			joinMonth=3;
		}	
      }	
	}
	int dif[]=diff(maxm+"/"+maxy,endMonth+"/"+CurrentFinYear);
	int monthCoun=dif[1]+dif[0];
	for(int miss=1;miss<monthCoun;miss++,maxLastMonth++)
	{
		if(maxLastMonth==13)
			{	
			maxLastYear++;	
			dojYear++;
			maxLastMonth=1;
			}
			else if(maxLastMonth==14)
			{
				maxLastMonth=2;
			}
			else if(maxLastMonth==15)
			{
				maxLastMonth=3;
			}
		    spaceCount++;
		    if(spaceCount==11)
		    {
		    spaceCount=0;
		    if(maxLastMonth<10)
	    	{   
			Missing_Month=Missing_Month+"0"+maxLastMonth+"/"+dojYear+"<br/>";
	    	}
		    else
		    {
		    	Missing_Month=Missing_Month+maxLastMonth+"/"+dojYear+"<br/>";
		    }
		    }
		    else
			 {
		    	if(maxLastMonth<10)
		    	{
				 Missing_Month=Missing_Month+"0"+maxLastMonth+"/"+dojYear+"&nbsp;&nbsp;&nbsp;&nbsp;";				 
		    	}
		    	else
		    	{
					 Missing_Month=Missing_Month+maxLastMonth+"/"+dojYear+"&nbsp;&nbsp;&nbsp;&nbsp;";			 		    		
		    	}
			 }
	}
		td.add(new StyledData(Missing_Month, rowsFontsVO));
		dataListForTableWithHiddenColumns.add(td);
		td2 = new TabularData(dataListForTableWithHiddenColumns);
		(td2).setRelatedReport(RptVoForHiddenColumns);
		rowList.add(td2);
		dataList.add(rowList);
	 }
	}
	}catch (Exception e) {
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

	
		public List getEmpForMissingMonth(String urlDcpsEmpId) {
		List<Object> lListEmp = new ArrayList<Object>();
		List lLstResult=null;
		try {
		Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT distinct(td.DCPS_EMP_ID),me.EMP_NAME,od.DSGN_NAME,me.DOJ,me.DDO_CODE,MONTH(me.DOJ),me.DCPS_ID,YEAR(me.DOJ) FROM MST_DCPS_EMP me ");
		lSBQuery.append("INNER JOIN ORG_DESIGNATION_MST od ");
		lSBQuery.append("ON od.DSGN_ID=me.DESIGNATION ");
		lSBQuery.append("INNER JOIN TRN_DCPS_CONTRIBUTION td ");
		lSBQuery.append("ON td.DCPS_EMP_ID=me.DCPS_EMP_ID ");
		lSBQuery.append("INNER JOIN SGVC_FIN_YEAR_MST sf ");
		lSBQuery.append("ON sf.FIN_YEAR_ID=td.FIN_YEAR_ID ");
		lSBQuery.append("where td.DCPS_EMP_ID="+urlDcpsEmpId+" AND td.STARTDATE BETWEEN sf.FROM_DATE AND sf.TO_DATE ");
		SQLQuery Query = lObjSession.createSQLQuery(lSBQuery.toString());
		lLstResult = Query.list();

		} catch (Exception e) {
		gLogger.error("Error is : " + e, e);
		}
	
		return lLstResult;
		}


		public static int[] diff(String d1,String d2)
		{
		int res[]=new int[2];
		String dd1[]=d1.split("/");
		String dd2[]=d2.split("/");
	    int y=Integer.parseInt(dd1[1])-Integer.parseInt(dd2[1]);
	    int m=Integer.parseInt(dd1[0])-Integer.parseInt(dd2[0]);
	    res[1]=Math.abs(y)*12;
	    res[0]=Math.abs(m);
		return res ;
		}
		
	
}
