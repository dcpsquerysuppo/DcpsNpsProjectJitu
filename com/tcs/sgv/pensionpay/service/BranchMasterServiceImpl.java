package com.tcs.sgv.pensionpay.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import com.tcs.sgv.common.dao.CmnBranchMstDaoImpl;
import com.tcs.sgv.common.dao.CmnCountryMstDAOImpl;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnBranchMst;
import com.tcs.sgv.common.valueobject.CmnCountryMst;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.common.valueobject.MstBankPay;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.common.valueobject.RltBankBranchPay;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.eis.dao.BankMasterDAOImpl;
import com.tcs.sgv.eis.dao.BranchMasterDAO;
import com.tcs.sgv.eis.dao.BranchMasterDAOImpl;
import com.tcs.sgv.eis.dao.HrEisBranchDtlsDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.eis.valueobject.HrEisBranchDtls;
import com.tcs.sgv.ess.dao.CmnBranchlocMpgDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.CmnBranchlocMpg;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

public class BranchMasterServiceImpl extends ServiceImpl
{
	Log logger = LogFactory.getLog( getClass() );
	int msg=0;
	public ResultObject insertBranchMasterDtls(Map objectArgs) throws ConstraintViolationException
	{
		// TODO Auto-generated method stub
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS,"fail");

		
		Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
		
		try
		{			
			HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");	
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
			RltBankBranch branchMst = (RltBankBranch)objectArgs.get("branchMst");						
			ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
			
			RltBankBranch branchMstVO = null;
			
			com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl bankMaster = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
			com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
			
			MstBank mstBank = (MstBank)objectArgs.get("bankMst");	
			long bankId=mstBank.getBankId();

			MstBank mstbank =(MstBank)branchMasterDAO.getBankCode(bankId);
			String bankCode = mstbank.getBankCode();

			String micrCode = StringUtility.getParameter("txtMicrCode", request);
			String hidMicrCode = StringUtility.getParameter("hidMicrCode", request);
			String finalBrnchId = "N";
			String lStrChkBrnchId = "";
			List bankList = bankMaster.getAllBankMasterData(langId);
			List lLstTreasuryList = branchMasterDAO.getAllTreasury();

			String editFromVO = objectArgs.get("edit").toString();

			long userId=StringUtility.convertToLong(loginDetailsMap.get("userId").toString());
			OrgUserMstDaoImpl orgUserMstDaoImpl=new OrgUserMstDaoImpl(OrgUserMst.class,serv.getSessionFactory());
			OrgUserMst orgUserMst=orgUserMstDaoImpl.read(userId);

			long dbId=StringUtility.convertToLong(loginDetailsMap.get("dbId").toString());

			//String locId=loginDetailsMap.get("locationId").toString();

			langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());

			long postId=StringUtility.convertToLong(loginDetailsMap.get("primaryPostId").toString());

			Date sysdate = new Date();

			//long branchCode = Long.parseLong(objectArgs.get("branchCode").toString());
			
			
			
			if(editFromVO!=null && editFromVO.equals("Y"))
			{
				logger.info("INSIDE UPDATE");
				long branchId = branchMst.getBranchId();
				
				logger.info("branchId**************"+branchId);
				
				branchMstVO = branchMasterDAO.read(branchId);
				
				branchMstVO.setBranchName(branchMst.getBranchName());
				branchMstVO.setBranchAddress(branchMst.getBranchAddress());				
				branchMstVO.setBankCode(bankCode);
				branchMstVO.setIfscCode(branchMst.getIfscCode());
				branchMstVO.setContact(branchMst.getContact());								
				branchMstVO.setLocationCode(branchMst.getLocationCode());
				branchMstVO.setUpdatedDate(sysdate);
				branchMstVO.setUpdatedPostId(postId);
				branchMstVO.setUpdatedUserId(userId);				
				
				if(!micrCode.equals(hidMicrCode)){
					String lStrLocCode = "";
					String lStrMicr = "";
					String lStrMicrCode = branchMst.getMicrCode().toString();
					if(!lStrMicrCode.equals("")){
						if(lStrMicrCode.toString().length() == 9){
							lStrLocCode = branchMst.getLocationCode();
							lStrMicr = lStrMicrCode.substring(3);
							branchId = Long.parseLong(lStrLocCode+lStrMicr);
							lStrChkBrnchId = branchMasterDAO.chkBranchId(branchId);
							if(lStrChkBrnchId.equals("Y")){
								finalBrnchId = "Y";
							}else{																
								Long lLngBranchCode = branchMasterDAO.getBranchCodeFromBranchId(branchMst.getBranchId());
								branchMasterDAO.updateTableEntries(lLngBranchCode,branchId);
								branchMstVO.setBranchCode(branchId);
								branchMstVO.setMicrCode(branchMst.getMicrCode());
							}
						}else{
							branchMstVO.setMicrCode(branchMst.getMicrCode());
						}
					}
				}
				
				branchMasterDAO.update(branchMstVO);
				msg=1;
			}
			else if(editFromVO!=null && editFromVO.equals("N"))
			{
				logger.info("INSIDE CREATE");				
				String lStrMicrCode = branchMst.getMicrCode().toString();
				String lStrLocCode = "";
				String lStrMicr = "";
				
				IdGenerator idGen = new IdGenerator();
				
				long branchId = idGen.PKGenerator("rlt_bank_branch", objectArgs);
				
				if(!lStrMicrCode.equals("")){
					if(lStrMicrCode.toString().length() == 9){
						lStrLocCode = branchMst.getLocationCode();
						lStrMicr = lStrMicrCode.substring(3);
						branchId = Long.parseLong(lStrLocCode+lStrMicr);
					}
				}
				
				logger.info("branchId********" + branchId);
				
				branchMst.setCreatedDate(sysdate);
				branchMst.setBankCode(bankCode);
				branchMst.setCreatedUserId(userId);
				branchMst.setCreatedPostId(postId);
				branchMst.setCreatedDate(sysdate);
				branchMst.setDbId(dbId);
				branchMst.setLangId(1L);
				
				lStrChkBrnchId = branchMasterDAO.chkBranchId(branchId);
				if(lStrChkBrnchId.equals("Y")){
					finalBrnchId = "Y";
				}else{
					branchMst.setBranchCode(branchId);
					branchMst.setBranchId(branchId);
					branchMasterDAO.create(branchMst);
				}
			}
			else
			{
				throw new NullPointerException();
			}

			objectArgs.put("finalBrnchId", finalBrnchId);						
			
			//Map redirectMap = new HashMap();
			//redirectMap.put("actionFlag", "getBranchMasterDtls");
			if(msg==1){
				objectArgs.put("key", "Update");
				objectArgs.put("MESSAGECODE",300006);
			}
			else{
				objectArgs.put("key", "Insert");
				objectArgs.put("MESSAGECODE",300005);
			}
			//objectArgs.put("redirectMap", redirectMap);			
			//resultObject.setResultCode(ErrorConstants.SUCCESS);			
			
			if(finalBrnchId.equals("Y")){
				objectArgs.put("bankid",bankId);
				objectArgs.put("bankList", bankList);    	
				objectArgs.put("treasuryList", lLstTreasuryList);				
			}
			if(editFromVO!=null && editFromVO.equals("N")){
				objectArgs.put("actionList", branchMst);
				resultObject.setViewName("BranchMaster");
			}else if(editFromVO!=null && editFromVO.equals("Y")){	
				objectArgs.put("actionList", branchMstVO);
				objectArgs.put("micrCode",hidMicrCode);
				resultObject.setViewName("BranchEditList");
			}
						
			resultObject.setResultValue(objectArgs);
		}
		catch(NullPointerException ne)
		{
			logger.info("Null Pointer Exception Ocuures...");
			 //ne.printStackTrace();
			 objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("errorInsert");
		}
		catch(ConstraintViolationException ex)
		{
			 logger.info("Constraint violation Ocuures...");
			 //ex.printStackTrace();
			 objectArgs.put("msg", "Record is not Inserted. Please Try Again.");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("errorInsert");
		}
		catch(Exception e){
			
			logger.info("Exception Ocuures...");
			//e.printStackTrace();
			 objectArgs.put("msg", "Record is not Inserted. Please Try Again.");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("errorInsert");
		}
		return resultObject;
	}
	
	public ResultObject checkBranchName(Map objectArgs)
	{
		
		logger.info("IN checkBranchName Data");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
        //Map voToService = (Map)objectArgs.get("voToServiceMap");
        HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
        
        try
        {
        com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
       
        Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
        long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
        CmnLanguageMstDaoImpl cmnLanguageMstDaoImpl=new CmnLanguageMstDaoImpl(CmnLanguageMst.class,serv.getSessionFactory());
		CmnLanguageMst cmnLanguageMst=cmnLanguageMstDaoImpl.read(langId);
		
        String strbranchName = StringUtility.getParameter("branchName", request);
        String oldname = StringUtility.getParameter("oldname", request);
        
        long bank_id=0;
        
        if(oldname!= null && (!oldname.equals("")))    
		{
			
        	oldname= oldname.trim().toLowerCase();
		}
        long old_bankId = 0;
        String bankCode = "";
        
        
        if(StringUtility.getParameter("old_bankId",request)!=null && !StringUtility.getParameter("old_bankId",request).toString().equals(""))
        {        	
         old_bankId = Long.parseLong(StringUtility.getParameter("old_bankId",request).toString());
        }
        
             
        
        String bankId = StringUtility.getParameter("bank_id", request);
        
        
        
        if(bankId!= null && (!bankId.equals("")))   
		{
			
			bank_id= Long.parseLong(bankId);
		}	
         
        	
        StringBuffer propertyList = new StringBuffer();
        strbranchName = strbranchName.trim().toLowerCase();
        if(bank_id!=old_bankId || !strbranchName.equals(oldname))
        {
        	bankCode = branchMasterDAO.getBankCodeFromId(bank_id);
        		List barnchName = branchMasterDAO.checkBranchName(strbranchName,bankCode,langId);
         
        propertyList.append("<branch-name>");   	
        propertyList.append("<list-size>").append(barnchName.size()).append("</list-size>");
        propertyList.append("</branch-name>"); 
        logger.info("Branch Name size " + barnchName.size());
        }
        else
        {
        	propertyList.append("<branch-name>");   	
            propertyList.append("<list-size>").append(0).append("</list-size>");
            propertyList.append("</branch-name>");
        }
        
        Map result = new HashMap();
        String branchNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", propertyList.toString()).toString();
        result=objectArgs;
        result.put("ajaxKey", branchNameIdStr);
           
        resultObject.setResultValue(result);
        resultObject.setViewName("ajaxData");                          	  	                          	               	   	                              	          	  
       logger.info("After Service Called.........\n");
		}
		catch(Exception e)
		{			
			//e.printStackTrace();			 
		}
		return resultObject;
	}

	public ResultObject checkIFSCcode(Map objectArgs)
	{		
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
        //Map voToService = (Map)objectArgs.get("voToServiceMap");
        HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
        
        try
        {
        	com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
        	String lStrIfscCode = StringUtility.getParameter("IFSCcode", request);
        	
        	String lStrRes = branchMasterDAO.checkIFSCcode(lStrIfscCode);
        	
        	String lSBStatus = getResponseXMLDoc(lStrRes).toString();
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			  
			objectArgs.put("ajaxKey", lStrResult);
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("ajaxData");
        }catch(Exception e){
        	//e.printStackTrace();
        }
        
        return resultObject;
	}
	
	public ResultObject checkMicrCode(Map objectArgs)
	{		
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");        
        HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
        String lSBStatus = "";
        
        try
        {
        	com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
        	String lStrMicrCode = StringUtility.getParameter("MICRcode", request);
        	
        	String lStrRes = branchMasterDAO.checkMicrCode(lStrMicrCode);
        	
        	if(lStrRes.equals("N")){
        		lSBStatus = getResponseXMLDocMicr(lStrRes,"").toString();
        	}else{
        		String lStrBrnchName = lStrRes.split("#")[0];
        		String lStrBrnchAddr = lStrRes.split("#")[1];
        		lSBStatus = getResponseXMLDocMicr(lStrBrnchName,lStrBrnchAddr).toString();
        	}        	
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			  
			objectArgs.put("ajaxKey", lStrResult);
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("ajaxData");
        }catch(Exception e){
        	//e.printStackTrace();
        }
        
        return resultObject;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrData) {
		
		StringBuilder lStrBldXML = new StringBuilder();
	
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DATA>");
		lStrBldXML.append(lStrData);
		lStrBldXML.append("</DATA>");		
		lStrBldXML.append("</XMLDOC>");
	
		return lStrBldXML;
	}
	
	private StringBuilder getResponseXMLDocMicr(String lStrBranchName, String lStrBranchAddr) {
		
		StringBuilder lStrBldXML = new StringBuilder();
	
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<BRNAME>");
		lStrBldXML.append(lStrBranchName);
		lStrBldXML.append("</BRNAME>");
		lStrBldXML.append("<BRADDR>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lStrBranchAddr);
		lStrBldXML.append("]]>");
		lStrBldXML.append("</BRADDR>");
		lStrBldXML.append("</XMLDOC>");
	
		return lStrBldXML;
	}

	public ResultObject getBranchMasterDtls(Map objectArgs)
	{
		logger.info("IN Branch Master Data");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);

		Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");

		try
		{
	            ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	            com.tcs.sgv.pensionpay.dao.BranchMasterDAO branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
	            long langId = StringUtility.convertToLong(loginDetailsMap.get("langId").toString());

	            com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl bankMaster = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
	            

	            //for new id
	            List branchList = branchMasterDAO.getAllBranchMasterData(langId);
	            /*List<RltBankBranch> branchMstList = new ArrayList();
	            List<MstBankPay> bankIDlist = new ArrayList();
	            Object []lObj = null;
	            
		            for (int i=0; i<branchList.size(); i++)
	                {
		            	
		            	lObj = (Object[]) branchList.get(i);

		            	RltBankBranch hrEisBranchMsten = new RltBankBranch();
		            	hrEisBranchMsten.setBankCode(lObj[1].toString());	            		
	                	hrEisBranchMsten.setBranchId(Long.parseLong(lObj[0].toString()));
	                	hrEisBranchMsten.setBranchName(lObj[2].toString());
	                	if(lObj[14] != null){
	                		hrEisBranchMsten.setMicrCode(Long.parseLong(lObj[14].toString()));
	                	}

	                	branchMstList.add(hrEisBranchMsten);
	                }//end for

	            List bankList = bankMaster.getAllBankMasterData(langId);*/
	           


	            objectArgs.put("actionList", branchList);
	            objectArgs.put("listSize", branchList.size());
	            //objectArgs.put("bankList", bankList);	            


	            resultObject.setResultCode(ErrorConstants.SUCCESS);
	            resultObject.setResultValue(objectArgs);
	            resultObject.setViewName("BranchMasterView");
	            
		}
		catch(Exception e)
		{
			logger.info("Exception Ocuures...");
			//e.printStackTrace();
			 objectArgs.put("msg", "There is some problem. Please Try Again.");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("errorInsert");
		}
	
		return resultObject;
	}	

	public ResultObject insertBranchDtls(Map objectArgs)
	{
		Calendar calTime = Calendar.getInstance();
		logger.info("insertBranchDtlsService starts: "+calTime.getTimeInMillis());
		logger.info("insertBranchDtlsService starts: "+calTime.getTimeInMillis());
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");	
		ServiceLocator serviceLocator = (ServiceLocator)objectArgs.get("serviceLocator");
		Map loginMap = (Map) objectArgs.get("baseLoginMap");
		
		String locId=((loginMap.get("locationId")).toString());
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	
		try
		{
	
		
			CmnBranchlocMpgDaoImpl cmnBranchlocMpgDaoImpl  = new CmnBranchlocMpgDaoImpl(CmnBranchlocMpg.class,serviceLocator.getSessionFactory());

			CmnBranchMstDaoImpl cmnBranchMstDaoImpl  = new CmnBranchMstDaoImpl(CmnBranchMst.class,serviceLocator.getSessionFactory());
	
			Map mapFromVogen = (Map)objectArgs.get("dtls");
			logger.info("map from vo gen is " + mapFromVogen);
		
			HrEisBranchDtlsDAOImpl hrEisBranchDtlsDaoImpl  = new HrEisBranchDtlsDAOImpl(HrEisBranchDtls.class,serviceLocator.getSessionFactory());
		

			long dbId=StringUtility.convertToLong(loginMap.get("dbId").toString());
			CmnDatabaseMstDaoImpl cmnDatabaseMstDaoImpl=new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,serv.getSessionFactory());
			CmnDatabaseMst cmnDatabaseMst=cmnDatabaseMstDaoImpl.read(dbId);
			
			long langId = StringUtility.convertToLong(loginMap.get("langId").toString());
			CmnLanguageMstDaoImpl cmnLanguageMstDaoImpl=new CmnLanguageMstDaoImpl(CmnLanguageMst.class,serv.getSessionFactory());
			CmnLanguageMst cmnLanguageMst=cmnLanguageMstDaoImpl.read(langId);
			
			CmnLocationMst cmnLocationMst = (CmnLocationMst) loginMap.get("locationVO");
			
			
			ResourceBundle resourceBundle = ResourceBundle.getBundle("resources.Payroll");
			logger.info("In Service Impl " + mapFromVogen.get("branchTypeLookup").toString());
			long branchTypeLookup=Long.parseLong(mapFromVogen.get("branchTypeLookup").toString());
			logger.info("type lookup id In service is "+branchTypeLookup);
			long TypeLookup;
			
			if(branchTypeLookup==1)
			{
				 TypeLookup=Long.parseLong(resourceBundle.getString("mainbranch"));
			}
			else
			{
				 TypeLookup=Long.parseLong(resourceBundle.getString("subbranch"));
			}
			
			logger.info("---------------TypeLookup---------------"+TypeLookup);
			CmnLookupMstDAOImpl cmnLookupMstDAOImpl=new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
			CmnLookupMst cmnLookupMst = cmnLookupMstDAOImpl.read(TypeLookup);

			
			long active=Long.parseLong(mapFromVogen.get("active").toString());
			logger.info("---------------active---------------"+active);
			CmnLookupMstDAOImpl cmnLookupMstDAOImpl1=new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
			CmnLookupMst cmnLookupMst1 = cmnLookupMstDAOImpl1.read(active);

			OrgPostMstDaoImpl orgPostMstDaoImpl=new OrgPostMstDaoImpl(OrgPostMst.class,serv.getSessionFactory());
			
			long postId=StringUtility.convertToLong(loginMap.get("primaryPostId").toString());
			OrgPostMst orgPostMst=orgPostMstDaoImpl.read(postId);
			logger.info("---------------postId---------------"+postId);
			
			long userId = StringUtility.convertToLong(loginMap.get("userId").toString());
			OrgUserMstDaoImpl orgUserMstDaoImpl=new OrgUserMstDaoImpl(OrgUserMst.class,serv.getSessionFactory());
			OrgUserMst orgUserMst=orgUserMstDaoImpl.read(userId);
			
			
			CmnBranchMst cmn2 = new CmnBranchMst();
			IdGenerator gen = new IdGenerator();
			long branchId=gen.PKGenerator("cmn_branch_mst",objectArgs);
			
			logger.info("id mgenerated for cmn_branch_mst and that is:"+branchId);
			
			
			cmn2.setBranchId(branchId);
			cmn2.setBranchName(mapFromVogen.get("branchname").toString());
			cmn2.setActivateFlag(Long.parseLong((mapFromVogen.get("active").toString())));
			cmn2.setBranchCode(mapFromVogen.get("branchcode").toString());
			cmn2.setBranchDesc(mapFromVogen.get("branchdesc").toString());
			cmn2.setCmnDatabaseMst(cmnDatabaseMst);
			logger.info("--------branchdesc-----------" +mapFromVogen.get("branchdesc").toString());
			cmn2.setCmnLanguageMst(cmnLanguageMst);
			cmn2.setCmnLocationMst(cmnLocationMst);
			cmn2.setCreatedBy(userId);
			cmn2.setCreatedByPost(postId);
			cmn2.setCreatedDate(new Date());
			cmn2.setDisplayName(mapFromVogen.get("branchname").toString());
			cmn2.setEndDate(new Date());
			cmn2.setStartDate(new Date());
			cmn2.setUpdatedBy(null);
			cmn2.setUpdatedByPost(null);
			cmn2.setUpdatedDate(new Date());
			
			logger.info("set method completed in CmnBranchMst");
			
			cmnBranchMstDaoImpl.create(cmn2);
			
		/*	long srNo=gen.PKGenerator("CMN_BRANCHLOC_MPG",objectArgs);

			logger.info("id mgenerated for CMN_BRANCHLOC_MPG and that is:"+srNo);
			CmnBranchlocMpg branchloc = new CmnBranchlocMpg();
			 
			branchloc.setSrno(srNo);
			branchloc.setBranchCode(mapFromVogen.get("branchcode").toString());		
			branchloc.setLocationCode(locId);
			logger.info("--------locationCode-----------" +locId);
			branchloc.setCmnDatabaseMst(cmnDatabaseMst);
			branchloc.setCmnLookupMst(cmnLookupMst1);
			branchloc.setCreatedDate(new Date());
			branchloc.setBranchHeadUserId(userId);
			branchloc.setCmnBranchMst(cmn2);
			branchloc.setOrgPostMstCreatedByPost(orgPostMst);
			branchloc.setOrgPostMstUpdatedBypost(orgPostMst);
			branchloc.setOrgUserMstCreatedBy(orgUserMst);
			branchloc.setOrgUserMstUpdatedBy(orgUserMst);
			branchloc.setUpdatedDate(new Date());
			branchloc.setOrgPostMstBranchHead(orgPostMst);
			branchloc.setCmnLocationMst(cmnLocationMst);*/
			
			
		//	CmnBranchlocMpgDaoImpl cmnBranchlocMpgDaoImpl2=new CmnBranchlocMpgDaoImpl(CmnBranchlocMpg.class,serv.getSessionFactory());
		//	CmnBranchlocMpg cmnBranchlocMpg = cmnBranchlocMpgDaoImpl2.read(srNo);
			
		//	logger.info("set method completed in CMN_BRANCHLOC_MPG");
			
		//	cmnBranchlocMpgDaoImpl.create(branchloc);
			
			HrEisBranchDtls branchdtls = new HrEisBranchDtls();
			
			long id = gen.PKGenerator("HR_EIS_BRANCH_DTLS",objectArgs);
			
			logger.info("id mgenerated for HR_EIS_BRANCH_DTLS and that is:"+id);
			branchdtls.setId(id);
			//branchdtls.setCmnBranchlocMpg(branchloc);
			branchdtls.setCmnLanguageMst(cmnLanguageMst);
			branchdtls.setCmnLocationMst(cmnLocationMst);
			branchdtls.setCmnLookupMst(cmnLookupMst);
			branchdtls.setCreatedDate(new Date());
			branchdtls.setOrgPostMstByCreatedByPost(orgPostMst);
			branchdtls.setOrgPostMstByUpdatedByPost(orgPostMst);
			logger.info("--------orgPostMst-----------" +orgPostMst);
			branchdtls.setOrgUserMstByCreatedBy(orgUserMst);
			branchdtls.setOrgUserMstByUpdatedBy(orgUserMst);
			branchdtls.setUpdatedDate(new Date());

			logger.info("set method completed in hrEisBranchDtls");
		
			hrEisBranchDtlsDaoImpl.create(branchdtls);
			
			logger.info("create method completed");
			
				objectArgs.put("msg", "Record Inserted Successfully");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("CreateBranch");
		}
			

		catch(Exception e)
		{			
			//e.printStackTrace();
			objectArgs.put("msg", "There is some problem. Please Try Again.");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("errorInsert");
			
			
		}
		
		return resultObject;
	}
	

	
	public ResultObject getBranchData(Map objectArgs)
	{
		logger.info("IN getBranchData Data");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
				
		try
		{			
	            ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	            com.tcs.sgv.pensionpay.dao.BranchMasterDAO branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
	            
	            Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
	            long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());   
	            
    			//Map voToService = (Map)objectArgs.get("voToServiceMap");
	            
    			String editFlag = StringUtility.getParameter("edit", request);
    			
    			if(editFlag != null && editFlag.equals("Y"))
    			{    				
    				String branchId = StringUtility.getParameter("branchId", request);    				

    				List<RltBankBranch> rltBankBranch = branchMasterDAO.getBranchIdFromBankCode(branchId);    				
    				    				
    				RltBankBranch actionList = (RltBankBranch)branchMasterDAO.getBranchIdData(branchId);

    				RltBankBranch newResult = new RltBankBranch();

    				com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl bankMaster = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());

    				String bankCode = StringUtility.getParameter("bankCode", request);
    				long BankCode = Long.parseLong(bankCode);
    				
    				List<MstBank> BankList = bankMaster.getAllBankIdFromBankCode(BankCode);
    				long bankid = 0;
    				if(BankList != null && BankList.size() > 0)
    					bankid = BankList.get(0).getBankId();
    				List bankList = bankMaster.getAllBankMasterData(langId); 
    				 
    				List lLstTreasuryList = branchMasterDAO.getAllTreasury(); 
    				//for language id change

    				BankMasterDAOImpl bankDao1 = new BankMasterDAOImpl(MstBankPay.class,serv.getSessionFactory());

    				newResult.setBranchAddress(actionList.getBranchAddress());
    				newResult.setBranchCode(actionList.getBranchCode());
    				newResult.setBranchId(actionList.getBranchId());
    				
    				logger.info("micr codeeeeeeeee in get branh data"+actionList.getMicrCode());
    				
    				Long micrCode = null;
    				if(actionList.getMicrCode() != null){
    					micrCode = actionList.getMicrCode();
    				}
    				
    				newResult.setBranchName(actionList.getBranchName());
    				newResult.setMicrCode(actionList.getMicrCode());
    				newResult.setIfscCode(actionList.getIfscCode());
    				newResult.setContact(actionList.getContact());
    				newResult.setLocationCode(actionList.getLocationCode());
    				//newResult.setMicrCode(actionList.getBankCode());
    				//end


    				Map map = new HashMap();
    				map=objectArgs;          
    				map.put("bankid",bankid);
    				map.put("micrCode",micrCode);
    				map.put("actionList", newResult);
    				map.put("bankList", bankList);    	
    				map.put("treasuryList", lLstTreasuryList);
    				resultObject.setResultCode(ErrorConstants.SUCCESS);
    				resultObject.setResultValue(map);
    				resultObject.setViewName("BranchEditList");
    			}
	            else
	            {
			            List BranchList = branchMasterDAO.getAllBranchMasterData(langId);
			            
			            /*List<RltBankBranchPay> BranctMstList = new ArrayList();
			            for (int i=0;i<BranchList.size();i++)
		                {
			            	RltBankBranch hrEisBranchMst=new RltBankBranch();
			            	hrEisBranchMst=(RltBankBranch) BranchList.get(i);
			            	RltBankBranch hrEisBranchMstgu=new RltBankBranch();
		                	
		            		
		                	BranchList.add(hrEisBranchMstgu);			
		                				
		                }*/
			            
			            
			            Map map = new HashMap();
			            map=objectArgs;
			            map.put("actionList", BranchList);
			            resultObject.setResultCode(ErrorConstants.SUCCESS);
			            resultObject.setResultValue(map);
			            resultObject.setViewName("BranchMasterView");
	            }
			            
			
		}
		catch(Exception e)
		{
			logger.info("Exception Ocuures...");
			//e.printStackTrace();
			 objectArgs.put("msg", "There is some problem. Please Try Again.");
			 resultObject.setResultValue(objectArgs);
			 resultObject.setViewName("errorInsert");
		}
	
		return resultObject;
	}	
	//method for getting all Country list and redirect to Branch Master Page
	public ResultObject getBranchMaster(Map objectArgs)
	{
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		com.tcs.sgv.pensionpay.dao.BranchMasterDAO branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());

		try{
			CmnCountryMstDAOImpl cmnCountry = new CmnCountryMstDAOImpl(CmnCountryMst.class,serv.getSessionFactory());
			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
            long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());   
			logger.info("LangID in getBranchMaster " + langId);
			
			com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl bankMaster = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
			
			List bankList = bankMaster.getAllBankMasterData(langId); 
			
			List lLstTreasuryList = branchMasterDAO.getAllTreasury();
			Map map = new HashMap();
			map=objectArgs;
            map.put("bankList", bankList);
            map.put("treasuryList", lLstTreasuryList);
            resultObject.setResultCode(ErrorConstants.SUCCESS);
            resultObject.setResultValue(map);
            
            resultObject.setViewName("BranchMaster");
	}
	catch(Exception e)
	{
		logger.info("Exception Ocuures...");
		//e.printStackTrace();
		 objectArgs.put("msg", "There is some problem. Please Try Again.");
		 resultObject.setResultValue(objectArgs);
		 resultObject.setViewName("errorInsert");
	}

	return resultObject;
 }	
		
	public ResultObject getBranchNameForAutoComplete(Map<String, Object> inputMap) throws Exception
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrBankCode = "";
		
		List finalList = null;		
		try {
			com.tcs.sgv.pensionpay.dao.BranchMasterDAO branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
			String lStrBranchName = StringUtility.getParameter("branchName", request);
			String lStrBankName = StringUtility.getParameter("bankName", request);
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
			
			if(!lStrBankName.equals("")){
				lStrBankCode = branchMasterDAO.getBankCodeFromName(lStrBankName);				
			}
				
			finalList = branchMasterDAO.branchMasterAutoComplt(langId, lStrBranchName, lStrBankCode);

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
	
	public ResultObject searchBranchData(Map inputMap)
	{
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List lLstBranchData = null;
		try
		{
			com.tcs.sgv.pensionpay.dao.BranchMasterDAO branchMasterDAO = new com.tcs.sgv.pensionpay.dao.BranchMasterDAOImpl(RltBankBranch.class,serv.getSessionFactory());
			String lStrBankName = StringUtility.getParameter("bankName", request);
			String lStrBranchName = StringUtility.getParameter("branchName", request);
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
			
			if(lStrBranchName.equals("") && lStrBankName.length() > 0){
				lLstBranchData = branchMasterDAO.searchBranchMasterFromBank(langId, lStrBankName);
			}else if(lStrBranchName.length() > 0 && lStrBankName.equals("")){
				lLstBranchData = branchMasterDAO.searchBranchMasterFromBranch(langId, lStrBranchName);
			}else if(lStrBankName.length() > 0 && lStrBranchName.length() > 0){
				lLstBranchData = branchMasterDAO.searchBranchMasterFromBankBranch(langId, lStrBankName, lStrBranchName);
			}else{
				
			}
			
			inputMap.put("actionList", lLstBranchData);
            resultObject.setResultValue(inputMap);
            resultObject.setViewName("BranchMasterView");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(logger, resultObject, e, "Error In searchBranchData");
		}
		
		return resultObject;
	}
}
