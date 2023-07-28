package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.pensionpay.valueobject.StgAcNoErrorDtls;
import com.tcs.sgv.pensionpay.valueobject.StgPnsnBankAcNo;

public class UploadPensionBankACNoExcelDAOImpl {

	private Session ghibSession = null;
	private SessionFactory lSess = null;
	private Log gLogger = LogFactory.getLog(getClass());
	
	public UploadPensionBankACNoExcelDAOImpl(SessionFactory sesFactory) {
		ghibSession = sesFactory.getCurrentSession();
		lSess = sesFactory;
	}	
	public void setStgPnsnBankAcNo(Object paraXlsData[][], int paraSizeOfList, Long paraLngDelvId, Long paraLngPostId,Long paraLngUserId,Map inputMap) throws Exception {

		StgPnsnBankAcNo	lObjPnsnBankAcNo = null;
		List<StgPnsnBankAcNo> lLstPnsnBankAcNo = new ArrayList<StgPnsnBankAcNo>();
		Long lLngPkCntStgPnsnBankAcNo = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("STG_PNSN_BANK_AC_NO", inputMap, paraSizeOfList);
		Long lLngStgPnsnBankAcNoId;
		String lStrLocCode = (String) inputMap.get("LocCode");
		try{
			for (int i = 1; i < paraSizeOfList; i++) {
				lObjPnsnBankAcNo = new StgPnsnBankAcNo();
				lLngStgPnsnBankAcNoId = ++lLngPkCntStgPnsnBankAcNo;
				lLngStgPnsnBankAcNoId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngStgPnsnBankAcNoId, inputMap);
				lObjPnsnBankAcNo.setStgPnsnBankAcNoId(lLngStgPnsnBankAcNoId);
				lObjPnsnBankAcNo.setDelvId(paraLngDelvId);
				lObjPnsnBankAcNo.setPpoNo(paraXlsData[i][0].toString().trim());
				lObjPnsnBankAcNo.setFirstName(paraXlsData[i][1].toString().trim());
				lObjPnsnBankAcNo.setBankName(paraXlsData[i][2].toString().trim());
				lObjPnsnBankAcNo.setBranchName(paraXlsData[i][3].toString().trim());
				lObjPnsnBankAcNo.setOldAccountNo(paraXlsData[i][4].toString().trim());
				lObjPnsnBankAcNo.setNewAccountNo(paraXlsData[i][5].toString().trim());
				lObjPnsnBankAcNo.setPensionerCode(paraXlsData[i][6].toString().trim());
				lObjPnsnBankAcNo.setLocationCode(lStrLocCode);
				lObjPnsnBankAcNo.setCreatedDate(DBUtility.getCurrentDateFromDB());
				lObjPnsnBankAcNo.setCreatedPostId(paraLngPostId);
				lObjPnsnBankAcNo.setCreatedUserId(paraLngUserId);
				lLstPnsnBankAcNo.add(lObjPnsnBankAcNo);
				ghibSession.save(lObjPnsnBankAcNo);
			}
			ghibSession.flush();
		}catch (Exception e) {
			gLogger.error("error occcured in setStgPnsnBankAcNo method ::" + e);
			inputMap.put("Msg", "NotValidExcel");
			throw e;
		}
		
	}
	
	public List getInvalidPPOList(String lStrLocationCode,Long lLngDevId) throws Exception{
		
		List lLstInvalidPPO = new ArrayList();
		StringBuffer strQuery = new StringBuffer();
		try{			
			strQuery.append(" select pba.ppo_no,pba.PENSIONER_CODE,pba.NEW_ACCOUNT_NO from STG_PNSN_BANK_AC_NO pba where not exists ( ");
			strQuery.append(" select 1 from TRN_PENSION_RQST_HDR  prh ");
			strQuery.append(" where prh.pensioner_code = pba.pensioner_code ");
			strQuery.append(" and prh.PPO_NO = pba.PPO_NO ");
			strQuery.append(" and pba.location_code = prh.location_code ");
			strQuery.append(" and prh.location_code = :LocationCode )");
			strQuery.append(" and pba.DELV_ID = :DevId ");
					
			Query lQuery = ghibSession.createSQLQuery(strQuery.toString());
			lQuery.setParameter("LocationCode", lStrLocationCode);
			lQuery.setParameter("DevId", lLngDevId);
			lLstInvalidPPO = lQuery.list();
		}catch (Exception e) {
			gLogger.error("error occcured in getInvalidPPOList method ::" + e);
			throw e;
		}
		
		return lLstInvalidPPO;
	}
	public List getPPONoAndPensionerCode(String lStrLocationCode,Long lLngDevId) throws Exception{
		
		List lLstPPONoAndPensionerCode = new ArrayList();
		StringBuffer strQuery = new StringBuffer();
		try{			
			strQuery.append(" select ppoNo,pensionerCode,newAccountNo from StgPnsnBankAcNo ");
			strQuery.append(" where locationCode = :LocationCode ");
			strQuery.append(" and delvId = :DevId ");
					
			Query lQuery = ghibSession.createQuery(strQuery.toString());
			lQuery.setParameter("LocationCode", lStrLocationCode);
			lQuery.setParameter("DevId", lLngDevId);
			lLstPPONoAndPensionerCode = lQuery.list();
		}catch (Exception e) {
			gLogger.error("error occcured in getPPONoAndPensionerCode method ::" + e);
			throw e;
		}
		
		return lLstPPONoAndPensionerCode;
	}
	public void insertErrorPPOData(List<String> lLstPPO,String lStrLocationCode,Long lLngDevId,Map inputMap) throws Exception{
				
		StgAcNoErrorDtls lObjAcNoErrorDtls = null;
		Long lLngInvalidPPONo = 140040L; //Invalid PPO
		Long lLngPostId = (Long) inputMap.get("PostId");
		Long lLngUserId = (Long) inputMap.get("UserId");
		List<String> lLstErrorPPOBatch = new ArrayList<String>();
		Long lLngPkStgAcNoError = null;
		StringBuilder lSBPPONo = new StringBuilder();
		String lStrCurrPPONo = "";
		
		try{
			for (String lStrPPONo : lLstPPO) {
				lStrCurrPPONo = lStrPPONo + ",";
				if ((lSBPPONo.length() + lStrCurrPPONo.length()) > 1000) {
					lLstErrorPPOBatch.add(lSBPPONo.toString());
					lSBPPONo = new StringBuilder();
					lSBPPONo.append(lStrCurrPPONo);
				} else {
					lSBPPONo.append(lStrCurrPPONo);
				}
			}
			if (lSBPPONo.length() > 0) {
				lLstErrorPPOBatch.add(lSBPPONo.toString());
			}			
			
			try{			
				if(!lLstErrorPPOBatch.isEmpty()){
					Long lLngPkCntStgAcNoError = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("STG_AC_NO_ERROR_DTLS", inputMap, lLstErrorPPOBatch.size());
					
					for(int i=0;i<lLstErrorPPOBatch.size();i++){							
						lLngPkStgAcNoError = ++lLngPkCntStgAcNoError;
						lLngPkStgAcNoError = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngPkStgAcNoError, inputMap);
						lObjAcNoErrorDtls = new StgAcNoErrorDtls();
						lObjAcNoErrorDtls.setStgAcNoErrorDtlsId(lLngPkStgAcNoError);
						lObjAcNoErrorDtls.setDelvId(lLngDevId);		
						lObjAcNoErrorDtls.setPpoNo(lLstErrorPPOBatch.get(i).toString());
						lObjAcNoErrorDtls.setErrorCode(lLngInvalidPPONo);
						lObjAcNoErrorDtls.setLocationCode(lStrLocationCode);
						lObjAcNoErrorDtls.setCreatedPostId(lLngPostId);
						lObjAcNoErrorDtls.setCreatedUserId(lLngUserId);
						lObjAcNoErrorDtls.setCreatedDate(DBUtility.getCurrentDateFromDB());
						lObjAcNoErrorDtls.setUpdatedDate(null);
						lObjAcNoErrorDtls.setUpdatedUserId(null);
						lObjAcNoErrorDtls.setUpdatedPostId(null);
						ghibSession.save(lObjAcNoErrorDtls);
						ghibSession.flush();	
					}
				}
			}catch (Exception e) {
				gLogger.error("error occcured in insertErrorPPOData method ::" + e);
				throw e;
			}
		}catch (Exception e) {
			gLogger.error("error occcured in creating Batch in  insertErrorPPOData method ::" + e);
			throw e;
		}
	}
	public void updateBankAcNo(List<String> lLstValidPPO,Map<Object,Object> lMapPensionerCode,Map<Object,Object> lMapNewBankAcNo,Map inputMap) throws Exception{
				
		Long lLngPostId = (Long) inputMap.get("PostId");
		Long lLngUserId = (Long) inputMap.get("UserId");
		Long lLngDelvId = (Long) inputMap.get("DelvId");
		String lStrLocCode = (String) inputMap.get("LocCode");
		
		Long lLngInvalidBankAcNo = 140041L;//Invalid Bank AC No
		String lStrPensionerCode = null;
		String lStrBankAcNo = null;
		
		Object lObjBankAcNo = null;
		Object lObjPensionerCode = null;
		StgAcNoErrorDtls lObjAcNoErrorDtls = null;
		List<String> lLstInvalidPPO = new ArrayList<String>();
		
		List<String> lLstErrorPPOBatch = new ArrayList<String>();
		Long lLngPkStgAcNoError = null;
		StringBuilder lSBPPONo = new StringBuilder();
		String lStrCurrPPONo = "";
		
		try{			
			for(int i=0;i<lLstValidPPO.size();i++){
				lObjBankAcNo = lMapNewBankAcNo.get(lLstValidPPO.get(i));
				lObjPensionerCode = lMapPensionerCode.get(lLstValidPPO.get(i));
				if(lObjBankAcNo != null && lObjPensionerCode != null){
					lStrBankAcNo = lObjBankAcNo.toString().trim();
					lStrPensionerCode = lObjPensionerCode.toString().trim();	
					if(containsOnlyNumbers(lStrBankAcNo) && lStrBankAcNo.length()<= 20){
						StringBuffer strQuery = new StringBuffer();
						try{			
							strQuery.append(" update MstPensionerDtls set accountNo = :accountNo where pensionerCode = :pensionerCode ");
									
							Query lQuery = ghibSession.createQuery(strQuery.toString());
							lQuery.setParameter("pensionerCode", lStrPensionerCode);
							lQuery.setParameter("accountNo", lStrBankAcNo);
							lQuery.executeUpdate();
							
						}catch (Exception e) {
							gLogger.error("error occcured in getPPONoAndPensionerCode method ::" + e);
							throw e;
						}
					}else{
						lLstInvalidPPO.add(lLstValidPPO.get(i));
					}
				}else{
					lLstInvalidPPO.add(lLstValidPPO.get(i));
				}				
			}
			if(lLstInvalidPPO != null && !lLstInvalidPPO.isEmpty()){
					for (String lStrPPONo : lLstInvalidPPO) {
						lStrCurrPPONo = lStrPPONo + ",";
						if ((lSBPPONo.length() + lStrCurrPPONo.length()) > 1000) {
							lLstErrorPPOBatch.add(lSBPPONo.toString());
							lSBPPONo = new StringBuilder();
							lSBPPONo.append(lStrCurrPPONo);
						} else {
							lSBPPONo.append(lStrCurrPPONo);
						}
					}
					if (lSBPPONo.length() > 0) {
						lLstErrorPPOBatch.add(lSBPPONo.toString());
					}
				try{	
					if(!lLstErrorPPOBatch.isEmpty()){
						Long lLngPkCntStgAcNoError = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("STG_AC_NO_ERROR_DTLS", inputMap, lLstErrorPPOBatch.size());
						
						for(int i=0;i<lLstErrorPPOBatch.size();i++){							
							lLngPkStgAcNoError = ++lLngPkCntStgAcNoError;
							lLngPkStgAcNoError = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngPkStgAcNoError, inputMap);
							lObjAcNoErrorDtls = new StgAcNoErrorDtls();
							lObjAcNoErrorDtls.setStgAcNoErrorDtlsId(lLngPkStgAcNoError);
							lObjAcNoErrorDtls.setDelvId(lLngDelvId);		
							lObjAcNoErrorDtls.setPpoNo(lLstErrorPPOBatch.get(i).toString());
							lObjAcNoErrorDtls.setErrorCode(lLngInvalidBankAcNo);
							lObjAcNoErrorDtls.setLocationCode(lStrLocCode);
							lObjAcNoErrorDtls.setCreatedPostId(lLngPostId);
							lObjAcNoErrorDtls.setCreatedUserId(lLngUserId);
							lObjAcNoErrorDtls.setCreatedDate(DBUtility.getCurrentDateFromDB());
							lObjAcNoErrorDtls.setUpdatedDate(null);
							lObjAcNoErrorDtls.setUpdatedUserId(null);
							lObjAcNoErrorDtls.setUpdatedPostId(null);
							ghibSession.save(lObjAcNoErrorDtls);
							ghibSession.flush();	
						}
					}
				}catch (Exception e) {
					gLogger.error("error occcured in insertErrorPPOData method ::" + e);
					throw e;
				}
			}						
		}catch (Exception e) {
			gLogger.error("error occcured in updateBankAcNo method ::" + e);
			throw e;
		}
	}
	public boolean containsOnlyNumbers(String str) {
        
        //It can't contain only numbers if it's null or empty...
        if (str == null || str.length() == 0)
            return false;
        
        for (int i = 0; i < str.length(); i++) {

            //If we find a non-digit character we return false.
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        
        return true;
    }

	public List getErroredPPONoList(String lStrLocationCode,Long lLngDevId) throws Exception{
		
		List lLstErroredPPONoList = new ArrayList();
		StringBuffer strQuery = new StringBuffer();
		try{			
			strQuery.append(" select ppoNo,errorCode from StgAcNoErrorDtls ");
			strQuery.append(" where locationCode = :LocationCode ");
			strQuery.append(" and delvId = :DevId ");
					
			Query lQuery = ghibSession.createQuery(strQuery.toString());
			lQuery.setParameter("LocationCode", lStrLocationCode);
			lQuery.setParameter("DevId", lLngDevId);
			lLstErroredPPONoList = lQuery.list();			
		}catch (Exception e) {
			gLogger.error("error occcured in getErroredPPONoList method ::" + e);
			throw e;
		}
		
		return lLstErroredPPONoList;
	}
}
