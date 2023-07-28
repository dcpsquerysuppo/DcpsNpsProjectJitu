/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 21, 2012		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.common.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.valueobject.MstIntegrationBillTypes;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Aug 21, 2012
 */
public class TreasuryIntegrationDAOImpl extends GenericDaoHibernateImpl implements TreasuryIntegrationDAO {

	private Log logger = LogFactory.getLog(getClass());
	private Session ghibSession = null;
	private ResourceBundle pnsnBillBundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	public TreasuryIntegrationDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public Map<String, Long> getPensionBillSchemewiseRecoveryDtls(Long lLngBillNo,long lSubjectId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Map<String, Long> lMapDeductionDtls = null;
		String lStrRcrySchemeCode = null;
		long lLngRcryAmount = 0;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select mrd.schemeCode,cast(sum(mrd.amount) as big_decimal) \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnMonthlyPensionRecoveryDtls mrd \n");
			lSBQuery.append("where \n");
			lSBQuery.append("mrd.billNo = :lLngBillNo \n");
			lSBQuery.append("and mrd.recoveryFromFlag = :lRcryFromFlag \n");
			lSBQuery.append("group by mrd.schemeCode \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			//edited by aditya on 19 Dec 13 
			if(lSubjectId==44){
				lQuery.setString("lRcryFromFlag", pnsnBillBundleConst.getString("RECOVERY.MONTHLY"));
			}
			else{
				lQuery.setString("lRcryFromFlag", pnsnBillBundleConst.getString("RECOVERY.PENSION"));
			}
			//edited by aditya on 19 Dec 13 
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lMapDeductionDtls = new HashMap<String, Long>();
				for (Object[] lArrObj : lLstResult) {
					lStrRcrySchemeCode = (String) lArrObj[0];
					lLngRcryAmount = ((BigDecimal) lArrObj[1]).longValue();
					if (lStrRcrySchemeCode != null) {
						lMapDeductionDtls.put("RC" + lStrRcrySchemeCode, lLngRcryAmount);
					}
					lStrRcrySchemeCode = null;
					lLngRcryAmount = 0;
				}
			}
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lMapDeductionDtls;
	}

	public Map<String, Long> getDCRGBillSchemewiseRecoveryDtls(Long lLngBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Map<String, Long> lMapDeductionDtls = null;
		String lStrRcrySchemeCode = null;
		long lLngRcryAmount = 0;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select prd.schemeCode,cast(sum(prd.amount) as big_decimal) \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnPensionRecoveryDtls prd \n");
			lSBQuery.append("where \n");
			lSBQuery.append("prd.billNo = :lLngBillNo \n");
			lSBQuery.append("group by prd.schemeCode \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lMapDeductionDtls = new HashMap<String, Long>();
				for (Object[] lArrObj : lLstResult) {
					lStrRcrySchemeCode = (String) lArrObj[0];
					lLngRcryAmount = ((BigDecimal) lArrObj[1]).longValue();
					if (lStrRcrySchemeCode != null) {
						lMapDeductionDtls.put("RC" + lStrRcrySchemeCode, lLngRcryAmount);
					}
					lStrRcrySchemeCode = null;
					lLngRcryAmount = 0;
				}
			}
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lMapDeductionDtls;
	}

	// Added by Aditya on 17th December 2013 --Start

	public Map<String, Long> getMonthlyPensionBillBankwiseDtls(String lStrBillNo, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		StringBuilder lSBQuery1 = new StringBuilder();
		StringBuilder lSBQuery2 = new StringBuilder();
		//StringBuilder lSBQuery3 = new StringBuilder();
		List<Object[]> lLstResult = null;
		List<Object[]> lLstResult1 = null;
		List<Object[]> lLstResult2 = null;
		//List<Object[]> lLstResult3 = null;
		ghibSession = getSession();
		Map<String, Long> lMapMonthlyDtls = null;
		Integer orderbypara = null;
		String BankName = null;
		String lStrBankName = null;
		String lLngAmount = null;
		long netAmt=0;
		//edited by aditya
		String PensionName=null;
		String lStrPensionName=null;
		String BankCode=null;
		String PensionCode=null;
		long payeeNetAmt=0;
		try {
			lSBQuery.append("select tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,mb.bank_Name,sum(tpb.no_Of_Pnsr),cast(sum(tpb.gross_Amount) as BIGINT), \n");
			lSBQuery.append("cast(sum(tpb.deduction_A + tpb.deduction_B) as BIGINT),cast(sum(tpb.net_Amount) as BIGINT),ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month,mb.BANK_CODE \n");
			lSBQuery.append("from Trn_Bill_Register tbr,Trn_Pension_Bill_Hdr tpb,Mst_Bank mb,Mst_Scheme ms,Rlt_Bank_Branch rbb where \n");
			lSBQuery.append("tbr.bill_No = tpb.bill_No \n");
			lSBQuery.append("and tpb.bank_Code = mb.bank_Code \n");
			lSBQuery.append("and tpb.branch_Code = rbb.branch_Code \n");
			lSBQuery.append("and rbb.location_Code = tbr.location_Code \n");
			lSBQuery.append("and tbr.scheme_No = ms.scheme_Code \n");
			lSBQuery.append("and rbb.reporting_Branch_Code is null \n");
			lSBQuery.append("and rbb.reporting_Bank_Code is null \n");
			lSBQuery.append("and tbr.bill_No = :billNo\n");
			lSBQuery.append("and tbr.location_Code = :locationCode \n");
			lSBQuery.append("group by tpb.bank_Code,mb.bank_Name,tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month,mb.BANK_CODE  \n");
			lSBQuery.append(" Union All \n");
			lSBQuery.append("select tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,(mb.bank_Name || ',' || rbb2.branch_Name),sum(tpb.no_Of_Pnsr),cast(sum(tpb.gross_Amount) as BIGINT), \n");
			lSBQuery.append("cast(sum(tpb.deduction_A + tpb.deduction_B) as BIGINT),cast(sum(tpb.net_Amount) as BIGINT),ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month,mb.BANK_CODE \n");
			lSBQuery.append("from Trn_Bill_Register tbr,Trn_Pension_Bill_Hdr tpb,Mst_Bank mb,Mst_Scheme ms,Rlt_Bank_Branch rbb1,Rlt_Bank_Branch rbb2 where \n");
			lSBQuery.append("tbr.bill_No = tpb.bill_No \n");
			lSBQuery.append("and rbb1.branch_Code = tpb.branch_Code \n");
			lSBQuery.append("and rbb2.branch_Code = rbb1.reporting_Branch_Code \n");
			lSBQuery.append("and rbb1.reporting_Bank_Code = mb.bank_Code \n");
			lSBQuery.append("and rbb1.location_Code = rbb2.location_Code \n");
			lSBQuery.append("and rbb1.location_Code = tbr.location_Code \n");
			lSBQuery.append("and tbr.scheme_No = ms.scheme_Code \n");
			lSBQuery.append("and rbb1.reporting_Branch_Code is not null \n");
			lSBQuery.append("and rbb1.reporting_Bank_Code is not null \n");
			lSBQuery.append("and tbr.bill_No = :billNo \n");
			lSBQuery.append("and tbr.location_Code = :locationCode \n");
			lSBQuery.append("group by mb.bank_Code,mb.bank_Name,rbb2.branch_Name,tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month,mb.BANK_CODE \n");
			lSBQuery.append("Order By 5 \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lMapMonthlyDtls = new HashMap<String, Long>();
				for (Object[] lArrObj : lLstResult) {
					BankName = (String) lArrObj[4];
					BankCode=(String) lArrObj[18];
					logger.info("lStrBankName : "+BankName);
					//edited by aditya for dummy --start
					if(BankCode.equalsIgnoreCase("9910000500093")){


						lSBQuery1.append("select tpb.pensioner_Name,mpd.ACOUNT_NO,rbb.ifsc_Code,rbb.MICR_CODE, ");
						lSBQuery1.append(" cast(tpb.net_Amount as BIGINT),mp.PNSNR_EMAIL_ID,mp.TELE_NO,tbr.bill_No,tbr.scheme_No,tph.for_Month,");   
						lSBQuery1.append(" tpb.pensioner_Code,tph.NO_OF_PNSR ,mb.bank_Name,rbb.branch_Name,tpb.ppo_No  ");
						lSBQuery1.append(" from Trn_Bill_Register tbr,Trn_Pension_Bill_Hdr tph,Mst_Bank mb,Rlt_Bank_Branch rbb,Mst_Scheme ms,Trn_Pension_Bill_Dtls tpb ");
						lSBQuery1.append(" ,Mst_Pension_Headcode mph  ,MST_PENSIONER_HDR mp,MST_PENSIONER_DTLS mpd ");
						lSBQuery1.append(" where tbr.bill_No = tph.bill_No ");
						lSBQuery1.append(" and mp.PENSIONER_CODE=tpb.PENSIONER_CODE "); 
						lSBQuery1.append(" and mp.PENSIONER_CODE=mpd.PENSIONER_CODE ");
						lSBQuery1.append(" and tph.trn_Pension_Bill_Hdr_Id = tpb.trn_Pension_Bill_Hdr_Id ");  
						lSBQuery1.append(" and tph.bank_Code = mb.bank_Code ");
						lSBQuery1.append(" and tph.branch_Code = rbb.branch_Code ");
						lSBQuery1.append(" and tbr.scheme_No = ms.scheme_Code ");
						lSBQuery1.append(" and tpb.head_Code = mph.head_Code ");
						//lSBQuery.append(" and tbr.SUBJECT_ID= 44 ");
						lSBQuery1.append(" and tbr.BILL_NO= :billNo "); 
						lSBQuery1.append("and tbr.LOCATION_CODE= :locationCode ");	
						lSBQuery1.append("and mb.BANK_CODE= :bankCode ");	
						Query lQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());
						lQuery1.setString("locationCode", lStrLocCode);
						lQuery1.setLong("billNo", Long.parseLong(lStrBillNo));
						lQuery1.setString("bankCode", "9910000500093");
						lLstResult1 = lQuery1.list();
						if (lLstResult1 != null && lLstResult1.size() > 0) {
							for (Object[] lArrObj1 : lLstResult1) {
								PensionName = (String) lArrObj1[0];
								System.out.println("PensionName : "+PensionName);
								lStrPensionName= PensionName.replaceAll("\\W","_");
								System.out.println("PensionNameFinal : "+PensionName);
								lLngAmount =   lArrObj1[4].toString();
								netAmt=Long.parseLong(lLngAmount);
								if (lStrPensionName != null) {
									lMapMonthlyDtls.put(lStrPensionName, netAmt);
								}
							}
						}
					}
					else if(BankCode.equalsIgnoreCase("9910000500094")){


						lSBQuery2.append("select tpb.pensioner_Name,mpd.ACOUNT_NO,rbb.ifsc_Code,rbb.MICR_CODE, ");
						lSBQuery2.append(" cast(tpb.net_Amount as BIGINT),mp.PNSNR_EMAIL_ID,mp.TELE_NO,tbr.bill_No,tbr.scheme_No,tph.for_Month,");   
						lSBQuery2.append(" tpb.pensioner_Code,tph.NO_OF_PNSR ,mb.bank_Name,rbb.branch_Name,tpb.ppo_No  ");
						lSBQuery2.append(" from Trn_Bill_Register tbr,Trn_Pension_Bill_Hdr tph,Mst_Bank mb,Rlt_Bank_Branch rbb,Mst_Scheme ms,Trn_Pension_Bill_Dtls tpb ");
						lSBQuery2.append(" ,Mst_Pension_Headcode mph  ,MST_PENSIONER_HDR mp,MST_PENSIONER_DTLS mpd ");
						lSBQuery2.append(" where tbr.bill_No = tph.bill_No ");
						lSBQuery2.append(" and mp.PENSIONER_CODE=tpb.PENSIONER_CODE "); 
						lSBQuery2.append(" and mp.PENSIONER_CODE=mpd.PENSIONER_CODE ");
						lSBQuery2.append(" and tph.trn_Pension_Bill_Hdr_Id = tpb.trn_Pension_Bill_Hdr_Id ");  
						lSBQuery2.append(" and tph.bank_Code = mb.bank_Code ");
						lSBQuery2.append(" and tph.branch_Code = rbb.branch_Code ");
						lSBQuery2.append(" and tbr.scheme_No = ms.scheme_Code ");
						lSBQuery2.append(" and tpb.head_Code = mph.head_Code ");
						//lSBQuery.append(" and tbr.SUBJECT_ID= 44 ");
						lSBQuery2.append(" and tbr.BILL_NO= :billNo "); 
						lSBQuery2.append("and tbr.LOCATION_CODE= :locationCode ");	
						lSBQuery2.append("and mb.BANK_CODE= :bankCode ");	
						Query lQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
						lQuery2.setString("locationCode", lStrLocCode);
						lQuery2.setLong("billNo", Long.parseLong(lStrBillNo));
						lQuery2.setString("bankCode", "9910000500094");
						lLstResult2 = lQuery2.list();
						if (lLstResult2 != null && lLstResult2.size() > 0) {
							for (Object[] lArrObj2 : lLstResult2) {
								PensionName = (String) lArrObj2[0];
								PensionCode = (String) lArrObj2[10];
								System.out.println("PensionName : "+PensionName);
								lStrPensionName= PensionName.replaceAll("\\W","_");
								String PensionNameFinal=lStrPensionName;
								System.out.println("PensionNameFinal : "+PensionName);
								lLngAmount =   lArrObj2[4].toString();
								netAmt=Long.parseLong(lLngAmount);
								payeeNetAmt=netAmt;
								if(checkPayeeDtls(PensionCode)){

									StringBuilder lSBQuery3 = new StringBuilder();
									lSBQuery3.append("SELECT NAME,AMOUNT FROM MST_PAYEE_DTLS where PENSIONER_CODE= :PensionCode ");
									Query lQuery3 = ghibSession.createSQLQuery(lSBQuery3.toString());
									lQuery3.setParameter("PensionCode",PensionCode);
									List<Object[]> lLstResult3 = null;
									lLstResult3 = lQuery3.list();
									if (lLstResult3 != null && lLstResult3.size() > 0) {
										for (Object[] lArrObj3 : lLstResult3) {
											PensionName = (String) lArrObj3[0];
											System.out.println("PensionName : "+PensionName);
											lStrPensionName= PensionName.replaceAll("\\W","_");
											System.out.println("PensionNameFinal : "+PensionName);
											lLngAmount =   lArrObj3[1].toString();
											netAmt=Long.parseLong(lLngAmount);
											
											payeeNetAmt=payeeNetAmt-netAmt;
											
											
											if (lStrPensionName != null) {
												lMapMonthlyDtls.put(lStrPensionName, netAmt);
											}
										}
									}
								}
								lMapMonthlyDtls.put(PensionNameFinal, payeeNetAmt);
							}
						}
					}
					//edited by aditya for dummy --end
					else{
						lStrBankName= BankName.replaceAll("\\W","_");
						logger.info("lStrBankNameFinal : "+lStrBankName);
						lLngAmount =   lArrObj[8].toString();
						netAmt=Long.parseLong(lLngAmount);
						if (lStrBankName != null) {
							lMapMonthlyDtls.put(lStrBankName, netAmt);
						}
						lStrBankName = null;
						netAmt = 0;
					}
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapMonthlyDtls;
	}

	//edited by aditya for payee dtls
	public boolean checkPayeeDtls(String PensionCode) throws Exception{

		StringBuilder lSBQuery5 = new StringBuilder();
		List lLstResult = null;
		ghibSession = getSession();
		try {

			lSBQuery5.append("SELECT count(1) FROM MST_PAYEE_DTLS where PENSIONER_CODE= :PensionCode ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery5.toString());
			lQuery.setParameter("PensionCode",PensionCode);
			lLstResult=lQuery.list();
			if(lLstResult!=null && lLstResult.size()> 0)
				return true;



		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return false;


	}

	//edited by aditya for payee dtls
	
	public Map<String, Long> getPartyNameForCMP(String lStrBillNo, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		Map<String, Long> lMapMonthlyDtls = null;
		Integer orderbypara = null;
		String BankName = null;
		String lStrBankName = null;
		String lLngAmount = null;
		long netAmt=0;
		//edited by aditya
		String PensionName=null;
		String lStrPensionName=null;
		String BankCode=null;
		try {
			
			lSBQuery.append("select 'ATO' || ',' ||clm.LOC_NAME,cast(tbr.BILL_NET_AMOUNT as BIGINT) ");
			lSBQuery.append("from Trn_Bill_Register tbr ");
			lSBQuery.append("join CMN_LOCATION_MST clm on clm.LOCATION_CODE=tbr.LOCATION_CODE ");
			lSBQuery.append("and clm.DEPARTMENT_ID=100003 ");
			lSBQuery.append("and tbr.bill_No = :billNo ");
			lSBQuery.append("and tbr.location_Code = :locationCode ");
			//lSBQuery.append("group by clm.LOC_NAME");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lMapMonthlyDtls = new HashMap<String, Long>();
				for (Object[] lArrObj : lLstResult) {
					BankName = (String) lArrObj[0];
					//BankCode=(String) lArrObj[18];
					logger.info("lStrBankName : "+BankName);
					//edited by aditya for dummy --start
					/*if(BankCode.equalsIgnoreCase("100144")){


						lSBQuery.append("select tpb.pensioner_Name,mpd.ACOUNT_NO,rbb.ifsc_Code,rbb.MICR_CODE, ");
						lSBQuery.append(" tpb.net_Amount,mp.PNSNR_EMAIL_ID,mp.TELE_NO,tbr.bill_No,tbr.scheme_No,tph.for_Month,");   
						lSBQuery.append(" tpb.pensioner_Code,tph.NO_OF_PNSR ,mb.bank_Name,rbb.branch_Name,tpb.ppo_No  ");
						lSBQuery.append(" from Trn_Bill_Register tbr,Trn_Pension_Bill_Hdr tph,Mst_Bank mb,Rlt_Bank_Branch rbb,Mst_Scheme ms,Trn_Pension_Bill_Dtls tpb ");
						lSBQuery.append(" ,Mst_Pension_Headcode mph  ,MST_PENSIONER_HDR mp,MST_PENSIONER_DTLS mpd ");
						lSBQuery.append(" where tbr.bill_No = tph.bill_No ");
						lSBQuery.append(" and mp.PENSIONER_CODE=tpb.PENSIONER_CODE "); 
						lSBQuery.append(" and mp.PENSIONER_CODE=mpd.PENSIONER_CODE ");
						lSBQuery.append(" and tph.trn_Pension_Bill_Hdr_Id = tpb.trn_Pension_Bill_Hdr_Id ");  
						lSBQuery.append(" and tph.bank_Code = mb.bank_Code ");
						lSBQuery.append(" and tph.branch_Code = rbb.branch_Code ");
						lSBQuery.append(" and tbr.scheme_No = ms.scheme_Code ");
						lSBQuery.append(" and tpb.head_Code = mph.head_Code ");
						//lSBQuery.append(" and tbr.SUBJECT_ID= 44 ");
						lSBQuery.append(" and tbr.BILL_NO= :billNo "); 
						lSBQuery.append("and tbr.LOCATION_CODE= :locationCode ");		
						Query lQuery1 = ghibSession.createSQLQuery(lSBQuery.toString());
						lQuery1.setString("locationCode", lStrLocCode);
						lQuery1.setLong("billNo", Long.parseLong(lStrBillNo));
						lLstResult = lQuery.list();
						if (lLstResult != null && lLstResult.size() > 0) {
						PensionName = (String) lArrObj[0];
						System.out.println("PensionName : "+PensionName);
						lStrPensionName= PensionName.replaceAll("\\W","_");
						System.out.println("PensionNameFinal : "+PensionName);
						lLngAmount =   lArrObj[4].toString();
						netAmt=Long.parseLong(lLngAmount);
						if (lStrPensionName != null) {
							lMapMonthlyDtls.put(lStrPensionName, netAmt);
						}
					}
					}*/
					//edited by aditya for dummy --end
					//else{
					lStrBankName= BankName.replaceAll("\\W","_");
					logger.info("lStrBankNameFinal : "+lStrBankName);
					lLngAmount =   lArrObj[1].toString();
					netAmt=Long.parseLong(lLngAmount);
					if (lStrBankName != null) {
						lMapMonthlyDtls.put(lStrBankName, netAmt);
					}
					lStrBankName = null;
					netAmt = 0;
					//}
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapMonthlyDtls;
	}


	public Map<String, Long> getFirstPensionBillBankwiseDtls(String lStrBillNo,long lSubjectId) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Map<String, Long> lMapFirstPensionDtls = null;
		String PartyName = null;
		String lStrPartyName = null;
		String lLngAmount = null;
		long netAmt=0;
		ghibSession = getSession();
		try {

			/*lSBQuery.append("SELECT rbp.PARTY_NAME,mbb.BANK_NAME || '/' || rbb.BRANCH_NAME,tbr.BILL_NET_AMOUNT FROM RLT_BILL_PARTY rbp ");
			lSBQuery.append(" join TRN_BILL_REGISTER tbr on tbr.BILL_NO=rbp.BILL_NO ");
			lSBQuery.append(" join MST_BANK mbb on mbb.BANK_CODE=rbp.BANK_CODE ");
			lSBQuery.append(" join RLT_BANK_BRANCH rbb on rbb.BRANCH_CODE=rbp.BRANCH_CODE ");
			lSBQuery.append(" where tbr.SUBJECT_ID= :subjectId ");
			lSBQuery.append(" and tbr.BILL_NO= :billNo ");*/
			
			//edited by aditya on 26 may 14
			
			lSBQuery.append("SELECT rbp.PARTY_NAME,mbb.BANK_NAME || '/' || rbb.BRANCH_NAME,tbr.BILL_NET_AMOUNT FROM RLT_BILL_PARTY rbp ");
			lSBQuery.append("join TRN_BILL_REGISTER tbr on tbr.BILL_NO=rbp.BILL_NO ");
			if(lSubjectId==45){
				lSBQuery.append(" join TRN_PENSION_SUPPLY_BILL_DTLS tps on tbr.BILL_NO=tps.BILL_NO ");
				lSBQuery.append(" join MST_PENSIONER_DTLS mpd on tps.PENSIONER_CODE=mpd.PENSIONER_CODE ");
			}
			else{
			lSBQuery.append("join TRN_PENSION_RQST_HDR tpr on tpr.PPO_NO=tbr.PPO_NO ");
			lSBQuery.append("join MST_PENSIONER_DTLS mpd on tpr.PENSIONER_CODE=mpd.PENSIONER_CODE ");
			}
			lSBQuery.append("join MST_BANK mbb on mbb.BANK_CODE=mpd.BANK_CODE ");
			lSBQuery.append("join RLT_BANK_BRANCH rbb on rbb.BRANCH_CODE=mpd.BRANCH_CODE "); 
			lSBQuery.append(" where tbr.SUBJECT_ID= :subjectId ");
			lSBQuery.append(" and tbr.BILL_NO= :billNo ");
			
			//edited by aditya on 26 may 14
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("subjectId", lSubjectId);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lMapFirstPensionDtls = new HashMap<String, Long>();
				for (Object[] lArrObj : lLstResult) {
					PartyName = (String) lArrObj[0];
					logger.info("lStrPartyName : "+PartyName);
					lStrPartyName= PartyName.replaceAll("\\W","_");
					logger.info("lStrPartyNameFinal : "+lStrPartyName);
					if(lArrObj[2]!=null){
						lLngAmount =   lArrObj[2].toString();
						double amt=Double.parseDouble(lLngAmount);
						if(amt!=0.0)
							netAmt=(long) (amt);
					}
					if (lStrPartyName != null) {
						lMapFirstPensionDtls.put(lStrPartyName, netAmt);
					}
					lStrPartyName = null;
					netAmt = 0;
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapFirstPensionDtls;
	}


	public Map<String, Long> getSupplementaryPensionBillBankwiseDtls(String lStrBillNo,long lSubjectId) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Map<String, Long> lMapSupplyPensionDtls = null;
		String PartyName = null;
		String lStrPartyName = null;
		String lLngAmount = null;
		long netAmt=0;
		ghibSession = getSession();
		try {

			lSBQuery.append("SELECT tpb.PARTY_NAME,mbb.BANK_NAME || '/' || rbb.BRANCH_NAME,tpb.NET_AMOUNT FROM TRN_PENSION_SUPPLY_BILL_DTLS tpb ");
			lSBQuery.append(" join TRN_BILL_REGISTER tbr on tbr.BILL_NO=tpb.BILL_NO  ");
			lSBQuery.append(" join MST_BANK mbb on mbb.BANK_CODE=tpb.BANK_CODE  ");
			lSBQuery.append(" join RLT_BANK_BRANCH rbb on rbb.BRANCH_CODE=tpb.BRANCH_CODE  ");
			lSBQuery.append(" where tbr.SUBJECT_ID= :subjectId ");
			lSBQuery.append(" and tbr.BILL_NO= :billNo ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("subjectId", lSubjectId);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lMapSupplyPensionDtls = new HashMap<String, Long>();
				for (Object[] lArrObj : lLstResult) {
					PartyName = (String) lArrObj[0];
					logger.info("lStrSupplyPartyName : "+PartyName);
					lStrPartyName= PartyName.replaceAll("\\W","_");
					logger.info("lStrSupplyPartyNameFinal : "+lStrPartyName);
					if(lArrObj[2]!=null){
						lLngAmount =   lArrObj[2].toString();
						double amt=Double.parseDouble(lLngAmount);
						if(amt!=0.0)
							netAmt=(long) (amt);
					}
					if (lStrPartyName != null) {
						lMapSupplyPensionDtls.put(lStrPartyName, netAmt);
						logger.info("lMapSupplyPensionDtls "+lMapSupplyPensionDtls.toString());
					}
					lStrPartyName = null;
					netAmt = 0;
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapSupplyPensionDtls;
	}

	public String getPayMode(String lStrBillNo,String lStrLocCode) throws Exception{

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		String payMode=null;
		String flag=" ";
		try {

			ghibSession = getSession();
			//lSBQuery.append("SELECT PAY_MODE FROM TRN_BILL_REGISTER where LOCATION_CODE=:locID and CURR_BILL_STATUS=20 and BILL_NO=:billNo ");

			lSBQuery.append("SELECT PAYMENT_MODE FROM  RLT_BILL_PARTY where  BILL_NO=:billNo ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("billNo", lStrBillNo);
			//	lQuery.setParameter("locID", lStrLocCode);
			lLstResult = lQuery.list();
			if(lLstResult.size() > 0){
				if(lLstResult.get(0).toString()!=null)
				{
					payMode=lLstResult.get(0).toString();
					/*if(payMode.equalsIgnoreCase("ECS"))
						flag="Y";
					else
						flag="N";*/
				}
			}

		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return payMode;
	}


	public int getNoOfPensioner(String lStrBillNo) throws Exception {

		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		int count=0;

		try {
			ghibSession = getSession();
			lSBQuery.append("SELECT count(tpd.PENSIONER_CODE) FROM TRN_BILL_REGISTER tbr ");
			lSBQuery.append("join TRN_PENSION_BILL_HDR tph on tph.BILL_NO=tbr.BILL_NO ");
			lSBQuery.append("join TRN_PENSION_BILL_DTLS tpd on tpd.TRN_PENSION_BILL_HDR_ID=tph.TRN_PENSION_BILL_HDR_ID ");
			lSBQuery.append("where tbr.BILL_NO=:lStrBillNo ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lStrBillNo", lStrBillNo.trim());
			lLstResult = lQuery.list();
			if(lLstResult!=null)
				count=Integer.parseInt(lLstResult.get(0).toString());

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return count;
	}

	//added by aditya for Check in Reverse Rejection for Voucher

	public String getVoucherFlagForReject(long lStrBillNo) throws Exception {

		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		String flag="false";
		Integer voucherNo=null;

		try {
			ghibSession = getSession();
			lSBQuery.append("SELECT VOUCHER_NO,VOUCHER_DATE FROM TRN_BILL_REGISTER where BILL_NO =:lStrBillNo ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lStrBillNo", lStrBillNo);
			lLstResult = lQuery.list();
			if(lLstResult!=null)
			{
				for (Object[] lArrObj : lLstResult) {
					voucherNo=(Integer) lArrObj[0];
					if(voucherNo!=null)
						flag="true";
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return flag;
	}
	//added by aditya for Check in Reverse Rejection for Voucher


	public String getCMPBeamsFlag(String locId) throws Exception {

		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		String flag="false";
		

		try {
			ghibSession = getSession();
			lSBQuery.append("SELECT * FROM CMP_BEAMS_ACTIVATE where LOCATION_CODE=:locId and ACTIVATE_FLAG=1 ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locId", locId);
			lLstResult = lQuery.list();
			if(lLstResult!=null && lLstResult.size()> 0)
			{
				flag="true";
			}
			

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return flag;
	}
	
	// Added by Aditya on 17th December 2013 --End

	public Integer getBeneficiaryCountOfBill(Long lLngBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		Integer lIntBeneficiaryCount = 0;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select count(1) \n");
			lSBQuery.append("from \n");
			lSBQuery.append("trn_bill_register tbr, \n");
			lSBQuery.append("rlt_bill_party rbp \n");
			lSBQuery.append("where \n");
			lSBQuery.append("tbr.bill_no = rbp.bill_no \n");
			lSBQuery.append("and tbr.bill_no = :lLngBillNo \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lIntBeneficiaryCount = ((Integer) lLstResult.get(0));
			}
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lIntBeneficiaryCount;
	}

	/**
	 * 
	 * <H3>This method is used to get BEAMS authorization details from db for
	 * displaying on worklist. -</H3>
	 * 
	 * 
	 * 
	 * @author Shripal Soni
	 * @param lLstBillNo
	 * @param lMapBeamsAuthDtls
	 * @return
	 * @throws Exception
	 */
	public Map<Long, Object[]> getBeamsAuthDtls(List<Long> lLstBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Integer lIntBeneficiaryCount = 0;
		Map<Long, Object[]> lMapBeamsAuthDtls = new HashMap<Long, Object[]>();
		Long lLngBillNo = null;
		String lStrAuthNo = null;
		String lStrBillValidStatus = null;
		String lStrBeamsBillStatus = null;
		String lStrBeamsStatusCode = null;
		Object[] lArrAuthDtls = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select integ.bill_no,integ.auth_no,integ.bill_valid_status,integ.beams_bill_status,integ.status_code \n");
			lSBQuery.append("from \n");
			lSBQuery.append("trn_ifms_beams_integration integ \n");
			lSBQuery.append("where \n");
			lSBQuery.append("integ.bill_no in (:lLstBillNo) \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstBillNo", lLstBillNo);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lLngBillNo = ((BigInteger) lArrObj[0]).longValue();
					lStrAuthNo = (String) lArrObj[1];
					lStrBillValidStatus = (String) lArrObj[2];
					lStrBeamsBillStatus = (String) lArrObj[3];
					lStrBeamsStatusCode = (String) lArrObj[4];
					lArrAuthDtls = new Object[4];
					lArrAuthDtls[0] = lStrAuthNo;
					lArrAuthDtls[1] = lStrBillValidStatus;
					lArrAuthDtls[2] = lStrBeamsBillStatus;
					lArrAuthDtls[3] = lStrBeamsStatusCode;
					if (lLngBillNo != null) {
						lMapBeamsAuthDtls.put(lLngBillNo, lArrAuthDtls);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lMapBeamsAuthDtls;
	}

	/**
	 * 
	 * <H3>This method is used to check whether the given bill no. and
	 * authorization no. exists in db or not. -</H3>
	 * 
	 * 
	 * 
	 * @author Shripal Soni
	 * @param lLngBillNo
	 * @param lStrAuthNo
	 * @return
	 * @throws Exception
	 */




	// Commented by Mubeen on 07th October 2013 - Start
	/**
	public Object[] validateBEAMSBillAuthNo(Long lLngBillNo) throws Exception {

		logger.info("inside dao ::::");
		Object[] lArrObjDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select integ.bill_no,integ.auth_no,integ.beams_bill_status \n");
			lSBQuery.append("from \n");
			lSBQuery.append("trn_ifms_beams_integration integ \n");
			lSBQuery.append("where \n");
			lSBQuery.append("integ.bill_no = :lLngBillNo \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lArrObjDtls = lLstResult.get(0);
			}
			logger.info("object arr is :" + lArrObjDtls);
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lArrObjDtls;
	}
	 **/
	// Commented by Mubeen on 07th October 2013 - End

	public Object[] validateBEAMSBillAuthNo(Long lLngBillNo,String authNo) throws Exception {

		logger.info("inside dao ::::");
		Object[] lArrObjDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select integ.bill_no,integ.auth_no,integ.beams_bill_status \n");
			lSBQuery.append("from \n");
			lSBQuery.append("trn_ifms_beams_integration integ \n");
			lSBQuery.append("where \n");
			lSBQuery.append("integ.PAYBILL_ID = :lLngBillNo \n and AUTH_NO=:authNo");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lQuery.setString("authNo", authNo);
			logger.info("lQuery=" + lArrObjDtls+" "+lLngBillNo+" "+authNo);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lArrObjDtls = lLstResult.get(0);
			}
			logger.info("object arr is :" + lArrObjDtls);
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lArrObjDtls;
	}








	public List<Object[]> getPensionBillSummaryForCMP(Long lLngBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select tbr.BILL_NO,pbh.SCHEME_CODE,msc.SCHEME_NAME,to_char(to_date(pbh.FOR_MONTH,'YYYYmm'),'MON-YYYY'),count(1),tbr.BILL_GROSS_AMOUNT,tbr.BILL_NET_AMOUNT \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("join TRN_PENSION_BILL_HDR pbh on tbr.bill_no = pbh.bill_no \n");
			lSBQuery.append("join TRN_PENSION_BILL_DTLS pbd on pbh.TRN_PENSION_BILL_HDR_ID = pbd.TRN_PENSION_BILL_HDR_ID \n");
			lSBQuery.append("join MST_SCHEME msc on msc.SCHEME_CODE = pbh.SCHEME_CODE \n");
			lSBQuery.append("where \n");
			lSBQuery.append("tbr.bill_no = : lLngBillNo \n");
			lSBQuery.append("group by tbr.BILL_NO,pbh.SCHEME_CODE,msc.SCHEME_NAME,pbh.FOR_MONTH,tbr.BILL_GROSS_AMOUNT,tbr.BILL_NET_AMOUNT \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lLstResult;
	}

	public List<Object[]> getMonthlySuppBillBeneficiaryDtls(Long lLngBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select pbd.PENSIONER_NAME,pbd.ACCOUNT_NO,pbd.GROSS_AMOUNT,pbd.NET_AMOUNT \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("join TRN_PENSION_BILL_HDR pbh on tbr.bill_no = pbh.bill_no \n");
			lSBQuery.append("join TRN_PENSION_BILL_DTLS pbd on pbh.TRN_PENSION_BILL_HDR_ID = pbd.TRN_PENSION_BILL_HDR_ID \n");
			lSBQuery.append("where \n");
			lSBQuery.append("tbr.bill_no = : lLngBillNo \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lLstResult;
	}

	public List<Object[]> getFirstPayBillBeneficiaryDtls(Long lLngBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select pbd.PENSIONER_NAME,rbp.ACCNT_NO,pbd.GROSS_AMOUNT,pbd.NET_AMOUNT \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("join TRN_PENSION_BILL_HDR pbh on tbr.bill_no = pbh.bill_no \n");
			lSBQuery.append("join TRN_PENSION_BILL_DTLS pbd on pbh.TRN_PENSION_BILL_HDR_ID = pbd.TRN_PENSION_BILL_HDR_ID \n");
			lSBQuery.append("join RLT_BILL_PARTY rbp on rbp.bill_no = tbr.bill_no \n");
			lSBQuery.append("where \n");
			lSBQuery.append("tbr.bill_no = : lLngBillNo \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lLstResult;
	}

	public MstIntegrationBillTypes getBEAMSBillTypeDtls(String lStrBillType,String lStrDtlsHead) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		MstIntegrationBillTypes lObjMstIntegrationBillTypes = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select mibt \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstIntegrationBillTypes mibt \n");
			lSBQuery.append("where \n");
			lSBQuery.append("mibt.billType = :lStrBillType \n");
			lSBQuery.append("and mibt.detailHead = :lStrDtlsHead \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lStrBillType", lStrBillType);
			lQuery.setString("lStrDtlsHead", lStrDtlsHead);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lObjMstIntegrationBillTypes = ((MstIntegrationBillTypes) lLstResult.get(0));
			}
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lObjMstIntegrationBillTypes;
	}

	public boolean isValidBillType(String lStrDtlsHead,String lStrBillType) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		boolean isValidBillType = false;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select mibt.billType \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstIntegrationBillTypes mibt \n");
			lSBQuery.append("where \n");
			lSBQuery.append("mibt.detailHead = :lStrDtlsHead \n");
			lSBQuery.append("and mibt.billType = :lStrBillType \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lStrDtlsHead", lStrDtlsHead);
			lQuery.setString("lStrBillType", lStrBillType);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				if(lLstResult.get(0) != null)
				{
					isValidBillType = true;
				}
			}
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return isValidBillType;
	}

	public List<String> getListOfAuthNumberOfBEAMS(List<String> lLstAuthNumber) throws Exception
	{
		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select ibi.authNo \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TrnIfmsBeamsIntegration ibi  \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("ibi.authNo in (:lLstAuthNo)  \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstAuthNo", lLstAuthNumber);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lLstResult;
	}

	public List<String> getListOfAuthNoWithVouchDtlsAvailable(List<String> lLstAuthNumber) throws Exception
	{
		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery.append("Select iai.authNo \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TrnIfmsArthwahiniIntegration iai  \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("iai.authNo in (:lLstAuthNo)  \n");
			lSBQuery.append("and iai.delvSuccessStatus = :lStrDelvSuccessStatus  \n");
			lSBQuery.append("and iai.voucherNo is not null and iai.voucherDate is not null  \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstAuthNo", lLstAuthNumber);
			lQuery.setString("lStrDelvSuccessStatus", "Y");
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return lLstResult;
	}


	public List<Object[]> getBillForSchemePaymode() throws Exception{


		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;


		System.out.println(" Inside getBillForSchemePaymode method of DAOImpl");

		try{
			ghibSession = getSession();

			System.out.println("0D");
			lSBQuery.append("(select  TPCF.SCHEME_CODE as \"SCHEME\",'ECS' as pay_mode \n");
			lSBQuery.append("from trn_pension_change_hdr TPCF \n"); 
			lSBQuery.append("join trn_monthly_change_rqst TMCR on TPCF.CHANGE_RQST_ID=TMCR.CHANGE_RQST_ID \n");
			lSBQuery.append("join RLT_BANK_BRANCH rbb on rbb.BRANCH_CODE = tpcf.BRANCH_CODE and rbb.ECS_BRANCH = 'Y' \n");
			lSBQuery.append("where TPCF.FOR_MONTH='201210' and TPCF.LOCATION_CODE='7101' and TMCR.status='Approved' \n");
			lSBQuery.append("group by TPCF.SCHEME_CODE,pay_mode \n");
			lSBQuery.append("union \n");
			lSBQuery.append("select  TPCF.SCHEME_CODE as \"SCHEME\",'Cheque' as pay_mode \n");
			lSBQuery.append("from trn_pension_change_hdr TPCF \n"); 
			lSBQuery.append("join trn_monthly_change_rqst TMCR on TPCF.CHANGE_RQST_ID=TMCR.CHANGE_RQST_ID \n");
			lSBQuery.append("join RLT_BANK_BRANCH rbb on rbb.BRANCH_CODE = tpcf.BRANCH_CODE and rbb.ECS_BRANCH = 'N' \n");
			lSBQuery.append("where TPCF.FOR_MONTH='201210' and TPCF.LOCATION_CODE='7101' and TMCR.status='Approved' \n");
			lSBQuery.append("group by TPCF.SCHEME_CODE,pay_mode) \n");
			lSBQuery.append("minus \n");
			lSBQuery.append("(select  SCHEME_NO as \"SCHEME\",PAY_MODE from TRN_BILL_REGISTER \n");
			lSBQuery.append("where FOR_MONTH='201210' and SUBJECT_ID='44' and PAY_MODE in ('Cheque','ECS') and LOCATION_CODE='7101' and curr_bill_status not in (-1,-15,60,5,10) \n");
			lSBQuery.append("group by scheme_no,pay_mode) \n");
			System.out.println("1D");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			System.out.println("2D");
			lLstResult = lQuery.list();
			System.out.println("3D");
		}catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}

		return lLstResult;	
	}



	// Mubeen Added on 04 Dec 2012 Start

	public List<BigInteger> getListOfBillsToReject(Integer lForMonth,String lLocationCode) throws Exception{
		StringBuilder lSBQuery = new StringBuilder();
		List<BigInteger> lLstResult = null;


		try{
			ghibSession = getSession();

			lSBQuery.append("select tbr.bill_no from TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("where \n");
			lSBQuery.append("tbr.location_code = :lLocationCode \n");
			lSBQuery.append("and tbr.SUBJECT_ID = 44 \n");
			lSBQuery.append("and tbr.FOR_MONTH = :lForMonth \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS not in (-1,-15,60) \n");
			lSBQuery.append("minus \n");
			lSBQuery.append("select tbr.bill_no From TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("join TRN_IFMS_BEAMS_INTEGRATION int on tbr.bill_no = int.bill_no and int.bill_valid_status = 'Y' and int.auth_slip is not null \n");
			lSBQuery.append("and tbr.location_code = :lLocationCode \n");
			lSBQuery.append("and tbr.SUBJECT_ID = 44 \n");
			lSBQuery.append("and tbr.FOR_MONTH = :lForMonth \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS not in (-1,-15,60) \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lLocationCode", lLocationCode);
			lQuery.setInteger("lForMonth", lForMonth);		



			lLstResult = lQuery.list();
			System.out.println("getListOfBillsToReject: 16");


		}catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}

		return lLstResult;	
	}

	// Mubeen Added on 04 Dec 2012 Start





	// Mubeen Added on 06 Dec 2012 Start
	// Fetches list of Bills for Reverse BEAMS Rejection validation

	public List<BigInteger> getListOfBillsForBEAMSReverseReject(Integer lForMonth,String lLocationCode) throws Exception{

		StringBuilder lSBQuery=new StringBuilder();
		List<BigInteger> lLstResult=null;

		try{
			ghibSession = getSession();
			System.out.println("*** Inside getListOfBillsForBEAMSReverseReject method ***");
			lSBQuery.append("select tbr.bill_no from TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("join TRN_IFMS_BEAMS_INTEGRATION int on tbr.bill_no = int.bill_no and int.auth_slip is not null \n");
			lSBQuery.append("where tbr.location_code = :lLocationCode \n");
			lSBQuery.append("and tbr.SUBJECT_ID = 44 \n");
			lSBQuery.append("and tbr.FOR_MONTH = :lForMonth \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS not in (-1,-15,60,5,10) \n");
			lSBQuery.append("minus \n");
			lSBQuery.append("select tbr.bill_no From TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("join TRN_IFMS_BEAMS_INTEGRATION int on tbr.bill_no = int.bill_no and int.beams_bill_status = 0  and int.auth_slip is not null \n");
			lSBQuery.append("where tbr.location_code = :lLocationCode \n");
			lSBQuery.append("and tbr.SUBJECT_ID = 44 \n");
			lSBQuery.append("and tbr.FOR_MONTH = :lForMonth \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS not in (-1,-15,60,5,10) \n");
			System.out.println("lQuery: "+lSBQuery);

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			System.out.println("After Query"+lQuery);
			lQuery.setString("lLocationCode", lLocationCode);
			System.out.println("After Setting param 1");
			lQuery.setInteger("lForMonth", lForMonth);		
			System.out.println("After Setting param 2");
			lLstResult = lQuery.list();
			System.out.println("Resultset "+lLstResult);

		}catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}


		return lLstResult;	
	}

	// Mubeen Added on 06 Dec 2012 End


	// Added by Shailesh on 31st October 2013 - Start

	//Added by vivek
	public int rejectSevaarthPaybill(Long lLngBillNo,String authNo) throws Exception {

		logger.info("inside dao ::::");
		Object[] lArrObjDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		Integer result=null;
		try {
			ghibSession = getSession();
			lSBQuery.append(" update PAYBILL_HEAD_MPG set APPROVE_FLAG=4,UPDATED_DATE=SYSDATE where AUTH_NO='"+authNo+"' and PAYBILL_ID="+lLngBillNo+" and  APPROVE_FLAG=5 ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			result = lQuery.executeUpdate();
			logger.info("result is :" + result);
		} catch (Exception e) {
			logger.error("Exception is e :" + e);
			throw e;
		}
		return result;
	}

	// Added by Shailesh on 31st October 2013 - End




}
