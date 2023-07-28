package com.tcs.sgv.pensionpay.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.PropertyValueException;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.common.valueobject.MstBankPay;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;



public class BankMasterServiceImpl extends ServiceImpl {
	Log logger = LogFactory.getLog( getClass() );
	int msg=0;
	public ResultObject insertBankMasterDtls(Map objectArgs) 
	{
		// TODO Auto-generated method stub
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		try
		{
			logger.info("Coming into the Service method insertBankMasterDtls");
			MstBank bankMst = (MstBank)objectArgs.get("bankMst");
			logger.info( "The BankMaster VO is " + bankMst);
			ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
			String editFromVO = objectArgs.get("edit").toString();
			
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long userId=StringUtility.convertToLong(loginDetailsMap.get("userId").toString());
			long dbId=StringUtility.convertToLong(loginDetailsMap.get("dbId").toString());
			String locId="10000";
			langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
			long postId=StringUtility.convertToLong(loginDetailsMap.get("primaryPostId").toString());
			
			long postttId = Long.parseLong(loginDetailsMap.get("loggedInPost").toString()); 
			logger.info("loggedInPost loggedInPost" + postttId);
			
			//BankMasterDAOImpl bankMasterDAO = new BankMasterDAOImpl(HrEisBankMst.class,serv.getSessionFactory());
			
            logger.info("editFromVO insertBankMasterDtls" + editFromVO);
	        logger.info("insertBankMasterDtls Loc Id is:-->" + dbId + " " + locId);
            
	        
	        com.tcs.sgv.pensionpay.service.BankMasterServiceImplHelper helper = new com.tcs.sgv.pensionpay.service.BankMasterServiceImplHelper(serv);
	        
            if(editFromVO!=null && editFromVO.equalsIgnoreCase("Y"))            	
			{
            	logger.info("INSIDE UPDATE");            	
            	helper.updateBankMasterDtls(bankMst, postId, userId);
            	
				objectArgs.put("msg", "Record Updated Successfully");
				msg=1;
			}
			else if(editFromVO!=null && editFromVO.equalsIgnoreCase("N"))
			{
				logger.info("INSIDE CREATE insertBankMasterDtls ********************************* "+objectArgs);
				//IdGenerator idGen = new IdGenerator();
				long bankId = IFMSCommonServiceImpl.getNextSeqNum("mst_bank", objectArgs);
				//long bankId = IDGenerateDelegate.getNextId("MST_BANK_PAY", objectArgs);
				logger.info("INSIDE INSERT BankMasterServiceImplHelper bankId" + bankId);	
				helper.inserBankMasteDtls(bankMst,bankId,langId,dbId,postId,userId,locId);
				objectArgs.put("msg", "Record Inserted Successfully");
			}
            else
			{
				throw new NullPointerException();
			}
			
           
			Map redirectMap = new HashMap();
			redirectMap.put("actionFlag", "getBankView");
			if(msg==1)
				objectArgs.put("MESSAGECODE",300006);
			else
				objectArgs.put("MESSAGECODE",300005);
			objectArgs.put("redirectMap", redirectMap);			
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("BankMaster");
			
			logger.info("INSERTED SUCCESSFULLY insertBankMasterDtls");
		}
		catch(NullPointerException ne)
		{
			logger.info("Null Pointer Exception Ocuures...insertBankMasterDtls");
			//ne.printStackTrace();
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");			
		}
		catch(PropertyValueException pe)
		{
			logger.info("PropertyValueException Ocuures...insertBankMasterDtls");
			//pe.printStackTrace();
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");		
			

		}
		catch(Exception e){
			
			logger.info("Exception Ocuures...insertBankMasterDtls");
			 //e.printStackTrace();
			 objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("errorInsert");
			

		}
		return resultObject;
	}
	
	
	
	public ResultObject checkBankName(Map objectArgs)
	{
		
		logger.info("IN checkBankName Data");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);

        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
        HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
        //Map voToService = (Map)objectArgs.get("voToServiceMap");
        String strbankName = "";
        
        try{
        	
        	com.tcs.sgv.pensionpay.dao.BankMasterDAO bankMasterDAO = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());        	
        	strbankName = StringUtility.getParameter("bankName", request);
        	/*String strbankName = (String)voToService.get("bankName");
            String oldname = (String)voToService.get("oldname");
            logger.info("checkBankName  Old name in service is " + oldname);
            if(oldname!= null && (!oldname.equals("")))        
	  	    {			
        	  oldname= oldname.trim().toLowerCase();
		    }	*/
           StringBuffer propertyList = new StringBuffer();
           strbankName = strbankName.trim().toLowerCase();
           if(!strbankName.equals(""))
           {
        	   Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
        	   long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
        	   List bankName = bankMasterDAO.checkBankName(strbankName,langId);
         
            propertyList.append("<bank-name>");   	
            propertyList.append("<list-size>").append(bankName.size()).append("</list-size>");
            propertyList.append("</bank-name>"); 
            logger.info("Bank Name size " + bankName.size());
           }
          else
          {
        	propertyList.append("<bank-name>");   	
            propertyList.append("<list-size>").append(0).append("</list-size>");
            propertyList.append("</bank-name>");
          }
        
          Map result = new HashMap();
          String bankNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", propertyList.toString()).toString();
          result=objectArgs;
          result.put("ajaxKey", bankNameIdStr);
          
          resultObject.setResultValue(result);
          resultObject.setViewName("ajaxData");                          	  	                          	               	   	                              	          	  
          logger.info("checkBankName After Service Called.........\n");
		}
		catch(Exception e)
		{
			logger.info("Null Pointer Exception Ocuures...insertBankMasterDtls");
			//e.printStackTrace();
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");		
			/*
			logger.info("setViewName-->errorPage");
			Map result = new HashMap();
			result.put("MESSAGECODE",300004);
			resultObject.setResultValue(result);
			resultObject.setThrowable(e);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			e.printStackTrace();*/
		}
		return resultObject;
	}
	
		
	public ResultObject getBankMasterDtls(Map objectArgs)
	{							
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);

        Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
   		
		try
		{
	            ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	            com.tcs.sgv.pensionpay.dao.BankMasterDAO lObjBankMasterDAO = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
	            long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
	            
	            List <MstBank> actionList = lObjBankMasterDAO.getAllBankMasterData(langId);	            	           
	            Map map = new HashMap();
	            map=objectArgs;
	            map.put("actionList", actionList);
	            resultObject.setResultCode(ErrorConstants.SUCCESS);
	            resultObject.setResultValue(map);
	           resultObject.setViewName("BankMasterView");
	            //resultObject.setViewName("OtherEditListMergedNew");
	            //resultObject.setViewName("BulkAllowances");
		}
		catch(Exception e)
		{		
			logger.info("Null Pointer Exception Ocuures...insertBankMasterDtls");
			//e.printStackTrace();
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");		
			/*
			logger.info("setViewName-->errorPage");
			Map result = new HashMap();
			result.put("MESSAGECODE",300004);
			resultObject.setResultValue(result);
			resultObject.setThrowable(e);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			e.printStackTrace();*/
		}
	
		return resultObject;
	}

	
	public ResultObject getBankData(Map objectArgs)
	{
		logger.info("getBankData IN Bank Master Data");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		
		try
		{
			
	            ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		
	            Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
				long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
				//Map voToService = (Map)objectArgs.get("voToServiceMap");
				String editFlag = StringUtility.getParameter("edit", request);
				
		       // logger.info("editFromVO in BankService getBankData" + voToService.get("edit"));
				
		        com.tcs.sgv.pensionpay.dao.BankMasterDAO bankMasterDAO = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
	            if(editFlag != null && editFlag.equals("Y"))
	            {
	            		logger.info("getBankData I m in edit mode ");
	            		long Bankid = Long.parseLong(StringUtility.getParameter("bankid", request));
	            		MstBank actionList = (MstBank)bankMasterDAO.getBankIdData(Bankid,langId);	            		     	   
	            		logger.info("getBankData Bank Name is " + actionList.getBankName());
	            		
	            		Map map = new HashMap();
	            	    
	            	    map=objectArgs;
				        map.put("actionList", actionList);
				        resultObject.setResultCode(ErrorConstants.SUCCESS);
				        resultObject.setResultValue(map);
				        //resultObject.setViewName("bankEditList");
				        resultObject.setViewName("BankMaster");
	            }
	            else
	            {
			            List <MstBank> actionList = bankMasterDAO.getAllBankMasterData(langId);
			            Map map = new HashMap();
			            map=objectArgs;
			            map.put("actionList", actionList);
			            resultObject.setResultCode(ErrorConstants.SUCCESS);
			            resultObject.setResultValue(map);
			            resultObject.setViewName("BankMasterView");
	            }			            			
		}
		catch(PropertyValueException pe)
		{
			logger.info("Null Pointer Exception Ocuures...insertBankMasterDtls");
			//pe.printStackTrace();
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");		
			/*
			logger.info("setViewName-->errorPage");
			Map result = new HashMap();
			result.put("MESSAGECODE",300004);
			resultObject.setResultValue(result);
			resultObject.setThrowable(pe);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			pe.printStackTrace();*/

		}
		catch(Exception e)
		{	
			logger.info("Null Pointer Exception Ocuures...insertBankMasterDtls");
			//e.printStackTrace();
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");		
			/*
			logger.info("setViewName-->errorPage");
			Map result = new HashMap();
			result.put("MESSAGECODE",300004);
			resultObject.setResultValue(result);
			resultObject.setThrowable(e);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			e.printStackTrace();*/
		}	
		return resultObject;
	}
	
	public ResultObject searchBankData(Map inputMap)
	{
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		
		try
		{
			com.tcs.sgv.pensionpay.dao.BankMasterDAO bankMasterDAO = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
			String lStrBankName = StringUtility.getParameter("bankName", request);
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
			
			List <MstBank> lLstBankData = bankMasterDAO.searchBankMasterData(langId, lStrBankName);
			inputMap.put("actionList", lLstBankData);
            resultObject.setResultValue(inputMap);
            resultObject.setViewName("BankMasterView");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(logger, resultObject, e, "Error In searchBankData");
		}
		
		return resultObject;
	}
	
	public ResultObject getBankNameForAutoComplete(Map<String, Object> inputMap) throws Exception
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		
		List finalList = null;		
		try {			
			com.tcs.sgv.pensionpay.dao.BankMasterDAO bankMasterDAO = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
			String lStrBankName = StringUtility.getParameter("bankName", request);
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
			
			finalList = bankMasterDAO.bankMasterAutoComplt(langId, lStrBankName);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList, "desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(logger, objRes, ex, "Error is: ");
			return objRes;
		}

		return objRes;
	}
}
