/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 29, 2011		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.RltBankBranchTmp;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Sep 29, 2011
 */
public class BankBranchMappingDAOImpl  extends GenericDaoHibernateImpl implements BankBranchMappingDAO{
	
	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(BankBranchMappingDAOImpl.class);

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public BankBranchMappingDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO#getPasBankList(java.lang.String)
	 */
	public List<ComboValuesVO> getPasBankList(String lStrLocationCode) throws Exception {

		List<ComboValuesVO> lLstPasBank = new ArrayList<ComboValuesVO>();
		Iterator itr;
		ComboValuesVO lObjComboValueVO;
		Object[] obj;
		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT pasBankCode,pasBankName FROM PasBankMaster where locationCode = :locationCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("locationCode", lStrLocationCode);
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				itr = lLstResult.iterator();
				while (itr.hasNext()) {
					lObjComboValueVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					lObjComboValueVO.setId(obj[0].toString());
					lObjComboValueVO.setDesc(obj[1].toString());
					lLstPasBank.add(lObjComboValueVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstPasBank;
	}

	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO#getPasBranchListFromBankCode(java.lang.String, java.lang.String)
	 */
	public List getPasBranchListFromBankCode(String lStrLocationCode, String lStrPasBankCode) throws Exception {

		List<ComboValuesVO> lLstPasBranch = new ArrayList<ComboValuesVO>();
		Iterator itr;
		ComboValuesVO lObjComboValueVO;
		Object[] obj;
		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			
			lSBQuery.append(" SELECT pbm.pas_Branch_Code,pbm.pas_Branch_Name,rbb.branch_name \n");
			lSBQuery.append(" FROM Pas_Branch_Master pbm left outer join rlt_bank_branch_tmp rbb on pbm.ifms_branch_code = rbb.branch_code and pbm.ifms_branch_code is not null \n");
			lSBQuery.append(" where pbm.pas_Bank_Code = :pasBankCode and pbm.location_Code = :locationCode ");
			Query sqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			sqlQuery.setString("pasBankCode", lStrPasBankCode);
			sqlQuery.setString("locationCode", lStrLocationCode);
			//sqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			lLstResult = sqlQuery.list();
			
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO#getDistrictList(java.lang.String)
	 */
	public List getDistrictList(String lStrLocationCode) throws Exception {

		List<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		Iterator itr;
		ComboValuesVO lObjComboValueVO;
		Object[] obj;
		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT DISTINCT district FROM RltBankBranchTmp");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			lLstResult = hqlQuery.list();
			for(int lIntCnt=0;lIntCnt<lLstResult.size();lIntCnt++)
			{
				lObjComboValueVO = new ComboValuesVO();
				lObjComboValueVO.setId(lLstResult.get(lIntCnt).toString());
				lObjComboValueVO.setDesc(lLstResult.get(lIntCnt).toString());
				lLstDistrict.add(lObjComboValueVO);
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO#getBankListFromDistrict(java.lang.String)
	 */
	public List<ComboValuesVO> getBankListFromDistrict(String lStrLocationCode,String lStrDistrictName) throws Exception {

		List<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		Iterator itr;
		ComboValuesVO lObjComboValueVO;
		Object[] obj;
		List lLstResult = null;
		try {
			
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT DISTINCT mb.bankCode,mb.bankName FROM RltBankBranchTmp rbb, MstBankTmp mb where rbb.bankCode=mb.bankCode and rbb.district like :districtName");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("districtName", "%" + lStrDistrictName + "%");
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			lLstResult = hqlQuery.list();
			lObjComboValueVO = new ComboValuesVO();
			lObjComboValueVO.setId("-1");
			lObjComboValueVO.setDesc("--Select--");
			lLstDistrict.add(lObjComboValueVO);
			if (lLstResult != null && !lLstResult.isEmpty()) {
				itr = lLstResult.iterator();
				while (itr.hasNext()) {
					lObjComboValueVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					lObjComboValueVO.setId(obj[0].toString());
					lObjComboValueVO.setDesc(obj[1].toString());
					lLstDistrict.add(lObjComboValueVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDistrict;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO#getBranchsFromBankCode(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<RltBankBranchTmp> getBranchsFromBankCode(String lStrLocationCode, String lStrBankCode, String lStrDistrictName) throws Exception {

		List<RltBankBranchTmp> lLstBranch = new ArrayList<RltBankBranchTmp>();
		Iterator itr;
		ComboValuesVO lObjComboValueVO;
		Object[] obj;
		//List lLstResult = null;
		try {
			
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" from RltBankBranchTmp where bankCode = :bankCode and district like :districtName order by branchName");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("bankCode", lStrBankCode);
			hqlQuery.setString("districtName", "%" + lStrDistrictName + "%");
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			lLstBranch = hqlQuery.list();
//			lObjComboValueVO = new ComboValuesVO();
//			lObjComboValueVO.setId("-1");
//			lObjComboValueVO.setDesc("--Select--");
//			lLstBranch.add(lObjComboValueVO);
//			if (lLstResult != null && !lLstResult.isEmpty()) {
//				itr = lLstResult.iterator();
//				while (itr.hasNext()) {
//					lObjComboValueVO = new ComboValuesVO();
//					obj = (Object[]) itr.next();
//					lObjComboValueVO.setId(obj[0].toString());
//					lObjComboValueVO.setDesc(obj[1].toString());
//					lLstBranch.add(lObjComboValueVO);
//				}
//			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstBranch;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO#updateBankBranchMapping(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void updateBankBranchMapping(String lStrLocationCode, String lStrIfmsBankCode, String lStrIfmsBranchCode, String lStrPasBankCode, String lStrPasBranchCode) throws Exception {

		// TODO Auto-generated method stub
		StringBuffer lSBQuery = new StringBuffer();
		Session hiSession = getSession();
		try {
			lSBQuery.append("update PasBranchMaster set ifmsBankCode = :ifmsBankCode , ifmsBranchCode = :ifmsBranchCode WHERE pasBankCode = :pasBankCode "); 
			lSBQuery.append(" AND pasBranchCode = :pasBranchCode AND locationCode = :locationCode ");

			Query hqlQuery = hiSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("ifmsBankCode", lStrIfmsBankCode);
			hqlQuery.setString("ifmsBranchCode", lStrIfmsBranchCode);
			hqlQuery.setString("pasBankCode", lStrPasBankCode);
			hqlQuery.setString("pasBranchCode", lStrPasBranchCode);
			hqlQuery.setString("locationCode", lStrLocationCode);

			hqlQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw e;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.BankBranchMappingDAO#deleteBankMpgDtls(java.lang.String, java.lang.String)
	 */
	public void deleteBankMpgDtls(String lStrLocationCode, String lStrPasBankCode) throws Exception {

		// TODO Auto-generated method stub
		StringBuffer lSBQuery = new StringBuffer();
		Session hiSession = getSession();
		try {
			lSBQuery.append("delete from BankMpg where pasBankCode = :pasBankCode and locationCode = :locationCode"); 
			
			Query hqlQuery = hiSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("pasBankCode", lStrPasBankCode);
			hqlQuery.setString("locationCode", lStrLocationCode);

			hqlQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw e;
		}
	}

	
	
}
