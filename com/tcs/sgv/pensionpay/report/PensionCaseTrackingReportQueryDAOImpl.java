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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Jun 22, 2011
 */
public class PensionCaseTrackingReportQueryDAOImpl extends GenericDaoHibernateImpl
{

	
    /** Global Variable for Resource Bundle */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");    
    
    /** Global Variable for Logger */    
    private static final Logger glogger = Logger.getLogger(PensionCaseTrackingReportQueryDAOImpl.class);    
   
    public PensionCaseTrackingReportQueryDAOImpl(Class type, SessionFactory sessionFactory)
    {
        super(type);
        setSessionFactory(sessionFactory);
    }
    
 
    public List getPensionCaseTrackingReport(ReportVO lObjReport,String lStrLocationCode,String lStrFromDate,String lStrToDate,String lStrPpoNo)
	{
    	ArrayList lArrListOuter = null;
    	SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    	Calendar time = new GregorianCalendar();
    	Session lHibSession = getReadOnlySession();
    	String lStrRoleId = null;
    	String lStrEmpName = null;
    	CommonReportDAO lObjCommonReportDAO = null;
    	try
		{
	
			StringBuilder lSBQuery = new StringBuilder();
			lObjCommonReportDAO = new CommonReportDAOImpl(TrnPensionRqstHdr.class,lHibSession.getSessionFactory());
			lSBQuery.append(" SELECT req.ppo_inward_date,req.ppo_no,hdr.first_name,hdr.date_of_retirement,req.commencement_date,req.pension_type,req.case_status,'' AS LyingWith ");
			lSBQuery.append(" FROM trn_pension_rqst_hdr req,mst_pensioner_dtls dtls,mst_pensioner_hdr hdr ");
			lSBQuery.append(" WHERE req.pensioner_code = dtls.pensioner_code AND req.pensioner_code = hdr.pensioner_code AND dtls.pensioner_code = hdr.pensioner_code");				
			
			if (lStrFromDate != null)
			{
				lSBQuery.append(" and req.ppo_Inward_Date >=:fromDate");
			}
			if (lStrToDate != null)
			{
				lSBQuery.append(" and req.ppo_Inward_Date <=:toDate");
			}
			if (lStrPpoNo!= null && lStrPpoNo.length()>0 )
			{
				lSBQuery.append(" and req.ppo_No like '" + lStrPpoNo.trim() + "' ");
			}
			
			SQLQuery Query = lHibSession.createSQLQuery(lSBQuery.toString());
	
			if (lStrFromDate != null)
			{
				Query.setDate("fromDate", lObjSimpleDateFormat.parse(lStrFromDate));
			}
			if (lStrToDate != null)
			{
				Query.setDate("toDate", lObjSimpleDateFormat.parse(lStrToDate));
			}
			
			
		//	Query.setString("locCode", lStrLocationCode);
		
			List lLstFinal = Query.list();
			if (lLstFinal != null && !lLstFinal.isEmpty())
			{
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal.iterator();
				while (it.hasNext())
				{
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					if(tuple[0] != null)
					{
						lArrListInner.add(IFMSCommonServiceImpl.getStringFromDate((Date) tuple[0]));// inwd date
					}
					if(tuple[1] != null)
					{
						lArrListInner.add(tuple[1]);// ppo_no
					}
					if(tuple[2] != null)
					{
						lArrListInner.add(tuple[2]);// pensioner_name
					}
					if(tuple[3] != null)
					{
						lArrListInner.add(tuple[3]);// retirement_date
					}
					if(tuple[4] != null)
					{
						lArrListInner.add(tuple[4]);// commencement_date
					}
					if(tuple[5] != null)
					{
						lArrListInner.add(tuple[5]);// pension_type
					}
					if(tuple[6] != null)
					{
						lArrListInner.add(tuple[6]);// case_status
					}
					if("ROLEID."+tuple[6].toString().trim()!= null)
					{
						lStrRoleId = gObjRsrcBndle.getString("ROLEID."+tuple[6].toString().trim());
						lStrEmpName = lObjCommonReportDAO.getEmpNameFromRoleId(lStrRoleId, lStrLocationCode);
						lArrListInner.add(lStrEmpName); // Lying with
					}
					
					lArrListOuter.add(lArrListInner);
				}
			}
		} 
	catch(Exception e)
	{
		//e.printStackTrace();
		glogger.error("Exception in PensionCaseTrackingReportQueryDAOImpl.getPensionCaseTrackingReport is ::" + e.getMessage(), e);
		
	}
	return lArrListOuter;
	}
}


