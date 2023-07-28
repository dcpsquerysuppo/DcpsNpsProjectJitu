

package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSNewRegistrationServiceImpl
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

import au.id.jericho.lib.html.Logger;

import com.ibm.db2.jcc.am.po;
import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
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
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAO;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAOImpl;
import com.tcs.sgv.dcps.dao.DdoInfoDAO;
import com.tcs.sgv.dcps.dao.DdoInfoDAOImpl;
import com.tcs.sgv.dcps.dao.DdoOfficeDAOImpl;
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.dao.EmpBankUpdateDao;
import com.tcs.sgv.dcps.dao.EmpBankUpdateDaoImpl;
//import com.tcs.sgv.dcps.dao.NewDDOTreasuryMappingDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAO;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.HstEmp;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.dcps.valueobject.RltDdoAsst;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.eis.dao.CmnlookupMstDAOImpl;
import com.tcs.sgv.eis.dao.EmpStatisticsDDOwiseDAOImpl;
import com.tcs.sgv.eis.dao.GradDesgScaleMapDAO;
import com.tcs.sgv.eis.dao.GradeMasterDAO;
import com.tcs.sgv.eis.dao.HrPayOfficePostMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadPostmpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderMstDAOImpl;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.dao.PsrPostMpgDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.eis.valueobject.HrEisGdMpg;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;
import com.tcs.sgv.eis.valueobject.HrEisSgdMpg;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;
import com.tcs.sgv.eis.valueobject.HrPayOfficepostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadPostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderMst;
import com.tcs.sgv.eis.valueobject.HrPayPaybill;
import com.tcs.sgv.eis.valueobject.HrPayPsrPostMpg;
import com.tcs.sgv.eis.valueobject.MstPayrollDesignationMst;
import com.tcs.sgv.ess.dao.OrgDesignationMstDao;
import com.tcs.sgv.ess.dao.OrgDesignationMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDao;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.ess.valueobject.OrgGradeMst;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;
import com.tcs.sgv.user.dao.AdminOrgPostDtlDao;
import com.tcs.sgv.user.dao.AdminOrgPostDtlDaoImpl;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;
import com.tcs.sgv.wf.exception.PostIdNotFoundException;
import com.tcs.sgv.dcps.valueobject.HrPaySubstituteEmpMpg;
import com.tcs.sgv.eis.dao.OtherDetailDAOImpl;
import com.tcs.sgv.eis.valueobject.HrEisOtherDtls;

public class NewDDOTreasuryMappingServiceImpl extends ServiceImpl 
{

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

	public ResultObject getDdoOficeTreasuryName(Map objectArgs)
	{
		gLogger.info("inside getDdoOficeTreasuryName");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String ddoCode=null;		

		try {
			setSessionInfo(objectArgs);			
			ddoCode=StringUtility.getParameter("ddoCode", request).trim();
			gLogger.info("--------ddoCode--------:"+ddoCode);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			//NewDDOTreasuryMappingDAOImpl mappingDAo=new NewDDOTreasuryMappingDAOImpl(null, serv.getSessionFactory());
			String flag=lObjDcpsCommonDAO.checkDDOCodePresent(ddoCode);
			List oficeTreasuryList=lObjDcpsCommonDAO.getDDOoficeTreasury(ddoCode);

			String officeName="N";
			String treasuryName="N";
			String lStrResult="";
			String lSBStatus="";
			StringBuilder lStrBldXML = new StringBuilder();
			if(flag.equals("YES")){


				if (oficeTreasuryList != null && oficeTreasuryList.size() > 0)
				{
					Iterator IT = oficeTreasuryList.iterator();
					Object[] lObj = null;
					while (IT.hasNext())
					{					
						lObj = (Object[]) IT.next();
						officeName =lObj[0].toString();
						gLogger.info("**************officeName*************" +officeName);
						treasuryName= lObj[1].toString();
						gLogger.info("***********ddoTreasury*********" +treasuryName);
					}
				}
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<ddoOffice>");
				lStrBldXML.append(officeName);
				lStrBldXML.append("</ddoOffice>");
				lStrBldXML.append("<ddoTreasury>");
				lStrBldXML.append(treasuryName);
				lStrBldXML.append("</ddoTreasury>");
				lStrBldXML.append("</XMLDOC>");
				lSBStatus = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			}else{


				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<ddoOffice>");
				lStrBldXML.append("NO");
				lStrBldXML.append("</ddoOffice>");
				lStrBldXML.append("<ddoTreasury>");
				lStrBldXML.append("NO");
				lStrBldXML.append("</ddoTreasury>");
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


	public ResultObject getNewTreasuryName(Map objectArgs)
	{
		gLogger.info("inside getNewTreasuryName");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String ddoCode=null;		

		try {
			setSessionInfo(objectArgs);			
			ddoCode=StringUtility.getParameter("ddoCode", request).trim();
			gLogger.info("--------ddoCode--------:"+ddoCode);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			//NewDDOTreasuryMappingDAOImpl mappingDAo=new NewDDOTreasuryMappingDAOImpl(null, serv.getSessionFactory());
			String flag=lObjDcpsCommonDAO.checkDDOCodePresent(ddoCode);
			List newTreasuryNameList=lObjDcpsCommonDAO.getNewTreasury(ddoCode);
			gLogger.info("newTreasuryName"+newTreasuryNameList.size());
			String treausryId="N";
			String treasuryName="N";
			String lStrResult="";
			String lSBStatus="";
			StringBuilder lStrBldXML = new StringBuilder();
			if(flag.equals("YES")){


				if (newTreasuryNameList != null && newTreasuryNameList.size() > 0)
				{
					Iterator IT = newTreasuryNameList.iterator();
					Object[] lObj = null;
					while (IT.hasNext())
					{					
						lObj = (Object[]) IT.next();
						treausryId =lObj[0].toString();
						gLogger.info("**************officeName*************" +treausryId);
						treasuryName= lObj[1].toString();
						gLogger.info("***********ddoTreasury*********" +treasuryName);
					}
				}
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<ddoOffice>");
				lStrBldXML.append(treausryId);
				lStrBldXML.append("</ddoOffice>");
				lStrBldXML.append("<ddoTreasury>");
				lStrBldXML.append(treasuryName);
				lStrBldXML.append("</ddoTreasury>");
				lStrBldXML.append("</XMLDOC>");
				lSBStatus = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			}else{


				lStrBldXML.append("<XMLDOC>");				
				lStrBldXML.append("<newTreasuryName>");
				lStrBldXML.append("NO");
				lStrBldXML.append("</newTreasuryName>");
				lStrBldXML.append("</XMLDOC>");
				lSBStatus = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			}
			gLogger.info("*************newTreasuryName ajax***********************" + lStrResult);
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





	public ResultObject getTreasuryList(Map objectArgs)
	{
		gLogger.info("inside getReasonListForOffice");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String officeType=null;		
		List<ComboValuesVO>  treasuries =  new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO = new ComboValuesVO();
		cmbVO.setId("-1");
		cmbVO.setDesc("Select");
		treasuries.add(cmbVO);
		try {
			setSessionInfo(objectArgs);		
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			treasuries=lObjDcpsCommonDAO.getTreasuryList();
			gLogger.info("--------treasuries size--------:"+treasuries.size());
			String AjaxResult = new AjaxXmlBuilder().addItems(treasuries, "desc", "id").toString();		
			gLogger.info("*************newTreasuryName ajax***********************" + AjaxResult);
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

	public ResultObject getSubTreasuryList(Map objectArgs)
	{
		gLogger.info("inside getSubTreasuryList");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String officeType=null;		
		List<ComboValuesVO>  treasuries =  new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO = new ComboValuesVO();
		cmbVO.setId("-1");
		cmbVO.setDesc("Select");
		treasuries.add(cmbVO);
		try {
			setSessionInfo(objectArgs);	
			Long treasuryId=Long.parseLong(StringUtility.getParameter("treasuryId", request).trim());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			treasuries=lObjDcpsCommonDAO.getSubTreasuryList(treasuryId);
			gLogger.info("--------getSubTreasuryList size--------:"+treasuries.size());
			String AjaxResult = new AjaxXmlBuilder().addItems(treasuries, "desc", "id").toString();		
			gLogger.info("*************getSubTreasuryList ajax***********************" + AjaxResult);
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

	public ResultObject getNewDDOCode(Map objectArgs)
	{

		gLogger.info("inside getNewTreasuryName");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String oldDdoCode=null;
		String newLocId="";
		String newDDOCode="";
		try {
			setSessionInfo(objectArgs);			
			oldDdoCode=StringUtility.getParameter("oldDDOCode", request).trim();
			newLocId=StringUtility.getParameter("newTresCode", request).trim();
			gLogger.info("--------ddoCode--------:"+oldDdoCode);
			gLogger.info("--------newLocId--------:"+newLocId);
			newDDOCode = newLocId.concat(oldDdoCode.substring(4,10));
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			//NewDDOTreasuryMappingDAOImpl mappingDAo=new NewDDOTreasuryMappingDAOImpl(null, serv.getSessionFactory());
			String flag=lObjDcpsCommonDAO.checkDDOCodePresent(newDDOCode);			
			gLogger.info("--------flag--------:"+flag);
			String treausryId="N";
			String treasuryName="N";
			String lStrResult="";
			String lSBStatus="";
			StringBuilder lStrBldXML = new StringBuilder();
			if(flag.equals("NO")){				

				lStrBldXML.append("<XMLDOC>");				
				lStrBldXML.append("<newDDOCode>");
				lStrBldXML.append(newDDOCode);
				lStrBldXML.append("</newDDOCode>");
				lStrBldXML.append("</XMLDOC>");
				lSBStatus = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			}else{
				/*Long locationCode= Long.parseLong(oldDdoCode.substring(4,10));
				locationCode=locationCode+1;
				newDDOCode=newLocId.concat(locationCode.toString());
				gLogger.info("--------newDDOCode in flag no--------:"+newDDOCode);*/
				Long newDDOCodeTreasury=Long.parseLong(lObjDcpsCommonDAO.getMaxDDOCode(newLocId));
				newDDOCodeTreasury=newDDOCodeTreasury+1;
				gLogger.info("--------newDDOCodeTreasury--------:"+newDDOCodeTreasury);
				lStrBldXML.append("<XMLDOC>");				
				lStrBldXML.append("<newTreasuryName>");
				lStrBldXML.append(newDDOCodeTreasury);
				lStrBldXML.append("</newTreasuryName>");
				lStrBldXML.append("</XMLDOC>");
				lSBStatus = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			}
			gLogger.info("*************newTreasuryName ajax***********************" + lStrResult);
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

	public ResultObject updateNewDDOTreasuryDtls(Map objectArgs)
	{
		gLogger.info("inside updateNewDDOTreasuryDtls");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		String oldDdoCode=null;	
		String newDdoCode=null;
		Long treasuryId=0l;
		int count=0;
		String flag="";
		String ddoPostId="";
		String asstDdoPostId="";
		String postTableName="";
		String newDdoPostId="";
		String newAsstDdoPostId="";
		int astuserNameCount=0;
		Long newDDocodeDelete=0l;
		try {
			setSessionInfo(objectArgs);			
			oldDdoCode=StringUtility.getParameter("oldDDOCode", request).trim();
			newDdoCode=StringUtility.getParameter("newDDOCode", request).trim();
			flag=StringUtility.getParameter("flag", request).trim();
			gLogger.info("--------flag in update-------:"+flag);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			gLogger.info("--------newDdoCode--------:"+newDdoCode);
			gLogger.info("--------oldDdoCode--------:"+oldDdoCode);
			String astUserName=lObjDcpsCommonDAO.getAstUsername(oldDdoCode);
			String ddoUserName=lObjDcpsCommonDAO.getDDOUsername(oldDdoCode,newDdoCode);
			gLogger.info("--------astUserName--------:"+astUserName);
			gLogger.info("--------astUserName.substring(0, 9)--------:"+astUserName.substring(0, 10));
			if(oldDdoCode.equals(astUserName.substring(0, 10))){
				String newASTuserName=newDdoCode.concat("_AST");
				gLogger.info("--------newASTuserName--------:"+newASTuserName);
				String checkFlag=lObjDcpsCommonDAO.checkAstUserName(newASTuserName);
				if(checkFlag.equals("YES")){
					String deleteExistnewUserName=newASTuserName.concat("_delete");
					gLogger.info("--------deleteExistnewUserName--------:"+deleteExistnewUserName);
					astuserNameCount=lObjDcpsCommonDAO.updateAstUserName(deleteExistnewUserName,newASTuserName);
					astuserNameCount=lObjDcpsCommonDAO.updateAstUserName(newASTuserName,astUserName);
				}else
				astuserNameCount=lObjDcpsCommonDAO.updateAstUserName(newASTuserName,astUserName);
			}

			List postIdList= lObjDcpsCommonDAO.getPostId(oldDdoCode);
			if (postIdList != null && postIdList.size() > 0)
			{
				Iterator IT = postIdList.iterator();
				Object[] lObj = null;
				while (IT.hasNext())
				{					
					lObj = (Object[]) IT.next();
					ddoPostId =lObj[0].toString();
					gLogger.info("**************ddoPostId*************" +ddoPostId);
					asstDdoPostId= lObj[1].toString();
					gLogger.info("***********asstDdoPostId*********" +asstDdoPostId);
				}
			}
			List newDDOcodePostIdList = lObjDcpsCommonDAO.getnewDDOPostId(newDdoCode);
			if (postIdList != null && postIdList.size() > 0)
			{
				Iterator IT = postIdList.iterator();
				Object[] lObj = null;
				while (IT.hasNext())
				{					
					lObj = (Object[]) IT.next();
					newDdoPostId =lObj[0].toString();
					gLogger.info("**************ddoPostId*************" +ddoPostId);
					newAsstDdoPostId= lObj[1].toString();
					gLogger.info("***********asstDdoPostId*********" +asstDdoPostId);
				}
			}

			String strTables[] = {"HR_PAY_CMP_DDO_CODE","HR_PAY_PAYBILLSCHDLR_RESULT",
					"HR_PAY_PAYBILL_SCHEDULER","HST_ORG_DDO_MST","ORG_DDO_MST","MST_DCPS_BILL_GROUP","MST_DCPS_DDO_INFO","MST_DCPS_DDO_OFFICE",
					"ORG_DDO_OTHER_DTLS","RLT_DDO_OFFICE","TEMP_DDO_MAP",
					"RLT_DCPS_DDO_CONTRIBUTION","RLT_DCPS_DDO_SCHEMES",
					"RLT_DCPS_PHY_FORM_STATUS","HR_PAY_CMP_RECORD_MST","HR_PAY_VOUCHER_DTLS_TEMP",
					"HST_DCPS_EMP_DETAILS",
					"HST_CHANGE_PAYSCALE_DTLS",
					"MST_EMP_GPF_ACC","HST_DCPS_CHANGES","TRN_PNSNPROC_INWARDPENSION",
					"TRN_IFMS_BEAMS_INTEGRATION","HR_CUSTODIAN_TYPE_MST","RLT_DDO_ORG","TRN_DCPS_CHANGES",
					"TRN_BILL_REGISTER","MST_GPF_EMP","MST_DCPS_EMP","MST_DCPS_EMP_DETAILS"};
		/*commented table on DCPS	"MST_DCPS_TREASURYNET_DATA",
			"MST_DCPS_TREASURYNET_DATA_TEMP,"TRN_DCPS_CONTRIBUTION","MST_DCPS_CONTRI_VOUCHER_DTLS","TRN_DCPS_TERMINAL_DTLS","TRN_DCPS_MISSING_CREDITS_DTLS","RLT_DCPS_SIXPC_YEARLY""*/
String strCronTables[] = {"ORG_DDO_MST","MST_DCPS_DDO_OFFICE","RLT_DDO_ORG"};
ArrayList<String> crontableNameList = new ArrayList<String>();
String crontableName="";
for (int i=0;i<strCronTables.length;i++){

	crontableNameList.add(strCronTables[i]);
}
		//	String postStrTables[]={"ORG_DDO_MST","ORG_USERPOST_RLT"};
			ArrayList<String> tablesList = new ArrayList<String>();
			//ArrayList<String> postTablesList = new ArrayList<String>();
			String tableName="";
			for (int i=0;i<strTables.length;i++){

				tablesList.add(strTables[i]);
			}
			/*for (int i=0;i<postStrTables.length;i++){

				postTablesList.add(postStrTables[i]);
			}*/
			
				if(flag.equals("Yes")){
					for(int i=0;i<tablesList.size();i++){
						gLogger.info("strComponents.size()*****"+tablesList.size());
						tableName=tablesList.get(i).toString();
						gLogger.info("tableName*****"+tableName);
					//count = lObjDcpsCommonDAO.updateDDOCode(tableName,oldDdoCode,newDdoCode);
					count=count+lObjDcpsCommonDAO.updateDDOCode(tableName,oldDdoCode,newDdoCode);
				}
			}
				else{
				
					String locationCode= lObjDcpsCommonDAO.getLocationCode(oldDdoCode);
					gLogger.info("locationCode*****"+locationCode);
					String ddoCheckFlag = lObjDcpsCommonDAO.checkDDOCodePresent(newDdoCode);
					if(ddoCheckFlag.equals("YES")){
						String newDDocodeDeleteChk=lObjDcpsCommonDAO.checkdeleteDDOcode();
						if(newDDocodeDeleteChk.equals("YES")){
							newDDocodeDelete = lObjDcpsCommonDAO.getdeleteDDOcode();
							newDDocodeDelete=newDDocodeDelete+1;
						} else{
					newDDocodeDelete=1111111111l;
						}
				
					}
					for(int j=0;j<crontableNameList.size();j++){
						gLogger.info("crontableNameList.size()*****"+crontableNameList.size());
						crontableName=crontableNameList.get(j).toString();
						gLogger.info("crontableName*****"+crontableName);
						gLogger.info("newDDocodeDelete*****"+newDDocodeDelete);
						gLogger.info("newDdoCode in 2nd option*****"+newDdoCode);
							count=count+lObjDcpsCommonDAO.updateNewDDOCodeDelete(crontableName,newDDocodeDelete,newDdoCode);
					}
					
					//lObjDcpsCommonDAO.updateOldDDOCodePostIdLocId(oldDdoCode,ddoPostId,newDdoPostId);
					//count = count+lObjDcpsCommonDAO.updatenewTreasuryDDOCode(tableName,oldDdoCode,newDdoCode,locationCode,ddoPostId,asstDdoPostId);--for new ddocode
					for(int k=0;k<tablesList.size();k++){
						gLogger.info("strComponents.size()*****"+tablesList.size());
						tableName=tablesList.get(k).toString();
						gLogger.info("tableName*****"+tableName);
					count=count+lObjDcpsCommonDAO.updateDDOCode(tableName,oldDdoCode,newDdoCode);
					}
					
						
					
					
					//lObjDcpsCommonDAO.updatenewTreasuryAsstDDOPostId(asstDdoPostId,newAsstDdoPostId);
				}
				gLogger.info("count*****"+count);
			
			
			if(count>0){
				objectArgs.put("msg", "updated successfully");
				gLogger.info("count---------" + count);
				objRes.setResultValue(objectArgs);
				objRes.setResultCode(ErrorConstants.SUCCESS);
				objRes.setViewName("newDDOTreasuryMapping");
			}

			//NewDDOTreasuryMappingDAOImpl mappingDAo=new NewDDOTreasuryMappingDAOImpl(null, serv.getSessionFactory());

			//String flag=lObjDcpsCommonDAO.checkDDOCodePresent(ddoCode);

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	} 


}