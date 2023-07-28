package com.tcs.sgv.dcps.service;

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

public class TerminationRqstsServiceImpl extends ServiceImpl  {
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

	public ResultObject loadTerminationReques(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		List empDtls=null;
		String user="";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationRqstsDAO lObjTerminationRqstsDAO = new TerminationDtlsRqstsDAOImpl(null, serv.getSessionFactory());
			user=StringUtility.getParameter("user",request);
			empDtls=lObjTerminationRqstsDAO.getEmployees(gStrLocationCode,user);
			inputMap.put("empDtls", empDtls);
			if(empDtls!=null)
			{
				inputMap.put("sizeOfList", empDtls.size());
			}
			else
			{
				inputMap.put("sizeOfList", 0);
			}
			inputMap.put("user", user);
			resObj.setViewName("terminationReques");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	public ResultObject proceedForTermination(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		List empDtls=null;
		String tranId=null;
		List empPayDtls=null;
		String lStrSevarthId=null;
		List empSavedDtls=null;
		Long ternId=null;
		List missingCredit=null;
		List closingBal=null;
		List legacyDyls=null;
		int missingCreditSize=0;
		String closingBalance="0";
		String legacyAmount="0";
		String user="";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationRqstsDAO lObjTerminationRqstsDAO = new TerminationDtlsRqstsDAOImpl(null, serv.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAO= null;;
			if(StringUtility.getParameter("tranId",request)!=null)
			{
				tranId=StringUtility.getParameter("tranId",request);
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(null, serv
						.getSessionFactory());
				
				if(StringUtility.getParameter("lStrSevarthId",request)!=null)
				{
					lStrSevarthId=StringUtility.getParameter("lStrSevarthId",request);
				}
				if(StringUtility.getParameter("user",request)!=null)
				{
					user=StringUtility.getParameter("user",request);
				}
			
				empDtls=lObjTerminationDtlsDAO.getEmpDetails(null,tranId);
				empPayDtls=lObjTerminationDtlsDAO.getEmpPayDetails(lStrSevarthId);
				empSavedDtls=lObjTerminationDtlsDAO.getEmpSavedDetails(lStrSevarthId);
				missingCredit=lObjTerminationRqstsDAO.getEmployeeMissingCredits(lStrSevarthId);
				closingBal=lObjTerminationRqstsDAO.getEmployeeClosingBalance(lStrSevarthId);
				legacyDyls=lObjTerminationRqstsDAO.getEmployeeLegacyOldAmount(lStrSevarthId);
				if(missingCredit!=null)
				{
					missingCreditSize=missingCredit.size();
				}
				if(closingBal!=null && closingBal.size()>0)
				{
					closingBalance=closingBal.get(0).toString();
				}
				if(legacyDyls!=null && legacyDyls.size()>0 && legacyDyls.get(0)!=null)
				{
					legacyAmount=legacyDyls.get(0).toString();
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
									
									inputMap.put("updatedTermDate", objSaved[0].toString());	
									
									
								}
								if(objSaved[1]!=null)
								{
									objNew[7]=objSaved[1];
									inputMap.put("updatedTermReason", objSaved[1].toString());
								}
								if(objSaved[2]!=null)
								{
									objNew[8]=objSaved[2];
									objNew[10]=objSaved[2];
									inputMap.put("currAddress", objSaved[2].toString());
								}
								if(objSaved[3]!=null)
								{
									objNew[5]=objSaved[3];
									inputMap.put("dsgn", objSaved[3].toString());
								}
							}
						}
						gLogger.info("objNew"+objNew.toString());
						empDtls.set(0, objNew);
					}
				}
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
			inputMap.put("user", user);
			inputMap.put("missingCredit", missingCredit);
			inputMap.put("lStrSevarthId", lStrSevarthId);
			inputMap.put("closingBalance", closingBalance);
			inputMap.put("legacyAmount", legacyAmount);
			inputMap.put("missingCreditSize", missingCreditSize);
			resObj.setViewName("printFormD");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	
	
	public ResultObject rejectTermFromTO(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
	
		String tranId=null;

		String lStrSevarthId=null;
		String remarks=null;
		String user="";
	
		try {
			
			setSessionInfo(inputMap);
			
			//ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			TerminationRqstsDAO lObjTerminationRqstsDAO = new TerminationDtlsRqstsDAOImpl(null, serv.getSessionFactory());
			TerminationDtlsDAO lObjTerminationDtlsDAO= null;
			TerminationDtlsDAO lObjTerminationDtlsDAOHst=null;
			if(StringUtility.getParameter("tranId",request)!=null)
			{
				tranId=StringUtility.getParameter("tranId",request);
				lObjTerminationDtlsDAO=new TerminationDtlsDAOImpl(TrnTerminalDtls.class, serv.getSessionFactory());
				
				if(StringUtility.getParameter("lStrSevarthId",request)!=null)
				{
					lStrSevarthId=StringUtility.getParameter("lStrSevarthId",request);
				}
				if(StringUtility.getParameter("remarks",request)!=null)
				{
					remarks=StringUtility.getParameter("remarks",request);
				}
				if(StringUtility.getParameter("user",request)!=null)
				{
					user=StringUtility.getParameter("user",request);
				}
				
				Long pk=lObjTerminationRqstsDAO.getPk(tranId);
				if(pk!=null)
				{
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
		            	
					   if(user!=null && "TO".equalsIgnoreCase(user))
						{
	
							objTermDtls.setStatusFlag(60005l);
							objTermDtls.setReasonOfTOReject(remarks);
						}
					   else
					   {

							objTermDtls.setStatusFlag(60007l);
							objTermDtls.setReasonOfTOReject(remarks);
					   }
					}
					objTermDtls.setUpdatedDate(gDtCurDate);
					lObjTerminationDtlsDAO.update(objTermDtls);
				}
				
			}
			
				
			String lSBStatus = getResponseXMLDoc(true,lStrSevarthId,tranId,user).toString();
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
	
	
	public ResultObject generateTermFormE(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empSavedDtls=null;
		List missingCredit=null;
		String strMissingCredit="No";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			
			if(StringUtility.getParameter("txtSevaarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("txtSevaarthId",request)))
			{
				TerminationRqstsDAO lObjTerminationRqstsDAO = new TerminationDtlsRqstsDAOImpl(null, serv.getSessionFactory());
				lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
				missingCredit=lObjTerminationRqstsDAO.getEmployeeMissingCredits(lStrSevarthId);
			
				if(missingCredit!=null && missingCredit.size()>0)
				{
					strMissingCredit="Yes";
				}
				
				
					
			
			}
			String lSBStatus = getResponseXMLDoc(strMissingCredit,lStrSevarthId).toString();
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
	public ResultObject generateTermFormD(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrSevarthId = "";
		List empSavedDtls=null;
		List missingCredit=null;
		String strMissingCredit="No";
		try {
			
			ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString()); 
			long locId=StringUtility.convertToLong(loginDetailsMap.get("locationId").toString());
			setSessionInfo(inputMap);
			
			if(StringUtility.getParameter("txtSevaarthId",request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("txtSevaarthId",request)))
			{
				TerminationRqstsDAO lObjTerminationRqstsDAO = new TerminationDtlsRqstsDAOImpl(null, serv.getSessionFactory());
				lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
				missingCredit=lObjTerminationRqstsDAO.getEmployeeMissingCredits(lStrSevarthId);
			
				if(missingCredit!=null && missingCredit.size()>0)
				{
					strMissingCredit="Yes";
				}
				
				
					
			
			}
			String lSBStatus = getResponseXMLDoc(strMissingCredit,lStrSevarthId).toString();
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
	
	public ResultObject generateTermFormEUpdate(Map inputMap) throws Exception {

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
		            	
		            	
						objTermDtls.setStatusFlag(60008l);
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
	
	public ResultObject generateTermFormDUpdate(Map inputMap) throws Exception {

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
		            	
						objTermDtls.setStatusFlag(60004l);
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
	
	private StringBuilder getResponseXMLDoc(Boolean flag,String sevarthId,String tranId,String user) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<sevarthId>");
		lStrBldXML.append(sevarthId);
		lStrBldXML.append("</sevarthId>");
		lStrBldXML.append("<tranId>");
		lStrBldXML.append(tranId);
		lStrBldXML.append("</tranId>");
		lStrBldXML.append("<user>");
		lStrBldXML.append(user);
		lStrBldXML.append("</user>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	
}
