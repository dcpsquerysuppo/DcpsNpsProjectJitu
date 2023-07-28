package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

public interface CommonRptQueryDAO{
	public HashMap getStateProfileAmt(String lStrFinYrId, String lStrLangId); 
	
	public void setSessionFactory(SessionFactory sessionFactory);
	
	public ArrayList getPlNpl(String lStrLangId, String lstrLocId);
	
	public HashMap getAllDeptBEAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllDeptGrantAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllDeptActualsAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllDeptLiabAmt(String lStrFinYrId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllMjrHdBEAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllMjrHdGrantAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllMjrHdActualExp(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllMjrHdLiabilityAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllSchemeBEAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllSchemeGrantAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId, String lStrLocId);
	
	public HashMap getAllSchemeActualExp(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId);
	
	public HashMap getAllSchemeLiabilityAmt(String lStrFinYrId, String lStrDeptId, String lStrPlanType, String lStrLangId);

	public Map getAllDeptPLNPCSSBudAmt(String lStrFinYrId, String lStrLangId);
    public Map getAllDeptPLNPCSSCurrExpAmt(Date lDtStartDate, Date lDtEndDate,String lStrLangId);
    public Map getAllDeptPLNPCSSGrantAmt(String lStrFinYrId, String lStrLangId);
    public int getMaxMonthForGrnt(String lStrFinYrId);
    public long getTotalREAmt(String lStrFinYrId, String lStrDeptId);
    
    public Map getAllDeptPLNPCSSActualExpAmt(String lStrFinYrId, Date lDtStartDate, Date lDtEndDate, String lStrLangId);

	public HashMap getFundMjrRange(int lintFundType);
	
	public List getSubFundMjrRange(int lintFundType);
	
	public double getCSSAmount(ArrayList lArrMjrHdRange,int liFinYrId);
	
	public HashMap getConsolidatedExpBudAmt(ArrayList lArrLstMjrHd ,int lFinYrId );
	
	public HashMap getConsolidatedRcpBudAmt(ArrayList lArrMjrHd,int lFinYrId);
	
	public int getFinancialYear(String lStrFromDate,String lStrToDate);
	
	public HashMap getGrantAmt(ArrayList larrMjrHdRange,long lFinYrId ,String LangId);
	
	public HashMap getCurrentExpAmt(ArrayList lArrMjrHdRange,String lStrFromDate,String lStrToDate);
	
	public HashMap getProgressiveExpAmt(ArrayList lArrMjrHdRange,int liFinYrId, String lToDate);
	
	public HashMap getTextExpData(ArrayList lArrMjrHdRange,int liFinYrId,String lStrFrmDate,String lStrToDate);
	
	public HashMap getTextRecptData(ArrayList lArrMjrHdRange,int liFinYrId,String lStrFrmDate,String lStrToDate);
	
	public HashMap getPublicContingencyFund(ArrayList larrMjrHdRange,long lFinYrId,String LangId);
	
	public double getTotalREAmt(ArrayList lArrMjrHdRange,String lStrFinYrId);
	
	public Map getAllDeptGrantCollAmt(String lStrFinYrId, Date lDtStartDate, Date lDtEndDate, String lStrLangId);
	
	public HashMap getMajorHeadWiseActuals(String lStrFinYr,String lStrLangId,String lStrPlanType);
	
	public HashMap getDeptBudEst(ArrayList lArrMjrHdRange,int liFinYrId,String Lang_Id);
	
	public HashMap getDeptRevExpText(ArrayList lArrMjrHdRange,String Lang_id,String lstrFromDate,String lstrToDate);
	
	public HashMap getDeptCapExpText(ArrayList lArrMjrHdRange,String Lang_id,String lstrFromDate,String lstrToDate);
	
	public HashMap getStrFundMjrRange(String strFundName);
	
	public List getPensionerData(String depname);
	
	public List getDepartmentwiseData(String depname);
	
	public List getDistrictwiseData(String depname,String ppo);
	
	public List getPensionerDetails(String depname,String ppo,String distname);
	
//	public List getdistNames(String depname,String ppo);
}
