package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.FormS1DAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

public class ManualBillEntryLatestDAOImpl
extends GenericDaoHibernateImpl {
	private final Log gLogger;
	org.hibernate.Session ghibSession;

	public ManualBillEntryLatestDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		this.gLogger = LogFactory.getLog(this.getClass());
		this.ghibSession = null;
		this.ghibSession = sessionFactory.getCurrentSession();
		this.setSessionFactory(sessionFactory);
	}
	
	public List getMonths() {

		String query = "select monthId,monthName from SgvaMonthMst where monthId < 13";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
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

	
	public List getFinyears() {

		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2018' order by finYearCode ASC";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
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

	public List getAllDDOForApprovedContriInTO(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT DISTINCT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM , TrnDcpsContribution VC ");
		sb.append(" WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb.append(" AND VC.ddoCode = DM.ddoCode AND VC.regStatus IN (1) ");
		sb.append(" order by DM.ddoCode ASC ");
		
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
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

	public List getEmployeeDetails(String lStrDcpsId){
		List lLstEmpDetials=null;
		StringBuilder str = new StringBuilder();
		try{
			str.append("SELECT EMP_NAME,PAY_COMMISSION,BASIC_PAY FROM mst_Dcps_emp emp where DCPS_ID = '"+lStrDcpsId+"'");
		
			SQLQuery lQuery = ghibSession.createSQLQuery(str.toString());
			lLstEmpDetials = lQuery.list();
		}catch (Exception e) {
			logger.info("error occured at **********"+e);
		}
		return lLstEmpDetials;
		
	}
		
	
	public List getEmployeeContriDetails(String lStrDcpsId,String lStrYear, String lStrMonth){
		List lLstEmpDetials=null;
		StringBuilder str = new StringBuilder();
		try{
			
			str.append(" SELECT to_char(trn.STARTDATE,'dd/mm/yyyy'),to_char(trn.ENDDATE,'dd/mm/yyyy'),nvl(trn.BASIC_PAY,EMp.BASIC_PAY),trn.DA,trn.CONTRIBUTION FROM MST_DCPS_EMP emp");
			str.append(" inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID = emp.DCPS_EMP_ID where emp.DCPS_ID = '"+lStrDcpsId+"' and trn.FIN_YEAR_ID = "+lStrYear+" and trn.MONTH_ID = "+lStrMonth);
		
			SQLQuery lQuery = ghibSession.createSQLQuery(str.toString());
			lLstEmpDetials = lQuery.list();
		}catch (Exception e) {
			logger.info("error occured at **********"+e);
		}
		return lLstEmpDetials;
		
	}
	
}