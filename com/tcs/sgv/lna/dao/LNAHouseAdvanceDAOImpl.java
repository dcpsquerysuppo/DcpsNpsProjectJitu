package com.tcs.sgv.lna.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LNAHouseAdvanceDAOImpl extends GenericDaoHibernateImpl implements LNAHouseAdvanceDAO {
	Session ghibSession = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public LNAHouseAdvanceDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getHouseAdvance(String lStrSevaarthId, Long lLngRequestType) {
		List houseAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select houseAdvanceId");
		lSBQuery.append(" FROM MstLnaHouseAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag in ('D','R') AND advanceType = :RequestType");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("RequestType", lLngRequestType);
		houseAdvanceList = lQuery.list();
		return houseAdvanceList;
	}

	public List getHouseAdvanceToDEOApprover(Long lLngHouseAdvnId) {
		List houseAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select houseAdvanceId");
		lSBQuery.append(" FROM MstLnaHouseAdvance");
		lSBQuery.append(" WHERE houseAdvanceId = :HouseAdvnId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("HouseAdvnId", lLngHouseAdvnId);
		houseAdvanceList = lQuery.list();
		return houseAdvanceList;
	}

	public Integer requestDataAlreadyExists(String lStrSevaarthId, Long lLngRequestType) {
		List houseAdvanceList = new ArrayList();
		Integer lIntReqCount = 0;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select houseAdvanceId");
		lSBQuery.append(" FROM MstLnaHouseAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag IN ('A','A1','A2','A3') AND advanceType = :RequestType");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("RequestType", lLngRequestType);
		houseAdvanceList = lQuery.list();
		if (!houseAdvanceList.isEmpty()) {
			lIntReqCount = houseAdvanceList.size();
		}
		return lIntReqCount;
	}

	public List getGuarantorDtls(String lStrEmpCode) {
		List lLstGuarantorDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.name,ME.ddoCode,DDO.ddoName,ME.basicPay");
			lSBQuery.append(" FROM MstEmp ME,OrgDdoMst DDO");
			lSBQuery.append(" WHERE ME.sevarthId = :sevarthId AND ME.ddoCode = DDO.ddoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrEmpCode);
			lLstGuarantorDtls = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstGuarantorDtls;
	}

	public Boolean requestPendingStatus(String lStrSevaarthId) {
		List houseAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select houseAdvanceId");
		lSBQuery.append(" FROM MstLnaHouseAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag = 'F'");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		houseAdvanceList = lQuery.list();
		if (houseAdvanceList.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public List getSubtypeDtlsforDisbursement(String lStrSevaarthId) {
		List houseAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select houseAdvanceId");
		lSBQuery.append(" FROM MstLnaHouseAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND advanceSubType IN (:CF,:PPLC) AND statusFlag != 'A'");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("CF", Long.parseLong(gObjRsrcBndle.getString("LNA.CONSTRUCTIONOFFLAT")));
		lQuery.setParameter("PPLC", Long.parseLong(gObjRsrcBndle.getString("LNA.PLOATPURCHASE")));
		houseAdvanceList = lQuery.list();
		return houseAdvanceList;
	}

	public List getEligibleStatusForExtnOfRoom(String lStrSevaarthId) {
		List houseAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select amountSanctioned");
		lSBQuery.append(" FROM MstLnaHouseAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag = 'A' AND recoveryStatus = 1 AND advanceSubType NOT IN (:SP,:ER)");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("SP", Long.parseLong(gObjRsrcBndle.getString("LNA.SPECIALREPAIRS")));
		lQuery.setParameter("ER", Long.parseLong(gObjRsrcBndle.getString("LNA.EXTENSIONOFROOMS")));
		houseAdvanceList = lQuery.list();
		return houseAdvanceList;
	}
}
