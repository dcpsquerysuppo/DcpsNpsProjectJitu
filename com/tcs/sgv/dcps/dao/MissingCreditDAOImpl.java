package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.R1_InputDAO;

public class MissingCreditDAOImpl extends GenericDaoHibernateImpl implements MissingCreditDAO
{
	Session ghibSession = null;
	public MissingCreditDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List getAllDdoCode(String LocCode) throws Exception
	{
		  List lLstDdoCode = null;
		  List<ComboValuesVO> lLstAllDdoCode = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT RDO.ddoCode, ODM.ddoName FROM RltDdoOrg RDO, OrgDdoMst ODM ");
			  lSBQuery.append("WHERE RDO.locationCode = :locCode AND ODM.ddoCode = RDO.ddoCode ORDER BY RDO.ddoCode");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setParameter("locCode", LocCode);
			  String dispDDo=null;
			  lLstDdoCode = lQuery.list();
			  
			  if(lLstDdoCode != null && lLstDdoCode.size() > 0){
				  Iterator IT = lLstDdoCode.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  
				  lLstAllDdoCode.add(cmbVO);
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[]) IT.next();
					  cmbVO.setId(lObj[0].toString());
					  dispDDo=lObj[0].toString()+" - "+lObj[1].toString();
					  cmbVO.setDesc(dispDDo);
					  lLstAllDdoCode.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllDdoCode;
	}
	
	public List getAllTreasury() throws Exception
	{
		  List lLstDept = null;
		  List<ComboValuesVO> lLstAllDept = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT locId, locName FROM CmnLocationMst \n");
			  lSBQuery.append("WHERE departmentId in (100003,100006) ORDER BY locId");
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
}
