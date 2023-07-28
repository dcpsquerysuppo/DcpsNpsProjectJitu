package com.tcs.sgv.lna.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LNAApprovedRequestDAOImpl extends GenericDaoHibernateImpl implements LNAApprovedRequestDAO {
	Session ghibSession = null;

	public LNAApprovedRequestDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getLNAApprovedRequestList(String lStrLocCode,String gStrPostId,String lStrUser) {

		List lLnaApprovedListCA = new ArrayList();
		List lLnaApprovedListHBA = new ArrayList();
		List lLnaApprovedListMCA = new ArrayList();
		StringBuilder lSBQueryCA = new StringBuilder();
		StringBuilder lSBQueryHBA = new StringBuilder();
		StringBuilder lSBQueryMCA = new StringBuilder();
		
		Query lQueryCA = null;
		if(lStrUser.equals("HOD")){
		lSBQueryCA.append("SELECT CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,CA.amountSanctioned,CA.advanceType,CLM.lookupName,");
		lSBQueryCA.append(" CA.lnaBillId FROM MstEmp ME,MstLnaCompAdvance CA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryCA.append(" where CA.statusFlag = 'A' AND ME.sevarthId = CA.sevaarthId AND CA.advanceSubType = CLM.lookupId");
		lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode");		
		 lQueryCA = ghibSession.createQuery(lSBQueryCA.toString());
		lQueryCA.setParameter("LocCode", lStrLocCode);
		}
		if(lStrUser.equals("HO")){
			lSBQueryCA.append("SELECT CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,CA.amountSanctioned,CA.advanceType,CLM.lookupName,");
			lSBQueryCA.append(" CA.lnaBillId from MstEmp ME, MstLnaCompAdvance CA,CmnLookupMst CLM ");
			lSBQueryCA.append(" where CA.statusFlag = 'A' AND CA.advanceSubType = CLM.lookupId AND ME.sevarthId = CA.sevaarthId ");
			lSBQueryCA.append(" AND CA.toPostID = "+gStrPostId);	
			lQueryCA = ghibSession.createQuery(lSBQueryCA.toString());
		}
		lLnaApprovedListCA = lQueryCA.list();

		Query lQueryHBA = null;
		if(lStrUser.equals("HOD")){
		lSBQueryHBA.append("SELECT HA.transactionId,HA.applicationDate,HA.sevaarthId,ME.name,HA.amountSanctioned,HA.advanceType,CLM.lookupName,");
		lSBQueryHBA.append(" HA.lnaBillId,HA.advanceSubType,HA.disbursementOne FROM MstEmp ME,MstLnaHouseAdvance HA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryHBA.append(" where (HA.statusFlag = 'A' or (HA.statusFlag='A1' and HA.advanceSubType = 800038) ");
		lSBQueryHBA.append(" or (HA.statusFlag = 'A1' and HA.advanceSubType = 800058))");	
		lSBQueryHBA.append(" AND ME.sevarthId = HA.sevaarthId AND HA.advanceSubType = CLM.lookupId");
		lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode");		
		 lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
		lQueryHBA.setParameter("LocCode", lStrLocCode);
		}
		if(lStrUser.equals("HO")){
			lSBQueryHBA.append("SELECT HA.transactionId,HA.applicationDate,HA.sevaarthId,ME.name,HA.amountSanctioned,HA.advanceType,CLM.lookupName,");
			lSBQueryHBA.append(" HA.lnaBillId,HA.advanceSubType,HA.disbursementOne from MstEmp ME, MstLnaHouseAdvance HA,CmnLookupMst CLM ");
			lSBQueryHBA.append(" where (HA.statusFlag = 'A' or (HA.statusFlag='A1' and HA.advanceSubType = 800038) ");
			lSBQueryHBA.append(" or (HA.statusFlag = 'A1' and HA.advanceSubType = 800058))");	
			lSBQueryHBA.append("  AND ME.sevarthId = HA.sevaarthId AND  HA.advanceSubType = CLM.lookupId  ");
			lSBQueryHBA.append(" AND HA.toPostID = "+gStrPostId);
			lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
		}
		
		lLnaApprovedListHBA = lQueryHBA.list();

		Query lQueryMCA = null;
		if(lStrUser.equals("HOD")){
		lSBQueryMCA.append("SELECT MA.transactionId,MA.applicationDate,MA.sevaarthId,ME.name,MA.amountSanctioned,MA.advanceType,CLM.lookupName,");
		lSBQueryMCA.append(" MA.lnaBillId FROM MstEmp ME,MstLnaMotorAdvance MA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryMCA.append(" where MA.statusFlag = 'A' AND ME.sevarthId = MA.sevaarthId AND MA.advanceSubType = CLM.lookupId");
		lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode");		
		lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
		lQueryMCA.setParameter("LocCode", lStrLocCode);
		}
		if(lStrUser.equals("HO")){
			lSBQueryMCA.append("SELECT MA.transactionId,MA.applicationDate,MA.sevaarthId,ME.name,MA.amountSanctioned,MA.advanceType,CLM.lookupName,");
			lSBQueryMCA.append(" MA.lnaBillId FROM MstEmp ME, MstLnaMotorAdvance MA,CmnLookupMst CLM ");
			lSBQueryMCA.append(" where MA.statusFlag = 'A' AND MA.advanceSubType = CLM.lookupId AND ME.sevarthId = MA.sevaarthId ");
			lSBQueryMCA.append(" AND MA.toPostID = "+gStrPostId);
			lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
		}
		lLnaApprovedListMCA = lQueryMCA.list();

		lLnaApprovedListCA.addAll(lLnaApprovedListHBA);
		lLnaApprovedListCA.addAll(lLnaApprovedListMCA);

		return lLnaApprovedListCA;
	}

}
