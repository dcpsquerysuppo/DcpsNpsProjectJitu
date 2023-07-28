package com.tcs.sgv.common.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


/**
 * BptmCommonServicesDAOImpl This class contains various utility methods such as
 * to get challan id from code and vice versa, to get Bill Control No from Bill
 * No and vice versa, to get Receipt VO, to get Budget Head Structure VO, to get
 * LookupId from Name, to get Emp Name from user Id, to get Lookup Text and
 * description, to get Bill type, to get bill control no, to get max of
 * reference number, to get Total Gross Amount. It also contains utility methods
 * for checking validity of Previous Bill No, to check validity of Receipt No,
 * to check bill(physical or online). Date of Creation : 13th July 2007 Author :
 * Hiral Shah Revision History ===================== Hiral 24-Oct-2007 For
 * making changes for code formating
 */
public class BptmCommonServicesDAOImpl extends GenericDaoHibernateImpl implements BptmCommonServicesDAO {

	Log logger = LogFactory.getLog(getClass());

	// private ResourceBundle lObjRsrcBndle =
	// ResourceBundle.getBundle("resources/billproc/BillprocConstants");

	// private static final String GET_MAX_REF_NUM =
	// "SELECT COUNT(billNo) FROM TrnBillRegister " +
	// "WHERE inwardDt =:currDate AND tsryOfficeCode = :tsryOfficeCode";

	private static final String GET_BILL_TYPE_LIST = " FROM MstBillType mbt WHERE mbt.langId = :langId ORDER BY mbt.subjectDesc, mbt.billTypeCode";
	
	/* Code Modified by Niteesh Bhargava DAT Mumbai for AG Update Utility
	 * Changed the folowing code :-
	 * Changed the value of string variable GET_LOOK_UP_VALUES
	 * from :-
	 * 
	  private static final String GET_LOOK_UP_VALUES = "SELECT O1 FROM CmnLookupMst O1, CmnLookupMst O2 WHERE O1.parentLookupId = O2.lookupId AND O2.lookupName = :lookupName AND O1.cmnLanguageMst.langId = :langId  ORDER BY O1.orderNo,O1.lookupId";
	
	 * to:-

	private static final String GET_LOOK_UP_VALUES = "SELECT O1 FROM CmnLookupMst O1, CmnLookupMst O2 WHERE O1.parentLookupId = O2.lookupId AND O2.lookupName = :lookupName AND O1.cmnLanguageMst.langId = :langId AND O1.orderNo!=17  ORDER BY O1.orderNo,O1.lookupId";
	 
	 * 
	 */
	
	private static final String GET_LOOK_UP_VALUES = "SELECT O1 FROM CmnLookupMst O1, CmnLookupMst O2 WHERE O1.parentLookupId = O2.lookupId AND O2.lookupName = :lookupName AND O1.cmnLanguageMst.langId = :langId AND O1.orderNo!=17  ORDER BY O1.orderNo,O1.lookupId";

	private static final String GET_LEVEL_FRM_STATUS = "SELECT L.levelCode FROM RltLevelStatus L WHERE L.statusCode = :statusCode AND L.category = :category";

	private static final String GET_BILL_MVMNT = "FROM TrnBillMvmnt BM WHERE BM.billNo = :billNo ORDER BY BM.movemntId DESC";

	private static final String GET_ALL_BILL_TYPES = "SELECT mbt.billShrtCode, mbt.subjectId, mbt.subjectDesc, mbt.billTypeCode, mbt.shortName, mbt.activateFlag FROM MstBillType mbt WHERE mbt.langId = :langId ORDER BY mbt.subjectDesc, mbt.billTypeCode";

	public BptmCommonServicesDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	/**
	 * Method to execute Query to get All Bill Types
	 * 
	 * @param Long
	 *            : lLngLangId
	 * @return List
	 */
	public List getBillType(Long lLngLangId) throws Exception {

		Session hibSession = getSession();
		List lListQuery = new ArrayList();

		try {
			Query lQuery = hibSession.createQuery(GET_BILL_TYPE_LIST).setShort("langId", Short.parseShort(lLngLangId.toString()));
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lListQuery = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception occured while getting bill type list : " + e, e);
			throw e;
		}
		return lListQuery;
	}

	public String getAllBillTypes(Long lLngLangId) throws Exception {

		Session hibSession = getSession();
		List lLstData = new ArrayList();
		String strList = "";
		Query lQuery = hibSession.createQuery(GET_ALL_BILL_TYPES).setShort("langId", Short.parseShort(lLngLangId.toString()));
		lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

		lLstData = lQuery.list();

		if (!lLstData.isEmpty()) {
			Object[] obj = null;
			for (int x = 0; x < lLstData.size(); x++) {
				obj = (Object[]) lLstData.get(x);
				strList += obj[0] + "~" + obj[1] + "~" + obj[2] + "~" + obj[3] + "~" + obj[4] + "~" + obj[5] + "^";
			}
		}
		return strList;
	}

	/**
	 * Method to find out all bill types from BillTypeCode(GTR Code)
	 * 
	 * @param lStrBillCode
	 * @param lLngLangId
	 * @param lShrtActvFlag
	 * @param lDbId
	 * @return
	 */
	public List getBillTypeCode(String lStrBillCode, Long lLngLangId, Short lShrtActvFlag, Long lDbId) throws Exception {

		Session hibSession = getSession();
		List lListQuery = null;

		try {
			Query lQuery = hibSession.createQuery("select mbt.billTypeCode, mbt.shortName, mbt.billShrtCode,mbt.subjectId from MstBillType mbt where upper(mbt.billTypeCode) like upper('"
					+ lStrBillCode + "%') and mbt.langId =" + lLngLangId + " " + " and mbt.activateFlag=" + lShrtActvFlag);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lListQuery = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception occured while getting bill type list : " + e, e);
			throw e;
		}
		return lListQuery;
	}

	/**
	 * Method to find out all bill types from BillType Description
	 * 
	 * @param lStrBillDesc
	 * @param lLngLangId
	 * @param lShrtActvFlag
	 * @param lDbId
	 * @return
	 */
	public List getBillTypeDesc(String lStrBillDesc, Long lLngLangId, Short lShrtActvFlag, Long lDbId) throws Exception {

		Session hibSession = getSession();
		List lListQuery = null;

		try {
			Query lQuery = hibSession.createQuery("select mbt.billTypeCode, mbt.shortName, mbt.billShrtCode,mbt.subjectId from MstBillType mbt where upper(mbt.subjectDesc) like upper('%"
					+ lStrBillDesc + "%') and mbt.langId =" + lLngLangId + " and mbt.activateFlag=" + lShrtActvFlag + " and mbt.dbId =" + lDbId + "");
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lListQuery = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception occured while getting bill type list : " + e, e);
			throw e;
		}
		return lListQuery;
	}

	public Long getBillCtrlNo(Map inputMap) {

		Session hibSession = getSession();
		String lStrDDOCode = (String) inputMap.get("DDOCode");
		Long lLongBillNo = Long.valueOf(0);
		StringBuffer lStrQuery = new StringBuffer();
		lStrQuery.append("SELECT bill_Cntrl_No  FROM Trn_Bill_Register t where created_Date = (");
		lStrQuery.append("select max(created_Date) from Trn_Bill_Register where fin_Year_Id = :finYearId ");

		if (lStrDDOCode != null && !lStrDDOCode.equals("")) {
			lStrQuery.append(" and ddo_Code ='" + lStrDDOCode + "'");
		}

		lStrQuery.append(" and location_Code='" + SessionHelper.getLocationCode(inputMap) + "' and ((token_Num is not null and token_Num > 0 and phy_Bill=1) OR phy_Bill=0))  and "
				+ " t.fin_Year_Id = :finYearId");

		if (lStrDDOCode != null && !lStrDDOCode.equals("")) {
			lStrQuery.append("  AND  t.ddo_Code ='" + lStrDDOCode + "' ");
		}

		lStrQuery.append(" AND 	t.location_Code = '" + SessionHelper.getLocationCode(inputMap) + "' and ((token_Num is not null and token_Num > 0 and phy_Bill=1) OR phy_Bill=0)");

		Query query = hibSession.createSQLQuery(lStrQuery.toString());
		query.setString("finYearId", SessionHelper.getFinYrId(inputMap).toString());
		logger.info("In BptmCommonServicesDAOImpl, Query to get Bill Control No. : " + query.toString());
		List lLstQList = query.list();
		if (lLstQList != null) {
			Iterator it = lLstQList.iterator();
			if (it != null && it.hasNext()) {
				Object lObjBilNo = it.next();
				if (lObjBilNo != null && lObjBilNo.toString().length() > 0) {
					try {
						lLongBillNo = Long.parseLong(lObjBilNo.toString().substring(lObjBilNo.toString().lastIndexOf('/') + 1));
					} catch (Exception ex) {
					}
				}
			}
		}

		return (++lLongBillNo);
	}

	public Long getBillControlNo(Map inputMap) {

		Session hibSession = getSession();
		// String lStrLocCode = (String) inputMap.get("locationCode");
		Long lLongBillNo = Long.valueOf(0);
		StringBuffer lStrQuery = new StringBuffer();
		lStrQuery.append("SELECT bill_Cntrl_No  FROM Trn_Bill_Register t where created_Date = (");
		lStrQuery.append("select max(created_Date) from Trn_Bill_Register where fin_Year_Id = :finYearId ");

		// if (lStrLocCode != null && !lStrLocCode.equals(""))
		// {
		// lStrQuery.append(" and location_Code ='" + lStrLocCode + "'");
		// }

		lStrQuery.append(" and location_Code='" + SessionHelper.getLocationCode(inputMap) + "') and t.fin_Year_Id = :finYearId");

		// if (lStrDDOCode != null && !lStrDDOCode.equals(""))
		// {
		// lStrQuery.append("  AND  t.ddo_Code ='" + lStrDDOCode + "' ");
		// }

		lStrQuery.append(" AND 	t.location_Code = '" + SessionHelper.getLocationCode(inputMap) + "'");
		lStrQuery.append(" order by bill_cntrl_no desc");
		Query query = hibSession.createSQLQuery(lStrQuery.toString());
		query.setString("finYearId", SessionHelper.getFinYrId(inputMap).toString());
		query.setMaxResults(1);
		logger.info("In BptmCommonServicesDAOImpl, Query to get Bill Control No. : " + query.toString());
		List lLstQList = query.list();
		if (lLstQList != null) {
			Iterator it = lLstQList.iterator();
			if (it != null && it.hasNext()) {
				Object[] lArrObjBilNo = (Object[]) it.next();
				if (lArrObjBilNo.length == 1) {
					// --For db other than db2.
					if (lArrObjBilNo[0] != null && lArrObjBilNo[0].toString().length() > 0) {
						try {
							lLongBillNo = Long.parseLong(lArrObjBilNo[0].toString().substring(lArrObjBilNo[0].toString().lastIndexOf('/') + 1));
						} catch (Exception ex) {
						}
					}
				} else {
					// --For DB2 db.
					if (lArrObjBilNo[1] != null && lArrObjBilNo[1].toString().length() > 0) {
						try {
							lLongBillNo = Long.parseLong(lArrObjBilNo[1].toString().substring(lArrObjBilNo[1].toString().lastIndexOf('/') + 1));
						} catch (Exception ex) {
						}
					}
				}
			}
		}

		return (++lLongBillNo);
	}

	/**
	 * Method to get Maximum of Bill Control No + 1(to generate Bill Control No.
	 * in an auto incremental mode)
	 * 
	 * @param Map
	 *            : inputMap
	 * @return String
	 */
	public String getBillCtrlNoForOBPM(Map inputMap) {

		Session hibSession = getSession();
		String lStrDDONum = (String) inputMap.get("DDOCode");
		String lStrBillNo = "0";
		StringBuffer lStrQuery = new StringBuffer();
		SessionHelper.getCurDate();

		lStrQuery.append("SELECT bill_Cntrl_No  FROM Trn_Bill_Register t where created_Date = " + "(select max(created_Date) from Trn_Bill_Register where fin_Year_Id = :currentFinYearId");

		if (lStrDDONum != null && !lStrDDONum.equals("")) {
			lStrQuery.append(" and ddo_Code ='" + lStrDDONum + "'");
		}
		lStrQuery.append(" and location_Code='" + SessionHelper.getLocationCode(inputMap) + "' and ((token_Num is not null and token_Num > 0 and phy_Bill=1) OR phy_Bill=0))  and "
				+ " t.fin_Year_Id = :currentFinYearId");

		if (lStrDDONum != null && !lStrDDONum.equals("")) {
			lStrQuery.append("  AND  t. ddo_Code ='" + lStrDDONum + "' ");
		}

		lStrQuery.append(" AND 	t.location_Code = '" + SessionHelper.getLocationCode(inputMap) + "' and ((token_Num is not null and token_Num > 0 and phy_Bill=1) OR phy_Bill=0) ");
		Query query = hibSession.createSQLQuery(lStrQuery.toString());

		query.setParameter("currentFinYearId", inputMap.get("FinYrId").toString());

		logger.info("In BptmCommonServicesDAOImpl, Query to get Bill Control No. : " + query.toString());
		// query.setDate("currDate", lDtCurDate);
		List lLstQList = query.list();
		if (lLstQList != null) {
			Iterator it = lLstQList.iterator();
			if (it != null && it.hasNext()) {
				Object lObjBilNo = it.next();
				if (lObjBilNo != null) {
					lStrBillNo = lObjBilNo.toString();
					logger.info(" Return from Query........ " + lStrBillNo);
				}
			}
		}
		String[] billNo = lStrBillNo.split("/");
		if (billNo != null && billNo.length > 1) {
			lStrBillNo = billNo[billNo.length - 1];
		}
		int billCntrlno = Integer.parseInt(lStrBillNo) + 1;
		lStrBillNo = String.valueOf(billCntrlno);
		return lStrBillNo;
	}

	/**
	 * Method to get Lookup Text from Lookup ID.
	 * 
	 * @param int : lIntLookupId
	 * @param Long
	 *            : lLngLangId
	 * @return String
	 */
	public String getLookUpText(String lStrLookupName, Long lLngLangId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		String lStrLookupText = "";
		try {
			lSBQuery.append(" SELECT clm.lookupName FROM CmnLookupMst clm ");
			lSBQuery.append(" WHERE clm.lookupName = :lookupName ");
			lSBQuery.append(" AND clm.cmnLanguageMst.langId = :langId ");

			// if (lStrLookupName.equals(lObjRsrcBndle.getString("CMN.TCBill")))
			// {
			// lSBQuery.append(" AND clm.parentLookupId = '" +
			// lObjRsrcBndle.getString("CMN.TCParentLookUp") + "'");
			// }

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lookupName", lStrLookupName);
			lQuery.setParameter("langId", lLngLangId);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lListQuery = lQuery.list();
			if (lListQuery != null) {
				lStrLookupText = (String) lListQuery.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getLookUpText : " + e, e);
			throw e;
		}
		logger.info("Returning Value from getLookUpText is : " + lStrLookupText);
		return lStrLookupText;
	}

	/**
	 * Method to get User ID from Post Id.
	 * 
	 * @param String
	 *            : postId
	 * @param Long
	 *            : lLngLangId
	 * @return String
	 * @author TCS
	 */
	public String getUserIdFromPost(String lStrPostId, Long lLngLangId) {

		Session hibSession = getSession();

		String str = "SELECT O.USER_ID userId FROM ORG_USERPOST_RLT O WHERE O.POST_ID = :postId AND O.ACTIVATE_FLAG = 1 ";

		SQLQuery query = hibSession.createSQLQuery(str);

		query.setCacheable(true).setCacheRegion("ecache_lookup");

		query.setLong("postId", Long.parseLong(lStrPostId));

		query.addScalar("userId", Hibernate.STRING);

		List lLstQList = query.list();
		Iterator it = lLstQList.iterator();
		String lStrUserId = null;
		Object tuple = null;

		while (it.hasNext()) {
			tuple = it.next();
			lStrUserId = tuple.toString();
		}

		return lStrUserId;
	}

	/**
	 * Method to get User Details for all the posts and levels passed.
	 * 
	 * @param String
	 *            [] : postId
	 * @param String
	 *            [] : levelId
	 * @param Long
	 *            : langId
	 * @return List
	 * @author TCS
	 */
	public List getUserFromPost(String[] postId, String[] levelId, Long lLngLangId) {

		Session hibSession = getSession();
		int lIntLoopI = 0;
		StringBuilder lQuery = new StringBuilder();
		// AND P.DSGN_CODE = MM.DSGN_CODE
		lQuery.append(" SELECT O.EMP_FNAME, O.EMP_LNAME, P.POST_ID,O.EMP_MNAME,MM.DSGN_NAME, L.LEVEL_DESC,R.USER_ID ");
		lQuery.append(" FROM ORG_EMP_MST O, ORG_USERPOST_RLT R ,ORG_DESIGNATION_MST  MM,ORG_POST_MST P, RLT_LEVEL_STATUS L ");
		lQuery.append(" WHERE  P.POST_ID = R.POST_ID  AND O.LANG_ID = :LANG_ID AND R.USER_ID != 100055 ");
		lQuery.append(" AND R.ACTIVATE_FLAG=1 " + " AND P.POST_ID IN(");

		for (lIntLoopI = 0; lIntLoopI < postId.length - 1; lIntLoopI++) {
			lQuery.append(postId[lIntLoopI] + ", ");
		}
		lQuery.append(postId[lIntLoopI] + ") AND R.USER_ID=O.USER_ID ");
		lQuery.append(" AND L.LEVEL_CODE = :LEVEL_CODE AND L.CATEGORY = :CATEGORY AND P.DSGN_CODE = MM.DSGN_ID");
		lQuery.append(" GROUP BY  P.POST_ID,O.EMP_FNAME,O.EMP_LNAME,O.EMP_MNAME,MM.DSGN_NAME, L.LEVEL_DESC,R.USER_ID");
		logger.info("Query to get related users as per posts for Multiple forward : :: " + lQuery);
		Query query = hibSession.createSQLQuery(lQuery.toString());
		query.setParameter("LANG_ID", lLngLangId);
		query.setParameter("LEVEL_CODE", levelId[0]);
		query.setParameter("CATEGORY", ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants").getString("PPROC.CATEGORY"));

		List lLstQList = query.list();
		Iterator it = lLstQList.iterator();
		List userList = new ArrayList();
		int lIntLoopJ = 0;
		Object[] tuple = null;
		String[] result = null;

		while (it.hasNext()) {
			tuple = (Object[]) it.next();
			result = new String[5];
			String middleName = tuple[3] != null ? tuple[3].toString() : "";
			String name = " " + tuple[0] + " " + middleName + " " + tuple[1] + " [" + tuple[4] + "]";
			result[0] = name;
			result[1] = tuple[2].toString();
			result[2] = levelId[0];
			result[3] = tuple[5].toString();
			result[4] = tuple[6].toString();

			userList.add(result);
			lIntLoopJ++;
		}
		logger.info("SIZE OF LIST FROM GET USERFROM POST :::: " + lLstQList);
		return userList;
	}

	/**
	 * Method to get Bill_Control_No from Bill No.
	 * 
	 * @param String
	 *            : billNo
	 * @return String
	 */
	public String getBillCntrlNoFromBillNo(String lStrBillNo) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		String lStrBillCtrlNo = null;
		try {
			lSBQuery.append("SELECT tbr.billCntrlNo FROM TrnBillRegister tbr");
			lSBQuery.append(" WHERE tbr.billNo = :billNo");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billNo", Long.parseLong(lStrBillNo));

			logger.info("Query for getBillCntrlNoFromBillNo : " + lQuery.toString());
			logger.info("Parameter BillNo : " + lStrBillNo);

			lListQuery = lQuery.list();
			if (lListQuery != null && lListQuery.size() > 0) {
				lStrBillCtrlNo = (String) lListQuery.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getBillCntrlNoFromBillNo : " + e, e);
			throw e;
		}
		logger.info("Returning Value from getBillCntrlNoFromBillNo : " + lStrBillCtrlNo);
		return lStrBillCtrlNo;
	}

	/**
	 * Method to check validity of Receipt No. And if it is valid then return
	 * related Receipt ID No for that Receipt No.
	 * 
	 * @param String
	 *            : lStrRcptNo
	 * @return long
	 */
	public long checkReceiptNo(String lStrRcptNo) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		Query lQuery = null;
		long rcptId = -1;
		try {
			lSBQuery.append("SELECT TRD.receiptNo FROM TrnReceiptDetails TRD, RltBillChallan RBC, TrnBillRegister TBR" + " WHERE TRD.receiptDetailId = RBC.challanNo AND RBC.billNo = TBR.billNo AND"
					+ " TBR.currBillStatus<>:rjctBillStatus" + " AND TRD.receiptNo = :receiptNo");

			lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("receiptNo", lStrRcptNo);
			lQuery.setShort("rjctBillStatus", DBConstants.ST_RJTD_BILL_DSPTCH);

			lListQuery = lQuery.list();
			if (lListQuery != null && lListQuery.size() > 0) {
				rcptId = -1;
			} else {
				rcptId = 1;
			}
		} catch (Exception e) {
			logger.error("Exception in checkReceiptNo : " + e, e);
			throw e;
		}
		logger.info("Returning Value from checkReceiptNo : " + lStrRcptNo);
		return rcptId;
	}

	/**
	 * To get total expenditure for the current financial year.
	 * 
	 * @param Map
	 *            : mp
	 * @return BigDecimal
	 */
	public BigDecimal getTotalGrossAmount(Map mp) throws Exception {

		String demandNo = (String) mp.get("demandCode");
		String mjrHd = (String) mp.get("majorHead");
		String subMjrHd = (String) mp.get("subMajorHead");
		String minorHd = (String) mp.get("minorHead");
		String subHd = (String) mp.get("subHead");
		String bdgtType = (String) mp.get("budgetType");
		String ddoCode = (String) mp.get("ddoCode");

		if (demandNo == null) {
			TrnBillRegister lObjTrnBillReg = (TrnBillRegister) mp.get("TrnBillRegister");
			if (lObjTrnBillReg != null) {
				demandNo = lObjTrnBillReg.getDemandCode();
				mjrHd = lObjTrnBillReg.getBudmjrHd();
				subMjrHd = lObjTrnBillReg.getBudSubmjrHd();
				minorHd = lObjTrnBillReg.getBudMinHd();
				subHd = lObjTrnBillReg.getBudSubHd();
				bdgtType = lObjTrnBillReg.getBudType().toString();
				ddoCode = lObjTrnBillReg.getDdoCode();
			}
		}
		try {
			SessionHelper.getLangId(mp);

			Session hibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT SUM(tbr.billGrossAmount) FROM TrnBillRegister tbr ");
			lSBQuery.append(" WHERE tbr.currBillStatus >=:aprvdStatus");
			lSBQuery.append(" AND tbr.demandCode=:demandCode AND tbr.budmjrHd=:budmjrHd");
			lSBQuery.append(" AND tbr.budSubmjrHd=:budSubmjrHd AND tbr.budMinHd=:budMinHd");
			lSBQuery.append(" AND tbr.budSubHd=:budSubHd AND tbr.finYearId=:finYearId");
			lSBQuery.append(" AND tbr.ddoCode=:ddoCode ");

			lSBQuery.append(" AND tbr.currBillStatus != '-1' AND tbr.currBillStatus != '250' ");
			lSBQuery.append(" AND tbr.currBillStatus != '80' AND tbr.currBillStatus != '120' ");

			if (bdgtType.equals("1") || bdgtType.equals("5") || bdgtType.equals("7")) {
				bdgtType = "1,5,7";
			} else if (bdgtType.equals("2") || bdgtType.equals("3") || bdgtType.equals("4")) {
				bdgtType = "2,3,4";
			} else if (bdgtType.equals("6") || bdgtType.equals("8") || bdgtType.equals("9")) {
				bdgtType = "6,8,9";
			}

			if (bdgtType != null && bdgtType.length() > 0) {
				lSBQuery.append(" AND tbr.budType IN (" + bdgtType + ")");
			}

			Query query = hibSession.createQuery(lSBQuery.toString());
			query.setString("demandCode", demandNo);
			query.setString("budmjrHd", mjrHd);
			query.setString("budSubmjrHd", subMjrHd);
			query.setString("budMinHd", minorHd);
			query.setString("budSubHd", subHd);
			query.setLong("finYearId", Long.valueOf(String.valueOf(SessionHelper.getFinYrId(mp))));
			query.setShort("aprvdStatus", DBConstants.ST_BS_TO);
			query.setString("ddoCode", ddoCode);
			// query.setShort("rejectStatus", DBConstants.ST_BRJCT_AUD);

			List lst = query.list();
			BigDecimal totalNetAmt = new BigDecimal(0);
			if (!lst.isEmpty() && lst.get(0) != null) {
				totalNetAmt = new BigDecimal(lst.get(0).toString());
			}

			logger.info("Total Net Amount is :::: " + totalNetAmt);
			return totalNetAmt;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * to get employee name from user id
	 * 
	 * @param lStrUserId
	 * @param lLngLangId
	 * @return
	 */

	public String getEmpNameFrmUserId(String lStrUserId, Long lLngLangId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		String lStrEmpName = null;
		try {
			lSBQuery.append("SELECT oem.empPrefix, oem.empFname, oem.empMname, oem.empLname FROM OrgEmpMst oem");
			lSBQuery.append(" WHERE oem.orgUserMst.userId = :userId");
			lSBQuery.append(" AND oem.cmnLanguageMst.langId = :langId");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lQuery.setParameter("userId", Long.parseLong(lStrUserId));
			lQuery.setParameter("langId", lLngLangId);

			logger.info("Query for getEmpNameFrmUserId : " + lQuery.toString());
			logger.info("Parameter lStrChallanId : " + lStrUserId + "  " + lLngLangId);

			lListQuery = lQuery.list();
			logger.info("Value of List from getEmpNameFrmUserId : " + lListQuery);
			if (lListQuery != null) {
				Iterator it = lListQuery.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					lStrEmpName = ((obj[0] != null) ? obj[0] : "") + " " + ((obj[1] != null) ? obj[1] : "") + " " + ((obj[2] != null) ? obj[2] : "") + " " + ((obj[3] != null) ? obj[3] : "");
				}
			}
		} catch (Exception e) {
			logger.error("Exception in getEmpNameFrmUserId : " + e, e);
			throw e;
		}
		logger.info("Returning Value from getEmpNameFrmUserId : " + lStrEmpName);
		return lStrEmpName;
	}

	/**
	 * Getting lookup id from lookup name
	 * 
	 * @param String
	 *            : lookupName
	 * @param Long
	 *            : lLngLangId
	 * @return String
	 */

	public String getLookupIdFromName(String lookupName, Long lLngLangId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		String lStrLookupId = null;
		try {
			lSBQuery.append("SELECT clm.lookupId FROM CmnLookupMst clm");
			lSBQuery.append(" WHERE clm.lookupName = :lookupName ");
			lSBQuery.append(" AND clm.cmnLanguageMst.langId = :langId");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lQuery.setParameter("lookupName", lookupName);
			lQuery.setParameter("langId", lLngLangId);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			logger.info("Query for getLookupIdFromName : " + lQuery.toString());
			logger.info("Parameter : " + lookupName + "  " + lLngLangId);

			lListQuery = lQuery.list();
			if (lListQuery != null) {
				logger.info("Value of list.get(0) : " + lListQuery.get(0));
				if (lListQuery.get(0) != null && lListQuery.get(0).toString().length() > 0) {
					lStrLookupId = String.valueOf(lListQuery.get(0));
					logger.info("lStrLookupId : " + lStrLookupId);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in getLookupIdFromName : " + e, e);
			throw e;
		}
		return lStrLookupId;
	}

	/**
	 * This is to check whether bill is physical or online
	 * 
	 * @param Long
	 *            :billNo
	 * @return boolean
	 */
	public boolean isPhyBill(Long billNo) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		boolean phyBill = true;
		try {
			lSBQuery.append("SELECT tbr.phyBill FROM TrnBillRegister tbr ");
			lSBQuery.append(" WHERE tbr.billNo = :billNo ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billNo", billNo);

			lListQuery = lQuery.list();
			if (lListQuery != null && lListQuery.size() > 0) {
				if (lListQuery.get(0) != null && lListQuery.get(0).toString().length() > 0) {
					String lStrPhyFlag = lListQuery.get(0).toString();
					if (lStrPhyFlag.equals("1")) {
						phyBill = true;
					} else {
						phyBill = false;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in isPhyBill : " + e, e);
			throw e;
		}
		return phyBill;
	}

	public void updCouterReferenceRange(Long postId, String locCode, Long updateBy, String userType) throws Exception {

		StringBuilder lsb = new StringBuilder();
		try {
			Session hibSession = getSession();
			lsb.append(" UPDATE MstCounterReferenceRange ");
			lsb.append(" SET currCounterNo=:currCounterNo");
			lsb.append(" where postId=:postId");
			lsb.append(" and locationCode=:locationCode");
			lsb.append(" and userType=:userType");

			Query lQuery = hibSession.createQuery(lsb.toString());
			lQuery.setLong("currCounterNo", updateBy);
			lQuery.setLong("postId", postId);
			lQuery.setString("locationCode", locCode);
			lQuery.setShort("userType", Short.parseShort(userType));
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void updCouterReferenceRangeForTC(String locCode, Long updateBy) throws Exception {

		StringBuilder lsb = new StringBuilder();
		try {
			Session hibSession = getSession();
			lsb.append(" UPDATE MstCounterReferenceRange ");
			lsb.append(" SET currCounterNo=:currCounterNo");
			lsb.append(" WHERE locationCode=:locationCode");
			/*
			 * lsb.append(" UPDATE MstCounterReferenceRange ");
			 * lsb.append(" SET currCounterNo=" + updateBy);
			 * lsb.append(" where locationCode='" + locCode + "'");
			 */
			lsb.append(" and userType=11");

			Query lQuery = hibSession.createQuery(lsb.toString());
			lQuery.setLong("currCounterNo", updateBy);
			// lQuery.setLong("postId", postId);
			lQuery.setString("locationCode", locCode);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public Long getTotalBillsInwardedByMe(String lStrLocCode, Long lLngPostId, boolean flag) {

		StringBuilder lSBQuery = new StringBuilder();
		Session hibSession = getSession();
		List lListQueryRes = null;
		Query lQueryRes = null;
		Long lLngBillsByMe = null;

		lSBQuery.append("SELECT COUNT(billNo) FROM TrnBillRegister ");
		lSBQuery.append("WHERE inwardDt = :currDate ");
		lSBQuery.append("AND tsryOfficeCode = :tsryOfficeCode ");
		lSBQuery.append("AND inwardedPost=:postId");
		lSBQuery.append(" AND refNum IS NOT NULL ");

		lQueryRes = hibSession.createQuery(lSBQuery.toString());
		lQueryRes.setString("tsryOfficeCode", lStrLocCode);
		lQueryRes.setDate("currDate", SessionHelper.getCurDate());
		lQueryRes.setLong("postId", lLngPostId);

		lListQueryRes = lQueryRes.list();
		if (lListQueryRes != null && lListQueryRes.size() > 0) {
			Iterator it = lListQueryRes.iterator();
			if (it.hasNext()) {
				lLngBillsByMe = (Long) it.next();
			}
		}
		if (flag) {
			return (lLngBillsByMe + 1);
		} else {
			return lLngBillsByMe;
		}
	}

	public Long[] getMaxReferenceNo(String lStrLocCode, Long lLngPostId, boolean flag) {

		Session hibSession = getSession();

		StringBuilder lSBQuery = new StringBuilder();

		Object lObjRes[] = null;

		Long lLngMaxRefNo = null;
		Long lLngStartRng = null;
		Long lLngEndRng = null;

		lSBQuery.append("SELECT startCounterNo,endCounterNo FROM MstCounterReferenceRange WHERE locationCode = :locationCode AND postId = :postId and userType=1");

		Query query = hibSession.createQuery(lSBQuery.toString());
		query.setCacheable(true).setCacheRegion("ecache_lookup");

		query.setParameter("locationCode", lStrLocCode);
		query.setParameter("postId", lLngPostId);

		List lLstAdvRange = query.list();

		if (!lLstAdvRange.isEmpty()) {
			lObjRes = (Object[]) lLstAdvRange.get(0);
			if (lObjRes[0] != null && lObjRes[1] != null) {
				lLngStartRng = Long.parseLong(lObjRes[0].toString());
				lLngEndRng = Long.parseLong(lObjRes[1].toString());
			}
		}

		if (lLngStartRng != null && lLngEndRng != null) {

			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(refNum) FROM TrnBillRegister WHERE inwardDt = :currDate AND tsryOfficeCode = :tsryOfficeCode " + "AND refNum BETWEEN :startRange AND :endRange");// +
			// lLngStartRng
			// +
			// " AND "
			// +
			// lLngEndRng);

			query = hibSession.createQuery(lSBQuery.toString()).setDate("currDate", SessionHelper.getCurDate()).setString("tsryOfficeCode", lStrLocCode);
			query.setParameter("startRange", lLngStartRng);
			query.setParameter("endRange", lLngEndRng);
			List refList = query.list();

			if (!refList.isEmpty() && refList.get(0) != null && refList.get(0).toString().length() > 0) {
				lLngMaxRefNo = Long.parseLong(refList.get(0).toString());
			}

			if (lLngMaxRefNo != null) {
				if (lLngMaxRefNo != lLngEndRng) {
					if (flag) {
						lLngMaxRefNo++;
					}
				} else {
					lLngMaxRefNo = -1L;
				}
			} else {
				lLngMaxRefNo = lLngStartRng;
			}

		} else {
			lLngMaxRefNo = -1L;
		}

		Long[] lLngRefNums = new Long[3];
		lLngRefNums[0] = lLngMaxRefNo;
		lLngRefNums[1] = lLngStartRng;
		lLngRefNums[2] = lLngEndRng;

		return lLngRefNums;
	}

	/**
	 * To get maximum reference number for that particular day TO wise
	 * 
	 * @param String
	 *            : locationCode
	 * @return Long
	 */
	/*
	 * public synchronized Long getMaxRefNumLoad(String locationCode, Long
	 * postId) {
	 * 
	 * Session hibSession = getSession();
	 * 
	 * String lStrQuery = GET_MAX_REF_NUM;
	 * 
	 * if (postId != null) lStrQuery += " AND createdPostId = :createdPostId ";
	 * 
	 * Query query = hibSession.createQuery(lStrQuery).setDate("currDate",
	 * SessionHelper.getCurDate()).setString("tsryOfficeCode", locationCode);
	 * 
	 * if (postId != null) query.setLong("createdPostId", postId);
	 * 
	 * List resultList = query.list(); Long nextRefNum = Long.valueOf(0);
	 * 
	 * if (resultList != null && resultList.size() > 0) { Object obj =
	 * resultList.get(0); if (obj != null) nextRefNum =
	 * Long.parseLong(obj.toString()); }
	 * 
	 * return nextRefNum; }
	 */
	public Long getTotalBillsInwardToday(String lStrLocCode, boolean flag) {

		Session hibSession = getSession();
		StringBuilder lSBQuery = new StringBuilder();
		Long lLngTotalBillsInward = Long.valueOf(0);
		lSBQuery.append(" SELECT COUNT(tbr.billNo) FROM TrnBillRegister tbr ");
		lSBQuery.append(" WHERE tbr.tsryOfficeCode=:tsryOfficeCode ");
		lSBQuery.append(" AND tbr.inwardDt=:inwardDt ");
		lSBQuery.append(" AND tbr.refNum IS NOT NULL ");

		Query lQuery = hibSession.createQuery(lSBQuery.toString());
		lQuery.setString("tsryOfficeCode", lStrLocCode);
		lQuery.setDate("inwardDt", SessionHelper.getCurDate());

		List lListQuery = lQuery.list();
		if (lListQuery != null && lListQuery.size() > 0) {
			Iterator it = lListQuery.iterator();
			if (it.hasNext()) {
				lLngTotalBillsInward = (Long) it.next();
			}
		}
		if (flag) {
			return (lLngTotalBillsInward + 1);
		} else {
			return lLngTotalBillsInward;
		}
	}

	/**
	 * Method to get Subject Id from bill Type
	 * 
	 * @param String
	 *            : lStrBillType
	 * @return Long
	 */
	public Long getSubIdFromBillType(String lStrBillType) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		Long lLngSubjectId = null;
		try {
			lSBQuery.append("SELECT mbt.subjectId FROM MstBillType mbt ");
			lSBQuery.append(" WHERE mbt.billShrtCode = :billShrtCode");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billShrtCode", lStrBillType);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lListQuery = lQuery.list();
			if (lListQuery != null && lListQuery.size() > 0) {
				lLngSubjectId = (Long) lListQuery.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getSubIdFromBillType : " + e, e);
			throw e;
		}
		logger.info("Returning Value from getSubIdFromBillType : " + lLngSubjectId);
		return lLngSubjectId;
	}

	/**
	 * Method to get Bill Type from Subject Id
	 * 
	 * @param Long
	 *            : lLngSubjectId
	 * @return String
	 */
	public String getBillTypeFromSubId(Long lLngSubjectId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List lListQuery = null;
		String lStrBillType = null;
		try {
			lSBQuery.append("SELECT mbt.billShrtCode FROM MstBillType mbt");
			lSBQuery.append(" WHERE mbt.subjectId = :subjectId");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("subjectId", lLngSubjectId);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lListQuery = lQuery.list();
			if (lListQuery != null && lListQuery.size() > 0) {
				lStrBillType = (String) lListQuery.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getBillTypeFromSubId : " + e, e);
			throw e;
		}
		logger.info("Returning Value from getBillTypeFromSubId : " + lStrBillType);
		return lStrBillType;
	}

	public List<CmnLookupMst> getAllChildrenByLookUpNameAndLang(String lStrLookupName, Long lLngLangId) throws Exception {

		try {

			Query lQuery = getSession().createQuery(GET_LOOK_UP_VALUES);
			lQuery.setString("lookupName", lStrLookupName);
			lQuery.setLong("langId", lLngLangId);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			logger.info("getAllChildrenByLookUpNameAndLang----"+lQuery.toString());
			return lQuery.list();

		} catch (Exception ex) {
			logger.error("Exception in getAllChildrenByLookUpNameAndLang : " + ex, ex);
			throw ex;
		}

	}

	public List getOtherUserList(Long lLngHierRefId, Long lLngBillNo, Long lLngLevelId, Long lLngLangId, String auditFlg) throws Exception {

		try {
			StringBuilder lSbQuery = new StringBuilder();
			List lLstRsltSet = null;

			lSbQuery.append("SELECT L.LEVEL_CODE levelCode," + " CONCAT(L.LEVEL_DESC, CONCAT( ' - ' ,CONCAT(RTRIM(CAST(E.EMP_FNAME AS CHAR(20))),"
					+ " CONCAT(CONCAT(' ', RTRIM(CAST(E.EMP_MNAME AS CHAR(20)))), " + " CONCAT(' ', RTRIM(CAST(E.EMP_LNAME AS CHAR(20)))))))) EMP_NAME,"
					+ " WP.POST_ID postId,L.STATUS_CODE statusCode,L.LEVEL_DESC levelDesc" + " FROM WF_HIERACHY_POST_MPG WP, ORG_USERPOST_RLT UP, ORG_EMP_MST E, RLT_LEVEL_STATUS L,TRN_BILL_MVMNT B,"
					+ " TRN_BILL_REGISTER BR" + " WHERE WP.HIERACHY_REF_ID = :hierRefId AND WP.POST_ID = UP.POST_ID AND UP.USER_ID = E.USER_ID "
					+ " AND E.LANG_ID = :langId AND  WP.LANG_ID = E.LANG_ID AND BR.HIERARCHY_REF_ID = WP.HIERACHY_REF_ID AND"
					+ " L.STATUS_CODE = B.MVMNT_STATUS AND B.BILL_NO = :billNo AND L.LEVEL_CODE = WP.LEVEL_ID AND " + " L.LEVEL_CODE !=:currLevelId ");
			if (auditFlg != null && auditFlg.equals("yes")) {
				lSbQuery.append(" AND L.LEVEL_CODE = 50");
			}
			lSbQuery.append(" AND L.CATEGORY = :category AND BR.BILL_NO = B.BILL_NO AND B.MVMNT_STATUS < BR.CURR_BILL_STATUS" + " AND wp.activate_flag=1 AND up. activate_flag=1 "
					+ " GROUP BY L.LEVEL_CODE,L.LEVEL_DESC, E.EMP_FNAME, E.EMP_MNAME,E.EMP_LNAME,WP.POST_ID,L.STATUS_CODE," + " L.LEVEL_DESC ORDER BY L.LEVEL_CODE");

			SQLQuery lQuery = getSession().createSQLQuery(lSbQuery.toString());
			lQuery.setLong("hierRefId", lLngHierRefId);
			lQuery.setLong("langId", lLngLangId);
			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setLong("currLevelId", lLngLevelId);
			lQuery.setString("category", DBConstants.CATEGORY_BILL);

			lQuery.addScalar("levelCode", Hibernate.LONG);
			lQuery.addScalar("EMP_NAME", Hibernate.STRING);
			lQuery.addScalar("postId", Hibernate.LONG);
			lQuery.addScalar("statusCode", Hibernate.SHORT);
			lQuery.addScalar("levelDesc", Hibernate.STRING);

			lLstRsltSet = lQuery.list();
			return lLstRsltSet;
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw e;
		}
	}

	public List getCurrLevelFrmStatus(String lStrCurrStatus, String category) throws Exception {

		try {
			Query lQuery = getSession().createQuery(GET_LEVEL_FRM_STATUS);
			lQuery.setShort("statusCode", Short.valueOf(lStrCurrStatus));
			lQuery.setString("category", category);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			return lQuery.list();
		} catch (Exception ex) {
			logger.error("Exception in getCurrLevelFrmStatus : " + ex, ex);
			throw ex;
		}
	}

	public TrnBillMvmnt getLastBillMvmntFrmBillNo(Long lLngBillNo) throws Exception {

		try {
			List rsltList = null;
			TrnBillMvmnt lObjTrnBillMvmnt = null;

			Query lQuery = getSession().createQuery(GET_BILL_MVMNT);
			lQuery.setLong("billNo", lLngBillNo);

			rsltList = lQuery.list();

			if (rsltList != null && !rsltList.isEmpty()) {
				lObjTrnBillMvmnt = (TrnBillMvmnt) rsltList.get(0);
			}
			return lObjTrnBillMvmnt;
		} catch (Exception ex) {
			logger.error("Exception in getLastBillMvmntFrmBillNo : " + ex, ex);
			throw ex;
		}
	}
}