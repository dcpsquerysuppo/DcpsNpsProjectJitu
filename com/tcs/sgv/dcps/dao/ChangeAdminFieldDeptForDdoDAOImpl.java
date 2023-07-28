package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * Sep 10, 2012
 */

public class ChangeAdminFieldDeptForDdoDAOImpl extends GenericDaoHibernateImpl implements ChangeAdminFieldDeptForDdoDAO
{
	Session ghibSession = null;
	public ChangeAdminFieldDeptForDdoDAOImpl(Class type, SessionFactory sessionFactory) 
	{
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
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

	public String getAdminAndFieldDeptOfDdo(String lStrDdoCode)throws Exception
	{
		List lLstDdoDtls = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		String lStrFinalData = "";
		
		try{
			//To get Admin Department
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.deptLocCode,CLM.locName FROM OrgDdoMst ODM, CmnLocationMst CLM ");
			lSBQuery.append("WHERE ODM.ddoCode = :ddoCode AND ODM.deptLocCode = CLM.locationCode");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstDdoDtls = lQuery.list();
			
			if(lLstDdoDtls != null && lLstDdoDtls.size() > 0)
			{
				Object[] lObj = (Object[]) lLstDdoDtls.get(0);
				lStrFinalData = lObj[0].toString() + "#" + lObj[1].toString() + "#";
			}
			
			//To get Field Department
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT CLM.locName, ODM.hodLocCode FROM OrgDdoMst ODM, CmnLocationMst CLM ");
			lSBQuery.append("WHERE ODM.ddoCode = :ddoCode AND ODM.hodLocCode = CLM.locationCode");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstDdoDtls = lQuery.list();
			
			if(lLstDdoDtls != null && lLstDdoDtls.size() > 0)
			{
				Object[] lObj =  (Object[]) lLstDdoDtls.get(0);
				lStrFinalData += lObj[0].toString() + "#" + lObj[1].toString();
			}
			
		}catch(Exception e){
			logger.error("Error is: "+e, e);
		}
		
		return lStrFinalData;
	}
	
	
	public List getAllDesignation(Long lLngFieldDept)throws Exception
	{
		List lLstDesignation = null;
		
		try{			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT distinct desigDesc FROM MstDcpsDesignation ");
			lSBQuery.append("WHERE fieldDeptId = :fieldDeptId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("fieldDeptId", lLngFieldDept);
			
			lLstDesignation = lQuery.list();
		}catch(Exception e){
			logger.error("Error is: "+e, e);
		}
		
		return lLstDesignation;
	}
	
	public List getAllDesignationUsed(Long lLngFieldDept,String lStrDDOCode)throws Exception
	{
		List lLstDesignation = null;
		
		try{			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct MD.desigDesc FROM MstDcpsDesignation MD , MstEmp EM , OrgDesignationMst OD");
			lSBQuery.append(" WHERE EM.parentDept = MD.fieldDeptId ");
			lSBQuery.append(" and OD.dsgnId = MD.orgDesignationId ");
			lSBQuery.append(" and EM.designation = OD.dsgnId");
			lSBQuery.append(" and MD.fieldDeptId = :fieldDeptId ");
			lSBQuery.append(" and EM.ddoCode = :ddoCode");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("fieldDeptId", lLngFieldDept);
			lQuery.setParameter("ddoCode", lStrDDOCode);
			
			lLstDesignation = lQuery.list();
		}catch(Exception e){
			logger.error("Error is: "+e, e);
		}
		
		return lLstDesignation;
	}
	
	public List getAllCadreNames(Long lLngFieldDept)throws Exception
	{
		List lLstDesignation = null;
		
		try{			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT distinct cadreName FROM DcpsCadreMst ");
			lSBQuery.append("WHERE fieldDeptId = :fieldDeptId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("fieldDeptId", lLngFieldDept);
			
			lLstDesignation = lQuery.list();
		}catch(Exception e){
			logger.error("Error is: "+e, e);
		}
		
		return lLstDesignation;
	}
	
	public List getAllCadreNamesUsed(Long lLngFieldDept,String lStrDDOCode)throws Exception
	{
		List lLstDesignation = null;
		
		try{			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct CD.cadreName FROM DcpsCadreMst CD , MstEmp EM ");
			lSBQuery.append(" WHERE CD.cadreId = EM.cadre ");
			lSBQuery.append(" AND CD.fieldDeptId = :fieldDeptId ");
			lSBQuery.append(" AND EM.ddoCode = :ddoCode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("fieldDeptId", lLngFieldDept);
			lQuery.setParameter("ddoCode", lStrDDOCode);
			
			lLstDesignation = lQuery.list();
		}catch(Exception e){
			logger.error("Error is: "+e, e);
		}
		
		return lLstDesignation;
	}
	
	public List getAllCadreIDName(Long lLngFieldDept)throws Exception
	{
		List lLstDesignation = null;
		
		try{			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT cadreId, cadreName FROM DcpsCadreMst ");
			lSBQuery.append("WHERE fieldDeptId = :fieldDeptId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("fieldDeptId", lLngFieldDept);
			
			lLstDesignation = lQuery.list();
		}catch(Exception e){
			logger.error("Error is: "+e, e);
		}
		
		return lLstDesignation;
	}
	
	public void updateData(String lStrDdoCode, String lStrAdminDept, String lStrFieldDept, Map<Integer, String> lMapCadre)throws Exception
	{
		StringBuilder lSBQuery = null;
		Long lLngOldCadreId =  null;
		Long lLngNewCadreId =  null;
		String lStrCadreIds = "";
		String []lStrData = null;
		String lStrOldCdr = "";
		String lStrNewCdr = "";
		
		try{			
			lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE OrgDdoMst SET deptLocCode = :adminDept, hodLocCode = :fieldDept ");
			lSBQuery.append("WHERE ddoCode = :ddoCode");
			Query lQueryDdoMst = ghibSession.createQuery(lSBQuery.toString());
			lQueryDdoMst.setParameter("adminDept", lStrAdminDept);
			lQueryDdoMst.setParameter("fieldDept", lStrFieldDept);
			lQueryDdoMst.setParameter("ddoCode", lStrDdoCode);
			lQueryDdoMst.executeUpdate();
			
			
			
			lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE CMN_LOCATION_MST SET PARENT_LOC_ID = :fieldDept ");
			lSBQuery.append("WHERE LOC_ID = (SELECT LOCATION_CODE FROM ORG_DDO_MST WHERE DDO_CODE = :ddoCode)");
			Query lQueryLocMst = ghibSession.createSQLQuery(lSBQuery.toString());			
			lQueryLocMst.setParameter("fieldDept", Long.parseLong(lStrFieldDept));
			lQueryLocMst.setParameter("ddoCode", lStrDdoCode);
			lQueryLocMst.executeUpdate();
			
			
			
			lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstEmp SET parentDept = :fieldDept ");
			lSBQuery.append("WHERE ddoCode = :ddoCode)");
			Query lQueryMstEmp = ghibSession.createQuery(lSBQuery.toString());			
			lQueryMstEmp.setParameter("fieldDept", lStrFieldDept);
			lQueryMstEmp.setParameter("ddoCode", lStrDdoCode);
			lQueryMstEmp.executeUpdate();
			
			
			
			for(Integer lIntCnt = 0; lIntCnt < lMapCadre.size(); lIntCnt++)
			{
				lStrCadreIds = lMapCadre.get(lIntCnt + 1);
				lStrData = lStrCadreIds.split("#");
				lStrOldCdr = lStrData[0];
				lStrNewCdr = lStrData[1];
				
				lSBQuery = new StringBuilder();
				lSBQuery.append("UPDATE MstEmp SET cadre = :newCadre WHERE cadre = :oldCadre AND ddoCode = :ddoCode ");
				Query lQueryCadre = ghibSession.createQuery(lSBQuery.toString());
				lQueryCadre.setParameter("newCadre", lStrNewCdr);
				lQueryCadre.setParameter("oldCadre", lStrOldCdr);
				lQueryCadre.setParameter("ddoCode", lStrDdoCode);
				lQueryCadre.executeUpdate();
			}
			
			
		}catch(Exception e){
			logger.error("Error is: "+e, e);
		}
		
	}
}
