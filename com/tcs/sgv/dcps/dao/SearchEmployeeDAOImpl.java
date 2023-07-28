/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 25, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.dcps.valueobject.MstEmp;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 25, 2011
 */
public class SearchEmployeeDAOImpl extends GenericDaoHibernateImpl implements
		SearchEmployeeDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	/**
	 * 
	 * @param type
	 * @param sessionFactory
	 */
	public SearchEmployeeDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);

		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public List searchEmployees(String lStrDcpsId, String lStrEmpName,
			Date lDateEmpDOB, String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List EmployeeList = null;

		lStrEmpName = lStrEmpName.toUpperCase();

		lSBQuery
				.append(" SELECT EM.dcpsEmpId, EM.name,EM.dcpsId, EM.gender, EM.dob, DO.dcpsDdoOfficeName,ODM.dsgnName,EM.pfdChangedBySRKA ");

		if (lStrDcpsId.equalsIgnoreCase("")) {
			lSBQuery
					.append(" FROM MstEmp EM,DdoOffice DO,OrgDesignationMst ODM WHERE UPPER(EM.name) like :empName and EM.dob = :empDOB AND DO.dcpsDdoOfficeIdPk=EM.currOff AND ODM.dsgnId=EM.designation");
		}

		if (lStrEmpName.equalsIgnoreCase("") && lDateEmpDOB == null) {
			lSBQuery
					.append(" FROM MstEmp EM,DdoOffice DO,OrgDesignationMst ODM WHERE EM.dcpsId = :dcpsId AND DO.dcpsDdoOfficeIdPk=EM.currOff AND ODM.dsgnId=EM.designation ");
		}

		lSBQuery.append(" AND EM.regStatus IN (1,2) AND EM.ddoCode = :ddoCode");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		if (lStrDcpsId != "") {
			lQuery.setParameter("dcpsId", lStrDcpsId);
		}

		if (lStrEmpName != "" && lDateEmpDOB != null) {
			lQuery.setParameter("empName", '%' + lStrEmpName + '%');
			lQuery.setParameter("empDOB", lDateEmpDOB);
		}

		lQuery.setParameter("ddoCode", lStrDdoCode);
		EmployeeList = lQuery.list();

		return EmployeeList;
	}
	
	// Old HQL Query
/*
	
	public List searchEmps(String lStrSevarthId, String lStrName,
			String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List EmployeeList = null;

		lStrSevarthId = lStrSevarthId.toUpperCase();
		lStrName = lStrName.toUpperCase();

		lSBQuery
				.append(" SELECT EM.dcpsEmpId, EM.name,EM.dcpsId, EM.gender, EM.dob, DO.dcpsDdoOfficeName,ODM.dsgnName,EM.pfdChangedBySRKA,EM.sevarthId ");
		lSBQuery.append(" FROM MstEmp EM,DdoOffice DO,OrgDesignationMst ODM ");
		lSBQuery.append(" WHERE ");
		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lSBQuery.append(" UPPER(EM.sevarthId) = :sevarthId ");
		}
		if (lStrName != null && !"".equals(lStrName)) {
			if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
				lSBQuery.append(" AND ");
			}
			lSBQuery.append(" UPPER(EM.name) = :empName ");
		}

		lSBQuery
				.append(" AND DO.dcpsDdoOfficeIdPk=EM.currOff AND ODM.dsgnId=EM.designation ");
		lSBQuery.append(" AND EM.regStatus IN (1,2) AND EM.ddoCode = :ddoCode");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());

		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lQuery.setParameter("sevarthId", lStrSevarthId.trim());
		}
		if (lStrName != null && !"".equals(lStrName)) {
			lQuery.setParameter("empName", lStrName.trim());
		}

		lQuery.setParameter("ddoCode", lStrDdoCode);
		EmployeeList = lQuery.list();

		return EmployeeList;
	}

*/
	// New SQL Query
	
	public List searchEmps(String lStrSevarthId, String lStrName,
			String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List EmployeeList = null;
		
		Date lDtCurrDate = SessionHelper.getCurDate();

		lStrSevarthId = lStrSevarthId.toUpperCase().trim();
		lStrName = lStrName.toUpperCase().trim();

		lSBQuery.append(" SELECT EM.dcps_emp_Id, EM.emp_name,EM.dcps_Id, EM.gender, EM.dob, nvl(DO.off_name,''),nvl(OD.DSGN_NAME,''),EM.PFD_CHANGED_BY_SRKA,EM.sevarth_Id ");
		lSBQuery.append(" from mst_dcps_Emp EM ");
		lSBQuery.append(" left join mst_dcps_ddo_office DO on DO.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF ");
		lSBQuery.append(" left join org_designation_mst OD on OD.DSGN_ID = EM.DESIGNATION");
		lSBQuery.append(" where EM.REG_STATUS in (1,2)");
		lSBQuery.append(" and EM.DDO_CODE  = '" + lStrDdoCode + "'");
		
		
		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			
			lSBQuery.append(" and UPPER(SEVARTH_ID) = '" + lStrSevarthId + "'");
		}
		if (lStrName != null && !"".equals(lStrName)) {
			
			lSBQuery.append(" and UPPER(EM.emp_name) = '" + lStrName + "'");
		}
		
		lSBQuery.append(" and ( EM.EMP_SERVEND_DT is null or EM.EMP_SERVEND_DT  >= :currentDate ) ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setDate("currentDate", lDtCurrDate);
		

		EmployeeList = lQuery.list();

		return EmployeeList;
	}
	
	public List getPayDetails(Long lLngEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List PayList = null;

		lSBQuery
				.append("Select EM.currOff,EM.cadre,EM.designation,EM.payCommission,EM.payScale,EM.basicPay,EM.billGroupId");
		lSBQuery.append("from MstEmp EM, DdoOffice DO");
		lSBQuery.append("where EM.dcpsEmpId = :EmpId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("EmpId", lLngEmpId);
		PayList = lQuery.list();

		return PayList;
	}

	public List searchEmployeesForSRKA(String lStrDcpsId, String lStrEmpName,
			Date lDateEmpDOB) {

		StringBuilder lSBQuery = new StringBuilder();
		List EmployeeList = null;

		lStrEmpName = lStrEmpName.toUpperCase();

		lSBQuery
				.append(" SELECT EM.dcpsEmpId, EM.name,EM.dcpsId, EM.gender, EM.dob, DO.dcpsDdoOfficeName,ODM.dsgnName ");

		if (lStrDcpsId.equalsIgnoreCase("")) {
			lSBQuery
					.append(" FROM MstEmp EM,DdoOffice DO,OrgDesignationMst ODM WHERE UPPER(EM.name) like :empName and EM.dob = :empDOB AND DO.dcpsDdoOfficeIdPk=EM.currOff AND ODM.dsgnId=EM.designation");
		}

		if (lStrEmpName.equalsIgnoreCase("") && lDateEmpDOB == null) {
			lSBQuery
					.append(" FROM MstEmp EM,DdoOffice DO,OrgDesignationMst ODM WHERE EM.dcpsId = :dcpsId AND DO.dcpsDdoOfficeIdPk=EM.currOff AND ODM.dsgnId=EM.designation ");
		}

		lSBQuery.append(" AND EM.regStatus IN (1,2) ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		if (lStrDcpsId != "") {
			lQuery.setParameter("dcpsId", lStrDcpsId);
		}

		if (lStrEmpName != "" && lDateEmpDOB != null) {
			lQuery.setParameter("empName", '%' + lStrEmpName + '%');
			lQuery.setParameter("empDOB", lDateEmpDOB);
		}

		EmployeeList = lQuery.list();

		return EmployeeList;
	}

	/*
	public List searchEmpsForSRKA(String lStrSevarthId, String lStrName) {

		StringBuilder lSBQuery = new StringBuilder();
		List EmployeeList = null;

		lStrSevarthId = lStrSevarthId.toUpperCase();
		lStrName = lStrName.toUpperCase();

		lSBQuery
				.append(" SELECT EM.dcpsEmpId, EM.name,EM.dcpsId, EM.gender, EM.dob, DO.dcpsDdoOfficeName,ODM.dsgnName,EM.sevarthId,EM.ddoCode,DO.dcpsDdoOfficeName");
		lSBQuery.append(" FROM MstEmp EM,DdoOffice DO,OrgDesignationMst ODM ");
		lSBQuery.append(" WHERE ");

		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lSBQuery.append(" UPPER(EM.sevarthId) = :sevarthId ");
		}
		if (lStrName != null && !"".equals(lStrName)) {
			if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
				lSBQuery.append(" AND ");
			}
			lSBQuery.append(" UPPER(EM.name) = :empName ");
		}

		lSBQuery
				.append(" AND DO.dcpsDdoOfficeIdPk=EM.currOff AND ODM.dsgnId=EM.designation ");
		lSBQuery.append(" AND EM.regStatus IN (1,2) ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());

		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lQuery.setParameter("sevarthId", lStrSevarthId.trim());
		}
		if (lStrName != null && !"".equals(lStrName)) {
			lQuery.setParameter("empName", lStrName.trim());
		}

		EmployeeList = lQuery.list();

		return EmployeeList;
	}
	*/
	
	
	public List searchEmpsForSRKA(String lStrSevarthId, String lStrName) {

		StringBuilder lSBQuery = new StringBuilder();
		
		StringBuilder lSBQuery1 = new StringBuilder();
		
		
		
		
		
		
		
		List EmployeeList = null;

		lStrSevarthId = lStrSevarthId.toUpperCase();
		lStrName = lStrName.toUpperCase();
		String reg_status="";
		String Dcps_or_gpf="";
		
		//ADDED BY POONAM FOR FORM 1 CHANGES

		List DesigList = new ArrayList();

		lSBQuery1.append(" select EM.REG_STATUS ,EM.DCPS_OR_GPF  FROM MST_DCPS_EMP EM  WHERE ");
		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lSBQuery1.append(" UPPER(EM.SEVARTH_ID) = :sevarthId ");
		}
		if (lStrName != null && !"".equals(lStrName)) {
			if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
				lSBQuery1.append(" AND ");
			}
			lSBQuery1.append(" UPPER(EM.emp_name) = :empName ");
		}

		Query lQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());
		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lQuery1.setParameter("sevarthId", lStrSevarthId.trim().toUpperCase());
		}
		if (lStrName != null && !"".equals(lStrName)) {
			lQuery1.setParameter("empName", lStrName.trim().toUpperCase());
		}
		gLogger.info("Inside the if of dcps employee " + lQuery1.toString());

		DesigList = lQuery1.list();

		
		Object[] obj;
		
	    if(DesigList!=null && !DesigList.isEmpty()){
		  Iterator it =  DesigList.iterator();
          while (it.hasNext()) {
        	  obj = (Object[]) it.next();
        	  reg_status=(obj[0].toString());
        	  Dcps_or_gpf=(obj[1].toString());
        	  gLogger.info("Inside the if of dcps employee " + reg_status);
  	        
	          gLogger.info("Inside the if of dcps employee " + Dcps_or_gpf);
          }
	    }
     //ENDED BY POONAM 
	
		
		
		
		
		
		
		
		
		
		

		lSBQuery.append(" SELECT EM.dcps_emp_Id, EM.emp_name,EM.dcps_Id, EM.gender, EM.dob, nvl(DO.off_name,''),nvl(OD.DSGN_NAME,''),EM.sevarth_Id,EM.DDO_CODE,nvl(DO.off_name,'') ,cmn.LOC_NAME ,EM.REG_STATUS ,EM.DCPS_OR_GPF ");
		
		 if ((reg_status.equals("1")) && (Dcps_or_gpf.equals("Y")))
	      {
		lSBQuery.append("  ,EM.PRAN_NO,cmn1.LOOKUP_NAME ");
	      }
		lSBQuery.append(" from mst_dcps_Emp EM ");
		lSBQuery.append(" left join mst_dcps_ddo_office DO on DO.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF ");
		lSBQuery.append(" left join org_designation_mst OD on OD.DSGN_ID = EM.DESIGNATION");
		lSBQuery.append(" inner join cmn_location_mst cmn  on cmn.loc_id = EM.PARENT_DEPT ");
		 if ((reg_status.equals("1")) && (Dcps_or_gpf.equals("Y")))
	      {
		lSBQuery.append(" left join CMN_LOOKUP_MST  cmn1  on cmn1.LOOKUP_ID = em.AC_DCPS_MAINTAINED_BY  ");
	      }
		lSBQuery.append(" WHERE ");

		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lSBQuery.append(" UPPER(EM.SEVARTH_ID) = :sevarthId ");
		}
		if (lStrName != null && !"".equals(lStrName)) {
			if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
				lSBQuery.append(" AND ");
			}
			lSBQuery.append(" UPPER(EM.emp_name) = :empName ");
		}

		lSBQuery.append(" AND EM.REG_STATUS IN (1,2) ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		if (lStrSevarthId != null && !"".equals(lStrSevarthId)) {
			lQuery.setParameter("sevarthId", lStrSevarthId.trim().toUpperCase());
		}
		if (lStrName != null && !"".equals(lStrName)) {
			lQuery.setParameter("empName", lStrName.trim().toUpperCase());
		}

		
		gLogger.info("Query to search in dao :"+lSBQuery.toString());
		gLogger.info("Query to search in dao :"+lSBQuery.toString());
		EmployeeList = lQuery.list();

		return EmployeeList;
	}
	

	public List getEmpNameForAutoComplete(String searchKey,
			String lStrSearchType, String lStrDDOCode,String lStrSearchBy) {

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;
		Date lDtCurrDate = SessionHelper.getCurDate();

		sb
				.append("select name,name from MstEmp where UPPER(name) LIKE :searchKey and regStatus in (1,2) ");
		
		if (lStrDDOCode != null) {
			if (!"".equals(lStrDDOCode) && !lStrSearchBy.equals("searchFromDDOSelection")) {
				sb.append(" and ddoCode = :ddoCode");
			}
		}
		
		if(lStrSearchBy.equals("searchFromDDOSelection"))
		{
			sb.append(" and ddoCode is null ");
		}

		if (lStrSearchType != null) {
			if (!"".equals(lStrSearchType)) {
				if (lStrSearchType.trim().equals("OnlyDCPS")) {
					sb.append(" and dcpsId is not null");
				}
			}
		}
		
	/*	sb.append(" and ( servEndDate is null or servEndDate  >= :currentDate ) ");*/

		selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("searchKey", '%' + searchKey + '%');
		/*selectQuery.setDate("currentDate", lDtCurrDate);*/

		if (lStrDDOCode != null) {
			if (!"".equals(lStrDDOCode)) {
				selectQuery.setParameter("ddoCode", lStrDDOCode.trim());
			}
		}

		List resultList = selectQuery.list();

		cmbVO = new ComboValuesVO();

		if (resultList != null && resultList.size() > 0) {
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) it.next();
				cmbVO.setId(obj[1].toString());
				cmbVO.setDesc(obj[1].toString());
				finalList.add(cmbVO);
			}
		}

		return finalList;

	}

	public void UpdateNewPost(Long lLngDcpsEmpId, Long lLngNewPost) {

		StringBuilder lSBQuery = null;
		Query lQuery = null;

		lSBQuery = new StringBuilder();
		lSBQuery.append("UPDATE RltDcpsPayrollEmp set postId = :NewPost");
		lSBQuery.append(" WHERE dcpsEmpId = :dcpsEmpId");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setLong("NewPost", lLngNewPost);
		lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);

		lQuery.executeUpdate();

	}

	public String getDesigFromPost(Long lLongPostId) {
		String lStrDsgnId = null;
		StringBuilder lSBQuery = new StringBuilder();

		List DesigList = new ArrayList();

		lSBQuery
				.append(" SELECT dsgn_id from org_post_details_rlt where post_id = '"
						+ lLongPostId + "'");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		DesigList = lQuery.list();

		if (DesigList.size() != 0) {
			lStrDsgnId = DesigList.get(0).toString();
		}

		return lStrDsgnId;

	}

	public Object[] getBGForPost(Long lLongPostId) {

		Object[] billGroupIdAndDesc = new Object[2];
		StringBuilder lSBQuery = new StringBuilder();

		List BGList = new ArrayList();

		lSBQuery
				.append(" SELECT BG.bill_group_id,BG.description FROM HR_PAY_POST_PSR_MPG PP join  MST_DCPS_BILL_GROUP BG on PP.bill_no = BG.bill_group_id where PP.POST_ID = "
						+ lLongPostId);

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		BGList = lQuery.list();

		if (BGList.size() != 0) {
			billGroupIdAndDesc = (Object[]) BGList.get(0);
		}

		return billGroupIdAndDesc;

	}

	public Object[] getGRForPost(Long lLongPostId) {

		Object[] GROrderNoAndName = new Object[2];
		StringBuilder lSBQuery = new StringBuilder();

		List GRList = new ArrayList();

		lSBQuery
				.append(" SELECT order_id,order_name FROM HR_PAY_ORDER_MST where order_id in (SELECT ORDER_id  FROM HR_PAY_ORDER_HEAD_MPG where ORDER_HEAD_ID in (SELECT ORDER_HEAD_ID FROM HR_PAY_ORDER_HEAD_POST_MPG where post_id = "
						+ lLongPostId + "))");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		GRList = lQuery.list();

		if (GRList.size() != 0) {
			GROrderNoAndName = (Object[]) GRList.get(0);
		}

		return GROrderNoAndName;

	}

	//Added by pooja - 28-03-2019 For Save EMP_GROUP
	public String getEmpGroup(Long lStrDcpsEmpId,String lStrCadre) {
		String lStrEmpGroup = null;
		StringBuilder lSBQuery = new StringBuilder();
		
        lSBQuery.append("SELECT cmn.LOOKUP_NAME FROM MST_DCPS_CADRE cadre INNER join CMN_LOOKUP_MST cmn on cmn.LOOKUP_ID = cadre.GROUP_ID where cadre.CADRE_ID= '"+lStrCadre+"'");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.error("getEmpGroupp :: "+lSBQuery.toString());
		lStrEmpGroup = (String) lQuery.list().get(0);
		logger.error(" lStrEmpGroup: " + lStrEmpGroup);
		
		return lStrEmpGroup;

	}
	
	//Added by pooja 01122020 - Change pay post details
	
/*
			public List getAllDcpsEmployees(String lStrSevarthId, String lStrName,
					String lStrDdoCode) {

				StringBuilder lSBQuery = new StringBuilder();
				List EmployeeList = null;
				
				Date lDtCurrDate = SessionHelper.getCurDate();

				lStrSevarthId = lStrSevarthId.toUpperCase().trim();
				lStrName = lStrName.toUpperCase().trim();

				lSBQuery.append(" SELECT EM.dcps_emp_Id, EM.emp_name,EM.dcps_Id, EM.gender, EM.dob, nvl(DO.off_name,''),nvl(OD.DSGN_NAME,''),EM.PFD_CHANGED_BY_SRKA,EM.sevarth_Id ");
				lSBQuery.append(" from mst_dcps_Emp EM ");
				lSBQuery.append(" left join mst_dcps_ddo_office DO on DO.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF ");
				lSBQuery.append(" left join org_designation_mst OD on OD.DSGN_ID = EM.DESIGNATION");
				lSBQuery.append(" where EM.REG_STATUS in (1,2)");
				lSBQuery.append(" and EM.DDO_CODE  = '" + lStrDdoCode + "'");
				
				
				if (lStrSevarthId != null && !"-1".equals(lStrSevarthId)) {
					
					lSBQuery.append(" and UPPER(SEVARTH_ID) = '" + lStrSevarthId + "'");
				}
				if (lStrName != null && !"-1".equals(lStrName)) {
					
					lSBQuery.append(" and UPPER(EM.emp_name) = '" + lStrName + "'");
				}
				
				lSBQuery.append(" and ( EM.EMP_SERVEND_DT is null or EM.EMP_SERVEND_DT  >= :currentDate ) ");

				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setDate("currentDate", lDtCurrDate);
				
				logger.info(" lQuery getAllDcpsEmployees: " + lQuery);
				EmployeeList = lQuery.list();

				return EmployeeList;
			}*/
}