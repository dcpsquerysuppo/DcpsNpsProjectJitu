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


public class NsdlReportsDAOImpl extends GenericDaoHibernateImpl implements NsdlReportsDAO{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionproc/PensionCaseConstants");

	public NsdlReportsDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);


}



public List getFinyears() {

	String query = "select finYearCode,finYearCode from SgvcFinYearMst where finYearCode between '2015' and '2019' order by finYearCode ASC";
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



@Override
public List getDetails(String lstrFinYear, String lstrMonth, String treasuryId, String pranWise){
	
	
	List lLstResult = null;
	//Map fieldMap=new HashMap();
	StringBuffer lSBQuery = null;
	
	try {		
		logger.info("Enter in DemoReportDAO Impl with getTotalDDOCountEnter : ");
		
		lSBQuery = new StringBuffer(" SELECT loc.loc_id,loc.loc_name,dto.dto_reg_no,count(distinct ddo.DDO_CODE),count(distinct hreis.emp_id),sum(paybill.DED_ADJUST) AS employye,     ");
		lSBQuery.append("  sum(paybill.DED_ADJUST),count(distinct head.PAYBILL_ID),sum(paybill.NET_TOTAL),sum(paybill.GROSS_AMT) FROM PAYBILL_HEAD_MPG head   inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID   ");
		lSBQuery.append("   inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID   ");
		lSBQuery.append("   inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID  ");
		lSBQuery.append("  inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code    ");
		lSBQuery.append("   inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2)  ");
		lSBQuery.append("   inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)   ");
	//lSBQuery.append("   left outer join NSDL_BH_DTLS nsdl on substr(nsdl.file_name,1,4)=loc.LOC_ID and nsdl.TRANSACTION_ID is null and nsdl.year= :yearId and nsdl.MONTH= :monthId   ");
	
		lSBQuery.append("   where head.PAYBILL_YEAR= :yearId and head.PAYBILL_MONTH= :monthId   and head.APPROVE_FLAG=1   ");
		if(pranWise.equals("true1")){
			lSBQuery.append(" and mstemp.PRAN_NO not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.TRANSACTION_ID is not null and bh.STATUS <>-1 and bh.YEAR = '"+lstrFinYear+"' and bh.MONTH='"+lstrMonth+"' and bh.file_name like '"+treasuryId+"%')");
		
			
			lSBQuery.append("   and mstemp.PRAN_NO is not null " );
		}else{
			lSBQuery.append(" and ((mstemp.PRAN_NO not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.TRANSACTION_ID is not null and bh.STATUS <>-1 and bh.YEAR = '"+lstrFinYear+"' and bh.MONTH='"+lstrMonth+"' and bh.file_name like '"+treasuryId+"%')) or (mstemp.PRAN_NO is null))");
		}
		
		// and mstemp.DCPS_OR_GPF='Y'  and mstemp.AC_DCPS_MAINTAINED_BY=700174
		lSBQuery.append("    and ((loc.department_id=100006 and loc.PARENT_LOC_ID=:loc_id ) or loc.LOC_ID= :loc_id)   ");
		lSBQuery.append("   group by loc.loc_id,loc.loc_name,dto.dto_reg_no having sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) >0  order by loc.loc_id   ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loc_id", treasuryId);
	lQuery.setParameter("yearId", lstrFinYear);
	lQuery.setParameter("monthId", lstrMonth);
	logger.info("==> :: "+lQuery);
	logger.info("==>  :: "+lSBQuery.toString());
	  lLstResult = lQuery.list();
			
	  logger.info(" Total List size of DDo count entered  ******.. "+lLstResult.size());	   	    	 			 			 		
	
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getTotalDDOCountEnter(string) : " + e, e);
	}
	return lLstResult;
}



@Override
public String getStatusOfDist(String strLocationCode) {
	Session hibSession = getSession();
	StringBuffer strBuff = new StringBuffer();
	strBuff.append(" SELECT cast(Allowed_or_not_nsdl as varchar(5)) from cmn_district_mst where district_id in (select distinct LOC_DISTRICT_ID from CMN_LOCATION_MST where loc_id like '"+strLocationCode.substring(0,2)+"%' and DEPARTMENT_ID=100006) ");
	Query query = hibSession.createSQLQuery(strBuff.toString());
	logger.info("getStatusOfDist isquery **************************** "+query.toString());
	logger.info("getStatusOfDist size is *************************** "+query.list().size());
	return  (String) query.list().get(0);
	
}

	
}




