package com.tcs.sgv.dcps.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.Service;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.FormS1DAO;
import com.tcs.sgv.dcps.dao.FormS1DAOImpl;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.dao.OpgmFileViewDao;
import com.tcs.sgv.dcps.dao.OpgmFileViewDaoImpl;
import com.tcs.sgv.dcps.dao.TierIISixPcArrearDao;
import com.tcs.sgv.dcps.dao.TierIISixPcArrearDaoImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;

public class OpgmFileViewServiceImpl extends ServiceImpl   {
	
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */
	
	private HttpServletResponse response= null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */
	/* Global Variable for Logger Class */

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

	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	private Long lLngLocId;

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			response = (HttpServletResponse) inputMap.get("responseObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			lLngLocId = Long.valueOf(SessionHelper.getLocationCode(inputMap));
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ResultObject getOpgmFileList(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List opgmFileList=null;
		//String strDDOCode=null;
		String strRoleId=null;
		int DepSize = 100;
		try
		{	
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			OpgmFileViewDao lObjSearchEmployeeDAO = new OpgmFileViewDaoImpl(MstEmp.class, serv.getSessionFactory());
			//strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			///strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
			//for year
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl( null, this.serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,this.serv.getSessionFactory());
		    List lLstYears = lObjSearchEmployeeDAO.getFinyear();
		    List lLstMonths = lObjSearchEmployeeDAO.getMonths();

		    String month = StringUtility.getParameter("cmbMonth", request);
			String year = StringUtility.getParameter("cmbYear", request);
			
        	String isLoad="N";
			gLogger.info("##################DepSize"+DepSize); 
			if(StringUtility.getParameter("isLoad", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("isLoad", request)))
			{
				isLoad=StringUtility.getParameter("isLoad", request);
			}
			Calendar cal = Calendar.getInstance();
			String currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
			String curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
			Long currentyear=null;
			Long currentmonth=null;
//			if(currmonth.equals("1")){
//				currentmonth= 12L;
//				currentyear=Long.parseLong(curryear)-1;
//
//			}
//			else{
				currentmonth= Long.parseLong(currmonth)-1;
				currentyear=Long.parseLong(curryear);

//			}

			
			
			if(month.length()!=0)
			{
				inputMap.put("selMonth", month);
				inputMap.put("selYear", year);
				opgmFileList=lObjSearchEmployeeDAO.getOpgmFileList(this.gStrLocationCode,month,year);
	        	DepSize=opgmFileList.size();
			}
			else
			{
				inputMap.put("selMonth", currmonth);
				inputMap.put("selYear", curryear);
				opgmFileList=lObjSearchEmployeeDAO.getOpgmFileList(this.gStrLocationCode,currmonth,curryear);
	        	DepSize=opgmFileList.size();
			}
			
			
			inputMap.put("size", DepSize);
			inputMap.put("opgmFileList", opgmFileList);
			//inputMap.put("DDOCode", strDDOCode);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("OpgmFileView");
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}		
	
	//to delete File
		public ResultObject deleteOpgmFile(Map<String, Object> inputMap) {
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

			OpgmFileViewDao  lObjDeleteFileDAO= new OpgmFileViewDaoImpl(null, serv.getSessionFactory());
			setSessionInfo(inputMap);
			try {
				String status = "N";
				String Treasury = StringUtility.getParameter("Treasury", request);
				String FileNo = StringUtility.getParameter("FileNo", request);
				
				lObjDeleteFileDAO.deleteOpgmFile(Treasury,FileNo);
				status = "Y";
				
				String lSBStatus ="D"; 
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			} catch (Exception e) {
				gLogger.error("Error is;" + e, e);
				resObj.setResultValue(null);
			}
			return resObj;
		}
		

		//to download File
		public ResultObject downloadOpgmFile(Map<String, Object> inputMap) throws IOException {
			/*ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");*/
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);					
			setSessionInfo(inputMap);
			Session session = null;
			Channel channel = null;
			ChannelSftp channelSftp = null;
			OutputStream lOutStream = null;
			
			try {
				
				////vm for staging SFTP
				/*String SFTPHOST= "100.70.201.169"; 
				int SFTPPORT =22;  
				String SFTPUSER = "mahait";
				String SFTPPASS = "Mahait@99";*/
				////sftpvm
				////sftp live
				String SFTPHOST = "10.34.82.225";
				int SFTPPORT = 8888;
				String SFTPUSER = "tcsadmin";
				String SFTPPASS = "Tcsadmin@123";
			    ////sftp live				
				String parentDirectoryPath = request.getSession().getServletContext().getRealPath("/")+"/rpttemp";
				//System.out.println("Path is ------------------------------------------------------------>:"+parentDirectoryPath);
				/*int year = Calendar.getInstance().get(Calendar.YEAR);
				int month = Calendar.getInstance().get(Calendar.MONTH)+1;*/
				String month = StringUtility.getParameter("Month", request);
				String year = StringUtility.getParameter("Year", request);
				
				//String SFTPWORKINGDIR = "/upload/OPGM"+"/"+month+year+"/";////vm
				String SFTPWORKINGDIR = "/home/OPGM_TEMP"+"/"+month+year+"/";////live
				String fileNameGet = StringUtility.getParameter("FileNo", request);
				
		        /*for Sftp session starting/loading*/
				JSch jsch = new JSch();
				session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
				session.setPassword(SFTPPASS);
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				channel = session.openChannel("sftp");
				channel.connect();
				channelSftp = (ChannelSftp) channel;
				/*end  */
				//System.out.println("channelSftp = (ChannelSftp) channel; is ------------------------------------------------------------>:"+parentDirectoryPath);

				/*  new code for opgm */
				fileNameGet=fileNameGet+"_verified.txt";
				//System.out.println("fileNameGet is ------------------------------------------------------------>:"+fileNameGet);
				//System.out.println("SFTPWORKINGDIR + fileNameGet is ------------------------------------------------------------>:"+SFTPWORKINGDIR + fileNameGet);
				channelSftp.get(SFTPWORKINGDIR + fileNameGet,parentDirectoryPath);
				byte[] arrayOfByte = Files.readAllBytes(new File(parentDirectoryPath+ "/"  + fileNameGet).toPath());
			    HttpServletResponse response = (HttpServletResponse) inputMap.get("responseObj");
				lOutStream = response.getOutputStream();
				response.setContentType("text/plain;charset=UTF-8");
				response.addHeader("Content-disposition","attachment; filename="+fileNameGet);
				response.setCharacterEncoding("UTF-8");
			    lOutStream.write(arrayOfByte);
				lOutStream.flush();
				//System.out.println("download is ------------------------------------------------------------>:"+fileNameGet);
				File parentMainDirectory = new File(parentDirectoryPath+ "/"  + fileNameGet);
				parentMainDirectory.delete();
				//System.out.println("delete is ------------------------------------------------------------>:"+fileNameGet);
			} catch (Exception e) {
				gLogger.error("Error is;" + e, e);
				resObj.setResultValue(null);
			}finally{
				lOutStream.close();
				channelSftp.disconnect();
				channel.disconnect();
				session.disconnect();
			}
			return resObj;
		}

		
		
/*		//to download File
				public ResultObject downloadOpgmFile(Map<String, Object> inputMap) {
					ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
					HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
					ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);					
					setSessionInfo(inputMap);
					try {
						String FileNo = StringUtility.getParameter("FileNo", request);
						
						String directoryPath = request.getSession().getServletContext().getRealPath("/") + "/" + FileNo;
						
						zipFolder(directoryPath,directoryPath+".zip");
						
						String ZIpPath=directoryPath+".zip";
						
						try {
							if (ZIpPath == null|| ZIpPath.equals("")) {
								throw new ServletException(
										"File Name can't be null or empty");
							}

							File file1 = new File(ZIpPath);
							if (!file1.exists()) {
								throw new ServletException("File doesn't exists on server.");
							}

							response.setContentType("APPLICATION/OCTET-STREAM");
							response.setHeader("Content-Disposition","attachment;filename="+FileNo+".zip");

							FileInputStream fileInputStream = new FileInputStream(file1);
							OutputStream os = response.getOutputStream();
							byte[] bufferData = new byte[4096];
							int read = 0;
							while ((read = fileInputStream.read(bufferData)) > 0) {
								os.write(bufferData, 0, read);
							}
							os.close();
							fileInputStream.close();
							response.flushBuffer();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						resObj.setResultValue(inputMap);
						resObj.setViewName("ExportReportPage");
					} catch (Exception e) {
						gLogger.error("Error is;" + e, e);
						resObj.setResultValue(null);
					}
					return resObj;
				}
				
				static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
				    ZipOutputStream zip = null;
				    FileOutputStream fileWriter = null;
				    fileWriter = new FileOutputStream(destZipFile);
				    zip = new ZipOutputStream(fileWriter);
				    addFolderToZip("", srcFolder, zip);
				    zip.flush();
				    zip.close();
				  }
				
				  static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
					      throws Exception {
					    File folder = new File(srcFolder);

					    for (String fileName : folder.list()) {
					      if (path.equals("")) {
					        addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
					      } else {
					        addFileToZip(path + "/" + folder.getName(), srcFolder + "/" +   fileName, zip);
					      }
					    }
					  }
				  
				  static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
					      throws Exception {
					    File folder = new File(srcFile);
					    if (folder.isDirectory()) {
					      addFolderToZip(path, srcFile, zip);
					    } else {
					      byte[] buf = new byte[1024];
					      int len;
					      FileInputStream in = new FileInputStream(srcFile);
					      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
					      while ((len = in.read(buf)) > 0) {
					        zip.write(buf, 0, len);
					      }
					    }
					  }
*/
}//end class
