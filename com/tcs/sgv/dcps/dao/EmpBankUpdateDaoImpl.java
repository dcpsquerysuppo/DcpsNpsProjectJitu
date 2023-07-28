package com.tcs.sgv.dcps.dao;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;

public class EmpBankUpdateDaoImpl extends GenericDaoHibernateImpl implements EmpBankUpdateDao{
	
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	public EmpBankUpdateDaoImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = getSession();

		
	}
	
	public List getTreasuryForDDO(String lStrTreasuryLocCode) {

		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb
				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL order by DM.ddoCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		
		lLstReturnList = new ArrayList<ComboValuesVO>();
		
		List lLstResult = selectQuery.list();
			gLogger.info("list size:" +lLstResult.size());

		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc="( "+obj[0].toString()+") "+obj[1].toString()+"";
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<ComboValuesVO>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}
	
	
	
	public List getAllDDO(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb.append(" order by DM.ddoName");
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
				lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " + obj[1].toString());
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

	public List getCurrentTreasury(String gStrLocationCode) {

		String query = "select LM.locationCode, LM.locName from CmnLocationMst LM where LM.locationCode = :locationCode";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", gStrLocationCode);
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

	
	//added by samadhan
	public List getTreasury() {
		String query = "SELECT loc_id,loc_Name  FROM CMN_LOCATION_MST  where department_Id=100003 order by loc_Name";
		gLogger.info("query to select treasury codes "+query);
		List<Object> lLstReturnList = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList=selectQuery.list();
		gLogger.info("Query executed!");
		
	return lLstReturnList;
	}
	
	public List getsubTreasuryForTreasury(String strTreasuryId)
	{
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where (department_id=100006 and PARENT_LOC_ID=:loc_id ) or LOC_ID= :loc_id order by loc_name ");
		gLogger.info("query to select sub treasury from treasury code:::" + sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		gLogger.info("sql query created");
		selectQuery.setParameter("loc_id", strTreasuryId);
		lLstReturnList = new ArrayList<ComboValuesVO>();
		
		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());
		return lLstResult;
	}
	
	public List getDDOForSubTreasury(String lStrSubTreasuryName)
	{
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		
		//sb.append("SELECT ddo.ddo_name||'( ' ||ddo.ddo_code||' )',ddo.ddo_code FROM ORG_DDO_MST ddo");
		sb.append("SELECT ddo.ddo_code,ddo.ddo_name FROM ORG_DDO_MST ddo");
		sb.append(" inner join RLT_DDO_ORG rlt on rlt.DDO_CODE=ddo.DDO_CODE");
		sb.append(" where rlt.LOCATION_CODE=:loc_code order by ddo.ddo_code");
		
		gLogger.info("query to select DDO from sub treasury code:::" + sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		gLogger.info("sql query created");
		selectQuery.setParameter("loc_code", lStrSubTreasuryName);
		
		
		lLstReturnList = new ArrayList<ComboValuesVO>();
		
		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());
		return lLstResult;
	}
	
	public List getAllDcpsEmployeesZPForBankUpdate(String ddoCode) {
		logger.info("lStrUser...."+ddoCode);
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		List<MstEmp> EmpList = null;
		
		//lSBQuery.append(" Select EM.dcps_Emp_Id,EM.EMP_NAME,EM.dob,EM.zp_Status,EM.sentBack_Remarks,Em.FATHER_OR_HUSBAND,EM.gender,EM.ddo_Code,em.zp_Status,em.bank_acnt_no");
		//lSBQuery.append(" Select EM.dcps_Emp_Id,EM.EMP_NAME,EM.dob,EM.sentBack_Remarks,Em.FATHER_OR_HUSBAND,EM.gender,EM.ddo_Code,em.bank_acnt_no");
		//lSBQuery.append(" FROM Mst_dcps_emp EM  WHERE ");
		//lSBQuery.append(" WHERE zp_status in (0,-1,2,3,4,10) and ");
		
		
		//lSBQuery.append("Select EM.dcps_Emp_Id,EM.EMP_NAME,EM.dob,EM.sentBack_Remarks,Em.FATHER_OR_HUSBAND, ");
		//lSBQuery.append("EM.gender,EM.ddo_Code,em.bank_acnt_no,MB.bank_name,RB.branch_name  ");
		//lSBQuery.append("FROM Mst_dcps_emp EM , MST_BANK_PAY MB,  RLT_BANK_BRANCH_PAY RB  ");
		//lSBQuery.append("where EM.BANK_NAME=MB.BANK_CODE and  EM.BRANCH_NAME=RB.BRANCH_CODE  ");
		//lSBQuery.append(" and em.ddo_code ='"+ddoCode+"'");
		
		
		//lSBQuery.append("SELECT mst.DCPS_EMP_ID,mst.EMP_NAME,mst.dob,mst.sentBack_Remarks,mst.FATHER_OR_HUSBAND,  ");
		//lSBQuery.append(" mst.gender, mst.DDO_CODE ,mst.BANK_ACNT_NO, bank.BANK_NAME,branch.BRANCH_NAME FROM org_ddo_mst ddo inner join ORG_POST_DETAILS_RLT post ");
		//lSBQuery.append(" on ddo.LOCATION_CODe =post.LOC_ID inner join ORG_USERPOST_RLT usr on usr.POST_ID = post.POST_ID and usr.ACTIVATE_FLAG =1 ");
		//lSBQuery.append(" inner join ORG_EMP_MST emp on usr.USER_ID = emp.USER_ID ");
		//lSBQuery.append(" inner join MST_DCPS_EMP mst on mst.ORG_EMP_MST_ID = emp.EMP_ID "); 
		//lSBQuery.append(" full outer join MST_BANK_PAY bank on bank.BANK_code = mst.BANK_NAME ");
		//lSBQuery.append(" inner join RLT_BANK_BRANCH_PAY branch on branch.BRANCH_ID = mst.BRANCH_NAME ");
		//lSBQuery.append(" where ddo.ddo_code ='"+ddoCode+"'");
		
		lSBQuery.append(" SELECT em.DCPS_EMP_ID,em.EMP_NAME,em.dob,em.sentBack_Remarks,em.FATHER_OR_HUSBAND, ");
		lSBQuery.append(" em.gender,em.ddo_code,em.BANK_ACNT_NO,bank.BANK_NAME,branch.BRANCH_NAME  "); 
		lSBQuery.append(" FROM mst_dcps_emp em left join MST_BANK_PAY bank on cast(bank.BANK_CODE as bigint) = cast(em.BANK_NAME as bigint) "); 
		lSBQuery.append(" left join RLT_BANK_BRANCH_PAY branch on cast(branch.BRANCH_ID as bigint) = cast(em.BRANCH_NAME as bigint) ");   
		lSBQuery.append(" inner join ORG_EMP_MST emp on em.ORG_EMP_MST_ID = emp.emp_id and ( emp.EMP_SRVC_EXP > sysdate or emp. EMP_SRVC_EXP is null)  "); 
		lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = emp.user_id and usr.activate_flag = 1 ");  
		lSBQuery.append("  where em.ddo_code = '"+ddoCode+"' and em.SERV_END_FLAG is null and em.FORM_STATUS = 1 and em.REG_STATUS in (1,2) "); 
		lSBQuery.append(" and (em.SUPER_ANN_DATE > sysdate or em.SUPER_ANN_DATE is null) order by em.EMP_NAME  "); 
		
		
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("query is"+lQuery.toString());
			
		EmpList = lQuery.list();
		logger.info("size is "+EmpList.size());
		return EmpList;
	}
	
	public List getBankNames() {
		List bankList=null;
		StringBuffer sb= new StringBuffer();
		sb.append("select bank_code, bank_name from mst_bank_pay order by bank_name ");
		Query sqlQuery= ghibSession.createSQLQuery(sb.toString());
		logger.info("query DAO.."+sb.toString());
		if(sqlQuery.list()!=null){
			bankList=sqlQuery.list();
		}
		return bankList;
	}
	
	public List getBranchList(String cmbBank){
		List branchList=null;
		List<Object> lLstReturnList;
		StringBuffer sb= new StringBuffer();
		sb.append("select branch_id, branch_name,ifsc_code from rlt_bank_branch_pay where bank_code="+cmbBank+" order by  branch_name");
		Query sqlQuery= ghibSession.createSQLQuery(sb.toString());
		logger.info("query DAO..**********"+sb.toString());
		if(sqlQuery.list()!=null){
			branchList=sqlQuery.list();
		}
		lLstReturnList = null;
		ComboValuesVO lObjComboValuesVO = null;
		if (branchList != null && branchList.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < branchList.size(); liCtr++) {

				obj = (Object[]) branchList.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				
				//String desc=obj[0].toString()+"-"+obj[1].toString();
				
				lObjComboValuesVO.setDesc(obj[1].toString()+"-"+obj[2].toString());
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
	
	public void updateBankDetails(String lngEmpID, String bankId,
			String branchId,String AccountNo) {
	
		try {
			logger.info("Inside updateDdoOffice....");
			logger.info("emp id is "+lngEmpID);
			logger.info("bank id is "+bankId);
			logger.info("branch id is "+branchId);
			
			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append("UPDATE MST_DCPS_EMP SET BANK_NAME='"+bankId+"', BRANCH_NAME='"+branchId+"',bank_acnt_no='"+AccountNo+"',IFSC_CODE=(select ifsc_code From rlt_bank_branch_pay where branch_id="+branchId+")" +
					" WHERE DCPS_EMP_ID="+lngEmpID+"");
			Query lQuery = ghibSession.createSQLQuery(SBQuery.toString());
			logger.info("the query is ********"+lQuery.toString());
			lQuery.executeUpdate();
			
			//to update in hr_eis_bank_detls table also

			SBQuery = new StringBuilder();
			SBQuery.append("update  HR_EIS_BANK_DTLS set BANK_ACCT_NO = '"+AccountNo+"' ");
			SBQuery.append(" where BANK_EMP_ID in ( SELECT eis.EMP_ID FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis " );
			SBQuery.append(" on emp.ORG_EMP_MST_ID = eis.EMP_MPG_ID ");
			SBQuery.append(" where emp.DCPS_EMP_ID = "+lngEmpID+") ");
			lQuery = ghibSession.createSQLQuery(SBQuery.toString());
			logger.info("the query is ********"+lQuery.toString());
			lQuery.executeUpdate();			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
	}
}
