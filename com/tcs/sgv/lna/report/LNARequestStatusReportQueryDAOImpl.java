package com.tcs.sgv.lna.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LNARequestStatusReportQueryDAOImpl extends GenericDaoHibernateImpl {

	Session ghibSession = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public LNARequestStatusReportQueryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List<Object> getStatusReport(Date lDtFromDate, Date lDtToDate, String lStrUser, String lCmbLoanType, String gStrLocCode) {

		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<Object> lLnaStatusListCA = new ArrayList<Object>();
		List<Object> lLnaStatusListHBA = new ArrayList<Object>();
		List<Object> lLnaStatusListMCA = new ArrayList<Object>();
		List<Object> lLstOuterDtlsList = null;
try{
		if (lStrUser.equals("HODASST")) {
			if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))) {
				lLnaStatusListCA = getStatusListCAForHodAst(lDtFromDate, lDtToDate, gStrLocCode);
			} else if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))) {
				lLnaStatusListHBA = getStatusListHBAForHodAst(lDtFromDate, lDtToDate, gStrLocCode);
			} else if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))) {
				lLnaStatusListMCA = getStatusListMCAForHodAst(lDtFromDate, lDtToDate, gStrLocCode);
			} else {
				lLnaStatusListCA = getStatusListCAForHodAst(lDtFromDate, lDtToDate, gStrLocCode);
				lLnaStatusListHBA = getStatusListHBAForHodAst(lDtFromDate, lDtToDate, gStrLocCode);
				lLnaStatusListMCA = getStatusListMCAForHodAst(lDtFromDate, lDtToDate, gStrLocCode);
			}
		} else if (lStrUser.equals("HOD")) {
			if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))) {
				lLnaStatusListCA = getStatusListCAForHod(lDtFromDate, lDtToDate, gStrLocCode);
			} else if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))) {
				lLnaStatusListHBA = getStatusListHBAForHod(lDtFromDate, lDtToDate, gStrLocCode);
			} else if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))) {
				lLnaStatusListMCA = getStatusListMCAForHod(lDtFromDate, lDtToDate, gStrLocCode);
			} else {
				lLnaStatusListCA = getStatusListCAForHod(lDtFromDate, lDtToDate, gStrLocCode);
				lLnaStatusListHBA = getStatusListHBAForHod(lDtFromDate, lDtToDate, gStrLocCode);
				lLnaStatusListMCA = getStatusListMCAForHod(lDtFromDate, lDtToDate, gStrLocCode);
			}
		}
		else if (lStrUser.equals("HO")) {
			if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))) {
				lLnaStatusListCA = getStatusListCAForHO(lDtFromDate, lDtToDate, gStrLocCode);
			} else if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))) {
				lLnaStatusListHBA = getStatusListHBAForHO(lDtFromDate, lDtToDate, gStrLocCode);
			} else if (lCmbLoanType.equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))) {
				lLnaStatusListMCA = getStatusListMCAForHO(lDtFromDate, lDtToDate, gStrLocCode);
			} else {
				lLnaStatusListCA = getStatusListCAForHO(lDtFromDate, lDtToDate, gStrLocCode);
				lLnaStatusListHBA = getStatusListHBAForHO(lDtFromDate, lDtToDate, gStrLocCode);
				lLnaStatusListMCA = getStatusListMCAForHO(lDtFromDate, lDtToDate, gStrLocCode);
			}
		}
		lLnaStatusListCA.addAll(lLnaStatusListHBA);
		lLnaStatusListCA.addAll(lLnaStatusListMCA);

		if (lLnaStatusListCA != null && !lLnaStatusListCA.isEmpty()) {
			Iterator<Object> it = lLnaStatusListCA.iterator();
			lLstOuterDtlsList = new ArrayList<Object>();
			List<Object> lLstDtlsList;
			String requestType = "";
			String reqStatus = "";
			while (it.hasNext()) {
				lLstDtlsList = new ArrayList<Object>();
				Object[] tuple = (Object[]) it.next();

				lLstDtlsList.add(tuple[0]);
				if (tuple[1] != null) {
					lLstDtlsList.add(tuple[1]);
				} else {
					lLstDtlsList.add("");
				}
				if (tuple[2].toString().equals(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))) {
					requestType = "Computer Advance";
				} else if (tuple[2].toString().equals(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))) {
					requestType = "House Building Advance";
				} else if (tuple[2].toString().equals(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))) {
					requestType = "Vehicle Advance";
				}
				lLstDtlsList.add(requestType);
				lLstDtlsList.add(tuple[3]);
				lLstDtlsList.add(tuple[4]);
				if (tuple[5] != null) {
					lLstDtlsList.add(lObjDateFormat.format(tuple[5]));
				} else {
					lLstDtlsList.add("");
				}
				if (tuple[6] != null) {
					lLstDtlsList.add(lObjDateFormat.format(tuple[6]));
				} else {
					lLstDtlsList.add("");
				}
				if (tuple[7] != null) {
					lLstDtlsList.add(lObjDateFormat.format(tuple[7]));
				} else {
					lLstDtlsList.add("");
				}
				/*if (tuple[8].equals("D") || tuple[8].equals("R")) {
					reqStatus = "Pending with RHO";
				} else*/
				if (tuple[8].equals("F")) {
					reqStatus = "Pending with ROD";
				} else if (tuple[8].equals("A")) {
					reqStatus = "Approved";
				} else if (tuple[8].equals("A1")) {
					reqStatus = "Disburse First Principal Installment";
				} else if (tuple[8].equals("A2")) {
					reqStatus = "Disburse Second Principal Installment";
				} else if (tuple[8].equals("A3")) {
					reqStatus = "Disburse Third Principal Installment";
				}
				lLstDtlsList.add(reqStatus);
				lLstOuterDtlsList.add(lLstDtlsList);
			}
		}
}catch(Exception e){}
		return lLstOuterDtlsList;

	}

	public List<Object> getStatusListCAForHodAst(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListCA = new ArrayList<Object>();
		StringBuilder lSBQueryCA = new StringBuilder();
		lSBQueryCA.append("SELECT ME.name,CA.transactionId,CA.advanceType,CLM.lookupName,CA.sevaarthId,CA.applicationDate,CA.createdDate,");
		lSBQueryCA.append("CA.hodasstActionDate,CA.statusFlag");
		lSBQueryCA.append(" FROM MstEmp ME,MstLnaCompAdvance CA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryCA.append(" where CA.createdDate >= :FromDate AND CA.createdDate <= :ToDate");
		lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode");
		lSBQueryCA.append(" AND ME.sevarthId = CA.sevaarthId AND CLM.lookupId = CA.advanceSubType ORDER BY 2");
		Query lQueryCA = ghibSession.createQuery(lSBQueryCA.toString());
		lQueryCA.setParameter("hodLocCode", gStrLocCode);
		lQueryCA.setDate("FromDate", lDtFromDate);
		lQueryCA.setDate("ToDate", lDtToDate);
		lLnaStatusListCA = lQueryCA.list();
		return lLnaStatusListCA;
	}

	public List<Object> getStatusListMCAForHodAst(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListMCA = new ArrayList<Object>();
		StringBuilder lSBQueryMCA = new StringBuilder();
		lSBQueryMCA.append("SELECT ME.name,MA.transactionId,MA.advanceType,CLM.lookupName,MA.sevaarthId,MA.applicationDate,MA.createdDate,");
		lSBQueryMCA.append("MA.hodasstActionDate,MA.statusFlag");
		lSBQueryMCA.append(" FROM MstEmp ME,MstLnaMotorAdvance MA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryMCA.append(" where MA.createdDate >= :FromDate AND MA.createdDate <= :ToDate");
		lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode");
		lSBQueryMCA.append(" AND ME.sevarthId = MA.sevaarthId AND CLM.lookupId = MA.advanceSubType  ORDER BY 2");
		Query lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
		lQueryMCA.setParameter("hodLocCode", gStrLocCode);
		lQueryMCA.setDate("FromDate", lDtFromDate);
		lQueryMCA.setDate("ToDate", lDtToDate);
		lLnaStatusListMCA = lQueryMCA.list();
		return lLnaStatusListMCA;
	}

	public List<Object> getStatusListHBAForHodAst(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListHBA = new ArrayList<Object>();
		StringBuilder lSBQueryHBA = new StringBuilder();
		lSBQueryHBA.append("SELECT ME.name,HA.transactionId,HA.advanceType,CLM.lookupName,HA.sevaarthId,HA.applicationDate,HA.createdDate,");
		lSBQueryHBA.append("HA.hodasstActionDate,HA.statusFlag");
		lSBQueryHBA.append(" FROM MstEmp ME,MstLnaHouseAdvance HA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryHBA.append(" where HA.createdDate >= :FromDate AND HA.createdDate <= :ToDate");
		lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode");
		lSBQueryHBA.append(" AND ME.sevarthId = HA.sevaarthId AND CLM.lookupId = HA.advanceSubType ORDER BY 2");
		Query lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
		lQueryHBA.setParameter("hodLocCode", gStrLocCode);
		lQueryHBA.setDate("FromDate", lDtFromDate);
		lQueryHBA.setDate("ToDate", lDtToDate);
		lLnaStatusListHBA = lQueryHBA.list();
		return lLnaStatusListHBA;
	}

	public List<Object> getStatusListCAForHod(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListCA = new ArrayList<Object>();
		StringBuilder lSBQueryCA = new StringBuilder();
		lSBQueryCA.append("SELECT ME.name,CA.transactionId,CA.advanceType,CLM.lookupName,CA.sevaarthId,CA.applicationDate,CA.hodasstActionDate,");
		lSBQueryCA.append("CA.hodActionDate,CA.statusFlag");
		lSBQueryCA.append(" FROM MstEmp ME,MstLnaCompAdvance CA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryCA.append(" where CA.hodasstActionDate >= :FromDate AND CA.hodasstActionDate <= :ToDate");
		lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :hodLocCode");
		lSBQueryCA.append(" AND ME.sevarthId = CA.sevaarthId AND CLM.lookupId = CA.advanceSubType  ORDER BY 2");
		Query lQueryCA = ghibSession.createQuery(lSBQueryCA.toString());
		lQueryCA.setParameter("hodLocCode", gStrLocCode);
		lQueryCA.setDate("FromDate", lDtFromDate);
		lQueryCA.setDate("ToDate", lDtToDate);
		lLnaStatusListCA = lQueryCA.list();
		return lLnaStatusListCA;
	}

	public List<Object> getStatusListMCAForHod(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListMCA = new ArrayList<Object>();
		StringBuilder lSBQueryMCA = new StringBuilder();
		lSBQueryMCA.append("SELECT ME.name,MA.transactionId,MA.advanceType,CLM.lookupName,MA.sevaarthId,MA.applicationDate,MA.hodasstActionDate,");
		lSBQueryMCA.append("MA.hodActionDate,MA.statusFlag");
		lSBQueryMCA.append(" FROM MstEmp ME,MstLnaMotorAdvance MA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryMCA.append(" where MA.hodasstActionDate >= :FromDate AND MA.hodasstActionDate <= :ToDate");
		lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :hodLocCode");
		lSBQueryMCA.append(" AND ME.sevarthId = MA.sevaarthId AND CLM.lookupId = MA.advanceSubType  ORDER BY 2");
		Query lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
		lQueryMCA.setParameter("hodLocCode", gStrLocCode);
		lQueryMCA.setDate("FromDate", lDtFromDate);
		lQueryMCA.setDate("ToDate", lDtToDate);
		lLnaStatusListMCA = lQueryMCA.list();
		return lLnaStatusListMCA;
	}

	public List<Object> getStatusListHBAForHod(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListHBA = new ArrayList<Object>();
		StringBuilder lSBQueryHBA = new StringBuilder();
		lSBQueryHBA.append("SELECT ME.name,HA.transactionId,HA.advanceType,CLM.lookupName,HA.sevaarthId,HA.applicationDate,HA.hodasstActionDate,");
		lSBQueryHBA.append("HA.hodActionDate,HA.statusFlag");
		lSBQueryHBA.append(" FROM MstEmp ME,MstLnaHouseAdvance HA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryHBA.append(" where HA.hodasstActionDate >= :FromDate AND HA.hodasstActionDate <= :ToDate");
		lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :hoLocCode");
		lSBQueryHBA.append(" AND ME.sevarthId = HA.sevaarthId AND CLM.lookupId = HA.advanceSubType  ORDER BY 2");
		Query lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
		lQueryHBA.setParameter("hoLocCode", gStrLocCode);
		lQueryHBA.setDate("FromDate", lDtFromDate);
		lQueryHBA.setDate("ToDate", lDtToDate);
		lLnaStatusListHBA = lQueryHBA.list();
		return lLnaStatusListHBA;
	}
	
	
	public List<Object> getStatusListCAForHO(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListCA = new ArrayList<Object>();
		StringBuilder lSBQueryCA = new StringBuilder();
		lSBQueryCA.append("SELECT ME.name,CA.transactionId,CA.advanceType,CLM.lookupName,CA.sevaarthId,CA.applicationDate,CA.hodasstActionDate,");
		lSBQueryCA.append("CA.hodActionDate,CA.statusFlag");
		lSBQueryCA.append(" FROM MstEmp ME,MstLnaCompAdvance CA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryCA.append(" where CA.hodasstActionDate >= :FromDate AND CA.hodasstActionDate <= :ToDate");
		lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode");
		lSBQueryCA.append(" AND ME.sevarthId = CA.sevaarthId AND CLM.lookupId = CA.advanceSubType  ORDER BY 2");
		Query lQueryCA = ghibSession.createQuery(lSBQueryCA.toString());
		lQueryCA.setParameter("hodLocCode", gStrLocCode);
		lQueryCA.setDate("FromDate", lDtFromDate);
		lQueryCA.setDate("ToDate", lDtToDate);
		lLnaStatusListCA = lQueryCA.list();
		return lLnaStatusListCA;
	}

	public List<Object> getStatusListMCAForHO(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListMCA = new ArrayList<Object>();
		StringBuilder lSBQueryMCA = new StringBuilder();
		lSBQueryMCA.append("SELECT ME.name,MA.transactionId,MA.advanceType,CLM.lookupName,MA.sevaarthId,MA.applicationDate,MA.hodasstActionDate,");
		lSBQueryMCA.append("MA.hodActionDate,MA.statusFlag");
		lSBQueryMCA.append(" FROM MstEmp ME,MstLnaMotorAdvance MA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryMCA.append(" where MA.hodasstActionDate >= :FromDate AND MA.hodasstActionDate <= :ToDate");
		lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hoLocCode");
		lSBQueryMCA.append(" AND ME.sevarthId = MA.sevaarthId AND CLM.lookupId = MA.advanceSubType  ORDER BY 2");
		Query lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
		lQueryMCA.setParameter("hoLocCode", gStrLocCode);
		lQueryMCA.setDate("FromDate", lDtFromDate);
		lQueryMCA.setDate("ToDate", lDtToDate);
		lLnaStatusListMCA = lQueryMCA.list();
		return lLnaStatusListMCA;
	}

	public List<Object> getStatusListHBAForHO(Date lDtFromDate, Date lDtToDate, String gStrLocCode) {
		List<Object> lLnaStatusListHBA = new ArrayList<Object>();
		StringBuilder lSBQueryHBA = new StringBuilder();
		lSBQueryHBA.append("SELECT ME.name,HA.transactionId,HA.advanceType,CLM.lookupName,HA.sevaarthId,HA.applicationDate,HA.hodasstActionDate,");
		lSBQueryHBA.append("HA.hodActionDate,HA.statusFlag");
		lSBQueryHBA.append(" FROM MstEmp ME,MstLnaHouseAdvance HA,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryHBA.append(" where HA.hodasstActionDate >= :FromDate AND HA.hodasstActionDate <= :ToDate");
		lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hoLocCode");
		lSBQueryHBA.append(" AND ME.sevarthId = HA.sevaarthId AND CLM.lookupId = HA.advanceSubType  ORDER BY 2");
		Query lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
		lQueryHBA.setParameter("hoLocCode", gStrLocCode);
		lQueryHBA.setDate("FromDate", lDtFromDate);
		lQueryHBA.setDate("ToDate", lDtToDate);
		lLnaStatusListHBA = lQueryHBA.list();
		return lLnaStatusListHBA;
	}
}
