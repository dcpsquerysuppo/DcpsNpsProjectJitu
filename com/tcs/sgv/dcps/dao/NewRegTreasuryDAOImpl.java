/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 11, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpDetails;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.MstEmpNmnDetails;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmpDetails;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 11, 2011
 */
public class NewRegTreasuryDAOImpl extends GenericDaoHibernateImpl implements
		NewRegTreasuryDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	SessionFactory sessionFactory = null;

	public NewRegTreasuryDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/**
	 * Method to get all the details of DDO
	 * 
	 * @param Map
	 *            : inputMap
	 * 
	 * @return List
	 */

	public List getAllDDOListForPhyFormRcvd(String lStrPostId,
			String lStrUserType) {

		List listAllForms = null;
		List<ComboValuesVO> listAllFormsCombo = new ArrayList<ComboValuesVO>();
		StringBuilder lSBQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList = null;
		Iterator itr;
		Object[] obj;
		try {
			if (lStrUserType.equals("TO")) {
				lSBQuery
						.append("select distinct DDO.ddoCode, DDO.ddoName from OrgDdoMst DDO, MstEmp emp,WfJobMst wf");
				lSBQuery
						.append(" WHERE DDO.ddoCode = emp.ddoCode AND wf.jobRefId = emp.dcpsEmpId AND wf.lstActPostId = :postId ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("postId", lStrPostId);
				lLstResultList = lQuery.list();
				cmbVO = new ComboValuesVO();
				if (lLstResultList != null && lLstResultList.size() > 0) {
					itr = lLstResultList.iterator();
					while (itr.hasNext()) {

						obj = (Object[]) itr.next();
						cmbVO.setId(obj[0].toString());
						cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
						listAllFormsCombo.add(cmbVO);
					}
				}
			} else {
				lSBQuery.append("select distinct DDO.ddoCode, DDO.ddoName,DM.dsgnName from OrgDdoMst DDO, MstEmp emp,WfJobMst wf,OrgDesignationMst DM");
				lSBQuery.append(" WHERE DDO.ddoCode = emp.ddoCode AND wf.jobRefId = emp.dcpsEmpId AND wf.lstActPostId = :postId ");
				lSBQuery.append(" AND DM.dsgnId = DDO.designCode");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("postId", lStrPostId);
				listAllForms = lQuery.list();
			}

		} catch (Exception e) {
			gLogger
					.error("Exception occured from getAllDDOListForPhyFormRcvd of TreasuryDAOImpl is :: "
							+ e);
			//e.printStackTrace();
		}
		if (lStrUserType.equals("TO")) {
			return listAllFormsCombo;
		} else {
			return listAllForms;
		}

	}

	/**
	 * Method to get all the details of Employee according to the DDO Code
	 * 
	 * @param Map
	 *            : inputMap, Long lLngDDODode
	 * 
	 * @return List
	 */

	public List getAllFormsForDDO(String lStrDDODode, String lStrPostId) {

		List listAllForms = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
				.append("SELECT EM.dcpsEmpId,EM.ddoCode,EM.name,EM.gender,EM.dob,");
		lSBQuery
				.append("OM.dsgnName,EM.approvalByDDODate,DM.ddoName,EM.sevarthId FROM MstEmp EM, WfJobMst wf,OrgDesignationMst OM,OrgDdoMst DM ");
		lSBQuery
				.append(" WHERE EM.ddoCode =:ddoCode AND  wf.jobRefId = EM.dcpsEmpId AND wf.lstActPostId = :postId AND EM.designation = OM.dsgnId AND wf.wfDocMst.docId = :docId AND DM.ddoCode = EM.ddoCode ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDDODode);
		lQuery.setParameter("postId", lStrPostId);
		lQuery.setParameter("docId", 700001L);
		listAllForms = lQuery.list();

		return listAllForms;
	}

	public List getApprovalByDDODatesforAll(String lStrPostId) {

		List listAllForms = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
				.append("SELECT EM.dcpsEmpId,EM.approvalByDDODate FROM MstEmp EM, WfJobMst wf");
		lSBQuery
				.append(" WHERE wf.jobRefId = EM.dcpsEmpId and EM.regStatus not in(1,2) AND wf.lstActPostId = :postId ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("postId", lStrPostId);
		listAllForms = lQuery.list();

		return listAllForms;
	}

	public List getAllSavedRequests(Integer lIntCriteria, String lStrPostId) {

		List listSavedRequests = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		// For drafts
		if (lIntCriteria == 1) {

			lSBQuery
					.append(" SELECT EM.dcpsEmpId, EM.fname, EM.mname ,EM.lname, EM.dob, EM.gender, EM.doj ");
			lSBQuery.append(" FROM MstEmp EM");
			lSBQuery.append(" WHERE EM.formStatus = :formStatus ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("formStatus", 0L);
		}

		// For completed forms of DDO Asst and Verification form of DDO
		if (lIntCriteria == 2 || lIntCriteria == 3) {

			lSBQuery
					.append(" SELECT EM.dcpsEmpId, EM.fname, EM.mname ,EM.lname, EM.gender, EM.dob, EM.currOff,EM.designation ");
			lSBQuery.append(" FROM MstEmp EM, WfJobMst WJ");
			lSBQuery
					.append(" WHERE WJ.jobRefId = EM.dcpsEmpId AND WJ.lstActPostId = :postId AND EM.regStatus = :regStatus ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("regStatus", 0L);
			lQuery.setParameter("postId", lStrPostId);
		}

		// For treasury
		if (lIntCriteria == 4) {

			lSBQuery.append(" SELECT EM.dcpsEmpId, EM.name,EM.dcpsId, EM.gender, EM.dob, DO.dcpsDdoOfficeName,OM.dsgnName,EM.doj,EM.payCommission,EM.ddoCode,DM.ddoName,EM.approvalByDDODate,EM.phyRcvdDate ");
			lSBQuery.append(" from MstEmp EM,WfJobMst wf,OrgDesignationMst OM,DdoOffice DO,OrgDdoMst DM");
			lSBQuery.append(" WHERE wf.jobRefId = EM.dcpsEmpId AND EM.regStatus = :regStatus AND wf.lstActPostId = :postId AND DO.dcpsDdoOfficeIdPk = EM.currOff AND EM.designation = OM.dsgnId AND DM.ddoCode = EM.ddoCode and EM.phyRcvdDate is not null and wf.wfDocMst.docId = :docId order by EM.name, EM.dcpsEmpId, EM.dcpsId, EM.gender, EM.dob, DO.dcpsDdoOfficeName,OM.dsgnName,EM.doj,EM.payCommission,EM.ddoCode,DM.ddoName,EM.approvalByDDODate,EM.phyRcvdDate");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("regStatus", 0L);
			lQuery.setParameter("postId", lStrPostId);
			lQuery.setParameter("docId", 700001L);
		}

		// For DDO : Approved Cases
		if (lIntCriteria == 5) {

			Long lLongDDOCode = 12345L;
			lSBQuery
					.append(" SELECT EM.dcpsEmpId, EM.fname, EM.mname ,EM.lname, EM.dcpsId, EM.gender, EM.dob, EM.currOff,EM.designation");
			lSBQuery.append(" FROM MstEmp EM");
			lSBQuery
					.append(" WHERE EM.regStatus = :regStatus AND EM.ddoCode = :ddoCode ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("regStatus", 1L);
			lQuery.setParameter("ddoCode", lLongDDOCode);
		}

		// For DDO ASST & DDO: Rejected Cases
		if (lIntCriteria == 6 || lIntCriteria == 7) {

			lSBQuery
					.append(" SELECT EM.dcpsEmpId, EM.fname, EM.mname ,EM.lname, EM.gender, EM.dob, EM.currOff,EM.designation ");
			lSBQuery.append(" FROM MstEmp EM, WfJobMst WJ");
			lSBQuery
					.append(" WHERE WJ.jobRefId = EM.dcpsEmpId AND WJ.lstActPostId = :postId AND EM.regStatus = :regStatus ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("regStatus", -1L);
			lQuery.setParameter("postId", lStrPostId);

		}

		listSavedRequests = lQuery.list();

		return listSavedRequests;

	}

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

	public String getDDOAsstPostIdForEmpId(String dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String postIdDDOAsst;

		lSBQuery
				.append(" SELECT init_unit FROM wf_job_mst WHERE job_ref_id = '"
						+ dcpsEmpId + "' and doc_id = '700001'");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		postIdDDOAsst = tempList.get(0).toString();
		return postIdDDOAsst;
	}

	public List getAllFormsStatusList(String lStrATOPostId, String lStrTOPostId) {

		List listAllForms = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
				.append("SELECT EM.ddoCode,EM.dcpsEmpId,EM.name,EM.phyRcvdFormStatus,EM.regStatus,EM.dcpsId ");
		lSBQuery.append(" FROM MstEmp EM, WfJobMst wf");
		lSBQuery
				.append(" WHERE wf.jobRefId = EM.dcpsEmpId AND wf.lstActPostId IN (:TOPostId,:ATOPostId) AND wf.wfDocMst.docId = :docId and EM.regStatus in (-1,0) order by EM.name,EM.ddoCode,EM.dcpsEmpId,EM.phyRcvdFormStatus,EM.regStatus,EM.dcpsId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("TOPostId", lStrTOPostId);
		lQuery.setParameter("ATOPostId", lStrATOPostId);
		lQuery.setParameter("docId", 700001L);
		listAllForms = lQuery.list();

		return listAllForms;
	}

	/*
	public Long getCountOfEmpOfSameName(String lStrDcpsId) {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long count = 0L;

		lSBQuery
				.append(" select count(*) FROM MstEmp WHERE dcpsId like :lStrDcpsId and dcpsId is not null");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrDcpsId", lStrDcpsId + '%');

		tempList = lQuery.list();
		count = tempList.get(0);
		return count;

	}
	*/
	
	public Long getCountOfEmpOfSameName(String lStrDcpsId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long count = 0L;

		//lSBQuery.append(" select max(dcpsId) FROM MstEmp WHERE dcpsId like :lStrDcpsId and dcpsId is not null and regStatus = 1");
		
		// Above query changed because there were some DCPS Ids for GPF employees (Reg Status 2) also in Production database (Data Discrepancy) 
		lSBQuery.append(" select max(dcpsId) FROM MstEmp WHERE dcpsId like :lStrDcpsId and dcpsId is not null and regStatus in (1,2)");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrDcpsId", lStrDcpsId + '%');

		tempList = lQuery.list();
		
		if(tempList != null)
		{
			if(tempList.size() != 0)
			{
				if(tempList.get(0) != null)
				{
					String maxDCPSId = tempList.get(0).toString();
					count = Long.parseLong(maxDCPSId.substring(maxDCPSId.length()-3, maxDCPSId.length()-1));
				}
			}
		}
		return count;

	}

	public void ArchiveNewRegForm(MstEmp lObjDcpsEmpMst,ServiceLocator serv) throws Exception
	{
		NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null,serv.getSessionFactory());
		RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = lObjNewRegDdoDAO.getPayrollVOForEmpId(lObjDcpsEmpMst.getDcpsEmpId());
		List<MstEmpNmn> NomineesList  = lObjNewRegDdoDAO.getNominees(lObjDcpsEmpMst.getDcpsEmpId().toString());
		
		//Archives the table MstEmpDetails
		MstEmpDetails lObjEmpDetails = new MstEmpDetails();
		lObjEmpDetails.setAcDcpsMaintainedBy(lObjDcpsEmpMst.getAcDcpsMaintainedBy());
		lObjEmpDetails.setAppointmentDate(lObjDcpsEmpMst.getAppointmentDate());
		lObjEmpDetails.setApprovalByDDODate(lObjDcpsEmpMst.getApprovalByDDODate());
		lObjEmpDetails.setBankAccountNo(lObjDcpsEmpMst.getBankAccountNo());
		lObjEmpDetails.setBankName(lObjDcpsEmpMst.getBankName());
		lObjEmpDetails.setBasicPay(lObjDcpsEmpMst.getBasicPay());
		lObjEmpDetails.setBillGroupId(lObjDcpsEmpMst.getBillGroupId());
		lObjEmpDetails.setBranchName(lObjDcpsEmpMst.getBranchName());
		lObjEmpDetails.setBuilding_address(lObjDcpsEmpMst.getBuilding_address());
		lObjEmpDetails.setBuilding_street(lObjDcpsEmpMst.getBuilding_street());
		lObjEmpDetails.setCadre(lObjDcpsEmpMst.getCadre());
		lObjEmpDetails.setCellNo(lObjDcpsEmpMst.getCellNo());
		lObjEmpDetails.setCntctNo(lObjDcpsEmpMst.getCntctNo());
		lObjEmpDetails.setCreatedDate(lObjDcpsEmpMst.getCreatedDate());
		lObjEmpDetails.setCreatedPostId(lObjDcpsEmpMst.getCreatedPostId());
		lObjEmpDetails.setCreatedUserId(lObjDcpsEmpMst.getCreatedUserId());
		lObjEmpDetails.setCurrOff(lObjDcpsEmpMst.getCurrOff());
		lObjEmpDetails.setDbId(lObjDcpsEmpMst.getDbId());
		lObjEmpDetails.setDcpsEmpId(lObjDcpsEmpMst.getDcpsEmpId());
		lObjEmpDetails.setDcpsId(lObjDcpsEmpMst.getDcpsId());
		lObjEmpDetails.setDcpsOrGpf(lObjDcpsEmpMst.getDcpsOrGpf());
		lObjEmpDetails.setDdoCode(lObjDcpsEmpMst.getDdoCode());
		lObjEmpDetails.setDesignation(lObjDcpsEmpMst.getDesignation());
		lObjEmpDetails.setDistrict(lObjDcpsEmpMst.getDistrict());
		lObjEmpDetails.setDob(lObjDcpsEmpMst.getDob());
		lObjEmpDetails.setDoj(lObjDcpsEmpMst.getDoj());
		lObjEmpDetails.setEIDNo(lObjDcpsEmpMst.getEIDNo());
		lObjEmpDetails.setEmailId(lObjDcpsEmpMst.getEmailId());
		lObjEmpDetails.setEmpOnDeptn(lObjDcpsEmpMst.getEmpOnDeptn());
		lObjEmpDetails.setFather_or_husband(lObjDcpsEmpMst.getFather_or_husband());
		lObjEmpDetails.setFirstDesignation(lObjDcpsEmpMst.getFirstDesignation());
		lObjEmpDetails.setFormStatus(lObjDcpsEmpMst.getFormStatus());
		lObjEmpDetails.setGender(lObjDcpsEmpMst.getGender());
		lObjEmpDetails.setGroup(lObjDcpsEmpMst.getGroup());
		lObjEmpDetails.setIFSCCode(lObjDcpsEmpMst.getIFSCCode());
		lObjEmpDetails.setLandmark(lObjDcpsEmpMst.getLandmark());
		lObjEmpDetails.setLangId(lObjDcpsEmpMst.getLangId());
		lObjEmpDetails.setLocality(lObjDcpsEmpMst.getLocality());
		lObjEmpDetails.setLocId(lObjDcpsEmpMst.getLocId());
		lObjEmpDetails.setName(lObjDcpsEmpMst.getName());
		lObjEmpDetails.setName_marathi(lObjDcpsEmpMst.getName_marathi());
		lObjEmpDetails.setOrgEmpMstId(lObjDcpsEmpMst.getOrgEmpMstId());
		lObjEmpDetails.setPANNo(lObjDcpsEmpMst.getPANNo());
		lObjEmpDetails.setParentDept(lObjDcpsEmpMst.getParentDept());
		lObjEmpDetails.setPayCommission(lObjDcpsEmpMst.getPayCommission());
		lObjEmpDetails.setPayScale(lObjDcpsEmpMst.getPayScale());
		lObjEmpDetails.setPhotoAttachmentID(lObjDcpsEmpMst.getPhotoAttachmentID());
		lObjEmpDetails.setPhyRcvdDate(lObjDcpsEmpMst.getPhyRcvdDate());
		lObjEmpDetails.setPhyRcvdFormStatus(lObjDcpsEmpMst.getPhyRcvdFormStatus());
		lObjEmpDetails.setPincode(lObjDcpsEmpMst.getPincode());
		lObjEmpDetails.setReasonChangePFD(lObjDcpsEmpMst.getReasonChangePFD());
		lObjEmpDetails.setRegStatus(lObjDcpsEmpMst.getRegStatus());
		lObjEmpDetails.setRegStatusUpdtdDate(lObjDcpsEmpMst.getRegStatusUpdtdDate());
		lObjEmpDetails.setRemarks(lObjDcpsEmpMst.getRemarks());
		lObjEmpDetails.setSalutation(lObjDcpsEmpMst.getSalutation());
		lObjEmpDetails.setSentBackRemarks(lObjDcpsEmpMst.getSentBackRemarks());
		lObjEmpDetails.setSignatureAttachmentID(lObjDcpsEmpMst.getSignatureAttachmentID());
		lObjEmpDetails.setState(lObjDcpsEmpMst.getState());
		lObjEmpDetails.setUIDNo(lObjDcpsEmpMst.getUIDNo());
		lObjEmpDetails.setUpdatedDate(lObjDcpsEmpMst.getUpdatedDate());
		lObjEmpDetails.setUpdatedPostId(lObjDcpsEmpMst.getUpdatedPostId());
		lObjEmpDetails.setUpdatedUserId(lObjDcpsEmpMst.getUpdatedUserId());
		lObjEmpDetails.setPfdChangedBySRKA(lObjDcpsEmpMst.getPfdChangedBySRKA());
		lObjEmpDetails.setAcNonSRKAEmp(lObjDcpsEmpMst.getAcNonSRKAEmp());
		lObjEmpDetails.setAcMntndByOthers(lObjDcpsEmpMst.getAcMntndByOthers());
		lObjEmpDetails.setReasonForPSChange(lObjDcpsEmpMst.getReasonForPSChange());
		lObjEmpDetails.setOtherReasonForPSChange(lObjDcpsEmpMst.getOtherReasonForPSChange());
		lObjEmpDetails.setWithEffectFromDate(lObjDcpsEmpMst.getWithEffectFromDate());
		lObjEmpDetails.setSevarthId(lObjDcpsEmpMst.getSevarthId());
		lObjEmpDetails.setDeselectionDate(lObjDcpsEmpMst.getDeselectionDate());
		lObjEmpDetails.setAddressVTC(lObjDcpsEmpMst.getAddressVTC());
		//added by arpan
		lObjEmpDetails.setPBuildingAddress(lObjDcpsEmpMst.getPBuildingAddress());
		lObjEmpDetails.setPBuildingStreet(lObjDcpsEmpMst.getPBuildingStreet());
		lObjEmpDetails.setPCellNo(lObjDcpsEmpMst.getPCellNo());
		lObjEmpDetails.setPCntctNo(lObjDcpsEmpMst.getPCntctNo());
		lObjEmpDetails.setPBuildingAddress(lObjDcpsEmpMst.getPBuildingAddress());
		lObjEmpDetails.setPBuildingAddress(lObjDcpsEmpMst.getPBuildingAddress());
		lObjEmpDetails.setPBuildingAddress(lObjDcpsEmpMst.getPBuildingAddress());
		lObjEmpDetails.setPBuildingAddress(lObjDcpsEmpMst.getPBuildingAddress());
		lObjEmpDetails.setPBuildingAddress(lObjDcpsEmpMst.getPBuildingAddress());
		lObjEmpDetails.setPBuildingAddress(lObjDcpsEmpMst.getPBuildingAddress());
		//end
		lObjNewRegDdoDAO.create(lObjEmpDetails);
		
		//Archives the table RltDcpsPayrollEmpDetails
		RltDcpsPayrollEmpDetails lObjRltDcpsPayrollEmpDetails = new RltDcpsPayrollEmpDetails();
		lObjRltDcpsPayrollEmpDetails.setAcMaintainedBy(lObjRltDcpsPayrollEmp.getAcMaintainedBy());
		lObjRltDcpsPayrollEmpDetails.setCreatedDate(lObjRltDcpsPayrollEmp.getCreatedDate());
		lObjRltDcpsPayrollEmpDetails.setCreatedPostId(lObjRltDcpsPayrollEmp.getCreatedPostId());
		lObjRltDcpsPayrollEmpDetails.setCreatedUserId(lObjRltDcpsPayrollEmp.getCreatedUserId());
		lObjRltDcpsPayrollEmpDetails.setCurrCadreJoiningDate(lObjRltDcpsPayrollEmp.getCurrCadreJoiningDate());
		lObjRltDcpsPayrollEmpDetails.setCurrPostJoiningDate(lObjRltDcpsPayrollEmp.getCurrPostJoiningDate());
		lObjRltDcpsPayrollEmpDetails.setDbId(lObjRltDcpsPayrollEmp.getDbId());
		lObjRltDcpsPayrollEmpDetails.setDcpsEmpId(lObjRltDcpsPayrollEmp.getDcpsEmpId());
		lObjRltDcpsPayrollEmpDetails.setDcpsPayrollEmpId(lObjRltDcpsPayrollEmp.getDcpsPayrollEmpId());
		lObjRltDcpsPayrollEmpDetails.setLangId(lObjRltDcpsPayrollEmp.getLangId());
		lObjRltDcpsPayrollEmpDetails.setLocId(lObjRltDcpsPayrollEmp.getLocId());
		lObjRltDcpsPayrollEmpDetails.setPfAcNo(lObjRltDcpsPayrollEmp.getPfAcNo());
		lObjRltDcpsPayrollEmpDetails.setPfSeries(lObjRltDcpsPayrollEmp.getPfSeries());
		lObjRltDcpsPayrollEmpDetails.setPfSeriesDesc(lObjRltDcpsPayrollEmp.getPfSeriesDesc());
		lObjRltDcpsPayrollEmpDetails.setPhychallanged(lObjRltDcpsPayrollEmp.getPhychallanged());
		lObjRltDcpsPayrollEmpDetails.setPostId(lObjRltDcpsPayrollEmp.getPostId());
		lObjRltDcpsPayrollEmpDetails.setUpdatedDate(lObjRltDcpsPayrollEmp.getUpdatedDate());
		lObjRltDcpsPayrollEmpDetails.setUpdatedPostId(lObjRltDcpsPayrollEmp.getUpdatedPostId());
		lObjRltDcpsPayrollEmpDetails.setUpdatedUserId(lObjRltDcpsPayrollEmp.getUpdatedUserId());
		lObjNewRegDdoDAO.create(lObjRltDcpsPayrollEmpDetails);
		
		//Archives the table MstEmpNmnDetails
		for(MstEmpNmn lObjMstEmpNmn:NomineesList)
		{
			MstEmpNmnDetails lObjMstEmpNmnDetails  = new MstEmpNmnDetails();
			lObjMstEmpNmnDetails.setAddress1(lObjMstEmpNmn.getAddress1());
			lObjMstEmpNmnDetails.setAddress2(lObjMstEmpNmn.getAddress2());
			lObjMstEmpNmnDetails.setCreatedDate(lObjMstEmpNmn.getCreatedDate());
			lObjMstEmpNmnDetails.setCreatedPostId(lObjMstEmpNmn.getCreatedPostId());
			lObjMstEmpNmnDetails.setCreatedUserId(lObjMstEmpNmn.getCreatedUserId());
			lObjMstEmpNmnDetails.setDbId(lObjMstEmpNmn.getDbId());
			lObjMstEmpNmnDetails.setDcpsEmpId(lObjMstEmpNmn.getDcpsEmpId());
			lObjMstEmpNmnDetails.setDcpsEmpNmnId(lObjMstEmpNmn.getDcpsEmpNmnId());
			lObjMstEmpNmnDetails.setDob(lObjMstEmpNmn.getDob());
			lObjMstEmpNmnDetails.setLangId(lObjMstEmpNmn.getLangId());
			lObjMstEmpNmnDetails.setLocId(lObjMstEmpNmn.getLocId());
			lObjMstEmpNmnDetails.setName(lObjMstEmpNmn.getName());
			lObjMstEmpNmnDetails.setRlt(lObjMstEmpNmn.getRlt());
			lObjMstEmpNmnDetails.setShare(lObjMstEmpNmn.getShare());
			lObjMstEmpNmnDetails.setUpdatedDate(lObjMstEmpNmn.getUpdatedDate());
			lObjMstEmpNmnDetails.setUpdatedPostId(lObjMstEmpNmn.getUpdatedPostId());
			lObjMstEmpNmnDetails.setUpdatedUserId(lObjMstEmpNmn.getUpdatedUserId());
			lObjNewRegDdoDAO.create(lObjMstEmpNmnDetails);
		}
		
	}
	//NPS
	public Long getCountOfEmpOfSameNameForNps(String PPAN) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long count = 0L;
			System.out.println("PPAN*****"+PPAN);
		//lSBQuery.append(" select max(dcpsId) FROM MstEmp WHERE dcpsId like :lStrDcpsId and dcpsId is not null and regStatus = 1");
		
		// Above query changed because there were some DCPS Ids for GPF employees (Reg Status 2) also in Production database (Data Discrepancy) 
		lSBQuery.append(" select max(ppan) FROM MstEmp WHERE ppan like :PPAN and ppan is not null and  dcpsId is not null and regStatus in (1,2)");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("PPAN", PPAN + '%');

		tempList = lQuery.list();
	
		if(tempList != null)
		{
			if(tempList.size() != 0)
			{
				if(tempList.get(0) != null)
				{
					String maxDCPSId = tempList.get(0).toString();
					System.out.println("maxDCPSId*******"+maxDCPSId);
				
					count = Long.parseLong(maxDCPSId.substring(maxDCPSId.length()-2, maxDCPSId.length()));
				}
			}
		}
		return count;

	}
}
