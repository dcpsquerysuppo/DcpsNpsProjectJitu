/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAO;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.RltBillgroupClassgroup;
import com.tcs.sgv.dcps.valueobject.postDetailsCustomVO;
import com.tcs.sgv.eis.dao.OtherDetailDAOImpl;
import com.tcs.sgv.eis.valueobject.HrEisOtherDtls;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.user.valueobject.UserPostCustomVO;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 28, 2011
 */
public class DdoBillGroupServiceImpl extends ServiceImpl implements DdoBillGroupService {

	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Current Date */
	Date gCurDate = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private Locale gLclLocale = null; /* LOCALE */
	private String gStrPostId = null; /* STRING POST ID */
	private String gStrUserId = null; /* STRING USER ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private HttpSession session = null; /* SESSION */
	private String gStrLocale = null; /* STRING LOCALE */
	private Date gDtCurDate = null; /* CURRENT DATE */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	private final static Logger gLogger = Logger.getLogger(DdoInfoServiceImpl.class);

	private static final Log logger = LogFactory.getLog(DdoInfoServiceImpl.class); /* LOGGER */

	private void setSessionInfo(Map inputMap) throws Exception {

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
			gStrLocId = SessionHelper.getLocationId(inputMap).toString();

		} catch (Exception e) {
			logger.error("Error in setSessionInfo of HBARequestServiceImpl ", e);
			throw e;
		}
	}

	public ResultObject loadBillGroupCreation(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {

			setSessionInfo(inputMap);
			//added by Kinjal
			String billGroupFlag = StringUtility.getParameter("billGrpFlag", request).trim();
			inputMap.put("billGroupFlag", billGroupFlag);

			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(DdoOffice.class, serv.getSessionFactory());

			DcpsCommonDAO DcpsCommonDAOObj = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			String lStrDdoCode = DcpsCommonDAOObj.getDdoCode(gLngPostId);
			inputMap.put("DDOCODE", lStrDdoCode);

			List lListSchemes = lObjDdoBillGroupDAO.getSchemesforDDOComboVO(lStrDdoCode);
			inputMap.put("lListSchemes", lListSchemes);

			List lListClassGroups = IFMSCommonServiceImpl.getLookupValues("GroupListForBillGroup", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("lListClassGroups", lListClassGroups);

			List lLstBillGroups = lObjDdoBillGroupDAO.getSavedBillGroups(lStrDdoCode, billGroupFlag);
			inputMap.put("BillGroupList", lLstBillGroups);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DcpsDdoGroupBill");

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

	public ResultObject popUpBillGroupDtls(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lListClassGroup = null;
		Boolean lBlEmpsInBGOrNot = null;
		Boolean lBlPostsInBGOrNot = null;
		Boolean lBlBillsGeneratedInBGOrNot = null;

		try {

			setSessionInfo(inputMap);
			
			DcpsCommonDAO DcpsCommonDAOObj = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(MstDcpsBillGroup.class, serv.getSessionFactory());
			
			Long lLongBillGroupId = Long.valueOf(StringUtility.getParameter("billGroupId", request).trim());

			MstDcpsBillGroup lObjMstDcpsBillGroup = lObjDdoBillGroupDAO.getBillGroupDtlsForBillGroupId(lLongBillGroupId);

			lListClassGroup = lObjDdoBillGroupDAO.getClassGroupsForGivenBGId(lLongBillGroupId);
			
			Integer lIntTotalEmployeesForBillGroup = lObjDdoBillGroupDAO.getTotalEmployeesForBillGroup(lObjMstDcpsBillGroup.getDcpsDdoBillGroupId());
			
			if(lIntTotalEmployeesForBillGroup == 0)
			{
				lBlEmpsInBGOrNot = false;
			}
			else
			{
				lBlEmpsInBGOrNot = true;
			}
			
			Integer lIntTotalPostsForBillGroup = lObjDdoBillGroupDAO.getTotalPostsForBillGroup(lObjMstDcpsBillGroup.getDcpsDdoBillGroupId());
			
			if(lIntTotalPostsForBillGroup == 0)
			{
				lBlPostsInBGOrNot = false;
			}
			else
			{
				lBlPostsInBGOrNot = true;
			}
			
			Integer lIntTotalBillsGeneratedForBillGroup = lObjDdoBillGroupDAO.getBillsGeneratedForTheBillGroup(lObjMstDcpsBillGroup.getDcpsDdoBillGroupId());
			
			if(lIntTotalBillsGeneratedForBillGroup == 0)
			{
				lBlBillsGeneratedInBGOrNot = false;
			}
			else
			{
				lBlBillsGeneratedInBGOrNot = true;
			}
			
			String lStrDdoCode = DcpsCommonDAOObj.getDdoCode(gLngPostId);
			
			Boolean lBlContriExistInTheBillGroupFlag = false;
			lBlContriExistInTheBillGroupFlag = lObjDdoBillGroupDAO.checkContributionsExistInTheBillGroup(lLongBillGroupId, lStrDdoCode);

			String lSBStatus = getResponseXMLDocForPopupBGDtls(lObjMstDcpsBillGroup, lListClassGroup,lBlEmpsInBGOrNot,lBlContriExistInTheBillGroupFlag,lBlPostsInBGOrNot,lBlBillsGeneratedInBGOrNot).toString();
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
	
	public ResultObject deleteSubBillGroup(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlSuccessFlag = null;

		try {

			setSessionInfo(inputMap);

			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(MstDcpsBillGroup.class, serv.getSessionFactory());
			Long lLongBillGroupId = Long.valueOf(StringUtility.getParameter("billGroupId", request));

			MstDcpsBillGroup lObjMstDcpsBillGroup = lObjDdoBillGroupDAO.getBillGroupDtlsForBillGroupId(lLongBillGroupId);
			
			lObjMstDcpsBillGroup.setBillDeleted('Y');
			lObjDdoBillGroupDAO.update(lObjMstDcpsBillGroup);
			//Bill-groups not allowed be hard-deleted 
			/*
			lObjDdoBillGroupDAO.delete(lObjMstDcpsBillGroup);
			lObjDdoBillGroupDAO.deleteClassGroupsForGivenBGId(lLongBillGroupId);
			*/
			
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

	public ResultObject saveDCPSDdoBill(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lblFlagBillGroupClassGroupVO = false;
		Boolean lBlGroupExistOrNotForBG = false;
		Long lLngBillClassGroupId = null;
		Long lLongBillGroupId = null;

		try {
			setSessionInfo(inputMap);

			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(MstDcpsBillGroup.class, serv.getSessionFactory());

			Integer createOrUpdateFlag = (Integer) inputMap.get("createOrUpdateFlag");
			
			if (createOrUpdateFlag == 2) {
				lLongBillGroupId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_bill_group", inputMap);
			} else {
				lLongBillGroupId = Long.valueOf(StringUtility.getParameter("txtBillGroupNo", request).trim());
			}

			String lStrtypeOfPost = StringUtility.getParameter("RadioPermenantTempBoth", request).trim();
			String lStrDescription = StringUtility.getParameter("txtDescription", request).trim();
			String lStrtypeOfBillGrp = StringUtility.getParameter("billType", request);
			logger.info("lStrtypeOfBillGrp----"+lStrtypeOfBillGrp);
			lStrDescription = lStrDescription.replace("~percent~", "%");

			// Flag - 2 for First time save.
			if (createOrUpdateFlag == 2) {
				MstDcpsBillGroup lObjMstDcpsBillGroup = (MstDcpsBillGroup) inputMap.get("dcpsddobillgroup");
				lObjMstDcpsBillGroup.setDcpsDdoBillGroupId(lLongBillGroupId);
				lObjMstDcpsBillGroup.setDcpsDdoBillTypeOfPost(lStrtypeOfPost);
				lObjMstDcpsBillGroup.setSubBGOrNot(1l);
				lObjMstDcpsBillGroup.setTypeOfBill(lStrtypeOfBillGrp);
				lObjDdoBillGroupDAO.create(lObjMstDcpsBillGroup);
			} else {

				/* Updates the Post Type in MstDcpsBillGroup VO */
				MstDcpsBillGroup MstDcpsBillGroupObj = null;
				MstDcpsBillGroupObj = (MstDcpsBillGroup) lObjDdoBillGroupDAO.read(lLongBillGroupId);
				MstDcpsBillGroupObj.setDcpsDdoBillTypeOfPost(lStrtypeOfPost);
				MstDcpsBillGroupObj.setDcpsDdoBillDescription(lStrDescription);
				//MstDcpsBillGroupObj.setSubBGOrNot(1l);
				MstDcpsBillGroupObj.setTypeOfBill(lStrtypeOfBillGrp);
				lObjDdoBillGroupDAO.update(MstDcpsBillGroupObj);
			}

			List<RltBillgroupClassgroup> lListBillGroupClassGroup = (List<RltBillgroupClassgroup>) inputMap.get("dcpsBillGroupClassGroupVOList");
			lBlGroupExistOrNotForBG = lObjDdoBillGroupDAO.checkGroupExistsOrNotForBG(lLongBillGroupId);

			if (lBlGroupExistOrNotForBG == true) {
				lObjDdoBillGroupDAO.deleteClassGroupsForGivenBGId(lLongBillGroupId);
			}

			for (Integer lInt = 0; lInt < lListBillGroupClassGroup.size(); lInt++) {
				RltBillgroupClassgroup dcpsBillGroupClassGroupVO = lListBillGroupClassGroup.get(lInt);
				if (createOrUpdateFlag == 2) {
					dcpsBillGroupClassGroupVO.setDcpsBillGroupId(lLongBillGroupId);
				}
				lLngBillClassGroupId = IFMSCommonServiceImpl.getNextSeqNum("rlt_dcps_billgroup_classgroup", inputMap);
				dcpsBillGroupClassGroupVO.setDcpsBillClassGroupId(lLngBillClassGroupId);
				lObjDdoBillGroupDAO.create(dcpsBillGroupClassGroupVO);

			}

			/*
			 * String lStrDcpsEmpIdsUnchecked = StringUtility.getParameter(
			 * "dcpsEmpIdsUnChecked", request); String[]
			 * lStrArrdcpsEmpIdsUnchecked = lStrDcpsEmpIdsUnchecked .split(",");
			 * Long[] lLongArrdcpsEmpIdsUnchecked = new
			 * Long[lStrArrdcpsEmpIdsUnchecked.length]; for (Integer lInt = 0;
			 * lInt < lStrArrdcpsEmpIdsUnchecked.length; lInt++) {
			 * lLongArrdcpsEmpIdsUnchecked[lInt] = Long
			 * .valueOf(lStrArrdcpsEmpIdsUnchecked[lInt]); }
			 * 
			 * MstEmp MstEmpObj = null;
			 * 
			 * for (Integer lInt = 0; lInt < lLongArrdcpsEmpIdsUnchecked.length;
			 * lInt++) { MstEmpObj = (MstEmp) (NewRegistrationDAOImpl
			 * .read(lLongArrdcpsEmpIdsUnchecked[lInt]));
			 * MstEmpObj.setBillGroupId(null); }
			 */

			lblFlagBillGroupClassGroupVO = true;

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error Dip" + e, e);
		}

		String lSBStatus = getResponseXMLDoc(lblFlagBillGroupClassGroupVO).toString();

		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");

		return resObj;
	}

	public ResultObject loadAttachBillGroup(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

			List showgroupList = null;
			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO DcpsCommonDAOObj = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			String lStrDDOCode = DcpsCommonDAOObj.getDdoCode(SessionHelper.getPostId(inputMap));
			showgroupList = lObjDdoBillGroupDAO.getShowGroupList(lStrDDOCode);
			inputMap.put("showgroupList", showgroupList);

			List billgroupList = null;
			billgroupList = lObjDdoBillGroupDAO.getBillGroupsWithSchemeCode(lStrDDOCode);
			inputMap.put("billgroupList", billgroupList);
			
			String dsgnName = "";
			Long dcpsEmpId = null;
			String name = "";
			Object rowList[] = null;
			
			Long postId = null;
			String postName = "";
			List status=null;
			boolean flag=false;
			
			String lStrBillGrupId = StringUtility.getParameter("billGroupId", request).trim();
			logger.info("lStrBillGrupId1123"+lStrBillGrupId);
			String lStrTypeOfAttachDetach = StringUtility.getParameter("typeOfAttachDetach", request ).trim();

			if (!"".equals(lStrBillGrupId) && !"".equals(lStrTypeOfAttachDetach))
			{
				Long lLongTypeOfAttachDetach = Long.valueOf(lStrTypeOfAttachDetach);
				if(lLongTypeOfAttachDetach == 1)
				{
					//Integer totalRecords = lObjDdoBillGroupDAO.getEmpListCount(lStrDDOCode, displayTag);
					//inputMap.put("totalRecords", totalRecords);

					List empList = null;
					
					empList = lObjDdoBillGroupDAO.getEmpListForBGIdNull(lStrDDOCode, displayTag);
					if(empList != null)
					{
						inputMap.put("totalRecords", empList.size());
					}
					else
					{
						inputMap.put("totalRecords", 0);
					}

					Long lLongBillGroupId = Long.valueOf(lStrBillGrupId);
					inputMap.put("billGroupIdPassed", lLongBillGroupId);

					String lStrBillGroupDtls = lObjDdoBillGroupDAO.getBillGroupDetailsForBillGroupId(lLongBillGroupId);
					//status = lObjDdoBillGroupDAO.getStatusForBillGroupId(lLongBillGroupId);
					inputMap.put("BillGroupDtls", lStrBillGroupDtls);

					List empListForBG = null;
					empListForBG = lObjDdoBillGroupDAO.getEmpListForGivenBillGroup(lLongBillGroupId, lStrDDOCode);
					
					List empVacantList = new ArrayList();	
					postDetailsCustomVO customVO = new postDetailsCustomVO();
					
					if(empList!=null && !empList.isEmpty())
					{
						for(Integer i =0;i<empList.size();i++)
						{
							customVO = new postDetailsCustomVO();
							
							rowList = (Object[]) empList.get(i);
							
							if(rowList[3] != null)
							{
								dsgnName = rowList[3].toString();
							}
							
							if(rowList[1] != null)
							{
								if(!"".equals(rowList[1].toString()))
								{
									dcpsEmpId = Long.valueOf(rowList[1].toString());
								}
							}
							
							if(rowList[2] != null)
							{
								name = rowList[2].toString();
							}
							
							name = name.concat(" ("+dsgnName+")");
							
							customVO.setName(name);
							customVO.setDcpsEmpId(dcpsEmpId);
							
							empVacantList.add(customVO);
						}
					}
					if(empList!=null && !empList.isEmpty())
					{
						inputMap.put("empList", empVacantList);
					}
					else
					{
						inputMap.put("empList", empList);
					}
					
					List empRightSideList = new ArrayList();	
					
					if(empListForBG!=null && !empListForBG.isEmpty())
					{
						for(Integer i =0;i<empListForBG.size();i++)
						{
							customVO = new postDetailsCustomVO();
							
							rowList = (Object[]) empListForBG.get(i);
							
							if(rowList[2] != null)
							{
								dsgnName = rowList[2].toString();
							}
							
							if(rowList[0] != null)
							{
								if(!"".equals(rowList[0].toString()))
								{
									dcpsEmpId = Long.valueOf(rowList[0].toString());
								}
							}
							
							if(rowList[1] != null)
							{
								name = rowList[1].toString();
							}
							name = name.concat(" ("+dsgnName+")");
							
							customVO.setName(name);
							customVO.setDcpsEmpId(dcpsEmpId);
							
							empRightSideList.add(customVO);
						}
					}
					
					if(empListForBG != null && !empListForBG.isEmpty())
					{
						inputMap.put("empListForBG", empRightSideList);
					}
					else
					{
						inputMap.put("empListForBG", empListForBG);
						
					}
				}
				if(lLongTypeOfAttachDetach == 2)
				{
					//LoginDetails objLoginDetails = (LoginDetails) inputMap.get("baseLoginVO");
					//Long locationId = objLoginDetails.getLoggedInPostDetailsRlt().getCmnLocationMst().getLocId();
										
					Long lLongBillGroupId = Long.valueOf(lStrBillGrupId);
					inputMap.put("billGroupIdPassed", lLongBillGroupId);

					String lStrBillGroupDtls = lObjDdoBillGroupDAO.getBillGroupDetailsForBillGroupId(lLongBillGroupId);
					
					inputMap.put("BillGroupDtls", lStrBillGroupDtls);
					
					Long postLookupId=0l;
					String permenantlookupId="10001198129";
					String temparerylookupId="10001198130";
					
					List postVacanceTypeList = new ArrayList();	
					List postVacanceTypeListForBG = null;
					postDetailsCustomVO customVO = new postDetailsCustomVO();
					
					postVacanceTypeListForBG = lObjDdoBillGroupDAO.getPostListForLocation(gStrLocId, displayTag);
					
					if(postVacanceTypeListForBG!=null && !postVacanceTypeListForBG.isEmpty())
					{
						for(int i =0;i<postVacanceTypeListForBG.size();i++)
						{
							customVO = new postDetailsCustomVO();
							
							rowList = (Object[]) postVacanceTypeListForBG.get(i);
							
							postId = Long.parseLong(rowList[0].toString());
							postName = rowList[1].toString();
							 
							postLookupId  = Long.parseLong(rowList[2].toString());
						
							if(postLookupId==10001198129l)
							{
								postName =postName.concat("(P)");
							}
							else if(postLookupId==10001198130l)
							{
								postName =postName.concat("(T)");
							}
							else
							{
								postName =postName;
							}
							
							customVO.setPostId(postId);
							customVO.setPostName(postName);
							
							postVacanceTypeList.add(customVO);
						}
					}
					//inputMap.put("totalRecords", postVacanceTypeListForBG.size());
					
					if(postVacanceTypeListForBG != null)
					{
						inputMap.put("totalRecords", postVacanceTypeListForBG.size());
					}
					else
					{
						inputMap.put("totalRecords", 0);
					}
					
					if(postVacanceTypeListForBG!=null && !postVacanceTypeListForBG.isEmpty())
					{
						inputMap.put("empList", postVacanceTypeList);
					}
					else
					{
						inputMap.put("empList", postVacanceTypeListForBG);
					}
					
					List postTypeList = new ArrayList();	
					List postTypeListForBG = null;
					
					postTypeListForBG = lObjDdoBillGroupDAO.getPostTypeListForGivenBillGroup(lLongBillGroupId, gStrLocId);
					
					if(postTypeListForBG!=null && !postTypeListForBG.isEmpty())
					{
						for(int i =0;i<postTypeListForBG.size();i++)
						{
							customVO = new postDetailsCustomVO();
							
							rowList = (Object[]) postTypeListForBG.get(i);
							
							postId = Long.parseLong(rowList[0].toString());
							postName = rowList[1].toString();
							 
							postLookupId  = Long.parseLong(rowList[2].toString());
							
							if(postLookupId == 10001198129l)
							{
								postName =postName.concat("(P)");
							}
							else if(postLookupId == 10001198130l)
							{
								postName =postName.concat("(T)");
							}
							else
							{
								postName = postName;
							}
							
							customVO.setPostId(postId);
							customVO.setPostName(postName);
							
							postTypeList.add(customVO);
						}
					}
					if(postTypeListForBG!=null && !postTypeListForBG.isEmpty())
					{
						inputMap.put("empListForBG", postTypeList);
					}
					else
					{
						inputMap.put("empListForBG", postTypeListForBG);						
					}
					
				}
				
				inputMap.put("typeOfOperation",lLongTypeOfAttachDetach);
				logger.info("lStrBillGrupId11234"+lStrBillGrupId);
			}
			//added by sunitha
			if (!"".equals(lStrBillGrupId)){
				logger.info("lStrBillGrupId1123444444"+lStrBillGrupId);
				Long lLongbillGroupId = Long.valueOf(lStrBillGrupId);
				status = lObjDdoBillGroupDAO.getStatusForBillGroupId(lLongbillGroupId);
				if(status!= null && status.size()>0){
					flag=true;
					Object obj[] = (Object[])status.get(0);
					inputMap.put("payMonth",obj[1].toString());
					inputMap.put("payYear",obj[2].toString());
					logger.info("status of bill"+status.size());
					
				}
				else{
					flag=false;
				
				}
				inputMap.put("flag",flag);
				logger.info("status of bill flag"+flag);
			}
		
			
			
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSAttachBillGroup");

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

	public ResultObject attachAndDetachEmpToBG(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlSuccessFlag = false;
		MstEmp lObjDcpsEmpMst = null;

		try {

			setSessionInfo(inputMap);
			NewRegDdoDAO dcpsNewRegistrationDao = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());
			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(null, serv.getSessionFactory());

			Long lLongbillGroupId = Long.valueOf(StringUtility.getParameter("billGroupId", request));
			Long typeOfOperation = Long.valueOf(StringUtility.getParameter("typeOfAttachDetach", request));
			if(typeOfOperation == 1)
			{
			
				String lStrDcpsEmpIdstoBeDetached = StringUtility.getParameter("dcpsEmpIdstoBeDetached", request);
				String[] lStrArrDcpsEmpIdstoBeDetached = lStrDcpsEmpIdstoBeDetached.split("~");
				Long[] lLongArrDcpsEmpIdstoBeDetached = new Long[lStrArrDcpsEmpIdstoBeDetached.length];
				for (Integer lInt = 0; lInt < lStrArrDcpsEmpIdstoBeDetached.length; lInt++) {
					if (lStrArrDcpsEmpIdstoBeDetached[lInt] != "") {
						lLongArrDcpsEmpIdstoBeDetached[lInt] = Long.valueOf(lStrArrDcpsEmpIdstoBeDetached[lInt]);
						lObjDdoBillGroupDAO.updateBillNoInPayroll(lLongArrDcpsEmpIdstoBeDetached[lInt], null, "Detach");
					}
				}

				String lStrDcpsEmpIdstoBeAttached = StringUtility.getParameter("dcpsEmpIdstoBeAttached", request);
				String[] lStrArrDcpsEmpIdstoBeAttached = lStrDcpsEmpIdstoBeAttached.split("~");
				Long[] lLongArrDcpsEmpIdstoBeAttached = new Long[lStrArrDcpsEmpIdstoBeAttached.length];
				for (Integer lInt = 0; lInt < lStrArrDcpsEmpIdstoBeAttached.length; lInt++) {
					if (lStrArrDcpsEmpIdstoBeAttached[lInt] != "") {
						lLongArrDcpsEmpIdstoBeAttached[lInt] = Long.valueOf(lStrArrDcpsEmpIdstoBeAttached[lInt]);
						lObjDdoBillGroupDAO.updateBillNoInPayroll(lLongArrDcpsEmpIdstoBeAttached[lInt], lLongbillGroupId, "Attach");
					}
				}

				// Does the function of Detachment
				for (Integer lInt = 0; lInt < lLongArrDcpsEmpIdstoBeDetached.length; lInt++) {

					if (lLongArrDcpsEmpIdstoBeDetached[lInt] != null) {

						lObjDcpsEmpMst = (MstEmp) dcpsNewRegistrationDao.read(Long.valueOf(lLongArrDcpsEmpIdstoBeDetached[lInt]));
						lObjDcpsEmpMst.setBillGroupId(null);
					}
				}

				// Does the function of Attachment

				for (Integer lInt = 0; lInt < lLongArrDcpsEmpIdstoBeAttached.length; lInt++) {

					if (lLongArrDcpsEmpIdstoBeAttached[lInt] != null) {

						lObjDcpsEmpMst = (MstEmp) dcpsNewRegistrationDao.read(Long.valueOf(lLongArrDcpsEmpIdstoBeAttached[lInt]));
						lObjDcpsEmpMst.setBillGroupId(lLongbillGroupId);
					}
				}
			}
			
			if(typeOfOperation ==2 ){
				
				String lStrPostIdstoBeDetached = StringUtility.getParameter("dcpsEmpIdstoBeDetached", request);
				String[] lStrArrPostIdstoBeDetached = lStrPostIdstoBeDetached.split("~");
				Long[] lLongArrPostIdstoBeDetached = new Long[lStrArrPostIdstoBeDetached.length];
				for (Integer lInt = 0; lInt < lStrArrPostIdstoBeDetached.length; lInt++) {
					if (lStrArrPostIdstoBeDetached[lInt] != "") {
						lLongArrPostIdstoBeDetached[lInt] = Long.valueOf(lStrArrPostIdstoBeDetached[lInt]);
						lObjDdoBillGroupDAO.updateBillNoOfPostInPayroll(lLongArrPostIdstoBeDetached[lInt], null, "Detach");
					}
				}

				String lStrPostIdstoBeAttached = StringUtility.getParameter("dcpsEmpIdstoBeAttached", request);
				String[] lStrArrPostIdstoBeAttached = lStrPostIdstoBeAttached.split("~");
				Long[] lLongArrPostIdstoBeAttached = new Long[lStrArrPostIdstoBeAttached.length];
				for (Integer lInt = 0; lInt < lStrArrPostIdstoBeAttached.length; lInt++) {
					if (lStrArrPostIdstoBeAttached[lInt] != "") {
						lLongArrPostIdstoBeAttached[lInt] = Long.valueOf(lStrArrPostIdstoBeAttached[lInt]);
						lObjDdoBillGroupDAO.updateBillNoOfPostInPayroll(lLongArrPostIdstoBeAttached[lInt], lLongbillGroupId, "Attach");
					}
				}
				
			}

			lBlSuccessFlag = true;

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		String lSBStatus = getResponseXMLDoc(lBlSuccessFlag).toString();

		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");

		return resObj;
	}
	
	public ResultObject getTotalSubBGsForScheme(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lListClassGroup = null;

		try {

			setSessionInfo(inputMap);

			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(MstDcpsBillGroup.class, serv.getSessionFactory());
			String lStrSchemeCode = StringUtility.getParameter("schemeCode", request);
			
			DcpsCommonDAO DcpsCommonDAOObj = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			String lStrDdoCode = DcpsCommonDAOObj.getDdoCode(gLngPostId);

			Integer lIntTotalSubBGsForScheme = lObjDdoBillGroupDAO.getTotalSubBGsForScheme(lStrDdoCode,lStrSchemeCode);

			String lSBStatus = getResponseXMLDocForGetTotalSubBgsForScheme(lIntTotalSubBGsForScheme).toString();
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
	
	private StringBuilder getResponseXMLDocForGetTotalSubBgsForScheme(Integer lIntTotalSubBGsForScheme) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<totalSubBgForScheme>");
		lStrBldXML.append(lIntTotalSubBGsForScheme);
		lStrBldXML.append("</totalSubBgForScheme>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForPopupBGDtls(MstDcpsBillGroup lObjMstDcpsBillGroup, List lListClassGroup,Boolean lBlEmpsInBGOrNot,Boolean lBlContriExistInTheBillGroupFlag,Boolean lBlPostsInBGOrNot,Boolean lBlBillsGeneratedInBGOrNot) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<billGroupId>");
		lStrBldXML.append(lObjMstDcpsBillGroup.getDcpsDdoBillGroupId());
		lStrBldXML.append("</billGroupId>");

		lStrBldXML.append("<billGroupDesc>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lObjMstDcpsBillGroup.getDcpsDdoBillDescription().trim());
		lStrBldXML.append("]]>");
		lStrBldXML.append("</billGroupDesc>");

		lStrBldXML.append("<schemeName>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lObjMstDcpsBillGroup.getDcpsDdoBillSchemeName().trim());
		lStrBldXML.append("]]>");
		lStrBldXML.append("</schemeName>");

		lStrBldXML.append("<schemeCode>");
		lStrBldXML.append(lObjMstDcpsBillGroup.getDcpsDdoSchemeCode());
		lStrBldXML.append("</schemeCode>");

		lStrBldXML.append("<typeOfPost>");
		lStrBldXML.append(lObjMstDcpsBillGroup.getDcpsDdoBillTypeOfPost());
		lStrBldXML.append("</typeOfPost>");

		lStrBldXML.append("<totalClassGroups>");
		lStrBldXML.append(lListClassGroup.size());
		lStrBldXML.append("</totalClassGroups>");

		for (Integer lInt = 0; lInt < lListClassGroup.size(); lInt++) {
			lStrBldXML.append("<ClassGroups>");
			lStrBldXML.append(lListClassGroup.get(lInt).toString());
			lStrBldXML.append("</ClassGroups>");
		}
		
		lStrBldXML.append("<subBillGroupOrNot>");
		lStrBldXML.append(lObjMstDcpsBillGroup.getSubBGOrNot());
		lStrBldXML.append("</subBillGroupOrNot>");

		lStrBldXML.append("<empsInBGOrNot>");
		lStrBldXML.append(lBlEmpsInBGOrNot);
		lStrBldXML.append("</empsInBGOrNot>");
		
		lStrBldXML.append("<contriInBGOrNot>");
		lStrBldXML.append(lBlContriExistInTheBillGroupFlag);
		lStrBldXML.append("</contriInBGOrNot>");
		
		// Two new flags added.
		lStrBldXML.append("<postsInBGOrNot>");
		lStrBldXML.append(lBlPostsInBGOrNot);
		lStrBldXML.append("</postsInBGOrNot>");
		
		lStrBldXML.append("<billsGeneratedInBGOrNot>");
		lStrBldXML.append(lBlBillsGeneratedInBGOrNot);
		lStrBldXML.append("</billsGeneratedInBGOrNot>");
		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;

	}

	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	//added by shailesh
	
	public ResultObject findSubstitutePair(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		try {
			setSessionInfo(inputMap);
			
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

			
			DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO DcpsCommonDAOObj = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			String lStrDDOCode = DcpsCommonDAOObj.getDdoCode(SessionHelper.getPostId(inputMap));
			logger.info("lStrDDOCode "+lStrDDOCode);
			String dsgnName = "";
			Long dcpsEmpId = null;
			String name = "";
			Object rowList[] = null;
						
			String lStrBillGrupId = StringUtility.getParameter("billGroupId", request).trim();
			String lStrTypeOfAttachDetach = StringUtility.getParameter("typeOfAttachDetach", request ).trim();
			String lStrDcpsEmpId = StringUtility.getParameter("dcpsEmpId", request ).trim();
			logger.info("lStrBillGrupId "+lStrBillGrupId);
			logger.info("lStrTypeOfAttachDetach "+lStrTypeOfAttachDetach);
			logger.info("lStrDcpsEmpId "+lStrDcpsEmpId);
			
			//List empList = null;
			
			//empList = lObjDdoBillGroupDAO.getEmpListForBGIdNull(lStrDDOCode, displayTag);
			
			String subDcpsEmpId = lObjDdoBillGroupDAO.getSubsPairFromdcpsEmpId(lStrDcpsEmpId);
			
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<genPostId>");			
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(subDcpsEmpId);
			lStrBldXML.append("]]>");			
			lStrBldXML.append("</genPostId>");
			lStrBldXML.append("</XMLDOC>");
			String lStrTempResult = null;				
			gLogger.info("subDcpsEmpId "+subDcpsEmpId);
			lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();

			inputMap.put("ajaxKey", lStrTempResult);
			gLogger.info("post id generated.."+lStrTempResult);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

			
		}
		catch(Exception e){
			
		}
		return resObj;
	}
}
