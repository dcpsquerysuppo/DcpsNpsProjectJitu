package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Oct 30, 2012
 */

public class AddNewAdminFieldDeptDAOImpl extends GenericDaoHibernateImpl implements AddNewAdminFieldDeptDAO
{
	Session ghibSession = null; 
	public AddNewAdminFieldDeptDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getAllAdminData() throws Exception
	{
		  List lLstDept = null;
		  		 
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT locId, locName, locShortName FROM CmnLocationMst \n");
			  lSBQuery.append("WHERE departmentId = 100001 ORDER BY locName");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lLstDept = lQuery.list();
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstDept;
	}
	
	public Long getMaxLocIdAdmin() throws Exception
	{
		List lLstData = null;
		Long lLngLocId = null;
		
		try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT MAX(locId) FROM CmnLocationMst WHERE departmentId = 100001");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lLstData = lQuery.list();
			  
			  if(lLstData != null && lLstData.size() > 0){
				  lLngLocId = (Long) lLstData.get(0);
			  }
		}catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		}
		
		return (lLngLocId + 1) ;
	}
	
	public Long getMaxAdminCode() throws Exception
	{
		List lLstData = null;
		Long lLngAdminCd = null;
		
		try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT MAX(admDeptCd) FROM AdmDept");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lLstData = lQuery.list();
			  
			  if(lLstData != null && lLstData.size() > 0){
				  lLngAdminCd = (Long) lLstData.get(0);
			  }
		}catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		}
		
		return (lLngAdminCd + 1) ;
	}
	
	public List getAdminData(Long lLngLocId) throws Exception
	{
		List lLstAdminData = null;
		
		try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT CLM.locName, CLM.locShortName, AD.admBdgtCd FROM CmnLocationMst CLM, AdmDept AD \n");
			  lSBQuery.append("WHERE CLM.locId = :locID AND CLM.locId = AD.locId");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setParameter("locID", lLngLocId);
			  lLstAdminData = lQuery.list();
		}catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		}  
		
		return lLstAdminData;
	}
	
	public Long getAdminDeptCode(Long lLngLocId) throws Exception
	{
		List lLstData = null;
		Long lLngAdminCd = null;
		
		try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT admDeptCd FROM AdmDept WHERE locId = :locId");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setParameter("locId", lLngLocId);
			  lLstData = lQuery.list();
			  
			  if(lLstData != null && lLstData.size() > 0){
				  lLngAdminCd = (Long) lLstData.get(0);
			  }
		}catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		}
		
		return lLngAdminCd ;
	}
	
	public List getAllAdminDepartment() throws Exception
	{
		  List lLstDept = null;
		  List<ComboValuesVO> lLstAllDept = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT locId, locName FROM CmnLocationMst \n");
			  lSBQuery.append("WHERE departmentId = 100001");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lLstDept = lQuery.list();
			  
			  if(lLstDept != null && lLstDept.size() > 0){
				  Iterator IT = lLstDept.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[]) IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllDept.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllDept;
	}
	
	public List getAllFieldData(Long lLngAdminCode) throws Exception
	{
		  List lLstDept = null;
		  		 
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT locId, locName, locShortName, parentLocId FROM CmnLocationMst \n");
			  lSBQuery.append("WHERE departmentId = 100011 AND parentLocId = :adminCode ORDER BY locName");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setParameter("adminCode", lLngAdminCode);
			  lLstDept = lQuery.list();
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstDept;
	}
	
	public List getFieldData(Long lLngFieldId) throws Exception
	{
		  List lLstDept = null;
		  		 
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT locName, locShortName FROM CmnLocationMst \n");
			  lSBQuery.append("WHERE locId = :locId");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setParameter("locId", lLngFieldId);
			  lLstDept = lQuery.list();
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstDept;
	}
	
	public Long getMaxLocIdField() throws Exception
	{
		List lLstData = null;
		Long lLngLocId = null;
		
		try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT MAX(locId) FROM CmnLocationMst WHERE departmentId = 100011 AND locId LIKE '10%'");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lLstData = lQuery.list();
			  
			  if(lLstData != null && lLstData.size() > 0){
				  lLngLocId = (Long) lLstData.get(0);
			  }
		}catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		}
		
		return (lLngLocId + 1) ;
	}
}
