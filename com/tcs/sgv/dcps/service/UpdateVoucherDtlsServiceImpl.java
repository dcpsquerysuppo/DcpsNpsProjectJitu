package com.tcs.sgv.eis.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ExcelExportHelper;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.eis.dao.DisplayPendingWorkDao;
import com.tcs.sgv.eis.dao.DisplayPendingWorkDaoImpl;

import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.dao.UpdateVoucherDtlsDao;
import com.tcs.sgv.eis.dao.UpdateVoucherDtlsDaoImpl;

import com.tcs.sgv.eis.valueobject.HrPayPaybill;


public class UpdateVoucherDtlsServiceImpl extends ServiceImpl{
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private Date gDtCurDate = null; /* CURRENT DATE */

	Log logger = LogFactory.getLog(UpdateVoucherDtlsServiceImpl.class);

	Long gLngUserId = null;
	Long gLngPostId = null;
	private Long gLngLangId = null; /* LANG ID */
	private String gStrPostId = null; /* STRING POST ID */
	private Object lstrRemarks;

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gLngLangId = SessionHelper.getLangId(inputMap);

		} catch (Exception e) {
			logger.error("Error in setSessionInfo of changeNameServiceImpl ", e);
			throw e;
		}
	}

	public ResultObject getDataForVoucherUpdate(Map objectArgs)
	{
		logger.info("Inside getDataForVoucherUpdate for voucher update");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");

		try{

			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
			logger.info("lng id id "+langId);

			CmnLookupMstDAO lookupDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());    		
			List yearList = lookupDAO.getAllChildrenByLookUpNameAndLang("Year", langId);
			//Collections.reverse(yearList);
			logger.info(yearList.size());
			List monthList =lookupDAO.getAllChildrenByLookUpNameAndLang("Month", langId);
			logger.info(monthList.size()
			);
			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
			if((StringUtility.getParameter("Flag", request)!=null)&&(StringUtility.getParameter("Flag", request)!="")){
				String flag= StringUtility.getParameter("Flag", request);
				logger.info("flag******"+flag);
				if(Long.parseLong(flag)==1){
					String selBill= StringUtility.getParameter("txtBillID", request);
					logger.info("selBill******"+selBill);
					String selYear= StringUtility.getParameter("year", request);
					logger.info("selYear******"+selYear);
					String selMonth= StringUtility.getParameter("month", request);
					logger.info("selMonth******"+selMonth);

					List voucherDetails=updateVoucherObj.getVoucherData(selBill,selYear,selMonth);
					objectArgs.put("yearSelected", selYear);
					objectArgs.put("monthSelected", selMonth);
					objectArgs.put("selBill", selBill);
					objectArgs.put("voucherDetails", voucherDetails);
				}
				if(Long.parseLong(flag)==2){
					String payBillId= StringUtility.getParameter("payBillId", request);
					logger.info("payBillId******"+payBillId);
					String voucherNo= StringUtility.getParameter("voucherNo", request);
					logger.info("voucherNo******"+voucherNo);
					String voucherDt= StringUtility.getParameter("voucherDt", request);
					logger.info("voucherDt******"+voucherDt);

					String finYear= StringUtility.getParameter("finYear", request);
					logger.info("finYear******"+finYear);

					SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

					try {
						voucherDt = sdf2.format(sdf1.parse(voucherDt));
						logger.info("voucherDt******"+voucherDt);
					} catch (Exception e) {
						e.printStackTrace();
					}
					updateVoucherObj.updateDetails(payBillId,voucherNo,voucherDt,finYear);


				}


			}

			objectArgs.put("yearList", yearList);
			objectArgs.put("monthList", monthList);

			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(objectArgs);//put in result object
			resultObject.setViewName("updateVoucherDetails");//set view name

		}catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in loadEmpDtlsDdoWise "+ e);
		}
		return resultObject;
	}



	//Added BY Roshan for Annual increment 
	public ResultObject AllwIncremennt(Map objectArgs)
	{
		this.logger.info("Inside Get district list");
		ResultObject resultObject = new ResultObject(0);
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest)objectArgs.get("requestObj");
		try
		{
			Map loginDetailsMap = (Map)objectArgs.get("baseLoginMap");

			List disOfcList = null;

			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());

			String allowedDis = null;
			String columnName = null;
			String checkedFlag = StringUtility.getParameter("checkedFlag", request);
			if (checkedFlag.equals("paybillgeneration")) {
				columnName = "Allowed_or_not_paybillgeneration";
			}
			else if (checkedFlag.equals("increment"))
				columnName = "Allowed_or_not";

			this.logger.info("checkedFlag***************" + checkedFlag);
			if((StringUtility.getParameter("flag", request)!=null)&&(StringUtility.getParameter("flag", request)!="") && Long.parseLong(StringUtility.getParameter("flag", request))==1){
				allowedDis = StringUtility.getParameter("allowedDis", request);
				this.logger.info("allowedDis***************" + allowedDis);
				if (allowedDis.length() > 0)
					allowedDis = allowedDis.substring(0, allowedDis.length() - 1);
				this.logger.info("allowedDis***************" + allowedDis);

				updateVoucherObj.allowDistrict(allowedDis, columnName);
			}

			List districtList = updateVoucherObj.getAllDistrict(columnName);
			objectArgs.put("districtList", districtList);
			objectArgs.put("checkedFlag", checkedFlag);
			districtList = null;
			resultObject.setResultCode(0);
			resultObject.setResultValue(objectArgs);
			resultObject.setViewName("AllowDistrcitForIncr");
		}
		catch (Exception e) {
			resultObject = new ResultObject(-1);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			this.logger.error("Error in mapDDOCodeList " + e);
		}
		return resultObject;
	}


	public ResultObject getEmpList(Map inputMap) throws Exception
	{

		logger.info("hiii i m in UidIntegrationServiceImpl to getEmpList");

		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String ddoCode = null;
		List empList=null;



		try
		{
			setSessionInfo(inputMap);
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginMap = (Map) (Map) inputMap.get("baseLoginMap");

			logger.info("hiii i m finding ddo code");
			String loggedInPost= loginMap.get("loggedInPost").toString();
			long langId = Long.parseLong(loginMap.get("langId").toString());
			logger.info("hiii i m finding logged in post Id"+loggedInPost);
			logger.info("hiii i m finding ddo code"+langId);
			long locId = StringUtility.convertToLong(loginMap.get("locationId").toString()).longValue();
			logger.info("hiii i m finding ddo code"+locId);
			DcpsCommonDAOImpl commonDao = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			PayBillDAOImpl payDAO = new PayBillDAOImpl(HrPayPaybill.class,serv.getSessionFactory());
			List<OrgDdoMst> ddoList = payDAO.getDDOCodeByLoggedInlocId(locId);
			OrgDdoMst ddoMst  = null;
			if(ddoList!=null && ddoList.size()>0) {
				ddoMst = ddoList.get(0);
				logger.info("hiii i m finding ddo code");
			}

			if(ddoMst!=null)
				ddoCode = ddoMst.getDdoCode();
			logger.info("hiii i m finding ddo code");
			logger.info("hii The Logged in DDO Code is "+ddoCode);


			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
			empList  = updateVoucherObj.getApprovedFormsForDDO(ddoCode);

			inputMap.put("empList", empList );
			empList =null;
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("UIDValidateList");

		}
		catch (Exception e)
		{
			resultObject.setResultValue(null);
			resultObject.setThrowable(e);
			resultObject.setResultCode(ErrorConstants.ERROR);
			resultObject.setViewName("errorPage");
		}

		return resultObject;
	}

	public ResultObject getJeevanPramanPatra(Map objectArgs)
	{
		logger.info("Inside Get uploadGr");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		long attachment_Id_order=0;
		try{
			setSessionInfo(objectArgs);
			//Addded by roshan For PDF Uploading
			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
			String function=StringUtility.getParameter("create", request);

			logger.info("hiii i m gLngPostId**********$$$$$$$$$$$$$$$*******" + gLngPostId);


			if((StringUtility.getParameter("flag", request)!=null)&&(StringUtility.getParameter("flag", request)!="") && Long.parseLong(StringUtility.getParameter("flag", request))==1){
				String currRowNum="1";
				objectArgs.put("rowNumber",currRowNum);
				objectArgs.put("attachmentName","orderId");
				resultObject = serv.executeService("FILE_UPLOAD_VOGEN",objectArgs);
				Map resultMap=(Map)resultObject.getResultValue();
				resultObject = serv.executeService("FILE_UPLOAD_SRVC", objectArgs);
				resultMap = (Map) resultObject.getResultValue();

				logger.info( "objectArgs data is *******************"+objectArgs.entrySet());

				logger.info( "resultMap data is *******************"+resultMap.entrySet());
				if(resultMap.get("AttachmentId_orderId")!=null)
					attachment_Id_order = (Long) resultMap.get("AttachmentId_orderId"); 
				logger.info("attachment_Id_order is "+attachment_Id_order);

				function="YES";
				updateVoucherObj.createNewGR(attachment_Id_order,gLngPostId);

				objectArgs.put("action", "prcoessFile");
				objectArgs.put("attachId",attachment_Id_order);

			}
			//Ended By Roshan for PDF Uploading

			Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
			List grList = updateVoucherObj.getGRlList(gLngPostId);
			objectArgs.put("grList", grList);
			objectArgs.put("function", function);
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(objectArgs);//put in result object
			resultObject.setViewName("JeevanPramanPatra");//set view name

		}catch(Exception e){
			e.printStackTrace();
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in mapDDOCodeList "+ e);
		}
		return resultObject;
	}


	public ResultObject openJeevanPramanPatra(Map objectArgs) {
		ResultObject resultObject = new ResultObject(0);
		try {
			HttpServletRequest request = (HttpServletRequest) objectArgs
			.get("requestObj");
			ServiceLocator serv = (ServiceLocator) objectArgs
			.get("serviceLocator");
			HttpServletResponse response = (HttpServletResponse) objectArgs
			.get("responseObj");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			//  EmployeeStatisticsViewDAOImpl employeeStatisticsViewDAOImpl = new EmployeeStatisticsViewDAOImpl(EmployeeStatisticsViewVO.class,serv.getSessionFactory());
			String authNo = (!("".equals(StringUtility.getParameter("authNo",
					request)))) ? StringUtility.getParameter("authNo", request)
							.toString() : "";
							this.logger.error("authNo is ::" + authNo);
							UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());


							byte[] lBytes = (byte[]) null;


							Blob blob = updateVoucherObj.getAttachment(authNo);

							System.out.println("Read "+ blob.length() + " bytes ");
							byte [] array = blob.getBytes( 1, ( int ) blob.length() );
							File file = File.createTempFile("rosahn", ".txt", new File("D:/"));
							FileOutputStream out = new FileOutputStream( file );
							out.write( array );
							System.out.println(array);
							out.close();


							resultObject.setResultValue(objectArgs);
							resultObject.setResultCode(0);
							resultObject.setViewName("orderDocumnet");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger
			.error("Exception Occurrs in view pdf data method ..Exception is "
					+ e.getMessage());
		}
		return resultObject;
	}


	public ResultObject processFile(Map objectArgs) {
		ResultObject resultObject = new ResultObject(0);
		try {
			HttpServletRequest request = (HttpServletRequest) objectArgs
			.get("requestObj");
			ServiceLocator serv = (ServiceLocator) objectArgs
			.get("serviceLocator");
			HttpServletResponse response = (HttpServletResponse) objectArgs
			.get("responseObj");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
			String attachId = (!("".equals(StringUtility.getParameter("attachId",
					request)))) ? StringUtility.getParameter("attachId", request)
							.toString() : "";
							String id = updateVoucherObj.getFileId(attachId);

							this.logger.error("attachId is ::" + attachId);
							setSessionInfo(objectArgs);

							byte[] lBytes = (byte[]) null;
							Blob blob = updateVoucherObj.getAttachment(attachId);

							System.out.println("Read "+ blob.length() + " bytes ");
							byte [] array = blob.getBytes( 1, ( int ) blob.length() );
							//  File file = File.createTempFile("id", ".txt", new File("D:/"));
							String filePath=request.getSession().getServletContext().getRealPath("/") +id+ ".dat";
							this.logger.error("filePath is *********** ::" + filePath);
							FileOutputStream out = new FileOutputStream( filePath );
							out.write( array );
							System.out.println(array);
							out.close();
							String[] fileDtls=null;
							File f = new File(new File(filePath).getAbsolutePath());
							if(f.exists() && !f.isDirectory()){
								BufferedReader br1 = new BufferedReader(new FileReader(new File(filePath).getAbsolutePath()));
								String line = br1.readLine();
								while (line != null) {
									logger.info(line.toString());
									fileDtls=line.split("\\|");
									updateVoucherObj.saveFileDtls(fileDtls,id,gLngPostId);
									line = br1.readLine();
								}
							}
 
							updateVoucherObj.updateStatus(id);
							List grList = updateVoucherObj.getGRlList(gLngPostId);
							objectArgs.put("grList", grList);
							resultObject.setResultCode(ErrorConstants.SUCCESS);
							resultObject.setResultValue(objectArgs);//put in result object
							resultObject.setViewName("JeevanPramanPatra");//set view name
		} catch (Exception e) {
			e.printStackTrace();
			this.logger
			.error("Exception Occurrs in view pdf data method ..Exception is "
					+ e.getMessage());
		}
		return resultObject;
	}



	public ResultObject createPramanPatraFile(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletResponse response = (HttpServletResponse) inputMap.get("responseObj");
		PrintWriter outputfile=null;
		try {
			setSessionInfo(inputMap);
			if(StringUtility.getParameter("fileId", request)!=null && !StringUtility.getParameter("fileId", request).equals(""))
			{
				String fileNumber = StringUtility.getParameter("fileId", request);
				outputfile =response.getWriter();
				UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
				List fileDtls=updateVoucherObj.getFileDtls(fileNumber);
				outputfile.write("BANK_CODE|PPO NUMBER|BANK ACCOUNT|LIFE CERTIFICATE|RE_MARRIAGE|RE_EMPLOYED|AUTHENTICATION DATE|PRAMAAN ID|AADHAAR|MOBILE|NAME|GENDER|DATE OF BIRTH");
				outputfile.write("\r\n"); 
				for(int i=0;i<fileDtls.size();i++){
					outputfile.write(fileDtls.get(i).toString());
					outputfile.write("\r\n"); 
				}
				String lStrFileName=fileNumber;

				String fileName = lStrFileName + ".dat";
				response.setContentType("text/plain;charset=UTF-8");
				response.addHeader("Content-disposition", "attachment; filename=" + fileName);
				response.setCharacterEncoding("UTF-8");
				logger.info("File is"+outputfile);
				outputfile.flush();
				resObj.setResultValue(inputMap);
				resObj.setViewName("ExportReportPage");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject updateLCFlag (Map inputMap) throws Exception
	{

		logger.info("hiii i m in getJeevanPramanPatraForVerify to getJeevanPramanPatraForVerify");

		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		List idPPOList=null;



		try
		{
			setSessionInfo(inputMap);
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginMap = (Map) (Map) inputMap.get("baseLoginMap");
			String ppoNumber=null;
			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
			/*if(StringUtility.getParameter("empIds", request)!=null && !StringUtility.getParameter("empIds", request).equals(""))
			{
				ppoNumber = StringUtility.getParameter("empIds", request);
				logger.info("ppoNumber is "+ppoNumber);
				ppoNumber=ppoNumber.substring(0,ppoNumber.length()-3);
				logger.info("ppoNumber final is "+ppoNumber);
				updateVoucherObj.updateLCFlag(ppoNumber,gLngPostId);
				inputMap.put("action", "save" );
			}*/


			///Added on 21-01-2015 for Approve-Reject
			String remark=null;
			String action=null;
			String[] lstrRemarks =null;
			String[] lstrRmrksArr=null;
			String fileId=null;
			if((StringUtility.getParameter("empIds", request)!=null)&&(StringUtility.getParameter("empIds", request)!="")){
				ppoNumber = StringUtility.getParameter("empIds", request);
				action= StringUtility.getParameter("function", request);
				fileId=StringUtility.getParameter("fileId", request);
				if(action!=null && action.equals("REJECT")){
					remark= StringUtility.getParameter("remark", request);	
				}

				logger.info("******************hiii i m********ppoNumber*****************" + ppoNumber);
				
				logger.info("******************hiii i m********action*****************" + action);
				
				logger.info("******************hiii i m********fileId*****************" + fileId);
				
				logger.info("******************hiii i m********remark*****************" + remark);
				
				String[] lstrPPOIds = ppoNumber.split("~");
				if(action!=null && action.equals("REJECT")){
					lstrRemarks = remark.split("~");
				}


				String[] lstrPPO = new String[lstrPPOIds.length];
				if(action!=null && action.equals("REJECT")){
					lstrRmrksArr = new String[lstrRemarks.length];
				}


				for (Integer lInt = 0; lInt < lstrPPOIds.length; lInt++)
				{
					if (lstrPPOIds[lInt] != null && !"".equals(lstrPPOIds[lInt]))
					{
						lstrPPO[lInt] = lstrPPOIds[lInt];
						if(action!=null && action.equals("REJECT")){
							lstrRmrksArr[lInt] = lstrRemarks[lInt];
							logger.info("hii********** "+lstrPPO[lInt]);
							logger.info("hii********** "+lstrRmrksArr[lInt]);
						}

					

						if(action!=null && action.equals("REJECT")){
							updateVoucherObj.rejectDetails(lstrPPO[lInt],lstrRmrksArr[lInt], gLngPostId);
						}

						if(action!=null && action.equals("APPROVE")){
							logger.info("hii*****lstrPPO***** "+lstrPPO[lInt]);
							logger.info("hii*****fileId***** "+fileId );
							updateVoucherObj.updateLCFlag(lstrPPO[lInt], gLngPostId,fileId);
						}


					}
				}


			}
			inputMap.put("action", action );

			idPPOList  = updateVoucherObj.getAllValidCaseDtls(gLngPostId);

			inputMap.put("idPPOList", idPPOList );
			idPPOList =null;
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("JeevanPramanAuditingDtls");


		}
		catch (Exception e)
		{
			resultObject.setResultValue(null);
			resultObject.setThrowable(e);
			resultObject.setResultCode(ErrorConstants.ERROR);
			resultObject.setViewName("errorPage");
		}

		return resultObject;
	}

	public ResultObject getJeevanPramanPatraForVerify (Map inputMap) throws Exception
	{

		logger.info("hiii i m in getJeevanPramanPatraForVerify to getJeevanPramanPatraForVerify");

		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		List idPPOList=null;



		try
		{
			setSessionInfo(inputMap);
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginMap = (Map) (Map) inputMap.get("baseLoginMap");

			logger.info("******************hiii i m********gLngPostId*****************" + gLngPostId);
			String loggedInPost= loginMap.get("loggedInPost").toString();
			long langId = Long.parseLong(loginMap.get("langId").toString());
			logger.info("hiii i m finding logged in post Id"+loggedInPost);
			logger.info("hiii i m finding ddo code"+langId);
			long locId = StringUtility.convertToLong(loginMap.get("locationId").toString()).longValue();
			logger.info("hiii i m finding ddo code"+locId);
			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
			idPPOList  = updateVoucherObj.getAllValidCaseDtls(gLngPostId);

			inputMap.put("idPPOList", idPPOList );
			idPPOList =null;
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("JeevanPramanAuditingDtls");

		}
		catch (Exception e)
		{
			resultObject.setResultValue(null);
			resultObject.setThrowable(e);
			resultObject.setResultCode(ErrorConstants.ERROR);
			resultObject.setViewName("errorPage");
		}

		return resultObject;
	}



	public ResultObject getJeevanPramanPatraForAuditor (Map inputMap) throws Exception
	{

		logger.info("hiii i m in getJeevanPramanPatraForVerify to getJeevanPramanPatraForVerify");

		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		List empPPOList=null;

		String fileId=null;

		try
		{
			setSessionInfo(inputMap);
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginMap = (Map) (Map) inputMap.get("baseLoginMap");
			fileId = StringUtility.getParameter("id", request);
			logger.info("hiii i m fileId*****************" + fileId);
			String loggedInPost= loginMap.get("loggedInPost").toString();
			long langId = Long.parseLong(loginMap.get("langId").toString());
			logger.info("hiii i m finding logged in post Id"+loggedInPost);
			logger.info("hiii i m finding ddo code"+langId);
			long locId = StringUtility.convertToLong(loginMap.get("locationId").toString()).longValue();
			logger.info("hiii i m finding ddo code"+locId);
			UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
			empPPOList  = updateVoucherObj.getAllValidCase(gLngPostId,fileId);
			inputMap.put("fileId", fileId );
			inputMap.put("empList", empPPOList );
			empPPOList =null;
			resultObject.setResultValue(inputMap);
			resultObject.setViewName("JeevanPramanAuditing");

		}
		catch (Exception e)
		{
			resultObject.setResultValue(null);
			resultObject.setThrowable(e);
			resultObject.setResultCode(ErrorConstants.ERROR);
			resultObject.setViewName("errorPage");
		}

		return resultObject;
	}


	public ResultObject createResponseFile(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletResponse response = (HttpServletResponse) inputMap.get("responseObj");
		PrintWriter outputfile=null;
		try {
			setSessionInfo(inputMap);
			if(StringUtility.getParameter("fileId", request)!=null && !StringUtility.getParameter("fileId", request).equals(""))
			{
				String fileNumber = StringUtility.getParameter("fileId", request);
				outputfile =response.getWriter();
				UpdateVoucherDtlsDao updateVoucherObj = new UpdateVoucherDtlsDaoImpl(UpdateVoucherDtlsDaoImpl.class, serv.getSessionFactory());
				List fileDtls=updateVoucherObj.getResponseFileDtls(fileNumber);
				outputfile.write("PRAMAAN ID|AADHAAR|RESULT_STATUS|REMARKS");
				outputfile.write("\r\n"); 
				for(int i=0;i<fileDtls.size();i++){
					outputfile.write(fileDtls.get(i).toString());
					outputfile.write("\r\n"); 
				}
				String lStrFileName=fileNumber;

				String fileName = lStrFileName+ "_Response" + ".dat";
				response.setContentType("text/plain;charset=UTF-8");
				response.addHeader("Content-disposition", "attachment; filename=" + fileName);
				response.setCharacterEncoding("UTF-8");
				logger.info("File is"+outputfile);
				outputfile.flush();
				resObj.setResultValue(inputMap);
				resObj.setViewName("ExportReportPage");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}




}
