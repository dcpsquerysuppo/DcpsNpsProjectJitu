package com.tcs.sgv.pensionproc.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;

public class PensionprocQueryDAOImpl extends GenericDaoHibernateImpl implements PensionprocQueryDAO {
	
	private Session ghibSession = null;
	SessionFactory gObjSessionFactory = null;
	private static final Logger gLogger = Logger.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);
	private SimpleDateFormat gObjDtFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private ResourceBundle gObjRsrcBndleCaseLables = ResourceBundle.getBundle("resources/pensionproc/PensionCaseLabels");
	private ResourceBundle gObjRsrcBndle  = ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants");
	
	public PensionprocQueryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		gObjSessionFactory = sessionFactory;
		ghibSession = sessionFactory.getCurrentSession();
	}
	public List getPensionCaseTrackingReport(ReportVO lObjReport, String lStrFromDate, String lStrToDate, String lStrPensionType, String lStrRetirementDate,			
			String lStrPensionerName, String lStrSevaarthId, String lStrPpoNo,String lStrLocCode,Long lLngDdoCode) throws Exception {

		
		ArrayList lArrListOuter = new ArrayList();
		
		String lStrDdoName = getDdoName(lStrLocCode);
		String lStrDdoAsstName = getDdoAsstName(lStrLocCode);		
		
		
		Query lHibQry = null;
		StringBuilder lSBQuery = new StringBuilder();
		
		try {

			
				lSBQuery.append(" SELECT inw.inwardDate,inw.sevaarthId,dtl.pnsnrName, ");
				lSBQuery.append(" dtl.retirementDate,inw.commensionDate,inw.pensionType,inw.caseStatus,inw.inwardPensionId,inw.caseType ");
				lSBQuery.append(" FROM TrnPnsnProcInwardPension inw , TrnPnsnProcPnsnrDtls dtl ");
				lSBQuery.append(" WHERE inw.inwardPensionId=dtl.inwardPensionId and inw.ddoCode = :ddoCode");
				
				
				if (!"".equals(lStrFromDate) && !"".equals(lStrToDate)) {
					lSBQuery.append(" AND inw.inwardDate BETWEEN :lDtFrmDate AND :lDtToDate ");
				}								
				if (!"".equals(lStrPpoNo)) {
					lSBQuery.append(" AND inw.ppoNo= :lStrPPONo");
				}
				if (!"".equals(lStrRetirementDate)) {
					lSBQuery.append(" AND dtl.retirementDate = :lDtRetiredDate");
				}
				if (lStrPensionerName != null && !lStrPensionerName.equals("")) {
					lSBQuery.append(" AND dtl.pnsnrName like :lStrName");
				}				
				if (!"".equals(lStrPensionType)) {
					lSBQuery.append(" AND inw.pensionType = :lStrPensionType");
				}
				if (!"".equals(lStrSevaarthId)) {
					lSBQuery.append(" AND inw.sevaarthId = :lStrSevaarthId");
				}
	
				lSBQuery.append(" ORDER BY inw.inwardPensionId");
				
	
				lHibQry = ghibSession.createQuery(lSBQuery.toString());
				
				lHibQry.setParameter("ddoCode",lLngDdoCode);
					
				if (!"".equals(lStrFromDate) && !"".equals(lStrFromDate)) {
					lHibQry.setParameter("lDtFrmDate", gObjDtFormat.parse(lStrFromDate));
					lHibQry.setParameter("lDtToDate", gObjDtFormat.parse(lStrToDate));
				}
				if (!"".equals(lStrSevaarthId)) {
					lHibQry.setParameter("lStrSevaarthId", lStrSevaarthId.toUpperCase().trim());
				}				
				if (!"".equals(lStrPpoNo)) {
					lHibQry.setParameter("lStrPPONo", lStrPpoNo.toUpperCase().trim());
				}
				if (!"".equals(lStrRetirementDate)) {
					lHibQry.setParameter("lDtRetiredDate", gObjDtFormat.parse(lStrRetirementDate));
				}
				if (lStrPensionerName != null && !lStrPensionerName.equals("")) {
					lHibQry.setParameter("lStrName", lStrPensionerName.toUpperCase().trim() +"%");
				}				
				if (!"".equals(lStrPensionType)) {
					lHibQry.setParameter("lStrPensionType", lStrPensionType);
				}
			
			String urlPrefix = "";
			List lLstFinal = lHibQry.list();
			if (lLstFinal != null && !lLstFinal.isEmpty()) {
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal.iterator();
				while (it.hasNext()) {
					
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					if (tuple[0] != null) {
						lArrListInner.add(IFMSCommonServiceImpl.getStringFromDate((Date) tuple[0]));// inwd
						// date
					} else {
						lArrListInner.add("");
					}
					if (tuple[1] != null) {
						urlPrefix = "ifms.htm?actionFlag=loadPensionCaseInwardForm&showReadOnly=Y&inwardId="+ tuple[7].toString() ;
						lArrListInner.add(new URLData(tuple[1].toString(), urlPrefix)); // Sevaarth Id
					} else {
						lArrListInner.add("");
					}
					if (tuple[2] != null) {
						lArrListInner.add(tuple[2]);// pensioner_name
					} else {
						lArrListInner.add("");
					}
					if (tuple[8] != null) {
						lArrListInner.add(tuple[8]);// pensioner_name
					} else {
						lArrListInner.add("");
					}
					if (tuple[3] != null) {
						lArrListInner.add(IFMSCommonServiceImpl.getStringFromDate((Date) tuple[3]));// retirement_date
					} else {
						lArrListInner.add("");
					}
					if (tuple[4] != null) {
						lArrListInner.add(IFMSCommonServiceImpl.getStringFromDate((Date) tuple[4]));// commencement_date
					} else {
						lArrListInner.add("");
					}
					if (tuple[5] != null) {
						lArrListInner.add(gObjRsrcBndleCaseLables.getString("PNSNTYPE." + tuple[5].toString().trim()));// pension_type					
					} else {
						lArrListInner.add("");
					}
					if (tuple[6] != null) {
						// case_status
						
					if(tuple[6].equals("DRAFT"))
						lArrListInner.add("Draft");
					
					else if(tuple[6].equals("FWDBYDEO"))
						lArrListInner.add("Fowarded to DDO");
					
					else if(tuple[6].equals("APRVDBYDDO"))
						lArrListInner.add("Approved By DDO");
					
					else if(tuple[6].equals("RJCTBYDDO"))
						lArrListInner.add("Rejected By DDO");
					
					else if(tuple[6].equals("SENDTOAG"))
						lArrListInner.add("Send to AG");
					
					else if(tuple[6].equals("MISBYDDO"))
						lArrListInner.add("Move for Correction due to approved by DDO mistake");
					
					else if(tuple[6].equals("AGQUERY"))
						lArrListInner.add("Move for correction due to AG query");
					
					else if(tuple[6].equals("APRVDBYAG"))
						lArrListInner.add("Approved By AG");
					
					} else {
						lArrListInner.add("");
					}
					if (tuple[6].equals("FWDBYDEO")) {						
						lArrListInner.add(lStrDdoName); // Lying with
					} else {
						lArrListInner.add(lStrDdoAsstName);
					}
					
					if (tuple[6] != null) {
						lArrListInner.add(gObjRsrcBndle.getString("LYINGAT." + tuple[6].toString().trim())); // Lying At
					} else {
						lArrListInner.add("");
					}					
					lArrListOuter.add(lArrListInner);
				}
			}
		} catch (Exception e) {
			gLogger.error("Exception in PensionCaseTrackingReportQueryDAOImpl.getPensionCaseTrackingReport is ::" + e.getMessage(), e);
	
		}
		return lArrListOuter;
	}
	
	public String getDdoName(String lStrLocCode){
		String lStrDdoName = "";
		Query lHibQry = null;
		StringBuilder lSBQuery = new StringBuilder();
		List lLstDdoName = null;
		try {
				lSBQuery.append(" SELECT e.EMP_FNAME || e.EMP_MNAME || e.EMP_LNAME ");
				lSBQuery.append(" FROM org_emp_mst e,org_user_mst u,ORG_DDO_MST d,ORG_USERPOST_RLT p  ");
				lSBQuery.append(" where d.LOCATION_CODE = :LocationCode ");
				lSBQuery.append(" and d.POST_ID = p.POST_ID and p.USER_ID = u.USER_ID and u.USER_ID = e.USER_ID and u.ACTIVATE_FLAG = 1");
				
				lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());
				lHibQry.setParameter("LocationCode", lStrLocCode);
				
				lLstDdoName = lHibQry.list();
				
				if(!lLstDdoName.isEmpty())
					lStrDdoName = (String) lLstDdoName.get(0);
				
					
		}catch(Exception e){
			gLogger.error("Exception in getDdoName",e);
		}
		return lStrDdoName;
	}
	public String getDdoAsstName(String lStrLocCode){
		String lStrDdoAsstName = "";		
		Query lHibQry = null;
		StringBuilder lSBQuery = new StringBuilder();
		List lLstDdoAsstName = null;
		try {
				lSBQuery.append(" SELECT e.EMP_FNAME || e.EMP_MNAME || e.EMP_LNAME ");
				lSBQuery.append(" FROM org_emp_mst e,org_user_mst u,ORG_DDO_MST d,ORG_USERPOST_RLT p,RLT_DCPS_DDO_ASST r ");
				lSBQuery.append(" where d.LOCATION_CODE = :LocationCode and d.POST_ID = r.DDO_POST_ID ");
				lSBQuery.append(" and r.ASST_POST_ID = p.POST_ID and p.USER_ID = u.USER_ID and u.ACTIVATE_FLAG = 1 and u.USER_ID = e.USER_ID ");
				
				lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());
				lHibQry.setParameter("LocationCode", lStrLocCode);
				
				lLstDdoAsstName = lHibQry.list();
				
				if(!lLstDdoAsstName.isEmpty())
					lStrDdoAsstName = (String) lLstDdoAsstName.get(0);
				
					
		}catch(Exception e){
			gLogger.error("Exception in getDdoAsstName",e);
		}
		return lStrDdoAsstName;
	}
}
