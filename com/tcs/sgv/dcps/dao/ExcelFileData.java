/*package com.tcs.sgv.dcps.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.ExcelParser;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.UploadPranNo;


public class ExcelFileData extends ServiceImpl
{
	
	Long gLngUserId = null;
	Long gLngPostId = null;
	private Long gLngLangId = null;  LANG ID 
	private HttpServletRequest request = null;  REQUEST OBJECT 
	private ServiceLocator serv = null;  SERVICE LOCATOR 
	private Date gDtCurDate = null;  CURRENT DATE 
	private final String gStrPostId = null;  STRING POST ID 
	Log logger = LogFactory.getLog(this.getClass());

public ResultObject importExcel(Map inputMap) throws Exception {
			ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			logger.info("Inside file read ");

			String fileName = null;
			  Integer result=null;	
			  String status="NO";
			  
			  
			try {
				setSessionInfo(inputMap);
				   Integer lIntRowSize = 0;
				   Object[][] xlsData = null;
				   UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
				//DeleteCsrfFormDaoImpl objUpdateDao = new DeleteCsrfFormDaoImpl(null, serv.getSessionFactory());
				fileName = StringUtility.getParameter("filename", request).trim().toUpperCase();
				if (StringUtility.getParameter("filename", request).trim() != null) {
					fileName = StringUtility.getParameter("filename", request).trim().toUpperCase();
					
                  	   
                 	   
                 	   File inputStream = new File(fileName);	                  
		              List lObjSheetLst = ExcelParser.parseExcel(inputStream);
		             // List lObjSheetLst1=ExcelFileParserPran.parseExcel(fileName);
		             if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
                         List lObjRowLst = (List) lObjSheetLst.get(0);
                         System.out.println("list of data" +lObjRowLst);
                     }
		             if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
                         List lObjRowLst = (List) lObjSheetLst.get(0);
                         if (lObjRowLst != null && !lObjRowLst.isEmpty()) {
                                 lIntRowSize = lObjRowLst.size();
                                 xlsData = new Object[lObjRowLst.size()][];
                                 for (int i = 1; i < lObjRowLst.size(); ++i) {
                                         xlsData[i] = ((List) lObjRowLst.get(i)).toArray();
                                         System.out.println("list of data" +xlsData[i].toString()+"data:-"+ lObjRowLst.get(i));
                                         int l=xlsData[i].length;
                                        System.out.println("data array"+xlsData[i][2].toString().trim());
                                     //  emplList.add(xlsData[i][0].toString().trim());
                                   //    boolean flag=lObjUploadPranNo.checkIfPranUploaded(xlsData[i][1].toString().trim());
                                        result=lObjUploadPranNo.activePranNOUpdate(xlsData[i][0].toString().trim());                                        
                                 }      
                                 if(result!=null || result!=0){
                                	 
                                	 status="YES";
                                 }
                         }
                     }
				}
				   
				inputMap.put("reload", "Yes");
				     inputMap.put("isExecl", status);
					objRes.setResultValue(inputMap);
					objRes.setViewName("ExcelFileUpload");		
					
			}
				catch (Exception e) {
					logger.info("Exception is " + e);
					objRes.setResultValue(null);
					objRes.setThrowable(e);
					objRes.setResultCode(ErrorConstants.ERROR);
					objRes.setViewName("errorPage");
				}
				return objRes;
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
	

}
 */

package com.tcs.sgv.dcps.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
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
import com.tcs.sgv.dcps.valueobject.UploadPranNo;

public class ExcelFileData extends ServiceImpl {

	Long gLngUserId = null;
	Long gLngPostId = null;
	private Long gLngLangId = null; /* LANG ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private Date gDtCurDate = null; /* CURRENT DATE */
	private final String gStrPostId = null; /* STRING POST ID */
	Log logger = LogFactory.getLog(this.getClass());

	@SuppressWarnings("unchecked")
	public ResultObject openReportForUploadedPran(final Map inputMap)
			throws Exception {

		String FileId = "";
		String status = "";
		final ResultObject resultObject = new ResultObject(
				ErrorConstants.SUCCESS, "FAIL");

		try {

			this.setSessionInfo(inputMap);

			if (StringUtility.getParameter("FileId", this.request) != null) {
				FileId = StringUtility.getParameter("FileId", this.request);
			}
			if (StringUtility.getParameter("status", this.request) != null) {
				status = StringUtility.getParameter("status", this.request);
			}
			UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(
					UploadPranNo.class, serv.getSessionFactory());
			List l = lObjUploadPranNo.getUploadedReportDtls(FileId, status);
			int sizeOfList = 0;
			if (l != null) {
				sizeOfList = l.size();
			}
			inputMap.put("list", l);
			inputMap.put("sizeOfList", sizeOfList);
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("uploadedPranNoDtls");

		} catch (final Exception e) {
			resultObject.setResultValue(null);
			resultObject.setThrowable(e);
			resultObject.setResultCode(ErrorConstants.ERROR);
			resultObject.setViewName("errorPage");
		}

		return resultObject;
	}

	public ResultObject loadUploadPranNo(final Map inputMap) throws Exception {

		final ResultObject resultObject = new ResultObject(
				ErrorConstants.SUCCESS, "FAIL");

		try {

			this.setSessionInfo(inputMap);
			UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(
					UploadPranNo.class, serv.getSessionFactory());
			List l = lObjUploadPranNo.getUploadedDtls();
			int sizeOfList = 0;
			if (l != null) {
				sizeOfList = l.size();
			}
			inputMap.put("list", l);
			inputMap.put("sizeOfList", sizeOfList);
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("uploadPranNo");

		} catch (final Exception e) {
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
	        List lObjSheetLst=null;
	        ArrayList emplList= new ArrayList();
	        Integer result;
	        try{
	        	
	        	
	        	
	        	 setSessionInfo(objectArgs);
		         	UploadPranDAO lObjUploadPranNo = new UploadPranDaoImpl(UploadPranNo.class, serv.getSessionFactory());
		            String currRowNum="1";
		            objectArgs.put("rowNumber",currRowNum);
		            objectArgs.put("attachmentName","orderId");
		            resultObject = serv.executeService("FILE_UPLOAD_VOGEN",objectArgs);
		            Map resultMap=(Map)resultObject.getResultValue();
		            resultObject = serv.executeService("FILE_UPLOAD_SRVC", objectArgs);
		            resultMap = (Map) resultObject.getResultValue();
		            attachment_Id_order = (Long) resultMap.get("AttachmentId_orderId"); 
	            logger.info("attachment_Id_order is "+attachment_Id_order);
	            String parentDirectoryPath = request.getSession().getServletContext().getRealPath("/UPLOADED-FILES")+"/";
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
	                    
	                    String source= parentDirectoryPath+lStrFileName;
	                   
	                   // boolean fileRenameStatuse=false;
	                                        
	                    Boolean fileRenameStatuse=null;                    
	                     lStrFileName = cmnAttachmentMpg.getOrgFileName().trim();
	                     lIntDotPos = lStrFileName.lastIndexOf(".");
	                     String filename=lStrFileName.substring(0,lIntDotPos);
	                     
	                    // File f=null;
	                     String dirc="";
	                     String values="";
	                    if(lStrExtension.equalsIgnoreCase(".xls")|| lStrExtension.equalsIgnoreCase(".xlsx"))
	                    {
	                        if (cmnAttDocMst != null) 
		                    {   
		                        
		                        
		                    		                       
		                       if( lStrExtension.equalsIgnoreCase(".xlsx"))
		                       {
		                    	   InputStream byteArrayInputStream= (new ByteArrayInputStream(cmnAttDocMst.getFinalAttachment()));
		                    		
				                      byte array[]= readAllBytes(byteArrayInputStream);
				                 	 dirc=parentDirectoryPath+lStrFileName;
				                    	  
			                    	   File f=new File(dirc);
			                    	   f.createNewFile();
			                    	   try {

				                         FileOutputStream fileOutputStream=new FileOutputStream(f);
				                         fileOutputStream.write(array);
				                         fileOutputStream.close();
				                         }
				                       catch(Exception e) {
				                         e.getStackTrace();
				                       
				                       }
		                     List ret = new ArrayList<String>();
		                     URL obj = new URL("http://localhost:8181/readExcelXLSX");
		                     HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		                     con.setRequestMethod("POST");
		                     con.setRequestProperty("Content-Type", "application/json"); 
		                     con.setRequestProperty("charset","UTF-8");
		              		 //System.out.println(dirc);
		                     
		                     String input = "{\"fileName\": \""+lStrFileName+"\"}";
		                     System.out.println("string "+input);
		               		con.setDoOutput(true);
		               		OutputStream os = con.getOutputStream();
		               		os.write(input.getBytes());
		               		os.flush();
		               		os.close();
		               		
		               		int responseCode = con.getResponseCode();
		               		System.out.println("POST Response Code :: " + responseCode);
		               		String strArray = null;

		               		if (responseCode == HttpURLConnection.HTTP_OK) { //success
		               			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		               			String inputLine;
		               			StringBuffer response = new StringBuffer();
		               			while ((inputLine = in.readLine()) != null) {
		               				strArray = inputLine;
			               			
		               			}
		               			in.close();
		               			System.out.println("output string "+strArray);
		               			result=lObjUploadPranNo.activePranNOUpdate(strArray);
		               		//	strArray = strArray.length;
		               			
		               		} else {
		               			System.out.println("POST request did not work.");
		               		}	
		               		
		               		
		                   }
		                       /* start for xls file*/ 
		                   
		                       if( lStrExtension.equalsIgnoreCase(".xls"))
		                       {
		                    	   lObjSheetLst = ExcelParser.parseExcel(new ByteArrayInputStream(cmnAttDocMst.getFinalAttachment()));
		                        logger.info("attachment_Id_order inside **********4******* " );
		                        if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
		                                List lObjRowLst = (List) lObjSheetLst.get(0);
		                                if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
		                                    //List lObjRowLst = (List) lObjSheetLst.get(0);
		                                    if (lObjRowLst != null && !lObjRowLst.isEmpty()) {
		                                            lIntRowSize = lObjRowLst.size();
		                                            xlsData = new Object[lObjRowLst.size()][];
		                                            for (int i = 1; i < lObjRowLst.size(); ++i) {
		                                                    xlsData[i] = ((List) lObjRowLst.get(i)).toArray();
		                                                   // System.out.println("list of data" +xlsData[i].toString()+"data:-"+ lObjRowLst.get(i));
		                                                     emplList.add(xlsData[i][0]);
		                                                                              
		                                               //   boolean flag=lObjUploadPranNo.checkIfPranUploaded(xlsData[i][1].toString().trim());
		                                                  //result=lObjUploadPranNo.activePranNOUpdate(xlsData[i][0].toString().trim());                                        
		                                            }      
		                                            values   = (String) emplList.stream().map(str -> String.format("'%s'", str)).collect(Collectors.joining(","));
		                                            System.out.println("data array"+values);
		                                            result=lObjUploadPranNo.activePranNOUpdate(values);
		                                            /*if(result!=null || result!=0){
		                                           	 
		                                           	 status="YES";
		                                            }*/
		                                    }
		                                }
		                        }
		                       }
		                        /*end for xlx file*/
		                    }
	                    }else
	                    {
	                    	isExcel="No";
	                    }
	            
	       
	                    }
  
	            }
	            
				objectArgs.put("reload", "Yes");
				objectArgs.put("isExecl", isExcel);
				resultObject.setResultValue(objectArgs);
				resultObject.setViewName("ExcelFileUpload1");
				
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

	public ResultObject reloadUploadPranDtls(Map objectArgs) {
		logger.info("Inside Get uploadGr");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		Long attachment_Id_order = 0l;
		Object[][] xlsData = null;
		UploadPranNo objUploadPranNo = null;
		try {
			setSessionInfo(objectArgs);

			UploadPranDAO lObj1UploadPranNo = new UploadPranDaoImpl(
					UploadPranNo.class, serv.getSessionFactory());
			List l = lObj1UploadPranNo.getUploadedDtls();
			int sizeOfList = 0;
			if (l != null) {
				sizeOfList = l.size();
			}
			objectArgs.put("list", l);
			objectArgs.put("sizeOfList", sizeOfList);

			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("uploadPranNo");

		} catch (Exception e) {
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in mapDDOCodeList " + e);
		}
		return resultObject;
	}

	private StringBuilder getResponseXMLDoc(String flag, String pranNo) {

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

	@SuppressWarnings("unchecked")
	private void setSessionInfo(final Map inputMap) throws Exception {

		try {
			this.request = (HttpServletRequest) inputMap.get("requestObj");
			this.serv = (ServiceLocator) inputMap.get("serviceLocator");
			this.gLngPostId = SessionHelper.getPostId(inputMap);
			this.gLngUserId = SessionHelper.getUserId(inputMap);
			this.gDtCurDate = SessionHelper.getCurDate();
			this.gLngLangId = SessionHelper.getLangId(inputMap);

		} catch (final Exception e) {
			this.logger.error(
					"Error in setSessionInfo of changeNameServiceImpl ", e);
			throw e;
		}
	}

	public static byte[] readAllBytes(InputStream inputStream)
			throws IOException {
		final int bufLen = 4 * 0x400; // 4KB
		byte[] buf = new byte[bufLen];
		int readLen;
		IOException exception = null;

		try {
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
					outputStream.write(buf, 0, readLen);

				return outputStream.toByteArray();
			}
		} catch (IOException e) {
			exception = e;
			throw e;
		} finally {
			if (exception == null)
				inputStream.close();
			else
				try {
					inputStream.close();
				} catch (IOException e) {
					exception.addSuppressed(e);
				}
		}
	}

	public static File changeExtension(File f, String newExtension) {
		int i = f.getName().lastIndexOf('.');
		String name = f.getName().substring(0, i);
		return new File(f.getParent(), name + newExtension);
	}
}
