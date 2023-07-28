/**
 * Class Description -
 * 
 * 
 * @author Niteesh Kumar Bhargava and Shekhar Kadam DAT, Mumbai
 * @version 0.1
 * @since JDK 1.7 Aug 12, 2014
 */

package com.tcs.sgv.retirement.dao;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.util.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jfree.util.Log;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.history.dao.MaintainHistoryDaoImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

public class RetirementDAOImpl extends GenericDaoHibernateImpl implements RetirementDAO{

	Session hibSession=null; 
	SessionFactory factory=null;//This is hibernate session
	Logger logger=null;
	StringBuilder strQuery= null;
	
	
	public RetirementDAOImpl(Class type,SessionFactory sessionFactory) {
		super(type);
		factory=sessionFactory;
		hibSession=factory.getCurrentSession();
		logger = Logger.getLogger(this.getClass());
		strQuery = new StringBuilder();
		// TODO Auto-generated constructor stub
	}
	
	
	 public List getDDOCode_locNameByLocId(String locationId) {
	        strQuery.setLength(0);
	        strQuery.append("select d.ddo_code as DDOCODE,l.loc_name as LOCNAME from ORG_DDO_MST d, CMN_LOCATION_MST l");
	        strQuery.append(" where d.LOCATION_CODE = "+locationId+" and d.LOCATION_CODE = l.LOC_ID");
	        
	        Query query = hibSession.createSQLQuery(strQuery.toString());
	        return query.list();
	    }
	

	@Override
	public List getRetirementListByDDOCode(String locationId) throws Exception {
		
		
		strQuery.setLength(0);
		strQuery.append("SELECT de.SEVARTH_ID,de.EMP_NAME,d.DSGN_NAME,OEM.EMP_DOB,OEM.EMP_SRVC_EXP ");
		strQuery.append(" FROM  ORG_EMP_MST OEM, ORG_DESIGNATION_MST d, MST_DCPS_EMP de,org_ddo_mst odm ");
		strQuery.append(" WHERE de.ORG_EMP_MST_ID = oem.EMP_ID and de.DDO_CODE = odm.DDO_CODE and de.DDO_CODE =(select DDO_CODE from ORG_DDO_MST where LOCATION_CODE ="+locationId+")"); 
		strQuery.append(" and de.loc_id ="+locationId+"  and month(OEM.EMP_SRVC_EXP) = month(sysdate)"); 
		strQuery.append(" AND year(OEM.EMP_SRVC_EXP) = year(sysdate)  AND de.DESIGNATION = d.DSGN_ID ");
		strQuery.append(" ORDER BY OEM.EMP_SRVC_EXP,d.DSGN_NAME");
		
		Query query= hibSession.createSQLQuery(strQuery.toString());
		logger.info(query.toString());
		return query.list();
		
		
        //Query query= hibSession.createSQLQuery("select DDO_ID,DDO_CODE, DDO_NAME,DDO_PERSONAL_NAME,POST_ID from org_ddo_mst order by DDO_ID fetch first 17 rows only");
        
        //return query.list();
		
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
