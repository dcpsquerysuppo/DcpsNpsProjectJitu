package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.common.valueobject.RltBankBranchPay;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class BranchMasterDAOImpl extends GenericDaoHibernateImpl<RltBankBranch, Long> implements BranchMasterDAO 
{
	
 	public BranchMasterDAOImpl(Class<RltBankBranch> type, SessionFactory sessionFactory)
    {
        super(type);
        setSessionFactory(sessionFactory);
    }
	public List getAllBranchMasterData(long langId)
    {
        Criteria objCrt = null;
        List  list = null;
            Session hibSession = getSession();
            try{
            String strQuery = "SELECT RBB.BRANCH_ID, RBB.BRANCH_NAME, RBB.BANK_CODE, RBB.MICR_CODE, MB.BANK_NAME FROM RLT_BANK_BRANCH RBB, MST_BANK MB WHERE RBB.LANG_ID =" + langId+ " and RBB.BANK_CODE != 0 " +
            		" and RBB.BANK_CODE = MB.BANK_CODE and MB.LANG_ID =" + langId+ " order by MB.BANK_NAME,RBB.BRANCH_NAME";
            Query query = hibSession.createSQLQuery(strQuery);
            list = query.list();
            }catch (Exception e) {
            	logger.error("Exception in getAllBranchMasterData : ", e);
			}
        return list;
    }
	
	public List searchBranchMasterFromBranch(long langId, String lStrBranchName)
    {
        Criteria objCrt = null;
        List  list = null;
            Session hibSession = getSession();
            try{
            String strQuery = "SELECT RBB.BRANCH_ID, RBB.BRANCH_NAME, RBB.BANK_CODE, RBB.MICR_CODE, MB.BANK_NAME FROM RLT_BANK_BRANCH RBB, MST_BANK MB WHERE RBB.LANG_ID =" + langId+ " and RBB.BANK_CODE != 0 " +
            		" and RBB.BANK_CODE = MB.BANK_CODE and MB.LANG_ID =" + langId+ " and RBB.BRANCH_NAME = '"+lStrBranchName+"' order by MB.BANK_NAME,RBB.BRANCH_NAME";
            Query query = hibSession.createSQLQuery(strQuery);
            list = query.list();
            }catch (Exception e) {
            	logger.error("Exception in searchBranchMasterFromBranch : ", e);
			}
        return list;
    }
	
	public List searchBranchMasterFromBank(long langId, String lStrBankName)
    {
        Criteria objCrt = null;
        List  list = null;
            Session hibSession = getSession();
            try{
            String strQuery = "SELECT RBB.BRANCH_ID, RBB.BRANCH_NAME, RBB.BANK_CODE, RBB.MICR_CODE, MB.BANK_NAME FROM RLT_BANK_BRANCH RBB, MST_BANK MB WHERE RBB.LANG_ID =" + langId+ " and RBB.BANK_CODE != 0 " +
            		" and RBB.BANK_CODE = MB.BANK_CODE and MB.LANG_ID =" + langId+ " and MB.BANK_NAME = '"+lStrBankName+"' order by MB.BANK_NAME,RBB.BRANCH_NAME";
            Query query = hibSession.createSQLQuery(strQuery);
            list = query.list();
            }catch (Exception e) {
            	logger.error("Exception in searchBranchMasterFromBank : ", e);
			}
        return list;
    }
	
	public List searchBranchMasterFromBankBranch(long langId, String lStrBankName, String lStrBranchName)
    {
        Criteria objCrt = null;
        List  list = null;
            Session hibSession = getSession();
            try{
            String strQuery = "SELECT RBB.BRANCH_ID, RBB.BRANCH_NAME, RBB.BANK_CODE, RBB.MICR_CODE, MB.BANK_NAME FROM RLT_BANK_BRANCH RBB, MST_BANK MB WHERE RBB.LANG_ID =" + langId+ " and RBB.BANK_CODE != 0 " +
            		" and RBB.BANK_CODE = MB.BANK_CODE and MB.LANG_ID =" + langId+ " and MB.BANK_NAME = '"+lStrBankName+"' and RBB.BRANCH_NAME = '"+lStrBranchName+"' order by MB.BANK_NAME,RBB.BRANCH_NAME";
            Query query = hibSession.createSQLQuery(strQuery);
            list = query.list();
            }catch (Exception e) {
            	logger.error("Exception in searchBranchMasterFromBankBranch : ", e);
			}
        return list;
    }
	
	public RltBankBranch getBranchIdData(String branchId)
    {
		RltBankBranch hrBranchInfo = new RltBankBranch();
        
            Session hibSession = getSession();
            String query1 = "from RltBankBranch as branchLookup where branchLookup.branchId = "+ branchId;
            Query sqlQuery1 = hibSession.createQuery(query1);
            hrBranchInfo = (RltBankBranch)sqlQuery1.uniqueResult();            
        
        return hrBranchInfo;
    }
	
	public List getBranchIdFromBankCode(String branchId)
    {
		 List branchList=null;
		 
		 RltBankBranchPay hrBranchInfo = new RltBankBranchPay();
        
            Session hibSession = getSession();
            String query1 = "from RltBankBranchPay as branchLookup where branchLookup.branchId = "+ branchId;
            Query sqlQuery1 = hibSession.createQuery(query1);
            branchList = sqlQuery1.list();            
        
        return branchList;
    }
	
	public MstBank getBankCode(long bankId)
    {
 		MstBank hrBranchInfo = new MstBank();
        
            Session hibSession = getSession();
            String query1 = "from MstBank as branchLookup where branchLookup.bankId = "+ bankId;
            Query sqlQuery1 = hibSession.createQuery(query1);
            hrBranchInfo = (MstBank)sqlQuery1.uniqueResult();            
            
        return hrBranchInfo;
    }	
	
	public List checkBranchName(String branchName,String bank_code,long langId)
	{
		List branchNameList=null;
        
        Session hibSession = getSession();
        String query1 = "from RltBankBranch where lower(branchName) = lower('"
                + branchName + "') and bankCode=" + bank_code+" and langId ="+ langId;
        
        Query sqlQuery1 = hibSession.createQuery(query1);
        
        //sqlQuery1.setParameter("param",langId);
        branchNameList = sqlQuery1.list();
    
    return branchNameList;
	}
	
	public String checkIFSCcode(String ifscCode)
	{
		String lStrRes = "";
		List lLstresData = null;
		Session hibSession = getSession();
        String query1 = "from RltBankBranch where lower(ifscCode) = lower('"
                + ifscCode + "')";
        
        Query sqlQuery1 = hibSession.createQuery(query1);
        lLstresData = sqlQuery1.list();
        
        if(lLstresData.size()> 0){
        	lStrRes = "Y";
        }else{
        	lStrRes = "N";
        }
        return lStrRes;
	}
	
	public String checkMicrCode(String micrCode)
	{
		String lStrRes = "";
		String lStrBranchName = "";
		String lStrBranchAddr = "";
		List lLstresData = null;
		Session hibSession = getSession();
        String query1 = "Select branchName, branchAddress from RltBankBranchTmp where micrCode = '"+ micrCode + "'" ;
        
        Query sqlQuery1 = hibSession.createQuery(query1);
        lLstresData = sqlQuery1.list();
        
        if(lLstresData.size()> 0){
        	Object []lObj = (Object[]) lLstresData.get(0);
        	lStrBranchName = lObj[0].toString();
        	lStrBranchAddr = lObj[1].toString();
        	lStrRes = lStrBranchName + "#" + lStrBranchAddr;
        }else{
        	lStrRes = "N";
        }
        return lStrRes;
	}
	
	public String getBankCodeFromId(Long lLngBankId)
	{
		String lStrBankCode = "";
		Session hibSession = getSession();
		String query1 = "select bankCode from MstBank  where bankId = "+ lLngBankId;
		Query sqlQuery1 = hibSession.createQuery(query1);
		lStrBankCode = (String) sqlQuery1.list().get(0);
		
		return lStrBankCode;
	}
	
	public List<ComboValuesVO> getAllTreasury() throws Exception {

		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		Session ghibSession = getSession();
		try {
			lStrQuery.append(" Select locId,locName ");
			lStrQuery.append(" FROM CmnLocationMst ");
			lStrQuery.append(" WHERE departmentId = :departmentId");
			lStrQuery.append(" AND cmnLanguageMst.langId =:langId");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("departmentId", 100003);
			hqlQuery.setLong("langId", 1);
			
			lLstResultList = hqlQuery.list();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator itr = lLstResultList.iterator();

				ComboValuesVO cmbVO = null;
				Object[] obj = null;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstTreasury.add(cmbVO);
				}
			}
			
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstTreasury;
	}
	
	public void updateTableEntries(Long lLngBrnchIdOld, Long lLngBrnchIdNew) throws Exception
	{
		Session ghibSession = getSession();
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
				String[] lLstTables = {"HST_MST_PENSIONER_DTLS","HST_RLT_AUDITOR_BANK","HST_RLT_BANK_BRANCH","MST_PENSIONER_DTLS","MST_PENSIONER_FAMILY_DTLS",
						"MST_PENSIONER_NOMINEE_DTLS","RLT_AUDITOR_BANK","RLT_BANK_BRANCH","RLT_BANK_BRANCH_GRP","RLT_BILL_PARTY","TRN_MONTHLY_CHANGE_RQST",
						"TRN_MONTHLY_PENSION_RECOVERY_DTLS","TRN_PENSION_BILL_HDR","TRN_PENSION_BILL_HDR_ARC","TRN_PENSION_CHANGE_HDR","TRN_PENSION_SUPPLY_BILL_DTLS"};
				
				for(int i=0;i<lLstTables.length;i++)
				{
					lSBQuery = new StringBuilder();
					lSBQuery.append("UPDATE "+lLstTables[i]+" SET BRANCH_CODE = :newBrnchCode WHERE BRANCH_CODE = :oldBrnchCode ");
					lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					lQuery.setParameter("newBrnchCode", lLngBrnchIdNew.toString());
					lQuery.setParameter("oldBrnchCode", lLngBrnchIdOld.toString());
					lQuery.executeUpdate();
				}
		}catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}
	
	public String chkBranchId(Long lLngBranchId) throws Exception
	{
		Session ghibSession = getSession();
		List lLstData = null;
		String lStrResData = "";
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT branchId FROM RltBankBranch WHERE branchId =:brnchId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("brnchId", lLngBranchId);
			lLstData = lQuery.list();
			
			if(lLstData.size() > 0){
				lStrResData = "Y";
			}else{
				lStrResData = "N";
			}
		}catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrResData;
	}
	
	public Long getBranchCodeFromBranchId(Long lLngBranchId) throws Exception
	{
		Session ghibSession = getSession();
		Long lLngBranchCode = null;
		String lStrResData = "";
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT branchCode FROM RltBankBranch WHERE branchId =:brnchId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("brnchId", lLngBranchId);
			lLngBranchCode = (Long) lQuery.list().get(0);			
		}catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLngBranchCode;
	}
	
	public List branchMasterAutoComplt(long langId, String lStrBranchName, String lStrBankCode)
	{	
		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;
		try {
			Session ghibSession = getSession();
			
			sb.append("SELECT branchId, branchName FROM RltBankBranch WHERE langId =:langId and bankCode != 0 ");
			sb.append("and UPPER(branchName) like UPPER(:branchName) ");
			if(!lStrBankCode.equals("")){
				sb.append("and bankCode =:bankCode ");
			}
			selectQuery = ghibSession.createQuery(sb.toString());
			selectQuery.setParameter("langId", langId);
			selectQuery.setParameter("branchName", lStrBranchName + '%');
			if(!lStrBankCode.equals("")){
				selectQuery.setParameter("bankCode", lStrBankCode);
			}

			List resultList = selectQuery.list();

			cmbVO = new ComboValuesVO();

			if (resultList != null && resultList.size() > 0) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[1].toString());
					cmbVO.setDesc(obj[1].toString());
					finalList.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in bankMasterAutoComplt : ", e);			
		}
		return finalList;
		
	}
	
	public String getBankCodeFromName(String lStrBankName) throws Exception
	{
		List lLstResData = null;
		String lStrBankCode = "";
		
		try{
			Session ghibSession = getSession();
			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT bankCode FROM MstBank ");
			lSBQuery.append("WHERE bankName =:bankName ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("bankName", lStrBankName);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrBankCode = (String) lLstResData.get(0);
			}
		}catch (Exception e) {
			logger.error("Exception in getBankCodeFromName : ", e);
		}
		
		return lStrBankCode;
	}
}
