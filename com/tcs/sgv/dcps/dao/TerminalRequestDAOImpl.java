/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 25, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;


/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Sept 11, 2014
 */
public class TerminalRequestDAOImpl extends GenericDaoHibernateImpl implements TerminalRequestDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	/**
	 * 
	 * @param type
	 * @param sessionFactory
	 */
	public TerminalRequestDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);

		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public List getEmpDtlsForName(String lStrName, String lStrDDOCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("SELECT dcpsEmpId,dcpsId,doj FROM MstEmp where name = :name and ddoCode = :ddoCode and dcpsId is not null");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("name", lStrName.trim());
		query.setParameter("ddoCode", lStrDDOCode.trim());

		List resultList = query.list();
		return resultList;
	}

	public List getAllMissingCreditsForEmp(Long lLongEmpId, Date lDtDOJ, Date lDtTerminationDate) throws Exception {

		List listMissingCredits = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" select ofy.fin_year_desc,ofm.month_name,ofy.fin_year_id,ofm.month_id,nvl(MC.MISSING_CREDIT_ID,0),MC.AMOUNT_DEDUCTION,MC.VOUCHER_NO,MC.VOUCHER_DATE,MC.REMARKS from ");
		lSBQuery.append(" (");
		lSBQuery.append(" select fir.total_year as total_year,fir.total_month as total_month from ");
		lSBQuery
		.append(" (Select Fy.Fin_Year_code As Total_Year,Fm.Month_no Total_month From Sgva_Month_Mst Fm,Sgvc_Fin_Year_Mst Fy Where  (Date(Fy.Fin_Year_Code || '-' || Fm.Month_No || '-' ||01) Between '"
				+ sdf1.format(lDtDOJ) + "' AND '" + sdf1.format(lDtTerminationDate) + "' ) And Fm.Lang_Id = 'en_US') As Fir");
		lSBQuery.append(" Left Outer Join  ");
		lSBQuery.append(" (Select Year(Tr.Startdate) As Pay_Year,Month(Tr.Startdate) As Pay_Month From Trn_Dcps_Contribution Tr Where Tr.Dcps_Emp_Id = " + lLongEmpId + " And Tr.Startdate > '"
				+ sdf1.format(lDtDOJ) + "' And Tr.Enddate < '" + sdf1.format(lDtTerminationDate) + "'  or (Month(Tr.Enddate) = Month('" + sdf1.format(lDtTerminationDate)
				+ "') and year(Tr.Enddate) = year('" + sdf1.format(lDtTerminationDate) + "')) and Tr.type_of_payment in (700046,700047)) As Sec");
		lSBQuery.append(" On (Fir.Total_Year = Sec.Pay_Year And Fir.Total_Month = Sec.Pay_Month) ");
		lSBQuery.append(" Where Pay_Month Is Null And Pay_year is null");
		lSBQuery.append(" order by 1,2");
		lSBQuery.append(" ) as thi ");
		lSBQuery.append(" join sgvc_fin_year_mst ofy on Date(Thi.Total_Year || '-' || Thi.Total_Month || '-' || '01') between Ofy.From_date and Ofy.To_date");
		lSBQuery.append(" join sgva_month_mst ofm on thi.total_month = ofm.month_no and ofm.lang_id = 'en_US'");
		lSBQuery.append(" left outer join trn_dcps_missing_credits_dtls MC");
		lSBQuery.append(" on  MC.MONTH = ofm.month_id and MC.YEAR = ofy.fin_year_id and MC.DCPS_EMP_ID = " + lLongEmpId);
		lSBQuery.append(" order by ofy.fin_year_id,ofm.month_id");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listMissingCredits = lQuery.list();

		return listMissingCredits;

	}

	public List getAllTerminalRequests(String lStrDDOCode, String gStrLocationCode, String lStrUser, String lStrUse) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT EM.name,TD.terminalId ");
		lSBQuery.append(" FROM TrnDcpsTerminalDtls TD,MstEmp EM ");
		lSBQuery.append(" where TD.dcpsEmpId = EM.dcpsEmpId ");

		if (lStrUser.equals("DDO")) {
			lSBQuery.append(" and TD.ddoCode = :ddoCode");
		}
		if (lStrUser.equals("DDO") && lStrUse.equals("FromDraft")) {
			lSBQuery.append(" AND TD.statusFlag = 0");
		}
		if (lStrUser.equals("TO") && lStrUse.equals("FromDDO")) {
			lSBQuery.append(" AND TD.statusFlag = 1 AND TD.treasuryCode = :treasuryCode");
		}
		if (lStrUser.equals("DDO") && lStrUse.equals("FromTO")) {
			lSBQuery.append(" AND TD.statusFlag = 2");
		}
		if (lStrUser.equals("SRKA") && lStrUse.equals("FromDDO")) {
			lSBQuery.append(" AND TD.statusFlag = 3");
		}

		lSBQuery.append(" order by EM.name,TD.terminalId ASC");

		Query query = ghibSession.createQuery(lSBQuery.toString());

		if (lStrUser.equals("DDO")) {
			query.setParameter("ddoCode", lStrDDOCode.trim());
		}

		if (lStrUser.equals("TO") && lStrUse.equals("FromDDO")) {
			query.setParameter("treasuryCode", Long.valueOf(gStrLocationCode));
		}

		List resultList = query.list();
		return resultList;
	}

	public List getAllMissingCreditsSavedForTerminalId(Long lLongTerminalId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		// Sequence of parameters got here should be same as that of the method
		// getAllMissingCreditsForEmp

		lSBQuery
		.append(" SELECT FY.finYearDesc,FM.monthName,MD.year,MD.month,MD.missingCreditId,MD.amountDeduction,MD.voucherNo,MD.voucherDate,MD.Remarks FROM TrnDcpsMissingCreditsDtls MD,SgvaMonthMst FM,SgvcFinYearMst FY ");
		lSBQuery.append(" Where FM.monthId = MD.month and FM.langId =  'en_US' ");
		lSBQuery.append(" and FY.finYearId = MD.year and FY.langId =  'en_US' ");
		lSBQuery.append(" and MD.rltTerminalId = :rltTerminalId ");
		lSBQuery.append(" order by MD.year,MD.month");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("rltTerminalId", lLongTerminalId);

		List resultList = query.list();
		return resultList;
	}

	public Boolean checkTerminalRequestRaisedOrNot(Long dcpsEmpId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		try {

			lSBQuery.append(" select terminalId FROM TrnDcpsTerminalDtls WHERE dcpsEmpId = :dcpsEmpId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("dcpsEmpId", dcpsEmpId);

			tempList = lQuery.list();

			if (tempList.size() == 0) {
				flag = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		return flag;
	}

	public void deleteMissingCreditsSavedForTerminalId(Long lLongTerminalId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		try {

			lSBQuery.append(" delete from TrnDcpsMissingCreditsDtls where rltTerminalId = :terminalId");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("terminalId", lLongTerminalId);
			lQuery.executeUpdate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}

	}

	public Double getOpeningBalanceForDcpsId(String lStrDcpsId, Long lLngFinYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Double lDblClosingBal = 0d;
		lSBQuery.append(" SELECT MD.closeNet ");
		lSBQuery.append(" FROM MstDcpsContributionYearly MD ");
		lSBQuery.append(" Where MD.yearId = :yearId and MD.dcpsId =  :dcpsId ");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("yearId", lLngFinYearId - 1);
		query.setParameter("dcpsId", lStrDcpsId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0) {
			lDblClosingBal = (Double) resultList.get(0);
		}
		return lDblClosingBal;
	}

	public Double getPaidEmployerContributionForYear(String lStrDcpsId, Long lLngYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Double lDblTotalContribution = 0d;

		lSBQuery.append(" SELECT SUM(TDC.contribution) ");
		lSBQuery.append(" FROM TrnDcpsContribution TDC,MstEmp ME ");
		lSBQuery.append(" Where TDC.finYearId = :yearId and TDC.dcpsEmpId = ME.dcpsEmpId AND ME.dcpsId= :dcpsId and TDC.employerContriFlag='Y'");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("yearId", lLngYearId);
		query.setParameter("dcpsId", lStrDcpsId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0 && resultList.get(0) != null) {
			lDblTotalContribution = (Double) resultList.get(0);
		}
		return lDblTotalContribution;
	}

	public Double getPendingEmployerContributionForYear(String lStrDcpsId, Long lLngYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Double lDblTotalContribution = 0d;

		lSBQuery.append(" SELECT SUM(TDC.contribution) ");
		lSBQuery.append(" FROM TrnDcpsContribution TDC,MstEmp ME ");
		lSBQuery.append(" Where TDC.finYearId = :yearId and TDC.dcpsEmpId = ME.dcpsEmpId AND ME.dcpsId= :dcpsId and (TDC.employerContriFlag is null OR TDC.employerContriFlag!='Y')");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("yearId", lLngYearId);
		query.setParameter("dcpsId", lStrDcpsId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0 && resultList.get(0) != null) {
			lDblTotalContribution = (Double) resultList.get(0);
		}
		return lDblTotalContribution;
	}

	public Double getTotalMissingCreditsForEmp(String lStrDcpsId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Double lDblTotalContribution = 0d;

		lSBQuery.append(" SELECT SUM(TDMC.amountDeduction) ");
		lSBQuery.append(" FROM TrnDcpsMissingCreditsDtls TDMC,MstEmp ME ");
		lSBQuery.append(" Where TDMC.dcpsEmpId = ME.dcpsEmpId AND ME.dcpsId= :dcpsId ");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("dcpsId", lStrDcpsId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0 && resultList.get(0) != null) {
			lDblTotalContribution = (Double) resultList.get(0);
		}
		return lDblTotalContribution;
	}

	public Double getTier2ContributionForYear(String lStrDcpsId, Long lLngFinYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Double lDblTier2Contribution = 0d;

		lSBQuery.append(" SELECT RDY.yearlyAmount ");
		lSBQuery.append(" FROM RltDcpsSixPCYearly RDY,MstEmp ME ");
		lSBQuery.append(" Where RDY.finYearId=:yearId AND RDY.statusFlag='A' AND RDY.dcpsEmpId = ME.dcpsEmpId AND ME.dcpsId= :dcpsId ");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("dcpsId", lStrDcpsId);
		query.setParameter("yearId", lLngFinYearId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0) {
			lDblTier2Contribution = (Double) resultList.get(0);
		}
		return lDblTier2Contribution;
	}

	public Date getEndDateForFinYear(Long lLngFinYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Date lDtEndDate = null;

		lSBQuery.append(" SELECT FYM.toDate ");
		lSBQuery.append(" FROM SgvcFinYearMst FYM ");
		lSBQuery.append(" Where FYM.finYearId=:yearId ");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("yearId", lLngFinYearId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0) {
			lDtEndDate = (Date) resultList.get(0);
		}
		return lDtEndDate;
	}

	public List getContributionTillDate(String lStrDcpsId, Long lLngYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT TDC.contribution,CV.voucherDate ");
		lSBQuery.append(" FROM TrnDcpsContribution TDC,MstDcpsContriVoucherDtls CV,MstEmp ME ");
		lSBQuery.append(" Where TDC.finYearId = :yearId and TDC.dcpsEmpId = ME.dcpsEmpId AND ME.dcpsId= :dcpsId  AND TDC.rltContriVoucherId=CV.dcpsContriVoucherDtlsId");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("yearId", lLngYearId);
		query.setParameter("dcpsId", lStrDcpsId);

		List resultList = query.list();

		return resultList;
	}

	public Date getStartDateForFinYear(Long lLngFinYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Date lDtStartDate = null;

		lSBQuery.append(" SELECT FYM.fromDate ");
		lSBQuery.append(" FROM SgvcFinYearMst FYM ");
		lSBQuery.append(" Where FYM.finYearId=:yearId ");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("yearId", lLngFinYearId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0) {
			lDtStartDate = (Date) resultList.get(0);
		}
		return lDtStartDate;
	}

	public List getMissingCreditsForDcpsId(String lStrDcpsId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT MD.amountDeduction,MD.voucherDate FROM TrnDcpsMissingCreditsDtls MD,MstEmp ME ");
		lSBQuery.append(" Where MD.dcpsEmpId = ME.dcpsEmpId AND ME.dcpsId= :dcpsId ");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("dcpsId", lStrDcpsId);

		List resultList = query.list();
		return resultList;
	}

	public Long getDcpsEmpIdForDcpsId(String lStrDcpsId) throws Exception {

		Long lLngDcpsEmpId = 0l;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT ME.dcpsEmpId FROM MstEmp ME ");
		lSBQuery.append(" Where ME.dcpsId=:dcpsId ");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("dcpsId", lStrDcpsId);

		List resultList = query.list();
		if (resultList != null && resultList.size() > 0) {
			lLngDcpsEmpId = (Long) resultList.get(0);
		}

		return lLngDcpsEmpId;
	}

	public List getAllTreasuries() {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer("select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100006,100003) and CM.LOC_ID not in(9991,1111)  order by CM.loc_id  ");

			lCon = DBConnection.getConnection();
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id + "-" + treasury_name);
				arrTreasury.add(vo);
			}

		} catch (Exception e) {
			gLogger.info("Sql Exception:" + e, e);
		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}

	public List getAllDDO(String treasuryId) {

		List resultList = null;
		String ddo_code = null;
		String ddo_name = null;
		ComboValuesVO vo = new ComboValuesVO();
		try {

			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer("SELECT  DM.ddo_code,'(' ||DM.ddo_code ||')'||replace(DM.ddo_name,'&','and')   FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM "
					+ "WHERE RO.location_Code = '" + treasuryId + "' AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code order by DM.ddo_code");

			Query query = ghibSession.createSQLQuery(lsb.toString());
			resultList = query.list();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return resultList;
	}

	public List getAllEmpsUnderDDO(String lStrddoCode) {

		List<Object> lLstReturnList = null;
		try {

			StringBuilder sb = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			/*sb.append(" SELECT emp.EMP_NAME||'----'||emp.DCPS_ID,emp.DCPS_ID FROM mst_dcps_emp emp inner join MST_DCPS_EMP_DETAILS det on det.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
			sb.append(" left outer join TRN_DCPS_TERMINAL_DTLS ter on ter.DCPS_EMP_ID=emp.DCPS_EMP_ID and ter.STATUS_FLAG=0 ");
			sb.append(" where emp.DDO_CODE is null and emp.REG_STATUS=1 and emp.DCPS_OR_GPF='Y' and det.DDO_CODE=:ddoCode order by emp.EMP_NAME ");
			*/sb.append(" SELECT tmp.data,tmp.id FROM "); 
			sb.append(" (SELECT emp.EMP_NAME||'----'||emp.DCPS_ID as data,emp.DCPS_ID as Id,ter.STATUS_FLAG,ter.TERMINAL_ID  FROM mst_dcps_emp emp inner join MST_DCPS_EMP_DETAILS det on det.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
			sb.append(" left outer join TRN_DCPS_TERMINAL_DTLS ter on ter.DCPS_EMP_ID=emp.DCPS_EMP_ID  ");
			sb.append(" where emp.DDO_CODE is null and emp.REG_STATUS=1 and emp.DCPS_OR_GPF='Y' and det.DDO_CODE=:ddoCode order by emp.EMP_NAME)tmp ");
			sb.append("  where tmp.STATUS_FLAG =0 or tmp.STATUS_FLAG is null ");
			
			Query selectQuery = lObjSession.createSQLQuery(sb.toString());
			selectQuery.setParameter("ddoCode", lStrddoCode);
			lLstReturnList = selectQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList;
	}

	public List getTerminationDetailsOfEmp(String ddoCode, String lStrDcpsId) {

		List resultList = null;
		StringBuilder lSBQuery = new StringBuilder();

		gLogger.info("ddoCode is ***" + ddoCode);
		gLogger.info("dcpsID is ***" + lStrDcpsId);
		lSBQuery
		.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID,ddo.DDO_NAME,des.DSGN_NAME,emp.DOJ FROM mst_dcps_emp emp inner join MST_DCPS_EMP_DETAILS det on det.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
		lSBQuery.append(" inner join ORG_DDO_MST ddo on ddo.DDO_CODE=det.DDO_CODE inner join ORG_DESIGNATION_MST des on des.DSGN_ID=emp.DESIGNATION ");
		lSBQuery.append(" where emp.DDO_CODE is null and emp.REG_STATUS=1 and emp.DCPS_OR_GPF='Y' ");
		lSBQuery.append(" and det.DDO_CODE=:ddoCode and emp.DCPS_ID=:dcpsId and emp.dcps_Id is not null ");
		Query query = ghibSession.createSQLQuery(lSBQuery.toString());
		query.setParameter("dcpsId", lStrDcpsId);
		query.setParameter("ddoCode", ddoCode);
		resultList = query.list();
		return resultList;
	}

	public String getSuperAnnDateexist(String sevaarthId) {

		List resultList = null;
		String serEndDate = null;
		StringBuilder lSBQuery = new StringBuilder();

		gLogger.info("sevaarth Id is ***" + sevaarthId);

		lSBQuery.append(" SELECT to_char(EMP_SERVEND_DT,'dd/MM/yyyy') FROM  MST_DCPS_EMP where EMP_SERVEND_DT < sysdate  and SEVARTH_ID=:sevaarthId ");

		Query query = ghibSession.createSQLQuery(lSBQuery.toString());
		query.setParameter("sevaarthId", sevaarthId);

		Object obj[] = null;
		resultList = query.list();
		if (resultList.size() >0) {

			serEndDate =   resultList.get(0).toString() ;
		}

		else {
			/*	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();

			serEndDate =sdf.format(date);*/
			serEndDate="blank";

		}
		gLogger.info("serEndDate in DAO*************" + serEndDate);
		return serEndDate;
	}


	public Double getOpeningBalanceTier1(String lStrDcpsId,long lLngYearId) throws Exception {

		Double OpenEmployee = null;
		StringBuilder lSBQuery = new StringBuilder();
		try{
			lSBQuery.append("SELECT OPEN_EMPLOYEE ");
			lSBQuery.append("FROM MST_DCPS_CONTRIBUTION_YEARLY ");
			lSBQuery.append("WHERE dcps_id =:lStrDcpsId ");
			lSBQuery.append("AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lStrDcpsId", lStrDcpsId);
			lQuery.setLong("year", lLngYearId);
			List resultList = lQuery.list();

			if (resultList != null && resultList.size() > 0) {
				OpenEmployee = (Double) resultList.get(0);
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return OpenEmployee;
	}
	public Double getEmployeeContributionNonMissingTier1(String lStrDcpsId,long lLngYearId) throws Exception {

		Double empContribution =null;
		StringBuilder lSBQuery = new StringBuilder();
		try{
			lSBQuery.append("SELECT nvl(sum(CONTRIBUTION),0) FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and fin_year_id=:lLngYearId and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:lStrDcpsId and is_missing_credit is null ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lStrDcpsId", lStrDcpsId);
			lQuery.setLong("lLngYearId", lLngYearId);
			List resultList = lQuery.list();

			if (resultList != null && resultList.size() > 0) {
				empContribution = (Double) resultList.get(0);
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return empContribution;
	}
	public Double getEmployeeContributionWithMissingTier1(String lStrDcpsId,String fromDate,String toDate) throws Exception {

		Double empContributionMis = null;
		StringBuilder lSBQuery = new StringBuilder();
		try{
			lSBQuery.append(" SELECT nvl(sum(CONTRIBUTION),0) FROM  TRN_DCPS_CONTRIBUTION where REG_STATUS=1 and MISSING_CREDIT_APPROVAL_DATE between '"+fromDate+"' and '"+toDate+"'  and EMPLOYER_CONTRI_FLAG='Y'  and DCPS_EMP_ID=:lStrDcpsId  and is_missing_credit='Y' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lStrDcpsId", lStrDcpsId);

			List resultList = lQuery.list();

			if (resultList != null && resultList.size() > 0) {
				empContributionMis = (Double) resultList.get(0);
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return empContributionMis;
	}

	public Double getInterestTier1(String lStrDcpsId,long lLngYearId) throws Exception {

		Double interestTier1 = null;
		StringBuilder lSBQuery = new StringBuilder();
		try{
			lSBQuery.append(" SELECT NVL((INT_CONTRB_EMPLOYEE + INT_CONTRB_EMPLOYER),0) ");
			lSBQuery.append(" FROM MST_DCPS_CONTRIBUTION_YEARLY where dcps_id = :lStrDcpsId ");			
			lSBQuery.append(" AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lStrDcpsId", lStrDcpsId);
			lQuery.setLong("year", lLngYearId);
			List resultList = lQuery.list();

			if (resultList != null && resultList.size() > 0) {
				interestTier1 = (Double) resultList.get(0);
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return interestTier1;
	}

	public List getContributionAndIntTier2(String lStrDcpsId,long lLngYearId) throws Exception {

		List lLstData = null;
		StringBuilder lSBQuery = new StringBuilder();
		try{
			lSBQuery.append("select OPEN_TIER2,CONTRIB_TIER2,OPEN_TIER2+CONTRIB_TIER2,INT_CONTRB_TIER2 ,cast(INT_CONTRB_EMPLOYEE as decimal(16,2)) as INT_CONTRB_EMP,cast(INT_CONTRB_EMPLOYER as decimal(16,2)) as INT_CONTRB_EMPlr,OPEN_EMPLOYEE,OPEN_EMPLOYER,OPEN_INT FROM MST_DCPS_CONTRIBUTION_YEARLY ");
			lSBQuery.append("WHERE dcps_id = :lStrDcpsId ");			
			lSBQuery.append("AND YEAR_ID = :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lStrDcpsId", lStrDcpsId);
			lQuery.setLong("year", lLngYearId);
			lLstData = lQuery.list();


		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}

		return lLstData;
	}
	public long getfinYearIdFromYr(int year) throws Exception {

		long yearId =0;
		StringBuilder lSBQuery = new StringBuilder();
		try{
			lSBQuery.append("SELECT FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST where FIN_YEAR_CODE= :year ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("year", year);
			List resultList = lQuery.list();

			if (resultList != null && resultList.size() > 0) {
				yearId = Long.parseLong(resultList.get(0).toString());
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}
		gLogger.info("yearId in DAO  " +yearId);

		return yearId;
	}
	public boolean checkPrvInterestCal(String dcpsId,Long yearId) throws Exception {

		boolean check=false;

		StringBuilder lSBQuery = new StringBuilder();
		try{
			lSBQuery.append(" SELECT count(1) FROM  TRN_DCPS_CONTRIBUTION where FIN_YEAR_ID<=:yearId  and DCPS_EMP_ID in (SELECT dcps_emp_id FROM mst_dcps_emp where DCPS_ID=:dcpsId) and status<> 'H' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("yearId", yearId);
			lQuery.setParameter("dcpsId", dcpsId);
			List resultList = lQuery.list();

			if (resultList != null && resultList.size() > 0) {
				if(Integer.parseInt(resultList.get(0).toString())!=0)
					check = true;
			}
		}catch (Exception e) {			
			gLogger.error("Error is : " + e, e);
		}
		return check;
	}



	public List getFromToDate(Long lLngYearId)
	{
		List lLstData=null;


		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT from_date,TO_DATE  FROM  SGVC_FIN_YEAR_MST  where FIN_year_id=:lLngYearId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngYearId", lLngYearId);
			lLstData = lQuery.list();


		}catch(Exception e){
			gLogger.error("Exception in getDdoName:" + e, e);
		}
		return lLstData;
	}


	public String getReasonOfTermination(String terminationReasonId){

		gLogger.info("termination Id is :::::::"+terminationReasonId);
		String terminationReason=null;
		List lGenId=null;

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT LOOKUP_NAME FROM CMN_LOOKUP_MST where LOOKUP_ID=:terminationReasonId ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("terminationReasonId", terminationReasonId);

		lGenId = lQuery.list();
		if(lGenId.size()>0 && lGenId.get(0)!=null){
			terminationReason=lGenId.get(0).toString();
		}
		gLogger.info("terminationReason is :::::::"+terminationReason);
		return terminationReason;


	}
	
	

	//Check already termination for status 0
	

	public List getTerminationDetailsForPartialTerminate(String dcpsId){

		gLogger.info("dcpsId  is :::::::"+dcpsId);
		
		List terDtls=null;

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT ter.order_no,to_char(ter.order_date,'dd/MM/yyyy'),to_char(ter.date_of_termination,'dd/MM/yyyy'),ter.REASON_OF_TERMINATION,ter.AUTHORITY_LETTER_NO FROM TRN_DCPS_TERMINAL_DTLS ter inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=ter.REASON_OF_TERMINATION ");
	    lSBQuery.append(" where DCPS_EMP_ID in (SELECT DCPS_EMP_ID FROM mst_dcps_emp where DCPS_ID=:dcpsId) and ter.STATUS_FLAG=0 ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);

		terDtls = lQuery.list();
		
		gLogger.info("terDtls.size() is :::::::"+terDtls.size());
		return terDtls;


	}
//Check Employee in termination table
	public List checkEmployeeInPartialTermination(String dcpsId){

		gLogger.info("dcpsId  is :::::::"+dcpsId);
		
		List terDtls=null;

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT * FROM  TRN_DCPS_TERMINAL_DTLS where DCPS_EMP_ID in (SELECT DCPS_EMP_ID FROM mst_dcps_emp where DCPS_ID=:dcpsId) and STATUS_FLAG=0 ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);

		terDtls = lQuery.list();
		
		gLogger.info("terDtls.size() is :::::::"+terDtls.size());
		return terDtls;


	}
	public List getFromToDt(Long yrId)
	{
		List lLstDate = null;
		String lstrempName = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT to_char(FROM_DATE,'dd/MM/yyyy'),to_char(TO_DATE,'dd/MM/yyyy') from  SGVC_FIN_YEAR_MST where FIN_YEAR_ID= :yrId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("yrId", yrId);
			lLstDate = lQuery.list();

			
		}catch(Exception e){
			logger.error("Exception in getFromToDt:" + e, e);
		}
		return lLstDate;
	}


	public String getTerminationDt(String dcpsEmpId)
	{
		List lLstDate = null;
		String termDt = "";

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DATE_OF_TERMINATION FROM TRN_DCPS_TERMINAL_DTLS where DCPS_EMP_ID=:dcpsEmpId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("dcpsEmpId", dcpsEmpId);
			lLstDate = lQuery.list();

			if(lLstDate.size()>0 && lLstDate != null){
				termDt=lLstDate.get(0).toString();;
			}
			
		}catch(Exception e){
			logger.error("Exception in getTerminationDt:" + e, e);
		}
		return termDt;
	}
//Interest Calculation Queries
	public List getContriDtlsForGivenEmployeeListFinal(String lStrFromDate, String lStrToDate,Long previousFinYearId,List listAllEmpsDCPSEmpIdsForIntrstCalc)  throws Exception {

		List listContriDtlsForGivenEmp = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT VC.voucher_date,TR.CONTRIBUTION,VC.month_id,VC.year_id,TR.EMPLOYER_CONTRI_FLAG,TR.DCPS_CONTRIBUTION_ID,VC.voucher_no,FY.FIN_YEAR_CODE,TR.TYPE_OF_PAYMENT,(case  when :previousFinYearId=24 then 8.6 when :previousFinYearId=25 then 8.8  when :previousFinYearId=26 then 8.7 else 8 end) as Ints ,  ");

		lSBQuery.append(" EM.dcps_emp_id,EM.dcps_id,TR.ddo_code,nvl(INP.MST_DCPS_CONTRIBUTION_YEARLY_ID,0),nvl(INP.CLOSE_EMPLOYEE,0),nvl(CLOSE_EMPLOYER,0),nvl(OPEN_INT,0),nvl(CLOSE_TIER2,0)");

		lSBQuery.append(" ,TR.TREASURY_CODE,EM.DDO_CODE ");

		// Below line added to consider weighted average in case the interest rate changes within the year

		//lSBQuery.append(" ,MAX(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE),nvl(IR.APPLICABLE_TO,FY.TO_DATE)");

		lSBQuery.append(" ,MAX(nvl(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE),VC.VOUCHER_DATE),FY.TO_DATE ");
		lSBQuery.append(" FROM trn_dcps_contribution TR");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.MST_DCPS_CONTRI_VOUCHER_DTLS = TR.RLT_CONTRI_VOUCHER_ID");
		//lSBQuery.append(" JOIN MST_DCPS_BILL_GROUP BG on BG.BILL_GROUP_ID = VC.BILL_GROUP_ID");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");


		// Added by Ashish for issue related to pre and future fin_year_id 

		if(lStrFromDate.equals("2012-04-01")  && lStrToDate.equals("2013-03-31")){
			lSBQuery.append(" JOIN sgvc_fin_year_mst FY on FY.FIN_YEAR_ID = 25 ");
			gLogger.info("In if From date"+lStrFromDate);
			gLogger.info("In if To date"+lStrToDate);
		}
		else if(lStrFromDate.equals("2013-04-01")  && lStrToDate.equals("2014-03-31"))
		{
			gLogger.info("In else From date"+lStrFromDate);
			gLogger.info("In else To date"+lStrToDate);
			lSBQuery.append(" JOIN sgvc_fin_year_mst FY on FY.FIN_YEAR_ID = 26 ");
		}
		// Ended by Ashish for issue related to pre and future fin_year_id 
		lSBQuery.append(" JOIN MST_DCPS_EMP EM on EM.dcps_emp_id = TR.dcps_emp_id ");

		//lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on VC.voucher_date BETWEEN IR.EFFECTIVE_FROM AND IR.APPLICABLE_TO ");

		//("SELECT interest FROM mst_dcps_interest_rate WHERE '"	+ lStrYear + "' >= effective_from AND ('" + lStrYear + "' < applicable_to OR applicable_to IS NULL )");

		// Below line commented as Weighted Interest has to be calculated
		//lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on VC.voucher_date >= IR.EFFECTIVE_FROM AND ( VC.voucher_date < IR.APPLICABLE_TO OR IR.APPLICABLE_TO IS NULL ) ");

		//lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on IR.FIN_YEAR_ID = VC.YEAR_ID");

		lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on IR.FIN_YEAR_ID = VC.YEAR_ID");

		lSBQuery.append(" LEFT JOIN MST_DCPS_CONTRIBUTION_YEARLY INP on INP.dcps_id = EM.dcps_id and INP.YEAR_ID = " + previousFinYearId);


		lSBQuery.append(" WHERE ");

		// lSBQuery.append(" VC.treasury_code = " + treasuryCode");
		lSBQuery.append(" EM.dcps_emp_id in (:lListDcpsEmpIds)");
		lSBQuery.append(" AND VC.voucher_date BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "'");
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date=tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		//lSBQuery.append(" AND tn.TREASURY_CODE = '" + treasuryCode +"'");
		//lSBQuery.append(" AND TR.dcps_emp_id = " + dcpsEmpId);
		lSBQuery.append(" AND TR.reg_status = 1 ");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");

		lSBQuery.append(" AND TR.IS_MISSING_CREDIT IS NULL AND TR.IS_CHALLAN IS NULL");
		//	lSBQuery.append(" AND VC.voucher_date BETWEEN  IR.EFFECTIVE_FROM AND NVL(IR.APPLICABLE_TO ,sysdate) ");
		//lSBQuery.append(" AND MAX(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE) < nvl(IR.APPLICABLE_TO,FY.TO_DATE)");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsEmpIds", listAllEmpsDCPSEmpIdsForIntrstCalc);
		lQuery.setLong("previousFinYearId", previousFinYearId+1);
		listContriDtlsForGivenEmp = lQuery.list();

		return listContriDtlsForGivenEmp;
	}

	public List getContriDtlsForGivenEmployeeListFinalForMissingCredits(String lStrFromDate, String lStrToDate,Long finYearId,List listAllEmpsEmpIdsForIntrstCalcForMissingCredits) throws Exception {

		List listContriDtlsForGivenEmp = null;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		lSBQuery.append(" SELECT MAX(IR.EFFECTIVE_FROM,VC.VOUCHER_DATE),nvl(IR.APPLICABLE_TO,FY.TO_DATE),FY.FROM_DATE,FY.TO_DATE,FY.fin_year_id,VC.voucher_date,");
		lSBQuery.append(" TR.CONTRIBUTION,VC.month_id,VC.year_id,TR.EMPLOYER_CONTRI_FLAG,TR.DCPS_CONTRIBUTION_ID,VC.voucher_no,FY.FIN_YEAR_CODE,");
		lSBQuery.append(" TR.TYPE_OF_PAYMENT,IR.INTEREST,EM.dcps_emp_id,EM.dcps_id,TR.ddo_code,TR.TREASURY_CODE,EM.DDO_CODE, ");
		lSBQuery.append(" TR.IS_MISSING_CREDIT, TR.IS_CHALLAN ");
		lSBQuery.append(" FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.MST_DCPS_CONTRI_VOUCHER_DTLS = TR.RLT_CONTRI_VOUCHER_ID ");
		lSBQuery.append(" JOIN sgvc_fin_year_mst FY on FY.FIN_YEAR_ID >= VC.year_id and FY.FIN_YEAR_ID <= :finYearId");
		lSBQuery.append(" JOIN MST_DCPS_EMP EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		lSBQuery.append(" LEFT JOIN MST_DCPS_INTEREST_RATE IR on IR.FIN_YEAR_ID = FY.FIN_YEAR_ID");
		lSBQuery.append(" WHERE ");
		lSBQuery.append(" EM.dcps_emp_id in (:lListDcpsEmpIds)");
		lSBQuery.append(" AND VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "'");
		lSBQuery.append(" AND TR.reg_status = 1 ");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') ");
		lSBQuery.append(" ORDER BY TR.DCPS_CONTRIBUTION_ID,FY.FIN_YEAR_ID");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameterList("lListDcpsEmpIds", listAllEmpsEmpIdsForIntrstCalcForMissingCredits);
		lQuery.setParameter("finYearId", finYearId);
		listContriDtlsForGivenEmp = lQuery.list();

		return listContriDtlsForGivenEmp;

	}


	public List getDCPSIdsForDcpsEmpIds(List lListDcpsEmpIds) {

		StringBuilder lSBQuery = new StringBuilder();
		List lListDcpsIds = null;
		logger.info("List size "+lListDcpsEmpIds.size());
		lSBQuery.append(" SELECT mst.dcpsId from MstEmp  mst where mst.dcpsEmpId in (:dcpsEmpIdList) ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameterList("dcpsEmpIdList", lListDcpsEmpIds);

		lListDcpsIds = lQuery.list();
		return lListDcpsIds;
	}

	public void deleteYearlyIntrstsForGivenEmpList(String dcpsEmpId,Long lLongYearId) {

		Session session = getSession();
		StringBuilder lSBQuery = new StringBuilder();
         gLogger.info("dcpsEmpId for yearly deletion is "+dcpsEmpId);
         gLogger.info("lLongYearId for yearly deletion is "+lLongYearId);
		lSBQuery.append(" delete from MST_DCPS_CONTRIBUTION_YEARLY ");
		lSBQuery.append(" where year_id = :yearId");
		lSBQuery.append(" and dcps_id = :dcpsEmpId ");

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("yearId", lLongYearId);

		int status = lQuery.executeUpdate();
		gLogger.info("status:::"+status);
	}

	public void deleteMonthlyIntrstsForGivenEmpList(String dcpsId,Long lLongYearId) {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MstDcpsContributionMonthly ");
		lSBQuery.append(" where yearId = :yearId");
		lSBQuery.append(" and dcpsId = :dcpsId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);
		lQuery.setParameter("yearId", lLongYearId);

		lQuery.executeUpdate();
	}

	public Double getInterestRateForGivenYear(String lStrYear) {

		Double interestRate = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT interest FROM mst_dcps_interest_rate WHERE '"
				+ lStrYear + "' >= effective_from AND ('" + lStrYear
				+ "' < applicable_to OR applicable_to IS NULL )");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		interestRate = Double.parseDouble(lQuery.uniqueResult().toString());

		return interestRate;
	}

	public MstDcpsContributionYearly getContriYearlyVOForYear(
			Long dcpsEmpId, Long previousYearId) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;

		Query lQuery = null;

		lSBQuery
		.append(" SELECT YC FROM MstDcpsContributionYearly YC,MstEmp EM WHERE YC.dcpsId = EM.dcpsId AND EM.dcpsEmpId = :dcpsEmpId AND YC.yearId = :yearId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("yearId", previousYearId);

		lObjMstDcpsContributionYearly = (MstDcpsContributionYearly) lQuery
		.uniqueResult();

		return lObjMstDcpsContributionYearly;
	}

	public String getDcpsIdForEmpId(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String dcpsId = null;

		lSBQuery
		.append(" select dcpsId FROM MstEmp WHERE dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		tempList = lQuery.list();
		if(tempList != null)
		{
			if(tempList.size() != 0)
			{
				dcpsId = tempList.get(0);
			}
		}

		return dcpsId;

	}

	public Long getYearIdForYearCode(String yearCode) {

		Long lLongYearId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			//ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
			.append(" SELECT finYearId FROM SgvcFinYearMst WHERE finYearCode = :yearCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("yearCode", yearCode);
			lLongYearId = Long.valueOf(hqlQuery.list().get(0).toString());

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Error  is " + e);

		}
		return lLongYearId;
	}

	/*	
	public List getAllDCPSEmployeesForIntrstCalcSixPC(Long treasuryCode,String ddoCode) {

		List listAllEmpsForIntrstCalc = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT EM.dcpsEmpId FROM MstEmp EM,OrgDdoMst OD,RltDdoOrg RD,MstSixPCArrears PC");
		lSBQuery.append(" WHERE EM.dcpsEmpId = PC.dcpsEmpId ");
		lSBQuery.append(" AND RD.ddoCode = OD.ddoCode ");
		lSBQuery.append(" AND RD.locationCode = :treasuryCode ");

		if(!ddoCode.equals(""))
		{
			lSBQuery.append(" AND EM.ddoCode = :ddoCode");
		}

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("treasuryCode", treasuryCode.toString());

		if(!ddoCode.equals(""))
		{
			lQuery.setParameter("ddoCode", ddoCode);
		}

		listAllEmpsForIntrstCalc = lQuery.list();

		return listAllEmpsForIntrstCalc;
	}


	public MstDcpsSixPCInterestYearly getSixPCYearlyInterestVOForYear(
			Long dcpsEmpId, Long previousYearId) {

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsSixPCInterestYearly lObjMstDcpsSixPCInterestYearly = null;

		Query lQuery = null;

		lSBQuery.append(" SELECT YC FROM MstDcpsSixPCInterestYearly YC,MstEmp EM WHERE YC.dcpsId = EM.dcpsId AND EM.dcpsEmpId = :dcpsEmpId AND YC.yearId = :yearId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("yearId", previousYearId);

		lObjMstDcpsSixPCInterestYearly = (MstDcpsSixPCInterestYearly) lQuery
				.uniqueResult();

		return lObjMstDcpsSixPCInterestYearly;
	}

	 */

	/*
	public List getArrearsDtlsForGivenEmployee(Long dcpsEmpId,Long yearId) {

		List listArrearsDtlsForGivenEmp = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" select RL.yearlyAmount,RL.dcpsEmpId,RL.voucherNo,RL.voucherDate,FY.finYearCode,EM.payCommission from RltDcpsSixPCYearly RL,MstEmp EM,SgvcFinYearMst FY");
		lSBQuery.append(" where RL.dcpsEmpId = :dcpsEmpId");
		lSBQuery.append(" and RL.finYearId = :yearId ");
		lSBQuery.append(" and RL.dcpsEmpId = EM.dcpsEmpId ");
		lSBQuery.append(" and RL.finYearId = FY.finYearId ");
		lSBQuery.append(" and RL.statusFlag = 'A'");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		listArrearsDtlsForGivenEmp = lQuery.list();

		return listArrearsDtlsForGivenEmp;
	}
	 */

	/*public List getArrearsDtlsForAllEmps(List listAllEmpsDCPSEmpIdsForIntrstCalc,Long yearId) {

		List listArrearsDtlsForGivenEmp = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" select RL.yearlyAmount,RL.dcpsEmpId,RL.voucherNo,RL.voucherDate,FY.finYearCode,EM.payCommission from RltDcpsSixPCYearly RL,MstEmp EM,SgvcFinYearMst FY");
		lSBQuery.append(" where ");
		//lSBQuery.append(" RL.dcpsEmpId = :dcpsEmpId");
		lSBQuery.append(" RL.dcpsEmpId in (:dcpsEmpIdList)");
		lSBQuery.append(" and RL.finYearId = :yearId ");
		lSBQuery.append(" and RL.dcpsEmpId = EM.dcpsEmpId ");
		lSBQuery.append(" and RL.finYearId = FY.finYearId ");
		lSBQuery.append(" and RL.statusFlag = 'A'");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameterList("dcpsEmpIdList", listAllEmpsDCPSEmpIdsForIntrstCalc);

		listArrearsDtlsForGivenEmp = lQuery.list();

		return listArrearsDtlsForGivenEmp;
	}*/

	//Added by ashish to display only  employee eligible for intertest cal	

	public List getAllEmpsUnderDDO(String treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception  {

		/*List empList = null;

		try {
			//ghibSession = getSession();

		String query = "select EM.dcpsEmpId,EM.dcpsId,EM.name from MstEmp EM where ddoCode= :DDOCode and EM.dcpsId is not null  AND EM.acDcpsMaintainedBy not in (700240,700241,700242) order by EM.name,EM.dcpsEmpId,EM.dcpsId ";



			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			empList = stQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			e.printStackTrace();
			throw (e);
		}
			return empList;*/


		List listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT EM.dcps_emp_id,EM.dcps_id,EM.EMP_NAME FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		lSBQuery.append(" JOIN mst_dcps_emp EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");

		lSBQuery.append(" WHERE ");
		lSBQuery.append(" VC.treasury_code= '"+ treasuryCode +"' ");

		if(!ddoCode.equals(""))
		{
			lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		}

		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		lSBQuery.append(" AND VC.voucher_date BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "'");
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date = tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		lSBQuery.append(" AND VC.voucher_status = 1");

		// Gets only those employees whose post employer Contribution is done.
		lSBQuery.append(" AND TR.REG_STATUS = 1");
		lSBQuery.append(" AND EM.REG_STATUS = 1");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS = 'G' AND VC.status = 'G' ");
		lSBQuery.append(" AND EM.dcps_id is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) <> '-1'  ");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY  not in (700240,700241,700242) ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lQuery.list();

		return listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc;



	}
	//Ended by ashish to display only  employee eligible for intertest cal		


	/*
	public void updateTrnDcpsContriIntCalculated(Long lLongDcpsContriId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" UPDATE trn_dcps_contribution SET status='H' ");
		lSBQuery.append(" WHERE DCPS_CONTRIBUTION_ID = :dcpsContriId ");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsContriId", lLongDcpsContriId);
		lQuery.executeUpdate();

	}
	 */

	public void updateTrnDcpsContriIntCalculated(List lListDcpsContriIdsPk) throws Exception  {

		try {
			StringBuilder lSBQuery = new StringBuilder();

			Query lQuery = null;
			lSBQuery.append(" UPDATE TrnDcpsContribution SET status='H' ");
			lSBQuery.append(" WHERE dcpsContributionId in (:dcpsContriId) ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("dcpsContriId", lListDcpsContriIdsPk);
			lQuery.executeUpdate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(" Error is : " + e, e);
			e.printStackTrace();
			throw (e);
		}

	}


	/*public void updateTrnDcpsContriIntCalculated(List lListDcpsContriIdsPk) throws Exception  {

		StringBuilder lSBQuery = new StringBuilder();

		//Array lArrTrnDcpsContriPk = new Long[lListDcpsContriIdsPk.size()];

		Object[] lObjArrTrnDcpsContriPk = lListDcpsContriIdsPk.toArray();

		ghibSession = getSession();

		Array lArrTrnDcpsContriPk = ghibSession.connection().createArrayOf("BIGINT", lObjArrTrnDcpsContriPk);


		Query lQuery = null;
		lSBQuery.append(" UPDATE TrnDcpsContribution SET status='H' ");
		lSBQuery.append(" WHERE dcpsContributionId in (:dcpsContriId) ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameterList("dcpsContriId", lListDcpsContriIdsPk);
		lQuery.executeUpdate();


		// Code to call procedure from Java
		CallableStatement lClbStmnt = null;
		try {
			//StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call updateTrnDcpsContriOnInterestCalculated (?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setArray(1, lArrTrnDcpsContriPk);
			lClbStmnt.setInt(2, lListDcpsContriIdsPk.size());
			lClbStmnt.execute();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

	}*/


	public void deleteMonthlyInterestForDCPSIdAndYear(String dcpsId,Long finYearId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from MstDcpsContributionMonthly where dcpsId = :dcpsId and yearId = :finYearId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);
		lQuery.setParameter("finYearId", finYearId);
		lQuery.executeUpdate();

	}

	public List getAllIntRateDtlsForGivenYear(Long lLongYear) {

		List lListIntRateDetailsForGivenYear = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT IR.INTEREST,IR.EFFECTIVE_FROM,nvl(IR.APPLICABLE_TO,FY.TO_DATE),days(nvl(IR.APPLICABLE_TO,FY.TO_DATE)) - days(IR.EFFECTIVE_FROM) + 1  FROM mst_dcps_interest_rate IR ");
		lSBQuery.append(" join SGVC_FIN_YEAR_MST FY on IR.FIN_YEAR_ID = FY.FIN_YEAR_ID");
		lSBQuery.append(" where IR.FIN_YEAR_ID = " + lLongYear );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListIntRateDetailsForGivenYear = lQuery.list();

		return lListIntRateDetailsForGivenYear;
	}

	//Added by Ashish for New Interest Calculation : start
	public String getAllDCPSEmployeesForIntrstCalc(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception {

		List listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT EM.dcps_emp_id FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		lSBQuery.append(" JOIN mst_dcps_emp EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");

		lSBQuery.append(" WHERE ");
		//VC.treasury_code in (SELECT LOC_ID FROM CMN_LOCATION_MST where ( PARENT_LOC_ID = 3501 or LOC_ID = 3501))
		lSBQuery.append(" VC.treasury_code in ( SELECT LOC_ID FROM CMN_LOCATION_MST where ( PARENT_LOC_ID =" + treasuryCode +" or LOC_ID = "+ treasuryCode+" )) ");
		//lSBQuery.append(" VC.treasury_code=" + treasuryCode);
		if(!ddoCode.equals(""))
		{
			lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		}

		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		lSBQuery.append(" AND VC.voucher_date BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "'");
		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date = tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		lSBQuery.append(" AND VC.voucher_status = 1");

		// Gets only those employees whose post employer Contribution is done.
		lSBQuery.append(" AND TR.REG_STATUS = 1");
		lSBQuery.append(" AND EM.REG_STATUS = 1");
		//Excluding missing credit records
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");
		lSBQuery.append(" AND EM.dcps_id is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) <> '-1'  ");
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT is null) OR (TR.IS_CHALLAN is null ) ");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY  not in (700240,700241,700242) ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lQuery.list();
		String dcpsEmpIds = "";
		for(int i = 0;i<listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.size();i++){
			if(i!=0){
				dcpsEmpIds = dcpsEmpIds+","+listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.get(i).toString();
			}
			else{
				dcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.get(i).toString();
			}
		}
		lQuery = null;
		listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
		gLogger.info("Emp list is ::: "+dcpsEmpIds);
		return dcpsEmpIds;
	}

	public String getAllDCPSEmployeesForIntrstCalcForMissingCredits(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception{

		List listAllEmpsEmpIdsForIntrstCalcForMissingCredits = null;

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT EM.dcps_emp_id FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		lSBQuery.append(" JOIN mst_dcps_emp EM on EM.dcps_emp_id = TR.dcps_emp_id  ");
		lSBQuery.append(" WHERE ");
		lSBQuery.append(" VC.treasury_code in ( SELECT LOC_ID FROM CMN_LOCATION_MST where ( PARENT_LOC_ID =" + treasuryCode +" or LOC_ID = "+ treasuryCode+" )) ");

		if(!ddoCode.equals(""))
		{
			lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		}

		lSBQuery.append(" AND VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "'");
		lSBQuery.append(" AND VC.voucher_status = 1");
		lSBQuery.append(" AND TR.REG_STATUS = 1");
		lSBQuery.append(" AND EM.REG_STATUS = 1");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");
		lSBQuery.append(" AND EM.dcps_id is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) is not null ");
		lSBQuery.append(" AND trim(EM.dcps_id) <> '-1'  ");
		lSBQuery.append(" AND (TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y') ");
		lSBQuery.append(" AND EM.AC_DCPS_MAINTAINED_BY not in (700240,700241,700242) ");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listAllEmpsEmpIdsForIntrstCalcForMissingCredits = lQuery.list();

		String dcpsEmpIds = "";
		for(int i = 0;i<listAllEmpsEmpIdsForIntrstCalcForMissingCredits.size();i++){
			if(i!=0){
				dcpsEmpIds = dcpsEmpIds+","+listAllEmpsEmpIdsForIntrstCalcForMissingCredits.get(i).toString();
			}
			else{
				dcpsEmpIds = listAllEmpsEmpIdsForIntrstCalcForMissingCredits.get(i).toString();
			}
		}
		lQuery = null;
		listAllEmpsEmpIdsForIntrstCalcForMissingCredits = null;
		return dcpsEmpIds;
	}

	/*	public String getDcpsEmpIdsForSixPc(String totalDcpsEmpIds, Long yearId){
		List listArrearsDtlsForGivenEmp = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" select RL.yearlyAmount,RL.dcpsEmpId,RL.voucherNo,RL.voucherDate,FY.finYearCode,EM.payCommission from RltDcpsSixPCYearly RL,MstEmp EM,SgvcFinYearMst FY");
		lSBQuery.append(" where ");
		//lSBQuery.append(" RL.dcpsEmpId = :dcpsEmpId");
		lSBQuery.append(" RL.dcpsEmpId in (:dcpsEmpIdList)");
		lSBQuery.append(" and RL.finYearId = :yearId ");
		lSBQuery.append(" and RL.dcpsEmpId = EM.dcpsEmpId ");
		lSBQuery.append(" and RL.finYearId = FY.finYearId ");
		lSBQuery.append(" and RL.statusFlag = 'A'");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("dcpsEmpIdList", totalDcpsEmpIds);

		listArrearsDtlsForGivenEmp = lQuery.list();

		String dcpsEmpIds = "";
		for(int i = 0;i<listArrearsDtlsForGivenEmp.size();i++){
			dcpsEmpIds = dcpsEmpIds+","+listArrearsDtlsForGivenEmp.get(i).toString();
		}
		lQuery = null;
		listArrearsDtlsForGivenEmp = null;
		return dcpsEmpIds;
	}
	 */
	public String checkEmployeeEligibleForIntrstCalc(String dcpsEmpId,String lStrFromDate, String lStrToDate,Long yearId) {

		List listAllEmpsForIntrstCalc = new ArrayList();
	
		StringBuilder lSBQuery = new StringBuilder();
        String dcpsempId=null;
		Query lQuery = null;
		gLogger.info("EmpId is::"+dcpsEmpId);
		

		lSBQuery.append(" SELECT DISTINCT dcps_emp_id FROM trn_dcps_contribution TR ");
		lSBQuery.append(" JOIN mst_dcps_contri_voucher_dtls VC ON VC.mst_dcps_contri_voucher_dtls = TR.rlt_contri_voucher_id ");
		//lSBQuery.append(" JOIN MST_DCPS_BILL_GROUP BG on BG.BILL_GROUP_ID = VC.BILL_GROUP_ID");
		//lSBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON VC.voucher_no = tn.voucher_no ");
		lSBQuery.append(" WHERE ");
		//lSBQuery.append(" VC.treasury_code=" + treasuryCode);
		//lSBQuery.append(" AND VC.ddo_code= '" + ddoCode + "' ");
		lSBQuery.append(" TR.dcps_emp_id =:dcpsEmpId ");
		//lSBQuery.append(" AND TR.SCHEME_CODE = tn.FROM_SCHEME");
		//lSBQuery.append(" AND tn.TREASURY_CODE = '" + treasuryCode +"'");
		lSBQuery.append(" AND (");

		lSBQuery.append(" (VC.VOUCHER_DATE BETWEEN '" + lStrFromDate+ "' AND '" + lStrToDate + "' )");
		lSBQuery.append(" OR");
		lSBQuery.append(" (VC.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + lStrFromDate + "' AND '" + lStrToDate + "' )");
		lSBQuery.append(" )");

		//lSBQuery.append(" AND ((VC.voucher_no = tn.voucher_no AND VC.voucher_amount = tn.dcps_amount AND VC.voucher_date = tn.voucher_date and TR.scheme_code = tn.FROM_SCHEME AND VC.treasury_code = tn.treasury_code AND VC.ddo_code = tn.ddo_code) OR (VC.manually_matched = 1))");
		lSBQuery.append(" AND VC.voucher_status = 1");
		lSBQuery.append(" AND TR.reg_status = 1");
		//lSBQuery.append(" AND EM.reg_status = 1");
		lSBQuery.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VC.status = 'G' ");

		lSBQuery.append(" UNION");

		lSBQuery.append(" select dcps_emp_id from rlt_dcps_sixpc_yearly ");
		lSBQuery.append(" where dcps_emp_id  =:dcpsEmpId ");
		lSBQuery.append(" and STATUS_FLAG = 'A' and FIN_YEAR_ID = " + yearId );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		listAllEmpsForIntrstCalc = lQuery.list();

		
		if(listAllEmpsForIntrstCalc.size()>0 && listAllEmpsForIntrstCalc!=null)
		    {
			dcpsempId = listAllEmpsForIntrstCalc.get(0).toString();
			}
		lQuery = null;
		listAllEmpsForIntrstCalc = null;
		return dcpsempId;
	}

	public List getEmpListForRegularConti(String dcpsEmpId,String strFromDatePassed, String strToDatePassed,String yearId){
	
		List tempEmpDetails = null;
		
	
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, (case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end) as payble_year, ");
		sb.append("tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
		sb.append("tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
		sb.append("nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0) ");
		sb.append("FROM TRN_DCPS_CONTRIBUTION tr ");
		sb.append("inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		sb.append("inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
		sb.append("inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
		sb.append("left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
		sb.append("where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
		sb.append("and trim(emp.dcps_id) <>'-1' and vd.VOUCHER_DATE BETWEEN :fromDate AND :toDate ");
		sb.append("AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
		sb.append("AND TR.IS_MISSING_CREDIT IS NULL AND TR.IS_CHALLAN IS NULL ");
		sb.append("and emp.DCPS_EMP_ID =:dcpsEmpId ");

		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
		lQuery.setString("fromDate", strFromDatePassed);
		lQuery.setString("toDate", strToDatePassed);
		lQuery.setParameter("dcpsEmpId",dcpsEmpId);

		tempEmpDetails = lQuery.list();
		
		
		lQuery = null;
		
		return tempEmpDetails;
	}

	public List getEmpListForMissingCredit(String dcpsEmpId,String strFromDatePassed, String strToDatePassed ,String yearId){
		List empDetails = null;
		StringBuffer sb = new StringBuffer();
		
		gLogger.info("Dcps Emp Id in getEmpListForMissingCredit is "+dcpsEmpId);

	    sb.append(" SELECT VD.VOUCHER_DATE,tr.CONTRIBUTION, TR.TYPE_OF_PAYMENT, vd.MONTH_ID, case when vd.MONTH_ID>3 then cast(fy.FIN_YEAR_CODE as bigint) else cast(fy.FIN_YEAR_CODE as bigint)+1 end as payble_year, ");
		sb.append(" tr.DCPS_CONTRIBUTION_ID,vd.VOUCHER_NO, month(vd.VOUCHER_DATE), year(vd.VOUCHER_DATE),emp.DCPS_EMP_ID, emp.DCPS_ID, ");
		sb.append(" tr.ddo_code, tr.TREASURY_CODE,emp.ddo_code,case when emp.ddo_code is not null then substr(emp.ddo_code,1,4) else '0' end as curr_treasury, ");
		sb.append(" tr.IS_MISSING_CREDIT, TR.IS_CHALLAN, ");
		sb.append(" nvl(contri_year.MST_DCPS_CONTRIBUTION_YEARLY_ID,0), nvl(contri_year.CLOSE_EMPLOYEE,0),nvl(contri_year.CLOSE_EMPLOYER,0),nvl(contri_year.OPEN_INT,0),nvl(contri_year.CLOSE_TIER2,0) ");
		sb.append(" FROM TRN_DCPS_CONTRIBUTION tr ");
		sb.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS VD on tr.RLT_CONTRI_VOUCHER_ID = VD.MST_DCPS_CONTRI_VOUCHER_DTLS ");
		sb.append(" inner join sgvc_fin_year_mst fy on fy.FIN_YEAR_ID = vd.YEAR_ID ");
		sb.append(" inner join mst_dcps_emp emp on tr.DCPS_EMP_ID = emp.DCPS_EMP_ID ");
		sb.append(" left outer join MST_DCPS_CONTRIBUTION_YEARLY contri_year on contri_year.DCPS_ID = emp.DCPS_ID and contri_year.YEAR_ID = :yearId  ");
		sb.append(" where emp.REG_STATUS =1 and trim(emp.dcps_id) is not null ");
		sb.append(" and trim(emp.dcps_id) <>'-1' and vd.MISSING_CREDIT_APPROVAL_DATE BETWEEN :fromDate AND :toDate ");
		sb.append(" AND TR.EMPLOYER_CONTRI_FLAG = 'Y' AND TR.STATUS in ('G','H') AND VD.status = 'G' ");
		sb.append(" AND TR.IS_MISSING_CREDIT = 'Y' OR TR.IS_CHALLAN = 'Y' ");
		sb.append(" and emp.DCPS_EMP_ID = :dcpsEmpId ");

		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setParameter("yearId", Long.parseLong(yearId)-1);
		lQuery.setString("fromDate", strFromDatePassed);
		lQuery.setString("toDate", strToDatePassed);
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		

		empDetails = lQuery.list();
		lQuery = null;

		return empDetails;
	}

	public List getInterestRatesForVoucherNo(Long finYearId){
		List ratesApplicable = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select INTEREST, EFFECTIVE_FROM,APPLICABLE_TO from mst_dcps_interest_rate where FIN_YEAR_ID = :finYearId");
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setLong("finYearId", finYearId);

		ratesApplicable = lQuery.list();
		lQuery = null;
		return ratesApplicable;
	}



	public List getInterestRatesForMissingCredit(String strToDatePassed,String voucherDate,Long yearId){

		List ratesApplicables = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT interest, effective_from,applicable_to,fin_year_id FROM mst_dcps_interest_rate ");
		sb.append("where fin_year_id<=:yearId and ((effective_from>=:voucherDate and applicable_to<=:strToDatePassed) or (:voucherDate BETWEEN EFFECTIVE_FROM AND APPLICABLE_TO) or (:strToDatePassed>EFFECTIVE_FROM and applicable_to is null)) ");
		sb.append("order by effective_from");
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		lQuery.setLong("yearId", yearId);
		lQuery.setParameter("voucherDate", voucherDate);
		lQuery.setParameter("strToDatePassed", strToDatePassed);
		ratesApplicables = lQuery.list();
		lQuery = null;
		return ratesApplicables;
	}
	public List getArrearsDtlsForAllEmps(String dcpsEmpid,Long yearId) {

		List tempEmpDetails = null;
		
	   StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
         gLogger.info("getArrearsDtlsForAllEmps emp is "+dcpsEmpid);
		
		lSBQuery.append(" select RL.yearlyAmount,RL.dcpsEmpId,RL.voucherNo,RL.voucherDate,FY.finYearCode,EM.payCommission,EM.dcpsId from RltDcpsSixPCYearly RL,MstEmp EM,SgvcFinYearMst FY");
		lSBQuery.append(" where ");
		//lSBQuery.append(" RL.dcpsEmpId = :dcpsEmpId");
		lSBQuery.append(" RL.dcpsEmpId = :dcpsEmpid");
		lSBQuery.append(" and RL.finYearId = :yearId ");
		lSBQuery.append(" and RL.dcpsEmpId = EM.dcpsEmpId ");
		lSBQuery.append(" and RL.finYearId = FY.finYearId ");
		lSBQuery.append(" and RL.statusFlag = 'A'");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setLong("yearId", yearId);
		lQuery.setLong("dcpsEmpid", Long.parseLong(dcpsEmpid));

		tempEmpDetails = lQuery.list();
		lQuery = null;
		
		
		return tempEmpDetails;
	}

	public MstDcpsContributionYearly getContriYearlyVOForYear(String dcpsId,
			Long previousFinYearId){
		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;

		Query lQuery = null;

		lSBQuery.append(" SELECT YC FROM MstDcpsContributionYearly YC,MstEmp EM WHERE YC.dcpsId = EM.dcpsId AND EM.dcpsId = :dcpsId AND YC.yearId = :yearId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);
		lQuery.setParameter("yearId", previousFinYearId);

		lObjMstDcpsContributionYearly = (MstDcpsContributionYearly) lQuery
		.uniqueResult();

		return lObjMstDcpsContributionYearly;
	}

	public void updateGeneratedId(Long dcpsContributionMonthlyIdForUpdate,String tableName){
		Session session = getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" UPDATE INT_TABLE_SEQ_MST SET GENERATED_ID=:generatedId WHERE UPPER(TABLE_NAME) = :tableName");

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("generatedId", dcpsContributionMonthlyIdForUpdate);
		lQuery.setParameter("tableName", tableName);

		int status = lQuery.executeUpdate();
		gLogger.info("status:::"+status);
	}



	public Long getNextSeqNum(String generatePkForMonthly){


		Long genId=0l;
		List lGenIdForMonthly=null;

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT GENERATED_ID FROM INT_TABLE_SEQ_MST where UPPER(TABLE_NAME)= :generatePkForMonthly ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("generatePkForMonthly", generatePkForMonthly);

		lGenIdForMonthly = lQuery.list();
		if(lGenIdForMonthly.size()>0 && lGenIdForMonthly.get(0)!=null){
			genId=Long.parseLong(lGenIdForMonthly.get(0).toString());
		}
	    gLogger.info("getNextSeqNum is############"+genId);
		return genId;


	}
	//Added by Ashish for New Interest Calculation : end

	public List getR3DtlsBeforeIntCal(Long dcpsEmpid) {

		List tempEmpDetails = null;
		
	   StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
         gLogger.info("getR3DtlsBeforeIntCal emp is "+dcpsEmpid);
		
		lSBQuery.append(" SELECT CURR_OPENING_BALANCE,CURR_EMP_CONTRI_CURR_YEAR,CURR_EMPLR_CONTRI_CURR_YEAR, ");
		lSBQuery.append(" CURR_INTEREST_CONTRI,CURR_TIER2_CONTRI,CURR_TIER2_INTEREST,CURR_PAYABLE_AMOUNT FROM TRN_DCPS_TERMINAL_DTLS ");
        lSBQuery.append(" where DCPS_EMP_ID=:dcpsEmpid ");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("dcpsEmpid", dcpsEmpid);
		tempEmpDetails = lQuery.list();
		lQuery = null;
		
		
		return tempEmpDetails;
	}


	public int finalterminationFlagUpdation(long dcpsEmpId) {

		Session session = getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" update TRN_DCPS_TERMINAL_DTLS set STATUS_FLAG=1,updated_date=sysdate where DCPS_EMP_ID=:dcpsEmpId ");

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("dcpsEmpId", dcpsEmpId);
	

		int status = lQuery.executeUpdate();
		gLogger.info("status:::"+status);
		
		return status;
	}

	public String checkEmployeePrvR3gen(long dcpsEmpId) {

		Session session = getSession();
		List prvR3status = null;
		String genStatus=null;
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT * FROM TRN_DCPS_TERMINAL_DTLS where DCPS_EMP_ID=:dcpsEmpId and prv_r3_gen='Y' ");

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("dcpsEmpId", dcpsEmpId);
		prvR3status = lQuery.list();
	
	     if(prvR3status.size()>0 && !prvR3status.equals("")){
	    	 genStatus="Yes";
	     }

		
		
		return genStatus;
	}



}

	


