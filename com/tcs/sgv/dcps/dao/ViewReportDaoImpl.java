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

public class ViewReportDaoImpl extends GenericDaoHibernateImpl implements ViewReportDAO{

	public ViewReportDaoImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		/*ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);*/


		setSessionFactory(sessionFactory);
		ghibSession = getSession();


	}

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");


	public List getAllDDOForContriForwardedToTO(String lStrTreasuryLocCode,int flag) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		//SELECT LM.LOC_ID,LM.LOC_NAME FROM CMN_LOCATION_MST LM where OFFICE_CODE=8101
		sb.append("SELECT LM.locationCode, LM.locName FROM  CmnLocationMst LM ");
		sb
		.append("where LM.officeCode=:officeCode order by LM.locationCode asc ");

		/*sb
				.append(" SELECT DISTINCT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM , TrnDcpsContribution VC ");
		sb
				.append(" WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb
				.append(" AND VC.ddoCode = DM.ddoCode AND VC.regStatus IN (1,3,4) ");*/
		//sb.append(" order by DM.ddoCode ASC ");
		gLogger.info("ddo:" +lStrTreasuryLocCode);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("officeCode", lStrTreasuryLocCode);
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
				//lObjComboValuesVO.setDesc(obj[1].toString());
				//String desc="("+obj[0].toString()+" )<![CDATA[ "+obj[1].toString()+"]]>";
				String desc="";
				if(flag==1)
					desc="<![CDATA[ ("+obj[0].toString()+" )"+obj[1].toString()+"]]>";
				else
					desc="("+obj[0].toString()+" )"+obj[1].toString();
				lObjComboValuesVO.setDesc(desc);
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
	public List getTreasuryForDDO(String lStrTreasuryLocCode) {

		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb
		.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
		.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL order by DM.ddoCode asc");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);

		lLstReturnList = new ArrayList<ComboValuesVO>();

		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());

		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc="<![CDATA[( "+obj[0].toString()+" )"+obj[1].toString()+"]]>";
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<ComboValuesVO>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public Object getCurrentBillStatus(String billNO,String year,String month) {


		StringBuilder sb = new StringBuilder();
		//SELECT LM.LOC_ID,LM.LOC_NAME FROM CMN_LOCATION_MST LM where OFFICE_CODE=8101
		sb.append("SELECT count(1) FROM paybill_head_mpg where bill_no="+billNO+" and paybill_month="+month+" and paybill_year="+year+" and APPROVE_FLAG=1");
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		Object lLstResult = selectQuery.uniqueResult();
		return lLstResult;
	}


	public List getSubTreasury(long treasuryId){
		List temp=null;
		try
		{		
			String branchQuery = "SELECT loc_id,loc_name from CMN_LOCATION_MST where loc_id="+treasuryId +" OR ( DEPARTMENT_ID= 100006 and PARENT_LOC_ID="+treasuryId+") order by loc_id asc";
			Query sqlQuery= ghibSession.createSQLQuery(branchQuery);
			logger.error("---------------------get getSubTreasury query"+sqlQuery.toString());
			temp= sqlQuery.list();
			logger.error("---------------------getSubTreasury"+temp.size());

		}
		catch(Exception e){
			logger.error("Error in ReportingDDODaoImpl \n " + e);
			e.printStackTrace();
		}
		return temp;
	}
	//added by sunitha for approved billgroups for AG
	public List getApprovedBillForAG(Long selYear,Long selMonth,String ddoCode) {
		logger.info(" in getApprovedBillsForAG");
		Session session = getSession();
		
		List lLstReturnList=null;
		StringBuffer sb = new StringBuffer();
		sb.append("select  s.BILL_GROUP_ID,s.DESCRIPTION from mst_dcps_bill_group s inner join PAYBILL_HEAD_MPG mpg on s.BILL_GROUP_ID = mpg.BILL_NO where  mpg.APPROVE_FLAG = 1 and mpg.PAYBILL_MONTH="+selMonth+" and mpg.PAYBILL_YEAR = "+selYear+" and s.DDO_CODE ="+ddoCode+" ");
		logger.info("Query for get getApprovedBillsForAG is---->>>>"+sb.toString());
		Query sqlQuery=session.createSQLQuery(sb.toString());	
		if(sqlQuery.list()!=null && !sqlQuery.list().isEmpty()){

			lLstReturnList = sqlQuery.list();
		}

		return lLstReturnList;

	}
	//START added by samadhan
	public List getVoucherDtlsForAG(Long selYear, Long selMonth,String ddoCode, Long selBillId) {
		logger.info(" in getApprovedBillsForAG");
		Session session = getSession();
		
		List lLstReturnList=null;
		StringBuffer sb = new StringBuffer();
		sb.append("select  s.BILL_GROUP_ID,s.DESCRIPTION  " );
		sb.append(" ,(case when mpg.VOUCHER_NO = 0 then 'Not Available' else cast(mpg.VOUCHER_NO as varchar) end)," );
		sb.append(" VARCHAR_FORMAT(mpg.VOUCHER_DATE,'dd/MM/yyyy')    ");
		sb.append(" from mst_dcps_bill_group s inner join PAYBILL_HEAD_MPG mpg on s.BILL_GROUP_ID = mpg.BILL_NO where  mpg.APPROVE_FLAG = 1 and mpg.PAYBILL_MONTH="+selMonth+" and mpg.PAYBILL_YEAR = "+selYear+" and s.DDO_CODE ="+ddoCode+"  ");
		sb.append(" and s.BILL_GROUP_ID = '"+selBillId+"' ");
		
		logger.info("Query for get getVoucherDtlsForAG is---->>>>"+sb.toString());
		Query sqlQuery=session.createSQLQuery(sb.toString());	
		if(sqlQuery.list()!=null && !sqlQuery.list().isEmpty()){

			lLstReturnList = sqlQuery.list();
		}

		return lLstReturnList;

	}
	//END added by samadhan
}
