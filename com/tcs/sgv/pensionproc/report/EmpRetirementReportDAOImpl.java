package com.tcs.sgv.pensionproc.report;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.eis.valueobject.HrEisEmpMst;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;

public class EmpRetirementReportDAOImpl extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("PensionProcessingReports");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Session ghibSession = null;	
	Long gLngPostId = null;

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		List<Object> lEmpRetirementReportDataList = new ArrayList<Object>();
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
			gLngPostId = lObjLoginVO.getPrimaryPost().getPostId();
			ghibSession = gObjSessionFactory.getCurrentSession();

			if (lObjReport.getReportCode().equals("1000001")) {
				lEmpRetirementReportDataList = getEmpRetirementReportData(lObjReport);
			}

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}
		return lEmpRetirementReportDataList;
	}

	public List<Object> getEmpRetirementReportData(ReportVO lObjReport) {
		List<Object> resultDataList = new ArrayList<Object>();
		try {
			EmpRetirementReportQueryDAOImpl lObjRetirementReportQueryDAOImpl = new EmpRetirementReportQueryDAOImpl(HrEisEmpMst.class, serviceLocator
					.getSessionFactorySlave());
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String lStrFromDate = lObjReport.getParameterValue("fromDate").toString();
			String lStrToDate = lObjReport.getParameterValue("toDate").toString();
			String lStrDesignation = lObjReport.getParameterValue("designation").toString().trim();
			
			Date lDtFromDate = new Date(lObjDateFormat.parse(lStrFromDate).getTime());
			Date lDtToDate = new Date(lObjDateFormat.parse(lStrToDate).getTime());
			lDtToDate.setDate(lDtToDate.getDate() + 1);
			
			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serviceLocator
					.getSessionFactorySlave());
			
			Long isDdoOrDdoAsst = lObjTrnPnsnProcInwardPensionDAO.isDdoOrDdoAsst(gLngPostId);
			String lStrDdoCode = "";
			if(isDdoOrDdoAsst == 1L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDO(gLngPostId);
			}else if(isDdoOrDdoAsst == 2L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDOAsst(gLngPostId);
			}			

			resultDataList = lObjRetirementReportQueryDAOImpl.getEmpRetirementReport(lDtFromDate, lDtToDate, lStrDesignation, lStrDdoCode);

		} catch (Exception e) {
			gLogger.info("getEmpRetirementReportData(): Exception is" + e, e);
		}

		return resultDataList;
	}


}
