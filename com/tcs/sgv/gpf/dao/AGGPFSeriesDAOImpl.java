package com.tcs.sgv.gpf.dao;

/**
 * @author Niteesh Kumar Bhargava
 * @version 0.1
 * @since JDK 1.7 Sep 9, 2014
 * 
 * This is Data Access Layer (DAO)class implements the methods 
 * for AG and GPF series regarding functions
 * Place new functions in this,
 * if they perform some direct functions on GPF Series
 */

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.directwebremoting.util.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction; 

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.history.dao.MaintainHistoryDaoImpl;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;


public class AGGPFSeriesDAOImpl extends GenericDaoHibernateImpl implements AGGPFSeriesDAO {
  
	Session hibSession=null; 
	SessionFactory factory=null;//This is hibernate session
	Logger logger=null; 
	StringBuilder strQuery=null;
	RltDcpsPayrollEmp RltDcpsPayrollEmpObj = null;
	List listPFSeries = null;
	Query query=null; 
	Transaction transaction=null;
	
	public AGGPFSeriesDAOImpl(Class type,SessionFactory sessionFactory) {
		super(type);
		factory=sessionFactory;
		hibSession=factory.getCurrentSession();
		logger = Logger.getLogger(this.getClass());
		strQuery= new StringBuilder();
	}
	@Override
	public List getObsoletePFSeriesFromAGLookupId(String agLookupId)
			throws Exception {
		strQuery.setLength(0);
		strQuery.append("SELECT lookup_id, lookup_name FROM Cmn_Lookup_Mst where actv=17 AND  parent_Lookup_Id = (SELECT PF_PARENT_LOOKUP_ID from AG_PF_SERIES_RLT where AG_LOOKUP_ID = "+agLookupId+")");
		query= hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());
		return query.list();
	}
	@Override
	public List getActivePFSeriesFromAGLookupId(String agLookupId)
			throws Exception {
		strQuery.setLength(0);
		strQuery.append("SELECT lookup_id, lookup_name FROM Cmn_Lookup_Mst where actv<>17 AND  parent_Lookup_Id = (SELECT PF_PARENT_LOOKUP_ID from AG_PF_SERIES_RLT where AG_LOOKUP_ID = "+agLookupId+")");
		query= hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());
		return query.list();
	}
	@Override
	public List getPFSeriesFromAGLookupId(String agLookupId) throws Exception {
		strQuery.setLength(0);
		strQuery.append("SELECT lookup_id, lookup_name FROM Cmn_Lookup_Mst where  parent_Lookup_Id = (SELECT PF_PARENT_LOOKUP_ID from AG_PF_SERIES_RLT where AG_LOOKUP_ID = "+agLookupId+")");
		query= hibSession.createSQLQuery(strQuery.toString());
		logger.info("getPFSeriesFromAGLookupId---------"+query.toString());
		return query.list();
		} 
	@Override
	public List getPFSeries(long parentLookupId) throws Exception {
		strQuery.setLength(0);
		strQuery.append("SELECT lookup_id, lookup_name FROM Cmn_Lookup_Mst where  parent_Lookup_Id = "+parentLookupId);
		query= hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());	
		return query.list();
	}
	
	@Override
	public List getObsoleteAGList() throws Exception {
		strQuery.setLength(0);
		strQuery.append("SELECT O1.lookup_id,O1.lookup_name FROM Cmn_Lookup_Mst O1, Cmn_Lookup_Mst O2 WHERE O1.parent_Lookup_Id = O2.lookup_Id AND O2.lookup_Name = 'AccountMaintaindedBy' AND O1.actv=17  ORDER BY O1.order_No,O1.lookup_Id");
		query= hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());
		return query.list();
	}


	@Override
	public List getActiveAGList() throws Exception {
		strQuery.setLength(0);
		strQuery.append("SELECT O1.lookup_id,O1.lookup_name FROM Cmn_Lookup_Mst O1, Cmn_Lookup_Mst O2 WHERE O1.parent_Lookup_Id = O2.lookup_Id AND O2.lookup_Name = 'AccountMaintaindedBy' AND O1.actv!=17  ORDER BY O1.order_No,O1.lookup_Id");
		query= hibSession.createSQLQuery(strQuery.toString());
		logger.info("getActiveAGList------"+query.toString());	
		return query.list();
	}
	
	@Override
	public int activateAG(String lookupId) {
		try{
			strQuery.setLength(0);
			strQuery.append("update CMN_LOOKUP_MST set actv=1 where LOOKUP_ID = "+lookupId);
			query= hibSession.createSQLQuery(strQuery.toString());
			logger.info(query.toString()); 
			query.executeUpdate();
			return 1; //AG obsoleted successfully
			}catch(Exception cve){
				return 2; //AG not obsoleted
		}
	}

	
	@Override
	public int obsolateAG(String lookupId) {
		try{
			strQuery.setLength(0);
			strQuery.append("update CMN_LOOKUP_MST set actv=17 where LOOKUP_ID = "+lookupId);
			query= hibSession.createSQLQuery(strQuery.toString());
			logger.info(query.toString()); 
			query.executeUpdate();
			return 1; //AG obsoleted successfully
			}catch(Exception cve){
				return 2; //AG not obsoleted
		}
	}
	
	
	@Override
	public int upDateAGName(String lookupId, String newName) {
		try{
		
		strQuery.setLength(0);
		strQuery.append("update CMN_LOOKUP_MST set LOOKUP_NAME = '"+newName+"' , LOOKUP_SHORT_NAME = '"+newName+"' , LOOKUP_DESC = '"+newName+"' where LOOKUP_ID = "+lookupId);
		query= hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString()); 
		query.executeUpdate();
		return 1;
		}catch(org.hibernate.exception.DataException cve){
			return 3; //AG name is too long
	}
	}

	@Override
	public int upDatePFName(String lookupId, String newName) {
		try{
			
			strQuery.setLength(0);
			strQuery.append("update CMN_LOOKUP_MST set LOOKUP_NAME = '"+newName+"' , LOOKUP_SHORT_NAME = '"+newName+"' , LOOKUP_DESC = '"+newName+"' where LOOKUP_ID = "+lookupId);
			query= hibSession.createSQLQuery(strQuery.toString());
			logger.info(query.toString()); 
			query.executeUpdate();
			return 1;
			}catch(org.hibernate.exception.DataException cve){
				return 3; //AG name is too long
		}
	}
	
	@Override
	public long getPFPrntLkpIdFromAGLkpId(String agLookupId) {
		long pfParentLookupId=0;
		java.math.BigInteger temp=null;
		strQuery.setLength(0);
		logger.info("code changed");
		strQuery.append("SELECT * from AG_PF_SERIES_RLT where AG_LOOKUP_ID = "+agLookupId);
		query= hibSession.createSQLQuery(strQuery.toString());
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		logger.info(query.toString());
		List list = query.list();
		 for(Object object : list)
         {
            Map row = (Map)object;
            temp = (BigInteger) row.get("PF_PARENT_LOOKUP_ID");
            pfParentLookupId=temp.longValue();
            logger.info(" pfParentLookupId is "+ pfParentLookupId);
         }
		
		return pfParentLookupId;
		
	}
	
	
	@Override
	public List getAGList() throws Exception {
		strQuery.setLength(0);
		strQuery.append("SELECT O1 FROM CmnLookupMst O1, CmnLookupMst O2 WHERE O1.parentLookupId = O2.lookupId AND O2.lookupName = 'AccountMaintaindedBy'   ORDER BY O1.orderNo,O1.lookupId");
		query= hibSession.createQuery(strQuery.toString());
		logger.info(query.toString());
		return query.list();
	}

	
	@Override
	public int addAG(String agName){

			try{
		strQuery.setLength(0);
		strQuery.append("INSERT INTO CMN_LOOKUP_MST VALUES((select max(LOOKUP_ID)+1 from CMN_LOOKUP_MST),700091,'"+agName+"','"+agName+"','"+agName+"',1,1,1, sysdate , null , null , null , 1 , 1)");
		query=hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());
		query.executeUpdate(); //add this line  
		
		strQuery.setLength(0);
		strQuery.append("INSERT INTO CMN_LOOKUP_MST  VALUES ((select max(LOOKUP_ID)+1 from CMN_LOOKUP_MST), -1 , 'PF_"+agName+"' , 'PF_"+agName+"','PF_"+agName+"' , 1 , 1 , 1 , sysdate , null , null , null , 0 , 1)");
		query=hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());
		query.executeUpdate();
		
		strQuery.setLength(0);
		strQuery.append("INSERT INTO AG_PF_SERIES_RLT VALUES ((select max(LOOKUP_ID)-1 from CMN_LOOKUP_MST),(select max(LOOKUP_ID) from CMN_LOOKUP_MST))");
		query=hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());
		query.executeUpdate();
		return 1;
		}catch(org.hibernate.exception.ConstraintViolationException cve){
			
			logger.info("AG to be added is already registered");
			return 2; //AG Already Exist
		}
		
		catch(org.hibernate.exception.DataException cve){
				return 3; //AG name is too long
		}
			
		catch(Exception e){
				e.printStackTrace();
				return -1;
			}
	}
	
	
	@Override
	public int addPFSeries(Long agLookupId,String pFSeriesName) {
		try{
			strQuery.setLength(0);
			
	strQuery.append("INSERT INTO CMN_LOOKUP_MST VALUES ((select max(LOOKUP_ID)+1 from CMN_LOOKUP_MST) , (select PF_PARENT_LOOKUP_ID from AG_PF_SERIES_RLT where AG_LOOKUP_ID = "+agLookupId+") , '"+pFSeriesName+"' , '"+pFSeriesName+"' , '"+pFSeriesName+"' , 1 , 1 , 1 , sysdate , 1 , 1 , sysdate , 0 , 1)");
	query=hibSession.createSQLQuery(strQuery.toString());
	logger.info(query.toString());
	query.executeUpdate(); //add this line  
	return 1;
	}
		catch(org.hibernate.exception.ConstraintViolationException cve){
			return 2; //PF Series Already Exist
		}
		
		catch(org.hibernate.exception.DataException cve){
			return 3; //PF Series name is too long
		}
		
	catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	@Override
	public Serializable create(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List getListByColumnAndValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getListByColumnAndValue(String[] arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getListByColumnAndValue(String[] arg0, Object[] arg1,
			String[] arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getListByColumnAndValue(String[] arg0, Object[] arg1,
			String[] arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getListForColumnByValues(String arg0, List arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(Serializable arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSessionFactory(SessionFactory arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateWithHistory(Object arg0, MaintainHistoryDaoImpl arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
