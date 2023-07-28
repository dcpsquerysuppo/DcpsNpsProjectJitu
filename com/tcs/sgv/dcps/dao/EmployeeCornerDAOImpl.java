package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class EmployeeCornerDAOImpl extends GenericDaoHibernateImpl{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	
	/**
	 * 
	 * @param type
	 * @param sessionFactory
	 */
	public EmployeeCornerDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);

		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}
	
	public List searchEmpsForEmpCorner(long empId) {

		StringBuilder lSBQuery = new StringBuilder();
		List EmployeeList = null;

		empId = empId;
		/*lStrName = lStrName.toUpperCase();*/

		lSBQuery.append(" SELECT EM.dcps_emp_Id, EM.emp_name,EM.dcps_Id, EM.gender, EM.dob, nvl(DO.off_name,''),nvl(OD.DSGN_NAME,''),EM.sevarth_Id,EM.DDO_CODE,nvl(DO.off_name,'')");
		lSBQuery.append(" from mst_dcps_Emp EM ");
		lSBQuery.append(" left join mst_dcps_ddo_office DO on DO.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF ");
		lSBQuery.append(" left join org_designation_mst OD on OD.DSGN_ID = EM.DESIGNATION");
		lSBQuery.append(" WHERE ");

		lSBQuery.append(" UPPER(EM.ORG_EMP_MST_ID) = :empId ");
		
		/*if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lSBQuery.append(" UPPER(EM.SEVARTH_ID) = :sevarthId ");
		}
		if (lStrName != null && !"".equals(lStrName)) {
			if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
				lSBQuery.append(" AND ");
			}
			lSBQuery.append(" UPPER(EM.emp_name) = :empName ");
		}
*/
		lSBQuery.append(" AND EM.REG_STATUS IN (1,2) ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("empId", empId);
		/*if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lQuery.setParameter("sevarthId", lStrSevarthId.trim().toUpperCase());
		}
		if (lStrName != null && !"".equals(lStrName)) {
			lQuery.setParameter("empName", lStrName.trim().toUpperCase());
		}*/

		EmployeeList = lQuery.list();

		return EmployeeList;
	}
	
	public List getNameForAutoComplete(String searchKey)
	{
		
		logger.info("Inside getEMPNameForAutoComplete****** ");

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();

		sb
				.append("select name,name from MstEmp where UPPER(name) LIKE :searchKey and regStatus in (1,2) ");
		
		
		
		sb.append(" and ( servEndDate is null or servEndDate  >= :currentDate ) ");

		selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("searchKey", '%' + searchKey + '%');
		selectQuery.setDate("currentDate", lDtCurrDate);

		

		List resultList = selectQuery.list();

		cmbVO = new ComboValuesVO();

		if (resultList != null && resultList.size() > 0) {
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) it.next();
				logger.info("Inside getEmpNameForAutoComplete List results are--->"+obj[0].toString());
				cmbVO.setId(obj[0].toString());
				
				cmbVO.setDesc(obj[1].toString());
				
				finalList.add(cmbVO);
			}
		}

		return finalList;
	}

	public List viewpaySlip(String SevarthId,String Month,String Year )
	{
		StringBuilder sb = new StringBuilder();
		
	
		sb.append("SELECT EM.dcps_emp_Id, EM.emp_name,EM.dcps_Id, EM.gender, EM.dob, nvl(DO.off_name,''),nvl(OD.DSGN_NAME,''),EM.sevarth_Id,EM.DDO_CODE,nvl(DO.off_name,'')");
		sb.append(",payslip.EMP_ID,billgrp.loc_id,billGrp.BILL_GROUP_ID,OD.DSGN_ID from mst_dcps_Emp EM");
		sb.append(" left join mst_dcps_ddo_office DO on DO.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF");
		sb.append(" left join org_designation_mst OD on OD.DSGN_ID = EM.DESIGNATION");
		sb.append(" left join hr_eis_emp_mst emp on em.ORG_EMP_MST_ID=emp.EMP_MPG_ID");
		sb.append(" left join HR_PAY_PAYSLIP payslip on payslip.EMP_ID=emp.EMP_ID");
		sb.append(" left join MST_DCPS_BILL_GROUP billGrp on billGrp.BILL_GROUP_ID=payslip.BILL_NO");
		sb.append(" where payslip.PAYSLIP_MONTH = :month and payslip.PAYSLIP_YEAR = :year");
		sb.append(" and em.SEVARTH_ID = :sevarthId ");
		
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("month", Month);
		lQuery.setParameter("year", Year);
		lQuery.setParameter("sevarthId",SevarthId.trim().toUpperCase());
		
		List resultList = lQuery.list();

		return resultList;	
		
	}
}
