package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.IFMSCommonDAOImpl;
import com.tcs.sgv.common.helper.ReportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.pensionpay.constants.PensionConstants;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.SupplementaryPartyDtlsVO;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls;


//import com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO;

/**
 * PensionBillDAOImpl Its a DAO for Pension Bill Preparation.
 * 
 * @author Sagar Patel
 * @version 1.0
 */

public class PensionBillDAOImpl implements PensionBillDAO {

	/* Global Variable for Logger Class */
	private Log logger = LogFactory.getLog(getClass());

	/* Global Variable for Session Class */

	private SessionFactory sessionFactory = null;

	public PensionBillDAOImpl(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {

		boolean allowCreate = true;
		return SessionFactoryUtils.getSession(sessionFactory, allowCreate);
	}

	private static final SimpleDateFormat oSimpleFormat = new SimpleDateFormat("dd/MM/yyyy");

	private static final SimpleDateFormat oMySQLFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	private ResourceBundle bundleBillConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	/**
	 * Get Claimant inforation by post.
	 * 
	 * @param Long
	 *            lClaimantpostID
	 * @return lDDOInfo List
	 */
	public List getClaimantDDOInfo(Long lLngLocCode, Long langId) throws Exception {

		List lDDOInfo = null;

		try {
			StringBuffer lSBQuery = new StringBuffer();
			Session ghibSession = getSession();
			lSBQuery.append(" select concat(concat(concat(concat(em.empFname,' '),em.empMname),' '),em.empLname),dm.dsgnName, ddm.ddoName, ddm.ddoCode,ddm.cardexNo "
					+ " from OrgDesignationMst dm, OrgEmpMst em, OrgUserpostRlt up,  OrgPostDetailsRlt pd, CmnLocationMst cl, OrgDdoMst ddm"
					+ " where dm.dsgnId = pd.orgDesignationMst.dsgnId and em.orgUserMst.userId = up.orgUserMst.userId "
					+ " AND up.activateFlag=:activateFlag AND up.orgPostMstByPostId.postId = pd.orgPostMst.postId and ddm.postId = pd.orgPostMst.postId and"
					+ " pd.cmnLocationMst.locId = cl.locId and cl.locationCode =:locCode " + " and em.cmnLanguageMst.langId=:langId and em.activateFlag =:activateFlag ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("activateFlag", 1L);
			lQuery.setLong("locCode", lLngLocCode);
			lQuery.setLong("langId", langId);
			lDDOInfo = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lDDOInfo;
	}

	public List getOtherUsersList(String lStrLevel, String lStrHirarchyRefId, String lStrOtherType, String lStrPostId, String lStrCategry, String lStrSubName, String lStrMRFlowFlag, Long lLngLangId)
			throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session ghibSession = getSession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery.append(" SELECT DISTINCT RLS.LEVEL_CODE,RLS.LEVEL_DESC,WPM.POST_ID,OUM.Emp_Prefix,OUM.EMP_FNAME,OUM.EMP_MNAME,OUM.EMP_LNAME "
					+ " FROM RLT_LEVEL_STATUS RLS,WF_HIERACHY_POST_MPG WPM , ORG_EMP_MST OUM,ORG_USERPOST_RLT OUPR ");

			// changed by Soumya
			if (lStrMRFlowFlag != null && lStrMRFlowFlag.equals("Y")) {
				lSBQuery.append(" WHERE RLS.CATEGORY = '" + lStrCategry + "' ");
				lSBQuery.append(" AND WPM.LEVEL_ID = RLS.LEVEL_CODE ");
				lSBQuery.append(" AND WPM.HIERACHY_REF_ID = " + lStrHirarchyRefId + " ");
				lSBQuery.append(" AND OUPR.POST_ID = WPM.POST_ID ");
				lSBQuery.append(" AND OUM.USER_ID = OUPR.USER_ID ");
				lSBQuery.append(" AND WPM.LEVEL_ID = 90 AND OUM.LANG_ID =:LangId AND OUPR.activate_flag=1 AND WPM.activate_flag=1 AND OUM.activate_flag=1");
			} else {
				if (lStrOtherType != null && lStrOtherType.equals("STOCNTR")) {
					lSBQuery.append(",ACL_POSTROLE_RLT APR ");
				}
				if (lStrOtherType != null && lStrOtherType.equals("SP")) {
					lSBQuery.append(" WHERE WPM.LEVEL_ID =:levelCode  and WPM.POST_ID !=:wpmPostId AND RLS.CATEGORY =:category AND ");
				} else if (lStrOtherType != null && lStrOtherType.equals("SOT")) {
					if (PensionConstants.PENSIONCASESUBJECT.equalsIgnoreCase(lStrSubName)) {
						if (Integer.parseInt(lStrLevel) >= 50) {
							lSBQuery.append(" WHERE WPM.LEVEL_ID <:levelCode AND WPM.LEVEL_ID >=:levelCode50  AND RLS.CATEGORY =:category AND ");
						} else {
							lSBQuery.append(" WHERE WPM.LEVEL_ID <:levelCode AND RLS.CATEGORY =:category AND ");
						}
					} else {
						lSBQuery.append(" WHERE WPM.LEVEL_ID <:levelCode AND RLS.CATEGORY =:category AND ");
					}
				} else if (lStrOtherType != null && lStrOtherType.equals("STOCNTR")) {
					lSBQuery.append(" WHERE WPM.LEVEL_ID =:levelCode20 AND APR.POST_ID = WPM.POST_ID AND APR.ROLE_ID =:RoleIdFP AND RLS.CATEGORY =:category AND ");
				} else if (lStrOtherType != null && lStrOtherType.equals("STOFRWD")) {
					lSBQuery.append(" WHERE WPM.LEVEL_ID >:levelCode AND WPM.LEVEL_ID <:levelCode90 AND RLS.CATEGORY =:category  AND ");
				}
				lSBQuery.append(" WPM.LEVEL_ID = RLS.LEVEL_CODE ");
				if (lStrSubName != null && lStrSubName.equals("PensionBill Processing")) {
					lSBQuery.append(" AND WPM.LEVEL_ID >=:levelCode30 ");
				}
				lSBQuery.append(" AND WPM.HIERACHY_REF_ID =:HirRefId  AND OUPR.POST_ID = WPM.POST_ID AND OUM.USER_ID = OUPR.USER_ID AND OUM.LANG_ID =:LangId AND OUPR.activate_flag=1 AND WPM.activate_flag=1 AND OUM.activate_flag=1 ORDER BY RLS.LEVEL_CODE ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			if (lStrMRFlowFlag == null || !lStrMRFlowFlag.equals("Y")) {
				if (lStrOtherType != null && lStrOtherType.equals("SP")) {
					lQuery.setInteger("levelCode", Integer.parseInt(lStrLevel));
					lQuery.setBigDecimal("wpmPostId", new BigDecimal(lStrPostId));
					lQuery.setString("category", lStrCategry);
				} else if (lStrOtherType != null && lStrOtherType.equals("SOT")) {
					if (PensionConstants.PENSIONCASESUBJECT.equalsIgnoreCase(lStrSubName)) {
						if (Integer.parseInt(lStrLevel) >= 50) {
							lQuery.setInteger("levelCode", Integer.parseInt(lStrLevel));
							lQuery.setInteger("levelCode50", 50);
							lQuery.setString("category", lStrCategry);
						} else {
							lQuery.setInteger("levelCode", Integer.parseInt(lStrLevel));
							lQuery.setString("category", lStrCategry);
						}
					} else {
						lQuery.setInteger("levelCode", Integer.parseInt(lStrLevel));
						lQuery.setString("category", lStrCategry);
					}
				} else if (lStrOtherType != null && lStrOtherType.equals("STOCNTR")) {
					lQuery.setInteger("levelCode20", 20);
					lQuery.setString("category", lStrCategry);
					lQuery.setLong("RoleIdFP", 210004);
				} else if (lStrOtherType != null && lStrOtherType.equals("STOFRWD")) {
					lQuery.setInteger("levelCode90", 90);
					lQuery.setInteger("levelCode", Integer.parseInt(lStrLevel));
					lQuery.setString("category", lStrCategry);
				}
				if (lStrSubName != null && lStrSubName.equals(PensionConstants.ONLINEBILLSUBJECT)) {
					lQuery.setInteger("levelCode30", 30);
				}
				lQuery.setString("HirRefId", lStrHirarchyRefId);
			}
			lQuery.setLong("LangId", lLngLangId);

			List lLstQList = lQuery.list();
			Iterator it = lLstQList.iterator();
			int lIntLoopJ = 0;
			Object[] tuple = null;
			String[] result = null;
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				result = new String[4];
				result[0] = tuple[0].toString();
				result[1] = tuple[1].toString();
				result[2] = tuple[2].toString();
				if (tuple[3] != null) {
					result[3] = tuple[3].toString();
				}
				if (tuple[4] != null) {
					result[3] = tuple[4].toString();
				}
				if (tuple[5] != null) {
					result[3] = result[3] + " " + tuple[5].toString();
				}
				if (tuple[6] != null) {
					result[3] = result[3] + " " + tuple[6].toString();
				}
				lLstRes.add(result);
				lIntLoopJ++;
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstRes;
	}

	/**
	 * Get Pension Bills Party Details...
	 * 
	 * @author Sagar.
	 */

	public List<RltBillParty> getPensionerPartyDtls(Map inputMap) throws Exception {

		String lPnsnsBillType = inputMap.get("subjectId").toString();

		String lPnsnCode = null;
		String lStrPnsnBillType = PensionConstants.BILLTYPEPENSION;
		String lStrCVPBillType = PensionConstants.BILLTYPECVP;
		String lStrDCRGBillType = PensionConstants.BILLTYPEDCRG;
		String lStrStatus = null;

		if (inputMap.containsKey("PensionType")) // Monthly Bill ID
		{
			inputMap.get("PensionType").toString();
		}
		if (inputMap.containsKey("PensionScheme")) // MR Bill ID
		{
			inputMap.get("PensionScheme").toString();
		}

		RltBillParty lObjRltBillPartyVO = null;
		List<RltBillParty> lLstRltBillPartyVO = new ArrayList<RltBillParty>();
		MstPensionerHdr lObjPensionerHdr = null;

		List lresNomineeList = null;
		List lresFmlyMemberList = null;

		try {
			Session ghibSession = getSession();
			if (("44".equals(lPnsnsBillType)) || ("43".equals(lPnsnsBillType))) {
				lObjRltBillPartyVO = new RltBillParty();

				StringBuffer lSBQuery = new StringBuffer();
				lSBQuery.append("SELECT  rbb.branch_Name,mb.bank_Name,rbb.micr_Code FROM Mst_Bank mb, Rlt_Bank_Branch rbb WHERE mb.bank_Code = rbb.bank_Code    "
						+ " AND mb.lang_Id = :lLng  AND rbb.location_Code = :lLocCode AND rbb.branch_Code = :lBranchCode ");

				SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

				lQuery.addScalar("branch_Name", Hibernate.STRING);
				lQuery.addScalar("bank_Name", Hibernate.STRING);
				lQuery.addScalar("micr_Code", Hibernate.BIG_DECIMAL);

				lQuery.setLong("lBranchCode", Long.parseLong(inputMap.get("Branch").toString()));
				lQuery.setString("lLocCode", SessionHelper.getLocationCode(inputMap).toString());
				lQuery.setLong("lLng", SessionHelper.getLangId(inputMap));

				List lLstBank = lQuery.list();

				Object[] lObjects = (Object[]) lLstBank.get(0);
				String lBranchName = (String) lObjects[1] + "," + (String) lObjects[0];
				BigDecimal lMicrCode = null;
				if (lObjects[2] != null) {
					lMicrCode = (BigDecimal) lObjects[2];
				}

				lObjRltBillPartyVO.setPartyName(lBranchName.toUpperCase());
				if (lObjects[2] != null) {
					lObjRltBillPartyVO.setMicrCode(new Long(lMicrCode.toString()));
				}
				// lObjRltBillPartyVO.setPartyAddr(lBankName);
				Double amount = 0D;
				if (inputMap.get("TotalNetAmt") != null) {
					amount = Double.parseDouble(inputMap.get("TotalNetAmt").toString());
				}
				long lLngAmount = Math.round(amount);
				lObjRltBillPartyVO.setPartyAmt(new BigDecimal(lLngAmount));
				lObjRltBillPartyVO.setPaymentMode("ECS");

				lLstRltBillPartyVO.add(lObjRltBillPartyVO);

				if (inputMap.containsKey("lstParty") && inputMap.containsKey("PartyAmntMap")) {

					List lstPartyName = (List) inputMap.get("lstParty");
					Map OthPartyAmnt = (Map) inputMap.get("PartyAmntMap");
					String lStrAmnt = null;

					for (int i = 0; i < lstPartyName.size(); i++) {
						lObjRltBillPartyVO = new RltBillParty();
						String lPartyNm = lstPartyName.get(i).toString().split("~")[1];
						String lnType = lstPartyName.get(i).toString().split("~")[0];

						if (OthPartyAmnt.containsKey(Long.parseLong(lnType))) {
							lStrAmnt = (String.valueOf(OthPartyAmnt.get(Long.parseLong(lnType))));
						}

						lObjRltBillPartyVO.setPartyName(lPartyNm);
						lObjRltBillPartyVO.setPartyAmt(new BigDecimal(lStrAmnt));
						lObjRltBillPartyVO.setPaymentMode("ECS");
						lLstRltBillPartyVO.add(lObjRltBillPartyVO);
					}
				}
			} else {
				String lPnsnPPONo = inputMap.get("PPONo").toString();
				lPnsnCode = getPnsnCodeFromPPONo(lPnsnPPONo, PensionConstants.STATUSCONTINUE, SessionHelper.getLocationCode(inputMap).toString());
				if (lPnsnCode != null) {
					StringBuffer lSBQuery = new StringBuffer();

					lSBQuery.append(" FROM MstPensionerHdr A WHERE A.pensionerCode = :lPensionerCode ");

					Query lQuery = ghibSession.createQuery(lSBQuery.toString());

					lQuery.setString("lPensionerCode", lPnsnCode);

					// lStrStatus = "APPROVED";
					// lQuery.setString("lstatus", lStrStatus);

					lObjPensionerHdr = (MstPensionerHdr) lQuery.list().get(0);

					List lLstAcNoDtls = getPartyAcNoDtls(lObjPensionerHdr.getPensionerCode(), lStrStatus);
					Iterator lItlLstAcNoDtls = lLstAcNoDtls.iterator();
					String lStrAccountNO = null;
					Long lLngMicrCode = null;
					String lStrBankCode = null;
					String lStrBranchCode = null;
					while (lItlLstAcNoDtls.hasNext()) {
						Object[] lObj = (Object[]) lItlLstAcNoDtls.next();
						lStrAccountNO = (String) lObj[0];
						lLngMicrCode = (Long) lObj[1];
						lStrBankCode = (String) lObj[2];
						lStrBranchCode = (String) lObj[3];
					}

					if (lObjPensionerHdr.getDateOfDeath() != null) {
						if (lPnsnsBillType.equalsIgnoreCase(lStrDCRGBillType)) {
							lresNomineeList = getNomineeDtls(lPnsnCode);
							String lStrMicrCode = "";
							Long lLngMicrCodeForNominee = null;
							if (lresNomineeList != null) {
								inputMap.put("ValidNomineeFlag", "Y");
								Double lTotPaidAmt = 0d;
								for (int i = 0; i < lresNomineeList.size(); i++) {
									MstPensionerNomineeDtls lTempObj = (MstPensionerNomineeDtls) lresNomineeList.get(i);
									lObjRltBillPartyVO = new RltBillParty();
									lObjRltBillPartyVO.setPartyName(lTempObj.getName().toString().toUpperCase());
									lObjRltBillPartyVO.setAccntNo((lTempObj.getAccountNo() != null) ? lTempObj.getAccountNo() : "");
									if (lTempObj.getBranchCode() != null) {
										lStrMicrCode = getIfscCodeFromBranchCode(lTempObj.getBranchCode(), lTempObj.getLocationCode());
										if (lStrMicrCode != null && lStrMicrCode != "") {
											lLngMicrCodeForNominee = Long.parseLong(lStrMicrCode);
										}
										lObjRltBillPartyVO.setMicrCode(lLngMicrCodeForNominee);
										lObjRltBillPartyVO.setBankCode(lTempObj.getBankCode());
										lObjRltBillPartyVO.setBranchCode(lTempObj.getBranchCode().toString());
									}
									Long tPartyAmt = (Long) inputMap.get("NetAmount");

									if (lresNomineeList.size() == 1) {
										lObjRltBillPartyVO.setPartyAmt(new BigDecimal(tPartyAmt));
									} else {
										Double lPayableAmt = tPartyAmt.doubleValue();
										lPayableAmt = (lPayableAmt * (Double.valueOf(lTempObj.getPercent().toString()))) / 100;
										lPayableAmt = Double.valueOf(new DecimalFormat("0.00").format(lPayableAmt));
										lPayableAmt = Double.valueOf(Math.round(lPayableAmt));

										if (i == 0) {
											lTotPaidAmt += lPayableAmt;
											lObjRltBillPartyVO.setPartyAmt(new BigDecimal(lPayableAmt));
										} else {
											// lPayableAmt =
											// Math.floor(lPayableAmt);
											lTotPaidAmt += lPayableAmt;

											if (lresNomineeList.size() == i) {
												lPayableAmt += tPartyAmt - lPayableAmt;
												lPayableAmt = Double.valueOf(Math.round(lPayableAmt));
											}
											lObjRltBillPartyVO.setPartyAmt(new BigDecimal(lPayableAmt));
										}
									}
									lObjRltBillPartyVO.setPaymentMode("ECS");

									lLstRltBillPartyVO.add(lObjRltBillPartyVO);
								}
							} else {
								/*
								 * If no valid nomineed is present than showing
								 * alert to enter nominee details.
								 */
								inputMap.put("ValidNomineeFlag", "N");
								// Pensioner is dead but and Family Pensioner is
								// not
								// existed.
								// so we pay pension till he/she live on the
								// name of
								// pensioner
								/*
								 * lObjRltBillPartyVO = new RltBillParty();
								 * String lPartName = null; lPartName =
								 * (lObjPensionerHdr.getFirstName() != null &&
								 * lObjPensionerHdr.getFirstName().length() > 0)
								 * ? lObjPensionerHdr.getFirstName() : "";
								 * lPartName +=
								 * (lObjPensionerHdr.getMiddleName() != null &&
								 * lObjPensionerHdr.getMiddleName().length() >
								 * 0) ? lObjPensionerHdr.getMiddleName() : "";
								 * lPartName += (lObjPensionerHdr.getLastName()
								 * != null &&
								 * lObjPensionerHdr.getLastName().length() > 0)
								 * ? lObjPensionerHdr.getLastName() : "";
								 * 
								 * if (lObjPensionerHdr.getMiddleName() != null
								 * && lObjPensionerHdr.getMiddleName().length()
								 * > 0) { lPartName =
								 * lObjPensionerHdr.getFirstName() + " " +
								 * lObjPensionerHdr.getMiddleName() + " " +
								 * lObjPensionerHdr.getLastName(); } else {
								 * lPartName = lObjPensionerHdr.getFirstName() +
								 * " " + lObjPensionerHdr.getLastName(); }
								 * 
								 * 
								 * 
								 * 
								 * 
								 * 
								 * 
								 * lObjRltBillPartyVO.setPartyName(lPartName.
								 * toString ().toUpperCase());
								 * lObjRltBillPartyVO.setPartyAddr
								 * (lObjPensionerHdr.getPensionerAddr());
								 * 
								 * if (lLstAcNoDtls != null) {
								 * lObjRltBillPartyVO.setAccntNo(lStrAccountNO);
								 * lObjRltBillPartyVO.setMicrCode(lLngMicrCode);
								 * 
								 * }
								 * 
								 * if
								 * (lPnsnsBillType.equalsIgnoreCase(lStrDCRGBillType
								 * )) { lObjRltBillPartyVO.setPartyAmt(new
								 * BigDecimal
								 * (inputMap.get("NetAmount").toString()));
								 * 
								 * }
								 * 
								 * lObjRltBillPartyVO.setPaymentMode("ECS");
								 * 
								 * lLstRltBillPartyVO.add(lObjRltBillPartyVO);
								 */
							}

						} else if (lPnsnsBillType.equalsIgnoreCase(lStrCVPBillType) || lPnsnsBillType.equalsIgnoreCase(lStrPnsnBillType)) // CVP
						// &
						// Pension
						{
							lresFmlyMemberList = getFmlyMemberDtls(lPnsnCode);

							if (lresFmlyMemberList != null) {
								for (int i = 0; i < lresFmlyMemberList.size(); i++) {
									MstPensionerFamilyDtls lObjMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) lresFmlyMemberList.get(i);
									lObjRltBillPartyVO = new RltBillParty();

									Double lPercentage = 0D;
									Double salary = 0D;

									if (!"Son".equals(lObjMstPensionerFamilyDtlsVO.getRelationship()) && !"Daughter".equals(lObjMstPensionerFamilyDtlsVO.getRelationship())) {
										lObjRltBillPartyVO.setPartyName(lObjMstPensionerFamilyDtlsVO.getName());
										lPercentage = Double.valueOf(lObjMstPensionerFamilyDtlsVO.getPercentage().toString());
										lObjRltBillPartyVO.setAccntNo(lStrAccountNO);
										lObjRltBillPartyVO.setMicrCode(lLngMicrCode);
										lObjRltBillPartyVO.setBankCode(lStrBankCode);
										lObjRltBillPartyVO.setBranchCode(lStrBranchCode);
									} else {
										if ("N".equals(lObjMstPensionerFamilyDtlsVO.getMarriedFlag())) {
											if (lObjMstPensionerFamilyDtlsVO.getSalary() != null) {
												salary = Double.parseDouble(lObjMstPensionerFamilyDtlsVO.getSalary().toString());
											} else {
												salary = 0.00;
											}

											if (salary <= 2550) {
												if ("N".equals(lObjMstPensionerFamilyDtlsVO.getMajorFlag())) {
													if (lObjMstPensionerFamilyDtlsVO.getGuardianName() != null) {
														lObjRltBillPartyVO.setPartyName(lObjMstPensionerFamilyDtlsVO.getGuardianName());
													} else {
														lObjRltBillPartyVO.setPartyName(" ");
													}
													lPercentage = Double.valueOf(lObjMstPensionerFamilyDtlsVO.getPercentage().toString());
													lObjRltBillPartyVO.setAccntNo(lStrAccountNO);
													lObjRltBillPartyVO.setMicrCode(lLngMicrCode);
													lObjRltBillPartyVO.setBankCode(lStrBankCode);
													lObjRltBillPartyVO.setBranchCode(lStrBranchCode);
													lObjRltBillPartyVO.setPartyAddr(lObjPensionerHdr.getGuardianAddr());
												} else {
													lObjRltBillPartyVO.setPartyName(lObjMstPensionerFamilyDtlsVO.getName());
													lPercentage = Double.valueOf(lObjMstPensionerFamilyDtlsVO.getPercentage().toString());
													lObjRltBillPartyVO.setAccntNo(lStrAccountNO);
													lObjRltBillPartyVO.setMicrCode(lLngMicrCode);
													lObjRltBillPartyVO.setBankCode(lStrBankCode);
													lObjRltBillPartyVO.setBranchCode(lStrBranchCode);
													lObjRltBillPartyVO.setPartyAddr(lObjPensionerHdr.getPensionerAddr());
												}
											}
										}
									}

									if (lPnsnsBillType.equalsIgnoreCase(lStrCVPBillType)) {
										Double lNetAmt = Double.valueOf(inputMap.get("NetAmount").toString());
										Double lPaybleAmt = (lNetAmt * lPercentage) / 100;
										lObjRltBillPartyVO.setPartyAmt(new BigDecimal(lPaybleAmt));

									} else if (lPnsnsBillType.equalsIgnoreCase(lStrPnsnBillType)) {
										Double lNetAmt = Double.valueOf(inputMap.get("lNetPensionAmt").toString());
										Double lPaybleAmt = (lNetAmt * lPercentage) / 100;
										lObjRltBillPartyVO.setPartyAmt(new BigDecimal(lPaybleAmt));
									}

									lObjRltBillPartyVO.setPaymentMode("ECS");

									lLstRltBillPartyVO.add(lObjRltBillPartyVO);
								}
							} else {
								// Pensioner is dead but and Family Pensioner is
								// not
								// existed.so we pay pension till he/she live on
								// the
								// name of pensioner

								lObjRltBillPartyVO = new RltBillParty();
								String lPartName = null;
								// if (lObjPensionerHdr.getMiddleName() != null
								// && lObjPensionerHdr.getMiddleName()
								// .length() > 0) {
								// lPartName = lObjPensionerHdr.getFirstName()
								// + " "
								// + lObjPensionerHdr.getMiddleName()
								// + " " + lObjPensionerHdr.getLastName();
								// } else {
								// lPartName = lObjPensionerHdr.getFirstName()
								// + " " + lObjPensionerHdr.getLastName();
								// }
								lPartName = (lObjPensionerHdr.getFirstName() != null && lObjPensionerHdr.getFirstName().length() > 0) ? lObjPensionerHdr.getFirstName() : "";
								// lPartName +=
								// (lObjPensionerHdr.getMiddleName() != null &&
								// lObjPensionerHdr.getMiddleName().length() >
								// 0) ? lObjPensionerHdr.getMiddleName() : "";
								// lPartName += (lObjPensionerHdr.getLastName()
								// != null &&
								// lObjPensionerHdr.getLastName().length() > 0)
								// ? lObjPensionerHdr.getLastName() : "";
								lObjRltBillPartyVO.setPartyName(lPartName.toString().toUpperCase());
								lObjRltBillPartyVO.setPartyAddr(lObjPensionerHdr.getPensionerAddr());
								if (lLstAcNoDtls != null)
								// && lLstAcNoDtls.get(0) != null)
								{
									// lObjRltBillPartyVO.setAccntNo(lLstAcNoDtls.get(
									// 0).toString());
									// lObjRltBillPartyVO.setMicrCode(Long
									// .parseLong((lLstAcNoDtls.get(1)
									// .toString())));
									lObjRltBillPartyVO.setAccntNo(lStrAccountNO);
									lObjRltBillPartyVO.setMicrCode(lLngMicrCode);
									lObjRltBillPartyVO.setBankCode(lStrBankCode);
									lObjRltBillPartyVO.setBranchCode(lStrBranchCode);
								}

								if (lPnsnsBillType.equalsIgnoreCase(lStrCVPBillType)) {
									lObjRltBillPartyVO.setPartyAmt(new BigDecimal(inputMap.get("NetAmount").toString()));

								} else if (lPnsnsBillType.equalsIgnoreCase(lStrPnsnBillType)) {
									lObjRltBillPartyVO.setPartyAmt(new BigDecimal(inputMap.get("lNetPensionAmt").toString()));
								}

								lObjRltBillPartyVO.setPaymentMode("ECS");

								lLstRltBillPartyVO.add(lObjRltBillPartyVO);
							}
						}

					} else {
						lObjRltBillPartyVO = new RltBillParty();
						String lPartName = null;

						lPartName = (lObjPensionerHdr.getFirstName() != null && lObjPensionerHdr.getFirstName().length() > 0) ? lObjPensionerHdr.getFirstName() : "";
						// lPartName += (lObjPensionerHdr.getMiddleName() !=
						// null && lObjPensionerHdr.getMiddleName().length() >
						// 0) ? lObjPensionerHdr.getMiddleName() : "";
						// lPartName += (lObjPensionerHdr.getLastName() != null
						// && lObjPensionerHdr.getLastName().length() > 0) ?
						// lObjPensionerHdr.getLastName() : "";

						lObjRltBillPartyVO.setPartyName(lPartName.toString().toUpperCase());
						lObjRltBillPartyVO.setPartyAddr(lObjPensionerHdr.getPensionerAddr());
						if (lLstAcNoDtls != null) {// && lLstAcNoDtls.get(0) !=
							// null) {
							// lObjRltBillPartyVO.setAccntNo(lLstAcNoDtls.get(0)
							// .toString());
							// lObjRltBillPartyVO.setMicrCode(Long
							// .parseLong((lLstAcNoDtls.get(1).toString())));
							lObjRltBillPartyVO.setAccntNo(lStrAccountNO);
							lObjRltBillPartyVO.setMicrCode(lLngMicrCode);
							lObjRltBillPartyVO.setBankCode(lStrBankCode);
							lObjRltBillPartyVO.setBranchCode(lStrBranchCode);

						}

						if (lPnsnsBillType.equalsIgnoreCase(lStrCVPBillType)) {
							lObjRltBillPartyVO.setPartyAmt(new BigDecimal(inputMap.get("NetAmount").toString()));

						} else if (lPnsnsBillType.equalsIgnoreCase(lStrDCRGBillType)) {
							lObjRltBillPartyVO.setPartyAmt(new BigDecimal(inputMap.get("NetAmount").toString()));

						} else if (lPnsnsBillType.equalsIgnoreCase(lStrPnsnBillType)) {
							lObjRltBillPartyVO.setPartyAmt(new BigDecimal(inputMap.get("lNetPensionAmt").toString()));
						}

						lObjRltBillPartyVO.setPaymentMode("ECS");

						lLstRltBillPartyVO.add(lObjRltBillPartyVO);
					}

				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstRltBillPartyVO;
	}

	public String getPnsnCodeFromPPONo(String lStrPPONO, String lStrStatus, String lStrLocCode) throws Exception {

		String lrestString = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("Select A.pensionerCode FROM TrnPensionRqstHdr A WHERE A.ppoNo = :lPPONO AND A.status = :lStatus AND A.locationCode = :locationCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPPONO", lStrPPONO.toUpperCase());
			lQuery.setString("lStatus", lStrStatus);
			lQuery.setString("locationCode", lStrLocCode);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lrestString = lLstVO.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lrestString;
	}

	/**
	 * Get Nominee Detls
	 * 
	 * @author Sagar
	 */
	private List getNomineeDtls(String lPnsnCode) throws Exception {

		List lresLst = null;
		List<MstPensionerNomineeDtls> lObjNomineeDtls = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM MstPensionerNomineeDtls PN WHERE PN.pensionerCode = :lPensionerCode and percent > 0 ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPensionerCode", lPnsnCode);

			lresLst = lQuery.list();

			if (!lresLst.isEmpty()) {
				lObjNomineeDtls = new ArrayList<MstPensionerNomineeDtls>();
				for (int i = 0; i < lresLst.size(); i++) {
					lObjNomineeDtls.add((MstPensionerNomineeDtls) lresLst.get(i));
				}
			}

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lObjNomineeDtls;
	}

	/**
	 * Get Family Member Dtls Details
	 * 
	 * @author Sagar
	 */
	private List getFmlyMemberDtls(String lPnsnCode) throws Exception {

		List lresLst = null;
		List<MstPensionerFamilyDtls> lObjFmlyMemDtls = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM MstPensionerFamilyDtls PN WHERE PN.pensionerCode = :lPensionerCode and PN.percentage = :lPer");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPensionerCode", lPnsnCode);
			lQuery.setLong("lPer", 100);

			lresLst = lQuery.list();

			if (!lresLst.isEmpty()) {
				lObjFmlyMemDtls = new ArrayList<MstPensionerFamilyDtls>();
				for (int i = 0; i < lresLst.size(); i++) {
					lObjFmlyMemDtls.add((MstPensionerFamilyDtls) lresLst.get(i));
				}
			}

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lObjFmlyMemDtls;
	}

	/**
	 * Getting a Party Address And Account No Details . . .
	 */
	public List getPartyAcNoDtls(String lStrPnsnerCode, String lStrStatus) throws Exception {

		List lresList = new ArrayList();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			/*
			 * lSBQuery.append(
			 * " SELECT pd.accountNo FROM MstPensionerDtls pd WHERE pd.pensionerCode = :lPensionerCode "
			 * +
			 * " AND pd.activeFlag = :lActiveFlg AND pd.caseStatus = :lstatus "
			 * );
			 */

			lSBQuery.append(" SELECT pd.accountNo,rb.micrCode,pd.bankCode,pd.branchCode " + "FROM MstPensionerDtls pd,RltBankBranch rb " + "WHERE rb.branchCode=pd.branchCode "
					+ "AND pd.pensionerCode = :lPensionerCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPensionerCode", lStrPnsnerCode);
			// lQuery.setString("lActiveFlg", "Y");
			// lQuery.setString("lstatus", lStrStatus);

			lresList = lQuery.list();

			// if (lresult != null && !lresult.isEmpty()) {
			// if (lresult.get(0) != null) {
			// lresList = new ArrayList();
			// lresList.add(lresult.get(0).toString());
			// }
			// }

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lresList;
	}

	/*
	 * public List<SavedPensionBillVO> getMyBills(Long userId, Long langId, Map
	 * orgsMap) throws Exception {
	 * 
	 * String[] lStrSortCols = new String[]{"", "tbr.ref_num", "tbr.token_Num",
	 * "tbr.budmjr_hd,tpbh.head_code","tbr.ppo_no", "RBP.Party_Name",
	 * "mbt.short_name", "tbr.bill_net_amount", "tbr.bill_Date", "tbr.tc_bill",
	 * ""}; if (orgsMap.containsKey("savedBillsStatus")) { if
	 * ("20".equals(orgsMap.get("savedBillsStatus"))) { lStrSortCols = new
	 * String[]{"", "tbr.bill_cntrl_no", "tbr.budmjr_hd","tbr.ppo_no",
	 * "RBP.Party_Name", "mbt.short_name", "tbr.bill_net_amount",
	 * "tbr.bill_Date", "tbr.tc_bill", ""}; } else if
	 * ("30".equals(orgsMap.get("savedBillsStatus"))) { lStrSortCols = new
	 * String[]{"", "tbr.bill_cntrl_no", "tbr.token_Num",
	 * "tbr.budmjr_hd","tbr.ppo_no", "RBP.Party_Name", "mbt.short_name",
	 * "tbr.bill_net_amount", "tbr.bill_Date", "tbr.tc_bill", ""}; } else {
	 * lStrSortCols = new String[]{"", "tbr.ref_num", "tbr.token_Num",
	 * "tbr.budmjr_hd","tbr.ppo_no", "RBP.Party_Name", "mbt.short_name",
	 * "tbr.bill_net_amount", "tbr.bill_Date", "tbr.tc_bill", ""}; } }
	 * List<SavedPensionBillVO> dataList = new ArrayList<SavedPensionBillVO>();
	 * List<SavedPensionBillVO> dataList2 = new ArrayList<SavedPensionBillVO>();
	 * HttpServletRequest request = (HttpServletRequest)
	 * orgsMap.get("requestObj"); Integer orderType =
	 * IFMSCommonServiceImpl.getOrderType(request, "vo"); Integer sortColumn =
	 * IFMSCommonServiceImpl.getSortIndex(request, "vo"); SimpleDateFormat
	 * lObjSmplDtFmt = new SimpleDateFormat("dd/MM/yyyy"); List<Long> lLngList =
	 * new ArrayList<Long>(); try { Session ghibSession = getSession();
	 * 
	 * StringBuffer queryBuilder = new StringBuffer();
	 * 
	 * queryBuilder.append(
	 * " select TBR1.TC_BILL,TBR1.BILL_NO,TBR1.BILL_GROSS_AMOUNT, TBR1.BILL_NET_AMOUNT, TBR1.BILL_DATE,"
	 * +
	 * " TBR1.BUDMJR_HD,TBR1.TOKEN_NUM, TBR1.REF_NUM, TBR1.AUD_POST_ID, TBR1.SUBJECT_ID, "
	 * +
	 * " TBR1.CURR_BILL_STATUS,TBR1.HIERARCHY_REF_ID,TBR1.PPO_NO, TBR1.SHORT_NAME, TBR1.EMP_FNAME, "
	 * +
	 * " TBR1.EMP_MNAME, TBR1.EMP_LNAME, TBR1.NOBJ, TBR1.BILL_CNTRL_NO,TBR1.PARTY_NAME from ( "
	 * +
	 * " SELECT TBR.TC_BILL,TBR.BILL_NO,TBR.BILL_GROSS_AMOUNT, TBR.BILL_NET_AMOUNT, TBR.BILL_DATE, "
	 * +
	 * " TBR.BUDMJR_HD,TBR.TOKEN_NUM, TBR.REF_NUM, TBR.AUD_POST_ID, TBR.SUBJECT_ID, "
	 * +
	 * " TBR.CURR_BILL_STATUS,TBR.HIERARCHY_REF_ID,TBR.PPO_NO, MBT.SHORT_NAME, OEM.EMP_FNAME, "
	 * +
	 * " OEM.EMP_MNAME, OEM.EMP_LNAME, TMP.NOBJ, TBR.BILL_CNTRL_NO,RBP.PARTY_NAME "
	 * + " FROM TRN_BILL_REGISTER TBR " +
	 * " JOIN MST_BILL_TYPE MBT ON TBR.SUBJECT_ID = MBT.SUBJECT_ID " +
	 * " LEFT JOIN RLT_BILL_PARTY  RBP ON RBP.BILL_NO = TBR.BILL_NO " +
	 * " JOIN ORG_DDO_MST ODM ON TBR.DDO_CODE = ODM.DDO_CODE AND ODM.activate_flag=1 "
	 * +
	 * " LEFT OUTER JOIN ORG_USERPOST_RLT OUR ON TBR.AUD_POST_ID = OUR.POST_ID AND OUR.activate_flag=1 "
	 * +
	 * "  LEFT OUTER JOIN ORG_EMP_MST OEM ON OUR.USER_ID = OEM.USER_ID AND OEM.activate_flag=1 "
	 * +
	 * " JOIN WF_JOB_MST J ON TBR.BILL_NO = J.JOB_REF_ID_NUM AND J.DOC_ID =:PENSIONBILLDOCID "
	 * +
	 * " LEFT OUTER JOIN (SELECT rbo.master_id AS bNo, count(rbo.rlt_obj_id) AS nObj FROM rlt_objection rbo,  "
	 * + " wf_job_mst jm " + " WHERE RBO.MASTER_ID = JM.JOB_REF_ID_NUM AND " +
	 * " JM.DOC_ID =210004 AND RBO.MODULE_NAME =:TBPFLAG AND JM.JOB_STATUS =:ACTIVEFLAG AND JM.LST_ACT_POST_ID_num =:LSTACTPOSTID GROUP BY RBO.MASTER_ID) TMP "
	 * +
	 * " ON TBR.BILL_NO = TMP.BNO WHERE J.JOB_STATUS =:ACTIVEFLAG AND J.LST_ACT_POST_ID_num =:LSTACTPOSTID  AND MBT.LANG_ID =:LANGID "
	 * + "  AND (OEM.LANG_ID =:LANGID OR OEM.LANG_ID IS NULL) ");
	 * 
	 * if (Short.parseShort(orgsMap.get("savedBillsStatus").toString()) ==
	 * DBConstants.ST_BAPRV_AUD) {queryBuilder.append(
	 * " AND TBR.CURR_BILL_STATUS IN (:BILLSTATUS1,:BILLSTATUS2)"); } else if
	 * (orgsMap.get("savedBillsStatus") != null) {
	 * queryBuilder.append(" AND TBR.CURR_BILL_STATUS =:BILLSTATUS1"); } //
	 * Added By Chandra sekhar for Search Option Start String lStrSearchStr =
	 * null; String lStrSearchVale = null;
	 * 
	 * if (StringUtility.getParameter("cmbSearch", request) != null &&
	 * !"-1".equals(StringUtility.getParameter("cmbSearch", request))) {
	 * lStrSearchStr = StringUtility.getParameter("cmbSearch", request); if
	 * (lStrSearchStr.equals("tbr.subject_id")) { lStrSearchVale =
	 * StringUtility.getParameter("CmbBillType", request); if (lStrSearchVale !=
	 * null && !"-1".equals(lStrSearchVale)) {
	 * queryBuilder.append(" AND TBR.SUBJECT_ID =" + lStrSearchVale); } } else
	 * if (lStrSearchStr.equals("tbr.tc_bill")) { lStrSearchVale =
	 * StringUtility.getParameter("CmbBillCtgry", request); if (lStrSearchVale
	 * != null && !"-1".equals(lStrSearchVale)) {
	 * queryBuilder.append(" AND TBR.TC_BILL LIKE '%" + lStrSearchVale + "%'");
	 * } } else if (lStrSearchStr.equals("our.post_id")) { lStrSearchVale =
	 * StringUtility.getParameter("CmbSerchAuditots", request); if
	 * (lStrSearchVale != null && !"-1".equals(lStrSearchVale)) {
	 * queryBuilder.append(" AND OUR.POST_ID =" + lStrSearchVale); } } else if
	 * (lStrSearchStr.equals("tbr.bill_Date")) { lStrSearchVale =
	 * StringUtility.getParameter("txtSearch", request); if (lStrSearchVale !=
	 * null && lStrSearchVale.length() > 0) {
	 * queryBuilder.append(" AND TBR.BILL_DATE =:FROMDATE "); } } else
	 * if(lStrSearchStr.equals("tbr.token_Num")) { lStrSearchVale =
	 * StringUtility.getParameter("txtSearch", request); if (lStrSearchVale !=
	 * null && lStrSearchVale.length() > 0) {
	 * queryBuilder.append(" AND TBR.TOKEN_NUM ="+ lStrSearchVale); } } else {
	 * lStrSearchVale = StringUtility.getParameter("txtSearch", request); if
	 * (lStrSearchVale != null && !"-1".equals(lStrSearchVale) &&
	 * lStrSearchVale.length() > 0) { queryBuilder.append(" AND " +
	 * lStrSearchStr + " LIKE '%" + lStrSearchVale + "%' "); } }
	 * 
	 * } String lStrSort = "ASC"; if (orderType == 2) lStrSort = "DESC";
	 * 
	 * if (sortColumn != null && sortColumn != -1 &&
	 * lStrSortCols[sortColumn].length() > 0) {
	 * queryBuilder.append(" ORDER BY "+ lStrSortCols[sortColumn] +" "+lStrSort
	 * +") TBR1 "); } else { queryBuilder.append(" ) TBR1 "); }
	 * 
	 * // Added By Chandra sekhar for Search Option End
	 * 
	 * Query sqlquery = ghibSession.createSQLQuery(queryBuilder.toString());
	 * 
	 * if (lStrSearchStr != null && "tbr.bill_Date".equals(lStrSearchStr)) { if
	 * (lStrSearchVale != null && lStrSearchVale.length() > 0) {
	 * sqlquery.setDate("FROMDATE", lObjSmplDtFmt.parse(lStrSearchVale)); } }
	 * sqlquery.setLong("LSTACTPOSTID", SessionHelper.getPostId(orgsMap));
	 * sqlquery.setLong("LANGID", langId); sqlquery.setString("ACTIVEFLAG",
	 * "Active"); sqlquery.setString("TBPFLAG", "TBP");
	 * sqlquery.setLong("PENSIONBILLDOCID", 210004);
	 * 
	 * if (Short.parseShort(orgsMap.get("savedBillsStatus").toString()) ==
	 * DBConstants.ST_BAPRV_AUD) { sqlquery.setShort("BILLSTATUS1",
	 * DBConstants.ST_BAPRV_AUD); sqlquery.setShort("BILLSTATUS2",
	 * DBConstants.ST_BRJCT_AUD); } else if (orgsMap.get("savedBillsStatus") !=
	 * null) { sqlquery.setString("BILLSTATUS1",
	 * orgsMap.get("savedBillsStatus").toString()); }
	 * 
	 * List resList = sqlquery.list(); SavedPensionBillVO lObjVo = null;
	 * Object[] tuple = null; String lStrName = ""; if (!resList.isEmpty()) {
	 * for (int x = 0; x < resList.size(); x++) { lObjVo = new
	 * SavedPensionBillVO(); tuple = (Object[]) resList.get(x); lStrName = "";
	 * if (tuple[0] != null) lObjVo.setBillCtgry(tuple[0].toString()); if
	 * (tuple[1] != null) lObjVo.setBillNo(Long.parseLong(tuple[1].toString()));
	 * if (tuple[2] != null) lObjVo.setBillGrossAmt((BigDecimal) tuple[2]); if
	 * (tuple[3] != null) lObjVo.setBillNetAmt((BigDecimal) tuple[3]); if
	 * (tuple[4] != null) lObjVo.setBillDate((Date) tuple[4]); if (tuple[5] !=
	 * null) lObjVo.setBdjtMjrHd(tuple[5].toString()); if (tuple[6] != null)
	 * lObjVo.setTokenNum(Long.parseLong(tuple[6].toString())); if (tuple[7] !=
	 * null) lObjVo.setRefNum(Long.parseLong(tuple[7].toString())); if (tuple[8]
	 * != null) lObjVo.setAudPostId(Long.valueOf(tuple[8].toString())); if
	 * (tuple[9] != null)
	 * lObjVo.setSubjectId(Long.valueOf(tuple[9].toString())); if (tuple[10] !=
	 * null) lObjVo.setCurrBillStatus(new BigDecimal(tuple[10].toString())); if
	 * (tuple[11] != null)
	 * lObjVo.setHierarchyRefId(Long.parseLong(tuple[11].toString())); if
	 * (tuple[12] != null) lObjVo.setPpoNo(tuple[12].toString()); if (tuple[13]
	 * != null) lObjVo.setBillDesc(tuple[13].toString()); if (tuple[14] != null)
	 * lStrName = tuple[14].toString(); if (tuple[15] != null) lStrName =
	 * lStrName + " " + tuple[15].toString(); if (tuple[16] != null) lStrName =
	 * lStrName + " " + tuple[16].toString(); lObjVo.setAudtrName(lStrName); if
	 * (tuple[17] != null)
	 * lObjVo.setObjCount(Integer.parseInt(tuple[17].toString())); if (tuple[18]
	 * != null) lObjVo.setBillCntrlNo(tuple[18].toString()); if (tuple[19] !=
	 * null) lObjVo.setPartyName(tuple[19].toString()); if
	 * (!lLngList.contains(Long.parseLong(tuple[1].toString()))) {
	 * dataList.add(lObjVo); lLngList.add(Long.parseLong(tuple[1].toString()));
	 * } else { dataList2.add(lObjVo); } } } if (dataList2 != null &&
	 * !dataList2.isEmpty()) { for (int i = 0; i < dataList.size(); i++) { for
	 * (int k = 0; k < dataList2.size(); k++) { if
	 * (dataList.get(i).getBillNo().equals(dataList2.get(k).getBillNo())) {
	 * dataList.get(i).setPartyName(dataList.get(i).getPartyName() + "," +
	 * dataList2.get(k).getPartyName()); } } } } } catch (Exception e) {
	 * logger.error("Error is " + e, e); throw e; } return dataList; }
	 */
	public Map genBillTranRegForAudit(Long userId, String vitoType, String[] arrSelectedBills, String strLocationCode, Long langID, Long gLngPostId, Map billPostMap, Map audPostMap) throws Exception {

		Double total = 0.0;
		int totalBill = 0;
		Map vitoMap = new HashMap();
		List vitoSummaryList = new ArrayList();
		List vitoDataList = new ArrayList();
		try {
			Session hibSession = getSession();

			hibSession.flush();

			SQLQuery sqlQuery = null;
			StringBuffer query = new StringBuffer();

			query.append("SELECT BR.REF_NUM refNo," + "       BR.TOKEN_NUM tokNo," + "       BR.BUDMJR_HD mjrHd," + "       BT.SHORT_NAME shortName," + "       BR.BILL_NET_AMOUNT netAmount,"
					+ "       BR.CARDEX_NO cardxNo,\n" + "       BR.PPO_NO PPO, " + "       EM.EMP_FNAME empFName," + "       EM.EMP_MNAME empMName," + "       EM.EMP_LNAME empLName,"
					+ "       BR.AUD_POST_ID audPostId," + "       BR.BILL_NO billNo, " + "       RBP.PARTY_NAME PNAME" + "  FROM TRN_BILL_REGISTER BR," + "       MST_BILL_TYPE     BT,"
					+ "       ORG_EMP_MST       EM," + "       ORG_USERPOST_RLT  PM," + "       RLT_BILL_PARTY    RBP" + " WHERE EM.USER_ID = PM.USER_ID AND PM.POST_ID = BR.AUD_POST_ID AND"
					+ "       BR.SUBJECT_ID = BT.SUBJECT_ID AND RBP.BILL_NO = BR.BILL_NO AND" + "       BR.TOKEN_NUM IS NOT NULL AND PM.ACTIVATE_FLAG = 1 AND"
					+ "       BR.BILL_NO IN ( :BILL_LIST ) AND BR.CURR_BILL_STATUS = :currBillStatus AND"
					+ "       BR.TSRY_OFFICE_CODE = :locationCode AND BT.LANG_ID = :langId AND EM.LANG_ID = :langId AND PM.activate_flag=1 AND EM.activate_flag=1"
					+ " ORDER BY BR.AUD_POST_ID, BR.REF_NUM ASC");

			sqlQuery = hibSession.createSQLQuery(query.toString());

			sqlQuery.setString("locationCode", strLocationCode);
			sqlQuery.setLong("langId", langID);
			sqlQuery.setLong("currBillStatus", DBConstants.ST_BS_TO);
			sqlQuery.setParameterList("BILL_LIST", arrSelectedBills);
			sqlQuery.addScalar("refNo", Hibernate.LONG);
			sqlQuery.addScalar("tokNo", Hibernate.LONG);
			sqlQuery.addScalar("mjrHd", Hibernate.STRING);
			sqlQuery.addScalar("shortName", Hibernate.STRING);
			sqlQuery.addScalar("netAmount", Hibernate.BIG_DECIMAL);
			sqlQuery.addScalar("cardxNo", Hibernate.LONG);
			sqlQuery.addScalar("PPO", Hibernate.STRING);
			sqlQuery.addScalar("empFName", Hibernate.STRING);
			sqlQuery.addScalar("empMName", Hibernate.STRING);
			sqlQuery.addScalar("empLName", Hibernate.STRING);
			sqlQuery.addScalar("audPostId", Hibernate.LONG);
			sqlQuery.addScalar("billNo", Hibernate.LONG);
			sqlQuery.addScalar("PNAME", Hibernate.STRING);

			List resList = sqlQuery.list();
			List vitoList = null;
			String prevAudPostId = "";
			String audPostId = "";
			String prevAudName = "";
			String audName = "";
			List vitoSummaryInnerList = null;
			List vitoStringList = new ArrayList();
			int listSize = resList.size();
			int counter = 0;
			int summaryCount = 1;
			double grandTotal = 0.0;
			int grandTotalBills = 0;
			if (!resList.isEmpty()) {
				Object[] tuple = null;
				for (Object row : resList) {
					tuple = (Object[]) row;
					// audPostId = tuple[10].toString();
					audPostId = (String) billPostMap.get(tuple[11].toString());
					// audName = tuple[7] + " " + tuple[8] + " " + tuple[9];
					audName = audPostMap.get(audPostId).toString();
					if (counter == 0) {
						prevAudPostId = audPostId;
						prevAudName = audName;
					}
					if (!prevAudPostId.equals(audPostId) || counter == listSize) {
						vitoList = new ArrayList();
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoStringList.add(vitoList);
						vitoList = new ArrayList();
						vitoList.add("Total ");
						vitoList.add("Bills ");
						vitoList.add(":" + totalBill);
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add(" Total Amount : ");
						vitoList.add(ReportHelper.numberFormat(new BigDecimal(total), 2));
						vitoStringList.add(vitoList);

						// vitoMap.put(prevAudPostId, vitoStringList);
						vitoDataList.add(vitoStringList);
						vitoSummaryInnerList = new ArrayList();
						vitoSummaryInnerList.add(summaryCount);
						vitoSummaryInnerList.add(prevAudName);
						vitoSummaryInnerList.add(totalBill);
						vitoSummaryInnerList.add(ReportHelper.numberFormat(new BigDecimal(total), 2));
						vitoSummaryList.add(vitoSummaryInnerList);
						grandTotalBills = grandTotalBills + totalBill;
						grandTotal = grandTotal + total;
						vitoStringList = new ArrayList();
						total = 0.0;
						totalBill = 0;
						summaryCount++;
					}
					vitoList = new ArrayList();
					vitoList.add(tuple[0].toString());
					if (tuple[1] != null) {
						vitoList.add(tuple[1].toString());
					} else {
						vitoList.add("0");
					}
					vitoList.add(tuple[2].toString());
					vitoList.add(tuple[3]);
					vitoList.add(tuple[5].toString());

					StringBuffer lStrPartyName = new StringBuffer();
					lStrPartyName.append(tuple[6] != null ? tuple[6] + " " : "");
					lStrPartyName.append(" - ");
					lStrPartyName.append(tuple[12] != null ? tuple[12] + " " : "");

					vitoList.add(lStrPartyName.toString());

					vitoList.add(ReportHelper.numberFormat((BigDecimal) tuple[4], 2));
					vitoStringList.add(vitoList);
					total = total + Double.parseDouble(tuple[4].toString());
					totalBill++;

					prevAudPostId = audPostId;
					prevAudName = audName;
					counter++;
					if (counter == listSize) {
						vitoList = new ArrayList();
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add("-");
						vitoStringList.add(vitoList);
						vitoList = new ArrayList();
						vitoList.add("Total ");
						vitoList.add("Bills ");
						vitoList.add(":" + totalBill);
						vitoList.add("-");
						vitoList.add("-");
						vitoList.add(" Total Amount : ");
						vitoList.add(ReportHelper.numberFormat(new BigDecimal(total), 2));
						vitoStringList.add(vitoList);

						// vitoMap.put(audPostId, vitoStringList);
						vitoDataList.add(vitoStringList);
						vitoSummaryInnerList = new ArrayList();
						vitoSummaryInnerList.add(summaryCount);
						vitoSummaryInnerList.add(audName);
						vitoSummaryInnerList.add(totalBill);
						vitoSummaryInnerList.add(ReportHelper.numberFormat(new BigDecimal(total), 2));
						vitoSummaryList.add(vitoSummaryInnerList);
						grandTotalBills = grandTotalBills + totalBill;
						grandTotal = grandTotal + total;
						vitoStringList = new ArrayList();
						total = 0.0;
						totalBill = 0;
						summaryCount++;
					}
				}
				vitoSummaryInnerList = new ArrayList();
				vitoSummaryInnerList.add("");
				vitoSummaryInnerList.add("Total");
				vitoSummaryInnerList.add(grandTotalBills);
				vitoSummaryInnerList.add(ReportHelper.numberFormat(new BigDecimal(grandTotal), 2));
				vitoSummaryList.add(vitoSummaryInnerList);
			}
			vitoMap.put("summaryList", vitoSummaryList);
			vitoMap.put("vitoDataList", vitoDataList);
		} catch (Exception e) {
			logger.error("Exception occured in PensionBillDao .genBillTranRegForAudit # \n" + e);
			throw e;
		}
		return vitoMap;
	}

	public String isAllBillsCreated(String lStrppoNo, String lStrLocCd) throws Exception {

		try {
			Session ghibSession = getSession();
			StringBuffer queryBuilder = new StringBuffer();
			queryBuilder.append(" FROM RltPensioncaseBill RPB  WHERE RPB.ppoNo =:ppoNo AND billNo IS NULL AND locationCode = :locationCode ");
			Query hqlQuery = ghibSession.createQuery(queryBuilder.toString());
			hqlQuery.setString("ppoNo", lStrppoNo.toUpperCase());
			hqlQuery.setString("locationCode", lStrLocCd);
			List lStRes = hqlQuery.list();
			if (!lStRes.isEmpty()) {
				return "N";
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw e;
		}
		return "Y";
	}

	public boolean isCounterSelected(String lStrLocCode, Long lLngPostId) throws Exception {

		int flag = 0;
		try {
			Session ghibSession = getSession();
			StringBuffer queryBuilder = new StringBuffer();
			queryBuilder.append(" SELECT X.POST_ID  FROM MST_COUNTER_REFERENCE_RANGE X WHERE LOCATION_CODE =:LocCode   ");
			Query hqlQuery = ghibSession.createSQLQuery(queryBuilder.toString());
			hqlQuery.setString("LocCode", lStrLocCode);
			List lLstRes = hqlQuery.list();
			if (!lLstRes.isEmpty()) {
				for (int i = 0; i < lLstRes.size(); i++) {
					if (lLstRes.get(i) != null && Long.valueOf(lLstRes.get(i).toString()).equals(lLngPostId)) {
						flag = 1;
					}
				}
			}
			if (flag == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw e;
		}
	}

	/*
	 * public Long getMaxRefNumberByAuditorAndCreatedDate(String TrsryOffCode,
	 * Date BillDate) throws Exception {
	 * 
	 * Long lLngMaxRefId = 0L; try { Session ghibSession = getSession();
	 * StringBuffer queryBuilder = new StringBuffer();queryBuilder.append(
	 * " SELECT MAX(TBR.REF_NUM)REFNO FROM TRN_BILL_REGISTER TBR  WHERE TBR.BILL_DATE =:BillDate  AND TBR.TSRY_OFFICE_CODE =:LocCode"
	 * ); SQLQuery hqlQuery =
	 * ghibSession.createSQLQuery(queryBuilder.toString());
	 * hqlQuery.setString("LocCode", TrsryOffCode); hqlQuery.setDate("BillDate",
	 * BillDate);
	 * 
	 * hqlQuery.addScalar("REFNO", Hibernate.BIG_DECIMAL);
	 * 
	 * List lLstRes = hqlQuery.list(); if (!lLstRes.isEmpty()) { if
	 * (lLstRes.get(0) != null) { lLngMaxRefId =
	 * Long.valueOf(lLstRes.get(0).toString()); } } if (lLngMaxRefId < 0) {
	 * lLngMaxRefId = 0L; } } catch (Exception e) { logger.error("Error is : " +
	 * e, e); throw e; } return lLngMaxRefId + 1; }
	 */

	public void updateBookBillNoAndTokenNo(Long lBillNo, Long lTokenNo, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Session hibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" UPDATE trn_pension_bill_hdr SET book_bill_no = :billNoLong, book_token = :TokenNo,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE bill_no = :billNoLong ");

			Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("billNoLong", lBillNo);
			lQuery.setLong("TokenNo", lTokenNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is: " + e, e);
			throw (e);
		}
	}

	/**
	 * getting Last Paid date from detail for set in rqst hdr , last paid &
	 * Amount
	 * 
	 * @param lStrBillNo
	 * @return
	 * @throws Exception
	 */
	public List getPnsnerLastPaidDateAmt(Long lLngBillNo) throws Exception {

		StringBuilder lsb = new StringBuilder();
		List rsltSet = null;

		try {
			Session hibSession = getSession();

			lsb.append(" SELECT bd.ppo_no ppoNo,bd.To_Date lDate,bd.reduced_pension redPnsn FROM trn_pension_bill_dtls bd , trn_pension_bill_hdr bh "
					+ " WHERE bh.trn_pension_bill_hdr_id = bd.trn_pension_bill_hdr_id  AND (bh.bill_type = 'PENSION' OR bh.bill_type = 'Monthly') "
					+ " AND bh.For_Month = bd.pay_for_month AND bh.bill_no = :billNo ");

			SQLQuery lQuery = hibSession.createSQLQuery(lsb.toString());

			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("lDate", Hibernate.DATE);
			lQuery.addScalar("redPnsn", Hibernate.BIG_DECIMAL);

			lQuery.setLong("billNo", lLngBillNo);

			rsltSet = lQuery.list();

		} catch (Exception e) {
			logger.error("Error in : " + e, e);
			throw e;
		}
		return rsltSet;
	}

	public void updatRltPnsnCaseBillBillNoToNullByBillno(Long BillNo, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		new StringBuilder();
		try {
			Session hibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" UPDATE RLT_PENSIONCASE_BILL SET BILL_NO = NULL ,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO =:billNoStr AND PAY_MODE = 'M' "
					+ " AND BILL_TYPE IN('CVP','DCRG') AND LOCATION_CODE = :LOCATION_CODE ");

			Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("billNoStr", BillNo);
			lQuery.setString("LOCATION_CODE", lStrLocCd);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error in : " + e, e);
			throw e;
		}

	}

	public void updateLastPaidDateAtApproveBill(Long lLngBillNo, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		Object[] lObjArr = null;
		String updateSql = null;
		List lPrvBillDateLst = null;

		try {
			Session ghibSession = getSession();

			lPrvBillDateLst = getPnsnerLastPaidDateAmt(lLngBillNo);

			if (lPrvBillDateLst != null && lPrvBillDateLst.size() > 0) {
				Date lastPaidDate = null;
				BigDecimal lBDAmnt = BigDecimal.ZERO;
				String lStrPPONo = "";
				Query lQuery = null;
				for (int c = 0; c < lPrvBillDateLst.size(); c++) {
					lObjArr = (Object[]) lPrvBillDateLst.get(c);
					lastPaidDate = (java.sql.Date) (lObjArr[1] != null ? lObjArr[1] : null);
					lBDAmnt = (BigDecimal) (lObjArr[2] != null ? lObjArr[2] : null);
					lStrPPONo = lObjArr[0].toString();

					updateSql = "UPDATE trn_pension_rqst_hdr rh SET rh.last_paid_date =:lastPaidDate, "
							+ " rh.last_paid_amount =:lBDAmnt,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE rh.ppo_no =:lStrPPONo AND "
							+ " (rh.Case_Status = 'APPROVED' OR rh.Case_Status = 'NEW') AND " + " rh.location_code = :LocCode ";

					if (lStrPPONo != null) {
						lQuery = ghibSession.createSQLQuery(updateSql.toString());

						lQuery.setDate("lastPaidDate", lastPaidDate);
						lQuery.setBigDecimal("lBDAmnt", lBDAmnt);
						lQuery.setString("lStrPPONo", lStrPPONo);
						lQuery.setParameter("LocCode", lStrLocCode);
						lQuery.setLong("updated_user_id", gLngUserId);
						lQuery.setLong("updated_post_id", gLngPostId);
						lQuery.setDate("updated_date", gDate);

						lQuery.executeUpdate();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in updateLastPaidDateAtApproveBill @ PensionBillDAOImpl  : " + e, e);
			throw e;
		}
	}

	public void updateBillNetAmountForMRCounterPayment(Long lLngBillNo, BigDecimal lBDGrantAmtMR, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			Long lLngBillHdrId = null;

			// Updating Bill Register
			lSBQuery.append(" UPDATE TRN_BILL_REGISTER SET BILL_GROSS_AMOUNT = :Amount, BILL_NET_AMOUNT = :Amount,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO = :BillNo ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setBigDecimal("Amount", lBDGrantAmtMR);
			lQuery.setLong("BillNo", lLngBillNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();

			// Updating Bill Party
			lSBQuery = new StringBuilder();
			lSBQuery.append(" UPDATE RLT_BILL_PARTY SET PARTY_AMT = :Amount,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO = :BillNo ");
			lQuery = null;
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setBigDecimal("Amount", lBDGrantAmtMR);
			lQuery.setLong("BillNo", lLngBillNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();

			// Updating Bill EDP Dtls
			lSBQuery = new StringBuilder();
			lSBQuery.append(" UPDATE TRN_BILL_EDP_DTLS SET EDP_AMT = :Amount,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO = :BillNo ");
			lQuery = null;
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setBigDecimal("Amount", lBDGrantAmtMR);
			lQuery.setLong("BillNo", lLngBillNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();

			// Updating Bill Hdr
			lSBQuery = new StringBuilder();
			lSBQuery.append(" UPDATE TRN_PENSION_BILL_HDR SET GROSS_AMOUNT = :Amount, NET_AMOUNT = :Amount,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO = :BillNo ");
			lQuery = null;
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setBigDecimal("Amount", lBDGrantAmtMR);
			lQuery.setLong("BillNo", lLngBillNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();

			// Getting Bill Hdr id for That Bill....
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT TRN_PENSION_BILL_HDR_ID FROM TRN_PENSION_BILL_HDR WHERE BILL_NO = :BillNo ");
			lQuery = null;
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("BillNo", lLngBillNo);
			List resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				lLngBillHdrId = Long.parseLong(resultList.get(0).toString());
			}

			// Updating Bill Dtls
			lSBQuery = new StringBuilder();
			lSBQuery.append(" UPDATE TRN_PENSION_BILL_DTLS SET REDUCED_PENSION = :Amount,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE TRN_PENSION_BILL_HDR_ID = :HdrId ");
			lQuery = null;
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setBigDecimal("Amount", lBDGrantAmtMR);
			lQuery.setLong("HdrId", lLngBillHdrId);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();

			// Updating Med Remb
			lSBQuery = new StringBuilder();
			lSBQuery.append(" UPDATE TRN_PENSION_MED_REMBRSMNT SET GRANT_AMNT = :Amount,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_HDR_ID = :HdrId ");
			lQuery = null;
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setBigDecimal("Amount", lBDGrantAmtMR);
			lQuery.setLong("HdrId", lLngBillHdrId);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}
	}

	// Soumya New Methods.................................starts
	public void deleteTrnPensionArrearDtls(String lStrBillNo) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnPensionArrearDtls WHERE billNo = :billNo ");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteRltBillParty(String lStrBillNo, String lStrLocCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM RltBillParty WHERE billNo = :billNo AND locationCode = :locationCode");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteOtherPartyPayment(String lStrBillNo, String lStrLocCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnPensionOtherPartyPay WHERE billNo = :billNo AND locationCode = :locationCode");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void updateRecoveryDtls(String lStrBillNo, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Query lQuery = getSession().createQuery(
					" UPDATE TrnPensionRecoveryDtls SET billNo = NULL,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date WHERE billNo = :billNo ");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteTrnOnlinebillEmp(String lStrBillNo, String lStrLocCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnOnlinebillEmp WHERE billNo = :billNo AND locationCode = :locationCode");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteTrnBillMvmnt(String lStrBillNo, String lStrLocCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnBillMvmnt WHERE billNo = :billNo AND locationCode = :locationCode");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteRptExpEdpDtls(String lStrBillNo, String lStrLocCode, String lStrSubId) throws Exception {

		List resList = null;
		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;

		try {
			Session ghibSession = getSession();

			lSBQuery.append("SELECT exp_Id ID FROM Rpt_Expenditure_Dtls WHERE exp_No = :billNo AND exp_Type_Code = 'Bill' AND tsry_Code = :locationCode AND active = 'Y'");

			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.addScalar("ID", Hibernate.BIG_DECIMAL);
			Query.setLong("billNo", Long.parseLong(lStrBillNo));
			Query.setString("locationCode", lStrLocCode);

			resList = Query.list();

			if (resList != null && !resList.isEmpty() && resList.get(0) != null) {
				lQuery = getSession().createQuery("DELETE FROM RptExpEdpDtls WHERE expId = :expId ");
				lQuery.setBigDecimal("expId", new BigDecimal(resList.get(0).toString()));
				lQuery.executeUpdate();

				Query lQuery1 = getSession().createQuery(" DELETE FROM RptExpenditureDtls WHERE expId = :expId ");
				lQuery1.setBigDecimal("expId", new BigDecimal(resList.get(0).toString()));
				lQuery1.executeUpdate();

				if (!lStrSubId.equals("43")) {
					Query lQuery2 = getSession().createQuery(" DELETE FROM RptReceiptDtls WHERE expId = :expId ");
					lQuery2.setBigDecimal("expId", new BigDecimal(resList.get(0).toString()));
					lQuery2.executeUpdate();
				}
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	/*
	 * public void deleteRptExpenditureDtls(String lStrBillNo,String
	 * lStrLocCode) throws Exception { try { Query lQuery =
	 * getSession().createQuery(
	 * " DELETE FROM RptExpenditureDtls WHERE expNo = :billNo AND expTypeCode = 'Bill' AND tsryCode = :locationCode AND active = 'Y'"
	 * );
	 * 
	 * lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
	 * lQuery.setString("locationCode", lStrLocCode);
	 * 
	 * lQuery.executeUpdate(); } catch (Exception e) { logger.error("Error is "
	 * + e, e); throw (e); } }
	 */
	public void deleteTrnBillEdpDtls(String lStrBillNo, String lStrLocCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnBillEdpDtls WHERE billNo = :billNo AND expRcpRec IN ('EXP','RCP') AND locationCode = :locationCode");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteTrnPensionBillDtls(String lStrBillNo, String lStrSubId, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		List resList = null;
		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;
		Query lQuery1 = null;
		try {
			Session ghibSession = getSession();

			lSBQuery.append("SELECT trn_Pension_Bill_Hdr_Id ID FROM Trn_Pension_Bill_Hdr WHERE bill_No = :billNo AND location_Code = :locationCode");

			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.addScalar("ID", Hibernate.LONG);
			Query.setLong("billNo", Long.parseLong(lStrBillNo));
			Query.setString("locationCode", lStrLocCode);

			resList = Query.list();

			if (resList != null && !resList.isEmpty()) {
				if ("43".equals(lStrSubId)) // MR Bill
				{
					lQuery1 = getSession()
							.createQuery(
									"UPDATE TrnPensionMedRembrsmnt SET billHdrId = NULL ,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date WHERE billHdrId IN (:trnPensionBillHdrId) AND locationCode = :locationCode");

					lQuery1.setParameterList("trnPensionBillHdrId", resList);
					lQuery1.setString("locationCode", lStrLocCode);
					lQuery1.setLong("updated_user_id", gLngUserId);
					lQuery1.setLong("updated_post_id", gLngPostId);
					lQuery1.setDate("updated_date", gDate);

					lQuery1.executeUpdate();
				}
				lQuery = getSession().createQuery("DELETE FROM TrnPensionBillDtls WHERE trnPensionBillHdrId IN (:trnPensionBillHdrId) ");

				lQuery.setParameterList("trnPensionBillHdrId", resList);

				lQuery.executeUpdate();
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteTrnPensionBillHdr(String lStrBillNo, String lStrLocCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnPensionBillHdr WHERE billNo = :billNo AND locationCode = :locationCode");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteTrnBillRegister(String lStrBillNo, String lStrLocCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnBillRegister WHERE billNo = :billNo AND locationCode = :locationCode AND phyBill = 2");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	/*
	 * public void deleteRptReceiptDtls(String lStrBillNo,String lStrLocCode)
	 * throws Exception { try { Query lQuery =getSession().createQuery(
	 * " DELETE FROM RptReceiptDtls WHERE rcptNo = :billNo AND tsryCode = :locationCode AND active = 'Y'"
	 * );
	 * 
	 * lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
	 * lQuery.setString("locationCode", lStrLocCode);
	 * 
	 * lQuery.executeUpdate(); } catch (Exception e) { logger.error("Error is "
	 * + e, e); throw (e); } }
	 */
	public void deleteRltPensioncaseBill(String lStrBillNo, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Query lQuery = getSession()
					.createQuery(
							" UPDATE RltPensioncaseBill SET billNo = NULL,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date,status='N' WHERE billNo = :billNo AND locationCode = :locationCode ");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);
			// lQuery.setString("status", arg1)
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void updateRltPensioncaseBill(List<Long> lLstBillNo, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Query lQuery = getSession()
					.createQuery(
							" UPDATE RltPensioncaseBill SET billNo = NULL,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date,status='N' WHERE billNo IN (:billNoList) AND locationCode = :locationCode ");

			lQuery.setParameterList("billNoList", lLstBillNo);
			lQuery.setString("locationCode", lStrLocCode);
			// lQuery.setString("status", arg1)
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void updateCaseApprStatus(String lStrPpoNo, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Query lQuery = getSession()
					.createQuery(
							" UPDATE TrnPensionRqstHdr SET approveStatus = 'CASEINWARD',updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date  WHERE ppoNo = :ppoNo AND locationCode = :locationCode AND approveStatus = 'BILLCRTD' ");

			lQuery.setString("ppoNo", lStrPpoNo);
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public String getPPONoForBill(String lStrBillNo, String lStrLocCode) throws Exception {

		String lStrPpoNo = null;
		List resultList = null;
		try {
			Session ghibSession = getSession();
			StringBuffer strQuery = new StringBuffer();

			strQuery.append(" SELECT ppo_No as ppo_no");
			strQuery.append(" FROM Rlt_Pensioncase_Bill ");
			strQuery.append(" WHERE bill_No = :billNo AND location_Code = :locationCode ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(strQuery.toString());
			hqlQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			hqlQuery.setString("locationCode", lStrLocCode);

			hqlQuery.addScalar("ppo_no", Hibernate.STRING);

			resultList = hqlQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				lStrPpoNo = resultList.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrPpoNo;
	}

	public String getCaseEditStatusLogic(String lStrPpoNo, String lStrSubId, String lStrLocCode) throws Exception {

		String lStrCaseEditStatusDecision = null;
		List resultList;
		String lStrBillType = "";
		Iterator it;
		try {
			Session ghibSession = getSession();
			StringBuffer strQuery = new StringBuffer();

			if ("9".equals(lStrSubId)) {
				lStrBillType = "PENSION";
			} else if ("10".equals(lStrSubId)) {
				lStrBillType = "DCRG";
			} else if ("11".equals(lStrSubId)) {
				lStrBillType = "CVP";
			}

			strQuery.append(" SELECT billNo ");
			strQuery.append(" FROM RltPensioncaseBill ");
			strQuery.append(" WHERE ppoNo = :ppoNo AND locationCode = :locationCode AND billType NOT IN (:billType) ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("ppoNo", lStrPpoNo);
			hqlQuery.setString("locationCode", lStrLocCode);
			hqlQuery.setString("billType", lStrBillType);

			resultList = hqlQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				it = resultList.iterator();
				Object tuple = null;
				while (it.hasNext()) {
					tuple = it.next();

					if (tuple != null) {
						lStrCaseEditStatusDecision = "true";
					}
				}
				if (lStrCaseEditStatusDecision == null) {
					lStrCaseEditStatusDecision = "false";
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrCaseEditStatusDecision;
	}

	public void updateSuppBillDtls(String lStrBillNo, String lStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Query lQuery = getSession().createQuery(
					" UPDATE TrnPensionSupplyBillDtls SET billNo = null,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date  "
							+ " WHERE billNo = :billNo AND locationCode = :locationCode");

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public Long getMrBillHdrIdForRejection(String lStrBillNo, String lStrLocCode) throws Exception {

		Long lLngHdrId = null;
		List resultList = null;
		try {
			Session ghibSession = getSession();
			StringBuffer strQuery = new StringBuffer();

			strQuery.append(" SELECT BH.TRN_PENSION_BILL_HDR_ID HDRID" + " FROM TRN_PENSION_BILL_HDR BH " + " WHERE BH.BILL_NO = :billNo " + "       AND BH.BILL_TYPE = 'MR' "
					+ "       AND BH.SCHEME = 'RUBARU' " + "       AND BH.LOCATION_CODE = :locationCode ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(strQuery.toString());
			hqlQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			hqlQuery.setString("locationCode", lStrLocCode);

			hqlQuery.addScalar("HDRID", Hibernate.LONG);

			resultList = hqlQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				lLngHdrId = new Long(resultList.get(0).toString());
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLngHdrId;
	}

	public void updateMedicalOnRejection(Long lLngBillHdrId, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Query lQuery = getSession().createQuery(
					" UPDATE TrnPensionMedRembrsmnt SET status = 70,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date  WHERE billHdrId = :BILL_HDR_ID ");

			lQuery.setLong("BILL_HDR_ID", lLngBillHdrId);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	// Soumya New Methods.................................ends
	public boolean getBooleanFlagForApprove(String lStrPPONo, String locationCode, List lLstBillType) throws Exception {

		boolean returnFlag = true;
		Integer auditStatus;
		StringBuffer lQueryString = new StringBuffer();
		try {

			lQueryString.append(" SELECT tbr.audit_status,temp1.billtype,temp1.STATUS FROM ( " + " SELECT rpc.bill_type AS billtype ,rpc.bill_no billno,rpc.ppo_no AS ppono,rpc.status AS STATUS "
					+ " FROM rlt_pensioncase_bill rpc WHERE rpc.ppo_no =:ppoNo and rpc.bill_type in (:billType) AND rpc.status = 'Y' ");
			if (locationCode != null) {
				lQueryString.append(" and rpc.location_code =:location_code  ");
			}
			lQueryString.append(" )AS temp1 LEFT JOIN trn_bill_register tbr ON tbr.bill_no = temp1.billno");

			Query lQuery = getSession().createSQLQuery(lQueryString.toString());

			if (locationCode != null) {
				lQuery.setString("location_code", locationCode);
			}

			lQuery.setString("ppoNo", lStrPPONo);
			lQuery.setParameterList("billType", lLstBillType);
			//
			getSession().flush();
			List resList = lQuery.list();
			Object[] tuple = null;
			for (int i = 0; i < resList.size(); i++) {
				tuple = (Object[]) resList.get(i);
				if (tuple[0] != null) {
					auditStatus = (Integer) tuple[0];
					if (auditStatus == 1) {
						returnFlag = true;
					} else {
						returnFlag = false;
						break;
					}
				} else {
					returnFlag = false;
				}
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
		return returnFlag;
	}

	public Map getLastPaidDateOnCorrectionFwd(List lstBillNo) throws Exception {

		Map<String, Date> mapPensionerDtls = new HashMap();
		try {
			IFMSCommonDAOImpl commonDAO = new IFMSCommonDAOImpl(this.sessionFactory);
			Map options = new HashMap();
			options.put("BILL_LIST", lstBillNo);
			List<Object[]> lstPensionerDtls = commonDAO.findByNamedQuery("pension.billDtls.pnsnrDtlsFrmBill", options);

			for (Object[] pnsnrDtl : lstPensionerDtls) {
				// Get the last 'from date' for a particular pensioner and set
				// the last paid date = fromdate - 1
				mapPensionerDtls.put(pnsnrDtl[0].toString(), pnsnrDtl[1] != null ? new Date(oMySQLFormat.parse(pnsnrDtl[1].toString()).getTime() - 86400000) : null);
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}

		return mapPensionerDtls;
	}

	public Map getLastPaidAmountOnCorrectionFwd(Map<String, Date> mapPnsnrDtls) throws Exception {

		Map<String, List> mapLstPdDtls = new HashMap<String, List>();
		try {
			IFMSCommonDAOImpl commonDAO = new IFMSCommonDAOImpl(this.sessionFactory);
			Map options = new HashMap();
			options.put("PENSIONER_CODE_LIST", mapPnsnrDtls.keySet());

			// Following code gives you list of all the reduced pension amt for
			// the given pensioners in the reverse chronological order (starting
			// from latest month)
			List<Object[]> lstPensionerDtls = commonDAO.findByNamedQuery("pension.billDtls.pnsnrRedPnsn", options);

			List intermedList = null;
			String prevPnsnr = null;

			// Following is the logic to prepare a map having key as pensioner
			// code and value as a list
			// which contains last paid date and last paid amount in that order,
			// to update the rqst hdr table accordingly
			for (Object[] obj : lstPensionerDtls) {

				if (prevPnsnr != null) {
					if (!prevPnsnr.equals(obj[0].toString())) {
						// get the reduced pension amount from bill dtls of the
						// last approved bill
						// created for a particular pensioner and skip for other
						// bills
						intermedList = new ArrayList();
						intermedList.add(mapPnsnrDtls.get(obj[0]));// last paid
						// date
						intermedList.add(obj[1]);// last paid amount
						mapLstPdDtls.put(obj[0].toString(), intermedList);// key=pensioner
						// code
						// ;
						// value
						// =
						// list
					}
				} else {
					// for the first ppo of the result set
					intermedList = new ArrayList();
					intermedList.add(mapPnsnrDtls.get(obj[0]));// last paid date
					intermedList.add(obj[1]);// last paid amount
					mapLstPdDtls.put(obj[0].toString(), intermedList);// key=pensioner
					// code
					// ;
					// value
					// =
					// list
				}

				prevPnsnr = obj[0].toString();
			}

		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}

		return mapLstPdDtls;
	}

	public void updateLstPaidDateAndAmt(Map<String, List> mapPnsnrDtls, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		StringBuilder lsb = new StringBuilder();

		try {
			Session hibSession = getSession();

			lsb.append("UPDATE TRN_PENSION_RQST_HDR H SET H.LAST_PAID_DATE=:lastPaidDate,H.LAST_PAID_AMOUNT=:lastPaidAmt,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date  WHERE PENSIONER_CODE=:pensionerCode AND CASE_STATUS IN ('APPROVED','NEW') ");

			Query lQuery = hibSession.createSQLQuery(lsb.toString());
			List pnsnrLstPdDtlLst = null;

			for (String keyStat : mapPnsnrDtls.keySet()) {
				pnsnrLstPdDtlLst = mapPnsnrDtls.get(keyStat);
				lQuery.setDate("lastPaidDate", pnsnrLstPdDtlLst.get(0) != null ? (Date) pnsnrLstPdDtlLst.get(0) : null);
				lQuery.setBigDecimal("lastPaidAmt", pnsnrLstPdDtlLst.get(1) != null ? new BigDecimal(pnsnrLstPdDtlLst.get(1).toString()) : null);
				lQuery.setString("pensionerCode", keyStat);
				lQuery.setLong("updated_user_id", gLngUserId);
				lQuery.setLong("updated_post_id", gLngPostId);
				lQuery.setDate("updated_date", gDate);

				lQuery.executeUpdate();
			}
		} catch (Exception e) {
			logger.error("Exception occurred in updateLstPaidDateAndAmt : " + e, e);
			throw e;
		}
	}

	public Long getCaseIdfromPpoNo(String ppoNo, String Status, String locationCode) throws Exception // check
	{

		Session hiSession = getSession();
		Long lbgdcCaseId = 0L;
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {
			strQuery.append(" SELECT r.pensionRequestId  FROM TrnPensionRqstHdr r  WHERE r.ppoNo =:ppoNo and r.caseStatus =:status and locationCode=:locationCode order by r.createdDate DESC");

			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("ppoNo", ppoNo.trim());
			hqlQuery.setString("status", Status.trim());
			hqlQuery.setString("locationCode", locationCode);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				lbgdcCaseId = (Long) resultList.get(0);
			}
			return lbgdcCaseId;
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
	}

	public List getCaseUsersLsitgetUserFromPost(String[] postId, String[] levelId, Long lLngLangId, Map orgsMap, String MRFlag) throws Exception {

		List userList = new ArrayList();
		List lLstPost = new ArrayList();
		try {
			Session hibSession = getSession();
			int lIntLoopI = 0;
			String str = "SELECT O.EMP_FNAME, " + "       O.EMP_LNAME, " + "       R.POST_ID,  " + "       O.EMP_MNAME, " + "       MM.DSGN_NAME, " + "       L.LEVEL_DESC," + "       O.EMP_PREFIX "
					+ "FROM ORG_EMP_MST O, " + "     ORG_USERPOST_RLT R ," + "     ORG_DESIGNATION_MST  MM," + "     ORG_POST_MST P, " + "     RLT_LEVEL_STATUS L, "
					+ "     ORG_POST_DETAILS_RLT OPDR  " + "WHERE  R.POST_ID = P.POST_ID " + "       AND R.ACTIVATE_FLAG =:activeFlag  " + "		 AND O.ACTIVATE_FLAG =:activeFlag "
					+ "       AND P.ACTIVATE_FLAG =:activeFlag " + "       AND OPDR.DSGN_ID = MM.DSGN_ID " + "       AND OPDR.POST_ID = P.POST_ID " + "       AND R.POST_ID IN (:postList) "
					+ "       AND R.USER_ID=O.USER_ID " + "       AND L.LEVEL_CODE =:levelCode " + "       AND O.LANG_ID =:langId ";
			str = str + " AND L.CATEGORY =:category ";

			Query query = hibSession.createSQLQuery(str);
			for (lIntLoopI = 0; lIntLoopI < postId.length; lIntLoopI++) {
				lLstPost.add(postId[lIntLoopI]);
			}
			query.setParameterList("postList", lLstPost);
			query.setLong("langId", lLngLangId);
			query.setString("levelCode", levelId[0]);
			query.setInteger("activeFlag", 1);

			if (MRFlag.equals("Y")) {
				query.setString("category", DBConstants.CATEGORY_MRCASE);
			} else if (MRFlag.equals("B")) {
				query.setString("category", DBConstants.CATEGORY_PNSNBILL);
			} else {
				query.setString("category", DBConstants.CATEGORY_PPO);
			}

			List lLstQList = query.list();
			Iterator it = lLstQList.iterator();
			int lIntLoopJ = 0;
			String[] result = null;
			Object[] tuple = null;
			String middleName = "";
			String name = "";
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				result = new String[4];
				middleName = tuple[3] != null ? tuple[3].toString() : "";
				name = tuple[0] + " " + middleName + " " + tuple[1] + " [" + tuple[4] + "]";
				result[0] = name;
				result[1] = tuple[2].toString();
				result[2] = levelId[lIntLoopJ];
				result[3] = tuple[5].toString();
				userList.add(result);
				lIntLoopJ++;
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return userList;
	}

	public static void main(String[] args) {

		int maxdigits = 9; // Change to needed # of digits
		List l = new ArrayList();
		for (int i = 1; i < 1000000; i++) {
			StringBuilder result = new StringBuilder();
			Random r = new Random(); // Seed with what you feel is appropriate
			for (int j = 1; j <= maxdigits; j++) {
				result.append(r.nextInt(10));
			}
			// System.out.println("Sr No=" + i + "   " + result.toString() +
			// "    " + l.contains(result.toString())); // Append
			// a
			// number
			// from
			// 0
			// to
			// 9
			l.add(result.toString());
			// System.out.println(result.toString());
		}
	}

	public List getAllFirstPensionBillCases(String locationCode) throws Exception {

		List lLstPensionDtlVO;
		Session hibSession = getSession();
		StringBuffer lSBGetPnsnQry = new StringBuffer();

		lSBGetPnsnQry.append("SELECT new com.tcs.sgv.pensionpay.valueobject.PensionDtlVO" + "(rqst.pensionerCode,rqst.ppoNo,hdr.firstName, ");
		lSBGetPnsnQry.append("hdr.dateOfRetirement,rqst.commensionDate) ");
		lSBGetPnsnQry.append("FROM MstPensionerHdr hdr, TrnPensionRqstHdr rqst, ");
		lSBGetPnsnQry.append("MstPensionerDtls mstdtl ");
		lSBGetPnsnQry.append("WHERE hdr.pensionerCode = rqst.pensionerCode ");
		lSBGetPnsnQry.append("AND mstdtl.pensionerCode = rqst.pensionerCode ");
		lSBGetPnsnQry.append("AND mstdtl.identificationFlag = :identifiacationFlag ");
		lSBGetPnsnQry.append("AND rqst.locationCode = :locationCode ");

		Query lHibQry = hibSession.createQuery(lSBGetPnsnQry.toString());
		lHibQry.setString("identifiacationFlag", "YES");// pensionConst.getString("PENSIONBILLS.IDENTIFICATION.YES"));
		lHibQry.setString("locationCode", locationCode);
		lLstPensionDtlVO = lHibQry.list();

		return lLstPensionDtlVO;

	}

	public List getCommDtls(String pensionerCode, String locationCode) {

		Session hibSession = getSession();
		List lLstCommDtls = new ArrayList();
		StringBuffer lSBGetPnsnQry = new StringBuffer();
		lSBGetPnsnQry.append("SELECT hdr.ppoNo,mst.firstName,mst.officeAddr,mst.designation,hdr.cvpAmount,dtls.accountNo,rlt.micrCode\n");
		lSBGetPnsnQry.append(" FROM MstPensionerHdr mst, TrnPensionRqstHdr hdr, MstPensionerDtls dtls, RltBankBranch rlt\n");
		lSBGetPnsnQry.append(" WHERE mst.pensionerCode = hdr.pensionerCode\n");
		lSBGetPnsnQry.append(" AND hdr.pensionerCode=dtls.pensionerCode");
		lSBGetPnsnQry.append(" AND rlt.branchCode=dtls.branchCode");
		lSBGetPnsnQry.append(" AND mst.pensionerCode=:pensionerCode");
		lSBGetPnsnQry.append(" AND rlt.locationCode=:locationCode");

		Query lHibQry = hibSession.createQuery(lSBGetPnsnQry.toString());
		lHibQry.setParameter("pensionerCode", pensionerCode);
		lHibQry.setParameter("locationCode", locationCode);
		lLstCommDtls = lHibQry.list();

		return lLstCommDtls;
	}

	public List<RltBillParty> getBillPartyRltInfo(Long lLngBillNo) throws Exception {

		logger.info("Start");
		StringBuilder lsb = new StringBuilder();
		List lLstRsltSet = null;
		try {
			lsb.append(" FROM RltBillParty tbr");
			lsb.append(" Where tbr.billNo = :billNo ");

			Session hibSession = getSession();
			Query lQuery = hibSession.createQuery(lsb.toString());

			lQuery.setParameter("billNo", lLngBillNo);
			lLstRsltSet = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		logger.info("End");
		return lLstRsltSet;
	}

	public List getAttachments(Long attachmentId) throws Exception {

		StringBuilder lsb = new StringBuilder();
		List lLstRsltSet = null;

		try {
			lsb.append(" SELECT a.attachmentId, am.srNo, am.orgFileName, am.attachmentDesc ");
			lsb.append(" FROM CmnAttachmentMst a, CmnAttachmentMpg am ");
			lsb.append(" WHERE a.attachmentId = :attachmentId AND ");
			lsb.append(" a.attachmentId = am.cmnAttachmentMst.attachmentId AND am.activateFlag = 'Y' ");

			Query lQuery = getSession().createQuery(lsb.toString());

			lQuery.setParameter("attachmentId", attachmentId);

			lLstRsltSet = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstRsltSet;

	}

	public List getDraftBills1(String locationCode) {

		Session hibSession = getSession();
		List lLstBillDtls = new ArrayList();
		StringBuffer lSBBillQry = new StringBuffer();
		lSBBillQry.append("SELECT reg.ppoNo,rlt.partyName,hdr.billType,hdr.billDate,hdr.netAmount,reg.tcBill \n");
		lSBBillQry.append("FROM RltBillParty rlt,TrnBillRegister reg,TrnPensionBillHdr hdr \n");
		lSBBillQry.append("WHERE rlt.billNo=reg.billNo \n");
		lSBBillQry.append("AND hdr.billNo=reg.billNo \n");
		lSBBillQry.append("AND hdr.locationCode=:locationCode \n");
		lSBBillQry.append("AND reg.currBillStatus=70 \n");
		lSBBillQry.append("AND hdr.billType IN ('PENSION','CVP','DCRG')");

		Query lHibQry = hibSession.createQuery(lSBBillQry.toString());
		lHibQry.setParameter("locationCode", locationCode);
		lLstBillDtls = lHibQry.list();

		return lLstBillDtls;

	}

	public int getDraftBillCounts(String locationCode, String lStrSearchStr, String lStrSearchValue, List lLstBillType, String lStrCurrRoleId, String lStrBillFlag, Long lLngPostId) throws Exception {

		StringBuffer strQuery = null;
		List lLstBillDtls = null;
		Session hibSession = getSession();
		SimpleDateFormat lObjSmplDtFmt = new SimpleDateFormat("dd/MM/yyyy");
		int lIntCount = 0;
		try {
			strQuery = new StringBuffer();
			lLstBillDtls = new ArrayList();
			strQuery.append("SELECT count(rlt.partyName) ");
			strQuery.append("FROM RltBillParty rlt,TrnBillRegister reg,OrgUserpostRlt our,OrgEmpMst oem  \n");
			strQuery.append("WHERE rlt.billNo=reg.billNo \n");
			// strQuery.append("AND hdr.billNo=reg.billNo \n");
			// strQuery.append("AND rpb.billNo=reg.billNo \n");
			strQuery.append("AND reg.audPostId = our.orgPostMstByPostId.postId \n");
			strQuery.append("AND our.orgUserMst.userId = oem.orgUserMst.userId \n");
			strQuery.append("AND reg.locationCode=:locationCode \n");
			strQuery.append("AND our.activateFlag=:activateFlag \n");

			if ((bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "A".equalsIgnoreCase(lStrBillFlag)) || (bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId))) {
				strQuery.append("AND (reg.currBillStatus=:billStatus OR reg.currBillStatus=:otherBillStatus) \n");
			} else {
				strQuery.append(" AND ");
				if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "AR".equalsIgnoreCase(lStrBillFlag)) {
					strQuery.append("( \n");
				}
				strQuery.append(" reg.currBillStatus=:billStatus \n");
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "AR".equalsIgnoreCase(lStrBillFlag)) {
				strQuery.append(" OR reg.isArchieved = :archievedFlag )\n");
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId)) {
				strQuery.append("AND reg.audPostId=:auditorPostId \n");
			}
			if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && !"C".equals(lStrBillFlag)) {
				strQuery.append("AND rlt.paymentMode=:paymentMode\n");
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "C".equals(lStrBillFlag)) {
				strQuery.append("AND rlt.paymentMode=:paymentMode\n");
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "S".equals(lStrBillFlag)) {
				strQuery.append("AND rlt.paymentMode=:paymentMode\n");
			}

			if (!"-1".equals(lStrSearchStr) && lStrSearchStr != null) {
				if (lStrSearchValue != null && !"-1".equals(lStrSearchValue) && !"".equals(lStrSearchValue)) {
					if (lStrSearchStr.equals("tbr.subject_id")) {
						strQuery.append("AND reg.subjectId = " + lStrSearchValue);
					} else if (lStrSearchStr.equals("tbr.tc_bill")) {
						strQuery.append("AND reg.tcBill LIKE '%" + lStrSearchValue + "%'");
					} else if (lStrSearchStr.equals("our.post_id")) {
						strQuery.append(" AND our.orgPostMstByPostId.postId =" + lStrSearchValue);
					} else if (lStrSearchStr.equals("tbr.bill_Date")) {
						strQuery.append(" AND reg.billDate =:FROMDATE ");
					} else {
						if (lStrSearchStr.equals("reg.ppoNo")) {
							lStrSearchValue = lStrSearchValue.toUpperCase();
						}
						strQuery.append(" AND " + lStrSearchStr + " LIKE '%" + lStrSearchValue + "%' ");

					}
				}
			}
			strQuery.append(" AND reg.subjectId IN (:lLstBillType) \n");

			Query hqlQuery = hibSession.createQuery(strQuery.toString());
			hqlQuery.setParameter("locationCode", locationCode);
			hqlQuery.setParameter("activateFlag", Long.valueOf(1));
			hqlQuery.setParameterList("lLstBillType", lLstBillType);
			if (lStrSearchStr != null && "tbr.bill_Date".equals(lStrSearchStr)) {
				if (lStrSearchValue != null && !"".equals(lStrSearchValue)) {
					hqlQuery.setDate("FROMDATE", lObjSmplDtFmt.parse(lStrSearchValue));
				}
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "D".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_CREATED);

				// For
				// Draft
				// Bills(5)
			} else if (bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_ATO);
				hqlQuery.setShort("otherBillStatus", DBConstants.ST_BILL_REJECTED_BY_TRSRY);
				// For
				// View
				// Bills(10)
			} else if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "A".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_APPROVED);
				hqlQuery.setShort("otherBillStatus", DBConstants.ST_BILL_REJECTED);// For
				// Approved/Rejected Bills
				// Bills(
			} else if (bundleConst.getString("PPMT.TRSRYAUDITORROLE").equals(lStrCurrRoleId)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_TAUD);// For
				// View Approved Bills
				// Bills
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "E".equals(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_USER);
				hqlQuery.setString("paymentMode", bundleConst.getString("PPMT.PAYMODEECS"));
				// For
				// Generate ECS
				// for Bills
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "C".equals(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_USER);
				hqlQuery.setString("paymentMode", bundleConst.getString("PPMT.PAYMODECHQ"));
				// For
				// Generate
				// Cheque
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "S".equals(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_USER);
				hqlQuery.setString("paymentMode", bundleConst.getString("PPMT.PAYMODECASH"));
				// For
				// Generate
				// Cash
			} else if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "DI".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_DISCARD);
				// For
				// Discarded
				// Bills
			} else if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "AR".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_ARCHEIVED);
				hqlQuery.setShort("archievedFlag", Short.parseShort("1"));
				// For
				// Archieve
				// Bills(5)
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId)) {
				hqlQuery.setLong("auditorPostId", lLngPostId);
			}
			lLstBillDtls = hqlQuery.list();
			lIntCount = ((Long) lLstBillDtls.get(0)).intValue();
		} catch (Exception e) {
			logger.error("Error occured during fetching of stg delivery details");
			throw (e);
		}
		return lIntCount;

	}

	public List<SavedPensionBillVO> getDraftBills(Map displayTag, String locationCode, String lStrSearchStr, String lStrSearchValue, List lLstBillType, String lStrCurrRoleId, String lStrBillFlag,
			Long lLngPostId) throws Exception {

		StringBuffer strQuery = null;
		List<SavedPensionBillVO> lLstBillDtls = null;
		List<SavedPensionBillVO> lLstUniqueBillPartyDtls = new ArrayList();
		Map<String, SavedPensionBillVO> partyBillMap = new LinkedHashMap<String, SavedPensionBillVO>();

		Session hibSession = getSession();
		SimpleDateFormat lObjSmplDtFmt = new SimpleDateFormat("dd/MM/yyyy");
		try {

			String[] columnValues = new String[]{"", "reg.ppoNo", "reg.billNo", "rlt.partyName", "reg.subjectId", "reg.billDate", "reg.billNetAmount", "reg.tcBill", "oem.empFname", "rlt.partyAmt"};
			strQuery = new StringBuffer();
			lLstBillDtls = new ArrayList();
			strQuery.append("select new com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO (reg.ppoNo,rlt.partyName,reg.billDate,reg.billNetAmount,reg.tcBill,concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname),reg.subjectId,reg.billNo,reg.currBillStatus,rlt.partyAmt,reg.billCntrlNo) \n");
			strQuery.append("FROM RltBillParty rlt,TrnBillRegister reg,OrgUserpostRlt our,OrgEmpMst oem  \n");
			strQuery.append("WHERE rlt.billNo=reg.billNo \n");
			// strQuery.append("AND hdr.billNo=reg.billNo \n");
			// strQuery.append("AND rpb.billNo=reg.billNo \n");
			strQuery.append("AND reg.audPostId = our.orgPostMstByPostId.postId \n");
			strQuery.append("AND our.orgUserMst.userId = oem.orgUserMst.userId \n");
			strQuery.append("AND reg.locationCode=:locationCode \n");
			strQuery.append("AND our.activateFlag=:activateFlag \n");

			if ((bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "A".equalsIgnoreCase(lStrBillFlag)) || (bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId))) {
				strQuery.append("AND (reg.currBillStatus=:billStatus OR reg.currBillStatus=:otherBillStatus) \n");
			} else {

				strQuery.append("AND ");
				if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "AR".equalsIgnoreCase(lStrBillFlag)) {
					strQuery.append("( \n");
				}
				strQuery.append(" reg.currBillStatus=:billStatus \n");
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "AR".equalsIgnoreCase(lStrBillFlag)) {
				strQuery.append(" OR reg.isArchieved = :archievedFlag )\n");
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId)) {
				strQuery.append("AND reg.audPostId=:auditorPostId \n");
			}
			if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "E".equals(lStrBillFlag)) {
				strQuery.append("AND rlt.paymentMode=:paymentMode\n");
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "C".equals(lStrBillFlag)) {
				strQuery.append("AND rlt.paymentMode=:paymentMode\n");
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "S".equals(lStrBillFlag)) {
				strQuery.append("AND rlt.paymentMode=:paymentMode\n");
			}

			if (!"-1".equals(lStrSearchStr) && lStrSearchStr != null) {
				if (lStrSearchValue != null && !"-1".equals(lStrSearchValue) && !"".equals(lStrSearchValue)) {
					if (lStrSearchStr.equals("tbr.subject_id")) {
						strQuery.append("AND reg.subjectId = " + lStrSearchValue);
					} else if (lStrSearchStr.equals("tbr.tc_bill")) {
						strQuery.append("AND reg.tcBill LIKE '%" + lStrSearchValue + "%'");
					} else if (lStrSearchStr.equals("our.post_id")) {
						strQuery.append(" AND our.orgPostMstByPostId.postId =" + lStrSearchValue);
					} else if (lStrSearchStr.equals("tbr.bill_Date")) {
						strQuery.append(" AND reg.billDate =:FROMDATE ");
					} else {

						if (lStrSearchStr.equals("reg.ppoNo")) {
							lStrSearchValue = lStrSearchValue.toUpperCase();
						}
						strQuery.append(" AND " + lStrSearchStr + " LIKE '%" + lStrSearchValue + "%' ");

					}
				}
			}
			strQuery.append(" AND reg.subjectId IN (:lLstBillType) \n");
			strQuery.append("ORDER BY ");

			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				strQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				strQuery.append("reg.billDate,reg.billNo,reg.ppoNo");
			}
			Query hqlQuery = hibSession.createQuery(strQuery.toString());

			hqlQuery.setParameter("locationCode", locationCode);
			hqlQuery.setParameter("activateFlag", Long.valueOf(1));
			hqlQuery.setParameterList("lLstBillType", lLstBillType);
			if (lStrSearchStr != null && "tbr.bill_Date".equals(lStrSearchStr)) {
				if (lStrSearchValue != null && !"".equals(lStrSearchValue)) {
					hqlQuery.setDate("FROMDATE", lObjSmplDtFmt.parse(lStrSearchValue));
				}
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "D".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_CREATED);
				// For
				// Draft
				// Bills(5)
			} else if (bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_ATO);
				hqlQuery.setShort("otherBillStatus", DBConstants.ST_BILL_REJECTED_BY_TRSRY);
				// For
				// View
				// Bills(10)
			} else if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "A".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_APPROVED);
				hqlQuery.setShort("otherBillStatus", DBConstants.ST_BILL_REJECTED);// For
				// Approved/Rejected Bills
				// Bills(
			} else if (bundleConst.getString("PPMT.TRSRYAUDITORROLE").equals(lStrCurrRoleId)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_TAUD);// For
				// View Approved Bills
				// Bills
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "E".equals(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_USER);
				hqlQuery.setString("paymentMode", bundleConst.getString("PPMT.PAYMODEECS"));
				// For
				// Generate ECS
				// for Bills
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "C".equals(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_USER);
				hqlQuery.setString("paymentMode", bundleConst.getString("PPMT.PAYMODECHQ"));
				// For
				// Generate
				// Cheque
			} else if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId) && "S".equals(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_USER);
				hqlQuery.setString("paymentMode", bundleConst.getString("PPMT.PAYMODECASH"));
				// For
				// Generate
				// Cash
			} else if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "DI".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_DISCARD);
				// For
				// Discarded
				// Bills(5)
			} else if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && "AR".equalsIgnoreCase(lStrBillFlag)) {
				hqlQuery.setShort("billStatus", DBConstants.ST_BILL_ARCHEIVED);
				hqlQuery.setShort("archievedFlag", Short.parseShort("1"));
				// For
				// Archeive
				// Bills(5)
			}
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId)) {
				hqlQuery.setLong("auditorPostId", lLngPostId);
			}
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			hqlQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			hqlQuery.setMaxResults(Constants.PAGE_SIZE);
			lLstBillDtls = hqlQuery.list();
			new ArrayList();

			for (SavedPensionBillVO currentBillVO : lLstBillDtls) {
				SavedPensionBillVO prevBillVO = partyBillMap.get(currentBillVO.getBillNo().toString());

				if (prevBillVO != null) {
					prevBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					prevBillVO.addPartyName(currentBillVO.getPartyName());

				} else {
					currentBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					currentBillVO.addPartyName(currentBillVO.getPartyName());
					partyBillMap.put(currentBillVO.getBillNo().toString(), currentBillVO);
				}
			}

			Iterator<String> lObjBillItr = partyBillMap.keySet().iterator();
			while (lObjBillItr.hasNext()) {
				lLstUniqueBillPartyDtls.add(partyBillMap.get(lObjBillItr.next()));
			}

			// for (SavedPensionBillVO currentBillVO : lLstBillDtls) {
			// partyBillMap.get(currentBillVO.getBillNo().toString());
			// if (!lLstUniqueBillPartyDtls.contains(currentBillVO)) {
			// lLstUniqueBillPartyDtls.add(currentBillVO);
			// }
			// }
		} catch (Exception e) {
			// TODO: use appropriate log statement
			logger.error("Error occured during fetching of stg delivery details");
			throw (e);
		}
		return lLstUniqueBillPartyDtls;

	}

	public List getAuditorsListFromRoleID(Long lLngRoleId, String locationCode) {

		List lLstAuditors = null;
		Session hibSession = getSession();
		StringBuffer lSBBillQry = new StringBuffer();
		lSBBillQry.append("SELECT concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname),oup.orgPostMstByPostId.postId \n");
		lSBBillQry.append(" FROM AclRoleMst arm,AclPostroleRlt apr,OrgUserpostRlt oup,OrgEmpMst oem, OrgPostMst opm \n");
		lSBBillQry.append(" WHERE arm.roleId=apr.aclRoleMst.roleId \n");

		lSBBillQry.append("AND oup.orgPostMstByPostId.postId=apr.orgPostMst.postId \n");
		lSBBillQry.append("AND oup.orgUserMst.userId = oem.orgUserMst.userId \n");
		lSBBillQry.append("AND oup.orgPostMstByPostId.postId=opm.postId\n");
		lSBBillQry.append("AND arm.roleId=:roleId \n");
		lSBBillQry.append("AND opm.locationCode=:locationCode \n");

		Query lHibQry = hibSession.createQuery(lSBBillQry.toString());
		lHibQry.setParameter("locationCode", locationCode);
		lHibQry.setParameter("roleId", lLngRoleId);
		lLstAuditors = lHibQry.list();

		return lLstAuditors;

	}

	public List getPensionerDtls(List billNoList, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = null;
		Query lQuery = null;
		try {
			Session ghibSession = getSession();
			// lSBQuery =
			// lSBQuery.append("SELECT BD.PPO_NO,BD.PENSIONER_NAME,RP.MICR_CODE, RP.ACCNT_NO,BD.REDUCED_PENSION,BR.BILL_NO "
			// +
			// " FROM TRN_PENSION_BILL_HDR BH,TRN_PENSION_BILL_DTLS BD, TRN_BILL_REGISTER BR,RLT_BILL_PARTY RP  WHERE BD.TRN_PENSION_BILL_HDR_ID = BH.TRN_PENSION_BILL_HDR_ID "
			// +
			// " AND BH.FOR_MONTH = BD.PAY_FOR_MONTH AND BR.CURR_BILL_STATUS =:BILLSTATUS  AND BR.BILL_NO = BH.BILL_NO AND RP.BILL_NO = BR.BILL_NO "
			// +
			// " AND BH.REJECTED != 1 AND BR.AUDIT_STATUS != 2 AND BR.LOCATION_CODE = BH.LOCATION_CODE AND BR.LOCATION_CODE = RP.Location_Code "
			// + " AND BH.LOCATION_CODE = :lLocCode "
			// + " AND BH.BILL_NO IN (:billNoList) UNION " +
			// "SELECT BD.PPO_NO,BD.PENSIONER_NAME,RP.MICR_CODE, BD.ACCOUNT_NO,BD.REDUCED_PENSION,BR.BILL_NO "
			// +
			// "FROM TRN_PENSION_BILL_HDR BH,TRN_PENSION_BILL_DTLS BD, TRN_BILL_REGISTER BR,RLT_BANK_BRANCH RP  "
			// +
			// "WHERE BD.TRN_PENSION_BILL_HDR_ID = BH.TRN_PENSION_BILL_HDR_ID "
			// + "AND BH.FOR_MONTH = BD.PAY_FOR_MONTH " +
			// "AND BR.BILL_NO = BH.BILL_NO AND RP.BANK_CODE = BH.BANK_CODE AND RP.BRANCH_CODE = BH.BRANCH_CODE "
			// + "AND BH.REJECTED != 1   "
			// +
			// "AND BR.LOCATION_CODE = BH.LOCATION_CODE AND BR.LOCATION_CODE = RP.LOCATION_CODE  "
			// + "AND BH.LOCATION_CODE = :lLocCode "
			// +
			// "AND  BR.subject_id = 44 AND BR.CURR_BILL_STATUS =:BILLSTATUS AND BH.BILL_NO IN (:billNoList)");
			lSBQuery.append("SELECT BD.PPO_NO,RP.PARTY_NAME,RP.MICR_CODE, RP.ACCNT_NO,RP.PARTY_AMT,BR.BILL_NO "
					+ "FROM TRN_PENSION_BILL_HDR BH,TRN_PENSION_BILL_DTLS BD, TRN_BILL_REGISTER BR,RLT_BILL_PARTY RP  " + "WHERE BD.TRN_PENSION_BILL_HDR_ID = BH.TRN_PENSION_BILL_HDR_ID "
					+ "AND BH.FOR_MONTH = BD.PAY_FOR_MONTH " + "AND BR.CURR_BILL_STATUS =:BILLSTATUS " + "AND BR.BILL_NO = BH.BILL_NO " + "AND RP.BILL_NO = BR.BILL_NO " + "AND BH.REJECTED != 1 "
					+ "AND BR.AUDIT_STATUS != 2 " + "AND BR.LOCATION_CODE = BH.LOCATION_CODE " + "AND BR.LOCATION_CODE = RP.Location_Code " + "AND BH.LOCATION_CODE = :lLocCode "
					+ "AND BH.BILL_NO IN (:billNoList) " + "UNION " + "SELECT BD.PPO_NO,BD.PENSIONER_NAME,RP.MICR_CODE, BD.ACCOUNT_NO,BD.NET_AMOUNT,BR.BILL_NO "
					+ "FROM TRN_PENSION_BILL_HDR BH,TRN_PENSION_BILL_DTLS BD, TRN_BILL_REGISTER BR,RLT_BANK_BRANCH RP " + "WHERE BD.TRN_PENSION_BILL_HDR_ID = BH.TRN_PENSION_BILL_HDR_ID "
					+ "AND BH.FOR_MONTH = BD.PAY_FOR_MONTH " + "AND BR.BILL_NO = BH.BILL_NO " + "AND RP.BANK_CODE = BH.BANK_CODE " + "AND RP.BRANCH_CODE = BH.BRANCH_CODE "
					+ "AND BR.CURR_BILL_STATUS =:BILLSTATUS " + "AND BR.LOCATION_CODE = BH.LOCATION_CODE " + "AND BR.LOCATION_CODE = RP.LOCATION_CODE  " + "AND BH.LOCATION_CODE = :lLocCode "
					+ "AND  BR.subject_id = 44 " + "AND BH.BILL_NO IN (:billNoList) ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setShort("BILLSTATUS", DBConstants.ST_BILL_FORW_TO_USER);
			lQuery.setParameterList("billNoList", billNoList);
			lQuery.setString("lLocCode", lStrLocCode);

			lLstReturn = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstReturn;
	}

	public BigDecimal autoGeneratedECSCode(String locCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		BigDecimal bECSCode = BigDecimal.ZERO;
		try {
			Session ghibSession = getSession();
			Calendar lObjCalendar = Calendar.getInstance();
			String lIntCurrentMonth = String.valueOf(lObjCalendar.get(Calendar.MONTH) + 1);
			String lIntCurrentYear = String.valueOf(lObjCalendar.get(Calendar.YEAR));
			/*
			 * char locCode1 = locCode.charAt(locCode.length() - 1); char
			 * locCode2 = locCode.charAt(locCode.length() - 2); String
			 * locationCode = String.valueOf(locCode2) + locCode1;
			 */
			if (lIntCurrentMonth.length() == 1) {
				lIntCurrentMonth = "0" + lIntCurrentMonth;
			}
			lSBQuery.append(" SELECT MAX(ECS_CODE) lcode FROM TRN_ECS_DTL WHERE ECS_CODE  LIKE '" + lIntCurrentYear + lIntCurrentMonth + "%'");
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("lcode", Hibernate.BIG_INTEGER);

			if (lQuery.uniqueResult() != null) {
				bECSCode = BigDecimal.valueOf(Long.valueOf(lQuery.uniqueResult().toString()));
			} else {
				bECSCode = BigDecimal.valueOf(Long.valueOf(lIntCurrentYear + lIntCurrentMonth + "00"));
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return bECSCode.add(BigDecimal.ONE);
	}

	public void updateECSRqstHdrDtls(List lLstBillNos, Short lStatus, Long gLngPostId, Long gLngUserId, Date gDate) {

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" UPDATE TRN_BILL_REGISTER SET CURR_BILL_STATUS = :lStatus,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO IN (:lBillNos) ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setShort("lStatus", lStatus);
			lQuery.setParameterList("lBillNos", lLstBillNos);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);

		}
	}

	public String getAttDocId(String strECSCode) {

		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;
		String AttDocId = null;
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT CAM.ATTDOC_ID FROM CMN_ATTDOC_MST CAM , TRN_ECS_DTL TED  WHERE TED.ECS_ATTACHMENT_ID = CAM.ATTDOC_ID  AND TED.ECS_CODE =:EcsCode ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("EcsCode", strECSCode);

			if (lQuery.uniqueResult() != null) {
				AttDocId = lQuery.uniqueResult().toString();
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);

		}
		return AttDocId;
	}

	public int getECSForAuthorizationCount(String lStrLocId, String lStrRoleId, String lStrAuthFlag) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = null;
		Query lQuery = null;
		int lIntCount = 0;

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT count(tcd.adviceNo)  " + "FROM TrnChequeDtls tcd  " + "WHERE tcd.locationCode=:lStrLocId " + "AND tcd.chequeType = :CHQTYPE  " + "AND tcd.status=:STATUS ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("CHQTYPE", PensionConstants.ECS);
			if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrRoleId)) {
				lQuery.setShort("STATUS", DBConstants.ST_ECS_GEN);
			} else if (bundleConst.getString("PPMT.PAYMENTATO").equals(lStrRoleId) && lStrAuthFlag.equals("N")) {
				lQuery.setShort("STATUS", DBConstants.ST_ECS_FRWD);
			} else if (bundleConst.getString("PPMT.PAYMENTATO").equals(lStrRoleId) && lStrAuthFlag.equals("Y")) {
				lQuery.setShort("STATUS", DBConstants.ST_ECS_AUTH);
			}

			lQuery.setString("lStrLocId", lStrLocId);
			lLstReturn = lQuery.list();
			lIntCount = ((Long) lLstReturn.get(0)).intValue();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lIntCount;
	}

	public List getECSForAuthorization(Map displayTag, String lStrLocId, String lStrRoleId, String lStrAuthFlag) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = null;
		Query lQuery = null;
		try {
			Session ghibSession = getSession();
			String[] columnValues = new String[]{"", "tcd.adviceNo", "tcd.status", "tcd.chequeId", "rbc.billNo"};

			// lSBQuery
			// .append(" SELECT TCD.CHEQUE_NO ,tcd.status,tcd.cheque_id,rbc.bill_no "
			// + "FROM TRN_CHEQUE_DTLS TCD ,RLT_BILL_CHEQUE rbc "
			// + "WHERE tcd.cheque_id=rbc.cheque_id "
			// + "AND TCD.location_code=:lStrLocId "
			// + "AND TCD.CHEQUE_TYPE = :CHQTYPE  "
			// + "AND tcd.status=:STATUS");

			lSBQuery.append(" SELECT tcd.adviceNo ,tcd.status,tcd.chequeId,tcd.chequeAmt,tcd.fromDt " + "FROM TrnChequeDtls tcd  " + "WHERE tcd.locationCode=:lStrLocId "
					+ "AND tcd.chequeType = :CHQTYPE  " + "AND tcd.status=:STATUS ");

			lSBQuery.append("ORDER BY ");

			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append("tcd.adviceNo DESC");
			}

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("CHQTYPE", PensionConstants.ECS);
			if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrRoleId)) {
				lQuery.setShort("STATUS", DBConstants.ST_ECS_GEN);
			} else if (bundleConst.getString("PPMT.PAYMENTATO").equals(lStrRoleId) && lStrAuthFlag.equals("N")) {
				lQuery.setShort("STATUS", DBConstants.ST_ECS_FRWD);
			} else if (bundleConst.getString("PPMT.PAYMENTATO").equals(lStrRoleId) && lStrAuthFlag.equals("Y")) {
				lQuery.setShort("STATUS", DBConstants.ST_ECS_AUTH);
			}
			lQuery.setString("lStrLocId", lStrLocId);

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);

			lLstReturn = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstReturn;
	}

	public String getChequeIdFromECSCode(String lStrECSCode, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = new ArrayList();

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT tcd.chequeId " + "FROM TrnChequeDtls tcd " + "WHERE tcd.adviceNo=:lStrECSCode " + "AND tcd.locationCode=:lStrLocCode " + "AND tcd.chequeType = :lStrChequeType ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lStrLocCode", lStrLocCode);
			lQuery.setString("lStrECSCode", lStrECSCode);
			lQuery.setString("lStrChequeType", PensionConstants.ECS);

			lLstReturn = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstReturn.get(0).toString();
	}

	public List getBillNosFromECSCode(String lStrECSCode, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = new ArrayList();

		try {
			Session ghibSession = getSession();
			lSBQuery.append("SELECT rbc.billNo FROM TrnChequeDtls tcd, RltBillCheque rbc WHERE tcd.chequeId=rbc.chequeId AND tcd.locationCode=:lStrLocCode AND tcd.chequeType =:lStrChequeType "
					+ "AND tcd.adviceNo=:lStrECSCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lStrLocCode", lStrLocCode);
			lQuery.setString("lStrECSCode", lStrECSCode);
			lQuery.setString("lStrChequeType", PensionConstants.ECS);

			lLstReturn = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstReturn;
	}

	public void updateStatusInChqDtls(List lLstChequeNos, Short status, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" UPDATE TRN_CHEQUE_DTLS TCD " + "SET TCD.STATUS = :STATUS,TCD.updated_user_id = :updated_user_id,TCD.updated_post_id = :updated_post_id,TCD.updated_date = :updated_date "
					+ "WHERE advice_no IN (:chequeNoList)");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setShort("STATUS", status);

			lQuery.setParameterList("chequeNoList", lLstChequeNos);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public void updateDateInChequeDtls(String lStrECSNo, Date lDtAuthDate, String lStrChequeNo, Long gLngPostId, Long gLngUserId, Date gDate, Date lDtSettDate) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" UPDATE TRN_CHEQUE_DTLS TCD "
					+ "SET TCD.authority_date = :authority_date,TCD.settlement_date=:settlement_date,TCD.updated_user_id = :updated_user_id,TCD.updated_post_id = :updated_post_id,TCD.updated_date = :updated_date,TCD.STATUS=:STATUS,TCD.cheque_no=:lStrChequeNo "
					+ "WHERE advice_no = :lStrECSNo");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setDate("authority_date", lDtAuthDate);
			lQuery.setDate("settlement_date", lDtSettDate);
			lQuery.setParameter("lStrECSNo", lStrECSNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.setShort("STATUS", DBConstants.ST_ECS_AUTH);
			lQuery.setParameter("lStrChequeNo", lStrChequeNo);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public void deleteTrnEcsDtl(String lStrECSCode) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnEcsDtl WHERE ecsCode = :ecsCode ");

			lQuery.setParameter("ecsCode", Long.parseLong(lStrECSCode));

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteRltBillCheque(String lStrChequeId) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM RltBillCheque WHERE chequeId = :chequeId ");

			lQuery.setParameter("chequeId", Long.parseLong(lStrChequeId));

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void deleteTrnChequeDtls(String lStrChequeId) throws Exception {

		try {
			Query lQuery = getSession().createQuery(" DELETE FROM TrnChequeDtls WHERE chequeId = :chequeId ");

			lQuery.setParameter("chequeId", Long.parseLong(lStrChequeId));

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public List getRemarksOfBill(Long lLngBillNo, String gStrLocationCode, Long gLngLangId) {

		List lLstTotalUsers = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT concat(concat(concat(concat(em.empFname,' '),em.empMname),' '),em.empLname),rd.roleName,bm.billRemarks FROM TrnBillMvmnt bm,OrgUserMst um, \n");
			lSBQuery.append(" OrgEmpMst em,AclRoleDetailsRlt rd \n");
			lSBQuery.append(" WHERE bm.statusUpdtUserid = um.userId AND um.userId = em.orgUserMst.userId  \n");
			// lSBQuery.append(" AND pm.postId = up.orgPostMstByPostId.postId AND pm.postId = pr.orgPostMst.postId AND pr.aclRoleMst.roleId = rd.aclRoleMst.roleId  \n");
			lSBQuery.append(" AND bm.roleId=rd.aclRoleMst.roleId AND um.activateFlag=1 AND em.activateFlag=1  \n ");
			lSBQuery.append(" AND bm.billNo = :billNo AND bm.locationCode =:locationCode AND  rd.cmnLanguageMst.langId=:langId \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("billNo", lLngBillNo);
			lHibQry.setParameter("langId", gLngLangId);
			lHibQry.setParameter("locationCode", gStrLocationCode);
			lLstTotalUsers = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}

		return lLstTotalUsers;

	}

	public TrnPensionRecoveryDtls getTrnPensionRecoveryDtls(String lStrPensionerCode, String lStrStatus) throws Exception {

		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls = new TrnPensionRecoveryDtls();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionRecoveryDtls A WHERE A.pensionerCode = :lPnsrCode AND A.recoveryFromFlag = :lStatus  AND A.billNo is null ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setString("lStatus", lStrStatus);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {

				lObjTrnPensionRecoveryDtls = (TrnPensionRecoveryDtls) lLstVO.get(0);

			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lObjTrnPensionRecoveryDtls;
	}

	public List isChequeNoExsist(String lStrChequeNo, String lStrLocationCode) throws Exception {

		List<String> lLstResultList = null;
		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT chequeNo FROM TrnChequeDtls WHERE chequeNo=:chequeNo AND locationCode=:locationCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("chequeNo", lStrChequeNo);
			hqlQuery.setString("locationCode", lStrLocationCode);
			lLstResultList = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstResultList;
	}

	public List getRltBillPartyDtls(String lStrBillNo) throws Exception {

		List<String> lLstResultList = null;
		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT partyName,partyAmt FROM RltBillParty WHERE billNo=:billNo ");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("billNo", lStrBillNo);

			lLstResultList = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstResultList;
	}

	public int getAllChequesCount(String lStrLocationCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = null;
		int lIntCount = 0;
		try {
			lLstReturn = new ArrayList();
			Session ghibSession = getSession();
			lSBQuery.append("SELECT COUNT(reg.billNo) " + "FROM TrnBillRegister reg,TrnChequeDtls tcd,RltBillCheque rbc \n" + "WHERE rbc.billNo=reg.billNo " + "AND rbc.chequeId=tcd.chequeId "
					+ "AND tcd.chequeType=:lChequeType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lChequeType", bundleConst.getString("PPMT.PAYMODECHQ"));

			lLstReturn = lQuery.list();

			lIntCount = ((Long) lLstReturn.get(0)).intValue();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lIntCount;
	}

	public List getAllCheques(Map displayTag, String lStrLocationCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<SavedPensionBillVO> lLstReturn = null;
		List<SavedPensionBillVO> lLstUniqueBillPartyDtls = new ArrayList();
		Map<String, SavedPensionBillVO> partyBillMap = new HashMap<String, SavedPensionBillVO>();
		String[] columnValues = new String[]{"reg.ppoNo", "reg.billCntrlNo", "rlt.partyName"};
		try {
			lLstReturn = new ArrayList<SavedPensionBillVO>();
			Session ghibSession = getSession();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO(reg.ppoNo,tcd.partyName,reg.billNo,reg.currBillStatus,tcd.chequeAmt,tcd.chequeNo,tcd.fromDt,reg.billCntrlNo,reg.subjectId) \n"
					+ "FROM TrnBillRegister reg,TrnChequeDtls tcd,RltBillCheque rbc \n"
					+ "WHERE rbc.billNo=reg.billNo "
					+ "AND rbc.chequeId=tcd.chequeId "
					+ "AND tcd.chequeType=:lChequeType "
					+ "and reg.locationCode = :locCode \n");
			lSBQuery.append("ORDER BY ");

			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append("tcd.fromDt DESC");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lChequeType", bundleConst.getString("PPMT.PAYMODECHQ"));
			lQuery.setString("locCode", lStrLocationCode);

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);

			lLstReturn = lQuery.list();

			for (SavedPensionBillVO currentBillVO : lLstReturn) {
				SavedPensionBillVO prevBillVO = partyBillMap.get(currentBillVO.getBillNo().toString());

				if (prevBillVO != null) {
					prevBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					prevBillVO.addPartyName(currentBillVO.getPartyName());
					prevBillVO.addChequeNo(currentBillVO.getChequeNo());
				} else {
					currentBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					currentBillVO.addPartyName(currentBillVO.getPartyName());
					currentBillVO.addChequeNo(currentBillVO.getChequeNo());
					partyBillMap.put(currentBillVO.getBillNo().toString(), currentBillVO);
				}
			}
			Iterator<String> lObjBillItr = partyBillMap.keySet().iterator();
			while (lObjBillItr.hasNext()) {
				lLstUniqueBillPartyDtls.add(partyBillMap.get(lObjBillItr.next()));
			}
		} catch (Exception e) {
			// TODO: use appropriate log statement
			logger.error("Error occured during fetching of stg delivery details");
			throw (e);
		}
		return lLstUniqueBillPartyDtls;
	}

	public int getAllCashPaymentCount(String lStrLocationCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = null;
		int lIntCount = 0;
		try {
			lLstReturn = new ArrayList();
			Session ghibSession = getSession();
			lSBQuery.append("SELECT COUNT(reg.billNo) " + "FROM TrnBillRegister reg,TrnChequeDtls tcd,RltBillCheque rbc \n" + "WHERE rbc.billNo=reg.billNo " + "AND rbc.chequeId=tcd.chequeId "
					+ "AND tcd.chequeType=:lChequeType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lChequeType", bundleConst.getString("PPMT.PAYMODECASH"));

			lLstReturn = lQuery.list();

			lIntCount = ((Long) lLstReturn.get(0)).intValue();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lIntCount;
	}

	public List getAllCashPayment(Map displayTag, String lStrLocationCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<SavedPensionBillVO> lLstReturn = null;
		List<SavedPensionBillVO> lLstUniqueBillPartyDtls = new ArrayList();
		Map<String, SavedPensionBillVO> partyBillMap = new HashMap<String, SavedPensionBillVO>();
		String[] columnValues = new String[]{"reg.ppoNo", "reg.billCntrlNo", "rlt.partyName"};
		try {
			lLstReturn = new ArrayList<SavedPensionBillVO>();
			Session ghibSession = getSession();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO(reg.ppoNo,tcd.partyName,reg.billNo,reg.currBillStatus,tcd.chequeAmt,tcd.chequeNo,tcd.paymentDate,reg.billCntrlNo,reg.subjectId) \n"
					+ "FROM TrnBillRegister reg,TrnChequeDtls tcd,RltBillCheque rbc \n" + "WHERE rbc.billNo=reg.billNo " + "AND rbc.chequeId=tcd.chequeId " + "AND tcd.chequeType=:lChequeType ");
			lSBQuery.append("ORDER BY ");

			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append("tcd.fromDt DESC");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lChequeType", bundleConst.getString("PPMT.PAYMODECASH"));

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);

			lLstReturn = lQuery.list();

			for (SavedPensionBillVO currentBillVO : lLstReturn) {
				SavedPensionBillVO prevBillVO = partyBillMap.get(currentBillVO.getBillNo().toString());

				if (prevBillVO != null) {
					prevBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					prevBillVO.addPartyName(currentBillVO.getPartyName());
					prevBillVO.addChequeNo(currentBillVO.getChequeNo());
				} else {
					currentBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					currentBillVO.addPartyName(currentBillVO.getPartyName());
					currentBillVO.addChequeNo(currentBillVO.getChequeNo());
					partyBillMap.put(currentBillVO.getBillNo().toString(), currentBillVO);
				}
			}
			Iterator<String> lObjBillItr = partyBillMap.keySet().iterator();
			while (lObjBillItr.hasNext()) {
				lLstUniqueBillPartyDtls.add(partyBillMap.get(lObjBillItr.next()));
			}
		} catch (Exception e) {
			// TODO: use appropriate log statement
			logger.error("Error occured during fetching of stg delivery details");
			throw (e);
		}
		return lLstUniqueBillPartyDtls;
	}

	public List<SupplementaryPartyDtlsVO> getSuppBillData(String lStrPnsnCode, String lStrLocCode, String lStrBillType) throws Exception {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		List<SupplementaryPartyDtlsVO> lLstPensiondeDtls = null;
		try {
			Session ghibSession = getSession();
			lLstPensiondeDtls = new ArrayList();
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.SupplementaryPartyDtlsVO(mph.firstName,mpd.bankCode,rbb.branchName,mpd.accountNo,mpd.branchCode,rbb.micrCode,mph.dateOfDeath,rphc.schemeCode,tph.headCode,tph.ledgerNo,tph.pageNo) \n "
					+ "FROM MstPensionerDtls mpd,MstPensionerHdr mph,RltBankBranch rbb,TrnPensionRqstHdr tph,RltPensionHeadcodeChargable rphc \n"
					+ "WHERE mpd.pensionerCode=mph.pensionerCode\n "
					+ "AND mph.pensionerCode = tph.pensionerCode \n"
					+ "AND tph.headCode = rphc.headCode \n"
					+ "AND rphc.billType = :billType \n"
					+ "AND mpd.pensionerCode=:pensionerCode \n"
					+ "AND mpd.locationCode=:locationCode\n " + "AND  rbb.branchCode=mpd.branchCode");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());

			lHibQry.setParameter("pensionerCode", lStrPnsnCode);
			lHibQry.setParameter("locationCode", lStrLocCode);
			if ("PENSION".equals(lStrBillType)) {
				lHibQry.setParameter("billType", "PENSION");
			} else if ("CVP".equals(lStrBillType)) {
				lHibQry.setParameter("billType", "CVP");
			} else if ("DCRG".equals(lStrBillType)) {
				lHibQry.setParameter("billType", "DCRG");
			}
			lLstPensiondeDtls = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstPensiondeDtls;
	}

	public List<SupplementaryPartyDtlsVO> getFamilyDtlsSuppBill(String lStrPnsnCode, String lStrLocCode, String lStrBillType) throws Exception {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		List<SupplementaryPartyDtlsVO> lLstPensiondeDtls = null;
		try {
			Session ghibSession = getSession();
			lLstPensiondeDtls = new ArrayList();
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.SupplementaryPartyDtlsVO(mpf.name,mpd.bankCode,rbb.branchName,mpd.accountNo,mpd.branchCode,rbb.micrCode,rphc.schemeCode,tph.headCode,tph.ledgerNo,tph.pageNo) \n "
					+ "FROM MstPensionerDtls mpd,MstPensionerFamilyDtls mpf,RltBankBranch rbb,TrnPensionRqstHdr tph,RltPensionHeadcodeChargable rphc "
					+ "WHERE mpd.pensionerCode=mpf.pensionerCode AND mpd.pensionerCode=:pensionerCode AND mpd.pensionerCode = tph.pensionerCode \n"
					+ " AND tph.headCode = rphc.headCode "
					+ " AND rphc.billType = :billType \n" + "AND mpf.percentage = :percentage AND mpd.locationCode=:locationCode AND  rbb.branchCode=mpd.branchCode");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());

			lHibQry.setParameter("pensionerCode", lStrPnsnCode);
			lHibQry.setLong("percentage", 100L);
			lHibQry.setParameter("locationCode", lStrLocCode);
			if ("PENSION".equals(lStrBillType)) {
				lHibQry.setParameter("billType", "PENSION");
			} else if ("CVP".equals(lStrBillType)) {
				lHibQry.setParameter("billType", "CVP");
			} else if ("DCRG".equals(lStrBillType)) {
				lHibQry.setParameter("billType", "DCRG");
			}
			lLstPensiondeDtls = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstPensiondeDtls;
	}

	// public List<SupplementaryPartyDtlsVO> getNomineeDtlsSuppBill(String
	// lStrPnsnCode, String lStrLocCode) throws Exception{
	//
	// Query lHibQry = null;
	// StringBuffer lSBQuery = null;
	// List<SupplementaryPartyDtlsVO> lLstPensiondeDtls = null;
	// try {
	// Session ghibSession = getSession();
	// lLstPensiondeDtls = new ArrayList();
	// lSBQuery = new StringBuffer();
	// lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.SupplementaryPartyDtlsVO(mpn.name,mpn.bankCode,rbb.branchName,mpn.accountNo,mpn.branchCode,rbb.micrCode) \n "
	// +
	// "FROM MstPensionerNomineeDtls mpn,RltBankBranch rbb WHERE mpn.pensionerCode=:pensionerCode \n"
	// +
	// "AND mpn.locationCode=:locationCode AND  rbb.branchCode=mpn.branchCode");
	//
	// lHibQry = ghibSession.createQuery(lSBQuery.toString());
	//
	// lHibQry.setParameter("pensionerCode", lStrPnsnCode);
	// lHibQry.setParameter("locationCode", lStrLocCode);
	// lLstPensiondeDtls = lHibQry.list();
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error("Error is :" + e, e);
	// }
	// return lLstPensiondeDtls;
	// }

	public List getNomineeDtlsSuppBill(String lStrPnsnCode, String lStrLocCode) throws Exception {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		List<SupplementaryPartyDtlsVO> lLstPensiondeDtls = null;
		try {
			Session ghibSession = getSession();
			lLstPensiondeDtls = new ArrayList();
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT mpn.name,mpn.bank_code, \n");
			lSBQuery.append("(case when mpn.branch_code IS NOT NULL THEN (select rbb.branch_name from rlt_bank_branch rbb where rbb.branch_code= mpn.branch_code) else '' end),\n");
			lSBQuery.append("mpn.account_no,mpn.branch_code, \n");
			lSBQuery.append("(case when mpn.branch_code IS NOT NULL THEN (select rbb.micr_code from rlt_bank_branch rbb where rbb.branch_code= mpn.branch_code) else 1 end) \n");
			lSBQuery.append("FROM mst_pensioner_nominee_dtls mpn \n");
			lSBQuery.append("WHERE mpn.pensioner_code= :pensionerCode \n");
			lSBQuery.append("AND mpn.location_code = :locationCode \n");

			// lSBQuery.append("SELECT mpn.name,mpn.bank_code,rbb.branch_name,mpn.account_no,mpn.branch_code,rbb.micr_code \n "
			// +
			// "FROM mst_pensioner_nominee_dtls mpn,rlt_bank_branch rbb WHERE mpn.pensioner_code=:pensionerCode \n"
			// +
			// "AND mpn.location_code = :locationCode AND  rbb.branch_code=mpn.branch_code");

			lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());

			lHibQry.setParameter("pensionerCode", lStrPnsnCode);
			lHibQry.setParameter("locationCode", lStrLocCode);
			lLstPensiondeDtls = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstPensiondeDtls;
	}

	public List isValidPPONo(String lStrPPONo, String lStrLocCode, String lStrStatus, Long lLngPostId) throws Exception {

		List<String> lLstResultList = null;
		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery.append("FROM TrnPensionRqstHdr " + "WHERE ppoNo = :ppoNo " + "AND seenFlag=:seenFlag " + "AND status=:status " + "AND caseOwner=:caseOwner AND locationCode=:locationCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("ppoNo", lStrPPONo);
			hqlQuery.setString("status", lStrStatus);
			hqlQuery.setString("seenFlag", bundleConst.getString("IDENT.YES"));
			hqlQuery.setLong("caseOwner", lLngPostId);
			hqlQuery.setString("locationCode", lStrLocCode);
			lLstResultList = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			// e.printStackTrace();
			throw e;
		}
		return lLstResultList;
	}

	public String getBranchName(String lStrBank, String lStrBranch, String lStrLocCode) throws Exception {

		String lStrBranchName = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT B.branchName FROM RltBankBranch B WHERE B.branchCode = :branchCode ");
			lSBQuery.append(" AND B.bankCode = :bankCode AND B.locationCode = :lLocCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("bankCode", lStrBank);
			lQuery.setParameter("branchCode", Long.valueOf(lStrBranch));
			lQuery.setParameter("lLocCode", lStrLocCode);

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty() && lLst.get(0) != null) {
				lStrBranchName = lLst.get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrBranchName;
	}

	public List<TrnPensionRecoveryDtls> getTrnPensionRecoveryDtlsForSupp(String lStrPensionerCode, String lStrStatus, Long lLngBillNo) throws Exception {

		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = new ArrayList();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionRecoveryDtls A WHERE A.pensionerCode = :lPnsrCode AND A.recoveryFromFlag = :lStatus  AND A.billNo = :lLngBillNo ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setString("lStatus", lStrStatus);
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstTrnPensionRecoveryDtls = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return lLstTrnPensionRecoveryDtls;
	}

	public String getBillTypeForSupp(String lStrPensionerCode, Long lLngBillNo, String lStrLocCode) throws Exception {

		String lStrBillType = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT S.billType FROM TrnPensionSupplyBillDtls S WHERE S.pensionerCode = :lStrPensionerCode ");
			lSBQuery.append(" AND S.billNo = :lLngBillNo AND S.locationCode = :lStrLocCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lLngBillNo", lLngBillNo);
			lQuery.setParameter("lStrPensionerCode", lStrPensionerCode);
			lQuery.setParameter("lStrLocCode", lStrLocCode);

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty() && lLst.get(0) != null) {
				lStrBillType = lLst.get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrBillType;
	}

	public TrnPensionSupplyBillDtls getSupplyDtlsVO(String lStrPensionerCode, Long lLngBillNo, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" FROM TrnPensionSupplyBillDtls S WHERE S.pensionerCode = :lStrPensionerCode ");
			lSBQuery.append(" AND S.billNo = :lLngBillNo AND S.locationCode = :lStrLocCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lLngBillNo", lLngBillNo);
			lQuery.setParameter("lStrPensionerCode", lStrPensionerCode);
			lQuery.setParameter("lStrLocCode", lStrLocCode);

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty() && lLst.get(0) != null) {
				lObjTrnPensionSupplyBillDtls = (TrnPensionSupplyBillDtls) lLst.get(0);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lObjTrnPensionSupplyBillDtls;
	}

	public List<String> getPPONoListFromPnsncaseRlt(List<Long> lLstBillNos) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<String> lLstPPONo = new ArrayList<String>();
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT pcb.ppoNo  \n");
			lSBQuery.append(" From RltPensioncaseBill pcb\n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" pcb.billNo in (:lLstBillNos) \n");
			lSBQuery.append(" and pcb.billType = :lBillType \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lLstBillNos", lLstBillNos);
			lQuery.setString("lBillType", "PENSION");
			lLstPPONo = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPPONo;
	}

	public void updateLastPaidDateForPensionBills(List<String> lLstPpoNos, Date lBillDate) throws Exception {

		Calendar lObjCal = new GregorianCalendar();
		lObjCal.setTime(lBillDate);
		int maxDaysInMonth = lObjCal.getActualMaximum(Calendar.DATE);
		lObjCal.set(Calendar.DATE, maxDaysInMonth);
		Date lDtMonthEnd = lObjCal.getTime();
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();

			lSBQuery.append(" UPDATE TrnPensionRqstHdr prh SET prh.lastPaidDate = :lMonthEndDate  \n");
			lSBQuery.append(" where prh.ppoNo in (:lLstPpoNos) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lLstPpoNos", lLstPpoNos);
			lQuery.setDate("lMonthEndDate", oMySQLFormat.parse(oMySQLFormat.format(lDtMonthEnd)));

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);

		}
	}

	public String getIfscCodeFromBranchCode(Long lLngBranchCode, String lStrLocationCode) throws Exception {

		String lStrIfscCode = "";
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();
		Session ghibSession = getSession();

		try {
			lSBQuery.append("SELECT rlt.micrCode  FROM  RltBankBranch rlt WHERE rlt.branchCode=:branchCode AND rlt.locationCode=:locationCode");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("branchCode", lLngBranchCode);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lStrIfscCode = lLstResult.get(0).toString();
				}
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lStrIfscCode;
	}

	public List<String> getPpoNoByBillNo(List<Long> lLstBillNo) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<String> lLstPPONo = new ArrayList<String>();
		List<String> lLstBillType = new ArrayList<String>();
		try {
			Session ghibSession = getSession();
			lLstBillType.add(bundleBillConst.getString("RECOVERY.MONTHLY"));
			lLstBillType.add(bundleBillConst.getString("RECOVERY.PENSION"));
			lSBQuery.append(" Select pbd.ppoNo  \n");
			lSBQuery.append(" From TrnPensionBillHdr pbh,TrnPensionBillDtls pbd \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" pbh.trnPensionBillHdrId = pbd.trnPensionBillHdrId \n");
			lSBQuery.append(" and pbh.billNo in (:lLstBillNo) \n");
			lSBQuery.append(" and pbh.billType in (:lStrBillType) \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lLstBillNo", lLstBillNo);
			lQuery.setParameterList("lStrBillType", lLstBillType);
			lLstPPONo = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPPONo;
	}

	public List<Object[]> getPnsrDtlsForPaymentHistory(String lStrPnsrCode, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstPnsrDtls = null;
		Session ghibSession = getSession();
		List<Short> lLstCurrBillStatus = new ArrayList<Short>();
		Short lShrtBillApprovedStatus = DBConstants.ST_BILL_APPROVED;
		Short lShrtBillAuthorizedStatus = DBConstants.ST_BILL_ECS_AUTH;
		try {
			lLstCurrBillStatus.add(lShrtBillApprovedStatus);
			lLstCurrBillStatus.add(lShrtBillAuthorizedStatus);
			// lSBQuery.append("select dtls.ppo_no,hdr.bill_type,reg.tc_bill,reg.bill_date, dtls.net_amount,\n");
			// lSBQuery.append("(case when hdr.bank_code is not null then (select mb.bank_name  from mst_bank mb where mb.bank_code=hdr.bank_code) else '' end), \n");
			// lSBQuery.append("(case when hdr.branch_code IS NOT NULL THEN (select rbb.branch_name  from rlt_bank_branch rbb where rbb.branch_code=hdr.branch_code) else '' end), \n");
			// lSBQuery.append("clm.loc_name,reg.bill_no,dtls.ecs_rejected_flag,dtls.pay_for_month from trn_bill_register reg,trn_pension_bill_hdr hdr,trn_pension_bill_dtls dtls,cmn_location_mst clm \n");
			// lSBQuery.append("where dtls.pensioner_code=:pensionercode \n");
			// lSBQuery.append("and reg.bill_no=hdr.bill_no \n");
			// lSBQuery.append("and hdr.trn_pension_bill_hdr_id=dtls.trn_pension_bill_hdr_id \n");
			// // lSBQuery.append("and hdr.location_code=:locationcode\n");
			// lSBQuery.append("and clm.location_code=hdr.location_code\n");
			// lSBQuery.append("and reg.curr_bill_status=:currbillstatus\n");
			// lSBQuery.append("order by dtls.pay_for_month \n");

			lSBQuery.append("select dtls.ppo_no,hdr.bill_type,reg.tc_bill,reg.bill_date, dtls.net_amount,\n");
			lSBQuery.append("(case when hdr.bank_code is not null then (select mb.bank_name  from mst_bank mb where mb.bank_code=hdr.bank_code) else '' end),\n");
			lSBQuery.append("(case when hdr.branch_code is not null THEN (select rbb.branch_name  from rlt_bank_branch rbb where rbb.branch_code=hdr.branch_code) else '' end),\n");
			lSBQuery.append("clm.loc_name,reg.bill_no,dtls.ecs_rejected_flag,dtls.pay_for_month \n");
			lSBQuery.append("from trn_bill_register reg,trn_pension_bill_hdr hdr,trn_pension_bill_dtls dtls,cmn_location_mst clm \n");
			lSBQuery.append("where dtls.pensioner_code = :pensionercode \n");
			lSBQuery.append("and reg.bill_no=hdr.bill_no \n");
			lSBQuery.append("and hdr.trn_pension_bill_hdr_id=dtls.trn_pension_bill_hdr_id \n");
			lSBQuery.append("and clm.location_code=hdr.location_code \n");
			lSBQuery.append("and reg.curr_bill_status in  (:currbillstatus) \n");
			lSBQuery.append("union all \n");
			lSBQuery.append("select dtls.ppo_no,hdr.bill_type,reg.tc_bill,reg.bill_date, dtls.net_amount, \n");
			lSBQuery.append("(case when hdr.bank_code is not null then (select mb.bank_name  from mst_bank mb where mb.bank_code=hdr.bank_code) else '' end), \n");
			lSBQuery.append("(case when hdr.branch_code is not null THEN (select rbb.branch_name  from rlt_bank_branch rbb where rbb.branch_code=hdr.branch_code) else '' end),\n");
			lSBQuery.append("clm.loc_name,reg.bill_no,dtls.ecs_rejected_flag,dtls.pay_for_month \n");
			lSBQuery.append("from trn_bill_register reg,trn_pension_bill_hdr_arc hdr,trn_pension_bill_dtls_arc dtls,cmn_location_mst clm \n");
			lSBQuery.append("where dtls.pensioner_code= :pensionercode \n");
			lSBQuery.append("and reg.bill_no=hdr.bill_no \n");
			lSBQuery.append("and hdr.trn_pension_bill_hdr_arc_id=dtls.trn_pension_bill_hdr_arc_id \n");
			lSBQuery.append("and clm.location_code=hdr.location_code \n");
			lSBQuery.append("and reg.curr_bill_status in (:currbillstatus) \n");
			lSBQuery.append("order by 11 \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("pensionercode", lStrPnsrCode);
			// lQuery.setParameter("locationcode", lStrLocCode);
			// lQuery.setParameter("currbillstatus",
			// DBConstants.ST_BILL_ECS_AUTH);
			lQuery.setParameterList("currbillstatus", lLstCurrBillStatus);
			lLstPnsrDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPnsrDtls;
	}

	/**
	 * 
	 * 
	 * <H3>Description -</H3>
	 * 
	 * 
	 * Do not remove this method..
	 * 
	 * @author Vrajesh Raval
	 * @param lStrPensionerCode
	 * @param lStrLocCode
	 * @return
	 * @throws Exception
	 */
	// public List<Object[]> getChequeDtls(String lStrPensionerCode, String
	// lStrLocCode) throws Exception {
	//
	// StringBuilder lSBQuery = new StringBuilder();
	// List<Object[]> lLstPnsrDtls = null;
	// Session ghibSession = getSession();
	// try {
	// lSBQuery.append("select tcd.cheque_no,tcd.from_dt,rbc.bill_no \n");
	// lSBQuery.append("from trn_pension_bill_hdr hdr,trn_pension_bill_dtls dtls,trn_bill_register reg\n");
	// lSBQuery.append("left outer join rlt_bill_cheque rbc  on rbc.bill_no=reg.bill_no\n");
	// lSBQuery.append("left outer join trn_cheque_dtls tcd  on rbc.cheque_id=tcd.cheque_id and tcd.status=:status \n");
	// lSBQuery.append("where dtls.pensioner_code=:pensionercode \n");
	// lSBQuery.append("and reg.bill_no=hdr.bill_no \n");
	// lSBQuery.append("and hdr.trn_pension_bill_hdr_id=dtls.trn_pension_bill_hdr_id \n");
	// // lSBQuery.append("and hdr.location_code=:locationcode\n");
	// lSBQuery.append("and reg.curr_bill_status=:currbillstatus\n");
	//
	// Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	// lQuery.setParameter("pensionercode", lStrPensionerCode);
	// // lQuery.setParameter("locationcode", lStrLocCode);
	// lQuery.setParameter("currbillstatus", DBConstants.ST_BILL_ECS_AUTH);
	// lQuery.setParameter("status", DBConstants.ST_ECS_AUTH);
	// lLstPnsrDtls = lQuery.list();
	//
	// } catch (Exception e) {
	// logger.error("Error is : " + e, e);
	// throw (e);
	// }
	// return lLstPnsrDtls;
	// }

	public List<Object[]> getChequeDtls(String lStrPensionerCode, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstPnsrDtls = null;
		Session ghibSession = getSession();
		try {
			
			lSBQuery.append("SELECT rbp.CHEQUE_NO,rbp.CHEQUE_DATE,tbr.VOUCHER_NO,tbr.bill_no FROM \n");
			lSBQuery.append("TRN_BILL_REGISTER tbr,TRN_PENSION_BILL_HDR tph,TRN_PENSION_BILL_DTLS tpd,rlt_bank_branch rbb,rlt_bill_party rbp \n");
			lSBQuery.append("where tbr.BILL_NO = tph.BILL_NO  \n");
			lSBQuery.append("and tph.TRN_PENSION_BILL_HDR_ID = tpd.TRN_PENSION_BILL_HDR_ID \n"); 
			lSBQuery.append("and (tph.BANK_CODE = rbp.BANK_CODE and rbp.BRANCH_CODE is null) \n");
			lSBQuery.append("and tbr.bill_no = rbp.bill_no \n");
			lSBQuery.append("and tph.BANK_CODE = rbb.BANK_CODE and tph.BRANCH_CODE = rbb.BRANCH_CODE \n");
			lSBQuery.append("and (rbb.REPORTING_BANK_CODE is null and rbb.REPORTING_BRANCH_CODE is null) \n");
			lSBQuery.append("and tpd.PENSIONER_CODE = :pensionercode \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS in (80,20) \n");
			lSBQuery.append("union \n");
			lSBQuery.append("SELECT rbp.CHEQUE_NO,rbp.CHEQUE_DATE,tbr.VOUCHER_NO,tbr.bill_no FROM \n");
			lSBQuery.append("TRN_BILL_REGISTER tbr,TRN_PENSION_BILL_HDR tph,TRN_PENSION_BILL_DTLS tpd,rlt_bank_branch rbb,rlt_bill_party rbp \n");
			lSBQuery.append("where tbr.BILL_NO = tph.BILL_NO  \n");
			lSBQuery.append("and tph.TRN_PENSION_BILL_HDR_ID = tpd.TRN_PENSION_BILL_HDR_ID \n"); 
			lSBQuery.append("and tbr.bill_no = rbp.bill_no \n");
			lSBQuery.append("and tph.BANK_CODE = rbb.BANK_CODE and tph.BRANCH_CODE = rbb.BRANCH_CODE \n");
			lSBQuery.append("and (rbb.REPORTING_BANK_CODE = rbp.BANK_CODE and rbb.REPORTING_BRANCH_CODE = rbp.BRANCH_CODE) \n");
			lSBQuery.append("and tpd.PENSIONER_CODE = :pensionercode \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS in (80,20) ");
			
			
//			lSBQuery.append("select rbp.cheque_no,rbp.cheque_date,rbp.bill_no \n");
//			lSBQuery.append("from trn_pension_bill_hdr hdr,trn_pension_bill_dtls dtls,trn_bill_register reg,rlt_bill_party rbp \n");
//			lSBQuery.append("where dtls.pensioner_code=:pensionercode \n");
//			lSBQuery.append("and reg.bill_no=hdr.bill_no \n");
//			lSBQuery.append("and hdr.trn_pension_bill_hdr_id=dtls.trn_pension_bill_hdr_id \n");
//			lSBQuery.append("and rbp.bill_no = reg.bill_no \n");
//			// lSBQuery.append("and hdr.location_code=:locationcode\n");
//			lSBQuery.append("and reg.curr_bill_status=:currbillstatus\n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("pensionercode", lStrPensionerCode);
			// lQuery.setParameter("locationcode", lStrLocCode);
			//lQuery.setParameter("currbillstatus", DBConstants.ST_BILL_ECS_AUTH);
			lLstPnsrDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPnsrDtls;
	}

	public List<Object[]> getAddressOfCurrentLocation(String lStrLocationCode, String lStrLangId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstAddDetails = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select clm.loc_name ,clm.loc_addr_1 ,clm.loc_addr_2,ccm.city_name ,clm.loc_pin  \n");
			lSBQuery.append("from cmn_location_mst clm ,cmn_city_mst ccm\n");
			lSBQuery.append("where clm.loc_city_id=ccm.city_id \n");
			lSBQuery.append("and clm.loc_id=:locationcode \n");
			lSBQuery.append("and ccm.lang_id=:langId \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("langId", lStrLangId);
			lQuery.setParameter("locationcode", lStrLocationCode);

			lLstAddDetails = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstAddDetails;
	}

	public List<Object[]> getFileName(String ecsCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstFileName = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select ted.ecs_code ,mpg.org_file_name\n");
			lSBQuery.append("from trn_ecs_dtl ted,cmn_attdoc_mst doc,cmn_attachment_mpg mpg\n");
			lSBQuery.append("where ted.ecs_attachment_id=doc.attdoc_id\n");
			lSBQuery.append("and mpg.sr_no = doc.sr_no \n");
			lSBQuery.append("and ted.ecs_code=:ecsCode \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ecsCode", ecsCode);

			lLstFileName = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstFileName;
	}

	public List<Object[]> getChequeAmount(String ecsCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstChequeDtls = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select tcd.cheque_amt,tcd.authority_date\n");
			lSBQuery.append("from trn_cheque_dtls tcd\n");
			lSBQuery.append("where tcd.advice_no =:ecsCode\n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ecsCode", ecsCode);

			lLstChequeDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstChequeDtls;
	}

	public BigInteger getTotalTransactions(String ecsCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstCount = null;
		BigInteger lBgIntTotalCount = BigInteger.ZERO;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select count(rbc.bill_cheque_id)\n");
			lSBQuery.append("from trn_cheque_dtls tcd,rlt_bill_cheque rbc\n");
			lSBQuery.append("where tcd.cheque_id=rbc.cheque_id\n");
			lSBQuery.append("and tcd.advice_no =:ecsCode");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ecsCode", ecsCode);

			lLstCount = lQuery.list();
			lBgIntTotalCount = (BigInteger) lLstCount.get(0);

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lBgIntTotalCount;
	}

	public void saveVoucherDetails(Long lLngVoucherNo, Date lDtVoucherDate, Long lLngUserId, Long lLngPostId, Date lDate, Long lLngBillNo) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" UPDATE trn_bill_register tbr "
					+ "SET tbr.voucher_no = :voucherNo,tbr.updated_user_id = :updated_user_id,tbr.updated_post_id = :updated_post_id,tbr.updated_date = :updated_date,tbr.voucher_date=:voucherDate "
					+ "WHERE tbr.bill_no = :billNo");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("voucherNo", lLngVoucherNo);
			lQuery.setDate("voucherDate", lDtVoucherDate);
			lQuery.setLong("updated_user_id", lLngUserId);
			lQuery.setLong("updated_post_id", lLngPostId);
			lQuery.setDate("updated_date", lDate);
			lQuery.setParameter("billNo", lLngBillNo);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public List getAllData(Long lLngMandateNo, Double lDbNetAmount, String lStrPpoNo, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstChequeDtls = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select dtls.trn_pension_bill_hdr_id\n");
			lSBQuery.append("from trn_ecs_dtl ted,trn_cheque_dtls tcd,rlt_bill_cheque rbc,trn_pension_bill_hdr hdr,trn_pension_bill_dtls dtls,trn_bill_register reg\n");
			lSBQuery.append("where ted.ecs_code=tcd.advice_no\n");
			lSBQuery.append("and tcd.cheque_id=rbc.cheque_id\n");
			lSBQuery.append("and rbc.bill_no=reg.bill_no\n");
			lSBQuery.append("and reg.bill_no=hdr.bill_no\n");
			lSBQuery.append("and hdr.trn_pension_bill_hdr_id=dtls.trn_pension_bill_hdr_id\n");
			lSBQuery.append("and dtls.ppo_no=:ppono\n");
			lSBQuery.append("and dtls.net_amount=:netamount\n");
			lSBQuery.append("and ted.mandate_serial_no=:mandateserialno\n");
			lSBQuery.append("and hdr.location_code=:locationcode\n");
			lSBQuery.append("and reg.curr_bill_status NOT IN (5,-1,10,-15)\n");
			lSBQuery.append("order by dtls.pay_for_month desc");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ppono", lStrPpoNo);
			lQuery.setParameter("netamount", lDbNetAmount);
			lQuery.setParameter("mandateserialno", lLngMandateNo);
			lQuery.setParameter("locationcode", lStrLocationCode);
			lQuery.setMaxResults(1);
			lLstChequeDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstChequeDtls;
	}

	public void updateTrnPensionBillDtls(List<Long> lLngBillHdrId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();

			lSBQuery.append(" UPDATE TrnPensionBillDtls pbd SET pbd.ecsRejectedFlag='Y'  \n");
			lSBQuery.append(" where pbd.trnPensionBillHdrId in (:lLstBillHdrId) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lLstBillHdrId", lLngBillHdrId);
			// lQuery.setDate("lCurrDate",
			// oMySQLFormat.parse(oMySQLFormat.format(gCurrDate)));

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);

		}
	}

	public List<Long> getBillNosFromAdviceNo(String ecsCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> lLstBillNos = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select rbc.bill_no \n");
			lSBQuery.append("from trn_cheque_dtls tcd,rlt_bill_cheque rbc \n");
			lSBQuery.append("where tcd.cheque_id=rbc.cheque_id \n");
			lSBQuery.append("and tcd.advice_no=:ecsCode \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ecsCode", ecsCode);

			lLstBillNos = lQuery.list();

			// if (!lLstResult.isEmpty()) {
			// Iterator lObjIt = lLstResult.iterator();
			// while (lObjIt.hasNext()) {
			// Object lObjArr = lObjIt.next();
			//
			// if (lObjArr != null) {
			// lLstBillNos.add(Long.parseLong(lObjArr.toString()));
			// }
			//
			// }
			// }

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstBillNos;
	}

	public List<Object[]> getPnsrCodeFromBillNo(List<Long> lLstBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstPnsrCodeForMonth = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select pbd.pensionerCode,pbd.payForMonth \n");
			lSBQuery.append("from TrnPensionBillHdr pbh,TrnPensionBillDtls pbd \n");
			lSBQuery.append("where pbh.trnPensionBillHdrId = pbd.trnPensionBillHdrId \n");
			lSBQuery.append("and pbh.billNo  in (:lLstBillNo) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstBillNo", lLstBillNo);
			lLstPnsrCodeForMonth = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPnsrCodeForMonth;
	}

	public void updateSixpayArrearPaidFlag(List<String> lLstStrPnsrCode, Long lLngUserId, Long lLngPostId, Date lCurrDate, Character lCharPaidFlag, Integer lIntPayForMonth, Long lLngBillNo)
			throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getSession();
		// int maxBatchSize = 900;
		// List<String> lLstPnsrCodeBatch = null;
		// List<List<String>> lLstAllPnsrCodeBatch = new
		// ArrayList<List<String>>();
		try {
			// Preparing batches of 900 pensioner codes if list has more than
			// 900
			/*
			 * if (lLstStrPnsrCode.size() > maxBatchSize) { int totalBatches =
			 * lLstStrPnsrCode.size() / maxBatchSize; for (int cnt = 1; cnt <=
			 * totalBatches; cnt++) { lLstPnsrCodeBatch = new
			 * ArrayList<String>();
			 * lLstPnsrCodeBatch.addAll((lLstStrPnsrCode.subList(maxBatchSize *
			 * (cnt - 1), (maxBatchSize * cnt))));
			 * lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch); } int
			 * remainingBranchCodes = lLstStrPnsrCode.size() % maxBatchSize; if
			 * (remainingBranchCodes > 0) { lLstPnsrCodeBatch = new
			 * ArrayList<String>();
			 * lLstPnsrCodeBatch.addAll(lLstStrPnsrCode.subList(maxBatchSize *
			 * totalBatches, (maxBatchSize * totalBatches) +
			 * remainingBranchCodes));
			 * lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch); } } else {
			 * lLstPnsrCodeBatch = new ArrayList<String>();
			 * lLstPnsrCodeBatch.addAll(lLstStrPnsrCode);
			 * lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch); }
			 * lSBQuery.append("update TrnPensionSixpayfpArrear psa \n");
			 * lSBQuery.append("set psa.paidFlag = :lPaidFlag, \n");
			 * lSBQuery.append("psa.updatedUserId = :lLngUserId, \n");
			 * lSBQuery.append("psa.updatedPostId = :lLngPostId, \n");
			 * lSBQuery.append("psa.updatedDate = :lCurrDate \n");
			 * lSBQuery.append
			 * ("where (psa.pensionerCode in (:lLstStrPnsrCode1) \n"); for (int
			 * i = 2; i <= lLstAllPnsrCodeBatch.size(); i++) {
			 * lSBQuery.append(" or psa.pensionerCode in (:lLstStrPnsrCode" + i
			 * + ") "); } lSBQuery.append(" ) \n "); //
			 * lSBQuery.append("and psa.arrearType = :lCharArrearType \n");
			 * lSBQuery.append("and psa.activeFlag = :lActiveFlag \n");
			 * lSBQuery.append("and psa.payInMonth = :lStrPayInMonth \n");
			 * 
			 * lSBQuery.append("update TrnPensionSixpayfpArrear psa \n");
			 * lSBQuery.append("set psa.paidFlag = :lPaidFlag, \n");
			 * lSBQuery.append("psa.updatedUserId = :lLngUserId, \n");
			 * lSBQuery.append("psa.updatedPostId = :lLngPostId, \n");
			 * lSBQuery.append("psa.updatedDate = :lCurrDate \n");
			 * lSBQuery.append
			 * ("where (psa.pensionerCode in (:lLstStrPnsrCode1) \n"); for (int
			 * i = 2; i <= lLstAllPnsrCodeBatch.size(); i++) {
			 * lSBQuery.append(" or psa.pensionerCode in (:lLstStrPnsrCode" + i
			 * + ") "); } lSBQuery.append(" ) \n "); //
			 * lSBQuery.append("and psa.arrearType = :lCharArrearType \n");
			 * lSBQuery.append("and psa.activeFlag = :lActiveFlag \n");
			 * lSBQuery.append("and psa.payInMonth = :lStrPayInMonth \n"); Query
			 * lQuery = ghibSession.createQuery(lSBQuery.toString()); for (int i
			 * = 1; i <= lLstAllPnsrCodeBatch.size(); i++) {
			 * lQuery.setParameterList("lLstStrPnsrCode" + i,
			 * lLstAllPnsrCodeBatch.get(i - 1)); }
			 * lQuery.setCharacter("lPaidFlag", lCharPaidFlag);
			 * lQuery.setBigDecimal("lLngUserId", new BigDecimal(lLngUserId));
			 * lQuery.setBigDecimal("lLngPostId", new BigDecimal(lLngPostId));
			 * lQuery.setDate("lCurrDate", lCurrDate); //
			 * lQuery.setCharacter("lCharArrearType", 'S');
			 * lQuery.setCharacter("lActiveFlag", 'Y');
			 * lQuery.setString("lStrPayInMonth", lIntPayForMonth.toString());
			 * lQuery.executeUpdate();
			 */
			lSBQuery.append("update trn_pension_sixpayfp_arrear psa \n");
			if (lCharPaidFlag.equals('N')) {
				lSBQuery.append("set psa.bill_no = null, \n");
			} else {
				lSBQuery.append("set psa.bill_no = :lLngBillNo, \n");
			}

			lSBQuery.append("psa.paid_Flag = :lPaidFlag, \n");
			lSBQuery.append("psa.updated_User_Id = :lLngUserId, \n");
			lSBQuery.append("psa.updated_Post_Id = :lLngPostId, \n");
			lSBQuery.append("psa.updated_Date = :lCurrDate \n");
			lSBQuery.append("where exists \n");
			lSBQuery.append("( \n");
			lSBQuery.append("	select 1 from trn_bill_register tbr \n");
			lSBQuery.append("	join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no and tbr.bill_no = :lLngBillNo \n");
			lSBQuery.append("	join trn_pension_bill_dtls pbd on pbd.trn_pension_bill_hdr_id = pbh.trn_pension_bill_hdr_id \n");
			lSBQuery.append("	where \n");
			lSBQuery.append("	pbd.pensioner_code = psa.pensioner_code \n");
			lSBQuery.append("	and psa.pay_in_month =  pbh.for_month \n");
			lSBQuery.append("	and pbh.for_month is not null \n");
			lSBQuery.append("	and psa.active_flag = 'Y' \n");
			lSBQuery.append("	and psa.config_from_suppl = 'N' \n");
			if (lCharPaidFlag.equals('N')) {
				lSBQuery.append("	and psa.paid_flag = 'Y' \n");
			}
			lSBQuery.append(") \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setCharacter("lPaidFlag", lCharPaidFlag);
			lQuery.setBigDecimal("lLngUserId", new BigDecimal(lLngUserId));
			lQuery.setBigDecimal("lLngPostId", new BigDecimal(lLngPostId));
			lQuery.setDate("lCurrDate", lCurrDate);
			// lQuery.setString("lStrPayInMonth", lIntPayForMonth.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionBillDAO#getCvpBillDtls(java.lang.String
	 * , java.lang.String)
	 */

	public List getPrintBillDtls(String lStrPnsnrCode, String lStrLocationCode, String lStrCurrDate, String lStrBillType) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstCVPBillDtls = null;
		Session ghibSession = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			if (lStrBillType.equals("CVP")) {
				lSBQuery.append(" SELECT rqst.cvpOrderNo,rstrn.amount ");
			} else {
				lSBQuery.append(" SELECT rqst.dcrgOrderNo,rqst.dcrgAmount ");
			}

			lSBQuery.append(" FROM TrnPensionRqstHdr rqst,TrnCvpRestorationDtls rstrn ");
			lSBQuery.append(" WHERE rstrn.pensionerCode=rqst.pensionerCode ");
			lSBQuery.append(" AND rstrn.restnAplnReceivedFlag = 'N' AND rstrn.restnAplnReceivedDate IS NULL ");
			lSBQuery.append(" AND  :currDate BETWEEN rstrn.fromDate AND rstrn.toDate ");
			lSBQuery.append(" AND rstrn.pensionerCode=:pensionerCode AND rstrn.locationCode = :locationCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", lStrPnsnrCode);
			hqlQuery.setParameter("locationCode", Long.valueOf(lStrLocationCode));
			hqlQuery.setDate("currDate", StringUtility.convertStringToDate(lStrCurrDate.trim()));
			lLstCVPBillDtls = hqlQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);

		}
		return lLstCVPBillDtls;

	}

	public void updateBillNoForRecovery(List<String> lLstStrPnsrCode, Integer lIntForMonth, String lStrLocCode, Long lLngBillNo, Long gLngUserId, Long gLngPostId, Date gDate) throws Exception {

		int maxBatchSize = 900;
		List<String> lLstPnsrCodeBatch = null;
		List<List<String>> lLstAllPnsrCodeBatch = new ArrayList<List<String>>();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			// Preparing batches of 900 pensioner codes if list has more than
			// 900
			if (lLstStrPnsrCode.size() > maxBatchSize) {
				int totalBatches = lLstStrPnsrCode.size() / maxBatchSize;
				for (int cnt = 1; cnt <= totalBatches; cnt++) {
					lLstPnsrCodeBatch = new ArrayList<String>();
					lLstPnsrCodeBatch.addAll((lLstStrPnsrCode.subList(maxBatchSize * (cnt - 1), (maxBatchSize * cnt))));
					lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch);
				}
				int remainingBranchCodes = lLstStrPnsrCode.size() % maxBatchSize;
				if (remainingBranchCodes > 0) {
					lLstPnsrCodeBatch = new ArrayList<String>();
					lLstPnsrCodeBatch.addAll(lLstStrPnsrCode.subList(maxBatchSize * totalBatches, (maxBatchSize * totalBatches) + remainingBranchCodes));
					lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch);
				}
			} else {
				lLstPnsrCodeBatch = new ArrayList<String>();
				lLstPnsrCodeBatch.addAll(lLstStrPnsrCode);
				lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch);
			}
			lSBQuery.append("update TrnMonthlyPensionRecoveryDtls set billNo = :billNo,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date  \n");
			lSBQuery.append(" where \n");
			lSBQuery.append(" (pensionerCode in (:lLstStrPnsrCode1) \n");
			for (int i = 2; i <= lLstAllPnsrCodeBatch.size(); i++) {
				lSBQuery.append(" or pensionerCode in (:lLstStrPnsrCode" + i + ") ");
			}
			lSBQuery.append(" ) \n");
			lSBQuery.append("and changeStmntStatus = :lChngStmntStatus \n");
			lSBQuery.append("and forMonth = :forMonth and locationCode=:locationCode \n");
			// Query lQuery = getSession().createQuery(
			// " update TrnMonthlyPensionRecoveryDtls set billNo = :billNo,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date  "
			// +
			// " where  pensionerCode in (:pensionerCode)  and forMonth = :forMonth and locationCode=:locationCode");

			Query lQuery = getSession().createQuery(lSBQuery.toString());
			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setString("lChngStmntStatus", bundleBillConst.getString("CHANGSTMNTSTATUS.APPROVED"));
			lQuery.setString("locationCode", lStrLocCode);
			for (int i = 1; i <= lLstAllPnsrCodeBatch.size(); i++) {
				lQuery.setParameterList("lLstStrPnsrCode" + i, lLstAllPnsrCodeBatch.get(i - 1));
			}
			lQuery.setInteger("forMonth", lIntForMonth);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public List getCashPaymentPensioners(String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstPnsrCodeForMonth = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("select new com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO (reg.ppoNo,rlt.partyName,reg.billDate,reg.billNetAmount,reg.tcBill,reg.subjectId,reg.billNo,reg.currBillStatus,rlt.partyAmt,reg.billCntrlNo) \n");
			lSBQuery.append("from RltBillParty rlt,TrnBillRegister reg \n");
			lSBQuery.append("where rlt.billNo=reg.billNo \n");
			lSBQuery.append(" and rlt.paymentMode=:paymentMode \n");
			lSBQuery.append(" and reg.locationCode=:lStrLocationCode \n");
			lSBQuery.append(" and reg.currBillStatus=:billStatus \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lStrLocationCode", lStrLocationCode);
			lQuery.setString("paymentMode", bundleConst.getString("PPMT.PAYMODECASH"));
			lQuery.setShort("billStatus", DBConstants.ST_BILL_FORW_TO_USER);

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPnsrCodeForMonth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionBillDAO#getBillVoucherMappingCount(
	 * java.lang.String)
	 */
	public int getBillVoucherMappingCount(String lStrLocationCode, Long lLngPostId) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstResult = null;
		Session ghibSession = null;
		int lIntTotalRecords = 0;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" select count(tbr.billNo) \n");
			lSBQuery.append(" from TrnBillRegister tbr, \n");
			lSBQuery.append(" RltBillParty rbp  \n");
			lSBQuery.append(" where tbr.billNo=rbp.billNo  \n");
			lSBQuery.append(" and tbr.audPostId=:auditorPostId \n");
			lSBQuery.append(" and tbr.currBillStatus = :status and tbr.isArchieved = 0\n");
			lSBQuery.append(" and tbr.locationCode = :locationCode \n");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setShort("status", DBConstants.ST_BILL_ECS_AUTH);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			hqlQuery.setLong("auditorPostId", lLngPostId);
			lLstResult = hqlQuery.list();
			lIntTotalRecords = ((Long) lLstResult.get(0)).intValue();

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);

		}
		return lIntTotalRecords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionBillDAO#getBillVoucherMappingList(java
	 * .lang.String, java.lang.String) DO NOT REMOVE THIS METHOD.
	 */
	// public List<SavedPensionBillVO> getBillVoucherMappingList(String
	// lStrLocationCode) throws Exception {
	//
	// StringBuilder lSBQuery = null;
	// Query hqlQuery = null;
	// List<SavedPensionBillVO> lLstResult = null;
	// Session ghibSession = null;
	// try {
	// ghibSession = getSession();
	// lSBQuery = new StringBuilder();
	//
	// lSBQuery.append(" select new com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO(tbr.billNo,tbr.billCntrlNo,rbp.partyName, \n");
	// lSBQuery.append(" tbr.voucherNo,CASE WHEN tcd.chequeType = :chequeType THEN tcd.authorityDate ELSE tcd.fromDt END AS voucherDate \n");
	// lSBQuery.append(" ,tbr.billNetAmount,tcd.adviceNo,tcd.chequeNo) \n");
	// lSBQuery.append(" from TrnBillRegister tbr, \n");
	// lSBQuery.append(" RltBillCheque rbc, TrnChequeDtls tcd, RltBillParty rbp  \n");
	// lSBQuery.append(" where tbr.billNo=rbc.billNo \n");
	// lSBQuery.append(" and rbc.chequeId=tcd.chequeId \n");
	// lSBQuery.append(" and tbr.billNo=rbp.billNo \n");
	// lSBQuery.append(" and tbr.currBillStatus = :status and tbr.isArchieved = 0\n");
	// lSBQuery.append(" and tbr.locationCode = :locationCode \n");
	//
	// hqlQuery = ghibSession.createQuery(lSBQuery.toString());
	// hqlQuery.setParameter("chequeType", DBConstants.CHEQ_TYPE_ECS);
	// hqlQuery.setShort("status", DBConstants.ST_BILL_ECS_AUTH);
	// // hqlQuery.setParameter("status", 80);
	// hqlQuery.setParameter("locationCode", lStrLocationCode);
	//
	// lLstResult = hqlQuery.list();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error("Error is : " + e, e);
	// throw (e);
	//
	// }
	// return lLstResult;
	// }

	public List<SavedPensionBillVO> getBillVoucherMappingList(String lStrLocationCode, Long lLngPostId) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<SavedPensionBillVO> lLstResult = null;
		Session ghibSession = null;
		Map<String, SavedPensionBillVO> partyBillMap = new HashMap<String, SavedPensionBillVO>();
		List<SavedPensionBillVO> lLstUniqueBillPartyDtls = new ArrayList();
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" select new com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO(tbr.billNo,tbr.billCntrlNo,rbp.partyName, \n");
			lSBQuery.append(" tbr.voucherNo,tbr.voucherDate, \n");
			lSBQuery.append(" tbr.billNetAmount,rbp.chequeNo,rbp.partyAmt,rbp.billPartyId,tbr.subjectId,tbr.ppoNo) \n");
			lSBQuery.append(" from TrnBillRegister tbr, \n");
			lSBQuery.append(" RltBillParty rbp  \n");
			lSBQuery.append(" where tbr.billNo=rbp.billNo \n");
			lSBQuery.append(" and tbr.audPostId=:auditorPostId \n");
			lSBQuery.append(" and tbr.currBillStatus = :status and tbr.isArchieved = 0 \n");
			lSBQuery.append(" and tbr.locationCode = :locationCode \n");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setShort("status", DBConstants.ST_BILL_ECS_AUTH);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			hqlQuery.setLong("auditorPostId", lLngPostId);
			lLstResult = hqlQuery.list();
			for (SavedPensionBillVO currentBillVO : lLstResult) {
				SavedPensionBillVO prevBillVO = partyBillMap.get(currentBillVO.getBillNo().toString());

				if (prevBillVO != null) {
					prevBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					prevBillVO.addPartyName(currentBillVO.getPartyName());
					prevBillVO.addChequeNo(currentBillVO.getChequeNo());
					prevBillVO.addBillPartyId(currentBillVO.getBillPartyId());

				} else {
					currentBillVO.addChequeAmounts(currentBillVO.getPartyAmount());
					currentBillVO.addPartyName(currentBillVO.getPartyName());
					currentBillVO.addChequeNo(currentBillVO.getChequeNo());
					currentBillVO.addBillPartyId(currentBillVO.getBillPartyId());
					partyBillMap.put(currentBillVO.getBillNo().toString(), currentBillVO);
				}
			}

			Iterator<String> lObjBillItr = partyBillMap.keySet().iterator();
			while (lObjBillItr.hasNext()) {
				lLstUniqueBillPartyDtls.add(partyBillMap.get(lObjBillItr.next()));
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);

		}
		return lLstUniqueBillPartyDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionBillDAO#archieveBill(java.util.List,
	 * java.lang.String)
	 */
	public void archieveBill(List<Long> lLstBillNo, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getSession();
		try {
			lSBQuery.append("update TrnBillRegister set isArchieved = 1 where billNo in (:billNoList) ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("billNoList", lLstBillNo);

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	/**
	 * 
	 * <H3>Description -</H3> This method is used to set bill no in all arrear
	 * details of the pensioner on generation of bill.
	 * 
	 * 
	 * @author Shripal Soni
	 * @param lLstStrPnsrCode
	 * @param lLngBillNo
	 * @param lLngUpdtUserId
	 * @param lLngUpdtPostId
	 * @param lDtUpdtDate
	 * @throws Exception
	 */
	public void updateBillNoInArrearDtls(List<String> lLstStrPnsrCode, Long lLngBillNo, Integer lIntForMonth, Long lLngUpdtUserId, Long lLngUpdtPostId, Date lDtUpdtDate) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		Session ghibSession = getSession();
		int maxBatchSize = 900;
		List<String> lLstPnsrCodeBatch = null;
		List<List<String>> lLstAllPnsrCodeBatch = new ArrayList<List<String>>();
		try {
			// Preparing batches of 900 pensioner codes if list has more than
			// 900
			if (lLstStrPnsrCode.size() > maxBatchSize) {
				int totalBatches = lLstStrPnsrCode.size() / maxBatchSize;
				for (int cnt = 1; cnt <= totalBatches; cnt++) {
					lLstPnsrCodeBatch = new ArrayList<String>();
					lLstPnsrCodeBatch.addAll((lLstStrPnsrCode.subList(maxBatchSize * (cnt - 1), (maxBatchSize * cnt))));
					lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch);
				}
				int remainingBranchCodes = lLstStrPnsrCode.size() % maxBatchSize;
				if (remainingBranchCodes > 0) {
					lLstPnsrCodeBatch = new ArrayList<String>();
					lLstPnsrCodeBatch.addAll(lLstStrPnsrCode.subList(maxBatchSize * totalBatches, (maxBatchSize * totalBatches) + remainingBranchCodes));
					lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch);
				}
			} else {
				lLstPnsrCodeBatch = new ArrayList<String>();
				lLstPnsrCodeBatch.addAll(lLstStrPnsrCode);
				lLstAllPnsrCodeBatch.add(lLstPnsrCodeBatch);
			}

			lSBQuery.append("Update TrnPensionArrearDtls  \n");
			lSBQuery.append("set billNo = :lLngBillNo, \n");
			lSBQuery.append("updatedUserId = :lLngUpdtUserId, \n");
			lSBQuery.append("updatedPostId = :lLngUpdtPostId, \n");
			lSBQuery.append("updatedDate = :lDtUpdtDate \n");
			lSBQuery.append("where (pensionerCode in (:lLstStrPnsrCode1) \n");
			for (int i = 2; i <= lLstAllPnsrCodeBatch.size(); i++) {
				lSBQuery.append(" or pensionerCode in (:lLstStrPnsrCode" + i + ") ");
			}
			lSBQuery.append(" )");
			lSBQuery.append("and (paymentFromYyyymm <= :lIntForMonth and paymentToYyyymm >= :lIntForMonth) \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lQuery.setLong("lLngUpdtUserId", lLngUpdtUserId);
			lQuery.setLong("lLngUpdtPostId", lLngUpdtPostId);
			lQuery.setDate("lDtUpdtDate", lDtUpdtDate);
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			for (int i = 1; i <= lLstAllPnsrCodeBatch.size(); i++) {
				lQuery.setParameterList("lLstStrPnsrCode" + i, lLstAllPnsrCodeBatch.get(i - 1));
			}
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	/**
	 * 
	 * <H3>Description -</H3> This method is used to set paid flag to Y on
	 * approval of bill. On rejection of bill set paid flag to N and bill number
	 * to null.
	 * 
	 * 
	 * @author Shripal Soni
	 * @param lLngBillNo
	 * @param lStrPaidFlagTo
	 * @param lLngUpdtUserId
	 * @param lLngUpdtPostId
	 * @param lDtUpdtDate
	 * @throws Exception
	 */
	public void updatePaidFlagInArrearDtlsOnApproveReject(List<Long> lLstBillNo, String lStrPaidFlagTo, Long lLngUpdtUserId, Long lLngUpdtPostId, Date lDtUpdtDate) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("Update TrnPensionArrearDtls  \n");
			lSBQuery.append(" set \n");
			// On rejection of bill set paid flag to N and set bill no to null.
			// On approval of bill set paid flag to Y.
			if ("N".equals(lStrPaidFlagTo)) {
				lSBQuery.append("billNo = :lLngBillNoTo, \n");
			}
			lSBQuery.append("paidFlag = :lStrPaidFlag, \n");
			lSBQuery.append("updatedUserId = :lLngUpdtUserId, \n");
			lSBQuery.append("updatedPostId = :lLngUpdtPostId, \n");
			lSBQuery.append("updatedDate = :lDtUpdtDate \n");
			lSBQuery.append("where billNo in (:lLstBillNo) \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			if ("N".equals(lStrPaidFlagTo)) {
				lQuery.setParameter("lLngBillNoTo", null);
			}
			lQuery.setParameterList("lLstBillNo", lLstBillNo);
			lQuery.setLong("lLngUpdtUserId", lLngUpdtUserId);
			lQuery.setLong("lLngUpdtPostId", lLngUpdtPostId);
			lQuery.setDate("lDtUpdtDate", lDtUpdtDate);
			lQuery.setString("lStrPaidFlag", lStrPaidFlagTo);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public List<Object[]> getPensionOuterBillData(String lStrBillNo, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Session ghibSession = getSession();

		try {
			lSBQuery.append("select tbr.billNo,tbr.billCntrlNo,tbr.schemeNo,ms.schemeName,mb.bankName,rbb.branchName,tbh.grossAmount, \n");
			lSBQuery.append("tbh.deductionA,tbh.netAmount,ms.majorHead,ms.subMajorHead,ms.minorHead, \n");
			lSBQuery.append("ms.subMinorHead,ms.subHead,ms.demandCode,ms.charged,ms.plan,tbh.forMonth,tpd.pensionerCode \n");
			lSBQuery.append("from TrnBillRegister tbr,TrnPensionBillHdr tbh,TrnPensionBillDtls tpd,RltBillParty rbp,MstBank mb,RltBankBranch rbb,MstScheme ms \n");
			lSBQuery.append("where tbr.billNo = tbh.billNo \n");
			lSBQuery.append("and tbh.trnPensionBillHdrId = tpd.trnPensionBillHdrId \n");
			lSBQuery.append("and tbr.billNo = rbp.billNo \n");
			lSBQuery.append("and rbp.bankCode = mb.bankCode \n");
			lSBQuery.append("and rbp.branchCode = rbb.branchCode \n");
			lSBQuery.append("and tbr.schemeNo = ms.schemeCode \n");
			lSBQuery.append("and tbr.billNo = :billNo \n");
			lSBQuery.append("and tbr.locationCode = :locationCode \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getPensionInnerBillData(String lStrBillNo, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Session ghibSession = getSession();

		try {

			lSBQuery.append("select tbr.billNo,tbr.billCntrlNo,rbp.bankCode,rbp.branchCode,mb.bankName,rbb.branchName,rbb.ifscCode, \n");
			lSBQuery.append("mph.series,mph.mainCatCode,mph.mainCatDesc,tprh.ledgerNo,tprh.pageNo, \n");
			lSBQuery.append("tbr.schemeNo,ms.schemeName,tph.payMode,tph.forMonth,tpb.pensionerCode,tpb.ppoNo,rbp.partyName, \n");
			lSBQuery.append("rbp.accntNo,tprh.ropType,tpb.pensionAmount, \n");
			lSBQuery.append("tpb.pensnCutAmount,tpb.gallantryAmount,tpb.other1,tpb.allcationBf11156, \n");
			lSBQuery.append("tpb.allcationAf11156,tpb.allcationAf10560,tpb.allcationBf1436,tpb.allcationAfZp, \n");
			lSBQuery.append("tpb.ir1Amount,tpb.ir2Amount,tpb.ir3Amount,tpb.dpAmount,tpb.tiAmount,tpb.peonAllowance,tpb.adpAmount, \n");
			lSBQuery.append("tpb.medicalAllowenceAmount,tpb.grossAmount,tpb.arrearAmount,tpb.arrear6PC,tpb.arrearCommutation, \n");
			lSBQuery.append("tpb.arrearOthrDiff,tpb.arrearLC,tpb.recoveryAmount,tpb.netAmount, \n");
			lSBQuery.append("ms.majorHead,ms.subMajorHead,ms.minorHead, \n");
			lSBQuery.append("ms.subMinorHead,ms.subHead,ms.demandCode,ms.charged,ms.plan \n");
			lSBQuery.append("from TrnBillRegister tbr,TrnPensionBillHdr tph,TrnPensionBillDtls tpb,RltBillParty rbp,TrnPensionRqstHdr tprh, \n");
			lSBQuery.append("MstBank mb,RltBankBranch rbb,MstScheme ms,MstPensionHeadcode mph  \n");
			lSBQuery.append("where tbr.billNo = tph.billNo  \n");
			lSBQuery.append("and tph.trnPensionBillHdrId = tpb.trnPensionBillHdrId \n");
			lSBQuery.append("and tbr.billNo = rbp.billNo \n");
			lSBQuery.append("and tpb.pensionerCode = tprh.pensionerCode \n");
			lSBQuery.append("and rbp.bankCode = mb.bankCode  \n");
			lSBQuery.append("and rbp.branchCode = rbb.branchCode  \n");
			lSBQuery.append("and tbr.schemeNo = ms.schemeCode \n");
			lSBQuery.append("and tprh.headCode = mph.headCode  \n");
			lSBQuery.append("and tbr.billNo =  :billNo  \n");
			lSBQuery.append("and tbr.locationCode = :locationCode  \n");
			lSBQuery.append("order by mb.bankName,rbb.branchName,tprh.ropType,tprh.ledgerNo,tprh.pageNo,tpb.pensionerName \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lLstResult = lQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public void updateSixpayArrearDtlBySupplementaryBill(Long lLngBillNo, Character lCharPaidFlag, Long lLngUserId, Long lLngPostId, Date lCurrDate) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("update TrnPensionSixpayfpArrear psa \n");
			lSBQuery.append("set psa.paidFlag = :lPaidFlag, \n");
			if (lCharPaidFlag == 'N') {
				lSBQuery.append("psa.activeFlag = :lActvFlag, \n");
			}
			lSBQuery.append("psa.updatedUserId = :lLngUserId, \n");
			lSBQuery.append("psa.updatedPostId = :lLngPostId, \n");
			lSBQuery.append("psa.updatedDate = :lCurrDate \n");
			lSBQuery.append("where \n");
			lSBQuery.append("psa.billNo = :lLngBillNo \n");
			if (lCharPaidFlag == 'Y') {
				lSBQuery.append("and psa.payInMonth is not null \n");
			}
			lSBQuery.append("and psa.configFromSuppl = :lCharConfigFromSupple \n ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lQuery.setBigDecimal("lLngUserId", new BigDecimal(lLngUserId));
			lQuery.setBigDecimal("lLngPostId", new BigDecimal(lLngPostId));
			lQuery.setDate("lCurrDate", lCurrDate);
			lQuery.setCharacter("lPaidFlag", lCharPaidFlag);
			lQuery.setCharacter("lCharConfigFromSupple", 'Y');
			if (lCharPaidFlag == 'N') {
				lQuery.setCharacter("lActvFlag", 'N');
			}
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public void deleteSixpayArrearConfigBySuppleBill(List<Long> lLstBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Session ghibSession = getSession();
		try {
			lSBQuery.append("delete from  TrnPensionSixpayfpArrear psa \n");
			lSBQuery.append("where psa.billNo in (:billNoList) \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("billNoList", lLstBillNo);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public List<Object[]> getSubjectIdFromBillNo(List lLstBillNo, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Session ghibSession = getSession();

		try {
			lSBQuery.append("select billNo,subjectId from TrnBillRegister where billNo in (:billNoList) and locationCode = :locationCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setParameterList("billNoList", lLstBillNo);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;

	}

	
	public void updateStatusInSupplyBillDtls(List lLstBillNo, Short lStStatus,
			Long lLngPostId, Long lLngUserId, Date lDtCurDate) throws Exception{
		// TODO Auto-generated method stub
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("update TrnPensionSupplyBillDtls set status = :status,updatedUserId = :updatedUserId,updatedPostId = :updatedPostId,updatedDate = :updatedDate where billNo in (:billNoList) \n");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			
			lQuery.setShort("status", lStStatus);
			lQuery.setBigDecimal("updatedUserId", new BigDecimal(lLngUserId));
			lQuery.setBigDecimal("updatedPostId", new BigDecimal(lLngPostId));
			lQuery.setDate("updatedDate", lDtCurDate);
			lQuery.setParameterList("billNoList",lLstBillNo);
									
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	
	public HstPnsnPmntDcrgDtls getDcrgDtlsFromBillNo(Long lLngBillNo)
			throws Exception {
		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List<HstPnsnPmntDcrgDtls> lLstResult = null;
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = new HstPnsnPmntDcrgDtls();
		Session ghibSession = getSession();

		try {
			lSBQuery.append("select P from HstPnsnPmntDcrgDtls P where P.billNo = :billNo");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo",lLngBillNo);
			
			lLstResult = lQuery.list();
			
			if(lLstResult != null && !lLstResult.isEmpty())
			{
				lObjHstPnsnPmntDcrgDtls = lLstResult.get(0);
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lObjHstPnsnPmntDcrgDtls;
	}

	
	public HstCommutationDtls getCvpDtlsFromBillNo(Long lLngBillNo)
			throws Exception {
		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List<HstCommutationDtls> lLstResult = null;
		HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();
		Session ghibSession = getSession();

		try {
			lSBQuery.append("select P from HstCommutationDtls P where P.billNo = :billNo");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo",lLngBillNo);
			
			lLstResult = lQuery.list();
			
			if(lLstResult != null && !lLstResult.isEmpty())
			{
				lObjHstCommutationDtls = lLstResult.get(0);
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lObjHstCommutationDtls;
	}
}