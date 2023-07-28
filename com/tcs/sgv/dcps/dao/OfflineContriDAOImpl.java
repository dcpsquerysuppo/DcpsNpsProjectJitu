/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.eis.dao.PayBillDAO;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.valueobject.HrPayPaybill;

/**
 * 
 * Class Description -
 * 
 * 
 * @author Vihan
 * @version 0.1
 * @since JDK 5.0 Mar 4, 2011
 */
public class OfflineContriDAOImpl extends GenericDaoHibernateImpl implements
		OfflineContriDAO {

	/**
	 * @param type
	 */

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	public OfflineContriDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
/*		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
*/
		
		setSessionFactory(sessionFactory);
		ghibSession = getSession();

		
	}
	
	

	public List getEmpListForContribution(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId, Map displayTag,
			String lDtFirstDate, String lStrTypeOfPaymentMaster,Long delayedMonthId,Long delayedYearId,String lDtLastDate) {

		List empList = null;
		List finalList = new ArrayList();

		String[] columnValues = null;
		columnValues = new String[] { "EM.EMP_NAME", "EM.DCPS_ID" };
		
		Double lDoubleDefaultDArateForNon5th6thPC = 58d;
		Date lDtCurrDate = SessionHelper.getCurDate();
		 gLogger.info("lStrDDOCode is *********"+lStrDDOCode);
		 gLogger.info("lDtFirstDate is *********"+lDtFirstDate);
		 gLogger.info("lLongbillGroupId is *********"+lLongbillGroupId);
		try {

			StringBuilder SBQuery = new StringBuilder();
			
			if(!(lStrUser.equals("ATO")))
			{
				SBQuery.append("SELECT EM.DCPS_EMP_ID,EM.DCPS_ID,EM.EMP_NAME,EM.PAY_COMMISSION,nvl(CO.BASIC_PAY,EM.BASIC_PAY),nvl(CO.DCPS_CONTRIBUTION_ID,0),nvl(CO.TYPE_OF_PAYMENT,'"
								+ lStrTypeOfPaymentMaster.trim()
								+ "'),nvl(CO.MONTH_ID,0),nvl(CO.FIN_YEAR_ID,0),nvl(DA.DA_RATE,"+ lDoubleDefaultDArateForNon5th6thPC + "),CO.REG_STATUS,EM.DOJ,CO.DA,CO.DP,CO.CONTRIBUTION,CO.startDate,CO.endDate"
								+ " FROM mst_dcps_emp EM LEFT OUTER JOIN TRN_DCPS_CONTRIBUTION CO ON EM.DCPS_EMP_ID=CO.DCPS_EMP_ID "
								+ " AND CO.MONTH_ID="
								+ monthId
								+ " AND CO.FIN_YEAR_ID=" + yearId);
			}

			if (
					(!(lStrUser.equals("TO")))
					&& (!(lStrUser.equals("DDO") && (lStrUse.equals("ViewApproved")	|| (lStrUse.equals("ViewForwarded")))))
					&& (!(lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))))
					&& (!((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && lStrUse.equals("ViewRejected")))
				) {
				SBQuery.append(" AND CO.TYPE_OF_PAYMENT = '"
						+ lStrTypeOfPaymentMaster.trim() + "'");
			}
			
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")) && lStrTypeOfPaymentMaster.equals("700047") )
			{
				if(delayedMonthId != null && delayedYearId != null)
				{
					SBQuery.append(" AND CO.DELAYED_MONTH_ID = " + delayedMonthId);
					SBQuery.append(" AND CO.DELAYED_FIN_YEAR_ID = " + delayedYearId);
				}
			}

			SBQuery
					.append(" LEFT OUTER JOIN mst_dcps_contri_voucher_dtls CV ON CV.treasury_code=CO.TREASURY_CODE AND CV.month_id=CO.MONTH_ID AND CV.year_id = CO.FIN_YEAR_ID AND CV.bill_group_id=CO.BILL_GROUP_ID");

			// Temporarily removed...
			/*
			
			if (!((lStrUser.equals("ATO") || lStrUser.equals("DDOAsst") || lStrUser.equals("DDO")) && lStrUse.equals("ViewAll"))) {
				if (!(lStrUser.equals("DDO") && lStrUse.equals("ViewApproved"))) {
					SBQuery
							.append(" LEFT OUTER JOIN wf_job_mst WF ON WF.job_ref_id=CO.DCPS_CONTRIBUTION_ID AND WF.lst_act_post_id = '"
									+ lStrPostId + "'");
				}

			}
			
			*/

			SBQuery
					.append(" LEFT JOIN MST_DCPS_DA_RATE DA ON DA.PAY_COMMISSION = EM.PAY_COMMISSION AND  (('"
							+ lDtFirstDate
							+ "' BETWEEN DA.EFFECTIVE_FROM AND DA.APPLICABLE_TO) OR ('"
							+ lDtFirstDate
							+ "' >= DA.EFFECTIVE_FROM and DA.APPLICABLE_TO IS NULL)) ");
			
			// Code Added to show employees of valid post and service only for first online contribution entry
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" join org_emp_mst OE on OE.emp_id = EM.ORG_EMP_MST_ID");
				SBQuery.append(" join org_userpost_rlt OUP on OUP.user_id = OE.user_id and OUP.activate_flag = 1 ");
				SBQuery.append(" join org_post_mst OP on OP.post_id = OUP.post_id and OP.ACTIVATE_FLAG = 1");
			}
			

			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" WHERE EM.DDO_CODE='" + lStrDDOCode + "'");
			}
			else
			{
				SBQuery.append(" WHERE CO.DDO_CODE='" + lStrDDOCode + "'");
			}

		/*	if (lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))) {
				SBQuery.append(" AND CO.BILL_GROUP_ID=" + lLongbillGroupId);
			} else {
				SBQuery.append(" AND EM.BILLGROUP_ID=" + lLongbillGroupId);
			}
		*/	
			if((!(lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))))
					&&
			(lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll"))	
			)
			{
				SBQuery.append(" AND EM.BILLGROUP_ID=" + lLongbillGroupId);
			}
			else
			{
				SBQuery.append(" AND CO.BILL_GROUP_ID=" + lLongbillGroupId);
			}
			
			// Code Added to show employees of valid post and service and date of joining and DCPS employee only for first online contribution entry
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				//SBQuery.append(" AND EM.REG_STATUS=1 AND EM.DCPS_OR_GPF = 'Y' AND EM.DOJ <'" + lDtFirstDate+ "'");
				SBQuery.append(" AND EM.REG_STATUS=1 AND EM.DCPS_OR_GPF = 'Y' AND EM.DOJ <'" + lDtLastDate+ "'");
				SBQuery.append(" AND ( EM.EMP_SERVEND_DT is null or EM.EMP_SERVEND_DT > '" + lDtFirstDate+ "' )");
				SBQuery.append(" AND (OE.EMP_SRVC_EXP is null or OE.EMP_SRVC_EXP >'" + lDtFirstDate+ "' )");
				SBQuery.append(" AND (OP.END_DATE is null or OP.END_DATE > '" + lDtFirstDate+ "' )");
			}

			if (!((lStrUser.equals("ATO") || lStrUser.equals("DDOAsst") || lStrUser
					.equals("DDO")) && lStrUse.equals("ViewAll"))) {

				if ((lStrUser.equals("TO")) && lStrUse.equals("ViewForwarded")) {
					SBQuery.append(" AND CO.REG_STATUS IN (4,3,1) "); // 3-Online,4-Manual

				}

				if ((lStrUser.equals("TO")) && lStrUse.equals("ViewReverted")) {
					SBQuery.append(" AND CV.REVERSION_FLAG = 2 "); 

				}

				if (lStrUser.equals("DDO") && lStrUse.equals("ViewForwarded")) {
					SBQuery.append(" AND CO.REG_STATUS = 2"); // for Online
				}

				if (lStrUser.equals("DDO") && lStrUse.equals("ViewApproved")) {
					SBQuery.append(" AND CO.REG_STATUS = 1"); // for Online
				}

				if (lStrUser.equals("ATO") && lStrUse.equals("ViewRejected")) {
					SBQuery.append(" AND CO.REG_STATUS = -4"); // 4 for Manual
				}

				if (lStrUser.equals("DDOAsst")
						&& lStrUse.equals("ViewRejected")) {
					SBQuery.append(" AND CO.REG_STATUS = -3"); // 3 for Online
				}
				
				// Temporarily removed

				/*
				 
				if (!(lStrUser.equals("DDO") && lStrUse.equals("ViewApproved"))) {
					SBQuery.append(" AND WF.doc_id IN (700003,700006) ");
				}
				
				*/

			} else {
				SBQuery.append(" AND CO.REG_STATUS IS NULL ");
			}
			
			SBQuery.append(" Order By \n");
			String orderString = (displayTag
					.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag
					.get(Constants.KEY_SORT_ORDER)
					: "desc");
			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				if (orderbypara == 0 || orderbypara == 1) {
					SBQuery.append(columnValues[orderbypara.intValue()] + " "
							+ orderString);
				} else {
					SBQuery.append(" EM.EMP_NAME ASC");
				}
			} else {
				SBQuery.append(" EM.EMP_NAME ASC");
			}

			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());
			//stQuery.setDate("currentDate", lDtCurrDate);
             
			empList = stQuery.list();
			Integer lInt2 = 0;
			Double BasicPay = 0d;
			Double DP = 0D;
			String lStrDP = "";
			Double DARate = 0d;
			String lStrTypeOfPayment ="";
			Double DA = 0D;
			Double employeeContribution = 0D;
			String lStrDA="";

			for (Integer lInt1 = 0; lInt1 < empList.size(); lInt1++) {
				Object[] tempObjectList = (Object[]) empList.get(lInt1);
				Object[] newList = new Object[tempObjectList.length + 4];
				lInt2 = 0;
				lInt2 = tempObjectList.length;
				for (lInt2 = 0; lInt2 < tempObjectList.length; lInt2++) {
					
					// Changes Basic pay to Integer value
					if(lInt2 == 4)
					{
						if(tempObjectList[lInt2] == null)
						{
							tempObjectList[lInt2] = 0;
						}
						else
						{
						
							tempObjectList[lInt2] = (int) Math.ceil(Double.parseDouble(tempObjectList[lInt2].toString()));
						}
					}
					
					newList[lInt2] = tempObjectList[lInt2];
					
				}
				BasicPay = Double.parseDouble(tempObjectList[4]
						.toString());

				DP = 0D;
				lStrDP = "";
				if (null != tempObjectList[13]) {
					lStrDP = tempObjectList[13].toString();
				}
				if (newList[3].toString().equals("700015") || newList[3].toString().equals("700345")) {
					if (null != lStrDP && !"".equals(lStrDP)) {
						DP = Double.parseDouble(lStrDP);
					} else {
						DP = BasicPay / 2;
					}
				}
				DARate = 0.01 * Double.parseDouble(tempObjectList[9]
						.toString());
				lStrTypeOfPayment = tempObjectList[6].toString();
				DA = 0D;
				employeeContribution = 0D;

				lStrDA = "";
				
				if (null != tempObjectList[12]) {
					lStrDA = tempObjectList[12].toString();
				}
				if (lStrTypeOfPayment.equals("700048")) {
					if (tempObjectList[12] != null) {
						DA = Double.parseDouble(tempObjectList[12].toString());
					}
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
				} else if (lStrTypeOfPayment.equals("700049")) {
					DA = 0D;
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
				}else if (lStrTypeOfPayment.equals("700080")) {////PayArrearDiff $t 23-2-2021
					DA = 0D;
					if (tempObjectList[14] != null) {
						employeeContribution = Double.parseDouble(tempObjectList[14].toString());
					}
				}else {
					if (null != lStrDA && !"".equals(lStrDA)) {
						DA = Double.parseDouble(lStrDA);
					} else {
						DA = ((BasicPay + DP) * DARate);
					}

					if (null != tempObjectList[14]) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					} else {
						if (newList[3].toString().equals("700015")) {
							employeeContribution = ((double) Math
									.ceil(BasicPay)
									+ Math.ceil(DP) + Math.round(DA)) * 0.10;
						} else {
							employeeContribution = ((double) Math
									.ceil(BasicPay) + Math.round(DA)) * 0.10;
						}
					}
				}
				DA = (double) Math.round(DA);

				employeeContribution = (double) Math
						.ceil(employeeContribution);
				newList[lInt2] = (int) Math.ceil(DP);
				newList[lInt2 + 1] = (int) Math.round(DA);
				newList[lInt2 + 2] = (int) Math.ceil(employeeContribution);

				newList[lInt2 + 3] = DARate;

				finalList.add(newList);
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
		return finalList;
	}
	
	
	
	public List getEmpListForContributionSchdlr(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId,
			String lDtFirstDate, String lStrTypeOfPaymentMaster,Long delayedMonthId,Long delayedYearId) {

		List empList = null;
		List finalList = new ArrayList();
		 gLogger.info("lStrDDOCode is *********"+lStrDDOCode);
		 gLogger.info("lDtFirstDate is *********"+lDtFirstDate);
		 gLogger.info("lLongbillGroupId is *********"+lLongbillGroupId);

		String[] columnValues = null;
		columnValues = new String[] { "EM.EMP_NAME", "EM.DCPS_ID" };
		
		Double lDoubleDefaultDArateForNon5th6thPC = 58d;
		Date lDtCurrDate = SessionHelper.getCurDate();

		try {

			StringBuilder SBQuery = new StringBuilder();
			
			if(!(lStrUser.equals("ATO")))
			{
				SBQuery.append("SELECT EM.DCPS_EMP_ID,EM.DCPS_ID,EM.EMP_NAME,EM.PAY_COMMISSION,nvl(CO.BASIC_PAY,EM.BASIC_PAY),nvl(CO.DCPS_CONTRIBUTION_ID,0),nvl(CO.TYPE_OF_PAYMENT,'"
								+ lStrTypeOfPaymentMaster.trim()
								+ "'),nvl(CO.MONTH_ID,0),nvl(CO.FIN_YEAR_ID,0),nvl(DA.DA_RATE,"+ lDoubleDefaultDArateForNon5th6thPC + "),CO.REG_STATUS,EM.DOJ,CO.DA,CO.DP,CO.CONTRIBUTION,CO.startDate,CO.endDate"
								+ " FROM mst_dcps_emp EM LEFT OUTER JOIN TRN_DCPS_CONTRIBUTION CO ON EM.DCPS_EMP_ID=CO.DCPS_EMP_ID "
								+ " AND CO.MONTH_ID="
								+ monthId
								+ " AND CO.FIN_YEAR_ID=" + yearId);
			}

			if (
					(!(lStrUser.equals("TO")))
					&& (!(lStrUser.equals("DDO") && (lStrUse.equals("ViewApproved")	|| (lStrUse.equals("ViewForwarded")))))
					&& (!(lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))))
					&& (!((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && lStrUse.equals("ViewRejected")))
				) {
				SBQuery.append(" AND CO.TYPE_OF_PAYMENT = '"
						+ lStrTypeOfPaymentMaster.trim() + "'");
			}
			
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")) && lStrTypeOfPaymentMaster.equals("700047") )
			{
				if(delayedMonthId != null && delayedYearId != null)
				{
					SBQuery.append(" AND CO.DELAYED_MONTH_ID = " + delayedMonthId);
					SBQuery.append(" AND CO.DELAYED_FIN_YEAR_ID = " + delayedYearId);
				}
			}

			SBQuery
					.append(" LEFT OUTER JOIN mst_dcps_contri_voucher_dtls CV ON CV.treasury_code=CO.TREASURY_CODE AND CV.month_id=CO.MONTH_ID AND CV.year_id = CO.FIN_YEAR_ID AND CV.bill_group_id=CO.BILL_GROUP_ID");

			// Temporarily removed...
			/*
			
			if (!((lStrUser.equals("ATO") || lStrUser.equals("DDOAsst") || lStrUser.equals("DDO")) && lStrUse.equals("ViewAll"))) {
				if (!(lStrUser.equals("DDO") && lStrUse.equals("ViewApproved"))) {
					SBQuery
							.append(" LEFT OUTER JOIN wf_job_mst WF ON WF.job_ref_id=CO.DCPS_CONTRIBUTION_ID AND WF.lst_act_post_id = '"
									+ lStrPostId + "'");
				}

			}
			
			*/

			SBQuery
					.append(" LEFT JOIN MST_DCPS_DA_RATE DA ON DA.PAY_COMMISSION = EM.PAY_COMMISSION AND  (('"
							+ lDtFirstDate
							+ "' BETWEEN DA.EFFECTIVE_FROM AND DA.APPLICABLE_TO) OR ('"
							+ lDtFirstDate
							+ "' >= DA.EFFECTIVE_FROM and DA.APPLICABLE_TO IS NULL)) ");
			
			// Code Added to show employees of valid post and service only for first online contribution entry
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" join org_emp_mst OE on OE.emp_id = EM.ORG_EMP_MST_ID");
				SBQuery.append(" join org_userpost_rlt OUP on OUP.user_id = OE.user_id and OUP.activate_flag = 1 ");
				SBQuery.append(" join org_post_mst OP on OP.post_id = OUP.post_id and OP.ACTIVATE_FLAG = 1");
			}
			

			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" WHERE EM.DDO_CODE='" + lStrDDOCode + "'");
			}
			else
			{
				SBQuery.append(" WHERE CO.DDO_CODE='" + lStrDDOCode + "'");
			}

		/*	if (lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))) {
				SBQuery.append(" AND CO.BILL_GROUP_ID=" + lLongbillGroupId);
			} else {
				SBQuery.append(" AND EM.BILLGROUP_ID=" + lLongbillGroupId);
			}
		*/	
			if((!(lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))))
					&&
			(lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll"))	
			)
			{
				SBQuery.append(" AND EM.BILLGROUP_ID=" + lLongbillGroupId);
			}
			else
			{
				SBQuery.append(" AND CO.BILL_GROUP_ID=" + lLongbillGroupId);
			}
			
			// Code Added to show employees of valid post and service and date of joining and DCPS employee only for first online contribution entry
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" AND EM.REG_STATUS=1 AND EM.DCPS_OR_GPF = 'Y' AND EM.DOJ <'" + lDtFirstDate+ "'");
				
				SBQuery.append(" AND (OE.EMP_SRVC_EXP is null or OE.EMP_SRVC_EXP >'" + lDtFirstDate+ "' )");
				SBQuery.append(" AND (OP.END_DATE is null or OP.END_DATE > '" + lDtFirstDate+ "' )");
			}

			if (!((lStrUser.equals("ATO") || lStrUser.equals("DDOAsst") || lStrUser
					.equals("DDO")) && lStrUse.equals("ViewAll"))) {

				if ((lStrUser.equals("TO")) && lStrUse.equals("ViewForwarded")) {
					SBQuery.append(" AND CO.REG_STATUS IN (4,3,1) "); // 3-Online,4-Manual

				}

				if ((lStrUser.equals("TO")) && lStrUse.equals("ViewReverted")) {
					SBQuery.append(" AND CV.REVERSION_FLAG = 2 "); 

				}

				if (lStrUser.equals("DDO") && lStrUse.equals("ViewForwarded")) {
					SBQuery.append(" AND CO.REG_STATUS = 2"); // for Online
				}

				if (lStrUser.equals("DDO") && lStrUse.equals("ViewApproved")) {
					SBQuery.append(" AND CO.REG_STATUS = 1"); // for Online
				}

				if (lStrUser.equals("ATO") && lStrUse.equals("ViewRejected")) {
					SBQuery.append(" AND CO.REG_STATUS = -4"); // 4 for Manual
				}

				if (lStrUser.equals("DDOAsst")
						&& lStrUse.equals("ViewRejected")) {
					SBQuery.append(" AND CO.REG_STATUS = -3"); // 3 for Online
				}
				
				// Temporarily removed

				/*
				 
				if (!(lStrUser.equals("DDO") && lStrUse.equals("ViewApproved"))) {
					SBQuery.append(" AND WF.doc_id IN (700003,700006) ");
				}
				
				*/

			} else {
				SBQuery.append(" AND CO.REG_STATUS IS NULL ");
			}
			
			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());
			//stQuery.setDate("currentDate", lDtCurrDate);

			empList = stQuery.list();
			Integer lInt2 = 0;
			Double BasicPay = 0d;
			Double DP = 0D;
			String lStrDP = "";
			Double DARate = 0d;
			String lStrTypeOfPayment ="";
			Double DA = 0D;
			Double employeeContribution = 0D;
			String lStrDA="";

			for (Integer lInt1 = 0; lInt1 < empList.size(); lInt1++) {
				Object[] tempObjectList = (Object[]) empList.get(lInt1);
				Object[] newList = new Object[tempObjectList.length + 4];
				lInt2 = 0;
				lInt2 = tempObjectList.length;
				for (lInt2 = 0; lInt2 < tempObjectList.length; lInt2++) {
					
					// Changes Basic pay to Integer value
					if(lInt2 == 4)
					{
						if(tempObjectList[lInt2] == null)
						{
							tempObjectList[lInt2] = 0;
						}
						else
						{
						
							tempObjectList[lInt2] = (int) Math.ceil(Double.parseDouble(tempObjectList[lInt2].toString()));
						}
					}
					
					newList[lInt2] = tempObjectList[lInt2];
					
				}
				BasicPay = Double.parseDouble(tempObjectList[4]
						.toString());

				DP = 0D;
				lStrDP = "";
				if (null != tempObjectList[13]) {
					lStrDP = tempObjectList[13].toString();
				}
				if (newList[3].toString().equals("700015") || newList[3].toString().equals("700345")) {
					if (null != lStrDP && !"".equals(lStrDP)) {
						DP = Double.parseDouble(lStrDP);
					} else {
						DP = BasicPay / 2;
					}
				}
				DARate = 0.01 * Double.parseDouble(tempObjectList[9]
						.toString());
				lStrTypeOfPayment = tempObjectList[6].toString();
				DA = 0D;
				employeeContribution = 0D;

				lStrDA = "";
				
				if (null != tempObjectList[12]) {
					lStrDA = tempObjectList[12].toString();
				}
				if (lStrTypeOfPayment.equals("700048")) {
					if (tempObjectList[12] != null) {
						DA = Double.parseDouble(tempObjectList[12].toString());
					}
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
				} else if (lStrTypeOfPayment.equals("700049")) {
					DA = 0D;
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
				}else if (lStrTypeOfPayment.equals("700080")) {////PayArrearDiff $t 23-2-2021
					DA = 0D;
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
				}else {
					if (null != lStrDA && !"".equals(lStrDA)) {
						DA = Double.parseDouble(lStrDA);
					} else {
						DA = ((BasicPay + DP) * DARate);
					}

					if (null != tempObjectList[14]) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					} else {
						if (newList[3].toString().equals("700015")) {
							employeeContribution = ((double) Math
									.ceil(BasicPay)
									+ Math.ceil(DP) + Math.round(DA)) * 0.10;
						} else {
							employeeContribution = ((double) Math
									.ceil(BasicPay) + Math.round(DA)) * 0.10;
						}
					}
				}
				DA = (double) Math.round(DA);

				employeeContribution = (double) Math
						.ceil(employeeContribution);
				newList[lInt2] = (int) Math.ceil(DP);
				newList[lInt2 + 1] = (int) Math.round(DA);
				newList[lInt2 + 2] = (int) Math.ceil(employeeContribution);

				newList[lInt2 + 3] = DARate;

				finalList.add(newList);
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
		return finalList;
	}

	public List getCurrentTreasury(String gStrLocationCode) {

		String query = "select LM.locationCode, LM.locName from CmnLocationMst LM where LM.locationCode = :locationCode";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", gStrLocationCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getAllTreasuries() {

		String query = "select LM.locationCode, LM.locName from CmnLocationMst LM where departmentId = 100003 ";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getTreasuryForDDO(String lStrDdoCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT LM.locationCode, LM.locName FROM RltDdoOrg RO, CmnLocationMst LM ");
		sb
				.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getAllDDO(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb.append(" order by DM.ddoName");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " + obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}
	
	public List getAllDDOCodeASC(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb.append(" order by DM.ddoCode ASC");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " + obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}
	
	public List getAllDDOWOBrackets(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb.append(" order by DM.ddoCode ASC");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getAllDDOForContriForwardedToTO(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT DISTINCT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM , TrnDcpsContribution VC ");
		sb.append(" WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
//		sb.append(" AND VC.ddoCode = DM.ddoCode AND VC.regStatus IN (1,4,3) ");
		// Approved not shown
		sb.append(" AND VC.ddoCode = DM.ddoCode AND VC.regStatus IN (3) ");
		sb.append(" order by DM.ddoCode ASC ");
		
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}
	
	public List getAllDDOForApprovedContriInTO(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT DISTINCT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM , TrnDcpsContribution VC ");
		sb.append(" WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb.append(" AND VC.ddoCode = DM.ddoCode AND VC.regStatus IN (1) ");
		sb.append(" order by DM.ddoCode ASC ");
		
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getBillGroupsForDdo(String lStrDdoCode) throws Exception {

		StringBuilder sb = new StringBuilder();
		sb.append(" select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG where BG.dcpsDdoCode = :ddoCode ");
		sb.append(" and (BG.billDeleted is null or BG.billDeleted <> 'Y') and (BG.billDcps is null or BG.billDcps <> 'Y')");
		sb.append(" order by BG.dcpsDdoBillDescription ASC");
		List<Object> lLstReturnList = null;
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}
	
	public List getBillGroupsRegularForDdoInDDOAsstLogin(String lStrDdoCode) throws Exception {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG where BG.dcpsDdoCode = :ddoCode ");
		sb.append(" and (BG.billDeleted is null or BG.billDeleted <> 'Y') and (BG.billDcps is null or BG.billDcps <> 'Y') ");
		sb.append(" order by BG.dcpsDdoBillDescription ASC ");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getBillGroupsRejectedForDdo(String lStrDdoCode)
			throws Exception {

		String query = "select distinct BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG,TrnDcpsContribution TR where BG.dcpsDdoCode = :ddoCode and BG.dcpsDdoBillGroupId = TR.dcpsDdoBillGroupId and TR.regStatus = -3 order by BG.dcpsDdoBillDescription ";
		// voucher status -3 is meant for rejected contributions
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getBillGroupsRejectedForDdoInATOLogin(String lStrDdoCode)
			throws Exception {

		String query = "select distinct BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG,TrnDcpsContribution TR where BG.dcpsDdoCode = :ddoCode and BG.dcpsDdoBillGroupId = TR.dcpsDdoBillGroupId and TR.regStatus = -4 order by BG.dcpsDdoBillDescription ";
		// voucher status -3 is meant for rejected contributions
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getApprovedBillGroupsForDdoInDDOLogin(String lStrDdoCode)
			throws Exception {

		String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG,TrnDcpsContribution VC where BG.dcpsDdoCode = :ddoCode and BG.dcpsDdoBillGroupId = VC.dcpsDdoBillGroupId and VC.regStatus in (1,3) group by BG.dcpsDdoBillGroupId,BG.dcpsDdoBillDescription order by BG.dcpsDdoBillDescription ASC";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getBillGroupsForDdoInDDOLogin(String lStrDdoCode)
			throws Exception {

		String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG,TrnDcpsContribution TR where BG.dcpsDdoCode = :ddoCode and BG.dcpsDdoBillGroupId = TR.dcpsDdoBillGroupId and TR.regStatus = 2 group by BG.dcpsDdoBillGroupId,BG.dcpsDdoBillDescription order by BG.dcpsDdoBillDescription desc";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getBillGroupsForDdoInTOLogin(String lStrDdoCode)
			throws Exception {

		//String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG,TrnDcpsContribution VC where BG.dcpsDdoCode = :ddoCode and BG.dcpsDdoBillGroupId = VC.dcpsDdoBillGroupId and VC.regStatus IN (1,3,4) group by BG.dcpsDdoBillGroupId,BG.dcpsDdoBillDescription order by BG.dcpsDdoBillDescription ASC ";
		// Bills for approved vouchers not shown
		String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG,TrnDcpsContribution VC where BG.dcpsDdoCode = :ddoCode and BG.dcpsDdoBillGroupId = VC.dcpsDdoBillGroupId and VC.regStatus IN (3) group by BG.dcpsDdoBillGroupId,BG.dcpsDdoBillDescription order by BG.dcpsDdoBillDescription ASC ";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getApprovedBillGroupsForDdo(String lStrDdoCode,
			Long lLongMonthId, Long lLongYearId) throws Exception {

		String query = "SELECT DISTINCT BG.BILL_GROUP_ID, BG.DESCRIPTION FROM mst_dcps_bill_group BG JOIN mst_dcps_contri_voucher_dtls VC "
				+ " ON BG.bill_group_id = VC.bill_group_id JOIN TRN_DCPS_CONTRIBUTION TR ON TR.RLT_CONTRI_VOUCHER_ID = VC.MST_DCPS_CONTRI_VOUCHER_DTLS" 
				+ " WHERE (VC.voucher_status IN (-2) OR TR.REG_STATUS = 1)  AND VC.ddo_code = '"
				+ lStrDdoCode
				+ "'"
				+ " AND VC.year_id = "
				+ lLongYearId
				+ " AND VC.month_id = " + lLongMonthId;
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getDdoNameFromDdoCode(String lStrDdoCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT DM.ddoCode, DM.ddoName FROM  OrgDdoMst DM ");
		sb.append("WHERE  DM.ddoCode = :ddoCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public String getInitUnitPostIdForContriIdOnline(String dcpsContriId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String postIdDDOAsst;

		lSBQuery
				.append(" SELECT init_unit FROM wf_job_mst WHERE job_ref_id = '"
						+ dcpsContriId + "'   AND (doc_id IN ('700006'))");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		postIdDDOAsst = tempList.get(0).toString();
		return postIdDDOAsst;
	}

	public String getInitUnitPostIdForContriIdManual(String dcpsContriId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String postIdDDOAsst;

		lSBQuery
				.append(" SELECT init_unit FROM wf_job_mst WHERE job_ref_id = '"
						+ dcpsContriId + "'   AND (doc_id IN ('700003'))");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		postIdDDOAsst = tempList.get(0).toString();
		return postIdDDOAsst;
	}

	public Integer getRegStatusForContriId(String dcpsContriId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = null;
		Integer regStatus = null;

		lSBQuery
				.append(" SELECT REG_STATUS FROM TRN_DCPS_CONTRIBUTION WHERE dcps_contribution_id = '"
						+ dcpsContriId + "'");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			if (tempList.get(0) != null) {
				regStatus = Integer.parseInt(tempList.get(0).toString());
			}
		}
		return regStatus;
	}

	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtls(Long yearId,
			Long monthId, String ddoCode, Long billGroupId) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Query lQuery = null;

		lSBQuery.append("FROM MstDcpsContriVoucherDtls");
		lSBQuery
				.append(" WHERE yearId = :yearId and  monthId= :monthId and ddoCode= :ddoCode and billGroupId= :billGroupId");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("billGroupId", billGroupId);
		
		List tempList = null;
		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) tempList.get(0);
		}

		return lObjMstDcpsContriVoucherDtls;
	}
	
	public List getVoucherNoAndDateForInputDtls(Long yearId,
			Long monthId, String ddoCode, Long billGroupId) {

		StringBuilder lSBQuery = new StringBuilder();
		List lListVoucherNoAndDate = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT CV.voucherNo,CV.voucherDate FROM MstDcpsContriVoucherDtls CV, TrnDcpsContribution TR");
		lSBQuery.append(" WHERE CV.dcpsContriVoucherDtlsId = TR.rltContriVoucherId ");
		lSBQuery.append(" AND TR.finYearId = :yearId and TR.monthId = :monthId and TR.ddoCode= :ddoCode and TR.dcpsDdoBillGroupId = :billGroupId");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("billGroupId", billGroupId);
		
		lListVoucherNoAndDate = lQuery.list();

		return lListVoucherNoAndDate;
	}

	public MstDcpsContriVoucherDtls getContriApprovedVoucherVOForInputDtls(
			Long yearId, Long monthId, String ddoCode, Long billGroupId) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Query lQuery = null;

		lSBQuery.append("FROM MstDcpsContriVoucherDtls");
		lSBQuery
				.append(" WHERE yearId = :yearId and  monthId= :monthId and ddoCode= :ddoCode and billGroupId= :billGroupId and voucherStatus = 1");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("billGroupId", billGroupId);
		
		List tempList = null;
		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) tempList.get(0);
		}

		return lObjMstDcpsContriVoucherDtls;
	}

	public MstDcpsContriVoucherDtls checkContributionsForPrvsMonth(Long yearId,
			Long monthId, String ddoCode, Long billGroupId) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Query lQuery = null;

		lSBQuery.append("FROM MstDcpsContriVoucherDtls");
		lSBQuery
				.append(" WHERE yearId = :yearId and  monthId= :monthId and ddoCode= :ddoCode and billGroupId= :billGroupId and voucherStatus != -3");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("billGroupId", billGroupId);

		List tempList = null;
		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) tempList.get(0);
		}

		return lObjMstDcpsContriVoucherDtls;
	}

	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtlsForPopup(
			Long yearId, Long monthId, String ddoCode, Long billGroupId,
			Long treasuryCode) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Query lQuery = null;

		lSBQuery.append("FROM MstDcpsContriVoucherDtls");
		lSBQuery
				.append(" WHERE yearId = :yearId and  monthId= :monthId and ddoCode= :ddoCode and billGroupId= :billGroupId and treasuryCode= :treasuryCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("billGroupId", billGroupId);
		lQuery.setParameter("treasuryCode", treasuryCode);
		
		List tempList = null;
		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) tempList.get(0);
		}

		return lObjMstDcpsContriVoucherDtls;
	}

	public void revertRequestAndUpdtVoucherVO(Long year, Long month,
			Long billGroupId, Long treasuryCode, Long voucherNo,
			String reasonForRevert) {

		StringBuilder lSBQuery = null;
		Query lQuery = null;

		lSBQuery = new StringBuilder();
		lSBQuery
				.append("UPDATE MstDcpsContriVoucherDtls CV SET CV.reasonForReversion = :reasonForRevert , CV.reversionFlag = -1");
		lSBQuery
				.append(" WHERE CV.yearId = :year  AND CV.monthId = :month AND CV.billGroupId = :billGroupId");
		lSBQuery
				.append(" AND CV.treasuryCode = :treasuryCode ");
		
		if(voucherNo != null)
		{
			lSBQuery.append("  AND CV.voucherNo = :voucherNo ");
		}
		
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setLong("year", year);
		lQuery.setLong("month", month);
		lQuery.setLong("billGroupId", billGroupId);
		lQuery.setLong("treasuryCode", treasuryCode);
		
		if(voucherNo != null)
		{
			lQuery.setParameter("voucherNo", voucherNo);
		}
		lQuery.setParameter("reasonForRevert", reasonForRevert);

		lQuery.executeUpdate();

	}

	public List getAllRevertRequestsForSRKA() {

		List listReversionRequests = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT CV.dcpsContriVoucherDtlsId, CV.ddoCode,FM.monthName,FY.finYearCode,BG.dcpsDdoBillDescription,BG.dcpsDdoBillSchemeName,");
		lSBQuery.append(" CV.voucherNo, CV.voucherDate,CV.reasonForReversion,FY.finYearDesc ");
		lSBQuery
				.append(" FROM MstDcpsContriVoucherDtls CV,MstDcpsBillGroup BG,SgvcFinYearMst FY,SgvaMonthMst FM");
		lSBQuery
				.append(" WHERE CV.billGroupId = BG.dcpsDdoBillGroupId AND CV.monthId=FM.monthId AND CV.yearId=FY.finYearId ");
		lSBQuery.append(" AND CV.reversionFlag = -1");

		lQuery = ghibSession.createQuery(lSBQuery.toString());

		listReversionRequests = lQuery.list();

		return listReversionRequests;

	}

	public List getAllRevertRequestsForTreasury(String lStrTreasuryLocCode) {

		List listReversionRequests = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT CV.dcpsContriVoucherDtlsId, CV.ddoCode,FM.monthName,FY.finYearCode,BG.dcpsDdoBillDescription,BG.dcpsDdoBillSchemeName,");
		lSBQuery
				.append(" CV.voucherNo, CV.voucherDate,CV.reasonForReversion,CV.reversionFlag,BG.dcpsDdoBillSchemeName,CV.monthId,CV.yearId,CV.billGroupId,BG.dcpsDdoSchemeCode,FY.finYearDesc ");
		lSBQuery
				.append(" FROM MstDcpsContriVoucherDtls CV,MstDcpsBillGroup BG,SgvcFinYearMst FY,SgvaMonthMst FM");
		lSBQuery
				.append(" WHERE CV.billGroupId = BG.dcpsDdoBillGroupId AND CV.monthId=FM.monthId AND CV.yearId=FY.finYearId ");
		lSBQuery.append(" AND CV.reversionFlag IN (-1,-2,2)");

		lQuery = ghibSession.createQuery(lSBQuery.toString());

		listReversionRequests = lQuery.list();

		return listReversionRequests;

	}

	public Double getTotalVoucherAmountForGivenVoucher(Long monthId,
			Long finYearId, Long billGroupId, String ddoCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = null;
		Double voucherAmount = null;

		lSBQuery
				.append(" SELECT SUM(CONTRIBUTION) FROM trn_dcps_contribution WHERE month_id = "
						+ monthId
						+ " AND fin_year_id ="
						+ finYearId
						+ " AND bill_group_id ="
						+ billGroupId
						+ " AND ddo_code = '" + ddoCode + "'");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		if (tempList.get(0) != null) {
			voucherAmount = Double.parseDouble(tempList.get(0).toString());
		}
		return voucherAmount;
	}

	public Boolean checkContriOfEmpForSelectedPeriod(Long dcpsEmpId,
			Date contriStartDate, Date contriEndDate, String typeOfPayment,String lStrDDOCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List<TrnDcpsContribution> lListTrnDcpsContribution = null;
		Boolean flag = true;
		
		lSBQuery
				.append(" FROM TrnDcpsContribution WHERE dcpsEmpId = :dcpsEmpId AND  ");
		lSBQuery
				.append(" ((:contriStartDate between startDate and endDate) OR (:contriEndDate between startDate and endDate))");
		if (typeOfPayment.equals("700046") || typeOfPayment.equals("700047")) {
			lSBQuery
					.append(" AND (typeOfPayment = '700046' OR typeOfPayment = '700047')");
		} else {
			lSBQuery.append(" AND typeOfPayment = :typeOfPayment ");
		}
		if(lStrDDOCode != null)
		{
			if(!"".equals(lStrDDOCode))
			{
				lSBQuery.append(" AND ddoCode = :ddoCode ");
			}
		}
		
		lSBQuery.append(" AND regStatus in (1,3,4,2)");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("contriStartDate", contriStartDate);
		lQuery.setParameter("contriEndDate", contriEndDate);
		if(lStrDDOCode != null)
		{
			if(!"".equals(lStrDDOCode))
			{
				lQuery.setParameter("ddoCode", lStrDDOCode);
			}
		}
		
		
		if (!(typeOfPayment.equals("700046") || typeOfPayment.equals("700047"))) {
			lQuery.setParameter("typeOfPayment", typeOfPayment);
		}
		lListTrnDcpsContribution = lQuery.list();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//String strt=new Date(15,1,01);
		//String end=new Date(15,9,30);
		Date strts = null;
		Date strts16 = null;
		try {
			strts = sdf.parse("01/07/2015");
			strts16 = sdf.parse("01/07/2016");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Date ends = null;
		Date ends16 = null;
		try {
			ends = sdf.parse("30/09/2015");
			ends16 = sdf.parse("31/03/2017");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date endl = null;
	    Date endl7 = null;
	    try
	    {
	      endl = sdf.parse("01/01/2017");
	      endl7 = sdf.parse("31/01/2018");
	    }
	    catch (ParseException e)
	    {
	      e.printStackTrace();
	    }
	    Date end2 = null;
	    Date endl8 = null;
	    try
	    {
	      end2 = sdf.parse("01/01/2018");
	      endl8 = sdf.parse("30/09/2018");
	    }
	    catch (ParseException e)
	    {
	      e.printStackTrace();
	    }
		
		int comparison1 = contriStartDate.compareTo(strts);
		int comparison2 = contriEndDate.compareTo(ends);
		int comparison3 = contriStartDate.compareTo(strts16);
		int comparison4 = contriEndDate.compareTo(ends16);
		
		int comparison5 = contriStartDate.compareTo(endl);
	    int comparison6 = contriEndDate.compareTo(endl7);
	    
	    int comparison7 = contriStartDate.compareTo(end2);
	    int comparison8 = contriEndDate.compareTo(endl8);
	    
		gLogger.info("comparison1"+comparison1);
		gLogger.info("comparison2"+comparison2);
		gLogger.info("end date"+contriStartDate.getYear());
		//if(contriStartDate=='2015-01-')
		if(typeOfPayment.equals("700048")){
			gLogger.info("######typeOfPaymentif"+typeOfPayment);
			gLogger.info("######typeOfPaymentif" + typeOfPayment);
		if((comparison1==0 || comparison1==1)&&(comparison2 == 0 || comparison2==-1))
		{
			gLogger.info("######if part"+contriEndDate);
			flag = true;
			gLogger.info("######flagif"+flag);
		}
		else if((comparison3==0 || comparison3==1)&&(comparison4 == 0 || comparison4==-1))
		{
			gLogger.info("######if part"+contriEndDate);
			flag = true;
			gLogger.info("######flagif"+flag);
		}
		else if (((comparison5 == 0) || (comparison5 == 1)) && ((comparison6 == 0) || (comparison6 == -1)))
	      {
	        this.gLogger.info("###### TEST if part" + contriEndDate);
	        flag = Boolean.valueOf(true);
	        this.gLogger.info("######flagif" + flag);
	      }
	      else if (((comparison7 == 0) || (comparison7 == 1)) && ((comparison8 == 0) || (comparison8 == -1)))
	      {
	        this.gLogger.info("###### TEST if part" + contriEndDate);
	        flag = Boolean.valueOf(true);
	        this.gLogger.info("######flagif" + flag);
	      }
		else
		{
			gLogger.info("######else part"+contriEndDate);
		if (lListTrnDcpsContribution != null
				&& lListTrnDcpsContribution.size() != 0) {
			flag = false;
			
		}gLogger.info("######flagesle"+flag);
		}
		}else{
			gLogger.info("######typeOfPaymentelse"+typeOfPayment);
			if (lListTrnDcpsContribution != null
					&& lListTrnDcpsContribution.size() != 0) {
				flag = false;
				
			}
			gLogger.info("######flagelsess"+flag);
		}
		return flag;
	}
	
	
	
	public Boolean checkContriOfEmpForSelectedPeriod_new(Long dcpsEmpId,
			Date contriStartDate, Date contriEndDate, String typeOfPayment,String lStrDDOCode,String payCommission) {
//pay arrear =700049
//delayed = 700047
//DA Arrears=700048
//regular=700046		
		StringBuilder lSBQuery = new StringBuilder();
		List<TrnDcpsContribution> lListTrnDcpsContribution = null;
		Boolean flag = true;
		
		lSBQuery
				.append(" FROM TrnDcpsContribution WHERE dcpsEmpId = :dcpsEmpId AND  ");
		lSBQuery
				.append(" ((:contriStartDate between startDate and endDate) OR (:contriEndDate between startDate and endDate)) and  (:contriStartDate between startDate and endDate)");
		if (typeOfPayment.equals("700046") || typeOfPayment.equals("700047")) {
			lSBQuery
					.append(" AND (typeOfPayment = '700046' OR typeOfPayment = '700047')");
		} else {
			lSBQuery.append(" AND typeOfPayment = :typeOfPayment ");
		}
		if(lStrDDOCode != null)
		{
			if(!"".equals(lStrDDOCode))
			{
				lSBQuery.append(" AND ddoCode = :ddoCode ");
			}
		}
		if(payCommission != null && payCommission.equals("700349"))
		{
		if(!"".equals(payCommission) && payCommission.equals("700349"))
		{
		lSBQuery.append(" AND payCommission = :payCommission ");
		}
		}
		lSBQuery.append(" AND regStatus in (1,3,4,2)");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("contriStartDate", contriStartDate);
		lQuery.setParameter("contriEndDate", contriEndDate);
		if(lStrDDOCode != null)
		{
			if(!"".equals(lStrDDOCode))
			{
				lQuery.setParameter("ddoCode", lStrDDOCode);
			}
		}
		
		if(payCommission != null && payCommission.equals("700349"))
		{
		if(!"".equals(payCommission) && payCommission.equals("700349"))
		{
		lQuery.setParameter("payCommission", payCommission);
		}
		}
		if (!(typeOfPayment.equals("700046") || typeOfPayment.equals("700047"))) {
			if(typeOfPayment.equals("700080"))////PayArrearDiff $t 23-2-2021
			lQuery.setParameter("typeOfPayment", "700049");
			else
				lQuery.setParameter("typeOfPayment", typeOfPayment);
		}
		lListTrnDcpsContribution = lQuery.list();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//String strt=new Date(15,1,01);
		//String end=new Date(15,9,30);
		Date strts = null;
		Date strts16 = null;
		try {
			strts = sdf.parse("01/07/2015");
			strts16 = sdf.parse("01/07/2016");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Date ends = null;
		Date ends16 = null;
		try {
			ends = sdf.parse("30/09/2015");
			ends16 = sdf.parse("31/03/2017");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date endl = null;
	    Date endl7 = null;
	    try
	    {
	      endl = sdf.parse("01/01/2017");
	      endl7 = sdf.parse("31/01/2018");
	    }
	    catch (ParseException e)
	    {
	      e.printStackTrace();
	    }
	    Date end2 = null;
	    Date endl8 = null;
	    try
	    {
	      end2 = sdf.parse("01/01/2018");
	      endl8 = sdf.parse("30/09/2018");
	    }
	    catch (ParseException e)
	    {
	      e.printStackTrace();
	    }
	    Date strts19 = null;
	    Date end19 = null;
	    try
	    {
	    	strts19 = sdf.parse("01/07/2018");
	    	end19 = sdf.parse("30/06/2019");
	    }
	    catch (ParseException e)
	    {
	      e.printStackTrace();
	    }
		
		int comparison1 = contriStartDate.compareTo(strts);
		int comparison2 = contriEndDate.compareTo(ends);
		int comparison3 = contriStartDate.compareTo(strts16);
		int comparison4 = contriEndDate.compareTo(ends16);
		
		int comparison5 = contriStartDate.compareTo(endl);
	    int comparison6 = contriEndDate.compareTo(endl7);
	    
	    int comparison7 = contriStartDate.compareTo(end2);
	    int comparison8 = contriEndDate.compareTo(endl8);
	    
	    int comparison9 = contriStartDate.compareTo(strts19);//1
	    int comparison10 = contriEndDate.compareTo(end19);//1
	    
		gLogger.info("comparison1"+comparison1);
		gLogger.info("comparison2"+comparison2);
		gLogger.info("end date"+contriStartDate.getYear());
		//if(contriStartDate=='2015-01-')
		if(typeOfPayment.equals("700048")){
			gLogger.info("######typeOfPaymentif"+typeOfPayment);
			gLogger.info("######typeOfPaymentif" + typeOfPayment);
		if((comparison1==0 || comparison1==1)&&(comparison2 == 0 || comparison2==-1))
		{
			gLogger.info("######if part"+contriEndDate);
			flag = true;
			gLogger.info("######flagif"+flag);
		}
		else if((comparison3==0 || comparison3==1)&&(comparison4 == 0 || comparison4==-1))
		{
			gLogger.info("######if part"+contriEndDate);
			flag = true;
			gLogger.info("######flagif"+flag);
		}
		else if (((comparison5 == 0) || (comparison5 == 1)) && ((comparison6 == 0) || (comparison6 == -1)))
	      {
	        this.gLogger.info("###### TEST if part" + contriEndDate);
	        flag = Boolean.valueOf(true);
	        this.gLogger.info("######flagif" + flag);
	      }
	      else if (((comparison7 == 0) || (comparison7 == 1)) && ((comparison8 == 0) || (comparison8 == -1)))
	      {
	        this.gLogger.info("###### TEST if part" + contriEndDate);
	        flag = Boolean.valueOf(true);
	        this.gLogger.info("######flagif" + flag);
	      }
	      else if (((comparison9 == 0) || (comparison9 == 1)) && ((comparison10 == 0) || (comparison10 == -1)))//$t 2019
	      {
	        this.gLogger.info("###### TEST if part" + contriEndDate);
	        flag = Boolean.valueOf(true);
	        this.gLogger.info("######flagif" + flag);
	      }
		else
		{
			gLogger.info("######else part"+contriEndDate);
		if (lListTrnDcpsContribution != null
				&& lListTrnDcpsContribution.size() != 0) {
			flag = false;
			
		}gLogger.info("######flagesle"+flag);
		}
		}else{
			gLogger.info("######typeOfPaymentelse"+typeOfPayment);
			if (lListTrnDcpsContribution != null
					&& lListTrnDcpsContribution.size() != 0) {
				flag = false;
				
			}
			gLogger.info("######flagelsess"+flag);
		}
		return flag;
	}
	
	
	
	

	public Float getDARateForPayCommission(String payCommission) {

		List<Float> lListDARate = null;

		Float DARate = 0f;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT daRate FROM DARate WHERE payCommission= :payCommission and status = 1");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("payCommission", payCommission);
		lListDARate = lQuery.list();

		if (lListDARate.size() != 0 && lListDARate != null) {
			if (lListDARate.get(0) != null) {
				DARate = lListDARate.get(0);
			}
		}
		return DARate;
	}

	public Double getDelayedContribution(Long lLngDcpsEmpId, Long lLngYearId,
			Long lLngMonthId) {
		
		List<Double> lListDelayedContris = null;

		Double DelayedContribution = 0D;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT SUM(TR.contribution) FROM TrnDcpsContribution TR WHERE TR.dcpsEmpId = :dcpsEmpId AND TR.typeOfPayment = :typeOfPayment");
		lSBQuery.append(" AND TR.finYearId = :finYearId AND TR.monthId = :monthId");
		lSBQuery.append(" AND TR.regStatus = 1");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", lLngDcpsEmpId);
		lQuery.setParameter("typeOfPayment", "700047");
		lQuery.setParameter("finYearId", lLngYearId);
		lQuery.setParameter("monthId", lLngMonthId);
		lListDelayedContris = lQuery.list();

		if (lListDelayedContris.size() != 0 && lListDelayedContris != null) {
			if (lListDelayedContris.get(0) != null) {
				DelayedContribution = lListDelayedContris.get(0);
			} else {
				DelayedContribution = 0D;
			}
		} else {
			DelayedContribution = 0D;
		}
		return DelayedContribution;
	}
	
	public Double getDelayedContributionMatched(Long lLngDcpsEmpId, Long lLngYearId,
			Long lLngMonthId) {
		
		List<Double> lListDelayedContris = null;

		Double DelayedContribution = 0D;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT SUM(TR.contribution) FROM TrnDcpsContribution TR WHERE TR.dcpsEmpId = :dcpsEmpId AND TR.typeOfPayment = :typeOfPayment");
		lSBQuery.append(" AND TR.finYearId = :finYearId AND TR.monthId = :monthId");
		lSBQuery.append(" AND TR.regStatus = 1");
		lSBQuery.append(" AND TR.employerContriFlag = 'Y'");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", lLngDcpsEmpId);
		lQuery.setParameter("typeOfPayment", "700047");
		lQuery.setParameter("finYearId", lLngYearId);
		lQuery.setParameter("monthId", lLngMonthId);
		lListDelayedContris = lQuery.list();

		if (lListDelayedContris.size() != 0 && lListDelayedContris != null) {
			if (lListDelayedContris.get(0) != null) {
				DelayedContribution = lListDelayedContris.get(0);
			} else {
				DelayedContribution = 0D;
			}
		} else {
			DelayedContribution = 0D;
		}
		return DelayedContribution;
	}

	public Double getDARateForGivenPrd(Date contriStartDate,
			Date contriEndDate, String lStrPayCommission) {

		StringBuilder lSBQuery = new StringBuilder();
		Double ldoubleDARate = 0d;

		lSBQuery.append(" SELECT daRate FROM DARate WHERE ");
		lSBQuery
				.append(" ( (:contriStartDate between effectiveFromDate and applicableToDate) OR (:contriStartDate >= effectiveFromDate AND applicableToDate IS NULL) )");
		lSBQuery.append(" AND payCommission = :payCommission");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("contriStartDate", contriStartDate);
		lQuery.setParameter("payCommission", lStrPayCommission.trim());
		// End Date not used here.
		// lQuery.setParameter("contriEndDate", contriEndDate);
		if (lQuery.list() != null) {
			if (lQuery.list().get(0) != null) {
				ldoubleDARate = Double.parseDouble(lQuery.list().get(0)
						.toString());
			}
		}
		return ldoubleDARate;
	}
	
	public MstDcpsContriVoucherDtls fetchMstContriVoucherDtls(Long lLngVoucherContriId,Map objectArgs){
		
		MstDcpsContriVoucherDtls mstDcpsContriVoucherDtls ;
		Session currSession =(Session)objectArgs.get("currentSessionForDcps");
		String lStrQuery = "select MST from MstDcpsContriVoucherDtls MST where MST.dcpsContriVoucherDtlsId = " + lLngVoucherContriId;
		Query lQuery = currSession.createQuery(lStrQuery);
		mstDcpsContriVoucherDtls =(MstDcpsContriVoucherDtls)lQuery.uniqueResult();
		return mstDcpsContriVoucherDtls;
	}
	
	public TrnDcpsContribution fetchTrnDcpsContribution(Long lLngTrnDcpsContributionId,Map objectArgs){
		
		TrnDcpsContribution trnDcpsContritbution ;
		Session currSession =(Session)objectArgs.get("currentSessionForDcps");
		String lStrQuery = "select TRN from TrnDcpsContribution TRN where TRN.dcpsContributionId = " + lLngTrnDcpsContributionId;
		Query lQuery = currSession.createQuery(lStrQuery);
		trnDcpsContritbution =(TrnDcpsContribution)lQuery.uniqueResult();
		return trnDcpsContritbution;
	}
	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtlsSchdlr(Long yearId,Long monthId, String ddoCode, Long billGroupId,Map objectArgs) {

		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Session currSession =(Session)objectArgs.get("currentSessionForDcps");
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT CV FROM MstDcpsContriVoucherDtls CV");
		lSBQuery.append(" WHERE yearId = :yearId and monthId= :monthId and ddoCode= :ddoCode and billGroupId= :billGroupId");
		Query lQuery = currSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("billGroupId", billGroupId);
		
		List tempList = null;
		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) tempList.get(0);
		}

		return lObjMstDcpsContriVoucherDtls;
	}
	
	public List getEmpListForContributionFinal(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId, Map displayTag,
			String lDtFirstDate, String lStrTypeOfPaymentMaster,Long delayedMonthId,Long delayedYearId,String lDtLastDate,String lStrFirstDateMainMonth,String schemeCode) {

		List empList = null;
		List finalList = new ArrayList();
		
		if(lStrDDOCode != null)
		{
			lStrDDOCode = lStrDDOCode.trim();
		}	 
		gLogger.error("lStrDDOCode is *********"+lStrDDOCode);
		 gLogger.error("lDtFirstDate is *********"+lDtFirstDate);
		 gLogger.error("lLongbillGroupId is *********"+lLongbillGroupId);

		String[] columnValues = null;
		columnValues = new String[] { "EM.EMP_NAME", "EM.DCPS_ID" };
		
		Double lDoubleDefaultDArateForNon5th6thPC = 58d;
		Date lDtCurrDate = SessionHelper.getCurDate();

		try {

			StringBuilder SBQuery = new StringBuilder();
			
			if(!(lStrUser.equals("ATO")))
			{
				SBQuery.append("SELECT EM.DCPS_EMP_ID,EM.DCPS_ID,EM.EMP_NAME,EM.PAY_COMMISSION,case when em.SEVEN_PC_BASIC > 0 then nvl(CO.BASIC_PAY,EM.SEVEN_PC_BASIC) else nvl(CO.BASIC_PAY,EM.BASIC_PAY) end,nvl(CO.DCPS_CONTRIBUTION_ID,0),nvl(CO.TYPE_OF_PAYMENT,'"
								+ lStrTypeOfPaymentMaster.trim()
								+ "'),nvl(CO.MONTH_ID,0),nvl(CO.FIN_YEAR_ID,0),nvl(DA.DA_RATE,"+ lDoubleDefaultDArateForNon5th6thPC + "),CO.REG_STATUS,EM.DOJ,CO.DA,CO.DP,CO.CONTRIBUTION," );
				
				// HST_DCPS_EMP_DETAILS join removed
				/*
				if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
					{
						SBQuery.append(" case when to_date(to_char(HE.START_DATE,'yyyy-MM-DD'),'yyyy-MM-DD') > to_date(to_char('"+ lDtFirstDate + "','yyyy-MM-DD'),'yyyy-MM-DD') then to_date(to_char(HE.START_DATE,'yyyy-MM-DD'),'yyyy-MM-DD') else  CO.startDate end StartDate");
					}
				else
					{
						SBQuery.append(" CO.startDate StartDate");
					}
				*/      //$t 2019  CO.CONTRIBUTION_EMPLR
						SBQuery.append(" CO.startDate StartDate");
						SBQuery.append(",CO.endDate,CO.status,CO.CONTRIBUTION_EMPLR FROM mst_dcps_emp EM " );
							
				// HST_DCPS_EMP_DETAILS join removed
						
				/*
				if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))		
					{
						SBQuery.append(" INNER JOIN HST_DCPS_EMP_DETAILS HE on EM.DCPS_EMP_ID=HE.DCPS_EMP_ID and HE.END_DATE is null and HE.ddo_code = EM.ddo_code and HE.ddo_code = '" + lStrDDOCode + "' and (to_date(to_char('" + lDtLastDate + "','yyyy-MM-DD'),'yyyy-MM-DD'))  > to_date(to_char(HE.START_DATE,'yyyy-MM-dd'),'yyyy-MM-DD' )");
					}
				*/
							
						SBQuery.append(" LEFT OUTER JOIN TRN_DCPS_CONTRIBUTION CO ON EM.DCPS_EMP_ID=CO.DCPS_EMP_ID AND CO.MONTH_ID="+ monthId+ " AND CO.FIN_YEAR_ID=" + yearId + " AND CO.DDO_CODE = '" + lStrDDOCode + "'" );
			}

			if (
					(!(lStrUser.equals("TO")))
					&& (!(lStrUser.equals("DDO") && (lStrUse.equals("ViewApproved")	|| (lStrUse.equals("ViewForwarded")))))
					&& (!(lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))))
					&& (!((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && lStrUse.equals("ViewRejected")))
				) {
				SBQuery.append(" AND CO.TYPE_OF_PAYMENT = '"
						+ lStrTypeOfPaymentMaster.trim() + "'");
			}
			
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")) && lStrTypeOfPaymentMaster.equals("700047") )
			{
				if(delayedMonthId != null && delayedYearId != null)
				{
					SBQuery.append(" AND CO.DELAYED_MONTH_ID = " + delayedMonthId);
					SBQuery.append(" AND CO.DELAYED_FIN_YEAR_ID = " + delayedYearId);
				}
			}

			//SBQuery.append(" LEFT OUTER JOIN mst_dcps_contri_voucher_dtls CV ON CV.treasury_code=CO.TREASURY_CODE AND CV.month_id=CO.MONTH_ID AND CV.year_id = CO.FIN_YEAR_ID AND CV.bill_group_id=CO.BILL_GROUP_ID AND CV.ddo_code = CO.ddo_code");

			// Above line commented as there were unnecessary joins and only rlt_contri_voucher_id's join is enough
			
			SBQuery.append(" LEFT OUTER JOIN mst_dcps_contri_voucher_dtls CV ON CO.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS");

			// Temporarily removed...
			/*
			
			if (!((lStrUser.equals("ATO") || lStrUser.equals("DDOAsst") || lStrUser.equals("DDO")) && lStrUse.equals("ViewAll"))) {
				if (!(lStrUser.equals("DDO") && lStrUse.equals("ViewApproved"))) {
					SBQuery
							.append(" LEFT OUTER JOIN wf_job_mst WF ON WF.job_ref_id=CO.DCPS_CONTRIBUTION_ID AND WF.lst_act_post_id = '"
									+ lStrPostId + "'");
				}

			}
			
			*/

			SBQuery
					.append(" LEFT JOIN MST_DCPS_DA_RATE DA ON DA.PAY_COMMISSION = EM.PAY_COMMISSION AND  (('"
							+ lDtFirstDate
							+ "' BETWEEN DA.EFFECTIVE_FROM AND DA.APPLICABLE_TO) OR ('"
							+ lDtFirstDate
							+ "' >= DA.EFFECTIVE_FROM and DA.APPLICABLE_TO IS NULL)) ");
			
			// Code Added to show employees of valid post and service only for first online contribution entry
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" join org_emp_mst OE on OE.emp_id = EM.ORG_EMP_MST_ID");
				SBQuery.append(" join org_userpost_rlt OUP on OUP.user_id = OE.user_id and OUP.activate_flag = 1 ");
				SBQuery.append(" join org_post_mst OP on OP.post_id = OUP.post_id and OP.ACTIVATE_FLAG = 1");
			}
			
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" join hr_eis_emp_mst HRS on HRS.EMP_MPG_ID = OE.emp_id");
				SBQuery.append(" join HR_EIS_EMP_COMPONENT_GRP_MST HRCGM on HRCGM.EMP_ID = HRS.emp_id and HRCGM.IS_ACTIVE = 1 ");
				SBQuery.append(" join HR_EIS_EMP_COMPONENT_MPG HRCM on HRCM.COMPO_GROUP_ID = HRCGM.EMP_COMPO_GRP_ID and HRCM.IS_ACTIVE = 1");
				SBQuery.append(" join HR_PAY_DEDUC_TYPE_MST HPDT on HPDT.DEDUC_CODE = HRCM.COMPO_ID AND");
				
				if(lStrTypeOfPaymentMaster.equals("700046"))  // Regular 
				{
					SBQuery.append(" HPDT.DEDUC_CODE = 59 ");
				}
				if(lStrTypeOfPaymentMaster.equals("700047"))  // Delayed 
				{
					SBQuery.append(" HPDT.DEDUC_CODE = 120 ");
				}
				if(lStrTypeOfPaymentMaster.equals("700048"))  // DA Arrear
				{
					SBQuery.append(" HPDT.DEDUC_CODE = 122 ");
				}
				if(lStrTypeOfPaymentMaster.equals("700049"))  // Pay Arrear
				{
					SBQuery.append(" HPDT.DEDUC_CODE = 121 ");
				}
				if(lStrTypeOfPaymentMaster.equals("700080"))  // PayArrearDiff $t 23-2-2021
				{
					SBQuery.append(" HPDT.DEDUC_CODE = 717 ");
				}
			}
			

			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				SBQuery.append(" WHERE EM.DDO_CODE='" + lStrDDOCode + "'");
			}
			else
			{
				SBQuery.append(" WHERE CO.DDO_CODE='" + lStrDDOCode + "'");
			}

		/*	if (lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))) {
				SBQuery.append(" AND CO.BILL_GROUP_ID=" + lLongbillGroupId);
			} else {
				SBQuery.append(" AND EM.BILLGROUP_ID=" + lLongbillGroupId);
			}
		*/	
			if((!(lLongbillGroupId.toString().equals(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"))))
					&&
			(lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll"))	
			)
			{
				SBQuery.append(" AND EM.BILLGROUP_ID=" + lLongbillGroupId);
			}
			else
			{
				SBQuery.append(" AND CO.BILL_GROUP_ID=" + lLongbillGroupId);
			}
			
			if(lStrUser.equals("TO") && lStrUse.equals("ViewApprovedByTO"))
			{
				SBQuery.append(" AND CO.SCHEME_CODE = '" + schemeCode + "' ");
			}
			
			// Code Added to show employees of valid post and service and date of joining and DCPS employee only for first online contribution entry
			if((lStrUser.equals("DDOAsst") || lStrUser.equals("ATO")) && (lStrUse.equals("ViewAll")))
			{
				//SBQuery.append(" AND EM.REG_STATUS=1 AND EM.DCPS_OR_GPF = 'Y' AND EM.DOJ <'" + lDtFirstDate+ "'");
				SBQuery.append(" AND EM.REG_STATUS=1 AND EM.DCPS_OR_GPF = 'Y' AND EM.DOJ <'" + lDtLastDate+ "'");
				
				SBQuery.append(" AND ( EM.EMP_SERVEND_DT is null or EM.EMP_SERVEND_DT > '" + lStrFirstDateMainMonth+ "' )");
				SBQuery.append(" AND (OE.EMP_SRVC_EXP is null or OE.EMP_SRVC_EXP >'" + lStrFirstDateMainMonth+ "' )");
				SBQuery.append(" AND (OP.END_DATE is null or OP.END_DATE > '" + lStrFirstDateMainMonth+ "' )");
			}

			if (!((lStrUser.equals("ATO") || lStrUser.equals("DDOAsst") || lStrUser.equals("DDO") || lStrUser.equals("TO")) && (lStrUse.equals("ViewAll") || lStrUse.equals("MissingCreditEntry")))) {

				if ((lStrUser.equals("TO")) && lStrUse.equals("ViewForwarded")) {
					//SBQuery.append(" AND CO.REG_STATUS IN (4,3,1) "); // 3-Online,4-Manual
					SBQuery.append(" AND CO.REG_STATUS IN (3) "); // 3-Online,4-Manual

				}
				
				if ((lStrUser.equals("TO")) && lStrUse.equals("ViewApprovedByTO")) {
					SBQuery.append(" AND CO.REG_STATUS = 1 "); 

				}

				if ((lStrUser.equals("TO")) && lStrUse.equals("ViewReverted")) {
					SBQuery.append(" AND CV.REVERSION_FLAG = 2 "); 

				}

				if (lStrUser.equals("DDO") && lStrUse.equals("ViewForwarded")) {
					SBQuery.append(" AND CO.REG_STATUS = 2"); // for Online
				}

				if (lStrUser.equals("DDO") && lStrUse.equals("ViewApproved")) {
					SBQuery.append(" AND CO.REG_STATUS in (1,3)"); // for Online
				}

				if (lStrUser.equals("ATO") && lStrUse.equals("ViewRejected")) {
					SBQuery.append(" AND CO.REG_STATUS = -4"); // 4 for Manual
				}

				if (lStrUser.equals("DDOAsst")
						&& lStrUse.equals("ViewRejected")) {
					SBQuery.append(" AND CO.REG_STATUS = -3"); // 3 for Online
				}
				
				// Temporarily removed

				/*
				 
				if (!(lStrUser.equals("DDO") && lStrUse.equals("ViewApproved"))) {
					SBQuery.append(" AND WF.doc_id IN (700003,700006) ");
				}
				
				*/

				
			} else {
				SBQuery.append(" AND (CO.REG_STATUS IS NULL OR CO.REG_STATUS = 0 OR CO.REG_STATUS=1 ) ");
			}
	
		
			
			SBQuery.append(" Order By \n");
			String orderString = (displayTag
					.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag
					.get(Constants.KEY_SORT_ORDER)
					: "desc");
			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				if (orderbypara == 0 || orderbypara == 1) {
					SBQuery.append(columnValues[orderbypara.intValue()] + " "
							+ orderString);
				} else {
					SBQuery.append(" EM.EMP_NAME ASC");
				}
			} else {
				SBQuery.append(" EM.EMP_NAME ASC");
			}

			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());
			//stQuery.setDate("currentDate", lDtCurrDate);
			gLogger.error("Query is ***************"+stQuery);
			empList = stQuery.list();
			Integer lInt2 = 0;
			Double BasicPay = 0d;
			Double DP = 0D;
			String lStrDP = "";
			Double DARate = 0d;
			String lStrTypeOfPayment ="";
			Double DA = 0D;
			Double employeeContribution = 0D;
			//$t 2019
			Double emplrContribution = 0D;
			//
			String lStrDA="";
			String status="";

			for (Integer lInt1 = 0; lInt1 < empList.size(); lInt1++) {
				
				Object[] tempObjectList = (Object[]) empList.get(lInt1); 
				
				//$t 2019 length + 5
				Object[] newList = new Object[tempObjectList.length + 5];
				
				lInt2 = 0;
				lInt2 = tempObjectList.length;
				
				for (lInt2 = 0; lInt2 < tempObjectList.length; lInt2++) {
					
					// Changes Basic pay to Integer value
					if(lInt2 == 4)
					{
						if(tempObjectList[lInt2] == null)
						{
							tempObjectList[lInt2] = 0;
						}
						else
						{
						
							tempObjectList[lInt2] = (int) Math.ceil(Double.parseDouble(tempObjectList[lInt2].toString()));
						}
					}
					
					newList[lInt2] = tempObjectList[lInt2];
					
				}
				BasicPay = Double.parseDouble(tempObjectList[4]
						.toString());
				
				if(tempObjectList[17]!=null)
				status=tempObjectList[17].toString();
				
				DP = 0D;
				lStrDP = "";
				if (null != tempObjectList[13]) {
					lStrDP = tempObjectList[13].toString();
				}
				if (newList[3].toString().equals("700015") || newList[3].toString().equals("700345")) {
					if (null != lStrDP && !"".equals(lStrDP)) {
						DP = Double.parseDouble(lStrDP);
					} else {
						DP = BasicPay / 2;
					}
				}
				DARate = 0.01 * Double.parseDouble(tempObjectList[9]
						.toString());
				lStrTypeOfPayment = tempObjectList[6].toString();
				DA = 0D;
				employeeContribution = 0D;
				//$t 2019
				emplrContribution = 0D;
                //
				lStrDA = "";
				
				if (null != tempObjectList[12]) {
					lStrDA = tempObjectList[12].toString();
				}
				if (lStrTypeOfPayment.equals("700048")) {
					if (tempObjectList[12] != null) {
						DA = Double.parseDouble(tempObjectList[12].toString());
					}
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
					//$t 2019
					if (tempObjectList[18] != null) {
						emplrContribution = Double.parseDouble(tempObjectList[18].toString());
					}
				} else if (lStrTypeOfPayment.equals("700049")) {
					DA = 0D;
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
					//$t 2019
					if (tempObjectList[18] != null) {
						emplrContribution = Double.parseDouble(tempObjectList[18].toString());
					}
				}else if (lStrTypeOfPayment.equals("700080")) {////PayArrearDiff $t 23-2-2021
					DA = 0D;
					if (tempObjectList[14] != null) {
						employeeContribution = Double
								.parseDouble(tempObjectList[14].toString());
					}
					//$t 2019
					if (tempObjectList[18] != null) {
						emplrContribution = Double.parseDouble(tempObjectList[18].toString());
					}
				}else {
					if (null != lStrDA && !"".equals(lStrDA)) {
						DA = Double.parseDouble(lStrDA);
					} else {
						DA = ((BasicPay + DP) * DARate);
					}
                   //$t 2019 here - && null != tempObjectList[18])
					if (null != tempObjectList[18]) {
						//$t 2019
						emplrContribution = Double.parseDouble(tempObjectList[18].toString());
					}
					if (null != tempObjectList[14]) {
						employeeContribution = Double.parseDouble(tempObjectList[14].toString());
					} else {
						if (newList[3].toString().equals("700015")) {
							employeeContribution = ((double) Math.ceil(BasicPay)+ Math.ceil(DP) + Math.round(DA)) * 0.10;
							//$t 2019
							emplrContribution = ((double) Math.ceil(BasicPay)+ Math.ceil(DP) + Math.round(DA)) * 0.14;
						} else {
							employeeContribution = ((double) Math.ceil(BasicPay) + Math.round(DA)) * 0.10;
							//$t 2019
							emplrContribution = ((double) Math.ceil(BasicPay) + Math.round(DA)) * 0.14;
						}
					}
				}//else
				DA = (double) Math.round(DA);

				employeeContribution = (double) Math.ceil(employeeContribution);
				//$t 2019
				emplrContribution = (double) Math.ceil(emplrContribution);
				
				
				
				newList[lInt2-1] = (int) Math.ceil(DP);
				newList[lInt2] = (int) Math.round(DA);
				newList[lInt2 + 1] = (int) Math.ceil(employeeContribution);

				newList[lInt2 + 2] = DARate;
				
				newList[lInt2 + 3] = status;
				//$t 2019
				newList[lInt2 + 4] = (int) Math.ceil(emplrContribution);
				
//				for (int i = 0; i < newList.length; i++) {
//					System.out.println("-->"+"i:"+i+"  "+newList[i]);
//				} 
				
				finalList.add(newList);
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		return finalList;
	}
	
	public Boolean checkEntryInTrnForRltContriVoucherId(Long lLongRltContriVoucherId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" select dcpsContributionId FROM TrnDcpsContribution WHERE rltContriVoucherId = :rltContriVoucherId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("rltContriVoucherId", lLongRltContriVoucherId);

		tempList = lQuery.list();
		if(tempList != null)
		{
			if (tempList.size() == 0) {
				flag = false;
			}
		}
		return flag;

	}
	
	public void deleteVoucherForNoContris(Long lLongMstContriVoucherId) throws Exception {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MstDcpsContriVoucherDtls where dcpsContriVoucherDtlsId = :dcpsContriVoucherDtlsId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsContriVoucherDtlsId", lLongMstContriVoucherId);
		lQuery.executeUpdate();

	}
	
	public List getRegularContributionsFromPayroll(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception  {

		StringBuilder lSBQuery = new StringBuilder();
		List lListRegularContriListFromPayroll = null;

		lSBQuery.append(" SELECT EM.dcps_emp_id, HPP.PO, HPP.D_PAY, HPP.DA, HPP.DCPS, EM.PAY_COMMISSION, HES.EMP_ID, HPP.CDA FROM HR_PAY_PAYBILL HPP");
		lSBQuery.append(" join PAYBILL_HEAD_MPG PHM on HPP.PAYBILL_GRP_ID = PHM.PAYBILL_ID and PHM.APPROVE_FLAG IN (0,5)");
		lSBQuery.append(" join hr_eis_emp_mst HES on HPP.EMP_ID = HES.EMP_ID");
		lSBQuery.append(" join MST_DCPS_EMP EM on EM.org_emp_mst_id = HES.emp_mpg_id");
		lSBQuery.append(" where ");
		lSBQuery.append(" HPP.PAYBILL_MONTH = " + lLongMonthId );
		lSBQuery.append(" and HPP.PAYBILL_YEAR = " + lLongYearIdFromPayroll);
		lSBQuery.append(" and PHM.BILL_NO = " + lLongBillGroupId);
		lSBQuery.append(" and PHM.APPROVE_FLAG IN (0,5) ");
		lSBQuery.append(" and EM.dcps_or_gpf = 'Y' ");
		
		// Code added to take only those contributions which do not have zero DCPS amount
		lSBQuery.append(" and HPP.DCPS <> 0");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListRegularContriListFromPayroll = lQuery.list();
		
		return lListRegularContriListFromPayroll;
	}
	
	public void insertRegularContributions(List lListRegularContributionsFromPayroll,Long lLongMonthId,Long lLongYearId,Long lLongBillGroupId,String lStrDDOCode,Long lLongTreasuryCode,String lStrSchemeCode,Date lDtStartDate,Date lDtEndDate,Date gDtCurDate,Long gLngPostId,Long glongLocationCode,Long lLngContriVoucherIdToBePassed,Map inputMap) throws Exception {

		
		Object[] lObjListTemp = null;
		
		Long lLongTrnDcpsContributionId = null;
		Long lLongDcpsEmpId = null;
		String lStrPayCommission = null;
		Double lDoubleBasic = null;
		Double lDoubleDP = null;
		Double lDoubleDA = null;
		Double lDoubleContribution = null;
		Object lObjTempDA = null;
		Object lObjTempDP = null;
		
		Long lLongHrEisEmpIdFromPaybill = null;
		Long lLongHrEisEmpIdFromBroken = null;
		Boolean BrokenPeriodFlagForEmployee = false;
		List lListBrokenIdPKListForEmp = null;
		Long lLongBrokenIdPk = null;
		List lListBasicStartAndEndDateForBrokenIdPK = null;
		Object[] lArrObjBasicStartAndEndDateForBrokenIdPK = null;
		
		List lListTempListDPForBrokenId = null;
		List lListTempListDAForBrokenId = null;
		
		List lListTempListCentralDAForBrokenId = null;
		
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		
		String lStrStartDateBroken = null;
		String lStrEndDateBroken = null;
		Date lDateStartDateBroken = null;
		Date lDateEndDateBroken = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		PayBillDAO lObjPayBillDao = new PayBillDAOImpl(HrPayPaybill.class,ghibSession.getSessionFactory());//added by arpan
		String lBillType = lObjPayBillDao.getBillType(lLongBillGroupId.toString());//added by arpan
		
		
		try {
		
			List lListEmpListBrokenPeriodDCPS = getEmpListOfDCPSBrokenPeriodForMonth(lLongYearId,lLongMonthId);
			
			for(Integer lInt = 0 ; lInt < lListRegularContributionsFromPayroll.size(); lInt++)
			{
				// Value set to 0
				lStrPayCommission = null;
				lDoubleBasic = 0d;
				lDoubleDP = 0d;
				lDoubleDA = 0d;
				lDoubleContribution = 0d;
				
				BrokenPeriodFlagForEmployee = false;
				
				lObjListTemp = (Object[]) lListRegularContributionsFromPayroll.get(lInt);
				
				// Checks if Broken DCPS Contribution is given to employee or not
				
				lLongHrEisEmpIdFromPaybill = Long.valueOf(lObjListTemp[6].toString());
				
				if(lListEmpListBrokenPeriodDCPS != null)
				{
					if(lListEmpListBrokenPeriodDCPS.size() != 0)
					{
						for(Integer lIntInner = 0 ; lIntInner < lListEmpListBrokenPeriodDCPS.size() ; lIntInner++)
						{
							if(lListEmpListBrokenPeriodDCPS.get(lIntInner) != null)
							{
								lLongHrEisEmpIdFromBroken = Long.valueOf(lListEmpListBrokenPeriodDCPS.get(lIntInner).toString());
								if((lLongHrEisEmpIdFromBroken.compareTo(lLongHrEisEmpIdFromPaybill) == 0))
								{
									BrokenPeriodFlagForEmployee = true;
									break;
								}
							}
						}
					}
				}
				
					if(!BrokenPeriodFlagForEmployee)
						
					{
						lLongDcpsEmpId = Long.valueOf(lObjListTemp[0].toString().trim());
						lStrPayCommission = lObjListTemp[5].toString().trim();
						
						lDoubleBasic = Double.valueOf(lObjListTemp[1].toString().trim());
						
						if(lObjListTemp[2] != null)
						{
							if(!"".equals(lObjListTemp[2].toString()))
							{
								lDoubleDP = Double.valueOf(lObjListTemp[2].toString().trim());
							}
						}
						
						if(lObjListTemp[3] != null)
						{
							if(!"".equals(lObjListTemp[3].toString()))
							{
								lDoubleDA = Double.valueOf(lObjListTemp[3].toString().trim());
							}
						}
						
						// Checks for Central DA, if it is given then takes Central DA else Normal DA
						if(lObjListTemp[7] != null)
						{
							if(!"".equals(lObjListTemp[7].toString()))
							{
								if(Long.valueOf(lObjListTemp[7].toString()) != 0l)
								{
									lDoubleDA = Double.valueOf(lObjListTemp[7].toString().trim());
								}
							}
						}
						
						lDoubleContribution = Double.valueOf(lObjListTemp[4].toString().trim());
						
							lLongTrnDcpsContributionId = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CONTRIBUTION", inputMap);
							
							lSBQuery = new StringBuilder();
							
							lSBQuery.append("INSERT INTO TRN_DCPS_CONTRIBUTION (DCPS_CONTRIBUTION_ID,DCPS_EMP_ID,TREASURY_CODE,DDO_CODE,BILL_GROUP_ID,SCHEME_CODE,PAY_COMMISSION,TYPE_OF_PAYMENT,FIN_YEAR_ID,MONTH_ID,BASIC_PAY,DP,DA,CONTRIBUTION,REG_STATUS,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STARTDATE,ENDDATE,RLT_CONTRI_VOUCHER_ID,DELAYED_FIN_YEAR_ID,DELAYED_MONTH_ID,EMPLOYER_CONTRI_FLAG,STATUS,VOUCHER_NO,VOUCHER_DATE,IS_MISSING_CREDIT,IS_CHALLAN,POST_EMPLR_CONTRI_STATUS) VALUES \n");
							lSBQuery.append("(:dcpsContributionId,:dcpsEmpId,:treasuryCode,:ddoCode,:dcpsDdoBillGroupId,:schemeCode,:payCommission,:typeOfPayment,:finYearId,:monthId,:basicPay,:DP,:DA,:contribution,:regStatus,:langId,:locId,:dbId,:createdPostId,:createdUserId,:createdDate,:updatedUserId,:updatedPostId,:updatedDate,:startDate,:endDate,:rltContriVoucherId,:delayedFinYearId,:delayedMonthId,:employerContriFlag,:status,:voucherNo,:voucherDate,:isMissingCredit,:isChallan,:postEmpContriStatus) \n"); 
				
							lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
							
							lQuery.setParameter("dcpsContributionId", lLongTrnDcpsContributionId);
							lQuery.setParameter("dcpsEmpId", lLongDcpsEmpId);
							lQuery.setParameter("treasuryCode", lLongTreasuryCode);
							lQuery.setParameter("ddoCode", lStrDDOCode);
							lQuery.setParameter("dcpsDdoBillGroupId", lLongBillGroupId);
							lQuery.setParameter("schemeCode",lStrSchemeCode);
							lQuery.setParameter("payCommission",lStrPayCommission);
							lQuery.setParameter("typeOfPayment","700046");
							lQuery.setParameter("finYearId",lLongYearId );
							lQuery.setParameter("monthId",lLongMonthId );
							lQuery.setParameter("basicPay",lDoubleBasic );
							lQuery.setParameter("DP", lDoubleDP);
							lQuery.setParameter("DA", lDoubleDA);
							lQuery.setParameter("contribution", lDoubleContribution);
							lQuery.setParameter("regStatus",3 );
							lQuery.setParameter("langId",1 );
							lQuery.setParameter("locId",glongLocationCode );
							lQuery.setParameter("dbId",99 );
							lQuery.setParameter("createdPostId",gLngPostId );
							lQuery.setParameter("createdUserId",gLngPostId );
							lQuery.setParameter("createdDate",gDtCurDate );
							lQuery.setParameter("updatedUserId",gLngPostId );
							lQuery.setParameter("updatedPostId",gLngPostId );
							lQuery.setParameter("updatedDate",gDtCurDate );
							lQuery.setParameter("startDate",lDtStartDate );
							lQuery.setParameter("endDate",lDtEndDate );
							lQuery.setParameter("rltContriVoucherId",lLngContriVoucherIdToBePassed );
							lQuery.setParameter("delayedFinYearId",null );
							lQuery.setParameter("delayedMonthId",null );
							lQuery.setParameter("employerContriFlag","N" );
							lQuery.setParameter("status",null );
							
							lQuery.setParameter("voucherNo",null );
							lQuery.setParameter("voucherDate",null );

							lQuery.setParameter("isChallan",null );
							lQuery.setParameter("isMissingCredit",null );
							lQuery.setParameter("postEmpContriStatus",0l );
							
							lQuery.executeUpdate();
						}
				
						else
						{
							// Code to Insert values from Broken Period since Employee has been given broken period DCPS
							lListBrokenIdPKListForEmp = null;
							lListBrokenIdPKListForEmp = getBrokenIdPKForEmployee(lLongHrEisEmpIdFromBroken,lLongYearId,lLongMonthId,lBillType);
							if(lListBrokenIdPKListForEmp != null)
							{
								if(lListBrokenIdPKListForEmp.size() != 0)
								{
									for(Integer lIntCntBrokenIdPk = 0 ;lIntCntBrokenIdPk < lListBrokenIdPKListForEmp.size() ; lIntCntBrokenIdPk++)
									{
										if(lListBrokenIdPKListForEmp.get(lIntCntBrokenIdPk) != null)
										{
											lListBasicStartAndEndDateForBrokenIdPK = null;
											lLongBrokenIdPk = Long.valueOf(lListBrokenIdPKListForEmp.get(lIntCntBrokenIdPk).toString());
											
											// Gets Basic, Start Date and End Date for Broken Period Cases
											lListBasicStartAndEndDateForBrokenIdPK = getBasicStartAndEndDateForBrokenIdPK(lLongBrokenIdPk);
											lArrObjBasicStartAndEndDateForBrokenIdPK = (Object[]) lListBasicStartAndEndDateForBrokenIdPK.get(0);
											
											lDoubleBasic = Double.valueOf(lArrObjBasicStartAndEndDateForBrokenIdPK[0].toString().trim());
											
											lStrStartDateBroken = lArrObjBasicStartAndEndDateForBrokenIdPK[1].toString().trim();
											lDateStartDateBroken = sdf.parse(sdf.format(sdfDate.parse(lStrStartDateBroken.trim())));
											
											lStrEndDateBroken = lArrObjBasicStartAndEndDateForBrokenIdPK[2].toString().trim();
											lDateEndDateBroken = sdf.parse(sdf.format(sdfDate.parse(lStrEndDateBroken.trim())));
											
											List lListDcpsEmpIdAndPCForEISId = null;
											Object[] lArrDcpsEmpIdAndPCForEISId = null;
											
											lListDcpsEmpIdAndPCForEISId = getDcpsEmpIdAndPCForEisId(lLongHrEisEmpIdFromBroken);
											lArrDcpsEmpIdAndPCForEISId = (Object[]) lListDcpsEmpIdAndPCForEISId.get(0);
											
											lLongDcpsEmpId = Long.valueOf(lArrDcpsEmpIdAndPCForEISId[0].toString());
											
											if(lArrDcpsEmpIdAndPCForEISId[1] != null)
											{
												lStrPayCommission = lArrDcpsEmpIdAndPCForEISId[1].toString();
											}
											logger.info("broken period pk is"+lLongBrokenIdPk);
											logger.info("lDoubleContribution*******before***"+Double.parseDouble(getDCPSValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim()));
											lDoubleContribution = Double.parseDouble(getDCPSValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim());
											logger.info("lDoubleContribution**********"+lDoubleContribution);
											lListTempListDAForBrokenId = getDAValueForBrokenPeriodIDPk(lLongBrokenIdPk);
											
											if(lListTempListDAForBrokenId != null)
											{
												if(lListTempListDAForBrokenId.size() != 0)
												{
													if(lListTempListDAForBrokenId.get(0) != null)
													{
														lObjTempDA = getDAValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0);
														if(lObjTempDA != null)
														{
															if(!"".equals(lObjTempDA))
															{
																if(Long.valueOf(lObjTempDA.toString()) != 0l)
																{
																	lDoubleDA = Double.parseDouble(getDAValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim());
																}
															}
														}
													}
												}
											}
											
											// Code added to check for Central DA if it is given, it will be taken
											
											lListTempListCentralDAForBrokenId = getCentralDAValueForBrokenPeriodIDPk(lLongBrokenIdPk);
											
											if(lListTempListCentralDAForBrokenId != null)
											{
												if(lListTempListCentralDAForBrokenId.size() != 0)
												{
													if(lListTempListCentralDAForBrokenId.get(0) != null)
													{
														lObjTempDA = getCentralDAValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0);
														if(lObjTempDA != null)
														{
															if(!"".equals(lObjTempDA))
															{
																if(Long.valueOf(lObjTempDA.toString()) != 0l)
																{
																	lDoubleDA = Double.parseDouble(getCentralDAValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim());
																}
															}
														}
													}
												}
											}
											
											// Code added to check for Central DA if it is given, it will be taken overs
											
											lListTempListDPForBrokenId = getDPValueForBrokenPeriodIDPk(lLongBrokenIdPk);
											
											if(lListTempListDPForBrokenId != null)
											{
												if(lListTempListDPForBrokenId.size() != 0)
												{
													if(lListTempListDPForBrokenId.get(0) != null)
													{
														lObjTempDP = getDPValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0);
														if(lObjTempDP != null)
														{
															lDoubleDP = Double.parseDouble(getDPValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim());
														}
													}
												}
											}
											
											// Insert code for employees having broken contributions starts
											
												lLongTrnDcpsContributionId = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CONTRIBUTION", inputMap);
												
												lSBQuery = new StringBuilder();
												
												//lSBQuery.append("INSERT INTO TRN_DCPS_CONTRIBUTION VALUES \n");
												lSBQuery.append("INSERT INTO TRN_DCPS_CONTRIBUTION (DCPS_CONTRIBUTION_ID,DCPS_EMP_ID,TREASURY_CODE,DDO_CODE,BILL_GROUP_ID,SCHEME_CODE,PAY_COMMISSION,TYPE_OF_PAYMENT,FIN_YEAR_ID,MONTH_ID,BASIC_PAY,DP,DA,CONTRIBUTION,REG_STATUS,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STARTDATE,ENDDATE,RLT_CONTRI_VOUCHER_ID,DELAYED_FIN_YEAR_ID,DELAYED_MONTH_ID,EMPLOYER_CONTRI_FLAG,STATUS,VOUCHER_NO,VOUCHER_DATE,IS_MISSING_CREDIT,IS_CHALLAN,POST_EMPLR_CONTRI_STATUS) VALUES \n");
												lSBQuery.append("(:dcpsContributionId,:dcpsEmpId,:treasuryCode,:ddoCode,:dcpsDdoBillGroupId,:schemeCode,:payCommission,:typeOfPayment,:finYearId,:monthId,:basicPay,:DP,:DA,:contribution,:regStatus,:langId,:locId,:dbId,:createdPostId,:createdUserId,:createdDate,:updatedUserId,:updatedPostId,:updatedDate,:startDate,:endDate,:rltContriVoucherId,:delayedFinYearId,:delayedMonthId,:employerContriFlag,:status,:voucherNo,:voucherDate,:isMissingCredit,:isChallan,:postEmpContriStatus) \n"); 
									
												lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
												
												lQuery.setParameter("dcpsContributionId", lLongTrnDcpsContributionId);
												lQuery.setParameter("dcpsEmpId", lLongDcpsEmpId);
												lQuery.setParameter("treasuryCode", lLongTreasuryCode);
												lQuery.setParameter("ddoCode", lStrDDOCode);
												lQuery.setParameter("dcpsDdoBillGroupId", lLongBillGroupId);
												lQuery.setParameter("schemeCode",lStrSchemeCode);
												lQuery.setParameter("payCommission",lStrPayCommission);
												lQuery.setParameter("typeOfPayment","700046");
												lQuery.setParameter("finYearId",lLongYearId );
												lQuery.setParameter("monthId",lLongMonthId );
												lQuery.setParameter("basicPay",lDoubleBasic );
												lQuery.setParameter("DP", lDoubleDP);
												lQuery.setParameter("DA", lDoubleDA);
												lQuery.setParameter("contribution", lDoubleContribution);
												lQuery.setParameter("regStatus",3 );
												lQuery.setParameter("langId",1 );
												lQuery.setParameter("locId",glongLocationCode );
												lQuery.setParameter("dbId",99 );
												lQuery.setParameter("createdPostId",gLngPostId );
												lQuery.setParameter("createdUserId",gLngPostId );
												lQuery.setParameter("createdDate",gDtCurDate );
												lQuery.setParameter("updatedUserId",gLngPostId );
												lQuery.setParameter("updatedPostId",gLngPostId );
												lQuery.setParameter("updatedDate",gDtCurDate );
												lQuery.setParameter("startDate",lDateStartDateBroken );
												lQuery.setParameter("endDate",lDateEndDateBroken );
												lQuery.setParameter("rltContriVoucherId",lLngContriVoucherIdToBePassed );
												lQuery.setParameter("delayedFinYearId",null );
												lQuery.setParameter("delayedMonthId",null );
												lQuery.setParameter("employerContriFlag","N" );
												lQuery.setParameter("status",null );
												
												lQuery.setParameter("voucherNo",null );
												lQuery.setParameter("voucherDate",null );
												
												lQuery.setParameter("isChallan",null );
												lQuery.setParameter("isMissingCredit",null );
												lQuery.setParameter("postEmpContriStatus",0l );
												lQuery.executeUpdate();
											
											// Insert code for employees having broken contributions ends
											
										}
									}
								}
							}
						}
				}
			
			} catch (Exception e) {
				e.printStackTrace();
				gLogger.error("Error is : " + e, e);
				throw (e);
			}
	}
	
	public String getSchemeCodeForBillGroup(Long lLongbillGroup) {

		String lStrSchemeCode = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT dcpsDdoSchemeCode FROM MstDcpsBillGroup WHERE dcpsDdoBillGroupId = :lLongbillGroup");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("lLongbillGroup", lLongbillGroup);
			lStrSchemeCode = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Error  while executing getSchemeCodeForBillGroup of OfflinecontriDAOImpl is "+ e);

		}
		return lStrSchemeCode;
	}
	
	public void deleteRegularContributionsIfExist(Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from TrnDcpsContribution where dcpsDdoBillGroupId = :dcpsDdoBillGroupId ");
		lSBQuery.append(" and monthId = :monthId");
		lSBQuery.append(" and finYearId = :finYearId");
		lSBQuery.append(" and ddoCode = :ddoCode");
		lSBQuery.append(" and typeOfPayment = :typeOfPayment");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("finYearId", finYearId);
		lQuery.setParameter("dcpsDdoBillGroupId", dcpsDdoBillGroupId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("typeOfPayment", "700046");
		lQuery.executeUpdate();

	}
	
	/*
	public void updateRegStatusOfAllPayTypesOnApproval(Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" update TrnDcpsContribution set regStatus = 3 where dcpsDdoBillGroupId = :dcpsDdoBillGroupId ");
		lSBQuery.append(" and monthId = :monthId");
		lSBQuery.append(" and finYearId = :finYearId");
		lSBQuery.append(" and ddoCode = :ddoCode");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("finYearId", finYearId);
		lQuery.setParameter("dcpsDdoBillGroupId", dcpsDdoBillGroupId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.executeUpdate();

	}
	*/
	
	public List getEmpListForDelayedTypesInMonthAndBG(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListDelayedTypeEmpListFromPayroll = null;

		lSBQuery.append(" SELECT EM.dcps_emp_id FROM HR_PAY_PAYBILL HPP");
		lSBQuery.append(" join PAYBILL_HEAD_MPG PHM on HPP.PAYBILL_GRP_ID = PHM.PAYBILL_ID and PHM.APPROVE_FLAG IN (0,5)");
		lSBQuery.append(" join hr_eis_emp_mst HES on HPP.EMP_ID = HES.EMP_ID");
		lSBQuery.append(" join MST_DCPS_EMP EM on EM.org_emp_mst_id = HES.emp_mpg_id");
		lSBQuery.append(" where ");
		lSBQuery.append(" HPP.PAYBILL_MONTH = " + lLongMonthId );
		lSBQuery.append(" and HPP.PAYBILL_YEAR = " + lLongYearIdFromPayroll);
		lSBQuery.append(" and PHM.BILL_NO = " + lLongBillGroupId);
		lSBQuery.append(" and PHM.APPROVE_FLAG IN (0,5) ");
		lSBQuery.append(" and EM.dcps_or_gpf = 'Y' ");
		lSBQuery.append(" and HPP.DCPS_DELAY <> 0 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListDelayedTypeEmpListFromPayroll = lQuery.list();
		
		return lListDelayedTypeEmpListFromPayroll;
	}
	
	public List getEmpListForPayArrearTypesInMonthAndBG(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListPayArrearTypeEmpListFromPayroll = null;

		lSBQuery.append(" SELECT EM.dcps_emp_id FROM HR_PAY_PAYBILL HPP");
		lSBQuery.append(" join PAYBILL_HEAD_MPG PHM on HPP.PAYBILL_GRP_ID = PHM.PAYBILL_ID and PHM.APPROVE_FLAG IN (0,5)");
		lSBQuery.append(" join hr_eis_emp_mst HES on HPP.EMP_ID = HES.EMP_ID");
		lSBQuery.append(" join MST_DCPS_EMP EM on EM.org_emp_mst_id = HES.emp_mpg_id");
		lSBQuery.append(" where ");
		lSBQuery.append(" HPP.PAYBILL_MONTH = " + lLongMonthId );
		lSBQuery.append(" and HPP.PAYBILL_YEAR = " + lLongYearIdFromPayroll);
		lSBQuery.append(" and PHM.BILL_NO = " + lLongBillGroupId);
		lSBQuery.append(" and PHM.APPROVE_FLAG IN (0,5) ");
		lSBQuery.append(" and EM.dcps_or_gpf = 'Y' ");
		lSBQuery.append(" and HPP.DCPS_PAY <> 0 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListPayArrearTypeEmpListFromPayroll = lQuery.list();
		
		return lListPayArrearTypeEmpListFromPayroll;
	}
	
	public List getEmpListForDAArrearTypesInMonthAndBG(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListDAArrearTypeEmpListFromPayroll = null;

		lSBQuery.append(" SELECT EM.dcps_emp_id FROM HR_PAY_PAYBILL HPP");
		lSBQuery.append(" join PAYBILL_HEAD_MPG PHM on HPP.PAYBILL_GRP_ID = PHM.PAYBILL_ID and PHM.APPROVE_FLAG IN (0,5)");
		lSBQuery.append(" join hr_eis_emp_mst HES on HPP.EMP_ID = HES.EMP_ID");
		lSBQuery.append(" join MST_DCPS_EMP EM on EM.org_emp_mst_id = HES.emp_mpg_id");
		lSBQuery.append(" where ");
		lSBQuery.append(" HPP.PAYBILL_MONTH = " + lLongMonthId );
		lSBQuery.append(" and HPP.PAYBILL_YEAR = " + lLongYearIdFromPayroll);
		lSBQuery.append(" and PHM.BILL_NO = " + lLongBillGroupId);
		lSBQuery.append(" and PHM.APPROVE_FLAG IN (0,5) ");
		lSBQuery.append(" and EM.dcps_or_gpf = 'Y' ");
		lSBQuery.append(" and HPP.DCPS_DA <> 0 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListDAArrearTypeEmpListFromPayroll = lQuery.list();
		
		return lListDAArrearTypeEmpListFromPayroll;
	}
	
	public void updateDelayedContriStatusInTrn(List lListDelayedTypeEmpList,Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception {

		Query lQuery = null;
		StringBuilder lSBQuery = null;
		Long lLongDcpsEmpId = null;
		
		for(Integer lInt=0; lInt < lListDelayedTypeEmpList.size(); lInt++ )
		{
			lLongDcpsEmpId = Long.valueOf(lListDelayedTypeEmpList.get(lInt).toString());
			lSBQuery = new StringBuilder();
	
			lSBQuery.append(" update TrnDcpsContribution set regStatus = 3 ");
			lSBQuery.append(" where dcpsDdoBillGroupId = :dcpsDdoBillGroupId ");
			lSBQuery.append(" and monthId = :monthId");
			lSBQuery.append(" and finYearId = :finYearId");
			lSBQuery.append(" and ddoCode = :ddoCode");
			lSBQuery.append(" and typeOfPayment = :typeOfPayment");
			lSBQuery.append(" and dcpsEmpId = :dcpsEmpId");
			lSBQuery.append(" and regStatus = 0");
	
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("monthId", monthId);
			lQuery.setParameter("finYearId", finYearId);
			lQuery.setParameter("dcpsDdoBillGroupId", dcpsDdoBillGroupId);
			lQuery.setParameter("ddoCode", ddoCode);
			lQuery.setParameter("typeOfPayment", "700047");
			lQuery.setParameter("dcpsEmpId", lLongDcpsEmpId);
			
			lQuery.executeUpdate();
		}
	}
	
	public void updatePayArrearContriStatusInTrn(List lListPayArrearTypeEmpList,Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception {

		Query lQuery = null;
		StringBuilder lSBQuery = null;
		Long lLongDcpsEmpId = null;
		
		for(Integer lInt=0; lInt < lListPayArrearTypeEmpList.size(); lInt++ )
		{
			lLongDcpsEmpId = Long.valueOf(lListPayArrearTypeEmpList.get(lInt).toString());
			lSBQuery = new StringBuilder();
	
			lSBQuery.append(" update TrnDcpsContribution set regStatus = 3 ");
			lSBQuery.append(" where dcpsDdoBillGroupId = :dcpsDdoBillGroupId ");
			lSBQuery.append(" and monthId = :monthId");
			lSBQuery.append(" and finYearId = :finYearId");
			lSBQuery.append(" and ddoCode = :ddoCode");
			lSBQuery.append(" and typeOfPayment = :typeOfPayment");
			lSBQuery.append(" and dcpsEmpId = :dcpsEmpId");
			lSBQuery.append(" and regStatus = 0");
	
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("monthId", monthId);
			lQuery.setParameter("finYearId", finYearId);
			lQuery.setParameter("dcpsDdoBillGroupId", dcpsDdoBillGroupId);
			lQuery.setParameter("ddoCode", ddoCode);
			lQuery.setParameter("typeOfPayment", "700049");
			lQuery.setParameter("dcpsEmpId", lLongDcpsEmpId);
			
			lQuery.executeUpdate();
		}
	}
	
	public void updateDAArrearContriStatusInTrn(List lListDAArrearTypeEmpList,Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception {

		Query lQuery = null;
		StringBuilder lSBQuery = null;
		Long lLongDcpsEmpId = null;
		
		for(Integer lInt=0; lInt < lListDAArrearTypeEmpList.size(); lInt++ )
		{
			lLongDcpsEmpId = Long.valueOf(lListDAArrearTypeEmpList.get(lInt).toString());
			lSBQuery = new StringBuilder();
	
			lSBQuery.append(" update TrnDcpsContribution set regStatus = 3 ");
			lSBQuery.append(" where dcpsDdoBillGroupId = :dcpsDdoBillGroupId ");
			lSBQuery.append(" and monthId = :monthId");
			lSBQuery.append(" and finYearId = :finYearId");
			lSBQuery.append(" and ddoCode = :ddoCode");
			lSBQuery.append(" and typeOfPayment = :typeOfPayment");
			lSBQuery.append(" and dcpsEmpId = :dcpsEmpId");
			lSBQuery.append(" and regStatus = 0");
	
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("monthId", monthId);
			lQuery.setParameter("finYearId", finYearId);
			lQuery.setParameter("dcpsDdoBillGroupId", dcpsDdoBillGroupId);
			lQuery.setParameter("ddoCode", ddoCode);
			lQuery.setParameter("typeOfPayment", "700048");
			lQuery.setParameter("dcpsEmpId", lLongDcpsEmpId);
			
			lQuery.executeUpdate();
		}
	}
	
	public List getEmpListOfDCPSBrokenPeriodForMonth(Long lLongYearId, Long lLongMonthId) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListEmpListBrokenPeriodDCPS = null;

		lSBQuery.append(" SELECT MBP.EIS_EMP_ID FROM MST_DCPS_BROKEN_PERIOD_PAY MBP");
		lSBQuery.append(" join RLT_DCPS_BROKEN_PERIOD_DEDUC RBP on MBP.BROKEN_PERIOD_ID = RBP.RLT_BROKEN_PERIOD_ID");
		lSBQuery.append(" where ");
		lSBQuery.append(" MBP.YEAR_ID = " + lLongYearId);
		lSBQuery.append(" and MBP.MONTH_ID = " + lLongMonthId);
		lSBQuery.append(" and RBP.DEDUC_CODE = 59 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListEmpListBrokenPeriodDCPS = lQuery.list();
		
		return lListEmpListBrokenPeriodDCPS;
	}
	
	public List getBrokenIdPKForEmployee(Long hrEisEmpId, Long lLongYearId, Long lLongMonthId, String lBillType) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListBrokenIdPKListForEmp = null;

		lSBQuery.append(" SELECT BROKEN_PERIOD_ID FROM MST_DCPS_BROKEN_PERIOD_PAY MBP ");
		lSBQuery.append(" where ");
		lSBQuery.append(" MBP.EIS_EMP_ID = " + hrEisEmpId);
		lSBQuery.append(" and MBP.YEAR_ID = " + lLongYearId);
		lSBQuery.append(" and MBP.MONTH_ID = " + lLongMonthId);
		lSBQuery.append(" and MBP.TYPE_OF_BILL = '" + lBillType+"'");//added by arpan
		logger.info("getBrokenIdPKForEmployee---------------"+lSBQuery.toString());
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListBrokenIdPKListForEmp = lQuery.list();
		
		return lListBrokenIdPKListForEmp;
	}
	
	public List getBasicStartAndEndDateForBrokenIdPK(Long lLongBrokenIdPK) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListBasicStartAndEndDateForBrokenIdPK = null;

		lSBQuery.append(" SELECT MBP.BASIC_PAY,MBP.FROM_DATE,MBP.TO_DATE FROM ");
		lSBQuery.append(" MST_DCPS_BROKEN_PERIOD_PAY MBP where MBP.BROKEN_PERIOD_ID = " + lLongBrokenIdPK );

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListBasicStartAndEndDateForBrokenIdPK = lQuery.list();
		
		return lListBasicStartAndEndDateForBrokenIdPK;
	}
	
	public List getDcpsEmpIdAndPCForEisId(Long lLongHrEISEmpId) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListDcpsEmpIdAndPCForEISId = null;

		lSBQuery.append(" SELECT EM.DCPS_EMP_ID,EM.PAY_COMMISSION,EM.emp_name,EM.dcps_id FROM mst_dcps_emp EM  ");
		lSBQuery.append(" join HR_EIS_EMP_MST HR on EM.ORG_EMP_MST_ID = HR.EMP_MPG_ID" );
		lSBQuery.append(" where HR.emp_id = " + lLongHrEISEmpId);

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListDcpsEmpIdAndPCForEISId = lQuery.list();
		
		return lListDcpsEmpIdAndPCForEISId;
	}
	
	public List getDCPSValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListDCPSValueForBrokenPeriodIDPK = null;

		lSBQuery.append(" SELECT DEDUC_VALUE FROM RLT_DCPS_BROKEN_PERIOD_DEDUC  ");
		lSBQuery.append(" where RLT_BROKEN_PERIOD_ID = " + lLongBrokenPeriodIdPk );
		lSBQuery.append(" and DEDUC_CODE = 59 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListDCPSValueForBrokenPeriodIDPK = lQuery.list();
		logger.info("Query........ashish..."+lSBQuery.toString());
		
		return lListDCPSValueForBrokenPeriodIDPK;
	}
	
	public List getCentralDAValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListDAValueForBrokenPeriodIDPK = null;

		lSBQuery.append(" SELECT ALLOW_VALUE FROM RLT_DCPS_BROKEN_PERIOD_ALLOW  ");
		lSBQuery.append(" where RLT_BROKEN_PERIOD_ID = " + lLongBrokenPeriodIdPk );
		lSBQuery.append(" and ALLOW_CODE = 162 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListDAValueForBrokenPeriodIDPK = lQuery.list();
		
		return lListDAValueForBrokenPeriodIDPK;
	}
	
	public List getDAValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListDAValueForBrokenPeriodIDPK = null;

		lSBQuery.append(" SELECT ALLOW_VALUE FROM RLT_DCPS_BROKEN_PERIOD_ALLOW  ");
		lSBQuery.append(" where RLT_BROKEN_PERIOD_ID = " + lLongBrokenPeriodIdPk );
		lSBQuery.append(" and ALLOW_CODE = 10 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListDAValueForBrokenPeriodIDPK = lQuery.list();
		
		return lListDAValueForBrokenPeriodIDPK;
	}
	
	public List getDPValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		List lListDPValueForBrokenPeriodIDPK = null;

		lSBQuery.append(" SELECT ALLOW_VALUE FROM RLT_DCPS_BROKEN_PERIOD_ALLOW  ");
		lSBQuery.append(" where RLT_BROKEN_PERIOD_ID = " + lLongBrokenPeriodIdPk );
		lSBQuery.append(" and ALLOW_CODE = 145 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListDPValueForBrokenPeriodIDPK = lQuery.list();
		
		return lListDPValueForBrokenPeriodIDPK;
	}
	
	public Boolean checkIfBillAlreadyGenerated(Long lLongBillGroupId,Long lLongMonth,Long lLongYear) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = false;

		lSBQuery.append(" SELECT ID FROM PAYBILL_HEAD_MPG where BILL_NO = :billNo and PAYBILL_MONTH = :month and PAYBILL_YEAR = :year and APPROVE_FLAG in (0,1,5) ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("billNo", lLongBillGroupId);
		lQuery.setParameter("month", lLongMonth);
		lQuery.setParameter("year", lLongYear);

		tempList = lQuery.list();
		if(tempList != null)
		{
			if (tempList.size() != 0) {
				flag = true;
			}
		}
		return flag;
	}

	public void updateVoucherDetailsInPayBillHeadMpg(Long lLongBillGroupId,Long lLongMonth,Long lLongYear,Long lLongVoucherNo,Date lDateVoucherDate) {
		
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" update PAYBILL_HEAD_MPG set VOUCHER_NO = :voucherNo , VOUCHER_DATE = :voucherDate where BILL_NO = :billNo and PAYBILL_MONTH = :payBillMonth and PAYBILL_YEAR = :payBillYear and APPROVE_FLAG = 1 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("billNo", lLongBillGroupId);
		lQuery.setParameter("payBillMonth", lLongMonth);
		lQuery.setParameter("payBillYear", lLongYear);
		lQuery.setParameter("voucherNo", lLongVoucherNo);
		lQuery.setParameter("voucherDate", lDateVoucherDate);

		lQuery.executeUpdate();

	}
	
	public void updateVoucherDetailsInTrnDcpsContri(Long lLongBillGroupId,Long lLongMonthId,Long lLongYearId,String lStrDDOCode,Long lLongVoucherNo,Date lDateVoucherDate) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" update TRN_DCPS_CONTRIBUTION set VOUCHER_NO = :voucherNo , VOUCHER_DATE = :voucherDate, STATUS = 'A', REG_STATUS = 1 where BILL_GROUP_ID = :billGroupId and MONTH_ID = :monthId and FIN_YEAR_ID = :yearId and DDO_CODE = :ddoCode ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("billGroupId", lLongBillGroupId);
		lQuery.setParameter("monthId", lLongMonthId);
		lQuery.setParameter("yearId", lLongYearId);
		lQuery.setParameter("ddoCode", lStrDDOCode);
		lQuery.setParameter("voucherNo", lLongVoucherNo);
		lQuery.setParameter("voucherDate", lDateVoucherDate);

		lQuery.executeUpdate();

	}
	
	public void updateVoucherDetailsInMstContriOnApproval(Long lLngContriVoucherIdToBePassed, Double lDoubleVoucherAmount) throws Exception {
		
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" update MST_DCPS_CONTRI_VOUCHER_DTLS set STATUS = 'A', VOUCHER_STATUS = 1, VOUCHER_AMOUNT = :voucherAmount where MST_DCPS_CONTRI_VOUCHER_DTLS = :voucherIdPK  ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("voucherIdPK", lLngContriVoucherIdToBePassed);
		lQuery.setParameter("voucherAmount", lDoubleVoucherAmount);

		lQuery.executeUpdate();

	}
	
	public void updateNonRegularTypeContriStatusInTrn(Long lLongYearId, Long lLongMonthId, Long lLongBillGroupId) throws Exception {

		Query lQuery = null;
		StringBuilder lSBQuery = null;
		Long lLongDcpsEmpId = null;
		
		lSBQuery = new StringBuilder();

		lSBQuery.append(" update TrnDcpsContribution set regStatus = 0 ");
		lSBQuery.append(" where dcpsDdoBillGroupId = :dcpsDdoBillGroupId ");
		lSBQuery.append(" and monthId = :monthId");
		lSBQuery.append(" and finYearId = :finYearId");
		lSBQuery.append(" and typeOfPayment in (:delayed,:DAArr,:PayArr)");
		lSBQuery.append(" and regStatus = 3");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("monthId", lLongMonthId);
		lQuery.setParameter("finYearId", lLongYearId);
		lQuery.setParameter("dcpsDdoBillGroupId", lLongBillGroupId);
		lQuery.setParameter("delayed", "700047");
		lQuery.setParameter("DAArr", "700048");
		lQuery.setParameter("PayArr", "700049");
		
		lQuery.executeUpdate();
		
	}

	public List getSchemeNamesFromCodeInMissingCredit(String schemeCode, String lStrDdoCode) {

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		List resultList = null;
		ComboValuesVO cmbVO;
		Object[] obj;

		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery.append("SELECT MS.scheme_code, MS.scheme_name FROM Mst_Scheme MS WHERE scheme_Code LIKE '" + schemeCode + "%'");
			Query lQuery = ghibSession.createSQLQuery(SBQuery.toString());

			resultList = lQuery.list();
			
			cmbVO = new ComboValuesVO();

			if (resultList != null && resultList.size() > 0) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					finalList.add(cmbVO);
				}
			}

			return finalList;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return resultList;
	}
	
	public MstEmp getEmpVOForDCPSId(String dcpsId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
		MstEmp lObjEmpVO = null;

		lSBQuery.append("FROM MstEmp");
		lSBQuery.append(" WHERE dcpsId = :dcpsId and pranNo is not null order by reg_status desc ");////$t 30-1-2021 pranNo is not null condition added
		//lSBQuery.append(" WHERE dcpsId = :dcpsId  order by pranNo,reg_status fetch first row only  ");////$t 30-1-2021 pranNo is not null condition added

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId.trim());
            gLogger.info(lQuery.toString());
            
        if(lQuery.list().size()>0 && lQuery.list()!=null){  
        gLogger.info("$t (MstEmp) lQuery.list().get(0) -->"+(MstEmp) lQuery.list().get(0));////////$t 17-2-2021
		lObjEmpVO = (MstEmp) lQuery.list().get(0);
        }

		return lObjEmpVO;
	}
	
	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtlsMissingCredit(Long lLongYearId,Long lLongMonthId,
			String lStrDDOCode,Long lLongBillGroupId,String lStrSchemeCode,Long lLongVoucherNo,Date lDateVoucherDate) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Query lQuery = null;

		lSBQuery.append("FROM MstDcpsContriVoucherDtls");
		lSBQuery.append(" WHERE yearId = :yearId and monthId = :monthId and ddoCode = :ddoCode and billGroupId = :billGroupId");
		lSBQuery.append(" and schemeCode = :schemeCode and voucherNo = :voucherNo and voucherDate = :voucherDate and status = 'F' and isChallan is null ");
		
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", lLongYearId);
		lQuery.setParameter("monthId", lLongMonthId);
		lQuery.setParameter("ddoCode", lStrDDOCode);
		lQuery.setParameter("billGroupId", lLongBillGroupId);
		
		lQuery.setParameter("schemeCode", lStrSchemeCode);
		lQuery.setParameter("voucherNo", lLongVoucherNo);
		lQuery.setParameter("voucherDate", lDateVoucherDate);
		
		List tempList = null;
		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) tempList.get(0);
		}

		return lObjMstDcpsContriVoucherDtls;
	}
	
	/*
	public Double getTotalVoucherAmountForGivenVoucherMissingCredit(Long monthId,
			Long finYearId, Long billGroupId, String ddoCode,String lStrSchemeCode,Long lLongVoucherNo,Date lDateVoucherDate) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = null;
		Double voucherAmount = null;
		
		lSBQuery.append("  SELECT SUM(CONTRIBUTION) FROM trn_dcps_contribution WHERE ");
		
		lSBQuery.append("  month_id = :monthId and ");
		lSBQuery.append("  fin_year_id = :yearId and");
		lSBQuery.append("  bill_group_id = :billGroupId and ");
		lSBQuery.append("  ddo_code = :ddoCode and ");
		lSBQuery.append("  SCHEME_CODE = :schemeCode and ");
		lSBQuery.append("  VOUCHER_NO = :voucherNo and ");
		lSBQuery.append("  VOUCHER_DATE = :voucherDate and ");
		lSBQuery.append("  IS_CHALLAN is null and ");
		lSBQuery.append("  STATUS = 'F' ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", finYearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("billGroupId", billGroupId);
		
		lQuery.setParameter("schemeCode", lStrSchemeCode);
		lQuery.setParameter("voucherNo", lLongVoucherNo);
		lQuery.setParameter("voucherDate", lDateVoucherDate);

		tempList = lQuery.list();
		if (tempList.get(0) != null) {
			voucherAmount = Double.parseDouble(tempList.get(0).toString());
		}
		return voucherAmount;
	}
	*/
	
	public Double getTotalVoucherAmountForGivenVoucherMissingCredit(Long lLngContriVoucherIdPassed ) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = null;
		Double voucherAmount = null;
		
		lSBQuery.append("  SELECT SUM(CONTRIBUTION) FROM trn_dcps_contribution WHERE ");
		lSBQuery.append("  rlt_contri_voucher_id = :voucherIdPK and reg_status = 1 ");
	
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("voucherIdPK", lLngContriVoucherIdPassed);
		
		tempList = lQuery.list();
		if (tempList.get(0) != null) {
			voucherAmount = Double.parseDouble(tempList.get(0).toString());
		}
		return voucherAmount;
	}
	
	public void updateVoucherAmountInMstContriForMissingCreditVoucher(Long lLngContriVoucherIdPassed, Double voucherAmount) {
		
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" update MST_DCPS_CONTRI_VOUCHER_DTLS set VOUCHER_AMOUNT = :voucherAmount where MST_DCPS_CONTRI_VOUCHER_DTLS = :voucherIdPK");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		lQuery.setParameter("voucherIdPK", lLngContriVoucherIdPassed);
		lQuery.setParameter("voucherAmount", voucherAmount);
	
		lQuery.executeUpdate();

	}
	
	public List getBillGroupsForDdoInTOLoginInclMisngCrdt(String lStrDdoCode)
	throws Exception {

		String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG,TrnDcpsContribution VC where BG.dcpsDdoCode = :ddoCode and BG.dcpsDdoBillGroupId = VC.dcpsDdoBillGroupId and VC.regStatus IN (1) group by BG.dcpsDdoBillGroupId,BG.dcpsDdoBillDescription order by BG.dcpsDdoBillDescription ASC ";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrDdoCode.equals("")) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		} else if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
			// Missing Credit Bill group added.
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
			lObjComboValuesVO.setDesc("Missing Credit");
			lLstReturnList.add(lObjComboValuesVO);
			
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
			// Missing Credit Bill group added.
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
			lObjComboValuesVO.setDesc("Missing Credit");
			lLstReturnList.add(lObjComboValuesVO);
		}
		
		return lLstReturnList;
		}
//Added by Ashish for Contribution already done data			
	public List getContriOfEmpForSelectedPeriod(Long dcpsEmpId,
			Date contriStartDate, Date contriEndDate, String typeOfPayment,String lStrDDOCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List lListTrnDcpsContribution = null;
		Boolean flag = true;
	//$t 2019 
		lSBQuery.append("	select voucher_no,to_char(voucher_date,'dd-MM-yyyy'),contribution , nvl(STATUS,'M'),CONTRIBUTION_EMPLR,to_char(STARTDATE,'dd/MM/yyyy'),to_char(ENDDATE,'dd/MM/yyyy') FROM Trn_Dcps_Contribution WHERE dcps_Emp_Id = :dcpsEmpId AND  ");////$t 17Jan2022
		lSBQuery.append(" ((:contriStartDate between startDate and endDate) OR (:contriEndDate between startDate and endDate))");
		if (typeOfPayment.equals("700046") || typeOfPayment.equals("700047")) {
			lSBQuery.append(" AND (type_Of_Payment = '700046' OR type_Of_Payment = '700047')");
		} else {
			lSBQuery.append(" AND type_Of_Payment = :typeOfPayment ");
		}
		if(lStrDDOCode != null)
		{
			if(!"".equals(lStrDDOCode))
			{
				lSBQuery.append(" AND ddo_Code = :ddoCode ");
			}
		}
		lSBQuery.append(" AND reg_Status in (1,3,4,2)");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("contriStartDate", contriStartDate);
		lQuery.setParameter("contriEndDate", contriEndDate);
		if(lStrDDOCode != null)
		{
			if(!"".equals(lStrDDOCode))
			{
				lQuery.setParameter("ddoCode", lStrDDOCode);
			}
		}

		if (!(typeOfPayment.equals("700046") || typeOfPayment.equals("700047"))) {
			lQuery.setParameter("typeOfPayment", typeOfPayment);
		}

		lListTrnDcpsContribution = lQuery.list();
		
		return lListTrnDcpsContribution;
		
		
		
	}

	  public List getDORDetails(String billNo, String month, String year, String dcpsId)
	  {
	    List billType = new ArrayList();
	    Session session = getSession();
	    StringBuffer sb = new StringBuffer();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date date = new Date();

	    String finYear = year;

	    String[] a = new String[3];
	    this.gLogger.info("year is " + finYear);
	    this.gLogger.info("monthis " + month);
	    a[0] = finYear;
	    a[1] = month;
	    a[2] = "31";

	    if (Integer.parseInt(a[1]) <= 7) {
	      a[1] = "0" + Integer.toString(Integer.parseInt(a[1]) + 2);
	    }
	    else if ((Integer.parseInt(a[1]) > 7) && (Integer.parseInt(a[1]) <= 10)) {
	      a[1] = Integer.toString(Integer.parseInt(a[1]) + 2);
	    }
	    else if (Integer.parseInt(a[1]) == 11) {
	      a[1] = "01";
	      a[0] = Integer.toString(Integer.parseInt(a[0]) + 1);
	    }
	    else if (Integer.parseInt(a[1]) == 12) {
	      a[1] = "02";
	      a[0] = Integer.toString(Integer.parseInt(a[0]) + 1);
	    }

	    this.gLogger.info("monthis " + month);

	    String CurrDate = a[0] + "-" + a[1] + "-" + a[2];
	    String nYear = a[0];
	    String nMonth = a[1];

	    System.out.println(" DATE IS:::::::::::: " + CurrDate);

	    sb.append(" SELECT emp.EMP_NAME ||' ('||emp.SEVARTH_ID||')' FROM mst_dcps_emp emp    ");
	    sb.append(" where  emp.REG_STATUS = 1 and emp.DCPS_OR_GPF = 'Y' and emp.BILLGROUP_ID= " + billNo + "   and emp.DCPS_ID = '" + dcpsId + "' and year(emp.EMP_SERVEND_DT) <= '" + nYear + "' and month(emp.EMP_SERVEND_DT) <='" + nMonth + "' ");

	    Query query = session.createSQLQuery(sb.toString());
	    this.logger.info("query is *************" + query.toString());
	    billType = query.list();

	    return billType;
	  }

	  public String finyear(String year, String month)
	  {
	    String billType = "";
	    Session session = getSession();
	    StringBuffer sb = new StringBuffer();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    int months = Integer.parseInt(month);

	    if (months < 4)
	    {
	      sb.append(" SELECT substr(FIN_YEAR_DESC,6,8) FROM SGVC_FIN_YEAR_MST where FIN_YEAR_ID= " + year + "  ");
	    }

	    if (months >= 4)
	    {
	      sb.append(" SELECT substr(FIN_YEAR_DESC,1,4) FROM SGVC_FIN_YEAR_MST where FIN_YEAR_ID= " + year + "  ");
	    }

	    Query query = session.createSQLQuery(sb.toString());
	    this.logger.info("query is *************" + query.toString());
	    billType = query.list().get(0).toString();

	    return billType;
	}

	  public List getMissingCreditForApprovalSRKA() throws Exception
	  {

			StringBuilder lSBQuery = new StringBuilder();
			List lListTrnDcpsContribution = null;
			Boolean flag = true;
			try
			{
				lSBQuery.append(" SELECT c.DCPS_CONTRIBUTION_ID,loc.LOC_NAME,emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,l.LOOKUP_NAME,c.CONTRIBUTION,c.CONTRIBUTION,mon.MONTH_NAME,f.fin_year_desc, ");
				lSBQuery.append(" case when c.MONTH_ID > 3 then cast(f.fin_year_code as integer) else cast(f.fin_year_code as integer)+1 end from ");
				
				lSBQuery.append(" TRN_DCPS_CONTRIBUTION c inner join MST_DCPS_EMP emp on emp.DCPS_EMP_ID=c.DCPS_EMP_ID");
				lSBQuery.append(" inner join CMN_LOOKUP_MST l on l.lookup_id=c.TYPE_OF_PAYMENT  inner join SGVA_MONTH_MST mon on mon.MONTH_ID=c.MONTH_ID and mon.LANG_ID='en_US'");
				lSBQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(c.TREASURY_CODE,1,2) and loc.DEPARTMENT_ID=100003  inner join SGVC_FIN_YEAR_MST f on f.fin_year_id=c.FIN_YEAR_ID where c.STATUS='S' ");
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			

				lListTrnDcpsContribution = lQuery.list();
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);

			}
			
			
			return lListTrnDcpsContribution;
			
			
			
		}


	  public List getBsicPayDetails(Long dcpsEmpId) throws Exception {
			
			StringBuilder lSBQuery = new StringBuilder();
			List lListDcpsEmpIdAndPCForEISId = null;

			lSBQuery.append(" SELECT EM.basic_pay,EM.seven_pc_basic FROM mst_dcps_emp EM  ");
		
			lSBQuery.append(" where EM.dcps_emp_id = " + dcpsEmpId);

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lListDcpsEmpIdAndPCForEISId = lQuery.list();
			
			return lListDcpsEmpIdAndPCForEISId;
		}










	

	//Ended by Ashish for Contribution already done data
	  
	  
	  
	  public Boolean checkContriOfEmpForSelectedPeriod_newARREAR(Long dcpsEmpId,
				Date contriStartDate, Date contriEndDate, String typeOfPayment,String lStrDDOCode,String payCommission) {
	//pay arrear =700049
	//delayed = 700047
	//DA Arrears=700048
	//regular=700046		
			StringBuilder lSBQuery = new StringBuilder();
			List<TrnDcpsContribution> lListTrnDcpsContribution = null;
			Boolean flag = true;
			
			lSBQuery
					.append(" FROM TrnDcpsContribution WHERE dcpsEmpId = :dcpsEmpId AND  ");
			lSBQuery
					.append(" ((:contriStartDate between startDate and endDate) OR (:contriEndDate between startDate and endDate)) and   (:contriStartDate between startDate and endDate)");
			if (typeOfPayment.equals("700046") || typeOfPayment.equals("700047")) {
				lSBQuery
						.append(" AND (typeOfPayment = '700046' OR typeOfPayment = '700047')");
			} else {
				lSBQuery.append(" AND typeOfPayment = :typeOfPayment ");
			}
			if(lStrDDOCode != null)
			{
				if(!"".equals(lStrDDOCode))
				{
					lSBQuery.append(" AND ddoCode = :ddoCode ");
				}
			}
			if(payCommission != null && payCommission.equals("700349"))
			{
			if(!"".equals(payCommission) && payCommission.equals("700349"))
			{
			lSBQuery.append(" AND payCommission = :payCommission ");
			}
			}
			lSBQuery.append(" AND regStatus in (1,3,4,2)");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("dcpsEmpId", dcpsEmpId);
			lQuery.setParameter("contriStartDate", contriStartDate);
			lQuery.setParameter("contriEndDate", contriEndDate);
			if(lStrDDOCode != null)
			{
				if(!"".equals(lStrDDOCode))
				{
					lQuery.setParameter("ddoCode", lStrDDOCode);
				}
			}
			
			if(payCommission != null && payCommission.equals("700349"))
			{
			if(!"".equals(payCommission) && payCommission.equals("700349"))
			{
			lQuery.setParameter("payCommission", payCommission);
			}
			}
			if (!(typeOfPayment.equals("700046") || typeOfPayment.equals("700047"))) {
					lQuery.setParameter("typeOfPayment", typeOfPayment);
			}
			lListTrnDcpsContribution = lQuery.list();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			//String strt=new Date(15,1,01);
			//String end=new Date(15,9,30);
			Date strts = null;
			Date strts16 = null;
			try {
				strts = sdf.parse("01/07/2015");
				strts16 = sdf.parse("01/07/2016");
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Date ends = null;
			Date ends16 = null;
			try {
				ends = sdf.parse("30/09/2015");
				ends16 = sdf.parse("31/03/2017");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date endl = null;
		    Date endl7 = null;
		    try
		    {
		      endl = sdf.parse("01/01/2017");
		      endl7 = sdf.parse("31/01/2018");
		    }
		    catch (ParseException e)
		    {
		      e.printStackTrace();
		    }
		    Date end2 = null;
		    Date endl8 = null;
		    try
		    {
		      end2 = sdf.parse("01/01/2018");
		      endl8 = sdf.parse("30/09/2018");
		    }
		    catch (ParseException e)
		    {
		      e.printStackTrace();
		    }
		    Date strts19 = null;
		    Date end19 = null;
		    try
		    {
		    	strts19 = sdf.parse("01/07/2018");
		    	end19 = sdf.parse("30/06/2019");
		    }
		    catch (ParseException e)
		    {
		      e.printStackTrace();
		    }
			
			int comparison1 = contriStartDate.compareTo(strts);
			int comparison2 = contriEndDate.compareTo(ends);
			int comparison3 = contriStartDate.compareTo(strts16);
			int comparison4 = contriEndDate.compareTo(ends16);
			
			int comparison5 = contriStartDate.compareTo(endl);
		    int comparison6 = contriEndDate.compareTo(endl7);
		    
		    int comparison7 = contriStartDate.compareTo(end2);
		    int comparison8 = contriEndDate.compareTo(endl8);
		    
		    int comparison9 = contriStartDate.compareTo(strts19);//1
		    int comparison10 = contriEndDate.compareTo(end19);//1
		    
			gLogger.info("comparison1"+comparison1);
			gLogger.info("comparison2"+comparison2);
			gLogger.info("end date"+contriStartDate.getYear());
			//if(contriStartDate=='2015-01-')
			if(typeOfPayment.equals("700048")){
				gLogger.info("######typeOfPaymentif"+typeOfPayment);
				gLogger.info("######typeOfPaymentif" + typeOfPayment);
			if((comparison1==0 || comparison1==1)&&(comparison2 == 0 || comparison2==-1))
			{
				gLogger.info("######if part"+contriEndDate);
				flag = true;
				gLogger.info("######flagif"+flag);
			}
			else if((comparison3==0 || comparison3==1)&&(comparison4 == 0 || comparison4==-1))
			{
				gLogger.info("######if part"+contriEndDate);
				flag = true;
				gLogger.info("######flagif"+flag);
			}
			else if (((comparison5 == 0) || (comparison5 == 1)) && ((comparison6 == 0) || (comparison6 == -1)))
		      {
		        this.gLogger.info("###### TEST if part" + contriEndDate);
		        flag = Boolean.valueOf(true);
		        this.gLogger.info("######flagif" + flag);
		      }
		      else if (((comparison7 == 0) || (comparison7 == 1)) && ((comparison8 == 0) || (comparison8 == -1)))
		      {
		        this.gLogger.info("###### TEST if part" + contriEndDate);
		        flag = Boolean.valueOf(true);
		        this.gLogger.info("######flagif" + flag);
		      }
		      else if (((comparison9 == 0) || (comparison9 == 1)) && ((comparison10 == 0) || (comparison10 == -1)))//$t 2019
		      {
		        this.gLogger.info("###### TEST if part" + contriEndDate);
		        flag = Boolean.valueOf(true);
		        this.gLogger.info("######flagif" + flag);
		      }
			else
			{
				gLogger.info("######else part"+contriEndDate);
			if (lListTrnDcpsContribution != null
					&& lListTrnDcpsContribution.size() != 0) {
				flag = false;
				
			}gLogger.info("######flagesle"+flag);
			}
			}else{
				gLogger.info("######typeOfPaymentelse"+typeOfPayment);
				if (lListTrnDcpsContribution != null
						&& lListTrnDcpsContribution.size() != 0) {
					flag = false;
					
				}
				gLogger.info("######flagelsess"+flag);
			}
			return flag;
		}
		
		
		
	  
	
}

