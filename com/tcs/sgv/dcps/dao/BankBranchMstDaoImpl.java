package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.dao.BankBranchMappingDAOImpl;
import java.util.*;

public class BankBranchMstDaoImpl extends GenericDaoHibernateImpl{

	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(BankBranchMstDaoImpl.class);

	//private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public BankBranchMstDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}
	
	public List getExistingBanks(){
		StringBuffer sb = new StringBuffer();
		List bnkDtls = null;
		sb.append("select bank_code, bank_name from MST_BANK_PAY order by bank_name");
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			bnkDtls = query.list();			
		}
		return bnkDtls;
	}
	
	public List getRBIBankNames(){
		StringBuffer sb = new StringBuffer();
		List bnkDtls = null;
		sb.append("select bank_code, bank_name from rbi_bank_mst order by bank_name");
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			bnkDtls = query.list();			
		}		
		return bnkDtls;
	}
	
	public List getExtBankBranch(long bankCode){
		StringBuffer sb = new StringBuffer();
		List lLstBranch = null;
		//sb.append("select bank_code, bank_name from rbi_bank_mst");	
		try{
		sb.append("SELECT org.BRANCH_ID , org.BRANCH_NAME,org.BRANCH_NAME||'', org.IFSC_CODE,tmp.MICR_CODE,tmp.city  FROM RLT_BANK_BRANCH_PAY org left outer join RLT_BANK_BRANCH_PAY_TMP tmp ");
		sb.append(" on org.BRANCH_ID = tmp.BRANCH_ID  ");
		sb.append(" and tmp.IS_MAPPED = 1 where org.BANK_CODE = "+bankCode);
		sb.append(" order by org.BRANCH_NAME ");
		gLogger.info("queyy "+sb.toString());
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			lLstBranch = query.list();			
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return lLstBranch;
		
	}
	public List getRBIBankBranch(long bankCode){
		StringBuffer sb = new StringBuffer();
		List lLstBranch = null;
		//sb.append("select bank_code, bank_name from rbi_bank_mst");
		
		
		sb.append("SELECT branch_Id, branch_Name from RBI_BANK_BRANCH_RLT  ");
		sb.append(" WHERE bank_code = "+bankCode+" order by branch_Name, branch_Id ");
		
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			lLstBranch = query.list();			
		}
		return lLstBranch;
	}
	
	public List getCityList(){
		StringBuffer sb = new StringBuffer();
		List lLstBranch = null;
		//sb.append("select bank_code, bank_name from rbi_bank_mst");	
		
		sb.append("SELECT city_id, city_Name from CMN_CITY_MST order by city_Name");		

		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			lLstBranch = query.list();			
		}
		return lLstBranch;
	}
	
	public List getBrachesFrmCity(long rbibnkcode, long cityId){
		StringBuffer sb = new StringBuffer();
		List lLstBranch = null;
		List lLstReturnList = null;
		//sb.append("select bank_code, bank_name from rbi_bank_mst");	
		
		sb.append("SELECT BRANCH_ID, BRANCH_NAME FROM RBI_BANK_BRANCH_RLT where BANK_CODE = ");
		sb.append(rbibnkcode);
		sb.append(" and branch_city in (SELECT CITY_NAME FROM CMN_CITY_MST where CITY_CODE = ");
		sb.append(cityId);
		sb.append(") order by BRANCH_NAME");
		gLogger.info("queyy "+sb.toString());
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			lLstBranch = query.list();	
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstBranch != null && lLstBranch.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
				for (int liCtr = 0; liCtr < lLstBranch.size(); liCtr++) {
					obj = (Object[]) lLstBranch.get(liCtr);
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
		}
		return lLstReturnList;
	}
	
	public List getIFSCFrmBranchCode(long brnchCode){
		StringBuffer sb = new StringBuffer();
		List brncDtls= null;
		//sb.append("select bank_code, bank_name from rbi_bank_mst");	
		
		sb.append("SELECT IFSC_CODE||'',BRANCH_CITY||'',MICR_CODE||'' FROM RBI_BANK_BRANCH_RLT where BRANCH_ID = ");
		sb.append(brnchCode);
		gLogger.info("ifscCode1 "+sb.toString());
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			brncDtls = query.list();
			gLogger.info("ifscCode "+brncDtls.size());			
		}
		return brncDtls;
	}
	
	public int mapBankBranches(long extnBrnchCode, long rbiBranchCode,String ifscCode, String city, String micrCode){
		StringBuffer sb = new StringBuffer();
		gLogger.info("in dao ipmpl..");
		//String ifscCode= null;
		//sb.append("select bank_code, bank_name from rbi_bank_mst");	
		
		sb.append("	update RLT_BANK_BRANCH_PAY_TMP set  is_mapped=1, BRANCH_NAME = ");
		sb.append(" (select branch_name from RBI_BANK_BRANCH_RLT where BRANCH_ID = ");
		sb.append(rbiBranchCode);
		sb.append(" )  ");
		//sb.append(rbiBranchCode);
		sb.append("	,IFSC_CODE = ");
		sb.append("'"+ifscCode+"'");
		sb.append("	,MICR_CODE = ");
		sb.append("'"+micrCode+"'");		
		sb.append("	,CITY = ");
		sb.append("'"+city+"'");		
		sb.append(",BRANCH_ADDRESS = (select BRANCH_ADDRESS from RBI_BANK_BRANCH_RLT where BRANCH_ID = ");
		sb.append(rbiBranchCode);
		sb.append( ") where BRANCH_ID = ");
		sb.append(extnBrnchCode);
		gLogger.info("ifscCode1 "+sb.toString());
		Query query = ghibSession.createSQLQuery(sb.toString());
		int reslt = query.executeUpdate();
		
		return reslt;
	} 
}
