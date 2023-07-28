/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	June 13, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 June 13, 2011
 */
public class ChangeEmpDeptDAOImpl extends GenericDaoHibernateImpl implements
		ChangeEmpDeptDAO {
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/pensionproc/PensionCaseConstants");

	public ChangeEmpDeptDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public List getEmpDetails(String strDcpsId) {

		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
				.append("SELECT dcpsEmpId,name,ddoCode,parentDept from MstEmp where dcpsId = :dcpsId");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("dcpsId", strDcpsId);

		List resultList = query.list();
		return resultList;
	}

	public void updateParentDeptOfDDO(String lStrDDOCode,
			String lStrDeptLocCode, String lStrHODLocCode) throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery
					.append("UPDATE OrgDdoMst OD SET OD.deptLocCode = :deptLocCode,OD.hodLocCode = :hodLocCode,OD.adminFlag = 1 ");
			lSBQuery.append(" WHERE OD.ddoCode = :ddoCode  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("deptLocCode", lStrDeptLocCode);
			lQuery.setParameter("hodLocCode", lStrHODLocCode);
			lQuery.setParameter("ddoCode", lStrDDOCode);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
	}

	public void updateParentDeptOfAllEmpsUnderTheDDO(String lStrDDOCode,
			String lStrHODLocCode) throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery
					.append("UPDATE MstEmp EM SET EM.parentDept = :hodLocCode,EM.pfdChangedBySRKA=1 ");
			lSBQuery.append(" WHERE EM.ddoCode = :ddoCode ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("hodLocCode", lStrHODLocCode);
			lQuery.setParameter("ddoCode", lStrDDOCode);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
	}
	
	public void updateDDOCodeOfEmp(String lStrDDOCode,Long lLongDcpsEmpId) throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery
					.append(" UPDATE MstEmp EM SET EM.ddoCode = :ddoCode WHERE EM.dcpsEmpId = :dcpsEmpId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDDOCode);
			lQuery.setParameter("dcpsEmpId", lLongDcpsEmpId);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
	}
	
	public List getAllEmpsUnderDDO(String lStrDdoCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		List EmpList = null;

			lSBQuery.append("Select EM.dcpsEmpId,EM.dcpsId,EM.name");
			lSBQuery.append(" FROM MstEmp EM where regStatus IN (1,2) AND EM.ddoCode= :ddoCode ORDER BY EM.name,EM.dcpsEmpId,EM.dcpsId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);

		EmpList = lQuery.list();

		return EmpList;
	}
	
	public List getFieldHODComboForEmp(Long lLongEmpId)
	throws Exception {

		List<Object> lLstReturnList = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
		
			lSBQuery.append(" SELECT CM.locId,CM.locName FROM CmnLocationMst CM,MstEmp EM where CM.locId = EM.parentDept and EM.dcpsEmpId = :dcpsEmpId");
		
			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());
		
			lObjQuery.setParameter("dcpsEmpId", lLongEmpId);
		
			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}
	
	public List getParentDeptComboForEmp(Long lLongEmpId)
	throws Exception {

		List<Object> lLstReturnList = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
		
			lSBQuery.append(" SELECT CMP.locId,CMP.locName FROM CmnLocationMst CMP,CmnLocationMst CMC, MstEmp EM ");
			lSBQuery.append(" where CMC.locId = EM.parentDept and CMC.parentLocId = CMP.locId and EM.dcpsEmpId = :dcpsEmpId");
			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());
		
			lObjQuery.setParameter("dcpsEmpId", lLongEmpId);
		
			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}
	
	public void updateParentDeptOfEmp(Long lLongEmpId,String lStrHODLocCode) throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			
			lSBQuery.append("UPDATE MstEmp EM set EM.parentDept = :hodLocCode,EM.pfdChangedBySRKA=1 ");
			lSBQuery.append(" WHERE EM.dcpsEmpId = :dcpsEmpId  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("hodLocCode", lStrHODLocCode);
			lQuery.setParameter("dcpsEmpId", lLongEmpId);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
	}


}
