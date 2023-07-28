/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 12,2013	        Ashish Sharma			
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Dec 12,2013
 */
public class MatchContriEntryDAOTOImpl extends GenericDaoHibernateImpl implements
     MatchContriEntryDAOTO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/pensionproc/PensionCaseConstants");

	public MatchContriEntryDAOTOImpl(Class type, SessionFactory sessionFactory) {

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
			String lStrToDate, String subTreasury,Long acMain) {

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
		
	/*	sb.append(" SELECT vd.treasury_code,LM.loc_name,CAST(SUM(vd.voucher_amount) as DECIMAL(20,0)),CAST(SUM(tn.DCPS_AMOUNT) as DECIMAL(20,0)) FROM ");
		sb.append(" mst_dcps_contri_voucher_dtls vd, mst_dcps_treasurynet_data tn,  cmn_location_mst LM , sgvc_fin_year_mst FY,mst_dcps_emp emp");
		sb.append(" WHERE ");
		sb.append(" vd.treasury_code = tn.treasury_code AND vd.ddo_code = tn.ddo_code and trim(vd.scheme_code) = trim(tn.FROM_SCHEME) AND vd.voucher_no = tn.voucher_no AND vd.voucher_amount = tn.dcps_amount AND vd.voucher_date=tn.voucher_date");
		sb.append(" AND vd.treasury_code = LM.loc_id ");
		sb.append(" AND FY.fin_year_desc = tn.year_desc ");
		sb.append(" AND FY.fin_year_id = vd.year_id AND");
		sb.append(" vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		sb.append(" AND vd.STATUS = 'F'");
		sb.append(" and vd.TREASURY_CODE in ("+subTreasury+")");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name");
		sb.append(" order by vd.treasury_code ");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();*/
		
		Query lQuery = null;
		Query lQuery1 = null;
		sb.append(" SELECT cv.treasury_code,loc.loc_name,cast(SUM(TR.CONTRIBUTION) as DECIMAL(20,0)) as contribution,count(MST_DCPS_CONTRI_VOUCHER_DTLS) FROM trn_dcps_contribution tr  ");
		sb.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS CV on tr.RLT_CONTRI_VOUCHER_ID= CV.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		sb.append(" inner join CMN_LOCATION_MST loc on  loc.LOC_ID=cv.TREASURY_CODE ");
		sb.append(" inner join mst_dcps_emp em on TR.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
		sb.append(" where cv.STATUS = 'F' AND  cv.voucher_amount<>0 AND cv.voucher_status=1    ");
		sb.append(" and tr.TREASURY_CODE like '"+subTreasury+"' ");
		sb.append(" AND cv.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' and em.AC_DCPS_MAINTAINED_BY="+acMain+"   ");
		sb.append(" AND TR.REG_STATUS = 1  AND TR.EMPLOYER_CONTRI_FLAG = 'N' AND CV.bill_group_id is not null   ");
		sb.append(" AND CV.voucher_amount<>0  AND CV.voucher_amount is not null  and TR.POST_EMPLR_CONTRI_STATUS = 0  ");
		sb.append(" GROUP BY loc.loc_id,cv.treasury_code, loc.loc_name  order by cv.treasury_code   ");
		lQuery  = ghibSession.createSQLQuery(sb.toString());
	
		lLstReturnList = lQuery.list();

		StringBuilder sb1 = new StringBuilder();
		sb1.append(" SELECT '','Total', cast(SUM(TR.CONTRIBUTION) as DECIMAL(20,0))  as contribution,count(MST_DCPS_CONTRI_VOUCHER_DTLS)  FROM mst_dcps_contri_voucher_dtls CV ");
		sb1.append(" inner join TRN_DCPS_CONTRIBUTION TR on TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS 	 ");
		sb1.append(" inner join MST_DCPS_EMP EM  on TR.DCPS_EMP_ID = EM.DCPS_EMP_ID  ");
		sb1.append(" inner join CMN_LOCATION_MST loc on  loc.LOC_ID=cv.TREASURY_CODE ");
		sb1.append(" WHERE  TR.POST_EMPLR_CONTRI_STATUS = 0  AND CV.voucher_status = 1 AND CV.STATUS = 'F'  ");
		sb1.append(" and tr.TREASURY_CODE like  '"+subTreasury+"' ");
		sb1.append(" AND cv.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "' and em.AC_DCPS_MAINTAINED_BY="+acMain+"   ");
		sb1.append(" AND TR.REG_STATUS = 1 AND TR.EMPLOYER_CONTRI_FLAG = 'N' AND CV.bill_group_id is not null   ");
		sb1.append(" AND CV.voucher_amount<>0  AND CV.voucher_amount is not null ");
		lQuery1 = ghibSession.createSQLQuery(sb1.toString());

		List lLstLastRow= lQuery1.list();
		gLogger.info("Last row size is "+lLstLastRow.size());
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
		
		sb.append(" AND vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" GROUP BY LM.loc_id,vd.treasury_code, LM.loc_name");
		sb.append(" order by vd.treasury_code ");
		
		

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}
	
	public List getAllTreasuriesForUnMatchedEntriesMstcontri(String lStrFromDate,String lStrToDate,String subTresury) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT vd.treasury_code,LM.loc_name,CAST(SUM(vd.voucher_amount) as DECIMAL(20,0))");
		sb.append(" FROM CMN_LOCATION_MST LM,MST_DCPS_CONTRI_VOUCHER_DTLS vd");
		sb.append(" WHERE vd.treasury_code = LM.loc_id and vd.treasury_code<>'1111'");
		sb.append(" AND vd.voucher_date BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		sb.append(" AND vd.STATUS in ('A','B','E')");
		sb.append(" and vd.TREASURY_CODE in ("+subTresury+")");
		sb.append(" GROUP BY vd.treasury_code,LM.loc_name");
		sb.append(" order by vd.treasury_code ");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}
	
	public List getAllTreasuriesForUnMatchedEntriesTreasuryNet(String lStrFromDate,String lStrToDate,String subTresury) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT tn.treasury_code,LM.loc_name,CAST(SUM(tn.dcps_amount) as DECIMAL(20,0))");
		sb.append(" FROM CMN_LOCATION_MST LM,MST_DCPS_TREASURYNET_DATA tn");
		sb.append(" WHERE tn.treasury_code = LM.loc_id  ");
		sb.append(" AND tn.voucher_date BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		sb.append(" AND tn.STATUS in ('A','B','E')");
		sb.append(" and tn.TREASURY_CODE in ("+subTresury+")");
		sb.append(" GROUP BY tn.treasury_code,LM.loc_name");
		sb.append(" order by tn.treasury_code ");
		
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
		sb.append(" AND vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" AND vd.voucher_status = 1");
		sb.append(" AND vd.treasury_code = " + treasuryCode);
		sb.append(" AND vd.manually_matched = 0");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}
	
	
	public List getUnMatchedVouchersForMatchingFromMstContriVoucherDtls(String lStrFromDate,
			String lStrToDate, String treasuryCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT vd.treasury_code,vd.mst_dcps_contri_voucher_dtls,vd.voucher_no,cast(vd.voucher_amount as BIGINT),vd.voucher_date as mstVoDt,vd.ddo_code,vd.SCHEME_CODE,mon.MONTH_NAME,case when mon.MONTH_ID>3 then cast(fin.FIN_YEAR_CODE  as  BIGINT)  else cast(fin.FIN_YEAR_CODE as  BIGINT)+1 end Year1,MANUAL_CHANGE_COUNT  ");
		sb.append(" FROM mst_dcps_contri_voucher_dtls vd, sgvc_fin_year_mst FY ");
		sb.append(" WHERE");
		sb.append(" vd.manually_matched = 0 ");
		sb.append(" AND vd.STATUS in ('A','B','E') ");
		sb.append(" AND FY.fin_year_id = vd.year_id ");
		sb.append(" AND vd.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" AND vd.voucher_status = 1");
		sb.append(" AND vd.treasury_code = " + treasuryCode);
		sb.append(" AND vd.BILL_GROUP_ID is not null ");
		sb.append(" AND vd.VOUCHER_AMOUNT<>0 ");
		sb.append(" order by vd.DDO_CODE ASC");
		sb.append(" order by vd.treasury_code ");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}
	
	public List getUnMatchedVouchersForMatchingFromTreasuryNetData(String lStrFromDate,
			String lStrToDate, String treasuryCode) {

		List lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT tn.MST_DCPS_TREASURYNET_DATA_ID,tn.voucher_no,cast(tn.dcps_amount as BIGINT),tn.voucher_date treVoDt,tn.ddo_code ");
		sb.append(" FROM MST_DCPS_TREASURYNET_DATA tn, sgvc_fin_year_mst FY ");
		sb.append(" WHERE");
		sb.append(" FY.fin_year_desc = tn.year_desc ");
		sb.append(" AND tn.STATUS in ('A','B','E') ");
		sb.append(" AND tn.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"
				+ lStrToDate + "'");
		sb.append(" AND  tn.treasury_code = " + treasuryCode);
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
		sb.append(" AND vd.voucher_date BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		sb.append(" AND vd.treasury_code = " + treasuryCode);
		sb.append(" AND tr.RLT_CONTRI_VOUCHER_ID = vd.MST_DCPS_CONTRI_VOUCHER_DTLS");
		sb.append(" AND tr.REG_STATUS = 1");
		sb.append(" AND vd.VOUCHER_NO IS NOT NULL AND vd.VOUCHER_DATE IS NOT NULL");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		lLstReturnList = selectQuery.list();

		return lLstReturnList;
	}
	
	
	public void updateVouchersManuallyMatched (Long voucherIdPk) throws Exception {
		
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			
			lSBQuery.append(" UPDATE MstDcpsContriVoucherDtls VC SET VC.manuallyMatched = 1,VC.status = 'F' WHERE dcpsContriVoucherDtlsId = :voucherIdPk");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("voucherIdPk", voucherIdPk);
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

			lSBQuery.append(" Select CM.locId , CM.locName from CmnLocationMst CM where departmentId in (100006,100003) and LANG_ID = 1 and LOC_ID<>1111 ");
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
	
	
	@Override
	public List getSubTresuryList(String strLocationCode) {
	

		List subTresuryList = null;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT LOC_ID FROM CMN_LOCATION_MST where PARENT_LOC_ID='"+strLocationCode+"' or LOC_ID='"+strLocationCode+"'");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		subTresuryList = selectQuery.list();

		return subTresuryList;
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
	public List getFinyears() {

		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode = 2012 ";
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
	public List getAllDDOForTreasury(String tresuryCode){
		StringBuilder sb = new StringBuilder();

		sb
		.append("SELECT DM.ddo_Code, DM.ddo_Name FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM ");
		sb
		.append("WHERE RO.location_Code = :locationCode AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code");
		sb.append(" order by DM.ddo_Code");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		selectQuery.setParameter("locationCode", tresuryCode);
		List lLstResult = selectQuery.list();
		return lLstResult;
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
	public Long getSubTreasuryList(String strLocationCode) {
		
       Long treasuryCode=null;
		List subTresuryList = null;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT distinct substr(loc_id,1,2) FROM CMN_LOCATION_MST where PARENT_LOC_ID='"+strLocationCode+"' or LOC_ID='"+strLocationCode+"'");

		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		subTresuryList = selectQuery.list();
		
		if(subTresuryList!=null && subTresuryList.size()>0)
		{
			treasuryCode=Long.parseLong(subTresuryList.get(0).toString());
		}
		return treasuryCode;
	}



	
	
}