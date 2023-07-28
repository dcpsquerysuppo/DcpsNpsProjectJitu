package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;


public class RevisedArrearCalcDAOImpl implements RevisedArrearCalcDAO {

	private SessionFactory sessionFactory = null;

	private static final Logger gLogger = Logger
	.getLogger(RevisedArrearCalcDAOImpl.class);
	
	public RevisedArrearCalcDAOImpl(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {

		boolean allowCreate = true;
		return SessionFactoryUtils.getSession(sessionFactory, allowCreate);
	}

	public String getMaxOfForMonth(String lStrPensionerCode) throws Exception {

		String lstrReturn = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT max(B.forMonth) FROM TrnPensionBillDtls A,TrnPensionBillHdr B WHERE A.trnPensionBillHdrId = B.trnPensionBillHdrId "
					+ " AND A.pensionerCode = :pensionerCode and B.billType in(:Pension,:Monthly)");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("Pension", "Pension");
			lQuery.setString("Monthly", "Monthly");

			List lLst = lQuery.list();

			if (!lLst.isEmpty()) {
				if (lLst.get(0) != null) {
					lstrReturn = (lLst.get(0)).toString();
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw (e);
		}
		return lstrReturn;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.RevisedArrearCalcDAO#getPensionerDtlsFromPPONo(java.lang.String)
	 */
	public List getPensionerDtlsFromPPONo(String lStrPPONo,String lStrBankCode,String lStrBranchCode,String lStrAccountNo,String lStrLocationCode) throws Exception {
		
		List lLstPensionerDtls = new ArrayList();
		
		Query lSqlQuery = null;
		try {
			
			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();
			

			lStrQuery.append("SELECT tpr.pension_request_id,mph.pensioner_code,tpr.ppo_no,tpr.linked_ppo_no,tpr.da_rate_for_state, \n");
			lStrQuery.append("mph.date_of_birth,mph.date_of_death, tpr.basic_pension_amount, \n");
			lStrQuery.append("tpr.commencement_date, tpr.fp1_date, tpr.fp2_date, tpr.fp1_amount, tpr.fp2_amount, tpr.cvp_monthly_amount, tpr.end_date, \n");
			lStrQuery.append("mph.date_of_retirement, mph.first_name, mpf.date_of_birth, mpf.date_of_death, mpf.percentage, mph.alive_Flag, mpf.name, \n");
			lStrQuery.append("mb.bank_name,rbb.branch_name, mpd.acount_no, tpr.re_employment_from_date, tpr.re_employment_to_date, tpr.da_in_pension_salary, \n");
			lStrQuery.append("tpr.ledger_no, tpr.page_no, clm.loc_name \n");
			lStrQuery.append("FROM trn_pension_rqst_hdr tpr ,mst_pensioner_dtls mpd, mst_bank mb, rlt_bank_branch rbb, cmn_location_mst clm, \n");
			lStrQuery.append("mst_pensioner_hdr mph LEFT OUTER JOIN mst_pensioner_family_dtls mpf ON mph.pensioner_code=mpf.pensioner_code AND  mpf.percentage =:lPercent \n");
			lStrQuery.append("WHERE mph.pensioner_Code=tpr.pensioner_Code AND mph.pensioner_code = mpd.pensioner_code \n");
			lStrQuery.append("AND mpd.bank_code=mb.bank_code AND mpd.branch_code=rbb.branch_code  \n");
			lStrQuery.append("AND mpd.location_code=rbb.location_code \n");
			lStrQuery.append("AND tpr.location_code=clm.location_code AND tpr.location_code=:locationCode \n");
			
			if(!"".equals(lStrPPONo))
				lStrQuery.append("AND tpr.ppo_No = :PPONo ");
			if(!"".equals(lStrBankCode))
				lStrQuery.append("AND mpd.bank_code = :bankCode ");
			if(!"".equals(lStrBranchCode))
				lStrQuery.append("AND mpd.branch_code = :branchCode ");
			if(!"".equals(lStrAccountNo))
				lStrQuery.append("AND mpd.acount_no = :accountNo ");
			
			
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());		
			lSqlQuery.setString("locationCode", lStrLocationCode);
			if(!"".equals(lStrPPONo))
			  lSqlQuery.setString("PPONo", lStrPPONo.toUpperCase());
			if(!"".equals(lStrBankCode))
				lSqlQuery.setString("bankCode", lStrBankCode);
			if(!"".equals(lStrBranchCode))
				lSqlQuery.setString("branchCode", lStrBranchCode);
			if(!"".equals(lStrAccountNo))
				lSqlQuery.setString("accountNo", lStrAccountNo);
			lSqlQuery.setLong("lPercent", 100);
			lLstPensionerDtls = lSqlQuery.list();
			
		} catch (Exception e) {
			gLogger.error(" Error is :"+ e, e);
			throw e;
		}
		return lLstPensionerDtls;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.RevisedArrearCalcDAO#getRateFromHeadcodeRateRlt(java.lang.Long, java.lang.String, java.lang.Double, java.util.Date)
	 */
	public List<RltPensionHeadcodeRate> getRateFromHeadcodeRateRlt(Long lLngHeadcode, String lStrFieldType, Double lDbPnsnBasic, Date lDtFromDate, Date lDtToDate) throws Exception {

        List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRate = new ArrayList<RltPensionHeadcodeRate>();
		
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
//			lSBQuery.append(" FROM RltPensionHeadcodeRate  WHERE fieldType = :lFieldType AND headCode = :lHeadcode \n" );
//			lSBQuery.append(" AND  ((effectiveFromDate BETWEEN :lStrForDate  AND :lDtToDate)  \n" );
//			lSBQuery.append(" AND (effectiveToDate >= :lStrForDate OR effectiveToDate IS NULL)) ");
			lSBQuery.append(" FROM RltPensionHeadcodeRate  WHERE fieldType = :lFieldType AND headCode = :lHeadcode \n");
			lSBQuery.append("  AND (((effectiveFromDate >= :lStrForDate  AND effectiveFromDate <=:lDtToDate) OR \n");
			lSBQuery.append("  (effectiveToDate >=:lStrForDate  AND effectiveToDate <=:lDtToDate) OR \n");
			lSBQuery.append(" ( effectiveFromDate <= :lStrForDate AND effectiveToDate >=:lDtToDate) \n");
			lSBQuery.append("  ) OR	(effectiveFromDate <= :lStrForDate AND effectiveToDate IS NULL)	) \n");
					
//			if (lStrFieldType.equalsIgnoreCase("DA_1986") && lDbPnsnBasic != 0) {
//				if (lDbPnsnBasic <= 1750) {
//					lSBQuery.append(" AND uptoBasic = 1750 ");
//				} else if (lDbPnsnBasic <= 3000) {
//					lSBQuery.append(" AND uptoBasic = 3000 ");
//				} else {
//					lSBQuery.append(" AND uptoBasic = 999999");
//				}
//			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lFieldType", lStrFieldType);
			lQuery.setBigDecimal("lHeadcode", new BigDecimal(lLngHeadcode));
			lQuery.setDate("lStrForDate", lDtFromDate);
			lQuery.setDate("lDtToDate", lDtToDate);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstRltPensionHeadcodeRate = lQuery.list();

			
		} catch (Exception e) {
			gLogger.error(" Error is :"+ e, e);
			throw e;
		}

		return lLstRltPensionHeadcodeRate;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.RevisedArrearCalcDAO#getDPRate(java.lang.String)
	 */
	public RltPensionHeadcodeRate getDPRate(String lStrFieldType) throws Exception {

		 List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRate = new ArrayList<RltPensionHeadcodeRate>();
		 RltPensionHeadcodeRate lObjRltPensionHeadCodeRate = null;
			
			try {
				Session ghibSession = getSession();
				StringBuffer lSBQuery = new StringBuffer();

				lSBQuery.append(" FROM RltPensionHeadcodeRate  WHERE fieldType = :lFieldType \n");
		
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());

				lQuery.setString("lFieldType", lStrFieldType);
				
				lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
				lLstRltPensionHeadcodeRate = lQuery.list();
				if(!lLstRltPensionHeadcodeRate.isEmpty())
					lObjRltPensionHeadCodeRate = lLstRltPensionHeadcodeRate.get(0);

				
			} catch (Exception e) {
				gLogger.error(" Error is :"+ e, e);
				throw e;
			}

			return lObjRltPensionHeadCodeRate;
	}

	public List<ComboValuesVO> getDaRateFromPayCommission(String lStrPayCommissionType) throws Exception {

		ArrayList<ComboValuesVO> lArrListDARate = new ArrayList<ComboValuesVO>();
		List<BigDecimal> lLstDARate = new ArrayList<BigDecimal>();
		StringBuilder lSBQuery = new StringBuilder();
		ComboValuesVO cmbVO = null;
		BigDecimal lBdDARate = null;
		lLstDARate.add(BigDecimal.ZERO);

		try {
			Session ghibSession = getSession();
			lSBQuery.append("SELECT rate FROM RltPensionHeadcodeRate");
			lSBQuery.append(" WHERE fieldType = :PayCommissionType Order by 1");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("PayCommissionType", lStrPayCommissionType);
			lLstDARate.addAll(lQuery.list());
			if (!lLstDARate.isEmpty()) {
				for (Integer lInt = 0; lInt <= lLstDARate.size(); lInt++) {
					if (lInt == 0) {
						cmbVO = new ComboValuesVO();
						cmbVO.setId("-1");
						cmbVO.setDesc("----Select----");
						lArrListDARate.add(cmbVO);
					} else {
						cmbVO = new ComboValuesVO();
						lBdDARate = lLstDARate.get(lInt - 1);
						cmbVO.setId(lLstDARate.get(lInt - 1).toString());
						cmbVO.setDesc(lLstDARate.get(lInt - 1).toString());
						lArrListDARate.add(cmbVO);
					}
				}
			}

		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		}
		return lArrListDARate;

	}
	
}
