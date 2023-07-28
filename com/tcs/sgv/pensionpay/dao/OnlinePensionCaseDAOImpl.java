/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.valueobject.SavedCasesVO;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 25, 2011
 */
public class OnlinePensionCaseDAOImpl extends GenericDaoHibernateImpl implements OnlinePensionCaseDAO {

	/**
	 * @param type
	 */
	public OnlinePensionCaseDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 */

	private Log logger = LogFactory.getLog(getClass());
	private static final Logger gLogger = Logger.getLogger(OnlinePensionCaseDAOImpl.class);

	Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public List<SavedCasesVO> getPensionerDetails(String lStrShowCaseFor, Map inputMap, Long lLngPostId, String lStrToRole, List<String> caseStatusList, String locationCode, Map displayTag,
			String lStrSearchCrt, String lStrSearchVal) throws Exception {

		List lLstPensioners = new ArrayList();
		List<SavedCasesVO> lLstSavedCaseVO = new ArrayList<SavedCasesVO>();
		StringBuilder lSBQuery = new StringBuilder();
		String[] columnValues = null;
		String lStrColumnValues = null;
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		// "prh.commencement_date"
		lSBQuery.append("Select \n");
		if (bundleConst.getString("CASEFOR.RCVONL").equals(lStrShowCaseFor)) {
			columnValues = new String[]{"", "prh.ppo_no", "mph.first_name", "mpd.registration_no", "prh.PPO_INWARD_DATE", "prh.ppo_reg_date", "mpd.branch_code", "mb.bank_name", "rlt.branch_name",
					"mpd.acount_no", "audi.post_id", "prh.type_flag", "prh.inward_mode"};
			lStrColumnValues = "prh.ppo_no," + "mph.pensioner_id," + "mpd.pensioner_dtls_id," + "mph.first_name," + "mpd.registration_no," + "prh.PPO_INWARD_DATE," + "mpd.branch_code,"
					+ "mb.bank_name," + "rlt.branch_name," + "cast('' as char)," + "audi.post_id," + "prh.type_flag," + "prh.inward_mode," + "cast('' as char)," + "cast('' as char),"
					+ "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "mpd.acount_no,"
					+ "cast('' as char)," + "cast('' as char)," + "mpd.bank_code," + "prh.pension_request_id," + "cast('' as char)," + "emp.emp_fname," + "emp.emp_mname," + "emp.emp_lname,"
					+ "prh.ppo_reg_date," + "pfd.name," + "mph.date_of_death";
		} else if (bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
			columnValues = new String[]{"", "prh.ppo_no", "mph.first_name", "mph.date_of_retirement", "prh.commencement_date", "mpd.branch_code", "mb.bank_name", "rlt.branch_name", "mpd.acount_no",
					"audi.post_id", "sch.call_date", "sch.slot_time", "sch.slot_time", "sch.schedule_status"};
			lStrColumnValues = "prh.ppo_no," + "mph.pensioner_id," + "mpd.pensioner_dtls_id," + "mph.first_name," + "cast('' as char)," + "prh.PPO_INWARD_DATE," + "mpd.branch_code," + "mb.bank_name,"
					+ "rlt.branch_name," + "cast('' as char)," + "audi.post_id," + "cast('' as char)," + "cast('' as char)," + "mph.date_of_retirement," + "prh.commencement_date,"
					+ "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "sch.call_date," + "sch.slot_time," + "sch.schedule_status," + "mpd.acount_no,"
					+ "cast('' as char)," + "cast('' as char)," + "mpd.bank_code," + "prh.pension_request_id," + "sch.slot_no," + "emp.emp_fname," + "emp.emp_mname," + "emp.emp_lname,"
					+ "cast('' as char)," + "pfd.name," + "mph.date_of_death";
		} /*
		 * else if
		 * (bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor
		 * )) { columnValues = new String[]{"", "prh.ppo_no", "mph.first_name",
		 * "sch.call_date", "sch.slot_time", "sch.schedule_status",
		 * "mph.pensioner_id", "mpd.pensioner_dtls_id",
		 * "prh.pension_request_id"}; lStrColumnValues = "prh.ppo_no," +
		 * "mph.pensioner_id," + "mpd.pensioner_dtls_id," + "mph.first_name," +
		 * "''," + "''," + "''," + "''," + "''," + "''," + "''," + "''," + "'',"
		 * + "''," + "''," + "''," + "''," + "''," + "''," + "sch.call_date," +
		 * "sch.slot_time," + "sch.schedule_status," + "''," + "''," + "''," +
		 * "''," + "prh.pension_request_id," + "sch.slot_no," + "''," + "''," +
		 * "''"; }
		 */else if (bundleConst.getString("CASEFOR.FIRSTPENSION").equals(lStrShowCaseFor)) {
			columnValues = new String[]{"", "prh.ppo_no", "mph.first_name", "mph.date_of_retirement", "prh.commencement_date", "mb.bank_name", "rlt.branch_name"};
			lStrColumnValues = "prh.ppo_no," + "mph.pensioner_id," + "mpd.pensioner_dtls_id," + "mph.first_name," + "cast('' as char)," + "prh.PPO_INWARD_DATE," + "cast('' as char),"
					+ "mb.bank_name," + "rlt.branch_name," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "mph.date_of_retirement,"
					+ "prh.commencement_date," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char),"
					+ "cast('' as char)," + "mpd.acount_no," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "prh.pension_request_id," + "cast('' as char)," + "cast('' as char),"
					+ "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "pfd.name," + "mph.date_of_death";
		} else if (bundleConst.getString("CASEFOR.RECORDROOM").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.MODIFICATIONCASE").equals(lStrShowCaseFor)) {
			columnValues = new String[]{"", "prh.ppo_no", "mph.first_name", "prh.case_Status", "mb.bank_name", "mpd.branch_code"};
			lStrColumnValues = "prh.ppo_no," + "mph.pensioner_id," + "mpd.pensioner_dtls_id," + "mph.first_name," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "mb.bank_name,"
					+ "rlt.branch_name," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char),"
					+ "cast('' as char)," + "cast('' as char)," + "prh.case_Status," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "mpd.acount_no," + "cast('' as char),"
					+ "cast('' as char)," + "cast('' as char)," + "prh.pension_request_id," + "cast('' as char)," + "emp.emp_fname," + "emp.emp_mname," + "emp.emp_lname," + "cast('' as char),"
					+ "pfd.name," + "mph.date_of_death";
		} else if (bundleConst.getString("CASEFOR.DRAFTCASE").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.VIEWNEWCASE").equals(lStrShowCaseFor)) {
			columnValues = new String[]{"", "prh.ppo_no", "mph.first_name", "prh.commencement_date", "mph.pensioner_id", "mpd.pensioner_dtls_id", "prh.pension_request_id"};
			lStrColumnValues = "prh.ppo_no," + "mph.pensioner_id," + "mpd.pensioner_dtls_id," + "mph.first_name," + "cast('' as char)," + "cast('' as char)," + "cast('' as char),"
					+ "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char),"
					+ "prh.commencement_date," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char),"
					+ "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "cast('' as char)," + "prh.pension_request_id," + "cast('' as char)," + "emp.emp_fname,"
					+ "emp.emp_mname," + "emp.emp_lname," + "cast('' as char)," + "pfd.name," + "mph.date_of_death";
		}
		lSBQuery.append(lStrColumnValues + "\n");
		lSBQuery.append("From \n");
		lSBQuery.append("TRN_PENSION_RQST_HDR prh \n");
		lSBQuery.append("JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code \n");
		lSBQuery.append("JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code \n");
		lSBQuery.append("LEFT OUTER JOIN mst_pensioner_family_dtls pfd ON pfd.pensioner_code = mph.pensioner_code AND pfd.percentage = :lPercent \n");
		if (bundleConst.getString("CASEFOR.RCVONL").equals(lStrShowCaseFor)) {
			lSBQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code=rlt.branch_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code \n");
			lSBQuery.append("LEFT OUTER JOIN org_userpost_rlt usrpst ON usrpst.post_id = audi.post_id \n");
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id \n");
		} else if (bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
			lSBQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code=rlt.branch_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code \n");
			lSBQuery.append("LEFT OUTER JOIN org_userpost_rlt usrpst ON usrpst.post_id = audi.post_id \n");
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id \n");
			lSBQuery.append("LEFT OUTER JOIN trn_pnsnpmnt_schedular sch ON sch.pensioner_Code = mph.pensioner_Code and sch.location_code = :locationCode and sch.schedule_status != :identifiedStatus \n");
		} /*
		 * else if
		 * (bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor
		 * )) {lSBQuery.append(
		 * "JOIN trn_pnsnpmnt_schedular sch ON sch.pensioner_Code = mph.pensioner_Code \n"
		 * ); }
		 */
		else if (bundleConst.getString("CASEFOR.FIRSTPENSION").equals(lStrShowCaseFor)) {
			lSBQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code=rlt.branch_code \n");
		} else if ((bundleConst.getString("CASEFOR.RECORDROOM").equals(lStrShowCaseFor)) || (bundleConst.getString("CASEFOR.MODIFICATIONCASE").equals(lStrShowCaseFor))) {
			lSBQuery.append("JOIN org_userpost_rlt usrpst ON usrpst.post_id = prh.case_owner AND usrpst.activate_flag = 1 \n");
			lSBQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code=rlt.branch_code \n");
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id \n");
		} else if ((bundleConst.getString("CASEFOR.DRAFTCASE").equals(lStrShowCaseFor)) || bundleConst.getString("CASEFOR.VIEWNEWCASE").equals(lStrShowCaseFor)) {
			lSBQuery.append("JOIN org_userpost_rlt usrpst ON usrpst.post_id = prh.case_owner AND usrpst.activate_flag = 1 \n");
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id \n");
		}
		lSBQuery.append("WHERE prh.location_code=:locationCode and prh.case_Status IN (:caseStatusList) \n");

		if (bundleConst.getString("CASEFOR.RCVONL").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor)
				|| bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
			lSBQuery.append("AND audi.bill_type IS NULL \n");
		}
		// --------If role of current user is auditor then putting condition of
		// case_owner-------//
		if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrToRole)) {
			lSBQuery.append("AND prh.case_owner=:caseOwner \n");
		}

		// ----------Search Conditions Starts
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
			if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
				if ("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.ppo_no = :ppoNo \n");
				} else if ("mph.first_name".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND ((mph.first_name like :pnsrName) or (pfd.name like :pnsrName))  \n");
				} else if ("mpd.registration_no".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.registration_no like :regNo \n");
				} else if ("prh.PPO_INWARD_DATE".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.PPO_INWARD_DATE = :inwdDate \n");
				} else if ("prh.PPO_REG_DATE".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.PPO_REG_DATE = :ppoRegDate \n");
				} else if ("mpd.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.bank_code = :bankCode \n");
				} else if ("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.branch_code = :branchCode \n");
				} else if ("audi.post_id".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND audi.post_id = :audiPostId \n");
				} else if ("prh.type_flag".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.type_flag = :caseType \n");
				} else if ("prh.inward_mode".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.inward_mode = :inwdMode \n");
				} else if ("mph.date_of_retirement".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mph.date_of_retirement = :pnsrDor \n");
				} else if ("prh.commencement_date".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.commencement_date = :pnsnStartDt \n");
				} else if ("mpd.acount_no".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.acount_no = :acNo \n");
				} else if ("sch.call_date".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND sch.call_date = :callDate \n");
				} else if ("sch.schedule_status".equalsIgnoreCase(lStrSearchCrt)) {
					if ((bundleConst.getString("IDENT.NOTSCHEDULED")).equals(lStrSearchVal)) {
						lSBQuery.append("AND sch.schedule_status is :identStatus \n");
					} else {
						lSBQuery.append("AND sch.schedule_status = :identStatus \n");
					}
				} else if ("prh.case_Status".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.case_Status = :caseStatus \n");
				} else if ("prh.case_owner".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.case_owner = :searchCaseOwner \n");
				}
			}
		}

		// ----------Search Conditions Ends
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		lSBQuery.append("Order By \n");
		String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");
		Integer orderbypara = null;

		if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
			orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
			lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
		} else {
			if ((bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor))) {
				lSBQuery.append("sch.call_date,sch.slot_no ");
			} else {
				lSBQuery.append("prh.PPO_INWARD_DATE ");
			}
		}

		try {

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameterList("caseStatusList", caseStatusList);
			lQuery.setParameter("locationCode", locationCode);
			lQuery.setLong("lPercent", 100);

			if (bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
				lQuery.setParameter("identifiedStatus", bundleConst.getString("IDENT.IDENTIFIED"));
			}

			/*
			 * If role of current user is auditor then putting condition of
			 * case_owner
			 */
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrToRole)) {
				lQuery.setParameter("caseOwner", lLngPostId);
			}

			/*
			 * Parameter for search starts <<<<<<<<<
			 */
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if ("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("ppoNo", lStrSearchVal);
					} else if ("mph.first_name".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("pnsrName", "%" + lStrSearchVal + "%");
					} else if ("mpd.registration_no".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("regNo", "%" + lStrSearchVal + "%");
					} else if ("prh.PPO_INWARD_DATE".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("inwdDate", lSdf.parse(lStrSearchVal));
					} else if ("prh.PPO_REG_DATE".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("ppoRegDate", lSdf.parse(lStrSearchVal));
					} else if ("mpd.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("bankCode", Long.valueOf(lStrSearchVal));
					} else if ("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("branchCode", Long.valueOf(lStrSearchVal));
					} else if ("audi.post_id".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("audiPostId", Long.valueOf(lStrSearchVal));
					} else if ("prh.type_flag".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("caseType", lStrSearchVal);
					} else if ("prh.inward_mode".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("inwdMode", lStrSearchVal);
					} else if ("mph.date_of_retirement".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("pnsrDor", lSdf.parse(lStrSearchVal));
					} else if ("prh.commencement_date".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("pnsnStartDt", lSdf.parse(lStrSearchVal));
					} else if ("mpd.acount_no".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("acNo", lStrSearchVal);
					} else if ("sch.call_date".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("callDate", lSdf.parse(lStrSearchVal));
					} else if ("sch.schedule_status".equalsIgnoreCase(lStrSearchCrt)) {
						if ((bundleConst.getString("IDENT.NOTSCHEDULED")).equals(lStrSearchVal)) {
							lQuery.setString("identStatus", null);
						} else {
							lQuery.setString("identStatus", lStrSearchVal);
						}
					} else if ("prh.case_Status".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("caseStatus", lStrSearchVal);
					} else if ("prh.case_owner".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("searchCaseOwner", Long.valueOf(lStrSearchVal));
					}
				}
			}
			/*
			 * Parameter for search ends >>>>>>>>>>>
			 */
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);
			lLstPensioners = lQuery.list();
			if (lLstPensioners != null && lLstPensioners.size() > 0) {
				Iterator it = lLstPensioners.iterator();
				Object[] tuple = null;
				Object[] tmpTuple = null;
				SavedCasesVO savedCasesVo = null;
				// ---For db2 result array has one more element of row id.So
				// removing that extra element and changing index of array
				// accordingly for db2.
				int actualLength = (lStrColumnValues != null) ? lStrColumnValues.split(",").length : 0;
				int resultLength = ((Object[]) lLstPensioners.get(0)).length;
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

					savedCasesVo = new SavedCasesVO();
					savedCasesVo.setPpoNo((tuple[0] != null && !tuple[0].equals("")) ? tuple[0].toString() : "");
					savedCasesVo.setPensionerId((tuple[1] != null && !tuple[1].equals("")) ? Long.valueOf(tuple[1].toString()) : null);
					savedCasesVo.setPensionerDtlsId((tuple[2] != null && !tuple[2].equals("")) ? Long.valueOf(tuple[2].toString()) : null);
					savedCasesVo.setPnsrName((tuple[3] != null) ? tuple[3].toString() : "");
					savedCasesVo.setRegNo((tuple[4] != null && !tuple[4].equals("")) ? tuple[4].toString() : "");
					savedCasesVo.setPpoInwardDate((tuple[5] != null && tuple[5].toString().trim().length() > 0) ? (Date) tuple[5] : null);
					savedCasesVo.setBranchCode((tuple[6] != null && tuple[6].toString().trim().length() > 0) ? Long.parseLong(tuple[6].toString()) : null);
					savedCasesVo.setBankName((tuple[7] != null && tuple[7].toString().trim().length() > 0) ? tuple[7].toString() : "");
					savedCasesVo.setBranchName((tuple[8] != null && tuple[8].toString().trim().length() > 0) ? tuple[8].toString() : "");
					savedCasesVo.setAuditorName((tuple[9] != null && tuple[9].toString().trim().length() > 0) ? tuple[9].toString() : "");
					savedCasesVo.setAuditorPostId((tuple[10] != null && tuple[10].toString().trim().length() > 0) ? Long.parseLong(tuple[10].toString()) : null);
					savedCasesVo.setCaseType((tuple[11] != null && tuple[11].toString().trim().length() > 0) ? tuple[11].toString() : "");
					savedCasesVo.setInwardMode((tuple[12] != null && tuple[12].toString().trim().length() > 0) ? tuple[12].toString() : "");
					savedCasesVo.setRetirementDate((tuple[13] != null && tuple[13].toString().trim().length() > 0) ? (Date) tuple[13] : null);
					savedCasesVo.setCommencementDate((tuple[14] != null && tuple[14].toString().trim().length() > 0) ? (Date) tuple[14] : null);
					savedCasesVo.setPnsnAmt((tuple[15] != null && tuple[15].toString().trim().length() > 0) ? new BigDecimal(tuple[15].toString()) : BigDecimal.ZERO);
					savedCasesVo.setGratuityAmt((tuple[16] != null && tuple[16].toString().trim().length() > 0) ? new BigDecimal(tuple[16].toString()) : BigDecimal.ZERO);
					savedCasesVo.setCvpAmt((tuple[17] != null && tuple[17].toString().trim().length() > 0) ? new BigDecimal(tuple[17].toString()) : BigDecimal.ZERO);
					savedCasesVo.setCaseStatus((tuple[18] != null && tuple[18].toString().trim().length() > 0) ? tuple[18].toString() : "");
					savedCasesVo.setCallDate((tuple[19] != null && tuple[19].toString().trim().length() > 0) ? (Date) tuple[19] : null);
					savedCasesVo.setCallTime((tuple[20] != null && tuple[20].toString().trim().length() > 0) ? tuple[20].toString() : "");
					savedCasesVo.setIdentStatus((tuple[21] != null && tuple[21].toString().trim().length() > 0) ? tuple[21].toString() : "");
					savedCasesVo.setAccountNo((tuple[22] != null && tuple[22].toString().trim().length() > 0) ? tuple[22].toString() : "");
					savedCasesVo.setOldTreasuryName((tuple[23] != null && tuple[23].toString().trim().length() > 0) ? tuple[23].toString() : "");
					savedCasesVo.setRemarks((tuple[24] != null && tuple[24].toString().trim().length() > 0) ? tuple[24].toString() : "");
					savedCasesVo.setBankCode((tuple[25] != null && tuple[25].toString().trim().length() > 0) ? Long.valueOf(tuple[25].toString()) : null);
					savedCasesVo.setPensionRequestId((tuple[26] != null && tuple[26].toString().trim().length() > 0) ? Long.valueOf(tuple[26].toString()) : null);
					savedCasesVo.setSlotNo((tuple[27] != null && tuple[27].toString().trim().length() > 0) ? Long.valueOf(tuple[27].toString()) : null);
					savedCasesVo.setAuditorFname((tuple[28] != null && tuple[28].toString().trim().length() > 0) ? tuple[28].toString() : "");
					savedCasesVo.setAuditorMname((tuple[29] != null && tuple[29].toString().trim().length() > 0) ? tuple[29].toString() : "");
					savedCasesVo.setAuditorLname((tuple[30] != null && tuple[30].toString().trim().length() > 0) ? tuple[30].toString() : "");
					savedCasesVo.setPpoRegDate((tuple[31] != null && tuple[31].toString().trim().length() > 0) ? (Date) tuple[31] : null);
					savedCasesVo.setFamilyMemName((tuple[32] != null) ? tuple[32].toString() : "");
					savedCasesVo.setDeathDate((tuple[33] != null && tuple[33].toString().trim().length() > 0) ? (Date) tuple[33] : null);
					lLstSavedCaseVO.add(savedCasesVo);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			//e.printStackTrace();
			throw (e);
		}

		return lLstSavedCaseVO;
	}

	public int getPensionerDetailsCount(String lStrShowCaseFor, Map inputMap, Long lLngPostId, String lStrToRole, List<String> caseStatusList, String locationCode, String lStrSearchCrt,
			String lStrSearchVal) throws Exception {

		List lLstPensioners = new ArrayList();
		new ArrayList<SavedCasesVO>();
		StringBuilder lSBQuery = new StringBuilder();
		int lIntCount = 0;
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		// "prh.commencement_date"
		lSBQuery.append("Select count(mpd.pensioner_dtls_id) \n");
		lSBQuery.append("From \n");
		lSBQuery.append("TRN_PENSION_RQST_HDR prh \n");
		lSBQuery.append("JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code \n");
		lSBQuery.append("JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code \n");
		lSBQuery.append("LEFT OUTER JOIN mst_pensioner_family_dtls pfd ON pfd.pensioner_code = mph.pensioner_code AND pfd.percentage = :lPercent \n");
		if (bundleConst.getString("CASEFOR.RCVONL").equals(lStrShowCaseFor)) {
			lSBQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code=rlt.branch_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code \n");
			lSBQuery.append("LEFT OUTER JOIN org_userpost_rlt usrpst ON usrpst.post_id = audi.post_id and usrpst.activate_flag = 1 \n");
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id \n");
		} else if (bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
			lSBQuery.append("LEFT OUTER JOIN MST_BANK mb ON mb.bank_code=mpd.bank_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code=rlt.branch_code \n");
			lSBQuery.append("LEFT OUTER JOIN rlt_auditor_bank audi ON mpd.branch_code=audi.branch_code AND audi.location_code = prh.location_code \n");
			lSBQuery.append("LEFT OUTER JOIN org_userpost_rlt usrpst ON usrpst.post_id = audi.post_id \n");
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id \n");
			lSBQuery.append("LEFT OUTER JOIN trn_pnsnpmnt_schedular sch ON sch.pensioner_Code = mph.pensioner_Code and sch.location_code = :locationCode and sch.schedule_status != :identifiedStatus \n");
		} /*
		 * else if
		 * (bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor
		 * )) {lSBQuery.append(
		 * "JOIN trn_pnsnpmnt_schedular sch ON sch.pensioner_Code = mph.pensioner_Code \n"
		 * ); }
		 */else if (bundleConst.getString("CASEFOR.RECORDROOM").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.MODIFICATIONCASE").equals(lStrShowCaseFor)
				|| bundleConst.getString("CASEFOR.DRAFTCASE").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.VIEWNEWCASE").equals(lStrShowCaseFor)) {
			lSBQuery.append("JOIN org_userpost_rlt usrpst ON usrpst.post_id = prh.case_owner AND usrpst.activate_flag = 1  \n");
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id \n");
		}
		lSBQuery.append("WHERE prh.location_code=:locationCode and prh.case_Status IN (:caseStatusList) \n");
		if (bundleConst.getString("CASEFOR.RCVONL").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor)
				|| bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
			lSBQuery.append("AND audi.bill_type IS NULL \n");
		}
		// --------If role of current user is auditor then putting condition of
		// case_owner-------//
		if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrToRole)) {
			lSBQuery.append("AND prh.case_owner=:caseOwner \n");
		}
		// ----------Search Conditions Starts
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
			if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
				if ("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.ppo_no = :ppoNo \n");
				} else if ("mph.first_name".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND ((mph.first_name like :pnsrName) or (pfd.name like :pnsrName)) \n");
				} else if ("mpd.registration_no".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.registration_no like :regNo \n");
				} else if ("prh.PPO_INWARD_DATE".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.PPO_INWARD_DATE = :inwdDate \n");
				} else if ("prh.PPO_REG_DATE".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.PPO_REG_DATE = :ppoRegDate \n");
				} else if ("mpd.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.bank_code = :bankCode \n");
				} else if ("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.branch_code = :branchCode \n");
				} else if ("audi.post_id".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND audi.post_id = :audiPostId \n");
				} else if ("prh.type_flag".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.type_flag = :caseType \n");
				} else if ("prh.inward_mode".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.inward_mode = :inwdMode \n");
				} else if ("mph.date_of_retirement".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mph.date_of_retirement = :pnsrDor \n");
				} else if ("prh.commencement_date".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.commencement_date = :pnsnStartDt \n");
				} else if ("mpd.acount_no".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND mpd.acount_no = :acNo \n");
				} else if ("sch.call_date".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND sch.call_date = :callDate \n");
				} else if ("sch.schedule_status".equalsIgnoreCase(lStrSearchCrt)) {
					if ((bundleConst.getString("IDENT.NOTSCHEDULED")).equals(lStrSearchVal)) {
						lSBQuery.append("AND sch.schedule_status is :identStatus \n");
					} else {
						lSBQuery.append("AND sch.schedule_status = :identStatus \n");
					}
				} else if ("prh.case_Status".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.case_Status = :caseStatus \n");
				} else if ("prh.case_owner".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.case_owner = :searchCaseOwner \n");
				} else if ("prh.case_owner".equalsIgnoreCase(lStrSearchCrt)) {
					lSBQuery.append("AND prh.case_owner = :searchCaseOwner \n");
				}
			}
		}

		// ----------Search Conditions Ends
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		try {
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameterList("caseStatusList", caseStatusList);
			lQuery.setParameter("locationCode", locationCode);
			lQuery.setLong("lPercent", 100);

			if (bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
				lQuery.setParameter("identifiedStatus", bundleConst.getString("IDENT.IDENTIFIED"));
			}

			/*
			 * If role of current user is auditor then putting condition of
			 * case_owner
			 */
			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrToRole)) {
				lQuery.setParameter("caseOwner", lLngPostId);
			}
			/*
			 * Parameter for search starts <<<<<<<<<
			 */
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if (lStrSearchVal != null && !lStrSearchVal.equals("") && !lStrSearchVal.equals("-1")) {
					if ("prh.ppo_no".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("ppoNo", lStrSearchVal);
					} else if ("mph.first_name".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("pnsrName", "%" + lStrSearchVal + "%");
					} else if ("mpd.registration_no".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("regNo", "%" + lStrSearchVal + "%");
					} else if ("prh.PPO_INWARD_DATE".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("inwdDate", lSdf.parse(lStrSearchVal));
					} else if ("prh.PPO_REG_DATE".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("ppoRegDate", lSdf.parse(lStrSearchVal));
					} else if ("mpd.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("bankCode", Long.valueOf(lStrSearchVal));
					} else if ("mpd.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("branchCode", Long.valueOf(lStrSearchVal));
					} else if ("audi.post_id".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("audiPostId", Long.valueOf(lStrSearchVal));
					} else if ("prh.type_flag".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("caseType", lStrSearchVal);
					} else if ("prh.inward_mode".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("inwdMode", lStrSearchVal);
					} else if ("mph.date_of_retirement".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("pnsrDor", lSdf.parse(lStrSearchVal));
					} else if ("prh.commencement_date".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("pnsnStartDt", lSdf.parse(lStrSearchVal));
					} else if ("mpd.acount_no".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("acNo", lStrSearchVal);
					} else if ("sch.call_date".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setDate("callDate", lSdf.parse(lStrSearchVal));
					} else if ("sch.schedule_status".equalsIgnoreCase(lStrSearchCrt)) {
						if ((bundleConst.getString("IDENT.NOTSCHEDULED")).equals(lStrSearchVal)) {
							lQuery.setString("identStatus", null);
						} else {
							lQuery.setString("identStatus", lStrSearchVal);
						}
					} else if ("prh.case_Status".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setString("caseStatus", lStrSearchVal);
					} else if ("prh.case_owner".equalsIgnoreCase(lStrSearchCrt)) {
						lQuery.setLong("searchCaseOwner", Long.valueOf(lStrSearchVal));
					}
				}
			}
			/*
			 * Parameter for search ends >>>>>>>>>>>
			 */
			lLstPensioners = lQuery.list();
			lIntCount = Integer.parseInt((lLstPensioners.get(0).toString()));
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			//e.printStackTrace();
			throw (e);
		}
		return lIntCount;
	}

	/*
	 * public List<StgPensioner> getPensionerDetails(Map inputMap, String
	 * lStrPostId, String lStrToRole, List<String> caseStatusList, String
	 * locationCode) throws Exception {
	 * 
	 * List<StgPensioner> listPensioners = new ArrayList<StgPensioner>();
	 * StringBuilder lSBQuery = new StringBuilder();
	 * 
	 * /lSBQuery.append(
	 * "SELECT prh.ppoNo,mph.firstName,mph.dateOfRetirement,mb.bankName,mpd.registrationNo,mpd.pensionerDtlsId,"
	 * + "mpd.bankCode,mpd.branchCode,mph.pensionerId," +
	 * "rlt.branchName,prh.commensionDate,usr.userName,audi.postId,mpd.acountNo,sch.callDate,sch.slotTime,"
	 * + "sch.slotNo,sch.scheduleStatus \n");
	 * 
	 * 
	 * lSBQuery.append("FROM TrnPensionRqstHdr prh \n");lSBQuery.append(
	 * "JOIN MstPensionerHdr mph ON prh.pensionerCode=mph.pensionerCode \n");
	 * lSBQuery
	 * .append("JOIN MstPensionerDtls mpd ON mph.pensionerCode=mpd.pensionerCode \n"
	 * ); lSBQuery.append("JOIN MstBank mb ON mb.bankCode=mpd.bankCode \n");
	 * lSBQuery
	 * .append("JOIN RltBankBranch rlt ON mpd.branchCode=rlt.branchCode \n");
	 * lSBQuery
	 * .append("JOIN RltAuditorBank audi ON mpd.branchCode=audi.branchCode \n");
	 * lSBQuery.append(
	 * "JOIN OrgUserPostRlt usrpst ON usrpst.orgPostMstByPostId.postId = audi.postId \n"
	 * );lSBQuery.append(
	 * "JOIN OrgUserMst usr ON usr.userId = usrpst.orgUserMst.userId \n");
	 * lSBQuery.append(
	 * "LEFT OUTER JOIN TrnPnsnpmntSchedular sch on sch.pensionerCode = mph.pensionerCode \n"
	 * ); if (!lStrToRole.equals(bundleConst.getString("PPMT.DISPATCHERROLE")))
	 * {
	 * lSBQuery.append("JOIN WfJobMst wjm ON mph.pensionerId=wjm.jobRefId \n");
	 * }lSBQuery.append(
	 * "WHERE prh.locationCode=:locationCode AND prh.caseStatus IN (:caseStatusList)\n"
	 * ); if (!lStrToRole.equals(bundleConst.getString("PPMT.DISPATCHERROLE")))
	 * { lSBQuery.append("AND wjm.lstActPostId=:lStrPostId \n"); }
	 * lSBQuery.append("AND usrpst.activateFlag = :activateFlag \n");
	 * lSBQuery.append("GROUP BY prh.ppoNo \n");
	 * 
	 * try {
	 * 
	 * Query lQuery = ghibSession.createQuery(lSBQuery.toString());
	 * lQuery.setParameterList("caseStatusList", caseStatusList);
	 * lQuery.setParameter("locationCode", locationCode);
	 * lQuery.setParameter("activateFlag", Long.valueOf(1)); if
	 * (!lStrToRole.equals(bundleConst.getString("PPMT.DISPATCHERROLE"))) {
	 * lQuery.setParameter("lStrPostId", lStrPostId); } listPensioners =
	 * lQuery.list();
	 * 
	 * } catch (Exception e) { logger.error(" Error is : " + e, e);
	 * e.printStackTrace(); throw (e); }
	 * 
	 * return listPensioners; }
	 */

	public List<ComboValuesVO> getBankNames(Map inputMap, Long langId) throws Exception {

		List<MstBank> listBanks = new ArrayList();

		List<ComboValuesVO> lLstBanks = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Iterator itr;
		Object[] obj;

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("select mb.bankCode, mb.bankName ");
		lSBQuery.append("from MstBank mb where ");
		lSBQuery.append("mb.langId=:langId and mb.activateFlag=:activeFlag ");
		lSBQuery.append("order by mb.bankName");
		try {

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("langId", langId);
			lQuery.setParameter("activeFlag", Long.valueOf(1));

			listBanks = lQuery.list();

			if (listBanks != null && listBanks.size() > 0) {
				itr = listBanks.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstBanks.add(cmbVO);
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			//e.printStackTrace();
			throw (e);
		}

		return lLstBanks;
	}

	public String getLocationShortNameFromCode(String locationCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstLocShrtNames = new ArrayList();
		String lStrLocationName = null;
		lSBQuery.append("SELECT clm.locShortName \n");
		lSBQuery.append("FROM CmnLocationMst clm \n");
		lSBQuery.append("WHERE clm.locationCode = :locationCode");

		try {
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locationCode", locationCode);
			lLstLocShrtNames = lQuery.list();

			Iterator lObjIterator = lLstLocShrtNames.iterator();
			while (lObjIterator.hasNext()) {
				lStrLocationName = lObjIterator.next().toString();
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
		return lStrLocationName;

	}

	public List getBranchsOfBank(String bankCode, Long langId, String lStrLocCode) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		List resultList = new ArrayList();
		try {
			strQuery.append(" SELECT branchCode,branchName  FROM RltBankBranch  WHERE bankCode =:bankCode AND langId =:langId AND locationCode= :locationCode" + " order by branchName");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());

			hqlQuery.setString("bankCode", bankCode);
			hqlQuery.setLong("langId", langId);
			hqlQuery.setString("locationCode", lStrLocCode);
			resultList = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return resultList;
	}

	public List getAuditorName(Map inputMap, String locationCode, String branchCode) throws Exception {

		List lLstAuditors = new ArrayList();

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("SELECT audi.post_id,concat(concat(concat(concat(emp.emp_fname,' '),emp.emp_mname),' '),emp.emp_lname),audi.post_id \n");
		lSBQuery.append("FROM rlt_auditor_bank audi \n");
		lSBQuery.append("JOIN org_userpost_rlt usrpst ON usrpst.post_id = audi.post_id \n");
		lSBQuery.append("JOIN org_user_mst usr ON usr.user_id = usrpst.user_id \n");
		lSBQuery.append("JOIN org_emp_mst emp ON usr.user_id=emp.user_id \n");
		lSBQuery.append("WHERE audi.branch_code = :branchCode \n");
		lSBQuery.append("AND audi.location_code = :locationCode \n");
		lSBQuery.append("AND audi.bill_type IS NULL \n");
		lSBQuery.append("AND usrpst.activate_flag = :activateFlag \n");
		lSBQuery.append("AND usr.activate_flag = :activateFlag \n");
		lSBQuery.append("AND emp.activate_flag = :activateFlag \n");
		// lSBQuery.append("GROUP BY audi.branch_code ");

		try {

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("branchCode", branchCode);
			lQuery.setParameter("activateFlag", Long.valueOf(1));
			lQuery.setParameter("locationCode", locationCode);

			lLstAuditors = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			//e.printStackTrace();
			throw (e);
		}

		return lLstAuditors;
	}

	public List getBnkBrnchAudiNmeFrmBnkCode(Map inputMap, String locationCode, Long branchCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		List resultList = new ArrayList();
		try {

			lSBQuery.append("SELECT rlt.branch_name,mst.bank_code,mst.bank_name,concat(concat(concat(concat(emp.emp_fname,' '),emp.emp_mname),' '),emp.emp_lname),audi.post_id \n");
			lSBQuery.append("FROM rlt_bank_branch rlt \n");
			lSBQuery.append("JOIN mst_bank mst ON mst.bank_code=rlt.bank_code \n");
			lSBQuery.append("JOIN rlt_auditor_bank audi ON audi.branch_code=rlt.branch_code \n");
			lSBQuery.append("JOIN org_userpost_rlt usrpst ON usrpst.post_id = audi.post_id \n");
			lSBQuery.append("JOIN org_user_mst usr ON usr.user_id = usrpst.user_id \n");
			lSBQuery.append("JOIN org_emp_mst emp ON usr.user_id=emp.user_id \n");
			lSBQuery.append("WHERE rlt.branch_code = :branchCode \n");
			lSBQuery.append("AND rlt.location_code = audi.location_code \n");
			lSBQuery.append("AND audi.location_code = :locationCode \n");
			lSBQuery.append("AND audi.bill_type IS NULL \n");
			lSBQuery.append("AND usrpst.activate_flag = :activateFlag \n");
			lSBQuery.append("AND mst.activate_flag = :activateFlag \n");
			lSBQuery.append("AND usr.activate_flag = :activateFlag \n");
			lSBQuery.append("AND emp.activate_flag = :activateFlag \n");
			// lSBQuery.append("GROUP BY audi.branch_code");
			Query sqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			sqlQuery.setParameter("branchCode", branchCode);
			sqlQuery.setParameter("activateFlag", Long.valueOf(1));
			sqlQuery.setParameter("locationCode", locationCode);

			resultList = sqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return resultList;

	}

	public String getRoleByPost(Long lLngPostId) throws Exception {

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
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		gLogger.info("getRoleByPost in Role Ids : " + lStrToRole);
		gLogger.info("getRoleByPost end...................");

		return lStrToRole;
	}

	private String arrToStr(String[] lArrColVal) {

		String lStrColumns = "";
		if (lArrColVal != null && lArrColVal.length > 0) {
			for (String lStrColVal : lArrColVal) {
				if (arrayContains(lArrColVal, "prh.ppo_no")) {
					lStrColumns += "prh.ppo_no";
				} else if (arrayContains(lArrColVal, "mph.first_name")) {
					lStrColumns += "mph.first_name";
				} else if (arrayContains(lArrColVal, "mpd.registration_no")) {
					lStrColumns += "mpd.registration_no";
				} else if (arrayContains(lArrColVal, "prh.PPO_INWARD_DATE")) {
					lStrColumns += "prh.PPO_INWARD_DATE";
				} else if (arrayContains(lArrColVal, "mpd.branch_code")) {
					lStrColumns += "mpd.branch_code";
				} else if (arrayContains(lArrColVal, "mb.bank_name")) {
					lStrColumns += "mb.bank_name";
				} else if (arrayContains(lArrColVal, "rlt.branch_name")) {
					lStrColumns += "rlt.branch_name";
				}
			}
			return lStrColumns.toString();
		} else {
			return null;
		}
	}

	private boolean arrayContains(String[] lArrColVal, String colName) {

		for (int cnt = 0; cnt < lArrColVal.length; cnt++) {
			if (colName.equals(lArrColVal[cnt])) {
				return true;
			}
		}
		return false;
	}

	public List getAuditorsListFromRoleID(String locationCode, Long langId) throws Exception {

		List lLstAuditors = null;
		ComboValuesVO cmbVO;
		Iterator itr;
		Object[] obj;
		List<ComboValuesVO> lLstComboAuditor = new ArrayList<ComboValuesVO>();
		Session hibSession = getSession();
		StringBuffer lSBQuery = new StringBuffer();
		lSBQuery.append("SELECT concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname),oup.orgPostMstByPostId.postId \n");
		lSBQuery.append("FROM AclPostroleRlt apr,OrgUserpostRlt oup,OrgEmpMst oem, OrgPostMst opm \n");
		lSBQuery.append("Where oup.orgPostMstByPostId.postId=apr.orgPostMst.postId \n");
		lSBQuery.append("AND oup.orgUserMst.userId = oem.orgUserMst.userId \n");
		lSBQuery.append("AND oup.orgPostMstByPostId.postId=opm.postId\n");
		lSBQuery.append("AND apr.aclRoleMst.roleId=:roleId \n");
		lSBQuery.append("AND opm.locationCode=:locationCode \n");
		lSBQuery.append("AND oup.activateFlag = :activeFlag \n");
		lSBQuery.append("and oem.cmnLanguageMst.langId = :langId \n");

		Query lHibQry = hibSession.createQuery(lSBQuery.toString());
		lHibQry.setParameter("locationCode", locationCode);
		lHibQry.setParameter("roleId", Long.valueOf(bundleConst.getString("PPMT.AUDITORROLE")));
		lHibQry.setParameter("activeFlag", 1L);
		lHibQry.setLong("langId", langId);
		lLstAuditors = lHibQry.list();
		if (lLstAuditors != null && lLstAuditors.size() > 0) {
			itr = lLstAuditors.iterator();
			while (itr.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) itr.next();
				cmbVO.setId(obj[1].toString());
				cmbVO.setDesc(obj[0].toString());
				lLstComboAuditor.add(cmbVO);
			}
		}
		return lLstComboAuditor;

	}
	
	public List validatePPOBeforeArchive(String lStrPpoNo, String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		Query lHibQry = null;
		
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select mph.pensionerCode, tpr.ppoNo, mph.firstName from TrnPensionRqstHdr tpr, MstPensionerHdr mph \n");
			lSBQuery.append(" where tpr.pensionerCode = mph.pensionerCode and tpr.ppoNo = :ppoNo and tpr.caseStatus = :status and tpr.locationCode = :locationCode \n");
			
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setString("ppoNo", lStrPpoNo);
			lHibQry.setString("status",bundleConst.getString("STATFLG.ARCHIVED"));
			lHibQry.setString("locationCode", lStrLocCode);
			lLstResult = lHibQry.list();
							
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
		}
		return lLstResult;
	}
	
	public List validatePPOBeforeArchiveWithBill(String lStrPensionerCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		Query lHibQry = null;
		
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select * from TrnPensionBillDtls where pensionerCode = :pensionerCode \n");
					
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setString("pensionerCode", lStrPensionerCode);
			
			lLstResult = lHibQry.list();
				
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
		}
		return lLstResult;
	}
	
	public void updateCaseStatusAndSendBackToClerk(String lStrPensionerCode,String lStrLocCode,BigDecimal lBgDcmlPostId,BigDecimal lBgDcmlUserId,Date lDtCurrDate) throws Exception {
		// TODO Auto-generated method stub
		
		Query lHibQry = null;
		
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("update TrnPensionRqstHdr set caseStatus = :status, updatedUserId = :updatedUserId, updatedPostId = :updatedPostId, \n");
			lSBQuery.append(" updatedDate = :updatedDate where pensionerCode = :pensionerCode and locationCode = :locationCode \n");
					
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setString("pensionerCode", lStrPensionerCode);
			lHibQry.setString("locationCode", lStrLocCode);
			lHibQry.setString("status", bundleConst.getString("STATFLG.REGISTERED"));
			lHibQry.setBigDecimal("updatedUserId", lBgDcmlUserId);
			lHibQry.setBigDecimal("updatedPostId", lBgDcmlPostId);
			lHibQry.setDate("updatedDate", lDtCurrDate);
			
			lHibQry.executeUpdate();			
				
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
		}
	}
}
