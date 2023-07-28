/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 May 30, 2011
 */
public class PostEmpContriDAOImpl extends GenericDaoHibernateImpl implements PostEmpContriDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public PostEmpContriDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	/*
	public List getAllContributions(String userType, Long finYear, String contriMonth) {

		List listPostEmpContri = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT dcpsPostEmpContriIdPk,finYear,contriMonth,billNo,billAmount,voucherNo,voucherDate,statusFlag,generateBill ");
		lSBQuery.append(" FROM PostEmpContri WHERE ");
		if (userType.equals("SRKA")) {
			lSBQuery.append("finYear = :finYear And contriMonth = :contriMonth And statusFlag IN ('D','R','A','F')");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finYear", finYear);
			lQuery.setParameter("contriMonth", contriMonth);
		}
		if (userType.equals("PAO")) {
			lSBQuery.append(" statusFlag='F'");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
		}

		listPostEmpContri = lQuery.list();

		return listPostEmpContri;
	}
	*/
	
	public List getAllContributions(String userType, Long finYear ,String accMain) {

		List listPostEmpContri = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT dcpsPostEmpContriIdPk,finYear,finYear,billNo,billAmount,voucherNo,voucherDate,statusFlag,generateBill ");
		lSBQuery.append(" FROM PostEmpContri WHERE ");
		if (userType.equals("SRKA")) {
			lSBQuery.append(" finYear = :finYear And statusFlag IN ('D','R','A','F' ) ");
			lSBQuery.append(" AND acDcpsMaintainedBy=  '"+accMain+"'   ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finYear", finYear);
		}
		if (userType.equals("PAO")) {
			lSBQuery.append(" statusFlag='F'");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
		}

		listPostEmpContri = lQuery.list();

		return listPostEmpContri;
	}


	public Long getSancBudget(Long finYear) {

		List<Long> listSancBudget = null;

		Long lLngTotalBudget = 0L;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT totalBudget FROM SanctionedBudget WHERE dcpsSancBudgetFinYear= :finYear ORDER BY CreatedDate DESC");


		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		listSancBudget = lQuery.list();

		if (listSancBudget.size() != 0 && listSancBudget != null) {
			if (listSancBudget.get(0) != null) {
				lLngTotalBudget = listSancBudget.get(0);
			}
		}
		return lLngTotalBudget;
	}

	public Long getSancBudgetForAcc(Long finYear,String accMain) {

		gLogger.info(" Account main by-------  "+accMain);
		List<Long> listSancBudget = null;

		Long lLngTotalBudget = 0L;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT totalBudget FROM SanctionedBudget WHERE dcpsSancBudgetFinYear= :finYear");
     
		 if(accMain!="" && !accMain.equals("") && Long.parseLong(accMain)==700240) 
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000016 ");
		 }	 
		 else if(accMain!="" && !accMain.equals("") && Long.parseLong(accMain)==700241)	
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000017 ");
		 }
		 else if(accMain!="" && !accMain.equals("") && Long.parseLong(accMain)==700242)	
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000018 ");
		 }
			
		 else
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000015 ");
		 }
		 lSBQuery.append(" ORDER BY CreatedDate DESC");
		
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		listSancBudget = lQuery.list();

		if (listSancBudget.size() != 0 && listSancBudget != null) {
			if (listSancBudget.get(0) != null) {
				lLngTotalBudget = listSancBudget.get(0);
			}
		}
		else
		{
			lLngTotalBudget=0L;
		}
		return lLngTotalBudget;
	}
	
	
	
	public Long getSancBudgetPK(Long finYear) {

		Long lSancBudgetPK = null;
		List listSancBudget = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT dcpsSanctionedBudgetIdPk FROM SanctionedBudget WHERE dcpsSancBudgetFinYear= :finYear");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		listSancBudget = lQuery.list();
		if (listSancBudget != null && listSancBudget.size() != 0) {
			lSancBudgetPK = (Long) listSancBudget.get(0);
		}
		return lSancBudgetPK;
	}

	public PostEmpContri getPostEmpContriVOForGivenMonthAndYear(Long monthId, Long yearId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<PostEmpContri> tempList = null;
		PostEmpContri PostEmpContriVO = null;
		String lStrMonth = monthId.toString().trim();
		lSBQuery.append("FROM PostEmpContri WHERE finYear = :yearId and contriMonth = :monthId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setLong("yearId", yearId);
		lQuery.setParameter("monthId", lStrMonth);

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			PostEmpContriVO = tempList.get(0);
		}
		return PostEmpContriVO;

	}

	public Long getExpenditure(Long finYear,String accMain ) {
		List<Long> listExpenditure = null;

		Long lLngExpenditure = 0L;

		StringBuilder lSBQuery = new StringBuilder();
	     gLogger.info("accMain-----------"+accMain);
		Query lQuery = null;

		lSBQuery.append(" SELECT sum(billAmount) FROM PostEmpContri WHERE finYear= :finYear ");
		lSBQuery.append(" AND acDcpsMaintainedBy=  '"+accMain+"'   ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
	
		listExpenditure = lQuery.list();
        gLogger.info("Query is -------"+lQuery);
		if (listExpenditure.size() != 0 && listExpenditure != null) {
			if (listExpenditure.get(0) != null) {
				lLngExpenditure = listExpenditure.get(0).longValue();
			}
		}
		return lLngExpenditure;
	}

	public String getBillNumber(Long finYear,String accMain) {
		List listExpenditure = null;

		Long lLngCount = 0L;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT count(billNo) FROM PostEmpContri WHERE finYear= :finYear");
		lSBQuery.append(" AND acDcpsMaintainedBy=  '"+accMain+"'   ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		listExpenditure = lQuery.list();

		if (listExpenditure.size() != 0 && listExpenditure != null) {
			if (listExpenditure.get(0) != null) {
				lLngCount = Long.parseLong(listExpenditure.get(0).toString());
			}

		}

		return String.format("%03d", lLngCount + 1);
	}


	public String getBillNumber(Long finYear) {
		List listExpenditure = null;

		Long lLngCount = 0L;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT count(billNo) FROM PostEmpContri WHERE finYear= :finYear ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		listExpenditure = lQuery.list();

		if (listExpenditure.size() != 0 && listExpenditure != null) {
			if (listExpenditure.get(0) != null) {
				lLngCount = Long.parseLong(listExpenditure.get(0).toString());
			}

		}

		return String.format("%03d", lLngCount + 1);
	}
	
	
	
	
	
	
	public Long getExcessAmount(Long finYear,String accMain) {

		List<Long> listExcess = null;

		Long lLngExcessAmount = 0L;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT excessExpenditure FROM PostEmpContri WHERE finYear= :finYear ORDER BY CreatedDate DESC");
		lSBQuery.append(" AND acDcpsMaintainedBy=  '"+accMain+"'   ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		listExcess = lQuery.list();

		if (listExcess.size() != 0 && listExcess != null) {
			if (listExcess.get(0) != null) {
				lLngExcessAmount = listExcess.get(0);
			}
		}
		return lLngExcessAmount;
	}

	
	/*public Double getExpInCurrBill(String finYearCode, Long monthId) {

		List<Double> listExpInCurrBill = null;
		Double lDoubleExpIncurrBill = 0d;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("select sum(VA) from (SELECT CV.voucher_amount AS VA,CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
		//lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		//lSBQuery.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 ");
		
		//lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution TR,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		// join of trn_dcps_contribution table removed, as it will have lakhs of rows and make the join heavy, scheme_code introduced in mst_dcps_contri_voucher_dtls table and its join is made with from_scheme of mst_dcps_treasurynet_data table.
		lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		//lSBQuery.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 ");
		//join of trn_dcps_contribution table removed, as it will have lakhs of rows and make the join heavy, scheme_code introduced in mst_dcps_contri_voucher_dtls table and its join is made with from_scheme of mst_dcps_treasurynet_data table.
		lSBQuery.append(" WHERE FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 ");		
		lSBQuery.append(" AND CV.voucher_no = tn.voucher_no AND CV.treasury_code = TN.treasury_code AND CV.ddo_code = TN.ddo_code AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND CV.SCHEME_CODE = TN.from_scheme ");
		// below line added later
		lSBQuery.append(" AND FY.fin_year_id=CV.year_id");
		
		// Below condition changed as to show expenditure only of that year, not including all previous years.
		
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId))");
		}
		
		lSBQuery.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId");
		}
		lSBQuery.append(" UNION");
		lSBQuery.append(" SELECT CV.voucher_amount AS VA,CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQuery.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1");
		
		//Below condition changed as to show expenditure only of that year, not including all previous years.
		
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3))))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId)))");
		}
		
		
		lSBQuery.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId)");
		}

		// lSBQuery.append(" SELECT sum(CV.voucher_amount) ");
		// lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV");
		// lSBQuery.append(" JOIN mst_dcps_bill_group BG ON BG.bill_group_id = CV.bill_group_id");
		// lSBQuery
		// .append(" JOIN mst_dcps_treasurynet_data TN ON CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code AND");
		// lSBQuery
		// .append(" (( CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date) OR (CV.manually_matched = 1))  AND BG.scheme_code = TN.from_scheme ");
		// lSBQuery.append(" JOIN sgvc_fin_year_mst FY ON FY.fin_year_desc = TN.year_desc");
		// lSBQuery.append(" JOIN sgva_month_mst FM ON FM.month_id = CV.month_id");
		// lSBQuery.append(" WHERE CV.post_emplr_contri_status = 0");
		// lSBQuery.append(" AND CV.voucher_status = 1");
		// lSBQuery
		// .append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND CV.month_id <= :monthId))");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("finYearCode", finYearCode);
		lQuery.setParameter("monthId", monthId);
		listExpInCurrBill = lQuery.list();

		if (listExpInCurrBill.size() != 0 && listExpInCurrBill != null) {
			if (listExpInCurrBill.get(0) != null) {
				lDoubleExpIncurrBill = listExpInCurrBill.get(0);
			}
		}
		return lDoubleExpIncurrBill;
	}*/
	
	// Above query commented as treasury-net data table's join is to be removed while matching
	
	/*
	public Double getExpInCurrBill(String finYearCode, Long monthId) {

		List<Double> listExpInCurrBill = null;
		Double lDoubleExpIncurrBill = 0d;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("select sum(VA) from (SELECT CV.voucher_amount AS VA,CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
		//lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		//lSBQuery.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 ");
		
		//lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution TR,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		// join of trn_dcps_contribution table removed, as it will have lakhs of rows and make the join heavy, scheme_code introduced in mst_dcps_contri_voucher_dtls table and its join is made with from_scheme of mst_dcps_treasurynet_data table.
		lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		//lSBQuery.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 ");
		//join of trn_dcps_contribution table removed, as it will have lakhs of rows and make the join heavy, scheme_code introduced in mst_dcps_contri_voucher_dtls table and its join is made with from_scheme of mst_dcps_treasurynet_data table.
		lSBQuery.append(" WHERE CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.STATUS = 'F' ");		
		// below line added later
		lSBQuery.append(" AND FY.fin_year_id = CV.year_id");
		
		// Below condition changed as to show expenditure only of that year, not including all previous years.
		
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId))");
		}
		
		lSBQuery.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId");
		}
		lSBQuery.append(" UNION");
		lSBQuery.append(" SELECT CV.voucher_amount AS VA,CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQuery.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1");
		
		//Below condition changed as to show expenditure only of that year, not including all previous years.
		
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3))))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId)))");
		}
		
		
		lSBQuery.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId)");
		}

		// lSBQuery.append(" SELECT sum(CV.voucher_amount) ");
		// lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV");
		// lSBQuery.append(" JOIN mst_dcps_bill_group BG ON BG.bill_group_id = CV.bill_group_id");
		// lSBQuery
		// .append(" JOIN mst_dcps_treasurynet_data TN ON CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code AND");
		// lSBQuery
		// .append(" (( CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date) OR (CV.manually_matched = 1))  AND BG.scheme_code = TN.from_scheme ");
		// lSBQuery.append(" JOIN sgvc_fin_year_mst FY ON FY.fin_year_desc = TN.year_desc");
		// lSBQuery.append(" JOIN sgva_month_mst FM ON FM.month_id = CV.month_id");
		// lSBQuery.append(" WHERE CV.post_emplr_contri_status = 0");
		// lSBQuery.append(" AND CV.voucher_status = 1");
		// lSBQuery
		// .append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND CV.month_id <= :monthId))");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("finYearCode", finYearCode);
		lQuery.setParameter("monthId", monthId);
		listExpInCurrBill = lQuery.list();

		if (listExpInCurrBill.size() != 0 && listExpInCurrBill != null) {
			if (listExpInCurrBill.get(0) != null) {
				lDoubleExpIncurrBill = listExpInCurrBill.get(0);
			}
		}
		return lDoubleExpIncurrBill;
	}
	
	*/

	// Above query commented as Period wise expenditure is to be shown

	public Double getExpInCurrBillPrdWise(Long finYearId,String lStrFromDate,String lStrToDate) {

		List<Double> listExpInCurrBill = null;
		Double lDoubleExpIncurrBill = 0d;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("select sum(VA) from (SELECT CV.voucher_amount AS VA,CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
		//lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		//lSBQuery.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 ");
		
		//lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution TR,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		// join of trn_dcps_contribution table removed, as it will have lakhs of rows and make the join heavy, scheme_code introduced in mst_dcps_contri_voucher_dtls table and its join is made with from_scheme of mst_dcps_treasurynet_data table.
		lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		//lSBQuery.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 ");
		//join of trn_dcps_contribution table removed, as it will have lakhs of rows and make the join heavy, scheme_code introduced in mst_dcps_contri_voucher_dtls table and its join is made with from_scheme of mst_dcps_treasurynet_data table.
		lSBQuery.append(" WHERE CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.STATUS = 'F' ");		
		// below line added later
		lSBQuery.append(" AND FY.fin_year_id = CV.year_id");
		
		// Below condition changed as to show expenditure only of that year, not including all previous years.
		
		lSBQuery.append(" AND CV.year_id = :finYearId");
		//lSBQuery.append(" AND CV.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		lSBQuery.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate");
		
		
		/*
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId))");
		}
		
		lSBQuery.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId");
		}
		*/
		lSBQuery.append(" UNION");
		lSBQuery.append(" SELECT CV.voucher_amount AS VA,CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQuery.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1");
		lSBQuery.append(" AND CV.year_id = :finYearId");
		//lSBQuery.append(" AND CV.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		lSBQuery.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate )");
		
		//Below condition changed as to show expenditure only of that year, not including all previous years.
		
		/*
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3))))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId)))");
		}
		
		
		lSBQuery.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId)");
		}
		*/

		// lSBQuery.append(" SELECT sum(CV.voucher_amount) ");
		// lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV");
		// lSBQuery.append(" JOIN mst_dcps_bill_group BG ON BG.bill_group_id = CV.bill_group_id");
		// lSBQuery
		// .append(" JOIN mst_dcps_treasurynet_data TN ON CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code AND");
		// lSBQuery
		// .append(" (( CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date) OR (CV.manually_matched = 1))  AND BG.scheme_code = TN.from_scheme ");
		// lSBQuery.append(" JOIN sgvc_fin_year_mst FY ON FY.fin_year_desc = TN.year_desc");
		// lSBQuery.append(" JOIN sgva_month_mst FM ON FM.month_id = CV.month_id");
		// lSBQuery.append(" WHERE CV.post_emplr_contri_status = 0");
		// lSBQuery.append(" AND CV.voucher_status = 1");
		// lSBQuery
		// .append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND CV.month_id <= :monthId))");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("finYearCode", finYearCode);
		//lQuery.setParameter("monthId", monthId);
		
		lQuery.setParameter("finYearId", finYearId);
		lQuery.setParameter("fromDate", lStrFromDate);
		lQuery.setParameter("toDate", lStrToDate);
		listExpInCurrBill = lQuery.list();

		if (listExpInCurrBill.size() != 0 && listExpInCurrBill != null) {
			if (listExpInCurrBill.get(0) != null) {
				lDoubleExpIncurrBill = listExpInCurrBill.get(0);
			}
		}
		return lDoubleExpIncurrBill;
	}
	
	public Double getExpInCurrBillPrdWiseFromTrn(Long finYearId,String lStrFromDate,String lStrToDate,String lStrAcDcpsMntndBy) {

		List<Double> listExpInCurrBill = null;
		Double lDoubleExpIncurrBill = 0d;
           gLogger.info("Account maintained by**********"+lStrAcDcpsMntndBy);
		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" select sum(VA) from (  " );
		
		lSBQuery.append(" SELECT TR.CONTRIBUTION AS VA,TR.DCPS_CONTRIBUTION_ID");
		lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,TRN_DCPS_CONTRIBUTION TR,MST_DCPS_EMP EM");
		lSBQuery.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS ");		
		lSBQuery.append(" AND TR.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
		lSBQuery.append(" and TR.POST_EMPLR_CONTRI_STATUS = 0");
		lSBQuery.append(" AND CV.voucher_status = 1 AND CV.STATUS = 'F'  ");
		//lSBQuery.append(" AND CV.year_id = :finYearId");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY = :AcDcpsMntndBy ");
		lSBQuery.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate");
		lSBQuery.append(" AND TR.REG_STATUS = 1  AND EM.LOC_ID <> 380001 ");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'N' AND CV.bill_group_id is not null " );
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT is null or TR.IS_CHALLAN is null) ");
		
		if(lStrAcDcpsMntndBy.trim().equals("700240") || lStrAcDcpsMntndBy.trim().equals("700241") || lStrAcDcpsMntndBy.trim().equals("700242"))
		{
			  gLogger.info("Account maintained by* if*********"+lStrAcDcpsMntndBy);
			lSBQuery.append(" AND EM.PRAN_NO is not null " );
		}
		lSBQuery.append(" AND CV.voucher_status is not null AND CV.voucher_amount<>0  AND CV.voucher_amount is not null");
		
		lSBQuery.append(" UNION");
		
		lSBQuery.append(" SELECT TR.CONTRIBUTION AS VA,TR.DCPS_CONTRIBUTION_ID");
		lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,TRN_DCPS_CONTRIBUTION TR,MST_DCPS_EMP EM");
		lSBQuery.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		lSBQuery.append(" AND TR.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
		lSBQuery.append(" AND TR.POST_EMPLR_CONTRI_STATUS = 0 ");
		lSBQuery.append(" AND CV.voucher_status = 1 AND   CV.STATUS = 'F' ");  
		//lSBQuery.append(" AND CV.year_id = :finYearId");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY = :AcDcpsMntndBy ");
		lSBQuery.append("  AND CV.MISSING_CREDIT_APPROVAL_DATE BETWEEN :fromDate AND :toDate");
		lSBQuery.append(" AND TR.REG_STATUS = 1 AND EM.LOC_ID <> 380001 ");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'N'  AND CV.bill_group_id is not null");
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT='Y' or TR.IS_CHALLAN='Y') ");
		if(lStrAcDcpsMntndBy.trim().equals("700240") || lStrAcDcpsMntndBy.trim().equals("700241") || lStrAcDcpsMntndBy.trim().equals("700242"))
		{
			  gLogger.info("Account maintained by* if*********"+lStrAcDcpsMntndBy);
			lSBQuery.append(" AND EM.PRAN_NO is not null " );
		}
		lSBQuery.append(" AND  CV.voucher_status is not null AND CV.voucher_amount<>0 AND CV.voucher_amount is not null ");
		lSBQuery.append(" )");
		
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		//lQuery.setParameter("finYearId", finYearId);
		lQuery.setParameter("fromDate", lStrFromDate);
		lQuery.setParameter("toDate", lStrToDate);
		lQuery.setParameter("AcDcpsMntndBy", lStrAcDcpsMntndBy.trim());
		
		listExpInCurrBill = lQuery.list();

		if (listExpInCurrBill.size() != 0 && listExpInCurrBill != null) {
			if (listExpInCurrBill.get(0) != null) {
				lDoubleExpIncurrBill = listExpInCurrBill.get(0);
			}
		}
		return lDoubleExpIncurrBill;
	}
	
	public void updateTrnDcpsContributionList(String finYearCode, Long monthId,String lStrBillNo,Long lLongYearId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		//Below query was not necessary as VoucherIds can be obtained by the bill no only. 
		
		/*
		lSBQuery.append(" UPDATE trn_dcps_contribution SET employer_contri_flag='Y',status='G' ");
		//lSBQuery.append(" WHERE status <> 'H' and rlt_contri_voucher_id IN ");
		lSBQuery.append(" WHERE rlt_contri_voucher_id IN ");
		lSBQuery.append(" (SELECT CV.mst_dcps_contri_voucher_dtls ");
		lSBQuery
				.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY");
		lSBQuery
				.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 2 AND CV.voucher_status = 1 ");
		lSBQuery
				.append(" AND CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND BG.scheme_code = TN.from_scheme ");
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id>3)))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId))");
		}
		lSBQuery.append(" UNION");
		lSBQuery
				.append(" SELECT CV.mst_dcps_contri_voucher_dtls FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQuery
				.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 2 And  CV.manually_matched = 1");
		lSBQuery.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQuery.append(" AND (CV.month_id <= :monthId OR CV.month_id>3))))");
		} else {
			lSBQuery.append(" AND CV.month_id <= :monthId)))");
		}
		
		*/
		
		lSBQuery.append(" UPDATE trn_dcps_contribution SET employer_contri_flag='Y',status='G' ");
		lSBQuery.append(" WHERE rlt_contri_voucher_id IN ");
		lSBQuery.append(" ( SELECT CV.mst_dcps_contri_voucher_dtls ");
		lSBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV WHERE EMPLR_BILL_NO = :billNo AND EMPLR_YEAR_ID = :yearId ) ");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", lLongYearId);
		//lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("billNo", lStrBillNo.trim());
		lQuery.executeUpdate();

	}
	
	public void updateTrnDcpsContributionListInTrn(String lStrBillNo,Long lLongYearId) throws Exception  {

		/*
		try {
			StringBuilder lSBQuery = new StringBuilder();

			Query lQuery = null;

			lSBQuery.append(" UPDATE trn_dcps_contribution SET employer_contri_flag = :employerContriFlag,status = :status,POST_EMPLR_CONTRI_STATUS = :postEmplrContriStatus ");
			lSBQuery.append(" WHERE EMPLR_BILL_NO = :billNo ");
			lSBQuery.append(" AND EMPLR_YEAR_ID = :yearId");
			lSBQuery.append(" AND POST_EMPLR_CONTRI_STATUS = :previousEmplrContriStatus");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("yearId", lLongYearId);
			lQuery.setParameter("billNo", lStrBillNo.trim());
			lQuery.setParameter("previousEmplrContriStatus", 2l);
			lQuery.setParameter("postEmplrContriStatus", 1l);
			lQuery.setCharacter("status", 'G');
			lQuery.setCharacter("employerContriFlag", 'Y');
			
			lQuery.executeUpdate();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		*/
		
		// Code to call procedure from Java
		
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updateTrnDcpsContributionListInTrn (?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrBillNo);
			lClbStmnt.setLong(2, lLongYearId);
			
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		finally{
			lClbStmnt.close();
		}
		

	}

	/*public void updateBillNoAndYearIdForPostEmpcontri(String lStrBillno, Long lLongYearId, String finYearCode,
			Long monthId) {

		StringBuilder lSBQueryForGettingIds = new StringBuilder();
		List<BigInteger> lListVoucherPks = null;
		Long dcpsContriVoucherDtlsId = null;
		BigInteger bigIntdcpsContriVoucherDtlsId = null;

		
		lSBQueryForGettingIds.append(" SELECT CV.mst_dcps_contri_voucher_dtls ");
		lSBQueryForGettingIds.append(" FROM mst_dcps_contri_voucher_dtls CV");
		lSBQueryForGettingIds.append(" JOIN mst_dcps_bill_group BG ON BG.bill_group_id = CV.bill_group_id");
		lSBQueryForGettingIds.append(" JOIN mst_dcps_treasurynet_data TN ON CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code AND");
		lSBQueryForGettingIds.append(" (( CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date) OR (CV.manually_matched = 1))  AND BG.scheme_code = TN.from_scheme ");
		lSBQueryForGettingIds.append(" JOIN sgvc_fin_year_mst FY ON FY.fin_year_desc = TN.year_desc");
		lSBQueryForGettingIds.append(" JOIN sgva_month_mst FM ON FM.month_id = CV.month_id");
		lSBQueryForGettingIds.append(" WHERE CV.post_emplr_contri_status = 0");
		lSBQueryForGettingIds.append(" AND CV.voucher_status = 1");
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND CV.month_id <= :monthId))");
		
		
		//Above query changed as it was wrong and did not bring correct voucher Ids. 
		
		
		lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY "); 
		lSBQueryForGettingIds.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1"); 
		lSBQueryForGettingIds.append(" AND CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code");
		lSBQueryForGettingIds.append(" AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND BG.scheme_code = TN.from_scheme");  
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND (CV.month_id <= :monthId OR CV.month_id > :monthId)))");
		lSBQueryForGettingIds.append(" UNION SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQueryForGettingIds.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1"); 
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND (CV.month_id <= :monthId OR CV.month_id > :monthId)))");
		
		
		// Same above query corrected to set months properly which were set at the time of getting expenditure
		
		//lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY ");
		//lSBQueryForGettingIds.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1");
		//lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution TR,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY ");
		// trn_dcps_contribution removed from the above query as it will cause a heavy join.
		lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY ");
		//lSBQueryForGettingIds.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1"); 
		lSBQueryForGettingIds.append(" WHERE FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1");
		lSBQueryForGettingIds.append(" AND CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code");
		//lSBQueryForGettingIds.append(" AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND BG.scheme_code = TN.from_scheme");  
		lSBQueryForGettingIds.append(" AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND CV.SCHEME_CODE = TN.from_scheme");
		// below line added later
		lSBQueryForGettingIds.append(" AND FY.fin_year_id=CV.year_id");
		
		//Below condition changed as to update contributions only of that year, not including all previous years.
		
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode ");
		
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId))");
		}
		
		
		lSBQueryForGettingIds.append(" AND FY.fin_year_code = :finYearCode ");
		
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId");
		}
		
		lSBQueryForGettingIds.append(" UNION SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQueryForGettingIds.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1"); 
		
		//Below condition changed as to update contributions only of that year, not including all previous years.
		
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId))");
		}
		
		lSBQueryForGettingIds.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId");
		}
		
		Query lQueryForGettingIds = ghibSession.createSQLQuery(lSBQueryForGettingIds.toString());
		lQueryForGettingIds.setParameter("finYearCode", finYearCode);
		lQueryForGettingIds.setParameter("monthId", monthId);

		
		lListVoucherPks = lQueryForGettingIds.list();
		
		
		StringBuilder lSBQuery = null;
		Query lQuery = null;

		for (Integer lInt = 0; lInt < lListVoucherPks.size(); lInt++) {
			bigIntdcpsContriVoucherDtlsId = lListVoucherPks.get(lInt);
			dcpsContriVoucherDtlsId = bigIntdcpsContriVoucherDtlsId.longValue();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
			lSBQuery
					.append(" SET emplrBillNo = :billNo ,emplrYearId = :yearId,postEmplrContriStatus = :postEmplrContriStatus ");
			lSBQuery.append(" WHERE dcpsContriVoucherDtlsId = :dcpsContriVoucherDtlsId ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billNo", lStrBillno);
			lQuery.setParameter("yearId", lLongYearId);
			lQuery.setParameter("dcpsContriVoucherDtlsId", dcpsContriVoucherDtlsId);
			lQuery.setParameter("postEmplrContriStatus", 2l);
			lQuery.executeUpdate();
		}
		
		
		
		// A simple code of update that uses passing a whole list for updates
		

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
		lSBQuery.append(" SET emplrBillNo = :billNo ,emplrYearId = :yearId,postEmplrContriStatus = :postEmplrContriStatus ");
		lSBQuery.append(" WHERE dcpsContriVoucherDtlsId in (:dcpsContriVoucherDtlsId) ");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameterList("dcpsContriVoucherDtlsId", lListVoucherPks);
		lQuery.executeUpdate();
	}*/
	
	// Above query commented as treasurynet data's join was to be removed as was not necessary
	
	
/*	public void updateBillNoAndYearIdForPostEmpcontri(String lStrBillno, Long lLongYearId, String finYearCode,
			Long monthId) {

		StringBuilder lSBQueryForGettingIds = new StringBuilder();
		List<BigInteger> lListVoucherPks = null;
		Long dcpsContriVoucherDtlsId = null;
		BigInteger bigIntdcpsContriVoucherDtlsId = null;

		
		lSBQueryForGettingIds.append(" SELECT CV.mst_dcps_contri_voucher_dtls ");
		lSBQueryForGettingIds.append(" FROM mst_dcps_contri_voucher_dtls CV");
		lSBQueryForGettingIds.append(" JOIN mst_dcps_bill_group BG ON BG.bill_group_id = CV.bill_group_id");
		lSBQueryForGettingIds.append(" JOIN mst_dcps_treasurynet_data TN ON CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code AND");
		lSBQueryForGettingIds.append(" (( CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date) OR (CV.manually_matched = 1))  AND BG.scheme_code = TN.from_scheme ");
		lSBQueryForGettingIds.append(" JOIN sgvc_fin_year_mst FY ON FY.fin_year_desc = TN.year_desc");
		lSBQueryForGettingIds.append(" JOIN sgva_month_mst FM ON FM.month_id = CV.month_id");
		lSBQueryForGettingIds.append(" WHERE CV.post_emplr_contri_status = 0");
		lSBQueryForGettingIds.append(" AND CV.voucher_status = 1");
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND CV.month_id <= :monthId))");
		
		
		//Above query changed as it was wrong and did not bring correct voucher Ids. 
		
		
		lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY "); 
		lSBQueryForGettingIds.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1"); 
		lSBQueryForGettingIds.append(" AND CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code");
		lSBQueryForGettingIds.append(" AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND BG.scheme_code = TN.from_scheme");  
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND (CV.month_id <= :monthId OR CV.month_id > :monthId)))");
		lSBQueryForGettingIds.append(" UNION SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQueryForGettingIds.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1"); 
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode AND (CV.month_id <= :monthId OR CV.month_id > :monthId)))");
		
		
		// Same above query corrected to set months properly which were set at the time of getting expenditure
		
		//lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_bill_group BG,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY ");
		//lSBQueryForGettingIds.append(" WHERE BG.bill_group_id = CV.bill_group_id and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1");
		//lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution TR,mst_dcps_treasurynet_data TN,sgvc_fin_year_mst FY ");
		// trn_dcps_contribution removed from the above query as it will cause a heavy join.
		lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY ");
		//lSBQueryForGettingIds.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS and FY.fin_year_desc = TN.year_desc and CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1"); 
		lSBQueryForGettingIds.append(" WHERE CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.STATUS = 'F'");
		//lSBQueryForGettingIds.append(" AND CV.voucher_no = tn.voucher_no AND CV.treasury_code=TN.treasury_code AND  CV.ddo_code = TN.ddo_code");
		//lSBQueryForGettingIds.append(" AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND BG.scheme_code = TN.from_scheme");  
		//lSBQueryForGettingIds.append(" AND CV.voucher_amount = tn.dcps_amount AND CV.voucher_date=tn.voucher_date AND CV.SCHEME_CODE = TN.from_scheme");
		// below line added later
		lSBQueryForGettingIds.append(" AND FY.fin_year_id=CV.year_id");
		
		//Below condition changed as to update contributions only of that year, not including all previous years.
		
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode ");
		
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId))");
		}
		
		
		lSBQueryForGettingIds.append(" AND FY.fin_year_code = :finYearCode ");
		
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId");
		}
		
		lSBQueryForGettingIds.append(" UNION SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQueryForGettingIds.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1"); 
		
		//Below condition changed as to update contributions only of that year, not including all previous years.
		
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId))");
		}
		
		lSBQueryForGettingIds.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId");
		}
		
		Query lQueryForGettingIds = ghibSession.createSQLQuery(lSBQueryForGettingIds.toString());
		lQueryForGettingIds.setParameter("finYearCode", finYearCode);
		lQueryForGettingIds.setParameter("monthId", monthId);

		
		lListVoucherPks = lQueryForGettingIds.list();
		
		
		StringBuilder lSBQuery = null;
		Query lQuery = null;

		for (Integer lInt = 0; lInt < lListVoucherPks.size(); lInt++) {
			bigIntdcpsContriVoucherDtlsId = lListVoucherPks.get(lInt);
			dcpsContriVoucherDtlsId = bigIntdcpsContriVoucherDtlsId.longValue();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
			lSBQuery
					.append(" SET emplrBillNo = :billNo ,emplrYearId = :yearId,postEmplrContriStatus = :postEmplrContriStatus ");
			lSBQuery.append(" WHERE dcpsContriVoucherDtlsId = :dcpsContriVoucherDtlsId ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billNo", lStrBillno);
			lQuery.setParameter("yearId", lLongYearId);
			lQuery.setParameter("dcpsContriVoucherDtlsId", dcpsContriVoucherDtlsId);
			lQuery.setParameter("postEmplrContriStatus", 2l);
			lQuery.executeUpdate();
		}
		
		
		
		// A simple code of update that uses passing a whole list for updates
		

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
		lSBQuery.append(" SET emplrBillNo = :billNo ,emplrYearId = :yearId,postEmplrContriStatus = :postEmplrContriStatus ");
		lSBQuery.append(" WHERE dcpsContriVoucherDtlsId in (:dcpsContriVoucherDtlsId) ");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameterList("dcpsContriVoucherDtlsId", lListVoucherPks);
		lQuery.setParameter("billNo", lStrBillno);
		lQuery.setParameter("yearId", lLongYearId);
		lQuery.setParameter("postEmplrContriStatus", 2l);
		lQuery.executeUpdate();
	}
*/

	
	// Above method commented as bill amount is brought period wise
	
	public void updateBillNoAndYearIdForPostEmpcontri(String lStrBillno, Long lLongYearId, String finYearCode,String lStrFromDate,String lStrToDate ) {

		StringBuilder lSBQueryForGettingIds = new StringBuilder();
		List<BigInteger> lListVoucherPks = null;
		List<Long> lListLongVoucherPks = new ArrayList<Long>();
		Long dcpsContriVoucherDtlsId = null;
		BigInteger bigIntdcpsContriVoucherDtlsId = null;

		lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY ");
		lSBQueryForGettingIds.append(" WHERE CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.STATUS = 'F'");
		lSBQueryForGettingIds.append(" AND FY.fin_year_id=CV.year_id");
		
		lSBQueryForGettingIds.append(" AND CV.year_id = :finYearId");
		lSBQueryForGettingIds.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate");
		
		/*
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode ");
		
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId))");
		}
		
		lSBQueryForGettingIds.append(" AND FY.fin_year_code = :finYearCode ");
		
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId");
		}
		*/
		
		lSBQueryForGettingIds.append(" UNION SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY");
		lSBQueryForGettingIds.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1");
		lSBQueryForGettingIds.append(" AND CV.year_id = :finYearId");
		//lSBQuery.append(" AND CV.voucher_date  BETWEEN '" + lStrFromDate + "' AND '"+ lStrToDate + "'");
		lSBQueryForGettingIds.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate ");
		
		//Below condition changed as to update contributions only of that year, not including all previous years.
		
		/*
		lSBQueryForGettingIds.append(" AND (FY.fin_year_code < :finYearCode OR (FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)))");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId))");
		}
		
		lSBQueryForGettingIds.append(" AND FY.fin_year_code = :finYearCode");
		if (monthId <= 3) {
			lSBQueryForGettingIds.append(" AND (CV.month_id <= :monthId OR CV.month_id > 3)");
		} else {
			lSBQueryForGettingIds.append(" AND CV.month_id <= :monthId");
		}
		*/
		
		Query lQueryForGettingIds = ghibSession.createSQLQuery(lSBQueryForGettingIds.toString());
		//lQueryForGettingIds.setParameter("finYearCode", finYearCode);
		//lQueryForGettingIds.setParameter("monthId", monthId);
		lQueryForGettingIds.setParameter("finYearId", lLongYearId);
		lQueryForGettingIds.setParameter("fromDate", lStrFromDate);
		lQueryForGettingIds.setParameter("toDate", lStrToDate);

		lListVoucherPks = lQueryForGettingIds.list();
		
		Long lLongVoucherPK = null;
		
		for(Integer lCount=0;lCount<lListVoucherPks.size();lCount++)
		{
			lLongVoucherPK = lListVoucherPks.get(lCount).longValue();
			lListLongVoucherPks.add(lLongVoucherPK);
		}
		
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
		lSBQuery.append(" SET emplrBillNo = :billNo ,emplrYearId = :yearId,postEmplrContriStatus = :postEmplrContriStatus ");
		lSBQuery.append(" WHERE dcpsContriVoucherDtlsId in (:dcpsContriVoucherDtlsId) ");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameterList("dcpsContriVoucherDtlsId", lListLongVoucherPks);
		lQuery.setParameter("billNo", lStrBillno);
		lQuery.setParameter("yearId", lLongYearId);
		lQuery.setParameter("postEmplrContriStatus", 2l);
		lQuery.executeUpdate();
		
	}
	
	public void updateBillNoAndYearIdForPostEmpcontriWithAcMntndBy(String lStrBillno, Long lLongYearId, String finYearCode,String lStrFromDate,String lStrToDate,String lStrAcDcpsMntndBy,Date lDateFromDate,Date lDateToDate) throws Exception {

		/*
		
		try {
			StringBuilder lSBQueryForGettingIds = new StringBuilder();
			List<BigInteger> lListVoucherPks = null;
			List<Long> lListLongVoucherPks = new ArrayList<Long>();
			Long dcpsContriVoucherDtlsId = null;
			BigInteger bigIntdcpsContriVoucherDtlsId = null;

			lSBQueryForGettingIds.append(" SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY,trn_dcps_contribution TR,mst_dcps_emp EM ");
			lSBQueryForGettingIds.append(" WHERE CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.STATUS = 'F'");
			lSBQueryForGettingIds.append(" AND TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
			lSBQueryForGettingIds.append(" AND TR.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
			lSBQueryForGettingIds.append(" AND EM.AC_DCPS_MAINTAINED_BY = :AcDcpsMntndBy ");
			lSBQueryForGettingIds.append(" AND FY.fin_year_id=CV.year_id");
			//lSBQueryForGettingIds.append(" AND CV.year_id = :finYearId");
			lSBQueryForGettingIds.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate");
			lSBQueryForGettingIds.append(" UNION SELECT CV.MST_DCPS_CONTRI_VOUCHER_DTLS FROM mst_dcps_contri_voucher_dtls CV,sgvc_fin_year_mst FY,trn_dcps_contribution TR,mst_dcps_emp EM");
			lSBQueryForGettingIds.append(" WHERE FY.fin_year_id=CV.year_id AND CV.post_emplr_contri_status = 0 AND CV.voucher_status = 1 AND CV.manually_matched = 1");
			lSBQueryForGettingIds.append(" AND TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
			lSBQueryForGettingIds.append(" AND TR.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
			lSBQueryForGettingIds.append(" AND EM.AC_DCPS_MAINTAINED_BY = :AcDcpsMntndBy ");
			//lSBQueryForGettingIds.append(" AND CV.year_id = :finYearId");
			lSBQueryForGettingIds.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate ");
			
			Query lQueryForGettingIds = ghibSession.createSQLQuery(lSBQueryForGettingIds.toString());
			//lQueryForGettingIds.setParameter("finYearId", lLongYearId);
			lQueryForGettingIds.setParameter("fromDate", lStrFromDate);
			lQueryForGettingIds.setParameter("toDate", lStrToDate);
			lQueryForGettingIds.setParameter("AcDcpsMntndBy", lStrAcDcpsMntndBy.trim());

			lListVoucherPks = lQueryForGettingIds.list();
			
			Long lLongVoucherPK = null;
			
			for(Integer lCount=0;lCount<lListVoucherPks.size();lCount++)
			{
				lLongVoucherPK = lListVoucherPks.get(lCount).longValue();
				lListLongVoucherPks.add(lLongVoucherPK);
			}
			
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;

			lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
			lSBQuery.append(" SET emplrBillNo = :billNo ,emplrYearId = :yearId,postEmplrContriStatus = :postEmplrContriStatus ");
			lSBQuery.append(" WHERE dcpsContriVoucherDtlsId in (:dcpsContriVoucherDtlsId) ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("dcpsContriVoucherDtlsId", lListLongVoucherPks);
			lQuery.setParameter("billNo", lStrBillno);
			lQuery.setParameter("yearId", lLongYearId);
			lQuery.setParameter("postEmplrContriStatus", 2l);
			lQuery.executeUpdate();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
			throw e;
		}
		
		
		*/
		// Code to call procedure from DB
		
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updateBillNoAndYearIdForPostEmpcontriWithAcMntndBy (?,?,?,?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrBillno);
			lClbStmnt.setLong(2, lLongYearId);
			lClbStmnt.setDate(3, (java.sql.Date) lDateFromDate);
			lClbStmnt.setDate(4, (java.sql.Date) lDateToDate);
			lClbStmnt.setString(5, lStrAcDcpsMntndBy);
			
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		finally{
			lClbStmnt.close();
		}
		
	}
	
	public void updateBillNoAndYearIdForPostEmpcontriInTrn(String lStrBillno, Long lLongYearId, String finYearCode,String lStrFromDate,String lStrToDate,String lStrAcDcpsMntndBy,Date lDateFromDate,Date lDateToDate) throws Exception {

		/*
		try {
			StringBuilder lSBQueryForGettingIds = new StringBuilder();
			List<BigInteger> lListTrnPks = null;
			List<Long> lListLongTrnPks = new ArrayList<Long>();

			lSBQueryForGettingIds.append(" SELECT TR.DCPS_CONTRIBUTION_ID FROM mst_dcps_contri_voucher_dtls CV,TRN_DCPS_CONTRIBUTION TR,MST_DCPS_EMP EM ");
			lSBQueryForGettingIds.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
			lSBQueryForGettingIds.append(" AND TR.DCPS_EMP_ID = EM.DCPS_EMP_ID ");
			lSBQueryForGettingIds.append(" and TR.POST_EMPLR_CONTRI_STATUS = 0");
			lSBQueryForGettingIds.append(" AND CV.voucher_status = 1 AND CV.STATUS in ('F','G')");
			//lSBQueryForGettingIds.append(" AND CV.year_id = :finYearId");
			lSBQueryForGettingIds.append(" AND EM.AC_DCPS_MAINTAINED_BY = :AcDcpsMntndBy ");
			lSBQueryForGettingIds.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate");
			lSBQueryForGettingIds.append(" AND TR.REG_STATUS = 1");
			lSBQueryForGettingIds.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'N'");
			
			lSBQueryForGettingIds.append(" UNION");
			
			lSBQueryForGettingIds.append(" SELECT TR.DCPS_CONTRIBUTION_ID");
			lSBQueryForGettingIds.append(" FROM mst_dcps_contri_voucher_dtls CV,TRN_DCPS_CONTRIBUTION TR,MST_DCPS_EMP EM");
			lSBQueryForGettingIds.append(" WHERE TR.RLT_CONTRI_VOUCHER_ID = CV.MST_DCPS_CONTRI_VOUCHER_DTLS ");
			lSBQueryForGettingIds.append(" AND TR.DCPS_EMP_ID = EM.DCPS_EMP_ID");
			lSBQueryForGettingIds.append(" AND TR.POST_EMPLR_CONTRI_STATUS = 0");
			lSBQueryForGettingIds.append(" AND CV.voucher_status = 1 AND CV.manually_matched = 1 ");
			//lSBQueryForGettingIds.append(" AND CV.year_id = :finYearId");
			lSBQueryForGettingIds.append(" AND EM.AC_DCPS_MAINTAINED_BY = :AcDcpsMntndBy ");
			lSBQueryForGettingIds.append(" AND CV.voucher_date BETWEEN :fromDate AND :toDate");
			lSBQueryForGettingIds.append(" AND TR.REG_STATUS = 1");
			lSBQueryForGettingIds.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'N'");
			
			Query lQueryForGettingIds = ghibSession.createSQLQuery(lSBQueryForGettingIds.toString());
			//lQueryForGettingIds.setParameter("finYearId", lLongYearId);
			lQueryForGettingIds.setParameter("fromDate", lStrFromDate);
			lQueryForGettingIds.setParameter("toDate", lStrToDate);
			lQueryForGettingIds.setParameter("AcDcpsMntndBy", lStrAcDcpsMntndBy.trim());
			

			lListTrnPks = lQueryForGettingIds.list();
			
			Long lLongTrnPK = null;
			
			for(Integer lCount=0;lCount<lListTrnPks.size();lCount++)
			{
				lLongTrnPK = lListTrnPks.get(lCount).longValue();
				lListLongTrnPks.add(lLongTrnPK);
			}
			
			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;

			lSBQuery.append(" Update TrnDcpsContribution ");
			lSBQuery.append(" SET emplrBillNo = :billNo ,emplrYearId = :yearId,postEmplrContriStatus = :postEmplrContriStatus ");
			lSBQuery.append(" WHERE dcpsContributionId in (:dcpsContributionIdList) ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("dcpsContributionIdList", lListLongTrnPks);
			lQuery.setParameter("billNo", lStrBillno);
			lQuery.setParameter("yearId", lLongYearId);
			lQuery.setParameter("postEmplrContriStatus", 2l);
			lQuery.executeUpdate();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		
		*/
		// Code to call procedure from DB
		
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updateBillNoAndYearIdForPostEmpcontriInTrn (?,?,?,?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrBillno);
			lClbStmnt.setLong(2, lLongYearId);
			lClbStmnt.setDate(3, (java.sql.Date) lDateFromDate);
			lClbStmnt.setDate(4, (java.sql.Date) lDateToDate);
			lClbStmnt.setString(5, lStrAcDcpsMntndBy);
			
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		
		finally{
			lClbStmnt.close();
		}
	}

	public void updateVoucherPostEmpStatusOnApproval(String lStrBillno, Long lLongYearId) throws Exception {

		/*
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
			lSBQuery.append(" SET postEmplrContriStatus = :postEmplrContriStatus , status = :status  ");
			//lSBQuery.append(" SET postEmplrContriStatus = :postEmplrContriStatus , status = :status, emplrVoucherAmount = voucherAmount  ");
			lSBQuery.append(" WHERE postEmplrContriStatus = :previousEmplrContriStatus ");
			lSBQuery.append(" AND emplrBillNo = :billNo ");
			lSBQuery.append(" AND emplrYearId = :yearId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billNo", lStrBillno);
			lQuery.setParameter("yearId", lLongYearId);
			lQuery.setParameter("postEmplrContriStatus", 1l);
			lQuery.setParameter("previousEmplrContriStatus", 2l);
			lQuery.setCharacter("status", 'G');
			lQuery.executeUpdate();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		*/
		
		// Code to call procedure from Java
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updateVoucherPostEmpStatusOnApproval (?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrBillno);
			lClbStmnt.setLong(2, lLongYearId);
			
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		
		finally{
			lClbStmnt.close();
		}
	}
	
	public void updatePostEmplrVoucherDtlsOfApprovedBills(String lStrBillno, Long lLongYearId,
			Long lLongEmplrVoucherNo, Date lDateEmplrVoucherDate) throws Exception {

		/*
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" Update MstDcpsContriVoucherDtls ");
			lSBQuery.append(" SET emplrVoucherNo = :emplrVoucherNo, emplrVoucherDate = :emplrVoucherDate ");
			lSBQuery.append(" WHERE  emplrBillNo = :billNo ");
			lSBQuery.append(" AND emplrYearId = :yearId ");
			lSBQuery.append(" AND postEmplrContriStatus = 1");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billNo", lStrBillno);
			lQuery.setParameter("yearId", lLongYearId);
			lQuery.setParameter("emplrVoucherNo", lLongEmplrVoucherNo);
			lQuery.setParameter("emplrVoucherDate", lDateEmplrVoucherDate);
			lQuery.executeUpdate();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		
		*/
		
		java.sql.Date lDateEmplrVoucherDateSQL = new java.sql.Date(lDateEmplrVoucherDate.getTime());
		
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updatePostEmplrVoucherDtlsOfApprovedBills (?,?,?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrBillno);
			lClbStmnt.setLong(2, lLongYearId);
			lClbStmnt.setLong(3, lLongEmplrVoucherNo);
			lClbStmnt.setDate(4, lDateEmplrVoucherDateSQL);
			
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		finally{
			lClbStmnt.close();
		}
	}
	
	public void updateBillNoAndYearIdForPostEmpcontriInTrnAndMstBoth(String lStrBillno, Long lLongYearId, String lStrAcDcpsMntndBy,Date lDateFromDate,Date lDateToDate) throws Exception {

		// Code to call procedure from DB
		
		java.sql.Date lDateFromDateSQL = new java.sql.Date(lDateFromDate.getTime());
		java.sql.Date lDateToDateSQL = new java.sql.Date(lDateToDate.getTime());
		
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updateBillNoAndYearIdForPostEmpcontriInTrnAndMstBoth (?,?,?,?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrBillno);
			lClbStmnt.setLong(2, lLongYearId);
			lClbStmnt.setDate(3, lDateFromDateSQL);
			lClbStmnt.setDate(4, lDateToDateSQL);
			lClbStmnt.setString(5, lStrAcDcpsMntndBy);
			
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		finally{
			lClbStmnt.close();
		}
		
	}
	
	public void updateVoucherPostEmpStatusOnApprovalInTrnAndMstBoth(String lStrBillno, Long lLongYearId) throws Exception {

		
		// Code to call procedure from Java
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updateVoucherPostEmpStatusOnApprovalInTrnAndMstBoth (?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrBillno);
			lClbStmnt.setLong(2, lLongYearId);
			
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		finally{
			lClbStmnt.close();
		}
		
	}
	public String getFinYear(String lStrPostEmpContriId,String accMain) {
		
		List listFinYr = null;

		String  finYear = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT  FIN_YEAR  FROM MST_DCPS_POST_EMPLOYER_CONTRI where DCPS_POST_EMP_CONTRI_ID=:lStrPostEmpContriId");
		lSBQuery.append(" AND AC_DCPS_MAINTAINED_BY=:accMain   ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		
		lQuery.setString("accMain",accMain);
		lQuery.setString("lStrPostEmpContriId",lStrPostEmpContriId);  
		
		listFinYr=lQuery.list();

		if (listFinYr.size() != 0 && listFinYr != null) {
			if (listFinYr.get(0) != null) {
				finYear = listFinYr.get(0).toString();
			}

		}

		return  finYear;
	}
	public String accMain(String accMain) {
		List listaccMain = null;

		String  accMainby = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
		
		if(accMain.equals("777")){
			accMainby="All AIS Employees";
		}
		else{
		lSBQuery.append("SELECT distinct decode(look.LOOKUP_NAME,'A/c Maintained BY IAS','IAS','A/c Maintained BY IPS','IPS','A/c Maintained BY IFS','IFS','SRKA(GOV)') ac_maintainedby   FROM CMN_LOOKUP_MST look inner join mst_dcps_emp emp on emp.AC_DCPS_MAINTAINED_BY=look.LOOKUP_ID where  emp.AC_DCPS_MAINTAINED_BY=:accMain ");
	
	     lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	 	lQuery.setString("accMain",accMain);
		
		listaccMain=lQuery.list();
		if (listaccMain.size() != 0 && listaccMain != null) {
			if (listaccMain.get(0) != null) {
				accMainby = listaccMain.get(0).toString();
			}
		}
		}
		
	
		return accMainby;
	}
   public Date getBillGendt(String lStrPostEmpContriId,String accMain) {
		
		List listFinYr = null;

		Date  BillGendate = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT  CREATED_DATE FROM MST_DCPS_POST_EMPLOYER_CONTRI where DCPS_POST_EMP_CONTRI_ID=:lStrPostEmpContriId");
		lSBQuery.append(" AND AC_DCPS_MAINTAINED_BY=:accMain   ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		
		lQuery.setString("accMain",accMain);
		lQuery.setString("lStrPostEmpContriId",lStrPostEmpContriId);  
		
		listFinYr=lQuery.list();

		if (listFinYr.size() != 0 && listFinYr != null) {
			if (listFinYr.get(0) != null) {
				BillGendate = (Date) listFinYr.get(0);
			}

		}

		return  BillGendate;
	}
}
