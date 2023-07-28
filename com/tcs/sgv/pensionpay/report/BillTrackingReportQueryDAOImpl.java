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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.text.DateFormatter;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pdfbox.util.DateConverter;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.apps.util.DAOFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.IFMSCommonDAO;
import com.tcs.sgv.common.dao.IFMSCommonDAOImpl;
import com.tcs.sgv.common.service.IFMSCommonService;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valueobject.DDODetailsVO;
import com.tcs.sgv.common.valueobject.MstBillType;
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
public class BillTrackingReportQueryDAOImpl extends GenericDaoHibernateImpl
{
	/** Global Variable for Session Class */
    private Session ghibSession = null;    

    /** Global Variable for Resource Bundle */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");    
    
    /** Global Variable for Logger */    
    private static final Logger glogger = Logger.getLogger(PensionCaseTrackingReportQueryDAOImpl.class);    
    
    /** Global Variable for Simple Date Format */
    private SimpleDateFormat gObjDtFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    

  
    public BillTrackingReportQueryDAOImpl(Class type, SessionFactory sessionFactory)
    {
        super(type);
        setSessionFactory(sessionFactory);
    }
    
    
    public List getBillTrackingReport(ReportVO lObjReport,String lStrLocationCode,String lStrFromDate,String lStrToDate,String lStrBillNo,String lStrBillType)
    {	
    	ArrayList lArrListOuter = new ArrayList();
    	SQLQuery lSQLQuery = null;
    	Session lHibSession = null;
    	CommonReportDAO lObjCommonReportDAO = null;
    	String lStrRoleId = null;
    	String lStrEmpName = null;
    	
    	try 
		{
			lHibSession = getReadOnlySession();
			lObjCommonReportDAO = new CommonReportDAOImpl(null,lHibSession.getSessionFactory());
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT reg.bill_date,reg.bill_cntrl_no,hdr.bill_type,dtls.ppo_no,party.party_name,reg.bill_gross_amount,reg.curr_bill_status ,'' " +
					" FROM trn_bill_register reg,trn_pension_bill_dtls dtls,trn_pension_bill_hdr hdr,rlt_bill_party party" +
					" WHERE reg.bill_no = hdr.bill_no AND hdr.trn_pension_bill_hdr_id = dtls.trn_pension_bill_hdr_id  AND party.bill_no = reg.bill_no" +
					" AND party.bill_no = hdr.bill_no ");
			
			// for Right Alignment format
			StyleVO[] RightAlignVO = new StyleVO[2];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			
			
			
			if (lStrFromDate != null)
			{
				lSBQuery.append(" AND reg.bill_Date >=:fromDate ");
			}
			if (lStrToDate != null)
			{
				lSBQuery.append(" AND reg.bill_Date <=:toDate ");
			}
			if (lStrBillNo != "" && lStrBillNo.length() > 0)
			{
				lSBQuery.append(" AND reg.bill_No =:billNo ");
			}
			if(lStrBillType != null)
			{
				lSBQuery.append(" ANd hdr.bill_type=:billType  ");
			}
			if(lStrLocationCode != null)
			{
				lSBQuery.append(" AND hdr.location_code=:locationCode ");
			}
			
			lSQLQuery = lHibSession.createSQLQuery(lSBQuery.toString());
			lSQLQuery.setString("locationCode", lStrLocationCode);
			lSQLQuery.setDate("fromDate", gObjDtFormat.parse(lStrFromDate));
			lSQLQuery.setDate("toDate",gObjDtFormat.parse(lStrToDate));
			if (lStrBillNo != "" && lStrBillNo.length() > 0)
			{
				lSQLQuery.setParameter("billNo",lStrBillNo.trim());
			}
			
			lSQLQuery.setParameter("billType",lStrBillType.trim());
			lSQLQuery.setParameter("locationCode",lStrLocationCode.trim());
			List lLstFinal = lSQLQuery.list();
			if (lLstFinal != null && !lLstFinal.isEmpty())
			{
				lArrListOuter = new ArrayList();
				Iterator it = lLstFinal.iterator();
				int lIntSrNo = 1;
				while (it.hasNext())
				{
					Object[] tuple = (Object[]) it.next();
					ArrayList lArrListInner = new ArrayList();
					lArrListInner.add(lIntSrNo);
					if(tuple[0] != null)
					{
						lArrListInner.add(IFMSCommonServiceImpl.getStringFromDate((Date) tuple[0]));// bill date
					}
					if(tuple[1] != null)
					{
						lArrListInner.add(tuple[1].toString().trim());// bill_cntrl_no
					}
					if(tuple[2] != null)
					{
						lArrListInner.add(tuple[2]);// bill_type
					}
					if(tuple[3] != null)
					{
						lArrListInner.add(tuple[3]);// ppo_no
					}
					if(tuple[4] != null)
					{
						lArrListInner.add(tuple[4]);// party_name
					}
					if(tuple[5] != null)
					{
						lArrListInner.add(new StyledData(tuple[5],RightAlignVO));// bill_gross_amount
					}
					if(tuple[6] != null) //status
					{
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_CREATED.toString().trim()))
						{
							lArrListInner.add("Created");	
						}
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_FORW_TO_ATO.toString().trim())|| tuple[6].toString().trim().equals(DBConstants.ST_BILL_FORW_TO_TAUD.toString().trim()) || tuple[6].toString().trim().equals(DBConstants.ST_BILL_FORW_TO_USER.toString().trim()))
						{
							lArrListInner.add("Forwarded");
						}
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_REJECTED.toString().trim())|| tuple[6].toString().trim().equals(DBConstants.ST_BILL_REJECTED_BY_TRSRY.toString().trim()))
						{
							lArrListInner.add("Rejected");
						}
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_DISCARD.toString().trim()))
						{
							lArrListInner.add("Discarded");
						}
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_ECS_GENERTED.toString().trim()))
						{
							lArrListInner.add("ECS genereted");
						}
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_APPROVED.toString().trim()))
						{
							lArrListInner.add("Approved");
						}
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_ARCHEIVED.toString().trim()))
						{
							lArrListInner.add("Archeived");
						}
					}
					if("ROLEID."+tuple[6].toString().trim()!= null) //Lying With
					{
						if(tuple[6].toString().trim().equals(DBConstants.ST_BILL_ECS_GENERTED.toString().trim()))
						{
							lArrListInner.add("ECS already Genereated"); 
						}
						else
						{
							lStrRoleId = gObjRsrcBndle.getString("ROLEID."+tuple[6].toString().trim());
							lStrEmpName = lObjCommonReportDAO.getEmpNameFromRoleId(lStrRoleId, lStrLocationCode);
							lArrListInner.add(lStrEmpName); 
						}
						
					}
					
					lArrListOuter.add(lArrListInner);
					lIntSrNo++;
				}
			}
		} 
		catch (Exception e) 
		{
			 //e.printStackTrace();
			glogger.error("Exception occured in BillTrackingReportQueryDAOImpl.getBillTrackingReport # \n" + e, e);
		}
		return lArrListOuter;
    }
    
    
}
