package com.tcs.sgv.common.dao;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.formula.Ptg;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportParameterVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgPostMst;


public class TreasuryDDOWiseReportDAOImpl extends GenericDaoHibernateImpl 
		implements TreasuryDDOWiseReportDAO,ReportDataFinder {

	public TreasuryDDOWiseReportDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
	}
	
	public TreasuryDDOWiseReportDAOImpl() {
		super(CmnLocationMst.class);
		setSessionFactory(ServiceLocator.getServiceLocator().getSessionFactory());
	}
	private static final Logger glogger = Logger.getLogger(TreasuryDDOWiseReportDAOImpl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private SessionFactory lObjSessionFactory = null;
	Long langId = null;	
	
	public Collection findReportData(ReportVO report, Object criteria) throws ReportException 
	{
		List lArrReportData = new ArrayList();
		try{
			
			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes .get("serviceLocator");	
			
			Map serviceMap = (Map)requestAttributes.get("serviceMap");
			HttpServletRequest request=(HttpServletRequest)serviceMap.get("requestObj");
			//--------GET LOGGED IN DTLS-------------------------------------------------------
			
			Map sessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			LoginDetails loginVO = (LoginDetails)sessionAttributes.get("loginDetails");
			langId=loginVO.getLangId();
					
			OrgPostMst postVO=null;
			postVO=(OrgPostMst)loginVO.getLoggedInPost();
			long lLngLoginPostId=postVO.getPostId();
			String lStrLoginLocCode=postVO.getLocationCode();
			
			
			
		   	if (report.getReportCode().equals("160352")) //PR CR Tracking Report
		 	{	
		   		lArrReportData = getDDOTreasuryWiseReport(report);		   		
		 	}
		   	if (report.getReportCode().equals("160354")) // Login Audit Report - TO Login
		 	{	
		   		lArrReportData = getDDOWiseReport(report);				   		
		 	}
		   	if (report.getReportCode().equals("160356")) // Login Audit Report - TO Login
		 	{	
		   		lArrReportData = getBillNotGeneratedDetailsReport(report);				   		
		 	}
		   
	   }
		catch (Exception e) {			
			glogger.error("Exception in OnlineBugTrackingDAOImpl findReportData :: " + e.getMessage(), e);
			throw new ReportException(e.getMessage());
			
		}
		return lArrReportData;
	}
	
	/*
	 * Method to fill combo of User ID in TO Login in Login Audit Report(220101)
	 */
	public ArrayList<ComboValuesVO> getAllTreasuryLoginName(Hashtable hashtable,String lStrLangId,String lStrLocationcode) throws Exception{
		ArrayList<ComboValuesVO> treasuryNameList = new ArrayList<ComboValuesVO>();
		try{
			
			Session hibSession = getSession();
			
			LoginDetails loginVO = (LoginDetails) hashtable.get("loginDetails");
			long locID = loginVO.getLocation().getLocId();
						
			String lStrQuery = 
				"SELECT um.user_id,um.user_name\n" +
				"FROM org_user_mst um,org_userpost_rlt up,org_post_mst pm\n" + 
				"WHERE um.user_id = up.user_id AND up.post_id = pm.post_id\n" + 
				" AND pm.location_code = "+locID+" AND pm.activate_flag = 1 ORDER BY um.user_name ";
		
					
			Query sqlQuery = hibSession.createSQLQuery(lStrQuery);	
			
						
			List resCash = sqlQuery.list();

			ComboValuesVO vo= null;
			
			for (int i=0;i<resCash.size();i++) 
			{	
				Object[] obj = (Object[])resCash.get(i); 
				vo= new ComboValuesVO(  );
				
				vo.setId(obj[0].toString());
				vo.setDesc(obj[1].toString());
				
				treasuryNameList.add(vo);
			}
		}catch(Exception e){
			e.printStackTrace();
			glogger.error( "Exception in OnlineBugTrackingDAOImpl(getAllTreasuryLoginName)::"+e.getMessage(), e );
		}
		return treasuryNameList;
	}
	
	/*
	 * Parameter Page for Login Audit report ( 220101)
	 */
	private ArrayList getDDOTreasuryWiseReport(ReportVO report )
	{
		ArrayList lArrayList = new ArrayList();
		Date fromDate = null ;
		Date toDate = null;	
		try{
			
	
			String region = (String) report.getParameterValue("region");
	        Integer finYear = Integer.parseInt(report.getParameterValue("finYear").toString());
			String month =  report.getParameterValue("month").toString();
			
			
			lArrayList = getDDOWiseTreasuryReportQuery(region,finYear,month,report);
			report.setParameterValue("month", month.split(":")[1]);
			
			 
		}catch(Exception e){
			e.printStackTrace();
			glogger.error("Exception in OnlineBugTrackingDAOImpl(getLoginLogoutReportPara) ::"+e.getMessage());
		}
		return lArrayList;
	}
	private ArrayList getDDOWiseReport(ReportVO report )
	{
		ArrayList lArrayList = new ArrayList();
		Date fromDate = null ;
		Date toDate = null;	
		try{
			
		
			String region = (String) report.getParameterValue("region");
			String ddoOffice = report.getParameterValue("ddoOffice").toString();
			Integer finYear = Integer.parseInt(report.getParameterValue("finYear").toString());
			String month =  report.getParameterValue("month").toString();
			
			
			lArrayList = getDDOWiseReportQuery(region,ddoOffice,finYear,month,report);
			report.setParameterValue("month", month.split(":")[1]);
			 
		}catch(Exception e){
			e.printStackTrace();
			glogger.error("Exception in OnlineBugTrackingDAOImpl(getLoginLogoutReportPara) ::"+e.getMessage());
		}
		return lArrayList;
	}
	
	private ArrayList getBillNotGeneratedDetailsReport(ReportVO report )
	{
		ArrayList lArrayList = new ArrayList();
		Date fromDate = null ;
		Date toDate = null;	
		try{
			
		
			String ddoCode = (String) report.getParameterValue("ddoCode");
			Integer finYear = Integer.parseInt(report.getParameterValue("finYear").toString());
			String month = report.getParameterValue("month").toString();
			
			lArrayList = getBillNotGeneratedDetailsQuery(ddoCode,finYear,month);
			report.setParameterValue("month", month.split(":")[1]);
			 
		}catch(Exception e){
			e.printStackTrace();
			glogger.error("Exception in OnlineBugTrackingDAOImpl(getBillNotGeneratedDetailsReport) ::"+e.getMessage());
		}
		return lArrayList;
	}
	
	public ArrayList getDDOWiseTreasuryReportQuery(String region,Integer finYear,String month,ReportVO report)
	{
	
			ArrayList lArrOutrLst = new ArrayList();
			ArrayList linnerList = null ;
			Session hibsession = getReadOnlySession();
			StringBuilder lsb = new StringBuilder();
			try 
			{				 
				lsb.append(" Select Left(Trea_Name.Location_Code,1) Region_Code,Case When Left(Trea_Name.Location_Code,1)=1 Then 'Konkan' ");
				lsb.append(" when left(Trea_Name.Location_Code,1)=2 then 'Pune' ");
				lsb.append(" when left(Trea_Name.Location_Code,1)=3 then 'Aurangabad' ");
				lsb.append(" when left(Trea_Name.Location_Code,1)=4 then 'Nagpur' ");
				lsb.append(" when left(Trea_Name.Location_Code,1)=5 then 'Nasik' ");
				lsb.append(" When Left(Trea_Name.Location_Code,1)=6 Then 'Amravati' ");
				lsb.append(" when left(Trea_Name.Location_Code,1)=7 then 'Mumbai' else '' End Region,Trea_Name.Location_Code  ,Trea_Name.Loc_Name ,Count(distinct A.Ddo_Code) ,Count(D.Bill_Group_Id) ,Count(F.Bill_No)  ,Count(D.Bill_Group_Id) - Count(F.Bill_No)  ");
				lsb.append(" From Org_Ddo_Mst A "); 
				lsb.append(" Inner Join Cmn_Location_Mst B On B.Location_Code=A.Location_Code ");
				lsb.append(" Inner Join Mst_Dcps_Bill_Group D On D.Ddo_Code=A.Ddo_Code ");
				lsb.append(" Inner Join Rlt_Ddo_Org Trea On Trea.Ddo_Code=A.Ddo_Code ");
				lsb.append(" Inner Join Cmn_Location_Mst Trea_Name On Trea_Name.Location_Code=Trea.Location_Code  ");
				lsb.append(" left outer join Paybill_Head_Mpg F On F.BILL_NO=D.Bill_Group_Id and F.Paybill_Month=:month And F.Paybill_Year=:finYear And F.Approve_Flag In (0,1) "); 
				if(!region.equals("Region"))
				{
					logger.error(" region in first if "  +region);
					lsb.append(" where left(Trea_Name.Location_Code,1)=:region ");
				}
				lsb.append(" GROUP BY Trea_Name.Location_Code, Trea_Name.Loc_Name ");
				
				
				 SQLQuery lQuery = hibsession.createSQLQuery(lsb.toString());				  
				 lQuery.setParameter("month", month.split(":")[0]);
				 lQuery.setParameter("finYear", finYear);
				 if(!region.equals("Region"))
				{
					 logger.error(" region in second if "  +region);
				 lQuery.setParameter("region", region);
				}
				
				 								 
				  List<Object[]> resList = lQuery.list();
				  
				  int i = 1;
				  for (Object[] cols : resList) 
				  {
					  linnerList = new ArrayList();
					 
					  
					  linnerList.add(cols[1]!=null?cols[1].toString():"");
					  linnerList.add(cols[3]!=null?cols[3].toString():"");
					  linnerList.add(cols[4]!=null?cols[4].toString():"");
					  linnerList.add(cols[5]!=null?cols[5].toString():"");
					  linnerList.add(cols[6]!=null?cols[6].toString():"");
					  linnerList.add(cols[7]!=null?cols[7].toString():"");
					  
					
						  linnerList.add("");
					      linnerList.add(cols[1]!=null?cols[1].toString():"");
						  linnerList.add("");
			              linnerList.add((cols[2]!=null?cols[2].toString():"")+":"+(cols[3]!=null?cols[3].toString():""));			              
			              linnerList.add(finYear);
			              linnerList.add(month);
					 
					  lArrOutrLst.add(linnerList);
				  }
			}
			catch (Exception e) {
				
				e.printStackTrace();
			}
			  
			return lArrOutrLst;
		}
	
	public ArrayList getDDOWiseReportQuery(String region,String ddoOffice,Integer finYear,String month,ReportVO report)
	{
	
			ArrayList lArrOutrLst = new ArrayList();
			ArrayList linnerList = null ;
			Session hibsession = getReadOnlySession();
			StringBuilder lsb = new StringBuilder();
			try
			{				 
				lsb
					.append("Select A.Ddo_Code ,a.DDO_NAME,Count(D.Bill_Group_Id) Total_Bills,Count(F.Bill_No) Generated ,Count(D.Bill_Group_Id) - Count(F.Bill_No) Not_Generated,'temp'  ");
			lsb.append("From Org_Ddo_Mst A  ");
			lsb
					.append("Inner Join Cmn_Location_Mst B On B.Location_Code=A.Location_Code ");
			lsb
					.append("inner join Cmn_Location_Mst Field_Dept on Field_Dept.location_code=b.parent_loc_id ");
			lsb
					.append("Inner Join Mst_Dcps_Bill_Group D On D.Ddo_Code=A.Ddo_Code ");
			lsb
					.append("Inner Join Rlt_Ddo_Org Trea On Trea.Ddo_Code=A.Ddo_Code ");
			lsb
					.append("Inner Join Cmn_Location_Mst Trea_Name On Trea_Name.Location_Code=Trea.Location_Code ");
			lsb
					.append("Inner Join Mst_Dcps_Ddo_Office E On E.Loc_Id=A.Location_Code ");
			lsb
					.append("inner join CMN_DISTRICT_MST f on f.DISTRICT_ID=e.DISTRICT ");
			lsb
					.append("Left Outer Join Paybill_Head_Mpg F On F.Bill_No=D.Bill_Group_Id And F.Paybill_Month=:month And F.Paybill_Year=:finYear And F.Approve_Flag In (0,1)  ");
			lsb.append("Where E.Ddo_Office='Yes' ");
			lsb.append(" and Trea_Name.Location_Code=:locationCode  ");
			lsb
					.append("GROUP BY Trea_Name.Location_Code, Trea_Name.Loc_Name,Field_Dept.Loc_Name, A.Ddo_Code,a.DDO_NAME ");
				

				
				
				 SQLQuery lQuery = hibsession.createSQLQuery(lsb.toString());				  
				 lQuery.setParameter("month", month.split(":")[0]);
				 lQuery.setParameter("finYear", finYear);
				 lQuery.setParameter("locationCode", ddoOffice.split(":")[0]);
				
				 				
				  
				  List<Object[]> resList = lQuery.list();
				  int i = 1;
				  for (Object[] cols : resList) 
				  {
					  linnerList = new ArrayList();
					 
					  
					  linnerList.add(region);
					  linnerList.add(ddoOffice.split(":")[1]);
					  linnerList.add(cols[0]!=null?cols[0].toString():"");
					  linnerList.add(cols[1]!=null?cols[1].toString():"");
					  linnerList.add(cols[2]!=null?cols[2].toString():"");
					  linnerList.add(cols[3]!=null?cols[3].toString():"");
					  linnerList.add(cols[4]!=null?cols[4].toString():"");
					  linnerList.add(cols[5]!=null?cols[5].toString():"");					  
					  linnerList.add(month);
					  linnerList.add(finYear);
					  lArrOutrLst.add(linnerList);
				  }
			}
			catch (Exception e) {
				
				e.printStackTrace();
			}
			  
			return lArrOutrLst;
		}
	
	public ArrayList getBillNotGeneratedDetailsQuery(String ddoCode,Integer finYear,String month)
	{
	
			ArrayList lArrOutrLst = new ArrayList();
			ArrayList linnerList = null ;
			Session hibsession = getReadOnlySession();
			StringBuilder lsb = new StringBuilder();
			try
			{	
				lsb.append("Select D.BILL_GROUP_ID,D.Description,count(Psr.post_id)Total_Employees From Org_Ddo_Mst A ");
				lsb.append("Inner Join Cmn_Location_Mst B On B.Location_Code=A.Location_Code ");
				lsb.append("Inner Join Mst_Dcps_Bill_Group D On D.Ddo_Code=A.Ddo_Code ");
				lsb.append("left outer Join Org_Post_Mst Post On Post.Location_Code=A.Location_Code ");
				lsb.append("Inner Join Org_Userpost_Rlt Userpost On Userpost.Post_Id=Post.Post_Id And Userpost.Activate_Flag=1 ");
				lsb.append("left outer Join Hr_Pay_Post_Psr_Mpg Psr On Psr.Post_Id=Post.Post_Id And Psr.Bill_No=D.Bill_Group_Id ");
				lsb.append("Inner Join Rlt_Ddo_Org Trea On Trea.Ddo_Code=A.Ddo_Code ");
				lsb.append("Inner Join Cmn_Location_Mst Trea_Name On Trea_Name.Location_Code=Trea.Location_Code ");
				
				lsb.append("left outer join (Select f.bill_no,e.post_id,e.location_code From Hr_Pay_Paybill A Inner Join Hr_Eis_Emp_Mst B On B.Emp_Id=A.Emp_Id ");
				lsb.append("Inner Join Org_Emp_Mst C On C.Emp_Id=B.Emp_Mpg_Id Inner Join Org_Userpost_Rlt D On D.User_Id=C.User_Id And D.Activate_Flag=1 ");
				lsb.append("Inner Join Org_Post_Mst E On E.Post_Id=D.Post_Id ");
				lsb.append("Inner Join Paybill_Head_Mpg F On F.Paybill_Id=A.Paybill_Grp_Id ");
				lsb.append("Where E.Post_Type_Lookup_Id In (10001198129,10001198130) ");
				lsb.append("And F.Paybill_Month=:month And F.Paybill_Year=:finYear And F.Approve_Flag In (0,1) and a.emp_id is not null)Paybill_Post ");
				lsb.append("On Paybill_Post.Location_Code=Post.Location_Code And Paybill_Post.Post_Id=Post.Post_Id ");
				lsb.append("And Paybill_Post.Bill_No=D.Bill_Group_Id ");
				
				lsb.append("where a.DDO_CODE=:ddoCode ");
				lsb.append("GROUP BY D.BILL_GROUP_ID,D.Description "); 
				lsb.append("having (count(Psr.post_id) = 0 OR (count(Psr.post_id)-count(Paybill_Post.Post_Id))  > 0 ) ");
				SQLQuery lQuery = hibsession.createSQLQuery(lsb.toString());				  
				 lQuery.setParameter("ddoCode", ddoCode);
				 lQuery.setParameter("month", month.split(":")[0]);
				 lQuery.setParameter("finYear", finYear);
				 List<Object[]> resList = lQuery.list();
				 int i = 1;
				  for (Object[] cols : resList) 
				  {
					  linnerList = new ArrayList();
					  linnerList.add(cols[0]!=null?cols[0].toString():"");
					  linnerList.add(cols[1]!=null?cols[1].toString():"");
					  linnerList.add(cols[2]!=null?cols[2].toString():"");
					  lArrOutrLst.add(linnerList);
				 }
			}
			catch (Exception e) {
				
				e.printStackTrace();
			}
			  
			return lArrOutrLst;
		}
		
}

