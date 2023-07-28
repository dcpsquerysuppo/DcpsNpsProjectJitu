package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.tcs.sgv.dcps.dao.DeputationEmpBankUpdateDao;
import com.tcs.sgv.dcps.dao.DeputationEmpBankUpdateDaoImpl;
import com.tcs.sgv.dcps.dao.EmpBankUpdateDao;
import com.tcs.sgv.dcps.dao.EmpBankUpdateDaoImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class DeputationEmpBankUpdateServiceImpl extends ServiceImpl {
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	
	
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			session = request.getSession();
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}
	private void setSessionInfoSchdlr(Map inputMap) {

		try {
			//request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			//session = (Session)inputMap.get("currentSession");
			Map  SchedlrLoginMap =(HashMap)inputMap.get("baseLoginMap");
			gStrPostId = String.valueOf(SchedlrLoginMap.get("postId"));
			gLngPostId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("postId")));
			gStrLocationCode =String.valueOf(SchedlrLoginMap.get("locationCode"));
			gLngUserId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("userId")));
			gStrUserId = String.valueOf(SchedlrLoginMap.get("userId"));
			gLngDBId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("dbId")));
			gDtCurDate = new Date();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}
	
	
	
	
	
	public ResultObject getEmpList(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);

		
	
			DeputationEmpBankUpdateDao lObjDcpsCommonDAO = new DeputationEmpBankUpdateDaoImpl(null, serv
					.getSessionFactory());
			
			List treasuries = null;
			treasuries = lObjDcpsCommonDAO.getAdminDepartment();
			List finalList = getIdDescList(treasuries);
			inputMap.put("treasuries", finalList);
			
			List empList=null;
			inputMap.put("empList", empList);
			resObj.setViewName("bulkBankUpdateForDeputation");
			resObj.setResultValue(inputMap);
			
		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			gLogger.error(" Error is : " + ex, ex);
			return resObj;
		}

		return resObj;	

	}

	
	
	
	
	public ResultObject getFieldDepartmentFromAdminDepartment(Map<String, Object> inputMap) throws Exception 
	{
		gLogger.info("Inside getSubTreasuryFromTreasury");
		setSessionInfo(inputMap);

		DeputationEmpBankUpdateDao lObjDcpsCommonDAO = new DeputationEmpBankUpdateDaoImpl(null, serv
				.getSessionFactory());
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTreasuryName = request.getParameter("cmbTrName");
		//String lStrTreasuryId = request.getParameter("treasuryId");
		gLogger.info("Treasury name:" +lStrTreasuryName);
		//gLogger.info("treasuryId:" +lStrTreasuryId);
		List<ComboValuesVO> subTreasury=lObjDcpsCommonDAO.getFieldDepartmentFromAdminDepartment(lStrTreasuryName);
		gLogger.info("size" +subTreasury.size());
		gLogger.info("Outside getSubTreasuryFromTreasury");
		inputMap.put("subTreasury", subTreasury);
		gLogger.info("AJAXXXXX");
		gLogger.info("size of subTreasury:" + subTreasury.size());
		String ajaxResult= null;
		List<ComboValuesVO> finalSubTreasuryList = new ArrayList<ComboValuesVO>();
		Map result = new HashMap();
		ComboValuesVO cmbVO = new ComboValuesVO();
		cmbVO.setId("-1");
		cmbVO.setDesc("-- Select --");
		finalSubTreasuryList.add(cmbVO);

		if (subTreasury != null && subTreasury.size() > 0)
		{
			Iterator IT = subTreasury.iterator();

			cmbVO = new ComboValuesVO();
			Object[] lObj = null;
			while (IT.hasNext())
			{
				cmbVO = new ComboValuesVO();
				lObj = (Object[]) IT.next();
				cmbVO.setId(lObj[0].toString());
				//cmbVO.setDesc(lObj[1].toString());
				cmbVO.setDesc("<![CDATA[ "+lObj[1].toString()+"("+lObj[0].toString()+")]]>");
				finalSubTreasuryList.add(cmbVO);
			}
		}
		gLogger.info("AJAXXXXX");
		
		
		ajaxResult = new AjaxXmlBuilder().addItems(finalSubTreasuryList, "desc", "id").toString();
		
		gLogger.info("ajax builder created");
		gLogger.info("Ajax result:" +ajaxResult);	
		inputMap.put("ajaxKey", ajaxResult);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		
	
		return resObj;
	}
	
	
	public ResultObject getBulkEmplForDeputation (Map inputMap) throws Exception
	{
		gLogger.info("Inside getBulkEmplForDDO");


		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try
		{
			gLogger.info("in getEmployeeListForBankDetailsUpdate");
			setSessionInfo(inputMap);

			String lStrUserZP = "zpDDO";
			String lStrUseZP = "bank_barnch_update";
			//String lStrUseZP = "";

			inputMap.put("User", lStrUserZP.trim());
			inputMap.put("Use", lStrUseZP.trim());
			//NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());
			DeputationEmpBankUpdateDao lObjDcpsCommonDAO = new DeputationEmpBankUpdateDaoImpl(null, serv
					.getSessionFactory());
			BankDetailsUpdateDao lObjBankDetailsUpdateDao=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			String empIds=null;
			String cmbBank=null;
			String cmbBranch=null;
			String accountNumber=null;
			String savedFlag="N";
			
			//String lStrDDO = request.getParameter("DDOCode");
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

			
			String lStrTreasuryCode = request.getParameter("TreasuryCode");
			String lStrSubTreasuryCode = request.getParameter("SubTrCode");
			String SevaarthId = request.getParameter("SevaarthId");
			
			gLogger.info("hii SevaarthId"+SevaarthId);
			List subTreasury=lObjDcpsCommonDAO.getFieldDepartmentFromAdminDepartment(lStrTreasuryCode);
			List<ComboValuesVO> idDescSubTrsryList = getIdDescList(subTreasury);
			
		/*	List DDOList=lObjDcpsCommonDAO.getDDOForSubTreasury(lStrSubTreasuryCode);
			List<ComboValuesVO> idDescDDOList = getIdDescList(DDOList);*/
			
			//gLogger.info("hi "+lStrDDO);
			List treasuries = null;
			
			empList = lObjDcpsCommonDAO.getAllDcpsDeputationEmployeesZPForBankUpdate(lStrSubTreasuryCode,SevaarthId);
			
			
			
			treasuries = lObjDcpsCommonDAO.getAdminDepartment();
			List<ComboValuesVO> idDescTrsryList = getIdDescList(treasuries);
			inputMap.put("treasuries", idDescTrsryList);
			inputMap.put("subTreasuryNames", idDescSubTrsryList);
			//inputMap.put("DDONames", idDescDDOList);
			
			inputMap.put("selectedTreasy", lStrTreasuryCode);
			inputMap.put("selectedSubTreasuryNames", lStrSubTreasuryCode);                                                                    
		//	inputMap.put("selectedDDONames", lStrDDO);
			//added by roshan for performance tuning
			
			inputMap.put("empList", empList);
			//ended by roshan for performance tuning
			
			inputMap.put("EditForm", "N");
			
			
			
			// Get the Bank Names
			EmpBankUpdateDao lObjEmpBankUpdateDao=new EmpBankUpdateDaoImpl(MstEmp.class, serv.getSessionFactory());

			List lLstBankNames = lObjEmpBankUpdateDao.getBankNames();
			inputMap.put("BANKNAMES", lLstBankNames);
			lLstBankNames=null;
			inputMap.put("savedFlag",savedFlag);
			
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("bulkBankUpdateForDeputation");
			
			

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
	
	
	
	
	public ResultObject checkBankAccountNoForDeputation(Map objectArgs)
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		
		String bank=null;
		String accountNumber=null;
		Long finalCheckFlag=null;
		String lStrResult = null;
		String empId = null;
		String sevarth_id=null;
		String Ddo_code=null;
		String Off_name=null;
		List EmpList;
		try {
			BankDetailsUpdateDao lObjBankDetailsUpdateDao=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			
			
			String res=lObjBankDetailsUpdateDao.checkBankDetails(bank,accountNumber);
			
			bank=StringUtility.getParameter("bank", request).trim();
			accountNumber = StringUtility.getParameter("accountNumber", request).trim();			
			empId = StringUtility.getParameter("empId", request).trim();
			//NewRegDdoDAOImpl objNewRegDdoDaoImpl=new NewRegDdoDAOImpl(HrPayGpfBalanceDtls.class,serv.getSessionFactory());
			BankDetailsUpdateDao lObjBankDetailsUpdateDaoImpl=new BankDetailsUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			
			
			
			DeputationEmpBankUpdateDao lObjDcpsCommonDAO = new DeputationEmpBankUpdateDaoImpl(null, serv
					.getSessionFactory());
			//finalCheckFlag=lObjBankDetailsUpdateDaoImpl.checkBankAccountNumber(bank,accountNumber,empId);
			EmpList=lObjDcpsCommonDAO.checkBankAccountNumberForDeputation(bank,accountNumber,empId);
			
			String status=null;
			/*if(finalCheckFlag>0){
				status="wrong";
			}*/
			/*if(EmpList.size()>0){
				status="wrong";
			}
			else{
				status="correct";
			}*/
			
			
			
			if (EmpList != null && !EmpList.isEmpty() && EmpList.size() > 0)
			{
				Iterator IT = EmpList.iterator();
				Object[] lObj = null;
				while (IT.hasNext())
				{					
					lObj = (Object[]) IT.next();
					sevarth_id =lObj[0].toString();
					gLogger.info("**************officeName*************" +sevarth_id);
					if (lObj[1]!=null && lObj[1]!="")
					{
					Ddo_code= lObj[1].toString();
					gLogger.info("***********Ddo_code for employee deputation********" +Ddo_code);
					}
					Off_name= lObj[2].toString();
					gLogger.info("***********ddoTreasury*********" +Off_name);
				}
				status="wrong";
			}
			
			else{
				status="correct";
			}
			gLogger.error(" the value of status is " +status);
			/*String lSBStatus = getResponseXMLDocForDDOFwd(status).toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
			objRes.setResultValue(objectArgs);
			objRes.setViewName("ajaxData");*/
			String lSBStatus="";
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<Status>");
			lStrBldXML.append(status);
			lStrBldXML.append("</Status>");
			lStrBldXML.append("<Sevarthid>");
			lStrBldXML.append(sevarth_id);
			lStrBldXML.append("</Sevarthid>");
			lStrBldXML.append("<DdoCode>");
			lStrBldXML.append(Ddo_code);
			lStrBldXML.append("</DdoCode>");
			lStrBldXML.append("<OffName>");
			lStrBldXML.append(Off_name);
			lStrBldXML.append("</OffName>");
			lStrBldXML.append("</XMLDOC>");
			lSBStatus = lStrBldXML.toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
			gLogger.info("lStrResult---------" + lStrResult);
			objRes.setResultValue(objectArgs);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setViewName("ajaxData");
			return objRes;
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
	
	
	
	
	
	public ResultObject CheckFieldDepartmentOfemployee(Map objectArgs) {
	    this.gLogger.info("IN getActivateDDOCode");
	    ResultObject resultObject = new ResultObject(0);
	    Map loginDetailsMap = (Map)objectArgs.get("baseLoginMap");
	    long langId = Long.parseLong(loginDetailsMap.get("langId").toString());

	    String ddoCode = "";
	    List ddoCodeList = null;
	    boolean check = false;
	    String lStrResult = "";
	    String flag = "true";
	    try
	    {
	      ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
	      HttpServletRequest request = (HttpServletRequest)objectArgs.get("requestObj");

	    
	     
	      this.gLogger.info("--------ddoCode--------:" + ddoCode);
	      String status=null;
			
			
			
			
			gLogger.error(" the value of status is " +status);
			
			
			
			
			
			
			DeputationEmpBankUpdateDao lObjDcpsCommonDAO = new DeputationEmpBankUpdateDaoImpl(null, serv
					.getSessionFactory());
			

			int temp=0;
			String lStrSubTreasuryCode =StringUtility.getParameter("subTrCode", request).trim();
			gLogger.info("lStrSubTreasuryCode---------" + lStrSubTreasuryCode);
			temp=Integer.parseInt(lStrSubTreasuryCode);
			gLogger.info("lStrSubTreasuryCode ------temp---" + temp);
					String SevaarthId =  StringUtility.getParameter("SevaarthId", request).trim();
				
					gLogger.info("SevaarthId---------" + SevaarthId);
					
					
					int count= lObjDcpsCommonDAO.CheckFieldDepartmentOfemployee(SevaarthId);
				

					gLogger.info("SevaarthId---------" + count);

					if (count!=temp)
					{
						
						gLogger.info(" Inside the if of field deprtment does not match" );
						status="Wrong";
						flag = "invalid";
					}
			

	      StringBuilder lStrBldXML = new StringBuilder();
	     

	      this.gLogger.info("********************************" + flag);
	      String lSBStatus = getResponseXMLDocForValidDDOCodeDtls(flag).toString();
	      lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

	      objectArgs.put("ajaxKey", lStrResult);
	      this.gLogger.info("lStrResult---------" + lStrResult);

	      resultObject.setResultValue(objectArgs);
	      resultObject.setResultCode(0);
	      resultObject.setViewName("ajaxData");
	    } catch (Exception e) {
	      e.printStackTrace();
	    }

	    return resultObject;
	  }
	 private Object getResponseXMLDocForValidDDOCodeDtls(String flag)
	  {
	    StringBuilder lStrBldXML = new StringBuilder();

	    lStrBldXML.append("<XMLDOC>");
	    lStrBldXML.append("<Flag>");
	    lStrBldXML.append(flag);
	    lStrBldXML.append("</Flag>");
	    lStrBldXML.append("</XMLDOC>");

	    return lStrBldXML;
	  }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
