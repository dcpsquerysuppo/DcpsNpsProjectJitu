package com.tcs.sgv.dcps.service;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.tool.hbm2x.StringUtils;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.ExcelParser;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.UploadPranDAO;
import com.tcs.sgv.dcps.dao.UploadPranDaoImpl;
import com.tcs.sgv.dcps.valueobject.UploadPranNo;

public class UploadPranNoServiceImpl extends ServiceImpl
{

	Long gLngUserId = null;
	Long gLngPostId = null;
	private Long gLngLangId = null; /* LANG ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private Date gDtCurDate = null; /* CURRENT DATE */
	private final String gStrPostId = null; /* STRING POST ID */
	Log logger = LogFactory.getLog(this.getClass());

	@SuppressWarnings("unchecked")
	public ResultObject openReportForUploadedPran(final Map inputMap) throws Exception
	{

		

		String FileId="";
		 	String status="";
		final ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try
		{

			this.setSessionInfo(inputMap);
			
			if(StringUtility.getParameter("FileId", this.request)!=null)
			{
				FileId=StringUtility.getParameter("FileId", this.request);
			}
			if(StringUtility.getParameter("status", this.request)!=null)
			{
				status=StringUtility.getParameter("status", this.request);
			}
			UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
			List l= lObjUploadPranNo.getUploadedReportDtls(FileId, status);
			int sizeOfList=0;
			if(l!=null)
			{
				sizeOfList=l.size();
			}
			inputMap.put("list", l);
			inputMap.put("sizeOfList", sizeOfList);
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("uploadedPranNoDtls");
			
			
			


		} catch (final Exception e)
		{
			resultObject.setResultValue(null);
			resultObject.setThrowable(e);
			resultObject.setResultCode(ErrorConstants.ERROR);
			resultObject.setViewName("errorPage");
		}

		return resultObject;
	}
	
	public ResultObject loadUploadPranNo(final Map inputMap) throws Exception
	{

		


		final ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try
		{

			this.setSessionInfo(inputMap);
			UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
			List l= lObjUploadPranNo.getUploadedDtls();
			int sizeOfList=0;
			if(l!=null)
			{
				sizeOfList=l.size();
			}
			inputMap.put("list", l);
			inputMap.put("sizeOfList", sizeOfList);
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("uploadPranNo");
			
			
			


		} catch (final Exception e)
		{
			resultObject.setResultValue(null);
			resultObject.setThrowable(e);
			resultObject.setResultCode(ErrorConstants.ERROR);
			resultObject.setViewName("errorPage");
		}

		return resultObject;
	}

	 public ResultObject importExcel(Map objectArgs) throws Exception
	    {
	        logger.info("Inside Get uploadGr");
	        ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
	        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	        HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
	        Long attachment_Id_order=0l;
	        Object[][] xlsData = null;
	        UploadPranNo objUploadPranNo=null;
	        String desc="";
	        Long attachId=0l;
	        String isExcel="Yes";
	        try{
	            setSessionInfo(objectArgs);
	           // PRTrackingDAO lobjPRTrackingDAOImpl = new PRTrackingDAOImpl(PRTrackingDAOImpl.class, this.serv.getSessionFactory());
	        	UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
	            String currRowNum="1";
	            objectArgs.put("rowNumber",currRowNum);
	            objectArgs.put("attachmentName","orderId");
	            resultObject = serv.executeService("FILE_UPLOAD_VOGEN",objectArgs);
	            Map resultMap=(Map)resultObject.getResultValue();
	            resultObject = serv.executeService("FILE_UPLOAD_SRVC", objectArgs);
	            resultMap = (Map) resultObject.getResultValue();
	            if(resultMap.get("AttachmentId_orderId")!=null)
	                attachment_Id_order = (Long) resultMap.get("AttachmentId_orderId"); 
	            logger.info("attachment_Id_order is "+attachment_Id_order);

	            
	            if (attachment_Id_order != null) {
	             
	                logger.info("attachment_Id_order inside ******************* " );
	                
	                CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
	                CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(attachment_Id_order);
	                Iterator lObjIterator = cmnAttachmentMst.getCmnAttachmentMpgs().iterator();
	                if(cmnAttachmentMst.getAttachmentId() > 0)
	                {
	                	attachId=cmnAttachmentMst.getAttachmentId();
	                }
	                logger.info("attachment_Id_order inside **********2********* " );
	                while (lObjIterator != null && lObjIterator.hasNext()) {
	                    CmnAttachmentMpg cmnAttachmentMpg = (CmnAttachmentMpg) lObjIterator.next();
	                    if(cmnAttachmentMpg.getAttachmentDesc()!=null)
	                    {
	                    	 desc=cmnAttachmentMpg.getAttachmentDesc();
	                    	
	                    }
	                    
	                    CmnAttdocMst cmnAttDocMst = (CmnAttdocMst) cmnAttachmentMpg.getCmnAttdocMsts().iterator().next();
	                    logger.info("attachment_Id_order inside **********3******** " );
	                    String lStrFileName = cmnAttachmentMpg.getOrgFileName().trim();
	                    int lIntDotPos = lStrFileName.lastIndexOf(".");
	                    String lStrExtension = lStrFileName.substring(lIntDotPos);
	                    Integer lIntRowSize = 0;
	                    Long fileId=0l;
	                    if(lStrExtension.equalsIgnoreCase(".xls"))
	                    {
	                        if (cmnAttDocMst != null) 
		                    {
		                        List lObjSheetLst = ExcelParser.parseExcel(new ByteArrayInputStream(cmnAttDocMst.getFinalAttachment()));
		                        //List lObjSheetLst = ExcelParser.parseExcel(cmnAttDocMst.getFinalAttachment());
		                        logger.info("attachment_Id_order inside **********4******* " );
		                        if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
		                                List lObjRowLst = (List) lObjSheetLst.get(0);
		                                if (lObjRowLst != null && !lObjRowLst.isEmpty()) {
		                                        lIntRowSize = lObjRowLst.size();
		                                        xlsData = new Object[lObjRowLst.size()][];
		                                        for (int i = 0; i < lObjRowLst.size(); ++i) {
		                                                xlsData[i] = ((List) lObjRowLst.get(i)).toArray();
		                                        }
		                                        if(xlsData!=null)
		                                        {
		                                        	fileId=lObjUploadPranNo.getFileId();
		                                        	  for (int i = 1; i < lObjRowLst.size(); ++i) {
		  	                                        	objUploadPranNo= new UploadPranNo();
		  	                                        	if(StringUtils.isNumeric(xlsData[i][1].toString().trim()))
		  	                                        	{
		  	                                        		boolean flag=lObjUploadPranNo.checkIfPranUploaded(xlsData[i][1].toString().trim());
		  	                                        		if(!flag)
		  	                                        		{
		  	                                        			objUploadPranNo.setPranNo(xlsData[i][1].toString().trim());
				  	                                        	objUploadPranNo.setPpan(xlsData[i][2].toString().trim());
				  	                                        	objUploadPranNo.setEmpName(xlsData[i][3].toString().trim());
				  	                                        	/*if(xlsData[i][4]!=null)
				  	                                        	{
				  	                                        		String s=xlsData[i][4].toString().substring(0, 2)+"/"+xlsData[i][4].toString().substring(3, 5)+"/"+xlsData[i][4].toString().substring(6, 10);
				  	                                        		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				  	                                        		Date d=fmt.parse(s);  
				  	                              				    objUploadPranNo.setDoj(d);
				  	                                        	}*/
				  	                                        	/*if(xlsData[i][5]!=null)
				  	                                        	{
				  	                                        		String s=xlsData[i][5].toString().substring(0, 2)+"/"+xlsData[i][5].toString().substring(3, 5)+"/"+xlsData[i][5].toString().substring(6, 10);
				  	                                        		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				  	                              				    Date d=fmt.parse(s);  
				  	                              				    objUploadPranNo.setPranGenDate(d);
				  	                                        	}*/
				  	                                        	
				  	                                        	SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			  	                              				    Date d=new Date();
			  	                              				    objUploadPranNo.setPranGenDate(d);
				  	                                        	
				  	                                        	objUploadPranNo.setDtoCode(Long.parseLong(xlsData[i][6].toString().trim()));
				  	                                        	objUploadPranNo.setDdoRegNo(xlsData[i][7].toString().trim());
				  	                                        	objUploadPranNo.setCreatedDate(gDtCurDate);
				  	                                        	objUploadPranNo.setCreatedPostId(gLngPostId);
				  	                                        	Long uploadId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PRAN_UPLOAD_DTLS", objectArgs);
				  	                                        	objUploadPranNo.setUploadPranId(uploadId);
				  	                                        	objUploadPranNo.setFileName(lStrFileName);
				  	                                        	objUploadPranNo.setFileId(fileId);
				  	                                        	objUploadPranNo.setStatus(1);
				  	                                        	objUploadPranNo.setAttachDesc(desc);
				  	                                        	objUploadPranNo.setAttachId(attachId);
		  	                                     
		  	                                        		}
		  	                                        		else
		  	                                        		{
		  	                                        			objUploadPranNo.setPranNo(xlsData[i][1].toString().trim());
				  	                                        	objUploadPranNo.setPpan(xlsData[i][2].toString().trim());
				  	                                        	objUploadPranNo.setEmpName(xlsData[i][3].toString().trim());
				  	                                        	/*if(xlsData[i][4]!=null)
				  	                                        	{
				  	                                        		logger.error("Data "+ xlsData[i][4].toString());
				  	                                        		String s=xlsData[i][4].toString().substring(0, 2)+"/"+xlsData[i][4].toString().substring(3, 5)+"/"+xlsData[i][4].toString().substring(6, 10);
				  	                                        		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				  	                                        		Date d=fmt.parse(s);  
				  	                              				    objUploadPranNo.setDoj(d);
				  	                                        	}*/
				  	                                        	/*if(xlsData[i][5]!=null)
				  	                                        	{
				  	                                        		logger.error("Data "+ xlsData[i][5].toString());
				  	                                        		String s=xlsData[i][5].toString().substring(0, 2)+"/"+xlsData[i][5].toString().substring(3, 5)+"/"+xlsData[i][5].toString().substring(6, 10);
				  	                                        		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				  	                              				    Date d=fmt.parse(s);  
				  	                              				    objUploadPranNo.setPranGenDate(d);
				  	                                        	}*/
				  	                                        	
				  	                                        	SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			  	                              				    Date d=new Date();
			  	                              				    objUploadPranNo.setPranGenDate(d);
				  	                                        	
				  	                                        	objUploadPranNo.setDtoCode(Long.parseLong(xlsData[i][6].toString().trim()));
				  	                                        	objUploadPranNo.setDdoRegNo(xlsData[i][7].toString().trim());
		  	                                        			
		  	                                        			objUploadPranNo.setCreatedDate(gDtCurDate);
				  	                                        	objUploadPranNo.setCreatedPostId(gLngPostId);
				  	                                        	Long uploadId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PRAN_UPLOAD_DTLS", objectArgs);
				  	                                        	objUploadPranNo.setUploadPranId(uploadId);
				  	                                        	objUploadPranNo.setFileName(lStrFileName);
				  	                                        	objUploadPranNo.setFileId(fileId);
				  	                                        	objUploadPranNo.setStatus(0);
				  	                                        	objUploadPranNo.setAttachDesc(desc);
				  	                                        	objUploadPranNo.setAttachId(attachId);
		  	                                        		}
		  	                                        		
			  	                                        	lObjUploadPranNo.create(objUploadPranNo);
		  	                                        	}
		  	                                        	
		                                          }
		                                        }
		                                     
		                                }
		                        }
		                        
		                    }
	                    }
	                    else
	                    {
	                    	isExcel="No";
	                    }
	            
	       
	                    }
	              // List<OrgTicketMst> lLstTicket = new ArrayList<OrgTicketMst>();
	            //   lobjPRTrackingDAOImpl.insertExecelData(xlsData,objectArgs);
	               
	                
	            }
	            /*UploadPranDAO lObj1UploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
	            List l= lObj1UploadPranNo.getUploadedDtls();
				int sizeOfList=0;
				if(l!=null)
				{
					sizeOfList=l.size();
				}
				objectArgs.put("list", l);*/
				objectArgs.put("reload", "Yes");
				objectArgs.put("isExecl", isExcel);
				resultObject.setResultValue(objectArgs);
				resultObject.setViewName("uploadPranNo");
				
				//resultObject = serv.executeService("reloadUploadPranDtls",objectArgs);

	        }catch(Exception e){
	            resultObject = new ResultObject(ErrorConstants.ERROR);
	            resultObject.setResultCode(-1);
	            resultObject.setViewName("errorPage");
	            e.printStackTrace();
	            logger.error("Error in mapDDOCodeList "+ e);
	            throw e;
	        }
	        return resultObject;
	    }
	 
	 
	 public ResultObject reloadUploadPranDtls(Map objectArgs)
	    {
	        logger.info("Inside Get uploadGr");
	        ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
	        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	        HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
	        Long attachment_Id_order=0l;
	        Object[][] xlsData = null;
	        UploadPranNo objUploadPranNo=null;
	        try{
	            setSessionInfo(objectArgs);
	           
	          
	            UploadPranDAO lObj1UploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
	            List l= lObj1UploadPranNo.getUploadedDtls();
				int sizeOfList=0;
				if(l!=null)
				{
					sizeOfList=l.size();
				}
				objectArgs.put("list", l);
				objectArgs.put("sizeOfList", sizeOfList);
	           
				resultObject.setResultValue(objectArgs);
				resultObject.setViewName("uploadPranNo");
				
				

	        }catch(Exception e){
	            resultObject = new ResultObject(ErrorConstants.ERROR);
	            resultObject.setResultCode(-1);
	            resultObject.setViewName("errorPage");
	            logger.error("Error in mapDDOCodeList "+ e);
	        }
	        return resultObject;
	    }
	 
		private StringBuilder getResponseXMLDoc(String flag,String pranNo) {
		
			StringBuilder lStrBldXML = new StringBuilder();

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<Flag>");
			lStrBldXML.append(flag);
			lStrBldXML.append("</Flag>");
			lStrBldXML.append("<pranNo>");
			lStrBldXML.append(pranNo);
			lStrBldXML.append("</pranNo>");
			lStrBldXML.append("</XMLDOC>");

			return lStrBldXML;
		}
	/*    public ResultObject filterScreen(final Map inputMap) throws Exception
    {

        this.logger.info("hiii i m in filterScreen ");

        final ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

        try
        {

            this.setSessionInfo(inputMap);
            final PRTrackingDAO prTrackingDAO = new PRTrackingDAOImpl(PRTrackingDAOImpl.class, this.serv
                    .getSessionFactory());
            PRTrackingDAO orgTicketMstDAO = new PRTrackingDAOImpl(OrgTicketMst.class, this.serv
                    .getSessionFactory());
            OrgTicketMst orgTicketMst;
            final String flag = StringUtility.getParameter("flag", this.request);
            inputMap.put("flag", flag);
            logger.info("flag-----"+flag);
            String lStrUser = StringUtility.getParameter("lStrUser", this.request);
            inputMap.put("lStrUser", lStrUser);

            String moduleName = StringUtility.getParameter("selModule", this.request);
            inputMap.put("moduleName", moduleName);

            System.out.println("moduleName****"+moduleName);

            Long ticketId = Long.parseLong((StringUtility.getParameter("ticketId", this.request)!=null && !StringUtility.getParameter("ticketId", this.request).equals(""))?(StringUtility.getParameter("ticketId", this.request)):"0");
            if(ticketId!= 0)
            {
                orgTicketMst = (OrgTicketMst) orgTicketMstDAO.read(ticketId);
                inputMap.put("orgTicketMst", orgTicketMst);



                inputMap.put("strRemarks", clobStringConversion(orgTicketMst.getRemarks()));
                logger.info("strRemarks----------"+clobStringConversion(orgTicketMst.getRemarks()));

                prTrackingDAO.updateTicketFlag(ticketId);
            }


            // Screen List
            final List lLstScreen = prTrackingDAO.getScreenFilter(moduleName);
            inputMap.put("lLstScreen", lLstScreen);


            // STatus List
            final List lLstStatus = IFMSCommonServiceImpl.getLookupValues("TicketStatus", SessionHelper
                    .getLangId(inputMap), inputMap);
            inputMap.put("lLstStatus", lLstStatus);
            inputMap.put("lLstModules", "5");

            // Priority List
            final List lLstPriority = IFMSCommonServiceImpl.getLookupValues("TicketPriority", SessionHelper
                    .getLangId(inputMap), inputMap);
            inputMap.put("lLstPriority", lLstPriority);



            resultObject.setResultValue(inputMap);
            resultObject.setViewName("NewTicketEntry");

        } catch (final Exception e)
        {
            resultObject.setResultValue(null);
            resultObject.setThrowable(e);
            resultObject.setResultCode(ErrorConstants.ERROR);
            resultObject.setViewName("errorPage");
        }

        return resultObject;
    }*/

	

	@SuppressWarnings("unchecked")
	


	private void setSessionInfo(final Map inputMap) throws Exception
	{

		try
		{
			this.request = (HttpServletRequest) inputMap.get("requestObj");
			this.serv = (ServiceLocator) inputMap.get("serviceLocator");
			this.gLngPostId = SessionHelper.getPostId(inputMap);
			this.gLngUserId = SessionHelper.getUserId(inputMap);
			this.gDtCurDate = SessionHelper.getCurDate();
			this.gLngLangId = SessionHelper.getLangId(inputMap);

		} catch (final Exception e)
		{
			this.logger.error("Error in setSessionInfo of changeNameServiceImpl ", e);
			throw e;
		}
	}



	
	
 /*   public ResultObject uploadExcel(Map objectArgs)
    {
        logger.info("Inside Get uploadGr");
        ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
        ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
        HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
        Long attachment_Id_order=0l;
        Object[][] xlsData = null;
        try{
            setSessionInfo(objectArgs);
            PRTrackingDAO lobjPRTrackingDAOImpl = new PRTrackingDAOImpl(PRTrackingDAOImpl.class, this.serv.getSessionFactory());
            String currRowNum="1";
            objectArgs.put("rowNumber",currRowNum);
            objectArgs.put("attachmentName","orderId");
            resultObject = serv.executeService("FILE_UPLOAD_VOGEN",objectArgs);
            Map resultMap=(Map)resultObject.getResultValue();
            resultObject = serv.executeService("FILE_UPLOAD_SRVC", objectArgs);
            resultMap = (Map) resultObject.getResultValue();
            if(resultMap.get("AttachmentId_orderId")!=null)
                attachment_Id_order = (Long) resultMap.get("AttachmentId_orderId"); 
            logger.info("attachment_Id_order is "+attachment_Id_order);

            
            if (attachment_Id_order != null) {
             
                logger.info("attachment_Id_order inside ******************* " );
                
                CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
                CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(attachment_Id_order);
                Iterator lObjIterator = cmnAttachmentMst.getCmnAttachmentMpgs().iterator();
                Integer lIntRowSize = 0;
                logger.info("attachment_Id_order inside **********2********* " );
                while (lObjIterator != null && lObjIterator.hasNext()) {
                    CmnAttachmentMpg cmnAttachmentMpg = (CmnAttachmentMpg) lObjIterator.next();
                    CmnAttdocMst cmnAttDocMst = (CmnAttdocMst) cmnAttachmentMpg.getCmnAttdocMsts().iterator().next();
                    logger.info("attachment_Id_order inside **********3******** " );
                    String lStrFileName = cmnAttachmentMpg.getOrgFileName().trim();
                    int lIntDotPos = lStrFileName.lastIndexOf(".");
                    String lStrExtension = lStrFileName.substring(lIntDotPos);
                 
                    if (cmnAttDocMst != null) {
                        List lObjSheetLst = ExcelParser.parseExcel(new ByteArrayInputStream(cmnAttDocMst.getFinalAttachment()));
                        logger.info("attachment_Id_order inside **********4******* " );
                        if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
                                List lObjRowLst = (List) lObjSheetLst.get(0);////here to get the first sheet of excel
                                if (lObjRowLst != null && !lObjRowLst.isEmpty()) {
                                        lIntRowSize = lObjRowLst.size(); /// Number of rows will be this 
                                        xlsData = new Object[lObjRowLst.size()][];
                                        for (int i = 0; i < lObjRowLst.size(); ++i) {
                                                xlsData[i] = ((List) lObjRowLst.get(i)).toArray();
                                        }
                                }
                        }
                        
                    }
       
                    }
                
                final Map loginMap = (Map) objectArgs.get("baseLoginMap");
            	final PayBillDAOImpl payDAO = new PayBillDAOImpl(HrPayPaybill.class, this.serv.getSessionFactory());
            	final long locId = StringUtility.convertToLong(loginMap.get("locationId").toString()).longValue();
            	
                final List<OrgDdoMst> ddoList = payDAO.getDDOCodeByLoggedInlocId(locId);
        		OrgDdoMst ddoMst = null;
        		String ddoCode="";
        		
        		if (ddoList != null && ddoList.size() > 0)
        		{
        			ddoMst = ddoList.get(0);
        			this.logger.info("hiii i m finding ddo code");
        		}

        		if (ddoMst != null)
        		{
        			ddoCode = ddoMst.getDdoCode();
        		}
                
                
                Long id=0l;
                logger.info("lIntRowSize inside ***************** "+lIntRowSize );
              // List<OrgTicketMst> lLstTicket = new ArrayList<OrgTicketMst>();
                lobjPRTrackingDAOImpl.insertExecelData(xlsData,objectArgs,lIntRowSize,ddoCode,gLngUserId,gLngPostId);
               
                
            }
            resultObject.setResultCode(ErrorConstants.SUCCESS);
            resultObject.setResultValue(objectArgs);//put in result object
            resultObject.setViewName("LoadEmpTickets");//set view name

        }catch(Exception e){
            resultObject = new ResultObject(ErrorConstants.ERROR);
            resultObject.setResultCode(-1);
            resultObject.setViewName("errorPage");
            logger.error("Error in mapDDOCodeList "+ e);
        }
        return resultObject;
    }*/




}
