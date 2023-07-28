package com.tcs.sgv.lna.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LNAMotorAdvanceDAOImpl extends GenericDaoHibernateImpl implements LNAMotorAdvanceDAO {
	Session ghibSession = null;

	public LNAMotorAdvanceDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getMotorAdvance(String lStrSevaarthId, Long lLngRequestType) {
		List motorcarAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select motorAdvanceId");
		lSBQuery.append(" FROM MstLnaMotorAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag in ('D','R') AND advanceType = :RequestType");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("RequestType", lLngRequestType);
		motorcarAdvanceList = lQuery.list();
		return motorcarAdvanceList;
	}

	public List getMotorAdvanceToDEOApprover(Long lLngMotorAdvnId) {
		List motorcarAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select motorAdvanceId");
		lSBQuery.append(" FROM MstLnaMotorAdvance");
		lSBQuery.append(" WHERE motorAdvanceId = :MotorAdvnId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("MotorAdvnId", lLngMotorAdvnId);
		motorcarAdvanceList = lQuery.list();
		return motorcarAdvanceList;
	}

	public Double getMotorSubtype(String lStrSevaarthId)
	{
		List<Double> motorcarAdvanceList = new ArrayList<Double>();
		double subtype=0;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT ADVANCE_SUB_TYPE FROM MST_LNA_MOTOR_ADVANCE");
		lSBQuery.append(" where SEVAARTH_ID=:sevaarthId and STATUS_FLAG='A'");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		motorcarAdvanceList = lQuery.list();
		if(motorcarAdvanceList != null)
		{
			if(motorcarAdvanceList.size()!= 0)
			{
				if(motorcarAdvanceList.get(0) != null)
				{
					 subtype = motorcarAdvanceList.get(0);
				}
			}
		}
		else
		{
			subtype=0;
		}
		return subtype;
	}
	
	public Boolean requestCheck(String lStrSevaarthId, Long lLngRequestType) {
		List<Integer> motorcarAdvanceList =new ArrayList<Integer>();
		int count=0;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select count(motor_Advance_Id)");
		lSBQuery.append(" FROM Mst_Lna_Motor_Advance");
		lSBQuery.append(" WHERE sevaarth_Id = :sevaarthId AND status_Flag = 'A' AND advance_Type = :RequestType");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("RequestType", lLngRequestType);
		motorcarAdvanceList = lQuery.list();
		count=motorcarAdvanceList.get(0);
		
		if (count<2) {
			return false;
		} else {
			return true;
		}
	}
	
	public Boolean requestDataAlreadyExists(String lStrSevaarthId, Long lLngRequestType) {
		List motorcarAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select motorAdvanceId");
		lSBQuery.append(" FROM MstLnaMotorAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag = 'A' AND advanceType = :RequestType");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("RequestType", lLngRequestType);
		motorcarAdvanceList = lQuery.list();
		if (motorcarAdvanceList.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public Boolean requestPendingStatus(String lStrSevaarthId) {
		List motorcarAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select motorAdvanceId");
		lSBQuery.append(" FROM MstLnaMotorAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag = 'F'");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		motorcarAdvanceList = lQuery.list();
		if (motorcarAdvanceList.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

}
