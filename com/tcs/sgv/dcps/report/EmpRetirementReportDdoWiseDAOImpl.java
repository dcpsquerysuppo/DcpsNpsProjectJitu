/**
 * Class Description -
 * 
 * 
 * @author Shekhar Kadam DAT,Mumbai
 * @version 0.1
 * @since JDK 1.7 Aug 12, 2014
 */
package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.dao.PostEmpContriDAO;
import com.tcs.sgv.dcps.dao.PostEmpContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;
import com.tcs.sgv.lna.report.LNARequestStatusReportQueryDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;


public class EmpRetirementReportDdoWiseDAOImpl extends DefaultReportDataFinder implements ReportDataFinder 

{
	
	private static final Logger gLogger = Logger.getLogger("EmpRetirementeports");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Session ghibSession = null;
	
	//private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		List lStatusReportDataList = new ArrayList();
		List StatusReportDataList = new ArrayList(); 
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
			
			
			if (lObjReport.getReportCode().equals("8000084")) {
				StatusReportDataList = getStatusReportData(lObjReport);		
				
				if(StatusReportDataList != null)
				lStatusReportDataList = StatusReportDataList;
			}
			

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}
		return lStatusReportDataList;
	}
	
	public List getStatusReportData(ReportVO lObjReport) {
		List resultDataList = new ArrayList();
		try {
			EmpRetirementReportDdoWiseQueryDAOImpl lObjEmpRetirementReportDdoWiseQueryDAOImpl = new EmpRetirementReportDdoWiseQueryDAOImpl(MstLnaCompAdvance.class, serviceLocator
					.getSessionFactorySlave());
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String lStrFromDate = lObjReport.getParameterValue("fromDate").toString();
			String lStrToDate = lObjReport.getParameterValue("toDate").toString();
			String cmbDdoList = lObjReport.getParameterValue("cmbDdoList").toString();
			
			
			Date lDtFromDate = new Date(lObjDateFormat.parse(lStrFromDate).getTime());
			Date lDtToDate = new Date(lObjDateFormat.parse(lStrToDate).getTime());
			lDtToDate.setDate(lDtToDate.getDate() + 1);
			gLogger.info(" List : "+cmbDdoList);
			
			resultDataList = lObjEmpRetirementReportDdoWiseQueryDAOImpl.getEmpRetirementReport(lDtFromDate, lDtToDate, cmbDdoList, gStrLocCode);

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}

		return resultDataList;
	}
	
 //DropDown Query
	public List getAllDDOsInTreasury(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		String lStrLocationId = null;
		if (loginMap.containsKey("locationId")) {
			lStrLocationId = loginMap.get("locationId").toString();
		}
		List<Object> lLstReturnList = null;

		try {
			StringBuilder sb = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			sb.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
			sb.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND LM.cmnLanguageMst.langId = 1");
			sb.append("ORDER BY DM.ddoName ");
			Query selectQuery = lObjSession.createQuery(sb.toString());
			selectQuery.setParameter("locationCode", lStrLocationId);
			List lLstResult = selectQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList;
	}
}

/**End**/