package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.TerminationDtlsDAO;
import com.tcs.sgv.dcps.dao.TerminationDtlsDAOImpl;
import com.tcs.sgv.dcps.dao.TerminationDtlsRqstsDAOImpl;
import com.tcs.sgv.dcps.dao.TerminationRqstsDAO;
import com.tcs.sgv.dcps.valueobject.TrnTerminalDtls;
import com.tcs.sgv.dcps.valueobject.TrnTerminalDtlsHst;

public class TerminationServiceImpl extends ServiceImpl  {
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
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

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
			e.printStackTrace();
		}
	}

	public ResultObject loadPrintFormAandB(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empDtls=null;
		List empPayDtls=null;
		List<CmnLookupMst> reasonList=null;
		List dsgnList= null;
		Long ternId=null;
		List empSavedDtls=null;
		String status="60001";
		String reason="1";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationDtlsDAO = null;
			if(StringUtility.getParameter("txtSevaarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("txtSevaarthId",request)))
			{
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(null, serv
						.getSessionFactory());
				lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
				empDtls=lObjTerminationDtlsDAO.getEmpDetails(lStrSevarthId,null);
				empPayDtls=lObjTerminationDtlsDAO.getEmpPayDetails(lStrSevarthId);
				empSavedDtls=lObjTerminationDtlsDAO.getEmpSavedDetails(lStrSevarthId);
				if(empDtls!=null && empDtls.size()>0)
				{
					Object[] obj=(Object[]) empDtls.get(0);
					if(obj!=null && obj[11]!=null)
					{
						ternId=Long.parseLong(obj[11].toString());
					}
					if(obj!=null && obj[12]!=null)
					{
						reason=obj[12].toString();
					}
					if(obj!=null)
					{
						Object[] objNew = new Object[14];
						for(int i=0;i<12;i++)
						{
							objNew[i]=obj[i];
						}
						
						if(empPayDtls!=null && empPayDtls.size()>0)
						{
							Object[] objPay=(Object[]) empPayDtls.get(0);
							
							if(objPay!=null)
							{
								if(objPay[1]!=null && objPay[2]!=null)
								{
									objNew[12]=objPay[1].toString()+" - "+objPay[2].toString();
								}
								else
								{
									objNew[12]="-";
								}
								if(objPay[3]!=null && objPay[4]!=null)
								{
									objNew[13]=objPay[3].toString()+" - "+objPay[4].toString();
								}
								else
								{
									objNew[13]="-";
								}
								
							}
							else
							{
								objNew[12]="-";
								objNew[13]="-";
							}
						}
						else
						{
							objNew[12]="-";
							objNew[13]="-";
						}
						gLogger.info("objNew"+objNew.toString());
						if(empSavedDtls!=null && empSavedDtls.size()>0)
						{
							Object[] objSaved=(Object[]) empSavedDtls.get(0);
							if(objSaved!=null)
							{
								if(objSaved[0]!=null)
								{
									objNew[6]=objSaved[0];
								}
								if(objSaved[1]!=null)
								{
									objNew[7]=objSaved[1];
								}
								if(objSaved[2]!=null)
								{
									objNew[8]=objSaved[2];
									objNew[10]=objSaved[2];
								}
								if(objSaved[3]!=null)
								{
									objNew[5]=objSaved[3];
								}
								if(objSaved[7]!=null)
								{
									status=objSaved[7].toString();
								}
								if(objSaved[8]!=null)
								{
									reason=objSaved[8].toString();
								}
								
							}
						}
						gLogger.info("objNew"+objNew.toString());
						empDtls.set(0, objNew);
					}
				}
				
				dsgnList=lObjTerminationDtlsDAO.getDesignationList(langId, locId);
				reasonList = IFMSCommonServiceImpl.getLookupValues("EmpEndSpc", SessionHelper.getLangId(inputMap), inputMap);
			}
			inputMap.put("empDtls", empDtls);
			if(empDtls!=null)
			{
				inputMap.put("sizeOfList", empDtls.size());
			}
			else
			{
				inputMap.put("sizeOfList", 0);
			}
			inputMap.put("status", status);
			inputMap.put("ternId", ternId);
			inputMap.put("reasonList", reasonList);
			inputMap.put("dsgnList", dsgnList);
			inputMap.put("lStrSevarthId", lStrSevarthId);
			inputMap.put("reason", reason);
			resObj.setViewName("printFormAandB");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	
	public ResultObject generateTermFormA(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empDtls=null;
		
	
		
		Long ternId=null;
		List empSavedDtls=null;
		String lStrDDOCode="";
		String transactionId="";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			Long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationDtlsDAO = null;
			TerminationDtlsDAO lObjTerminationDtlsDAOHst=null;
			if(StringUtility.getParameter("txtSevaarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("txtSevaarthId",request)))
			{
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(TrnTerminalDtls.class, serv
						.getSessionFactory());
				
				lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
				empSavedDtls=lObjTerminationDtlsDAO.getEmpSavedDetails(lStrSevarthId);
				//transactionId=lObjTerminationDtlsDAO.getEmployeesPPAN(lStrSevarthId);
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
						.getSessionFactory());
			
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
				transactionId=lStrDDOCode.substring(0, 4);
				transactionId=lObjTerminationDtlsDAO.getTranId(transactionId);
				if(empSavedDtls!=null && empSavedDtls.size()>0)
				{
					Object[] obj=(Object[]) empSavedDtls.get(0);
					Long pk=Long.parseLong(obj[6].toString());
					TrnTerminalDtls objTermDtls=(TrnTerminalDtls) lObjTerminationDtlsDAO.read(pk);
					
					  TrnTerminalDtlsHst trnHst=null;
			            if(objTermDtls!=null)
			            {
			            	lObjTerminationDtlsDAOHst= new TerminationDtlsDAOImpl(TrnTerminalDtlsHst.class, serv
									.getSessionFactory());
			            	trnHst= new TrnTerminalDtlsHst();
			            	trnHst.setCreatedDate(gDtCurDate);
			            	trnHst.setCreatedPostId(objTermDtls.getCreatedPostId());
			            	trnHst.setCurAddress(objTermDtls.getCurAddress());
			            	trnHst.setDateOfTermination(objTermDtls.getDateOfTermination());
			            	trnHst.setDdoCode(objTermDtls.getDdoCode());
			            	trnHst.setDsgn(objTermDtls.getDsgn());
			            	trnHst.setFormAGenDate(objTermDtls.getFormAGenDate());
			            	trnHst.setLocCode(objTermDtls.getLocCode());
			            	trnHst.setReasonOfTermination(objTermDtls.getReasonOfTermination());
			            	trnHst.setReasonOfTOReject(objTermDtls.getReasonOfTOReject());
			            	trnHst.setSevarthId(objTermDtls.getSevarthId());
			            	trnHst.setStatusFlag(objTermDtls.getStatusFlag());
			            	trnHst.setTerminationId(objTermDtls.getTerminationId());
			            	trnHst.setTransactionId(objTermDtls.getTransactionId());
			            	int orderNo=lObjTerminationDtlsDAO.getLatestOrderNo(pk);
			            	trnHst.setOrderNo(orderNo);
			            	lObjTerminationDtlsDAOHst.create(trnHst);
			            	
			            }
					if(objTermDtls!=null)
					{
						if(objTermDtls.getTransactionId()!=null)
						{
							
						}
						else
						{
							objTermDtls.setTransactionId(transactionId);
						}
						transactionId=objTermDtls.getTransactionId();
						objTermDtls.setStatusFlag(60002l);
						objTermDtls.setFormAGenDate(gDtCurDate);
						objTermDtls.setUpdatedDate(gDtCurDate);
						lObjTerminationDtlsDAO.update(objTermDtls);
					}
				}
				else
				{
					
					empDtls=lObjTerminationDtlsDAO.getEmpDetailsForFormA(lStrSevarthId);
					if(empDtls!=null)
					{
						Object[] objDtls=(Object[]) empDtls.get(0);
						TrnTerminalDtls trn=new TrnTerminalDtls();
						trn.setSevarthId(lStrSevarthId);
						if(objDtls[1]!=null)
						{
						
							SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
							Date d=fmt.parse(objDtls[1].toString());
							trn.setDateOfTermination(d);
							
						}
						if(objDtls[2]!=null)
							
						{
							trn.setReasonOfTermination(Long .parseLong(objDtls[2].toString()));
						}
			            if(objDtls[4]!=null)
							
						{
			            	trn.setCurAddress(objDtls[5].toString());
						}
			            else
			            {
			            	trn.setCurAddress(objDtls[3].toString());
			            }
			            if(objDtls[6]!=null)
							
						{
			            	trn.setDsgn(Long.parseLong(objDtls[6].toString()));
						}
			          
			           trn.setCreatedDate(gDtCurDate);
			           trn.setFormAGenDate(gDtCurDate);
			           trn.setCreatedPostId(gLngPostId);
			           trn.setDdoCode(lStrDDOCode);
			           trn.setLocCode(gStrLocationCode);
			           Long terminationId = IFMSCommonServiceImpl.getNextSeqNum("TERN_TERMINATION_DTLS", inputMap);
			           trn.setTerminationId(terminationId);
			           trn.setTransactionId(transactionId);
			           trn.setStatusFlag(60002l);
			           
			           lObjTerminationDtlsDAO.create(trn);
					}
					
					
				}
				
					
			
			}
			String lSBStatus = getResponseXMLDoc("Yes",transactionId,lStrSevarthId).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	
	public ResultObject generateTermFormB(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empSavedDtls=null;
		
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationDtlsDAO = null;
			TerminationDtlsDAO lObjTerminationDtlsDAOHst = null;
			if(StringUtility.getParameter("txtSevaarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("txtSevaarthId",request)))
			{
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(TrnTerminalDtls.class, serv
						.getSessionFactory());
				lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
				empSavedDtls=lObjTerminationDtlsDAO.getEmpSavedDetails(lStrSevarthId);
			
				if(empSavedDtls!=null && empSavedDtls.size()>0)
				{
					Object[] obj=(Object[]) empSavedDtls.get(0);
					Long pk=Long.parseLong(obj[6].toString());
					TrnTerminalDtls objTermDtls=(TrnTerminalDtls) lObjTerminationDtlsDAO.read(pk);
					if(objTermDtls!=null)
					{
						 		TrnTerminalDtlsHst trnHst=null;
				            	lObjTerminationDtlsDAOHst= new TerminationDtlsDAOImpl(TrnTerminalDtlsHst.class, serv
										.getSessionFactory());
				            	trnHst= new TrnTerminalDtlsHst();
				            	trnHst.setCreatedDate(gDtCurDate);
				            	trnHst.setCreatedPostId(objTermDtls.getCreatedPostId());
				            	trnHst.setCurAddress(objTermDtls.getCurAddress());
				            	trnHst.setDateOfTermination(objTermDtls.getDateOfTermination());
				            	trnHst.setDdoCode(objTermDtls.getDdoCode());
				            	trnHst.setDsgn(objTermDtls.getDsgn());
				            	trnHst.setFormAGenDate(objTermDtls.getFormAGenDate());
				            	trnHst.setLocCode(objTermDtls.getLocCode());
				            	trnHst.setReasonOfTermination(objTermDtls.getReasonOfTermination());
				            	trnHst.setReasonOfTOReject(objTermDtls.getReasonOfTOReject());
				            	trnHst.setSevarthId(objTermDtls.getSevarthId());
				            	trnHst.setStatusFlag(objTermDtls.getStatusFlag());
				            	trnHst.setTerminationId(objTermDtls.getTerminationId());
				            	trnHst.setTransactionId(objTermDtls.getTransactionId());
				            	int orderNo=lObjTerminationDtlsDAO.getLatestOrderNo(pk);
				            	trnHst.setOrderNo(orderNo);
				            	lObjTerminationDtlsDAOHst.create(trnHst);
				            	
				            
								objTermDtls.setStatusFlag(60003l);
								objTermDtls.setUpdatedDate(gDtCurDate);
								lObjTerminationDtlsDAO.update(objTermDtls);
					}
				}
				
				
					
			
			}
			String lSBStatus = getResponseXMLDoc("Yes",lStrSevarthId).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	public ResultObject generateTermFormC(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empSavedDtls=null;
		
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationDtlsDAO = null;
			TerminationDtlsDAO lObjTerminationDtlsDAOHst=null;
			if(StringUtility.getParameter("txtSevaarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("txtSevaarthId",request)))
			{
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(TrnTerminalDtls.class, serv
						.getSessionFactory());
				lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
				empSavedDtls=lObjTerminationDtlsDAO.getEmpSavedDetails(lStrSevarthId);
			
				if(empSavedDtls!=null && empSavedDtls.size()>0)
				{
					Object[] obj=(Object[]) empSavedDtls.get(0);
					Long pk=Long.parseLong(obj[6].toString());
					TrnTerminalDtls objTermDtls=(TrnTerminalDtls) lObjTerminationDtlsDAO.read(pk);
					if(objTermDtls!=null)
					{
						TrnTerminalDtlsHst trnHst=null;
		            	lObjTerminationDtlsDAOHst= new TerminationDtlsDAOImpl(TrnTerminalDtlsHst.class, serv
								.getSessionFactory());
		            	trnHst= new TrnTerminalDtlsHst();
		            	trnHst.setCreatedDate(gDtCurDate);
		            	trnHst.setCreatedPostId(objTermDtls.getCreatedPostId());
		            	trnHst.setCurAddress(objTermDtls.getCurAddress());
		            	trnHst.setDateOfTermination(objTermDtls.getDateOfTermination());
		            	trnHst.setDdoCode(objTermDtls.getDdoCode());
		            	trnHst.setDsgn(objTermDtls.getDsgn());
		            	trnHst.setFormAGenDate(objTermDtls.getFormAGenDate());
		            	trnHst.setLocCode(objTermDtls.getLocCode());
		            	trnHst.setReasonOfTermination(objTermDtls.getReasonOfTermination());
		            	trnHst.setReasonOfTOReject(objTermDtls.getReasonOfTOReject());
		            	trnHst.setSevarthId(objTermDtls.getSevarthId());
		            	trnHst.setStatusFlag(objTermDtls.getStatusFlag());
		            	trnHst.setTerminationId(objTermDtls.getTerminationId());
		            	trnHst.setTransactionId(objTermDtls.getTransactionId());
		            	int orderNo=lObjTerminationDtlsDAO.getLatestOrderNo(pk);
		            	trnHst.setOrderNo(orderNo);
		            	lObjTerminationDtlsDAOHst.create(trnHst);
		            	
		            	
						objTermDtls.setStatusFlag(60006l);
						objTermDtls.setUpdatedDate(gDtCurDate);
						lObjTerminationDtlsDAO.update(objTermDtls);
					}
				}
				
				
					
			
			}
			String lSBStatus = getResponseXMLDoc("Yes",lStrSevarthId).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	
	
	public ResultObject loadPrintFormC(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		List empDtls=null;
		String lStrDDOCode="";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
		    setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationDtlsDAO = new TerminationDtlsDAOImpl(null,serv
					.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			empDtls=lObjTerminationDtlsDAO.getEmployeesForFormC(lStrDDOCode);
			
			inputMap.put("empDtls", empDtls);
			if(empDtls!=null)
			{
				inputMap.put("sizeOfList", empDtls.size());
			}
			else
			{
				inputMap.put("sizeOfList", 0);
			}
		
		
			resObj.setViewName("printFormC");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	
	public ResultObject checkSearchDtls(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId="";
		String lStrDDOCode="";
		String flag="No";
		String Termflag="No";
		String isSaved="No";
		String user="None";
		String status="Yes";
		try {

			setSessionInfo(inputMap);
			lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAO = new TerminationDtlsDAOImpl(null, serv
					.getSessionFactory());
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			List resultList=lObjTerminationDtlsDAO.checkIfEmployeeBelongsToDDO(lStrSevarthId, lStrDDOCode);
			if(resultList!=null && resultList.size()>0)
			{
				flag="Yes";
				Object[] obj=(Object[]) resultList.get(0);
				if(obj!=null)
				{
					if(obj[0]!=null && obj[0].toString().equalsIgnoreCase("2"))
					{
						flag="GPF";
					}
					else if (obj[1]!=null && !obj[1].toString().equalsIgnoreCase("1"))
					{
						flag="FORM";
					}
					else
					{
						flag="Yes";
					}
				}
				
				
				
			}
			
			List termList=lObjTerminationDtlsDAO.checkIfTerminationDone(lStrSevarthId);
			if(termList!=null && termList.size()>0)
			{
				Termflag="Yes";
				Object[] obj=(Object[]) termList.get(0);
				if(obj!=null && obj[1]!=null)
				{
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
					Date d=fmt.parse("2015-03-31");
					Date d1=fmt.parse(obj[1].toString());
					if(d.after(d1) || d.equals(d1))
					{
						Termflag="Yes";
					}
					else
					{
						Termflag="date";
					}
				}
			}

			List savedDtls=lObjTerminationDtlsDAO.getAlreadySavedData(lStrSevarthId);
			if(savedDtls!=null && savedDtls.size()>0)
			{
				isSaved="Yes";
				Object[] objSaved=(Object[]) savedDtls.get(0);
				if(objSaved!=null && objSaved[0]!=null)
				{
					if("60005".equalsIgnoreCase(objSaved[0].toString()))
					{
						user="TO";
					}
					if("60007".equalsIgnoreCase(objSaved[0].toString()))
					{
						user="SRKA";
					}
					if("60009".equalsIgnoreCase(objSaved[0].toString()))
					{
						isSaved="No";
					}
					status=objSaved[0].toString();
				}
				
			}
			String lSBStatus = getResponseXMLDoc(flag,Termflag,isSaved,user,status).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	public ResultObject saveTerminationChangeDtls(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId="";
		String lStrDDOCode="";
		String flag="No";
		String Termflag="No";
		String terminationDate="";
		Long reasonForTermination=null;
		String address="";
		Long dsgn=null;
		try {

			setSessionInfo(inputMap);
			lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
			

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAO = new TerminationDtlsDAOImpl(TrnTerminalDtls.class, serv
					.getSessionFactory());
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			
			
			
			TrnTerminalDtls trn=new TrnTerminalDtls();
			trn.setSevarthId(lStrSevarthId);
			if(StringUtility.getParameter("terminationDate",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("terminationDate",request)))
			{
				terminationDate=StringUtility.getParameter("terminationDate",request);
				SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				Date d=fmt.parse(terminationDate);
				trn.setDateOfTermination(d);
				
			}
			if(StringUtility.getParameter("terminationReason",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("terminationReason",request)))
				
			{
				trn.setReasonOfTermination(Long .parseLong(StringUtility.getParameter("terminationReason",request)));
			}
            if(StringUtility.getParameter("dsgn",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("dsgn",request)))
				
			{
				trn.setDsgn(Long .parseLong(StringUtility.getParameter("dsgn",request)));
			}
           if(StringUtility.getParameter("address",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("address",request)))
				
			{
        	   	trn.setCurAddress(StringUtility.getParameter("address",request));
			}
           trn.setCreatedDate(gDtCurDate);
           trn.setCreatedPostId(gLngPostId);
           trn.setDdoCode(lStrDDOCode);
           trn.setLocCode(gStrLocationCode);
           Long terminationId = IFMSCommonServiceImpl.getNextSeqNum("TERN_TERMINATION_DTLS", inputMap);
           trn.setTerminationId(terminationId);
           trn.setStatusFlag(60001l);
           
           lObjTerminationDtlsDAO.create(trn);
           flag="Yes";
			String lSBStatus = getResponseXMLDoc(flag,lStrSevarthId).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	public ResultObject viewDraftRqsts(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		List empDtls=null;
		String lStrDDOCode="";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationRqstsDAO = new TerminationDtlsDAOImpl(null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			empDtls=lObjTerminationRqstsDAO.getEmployeesInRejected(lStrDDOCode);
			inputMap.put("empDtls", empDtls);
			if(empDtls!=null)
			{
				inputMap.put("sizeOfList", empDtls.size());
			}
			else
			{
				inputMap.put("sizeOfList", 0);
			}
			
			resObj.setViewName("viewDraftTerminationRequest");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	public ResultObject updateDraftRqst(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empDtls=null;
		List empPayDtls=null;
		List<CmnLookupMst> reasonList=null;
		List dsgnList= null;
		Long ternId=null;
		List empSavedDtls=null;
		String status="60001";
		String pk="";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationDtlsDAO = null;
			if(StringUtility.getParameter("lStrSevarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("lStrSevarthId",request)))
			{
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(null, serv
						.getSessionFactory());
				lStrSevarthId = StringUtility.getParameter("lStrSevarthId",request).trim();
				empDtls=lObjTerminationDtlsDAO.getEmpDetails(lStrSevarthId,null);
				empPayDtls=lObjTerminationDtlsDAO.getEmpPayDetails(lStrSevarthId);
				empSavedDtls=lObjTerminationDtlsDAO.getEmpSavedDetails(lStrSevarthId);
				if(StringUtility.getParameter("pk",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("pk",request)))
				{
					pk=StringUtility.getParameter("pk",request);
				}
				if(empDtls!=null && empDtls.size()>0)
				{
					Object[] obj=(Object[]) empDtls.get(0);
					if(obj!=null && obj[11]!=null)
					{
						ternId=Long.parseLong(obj[11].toString());
					}
					if(obj!=null)
					{
						Object[] objNew = new Object[14];
						for(int i=0;i<12;i++)
						{
							objNew[i]=obj[i];
						}
						
						if(empPayDtls!=null && empPayDtls.size()>0)
						{
							Object[] objPay=(Object[]) empPayDtls.get(0);
							
							if(objPay!=null)
							{
								if(objPay[1]!=null && objPay[2]!=null)
								{
									objNew[12]=objPay[1].toString()+" - "+objPay[2].toString();
								}
								else
								{
									objNew[12]="-";
								}
								if(objPay[3]!=null && objPay[4]!=null)
								{
									objNew[13]=objPay[3].toString()+" - "+objPay[4].toString();
								}
								else
								{
									objNew[13]="-";
								}
								
							}
							else
							{
								objNew[12]="-";
								objNew[13]="-";
							}
						}
						else
						{
							objNew[12]="-";
							objNew[13]="-";
						}
						gLogger.info("objNew"+objNew.toString());
						if(empSavedDtls!=null && empSavedDtls.size()>0)
						{
							Object[] objSaved=(Object[]) empSavedDtls.get(0);
							if(objSaved!=null)
							{
								if(objSaved[0]!=null)
								{
									objNew[6]=objSaved[0];
								}
								if(objSaved[1]!=null)
								{
									objNew[7]=objSaved[1];
								}
								if(objSaved[2]!=null)
								{
									objNew[8]=objSaved[2];
									objNew[10]=objSaved[2];
								}
								if(objSaved[3]!=null)
								{
									objNew[5]=objSaved[3];
								}
								if(objSaved[7]!=null)
								{
									status=objSaved[7].toString();
								}
								
							}
						}
						gLogger.info("objNew"+objNew.toString());
						empDtls.set(0, objNew);
					}
				}
				
				dsgnList=lObjTerminationDtlsDAO.getDesignationList(langId, locId);
				reasonList = IFMSCommonServiceImpl.getLookupValues("EmpEndSpc", SessionHelper.getLangId(inputMap), inputMap);
			}
			inputMap.put("empDtls", empDtls);
			if(empDtls!=null)
			{
				inputMap.put("sizeOfList", empDtls.size());
			}
			else
			{
				inputMap.put("sizeOfList", 0);
			}
			inputMap.put("status", status);
			inputMap.put("ternId", ternId);
			inputMap.put("reasonList", reasonList);
			inputMap.put("dsgnList", dsgnList);
			inputMap.put("lStrSevarthId", lStrSevarthId);
			inputMap.put("pk", pk);
			resObj.setViewName("updateDraftRqsts");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	public ResultObject saveTerminationChangeDtlsForDraft(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId="";
		String lStrDDOCode="";
		String flag="No";
		String Termflag="No";
		String terminationDate="";
		Long reasonForTermination=null;
		String address="";
		Long dsgn=null;
		Long pk=0l;
		String tranId="";
		try {

			setSessionInfo(inputMap);
			lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
			

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAO = new TerminationDtlsDAOImpl(TrnTerminalDtls.class, serv
					.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAOHst = new TerminationDtlsDAOImpl(TrnTerminalDtlsHst.class, serv
					.getSessionFactory());
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
            if(StringUtility.getParameter("pk",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("pk",request)))
				
			{
				pk=Long .parseLong(StringUtility.getParameter("pk",request));
			} 
			
            TrnTerminalDtls trnRead=(TrnTerminalDtls) lObjTerminationDtlsDAO.read(pk);
            TrnTerminalDtlsHst trnHst=null;
            if(trnRead!=null)
            {
            	trnHst= new TrnTerminalDtlsHst();
            	trnHst.setCreatedDate(gDtCurDate);
            	trnHst.setCreatedPostId(trnRead.getCreatedPostId());
            	trnHst.setCurAddress(trnRead.getCurAddress());
            	trnHst.setDateOfTermination(trnRead.getDateOfTermination());
            	trnHst.setDdoCode(trnRead.getDdoCode());
            	trnHst.setDsgn(trnRead.getDsgn());
            	trnHst.setFormAGenDate(trnRead.getFormAGenDate());
            	trnHst.setLocCode(trnRead.getLocCode());
            	trnHst.setReasonOfTermination(trnRead.getReasonOfTermination());
            	trnHst.setReasonOfTOReject(trnRead.getReasonOfTOReject());
            	trnHst.setSevarthId(trnRead.getSevarthId());
            	trnHst.setStatusFlag(trnRead.getStatusFlag());
            	trnHst.setTerminationId(trnRead.getTerminationId());
            	trnHst.setTransactionId(trnRead.getTransactionId());
            	tranId=trnRead.getTransactionId();
            	int orderNo=lObjTerminationDtlsDAO.getLatestOrderNo(pk);
            	trnHst.setOrderNo(orderNo);
            	lObjTerminationDtlsDAOHst.create(trnHst);
            	if(StringUtility.getParameter("terminationDate",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("terminationDate",request)))
    			{
    				terminationDate=StringUtility.getParameter("terminationDate",request);
    				SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
    				Date d=fmt.parse(terminationDate);
    				trnRead.setDateOfTermination(d);
    				
    			}
    			if(StringUtility.getParameter("terminationReason",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("terminationReason",request)))
    				
    			{
    				trnRead.setReasonOfTermination(Long .parseLong(StringUtility.getParameter("terminationReason",request)));
    			}
                if(StringUtility.getParameter("dsgn",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("dsgn",request)))
    				
    			{
                	trnRead.setDsgn(Long .parseLong(StringUtility.getParameter("dsgn",request)));
    			}
               if(StringUtility.getParameter("address",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("address",request)))
    				
    			{
            	   trnRead.setCurAddress(StringUtility.getParameter("address",request));
    			}
              
               
               trnRead.setStatusFlag(60001l);
               trnRead.setUpdatedDate(gDtCurDate);
               lObjTerminationDtlsDAO.update(trnRead);
               flag="Yes";
            }
            
			//TrnTerminalDtls trn=new TrnTerminalDtls();
          
			
			String lSBStatus = getResponseXMLDoc(flag,lStrSevarthId,pk,tranId).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	public ResultObject deleteDraftRqst(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId="";
		String lStrDDOCode="";
		String flag="No";
		String Termflag="No";
		String terminationDate="";
		Long reasonForTermination=null;
		String address="";
		Long dsgn=null;
		Long pk=0l;
		String tranId="";
		try {

			setSessionInfo(inputMap);
			lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
			

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAO = new TerminationDtlsDAOImpl(TrnTerminalDtls.class, serv
					.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAOHst = new TerminationDtlsDAOImpl(TrnTerminalDtlsHst.class, serv
					.getSessionFactory());
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
            if(StringUtility.getParameter("pk",request) !=null && !"".equalsIgnoreCase(StringUtility.getParameter("pk",request)))
				
			{
				pk=Long .parseLong(StringUtility.getParameter("pk",request));
			} 
			
            TrnTerminalDtls trnRead=(TrnTerminalDtls) lObjTerminationDtlsDAO.read(pk);
            TrnTerminalDtlsHst trnHst=null;
            if(trnRead!=null)
            {
            	trnHst= new TrnTerminalDtlsHst();
            	trnHst.setCreatedDate(gDtCurDate);
            	trnHst.setCreatedPostId(trnRead.getCreatedPostId());
            	trnHst.setCurAddress(trnRead.getCurAddress());
            	trnHst.setDateOfTermination(trnRead.getDateOfTermination());
            	trnHst.setDdoCode(trnRead.getDdoCode());
            	trnHst.setDsgn(trnRead.getDsgn());
            	trnHst.setFormAGenDate(trnRead.getFormAGenDate());
            	trnHst.setLocCode(trnRead.getLocCode());
            	trnHst.setReasonOfTermination(trnRead.getReasonOfTermination());
            	trnHst.setReasonOfTOReject(trnRead.getReasonOfTOReject());
            	trnHst.setSevarthId(trnRead.getSevarthId());
            	trnHst.setStatusFlag(trnRead.getStatusFlag());
            	trnHst.setTerminationId(trnRead.getTerminationId());
            	trnHst.setTransactionId(trnRead.getTransactionId());
            	tranId=trnRead.getTransactionId();
            	int orderNo=lObjTerminationDtlsDAO.getLatestOrderNo(pk);
            	trnHst.setOrderNo(orderNo);
            	lObjTerminationDtlsDAOHst.create(trnHst);
            	trnRead.setStatusFlag(60009l);
            	trnRead.setUpdatedDate(gDtCurDate);
                lObjTerminationDtlsDAO.update(trnRead);
                flag="Yes";
            }
            
			//TrnTerminalDtls trn=new TrnTerminalDtls();
          
			
           
           
			String lSBStatus = getResponseXMLDoc(flag,lStrSevarthId,pk,tranId).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	public ResultObject loadTerminationReport(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empDtls=null;
		List empPayDtls=null;
		List<CmnLookupMst> reasonList=null;
		List dsgnList= null;
		Long ternId=null;
		List empSavedDtls=null;
		List listTypeOfForm = null;
		String user="";
		String status="";
	
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationDtlsDAO lObjTerminationDtlsDAO = null;
			if(StringUtility.getParameter("user",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("user",request)))
			{
				user = StringUtility.getParameter("user",request).trim();
			}
			
			if(StringUtility.getParameter("txtSevaarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("txtSevaarthId",request)))
			{
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(null, serv
						.getSessionFactory());
				lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
				
				empDtls=lObjTerminationDtlsDAO.getReportData(lStrSevarthId);
				  
				
			inputMap.put("empDtls", empDtls);
			if(empDtls!=null)
			{
				inputMap.put("sizeOfList", empDtls.size());
				Object[] obj=(Object[]) empDtls.get(0);
				if(obj!=null && obj[9]!=null)
				{
					status=obj[9].toString();
				}
			}
			else
			{
				inputMap.put("sizeOfList", 0);
			}
			if(user!=null && "SRKA".equalsIgnoreCase(user))
			{
				if("60002".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
				}
				if("60003".equalsIgnoreCase(status)  || "60005".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
				}
				if("60004".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
					
					
					CmnLookupMst tempCmnLookupMst4 = new CmnLookupMst();
					tempCmnLookupMst4.setLookupId(4);
					tempCmnLookupMst4.setLookupDesc("Form D");
					listTypeOfForm.add(tempCmnLookupMst4);
				}
				if("60006".equalsIgnoreCase(status) || "60007".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
					

					CmnLookupMst tempCmnLookupMst3 = new CmnLookupMst();
					tempCmnLookupMst3.setLookupId(3);
					tempCmnLookupMst3.setLookupDesc("Form C");
					listTypeOfForm.add(tempCmnLookupMst3);
					
					
					CmnLookupMst tempCmnLookupMst4 = new CmnLookupMst();
					tempCmnLookupMst4.setLookupId(4);
					tempCmnLookupMst4.setLookupDesc("Form D");
					listTypeOfForm.add(tempCmnLookupMst4);
					
				}
				if("60008".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
					

					CmnLookupMst tempCmnLookupMst3 = new CmnLookupMst();
					tempCmnLookupMst3.setLookupId(3);
					tempCmnLookupMst3.setLookupDesc("Form C");
					listTypeOfForm.add(tempCmnLookupMst3);
					
					
					CmnLookupMst tempCmnLookupMst4 = new CmnLookupMst();
					tempCmnLookupMst4.setLookupId(4);
					tempCmnLookupMst4.setLookupDesc("Form D");
					listTypeOfForm.add(tempCmnLookupMst4);
					
					CmnLookupMst tempCmnLookupMst5 = new CmnLookupMst();
					tempCmnLookupMst5.setLookupId(5);
					tempCmnLookupMst5.setLookupDesc("Form E");
					listTypeOfForm.add(tempCmnLookupMst5);
				}
				
				
			}
			else if(user!=null && "DDO".equalsIgnoreCase(user))
			{
				if("60002".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
				}
				if("60003".equalsIgnoreCase(status)  || "60005".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
					
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
				}
				if("60004".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
					
					
					CmnLookupMst tempCmnLookupMst4 = new CmnLookupMst();
					tempCmnLookupMst4.setLookupId(4);
					tempCmnLookupMst4.setLookupDesc("Form D");
					listTypeOfForm.add(tempCmnLookupMst4);
				}
				if("60006".equalsIgnoreCase(status) || "60007".equalsIgnoreCase(status) || "60008".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
					

					CmnLookupMst tempCmnLookupMst3 = new CmnLookupMst();
					tempCmnLookupMst3.setLookupId(3);
					tempCmnLookupMst3.setLookupDesc("Form C");
					listTypeOfForm.add(tempCmnLookupMst3);
					
					
					CmnLookupMst tempCmnLookupMst4 = new CmnLookupMst();
					tempCmnLookupMst4.setLookupId(4);
					tempCmnLookupMst4.setLookupDesc("Form D");
					listTypeOfForm.add(tempCmnLookupMst4);
					
				}
			}
			else if(user!=null && "TO".equalsIgnoreCase(user))
			{
				
				if("60002".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
				}
				if("60003".equalsIgnoreCase(status)  || "60005".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
				
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
				}
				
			
				if("60004".equalsIgnoreCase(status) || "60006".equalsIgnoreCase(status) || "60007".equalsIgnoreCase(status) || "60008".equalsIgnoreCase(status))
				{
					listTypeOfForm= new ArrayList();
					CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
					tempCmnLookupMst1.setLookupId(1);
					tempCmnLookupMst1.setLookupDesc("Form A");
					listTypeOfForm.add(tempCmnLookupMst1);
					
				
					CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
					tempCmnLookupMst2.setLookupId(2);
					tempCmnLookupMst2.setLookupDesc("Form B");
					listTypeOfForm.add(tempCmnLookupMst2);
					
				
					CmnLookupMst tempCmnLookupMst4 = new CmnLookupMst();
					tempCmnLookupMst4.setLookupId(4);
					tempCmnLookupMst4.setLookupDesc("Form D");
					listTypeOfForm.add(tempCmnLookupMst4);
				}
				
			}
			
			
			inputMap.put("listTypeOfForm", listTypeOfForm);
		}
		
			
			inputMap.put("lStrSevarthId", lStrSevarthId);
			inputMap.put("user", user);
			resObj.setViewName("printTerminationFormsReport");
			resObj.setResultValue(inputMap);

		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	public ResultObject checkSearchDtlsForReport(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId="";
		String lStrDDOCode="";
		String flag="No";
		String user="";
		
		try {

			setSessionInfo(inputMap);
			lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
			user = StringUtility.getParameter("user",request).trim();
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAO = new TerminationDtlsDAOImpl(null, serv
					.getSessionFactory());
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			
			
		

			List savedDtls=lObjTerminationDtlsDAO.getAlreadySavedData(lStrSevarthId);
			if(savedDtls!=null && savedDtls.size()>0)
			{
				
				Object[] objSaved=(Object[]) savedDtls.get(0);
				if(objSaved!=null && objSaved[0]!=null)
				{
					if(user!=null && "SRKA".equalsIgnoreCase(user))
					{
						if("60001".equalsIgnoreCase(objSaved[0].toString()))
						{
							flag="Saved";
						}
						else
						{
							flag="Yes";
						}
					}
					else if(user!=null && "DDO".equalsIgnoreCase(user))
					{
						
						if("60001".equalsIgnoreCase(objSaved[0].toString()))
						{
							flag="Saved";
						}
						else
						{
							flag="Yes";
						}
					}
					else if(user!=null && "TO".equalsIgnoreCase(user))
					{
						
						if("60001".equalsIgnoreCase(objSaved[0].toString()))
						{
							flag="Saved";
						}
						else
						{
							flag="Yes";
						}
					}
					
				}
				
			}
			else
			{
				
					flag="Saved";
				
				
			}
			String lSBStatus = getResponseXMLDoc(flag).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	private StringBuilder getResponseXMLDoc(String flag) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	private StringBuilder getResponseXMLDoc(String flag,String lStrSevarthId,Long pk,String tranId) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<lStrSevarthId>");
		lStrBldXML.append(lStrSevarthId);
		lStrBldXML.append("</lStrSevarthId>");
		lStrBldXML.append("<pk>");
		lStrBldXML.append(pk);
		lStrBldXML.append("</pk>");
		lStrBldXML.append("<tranId>");
		lStrBldXML.append(tranId);
		lStrBldXML.append("</tranId>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	private StringBuilder getResponseXMLDoc(String flag,String Termflag) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<Termflag>");
		lStrBldXML.append(Termflag);
		lStrBldXML.append("</Termflag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	private StringBuilder getResponseXMLDoc(String flag,String Termflag,String sevarthId) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<Termflag>");
		lStrBldXML.append(Termflag);
		lStrBldXML.append("</Termflag>");
		lStrBldXML.append("<sevarthId>");
		lStrBldXML.append(sevarthId);
		lStrBldXML.append("</sevarthId>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	private StringBuilder getResponseXMLDoc(String flag,String Termflag,String savedFlag,String user,String status) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<Termflag>");
		lStrBldXML.append(Termflag);
		lStrBldXML.append("</Termflag>");
		lStrBldXML.append("<savedFlag>");
		lStrBldXML.append(savedFlag);
		lStrBldXML.append("</savedFlag>");
		lStrBldXML.append("<user>");
		lStrBldXML.append(user);
		lStrBldXML.append("</user>");
		lStrBldXML.append("<status>");
		lStrBldXML.append(status);
		lStrBldXML.append("</status>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	
}
