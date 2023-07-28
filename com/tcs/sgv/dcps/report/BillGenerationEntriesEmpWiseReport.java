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
 * @since JDK 7.0 Feb7, 2014
 */


public class BillGenerationEntriesEmpWiseReport extends DefaultReportDataFinder implements ReportDataFinder{

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


			if (report.getReportCode().equals("8000080")) {

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
				String voucherno=null;
				String voucherdate=null;

				lStrPostEmpContriId =  report.getParameterValue("postEmpContriPkId").toString();
				logger.info("lStrPostEmpContriId is ---------------"+lStrPostEmpContriId);

				strAcDcpsMntndBy = report.getParameterValue("dcpsAcntMntndBy").toString();
				logger.info("strAcDcpsMntndBy is ---------------"+strAcDcpsMntndBy);

				finYear=report.getParameterValue("finYear").toString();
				logger.info("finYear is ---------------"+finYear);

				billNo=report.getParameterValue("billNo").toString();
				logger.info("billNo is ---------------"+billNo);

				String lStrYearCOde = objDcpsCommonDAO.getFinYearCodeForYearId(Long.parseLong(finYear));

				PostEmpContriDAO objPostEmpContriDAO = new PostEmpContriDAOImpl(
						PostEmpContri.class, serviceLocator.getSessionFactory());

				String acMaintBy=objPostEmpContriDAO.accMain(strAcDcpsMntndBy);

				logger.info("lStrYearCOde is ---------------"+lStrYearCOde);
				Long lStrYearCodenxt=Long.parseLong(lStrYearCOde);
				Long lStrYearCodenxtl=lStrYearCodenxt+1;
				Object obj[];
				String fromDate=null;
				String toDate=null;
				List lFromToDate=selectFromToDate(finYear);
				for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
					obj = (Object[]) lFromToDate.get(liCtr);
					 fromDate=obj[0].toString();
					 toDate=obj[1].toString();
				}
				
			
				logger.info("fromDate is ---------------"+fromDate);
				logger.info("toDate is ---------------"+toDate);
				createdDt=objPostEmpContriDAO.getBillGendt(lStrPostEmpContriId,strAcDcpsMntndBy);

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				lStrCurrDatef=sdf.format(createdDt);
				logger.info("lStrCurrDatef is ---------------"+lStrCurrDatef);

				report.setAdditionalHeader("List Of "+acMaintBy+" Employees "+"\r\n Bill No: "+billNo+"\r\nBill Generation Date: "+lStrCurrDatef+" \r\n Financial Year :"+lStrYearCOde+"-"+lStrYearCodenxtl);




				StringBuilder SBQuery = new StringBuilder();

				SBQuery.append(" SELECT emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.lookup_name,mo.month_name,sum(trn.CONTRIBUTION) as Employee_Contributon,sum(trn.CONTRIBUTION )as Employer_Contribution,trn.voucher_no,to_char(trn.voucher_date,'dd/MM/yyyy') as voucher_date,month(trn.STARTDATE),year(trn.STARTDATE) ");
				SBQuery.append(" FROM mst_dcps_emp emp  ");
				SBQuery.append(" inner join TRN_DCPS_CONTRIBUTION trn  on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
				SBQuery.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS vou on  vou.MST_DCPS_CONTRI_VOUCHER_DTLS=trn.RLT_CONTRI_VOUCHER_ID ");
				SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on   trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE ");
				SBQuery.append(" inner join SGVA_MONTH_MST mo on  mo.MONTH_ID = month(trn.STARTDATE)  ");
				//SBQuery.append(" inner join MST_DCPS_POST_EMPLOYER_CONTRI post on  emp.AC_DCPS_MAINTAINED_BY=post.AC_DCPS_MAINTAINED_BY ");
				SBQuery.append(" inner join MST_DCPS_POST_EMPLOYER_CONTRI post on  trn.EMPLR_BILL_NO=post.BILL_NO ");
				SBQuery.append(" inner join CMN_LOOKUP_MST look on look.lookup_id=trn.TYPE_OF_PAYMENT ");
				//SBQuery.append(" where post.GENERATE_BILL='Y' and emp.AC_DCPS_MAINTAINED_BY=:strAcDcpsMntndBy ");
				SBQuery.append(" where post.GENERATE_BILL='Y' and post.AC_DCPS_MAINTAINED_BY=:strAcDcpsMntndBy ");
				SBQuery.append(" and trn.POST_EMPLR_CONTRI_STATUS in (1,2) "); 
				SBQuery.append(" and vou.POST_EMPLR_CONTRI_STATUS in (1,2) ");
				SBQuery.append(" and post.BILL_NO=:billNo   and post.FIN_YEAR=:finYear ");
				SBQuery.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) ");
				SBQuery.append(" and trn.EMPLR_BILL_NO =post.BILL_NO and trn.type_of_payment=700047 ");
				SBQuery.append(" and vou.EMPLR_BILL_NO=post.BILL_NO  AND emp.LOC_ID <> 380001  ");
				SBQuery.append(" group by emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.lookup_name,mo.month_name,month(trn.STARTDATE),year(trn.STARTDATE),trn.voucher_no,trn.voucher_date");
				//SBQuery.append(" order by emp.EMP_NAME ");
				
				SBQuery.append(" union all ");
				
				SBQuery.append(" SELECT emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.lookup_name,mo.month_name,sum(trn.CONTRIBUTION) as Employee_Contributon,sum(trn.CONTRIBUTION )as Employer_Contribution,trn.voucher_no,to_char(trn.voucher_date,'dd/MM/yyyy') as voucher_date,mo.month_id,case when mo.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year ");
				SBQuery.append(" FROM mst_dcps_emp emp  ");
				SBQuery.append(" inner join TRN_DCPS_CONTRIBUTION trn  on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
				SBQuery.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS vou on  vou.MST_DCPS_CONTRI_VOUCHER_DTLS=trn.RLT_CONTRI_VOUCHER_ID ");
				SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on   trn.fin_year_id=fin.fin_year_id  ");
				SBQuery.append(" inner join SGVA_MONTH_MST mo on  mo.MONTH_ID = trn.month_id  ");
				//SBQuery.append(" inner join MST_DCPS_POST_EMPLOYER_CONTRI post on  emp.AC_DCPS_MAINTAINED_BY=post.AC_DCPS_MAINTAINED_BY ");
				SBQuery.append(" inner join MST_DCPS_POST_EMPLOYER_CONTRI post on  trn.EMPLR_BILL_NO=post.BILL_NO ");
				SBQuery.append(" inner join CMN_LOOKUP_MST look on look.lookup_id=trn.TYPE_OF_PAYMENT ");
				//SBQuery.append(" where post.GENERATE_BILL='Y' and emp.AC_DCPS_MAINTAINED_BY=:strAcDcpsMntndBy ");
				SBQuery.append(" where post.GENERATE_BILL='Y' and post.AC_DCPS_MAINTAINED_BY=:strAcDcpsMntndBy ");
				SBQuery.append(" and trn.POST_EMPLR_CONTRI_STATUS in (1,2) "); 
				SBQuery.append(" and vou.POST_EMPLR_CONTRI_STATUS in (1,2) ");
				SBQuery.append(" and post.BILL_NO=:billNo   and post.FIN_YEAR=:finYear ");
				SBQuery.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) ");
				SBQuery.append(" and trn.EMPLR_BILL_NO =post.BILL_NO and trn.type_of_payment<>700047 ");
				SBQuery.append(" and vou.EMPLR_BILL_NO=post.BILL_NO  AND emp.LOC_ID <> 380001  ");
				SBQuery.append(" group by emp.EMP_NAME,emp.DDO_CODE,emp.DCPS_ID,emp.PRAN_NO,look.lookup_name,mo.month_name,mo.month_id,fin.FIN_YEAR_CODE,trn.voucher_no,trn.voucher_date");
				//SBQuery.append(" order by emp.EMP_NAME ");
				logger.info("   ---------"+SBQuery.toString() );
				SQLQuery lQuery = ghibSession.createSQLQuery(SBQuery.toString());


				lQuery.setString("strAcDcpsMntndBy",strAcDcpsMntndBy);
				lQuery.setString("billNo",billNo);  
				lQuery.setString("finYear",finYear);  

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

						voucherno = (lObj[8] != null) ? lObj[8].toString() : "";						
						logger.info(" voucherno  ***********"+ voucherno);

						voucherdate = (lObj[9] != null) ? lObj[9].toString() : "";						
						logger.info(" voucherdate  ***********"+ voucherdate);




						Total = (int) (EmployeeContri + EmployerContri) ;
						logger.info("total number of Contribution  ***********"+ Total);





						dataListForTable.add(new StyledData(count,CenterAlignVO));
						dataListForTable.add(new StyledData(empname,CenterAlignVO));
						dataListForTable.add(new StyledData(ddoCode,CenterAlignVO));
						dataListForTable.add(new StyledData(Dcspid,CenterAlignVO));					
						dataListForTable.add(new StyledData(PranNo,CenterAlignVO));
						dataListForTable.add(new StyledData(ContriTYPE,CenterAlignVO));
						dataListForTable.add(new StyledData(monthname,CenterAlignVO));
						dataListForTable.add(new StyledData(EmployeeContri,CenterAlignVO));
						dataListForTable.add(new StyledData(EmployerContri,CenterAlignVO));
						dataListForTable.add(new StyledData(voucherno,CenterAlignVO));
						dataListForTable.add(new StyledData(voucherdate,CenterAlignVO));
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















