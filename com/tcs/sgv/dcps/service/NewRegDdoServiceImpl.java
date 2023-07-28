package com.tcs.sgv.dcps.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnLanguageMstDao;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.AttachmentHelper;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoOfficeDAOImpl;
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAO;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.HrPaySubstituteEmpMpg;
import com.tcs.sgv.dcps.valueobject.HstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.dcps.valueobject.RltDdoAsst;
import com.tcs.sgv.eis.dao.CmnlookupMstDAOImpl;
import com.tcs.sgv.eis.dao.HrPayOfficePostMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadPostmpgDAOImpl;
import com.tcs.sgv.eis.dao.OtherDetailDAOImpl;
import com.tcs.sgv.eis.dao.PsrPostMpgDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.eis.valueobject.HrEisOtherDtls;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;
import com.tcs.sgv.eis.valueobject.HrPayOfficepostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadPostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderMst;
import com.tcs.sgv.eis.valueobject.HrPayPsrPostMpg;
import com.tcs.sgv.ess.dao.OrgDesignationMstDao;
import com.tcs.sgv.ess.dao.OrgDesignationMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDao;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;
import com.tcs.sgv.gpf.dao.AGGPFSeriesDAO;
import com.tcs.sgv.gpf.dao.AGGPFSeriesDAOImpl;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;


public class NewRegDdoServiceImpl extends ServiceImpl implements
NewRegDdoService {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */
	

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	String subjectName = null;
	
	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {

		}

	}

	public ResultObject viewApprovedForms(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			List empList = lObjNewRegDdoDAO.getApprovedFormsForDDO(lStrDdoCode);
			inputMap.put("empList", empList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("NewRegApprovedForms");

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject viewFormsForwardedByAsst(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			inputMap.put("DDOCODE", lStrDdoCode);

			String ATOPostIdForDDO = null;
			List UserList = getHierarchyUsers(inputMap);
			ATOPostIdForDDO = UserList.get(0).toString();

			List lListFormsForATORejection = lObjNewRegDdoDAO
			.getApprovalByDDODatesforAll(lStrDdoCode, ATOPostIdForDDO);

			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

			Object[] lObjArr = null;
			List lLongListEmpIds = new ArrayList();
			List lDateListApprovalByDDO = new ArrayList();
			List lLongListEmpIdsForRejection = new ArrayList();
			Integer lIntDaysDifference = null;

			for (Integer lInt = 0; lInt < lListFormsForATORejection.size(); lInt++) {
				lObjArr = (Object[]) lListFormsForATORejection.get(lInt);
				if (lObjArr[1] == null) {
					continue;
				}
				lLongListEmpIds.add(Long.valueOf(lObjArr[0].toString()));
				lDateListApprovalByDDO.add(lObjArr[1]);
				lIntDaysDifference = daysBetween(lObjSimpleDateFormat
						.parse(lObjArr[1].toString()), gDtCurDate);
				if (lIntDaysDifference > 30) {
					lLongListEmpIdsForRejection.add(Long.valueOf(lObjArr[0]
					                                                     .toString()));
				}
			}

			inputMap.put("empIdsForPhysicalFormsNotReceived",
					lLongListEmpIdsForRejection);
			resObj = serv.executeService("rejectRequestForPhyFormNotRcvd",
					inputMap);

			String lStrUser = "DDO";

			List empList = lObjNewRegDdoDAO.getAllDcpsEmployees(lStrUser,
					gStrPostId, lStrDdoCode);
			inputMap.put("empList", empList);

			inputMap.put("EditForm", "N");

			resObj.setResultValue(inputMap);
			resObj.setViewName("NewRegForwardedForms");

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject viewDraftForms(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List empList = null;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
			inputMap.put("DDOCODE", lStrDdoCode);
			String lStrUser = "Asst";

			List lLstParentDept = lObjDcpsCommonDAO
			.getParentDeptForDDO(lStrDdoCode);
			Object[] objParentDept = (Object[]) lLstParentDept.get(0);

			List lLstDesignation = lObjDcpsCommonDAO.getAllDesignation(
					(Long) objParentDept[0], gLngLangId);
			inputMap.put("lLstDesignation", lLstDesignation);

			String lStrSearchCriteria = StringUtility.getParameter(
					"searchCriteria", request);
			String lStrSearchValue = StringUtility.getParameter("searchValue",
					request);

			if (!lStrSearchCriteria.equals("")) {
				if (lStrSearchCriteria.equals("Designation")) {
					empList = lObjNewRegDdoDAO.getAllDcpsEmployeesForDesig(
							lStrUser, gStrPostId, lStrDdoCode, lStrSearchValue);
					inputMap.put("DesignationId", lStrSearchValue.trim());
					inputMap.put("CaseStatus", "");
				}
				if (lStrSearchCriteria.equals("Case Status")) {
					empList = lObjNewRegDdoDAO
					.getAllDcpsEmployeesForCaseStatus(lStrUser,
							gStrPostId, lStrDdoCode, lStrSearchValue);
					inputMap.put("CaseStatus", lStrSearchValue.trim());
					inputMap.put("DesignationId", "");
				}
				inputMap.put("SearchCriteria", lStrSearchCriteria.trim());
			} else {
				empList = lObjNewRegDdoDAO.getAllDcpsEmployees(lStrUser,
						gStrPostId, lStrDdoCode);
				inputMap.put("DesignationId", "");
				inputMap.put("CaseStatus", "");
				inputMap.put("SearchCriteria", "");
			}

			inputMap.put("empList", empList);
			inputMap.put("EditForm", "Y");

			resObj.setResultValue(inputMap);
			resObj.setViewName("NewRegDrafts");

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject loadRegistrationForm(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngEmpID=0l;

		try {

			setSessionInfo(inputMap);
			
			RltDcpsPayrollEmp lObjRltDcpsParrollEmp = null;
			List lObjHrPaySubstituteEmpMpg = null;
			
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			AGGPFSeriesDAO lObjAGGPFSeriesDAO=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
			
			if (request != null) {
				String lStrEmpId = StringUtility.getParameter("empId", request);
				
				gLogger.info("emp id-----"+lStrEmpId);
				if (!lStrEmpId.equals("")) {
				lLngEmpID = Long.parseLong(lStrEmpId);
					MstEmp MstEmpObj = (MstEmp) lObjNewRegDdoDAO.read(lLngEmpID);	
					String doj = MstEmpObj.getDoj().toString();
					gLogger.info("doj ashsish-----"+doj);
					inputMap.put("lObjEmpData", MstEmpObj);
				}
				
			}
			//added by arpan
//			RltDcpsPayrollEmp lObjRltDcpsParrollEmp = null;
//			List lObjHrPaySubstituteEmpMpg = null;
			Long lLngDcpsPayrollId = lObjNewRegDdoDAO.getDcpsEmpPayrollIdForEmpId(lLngEmpID);
//			lObjRltDcpsParrollEmp = (RltDcpsPayrollEmp) lObjNewRegDdoDAO.read(lLngDcpsPayrollId);
			Long postId = lObjNewRegDdoDAO.getPostId(lLngDcpsPayrollId);
			lObjHrPaySubstituteEmpMpg = lObjNewRegDdoDAO.getHrPaySubstituteVO(postId);
			Long subPostId = null;
			Long subLookupId = null;
			Date startDate = null;
			Date endDate = null;
			String lStrSubstituteName = null;
			Iterator it = lObjHrPaySubstituteEmpMpg.iterator();
			if(lObjHrPaySubstituteEmpMpg!=null && lObjHrPaySubstituteEmpMpg.size()>0)
			{
				while(it.hasNext())
				{
					Object[] obj = (Object [])it.next();
					subPostId = Long.parseLong(obj[1].toString());
					subLookupId=Long.parseLong(obj[0].toString());
					startDate=(Date) obj[2];
					endDate = (Date) obj[3];
					obj = null;
				}
			}
			if(subPostId!=null)
				lStrSubstituteName = lObjNewRegDdoDAO.getSubstituteEmpName(subPostId);
			gLogger.info("lStrSubstituuteName"+lStrSubstituteName);
			gLogger.info("subPostId"+subPostId);
			gLogger.info("subLookupId"+subLookupId);
			inputMap.put("substituteEmpName", lStrSubstituteName);
			inputMap.put("subPostId", subPostId);
			inputMap.put("subLookupId", subLookupId);
			if(startDate!=null)
				inputMap.put("startDate", lObjDateFormat.format(startDate));
			if(endDate!=null)
				inputMap.put("endDate", lObjDateFormat.format(endDate));
			lObjHrPaySubstituteEmpMpg = null;
			
			//end
			String lStrDdoCode = null;
			StringUtility.getParameter("User", request);

			inputMap.put("EditForm", "Y");
			lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
			inputMap.put("DDOCODE", lStrDdoCode);

			// Get Employee code for New Registration

			// Long lLongDcpsEmpIdNewReg =
			// IFMSCommonServiceImpl.getNextSeqNum(
			// "MST_DCPS_EMP", inputMap);

			// Get the StateNames
			List lLstState = lObjDcpsCommonDAO.getStateNames(1L);
			inputMap.put("STATENAMES", lLstState);

			// Get the office list from the database
			List listOfficeNames = lObjDcpsCommonDAO
			.getCurrentOffices(lStrDdoCode);
			inputMap.put("OFFICELIST", listOfficeNames);

			// Get the Bank Names
			List lLstBankNames = lObjDcpsCommonDAO.getBankNames();
			inputMap.put("BANKNAMES", lLstBankNames);

			// Get Class List from lookup Master
			List listCityClass = IFMSCommonServiceImpl.getLookupValues(
					"DCPS_OFFICE_CLASS", SessionHelper.getLangId(inputMap),
					inputMap);
			inputMap.put("listCityClass", listCityClass);

			// Get Salutation List from Lookup Master
			List listSalutation = IFMSCommonServiceImpl.getLookupValues(
					"Salutation", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("listSalutation", listSalutation);

			// Get type of Pay Commission from lookup Master
			List listPayCommission = IFMSCommonServiceImpl.getLookupValues(
					"PayCommissionDCPS", SessionHelper.getLangId(inputMap),
					inputMap);
			inputMap.put("listPayCommission", listPayCommission);

			// Get Type of RelationList from lookup Master
			List listRelationship = IFMSCommonServiceImpl
			.getLookupValues("RelationList", SessionHelper
					.getLangId(inputMap), inputMap);
			inputMap.put("listRelationship", listRelationship);

			// Set the current date for validation of date of birth
			Date lDtcurDate = SessionHelper.getCurDate();
			inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));

			// Set the date of joining limit as 01-Nov-2005
			inputMap.put("lDtJoiDtLimit", "01/11/2005");

			// Get PF Account Maintained from lookup Master
			
			/***** Code modified by Niteesh DAT Mumbai for New Column in CMN_LOOKUP_MST
			List lLstPFAccntMntdBy = IFMSCommonServiceImpl.getLookupValues(
					"AccountMaintaindedBy", SessionHelper.getLangId(inputMap),
					inputMap);
					*/
			List lLstPFAccntMntdBy=lObjAGGPFSeriesDAO.getActiveAGList();
			inputMap.put("lLstPFAccntMntdBy", lLstPFAccntMntdBy);
			gLogger.info("lLstPFAccntMntdBy---"+lLstPFAccntMntdBy.size());
			// Get AcDcpsMaintainedBy from Lookup Master
			List lLstPFAccntMntdByDCPS = IFMSCommonServiceImpl.getLookupValues(
					"AccountMaintainedByForDCPSEmp", SessionHelper
					.getLangId(inputMap), inputMap);
			

			inputMap.put("lLstPFAccntMntdByDCPS", lLstPFAccntMntdByDCPS);

			DdoProfileDAO lObjDdoProfileDAO = new DdoProfileDAOImpl(null, serv
					.getSessionFactory());
			List lLstAllDesignations = lObjDdoProfileDAO
			.getAllDesignation(gLngLangId);
			inputMap.put("lLstAllDesignations", lLstAllDesignations);

			// Code Added to get all states List

			List lLstAllStates = lObjDcpsCommonDAO.getStates(1l);
			inputMap.put("lLstAllStates", lLstAllStates);

			List lStrParentDept = lObjDcpsCommonDAO
			.getParentDeptForDDO(lStrDdoCode);
			List listParentDept = null;

			if (lStrParentDept != null) {
				Object[] lListObj = (Object[]) lStrParentDept.get(0);

				Long ParentDeptId = Long.valueOf(lListObj[0].toString());
				String ParentDeptDesc = lListObj[1].toString();
				inputMap.put("ParentDeptIdByDefault", ParentDeptId);
				inputMap.put("ParentDeptDescByDefault", ParentDeptDesc);

				listParentDept = lObjDcpsCommonDAO.getAllHODDepartment(Long
						.parseLong(gObjRsrcBndle
								.getString("DCPS.CurrentFieldDeptID")),
								gLngLangId);
				inputMap.put("listParentDept", listParentDept);

				// Get the Cadre list from the database
				List listCadres = lObjDcpsCommonDAO
				.getCadreForDept(ParentDeptId);
				inputMap.put("CADRELIST", listCadres);

				List lLstDesignation = lObjDcpsCommonDAO
				.getDesigsForPFDAndCadre(ParentDeptId);

				inputMap.put("lLstDesignation", lLstDesignation);

			}

			//added by shailesh : start
			List lLstSubstituteList = lObjNewRegDdoDAO.getSubstituteList();
			System.out.println("lLstSubstituteList "+lLstSubstituteList.size());
			inputMap.put("lLstSubstituteList", lLstSubstituteList);
			
			List religionList = lObjNewRegDdoDAO.getReligionData();
			System.out.println("lLstSubstituteList "+religionList.size());
			inputMap.put("religionList", religionList);
			
			inputMap.put("substOrNt", "no");
			//added by shailesh:end

			List UserList = getHierarchyUsers(inputMap);
			inputMap.put("UserList", UserList);

			List ATOUserList = getAsstHierarchyUsers(inputMap);
			inputMap.put("ATOUserList", ATOUserList);

			/* Changes for GIS Details */
			List lLstGISDetails = IFMSCommonServiceImpl.getLookupValues(
					"GISDetails", SessionHelper.getLangId(inputMap), inputMap);

			inputMap.put("lLstGISDetails", lLstGISDetails);

			List lLstGISGroup = IFMSCommonServiceImpl.getLookupValues(
					"GISGroup", SessionHelper.getLangId(inputMap), inputMap);

			inputMap.put("lLstGISGroup", lLstGISGroup);

			/* Changes for GIS Details ends */
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSRegistrationForm");

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	/**
	 * service method to used to populate the Combo Boxes when Page is Loaded
	 * 
	 * @param Map
	 *            <String,Object> inputMap
	 * @return ResultObject
	 */

	public ResultObject popUpEmpDtls(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrEmpId = null;
		MstEmp MstEmpObj = null;
		DcpsCadreMst lObjMstCadre = null;
		RltDcpsPayrollEmp RltDcpsPayrollEmpObj = null;
		Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
		Long lLngPhotoAttachmentId = null;
		Long lLngSignAttachmentId = null;
		Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
		CmnAttachmentMpg cmnAttachmentMpg = null;
		Long lLngFieldDept = null;
		Long lLngEmpID = null;
		SimpleDateFormat sdf =new SimpleDateFormat ("yyyy-MM-ddhh:mm:ss");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		Date lDateDOB = null;
		Date lDateDOj=null;
		String [] doj=null;
		String month=null;
		String yr=null;

		try {
			setSessionInfo(inputMap);

			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			NewRegDdoDAO lObjNewRegDdoDAOForCadre = new NewRegDdoDAOImpl(
					DcpsCadreMst.class, serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegPayrollDdoDAO = new NewRegDdoDAOImpl(
					RltDcpsPayrollEmp.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			DdoProfileDAO lObjDdoProfileDAO = new DdoProfileDAOImpl(null, serv
					.getSessionFactory());
			if (request != null) {
				lStrEmpId = StringUtility.getParameter("empId", request).trim();
				gLogger.info("emp id-----"+lStrEmpId);
				if (!lStrEmpId.equals("")) {
					lLngEmpID = Long.parseLong(lStrEmpId);
					MstEmpObj = (MstEmp) lObjNewRegDdoDAO.read(lLngEmpID);
					inputMap.put("lObjEmpData", MstEmpObj);

					
					lDateDOj = MstEmpObj.getDoj();
					System.out.println("Date of joining***"+df.format(lDateDOj));
					doj=df.format(lDateDOj).split("/");
					month=doj[1];
					yr=doj[2];
					inputMap.put("month", month);
					inputMap.put("yr", yr);
					System.out.println("month is***"+month+"year is***"+yr);
					
					if (lObjNewRegDdoDAO
							.checkDcpsEmpPayrollIdForEmpIdExists(lLngEmpID)) {
						Long lLngDcpsPayrollId = lObjNewRegDdoDAO
						.getDcpsEmpPayrollIdForEmpId(lLngEmpID);
						RltDcpsPayrollEmpObj = (RltDcpsPayrollEmp) lObjNewRegPayrollDdoDAO
						.read(lLngDcpsPayrollId);
						inputMap.put("lObjEmpPayrollData", RltDcpsPayrollEmpObj);

						String substOrNt = "";
						//added by shailesh
						//List postDetails = new ArrayList();
						Object postDetails[] = new Object[2]; 
						Long postId = RltDcpsPayrollEmpObj.getPostId();
						gLogger.info("post-------"+postId);
						if(postId != null && postId != -1){
						postDetails[0] = postId;
						String postName = lObjNewRegDdoDAO.getPostName(postId);
						//added by sunitha for post display:start
						String postType= lObjNewRegDdoDAO.getPostType(postId);
						if(postType!=null && postType.equals("10001198129")){
							postName=postName+"P";
						}
						
						else if(postType!=null && postType.equals("10001198130")){
							postName=postName+"T";
						}
						inputMap.put("empPostName", postName);
						//added by sunitha for post display:end
						
						
						postDetails[1] = postName;
						//postDetails.add(obj);
						gLogger.info("lLngEmpID "+lLngEmpID);
						gLogger.info("postDetails "+postName);						
						gLogger.info("postId "+postId);
						long lookUpID  = 0l;
						//OrgPostMst post = new OrgPostMst();						
						
							if(lObjNewRegDdoDAO.getPostDetails(postId).size()>= 0 && lObjNewRegDdoDAO.getPostDetails(postId).get(0) != null)
								lookUpID = ((OrgPostMst)lObjNewRegDdoDAO.getPostDetails(postId).get(0)).getPostTypeLookupId().getLookupId();
							gLogger.info("lookUpID "+lookUpID);
							if(lookUpID == 10001198152l || lookUpID == 10001198161l ){
								substOrNt = "yes";
								gLogger.info("substOrNt "+substOrNt);
							}
							else substOrNt =  "no";
						}
						else substOrNt =  "no";
						inputMap.put("substOrNt", substOrNt);
						inputMap.put("postDetails", postDetails);
						//gLogger.info("postDetails "+((Object[])postDetails.get(0)));
						//gLogger.info("postDetails "+((Object[])postDetails.get(0))[1].toString());
					}

				}
			}

			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");

			String lStrDobEmp = lObjSimpleDateFormat.format(MstEmpObj.getDob());


			if(MstEmpObj.getParentDept() != null)
			{
				lLngFieldDept = Long.parseLong(MstEmpObj.getParentDept());
				List lLstDesignation = lObjDcpsCommonDAO
				.getDesigsForPFDAndCadre(lLngFieldDept);
				inputMap.put("lLstDesignation", lLstDesignation);
			}

			if (MstEmpObj.getCadre() != null
					&& !MstEmpObj.getCadre().equalsIgnoreCase("")) {

				lObjMstCadre = (DcpsCadreMst) lObjNewRegDdoDAOForCadre
				.read(Long.valueOf(MstEmpObj.getCadre()));
				inputMap.put("SuperAnnAge", lObjMstCadre.getSuperAntunAge());

				String lStrGroupName = lObjDcpsCommonDAO
				.getCmnLookupNameFromId(Long.valueOf(lObjMstCadre
						.getGroupId().trim()));
				inputMap.put("GroupName", lStrGroupName.trim());
				// inputMap.put("GroupName", lObjMstCadre.getGroupId());

				/*
				String lStrWithoutYear = lStrDobEmp.substring(0, 6);
				Long SuperAnnuationAge = lObjMstCadre.getSuperAntunAge();
				Long lLongBirthYear = Long.valueOf(lStrDobEmp.substring(6));
				Long lLongRetiringYear = lLongBirthYear + SuperAnnuationAge;
				String lStrRetiringYear = lStrWithoutYear
						+ lLongRetiringYear.toString();
				 */

				String lStrRetiringYear = null;
				if(MstEmpObj.getSuperAnndate() != null)
				{
					if(!"".equals(lStrRetiringYear))
					{
						lStrRetiringYear = lObjSimpleDateFormat.format(MstEmpObj.getSuperAnndate());
					}
				}

				inputMap.put("SuperAnnDate", lStrRetiringYear);

				List lLstOfficesForPost = null;
				if (RltDcpsPayrollEmpObj.getPostId() != null
						&& RltDcpsPayrollEmpObj.getPostId() != -1) {
					lLstOfficesForPost = lObjDcpsCommonDAO
					.getOfficesForPost(RltDcpsPayrollEmpObj.getPostId());
				}
				inputMap.put("lLstOfficesForPost", lLstOfficesForPost);
			}

			/*
			 * Check if Pay commission is added, then get list of concerned
			 * payscales
			 */

			List PayScaleList = null;
			if (MstEmpObj.getPayCommission() != null
					&& !MstEmpObj.getPayCommission().equalsIgnoreCase("")) {

				if(!MstEmpObj.getPayCommission().equals("700337"))
				{
					Map voToServiceMap = new HashMap();
					voToServiceMap
					.put("commissionId", MstEmpObj.getPayCommission());

					inputMap.put("voToServiceMap", voToServiceMap);

					resObj = serv.executeService("GetScalefromDesg", inputMap);
					PayScaleList = (List) inputMap.get("PayScaleList");
				}
				inputMap.put("PayScaleList", PayScaleList);
			}

			if (MstEmpObj.getDesignation() != null
					&& !MstEmpObj.getDesignation().equalsIgnoreCase("")) {
				Map voToServiceMap = new HashMap();

				voToServiceMap.put("dsgnId", MstEmpObj.getDesignation());
				inputMap.put("voToServiceMap", voToServiceMap);
				resObj = serv
				.executeService("GetPostfromDesignation", inputMap);

				List CurrentPostList = (List) inputMap.get("CurrentPostList");
				inputMap.put("CurrentPostList", CurrentPostList);
				
				List lLstPostLookUpId = (List) inputMap.get("lLstPostLookUpId");
				inputMap.put("lLstPostLookUpId", lLstPostLookUpId);

			}

			/* code for accessing Ddo Office VO from Office Id */

			if (MstEmpObj.getCurrOff() != null) {
				Long lLongDdoOfficeId = Long.valueOf(MstEmpObj.getCurrOff());
				DdoOffice lObjDdoOfficeVO = lObjNewRegDdoDAO
				.getDdoOfficeVO(lLongDdoOfficeId);
				inputMap.put("lObjDdoOfficeVO", lObjDdoOfficeVO);
			}

			// Added for viewing Photo and signature
			CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(
					CmnAttachmentMst.class, serv.getSessionFactory());
			CmnAttachmentMst lObjCmnAttachmentMst = null;

			if (MstEmpObj.getPhotoAttachmentID() != null
					&& MstEmpObj.getPhotoAttachmentID().doubleValue() > 0) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO
				.findByAttachmentId(Long.parseLong(MstEmpObj
						.getPhotoAttachmentID().toString()));

				cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

				if (lObjCmnAttachmentMst != null) {
					lLngPhotoAttachmentId = lObjCmnAttachmentMst
					.getAttachmentId();
				}
				if (lObjCmnAttachmentMst != null
						&& lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
					cmnAttachmentMpgs = lObjCmnAttachmentMst
					.getCmnAttachmentMpgs();
				}
				cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();
				Long srNo = 0L;
				for (Integer lInt = 0; lInt < cmnAttachmentMpgs.size(); lInt++) {
					cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
					if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("photo")) {
						srNo = cmnAttachmentMpg.getSrNo();
						inputMap.put("Photo", lObjCmnAttachmentMst);
						inputMap.put("PhotoId", lLngPhotoAttachmentId);
						inputMap.put("PhotosrNo", srNo);
					}
				}
			}

			if (MstEmpObj.getSignatureAttachmentID() != null
					&& MstEmpObj.getSignatureAttachmentID().doubleValue() > 0) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO
				.findByAttachmentId(Long.parseLong(MstEmpObj
						.getSignatureAttachmentID().toString()));

				cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

				if (lObjCmnAttachmentMst != null) {
					lLngSignAttachmentId = lObjCmnAttachmentMst
					.getAttachmentId();
				}
				if (lObjCmnAttachmentMst != null
						&& lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
					cmnAttachmentMpgs = lObjCmnAttachmentMst
					.getCmnAttachmentMpgs();
				}

				cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();
				Long srNo = 0L;
				for (Integer lInt = 0; lInt < cmnAttachmentMpgs.size(); lInt++) {
					cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
					if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase(
					"signature")) {
						srNo = cmnAttachmentMpg.getSrNo();
						inputMap.put("Sign", lObjCmnAttachmentMst);
						inputMap.put("SignId", lLngSignAttachmentId);
						inputMap.put("SignsrNo", srNo);
					}
				}
			}

			// Added for viewing photo and signature overs
			// Get the StateNames

			List<MstEmpNmn> NomineesList = lObjNewRegDdoDAO
			.getNominees(lStrEmpId);
			inputMap.put("NomineesList", NomineesList);

			Integer lIntTotalNominees = NomineesList.size();
			inputMap.put("lIntTotalNominees", lIntTotalNominees);

			String lStrDdoCode = null;
			String lStrUser = StringUtility.getParameter("User", request).trim();

			if (lStrUser.equals("DDO")) {
				inputMap.put("EditForm", "N");
				lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
				inputMap.put("DDOCODE", lStrDdoCode);

			}

			else if (lStrUser.equals("Asst")) {

				inputMap.put("EditForm", "Y");
				lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
				inputMap.put("DDOCODE", lStrDdoCode);

			}

			else if (lStrUser.equals("MDC"))
			{
				inputMap.put("EditForm", "N");
				lStrDdoCode = MstEmpObj.getDdoCode();
				inputMap.put("DDOCODE", lStrDdoCode);
			}

			/* Changes for GIS Details */
			List lLstGISDetails = IFMSCommonServiceImpl.getLookupValues(
					"GISDetails", SessionHelper.getLangId(inputMap), inputMap);

			inputMap.put("lLstGISDetails", lLstGISDetails);

			List lLstGISGroup = IFMSCommonServiceImpl.getLookupValues(
					"GISGroup", SessionHelper.getLangId(inputMap), inputMap);

			inputMap.put("lLstGISGroup", lLstGISGroup);

			/* Changes for GIS Details ends */

			String lStrFromSearch = StringUtility.getParameter("FromSearchEmp",
					request).trim();

			inputMap.put("lStrFromSearch", lStrFromSearch);

			List lLstState = lObjDcpsCommonDAO.getStateNames(1L);
			inputMap.put("STATENAMES", lLstState);

			// Get the office list from the database
			List listOfficeNames = lObjDcpsCommonDAO
			.getCurrentOffices(lStrDdoCode);
			inputMap.put("OFFICELIST", listOfficeNames);

			// Get Salutation List from Lookup Master
			List listSalutation = IFMSCommonServiceImpl.getLookupValues(
					"Salutation", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("listSalutation", listSalutation);

			lLngFieldDept = Long.parseLong(MstEmpObj.getParentDept());
			// Long lLngPayCmnId = Long.parseLong(MstEmpObj.getPayCommission());

			List listRelationship = IFMSCommonServiceImpl
			.getLookupValues("RelationList", SessionHelper
					.getLangId(inputMap), inputMap);
			inputMap.put("listRelationship", listRelationship);

			// Get the Bank Names
			List lLstBankNames = lObjDcpsCommonDAO.getBankNames();
			inputMap.put("BANKNAMES", lLstBankNames);

			// Get the BankBranchNames
			if (MstEmpObj.getBankName() != null) {
				List lLstBrachNames = lObjDcpsCommonDAO.getBranchNames(Long
						.valueOf(MstEmpObj.getBankName()));
				inputMap.put("BRANCHNAMES", lLstBrachNames);
			}

			// Get the list of all  states
			List lLstAllStates = lObjDcpsCommonDAO.getStates(1l);
			inputMap.put("lLstAllStates", lLstAllStates);

			// Get the Cadre list from the database
			List listCadres = lObjDcpsCommonDAO.getCadreForDept(lLngFieldDept);
			inputMap.put("CADRELIST", listCadres);

			List listParentDept = lObjDcpsCommonDAO.getAllHODDepartment(Long
					.parseLong(gObjRsrcBndle
							.getString("DCPS.CurrentFieldDeptID")), gLngLangId);
			inputMap.put("listParentDept", listParentDept);

			List listPayCommission = IFMSCommonServiceImpl.getLookupValues(
					"PayCommissionDCPS", SessionHelper.getLangId(inputMap),
					inputMap);
			inputMap.put("listPayCommission", listPayCommission);

			// Set the current date for validation of date of birth
			Date lDtcurDate = SessionHelper.getCurDate();
			inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));

			// Set the date of joining limit as 01-Nov-2005
			inputMap.put("lDtJoiDtLimit", "01/11/2005");

			// Get PF Account Maintained from lookup Master
			/*List lLstPFAccntMntdBy = IFMSCommonServiceImpl.getLookupValues(
					"AccountMaintaindedBy", SessionHelper.getLangId(inputMap),
					inputMap);*/ //commented by arpan for pf series
			AGGPFSeriesDAO lObjAGGPFSeriesDAO=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
			List lLstPFAccntMntdBy=lObjAGGPFSeriesDAO.getActiveAGList();
			inputMap.put("lLstPFAccntMntdBy", lLstPFAccntMntdBy);

			// Get AcDcpsMaintainedBy from Lookup Master
			List lLstPFAccntMntdByDCPS = IFMSCommonServiceImpl.getLookupValues(
					"AccountMaintainedByForDCPSEmp", SessionHelper
					.getLangId(inputMap), inputMap);
			inputMap.put("lLstPFAccntMntdByDCPS", lLstPFAccntMntdByDCPS);

			// Get PF Series from lookup Master
			
			
			List lLstPFSeries = null;
			String lStrAcMntndBy = RltDcpsPayrollEmpObj.getAcMaintainedBy();
			String MumbaiOrNagpurAG = null;
			if (lStrAcMntndBy != null && !lStrAcMntndBy.equals("")) {
				
				AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
				
				lLstPFSeries=dao.getPFSeriesFromAGLookupId(lStrAcMntndBy);
				 
				
				
				if (lStrAcMntndBy.equals("700092")) {
					/*  #### Code commented & Modified
					 *  by Niteesh DAT Mumbai For
					 * #####  Add PF Series Utility
					lLstPFSeries = IFMSCommonServiceImpl.getLookupValues(
							"PF_Series", SessionHelper.getLangId(inputMap),
							inputMap);   */
					MumbaiOrNagpurAG = "Yes";
				} // PF Series for AG Mumbai
				else if (lStrAcMntndBy.equals("700093")) {
					/*	lLstPFSeries = IFMSCommonServiceImpl.getLookupValues(
							"PF_Series_AG_Nagpur", SessionHelper
							.getLangId(inputMap), inputMap);    */
					MumbaiOrNagpurAG = "Yes";
				} // PF Series for AG Nagpur
				else {
					MumbaiOrNagpurAG = "Yes";
				}
			}
			inputMap.put("lLstPFSeries", lLstPFSeries);
			inputMap.put("MumbaiOrNagpurAG", MumbaiOrNagpurAG);
			
			
			
			//************Code modified by Niteesh Bhargava DAT 
			//************for New PF Series Entry
			
			List lLstAllDesignations = lObjDdoProfileDAO
			.getAllDesignation(gLngLangId);
			inputMap.put("lLstAllDesignations", lLstAllDesignations);

			inputMap.put("UserType", lStrUser);
			List UserList = getHierarchyUsers(inputMap);
			inputMap.put("UserList", UserList);

			List ATOUserList = getAsstHierarchyUsers(inputMap);
			inputMap.put("ATOUserList", ATOUserList);
			
			
			//added by shailesh : start
			List lLstSubstituteList = lObjNewRegDdoDAO.getSubstituteList();
			System.out.println("lLstSubstituteList "+lLstSubstituteList.size());
			inputMap.put("lLstSubstituteList", lLstSubstituteList);

			List religionList = lObjNewRegDdoDAO.getReligionData();
			System.out.println("lLstSubstituteList "+religionList.size());
			inputMap.put("religionList", religionList);
			// Get the type of user
			
			//added by arpan
			RltDcpsPayrollEmp lObjRltDcpsParrollEmp = null;
			List lObjHrPaySubstituteEmpMpg = null;
			Long lLngDcpsPayrollId = lObjNewRegDdoDAO.getDcpsEmpPayrollIdForEmpId(lLngEmpID);
//			lObjRltDcpsParrollEmp = (RltDcpsPayrollEmp) lObjNewRegDdoDAO.read(lLngDcpsPayrollId);
			Long postId = lObjNewRegDdoDAO.getPostId(lLngDcpsPayrollId);
			lObjHrPaySubstituteEmpMpg = lObjNewRegDdoDAO.getHrPaySubstituteVO(postId);
			Long subPostId = null;
			Long subLookupId = null;
			Date startDate = null;
			Date endDate = null;
			String lStrSubstituteName = null;
			Iterator it = lObjHrPaySubstituteEmpMpg.iterator();
			Object[] obj = null;
			if(lObjHrPaySubstituteEmpMpg!=null && lObjHrPaySubstituteEmpMpg.size()>0)
			{
				while(it.hasNext())
				{
					obj = (Object [])it.next();
					subPostId = Long.parseLong(obj[1].toString());
					subLookupId=Long.parseLong(obj[0].toString());
					startDate=(Date) obj[2];
					endDate = (Date) obj[3];
					obj = null;
				}
			}
			if(subPostId!=null)
				lStrSubstituteName = lObjNewRegDdoDAO.getSubstituteEmpName(subPostId);
			gLogger.info("lStrSubstituuteName"+lStrSubstituteName);
			gLogger.info("subPostId"+subPostId);
			gLogger.info("subLookupId"+subLookupId);
			inputMap.put("substituteEmpName", lStrSubstituteName);
			inputMap.put("subPostId", subPostId);
			inputMap.put("subLookupId", subLookupId);
			if(startDate!=null)
				inputMap.put("startDate", lObjDateFormat.format(startDate));
			if(endDate!=null)
				inputMap.put("endDate", lObjDateFormat.format(endDate));
			lObjHrPaySubstituteEmpMpg = null;
			lLngDcpsPayrollId = null;
			postId = null;
			subPostId = null;
			lStrSubstituteName = null;
			subLookupId = null;
			startDate = null;
			endDate = null;
			//end
			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSRegistrationForm");

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject viewAttachmentForDCPS(Map mp) {

		ResultObject objRes = new ResultObject(-1, "FAIL");

		try {
			setSessionInfo(mp);
			StringBuilder lStrData = new StringBuilder();
			byte allBytesInBlob[] = new byte[0];
			objRes = new ResultObject(0, "FAIL");
			Map fileItemArrayListMap = AttachmentHelper.fileItemArrayListMap;
			String key = "";
			String rowNumber = request.getParameter("rowNumber");
			String attachmentName = request.getParameter("attachmentUniqeName");
			Integer rowIndex = 0;
			if (request.getParameter("rowIndex") != null) {
				rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
			}
			Integer rowCount = 0;
			if (request.getParameter("rowCount") != null) {
				rowCount = Integer.parseInt(request.getParameter("rowCount"));
			}
			rowIndex -= rowCount;
			if (rowNumber != null && rowNumber.length() >= 1) {
				key = (new StringBuilder(String.valueOf(request.getSession()
						.getAttribute("name")))).append(attachmentName).append(
								request.getSession().getId()).append(rowNumber)
								.toString();
			} else {
				key = (new StringBuilder(String.valueOf(request.getSession()
						.getAttribute("name")))).append(attachmentName).append(
								request.getSession().getId()).toString();
			}
			ArrayList attachmentList = (ArrayList) fileItemArrayListMap
			.get(key);
			if (attachmentList != null && !attachmentList.isEmpty()) {
				FileItem fileItem = (FileItem) attachmentList
				.get(attachmentList.size() - 1);
				allBytesInBlob = fileItem.get();
			}
			mp.put("buteArray", allBytesInBlob);
			lStrData.append("<XMLDOC>");
			lStrData.append("<HEADDESC>");
			lStrData.append(allBytesInBlob);
			lStrData.append("</HEADDESC>");
			lStrData.append("</XMLDOC>");

			objRes.setResultValue(mp);
			objRes.setViewName("viewAttachment");
			//System.out.println("End of Service to view");
		} catch (Exception e) {
			objRes.setThrowable(e);
			objRes.setResultCode(-1);

		}
		return objRes;
	}

	public ResultObject saveDCPSEmpData(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstEmp lObjEmpData = null;
		RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;
		Boolean lBlFlag;
		Long lLngEmpId = 0l;
		Long lLngDcpsPayrollEmpId = 0l;

		try {
			setSessionInfo(inputMap);

			lObjEmpData = new MstEmp();
			lObjEmpData = (MstEmp) inputMap.get("DCPSEmpData");
			lBlFlag = false;

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS,
			"FAIL");
			objRes = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			objRes = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) objRes.getResultValue();

			Long lLngAttachId = 0L;
			if (attachMap.get("AttachmentId_Photo") != null) {
				lLngAttachId = Long.parseLong(String.valueOf(attachMap
						.get("AttachmentId_Photo")));
				lObjEmpData.setPhotoAttachmentID(lLngAttachId);
			}

			if (attachMap.get("AttachmentId_Sign") != null) {
				lLngAttachId = Long.parseLong(String.valueOf(attachMap
						.get("AttachmentId_Sign")));
				lObjEmpData.setSignatureAttachmentID(lLngAttachId);
			}

			lLngEmpId = IFMSCommonServiceImpl.getNextSeqNum("MST_DCPS_EMP",
					inputMap);
			lObjEmpData.setDcpsEmpId(lLngEmpId);
			lObjEmpData.setSevarthId(lLngEmpId.toString());
			lObjEmpData.setDcpsId(lLngEmpId.toString());
			
			//aaded by shailesh for Religion
			String religion = StringUtility.getParameter("lstReligion", request);
			gLogger.info("religion is "+religion);
			lObjEmpData.setReligion(religion);
			
			lObjNewRegDdoDAO.create(lObjEmpData);

			lObjRltDcpsPayrollEmp = new RltDcpsPayrollEmp();
			lObjRltDcpsPayrollEmp = (RltDcpsPayrollEmp) inputMap
			.get("DCPSEmpPayrollData");
			lLngDcpsPayrollEmpId = IFMSCommonServiceImpl.getNextSeqNum(
					"RLT_DCPS_PAYROLL_EMP", inputMap);
			lObjRltDcpsPayrollEmp.setDcpsEmpId(lLngEmpId);
			lObjRltDcpsPayrollEmp.setDcpsPayrollEmpId(lLngDcpsPayrollEmpId);
			lObjNewRegDdoDAO.create(lObjRltDcpsPayrollEmp);

			lBlFlag = true;
			
			// Long lFormStatus = lObjEmpData.getFormStatus();
			// Create the workflow only when it is a new request
			// if (lFormStatus == 1)
			// { createWF(inputMap); }

		} catch (Exception ex) {
			resObj.setResultValue(null);
			gLogger.error(" Error is : " + ex, ex);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag,
				lObjEmpData.getDcpsEmpId()).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	/**
	 * 
	 * Service to update the Employee's DCPS Data
	 * 
	 * @author Kapil Devani
	 * @param inputMap
	 * @return
	 */
	public ResultObject updateDCPSEmpData(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstEmp lObjEmpUpdatedData = null;
		RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;
		Boolean lBlFlag;
		Boolean lBlPayrollVOExistsOrNot = true;
		RltDcpsPayrollEmp lObjTempRltDcpsPayrollEmp = null;

		try {
			setSessionInfo(inputMap);

			lObjEmpUpdatedData = new MstEmp();
			lObjEmpUpdatedData = (MstEmp) inputMap.get("DCPSEmpData");

			lObjRltDcpsPayrollEmp = (RltDcpsPayrollEmp) inputMap
			.get("DCPSEmpPayrollData");

			lBlFlag = false;

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();

			Long lLngAttachId = 0L;
			if (attachMap.get("AttachmentId_Photo") != null) {
				lLngAttachId = Long.parseLong(String.valueOf(attachMap
						.get("AttachmentId_Photo")));
				lObjEmpUpdatedData.setPhotoAttachmentID(lLngAttachId);
			}

			if (attachMap.get("AttachmentId_Sign") != null) {
				lLngAttachId = Long.parseLong(String.valueOf(attachMap
						.get("AttachmentId_Sign")));
				lObjEmpUpdatedData.setSignatureAttachmentID(lLngAttachId);
			}

			lObjNewRegDdoDAO.update(lObjEmpUpdatedData);

//			lObjTempRltDcpsPayrollEmp = lObjNewRegDdoDAO
//			.getPayrollVOForEmpId(lObjEmpUpdatedData.getDcpsEmpId());
//
//			if (lObjTempRltDcpsPayrollEmp == null) {
//				lBlPayrollVOExistsOrNot = false;
//			}

			if (lBlPayrollVOExistsOrNot) {
				lObjNewRegDdoDAO.update(lObjRltDcpsPayrollEmp);
			}

			Long lLngFormStatus = lObjEmpUpdatedData.getFormStatus();
			if (lLngFormStatus == 1L) {
				createWF(inputMap);
			}

			lBlFlag = true;

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is : " + ex, ex);
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag,
				lObjEmpUpdatedData.getDcpsEmpId()).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	/*
	 * Method to generate the xml response for Ajax
	 */

	/**
	 * service method to used to populate the Designations names Combo box
	 * according to the selected Current Office
	 * 
	 * @param Map
	 *            <String,Object> lMapInputMap
	 * @return ResultObject
	 */

	public ResultObject populateDesigsUsingAjax(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS,
		"FAIL");

		try {

			setSessionInfo(lMapInputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrCurrOffice = StringUtility.getParameter(
					"cmbCurrentOffice", request);

			// Call the functions to get list of designations for the selected
			// office
			List lLstDesignations = lObjDcpsCommonDAO
			.getDesignations(lStrCurrOffice);

			String lStrTempResult = null;
			if (lLstDesignations != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(
						lLstDesignations, "desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}

	/**
	 * service method to used to populate the Group name TextBox according to
	 * the selected Cadre
	 * 
	 * @param Map
	 *            <String,Object> inputMap
	 * @return ResultObject
	 */

	// Old One

	/*
	public ResultObject populateGroupUsingAjax(Map<String, Object> inputMap)
			throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrGroupName = null;
		Long SuperAnnuationAge = null;
		String lStrRetiringYear = null;
		List finalList = null;
		Long lLongCadreId = null;
		// Get the group name for the cadre name selected
		try {

			setSessionInfo(inputMap);

			String lStrCadreId = StringUtility
					.getParameter("cmbCadre", request).trim();

			if (!lStrCadreId.equalsIgnoreCase("")) {
				lLongCadreId = Long.valueOf(lStrCadreId);
			}

			String lStrDobEmp = StringUtility.getParameter("dobEmp", request);
			String lStrWithoutYear = lStrDobEmp.substring(0, 6);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			finalList = lObjNewRegDdoDAO.getGroupName(lLongCadreId);

			Object[] groupAndSuperAnnAge = (Object[]) finalList.get(0);

			if (finalList != null && finalList.size() > 0) {

				lStrGroupName = groupAndSuperAnnAge[0].toString();
				SuperAnnuationAge = Long.valueOf(groupAndSuperAnnAge[1]
						.toString());
				Long lLongBirthYear = Long.valueOf(lStrDobEmp.substring(6));
				Long lLongRetiringYear = lLongBirthYear + SuperAnnuationAge;
				lStrRetiringYear = lStrWithoutYear
						+ lLongRetiringYear.toString();

			}
		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
			return objRes;
		}

		String lSBStatus = getResponseXMLDocForGroup(lStrGroupName,
				SuperAnnuationAge, lStrRetiringYear).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	 */

	// New One Modified 

	public ResultObject populateGroupUsingAjax(Map<String, Object> inputMap)
	throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrGroupName = null;
		Long SuperAnnuationAge = null;
		String lStrRetiringYear = null;
		List finalList = null;
		Long lLongCadreId = null;
		Date lDtDobEmp = null;
		Integer lIntDay=null;
		Integer lIntMonth=null;
		Integer lIntYear=null;
		String lStrDay = null;
		String lStrMonth = null;
		String lStrYear = null;

		// Get the group name for the cadre name selected
		try {

			setSessionInfo(inputMap);

			String lStrCadreId = StringUtility.getParameter("cmbCadre", request).trim();

			if (!lStrCadreId.equalsIgnoreCase("")) {
				lLongCadreId = Long.valueOf(lStrCadreId);
			}
			String lStrDobEmp = StringUtility.getParameter("dobEmp", request);			
			if(!lStrDobEmp.equals("")){

				NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
						serv.getSessionFactory());

				finalList = lObjNewRegDdoDAO.getGroupName(lLongCadreId);				

				if (finalList != null && finalList.size() > 0) {

					Object[] groupAndSuperAnnAge = (Object[]) finalList.get(0);

					lStrGroupName = groupAndSuperAnnAge[0].toString();
					SuperAnnuationAge = Long.valueOf(groupAndSuperAnnAge[1].toString());

					String lArrStrDOB[] = lStrDobEmp.split("/");
					Long lLongBirthYear = Long.valueOf(lArrStrDOB[2]);					
					Long lLongRetiringYear = lLongBirthYear + SuperAnnuationAge;

					lDtDobEmp = IFMSCommonServiceImpl.getDateFromString(lStrDobEmp);
					Calendar lCalendar = Calendar.getInstance();					
					lCalendar.set(lLongRetiringYear.intValue(), lDtDobEmp.getMonth() , lDtDobEmp.getDate());
					lIntDay=lCalendar.get(lCalendar.DATE);
					lIntMonth=lCalendar.get(lCalendar.MONTH) + 1;
					lIntYear=lCalendar.get(lCalendar.YEAR);

					if(lIntDay.equals(1)){
						if(lIntMonth.equals(1)){							
							lStrDay = "31";
							lStrMonth = "12";
							lStrYear = String.valueOf(lLongBirthYear - 1L + SuperAnnuationAge);
						}else if(lIntMonth.equals(2)){
							lStrDay = "31";
							lStrMonth = "1";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(3)){
							lStrDay = String.valueOf((((lIntYear % 4 == 0) && ( (!(lIntYear % 100 == 0)) || (lIntYear % 400 == 0))) ? 29 : 28 ));							
							lStrMonth = "2";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(4)){
							lStrDay = "31";							
							lStrMonth = "3";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(5)){
							lStrDay = "30";							
							lStrMonth = "4";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(6)){
							lStrDay = "31";							
							lStrMonth = "5";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(7)){
							lStrDay = "30";							
							lStrMonth = "6";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(8)){
							lStrDay = "31";							
							lStrMonth = "7";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(9)){
							lStrDay = "31";							
							lStrMonth = "8";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(10)){
							lStrDay = "30";							
							lStrMonth = "9";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(11)){
							lStrDay = "31";							
							lStrMonth = "10";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}else if(lIntMonth.equals(12)){
							lStrDay = "30";							
							lStrMonth = "11";
							lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);
						}
					}else{										
						lStrDay = String.valueOf(lCalendar.getActualMaximum(lCalendar.DAY_OF_MONTH));						 
						lStrMonth = lIntMonth.toString();
						lStrYear = String.valueOf(lLongBirthYear + SuperAnnuationAge);

					}
					lStrRetiringYear = lStrDay+"/"+lStrMonth+"/" + lStrYear;
				}
			}			
		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			return objRes;
		}

		String lSBStatus = getResponseXMLDocForGroup(lStrGroupName,
				SuperAnnuationAge, lStrRetiringYear).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	public ResultObject getOfficeDetails(Map<String, Object> inputMap)
	throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			Long lLngOfficeId = Long.parseLong(StringUtility.getParameter(
					"officeId", request).trim());

			finalList = lObjNewRegDdoDAO.getOfficeDetails(lLngOfficeId);

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return objRes;
		}

		String lSBStatus = getResponseXMLDocForOffice(finalList).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	/**
	 * service method to used to save DCPS Nominee Details into Databse
	 * 
	 * @param Map
	 *            <String,Object> inputMap
	 * @return ResultObject
	 */

	public ResultObject saveNomineeDetails(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag;
		Integer SaveOrUpdateNominee;

		try {
			setSessionInfo(inputMap);

			MstEmpNmn[] lArrNomineeDtls = (MstEmpNmn[]) inputMap
			.get("DCPSNomineeDtls");
			gLogger.info("lArrNomineeDtls-----"+lArrNomineeDtls.length);
			gLogger.info("lArrNomineeDtls[0]----"+lArrNomineeDtls[0].getGuardian());
			lBlFlag = false;

			SaveOrUpdateNominee = (Integer) inputMap.get("SaveOrUpdateNominee");
			gLogger.info("SaveOrUpdateNominee-----"+SaveOrUpdateNominee);
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			// Save the data for nominees in the database

			if (SaveOrUpdateNominee == 1) {
				gLogger.info("inside SaveOrUpdateNominee = 1");
				for (Integer lInt = 0; lInt < lArrNomineeDtls.length; lInt++) {
					Long lLngNmnId = IFMSCommonServiceImpl.getNextSeqNum(
							"MST_DCPS_EMP_NMN", inputMap);
					lArrNomineeDtls[lInt].setDcpsEmpNmnId(lLngNmnId);
					gLogger.info("nominee by roshan kuam r osa a *****"+lInt+"*****"+lArrNomineeDtls[lInt].getGuardian());
					
					gLogger.info("nominee by roshan kuam r osa a *******"+lInt+"*****"+lArrNomineeDtls[lInt].getDcpsEmpNmnId());
					lObjNewRegDdoDAO.create(lArrNomineeDtls[lInt]);
					lBlFlag = true;
				}

			}
			if (SaveOrUpdateNominee > 1) {
				gLogger.info("inside else");
				// dcpsNewRegistrationDAO
				// .deleteNomineesForGivenEmployee(lLngEmpID);

				for (Integer lInt = 0; lInt < lArrNomineeDtls.length; lInt++) {
					Long lLngNmnId = IFMSCommonServiceImpl.getNextSeqNum(
							"MST_DCPS_EMP_NMN", inputMap);
					lArrNomineeDtls[lInt].setDcpsEmpNmnId(lLngNmnId);
					gLogger.info("nominee by roshan kuam r osa a ********"+lInt+"****"+lArrNomineeDtls[lInt].getGuardian());
					gLogger.info("nominee by roshan kuam r osa a ********"+lInt+"****"+lArrNomineeDtls[lInt].getDcpsEmpNmnId());
					lObjNewRegDdoDAO.create(lArrNomineeDtls[lInt]);
					lBlFlag = true;
				}

			}

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return resObj;
		}

		String lSBStatus = getResponseXMLDocForNominee(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/**
	 * 
	 * <H3>Description -</H3> Method to populate the worklist of all saved cases
	 * for DDO Assistant
	 * 
	 * 
	 * @author Kapil Devani
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */

	/*
	 * This method will get the list of hierarchy users at the next level
	 */

	private List getHierarchyUsers(Map inputMap) {

		List UserList = null;

		try {

			setSessionInfo(inputMap);
			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();

			// Get the Subject Name
			subjectName = gObjRsrcBndle
			.getString("DCPS.RegistrationForm");

			// Get the Hierarchy Id
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId,	subjectName, inputMap);
			
			// Get the From level Id
			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId,
					lLngHierRefId, inputMap);

			// Get the List of Post ID of the users at the next Level
			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId,
					lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}
		return UserList;
	}

	/* Function to get hierarchy from Asst to ATo */
	private List getAsstHierarchyUsers(Map inputMap) {

		List UserList = null;

		try {

			setSessionInfo(inputMap);
			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();

			// Get the Subject Name
			String subjectName = gObjRsrcBndle
			.getString("DCPS.RegistrationForm");

			// Get the Hierarchy Id
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId,subjectName, inputMap);
			
			
			// Get the From level Id
			llFromLevelId = 20;

			// Get the List of Post ID of the users at the next Level
			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId,lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}
		return UserList;
	}

	/**
	 * 
	 * <H3>Description -</H3> Function to forward the request to DDO from DDO
	 * Asst.
	 * 
	 * 
	 * @author Kapil Devani
	 * @param objectArgs
	 * @return
	 */
	public ResultObject forwardRequestToDDO(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag;
		Long lLongDCPSEmpId;
		Boolean lBlDuplicateEmp = false;

		try {

			setSessionInfo(objectArgs);

			lLongDCPSEmpId = 0l;
			lBlFlag = false;

			String lStrApproveFlag = StringUtility
			.getParameter("flag", request).toString();
			String toPost = null;
			if (lStrApproveFlag.equals("1")) {
				toPost = StringUtility.getParameter("ApproveToPost", request)
				.toString();
			} else {
				toPost = StringUtility.getParameter("ForwardToPost", request)
				.toString();
			}

			String toLevel = gObjRsrcBndle.getString("DCPS.DDO");
			String strPKValue = StringUtility.getParameter("Emp_Id", request)
			.toString().trim();

			// Split the array to get the ID of forms selected
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);

			objectArgs.put("jobTitle", gObjRsrcBndle
					.getString("DCPS.RegistrationForm"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.RegistrationFormID")));

			// Variables declared to match with existing data

			String lStrEmpName = null;
			SimpleDateFormat sdf =new SimpleDateFormat ("yyyy-MM-ddhh:mm:ss");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			
			Date lDateDOB = null;
			Date lDateDOj=null;
			String [] doj=null;
			String month=null;
			String yr=null;
			Character lCharGender = null;
			String lStrFatherOrHusband = null;
			Character lCharFatherOrHusband = null;
		
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(
					MstEmp.class, serv.getSessionFactory());

			// Iterates more than 1 time if more than 1 form are selected
			for (Integer index = 0; index < strArrPKValue.length; index++) {

				lBlDuplicateEmp = false;
				// Never delete or comment above line else will create hevoc !!
				objectArgs.put("Pkvalue", strArrPKValue[index]);

				Long lLongPKValue = Long.parseLong(strArrPKValue[index].trim());
				MstEmp lObjDcpsEmpMst = (MstEmp) lObjNewRegDdoDAO.read(lLongPKValue);
					
				// Code that will check duplicate employee and send such employees to MDC
					
				lStrEmpName = lObjDcpsEmpMst.getName().trim();
				lDateDOB = lObjDcpsEmpMst.getDob();
				lDateDOj = lObjDcpsEmpMst.getDoj();
				System.out.println("Date of joining***"+df.format(lDateDOj));
				doj=df.format(lDateDOj).split("/");
				month=doj[1];
				yr=doj[2];
				objectArgs.put("month", month);
				objectArgs.put("yr", yr);
				System.out.println("month is***"+month+"year is***"+yr);
				
				//System.out.println("first parse**"+sdf1.format(lDateDOj));
				//String doj=sdf.format(sdf1.parse(lDateDOj.toString()));
				//gLogger.info("lDateDOj before parse"+lDateDOj);
				//gLogger.info("doj after parse"+doj);
				
				lCharGender = lObjDcpsEmpMst.getGender();

				lStrFatherOrHusband = lObjDcpsEmpMst.getFather_or_husband();

				if(lStrFatherOrHusband != null)
				{
					if(!"".equals(lStrFatherOrHusband) && !"NOT PROVIDED".equals(lStrFatherOrHusband.trim().toUpperCase()))
					{
						lCharFatherOrHusband = lObjDcpsEmpMst.getFather_or_husband().charAt(0);
					}
				}

				// Does not check the condition if the duplicate employee is approved by MDC
				if(lObjDcpsEmpMst.getDupEmpSentToMDC() != null)
				{
					if(lObjDcpsEmpMst.getDupEmpSentToMDC() != 1)
					{
						lBlDuplicateEmp = lObjNewRegDdoDAO.checkIfDuplicateEmpForGivenCriteria(lStrEmpName,lDateDOB,lCharGender,lCharFatherOrHusband);
					}
				}
				else
				{
					lBlDuplicateEmp = lObjNewRegDdoDAO.checkIfDuplicateEmpForGivenCriteria(lStrEmpName,lDateDOB,lCharGender,lCharFatherOrHusband);
				}

				if(lBlDuplicateEmp)
				{
					lObjDcpsEmpMst.setDupEmpSentToMDC(2l);
					lObjNewRegDdoDAO.update(lObjDcpsEmpMst);
					lBlFlag = true;
					lLongDCPSEmpId = lObjDcpsEmpMst.getDcpsEmpId();
					continue;
				}

				// Code that will check duplicate employee and send such employees to MDC ends

				if(lObjDcpsEmpMst.getRegStatus() != -1l)
				{
					createWF(objectArgs);
				}

				lObjDcpsEmpMst.setFormStatus(1L);
				lObjDcpsEmpMst.setRegStatus(0L);
				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);
				if (lStrApproveFlag.equals("1")) {
					lObjDcpsEmpMst.setApprovalByDDODate(gDtCurrDt);
				}
				lLongDCPSEmpId = lObjDcpsEmpMst.getDcpsEmpId();

				//createWF(objectArgs);

				if (lStrApproveFlag.equals("1")
						&& lObjDcpsEmpMst.getDcpsOrGpf().equals('N')) {

					lObjDcpsEmpMst.setApprovalByDDODate(gDtCurrDt);
					lObjDcpsEmpMst.setRegStatus(2L);
					lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
					lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
					lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
					lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);

					objectArgs.put("dcpsEmpId", lLongPKValue);

					ResultObject objRes = serv.executeService("createEmployee",
							objectArgs);

					if (objRes.getResultCode() == ErrorConstants.ERROR) {

						throw new Exception();
					}

					Long lLngOrgEmpMstId = Long.parseLong(objectArgs.get(
					"orgEmpMstId").toString());
					lObjDcpsEmpMst.setOrgEmpMstId(lLngOrgEmpMstId);

					// objectArgs.put("EmpIdFromGPF", lLongPKValue);

					// resObj = serv.executeService("SIXPC_ARREARS_VOGEN",
					// objectArgs);

					// resObj = serv.executeService("SIXPC_ARREARS_SRVC",
					// objectArgs);
					String lStrDdoCode = lObjDcpsEmpMst.getDdoCode();

					HstEmp lObjHstEmp = new HstEmp();

					Long lLongHstEmpIdPk = IFMSCommonServiceImpl.getNextSeqNum(
							"hst_dcps_emp_details", objectArgs);

					lObjHstEmp.setHstdcpsId(lLongHstEmpIdPk);

					lObjHstEmp.setDbId(gLngDBId);
					lObjHstEmp.setDcpsEmpId(lLongPKValue);
					lObjHstEmp.setLocId(Long.parseLong(gStrLocationCode));
					lObjHstEmp.setDdoCode(lStrDdoCode);
					lObjHstEmp.setStartDate(gDtCurDate);
					lObjHstEmp.setCreatedUserId(gLngUserId);
					lObjHstEmp.setCreatedPostId(gLngPostId);
					lObjHstEmp.setCreatedDate(gDtCurDate);

					lObjNewRegDdoDAO.create(lObjHstEmp);

					// Archive Form 1 for GPF Employee
					NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(
							MstEmp.class, serv.getSessionFactory());
					lObjNewRegTreasuryDAO.ArchiveNewRegForm(lObjDcpsEmpMst,
							serv);

				} else {
					WorkFlowDelegate.forward(objectArgs);
					lBlFlag = true;
					// Update the Registration form status to 0 suggesting it is
					// in progress

				}
			}

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag, lLongDCPSEmpId,lBlDuplicateEmp).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setResultValue(objectArgs);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/**
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Vihan Khatri
	 * @param objectArgs
	 */

	/**
	 * 
	 * Forwards the request to the treasury upon verification by DDO
	 * 
	 * 
	 * @author Kapil Devani
	 * @param objectArgs
	 * @return
	 */
	public ResultObject forwardRequestToTreasury(Map objectArgs) {


		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String strPKValue = null;
		Boolean lBlFlag = false;
		String lStrDcpsOrGpf = null;
		String ddoCheck= null;
		Long lLongHstEmpIdPk = null;
		String lStrDdoCode = null;
		String lStrDdoCodeCheck="";//added by sunitha 
		String lStrSevarthId ="";
		Boolean lBlBillGroupFlag = false;
		Date lDateDOJ = null;
		String lStrDcpsId=null;
	//	String ppan=null;
		Long lLngEmpId=0l;

		try {

			setSessionInfo(objectArgs);
			String toPost = StringUtility
			.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("DCPS.TREASURY");
			strPKValue = StringUtility.getParameter("Emp_Id", request)
			.toString().trim();

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			Long lLongPKValue = Long.parseLong(strPKValue);
			MstEmp lObjDcpsEmpMst = (MstEmp) lObjNewRegDdoDAO
			.read(lLongPKValue);
			lStrDdoCodeCheck = lObjDcpsEmpMst.getDdoCode();//agri check
			Date doj=lObjDcpsEmpMst.getDoj();
			Long payCommission=Long.parseLong(lObjDcpsEmpMst.getPayCommission());
			gLogger.info(" lStrDdoCodeCheck before logic: " +lStrDdoCodeCheck);
			gLogger.info(" doj before logic: " +doj);
			objectArgs.put("dcpsEmpId", lLongPKValue);

			if (lObjDcpsEmpMst.getDcpsOrGpf().equals('Y') && (!(lStrDdoCodeCheck).equals("2201001211")) ){

				lStrDcpsOrGpf = "DCPS";
				ddoCheck="DDO";
				gLogger.info(" gLngUserId is in dcps : " +gLngUserId);
				gLogger.info(" gLngPostId is in dcps : " +gLngPostId);
				
				objectArgs.put("toPost", toPost);
				objectArgs.put("toPostId", toPost);
				objectArgs.put("toLevel", toLevel);

				objectArgs.put("jobTitle", gObjRsrcBndle
						.getString("DCPS.RegistrationForm"));
				objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
						.getString("DCPS.RegistrationFormID")));

				objectArgs.put("Pkvalue", strPKValue);

				WorkFlowDelegate.forward(objectArgs);

				lObjDcpsEmpMst.setApprovalByDDODate(gDtCurrDt);
				lObjDcpsEmpMst.setRegStatus(0L);
				lObjDcpsEmpMst.setPhyRcvdFormStatus(0l);
				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);

				lStrSevarthId = generateSeevarthIdNewLogic(objectArgs);

				//lObjDcpsEmpMst.setSevarthId(lStrSevarthId);
				lObjNewRegDdoDAO.update(lObjDcpsEmpMst);


			}
			else if(lObjDcpsEmpMst.getDcpsOrGpf().equals('Y') && (lStrDdoCodeCheck).equals("2201001211")) {
				gLogger.info(" in  agri dcps " );
				lStrDcpsOrGpf = "DCPS";
				ddoCheck="AGRIDDO";
				strPKValue = StringUtility.getParameter("Emp_Id", request).toString().trim();
				gLogger.info(" empid is in dcps agri: " +strPKValue);
				NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());
			lLngEmpId = Long.parseLong(strPKValue);
			gLogger.info(" empid is in dcps agri: " +lLngEmpId);
			lStrDcpsId = generateDCPSId(objectArgs, lLngEmpId);
			
			
			gLogger.info(" lStrDcpsId is in dcps agri: " + lStrDcpsId);
			gLogger.info(" doj is in dcps agri: " + doj);
			objectArgs.put("DCPSId", lStrDcpsId);			
			objectArgs.put("dcpsEmpId", lLngEmpId);
			objectArgs.put("DOJ", doj);
			objectArgs.put("payCommission", payCommission);
			//inputMap.put("ddoCode",);
			
			lObjDcpsEmpMst = (MstEmp) lObjNewRegTreasuryDAO.read(lLngEmpId);
			lStrDdoCode = lObjDcpsEmpMst.getDdoCode();
			gLogger.info(" lStrDdoCode is in dcps agri: " + lStrDdoCode);
			objectArgs.put("ddoCode", lStrDdoCode);
			
			lDateDOJ = lObjDcpsEmpMst.getDoj();
			gLogger.info(" lDateDOJ is in dcps agri: " + lDateDOJ);
			 resObj = serv.executeService("createEmployee", objectArgs);

			if (resObj.getResultCode() == ErrorConstants.ERROR) {

				throw new Exception();
			}
			Map inputMapFromPayroll = (Map)resObj.getResultValue();
			
			Long lLngOrgEmpMstId = Long.parseLong(inputMapFromPayroll.get("orgEmpMstId").toString());
			String lStrBillGroupId = "";
			
			if(inputMapFromPayroll.containsKey("billNo"))
			{
				if(inputMapFromPayroll.get("billNo") != null)
				{
					if(!inputMapFromPayroll.get("billNo").toString().equals(""))
					{
						lStrBillGroupId = inputMapFromPayroll.get("billNo").toString();
						lBlBillGroupFlag = true;
					}
				}
			}
		lStrSevarthId = inputMapFromPayroll.get("sevarthId").toString();
		gLogger.info(" lStrSevarthId is in dcps agri: " + lStrSevarthId);
			Long lLongBillGroupId = null;

			if (!lStrBillGroupId.equals("")) {
				lLongBillGroupId = Long.valueOf(lStrBillGroupId);
			}
		
			lObjDcpsEmpMst.setDcpsId(lStrDcpsId);
			lObjDcpsEmpMst.setRegStatus(1L);
			lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
			lObjDcpsEmpMst.setBillGroupId(lLongBillGroupId);
			lObjDcpsEmpMst.setSevarthId(lStrSevarthId);
			lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
			lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
			lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);
			lObjDcpsEmpMst.setOrgEmpMstId(lLngOrgEmpMstId);
			lObjNewRegTreasuryDAO.update(lObjDcpsEmpMst);
			HstEmp lObjHstEmp = new HstEmp();

			lLongHstEmpIdPk = IFMSCommonServiceImpl.getNextSeqNum("hst_dcps_emp_details", objectArgs);

			lObjHstEmp.setHstdcpsId(lLongHstEmpIdPk);

			lObjHstEmp.setDbId(gLngDBId);
			lObjHstEmp.setDcpsEmpId(lLngEmpId);
			lObjHstEmp.setLocId(Long.parseLong(gStrLocationCode));
			lObjHstEmp.setDdoCode(lStrDdoCode);
			//lObjHstEmp.setStartDate(gDtCurDate);
			lObjHstEmp.setStartDate(lDateDOJ);
			lObjHstEmp.setCreatedUserId(gLngUserId);
			lObjHstEmp.setCreatedPostId(gLngPostId);
			lObjHstEmp.setCreatedDate(gDtCurDate);

			lObjNewRegTreasuryDAO.create(lObjHstEmp);
				
			lObjNewRegTreasuryDAO.ArchiveNewRegForm(lObjDcpsEmpMst, serv);
			resObj = serv.executeService("SIXPC_ARREARS_VOGEN", objectArgs);

			resObj = serv.executeService("SIXPC_ARREARS_SRVC", objectArgs);
			}
			if (lObjDcpsEmpMst.getDcpsOrGpf().equals('N')) {

				lStrDcpsOrGpf = "GPF";
				ddoCheck="DDO";
				gLogger.info(" gLngUserId is in gpf : " +gLngUserId);
				gLogger.info(" gLngPostId is in gpf : " +gLngPostId);
				
				lObjDcpsEmpMst.setApprovalByDDODate(gDtCurrDt);
				lObjDcpsEmpMst.setRegStatus(2L);
				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);

				lStrDdoCode = lObjDcpsEmpMst.getDdoCode();

				lDateDOJ = lObjDcpsEmpMst.getDoj();

				objectArgs.put("ddoCode", lStrDdoCode);

				ResultObject objRes = serv.executeService("createEmployee",
						objectArgs);

				Map inputMapFromPayroll = (Map)objRes.getResultValue();

				if (objRes.getResultCode() == ErrorConstants.ERROR) {

					throw new Exception();
				}

				Long lLngOrgEmpMstId = Long.parseLong(inputMapFromPayroll.get(
				"orgEmpMstId").toString());
				lObjDcpsEmpMst.setOrgEmpMstId(lLngOrgEmpMstId);

				String lStrBillGroupId = "";

				if(inputMapFromPayroll.containsKey("billNo"))
				{
					if(inputMapFromPayroll.get("billNo") != null)
					{
						if(!inputMapFromPayroll.get("billNo").toString().equals(""))
						{
							lStrBillGroupId = inputMapFromPayroll.get("billNo").toString();
							lBlBillGroupFlag = true;
						}
					}
				}

				lStrSevarthId = inputMapFromPayroll.get("sevarthId").toString();
				Long lLongBillGroupId = null;

				if (!lStrBillGroupId.equals("")) {
					lLongBillGroupId = Long.valueOf(lStrBillGroupId);
				}

				lObjDcpsEmpMst.setBillGroupId(lLongBillGroupId);
				lObjDcpsEmpMst.setSevarthId(lStrSevarthId);
				lObjNewRegDdoDAO.update(lObjDcpsEmpMst);

				// objectArgs.put("EmpIdFromGPF", lLongPKValue);

				// resObj = serv.executeService("SIXPC_ARREARS_VOGEN",
				// objectArgs);

				// resObj = serv.executeService("SIXPC_ARREARS_SRVC",
				// objectArgs);

				HstEmp lObjHstEmp = new HstEmp();

				lLongHstEmpIdPk = IFMSCommonServiceImpl.getNextSeqNum(
						"hst_dcps_emp_details", objectArgs);

				lObjHstEmp.setHstdcpsId(lLongHstEmpIdPk);

				lObjHstEmp.setDbId(gLngDBId);
				lObjHstEmp.setDcpsEmpId(lLongPKValue);
				lObjHstEmp.setLocId(Long.parseLong(gStrLocationCode));
				lObjHstEmp.setDdoCode(lStrDdoCode);
				//lObjHstEmp.setStartDate(gDtCurDate);
				lObjHstEmp.setStartDate(lDateDOJ);
				lObjHstEmp.setCreatedUserId(gLngUserId);
				lObjHstEmp.setCreatedPostId(gLngPostId);
				lObjHstEmp.setCreatedDate(gDtCurDate);

				lObjNewRegDdoDAO.create(lObjHstEmp);

				// Archive Form 1 for GPF Employee
				NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(
						MstEmp.class, serv.getSessionFactory());
				lObjNewRegTreasuryDAO.ArchiveNewRegForm(lObjDcpsEmpMst, serv);
					
				///for gpf grp d employee
				
				if(lObjDcpsEmpMst.getGroup().equals("D")){
				
					objRes = serv.executeService("saveGPFEmpVOGEN",
							objectArgs);	
					
					objRes = serv.executeService("saveGPFEmpSRVC",
						objectArgs);
				}
				
			}

			lBlFlag = true;

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return resObj;
		}

		String lSBStatus = getResponseXMLDocForDDOFwd(lBlFlag, lStrDcpsOrGpf,lStrSevarthId,lBlBillGroupFlag,ddoCheck,lStrDcpsId,lLngEmpId)
		.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		resObj.setResultValue(objectArgs);

		return resObj;
	}

	/**
	 * 
	 * Service called before the update page is opened.
	 * 
	 * 
	 * 
	 * @author Kapil Devani
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public ResultObject showAndUpdateForm(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);

			// Flag to check if the request has come from Draft list of
			// completed
			// list: 1 For Draft 2 For Completed
			Integer iDraftFlag = Integer.parseInt(StringUtility.getParameter(
					"Draft", request));

			inputMap.put("DraftFlag", iDraftFlag);
			String lStrEmpId = StringUtility.getParameter("Emp_Id", request);

			Long lLngEmpID = Long.parseLong(lStrEmpId);

			MstEmp lObjEmpData = (MstEmp) inputMap.get("DCPSEmpData");

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			lObjEmpData = (MstEmp) lObjNewRegDdoDAO.read(lLngEmpID);
			inputMap.put("lObjEmpData", lObjEmpData);

			String lStrCurrOff = lObjEmpData.getCurrOff();

			List listDesignation = lObjDcpsCommonDAO
			.getDesignations(lStrCurrOff);
			inputMap.put("DESIGNATIONLIST", listDesignation);

			List listRelationship = IFMSCommonServiceImpl
			.getLookupValues("RelationList", SessionHelper
					.getLangId(inputMap), inputMap);

			inputMap.put("RELATIONLIST", listRelationship);

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
			List listOfficeNames = lObjDcpsCommonDAO
			.getCurrentOffices(lStrDdoCode);

			inputMap.put("OFFICELIST", listOfficeNames);
			List listCadres = lObjDcpsCommonDAO.getCadres();

			inputMap.put("CADRELIST", listCadres);

			List listParentDept = lObjDcpsCommonDAO.getAllDepartment(Long
					.parseLong(gObjRsrcBndle.getString("DCPS.DEPARTMENTID")),
					gLngLangId);
			inputMap.put("listParentDept", listParentDept);

			Date lDtcurDate = SessionHelper.getCurDate();
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));
			inputMap.put("lDtJoiDtLimit", "01/01/2005");
			inputMap.put("DDOCODE", "12345");

			resObj.setViewName("DCPSRegistrationForm");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return resObj;
		}

		return resObj;
	}

	public ResultObject rejectRequestDDO(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String strPKValue;

		try {

			setSessionInfo(objectArgs);
			strPKValue = StringUtility.getParameter("Emp_Id", request)
			.toString().trim();

			String lStrRemarks = StringUtility.getParameter("remarks", request)
			.toString().trim();

			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("SendNotification", lStrRemarks);
			objectArgs.put("jobTitle", gObjRsrcBndle
					.getString("DCPS.RegistrationForm"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.RegistrationFormID")));

			objectArgs.put("Pkvalue", strPKValue);
			WorkFlowDelegate.returnDoc(objectArgs);

			// Update the Registration form status to -1 suggesting it is
			// rejected

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO1 = new NewRegDdoDAOImpl(RltDcpsPayrollEmp.class,
					serv.getSessionFactory());

			Long lLongPKValue = Long.parseLong(strPKValue);
			MstEmp lObjDcpsEmpMst = (MstEmp) lObjNewRegDdoDAO
			.read(lLongPKValue);

			// Set the value in Read VO
			lObjDcpsEmpMst.setRegStatus(-1L);
			lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
			lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
			lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
			lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);
			lObjDcpsEmpMst.setSentBackRemarks(lStrRemarks);
			lObjDcpsEmpMst.setFormStatus(0L);

			lObjNewRegDdoDAO.update(lObjDcpsEmpMst);
			
			
			//added by shailesh for deactivating substitute post id
			boolean substituteOrNot = false;			
			OrgPostMstDaoImpl orgPostMstDaoImpl = new OrgPostMstDaoImpl(OrgPostMst.class, serv.getSessionFactory());
			OrgPostMst orgPostMaster= new OrgPostMst();	
			long postId = lObjNewRegDdoDAO1.getPostIdFrmRltDcpsPayEmp(lLongPKValue);
			if(postId != -1 && postId != 0){
				substituteOrNot = lObjNewRegDdoDAO.checkSubstituteForRjctn(postId);
				gLogger.error("substituteOrNot  "+substituteOrNot);
				if (substituteOrNot) {					
					//long postId = lObjNewRegDdoDAO1.getPostIdFrmRltDcppayEmp(lLongPKValue);		
					gLogger.error("postId  "+postId);
					lObjNewRegDdoDAO.updateHrPaySubsEmpMpg(postId);
					orgPostMaster = orgPostMstDaoImpl.read(postId);
					orgPostMaster.setActivateFlag(0);
					orgPostMstDaoImpl.update(orgPostMaster);
					gLogger.error("Long.parseLong(lStrOldPost) "+lLongPKValue);
				}
			}
			//end by shailesh

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(true, Long.parseLong(strPKValue))
		.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		objectArgs.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		resObj.setResultValue(objectArgs);

		return resObj;
	}

	private void createWF(Map inputMap) {

		try {

			Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString());
			setSessionInfo(inputMap);

			String subjectName = gObjRsrcBndle
			.getString("DCPS.RegistrationForm");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper
			.getHierarchyByPostIDAndDescription(lStrPostId,
					subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.RegistrationFormID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle
					.getString("DCPS.RegistrationForm"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
		}

	}

	public Integer calculateAge(Date dob) {

		SimpleDateFormat simpleDateFormatObj = new SimpleDateFormat("yyyy");
		Integer age;
		Integer birthYear = Integer.parseInt(simpleDateFormatObj.format(dob));
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		String currYearStr = simpleDateFormatObj.format(gDtCurrDt);
		Integer currYear = Integer.parseInt(currYearStr);
		age = currYear - birthYear;
		return age;
	}

	/*
	 * Method to used to generate the XML Response
	 */

	private StringBuilder getResponseXMLDocForGroup(String lStrGroup,
			Long lLongSuperAnnAge, String lStrRetiringYear) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<txtGroup>");
		lStrBldXML.append(lStrGroup.trim());
		lStrBldXML.append("</txtGroup>");
		lStrBldXML.append("<txtSuperAnnAge>");
		lStrBldXML.append(lLongSuperAnnAge);
		lStrBldXML.append("</txtSuperAnnAge>");
		lStrBldXML.append("<lStrRetiringYear>");
		lStrBldXML.append(lStrRetiringYear.trim());
		lStrBldXML.append("</lStrRetiringYear>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForOffice(List finalList) {

		StringBuilder lStrBldXML = new StringBuilder();

		Object obj[] = (Object[]) finalList.get(0);

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<txtAddress1>");
		if (obj[0] != null) {
			lStrBldXML.append(obj[0].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</txtAddress1>");
		lStrBldXML.append("<txtContact1>");
		if (obj[1] != null) {
			lStrBldXML.append(obj[1].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</txtContact1>");
		lStrBldXML.append("<txtContact2>");
		if (obj[2] != null) {
			lStrBldXML.append(obj[2].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</txtContact2>");
		lStrBldXML.append("<txtContact3>");
		if (obj[3] != null) {
			lStrBldXML.append(obj[3].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</txtContact3>");
		lStrBldXML.append("<email>");
		if (obj[4] != null) {
			lStrBldXML.append(obj[4].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</email>");
		lStrBldXML.append("<txtAddress2>");
		if (obj[5] != null) {
			lStrBldXML.append(obj[5].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</txtAddress2>");
		lStrBldXML.append("<txtOfficeCityClass>");
		if (obj[6] != null) {
			lStrBldXML.append(obj[6].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</txtOfficeCityClass>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForNominee(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag, Long empID) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<EMPID>");
		lStrBldXML.append(empID);
		lStrBldXML.append("</EMPID>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag, Long empID,Boolean dupEmpFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("<EMPID>");
		lStrBldXML.append(empID);
		lStrBldXML.append("</EMPID>");

		lStrBldXML.append("<dupEmpFlag>");
		lStrBldXML.append(dupEmpFlag);
		lStrBldXML.append("</dupEmpFlag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocToMapDDOAsst(Boolean flag,String lStrUserName,String lStrPassword) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("<UserName>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lStrUserName.trim());
		lStrBldXML.append("]]>");
		lStrBldXML.append("</UserName>");

		lStrBldXML.append("<Password>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lStrPassword.trim());
		lStrBldXML.append("]]>");
		lStrBldXML.append("</Password>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForDDOFwd(Boolean flag,
			String dcpsOrGpf,String lStrSevarthId,Boolean lBlBillGroupFlag,String ddoCheck,String lStrDcpsId,Long lLngEmpId) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<DcpsOrGPF>");
		lStrBldXML.append(dcpsOrGpf.trim());
		lStrBldXML.append("</DcpsOrGPF>");
		lStrBldXML.append("<SevarthId>");
		lStrBldXML.append(lStrSevarthId.trim());
		lStrBldXML.append("</SevarthId>");
		lStrBldXML.append("<BillGroupFlag>");
		lStrBldXML.append(lBlBillGroupFlag);
		lStrBldXML.append("</BillGroupFlag>");
		lStrBldXML.append("<ddoCheck>");
		lStrBldXML.append(ddoCheck);
		lStrBldXML.append("</ddoCheck>");
		lStrBldXML.append("<lStrDcpsId>");
		lStrBldXML.append(lStrDcpsId);
		lStrBldXML.append("</lStrDcpsId>");
		lStrBldXML.append("<lLngEmpId>");
		lStrBldXML.append(lLngEmpId);
		lStrBldXML.append("</lLngEmpId>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public static Integer daysBetween(Date sPassedDate, Date ePassedDate) {

		Calendar sDate = Calendar.getInstance();
		sDate.setTime(sPassedDate);
		Calendar eDate = Calendar.getInstance();
		eDate.setTime(ePassedDate);

		Calendar d = (Calendar) sDate.clone();
		Integer dBetween = 0;
		while (d.before(eDate)) {
			d.add(Calendar.DAY_OF_MONTH, 1);
			dBetween++;
		}
		dBetween = dBetween - 1;
		return dBetween;
	}

	public ResultObject getDesigsForPFDAndCadre(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS,
		"FAIL");
		Long lLngParentDept = null;
		try {

			/* Sets the Session Information */
			setSessionInfo(lMapInputMap);

			/* Initializes the DAO */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			if (!StringUtility.getParameter("listParentDept", request)
					.equalsIgnoreCase("")) {
				lLngParentDept = Long.parseLong(StringUtility.getParameter(
						"listParentDept", request));
			}
			if (!StringUtility.getParameter("cmbCadre", request)
					.equalsIgnoreCase("")) {
				Long.parseLong(StringUtility.getParameter("cmbCadre", request));
			}

			/*
			 * Gets the branch names from the bank name and sends them using
			 * AJAX
			 */
			List lLstDesignation = lObjDcpsCommonDAO
			.getDesigsForPFDAndCadre(lLngParentDept);

			String lStrTempResult = null;
			if (lLstDesignation != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lLstDesignation,
						"desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}
/***************Need to be edited  *******************/
	public ResultObject getLookupValuesForParentAG(
			Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS,
		"FAIL");
		Long lLngParentLookupId = null;
		String lStrTypeOfAG = null;
		try {

			/* Sets the Session Information */
			setSessionInfo(lMapInputMap);

			/* Initializes the DAO */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			AGGPFSeriesDAO lObjAGGPFSeriesDAO=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());

			if (!StringUtility.getParameter("typeOfAG", request)
					.equalsIgnoreCase("")) {
				lStrTypeOfAG = StringUtility.getParameter("typeOfAG", request);
			}
			/*	
			
			if (lStrTypeOfAG.equals("700092")) {
				lLngParentLookupId = 700098l;
			}

			if (lStrTypeOfAG.equals("700093")) {
				lLngParentLookupId = 700181l;
			}
			
			
			

			
			 * Gets the branch names from the bank name and sends them using
			 * AJAX
			 */
				
				List lLstLookupValues=null;
				
			
			  lLstLookupValues = lObjDcpsCommonDAO
			.getLookupValuesForParent(lObjAGGPFSeriesDAO.getPFPrntLkpIdFromAGLkpId(lStrTypeOfAG));

			String lStrTempResult = null;
			if (lLstLookupValues != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(
						lLstLookupValues, "desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}

	public ResultObject loadFormListForDDO(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			getHierarchyUsers(inputMap);
			lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);

			List AllFormsList = lObjNewRegDdoDAO.getFormListForDDO(lStrDdoCode);
			inputMap.put("AllFormsList", AllFormsList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("FormListForDDO");

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	public ResultObject getFirstDesignationForAutoComplete(
			Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String strFirstDesig = null;
		// Get the group name for the cadre name selected
		try {

			setSessionInfo(inputMap);

			strFirstDesig = StringUtility.getParameter("searchKey", request)
			.trim();
			//System.out.println(strFirstDesig);
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());

			finalList = lObjNewRegDdoDAO
			.getDesigsForAutoComplete(strFirstDesig);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return objRes;
		}

		return objRes;

	}

	public ResultObject popOfficesForPost(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS,
		"FAIL");

		try {

			/* Sets the Session Information */
			setSessionInfo(lMapInputMap);

			/* Initializes the DAO */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			/* Gets the bank name from request */
			Long lLngPostId = Long.parseLong(StringUtility.getParameter(
					"cmbCurrentPost", request));

			/*
			 * Gets the branch names from the bank name and sends them using
			 * AJAX
			 */
			List lListCadres = lObjDcpsCommonDAO.getOfficesForPost(lLngPostId);

			String lStrTempResult = null;
			if (lListCadres != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lListCadres,
						"desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}

	public ResultObject deleteNewRegDraft(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlSuccessFlag = null;
		MstEmp lObjMstEmp = null;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv
					.getSessionFactory());
			String lStrDcpsEmpIds = StringUtility.getParameter("draftDcpsEmpIds", request).trim();

			String[] lStrArrDcpsEmpIds = lStrDcpsEmpIds.split("~");
			Long[] lLongArrDcpsEmpIds = new Long[lStrArrDcpsEmpIds.length];

			for(Integer lInt=0;lInt<lStrArrDcpsEmpIds.length;lInt++)
			{
				if(lStrArrDcpsEmpIds[lInt] != null && !"".equals(lStrArrDcpsEmpIds[lInt]))
				{
					lLongArrDcpsEmpIds[lInt] = Long.valueOf(lStrArrDcpsEmpIds[lInt]);
				}
			}

			for(Integer lInt=0;lInt<lStrArrDcpsEmpIds.length;lInt++)
			{
				lObjMstEmp = (MstEmp) lObjNewRegDdoDAO.read(lLongArrDcpsEmpIds[lInt]);
				lObjNewRegDdoDAO.deleteNomineesForGivenEmployee(lLongArrDcpsEmpIds[lInt]);
				lObjNewRegDdoDAO.deleteRltPayrollEmpForGivenEmployee(lLongArrDcpsEmpIds[lInt]);
				lObjNewRegDdoDAO.delete(lObjMstEmp);
			}

			lBlSuccessFlag = true ;
			String lSBStatus = getResponseXMLDoc(lBlSuccessFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	public ResultObject loadMapDDOAsst(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		String lStrRequestForSearch = null;
		List EmpList = null;
		String lStrSevaarthId = null;
		String lStrName = null;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);

			lStrRequestForSearch = StringUtility.getParameter("requestForSearch", request).trim();
			lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim();
			lStrName = StringUtility.getParameter("txtEmployeeName", request).trim();

			if(lStrRequestForSearch.equals("Yes"))
			{
				EmpList = lObjNewRegDdoDAO.getAllApprovedEmpsUnderDDO(lStrDdoCode,lStrSevaarthId,lStrName);
			}

			inputMap.put("EmpList", EmpList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("MapDDOAsst");

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}

		return resObj;

	}

	public ResultObject MapDDOAsst(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		Boolean lBlCheckLocationInWFFlag = false;
		String lStrDcpsEmpId = null;
		String lStrOrgEmpId = null;
		String lStrRequestFor = null;
		String lStrDcpsEmpIds = null;
		String lStrOrgEmpIds = null;
		Long lLongDcpsEmpId = null;
		Long lLongOrgEmpMstId = null;
		Long lLongPostId = null;
		Long lLongUserId = null;
		Long lLongDDOPostId = null;
		Long lLongRltDDOAsstId = null;
		Long lLongAclPostRoleId = null;
		RltDdoAsst lObjRltDdoAsst = null;
		AclPostroleRlt lObjAclPostroleRlt = null;
		WfHierachyPostMpg lObjWfHierachyPostMpg = null;
		AclRoleMst lObjAclRoleMst = null;
		OrgPostMst lObjOrgPostMst = null;
		OrgPostMst lObjCreatedByOrgPostMst = null;
		OrgUserMst lObjCreatedByOrgUserMst = null;
		Long lLongRoleIdOfDDOAsst = 700001l;  // Fixed. Never change.

		Long lLongWFOrgPostMpgMst = null;
		Long lLongWFOrgUserMpgMst = null;
		Long lLongHierarchyRefId = null;
		Long lLongHierarchySeqId = null;
		Long lLongCreatedByUserId = null;

		String lStrUserName = "";
		String lStrPassword = "";

		try {
			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			lStrRequestFor = StringUtility.getParameter("requestFor", request).trim();
			lStrDcpsEmpIds = StringUtility.getParameter("dcpsEmpIds", request).toString().trim();
			lStrOrgEmpIds = StringUtility.getParameter("orgEmpMstIds", request).toString().trim();
			String[] lArrStrDcpsEmpIds = lStrDcpsEmpIds.split("~");
			String[] lArrStrOrgEmpIds = lStrOrgEmpIds.split("~");

			if(lStrRequestFor.equals("Assign"))
			{
				// Assigns the role of DDO Asst to the employees selected
				for(Integer lInt = 0; lInt<lArrStrDcpsEmpIds.length ; lInt++)
				{
					if(lArrStrDcpsEmpIds[lInt] != null && !(lArrStrDcpsEmpIds[lInt].equals("")) && lArrStrOrgEmpIds[lInt] != null && !(lArrStrOrgEmpIds[lInt].equals("")))
					{
						lLongDcpsEmpId = Long.valueOf(lArrStrDcpsEmpIds[lInt].trim());
						lLongOrgEmpMstId = Long.valueOf(lArrStrOrgEmpIds[lInt].trim());

						lLongPostId = lObjNewRegDdoDAO.getPostForEmpId(lLongOrgEmpMstId);
						lLongUserId = lObjNewRegDdoDAO.getUserIdForEmpId(lLongOrgEmpMstId);
						lLongDDOPostId = gLngPostId;
						lLongCreatedByUserId = lObjNewRegDdoDAO.getUserIdForPostId(gLngPostId);

						lObjNewRegDdoDAO.unlockAccountForOrgEmpId(lLongOrgEmpMstId);

						Object[] lObjUserNameAndPwd = lObjNewRegDdoDAO.getUserNameAndPwdForEmpId(lLongOrgEmpMstId);

						if (lObjUserNameAndPwd[0] != null) {
							lStrUserName = lObjUserNameAndPwd[0].toString().trim();
						}
						if (lObjUserNameAndPwd[1] != null) {
							lStrPassword = lObjUserNameAndPwd[1].toString().trim();
						}

						// creates entry in rlt_dcps_ddo_asst table if not there between that DDO and Assistant 
						if(!(lObjNewRegDdoDAO.checkEntryInRltDDOAsstTable(lLongPostId,lLongDDOPostId)))
						{
							lObjRltDdoAsst = new RltDdoAsst();
							lObjRltDdoAsst.setAsstPostId(lLongPostId);
							lObjRltDdoAsst.setDdoPostId(lLongDDOPostId);

							lLongRltDDOAsstId = IFMSCommonServiceImpl.getNextSeqNum("rlt_dcps_ddo_asst",inputMap);
							lObjRltDdoAsst.setRltDdoAsstId(lLongRltDDOAsstId);
							lObjNewRegDdoDAO.create(lObjRltDdoAsst);
						}

						// creates entry in ACL_POSTROLE_RLT table to map role of Assistant to that DDO
						if(!(lObjNewRegDdoDAO.checkEntryInAclPostRoleTable(lLongPostId)))
						{

							/*
							lObjAclRoleMst = lObjNewRegDdoDAO.getRoleVOForRoleId(lLongRoleIdOfDDOAsst);
							lObjOrgPostMst = lObjNewRegDdoDAO.getPostVOForPostId(lLongPostId);
							lObjCreatedByOrgPostMst = lObjNewRegDdoDAO.getPostVOForPostId(lLongDDOPostId);
							lObjCreatedByOrgUserMst = lObjNewRegDdoDAO.getUserVOForUserId(lLongCreatedByUserId);

							lObjAclPostroleRlt = new AclPostroleRlt();
							lObjAclPostroleRlt.setAclRoleMst(lObjAclRoleMst);
							lObjAclPostroleRlt.setOrgPostMst(lObjOrgPostMst);
							lObjAclPostroleRlt.setOrgPostMstByCreatedByPost(lObjCreatedByOrgPostMst);
							lObjAclPostroleRlt.setStartDate(gDtCurDate);
							lObjAclPostroleRlt.setPostRoleId(lLongAclPostRoleId);
							lObjAclPostroleRlt.setOrgUserMstByCreatedBy(lObjCreatedByOrgUserMst);
							//lObjAclPostroleRlt.setCmnLookupMstByActivate(arg0);
							lObjNewRegDdoDAO.create(lObjAclPostroleRlt);
							 */

							lLongAclPostRoleId = IFMSCommonServiceImpl.getNextSeqNum("acl_postrole_rlt",inputMap);
							lObjNewRegDdoDAO.insertAclPostRoleRlt(lLongAclPostRoleId,lLongRoleIdOfDDOAsst,lLongPostId,lLongDDOPostId,gDtCurDate);

						}

						// creates entry in WF_ORG_POST_MPG_MST table

						if(!(lObjNewRegDdoDAO.checkEntryInWFOrgPostMpgMst(lLongPostId)))
						{
							lLongWFOrgPostMpgMst = IFMSCommonServiceImpl.getNextSeqNum("wf_org_post_mpg_mst",inputMap);
							lObjNewRegDdoDAO.insertWFOrgPostMpg(lLongPostId);
						}

						if(!(lObjNewRegDdoDAO.checkEntryInWFOrgUserMpgMst(lLongUserId)))
						{
							lLongWFOrgUserMpgMst = IFMSCommonServiceImpl.getNextSeqNum("wf_org_usr_mpg_mst",inputMap);
							lObjNewRegDdoDAO.insertWFOrgUsrMpg(lLongUserId);
						}

						// creates entries in WF_HIERARCHY_REFERENCE_MST table

						List lListHierarchyRefIds = lObjNewRegDdoDAO.getAllHierarchyRefIdsForLocation(Long.valueOf(gStrLocationCode));
						for(Integer k = 0; k < lListHierarchyRefIds.size() ; k++ )
						{
							lLongHierarchyRefId = Long.valueOf(lListHierarchyRefIds.get(k).toString());
							if(!(lObjNewRegDdoDAO.checkEntryInWfHierachyPostMpg(lLongHierarchyRefId,lLongPostId)))
							{
								lLongHierarchySeqId = IFMSCommonServiceImpl.getNextSeqNum("wf_hierachy_post_mpg",inputMap);
								lObjNewRegDdoDAO.insertWfHierachyPostMpg(lLongHierarchySeqId,lLongHierarchyRefId,lLongPostId,lLongCreatedByUserId,gDtCurDate,Long.valueOf(gStrLocationCode));
							}
							lBlCheckLocationInWFFlag = true;
						}

						lObjNewRegDdoDAO.updateDDOAsstStatusInMstEmp(lLongDcpsEmpId,lStrRequestFor);

					}
				}

				if(lBlCheckLocationInWFFlag)
				{
					lBlFlag = true;
				}

			}

			if(lStrRequestFor.equals("DeAssign"))
			{
				// De-assigns the role of DDO Asst to the employees selected and locks the account of those employees
				for(Integer lInt = 0; lInt<lArrStrDcpsEmpIds.length ; lInt++)
				{
					if(lArrStrDcpsEmpIds[lInt] != null && !(lArrStrDcpsEmpIds[lInt].equals("")) && lArrStrOrgEmpIds[lInt] != null && !(lArrStrOrgEmpIds[lInt].equals("")))
					{
						lLongDcpsEmpId = Long.valueOf(lArrStrDcpsEmpIds[lInt]);
						lLongOrgEmpMstId = Long.valueOf(lArrStrOrgEmpIds[lInt]);
						lObjNewRegDdoDAO.lockAccountForOrgEmpId(lLongOrgEmpMstId);
						lObjNewRegDdoDAO.updateDDOAsstStatusInMstEmp(lLongDcpsEmpId,lStrRequestFor);

						lLongPostId = lObjNewRegDdoDAO.getPostForEmpId(lLongOrgEmpMstId);
						lLongDDOPostId = gLngPostId;
						lObjNewRegDdoDAO.deleteRltDdoAsstEntryWhileDeAssign(lLongPostId, lLongDDOPostId);
					}
				}

				lBlFlag = true;
			}

			IFMSCommonServiceImpl lObjIFMSCommonServiceImpl = new IFMSCommonServiceImpl();
			lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactory());
			lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactorySlave());

			//Commented as perhaps was wrong
			/*
		if(lBlFlag)
		{
			lObjNewRegDdoDAO.updateDDOAsstStatusInMstEmp(lLongDcpsEmpId,lStrRequestFor);
		}
			 */

		} catch (Exception ex) {
			resObj.setResultValue(null);
			gLogger.error(" Error is : " + ex, ex);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}

		String lSBStatus = getResponseXMLDocToMapDDOAsst(lBlFlag,lStrUserName,lStrPassword).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public String generateSeevarthIdNewLogic(Map inputMap) throws Exception
	{
		String lStrSevarthEmpCode = "";
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstEmp lObjEmpData = null;

		try {

			ServiceLocator serv  =(ServiceLocator) inputMap.get("serviceLocator");
			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());
			OtherDetailDAOImpl otherDtlsDao = new OtherDetailDAOImpl(HrEisOtherDtls.class,serv.getSessionFactory());
			CmnLocationMst cmnLocationMst = null;

			Long lLngEmpId = Long.parseLong(String.valueOf(inputMap.get("dcpsEmpId")));
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			lObjEmpData = new MstEmp();
			lObjEmpData = (MstEmp) lObjNewRegTreasuryDAO.read(lLngEmpId);

			String lStrEmpFname = lObjEmpData.getName();
			Character lCharGender = lObjEmpData.getGender();

			Date lDateBirthDate = lObjEmpData.getDob();
			String lStrBirthDate = lObjDateFormat.format(lDateBirthDate);

			lStrEmpFname = lStrEmpFname.replace('.', ' ');
			lStrEmpFname = lStrEmpFname.replaceAll("\\s+", " ");

			String Names[] = lStrEmpFname.split(" ");
			String Fname = null;
			String Mname = null;
			String Lname = null;

			if (Names.length >= 3) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				Lname = Names[2].substring(0, 1);

			}

			else if (Names.length == 2) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				//Lname = ".";

				Lname = Names[1].substring(1, 2);

			}

			else if (Names.length == 1) {
				Fname = Names[0].substring(0, 1);
				//Mname = ".";
				//Lname = ".";

				Mname = Names[0].substring(1,2);
				Lname = Names[0].substring(2,3);
			}

			cmnLocationMst = otherDtlsDao.getCmnLocationMst(lObjEmpData.getDdoCode());
			String lStrOfficeName = cmnLocationMst.getLocShortName().substring(0,3).toUpperCase();

			String lStrTempSevarthCode=lStrOfficeName+Fname+Mname+Lname+lCharGender.toString()+lStrBirthDate.substring(8, 10);

			long curentIdCount=otherDtlsDao.getCount(lStrTempSevarthCode);
			long tempIdCount=curentIdCount+01;
			String tempCountVar=String.format("%2s", tempIdCount).replace(' ', '0');

			if(curentIdCount==0)
			{
				lStrSevarthEmpCode=lStrSevarthEmpCode+"01";
			}
			lStrSevarthEmpCode=lStrTempSevarthCode+tempCountVar;

			gLogger.info("end of  generate seevarth id method with sevaarth Id value:"+lStrSevarthEmpCode);

		}catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
		}


		return lStrSevarthEmpCode;
	}

	public ResultObject checkIfNameExists(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());

			String lStrName = StringUtility.getParameter("txtName",request).trim();

			if (!"".equals(lStrName)) {

				lBlFlag = lObjNewRegDdoDAO.checkIfNameExists(lStrName);
			}

			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	public ResultObject checkIfNameAndDOBExists(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date lDateDOB = null;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());

			String lStrName = StringUtility.getParameter("txtName",request).trim();
			String lStrDOB = StringUtility.getParameter("dob",request).trim();

			if(!"".equals(lStrDOB))
			{
				lDateDOB = lObjSimpleDateFormat.parse(lStrDOB);
			}

			if (!"".equals(lStrName) && !"".equals(lStrDOB) ) {

				lBlFlag = lObjNewRegDdoDAO.checkIfNameAndDOBExists(lStrName,lDateDOB);
			}

			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	public ResultObject loadApprRjctDupEmp(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		Integer lIntTotalDupEmps = 0;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv.getSessionFactory());

			List lListDupEmps = lObjNewRegDdoDAO.getAllDupEmpsForMDC();
			inputMap.put("lListDupEmps", lListDupEmps);

			if(lListDupEmps != null)
			{
				lIntTotalDupEmps = lListDupEmps.size();
			}

			inputMap.put("totalDupEmps", lIntTotalDupEmps);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DupEmpListForMDC");

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	public ResultObject apprRjctDupEmp(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);

		Boolean lStrflag;

		try {
			setSessionInfo(inputMap);
			lStrflag = false;
			MstEmp lObjDcpsEmpMst = null;
			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());

			String lStrDcpsEmpIds = StringUtility.getParameter("Emp_Id", request).trim();
			String[] lArrDcpsEmpIds = lStrDcpsEmpIds.split("~");

			String lStrApproveOrRejectFlag = StringUtility.getParameter("approveOrRejectFlag", request).trim();
			Long lLongApproveOrRejectFlag = Long.valueOf(lStrApproveOrRejectFlag);

			String lStrRejectionRemarks = StringUtility.getParameter("RejectionRemarks", request).trim();
			String[] lArrRejectionRemarks = lStrRejectionRemarks.split("~");

			for (Integer i = 0; i < lArrDcpsEmpIds.length; i++) {

				lObjDcpsEmpMst = (MstEmp) lObjNewRegTreasuryDAO.read(Long.valueOf(lArrDcpsEmpIds[i].trim()));

				if(lLongApproveOrRejectFlag ==  1)
				{
					lObjDcpsEmpMst.setDupEmpSentToMDC(1l);
				}
				if(lLongApproveOrRejectFlag ==  2)
				{
					lObjDcpsEmpMst.setSentBackRemarks(lArrRejectionRemarks[i].trim());
					lObjDcpsEmpMst.setDupEmpSentToMDC(-1l);
				}
				lObjNewRegTreasuryDAO.update(lObjDcpsEmpMst);

				lStrflag = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error in Approving or Rejecting Duplicate Emp is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			return objRes;
		}

		String lSBStatus = getResponseXMLDoc(lStrflag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	public ResultObject loadAdminDeptWiseForm1Count(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstAdminDepartments = null;

		try{
			DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(CmnLocationMst.class,serv.getSessionFactory());

			lLstAdminDepartments =  lObjCommomDao.getAllAdminDeptsForReportIncludingAllDepts();

			inputMap.put("AllAdminDepts", lLstAdminDepartments);

			resObj.setResultValue(inputMap);
			resObj.setViewName("AdminDeptWiseFormStatus");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadAdminDeptWiseForm1Count");
		}
		return resObj;
	}

	//added by shailesh:start
	public ResultObject getEmployeesForDesig(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS,
		"FAIL");
		Long lLngDesgnCode = null;
		try {

			/* Sets the Session Information */
			setSessionInfo(lMapInputMap);

			/* Initializes the DAO */
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv.getSessionFactory());

			if (!StringUtility.getParameter("cmbDesignation", request)
					.equalsIgnoreCase("")) {
				lLngDesgnCode = Long.parseLong(StringUtility.getParameter(
						"cmbDesignation", request));
			}
			
			Map loginDetailsMap = (Map) lMapInputMap.get("baseLoginMap");
			long loc_id = Long.parseLong(loginDetailsMap.get("locationId")
					.toString());
			
			long size = lObjNewRegDdoDAO.getVacantPostLstSize(lLngDesgnCode, loc_id);
			gLogger.info("size..."+size);
			if(size > 0){
				StringBuilder lStrBldXML = new StringBuilder();
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<genMsg>");
				lStrBldXML.append("<![CDATA[");
				lStrBldXML.append(size);
				lStrBldXML.append("]]>");					
				lStrBldXML.append("</genMsg>");
				lStrBldXML.append("</XMLDOC>");
				String lStrTempResult = null;				
				gLogger.info("postId "+size);
				lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
				lMapInputMap.put("ajaxKey", lStrTempResult);
				gLogger.info("post id generated.."+lStrTempResult);
				lObjResultObj.setResultCode(ErrorConstants.SUCCESS);
				lObjResultObj.setResultValue(lMapInputMap);
				lObjResultObj.setViewName("ajaxData");
				return lObjResultObj;
			}
			
			System.out.println("in getemployee....");
			System.out.println("lLngDesgnCode."+lLngDesgnCode);
			System.out.println("gStrLocationCode."+gStrLocationCode);
			/*
			 * Gets the branch names from the bank name and sends them using
			 * AJAX
			 */
			List<ComboValuesVO>  lLstDesignation =  new ArrayList<ComboValuesVO>();
			lLstDesignation = lObjNewRegDdoDAO.getEmployeesForDesg(lLngDesgnCode, Long.parseLong(gStrLocationCode));
			System.out.println("lLstDesignation."+lLstDesignation.size());
			String lStrTempResult = "";
			if (lLstDesignation != null) {
				lStrTempResult = (new AjaxXmlBuilder().addItems(lLstDesignation,"desc", "id")).toString();

			}

			System.out.println("in lStrTempResult...."+lStrTempResult);
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setResultCode(ErrorConstants.SUCCESS);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}

	public ResultObject generatePostId(Map<String, Object> objectArgs) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			ServiceLocator serv = (ServiceLocator) objectArgs
			.get("serviceLocator");
			Map loginMap = (Map) (Map) objectArgs.get("baseLoginMap");
			gLogger.info("===========submitAdminOrgPostDtl=====called=================");

			long locId = StringUtility.convertToLong(
					loginMap.get("locationId").toString()).longValue();
			gLogger.info("===========submitAdminOrgPostDtl=====called================="+locId);
			long userID = Long.parseLong(loginMap.get("userId").toString());
			gLogger.info("===========3================="+userID);
			long setPostId = Long.parseLong(loginMap.get("loggedInPost").toString());
			gLogger.info("===========1================="+userID);

			OrgUserMstDaoImpl orgUserMstDaoImpl = new OrgUserMstDaoImpl(OrgUserMst.class, serv.getSessionFactory());
			gLogger.info("===========2================="+userID);
			OrgUserMst orgUserMstLoggedin = (OrgUserMst) orgUserMstDaoImpl.read(Long.valueOf(userID));
			gLogger.info("===========3================="+userID);
			OrgPostMstDaoImpl orgPostMstDaoImpl = new OrgPostMstDaoImpl(OrgPostMst.class, serv.getSessionFactory());
			gLogger.info("===========4================="+userID);
			OrgPostMst orgPostMstLoggedIn = (OrgPostMst) orgPostMstDaoImpl.read(Long.valueOf(setPostId));
			gLogger.info("===========5================="+userID);

			long officeId=0;
			long employeePostId=0;
			long noOfPost=0;
			long orderId=0;
			String desgnCode="";
			long desgnId=0;
			String startDate="";
			Date todaysDate = null;
			//gLogger.info("===========6================="+userID);
			//startDate = todaysDate.toGMTString();
			gLogger.info("===========7================="+userID);
			String endDate= null;
			// added by khushal
			String tempEndDate="";
			String orderDate="";    
			String substituteType="";
			String remarks="";
			String oldOrderDate="";
			String oldOrderCmb="";
			String newOrderDate="";
			String newOrderId="";
			String tempTypePost="";
			String Permenant="";
			String empName = "";			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");


			gLogger.info("===========8================="+userID);
			request = (HttpServletRequest) objectArgs.get("requestObj");
			gLogger.info("===========9================="+userID);
			officeId = Long.parseLong(StringUtility.getParameter("cmbCurrentOffice", request).toString());
			gLogger.info(StringUtility.getParameter("cmbCurrentOffice", request));
			employeePostId = Long.parseLong(StringUtility.getParameter("cmbEmployeePostId", request));
			gLogger.info(StringUtility.getParameter("employeePostId", request));
			desgnCode = StringUtility.getParameter("desgId", request).toString();
			desgnId=Long.parseLong(desgnCode);
			gLogger.info("StringUtility.getParameter(\"txtSubStartDate\", request).toString() "+StringUtility.getParameter("txtSubStartDate", request));
			gLogger.info("StringUtility.getParameter(\"txtSubStartDate\", request).toString() "+StringUtility.getParameter("txtSubEndDate", request));
			if(StringUtility.getParameter("txtSubStartDate", request) != null && !StringUtility.getParameter("txtSubStartDate", request).equals("")  )
			{
				startDate = StringUtility.getParameter("txtSubStartDate", request).trim();
			}
			if(StringUtility.getParameter("txtSubEndDate", request).toString() != null && !StringUtility.getParameter("txtSubEndDate", request).equals("")  )
			{
				endDate = StringUtility.getParameter("txtSubEndDate", request).trim();
			}

			substituteType = StringUtility.getParameter("cmbSubstitute", request).toString();

			empName = StringUtility.getParameter("empName", request).toString();
			Date dtStartDate = null;
			Date dtEndDate = null;
			dtStartDate = simpleDateFormat.parse(startDate.trim());
			if(endDate != null)
			dtEndDate = simpleDateFormat.parse(endDate.trim());




			gLogger.info("====> Permenant in Service :: "+Permenant);			
			gLogger.info("====> officeCmb in Service :: "+officeId);
			gLogger.info("====> schemecmb :: "+employeePostId);
			gLogger.info("====> noofpost :: "+noOfPost);
			gLogger.info("====> KhushalsorderId :: "+orderId);
			gLogger.info("====> desgnId:: "+desgnId);
			gLogger.info("====> orderDate :: "+orderDate);
			gLogger.info("====> tempTypePost :: "+tempTypePost);
			gLogger.info("====> newOrderCmb :: "+newOrderId);
			gLogger.info("====> newDate :: "+newOrderDate);
			gLogger.info("====> start data :: "+startDate);
			gLogger.info("====> end date :: "+endDate);
			gLogger.info("====> remarks in service:: "+remarks);
			gLogger.info("====> postType :: "+substituteType);
			gLogger.info("====> empName :: "+empName);


			//************************

			CmnLookupMstDAO cmnLookupMstDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());

			CmnLookupMst cmnLookupPostStatus=cmnLookupMstDAO.read(13L);

			OrderHeadPostmpgDAOImpl orderHeadPostMpgDAO = new OrderHeadPostmpgDAOImpl(HrPayOrderHeadPostMpg.class, serv.getSessionFactory());

			CmnLanguageMstDao cmnLanguageMstDao = new CmnLanguageMstDaoImpl(CmnLanguageMst.class, serv.getSessionFactory());

			CmnLocationMstDaoImpl cmnLocationMstDaoImpl = new CmnLocationMstDaoImpl(CmnLocationMst.class, serv.getSessionFactory());

			OrgDesignationMstDao orgDesignationMstDao = new OrgDesignationMstDaoImpl(OrgDesignationMst.class, serv.getSessionFactory());


			PsrPostMpgDAOImpl postPsrMpgDao = new PsrPostMpgDAOImpl(HrPayPsrPostMpg.class, serv.getSessionFactory());

			OrgPostDetailsRltDao orgPostDetailsRltDao = new OrgPostDetailsRltDaoImpl(OrgPostDetailsRlt.class, serv.getSessionFactory());

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			OrgDesignationMst desgnMst = orgDesignationMstDao.read(desgnId);

			HrPaySubstituteEmpMpg hrPaySubstituteEmpMpg = new HrPaySubstituteEmpMpg();

			gLogger.info("designation Name-----"+desgnMst.getDsgnName());

			CmnLanguageMst cmnLangMst=cmnLanguageMstDao.read(1L);

			OrderHeadMpgDAOImpl orderHeadDAO = new OrderHeadMpgDAOImpl(HrPayOrderHeadMpg.class, serv.getSessionFactory());


			long postId=0;
			long postDtlId=0;
			long nextPsr=postPsrMpgDao.getNextPsrNo();
			gLogger.info("Next PSR No---"+nextPsr);
			long psrPostId=0;
			long orderHeadPostId=0;
			HrPayOrderMst hrPayOrderMst=null;
			OrgPostMst subPostMaster=null;
			OrgPostMst empPostDetails=null;
			OrgPostDetailsRlt orgPostDtlRlt=null;
			HrPayPsrPostMpg postPsrMpg=null;
			HrPayPsrPostMpg empPostPsrMpg=null;
			HrPayOrderHeadPostMpg orderHeadPostmpg=null;


			CmnLookupMst cmnLookupMst=null;
			CmnLookupMst permSubLookupMst=null;
			CmnLookupMst tempSubLookupMst=null;		
			//long parentPostId = 10001198152l;
			IdGenerator idGen = new IdGenerator();

			CmnlookupMstDAOImpl  cmnlookupMstDAOImpl=new CmnlookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
			permSubLookupMst = cmnlookupMstDAOImpl.read(10001198152L);  ////10001198152L permanent substitute
			tempSubLookupMst = cmnlookupMstDAOImpl.read(10001198161L);  ////10001198161L temporary substitute
			empPostDetails = (OrgPostMst)lObjNewRegDdoDAO.getPostDetails(employeePostId).get(0);
			gLogger.info("postId in org_post_mst ::::: "+employeePostId);
			gLogger.info("postId in org_post_mst ::::: "+empPostDetails.getEndDate());
			CmnLocationMst cmnLocMst=cmnLocationMstDaoImpl.read(locId);

			subPostMaster= new OrgPostMst();
			postId=idGen.PKGeneratorWODBLOC("org_post_mst",objectArgs);

			gLogger.info("postId in org_post_mst ::::: "+postId);

			subPostMaster.setPostId(postId);
			subPostMaster.setParentPostId(employeePostId); 
			subPostMaster.setPostLevelId(1);
			subPostMaster.setStartDate(new Date());
			
			subPostMaster.setCmnLookupMst(cmnLookupPostStatus);
			subPostMaster.setDsgnCode(desgnCode);
			subPostMaster.setActivateFlag(1);
			subPostMaster.setCreatedDate(new Date());
			subPostMaster.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
			subPostMaster.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
			subPostMaster.setLocationCode(String.valueOf(locId));

			if(empPostDetails.getEndDate() != null){
				subPostMaster.setEndDate(empPostDetails.getEndDate());
				subPostMaster.setPostTypeLookupId(tempSubLookupMst);
			}
			else subPostMaster.setPostTypeLookupId(permSubLookupMst);
			

			orgPostMstDaoImpl.create(subPostMaster);
			
			//updating parent_post_id
			empPostDetails.setParentPostId(empPostDetails.getPostId());
			orgPostMstDaoImpl.update(empPostDetails);
			
			//#########################
			postPsrMpg=new HrPayPsrPostMpg();
			empPostPsrMpg = (HrPayPsrPostMpg)lObjNewRegDdoDAO.getPostPsrDetails(employeePostId).get(0);

			psrPostId=idGen.PKGeneratorWODBLOC("HR_PAY_POST_PSR_MPG",objectArgs);
			postPsrMpg.setPsrPostId(psrPostId);
			gLogger.info("psrPostId:::::::::::: " +postPsrMpg.getPsrPostId());
			postPsrMpg.setPsrId(nextPsr);
			gLogger.info("nextPsr:::::::::::: " +postPsrMpg.getPsrId());
			postPsrMpg.setPostId(postId);
			gLogger.info("postId:::::::::::: " +postPsrMpg.getPostId());
			postPsrMpg.setBillNo(empPostPsrMpg.getBillNo());
			postPsrMpg.setLoc_id(locId);
			postPsrMpgDao.create(postPsrMpg);
			String postName= null;
			//##########################nextPsr
			/*String name[] = empName.split(" ");

				if(name.length == 3){
					postName = empName+name[0].charAt(0)+". "+name[1].charAt(0)+". "+name[2];
				}
				else if(name.length == 2)
					postName = empName+name[0].charAt(0)+". "+name[1];
				else if(name.length == 1)
					postName = empName+name[0];
				else postName = " ";*/
			////to get short name of substitute post
			cmnLookupMst = cmnlookupMstDAOImpl.read(Long.parseLong(substituteType));  
			postName = cmnLookupMst.getLookupShortName()+" of "+empName+" "+postId;
			gLogger.info("postName "+postName);
			orgPostDtlRlt= new OrgPostDetailsRlt();
			postDtlId=idGen.PKGeneratorWODBLOC("org_post_details_rlt",objectArgs);
			gLogger.info("PK For org_post_details_rlt******"+postDtlId);
			orgPostDtlRlt.setPostDetailId(postDtlId);
			orgPostDtlRlt.setOrgPostMst(subPostMaster);
			orgPostDtlRlt.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
			orgPostDtlRlt.setOrgUserMstByCreatedBy(orgUserMstLoggedin);

			orgPostDtlRlt.setCmnLanguageMst(cmnLangMst);
			orgPostDtlRlt.setOrgDesignationMst(desgnMst);
			orgPostDtlRlt.setCreatedDate(new Date());
			orgPostDtlRlt.setPostName(postName);
			orgPostDtlRlt.setPostShortName(postName);

			orgPostDtlRlt.setCmnLocationMst(cmnLocMst);


			orgPostDetailsRltDao.create(orgPostDtlRlt);

			gLogger.info("PostDetailsRLT DAO Created");

			//##################

			/*orderHeadPostmpg=new HrPayOrderHeadPostMpg();
				orderHeadPostId=idGen.PKGeneratorWODBLOC("HR_PAY_ORDER_HEAD_POST_MPG",objectArgs);
				orderHeadPostmpg.setOrderHeadPostId(orderHeadPostId);
				orderHeadPostmpg.setOrderHeadId(orderHeadId);
				orderHeadPostmpg.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
				orderHeadPostmpg.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
				orderHeadPostmpg.setPostId(postId);
				orderHeadPostmpg.setCreatedDate(new Date());

				orderHeadPostMpgDAO.create(orderHeadPostmpg);*/

			gLogger.info("orderHeadPostmpg DAO Created");


			HrPayOfficepostMpg hrOfficePostMpg = new HrPayOfficepostMpg();

			HrPayOfficePostMpgDAOImpl officePostMpgDAOImpl = new HrPayOfficePostMpgDAOImpl(HrPayOfficepostMpg.class, serv.getSessionFactory());

			DdoOffice ddoOffice=new DdoOffice();
			DdoOfficeDAOImpl ddoOfficeDAOImpl = new DdoOfficeDAOImpl(DdoOffice.class,serv.getSessionFactory());
			ddoOffice =(DdoOffice) ddoOfficeDAOImpl.read(officeId);



			long officePostId = IDGenerateDelegate.getNextId("HR_PAY_OFFICEPOST_MPG",objectArgs);
			gLogger.info("generated officePostId is ===>"+officePostId);
			gLogger.info("post id*****" +subPostMaster.getPostId() );
			gLogger.info("office id********* " + ddoOffice.getDcpsDdoOfficeIdPk());

			hrOfficePostMpg.setOfficePostId(officePostId);
			hrOfficePostMpg.setDdoOffice(ddoOffice);
			hrOfficePostMpg.setOrgPostMstByPostId(subPostMaster);

			hrOfficePostMpg.setStartDate(new Date());
			hrOfficePostMpg.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
			hrOfficePostMpg.setCreatedDate(new Date());
			hrOfficePostMpg.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
			officePostMpgDAOImpl.create(hrOfficePostMpg);

			gLogger.info("officePostMpg DAO Created");
			//	NewRegDdoDAO lObjNewRegDdo = new NewRegDdoDAOImpl(HrPaySubstituteEmpMpg.class,
			//		serv.getSessionFactory());
			long subEmpMapId =  lObjNewRegDdoDAO.getNextSeqNum();
			gLogger.info("subEmpMapId "+subEmpMapId);
			hrPaySubstituteEmpMpg.setEmpPostId(employeePostId);
			hrPaySubstituteEmpMpg.setSubEmpMapId(subEmpMapId);
			hrPaySubstituteEmpMpg.setSubPostId(postId);
			hrPaySubstituteEmpMpg.setSubLookupId(Long.parseLong(substituteType));  ///type of substitute
			hrPaySubstituteEmpMpg.setActivateFlag(1);
			//hrPaySubstituteEmpMpg.setUpdatedByPost(orgPostMstLoggedIn);
			hrPaySubstituteEmpMpg.setStartDate(dtStartDate);
			hrPaySubstituteEmpMpg.setEndDate(dtEndDate);
			hrPaySubstituteEmpMpg.setCreatedDate(new Date());
			hrPaySubstituteEmpMpg.setUpdatedDate(new Date());
			hrPaySubstituteEmpMpg.setcreatedByUser(orgUserMstLoggedin.getUserId());		
			hrPaySubstituteEmpMpg.setCreatedByPost(orgPostMstLoggedIn.getPostId());
			lObjNewRegDdoDAO.insertNewPostDetails(hrPaySubstituteEmpMpg);
			gLogger.info("postId "+postId);
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<genPostId>");
			//lStrBldXML.append("<postName>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(postId);
			lStrBldXML.append("]]>");
			/*lStrBldXML.append("</postName>");
				lStrBldXML.append("<postId>");
				lStrBldXML.append("<![CDATA[");
				lStrBldXML.append(postId);
				lStrBldXML.append("]]>");
				lStrBldXML.append("</postId>");*/
			lStrBldXML.append("</genPostId>");
			lStrBldXML.append("</XMLDOC>");
			String lStrTempResult = null;				
			gLogger.info("postId "+postId);
			lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();

			objectArgs.put("ajaxKey", lStrTempResult);
			gLogger.info("post id generated.."+lStrTempResult);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(objectArgs);
			resObj.setViewName("ajaxData");

		} catch (Exception ex) {
			ex.printStackTrace();
			resObj.setThrowable(ex);
			gLogger.error("Admin Screen Submitting A Post Error", ex);
			resObj.setResultCode(-1);
		}
		return resObj;
	}


	int msg;
	//added by shailesh:end
	
	
	/**
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author start by amit bhattad
	 * @param objectArgs
	 */
	
	public ResultObject loadDupEmp(Map inputMap) {
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		
		String  StrEmpname= null;
		String  StrDob = null;
	String  Dob = null;
		String Strgender = null;
		Integer lIntTotalDupEmps = 0;
		List LstDupEmp = new ArrayList();
		try {

			setSessionInfo(inputMap);
			
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());
			
			if (request != null) {
				StrEmpname = StringUtility.getParameter("empId", request).trim();
			
				StrDob = StringUtility.getParameter("Dob", request).trim();
				
				
				
				Strgender = StringUtility.getParameter("Gender", request).trim();
				
			LstDupEmp = lObjNewRegDdoDAO.getListOfDupEmp(StrEmpname, StrDob, Strgender);
			
			String  DCPS_EMP_ID= null;
			String  DCPS_ID = null;
			String  DDO_CODE= null;
			String  EMP_NAME = null;
			String  SEVARTH_ID = null;
			String  gender = null;
			String  DOB = null;
			String  FATHER_OR_HUSBAND = null;
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");
			String dob= StringUtility.getParameter("strDOB", request).trim();
			
			String lStrTempResult ;
			for( Object obj:LstDupEmp)
			{
				  Object[] lObj = (Object[]) obj;
				 lStrTempResult = "";
					 
				  
				  DCPS_ID= (lObj[0]!=null)?lObj[0].toString():"0";
				  DDO_CODE= (lObj[1]!=null)?lObj[1].toString():"0";
				  EMP_NAME= (lObj[2]!=null)?lObj[2].toString():"0";
				  gender= (lObj[3]!=null)?lObj[3].toString():"0";
				  SEVARTH_ID= (lObj[4]!=null)?lObj[4].toString():"0";
				  DOB= (lObj[5]!=null)?lObj[5].toString():"0";
				  FATHER_OR_HUSBAND= (lObj[6]!=null)?lObj[6].toString():"0";
				  DCPS_EMP_ID= (lObj[7]!=null)?lObj[7].toString():"0";
				  
				 
				  
				 		if (lObj != null) {
				 			
				 			lStrBldXML.append("<totalDUPEMP>");
							lStrBldXML.append(LstDupEmp.size());
							lStrBldXML.append("</totalDUPEMP>");
					
						lStrBldXML.append("<dcpsempid>");
						lStrBldXML.append(DCPS_EMP_ID);
						lStrBldXML.append("</dcpsempid>");
									
						
						lStrBldXML.append("<dcps_id>");		
						lStrBldXML.append(DCPS_ID);									
						lStrBldXML.append("</dcps_id>");
						
						
						lStrBldXML.append("<ddo_code>");		
						lStrBldXML.append(DDO_CODE);									
						lStrBldXML.append("</ddo_code>");
						
						
						lStrBldXML.append("<Emp_name>");		
						lStrBldXML.append(EMP_NAME);									
						lStrBldXML.append("</Emp_name>");
						
						
						lStrBldXML.append("<Sevarth_id>");		
						lStrBldXML.append(SEVARTH_ID);									
						lStrBldXML.append("</Sevarth_id>");
						
						
						lStrBldXML.append("<DOB>");		
						lStrBldXML.append(dob);									
						lStrBldXML.append("</DOB>");
						
						lStrBldXML.append("<Gender>");		
						lStrBldXML.append(gender);									
						lStrBldXML.append("</Gender>");
						
						lStrBldXML.append("<Father_name>");		
						lStrBldXML.append(FATHER_OR_HUSBAND);									
						lStrBldXML.append("</Father_name>");
						
					
			}
								
			}	
			lStrBldXML.append("</XMLDOC>");
			lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrTempResult);
			gLogger.info("  final List data .."+lStrTempResult);
			
	   	}
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
				
			
			
	} catch (Exception e) {
		e.printStackTrace();
		objRes.setThrowable(e);
		objRes.setResultCode(ErrorConstants.ERROR);
		objRes.setViewName("errorPage");
		gLogger.error(" Error in loadDupEmp---------------- " + e);
		
	}
		return objRes;
		
		
	}
	
	public ResultObject checkUid(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		int count = 0;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());

			String uid_No = StringUtility.getParameter("uid",request).trim();			

			if (uid_No != null && !"".equals(uid_No)) {
				count = lObjNewRegDdoDAO.checkIfUID(uid_No);				
			}
			
			
			StringBuffer returnMsg = new StringBuffer();
			
			returnMsg.append("<count>");
			returnMsg.append(count);
			returnMsg.append("</count>");
			
			Map result = new  HashMap();
			String stateNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", returnMsg.toString()).toString();
			gLogger.info("stateNameIdStr "+stateNameIdStr);
			result.put("ajaxKey", stateNameIdStr);
			 resObj.setResultValue(result);
			 resObj.setViewName("ajaxData");
		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}
	public ResultObject checkEid(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		int count = 0;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());
			
			String eid_No = StringUtility.getParameter("eid",request).trim();

			if (eid_No != null && !"".equals(eid_No)) {
				count = lObjNewRegDdoDAO.checkIfEID(eid_No);		
			}					
			
			StringBuffer returnMsg = new StringBuffer();
			
			returnMsg.append("<count>");
			returnMsg.append(count);			
			returnMsg.append("</count>");
			Map result = new  HashMap();
			String stateNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", returnMsg.toString()).toString();
			gLogger.info("stateNameIdStr "+stateNameIdStr);
			result.put("ajaxKey", stateNameIdStr);
			 resObj.setResultValue(result);
			 resObj.setViewName("ajaxData");
		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}
	//end by amit bhattad

	//added by samadhan for pf account check ajax
	public ResultObject checkPFAccountNo(Map objectArgs)
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		
		String pfSeries=null;
		String pfAccNo=null;
		Long finalCheckFlag=null;
		String lStrResult = null;
		try {
			
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			String empId = StringUtility.getParameter("id", request).trim();
			String res=lObjNewRegDdoDAO.checkPfDetails(pfSeries,pfAccNo);
			
			pfSeries=StringUtility.getParameter("pfSeries", request).trim();
			pfAccNo=StringUtility.getParameter("pfAcNo", request).trim();
			NewRegDdoDAOImpl objNewRegDdoDaoImpl=new NewRegDdoDAOImpl(HrPayGpfBalanceDtls.class,serv.getSessionFactory());
			finalCheckFlag=objNewRegDdoDaoImpl.checkPFAccountNumber(pfSeries,pfAccNo,empId);
			
			String status=null;
			if(finalCheckFlag>0){
				status="wrong";
			}

			else{
				status="correct";
			}
			String lSBStatus = getResponseXMLDocForDDOFwd(status).toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
			objRes.setResultValue(objectArgs);
			objRes.setViewName("ajaxData");
			
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	private StringBuilder getResponseXMLDocForDDOFwd(String status) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(status);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	//ended by samadhan for pf account check ajax
	
	public ResultObject validateAndPopulateastDDO(Map objectArgs)
	{
		gLogger.info("inside validateAndPopulateastDDO");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		
		String empUserName=null;
		String empName=null;
		String astDDOFlag=null;
		
		
		String lStrResult = null;
		try {
			setSessionInfo(objectArgs);
			
			empUserName=StringUtility.getParameter("empUserName", request).trim();
			gLogger.info("--------empUserName--------:"+empUserName);
			
			
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			gLogger.info("gLngPostId: "+gLngPostId);
			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			gLogger.info("lStrDdoCode: "+lStrDdoCode);
			empName=lObjDcpsCommonDAO.getAstDDONameForAstDDO(empUserName,lStrDdoCode);
			
			
			String lSBStatus = getResponseXMLDocForValidateAstDDO(empName).toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
			objRes.setResultValue(objectArgs);
			objRes.setViewName("ajaxData");
			
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	private StringBuilder getResponseXMLDocForValidateAstDDO(String status) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(status);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	public ResultObject validateAndPopulateDobDojEmpResetPwd(Map objectArgs)
	{
		gLogger.info("inside validateAndPopulateDobDojEmpResetPwd");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		
		String empUserName=null;
		String dob=null;
		String doj=null;
		String resultFlag=null;
		
		
		String lStrResult = null;
		try {
			setSessionInfo(objectArgs);
			
			empUserName=StringUtility.getParameter("empUserName", request).trim();
			gLogger.info("--------empUserName--------:"+empUserName);
			dob=StringUtility.getParameter("dob", request).trim();
			gLogger.info("--------dob--------:"+dob);
			doj=StringUtility.getParameter("doj", request).trim();
			gLogger.info("--------doj--------:"+doj);
			
			
			//SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			//dob=lObjSimpleDateFormat.parse(dob).toString();
			//doj=lObjSimpleDateFormat.parse(doj).toString();
			
			dob=formatDate(dob);
			doj=formatDate(doj);
			
			gLogger.info("--------dob after parsing--------:"+dob);
			gLogger.info("--------doj after parsing--------:"+doj);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			gLogger.info("gLngPostId: "+gLngPostId);
			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			gLogger.info("lStrDdoCode: "+lStrDdoCode);
			resultFlag=lObjDcpsCommonDAO.validateEmpDobDojForResetPwd(empUserName,lStrDdoCode,dob,doj);
			gLogger.info("resultFlag(count of valid emp): "+resultFlag);
			//
			//reset pwd code goes here
			String finalFlag="";
			if(Integer.parseInt(resultFlag)>0)
			{
			lObjDcpsCommonDAO.UpdatePwd(empUserName);
			finalFlag="Y";
			}
			else
			{
				finalFlag="N";
			}
			//
			
			
			//lSBStatus = Y or N
			String lSBStatus = getResponseXMLDocForValidateAstDDO(finalFlag).toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
			objRes.setResultValue(objectArgs);
			objRes.setViewName("ajaxData");
			
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	private String formatDate(String dt) {
		String []arrDate=dt.split("/");
		return(arrDate[2]+"-"+arrDate[1]+"-"+arrDate[0]+" 00:00:00.0");
	}
	//added for reliving Employee
	
	
	public ResultObject getReasonListForOffice(Map objectArgs)
	{
		gLogger.info("inside getReasonListForOffice");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String officeType=null;		
		List<ComboValuesVO>  lLstReasons =  new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO = new ComboValuesVO();
		cmbVO.setId("-1");
		cmbVO.setDesc("Select");
		lLstReasons.add(cmbVO);
		Long prntLookupId=0l;
		try {
			setSessionInfo(objectArgs);
			
			officeType=StringUtility.getParameter("officeType", request).trim();
			gLogger.info("--------officeType--------:"+officeType);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			if(officeType.equals("Sevaarthoffice")){
				prntLookupId=700050l;
			lLstReasons = lObjDcpsCommonDAO.getReasonValues(prntLookupId);
			}
			else
			{
				 prntLookupId=700053l;
				lLstReasons = lObjDcpsCommonDAO.getDeptReasonValues(prntLookupId);	
			}
				gLogger.info("--------lLstReasons size--------:"+lLstReasons.size());
			String AjaxResult = new AjaxXmlBuilder().addItems(lLstReasons, "desc", "id").toString();					
			objectArgs.put("ajaxKey", AjaxResult);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(objectArgs);
			objRes.setViewName("ajaxData");
			return objRes;
			
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	

	public ResultObject getDdoOficeDesignName(Map objectArgs)
	{
		gLogger.info("inside getDdoOficeDesignName");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String ddoCode=null;		
		
		try {
			setSessionInfo(objectArgs);			
			ddoCode=StringUtility.getParameter("ddoCode", request).trim();
			gLogger.info("--------ddoCode--------:"+ddoCode);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			String flag=lObjDcpsCommonDAO.checkDDOCodePresent(ddoCode);
			List oficeDesgnList=lObjDcpsCommonDAO.getDDOoficeDesgn(ddoCode);
		
			String officeName="N";
			String desingName="N";
			String lStrResult="";
			String lSBStatus="";
			StringBuilder lStrBldXML = new StringBuilder();
			if(flag.equals("YES")){
				
			
			if (oficeDesgnList != null && oficeDesgnList.size() > 0)
			{
				Iterator IT = oficeDesgnList.iterator();
				Object[] lObj = null;
				while (IT.hasNext())
				{					
					lObj = (Object[]) IT.next();
					 officeName =lObj[0].toString();
						gLogger.info("**************officeName*************" +officeName);
					desingName= lObj[1].toString();
					gLogger.info("***********desingName*********" +desingName);
				}
			}
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ddoOffice>");
			lStrBldXML.append(officeName);
			lStrBldXML.append("</ddoOffice>");
			lStrBldXML.append("<ddoDesgin>");
			lStrBldXML.append(desingName);
			lStrBldXML.append("</ddoDesgin>");
			lStrBldXML.append("</XMLDOC>");
			lSBStatus = lStrBldXML.toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			}else{
				
				
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<ddoOffice>");
				lStrBldXML.append("NO");
				lStrBldXML.append("</ddoOffice>");
				lStrBldXML.append("<ddoDesgin>");
				lStrBldXML.append("NO");
				lStrBldXML.append("</ddoDesgin>");
				lStrBldXML.append("</XMLDOC>");
				lSBStatus = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			}
			gLogger.info("********************************************" + lStrResult);
			objectArgs.put("ajaxKey", lStrResult);
			gLogger.info("lStrResult---------" + lStrResult);
			objRes.setResultValue(objectArgs);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setViewName("ajaxData");
			return objRes;
			
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	public String generateDCPSId(Map inputMap, Long lLngEmpId) throws Exception{

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstEmp lObjEmpData = null;
		String DCPSID = "";

		try {
			setSessionInfo(inputMap);

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			lObjEmpData = new MstEmp();
			lObjEmpData = (MstEmp) lObjNewRegTreasuryDAO.read(lLngEmpId);

			// Code for DCPS ID Generation

			String lLongDDOCode = lObjEmpData.getDdoCode();
			String lStrDDOCode = String.valueOf(lLongDDOCode);
			String lStrEmpFname = lObjEmpData.getName();
			Character lCharGender = lObjEmpData.getGender();
			Date lDateBirthDate = lObjEmpData.getDob();
			lObjEmpData.getDesignation();
			lObjEmpData.getParentDept();
			String lStrBirthDate = lObjDateFormat.format(lDateBirthDate);

			// 1st digit based on state or Zonal
			DCPSID = DCPSID + "1";

			DCPSID = DCPSID + lStrDDOCode;

			lStrEmpFname = lStrEmpFname.replace('.', ' ');
			lStrEmpFname = lStrEmpFname.replaceAll("\\s+", " ");

			String Names[] = lStrEmpFname.split(" ");
			String Fname = null;
			String Mname = null;
			String Lname = null;

			if (Names.length >= 3) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				Lname = Names[2].substring(0, 1);

			}

			else if (Names.length == 2) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				//Lname = ".";
				if(Names[1].length() > 1)
				Lname = Names[1].substring(1, 2);
				else 
				{
				Lname = Names[0].substring(Names[0].length()-1);				
				}

			}

			else if (Names.length == 1) {
				Fname = Names[0].substring(0, 1);
				//Mname = ".";
				//Lname = ".";
				
				Mname = Names[0].substring(1,2);
				Lname = Names[0].substring(2,3);
			}

			DCPSID = DCPSID + Fname + Mname + Lname;
			
			if (lCharGender == 'M') {
				DCPSID = DCPSID + "M";
			}
			if (lCharGender == 'F') {
				DCPSID = DCPSID + "F";
			}
			if (lCharGender == 'T') {
				DCPSID = DCPSID + "T";
			}

			DCPSID = DCPSID + lStrBirthDate.substring(8, 10);

			Long count = lObjNewRegTreasuryDAO.getCountOfEmpOfSameName(DCPSID);

			count = count + 1;
			String countForNewEmployee = "";

			if (String.valueOf(count).length() == 1) {
				countForNewEmployee = "0" + String.valueOf(count);
			} else {
				countForNewEmployee = String.valueOf(count);
			}

			DCPSID = DCPSID + countForNewEmployee;

			Character LastDigit = getLastDigit(DCPSID);

			DCPSID = DCPSID + LastDigit;

			// Code for DCPS ID Generation

		} catch (Exception ex) {
			gLogger.error(" Error generating DCPS Id is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			throw ex;
			//ex.printStackTrace();

		}

		return DCPSID;

	}

	public static Character getLastDigit(String lStrDCPSId) {

		Character LastDigit = null;
		Map<String, Integer> MappingData = getMappingData();

		char[] lArrStrDcpsId = lStrDCPSId.toCharArray();

		Integer[] lArrIntMultiplication = {1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5};

		Integer lIntTotal = 0;

		for (Integer index = 0; index < 19; index++) {
			String lStrMappedKey = Character.toString(lArrStrDcpsId[index]);
			Integer lIntMap = MappingData.get(lStrMappedKey);
			lIntTotal = lIntTotal + (lIntMap * lArrIntMultiplication[index]);
		}

		Integer lIntLastDigit = 26 - (lIntTotal % 26);
		LastDigit = (char) (64 + lIntLastDigit);

		return LastDigit;
	}

	public static Map getMappingData() {

		Map<String, Integer> MappingData = new HashMap<String, Integer>();

		MappingData.put("0", 0);
		MappingData.put("1", 1);
		MappingData.put("2", 2);
		MappingData.put("3", 3);
		MappingData.put("4", 4);
		MappingData.put("5", 5);
		MappingData.put("6", 6);
		MappingData.put("7", 7);
		MappingData.put("8", 8);
		MappingData.put("9", 9);
		MappingData.put("A", 10);
		MappingData.put("B", 11);
		MappingData.put("C", 12);
		MappingData.put("D", 13);
		MappingData.put("E", 14);
		MappingData.put("F", 15);
		MappingData.put("G", 16);
		MappingData.put("H", 17);
		MappingData.put("I", 18);
		MappingData.put("J", 19);
		MappingData.put("K", 20);
		MappingData.put("L", 21);
		MappingData.put("M", 22);
		MappingData.put("N", 23);
		MappingData.put("O", 24);
		MappingData.put("P", 25);
		MappingData.put("Q", 26);
		MappingData.put("R", 27);
		MappingData.put("S", 28);
		MappingData.put("T", 29);
		MappingData.put("U", 30);
		MappingData.put("V", 31);
		MappingData.put("W", 32);
		MappingData.put("X", 33);
		MappingData.put("Y", 34);
		MappingData.put("Z", 35);
		MappingData.put(".", 36);

		return MappingData;
	}
	

}