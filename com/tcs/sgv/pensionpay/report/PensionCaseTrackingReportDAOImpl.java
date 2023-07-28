/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 22, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.apps.util.DAOFactory;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.CmnDistrictMstDAOImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDao;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.event.reports.ReportEvent;
import com.tcs.sgv.common.event.reports.ReportEventListener;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.DDOInfoServiceImpl;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valuebeans.reports.AmountInWords;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportParameterVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.SubReportVO;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.service.ServiceLocator;

import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.wf.exception.HierarchyNotFoundException;

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Jun 22, 2011
 */
public class PensionCaseTrackingReportDAOImpl extends DefaultReportDataFinder implements ReportDataFinder
{

	/**
	 * Global Variable for Logger
	 */
	private Log gLogger = LogFactory.getLog(getClass());

	/**
	 * Global Variable for Sessions Factory
	 */
	private SessionFactory gObjSessionFactory = null;

	/**
	 * Global Variable for Service Locator
	 */
	private ServiceLocator serviceLocator = null;

	/**
	 * Global Variable for LangId
	 */
	private Long langID = null;

	/**
	 * Global Variable for post Id
	 */
	private Long postId = null;

	/**
	 * Global Variable for Location Id
	 */
	private long locID = 0;

	/** Global variable for Treasury Name */
	private String gStrDistrictCode = null;

	private String gStrLocName = null;
	
	/**
	 * Global Variable for Resource Bundle
	 */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");


	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.common.business.reports.ReportDataFinder#findReportData(com.tcs.sgv.common.valuebeans.reports.ReportVO, java.lang.Object)
	 */
	
	public Collection findReportData(ReportVO lObjReport, Object criteria)throws ReportException 
	{

		Map requestAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.REQUEST_ATTRIBUTES);
		Map sessionAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.SESSION_KEYS);
		List lArrReportData = new ArrayList();
		LoginDetails loginVO = (LoginDetails) sessionAttributes
				.get("loginDetails");

		locID = loginVO.getLocation().getLocId();
		postId = loginVO.getLoggedInPost().getPostId();
		langID = loginVO.getLangId();
		String lStrLocationCode = loginVO.getLocation().getLocationCode();
		lObjReport.setLocId(String.valueOf(locID));

		serviceLocator = (ServiceLocator) requestAttributes
				.get("serviceLocator");
		/** get the SessionFactory instance for using it to fetch data... * */
		gObjSessionFactory = serviceLocator.getSessionFactory();
		try
		{
			if (lObjReport.getReportCode().equals("365455")) 
			{
				lArrReportData = getPensionCaseTrackingReport(lObjReport,lStrLocationCode);
			}
		}
		catch(Exception e)
		{
			gLogger.error(e.getMessage(), e);
		}
		return lArrReportData;
	}
	
	public List getPensionCaseTrackingReport(ReportVO lObjReport,String lStrLocationCode) 
	{
		List lArrReturn = new ArrayList();
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		PensionCaseTrackingReportQueryDAOImpl rptQueryDAO = new PensionCaseTrackingReportQueryDAOImpl(TrnPensionRqstHdr.class, gObjSessionFactory);
		rptQueryDAO.setSessionFactory(gObjSessionFactory);
		String lStrFromDate = null;
		String lStrToDate = null;
		String lStrPpoNo = null;
		try 
		{
			if(lObjReport.getParameterValue("PPONo") != "")
				lStrPpoNo = lObjReport.getParameterValue("PPONo").toString();
			
			lStrFromDate = lObjReport.getParameterValue("InwardDatefrom").toString();
			lStrToDate = lObjReport.getParameterValue("InwardDateto").toString();
			lArrReturn = rptQueryDAO.getPensionCaseTrackingReport(lObjReport,lStrLocationCode,lStrFromDate,lStrToDate,lStrPpoNo);
		} 
		catch (Exception e) 
		{
			gLogger.error(e.getMessage(), e);
			
		}
		return lArrReturn;
	}
	
	
	 List getBillTypeList(String lStrLangId, String lStrLocId)
	{
		List lLstBillType = new ArrayList();
		
		try 
		{
			ComboValuesVO lObjCombo1 = new ComboValuesVO();
	        ComboValuesVO lObjCombo2 = new ComboValuesVO();
	        ComboValuesVO lObjCombo3 = new ComboValuesVO();
	        ComboValuesVO lObjCombo4 = new ComboValuesVO();
	        
	        lObjCombo1.setId("9");
	        lObjCombo1.setDesc(gObjRsrcBndle.getString("PPMT.PENSION"));
	        lLstBillType.add(lObjCombo1);
	        
	        lObjCombo2.setId("10");
	        lObjCombo2.setDesc(gObjRsrcBndle.getString("PPMT.CVP"));
	        lLstBillType.add(lObjCombo2);
	        
	        lObjCombo3.setId("11");
	        lObjCombo3.setDesc(gObjRsrcBndle.getString("PPMT.DCRG"));
	        lLstBillType.add(lObjCombo3);
	        
	        lObjCombo4.setId("44");
	        lObjCombo4.setDesc(gObjRsrcBndle.getString("PPMT.MONTHLY"));
	        lLstBillType.add(lObjCombo4);
	       
	        //System.out.println("lLstBillType.size() is" + lLstBillType.size());

	    }
		catch (Exception e) 
		{
			gLogger.error("In  class :::: getBillTypeList method :::: Error is  : " + e, e);
		} 
		
		return lLstBillType;
		
	}
    

	
}
