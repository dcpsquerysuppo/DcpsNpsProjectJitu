/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 1, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.pensionproc.valueobject.SavedPensionCasesVO;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAssesdDues;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcCheckList;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRecovery;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRevision;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAgDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAuthorityDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAvgPayCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocEventdtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocForeignServ;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrpay;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrservcbreak;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocProvisionalPaid;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 1, 2011
 */

public class TrnPnsnProcInwardPensionDAOImpl extends GenericDaoHibernateImpl implements TrnPnsnProcInwardPensionDAO {

	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants");

	public TrnPnsnProcInwardPensionDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	public List<SavedPensionCasesVO> getAllFrwdCases(String lStrCasesFrom, String gStrPostId, String lStrDraftFlag, Map displayTag,Long lLngDdoCode) {
		List<SavedPensionCasesVO> lLstPensionCases = null;
		Query lHibQry = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		
		try {
			String[] columnValues = new String[] {"","inw.inwardNo", "inw.sevaarthId", "dtl.pnsnrName", "inw.pensionType", "loc.locName",
					"dtl.joiningDate", "dtl.retirementDate","inw.caseStatus" };

			lSBQuery.append("SELECT new com.tcs.sgv.pensionproc.valueobject.SavedPensionCasesVO(inw.inwardPensionId,inw.inwardNo,inw.sevaarthId,dtl.pnsnrName,inw.pensionType,");
			lSBQuery.append("loc.locName,dtl.joiningDate,dtl.retirementDate,inw.caseStatus,inw.outwardNo,inw.outwardDate) FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl,CmnLocationMst loc");
			lSBQuery.append(" WHERE  inw.inwardPensionId=dtl.inwardPensionId ");
			lSBQuery.append(" AND  inw.draftFlag in (:lStrDraftFlag) ");
			lSBQuery.append(" AND dtl.departmentId = loc.locId AND inw.caseStatus IN (:caseStatus)");
			lSBQuery.append(" AND inw.ddoCode = :ddoCode");
			
			if (lStrDraftFlag.equalsIgnoreCase("D")) {
				//	lSBQuery.append(" AND inw.createdPostId=:createdPostId ");
			}
			
			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(" ORDER BY "+columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append(" ORDER BY inw.inwardPensionId");
			}

			lHibQry = session.createQuery(lSBQuery.toString());

			lHibQry.setParameter("ddoCode",lLngDdoCode);			
			if (lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("R")) {
				//lHibQry.setParameter("createdPostId", Long.valueOf(gStrPostId));					
				List<String> lLstcaseStatus = new ArrayList<String>();
				lLstcaseStatus.add("D");
				lLstcaseStatus.add("R");
				lLstcaseStatus.add("A");
				lHibQry.setParameterList("lStrDraftFlag", lLstcaseStatus);
			} else {
				lHibQry.setParameter("lStrDraftFlag", lStrDraftFlag);
			}
			if ((lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A"))) {
				if (lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("R")) {
					//lHibQry.setParameter("createdPostId", Long.valueOf(gStrPostId));					
					List<String> lLstcaseStatus = new ArrayList<String>();
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORDDOMISTAKESTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORAGQUERYSTATUSID"));					
					lHibQry.setParameterList("caseStatus", lLstcaseStatus);
				} else {

					if (lStrDraftFlag.equalsIgnoreCase("F")) {
						if ("V".equalsIgnoreCase(lStrCasesFrom)) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
						}
						if ("D".equalsIgnoreCase(lStrCasesFrom)) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
						}
					} else if (lStrDraftFlag.equalsIgnoreCase("R")) {
						
					} else // lStrDraftFlag.equalsIgnoreCase("A")
					{
						List<String> lLstcaseStatus = new ArrayList<String>();
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.SENDTOAGSTATUSID"));	
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID"));
						lHibQry.setParameterList("caseStatus", lLstcaseStatus);
					}
				}
			}

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
			lHibQry.setMaxResults(Constants.PDWL_PAGE_SIZE);

			lLstPensionCases = lHibQry.list();

		} catch (Exception e) {

			gLogger.error("Error is" + e, e);

		}
		return lLstPensionCases;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#getAllFrwdCasesCount
	 * (java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */

	public Integer getAllFrwdCasesCount(String lStrCasesFrom, String gStrPostId, String lStrDraftFlag, Map displayTag,Long lLngDdoCode) {

		Integer lIntCount = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();

			lSBQuery.append("SELECT COUNT(*) FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl,CmnLocationMst loc");
			lSBQuery.append(" WHERE  inw.inwardPensionId=dtl.inwardPensionId ");
			lSBQuery.append(" AND  inw.draftFlag in (:lStrDraftFlag)");
			lSBQuery.append(" AND dtl.departmentId = loc.locId AND inw.caseStatus IN (:caseStatus)");
			lSBQuery.append(" AND inw.ddoCode = :ddoCode");
//			lSBQuery
//					.append("SELECT COUNT(*) FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl,CmnLocationMst loc,CmnLookupMst look ");
//			lSBQuery.append(" WHERE  inw.inwardPensionId=dtl.inwardPensionId ");
//			lSBQuery.append(" AND  inw.draftFlag=:lStrDraftFlag");
//			lSBQuery
//					.append(" AND inw.pensionType = look.lookupId AND dtl.departmentId = loc.locId AND inw.caseStatus IN (:caseStatus)");
			if (lStrDraftFlag.equalsIgnoreCase("D")) {
				//	lSBQuery.append(" AND inw.createdPostId=:createdPostId ");
			}
		
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("ddoCode",lLngDdoCode);
			if (lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("R")) {
				//lHibQry.setParameter("createdPostId", Long.valueOf(gStrPostId));					
				List<String> lLstcaseStatus = new ArrayList<String>();
				lLstcaseStatus.add("D");
				lLstcaseStatus.add("R");
				lLstcaseStatus.add("A");
				lHibQry.setParameterList("lStrDraftFlag", lLstcaseStatus);
			} else {
				lHibQry.setParameter("lStrDraftFlag", lStrDraftFlag);
			}
			if ((lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A"))) {
				if (lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("R")) {
					//lHibQry.setParameter("createdPostId", Long.valueOf(gStrPostId));
					List<String> lLstcaseStatus = new ArrayList<String>();
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORDDOMISTAKESTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORAGQUERYSTATUSID"));					
					lHibQry.setParameterList("caseStatus", lLstcaseStatus);
				} else {
					if (lStrDraftFlag.equalsIgnoreCase("F")) {
						if ("V".equalsIgnoreCase(lStrCasesFrom)) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
						}
						if ("D".equalsIgnoreCase(lStrCasesFrom)) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
						}
					} else if (lStrDraftFlag.equalsIgnoreCase("R")) {
						/*List<String> lLstcaseStatus = new ArrayList<String>();
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYVERIFIERSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
						lHibQry.setParameterList("caseStatus", lLstcaseStatus);*/
					} else // lStrDraftFlag.equalsIgnoreCase("A")
					{
						List<String> lLstcaseStatus = new ArrayList<String>();
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.SENDTOAGSTATUSID"));	
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID"));
						lHibQry.setParameterList("caseStatus", lLstcaseStatus);
					}
				}
			}

			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lIntCount = Integer.parseInt(lHibQry.list().get(0).toString());
			} else {
				lIntCount = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lIntCount;

	}

	public List<SavedPensionCasesVO> getPensionCaseDtls(String lStrCasesFrom, String lStrDraftFlag, String gStrPostId, Long gLngLangId, String lStrSearchby,
			String lStrSearchValue, Date lDtFrmDate, Date lDtToDate, String lStrSevaarthId, String lStrPPONo, String lStrInwardNo, Date lDtRetiredDate, String lStrName,
			Long lLngDeparmentTypeId, String lStrPensionTypeId, Map displayTag, String lStrBranchName) throws Exception {
		gLogger.info("getPensionCaseDtls start.......");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<SavedPensionCasesVO> lLstInwardCaseDtls = null;
		Query lHibQry = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		Date lDtRetirementDate = null;
		Date lDtInwardDate = null;
		try {

			String[] columnValues = new String[] {"","inw.inwardNo", "inw.sevaarthId", "dtl.pnsnrName", "inw.pensionType", "loc.locName",
					"dtl.joiningDate", "dtl.retirementDate","inw.caseStatus" };
			lSBQuery.append(" SELECT new com.tcs.sgv.pensionproc.valueobject.SavedPensionCasesVO(inw.inwardPensionId,inw.inwardNo,inw.sevaarthId,dtl.pnsnrName,inw.pensionType, ");
			lSBQuery.append(" loc.locName,dtl.joiningDate,dtl.retirementDate,inw.caseStatus,inw.outwardNo,inw.outwardDate) ");
			lSBQuery.append(" FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl ,CmnLocationMst loc ");
			lSBQuery.append(" WHERE inw.inwardPensionId=dtl.inwardPensionId ");
			lSBQuery.append(" AND dtl.departmentId = loc.locId AND loc.cmnLanguageMst.langId =:gLngLangId ");
			if ((lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A"))) {
				lSBQuery.append(" AND inw.draftFlag in (:lStrDraftFlag) ");
				lSBQuery.append(" AND inw.caseStatus IN (:caseStatus)");
			}
			if (!lStrSearchby.equals("-1") && lStrSearchValue != null) {
				if (!lStrSearchby.equals("-1") && (!lStrSearchValue.equals("") || lStrSearchValue.length() > 1)) {
					if (lStrSearchby.equals("Inward No")) {
						lSBQuery.append(" AND inw.inwardNo= :lStrSearchValue");
					}
					if (lStrSearchby.equals("Inward Date")) {

						lDtInwardDate = simpleDateFormat.parse(lStrSearchValue);
						lSBQuery.append(" AND inw.inwardDate= :lStrSearchValue");
					}
					if (lStrSearchby.equals("Inward Type")) {
						lSBQuery.append(" AND inw.caseType= :lStrSearchValue ");
					}
					if (lStrSearchby.equals("Retirement Date")) {

						lDtRetirementDate = simpleDateFormat.parse(lStrSearchValue);
						lSBQuery.append(" AND dtl.retirementDate= :lStrSearchValue ");
					}
					if (lStrSearchby.equals("Pension Type")) {
						lSBQuery.append(" AND inw.pensionType= :lStrSearchValue ");
					}
					if (lStrSearchby.equals("Bank Branch Name")) {
						lSBQuery.append(" AND dtl.bankName= :lStrSearchValue ");
						lSBQuery.append(" AND dtl.bankBranchName= :lStrBranchName ");
					}
					if (lStrSearchby.equals("PPO No")) {
						lSBQuery.append(" AND inw.ppoNo= :lStrSearchValue ");
					}
				}

			} else {
				if (lDtFrmDate != null && lDtToDate != null) {
					lSBQuery.append(" AND inw.inwardDate BETWEEN :lDtFrmDate AND :lDtToDate ");
				}
				if (lStrSevaarthId != null) {
					lSBQuery.append(" AND inw.sevaarthId = :lStrSevaarthId");
				}
				if (lStrInwardNo != null) {
					lSBQuery.append(" AND inw.inwardNo = :lStrInwardNo");
				}
				if (lStrPPONo != null) {
					lSBQuery.append(" AND inw.ppoNo= :lStrPPONo");
				}
				if (lDtRetiredDate != null) {
					lSBQuery.append(" AND dtl.retirementDate = :lDtRetiredDate");
				}
				if (lStrName != null && !lStrName.equals("")) {
					lSBQuery.append(" AND dtl.pnsnrName like :lStrName");
				}
				if (lLngDeparmentTypeId != null) {
					lSBQuery.append(" AND dtl.departmentId = :lLngDeparmentTypeId");
				}
				if (lStrPensionTypeId != null) {
					lSBQuery.append(" AND inw.pensionType = :lLngPensionTypeId");
				}

			}

			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(" ORDER BY "+columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append(" ORDER BY inw.inwardPensionId");
			}

			lHibQry = session.createQuery(lSBQuery.toString());
			if (!lStrSearchby.equals("-1") && lStrSearchValue != null) {
				if (lStrSearchby.equals("Inward Date")) {
					lHibQry.setParameter("lStrSearchValue", lDtInwardDate);
				} else if (lStrSearchby.equals("Retirement Date")) {
					lHibQry.setParameter("lStrSearchValue", lDtRetirementDate);
				} else {
					lHibQry.setParameter("lStrSearchValue", lStrSearchValue.toUpperCase());
					if (lStrSearchby.equals("Bank Branch Name")) {
						lHibQry.setParameter("lStrBranchName", lStrBranchName);
					}
				}

			} else {
				if ((lDtFrmDate != null && lDtToDate != null) || lStrSevaarthId != null || lStrPPONo != null || lStrInwardNo != null || lDtRetiredDate != null || lStrName != null
						|| lLngDeparmentTypeId != null || lStrPensionTypeId != null) {
					if (lDtFrmDate != null && lDtToDate != null) {
						lHibQry.setParameter("lDtFrmDate", lDtFrmDate);
						lHibQry.setParameter("lDtToDate", lDtToDate);
					}
					if (lStrSevaarthId != null) {
						lHibQry.setParameter("lStrSevaarthId", lStrSevaarthId);
					}
					if (lStrInwardNo != null) {
						lHibQry.setParameter("lStrInwardNo", lStrInwardNo.toUpperCase());
					}
					if (lStrPPONo != null) {
						lHibQry.setParameter("lStrPPONo", lStrPPONo);
					}
					if (lDtRetiredDate != null) {
						lHibQry.setParameter("lDtRetiredDate", lDtRetiredDate);
					}
					if (lStrName != null) {
						lHibQry.setParameter("lStrName", lStrName +"%");
					}
					if (lLngDeparmentTypeId != null) {
						lHibQry.setLong("lLngDeparmentTypeId", lLngDeparmentTypeId);
					}
					if (lStrPensionTypeId != null) {
						lHibQry.setParameter("lLngPensionTypeId", lStrPensionTypeId);
					}
				}
			}
			if ((lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A"))) {
				if (lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("R")) {
					List<String> lLstcaseStatus = new ArrayList<String>();
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORDDOMISTAKESTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORAGQUERYSTATUSID"));
										
					lHibQry.setParameterList("caseStatus", lLstcaseStatus);
					
					lLstcaseStatus = new ArrayList<String>();
					lLstcaseStatus.add("D");
					lLstcaseStatus.add("F");
					lLstcaseStatus.add("A");
					
					lHibQry.setParameterList("lStrDraftFlag", lLstcaseStatus);
				}else if (lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A")) {
					if (lStrDraftFlag.equalsIgnoreCase("F")) {
						if (lStrCasesFrom.equalsIgnoreCase("D")) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID"));
						}
						if (lStrCasesFrom.equalsIgnoreCase("V")) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
						}
					}
					if (lStrDraftFlag.equalsIgnoreCase("R")) {
						/*List<String> lLstcaseStatus = new ArrayList<String>();
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYVERIFIERSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
						lHibQry.setParameterList("caseStatus", lLstcaseStatus);*/
					}
					if (lStrDraftFlag.equalsIgnoreCase("A")) {						
						List<String> lLstcaseStatus = new ArrayList<String>();
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.SENDTOAGSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID"));
						lHibQry.setParameterList("caseStatus", lLstcaseStatus);
					}
					lHibQry.setParameter("lStrDraftFlag", lStrDraftFlag);
				}
				
			}

			lHibQry.setLong("gLngLangId", gLngLangId);

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
			lHibQry.setMaxResults(Constants.PDWL_PAGE_SIZE);

			lLstInwardCaseDtls = lHibQry.list();
		} catch (Exception e) {

			gLogger.error("Error is" + e, e);
			throw e;
		}
		gLogger.info("getPensionCaseDtls end.......");
		return lLstInwardCaseDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * getPensionCaseDtlsCount(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Long, java.lang.String, java.lang.String,
	 * java.util.Date, java.util.Date, java.lang.Long, java.lang.Long,
	 * java.lang.String, java.util.Date, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.util.Map)
	 */

	public Integer getPensionCaseDtlsCount(String lStrCasesFrom, String lStrDraftFlag, String gStrPostId, Long gLngLangId, String lStrSearchby, String lStrSearchValue,
			Date lDtFrmDate, Date lDtToDate, String lStrSevaarthId, String lStrPPONo, String lStrInwardNo, Date lDtRetiredDate, String lStrName, Long lLngDeparmentTypeId,
			String lStrPensionTypeId, Map displayTag, String lStrBranchName) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Integer lIntCount = null;
		Query lHibQry = null;
		StringBuilder lSBQuery = null;
		Date lDtRetirementDate = null;
		Date lDtInwardDate = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(*) FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl ,CmnLocationMst loc ");
			lSBQuery.append("WHERE inw.inwardPensionId=dtl.inwardPensionId ");
			lSBQuery.append(" AND dtl.departmentId = loc.locId");
			lSBQuery.append(" AND loc.cmnLanguageMst.langId =:gLngLangId  ");
			if ((lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A"))) {
				lSBQuery.append(" AND inw.draftFlag in (:lStrDraftFlag) ");
				lSBQuery.append(" AND inw.caseStatus IN (:caseStatus)");

			}
			if (!lStrSearchby.equals("-1") && lStrSearchValue != null) {
				if (!lStrSearchby.equals("-1") && (!lStrSearchValue.equals("") || lStrSearchValue.length() > 1)) {
					if (lStrSearchby.equals("Inward No")) {
						lSBQuery.append(" AND inw.inwardNo= :lStrSearchValue");
					}
					if (lStrSearchby.equals("Inward Date")) {

						lDtInwardDate = simpleDateFormat.parse(lStrSearchValue);
						lSBQuery.append(" AND inw.inwardDate= :lStrSearchValue");
					}
					if (lStrSearchby.equals("Inward Type")) {
						lSBQuery.append(" AND inw.caseType= :lStrSearchValue ");
					}
					if (lStrSearchby.equals("Retirement Date")) {

						lDtRetirementDate = simpleDateFormat.parse(lStrSearchValue);
						lSBQuery.append(" AND dtl.retirementDate= :lStrSearchValue ");
					}
					if (lStrSearchby.equals("Pension Type")) {
						lSBQuery.append(" AND inw.pensionType= :lStrSearchValue ");
					}
					if (lStrSearchby.equals("Bank Branch Name")) {
						lSBQuery.append(" AND dtl.bankName= :lStrSearchValue ");
						lSBQuery.append(" AND dtl.bankBranchName= :lStrBranchName ");
					}
					if (lStrSearchby.equals("PPO No")) {
						lSBQuery.append(" AND inw.ppoNo= :lStrSearchValue ");
					}
				}

			} else {
				if (lDtFrmDate != null && lDtToDate != null) {
					lSBQuery.append(" AND inw.inwardDate BETWEEN :lDtFrmDate AND :lDtToDate ");
				}
				if (lStrSevaarthId != null) {
					lSBQuery.append(" AND inw.sevaarthId = :lStrSevaarthId");
				}
				if (lStrInwardNo != null) {
					lSBQuery.append(" AND inw.inwardNo = :lStrInwardNo");
				}
				if (lStrPPONo != null) {
					lSBQuery.append(" AND inw.ppoNo= :lStrPPONo");
				}
				if (lDtRetiredDate != null) {
					lSBQuery.append(" AND dtl.retirementDate = :lDtRetiredDate");
				}
				if (lStrName != null && !lStrName.equals("")) {
					lSBQuery.append(" AND dtl.pnsnrName like :lStrName");
				}
				if (lLngDeparmentTypeId != null) {
					lSBQuery.append(" AND dtl.departmentId = :lLngDeparmentTypeId");
				}
				if (lStrPensionTypeId != null) {
					lSBQuery.append(" AND inw.pensionType = :lLngPensionTypeId");
				}

			}

			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			if (!lStrSearchby.equals("-1") && lStrSearchValue != null) {
				if (lStrSearchby.equals("Inward Date")) {
					lHibQry.setParameter("lStrSearchValue", lDtInwardDate);
				} else if (lStrSearchby.equals("Retirement Date")) {
					lHibQry.setParameter("lStrSearchValue", lDtRetirementDate);
				} else {
					lHibQry.setParameter("lStrSearchValue", lStrSearchValue);
					if (lStrSearchby.equals("Bank Branch Name")) {
						lHibQry.setParameter("lStrBranchName", lStrBranchName);
					}
				}

			} else {
				if ((lDtFrmDate != null && lDtToDate != null) || lStrSevaarthId != null || lStrPPONo != null || lStrInwardNo != null || lDtRetiredDate != null || lStrName != null
						|| lLngDeparmentTypeId != null || lStrPensionTypeId != null) {
					if (lDtFrmDate != null && lDtToDate != null) {
						lHibQry.setParameter("lDtFrmDate", lDtFrmDate);
						lHibQry.setParameter("lDtToDate", lDtToDate);
					}
					if (lStrSevaarthId != null) {
						lHibQry.setParameter("lStrSevaarthId", lStrSevaarthId);
					}
					if (lStrInwardNo != null) {
						lHibQry.setParameter("lStrInwardNo", lStrInwardNo);
					}
					if (lStrPPONo != null) {
						lHibQry.setParameter("lStrPPONo", lStrPPONo);
					}
					if (lDtRetiredDate != null) {
						lHibQry.setParameter("lDtRetiredDate", lDtRetiredDate);
					}
					if (lStrName != null) {
						lHibQry.setParameter("lStrName", lStrName + "%");
					}
					if (lLngDeparmentTypeId != null) {
						lHibQry.setLong("lLngDeparmentTypeId", lLngDeparmentTypeId);
					}
					if (lStrPensionTypeId != null) {
						lHibQry.setParameter("lLngPensionTypeId", lStrPensionTypeId);
					}
				}
			}
			if ((lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A"))) {
				if (lStrDraftFlag.equalsIgnoreCase("D") || lStrDraftFlag.equalsIgnoreCase("R")) {
					List<String> lLstcaseStatus = new ArrayList<String>();
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORDDOMISTAKESTATUSID"));
					lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.MOVEFORAGQUERYSTATUSID"));					
					
					lHibQry.setParameterList("caseStatus", lLstcaseStatus);
					
					lLstcaseStatus = new ArrayList<String>();
					lLstcaseStatus.add("D");
					lLstcaseStatus.add("F");
					lLstcaseStatus.add("A");
					lHibQry.setParameterList("lStrDraftFlag", lLstcaseStatus);
				}else if (lStrDraftFlag.equalsIgnoreCase("F") || lStrDraftFlag.equalsIgnoreCase("R") || lStrDraftFlag.equalsIgnoreCase("A")) {
					if (lStrDraftFlag.equalsIgnoreCase("F")) {
						if (lStrCasesFrom.equalsIgnoreCase("D")) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID"));
						}
						if (lStrCasesFrom.equalsIgnoreCase("V")) {
							lHibQry.setParameter("caseStatus", gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
						}
					}
					if (lStrDraftFlag.equalsIgnoreCase("R")) {
						/*List<String> lLstcaseStatus = new ArrayList<String>();
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYVERIFIERSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
						lHibQry.setParameterList("caseStatus", lLstcaseStatus);*/
					}
					if (lStrDraftFlag.equalsIgnoreCase("A")) {
						List<String> lLstcaseStatus = new ArrayList<String>();
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.SENDTOAGSTATUSID"));
						lLstcaseStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID"));
						lHibQry.setParameterList("caseStatus", lLstcaseStatus);
					}
					lHibQry.setParameter("lStrDraftFlag", lStrDraftFlag);
				}
				
			}
			lHibQry.setLong("gLngLangId", gLngLangId);
			List lLstCount = lHibQry.list();
			if (lLstCount != null && lLstCount.size() > 0) {
				lIntCount = Integer.parseInt(lLstCount.get(0).toString());
			} else {
				lIntCount = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lIntCount;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#getPnsnrDtlsVO
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public TrnPnsnProcPnsnrDtls getPnsnrDtlsVO(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List lLstResult = new ArrayList();
		TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtls = new TrnPnsnProcPnsnrDtls();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnProcPnsnrDtls.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstResult = objCrt.list();
			if (!lLstResult.isEmpty()) {
				lObjTrnPnsnProcPnsnrDtls = (TrnPnsnProcPnsnrDtls) lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPnsnrDtlsVO() : Error is :" + e, e);

		}
		return lObjTrnPnsnProcPnsnrDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * getPensionCaseEventDtls
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public List<TrnPnsnprocEventdtls> getPensionCaseEventDtls(Long lLngInwardPensionId) throws Exception {
		Criteria objCrt = null;
		List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtls = new ArrayList<TrnPnsnprocEventdtls>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocEventdtls.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstTrnPnsnprocEventdtls = objCrt.list();
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPensionCaseEventDtls() : Error is :" + e, e);

		}
		return lLstTrnPnsnprocEventdtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * getPensionCaseSrvcBrkDtls
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public List<TrnPnsnprocPnsnrservcbreak> getPensionCaseSrvcBrkDtls(Long lLngInwardPensionId) throws Exception {
		Criteria objCrt = null;
		List<TrnPnsnprocPnsnrservcbreak> lLstTrnPnsnprocPnsnrservcbreak = new ArrayList<TrnPnsnprocPnsnrservcbreak>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocPnsnrservcbreak.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstTrnPnsnprocPnsnrservcbreak = objCrt.list();
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPensionCaseSrvcBrkDtls() : Error is :" + e, e);

		}
		return lLstTrnPnsnprocPnsnrservcbreak;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * getPensionCaseAdvnceBalDtls
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public List<TrnPnsnProcAdvnceBal> getPensionCaseAdvnceBalDtls(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBal = new ArrayList<TrnPnsnProcAdvnceBal>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnProcAdvnceBal.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstTrnPnsnProcAdvnceBal = objCrt.list();
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPensionCaseAdvnceBalDtls() : Error is :" + e, e);

		}
		return lLstTrnPnsnProcAdvnceBal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * getPensionCaseAssesdDueDtls
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public List<TrnPnsnProcAssesdDues> getPensionCaseAssesdDueDtls(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List<TrnPnsnProcAssesdDues> lLstTrnPnsnProcAssesdDues = new ArrayList<TrnPnsnProcAssesdDues>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnProcAssesdDues.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstTrnPnsnProcAssesdDues = objCrt.list();
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPensionCaseAssesdDueDtls() : Error is :" + e, e);

		}
		return lLstTrnPnsnProcAssesdDues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#getPnsnrPayVO
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public TrnPnsnprocPnsnrpay getPnsnrPayVO(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List lLstResult = new ArrayList();
		TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpay = new TrnPnsnprocPnsnrpay();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocPnsnrpay.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstResult = objCrt.list();
			if (!lLstResult.isEmpty()) {
				lObjTrnPnsnprocPnsnrpay = (TrnPnsnprocPnsnrpay) lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPnsnrPayVO() : Error is :" + e, e);
			throw (e);

		}
		return lObjTrnPnsnprocPnsnrpay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#getPnsnCalcVO
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public TrnPnsnProcPnsnCalc getPnsnCalcVO(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List lLstResult = new ArrayList();
		TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalc = new TrnPnsnProcPnsnCalc();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnProcPnsnCalc.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstResult = objCrt.list();
			if (!lLstResult.isEmpty()) {
				lObjTrnPnsnProcPnsnCalc = (TrnPnsnProcPnsnCalc) lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPnsnCalcVO() : Error is :" + e, e);

		}
		return lObjTrnPnsnProcPnsnCalc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#getPnsnrRecoveryVO
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public TrnPnsnProcRecovery getPnsnrRecoveryVO(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List lLstResult = new ArrayList();
		TrnPnsnProcRecovery lObjTrnPnsnProcRecovery = new TrnPnsnProcRecovery();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnProcRecovery.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstResult = objCrt.list();
			if (!lLstResult.isEmpty()) {
				lObjTrnPnsnProcRecovery = (TrnPnsnProcRecovery) lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPnsnrRecoveryVO() : Error is :" + e, e);

		}
		return lObjTrnPnsnProcRecovery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#getPnsnrCheklistVO
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public List<TrnPnsnProcCheckList> getPnsnrCheklistVO(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List<TrnPnsnProcCheckList> lLstTrnPnsnProcCheckList = new ArrayList<TrnPnsnProcCheckList>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnProcCheckList.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstTrnPnsnProcCheckList = objCrt.list();

		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPnsnrCheklistVO() : Error is :" + e, e);
		}
		return lLstTrnPnsnProcCheckList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * getPensionCaseAvgPayCalcDtls
	 * (com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension)
	 */
	public List<TrnPnsnprocAvgPayCalc> getPensionCaseAvgPayCalcDtls(Long lLngInwardPensionId) throws Exception {
		Criteria objCrt = null;
		List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = new ArrayList<TrnPnsnprocAvgPayCalc>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocAvgPayCalc.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			objCrt.addOrder(Order.asc("fromDate"));
			lLstTrnPnsnprocAvgPayCalcVO = objCrt.list();

		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPensionCaseAvgPayCalcDtls() : Error is :" + e, e);

		}
		return lLstTrnPnsnprocAvgPayCalcVO;

	}

	public String getLowerLevelForReturn(Long PostId, Long lLngHierarchyRefId) {
		String lStrToLevel = "";
		List lLstToLevel = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<Long> lLstLevelStatusIDs = new ArrayList<Long>();
		try {

			lLstLevelStatusIDs.add(Long.valueOf(gObjRsrcBndle.getString("PPROC.DEOSTATUSID")));
			lLstLevelStatusIDs.add(Long.valueOf(gObjRsrcBndle.getString("PPROC.VERIFIERSTATUSID")));
			lLstLevelStatusIDs.add(Long.valueOf(gObjRsrcBndle.getString("PPROC.HOOSTATUSID")));
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT st.levelCode FROM  RltLevelStatus st WHERE st.levelStatusId IN (:levelStatusId)");
			lSBQuery
					.append(" AND  st.levelCode < (SELECT mpg.levelId FROM WfHierachyPostMpg mpg WHERE mpg.wfOrgPostMpgMst.postId =:PostId  AND mpg.activateFlag=1 AND mpg.hierachyRefId = :hierachyRefId)  ORDER BY st.levelCode DESC ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			hqlQuery.setLong("PostId", PostId);
			hqlQuery.setLong("hierachyRefId", lLngHierarchyRefId);
			hqlQuery.setParameterList("levelStatusId", lLstLevelStatusIDs);
			lLstToLevel = hqlQuery.list();

			if (lLstToLevel.size() > 0) {
				lStrToLevel = (lLstToLevel.get(0).toString());
			}

		} catch (Exception e) {

			gLogger.error("Error is :" + e, e);

		}
		return lStrToLevel;
	}

	public List getLowerLevelUserList(String lStrToLevel, Long lLngHierRefId, Long lLngLangId) {
		List lLstLowerLevelUserList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstUserList = new ArrayList();
		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OUM.empFname,OUM.empMname,OUM.empLname,RLS.levelCode,RLS.levelDesc,WPM.wfOrgPostMpgMst.postId,OUM.empPrefix ");
			lSBQuery.append(" FROM RltLevelStatus RLS,WfHierachyPostMpg WPM , OrgEmpMst OUM,OrgUserpostRlt OUPR ");
			lSBQuery.append(" WHERE RLS.category = :CATEGORY ");
			lSBQuery.append(" AND WPM.levelId = RLS.levelCode ");
			lSBQuery.append(" AND WPM.hierachyRefId =  :lLngHierRefId ");
			lSBQuery.append(" AND OUPR.orgPostMstByPostId.postId = WPM.wfOrgPostMpgMst.postId ");
			lSBQuery.append(" AND OUM.orgUserMst.userId = OUPR.orgUserMst.userId ");
			lSBQuery.append(" AND WPM.levelId = :LEVEL_CODE AND OUM.cmnLanguageMst.langId =:LANG_ID AND OUPR.activateFlag=1 AND WPM.activateFlag=1 AND OUM.activateFlag=1 ");
			lSBQuery.append(" GROUP BY WPM.wfOrgPostMpgMst.postId,OUM.empPrefix,OUM.empFname,OUM.empMname,OUM.empLname,RLS.levelCode,RLS.levelDesc");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("LANG_ID", lLngLangId);
			hqlQuery.setParameter("lLngHierRefId", lLngHierRefId);
			hqlQuery.setParameter("LEVEL_CODE", Integer.parseInt(lStrToLevel));
			hqlQuery.setParameter("CATEGORY", gObjRsrcBndle.getString("PPROC.CATEGORY"));
			lLstLowerLevelUserList = hqlQuery.list();

			Iterator it = lLstLowerLevelUserList.iterator();

			int lIntLoopJ = 0;
			Object[] tuple = null;
			String[] result = null;

			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				result = new String[5];
				String middleName = tuple[1] != null ? tuple[1].toString() : "";
				String name = " " + tuple[0] + " " + middleName + " " + tuple[2] + " [" + tuple[4] + "]";
				result[0] = name;
				result[1] = tuple[5].toString();
				result[2] = lStrToLevel;
				result[3] = tuple[4].toString();

				lLstUserList.add(result);

				lIntLoopJ++;
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lLstUserList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * dsplyPensionCaseStatus(java.util.Map)
	 */
	public List displayPensionCaseStatus(List<String> lLstStatus, Map displayTag,Long lLngDdoCode) {
		List lLstPnsnCaseStatus = null;
		Query lHibQry = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		try {
			String[] columnValues = new String[] {"inw.inwardNo", "dtl.pnsnrName","inw.pensionType", "dtl.retirementDate","inw.caseStatus","inw.caseType" };
			lSBQuery.append(" SELECT inw.inwardPensionId,inw.inwardNo,dtl.pnsnrName,inw.pensionType,dtl.retirementDate,inw.caseStatus,inw.caseType ");
			lSBQuery.append(" FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl ");
			lSBQuery.append(" WHERE inw.inwardPensionId=dtl.inwardPensionId ");
			//lSBQuery.append(" AND inw.caseStatus IN (:caseStatus) ");
			lSBQuery.append(" AND inw.ddoCode = :ddoCode");

			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(" ORDER BY "+columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append(" ORDER BY inw.inwardPensionId");
			}

			lHibQry = session.createQuery(lSBQuery.toString());
			lHibQry.setParameter("ddoCode", lLngDdoCode);			
			//lHibQry.setParameterList("caseStatus", lLstStatus);

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
			lHibQry.setMaxResults(Constants.PDWL_PAGE_SIZE);

			lLstPnsnCaseStatus = lHibQry.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}
		return lLstPnsnCaseStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#
	 * displayPensionCaseStatusCount(java.util.List)
	 */

	public Integer displayPensionCaseStatusCount(List<String> lLstStatus, Map displayTag,Long lLngDdoCode) {
		Integer lIntCount = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT COUNT(*) ");
			lSBQuery.append(" FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl ");
			lSBQuery.append(" WHERE inw.inwardPensionId=dtl.inwardPensionId ");
			//lSBQuery.append(" AND inw.caseStatus IN (:caseStatus) ");
			lSBQuery.append(" AND inw.ddoCode = :ddoCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("ddoCode", lLngDdoCode);
			//lHibQry.setParameterList("caseStatus", lLstStatus);
			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lIntCount = Integer.parseInt(lHibQry.list().get(0).toString());
			} else {
				lIntCount = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lIntCount;

	}

	public String getRoleByPost(Long lLngPostId) {
		List lLstResultList = null;
		Query hqlQuery = null;
		String lStrToRole = "";
		StringBuilder lStrQuery = new StringBuilder();
		Session session = getSession();
		try {
			lStrQuery.append(" SELECT acl.aclRoleMst.roleId from AclPostroleRlt acl where acl.orgPostMst.postId=:lLngPostId ");

			hqlQuery = session.createQuery(lStrQuery.toString());
			hqlQuery.setParameter("lLngPostId", lLngPostId);
			lLstResultList = hqlQuery.list();

			gLogger.info("resultList Size FOR LEVEL : " + lLstResultList.size());
			if (lLstResultList.size() > 0) {
				for (int i = 0; i < lLstResultList.size(); i++) {
					if (lStrToRole == null || lStrToRole.equals("")) {
						lStrToRole = (lLstResultList.get(i).toString());
					} else {
						lStrToRole = lStrToRole + "," + (lLstResultList.get(i).toString());
					}
				}
			} else {
				lStrToRole = "";
			}
		} catch (Exception e) {

			gLogger.error("Error is :" + e, e);

		}
		gLogger.info("getRoleByPost in Role Ids : " + lStrToRole);
		gLogger.info("getRoleByPost end...................");

		return lStrToRole;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#wfIsExistsOrNot
	 * (java.lang.Long)
	 */

	public Boolean isWfExists(Long lLngInwardPensionId) {

		Integer lIntCnt = null;
		Query hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		Session session = getSession();
		try {
			lStrQuery.append(" SELECT COUNT(*) FROM TrnPnsnProcInwardPension inw,WfJobMst job ");
			lStrQuery.append(" WHERE inw.inwardPensionId=job.jobRefId ");
			lStrQuery.append(" AND job.wfDocMst.docId=:docId AND inw.inwardPensionId=:inwardPensionId");

			hqlQuery = session.createQuery(lStrQuery.toString());
			hqlQuery.setParameter("docId", Long.valueOf(gObjRsrcBndle.getString("PPROC.DOCID")));
			hqlQuery.setParameter("inwardPensionId", lLngInwardPensionId);
			if (hqlQuery.list().size() > 0) {
				lIntCnt = Integer.valueOf(hqlQuery.list().get(0).toString());
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}
		if (lIntCnt > 0) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO#getDDOCode(java
	 * .lang.String)
	 */

	public String getLocCodeFromDDO(String lStrLocationCode) {
		String lStrLocCode = null;
		Query hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		Session session = getSession();
		try {
			lStrQuery.append(" SELECT rlt.locationCode FROM RltDdoOrg rlt,OrgDdoMst mst WHERE mst.ddoCode = rlt.ddoCode AND mst.locationCode =:locationCode");
			hqlQuery = session.createQuery(lStrQuery.toString());
			hqlQuery.setParameter("locationCode", lStrLocationCode.trim());
			if (hqlQuery.list().size() > 0) {
				lStrLocCode = hqlQuery.list().get(0).toString();
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}
		return lStrLocCode;
	}

	public BigDecimal getCvpRate(BigDecimal lBDAge, String lStrPayCmsn) throws Exception {
		BigDecimal lBDCvpRate = BigDecimal.ZERO;
		Query hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		Session session = getSession();
		try {
			lStrQuery.append(" SELECT cvpRate FROM MstPensionCvpRate WHERE age=:age AND  payCommission =:payCommission ");
			hqlQuery = session.createQuery(lStrQuery.toString());
			hqlQuery.setParameter("age", lBDAge);
			hqlQuery.setParameter("payCommission", lStrPayCmsn.trim());
			if (hqlQuery.list().size() > 0) {
				lBDCvpRate = new BigDecimal(hqlQuery.list().get(0).toString());
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}
		return lBDCvpRate;

	}

	public List<ComboValuesVO> getDesignationForPensionCase(String lStrDesignation,Long lLngLangId) throws Exception {
		List<ComboValuesVO> lLstDesignation = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;
		List lLstResult = new ArrayList();
		String lStrDesig = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" Select dsgn.dsgnName ");
			lSBQuery.append(" FROM OrgDesignationMst dsgn ");
			lSBQuery.append(" WHERE dsgn.cmnLanguageMst.langId =:langId AND dsgn.dsgnName LIKE '" + lStrDesignation + "%' order by dsgn.dsgnName,dsgn.dsgnId");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("langId", lLngLangId);
			
			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				Iterator it = lLstResult.iterator();
				while (it.hasNext()) {
					lObjComboValueVO = new ComboValuesVO();
					lStrDesig = (String) it.next();
					lObjComboValueVO.setId(lStrDesig);
					lObjComboValueVO.setDesc(lStrDesig);
					lLstDesignation.add(lObjComboValueVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDesignation;

	}

	public List<TrnPnsnprocProvisionalPaid> getPnsnprocProvisionalPaidDtls(Long lLngInwardPensionId) throws Exception {
		Criteria objCrt = null;
		List<TrnPnsnprocProvisionalPaid> lLstPnsnprocProvisionalPaid = new ArrayList<TrnPnsnprocProvisionalPaid>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocProvisionalPaid.class);
			objCrt.add(Restrictions.eq("inwardPensionId", lLngInwardPensionId));
			lLstPnsnprocProvisionalPaid = objCrt.list();
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPnsnprocProvisionalPaidDtls() : Error is :" + e, e);

		}
		return lLstPnsnprocProvisionalPaid;

	}
	
	public List<TrnPnsnprocForeignServ> getForeignServDtls(Long lLngInwardPensionId) throws Exception {
		Criteria objCrt = null;
		List<TrnPnsnprocForeignServ> lLstForeignServ = new ArrayList<TrnPnsnprocForeignServ>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocForeignServ.class);
			objCrt.add(Restrictions.eq("inwardPensionId", lLngInwardPensionId));
			lLstForeignServ = objCrt.list();
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getForeignServDtls() : Error is :" + e, e);

		}
		return lLstForeignServ;

	}


	public List<MstEmp> getEmpBasicDtls(String lStrSevaarthId,String lStrDdoCode) throws Exception {

		
		List<MstEmp> lLstEmpBasicDtls = new ArrayList<MstEmp>();
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append("from MstEmp where sevarthId = :sevarthId ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("sevarthId", lStrSevaarthId);
			lLstEmpBasicDtls = lHibQry.list();			
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getEmpBasicDtls() : Error is :" + e, e);

		}
		
		return lLstEmpBasicDtls;
	}
	public String getDdoCodeForDDOAsst(Long lLngAsstPostId) {

		String lStrDdoCode = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT OD.ddoCode");
		lSBQuery.append(" FROM RltDdoAsst RD, OrgDdoMst OD");
		lSBQuery.append(" WHERE OD.postId = RD.ddoPostId AND RD.asstPostId = :asstPostId ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("asstPostId", lLngAsstPostId);

		List lLstCodeList = lQuery.list();

		if(lLstCodeList != null)
		{
			if(lLstCodeList.size() != 0)
			{
				if(lLstCodeList.get(0) != null)
				{
					lStrDdoCode = lLstCodeList.get(0).toString();
				}
			}
		}

		return lStrDdoCode;
	}

	
	public List getEmpGpfOrDcpsAccNo(String lStrSevaarthId) throws Exception {
	
		List lLstEmpGpfOrDcpsAccNo = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT CASE WHEN e.DCPS_OR_GPF = 'Y' THEN e.DCPS_ID  ELSE h.PF_SERIES || '/' || h.GPF_ACC_NO  END AS AccNo,e.DCPS_OR_GPF ");
			lSBQuery.append(" FROM MST_DCPS_EMP e, HR_PAY_GPF_DETAILS h,ORG_EMP_MST o,ORG_USER_MST u where ");
			lSBQuery.append(" e.SEVARTH_ID = :sevarthId ");
			lSBQuery.append(" and e.ORG_EMP_MST_ID = o.EMP_ID and o.USER_ID = u.USER_ID and u.USER_ID = h.USER_ID");
			Query lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());
			lHibQry.setParameter("sevarthId", lStrSevaarthId);
			lLstEmpGpfOrDcpsAccNo = lHibQry.list();			
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getEmpGpfOrDcpsAccNo() : Error is :" + e, e);

		}
		
		return lLstEmpGpfOrDcpsAccNo;
	}

	
	public String getDeptName(Long lLngLocId) throws Exception {
		String lStrDeptName = "";
		List lLstDeptName = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT locName FROM CmnLocationMst WHERE locId = :LocId");			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("LocId", lLngLocId);
			lLstDeptName = lQuery.list();
			if(!lLstDeptName.isEmpty())
			{
				lStrDeptName = lLstDeptName.get(0).toString();
			}			

		} catch (Exception e) {
			
			gLogger.error("Error is :" + e, e);

		}
		return lStrDeptName;
	}

	
	public List getPayScaleDescFromScaleId(Long lLngScaleId) throws Exception {
		List lLstEmpGpfOrDcpsAccNo = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT scaleDesc,hrPayCommissionMst.id FROM HrEisScaleMst WHERE scaleId = :scaleId");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("scaleId", lLngScaleId);
			lLstEmpGpfOrDcpsAccNo = lHibQry.list();			
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getPayScaleDescFromScaleId() : Error is :" + e, e);

		}
		
		return lLstEmpGpfOrDcpsAccNo;
	}

	
	public BigInteger getProvisionalPensionSum(Long lLngInwardId) throws Exception {
		
		BigInteger lBIProPenSum = BigInteger.ZERO;
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT NVL(SUM(PRVSNL_PNSNPAID_PRO_AMT_PAID),0) FROM TRN_PNSNPROC_PROVISIONAL_PAID where INWARD_PENSION_ID = :InwardId");
			Query lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());
			lHibQry.setParameter("InwardId", lLngInwardId);
			lBIProPenSum = (BigInteger) lHibQry.list().get(0);			
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getPayScaleDescFromScaleId() : Error is :" + e, e);

		}
		
		return lBIProPenSum;
	}
	
	public String getTresuryNameFormDDOCode(String lStrDdoCode) throws Exception {
		String lStrTresuryName = "";
		List lLstTresuryName = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT l.locName FROM CmnLocationMst l,RltDdoOrg d WHERE d.ddoCode = :DdoCode and d.locationCode = l.locId");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("DdoCode", lStrDdoCode);
			lLstTresuryName = lHibQry.list();	
			
			if(!lLstTresuryName.isEmpty())
			{
				lStrTresuryName = lLstTresuryName.get(0).toString();
			}	
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getPayScaleDescFromScaleId() : Error is :" + e, e);

		}
		
		return lStrTresuryName;
	}

	
	public String getLookupNameFromLookupId(Long lLngLookupId) throws Exception {
		String lStrLookupName = "";
		List lLstLookupName = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT lookupName FROM CmnLookupMst WHERE lookupId = :lookupId ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("lookupId", lLngLookupId);
			lLstLookupName = lHibQry.list();	
			
			if(!lLstLookupName.isEmpty())
			{
				lStrLookupName = lLstLookupName.get(0).toString();
			}	
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getLookupNameFromLookupId() : Error is :" + e, e);

		}
		
		return lStrLookupName;
	}

	
	public String getBankNameFromBankCode(String lStrBankId) throws Exception {
		String lStrBankName = "";
		List lLstBankName = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT bankName FROM MstBank WHERE bankCode = :bankCode ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("bankCode", lStrBankId);
			lLstBankName = lHibQry.list();	
			
			if(!lLstBankName.isEmpty())
			{
				lStrBankName = lLstBankName.get(0).toString();
			}	
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getBankNameFromBankCode() : Error is :" + e, e);

		}
		
		return lStrBankName;
	}


	public String getBranchNameFromBrachCode(String lStrBranchId) throws Exception {
		String lStrBranchName = "";
		List lLstBranchName = new ArrayList();
		Long lLngBranchCode = 0L;
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT branchName FROM RltBankBranch WHERE branchCode = :branchCode ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			if(lStrBranchId != "" && lLngBranchCode != null)
				lLngBranchCode = Long.parseLong(lStrBranchId);
			
			lHibQry.setParameter("branchCode", lLngBranchCode);
			lLstBranchName = lHibQry.list();	
			
			if(!lLstBranchName.isEmpty())
			{
				lStrBranchName = lLstBranchName.get(0).toString();
			}	
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getBranchNameFromBrachCode() : Error is :" + e, e);

		}
		
		return lStrBranchName;
	}


	public Long getApprovedCaseForRevision(String lStrSevaarthId, Long lLngDdoCode) throws Exception {	
		
		List<Long> lLstApprovedCaseList = new ArrayList<Long>();
		Long lLngInwardPensionId = 0L;
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT inwardPensionId FROM TrnPnsnProcInwardPension WHERE sevaarthId = :sevaarthId and ddoCode = :ddoCode ");
			lSBQuery.append(" AND caseStatus = 'APRVDBYAG' AND draftFlag = 'A' ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());			
			lHibQry.setParameter("sevaarthId", lStrSevaarthId);
			lHibQry.setParameter("ddoCode", lLngDdoCode);
			
			lLstApprovedCaseList = lHibQry.list();	
			
			if(!lLstApprovedCaseList.isEmpty())
			{
				lLngInwardPensionId = lLstApprovedCaseList.get(0);
			}	
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getApprovedCaseForRevision() : Error is :" + e, e);

		}
		
		return lLngInwardPensionId;
	}

	
	public List<MstEmpNmn> getEmpNomineeDtls(Long lLngDcpsEmpId) throws Exception {
		
		List<MstEmpNmn> lLstEmpNomineeDtls = new ArrayList<MstEmpNmn>();		
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append("FROM MstEmpNmn WHERE dcpsEmpId.dcpsEmpId = :lLngDcpsEmpId ");			
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());			
			lHibQry.setParameter("lLngDcpsEmpId", lLngDcpsEmpId);
						
			lLstEmpNomineeDtls = lHibQry.list();	
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getEmpNomineeDtls() : Error is :" + e, e);

		}
		
		return lLstEmpNomineeDtls;
	}

	
	public Long isDdoOrDdoAsst(Long lLngPostId) {	
		
		Long lLngDdoOrDdoAsst = 0L;
		Long isDdoOrDdoAsst = 0L;
		
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append("SELECT COUNT(*) FROM RltDdoAsst WHERE asstPostId = :PostId ");			
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());			
			lHibQry.setParameter("PostId", lLngPostId);
						
			lLngDdoOrDdoAsst = (Long) lHibQry.list().get(0);
			if(lLngDdoOrDdoAsst <= 0){
				lSBQuery = new StringBuilder();	
				lSBQuery.append("SELECT COUNT(*) FROM OrgDdoMst WHERE postId = :PostId ");			
				lHibQry = ghibSession.createQuery(lSBQuery.toString());			
				lHibQry.setParameter("PostId", lLngPostId);
				
				lLngDdoOrDdoAsst = (Long) lHibQry.list().get(0);
				if(lLngDdoOrDdoAsst > 0){
					isDdoOrDdoAsst = 1L;//DDO
				}
			}else{
				isDdoOrDdoAsst = 2L;//DDO Asst
			}
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : isDdoOrDdoAsst() : Error is :" + e, e);

		}
		
		return isDdoOrDdoAsst;
	}
	public String getDdoCodeForDDO(Long lLngPostId) {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			if(lLstDdoDtls != null)
			{
				if(lLstDdoDtls.size()!= 0)
				{
					if(lLstDdoDtls.get(0) != null)
					{
						lStrDdoCode = lLstDdoDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {			
			gLogger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
	}


	public Long getReqPenddingStatus(String lStrSevaarthId, Long lLngDdoCode) throws Exception {
		List<Long> lLstReqPenddingStatus = new ArrayList<Long>();
		Long lLngInwardPensionId = 0L;
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT inwardPensionId FROM TrnPnsnProcInwardPension WHERE sevaarthId = :sevaarthId and ddoCode = :ddoCode ");
			lSBQuery.append(" AND caseStatus != 'APRVDBYAG' ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());			
			lHibQry.setParameter("sevaarthId", lStrSevaarthId);
			lHibQry.setParameter("ddoCode", lLngDdoCode);
			
			lLstReqPenddingStatus = lHibQry.list();	
			
			if(!lLstReqPenddingStatus.isEmpty())
			{
				lLngInwardPensionId = lLstReqPenddingStatus.get(0);
			}	
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getReqPenddingStatus() : Error is :" + e, e);

		}
		
		return lLngInwardPensionId;
	}

	
	public Long getPnsnRevisionId(String lStrSevaarthId) throws Exception {
		
		List<BigInteger> lLstRevisionId = new ArrayList<BigInteger>();
		Long lLngRevisionId = 0L;
		BigInteger lBIRevisionId = BigInteger.ZERO;
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT REVISION_ID FROM  TRN_PNSNPROC_REVISION where SEVAARTH_ID = :sevaarthId and ACTIVE_FLAG = 'Y' and " );
			lSBQuery.append(" REVISION_NO = (SELECT max(REVISION_NO) FROM TRN_PNSNPROC_REVISION where SEVAARTH_ID = :sevaarthId) ");
			Query lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());			
			lHibQry.setParameter("sevaarthId", lStrSevaarthId);
						
			lLstRevisionId = lHibQry.list();	
			if(!lLstRevisionId.isEmpty())
				lBIRevisionId = lLstRevisionId.get(0);
			
			lLngRevisionId = lBIRevisionId.longValue();
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getReqPenddingStatus() : Error is :" + e, e);

		}
		
		return lLngRevisionId;
	}

	
	public Long getRevisionCount(String lStrSevaarthId) throws Exception {
		
		List<Long> lLstRevisionCount = new ArrayList<Long>();
		Long lLngRevisionCount = 0L;
		StringBuilder lSBQuery = new StringBuilder();	
		try {
			lSBQuery.append(" SELECT count(inwardPensionId) FROM TrnPnsnProcRevision WHERE sevaarthId = :sevaarthId ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());			
			lHibQry.setParameter("sevaarthId", lStrSevaarthId);
						
			lLstRevisionCount = lHibQry.list();	
			lLngRevisionCount = lLstRevisionCount.get(0);
		
			lLngRevisionCount++;
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getReqPenddingStatus() : Error is :" + e, e);

		}
		
		return lLngRevisionCount;
	}

	
	public List<TrnPnsnprocAuthorityDtls> getAuthorityDtls(Long lLngInwardPensionId) throws Exception {
		Criteria objCrt = null;
		List<TrnPnsnprocAuthorityDtls> lLstTrnPnsnprocAuthorityDtls = new ArrayList<TrnPnsnprocAuthorityDtls>();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocAuthorityDtls.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstTrnPnsnprocAuthorityDtls = objCrt.list();
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPensionCaseEventDtls() : Error is :" + e, e);

		}
		return lLstTrnPnsnprocAuthorityDtls;
	}	

	public TrnPnsnprocAgDtls getPnsnAgDtls(Long lLngInwardPensionId) throws Exception {

		Criteria objCrt = null;
		List lLstResult = new ArrayList();
		TrnPnsnprocAgDtls lObjTrnPnsnProcAgDtls = new TrnPnsnprocAgDtls();

		try {

			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPnsnprocAgDtls.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstResult = objCrt.list();
			if (!lLstResult.isEmpty()) {
				lObjTrnPnsnProcAgDtls = (TrnPnsnprocAgDtls) lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcInwardPensionDAOImpl : getPnsnCalcVO() : Error is :" + e, e);

		}
		return lObjTrnPnsnProcAgDtls;
	}
}
