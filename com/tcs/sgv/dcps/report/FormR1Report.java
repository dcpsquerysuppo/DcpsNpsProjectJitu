package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Jul 25, 2012
 */

public class FormR1Report extends DefaultReportDataFinder implements ReportDataFinder
{
	private static final Logger gLogger = Logger.getLogger(FormR1Report.class);
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Session ghibSession = null;
	Map requestAttributes = null;

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		
		List lLstDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		TabularData rptTd = null;
		ReportVO RptVo = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();
		ArrayList tr = null;
		ArrayList td = null;
		ArrayList rptList1 = null;
		Object []lObj = null;
		Object []lObjNominee = null;
		
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
			
			String lStrDdoCode = "";
			List lLstResData = null;
			String lStrFromDate = "";
			String lStrToDate = "";
			String lStrType = "";
			String []lStrFromToDate = null;
			String lStrFromAndToDate = "";
			Long lLngFinYear = null;
			String lStrTreasury = "";
			String lStrDOB = "";
			String lStrDOJ = "";
			
			SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
			
			if (lObjReport.getReportCode().equals("700094")) 
			{
				lStrType = lObjReport.getParameterValue("type").toString();
				lLngFinYear = Long.parseLong(lObjReport.getParameterValue("FinYear").toString());
				lStrDdoCode = lObjReport.getParameterValue("ddoCode").toString();
				lStrTreasury = lObjReport.getParameterValue("treasuryCode").toString();
				
				if(lStrType.trim().toString().equals("P")){
					lStrFromDate = lObjReport.getParameterValue("FromDate").toString();
					lStrToDate = lObjReport.getParameterValue("ToDate").toString();
				}else if(lStrType.trim().toString().equals("F")){
					lStrFromAndToDate = getFromToDate(lLngFinYear);
					lStrFromToDate = lStrFromAndToDate.split(",");
					lStrFromDate = lStrFromToDate[0];
					lStrToDate = lStrFromToDate[1];
				}
				
				if(lStrDdoCode.equals("-1")){
					lStrDdoCode = getDdoCodeForDDO(gLngPostId);
				}
				
				String lSBHeader = "FORM R1 \r\n(As Referred to in part no 13, Of Govt. Resolution, Finance Department,No.CPS 1007/18/SER-4,dated 7 July, 2007) " +
						"\r\n <b>FORM OF REGISTER TO BE MAINTENED IN THE OFFICE OF THE STATE RECORD KEEPING AGENCY</b>";
				
				if(!lStrTreasury.equals("")){
					lObjReport.setAdditionalHeader("Treasury Name : "+lStrTreasury+"\r\n" +
							"DDO Code : "+lStrDdoCode+"\r\nFrom-To Period : "+lStrFromDate+"-"+lStrToDate);
				}else{
					lObjReport.setAdditionalHeader("DDO Code : "+lStrDdoCode+"\r\nFrom-To Period : "+lStrFromDate+"-"+lStrToDate);
				}				
				lObjReport.setReportName(lSBHeader);
				
				
				lLstResData = getR1Details(lStrDdoCode, lStrFromDate, lStrToDate);
				
				ArrayList rowList = new ArrayList();
				
				Iterator IT = lLstResData.iterator();
				Integer counter = 1;
				List lLstNomineeDtls = null;
				String lStrNomineeName = "";
				String lStrNomineeAddress = "";
				String lStrNomineeNameAndAddress = "";
				String lStrNomineeAge = "";
				String lStrNomineeRltn = "";
				String lStrShare = "";
				
				while (IT.hasNext()) {
					lObj = (Object[]) IT.next();
					rowList = new ArrayList();
					
					rowList.add(counter);
					
					if(lObj[0] != null){
						rowList.add(lObj[0].toString());
					}else{
						rowList.add("");
					}
					
					if(lObj[1] != null){
						rowList.add(lObj[1].toString());
					}else{
						rowList.add("");
					}
					
					if(lObj[2] != null){
						lStrDOB = lObjSimpleDate.format(lObj[2]);
						rowList.add(lStrDOB);
					}else{
						rowList.add("");
					}
					
					if(lObj[3] != null){
						lStrDOJ = lObjSimpleDate.format(lObj[3]);
						rowList.add(lStrDOJ);
					}else{
						rowList.add("");
					}
					
					if(lObj[4] != null){
						rowList.add(lObj[4].toString());
					}else{
						rowList.add("");
					}
					
					if(lObj[5] != null){
						rowList.add(lObj[5].toString());
					}else{
						rowList.add("");
					}
					
					if(lObj[6] != null){
						rowList.add(lObj[6].toString());
					}else{
						rowList.add("");
					}					
					
					rowList.add("");
					
					lStrNomineeName = "";
					lStrNomineeAddress = "";
					lStrNomineeNameAndAddress = "";
					lStrNomineeAge = "";
					lStrNomineeRltn = "";
					lStrShare = "";
										
					lLstNomineeDtls = getNomineeDetails(lObj[8].toString());					
					
					if(lLstNomineeDtls != null){
						for(Integer lintCnt=0;lintCnt<lLstNomineeDtls.size();lintCnt++){
							lObjNominee = (Object[]) lLstNomineeDtls.get(lintCnt);
							lStrNomineeName = lObjNominee[0].toString();
							lStrNomineeAddress = lObjNominee[1].toString();
							if(lObjNominee[2] != null){
								lStrNomineeAddress = lStrNomineeAddress + lObjNominee[2].toString();
							}
							lStrNomineeNameAndAddress += newline + lStrNomineeName + "--" + lStrNomineeAddress + newline;
							if(lObjNominee[3] != null){
								lStrNomineeAge += newline + lObjSimpleDate.format(lObjNominee[3]) + newline;
							}
							if(lObjNominee[4] != null){
								lStrNomineeRltn += newline + lObjNominee[4].toString() + newline;
							}
							if(lObjNominee[5] != null){
								lStrShare += newline + lObjNominee[5].toString() + newline;
							}
						}
					}
					
					if(lObj[7] != null){
						rowList.add(lObj[7].toString());
					}else{
						rowList.add("");
					}
					
					rowList.add(lStrNomineeNameAndAddress);
					rowList.add(lStrNomineeAge);
					rowList.add(lStrNomineeRltn);
					rowList.add(lStrShare);
					
					lLstDataList.add(rowList);
					counter = counter +1;
				}
			}
		}catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		} 
		return lLstDataList;
	}
	
	public List getR1Details(String lStrDdoCode, String lStrFromDate, String lStrToDate)
	{
		List lLstResData = null;
		SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT EM.dcps_id,EM.emp_name,EM.dob,EM.doj,OPR.POST_NAME,ES.SCALE_DESC,EM.BASIC_PAY,OD.ddo_name,EM.DCPS_EMP_ID FROM mst_dcps_emp EM ");
			lSBQuery.append("join RLT_DCPS_PAYROLL_EMP RL on RL.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
			lSBQuery.append("join ORG_POST_DETAILS_RLT OPR on OPR.POST_ID = RL.post_id ");
			lSBQuery.append("join HR_EIS_SCALE_MST ES on ES.SCALE_ID = EM.PAYSCALE ");
			lSBQuery.append("join org_ddo_mst OD on OD.ddo_Code = EM.ddo_code  ");
			lSBQuery.append("where EM.DDO_CODE = :ddoCode AND ");
			lSBQuery.append("EM.doj between :fromDate and :toDate ");
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.setParameter("fromDate", lObjSimpleDate.parse(lStrFromDate));
			lQuery.setParameter("toDate", lObjSimpleDate.parse(lStrToDate));
			lLstResData = lQuery.list();
		}catch(Exception e){
			e.printStackTrace();
			gLogger.error("Exception in getR1Details:" + e, e);
		}
		return lLstResData;
	}
	
	public String getDdoCodeForDDO(Long lLngPostId) 
	{
		String lStrDdoCode = "";
		List lLstDdoDtls = null;

		try {			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			if(lLstDdoDtls != null && lLstDdoDtls.size() > 0){
				lStrDdoCode = lLstDdoDtls.get(0).toString();
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lStrDdoCode;
	}
	
	public List getNomineeDetails(String lStrDcpsEmpId)
	{
		List lLstResData = null;		
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MEN.name, MEN.address1, MEN.address2, MEN.dob, CLM.lookupDesc, MEN.share ");
			lSBQuery.append("FROM MstEmpNmn MEN, CmnLookupMst CLM ");
			lSBQuery.append("WHERE MEN.dcpsEmpId.dcpsEmpId =:dcpsEmpId AND CLM.lookupId = MEN.rlt");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("dcpsEmpId", Long.parseLong(lStrDcpsEmpId));
			lLstResData = lQuery.list();
		}catch(Exception e){
			gLogger.error("Exception in getNomineeDetails:" + e, e);
		}
		return lLstResData;
	}
	
	public String getFromToDate(Long lLngFinYearId)
	{
		List lLstResData = null;
		SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
		String lStrFromDate = "";
		String lStrToDate = "";
		String lStrResData = "";
		Object lObj[] = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT fromDate, toDate FROM SgvcFinYearMst ");
			lSBQuery.append("WHERE finYearId = :finYearId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("finYearId", lLngFinYearId);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lObj = (Object[]) lLstResData.get(0);
				lStrFromDate = lObjSimpleDate.format(lObj[0]);
				lStrToDate = lObjSimpleDate.format(lObj[1]);
			}
			
			lStrResData = lStrFromDate+","+lStrToDate;
		}catch(Exception e){
			gLogger.error("Exception in getFromToDate:" + e, e);
		}
		
		return lStrResData;
	}
}
