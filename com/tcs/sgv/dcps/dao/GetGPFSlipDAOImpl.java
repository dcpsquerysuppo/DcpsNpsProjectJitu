package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class GetGPFSlipDAOImpl extends GenericDaoHibernateImpl implements GetGPFSlipDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	public GetGPFSlipDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = getSession();
		
	}
	
	public List getYearList()
	{
		gLogger.info("in getYearList for GPF Slip");
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		
		sb.append("SELECT FIN_YEAR_DESC, FIN_YEAR_ID FROM SGVC_FIN_YEAR_MST ORDER by FIN_YEAR_DESC");
		

		gLogger.info("query to select FIN_YEAR_CODE, FIN_YEAR_ID:::" + sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		
		


		lLstReturnList = new ArrayList<ComboValuesVO>();

		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());
		return lLstResult;
	}
	//added by sunitha
	public String getGPFpfSeries(long userid)
	{
		String pfSeries=null;
		gLogger.info("in getCreditBlockDetails");
		List gpfList =new ArrayList();
		StringBuilder sb = new StringBuilder();
		//sb.append("SELECT PF_SERIES FROM HR_PAY_GPF_DETAILS where USER_ID=:userid"); uncomment before live
		sb.append("SELECT PF_SERIES FROM HR_PAY_GPF_DETAILS_CP where USER_ID=:userid"); 
		gLogger.info("query to PF_SERIES" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		
		selectQuery.setParameter("userid", userid);
		
		pfSeries = selectQuery.uniqueResult().toString();
		return pfSeries;
		
		
	}
	
	public String getGPFaccno(long userid)
	{
		String pfSeries=null;
		gLogger.info("in getGPFaccno");
		List gpfList =new ArrayList();
		StringBuilder sb = new StringBuilder();
		//sb.append("SELECT GPF_ACC_NO FROM HR_PAY_GPF_DETAILS where USER_ID=:userid");
		sb.append("SELECT GPF_ACC_NO FROM HR_PAY_GPF_DETAILS_CP where USER_ID=:userid");
		gLogger.info("query to getGPFaccno" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		
		selectQuery.setParameter("userid", userid);
		
		pfSeries = selectQuery.uniqueResult().toString();
		return pfSeries;
		
		
	}
	
	
	
	public List getCreditBlockDetails(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in getCreditBlockDetails");
		List creditDetailsList =new ArrayList();
		StringBuilder sb = new StringBuilder();
		/*sb.append("SELECT  PD.PCD_PCH_GPF_NMBR	GPF_Number , PD.PCD_PCH_GPF_PRFX  GPF_prfx ,MONTHNAME (PD.pcd_slip_mnth ) Slip_Month");
		sb.append(" ,PD.PCD_PCH_ABSTRCT_NMBR  ABSNO ,PD.PCD_PCH_VCHR_CHLN_TE_NMBR VCNO  ,MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NVL(PD.pcd_amnt,0)))   Subs");
		sb.append(",MAX(DECODE(PD.pcd_amnt_ctgry,'RFND',NVL(PD.pcd_amnt,0)))  Refunds ,MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NULL,'RFND',NULL,NVL(PD.pcd_amnt,0))) Others");
		sb.append(",cast(NVL(MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NVL(PD.pcd_amnt,0))),0)as bigint) +cast( NVL(MAX(DECODE(PD.pcd_amnt_ctgry,'RFND', NVL(PD.pcd_amnt,0))),0) as bigint)+ cast (NVL(MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NULL,'RFND',NULL,NVL(PD.pcd_amnt,0))),0) as bigint) totals ");
		
		sb.append(",MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',' ','RFND',' ',PD.pcd_amnt_ctgry))   Categorys ,PD.pcd_pch_srl_nmbr ,PCH_MJR_HEAD ,PCH_DDO_ID");
		sb.append(" FROM  PSTD_CRDT_ITEM_DTLS PD,PSTD_CRDT_ITEM_HDR PH WHERE ");
		sb.append(" PD.pcd_pch_gpf_prfx = PH.pch_gpf_prfx  AND PD.pcd_pch_gpf_nmbr = PH.pch_gpf_nmbr AND PD.pcd_pch_srce_dcmnt = PH.pch_srce_dcmnt AND ");
		sb.append(" PD.pcd_pch_adj_sqnce_nmbr = PH.pch_adjstmnt_sqnce_nmbr  AND  PD.pcd_pch_srl_nmbr = PH.pch_srl_nmbr AND	");
		sb.append(" PD.pcd_pch_abstrct_nmbr = PH.pch_abstrct_nmbr AND   PD.pcd_pch_vchr_chln_te_nmbr = PH.pch_vchr_chln_te_nmbr ");
		sb.append(" AND PD.pcd_pch_gpf_prfx  = :pfseries  AND   PD.pcd_pch_gpf_nmbr = :gpfaccno  AND  UPPER(PH.pch_wrng_pstd_item_flag ) = 'N' ");
		sb.append(" AND  ( (PH.pch_srce_dcmnt = 'N'  AND   PH.pch_abstrct_mnth  BETWEEN SUBSTR(:p_year,1,4)||'-04-01 00:00:00.0' ");
		sb.append(" AND  SUBSTR(:p_year,6,4)||'-03-31 00:00:00.0'  )");
		sb.append(" OR  ( PH.pch_srce_dcmnt = 'A'  AND PH.pch_adjstmnt_dt  BETWEEN SUBSTR(:p_year,1,4)||'-04-01 00:00:00.0' ");
		sb.append("AND SUBSTR(:p_year ,6,4)||'-03-31 00:00:00.0') )");
		sb.append(" GROUP BY  PD.PCD_PCH_GPF_NMBR, PD.PCD_PCH_GPF_PRFX, PD.pcd_slip_mnth , PD.PCD_PCH_ABSTRCT_NMBR, PD.PCD_PCH_VCHR_CHLN_TE_NMBR,");
		sb.append("  PD.pcd_pch_srl_nmbr,PCH_MJR_HEAD,PCH_DDO_ID ORDER BY 2,1,3");*/
		sb.append(" SELECT   PD.PCD_PCH_GPF_NMBR	 GPF_Number,PD.PCD_PCH_GPF_PRFX  GPF_prfx ,TO_CHAR(PD.pcd_slip_mnth,'DD/MM/YYYY')   Slip_Month ");
		sb.append(" ,PD.PCD_PCH_ABSTRCT_NMBR    ABSNO ,PD.PCD_PCH_VCHR_CHLN_TE_NMBR VCNO ");
		sb.append(" ,MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NVL(PD.pcd_amnt,0)))   Subs ");
		sb.append(" ,MAX(DECODE(PD.pcd_amnt_ctgry,'RFND',NVL(PD.pcd_amnt,0)))  Refunds ");
		sb.append(" ,MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NULL,'RFND',NULL,NVL(PD.pcd_amnt,0))) Others ");
		sb.append(" ,cast(NVL(MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NVL(PD.pcd_amnt,0))),0) as bigint) + cast(NVL(MAX(DECODE(PD.pcd_amnt_ctgry,'RFND',NVL(PD.pcd_amnt,0))),0)as bigint)+cast(NVL(MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',NULL,'RFND',NULL,NVL(PD.pcd_amnt,0))),0)as bigint)  totals");
		sb.append(" ,MAX(DECODE(PD.pcd_amnt_ctgry,'SUB',' ','RFND',' ',PD.pcd_amnt_ctgry))   Categorys ");
		sb.append(" ,PD.pcd_pch_srl_nmbr,PCH_MJR_HEAD,PCH_DDO_ID ");
		sb.append(" FROM PSTD_CRDT_ITEM_DTLS PD,PSTD_CRDT_ITEM_HDR PH  ");
		sb.append(" WHERE     PD.pcd_pch_gpf_prfx = PH.pch_gpf_prfx AND PD.pcd_pch_gpf_nmbr = PH.pch_gpf_nmbr AND  PD.pcd_pch_srce_dcmnt = PH.pch_srce_dcmnt ");
		sb.append(" AND PD.pcd_pch_adj_sqnce_nmbr = PH.pch_adjstmnt_sqnce_nmbr ");
		sb.append("AND  PD.pcd_pch_srl_nmbr = PH.pch_srl_nmbr AND PD.pcd_pch_abstrct_nmbr = PH.pch_abstrct_nmbr ");
		sb.append(" AND  PD.pcd_pch_vchr_chln_te_nmbr = PH.pch_vchr_chln_te_nmbr ");
		sb.append(" AND	PD.pcd_pch_gpf_prfx  =  '"+pfseries+"' AND  PD.pcd_pch_gpf_nmbr    = "+gpfaccno+" ");
		sb.append(" AND  UPPER(PH.pch_wrng_pstd_item_flag ) = 'N' AND    ");
		sb.append(" ((PH.pch_srce_dcmnt = 'N' AND PH.pch_abstrct_mnth  BETWEEN TO_DATE('01-APR-'||SUBSTR('"+p_year+"',1,4),'DD-MON-RRRR') ");
		sb.append(" AND TO_DATE('31-MAR-'||SUBSTR('"+p_year+"',6,9),'DD-MON-RRRR') ) OR ");
		sb.append(" ( PH.pch_srce_dcmnt = 'A'   AND PH.pch_adjstmnt_dt  BETWEEN TO_DATE('01-APR-'||SUBSTR('"+p_year+"',1,4),'DD-MON-RRRR') ");
		sb.append(" AND TO_DATE('31-MAR-'||SUBSTR('"+p_year+"' ,6,9),'DD-MON-RRRR')  ) ) ");
		sb.append(" GROUP BY  PD.PCD_PCH_GPF_NMBR,PD.PCD_PCH_GPF_PRFX,PD.pcd_slip_mnth ,PD.PCD_PCH_ABSTRCT_NMBR, PD.PCD_PCH_VCHR_CHLN_TE_NMBR,PD.pcd_pch_srl_nmbr,PCH_MJR_HEAD,PCH_DDO_ID ORDER BY 	2,1,3 ");
	
		gLogger.info("query to select cerdit details" + sb);
		//Session hibSession = getSession();
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		//selectQuery.setParameter("p_year", p_year);
		//selectQuery.setParameter("pfseries", pfseries);
		//selectQuery.setParameter("gpfaccno", gpfaccno);
		if(selectQuery.list()!=null && selectQuery.list().size()>0){
		creditDetailsList= selectQuery.list();
		}
		gLogger.info("list size:" +creditDetailsList.size());
		return creditDetailsList;
	}
	
	
	//for debit details
	
	
	public List getDebitBlockDetails(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in getDebitBlockDetails");
		List debitDetailsList =new ArrayList();
		StringBuilder sb = new StringBuilder();
	/*	sb.append("SELECT P.pdi_gpf_nmbr GPF_Number, P.pdi_gpf_prfx  GPF_prfx , MONTHNAME (P.pdi_slip_mnth  ) slip_mnth2, P.pdi_wthdrwl_amnt Debit , P.pdi_type_of_advnce Type ");
		sb.append("FROM	 pstd_dbt_item P WHERE  P.pdi_gpf_prfx  = :pfseries  AND	P.pdi_gpf_nmbr = :gpfaccno AND 	");
		sb.append("((UPPER(P.pdi_srce_dcmnt) = 'N' AND   P.PDI_SLIP_MNTH BETWEEN SUBSTR(:p_year,1,4)||'-04-01 00:00:00.0' ");
		sb.append("AND	SUBSTR(:p_year,6,4)||'-03-31 00:00:00.0' ) OR  (UPPER(P.pdi_srce_dcmnt) = 'A' ");
		sb.append("AND    P.PDI_SLIP_MNTH BETWEEN  SUBSTR(:p_year,1,4)||'-04-01 00:00:00.0' AND	SUBSTR(:p_year,6,4)||'-03-31 00:00:00.0' ))");
		sb.append("AND  UPPER(P.pdi_wrng_pstd_item_flag)  = 'N' ORDER BY 2,1,3   ");*/
		sb.append(" SELECT P.pdi_gpf_nmbr GPF_Number, P.pdi_gpf_prfx  GPF_prfx ");
		sb.append(" , MONTHNAME(P.pdi_slip_mnth)  slip_mnth2 ,P.pdi_wthdrwl_amnt Debit ");
		sb.append(" ,P.pdi_type_of_advnce Type FROM  pstd_dbt_item P  ");
		sb.append(" WHERE 	    P.pdi_gpf_prfx  =:pfseries AND	 P.pdi_gpf_nmbr =:gpfaccno ");
		sb.append(" AND	 (UPPER(P.pdi_srce_dcmnt) = 'N' AND	 UPPER(P.pdi_wrng_pstd_item_flag)  = 'N') ORDER BY 2,1,3  ");		
		
		gLogger.info("query to select cerdit details" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		//selectQuery.setParameter("p_year", p_year);
		selectQuery.setParameter("pfseries", pfseries);
		selectQuery.setParameter("gpfaccno", gpfaccno);
		debitDetailsList= selectQuery.list();
		gLogger.info("list size:" +debitDetailsList.size());
		return debitDetailsList;
	}
	//for employee details
	
	public List getemployeeBlockDetails(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in getCreditBlockDetails");
		List empDetailsList =new ArrayList();
		StringBuilder sb = new StringBuilder();
		/*sb.append("SELECT E.emp_gpf_no  GPF_Number,E.emp_gpf_prfx  GPF_prfx,E.emp_name  Subscriber_name, E.emp_dt_of_brth DOB ");
		sb.append(",DM.dmn_dscrptn  DDO,A.acs_dscrptn  Treasury FROM EMPLYS E, ddos DD , dmns DM,acnt_srcs A, GPF_BLNCE_HDR ,GPF_BLNCE_DTLS ");
		sb.append(" WHERE DD.ddo_id = E.emp_ddo_id AND  DD.ddo_dmn_des  =  DM.dmn_vlue AND  DD.ddo_dmn_des_typ = DM.dmn_name AND ");
		sb.append(" A.acs_id = DD.ddo_acs_id AND E.EMP_GPF_PRFX =GBH_SRM_SRS_CODE AND E.EMP_GPF_NO=GBH_SBR_GPF_NMBR AND ");
		sb.append(" E.EMP_GPF_PRFX=GBD_SRM_SRS_CODE AND E.EMP_GPF_NO=GBD_SBR_GPF_NMBR  AND  E.emp_gpf_no  = :gpfaccno and E.EMP_GPF_PRFX = :pfseries ");
		sb.append(" AND GBD_GBH_YEAR= SUBSTR(:p_year,1,4)||'-04-01 00:00:00.0' AND GBH_YEAR= SUBSTR(:p_year,1,4)||'-04-01 00:00:00.0' ");*/
		
		///changed given(above) query
		/*sb.append(" SELECT E.emp_gpf_no  GPF_Number,E.emp_gpf_prfx  GPF_prfx,E.emp_name  Subscriber_name, varchar_format(E.emp_dt_of_brth,'DD/MM/YYYY') DOB "); 
		sb.append(" ,DM.dmn_dscrptn  DDO,A.acs_dscrptn  Treasury "); 
		sb.append(" FROM EMPLYS E, ddos DD , dmns DM,acnt_srcs A "); 
		 sb.append(" WHERE DD.ddo_id = E.emp_ddo_id AND  DD.ddo_dmn_des  =  DM.dmn_vlue AND  DD.ddo_dmn_des_typ = DM.dmn_name AND "); 
		sb.append(" A.acs_id = DD.ddo_acs_id AND E.emp_gpf_no  = :gpfaccno and E.EMP_GPF_PRFX = :pfseries ");*/
		
		sb.append(" SELECT		E.emp_gpf_no   GPF_Number,E.emp_gpf_prfx GPF_prfx,E.emp_name  Subscriber_name ");
		sb.append(" ,TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY')   DOB,DM.dmn_dscrptn  DDO,A.acs_dscrptn  Treasury ");
		sb.append(" ,DECODE(TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY'),'01-APR-1957','CAUTION' ,null)   DOBtext8 ");
		sb.append(" ,DECODE(TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY'),'01-APR-1957','Incorrect date of birth',null)  DOBtext1 ");
		sb.append(" ,DECODE(TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY'),'01-APR-1957','computer will',null) DOBtext2 ");
		sb.append(" ,DECODE(TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY'),'01-APR-1957','NOT ACCEPT CREDITS' ,null)   DOBtext3 ");
		sb.append(" ,DECODE(TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY'),'01-APR-1957','Submit DOB to' ,null)   DOBtext4 ");
		 sb.append(" ,DECODE(TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY'),'01-APR-1957','avoid loss of' ,null)   DOBtext5 ");
		 sb.append(" ,DECODE(TO_CHAR(E.emp_dt_of_brth,'DD-MON-YYYY'),'01-APR-1957','credits' ,null)   DOBtext6 ");
		 sb.append(" FROM EMPLYS E ,ddos DD ,dmns DM ,acnt_srcs A,GPF_BLNCE_HDR ,GPF_BLNCE_DTLS ");
		 sb.append(" WHERE   DD.ddo_id = E.emp_ddo_id AND  DD.ddo_dmn_des  =  DM.dmn_vlue AND  DD.ddo_dmn_des_typ = DM.dmn_name AND A.acs_id = DD.ddo_acs_id ");
		 sb.append(" AND E.EMP_GPF_PRFX =GBH_SRM_SRS_CODE AND E.EMP_GPF_NO=GBH_SBR_GPF_NMBR AND E.EMP_GPF_PRFX=GBD_SRM_SRS_CODE AND E.EMP_GPF_NO=GBD_SBR_GPF_NMBR ");
		 sb.append(" AND UPPER(emp_gpf_acnt_cls_flag) = 'N' and  E.emp_gpf_prfx =:pfseries AND     E.emp_gpf_no =:gpfaccno AND GBD_GBH_YEAR=TO_DATE('04/2012','MM/YYYY')  ");
		 sb.append(" AND GBH_YEAR=TO_DATE('04/2012','MM/YYYY')  and   GBH_INTRST_GVN_FLAG='Y' ");
		sb.append(" AND  NVL(GBD_CLSNG_BLNCE_NW_NI, 0)+NVL(GBD_CLSNG_BLNCE_W_I, 0)+NVL(GBD_CLSNG_BLNCE_NW_I, 0)+NVL(GBD_CLSNG_BLNCE_W_NI, 0) >0   ");
		sb.append(" and e.emp_ddo_id  not in ");
		sb.append(" ('NAGA4946','NAGA4948','NAGA4953','NAGA4954','NAGA4955', 'NAGA4956','NAGA4958','NAGA4959','NAGA4961','NAGA4962', 'NAGA4964','NAGA4965','NAGA4967','NAGA4968','NAGA4969', ");
		sb.append(" 'NAGA4972','NAGA4973','NAGA4975','NAGA4977','NAGA4978','NAGA4982','NAGA4985','NAGA4988','NAGA4983','NAGA4980','NAGA7273','NAGA4970','NAGA4951','NAGA4957','NAGA4960',");
		sb.append(" 'NAGA4963','NAGA4966','NAGA4971','NAGA4986','NAGA8402','NAG15764','NAG16015','NAG16392','NAG16590','NAGA8492','NAGA8500','NAGA8512','NAGA8514','NAG12379','NAG12380', ");
		sb.append(" 'NAG12601','NAG12619','NAG12620','NAG12625','NAG12626','NAG12628','NAG12632','NAG12633','NAG12637','NAG12638','NAG12640','NAG12649','NAG12651','NAG12653','NAG12655','NAG12657','NAG12659','NAG12662','NAG13422','NAG13684', ");
		sb.append(" 'NAG13741','NAG15509','NAG15902','NAG15929','NAG16417','NAGA4951','NAGA4957','NAGA4960','NAGA4963','NAGA4966','NAGA4970','NAGA4971','NAGA4980','NAGA4983','NAGA4986','NAGA7634','NAGA8402','NAG12695','NAG8628','NAG8629','NAG13434',");
		sb.append("'NAG13449','NAG15988','NAG12256','NAG13426','NAG15455','NAG13423','NAGA8613','NAG13424','NAG13425','NAG13427','NAG13428','NAG8653','NAG13429','NAG13430','NAG13431','NAG13432','NAG13433','NAG13451','NAG13435','NAG13436','NAG13437','NAG13438','NAG13439','NAG13440','NAG13453',");
		sb.append("'NAG13447','NAGA8653','NAG13754','NAG15911','NAG16020') oRDER BY  a.acs_dscrptn ,E.emp_ddo_id ,E.emp_gpf_prfx,E.emp_gpf_no  ");
		gLogger.info("query to select cerdit details" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		//selectQuery.setParameter("p_year", p_year);
		selectQuery.setParameter("pfseries", pfseries);
		selectQuery.setParameter("gpfaccno", gpfaccno);
		empDetailsList= selectQuery.list();
		gLogger.info("list size:" +empDetailsList.size());
		return empDetailsList;
	}
	
	/*public List getSummaryBlockBlockDetails(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in summaryBlockDetailsList");
		List summaryBlockDetailsList =new ArrayList();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT G.GBD_SBR_GPF_NMBR  GPF_Number ,G.GBD_SRM_SRS_CODE  GPF_prfx ");
		sb.append(" ,cast(NVL(G.GBD_OPNG_BLNCE_W_I,0) as bigint)+cast( NVL(G.GBD_OPNG_BLNCE_W_NI,0) as bigint) -cast((" );
				if(p_year.equals("2010-2011"))
			sb.append(" nw_int_2009_10  ");
		else 	if(p_year.equals("2011-2012"))
			sb.append(" nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011  ");
					
			sb.append(	"nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011) as bigint) 	opening_balance_W_I ");
		sb.append("  ,cast(NVL(G.GBD_OPNG_BLNCE_NW_I,0) as bigint) +cast(NVL(G.GBD_OPNG_BLNCE_NW_NI,0) as bigint)+cast((nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011) as bigint)	opening_balance_NW_NI ");
		sb.append("  ,cast(NVL(G.GBD_OPNG_BLNCE_W_I,0) as bigint)+ cast(NVL(G.GBD_OPNG_BLNCE_NW_I,0)  as bigint)+  cast(NVL(G.GBD_OPNG_BLNCE_W_NI,0)  as bigint)+ cast(NVL(G.GBD_OPNG_BLNCE_NW_NI,0) as bigint) 	Total_opening_balance ");
		sb.append("  ,cast(NVL(G.GBD_DPST_W_I,0) as bigint) + cast(NVL(G.GBD_CR_ADJSTMNT_W_I,0) as bigint) + cast(NVL(G.GBD_DPST_W_NI,0 ) as bigint) + cast(NVL(G.GBD_CR_ADJSTMNT_W_NI,0) as bigint) Deposits_W_I  ");
		sb.append("   ,cast(NVL(G.GBD_DPST_NW_I,0 ) as bigint)+cast(NVL( G.GBD_CR_ADJSTMNT_NW_I,0) as bigint) +  cast(NVL(G.GBD_DPST_NW_NI,0) as bigint)+cast(NVL(G.GBD_CR_ADJSTMNT_NW_NI,0) as bigint) Deposits_NW_NI  ");
		sb.append("  ,cast(NVL(G.GBD_DPST_W_I,0) as bigint) + cast(NVL(G.GBD_CR_ADJSTMNT_W_I,0) as bigint) + cast(NVL(G.GBD_DPST_NW_I,0 ) as bigint)+cast(NVL( G.GBD_CR_ADJSTMNT_NW_I,0) as bigint) ");
		sb.append("  + cast(NVL(G.GBD_DPST_W_NI ,0 ) as bigint)+ cast(NVL(G.GBD_CR_ADJSTMNT_W_NI,0) as bigint)  + cast(NVL(G.GBD_DPST_NW_NI ,0 ) as bigint)+ cast(NVL(G.GBD_CR_ADJSTMNT_NW_NI,0)as bigint)    Total_deposits ");
		sb.append("  ,cast(NVL(G.GBD_WTHDRWL_I,0) as bigint) + cast(NVL(G.GBD_DR_ADJSTMNT_I,0) as bigint)+cast(NVL(G.GBD_WTHDRWL_NI,0) as bigint) + cast(NVL(G.GBD_DR_ADJSTMNT_NI,0) as bigint) Withdrawals_W_I ");
		sb.append("  ,0 	Withdrawals_NW_I,0 	Withdrawals_NW_I ");
		sb.append(" , cast(NVL(G.GBD_WTHDRWL_I,0) as bigint) + cast(NVL(G.GBD_DR_ADJSTMNT_I,0)  as bigint) + cast(NVL(G.GBD_WTHDRWL_NI,0) as bigint) + cast(NVL(G.GBD_DR_ADJSTMNT_NI,0)as bigint) 	Total_withdrawals");
		sb.append(" ,cast(NVL(G.GBD_INTRST_ON_OPNG_BLNCE,0) as bigint) + cast(NVL(GBD_INTRST_ON_CR_YR_DPST,0) as bigint)  + cast(NVL(GBD_INTRST_ON_CR_ADJSTMNT,0) as bigint)   ");
		sb.append("  - cast(NVL(GBD_INTRST_ON_DR_ADJSTMNT,0) as bigint) - cast(NVL(G.GBD_INTRST_ON_CRNT_YR_W,0) as bigint) - cast(nvl(nw_int_2011_12,0) as bigint)        Interest_W_I");
		sb.append("  , 0 Interest_W_I, 0 Interest_W_NI , nvl(nw_int_2011_12,0) Interest_NW_I , 0 Interest_NW_NI,cast(NVL(G.GBD_INTRST_ON_OPNG_BLNCE,0)  as bigint)    ");
		sb.append(" + cast(NVL(GBD_INTRST_ON_CR_YR_DPST,0) as bigint)  + cast(NVL(GBD_INTRST_ON_CR_ADJSTMNT,0) as bigint) - cast(NVL(GBD_INTRST_ON_DR_ADJSTMNT,0) as bigint) - cast(NVL(G.GBD_INTRST_ON_CRNT_YR_W,0) as bigint) 	Total_interest ");
		sb.append("  ,cast(NVL(G.GBD_CLSNG_BLNCE_W_I,0) as bigint)    + ");
		sb.append("  cast(NVL(G.GBD_CLSNG_BLNCE_W_NI,0) as bigint) - cast((nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011+nw_int_2011_12)as bigint)    Closing_balance_W_I ");
		sb.append("  , cast(NVL(G.GBD_CLSNG_BLNCE_NW_I,0) as bigint)  + ");
		sb.append(" cast(NVL(G.GBD_CLSNG_BLNCE_NW_NI,0) as bigint) + cast((nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011+nw_int_2011_12) as bigint)   Closing_balance_NW_I");
		sb.append("  ,round( cast(NVL(G.GBD_CLSNG_BLNCE_W_I,0) as bigint)  + cast( NVL(G.GBD_CLSNG_BLNCE_W_NI,0) as bigint)  +  ");
		sb.append("   cast( NVL(G.GBD_CLSNG_BLNCE_NW_I,0) as bigint)   +  cast(NVL(G.GBD_CLSNG_BLNCE_NW_NI,0) as bigint))  Total_closing_balance,");
		sb.append(" (cast(NVL(G.GBD_DPST_W_NI,0) as bigint) + cast(NVL(G.GBD_CR_ADJSTMNT_W_NI,0) as bigint) + cast(NVL(G.GBD_DPST_NW_NI,0 ) as bigint) +cast(NVL( G.GBD_CR_ADJSTMNT_NW_NI,0) as bigint) ) non_intt_amot ");
		sb.append(" FROM   GPF_BLNCE_DTLS G,gpf_blnce_dtls_intt i WHERE ");
		sb.append("  i.srs_code=g.gbd_srm_srs_code and i.gpf_nmbr=g.gbd_sbr_gpf_nmbr and G.GBD_SRM_SRS_CODE = :pfseries ");
		sb.append("  AND  G.GBD_SBR_GPF_NMBR  = :gpfaccno  AND  TO_CHAR(G.GBD_GBH_YEAR, 'RRRR')  =  SUBSTR(:p_year,1,4) and ");
		sb.append(" to_char(i.year,'RRRR')=SUBSTR(:p_year,1,4) AND UPPER(G.gbd_acnt_ctgry_code) = 'GPF'");
		
		gLogger.info("query to select cerdit details" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		selectQuery.setParameter("p_year", p_year);
		selectQuery.setParameter("pfseries", pfseries);
		selectQuery.setParameter("gpfaccno", gpfaccno);
		summaryBlockDetailsList= selectQuery.list();
		gLogger.info("list size:" +summaryBlockDetailsList.size());
		return summaryBlockDetailsList;
	}
	*/
	public List getSummaryBlockBlockDetailsupdated(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in summaryBlockDetailsList");
		List summaryBlockDetailsList =new ArrayList();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT G.GBD_SBR_GPF_NMBR GPF_Number,G.GBD_SRM_SRS_CODE GPF_prfx   ");
		sb.append(" ,NVL(G.GBD_OPNG_BLNCE_W_I,0) + NVL(G.GBD_OPNG_BLNCE_W_NI,0)-(nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011) 	opening_balance_W_I ");
		sb.append(" ,NVL(G.GBD_OPNG_BLNCE_NW_I,0) + NVL(G.GBD_OPNG_BLNCE_NW_NI,0)+(nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011)	opening_balance_NW_NI ");
		sb.append(" ,(NVL(G.GBD_OPNG_BLNCE_W_I,0) + NVL(G.GBD_OPNG_BLNCE_NW_I,0)  + NVL(G.GBD_OPNG_BLNCE_W_NI,0) + NVL(G.GBD_OPNG_BLNCE_NW_NI,0)) Total_opening_balance ");
		sb.append(" ,NVL(G.GBD_DPST_W_I,0) + NVL(G.GBD_CR_ADJSTMNT_W_I,0) + NVL(G.GBD_DPST_W_NI,0 ) + NVL(G.GBD_CR_ADJSTMNT_W_NI,0) Deposits_W_I    ");
		sb.append(" ,NVL(G.GBD_DPST_NW_I,0 )+NVL( G.GBD_CR_ADJSTMNT_NW_I,0)  + NVL(G.GBD_DPST_NW_NI,0)+NVL(G.GBD_CR_ADJSTMNT_NW_NI,0) Deposits_NW_NI  ");
		sb.append(" ,(NVL(G.GBD_DPST_W_I,0) + NVL(G.GBD_CR_ADJSTMNT_W_I,0)  + NVL(G.GBD_DPST_NW_I,0 ) +NVL( G.GBD_CR_ADJSTMNT_NW_I,0) + NVL(G.GBD_DPST_W_NI ,0 )+ NVL(G.GBD_CR_ADJSTMNT_W_NI,0)   +NVL(G.GBD_DPST_NW_NI ,0 )+ NVL(G.GBD_CR_ADJSTMNT_NW_NI,0))    Total_deposits ");
		sb.append(" ,NVL(G.GBD_WTHDRWL_I,0) + NVL(G.GBD_DR_ADJSTMNT_I,0)+NVL(G.GBD_WTHDRWL_NI,0) + NVL(G.GBD_DR_ADJSTMNT_NI,0) Withdrawals_W_I ");
		sb.append("     ,0 	Withdrawals_NW_I   ");
		sb.append(",0 	Withdrawals_NW_I  ");
		sb.append(" , (NVL(G.GBD_WTHDRWL_I,0) + NVL(G.GBD_DR_ADJSTMNT_I,0)  +  NVL(G.GBD_WTHDRWL_NI,0) + NVL(G.GBD_DR_ADJSTMNT_NI,0)) 	Total_withdrawals ");
		sb.append(" ,NVL(G.GBD_INTRST_ON_OPNG_BLNCE,0) + NVL(GBD_INTRST_ON_CR_YR_DPST,0) + NVL(GBD_INTRST_ON_CR_ADJSTMNT,0) - NVL(GBD_INTRST_ON_DR_ADJSTMNT,0)- NVL(G.GBD_INTRST_ON_CRNT_YR_W,0) - (nvl(nw_int_2011_12,0))  Interest_W_I ");
		sb.append(" , 0 Interest_W_I ");
		sb.append(" , 0 Interest_W_NI ");
		sb.append(" , nvl(nw_int_2011_12,0) Interest_NW_I ");
		sb.append(" , 0 Interest_NW_NI ");
		sb.append(" ,NVL(G.GBD_INTRST_ON_OPNG_BLNCE,0)  + NVL(GBD_INTRST_ON_CR_YR_DPST,0) + NVL(GBD_INTRST_ON_CR_ADJSTMNT,0) - NVL(GBD_INTRST_ON_DR_ADJSTMNT,0) - NVL(G.GBD_INTRST_ON_CRNT_YR_W,0)	 Total_interest	  ");
		sb.append(" ,NVL(G.GBD_CLSNG_BLNCE_W_I,0)     +  NVL(G.GBD_CLSNG_BLNCE_W_NI,0)  - (nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011+nw_int_2011_12)   Closing_balance_W_I ");
		sb.append(" ,NVL(G.GBD_CLSNG_BLNCE_NW_I,0)  +  NVL(G.GBD_CLSNG_BLNCE_NW_NI,0) +(nw_int_2009_10+nw_int_2010_11+nw_old_int_jun2011+nw_int_2011_12)  Closing_balance_NW_I ");
		sb.append(" ,round((NVL(G.GBD_CLSNG_BLNCE_W_I,0)  +  NVL(G.GBD_CLSNG_BLNCE_W_NI,0)  + NVL(G.GBD_CLSNG_BLNCE_NW_I,0) + NVL(G.GBD_CLSNG_BLNCE_NW_NI,0)))            Total_closing_balance, ");
		sb.append(" (NVL(G.GBD_DPST_W_NI,0) + NVL(G.GBD_CR_ADJSTMNT_W_NI,0)  +NVL(G.GBD_DPST_NW_NI,0 ) +NVL( G.GBD_CR_ADJSTMNT_NW_NI,0) ) non_intt_amot ");
		sb.append(" FROM   GPF_BLNCE_DTLS G,gpf_blnce_dtls_intt i ");
		sb.append(" WHERE  i.srs_code=g.gbd_srm_srs_code and  i.gpf_nmbr=g.gbd_sbr_gpf_nmbr and ");
		sb.append(" G.GBD_SRM_SRS_CODE =:pfseries AND G.GBD_SBR_GPF_NMBR  =:gpfaccno ");
		sb.append(" AND TO_CHAR(G.GBD_GBH_YEAR, 'RRRR')  =  SUBSTR(:p_year,1,4) ");
		sb.append("  and to_char(i.year,'RRRR')=SUBSTR(:p_year,1,4) ");
		sb.append(" AND  UPPER(G.gbd_acnt_ctgry_code) = 'GPF' ");
		gLogger.info("query to select cerdit details" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		selectQuery.setParameter("p_year", p_year);
		selectQuery.setParameter("pfseries", pfseries);
		selectQuery.setParameter("gpfaccno", gpfaccno);
		summaryBlockDetailsList= selectQuery.list();
		gLogger.info("list size:" +summaryBlockDetailsList.size());
		return summaryBlockDetailsList;
	}
	public List missingCredit(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in missingCredit");
		List missingCredit =new ArrayList();
		StringBuilder sb = new StringBuilder();
		/*sb.append("SELECT  M.mcd_sbr_gpf_nmbr GPF_Number , M.mcd_srm_srs_code GPF_prfx ,(DECODE(M.MCD_CRDT_DBT_FLAG,'SB',M.MCD_MSNG_MNTH)) missing_crs ");
		sb.append(" , (DECODE(M.MCD_CRDT_DBT_FLAG,'DR',M.MCD_MSNG_MNTH)) missing_drs  FROM msng_crdt_dbts M   WHERE  M.mcd_srm_srs_code = :pfseries ");
		sb.append(" AND	 M.mcd_sbr_gpf_nmbr   = :gpfaccno AND UPPER(M.mcd_clrnce_flag ) = 'N' And  to_char(mcd_msng_mnth,'RRRR')<SUBSTR(:p_year,6,4) ");*/
		sb.append(" SELECT	    M.mcd_sbr_gpf_nmbr GPF_Number, M.mcd_srm_srs_code GPF_prfx ,(DECODE(M.MCD_CRDT_DBT_FLAG,'SB',M.MCD_MSNG_MNTH)) missing_crs ");
		sb.append(" , (DECODE(M.MCD_CRDT_DBT_FLAG,'DR',M.MCD_MSNG_MNTH)) missing_drs FROM	    msng_crdt_dbts M WHERE	    M.mcd_srm_srs_code =:pfseries ");
		sb.append("  AND	M.mcd_sbr_gpf_nmbr  =:gpfaccno AND	 mcd_crdt_dbt_flag='SB' and M.MCD_MSNG_MNTH <= '31-MAR-2012' AND UPPER(M.mcd_clrnce_flag ) = 'N'");
		gLogger.info("query to select missingCredit" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		//selectQuery.setParameter("p_year", p_year);
		selectQuery.setParameter("pfseries", pfseries);
		selectQuery.setParameter("gpfaccno", gpfaccno);
		missingCredit= selectQuery.list();
		gLogger.info("list size:" +missingCredit.size());
		return missingCredit;
	}
	
	public List missingDebit(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in missingDebit");
		List missingCredit =new ArrayList();
		StringBuilder sb = new StringBuilder();
		/*sb.append("SELECT   M.mcd_sbr_gpf_nmbr GPF_Number , M.mcd_srm_srs_code GPF_prfx , M.mcd_msng_mnth Missing_debits FROM	    msng_crdt_dbts M ");
		sb.append(" WHERE  M.mcd_srm_srs_code = :pfseries  AND	  M.mcd_sbr_gpf_nmbr   = :gpfaccno AND	  mcd_crdt_dbt_flag='DR'  AND	 UPPER(M.mcd_clrnce_flag ) = 'N' ");
		sb.append(" And to_char(mcd_msng_mnth,'RRRR')<SUBSTR(:p_year,6,4)");
	*/
		sb.append(" SELECT M.mcd_sbr_gpf_nmbr GPF_Number, M.mcd_srm_srs_code GPF_prfx , M.mcd_msng_mnth Missing_debits FROM	    msng_crdt_dbts M WHERE	M.mcd_srm_srs_code = :pfseries");
		sb.append(" AND	 M.mcd_sbr_gpf_nmbr  =:gpfaccno AND	 mcd_crdt_dbt_flag='DR' and ");
		sb.append(" M.MCD_MSNG_MNTH <= '31-MAR-2013' AND	 UPPER(M.mcd_clrnce_flag ) = 'N'");		
		gLogger.info("query to select missingCredit" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		//selectQuery.setParameter("p_year", p_year);
		selectQuery.setParameter("pfseries", pfseries);
		selectQuery.setParameter("gpfaccno", gpfaccno);
		missingCredit= selectQuery.list();
		gLogger.info("list size:" +missingCredit.size());
		return missingCredit;
	}
	
	public List getSummaryBlockBlockDetails(String p_year,String pfseries,String gpfaccno)
	{
		gLogger.info("in summaryBlockDetailsList");
		List summaryBlockDetailsList =new ArrayList();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT G.GBD_SBR_GPF_NMBR GPF_Number,G.GBD_SRM_SRS_CODE GPF_prfx   ");
		sb.append(" ,NVL(G.GBD_OPNG_BLNCE_W_I,0) + NVL(G.GBD_OPNG_BLNCE_W_NI,0)-(nw_int_2009_10+nw_int_2010_11+nw_int_2011_12+nw_old_int_jun2011) 	opening_balance_W_I ");
		sb.append(" ,NVL(G.GBD_OPNG_BLNCE_NW_I,0) + NVL(G.GBD_OPNG_BLNCE_NW_NI,0)+(nw_int_2009_10+nw_int_2010_11+nw_int_2011_12+nw_old_int_jun2011)	opening_balance_NW_NI ");
		sb.append(" ,(NVL(G.GBD_OPNG_BLNCE_W_I,0) + NVL(G.GBD_OPNG_BLNCE_NW_I,0)  + NVL(G.GBD_OPNG_BLNCE_W_NI,0) + NVL(G.GBD_OPNG_BLNCE_NW_NI,0)) Total_opening_balance ");
		sb.append(" ,NVL(G.GBD_DPST_W_I,0) + NVL(G.GBD_CR_ADJSTMNT_W_I,0) + NVL(G.GBD_DPST_W_NI,0 ) + NVL(G.GBD_CR_ADJSTMNT_W_NI,0) Deposits_W_I    ");
		sb.append(" ,NVL(G.GBD_DPST_NW_I,0 )+NVL( G.GBD_CR_ADJSTMNT_NW_I,0)  + NVL(G.GBD_DPST_NW_NI,0)+NVL(G.GBD_CR_ADJSTMNT_NW_NI,0) Deposits_NW_NI  ");
		sb.append(" ,(NVL(G.GBD_DPST_W_I,0) + NVL(G.GBD_CR_ADJSTMNT_W_I,0)  + NVL(G.GBD_DPST_NW_I,0 ) +NVL( G.GBD_CR_ADJSTMNT_NW_I,0) + NVL(G.GBD_DPST_W_NI ,0 )+ NVL(G.GBD_CR_ADJSTMNT_W_NI,0)   +NVL(G.GBD_DPST_NW_NI ,0 )+ NVL(G.GBD_CR_ADJSTMNT_NW_NI,0))    Total_deposits ");
		sb.append(" ,NVL(G.GBD_WTHDRWL_I,0) + NVL(G.GBD_DR_ADJSTMNT_I,0)+NVL(G.GBD_WTHDRWL_NI,0) + NVL(G.GBD_DR_ADJSTMNT_NI,0) Withdrawals_W_I ");
		sb.append("     ,0 	Withdrawals_NW_I   ");
		sb.append(",0 	Withdrawals_NW_I  ");
		sb.append(" , (NVL(G.GBD_WTHDRWL_I,0) + NVL(G.GBD_DR_ADJSTMNT_I,0)  +  NVL(G.GBD_WTHDRWL_NI,0) + NVL(G.GBD_DR_ADJSTMNT_NI,0)) 	Total_withdrawals ");
		sb.append(" ,NVL(G.GBD_INTRST_ON_OPNG_BLNCE,0) + NVL(GBD_INTRST_ON_CR_YR_DPST,0) + NVL(GBD_INTRST_ON_CR_ADJSTMNT,0) - NVL(GBD_INTRST_ON_DR_ADJSTMNT,0)- NVL(G.GBD_INTRST_ON_CRNT_YR_W,0) - (nvl(nw_int_2012_13,0))  Interest_W_I ");
		sb.append(" , 0 Interest_W_I ");
		sb.append(" , 0 Interest_W_NI ");
		sb.append(" , nvl(nw_int_2012_13,0) Interest_NW_I ");
		sb.append(" , 0 Interest_NW_NI ");
		sb.append(" ,NVL(G.GBD_INTRST_ON_OPNG_BLNCE,0)  + NVL(GBD_INTRST_ON_CR_YR_DPST,0) + NVL(GBD_INTRST_ON_CR_ADJSTMNT,0) - NVL(GBD_INTRST_ON_DR_ADJSTMNT,0) - NVL(G.GBD_INTRST_ON_CRNT_YR_W,0)	 Total_interest	  ");
		sb.append(" ,NVL(G.GBD_CLSNG_BLNCE_W_I,0)     +  NVL(G.GBD_CLSNG_BLNCE_W_NI,0)  - (nw_int_2009_10+nw_int_2010_11+nw_int_2012_13+nw_old_int_jun2011+nw_int_2011_12)   Closing_balance_W_I ");
		sb.append(" ,NVL(G.GBD_CLSNG_BLNCE_NW_I,0)  +  NVL(G.GBD_CLSNG_BLNCE_NW_NI,0) +(nw_int_2009_10+nw_int_2010_11+nw_int_2012_13+nw_old_int_jun2011+nw_int_2011_12)  Closing_balance_NW_I ");
		sb.append(" ,round((NVL(G.GBD_CLSNG_BLNCE_W_I,0)  +  NVL(G.GBD_CLSNG_BLNCE_W_NI,0)  + NVL(G.GBD_CLSNG_BLNCE_NW_I,0) + NVL(G.GBD_CLSNG_BLNCE_NW_NI,0)))            Total_closing_balance, ");
		sb.append(" (NVL(G.GBD_DPST_W_NI,0) + NVL(G.GBD_CR_ADJSTMNT_W_NI,0)  +NVL(G.GBD_DPST_NW_NI,0 ) +NVL( G.GBD_CR_ADJSTMNT_NW_NI,0) ) non_intt_amot ");
		sb.append(" FROM   GPF_BLNCE_DTLS G,gpf_blnce_dtls_intt i ");
		sb.append(" WHERE  i.srs_code=g.gbd_srm_srs_code and  i.gpf_nmbr=g.gbd_sbr_gpf_nmbr and ");
		sb.append(" G.GBD_SRM_SRS_CODE =:pfseries AND G.GBD_SBR_GPF_NMBR  =:gpfaccno ");
		sb.append(" AND TO_CHAR(G.GBD_GBH_YEAR, 'RRRR')  =  SUBSTR(:p_year,1,4) ");
		sb.append("  and to_char(i.year,'RRRR')=SUBSTR(:p_year,1,4) ");
		sb.append(" AND  UPPER(G.gbd_acnt_ctgry_code) = 'GPF' ");
		gLogger.info("query to select cerdit details" + sb);
		Session hibSession = getSession();
		Query selectQuery = hibSession.createSQLQuery(sb.toString());
		selectQuery.setParameter("p_year", p_year);
		selectQuery.setParameter("pfseries", pfseries);
		selectQuery.setParameter("gpfaccno", gpfaccno);
		summaryBlockDetailsList= selectQuery.list();
		gLogger.info("list size:" +summaryBlockDetailsList.size());
		return summaryBlockDetailsList;
	}
	
}
