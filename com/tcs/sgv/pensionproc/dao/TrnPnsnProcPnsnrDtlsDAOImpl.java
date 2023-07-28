/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 1, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 1, 2011
 */
public class TrnPnsnProcPnsnrDtlsDAOImpl extends GenericDaoHibernateImpl implements TrnPnsnProcPnsnrDtlsDAO {

	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);


	public TrnPnsnProcPnsnrDtlsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	
	public String getDesignation(Long lLngEmpId) {
		
		String lStrDesignationName = null;
		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstDsng = null;
		try {
			lSBQuery.append(" select ODM.dsgnName from OrgDesignationMst ODM,OrgEmpMst OEM,OrgUserpostRlt UPR,OrgPostDetailsRlt PDR where");
			lSBQuery.append(" OEM.empId = :empId and UPR.orgUserMst.userId = OEM.orgUserMst.userId and UPR.activateFlag = 1");
			lSBQuery.append(" and PDR.orgPostMst.postId =  UPR.orgPostMstByPostId.postId and ODM.dsgnId = PDR.orgDesignationMst.dsgnId ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("empId", lLngEmpId);
			lLstDsng = lHibQry.list();
			
			if(!lLstDsng.isEmpty())
				lStrDesignationName = lLstDsng.get(0);
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getDesignation() : Error is :" + e, e);

		}
		return lStrDesignationName;		
	}


	
	public Long getFieldDept(String lStrDdoCode) {
		
		Long lLngFieldDept = null;
		StringBuilder lSBQuery = new StringBuilder();
		List lLstFieldDept = null;
		try {
			lSBQuery.append("select hodLocCode from OrgDdoMst where ddoCode = :ddoCode");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("ddoCode", lStrDdoCode);
			lLstFieldDept = lHibQry.list();
			
			if(!lLstFieldDept.isEmpty())
				lLngFieldDept = Long.parseLong(lLstFieldDept.get(0).toString());
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getFieldDept() : Error is :" + e, e);

		}
		return lLngFieldDept;		
	}


	
	public DdoOffice getEmpOfficeDtls(Long lLngOfficeId) {
		
		StringBuilder lSBQuery = new StringBuilder();
		List<DdoOffice> lLstOfficeDtls = null;
		DdoOffice lObjDdoOffice = null;
		try {
			lSBQuery.append("from DdoOffice where dcpsDdoOfficeIdPk = :OfficeId");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("OfficeId", lLngOfficeId);
			lLstOfficeDtls = lHibQry.list();
			if(!lLstOfficeDtls.isEmpty())
				lObjDdoOffice = lLstOfficeDtls.get(0);
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getEmpOfficeDtls() : Error is :" + e, e);

		}
		return lObjDdoOffice;
	}



	public String getBankAccNo(Long lLngEmpId) {
		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstBankAccNo = null;
		String lStrBankAccNo = null;
		try {
			lSBQuery.append("select BK.bankAcctNo from HrEisBankDtls BK,HrEisEmpMst EM where EM.orgEmpMst.empId = :empId and BK.hrEisEmpMst.empId = EM.empId");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("empId", lLngEmpId);
			lLstBankAccNo = lHibQry.list();
			if(!lLstBankAccNo.isEmpty())
				lStrBankAccNo = lLstBankAccNo.get(0);
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnProcPnsnrDtlsDAOImpl : getBankAccNo() : Error is :" + e, e);

		}
		return lStrBankAccNo;
	}
}
