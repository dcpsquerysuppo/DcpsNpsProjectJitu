package com.tcs.sgv.dcps._TDSWebService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

public class TDSWSServExecutor extends ServiceImpl
{
	private final Log gLogger = LogFactory.getLog(getClass());
	//authenticateEmpForTDS
	public ResultObject authenticateEmpForTDS(Map objectArgs)
	{
		gLogger.info("Inside Get generateBillPortalDataForEmp");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");

		String userName="";
		String md5Pwd="";
		String userType="";
		try
		{
			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");

			userName = objectArgs.get("userName").toString();
			gLogger.info("userName is "+userName);

			md5Pwd = objectArgs.get("md5Pwd").toString();
			gLogger.info("md5Pwd is "+md5Pwd);
			
			userType = objectArgs.get("userType").toString();
			gLogger.info("userType is "+userType);

			TDSEmpAuthValueObjet tdsEmpAuth = new TDSEmpAuthValueObjet();
			TDSWSDAOImpl lObjTDSWSDAOImpl = new TDSWSDAOImpl(null ,serv.getSessionFactory());
			List lstTdsEmpAuth=null;
			lstTdsEmpAuth = lObjTDSWSDAOImpl.getCountOfValidatedUser(userName,md5Pwd,userType);
			gLogger.info("lstTdsEmpAuth.size: "+lstTdsEmpAuth.size());
			if(lstTdsEmpAuth.size()>0)
			{
				Object Obj[];
				Obj = (Object[]) lstTdsEmpAuth.get(0);
				tdsEmpAuth.setUserName(Obj[0]!=null ? Obj[0].toString() :"NA");
				tdsEmpAuth.setMd5Pwd(Obj[1]!=null ? Obj[1].toString() :"NA");
				tdsEmpAuth.setMobileNo(Obj[2]!=null ? Obj[2].toString() :"NA");
				tdsEmpAuth.setEmailId(Obj[3]!=null ? Obj[3].toString() :"NA");
				tdsEmpAuth.setUserPersonalName(Obj[4]!=null ? Obj[4].toString() :"NA");
				tdsEmpAuth.setAuthFlag("success");
			}
			else
			{
				tdsEmpAuth.setUserName(userName);
				tdsEmpAuth.setMd5Pwd(md5Pwd);
				tdsEmpAuth.setMobileNo("NA");
				tdsEmpAuth.setEmailId("NA");
				tdsEmpAuth.setUserPersonalName("NA");
				tdsEmpAuth.setAuthFlag("failure");
			}
			objectArgs.put("tdsEmpAuth", tdsEmpAuth);
		}
		catch(Exception e)
		{
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			gLogger.error("Error in loadEmpDtlsDdoWise "+ e);
		}

		return resultObject;
	}
	//get user info
	public ResultObject getUserInfoForTDS(Map objectArgs)
	{
		gLogger.info("Inside Get getUserInfoForTDS");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");

		String sevarthId="";

		try
		{
			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");

			sevarthId = objectArgs.get("sevarthId").toString();
			gLogger.info("sevarthId is "+sevarthId);



			TDSEmpInfoValueObject tdsEmpInfo = new TDSEmpInfoValueObject();
			TDSWSDAOImpl lObjTDSWSDAOImpl = new TDSWSDAOImpl(null ,serv.getSessionFactory());
			List lstTdsEmpInfo=null;
			lstTdsEmpInfo = lObjTDSWSDAOImpl.getuserInfoForTCS(sevarthId);
			gLogger.info("lstTdsEmpAuth.size: "+lstTdsEmpInfo.size());
			if(lstTdsEmpInfo.size()>0)
			{
				Object Obj[];
				Obj = (Object[]) lstTdsEmpInfo.get(0);
				tdsEmpInfo.setSevarthId(Obj[0]!=null ? Obj[0].toString() :"NA");
				tdsEmpInfo.setGender(Obj[1]!=null ? Obj[1].toString() :"NA");
				tdsEmpInfo.setDateOfBirth(Obj[2]!=null ? Obj[2].toString() :"NA");
				tdsEmpInfo.setOfficeEmailId(Obj[3]!=null ? Obj[3].toString() :"NA");
				tdsEmpInfo.setCadre(Obj[4]!=null ? Obj[4].toString() :"NA");
				tdsEmpInfo.setGroup(Obj[5]!=null ? Obj[5].toString() :"NA");
				tdsEmpInfo.setDateOfjoiningMHGov(Obj[6]!=null ? Obj[6].toString() :"NA");
				tdsEmpInfo.setDateOfJoiningCurrentcadre(Obj[7]!=null ? Obj[7].toString() :"NA");
				tdsEmpInfo.setCurentPost(Obj[8]!=null ? Obj[8].toString() :"NA");
				tdsEmpInfo.setCurrentOffice(Obj[9]!=null ? Obj[9].toString() :"NA");
				tdsEmpInfo.setFirstPostDesig(Obj[10]!=null ? Obj[10].toString() :"NA");
				tdsEmpInfo.setParentFieldDept(Obj[11]!=null ? Obj[11].toString() :"NA");
				tdsEmpInfo.setInfoFoundFlag("success");
			}
			else
			{
				tdsEmpInfo.setSevarthId(sevarthId);
				tdsEmpInfo.setInfoFoundFlag("failure");
			}
			objectArgs.put("tdsEmpInfo", tdsEmpInfo);
		}
		catch(Exception e)
		{
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			gLogger.error("Error in loadEmpDtlsDdoWise "+ e);
		}

		return resultObject;
	}
	//update User Info mobile no and email id
	public ResultObject updateUserInfoForTDS(Map objectArgs)
	{
		gLogger.info("Inside updateUserInfoForTDS");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");

		String userName="";
		String userType="";
		String mobileNo="";
		String emailId="";

		try
		{
			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");

			userName = objectArgs.get("userName").toString();
			gLogger.info("sevarthId is "+userName);
			
			userType = objectArgs.get("userType").toString();
			gLogger.info("userType is "+userType);
			
			mobileNo = objectArgs.get("mobileNo").toString();
			gLogger.info("mobileNo is "+mobileNo);
			
			emailId = objectArgs.get("emailId").toString();
			gLogger.info("emailId is "+emailId);



			TDSEmpInfoUpdateValueObject tdsEmpInfoUpdate = new TDSEmpInfoUpdateValueObject();
			TDSWSDAOImpl lObjTDSWSDAOImpl = new TDSWSDAOImpl(null ,serv.getSessionFactory());
			
			//int array for flags (number of rows updated 0 index mobile; 1 index email)
			int arrFlag[] = new int[2];
			arrFlag = lObjTDSWSDAOImpl.updateUserInfoForTDS(userName,userType,mobileNo,emailId);
			gLogger.info("arrFlag values: "+Arrays.toString(arrFlag));
			tdsEmpInfoUpdate.setSevarthId(userName);
			tdsEmpInfoUpdate.setUserType(userType);
			tdsEmpInfoUpdate.setMobileNo(mobileNo);
			tdsEmpInfoUpdate.setEmailId(emailId);
			//check mobile no updated or not
			if(arrFlag[0]>0)
			{
				tdsEmpInfoUpdate.setMobileNoUpdateFlag("mobile number updated Successfully");
			}
			else
			{
				tdsEmpInfoUpdate.setMobileNoUpdateFlag("mobile number not updated");
			}
			//check email updated or not
			if(arrFlag[1]>0)
			{
				tdsEmpInfoUpdate.setEmailIdUpdateFlag("email updated Successfully");
			}
			else
			{
				tdsEmpInfoUpdate.setEmailIdUpdateFlag("email not updated");
			}
			objectArgs.put("tdsEmpInfoUpdate", tdsEmpInfoUpdate);
		}
		catch(Exception e)
		{
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			gLogger.error("Error in loadEmpDtlsDdoWise "+ e);
		}

		return resultObject;
	}
}
