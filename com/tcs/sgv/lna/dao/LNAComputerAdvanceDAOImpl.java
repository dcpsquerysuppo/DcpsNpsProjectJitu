package com.tcs.sgv.lna.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LNAComputerAdvanceDAOImpl extends GenericDaoHibernateImpl implements LNAComputerAdvanceDAO {
	Session ghibSession = null;

	public LNAComputerAdvanceDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getComputerAdvance(String lStrSevaarthId, Long lLngRequestType) {
		List computerAdvanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select computerAdvanceId");
		lSBQuery.append(" FROM MstLnaCompAdvance");
		lSBQuery.append(" WHERE sevaarthId = :sevaarthId AND statusFlag in ('D','R') AND advanceType = :RequestType");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("RequestType", lLngRequestType);
		computerAdvanceList = lQuery.list();
		return computerAdvanceList;
	}

	public List getComAdvanceToDEOApprover(Long lLngComAdvnId) {
		List gpfEmpApproverlist = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select computerAdvanceId");
		lSBQuery.append(" FROM MstLnaCompAdvance");
		lSBQuery.append(" WHERE computerAdvanceId = :ComAdvnId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ComAdvnId", lLngComAdvnId);
		gpfEmpApproverlist = lQuery.list();
		return gpfEmpApproverlist;
	}

	public Boolean requestDataAlreadyExists(String lStrSevaarthId) {
		List CARequest = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select MCA.computerAdvanceId");
		lSBQuery.append(" FROM MstLnaCompAdvance MCA");
		lSBQuery.append(" WHERE MCA.sevaarthId = :SevaarthId AND MCA.statusFlag = 'A'");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("SevaarthId", lStrSevaarthId);
		CARequest = lQuery.list();
		if (CARequest.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public Boolean requestPendingStatus(String lStrSevaarthId) {
		List CARequest = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select MCA.computerAdvanceId");
		lSBQuery.append(" FROM MstLnaCompAdvance MCA");
		lSBQuery.append(" WHERE MCA.sevaarthId = :SevaarthId AND MCA.statusFlag = 'F'");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("SevaarthId", lStrSevaarthId);
		CARequest = lQuery.list();
		if (CARequest.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public List<Long> getAllHierarchyRefIdsForLocation(Long LocationCode) {

		List<Long> listHierarchyRefIds = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT WFP.HIERACHY_REF_ID FROM WF_HIERACHY_POST_MPG WFP");
		lSBQuery.append(" JOIN WF_HIERARCHY_REFERENCE_MST WFR ON WFP.HIERACHY_REF_ID = WFR.HIERACHY_REF_ID ");
		lSBQuery.append(" JOIN WF_DOC_MST WFD ON WFR.DOC_ID = WFD.DOC_ID");
		lSBQuery.append(" WHERE WFD.DOC_ID in (800007,800008,800009) and ");
		lSBQuery.append(" WFP.LOC_ID = " + LocationCode );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listHierarchyRefIds = lQuery.list();

		return listHierarchyRefIds;

	}
	
	public Boolean checkEntryInWfHierachyPostMpg(Long lLongHierarchyRefId,Long lLongPostId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" SELECT * FROM WF_HIERACHY_POST_MPG WFP where WFP.HIERACHY_REF_ID = " + lLongHierarchyRefId + " and WFP.POST_ID = '"+ lLongPostId  +"' and WFP.LEVEL_ID = 20 and WFP.activate_flag = 1 ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		if (tempList.size() == 0) {
			flag = false;
		}
		return flag;

	}
	
	public void insertWfHierachyPostMpg(Long lLongHierarchySeqId ,Long lLongHierarchyRefId ,Long lLongPostId,
			Long lLongCreatedByUserId,Date gDtCurDate,Long LocId ) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("INSERT INTO WF_HIERACHY_POST_MPG VALUES \n");
			lSBQuery.append("(:hierachySeqId,:parentHierachy,:postId,:levelId,:hierachyRefId,:crtUser,:createdDate,:lstUpdUser,:lstUpdDate,:startDate,:endDate,:activeFlag,:locId,:langId,:dueDays) \n"); 

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("hierachySeqId", lLongHierarchySeqId);
			lQuery.setParameter("parentHierachy", null );
			lQuery.setParameter("postId", lLongPostId);
			lQuery.setParameter("levelId", 20);
			lQuery.setParameter("hierachyRefId", lLongHierarchyRefId);
			lQuery.setParameter("crtUser",lLongCreatedByUserId );
			lQuery.setParameter("createdDate",gDtCurDate );
			lQuery.setParameter("lstUpdUser",null );
			lQuery.setParameter("lstUpdDate",null );
			lQuery.setParameter("startDate",gDtCurDate );
			lQuery.setParameter("endDate",null );
			lQuery.setParameter("activeFlag", 1);
			lQuery.setParameter("locId", LocId  );
			lQuery.setParameter("langId",1 );
			lQuery.setParameter("dueDays",null );

			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw (e);
		}
	}
}
