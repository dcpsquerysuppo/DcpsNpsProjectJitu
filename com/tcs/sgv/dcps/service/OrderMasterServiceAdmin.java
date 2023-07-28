package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.PropertyValueException;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.FileUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.AdminPostDtlsDAO;
import com.tcs.sgv.dcps.dao.AdminPostDtlsDAOImpl;
import com.tcs.sgv.eis.dao.OrderMstDAO;
import com.tcs.sgv.eis.dao.OrderMstDAOImpl;
import com.tcs.sgv.eis.dao.PaybillHeadMpgDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.eis.valueobject.HrEisEmpMst;
import com.tcs.sgv.eis.valueobject.HrPayOrderMst;
import com.tcs.sgv.eis.valueobject.PaybillHeadMpg;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;




public class OrderMasterServiceAdmin extends ServiceImpl  {	
	Log logger = LogFactory.getLog( getClass() );
	int msg=0;
	
	
	private String gStrPostId = null; /* STRING POST ID */

	private Date gDtCurDate = null; /* CURRENT DATE */
	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;
	
	/* Global Variable for LangId */
	Long gLngLangId = null;
	
	Long gLngLocationCode = null;
	
	
	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngLocationCode = Long.parseLong(gStrLocationCode);
		gLngLangId = SessionHelper.getLangId(inputMap);

	}
	
	//This method will insert and update the order data
	
	public ResultObject insertOrderMasterDtls(Map objectArgs) {
		
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		try{
			 setSessionInfo(objectArgs);
			ResultObject resultObj = serv.executeService("FILE_UPLOAD_SRVC", objectArgs);
			Map resultMap = (Map) resultObj.getResultValue();			
			
			HrPayOrderMst orderMst = (HrPayOrderMst)objectArgs.get("orderMst");//object of VOGEN
			HrPayOrderMst hrPayOrderMst;	// Newer Object of the HrPayOrderMst
			
			OrderMstDAOImpl ordermstDAOImpl = new OrderMstDAOImpl(HrPayOrderMst.class,serv.getSessionFactory());//object of DAOIMPL
			
			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
		//	Long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());//For Language independent
			
            String editFromVO = objectArgs.get("edit").toString();//Flag for edit mode yes or no
            logger.info("Flag which display go in insert or update " + editFromVO);
            
            Long userId=StringUtility.convertToLong(loginDetailsMap.get("userId").toString());
			OrgUserMstDaoImpl orgUserMstDaoImpl=new OrgUserMstDaoImpl(OrgUserMst.class,serv.getSessionFactory());
			OrgUserMst orgUserMst=orgUserMstDaoImpl.read(userId);
			
			
	        Long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
	        CmnLanguageMstDaoImpl cmnLanguageMstDaoImpl=new CmnLanguageMstDaoImpl(CmnLanguageMst.class,serv.getSessionFactory());
			CmnLanguageMst cmnLanguageMst=cmnLanguageMstDaoImpl.read(langId);
			
			Long postId=StringUtility.convertToLong(loginDetailsMap.get("primaryPostId").toString());
			OrgPostMstDaoImpl orgPostMstDaoImpl=new OrgPostMstDaoImpl(OrgPostMst.class,serv.getSessionFactory());
			OrgPostMst orgPostMst=orgPostMstDaoImpl.read(postId);
			
			HttpServletRequest request1 = (HttpServletRequest) objectArgs.get("requestObj");							
			
			String lStrDDOCode = StringUtility.getParameter("DDO", request1);
			String lStrFieldDeptCode = StringUtility.getParameter("FD", request1);
			
			AdminPostDtlsDAO lObjPostDtlsDAO = new AdminPostDtlsDAOImpl(null, serv.getSessionFactory());
			
			List<ComboValuesVO> lLstFieldDept = lObjPostDtlsDAO.getFieldDeptFromAdminDeptCode(gLngLocationCode);
			List<ComboValuesVO> lLstDdo = lObjPostDtlsDAO.getDdoListFromFieldDept(lStrFieldDeptCode);
			objectArgs.put("lLstFieldDept", lLstFieldDept);
			objectArgs.put("lLstDdo", lLstDdo);
			objectArgs.put("DDOCode", lStrDDOCode);
			objectArgs.put("FieldDeptCode", lStrFieldDeptCode);			
			
			String locationCode = lObjPostDtlsDAO.getLocationCodeForDDO(lStrDDOCode);
			
			Date sysdate = new Date();
			Long attachment_Id=0L;
			if(resultMap.get("AttachmentId_orderId")!=null)
			 attachment_Id = (Long) resultMap.get("AttachmentId_orderId"); 
					
	
            
            if(editFromVO!=null && editFromVO.equalsIgnoreCase("Y"))            	
			{
            	
            	Long orderid = orderMst.getOrderId();
            	            	
       		    hrPayOrderMst = ordermstDAOImpl.read(orderid);//Reading orderid from Vo
				
				hrPayOrderMst.setLocationCode(orderMst.getLocationCode());//Setting Order Name in vo
				hrPayOrderMst.setOrderName(orderMst.getOrderName());//Setting Order Name in vo
				hrPayOrderMst.setOrderDate(orderMst.getOrderDate());//setting Order Date in vo			
				hrPayOrderMst.setEndDate(orderMst.getEndDate());	// Setting Order end date.
				hrPayOrderMst.setUpdatedDate(sysdate);
				hrPayOrderMst.setOrgUserMstByUpdatedBy(orgUserMst);
				hrPayOrderMst.setOrgPostMstByUpdatedByPost(orgPostMst);
				hrPayOrderMst.setAttachment_Id(attachment_Id);
				
				ordermstDAOImpl.update(hrPayOrderMst);//update Vo...VO ready to update
				msg=1;//for display message for update
			}
            
            
			else if(editFromVO!=null && editFromVO.equalsIgnoreCase("N"))
			{
				
				String encXML[] = StringUtility.getParameterValues("encXML",request); 
				if(encXML!=null && encXML.length>0)
		    	{
		    		int rowNumber = 0;
		    		String [] rowNumForAttachment = StringUtility.getParameterValues("encXML_rowNumber", request);
		    		Long orderIdAttachment=0l;
		    							
					List result=FileUtility.xmlFilesToVoByXStream(encXML);			    	
		    		
		    		Iterator it = result.iterator(); 
					while(it.hasNext())
					{
						HrPayOrderMst payOrderMst=(HrPayOrderMst) it.next();
						
						IdGenerator idGen = new IdGenerator();
						Long orderId = idGen.PKGenerator("HR_PAY_ORDER_MST", objectArgs);
						payOrderMst.setOrderId(orderId);//setting order id
						payOrderMst.setCmnLanguageMst(cmnLanguageMst);
						payOrderMst.setTrnCounter(new Integer(1));
						payOrderMst.setOrgPostMstByCreatedByPost(orgPostMst);
						payOrderMst.setOrgUserMstByCreatedBy(orgUserMst);
						payOrderMst.setCreatedDate(sysdate);
						payOrderMst.setLocationCode(locationCode);
						
						String currRowNum=rowNumForAttachment[rowNumber];
						objectArgs.put("rowNumber",currRowNum);
						objectArgs.put("attachmentName","orderId");
						
						resultObject = serv.executeService("FILE_UPLOAD_VOGEN",objectArgs);
						objectArgs=(Map)resultObject.getResultValue();
						
						resultObject = serv.executeService("FILE_UPLOAD_SRVC",objectArgs);
						objectArgs=(Map)resultObject.getResultValue();
												
						 if(objectArgs.get("AttachmentId_orderId")!=null)
							{
							    orderIdAttachment = Long.parseLong(objectArgs.get("AttachmentId_orderId").toString());
								logger.info("orderIdAttachmentorderIdAttachment========" +orderIdAttachment);
								
							}
							
						payOrderMst.setAttachment_Id(orderIdAttachment);
						ordermstDAOImpl.create(payOrderMst);//ready to insert			
								
						rowNumber++;
					}
		    	}
											
			}
            
            if(msg==1)
    			objectArgs.put("MESSAGECODE",300006);//message code from frm_message_mst 300006 for Update
    		else
    			objectArgs.put("MESSAGECODE",300005);//message code from frm_message_mst 300005 for Insert
    					
    		resultObject.setResultCode(ErrorConstants.SUCCESS);
    		
    		resultObject.setResultValue(objectArgs);
    		if(msg==1)
    			resultObject.setViewName("OrderMasterEditAdmin");//For Redirect from message to view jsp
    		else
    			resultObject.setViewName("OrderMasterAdmin");//For Redirect from message to view jsp
    		
    	}
    	catch(NullPointerException ne)
    	{	
    		objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
    		resultObject.setResultValue(objectArgs);
    		resultObject.setViewName("errorInsert");			
    	}
    	
    	catch(Exception e){
    		 objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
    		 resultObject.setResultValue(objectArgs);
    		 resultObject.setViewName("errorInsert");
    	}
    	return resultObject;
    	}
	
//This method will fetch all hr_pay_order_mst data for display in the View, Insert and Edit Mode.
	
	public ResultObject getOrderData(Map objectArgs)
	{
		
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		setSessionInfo(objectArgs);
		try {
		   ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	       Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
	       CmnLocationMst cmnLocationMst = (CmnLocationMst) loginDetailsMap.get("locationVO");   		 
   		
		   Map voToService = (Map)objectArgs.get("voToServiceMap");
		   String editFlag = (String)voToService.get("edit");//edit flag passing
		
		   Long langId = Long.parseLong(loginDetailsMap.get("langId").toString());
		   String lStrDDOCode = StringUtility.getParameter("DDO", request);
		   String lStrFieldDeptCode = StringUtility.getParameter("FD", request);
		 
		   AdminPostDtlsDAO lObjPostDtlsDAO = new AdminPostDtlsDAOImpl(null, serv.getSessionFactory());			
		   String lStrLocCode = lObjPostDtlsDAO.getLocationCodeForDDO(lStrDDOCode);
					
		   List<ComboValuesVO> lLstFieldDept = lObjPostDtlsDAO.getFieldDeptFromAdminDeptCode(gLngLocationCode);
		   List<ComboValuesVO> lLstDdo = lObjPostDtlsDAO.getDdoListFromFieldDept(lStrFieldDeptCode);
		   objectArgs.put("lLstFieldDept", lLstFieldDept);
		   objectArgs.put("lLstDdo", lLstDdo);
			
		   objectArgs.put("DDOCode", lStrDDOCode);
		   objectArgs.put("FieldDeptCode", lStrFieldDeptCode);
		   	   
		   CmnLocationMstDaoImpl locationDAO = new CmnLocationMstDaoImpl(CmnLocationMst.class,serv.getSessionFactory());
           
		   OrderMstDAO orderMasterDAO = new OrderMstDAOImpl(HrPayOrderMst.class,serv.getSessionFactory());//daoimpl object
		   
		   PaybillHeadMpgDAOImpl paybillheadmpgdao = new PaybillHeadMpgDAOImpl(PaybillHeadMpg.class,serv.getSessionFactory());
	       Boolean isRoleAdminFlag = paybillheadmpgdao.isLoggedInUserAdmin(loginDetailsMap);
	       List locationList = null;
	       if(isRoleAdminFlag)
			{
				
				locationList = locationDAO.getListByColumnAndValue("cmnLanguageMst.langId", langId);
				
			}
			else
			{				
				String[] critariaArugs={"cmnLanguageMst.langId","locationCode"};
				Object[] valueArgus=new Object[2];
				valueArgus[0] = langId;
				valueArgus[1] = lStrLocCode;
				locationList = locationDAO.getListByColumnAndValue(critariaArugs, valueArgus);				
			}	       
	       
	       objectArgs.put("locationCode", lStrLocCode);	       
	       
	       String strSearchFlag = (voToService.get("orderSearchFlag")!=null && !voToService.get("orderSearchFlag").toString().equals(""))?voToService.get("orderSearchFlag").toString():"";	// OrderSearchFlag
	      
	       // if Flag's Value is 'y' then go for searching.
	       if(!strSearchFlag.equals("") && (strSearchFlag.equals("y") || strSearchFlag.equals("Y"))){
	    	   String strOrderName = (voToService.get("orderName")!=null && !voToService.get("orderName").toString().equals(""))?voToService.get("orderName").toString():"";
	    	   String strStartDate = (voToService.get("startDate")!=null && !voToService.get("startDate").toString().equals(""))?voToService.get("startDate").toString():"";
	    	   String strEndDate = (voToService.get("endDate")!=null && !voToService.get("endDate").toString().equals(""))?voToService.get("endDate").toString():"";
	    	   List<HrPayOrderMst> lstSearchOrders = orderMasterDAO.getSearchOrderData(strOrderName, strStartDate, strEndDate,lStrLocCode);
	    	   objectArgs.put("actionList", lstSearchOrders);
			   resultObject.setResultCode(ErrorConstants.SUCCESS);
	           resultObject.setResultValue(objectArgs);
			   resultObject.setViewName("OrderMasterViewAdmin");//view Add Order Mode jsp
	       }
	       // End.
	       
	     // for fetching the Location List In the Insert mode of Order Master.
	       else if(editFlag != null && editFlag.equals("N")) {		    	     
			    
			   objectArgs.put("deptList", locationList);
			   resultObject.setResultCode(ErrorConstants.SUCCESS);
	           resultObject.setResultValue(objectArgs);
			   resultObject.setViewName("OrderMasterAdmin");//view Add Order Mode jsp
		   }
		   
		   else if(editFlag != null && editFlag.equals("Y")) {
			
			   Long Orderid =Long.parseLong((String)voToService.get("orderid"));
			   objectArgs.put("deptList", locationList);
			   HrPayOrderMst actionList = (HrPayOrderMst)orderMasterDAO.getOrderMstDataByid(Orderid);//action list	            		     	   
			   Long attach_id = actionList.getAttachment_Id();
			   
			   if(attach_id!=null)
			   {
				   CmnAttachmentMstDAO mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class,serv.getSessionFactory());

				   CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(attach_id);

				   objectArgs.put("orderId",cmnAttachmentMst);
				  
			   }



			   //CmnLocationMst cmnLocationMst = locationDAO.getLocationVOByLocationCodeAndLangId(actionList.getLocationCode(), langId);
			   objectArgs.put("actionList", actionList);
			   objectArgs.put("cmnLocationMst", cmnLocationMst);
			   resultObject.setResultCode(ErrorConstants.SUCCESS);
			   resultObject.setResultValue(objectArgs);//passing map to result value for get data in jsp		      
			   resultObject.setViewName("OrderMasterEditAdmin");//view edit jsp
		   }
		   else {  
			   //CmnLocationMst cmnLocationMst = locationDAO.read(locId);
			   List <HrEisEmpMst> actionList = orderMasterDAO.getAllData(lStrLocCode);//list will collect all data from Vo           
			   objectArgs.put("actionList", actionList);
			   resultObject.setResultCode(ErrorConstants.SUCCESS);
			   resultObject.setResultValue(objectArgs);//passing map to jsp
			   resultObject.setViewName("OrderMasterViewAdmin");//view jsp
		   }			            			
		}
		catch(PropertyValueException pe) {
			logger.info("Null Pointer Exception Ocuures...insertOrderMasterDtls");
			logger.error("Error is: "+ pe.getMessage());
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");
		}
		catch(Exception e)
		{	
			logger.info("Null Pointer Exception Ocuures...insertOrderMasterDtls");
			logger.error("Error is: "+ e.getMessage());
			objectArgs.put("msg", "There is Some Problem.Please Try Again Later.");
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("errorInsert");			
		}	
		return resultObject;
	}		
//For AJAX :- Checking Duplicate Order Name	
public ResultObject checkOrderAvailability(Map objectArgs){
	 	
	
		logger.info("Inside AJAX Service");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		HttpSession session=request.getSession();
		Map voToService = (Map)objectArgs.get("voToServiceMap");
		Map loginDetailsMap = (Map)objectArgs.get("baseLoginMap");
        StringBuffer orderNameBf=new StringBuffer();
        String check; 
		//Long langId=Long.parseLong(loginDetailsMap.get("langId").toString());//For Language Independent
		try{
			ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
			OrderMstDAO orderMasterDAO = new OrderMstDAOImpl(HrPayOrderMst.class,serv.getSessionFactory());
            String newOrderName = voToService.get("newOrderName").toString();
            String dept=voToService.get("dept").toString();
            String ordDate=voToService.get("date").toString();
            
            
           
        	Date dtTmp = new SimpleDateFormat("dd.MM.yyyy").parse(ordDate.replace("/","."));
        	String strOutDt = new SimpleDateFormat("dd.MMM.yyyy").format(dtTmp);
        	
            
            newOrderName=newOrderName.toLowerCase();
            List <HrPayOrderMst> actionList = orderMasterDAO.getOrderDataFromPara(newOrderName,dept,dtTmp);
           
            
            if(actionList.size()==0){
            	check="false";
            	orderNameBf.append("<orderNameMapping>");
            	orderNameBf.append("<availability>").append(check).append("</availability>");
            	orderNameBf.append("</orderNameMapping>");            	
            }
            else {
            	check="true";
            	orderNameBf.append("<orderNameMapping>");
            	orderNameBf.append("<availability>").append(check).append("</availability>");
            	orderNameBf.append("</orderNameMapping>");
            }           
            String orderNameMapping = new AjaxXmlBuilder().addItem("ajax_key", orderNameBf.toString()).toString();
	         
            logger.info(" the string buffer is :"+orderNameMapping);
            objectArgs.put("ajaxKey", orderNameMapping);
	        resultObject.setResultCode(ErrorConstants.SUCCESS); 
	        resultObject.setResultValue(objectArgs);
	        resultObject.setViewName("ajaxData");
		}
		catch(Exception e){
			logger.error("Exception while generating VO to XML ", e);
			ResultObject retObj = new ResultObject(ErrorConstants.ERROR);
			retObj.setViewName("ajaxError");
			objectArgs.put("ajaxKey", "ERROR");
			retObj.setResultValue(objectArgs);
			retObj.setThrowable(e);
			return retObj;

		}
		return resultObject;
	}
	
	
	public ResultObject showAdminGRForm(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			setSessionInfo(inputMap);
			AdminPostDtlsDAO lObjOrgPostDtlDao = new AdminPostDtlsDAOImpl(null,serv.getSessionFactory());
			
			List<ComboValuesVO> lLstFieldDept = lObjOrgPostDtlDao.getFieldDeptFromAdminDeptCode(gLngLocationCode);
			
			inputMap.put("lLstFieldDept", lLstFieldDept);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(logger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("AdminGRForm");
		return resObj;
	}	
	
}

