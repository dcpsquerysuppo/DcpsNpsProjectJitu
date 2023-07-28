package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.valueobject.BillEdpVO;
import com.tcs.sgv.pensionpay.constants.PensionConstants;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;

public class BudgetDtlsDAOImpl implements BudgetDtlsDAO
{

	/* Global Variable for Logger Class */
	private static final Log logger = LogFactory.getLog(BudgetDtlsDAOImpl.class);

	private SessionFactory sessionFactory = null;

	public BudgetDtlsDAOImpl(SessionFactory sessionFactory)
	{

		this.sessionFactory = sessionFactory;
	}

	public Session getSession()
	{

		boolean allowCreate = true;
		return SessionFactoryUtils.getSession(sessionFactory, allowCreate);
	}

	/**
	 * get {@link TrnPensionRqstHdr} VO Written By Sagar
	 */
	public List getTrnPensionRqstHdrDtls(String lStrPPONO, String lStrStatus,String lStrLocCode) throws Exception
	{
		Iterator it = null;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();

		try
		{
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer(100);

			lSBQuery.append(" Select A.pensionerCode,A.headCode,A.commensionDate,A.endDate FROM TrnPensionRqstHdr A WHERE A.ppoNo = :lppoNo AND A.status = :lStatus AND A.locationCode = :locationCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lppoNo", lStrPPONO);
			lQuery.setString("lStatus", lStrStatus);
			lQuery.setString("locationCode", lStrLocCode);

			List resultList = lQuery.list();

			if (!resultList.isEmpty())
			{
				it = resultList.iterator();
				while (it.hasNext())
				{
					tuple = (Object[]) it.next();
					lLstDtlsList.add(tuple[0]);
					lLstDtlsList.add(tuple[1]);
					lLstDtlsList.add(tuple[2]);
					lLstDtlsList.add(tuple[3]);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error in getTrnPensionRqstHdrDtls. Error is : " + e, e);
			throw (e);
		}

		return lLstDtlsList;
	}

	/**
	 * get {@link MstPensionerHdrDtls} VO Written By Sagar
	 */

	public RltPensionHeadcodeChargable getMstPensionHeadcodeDtls(String lStrHeadCode, String lStrBillType) throws Exception
	{

		RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargable = new RltPensionHeadcodeChargable();

		try
		{
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer(150);

			lSBQuery.append(" FROM RltPensionHeadcodeChargable H WHERE H.headCode = :lHeadCode AND H.billType = :lBillType  AND H.activeFlag =:activeFlag");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lHeadCode", Long.valueOf(lStrHeadCode));
			lQuery.setString("lBillType", lStrBillType);
			lQuery.setString("activeFlag", "Y");
			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty())
			{
				lObjRltPensionHeadcodeChargable = (RltPensionHeadcodeChargable) lLstVO.get(0);
			}
		}
		catch(Exception e)
		{
			logger.error("Error in getMstPensionHeadcodeDtls.class BudgetDtlsDAOImpl Error is : " + e, e);
			throw (e);
		}

		return lObjRltPensionHeadcodeChargable;
	}

	/**
	 * Method to get all Edp details.
	 * @param lStrEdpCode
	 * @return List
	 */
	public List getExpEdpDtl(String lStrEdpCode,Long lLangId, Integer lFinYr) throws Exception
	{

		List<BillEdpVO> dataList = new ArrayList<BillEdpVO>();
		try
		{
			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer(250);
			hqlQuery.append("select me.edpCode, me.edpDesc, me.budobjhdCode,me.receiptEdp, me.budmjrhdCode,me.budsubmjrhdCode, " +
					        " me.budminhdCode,me.budsubhdCode,me.buddtlhdCode,me.budobjhdCode from MstEdp me where me.edpCode = :lEdpCode " +
					        " and activateFlag = '1' and langId = :Lang and finYearId = :FinYr ");

			Query lQuery = ghibSession.createQuery(hqlQuery.toString());

			lQuery.setString("lEdpCode", lStrEdpCode);
			lQuery.setLong("Lang", lLangId);
			lQuery.setInteger("FinYr", lFinYr);
			
			List resList = lQuery.list();
			if (resList != null)
			{
				Object[] tuple = null;
				BillEdpVO billEdpVO = null;
				Iterator it = resList.iterator();
				while (it.hasNext())
				{
					tuple = (Object[]) it.next();
					billEdpVO = new BillEdpVO();
					billEdpVO.setEdpCode((String) tuple[0]);
					billEdpVO.setEdpDesc((String) tuple[1]);
					billEdpVO.setObjHdCode((String) tuple[2]);
					billEdpVO.setReceiptEdp((String) tuple[3]);
					billEdpVO.setBudmjrHd((String) tuple[4]);
					billEdpVO.setBudsubmjrHd((String) tuple[5]);
					billEdpVO.setBudminHd((String) tuple[6]);
					billEdpVO.setBudsubHd((String) tuple[7]);
					billEdpVO.setBuddtlHd((String) tuple[8]);
					billEdpVO.setBudobjHd((String) tuple[9]);
					billEdpVO.setEdpAmt(BigDecimal.ZERO);
					dataList.add(billEdpVO);
				}
			}

		}
		catch(Exception e)
		{
			logger.error("Error in getExpEdpDtl.class BudgetDtlsDAOImpl Error is : " + e, e);
			throw (e);
		}
		return dataList;
	}

	/**
	 * Method to get Receipt Object head details By Bill Type.
	 * @param lStrPnsnerCode
	 * @param lStrBillType
	 * @return List
	 */
	public List getRcptEdpDtlByBillType(String lStrPnsnerCode, String lStrBillType) throws Exception
	{

		List dataList = new ArrayList();
		BigDecimal lBDZero = new BigDecimal("0.00");
		try
		{
			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer(300);

			if (lStrBillType != null && "OMR".equalsIgnoreCase(lStrBillType))
			{
				hqlQuery.append(" select pr.edpCode, pr.deductionType, pr.mjrhdCode,pr.submjrhdCode, pr.minhdCode,  " +
						" pr.subhdCode, sum(pr.amount), pr.dateOfReceipt from TrnPensionRecoveryDtls pr where " +
						" pr.pensionerCode = :lPnsnerCode And pr.recoveryFromFlag = :lBillType  " +
						" group by pr.edpCode,pr.deductionType,pr.mjrhdCode,pr.submjrhdCode, pr.minhdCode, pr.subhdCode,pr.dateOfReceipt " +
						" order by pr.edpCode ");
			}
			else
			{
				hqlQuery.append(" select pr.edpCode, pr.deductionType, pr.mjrhdCode,pr.submjrhdCode, pr.minhdCode, pr.subhdCode, " +
						" pr.amount, pr.dateOfReceipt from TrnPensionRecoveryDtls pr where pr.pensionerCode = :lPnsnerCode And " +
						" pr.recoveryFromFlag = :lBillType and pr.billNo is null order by pr.edpCode ");
			}
			Query lQuery = ghibSession.createQuery(hqlQuery.toString());
			lQuery.setString("lPnsnerCode", lStrPnsnerCode);
			lQuery.setString("lBillType", lStrBillType.toUpperCase());
			List resList = lQuery.list();
			if (resList != null)
			{
				Iterator it = resList.iterator();
				Object[] tuple = null;
				BillEdpVO billEdpVO = null;
				while (it.hasNext())
				{
					tuple = (Object[]) it.next();
					billEdpVO = new BillEdpVO();
					if (tuple[7] == null)
					{
						billEdpVO.setEdpCode((String) tuple[0]);
						billEdpVO.setEdpCategory((String) tuple[1]);
						billEdpVO.setBudmjrHd((String) tuple[2]);
						billEdpVO.setBudsubmjrHd((String) tuple[3]);
						billEdpVO.setBudminHd((String) tuple[4]);
						billEdpVO.setBudsubHd((String) tuple[5]);
						if (tuple[6].toString() != null)
						{
							billEdpVO.setEdpAmt(new BigDecimal(tuple[6].toString()));
						}
						else
						{
							billEdpVO.setEdpAmt(lBDZero);
						}
					}
					dataList.add(billEdpVO);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error in getRcptEdpDtlByBillType. class BudgetDtlsDAOImpl Error is : " + e, e);
			throw (e);
		}

		return dataList;
	}

	public List getRcptEdpDtlForMnthly(String lForMonth, String lStrTypeFlag, List lPnsnrCodeLst) throws Exception
	{

		List dataList = new ArrayList();
		BigDecimal lBDZero = new BigDecimal("0.00");
		try
		{

			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer(400);

			hqlQuery.append(" select pr.edp_Code edpCode,pr.deduction_Type dedType, pr.mjrhd_Code mjrHdCd, pr.submjrhd_Code sbMjrHdCd, " +
					" pr.minhd_Code minHdCd,pr.subhd_Code sbhdCode, " +
					" sum(pr.amount) lAmnt from Trn_Pension_Recovery_Dtls pr  " +
					" where pr.recovery_From_Flag = :lStrTypeFlag and pr.from_Month <= :lForMonth and pr.to_Month >= :lForMonth ");
			if (lPnsnrCodeLst != null && !lPnsnrCodeLst.isEmpty())
			{
				hqlQuery.append(" and ( ");
				int flg = 0;
				for (int i = 0; i < lPnsnrCodeLst.size(); i++)
				{
					if (flg == 1)
						hqlQuery.append(" or ");
					hqlQuery.append("pr.pensioner_Code in (" + lPnsnrCodeLst.get(i) + ")");
					flg = 1;
				}
				hqlQuery.append(") ");
			}
			else
			{
				hqlQuery.append(" and 1 = 2 ");
			}
			hqlQuery.append(" group by pr.edp_Code ,pr.deduction_Type, pr.mjrhd_Code, pr.submjrhd_Code,pr.minhd_Code,pr.subhd_Code ");

			SQLQuery lQuery = ghibSession.createSQLQuery(hqlQuery.toString());

			lQuery.setInteger("lForMonth", Integer.parseInt(lForMonth));
			lQuery.setString("lStrTypeFlag", lStrTypeFlag);
			
			lQuery.addScalar("edpCode",Hibernate.STRING);
			lQuery.addScalar("dedType",Hibernate.STRING);
			lQuery.addScalar("mjrHdCd",Hibernate.STRING);
			lQuery.addScalar("sbMjrHdCd",Hibernate.STRING);
			lQuery.addScalar("minHdCd",Hibernate.STRING);
			lQuery.addScalar("sbhdCode",Hibernate.STRING);
			lQuery.addScalar("lAmnt",Hibernate.BIG_DECIMAL);
			
			
			List resList = lQuery.list();
			
			
			if (resList != null)
			{
				Iterator it = resList.iterator();
				Object[] tuple = null;
				BillEdpVO billEdpVO = null;
				while (it.hasNext())
				{
					tuple = (Object[]) it.next();
					billEdpVO = new BillEdpVO();
					billEdpVO.setEdpCode((String) tuple[0]);
					billEdpVO.setEdpCategory((String) tuple[1]);
					billEdpVO.setBudmjrHd((String) tuple[2]);
					billEdpVO.setBudsubmjrHd((String) tuple[3]);
					billEdpVO.setBudminHd((String) tuple[4]);
					billEdpVO.setBudsubHd((String) tuple[5]);
					if (tuple[6].toString() != null)
					{
						billEdpVO.setEdpAmt(new BigDecimal(tuple[6].toString()));
					}
					else
					{
						billEdpVO.setEdpAmt(lBDZero);
					}
					dataList.add(billEdpVO);
				}
			}

		}
		catch(Exception e)
		{
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return dataList;
	}

	public List getRcptEdpDtlForConsolidatedMnthly(String lForMonth, String lStrTypeFlag, List lPnsnrCodeLst,String lStrPPOList, String lLocCode) throws Exception {

		List dataList = new ArrayList();
		BigDecimal lBDZero = new BigDecimal("0.00");
		try {

			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer();

			hqlQuery.append(" SELECT PR.EDP_CODE EDPCODE,PR.DEDUCTION_TYPE DEDUCTIONTYPE, PR.MJRHD_CODE MJRHDCODE, " +
					" PR.SUBMJRHD_CODE SUBMJRHDCODE,PR.MINHD_CODE MINHDCODE,PR.SUBHD_CODE SUBHDCODE, SUM(PR.AMOUNT) AMOUNT "+
    			    " FROM TRN_PENSION_RECOVERY_DTLS PR ,TRN_PENSION_RQST_HDR TR "+
    			    " WHERE PR.PENSIONER_CODE = TR.PENSIONER_CODE AND TR.location_code = :LocCode AND TR.CASE_STATUS = :status " +
    			    " AND PR.BILL_NO IS NULL AND "+
    			    " ( ( (PR.RECOVERY_FROM_FLAG = :lStrTypeFlag AND PR.FROM_MONTH <= :lForMonth AND PR.TO_MONTH >= :lForMonth) " );
    			    
    			   
    			  //  hqlQuery.append(  ") ");        			    
		 			
    				if (lPnsnrCodeLst != null && !lPnsnrCodeLst.isEmpty())
					{
						hqlQuery.append(" and ( ");
						int flg = 0;
						for (int i = 0; i < lPnsnrCodeLst.size(); i++)
						{
							if (flg == 1)
								hqlQuery.append(" or ");
							hqlQuery.append(" PR.PENSIONER_CODE IN (" + lPnsnrCodeLst.get(i) + ")");
							flg = 1;
						}
						hqlQuery.append(") ");
					}
					else
					{
						// hqlQuery.append(" and 1 = 2 ");
						return dataList;
					}
	
		hqlQuery.append(" )");
		
		if(lStrPPOList != null && lStrPPOList.length() > 0) 
		{
			hqlQuery.append(" or " +
							" ( PR.RECOVERY_FROM_FLAG in (:CVP,:DCRG)" +
							" AND TR.PPO_NO IN ( " + lStrPPOList + " )" +
							" )");
		}
		
/*		 if(lLstOmrPenCode!=null && !lLstOmrPenCode.isEmpty())
		    {
		    	hqlQuery.append(" or (PR.RECOVERY_FROM_FLAG = :lOMRFlag and PR.PENSIONER_CODE IN (:lstOmr) )" );
		    
		    }*/
		
		hqlQuery.append(" ) ");
		
		hqlQuery.append(" GROUP BY PR.EDP_CODE ,PR.DEDUCTION_TYPE, PR.MJRHD_CODE, PR.SUBMJRHD_CODE,PR.MINHD_CODE,PR.SUBHD_CODE");

		SQLQuery lQuery = ghibSession.createSQLQuery(hqlQuery.toString());

			lQuery.setParameter("lForMonth", Integer.parseInt(lForMonth));
			lQuery.setParameter("lStrTypeFlag", "Monthly");
			lQuery.setParameter("status", "APPROVED");
			lQuery.setString("LocCode", lLocCode);
			
/*			if(lLstOmrPenCode!=null && !lLstOmrPenCode.isEmpty())
		    {
				lQuery.setParameter("lOMRFlag", "OMR");
				lQuery.setParameterList("lstOmr",lLstOmrPenCode);
		    }*/
			
			if(lStrPPOList != null && lStrPPOList.length() > 0) 
			{
				lQuery.setString("CVP", "CVP");
				lQuery.setString("DCRG", "DCRG");				
			}
			
			lQuery.addScalar("EDPCODE", Hibernate.STRING);
			lQuery.addScalar("DEDUCTIONTYPE", Hibernate.STRING);
			lQuery.addScalar("MJRHDCODE", Hibernate.STRING);
			lQuery.addScalar("SUBMJRHDCODE", Hibernate.STRING);
			lQuery.addScalar("MINHDCODE", Hibernate.STRING);
			lQuery.addScalar("SUBHDCODE", Hibernate.STRING);
			lQuery.addScalar("AMOUNT", Hibernate.BIG_DECIMAL);
			
			List resList = lQuery.list();

			if (resList != null) {
				Iterator it = resList.iterator();
				Object[] tuple = null;
				BillEdpVO billEdpVO = null;
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					billEdpVO = new BillEdpVO();
					billEdpVO.setEdpCode((String) tuple[0]);
					billEdpVO.setEdpCategory((String) tuple[1]);
					billEdpVO.setBudmjrHd((String) tuple[2]);
					billEdpVO.setBudsubmjrHd((String) tuple[3]);
					billEdpVO.setBudminHd((String) tuple[4]);
					billEdpVO.setBudsubHd((String) tuple[5]);
					if (tuple[6].toString() != null) {
						billEdpVO.setEdpAmt(new BigDecimal(tuple[6].toString()));
					} else {
						billEdpVO.setEdpAmt(lBDZero);
					}
					dataList.add(billEdpVO);
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return dataList;
	}

	public Map getEdpDetails(BigDecimal headCode) throws Exception
	{

		Map resultMap = new HashMap();
		List lLst;
		Query lQuery;
		long lLngheadCode = Long.parseLong(headCode.toString());
		try
		{
			Session ghibSession = getSession();
			String mySql = " select ph.headCode,ph.edpCode,me.budobjhdCode,me.edpDesc from RltPensionHeadcodeChargable ph, MstEdp me " +
					       " where ph.headCode=:lHeadcode and ph.edpCode=me.edpCode and ph.billType=:Pension ";
			lQuery = ghibSession.createQuery(mySql);
			lQuery.setLong("lHeadcode", lLngheadCode);
			lQuery.setString("Pension", "Pension");
			lLst = lQuery.list();
			if (lLst != null && !lLst.isEmpty())
			{
				Object cols[] = (Object[]) lLst.get(0);
				resultMap.put("headCode", cols[0].toString());
				resultMap.put("edpCode", cols[1].toString());
				resultMap.put("budgetCode", cols[2].toString());
				resultMap.put("edpDesc", cols[3].toString());
			}
		}
		catch(Exception e)
		{
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return resultMap;
	}

	public BillEdpVO getITHeadStructureDtl(long lLngId, Integer lIntFinYr) throws Exception
	{

		BillEdpVO billEdpVO = null;
		try
		{

			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer();

			hqlQuery.append(" SELECT er.edpCode,er.edpCategory,er.budmjrhdCode,er.budsubmjrhdCode,er.budminhdCode,er.budsubhdCode " +
					        " FROM MstEdp er  WHERE er.edpCode = :lEdpCode and activateFlag = '1' AND er.langId = :llngId and finYearId = :FinYr  ");

			Query lQuery = ghibSession.createQuery(hqlQuery.toString());
		
			lQuery.setString("lEdpCode", PensionConstants.IT_EDP_CODE);
			lQuery.setLong("llngId", lLngId);
			lQuery.setInteger("FinYr", lIntFinYr);
			
			List resList = lQuery.list();

			if (resList != null)
			{
				Iterator it = resList.iterator();
				Object[] tuple = (Object[]) it.next();
				billEdpVO = new BillEdpVO();
				billEdpVO.setEdpCode((String) tuple[0]);
				billEdpVO.setEdpCategory((String) tuple[1]);
				billEdpVO.setBudmjrHd((String) tuple[2]);
				billEdpVO.setBudsubmjrHd((String) tuple[3]);
				billEdpVO.setBudminHd((String) tuple[4]);
				billEdpVO.setBudsubHd((String) tuple[5]);
			}
		}
		catch(Exception e)
		{
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return billEdpVO;
	}

}
