/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 9, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class MatchContriEntryDAOImpl extends GenericDaoHibernateImpl implements
MatchContriEntryDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionproc/PensionCaseConstants");

	public MatchContriEntryDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	/*
	public List getAllTreasuriesForMatchedEntries(String lStrFromDate,
			String lStrToDate) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append(" SELECT vd.treasury_code, LM.loc_name, SUM(vd.voucher_amount),SUM(tn.DCPS_AMOUNT) FROM ");
		sb
				.append(" mst_dcps_contri_voucher_dtls vd, mst_dcps_treasurynet_data tn, mst_dcps_bill_group bg, cmn_location_mst LM , sgvc_fin_year_mst FY");
		sb.append(" WHERE vd.treasury_code = tn.treasury_code AND  ");
		sb.append(" vd.ddo_code = tn.ddo_code AND ");
		sb.append(" vd.bill_group_id = bg.bill_group_id AND ");
		sb.append(" bg.scheme_code = tn.FROM_SCHEME AND ");
		sb.append(" ((vd.voucher_no = tn.voucher_no AND vd.voucher_amount = tn.dcps_amount AND vd.voucher_date=tn.voucher_date) OR (vd.manually_matched = 1))");
		sb.append(" AND vd.treasury_code = LM.loc_id AND");
		sb.append(" FY.fin_year_desc = tn.year_desc AND");
		sb.append(" FY.fin_year_id = vd.year_id AND");
		sb.append(" vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}
	 */

	// Above query contained bill group join for matching/mismatching and it should not be there, so removed. Below is final method.

	public List getAllTreasuriesForMatchedEntries(String lStrFromDate,
			String lStrToDate,Long acMain) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		/*
		sb.append(" SELECT vd.treasury_code, LM.loc_name, SUM(vd.voucher_amount),SUM(tn.DCPS_AMOUNT) FROM ");
		sb.append(" mst_dcps_contri_voucher_dtls vd, mst_dcps_treasurynet_data tn,  cmn_location_mst LM , sgvc_fin_year_mst FY");
		sb.append(" WHERE vd.treasury_code = tn.treasury_code AND  ");
		sb.append(" vd.ddo_code = tn.ddo_code AND ");
		sb.append(" vd.scheme_code = tn.FROM_SCHEME AND ");
		sb.append(" ((vd.voucher_no = tn.voucher_no AND vd.voucher_amount = tn.dcps_amount AND vd.voucher_date=tn.voucher_date) OR (vd.manually_matched = 1))");
		sb.append(" AND vd.treasury_code = LM.loc_id AND");
		sb.append(" FY.fin_year_desc = tn.year_desc AND");
		sb.append(" FY.fin_year_id = vd.year_id AND");
		sb.append(" vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name");
		 */

		// Above query changed as below so as to use STATUS column of mst_dcps_contri_voucher_dtls

		/*sb.append(" SELECT vd.treasury_code,LM.loc_name,CAST(SUM(vd.voucher_amount) as DECIMAL(20,0)),CAST(SUM(tn.DCPS_AMOUNT) as DECIMAL(20,0)) FROM ");
		sb.append(" mst_dcps_contri_voucher_dtls vd, mst_dcps_treasurynet_data tn,  cmn_location_mst LM , sgvc_fin_year_mst FY");
		sb.append(" WHERE ");
		sb.append(" vd.treasury_code = tn.treasury_code AND vd.ddo_code = tn.ddo_code and trim(vd.scheme_code) = trim(tn.FROM_SCHEME) AND vd.voucher_no = tn.voucher_no AND vd.voucher_amount = tn.dcps_amount AND vd.voucher_date=tn.voucher_date");
		sb.append(" AND vd.treasury_code = LM.loc_id ");
		sb.append(" AND FY.fin_year_desc = tn.year_desc ");
		sb.append(" AND FY.fin_year_id = vd.year_id ");
		sb.append(" AND vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		sb.append(" AND vd.STATUS = 'F' AND  vd.bill_group_id<>9999999  AND  vd.voucher_status is not null  AND  vd.voucher_amount<>0 AND  vd.voucher_status=1  ");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name ");
		sb.append(" order by vd.treasury_code ");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		StringBuilder sb1 = new StringBuilder();
		sb1.append(" SELECT '','Total', CAST(SUM(vd.voucher_amount)   as DECIMAL(20,0)),CAST(SUM(tn.DCPS_AMOUNT) as DECIMAL(20,0)) FROM  ");
		sb1.append(" mst_dcps_contri_voucher_dtls vd, mst_dcps_treasurynet_data tn,  cmn_location_mst LM , sgvc_fin_year_mst FY ");
		sb1.append(" WHERE ");
		sb1.append(" vd.treasury_code = tn.treasury_code AND vd.ddo_code = tn.ddo_code and trim(vd.scheme_code) = trim(tn.FROM_SCHEME) AND vd.voucher_no = tn.voucher_no AND vd.voucher_amount = tn.dcps_amount AND vd.voucher_date=tn.voucher_date ");
		sb1.append(" AND vd.treasury_code = LM.loc_id  ");
		sb1.append(" AND FY.fin_year_desc = tn.year_desc ");
		sb1.append(" AND FY.fin_year_id = vd.year_id AND ");
		sb1.append(" vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' ");
		sb1.append(" AND vd.STATUS = 'F'  AND  vd.bill_group_id is not null ");
		sb1.append(" AND vd.bill_group_id<>9999999  AND  vd.voucher_status is not null  AND  vd.voucher_amount<>0 AND  vd.voucher_status=1 ");*/
		
		sb.append(" SELECT cv.treasury_code,loc.loc_name,cast(SUM(TR.CONTRIBUTION) as DECIMAL(20,0)) as contribution,count(MST_DCPS_CONTRI_VOUCHER_DTLS) FROM trn_dcps_contribution tr  ");
		sb.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS CV on tr.RLT_CONTRI_VOUCHER_ID= CV.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		sb.append(" inner join CMN_LOCATION_MST loc on  loc.LOC_ID=cv.TREASURY_CODE ");
		sb.append(" inner join mst_dcps_emp em on TR.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
		sb.append(" where cv.STATUS = 'F' AND  cv.voucher_amount<>0 AND cv.voucher_status=1   ");
		sb.append(" AND ((cv.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' and cv.IS_MISSING_CREDIT is null ) or (CV.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' and  cv.IS_MISSING_CREDIT ='Y' )) and em.AC_DCPS_MAINTAINED_BY="+acMain+"   ");
		sb.append(" AND TR.REG_STATUS = 1  AND TR.EMPLOYER_CONTRI_FLAG = 'N' AND CV.bill_group_id is not null and  em.LOC_ID <> 380001  ");
		sb.append(" AND CV.voucher_amount<>0  AND CV.voucher_amount is not null  and TR.POST_EMPLR_CONTRI_STATUS = 0  ");
		sb.append(" GROUP BY loc.loc_id,cv.treasury_code, loc.loc_name  order by cv.treasury_code   ");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		StringBuilder sb1 = new StringBuilder();
		sb1.append(" SELECT '','A', cast(SUM(TR.CONTRIBUTION) as DECIMAL(20,0))  as contribution,count(MST_DCPS_CONTRI_VOUCHER_DTLS)  FROM mst_dcps_contri_voucher_dtls CV ");
		sb1.append(" inner join TRN_DCPS_CONTRIBUTION TR on TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS 	 ");
		sb1.append(" inner join MST_DCPS_EMP EM  on TR.DCPS_EMP_ID = EM.DCPS_EMP_ID  ");
		sb1.append(" WHERE  TR.POST_EMPLR_CONTRI_STATUS = 0  AND CV.voucher_status = 1 AND CV.STATUS = 'F' AND  EM.LOC_ID <> 380001 ");
		sb1.append(" AND ((cv.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' and cv.IS_MISSING_CREDIT is null ) or (CV.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' and  cv.IS_MISSING_CREDIT ='Y' )) and em.AC_DCPS_MAINTAINED_BY="+acMain+"   ");
		sb1.append(" AND TR.REG_STATUS = 1 AND TR.EMPLOYER_CONTRI_FLAG = 'N' AND CV.bill_group_id is not null   ");
		sb1.append(" AND CV.voucher_amount<>0  AND CV.voucher_amount is not null ");
		
		Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
		List lLstLastRow= selectQuery1.list();

		lLstReturnList.add(lLstLastRow.get(0));
		return lLstReturnList;
	}


	/*
	public List getAllTreasuriesForUnMatchedEntries(String lStrFromDate,
			String lStrToDate) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT vd.treasury_code,LM.loc_name,SUM(vd.voucher_amount),SUM(tn.DCPS_AMOUNT)");
		sb.append(" FROM mst_dcps_bill_group bg,cmn_location_mst LM,sgvc_fin_year_mst FY,mst_dcps_contri_voucher_dtls vd ");
		sb.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON vd.voucher_no = tn.voucher_no ");
		sb.append(" WHERE vd.treasury_code = LM.loc_id ");
		sb.append(" AND ");
		sb.append(" ( ");
		sb.append(" (tn.voucher_no IS NULL AND FY.fin_year_id = vd.year_id  AND vd.bill_group_id = bg.bill_group_id AND vd.manually_matched <> 1) ");
		sb.append("  OR" );
		sb.append(" (vd.voucher_no = tn.voucher_no AND vd.voucher_date <> tn.voucher_date AND FY.fin_year_desc = tn.year_desc  AND FY.fin_year_id = vd.year_id  AND vd.treasury_code = tn.treasury_code  AND vd.ddo_code = tn.ddo_code  AND vd.bill_group_id = bg.bill_group_id  AND bg.scheme_code = tn.FROM_SCHEME  AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.manually_matched <> 1)");
		sb.append("  OR ");
		sb.append(" (vd.voucher_no = tn.voucher_no AND vd.voucher_date = tn.voucher_date AND vd.voucher_amount <> tn.dcps_amount AND FY.fin_year_desc = tn.year_desc  AND FY.fin_year_id = vd.year_id  AND vd.treasury_code = tn.treasury_code  AND vd.ddo_code = tn.ddo_code  AND vd.bill_group_id = bg.bill_group_id  AND bg.scheme_code = tn.FROM_SCHEME  AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.manually_matched <> 1) ");
		sb.append(" )");
		sb.append(" AND vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name");



		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}
	 */

	//Above query contained bill group join for matching/mismatching and it should not be there, so removed. Below is final method.

	public List getAllTreasuriesForUnMatchedEntries(String lStrFromDate,
			String lStrToDate) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		/*sb.append(" SELECT vd.treasury_code,LM.loc_name,CAST(SUM(vd.voucher_amount) as DECIMAL(20,0)),CAST(SUM(tn.DCPS_AMOUNT) as DECIMAL(20,0))");
		sb.append(" FROM cmn_location_mst LM,sgvc_fin_year_mst FY,mst_dcps_contri_voucher_dtls vd ");
		sb.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON vd.voucher_no = tn.voucher_no ");
		sb.append(" WHERE vd.treasury_code = LM.loc_id ");
		sb.append(" AND ");
		sb.append(" ( ");
		sb.append(" (tn.voucher_no IS NULL AND FY.fin_year_id = vd.year_id AND vd.scheme_code = tn.FROM_SCHEME AND vd.manually_matched <> 1) ");
		sb.append("  OR" );
		sb.append(" (vd.STATUS in ('B','E') AND tn.STATUS in ('B','E')) ");
		sb.append(" )");

		sb.append(" (vd.voucher_no = tn.voucher_no AND vd.voucher_date <> tn.voucher_date AND FY.fin_year_desc = tn.year_desc  AND FY.fin_year_id = vd.year_id  AND vd.treasury_code = tn.treasury_code  AND vd.ddo_code = tn.ddo_code  AND vd.scheme_code = tn.FROM_SCHEME AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.manually_matched <> 1)");
		sb.append("  OR ");
		sb.append(" (vd.voucher_no = tn.voucher_no AND vd.voucher_date = tn.voucher_date AND vd.voucher_amount <> tn.dcps_amount AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.treasury_code = tn.treasury_code AND vd.ddo_code = tn.ddo_code AND vd.scheme_code = tn.FROM_SCHEME AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.manually_matched <> 1) ");
		sb.append(" )");

		sb.append(" AND vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name");*/

		sb.append(" SELECT vd.treasury_code,LM.loc_name,CAST(SUM(vd.voucher_amount) as DECIMAL(20,0)),CAST(SUM(tn.DCPS_AMOUNT) as DECIMAL(20,0))");
		sb.append(" FROM cmn_location_mst LM,sgvc_fin_year_mst FY,mst_dcps_contri_voucher_dtls vd,mst_dcps_treasurynet_data tn ");
		sb.append(" WHERE vd.treasury_code = LM.loc_id ");
		sb.append(" AND ");
		sb.append(" ( ");
		sb.append(" (tn.voucher_no IS NULL AND FY.fin_year_id = vd.year_id AND trim(vd.scheme_code) = trim(tn.FROM_SCHEME) AND vd.manually_matched <> 1) ");
		sb.append("  OR" );
		sb.append(" (vd.STATUS in ('B','E') AND tn.STATUS in ('B','E')) ");
		sb.append(" )");

		sb.append(" (vd.voucher_no = tn.voucher_no AND vd.voucher_date <> tn.voucher_date AND FY.fin_year_desc = tn.year_desc  AND FY.fin_year_id = vd.year_id  AND vd.treasury_code = tn.treasury_code  AND vd.ddo_code = tn.ddo_code  AND vd.scheme_code = tn.FROM_SCHEME AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.manually_matched <> 1)");
		sb.append("  OR ");
		sb.append(" (vd.voucher_no = tn.voucher_no AND vd.voucher_date = tn.voucher_date AND vd.voucher_amount <> tn.dcps_amount AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.treasury_code = tn.treasury_code AND vd.ddo_code = tn.ddo_code AND vd.scheme_code = tn.FROM_SCHEME AND FY.fin_year_desc = tn.year_desc AND FY.fin_year_id = vd.year_id AND vd.manually_matched <> 1) ");
		sb.append(" )");

		sb.append(" AND ((vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "') or (vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "')) ");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name");




		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}

	public List getAllTreasuriesForUnMatchedEntriesMstcontri(String treasuryCode,String lStrFromDate,String lStrToDate) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT LM. LOC_ID,LM.loc_name,CAST(SUM(vd.voucher_amount) as DECIMAL(20,0))");
		sb.append(" FROM CMN_LOCATION_MST LM inner join MST_DCPS_CONTRI_VOUCHER_DTLS vd ");
		sb.append(" on vd.treasury_code = LM.loc_id and LM.LOC_ID not in(9991,1111)   and LM.department_Id in (100003,100006) ");
		if(treasuryCode != null)
		sb.append(" and LM.parent_Loc_Id = "+treasuryCode+" or LM. LOC_ID ="+treasuryCode);	
		sb.append(" AND ((vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' and vd.IS_MISSING_CREDIT is null ) or (vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '" + lStrToDate + "' and vd.IS_MISSING_CREDIT='Y' )) ");
		sb.append(" AND vd.STATUS in ('A','B','E')");
		sb.append(" GROUP BY LM. LOC_ID,LM.loc_name");
		
		
		gLogger.info("Query========>"+sb.toString());

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		
		
		return lLstReturnList;
	}
	public List getAllTreasuriesForUnMatchedEntriesForMissing(String treasuryCode,String lStrFromDate,String lStrToDate) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT LM. LOC_ID,LM.loc_name,CAST(SUM(vd.voucher_amount) as DECIMAL(20,0))");
		sb.append(" FROM CMN_LOCATION_MST LM inner join MST_DCPS_CONTRI_VOUCHER_DTLS vd ");
		sb.append(" on vd.treasury_code = LM.loc_id and LM.LOC_ID not in(9991,1111)   and LM.department_Id in (100003,100006) ");
		if(treasuryCode != null)
		sb.append(" and LM.parent_Loc_Id = "+treasuryCode+" or LM. LOC_ID ="+treasuryCode);	
		sb.append(" AND (vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "') ");
		sb.append(" AND vd.STATUS in ('A','B','E')");
		sb.append(" GROUP BY LM. LOC_ID,LM.loc_name");
		
		
		gLogger.info("Query========>"+sb.toString());

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();
		
		
		return lLstReturnList;
	}

	public List getAllTreasuriesForUnMatchedEntriesTreasuryNet(String treasuryCode,String lStrFromDate,String lStrToDate) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT LM. LOC_ID,LM.loc_name,CAST(SUM(tn.dcps_amount) as DECIMAL(20,0))");
		sb.append(" FROM CMN_LOCATION_MST LM inner join MST_DCPS_TREASURYNET_DATA tn");
		sb.append(" on tn.treasury_code = LM.loc_id and LM.department_Id in (100003,100006) and LM.LOC_ID not in(9991,1111) ");
		if(treasuryCode != null)
		sb.append(" and LM.parent_Loc_Id = "+treasuryCode+" or LM. LOC_ID ="+treasuryCode);	
		sb.append(" AND tn.voucher_date BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		sb.append(" AND tn.STATUS in ('A','B','E')");
		sb.append(" GROUP BY LM. LOC_ID,LM.loc_name");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}

	/*

	public List getUnMatchedVouchersForMatching(String lStrFromDate,
			String lStrToDate, Long treasuryCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT vd.mst_dcps_contri_voucher_dtls,vd.voucher_no,vd.voucher_amount,vd.voucher_date as mstVoDt,bg.DESCRIPTION,vd.ddo_code,tn.dcps_amount,tn.voucher_date treVoDt ");
		sb.append(" FROM  mst_dcps_contri_voucher_dtls vd, mst_dcps_treasurynet_data tn, mst_dcps_bill_group bg, sgvc_fin_year_mst FY ");
		sb.append(" WHERE vd.treasury_code = tn.treasury_code ");
		sb.append(" AND vd.ddo_code = tn.ddo_code ");
		sb.append(" AND vd.bill_group_id = bg.bill_group_id ");
		sb.append(" AND bg.scheme_code = tn.FROM_SCHEME ");
		sb.append(" AND vd.voucher_no = tn.voucher_no ");
		sb.append(" AND (vd.voucher_amount <> tn.dcps_amount OR vd.voucher_date <> tn.voucher_date) ");
		sb.append(" AND vd.manually_matched = 0");
		sb.append(" AND FY.fin_year_desc = tn.year_desc ");
		sb.append(" AND FY.fin_year_id = vd.year_id ");
		sb.append(" AND vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" AND vd.voucher_status = 1");
		sb.append(" AND vd.treasury_code = " + treasuryCode);
		sb.append(" AND vd.manually_matched = 0");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}

	 */

	//Above query contained bill group join for matching/mismatching and it should not be there, so removed. Below is final method.


	public List getUnMatchedVouchersForMatching(String lStrFromDate,
			String lStrToDate, Long treasuryCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT vd.mst_dcps_contri_voucher_dtls,vd.voucher_no,vd.voucher_amount,vd.voucher_date as mstVoDt,bg.DESCRIPTION,vd.ddo_code,tn.dcps_amount,tn.voucher_date treVoDt ");
		sb.append(" FROM mst_dcps_contri_voucher_dtls vd, mst_dcps_treasurynet_data tn, sgvc_fin_year_mst FY ");
		sb.append(" WHERE vd.treasury_code = tn.treasury_code ");
		sb.append(" AND vd.ddo_code = tn.ddo_code ");
		sb.append(" AND trim(vd.scheme_code) = trim(tn.FROM_SCHEME) ");
		sb.append(" AND vd.voucher_no = tn.voucher_no ");
		sb.append(" AND (vd.voucher_amount <> tn.dcps_amount OR vd.voucher_date <> tn.voucher_date) ");
		sb.append(" AND vd.manually_matched = 0");
		sb.append(" AND FY.fin_year_desc = tn.year_desc ");
		sb.append(" AND FY.fin_year_id = vd.year_id ");
		sb.append(" AND ((vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "') or (vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "')) ");
		sb.append(" AND vd.voucher_status = 1");
		sb.append(" AND vd.treasury_code = " + treasuryCode);
		sb.append(" AND vd.manually_matched = 0");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}


	public List getUnMatchedVouchersForMatchingFromMstContriVoucherDtls(String lStrFromDate,
			String lStrToDate, Long treasuryCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT vd.mst_dcps_contri_voucher_dtls,vd.treasury_code,vd.voucher_no,cast(vd.voucher_amount as BIGINT),vd.voucher_date,vd.ddo_code,vd.SCHEME_CODE,mon.MONTH_NAME,case when mon.MONTH_ID>3 then cast(fin.FIN_YEAR_CODE  as  BIGINT)  else cast(fin.FIN_YEAR_CODE as  BIGINT)+1 end,MANUAL_CHANGE_COUNT   ");
		sb.append(" FROM mst_dcps_contri_voucher_dtls vd inner join sgvc_fin_year_mst fin on vd.YEAR_ID=fin.FIN_YEAR_ID ");
		sb.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID=vd.MONTH_ID");
		sb.append("	WHERE ");
		sb.append(" vd.manually_matched = 0 ");
		sb.append(" AND vd.STATUS in ('A','B','E') ");

		sb.append(" AND ((vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "') or (vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "')) ");
		sb.append(" AND vd.voucher_status = 1");
		//sb.append(" AND vd.treasury_code = " + treasuryCode);
		sb.append(" AND vd.DDO_CODE  like '"+treasuryCode+"%' ");
		sb.append(" AND vd.BILL_GROUP_ID is not null ");
		sb.append(" AND vd.VOUCHER_AMOUNT<>0 ");
		sb.append(" order by vd.DDO_CODE ASC");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());		
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}

	public List getUnMatchedVouchersForMatchingFromTreasuryNetData(String lStrFromDate,
			String lStrToDate, Long treasuryCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT tn.MST_DCPS_TREASURYNET_DATA_ID,tn.voucher_no,cast(tn.dcps_amount as BIGINT),tn.voucher_date,tn.ddo_code ,tn.FROM_SCHEME ");
		sb.append(" FROM MST_DCPS_TREASURYNET_DATA tn, sgvc_fin_year_mst FY ");
		sb.append(" WHERE");
		sb.append(" FY.fin_year_desc = tn.year_desc ");
		sb.append(" AND tn.STATUS in ('A','B','E') ");
		sb.append(" AND tn.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		//sb.append(" AND  tn.treasury_code = " + treasuryCode);
		sb.append(" AND tn.DDO_CODE  like '"+treasuryCode+"%' ");
		sb.append(" order by tn.DDO_CODE ASC");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());		
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}


	public List getUnMatchedVouchersAllForMatching(String lStrFromDate,
			String lStrToDate, Long treasuryCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT DISTINCT vd.mst_dcps_contri_voucher_dtls,vd.voucher_no,vd.voucher_amount,vd.voucher_date as mstVoDt,bg.DESCRIPTION,vd.ddo_code ");
		sb.append(" FROM  mst_dcps_contri_voucher_dtls vd, mst_dcps_bill_group bg, sgvc_fin_year_mst FY,trn_dcps_contribution tr ");
		sb.append(" WHERE vd.bill_group_id = bg.bill_group_id ");
		sb.append(" AND vd.manually_matched = 0");
		sb.append(" AND FY.fin_year_id = vd.year_id ");
		sb.append(" AND ((vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "') or (vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "')) ");
		sb.append(" AND vd.treasury_code = " + treasuryCode);
		sb.append(" AND tr.RLT_CONTRI_VOUCHER_ID = vd.MST_DCPS_CONTRI_VOUCHER_DTLS");
		sb.append(" AND tr.REG_STATUS = 1");
		sb.append(" AND vd.VOUCHER_NO IS NOT NULL AND vd.VOUCHER_DATE IS NOT NULL");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}


	public List selectDcpsIdForVoucherInTrn(Long voucherIdPk) throws Exception {

		StringBuilder sb  = new StringBuilder();

		List lLstReturnList = null;


		sb.append(" SELECT dtls.BILL_GROUP_ID,dtls.MONTH_ID,fin.FIN_YEAR_DESC FROM MST_DCPS_CONTRI_VOUCHER_DTLS dtls inner join SGVC_FIN_YEAR_MST fin on dtls.year_id =fin.FIN_YEAR_ID  ");
		sb.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID=dtls.MONTH_ID  ");
		sb.append("  where MST_DCPS_CONTRI_VOUCHER_DTLS =:voucherIdPk ");


		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		
		selectQuery.setParameter("voucherIdPk", voucherIdPk);
		lLstReturnList = selectQuery.list();
		return lLstReturnList;
	}



	public void updateVouchersManuallyMatched (Long voucherIdPk,String Remark) throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" UPDATE MstDcpsContriVoucherDtls VC SET VC.manuallyMatched = 1,VC.status = 'F',MANUAL_REMARK= :Remark WHERE dcpsContriVoucherDtlsId = :voucherIdPk");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("voucherIdPk", voucherIdPk);
			lQuery.setParameter("Remark", Remark);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}
	public void updateVouchersManuallyTrnsMatched (Long voucherIdPk,Long LStrNewVchrNo,String LStrNewSchemeCode,Date LStrNewVchrDate) throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" UPDATE TrnDcpsContribution trn  SET    ");

			if(LStrNewVchrNo!=null){
				lSBQuery.append("trn.voucherNo=:LStrNewVchrNo");
			}
			if(LStrNewVchrDate!=null && LStrNewVchrNo!=null){
				lSBQuery.append(" ,trn.voucherDate=:LStrNewVchrDate");
			}
			else  if(LStrNewVchrDate!=null)
			{
				lSBQuery.append(" trn.voucherDate=:LStrNewVchrDate");
			}
			if(LStrNewSchemeCode!=null  && LStrNewVchrDate!=null ){
				lSBQuery.append(" ,trn.schemeCode=:LStrNewSchemeCode");
			}
			else if(LStrNewSchemeCode!=null && LStrNewVchrNo!=null)
			{
				lSBQuery.append(" ,trn.schemeCode=:LStrNewSchemeCode");
			}
			else if(LStrNewSchemeCode!=null){
				lSBQuery.append(" trn.schemeCode=:LStrNewSchemeCode");
			}

			lSBQuery.append(", trn.updatedDate=sysdate WHERE trn.rltContriVoucherId = :voucherIdPk ");



			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("voucherIdPk", voucherIdPk);
			if(LStrNewVchrNo!=null){
				lQuery.setParameter("LStrNewVchrNo", LStrNewVchrNo);
			}
			if(LStrNewVchrDate!=null){
				lQuery.setParameter("LStrNewVchrDate", LStrNewVchrDate);
			}
			if(LStrNewSchemeCode!=null){
				lQuery.setParameter("LStrNewSchemeCode", LStrNewSchemeCode);
			}
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}


	public List getAllSubTreasuries (String treasuryCode)  {
		List lstSubTrsy = null;
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" Select CM.locId , CM.locName from CmnLocationMst CM where departmentId in (100006,100003) and LANG_ID = 1 and LOC_ID<>1111  and LOC_ID <>9991");
			if(treasuryCode != null)
				lSBQuery.append(" and CM.parentLocId = "+treasuryCode+" or LOC_ID ="+treasuryCode);	
			lSBQuery.append("	order by CM.locId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());

 
			List lLstResult = lQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lstSubTrsy = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[0].toString()+"-"+obj[1].toString());
					lstSubTrsy.add(lObjComboValuesVO);
				}
			} else {
				lstSubTrsy = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lstSubTrsy.add(lObjComboValuesVO);
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lstSubTrsy;	
	}

	public Long getRoleOfUserFrmPostId (Long postId)  {
		Long lstSubTrsy = null;
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT role_id FROM ACL_POSTROLE_RLT where POST_ID = "+postId+" and ACTIVATE_FLAG = 1  ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			List lLstResult = lQuery.list();

			if(lLstResult != null && !lLstResult.isEmpty()){
				lstSubTrsy = Long.parseLong(lLstResult.get(0).toString());
			}


		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lstSubTrsy;
	}

	public List getPayBillId (Long dcpsId,Long monthId,String yearDesc) throws Exception  {

		List lLstReturnList = null;
		StringBuilder lSBQuery = new StringBuilder("");



		gLogger.info("dcpsEmpid is ******"+dcpsId);
		gLogger.info("monthId is *****"+monthId);
		gLogger.info("yearDesc is here*****"+yearDesc);
		try{
			lSBQuery.append(" SELECT distinct bill.PAYBILL_ID FROM mst_dcps_emp emp ");
			lSBQuery.append(" inner join TRN_DCPS_CONTRIBUTION  trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
			lSBQuery.append(" inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
			lSBQuery.append(" inner join HR_PAY_PAYBILL pay on pay.EMP_ID=eis.EMP_ID ");
			lSBQuery.append(" inner join PAYBILL_HEAD_MPG bill on bill.PAYBILL_ID=pay.PAYBILL_GRP_ID ");
			lSBQuery.append(" where pay.PAYBILL_MONTH = "+ monthId );  
			lSBQuery.append(" and pay.PAYBILL_YEAR =" +yearDesc ); 
			lSBQuery.append(" and bill.APPROVE_FLAG=1 and trn.DCPS_EMP_ID="+dcpsId);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		Query selectQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//gLogger.info(); 

		lLstReturnList = selectQuery.list();
		return lLstReturnList;

	}

	public void updateVouchersManuallyPayMatched (Long LStrNewVchrNo,Date LStrNewVchrDate,Long billGrpId,Long monthid,String Year) throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" UPDATE PAYBILL_HEAD_MPG pay set  ");

			if(LStrNewVchrDate!=null && LStrNewVchrNo!=null ){
				lSBQuery.append(" pay.voucher_no=:LStrNewVchrNo ,pay.voucher_date=:LStrNewVchrDate ");
			}
			else if(LStrNewVchrNo!=null){
				lSBQuery.append(" pay.voucher_no=:LStrNewVchrNo");
			}
			 
			else if(LStrNewVchrDate!=null){
				lSBQuery.append(" pay.voucher_date=:LStrNewVchrDate ");
			}


			lSBQuery.append(" ,UPDATED_DATE=sysdate WHERE pay.bill_No = :billGrpId and approve_Flag=1 and PAYBILL_MONTH=:monthid and PAYBILL_YEAR=:Year");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			if(LStrNewVchrNo!=null){
				lQuery.setParameter("LStrNewVchrNo", LStrNewVchrNo);
			}
			if(LStrNewVchrDate!=null){
				lQuery.setParameter("LStrNewVchrDate", LStrNewVchrDate);
			}
			lQuery.setParameter("billGrpId", billGrpId);
			lQuery.setParameter("monthid", monthid);
			lQuery.setParameter("Year", Year);

			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}

	public List getAllDDOForTreasury(String tresuryCode){
		StringBuilder sb = new StringBuilder();

		sb
		.append("SELECT DM.ddo_Code, DM.ddo_Name FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM ");
		sb
		.append("WHERE RO.location_Code = :locationCode AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code AND LM.location_Code <> 9991 ");
		sb.append(" order by DM.ddo_Code");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		selectQuery.setParameter("locationCode", tresuryCode);
		List lLstResult = selectQuery.list();
		return lLstResult;
	}
	public List getFinyears() {
		
		Calendar c = Calendar.getInstance();
	    int year = c.get(Calendar.YEAR);
	    int month = c.get(Calendar.MONTH);
	    if(4>month+1){
	        year = year-1;
	    }

		//String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode  in  (2012,2013,2014) ";
		//String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode  in (2007,2008,2009,2010,2011,2012,2013,2014) ";
		String query = "select finYearId,finYearDesc from SgvcFinYearMst where (finYearCode >=2012 and finYearCode <="+year+") ";
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

	public Boolean checkUpdateStatusDtls(String ddoCode,String voucherNo,Date voucherDate,String Schemcode) throws Exception {

		StringBuilder sb  = new StringBuilder();
		Boolean Status=false;
		List lLstReturnList=null;
        Query lQuery=null;

		sb.append(" SELECT * FROM  MST_DCPS_CONTRI_VOUCHER_DTLS where voucher_no=:voucherNo and voucher_date=:voucherDate and scheme_code=:Schemcode and ddo_code=:ddoCode and status ='F' ");
		

		lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("voucherNo", voucherNo);
		lQuery.setParameter("voucherDate", voucherDate);
		lQuery.setParameter("Schemcode", Schemcode);
		lQuery.setParameter("ddoCode", ddoCode);
		lLstReturnList = lQuery.list();
		
		if(lLstReturnList.size()>=1)
		{
			Status=true;
		}
		
		
		return Status;
	}

	public List checkStatusOfMatch(String ddoCode,String voucherNo,String Schemcode,Date voucherDate,String Voucheramt) throws Exception {

		StringBuilder sb  = new StringBuilder();
		Boolean Status=false;
		List lLstReturnList=null;
        Query lQuery=null;

        gLogger.info("ddoCode in dao is"+ddoCode);
        gLogger.info("voucherNo in dao is"+voucherNo);
        gLogger.info("Schemcode in dao is"+Schemcode);
        gLogger.info("voucherDate in dao is"+voucherDate);
        gLogger.info("Voucheramt in dao is"+Voucheramt);
    
		sb.append(" SELECT voucher_no,voucher_date FROM  MST_DCPS_CONTRI_VOUCHER_DTLS where  voucher_no=:voucherNo and voucher_date=:voucherDate and scheme_code=:Schemcode and voucher_amount=:Voucheramt and ddo_code=:ddoCode  and  status ='F'  ");
		

		lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("voucherNo", voucherNo);
		lQuery.setParameter("voucherDate", voucherDate);
		lQuery.setParameter("Schemcode", Schemcode);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("Voucheramt",Voucheramt);
		lLstReturnList = lQuery.list();
		
		
		return lLstReturnList;
	}
	public List getAcMainDetails() throws Exception{

		String query = " SELECT distinct emp.AC_DCPS_MAINTAINED_BY,look.LOOKUP_NAME FROM mst_dcps_emp  emp inner join CMN_LOOKUP_MST look   on look.LOOKUP_ID=emp.AC_DCPS_MAINTAINED_BY   order by look.LOOKUP_NAME ";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
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
	
	


}
