package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.BankDetailsUpdateDao;
import com.tcs.sgv.dcps.dao.BankDetailsUpdateDaoImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;

public class BankDetailsUpdateServiceImpl extends ServiceImpl implements BankDetailsUpdateService {
	
	private final Log gLogger = LogFactory.getLog(getClass());

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

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
		}
	}
	public ResultObject getDdoList(Map<String, Object> inputMap)
	{
		gLogger.info("inside getDdoList for treasury");
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		try {
			Long TO =  SessionHelper.getLocationId(inputMap);
			String TOCode=TO.toString();
			gLogger.info("TO Loc Id:::"+TOCode);
			
			BankDetailsUpdateDao lObjBankDetailsUpdateDao=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			
			
			List DDOList = null;
			DDOList = lObjBankDetailsUpdateDao.getDDO(TOCode);
			gLogger.info("size of DDOList:::"+DDOList.size());
			List finalList = getIdDescList(DDOList);
			inputMap.put("DDOs", finalList);
			gLogger.info("size of finalList:::"+finalList.size());
			
			List empList=null;
			inputMap.put("empList", empList);
			
			resObj.setViewName("bankDetailsUpdateTreasury");	
			resObj.setResultValue(inputMap);
		} 
		catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			return resObj;
		}
		return resObj;
	}
	List getIdDescList(List list){
		List finalList = new ArrayList();
		ComboValuesVO cmbVO = new ComboValuesVO();
		cmbVO.setId("-1");
		cmbVO.setDesc("-- Select --");
		finalList.add(cmbVO);

		if (list != null && list.size() > 0)
		{
			Iterator IT = list.iterator();

			cmbVO = new ComboValuesVO();
			Object[] lObj = null;
			while (IT.hasNext())
			{
				cmbVO = new ComboValuesVO();
				lObj = (Object[]) IT.next();
				cmbVO.setId(lObj[0].toString());
				//cmbVO.setDesc(lObj[1].toString());
				//cmbVO.setDesc("<![CDATA[( "+lObj[0].toString()+" )"+lObj[1].toString()+"]]>");
				cmbVO.setDesc(lObj[1].toString()+"("+lObj[0].toString()+")");
				finalList.add(cmbVO);
			}
		}
		return finalList;
	}
	
	public ResultObject getEmplForDDO (Map inputMap)
	{
		gLogger.info("Inside getEmplForDDO for treasury");


		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try
		{
			gLogger.info("in getEmplForDDO for treasury");
			setSessionInfo(inputMap);

			String lStrUserZP = "zpDDO";
			String lStrUseZP = "bank_barnch_update";
			//String lStrUseZP = "";

			inputMap.put("User", lStrUserZP.trim());
			inputMap.put("Use", lStrUseZP.trim());
			//NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());
			BankDetailsUpdateDao lObjBankDetailsUpdateDao=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());

			String empIds=null;
			String cmbBank=null;
			String cmbBranch=null;
			String accountNumber=null;
			String savedFlag="N";
			String lStrDDO = request.getParameter("DDOCode");
			gLogger.info("DDOCode: " + lStrDDO);
			List empList=null;
			if((StringUtility.getParameter("empIds", request)!=null)&&(StringUtility.getParameter("empIds", request)!="")){
				empIds= StringUtility.getParameter("empIds", request);
				cmbBank= StringUtility.getParameter("bankId", request);	
				cmbBranch=StringUtility.getParameter("branchId", request);
				accountNumber=StringUtility.getParameter("acntNo", request);
				
				
				String[] lstrIds = empIds.split("~");
				String[] bank = cmbBank.split("~");
				String[] branch=cmbBranch.split("~");
				String[] acnt=accountNumber.split("~");
				
				String[] lstrEmpIDs = new String[lstrIds.length];
				String[] bankArr = new String[bank.length];
				String[] lstrNewBasic= new String[branch.length];
				String[] accntNo=new String[acnt.length];
				
				for (Integer lInt = 0; lInt < lstrIds.length; lInt++)
				{
					if (lstrIds[lInt] != null && !"".equals(lstrIds[lInt]))
					{
						lstrEmpIDs[lInt] = lstrIds[lInt];
						bankArr[lInt] = bank[lInt];
						lstrNewBasic[lInt]=branch[lInt];
						accntNo[lInt]=acnt[lInt];
							
						gLogger.info("hii********** "+lstrEmpIDs[lInt]);
						gLogger.info("hii********** "+bankArr[lInt]);
						gLogger.info("hii********** "+lstrNewBasic[lInt]);
						gLogger.info("hii********** "+accntNo[lInt]);
					}
				}

				for (Integer lInt = 0; lInt < lstrIds.length; lInt++)
				{
					lObjBankDetailsUpdateDao.updateBankDetails(lstrEmpIDs[lInt],bankArr[lInt],lstrNewBasic[lInt],accntNo[lInt]);
					savedFlag="Y";
				}
			}
			Long TO =  SessionHelper.getLocationId(inputMap);
			String lStrSubTreasuryCode=TO.toString();
			
			
			
			
			List DDOList=lObjBankDetailsUpdateDao.getDDO(lStrSubTreasuryCode);
			List<ComboValuesVO> idDescDDOList = getIdDescList(DDOList);
			
			
			
			
			gLogger.info("hi "+lStrDDO);
			List treasuries = null;
			empList = lObjBankDetailsUpdateDao.getAllDcpsEmployeesZPForBankUpdate(lStrDDO);
			inputMap.put("DDOs", idDescDDOList);
			inputMap.put("selectedDDONames", lStrDDO);
			
			//added by roshan for performance tuning
			inputMap.put("empList", empList);
			//ended by roshan for performance tuning
			inputMap.put("EditForm", "N");
			// Get the Bank Names
			List lLstBankNames = lObjBankDetailsUpdateDao.getBankNames();
			inputMap.put("BANKNAMES", lLstBankNames);
			lLstBankNames=null;
			
			inputMap.put("savedFlag",savedFlag);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("bankDetailsUpdateTreasury");
			

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
	public ResultObject getBankBranchList(Map<String, Object> lMapInputMap) {
		gLogger.info("in getBankBranchList for treasury");

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS,
		"FAIL");
		List branchList = null;
		String cmbBank=null;
		ServiceLocator serv = (ServiceLocator)lMapInputMap.get("serviceLocator");
		Map loginDetailsMap = (Map) lMapInputMap.get("baseLoginMap");

		Map voToService = (Map)lMapInputMap.get("voToServiceMap");
	
		try {

			/* Sets the Session Information */
			setSessionInfo(lMapInputMap);

			/* Initializes the DAO */
			//NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());
			BankDetailsUpdateDao lObjBankDetailsUpdateDao=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			cmbBank = StringUtility.getParameter("cmbBank", request).trim();
			gLogger.info("hhi bank name  is"+cmbBank);
			
			if((cmbBank!=null) && (cmbBank!="") && (Long.parseLong(cmbBank)!=-1)){
				
				branchList=lObjBankDetailsUpdateDao.getBankBranchList(cmbBank);
			}
			
			

			String lStrTempResult = null;
			if (branchList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(
						branchList, "desc", "id", true).toString();
			}
			
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}
	
	//added by samadhan for bank account check ajax
	public ResultObject checkBankAccountNo(Map objectArgs)
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		
		String bank=null;
		String accountNumber=null;
		Long finalCheckFlag=null;
		String lStrResult = null;
		String empId = null;
		try {
			BankDetailsUpdateDao lObjBankDetailsUpdateDao=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			
			
			String res=lObjBankDetailsUpdateDao.checkBankDetails(bank,accountNumber);
			
			bank=StringUtility.getParameter("bank", request).trim();
			accountNumber = StringUtility.getParameter("accountNumber", request).trim();			
			empId = StringUtility.getParameter("empId", request).trim();
			//NewRegDdoDAOImpl objNewRegDdoDaoImpl=new NewRegDdoDAOImpl(HrPayGpfBalanceDtls.class,serv.getSessionFactory());
			BankDetailsUpdateDao lObjBankDetailsUpdateDaoImpl=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			
			finalCheckFlag=lObjBankDetailsUpdateDaoImpl.checkBankAccountNumber(bank,accountNumber,empId);
			
			String status=null;
			if(finalCheckFlag>0){
				status="wrong";
			}

			else{
				status="correct";
			}
			String lSBStatus = getResponseXMLDocForDDOFwd(status).toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
			objRes.setResultValue(objectArgs);
			objRes.setViewName("ajaxData");
			
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	private StringBuilder getResponseXMLDocForDDOFwd(String status) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(status);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	//ended by samadhan for bank account check ajax
	
	
}
