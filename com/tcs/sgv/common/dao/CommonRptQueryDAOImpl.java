package com.tcs.sgv.common.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.valueobject.DDODetailsVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class CommonRptQueryDAOImpl extends
		GenericDaoHibernateImpl<DDODetailsVO, Integer> implements
		CommonRptQueryDAO {
	
	private static final Logger glogger=Logger.getLogger(CommonRptQueryDAOImpl.class);
	private Connection lCon = null;
	private static final SimpleDateFormat oDateFormat = new SimpleDateFormat("dd-MMM-yy"); 
	
	public CommonRptQueryDAOImpl(Class<DDODetailsVO> type,
			SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		lCon = sessionFactory.getCurrentSession().connection();
	}
	
	public CommonRptQueryDAOImpl() {
		super(DDODetailsVO.class);
	}
	
	
	/*public List getAllDept(String lStrLangId) {
		List lArrReturn = new ArrayList();
		
		Session hibSession = getSession();
		
		Query sqlQuery = hibSession.createQuery(" SELECT id.deptId, deptName FROM SgvoDeptMst " +
				" WHERE id.langId = :langId AND deptType = 'SEC' order by deptName");
		
		sqlQuery.setParameter("langId", lStrLangId);
		
		glogger.info("sqlQuery is : " + sqlQuery);
		
		Iterator iterator = sqlQuery.list().iterator();

		while (iterator.hasNext()) {
			
			Object[] tuple = (Object[]) iterator.next();
			
			ComboValuesVO lObjCombo = new ComboValuesVO();
			
			lObjCombo.setId((String)tuple[0]);
			lObjCombo.setDesc(IFMSUtil.getInitCapString((String)tuple[1]));
			
			lArrReturn.add(lObjCombo);

		}
		return lArrReturn;
	}
	*/
	
	
	public HashMap getStateProfileAmt(String lStrFinYrId, String lStrLangId) {
		HashMap lHashReturn = new HashMap();

		double lLngTotalExpAmt = 0;
		double lLngTotalRcptAmt = 0;
		double lLngTotalLiabAmt = 0;
		
		Object obj = new Object();

		Session hibSession = getSession();
		
		glogger.info("hibSession is : " + hibSession.getClass());
		
		Query sqlQuery = hibSession.createQuery("select (sum(f.nxtYrBe)* 1000) from SgvaExpestFinalamt f where f.finYrId = :finyrid " +
				" and f.levelId = 4 and f.formType != 'RE' and" +
				" (f.recFlag not in ('R', 'C', 'M') or f.recFlag is null) and f.giaFlg = 'N'");

		sqlQuery.setParameter("finyrid", Long.parseLong(lStrFinYrId));
		
		glogger.info("sqlQuery is : " + sqlQuery);

		Iterator iterator = sqlQuery.list().iterator();

		while (iterator.hasNext()) {
			obj = iterator.next();
			if (obj != null)
			{
				lLngTotalExpAmt = (double) Double.parseDouble(obj.toString()); 
				glogger.info("lLntTotalExpAmt is : " + lLngTotalExpAmt);
			}
		}

		lHashReturn.put("ExpAmt", lLngTotalExpAmt + "");
		
		sqlQuery = hibSession.createQuery("SELECT (SUM(r.nxtYrBeRcpt) * 1000) from SgvaRcptestFinalamt r"
				+ " WHERE r.finYrId = :finyrid AND r.levelId = 4");
		
		sqlQuery.setParameter("finyrid", Long.parseLong(lStrFinYrId));
		
		iterator = sqlQuery.list().iterator();

		while (iterator.hasNext()) {
			obj = iterator.next();
			if (obj != null)
				lLngTotalRcptAmt = (double) Double.parseDouble(obj.toString()); 
		}
		
		lHashReturn.put("RcptAmt", lLngTotalRcptAmt + "");
		
		sqlQuery = hibSession.createQuery("SELECT SUM(R.billNetAmount) FROM TrnBillRegister R"
				+ " WHERE R.finYearId = :finyrid AND R.versionId = (SELECT MAX(R1.versionId)" 
				+ " FROM TrnBillRegister R1 WHERE R1.billCntrlNo = R.billCntrlNo) AND R.currBillStatus IN "
				+ " (SELECT M.statusCode FROM MstStatusLaibility M WHERE M.liabilityId IN ('45', '46'))");
		
		sqlQuery.setParameter("finyrid", lStrFinYrId);
		
		iterator = sqlQuery.list().iterator();

		while (iterator.hasNext()) {
			obj = iterator.next();
			if (obj != null)
				lLngTotalLiabAmt = (double) Double.parseDouble(obj.toString()); 
		}
		
		lHashReturn.put("ActualExpAmt", lLngTotalLiabAmt + "");
		
		glogger.info("lHashReturn is : " + lHashReturn);
		
		return lHashReturn;
	}
	
	
	public ArrayList getPlNpl(String lStrLangId, String lstrLocId) 
    {
        ResourceBundle lObjBudConstantsBundle = ResourceBundle.getBundle("resources/common/budget/SGVConstantsBud", Locale.getDefault());
        ArrayList lArrPlNpl = new ArrayList();
        
        ComboValuesVO lObjCombo1 = new ComboValuesVO();
        ComboValuesVO lObjCombo2 = new ComboValuesVO();
        
        lObjCombo1.setId(lObjBudConstantsBundle.getString("EXPEST.Plan"));
        lObjCombo1.setDesc(lObjBudConstantsBundle.getString("EXPEST.PlanDesc"));
        
        lObjCombo2.setId(lObjBudConstantsBundle.getString("EXPEST.NonPlan"));
        lObjCombo2.setDesc(lObjBudConstantsBundle.getString("EXPEST.NonPlanDesc"));
        
        glogger.info("lObjCombo1.getId is : " + lObjCombo1.getId());
        glogger.info("lObjCombo2.getId is : " + lObjCombo2.getId());
        
        glogger.info("lObjCombo1.getDesc is : " + lObjCombo1.getDesc());
        glogger.info("lObjCombo2.getDesc is : " + lObjCombo2.getDesc());

        lArrPlNpl.add(lObjCombo1);
        lArrPlNpl.add(lObjCombo2);

        return lArrPlNpl;
    }
	
	public HashMap getAllDeptBEAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId)
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Session hibSession = getSession();
		
		StringBuffer lStrQuery = new StringBuffer();
		
		lStrQuery.append(" SELECT D.id.deptId ,(SUM(F.nxtYrBe) * 1000)" +
				" FROM SgvaExpestFinalamt F, SgvoDeptMst D " + 
				" WHERE D.id.deptId = F.deptId AND D.id.langId = :langId AND" +
				" D.deptType = 'SEC' AND F.levelId = 4 AND F.giaFlg = 'N' AND F.finYrId = :finYrId " +
				" AND F.formType <> 'RE' AND (F.recFlag NOT IN ('R', 'C', 'M')" +
				" OR F.recFlag IS NULL) AND F.planNonplan = :planType GROUP BY D.id.deptId");
		
		glogger.info("Query for getAllDeptBEAmt is : " + lStrQuery.toString());
		
		Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
		
		sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
		sqlQuery.setParameter("planType", lStrPlanType);
		sqlQuery.setParameter("langId", lStrLangId);
	    	
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			if (lObj != null){
				lHashData.put(lObj[0].toString(), Double.parseDouble(lObj[1].toString()));
			}
		}
		
		return lHashData;
	}
	
	public HashMap getAllDeptGrantAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId)
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Session hibSession = getSession();
		
		StringBuffer lStrQuery = new StringBuffer();
		
		lStrQuery.append(" SELECT DEP.id.deptId , (SELECT (SUM(AMT.proposedAmt) * 1000) " +
				" FROM SgvaBudrlsOrderHdr HDR, SgvaBudrlsMonthMpg MM, SgvaBudrlsOrderDtls DTL," + 
				" SgvaBudrlsUsrAmt AMT, SgvaBudrlsDivisionMpg DIV1 WHERE HDR.rlsOrderHdrId = MM.fkHdrId" +
				" and MM.monthMpgId = DTL.fkMonthMpgId and AMT.fkDtlsId = DTL.rlsOrderDtlsId" +
				" and AMT.approvedRejected in ('a', 'A') and MM.grntRlsFlag in ('y', 'Y')" +
				" and HDR.finYearId = :finYrId and DTL.divisionId = DIV1.divisionId and " + 
				" DIV1.deptId = DEP.id.deptId and HDR.planNonplan = :planType) " +
				" from SgvoDeptMst DEP where DEP.id.langId = :langId and DEP.deptType = 'SEC'");
		
		glogger.info("Query for getAllDeptGrantAmt is : " + lStrQuery.toString());
		
		Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
		
		sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
		sqlQuery.setParameter("planType", lStrPlanType);
		sqlQuery.setParameter("langId", lStrLangId);
	    	
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null){
				lHashData.put(lObj[0].toString(),lLngAmt);
			}
		}
		
		return lHashData;
	}
	
	public HashMap getAllDeptActualsAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId)
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Session hibSession = getSession();
		
		StringBuffer lStrQuery = new StringBuffer();
		
		/*lStrQuery.append(" select locdept.departmentId , sum(chkdtl.chequeAmt) from TrnBillRegister r," +
				" RltBillCheque billchk, TrnChequeDtls chkdtl, RltLocationDepartment locdept, " +
				" TrnBillBudheadDtls billhd, CmnLookupMst lkup, CmnLanguageMst lngmst " +
				" where r.finYearId = :finYrId and r.ddoDeptId = locdept.locId" +
				" and r.billNo = billchk.billNo and billchk.chequeId = chkdtl.chequeId" +
				" and chkdtl.clearedDt is not null and r.billNo = billhd.billNo " +
				" and billhd.budType = lkup.lookupId and lkup.lookupShortName = :planType " +
				" and lkup.cmnLanguageMst.langId = lngmst.langId and lngmst.langShortName = :langId " +
				" group by locdept.departmentId");*/
		
		if(lStrPlanType.equalsIgnoreCase("PL"))
			lStrPlanType = "Plan";
		else if(lStrPlanType.equalsIgnoreCase("NP"))
			lStrPlanType = "Non-Plan";
		
		lStrQuery.append(" select locdept.department_Id, sum(r.bill_gross_amount) from ");
		lStrQuery.append(" Trn_Bill_Register r,Rlt_Location_Department locdept , Trn_Bill_Budhead_Dtls billhd, ");
		lStrQuery.append(" Cmn_Lookup_Mst lkup, Cmn_Language_Mst lngmst where r.fin_Year_Id = '"+lStrFinYrId+"' and ");
		lStrQuery.append(" r.ddo_Dept_Id = locdept.loc_Id and r.cheque_status is not null and ");
		lStrQuery.append(" r.bill_No = billhd.bill_No "); //and r.version_Id = (select max(r1.version_Id) from ");
		//lStrQuery.append(" Trn_Bill_Register r1 where r1.bill_Cntrl_No = r.bill_Cntrl_No) and billhd.version_id = ");
		//lStrQuery.append(" ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where  ");
		//lStrQuery.append(" billhddtls.bill_no = billhd.bill_No) ");
		lStrQuery.append(" and billhd.bud_Type = lkup.lookup_Id and ");
		lStrQuery.append(" lkup.lookup_Short_Name = '"+lStrPlanType+"' and lkup.lang_Id = lngmst.lang_Id and ");
		lStrQuery.append(" lngmst.lang_Short_Name = '"+lStrLangId+"' ");
		lStrQuery.append(" group by locdept.department_Id ");
		
		glogger.info("Query for getAllDeptActualsAmt is : " + lStrQuery.toString());
		
		/*Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
		sqlQuery.setParameter("finYrId", lStrFinYrId);
		sqlQuery.setParameter("planType", lStrPlanType);
		sqlQuery.setParameter("langId", lStrLangId);*/
	    	
		Query sqlQuery = hibSession.createSQLQuery(lStrQuery.toString());
		
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? (long) Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null){
				lHashData.put(lObj[0].toString(),lLngAmt);
			}
		}
		
		return lHashData;
	}
	
	public HashMap getAllDeptLiabAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId)
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Session hibSession = getSession();
		
		if(lStrPlanType.equalsIgnoreCase("PL"))
			lStrPlanType = "Plan";
		else if(lStrPlanType.equalsIgnoreCase("NP"))
			lStrPlanType = "Non-Plan";
		
		StringBuffer lStrQuery = new StringBuffer();
		
		/*lStrQuery.append(" select sum(r.billNetAmount),locdept.departmentId" +
				" from TrnBillRegister r,RltLocationDepartment locdept," +
				" TrnBillBudheadDtls billhd, CmnLookupMst lkup, CmnLanguageMst lngmst" +
				" where r.finYearId = :finYrId and r.versionId = (select max(r1.versionId)" +
				" from TrnBillRegister r1 where r1.billCntrlNo = r.billCntrlNo)" +
				" and r.currBillStatus in (select m.statusCode from MstStatusLaibility m" +
				" where m.liabilityId in ('45', '46')) and r.ddoDeptId  = locdept.locId" +
				" and r.billNo = billhd.billNo and billhd.budType = lkup.lookupId" +
				" and lkup.lookupShortName = :planType and lkup.cmnLanguageMst.langId = lngmst.langId " +
				" and lngmst.langShortName = :langId group by locdept.departmentId");*/
		
		lStrQuery.append(" select locdept.department_Id, sum(r.bill_gross_Amount) from Trn_Bill_Register r, ");
		lStrQuery.append(" Rlt_Location_Department locdept, Trn_Bill_Budhead_Dtls billhd, Cmn_Lookup_Mst lkup, ");
		lStrQuery.append(" Cmn_Language_Mst lngmst where r.fin_Year_Id = '"+lStrFinYrId+"' ");//and r.version_Id = ");
		//lStrQuery.append(" (select max(r1.version_Id) from Trn_Bill_Register r1 where ");
		//lStrQuery.append(" r1.bill_Cntrl_No = r.bill_Cntrl_No) ");
		lStrQuery.append(" and r.curr_Bill_Status in ");
		lStrQuery.append(" (select m.status_Code from Mst_Status_Laibility m where m.liability_Id in ");
		lStrQuery.append(" ('45', '46')) and r.ddo_Dept_Id  = locdept.loc_Id and ");//billhd.version_id = ");
		//lStrQuery.append(" ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where ");
		//lStrQuery.append(" billhddtls.bill_no = billhd.bill_No) and ");
		lStrQuery.append(" r.bill_No = billhd.bill_No and ");
		lStrQuery.append(" billhd.bud_Type = lkup.lookup_Id and lkup.lookup_Short_Name = '"+lStrPlanType+"' and ");
		lStrQuery.append(" lkup.lang_Id = lngmst.lang_Id  and lngmst.lang_Short_Name = '"+lStrLangId+"' ");
		lStrQuery.append(" group by locdept.department_Id ");
		
		glogger.info("Query for getAllDeptLiabAmt is : " + lStrQuery.toString());
		
		/*Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
		sqlQuery.setParameter("finYrId", lStrFinYrId);
		sqlQuery.setParameter("planType", lStrPlanType);
		sqlQuery.setParameter("langId", lStrLangId);*/
	    	
		Query sqlQuery = hibSession.createSQLQuery(lStrQuery.toString());
		
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null){
				lHashData.put(lObj[0].toString(),lLngAmt);
			}
		}
		return lHashData;
	}
	
	public HashMap getAllMjrHdBEAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId) {
	    
		HashMap lHashReturn = new HashMap();
	    	    
	    Session hibSession = getSession();
	    glogger.info(" In getAllMjrHdBEAmt() ");
		glogger.info("hibSession is : " + hibSession.getClass());
				
		String lObjQuery =  " SELECT MJR.budmjrhdCode, (SUM(F.nxtYrBe) * 1000) " +
							" FROM SgvaExpestFinalamt F, SgvaBudmjrhdMst MJR " +
							" WHERE MJR.langId = :lngId AND F.levelId = 4 AND F.finYrId = :finyrid AND F.formType <> 'RE' AND " +
							" (F.recFlag NOT IN ('R', 'C', 'M') OR F.recFlag IS NULL) AND F.giaFlg = 'N' " +
							" AND F.budbpnCode = MJR.bpnCode AND F.buddemandCode = MJR.demandCode AND F.budmjrhdCode = " + 
							" MJR.budmjrhdCode AND F.deptId = :deptId AND F.planNonplan = :planNonPlan GROUP BY MJR.budmjrhdCode ORDER BY MJR.budmjrhdCode ";
		
		Query sqlQuery = hibSession.createQuery( lObjQuery );
	
		sqlQuery.setParameter("finyrid", Long.parseLong(lStrFinYrId));
		sqlQuery.setParameter("deptId", lStrDeptId);
		sqlQuery.setParameter("planNonPlan", lStrPlanType);
		sqlQuery.setParameter("lngId", lStrLangId);
		
		glogger.info("sqlQuery is : " + sqlQuery);
	
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null)
				lHashReturn.put(lObj[0].toString(), lLngAmt);
		}
				
		return lHashReturn;
	}
	
	public HashMap getAllMjrHdGrantAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId) 
	 {
	    HashMap lHashReturn = new HashMap();
	    StringBuffer lSb = new StringBuffer();
	    //Session hibSession = getSession();
	    
	    Statement lStmt = null;
	    ResultSet lRs = null; 
	    
	    glogger.info(" In getAllMjrHdGrantAmt() ");
		//glogger.info("hibSession is : " + hibSession.getClass());

	    try
	    {
	    	lSb.append("SELECT BUDMJRHD_CODE MJRHD, (SUM(AMOUNT) * 1000) GRANT_AMT " +
	                " FROM (SELECT MPG.OFFICER_MPG_ID, " + 
	                " MPG.BUDMJRHD_CODE, " + 
	                " MJR.BUDMJRHD_DESC_LONG, " + 
	                  " (SELECT SUM(AMT.PROPOSED_AMT) " + 
	                  "FROM SGVA_BUDRLS_ORDER_HDR  HDR, " + 
	                  " SGVA_BUDRLS_MONTH_MPG  MM, " + 
	                  " SGVA_GRNTRLS_CO_DTLS   CO, " + 
	                  " SGVA_GRNTRLS_CO_USRAMT AMT " + 
	                  " WHERE HDR.FIN_YEAR_ID = " + Long.parseLong(lStrFinYrId) + " AND HDR.PLAN_NONPLAN = '" + lStrPlanType + "' AND " + 
	                  " HDR.RLS_ORDER_HDR_ID = MM.FK_HDR_ID AND " + 
	                  " MM.GRNT_RLS_FLAG IN ('Y', 'y') AND " + 
	                  " MM.MONTH_MPG_ID = CO.FK_MONTH_MPG_ID AND " + 
	                  " CO.GRNTRLS_DTLS_ID = AMT.FK_DTLS_ID AND " + 
	                  " AMT.APPROVED_REJECTED IN ('A', 'a') AND " + 
	                  " CO.OFFICER_MPG_ID = MPG.OFFICER_MPG_ID) AMOUNT " + 
	                " FROM SGVA_BUDMJRHD_MST            MJR, " + 
	                " SGVA_CONTROLLING_OFFICER_MPG MPG, " + 
	                " SGVA_BUDBPN_MAPPING          BPN " + 
	                " WHERE MJR.BPN_CODE = BPN.BPNCODE AND BPN.DEPT_ID = MPG.DEPT_ID AND " + 
	                " BPN.LANG_ID = MJR.LANG_ID AND BPN.LOC_ID = MJR.LOC_ID AND " + 
	                " MJR.DEMAND_CODE = MPG.DEMAND_CODE AND " + 
	                " MJR.BUDMJRHD_CODE = MPG.BUDMJRHD_CODE AND " + 
	                " MPG.DEPT_ID = '" +  lStrDeptId + "' AND MJR.LANG_ID = '"  + lStrLangId + "' AND " + 
	                " MPG.PLAN_NONPLAN = '" + lStrPlanType + "' AND " + 
	                " MPG.STATUS = 'Active') dumpTable " + 
	                " GROUP BY MJRHD");

	            	
	      	glogger.info("Query for getAllSchemeBEAmt is : " + lSb.toString());
	      
	      	/*Query sqlQuery = hibSession.createSQLQuery( lSb.toString() );
					      
			Iterator iterator = sqlQuery.list().iterator();
			Object lObj[];
			double lLngAmt=0;
			
			while (iterator.hasNext()) {
				lObj = (Object[]) iterator.next();
				
				lLngAmt = (lObj[1] != null && lObj[1] !="" ) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
				
				if (lObj != null)
					lHashReturn.put(lObj[0].toString(), lLngAmt);
					
		   	}*/
	      	
	      	double lLngAmt=0;
	      	lCon = getSession().connection();
	      	lStmt = lCon.createStatement();
	      	lRs = lStmt.executeQuery(lSb.toString());
	      	while(lRs.next())
	      	{
	      		lLngAmt = (lRs.getDouble("GRANT_AMT") != 0)  ? lRs.getDouble("GRANT_AMT") : 0;
	      		lHashReturn.put(lRs.getString("MJRHD"), lLngAmt );
	      	}
	    }
	    catch(Exception e)
	    {
	    	glogger.info("Exception in getAllMjrHdGrantAmt "+e);
	    	//throw e;
	    }
	    finally
	    {
	    	try
	    	{
		    	lRs.close();
		    	lStmt.close();
		    	lCon.close();
	    	}
	    	catch(Exception e)
	    	{
	    		glogger.info("Exception in getAllMjrHdGrantAmt while closing Connection"+e);
	    	}
	    }
			return lHashReturn;
	  }
	
	
	public HashMap getAllMjrHdActualExp(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId)
	 {
	    HashMap lHashReturn = new HashMap();
	    StringBuffer lSb = new StringBuffer();
	    Session hibSession = getSession();
	    
		glogger.info("hibSession is : " + hibSession.getClass());
		
		/*lSb.append("select billhd.budMjrHd, sum(chkdtl.chequeAmt) from TrnBillRegister r, " +
					" RltBillCheque billchk, TrnChequeDtls chkdtl, RltLocationDepartment locdept, " +
					" TrnBillBudheadDtls billhd, CmnLookupMst lkup, CmnLanguageMst lngmst " +
					" where r.finYearId = :finYrId and r.ddoDeptId = locdept.locId " +
					" and r.billNo = billchk.billNo and billchk.chequeId = chkdtl.chequeId " +
					" and chkdtl.clearedDt is not null and r.billNo = billhd.billNo " +
					" and billhd.budType = lkup.lookupId and lkup.lookupShortName = :planType " +
					" and lkup.cmnLanguageMst.langId = lngmst.langId and lngmst.langShortName = :langId " +
					" and locdept.departmentId = :deptId group by billhd.budMjrHd ");*/
		
		if(lStrPlanType.equalsIgnoreCase("PL"))
			lStrPlanType = "Plan";
		else if(lStrPlanType.equalsIgnoreCase("NP"))
			lStrPlanType = "Non-Plan";
		
		lSb.append("select billhd.bud_Mjr_Hd, sum(r.bill_gross_amount) from Trn_Bill_Register r, ");
		lSb.append("Rlt_Location_Department locdept,  Trn_Bill_Budhead_Dtls billhd, Cmn_Lookup_Mst lkup, ");
		lSb.append("Cmn_Language_Mst lngmst  where r.fin_Year_Id = "+Long.parseLong(lStrFinYrId)+" ");
		lSb.append("and r.ddo_Dept_Id = locdept.loc_Id  ");
		lSb.append("and r.cheque_status is not null and ");//r.version_Id = (select max(r1.version_Id) ");
		//lSb.append("from Trn_Bill_Register r1 where r1.bill_Cntrl_No = r.bill_Cntrl_No) and ");
		//lSb.append("billhd.version_id = ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  ");
		//lSb.append("billhddtls where  billhddtls.bill_no = billhd.bill_No) and ");
		lSb.append(" r.bill_No = billhd.bill_No  ");
		lSb.append("and billhd.bud_Type = lkup.lookup_Id and lkup.lookup_Short_Name = '"+lStrPlanType+"'  ");
		lSb.append("and lkup.lang_Id = lngmst.lang_Id and lngmst.lang_Short_Name = '"+lStrLangId+"'  and ");
		lSb.append("locdept.department_Id = '"+lStrDeptId+"' group by billhd.bud_Mjr_Hd ");
	    
      	glogger.info("Query for getAllSchemeBEAmt is : " + lSb.toString());
      
      	/*Query sqlQuery = hibSession.createQuery( lSb.toString());
		
      	sqlQuery.setParameter("finYrId", lStrFinYrId);
		sqlQuery.setParameter("deptId", lStrDeptId);
		sqlQuery.setParameter("planType", lStrPlanType);
		sqlQuery.setParameter("langId", lStrLangId);*/
		
      	Query sqlQuery = hibSession.createSQLQuery( lSb.toString() );
      	
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null)
				lHashReturn.put(lObj[0].toString(), lLngAmt);
		}
      	
		return lHashReturn;
	  }
	
	
	public HashMap getAllMjrHdLiabilityAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId)
	 {
	    HashMap lHashReturn = new HashMap();
	    StringBuffer lSb = new StringBuffer();
	    Session hibSession = getSession();
	    
	    glogger.info("hibSession is : " + hibSession.getClass());
		
		/*lSb.append("select billhd.budMjrHd, sum(r.billNetAmount) " +
					" from TrnBillRegister r,RltLocationDepartment locdept, " +
					" TrnBillBudheadDtls billhd, CmnLookupMst lkup, CmnLanguageMst lngmst " +
					" where r.finYearId = :finYrId and r.versionId = (select max(r1.versionId) from TrnBillRegister r1 " +
					" where r1.billCntrlNo = r.billCntrlNo) " +
					" and r.currBillStatus in (select m.statusCode from MstStatusLaibility m " +
					" where m.liabilityId in ('45', '46')) " +
					" and r.ddoDeptId = locdept.locId and r.billNo = billhd.billNo and billhd.budType = lkup.lookupId " +
					" and lkup.lookupShortName = :planType and lkup.cmnLanguageMst.langId = lngmst.langId " +
					" and lngmst.langShortName = :langId and locdept.departmentId = :deptId " +
					" group by billhd.budMjrHd "); */
	    
	    if(lStrPlanType.equalsIgnoreCase("PL"))
			lStrPlanType = "Plan";
		else if(lStrPlanType.equalsIgnoreCase("NP"))
			lStrPlanType = "Non-Plan";
	    
	    lSb.append("select billhd.bud_Mjr_Hd, sum(r.bill_gross_amount) from Trn_Bill_Register r, ");
	    lSb.append("Rlt_Location_Department locdept,  Trn_Bill_Budhead_Dtls billhd, Cmn_Lookup_Mst lkup, ");
	    lSb.append("Cmn_Language_Mst lngmst  where r.fin_Year_Id = "+Long.parseLong(lStrFinYrId)+" and ");//r.version_Id = ");
	    //lSb.append("(select max(r1.version_Id) from Trn_Bill_Register r1  where r1.bill_Cntrl_No = r.bill_Cntrl_No) and ");
		lSb.append("r.curr_Bill_Status in (select m.status_Code from Mst_Status_Laibility m  where m.liability_Id ");
		lSb.append("in ('45', '46'))  and r.ddo_Dept_Id = locdept.loc_Id and ");
		//lSb.append("billhd.version_id = ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where  billhddtls.bill_no = billhd.bill_No) and ");
		//lSb.append("r.bill_No = billhd.bill_No and ");
		lSb.append("billhd.bud_Type = lkup.lookup_Id  and lkup.lookup_Short_Name = '"+lStrPlanType+"' and lkup.lang_Id = ");
		lSb.append("lngmst.lang_Id  and lngmst.lang_Short_Name = '"+lStrLangId+"' and locdept.department_Id = '"+lStrDeptId+"' ");
		lSb.append("group by billhd.bud_Mjr_Hd ");
	    
	    
      	glogger.info("Query for getAllSchemeBEAmt is : " + lSb.toString());
      
      	/*Query sqlQuery = hibSession.createQuery( lSb.toString());
		
      	sqlQuery.setParameter("finYrId", lStrFinYrId);
		sqlQuery.setParameter("deptId", lStrDeptId);
		sqlQuery.setParameter("planType", lStrPlanType);
		sqlQuery.setParameter("langId", lStrLangId);*/

      	Query sqlQuery = hibSession.createSQLQuery( lSb.toString() );
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null)
				lHashReturn.put(lObj[0].toString(), lLngAmt);
		}
    
		return lHashReturn;
	  }
	
	
	public HashMap getAllSchemeBEAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId) 
	{
		HashMap lHashReturn = new HashMap();
		StringBuffer lSb = new StringBuffer();
	    Session hibSession = getSession();
		glogger.info("hibSession is : " + hibSession.getClass());
		
				lSb.append("SELECT (F.buddemandCode || ':' || F.budmjrhdCode || ':' || " +
	                		"    F.budsubmjrhdCode || ':' || F.budminhdCode || ':' || " + 
	                		"    F.budsubhdCode), " + 
	                		"    (SUM(F.nxtYrBe) * 1000) " + 
	                		" FROM SgvaExpestFinalamt F " + 
	                		" WHERE F.finYrId = :finYrId AND F.planNonplan = :planNonPlan AND F.levelId = 4 AND " + 
	                		"    F.giaFlg = 'N' AND F.formType <> 'RE' AND " + 
	                		"    (F.recFlag NOT IN ('R', 'C', 'M') OR F.recFlag IS NULL) AND " + 
	                		"    F.deptId = :deptId " + 
	                		" GROUP BY F.buddemandCode, " + 
	                		"     F.budmjrhdCode, " + 
	                		"     F.budsubmjrhdCode, " + 
	                		"     F.budminhdCode, " + 
	                		"     F.budsubhdCode " +
	    					" ORDER BY F.buddemandCode, " + 
	                		"     F.budmjrhdCode, " + 
	                		"     F.budsubmjrhdCode, " + 
	                		"     F.budminhdCode, " + 
	                		"     F.budsubhdCode ");
	      
	    glogger.info("Query for getAllSchemeBEAmt is : " + lSb.toString());	      
	    Query sqlQuery = hibSession.createQuery( lSb.toString() );
		
		sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
		sqlQuery.setParameter("planNonPlan", lStrPlanType);
		sqlQuery.setParameter("deptId", lStrDeptId);
	    	
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null)
				lHashReturn.put(lObj[0].toString(), lLngAmt);
		}
	    		
	    return lHashReturn;
	}
	
	public HashMap getAllSchemeGrantAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId, String lStrLocId)
	  {
		HashMap lHashReturn = new HashMap();
	    StringBuffer lSb = new StringBuffer();
	    Session hibSession = getSession();
	    
		glogger.info("hibSession is : " + hibSession.getClass());
		
		lSb.append(" SELECT (DMN.DEMAND_CODE || ':' || MJR.BUDMJRHD_CODE || ':' || "  +
              	" SBM.BUDSUBMJRHD_CODE || ':' || MNR.BUDMINHD_CODE || ':' || " +
              	" SBH.BUDSUBHD_CODE) HEAD_COMBINATION, " +
              	" (SELECT (SUM(USR.PROPOSED_AMT) * 1000) " +
              	"  FROM SGVA_BUDRLS_MONTH_MPG  MNTH, " +
                  " SGVA_GRNTRLS_CO_DTLS   CODTL, " +
                  " SGVA_BUDRLS_ORDER_HDR  HDR, " +
                  " SGVA_GRNTRLS_CO_USRAMT USR " +
                  " WHERE CODTL.OFFICER_MPG_ID = MPG.OFFICER_MPG_ID AND " +
                  " CODTL.FK_MONTH_MPG_ID = MNTH.MONTH_MPG_ID AND " +
                  " MNTH.FK_HDR_ID = HDR.RLS_ORDER_HDR_ID AND " +   
                  " MPG.PLAN_NONPLAN = HDR.PLAN_NONPLAN AND " +
                  " HDR.FIN_YEAR_ID = " + Long.parseLong(lStrFinYrId) + " AND " +   
                  " CODTL.GRNTRLS_DTLS_ID = USR.FK_DTLS_ID AND " +   
                  " USR.APPROVED_REJECTED IN ('A', 'a')) GRANT_AMT " + 
                  " FROM SGVA_BUDBPN_MAPPING          BPN, " +   
                  " SGVA_BUDDEMAND_MST           DMN, " +   
                  " SGVA_BUDMJRHD_MST            MJR, " +
                  " SGVA_BUDSUBMJRHD_MST         SBM, " +   
                  " SGVA_BUDMINHD_MST            MNR, " +   
                  " SGVA_BUDSUBHD_MST            SBH, " +   
                  " SGVA_CONTROLLING_OFFICER_MPG MPG " +   
                  " WHERE SBH.LANG_ID = BPN.LANG_ID AND BPN.DEPT_ID = '" + lStrDeptId + "' AND " +   
                  " DMN.BPNCODE = BPN.BPNCODE AND DMN.LANG_ID = SBH.LANG_ID AND " +   
                  " DMN.LOC_ID = SBH.LOC_ID AND MJR.BPN_CODE = BPN.BPNCODE AND " +
                  " MJR.DEMAND_CODE = DMN.DEMAND_CODE AND MJR.LANG_ID = SBH.LANG_ID AND " +   
                  " MJR.LOC_ID = SBH.LOC_ID AND SBM.BPN_CODE = BPN.BPNCODE AND " +
                  " SBM.DEMAND_CODE = DMN.DEMAND_CODE AND " +   
                  " SBM.BUDMJRHD_CODE = MJR.BUDMJRHD_CODE AND SBM.LANG_ID = SBH.LANG_ID AND " +   
                  " SBM.LOC_ID = SBH.LOC_ID AND MNR.BPN_CODE = BPN.BPNCODE AND " +   
                  " MNR.DEMAND_CODE = DMN.DEMAND_CODE AND " +   
                  " MNR.BUDMJRHD_CODE = MJR.BUDMJRHD_CODE AND " +   
                  " MNR.BUDSUBMJRHD_CODE = SBM.BUDSUBMJRHD_CODE AND " +
                  " MNR.LANG_ID = SBH.LANG_ID AND MNR.LOC_ID = SBH.LOC_ID AND " +   
                  " SBH.BPN_CODE = BPN.BPNCODE AND SBH.DEMAND_CODE = DMN.DEMAND_CODE AND " +   
                  " SBH.BUDMJRHD_CODE = MJR.BUDMJRHD_CODE AND " +   
                  " SBH.BUDSUBMJRHD_CODE = SBM.BUDSUBMJRHD_CODE AND " +
                  " SBH.BUDMINHD_CODE = MNR.BUDMINHD_CODE AND SBH.LANG_ID = '" + lStrLangId + "' AND 	" +   
                  " SBH.LOC_ID = '" + lStrLocId + "' AND MPG.DEMAND_CODE = DMN.DEMAND_CODE AND " +  
                  " MPG.BUDMJRHD_CODE = MJR.BUDMJRHD_CODE AND " +   
                  " MPG.BUDSUBMJRHD_CODE = SBM.BUDSUBMJRHD_CODE AND " +   
                  " MPG.BUDMINHD_CODE = MNR.BUDMINHD_CODE AND " +   
                  " MPG.BUDSUBHD_CODE = SBH.BUDSUBHD_CODE AND MPG.PLAN_NONPLAN = '" + lStrPlanType + "' " + 
                  " ORDER BY DMN.DEMAND_CODE, " +
                  " MJR.BUDMJRHD_CODE, " +   
                  " SBM.BUDSUBMJRHD_CODE, " +
                  " MNR.BUDMINHD_CODE, " +   
                  " SBH.BUDSUBHD_CODE ");
       
		
	      	glogger.info("Query for getAllSchemeBEAmt is : " + lSb.toString());
	      
	      	Query sqlQuery = hibSession.createSQLQuery( lSb.toString() );
					      
			Iterator iterator = sqlQuery.list().iterator();
			Object lObj[];
			double lLngAmt=0;
			
			while (iterator.hasNext()) {
				lObj = (Object[]) iterator.next();
				
				lLngAmt = (lObj[1] != null) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
				
				if (lObj != null)
					lHashReturn.put(lObj[0].toString(), lLngAmt);
			}
	      	
			return lHashReturn;
	  }
	
	
	public HashMap getAllSchemeActualExp(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId)
	 {
	    HashMap lHashReturn = new HashMap();
	    StringBuffer lSb = new StringBuffer();
	    Session hibSession = getSession();
	    
	    glogger.info("hibSession is : " + hibSession.getClass());
		
	    /*lSb.append("select concat(billhd.dmndNo,':',billhd.budMjrHd,':',billhd.budSubmjrHd,':', billhd.budMinHd,':', billhd.budSubHd), sum(chkdtl.chequeAmt) " + 
					" from TrnBillRegister r, RltBillCheque billchk, TrnChequeDtls chkdtl, RltLocationDepartment locdept, " +
					" TrnBillBudheadDtls billhd, CmnLookupMst lkup, CmnLanguageMst lngmst " +
					" where r.finYearId = :finYrId and r.ddoDeptId = locdept.locId " +
					" and r.billNo = billchk.billNo and billchk.chequeId = chkdtl.chequeId " +
					" and chkdtl.clearedDt is not null and r.billNo = billhd.billNo " +
					" and billhd.budType = lkup.lookupId and lkup.lookupShortName = :planType " +
					" and lkup.cmnLanguageMst.langId = lngmst.langId and lngmst.langShortName = :langId " +
					" and locdept.departmentId = :deptId " + 
					" group by billhd.dmndNo, billhd.budMjrHd, billhd.budSubmjrHd, billhd.budMinHd, billhd.budSubHd ");*/
	    
	    if(lStrPlanType.equalsIgnoreCase("PL"))
			lStrPlanType = "Plan";
		else if(lStrPlanType.equalsIgnoreCase("NP"))
			lStrPlanType = "Non-Plan";
	    
		lSb.append("select concat(billhd.dmnd_No,':',billhd.bud_Mjr_Hd,':',billhd.bud_Submjr_Hd,':', billhd.bud_Min_Hd,':', billhd.bud_Sub_Hd), ");
		lSb.append("sum(r.bill_gross_amount)  from Trn_Bill_Register r, ");
		lSb.append("Rlt_Location_Department locdept,  Trn_Bill_Budhead_Dtls billhd, Cmn_Lookup_Mst lkup, ");
		lSb.append("Cmn_Language_Mst lngmst  where r.fin_Year_Id = "+Long.parseLong(lStrFinYrId)+" and r.ddo_Dept_Id = locdept.loc_Id ");
		lSb.append("and r.cheque_status is not null and ");
		//lSb.append("r.version_Id =(select max(r1.version_Id) from Trn_Bill_Register r1 where r1.bill_Cntrl_No = r.bill_Cntrl_No) and ");
		lSb.append("r.bill_No = billhd.bill_No  and ");
		//lSb.append("billhd.version_id = ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where  billhddtls.bill_no = billhd.bill_No) and ");
		lSb.append("billhd.bud_Type = lkup.lookup_Id and lkup.lookup_Short_Name = '"+lStrPlanType+"' ");
		lSb.append("and lkup.lang_Id = lngmst.lang_Id and lngmst.lang_Short_Name = '"+lStrLangId+"' ");
		lSb.append("and locdept.department_Id = '"+lStrDeptId+"'  group by billhd.dmnd_No, billhd.bud_Mjr_Hd, ");
		lSb.append("billhd.bud_Submjr_Hd, billhd.bud_Min_Hd, billhd.bud_Sub_Hd ");
	    
	            	
      	glogger.info("Query for getAllSchemeActualExp is : " + lSb.toString());
	      
      	/*Query sqlQuery = hibSession.createQuery( lSb.toString());
		
      	sqlQuery.setParameter("finYrId", lStrFinYrId);
		sqlQuery.setParameter("deptId", lStrDeptId);
		sqlQuery.setParameter("planType", lStrPlanType);
		sqlQuery.setParameter("langId", lStrLangId);*/
		
      	Query sqlQuery = hibSession.createSQLQuery( lSb.toString() );
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null)
				lHashReturn.put(lObj[0].toString(), lLngAmt);
		}
		
		return lHashReturn;
	  }
	
	
	public HashMap getAllSchemeLiabilityAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId) 
	{
		HashMap lHashReturn = new HashMap();

	    Session hibSession = getSession();
		glogger.info("hibSession is : " + hibSession.getClass());
		
		StringBuffer lStrQuery = new StringBuffer();
		
		/*lStrQuery.append("select concat(billhd.dmndNo,':',billhd.budMjrHd,':',billhd.budSubmjrHd,':', billhd.budMinHd,':', billhd.budSubHd), " + 
						" sum(r.billNetAmount) from TrnBillRegister r,RltLocationDepartment locdept, TrnBillBudheadDtls billhd, CmnLookupMst lkup, " + 
						" CmnLanguageMst lngmst where r.finYearId = :finYrId  and r.versionId = (select max(r1.versionId) from TrnBillRegister r1 " +
						" where r1.billCntrlNo = r.billCntrlNo) " +
						" and r.currBillStatus in (select m.statusCode from MstStatusLaibility m where m.liabilityId in ('45', '46')) " +
						" and r.ddoDeptId = locdept.locId " +
						" and r.billNo = billhd.billNo and billhd.budType = lkup.lookupId " +
						" and lkup.lookupShortName = :planNonPlan and lkup.cmnLanguageMst.langId = lngmst.langId " +
						" and lngmst.langShortName = :langId and locdept.departmentId = :deptId " +
						" group by billhd.dmndNo, billhd.budMjrHd, billhd.budSubmjrHd, billhd.budMinHd, billhd.budSubHd ");*/ 
		
		if(lStrPlanType.equalsIgnoreCase("PL"))
			lStrPlanType = "Plan";
		else if(lStrPlanType.equalsIgnoreCase("NP"))
			lStrPlanType = "Non-Plan";
	    
		lStrQuery.append("select concat(billhd.dmnd_No,':',billhd.bud_Mjr_Hd,':',billhd.bud_Submjr_Hd,':', billhd.bud_Min_Hd,':', billhd.bud_Sub_Hd), ");
		lStrQuery.append("sum(r.bill_gross_amount) from Trn_Bill_Register r,Rlt_Location_Department locdept, ");
		lStrQuery.append("Trn_Bill_Budhead_Dtls billhd, Cmn_Lookup_Mst lkup,  Cmn_Language_Mst lngmst ");
		lStrQuery.append("where r.fin_Year_Id = "+Long.parseLong(lStrFinYrId)+"  and ");//r.version_Id = (select max(r1.version_Id) from Trn_Bill_Register r1 ");
		//lStrQuery.append("where r1.bill_Cntrl_No = r.bill_Cntrl_No)  and ");
		lStrQuery.append("r.curr_Bill_Status in ");
		lStrQuery.append("(select m.status_Code from Mst_Status_Laibility m where m.liability_Id in ('45', '46'))  and ");
		lStrQuery.append("r.ddo_Dept_Id = locdept.loc_Id  and r.bill_No = billhd.bill_No and ");
		//lStrQuery.append("billhd.version_id = ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where  billhddtls.bill_no = billhd.bill_No) and ");
		lStrQuery.append("billhd.bud_Type = lkup.lookup_Id  and lkup.lookup_Short_Name = '"+lStrPlanType+"' and ");
		lStrQuery.append("lkup.lang_Id = lngmst.lang_Id  and lngmst.lang_Short_Name = '"+lStrLangId+"' and ");
		lStrQuery.append("locdept.department_Id = '"+lStrDeptId+"'  group by billhd.dmnd_No, billhd.bud_Mjr_Hd, ");
		lStrQuery.append("billhd.bud_Submjr_Hd, billhd.bud_Min_Hd, billhd.bud_Sub_Hd ");
		
	    glogger.info("Query for getAllSchemeBEAmt is : " + lStrQuery.toString());
	      
	    /*Query sqlQuery = hibSession.createQuery( lStrQuery.toString() );
		
		sqlQuery.setParameter("finYrId", lStrFinYrId);
		sqlQuery.setParameter("planNonPlan", lStrPlanType);
		sqlQuery.setParameter("deptId", lStrDeptId);
		sqlQuery.setParameter("langId", lStrLangId);*/
	    
	    Query sqlQuery = hibSession.createSQLQuery( lStrQuery.toString() );
		Iterator iterator = sqlQuery.list().iterator();
		Object lObj[];
		double lLngAmt=0;
		
		while (iterator.hasNext()) {
			lObj = (Object[]) iterator.next();
			
			lLngAmt = (lObj[1] != null) ? (double) Double.parseDouble(lObj[1].toString()) : 0;
			
			if (lObj != null)
				lHashReturn.put(lObj[0].toString(), lLngAmt);
		}
			    		
	    return lHashReturn;
	}
	
	//   Added by Keyur for DeptWise PlanNonPlan Report
	
	public Map getAllDeptPLNPCSSBudAmt(String lStrFinYrId, String lStrLangId)
	{
	    HashMap<String,Long> lHashData = new HashMap<String,Long>();
	    Session hibSession = getSession();
	    
	    StringBuffer lStrQuery = new StringBuffer();
	    
	    lStrQuery.append(" select b.deptId, sum(f.nxtYrBe * 1000) from SgvaExpestFinalamt f, SgvaBudbpnMapping b" +
	            " where f.finYrId = :finYrId and f.levelId = 4 and f.budbpnCode = b.bpncode and f.giaFlg = 'N'" + 
	            " and (f.recFlag not in ('R','C','M') or f.recFlag is null)" +
	            " and f.formType  <> 'RE' and f.planNonplan = 'PL'" +
	            " and b.langId = :langId group by b.deptId");
	    
	    glogger.debug("Query for getAllDeptPLNPCSSBudAmt -> PL is : " + lStrQuery.toString());
	    
	    Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
	    
	    sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
	    sqlQuery.setParameter("langId", lStrLangId);
	        
	    Iterator iterator = sqlQuery.list().iterator();
	    Object lObj[];
	    
	    while (iterator.hasNext()) {
	        lObj = (Object[]) iterator.next();
	        if (lObj != null){
	            lHashData.put(lObj[0].toString() + "_PL" ,(long) Double.parseDouble(lObj[1].toString()));
	        }
	    }
	    
	    lStrQuery.delete(0, lStrQuery.length());
	    
	    lStrQuery.append(" select bpn.deptId, (sum(css.id.cssAmount) * 1000)" +
	            " from SgvaBudcssMst css, SgvaBudbpnMapping bpn" + 
	            " where bpn.bpncode = css.id.budbpnCode and bpn.langId = :langId" +
	            " and css.id.finYrId = :finYrId group by bpn.deptId");
	    
	    glogger.debug("Query for getAllDeptPLNPCSSBudAmt -> CSS is : " + lStrQuery.toString());
	    
	    sqlQuery = hibSession.createQuery( lStrQuery.toString());
	    
	    sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
	    sqlQuery.setParameter("langId", lStrLangId);
	        
	    iterator = sqlQuery.list().iterator();
	    
	    while (iterator.hasNext()) {
	        lObj = (Object[]) iterator.next();
	        if (lObj != null){
	            lHashData.put(lObj[0].toString() + "_CSS" ,(long) Double.parseDouble(lObj[1].toString()));
	        }
	    }
	    
	    lStrQuery.delete(0, lStrQuery.length());
	    
	    lStrQuery.append(" select b.deptId, sum(f.nxtYrBe * 1000) from SgvaExpestFinalamt f, SgvaBudbpnMapping b" +
	            " where f.finYrId = :finYrId and f.levelId = 4 and f.budbpnCode = b.bpncode and f.giaFlg = 'N'" + 
	            " and (f.recFlag not in ('R','C','M') or f.recFlag is null)" +
	            " and f.formType  <> 'RE' and f.planNonplan = 'NP'" +
	            " and b.langId = :langId group by b.deptId");
	    
	    glogger.debug("Query for getAllDeptPLNPCSSBudAmt -> NP is : " + lStrQuery.toString());
	    
	    sqlQuery = hibSession.createQuery( lStrQuery.toString());
	    
	    sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
	    sqlQuery.setParameter("langId", lStrLangId);
	        
	    iterator = sqlQuery.list().iterator();
	    
	    long lLngCSSAmt = 0;
	    
	    while (iterator.hasNext()) {
	        lObj = (Object[]) iterator.next();
	        if (lObj != null){
	            
	            lLngCSSAmt = (lHashData.get(lObj[0].toString() + "_CSS") != null) ? 
	                    (long)Double.parseDouble(lHashData.get(lObj[0].toString() + "_CSS").toString())  : 0;
	                    
	            lHashData.put(lObj[0].toString() + "_NP" ,(long) Double.parseDouble(lObj[1].toString()) - lLngCSSAmt);
	        }
	    }
	    return lHashData;
	}

	public Map getAllDeptPLNPCSSCurrExpAmt(Date lDtStartDate, Date lDtEndDate,String lStrLangId)
	{
	    HashMap<String,Long> lHashData = new HashMap<String,Long>();
	    
	    Session hibSession = getSession();
	    
	    StringBuffer lStrQuery = new StringBuffer();
	    
	    lStrQuery.append(" select bpn.deptId, (sum(p.id.amount)) from Payment p," +
	            " SgvaBuddemandMst  dmd, SgvaBudbpnMapping bpn " + 
	            " where cast(dmd.demandCode as int) = p.id.demand and dmd.bpncode not in ('TT') and" +
	            " dmd.langId = :langId and p.id.rcptdate >= :startDate and p.id.rcptdate <= :endDate" +
	            " and dmd.bpncode = bpn.bpncode and bpn.langId = :langId" +
	            " and p.id.budget in (6,7,8,9) group by bpn.deptId");
	    
	    glogger.info("Query for getAllDeptPLNPCSSBudAmt -> PL is : " + lStrQuery.toString());
	    
	    Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
	    
	    glogger.info("lDtStartDate is : " + lDtStartDate+ " and lDtEndDate is :" + lDtEndDate);
	    
	    sqlQuery.setParameter("startDate", lDtStartDate);
	    sqlQuery.setParameter("endDate", lDtEndDate);
	    sqlQuery.setParameter("langId", lStrLangId);
	        
	    Iterator iterator = sqlQuery.list().iterator();
	    Object lObj[];
	    
	    while (iterator.hasNext()) {
	        lObj = (Object[]) iterator.next();
	        if (lObj != null){
	            lHashData.put(lObj[0].toString() + "_PL" ,(long) Double.parseDouble(lObj[1].toString()));
	        }
	    }
	    
	    lStrQuery.delete(0, lStrQuery.length());
	    
	    lStrQuery.append(" select bpn.deptId, (sum(p.id.amount)) from Payment p," +
	            " SgvaBuddemandMst  dmd, SgvaBudbpnMapping bpn " + 
	            " where cast(dmd.demandCode as int) = p.id.demand and dmd.bpncode not in ('TT') and" +
	            " dmd.langId = :langId and p.id.rcptdate >= :startDate and p.id.rcptdate <= :endDate" +
	            " and dmd.bpncode = bpn.bpncode and bpn.langId = :langId" +
	            " and p.id.budget in (1,5) group by bpn.deptId");
	    
	    glogger.info("Query for getAllDeptPLNPCSSBudAmt -> NP is : " + lStrQuery.toString());
	    
	    sqlQuery = hibSession.createQuery( lStrQuery.toString());
	    
	    sqlQuery.setParameter("startDate", lDtStartDate);
	    sqlQuery.setParameter("endDate", lDtEndDate);
	    sqlQuery.setParameter("langId", lStrLangId);
	        
	    iterator = sqlQuery.list().iterator();
	    while (iterator.hasNext()) {
	        lObj = (Object[]) iterator.next();
	        if (lObj != null){
	            lHashData.put(lObj[0].toString() + "_NP" ,(long) Double.parseDouble(lObj[1].toString()));
	        }
	    }
	    
	    lStrQuery.delete(0, lStrQuery.length());
	    
	    lStrQuery.append(" select bpn.deptId, (sum(p.id.amount)) from Payment p," +
	            " SgvaBuddemandMst  dmd, SgvaBudbpnMapping bpn " + 
	            " where cast(dmd.demandCode as int) = p.id.demand and dmd.bpncode not in ('TT') and" +
	            " dmd.langId = :langId and p.id.rcptdate >= :startDate and p.id.rcptdate <= :endDate" +
	            " and dmd.bpncode = bpn.bpncode and bpn.langId = :langId" +
	            " and p.id.budget in (2,3,4) group by bpn.deptId");
	    
	    glogger.info("Query for getAllDeptPLNPCSSBudAmt -> CSS is : " + lStrQuery.toString());
	    
	    sqlQuery = hibSession.createQuery( lStrQuery.toString());
	    
	    sqlQuery.setParameter("startDate", lDtStartDate);
	    sqlQuery.setParameter("endDate", lDtEndDate);
	    sqlQuery.setParameter("langId", lStrLangId);
	        
	    iterator = sqlQuery.list().iterator();
	    while (iterator.hasNext()) {
	        lObj = (Object[]) iterator.next();
	
	        if (lObj != null){
	            lHashData.put(lObj[0].toString() + "_CSS" ,(long) Double.parseDouble(lObj[1].toString()));
	        }
	    }
	    return lHashData;
	}

	public long getTotalREAmt(String lStrFinYrId, String lStrDeptId)
	{
	    long lLngTotalAmt=0;
	    
	    Session hibSession = getSession();
	    
	    StringBuffer lStrQuery = new StringBuffer();
	    
	    lStrQuery.append(" select max(curtYrRe * 1000) from SgvaExpestFinalamt" +
	            " where finYrId = :finYrId and deptId = :deptID and levelId = 4 and" + 
	            " giaFlg = 'N' and (recFlag not in ('R', 'C', 'M') or recFlag is null)" +
	            " group by finYrId, budbpnCode, buddemandCode, budmjrhdCode," +
	            " budsubmjrhdCode, budminhdCode, budsubhdCode, buddtlhdCode, budobjhdCode" +
	            " having max(curtYrRe) > 0");
	    
	    glogger.info("Query for getTotalREAmt is : " + lStrQuery.toString());
	    
	    Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
	    
	    sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
	    sqlQuery.setParameter("deptID", lStrDeptId);
	        
	    Iterator iterator = sqlQuery.list().iterator();
	    Object lObj = null;
	    
	    while (iterator.hasNext()) {
	        lObj = (Object) iterator.next();
	        
	        glogger.debug("lObj is : " + lObj + " for dept :" + lStrDeptId);
	        
	        if (lObj != null){
	            lLngTotalAmt = lLngTotalAmt + ((BigDecimal)lObj).longValue();
	        }
	    }
	
	    return lLngTotalAmt; 
	}
	
	public int getMaxMonthForGrnt(String lStrFinYrId)
    {
        int lIntMaxMnth = 0;
        
        String lStrMnthId = "";
        
        Session hibSession = getSession();
        
        StringBuffer lStrQuery = new StringBuffer();
        
        lStrQuery.append(" select max(md.monthId) from SgvaBudrlsMonthMpg  mm," +
                " SgvaBudrlsOrderHdr  hdr, SgvaBudrlsMonthDtls md" + 
                " where mm.fkHdrId = hdr.rlsOrderHdrId and" +
                " mm.monthMpgId = md.monthMpgId and hdr.finYearId = :finYrId and" +
                " hdr.planNonplan = 'NP' and hdr.applicationId = 'BUDRLS' and" +
                " mm.approvedRejected in ('a', 'A')");
        
        glogger.info("Query for getMaxMonthForGrnt is : " + lStrQuery.toString());
        
        Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
        
        sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
            
        Iterator iterator = sqlQuery.list().iterator();
        Object lObj = null;
        
        while (iterator.hasNext()) {
            lObj = (Object) iterator.next();
            if (lObj != null){
                lStrMnthId = (String)lObj;
            }
        }
        
        if(lStrMnthId != null && lStrMnthId.trim().length()>0)
        {
            glogger.info("SubStr Month is :" + lStrMnthId.trim().substring(3,lStrMnthId.trim().length()) + ":");
            lIntMaxMnth = Integer.parseInt(lStrMnthId.trim().substring(3,lStrMnthId.trim().length()));
        }
        return lIntMaxMnth;
    }

	public Map getAllDeptPLNPCSSGrantAmt(String lStrFinYrId, String lStrLangId)
    {
        HashMap<String,Long> lHashData = new HashMap<String,Long>();
        Object lObj[] = null;
        Query sqlQuery = null;
        Iterator iterator = null;
        Session hibSession = getSession();
        
        StringBuffer lStrQuery = new StringBuffer();
        
        lStrQuery.append(" SELECT DEP.id.deptId, (SELECT (SUM(AMT.proposedAmt) * 1000) " +
                        " FROM SgvaBudrlsOrderHdr HDR, SgvaBudrlsMonthMpg MM, " +
                        " SgvaBudrlsOrderDtls DTL, SgvaBudrlsUsrAmt AMT, SgvaBudrlsDivisionMpg DIV1 " + 
                        " WHERE HDR.rlsOrderHdrId = MM.fkHdrId AND MM.monthMpgId = DTL.fkMonthMpgId AND " +
                        " AMT.fkDtlsId = DTL.rlsOrderDtlsId AND AMT.approvedRejected IN ('a', 'A') AND " +
                        " MM.grntRlsFlag IN ('y', 'Y') AND HDR.finYearId = :finYearId AND DTL.divisionId = DIV1.divisionId AND " +
                        " DIV1.deptId = DEP.id.deptId AND HDR.planNonplan = 'PL') FROM SgvoDeptMst DEP WHERE DEP.id.langId = :langId AND DEP.deptType = 'SEC' "); 
        
        glogger.info("Query for getAllDeptPLNPCSSGrantAmt -> PL is : " + lStrQuery.toString());
        
        sqlQuery = hibSession.createQuery( lStrQuery.toString());
        
        sqlQuery.setParameter("finYearId", Long.parseLong(lStrFinYrId));
        sqlQuery.setParameter("langId", lStrLangId);
           
        iterator = sqlQuery.list().iterator();
              
        while (iterator.hasNext()) {
            lObj = (Object[]) iterator.next();
            if (lObj[0] != null && lObj[1] != null){
                lHashData.put(lObj[0].toString() + "_PL" , (long) Double.parseDouble(lObj[1].toString()));
            }
        }
        
        lStrQuery.delete(0, lStrQuery.length());
       
        lStrQuery.append(" SELECT DEP.id.deptId, (SELECT (SUM(AMT.proposedAmt) * 1000) " +
                        " FROM SgvaBudrlsOrderHdr HDR, SgvaBudrlsMonthMpg MM, " +
                        " SgvaBudrlsOrderDtls DTL, SgvaBudrlsUsrAmt AMT, SgvaBudrlsDivisionMpg DIV1 " + 
                        " WHERE HDR.rlsOrderHdrId = MM.fkHdrId AND MM.monthMpgId = DTL.fkMonthMpgId AND " +
                        " AMT.fkDtlsId = DTL.rlsOrderDtlsId AND AMT.approvedRejected IN ('a', 'A') AND " +
                        " MM.grntRlsFlag IN ('y', 'Y') AND HDR.finYearId = :finYearId AND DTL.divisionId = DIV1.divisionId AND " +
                        " DIV1.deptId = DEP.id.deptId AND HDR.planNonplan = 'NP') FROM SgvoDeptMst DEP WHERE DEP.id.langId = :langId AND DEP.deptType = 'SEC' "); 

        glogger.info("Query for getAllDeptPLNPCSSGrantAmt -> PL is : " + lStrQuery.toString());
        
        sqlQuery = hibSession.createQuery( lStrQuery.toString());
        
        sqlQuery.setParameter("finYearId", Long.parseLong(lStrFinYrId));
        sqlQuery.setParameter("langId", lStrLangId);
           
        iterator = sqlQuery.list().iterator();
                        
        while (iterator.hasNext()) {
            lObj = (Object[]) iterator.next();
            if (lObj[0] != null && lObj[1] != null){
                lHashData.put(lObj[0].toString() + "_NP" , (long) Double.parseDouble(lObj[1].toString()));
            }
        }
                
        lStrQuery.delete(0, lStrQuery.length());
        
        lStrQuery.append(" SELECT DEP.id.deptId, (SELECT (SUM(AMT.proposedAmt) * 1000) " +
                        " FROM SgvaBudrlsOrderHdr HDR, SgvaBudrlsMonthMpg MM, " +
                        " SgvaBudrlsOrderDtls DTL, SgvaBudrlsUsrAmt AMT, SgvaBudrlsDivisionMpg DIV1 " + 
                        " WHERE HDR.rlsOrderHdrId = MM.fkHdrId AND MM.monthMpgId = DTL.fkMonthMpgId AND " +
                        " AMT.fkDtlsId = DTL.rlsOrderDtlsId AND AMT.approvedRejected IN ('a', 'A') AND " +
                        " MM.grntRlsFlag IN ('y', 'Y') AND HDR.finYearId = :finYearId AND DTL.divisionId = DIV1.divisionId AND AMT.grantType = 'CSS' AND " +
                        " DIV1.deptId = DEP.id.deptId AND HDR.planNonplan = 'NP') FROM SgvoDeptMst DEP WHERE DEP.id.langId = :langId AND DEP.deptType = 'SEC' "); 
    
            glogger.info("Query for getAllDeptPLNPCSSGrantAmt -> PL is : " + lStrQuery.toString());
            
            sqlQuery = hibSession.createQuery( lStrQuery.toString());
            
            sqlQuery.setParameter("finYearId", Long.parseLong(lStrFinYrId));
            sqlQuery.setParameter("langId", lStrLangId);
               
            iterator = sqlQuery.list().iterator();
                        
            while (iterator.hasNext()) {
                lObj = (Object[]) iterator.next();
                if (lObj[0] != null && lObj[1] != null){
                    lHashData.put(lObj[0].toString() + "_CSS" , (long) Double.parseDouble(lObj[1].toString()));
                }
            }
        
       return lHashData;
    }
    
	public Map getAllDeptPLNPCSSActualExpAmt(String lStrFinYrId, Date lDtStartDate, Date lDtEndDate, String lStrLangId)
    {
        HashMap<String,Long> lHashData = new HashMap<String,Long>();
        
        Session hibSession = getSession();
        
        StringBuffer lStrQuery = new StringBuffer();
               
        lStrQuery.append(" SELECT locdept.departmentId, sum(r.billGrossAmount) FROM  " + 
                        " TrnBillRegister r, RltLocationDepartment locdept , TrnBillBudheadDtls billhd,  " + 
                        " CmnLookupMst lkup, CmnLanguageMst lngmst " + 
                        " WHERE r.finYearId = :finYrId AND  r.ddoDeptId = locdept.locId and " + 
                        " r.chequeStatus IS NOT NULL AND  " + 
                        " r.billNo = billhd.billNo AND r.chequeDispDt IS NOT NULL AND r.chequeDispDt >= :startDate " +
                        " AND r.chequeDispDt <= :endDate AND billhd.budType = lkup.lookupId AND  " + 
                        " lkup.lookupShortName = 'PL' AND lkup.cmnLanguageMst.langId = lngmst.langId AND  " + 
                        " lngmst.langShortName = :langId " + 
                        " GROUP BY locdept.departmentId ");
                
        glogger.info("Query for getAllDeptPLNPCSSActualExpAmt -> PL is : " + lStrQuery.toString());
        
        Query sqlQuery = hibSession.createQuery( lStrQuery.toString());
        
        glogger.info("lDtStartDate is : " + lDtStartDate+ " and lDtEndDate is :" + lDtEndDate);
        
        sqlQuery.setParameter("finYrId", lStrFinYrId);
        sqlQuery.setParameter("startDate", lDtStartDate);
        sqlQuery.setParameter("endDate", lDtEndDate);
        sqlQuery.setParameter("langId", lStrLangId);
            
        Iterator iterator = sqlQuery.list().iterator();
        Object lObj[];
        
        while (iterator.hasNext()) {
            lObj = (Object[]) iterator.next();
            if (lObj != null){
                lHashData.put(lObj[0].toString() + "_PL" ,(long) Double.parseDouble(lObj[1].toString()));
            }
        }
        
        lStrQuery.delete(0, lStrQuery.length());
        
        lStrQuery.append(" SELECT locdept.departmentId, sum(r.billGrossAmount) FROM  " + 
                " TrnBillRegister r, RltLocationDepartment locdept , TrnBillBudheadDtls billhd,  " + 
                " CmnLookupMst lkup, CmnLanguageMst lngmst " + 
                " WHERE r.finYearId = :finYrId AND  " + 
                " r.ddoDeptId = locdept.locId AND r.chequeStatus IS NOT NULL AND  " + 
                " r.billNo = billhd.billNo AND r.chequeDispDt IS NOT NULL AND r.chequeDispDt >= :startDate " +
                " AND r.chequeDispDt <= :endDate AND billhd.budType = lkup.lookupId AND  " + 
                " lkup.lookupShortName = 'NP' AND lkup.cmnLanguageMst.langId = lngmst.langId AND  " + 
                " lngmst.langShortName = :langId " + 
                " GROUP BY locdept.departmentId ");
        
        glogger.info("Query for getAllDeptPLNPCSSActualExpAmt -> NP is : " + lStrQuery.toString());
        
        sqlQuery.setParameter("finYrId", lStrFinYrId);
        sqlQuery.setParameter("startDate", lDtStartDate);
        sqlQuery.setParameter("endDate", lDtEndDate);
        sqlQuery.setParameter("langId", lStrLangId);
            
        iterator = sqlQuery.list().iterator();
        while (iterator.hasNext()) {
            lObj = (Object[]) iterator.next();
            if (lObj != null){
                lHashData.put(lObj[0].toString() + "_NP" ,(long) Double.parseDouble(lObj[1].toString()));
            }
        }
        
        return lHashData;
    }
	
	public HashMap getFundMjrRange(int lintFundType)
	{
		HashMap lhshFundMrj = new HashMap();
		StringBuffer lSb=new StringBuffer();
		Statement lStmt=null;
		Connection lCon=null;
		ResultSet rs=null;
		
		lSb.append("select fund_name,major_hd ,fund_type from config_recptexp_fund_mjrhd c"+
		" where c.fund_type='"+ lintFundType + "'"); 
		
		glogger.info("Query for getFundMjrRange() "+ lSb.toString());
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lSb.toString());
			if(rs.next())
			{
				lhshFundMrj.put("FundName", rs.getString("fund_name"));
				lhshFundMrj.put("FundId", rs.getInt("fund_type"));
				lhshFundMrj.put("MjrHdRange",rs.getString("major_hd"));
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				glogger.error("Exception in closing connection in getFundMjrRange() "+e);
			}
		}
		return lhshFundMrj;
	}
	
	public ArrayList getSubFundMjrRange(int lintFundType)
	{
		ArrayList lSubFundLst = new ArrayList();
		HashMap lhshFundMrj = null;
		Statement lStmt=null;
		Connection lCon=null;
		ResultSet rs=null;
		StringBuffer lSb=new StringBuffer();
		
		lSb.append("select fund_name,major_hd ,fund_type from config_recptexp_fund_mjrhd c"+
		" where c.parent_fund_type='"+ lintFundType + "'"); 
		
		glogger.info("Query for getSubFundMjrRange() -- "+lSb.toString());
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lSb.toString());
			while(rs.next())
			{
				lhshFundMrj = new HashMap();
				lhshFundMrj.put("FundName", rs.getString("fund_name"));
				lhshFundMrj.put("FundId", rs.getInt("fund_type"));
				lhshFundMrj.put("MjrHdRange", rs.getString("major_hd"));
				lSubFundLst.add(lhshFundMrj);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				glogger.error("Exception in closing connection in getSubFundMjrRange() "+e);
			}
		}
		return lSubFundLst;
	}
	
	public double getCSSAmount(ArrayList lArrMjrHdRange,int liFinYrId)
	{
		glogger.info("-- Inside getCSSAmount() -- ");
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		double lLngCSSAmnt=0;
		StringBuffer lsb=new StringBuffer();
		
		if(lArrMjrHdRange.size()!=2)
		{
			lsb.append(" select sum(css.css_amount)/10000 CSSAMOUNT from sgva_css_mst css ");
			lsb.append(" where css.budmjrhd_code='"+lArrMjrHdRange.get(0)+"' and css.fin_yr_id="+liFinYrId );
		}
		else
		{
			lsb.append(" select sum(css.css_amount)/10000 CSSAMOUNT from sgva_css_mst css ");
			lsb.append(" where css.budmjrhd_code between '"+lArrMjrHdRange.get(0)+"' and '"+lArrMjrHdRange.get(1)+"' and css.fin_yr_id="+liFinYrId );
		}
		glogger.info("Query to get CSS Amount -- "+lsb.toString());
		
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			if(rs.next())
			{
				lLngCSSAmnt=rs.getDouble("CSSAMOUNT");
			}
			glogger.info("CSS Amount == "+lLngCSSAmnt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return lLngCSSAmnt;
	}

	public HashMap getConsolidatedExpBudAmt(ArrayList lArrLstMjrHd ,int lFinYrId )
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();	
		System.out.println("--------------Inside  getConsolidatedExpBudAmt------------>");
	
		double douRevExptotal_PL_CH=0;
		double douRevExptotal_PL_VT=0;
		double douRevExptotal_NP_CH=0;
		double douRevExptotal_NP_VT=0;
		
		StringBuffer buffer=new StringBuffer();
		Statement lStmt = null;
		ResultSet rs = null;
		
		buffer.append("select sum(EXP.nxt_yr_be)/10000 amt from sgva_expest_finalamt EXP"+
						 " where ");
		if(lArrLstMjrHd.size()==2)
			buffer.append(" budmjrhd_code between '"+lArrLstMjrHd.get(0)+"' and '" +lArrLstMjrHd.get(1)+"' ");
		else
			buffer.append(" budmjrhd_code = '"+lArrLstMjrHd.get(0)+"' ");
		buffer.append(" and plan_nonplan='PL' and charg_vote='CH' and fin_yr_id="+lFinYrId);
		buffer.append(" AND EXP.GIA_FLG = 'N' AND EXP.FORM_TYPE != 'RE' AND ");
		buffer.append(" (EXP.REC_FLAG NOT IN ('R', 'C', 'M') OR EXP.REC_FLAG IS NULL) AND EXP.LEVEL_ID = 4");	
		
		glogger.info("Query for Consolidated_PL_CH -- "+buffer.toString());
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				douRevExptotal_PL_CH=rs.getDouble("amt");
			}
			glogger.info("getConsolidatedExpBudAmt_PL_Charged == "+douRevExptotal_PL_CH);
			lHashData.put("ConsExp_PL_CH", douRevExptotal_PL_CH);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
					
		buffer = new StringBuffer();
		buffer.append("select sum(EXP.nxt_yr_be)/10000 amt from sgva_expest_finalamt EXP"+
					 " where ");
		if(lArrLstMjrHd.size()==2)
			buffer.append(" budmjrhd_code between '"+lArrLstMjrHd.get(0)+"' and '" +lArrLstMjrHd.get(1)+"' ");	
		else	
			buffer.append(" budmjrhd_code = '"+lArrLstMjrHd.get(0)+"' ");
		buffer.append(" and plan_nonplan='PL' and charg_vote='VT' and fin_yr_id="+lFinYrId);
		buffer.append(" AND EXP.GIA_FLG = 'N' AND EXP.FORM_TYPE != 'RE' AND ");
		buffer.append(" (EXP.REC_FLAG NOT IN ('R', 'C', 'M') OR EXP.REC_FLAG IS NULL) AND EXP.LEVEL_ID = 4");	
		
		glogger.info("Query for Consolidated_PL_VT -- "+buffer.toString());
		
		try
		{
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				douRevExptotal_PL_VT=rs.getDouble("amt");
			}
			glogger.info("getConsolidatedExpBudAmt_PL_VT == "+douRevExptotal_PL_VT);
			lHashData.put("ConsExp_PL_VT", douRevExptotal_PL_VT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
		buffer = new StringBuffer();
		buffer.append("select sum(EXP.nxt_yr_be)/10000 amt from sgva_expest_finalamt EXP"+
				" where ");
		if(lArrLstMjrHd.size()==2)
			buffer.append(" budmjrhd_code between '"+lArrLstMjrHd.get(0)+"' and '" +lArrLstMjrHd.get(1)+"' ");
			
		else
		   buffer.append(" budmjrhd_code = '"+lArrLstMjrHd.get(0)+"' ");
		buffer.append(" and plan_nonplan='NP' and charg_vote='CH' and fin_yr_id="+lFinYrId);   
		buffer.append(" AND EXP.GIA_FLG = 'N' AND EXP.FORM_TYPE != 'RE' AND ");
		buffer.append(" (EXP.REC_FLAG NOT IN ('R', 'C', 'M') OR EXP.REC_FLAG IS NULL) AND EXP.LEVEL_ID = 4");	
		
		glogger.info("Query for Consolidated_NP_CH -- "+buffer.toString());
		
	   try
		{
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				douRevExptotal_NP_CH=rs.getDouble("amt");
			}
			glogger.info("getConsolidatedExpBudAmt_NP_CH == "+douRevExptotal_NP_CH);
			lHashData.put("ConsExp_NP_CH", douRevExptotal_NP_CH);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		   
		   
		 buffer = new StringBuffer();
		 buffer.append("select sum(EXP.nxt_yr_be)/10000 amt from sgva_expest_finalamt EXP "+
			" where ");
		 if(lArrLstMjrHd.size()==2)
			 buffer.append(" budmjrhd_code between '"+lArrLstMjrHd.get(0)+"' and '" +lArrLstMjrHd.get(1)+"' ");
		 else
			 buffer.append(" budmjrhd_code = '"+lArrLstMjrHd.get(0)+"' ");
		 buffer.append(" and plan_nonplan='NP' and charg_vote='VT' and fin_yr_id="+lFinYrId);
		 buffer.append(" AND EXP.GIA_FLG = 'N' AND EXP.FORM_TYPE != 'RE' AND ");
		 buffer.append(" (EXP.REC_FLAG NOT IN ('R', 'C', 'M') OR EXP.REC_FLAG IS NULL) AND EXP.LEVEL_ID = 4");	
		 
		 glogger.info("Query for Consolidated_NP_VT -- "+buffer.toString());
		 try
		 {
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				douRevExptotal_NP_VT=rs.getDouble("amt");
			}
			glogger.info("getConsolidatedExpBudAmt_NP_VT== "+douRevExptotal_NP_VT);
			lHashData.put("ConsExp_NP_VT", douRevExptotal_NP_VT);
		 }
		 catch(Exception e)
		 {
			e.printStackTrace();
		 }
		 finally
		 {
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		 }
		 return lHashData;
	}

	public HashMap getConsolidatedRcpBudAmt(ArrayList lArrMjrHd,int lFinYrId)
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Statement lStmt = null;
		ResultSet rs = null;
		glogger.info("--------------Inside getConsolidatedRcpAmt------------");
		
		double lngRevenueExptotal=0;
		
		StringBuffer buffer=new StringBuffer();
		
		buffer.append("select sum(EXP.nxt_yr_be_rcpt)/10000 amt from sgva_rcptest_finalamt EXP"+
						 " where ");
		if(lArrMjrHd.size()==2)
			buffer.append(" budmjrhd_code between '"+lArrMjrHd.get(0)+"' and '" +lArrMjrHd.get(1)+"' ");
		else
			buffer.append(" budmjrhd_code = '"+lArrMjrHd.get(0)+"' ");	
		buffer.append(" and fin_yr_id="+lFinYrId);
		buffer.append(" AND LEVEL_ID = 4");
		
		glogger.info("Query for ConsolidatedRcpBud == "+buffer.toString());
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				lngRevenueExptotal=rs.getDouble("amt");
			}
			glogger.info("ConsolidatedReceiptTotal Amount == "+lngRevenueExptotal);
			lHashData.put("ConsolidatedReceiptTotal", lngRevenueExptotal);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return lHashData;
	}
	
	public HashMap getPublicContingencyFund(ArrayList larrMjrHdRange,long lFinYrId,String LangId)
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Statement lStmt = null;
		ResultSet rs = null;
		glogger.info("--------------Inside getPublicContingencyFund------------");
		
		double lngContFundtotal=0;
		StringBuffer buffer=new StringBuffer();
			
		buffer.append("select sum(nxt_yr_be_disb)/10000-sum(nxt_yr_be_rcpt)/10000 amt FROM sgva_rcptest_finalamt"+
						 " where ");
		if(larrMjrHdRange.size()==2)
		{
			buffer.append(" budmjrhd_code between '"+larrMjrHdRange.get(0)+"' and '" +larrMjrHdRange.get(1)+"' ");
		}
		else
		{
			buffer.append(" budmjrhd_code = '"+larrMjrHdRange.get(0)+"' ");
		}		
		buffer.append(" and fin_yr_id="+lFinYrId+" and lang_id= '"+LangId+"'");
		buffer.append(" AND LEVEL_ID = 4");
		
		glogger.info("Query for getPublicContingencyFund == "+buffer.toString());
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				lngContFundtotal=rs.getDouble("amt");
			}
			glogger.info("ContinGencyFundTotal Amount == "+lngContFundtotal);
			lHashData.put("Cont_Public_FundTotal", lngContFundtotal);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return lHashData;
	}
	
	
	public HashMap getGrantAmt(ArrayList larrMjrHdRange,long lFinYrId ,String LangId)
	{
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Statement lStmt = null;
		ResultSet rs = null;
		
		glogger.info("--------------Inside getGrantAmt------------>");
		double PL_Amount=0;
		double NP_Amount=0;
		double CSS_Amount=0;
		double GrantTotalAmt = 0;
		StringBuffer buffer=new StringBuffer();
	
		buffer.append("select sum(plan_amount)/10000 amt from sgva_budgrant_mst"+
					 " where ");
		if(larrMjrHdRange.size()==2)
		{
			buffer.append(" budmjrhd_code between '"+larrMjrHdRange.get(0)+"' and '" +larrMjrHdRange.get(1)+"' ");
		}
		else
		{
			buffer.append(" budmjrhd_code = '"+larrMjrHdRange.get(0)+"' ");	
		}
		buffer.append(" and fin_yr_id="+ lFinYrId +" and lang_id= '"+LangId+"'");
	
		glogger.info("Query for GrantAmt_PL -- "+buffer.toString());
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				PL_Amount=rs.getDouble("amt");
			}
			glogger.info("GrantTotal == "+PL_Amount);
			lHashData.put("GrantAmt_PL", PL_Amount);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
				
		buffer = new StringBuffer();
		buffer.append("select sum(nonplan_amount)/10000 amt from sgva_budgrant_mst"+
						" where ");
		if(larrMjrHdRange.size()==2)
			buffer.append(" budmjrhd_code between '"+larrMjrHdRange.get(0)+"' and '" +larrMjrHdRange.get(1)+"' ");
			
		else
			buffer.append(" budmjrhd_code = '"+larrMjrHdRange.get(0)+"' ");	
		buffer.append(" and fin_yr_id="+lFinYrId+" and lang_id='"+LangId+"'");
		
		glogger.info("Query for GrantAmt_NP -- "+buffer.toString());
		try
		{
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				NP_Amount=rs.getDouble("amt");
			}
			glogger.info("GrantTotal == "+NP_Amount);
			lHashData.put("GrantAmt_NP", NP_Amount);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
		buffer = new StringBuffer();
		buffer.append("select sum(css_amount)/10000 amt from sgva_budgrant_mst"+
						" where ");
			if(larrMjrHdRange.size()==2)
				buffer.append(" budmjrhd_code between '"+larrMjrHdRange.get(0)+"' and '" +larrMjrHdRange.get(1)+"' ");
			
		else
			buffer.append(" budmjrhd_code = '"+larrMjrHdRange.get(0)+"' ");
		buffer.append(" and fin_yr_id="+ lFinYrId +" and lang_id='"+LangId+"'");
		 
		glogger.info("Query for GrantAmt_CSS -- "+buffer.toString());
		
	    try
		{
				
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(buffer.toString());
			if(rs.next())
			{
				CSS_Amount=rs.getDouble("amt");
			}
			glogger.info("GrantTotal == "+CSS_Amount);
			lHashData.put("GrantAmt_CSS", CSS_Amount);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
		catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		GrantTotalAmt = PL_Amount+NP_Amount+CSS_Amount;
		glogger.info("Total Grant Amount == "+GrantTotalAmt);
		lHashData.put("GrantAmt_Total", GrantTotalAmt);
		return lHashData;
}		
	
	public int getFinancialYear(String lStrFromDate,String lStrToDate)
	{
		glogger.info("-- In getFinancialYear() -- ");
		int lintFinYear = 0;
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;

		StringBuffer lsb=new StringBuffer();
		
		if(!lStrFromDate.equals("") && !lStrToDate.equals(""))
		{
			lsb.append("SELECT fin_year_id FROM sgvc_fin_year_mst s where ");	
			lsb.append("s.from_date <= '"+lStrFromDate+"' and ");
			lsb.append("s.to_date >= '"+lStrToDate+"' ");
			lsb.append("and s.lang_id='en_US' ");
		}
		else if(!lStrFromDate.equals("") && lStrToDate.equals(""))
		{
			lsb.append("SELECT fin_year_id FROM sgvc_fin_year_mst s where ");
			lsb.append("s.from_date <= '"+lStrFromDate+"' and "); 
			lsb.append("s.to_date >= current_date and s.lang_id='en_US' ");
		}
		else if(lStrFromDate.equals("") && !lStrToDate.equals(""))
		{
			lsb.append("SELECT fin_year_id FROM sgvc_fin_year_mst s where ");
			lsb.append("s.from_date <= current_date and "); 
			lsb.append("s.to_date >= '"+lStrToDate+"' and s.lang_id='en_US' ");
		}
		else if(lStrFromDate.equals("") && lStrToDate.equals(""))
		{
			lsb.append("SELECT fin_year_id FROM sgvc_fin_year_mst s where ");
			lsb.append("s.from_date <= current_date and s.to_date >= current_date and s.lang_id='en_US' ");
		}
		
		glogger.info("Query to get getFinancialYear -- "+lsb.toString());
		
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			if(rs.next())
			{
				lintFinYear=rs.getInt("fin_year_id");
			}
			glogger.info("Financial Year == "+lintFinYear);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return lintFinYear;
	}
	
	public HashMap getCurrentExpAmt(ArrayList lArrMjrHdRange,String lStrFromDate,String lStrToDate)
	{
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		double lLngTotalExp=0;
		double lLngTotalCurrExp=0;
		StringBuffer lsb=new StringBuffer();
		HashMap hmp=new HashMap();
		try
		{	
			glogger.info("FromDate == "+lStrFromDate+" :: ToDate == "+lStrToDate);
			lCon=getSession().connection();
			
			lsb.append(" select  sum(r.bill_gross_amount)/10000000 totexp from ");
			lsb.append(" Trn_Bill_Register r,  Trn_Bill_Budhead_Dtls billhd, ");
			lsb.append(" Cmn_Lookup_Mst lkup, Cmn_Language_Mst lngmst where r.cheque_status is not null and ");
			lsb.append(" r.cheque_disp_dt is not null and r.cheque_disp_dt>=date_format('"+lStrFromDate+"','%Y-%m-%d') and ");
			lsb.append(" r.cheque_disp_dt<=date_format('"+lStrToDate+"','%Y-%m-%d') ");
			if(lArrMjrHdRange.size()==1)
			{
				lsb.append(" and r.bill_No = billhd.bill_No and r.budmjr_hd='"+lArrMjrHdRange.get(0)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}
			else
			{
				lsb.append(" and r.bill_No = billhd.bill_No and r.budmjr_hd between '"+lArrMjrHdRange.get(0)+"' and '"+lArrMjrHdRange.get(1)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}	
			//lsb.append(" Trn_Bill_Register r1 where r1.bill_Cntrl_No = r.bill_Cntrl_No) and billhd.version_id = ");
			//lsb.append(" ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where ");
			//lsb.append(" billhddtls.bill_no = billhd.bill_No) ");
			lsb.append(" and billhd.bud_Type = lkup.lookup_Id and ");
			lsb.append(" lkup.lookup_Short_Name = 'Plan' and lkup.lang_Id = lngmst.lang_Id and ");
			lsb.append(" lngmst.lang_Short_Name = 'en_US' ");
			
			glogger.info("Query for CurrentExp_PL -- "+lsb.toString());
			
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			while(rs.next())
			{
				lLngTotalExp=rs.getDouble("totexp");
			}
			hmp.put("CurrentExp_PL", lLngTotalExp);
			lLngTotalCurrExp = lLngTotalCurrExp + lLngTotalExp;
			glogger.info("CurrAmt_PL == "+lLngTotalExp);
			rs.close();
			lStmt.close();
				
				
			lsb=new StringBuffer();
			lsb.append(" select  sum(r.bill_gross_amount)/10000000 totexp from ");
			lsb.append(" Trn_Bill_Register r,  Trn_Bill_Budhead_Dtls billhd, ");
			lsb.append(" Cmn_Lookup_Mst lkup, Cmn_Language_Mst lngmst where r.cheque_status is not null and ");
			lsb.append(" r.cheque_disp_dt is not null and r.cheque_disp_dt>=date_format('"+lStrFromDate+"','%Y-%m-%d') and ");
			lsb.append(" r.cheque_disp_dt<=date_format('"+lStrToDate+"','%Y-%m-%d') ");
			if(lArrMjrHdRange.size()==1)
			{
				lsb.append(" and r.bill_No = billhd.bill_No and r.budmjr_hd='"+lArrMjrHdRange.get(0)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}
			else
			{
				lsb.append(" and r.bill_No = billhd.bill_No and r.budmjr_hd between '"+lArrMjrHdRange.get(0)+"' and '"+lArrMjrHdRange.get(1)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}
			//lsb.append(" Trn_Bill_Register r1 where r1.bill_Cntrl_No = r.bill_Cntrl_No) and billhd.version_id = ");
			//lsb.append(" ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where ");
			//lsb.append(" billhddtls.bill_no = billhd.bill_No) ");
			lsb.append(" and billhd.bud_Type = lkup.lookup_Id and ");
			lsb.append(" lkup.lookup_Short_Name = 'Non-plan' and lkup.lang_Id = lngmst.lang_Id and ");
			lsb.append(" lngmst.lang_Short_Name = 'en_US' ");
			
			glogger.info("Query for CurrentExp_NP -- "+lsb.toString());
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			while(rs.next())
			{
				lLngTotalExp=rs.getDouble("totexp");
			}
			hmp.put("CurrentExp_NP", lLngTotalExp);
			glogger.info("CurrAmt_NP == "+lLngTotalExp);
			
			lLngTotalCurrExp = lLngTotalCurrExp + lLngTotalExp;
			hmp.put("CurrentExp_Total", lLngTotalCurrExp);
			glogger.info("CurrAmt_Total == "+lLngTotalCurrExp);
			
			rs.close();
			lStmt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return hmp;
		
	}

	public HashMap getProgressiveExpAmt(ArrayList lArrMjrHdRange,int liFinYrId, String lToDate)
	{
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		double lLngTotalExp=0;
		double lLngTotalPrgExp=0;
		StringBuffer lsb=new StringBuffer();
		HashMap hmp=new HashMap();
		try
		{	
			lCon=getSession().connection();
			
			lsb.append(" select  sum(r.bill_gross_amount)/10000000 totexp from ");
			lsb.append(" Trn_Bill_Register r,  Trn_Bill_Budhead_Dtls billhd, ");
			lsb.append(" Cmn_Lookup_Mst lkup, Cmn_Language_Mst lngmst where r.cheque_status is not null and r.fin_Year_Id ="+liFinYrId);
			if(lArrMjrHdRange.size()==1)
			{
				lsb.append(" and r.cheque_disp_dt is not null and r.cheque_disp_dt<=date_format('"+lToDate+"','%Y-%m-%d') and r.bill_No = billhd.bill_No and r.budmjr_hd='"+lArrMjrHdRange.get(0)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}
			else
			{
				lsb.append(" and r.cheque_disp_dt is not null and r.cheque_disp_dt<=date_format('"+lToDate+"','%Y-%m-%d') and r.bill_No = billhd.bill_No and r.budmjr_hd between '"+lArrMjrHdRange.get(0)+"' and '"+lArrMjrHdRange.get(1)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}
			//lsb.append(" Trn_Bill_Register r1 where r1.bill_Cntrl_No = r.bill_Cntrl_No) and billhd.version_id = ");
			//lsb.append(" ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where ");
			//lsb.append(" billhddtls.bill_no = billhd.bill_No) ");
			lsb.append(" and billhd.bud_Type = lkup.lookup_Id and ");
			lsb.append(" lkup.lookup_Short_Name = 'Plan' and lkup.lang_Id = lngmst.lang_Id and ");
			lsb.append(" lngmst.lang_Short_Name = 'en_US' ");
			
			glogger.info("Query for ProgExp_PL -- "+lsb.toString());
			
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			while(rs.next())
			{
				lLngTotalExp=rs.getDouble("totexp");
			}
			hmp.put("ProgExp_PL", lLngTotalExp);
			glogger.info("ProgExp_PL == "+lLngTotalExp);
			lLngTotalPrgExp = lLngTotalPrgExp + lLngTotalExp;
			rs.close();
			lStmt.close();
			
			
			lsb=new StringBuffer();
			lsb.append(" select  sum(r.bill_gross_amount)/10000000 totexp from ");
			lsb.append(" Trn_Bill_Register r,  Trn_Bill_Budhead_Dtls billhd, ");
			lsb.append(" Cmn_Lookup_Mst lkup, Cmn_Language_Mst lngmst where r.cheque_status is not null and r.fin_Year_Id ="+liFinYrId);
			if(lArrMjrHdRange.size()==1)
			{
				lsb.append(" and r.cheque_disp_dt is not null and r.cheque_disp_dt<=date_format('"+lToDate+"','%Y-%m-%d') and r.bill_No = billhd.bill_No and r.budmjr_hd='"+lArrMjrHdRange.get(0)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}
			else
			{
				lsb.append(" and r.cheque_disp_dt is not null and r.cheque_disp_dt<=date_format('"+lToDate+"','%Y-%m-%d') and r.bill_No = billhd.bill_No and r.budmjr_hd between '"+lArrMjrHdRange.get(0)+"' and '"+lArrMjrHdRange.get(1)+"' ");//and r.version_Id = (select max(r1.version_Id) from ");
			}
			//lsb.append(" Trn_Bill_Register r1 where r1.bill_Cntrl_No = r.bill_Cntrl_No) and billhd.version_id = ");
			//lsb.append(" ( select max(billhddtls.version_Id) from trn_bill_budhead_dtls  billhddtls where ");
			//lsb.append(" billhddtls.bill_no = billhd.bill_No) ");
			lsb.append(" and billhd.bud_Type = lkup.lookup_Id and ");
			lsb.append(" lkup.lookup_Short_Name = 'Non-Plan' and lkup.lang_Id = lngmst.lang_Id and ");
			lsb.append(" lngmst.lang_Short_Name = 'en_US' ");
			
			glogger.info("Query for ProgExp_NP -- "+lsb.toString());
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			while(rs.next())
			{
				lLngTotalExp=rs.getDouble("totexp");
			}
			hmp.put("ProgExp_NP", lLngTotalExp);
			glogger.info("ProgExp_NP == "+lLngTotalExp);
			lLngTotalPrgExp = lLngTotalPrgExp + lLngTotalExp;
			
			hmp.put("ProgExp_Total", lLngTotalPrgExp);
			glogger.info("ProgExp_Total == "+lLngTotalPrgExp);
			rs.close();
			lStmt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return hmp;
	}
	
	public HashMap getTextExpData(ArrayList lArrMjrHdRange,int liFinYrId,String lStrFrmDate,String lStrToDate)
	{
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		HashMap hmp=new HashMap();
		double lLngTotalExpPL=0;
		double lLngTotalExpNPL=0;
		double lLngTotalExpCSS=0;
		double lLngTotalExp = 0;
		StringBuffer lsb=null;
		int liQueryType=0;//0-plan 1-nonplan 2-css
		try
		{
			lCon=getSession().connection();
			while(liQueryType<3)
			{
				lsb=new StringBuffer();
				lsb.append(" select sum(p.amount)/10000000 totexp from  payment p where ");
				if(lStrFrmDate != null && !lStrFrmDate.equals(""))
				{
					lsb.append(" p.rcptdate>=('"+lStrFrmDate+"') and p.rcptdate<=('"+lStrToDate+"') ");
				}
				else
				{
					lsb.append(" p.yearcode="+liFinYrId+" and p.rcptdate<=('"+lStrToDate+"') ");
				}
				if(liQueryType==0)
				{
					lsb.append(" and  budget in (6,7,8,9) ");//plan
				}
				if(liQueryType==1)
				{
					lsb.append(" and  budget in (1,5) ");//non plan
				}
				if(liQueryType==2)
				{
					lsb.append(" and  budget in (2,3,4) ");//css
				}
				if(lArrMjrHdRange.size()==1)
				{
					Integer liMjrHd=new Integer(lArrMjrHdRange.get(0).toString());
					lsb.append(" and majorhead="+liMjrHd);
				}
				else
				{
					Integer liMjrHd1=new Integer(lArrMjrHdRange.get(0).toString());
					Integer liMjrHd2=new Integer(lArrMjrHdRange.get(1).toString());
					lsb.append("and majorhead between "+liMjrHd1+" and "+liMjrHd2);
				}
				
				glogger.info("Query for TxtExp -- "+lsb.toString());
				lStmt=lCon.createStatement();
				rs=lStmt.executeQuery(lsb.toString());
				while(rs.next())
				{
					if(liQueryType==0)
					{
						lLngTotalExpPL=rs.getDouble("totexp");
					}
					if(liQueryType==1)
					{
						lLngTotalExpNPL=rs.getDouble("totexp");
					}
					if(liQueryType==2)
					{
						lLngTotalExpCSS=rs.getDouble("totexp");
					}
				}
				rs.close();
				lStmt.close();
				liQueryType++;
			}
			lCon.close();
			hmp.put("Txt_PL",lLngTotalExpPL );
			hmp.put("Txt_NP",lLngTotalExpNPL );
			hmp.put("Txt_CSS",lLngTotalExpCSS );
			lLngTotalExp = lLngTotalExpPL + lLngTotalExpNPL + lLngTotalExpCSS;
			hmp.put("Txt_Total",lLngTotalExp );
			glogger.info("Total expenditure from TXT -- "+lLngTotalExp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return hmp;

	}
	
	public HashMap getTextRecptData(ArrayList lArrMjrHdRange,int liFinYrId,String lStrFrmDate,String lStrToDate)
	{
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		HashMap hmp=new HashMap();
		double lLngTotalExp=0;
		//double lLngTotalExpNPL=0;
		//double lLngTotalExpCSS=0;
		StringBuffer lsb=null;
		int liQueryType=0;//0-plan 1-nonplan 2-css
		try
		{
			lCon=getSession().connection();
			//while(liQueryType<3)
			//{
				lsb=new StringBuffer();
				lsb.append(" select sum(p.amount)/10000000 totexp from  receipt p where ");
				if(lStrFrmDate != null && !lStrFrmDate.equals(""))
				{
					lsb.append(" p.rcptdate>=('"+lStrFrmDate+"') and p.rcptdate<=('"+lStrToDate+"') ");
				}
				else
				{
					lsb.append(" p.yearcode="+liFinYrId+" and p.rcptdate<=('"+lStrToDate+"') ");
				}
				/*if(liQueryType==0)
				{
					lsb.append(" and  budget in (6,7,8,9) ");//plan
				}
				if(liQueryType==1)
				{
					lsb.append(" and  budget in (1,5) ");//non plan
				}
				if(liQueryType==2)
				{
					lsb.append(" and  budget in (2,3,4) ");//css
				}*/
				if(lArrMjrHdRange.size()==1)
				{
					int liMjrHd=Integer.parseInt(lArrMjrHdRange.get(0).toString());
					lsb.append(" and majorhead="+liMjrHd);
				}
				else
				{
					int liMjrHd1=Integer.parseInt(lArrMjrHdRange.get(0).toString());
					int liMjrHd2=Integer.parseInt(lArrMjrHdRange.get(1).toString());
					lsb.append("and majorhead between "+liMjrHd1+" and "+liMjrHd2);
				}
				glogger.info("Query for Receipt from TXT -- "+lsb.toString());
				
				lStmt=lCon.createStatement();
				rs=lStmt.executeQuery(lsb.toString());
				while(rs.next())
				{
					/*if(liQueryType==0)
					{
						lLngTotalExpPL=rs.getDouble("totexp");
					}
					if(liQueryType==1)
					{
						lLngTotalExpNPL=rs.getDouble("totexp");
					}
					if(liQueryType==2)
					{
						lLngTotalExpCSS=rs.getDouble("totexp");
					}*/
					lLngTotalExp = rs.getDouble("totexp");
				}
				rs.close();
				lStmt.close();
				//liQueryType++;
			//}
			lCon.close();
			/*hmp.put("Txt_PL",lLngTotalExpPL );
			hmp.put("Txt_NP",lLngTotalExpNPL );
			hmp.put("Txt_CSS",lLngTotalExpCSS );*/
			hmp.put("Txt_Total",lLngTotalExp );
			glogger.info("Total Recp from TXT -- "+hmp.get("Txt_Total"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return hmp;

	}
	
	public double getTotalREAmt(ArrayList lArrMjrHdRange,String lStrFinYrId)
    {
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		String DeptCapExp_Id=null;
		StringBuffer lStrQuery = new StringBuffer();
        double ldouTotalAmt=0;
       
        if(lArrMjrHdRange.size()==1)
		{
       		lStrQuery.append(" select max(curt_Yr_Re)/10000 Maxamt from Sgva_Expest_Finalamt ");
       		lStrQuery.append(" where fin_Yr_Id ='"+lStrFinYrId+"' and budmjrhd_code='"+lArrMjrHdRange.get(0)+"' and level_Id = 4 and ");
       		lStrQuery.append(" gia_Flg = 'N' and (rec_Flag not in ('R', 'C', 'M') or rec_Flag is null) ");
       		lStrQuery.append(" group by fin_Yr_Id, budbpn_Code, buddemand_Code, budmjrhd_Code, ");
       		lStrQuery.append(" budsubmjrhd_Code, budminhd_Code, budsubhd_Code, buddtlhd_Code, budobjhd_Code ");
       		lStrQuery.append(" having max(curt_Yr_Re) > 0 ");
		}
        else
        {
        	lStrQuery.append(" select max(curt_Yr_Re)/10000 Maxamt from Sgva_Expest_Finalamt ");
        	lStrQuery.append(" where fin_Yr_Id ='"+lStrFinYrId+"' and budmjrhd_code between'"+ lArrMjrHdRange.get(0)+"' and '"+ lArrMjrHdRange.get(1)+"' and level_Id = 4 and ");  
        	lStrQuery.append(" gia_Flg = 'N' and (rec_Flag not in ('R', 'C', 'M') or rec_Flag is null) ");
       		lStrQuery.append(" group by fin_Yr_Id, budbpn_Code, buddemand_Code, budmjrhd_Code, ");
       		lStrQuery.append(" budsubmjrhd_Code, budminhd_Code, budsubhd_Code, buddtlhd_Code, budobjhd_Code ");
       		lStrQuery.append(" having max(curt_Yr_Re) > 0 ");
        }
        glogger.info("Query for getTotalREAmt is : " + lStrQuery.toString());
        try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lStrQuery.toString());
			while(rs.next())
			{
				ldouTotalAmt+=rs.getDouble("Maxamt");
			}
		
			glogger.info("TotalRE Amount == "+ldouTotalAmt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ldouTotalAmt;
    }
	
	public Map getAllDeptGrantCollAmt(String lStrFinYrId, Date lDtStartDate, Date lDtEndDate, String lStrLangId)
    {
        HashMap<String,Long> lHashData = new HashMap<String,Long>();
        Object lObj[] = null;
        Query sqlQuery = null;
        Iterator iterator = null;
        Session hibSession = getSession();
        
        StringBuffer lStrQuery = new StringBuffer();
        
        lStrQuery.append(" select bpn.deptId, sum(grnt.id.planAmount * 1000) " +
                        " from SgvaBudgrantMst grnt, SgvaBudbpnMapping bpn where grnt.id.budbpnCode = bpn.bpncode" +
                        " and grnt.id.finYrId = :finYrId and bpn.langId = :langId" +
                        " group by bpn.deptId"); 
        
        glogger.info("Query for getAllDeptGrantCollAmt -> PL is : " + lStrQuery.toString());
        
        sqlQuery = hibSession.createQuery( lStrQuery.toString());
        
        sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
        sqlQuery.setParameter("langId", lStrLangId);
           
        iterator = sqlQuery.list().iterator();
              
        while (iterator.hasNext()) {
            lObj = (Object[]) iterator.next();
            if (lObj[0] != null && lObj[1] != null){
                lHashData.put(lObj[0].toString() + "_PL" , (long) Double.parseDouble(lObj[1].toString()));
            }
        }
        
        lStrQuery.delete(0, lStrQuery.length());
       
        lStrQuery.append(" select bpn.deptId, sum(grnt.id.nonplanAmount * 1000) " +
                " from SgvaBudgrantMst grnt, SgvaBudbpnMapping bpn where grnt.id.budbpnCode = bpn.bpncode" +
                " and grnt.id.finYrId = :finYrId and bpn.langId = :langId" +
                " group by bpn.deptId");  

        glogger.info("Query for getAllDeptGrantCollAmt -> NP is : " + lStrQuery.toString());
        
        sqlQuery = hibSession.createQuery( lStrQuery.toString());
        
        sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
        sqlQuery.setParameter("langId", lStrLangId);
           
        iterator = sqlQuery.list().iterator();
                        
        while (iterator.hasNext()) {
            lObj = (Object[]) iterator.next();
            if (lObj[0] != null && lObj[1] != null){
                lHashData.put(lObj[0].toString() + "_NP" , (long) Double.parseDouble(lObj[1].toString()));
            }
        }
                
        lStrQuery.delete(0, lStrQuery.length());
        
        lStrQuery.append(" select bpn.deptId, sum(grnt.id.cssAmount * 1000) " +
                " from SgvaBudgrantMst grnt, SgvaBudbpnMapping bpn where grnt.id.budbpnCode = bpn.bpncode" +
                " and grnt.id.finYrId = :finYrId and bpn.langId = :langId" +
                " group by bpn.deptId"); 
    
            glogger.info("Query for getAllDeptGrantCollAmt -> CSS is : " + lStrQuery.toString());
            
            sqlQuery = hibSession.createQuery( lStrQuery.toString());
            
            sqlQuery.setParameter("finYrId", Long.parseLong(lStrFinYrId));
            sqlQuery.setParameter("langId", lStrLangId);
               
            iterator = sqlQuery.list().iterator();
                        
            while (iterator.hasNext()) {
                lObj = (Object[]) iterator.next();
                if (lObj[0] != null && lObj[1] != null){
                    lHashData.put(lObj[0].toString() + "_CSS" , (long) Double.parseDouble(lObj[1].toString()));
                }
            }
        
       return lHashData;
    }
	
	public HashMap getMajorHeadWiseActuals(String lStrFinYr,String lStrLangId,String lStrPlanType)
	{
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		String DeptCapExp_Id=null;
		StringBuffer lStrQuery = new StringBuffer();
		String lStrMjrHd=null;
		HashMap lHmpMjrWiseAmt = new HashMap();
        long llngTotalAmt=0;
        
        lStrQuery.append("select p.majorhead mjr,sum(p.amount) amt from payment p  where yearcode=9 and ");    
        
        if(lStrPlanType.equalsIgnoreCase("PL"))
        	lStrQuery.append(" budget in (6,7,8,9) group by p.majorhead ");
        else
        	lStrQuery.append(" budget in (1,5) group by p.majorhead ");
        
        glogger.info("Query for major head wise amount is : " + lStrQuery.toString());
        try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lStrQuery.toString());
			while(rs.next())
			{
				lStrMjrHd=rs.getString("mjr");
				llngTotalAmt=rs.getLong("amt");
				lHmpMjrWiseAmt.put(lStrMjrHd,llngTotalAmt);
				
			}
		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return lHmpMjrWiseAmt;
	}
            
	public HashMap getDeptBudEst(ArrayList lArrMjrHdRange,int liFinYrId,String Lang_Id)
	{
		glogger.info("-- Inside getDeptBudEst -- ");
		
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		double lDblDeptBudEstAmt=0;
		String lstrDepBudEst_id=null;
		StringBuffer lsb=new StringBuffer();
		
		
			lsb.append(" SELECT DEPT.DEPT_ID, ");
			lsb.append(" (SELECT SUM(F.NXT_YR_BE)/10000  ");
			lsb.append(" FROM SGVA_EXPEST_FINALAMT F ");
			lsb.append(" WHERE F.FIN_YR_ID =" +liFinYrId+ " AND ");
			lsb.append(" F.BUDDEMAND_CODE IN ");
			lsb.append(" (SELECT DEM.DEMAND_CODE ");
			lsb.append(" FROM SGVA_BUDDEMAND_MST DEM ");
			lsb.append(" WHERE DEM.BPNCODE IN ");
			lsb.append(" (SELECT BPN.BPNCODE ");
			lsb.append(" FROM SGVA_BUDBPN_MAPPING BPN ");
			lsb.append(" WHERE BPN.DEPT_ID = DEPT.DEPT_ID AND ");
			lsb.append(" BPN.LANG_ID = '"+Lang_Id+"') AND ");
			lsb.append(" DEM.LANG_ID = '"+Lang_Id+"') AND F.LEVEL_ID = 4 AND ");
			lsb.append(" F.REC_FLAG is null AND F.GIA_FLG = 'N' AND ");
			if(lArrMjrHdRange.size()==1)
			{
				lsb.append(" F.PLAN_NONPLAN = 'PL' AND F.BUDMJRHD_CODE="+lArrMjrHdRange.get(0)+") NEXT_YR_EST ");
			}
			else
			{
				lsb.append(" F.PLAN_NONPLAN = 'PL' AND F.BUDMJRHD_CODE BETWEEN '"+ lArrMjrHdRange.get(0)+"' AND '"+lArrMjrHdRange.get(1)+"') NEXT_YR_EST ");
			}
			lsb.append(" FROM SGVO_DEPT_MST DEPT ");
			lsb.append(" WHERE DEPT.LANG_ID = '"+Lang_Id+"' AND DEPT.DEPT_TYPE='SEC' AND DEPT_ID NOT IN ('DP5','DP101') ");
	
			
		glogger.info("Query to getDeptBudEstAmt -- "+lsb.toString());
		
		try
		{
			glogger.info(" inside Try of getBudEstAmt");
			
			lCon=getSession().connection();
			System.out.println("--connection established in getDeptBudEstAmt--");
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			System.out.println("--Query Executed in getDeptBudEstAmt--");
			while(rs.next())
			{
				lstrDepBudEst_id=rs.getString("DEPT_ID");
				glogger.info("------Dept Id is--"+lstrDepBudEst_id);
				lDblDeptBudEstAmt=rs.getDouble("NEXT_YR_EST");
				lHashData.put(lstrDepBudEst_id, lDblDeptBudEstAmt);
				
			}
			glogger.info("DeptBudEstAmt== "+lDblDeptBudEstAmt);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return lHashData;
	}
	public HashMap getDeptRevExpText(ArrayList lArrMjrHdRange,String Lang_id,String lstrFromDate,String lstrToDate)
	{
		glogger.info("-- Inside getDeptRevExpText() -- ");
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		String DepRevExp_Id=null;
		double lDeptRevExpAmt=0;
		
		StringBuffer lsb=new StringBuffer();
		
		
			   lsb.append(" SELECT DEPT.DEPT_ID, ");
		       lsb.append(" (SELECT sum(PAY.AMOUNT) ");
		       lsb.append(" FROM PAYMENT PAY ");
		       lsb.append(" WHERE PAY.DEMAND IN (SELECT DEM.DEMAND_CODE ");
		       lsb.append(" FROM SGVA_BUDDEMAND_MST DEM ");
		       lsb.append(" WHERE DEM.BPNCODE IN ");
		       lsb.append(" (SELECT BPN.BPNCODE ");
		       lsb.append(" FROM SGVA_BUDBPN_MAPPING BPN ");
		       lsb.append(" WHERE BPN.DEPT_ID = DEPT.DEPT_ID AND ");
		       lsb.append(" BPN.LANG_ID = '"+ Lang_id + "')  AND ");
		       lsb.append(" DEM.LANG_ID = '"+ Lang_id + "') AND ");
		       if(lArrMjrHdRange.size()==1)
		       {
		    	   lsb.append(" PAY.MAJORHEAD ="+ lArrMjrHdRange.get(0)+" AND ");
		       }
		       else
		       {
		    	   lsb.append(" PAY.MAJORHEAD >="+ lArrMjrHdRange.get(0)+" AND PAY.MAJORHEAD <="+ lArrMjrHdRange.get(1)+" AND "); 
		       }
		       lsb.append(" PAY.RCPTDATE BETWEEN '"+lstrFromDate+"' AND ");
		       lsb.append(" '"+lstrToDate+"' AND ");
		       lsb.append(" PAY.BUDGET IN (6, 7, 8, 9)) REVENUE_EXP ");
		       lsb.append(" FROM SGVO_DEPT_MST DEPT ");
		       lsb.append(" WHERE DEPT.LANG_ID = '"+ Lang_id + "'");
		       lsb.append(" AND DEPT.DEPT_TYPE='SEC' AND DEPT_ID NOT IN ('DP5','DP101','DP45') ");
	
		
		glogger.info("Query to getDeptRevExpText -- "+lsb.toString());
		
		try
		{
			glogger.info("--In try of getDeptRevExpText");
			lCon=getSession().connection();
			glogger.info("<--Connection Created-->");
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			glogger.info("--Query Executed-->");
			while(rs.next())
			{
				DepRevExp_Id=rs.getString("DEPT_ID");
				glogger.info("DeptId in getDeptRevExp is----->"+DepRevExp_Id);
				lDeptRevExpAmt=rs.getDouble("REVENUE_EXP");	
				lHashData.put(DepRevExp_Id,lDeptRevExpAmt);
			}
	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{

				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return lHashData;
	}
	public HashMap getDeptCapExpText(ArrayList lArrMjrHdRange,String Lang_id,String lstrFromDate,String lstrToDate)
	{
		glogger.info("-- Inside getDeptCapExpText() -- ");
		HashMap<String,Double> lHashData = new HashMap<String,Double>();
		Connection lCon=null;
		Statement lStmt=null;
		ResultSet rs=null;
		String DepCapExp_Id=null;
		double lDeptRevExpAmt=0;
		
		StringBuffer lsb=new StringBuffer();
		
		
			   lsb.append(" SELECT DEPT.DEPT_ID,(SELECT sum(PAY.AMOUNT) FROM PAYMENT PAY ");
			   lsb.append(" WHERE PAY.DEMAND IN (SELECT DEM.DEMAND_CODE ");
			   lsb.append(" FROM SGVA_BUDDEMAND_MST DEM  WHERE DEM.BPNCODE IN ");
			   lsb.append(" (SELECT BPN.BPNCODE FROM SGVA_BUDBPN_MAPPING BPN ");
			   lsb.append(" WHERE BPN.DEPT_ID = DEPT.DEPT_ID AND ");
			   lsb.append(" BPN.LANG_ID = 'en_US') AND DEM.LANG_ID = 'en_US') AND ");
			   if(lArrMjrHdRange.size()==1)
		       {
				   lsb.append(" PAY.MAJORHEAD ="+ lArrMjrHdRange.get(0)+" AND ");
		       }
			   else
			   {
				   lsb.append(" PAY.MAJORHEAD >="+ lArrMjrHdRange.get(0)+" AND PAY.MAJORHEAD <="+ lArrMjrHdRange.get(1)+" AND ");
			   }
			   lsb.append(" PAY.RCPTDATE BETWEEN '"+lstrFromDate +"'AND '"+lstrToDate +"' AND ");
			   lsb.append(" PAY.BUDGET IN (6, 7, 8, 9)) CAP_EXP ");
			   lsb.append(" FROM SGVO_DEPT_MST DEPT WHERE DEPT.LANG_ID = 'en_US' ");
			   lsb.append(" AND DEPT.DEPT_TYPE='SEC' AND DEPT_ID NOT IN ('DP5','DP101','DP45') ");
		
		glogger.info("Query to getDeptCapExpText -- "+lsb.toString());
		
		try
		{
			glogger.info("--In try of getDeptCapExpText");
			lCon=getSession().connection();
			glogger.info("<--Connection Created-->");
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lsb.toString());
			glogger.info("--Query Executed-->");
			while(rs.next())
			{
				DepCapExp_Id=rs.getString("DEPT_ID");
				glogger.info("DeptId in getDeptCapExpText is----->"+DepCapExp_Id);
				lDeptRevExpAmt=rs.getDouble("REVENUE_EXP");	
				lHashData.put(DepCapExp_Id,lDeptRevExpAmt);
			}
	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{

				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return lHashData;
	}
	
	public HashMap getStrFundMjrRange(String strFundName)
	{
		HashMap lhshFundMrj = new HashMap();
		StringBuffer lSb=new StringBuffer();
		Statement lStmt=null;
		Connection lCon=null;
		ResultSet rs=null;
		//ArrayList lArrStrFundMjrHdRange=null;
		lSb.append("select major_hd  from config_recptexp_fund_mjrhd c"+
		" where c.fund_name='"+ strFundName + "'"); 
		
		glogger.info("Query for getStrFundMjrRange() "+ lSb.toString());
		try
		{
			lCon=getSession().connection();
			lStmt=lCon.createStatement();
			rs=lStmt.executeQuery(lSb.toString());
			if(rs.next())
			{
				//lhshFundMrj.put("FundId", rs.getInt("fund_type"));
				lhshFundMrj.put("MjrHdRange",rs.getString("major_hd"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				lStmt.close();
				lCon.close();
			}
			catch(Exception e)
			{
				glogger.error("Exception in closing connection in getFundMjrRange() "+e);
			}
		}
		return lhshFundMrj;
	}

	public List getPensionerData(String depname)
	{
		
		 List<List<Object>> arrOuter = null;
		 Query sqlQuery = null;
			try
			{
				Session hibSession = getSession();
				StringBuffer query = new StringBuffer();
				
				query.append(" select pm.departmentname dName, count(*) cnt " +
						     " from pen_mast pm ");
				
				if(!depname.equals("ALL"))
					query.append(" where pm.departmentname = :depname ");
				
				query.append(" group by pm.departmentname" +
						" order by pm.departmentname ");

				 sqlQuery = hibSession.createSQLQuery(query.toString())
				 						.addScalar("dName", Hibernate.STRING)
				 						.addScalar("cnt",Hibernate.INTEGER);
				 if(!depname.equals("ALL")) 
					 sqlQuery.setString("depname",depname.replaceAll("_", " "));	
				
				 List <Object[]>resList = sqlQuery.list();
					if (resList != null) 
					{
						arrOuter = new ArrayList<List<Object>>();
						int count = 1;
						for(Object[] tuple : resList)
						{
							List<Object> arrInner = new ArrayList<Object>();
							arrInner.add(count);
							arrInner.add((tuple[0] != null) ? tuple[0].toString() : "-");
							arrInner.add(tuple[1]);
							arrOuter.add(arrInner);
							count++;
						}	
					}      
				
			}
			catch(Exception e)
			{
				glogger.error("Exception occured in getPensionerData # \n"+e,e);
			}
			return arrOuter;
					
	}
	
	public List getDepartmentwiseData(String depname)
	{
		
		 List<List<Object>> arrOuter = null;
		 Query sqlQuery = null;
			try
			{
				Session hibSession = getSession();
				StringBuffer query = new StringBuffer();
				
				query.append(" select pm.ppo_desc pDesc, count(*) cnt" +
						" from pen_mast pm" +
						" where pm.departmentname = :depname "+
						" group by pm.ppo_desc " +
						" order by pm.ppo_desc ");

				 sqlQuery = hibSession.createSQLQuery(query.toString())
				 					  .addScalar("pDesc", Hibernate.STRING)
				 					  .addScalar("cnt",Hibernate.INTEGER);
				 
				 sqlQuery.setString("depname",depname);	
				
				 List <Object[]>resList = sqlQuery.list();
					if (resList != null) 
					{
						arrOuter = new ArrayList<List<Object>>();
						int count = 1;
						for(Object[] tuple : resList)
						{
							List<Object> arrInner = new ArrayList<Object>();
							arrInner.add(count);
							arrInner.add((tuple[0] != null) ? tuple[0].toString() : "-");
							arrInner.add(tuple[1]);
							arrInner.add(depname);
							arrOuter.add(arrInner);
							count++;
						}	
					}      
				
			}
			catch(Exception e)
			{
				glogger.error("Exception occured in getPensionerData # \n"+e,e);
			}
			return arrOuter;
					
	}	
	
	public List getDistrictwiseData(String depname, String ppo)
	{
		
		 List<List<Object>> arrOuter = null;
		 Query sqlQuery = null;
			try
			{
				Session hibSession = getSession();
				StringBuffer query = new StringBuffer();
				
				query.append(" select pm.districtname dName,count(*) cnt " +
						" from pen_mast pm" +
						" where pm.departmentname = :depname " +
						" and   pm.ppo_desc = :ppo" +
						" group by pm.districtname" +
						" order by pm.districtname ");

				 sqlQuery = hibSession.createSQLQuery(query.toString())
				 						.addScalar("dName", Hibernate.STRING)
				 						.addScalar("cnt",Hibernate.INTEGER);
				 sqlQuery.setString("depname",depname);	
				 sqlQuery.setString("ppo",ppo);	
				 int total = 0;
				 List <Object[]>resList = sqlQuery.list();
					if (resList != null) 
					{
						arrOuter = new ArrayList<List<Object>>();
						int count = 2;
						
						List<Object> arrInner = new ArrayList<Object>();
						arrInner.add(1);
						arrInner.add("ALL");
						arrInner.add(depname);
						arrInner.add(ppo);
						arrInner.add(total);
						arrOuter.add(arrInner);
						
						for(Object[] tuple : resList)
						{
							arrInner = new ArrayList<Object>();
							arrInner.add(count);
							arrInner.add((tuple[0] != null) ? tuple[0].toString() : "-");
							arrInner.add(depname);
							arrInner.add(ppo);
							arrInner.add(tuple[1]);
							arrOuter.add(arrInner);
							count++;
							total = total + Integer.valueOf(tuple[1].toString());
						}	
						
						arrInner =  arrOuter.get(0);
						arrInner.set(4, total);
						arrOuter.set(0, arrInner);
					}      
				
			}
			catch(Exception e)
			{
				glogger.error("Exception occured in getPensionerData # \n"+e,e);
			}
			return arrOuter;
					
	}
	
	public List getPensionerDetails(String depname, String ppo, String distname)
	{
		
		 List<List<Object>> arrOuter = null;
		 Query sqlQuery = null;
			try
			{
				Session hibSession = getSession();
				StringBuffer query = new StringBuffer();
				
				query.append(" select pm.departmentname deptName,pm.districtname distName,pm.ppo_no PnO," +
						     " pm.ppo_name pName, pm.ppo_retiredate retDate, pm.ppo_desc pDesc, pm.office_name offName, " +
						     " pm.ppo_address1 ||', '|| pm.ppo_address2 ||', '|| pm.ppo_address3 adrs " +
						     " from pen_mast pm " +
						     " where  1=1  " );
					
						     if(!depname.equals("ALL"))
						    	 query.append(" and  pm.departmentname = :depname " );
							if(!ppo.equals("ALL"))
						    	 query.append(" and   pm.ppo_desc = :ppo" );
							if(!distname.equals("ALL"))
							query.append(" and   pm.districtname in (:distname)" );
				      query.append(" order by pm.departmentname,pm.districtname, pm.ppo_desc,pm.ppo_retiredate desc ");

				 sqlQuery = hibSession.createSQLQuery(query.toString())
				 						.addScalar("deptName", Hibernate.STRING)
				 						.addScalar("distName", Hibernate.STRING)
				 						.addScalar("PnO", Hibernate.STRING)
				 						.addScalar("pName", Hibernate.STRING)
				 						.addScalar("retDate", Hibernate.DATE)
				 						.addScalar("pDesc", Hibernate.STRING)
				 						.addScalar("offName", Hibernate.STRING)
				 						.addScalar("adrs", Hibernate.STRING);
				 
				// sqlQuery.setString("depname",depname.replaceAll("_", " "));	
				 
				 	
				 if(!distname.equals("ALL"))	 
					 sqlQuery.setString("distname",distname.replaceAll("_", " "));	
				 if(!ppo.equals("ALL"))
					 sqlQuery.setString("ppo",ppo.replaceAll("_"," "));
				 if(!depname.equals("ALL"))
					 sqlQuery.setString("depname",depname.replaceAll("_"," "));
				 
				 List<Object[]> resList = sqlQuery.list();
					if (resList != null) 
					{
						arrOuter = new ArrayList<List<Object>>();
						int count = 1;
						for(Object[] tuple : resList)
						{
							List<Object> arrInner = new ArrayList<Object>();
							arrInner.add(count);
							arrInner.add((tuple[0] != null) ? tuple[0].toString() : "-");
							arrInner.add((tuple[1] != null) ? tuple[1].toString() : "-");
							arrInner.add((tuple[2] != null) ? tuple[2].toString() : "-");
							arrInner.add((tuple[3] != null) ? tuple[3].toString() : "-");
							arrInner.add((tuple[4] != null) ? oDateFormat.format(tuple[4]) : "-");
							arrInner.add((tuple[5] != null) ? tuple[5].toString() : "-");
							arrInner.add((tuple[6] != null) ? tuple[6].toString() : "-");
							arrInner.add((tuple[7] != null) ? tuple[7].toString() : "-");
							arrOuter.add(arrInner);
							count++;
						}	
					}      
				
			}
			catch(Exception e)
			{
				glogger.error("Exception occured in getPensionerData # \n"+e,e);
			}
			return arrOuter;
					
	}
	
/*	public List<String> getdistNames(String depname, String ppo)
	{
		
		List<String> arrInner = null;
		
		 Query sqlQuery = null;
			try
			{
				Session hibSession = getSession();
				StringBuffer query = new StringBuffer();
				
				query.append(" select distinct pm.districtname from pen_mast pm" +
						" where pm.departmentname = :depname " +
						" and   pm.ppo_desc = :ppo" +
						" order by pm.districtname ");

				 sqlQuery = hibSession.createSQLQuery(query.toString());
				 sqlQuery.setString("depname",depname);	
				 sqlQuery.setString("ppo",ppo);	
				 
				 List <Object[]>resList = sqlQuery.list();
					if (resList != null) 
					{
						 arrInner = new ArrayList<String>();
						
						for(Object tuple : resList)
						{
							arrInner.add(tuple.toString());
						}	
					}      
				
			}
			catch(Exception e)
			{
				glogger.error("Exception occured in getPensionerData # \n"+e,e);
			}
			return arrInner;
					
	}  */
	public List<ComboValuesVO> getdepartment(String lStrLangId, String langId) 
	{
		List<ComboValuesVO> arrMjrHead = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String depName = null;
		try
		{
			lCon = DBConnection.getConnection(  );
			StringBuffer lsb = new StringBuffer(150);
			
			lsb.append("select distinct pm.departmentname from pen_mast pm" +
					   " order by pm.departmentname " );
			
			lStmt = lCon.prepareStatement( lsb.toString() );
			lRs = lStmt.executeQuery();
			
			
			ComboValuesVO vo= new ComboValuesVO(  );
			vo.setDesc("ALL");
			vo.setId("ALL");
			arrMjrHead.add(vo);
			
			while(lRs.next())
			{	
				vo= new ComboValuesVO(  );
				depName = lRs.getString("DEPARTMENTNAME");
				String deptId = depName.replaceAll(" ", "_");
				vo.setId(deptId);
				vo.setDesc(depName);
				arrMjrHead.add(vo);
			}

		}
		catch( Exception e )
		{
			
			glogger.error( "SQLException::"+e.getMessage(), e );
		}

		return arrMjrHead;
	}

	public List getdesignation(String depname,String lStrLangId, String langId) throws SQLException
	{
		List lAlldesgn = new ArrayList();

		if(depname != null && !"-1".equals(depname)) {

			Connection lCon = null;
			PreparedStatement lStmt = null;
			ResultSet lRs = null;
			String lStrId=null;
			String vDesigId = null;
			StringBuffer query = new StringBuffer();

		 try{
				
			 	query.append("select distinct pm.ppo_desc from pen_mast pm" +
			 			     " where pm.ppo_desc is not null ");
				
				 if(!"ALL".equals(depname.replaceAll("_", " ")))
					 query.append(" and pm.departmentname = '" +depname.replaceAll("_", " " ) + "'");
				 
				 query.append(" order by pm.ppo_desc");
		
	        	lCon = DBConnection.getConnection(  );
	        	lStmt = lCon.prepareStatement(query.toString());
	            lRs = lStmt.executeQuery();

	            ComboValuesVO vo= new ComboValuesVO(  );
				vo.setDesc("ALL");
				vo.setId("ALL");
				lAlldesgn.add(vo);
                
	            while(lRs.next()) {

	            	vo= new ComboValuesVO(  );
	            	lStrId = lRs.getString("PPO_DESC");
	            	if(lStrId != null && !" ".equals(lStrId))
	            		vDesigId = lStrId.replaceAll(" ", "_");
	                vo.setId(vDesigId);
	                vo.setDesc(lStrId);
	                lAlldesgn.add(vo);
	            }
	        }
	        catch(Exception e) {

	        	glogger.error( "SQLException::"+e.getMessage(), e );
	        }
	        finally {

	        	lStmt.close();
	        	lCon.close();	        	
	        }
		}
        return lAlldesgn;
	}
	
	public List getdistrict(String depname, String designation,String lStrLangId, String langId) throws SQLException
	{
		List lAlldesgn = new ArrayList();

		if(depname != null && !"-1".equals(depname)) {

			Connection lCon = null;
			PreparedStatement lStmt = null;
			ResultSet lRs = null;
			String lStrId=null;
			StringBuffer query = new StringBuffer();

		 try{
				query.append(" select distinct pm.districtname from pen_mast pm " +
						    " where 1=1" );
				
				 if(!"ALL".equals(depname.replaceAll("_", " ")))
						query.append(" and pm.departmentname = '" +depname.replaceAll("_", " ")+ "' ");
				 
				 if(!"ALL".equals(designation.replaceAll("_", " ")))
					 query.append(" and   pm.ppo_desc = '" +  designation.replaceAll("_"," ")  + "' ");
						
				 query.append(" order by pm.districtname");
		
	        	lCon = DBConnection.getConnection(  );
	        	lStmt = lCon.prepareStatement(query.toString());
	            lRs = lStmt.executeQuery();
	            
	            ComboValuesVO vo= new ComboValuesVO(  );
	            vo.setId("ALL");
	            vo.setDesc("ALL");
	            lAlldesgn.add(vo);
	            while(lRs.next()) {

	            	 vo= new ComboValuesVO(  );
	            	lStrId = lRs.getString("DISTRICTNAME");
	                vo.setId(lStrId);
	                vo.setDesc(lStrId);
	                lAlldesgn.add(vo);
	            }
	        }
	        catch(Exception e) {

	        	glogger.error( "SQLException::"+e.getMessage(), e );
	        }
	        finally {

	        	lStmt.close();
	        	lCon.close();	        	
	        }
		}
        return lAlldesgn;
	}
}

