package com.tcs.sgv.lna.report;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.db2.jcc.am.sf;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;

public class LNARequestStatusReportDAOImpl extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("LNAReports");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Session ghibSession = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		List lStatusReportDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		try {
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			ghibSession = gObjSessionFactory.getCurrentSession();

			if (lObjReport.getReportCode().equals("8000051")) {
				if(getStatusReportData(lObjReport) != null)
				lStatusReportDataList = getStatusReportData(lObjReport);
			}

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}
		return lStatusReportDataList;
	}

	public List getStatusReportData(ReportVO lObjReport) {
		List resultDataList = new ArrayList();
		try {
			LNARequestStatusReportQueryDAOImpl lObjRequestStatusReportDAOImpl = new LNARequestStatusReportQueryDAOImpl(MstLnaCompAdvance.class, serviceLocator
					.getSessionFactorySlave());
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String lStrFromDate = lObjReport.getParameterValue("fromDate").toString();
			String lStrToDate = lObjReport.getParameterValue("toDate").toString();
			String lStrUser = lObjReport.getParameterValue("UserType").toString();
			String lCmbLoanType = lObjReport.getParameterValue("cmbLoanType").toString();
			Date lDtFromDate = new Date(lObjDateFormat.parse(lStrFromDate).getTime());
			Date lDtToDate = new Date(lObjDateFormat.parse(lStrToDate).getTime());
			lDtToDate.setDate(lDtToDate.getDate() + 1);

			gLogger.info(" lStrUser "+lStrUser);
			
			resultDataList = lObjRequestStatusReportDAOImpl.getStatusReport(lDtFromDate, lDtToDate, lStrUser, lCmbLoanType, gStrLocCode);

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}

		return resultDataList;
	}

	public List<ComboValuesVO> getLoanType(String lStrLangId, String lStrLocationCode) {

		ArrayList<ComboValuesVO> lArrListLoanType = new ArrayList<ComboValuesVO>();
		List<String> lLstLoan = null;
		Object[] obj;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT lookupId,lookupName FROM CmnLookupMst");
			lSBQuery.append(" WHERE parentLookupId = :lookupId");
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lookupId", Long.parseLong(gObjRsrcBndle.getString("LNA.LOANSADVANCES")));
			lLstLoan = lQuery.list();
			if (!lLstLoan.isEmpty()) {
				Iterator it = lLstLoan.iterator();
				while (it.hasNext()) {
					ComboValuesVO cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lArrListLoanType.add(cmbVO);
				}
			}

		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		}
		return lArrListLoanType;
	}
}
