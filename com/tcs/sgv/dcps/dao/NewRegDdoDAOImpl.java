
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

import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpDetails;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.lcm.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.dcps.valueobject.HrPaySubstituteEmpMpg;

import java.text.SimpleDateFormat;


public class NewRegDdoDAOImpl extends GenericDaoHibernateImpl implements
NewRegDdoDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	/**
	 * 
	 * @param type
	 * @param sessionFactory
	 */
	public NewRegDdoDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/**
	 * DAO method to used to get the Bank Names according from database
	 * 
	 * @param
	 * @return List
	 */

	public List getCurrentOffices() {

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice";

		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		List resultList = selectQuery.list();

		cmbVO = new ComboValuesVO();

		if (resultList != null && resultList.size() > 0) {
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("-- Select --");
			finalList.add(cmbVO);
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) it.next();
				cmbVO.setId(obj[0].toString());
				cmbVO.setDesc(obj[1].toString());
				finalList.add(cmbVO);
			}
		}

		return finalList;
	}

	public List getOfficeDetails(Long lLngOfficeId) {

		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
		.append("SELECT dcpsDdoOfficeAddress1,dcpsDdoOfficeTelNo1,dcpsDdoOfficeTelNo2,dcpsDdoOfficeFax,dcpsDdoOfficeEmail,dcpsDdoOfficeAddress2,dcpsDdoOfficeCityClass from DdoOffice where dcpsDdoOfficeIdPk = :officeId");

		Query query = ghibSession.createQuery(lSBQuery.toString());
		query.setParameter("officeId", lLngOfficeId);

		List resultList = query.list();
		return resultList;
	}

	/**
	 * DAO method to used to get the designation based on office names
	 * 
	 * @param String
	 * @return List
	 */
	public List getDesignations(String lStrCurrOffice) throws Exception {

		List<Object> lLstReturnList = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
			.append("SELECT OD.designationName FROM RltOfficeDesig OD,MstOffice OM ");
			lSBQuery
			.append(" WHERE OM.officeId = OD.officeId AND OM.officeName = :officeName");
			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());
			lObjQuery.setParameter("officeName", lStrCurrOffice);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();

				String lStrDesigName = null;
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					lStrDesigName = (String) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(lStrDesigName);
					lObjComboValuesVO.setDesc(lStrDesigName);

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
			throw e;
		}

		return lLstReturnList;

	}

	/**
	 * DAO method to used to get the Bank Names according from database
	 * 
	 * @param
	 * @return List
	 */

	/**
	 * DAO method to used to get the group names based on cadre
	 * 
	 * @param String
	 * @return List
	 */

	public List getGroupName(Long cadreId) {

		Session hibSession = getSession();
		String query = "select CLM.lookupName,CD.superAntunAge FROM DcpsCadreMst CD,CmnLookupMst CLM WHERE CD.groupId=CLM.lookupId AND CD.cadreId= :cadreId";
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		List lLstGroupsSuperAnnAges = null;
		Query selectQuery = hibSession.createQuery(sb.toString());
		selectQuery.setParameter("cadreId", cadreId);
		lLstGroupsSuperAnnAges = selectQuery.list();
		return lLstGroupsSuperAnnAges;
	}

	/**
	 * DAO method to used to get the group names based on cadre
	 * 
	 * @param Integer
	 *            ,String
	 * @return List
	 */

	/**
	 * DAO method to used to get the nominee details for the employee
	 * 
	 * @param String
	 * @return List
	 */
	public List getNominees(String empId) {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();

		List<MstEmpNmn> NomineesList = null;

		lSBQuery.append(" FROM MstEmpNmn WHERE dcpsEmpId.dcpsEmpId = :empId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", Long.parseLong(empId));

		NomineesList = lQuery.list();

		return NomineesList;
	}

	/**
	 * DAO method to used to get the count of the employees with the same name,
	 * designation and department useful for generation of DCPS ID
	 * 
	 * @param String
	 *            , String, String, String, String
	 * @return List
	 */

	public Long getCountOfEmpOfSameNameAndDesigAndSameDept(String lStrEmpName,
			String lStrDesig, String lStrDept) {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long count = 0L;

		lSBQuery.append(" select count(*) FROM MstEmp WHERE name = :Fname");
		lSBQuery.append(" and designation = :designation");
		lSBQuery.append(" and parentDept = :ParentDept");
		lSBQuery.append(" and regStatus = :regStatus");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("Fname", lStrEmpName);
		lQuery.setParameter("designation", lStrDesig);
		lQuery.setParameter("ParentDept", lStrDept);
		lQuery.setParameter("regStatus", 1L);

		tempList = lQuery.list();
		count = tempList.get(0);
		return count;

	}

	public void deleteNomineesForGivenEmployee(Long lLongEmpId) {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery
		.append(" delete from MstEmpNmn where dcpsEmpId.dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", lLongEmpId);
		lQuery.executeUpdate();

	}

	public void deleteRltPayrollEmpForGivenEmployee(Long lLongEmpId) {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery
		.append(" delete from RltDcpsPayrollEmp where dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", lLongEmpId);
		lQuery.executeUpdate();

	}

	public void updatePhyStatus(Long lLongEmpId) {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery
		.append(" Update RltPhyFormStatus SET PhyFormRcvd = :RltPhyFormStatus where dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("RltPhyFormStatus", 0L);
		lQuery.setParameter("dcpsEmpId", lLongEmpId);
		lQuery.executeUpdate();

	}

	public List getAllDcpsEmployees(String lStrUser, String lStrPostId,
			String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		List<MstEmp> EmpList = null;

		if (lStrUser.equals("Asst")) {
			lSBQuery
			.append(" Select EM.dcpsEmpId,EM.name,EM.dob,EM.regStatus,EM.sentBackRemarks,EM.dupEmpSentToMDC");
			lSBQuery
			.append(" FROM MstEmp EM where (EM.dupEmpSentToMDC is null or EM.dupEmpSentToMDC <> 2) and EM.regStatus IN (0,-1) AND EM.formStatus = :formStatus and EM.ddoCode= :ddoCode order by EM.regStatus,EM.dcpsEmpId,EM.name,EM.dob,EM.sentBackRemarks ");
			// EM.dupEmpSentToMDC <> 2
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("formStatus", 0L);
			lQuery.setParameter("ddoCode", lStrDdoCode);

			// Below condition added not to show the duplicate employees which are sent to MDC for approval

		} else if (lStrUser.equals("DDO")) {
			lSBQuery
			.append(" Select EM.dcpsEmpId,EM.name,EM.dob,EM.regStatus,EM.sentBackRemarks,DM.dsgnName,EM.gender");
			lSBQuery
			.append(" FROM MstEmp EM, WfJobMst WJ, OrgDesignationMst DM");
			lSBQuery
			.append(" WHERE WJ.jobRefId = EM.dcpsEmpId AND WJ.lstActPostId = :postId AND EM.regStatus = :regStatus AND  WJ.wfDocMst.docId = :docId ");
			lSBQuery
			.append(" AND EM.designation=DM.dsgnId order by EM.regStatus,EM.dcpsEmpId,EM.name,EM.dob,EM.sentBackRemarks,DM.dsgnName,EM.gender");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("regStatus", 0L);
			lQuery.setParameter("postId", lStrPostId);
			lQuery.setParameter("docId", 700001L);
		}
		logger.info("Draft query"+lQuery);
		EmpList = lQuery.list();

		return EmpList;
	}

	public List getAllDcpsEmployeesForDesig(String lStrUser, String lStrPostId,
			String lStrDdoCode, String lStrSearchValue) {

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		List<MstEmp> EmpList = null;

		lSBQuery
		.append("Select EM.dcpsEmpId, EM.name,EM.dob,EM.regStatus, EM.sentBackRemarks,EM.dupEmpSentToMDC");
		lSBQuery
		.append(" FROM MstEmp EM where regStatus IN (0,-1) AND EM.formStatus = :formStatus and EM.ddoCode= :ddoCode ");
		lSBQuery.append(" and EM.designation = :designation");

		// Below condition added not to show the duplicate employees which are sent to MDC for approval
		lSBQuery.append(" and EM.dupEmpSentToMDC <> 2 ");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("designation", lStrSearchValue.trim());
		lQuery.setParameter("formStatus", 0L);
		lQuery.setParameter("ddoCode", lStrDdoCode);

		EmpList = lQuery.list();

		return EmpList;
	}

	public List getAllDcpsEmployeesForCaseStatus(String lStrUser,
			String lStrPostId, String lStrDdoCode, String lStrSearchValue) {

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		List<MstEmp> EmpList = null;

		lSBQuery
		.append("Select EM.dcpsEmpId, EM.name,EM.dob,EM.regStatus, EM.sentBackRemarks,EM.dupEmpSentToMDC");
		lSBQuery
		.append(" FROM MstEmp EM where EM.formStatus = :formStatus and EM.ddoCode= :ddoCode ");

		if (lStrSearchValue.trim().equals("Draft")) {
			lSBQuery.append(" and EM.regStatus = 0");
		}
		if (lStrSearchValue.trim().equals("Rejected")) {
			lSBQuery.append(" and EM.regStatus = -1");
		}

		// Below condition added not to show the duplicate employees which are sent to MDC for approval
		lSBQuery.append(" and EM.dupEmpSentToMDC <> 2 ");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("formStatus", 0L);
		lQuery.setParameter("ddoCode", lStrDdoCode);

		EmpList = lQuery.list();

		return EmpList;
	}

	public DdoOffice getDdoOfficeVO(Long ddoOfficeId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM DdoOffice");
		lSBQuery.append(" WHERE dcpsDdoOfficeIdPk = :ddoOfficeId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoOfficeId", ddoOfficeId);
		logger.info(lQuery);
		DdoOffice lObjDdoOffice = (DdoOffice) lQuery.uniqueResult();

		return lObjDdoOffice;
	}

	public MstEmp getEmpVOForEmpId(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM MstEmp");
		lSBQuery.append(" WHERE dcpsEmpId = :dcpsEmpId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		MstEmp lObjEmpVO = (MstEmp) lQuery.uniqueResult();

		return lObjEmpVO;
	}

	public MstEmpDetails getEmpVOArchivedForEmpId(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM MstEmpDetails");
		lSBQuery.append(" WHERE dcpsEmpId = :dcpsEmpId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		MstEmpDetails lObjEmpVO = (MstEmpDetails) lQuery.uniqueResult();

		return lObjEmpVO;
	}

	public MstEmp getEmpVOForDCPSId(String dcpsId,String ddoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM MstEmp");
		lSBQuery.append(" WHERE dcpsId = :dcpsId and ddoCode = :ddoCode");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsId", dcpsId);
		lQuery.setParameter("ddoCode", ddoCode);

		MstEmp lObjEmpVO = (MstEmp) lQuery.uniqueResult();

		return lObjEmpVO;
	}

	public Boolean checkDcpsEmpPayrollIdForEmpIdExists(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery
		.append(" select dcpsPayrollEmpId FROM RltDcpsPayrollEmp WHERE dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		tempList = lQuery.list();
		if (tempList.size() == 0) {
			flag = false;
		}
		return flag;

	}

	public Long getDcpsEmpPayrollIdForEmpId(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long dcpsPayrollEmpId = 0L;

		lSBQuery
		.append(" select dcpsPayrollEmpId FROM RltDcpsPayrollEmp WHERE dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		tempList = lQuery.list();
		if(tempList!=null && tempList.size()>0)
			dcpsPayrollEmpId = tempList.get(0);
		return dcpsPayrollEmpId;

	}

	public Long getTotalEmployees() {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long count = 0L;

		lSBQuery.append(" select count(*) FROM MstEmp ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());

		tempList = lQuery.list();
		count = tempList.get(0);
		return count;

	}

	public RltDcpsPayrollEmp getPayrollVOForEmpId(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		RltDcpsPayrollEmp lObjPayrollVO = null;
		Query lQuery = null;
		try{
		lSBQuery.append("FROM RltDcpsPayrollEmp");
		lSBQuery.append(" WHERE dcpsEmpId = :dcpsEmpId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		logger.info("getPayrollVOForEmpId------"+lQuery+" "+dcpsEmpId);
		lObjPayrollVO = (RltDcpsPayrollEmp) lQuery.uniqueResult();
		}
		catch(Exception e)
		{
			logger.info("getPayrollVOForEmpId------"+e.getMessage());
		}
		return lObjPayrollVO;
	}

	public List getApprovalByDDODatesforAll(String lStrDDODode,
			String lStrPostId) {

		List listAllForms = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
		.append("SELECT EM.dcpsEmpId,EM.approvalByDDODate FROM MstEmp EM, WfJobMst wf");
		lSBQuery
		.append(" WHERE EM.ddoCode =:ddoCode AND wf.jobRefId = EM.dcpsEmpId AND EM.regStatus not in(1,2) AND wf.lstActPostId = :postId ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("postId", lStrPostId);
		lQuery.setParameter("ddoCode", lStrDDODode);
		listAllForms = lQuery.list();

		return listAllForms;
	}

	public List getAllPayScales() {

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		String query = "select payScaleId,payDescription from MstDcpsPayscale";

		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		List resultList = selectQuery.list();

		cmbVO = new ComboValuesVO();

		if (resultList != null && resultList.size() > 0) {
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("-- Select --");
			finalList.add(cmbVO);
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) it.next();
				cmbVO.setId(obj[0].toString());
				cmbVO.setDesc(obj[1].toString());
				finalList.add(cmbVO);
			}
		}

		return finalList;
	}

	public List getFormListForDDO(String lStrDDOCode) {

		List listAllForms = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
		.append(" SELECT EM.ddoCode,EM.dcpsEmpId,EM.name,EM.phyRcvdFormStatus,EM.regStatus,EM.formStatus,OM.dsgnName,DO.dcpsDdoOfficeName,EM.gender,EM.dob,EM.dcpsId,EM.dupEmpSentToMDC ");
		lSBQuery.append(" FROM MstEmp EM,OrgDesignationMst OM,DdoOffice DO ");
		lSBQuery
		.append(" WHERE EM.designation = OM.dsgnId AND DO.dcpsDdoOfficeIdPk = EM.currOff AND EM.ddoCode = :ddoCode AND EM.regStatus IN (0,-1) order by EM.name,EM.ddoCode,EM.dcpsEmpId,EM.phyRcvdFormStatus,EM.regStatus,EM.formStatus,OM.dsgnName,DO.dcpsDdoOfficeName,EM.gender,EM.dob,EM.dcpsId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDDOCode);
		listAllForms = lQuery.list();

		return listAllForms;
	}

	public List getApprovedFormsForDDO(String lStrDDOCode) {

		List listAllForms = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
		.append(" SELECT EM.dcpsEmpId,EM.name,EM.dcpsId,OM.dsgnName,DO.dcpsDdoOfficeName,EM.gender,EM.dob,EM.sevarthId ");
		lSBQuery.append(" FROM MstEmp EM,OrgDesignationMst OM,DdoOffice DO ");
		lSBQuery
		.append(" WHERE EM.designation = OM.dsgnId AND DO.dcpsDdoOfficeIdPk = EM.currOff AND EM.ddoCode = :ddoCode AND EM.regStatus in (1,2) order by EM.name,EM.dcpsEmpId,EM.dcpsId,OM.dsgnName,DO.dcpsDdoOfficeName,EM.gender,EM.dob");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDDOCode);
		listAllForms = lQuery.list();

		return listAllForms;
	}

	public List getAllApprovedEmpsUnderDDO(String lStrDDOCode,String lStrSevaarthId,String lStrName) {

		List listAllApprovedEmps = null;
		StringBuilder lSBQuery = new StringBuilder();
		Date lDtCurrDate = SessionHelper.getCurDate();

		lSBQuery.append(" SELECT EM.dcpsEmpId,EM.name,EM.sevarthId,EM.ddoAsstOrNot,EM.orgEmpMstId,OU.userName ");
		lSBQuery.append(" FROM MstEmp EM,OrgEmpMst OE,OrgUserMst OU ");
		lSBQuery.append(" WHERE EM.ddoCode = :ddoCode AND EM.regStatus in (1,2) ");
		lSBQuery.append(" AND OE.empId = EM.orgEmpMstId ");
		lSBQuery.append(" AND OE.orgUserMst.userId = OU.userId");
		lSBQuery.append(" AND EM.orgEmpMstId is not null");

		if(lStrSevaarthId != null)
		{
			if(!"".equals(lStrSevaarthId))
			{
				lSBQuery.append(" AND EM.sevarthId = :sevarthId ");
			}
		}
		if(lStrName != null)
		{
			if(!"".equals(lStrName))
			{
				lSBQuery.append(" AND EM.name = :name ");
			}
		}

		lSBQuery.append(" AND ( EM.servEndDate is null or EM.servEndDate  >= :currentDate ) ");

		lSBQuery.append(" order by EM.ddoAsstOrNot,EM.name,EM.sevarthId,EM.dcpsEmpId,EM.orgEmpMstId,OU.userName ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDDOCode);
		lQuery.setDate("currentDate", lDtCurrDate);

		if(lStrSevaarthId != null)
		{
			if(!"".equals(lStrSevaarthId))
			{
				lQuery.setParameter("sevarthId", lStrSevaarthId.trim());
			}
		}
		if(lStrName != null)
		{
			if(!"".equals(lStrName))
			{
				lQuery.setParameter("name", lStrName.trim());
			}
		}

		listAllApprovedEmps = lQuery.list();

		return listAllApprovedEmps;
	}

	public List getDesigsForAutoComplete(String searchKey) {
		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;

		sb
		.append("select dsgnId,dsgnName from OrgDesignationMst where dsgnName LIKE :searchKey ");
		selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("searchKey", searchKey + '%');

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

	public String getDistrictForDDO(String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String lStrDistrict = null;

		lSBQuery
		.append(" Select CD.districtName FROM OrgDdoMst OD,CmnLocationMst CL,CmnDistrictMst CD WHERE OD.locationCode = CL.locId and CL.locDistrictId = CD.districtId and OD.ddoCode = :lStrDdoCode");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrDdoCode", lStrDdoCode.trim());

		tempList = lQuery.list();

		if (tempList.size() != 0) {
			lStrDistrict = tempList.get(0);
		} else {
			lStrDistrict = "";
		}

		return lStrDistrict;
	}

	public void lockAccountForOrgEmpId (Long lLongOrgEmpMstId) {

		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" update org_user_mst set ACTIVATE_FLAG = 2 where USER_ID = (select USER_ID from org_emp_mst where EMP_ID = " + lLongOrgEmpMstId + ")");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
	}

	public void unlockAccountForOrgEmpId (Long lLongOrgEmpMstId) {

		StringBuilder lSBQuery = new StringBuilder();
		//lSBQuery.append(" update org_user_mst set ACTIVATE_FLAG = 1,PWDCHANGED_DATE = null where USER_ID = (select USER_ID from org_emp_mst where EMP_ID = " + lLongOrgEmpMstId + ")");
		lSBQuery.append(" update org_user_mst set ACTIVATE_FLAG = 1,PASSWORD = '0b76f0f411f6944f9d192da0fcbfb292' where USER_ID = (select USER_ID from org_emp_mst where EMP_ID = " + lLongOrgEmpMstId + ")");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
	}

	/*public void unlockAccountForOrgEmpId (Long lLongOrgEmpMstId) {

		StringBuilder lSBQuery = new StringBuilder();
		//lSBQuery.append(" update org_user_mst set ACTIVATE_FLAG = 1,PWDCHANGED_DATE = null where USER_ID = (select USER_ID from org_emp_mst where EMP_ID = " + lLongOrgEmpMstId + ")");
		lSBQuery.append(" update org_user_mst set ACTIVATE_FLAG = 1 where USER_ID = (select USER_ID from org_emp_mst where EMP_ID = " + lLongOrgEmpMstId + ")");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
	}*/

	public Object[] getUserNameAndPwdForEmpId(Long lLongOrgEmpId) {

		Object[] lObjUserNameAndPwd = new Object[2];
		StringBuilder lSBQuery = new StringBuilder();

		List lListUserNameAndPwd = new ArrayList();

		lSBQuery
		.append(" select user_name, password from org_user_mst where user_id in (select emp_id from org_emp_mst where emp_id = " +lLongOrgEmpId + ")");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListUserNameAndPwd = lQuery.list();

		if (lListUserNameAndPwd.size() != 0) {
			lObjUserNameAndPwd = (Object[]) lListUserNameAndPwd.get(0);
		}

		return lObjUserNameAndPwd;

	}

	public void updateDDOAsstStatusInMstEmp (Long lLongDcpsEmpId,String lStrRequest) {

		StringBuilder lSBQuery = new StringBuilder();
		if(lStrRequest.trim().equals("Assign"))
		{
			lSBQuery.append(" update mst_dcps_emp set DDOASST_OR_NOT = 1 where DCPS_EMP_ID = " + lLongDcpsEmpId );
		}
		else
		{
			lSBQuery.append(" update mst_dcps_emp set DDOASST_OR_NOT = null where DCPS_EMP_ID = " + lLongDcpsEmpId );
		}
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
	}

	public Long getPostForEmpId(Long lLongOrgEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long postId = 0L;

		lSBQuery.append(" SELECT post_id FROM org_post_mst where POST_ID in (SELECT post_id from ORG_USERPOST_RLT where USER_ID in (select USER_ID from org_emp_mst where EMP_ID = "+ lLongOrgEmpId  + " ) and ACTIVATE_FLAG = 1 )");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		postId = Long.valueOf(tempList.get(0).toString());
		return postId;

	}

	public Long getUserIdForEmpId(Long lLongOrgEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long userId = 0L;

		lSBQuery.append(" SELECT USER_ID FROM org_emp_mst where EMP_ID = " + lLongOrgEmpId);

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		userId = Long.valueOf(tempList.get(0).toString());
		return userId;

	}

	public Long getUserIdForPostId(Long PostId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long userId = 0L;

		lSBQuery.append(" SELECT USER_ID FROM ORG_USERPOST_RLT where POST_ID = " + PostId + " and ACTIVATE_FLAG = 1 ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		userId = Long.valueOf(tempList.get(0).toString());
		return userId;

	}

	public Boolean checkEntryInRltDDOAsstTable(Long lLongAsstPostId,Long lLongDDOPostId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" select rltDdoAsstId FROM RltDdoAsst WHERE asstPostId = :asstPostId and ddoPostId = :ddoPostId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("asstPostId", lLongAsstPostId);
		lQuery.setParameter("ddoPostId", lLongDDOPostId);

		tempList = lQuery.list();
		if (tempList.size() == 0) {
			flag = false;
		}
		return flag;

	}

	public Boolean checkEntryInAclPostRoleTable(Long lLongAsstPostId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" SELECT POST_ROLE_ID FROM ACL_POSTROLE_RLT WHERE POST_ID = " + lLongAsstPostId + " and ROLE_ID = 700001 " );
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		if (tempList.size() == 0) {
			flag = false;
		}
		return flag;

	}

	public AclRoleMst getRoleVOForRoleId(Long roleId) {

		StringBuilder lSBQuery = new StringBuilder();
		AclRoleMst lObjAclRoleMst = null;
		Query lQuery = null;
		List tempList = null;

		lSBQuery.append(" from AclRoleMst");
		lSBQuery.append(" WHERE roleId = :roleId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("roleId", roleId);

		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjAclRoleMst = (AclRoleMst) tempList.get(0);
		}

		return lObjAclRoleMst;
	}

	public OrgPostMst getPostVOForPostId(Long postId) {

		StringBuilder lSBQuery = new StringBuilder();
		OrgPostMst lObjOrgPostMst = null;
		Query lQuery = null;
		List tempList = null;

		lSBQuery.append(" from OrgPostMst");
		lSBQuery.append(" WHERE postId = :postId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("postId", postId);

		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjOrgPostMst = (OrgPostMst) tempList.get(0);
		}

		return lObjOrgPostMst;
	}


	public OrgUserMst getUserVOForUserId(Long userId) {

		StringBuilder lSBQuery = new StringBuilder();
		OrgUserMst lObjOrgUserMst = null;
		Query lQuery = null;
		List tempList = null;

		lSBQuery.append(" from OrgUserMst");
		lSBQuery.append(" WHERE userId = :userId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("userId", userId);

		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjOrgUserMst = (OrgUserMst) tempList.get(0);
		}

		return lObjOrgUserMst;
	}

	public Boolean checkEntryInWFOrgPostMpgMst(Long lLongPostId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" SELECT POST_ID FROM WF_ORG_POST_MPG_MST WHERE POST_ID = " + lLongPostId);
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		if (tempList.size() == 0) {
			flag = false;
		}
		return flag;

	}

	public Boolean checkEntryInWFOrgUserMpgMst(Long lLongUserId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" SELECT USER_ID FROM WF_ORG_USR_MPG_MST WHERE USER_ID = " + lLongUserId);
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		if (tempList.size() == 0) {
			flag = false;
		}
		return flag;

	}

	public void insertWFOrgPostMpg(Long lLongPostId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("INSERT INTO WF_ORG_POST_MPG_MST VALUES \n");
			lSBQuery.append("(:postId,:dbId,:projectId) \n"); 

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("postId",lLongPostId );
			lQuery.setParameter("dbId", 99);
			lQuery.setParameter("projectId", 101);

			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void insertWFOrgUsrMpg(Long lLongUserId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("INSERT INTO WF_ORG_USR_MPG_MST VALUES \n");
			lSBQuery.append("(:userId,:dbId,:projectId) \n"); 

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("userId",lLongUserId);
			lQuery.setParameter("dbId", 99);
			lQuery.setParameter("projectId", 101);

			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void insertAclPostRoleRlt(Long lLongAclPostRoleId,Long lLongRoleIdOfDDOAsst,Long lLongPostId,Long lLongDDOPostId,Date gDtCurDate) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append("INSERT INTO ACL_POSTROLE_RLT VALUES \n");
			lSBQuery.append("(:postRoleId,:postId,:roleId,:startDate,:endDate,:activeFlag,:createdBy,:createdDate,:createdByPost,:updatedBy,:updatedDate,:updatedByPost) \n"); 

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("postRoleId",lLongAclPostRoleId );
			lQuery.setParameter("postId", lLongPostId);
			lQuery.setParameter("roleId", lLongRoleIdOfDDOAsst);
			lQuery.setParameter("startDate", gDtCurDate);
			lQuery.setParameter("endDate", null );
			lQuery.setParameter("activeFlag", 1);
			lQuery.setParameter("createdBy", lLongDDOPostId);
			lQuery.setParameter("createdDate", gDtCurDate);
			lQuery.setParameter("createdByPost", lLongDDOPostId );
			lQuery.setParameter("updatedBy", null );
			lQuery.setParameter("updatedDate", null);
			lQuery.setParameter("updatedByPost", null );
			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void insertWfHierachyPostMpg(Long lLongHierarchySeqId ,Long lLongHierarchyRefId ,Long lLongPostId,Long lLongCreatedByUserId,Date gDtCurDate,Long LocId ) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("INSERT INTO WF_HIERACHY_POST_MPG VALUES \n");
			lSBQuery.append("(:hierachySeqId,:parentHierachy,:postId,:levelId,:hierachyRefId,:crtUser,:createdDate,:lstUpdUser,:lstUpdDate,:startDate,:endDate,:activeFlag,:locId,:langId,:dueDays) \n"); 

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("hierachySeqId", lLongHierarchySeqId);
			lQuery.setParameter("parentHierachy", null );
			lQuery.setParameter("postId", lLongPostId);
			lQuery.setParameter("levelId", 10);
			lQuery.setParameter("hierachyRefId", lLongHierarchyRefId);
			lQuery.setParameter("crtUser",lLongCreatedByUserId );
			lQuery.setParameter("createdDate",gDtCurDate );
			lQuery.setParameter("lstUpdUser",null );
			lQuery.setParameter("lstUpdDate",null );
			lQuery.setParameter("startDate",gDtCurDate );
			lQuery.setParameter("endDate",null );
			lQuery.setParameter("activeFlag", 1);
			lQuery.setParameter("locId", LocId  );
			lQuery.setParameter("langId",1 );
			lQuery.setParameter("dueDays",null );

			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public List<Long> getAllHierarchyRefIdsForLocation(Long LocationCode) {

		List<Long> listHierarchyRefIds = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT DISTINCT WFP.HIERACHY_REF_ID FROM WF_HIERACHY_POST_MPG WFP");
		lSBQuery.append(" JOIN WF_HIERARCHY_REFERENCE_MST WFR ON WFP.HIERACHY_REF_ID = WFR.HIERACHY_REF_ID ");
		lSBQuery.append(" JOIN WF_DOC_MST WFD ON WFR.DOC_ID = WFD.DOC_ID");
		lSBQuery.append(" WHERE WFD.DOC_ID in (700001,700002,700005,700006) and ");
		lSBQuery.append(" WFP.LOC_ID = " + LocationCode );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		listHierarchyRefIds = lQuery.list();

		return listHierarchyRefIds;

	}

	public Boolean checkEntryInWfHierachyPostMpg(Long lLongHierarchyRefId,Long lLongPostId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" SELECT * FROM WF_HIERACHY_POST_MPG WFP where WFP.HIERACHY_REF_ID = " + lLongHierarchyRefId + " and WFP.POST_ID = '"+ lLongPostId  +"' and WFP.LEVEL_ID = 10 ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		if (tempList.size() == 0) {
			flag = false;
		}
		return flag;

	}

	public Boolean checkIfNameExists(String lStrName) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = false;

		lSBQuery.append(" select dcpsEmpId FROM MstEmp WHERE upper(name) = :name");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("name", lStrName.trim().toUpperCase());

		tempList = lQuery.list();
		if(tempList != null)
		{
			if (tempList.size() != 0) {
				flag = true;
			}
		}
		return flag;
	}

	public Boolean checkIfNameAndDOBExists(String lStrName, Date lDateDOB)  {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = false;

		lSBQuery.append(" select dcpsEmpId FROM MstEmp WHERE upper(name) = :name and dob = :dob");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("name", lStrName.trim().toUpperCase());
		lQuery.setParameter("dob", lDateDOB);

		tempList = lQuery.list();
		if(tempList != null)
		{
			if (tempList.size() != 0) {
				flag = true;
			}
		}
		return flag;
	}

	public void deleteRltDdoAsstEntryWhileDeAssign(Long lLongAsstPostId,Long lLongDDOPostId) {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" delete from RltDdoAsst where asstPostId = :asstPostId and ddoPostId = :ddoPostId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("asstPostId", lLongAsstPostId);
		lQuery.setParameter("ddoPostId", lLongDDOPostId);
		lQuery.executeUpdate();

	}

	public Boolean checkIfDuplicateEmpForGivenCriteria(String lStrName, Date lDateDOB, Character lCharGender,Character lCharFatherOrHusb)  {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = false;

		lSBQuery.append(" select dcpsEmpId FROM MstEmp WHERE  ");
		lSBQuery.append(" upper(name) ='"+lStrName+"' and ");
		lSBQuery.append(" dob = '"+lDateDOB+"' and ");
		lSBQuery.append(" gender = '"+lCharGender+"'");

		if(lCharFatherOrHusb != null)
		{
			lSBQuery.append(" and substr(father_or_husband,1,1) = '"+lCharFatherOrHusb+"' ");
		}

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		/*lQuery.setParameter("name", lStrName.trim().toUpperCase());
		lQuery.setParameter("dob", lDateDOB);
		lQuery.setParameter("gender",lCharGender);

		if(lCharFatherOrHusb != null)
		{
			lQuery.setParameter("FatherOrHusbFirstChar",lCharFatherOrHusb.toString());
		}*/
		
		logger.info("query si ************"+lQuery.toString());
		tempList = lQuery.list();
		if(tempList != null)
		{
			if (tempList.size() != 0) {

				if(tempList.size() >= 2)
				{
					flag = true;
				}
			}
		}
		return flag;
	}

	public List getAllDupEmpsForMDC() {

		List lListDupEmps = null; 
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT EM.ddoCode,EM.dcpsEmpId,EM.name,EM.phyRcvdFormStatus,EM.regStatus,EM.formStatus,OM.dsgnName,DO.dcpsDdoOfficeName,EM.gender,EM.dob,EM.dcpsId,EM.createdDate  ");
		lSBQuery.append(" FROM MstEmp EM,OrgDesignationMst OM,DdoOffice DO ");
		lSBQuery.append(" WHERE EM.designation = OM.dsgnId AND DO.dcpsDdoOfficeIdPk = EM.currOff ");
		lSBQuery.append(" AND EM.dupEmpSentToMDC = 2 ");
		lSBQuery.append(" order by EM.name,EM.ddoCode,EM.dcpsEmpId,EM.phyRcvdFormStatus,EM.regStatus,EM.formStatus,OM.dsgnName,DO.dcpsDdoOfficeName,EM.gender,EM.dob,EM.dcpsId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lListDupEmps = lQuery.list();

		return lListDupEmps;
	}

	//added by shailesh
	public List getEmployeesForDesg(long desgnCode, long locId) {

		List<Object> lLstReturnList = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
			.append("SELECT rlt.POST_ID, concat(concat(concat(emp.EMP_FNAME,' '),CONCAT(emp.EMP_MNAME,' ')),emp.EMP_LNAME)");
			lSBQuery
			.append(" FROM org_emp_mst emp inner join ORG_USERPOST_RLT UP on emp.USER_ID = UP.USER_ID ");
			lSBQuery.append(" INNER JOIN  ORG_POST_DETAILS_RLT RLT ON UP.POST_ID= RLT.POST_ID inner join ");
			lSBQuery.append(" org_post_mst postmst on postmst.post_id=rlt.post_id and ");
			lSBQuery.append("  postmst.START_DATE<=sysdate and (postmst.END_DATE>=sysdate or postmst.END_DATE is null) "); 
			lSBQuery.append(" and postmst.POST_TYPE_LOOKUP_ID in (10001198130, 10001198129) AND RLT.LOC_id = ");
			lSBQuery.append(locId);
			lSBQuery.append(" AND RLT.DSGN_ID = ");
			lSBQuery.append(desgnCode);
			lSBQuery.append(" and up.START_DATE <= sysdate "); 
			lSBQuery.append(" and (up.END_DATE >= sysdate or up.END_DATE is null) and up.ACTIVATE_FLAG=1  and  emp.EMP_SRVC_EXP > sysdate");
			lSBQuery.append(" order by emp.EMP_FNAME");
			//lSBQuery.append(" and rlt.post_id not in (SELECT EMP_POST_ID FROM HR_PAY_SUBSTITUTE_EMP_MPG where ACTIVATE_FLAG = 0)");
			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createSQLQuery(lSBQuery.toString());

			//lObjQuery.setParameter("fieldDeptId", desgnCode);
			logger.info("lSBQuery "+lSBQuery.toString());
			logger.info("lObjQuery size "+lObjQuery.list().toString());
			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
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
			//e.printStackTrace();
		}
		return lLstReturnList;

	}

	//added by shailesh
	public List getSubstituteList(){
		List substituteList = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT LOOKUP_ID,LOOKUP_NAME FROM CMN_LOOKUP_MST where PARENT_LOOKUP_ID = 10001198152");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		substituteList = lQuery.list();

		return substituteList;
	}
	public List getPostDetails(long postId){
		List empPostDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("FROM OrgPostMst mst where mst.postId = "+postId);
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		empPostDtls = lQuery.list();

		return empPostDtls;
	}
	public List getPostPsrDetails(long postId){
		List postPsrLst = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("FROM HrPayPsrPostMpg psr where psr.postId = "+postId);
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		postPsrLst = lQuery.list();

		return postPsrLst;
	}
	public void insertNewPostDetails(HrPaySubstituteEmpMpg objHrPaySubEmpMpg){
		StringBuilder lSBQuery = new StringBuilder();


		SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy hh:mm");


		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		String strStartDate = sdf2.format(objHrPaySubEmpMpg.getStartDate());
		
		
		String strEndDate = null; 
		if(objHrPaySubEmpMpg.getEndDate() != null)
		strEndDate = sdf2.format(objHrPaySubEmpMpg.getEndDate());

		//sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
		//sdf.applyPattern("yyyy-MMM-dd HH:mm:ss");
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		lSBQuery = lSBQuery.append("insert into ifms.HR_PAY_SUBSTITUTE_EMP_MPG values (");
		lSBQuery = lSBQuery.append(objHrPaySubEmpMpg.getSubEmpMapId());

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getSubLookupId());
		lSBQuery = lSBQuery.append(", "+objHrPaySubEmpMpg.getSubLookupId());

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getSubPostId());		
		lSBQuery = lSBQuery.append(", "+objHrPaySubEmpMpg.getSubPostId());

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getEmpPostId());
		lSBQuery = lSBQuery.append(", "+objHrPaySubEmpMpg.getEmpPostId());

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getSubPostId());
		lSBQuery = lSBQuery.append(",sysdate");

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getCreatedDate().toGMTString());
		logger.info("lSBQuery "+sdf.format(objHrPaySubEmpMpg.getCreatedDate()));
		lSBQuery = lSBQuery.append(",sysdate");

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getActivateFlag());
		lSBQuery = lSBQuery.append(","+objHrPaySubEmpMpg.getActivateFlag());

		logger.info("lSBQuery "+sdf.format(objHrPaySubEmpMpg.getStartDate()));
		lSBQuery = lSBQuery.append(",'"+strStartDate+"',");

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getSubPostId());
		//lSBQuery = lSBQuery.append("','"+sdf.format(objHrPaySubEmpMpg.getEndDate()));
		if(strEndDate!= null)
		lSBQuery = lSBQuery.append("'"+strEndDate+"', ");
		else
			lSBQuery = lSBQuery.append(strEndDate+", ");

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getCreatedByPost());
		lSBQuery = lSBQuery.append(objHrPaySubEmpMpg.getCreatedByPost());

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getcreatedByUser());
		lSBQuery = lSBQuery.append(", "+objHrPaySubEmpMpg.getcreatedByUser()+")");

		logger.info("lSBQuery "+objHrPaySubEmpMpg.getSubPostId());
		logger.info("lSBQuery "+lSBQuery);

		int result = ghibSession.createSQLQuery(lSBQuery.toString()).executeUpdate();  //save(objHrPaySubEmpMpg);
		logger.info("result "+result);
		//postPsrLst = lQuery.list();

		//return postPsrLst;
	}

	public boolean checkSubstitute(String empId){
		String msg = null;
		long actvFlag = 0;
		StringBuffer query = new StringBuffer();
		query.append("SELECT sub.ACTIVATE_FLAG FROM HR_PAY_SUBSTITUTE_EMP_MPG sub inner join ORG_USERPOST_RLT usr on");
		query.append(" sub.SUBSITUTE_POST_ID=usr.POST_ID inner join ORG_EMP_MST emp on usr.USER_ID =emp.USER_ID ");
		query.append(" inner join mst_dcps_emp dcps on emp.EMP_ID = dcps.ORG_EMP_MST_ID ");
		query.append("  and  usr.ACTIVATE_FLAG = 1 and dcps.DCPS_EMP_ID = ");
		query.append(empId);
		logger.info("quey "+query);
		Query query2 = ghibSession.createSQLQuery(query.toString());
		logger.info("quey "+query2);
		int lst = query2.list().size();
		if(lst > 0){
			logger.info("in if 0");
			if(query2 != null && !query2.toString().equals("")){
				actvFlag = Long.parseLong(query2.uniqueResult().toString());
			}
		}
		logger.info("in out "+actvFlag);
		if(actvFlag == 1)
			return true;
		else return false;
	}
	public boolean checkSubstituteForRjctn(long postId){
		long actvFlag = 0;
		StringBuffer query = new StringBuffer();
		query.append("SELECT ACTIVATE_FLAG FROM ifms.HR_PAY_SUBSTITUTE_EMP_MPG where SUBSITUTE_POST_ID = ");
		query.append(postId);		
		logger.info("quey "+query);
		Query query2 = ghibSession.createSQLQuery(query.toString());
		if(query2 != null && !query2.equals("")){
			logger.info("in if ");
			if(query2.list().size() > 0)
			actvFlag = (Integer)query2.list().get(0);//Long.parseLong(query2.uniqueResult().toString());		
		}
		logger.info("in out "+actvFlag);
		if(actvFlag == 1)
			return true;
		else return false;
	}
	
	public void updateHrPaySubsEmpMpg(long postId){
		StringBuffer query = new StringBuffer();
		query.append("update HR_PAY_SUBSTITUTE_EMP_MPG set ACTIVATE_FLAG = 0 where SUBSITUTE_POST_ID = ");
		query.append(postId);
		int res = ghibSession.createSQLQuery(query.toString()).executeUpdate();
		logger.info("res of updation is "+res);
	}

	public void updateParentPostId(long postId){
		StringBuffer query = new StringBuffer();
		query.append("update org_post_mst set PARENT_POST_ID = null where post_id = ");
		query.append(postId);
		int res = ghibSession.createSQLQuery(query.toString()).executeUpdate();
		logger.info("res of updation is "+res);
	}

	public long getNextSeqNum() {
		long seqId=0;
		StringBuffer sb= new StringBuffer();
		sb.append("select GENERATED_ID from CMN_TABLE_SEQ_MST where upper(table_name)='HR_PAY_SUBSTITUTE_EMP_MPG'");
		Query query = ghibSession.createSQLQuery(sb.toString());
		seqId=Long.parseLong(query.uniqueResult().toString());		
		logger.info("seqId............"+seqId);
		long seqNo= seqId+1;
		logger.info("seqNo............"+seqId);
		StringBuffer sb2= new StringBuffer();
		sb2.append("update CMN_TABLE_SEQ_MST set GENERATED_ID="+seqNo+" where upper(TABLE_NAME)= 'HR_PAY_SUBSTITUTE_EMP_MPG'");
		Query query2 = ghibSession.createSQLQuery(sb2.toString());
		query2.executeUpdate();
		return seqId;
	}

	public String getPostName(long postId){
		StringBuffer sb= new StringBuffer();
		List post = null;
		String postName = null;
		OrgPostDetailsRlt orgPostDetailsRlt = null;
		sb.append("SELECT POST_NAME from Org_Post_Details_Rlt where post_Id = "+postId);
		logger.info("queryyy "+sb);
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			post = query.list();
			if(post != null)
			{
				//Object obj[] = (Object[])post.get(0);
				postName = post.get(0).toString();
				logger.info("postName "+postName);
			}
		}
		return postName;
	}
	
	public long getPostIdFrmEmpID(long EmpId){
		StringBuffer sb= new StringBuffer();
		List post = null;
		long postid = 0;
		OrgPostDetailsRlt orgPostDetailsRlt = null;
		sb.append(" select post_id FROM ORG_USERPOST_RLT up inner join ORG_EMP_MST emp on up.USER_ID = emp.USER_ID ");
		sb.append(" inner join MST_DCPS_EMP dcps on emp.EMP_ID = dcps.ORG_EMP_MST_ID ");
		sb.append(" where up.ACTIVATE_FLAG = 1 and dcps.DCPS_EMP_ID = "+EmpId);
		logger.info("queryyy "+sb);
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			post = query.list();
			if(post != null)
			{
				//Object obj[] = (Object[])post.get(0);
				postid = Long.parseLong(post.get(0).toString());
				logger.info("postName "+postid);
			}
		}
		return postid;
	}
	
	public long getVacantPostLstSize(long dsngCode, long locId){
		//List userpostrlt = null;
		long size = 0;
		StringBuffer sb= new StringBuffer();
				
		sb.append("  select pd.post_id,  pd.post_name ");
		sb.append(" from org_post_details_rlt pd , org_post_mst mst  where mst.POST_ID = pd.POST_ID and mst.ACTIVATE_FLAG = 1  and pd.dsgn_id = " + dsngCode + " and   ");	   
		sb.append(" pd.loc_id =  "+ locId + " and pd.lang_id = 1 and ");
	    sb.append(" pd.post_id not in (select prlt.post_id from org_userpost_rlt prlt,org_post_details_rlt pd1 where pd1.post_id = prlt.post_Id and (prlt.end_date is  null or prlt.activate_flag = 1) and pd1.loc_id="+locId+")  ");
	    sb.append(" and mst.POST_TYPE_LOOKUP_ID  in  (10001198130,10001198129) ");
		sb.append(" and (mst.END_DATE > sysdate or mst.END_DATE is null) ");
	    sb.append("  order by pd.post_name  ");
	    
	    logger.info( "execyting Query......."+sb.toString());
	    
	    Query query = ghibSession.createSQLQuery(sb.toString());
	    if(query != null)
	    	size = query.list().size();
	    logger.info("List size is:-"+size);
		return size;
	}
	
	
	public long getPostIdFrmRltDcpsPayEmp(long empID){
		StringBuilder lSBQuery = new StringBuilder();
		long postId = 0;
		
		logger.info("in getPostIdFrmRltDcppayEmp ");
		
		lSBQuery.append("  SELECT POST_ID FROM RLT_DCPS_PAYROLL_EMP where DCPS_EMP_ID =  ");
		lSBQuery.append(empID);
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		if(lQuery != null ){
			if(lQuery.list().size() > 0)
			postId = Long.parseLong(lQuery.list().get(0).toString());
			logger.info("postId "+postId);
		}
		
		return postId;
	}
	
	public List getReligionData(){
		StringBuilder lSBQuery = new StringBuilder();
		List lstReligion = null;
		
		logger.info("in getReligionData ");
		
		lSBQuery.append("  SELECT RELIGION_ID,RELIGION_NAME FROM CMN_RELIGION_MST");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		if(lQuery != null){
			lstReligion = lQuery.list();
			logger.info("postId "+lstReligion.size());
		}
		
		return lstReligion;
	}
	
	//end
	// start by amit bhattad
	public List getListOfDupEmp( String lStrName, String lStrDOB, String lStrGender)
	{
		
		StringBuilder lSBQuery = new StringBuilder();
		List DupEMPList = new ArrayList();	
		
		lSBQuery.append("  SELECT DCPS_ID,DDO_CODE,EMP_NAME,gender,SEVARTH_ID,DOB,FATHER_OR_HUSBAND,DCPS_EMP_ID  ");
		lSBQuery.append(" FROM MST_DCPS_EMP ");
		lSBQuery.append(" where EMP_NAME=:name and  DOB =:dob and GENDER =:gender ");
						
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("name",lStrName);
		lQuery.setParameter("dob", lStrDOB);
		lQuery.setParameter("gender", lStrGender);
		DupEMPList = lQuery.list();
		return DupEMPList;
		
	}
	
	// end
	
	public int checkIfUID(String uid)
	{
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tpList = new ArrayList();
		int count = 0;

		lSBQuery.append(" SELECT DCPS_EMP_ID FROM MST_DCPS_EMP where UID_NO ='"+uid+"'");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//	lQuery.setParameter("uidNO", uid);
		logger.info("lSBQuery "+lSBQuery+ " "+uid);
		if(lQuery != null){
			count = lQuery.list().size();			
		}		
		return count;		
	}
	
	public int checkIfEID( String eid)
	{		
		StringBuilder lsQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		int count = 0;
		lsQuery.append(" select DCPS_EMP_ID from Mst_DCPS_EMP where EID_NO ='"+eid+"'");
		
		Query lquery = ghibSession.createSQLQuery(lsQuery.toString());		
	//	lquery.setParameter("eidNo",eid);
		logger.info("lSBQuery "+lsQuery+ " "+eid);
		if(lquery != null)
		{
			count = lquery.list().size();
			
		}
		return count;		
	}
	
	//added by samadhan for pf account no check START
	public Long checkPFAccountNumber(String pfSeries, String pfAccNo, String empId) {
		Session hibSession = getSession();
		
		Long finalCheckFlag=null;
		StringBuffer sb= new StringBuffer();
		sb.append("SELECT count(1) FROM RLT_DCPS_PAYROLL_EMP where PF_SERIES_desc='"+pfSeries+"' and PF_ACNO='"+pfAccNo+"'");
		if(empId !=null && !empId.trim().equals("") && Long.parseLong(empId)!=0){
		sb.append(" and dcps_emp_id !="+empId+" ");
		}
		Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
		logger.info("lSBQuery "+sqlQuery1);
		finalCheckFlag=Long.parseLong(sqlQuery1.uniqueResult().toString());
		
		return finalCheckFlag;
	}
	
	public Long checkPFAccountNumberForGPFDetails(String pfSeries, String pfAccNo) {
		Session hibSession = getSession();
		
		Long finalCheckFlag=null;
		StringBuffer sb= new StringBuffer();	
		sb.append("SELECT count(1) FROM RLT_DCPS_PAYROLL_EMP where PF_SERIES_desc='"+pfSeries+"' and PF_ACNO='"+pfAccNo+"'");
		Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
		logger.info("lSBQuery "+sqlQuery1);
		finalCheckFlag=Long.parseLong(sqlQuery1.uniqueResult().toString());
		logger.info("finalCheckFlag "+finalCheckFlag);
		Long finalCheckFlag2=null;
		StringBuffer sb1= new StringBuffer();	
		sb1.append("SELECT count(1) FROM HR_PAY_gpf_birth_updation bu inner join CMN_LOOKUP_MST cmn on bu.NEW_GPF_SERIES= cmn.LOOKUP_ID  where cmn.LOOKUP_DESC ='"+pfSeries+"' and bu.NEW_ACCOUNT_NO ='"+pfAccNo+"'");
		Query sqlQuery2 = hibSession.createSQLQuery(sb1.toString());
		logger.info("lSBQuery2 "+sqlQuery2);
		finalCheckFlag2=Long.parseLong(sqlQuery2.uniqueResult().toString());
		logger.info("finalCheckFlag2 "+finalCheckFlag2);
		return (finalCheckFlag+finalCheckFlag2);
	}
	//added by samadhan for pf account no check END
	
	
	@Override
	public String checkPfDetails(String pfSeries, String pfAccNo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// end 
	
	
//added by sunitha for post display:start
	
	public String getPostType(Long postId){
		StringBuffer sb= new StringBuffer();
		List post = null;
		String postType = null;
		OrgPostDetailsRlt orgPostDetailsRlt = null;
		sb.append("SELECT POST_TYPE_LOOKUP_ID from Org_Post_Mst where post_Id = "+postId);
		logger.info("queryyy "+sb);
		Query query = ghibSession.createSQLQuery(sb.toString());
		if(query != null){
			post = query.list();
			if(post != null)
			{
				//Object obj[] = (Object[])post.get(0);
				postType = post.get(0).toString();
				logger.info("postType "+postType);
			}
		}
		return postType;
	}
	
	//added by sunitha for post display:end
	
	//added by arpan
	public List getHrPaySubstituteVO(Long postId)
	{
		List hrPaySubstituteEmpMpgList = null;
		StringBuffer lQuery = new StringBuffer();
		lQuery.append("SELECT SUBSTITUTE_LOOKUP_ID, EMP_POST_ID, START_DATE, END_DATE FROM ifms.HR_PAY_SUBSTITUTE_EMP_MPG where SUBSITUTE_POST_ID = "+postId);
		logger.info("getHrPaySubstituteVO"+lQuery.toString());
		Query query = ghibSession.createSQLQuery(lQuery.toString());
		hrPaySubstituteEmpMpgList = query.list();
		return hrPaySubstituteEmpMpgList;
	}
	
	public String getSubstituteEmpName(long subPostId)
	{
		String empName = null;
		StringBuffer lQuery = new StringBuffer();
		lQuery.append("select EMP_NAME from MST_DCPS_EMP where ORG_EMP_MST_ID in (select emp_id from ORG_EMP_MST where USER_ID in (select USER_ID from ORG_USERPOST_RLT where POST_ID = "+subPostId+"))");
		logger.info("getHrPaySubstituteVO"+lQuery.toString());
		Query query = ghibSession.createSQLQuery(lQuery.toString());
		empName = query.list().get(0).toString();
		return empName;
	}
	
	public Long getPostId(long lLngDcpsPayrollId)
	{
		StringBuffer lQuery = new StringBuffer();
		Long postId = null;
		lQuery.append("SELECT POST_ID FROM RLT_DCPS_PAYROLL_EMP where DCPS_PAYROLL_EMP_ID = "+lLngDcpsPayrollId);
		Query query = ghibSession.createSQLQuery(lQuery.toString());
		logger.info("getPostId------"+query);
		List list = query.list();
		if(list!=null && list.size()>0 && list.get(0)!=null)
		postId = Long.parseLong(list.get(0).toString());
		return postId;
		
	}
	//end
}

