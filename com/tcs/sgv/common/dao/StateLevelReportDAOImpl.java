package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class StateLevelReportDAOImpl extends GenericDaoHibernateImpl
{
	Session ghibSession = null;
	public StateLevelReportDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List getAllDepartment() throws Exception
	  {
		  List lLstDept = null;
		  List<ComboValuesVO> lLstAllDept = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT departmentId, depName FROM OrgDepartmentMst \n");			  
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
	  
	  public List getAllDistrict() throws Exception
	  {
		  List lLstDist = null;
		  List<ComboValuesVO> lLstAllDist = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT districtId, districtName FROM CmnDistrictMst \n");
			  lSBQuery.append("WHERE cmnLanguageMst.langId = :langId AND cmnStateMst.stateId = :stateId");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setLong("langId", 1);
			  lQuery.setLong("stateId",15);
			  lLstDist = lQuery.list();
			  
			  if(lLstDist != null && lLstDist.size() > 0){
				  Iterator IT = lLstDist.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[])IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllDist.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllDist;
	  }
	  
	  public List getAllOffices(String lStrDistrictId) throws Exception
	  {
			ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
			ComboValuesVO cmbVO;
			Object[] obj;
	
			String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice WHERE dcpsDdoOfficeDistrict =:district";
	
			StringBuilder sb = new StringBuilder();
			sb.append(query);
			Query selectQuery = ghibSession.createQuery(sb.toString());
			selectQuery.setParameter("district", lStrDistrictId);
			List resultList = selectQuery.list();
	
			cmbVO = new ComboValuesVO();
	
			if (resultList != null && resultList.size() > 0) {
				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("-- Select --");
				finalList.add(cmbVO);
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					finalList.add(cmbVO);
				}
			}
	
			return finalList;
	}
	  
	public List getAllFinyear() throws Exception
	{
		List lLstDist = null;
		List<ComboValuesVO> lLstAllFinYear = new ArrayList<ComboValuesVO>();
		Session ghibSession = getSession();
		try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT finYearId, finYearCode FROM SgvcFinYearMst \n");
			  lSBQuery.append("WHERE finYearId BETWEEN 23 AND 38 ");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());			  
			  lLstDist = lQuery.list();
			  
			  if(lLstDist != null && lLstDist.size() > 0){
				  Iterator IT = lLstDist.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[])IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllFinYear.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllFinYear;
	  }
	
	public List getAllMonth() throws Exception
	{
		List lLstDist = null;
		List<ComboValuesVO> lLstAllMonth = new ArrayList<ComboValuesVO>();
		Session ghibSession = getSession();
		try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT monthNo, monthName FROM SgvaMonthMst \n");
			  lSBQuery.append("WHERE langId = 'en_US' ");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());			  
			  lLstDist = lQuery.list();
			  
			  if(lLstDist != null && lLstDist.size() > 0){
				  Iterator IT = lLstDist.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[])IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllMonth.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllMonth;
	  }
}
