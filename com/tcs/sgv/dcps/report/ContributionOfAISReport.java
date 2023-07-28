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
 * @since JDK 7.0 jan 21, 2013
 */


public class ContributionOfAISReport  extends DefaultReportDataFinder implements ReportDataFinder{

	private static final Logger logger = Logger.getLogger(ContributionOfAISReport.class);

	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Map lMapSeriesHeadCode = null;
	Map requestAttributes = null;
	Session ghibSession = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException 
	{

		List DataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;

		new ReportsDAOImpl();

		try {

			//-------------- To maintained Session 
			//-------------------- Copy all code from Start to
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
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			//---------------------------End-----------------------------------------
			SimpleDateFormat objsimpledatefrm = new SimpleDateFormat("dd/MM/yyyy");


			//----------------Validation of Report-----------------------
			//-----start----
			if (report.getReportCode().equals("8000076"))
			{
				logger.info("in Report 8000076-----------------");

				String acMain = report.getParameterValue("allindiaid").toString();

				logger.info("acMain is -----------------" + acMain);

				//String monthid = report.getParameterValue("monthId").toString();

				//logger.info("month id is -----------------"+ monthid);

				String yearid = report.getParameterValue("yearId").toString();

				logger.info("year id is ---------------"+yearid);				 


				String tresuryCode = report.getParameterValue("treasuryCode").toString();

				logger.info("tresuryCode is ---------------"+tresuryCode);	

				String EmployeeList = (String) report.getParameterValue("EmployeeList");

				logger.info("EmployeeList is ---------------"+EmployeeList);	
				
				String Statuss = (String) report.getParameterValue("Status");
				logger.info("Status is ---------------"+Statuss);	
				List lListDt=getFromToDt(yearid);
				
				Object objDt[]=null;
				String fromDate=null;
				String toDate=null;
				objDt=(Object[]) lListDt.get(0);
				if(objDt[0]!=null){
					fromDate=objDt[0].toString();

				}
				if(objDt[1]!=null){
					toDate=objDt[1].toString();
				}
				String fromDt = sdf2.format(sdf1.parse(fromDate));
				String toDt = sdf2.format(sdf1.parse(toDate));

				Long lLngTreasuryId = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				String lStrTreasuryName = getLocationName(lLngTreasuryId);
				String lStrEmployeeName = getEmployeeName(EmployeeList);
				Date date = new Date();
				String strheader = report.getReportName();
				report.setReportName(strheader);
				DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());
				String empname = null;
				String Dcspid = null;
				String PranNo = null;
				String ContriTYPE = null;
				String nonRemonth = null;
				String nonReYear = null;
				String ReMonth = null;
				String ReYear =  null;
				String monid =null;
				String monthname =null;
				String yerid = null;
				String yeardesc = null;
				String voucherNo=null;
				String VoucherDate=null;
				String startmonid=null;
				String startyearcode=null;
				float EmployeeContri = 0;
				float EmployerContri = 0;
				//Long SchemCode=null;
				String ddoCode=null;
				String Status=null;
				

				int Total = 0;

				//String lStrYearCOde = objDcpsCommonDAO.getFinYearCodeForYearId(Long.parseLong(yearid));

				//logger.info("lStrYearCOde is ---------------"+lStrYearCOde);	
				//Long lStrYearCodenxt=Long.parseLong(lStrYearCOde);

				//Long lStrYearCodenxtl=lStrYearCodenxt+1;

				//String lStrMonthname=objDcpsCommonDAO.getMonthForId(Long.parseLong(monthid));
				PostEmpContriDAO objPostEmpContriDAO = new PostEmpContriDAOImpl(
						PostEmpContri.class, serviceLocator.getSessionFactory());
				String acMaintBy=null;			
				if(acMain.equals("777")){
					acMaintBy=objPostEmpContriDAO.accMain("777");
				}
				else{				
					acMaintBy=objPostEmpContriDAO.accMain(acMain);
				}

				if(Long.parseLong(report.getParameterValue("treasuryCode").toString())== -1 && Long.parseLong(report.getParameterValue("EmployeeList").toString())==-1 ){
					report.setAdditionalHeader("Report Of "+acMaintBy+"\r\nFor All Treasuries"+"\r\nFinancial Year :"+fromDt+" --- "+toDt);
				}
				else if(Long.parseLong(report.getParameterValue("treasuryCode").toString())!= -1 && Long.parseLong(report.getParameterValue("EmployeeList").toString())!=-1 )
				{
					report.setAdditionalHeader("Report Of "+acMaintBy+"\r\nEmployee Name :"+lStrEmployeeName+"\r\nTreasury Name :"+lStrTreasuryName+"\r\nFinancial Year :"+fromDt+" --- "+toDt);
				}
				else if(Long.parseLong(report.getParameterValue("treasuryCode").toString())== -1 && Long.parseLong(report.getParameterValue("EmployeeList").toString())!=-1){

					report.setAdditionalHeader("Report Of "+acMaintBy+"\r\nEmployee Name :"+lStrEmployeeName+"\r\nFor All Tresuries"+"\r\nFinancial Year :"+fromDt+" --- "+toDt);
				}
				else{
					report.setAdditionalHeader("Report Of "+acMaintBy+"\r\nTresury Name :"+lStrTreasuryName+"\r\nFinancial Year :"+fromDt+"---"+toDt);
				}



				StringBuilder  Strbld = new StringBuilder();


				/*	Strbld.append(" SELECT * FROM ");
				Strbld.append(" ( " );*/
				Strbld.append(" SELECT   emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,trn.DELAYED_FIN_YEAR_ID,trn.DELAYED_MONTH_ID,trn.FIN_YEAR_ID,trn.MONTH_ID||'',mo.MONTH_ID||'',mo.MONTH_NAME,fin.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
				Strbld.append("	,trn.voucher_no,to_char(trn.voucher_date,'dd/MM/yyyy') as Voudate,sum(trn.CONTRIBUTION),sum(decode(trn.EMPLOYER_CONTRI_FLAG,'Y',trn.CONTRIBUTION,0) )as EmployerContri,month(trn.STARTDATE),year(trn.STARTDATE),trn.DDO_CODE,mst.STATUS   ");
				Strbld.append("  FROM TRN_DCPS_CONTRIBUTION trn inner join mst_DCPS_EMP emp on emp.DCPS_EMP_ID = trn.DCPS_EMP_ID  ");
				Strbld.append("  inner join MST_DCPS_CONTRI_VOUCHER_DTLS mst on mst.MST_DCPS_CONTRI_VOUCHER_DTLS = trn.RLT_CONTRI_VOUCHER_ID  ");
				Strbld.append("  inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = trn.TYPE_OF_PAYMENT  ");
				Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on   trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE ");
				Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = month(trn.STARTDATE)  and mo.LANG_ID = 'en_US' ");
				Strbld.append("  where trn.REG_STATUS =1 ");
				Strbld.append("  AND mst.voucher_date BETWEEN '"+fromDt+"' AND '"+toDt+"' ");
				if(acMain.equals("777"))
				{
					Strbld.append("	and  emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) ");
				}
				else
				{
					Strbld.append("	and  emp.AC_DCPS_MAINTAINED_BY =:acMain ");
				}


				if(Long.parseLong(report.getParameterValue("treasuryCode").toString())!= -1){
					Strbld.append(" and trn.TREASURY_CODE=:tresuryCode ");
				}



				if( Long.parseLong(report.getParameterValue("EmployeeList").toString())!= -1)
				{
					Strbld.append(" and emp.DCPS_EMP_ID=:EmployeeList ");
				}
				if(!report.getParameterValue("Status").toString().equals("-1"))
				{
					Strbld.append(" and mst.status=:Statuss ");
				}
				Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1  AND emp.LOC_ID <> 380001 ");
				Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.contribution <> 0 ");
				//Strbld.append(" and trn.DELAYED_FIN_YEAR_ID is null and trn.DELAYED_MONTH_ID is null ");
				Strbld.append(" group by  EMp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,trn.DELAYED_FIN_YEAR_ID,trn.DELAYED_MONTH_ID,trn.FIN_YEAR_ID,trn.MONTH_ID,mo.MONTH_ID,mo.MONTH_NAME,fin.FIN_YEAR_ID,fin.FIN_YEAR_DESC, ");   
				Strbld.append(" trn.voucher_no,trn.VOUCHER_DATE,month(trn.STARTDATE),year(trn.STARTDATE),trn.DDO_CODE,mst.STATUS ");




				logger.info("   ---------"+Strbld.toString() );
				SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

				logger.info("script for all employee ---------"+lQuery.toString() );


				if(!acMain.equals("777")){
					lQuery.setString("acMain",acMain);
				}
				if(Long.parseLong(report.getParameterValue("EmployeeList").toString())!= -1){
					lQuery.setString("EmployeeList",EmployeeList);
				}
			
				if(!report.getParameterValue("Status").toString().equals("-1")){
					lQuery.setString("Statuss",Statuss);
				}
			

				if(Long.parseLong(report.getParameterValue("treasuryCode").toString())!= -1){
					lQuery.setString("tresuryCode",tresuryCode);

				}
				List outputlist = lQuery.list();

				/*	SchemCode= Long.parseLong(outputlist1.get(0).toString());
                 outputlist.add(outputlist1.get(0));*/

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

						Dcspid = (lObj[1] != null) ? lObj[1].toString() : "";
						logger.info("dcps id  ***********"+ Dcspid);

						PranNo = (lObj[2] != null) ? lObj[2].toString() : "";						
						logger.info(" pran no  ***********"+ PranNo);

						ContriTYPE = (lObj[3] != null) ? lObj[3].toString() : "";						
						logger.info(" type of contibution  ***********"+ ContriTYPE);



						nonReYear = (lObj[4] != null) ? lObj[4].toString() : "";						
						logger.info(" Non Regular Year  ***********"+ nonReYear);


						nonRemonth = (lObj[5] != null) ? lObj[5].toString() : "";						
						logger.info(" Non Regular Month ***********"+ nonRemonth);

						ReYear = (lObj[6] != null) ? lObj[6].toString() : "";						
						logger.info(" Regular Year  ***********"+ ReYear);


						ReMonth = (lObj[7] != null) ? lObj[7].toString() : "";						
						logger.info(" Regular Month ***********"+ ReMonth);

						monid = (lObj[8] != null) ? lObj[8].toString() : "";						
						logger.info(" Year  ***********"+ monid);


						monthname = (lObj[9] != null) ? lObj[9].toString() : "";						
						logger.info(" Month ***********"+ monthname);

						yerid = (lObj[10]!= null) ? lObj[10].toString() : "";						
						logger.info(" Year  ***********"+ yerid);


						yeardesc = (lObj[11] != null) ? lObj[11].toString() : "";						
						logger.info(" yeardesc ***********"+ yeardesc);

						voucherNo = (lObj[12] != null) ? lObj[12].toString() : "";						
						logger.info(" voucherNo ***********"+ voucherNo);


						VoucherDate = (lObj[13] != null) ? lObj[13].toString() : "";						
						logger.info(" VoucherDate ***********"+ VoucherDate);

						EmployeeContri = (lObj[14] != null) ? Float.parseFloat(lObj[14].toString()) : new Float(0);						
						logger.info(" EMployee Contribution  ***********"+ EmployeeContri);

						EmployerContri = (lObj[15] != null) ? Float.parseFloat(lObj[15].toString()) : new Float(0);						
						logger.info(" EMployer Contribution  ***********"+ EmployerContri);

						startmonid = (lObj[16] != null) ? lObj[16].toString() : "";						
						logger.info(" startmonid  ***********"+ startmonid);

						startyearcode = (lObj[17] != null) ? lObj[17].toString() : "";						
						logger.info(" startyearcode ***********"+ startyearcode);

						ddoCode = (lObj[18] != null) ? lObj[18].toString() : "";						
						logger.info(" ddoCode ***********"+ ddoCode);

						Status = (lObj[19] != null) ? lObj[19].toString() : "";						
						logger.info(" Status ***********"+ Status);

						String[] Yeardesc1=yeardesc.split("-");


						String yearDesc=null;

						if(Long.parseLong(monid)>3 && Long.parseLong(monid)<=12)  
						{
							yearDesc= Yeardesc1[0];
							logger.info(" yeardesc in first if***********"+ yeardesc);
						}

						else if(Long.parseLong(monid)<=3 && Long.parseLong(monid)>=1)  
						{

							yearDesc= Yeardesc1[1];
							logger.info(" yeardesc in first else***********"+ yeardesc);
						}




						/*	SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
									VoucherDate=sdf1.format(VoucherDate);*/



						if (Status != null  && !Status.equals("")) {

							if(Status.trim().equals("A"))
							{
								Status="Approved";
							}
							else if(Status.trim().equals("G"))
							{
								Status="Approved and Employer Contribution given";
							}
							else if(Status.trim().equals("B"))
							{
								Status="Amount or Date Mismatch";
							}
							else if(Status.trim().equals("E"))
							{
								Status="DDO Code Mismatch";
							}
							else if(Status.trim().equals("F"))
							{
								Status="Matched";
							}
							else
							{
								Status="";
							}
						} else {
							Status="";
						}






						Total = (int) (EmployeeContri + EmployerContri) ;
						logger.info("total number of Contribution  ***********"+ Total);


						dataListForTable.add(count);
						dataListForTable.add(empname);
						dataListForTable.add(Dcspid);	
						dataListForTable.add(ddoCode);
						dataListForTable.add(PranNo);
						dataListForTable.add(ContriTYPE);
						dataListForTable.add(EmployeeContri);
						dataListForTable.add(EmployerContri);
						dataListForTable.add(voucherNo);
						dataListForTable.add(VoucherDate);
						dataListForTable.add("8342508101");
						dataListForTable.add(monthname);
						dataListForTable.add(yearDesc);
						dataListForTable.add(Status);
						dataListForTable.add(Total);
						DataList.add(dataListForTable);

					}
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		return DataList;
	}


	///---------------------- Drop Down Value Start---------------------
	public List<ComboValuesVO> getGISApplicableid(Hashtable otherArgs,String lStrLangId, String lStrLocId) {
		{
			Hashtable sessionKeys = (Hashtable) (otherArgs)
			.get(IReportConstants.SESSION_KEYS);
			Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
			Long lLngLangId = null;
			if (loginMap.containsKey("langId")) {
				lLngLangId = (Long) loginMap.get("langId");
			}
			List<Object[]> lLstResult = new ArrayList<Object[]>();
			List<ComboValuesVO> lLstadmin = new ArrayList<ComboValuesVO>();
			ComboValuesVO lObjComboValueVO = null;

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT LOOKUP_ID,LOOKUP_DESC FROM CMN_LOOKUP_MST where LOOKUP_ID in (700240,700241,700242) and LANG_ID =  ");
			lSBQuery.append(" (SELECT LANG_ID from cmn_Language_Mst where LANG_ID =:langId )  ");

			try {
				ghibSession = ServiceLocator.getServiceLocator()
				.getSessionFactory().getCurrentSession();
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setLong("langId", lLngLangId);

				lLstResult = lQuery.list();

				if (lLstResult != null && lLstResult.size() > 0) {
					for (Object[] lArrObj : lLstResult) {
						lObjComboValueVO = new ComboValuesVO();
						lObjComboValueVO.setId(lArrObj[0].toString());
						lObjComboValueVO.setDesc(lArrObj[1].toString());
						lLstadmin.add(lObjComboValueVO);
					}
				}
				lObjComboValueVO = new ComboValuesVO();
				lObjComboValueVO.setId("777");
				lObjComboValueVO.setDesc("All");
				lLstadmin.add(lObjComboValueVO);

			} catch (Exception e) {
				logger.error(" Error is in getGISApplicableid methods ###################### : "+ e);
				e.printStackTrace();
			}


			return lLstadmin;
		}

	}




	public List getAllTreasuries( String lStrLangId, String lStrLocId) {

		System.out.println("Inside getAllTreasuries ");
		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {			
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003,100006)  and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111) order by CM.loc_id ");

			lCon = DBConnection.getConnection();
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id+"-"+treasury_name);
				arrTreasury.add(vo);
			}

		} catch (Exception e) {
			logger.info("Sql Exception:" + e, e);
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
				logger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}


	public List getMonth(String lStrLangId, String lStrLocCode) {
		List<Object> lArrMonths = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT monthId, monthName FROM SgvaMonthMst WHERE langId = :langId ORDER BY monthNo";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrMonths.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
		}

		return lArrMonths;
	}

	public List getYear(String lStrLangId, String lStrLocId) {

		List<Object> lArrYears = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT finYearId, finYearDesc FROM SgvcFinYearMst WHERE langId =:langId and finYearCode BETWEEN '2007' AND '2015' ORDER BY finYearCode";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrYears.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
		}

		return lArrYears;
	}

	public List getStatus(String lStrLangId, String lStrLocId) {

		List<Object> lArrStatus = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT distinct status,case when status='A' then 'Approved and voucher not match' when status='B' then 'Voucher Date or Amount mismatch' when status='E' then 'DDO mismatch' when status='F' then 'Match' else 'Post Employer Done' end from mst_dcps_contri_voucher_dtls where status is not null and status in ('A','B','E','F','G') ";

			Query lObjQuery = lObjSession.createSQLQuery(lStrBufLang);
		

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrStatus.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
		}

		return lArrStatus;
	}


	/*public List  getYear(String lStrLangId, String lStrLocId) {

		String query = "select finYearId,finYearCode from SgvcFinYearMst WHERE langId =:langId and  finYearCode between '2007' and '2015'";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setString("langId", lStrLangId);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;

		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}
	 */
	//------------------ end--------



	public String getLocationName(Long lLngLocId)
	{
		List lLstDept = null;
		String lStrLocName = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT LOC_NAME FROM CMN_LOCATION_MST WHERE LOC_ID = :locId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locId", lLngLocId);
			lLstDept = lQuery.list();

			if(lLstDept.size() > 0){
				lStrLocName = lLstDept.get(0).toString();
			}
		}catch(Exception e){
			logger.error("Exception in getLocationName:" + e, e);
		}
		return lStrLocName;
	}

	public List getAllEmployee( String acMain,String lStrLangId, String lStrLocId) {

		System.out.println("Inside getAllEmployee ");

		List<Object> lLstReturnList = null;

		try {
			if (!acMain.equals("-1")) {		
				StringBuilder sb = new StringBuilder();
				Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
				sb.append(" SELECT DISTINCT emp.DCPS_EMP_ID,emp.EMP_NAME FROM mst_dcps_emp emp  ");
				if(acMain.equals("777"))
				{
					sb.append(" where emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and emp.DCPS_OR_GPF = 'Y' and emp.REG_STATUS = 1  order by emp.EMP_NAME  ");
				}
				else{
					sb.append(" where emp.AC_DCPS_MAINTAINED_BY=:acMain and emp.DCPS_OR_GPF = 'Y' and emp.REG_STATUS = 1  order by emp.EMP_NAME  ");	
				}
				
				Query selectQuery = lObjSession.createSQLQuery(sb.toString());
				if(!acMain.equals("777")){
					selectQuery.setParameter("acMain", acMain);	
				}
				
				List lLstResult = selectQuery.list();
				ComboValuesVO lObjComboValuesVO = null;
				if (lLstResult != null && lLstResult.size() != 0) {
					lLstReturnList = new ArrayList<Object>();
					Object obj[];
					for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
						obj = (Object[]) lLstResult.get(liCtr);
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(obj[0].toString());
						lObjComboValuesVO.setDesc(obj[1].toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				}


				else {
					lLstReturnList = new ArrayList<Object>();
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("-- Select --");
					lLstReturnList.add(lObjComboValuesVO);
				}
			}

			else {
				lLstReturnList = new ArrayList<Object>();
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("-- Select --");
				lLstReturnList.add(lObjComboValuesVO);
			}


		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is : " + e, e);
		}	return lLstReturnList;
	}


	public String getEmployeeName(String empName)
	{
		List lLstemp = null;
		String lstrempName = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT EMP_NAME FROM  mst_dcps_emp where DCPS_EMP_ID=:empName");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("empName", empName);
			lLstemp = lQuery.list();

			if(lLstemp.size() > 0){
				lstrempName = lLstemp.get(0).toString();
			}
		}catch(Exception e){
			logger.error("Exception in getLocationName:" + e, e);
		}
		return lstrempName;
	}

	public List getFromToDt(String yrId)
	{
		List lLstDate = null;
		String lstrempName = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT to_char(FROM_DATE,'dd/MM/yyyy'),to_char(TO_DATE,'dd/MM/yyyy') from  SGVC_FIN_YEAR_MST where FIN_YEAR_ID= :yrId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("yrId", yrId);
			lLstDate = lQuery.list();

			
		}catch(Exception e){
			logger.error("Exception in getLocationName:" + e, e);
		}
		return lLstDate;
	}
	

}
