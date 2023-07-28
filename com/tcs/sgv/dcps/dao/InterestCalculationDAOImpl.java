package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;

public class InterestCalculationDAOImpl extends GenericDaoHibernateImpl
  implements InterestCalculationDAO
{
  private Session ghibSession = null;
  private static final Logger gLogger = Logger.getLogger(InterestCalculationDAOImpl.class);

  public InterestCalculationDAOImpl(Class type, SessionFactory sessionFactory)
  {
    super(type);
    setSessionFactory(sessionFactory);
    this.ghibSession = sessionFactory.getCurrentSession();
  }

 
  public Boolean checkEmployeeEligibleForIntrstCalc(Long dcpsEmpId,String lStrFromDate, String lStrToDate,Long yearId) {

		List listAllEmpsForIntrstCalc = null;
		Boolean lBlFlag = false;
		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT dcps_emp_id FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		//lSBQuery.append(" JOIN MST_DCPS_BILL_GROUP BG on BG.BILL_GROUP_ID = VC.BILL_GROUP_ID");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");
		lSBQuery.append(" WHERE ");
		//lSBQuery.append(" VC.treasury_code=" + treasuryCode);
		//lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		lSBQuery.append(" TR.dcps_emp_id = " + dcpsEmpId);
		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		//lSBQuery.append(" AND tn.TREASURY_CODE = '" + treasuryCode +"'");
		lSBQuery.append(" AND (");

		lSBQuery.append(" (VC.VOUCHER_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "' )");
		lSBQuery.append(" OR");
		lSBQuery.append(" (VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '" + lStrToDate + "' )");
		lSBQuery.append(" )");
		lSBQuery.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date = tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		lSBQuery.append(" AND VC.voucher_status = 1");
		lSBQuery.append(" AND TR.reg_status = 1");
		//lSBQuery.append(" AND EM.reg_status = 1");
		
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");

		lSBQuery.append(" UNION");

		lSBQuery.append(" select dcps_emp_id from rlt_dcps_sixpc_yearly ");
		lSBQuery.append(" where dcps_emp_id = " + dcpsEmpId );
		lSBQuery.append(" and STATUS_FLAG = 'A' and FIN_YEAR_ID = " + yearId );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listAllEmpsForIntrstCalc = lQuery.list();

		if(listAllEmpsForIntrstCalc != null && listAllEmpsForIntrstCalc.size()!=0 )
		{
			lBlFlag = true ;
		}

		return lBlFlag;
	}
  

  
  public List getContriDtlsForGivenEmployeeListFinal(String lStrFromDate, String lStrToDate,Long previousFinYearId,List listAllEmpsDCPSEmpIdsForIntrstCalc)  throws Exception {

		List listContriDtlsForGivenEmp = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT VC.voucher_date,TR.CONTRIBUTION,VC.month_id,VC.year_id,TR.EMPLOYER_CONTRI_FLAG,TR.DCPS_CONTRIBUTION_ID,VC.voucher_no,FY.FIN_YEAR_CODE,TR.TYPE_OF_PAYMENT,(case  when :previousFinYearId=24 then 8.6 when :previousFinYearId=25 then 8.8  when :previousFinYearId=26 then 8.7 else 8 end) as Ints ,  ");

		lSBQuery.append(" EM.dcps_emp_id,EM.dcps_id,TR.ddo_code,nvl(INP.MST_DCPS_CONTRIBUTION_YEARLY_ID,0),nvl(INP.CLOSE_EMPLOYEE,0),nvl(CLOSE_EMPLOYER,0),nvl(OPEN_INT,0),nvl(CLOSE_TIER2,0)");

		lSBQuery.append(" ,TR.TREASURY_CODE,EM.DDO_CODE ");

		// Below line added to consider weighted average in case the interest rate changes within the year

		//lSBQuery.append(" ,MAX(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE),nvl(IR.APPLICABLE_TO,FY.TO_DATE)");

		lSBQuery.append(" ,MAX(nvl(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE),VC.VOUCHER_DATE),FY.TO_DATE ");
		lSBQuery.append(" FROM trn_dcps_contribution TR");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.MST_DCPS_CONTRI_VOUCHER_DTLS = TR.RLT_CONTRI_VOUCHER_ID");
		//lSBQuery.append(" JOIN MST_DCPS_BILL_GROUP BG on BG.BILL_GROUP_ID = VC.BILL_GROUP_ID");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");


		// Added by Ashish for issue related to pre and future fin_year_id 

		if(lStrFromDate.equals("2012-04-01")  && lStrToDate.equals("2013-03-31")){
			lSBQuery.append(" JOIN sgvc_fin_year_mst FY on FY.FIN_YEAR_ID = 25 ");
			
		}
		else if(lStrFromDate.equals("2013-04-01")  && lStrToDate.equals("2014-03-31"))
		{
			
			lSBQuery.append(" JOIN sgvc_fin_year_mst FY on FY.FIN_YEAR_ID = 26 ");
		}
		// Ended by Ashish for issue related to pre and future fin_year_id 
		lSBQuery.append(" JOIN MST_DCPS_EMP EM on EM.dcps_emp_id = TR.dcps_emp_id ");

		//lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on VC.voucher_date BETWEEN IR.EFFECTIVE_FROM AND IR.APPLICABLE_TO ");

		//("SELECT interest FROM mst_dcps_interest_rate WHERE '"	+ lStrYear + "' >= effective_from AND ('" + lStrYear + "' < applicable_to OR applicable_to IS NULL )");

		// Below line commented as Weighted Interest has to be calculated
		//lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on VC.voucher_date >= IR.EFFECTIVE_FROM AND ( VC.voucher_date < IR.APPLICABLE_TO OR IR.APPLICABLE_TO IS NULL ) ");

		//lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on IR.FIN_YEAR_ID = VC.YEAR_ID");

		lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on IR.FIN_YEAR_ID = VC.YEAR_ID");

		lSBQuery.append(" LEFT JOIN MST_DCPS_CONTRIBUTION_YEARLY INP on INP.dcps_id = EM.dcps_id and INP.YEAR_ID = " + previousFinYearId);


		lSBQuery.append(" WHERE ");

		// lSBQuery.append(" VC.treasury_code = " + treasuryCode");
		lSBQuery.append(" EM.dcps_emp_id in (:lListDcpsEmpIds)");
		lSBQuery.append(" AND VC.voucher_date BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "' ");
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date=tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		//lSBQuery.append(" AND tn.TREASURY_CODE = '" + treasuryCode +"'");
		//lSBQuery.append(" AND TR.dcps_emp_id = " + dcpsEmpId);
		lSBQuery.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		lSBQuery.append(" AND TR.reg_status = 1 ");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");

		lSBQuery.append(" AND TR.IS_MISSING_CREDIT IS NULL AND TR.IS_CHALLAN IS NULL");
		//	lSBQuery.append(" AND VC.voucher_date BETWEEN  IR.EFFECTIVE_FROM AND NVL(IR.APPLICABLE_TO ,sysdate) ");
		//lSBQuery.append(" AND MAX(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE) < nvl(IR.APPLICABLE_TO,FY.TO_DATE)");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsEmpIds", listAllEmpsDCPSEmpIdsForIntrstCalc);
		lQuery.setLong("previousFinYearId", previousFinYearId+1);
		listContriDtlsForGivenEmp = lQuery.list();

		return listContriDtlsForGivenEmp;
	}

 
  public List getContriDtlsForGivenEmployeeListFinalForMissingCredits(String lStrFromDate, String lStrToDate,Long finYearId,List listAllEmpsEmpIdsForIntrstCalcForMissingCredits) throws Exception {

		List listContriDtlsForGivenEmp = null;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		lSBQuery.append(" SELECT MAX(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE),nvl(IR.APPLICABLE_TO,FY.TO_DATE),FY.FROM_DATE,FY.TO_DATE,FY.fin_year_id,VC.voucher_date,");
		lSBQuery.append(" TR.CONTRIBUTION,VC.month_id,VC.year_id,TR.EMPLOYER_CONTRI_FLAG,TR.DCPS_CONTRIBUTION_ID,VC.voucher_no,FY.FIN_YEAR_CODE,");
		lSBQuery.append(" TR.TYPE_OF_PAYMENT,IR.INTEREST,EM.dcps_emp_id,EM.dcps_id,TR.ddo_code,TR.TREASURY_CODE,EM.DDO_CODE, ");
		lSBQuery.append(" TR.IS_MISSING_CREDIT, TR.IS_CHALLAN ");
		lSBQuery.append(" FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.MST_DCPS_CONTRI_VOUCHER_DTLS = TR.RLT_CONTRI_VOUCHER_ID ");
		lSBQuery.append(" JOIN sgvc_fin_year_mst FY on FY.FIN_YEAR_ID >= VC.year_id and FY.FIN_YEAR_ID <= :finYearId");
		lSBQuery.append(" JOIN MST_DCPS_EMP EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on IR.FIN_YEAR_ID = FY.FIN_YEAR_ID");
		lSBQuery.append(" WHERE ");
		lSBQuery.append(" EM.dcps_emp_id in (:lListDcpsEmpIds)");
		lSBQuery.append(" AND VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "' ");
		lSBQuery.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		lSBQuery.append(" AND TR.reg_status = 1 ");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') ");
		lSBQuery.append(" ORDER BY TR.DCPS_CONTRIBUTION_ID,FY.FIN_YEAR_ID");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsEmpIds", listAllEmpsEmpIdsForIntrstCalcForMissingCredits);
		lQuery.setParameter("finYearId", finYearId);
		listContriDtlsForGivenEmp = lQuery.list();

		return listContriDtlsForGivenEmp;

	}




	public List getDCPSIdsForDcpsEmpIds(List lListDcpsEmpIds) {

		StringBuilder lSBQuery = new StringBuilder();
		List lListDcpsIds = null;
		logger.info("List size "+lListDcpsEmpIds.size());
		lSBQuery.append(" SELECT mst.dcpsId from MstEmp  mst where mst.dcpsEmpId in (:dcpsEmpIdList) ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameterList("dcpsEmpIdList", lListDcpsEmpIds);

		lListDcpsIds = lQuery.list();
		return lListDcpsIds;
	}


  
  public void deleteYearlyIntrstsForGivenEmpList(List lListDcpsIds,Long lLongYearId) {

		Session session = getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MST_DCPS_CONTRIB_TERMINATION_YEARLY ");
		lSBQuery.append(" where year_id = :yearId");
		lSBQuery.append(" and dcps_id in (:lListDcpsIds)");
		
		
		

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsIds", lListDcpsIds);
		lQuery.setParameter("yearId", lLongYearId);

		int status = lQuery.executeUpdate();
	
	}
  public void deleteYearlyIntrstsForGivenEmpListBkp2018(List lListDcpsIds,Long lLongYearId) {

		Session session = getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MST_DCPS_CONTRIBUTION_YEARLY ");
		lSBQuery.append(" where year_id = :yearId");
		lSBQuery.append(" and dcps_id in (:lListDcpsIds)");
		
		
		

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsIds", lListDcpsIds);
		lQuery.setParameter("yearId", lLongYearId);

		int status = lQuery.executeUpdate();
	
	}
  

 

	public void deleteMonthlyIntrstsForGivenEmpList(List lListDcpsIds,Long lLongYearId) {

		StringBuilder lSBQuery = new StringBuilder();
		
		
/*
		String dcpsEmpIds = "";
		for(int i = 0;i<lListDcpsIds.size();i++){
			if(i!=0){
				dcpsEmpIds = dcpsEmpIds+","+lListDcpsIds.get(i).toString();
			}
			else{
				dcpsEmpIds = lListDcpsIds.get(i).toString();
			}
		}*/
		
		lSBQuery.append(" delete from MST_DCPS_CONTRIB_TERMINATION_MONTHLY ");
		lSBQuery.append(" where year_id = :yearId");
		lSBQuery.append(" and dcps_id in (:lListDcpsIds)");
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsIds", lListDcpsIds);
		lQuery.setParameter("yearId", lLongYearId);

		int status = lQuery.executeUpdate();
	}
	public void deleteMonthlyIntrstsForGivenEmpListbkp2018(List lListDcpsIds,Long lLongYearId) {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MstDcpsContributionMonthly ");
		lSBQuery.append(" where yearId = :yearId");
		lSBQuery.append(" and dcpsId in (:lListDcpsIds)");
		
		
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsIds", lListDcpsIds);
		lQuery.setParameter("yearId", lLongYearId);

		lQuery.executeUpdate();
	}

  
	public Double getInterestRateForGivenYear(String lStrYear) {

		Double interestRate = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT interest FROM mst_dcps_interest_rate WHERE '"
				+ lStrYear + "' >= effective_from AND ('" + lStrYear
				+ "' < applicable_to OR applicable_to IS NULL )");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		interestRate = Double.parseDouble(lQuery.uniqueResult().toString());

		return interestRate;
	}

  
  
  public MstDcpsContributionYearly getContriYearlyVOForYear(
			Long dcpsEmpId, Long previousYearId) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;

		Query lQuery = null;

		lSBQuery
		.append(" SELECT YC FROM MstDcpsContributionYearly YC,MstEmp EM WHERE YC.dcpsId = EM.dcpsId AND EM.dcpsEmpId = :dcpsEmpId AND YC.yearId = :yearId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("yearId", previousYearId);

		lObjMstDcpsContributionYearly = (MstDcpsContributionYearly) lQuery
		.uniqueResult();

		return lObjMstDcpsContributionYearly;
	}

  
  
  public String getDcpsIdForEmpId(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String dcpsId = null;

		lSBQuery
		.append(" select dcpsId FROM MstEmp WHERE dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		tempList = lQuery.list();
		if(tempList != null)
		{
			if(tempList.size() != 0)
			{
				dcpsId = tempList.get(0);
			}
		}

		return dcpsId;

	}

  
	public Long getYearIdForYearCode(String yearCode) {

		Long lLongYearId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			//ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
			.append(" SELECT finYearId FROM SgvcFinYearMst WHERE finYearCode = :yearCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("yearCode", yearCode);
			lLongYearId = Long.valueOf(hqlQuery.list().get(0).toString());

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error  is " + e);

		}
		return lLongYearId;
	}


  
  
  public List getAllEmpsUnderDDO(String treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception  {

		/*List empList = null;

		try {
			//ghibSession = getSession();

		String query = "select EM.dcpsEmpId,EM.dcpsId,EM.name from MstEmp EM where ddoCode= :DDOCode and EM.dcpsId is not null  AND EM.acDcpsMaintainedBy not in (700240,700241,700242) order by EM.name,EM.dcpsEmpId,EM.dcpsId ";



			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			empList = stQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			e.printStackTrace();
			throw (e);
		}
			return empList;*/


		List listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT EM.dcps_emp_id,EM.dcps_id,EM.EMP_NAME FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		lSBQuery.append(" JOIN mst_dcps_emp EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");

		lSBQuery.append(" WHERE ");
		lSBQuery.append(" VC.treasury_code= '"+ treasuryCode +"' ");

		if(!ddoCode.equals(""))
		{
			lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		}

		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		lSBQuery.append(" AND ((VC.VOUCHER_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "' )");
		lSBQuery.append(" OR");
		lSBQuery.append(" (VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '" + lStrToDate + "' ) ");
		lSBQuery.append(" ) ");
		lSBQuery.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date = tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		lSBQuery.append(" AND VC.voucher_status = 1");

		// Gets only those employees whose post employer Contribution is done.
		lSBQuery.append(" AND TR.REG_STATUS = 1");
		lSBQuery.append(" AND EM.REG_STATUS = 1");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");
		lSBQuery.append(" AND EM.dcps_id is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) <> '-1'  ");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY  not in (700240,700241,700242) ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lQuery.list();

		return listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc;



	}


	public void updateTrnDcpsContriIntCalculated(List lListDcpsContriIdsPk) throws Exception  {

		try {
			StringBuilder lSBQuery = new StringBuilder();

			Query lQuery = null;
			lSBQuery.append(" UPDATE TrnDcpsContribution SET status='H' ");
			lSBQuery.append(" WHERE dcpsContributionId in (:dcpsContriId) ");
			
			
			
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("dcpsContriId", lListDcpsContriIdsPk);
			lQuery.executeUpdate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(" Error is : " + e, e);
			e.printStackTrace();
			throw (e);
		}

	}

  
  public void deleteMonthlyInterestForDCPSIdAndYear(String dcpsId,Long finYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MstDcpsContributionMonthly where dcpsId = :dcpsId and yearId = :finYearId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);
		lQuery.setParameter("finYearId", finYearId);
		lQuery.executeUpdate();

	}

 
  
  public List getAllIntRateDtlsForGivenYear(Long lLongYear) {

		List lListIntRateDetailsForGivenYear = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT IR.INTEREST,IR.EFFECTIVE_FROM,nvl(IR.APPLICABLE_TO,FY.TO_DATE),days(nvl(IR.APPLICABLE_TO,FY.TO_DATE)) - days(IR.EFFECTIVE_FROM) + 1  FROM mst_dcps_interest_rate IR ");
		lSBQuery.append(" join SGVC_FIN_YEAR_MST FY on IR.FIN_YEAR_ID = FY.FIN_YEAR_ID");
		lSBQuery.append(" where IR.FIN_YEAR_ID = " + lLongYear );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListIntRateDetailsForGivenYear = lQuery.list();

		return lListIntRateDetailsForGivenYear;
	}

 
  
  public String getAllDCPSEmployeesForIntrstCalc(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception {

		List listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT EM.dcps_emp_id FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		lSBQuery.append(" JOIN mst_dcps_emp EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");

		lSBQuery.append(" WHERE ");
		//VC.treasury_code in (SELECT LOC_ID FROM CMN_LOCATION_MST where ( PARENT_LOC_ID = 3501 or LOC_ID = 3501))
		lSBQuery.append(" VC.treasury_code in ( SELECT LOC_ID FROM CMN_LOCATION_MST where ( PARENT_LOC_ID =" + treasuryCode +" or LOC_ID = "+ treasuryCode+" )) ");
		//lSBQuery.append(" VC.treasury_code=" + treasuryCode);
		if(!ddoCode.equals(""))
		{
			lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		}

		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		lSBQuery.append(" AND VC.voucher_date BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "' ");
		lSBQuery.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date = tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		lSBQuery.append(" AND VC.voucher_status = 1");

		// Gets only those employees whose post employer Contribution is done.
		lSBQuery.append(" AND TR.REG_STATUS = 1");
		lSBQuery.append(" AND EM.REG_STATUS = 1");
		//Excluding missing credit records
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");
		lSBQuery.append(" AND EM.dcps_id is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) <> '-1'  ");
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT is null AND  TR.IS_CHALLAN is null ) ");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY  not in (700240,700241,700242) ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lQuery.list();
		String dcpsEmpIds = "";
		for(int i = 0;i<listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.size();i++){
			if(i!=0){
				dcpsEmpIds = dcpsEmpIds+","+listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.get(i).toString();
			}
			else{
				dcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.get(i).toString();
			}
		}
		
		lQuery = null;
		listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
		gLogger.info("First Query is ***"+lSBQuery.toString());
		gLogger.info("Employee lis is  ***"+dcpsEmpIds);
		return dcpsEmpIds;
	}

 
  
  public String getAllDCPSEmployeesForIntrstCalcForMissingCredits(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate,String excludeEmps) throws Exception{

		List listAllEmpsEmpIdsForIntrstCalcForMissingCredits = null;
	
		String[] dcpsEmpId = excludeEmps.split(",");
		List lstDcpsEmpId = new ArrayList();
		for(int i=0 ; i< dcpsEmpId.length; i++){
					lstDcpsEmpId.add(dcpsEmpId[i]);
					
				}
		

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT EM.dcps_emp_id FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		lSBQuery.append(" JOIN mst_dcps_emp EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		lSBQuery.append(" WHERE ");
		lSBQuery.append(" VC.treasury_code in ( SELECT LOC_ID FROM CMN_LOCATION_MST where ( PARENT_LOC_ID =" + treasuryCode +" or LOC_ID = "+ treasuryCode+" )) ");

		if(!ddoCode.equals(""))
		{
			lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		}

		lSBQuery.append(" AND VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "'");
		lSBQuery.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		lSBQuery.append(" AND VC.voucher_status = 1");
		lSBQuery.append(" AND TR.REG_STATUS = 1");
		lSBQuery.append(" AND EM.REG_STATUS = 1");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");
		lSBQuery.append(" AND EM.dcps_id is not null ");
		lSBQuery.append(" AND EM.dcps_id is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) <> '-1'  ");
		//lSBQuery.append(" AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') and (IS_DEPUTATION is null or IS_DEPUTATION <> 'Y') ");
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') ");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY not in (700240,700241,700242) "); 
		if(excludeEmps!=null && !"".equalsIgnoreCase(excludeEmps.trim()))
		{
			lSBQuery.append(" AND EM.dcps_emp_id not in  (:dcpsEmpIds) ");
		}
		
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		if(excludeEmps!=null && !"".equalsIgnoreCase(excludeEmps.trim()))
		{
			lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpId);
		}
		
		listAllEmpsEmpIdsForIntrstCalcForMissingCredits = lQuery.list();
		
		
		String dcpsEmpIds = "";
		for(int i = 0;i<listAllEmpsEmpIdsForIntrstCalcForMissingCredits.size();i++){
			if(i!=0){
				dcpsEmpIds = dcpsEmpIds+","+listAllEmpsEmpIdsForIntrstCalcForMissingCredits.get(i).toString();
			}
			else{
				dcpsEmpIds = listAllEmpsEmpIdsForIntrstCalcForMissingCredits.get(i).toString();
			}
		}
		
		lQuery = null;
		gLogger.info("Second query is **********"+lSBQuery.toString());
		gLogger.info("Employee list is ***************"+dcpsEmpIds);
		listAllEmpsEmpIdsForIntrstCalcForMissingCredits = null;
		return dcpsEmpIds;
	}


 
  public String checkEmployeeEligibleForIntrstCalc(String dcpsEmpId,String lStrFromDate, String lStrToDate,Long yearId) {

		List listAllEmpsForIntrstCalc = new ArrayList();
		Boolean lBlFlag = false;
		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
		
		String[] dcpsEmpIds = dcpsEmpId.split(",");

		for(int i=0 ; i< dcpsEmpIds.length; i++){
		
			listAllEmpsForIntrstCalc.add(dcpsEmpIds[i]);
		}

		lSBQuery.append(" SELECT DISTINCT dcps_emp_id FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		//lSBQuery.append(" JOIN MST_DCPS_BILL_GROUP BG on BG.BILL_GROUP_ID = VC.BILL_GROUP_ID");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");
		lSBQuery.append(" WHERE ");
		//lSBQuery.append(" VC.treasury_code=" + treasuryCode);
		//lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		lSBQuery.append(" TR.dcps_emp_id in (:listAllEmpsForIntrstCalc) ");
		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		//lSBQuery.append(" AND tn.TREASURY_CODE = '" + treasuryCode +"'");
		lSBQuery.append(" AND (");

		lSBQuery.append(" (VC.VOUCHER_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "' )");
		lSBQuery.append(" OR");
		lSBQuery.append(" (VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '" + lStrToDate + "' )");
		lSBQuery.append(" )");
		lSBQuery.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date = tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		lSBQuery.append(" AND VC.voucher_status = 1");
		lSBQuery.append(" AND TR.reg_status = 1");
		//lSBQuery.append(" AND EM.reg_status = 1");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");

		lSBQuery.append(" UNION");

		lSBQuery.append(" select dcps_emp_id from rlt_dcps_sixpc_yearly ");
		lSBQuery.append(" where dcps_emp_id  in (:listAllEmpsForIntrstCalc) ");
		lSBQuery.append(" and STATUS_FLAG = 'A' and FIN_YEAR_ID = " + yearId );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("listAllEmpsForIntrstCalc", listAllEmpsForIntrstCalc);

		listAllEmpsForIntrstCalc = lQuery.list();

		String dcpsEmpIdss = "";
		for(int i = 0;i<listAllEmpsForIntrstCalc.size();i++){
			if(i!=0){
				dcpsEmpIdss = dcpsEmpIdss+","+listAllEmpsForIntrstCalc.get(i).toString();
			}
			else{
				dcpsEmpIdss = listAllEmpsForIntrstCalc.get(i).toString();
			}
		}
		
		lQuery = null;
		listAllEmpsForIntrstCalc = null;
		return dcpsEmpIdss;
	}
 
  
  
  public List getEmpListForMissingCredit(String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,String strFromDatePassed, String strToDatePassed ,String yearId){
	  List empDetails = new ArrayList();
		List tempEmpDetails = null;
		List lstDcpsEmpId = new ArrayList();

		List lstDcpsEmpIds = new ArrayList();

		String[] dcpsEmpId = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.split(",");

          for(int i=0 ; i< dcpsEmpId.length; i++){
				
			
			if(i==0 || i%4000!=0){
				lstDcpsEmpIds.add(dcpsEmpId[i]);
			}
			else
			{
				lstDcpsEmpIds.add(dcpsEmpId[i]);
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end as payble_year, ");
				sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
				sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
				sb.append("tr.IS_MISSING_CREDIT, TR.IS_CHALLAN, ");
				if(yearId.equalsIgnoreCase("25"))
				{
					sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd'),b.end_date ");
				}
				else
				{
					sb.append("nvl(contri_year.MST_DCPS_CONTRIB_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd'),b.end_date ");
				}
				sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
				sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
				sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
				sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
				sb.append(" inner join HR_EIS_EMP_MST empMst on empMst.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				if(yearId.equalsIgnoreCase("25"))
				{
					sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
				}
				else
				{
					sb.append("left outer join MST_DCPS_CONTRIB_TERMINATION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
				}
				sb.append(" left outer join (SELECT end.emp_id,end.created_date,end.end_date FROM HR_EIS_EMP_END_DATE end inner join (select max(CREATED_DATE) created_dt,emp_id  as empid FROM HR_EIS_EMP_END_DATE group by emp_id ) a  on a.created_dt=end.created_date and a.empid=end.emp_id) b on empMst.EMP_ID = b.emp_id ");
				sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
				sb.append("and trim(emp.dcps_id) <>'-1' and vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN :fromDate AND :toDate ");
				sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
				sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
				//sb.append("AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') and (IS_DEPUTATION is null or IS_DEPUTATION <> 'Y') ");
				sb.append("AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') ");
				
				sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");
				
				

				Query lQuery = ghibSession.createSQLQuery(sb.toString());
				lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
				lQuery.setString("fromDate", strFromDatePassed);
				lQuery.setString("toDate", strToDatePassed);
				//lQuery.setString("fromDate", strFromDatePassed);
				lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

				tempEmpDetails = lQuery.list();
				
				empDetails.addAll(tempEmpDetails);
			
				lstDcpsEmpIds = null;
				
				lstDcpsEmpIds = new ArrayList();
				
				lQuery = null;
				
			}
          }
          
          StringBuffer sb = new StringBuffer();
            sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end as payble_year, ");
			sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
			sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
			sb.append("tr.IS_MISSING_CREDIT, TR.IS_CHALLAN, ");
			if(yearId.equalsIgnoreCase("25"))
			{
				sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd'),b.end_date ");
			}
			else
			{
				sb.append("nvl(contri_year.MST_DCPS_CONTRIB_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd'),b.end_date ");
			}
			sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
			sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
			sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
			sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
			sb.append(" inner join HR_EIS_EMP_MST empMst on empMst.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			if(yearId.equalsIgnoreCase("25"))
			{
				sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
			}
			else
			{
				sb.append("left outer join MST_DCPS_CONTRIB_TERMINATION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
			}
			sb.append(" left outer join (SELECT end.emp_id,end.created_date,end.end_date FROM HR_EIS_EMP_END_DATE end inner join (select max(CREATED_DATE) created_dt,emp_id  as empid FROM HR_EIS_EMP_END_DATE group by emp_id ) a  on a.created_dt=end.created_date and a.empid=end.emp_id) b on empMst.EMP_ID = b.emp_id ");
			sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
			sb.append("and trim(emp.dcps_id) <>'-1' and vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN :fromDate AND :toDate ");
			sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
			sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
			//sb.append("AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') and (IS_DEPUTATION is null or IS_DEPUTATION <> 'Y') ");
			sb.append("AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') ");
			
			sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");
			
		
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
			lQuery.setString("fromDate", strFromDatePassed);
			lQuery.setString("toDate", strToDatePassed);
			//lQuery.setString("fromDate", strFromDatePassed);
			lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

			tempEmpDetails = lQuery.list();
			
			
			gLogger.info("Missing credit query is **************"+sb.toString());
			
			empDetails.addAll(tempEmpDetails);
			
			lstDcpsEmpIds = null;
			
			lstDcpsEmpIds = new ArrayList();
			
			lQuery = null;
			
			return empDetails;
	}
  public List getEmpListForMissingCreditBkp2018(String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,String strFromDatePassed, String strToDatePassed ,String yearId){
	  List empDetails = new ArrayList();
		List tempEmpDetails = null;
		List lstDcpsEmpId = new ArrayList();

		List lstDcpsEmpIds = new ArrayList();

		String[] dcpsEmpId = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.split(",");

          for(int i=0 ; i< dcpsEmpId.length; i++){
				
			
			if(i==0 || i%4000!=0){
				lstDcpsEmpIds.add(dcpsEmpId[i]);
			}
			else
			{
				lstDcpsEmpIds.add(dcpsEmpId[i]);
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end as payble_year, ");
				sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
				sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
				sb.append("tr.IS_MISSING_CREDIT, TR.IS_CHALLAN, ");
				sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0) ");
				sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
				sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
				sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
				sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
				sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
				sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
				sb.append("and trim(emp.dcps_id) <>'-1' and vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN :fromDate AND :toDate ");
				sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
				sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
				sb.append("AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') and (IS_DEPUTATION is null or IS_DEPUTATION <> 'Y') ");
				sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");
				
				

				Query lQuery = ghibSession.createSQLQuery(sb.toString());
				lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
				lQuery.setString("fromDate", strFromDatePassed);
				lQuery.setString("toDate", strToDatePassed);
				lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

				tempEmpDetails = lQuery.list();
				
				empDetails.addAll(tempEmpDetails);
			
				lstDcpsEmpIds = null;
				
				lstDcpsEmpIds = new ArrayList();
				
				lQuery = null;
				
			}
          }
          
          StringBuffer sb = new StringBuffer();
            sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end as payble_year, ");
			sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
			sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
			sb.append("tr.IS_MISSING_CREDIT, TR.IS_CHALLAN, ");
			sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0) ");
			sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
			sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
			sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
			sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
			sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
			sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
			sb.append("and trim(emp.dcps_id) <>'-1' and vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN :fromDate AND :toDate ");
			sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
			sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
			sb.append("AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') and (IS_DEPUTATION is null or IS_DEPUTATION <> 'Y') ");
			sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");
			
		
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
			lQuery.setString("fromDate", strFromDatePassed);
			lQuery.setString("toDate", strToDatePassed);
			lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

			tempEmpDetails = lQuery.list();
			
			
			gLogger.info("Missing credit query is **************"+sb.toString());
			
			empDetails.addAll(tempEmpDetails);
			
			lstDcpsEmpIds = null;
			
			lstDcpsEmpIds = new ArrayList();
			
			lQuery = null;
			
			return empDetails;
	}
  
  public List getEmpListForRegularContiBkp2018(String dcpsEmpIds,String strFromDatePassed, String strToDatePassed,String yearId){
		List empDetails = new ArrayList();
		List tempEmpDetails = null;
		List lstDcpsEmpId = new ArrayList();

		List lstDcpsEmpIds = new ArrayList();
		
		String[] dcpsEmpId = dcpsEmpIds.split(",");
	
		for(int i=0 ; i< dcpsEmpId.length; i++){
				
			
			if(i==0 || i%4000!=0){
				lstDcpsEmpIds.add(dcpsEmpId[i]);
			}
			else{
				
				lstDcpsEmpIds.add(dcpsEmpId[i]);
				StringBuffer sb = new StringBuffer();
				
				sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, (case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end) as payble_year, ");
				sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
				sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury ,");
				sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0) ");
				sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
				sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
				sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
				sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
				sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
				sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
				sb.append("and trim(emp.dcps_id) <>'-1' and vd.VOUCHER_DATE BETWEEN :fromDate AND :toDate ");
				sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
				sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' and VD.voucher_status=1 and tr.reg_status=1 ");
				sb.append("AND TR.IS_MISSING_CREDIT IS NULL AND TR.IS_CHALLAN IS NULL ");
				sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");

				Query lQuery = ghibSession.createSQLQuery(sb.toString());
				lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
				lQuery.setString("fromDate", strFromDatePassed);
				lQuery.setString("toDate", strToDatePassed);
				lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

				tempEmpDetails = lQuery.list();
				
				empDetails.addAll(tempEmpDetails);
			
				lstDcpsEmpIds = null;
				
				lstDcpsEmpIds = new ArrayList();
				
				lQuery = null;
			}
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, (case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end) as payble_year, ");
		sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
		sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
		sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0) ");
		sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
		sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
		sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
		sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
		sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
		sb.append("and trim(emp.dcps_id) <>'-1' and vd.VOUCHER_DATE BETWEEN :fromDate AND :toDate ");
		sb.append(" AND tr.startDATE BETWEEN '2005-04-01' AND '2015-03-31' ");//////$t27Sept2022
		sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
		sb.append("AND TR.IS_MISSING_CREDIT IS NULL AND TR.IS_CHALLAN IS NULL ");
		sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");

		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
		lQuery.setString("fromDate", strFromDatePassed);
		lQuery.setString("toDate", strToDatePassed);
		lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

		tempEmpDetails = lQuery.list();
		gLogger.info("Third query is **************"+sb.toString());
		
		empDetails.addAll(tempEmpDetails);
		
		lstDcpsEmpIds = null;
		
		lstDcpsEmpIds = new ArrayList();
		
		lQuery = null;
		
		return empDetails;
	}
	

  public List getEmpListForRegularConti(String dcpsEmpIds,String strFromDatePassed, String strToDatePassed,String yearId){
		List empDetails = new ArrayList();
		List tempEmpDetails = null;
		List lstDcpsEmpId = new ArrayList();

		List lstDcpsEmpIds = new ArrayList();
		
		String[] dcpsEmpId = dcpsEmpIds.split(",");
	
		for(int i=0 ; i< dcpsEmpId.length; i++){
				
			
			if(i==0 || i%4000!=0){
				lstDcpsEmpIds.add(dcpsEmpId[i]);
			}
			else{
				
				lstDcpsEmpIds.add(dcpsEmpId[i]);
				StringBuffer sb = new StringBuffer();
				
				sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, (case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end) as payble_year, ");
				sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
				sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury ,");
				if(yearId.equalsIgnoreCase("25"))
				{
					sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd') ,b.end_date ");
				}
				else
				{
					sb.append("nvl(contri_year.MST_DCPS_CONTRIB_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd'),b.end_date  ");
				}
				sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
				sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
				sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
				sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
				sb.append(" inner join HR_EIS_EMP_MST empMst on empMst.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
				if(yearId.equalsIgnoreCase("25"))
				{
					sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
				}
				else
				{
					sb.append("left outer join MST_DCPS_CONTRIB_TERMINATION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
				}
				sb.append(" left outer join (SELECT end.emp_id,end.created_date,end.end_date FROM HR_EIS_EMP_END_DATE end inner join (select max(CREATED_DATE) created_dt,emp_id  as empid FROM HR_EIS_EMP_END_DATE group by emp_id ) a  on a.created_dt=end.created_date and a.empid=end.emp_id) b on empMst.EMP_ID = b.emp_id ");
				sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
				sb.append("and trim(emp.dcps_id) <>'-1' and vd.VOUCHER_DATE BETWEEN :fromDate AND :toDate ");
				sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
				sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' and VD.voucher_status=1 and tr.reg_status=1 ");
				sb.append("AND TR.IS_MISSING_CREDIT IS NULL AND TR.IS_CHALLAN IS NULL ");
			
				sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");

				gLogger.info("query is for regular **************"+sb.toString());
				
				Query lQuery = ghibSession.createSQLQuery(sb.toString());
				lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
				lQuery.setString("fromDate", strFromDatePassed);
				lQuery.setString("toDate", strToDatePassed);
				lQuery.setString("fromDate", strFromDatePassed);
				lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

				tempEmpDetails = lQuery.list();
				
				empDetails.addAll(tempEmpDetails);
			
				lstDcpsEmpIds = null;
				
				lstDcpsEmpIds = new ArrayList();
				
				lQuery = null;
			}
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, (case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end) as payble_year, ");
		sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
		sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
		if(yearId.equalsIgnoreCase("25"))
		{
			sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd'),b.end_date ");
		}
		else
		{
			sb.append("nvl(contri_year.MST_DCPS_CONTRIB_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0),to_char(emp.SUPER_ANN_DATE,'yyyy-MM-dd') ,b.end_date ");
		}
		
		sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
		sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
		sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
		sb.append(" inner join HR_EIS_EMP_MST empMst on empMst.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
		if(yearId.equalsIgnoreCase("25"))
		{
			sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
		}
		else
		{
			sb.append("left outer join MST_DCPS_CONTRIB_TERMINATION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
		}
		sb.append(" left outer join (SELECT end.emp_id,end.created_date,end.end_date FROM HR_EIS_EMP_END_DATE end inner join (select max(CREATED_DATE) created_dt,emp_id  as empid FROM HR_EIS_EMP_END_DATE group by emp_id ) a  on a.created_dt=end.created_date and a.empid=end.emp_id) b on empMst.EMP_ID = b.emp_id ");
		sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
		sb.append("and trim(emp.dcps_id) <>'-1' and vd.VOUCHER_DATE BETWEEN :fromDate AND :toDate ");
		sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
		sb.append("AND TR.IS_MISSING_CREDIT IS NULL AND TR.IS_CHALLAN IS NULL ");

		sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");

		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
		lQuery.setString("fromDate", strFromDatePassed);
		lQuery.setString("toDate", strToDatePassed);
		lQuery.setString("fromDate", strFromDatePassed);
		lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpIds);

		tempEmpDetails = lQuery.list();
		gLogger.info("Third query is **************"+sb.toString());
		
		empDetails.addAll(tempEmpDetails);
		
		lstDcpsEmpIds = null;
		
		lstDcpsEmpIds = new ArrayList();
		
		lQuery = null;
		
		return empDetails;
	}
	

  
  
  
  public List getEmpListForMissingCreditTest(String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,String strFromDatePassed, String strToDatePassed ,String yearId){
		List empDetails = null;
		StringBuffer sb = new StringBuffer();

		List lstDcpsEmpId = new ArrayList();

		String[] dcpsEmpId = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.split(",");

		for(int i=0 ; i< dcpsEmpId.length; i++){
			lstDcpsEmpId.add(dcpsEmpId[i]);
			
		}

		sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end as payble_year, ");
		sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
		sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
		sb.append("tr.IS_MISSING_CREDIT, TR.IS_CHALLAN, ");
		sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0) ");
		sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
		sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
		sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
		sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
		sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
		sb.append("and trim(emp.dcps_id) <>'-1' and vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN :fromDate AND :toDate ");
		sb.append(" AND tr.startDATE BETWEEN '2005-04-01'AND '2015-03-31' ");//////$t27Sept2022
		sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
		sb.append("AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') ");
		if(listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc!=null && !listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.trim().equalsIgnoreCase(""))
		{
			sb.append("and emp.DCPS_EMP_ID in (:dcpsEmpIds) ");
		}
		

		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
		lQuery.setString("fromDate", strFromDatePassed);
		lQuery.setString("toDate", strToDatePassed);
		if(listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc!=null && !listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.trim().equalsIgnoreCase(""))
		{
			lQuery.setParameterList("dcpsEmpIds", lstDcpsEmpId);
		}
		
		
	
		

		empDetails = lQuery.list();
		
		lQuery = null;

		return empDetails;
	}

  
  
  

 
  
	public List getInterestRatesForVoucherNo(Long finYearId){
		List ratesApplicable = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select INTEREST, EFFECTIVE_FROM,APPLICABLE_TO from mst_dcps_interest_rate where FIN_YEAR_ID = :finYearId");
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setLong("finYearId", finYearId);

		ratesApplicable = lQuery.list();
		lQuery = null;
		return ratesApplicable;
	}

 
  
	public List getInterestRatesForMissingCredit(String strToDatePassed,String voucherDate,Long yearId){

		List ratesApplicables = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT interest, effective_from,applicable_to,fin_year_id FROM mst_dcps_interest_rate ");
		sb.append("where fin_year_id<=:yearId and ((effective_from>=:voucherDate and applicable_to<=:strToDatePassed) or (:voucherDate BETWEEN EFFECTIVE_FROM AND APPLICABLE_TO) or (:strToDatePassed>EFFECTIVE_FROM and applicable_to is null)) ");
		sb.append("order by effective_from");
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setLong("yearId", yearId);
		lQuery.setParameter("voucherDate", voucherDate);
		lQuery.setParameter("strToDatePassed", strToDatePassed);
		ratesApplicables = lQuery.list();
		lQuery = null;
		return ratesApplicables;
	}

  
  
  public List getArrearsDtlsForAllEmps(String listAllEmpsDCPSEmpIdsForIntrstCalc,Long yearId) {

		List tempEmpDetails = null;
		
		List listArrearsDtlsForGivenEmp = new ArrayList();

		List<Long> lstDcpsEmpId = new ArrayList<Long>();

		String[] dcpsEmpId = listAllEmpsDCPSEmpIdsForIntrstCalc.split(",");

		for(int i=0 ; i< dcpsEmpId.length; i++){
			if(i==0 || i%4000!=0){
				lstDcpsEmpId.add(Long.parseLong(dcpsEmpId[i].toString()));
			}
			else{
				lstDcpsEmpId.add(Long.parseLong(dcpsEmpId[i].toString()));
				StringBuilder lSBQuery = new StringBuilder();

				Query lQuery = null;

				lSBQuery.append(" select RL.yearlyAmount,RL.dcpsEmpId,RL.voucherNo,RL.voucherDate,FY.finYearCode,EM.payCommission,EM.dcpsId from RltDcpsSixPCYearly RL,MstEmp EM,SgvcFinYearMst FY");
				lSBQuery.append(" where ");
				//lSBQuery.append(" RL.dcpsEmpId = :dcpsEmpId");
				lSBQuery.append(" RL.dcpsEmpId in (:dcpsEmpIdList)");
				lSBQuery.append(" and RL.finYearId = :yearId ");
				lSBQuery.append(" and RL.dcpsEmpId = EM.dcpsEmpId ");
				lSBQuery.append(" and RL.finYearId = FY.finYearId ");
				lSBQuery.append(" and RL.statusFlag = 'A'");

				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setLong("yearId", yearId);
				lQuery.setParameterList("dcpsEmpIdList", lstDcpsEmpId);

				tempEmpDetails = lQuery.list();
				
				listArrearsDtlsForGivenEmp.addAll(tempEmpDetails);
				
				lstDcpsEmpId = null;
				
				lstDcpsEmpId = new ArrayList();
			}
		}

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" select RL.yearlyAmount,RL.dcpsEmpId,RL.voucherNo,RL.voucherDate,FY.finYearCode,EM.payCommission,EM.dcpsId from RltDcpsSixPCYearly RL,MstEmp EM,SgvcFinYearMst FY");
		lSBQuery.append(" where ");
		//lSBQuery.append(" RL.dcpsEmpId = :dcpsEmpId");
		lSBQuery.append(" RL.dcpsEmpId in (:dcpsEmpIdList)");
		lSBQuery.append(" and RL.finYearId = :yearId ");
		lSBQuery.append(" and RL.dcpsEmpId = EM.dcpsEmpId ");
		lSBQuery.append(" and RL.finYearId = FY.finYearId ");
		lSBQuery.append(" and RL.statusFlag = 'A'");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setLong("yearId", yearId);
		lQuery.setParameterList("dcpsEmpIdList", lstDcpsEmpId);

		tempEmpDetails = lQuery.list();
		
		listArrearsDtlsForGivenEmp.addAll(tempEmpDetails);
		
		
		lstDcpsEmpId = null;
		
		lstDcpsEmpId = new ArrayList();
		return listArrearsDtlsForGivenEmp;
	}

 
  
  public MstDcpsContributionYearly getContriYearlyVOForYear(String dcpsId,
			Long previousFinYearId){
		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;

		Query lQuery = null;

		lSBQuery.append(" SELECT YC FROM MstDcpsContributionYearly YC,MstEmp EM WHERE YC.dcpsId = EM.dcpsId AND EM.dcpsId = :dcpsId AND YC.yearId = :yearId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);
		lQuery.setParameter("yearId", previousFinYearId);

		lObjMstDcpsContributionYearly = (MstDcpsContributionYearly) lQuery
		.uniqueResult();

		return lObjMstDcpsContributionYearly;
	}

 
  
  public void updateGeneratedId(Long dcpsContributionMonthlyIdForUpdate,String tableName){
		Session session = getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" UPDATE INT_TABLE_SEQ_MST SET GENERATED_ID=:generatedId WHERE UPPER(TABLE_NAME) = :tableName");

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("generatedId", dcpsContributionMonthlyIdForUpdate);
		lQuery.setParameter("tableName", tableName);

		int status = lQuery.executeUpdate();
	
	}

  
  
  public Long getNextSeqNum(String generatePkForMonthly){


		Long genId=0l;
		List lGenIdForMonthly=null;

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT GENERATED_ID FROM INT_TABLE_SEQ_MST where UPPER(TABLE_NAME)= :generatePkForMonthly ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("generatePkForMonthly", generatePkForMonthly);

		lGenIdForMonthly = lQuery.list();
		if(lGenIdForMonthly.size()>0 && lGenIdForMonthly.get(0)!=null){
			genId=Long.parseLong(lGenIdForMonthly.get(0).toString());
		}
   
		return genId;


	}
  public String getEndDate(String dcpsId){


		String endDate=null;
		List l=null;

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT en.END_DATE,en.CREATED_DATE,en.EMP_ID FROM  mst_dcps_emp emp inner join hr_eis_emp_mst mst on mst.EMP_MPG_ID=emp.ORG_EMP_MST_ID inner join HR_EIS_EMP_END_DATE en on en.EMP_ID=mst.EMP_ID where emp.dcps_id=:dcpsId  order by en.CREATED_DATE desc ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);
		
		l = lQuery.list();
		if(l!=null && l.size()>0 && l.get(0)!=null){
			Object[] obj=(Object[]) l.get(0);
			endDate=obj[0].toString();
			
		}
 
		return endDate;


	}
  public List getInterestRatesForTier2(String fromDate,String toDate){

		List ratesApplicables = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT interest, effective_from,applicable_to,fin_year_id FROM mst_dcps_interest_rate ");
		sb.append("where  and ((:fromDate BETWEEN EFFECTIVE_FROM AND APPLICABLE_TO) or (:toDate BETWEEN EFFECTIVE_FROM AND APPLICABLE_TO)) ");
		sb.append("order by effective_from");
		Query lQuery = ghibSession.createSQLQuery(sb.toString());

		lQuery.setParameter("fromDate", fromDate);
		lQuery.setParameter("toDate", toDate);
		ratesApplicables = lQuery.list();
		lQuery = null;
		return ratesApplicables;
	}
  public void updateSixPCAmountYearly(String dcpsId, Double interestForMonthlyContri, Double yearlyAmtSixpc, Long yearId) {
      gLogger.info("updateSixPCAmountYearly **************");
      Session session = this.getSession();
      new ArrayList();
      new ArrayList();
      double tier2 = 0.0D;
      String tier2STR = "";
      String OPEN_EMPL_CONTRIB = "";
      String OPEN_NET = "";
      String OPEN_EMPLr_CONTRIB = "";
      String FIN_YEAR = "";
      String StrSqlQuery = "";
      String StrSqlQuery1 = "";
      StringBuilder lSQuery = new StringBuilder();
      lSQuery.append(" SELECT tr.FIN_YEAR, tr.TIER2 FROM TEMPEMPR3 tr INNER join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC= tr.FIN_YEAR  ");
      lSQuery.append(" WHERE tr.EMP_ID_NO = '" + dcpsId + "' and fin.FIN_YEAR_ID = '" + yearId + "' ");
      StrSqlQuery = lSQuery.toString();
      SQLQuery lQuery0 = this.ghibSession.createSQLQuery(StrSqlQuery.toString());
      List tempList = lQuery0.list();
      if (tempList != null && tempList.size() > 0) {
         System.out.println("IN F1 IF................ ");
         if (yearId == 22L) {
            FIN_YEAR = "2009-2010";
         } else if (yearId == 23L) {
            FIN_YEAR = "2010-2011";
         } else if (yearId == 24L) {
            FIN_YEAR = "2011-2012";
         }

         System.out.println("IN F2 IF................ ");
         Object[] obj0 = (Object[])tempList.get(0);
         tier2STR = obj0[1].toString();
         System.out.println("tier2STR" + tier2STR);
         tier2 = Double.parseDouble(tier2STR);
         System.out.println("tier2STR" + tier2);
         StringBuilder lSQuery1 = new StringBuilder();
         long yearId1 = yearId + 1L;
         lSQuery1.append(" SELECT tr.FIN_YEAR, tr.OPEN_EMPL_CONTRIB,tr.OPEN_NET,tr.OPEN_EMPLR_CONTRIB FROM TEMPEMPR3 tr INNER join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC= tr.FIN_YEAR  ");
         lSQuery1.append(" WHERE tr.EMP_ID_NO = '" + dcpsId + "' and fin.FIN_YEAR_ID = '" + yearId1 + "' ");
         StrSqlQuery1 = lSQuery1.toString();
         SQLQuery lQuery01 = this.ghibSession.createSQLQuery(StrSqlQuery1.toString());
         List tempList1 = lQuery01.list();
         StringBuilder lSBQuery;
         SQLQuery lQuery;
         Object[] obj01;
         double OPEN_EMPL_CONTRIB1;
         double OPEN_NET1;
         double OPEN_EMPLr_CONTRIB1;
         StringBuilder lSBQuery1;
         SQLQuery lQuery1;
         if (tempList.size() > 0 && tier2 == 0.0D && FIN_YEAR == "2009-2010") {
            lSBQuery = new StringBuilder();
            lSBQuery.append(" UPDATE TEMPEMPR3 SET tier2 ='" + yearlyAmtSixpc + "' ,INT_TIER2_CONTRIB = '" + interestForMonthlyContri + "' , ENTRY_DT=sysdate  WHERE EMP_ID_NO='" + dcpsId + "' AND  FIN_YEAR='" + FIN_YEAR + "'  ");
            lQuery = session.createSQLQuery(lSBQuery.toString());
            gLogger.info("Third query is **************" + lQuery.executeUpdate());
            if (tempList1 != null && tempList1.size() > 0) {
               obj01 = (Object[])tempList1.get(0);
               OPEN_EMPL_CONTRIB = obj01[1].toString();
               OPEN_NET = obj01[2].toString();
               OPEN_EMPLr_CONTRIB = obj01[3].toString();
               OPEN_EMPL_CONTRIB1 = Double.parseDouble(OPEN_EMPL_CONTRIB);
               OPEN_NET1 = Double.parseDouble(OPEN_NET);
               OPEN_EMPLr_CONTRIB1 = Double.parseDouble(OPEN_EMPLr_CONTRIB);
               OPEN_EMPL_CONTRIB1 = OPEN_EMPL_CONTRIB1 + yearlyAmtSixpc + interestForMonthlyContri;
               OPEN_NET1 = OPEN_EMPL_CONTRIB1 + OPEN_EMPLr_CONTRIB1;
               lSBQuery1 = new StringBuilder();
               lSBQuery1.append(" UPDATE TEMPEMPR3 SET OPEN_EMPL_CONTRIB ='" + OPEN_EMPL_CONTRIB1 + "' ,OPEN_NET = '" + OPEN_NET1 + "' , ENTRY_DT=sysdate  WHERE EMP_ID_NO='" + dcpsId + "' AND  FIN_YEAR='2010-2011'  ");
               lQuery1 = session.createSQLQuery(lSBQuery1.toString());
               gLogger.info("Third query is **************" + lQuery1.executeUpdate());
            }
         }

         if (tempList.size() > 0 && tier2 == 0.0D && FIN_YEAR == "2010-2011") {
            lSBQuery = new StringBuilder();
            lSBQuery.append(" UPDATE TEMPEMPR3 SET tier2 ='" + yearlyAmtSixpc + "' ,INT_TIER2_CONTRIB = '" + interestForMonthlyContri + "' , ENTRY_DT=sysdate  WHERE EMP_ID_NO='" + dcpsId + "' AND  FIN_YEAR='" + FIN_YEAR + "'  ");
            lQuery = session.createSQLQuery(lSBQuery.toString());
            gLogger.info("Third query is **************" + lQuery.executeUpdate());
            if (tempList1 != null && tempList1.size() > 0) {
               obj01 = (Object[])tempList1.get(0);
               OPEN_EMPL_CONTRIB = obj01[1].toString();
               OPEN_NET = obj01[2].toString();
               OPEN_EMPLr_CONTRIB = obj01[3].toString();
               OPEN_EMPL_CONTRIB1 = Double.parseDouble(OPEN_EMPL_CONTRIB);
               OPEN_NET1 = Double.parseDouble(OPEN_NET);
               OPEN_EMPLr_CONTRIB1 = Double.parseDouble(OPEN_EMPLr_CONTRIB);
               OPEN_EMPL_CONTRIB1 = OPEN_EMPL_CONTRIB1 + yearlyAmtSixpc + interestForMonthlyContri;
               OPEN_NET1 = OPEN_EMPL_CONTRIB1 + OPEN_EMPLr_CONTRIB1;
               lSBQuery1 = new StringBuilder();
               lSBQuery1.append(" UPDATE TEMPEMPR3 SET OPEN_EMPL_CONTRIB ='" + OPEN_EMPL_CONTRIB1 + "' ,OPEN_NET = '" + OPEN_NET1 + "' , ENTRY_DT=sysdate  WHERE EMP_ID_NO='" + dcpsId + "' AND  FIN_YEAR='2011-2012'  ");
               lQuery1 = session.createSQLQuery(lSBQuery1.toString());
               gLogger.info("Third query is **************" + lQuery1.executeUpdate());
            }
         }

         if (tempList.size() > 0 && tier2 == 0.0D && FIN_YEAR == "2011-2012") {
            lSBQuery = new StringBuilder();
            lSBQuery.append(" UPDATE TEMPEMPR3 SET tier2 ='" + yearlyAmtSixpc + "' ,INT_TIER2_CONTRIB = '" + interestForMonthlyContri + "' , ENTRY_DT=sysdate  WHERE EMP_ID_NO='" + dcpsId + "' AND  FIN_YEAR='" + FIN_YEAR + "'  ");
            lQuery = session.createSQLQuery(lSBQuery.toString());
            gLogger.info("Third query is **************" + lQuery.executeUpdate());
         }
      }

   }

   public void updateSixPCAmountMonthly(String dcpsId, Double interestForMonthlyContri, Double yearlyAmtSixpc, Long voucherMonth, Long voucherYear, Long yearId, Date date) {
      gLogger.info("updateSixPCAmountMonthly **************");
      Session session = this.getSession();
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      double cur_net = 0.0D;
      double cur_t2_amount = 0.0D;
      double net_amt = 0.0D;
      String cur_net1 = "";
      String cur_t2_amount1 = "";
      String net_amt1 = "";
      String FIN_YEAR = "";
      String PAY_MONTH = "";
      String PAY_YEAR = "";
      String TRY_CD = "";
      String DDO_CD = "";
      String EMP_ID_NO = "";
      Integer VORC_NO = 0;
      Double CUR_EMPL_CONTRIB = 0.0D;
      Double CUR_EMPLR_CONTRIB = 0.0D;
      Double CUR_TIER2_CONTRIB = 0.0D;
      Double CUR_NET = 0.0D;
      Double WITHDRAWAL = 0.0D;
      Double REFUND = 0.0D;
      String CLOS_EMPL_CONTRIB = "";
      String CLOS_EMPLR_CONTRIB = "";
      String CLOS_TIER2_CONTRIB = "";
      String CLOS_NET = "";
      String REMARKS = "";
      String STATUS = "";
      String USER_ID = "";
      String StrSqlQuery = "";
      lSBQuery.append(" SELECT tr.FIN_YEAR,tr.CUR_TIER2_CONTRIB,tr.CUR_NET FROM TEMPR3 tr INNER join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC= tr.FIN_YEAR ");
      lSBQuery.append(" WHERE tr.EMP_ID_NO = '" + dcpsId + "' and fin.FIN_YEAR_ID = '" + yearId + "' and tr.PAY_MONTH=6  ");
      StrSqlQuery = lSBQuery.toString();
      SQLQuery lQuery0 = this.ghibSession.createSQLQuery(StrSqlQuery.toString());
      List tempList = lQuery0.list();
      if (tempList != null && tempList.size() > 0) {
         System.out.println("IN F1 IF................ ");
         tempList = lQuery0.list();
         if (yearId == 22L) {
            FIN_YEAR = "2009-2010";
         } else if (yearId == 23L) {
            FIN_YEAR = "2010-2011";
         } else if (yearId == 24L) {
            FIN_YEAR = "2011-2012";
         }

         System.out.println("IN F2 IF................ ");
         Object[] obj0 = (Object[])tempList.get(0);
         cur_t2_amount1 = obj0[1].toString();
         cur_net1 = obj0[2].toString();
         System.out.println("cur_t2_amount1" + cur_t2_amount1 + "cur_net1" + cur_net1);
         cur_t2_amount = Double.parseDouble(cur_t2_amount1);
         cur_net = Double.parseDouble(cur_net1);
         net_amt = (double)Math.round(cur_net + yearlyAmtSixpc);
         System.out.println("net_amt" + net_amt + "FIN_YEAR" + FIN_YEAR + "cur_t2_amount1" + cur_t2_amount1 + "cur_net1" + cur_net1);
         if (tempList.size() > 0 && cur_t2_amount == 0.0D) {
            System.out.println("net_amt" + net_amt + "FIN_YEAR" + FIN_YEAR + "cur_t2_amount1" + cur_t2_amount1 + "cur_net1" + cur_net1);
            StringBuilder lQ = new StringBuilder();
            lQ.append(" UPDATE TEMPR3 SET CUR_TIER2_CONTRIB ='" + yearlyAmtSixpc + "'  ,cur_net ='" + net_amt + "'  ,ENTRY_DT =sysdate WHERE EMP_ID_NO='" + dcpsId + "'  and FIN_YEAR='" + FIN_YEAR + "'  and PAY_MONTH='6' ");
            Query lQy = session.createSQLQuery(lQ.toString());
            gLogger.info("Third query is **************" + lQy.executeUpdate());
         }
      }

   }

   public String getAllArrearsEmployee(Long treasuryCode, Long yearId) {
      List listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
      StringBuilder lSBQuery = new StringBuilder();
      Query lQuery = null;
      lSBQuery.append(" SELECT  DISTINCT mst.DCPS_EMP_ID FROM TEMPEMPR3 tr ");
      lSBQuery.append(" INNER join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC= tr.FIN_YEAR  ");
      lSBQuery.append(" INNER join MST_DCPS_EMP mst on mst.dcps_id= tr.EMP_ID_NO  ");
      lSBQuery.append(" WHERE fin.FIN_YEAR_ID='" + yearId + "' and  tr.TIER2 <> 0 ");
      lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
      listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lQuery.list();
      String dcpsEmpIds = "";

      for(int i = 0; i < listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.size(); ++i) {
         if (i != 0) {
            dcpsEmpIds = dcpsEmpIds + "," + listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.get(i).toString();
         } else {
            dcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.get(i).toString();
         }
      }

      lQuery = null;
      listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
      gLogger.info("First Query is ***" + lSBQuery.toString());
      gLogger.info("Employee lis is  ***" + dcpsEmpIds);
      return dcpsEmpIds;
   }

   public List getArrearsDtlsForAllEmpsOld(Long treasuryCode, Long yearId) {
      List tempEmpDetails = null;
      List listArrearsDtlsForGivenEmp = new ArrayList();
      StringBuilder lSBQuery = new StringBuilder();
      Query lQuery = null;
      lSBQuery.append(" select RL.yearlyAmount,RL.dcpsEmpId,RL.voucherNo,RL.voucherDate,FY.finYearCode,EM.payCommission,EM.dcpsId from RltDcpsSixPCYearly RL,MstEmp EM,SgvcFinYearMst FY");
      lSBQuery.append(" where ");
      lSBQuery.append("  RL.finYearId = :yearId ");
      lSBQuery.append(" and RL.dcpsEmpId = EM.dcpsEmpId ");
      lSBQuery.append(" and RL.finYearId = FY.finYearId ");
      lSBQuery.append(" and RL.statusFlag = 'A'");
      lSBQuery.append(" AND substr(EM.ddoCode,1,2)=substr(:treasuryCode,1,2) ");
      lSBQuery.append(" and EM.dcpsId NOT IN ('16105004476SRTM7501G') ");
      lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setLong("yearId", yearId);
      lQuery.setLong("treasuryCode", treasuryCode);
      tempEmpDetails = lQuery.list();
      listArrearsDtlsForGivenEmp.addAll(tempEmpDetails);
      return listArrearsDtlsForGivenEmp;
   }

}