package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls;



public class SupplementaryBillDAOImpl extends GenericDaoHibernateImpl implements SupplementaryBillDAO {

	Log gLogger = LogFactory.getLog(getClass());
	private Session ghibSession = null;
	
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	public SupplementaryBillDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	// public List getSavedSupplementaryPartyRequests(String
	// lStrPnsnrCode,String lStrLocCode) throws Exception
	// {
	// StringBuffer lSBQuery = new StringBuffer();
	// List resultList;
	// Session ghibSession = getSession();
	// Object[] tuple;
	// Iterator itr;
	// ArrayList arrlstSuppDtls = null;
	//
	// try
	// {
	// lSBQuery.append(" SELECT ppo_no PPONO," +
	// " party_name NAME," +
	// " account_no ACCNO," +
	// " difference_amount TOTDIFF," +
	// " gross_amount GROSS," +
	// " deduction_a DEDA," +
	// " net_amount NET," +
	// " STATUS STATUSFLAG, " +
	// " bill_type BILLTYPE, "+
	// " pensioner_code PENCODE, "+
	// " pension_Supply_Bill_Id PK, "+
	// " head_code HEAD, " +
	// " deduction_b DEDB "+
	// " FROM trn_pension_supply_bill_dtls s" +
	// " WHERE s.location_code = :location_code AND s.pensioner_code = :lPnsnerCode"+
	// "   AND s.bill_no IS NULL AND s.branch_code IS NULL");
	//			
	// SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// hqlQuery.addScalar("PPONO", Hibernate.STRING);//0
	// hqlQuery.addScalar("NAME", Hibernate.STRING);//1
	// hqlQuery.addScalar("ACCNO", Hibernate.STRING); //2
	// hqlQuery.addScalar("TOTDIFF", Hibernate.BIG_DECIMAL);//3
	// hqlQuery.addScalar("GROSS", Hibernate.BIG_DECIMAL); //4
	// hqlQuery.addScalar("DEDA", Hibernate.BIG_DECIMAL); //5
	// hqlQuery.addScalar("NET", Hibernate.BIG_DECIMAL); //6
	// hqlQuery.addScalar("STATUSFLAG", Hibernate.LONG); //7
	// hqlQuery.addScalar("BILLTYPE", Hibernate.STRING); //8
	// hqlQuery.addScalar("PENCODE", Hibernate.STRING); //9
	// hqlQuery.addScalar("PK", Hibernate.LONG); //10
	// hqlQuery.addScalar("HEAD", Hibernate.BIG_DECIMAL); //11
	// hqlQuery.addScalar("DEDB", Hibernate.BIG_DECIMAL); //12
	//			
	// hqlQuery.setString("location_code", lStrLocCode);
	// hqlQuery.setString("lPnsnerCode", lStrPnsnrCode);
	//
	// resultList = hqlQuery.list();
	//			
	// if (!resultList.isEmpty())
	// {
	// arrlstSuppDtls = new ArrayList();
	// SupplementaryRequestVO lObjSupplementaryRequestVO = null;
	// itr = resultList.iterator();
	// while (itr.hasNext())
	// {
	// tuple = (Object[]) itr.next();
	// lObjSupplementaryRequestVO = new SupplementaryRequestVO();
	//					
	// lObjSupplementaryRequestVO.setPpoNo(tuple[0].toString());
	// lObjSupplementaryRequestVO.setPartyName(tuple[1].toString());
	// lObjSupplementaryRequestVO.setBillType(tuple[8].toString());
	// lObjSupplementaryRequestVO.setTotalDifferenceAmount(new
	// BigDecimal(tuple[3].toString()));
	// lObjSupplementaryRequestVO.setGrossAmount(new
	// BigDecimal(tuple[4].toString()));
	// lObjSupplementaryRequestVO.setDeductionA(new
	// BigDecimal(tuple[5].toString()));
	// lObjSupplementaryRequestVO.setDeductionB(new
	// BigDecimal(tuple[12].toString()));
	// lObjSupplementaryRequestVO.setNetAmount(new
	// BigDecimal(tuple[6].toString()));
	//					
	// if("0".equals(tuple[7].toString()))
	// {
	// lObjSupplementaryRequestVO.setStatus("Pending");
	// }
	// else if("1".equals(tuple[7].toString()))
	// {
	// lObjSupplementaryRequestVO.setStatus("Approved");
	// }
	// else
	// {
	// lObjSupplementaryRequestVO.setStatus("Rejected");
	// }
	//					
	// lObjSupplementaryRequestVO.setPensionerCode(tuple[9].toString());
	// lObjSupplementaryRequestVO.setSuppReqId(new Long(tuple[10].toString()));
	// lObjSupplementaryRequestVO.setHeadCode(new
	// BigDecimal(tuple[11].toString()));
	//					
	// arrlstSuppDtls.add(lObjSupplementaryRequestVO);
	// }
	// }
	// }
	// catch (Exception e)
	// {
	// gLogger.error(" Error is : " + e, e);
	// throw (e);
	// }
	//		
	// return arrlstSuppDtls;
	// }
	//	
	// public void updateSupplyBillNo(Long lLngbillNo,String
	// lStrSupplyBillID,Long gLngPostId,Long gLngUserId,Date gDate) throws
	// Exception
	// {
	// try
	// {
	// Session hibSession = getSession();
	// StringBuilder lSBQuery = new StringBuilder();
	//			
	// lSBQuery.append(" UPDATE trn_pension_supply_bill_dtls SET bill_no = "+lLngbillNo+",updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date "+
	// " WHERE pension_supply_bill_id = "+lStrSupplyBillID+" ");
	//	        
	// Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());
	// lQuery.setLong("updated_user_id", gLngUserId);
	// lQuery.setLong("updated_post_id", gLngPostId);
	// lQuery.setDate("updated_date", gDate);
	// lQuery.executeUpdate();
	// }
	// catch (Exception e) {
	// logger.error("Error is: "+e,e);
	// throw(e);
	// }
	// }
	//	
	// public int checkForExistedBills(String lStrPnsnrCode) throws Exception
	// {
	// int lCount = 0;
	//		
	// try
	// {
	// Session hibSession = getSession();
	// StringBuilder lSBQuery = new StringBuilder();
	//			
	// lSBQuery.append(" SELECT COUNT(*) spCount FROM trn_pension_supply_bill_dtls sp WHERE sp.pensioner_code = :PnsnrCode AND nvl(sp.bill_no,0) = 0");
	//	        
	// SQLQuery lQuery = hibSession.createSQLQuery(lSBQuery.toString());
	//
	// lQuery.addScalar("spCount", Hibernate.INTEGER);
	//	        
	// lQuery.setString("PnsnrCode", lStrPnsnrCode);
	//
	// List lLstResLst = lQuery.list();
	//	        
	// if(lLstResLst != null && !lLstResLst.isEmpty() && lLstResLst.get(0) !=
	// null)
	// {
	// lCount = Integer.parseInt(lLstResLst.get(0).toString());
	// }
	//			
	// }
	// catch (Exception e)
	// {
	// gLogger.error(" Error is : " + e, e);
	// throw (e);
	// }
	//
	// return lCount;
	// }
	//	
	// //Soumya ... New Code
	// public List<SupplementaryRequestVO> getSavedSupplementaryRequests(String
	// lStrLocCode,String lStrPostId) throws Exception
	// {
	// StringBuffer lSBQuery = new StringBuffer();
	// List resultList;
	// Session ghibSession = getSession();
	// Object[] tuple;
	// Iterator itr;
	// ArrayList arrlstSuppDtls = null;
	//
	// try
	// {
	// lSBQuery.append(" SELECT ppo_no PPONO," +
	// " party_name NAME," +
	// " mb.bank_name BANK," +
	// " rbb.branch_name BRANCH," +
	// " account_no ACCNO," +
	// " difference_amount TOTDIFF," +
	// " gross_amount GROSS," +
	// " deduction_a DEDA," +
	// " net_amount NET," +
	// " STATUS STATUSFLAG, " +
	// " bill_type BILLTYPE, "+
	// " pensioner_code PENCODE, "+
	// " rbb.bank_code BNCD, "+
	// " rbb.branch_code BRCD, "+
	// " pension_Supply_Bill_Id PK, "+
	// " head_code HEAD, " +
	// " deduction_b DEDB "+
	// " FROM trn_pension_supply_bill_dtls s," +
	// "	   rlt_bank_branch rbb," +
	// "	   mst_bank mb "+
	// " WHERE s.location_code = rbb.location_code "+
	// "   AND s.branch_code = rbb.branch_code "+
	// "   AND rbb.bank_code = mb.bank_code "+
	// "   AND s.location_code = :location_code "+
	// "   AND s.created_post_id = :created_post_id " +
	// "	AND s.bill_no IS NULL ");
	//			
	// SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// hqlQuery.addScalar("PPONO", Hibernate.STRING);
	// hqlQuery.addScalar("NAME", Hibernate.STRING);
	// hqlQuery.addScalar("BANK", Hibernate.STRING);
	// hqlQuery.addScalar("BRANCH", Hibernate.STRING);
	// hqlQuery.addScalar("ACCNO", Hibernate.STRING);
	// hqlQuery.addScalar("TOTDIFF", Hibernate.BIG_DECIMAL);
	// hqlQuery.addScalar("GROSS", Hibernate.BIG_DECIMAL);
	// hqlQuery.addScalar("DEDA", Hibernate.BIG_DECIMAL);
	// hqlQuery.addScalar("NET", Hibernate.BIG_DECIMAL);
	// hqlQuery.addScalar("STATUSFLAG", Hibernate.LONG);
	// hqlQuery.addScalar("BILLTYPE", Hibernate.STRING);
	// hqlQuery.addScalar("PENCODE", Hibernate.STRING);
	// hqlQuery.addScalar("BNCD", Hibernate.STRING);
	// hqlQuery.addScalar("BRCD", Hibernate.STRING);
	// hqlQuery.addScalar("PK", Hibernate.LONG);
	// hqlQuery.addScalar("HEAD", Hibernate.BIG_DECIMAL);
	// hqlQuery.addScalar("DEDB", Hibernate.BIG_DECIMAL);
	//			
	// hqlQuery.setString("location_code", lStrLocCode);
	// hqlQuery.setLong("created_post_id", new Long(lStrPostId));
	//
	// resultList = hqlQuery.list();
	//			
	// arrlstSuppDtls = new ArrayList();
	// if (!resultList.isEmpty())
	// {
	// SupplementaryRequestVO lObjSupplementaryRequestVO = null;
	// itr = resultList.iterator();
	// while (itr.hasNext())
	// {
	// tuple = (Object[]) itr.next();
	// lObjSupplementaryRequestVO = new SupplementaryRequestVO();
	//					
	// lObjSupplementaryRequestVO.setPpoNo(tuple[0].toString());
	// lObjSupplementaryRequestVO.setPartyName(tuple[1].toString());
	// lObjSupplementaryRequestVO.setBankBranch(tuple[2].toString()+" "+tuple[3].toString());
	// lObjSupplementaryRequestVO.setAccountNo(tuple[4].toString());
	// lObjSupplementaryRequestVO.setBillType(tuple[10].toString());
	// lObjSupplementaryRequestVO.setTotalDifferenceAmount(new
	// BigDecimal(tuple[5].toString()));
	// lObjSupplementaryRequestVO.setGrossAmount(new
	// BigDecimal(tuple[6].toString()));
	// lObjSupplementaryRequestVO.setDeductionA(new
	// BigDecimal(tuple[7].toString()));
	// lObjSupplementaryRequestVO.setDeductionB(new
	// BigDecimal(tuple[16].toString()));
	// lObjSupplementaryRequestVO.setNetAmount(new
	// BigDecimal(tuple[8].toString()));
	//					
	// if("0".equals(tuple[9].toString()))
	// {
	// lObjSupplementaryRequestVO.setStatus("Pending");
	// }
	// else if("1".equals(tuple[9].toString()))
	// {
	// lObjSupplementaryRequestVO.setStatus("Approved");
	// }
	// else if("2".equals(tuple[9].toString()))
	// {
	// lObjSupplementaryRequestVO.setStatus("Rejected");
	// }
	// else
	// {
	// lObjSupplementaryRequestVO.setStatus("Archived");
	// }
	//					
	// lObjSupplementaryRequestVO.setPensionerCode(tuple[11].toString());
	// lObjSupplementaryRequestVO.setBankCode(tuple[12].toString());
	// lObjSupplementaryRequestVO.setBranchCode(tuple[13].toString());
	// lObjSupplementaryRequestVO.setSuppReqId(new Long(tuple[14].toString()));
	// lObjSupplementaryRequestVO.setHeadCode(new
	// BigDecimal(tuple[15].toString()));
	//					
	// arrlstSuppDtls.add(lObjSupplementaryRequestVO);
	// }
	// }
	// }
	// catch (Exception e)
	// {
	// gLogger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return arrlstSuppDtls;
	// }
	// public void updateSuppReqStatusApproved(String lStrPensionerCode,String
	// lStrLocCode,Long gLngPostId,Long gLngUserId,Date gDate) throws Exception
	// {
	// try
	// {
	// Query lQuery =
	// getSession().createQuery("UPDATE TrnPensionSupplyBillDtls SET status = 1,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date WHERE pensionerCode = :pensionerCode AND locationCode = :locationCode AND status = 0 ");
	//			
	// lQuery.setString("pensionerCode", lStrPensionerCode);
	// lQuery.setString("locationCode", lStrLocCode);
	// lQuery.setLong("updated_user_id", gLngUserId);
	// lQuery.setLong("updated_post_id", gLngPostId);
	// lQuery.setDate("updated_date", gDate);
	//
	// lQuery.executeUpdate();
	// }
	// catch (Exception e)
	// {
	// logger.error("Error is " + e, e);
	// throw (e);
	// }
	// }
	// public List getGroupIdForSavedSuppReq(String lStrPnsnrCode,String
	// lStrLocCode) throws Exception
	// {
	// List lLstResLst = null;
	// try
	// {
	// Session hibSession = getSession();
	// StringBuilder lSBQuery = new StringBuilder();
	//			
	// lSBQuery.append(" SELECT bill_type BILLTYPE, " +
	// "	     group_id  GRID "+
	// " FROM trn_pension_supply_bill_dtls "+
	// " WHERE pensioner_code = :pensioner_code "+
	// " AND location_code = :location_code "+
	// " AND bill_type IN ('DCRG','CVP') AND group_id IS NOT NULL "+
	// " GROUP BY bill_type,group_id ");
	//	        		
	// SQLQuery lQuery = hibSession.createSQLQuery(lSBQuery.toString());
	//
	// lQuery.addScalar("BILLTYPE", Hibernate.STRING);
	// lQuery.addScalar("GRID", Hibernate.STRING);
	//	        
	// lQuery.setString("pensioner_code", lStrPnsnrCode);
	// lQuery.setString("location_code", lStrLocCode);
	//
	// lLstResLst = lQuery.list();
	// }
	// catch (Exception e)
	// {
	// gLogger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return lLstResLst;
	// }
	// public void insertGroupIdInRecoveryDtls(String lStrPensionerCode,String
	// lStrBillType,Long lLngGroupId,Long gLngPostId,Long gLngUserId,Date gDate)
	// throws Exception
	// {
	// try
	// {
	// Query lQuery =
	// getSession().createQuery(" UPDATE TrnPensionRecoveryDtls SET billNo = :groupId ,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date"+
	// " WHERE pensionerCode = :pensionerCode "+
	// " AND recoveryFromFlag = :recoveryFromFlag " +
	// " AND billNo is null ");
	//	
	// lQuery.setLong("groupId", lLngGroupId);
	// lQuery.setString("pensionerCode", lStrPensionerCode);
	// lQuery.setString("recoveryFromFlag", lStrBillType);
	// lQuery.setLong("updated_user_id", gLngUserId);
	// lQuery.setLong("updated_post_id", gLngPostId);
	// lQuery.setDate("updated_date", gDate);
	//
	// lQuery.executeUpdate();
	// }
	// catch (Exception e)
	// {
	// logger.error("Error is " + e, e);
	// throw (e);
	// }
	// }
	// public List getSuppBillDtlsData(String lStrPKList,String
	// lStrLocationCode) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	//
	// lSBQuery.append(" SELECT s.ppo_no PPO," +
	// " 		 s.pensioner_code PENCODE," +
	// " 		 s.net_amount NET," +
	// "        s.account_no ACCNO," +
	// "        s.party_name NAME, "+
	// "		 s.head_code HEAD, "+
	// "		 s.deduction_a DEDA, "+
	// "        s.deduction_b DEDB, "+
	// "        s.bill_type BILL "+
	// " FROM Trn_Pension_Supply_Bill_Dtls s, "+
	// "	   Trn_pension_rqst_hdr r "+
	// " WHERE s.pensioner_code = r.pensioner_code "+
	// "       AND r.location_code = s.location_code "+
	// "       AND r.location_code = :location_code "+
	// "       AND r.case_status = 'APPROVED' "+
	// "       AND s.pension_supply_bill_id IN (" + lStrPKList + ")");
	//
	// SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// lQuery.addScalar("PPO", Hibernate.STRING);
	// lQuery.addScalar("PENCODE", Hibernate.STRING);
	// lQuery.addScalar("NET", Hibernate.BIG_DECIMAL);
	// lQuery.addScalar("ACCNO", Hibernate.STRING);
	// lQuery.addScalar("NAME", Hibernate.STRING);
	// lQuery.addScalar("HEAD", Hibernate.BIG_DECIMAL);
	// lQuery.addScalar("DEDA", Hibernate.BIG_DECIMAL);
	// lQuery.addScalar("DEDB", Hibernate.BIG_DECIMAL);
	// lQuery.addScalar("BILL", Hibernate.STRING);
	//			
	// lQuery.setString("location_code", lStrLocationCode);
	// resList = lQuery.list();
	//
	// } catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//	
	// public List getSuppBillEDPReceiptDtls(String lStrPKList,String
	// lStrLocationCode) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	//
	// lSBQuery.append(" SELECT pr.edp_Code EDP," +
	// "        pr.deduction_Type DEDTYPE," +
	// "		 pr.mjrhd_Code MJR," +
	// "		 pr.submjrhd_Code SUBMJR," +
	// "		 pr.minhd_Code MIN, " +
	// "		 pr.subhd_Code SUB," +
	// "		 SUM(pr.amount) AMT "+
	// " FROM Trn_Pension_Recovery_Dtls pr, " +
	// "      trn_pension_supply_bill_dtls sp "+
	// " WHERE pr.bill_no = sp.group_id "+
	// "       AND sp.pension_supply_bill_id IN ("+ lStrPKList
	// +") AND sp.location_Code = :location_code "+
	// " GROUP BY pr.edp_Code,pr.deduction_Type,pr.mjrhd_Code,pr.submjrhd_Code, pr.minhd_Code, pr.subhd_Code ");
	//
	// SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// lQuery.addScalar("EDP", Hibernate.STRING);
	// lQuery.addScalar("DEDTYPE", Hibernate.STRING);
	// lQuery.addScalar("MJR", Hibernate.STRING);
	// lQuery.addScalar("SUBMJR", Hibernate.STRING);
	// lQuery.addScalar("MIN", Hibernate.STRING);
	// lQuery.addScalar("SUB", Hibernate.STRING);
	// lQuery.addScalar("AMT", Hibernate.BIG_DECIMAL);
	//			
	// lQuery.setString("location_code", lStrLocationCode);
	// resList = lQuery.list();
	//
	// } catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//	
	// public List getSuppPnsnBillPReceiptDtls(String lStrPKList,String
	// lStrLocationCode) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	//
	// lSBQuery.append(" SELECT PR.RECOVERY_FROM_FLAG TYPE, "+
	// "		 sp.head_code HEAD, "+
	// "		 PR.EDP_CODE EDP, "+
	// " 		 PR.DEDUCTION_TYPE DEDTYPE, "+
	// "		 PR.MJRHD_CODE MJR, "+
	// "		 PR.SUBMJRHD_CODE SUBMJR, "+
	// "		 PR.MINHD_CODE MIN, "+
	// " 		 PR.SUBHD_CODE SUB, "+
	// " 		 SUM(PR.AMOUNT) AMT "+
	// "	FROM TRN_PENSION_RECOVERY_DTLS PR," +
	// " 		 TRN_PENSION_SUPPLY_BILL_DTLS SP "+
	// "	WHERE pr.bill_no = sp.group_id " +
	// "         AND PR.PENSIONER_CODE = SP.PENSIONER_CODE "+
	// "		  AND sp.pension_supply_bill_id IN ("+ lStrPKList +") " +
	// "		  AND sp.location_code = :location_code "+
	// "	GROUP BY PR.RECOVERY_FROM_FLAG," +
	// "			 sp.head_code, "+
	// "			 PR.EDP_CODE, "+
	// "			 PR.DEDUCTION_TYPE, "+
	// "			 PR.MJRHD_CODE, "+
	// "			 PR.SUBMJRHD_CODE, "+
	// "			 PR.MINHD_CODE, "+
	// "			 PR.SUBHD_CODE ");
	//
	// SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// lQuery.addScalar("TYPE", Hibernate.STRING);
	// lQuery.addScalar("HEAD", Hibernate.BIG_DECIMAL);
	// lQuery.addScalar("EDP", Hibernate.STRING);
	// lQuery.addScalar("DEDTYPE", Hibernate.STRING);
	// lQuery.addScalar("MJR", Hibernate.STRING);
	// lQuery.addScalar("SUBMJR", Hibernate.STRING);
	// lQuery.addScalar("MIN", Hibernate.STRING);
	// lQuery.addScalar("SUB", Hibernate.STRING);
	// lQuery.addScalar("AMT", Hibernate.BIG_DECIMAL);
	//			
	// lQuery.setString("location_code", lStrLocationCode);
	// resList = lQuery.list();
	//
	// } catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//	
	// public List getHeadCodeWiseNetListForView(Long lLngBillNo,String
	// lStrLocCode) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	//
	// lSBQuery.append(" SELECT s.head_code HEAD, "+
	// "		 SUM(s.net_amount) TOTALNET "+
	// " FROM trn_pension_supply_bill_dtls s "+
	// " WHERE s.bill_no = :bill_no "+
	// " AND s.location_code = :location_code "+
	// " GROUP BY s.head_code");
	//
	// SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// Query.addScalar("HEAD", Hibernate.BIG_DECIMAL);
	// Query.addScalar("TOTALNET", Hibernate.BIG_DECIMAL);
	//
	// Query.setLong("bill_no", lLngBillNo);
	// Query.setString("location_code", lStrLocCode);
	//			
	// resList = Query.list();
	// }
	// catch (Exception e)
	// {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//	
	// public String getBrnchScheme(Long lBillNo,String lStrLocCode) throws
	// Exception {
	//
	// List resList = null;
	// String lStrBranchCode = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	//
	// lSBQuery.append(" SELECT DISTINCT s.branch_code BRANCH "+
	// " FROM trn_pension_supply_bill_dtls s "+
	// " WHERE s.bill_no = :bill_no "+
	// " AND s.location_code = :location_code ");
	//
	// SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// Query.addScalar("BRANCH", Hibernate.STRING);
	//
	// Query.setLong("bill_no", lBillNo);
	// Query.setString("location_code", lStrLocCode);
	//			
	// resList = Query.list();
	//			
	// if(resList != null && !resList.isEmpty() && resList.get(0) != null)
	// {
	// lStrBranchCode = resList.get(0).toString();
	// }
	//
	// } catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return lStrBranchCode;
	// }
	//	
	//	
	// public List getBnkBrnchHdcodeList(Long lBillNo) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	// lSBQuery.append(" SELECT rbb.bankCode,s.branchCode,s.headCode ");
	// lSBQuery.append(" FROM TrnPensionSupplyBillDtls s,RltBankBranch rbb ");
	// lSBQuery.append(" WHERE s.billNo =:billNo AND s.branchCode = rbb.branchCode "
	// +
	// " and rbb.locationCode = s.locationCode ");
	//			
	// Query lQuery = ghibSession.createQuery(lSBQuery.toString());
	// lQuery.setLong("billNo", lBillNo);
	// resList = lQuery.list();
	// } catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//	
	// public List getBillTypeListForBillNo(Long lBillNo,String
	// lStrLocationCode) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	// lSBQuery.append(" SELECT DISTINCT bill_type BILL "+
	// " FROM trn_pension_supply_bill_dtls "+
	// " WHERE bill_no = :bill_no "+
	// " AND location_code = :location_code ");
	//			
	// SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// Query.addScalar("BILL", Hibernate.STRING);
	// Query.setLong("bill_no", lBillNo);
	// Query.setString("location_code", lStrLocationCode);
	//			
	// resList = Query.list();
	// }
	// catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//
	// public List getHeadCodeListForBillType(Long lBillNo,String
	// lStrBillType,String lStrLocationCode) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	// lSBQuery.append(" SELECT DISTINCT head_code HD "+
	// " FROM  trn_pension_supply_bill_dtls "+
	// " WHERE bill_no = :bill_no "+
	// " AND bill_type = :bill_type "+
	// " AND location_code = :location_code " +
	// "order by head_code ");
	//			
	// SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// Query.addScalar("HD", Hibernate.BIG_DECIMAL);
	// Query.setLong("bill_no", lBillNo);
	// Query.setString("bill_type", lStrBillType);
	// Query.setString("location_code", lStrLocationCode);
	//			
	// resList = Query.list();
	// }
	// catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//	
	// public List getSuppBillDataforViewNew(Long lLngBillNo,String
	// lStrBillType,BigDecimal BDHeadCode,String lStrLocCode) throws Exception {
	//
	// List resList = null;
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	//
	// /*lSBQuery.append(" SELECT S.PPO_NO PPO, "+
	// "		 MH.FIRST_NAME FNAME, "+
	// "		 MH.MIDDLE_NAME MNAME, "+
	// "		 MH.LAST_NAME LNAME, "+
	// "		 S.GROSS_AMOUNT GROSS, "+
	// " 		 S.DEDUCTION_A DEDA, "+
	// " 		 S.DEDUCTION_B DEDB, "+
	// " 		 S.NET_AMOUNT NET, "+
	// " 		 S.BILL_TYPE BILLTYPE, "+
	// " 		 S.ACCOUNT_NO ACCNO, "+
	// " 		 OEM.EMP_FNAME EFNAME, "+
	// "		 OEM.EMP_MNAME ENAME, "+
	// "		 OEM.EMP_LNAME ELNAME, "+
	// "        S.HEAD_CODE HEAD "+
	// "	FROM TRN_PENSION_SUPPLY_BILL_DTLS S, "+
	// "		 MST_PENSIONER_HDR            MH, "+
	// "		 ORG_EMP_MST                  OEM, "+
	// "		 ORG_USERPOST_RLT             T "+
	// "	WHERE S.BILL_NO = :BILL_NO AND S.HEAD_CODE = :HEAD_CODE " +
	// "		  AND S.PENSIONER_CODE = MH.PENSIONER_CODE AND "+
	// " 		  OEM.LANG_ID = :LANG_ID AND OEM.USER_ID = T.USER_ID AND "+
	// "		  T.POST_ID = S.CREATED_POST_ID AND T.ACTIVATE_FLAG = :ACTIVATE_FLAG AND "+
	// "		  OEM.ACTIVATE_FLAG = :ACTIVATE_FLAG "+
	// "		  AND s.location_code = :location_code "+
	// "		  AND mh.case_status = :case_status " +
	// "		  AND s.bill_type = :bill_type ");*/
	//			
	//
	// lSBQuery.append(" SELECT S.PPO_NO PPO," +
	// "        S.PARTY_NAME NAME," +
	// "		 S.GROSS_AMOUNT GROSS," +
	// " 		 S.DEDUCTION_A DEDA," +
	// " 		 S.DEDUCTION_B DEDB," +
	// " 		 S.NET_AMOUNT NET," +
	// " 		 S.BILL_TYPE BILLTYPE," +
	// " 		 S.ACCOUNT_NO ACCNO," +
	// " 		 OEM.EMP_FNAME EFNAME," +
	// "		 OEM.EMP_MNAME ENAME," +
	// "		 OEM.EMP_LNAME ELNAME," +
	// "     	 S.HEAD_CODE HEAD" +
	// " FROM TRN_PENSION_SUPPLY_BILL_DTLS S," +
	// "	   ORG_EMP_MST                  OEM," +
	// "	   ORG_USERPOST_RLT             T," +
	// "      TRN_PENSION_RQST_HDR         RQ " +
	// " WHERE S.BILL_NO = :BILL_NO AND S.HEAD_CODE = :HEAD_CODE " +
	// "		AND OEM.LANG_ID = :LANG_ID AND OEM.USER_ID = T.USER_ID AND " +
	// "		T.POST_ID = S.CREATED_POST_ID AND T.ACTIVATE_FLAG = :ACTIVATE_FLAG AND "
	// +
	// "       OEM.ACTIVATE_FLAG = :ACTIVATE_FLAG " +
	// "	    AND s.location_code = :location_code " +
	// "       AND s.bill_type = :bill_type " +
	// "       AND S.PENSIONER_CODE = RQ.PENSIONER_CODE " +
	// "       AND S.LOCATION_CODE = RQ.LOCATION_CODE " +
	// "       AND RQ.CASE_STATUS = 'APPROVED'" +
	// "       AND RQ.STATUS = 'Continue' " +
	// " ORDER BY RQ.LEDGER_NO," +
	// "          RQ.PAGE_NO," +
	// "          RQ.PPO_NO ");
	//
	//
	// SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// Query.addScalar("PPO", Hibernate.STRING);
	// Query.addScalar("NAME", Hibernate.STRING);
	// Query.addScalar("GROSS", Hibernate.BIG_DECIMAL);
	// Query.addScalar("DEDA", Hibernate.BIG_DECIMAL);
	// Query.addScalar("DEDB", Hibernate.BIG_DECIMAL);
	// Query.addScalar("NET", Hibernate.BIG_DECIMAL);
	// Query.addScalar("BILLTYPE", Hibernate.STRING);
	// Query.addScalar("ACCNO", Hibernate.STRING);
	// Query.addScalar("EFNAME", Hibernate.STRING);
	// Query.addScalar("ENAME", Hibernate.STRING);
	// Query.addScalar("ELNAME", Hibernate.STRING);
	// Query.addScalar("HEAD", Hibernate.STRING);
	//			
	// Query.setLong("BILL_NO", lLngBillNo);
	// Query.setBigDecimal("HEAD_CODE", BDHeadCode);
	// Query.setLong("LANG_ID", 1L);
	// Query.setLong("ACTIVATE_FLAG", 1L);
	// Query.setString("location_code", lStrLocCode);
	// Query.setString("bill_type", lStrBillType);
	//			
	// resList = Query.list();
	// } catch (Exception e) {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return resList;
	// }
	//	
	// public BigDecimal getHeadCodeWiseNetListForBillTypeView(Long
	// lLngBillNo,String lStrBillType,BigDecimal lBDHd,String lStrLocCode)
	// throws Exception {
	//
	// List resList = null;
	// BigDecimal lBDNet = new BigDecimal(0);
	//
	// try {
	// StringBuffer lSBQuery = new StringBuffer();
	// Session ghibSession = getSession();
	//
	// lSBQuery.append(" SELECT SUM(s.net_amount) TOTALNET "+
	// " FROM trn_pension_supply_bill_dtls s "+
	// " WHERE s.bill_no = :bill_no "+
	// " AND s.location_code = :location_code " +
	// " AND s.bill_type = :bill_type " +
	// " AND s.head_code = :head_code "+
	// " GROUP BY s.head_code");
	//
	// SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// Query.addScalar("TOTALNET", Hibernate.BIG_DECIMAL);
	//
	// Query.setLong("bill_no", lLngBillNo);
	// Query.setString("location_code", lStrLocCode);
	// Query.setString("bill_type", lStrBillType);
	// Query.setBigDecimal("head_code", lBDHd);
	//			
	// resList = Query.list();
	//			
	// if(resList != null && !resList.isEmpty() && resList.get(0) != null)
	// {
	// lBDNet = new BigDecimal(resList.get(0).toString());
	// }
	// }
	// catch (Exception e)
	// {
	// logger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return lBDNet;
	// }
	// public void updateSuppBillDtlsForRejection(String lStrBillNo,Long
	// gLngPostId,Long gLngUserId,Date gDate) throws Exception
	// {
	// try
	// {
	// Query lQuery =
	// getSession().createQuery(" UPDATE TrnPensionSupplyBillDtls SET billNo = null,status = 1,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date  "+
	// " WHERE billNo = :billNo ");
	//	
	// lQuery.setLong("billNo", new Long(lStrBillNo));
	// lQuery.setLong("updated_user_id", gLngUserId);
	// lQuery.setLong("updated_post_id", gLngPostId);
	// lQuery.setDate("updated_date", gDate);
	//
	// lQuery.executeUpdate();
	// }
	// catch (Exception e)
	// {
	// logger.error("Error is " + e, e);
	// throw (e);
	// }
	// }
	//	
	// public void updateSuppBillDtlsForRejectionCase(String
	// lStrPensionerCode,String lStrLocCode,Long gLngPostId,Long gLngUserId,Date
	// gDate) throws Exception
	// {
	// List resList = null;
	// StringBuffer lSBQuery = new StringBuffer();
	// Query lQuery = null;
	//		
	// try
	// {
	// Session ghibSession = getSession();
	//
	// lSBQuery.append(" SELECT S.pension_supply_bill_id ID"+
	// "	FROM TRN_PENSION_SUPPLY_BILL_DTLS S "+
	// "	WHERE S.pensioner_code = :pensioner_code AND S.location_code = :location_code ");
	//			
	// SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
	//			
	// Query.addScalar("ID", Hibernate.LONG);
	// Query.setString("pensioner_code", lStrPensionerCode);
	// Query.setString("location_code", lStrLocCode);
	//			
	// resList = Query.list();
	//			
	// if(resList != null && !resList.isEmpty() && resList.get(0) != null)
	// {
	// lQuery =
	// getSession().createQuery(" UPDATE TrnPensionSupplyBillDtls SET status = 2,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date  "+
	// " WHERE pensioner_code = :pensioner_code AND location_code = :location_code AND"
	// +
	// " status != 1 ");
	// lQuery.setString("pensioner_code", lStrPensionerCode);
	// lQuery.setString("location_code", lStrLocCode);
	// lQuery.setLong("updated_user_id", gLngUserId);
	// lQuery.setLong("updated_post_id", gLngPostId);
	// lQuery.setDate("updated_date", gDate);
	//				
	// lQuery.executeUpdate();
	// }
	// }
	// catch (Exception e)
	// {
	// logger.error("Error is " + e, e);
	// throw (e);
	// }
	// }
	//	
	// public String getBillGenStatus(String lStrSuppIdList) throws Exception {
	//
	// String lStrFinyrdesc = null;
	// List resultList;
	// try {
	// Session ghibSession = getSession();
	// StringBuffer strQuery = new StringBuffer();
	//
	// strQuery.append(" SELECT pensionSupplyBillId ");
	// strQuery.append(" FROM TrnPensionSupplyBillDtls ");
	// strQuery.append(" WHERE billNo IS NOT NULL AND pensionSupplyBillId IN ("+lStrSuppIdList+") ");
	//
	// Query hqlQuery = ghibSession.createQuery(strQuery.toString());
	//
	// resultList = hqlQuery.list();
	//
	// if (resultList != null && !resultList.isEmpty()) {
	// lStrFinyrdesc = "true";
	// }
	// else
	// lStrFinyrdesc = "false";
	//			
	// } catch (Exception e) {
	// logger.error("Error is :" + e, e);
	// throw e;
	// }
	// return lStrFinyrdesc;
	// }
	//
	// public List getPnsnrBankDtls(String lStrPensionerCode, String
	// lStrCaseStatus, String lStrLocCode) throws Exception {
	//
	// List lPnsnrBankDtls = null;
	//
	// try {
	// Session hiSession = getSession();
	// StringBuffer lSBQuery = new StringBuffer();
	//
	// lSBQuery.append(" SELECT mb.bank_name,rb.branch_name,mp.bank_code,mp.branch_code,mp.acount_no "
	// +
	// " FROM mst_pensioner_dtls mp, mst_bank mb, rlt_bank_branch rb" +
	// " WHERE mp.branch_code = rb.branch_code AND rb.bank_code = mb.bank_code"
	// +
	// " AND mp.location_code = rb.location_code AND mp.case_status = :caseStatus "
	// +
	// " AND mp.pensioner_code = :lPnsrCode AND mp.location_code = :lLocCode AND mp.active_flag = :activeFlag ");
	//			
	// Query lQuery = hiSession.createSQLQuery(lSBQuery.toString());
	//
	// lQuery.setString("lPnsrCode", lStrPensionerCode);
	// lQuery.setString("caseStatus", lStrCaseStatus);
	// lQuery.setString("activeFlag", "Y");
	// lQuery.setString("lLocCode", lStrLocCode);
	//
	// lPnsnrBankDtls = lQuery.list();
	//
	// } catch (Exception e) {
	// gLogger.error(" Error is : " + e, e);
	// throw (e);
	// }
	// return lPnsnrBankDtls;
	// }
	//	
	// public List getListOfNominee(String pensionerCode, String lStrLocCode)
	// throws Exception {
	//
	// Session hiSession = getSession();
	// StringBuffer strQuery = new StringBuffer();
	// List resultList = null;
	//		
	// try {
	// strQuery.append(" SELECT mp.name, mp.percent, mb.bank_name,rb.branch_name,mp.bank_code,mp.branch_code,mp.account_no"
	// +
	// " FROM Mst_Pensioner_Nominee_Dtls mp, mst_bank mb, rlt_bank_branch rb" +
	// " WHERE mp.branch_code = rb.branch_code AND rb.bank_code = mb.bank_code"
	// +
	// " AND mp.location_code = rb.location_code AND mp.pensioner_code = :pensionerCode "
	// +
	// " AND mp.location_code = :lLocCode ");
	//
	// Query hqlQuery = hiSession.createSQLQuery(strQuery.toString());
	//			
	// hqlQuery.setString("pensionerCode", pensionerCode);
	// hqlQuery.setString("lLocCode", lStrLocCode);
	//			
	// resultList = hqlQuery.list();
	//			
	// } catch (Exception e) {
	// gLogger.error("Error is" + e, e);
	// throw e;
	// }
	// return resultList;
	// }
	//	
	//	
	// /**
	// * Method to get Receipt Object head details By Bill Type.
	// * @param lStrPnsnerCode
	// * @param lStrBillType
	// * @return List
	// */
	// public List getRcptEdpDtlByBillType(String lStrPnsnerCode, String
	// lStrBillType, long lStrGrpId) throws Exception
	// {
	//
	// List dataList = new ArrayList();
	// BigDecimal lBDZero = new BigDecimal("0.00");
	// try
	// {
	// Session ghibSession = getSession();
	// StringBuffer hqlQuery = new StringBuffer(300);
	//
	// hqlQuery.append(" select pr.edpCode, pr.deductionType, pr.mjrhdCode,pr.submjrhdCode, pr.minhdCode, pr.subhdCode, "
	// +
	// " pr.amount, pr.dateOfReceipt from TrnPensionRecoveryDtls pr where pr.pensionerCode = :lPnsnerCode And "
	// +
	// " pr.recoveryFromFlag = :lBillType and pr.billNo = :lGrpId order by pr.edpCode ");
	//			
	// Query lQuery = ghibSession.createQuery(hqlQuery.toString());
	// lQuery.setString("lPnsnerCode", lStrPnsnerCode);
	// lQuery.setString("lBillType", lStrBillType.toUpperCase());
	// lQuery.setLong("lGrpId", lStrGrpId);
	//			
	// List resList = lQuery.list();
	// if (resList != null)
	// {
	// Iterator it = resList.iterator();
	// Object[] tuple = null;
	// BillEdpVO billEdpVO = null;
	// while (it.hasNext())
	// {
	// tuple = (Object[]) it.next();
	// billEdpVO = new BillEdpVO();
	// if (tuple[7] == null)
	// {
	// billEdpVO.setEdpCode((String) tuple[0]);
	// billEdpVO.setEdpCategory((String) tuple[1]);
	// billEdpVO.setBudmjrHd((String) tuple[2]);
	// billEdpVO.setBudsubmjrHd((String) tuple[3]);
	// billEdpVO.setBudminHd((String) tuple[4]);
	// billEdpVO.setBudsubHd((String) tuple[5]);
	// if (tuple[6].toString() != null)
	// {
	// billEdpVO.setEdpAmt(new BigDecimal(tuple[6].toString()));
	// }
	// else
	// {
	// billEdpVO.setEdpAmt(lBDZero);
	// }
	// }
	// dataList.add(billEdpVO);
	// }
	// }
	// }
	// catch(Exception e)
	// {
	// logger.error("Error in getRcptEdpDtlByBillType. class BudgetDtlsDAOImpl Error is : "
	// + e, e);
	// throw (e);
	// }
	//
	// return dataList;
	// }
	//	
	// public void archiveApprovedReq(List lLstSuppIdList, Map inputMap) throws
	// Exception {
	//
	// try {
	// Session ghibSession = getSession();
	// StringBuffer strQuery = new StringBuffer();
	//
	// strQuery.append(" UPDATE TrnPensionSupplyBillDtls " +
	// " SET status = 3, updatedUserId = :User,updatedPostId = :Post, updatedDate = :currDate "
	// +
	// " WHERE billNo IS NULL AND pensionSupplyBillId IN (:pensionSupplyBillId) ");
	//
	// Query hqlQuery = ghibSession.createQuery(strQuery.toString());
	// hqlQuery.setBigDecimal("User", new
	// BigDecimal(SessionHelper.getUserId(inputMap)));
	// hqlQuery.setBigDecimal("Post", new
	// BigDecimal(SessionHelper.getPostId(inputMap)));
	// hqlQuery.setDate("currDate", DBUtility.getCurrentDateFromDB());
	// hqlQuery.setParameterList("pensionSupplyBillId", lLstSuppIdList);
	//			
	// hqlQuery.executeUpdate();
	//
	// } catch (Exception e) {
	// logger.error("Error is :" + e, e);
	// throw e;
	// }
	// }
	
	public Integer getPnsnrSixPayArrearRevisionCnt(String lStrPensionerCode,Long lLngPensionSupplyBillId) throws Exception {

		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		StringBuffer lSBQuery = new StringBuffer();
		Integer lIntCount = 0;

		try {
			lSBQuery.append("select count(arrearId) \n");
			lSBQuery.append(" from TrnPensionSixpayfpArrear where pensionerCode = :pensionerCode  \n");
			lSBQuery.append(" and pensionSupplyBillId = :pensionSupplyBillId and activeFlag = 'Y'");
			
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			hqlQuery.setLong("pensionSupplyBillId", lLngPensionSupplyBillId);
						
			lLstResult = hqlQuery.list();

			lIntCount = Integer.parseInt(lLstResult.get(0).toString());

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lIntCount;

	}
	
	public List<TrnPensionSixpayfpArrear> getPnsnrSixPayArrearDtlsFromPnsnrCode(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		List<TrnPensionSixpayfpArrear> lLstTrnPensionSixpayfpArrear = new ArrayList<TrnPensionSixpayfpArrear>();
		StringBuffer lSBQuery = new StringBuffer();
		Integer lIntCount = 0;

		try {
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear(installmentNo,installmentAmnt,diffAmnt) \n");
			lSBQuery.append(" FROM TrnPensionSixpayfpArrear where pensionerCode = :pensionerCode  \n");
			lSBQuery.append(" and activeFlag = 'Y'");
			
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			//hqlQuery.setLong("pensionSupplyBillId", lLngPensionSupplyBillId);
						
			lLstTrnPensionSixpayfpArrear = hqlQuery.list();

			//lIntCount = Integer.parseInt(lLstResult.get(0).toString());

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstTrnPensionSixpayfpArrear;

	}
	
	public Map<String, List<TrnPensionRecoveryDtls>> getRecoveryDtlsForSupplPensionBill(String lStrPensionerCode, String lStrForMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<TrnPensionRecoveryDtls> lLstResult = null;
		ghibSession = getSession();
		String lStrRcvryFrom = bundleConst.getString("RECOVERY.SUPPPNSN");
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsObj = null;
		Map<String, List<TrnPensionRecoveryDtls>> lMapRcvryDtl = new HashMap<String, List<TrnPensionRecoveryDtls>>();
		double lAmnt = 0;
		try {
			lSBQuery.append("Select prd \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TrnPensionRecoveryDtls prd  \n");
			lSBQuery.append("where \n");
			lSBQuery.append("prd.pensionerCode = :pensionerCode \n");
			lSBQuery.append("and prd.recoveryFromFlag = :lRcvryFrom \n");
			//lSBQuery.append("and ((prd.fromMonth <= :lForMonth and prd.toMonth >= :lForMonth) OR (prd.fromMonth = :lForMonth AND prd.toMonth IS NULL)) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("pensionerCode", lStrPensionerCode);
			//lQuery.setInteger("lForMonth", Integer.valueOf(lStrForMonth));
			lQuery.setString("lRcvryFrom", lStrRcvryFrom);
			lLstResult = lQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls : lLstResult) {
					lLstTrnPensionRecoveryDtlsObj = lMapRcvryDtl.get(lObjTrnPensionRecoveryDtls.getPensionerCode());
					lAmnt = lObjTrnPensionRecoveryDtls.getAmount().doubleValue();
					if (lLstTrnPensionRecoveryDtlsObj != null) {
						if (lAmnt > 0) {
							lLstTrnPensionRecoveryDtlsObj.add(lObjTrnPensionRecoveryDtls);
						}
					} else {
						lLstTrnPensionRecoveryDtlsObj = new ArrayList<TrnPensionRecoveryDtls>();
						if (lAmnt > 0) {
							lLstTrnPensionRecoveryDtlsObj.add(lObjTrnPensionRecoveryDtls);
						}
					}
					lMapRcvryDtl.put(lObjTrnPensionRecoveryDtls.getPensionerCode(), lLstTrnPensionRecoveryDtlsObj);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lMapRcvryDtl;
	}

	
	public List<TrnPensionRecoveryDtls> getRecoveryDtlsForSupplPnsnBillForDelete(String lStrPensionerCode, String lStrRecoveryFrom,Long lLngSupplBillId) throws Exception {
		
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = new ArrayList();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionRecoveryDtls A WHERE A.pensionerCode = :pensionerCode AND A.recoveryFromFlag = :recoveryFrom  AND A.billNo = :billNo ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("recoveryFrom", lStrRecoveryFrom);
			lQuery.setLong("billNo", lLngSupplBillId);
			lLstTrnPensionRecoveryDtls = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lLstTrnPensionRecoveryDtls;
	}

	
	public List getSupplRequestList(Map displayTag,
			Long lLngPostId, String lStrLocationCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstTrnPensionSupplyBillDtls = new ArrayList();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			//lSBQuery.append(" FROM TrnPensionSupplyRequestDtls WHERE audPostId = :postId and locationCode = :locationCode ");
			
//			lSBQuery.append("select  new com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls(requestNo,cast(sum(grossAmount) as decimal), \n");
//			lSBQuery.append(" cast(sum(differenceAmount) as decimal),cast(sum(netAmount) as decimal)) from \n"); 
//			lSBQuery.append(" TrnPensionSupplyBillDtls  \n");
//			lSBQuery.append(" where createdPostId = :postId and locationCode = :locationCode and requestNo is not null group by requestNo ");

			lSBQuery.append("select request_No,count(pension_Supply_Bill_Id),cast(sum(gross_Amount) as bigint), \n");
			lSBQuery.append(" cast(sum(difference_Amount) as bigint),cast(sum(net_Amount) as bigint) from \n"); 
			lSBQuery.append(" Trn_Pension_Supply_Bill_Dtls  \n");
			lSBQuery.append(" where created_Post_Id = :postId and location_Code = :locationCode and request_No is not null group by request_No ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);
			
			lQuery.setLong("postId", lLngPostId);
			lQuery.setString("locationCode", lStrLocationCode);
			
			lLstTrnPensionSupplyBillDtls = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lLstTrnPensionSupplyBillDtls;
	}


	public Integer getSupplRequestCount(Long lLngPostId,
			String lStrLocationCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		Integer lIntCount = 0;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select count(distinct request_No) from \n"); 
			lSBQuery.append(" Trn_Pension_Supply_Bill_Dtls  \n");
			lSBQuery.append(" where created_Post_Id = :postId and location_Code = :locationCode and request_No is not null ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());


			lQuery.setLong("postId", lLngPostId);
			lQuery.setString("locationCode", lStrLocationCode);
			lLstResult = lQuery.list();
			
			lIntCount = Integer.parseInt(lLstResult.get(0).toString());

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lIntCount;
	}

	public Integer getSupplRequestDtlsFromRequestNoCount(String lStrRequestNo,
			Long lLngPostId, String lStrLocationCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		Integer lIntCount = 0;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select count(pensionSupplyBillId) from \n"); 
			lSBQuery.append(" TrnPensionSupplyBillDtls  \n");
			lSBQuery.append(" where requestNo = :requestNo and createdPostId = :postId and locationCode = :locationCode ");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());


			lQuery.setLong("postId", lLngPostId);
			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setString("requestNo", lStrRequestNo);
			lLstResult = lQuery.list();
			
			lIntCount = Integer.parseInt(lLstResult.get(0).toString());

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lIntCount;
	}
	
	public List<TrnPensionSupplyBillDtls> getSupplRequestDtlsFromRequestNo(
			Map displayTag, String lStrRequestNo, Long lLngPostId,
			String lStrLocationCode) throws Exception {
		// TODO Auto-generated method stub
		List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = new ArrayList<TrnPensionSupplyBillDtls>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select new com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls(pensionSupplyBillId,ppoNo,partyName \n"); 
			lSBQuery.append(" ,grossAmount,differenceAmount,netAmount,status,billNo) from TrnPensionSupplyBillDtls  \n");
			lSBQuery.append(" where requestNo = :requestNo and createdPostId = :postId and locationCode = :locationCode ");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);
			
			lQuery.setString("requestNo", lStrRequestNo);
			lQuery.setLong("postId", lLngPostId);
			lQuery.setString("locationCode", lStrLocationCode);
			
			lLstTrnPensionSupplyBillDtls = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lLstTrnPensionSupplyBillDtls;
	}

	
	public List getSupplPnsnDtlSchemeCodePayModeWise(String lStrRequestNo,
			List<Short> lLstBillStatus, String lStrLocationCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select scheme_code,pay_mode,cast(sum(gross_amount) as bigint), \n");
			lSBQuery.append(" cast(sum(difference_amount) as bigint),cast(sum(net_amount) as bigint) \n");
			lSBQuery.append(" from Trn_Pension_Supply_Bill_Dtls where request_No = :requestNo and status in (:billStatusList) and location_code = :locationCode \n"); 
			lSBQuery.append(" group by scheme_code,pay_mode ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("requestNo", lStrRequestNo);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setString("locationCode", lStrLocationCode);
			
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lLstResult;
	}

	
	public List getSupplPnsnDtlBranchHeadWise(String lStrRequestNo,
			List<Short> lLstBillStatus, String lStrLocationCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select count(pension_supply_bill_id),scheme_code,pay_mode,bank_code,branch_code,head_code, \n");
			lSBQuery.append(" cast(sum(gross_amount) as bigint),cast(sum(difference_amount) as bigint),cast(sum(net_amount) as bigint) \n");
			lSBQuery.append(" from Trn_Pension_Supply_Bill_Dtls WHERE request_No = :requestNo AND status in (:billStatusList) and location_Code = :locationCode \n");
			lSBQuery.append(" group by scheme_code,pay_mode,bank_code,branch_code,head_code \n");
					
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("requestNo", lStrRequestNo);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setString("locationCode", lStrLocationCode);
			
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lLstResult;
	}

	
	public List<TrnPensionSupplyBillDtls> getSupplPnsnDtlsFromRequestNo(
			String lStrRequestNo, List<Short> lLstBillStatus,
			String lStrLocationCode) throws Exception {
		// TODO Auto-generated method stub
		List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = new ArrayList<TrnPensionSupplyBillDtls>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" from TrnPensionSupplyBillDtls WHERE requestNo = :requestNo AND status in (:billStatusList) and locationCode = :locationCode \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("requestNo", lStrRequestNo);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setString("locationCode", lStrLocationCode);
			
			lLstTrnPensionSupplyBillDtls = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lLstTrnPensionSupplyBillDtls;
	}

	
	public void deleteRecoveryDtlsFromSupplBillId(Long lLngPensionSupplBillId) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("delete from TrnPensionRecoveryDtls where pensionSupplyBillId = :pensionSupplyBillId \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("pensionSupplyBillId", lLngPensionSupplBillId);
						
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		
	}

	
	public void deleteSixPayArrearDtlsFromSupplBillId(Long lLngPensionSupplBillId)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("delete from TrnPensionSixpayfpArrear where pensionSupplyBillId = :pensionSupplyBillId \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("pensionSupplyBillId", lLngPensionSupplBillId);
						
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

	}

	
	public void updateBillNoInSupplDtls(List<Long> lLstSupplBillId,Long lLngBillNo,Short lShStatus)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("update TrnPensionSupplyBillDtls set billNo = :billNo,status = :status where pensionSupplyBillId in (:supplyBillIdList) \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setShort("status", lShStatus);
			lQuery.setParameterList("supplyBillIdList", lLstSupplBillId);
						
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	
	public void updateBillNoInRecoveryDtls(List<Long> lLstSupplBillId,Long lLngBillNo)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("update TrnPensionRecoveryDtls set billNo = :billNo where pensionSupplyBillId in (:supplyBillIdList) \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setParameterList("supplyBillIdList", lLstSupplBillId);
						
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	
	public void updateBillNoInSixPayArrearDtls(List<Long> lLstSupplBillId,Long lLngBillNo)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("update TrnPensionSixpayfpArrear set billNo = :billNo where paidFlag = 'N' and activeFlag = 'Y' and payInMonth is not null and  pensionSupplyBillId in (:supplyBillIdList) \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setParameterList("supplyBillIdList", lLstSupplBillId);
						
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	
	public List<Object> getSchemewiseRecoveryDtlsFromBillNo(
			Long lLngBillNo) throws Exception {
		// TODO Auto-generated method stub
		List<Object> lLstTrnPensionRecoveryDtls = new ArrayList<Object>();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select schemeCode,cast(sum(amount) as double) from TrnPensionRecoveryDtls where billNo = :billNo group by schemeCode");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", lLngBillNo);
									
			lLstTrnPensionRecoveryDtls = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstTrnPensionRecoveryDtls;
	}

	
	public void updateStatusInSupplBillDtls(Long lLngBillNo,Short lStatus,BigDecimal lBgDcmlUserId,BigDecimal lBgDcmlPostId,Date lDtCurrDate) throws Exception {
		// TODO Auto-generated method stub
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("update TrnPensionSupplyBillDtls set status = :status,updatedUserId = :updatedUserId,updatedPostId = :updatedPostId,updatedDate = :updatedDate where billNo = :billNo \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			
			lQuery.setShort("status", lStatus);
			lQuery.setBigDecimal("updatedUserId", lBgDcmlUserId);
			lQuery.setBigDecimal("updatedPostId", lBgDcmlPostId);
			lQuery.setDate("updatedDate", lDtCurrDate);
			lQuery.setLong("billNo",lLngBillNo);
									
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}


	public void updatePaidFlagInSixPayFromBillNo(Long lLngBillNo,BigDecimal lBgDcmlUserId,BigDecimal lBgDcmlPostId,Date lDtCurrDate)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("update TrnPensionSixpayfpArrear set paidFlag = 'Y',updatedUserId = :updatedUserId,updatedPostId = :updatedPostId,updatedDate = :updatedDate where billNo = :billNo \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			
			lQuery.setBigDecimal("updatedUserId", lBgDcmlUserId);
			lQuery.setBigDecimal("updatedPostId", lBgDcmlPostId);
			lQuery.setDate("updatedDate", lDtCurrDate);
			lQuery.setLong("billNo", lLngBillNo);
									
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	
	public List<TrnPensionRqstHdr> getAllocationPercentFromPensionerCode(
			List<String> lLstPensionerCode, String lStrLocCode)
			throws Exception {
		// TODO Auto-generated method stub
		List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = new ArrayList<TrnPensionRqstHdr>();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select new com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr(pensionerCode,orgBf10436Percent,orgAf10436Percent,orgAf11156Percent,orgAf10560Percent,orgZpAfPercent) \n");
			lSBQuery.append("from TrnPensionRqstHdr where pensionerCode in (:pensionerCodeList) and locationCode = :locationCode ");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("pensionerCodeList", lLstPensionerCode);
			lQuery.setString("locationCode", lStrLocCode);
			
			lLstTrnPensionRqstHdr = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstTrnPensionRqstHdr;
	}

	
	public void updateActiveFlagInSixPayArrear(String lStrPensionerCode,
			Character lCharRvsnCntr) throws Exception {
		// TODO Auto-generated method stub
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			Session ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append("update TrnPensionSixpayfpArrear set activeFlag = 'Y' where pensionerCode =:pensionerCode and revisionCounter =:revisionCounter and arrearType = :arrearType ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPensionerCode);
			lHibQry.setParameter("revisionCounter", lCharRvsnCntr);
			lHibQry.setCharacter("arrearType", 'S');
			lHibQry.executeUpdate();
			
		} catch (Exception e) {
			throw e;
			
		}
	}

	
	public String getRequestNoFromPpoNo(String lStrPpoNo,List<Short> lLstStatus, String lStrLocCode)
			throws Exception {
		// TODO Auto-generated method stub
		String lStrRequestNo = "";
		List lLstResult = new ArrayList();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select requestNo from TrnPensionSupplyBillDtls where ppoNo = :ppoNo and billType = 'PENSION' and requestNo is not null \n");
			lSBQuery.append(" and locationCode = :locationCode and status in (:statusList) ");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("ppoNo", lStrPpoNo);
			lQuery.setParameterList("statusList", lLstStatus);
			lQuery.setString("locationCode", lStrLocCode);
			
			lLstResult = lQuery.list();
			if(!lLstResult.isEmpty())
			{
				lStrRequestNo = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lStrRequestNo;
	}

	
}
