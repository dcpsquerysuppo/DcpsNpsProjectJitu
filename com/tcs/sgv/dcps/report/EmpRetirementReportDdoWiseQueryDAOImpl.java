/**
 * Class Description -
 * 
 * 
 * @author Shekhar Kadam DAT,Mumbai
 * @version 0.1
 * @since JDK 1.7 Aug 12, 2014
 */
package com.tcs.sgv.dcps.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class EmpRetirementReportDdoWiseQueryDAOImpl extends GenericDaoHibernateImpl  
{

Session ghibSession = null;
	
	public EmpRetirementReportDdoWiseQueryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
public List<Object> getEmpRetirementReport(Date lDtFromDate,Date lDtToDate,String cmbDdoList,String gStrLocCode){
		
		List<Object> lLnaEmpRetirement = new ArrayList<Object>();
		List<Object> lLstOuterDtlsList = new ArrayList<Object>();
		
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
		StringBuilder lSBQuery = new StringBuilder();
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		   Session neardrSession = sessionFactory1.openSession();

		try{
		lSBQuery.append("SELECT de.SEVARTH_ID,de.EMP_NAME,d.DSGN_NAME,OEM.EMP_DOB,OEM.EMP_SRVC_EXP ");
		lSBQuery.append("FROM ORG_EMP_MST OEM,ORG_DESIGNATION_MST d,MST_DCPS_EMP de, RLT_DDO_ORG rdo");
		lSBQuery.append(" WHERE  de.ORG_EMP_MST_ID = oem.EMP_ID and de.DDO_CODE = rdo.DDO_CODE and de.DDO_CODE = :DdoCode and ");
		lSBQuery.append(" rdo.LOCATION_CODE =:locationCode AND OEM.EMP_SRVC_EXP >= :FromDate AND OEM.EMP_SRVC_EXP <= :ToDate");
		lSBQuery.append(" AND de.DESIGNATION = d.DSGN_ID");
			
		lSBQuery.append(" ORDER BY OEM.EMP_SRVC_EXP,d.DSGN_NAME");
		
//		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		Query lQuery = neardrSession.createSQLQuery(lSBQuery.toString());
		
		lQuery.setParameter("DdoCode", cmbDdoList);
		lQuery.setParameter("locationCode", gStrLocCode);
		lQuery.setDate("FromDate", lDtFromDate);
		lQuery.setDate("ToDate", lDtToDate);
		
  lLnaEmpRetirement = lQuery.list();
		}
		catch(Exception e){
			System.out.println("Exception is"+e);
		}
		finally{
			neardrSession.close();
		}
		
		Integer lIntSrNo = 1;
		
		if (!lLnaEmpRetirement.isEmpty()) {
			Iterator<Object> it = lLnaEmpRetirement.iterator();			
			List<Object> lLstDtlsList;
		
			while (it.hasNext()) {
				lLstDtlsList = new ArrayList<Object>();
				Object[] tuple = (Object[]) it.next();
				
				lLstDtlsList.add(lIntSrNo);
				if(tuple[0] != null && tuple[0] != ""){
					lLstDtlsList.add(tuple[0]);					
				}else{
					lLstDtlsList.add("");
				}
				if(tuple[1] != null && tuple[1] != ""){
					lLstDtlsList.add(tuple[1]);					
				}else{
					lLstDtlsList.add("");
				}
				if(tuple[2] != null && tuple[2] != ""){
					lLstDtlsList.add(tuple[2]);					
				}else{
					lLstDtlsList.add("");
				}
				if(tuple[3] != null && tuple[3] != ""){
					lLstDtlsList.add(lObjSimpleDateFormat.format(tuple[3]));					
				}else{
					lLstDtlsList.add("");
				}
				if(tuple[4] != null && tuple[4] != ""){
					lLstDtlsList.add(lObjSimpleDateFormat.format(tuple[4]));					
				}else{
					lLstDtlsList.add("");
				}
				lLstOuterDtlsList.add(lLstDtlsList);
				lIntSrNo++;
			}
		}
		
		return lLstOuterDtlsList;	
		
	}
		
	
}

/************End*******************/
