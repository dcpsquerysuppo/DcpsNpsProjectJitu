package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.PostEmpContriDAO;
import com.tcs.sgv.dcps.dao.PostEmpContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;
/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Feb19, 2014
 */


public class NSDLEmployeeWiseReport extends DefaultReportDataFinder implements ReportDataFinder{

	private static final Logger logger = Logger.getLogger(ContributionOfAISReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Map lMapSeriesHeadCode = null;
	Map requestAttributes = null;
	Session ghibSession = null;



	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

     	List DataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		
		new ReportsDAOImpl();

		try {

			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			Map lServiceMap = (Map) requestAttributes.get("serviceMap");
			Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
			gLngPostId = (Long) lBaseLoginMap.get("loggedInPost");
			ghibSession = gObjSessionFactory.getCurrentSession();

		
			// for Center Alignment format
			StyleVO[] CenterAlignVO = new StyleVO[2];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0]
			              .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1]
			              .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			
			
			if (report.getReportCode().equals("8000082")) {

			ArrayList rowList = new ArrayList();
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());
			String lStrPostEmpContriId = null;
			Long lLongPostEmpContriId = null;
			String strAcDcpsMntndBy=null;
			String finYear=null;
		    String billNo=null;
		    String empname = null;
			String Dcspid = null;
			String PranNo = null;
			String ContriTYPE = null;
		    String ddoCode=null;
		    String monthname =null;
			float EmployeeContri = 0;
			float EmployerContri = 0;
			int Total = 0;
			float Sumemp=0;
			float Sumemplr=0;
			Date createdDt=null;
			String lStrCurrDatef = null;
			String batchId=null;
			String acMaintainedBycode=null;
			String Smonth=null;
			String startyearcode=null;
			String acMainBy=null;
			String voucherNo=null;
			String VoucherDate=null;
			String TransactionID=null;
			String isDep="N";
			
			batchId =  report.getParameterValue("batchId").toString().trim();
			logger.info("batchId is ---------------"+batchId);
			
			finYear = report.getParameterValue("yearId").toString().trim();
			logger.info("finYear is ---------------"+finYear);
			
		
			
			strAcDcpsMntndBy=report.getParameterValue("acMain").toString().trim();
			logger.info("acMain is ---------------"+strAcDcpsMntndBy);
			
			
			billNo=report.getParameterValue("billno").toString().trim();
			isDep=report.getParameterValue("isDep").toString().trim();
			
			logger.info("billNo is ---------------"+billNo);
			
			PostEmpContriDAO objPostEmpContriDAO = new PostEmpContriDAOImpl(
					PostEmpContri.class, serviceLocator.getSessionFactory());

			if(strAcDcpsMntndBy.equals("A/c Maintained BY IFS"))
			{
				acMaintainedBycode="700242";
			}
			else if(strAcDcpsMntndBy.equals("A/c Maintained BY IPS"))
			{
				acMaintainedBycode="700241";
			}
			else
			{
				acMaintainedBycode="700240";
			}
			
			logger.info("acMaintainedBycode is ---------------"+acMaintainedBycode);
		
		
			
			
				
			 
			 String finYrId=objDcpsCommonDAO.getFinYearIdForYearDesc(finYear);
			 logger.info("finYrId is ---------------"+finYrId);
		
			 
			 String lStrYearCOde = objDcpsCommonDAO.getFinYearCodeForYearId(Long.parseLong(finYrId));
				logger.info("lStrYearCOde is ---------------"+lStrYearCOde);
				
			/*Long lStrYearCodenxt=Long.parseLong(lStrYearCOde);
			Long lStrYearCodenxtl=lStrYearCodenxt+1;
			
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			lStrCurrDatef=sdf.format(createdDt);
			logger.info("lStrCurrDatef is ---------------"+lStrCurrDatef);*/
				
				
				Object obj[];
				String fromDate=null;
				String toDate=null;
				List lFromToDate=selectFromToDate(finYrId);
				for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
					obj = (Object[]) lFromToDate.get(liCtr);
					 fromDate=obj[0].toString();
					 toDate=obj[1].toString();
				}
				
			
			report.setAdditionalHeader("Bill No:"+billNo+"\r\n List Of Employees Under " +strAcDcpsMntndBy+"\r\nFinancial Year :"+finYear);
			
			
			
			
			StringBuilder SBQuery = new StringBuilder();
			if(isDep.equalsIgnoreCase("Y"))
			{
				SBQuery.append(" SELECT  emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME     ");
				SBQuery.append(" ,sum(trn.CONTRIBUTION),sum(trn.CONTRIBUTION) as EmployerContri,month(trn.STARTDATE),year(trn.STARTDATE),trn.voucher_no,to_char(trn.voucher_date,'dd/MM/yyyy'),nsdl.TRANSACTION_ID  ");
				SBQuery.append(" FROM TRN_DCPS_CONTRIBUTION trn inner join mst_DCPS_EMP emp on emp.DCPS_EMP_ID = trn.DCPS_EMP_ID ");
				SBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = trn.TYPE_OF_PAYMENT   ");
			    SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE  ");
				SBQuery.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = month(trn.STARTDATE)  and mo.LANG_ID = 'en_US'  ");
				SBQuery.append(" inner join NSDL_REPORT nsdl on nsdl.BATCH_ID=trn.BATCH_ID ");
				SBQuery.append(" where trn.REG_STATUS =1 and  emp.AC_DCPS_MAINTAINED_BY = :acMaintainedBycode  and trn.BATCH_ID= :batchId and nsdl.DEP_FILE_ID=:DEP_FILE_ID ");
				SBQuery.append(" and (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')  AND emp.LOC_ID <> 380001 and trn.IS_DEPUTATION='Y'  ");
				SBQuery.append(" and emp.REG_STATUS=1  and emp.PRAN_NO is NOT NULL and trn.EMPLOYER_CONTRI_FLAG ='Y'   ");
				SBQuery.append(" group by  emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME  ");
				SBQuery.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),trn.voucher_no,trn.voucher_date,nsdl.TRANSACTION_ID    ");
				SBQuery.append("  order by emp.EMP_NAME ");
			}
			else
			{
				SBQuery.append(" SELECT  emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME     ");
				SBQuery.append(" ,sum(trn.CONTRIBUTION),sum(decode(trn.EMPLOYER_CONTRI_FLAG,'Y',trn.CONTRIBUTION,0) )as EmployerContri,month(trn.STARTDATE),year(trn.STARTDATE),trn.voucher_no,to_char(trn.voucher_date,'dd/MM/yyyy'),nsdl.TRANSACTION_ID  ");
				SBQuery.append(" FROM TRN_DCPS_CONTRIBUTION trn inner join mst_DCPS_EMP emp on emp.DCPS_EMP_ID = trn.DCPS_EMP_ID ");
				SBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = trn.TYPE_OF_PAYMENT   ");
			    SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE  ");
				SBQuery.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = month(trn.STARTDATE)  and mo.LANG_ID = 'en_US'  ");
				SBQuery.append(" inner join NSDL_REPORT nsdl on nsdl.BATCH_ID=trn.BATCH_ID ");
				SBQuery.append(" inner join MST_DCPS_POST_EMPLOYER_CONTRI post on post.BILL_NO=nsdl.BILL_NO and trn.EMPLR_BILL_NO=post.BILL_NO ");
				SBQuery.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMaintainedBycode  and trn.BATCH_ID= :batchId and nsdl.bill_no=:billNo and post.fin_year=:finYrId");
				SBQuery.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "'))  AND emp.LOC_ID <> 380001  ");
				SBQuery.append(" and emp.REG_STATUS=1  and emp.PRAN_NO is NOT NULL and trn.EMPLOYER_CONTRI_FLAG ='Y'   ");
				SBQuery.append(" group by  emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME  ");
				SBQuery.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),trn.voucher_no,trn.voucher_date,nsdl.TRANSACTION_ID    ");
				SBQuery.append("  order by emp.EMP_NAME ");
			}
			
			logger.info("   ---------"+SBQuery.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(SBQuery.toString());
			
			
			lQuery.setString("acMaintainedBycode",acMaintainedBycode);
			if(isDep.equalsIgnoreCase("Y"))
			{
				lQuery.setString("DEP_FILE_ID",billNo); 
			}
			else
			{
				lQuery.setString("billNo",billNo);  
				lQuery.setString("finYrId",finYrId);  
			}
			
			
			lQuery.setString("batchId",batchId);  
			  
			
			List outputlist = lQuery.list();
			
			
			List dataListForTable = new ArrayList();
			
			

			if (outputlist != null && !outputlist.isEmpty()) {
				int count = 0;

				for (Iterator it = outputlist.iterator(); it.hasNext();)

				{
					count++;
					dataListForTable = new ArrayList();
					Object[] lObj = (Object[]) it.next();
			
					empname = (lObj[0] != null) ? lObj[0].toString(): "NA";
					logger.info("Employee name is  ***********" + empname);
					
					ddoCode = (lObj[1] != null) ? lObj[1].toString(): "";
					logger.info("ddoCode  is  ***********" + ddoCode);
					

					Dcspid = (lObj[2] != null) ? lObj[2].toString() : "";
					logger.info("dcps id  ***********"+ Dcspid);

					PranNo = (lObj[3] != null) ? lObj[3].toString() : "";						
					logger.info(" pran no  ***********"+ PranNo);

					ContriTYPE = (lObj[4] != null) ? lObj[4].toString() : "";						
					logger.info(" type of contibution  ***********"+ ContriTYPE);
					

					monthname = (lObj[5] != null) ? lObj[5].toString() : "";						
					logger.info(" monthname  ***********"+ monthname);
					
					EmployeeContri = (lObj[6] != null) ? Float.parseFloat(lObj[6].toString()) : new Float(0);						
					logger.info(" EMployee Contribution  ***********"+ EmployeeContri);

					EmployerContri = (lObj[7] != null) ? Float.parseFloat(lObj[7].toString()) : new Float(0);						
					logger.info(" EMployer Contribution  ***********"+ EmployerContri);
					
					monthname = (lObj[5] != null) ? lObj[5].toString() : "";						
					logger.info(" monthname  ***********"+ monthname);
					
					Total = (int) (EmployeeContri + EmployerContri) ;
					logger.info("total number of Contribution  ***********"+ Total);
		
                    Smonth=(lObj[8] != null) ? lObj[8].toString() : "";
                    logger.info("Smonth  ***********"+ Smonth);
                    
                	startyearcode = (lObj[9] != null) ? lObj[9].toString() : "";						
					logger.info(" startyearcode ***********"+ startyearcode);

					
					
					
					
					voucherNo = (lObj[10] != null) ? lObj[10].toString() : "";						
					logger.info(" voucherNo ***********"+ voucherNo);


					VoucherDate = (lObj[11] != null) ? lObj[11].toString() : "";						
					logger.info(" VoucherDate ***********"+ VoucherDate);
					
					TransactionID = (lObj[12] != null) ? lObj[12].toString() : "";						
					logger.info(" TransactionID ***********"+ TransactionID);
					
					
					dataListForTable.add(new StyledData(count,CenterAlignVO));
					dataListForTable.add(new StyledData(empname,CenterAlignVO));
					dataListForTable.add(new StyledData(ddoCode,CenterAlignVO));
					dataListForTable.add(new StyledData(Dcspid,CenterAlignVO));					
					dataListForTable.add(new StyledData(PranNo,CenterAlignVO));
					dataListForTable.add(new StyledData(ContriTYPE,CenterAlignVO));
					dataListForTable.add(new StyledData(EmployeeContri,CenterAlignVO));
					dataListForTable.add(new StyledData(EmployerContri,CenterAlignVO));
					dataListForTable.add(new StyledData(voucherNo,CenterAlignVO));
					dataListForTable.add(new StyledData(VoucherDate,CenterAlignVO));
				
					dataListForTable.add(new StyledData(monthname,CenterAlignVO));
					dataListForTable.add(new StyledData(startyearcode,CenterAlignVO));
					dataListForTable.add(new StyledData(TransactionID,CenterAlignVO));
					dataListForTable.add(new StyledData(Total,CenterAlignVO));
					DataList.add(dataListForTable);
				
				
					Sumemp=Sumemp+EmployeeContri;
					Sumemplr=Sumemplr+EmployerContri;
					logger.info("Sumemp  ***********"+ Sumemp);
					logger.info("Sumemplr  ***********"+ Sumemplr);
				}
				
			}	
	
	}
}
	
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Exception :" + e, e);
			e.printStackTrace();

		}
		return DataList;
}
	
	
	public List selectFromToDate(String finYearId)throws Exception {


		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT to_char(FROM_DATE,'yyyy-MM-dd'),to_char(TO_DATE,'yyyy-MM-dd') FROM  SGVC_FIN_YEAR_MST where FIN_YEAR_ID= :finYearId "); 
			
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setString("finYearId",finYearId);
			contrList = lQuery.list();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Error is :" + e, e);
			throw(e);
		}
		return contrList;
	}	
	
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

