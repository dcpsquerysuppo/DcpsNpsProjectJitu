/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 23, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Jun 23, 2011
 */
public class BillTrackingReportDAOImpl extends DefaultReportDataFinder implements ReportDataFinder
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
			if (lObjReport.getReportCode().equals("365456")) 
			{
				lArrReportData = getBillTrackingReport(lObjReport,lStrLocationCode);
			}
		}
		catch(Exception e)
		{
			
		}
		return lArrReportData;
	
	}
	
	
	public List getBillTrackingReport(ReportVO lObjReport,String lStrLocationCode) 
	{
		List lArrList = new ArrayList();
		BillTrackingReportQueryDAOImpl rptQueryDAO = new BillTrackingReportQueryDAOImpl(null, gObjSessionFactory);
		rptQueryDAO.setSessionFactory(gObjSessionFactory);
		String lStrFromDate = null;
		String lStrToDate = null;
		String lStrBillNo = null;
		String lStrBillType = null;
		try 
		{
			if(lObjReport.getParameterValue("BillNo")!= null )
				lStrBillNo = lObjReport.getParameterValue("BillNo").toString();
			
			lStrFromDate = lObjReport.getParameterValue("BillDatefrom").toString();
			lStrToDate = lObjReport.getParameterValue("BillDateto").toString();
			lStrBillType = lObjReport.getParameterValue("BillType").toString();
			if(lStrFromDate.length() >0 && lStrToDate.length() >0 && lStrBillType.length() >0 )
			{
				lArrList = rptQueryDAO.getBillTrackingReport(lObjReport,lStrLocationCode,lStrFromDate,lStrToDate,lStrBillNo,lStrBillType);
			}
		} 
		catch (Exception e) 
		{
			gLogger.error(e.getMessage(), e);
			
		}
		return lArrList;
	}
	public List getBillTypeList(String lStrLangId, String lStrLocId)
	{
		List lLstBillType = new ArrayList();
		try 
		{
			ComboValuesVO lObjCombo1 = new ComboValuesVO();
			lObjCombo1.setId(gObjRsrcBndle.getString("PPMT.PENSION"));
	        lObjCombo1.setDesc(gObjRsrcBndle.getString("PPMT.PENSION"));
	        lLstBillType.add(lObjCombo1);
	        
	        ComboValuesVO lObjCombo2 = new ComboValuesVO();
	        lObjCombo2.setId(gObjRsrcBndle.getString("PPMT.CVP"));
	        lObjCombo2.setDesc(gObjRsrcBndle.getString("PPMT.CVP"));
	        lLstBillType.add(lObjCombo2);
	        
	        ComboValuesVO lObjCombo3 = new ComboValuesVO();
	        lObjCombo3.setId(gObjRsrcBndle.getString("PPMT.DCRG"));
	        lObjCombo3.setDesc(gObjRsrcBndle.getString("PPMT.DCRG"));
	        lLstBillType.add(lObjCombo3);
	        
	        ComboValuesVO lObjCombo4 = new ComboValuesVO();
	        lObjCombo4.setId(gObjRsrcBndle.getString("PPMT.MONTHLY"));
	        lObjCombo4.setDesc(gObjRsrcBndle.getString("PPMT.MONTHLY"));
	        lLstBillType.add(lObjCombo4);
		}
		catch (Exception e) 
		{
			gLogger.error("In  class BillTrackingReportDAOImpl :::: getBillTypeList method :::: Error is  : " + e, e);
		} 
		return lLstBillType;
	}
}
