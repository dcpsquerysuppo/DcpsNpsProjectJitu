/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerSeenDtls;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * May 30, 2011
 */
public class LifeCertificateDAOImpl extends GenericDaoHibernateImpl implements LifeCertificateDAO{
	
	private Session ghibSession = null;
	private static final Logger gLogger = Logger
			.getLogger(LifeCertificateDAOImpl.class);
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	
	public LifeCertificateDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getLifeCertificateCount(java.util.List, java.util.Date, java.lang.String)
	 */
	public Integer getLifeCertificateCount(List<String> lLstCaseStatus,Date lDtDate,String lStrLocCode, String lStrSearchCrt,String lStrSearchType, String lStrSearchVal, String lStrPageNo, String lStrBankCode, String lStrBranchCode,String lStrLifeCertFlag,Long lLngPostId) throws Exception {
		List lLstLifeCertificate = new ArrayList();
		BigInteger lBigIntCount;
		Integer lIntCount=0;
		Query lSqlQuery = null;
		try {
			
			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();
			
			lStrQuery.append("SELECT COUNT(mph.pensioner_Code) \n");
			lStrQuery.append(" FROM trn_pension_rqst_hdr prh \n");
			lStrQuery.append(" JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code  \n");
			lStrQuery.append(" JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code \n");
			lStrQuery.append(" LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage = :lPercent  \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_pensioner_seen_dtls tps ON mph.pensioner_code = tps.pensioner_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_pension_arrear_dtls tpa ON mph.pensioner_code = tpa.pensioner_code and tpa.paid_flag = 'N' and arrear_field_type = :arrearType \n");
			lStrQuery.append(" LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_bank_branch rbb ON mpd.branch_code=rbb.branch_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code  \n");
			lStrQuery.append(" WHERE audi.post_id = :postId AND prh.case_owner = :postId \n");
			lStrQuery.append(" AND prh.case_status IN (:caseStatus) and prh.status = :status \n");
			lStrQuery.append(" AND prh.location_Code=:locationCode \n");
		    
			  
//			lStrQuery.append(" FROM trn_pension_rqst_hdr tpr, Mst_Pensioner_Dtls mpd, mst_bank mb, rlt_bank_branch rbb, \n");
//			lStrQuery.append(" mst_pensioner_hdr mph LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage= :lPercent \n");
//			lStrQuery.append(" LEFT OUTER JOIN trn_pensioner_seen_dtls tps ON mph.pensioner_code = tps.pensioner_code \n");
//			lStrQuery.append(" WHERE mph.pensioner_code=tpr.pensioner_code \n"); 
//			lStrQuery.append(" AND mph.pensioner_code=mpd.pensioner_code  \n");
//			lStrQuery.append(" AND mpd.bank_code=mb.bank_code \n"); 
//			lStrQuery.append(" AND mpd.branch_code=rbb.branch_code \n");
//			lStrQuery.append(" AND tpr.case_status IN (:caseStatus) \n");	
//			lStrQuery.append(" AND tpr.status=:status AND tpr.location_Code=:locationCode ");
						
			//lStrQuery.append(" AND mpd.identification_Date < :lDtDate "); 
			
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lStrQuery.append(" AND prh.ppo_No LIKE :ppoNo ");
					}
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						if("Normal".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								lStrQuery.append(" AND prh.ledger_No = :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No = :pageNo ");
								}
							}
						}
						if("Onward".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								
								lStrQuery.append(" AND prh.ledger_No >= :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No >= :pageNo ");
								}
							}
						}
					}
							
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND mpd.bank_code = :bankCode ");
					lStrQuery.append(" AND mpd.branch_code = :branchCode ");
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND prh.seen_flag = :lifeCertFlag ");
				}
			}

			
			
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());
			lSqlQuery.setLong("postId", lLngPostId);
			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setParameterList("caseStatus", lLstCaseStatus);
			lSqlQuery.setString("arrearType", gObjRsrcBndle.getString("ARREARTYPE.LC"));
			lSqlQuery.setString("locationCode", lStrLocCode);
			//lSqlQuery.setDate("lDtDate", lDtDate);
			lSqlQuery.setLong("lPercent", 100);
			
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lSqlQuery.setString("ppoNo", "%" + lStrSearchVal.toUpperCase() + "%");
					}
					
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						
						if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
							lSqlQuery.setString("ledgerNo", lStrSearchVal);
											
							if (lStrPageNo != null && !lStrPageNo.equals("")){
								lSqlQuery.setString("pageNo", lStrPageNo);
							}
						}
					}
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lSqlQuery.setString("bankCode", lStrBankCode);
					lSqlQuery.setString("branchCode", lStrBranchCode);
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lSqlQuery.setString("lifeCertFlag", lStrLifeCertFlag);
					
				}
			}
			lLstLifeCertificate = lSqlQuery.list();

			lIntCount = Integer.parseInt(lLstLifeCertificate.get(0).toString());
		
		} catch (Exception e) {
			gLogger.error(" Error is :"+ e, e);
			throw e;
		}
		return lIntCount;
	}
	
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getLifeCertificateList()
	 */
	
	
	public List getLifeCertificateList(Map displayTag,List<String> lLstCaseStatus,Date lDtDate,String lStrLocCode, String lStrSearchCrt, String lStrSortCrt, String lStrSearchType, String lStrSearchVal, String lStrPageNo, String lStrBankCode, String lStrBranchCode,String lStrLifeCertFlag,Long lLngPostId)throws Exception {
	

		List lLstLifeCertificate = new ArrayList();
		List lLstResult = new ArrayList();
		Query lSqlQuery = null;
		
		String[] lStrArrValues = null; // Added by Mubeen on 03 Dec 2012
		
		
		Integer lIntOrderByPara = null; // Added by Mubeen on 03 Dec 2012
				
		
		try {
						
			StringBuilder lStrQuery = new StringBuilder();
			String lStrColumnValues1 = null;
			String lStrColumnValues2 = null;
			
			
			Session hibSession = getSession();
			
			// lStrArrValues = new String[]{"", "prh.ppo_no", "mph.first_name", "prh.ledger_no", "mpd.acount_no"}; // Added by Mubeen on 03 Dec 2012
			
			
			lStrArrValues = new String[]{"", "RST.PPN", "RST.FN", "RST.LN", "RST.BN","RST.BR","RST.AC"}; // Added by Mubeen on 03 Dec 2012
			
			lStrColumnValues1 = "mph.pensioner_code as PC," + "prh.ppo_no as PPN," + "mph.first_name as FN," + "mpf.name as NM," + "mph.alive_flag as AF,"
			                 + "prh.ledger_no as LN,"+ "prh.page_no as PN,"+ "mb.bank_name as BN,"+ "rbb.branch_name as BR,"+ "mpd.acount_no as AC,"
			                 + "tps.seen_date as SD,"+ "prh.seen_flag as SF,"+ "tpa.total_difference_amt as TD,"+ "tpa.payment_from_yyyymm as PF,"+"prh.pension_request_id as PR";
			
			
			
			lStrQuery.append("select RST.PC,RST.PPN,RST.FN,RST.NM,RST.AF,RST.LN,RST.PN,RST.BN,RST.BR,RST.AC,RST.SD,RST.SF,RST.TD,RST.PF,RST.PR from ((SELECT \n");
			lStrQuery.append(lStrColumnValues1 + "\n");
			lStrQuery.append(" FROM trn_pension_rqst_hdr prh \n");
			lStrQuery.append(" JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code AND mph.date_of_death is null \n");
			lStrQuery.append(" JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code \n");
			lStrQuery.append(" LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage = :lPercent  \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_pensioner_seen_dtls tps ON mph.pensioner_code = tps.pensioner_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_pension_arrear_dtls tpa ON mph.pensioner_code = tpa.pensioner_code and tpa.paid_flag = 'N' and arrear_field_type = :arrearType \n");
			lStrQuery.append(" LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_bank_branch rbb ON mpd.branch_code=rbb.branch_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code  \n");
			lStrQuery.append(" WHERE audi.post_id = :postId  AND prh.case_owner = :postId \n");
			lStrQuery.append(" AND prh.case_status IN (:caseStatus) and prh.status = :status \n");
			lStrQuery.append(" AND prh.location_Code=:locationCode \n");
						 
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lStrQuery.append(" AND prh.ppo_No LIKE :ppoNo ");
					}
					if("mpd.account_no".equalsIgnoreCase(lStrSearchCrt)){
						lStrQuery.append(" AND mpd.acount_no = :accountNo ");
					}
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						if("Normal".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								lStrQuery.append(" AND prh.ledger_No = :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No = :pageNo ");
								}
							}
						}
						if("Onward".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								
								lStrQuery.append(" AND prh.ledger_No >= :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No >= :pageNo ");
								}
							}
						}
					}
							
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND mpd.bank_code = :bankCode ");
					lStrQuery.append(" AND mpd.branch_code = :branchCode ");
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND prh.seen_flag = :lifeCertFlag ");
				}
			}
						
			lStrQuery.append(") UNION (");
			
			lStrColumnValues2 = "mph.pensioner_code as PC," + "prh.ppo_no as PPN," + "mpf.name as NM," + "mpf.name as NM," + "mph.alive_flag as AF,"
            + "prh.ledger_no as LN,"+ "prh.page_no as PN,"+ "mb.bank_name as BN,"+ "rbb.branch_name as BR,"+ "mpd.acount_no as AC,"
            + "tps.seen_date as SD,"+ "prh.seen_flag as SF,"+ "tpa.total_difference_amt as TD,"+ "tpa.payment_from_yyyymm as PF,"+"prh.pension_request_id as PR";

			
					
			lStrQuery.append("SELECT \n");
			lStrQuery.append(lStrColumnValues2 + "\n");
			lStrQuery.append(" FROM trn_pension_rqst_hdr prh \n");
			lStrQuery.append(" JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code AND mph.date_of_death is not null \n");
			lStrQuery.append(" JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code \n");
			lStrQuery.append(" LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage = :lPercent  \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_pensioner_seen_dtls tps ON mph.pensioner_code = tps.pensioner_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_pension_arrear_dtls tpa ON mph.pensioner_code = tpa.pensioner_code and tpa.paid_flag = 'N' and arrear_field_type = :arrearType \n");
			lStrQuery.append(" LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_bank_branch rbb ON mpd.branch_code=rbb.branch_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code  \n");
			lStrQuery.append(" WHERE audi.post_id = :postId  AND prh.case_owner = :postId \n");
			lStrQuery.append(" AND prh.case_status IN (:caseStatus) and prh.status = :status \n");
			lStrQuery.append(" AND prh.location_Code=:locationCode \n");
						 
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lStrQuery.append(" AND prh.ppo_No LIKE :ppoNo ");
					}
					if("mpd.account_no".equalsIgnoreCase(lStrSearchCrt)){
						lStrQuery.append(" AND mpd.acount_no = :accountNo ");
					}
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						if("Normal".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								lStrQuery.append(" AND prh.ledger_No = :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No = :pageNo ");
								}
							}
						}
						if("Onward".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								
								lStrQuery.append(" AND prh.ledger_No >= :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No >= :pageNo ");
								}
							}
						}
					}
							
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND mpd.bank_code = :bankCode ");
					lStrQuery.append(" AND mpd.branch_code = :branchCode ");
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND prh.seen_flag = :lifeCertFlag ");
				}
			}
			
			
			
			
			/* Commented by Mubeen 04 DEC 2012 - Start
			// lStrQuery.append(" ORDER BY ");
			lStrQuery.append(")) as RST ORDER BY RST.BN,RST.BR");
			Commented by Mubeen 04 DEC 2012 - End */

			
			// Added by Mubeen 04 DEC 2012 - Start
					
			if ( lStrSortCrt != null && !lStrSortCrt.equals("") && !lStrSortCrt.equals("-1")){
						if("prh.ppo_no".equalsIgnoreCase(lStrSortCrt)){
							lStrQuery.append(")) as RST ORDER BY RST.BN,RST.BR,RST.PPN");
							//lStrQuery.append(" mb.bank_name, rbb.branch_name, prh.ppo_no ");
						}	
						else if ("mpd.account_no".equalsIgnoreCase(lStrSortCrt)){
							lStrQuery.append(")) as RST ORDER BY RST.BN,RST.BR,RST.AC");
							// lStrQuery.append(" mb.bank_name, rbb.branch_name, mpd.acount_no ");
						}
						else if ("mpf.name".equalsIgnoreCase(lStrSortCrt)){
							lStrQuery.append(")) as RST ORDER BY RST.BN,RST.BR,RST.FN");
						}
						else if ("prh.ledger_no".equalsIgnoreCase(lStrSortCrt)){
							lStrQuery.append(")) as RST ORDER BY RST.BN,RST.BR,RST.LN,RST.PN");
						}
						
						
			}else{
						lStrQuery.append(")) as RST ORDER BY RST.BN,RST.BR");
						//lStrQuery.append(" prh.ledger_No,prh.page_No, mb.bank_name, rbb.branch_name ");
			}
			
			// Added by Mubeen 04 DEC 2012 - End 
			
			
			
			
			
			
			
			
			// Added Mubeen on 03 Dec 2012 Start
					 
			String lStrOrderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");
			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				lIntOrderByPara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lStrQuery.append(","+lStrArrValues[lIntOrderByPara.intValue()] + " " + lStrOrderString);
			}

			// Added Mubeen on 03 Dec 2012 End
						
			

			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			
			
			
			lSqlQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lSqlQuery.setMaxResults(Constants.PAGE_SIZE);
			
			
			lSqlQuery.setLong("postId", lLngPostId);			
			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setParameterList("caseStatus", lLstCaseStatus);
			lSqlQuery.setString("arrearType", gObjRsrcBndle.getString("ARREARTYPE.LC"));
			lSqlQuery.setString("locationCode", lStrLocCode);
			//lSqlQuery.setDate("lDtDate", lDtDate);
			lSqlQuery.setLong("lPercent", 100);
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lSqlQuery.setString("ppoNo", "%" + lStrSearchVal.toUpperCase() + "%");
					}
					if("mpd.account_no".equalsIgnoreCase(lStrSearchCrt)){
						lSqlQuery.setString("accountNo", lStrSearchVal);
					}
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						
						if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
							lSqlQuery.setString("ledgerNo", lStrSearchVal);
											
							if (lStrPageNo != null && !lStrPageNo.equals("")){
								lSqlQuery.setString("pageNo", lStrPageNo);
							}
						}
					}
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lSqlQuery.setString("bankCode", lStrBankCode);
					lSqlQuery.setString("branchCode", lStrBranchCode);
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lSqlQuery.setString("lifeCertFlag", lStrLifeCertFlag);
					
				}
			}
			
			lLstResult = lSqlQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
			Iterator it = lLstResult.iterator();
			Object[] tuple = null;
			Object[] tmpTuple = null;
			
			// ---For db2 result array has one more element of row id.So
			// removing that extra element and changing index of array
			// accordingly for db2.
			int actualLength = (lStrColumnValues1 != null) ? lStrColumnValues1.split(",").length : 0;
			int resultLength = ((Object[]) lLstResult.get(0)).length;
			boolean isDb2 = false;
			if (actualLength != resultLength) {
				isDb2 = true;
			}
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				if (isDb2) {
					tmpTuple = tuple;
					tuple = new Object[resultLength - 1];
					for (int cnt = 0; cnt < (resultLength - 1); cnt++) {
						tuple[cnt] = tmpTuple[cnt + 1];
					}
					
				}
				lLstLifeCertificate.add(tuple);
			}
		}

		} catch (Exception e) {
			gLogger.error(" Error is :"+ e, e);
			throw e;
		}
		return lLstLifeCertificate;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#generateLifeCertificate(java.util.Map)
	 */
	public List generateLifeCertificate(List<String> lLstCaseStatus,Date lDtDate,String lStrLocCode, String lStrSearchCrt,String lStrSearchType, String lStrSearchVal, String lStrPageNo, List<String> lLstPensionerCode, String lStrBankCode, String lStrBranchCode,String lStrLifeCertFlag,Long lLngPostId) throws Exception {
		List lLstResult = new ArrayList();
		
		Query lSqlQuery = null;
		try {
			
			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();
			
			lStrQuery.append("SELECT prh.pensioner_code,prh.ppo_no,mph.first_name,mph.alive_flag,mb.bank_name,rbb.branch_name,mpd.acount_no,mpd.bank_code,mpd.branch_code  \n");
			lStrQuery.append("FROM trn_pension_rqst_hdr prh \n");
			lStrQuery.append("JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code and mph.date_of_death is null \n");
			lStrQuery.append("JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code \n");
			//lStrQuery.append("LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage = :lPercent \n");
			lStrQuery.append("LEFT OUTER JOIN trn_pensioner_seen_dtls tps ON mph.pensioner_code = tps.pensioner_code  \n");
			lStrQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
		    lStrQuery.append("LEFT OUTER JOIN rlt_bank_branch rbb ON mpd.branch_code=rbb.branch_code  \n");
		    lStrQuery.append("LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code  \n");
		    lStrQuery.append("WHERE audi.post_id = :postId  AND prh.case_owner = :postId \n");
		    lStrQuery.append("AND prh.case_status IN (:caseStatus)  and prh.status = :status \n");
		    lStrQuery.append("AND prh.location_Code=:locationCode \n");
					

			if(!lLstPensionerCode.isEmpty())
			{
				lStrQuery.append(" AND mph.pensioner_code IN (:pensionerCode) ");
			}
			
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lStrQuery.append(" AND prh.ppo_No LIKE :ppoNo ");
					}
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						if("Normal".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								lStrQuery.append(" AND prh.ledger_No = :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No = :pageNo ");
								}
							}
						}
						if("Onward".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								
								lStrQuery.append(" AND prh.ledger_No >= :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No >= :pageNo ");
								}
							}
						}
					}
							
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND mpd.bank_code = :bankCode ");
					lStrQuery.append(" AND mpd.branch_code = :branchCode ");
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND tps.seen_flage = :lifeCertFlag ");
				}
			}
			lStrQuery.append("GROUP BY mb.bank_name,rbb.branch_name,mpd.bank_code,mpd.branch_code,mpd.acount_no,prh.ppo_no,mph.first_name,mph.alive_flag,prh.pensioner_code \n");
			lStrQuery.append("UNION ");
			lStrQuery.append("SELECT prh.pensioner_code,prh.ppo_no,mpf.name,mph.alive_flag,mb.bank_name,rbb.branch_name,mpd.acount_no,mpd.bank_code,mpd.branch_code  \n");
			lStrQuery.append("FROM trn_pension_rqst_hdr prh \n");
			lStrQuery.append("JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code and mph.date_of_death is not null \n");
			lStrQuery.append("JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code \n");
			lStrQuery.append("LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage = :lPercent \n");
			lStrQuery.append("LEFT OUTER JOIN trn_pensioner_seen_dtls tps ON mph.pensioner_code = tps.pensioner_code  \n");
			lStrQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
		    lStrQuery.append("LEFT OUTER JOIN rlt_bank_branch rbb ON mpd.branch_code=rbb.branch_code  \n");
		    lStrQuery.append("LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code  \n");
		    lStrQuery.append("WHERE audi.post_id = :postId  AND prh.case_owner = :postId \n");
		    lStrQuery.append("AND prh.case_status IN (:caseStatus)  and prh.status = :status \n");
		    lStrQuery.append("AND prh.location_Code=:locationCode \n");
					

			if(!lLstPensionerCode.isEmpty())
			{
				lStrQuery.append(" AND mph.pensioner_code IN (:pensionerCode) ");
			}
			
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lStrQuery.append(" AND prh.ppo_No LIKE :ppoNo ");
					}
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						if("Normal".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								lStrQuery.append(" AND prh.ledger_No = :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No = :pageNo ");
								}
							}
						}
						if("Onward".equalsIgnoreCase(lStrSearchType))
						{
							if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
								
								lStrQuery.append(" AND prh.ledger_No >= :ledgerNo ");
													
								if (lStrPageNo != null && !lStrPageNo.equals("")){
									lStrQuery.append(" AND prh.page_No >= :pageNo ");
								}
							}
						}
					}
							
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND mpd.bank_code = :bankCode ");
					lStrQuery.append(" AND mpd.branch_code = :branchCode ");
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lStrQuery.append(" AND tps.seen_flage = :lifeCertFlag ");
				}
			}
			lStrQuery.append("GROUP BY mb.bank_name,rbb.branch_name,mpd.bank_code,mpd.branch_code,mpd.acount_no,prh.ppo_no,mpf.name,mph.alive_flag,prh.pensioner_code \n");
			lStrQuery.append("ORDER BY 5,6,3 \n");
			
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());		
			lSqlQuery.setLong("postId", lLngPostId);
			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setParameterList("caseStatus", lLstCaseStatus);
			lSqlQuery.setString("locationCode", lStrLocCode);
			//lSqlQuery.setDate("lDtDate", lDtDate);
			lSqlQuery.setLong("lPercent", 100);
			
			if(!lLstPensionerCode.isEmpty())
			{
				lSqlQuery.setParameterList("pensionerCode", lLstPensionerCode);
			}
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)){
						lSqlQuery.setString("ppoNo", "%" + lStrSearchVal + "%");
					}
					if (lStrSearchType != null && !lStrSearchType.equals("") && !lStrSearchType.equals("-1")) {
						
						if("prh.ledger_page_no".equalsIgnoreCase(lStrSearchCrt)){
							lSqlQuery.setString("ledgerNo", lStrSearchVal);
											
							if (lStrPageNo != null && !lStrPageNo.equals("")){
								lSqlQuery.setString("pageNo", lStrPageNo);
							}
						}
					}
				}
				if("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)){
					lSqlQuery.setString("bankCode", lStrBankCode);
					lSqlQuery.setString("branchCode", lStrBranchCode);
				}
				if("prh.life_cert_flag".equalsIgnoreCase(lStrSearchCrt)){
					lSqlQuery.setString("lifeCertFlag", lStrLifeCertFlag);
					
				}
			}
			
			lLstResult = lSqlQuery.list();
						
		} catch (Exception e) {
			gLogger.error(" Error is :"+ e, e);
			throw e;
		}
		return lLstResult;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getAtoNameFromLocationCode(java.lang.String)
	 */
	public String getAtoNameFromLocationCode(String lStrLocCode) throws Exception {
		
		String lStrAtoName = null;
		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			
			lSBQuery.append(" SELECT concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname)" );
			lSBQuery.append(" FROM OrgPostMst opm, AclPostroleRlt apr, OrgUserpostRlt our, OrgEmpMst oem  " );
			lSBQuery.append(" WHERE opm.postId = apr.orgPostMst.postId");
			lSBQuery.append(" AND opm.postId =  our.orgPostMstByPostId.postId ");
			lSBQuery.append(" AND our.orgUserMst.userId =  oem.orgUserMst.userId ");
			lSBQuery.append(" AND apr.aclRoleMst.roleId  =  :roleId ");
			lSBQuery.append(" AND opm.locationCode  =  :locationCode ");
			
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("roleId", gObjRsrcBndle.getString("PPMT.ATOROLE"));
			hqlQuery.setString("locationCode", lStrLocCode);
			
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				lStrAtoName = lLstResult.get(0).toString();
			}
			
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrAtoName;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#validatePpoWithReceivedCert(java.lang.String, java.lang.String)
	 */
	public List validatePpoWithReceivedCert(String lStrLocCode,String lStrPpoNo, Long lLngFinYearId) throws Exception{
		
		List lLstResult = new ArrayList();
		try {
			Query lSqlQuery = null;
			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();
			
			lStrQuery.append("SELECT mph.pensioner_Code,tpr.ppo_No,mph.first_Name,mpf.name,\n"); 
			lStrQuery.append(" mpd.acount_No,mph.alive_Flag,mbk.bank_Name,rlt.branch_Name,tps.seen_Date \n"); 
			lStrQuery.append(" FROM Trn_Pension_Rqst_Hdr tpr,Mst_Pensioner_Dtls mpd, \n"); 
			lStrQuery.append(" Mst_Bank mbk, Rlt_Bank_Branch rlt, Trn_Pensioner_Seen_Dtls tps, \n"); 
			lStrQuery.append(" Mst_Pensioner_Hdr mph LEFT OUTER JOIN Mst_Pensioner_Family_Dtls mpf ON mph.pensioner_Code = mpf.pensioner_Code AND  mpf.percentage=:lPercent \n");
			lStrQuery.append(" where mph.pensioner_Code = tpr.pensioner_Code and mph.pensioner_Code = mpd.pensioner_Code \n"); 
			lStrQuery.append(" and  mph.pensioner_Code=tps.pensioner_Code \n"); 
			lStrQuery.append(" and mpd.bank_Code=mbk.bank_Code and mpd.branch_Code=rlt.branch_Code and tpr.status=:status \n"); 
			lStrQuery.append(" and tpr.ppo_No=:ppoNo AND tpr.location_Code=:locationCode \n"); 
			lStrQuery.append(" and tps.life_cert_status= :lifeCertStatus and tps.fin_year_id= :finYearId \n"); 
			
			
//			lStrQuery.append("SELECT mph.pensioner_Code,tpr.ppo_No,mphc.description,mph.first_Name,mpf.name,mph.date_Of_Retirement,tpr.commencement_Date, \n"); 
//			lStrQuery.append(" mpd.acount_No,mph.alive_Flag,mbk.bank_Name,rlt.branch_Name,tps.seen_Date \n");
//			lStrQuery.append(" FROM Trn_Pension_Rqst_Hdr tpr,Mst_Pensioner_Dtls mpd, \n");
//			lStrQuery.append(" Mst_Pension_Headcode mphc, Mst_Bank mbk, Rlt_Bank_Branch rlt, Trn_Pensioner_Seen_Dtls tps, \n");
//			lStrQuery.append(" Mst_Pensioner_Hdr mph LEFT OUTER JOIN Mst_Pensioner_Family_Dtls mpf ON mph.pensioner_Code = mpf.pensioner_Code AND  mpf.percentage=:lPercent \n");
//			lStrQuery.append(" WHERE mph.pensioner_Code = tpr.pensioner_Code AND mph.pensioner_Code = mpd.pensioner_Code  \n");
//			lStrQuery.append(" AND  mph.pensioner_Code=tps.pensioner_Code AND  mphc.head_Code=tpr.head_Code AND tpr.seen_Flag='N' \n");
//			lStrQuery.append(" AND tps.seen_Flage='N' AND mpd.bank_Code=mbk.bank_Code  AND mpd.branch_Code=rlt.branch_Code AND tpr.status=:status \n");
//			lStrQuery.append(" AND tpr.ppo_No=:ppoNo AND tpr.location_Code=:locationCode \n");
			
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());
						
			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setString("ppoNo",lStrPpoNo.toUpperCase());
			lSqlQuery.setString("locationCode", lStrLocCode);
			lSqlQuery.setLong("lPercent", 100);
			lSqlQuery.setString("lifeCertStatus", gObjRsrcBndle.getString("LIFECERT.SAVED"));
			lSqlQuery.setLong("finYearId", lLngFinYearId);
			
			lLstResult = lSqlQuery.list();
		
		} catch (Exception e) {
			gLogger.error("Error is :"+ e, e);
			throw e;
		}
		return lLstResult;
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getPensionerDetailsFromPpoNo(java.lang.String, java.lang.String)
	 */
	public List getPnsnrDtlsFromPpoNo(String lStrPpoNo, String lStrLocCode)
			throws Exception {
		List lLstResult = new ArrayList();
		try {
			Query lSqlQuery = null;
			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();

			lStrQuery.append("SELECT mph.pensioner_Code,tpr.ppo_No,mph.first_Name,mpf.name,tpr.ledger_no,tpr.page_no, \n");
			lStrQuery.append(" mph.alive_Flag,mbk.bank_Name,rlt.branch_Name,mpd.acount_No \n");
			lStrQuery.append(" FROM trn_pension_rqst_hdr tpr,Mst_Pensioner_Dtls mpd, \n");
			lStrQuery.append(" Mst_Bank mbk, Rlt_Bank_Branch rlt, mst_pensioner_hdr mph  \n");
			lStrQuery.append(" LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage=:lPercent \n");
			lStrQuery.append(" WHERE mph.pensioner_code=tpr.pensioner_code  \n");
			lStrQuery.append(" AND mph.pensioner_Code = mpd.pensioner_Code  \n");
			lStrQuery.append(" AND mpd.bank_Code=mbk.bank_Code  AND mpd.branch_Code=rlt.branch_Code \n");
			lStrQuery.append(" AND tpr.ppo_no=:ppoNo AND tpr.status=:status \n");
			lStrQuery.append(" AND tpr.location_code=:locationCode");

			
//			lStrQuery.append("SELECT mph.pensioner_Code,tpr.ppo_No,mphc.description,mph.first_Name,mpf.name,mph.date_Of_Retirement, \n");
//			lStrQuery.append(" tpr.commencement_date,mpd.acount_No,mph.alive_Flag,mbk.bank_Name,rlt.branch_Name \n");
//			lStrQuery.append(" FROM trn_pension_rqst_hdr tpr,Mst_Pensioner_Dtls mpd,Mst_Pension_Headcode mphc, \n");
//			lStrQuery.append(" Mst_Bank mbk, Rlt_Bank_Branch rlt, mst_pensioner_hdr mph \n");
//			lStrQuery.append(" LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage=:lPercent \n"); 
//			lStrQuery.append(" WHERE mph.pensioner_code=tpr.pensioner_code \n");  
//			lStrQuery.append(" AND mph.pensioner_Code = mpd.pensioner_Code AND  mphc.head_Code=tpr.head_Code \n");  
//			lStrQuery.append(" AND mpd.bank_Code=mbk.bank_Code  AND mpd.branch_Code=rlt.branch_Code \n");
//			lStrQuery.append(" AND tpr.ppo_no=:ppoNo AND tpr.seen_Flag='N' AND tpr.status=:status \n");
//			lStrQuery.append(" AND tpr.location_Code=:locationCode");
			
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());
			
			lSqlQuery.setString("ppoNo", lStrPpoNo.toUpperCase());
			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setString("locationCode", lStrLocCode);
			lSqlQuery.setLong("lPercent", 100);
			lLstResult = lSqlQuery.list();
			
		} catch (Exception e) {
			gLogger.error("Error is :"+ e, e);
			throw e;
		}
		return lLstResult;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getReceivedLifeCertificateCount(java.lang.String)
	 */
	public Integer getReceivedLifeCertificateCount(String lStrLocCode)
			throws Exception {
		List lLstLifeCertificate = new ArrayList();
		BigInteger lBigIntCount;
		Integer lIntCount=0;
		Query lSqlQuery = null;
		try {
			
			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();
			
			lStrQuery.append("SELECT COUNT(mph.pensioner_Code) \n");
			lStrQuery.append(" FROM Trn_Pension_Rqst_Hdr tpr,Mst_Pensioner_Dtls mpd, \n");
			lStrQuery.append(" Mst_Bank mbk, Rlt_Bank_Branch rlt, Trn_Pensioner_Seen_Dtls tps,\n");
			lStrQuery.append(" Mst_Pensioner_Hdr mph LEFT OUTER JOIN Mst_Pensioner_Family_Dtls mpf ON  mph.pensioner_Code = mpf.pensioner_Code AND  mpf.percentage=:lPercent \n");
			lStrQuery.append(" WHERE mph.pensioner_Code = tpr.pensioner_Code AND mph.pensioner_Code = mpd.pensioner_Code \n");
			lStrQuery.append(" AND  mph.pensioner_Code=tps.pensioner_Code \n");
			lStrQuery.append(" AND tps.life_cert_status= :lifeCertStatus AND mpd.bank_Code=mbk.bank_Code  AND mpd.branch_Code=rlt.branch_Code AND tpr.status=:status \n");
			lStrQuery.append(" AND tpr.location_Code =  :locationCode \n");
			
//			lStrQuery.append(" FROM Trn_Pension_Rqst_Hdr tpr,Mst_Pensioner_Dtls mpd, \n");
//			lStrQuery.append(" Mst_Pension_Headcode mphc, Mst_Bank mbk, Rlt_Bank_Branch rlt, Trn_Pensioner_Seen_Dtls tps, \n");
//			lStrQuery.append(" Mst_Pensioner_Hdr mph LEFT OUTER JOIN Mst_Pensioner_Family_Dtls mpf ON  mph.pensioner_Code = mpf.pensioner_Code AND  mpf.percentage=:lPercent \n");
//			lStrQuery.append(" WHERE mph.pensioner_Code = tpr.pensioner_Code AND mph.pensioner_Code = mpd.pensioner_Code \n");
//			lStrQuery.append(" AND mph.pensioner_Code=tps.pensioner_Code AND mphc.head_Code=tpr.head_Code AND tpr.seen_Flag='N' \n");
//			lStrQuery.append(" AND tps.seen_Flage='N' AND mpd.bank_Code=mbk.bank_Code AND mpd.branch_Code=rlt.branch_Code AND tpr.status=:status \n");
//			lStrQuery.append(" AND tpr.location_Code = :locationCode \n");
	
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());		
			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setString("lifeCertStatus", gObjRsrcBndle.getString("LIFECERT.SAVED"));
			lSqlQuery.setString("locationCode", lStrLocCode);
			lSqlQuery.setLong("lPercent", 100);
			
			lLstLifeCertificate = lSqlQuery.list();
		
			lIntCount = Integer.parseInt(lLstLifeCertificate.get(0).toString());
			
		} catch (Exception e) {
			gLogger.error(" Error is :"+ e, e);
			throw e;
		}
		return lIntCount;	
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getReceivedLifeCertificateList(java.lang.String, java.lang.String)
	 */
	public List getReceivedLifeCertificateList(Map displayTag,String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		List lLstLifeCertificate =  new ArrayList();
		try {
			Query lSqlQuery = null;
			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();
			
			String[] columnValues = new String[]{"", "tpr.ppoNo", "mph.firstName", "tpr.ledger_no", "mbk.bankName","rlt.branchName", "mpd.accountNo","tps.seenDate" ,"tps.seen_flage"};
			String lStrColumnValues = null;
			
			lStrQuery.append("SELECT \n");
			lStrColumnValues = "mph.pensioner_code," + "tpr.ppo_No," + "mph.first_name," + "mpf.name," + "tpr.ledger_no,"
			                 + "tpr.page_no,"+ "mph.alive_flag,"+ "mbk.bank_Name,"+ "rlt.branch_Name," + "mpd.acount_no," + "tps.seen_Date," +"tps.seen_flage";
			
			lStrQuery.append(lStrColumnValues + "\n");
			
			lStrQuery.append(" FROM Trn_Pension_Rqst_Hdr tpr,Mst_Pensioner_Dtls mpd, \n");
			lStrQuery.append(" Mst_Bank mbk, Rlt_Bank_Branch rlt, Trn_Pensioner_Seen_Dtls tps,\n");
			lStrQuery.append(" Mst_Pensioner_Hdr mph LEFT OUTER JOIN Mst_Pensioner_Family_Dtls mpf ON  mph.pensioner_Code = mpf.pensioner_Code AND  mpf.percentage=:lPercent \n");
			lStrQuery.append(" WHERE mph.pensioner_Code = tpr.pensioner_Code AND mph.pensioner_Code = mpd.pensioner_Code \n");
			lStrQuery.append(" AND  mph.pensioner_Code=tps.pensioner_Code \n");
			lStrQuery.append(" AND tps.life_cert_status= :lifeCertStatus AND mpd.bank_Code=mbk.bank_Code  AND mpd.branch_Code=rlt.branch_Code AND tpr.status=:status \n");
			lStrQuery.append(" AND tpr.location_Code = :locationCode \n");
			
			
//			lStrQuery.append(" FROM Trn_Pension_Rqst_Hdr tpr,Mst_Pensioner_Dtls mpd, \n");
//			lStrQuery.append(" Mst_Pension_Headcode mphc, Mst_Bank mbk, Rlt_Bank_Branch rlt, Trn_Pensioner_Seen_Dtls tps, \n");
//			lStrQuery.append(" Mst_Pensioner_Hdr mph LEFT OUTER JOIN Mst_Pensioner_Family_Dtls mpf ON  mph.pensioner_Code = mpf.pensioner_Code AND  mpf.percentage=:lPercent \n");
//			lStrQuery.append(" WHERE mph.pensioner_Code = tpr.pensioner_Code AND mph.pensioner_Code = mpd.pensioner_Code \n");
//			lStrQuery.append(" AND  mph.pensioner_Code=tps.pensioner_Code AND  mphc.head_Code=tpr.head_Code AND tpr.seen_Flag='N' \n");
//			lStrQuery.append(" AND tps.seen_Flage='N' AND mpd.bank_Code=mbk.bank_Code  AND mpd.branch_Code=rlt.branch_Code AND tpr.status=:status \n");
//			lStrQuery.append(" AND tpr.location_Code = :locationCode  \n");
			lStrQuery.append(" ORDER BY ");
			
			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lStrQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lStrQuery.append(" mbk.bank_name,rlt.branch_name,mph.first_name ");
			}
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());
			
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lSqlQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lSqlQuery.setMaxResults(Constants.PAGE_SIZE);
			
			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setLong("lPercent", 100);
			lSqlQuery.setString("lifeCertStatus", gObjRsrcBndle.getString("LIFECERT.SAVED"));
			lSqlQuery.setString("locationCode", lStrLocCode);
			lLstResult = lSqlQuery.list();
			
			if (lLstResult != null && lLstResult.size() > 0) {
				Iterator it = lLstResult.iterator();
				Object[] tuple = null;
				Object[] tmpTuple = null;
				
				// ---For db2 result array has one more element of row id.So
				// removing that extra element and changing index of array
				// accordingly for db2.
				int actualLength = (lStrColumnValues != null) ? lStrColumnValues.split(",").length : 0;
				int resultLength = ((Object[]) lLstResult.get(0)).length;
				boolean isDb2 = false;
				if (actualLength != resultLength) {
					isDb2 = true;
				}
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					if (isDb2) {
						tmpTuple = tuple;
						tuple = new Object[resultLength - 1];
						for (int cnt = 0; cnt < (resultLength - 1); cnt++) {
							tuple[cnt] = tmpTuple[cnt + 1];
						}
						
					}
					lLstLifeCertificate.add(tuple);
				}
			}
		
		} catch (Exception e) {
			gLogger.error("Error is :"+ e, e);
			throw e;
		}
		return lLstLifeCertificate;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#updatePensionerSeenDtls(java.util.List)
	 */
	public void updatePensionerSeenDtls(List<String> lLstPensionerCode,Long gLngPostId,Long gLngUserId,Date gDate, Long lLngFinYearId)
			throws Exception {
		StringBuffer lSBQuery = new StringBuffer();
		Session hiSession = getSession();
		try
		{
			lSBQuery.append("UPDATE TrnPensionerSeenDtls SET updatedPostId=:updatedPostId,updatedUserId = :updatedUserId,updatedDate =:updatedDate, lifeCertStatus = :lifeCertStatus ");
			lSBQuery.append("  WHERE finYearId = :finYearId and lifeCertStatus = :currLifeCertStatus and pensionerCode IN(:pensionerCodeList) ");
			
			Query hqlQuery = hiSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameterList("pensionerCodeList", lLstPensionerCode);
			hqlQuery.setLong("updatedPostId", gLngPostId);
			hqlQuery.setLong("updatedUserId", gLngUserId);
			hqlQuery.setDate("updatedDate", gDate);
			hqlQuery.setString("currLifeCertStatus",gObjRsrcBndle.getString("LIFECERT.SAVED"));
			hqlQuery.setString("lifeCertStatus",gObjRsrcBndle.getString("LIFECERT.APPROVED"));
			hqlQuery.setLong("finYearId", lLngFinYearId);
			
			hqlQuery.executeUpdate();
		}
		catch(Exception e)
		{
			logger.error("Error is "+e,e);
			throw e;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getTrnPensionerSeenDtls(java.lang.String)
	 */
	public TrnPensionerSeenDtls getTrnPensionerSeenDtls(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnPensionerSeenDtls> lLstTrnPensionerSeenDtls = new ArrayList<TrnPensionerSeenDtls>();
		TrnPensionerSeenDtls lObjTrnPensionerSeenDtlsVO = new TrnPensionerSeenDtls();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionerSeenDtls.class);
			objCrt.add(Restrictions.like("pensionerCode", lStrPensionerCode));
			lLstTrnPensionerSeenDtls = objCrt.list();
			if (lLstTrnPensionerSeenDtls != null && !lLstTrnPensionerSeenDtls.isEmpty()) {
				lObjTrnPensionerSeenDtlsVO = lLstTrnPensionerSeenDtls.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lObjTrnPensionerSeenDtlsVO;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.LifeCertificateDAO#getTrnPensionArrearDtls(java.lang.String)
	 */
	public TrnPensionArrearDtls getTrnPensionArrearDtls(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnPensionArrearDtls> lLstTrnPensionArrearDtls = new ArrayList<TrnPensionArrearDtls>();
		TrnPensionArrearDtls lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionArrearDtls.class);
			objCrt.add(Restrictions.like("pensionerCode", lStrPensionerCode));
			objCrt.add(Restrictions.like("paidFlag", 'N'));
			lLstTrnPensionArrearDtls = objCrt.list();
			if (lLstTrnPensionArrearDtls != null && !lLstTrnPensionArrearDtls.isEmpty()) {
				lObjTrnPensionArrearDtlsVO = lLstTrnPensionArrearDtls.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lObjTrnPensionArrearDtlsVO;
	}
}












