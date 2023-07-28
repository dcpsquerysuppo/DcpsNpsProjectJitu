package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.EmpGPFDetailsDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;

public class BankCertificatesServiceImpl extends ServiceImpl{

	/* Global Variable for Logger Class */
	private final Log logger = LogFactory.getLog(getClass());

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
	.getBundle("resources/dcps/BankCertificateLabels");

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

	public ResultObject viewSampleABankCertificate(Map inputMap) throws Exception{
		logger.info("Inside Get viewEmpGPFSeriesReport");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Long lLngLocId = null;
		OrgDdoMst lObjDdoMst = null;
		String lStrDdocode = null;
		String lFlag = null;
		String offcName = "";
		String offceAdress1 = "";
		String offceAdress2 = "";
		String ddoname = "";
		String ddoDsgnName = "";
		String certi = "";
		String dddoPesrnalName = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 

		Date currDate = new Date();

		try{
			PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());    		
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	

			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			String locId=loginDetailsMap.get("locationId").toString();

			if(!"".equals(locId)){
				lLngLocId = Long.parseLong(locId);
			}
			certi = request.getParameter("certi");

			List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(lLngLocId);
			List lLstDDOOfcMSt =  lObjEmpGPFDetailsDAOImpl.getDDODOfcMst(locId);
			if(lLstDDOOfcMSt != null){
				Object[] obj  = (Object[])lLstDDOOfcMSt.get(0);
				if(obj[1]!= null){
					offcName = obj[1].toString();
				}
				if(obj[2]!= null){
					offceAdress1 = obj[2].toString();
				}
				if(obj[3]!= null){
					offceAdress2 = obj[3].toString();
				}
				inputMap.put("offcName",offcName);
				inputMap.put("offceAdress1",offceAdress1);
				inputMap.put("offceAdress2",offceAdress2);
			}
			if(!lLstDDOList.isEmpty() && lLstDDOList != null)
				lObjDdoMst = lLstDDOList.get(0);
			lStrDdocode = lObjDdoMst.getDdoCode();
			//offceAdress = lObjDdoMst.getDdoOffice();
			ddoname = lObjDdoMst.getDdoName();
			dddoPesrnalName = lObjDdoMst.getDdoPersonalName();
			inputMap.put("lStrDdocode",lStrDdocode);

			inputMap.put("ddoname",ddoname);
			inputMap.put("ddoDsgnName",ddoDsgnName);
			inputMap.put("currDate",sdf.format(currDate));
			inputMap.put("certi",certi);
			inputMap.put("dddoPesrnalName",dddoPesrnalName);

			//inputMap.put("ddoname",ddoname);

			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object
			resultObject.setViewName("bankCertiSampleA");//set view name
		}	

		catch(Exception e){
			logger.info("error "+e);

		}
		return resultObject;
	}

	public ResultObject getUidEidEmployees(Map inputMap) throws Exception{
		logger.info("Inside Get viewEmpGPFSeriesReport");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Long lLngLocId = null;
		OrgDdoMst lObjDdoMst = null;
		String lStrDdocode = null;
		String lFlag = null;
		String offcName = "";
		String offceAdress1 = "";
		String offceAdress2 = "";
		String ddoname = "";
		String ddoDsgnName = "";
		String certi = "";
		String dddoPesrnalName = "";
		List uidEidLst = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 

		Date currDate = new Date();

		try{
			PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());    		
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	

			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			String locId=loginDetailsMap.get("locationId").toString();

			if(!"".equals(locId)){
				lLngLocId = Long.parseLong(locId);
			}
			certi = request.getParameter("certi");

			List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(lLngLocId);
			List lLstDDOOfcMSt =  lObjEmpGPFDetailsDAOImpl.getDDODOfcMst(locId);
			if(lLstDDOOfcMSt != null){
				Object[] obj  = (Object[])lLstDDOOfcMSt.get(0);
				if(obj[1]!= null){
					offcName = obj[1].toString();
				}
				if(obj[2]!= null){
					offceAdress1 = obj[2].toString();
				}
				if(obj[3]!= null){
					offceAdress2 = obj[3].toString();
				}
				inputMap.put("offcName",offcName);
				inputMap.put("offceAdress1",offceAdress1);
				inputMap.put("offceAdress2",offceAdress2);
			}
			if(!lLstDDOList.isEmpty() && lLstDDOList != null)
				lObjDdoMst = lLstDDOList.get(0);
			lStrDdocode = lObjDdoMst.getDdoCode();

			uidEidLst = lObjEmpGPFDetailsDAOImpl.getNotUidEidEmployees(lStrDdocode);

			ddoname = lObjDdoMst.getDdoName();
			dddoPesrnalName = lObjDdoMst.getDdoPersonalName();
			inputMap.put("lStrDdocode",lStrDdocode);
			inputMap.put("uidEidLst",uidEidLst);

			inputMap.put("ddoname",ddoname);
			inputMap.put("ddoDsgnName",ddoDsgnName);
			inputMap.put("currDate",sdf.format(currDate));
			inputMap.put("certi",certi);
			inputMap.put("dddoPesrnalName",dddoPesrnalName);

			//inputMap.put("ddoname",ddoname);

			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object
			resultObject.setViewName("UidEidReport");//set view name
		}	

		catch(Exception e){
			logger.info("error "+e);

		}
		return resultObject;
	}


	public ResultObject saveUIDReason(Map<String, Object> inputMap) throws Exception{
		logger.info("Inside Get viewEmpGPFSeriesReport");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Long lLngLocId = null;
		OrgDdoMst lObjDdoMst = null;
		String lStrDdocode = null;
		String lFlag = null;
		try{
			PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());    		
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			String locId=loginDetailsMap.get("locationId").toString();
			long userID = Long.parseLong(loginDetailsMap.get("userId").toString());
			long setPostId = Long.parseLong(loginDetailsMap.get("loggedInPost").toString());

			String DDOCode = StringUtility.getParameter("DDOCode",request) != null ? StringUtility.getParameter("DDOCode",request).toString() :"";
			String sevaarthId = StringUtility.getParameter("sevaarthId",request) != null ? StringUtility.getParameter("sevaarthId",request).toString() :"";
			String empID = StringUtility.getParameter("empId",request) != null ? StringUtility.getParameter("empId",request).toString() :"";
			String reason = StringUtility.getParameter("reason",request) != null ? StringUtility.getParameter("reason",request).toString() :"";
			if(!"".equals(locId)){
				lLngLocId = Long.parseLong(locId);
			}
			List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(lLngLocId);
			if(!lLstDDOList.isEmpty() && lLstDDOList != null)
				lObjDdoMst = lLstDDOList.get(0);
			lStrDdocode = lObjDdoMst.getDdoCode();
			String arrsevaarthID[] = null;
			String arrEMPId[] = null;    		
			String reasonArray[] = null;


			if(sevaarthId != "")
				arrsevaarthID = sevaarthId.split("~");
			if(empID != "")
				arrEMPId = empID.split("~");

			if(reason != "")
				reasonArray = reason.split("~");

			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	

			int presentOrNot =  0;
			int i = 0;
			for( i =0; i < arrEMPId.length; i++){
				presentOrNot = lObjEmpGPFDetailsDAOImpl.checkEmpInUidOrNot( arrEMPId[i],  arrsevaarthID[i]);
				if(presentOrNot != 0){
					/*if(arrnewDOB[i].equals("-1"))
    					arrnewDOB[i] = null;*/    				
					lObjEmpGPFDetailsDAOImpl.updateUidReason( arrEMPId[i],  arrsevaarthID[i],reasonArray[i], userID+"");    				
				}
				if(presentOrNot == 0){


					lObjEmpGPFDetailsDAOImpl.insertUidReason( lStrDdocode,  arrEMPId[i],  arrsevaarthID[i],
							userID,reasonArray[i],locId);  
				}
			}
			logger.info("i before  "+i);
			String msg = "";

			logger.info("arrEMPId length "+arrEMPId.length);
			if(i ==  arrEMPId.length)
				msg = "Success";
			else msg ="Failure";

			StringBuffer returnMsg = new StringBuffer();
			returnMsg.append("<msg>");
			returnMsg.append(msg);
			returnMsg.append("</msg>");
			logger.info("msg "+msg);
			Map result = new  HashMap();
			String stateNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", returnMsg.toString()).toString();
			logger.info("stateNameIdStr "+stateNameIdStr);
			result.put("ajaxKey", stateNameIdStr);
			resultObject.setResultValue(result);
			resultObject.setViewName("ajaxData");
		}
		catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in savePFDetails"+e);
		}
		return resultObject;
	}

	public ResultObject getUidEidDtls(Map<String, Object> inputMap) throws Exception{
		logger.info("Inside Get viewEmpGPFSeriesReport");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Long lLngLocId = null;
		OrgDdoMst lObjDdoMst = null;
		String lStrDdocode = null;
		String lFlag = null;
		String offcName = "";
		String offceAdress1 = "";
		String offceAdress2 = "";
		String ddoname = "";
		String ddoDsgnName = "";
		String certi = "";
		String dddoPesrnalName = "";
		List uidEidLst = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 

		Date currDate = new Date();

		try{
			setSessionInfo(inputMap);
			PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());    		
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	

			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			String locId=loginDetailsMap.get("locationId").toString();

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);			

			List lLstParentDept = lObjDcpsCommonDAO
			.getParentDeptForDDO(lStrDdoCode);
			Object[] objParentDept = (Object[]) lLstParentDept.get(0);

			List lLstDesignation = lObjDcpsCommonDAO.getAllDesignation(
					(Long) objParentDept[0], gLngLangId);
			inputMap.put("lLstDesignation", lLstDesignation);

			String filter = request.getParameter("filter");
			String cmbDesig = null;
			if(filter != null){
				if(filter.equals("yes"))
					cmbDesig =  request.getParameter("cmbDesig");
			}

			if(!"".equals(locId)){
				lLngLocId = Long.parseLong(locId);
			}


			List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(lLngLocId);
			List lLstDDOOfcMSt =  lObjEmpGPFDetailsDAOImpl.getDDODOfcMst(locId);

			if(!lLstDDOList.isEmpty() && lLstDDOList != null)
				lObjDdoMst = lLstDDOList.get(0);
			lStrDdocode = lObjDdoMst.getDdoCode();

			uidEidLst = lObjEmpGPFDetailsDAOImpl.getUidEidDtls(lStrDdocode,cmbDesig);

			inputMap.put("cmbDesig",cmbDesig);
			inputMap.put("lStrDdocode",lStrDdocode);
			inputMap.put("uidEidLst",uidEidLst);			


			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object
			resultObject.setViewName("updateUidEidDtls");//set view name
		}	

		catch(Exception e){
			logger.info("error "+e);

		}
		return resultObject;
	}

	public ResultObject updateUidEidDtls(Map<String, Object> inputMap) throws Exception{
		logger.info("Inside Get updateUidEidDtls");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Long lLngLocId = null;
		OrgDdoMst lObjDdoMst = null;
		String lStrDdocode = null;
		String uidLst ;
		String eidLst ;
		String dcpsEmpIDLst ;
		String dcpsEmpIDArr[] = null;
		String uidArry[] = null;
		String eidArry[] = null;
		MstEmp dcpsEmpObj ;
		List uidEidLst = null;		
		int count =0;
		try{
			PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	

			uidLst = StringUtility.getParameter("uID", request);
			eidLst = StringUtility.getParameter("eID", request);
			dcpsEmpIDLst = StringUtility.getParameter("dcpsEmpID", request);


			if(dcpsEmpIDLst != null){
				dcpsEmpIDArr = dcpsEmpIDLst.split("~");
				uidArry = uidLst.split("~");
				eidArry = eidLst.split("~");

				logger.info(" uidArry.length "+uidArry.length);
				logger.info("eidArry.length "+eidArry.length);
			}

			for(int i = 0; i < dcpsEmpIDArr.length;i++){
				dcpsEmpObj = new MstEmp();
				dcpsEmpObj = (MstEmp)lObjNewRegDdoDAO.read(Long.parseLong(dcpsEmpIDArr[i]));
				if((dcpsEmpObj.getUIDNo() != null  && !dcpsEmpObj.getUIDNo().equals(uidArry[i]) )||
						( dcpsEmpObj.getEIDNo() != null &&!dcpsEmpObj.getEIDNo().equals(eidArry[i]) )){
					if(!uidArry[i].equals("NA"))
						dcpsEmpObj.setUIDNo(uidArry[i]);
					else dcpsEmpObj.setUIDNo(null);
					if(!eidArry[i].equals("NA"))
						dcpsEmpObj.setEIDNo(eidArry[i]);
					else dcpsEmpObj.setEIDNo(null);
					lObjNewRegDdoDAO.update(dcpsEmpObj);
					dcpsEmpObj = null;
					count++;
				}
				else {
					if((dcpsEmpObj.getUIDNo() == null && !uidArry[i].equals("NA"))
							|| (dcpsEmpObj.getEIDNo() == null && !eidArry[i].equals("NA")) ){
						if(!uidArry[i].equals("NA"))
							dcpsEmpObj.setUIDNo(uidArry[i]);
						else dcpsEmpObj.setUIDNo(null);
						if(!eidArry[i].equals("NA"))
							dcpsEmpObj.setEIDNo(eidArry[i]);
						else dcpsEmpObj.setEIDNo(null);
						lObjNewRegDdoDAO.update(dcpsEmpObj);
						dcpsEmpObj = null;
						count++;					
					}
				}
			}

			/*Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			String locId=loginDetailsMap.get("locationId").toString();

			if(!"".equals(locId)){
				lLngLocId = Long.parseLong(locId);
			}


			List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(lLngLocId);			

			if(!lLstDDOList.isEmpty() && lLstDDOList != null)
				lObjDdoMst = lLstDDOList.get(0);
			lStrDdocode = lObjDdoMst.getDdoCode();

			uidEidLst = lObjEmpGPFDetailsDAOImpl.getUidEidDtls(lStrDdocode);


			inputMap.put("lStrDdocode",lStrDdocode);
			inputMap.put("uidEidLst",uidEidLst);			
			 */

			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object

			StringBuffer returnMsg = new StringBuffer();
			returnMsg.append("<msg>");
			returnMsg.append("Success");
			returnMsg.append("</msg>");
			returnMsg.append("<count>");
			returnMsg.append(count);
			returnMsg.append("</count>");
			Map result = new  HashMap();
			String stateNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", returnMsg.toString()).toString();
			logger.info("stateNameIdStr "+stateNameIdStr);
			result.put("ajaxKey", stateNameIdStr);
			resultObject.setResultValue(result);
			resultObject.setViewName("ajaxData");
		}	

		catch(Exception e){
			logger.info("error "+e);

		}
		return resultObject;
	}
	
	public ResultObject checkDuplicateUidEid(Map<String, Object> inputMap) throws Exception{
		logger.info("Inside Get updateUidEidDtls");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String uidOrEidType =null;
		String uidOrEid = null;
		int count = 0;
		String msg = "";
		try{
			PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	

			uidOrEidType = StringUtility.getParameter("type", request);
			uidOrEid = StringUtility.getParameter("UIdOrEid", request);
			
			if(uidOrEidType != null && uidOrEid != null){
				count = lObjEmpGPFDetailsDAOImpl.checkDulicateUidEid(uidOrEid,uidOrEidType);			
				if(count >0)
					msg= uidOrEidType+" already present.";
				else msg = "";
			}

			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object

			StringBuffer returnMsg = new StringBuffer();
			returnMsg.append("<msg>");
			returnMsg.append(msg);
			returnMsg.append("</msg>");
			returnMsg.append("<count>");
			returnMsg.append(count);
			returnMsg.append("</count>");
			Map result = new  HashMap();
			String stateNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", returnMsg.toString()).toString();
			logger.info("stateNameIdStr "+stateNameIdStr);
			result.put("ajaxKey", stateNameIdStr);
			resultObject.setResultValue(result);
			resultObject.setViewName("ajaxData");
		}	

		catch(Exception e){
			logger.info("error "+e);

		}
		return resultObject;
	}
}

