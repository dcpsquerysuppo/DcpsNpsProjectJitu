/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 16, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.constants.PensionConstants;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionTransferDtls;
import com.tcs.sgv.web.jsp.tags.DateUtilities;
import java.util.GregorianCalendar;

/**
 * Class Description -
 * 
 * 
 * @author 365450
 * @version 0.1
 * @since JDK 5.0 Aug 16, 2011
 */
public class PensionpayQueryDAOImpl extends GenericDaoHibernateImpl implements PensionpayQueryDAO {

	SessionFactory gObjSessionFactory = null;

	Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

	private static final Logger gLogger = Logger.getLogger("PensionpayReports");

	/** Global Variable for Resource Bundle */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private ResourceBundle gObjRsrcBndleCaseLables = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels");

	/** Global Variable for Simple Date Format */
	private SimpleDateFormat gObjDtFormat = new SimpleDateFormat("dd/MM/yyyy");

	private static ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	public PensionpayQueryDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		gObjSessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}

	public List<TrnPensionBillDtls> getPensionAllocationDetails(Integer lIntForMonth, String lStrBranchCode, String lStrHeadCode) throws Exception {

		List<TrnPensionBillDtls> lLstResult = null;
		StringBuffer lSBQuery = null;

		try {
			lSBQuery = new StringBuffer("select pbd \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnPensionBillHdr pbh, \n");
			lSBQuery.append("TrnPensionBillDtls pbd \n");
			lSBQuery.append("where \n");
			lSBQuery.append("pbh.trnPensionBillHdrId = pbd.trnPensionBillHdrId \n");
			lSBQuery.append("and pbh.forMonth = :lIntPayForMonth \n");
			lSBQuery.append("and pbh.branchCode = :lStrBranchCode \n");
			if (!"".equals(lStrHeadCode) && !"-1".equals(lStrHeadCode)) {
				lSBQuery.append("and pbh.headCode = :lBIHeadCode \n");
			}
			lSBQuery.append("order by pbd.ledgerNo,pbd.pageNo \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setInteger("lIntPayForMonth", lIntForMonth);
			lQuery.setString("lStrBranchCode", lStrBranchCode);
			if (!"".equals(lStrHeadCode) && !"-1".equals(lStrHeadCode)) {
				lQuery.setBigInteger("lBIHeadCode", BigInteger.valueOf(Long.valueOf(lStrHeadCode)));
			}
			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getPensionCaseTrackingReport
	 * (com.tcs.sgv.common.valuebeans.reports.ReportVO, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */

	public List getPensionCaseTrackingReport(ReportVO lObjReport, String lStrLocationCode, String lStrFromDate, String lStrToDate, String lStrTreasuryName, String lStrBankName, String lStrBranchName,
			String lStrPensionerName, String lStrAccountNumber, String lStrPpoNo) {

		CommonDAO lObjCommonDAO = new CommonDAOImpl(gObjSessionFactory);
		TrnPensionTransferDtls lObjTrnPensionTransferDtls = new TrnPensionTransferDtls();
		ArrayList lArrListOuter = new ArrayList();
		String lStrRoleId = null;
		String lStrEmpName = null;
		String lStrArrPensionType[] = new String[14];
		Boolean lBlFlag = false;
		String lStrCaseStatus = null;
		Long lLngPostId = null;

		try {

			lStrArrPensionType[0] = "ABSORPTION";
			lStrArrPensionType[1] = "COMPASSIONATE";
			lStrArrPensionType[2] = "COMPENSATION";
			lStrArrPensionType[3] = "COMPULSORY";
			lStrArrPensionType[4] = "EXTRAORDINARY";
			lStrArrPensionType[5] = "FAMILYPNSN";
			lStrArrPensionType[6] = "INVALID";
			lStrArrPensionType[7] = "RETIRING105";
			lStrArrPensionType[8] = "RETIRING104";
			lStrArrPensionType[9] = "SUPERANNU";
			lStrArrPensionType[10] = "VOLUNTARY64";
			lStrArrPensionType[11] = "VOLUNTARY65";
			lStrArrPensionType[12] = "INJURY";
			lStrArrPensionType[13] = "GALLANTRY";

			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT req.ppoInwardDate,req.ppoNo,hdr.firstName,hdr.dateOfRetirement,req.commensionDate,req.pensionType,req.caseStatus,");
			lSBQuery.append(" dtls.pensionerCode,req.caseOwner,hdr.locationCode,clm.locName FROM TrnPensionRqstHdr req,MstPensionerDtls dtls, ");
			lSBQuery.append(" MstPensionerHdr hdr,CmnLocationMst clm WHERE req.pensionerCode = dtls.pensionerCode ");
			lSBQuery.append(" AND req.pensionerCode = hdr.pensionerCode AND dtls.pensionerCode = hdr.pensionerCode ");
			lSBQuery.append(" AND clm.locId = hdr.locationCode ");
			if (lStrFromDate != null && !lStrFromDate.equals("")) {
				lSBQuery.append(" and req.ppoInwardDate >=:fromDate");
			}
			if (lStrToDate != null && !lStrToDate.equals("")) {
				lSBQuery.append(" and req.ppoInwardDate <=:toDate");
			}
			if (lStrTreasuryName != null && !lStrTreasuryName.equals("-1")) {
				lSBQuery.append(" and hdr.locationCode = :locCode");
			}
			if (lStrBankName != null && !lStrBankName.equals("-1")) {
				lSBQuery.append(" and dtls.bankCode = :bankCode");
			}
			if (lStrBranchName != null && !lStrBranchName.equals("-1")) {
				lSBQuery.append(" and dtls.branchCode = :branchCode");
			}
			if (lStrPensionerName != null && !lStrPensionerName.equals("")) {
				lSBQuery.append(" and hdr.firstName like :pensionerName");
			}
			if (lStrAccountNumber != null && !lStrAccountNumber.equals("")) {
				lSBQuery.append(" and dtls.accountNo = :accountNumber");
			}
			if (lStrPpoNo != null && lStrPpoNo.length() > 0) {
				lSBQuery.append(" and req.ppoNo like '" + lStrPpoNo.trim() + "' ");
			}

			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());

			if (lStrFromDate != null && !lStrToDate.equals("")) {
				lObjQuery.setDate("fromDate", gObjDtFormat.parse(lStrFromDate));
			}

			if (lStrToDate != null && !lStrToDate.equals("")) {
				int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
				String lStrNextDate = gObjDtFormat.format(gObjDtFormat.parse(lStrToDate).getTime() + MILLIS_IN_DAY);
				lObjQuery.setDate("toDate", gObjDtFormat.parse(lStrNextDate));
			}
			if (lStrTreasuryName != null && !lStrTreasuryName.equals("-1")) {
				lObjQuery.setParameter("locCode", lStrTreasuryName);
			}
			if (lStrBankName != null && !lStrBankName.equals("-1")) {
				lObjQuery.setParameter("bankCode", lStrBankName);
			}
			if (lStrBranchName != null && !lStrBranchName.equals("-1")) {
				lObjQuery.setParameter("branchCode", lStrBranchName);
			}
			if (lStrPensionerName != null && !lStrPensionerName.equals("")) {
				lObjQuery.setParameter("pensionerName", '%' + lStrPensionerName + '%');
			}
			if (lStrAccountNumber != null && !lStrAccountNumber.equals("")) {
				lObjQuery.setParameter("accountNumber", lStrAccountNumber);
			}

			// Query.setString("locCode", lStrLocationCode);
			String urlPrefix = "";
			String lStrToLocCode = "";
			List lLstFinal = lObjQuery.list();
			if (lLstFinal != null && !lLstFinal.isEmpty()) {
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal.iterator();
				while (it.hasNext()) {
					lBlFlag = false;
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					if (tuple[0] != null) {
						lArrListInner.add(IFMSCommonServiceImpl.getStringFromDate((Date) tuple[0]));// inwd
						// date
					} else {
						lArrListInner.add("");
					}
					if (tuple[1] != null) {
						urlPrefix = "ifms.htm?actionFlag=loadPhyPenCase&showReadOnly=Y&showHistoryBtn=Y&pensionerId=" + tuple[7].toString();
						lArrListInner.add(new URLData(tuple[1].toString(), urlPrefix)); // ppo_no
					} else {
						lArrListInner.add("");
					}
					if (tuple[2] != null) {
						lArrListInner.add(tuple[2]);// pensioner_name
					} else {
						lArrListInner.add("");
					}
					if (tuple[3] != null) {
						lArrListInner.add(tuple[3]);// retirement_date
					} else {
						lArrListInner.add("");
					}
					if (tuple[4] != null) {
						lArrListInner.add(tuple[4]);// commencement_date
					} else {
						lArrListInner.add("");
					}
					if (tuple[5] != null) {
						for (Integer lInt = 0; lInt < lStrArrPensionType.length; lInt++) {
							if (lStrArrPensionType[lInt].equals(tuple[5].toString().trim())) {
								lBlFlag = true;
							}
						}
						if (lBlFlag) {
							lArrListInner.add(gObjRsrcBndleCaseLables.getString("PNSNTYPE." + tuple[5].toString().trim()));// pension_type
						} else {
							lArrListInner.add(tuple[5].toString());
						}
					} else {
						lArrListInner.add("");
					}
					if (tuple[6] != null) {
						lArrListInner.add(tuple[6]);// case_status
					} else {
						lArrListInner.add("");
					}
					if ("ROLEID." + tuple[6].toString().trim() != null && tuple[6].toString().length() > 0) {
						lStrCaseStatus = tuple[6].toString();
						if ((gObjRsrcBndle.getString("STATFLG.APPROVED")).equals(lStrCaseStatus) || (gObjRsrcBndle.getString("STATFLG.REJECTED")).equals(lStrCaseStatus)
								|| (gObjRsrcBndle.getString("STATFLG.MODIFIEDBYAUDITOR")).equals(lStrCaseStatus) || (gObjRsrcBndle.getString("STATFLG.IDENTIFIED")).equals(lStrCaseStatus)) {
							lLngPostId = Long.parseLong(tuple[8].toString());
							lStrEmpName = lObjCommonDAO.getEmpNameByPostId(lLngPostId);
						} else {
							if(gObjRsrcBndle.getString("STATFLG.TRANSFERNEW").equals(lStrCaseStatus))
							{
								lStrRoleId = gObjRsrcBndle.getString("ROLEID." + tuple[6].toString().trim());
								lObjTrnPensionTransferDtls = getTransferDtlsFromPnsnrCode(tuple[7].toString());
								lStrToLocCode = lObjTrnPensionTransferDtls.getToLocation();
								lStrEmpName = getEmpNameFromRoleId(lStrRoleId, lStrToLocCode);
							}
							else
							{
								lStrRoleId = gObjRsrcBndle.getString("ROLEID." + tuple[6].toString().trim());
								lStrEmpName = getEmpNameFromRoleId(lStrRoleId, tuple[9].toString());
							}
						}
						lArrListInner.add(lStrEmpName); // Lying with
					} else {
						lArrListInner.add("");
					}
					if (tuple[6] != null) {
						lArrListInner.add(gObjRsrcBndle.getString("LYINGAT." + tuple[6].toString().trim()));
					} else {
						lArrListInner.add("");
					}
					if (tuple[10] != null && !gObjRsrcBndle.getString("STATFLG.TRANSFERNEW").equals(lStrCaseStatus))  {
						lArrListInner.add(tuple[10]);
					} else if (gObjRsrcBndle.getString("STATFLG.TRANSFERNEW").equals(lStrCaseStatus)) {
						
						lArrListInner.add(lObjCommonDAO.getTreasuryName(1L, Long.parseLong(lStrToLocCode)));
					}else {
						lArrListInner.add("");
					}
					lArrListOuter.add(lArrListInner);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Exception in PensionCaseTrackingReportQueryDAOImpl.getPensionCaseTrackingReport is ::" + e.getMessage(), e);

		}
		return lArrListOuter;
	}

	public TrnPensionTransferDtls getTransferDtlsFromPnsnrCode(String lStrPensionerCode) throws Exception {
		
		Session hiSession = getSession();
		StringBuffer query = new StringBuffer();
		List<TrnPensionTransferDtls> lLstTrnPensionTransferDtls = null;
		TrnPensionTransferDtls lObjTrnPensionTransferDtls = new TrnPensionTransferDtls();
		try {
			query.append(" SELECT P FROM TrnPensionTransferDtls P WHERE P.pensionerCode in (:pensionerCode) order by P.createdDate desc ");
			
			Query sqlQuery = hiSession.createQuery(query.toString());
			sqlQuery.setParameter("pensionerCode", lStrPensionerCode);
			
			lLstTrnPensionTransferDtls = sqlQuery.list();
			if(!lLstTrnPensionTransferDtls.isEmpty())
			{
				lObjTrnPensionTransferDtls = lLstTrnPensionTransferDtls.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lObjTrnPensionTransferDtls;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getBillTrackingReport
	 * (com.tcs.sgv.common.valuebeans.reports.ReportVO, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */

	public List getBillTrackingReport(ReportVO lObjReport, String lStrLocationCode, String lStrFromDate, String lStrToDate, String lStrBillNo, String lStrBillType) {

		ArrayList lArrListOuter = new ArrayList();
		SQLQuery lObjSqlQuery = null;
		String lStrRoleId = null;
		String lStrEmpName = null;

		try {

			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT reg.bill_date,reg.bill_cntrl_no,hdr.bill_type,reg.ppo_no");
			lSBQuery.append(",party.party_name,reg.bill_gross_amount,reg.curr_bill_status ,cast('' as char) AS LyingWith "
					+ " FROM trn_bill_register reg,trn_pension_bill_dtls dtls,trn_pension_bill_hdr hdr,rlt_bill_party party"
					+ " WHERE reg.bill_no = hdr.bill_no AND hdr.trn_pension_bill_hdr_id = dtls.trn_pension_bill_hdr_id  AND party.bill_no = reg.bill_no" + " AND party.bill_no = hdr.bill_no ");

			// for Right Alignment format
			StyleVO[] RightAlignVO = new StyleVO[2];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			if (lStrFromDate != null) {
				lSBQuery.append(" AND reg.bill_Date >=:fromDate ");
			}
			if (lStrToDate != null) {
				lSBQuery.append(" AND reg.bill_Date <=:toDate ");
			}
			if (lStrBillNo != "" && lStrBillNo.length() > 0) {
				lSBQuery.append(" AND reg.bill_cntrl_no =:billCntrlNo ");
			}
			if (lStrBillType != null) {
				lSBQuery.append(" ANd hdr.bill_type=:billType  ");
			}
			if (lStrLocationCode != null) {
				lSBQuery.append(" AND hdr.location_code=:locationCode ");
			}
			if (!"".equals(lStrBillType) && lStrBillType.length() > 0) {
				if (lStrBillType.equals(gObjRsrcBndle.getString("PPMT.MONTHLY"))) {
					lSBQuery.append("GROUP BY reg.bill_date,reg.bill_cntrl_no,hdr.bill_type,reg.ppo_no,party.party_name,reg.bill_gross_amount,reg.curr_bill_status");
				}
			}
			lObjSqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lObjSqlQuery.setString("locationCode", lStrLocationCode);
			lObjSqlQuery.setDate("fromDate", gObjDtFormat.parse(lStrFromDate));
			int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
			String lStrNextDate = gObjDtFormat.format(gObjDtFormat.parse(lStrToDate).getTime() + MILLIS_IN_DAY);
			lObjSqlQuery.setDate("toDate", gObjDtFormat.parse(lStrNextDate));
			if (lStrBillNo != "" && lStrBillNo.length() > 0) {
				lObjSqlQuery.setParameter("billCntrlNo", lStrBillNo.trim());
			}

			lObjSqlQuery.setParameter("billType", lStrBillType.trim());
			lObjSqlQuery.setParameter("locationCode", lStrLocationCode.trim());
			List lLstFinal = lObjSqlQuery.list();
			if (lLstFinal != null && !lLstFinal.isEmpty()) {
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal.iterator();
				int lIntSrNo = 1;
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					lArrListInner.add(lIntSrNo);
					if (tuple[0] != null) {
						lArrListInner.add(IFMSCommonServiceImpl.getStringFromDate((Date) tuple[0]));// bill
						// date
					} else {
						lArrListInner.add("");
					}
					if (tuple[1] != null) {
						lArrListInner.add(tuple[1].toString().trim());// bill_cntrl_no
					} else {
						lArrListInner.add("");
					}
					if (tuple[2] != null) {
						lArrListInner.add(tuple[2]);// bill_type
					} else {
						lArrListInner.add("");
					}
					if (tuple[3] != null) {
						if (lStrBillType.equals(gObjRsrcBndle.getString("PPMT.MONTHLY"))) {
							lArrListInner.add("");// ppo_no
						} else {
							lArrListInner.add(tuple[3]);// ppo_no
						}
					} else {
						lArrListInner.add("");
					}
					if (tuple[4] != null) {
						lArrListInner.add(tuple[4]);// party_name
					} else {
						lArrListInner.add("");
					}
					if (tuple[5] != null) {
						lArrListInner.add(new StyledData(tuple[5], RightAlignVO));// bill_gross_amount
					} else {
						lArrListInner.add("");
					}
					if (tuple[6] != null) // status
					{
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_CREATED.toString().trim())) {
							lArrListInner.add("Created");
						}
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_FORW_TO_ATO.toString().trim())
								|| tuple[6].toString().trim().equals(DBConstants.ST_BILL_FORW_TO_TAUD.toString().trim())
								|| tuple[6].toString().trim().equals(DBConstants.ST_BILL_FORW_TO_USER.toString().trim())) {
							lArrListInner.add("Forwarded");
						}
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_REJECTED.toString().trim())
								|| tuple[6].toString().trim().equals(DBConstants.ST_BILL_REJECTED_BY_TRSRY.toString().trim())) {
							lArrListInner.add("Rejected");
						}
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_DISCARD.toString().trim())) {
							lArrListInner.add("Discarded");
						}
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_ECS_GENERTED.toString().trim())) {
							lArrListInner.add("ECS genereted");
						}
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_APPROVED.toString().trim())) {
							lArrListInner.add("Approved");
						}
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_ARCHEIVED.toString().trim())) {
							lArrListInner.add("Archeived");
						}
						if (tuple[6].toString().trim().equals(DBConstants.ST_BILL_ECS_AUTH.toString().trim())) {
							lArrListInner.add("ECS Authorized");
						}
					} else {
						lArrListInner.add("");
					}
					if ("ROLEID." + tuple[6].toString().trim() != null && tuple[6].toString().length() > 0) // Lying
					// With
					{
						lStrRoleId = gObjRsrcBndle.getString("ROLEID." + tuple[6].toString().trim());
						lStrEmpName = getEmpNameFromRoleId(lStrRoleId, lStrLocationCode);
						lArrListInner.add(lStrEmpName);

					} else {
						lArrListInner.add("");
					}

					lArrListOuter.add(lArrListInner);
					lIntSrNo++;
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Exception occured in BillTrackingReportQueryDAOImpl.getBillTrackingReport # \n" + e, e);
		}
		return lArrListOuter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getEmpNameFromRoleId
	 * (java.lang.String, java.lang.String)
	 */

	public String getEmpNameFromRoleId(String lStrRoleId, String lStrLocCode) {

		String lStrEmpName = "";
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		String lStrActiveFlag = null;
		try {

			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname),oup.activateFlag \n");
			lSBQuery.append(" FROM AclRoleMst arm,AclPostroleRlt apr,OrgUserpostRlt oup,OrgEmpMst oem, OrgPostMst opm \n");
			lSBQuery.append(" WHERE arm.roleId=apr.aclRoleMst.roleId \n");
			lSBQuery.append(" AND oup.orgPostMstByPostId.postId=apr.orgPostMst.postId \n");
			lSBQuery.append(" AND oup.orgUserMst.userId = oem.orgUserMst.userId \n");
			lSBQuery.append(" AND oup.orgPostMstByPostId.postId=opm.postId\n");
			lSBQuery.append(" AND arm.roleId=:roleId \n");
			lSBQuery.append(" AND opm.locationCode=:locationCode \n");
			lSBQuery.append(" Order by oup.createdDate desc \n");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("locationCode", lStrLocCode);
			lHibQry.setParameter("roleId", Long.valueOf(lStrRoleId));
			List lLstEmpName = lHibQry.list();

			if (!lLstEmpName.isEmpty()) {
				Object[] lObjArr = (Object[]) lLstEmpName.get(0);
				lStrActiveFlag = (lObjArr[1] != null) ? lObjArr[1].toString() : null;
				if (lStrActiveFlag != null && "1".equals(lStrActiveFlag)) {
					lStrEmpName = (lObjArr[0] != null) ? lObjArr[0].toString() : "";
				} else {
					lStrEmpName = "Vacant Post";
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lStrEmpName;
	}

	public BigDecimal getMandateSerialNo() throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		BigDecimal lBgdcSerailNo = BigDecimal.ZERO;

		try {

			/*
			 * char locCode1 = locCode.charAt(locCode.length() - 1); char
			 * locCode2 = locCode.charAt(locCode.length() - 2); String
			 * locationCode = String.valueOf(locCode2) + locCode1;
			 */
			// if (lIntCurrentMonth.length() == 1) {
			// lIntCurrentMonth = "0" + lIntCurrentMonth;
			// }
			lSBQuery.append(" SELECT MAX(mandate_Serial_no) mansrno FROM TRN_ECS_DTL");
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("mansrno", Hibernate.BIG_INTEGER);

			if (lQuery.uniqueResult() != null) {
				lBgdcSerailNo = BigDecimal.valueOf(Long.valueOf(lQuery.uniqueResult().toString()));
			} else {
				lBgdcSerailNo = BigDecimal.valueOf(Long.valueOf(0));
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lBgdcSerailNo.add(BigDecimal.ONE);
	}

	public void setMandateSerialNo(String lStrECSCode, BigDecimal lBgdcMandteSerialNo) throws Exception

	{

		StringBuffer lSBQuery = new StringBuffer();

		try {
			lSBQuery.append("UPDATE TRN_ECS_DTL SET mandate_Serial_no = :lBgdcMandteSerialNo WHERE ecs_code=:lStrECSCode");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lBgdcMandteSerialNo", lBgdcMandteSerialNo);
			lQuery.setParameter("lStrECSCode", lStrECSCode);
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
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getReportHeader(java
	 * .lang.String)
	 */

	public void setReportHeaderAndFooter(ReportVO lObjReport, LoginDetails lObjLoginVO) throws Exception {

		StringBuilder lSbHeaderVal = null;
		try {
			String lStrLocCode = lObjLoginVO.getLocation().getLocationCode().trim();
			lSbHeaderVal = getReportHeader(lStrLocCode);

			ReportAttributeVO[] lArrrReportAttributeVO = new ReportAttributeVO[2];
			lArrrReportAttributeVO[0] = new ReportAttributeVO();
			lArrrReportAttributeVO[0].setAttributeType(IReportConstants.ATTRIB_OTHER);
			lArrrReportAttributeVO[0].setLocation(IReportConstants.LOCATION_HEADER);
			lArrrReportAttributeVO[0].setAlignment(IReportConstants.ALIGN_RIGHT);
			lArrrReportAttributeVO[0].setAttributeValue("Date : " + DateUtility.getCurrentDateTime());

			lArrrReportAttributeVO[1] = new ReportAttributeVO();
			lArrrReportAttributeVO[1].setAttributeType(IReportConstants.ATTRIB_OTHER);
			lArrrReportAttributeVO[1].setLocation(IReportConstants.LOCATION_FOOTER);
			lArrrReportAttributeVO[1].setAlignment(IReportConstants.ALIGN_RIGHT);

			String lStrEmpName = ((lObjLoginVO.getEmployee().getEmpPrefix() != null) ? lObjLoginVO.getEmployee().getEmpPrefix() + " " : "")
					+ ((lObjLoginVO.getEmployee().getEmpFname() != null) ? lObjLoginVO.getEmployee().getEmpFname() + " " : "")
					+ ((lObjLoginVO.getEmployee().getEmpMname() != null) ? lObjLoginVO.getEmployee().getEmpMname() + " " : "")
					+ ((lObjLoginVO.getEmployee().getEmpLname() != null) ? lObjLoginVO.getEmployee().getEmpLname() + " " : "");

			lArrrReportAttributeVO[1].setAttributeValue("Prepared By : " + lStrEmpName);

			String lStrRptName = lObjReport.getReportName();
			lObjReport.setReportAttributes(lArrrReportAttributeVO);
			// for drill down report
			if (lStrRptName.indexOf(lSbHeaderVal.toString()) == -1) {

				lSbHeaderVal.append(lObjReport.getReportName() + "\r\n" + "\r\n"); // get
																					// original
				lObjReport.setReportName(lSbHeaderVal.toString());
			} // name
				// from
				// sgvc_reports

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public StringBuilder getReportHeader(String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		StringBuilder lSbHeaderVal = new StringBuilder();
		try {
			lSBQuery.append(" SELECT loc.loc_name,concat(concat(concat(concat(concat(concat(concat(concat(loc.loc_addr_1,' '),loc.loc_addr_2),' '),city.city_name),' '),dist.district_name),' '),state.state_name) , cntc.office_phone,cntc.email ");
			lSBQuery.append(" FROM cmn_city_mst city,cmn_district_mst dist,cmn_state_mst state  ,cmn_location_mst loc left outer join cmn_contact_mst cntc  on cntc.loc_id = loc.loc_id  ");
			lSBQuery.append(" WHERE city.city_id = loc.loc_city_id AND dist.district_id = city.district_id AND state.state_id = dist.state_id  ");
			lSBQuery.append(" AND loc.department_id =  100003 AND loc.location_code = :locationCode ");
			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
			Query.setString("locationCode", lStrLocCode);
			List lLstFinal = Query.list();
			if (lLstFinal != null && !lLstFinal.isEmpty()) {
				Iterator it = lLstFinal.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					if (tuple[0] != null) {
						lSbHeaderVal.append(tuple[0].toString() + "\r\n"); // Office
																			// Name
					}
					if (tuple[1] != null) {
						lSbHeaderVal.append(tuple[1] + "\r\n"); // Office
																// Address
					}
					if (tuple[2] != null) {
						lSbHeaderVal.append("Ph: " + tuple[2].toString() + "\r\n"); // Office
																					// Phone
																					// no.
					}
					if (tuple[3] != null) {
						lSbHeaderVal.append("E-mail: " + tuple[3].toString() + "\r\n" + "\r\n" + "\r\n"); // Office
																											// E-mail
					}
				}
			}

		} catch (Exception e) {

			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lSbHeaderVal;
	}

	public List getBankBranchWisePensionerCount(String lStrBankCode, String lStrBranchCode, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		List lLstResultList = null;
		try {
			lSBQuery = new StringBuilder();
			if (!"".equals(lStrBankCode) && lStrBankCode.length() != 0) {
				lSBQuery.append("select mb.bank_name,mpd.bank_code,rbb.branch_name,mpd.branch_code,count(mpd.pensioner_code) ");
				lSBQuery.append(" from mst_pensioner_dtls mpd,mst_bank mb,rlt_bank_branch rbb ");
				lSBQuery.append(" where mpd.bank_code=mb.bank_code and mpd.branch_code=rbb.branch_code ");
				lSBQuery.append(" and mpd.location_code = :locationCode ");

				if (Long.valueOf(lStrBankCode) != 0L) {
					lSBQuery.append(" and mpd.bank_code = :bankCode ");

					if (Long.valueOf(lStrBranchCode) != 0L) {
						lSBQuery.append(" and mpd.branch_code = :branchCode ");
					}
				}
				lSBQuery.append(" group by mb.bank_name,mpd.bank_code,rbb.branch_name,mpd.branch_code ");
				lSBQuery.append(" order by mpd.branch_code,mpd.bank_code,mb.bank_name,rbb.branch_name ");
			}
			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.setString("locationCode", lStrLocationCode);

			if (Long.valueOf(lStrBankCode) != 0L) {
				Query.setParameter("bankCode", lStrBankCode);

				if (Long.valueOf(lStrBranchCode) != 0L) {
					Query.setParameter("branchCode", lStrBranchCode);

				}
			}

			lLstResultList = Query.list();
			return lLstResultList;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public List getPensionerCountForMonth(String lStrBankCode, String lStrBranchCode, String lStrForMonth, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		List lLstResultList = null;
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		Short lShCreatedStatus = 5;
		Short lShForApprovalStatus = 10;

		try {
			List<Short> lLstCurrBillStatus = new ArrayList<Short>();
			lLstCurrBillStatus.add(lShRjctedStatus);
			lLstCurrBillStatus.add(lShArchivedStatus);
			lLstCurrBillStatus.add(lShDiscardedStatus);
			lLstCurrBillStatus.add(lShCreatedStatus);
			lLstCurrBillStatus.add(lShForApprovalStatus);

			lSBQuery = new StringBuilder();

			lSBQuery.append("select mb.bank_name,pbh.bank_code,rbb.branch_name,pbh.branch_code,sum(pbh.NO_OF_PNSR),cast(sum(pbh.GROSS_AMOUNT) as bigint), \n");
			lSBQuery.append("cast(sum(pbh.deduction_a + pbh.deduction_b) as bigint),cast(sum(pbh.NET_AMOUNT) as bigint) \n");
			lSBQuery.append("From trn_bill_register tbr  \n");
			lSBQuery.append("join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no \n");
			lSBQuery.append("join mst_bank mb on mb.bank_code = pbh.bank_code  \n");
			lSBQuery.append("join rlt_bank_branch rbb on rbb.branch_code = pbh.branch_code and rbb.location_code = :locationCode and rbb.lang_id = 1 \n");
			lSBQuery.append("where pbh.for_month = :payForMonth and pbh.bill_type = :billType \n");
			if (lStrBankCode != null && Long.valueOf(lStrBankCode) != 0L) {
				lSBQuery.append("and pbh.bank_code = :bankCode  \n");
			}
			if (lStrBranchCode != null && Long.valueOf(lStrBranchCode) != 0L) {
				lSBQuery.append("and pbh.branch_code = :branchCode  \n");
			}
			lSBQuery.append("and tbr.curr_bill_status not in (:billStatusList) \n");
			lSBQuery.append("group by pbh.bank_code,mb.bank_name,pbh.branch_code,rbb.branch_name \n");
			lSBQuery.append("order by mb.bank_name,rbb.branch_name \n");

			// if (!"".equals(lStrBankCode) && lStrBankCode.length() != 0) {
			// lSBQuery.append("select mb.bank_name,mpd.bank_code,rbb.branch_name,mpd.branch_code,count(mpd.pensioner_code),cast(sum(gross_amount) as double),cast(sum(recovery_amount) as double),cast(sum(net_amount) as double) ");
			// lSBQuery.append(" from mst_pensioner_dtls mpd,mst_bank mb,rlt_bank_branch rbb,trn_pension_bill_dtls dtls ");
			// lSBQuery.append(" where mpd.bank_code=mb.bank_code and mpd.branch_code=rbb.branch_code ");
			// lSBQuery.append(" and mpd.location_code = :locationCode ");
			// lSBQuery.append(" and mpd.pensioner_code=dtls.pensioner_code ");
			//
			// if (Long.valueOf(lStrBankCode) != 0L) {
			// lSBQuery.append(" and mpd.bank_code = :bankCode ");
			//
			// if (Long.valueOf(lStrBranchCode) != 0L) {
			// lSBQuery.append(" and mpd.branch_code = :branchCode ");
			// }
			// }
			// lSBQuery.append(" and dtls.pay_for_month=:payForMonth ");
			// lSBQuery.append(" group by mb.bank_name,mpd.bank_code,rbb.branch_name,mpd.branch_code ");
			// lSBQuery.append(" order by mpd.branch_code,mpd.bank_code,mb.bank_name,rbb.branch_name ");
			// }
			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.setString("locationCode", lStrLocationCode);
			Query.setString("payForMonth", lStrForMonth);
			Query.setString("billType", gObjRsrcBndle.getString("PPMT.MONTHLY"));
			Query.setParameterList("billStatusList", lLstCurrBillStatus);

			if (lStrBankCode != null && Long.valueOf(lStrBankCode) != 0L) {
				Query.setParameter("bankCode", lStrBankCode);
			}
			if (lStrBranchCode != null && Long.valueOf(lStrBranchCode) != 0L) {
				Query.setParameter("branchCode", lStrBranchCode);
			}

			lLstResultList = Query.list();
			return lLstResultList;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public List getAuditorwisePensionerCountForMonth(String lStrAudPostId, String lStrForMonth, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		List lLstResultList = null;
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		Short lShCreatedStatus = 5;
		Short lShForApprovalStatus = 10;
		try {
			List<Short> lLstCurrBillStatus = new ArrayList<Short>();
			lLstCurrBillStatus.add(lShRjctedStatus);
			lLstCurrBillStatus.add(lShArchivedStatus);
			lLstCurrBillStatus.add(lShDiscardedStatus);
			lLstCurrBillStatus.add(lShCreatedStatus);
			lLstCurrBillStatus.add(lShForApprovalStatus);

			lSBQuery = new StringBuilder();

			lSBQuery.append("select mb.bank_name,pbh.bank_code,rbb.branch_name,pbh.branch_code,count(pbh.trn_pension_bill_hdr_id),tbr.bill_gross_amount, \n");
			lSBQuery.append("cast(sum(tbr.deduction_a + tbr.deduction_b) as double),tbr.bill_net_amount \n");
			lSBQuery.append("From trn_pension_bill_hdr pbh \n");
			lSBQuery.append("join trn_pension_bill_dtls pbd on pbh.trn_pension_bill_hdr_id = pbd.trn_pension_bill_hdr_id \n");
			lSBQuery.append("join trn_bill_register tbr on tbr.bill_no = pbh.bill_no \n");
			lSBQuery.append("join mst_bank mb on mb.bank_code = pbh.bank_code  \n");
			lSBQuery.append("join rlt_bank_branch rbb on rbb.branch_code = pbh.branch_code and rbb.location_code = :locationCode and rbb.lang_id = 1 \n");
			lSBQuery.append("join rlt_auditor_bank rab on rab.branch_code = pbh.branch_code and rab.location_code = :locationCode \n");
			lSBQuery.append("where pbh.for_month = :payForMonth and pbh.bill_type = :billType \n");
			lSBQuery.append("and rab.post_id = :audPostId \n");
			lSBQuery.append("and tbr.curr_bill_status not in (:billStatusList) \n");
			lSBQuery.append("group by pbh.bank_code,pbh.branch_code,mb.bank_name,rbb.branch_name,tbr.bill_gross_amount, \n");
			lSBQuery.append("tbr.deduction_a,tbr.bill_net_amount \n");
			lSBQuery.append("order by pbh.bank_code,pbh.branch_code \n");

			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.setString("locationCode", lStrLocationCode);
			Query.setString("payForMonth", lStrForMonth);
			Query.setString("billType", gObjRsrcBndle.getString("PPMT.MONTHLY"));
			Query.setParameterList("billStatusList", lLstCurrBillStatus);
			Query.setString("audPostId", lStrAudPostId);

			lLstResultList = Query.list();
			return lLstResultList;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public List getArrearDtlsBankBranchWise(String lStrBankCode, String lStrBranchCode, String lStrForMonth, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		List lLstResultList = null;
		try {
			lSBQuery = new StringBuilder();
			if (!"".equals(lStrBankCode) && lStrBankCode.length() != 0) {
				lSBQuery.append(" select dtls.ppo_no,mph.first_name,mpd.bank_code,mpd.branch_code,cast(SUM(dtls.arrear_amount) as double),cast(SUM(dtls.ti_arrear_amount) as double) ");
				lSBQuery.append(" from mst_pensioner_dtls mpd,trn_pension_bill_dtls dtls,mst_pensioner_hdr mph ");
				lSBQuery.append(" where mpd.pensioner_code=dtls.pensioner_code and mpd.pensioner_code=mph.pensioner_code ");
				lSBQuery.append(" and mpd.location_code = :locationCode ");
				lSBQuery.append(" and mpd.pensioner_code=dtls.pensioner_code ");

				if (Long.valueOf(lStrBankCode) != 0L) {
					lSBQuery.append(" and mpd.bank_code = :bankCode ");

					if (Long.valueOf(lStrBranchCode) != 0L) {
						lSBQuery.append(" and mpd.branch_code = :branchCode ");
					}
				}
				lSBQuery.append(" and dtls.pay_for_month=:payForMonth ");
				lSBQuery.append(" group by mpd.bank_code,mpd.branch_code,dtls.ppo_no,mph.first_name ");
				lSBQuery.append(" order by mpd.bank_code,mpd.branch_code,dtls.ppo_no,mph.first_name ");
			}
			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.setString("locationCode", lStrLocationCode);
			Query.setParameter("payForMonth", lStrForMonth);

			if (Long.valueOf(lStrBankCode) != 0L) {
				Query.setParameter("bankCode", lStrBankCode);

				if (Long.valueOf(lStrBranchCode) != 0L) {
					Query.setParameter("branchCode", lStrBranchCode);

				}
			}

			lLstResultList = Query.list();
			return lLstResultList;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public List getRecoveryReport(String lStrBankCode, String lStrBranchCode, String lStrForMonth, String lStrSchemeCode, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		List lLstResultList = null;
		try {
			lSBQuery = new StringBuilder();
			if (!"".equals(lStrBankCode) && lStrBankCode.length() != 0) {
				lSBQuery.append(" select mrd.ppo_no,hdr.first_name,mrd.bank_code,mrd.branch_code,cast(sum(mrd.amount) as decimal),mrd.scheme_code  ");
				//
				// lSBQuery.append(" select mrd.ppo_no,hdr.first_name,mrd.bank_code,mrd.branch_code,mrd.scheme_code  ");
				lSBQuery.append(" from trn_monthly_pension_recovery_dtls mrd,mst_pensioner_hdr hdr ");
				lSBQuery.append(" where mrd.pensioner_code=hdr.pensioner_code ");
				lSBQuery.append(" and mrd.location_code = :locationCode ");

				if (Long.valueOf(lStrBankCode) != 0L) {
					lSBQuery.append(" and mrd.bank_code = :bankCode ");

					if (Long.valueOf(lStrBranchCode) != 0L) {
						lSBQuery.append(" and mrd.branch_code = :branchCode ");
					}
				}
				if (!"0".equals(lStrSchemeCode)) {
					lSBQuery.append(" and mrd.scheme_code = :schemeCode ");
				}
				lSBQuery.append(" and mrd.for_month=:payForMonth ");
				//
				lSBQuery.append(" group by mrd.pensioner_code,mrd.bank_code,mrd.branch_code,mrd.ppo_no,hdr.first_name,mrd.scheme_code ");
				// lSBQuery.append(" group by mrd.ppo_no,hdr.first_name,mrd.bank_code,mrd.branch_code,mrd.scheme_code");
				//
				lSBQuery.append(" order by mrd.bank_code,mrd.branch_code,mrd.scheme_code,mrd.ppo_no,hdr.first_name,mrd.pensioner_code ");
			}
			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.setParameter("locationCode", lStrLocationCode);
			Query.setInteger("payForMonth", Integer.valueOf((lStrForMonth)));

			if (Long.valueOf(lStrBankCode) != 0L) {
				Query.setParameter("bankCode", lStrBankCode);

				if (Long.valueOf(lStrBranchCode) != 0L) {
					Query.setParameter("branchCode", lStrBranchCode);

				}
			}
			if (!"0".equals(lStrSchemeCode)) {
				Query.setParameter("schemeCode", lStrSchemeCode);
			}

			lLstResultList = Query.list();
			return lLstResultList;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getSixPayArrearDtls(
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */

	public List getSixPayArrearDtls(String lStrBankCode, String lStrBranchCode, String lStrPpoNo, String lStrLocationCode) throws Exception {

		ArrayList lArrListOuter = new ArrayList();
		List lLstFinal = null;
		SQLQuery lObjSqlQuery = null;
		int lIntSerialNo = 0;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT hdr.first_name,rqst.ppo_no,cast('' as char) AS TotalArrear,arrear1.installment_amnt,arrear1.pay_in_month, ");
			lSBQuery.append(" arrear2.installment_amnt,arrear2.pay_in_month,arrear3.installment_amnt,arrear3.pay_in_month,");
			lSBQuery.append(" arrear4.installment_amnt,arrear4.pay_in_month,arrear5.installment_amnt,arrear5.pay_in_month ");
			lSBQuery.append(" FROM trn_pension_rqst_hdr rqst,mst_pensioner_dtls dtls,mst_pensioner_hdr hdr,trn_pension_sixpayfp_arrear arrear1,");
			lSBQuery.append(" trn_pension_sixpayfp_arrear arrear2,trn_pension_sixpayfp_arrear arrear3, ");
			lSBQuery.append(" trn_pension_sixpayfp_arrear arrear4,trn_pension_sixpayfp_arrear arrear5 ");
			lSBQuery.append(" WHERE rqst.pensioner_code = arrear1.pensioner_code AND rqst.pensioner_code = dtls.pensioner_code AND dtls.pensioner_code = arrear1.pensioner_code ");
			lSBQuery.append(" AND hdr.pensioner_code = arrear1.pensioner_code AND hdr.pensioner_code = rqst.pensioner_code AND hdr.pensioner_code = dtls.pensioner_code ");
			lSBQuery.append(" AND rqst.pensioner_code = arrear2.pensioner_code AND rqst.pensioner_code = dtls.pensioner_code AND dtls.pensioner_code = arrear2.pensioner_code ");
			lSBQuery.append(" AND hdr.pensioner_code = arrear2.pensioner_code AND hdr.pensioner_code = rqst.pensioner_code AND hdr.pensioner_code = dtls.pensioner_code ");
			lSBQuery.append(" AND rqst.pensioner_code = arrear3.pensioner_code AND rqst.pensioner_code = dtls.pensioner_code AND dtls.pensioner_code = arrear3.pensioner_code ");
			lSBQuery.append(" AND hdr.pensioner_code = arrear3.pensioner_code AND hdr.pensioner_code = rqst.pensioner_code AND hdr.pensioner_code = dtls.pensioner_code ");
			lSBQuery.append(" AND rqst.pensioner_code = arrear4.pensioner_code AND rqst.pensioner_code = dtls.pensioner_code AND dtls.pensioner_code = arrear4.pensioner_code ");
			lSBQuery.append(" AND hdr.pensioner_code = arrear4.pensioner_code AND hdr.pensioner_code = rqst.pensioner_code AND hdr.pensioner_code = dtls.pensioner_code ");
			lSBQuery.append(" AND rqst.pensioner_code = arrear5.pensioner_code AND rqst.pensioner_code = dtls.pensioner_code AND dtls.pensioner_code = arrear5.pensioner_code ");
			lSBQuery.append(" AND hdr.pensioner_code = arrear5.pensioner_code AND hdr.pensioner_code = rqst.pensioner_code AND hdr.pensioner_code = dtls.pensioner_code ");
			lSBQuery.append(" AND arrear1.active_flag = 'Y' AND arrear2.active_flag = 'Y' AND arrear3.active_flag = 'Y' ");
			lSBQuery.append(" AND arrear4.active_flag = 'Y' AND arrear5.active_flag = 'Y' ");
			lSBQuery.append(" AND arrear1.installment_no = 1 AND arrear2.installment_no = 2 AND arrear3.installment_no = 3 ");
			lSBQuery.append(" AND arrear4.installment_no = 4 AND arrear5.installment_no = 5  AND rqst.location_code = :locationCode ");
			lSBQuery.append(" AND dtls.bank_code = :bankCode AND dtls.branch_code = :branchCode ");

			if (lStrPpoNo != null && lStrPpoNo.length() > 0) {
				lSBQuery.append(" and rqst.ppo_No like '" + lStrPpoNo.trim() + "' ");
			}

			lObjSqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lObjSqlQuery.setString("locationCode", lStrLocationCode.trim());
			lObjSqlQuery.setString("bankCode", lStrBankCode.trim());
			lObjSqlQuery.setString("branchCode", lStrBranchCode.trim());

			lLstFinal = lObjSqlQuery.list();
			if (lLstFinal != null && !lLstFinal.isEmpty()) {
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					lIntSerialNo++;
					lArrListInner.add(lIntSerialNo);
					if (tuple[0] != null) {
						lArrListInner.add(tuple[0]);// pensioner_name
					} else {
						lArrListInner.add("");
					}
					if (tuple[1] != null) {
						lArrListInner.add(tuple[1]);// ppo_no
					} else {
						lArrListInner.add("");
					}
					if (tuple[3] != null) // total arrear amount
					{
						Long lLngTotalArrearAmnt = Long.valueOf(((BigDecimal) tuple[3]).longValue()) + Long.valueOf(((BigDecimal) tuple[5]).longValue())
								+ Long.valueOf(((BigDecimal) tuple[7]).longValue()) + Long.valueOf(((BigDecimal) tuple[9]).longValue()) + Long.valueOf(((BigDecimal) tuple[11]).longValue());
						lArrListInner.add(lLngTotalArrearAmnt);

					} else {
						lArrListInner.add("");
					}
					if (tuple[3] != null) {
						lArrListInner.add(tuple[3]);// inst 1 amount
					} else {
						lArrListInner.add("");
					}
					if (tuple[4] != null) {
						lArrListInner.add(tuple[4]);// inst 1 yearmonth
					} else {
						lArrListInner.add("Not paid yet");
					}
					if (tuple[5] != null) {
						lArrListInner.add(tuple[5]);// inst 2 amount
					} else {
						lArrListInner.add("");
					}
					if (tuple[6] != null) {
						lArrListInner.add(tuple[6]);// inst 2 yearmonth
					} else {
						lArrListInner.add("Not paid yet");
					}
					if (tuple[7] != null) {
						lArrListInner.add(tuple[7]);// inst 3 amount
					} else {
						lArrListInner.add("");
					}
					if (tuple[8] != null) {
						lArrListInner.add(tuple[8]);// inst 3 yearmonth
					} else {
						lArrListInner.add("Not paid yet");
					}
					if (tuple[9] != null) {
						lArrListInner.add(tuple[9]);// inst 4 amount
					} else {
						lArrListInner.add("");
					}
					if (tuple[10] != null) {
						lArrListInner.add(tuple[10]);// inst 4 yearmonth
					} else {
						lArrListInner.add("Not paid yet");
					}
					if (tuple[11] != null) {
						lArrListInner.add(tuple[11]);// inst 5 amount
					} else {
						lArrListInner.add("");
					}
					if (tuple[12] != null) {
						lArrListInner.add(tuple[12]);// inst 5 yearmonth
					} else {
						lArrListInner.add("Not paid yet");
					}

					lArrListOuter.add(lArrListInner);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Exception in PensionpayQueryDAOImpl.getSixPayArrearDtls is ::" + e.getMessage(), e);

		}
		return lArrListOuter;

	}

	public List getFirstPmntCases(String lStrTreasuryCode, String lStrPPONo) throws Exception {

		ArrayList lArrListOuter = new ArrayList();
		List lLstFinal1 = null;
		List lLstFinal2 = null;
		SQLQuery lObjSqlQuery1 = null;
		SQLQuery lObjSqlQuery2 = null;
		Iterator lObjIterator = null;
		int lIntSerialNo = 0;
		List<String> lLstPPONo = new ArrayList<String>();
		List<Date> lLstAuthorityDate = new ArrayList<Date>();
		int lIntPosition = 0;
		Boolean lBlFlag = false;
		Object[] lArrObj = null;
		try {
			StringBuilder lSBQuery1 = new StringBuilder();
			StringBuilder lSBQuery2 = new StringBuilder();

			lSBQuery1.append(" SELECT req.ppo_no,hdr.first_name,req.ppo_inward_date,req.ppo_reg_date,dtls.identification_date,dtls.identification_flag,req.commencement_date ");
			lSBQuery1.append(" FROM trn_pension_rqst_hdr req,mst_pensioner_dtls dtls,mst_pensioner_hdr hdr ");
			lSBQuery1.append(" WHERE req.pensioner_code = dtls.pensioner_code AND req.pensioner_code = hdr.pensioner_code ");
			lSBQuery1.append(" AND  hdr.pensioner_code = dtls.pensioner_code AND req.location_code =:locationCode ");

			if (lStrPPONo != null && lStrPPONo.length() > 0) {
				lSBQuery1.append(" and req.ppo_no like '" + lStrPPONo.trim() + "' ");
			}

			lSBQuery2.append(" SELECT rqst.ppo_no,cheque.authority_date  ");
			lSBQuery2.append(" FROM trn_pension_bill_dtls bill,trn_pension_rqst_hdr rqst,trn_pension_bill_hdr billhdr,trn_bill_register reg ");
			lSBQuery2.append(" ,rlt_bill_cheque rlt,trn_cheque_dtls cheque  ");
			lSBQuery2.append(" WHERE bill.pensioner_code = rqst.pensioner_code AND billhdr.trn_pension_bill_hdr_id=bill.trn_pension_bill_hdr_id  ");
			lSBQuery2.append(" AND reg.bill_no = billhdr.bill_no AND  rqst.location_code = :locationCode  ");
			lSBQuery2.append(" AND rlt.bill_no = reg.bill_no AND rlt.bill_no = billhdr.bill_no AND rlt.cheque_id = cheque.cheque_id ");
			lSBQuery2.append(" GROUP BY rqst.ppo_no,cheque.authority_date ORDER BY cheque.authority_date ");

			lObjSqlQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());
			lObjSqlQuery1.setString("locationCode", lStrTreasuryCode.trim());

			lObjSqlQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
			lObjSqlQuery2.setString("locationCode", lStrTreasuryCode.trim());

			lLstFinal1 = lObjSqlQuery1.list();
			lLstFinal2 = lObjSqlQuery2.list();

			lObjIterator = lLstFinal2.iterator();
			while (lObjIterator.hasNext()) {
				lArrObj = (Object[]) lObjIterator.next();
				if (lArrObj[1] != null) {
					lLstAuthorityDate.add((Date) lArrObj[1]);
				} else {
					lLstAuthorityDate.add(null);
				}
				lLstPPONo.add((String) lArrObj[0]);
			}

			if (lLstFinal1 != null && !lLstFinal1.isEmpty()) {
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal1.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					lIntSerialNo++;
					lArrListInner.add(lIntSerialNo);
					if (tuple[0] != null) {
						lArrListInner.add(tuple[0]);// ppo_no
					} else {
						lArrListInner.add("");
					}
					if (tuple[1] != null) {
						lArrListInner.add(tuple[1]);// pensioner_name
					} else {
						lArrListInner.add("");
					}
					if (tuple[2] != null) // Online PPO Received Date
					{
						lArrListInner.add(DateUtilities.stringFromDate((Date) tuple[2]));
					} else {
						lArrListInner.add("-");
					}
					if (tuple[3] != null) {
						lArrListInner.add(DateUtilities.stringFromDate((Date) tuple[3]));// Physical
																							// PPO
																							// Recieved
																							// Date
					} else {
						lArrListInner.add("-");
					}
					if (tuple[4] != null) {
						lArrListInner.add(DateUtilities.stringFromDate((Date) tuple[4]));// Identification
																							// Date
					} else {
						lArrListInner.add("-");
					}
					if (tuple[5] != null) {
						lArrListInner.add(tuple[5]); // Identification
														// Flag
					} else {
						lArrListInner.add("N");
					}
					if (tuple[0] != null) {
						for (int lIntCnt = 0; lIntCnt < lLstPPONo.size(); lIntCnt++) {
							if (tuple[0].toString().trim().equals(lLstPPONo.get(lIntCnt).trim())) {
								lIntPosition = lIntCnt;
								lBlFlag = true;
							}
						}
						if (lBlFlag == true) {
							if (lLstAuthorityDate.get(lIntPosition) != null) {
								lArrListInner.add(lLstAuthorityDate.get(lIntPosition));// ECS
																						// Authorised
																						// Date
							} else {
								lArrListInner.add("-");
							}
						} else {
							lArrListInner.add("-");
						}
					}
					if (tuple[6] != null) {
						lArrListInner.add(DateUtilities.stringFromDate((Date) tuple[6]));// Pension
																							// Start
																							// Date
					} else {
						lArrListInner.add("-");
					}

					lArrListOuter.add(lArrListInner);
					lBlFlag = false;
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Exception in PensionpayQueryDAOImpl.getFirstPmntCases is ::" + e.getMessage(), e);

		}
		return lArrListOuter;
	}

	public List getArchivedCases(String lStrTreasuryCode, String lStrPPONo) throws Exception {

		ArrayList lArrListOuter = new ArrayList();
		List lLstFinal = null;
		SQLQuery lObjSqlQuery = null;
		int lIntSerialNo = 0;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT rqst.ppo_no,hdr.first_name,rqst.case_status ");
			lSBQuery.append(" FROM trn_pension_rqst_hdr rqst,mst_pensioner_hdr hdr");
			lSBQuery.append(" WHERE rqst.pensioner_code = hdr.pensioner_code ");
			lSBQuery.append(" AND rqst.location_code =:locationCode AND  rqst.case_status IN ('" + gObjRsrcBndle.getString("STATFLG.ARCHIVED") + "','"
					+ gObjRsrcBndle.getString("STATFLG.TRANSFERSTATE") + "')");

			if (lStrPPONo != null && lStrPPONo.length() > 0) {
				lSBQuery.append(" AND rqst.ppo_no like '" + lStrPPONo.trim() + "' ");
			}

			lObjSqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lObjSqlQuery.setString("locationCode", lStrTreasuryCode.trim());

			lLstFinal = lObjSqlQuery.list();
			if (lLstFinal != null && !lLstFinal.isEmpty()) {
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					lIntSerialNo++;
					lArrListInner.add(lIntSerialNo);
					if (tuple[0] != null) {
						lArrListInner.add(tuple[0]);// ppo_no
					} else {
						lArrListInner.add("");
					}
					if (tuple[1] != null) {
						lArrListInner.add(tuple[1]);// pensioner_name
					} else {
						lArrListInner.add("");
					}
					if (tuple[2] != null) { // case status
						if (tuple[2].toString().trim().equals(gObjRsrcBndle.getString("STATFLG.ARCHIVED"))) {
							lArrListInner.add("ARCHIEVED");
						} else {
							lArrListInner.add("TRANSFER TO OTHER STATE");
						}

					} else {
						lArrListInner.add("");
					}
					lArrListOuter.add(lArrListInner);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Exception in PensionpayQueryDAOImpl.getArchivedCases is ::" + e.getMessage(), e);

		}
		return lArrListOuter;

	}

	public List<Object[]> getBankPaymentDtlsForMonthly(String lStrBankCode, String lStrBranchCode, String lStrForMonthYear, String lStrAudPostId, String lStrLocationCode, String lStrAudBankFlag,
			List<String> lLstPaymentMode, String lStrExportTo, Date lDtFromDate, Date lDtToDate, String lStrBillType, String lStrSchemeCode, String lStrOrderBy, String lStrReportingBankCode,
			String lStrReportingBranchCode) throws Exception {

		StringBuilder lSBQuery = null;
		List<Object[]> lLstResultList = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append("select tpb.ppo_no,tpb.pensioner_name,tpb.net_amount,tpb.account_no \n");
			lSBQuery.append(",mb.bank_name,rbb.branch_name,tph.bank_code,tph.branch_code,tph.bill_no \n");
			lSBQuery.append(",cast('' as char),cast('' as char),cast('' as char),tpb.ledger_no,tpb.page_no \n");
			// lSBQuery.append(",tph.scheme_code  \n");
			// lSBQuery.append(" from trn_pension_bill_dtls tpb, trn_pension_bill_hdr tph,mst_bank mb, rlt_bank_branch rbb,trn_bill_register tbr \n");
			lSBQuery.append(" from trn_bill_register tbr,trn_pension_bill_hdr tph,trn_pension_bill_dtls tpb,mst_bank mb, rlt_bank_branch rbb \n");
			// lSBQuery.append(" left outer join rlt_bill_party rbp on tbr.bill_no = rbp.bill_no and rbp.payment_mode in (:paymentMode)\n");

			if ("Auditor".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" ,rlt_auditor_bank rlb \n");
			}

			lSBQuery.append(" where tph.trn_pension_bill_hdr_id=tpb.trn_pension_bill_hdr_id \n");
			lSBQuery.append(" and tph.bank_code=mb.bank_code and tph.branch_code=rbb.branch_code \n");
			lSBQuery.append(" and tbr.bill_no=tph.bill_no and tbr.pay_mode in (:paymentMode)\n");
			// lSBQuery.append(" and tph.scheme_code = :schemeCode \n");
			lSBQuery.append(" and tph.bill_type = :billType and tbr.curr_bill_status in (80,20,30,40,50,5,10) \n");

			lSBQuery.append(" and tpb.pay_for_month = :payForMonth \n");

			if ("Auditor".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" and tph.bank_code=rlb.bank_code and tph.branch_code=rlb.branch_code and \n");
				lSBQuery.append(" rlb.post_id = :audPostId \n");
			}
			if ("Bank".equalsIgnoreCase(lStrAudBankFlag) || "Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" and tph.bank_code = :bankCode \n");
			}
			if ("Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" and tph.branch_code = :branchCode \n");
			}
			if ("RptBranch".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" and rbb.reporting_bank_code = :lRptBankCode \n");
				lSBQuery.append(" and rbb.reporting_branch_code = :lRptBranchCode \n");
			}
			lSBQuery.append(" and tbr.location_code = :locationCode \n");
			lSBQuery.append(" group by mb.bank_name,rbb.branch_name,tph.bank_code,tph.branch_code,tph.bill_no,tpb.ppo_no, \n");
			lSBQuery.append("tpb.pensioner_name,tpb.net_amount,tpb.account_no,tpb.ledger_no,tpb.page_no ");

			if (!"".equals(lStrOrderBy)) {
				if (gObjRsrcBndle.getString("ORDERBY.VOLUMEPAGENO").equals(lStrOrderBy)) {
					lSBQuery.append(" order by cast(tpb.ledger_no as double),cast(tpb.page_no as double)");
				}
				if (gObjRsrcBndle.getString("ORDERBY.ACCOUNTNO").equals(lStrOrderBy)) {
					lSBQuery.append(" order by cast(tpb.account_no as double)");
				}
			}
			// lSBQuery.append(",tph.scheme_code  ");

			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.setString("payForMonth", lStrForMonthYear);
			Query.setString("billType", lStrBillType);
			// Query.setString("schemeCode", lStrSchemeCode);
			Query.setParameterList("paymentMode", lLstPaymentMode);
			Query.setString("locationCode", lStrLocationCode);

			if ("Auditor".equalsIgnoreCase(lStrAudBankFlag)) {
				Query.setString("audPostId", lStrAudPostId);
			}
			if ("Bank".equalsIgnoreCase(lStrAudBankFlag) || "Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				Query.setString("bankCode", lStrBankCode);
			}
			if ("Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				Query.setString("branchCode", lStrBranchCode);
			}
			if ("RptBranch".equalsIgnoreCase(lStrAudBankFlag)) {
				Query.setString("lRptBankCode", lStrReportingBankCode);
				Query.setString("lRptBranchCode", lStrReportingBranchCode);
			}
			lLstResultList = Query.list();

			return lLstResultList;

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public List<Object[]> getBankPaymentDtls(String lStrBankCode, String lStrBranchCode, String lStrForMonthYear, String lStrAudPostId, String lStrLocationCode, String lStrAudBankFlag,
			List<String> lLstPaymentMode, String lStrExportTo, Date lDtFromDate, Date lDtToDate, String lStrBillType, String lStrOrderBy) throws Exception {

		StringBuilder lSBQuery = null;
		List<Object[]> lLstResultList = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append("select tpb.ppo_no,tpb.pensioner_name,tpb.net_amount,rbp.accnt_no \n");
			lSBQuery.append(",mb.bank_name,rbb.branch_name,rbp.bank_code,rbp.branch_code,tph.bill_no \n");
			lSBQuery.append(",rbp.cheque_no,rbp.party_amt,rbp.cheque_date,tpr.ledger_no,tpr.page_no from \n");
			lSBQuery.append(" trn_pension_bill_dtls tpb, trn_pension_bill_hdr tph,mst_bank mb, rlt_bank_branch rbb, \n");
			lSBQuery.append(" trn_pension_rqst_hdr tpr,trn_bill_register tbr \n");
			lSBQuery.append(" left outer join rlt_bill_party rbp on tbr.bill_no = rbp.bill_no and rbp.payment_mode in (:paymentMode)\n");

			if ("Auditor".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" ,rlt_auditor_bank rlb \n");
			}
			lSBQuery.append(" where tph.trn_pension_bill_hdr_id=tpb.trn_pension_bill_hdr_id \n");
			lSBQuery.append(" and rbp.bank_code=mb.bank_code and rbp.branch_code=rbb.branch_code \n");
			lSBQuery.append(" and tbr.bill_no=tph.bill_no \n");
			lSBQuery.append(" and tpb.pensioner_code = tpr.pensioner_code \n");
			lSBQuery.append(" and tph.bill_type = :billType and tbr.curr_bill_status in (80,20,30,40,50) \n");

			lSBQuery.append(" and tbr.bill_date between :fromDate and :toDate \n");

			if ("Auditor".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" and rbp.bank_code=rlb.bank_code and rbp.branch_code=rlb.branch_code and \n");
				lSBQuery.append(" rlb.post_id = :audPostId \n");
			}
			if ("Bank".equalsIgnoreCase(lStrAudBankFlag) || "Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" and rbp.bank_code = :bankCode \n");
			}
			if ("Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				lSBQuery.append(" and rbp.branch_code = :branchCode \n");
			}
			lSBQuery.append(" and tbr.location_code = :locationCode \n");
			lSBQuery.append(" group by mb.bank_name,rbb.branch_name,rbp.bank_code,rbp.branch_code,tph.bill_no,tpb.ppo_no,tpb.pensioner_name, \n");
			lSBQuery.append(" tpb.net_amount,rbp.accnt_no,rbp.cheque_no,rbp.party_amt,rbp.cheque_date,tpr.ledger_no,tpr.page_no  ");

			if (!"".equals(lStrOrderBy)) {
				if (gObjRsrcBndle.getString("ORDERBY.VOLUMEPAGENO").equals(lStrOrderBy)) {
					lSBQuery.append(" order by cast(tpr.ledger_no as double),cast(tpr.page_no as double)");
				}
				if (gObjRsrcBndle.getString("ORDERBY.ACCOUNTNO").equals(lStrOrderBy)) {
					lSBQuery.append(" order by cast(rbp.accnt_no as double)");
				}
			}

			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

			Query.setString("billType", lStrBillType);
			Query.setParameterList("paymentMode", lLstPaymentMode);
			Query.setDate("fromDate", lDtFromDate);
			Query.setDate("toDate", lDtToDate);
			Query.setString("locationCode", lStrLocationCode);

			if ("Auditor".equalsIgnoreCase(lStrAudBankFlag)) {
				Query.setString("audPostId", lStrAudPostId);
			}
			if ("Bank".equalsIgnoreCase(lStrAudBankFlag) || "Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				Query.setString("bankCode", lStrBankCode);
			}
			if ("Branch".equalsIgnoreCase(lStrAudBankFlag)) {
				Query.setString("branchCode", lStrBranchCode);
			}
			lLstResultList = Query.list();

			return lLstResultList;

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getSchemeWisePaymentDtls
	 * (java.lang.String, java.lang.String)
	 */
	public List getSchemeWisePaymentDtls(String lStrForMonthYear, String lStrSchemeCode, String lStrLocationCode) throws Exception {

		List lLstResult = null;
		StringBuilder lSBQuery = null;

		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append("select scheme_code, cast(sum(gross_amount) as double), cast(sum(deduction_a + deduction_b) as double), cast(sum(net_amount) as double) \n");
			lSBQuery.append(" from trn_pension_bill_hdr where for_month = :payForMonthYear and location_code= :locationCode \n");
			if (!"-1".equals(lStrSchemeCode)) {
				lSBQuery.append(" and scheme_code = :schemeCode \n");
			}
			lSBQuery.append(" group by scheme_code order by scheme_code");

			// lSBQuery.append("select location_code, cast(sum(gross_amount) as double), cast(sum(deduction_a + deduction_b) as double), cast(sum(net_amount) as double) \n");
			// lSBQuery.append("from trn_pension_bill_hdr where for_month = :payForMonthYear and location_code= :locationCode \n");
			// lSBQuery.append(" group by location_code order by location_code"
			// );

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("payForMonthYear", lStrForMonthYear);
			if (!"-1".equals(lStrSchemeCode)) {
				lQuery.setString("schemeCode", lStrSchemeCode);
			}
			lQuery.setString("locationCode", lStrLocationCode);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getBankWisePaymentDtls
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public List getBankWisePaymentDtls(String lStrForMonthYear, String lStrSchemeCode, String lStrBankCode, String lStrLocationCode) throws Exception {

		List lLstResult = null;
		StringBuilder lSBQuery = null;

		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append("select tph.bank_code,mb.bank_name, cast(sum(tph.gross_amount) as double), cast(sum(tph.deduction_a + deduction_b) as double), cast(sum(tph.net_amount) as double) \n");
			lSBQuery.append("from trn_pension_bill_hdr tph, mst_bank mb where \n");
			lSBQuery.append("tph.bank_code=mb.bank_code \n");
			if (lStrSchemeCode != null && !"-1".equals(lStrSchemeCode) && !"".equals(lStrSchemeCode)) {
				lSBQuery.append(" and tph.scheme_code = :schemeCode \n");
			}
			lSBQuery.append("and tph.for_month = :payForMonthYear and tph.location_code= :locationCode  \n");
			if (lStrBankCode != null && !"-1".equals(lStrBankCode) && !"".equals(lStrBankCode)) {
				lSBQuery.append("and tph.bank_code = :bankCode \n");
			}
			lSBQuery.append("group by tph.bank_code,mb.bank_name order by tph.bank_code,mb.bank_name \n");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("payForMonthYear", lStrForMonthYear);
			if (lStrSchemeCode != null && !"-1".equals(lStrSchemeCode) && !"".equals(lStrSchemeCode)) {
				lQuery.setString("schemeCode", lStrSchemeCode);
			}
			if (lStrBankCode != null && !"-1".equals(lStrBankCode) && !"".equals(lStrBankCode)) {
				lQuery.setString("bankCode", lStrBankCode);
			}
			lQuery.setString("locationCode", lStrLocationCode);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.report.PensionpayQueryDAO#getBranchWisePaymentDtls
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public List getBranchWisePaymentDtls(String lStrForMonthYear, String lStrSchemeCode, String lStrBankCode, String lStrBranchCode, String lStrLocationCode) throws Exception {

		List lLstResult = null;
		StringBuilder lSBQuery = null;

		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append("select tph.branch_code,rbb.branch_name, cast(sum(tph.gross_amount) as double), cast(sum(tph.deduction_a + deduction_b) as double), cast(sum(tph.net_amount) as double) \n");
			lSBQuery.append("from trn_pension_bill_hdr tph, rlt_bank_branch rbb where \n");
			lSBQuery.append("tph.branch_code=rbb.branch_code \n");
			if (lStrSchemeCode != null && !"-1".equals(lStrSchemeCode) && !"".equals(lStrSchemeCode)) {
				lSBQuery.append("and tph.scheme_code= :schemeCode \n");
			}
			lSBQuery.append("and tph.for_month = :payForMonthYear and tph.bank_code = :bankCode and tph.location_code= :locationCode \n");
			if (lStrBranchCode != null && !"-1".equals(lStrBranchCode) && !"".equals(lStrBranchCode)) {
				lSBQuery.append("and tph.branch_code = :branchCode \n");
			}
			lSBQuery.append("group by tph.branch_code,rbb.branch_name order by tph.branch_code,rbb.branch_name \n");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			if (lStrSchemeCode != null && !"-1".equals(lStrSchemeCode) && !"".equals(lStrSchemeCode)) {
				lQuery.setString("schemeCode", lStrSchemeCode);
			}
			lQuery.setString("payForMonthYear", lStrForMonthYear);
			lQuery.setString("bankCode", lStrBankCode);
			if (lStrBranchCode != null && !"-1".equals(lStrBranchCode) && !"".equals(lStrBranchCode)) {
				lQuery.setString("branchCode", lStrBranchCode);
			}
			lQuery.setString("locationCode", lStrLocationCode);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
			throw e;
		}
		return lLstResult;
	}

	public List getAGFirstPayStatement(Date lDtFromVoucherDate, Date lDtToVoucherDate, Long lLngTreasuryCode, String lStrPPONo) throws Exception {

		List lLstResult = null;
		StringBuilder lSBQuery = null;

		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append("select pbd.ppo_no,pbd.ppo_no,pbd.PENSIONER_NAME,prh.COMMENCEMENT_DATE,prh.PPO_AGOUTWARD_NO,prh.PPO_AGOUTWARD_DATE,tbr.VOUCHER_NO \n");
			lSBQuery.append(",tbr.VOUCHER_DATE,pbd.PENSION_AMOUNT,pbd.PENSN_CUT_AMOUNT,pbd.DP_AMOUNT,pbd.IR1_AMOUNT,pbd.IR2_AMOUNT,pbd.IR3_AMOUNT,pbd.TI_AMOUNT \n");
			lSBQuery.append(",pbd.CVP_MONTH_AMOUNT,pbd.TOTAL_ARREAR_AMT,pbd.GROSS_AMOUNT,pbd.RECOVERY_AMOUNT,pbd.NET_AMOUNT, \n");
			lSBQuery.append("pbd.pensioner_code,crd.FROM_DATE,crd.TO_DATE,crd.AMOUNT \n");
			lSBQuery.append("FROM trn_bill_register tbr \n");
			lSBQuery.append("join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no \n");
			lSBQuery.append("join trn_pension_bill_dtls pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID \n");
			lSBQuery.append("join trn_pension_rqst_hdr prh on prh.pensioner_code = pbd.pensioner_code \n");
			lSBQuery.append("left outer join TRN_CVP_RESTORATION_DTLS crd on crd.pensioner_code = pbd.pensioner_code \n");
			lSBQuery.append("WHERE tbr.TC_BILL = :FirstPay \n");
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lSBQuery.append("and tbr.VOUCHER_DATE  between :FromVoucherDate and :ToVoucherDate \n");
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lSBQuery.append("and pbd.ppo_no = :PPONo \n");
			}
			lSBQuery.append("and tbr.SUBJECT_ID = :SubjectId \n");
			lSBQuery.append("and tbr.location_code = :TreasuryCode \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS in (20,30,40,50,70,80) \n");
			lSBQuery.append("order by tbr.VOUCHER_DATE \n");
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("FirstPay", bundleConst.getString("PPMT.FIRSTPAY"));
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lQuery.setDate("FromVoucherDate", lDtFromVoucherDate);
				lQuery.setDate("ToVoucherDate", lDtToVoucherDate);
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lQuery.setParameter("PPONo", lStrPPONo.trim().toUpperCase());
			}
			lQuery.setLong("TreasuryCode", lLngTreasuryCode);
			lQuery.setInteger("SubjectId", 9);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	public List getCVPPaymentDtls(Date lDtFromVoucherDate, Date lDtToVoucherDate, Long lLngTreasuryCode, String lStrPPONo) throws Exception {

		List lLstResult = null;
		StringBuilder lSBQuery = null;

		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append("select pbd.ppo_no,pbd.ppo_no,pbd.PENSIONER_NAME,prh.COMMENCEMENT_DATE,sup.CVP_ORDER_NO,sup.CVP_ORDER_DATE,tbr.BILL_NET_AMOUNT  \n");
			lSBQuery.append(",tbr.voucher_no,tbr.voucher_date,rbp.PARTY_NAME FROM trn_bill_register tbr \n");
			lSBQuery.append("left outer join  TRN_PENSION_SUPPLY_BILL_DTLS sup on sup.bill_no = tbr.bill_no  \n");
			lSBQuery.append("join trn_pension_bill_hdr pbh on pbh.bill_no = tbr.bill_no \n");
			lSBQuery.append("join TRN_PENSION_BILL_DTLS pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID \n");
			lSBQuery.append("join RLT_BILL_PARTY rbp on rbp.bill_no = tbr.bill_no \n");
			lSBQuery.append("join trn_pension_rqst_hdr prh on prh.pensioner_code = pbd.pensioner_code \n");
			lSBQuery.append("where (tbr.SUBJECT_ID = 11 OR TBR.SUBJECT_ID = 45 ) \n");
			lSBQuery.append("and (sup.BILL_TYPE = 'CVP' or sup.BILL_TYPE is null) \n");
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lSBQuery.append("and tbr.VOUCHER_DATE  between :FromVoucherDate and :ToVoucherDate \n");
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lSBQuery.append("and pbd.ppo_no = :PPONo \n");
			}
			lSBQuery.append("and tbr.location_code = :TreasuryCode \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS in (20,30,40,50,70,80) \n");
			lSBQuery.append("order by tbr.voucher_date,tbr.voucher_no \n");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lQuery.setDate("FromVoucherDate", lDtFromVoucherDate);
				lQuery.setDate("ToVoucherDate", lDtToVoucherDate);
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lQuery.setParameter("PPONo", lStrPPONo.trim().toUpperCase());
			}
			lQuery.setLong("TreasuryCode", lLngTreasuryCode);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	public List getDCRGPaymentDtls(Date lDtFromVoucherDate, Date lDtToVoucherDate, Long lLngTreasuryCode, String lStrPPONo) throws Exception {

		List lLstResult = null;
		StringBuilder lSBQuery = null;

		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append("select tbr.bill_no,tbr.bill_no,pbd.ppo_no,pbd.PENSIONER_NAME,prh.COMMENCEMENT_DATE,sup.GPO_NO,sup.GPO_DATE,tbr.BILL_GROSS_AMOUNT,tbr.DEDUCTION_A  \n");
			lSBQuery.append(",tbr.BILL_NET_AMOUNT,tbr.voucher_no,tbr.voucher_date,rbp.PARTY_NAME,rbp.PARTY_AMT from trn_bill_register tbr \n");
			lSBQuery.append("left outer join  TRN_PENSION_SUPPLY_BILL_DTLS sup on sup.bill_no = tbr.bill_no  \n");
			lSBQuery.append("join trn_pension_bill_hdr pbh on pbh.bill_no = tbr.bill_no \n");
			lSBQuery.append("join TRN_PENSION_BILL_DTLS pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID \n");
			lSBQuery.append("join RLT_BILL_PARTY rbp on rbp.bill_no = tbr.bill_no \n");
			lSBQuery.append("join trn_pension_rqst_hdr prh on prh.pensioner_code = pbd.pensioner_code \n");
			lSBQuery.append("where (tbr.SUBJECT_ID = 10 OR TBR.SUBJECT_ID = 45 ) \n");
			lSBQuery.append("and (sup.BILL_TYPE = 'DCRG' or sup.BILL_TYPE is null) \n");
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lSBQuery.append("and tbr.VOUCHER_DATE  between :FromVoucherDate and :ToVoucherDate \n");
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lSBQuery.append("and pbd.ppo_no = :PPONo \n");
			}
			lSBQuery.append("and tbr.location_code = :TreasuryCode \n");
			lSBQuery.append("and tbr.CURR_BILL_STATUS in (20,30,40,50,70,80) \n");
			lSBQuery.append("order by tbr.voucher_date,tbr.voucher_no \n");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lQuery.setDate("FromVoucherDate", lDtFromVoucherDate);
				lQuery.setDate("ToVoucherDate", lDtToVoucherDate);
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lQuery.setParameter("PPONo", lStrPPONo.trim().toUpperCase());
			}
			lQuery.setLong("TreasuryCode", lLngTreasuryCode);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	public List<ComboValuesVO> getVoucherNo(Integer lIntForMonth, String lStrTreasuryCode) throws Exception {

		ArrayList<ComboValuesVO> lLstVoucherNo = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Integer lIntVoucherNo = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append(" select DISTINCT(tbr.voucher_no) from trn_bill_register tbr join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no where ");
			lSBQuery.append(" pbh.FOR_MONTH = :forMonth and tbr.location_code = :treasuryCode and tbr.TC_BILL = :billType");
			lSBQuery.append(" and tbr.SUBJECT_ID = :subjectId and tbr.voucher_no is not null");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("forMonth", lIntForMonth);
			lQuery.setParameter("treasuryCode", lStrTreasuryCode);
			lQuery.setParameter("billType", gObjRsrcBndle.getString("PPMT.MONTHLY"));
			lQuery.setParameter("subjectId", Integer.parseInt(PensionConstants.Monthly_Id));

			List resultList = lQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					lIntVoucherNo = (Integer) it.next();
					cmbVO.setId(lIntVoucherNo.toString());
					cmbVO.setDesc(lIntVoucherNo.toString());
					lLstVoucherNo.add(cmbVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstVoucherNo;
	}

	public List<ComboValuesVO> getYearListForMonthlyPenReport(String lStrLangId) throws Exception {

		List<String> lLstResult = new ArrayList();
		ComboValuesVO lObjComboValueVO = null;
		List<ComboValuesVO> lLstYears = new ArrayList<ComboValuesVO>();
		String lStrYearCode = null;
		try {
			StringBuffer lSBQuery = new StringBuffer("select finYearCode from SgvcFinYearMst where langId = :lLangId order by finYearCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLangId", lStrLangId);
			lLstResult = lQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				Iterator it = lLstResult.iterator();
				while (it.hasNext()) {
					lObjComboValueVO = new ComboValuesVO();
					lStrYearCode = (String) it.next();
					lObjComboValueVO.setId(lStrYearCode);
					lObjComboValueVO.setDesc(lStrYearCode);
					lLstYears.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lLstYears;
	}

	public List<Object> getMonthlyPenDtlsReport(Integer lIntForMonth, String lStrLocCode, String lStrVoucherNo, String lStrLangId) throws Exception {

		List<Object> lLstMonthlyPenDtls = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append(" select tbr.location_code,clm.LOC_NAME,tbr.voucher_no,tbr.voucher_date,tbr.scheme_no,msc.DEMAND_CODE,msc.MAJOR_HEAD");
			lSBQuery.append(" ,msc.SUB_MAJOR_HEAD,msc.MINOR_HEAD,msc.SUB_HEAD,pbh.FOR_MONTH,cast(sum(tbr.BILL_GROSS_AMOUNT) as bigint) as gross");
			lSBQuery.append(" ,cast(sum(tbr.DEDUCTION_A) as  bigint) as deduction,cast(sum(tbr.BILL_NET_AMOUNT) as bigint) as net,msc.CHARGED, msc.PLAN");
			lSBQuery.append(" From trn_bill_register tbr join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no");
			lSBQuery.append(" join mst_scheme msc on msc.scheme_code = tbr.SCHEME_NO join CMN_LOCATION_MST clm on tbr.LOCATION_CODE = clm.LOC_ID");
			lSBQuery.append(" where pbh.FOR_MONTH = :forMonth and tbr.location_code = :locCode and tbr.voucher_no = :voucherNo");
			lSBQuery.append(" and tbr.TC_BILL = :billType and tbr.SUBJECT_ID = :subjectId and msc.LANG_ID = :langId");
			lSBQuery.append(" group by tbr.location_code,clm.LOC_NAME,msc.DEMAND_CODE,msc.MAJOR_HEAD,msc.SUB_MAJOR_HEAD,msc.MINOR_HEAD,msc.SUB_HEAD");
			lSBQuery.append(" ,tbr.scheme_no,tbr.voucher_no,tbr.voucher_date,pbh.for_month,msc.CHARGED, msc.PLAN");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("forMonth", lIntForMonth);
			lQuery.setParameter("locCode", lStrLocCode);
			lQuery.setParameter("billType", gObjRsrcBndle.getString("PPMT.MONTHLY"));
			lQuery.setParameter("subjectId", Integer.parseInt(PensionConstants.Monthly_Id));
			lQuery.setParameter("voucherNo", lStrVoucherNo);
			lQuery.setParameter("langId", lStrLangId);

			lLstMonthlyPenDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstMonthlyPenDtls;
	}

	public List<Object> getMonthlyPenAllocationReport(Integer lIntForMonth, String lStrLocCode, String lStrVoucherNo, String lStrLangId) throws Exception {

		List<Object> lLstMonthlyPenDtls = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append(" select tbr.location_code,clm.LOC_NAME,tbr.voucher_no,tbr.voucher_date,cast(sum(pbd.ALLCATION_BF_1_4_36) as bigint),");
			lSBQuery.append(" cast(sum(pbd.ALLCATION_BF_1_11_56)as bigint),cast(sum(pbd.ALLCATION_AF_1_11_56)as bigint),cast(sum(pbd.ALLCATION_AF_1_05_60)as bigint),");
			lSBQuery.append(" cast(sum(pbd.ALLCATION_AF_ZP) as bigint) From trn_bill_register tbr join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no");
			lSBQuery.append(" join TRN_PENSION_BILL_DTLS pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID");
			lSBQuery.append(" join CMN_LOCATION_MST clm on tbr.LOCATION_CODE = clm.LOC_ID");
			lSBQuery.append(" where pbh.FOR_MONTH = :forMonth and tbr.location_code = :locCode and tbr.voucher_no = :voucherNo");
			lSBQuery.append(" and tbr.TC_BILL = :billType and tbr.SUBJECT_ID = :subjectId and clm.LANG_ID = :langId");
			lSBQuery.append(" group by tbr.voucher_no,tbr.location_code,clm.LOC_NAME,tbr.voucher_date");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("forMonth", lIntForMonth);
			lQuery.setParameter("locCode", lStrLocCode);
			lQuery.setParameter("billType", gObjRsrcBndle.getString("PPMT.MONTHLY"));
			lQuery.setParameter("subjectId", Integer.parseInt(PensionConstants.Monthly_Id));
			lQuery.setParameter("voucherNo", lStrVoucherNo);
			lQuery.setParameter("langId", lStrLangId);

			lLstMonthlyPenDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstMonthlyPenDtls;
	}

	public List<Object> getMonthlyPenRecoveryReport(Integer lIntForMonth, String lStrLocCode, String lStrVoucherNo, String lStrLangId) throws Exception {

		List<Object> lLstMonthlyPenRecovery = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append(" select tbr.LOCATION_CODE,clm.LOC_NAME,tbr.voucher_no,tbr.voucher_date,prd.scheme_code,");
			lSBQuery.append(" msc.DEMAND_CODE,msc.MAJOR_HEAD,msc.SUB_MAJOR_HEAD,msc.MINOR_HEAD,msc.SUB_HEAD,cast(SUM(prd.AMOUNT) as bigint) as DeductionAmount");
			lSBQuery.append(" ,msc.CHARGED, msc.PLAN From trn_bill_register tbr join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no");
			lSBQuery.append(" join TRN_MONTHLY_PENSION_RECOVERY_DTLS prd on prd.bill_no = tbr.bill_no");
			lSBQuery.append(" join mst_scheme msc on msc.scheme_code = prd.SCHEME_CODE join CMN_LOCATION_MST clm on tbr.LOCATION_CODE = clm.LOC_ID");
			lSBQuery.append(" where pbh.FOR_MONTH = :forMonth and tbr.location_code = :locCode and tbr.voucher_no = :voucherNo");
			lSBQuery.append(" and tbr.TC_BILL = :billType and tbr.SUBJECT_ID = :subjectId and clm.LANG_ID = :langId");
			lSBQuery.append(" group by  prd.scheme_code,tbr.LOCATION_CODE,clm.LOC_NAME,tbr.voucher_no,tbr.voucher_date,");
			lSBQuery.append(" msc.DEMAND_CODE,msc.MAJOR_HEAD,msc.SUB_MAJOR_HEAD,msc.MINOR_HEAD,msc.SUB_HEAD,msc.CHARGED, msc.PLAN");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("forMonth", lIntForMonth);
			lQuery.setParameter("locCode", lStrLocCode);
			lQuery.setParameter("billType", gObjRsrcBndle.getString("PPMT.MONTHLY"));
			lQuery.setParameter("subjectId", Integer.parseInt(PensionConstants.Monthly_Id));
			lQuery.setParameter("voucherNo", lStrVoucherNo);
			lQuery.setParameter("langId", lStrLangId);

			lLstMonthlyPenRecovery = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstMonthlyPenRecovery;
	}

	public List getChangeStatementData(Long lngAudPostId, Integer lIntForMonth, String lStrLocationCode, String lStrLangId) throws Exception {

		List<Object> lLstMonthlyPenRecovery = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select mb.bank_name,rbb.branch_name,mcr.status,NVL(oem.EMP_FNAME,' ') || ' ' || NVL(oem.EMP_MNAME,' ') || ' ' || NVL(oem.EMP_LNAME,' ') as empName from RLT_BANK_BRANCH rbb ");
			lSBQuery.append(" join MST_BANK mb on rbb.bank_code = mb.bank_code and mb.LANG_ID = :langId ");
			lSBQuery.append(" left outer join RLT_AUDITOR_BANK rab on rbb.bank_code = rab.bank_code and rbb.branch_code = rab.branch_code");
			lSBQuery.append(" left outer join TRN_MONTHLY_CHANGE_RQST mcr on rbb.branch_code = mcr.branch_code and rbb.bank_code = mcr.bank_code ");
			lSBQuery.append(" and  mcr.status not in ('Discarded','Rejected') and mcr.FOR_MONTH = :forMonth ");
			lSBQuery.append(" left outer join ORG_USERPOST_RLT upr on upr.post_id = rab.post_id");
			lSBQuery.append(" left outer join ORG_EMP_MST oem on oem.user_id = upr.user_id and oem.LANG_ID = :langId");
			lSBQuery.append(" where rbb.location_code = :locCode ");
			if (lngAudPostId != null) {
				lSBQuery.append(" and rab.POST_ID = :postId ");
			}
			lSBQuery.append(" order by empName,mb.bank_name,rbb.branch_name,mcr.status");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("forMonth", lIntForMonth);
			lQuery.setParameter("locCode", lStrLocationCode);
			if (lngAudPostId != null) {
				lQuery.setParameter("postId", lngAudPostId);
			}
			lQuery.setParameter("langId", lStrLangId);

			lLstMonthlyPenRecovery = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstMonthlyPenRecovery;
	}

	public List getPensionerACNumber(String lStrLocationCode, String lStrBankCode, String lStrBranchCode, Long lLngLangId) throws Exception {

		List<Object> lLstPensionerACNumber = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select prh.ppoNo,mph.firstName,mb.bankName,rbb.branchName,mpd.accountNo,prh.pensionerCode From MstPensionerHdr mph, ");
			lSBQuery.append(" MstPensionerDtls mpd ,TrnPensionRqstHdr prh ,MstBank mb,RltBankBranch rbb WHERE mph.pensionerCode = mpd.pensionerCode ");
			lSBQuery.append(" and mph.locationCode = mpd.locationCode and mph.locationCode = :locCode and mpd.bankCode = :bankCode ");
			lSBQuery.append(" and mpd.branchCode = :branchCode and prh.pensionerCode = mpd.pensionerCode  and prh.seenFlag = 'Y' ");
			lSBQuery.append(" and prh.status = 'Continue' and mb.bankCode = mpd.bankCode and mb.langId = :langId ");
			lSBQuery.append(" and rbb.branchCode = mpd.branchCode and rbb.locationCode = :locCode and rbb.langId = :langId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("bankCode", lStrBankCode);
			lQuery.setParameter("branchCode", lStrBranchCode);
			lQuery.setParameter("locCode", lStrLocationCode);
			lQuery.setParameter("langId", lLngLangId);

			lLstPensionerACNumber = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstPensionerACNumber;
	}

	
	public List getRevisedAgCases(Date lDtAcceptFromDate, Date lDtAcceptToDate,
			String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select tpr.ag_ppo_no,tpr.ag_pensioner_name,tpr.ifms_ppo_no,tpr.ifms_pensioner_name, \n");
			lSBQuery.append(" tpr.ppo_accept_date,tpr.cvp_order_no,tpr.cvp_amount,tpr.gpo_order_no,tpr.dcrg_amount, \n");
			lSBQuery.append(" concat(concat(concat(concat(emp.emp_fname,' '),emp.emp_mname),' '),emp.emp_lname) from  \n");
			lSBQuery.append(" trn_pensioner_rivision_dtls tpr join trn_pension_rqst_hdr tph on tpr.pensioner_code = tph.pensioner_code  \n");
			lSBQuery.append(" LEFT OUTER JOIN org_userpost_rlt usrpst ON usrpst.post_id = tph.case_owner  \n");
			lSBQuery.append(" LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id  \n");
			lSBQuery.append(" where tpr.ppo_accept_date is not null and tpr.ppo_accept_date >= :fromDate and tpr.ppo_accept_date <= :toDate \n"); 
			lSBQuery.append(" and tpr.revision_flag = 'AG' and tpr.location_code = :locationCode  \n");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("fromDate", lDtAcceptFromDate);
			
			Calendar lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(lDtAcceptToDate);
			lObjCalendar.add(Calendar.DATE, 1);
			lDtAcceptToDate =lObjCalendar.getTime();
			lQuery.setParameter("toDate", lDtAcceptToDate);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	public List getPensionDtlsReport(Date lDtFromVoucherDate,Date lDtToVoucherDate,Long lLngTreasuryCode, String lStrPPONo, String lStrReportType, String lStrLangId) {
		List<Object> lLstMonthlyPenDtls = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append(" select tbr.location_code,clm.LOC_NAME,tbr.voucher_no,tbr.voucher_date,tbr.scheme_no,msc.DEMAND_CODE,msc.MAJOR_HEAD");
			lSBQuery.append(" ,msc.SUB_MAJOR_HEAD,msc.MINOR_HEAD,msc.SUB_HEAD,pbh.FOR_MONTH,tbr.BILL_GROSS_AMOUNT as gross");
			lSBQuery.append(" ,tbr.DEDUCTION_A as deduction,tbr.BILL_NET_AMOUNT as net,msc.CHARGED, msc.PLAN, pbd.PPO_NO");
			lSBQuery.append(" From trn_bill_register tbr join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no");
			lSBQuery.append(" join TRN_PENSION_BILL_DTLS pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID ");
			lSBQuery.append(" join mst_scheme msc on msc.scheme_code = tbr.SCHEME_NO join CMN_LOCATION_MST clm on tbr.LOCATION_CODE = clm.LOC_ID");			
			
			lSBQuery.append(" WHERE tbr.CURR_BILL_STATUS in (20,30,40,50,70,80) \n");
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lSBQuery.append("and tbr.VOUCHER_DATE  between :FromVoucherDate and :ToVoucherDate \n");
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lSBQuery.append("and tbr.ppo_no = :PPONo \n");
			}
			lSBQuery.append("and tbr.SUBJECT_ID = :SubjectId \n");
			lSBQuery.append("and tbr.location_code = :TreasuryCode \n");			
			lSBQuery.append("order by tbr.VOUCHER_DATE \n");
			
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lQuery.setDate("FromVoucherDate", lDtFromVoucherDate);
				lQuery.setDate("ToVoucherDate", lDtToVoucherDate);
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lQuery.setParameter("PPONo", lStrPPONo.trim().toUpperCase());
			}
			lQuery.setLong("TreasuryCode", lLngTreasuryCode);
			if(lStrReportType.equals("F")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPEPENSION));				
			}else if(lStrReportType.equals("C")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPECVP));				
			}else if(lStrReportType.equals("D")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPEDCRG));				
			}
			lLstMonthlyPenDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstMonthlyPenDtls;
		
	}

	
	public List getPensionRecoveryReport(Date lDtFromVoucherDate,Date lDtToVoucherDate,Long lLngTreasuryCode, String lStrPPONo, String lStrReportType, String lStrLangId) {
		List<Object> lLstMonthlyPenDtls = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append(" select tbr.LOCATION_CODE,clm.LOC_NAME,tbr.voucher_no,tbr.voucher_date,prd.scheme_code,");
			lSBQuery.append(" msc.DEMAND_CODE,msc.MAJOR_HEAD,msc.SUB_MAJOR_HEAD,msc.MINOR_HEAD,msc.SUB_HEAD,prd.AMOUNT as DeductionAmount");
			lSBQuery.append(" ,msc.CHARGED, msc.PLAN, pbd.PPO_NO From trn_bill_register tbr join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no");
			lSBQuery.append(" join TRN_MONTHLY_PENSION_RECOVERY_DTLS prd on prd.bill_no = tbr.bill_no");
			lSBQuery.append(" join TRN_PENSION_BILL_DTLS pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID ");
			lSBQuery.append(" join mst_scheme msc on msc.scheme_code = prd.SCHEME_CODE join CMN_LOCATION_MST clm on tbr.LOCATION_CODE = clm.LOC_ID");
			
			lSBQuery.append(" WHERE tbr.CURR_BILL_STATUS in (20,30,40,50,70,80) \n");
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lSBQuery.append("and tbr.VOUCHER_DATE  between :FromVoucherDate and :ToVoucherDate \n");
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lSBQuery.append("and tbr.ppo_no = :PPONo \n");
			}
			lSBQuery.append("and tbr.SUBJECT_ID = :SubjectId \n");
			lSBQuery.append("and tbr.location_code = :TreasuryCode \n");			
			lSBQuery.append("order by tbr.VOUCHER_DATE \n");
			
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lQuery.setDate("FromVoucherDate", lDtFromVoucherDate);
				lQuery.setDate("ToVoucherDate", lDtToVoucherDate);
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lQuery.setParameter("PPONo", lStrPPONo.trim().toUpperCase());
			}
			lQuery.setLong("TreasuryCode", lLngTreasuryCode);
			if(lStrReportType.equals("F")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPEPENSION));				
			}else if(lStrReportType.equals("C")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPECVP));
			}else if(lStrReportType.equals("D")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPEDCRG));				
			}
			lLstMonthlyPenDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstMonthlyPenDtls;
		
		
	}
	
	public List getPensionAllocationReport(Date lDtFromVoucherDate,Date lDtToVoucherDate,Long lLngTreasuryCode, String lStrPPONo, String lStrReportType, String lStrLangId) {
		List<Object> lLstMonthlyPenDtls = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append(" select tbr.location_code,clm.LOC_NAME,tbr.voucher_no,tbr.voucher_date,pbd.ALLCATION_BF_1_4_36,");
			lSBQuery.append(" pbd.ALLCATION_BF_1_11_56,pbd.ALLCATION_AF_1_11_56,pbd.ALLCATION_AF_1_05_60,");
			lSBQuery.append(" pbd.ALLCATION_AF_ZP, pbd.PPO_NO From trn_bill_register tbr join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no");
			lSBQuery.append(" join TRN_PENSION_BILL_DTLS pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID");
			lSBQuery.append(" join CMN_LOCATION_MST clm on tbr.LOCATION_CODE = clm.LOC_ID");
			
			lSBQuery.append(" WHERE  tbr.CURR_BILL_STATUS in (20,30,40,50,70,80)");
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lSBQuery.append("and tbr.VOUCHER_DATE  between :FromVoucherDate and :ToVoucherDate \n");
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lSBQuery.append("and tbr.ppo_no = :PPONo \n");
			}
			lSBQuery.append("and tbr.SUBJECT_ID = :SubjectId \n");
			lSBQuery.append("and tbr.location_code = :TreasuryCode \n");			
			lSBQuery.append("order by tbr.VOUCHER_DATE \n");
			
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			if (lDtFromVoucherDate != null && lDtToVoucherDate != null) {
				lQuery.setDate("FromVoucherDate", lDtFromVoucherDate);
				lQuery.setDate("ToVoucherDate", lDtToVoucherDate);
			}
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				lQuery.setParameter("PPONo", lStrPPONo.trim().toUpperCase());
			}
			lQuery.setLong("TreasuryCode", lLngTreasuryCode);
			if(lStrReportType.equals("F")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPEPENSION));				
			}else if(lStrReportType.equals("C")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPECVP));			
			}else if(lStrReportType.equals("D")){
				lQuery.setInteger("SubjectId", Integer.parseInt(PensionConstants.BILLTYPEDCRG));				
			}
			lLstMonthlyPenDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstMonthlyPenDtls;
		
	}
	
	public List getRegisterOfPpoReceived(Date lDtInwardFromDate, Date lDtInwardToDate,String lStrPpoNo, String lStrLocCode,List<String> lLstCaseStatus,String lStrCaseStatus) throws Exception
	{
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select tph.ppoNo,mph.firstName,tph.basicPensionAmount,tph.ppoInwardDate,tph.commensionDate,tph.caseStatus from \n");
			lSBQuery.append(" MstPensionerHdr mph,TrnPensionRqstHdr tph \n");
			lSBQuery.append(" where mph.pensionerCode = tph.pensionerCode \n");
			if(lStrPpoNo != null && !"".equals(lStrPpoNo))
			{
				lSBQuery.append(" and tph.ppoNo = :ppoNo \n");
			}
			lSBQuery.append(" and tph.ppoInwardDate >= :fromDate and tph.ppoInwardDate <= :toDate and mph.locationCode = :locationCode \n");
			if(lStrCaseStatus != null && !"".equals(lStrCaseStatus) && !"-1".equals(lStrCaseStatus))
			{
				if(gObjRsrcBndle.getString("STATFLG.NONIDENTIFIED").equals(lStrCaseStatus))
				{
					lSBQuery.append(" and tph.caseStatus in (:caseStatusList) ");
				}
				else
				{
					lSBQuery.append(" and tph.caseStatus not in (:caseStatusList) ");
				}
			}
			lSBQuery.append(" order by tph.ppoInwardDate,mph.firstName ");
						
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
	
			if(lStrPpoNo != null && !"".equals(lStrPpoNo))
			{
				lQuery.setString("ppoNo", lStrPpoNo.toUpperCase());
			}
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("fromDate", lDtInwardFromDate);
			if(lStrCaseStatus != null && !"".equals(lStrCaseStatus) && !"-1".equals(lStrCaseStatus))
			{
				lQuery.setParameterList("caseStatusList", lLstCaseStatus);
			}
			Calendar lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(lDtInwardToDate);
			lObjCalendar.add(Calendar.DATE, 1);
			lDtInwardToDate =lObjCalendar.getTime();
			lQuery.setParameter("toDate", lDtInwardToDate);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}
	
	public List getRegisterOfGratuityOrderReceived(Date lDtOrderFromDate, Date lDtOrderToDate,List<Short> lLstBillStatus, String lStrLocCode) throws Exception
	{
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select mph.first_name,hpd.order_no,hpd.order_date,hpd.name_of_payee,clm.loc_name,hpd.paid_amount, \n");
			lSBQuery.append(" hpd.total_recovery_amount,hpd.voucher_no,hpd.voucher_date \n");
		    lSBQuery.append(" from mst_pensioner_hdr mph join hst_pnsnpmnt_dcrg_dtls hpd on mph.pensioner_code = hpd.pensioner_code \n"); 
		    lSBQuery.append(" left outer join trn_bill_register tbr on hpd.bill_no = tbr.bill_no and tbr.curr_bill_status not in (:billStatusList) \n"); 
		    lSBQuery.append(" left outer join cmn_location_mst clm on hpd.trsry_for_payment = clm.loc_id \n");
		    lSBQuery.append(" where hpd.order_date >= :fromDate and hpd.order_date <= :toDate and hpd.location_code = :locationCode \n");
		    lSBQuery.append(" order by hpd.order_date,mph.first_name ");
						
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("fromDate", lDtOrderFromDate);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			
//			Calendar lObjCalendar = Calendar.getInstance();
//			lObjCalendar.setTime(lDtOrderToDate);
//			lObjCalendar.add(Calendar.DATE, 1);
//			lDtOrderToDate =lObjCalendar.getTime();
			lQuery.setParameter("toDate", lDtOrderToDate);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}
	
	public List getRegisterOfCvpOrderReceived(Date lDtOrderFromDate, Date lDtOrderToDate,List<Short> lLstBillStatus, String lStrLocCode) throws Exception
	{
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select mph.first_name,hcd.order_no,hcd.order_date,hcd.name_of_payee,clm.loc_name,hcd.payment_amount, \n"); 
			lSBQuery.append(" hcd.voucher_no,hcd.voucher_date \n"); 
			lSBQuery.append(" from mst_pensioner_hdr mph join hst_commutation_dtls hcd on mph.pensioner_code = hcd.pensioner_code \n"); 
			lSBQuery.append(" left outer join trn_bill_register tbr on hcd.bill_no = tbr.bill_no and tbr.curr_bill_status not in (:billStatusList) \n"); 
			lSBQuery.append(" left outer join cmn_location_mst clm on hcd.trsry_for_payment = clm.loc_id \n");
			lSBQuery.append(" where hcd.order_date >= :fromDate and hcd.order_date <= :toDate and hcd.location_code = :locationCode \n");
			lSBQuery.append(" order by hcd.order_date,mph.first_name");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setParameter("fromDate", lDtOrderFromDate);
			lQuery.setParameter("toDate", lDtOrderToDate);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}
	
	public List getChangeInTrsryData(Date lDtRequestGenFromDate,Date lDtRequestGenToDate,String lStrLocCode, Long lStrLangId) throws Exception
	{
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select tpt.APPLN_RECEIVE_DATE,mph.FIRST_NAME,mph.PNSNR_ADDR1,mph.PNSNR_ADDR2,mph.PNSNR_ADDR_TOWN, \n");
			lSBQuery.append(" csm.STATE_NAME,cdm.DISTRICT_NAME,mph.PIN_CODE,tpt.ppo_no,mb.BANK_NAME,mpd.ACOUNT_NO,tpt.LAST_PAY_AMOUNT, \n");
			lSBQuery.append(" tpt.LAST_PAY_MONTHYEAR,clm1.LOC_NAME as loc1,clm2.LOC_NAME as loc2,mph.DATE_OF_DEATH,mpf.NAME from  \n");
			lSBQuery.append(" mst_pensioner_dtls mpd, TRN_PENSION_TRANSFER_DTLS tpt, mst_bank mb, cmn_location_mst clm1, \n"); 
			lSBQuery.append(" cmn_location_mst clm2, mst_pensioner_hdr mph \n");
			lSBQuery.append(" left outer join mst_pensioner_family_dtls mpf on mph.pensioner_code = mpf.pensioner_code and mpf.percentage = 100 \n");
			lSBQuery.append(" left outer join CMN_STATE_MST csm on mph.STATE_CODE = csm.STATE_CODE and csm.LANG_ID = 1 \n");
			lSBQuery.append(" left outer join CMN_DISTRICT_MST cdm on mph.DISTRICT_CODE = cdm.DISTRICT_CODE and cdm.LANG_ID = 1 \n");
			lSBQuery.append(" where mph.pensioner_code = mpd.pensioner_code \n");
			lSBQuery.append(" and mph.pensioner_code = tpt.PENSIONER_CODE \n");
			lSBQuery.append(" and mpd.bank_code = mb.bank_code \n");
			lSBQuery.append(" and tpt.FROM_LOCATION = clm1.LOCATION_CODE \n");
			lSBQuery.append(" and tpt.TO_LOCATION = clm2.LOCATION_CODE \n");
			lSBQuery.append(" and tpt.STATUS = :status \n");
			lSBQuery.append(" and tpt.TRANSFER_DATE >= :fromDate and tpt.TRANSFER_DATE <= :toDate and mph.location_code = :locationCode \n");
				
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("status", gObjRsrcBndle.getString("STATFLG.APPROVED"));
			lQuery.setParameter("fromDate", lDtRequestGenFromDate);
			
			Calendar lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(lDtRequestGenToDate);
			lObjCalendar.add(Calendar.DATE, 1);
			lDtRequestGenToDate =lObjCalendar.getTime();
			lQuery.setParameter("toDate", lDtRequestGenToDate);
			lQuery.setParameter("locationCode", lStrLocCode);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getAuditRegisterRptPpoList(Map displayTag,String lStrFinYearId,Date lDtFinYearFromDate,Date lDtFinYearToDate,
			String lStrLocCode, String lStrPostId) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select tpr.pensioner_code,tpr.ppo_no,mph.first_name,mph.date_of_death,mpf.name from  \n");
			lSBQuery.append(" trn_pension_rqst_hdr tpr join mst_pensioner_hdr mph on tpr.pensioner_code = mph.pensioner_code \n");
			lSBQuery.append(" left outer join mst_pensioner_family_dtls mpf on mph.pensioner_code = mpf.pensioner_code and mpf.percentage = 100 \n");
			lSBQuery.append(" where tpr.case_owner = :postId and tpr.location_code = :locationCode \n");  
				
			if("24".equals(lStrFinYearId))
			{
				lSBQuery.append(" and ((tpr.ppo_inward_date between :fromDate and :toDate) or (tpr.ppo_inward_date is null)) \n"); 
			}
			else
			{
				lSBQuery.append(" and tpr.ppo_inward_date between :fromDate and :toDate"); 
			}
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);
			
			lQuery.setParameter("postId", lStrPostId);
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("fromDate", lDtFinYearFromDate);
			lQuery.setParameter("toDate", lDtFinYearToDate);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	public Integer getAuditRegisterRptPpoCount(String lStrFinYearId,Date lDtFinYearFromDate,Date lDtFinYearToDate,
			String lStrLocCode, String lStrPostId) throws Exception {
		// TODO Auto-generated method stub
		Integer lIntCount = 0;
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select count(tpr.pensioner_code) from  \n");
			lSBQuery.append(" trn_pension_rqst_hdr tpr join mst_pensioner_hdr mph on tpr.pensioner_code = mph.pensioner_code \n");
			lSBQuery.append(" left outer join mst_pensioner_family_dtls mpf on mph.pensioner_code = mpf.pensioner_code  and mpf.percentage = 100 \n");
			lSBQuery.append(" where tpr.case_owner = :postId and tpr.location_code = :locationCode \n");
			
			if("24".equals(lStrFinYearId))
			{
				lSBQuery.append(" and ((tpr.ppo_inward_date between :fromDate and :toDate) or (tpr.ppo_inward_date is null)) \n"); 
			}
			else
			{
				lSBQuery.append(" and tpr.ppo_inward_date between :fromDate and :toDate"); 
			}
			
								
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("postId", lStrPostId);
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("fromDate", lDtFinYearFromDate);
			lQuery.setParameter("toDate", lDtFinYearToDate);

			lLstResult = lQuery.list();
			if(!lLstResult.isEmpty())
			{
				lIntCount = Integer.parseInt(lLstResult.get(0).toString());
			}
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lIntCount;
	}
	
	public List getAuditRegisterRptPnsnrDtls(String lStrPensionerCode, String lStrLocationCode, Long lLngLangId) throws Exception {

		List lLstPensionerDetails = new ArrayList();

		Query lSqlQuery = null;
		try {

			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();

			lStrQuery.append("SELECT mph.pensioner_code,prh.ppo_no, mph.first_name, prh.ledger_no, prh.page_no, mph.appln_no, \n");
			lStrQuery.append(" mph.pnsr_file_no, mph.guardian_name, mb.bank_name, rlt.branch_name, prh.pension_type,mpd.acount_no, \n");
			lStrQuery.append(" mph.designation, mph.date_of_join, mph.date_of_retirement, mph.date_of_birth, mpd.IDENTIFICATION_DATE,  \n");
			lStrQuery.append(" mph.identity_mark, mph.pnsnr_addr1, mph.pnsnr_addr2,mph.pnsnr_addr_town,mph.pin_code, \n");
			lStrQuery.append(" csm.state_name,cdm.district_name, mph.pan_no, mph.moblile_no, mph.tele_no, mph.pnsnr_email_id, \n");
			lStrQuery.append(" prh.basic_pension_amount, prh.commencement_date, prh.cvp_monthly_amount, prh.dcrg_order_no, cast(sum(tpd.basic_pension_amount) as bigint), \n");
			lStrQuery.append(" pfd.name, prh.fp1_amount, prh.fp1_date, prh.fp2_amount, prh.fp2_date, \n");
			lStrQuery.append(" prh.org_Bf_1_11_36,prh.org_Af_1_11_36,prh.org_Af_1_11_56, prh.org_Af_1_05_60, prh.ORG_ZP_AF, \n ");
			lStrQuery.append(" mph.pensioner_photo_id,mph.pensioner_sign_id, prh.reduced_pension, mph.employment_office, prh.case_received_from, \n");
			lStrQuery.append(" mph.library_no, prh.rop_type, mpd.branch_code, prh.location_code, mphc.series,prh.cvp_authority \n");
			lStrQuery.append(" FROM TRN_PENSION_RQST_HDR prh  \n");
			lStrQuery.append(" JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code  \n");
			lStrQuery.append(" JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN mst_pensioner_family_dtls pfd ON pfd.pensioner_code = mph.pensioner_code AND pfd.percentage = :lPercent \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_prvosional_pension_dtls tpd ON tpd.pensioner_code = mph.pensioner_code   \n");
			lStrQuery.append(" LEFT OUTER JOIN MST_BANK mb ON mb.bank_code = mpd.bank_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code = rlt.branch_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN cmn_state_mst csm ON mph.state_code=csm.state_code and csm.lang_id = :langId \n");
			lStrQuery.append(" LEFT OUTER JOIN cmn_district_mst cdm ON mph.district_code=cdm.district_code and cdm.lang_id = :langId \n");
			lStrQuery.append(" LEFT OUTER JOIN mst_pension_headcode mphc ON prh.head_code = mphc.head_code \n");
			lStrQuery.append(" WHERE prh.status= :status AND prh.location_code= :locationCode AND mph.pensioner_code= :pensionerCode  ");
			lStrQuery.append(" GROUP BY mph.pensioner_code,prh.ppo_no, mph.first_name, prh.ledger_no, prh.page_no, mph.appln_no, \n");
			lStrQuery.append(" mph.pnsr_file_no, mph.guardian_name, mb.bank_name, rlt.branch_name, prh.pension_type,mpd.acount_no, \n");
			lStrQuery.append(" mph.designation, mph.date_of_join, mph.date_of_retirement, mph.date_of_birth, mpd.IDENTIFICATION_DATE,  \n");
			lStrQuery.append(" mph.identity_mark, mph.pnsnr_addr1, mph.pnsnr_addr2,mph.pnsnr_addr_town,mph.pin_code, \n");
			lStrQuery.append(" csm.state_name,cdm.district_name, mph.pan_no, mph.moblile_no, mph.tele_no, mph.pnsnr_email_id, \n");
			lStrQuery.append(" prh.basic_pension_amount, prh.commencement_date, prh.cvp_monthly_amount, prh.dcrg_order_no, \n");
			lStrQuery.append(" pfd.name, prh.fp1_amount, prh.fp1_date, prh.fp2_amount, prh.fp2_date, \n");
			lStrQuery.append(" prh.org_Bf_1_11_36,prh.org_Af_1_11_36,prh.org_Af_1_11_56, prh.org_Af_1_05_60, prh.ORG_ZP_AF, \n ");
			lStrQuery.append(" mph.pensioner_photo_id,mph.pensioner_sign_id, prh.reduced_pension, mph.employment_office, prh.case_received_from, \n");
			lStrQuery.append(" mph.library_no, prh.rop_type, mpd.branch_code, prh.location_code, mphc.series,prh.cvp_authority ");
		        
			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());

			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setString("pensionerCode", lStrPensionerCode);
			lSqlQuery.setString("locationCode", lStrLocationCode);
			lSqlQuery.setLong("langId", lLngLangId);
			lSqlQuery.setLong("lPercent", 100);

			lLstPensionerDetails = lSqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is :" + e, e);
			throw e;
		}
		return lLstPensionerDetails;

	}

	
	public List getAuditRegisterRptCvpDtls(String lStrPensionerCode,
			String lStrLocationCode,List<Short> lLstBillStatus) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT hcd.order_no,hcd.basic_pension_amt,hcd.cvp_monthly_amt,hcd.reduced_pension_amt,hcd.cvp_start_date,  \n");
			lSBQuery.append(" hcd.cvp_end_date,hcd.payment_amount,hcd.cheque_no,hcd.cheque_date,hcd.voucher_no,hcd.voucher_date  \n");
			lSBQuery.append(" FROM hst_commutation_dtls hcd  \n");
			lSBQuery.append(" left outer join trn_bill_register tbr on hcd.bill_no = tbr.bill_no and tbr.curr_bill_status not in (:billStatusList)  \n");
			lSBQuery.append(" where hcd.pensioner_code = :pensionerCode and hcd.LOCATION_CODE = :locationCode  \n");
											
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						
			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			lQuery.setParameter("locationCode", lStrLocationCode);
			lQuery.setParameterList("billStatusList", lLstBillStatus);

			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getAuditRegisterRptGratuityDtls(String lStrPensionerCode,String lStrLocationCode, List<Short> lLstBillStatus)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT hpd.ORDER_NO,hpd.ORDER_DATE,null,hpd.AMOUNT_AFTER_WITHHELD,hpd.TOTAL_RECOVERY_AMOUNT,hpd.PAID_AMOUNT, \n");
			lSBQuery.append(" hpd.CHEQUE_NO,hpd.CHEQUE_DATE,hpd.VOUCHER_NO,hpd.VOUCHER_DATE \n");
			lSBQuery.append(" FROM  hst_pnsnpmnt_dcrg_dtls hpd \n");
			lSBQuery.append(" left outer join trn_bill_register tbr on hpd.bill_no = tbr.bill_no and tbr.curr_bill_status not in (:billStatusList) \n");
			lSBQuery.append(" where hpd.pensioner_code = :pensionerCode and hpd.LOCATION_CODE = :locationCode \n");
			lSBQuery.append(" union \n");
			lSBQuery.append(" select tpd.PROV_PENSION_AUTHORITY_NO,tpd.PROV_PENSION_AUTHORITY_DATE,tpd.BASIC_PENSION_AMOUNT,null,null,null, \n");
			lSBQuery.append(" null,null,tpd.GRATUITY_VOUCHER_NO,tpd.GRATUITY_VOUCHER_DATE \n");
			lSBQuery.append(" from TRN_PRVOSIONAL_PENSION_DTLS tpd where tpd.PENSIONER_CODE = :pensionerCode and tpd.LOCATION_CODE = :locationCode \n");
																	
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						
			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			lQuery.setParameter("locationCode", lStrLocationCode);
			lQuery.setParameterList("billStatusList", lLstBillStatus);

			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getAuditRegisterRptPaymentHistory(String lStrPensionerCode,
			String lStrFromMonthYear,String lStrToMonthYear, List<Short> lLstBillStatus) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT tpb.pensionAmount,tpb.tiAmount,tpb.totalArrearAmt, \n");
			lSBQuery.append(" tpb.grossAmount,tpb.recoveryAmount,tpb.netAmount,tbr.voucherNo,tbr.voucherDate, \n");
			lSBQuery.append(" rbp.chequeNo,rbp.chequeDate,tph.forMonth FROM  \n");
			lSBQuery.append(" TrnBillRegister tbr, TrnPensionBillHdr tph, TrnPensionBillDtls tpb, RltBillParty rbp \n");
			lSBQuery.append(" where tbr.billNo = tph.billNo and tph.trnPensionBillHdrId = tpb.trnPensionBillHdrId  \n");
			lSBQuery.append(" and tbr.billNo = rbp.billNo and tbr.subjectId in (9,44) and tbr.currBillStatus not in (:billStatusList) \n");
			lSBQuery.append(" and tpb.pensionerCode = :pensionerCode and tph.forMonth between :fromMonthYear and :toMonthYear order by tph.forMonth \n");
																				
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
						
			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			lQuery.setParameter("fromMonthYear", Integer.parseInt(lStrFromMonthYear));
			lQuery.setParameter("toMonthYear", Integer.parseInt(lStrToMonthYear));
			//lQuery.setParameterList("subjectIdList", lLstSubjectId);
			lQuery.setParameterList("billStatusList", lLstBillStatus);

			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getBankBranchDtlsFromPnsnrCode(String lStrPensionerCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select mb.bankName,rbb.branchName,mpd.accountNo from MstPensionerDtls mpd,MstBank mb,RltBankBranch rbb \n");
			lSBQuery.append(" where mpd.pensionerCode = :pensionerCode and mpd.bankCode = mb.bankCode and mpd.branchCode = rbb.branchCode \n");
																				
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
						
			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getOverPaymentRecoveryDtls(Integer lIntFromMonthYear, Integer lIntToMonthYear, List<Short> lLstBillStatus, 
			List<String> lLstRecoveryType,String lStrLocCode,Long gLngLangId) {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("SELECT mph.pensioner_code,tprd.scheme_code, ms.SCHEME_NAME,mph.FIRST_NAME,tpr.ppo_no,tpr.LEDGER_NO,tpr.PAGE_NO,  \n");
			lSBQuery.append(" mb.BANK_NAME,rbb.BRANCH_NAME,mpd.ACOUNT_NO,cast(sum(tprd.amount * tprd.NO_OF_INSTALLMENTS) as bigint) as Total_recovery_amount, \n");
			lSBQuery.append(" cast(sum(tprd.AMOUNT) as bigint) as monthly_recovery_amount,sum(tprd.NO_OF_INSTALLMENTS) \n");
			lSBQuery.append(" ,tprd.FROM_MONTH ,tprd.RECOVERY_FROM_FLAG,tprd.RECOVERY_TYPE \n");
			lSBQuery.append(" FROM mst_pensioner_hdr mph join mst_pensioner_dtls mpd on mph.pensioner_code = mpd.pensioner_code \n"); 
			lSBQuery.append(" join trn_pension_rqst_hdr tpr on mph.PENSIONER_CODE = tpr.PENSIONER_CODE and tpr.location_code = :locationCode \n");
			lSBQuery.append(" left outer join mst_bank mb on mpd.bank_code = mb.bank_code  \n");
			lSBQuery.append(" left outer join rlt_bank_branch rbb on mpd.branch_code = rbb.branch_code and rbb.LOCATION_CODE = :locationCode \n");
			lSBQuery.append(" left outer join TRN_PENSION_RECOVERY_DTLS tprd on mph.pensioner_code = tprd.pensioner_code \n");
			lSBQuery.append(" left outer join mst_scheme ms on tprd.SCHEME_CODE = ms.SCHEME_code \n");
			lSBQuery.append(" where tprd.RECOVERY_TYPE in (:recoveryTypeList) and  tprd.RECOVERY_FROM_FLAG not in('SupplyPension') and \n");
			lSBQuery.append(" ((tprd.FROM_MONTH > :fromMonthYear and tprd.from_month < :toMonthYear) or (tprd.TO_MONTH between :fromMonthYear and :toMonthYear))  \n");
			lSBQuery.append(" group by mph.pensioner_code,tprd.scheme_code,ms.SCHEME_NAME,mph.FIRST_NAME,tpr.ppo_no,tpr.LEDGER_NO,tpr.PAGE_NO,mb.BANK_NAME,rbb.BRANCH_NAME,mpd.ACOUNT_NO \n");
			lSBQuery.append(" ,tprd.FROM_MONTH,tprd.RECOVERY_FROM_FLAG,tprd.RECOVERY_TYPE \n");
			lSBQuery.append(" union \n");
			lSBQuery.append(" SELECT mph.pensioner_code,tprd.scheme_code,ms.scheme_name,mph.FIRST_NAME,tpr.ppo_no,tpr.LEDGER_NO,tpr.PAGE_NO, \n"); 
			lSBQuery.append(" mb.BANK_NAME,rbb.BRANCH_NAME,mpd.ACOUNT_NO,cast(sum(tprd.amount) as bigint) as Total_recovery_amount, \n");
			lSBQuery.append(" cast(sum(tprd.AMOUNT) as bigint)as monthly_recovery_amount,sum(tprd.NO_OF_INSTALLMENTS) \n");
			lSBQuery.append(" ,tph.FOR_MONTH ,tprd.RECOVERY_FROM_FLAG,tprd.RECOVERY_TYPE \n");
			lSBQuery.append(" FROM mst_pensioner_hdr mph join mst_pensioner_dtls mpd on mph.pensioner_code = mpd.pensioner_code \n"); 
			lSBQuery.append(" join trn_pension_rqst_hdr tpr on mph.PENSIONER_CODE = tpr.PENSIONER_CODE and tpr.location_code = :locationCode \n");
			lSBQuery.append(" left outer join mst_bank mb on mpd.bank_code = mb.bank_code  \n");
			lSBQuery.append(" left outer join rlt_bank_branch rbb on mpd.branch_code = rbb.branch_code and rbb.LOCATION_CODE = :locationCode \n");
			lSBQuery.append(" left outer join TRN_PENSION_RECOVERY_DTLS tprd on tprd.pensioner_code = mph.pensioner_code  \n");
			lSBQuery.append(" left outer join mst_scheme ms on tprd.SCHEME_CODE = ms.SCHEME_code \n");
			lSBQuery.append(" join trn_bill_register tbr on tprd.BILL_NO = tbr.bill_no and tbr.CURR_BILL_STATUS not in (:billStatusList) and tbr.LOCATION_CODE = :locationCode \n");
			lSBQuery.append(" left outer join trn_pension_bill_hdr tph on tbr.bill_no = tph.BILL_NO  \n");
			//lSBQuery.append(" left outer join trn_pension_bill_dtls tpbd on tph.TRN_PENSION_BILL_HDR_ID = tpbd.TRN_PENSION_BILL_HDR_ID and tprd.pensioner_code = tpbd.pensioner_code \n");
			lSBQuery.append(" where  tprd.RECOVERY_FROM_FLAG  in('SupplyPension') and tph.FOR_MONTH between :fromMonthYear and :toMonthYear \n");
			lSBQuery.append(" group by mph.pensioner_code,tprd.scheme_code,ms.scheme_name,mph.FIRST_NAME,tpr.ppo_no,tpr.LEDGER_NO,tpr.PAGE_NO,mb.BANK_NAME,rbb.BRANCH_NAME,mpd.ACOUNT_NO \n");
			lSBQuery.append(" ,tph.FOR_MONTH,tprd.RECOVERY_FROM_FLAG,tprd.RECOVERY_TYPE \n");
			
//			lSBQuery.append(" SELECT mph.FIRST_NAME,tpr.ppo_no,tpr.LEDGER_NO,tpr.PAGE_NO, \n");
//			lSBQuery.append(" mb.BANK_NAME,rbb.BRANCH_NAME,mpd.ACOUNT_NO,tprd.AMOUNT,tprd.nature,tprd.NO_OF_INSTALLMENTS,tprd.FROM_MONTH,tprd.TO_MONTH \n");
//			lSBQuery.append(" FROM mst_pensioner_hdr mph join mst_pensioner_dtls mpd on mph.pensioner_code = mpd.pensioner_code \n");
//			lSBQuery.append(" join trn_pension_rqst_hdr tpr on mph.PENSIONER_CODE = tpr.PENSIONER_CODE and tpr.location_code = :locationCode \n");
//			lSBQuery.append(" left outer join mst_bank mb on mpd.bank_code = mb.bank_code \n");
//			lSBQuery.append(" left outer join rlt_bank_branch rbb on mpd.branch_code = rbb.branch_code and rbb.LOCATION_CODE = :locationCode \n");
//			lSBQuery.append(" left outer join TRN_PENSION_RECOVERY_DTLS tprd on mph.pensioner_code = tprd.pensioner_code \n");
//			lSBQuery.append(" where tprd.RECOVERY_TYPE in (:recoveryTypeList) and  \n");
//			lSBQuery.append(" ((tprd.FROM_MONTH between :fromMonthYear and :toMonthYear) or (tprd.TO_MONTH between :fromMonthYear and :toMonthYear)) \n");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameterList("recoveryTypeList", lLstRecoveryType);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setParameter("fromMonthYear", lIntFromMonthYear);
			lQuery.setParameter("toMonthYear", lIntToMonthYear);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getStatePensionStatisticsDtls(Integer lIntForMonthYear,
			List<Short> lLstBillStatus, List<Long> lLstSubjectId)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select count(tpd.pensioner_code),cast(sum(tpd.gross_amount) as bigint) FROM \n");
			lSBQuery.append(" trn_bill_register tbr join trn_pension_bill_hdr tph on tbr.bill_no = tph.bill_no and tbr.subject_id in (:subjectIdList) \n"); 
			lSBQuery.append(" and tbr.curr_bill_status not in (:billStatusList) \n");
			lSBQuery.append(" join trn_pension_bill_dtls tpd on tph.trn_pension_bill_hdr_id = tpd.trn_pension_bill_hdr_id and tph.for_month = :forMonthYear \n");
			lSBQuery.append(" group by tph.for_month \n");
						
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setParameterList("subjectIdList", lLstSubjectId);
			lQuery.setParameter("forMonthYear", lIntForMonthYear);
						
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getTrsyWiseStatePensionStatisticsDtls(Integer lIntForMonthYear,
			List<Short> lLstBillStatus, List<Long> lLstSubjectId)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select count(tpd.pensioner_code),cast(sum(tpd.gross_amount) as bigint),clm.loc_name,clm.location_code FROM \n");
			lSBQuery.append(" trn_bill_register tbr join trn_pension_bill_hdr tph on tbr.bill_no = tph.bill_no and tbr.subject_id in (:subjectIdList) \n"); 
			lSBQuery.append(" and tbr.curr_bill_status not in (:billStatusList) \n");
			lSBQuery.append(" join trn_pension_bill_dtls tpd on tph.trn_pension_bill_hdr_id = tpd.trn_pension_bill_hdr_id and tph.for_month = :forMonthYear \n");
			lSBQuery.append(" join cmn_location_mst clm on tbr.location_code = clm.location_code \n");
			lSBQuery.append(" group by tph.for_month,clm.loc_name,clm.location_code \n");
						
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setParameterList("subjectIdList", lLstSubjectId);
			lQuery.setParameter("forMonthYear", lIntForMonthYear);
						
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getPnsnTypeWisePensionStatisticsDtls(Integer lIntForMonthYear,
			List<Short> lLstBillStatus, List<Long> lLstSubjectId,
			String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select count(tpd.pensioner_code),cast(sum(tpd.gross_amount) as bigint),clm.loc_name,tpr.pension_type FROM \n");
			lSBQuery.append(" trn_bill_register tbr join trn_pension_bill_hdr tph on tbr.bill_no = tph.bill_no and tbr.subject_id in (:subjectIdList) \n"); 
			lSBQuery.append(" and tbr.curr_bill_status not in (:billStatusList) \n");
			lSBQuery.append(" join trn_pension_bill_dtls tpd on tph.trn_pension_bill_hdr_id = tpd.trn_pension_bill_hdr_id and tph.for_month = :forMonthYear \n");
			lSBQuery.append(" join trn_pension_rqst_hdr tpr on tpd.pensioner_code = tpr.pensioner_code \n");
			lSBQuery.append(" join cmn_location_mst clm on tbr.location_code = clm.location_code \n");
			lSBQuery.append(" where tpr.location_code = :locationCode \n");
			lSBQuery.append(" group by tph.for_month,clm.loc_name,tpr.pension_type \n");
						
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setParameterList("subjectIdList", lLstSubjectId);
			lQuery.setParameter("forMonthYear", lIntForMonthYear);
			lQuery.setParameter("locationCode", lStrLocCode);
						
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;

	}

	
	public List getDeptWiseAgingAnalysisDtls(String lStrRetirementYear,Long lLngDepartmentId, Long lLngLangId)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select clm.loc_name,tpr.pension_type,count(mph.pensioner_code),mph.retiring_department FROM  \n");
			lSBQuery.append(" mst_pensioner_hdr mph join trn_pension_rqst_hdr tpr on mph.pensioner_code = tpr.pensioner_code \n");
			lSBQuery.append(" left outer join cmn_location_mst clm on mph.retiring_department = clm.loc_id and clm.lang_id = :langId \n");
			lSBQuery.append(" left outer join org_department_mst odm on clm.department_Id = odm.department_Id and odm.department_Id = :departmentId  \n");
			lSBQuery.append(" where mph.date_of_retirement like :retirementYear \n");
			lSBQuery.append(" group by clm.loc_name,tpr.pension_type,mph.retiring_department \n");
									
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("retirementYear", '%' + lStrRetirementYear + '%');
			lQuery.setParameter("departmentId", lLngDepartmentId);
			lQuery.setParameter("langId", lLngLangId);
						
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getTrsryDeptWiseAgingAnalysisDtls(String lStrRetirementYear,
			String lStrDepartmentId, String lStrPensionType) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT clm.loc_name,count(mph.pensioner_code),clm.location_code FROM \n");
			lSBQuery.append(" mst_pensioner_hdr mph join trn_pension_rqst_hdr tpr on mph.pensioner_code = tpr.pensioner_code \n");
			lSBQuery.append(" left outer join cmn_location_mst clm on mph.location_code = clm.location_code \n");
			lSBQuery.append(" where mph.date_of_retirement like :retirementYear \n");
			if(lStrDepartmentId != null && !"".equals(lStrDepartmentId))
				lSBQuery.append(" and mph.retiring_department = :departmentId \n");
			else
				lSBQuery.append(" and mph.retiring_department is null \n");
			if(lStrPensionType != null && !"".equals(lStrPensionType))
				lSBQuery.append(" and tpr.pension_type = :pensionType \n");
			else
				lSBQuery.append(" and tpr.pension_type is null \n");
			lSBQuery.append(" group by clm.loc_name,clm.location_code ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("retirementYear", '%' + lStrRetirementYear + '%');
			if(lStrDepartmentId != null && !"".equals(lStrDepartmentId))
				lQuery.setParameter("departmentId", lStrDepartmentId);
			if(lStrPensionType != null && !"".equals(lStrPensionType))
				lQuery.setParameter("pensionType", lStrPensionType);
						
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getPnsnrTrsryDeptWiseAgingAnalysisDtls(
			String lStrRetirementYear, String lStrDepartmentId,
			String lStrPensionType, String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select mph.first_name,mph.designation from \n");
			lSBQuery.append(" mst_pensioner_hdr mph join trn_pension_rqst_hdr tpr on mph.pensioner_code = tpr.pensioner_code and mph.location_code = :locationCode \n");
			lSBQuery.append(" where mph.date_of_retirement like :retirementYear ");
			if(lStrDepartmentId != null && !"".equals(lStrDepartmentId))
				lSBQuery.append(" and mph.retiring_department = :departmentId \n");
			else
				lSBQuery.append(" and mph.retiring_department is null \n");
			if(lStrPensionType != null && !"".equals(lStrPensionType))
				lSBQuery.append(" and tpr.pension_type = :pensionType \n");
			else
				lSBQuery.append(" and tpr.pension_type is null \n");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("retirementYear", '%' + lStrRetirementYear + '%');
			if(lStrDepartmentId != null && !"".equals(lStrDepartmentId))
				lQuery.setParameter("departmentId", lStrDepartmentId);
			if(lStrPensionType != null && !"".equals(lStrPensionType))
				lQuery.setParameter("pensionType", lStrPensionType);
			lQuery.setParameter("locationCode", lStrLocCode);
						
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getTrsryWiseAgingAnalysisDtls(String lStrRetirementYear)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select tpr.location_code,clm.loc_name,tpr.pension_type,count(mph.pensioner_code) from  \n");
			lSBQuery.append(" mst_pensioner_hdr mph join trn_pension_rqst_hdr tpr on mph.pensioner_code = tpr.pensioner_code \n");
			lSBQuery.append(" join cmn_location_mst clm on tpr.location_code = clm.location_code \n");
			lSBQuery.append(" where mph.date_of_retirement like :retirementYear group by tpr.location_code,clm.loc_name,tpr.pension_type \n");
												
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("retirementYear", '%' + lStrRetirementYear + '%');
								
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getTrsryPnsnrWiseAgingAnalysisDtls(String lStrRetirementYear,
			String lStrLocationCode, String lStrPensionType) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT mph.first_name,mph.designation FROM \n"); 
			lSBQuery.append(" mst_pensioner_hdr mph join trn_pension_rqst_hdr tpr on mph.pensioner_code = tpr.pensioner_code \n");
			lSBQuery.append(" where mph.date_of_retirement like :retirementYear and mph.location_code = :locationCode \n");
			if(lStrPensionType != null && !"".equals(lStrPensionType))
				lSBQuery.append(" and tpr.pension_type = :pensionType \n");
			else
				lSBQuery.append(" and tpr.pension_type is null \n");
															
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setParameter("retirementYear", '%' + lStrRetirementYear + '%');
			if(lStrPensionType != null && !"".equals(lStrPensionType))
				lQuery.setParameter("pensionType", lStrPensionType);
			lQuery.setParameter("locationCode", lStrLocationCode);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getCasesUploadedByAGDtls() throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select inn1.TREASURY_DATA_CD,inn1.loc_name,sum(inn1.total_pnsr) from  \n");
			lSBQuery.append(" (SELECT pen.TREASURY_DATA_CD,clm.LOC_NAME,del.delv_id,count(distinct pen.APPLN_NO) as total_pnsr FROM STG_DELIVERY_DTLS del \n"); 
			lSBQuery.append(" join STG_PENSIONER pen on del.DELV_ID = pen.DELV_ID  and del.delv_type = '140005' \n");
			lSBQuery.append(" join CMN_LOCATION_MST clm on pen.TREASURY_DATA_CD = clm.LOCATION_CODE \n");
			lSBQuery.append(" where del.DELV_STATUS = 'Successful' \n");
			lSBQuery.append(" group by pen.TREASURY_DATA_CD,clm.LOC_NAME,del.delv_id ) inn1 \n");
			lSBQuery.append(" group by inn1.TREASURY_DATA_CD,inn1.loc_name \n");
												
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getTrsrywiseCasesUploadedByAGDtls(String lStrLocationCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select inn2.fin_desc,sum(inn2.total_pnsr) from \n");
			lSBQuery.append(" (select (select sfm.FIN_YEAR_DESC from SGVC_FIN_YEAR_MST sfm where  inn1.UPLOAD_DATE between sfm.from_date and sfm.to_date ) as fin_desc, \n");
			lSBQuery.append(" inn1.total_pnsr from \n");
			lSBQuery.append(" (SELECT del.delv_id,del.UPLOAD_DATE,count(distinct pen.APPLN_NO) as total_pnsr FROM STG_DELIVERY_DTLS del \n"); 
			lSBQuery.append(" join STG_PENSIONER pen on del.DELV_ID = pen.DELV_ID  and del.delv_type = '140005' and pen.TREASURY_DATA_CD = :locationCode \n");
			lSBQuery.append(" where del.DELV_STATUS = 'Successful' \n");
			lSBQuery.append(" group by del.delv_id,del.UPLOAD_DATE ) inn1) inn2 \n");
			lSBQuery.append(" group by inn2.fin_desc \n");
															
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locationCode", lStrLocationCode);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public Integer getTotalNonIdentifiedPnsnr(String lStrStatus,List<String> lLstCaseStatus) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		Integer lIntNoOfPnsnr = 0;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select count(1) FROM trn_pension_rqst_hdr where status = :status and case_status in (:caseStatusList) \n");
													
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("status", lStrStatus);
			lQuery.setParameterList("caseStatusList", lLstCaseStatus);
			
			lLstResult = lQuery.list();
			
			if(!lLstResult.isEmpty())
			{
				lIntNoOfPnsnr = Integer.parseInt(lLstResult.get(0).toString());
			}
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lIntNoOfPnsnr;
	}

	
	public List getTrsryWiseNonIdentifiedPnsnr(String lStrStatus,
			List<String> lLstCaseStatus) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("SELECT clm.loc_name, count(pensioner_code),tpr.location_code FROM trn_pension_rqst_hdr tpr, cmn_location_mst clm \n");
			lSBQuery.append(" where tpr.location_code = clm.location_code  \n");
			lSBQuery.append(" and status = :status and case_status in (:caseStatusList) \n");
			lSBQuery.append(" group by tpr.location_code, clm.loc_name  \n");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("status", lStrStatus);
			lQuery.setParameterList("caseStatusList", lLstCaseStatus);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getPnsnrWiseNonIdentifiedPnsnr(String lStrStatus,
			List<String> lLstCaseStatus, String lStrLocationCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select mph.first_name, mph.designation  from  \n");
			lSBQuery.append(" mst_pensioner_hdr mph, trn_pension_rqst_hdr tpr \n");
			lSBQuery.append(" where mph.pensioner_code = tpr.pensioner_code \n");
			lSBQuery.append(" and tpr.status = :status and tpr.case_status in (:caseStatusList) and mph.location_code = :locationCode ");
					
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("status", lStrStatus);
			lQuery.setParameterList("caseStatusList", lLstCaseStatus);
			lQuery.setParameter("locationCode", lStrLocationCode);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getTrsryWiseRevisedPpoByAGDtls() throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select clm.location_code,count(1) From TRN_PENSIONER_RIVISION_DTLS tpr1 \n"); 
			lSBQuery.append(" join cmn_location_mst clm on tpr1.location_code = clm.location_code  \n");
			lSBQuery.append(" where (tpr1.location_code,tpr1.pensioner_code,tpr1.created_date) in \n");
			lSBQuery.append(" (SELECT tpr.LOCATION_CODE,tpr.pensioner_code,max(tpr.created_date)  FROM  \n");
			lSBQuery.append(" TRN_PENSIONER_RIVISION_DTLS tpr \n");
			lSBQuery.append(" group by tpr.lOCATION_CODE,tpr.pensioner_code) \n");
			lSBQuery.append(" and \n");
			lSBQuery.append(" tpr1.REVISION_FLAG = 'AG' and tpr1.PPO_ACCEPT_DATE is not null \n");
			lSBQuery.append(" group by clm.location_code \n");
								
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}
	
	public List getTrsryWiseNonRevisedPpoByAGDtls() throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select clm.location_code,count(1) From TRN_PENSIONER_RIVISION_DTLS tpr1 \n"); 
			lSBQuery.append(" join cmn_location_mst clm on tpr1.location_code = clm.location_code  \n");
			lSBQuery.append(" where (tpr1.location_code,tpr1.pensioner_code,tpr1.created_date) in \n");
			lSBQuery.append(" (SELECT tpr.LOCATION_CODE,tpr.pensioner_code,max(tpr.created_date)  FROM  \n");
			lSBQuery.append(" TRN_PENSIONER_RIVISION_DTLS tpr \n");
			lSBQuery.append(" group by tpr.lOCATION_CODE,tpr.pensioner_code) \n");
			lSBQuery.append(" and \n");
			lSBQuery.append(" tpr1.REVISION_FLAG = 'AG' and tpr1.PPO_ACCEPT_DATE is null \n");
			lSBQuery.append(" group by clm.location_code \n");
								
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}

	
	public List getPnsnrWiseRevisedPpoByAGDtls(String lStrLocationCode,
			String lStrRevisionFlag) throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select mph.first_name,mph.designation From mst_pensioner_hdr mph \n"); 
			lSBQuery.append(" join trn_pensioner_rivision_dtls tpr1 on mph.pensioner_code = tpr1.pensioner_code \n"); 
			lSBQuery.append(" where (tpr1.pensioner_code, tpr1.created_date) in \n");
			lSBQuery.append("(SELECT tpr.pensioner_code,max(tpr.created_date)  FROM \n"); 
			lSBQuery.append(" TRN_PENSIONER_RIVISION_DTLS tpr where tpr.location_code = :locationCode \n"); 
			lSBQuery.append(" and tpr.revision_flag = 'AG' \n"); 
			if("Y".equals(lStrRevisionFlag))
				lSBQuery.append(" and tpr.ppo_accept_date is not null \n");
			else
				lSBQuery.append(" and tpr.ppo_accept_date is null \n");
			lSBQuery.append(" group by tpr.pensioner_code) \n");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locationCode", lStrLocationCode);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;

	}
	
	public List getPayComWisePensionerDtls(String lStrLocationCode, String lStrRopType) throws Exception {
		
		List lLstResult = new ArrayList();
		List lArrListOuter = new ArrayList();
		ArrayList lArrListInner = null;
		Integer lIntSerialNo = 0;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("SELECT tph.ppo_no,tph.LEDGER_NO,tph.PAGE_NO,mph.FIRST_NAME,mb.BANK_NAME,rbb.BRANCH_NAME,mpd.ACOUNT_NO, "); 
			lSBQuery.append("concat(concat(concat(concat(emp.emp_fname,' '),emp.emp_mname),' '),emp.emp_lname) FROM "); 
			lSBQuery.append("mst_pensioner_hdr mph join trn_pension_rqst_hdr tph on mph.pensioner_code = tph.pensioner_code ");
			lSBQuery.append("and tph.status = :status and tph.LOCATION_CODE = :locationCode and tph.ROP_TYPE = :RopType "); 
			lSBQuery.append("join mst_pensioner_dtls mpd on mpd.pensioner_code = mph.pensioner_code "); 
			lSBQuery.append("LEFT OUTER JOIN org_userpost_rlt usrpst ON usrpst.post_id = tph.case_owner "); 
			lSBQuery.append("LEFT OUTER JOIN org_emp_mst emp ON usrpst.user_id = emp.user_id ");
			lSBQuery.append("left outer join mst_bank mb on mpd.bank_code = mb.bank_code ");
			lSBQuery.append("left outer join rlt_bank_branch rbb on mpd.branch_code = rbb.branch_code ");
			lSBQuery.append("order by concat(concat(concat(concat(emp.emp_fname,' '),emp.emp_mname),' '),emp.emp_lname),mb.BANK_NAME,rbb.BRANCH_NAME,tph.LEDGER_NO,tph.PAGE_NO,tph.ppo_no ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lQuery.setParameter("locationCode", lStrLocationCode);
			lQuery.setParameter("RopType", lStrRopType);
			
			lLstResult = lQuery.list();
			
			if (lLstResult != null && !lLstResult.isEmpty()) {
				
				Iterator it = lLstResult.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lArrListInner = new ArrayList();
					lIntSerialNo++;
					lArrListInner.add(lIntSerialNo);
					if (tuple[0] != null) {
						lArrListInner.add(tuple[0]);// PPO NO
					} else {
						lArrListInner.add("");
					}
					if (tuple[1] != null) {
						lArrListInner.add(tuple[1]);// Volume No.
					} else {
						lArrListInner.add("");
					}
					if (tuple[2] != null){
						lArrListInner.add(tuple[2]);// Page No.
					} else {
						lArrListInner.add("");
					}
					if (tuple[3] != null) {
						lArrListInner.add(tuple[3]);// Pensioner Name
					} else {
						lArrListInner.add("");
					}
					if (tuple[4] != null) {
						lArrListInner.add(tuple[4]);//Bank Name
					} else {
						lArrListInner.add("Not paid yet");
					}
					if (tuple[5] != null) {
						lArrListInner.add(tuple[5]);// Branch Name
					} else {
						lArrListInner.add("");
					}
					if (tuple[6] != null) {
						lArrListInner.add(tuple[6]);// A/C No
					} else {
						lArrListInner.add("Not paid yet");
					}
					if (tuple[7] != null) {
						lArrListInner.add(tuple[7]);// Auditor Name
					} else {
						lArrListInner.add("");
					}

					lArrListOuter.add(lArrListInner);
				}
			}
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lArrListOuter;
}

	
	public List getMonthlyRecoveryDtls(Integer lIntFromMonthYear,
			Integer lIntToMonthYear, List<String> lLstRecoveryType,List<Short> lLstBillStatus,
			String lStrLocCode) {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("select tmpr.pensioner_code,tmpr.SCHEME_CODE,cast(sum(tmpr.amount) as bigint) from TRN_MONTHLY_PENSION_RECOVERY_DTLS tmpr \n");
			lSBQuery.append(" join trn_bill_register tbr on tmpr.bill_no = tbr.bill_no and tbr.CURR_BILL_STATUS not in (:billStatusList) \n");
			lSBQuery.append(" where tmpr.FOR_MONTH between :fromMonthYear and :toMonthYear and tmpr.LOCATION_CODE = :locationCode \n");
			lSBQuery.append(" and tmpr.RECOVERY_TYPE in (:recoveryTypeList) \n");
			lSBQuery.append(" group by tmpr.pensioner_code,tmpr.scheme_code ");
			
//			lSBQuery.append("select pensioner_code,SCHEME_CODE,cast(sum(amount) as bigint) from TRN_MONTHLY_PENSION_RECOVERY_DTLS  \n");
//			lSBQuery.append(" where FOR_MONTH between :fromMonthYear and :toMonthYear and LOCATION_CODE = :locationCode  \n");
//			lSBQuery.append(" and RECOVERY_TYPE in (:recoveryTypeList) \n");
//			lSBQuery.append(" group by pensioner_code,scheme_code \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("fromMonthYear", lIntFromMonthYear);
			lQuery.setParameter("toMonthYear", lIntToMonthYear);
			lQuery.setParameterList("recoveryTypeList", lLstRecoveryType);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setParameter("locationCode", lStrLocCode);
			
			lLstResult = lQuery.list();
			
		} catch (Exception e) {
			gLogger.error("error is :" + e, e);
		}
		return lLstResult;
	}
}
