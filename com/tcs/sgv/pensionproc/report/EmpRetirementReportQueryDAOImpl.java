package com.tcs.sgv.pensionproc.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class EmpRetirementReportQueryDAOImpl  extends GenericDaoHibernateImpl {

	Session ghibSession = null;
	
	public EmpRetirementReportQueryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List<Object> getEmpRetirementReport(Date lDtFromDate,Date lDtToDate,String lStrDesignation,String lStrDdoCode){
		
		List<Object> lLnaEmpRetirement = new ArrayList<Object>();
		List<Object> lLstOuterDtlsList = new ArrayList<Object>();
		
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append("SELECT HEM.SEVARTH_EMP_CD,OEM.EMP_FNAME || ' ' || OEM.EMP_MNAME || ' ' || OEM.EMP_LNAME,d.DSGN_NAME,OEM.EMP_SRVC_EXP");
		lSBQuery.append(" FROM HR_EIS_EMP_MST HEM,ORG_EMP_MST OEM,ORG_DESIGNATION_MST d,ORG_POST_DETAILS_RLT p,ORG_USERPOST_RLT u,MST_DCPS_EMP de");
		lSBQuery.append(" WHERE  de.ORG_EMP_MST_ID = oem.EMP_ID and de.DDO_CODE = :DdoCode and ");
		lSBQuery.append(" HEM.EMP_MPG_ID = OEM.EMP_ID AND OEM.EMP_SRVC_EXP >= :FromDate AND OEM.EMP_SRVC_EXP <= :ToDate");
		lSBQuery.append(" AND OEM.USER_ID = u.USER_ID AND u.POST_ID = p.POST_ID AND p.DSGN_ID = d.DSGN_ID");
		
		if(!"".equals(lStrDesignation) && lStrDesignation != null)
			lSBQuery.append(" AND upper(d.DSGN_NAME) like :Designation ");
		
		lSBQuery.append(" ORDER BY OEM.EMP_SRVC_EXP,d.DSGN_NAME");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		lQuery.setParameter("DdoCode", lStrDdoCode);
		lQuery.setDate("FromDate", lDtFromDate);
		lQuery.setDate("ToDate", lDtToDate);
		
		if(!"".equals(lStrDesignation) && lStrDesignation != null)
			lQuery.setParameter("Designation", lStrDesignation.toUpperCase() +"%");
		
		lLnaEmpRetirement = lQuery.list();
		
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
				lLstOuterDtlsList.add(lLstDtlsList);
				lIntSrNo++;
			}
		}
		
		return lLstOuterDtlsList;	
		
	}
}
