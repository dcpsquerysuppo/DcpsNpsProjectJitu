package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;

public class DeputationEmpBankUpdateDaoImpl  extends GenericDaoHibernateImpl implements DeputationEmpBankUpdateDao {
	
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	public DeputationEmpBankUpdateDaoImpl(Class type ,SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = getSession();
	}

	@Override
	public List getAdminDepartment() {
		String query = "SELECT LOC_ID,LOC_NAME FROM CMN_LOCATION_MST WHERE DEPARTMENT_ID=100001 order by loc_Name";
		gLogger.info("query to select treasury codes "+query);
		List<Object> lLstReturnList = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList=selectQuery.list();
		gLogger.info("Query executed!");
		
	return lLstReturnList;
	}

	
	public List<ComboValuesVO> getFieldDepartmentFromAdminDepartment(
			String strTreasuryId) {
		
		
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where  PARENT_LOC_ID=:loc_id  order by loc_name ");
		gLogger.info("query to select sub treasury from treasury code:::" + sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		gLogger.info("sql query created");
		selectQuery.setParameter("loc_id", strTreasuryId);
		lLstReturnList = new ArrayList<ComboValuesVO>();
		
		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());
		return lLstResult;
		
	}

	@Override
	public List getAllDcpsDeputationEmployeesZPForBankUpdate(String fieldDept,String SevaarthId ) {
		logger.info("lStrUser...."+fieldDept);
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		List<MstEmp> EmpList = null;
		
		
		
		lSBQuery.append(" SELECT em.DCPS_EMP_ID,em.EMP_NAME,em.dob,em.sentBack_Remarks,em.FATHER_OR_HUSBAND, ");
		lSBQuery.append(" em.gender,em.ddo_code,em.BANK_ACNT_NO,bank.BANK_NAME,branch.BRANCH_NAME,em.SEVARTH_ID  "); 
		lSBQuery.append(" FROM mst_dcps_emp em left join MST_BANK_PAY bank on cast(bank.BANK_CODE as bigint) = cast(em.BANK_NAME as bigint) "); 
		lSBQuery.append(" left join RLT_BANK_BRANCH_PAY branch on cast(branch.BRANCH_ID as bigint) = cast(em.BRANCH_NAME as bigint) ");   
		lSBQuery.append(" inner join ORG_EMP_MST emp on em.ORG_EMP_MST_ID = emp.emp_id and ( emp.EMP_SRVC_EXP > sysdate or emp.EMP_SRVC_EXP is null)  "); 
		  
		lSBQuery.append("  where em.PARENT_DEPT = '"+fieldDept+"' and em.FORM_STATUS = 1 and em.REG_STATUS in (1,2) and  em.DDO_CODE is null "); 
		
		if (SevaarthId!=null && SevaarthId!="")
		{
			lSBQuery.append("  and  em.SEVARTH_ID = '"+SevaarthId+"'  ");
		}
		lSBQuery.append(" and (em.SUPER_ANN_DATE > sysdate or em.SUPER_ANN_DATE is null) order by em.EMP_NAME  "); 
		
		
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("query is"+lQuery.toString());
			
		EmpList = lQuery.list();
		logger.info("size is "+EmpList.size());
		return EmpList;
	}

	@Override
	public List checkBankAccountNumberForDeputation(String bank,
			String accountNumber, String empId) {
		Session hibSession = getSession();
		List EMpList;
		Long finalCheckFlag=null;
		StringBuffer sb= new StringBuffer();
		gLogger.info("bank: "+bank+" accountNumber "+accountNumber);
		//sb.append("SELECT count(1) FROM mst_dcps_emp where BANK_NAME="+bank+" and BANK_ACNT_NO='"+accountNumber+"'"
		//sb.append("SELECT SEVARTH_ID,DDO_CODE FROM mst_dcps_emp where BANK_NAME="+bank+" and BANK_ACNT_NO='"+accountNumber+"'"
				//+" and dcps_emp_id <>"+ empId );
		sb.append(" SELECT MST.SEVARTH_ID,MST.DDO_CODE,MST.EMP_NAME FROM mst_dcps_emp MST  ");
		//sb.append("  ON MST.DDO_CODE=MSTD.DDO_CODE AND MSTD.DDO_OFFICE='Yes' " );
		sb.append("  where MST.BANK_NAME="+bank+" and MST.BANK_ACNT_NO='"+accountNumber+"'and MST.dcps_emp_id <>"+ empId );
		gLogger.info("Query to check checkBankAccountNumberForDeputation bank account number:  " + sb.toString());
		Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
		
		EMpList=sqlQuery1.list();
		gLogger.info("checkBankAccountNumberForDeputation"+EMpList.size());
		//finalCheckFlag=Long.parseLong(sqlQuery1.uniqueResult().toString());
		gLogger.info("VALUE OF THE finalCheckFlag"+finalCheckFlag);
		return EMpList;
	}

	@Override
	public int   CheckFieldDepartmentOfemployee(String SevaarthId) {
		/*String query = ("SELECT PARENT_DEPT  FROM  mst_dcps_emp where SEVARTH_ID="+SevaarthId+" ");
		gLogger.info("query CheckFieldDepartmentOfemployee "+query);
		List<Object> lLstReturnList = null;
		int parentdept=0;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList=selectQuery.list();
		gLogger.info("Query executed!");
		
		if (lLstReturnList!=null)
			parentdept=Integer.parseInt(lLstReturnList.get(0).toString());
		
		gLogger.info("parentdept"+parentdept);
	return parentdept;*/
	
	
	
	
	
	
	
	
	List commonDtls=null;
	int parentdept=0;
	/*Long ngrAmount;
	String temp="";

	StringBuffer str= new StringBuffer();
	str.append("SELECT PARENT_DEPT  FROM  mst_dcps_emp where SEVARTH_ID=:SevaarthId");
	
	logger.info("getTotalEmpConfig DAO------"+str.toString());
	Query query= ghibSession.createSQLQuery(str.toString());
	query.setString("SevaarthId", SevaarthId);

	if(query.list()!=null && (query.list().size())> 0){
		commonDtls= query.list();
		if(commonDtls.get(0)!=null && !commonDtls.get(0).equals("")){
			parentdept = Integer.parseInt(commonDtls.get(0).toString());
		}
	}
	
	*/


	long cmpCountDDO;
	logger.info("getDDOOffice-gayathri-----"+SevaarthId);
	StringBuffer str = new StringBuffer();
	str.append("SELECT PARENT_DEPT  FROM  mst_dcps_emp where SEVARTH_ID='"+SevaarthId+"'");



	Query query= ghibSession.createSQLQuery(str.toString());

	logger.info("getCmpDdoDtls------"+query.toString());
	/*if(query.list()!=null){
	getCmpDdoDtls = (List) query.list().get(0);
	}*/
	if(query.list()!=null && (query.list().size())> 0){
		commonDtls= query.list();
		if(commonDtls.get(0)!=null && !commonDtls.get(0).equals("")){
			parentdept = Integer.parseInt(commonDtls.get(0).toString());
		}
	}
	logger.info("parentdept DAO------"+parentdept);
	return parentdept;
	
	
	
	
	
	
	
	
	
	
	
	
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	}


