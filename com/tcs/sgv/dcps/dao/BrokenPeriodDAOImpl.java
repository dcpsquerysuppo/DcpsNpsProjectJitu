/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 14, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstBrokenPeriodPay;
import com.tcs.sgv.eis.valueobject.HrEisEmpMst;


/**
 * Class Description -
 * 
 * 
 * @author Kapil Devani
 * @version 0.1
 * @since JDK 5.0 Sep 14, 2011
 */

@SuppressWarnings({"unchecked","unused"})
public class BrokenPeriodDAOImpl extends GenericDaoHibernateImpl implements BrokenPeriodDAO
{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public BrokenPeriodDAOImpl(Class type, SessionFactory sessionFactory)
	{
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = getSession();
	}

	public List SearchEmployeeWithName(String lStrSearchName, long locId) throws Exception
	{

		List lLstEmployee = null;
		StringBuilder lSBQuery = new StringBuilder();
		List lLstSingleEmployee = null;

		Query lQuery = null;

		//lSBQuery.append("SELECT * FROM org_emp_mst WHERE UPPER(CONCAT(CONCAT(CONCAT(CONCAT(emp_fname,' '),emp_mname),' '),emp_lname)) LIKE '%" + lStrSearchName + "%'");
		/*lSBQuery.append("SELECT empMst.* FROM ORG_POST_MST postMst , ORG_USERPOST_RLT postRlt, ORG_EMP_MST empMst ");
		lSBQuery.append("where postMst.POST_ID = postRlt.POST_ID ");
		lSBQuery.append("and postMst.LOCATION_CODE = "+locId+" ");
		lSBQuery.append("and empMst.USER_ID = postRlt.USER_ID ");
		lSBQuery.append("and postMst.ACTIVATE_FLAG = 1 ");
		lSBQuery.append("and postRlt.ACTIVATE_FLAG = 1 ");
		lSBQuery.append("and UPPER(CONCAT(CONCAT(CONCAT(CONCAT(emp_fname,' '),emp_mname),' '),emp_lname)) LIKE '%" + lStrSearchName + "%'");*/
		
		lSBQuery.append(" SELECT empMst.* FROM ORG_POST_MST postMst inner join ORG_USERPOST_RLT postRlt on postMst.POST_ID = postRlt.POST_ID  ");
		lSBQuery.append(" inner join ORG_EMP_MST empMst on empMst.USER_ID = postRlt.USER_ID  ");
		lSBQuery.append(" where postMst.LOCATION_CODE = "+locId+" ");
		lSBQuery.append(" and postMst.ACTIVATE_FLAG = 1 ");
		lSBQuery.append(" and postRlt.ACTIVATE_FLAG = 1 ");
		lSBQuery.append(" and UPPER(CONCAT(CONCAT(CONCAT(CONCAT(emp_fname,' '),emp_mname),' '),emp_lname)) LIKE '%" + lStrSearchName + "%'");


		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lLstEmployee = lQuery.list();

		if (lLstEmployee.size() != 0)
		{
			Object[] obj = (Object[]) lLstEmployee.get(0);
			lLstSingleEmployee = Arrays.asList(obj);
		}
		return lLstSingleEmployee;
	}

	public String getDesignationName(String lStrEmpId) throws Exception
	{
		List lLstDesignation = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT dm.dsgn_name FROM org_designation_mst dm, org_post_mst pm, org_userpost_rlt up, org_emp_mst em WHERE em.user_id = up.user_id AND up.post_id = pm.post_id AND pm.dsgn_code=dm.dsgn_code AND em.emp_id = " + lStrEmpId);

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lLstDesignation = lQuery.list();
		String lStrDsgnName = lLstDesignation.get(0).toString();

		return lStrDsgnName;
	}

	public String getOfficeName(String lStrEmpId) throws Exception
	{
		List lLstOfficeName = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT of.off_name FROM mst_dcps_ddo_office of, org_userpost_rlt up, org_emp_mst em,hr_pay_officepost_mpg hr WHERE em.user_id = up.user_id AND up.post_id = hr.post_id AND hr.office_id = of.DCPS_DDO_OFFICE_MST_ID AND em.emp_id = " + lStrEmpId);

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lLstOfficeName = lQuery.list();
		String lStrOfficeName = lLstOfficeName.get(0).toString();

		return lStrOfficeName;
	}

	public String getGPFNo(String lStrEmpId) throws Exception
	{
		List<String> lLstGpfNo = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT hr.gpf_acc_no FROM  org_emp_mst em,hr_pay_gpf_details hr WHERE em.user_id = hr.user_id AND em.emp_id = " + Long.valueOf(lStrEmpId));

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lLstGpfNo = lQuery.list();
		String lStrGPFNo = lLstGpfNo.get(0).toString();

		return lStrGPFNo;
	}

	public HrEisEmpMst getHrEisEmpMstVOForEmpMpgId(Long empMpgId)
	{

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM HrEisEmpMst HR");
		lSBQuery.append(" WHERE HR.orgEmpMst.empId = :empMpgId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empMpgId", empMpgId);

		HrEisEmpMst lObjHrEisEmpVO = (HrEisEmpMst) lQuery.uniqueResult();

		return lObjHrEisEmpVO;
	}

	public List getAllowancesListForGivenEmp(Long lLongEmpId)
	{

		List listAllowances = null;
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT PAM.allowCode,PAM.allowDesc");
		lSBQuery.append(" FROM HrEisEmpCompGrpMst CGM ,HrEisEmpCompMpg CM,HrPayAllowTypeMst PAM");
		lSBQuery.append(" WHERE CGM.EmpCompGrpId = CM.hrEisEmpCompGrpMst.EmpCompGrpId ");
		lSBQuery.append(" AND CM.compId = PAM.allowCode");
		lSBQuery.append(" AND CM.cmnLookupMst.lookupId = 2500134");
		lSBQuery.append(" AND CGM.hrEisEmpMst.empId = :empId");
		lSBQuery.append(" AND CGM.isactive = 1 ");
		lSBQuery.append(" AND CM.isactive = 1 ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		listAllowances = lQuery.list();

		return listAllowances;
	}

	public List getDeductionsListForGivenEmp(Long lLongEmpId)
	{

		List listDeductions = null;
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT PDM.deducCode,PDM.deducDesc");
		lSBQuery.append(" FROM HrEisEmpCompGrpMst CGM ,HrEisEmpCompMpg CM,HrPayDeducTypeMst PDM");
		lSBQuery.append(" WHERE CGM.EmpCompGrpId = CM.hrEisEmpCompGrpMst.EmpCompGrpId ");
		lSBQuery.append(" AND CM.compId = PDM.deducCode");
		lSBQuery.append(" AND CM.cmnLookupMst.lookupId = 2500135");
		lSBQuery.append(" AND CGM.hrEisEmpMst.empId = :empId");
		lSBQuery.append(" AND CGM.isactive = 1");
		lSBQuery.append(" AND CM.isactive = 1");
		lSBQuery.append(" AND PDM.deducCode not in (122,120,121)"); // To remove 3 DCPS components
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		listDeductions = lQuery.list();

		return listDeductions;
	}

	//Modified byu Kinjal : Start
	/*public Boolean checkBrokenPeriodPayExistsOrNot(Long lLongEmpId, Long lLongYearId, Long lLongMonthId)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List<MstBrokenPeriodPay> finalList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" FROM MstBrokenPeriodPay WHERE eisEmpId = :empId AND yearId = :year AND monthId = :month");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		lQuery.setParameter("year", lLongYearId);
		lQuery.setParameter("month", lLongMonthId);

		finalList = lQuery.list();

		if (finalList.size() == 0)
		{
			flag = false;
		}
		return flag;

	}*/
	public List checkBrokenPeriodExistsOrNot(Long lLongEmpId, Long lLongYearId, Long lLongMonthId)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List finalList = new ArrayList();

		lSBQuery.append(" Select month_id, year_id, to_char(from_date, 'DD/MM/YYYY'), to_char(to_date, 'DD/MM/YYYY'), no_of_days, type_of_bill FROM Mst_Dcps_Broken_Period_Pay WHERE eis_Emp_Id = :empId AND year(from_date) = :year AND month(from_date) = :month ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		lQuery.setParameter("year", lLongYearId);
		lQuery.setParameter("month", lLongMonthId);

		if(lQuery.list()!=null && lQuery.list().size()>0 && lQuery.list().get(0)!=null)
		{
			finalList = lQuery.list();
		}
	

		return finalList;
	}


	
	/*public List<MstBrokenPeriodPay> getAddedBrokenPeriodPaysForEmp(Long lLongEmpId, Long lLongYearId, Long lLongMonthId)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List<MstBrokenPeriodPay> finalList = new ArrayList();

		lSBQuery.append(" FROM MstBrokenPeriodPay WHERE eisEmpId = :empId AND yearId = :year AND monthId = :month");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		lQuery.setParameter("year", lLongYearId);
		lQuery.setParameter("month", lLongMonthId);

		finalList = lQuery.list();

		return finalList;
	}*/
	public List<MstBrokenPeriodPay> getAddedBrokenPeriodPaysForEmp(Long lLongEmpId, Long lLongYearId, Long lLongMonthId, String lStrBillType)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List<MstBrokenPeriodPay> finalList = new ArrayList();

		lSBQuery.append(" FROM MstBrokenPeriodPay WHERE eisEmpId = :empId AND yearId = :year AND monthId = :month AND typeOfBill = :type");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		lQuery.setParameter("year", lLongYearId);
		lQuery.setParameter("month", lLongMonthId);
		lQuery.setParameter("type", lStrBillType);

		finalList = lQuery.list();

		return finalList;
	}
	//Modified byu Kinjal : End

	public List getAddedAllowancesForEmp(Long lLongRltBrokenPeriodId)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List finalList = new ArrayList();

		lSBQuery.append(" SELECT RA.brokenPeriodAllowId,RA.rltBrokenPeriodId.brokenPeriodId,RA.allowCode,RA.allowValue,HR.allowDesc ");
		lSBQuery.append(" FROM RltBrokenPeriodAllow RA ,HrPayAllowTypeMst HR ");
		lSBQuery.append(" WHERE RA.allowCode = HR.allowCode");
		lSBQuery.append(" AND RA.rltBrokenPeriodId.brokenPeriodId = :brokenPeriodId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("brokenPeriodId", lLongRltBrokenPeriodId);

		finalList = lQuery.list();
		return finalList;
	}

	public List getAddedDeductionsForEmp(Long lLongRltBrokenPeriodId)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List finalList = new ArrayList();

		lSBQuery.append(" SELECT RD.brokenPeriodDeducId,RD.rltBrokenPeriodId.brokenPeriodId,RD.deducCode,RD.deducValue,HR.deducDesc ");
		lSBQuery.append(" FROM RltBrokenPeriodDeduc RD ,HrPayDeducTypeMst HR  ");
		lSBQuery.append(" WHERE RD.deducCode = HR.deducCode");
		lSBQuery.append(" AND RD.rltBrokenPeriodId.brokenPeriodId = :brokenPeriodId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("brokenPeriodId", lLongRltBrokenPeriodId);

		finalList = lQuery.list();

		return finalList;
	}

	public void deleteAllBrokenPeriodPaysForPk(Long lLongBrokenPrdId)
	{

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MstBrokenPeriodPay where brokenPeriodId = :brokenPeriodId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("brokenPeriodId", lLongBrokenPrdId);
		lQuery.executeUpdate();

	}

	public void deleteAllBrokenPeriodAllowancesForBrknPrdId(Long lLongBrokenPrdId)
	{

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from RltBrokenPeriodAllow where rltBrokenPeriodId.brokenPeriodId = :brokenPeriodId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("brokenPeriodId", lLongBrokenPrdId);
		lQuery.executeUpdate();

	}

	public void deleteAllBrokenPeriodDeductionsForBrknPrdId(Long lLongBrokenPrdId)
	{

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from RltBrokenPeriodDeduc where rltBrokenPeriodId.brokenPeriodId = :brokenPeriodId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("brokenPeriodId", lLongBrokenPrdId);
		lQuery.executeUpdate();

	}

	public List selectAllBrokenPeriodAllowancesForBrknPrdId(Long lLongBrokenPrdId)
	{

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("  from RltBrokenPeriodAllow where rltBrokenPeriodId.brokenPeriodId = :brokenPeriodId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("brokenPeriodId", lLongBrokenPrdId);
		return lQuery.list();

	}

	public List selectAllBrokenPeriodDeductionsForBrknPrdId(Long lLongBrokenPrdId)
	{

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("  from RltBrokenPeriodDeduc where rltBrokenPeriodId.brokenPeriodId = :brokenPeriodId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("brokenPeriodId", lLongBrokenPrdId);
		return lQuery.list();

	}

	//Added by Abhilash for displaying sevarthid
	public String getSevarthId(long emloyeeId)
	{
		String sevarthId = "";
		List list = new ArrayList();

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT SEVARTH_EMP_CD FROM HR_EIS_EMP_MST WHERE EMP_ID=" + emloyeeId);

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.info("==> getGisName query list:: " + lSBQuery.toString());
		list = lQuery.list();

		logger.info("==> getGisName query list:: " + list.size());
		if (list.size() > 0)
		{
			if (list.get(0) != null)
			{
				sevarthId = list.get(0).toString();
			}
			else
			{
				sevarthId = "";
			}
		}
		else
		{
			sevarthId = "";
		}

		return sevarthId;
	}

	//Ended
	public String getGPFOrDCPSNo(String sevarthID)
	{
		String GPFOrDCPSNo = "";
		StringBuffer sql_qury = new StringBuffer();
		sql_qury.append("SELECT gpf.PF_SERIES,gpf.GPF_ACC_NO FROM HR_PAY_GPF_DETAILS gpf,HR_EIS_EMP_MST eis, ORG_EMP_MST emp ");
		sql_qury.append("where gpf.USER_ID = emp.USER_ID ");
		sql_qury.append("and eis.SEVARTH_EMP_CD = '"+sevarthID+"' ");
		sql_qury.append("and eis.EMP_MPG_ID = emp.EMP_ID ");
		Query query = ghibSession.createSQLQuery(sql_qury.toString());
		List ans = query.list();
		Object[] objAns = (Object[]) ans.get(0);
		String pfSeries = ""; 
		String accNo = "";
		if(objAns[0] != null && objAns[1] != null)
		{
			pfSeries = objAns[0].toString();
			accNo = objAns[1].toString();
			if(pfSeries.equalsIgnoreCase("dcps"))
			{
				GPFOrDCPSNo = accNo;
			}
			else
			{
				GPFOrDCPSNo = pfSeries + "/" + accNo;
			}
		}
		else if(objAns[0] == null && objAns[1] != null)
		{
			GPFOrDCPSNo = objAns[1].toString();
		}
		else if(objAns[0] != null && objAns[1] == null)
		{
			GPFOrDCPSNo = objAns[0].toString();
		}
		else
		{
			GPFOrDCPSNo = "";
		}
		return GPFOrDCPSNo;
	}

	public int isGenerated(long eisEmpId, long month, long year)
	{
		StringBuffer sql_qury = new StringBuffer();
		sql_qury.append("SELECT count FROM MST_DCPS_EMP dcps, hr_eis_emp_mst eis, PAYBILL_HEAD_MPG phm ");
		sql_qury.append(" where eis.EMP_MPG_ID = dcps.ORG_EMP_MST_ID and dcps.BILLGROUP_ID = phm.BILL_NO and phm.APPROVE_FLAG in (0,1,5,6,7,8,9)");
		sql_qury.append(" and eis.EMP_ID = "+eisEmpId+ " and phm.PAYBILL_MONTH = " + month+" and phm.PAYBILL_YEAR = "+ year +"");
		Query query = ghibSession.createSQLQuery(sql_qury.toString());
		List generated = query.list();
		int i= 0;
		if (generated != null && generated.get(0)!= null)
		{
			i= (Integer) generated.get(0);
		}
		return i;
	}

	public long payBilYr(long month, long year)
	{
		long Paybilyear=0;
		StringBuffer sql_qury = new StringBuffer();
		sql_qury.append(" SELECT FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST where FIN_YEAR_ID = " +year);
		Query query = ghibSession.createSQLQuery(sql_qury.toString());
		List yrCode = query.list();
		if (yrCode != null && yrCode.get(0)!= null)
		{
			Paybilyear= Long.valueOf(yrCode.get(0).toString());
		}
		if( month <= 3)
		{
			Paybilyear+=1;
		}
		return Paybilyear;
	}


	public List getQtrAmount(String empId,Date givenDate)
	{
		long Paybilyear=0;
		StringBuffer sql_qury = new StringBuffer();
		sql_qury.append(" SELECT eis.EMP_ID,qtr.QUARTER_RENT FROM "); 
		sql_qury.append(" ORG_EMP_MST org inner join hr_eis_emp_mst eis on eis.EMP_MPG_ID=org.EMP_ID ");
		sql_qury.append(" inner join HR_EIS_QTR_EMP_MPG qtr on qtr.ALLOCATED_TO=org.USER_ID ");
		sql_qury.append("  where  (QTR.ALLOCATION_END_DATE >= :givenDate OR QTR.ALLOCATION_END_DATE IS NULL )");
		sql_qury.append(" and org.EMP_SRVC_EXP >=:givenDate ");
		sql_qury.append(" and org.EMP_DOB <= :givenDate ");
		sql_qury.append(" and eis.EMP_ID=:eisEmpId");
		Query query = ghibSession.createSQLQuery(sql_qury.toString());
		query.setParameter("givenDate", givenDate);
		query.setParameter("eisEmpId", empId);
		return query.list();

	}



	public List getEisGrade(String empId)
	{
		long Paybilyear=0;
		StringBuffer sql_qury = new StringBuffer();
		sql_qury.append(" SELECT grade.GRADE_ID, grade.GRADE_CODE,gis.EMP_ID  "); 
		sql_qury.append(" FROM HR_EIS_GIS_DTLS gis ");
		sql_qury.append(" inner join hr_eis_Emp_mst eis on eis.EMP_ID=gis.EMP_ID ");
		sql_qury.append(" inner join ORG_GRADE_MST grade on  gis.GIS_GROUP_GRADE_ID = grade.GRADE_ID  ");
		sql_qury.append(" where eis.EMP_ID=:eisEmpId ");

		Query query = ghibSession.createSQLQuery(sql_qury.toString());

		query.setParameter("eisEmpId", empId);
		return query.list();

	}


	public List getOfficeClass(long eisEmpId)
	{
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer("SELECT office.POST_ID, ddo.OFFICE_CITY_CLASS FROM HR_PAY_OFFICEPOST_MPG office inner join  ");
		strQuery.append("  ORG_USERPOST_RLT up on up.POST_ID=office.POST_ID and up.ACTIVATE_FLAG=1 ");
		strQuery.append("  inner join org_emp_mst emp on emp.USER_ID=up.USER_ID ");
		strQuery.append("   inner join MST_DCPS_DDO_OFFICE ddo on office.OFFICE_ID = ddo.DCPS_DDO_OFFICE_MST_ID  ");
		strQuery.append("    where up.POST_ID=office.POST_ID  and emp.EMP_ID=:eisEmpId  ");
		Query query = hibSession.createSQLQuery(strQuery.toString());
		query.setParameter("eisEmpId",eisEmpId);
		logger.info("query to get HrPayOfficepostMpg from post Id is below  "+strQuery.toString());
		return query.list();

	}


	public List getEmpMappedAllownace(long empId,String locId)
	{
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer("SELECT grpMst.EMP_ID,allowMst.ALLOW_CODE, allowMst.ALLOW_DESC,allowMst.all_ded_id FROM HR_PAY_ALLOW_TYPE_MST allowMst,HR_PAY_COMPONENT_GRP_MST compoGrp,HR_PAY_LOC_COMPONENT_MPG locCompo, HR_EIS_EMP_COMPONENT_MPG compoMpg, HR_EIS_EMP_COMPONENT_GRP_MST grpMst where  ");
		strQuery.append("  compoGrp.IS_ACTIVE = 1 and compoGrp.COMPO_GROUP_ID = locCompo.COMPO_GRP_ID and locCompo.ISACTIVE = 1 and locCompo.COMPO_TYPE = 2500134 ");
		strQuery.append("  and locCompo.COMPO_ID = allowMst.ALLOW_CODE and  compoMpg.COMPO_GROUP_ID = grpMst.EMP_COMPO_GRP_ID and ");
		strQuery.append("  allowMst.ALLOW_CODE = compoMpg.COMPO_ID and compoMpg.COMPO_TYPE=2500134 and compoMpg.IS_ACTIVE=1 and grpMst.IS_ACTIVE=1  ");
		strQuery.append("   and compoGrp.LOC_ID = :locId  and grpMst.emp_id=:empId order by grpMst.EMP_ID,allowMst.SEQUENCE_NO  ");
		Query query = hibSession.createSQLQuery(strQuery.toString());
		query.setParameter("locId",locId);
		query.setParameter("empId",empId);
		logger.info("query to get getEmpMappedAllownace  "+strQuery.toString());
		return query.list();

	}

	/*	
	select trndcpscon0_.CONTRIBUTION ,trndcpscon0_.TYPE_OF_PAYMENT,mstemp1_.ORG_EMP_MST_ID from 
	 TRN_DCPS_CONTRIBUTION trndcpscon0_ inner join MST_DCPS_EMP mstemp1_ on mstemp1_.DCPS_EMP_ID = trndcpscon0_.DCPS_EMP_ID
	  where  trndcpscon0_.TYPE_OF_PAYMENT in (700046,700047,700048,700049) 
	and trndcpscon0_.REG_STATUS in (0,2,1,3,4)  and trndcpscon0_.MONTH_ID= :monthId and trndcpscon0_.FIN_YEAR_ID= :finYrId 
	and mstemp1_.ORG_EMP_MST_ID=7720000321*/


	public List getAllActiveEditableAllowance(long empId,String locId)
	{
		ResourceBundle rb = ResourceBundle.getBundle("resources.Payroll");
		List allowanceList =new ArrayList();
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT hpem.ALLOW_CODE,hpem.EMP_ID,hpem.EMP_ALLOW_ID,hpem.EMP_ALLOW_AMOUNT FROM HR_PAY_EMPALLOW_MPG hpem ");
		sb.append(" inner join  HR_EIS_EMP_COMPONENT_MPG empComp on empComp.COMPO_ID = hpem.EMP_ALLOW_ID  and  empComp.IS_ACTIVE = 1   ");
		sb.append(" inner join  HR_EIS_EMP_COMPONENT_GRP_MST compoGrp on  compoGrp.EMP_COMPO_GRP_ID = empComp.COMPO_GROUP_ID and compoGrp.emp_id = :empId  and compoGrp.IS_ACTIVE = 1 and compoGrp.EMP_ID = hpem.EMP_ID");
		sb.append(" inner join HR_EIS_OTHER_DTLS otherDtls on otherDtls.emp_id=compoGrp.emp_id ");
		sb.append(" inner join  HR_EIS_SGD_MPG sgd on otherDtls.EMP_SGD_ID = sgd.SGD_MAP_ID inner join HR_EIS_SCALE_MST scale on sgd.SGD_SCALE_ID = scale.SCALE_ID ");
		sb.append(" left outer join  HR_PAY_EXPRESSION_MST exp on exp.PAY_COMMISSION_ID = scale.COMMISSION_ID and empComp.COMPO_ID=exp.ALLOW_ID ");
		sb.append(" ,HR_PAY_ALLOW_TYPE_MST allowMst, HR_PAY_COMPONENT_GRP_MST grpMst,  HR_PAY_LOC_COMPONENT_MPG locCompo ");
		sb.append(" where grpMst.IS_ACTIVE = 1 and hpem.MONTH= -1 and hpem.YEAR=  -1 and grpMst.COMPO_GROUP_ID = locCompo.COMPO_GRP_ID and allowMst.ALLOW_CODE = empComp.COMPO_ID ");
		sb.append(" and locCompo.ISACTIVE = 1  and locCompo.COMPO_TYPE = empComp.COMPO_TYPE  and locCompo.COMPO_ID = empComp.COMPO_ID and exp.ALLOW_ID is null ");
		sb.append(" and empComp.COMPO_TYPE = 2500134 and   grpMst.LOC_ID = :locId  and hpem.EMP_CURRENT_STATUS = 1 and hpem.emp_id=:empId");

		logger.info("Query for getting all editiable allowance "+sb.toString());
		logger.info("Location Id is"+locId);
		Query query= hibSession.createSQLQuery(sb.toString());
		query.setParameter("empId", empId);
		query.setParameter("locId", locId);
		//query.setParameter("emp_id", empId);

		allowanceList = query.list();
		return allowanceList;
	}

	public List getAllActiveEditableDeduction(long empId,String locId)
	{
		ResourceBundle rb = ResourceBundle.getBundle("resources.Payroll");
		List deductionList =new ArrayList();
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT hpdd.DEDUC_DTLS_CODE,hpdd.EMP_ID,hpdd.EMP_DEDUC_ID,hpdd.EMP_DEDUC_AMOUNT FROM HR_PAY_DEDUCTION_DTLS hpdd  ");
		sb.append("inner join  HR_EIS_EMP_COMPONENT_MPG empComp on empComp.COMPO_ID = hpdd.EMP_DEDUC_ID  and  empComp.IS_ACTIVE = 1 ");
		sb.append("  inner join  HR_EIS_EMP_COMPONENT_GRP_MST compoGrp on  compoGrp.EMP_COMPO_GRP_ID = empComp.COMPO_GROUP_ID and compoGrp.emp_id ="+empId+"  and compoGrp.IS_ACTIVE = 1 and compoGrp.EMP_ID = hpdd.emp_id ");
		sb.append(" inner join HR_EIS_OTHER_DTLS otherDtls on otherDtls.emp_id=compoGrp.emp_id ");
		sb.append(" inner join  HR_EIS_SGD_MPG sgd on otherDtls.EMP_SGD_ID = sgd.SGD_MAP_ID inner join HR_EIS_SCALE_MST scale on sgd.SGD_SCALE_ID = scale.SCALE_ID ");
		sb.append(" left outer join  HR_DEDUC_EXP_MST exp on exp.PAY_COMMISSION_ID = scale.COMMISSION_ID and empComp.COMPO_ID=exp.DEDUC_ID ");
		sb.append(" ,HR_PAY_DEDUC_TYPE_MST deducMst, HR_PAY_COMPONENT_GRP_MST grpMst,  HR_PAY_LOC_COMPONENT_MPG locCompo ");
		sb.append("  where grpMst.IS_ACTIVE = 1 and hpdd.MONTH= -1 and hpdd.YEAR=  -1 and grpMst.COMPO_GROUP_ID = locCompo.COMPO_GRP_ID and deducMst.DEDUC_CODE = empComp.COMPO_ID ");
		sb.append(" and locCompo.ISACTIVE = 1  and locCompo.COMPO_TYPE = empComp.COMPO_TYPE  and locCompo.COMPO_ID = empComp.COMPO_ID and exp.DEDUC_ID is null ");
		sb.append(" and empComp.COMPO_TYPE = 2500135 and   grpMst.LOC_ID = :locId  and hpdd.EMP_CURRENT_STATUS = 1 and hpdd.emp_id=:empId");
		logger.info("Query for getting all editiable deduction "+sb);
		logger.info("Location Id is"+locId);
		Query query= hibSession.createSQLQuery(sb.toString());
		query.setParameter("locId", locId);
		query.setParameter("empId", empId);
		//query.setParameter("emp_id", empId);
		deductionList = query.list();
		return deductionList;
	}
	public List getMappedDeductionsList(long empId,String locId){
		ResourceBundle rb = ResourceBundle.getBundle("resources.Payroll");
		List deductionsList =new ArrayList();
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT grpMst.EMP_ID,deducMst.DEDUC_CODE,deducMst.DEDUC_DESC,deducMst.all_ded_id FROM HR_PAY_DEDUC_TYPE_MST deducMst, HR_PAY_COMPONENT_GRP_MST compoGrp,HR_PAY_LOC_COMPONENT_MPG locCompo ,HR_EIS_EMP_COMPONENT_MPG compoMpg,HR_EIS_EMP_COMPONENT_GRP_MST grpMst inner join ");
		sb.append(" compoGrp.IS_ACTIVE = 1 and compoGrp.COMPO_GROUP_ID = locCompo.COMPO_GRP_ID and locCompo.ISACTIVE = 1 and locCompo.COMPO_TYPE = 2500135 and ");
		sb.append("   locCompo.COMPO_ID = deducMst.DEDUC_CODE and compoMpg.COMPO_GROUP_ID = grpMst.EMP_COMPO_GRP_ID and deducMst.DEDUC_CODE = compoMpg.COMPO_ID ");
		sb.append(" and compoMpg.COMPO_TYPE=2500135 and deducMst.DEDUC_TYPE!=300160 and compoMpg.IS_ACTIVE=1 and grpMst.IS_ACTIVE = 1 ");
		sb.append("   and compoGrp.LOC_ID = :locId ");
		sb.append("  order by grpMst.EMP_ID,deducMst.SEQUENCE_NO and grpMst.emp_id =:empId");
		logger.info(" Location Id to Get Deductions is :"+locId);
		logger.info("Query for getting all Deductions "+sb.toString());
		Query query= hibSession.createSQLQuery(sb.toString());
		query.setParameter("locId", locId);
		query.setParameter("empId", empId);
		deductionsList = query.list();
		return deductionsList;
	}


	public List getDCPSValues(String empId, int month, long finYrId)
	{
		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer("");
		strQuery.append(" SELECT trn.CONTRIBUTION ,trn.TYPE_OF_PAYMENT,emp.ORG_EMP_MST_ID FROM  ");
		strQuery.append(" TRN_DCPS_CONTRIBUTION trn,MST_DCPS_EMP emp,HR_EIS_EMP_MST eis ");
		strQuery.append(" where eis.emp_mpg_id=emp.ORG_EMP_MST_ID " );
		strQuery.append(" and emp.DCPS_EMP_ID=trn.DCPS_EMP_ID ");
		strQuery.append(" and trn.TYPE_OF_PAYMENT in (700046,700047,700048,700049) ");
		strQuery.append(" and trn.REG_STATUS in (0,2,1,3,4) ");
		strQuery.append(" and trn.REG_STATUS in (0,2,1,3,4) ");
		strQuery.append(" and trn.MONTH_ID=  "+month);
		strQuery.append(" and trn.FIN_YEAR_ID=  "+finYrId);
		strQuery.append(" and eis.EMP_ID=  "+empId);
		logger.info("Query is " + strQuery);
		Query query = hiSession.createSQLQuery(strQuery.toString());
		return query.list();
	}

	public int isComponenetMapped(String empId,long compoType , long compoId){

		logger.info("executing isComponenetMapped method for checking "+compoId);

		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(1) FROM HR_EIS_EMP_COMPONENT_MPG compoMpg, HR_EIS_EMP_COMPONENT_GRP_MST  compoGrp  "); 
		sb.append(" where compoGrp.IS_ACTIVE = 1 and compoGrp.EMP_COMPO_GRP_ID = compoMpg.COMPO_GROUP_ID and ");
		sb.append(" compoMpg.COMPO_TYPE =");
		sb.append(compoType);
		sb.append(" and compoMpg.COMPO_ID =");
		sb.append(compoId);
		sb.append(" and compoMpg.IS_ACTIVE = 1 ");
		sb.append(" and compoGrp.EMP_ID= "+empId);

		Query query=hibSession.createSQLQuery(sb.toString());
		return Integer.parseInt(query.uniqueResult().toString());

	}



	public List getEmpBasicDetails(String empId){

		logger.info("executing getEmpBasicDetails method for checking "+empId);

		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT eisemp.EMP_MPG_ID, eisemp.EMP_ID, dtls.OTHER_CURRENT_BASIC,  dtls.INCOME_TAX, gd.SGD_GRADE_ID");
		sb.append(",gd.SGD_DESIG_ID, scale.SCALE_ID, scale.SCALE_START_AMT, scale.SCALE_END_AMT,dcpsemp.DCPS_OR_GPF, ");
		sb.append(" up.POST_ID,  usermst.USER_ID,scale.COMMISSION_ID,eisemp.EMP_TYPE,gradeMst.GRADE_CODE, dtls.ISAVAILEDHRA,");
		sb.append(" orgemp.EMP_DOB,orgemp.EMP_SRVC_EXP,orgemp.EMP_DOJ,dtls.PHY_CHALLENGED,scale. SCALE_GRADE_PAY,dtls.CITY,  ");
		sb.append(" psr.PSR_NO, dtls.OTHER_ID,orgemp.emp_lname ");
		sb.append(" FROM org_emp_mst orgemp left outer join hr_eis_emp_mst eisemp on orgemp.EMP_ID = eisemp.EMP_MPG_ID ");
		sb.append(" left outer join hr_eis_other_dtls dtls on dtls.EMP_ID=eisemp.EMP_ID  ");
		sb.append(" left outer join org_user_mst usermst on orgemp.USER_ID=usermst.USER_ID  ");
		sb.append(" left outer join ORG_USERPOST_RLT up on  up.USER_ID=usermst.USER_ID  ");
		sb.append(" left outer join ORG_POST_MST pm on pm.POST_ID=up.POST_ID    ");
		sb.append(" left outer join HR_PAY_POST_PSR_MPG psr on psr.POST_ID=up.POST_ID and psr.POST_ID = pm.POST_ID ");
		sb.append(" left outer join HR_EIS_SGD_MPG sgd on dtls.EMP_SGD_ID=sgd.SGD_MAP_ID   ");
		sb.append(" left outer join HR_EIS_GD_MPG gd on gd.GD_MAP_ID = sgd.SGD_GD_ID     ");
		sb.append("  left outer join HR_EIS_SCALE_MST scale on sgd.SGD_SCALE_ID=scale.SCALE_ID     ");
		sb.append("  left outer join MST_DCPS_EMP dcpsEmp on orgemp.EMP_ID=dcpsemp.ORG_EMP_MST_ID and dcpsemp.reg_status in (1,2)   ");
		sb.append(" left outer join org_grade_mst gradeMst on gradeMst.GRADE_ID = gd.SGD_GRADE_ID     ");
		sb.append(" where orgemp.EMP_SRVC_EXP >= sysdate and orgemp.EMP_DOB <=sysdate  and  up.ACTIVATE_FLAG=1 ");
		sb.append(" and up.START_DATE <= sysdate  and (pm.end_date >= sysdate or pm.end_date is null) ");
		sb.append(" and (up.END_DATE >= sysdate or up.END_DATE is null)   ");
		sb.append("  and eisemp.EMP_ID="+empId);
		Query query=hibSession.createSQLQuery(sb.toString());
		logger.info(" " + query.toString());

		return query.list();

	}

	public int getyearfromFinYear(String finyearID,int monthId){
		logger.info("executing getEmpBasicDetails method for checking ");

		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		if(monthId<4)
			sb.append("SELECT year(to_date)");
		else
			sb.append("SELECT year(from_date)");
		sb.append(" FROM SGVC_FIN_YEAR_MST where FIN_YEAR_ID="+finyearID);
		Query query=hibSession.createSQLQuery(sb.toString());
		logger.info(" " + query.toString());
		return Integer.parseInt(query.uniqueResult().toString());
	}


	public List getEmpMappedDeduction(long empId,String locId)
	{
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer("SELECT grpMst.EMP_ID,deducMst.DEDUC_CODE,deducMst.DEDUC_DESC,deducMst.all_ded_id FROM HR_PAY_DEDUC_TYPE_MST deducMst, HR_PAY_COMPONENT_GRP_MST compoGrp,HR_PAY_LOC_COMPONENT_MPG locCompo ,HR_EIS_EMP_COMPONENT_MPG compoMpg,HR_EIS_EMP_COMPONENT_GRP_MST grpMst where  ");
		strQuery.append("  compoGrp.IS_ACTIVE = 1 and compoGrp.COMPO_GROUP_ID = locCompo.COMPO_GRP_ID and locCompo.ISACTIVE = 1 and locCompo.COMPO_TYPE = 2500135 and  ");
		strQuery.append(" locCompo.COMPO_ID = deducMst.DEDUC_CODE and compoMpg.COMPO_GROUP_ID = grpMst.EMP_COMPO_GRP_ID and deducMst.DEDUC_CODE = compoMpg.COMPO_ID  ");
		strQuery.append("   and compoMpg.COMPO_TYPE=2500135 and deducMst.DEDUC_TYPE!=300160 and compoMpg.IS_ACTIVE=1 and grpMst.IS_ACTIVE = 1  ");
		strQuery.append("   and compoGrp.LOC_ID = :locId  and grpMst.emp_id=:empId order by grpMst.EMP_ID,deducMst.SEQUENCE_NO  ");
		Query query = hibSession.createSQLQuery(strQuery.toString());
		query.setParameter("locId",locId);
		query.setParameter("empId",empId);
		logger.info("query to get getEmpMappedAllownace  "+strQuery.toString());
		return query.list();

	} 

	public List getAllowIDfrmAllowdedCode(String allowDedidStr)
	{
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer("SELECT ALLOW_CODE,ALL_DED_ID FROM HR_PAY_ALLOW_TYPE_MST where ALL_DED_ID in("+allowDedidStr+")");
		Query query = hibSession.createSQLQuery(strQuery.toString());
		logger.info("query to get getAllowdIDfrmAllowdedCode  "+strQuery.toString());
		return query.list();

	} 
	

	public List getDedIDfrmAllowdedCode(String allowDedidStr)
	{
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer("SELECT DEDUC_CODE,ALL_DED_ID FROM HR_PAY_DEDUC_TYPE_MST where ALL_DED_ID in ("+allowDedidStr+")");
		Query query = hibSession.createSQLQuery(strQuery.toString());
		logger.info("query to get getDedIDfrmAllowdedCode  "+strQuery.toString());
		return query.list();

	} 


	public List getQuarterDtls(long empId,long month,long year)
	{
		Session hibSession = getSession();
		StringBuffer strQuery = new StringBuffer("SELECT eis.EMP_ID,qtr.QUARTER_RENT FROM HR_EIS_QTR_EMP_MPG qtr,org_emp_mst org,hr_eis_emp_mst eis");
		strQuery.append(" where eis.emp_mpg_id=org.EMP_ID ");
		strQuery.append(" and qtr.ALLOCATED_TO=org.USER_ID ");
		strQuery.append(" and (qtr.ALLOCATION_END_DATE is null or (month(qtr.ALLOCATION_END_DATE))<="+month+" and year(qtr.ALLOCATION_END_DATE)<="+year+") ");
		strQuery.append(" and eis.EMP_ID= "+empId);
		Query query = hibSession.createSQLQuery(strQuery.toString());
		logger.info("query to getQuarterDtls "+strQuery.toString());
		return query.list();

	}
	

	public String getEmpNameFromEmpId(Long eisEmpIdForNGR) throws Exception{
		String empName=null;
		Session hibSession = getSession();
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT distinct dcps.emp_name || '(' || dcps.SEVARTH_ID || ')' FROM MST_DCPS_EMP dcps ");
		queryBuffer.append("inner join HR_EIS_EMP_MST eis on dcps.ORG_EMP_MST_ID=eis.EMP_MPG_ID ");
		queryBuffer.append("where eis.emp_id="+eisEmpIdForNGR+" ");
		queryBuffer.append("and dcps.reg_status in(1,2) ");
		Query query = hibSession.createSQLQuery(queryBuffer.toString());
		logger.info("Query is :: "+queryBuffer.toString());
		empName = query.uniqueResult().toString();
		return empName;
	}
	
	public Boolean checkBrokenPeriodPayExistsOrNot(Long lLongEmpId, Long lLongYearId, Long lLongMonthId, String lStrBillType)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List<MstBrokenPeriodPay> finalList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" FROM MstBrokenPeriodPay WHERE eisEmpId = :empId AND yearId = :year AND monthId = :month AND typeOfBill = :type");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		lQuery.setParameter("year", lLongYearId);
		lQuery.setParameter("month", lLongMonthId);
		lQuery.setParameter("type", lStrBillType);
		logger.info("checkBrokenPeriodPayExistsOrNot----"+lQuery.toString());
		finalList = lQuery.list();

		if (finalList.size() == 0)
		{
			flag = false;
		}
		return flag;

	}

	
	//Addded by Kinjal for NSB
	public Boolean checkPreviousBillExistsOrNot(Long lLongEmpId, Long lLongYearId, Long lLongMonthId)
	{

		StringBuilder lSBQuery = new StringBuilder();
		List<MstBrokenPeriodPay> finalList = new ArrayList();
		Boolean flag = true;
		

		lSBQuery.append(" SELECT count(1) FROM HR_PAY_PAYBILL where EMP_ID = :empId and PAYBILL_GRP_ID in (SELECT PAYBILL_ID FROM PAYBILL_HEAD_MPG where PAYBILL_YEAR = :year  and PAYBILL_MONTH = :month and APPROVE_FLAG <> 3 ) ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLongEmpId);
		lQuery.setParameter("year", lLongYearId);
		lQuery.setParameter("month", lLongMonthId);
		//lQuery.setParameter("type", lStrBillType);
		Long count = Long.parseLong(lQuery.uniqueResult().toString());
		///finalList = lQuery.list();

		if (count == 0)
		{
			flag = false;
		}
		return flag;

	}
	
public String getBillTypeOfbillGroup(String billNo){
		
		logger.info("hii i m in finding getBillType");
		Session session = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("select type_of_bill_group from mst_dcps_bill_group where BILL_GROUP_ID="+billNo+"");
		Query query = session.createSQLQuery(sb.toString());
		logger.info("query is *************"+query.toString());
		String billType = query.uniqueResult().toString();
		//String totalEmp = Long.valueOf(Long.parseLong(count));
		logger.info("billType" + billType);
		return billType;
		
	}


	


	
}