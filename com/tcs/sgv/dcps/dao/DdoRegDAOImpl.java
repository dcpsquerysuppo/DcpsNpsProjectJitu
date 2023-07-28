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
import com.tcs.sgv.dcps.dao.DdoRegDao;


public class DdoRegDAOImpl extends GenericDaoHibernateImpl implements DdoRegDao{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionproc/PensionCaseConstants");

	public DdoRegDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);


}







@Override
public List getDdoGeneralReport(){
	
	List lLstResult = null;
	//Map fieldMap=new HashMap();
	StringBuffer lSBQuery = new StringBuffer();
	
	try {		
		logger.info("Enter in getDdoGeneralReport ");
		
		
		lSBQuery.append(" SELECT loc.loc_id,loc.loc_name,count(ddo.ddo_code) as total_ddo,count(reg.ddo_code) as total_reg_ddo from org_ddo_mst ddo  ");
		lSBQuery.append(" inner join cmn_location_mst loc on substr(loc.loc_id,1,2)=substr(ddo.ddo_code,1,2) and loc.department_id=100003  ");
		lSBQuery.append(" left join mst_ddo_reg reg on reg.ddo_code=ddo.ddo_code  ");
		lSBQuery.append(" group by loc_id,loc_name  ");
			
	
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	  lLstResult = lQuery.list();
			
	  logger.info(" Total List size of Report is  ******.. "+lLstResult.size());	   	    	 			 			 		
	
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getDdoGeneralReport(string) : " + e, e);
	}
	return lLstResult;
}







@Override
public List getDdoDetailForReg(String ddoCode) {
	List ddoDetails=null;
	StringBuffer br=new StringBuffer();
	try{
		logger.info("Inside getDdoDetailForReg");
		br.append("");
		
		br.append(" SELECT zp.region_name,loc.loc_name,loc.loc_id,dto.dto_code,ddo.ddo_code,nvl(reg.ddo_reg_no,' ') as ddoReg FROM org_ddo_mst ddo     ");
		br.append(" inner join cmn_location_mst loc on substr(loc.loc_id,1,2)=substr(ddo.ddo_code,1,2) and loc.department_id=100003    ");
		br.append(" left join CMN_LOCATION_MST cmn on cmn.LOC_ID=ddo.DEPT_LOC_CODE    ");
		br.append(" left join CMN_LOCATION_MST cmnloc on cmnloc.LOC_ID=ddo.HOD_LOC_CODE    ");
		br.append(" inner join mst_dto_reg dto on dto.loc_id=loc.loc_id    ");
		br.append(" left join MST_DCPS_DDO_OFFICE office on office.DDO_CODE=ddo.DDO_CODE and upper(office.ddo_office)='YES'   ");
		br.append(" left join CMN_DISTRICT_MST dis  on dis.DISTRICT_ID=loc.loc_DISTRICT_id   ");
		br.append(" left join  ZP_REGION_NAME_MST zp on zp.REGION_CODE = dis.REGION_CODE    ");
		br.append(" left join mst_ddo_reg reg on reg.ddo_code=ddo.ddo_code    ");
		br.append(" where ddo.ddo_code='"+ddoCode+"'    ");
			
			

		
			
		Query qr=ghibSession.createSQLQuery(br.toString());
		ddoDetails=qr.list();
		
	}catch (Exception e) {
		logger.info("Exception :"+ e,e);
	}
	return ddoDetails;
}







@Override
public List isDdoRegExist(String ddo_reg_no) {
	List countReg=null;
	//int =0;
	StringBuffer br=new StringBuffer();
	try{
		logger.info("Inside getDdoDetailForReg");
		br.append("");
		


		br.append(" SELECT ddo_code,DDO_OFFICE_NAME,DDO_REG_NO FROM mst_ddo_reg where ddo_reg_no='"+ddo_reg_no+"' ");
	
		
			
			
		Query qr=ghibSession.createSQLQuery(br.toString());
		countReg=qr.list();
		
		//countReg=Integer.parseInt(ddoRegDetails.get(0).toString());
	}catch (Exception e) {
		logger.info("Exception :"+ e,e);
	}
	return countReg;
}







@Override
public void registerDDO(String ddo_reg_no, String ddocode) {
	logger.info("ddocode$$$$$$$$"+ddocode);
	logger.info("ddo_reg_no$$$$$$$$"+ddo_reg_no);
	List ddoRegDetails=null;
	int p=0;
	int countReg=0;
	StringBuffer br=new StringBuffer();
	try{
		logger.info("Inside getDdoDetailForReg");
		br.append("");
		


		br.append(" INSERT INTO mst_ddo_reg  VALUES ("+ddocode+",substr((SELECT off_name FROM MST_DCPS_DDO_OFFICE where ddo_code="+ddocode+" and upper(ddo_office)='YES'),1,90),'"+ddo_reg_no+"',(SELECT dto_reg_no FROM mst_dto_reg where substr(dto_code,1,2)=substr("+ddocode+",1,2)),sysdate, 'Newly Registered')");
	
		
			
			
		Query qr=ghibSession.createSQLQuery(br.toString());
		p=qr.executeUpdate();
		
		countReg=Integer.parseInt(ddoRegDetails.get(0).toString());
	}catch (Exception e) {
		logger.info("Exception :"+ e,e);
	}
	//return ;
	
}

	




}




