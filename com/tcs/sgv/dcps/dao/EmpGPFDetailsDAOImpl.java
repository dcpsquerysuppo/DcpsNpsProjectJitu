package com.tcs.sgv.dcps.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jgroups.jmx.protocols.pbcast.STREAMING_STATE_TRANSFER;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class EmpGPFDetailsDAOImpl  extends GenericDaoHibernateImpl{
	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(EmpGPFDetailsDAOImpl.class);

	//private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public EmpGPFDetailsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	public List getEMpGPFDetails(String lStrDdocode){
		List list =new ArrayList();
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append(" select eis.SEVARTH_EMP_CD,org.EMP_FNAME||' '||org.emp_mname||' '||org.emp_lname, ");
		strBfr.append(" gpf.PF_SERIES,gpf.GPF_ACC_NO,");
		//strBfr.append(" org.EMP_DOB,org.emp_id,gbu.NEW_GPF_SERIES,varchar_format(gbu.NEW_BIRTH_DATE,'DD/MM/YYYY'),gbu.NEW_ACCOUNT_NO,gbu.remarks from hr_Eis_emp_mst eis inner join org_emp_mst org on eis.EMP_MPG_ID = org.EMP_ID ");
		strBfr.append(" org.EMP_DOB,org.emp_id,gbu.NEW_GPF_SERIES,gbu.NEW_ACCOUNT_NO,gbu.remarks from hr_Eis_emp_mst eis inner join org_emp_mst org on eis.EMP_MPG_ID = org.EMP_ID ");
		strBfr.append(" inner join ORG_USERPOST_RLT up on up.USER_ID = org.USER_ID and up.ACTIVATE_FLAG = 1 ");
		strBfr.append(" inner join ORG_POST_MST post on post.POST_ID = up.POST_ID and post.ACTIVATE_FLAG = 1 ");
		strBfr.append(" inner join ORG_POST_DETAILS_RLT rlt on post.POST_ID = rlt.POST_ID and rlt.LOC_ID = post.LOCATION_CODE ");
		strBfr.append(" inner join HR_PAY_POST_PSR_MPG psr on psr.POST_ID = post.POST_ID and psr.LOC_ID = post.LOCATION_CODE ");
		strBfr.append(" inner join HR_PAY_GPF_DETAILS gpf on gpf.USER_ID = up.USER_ID ");
		strBfr.append(" inner join org_ddo_mst ddo on ddo.LOCATION_CODE = post.LOCATION_CODE ");
		strBfr.append(" left outer join HR_PAY_gpf_birth_updation gbu on gbu.EMP_ID = org.EMP_ID ");
		strBfr.append(" where ddo.DDO_CODE = :Ddocode   and gpf.PF_SERIES <> 'DCPS' " );
		strBfr.append("order by  org.EMP_FNAME");
		logger.info("strbfr ... "+strBfr);
		Query lQuery = hibSession.createSQLQuery(strBfr.toString());
		lQuery.setParameter("Ddocode", lStrDdocode);		
		list =lQuery.list();
		return list;

	}

	public List getGPFSeriesList(){
		List list =new ArrayList();
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("SELECT * FROM CMN_LOOKUP_MST where PARENT_LOOKUP_id in( 700098,700181) order by PARENT_LOOKUP_ID");
		Query query = hibSession.createSQLQuery(strBfr.toString());
		if(query != null)
			if(query.list().size() > 0){
				list = query.list();
			}
		return list;
	}

	public int checkPresentOrNot(String ddoCode,String empID, String sevaarthID){
		int presentOrNot = 0;
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("SELECT * FROM HR_PAY_gpf_birth_updation where DDO_CODE = "+ddoCode);
		strBfr.append(" and SEVAARTH_ID = '" +sevaarthID);
		strBfr.append("' and EMP_ID = "+empID);
		gLogger.info("checkPresentOrNot query..."+strBfr);
		Query query = hibSession.createSQLQuery(strBfr.toString());
		if(query != null)
			if(query.list().size() > 0){
				presentOrNot = 1;
			}
		return presentOrNot;
	}
	public void updateGPFAndDOB(String ddoCode,String empID, String sevaarthID,String newGPFSeries,
			String oldGPFSeries,String newAcntNo, String oldAcntNo, String OldDOB, long userID,String remarks){
		SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		String newDOB1 = null;
		/*try{
			newDOB1 = sdf2.format(frmt.parse(newDOB));
		}
		catch(Exception e){
			gLogger.info("updatiobn query..."+e);
		}*/
		//String OldDOB1 = frmt.format(OldDOB);
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("update  HR_PAY_gpf_birth_updation ");
		strBfr.append(" set OLD_GPF_SERIES ='"+oldGPFSeries+"', ");
		strBfr.append(" NEW_GPF_SERIES = '"+newGPFSeries+"'," );
		if(newAcntNo != null)
			strBfr.append(" NEW_ACCOUNT_NO = '"+newAcntNo +"'");
		else strBfr.append(" NEW_ACCOUNT_NO = null" );
		strBfr.append(",OLD_ACCOUNT_NO ='"+oldAcntNo+"'" );

		strBfr.append(" ,OLD_BIRTH_DATE = '"+OldDOB +"'");		
		strBfr.append(", UPDATED_BY = "+userID );	
		strBfr.append(" ,UPDATED_DATE = sysdate" );	
		strBfr.append(",remarks = '"+remarks+"'");
		strBfr.append( " where DDO_CODE = "+ddoCode);
		strBfr.append(" and SEVAARTH_ID = '" +sevaarthID);
		strBfr.append("' and EMP_ID = "+empID);
		gLogger.info("updatiobn query..."+strBfr);
		int query = hibSession.createSQLQuery(strBfr.toString()).executeUpdate();
	}
	public void insertGpfBirthDtls(String ddoCode,String empID, String sevaarthID,String newGPFSeries,
			String oldGPFSeries,String newAcntNo, String oldAcntNo, String OldDOB, long userId,String remarks){
		SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String newDOB1 = null;
		/*if(newDOB != null)
			try{
				newDOB1 = sdf2.format(frmt.parse(newDOB));
			}
			catch(Exception e){
				gLogger.info("updatiobn query..."+e);
			}*/
		//String OldDOB1 = frmt.format(OldDOB);
		long nextId = getNextSeqNum("HR_PAY_GPF_BIRTH_UPDATION");
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("insert into HR_PAY_GPF_BIRTH_UPDATION (GPF_BIRTH_ID,DDO_CODE,EMP_ID,SEVAARTH_ID,OLD_GPF_SERIES,NEW_GPF_SERIES,OLD_ACCOUNT_NO,NEW_ACCOUNT_NO,OLD_BIRTH_DATE,remarks,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)");
		strBfr.append("values (");
		strBfr.append(nextId+","+ddoCode+","+empID+",'"+sevaarthID+"',");
		strBfr.append("'"+oldGPFSeries+"','"+newGPFSeries+"','");
		strBfr.append(oldAcntNo+"',");
		if( newAcntNo != null)
			strBfr.append("'"+newAcntNo+"','"+OldDOB+"',");
		else strBfr.append("null,'"+OldDOB+"',");
		//if(newDOB1 != null)
		strBfr.append("'"+remarks+"'"+","+userId);
		//else strBfr.append("null,null,"+userId);
		strBfr.append(",sysdate,"+userId+",sysdate)");

		gLogger.info("updatiobn query..."+strBfr);
		hibSession.createSQLQuery(strBfr.toString()).executeUpdate();
	}
	public long getNextSeqNum(String tableName) {
		long seqId=0;
		StringBuffer sb= new StringBuffer();
		sb.append("select GENERATED_ID from CMN_TABLE_SEQ_MST where upper(table_name)='"+tableName+"'");
		Query query = ghibSession.createSQLQuery(sb.toString());
		seqId=Long.parseLong(query.uniqueResult().toString());		
		logger.info("seqId............"+seqId);
		long seqNo= seqId+1;
		logger.info("seqNo............"+seqId);
		StringBuffer sb2= new StringBuffer();
		sb2.append("update CMN_TABLE_SEQ_MST set GENERATED_ID="+seqNo+" where upper(TABLE_NAME)= '"+tableName+"'");
		Query query2 = ghibSession.createSQLQuery(sb2.toString());
		query2.executeUpdate();
		return seqId;
	}

	public List getAllTresasuriesList(){
		List treasuryList = null;

		StringBuilder sb = new StringBuilder();
		//SELECT LM.LOC_ID,LM.LOC_NAME FROM CMN_LOCATION_MST LM where OFFICE_CODE=8101
		sb.append("SELECT LM.locationCode, LM.locName FROM  CmnLocationMst LM ");
		sb
		.append("where departmentId = 100003 order by LM.locName ");		

		Query selectQuery = ghibSession.createQuery(sb.toString());
		if(selectQuery != null){
			treasuryList = selectQuery.list();
		}
		return treasuryList;
	}

	public List getDDODetails(String ddoCode){
		List obj = null;
		StringBuilder sb = new StringBuilder();
		//SELECT LM.LOC_ID,LM.LOC_NAME FROM CMN_LOCATION_MST LM where OFFICE_CODE=8101
		sb.append(" FROM OrgDdoMst ddo ");
		sb.append(" where ddoCode ="+ddoCode);		

		Query selectQuery = ghibSession.createQuery(sb.toString());
		if(selectQuery != null){
			obj = selectQuery.list();
		}
		return obj;
	}

	public List getDDOWiseBankDtls(String ddoCode){
		List obj = null;
		StringBuilder sb = new StringBuilder();
		//SELECT LM.LOC_ID,LM.LOC_NAME FROM CMN_LOCATION_MST LM where OFFICE_CODE=8101
		sb.append(" SELECT bank.BANK_CODE,bank.BANK_NAME FROM mst_dcps_emp em INNER JOIN org_ddo_mst ddo on ddo.DDO_CODE = em.ddo_code ");
		sb.append(" left outer join MST_BANK_PAY BANK ON cast(BANK.BANK_CODE as bigint) = cast(em.BANK_NAME as bigint) "); 
		sb.append(" left join ORG_EMP_MST emp on em.ORG_EMP_MST_ID = emp.emp_id ");
		sb.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = emp.user_id and usr.activate_flag = 1 ");
		sb.append("  where em.DDO_CODE =  "+ddoCode +" and bank.BANK_CODE is not null and (em.SUPER_ANN_DATE > sysdate or em.SUPER_ANN_DATE is null)  ");
		sb.append("  and em.REG_STATUS in (1,2) and em.FORM_STATUS  = 1 ");
		sb.append(" group by bank.BANK_NAME,bank.BANK_CODE ");		
		sb.append(" order by bank.BANK_NAME ");	
		logger.info("queryyyyy "+sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		if(selectQuery != null){
			obj = selectQuery.list();
		}
		return obj;
	}

	public List getDDOWiseBranchDtls(String ddoCode,String bankCode){
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		//SELECT LM.LOC_ID,LM.LOC_NAME FROM CMN_LOCATION_MST LM where OFFICE_CODE=8101
		sb.append(" SELECT Brnch.BRANCH_NAME,Brnch.BRANCH_id FROM mst_dcps_emp em ");
		sb.append(" left outer join RLT_BANK_BRANCH_PAY Brnch on cast(Brnch.BRANCH_ID as bigint)= cast(em.BRANCH_NAME as bigint)  ");
		sb.append(" left join ORG_EMP_MST emp on em.ORG_EMP_MST_ID = emp.emp_id ");
		sb.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = emp.user_id and usr.activate_flag = 1 ");
		sb.append(" where em.DDO_CODE = "+ddoCode);
		sb.append(" and cast(em.BANK_NAME as bigint)= cast( "+bankCode+" as bigint)");
		sb.append(" and Brnch.BRANCH_CODE is not null and (em.SUPER_ANN_DATE > sysdate or em.SUPER_ANN_DATE is null) ");
		sb.append("  and em.REG_STATUS in (1,2) and em.FORM_STATUS  = 1 ");
		sb.append(" GROUP by Brnch.BRANCH_id,Brnch.BRANCH_NAME ");		
		sb.append(" order by Brnch.BRANCH_NAME");
		logger.info("queryyyyy "+sb);	
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		if(selectQuery != null){
			List rsltLst = selectQuery.list();

			ComboValuesVO lObjComboValuesVO = null;
			if (rsltLst != null && rsltLst.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				//lObjComboValuesVO.setId("-1");
				//lObjComboValuesVO.setDesc("-- Select --");
				//lLstReturnList.add(lObjComboValuesVO);
				Object obj[];
				for (int liCtr = 0; liCtr < rsltLst.size(); liCtr++) {
					obj = (Object[]) rsltLst.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					//lObjComboValuesVO.setDesc(obj[1].toString());
					//String desc="("+obj[0].toString()+" )<![CDATA[ "+obj[1].toString()+"]]>";
					String desc="";

					desc="<![CDATA[ "+obj[1].toString()+"]]>";

					lObjComboValuesVO.setDesc(desc);
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		}
		return lLstReturnList;
	}

	public List getDDODOfcMst(String locCode){
		List obj = null;
		StringBuilder sb = new StringBuilder();
		//SELECT LM.LOC_ID,LM.LOC_NAME FROM CMN_LOCATION_MST LM where OFFICE_CODE=8101
		sb.append(" SELECT DDO_CODE,OFF_NAME,ADDRESS1,ADDRESS2 FROM MST_DCPS_DDO_OFFICE where LOC_ID = "+locCode);	
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		if(selectQuery != null){
			obj = selectQuery.list();
		}
		return obj;
	}

	public List getNotUidEidEmployees(String ddoCode){
		List obj = null;
		StringBuilder sb = new StringBuilder();		
		sb.append(" SELECT em.EMP_NAME, em.SEVARTH_ID, em.UID_NO, em.EID_NO,reason.NOT_UID_REASON,em.DCPS_EMP_ID FROM MST_DCPS_EMP em left outer join mst_dcps_not_uid_reason reason ");
		sb.append(" on reason.DCPS_EMP_ID = em.DCPS_EMP_ID and reason.SEVAARTH_ID = em.SEVARTH_ID ");
		sb.append(" left join ORG_EMP_MST emp on em.ORG_EMP_MST_ID = emp.emp_id ");
		sb.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = emp.user_id and usr.activate_flag = 1 ");
		sb.append("where em.DDO_CODE ="+ddoCode +"  and (em.UID_NO is null or em.UID_NO = 0) and em.FORM_STATUS = 1 and em.REG_STATUS in (1,2) and (em.SUPER_ANN_DATE > sysdate or em.SUPER_ANN_DATE is null) order by em.EMP_NAME ");

		gLogger.info("getNotUidEidEmployees query..."+sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		if(selectQuery != null){
			obj = selectQuery.list();
		}
		return obj;
	}

	public int checkEmpInUidOrNot(String dcpsEmpID, String sevaarthID){
		int presentOrNot = 0;
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("SELECT * FROM mst_dcps_not_uid_reason where dcps_emp_id = "+dcpsEmpID);
		strBfr.append(" and sevaarth_id = '" +sevaarthID+"'");		
		gLogger.info("checkPresentOrNot query..."+strBfr);
		Query query = hibSession.createSQLQuery(strBfr.toString());
		if(query != null)
			if(query.list().size() > 0){
				presentOrNot = 1;
			}
		gLogger.info("presentOrNot query..."+presentOrNot);
		return presentOrNot;
	}

	public void updateUidReason(String dcpsEmpID, String sevaarthID ,String reason,String userID){
		SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");	


		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("update  mst_dcps_not_uid_reason ");

		strBfr.append("set updated_by = "+userID );	
		strBfr.append(" ,updated_date = sysdate" );	
		strBfr.append(",NOT_UID_REASON = '"+reason+"'");
		strBfr.append( " where ");
		strBfr.append(" SEVAARTH_ID = '" +sevaarthID);
		strBfr.append("' and DCPS_EMP_ID = "+dcpsEmpID);
		gLogger.info("updatiobn query..."+strBfr);
		int query = hibSession.createSQLQuery(strBfr.toString()).executeUpdate();
	}

	public void insertUidReason(String ddoCode,String dcpsEmpID, String sevaarthID,long userId,String reason,String locId){
		SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String newDOB1 = null;

		long nextId = getNextSeqNum("MST_DCPS_NOT_UID_REASON");
		Session hibSession = getSession();
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("insert into MST_DCPS_NOT_UID_REASON (ID,SEVAARTH_ID,DCPS_EMP_ID,LOC_ID,DDO_CODE,NOT_UID_REASON,CREATED_DATE,CREATED_BY,UPDATED_DATE,UPDATED_BY)");
		strBfr.append("values (");
		strBfr.append(nextId+",'"+sevaarthID+"',"+dcpsEmpID+",");
		strBfr.append(" "+locId+", "+ddoCode+", '"+reason+"',sysdate,"+userId+",null,null)");

		gLogger.info("updatiobn query..."+strBfr);
		hibSession.createSQLQuery(strBfr.toString()).executeUpdate();
	}

	public List getUidEidDtls(String ddoCode,String cmbDesig){
		List obj = null;
		StringBuilder sb = new StringBuilder();		
		sb.append(" SELECT dcps.DCPS_EMP_ID, dcps.EMP_NAME, dcps.SEVARTH_ID, dcps.UID_NO,dcps.EID_NO FROM mst_dcps_emp dcps inner join org_emp_mst emp ");
		sb.append(" on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
		sb.append(" inner join ORG_USERPOST_RLT up on emp.USER_ID = up.USER_ID and up.ACTIVATE_FLAG = 1 ");
		sb.append(" inner join ORG_POST_DETAILS_RLT dtls on up.POST_ID = dtls.POST_ID ");
		sb.append(" and dcps.FORM_STATUS = 1 and dcps.REG_STATUS in (1,2) and (dcps.EMP_SERVEND_DT > sysdate OR DCPS.EMP_SERVEND_DT is null) ");
		sb.append(" where dcps.DDO_CODE = "+ddoCode);
		if(cmbDesig != null && !cmbDesig.equals("-1"))
			sb.append(" and dtls.DSGN_ID = "+cmbDesig);
		gLogger.info("getNotUidEidEmployees query..."+sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		if(selectQuery != null){
			obj = selectQuery.list();
		}
		return obj;
	}

	public List getPaybillDtls(String month, String year, String billNo){					
		List resList=null;

		Session hibSession = getSession();
		StringBuffer query = new StringBuffer(); 
		query.append( " select bill.BILL_GROUP_ID,bill.DESCRIPTION,mpg.APPROVE_FLAG");		
		query.append( " from  paybill_head_mpg AS mpg, mst_dcps_bill_group AS bill ");
		query.append( " where  "); 		
		query.append( " mpg.PAYBILL_MONTH=" + month + " and  mpg.PAYBILL_YEAR=" + year );
		query.append( "  and mpg.BILL_NO = bill.BILL_GROUP_ID ");				
		query.append( "  and mpg.approve_flag in (0, 5)");		
		query.append( " and bill.BILL_GROUP_ID="+billNo);
		query.append( "  order by bill.BILL_GROUP_ID");
		logger.info("Query for get getPaybillDtls is---->>>>"+query.toString());
		Query sqlQuery=hibSession.createSQLQuery(query.toString());		
		if (sqlQuery != null)
		resList=sqlQuery.list();					
		return resList;	
	}
	
	public int updatePaybillStatus(String month, String year, String billNo,String currStatus, String newStatus){
		int rowsUpdated;
		StringBuffer query = new StringBuffer();
		query.append(" update paybill_head_mpg set approve_flag = "+newStatus);
		query.append(" where BILL_NO="+billNo+" ");
		query.append(" and PAYBILL_MONTH=" + month + " and PAYBILL_YEAR=" + year );
		query.append(" and approve_flag ="+currStatus);
		logger.info("Query for get getPaybillDtls is---->>>>"+query.toString());
		Session hibSession = getSession();
		rowsUpdated = hibSession.createSQLQuery(query.toString()).executeUpdate();
		logger.info("no of rwos updated... "+rowsUpdated);
		return rowsUpdated;
	}
	
	public int checkDulicateUidEid(String id, String type){
		int count =0;
		StringBuffer query = new StringBuffer();
		query.append(" SELECT DCPS_EMP_ID FROM mst_dcps_emp where ");
		if(type.equals("UID"))
		query.append(" UID_NO = '"+id+"'");
		if(type.equals("EID"))
		query.append("  EID_NO = '"+id+"'" );
		
		logger.info("Query for get getPaybillDtls is---->>>>"+query.toString());
		Session hibSession = getSession();
		Query rslt  = hibSession.createSQLQuery(query.toString());
		if(rslt != null)
			count = rslt.list().size();
		logger.info("no of rwos updated... "+count);
		return count;
	}
}
