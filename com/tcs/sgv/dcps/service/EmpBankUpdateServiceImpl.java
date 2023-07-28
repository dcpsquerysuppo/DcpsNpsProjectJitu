package com.tcs.sgv.dcps.service;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
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
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.ViewReportDAO;
import com.tcs.sgv.dcps.dao.ViewReportDaoImpl;
import com.tcs.sgv.dcps.dao.EmpBankUpdateDao;
import com.tcs.sgv.dcps.dao.EmpBankUpdateDaoImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class EmpBankUpdateServiceImpl extends ServiceImpl{

	/* Global Variable for Logger Class */
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

		
			EmpBankUpdateDao lObjEmpBankUpdateDao=new EmpBankUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			
			
			List treasuries = null;
			treasuries = lObjEmpBankUpdateDao.getTreasury();
			List finalList = getIdDescList(treasuries);
			inputMap.put("treasuries", finalList);
			
			List empList=null;
			inputMap.put("empList", empList);
			resObj.setViewName("bulkBankUpdate");
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
	
	public ResultObject getSubTreasuryFromTreasury (Map<String, Object> inputMap) throws Exception 
	{
		gLogger.info("Inside getSubTreasuryFromTreasury");
		setSessionInfo(inputMap);
		EmpBankUpdateDao lObjEmpBankUpdateDao=new EmpBankUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTreasuryName = request.getParameter("cmbTrName");
		//String lStrTreasuryId = request.getParameter("treasuryId");
		gLogger.info("Treasury name:" +lStrTreasuryName);
		//gLogger.info("treasuryId:" +lStrTreasuryId);
		List<ComboValuesVO> subTreasury=lObjEmpBankUpdateDao.getsubTreasuryForTreasury(lStrTreasuryName);
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
	
	
	public ResultObject getDDOFromSubTreasury (Map<String, Object> inputMap) throws Exception 
	{
		gLogger.info("Inside getDDOFromSubTreasury");
		setSessionInfo(inputMap);
		EmpBankUpdateDao lObjEmpBankUpdateDao=new EmpBankUpdateDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrSubTreasuryName = request.getParameter("cmbSubTreasury");
		//String lStrTreasuryId = request.getParameter("treasuryId");
		gLogger.info("Treasury name:" +lStrSubTreasuryName);
		//gLogger.info("treasuryId:" +lStrTreasuryId);
		List<ComboValuesVO> DDOList=lObjEmpBankUpdateDao.getDDOForSubTreasury(lStrSubTreasuryName);
		gLogger.info("size" +DDOList.size());
		gLogger.info("Outside getDDOFromSubTreasury");
		inputMap.put("subTreasury", DDOList);
		gLogger.info("AJAXXXXX");
		gLogger.info("size of DDOList:" + DDOList.size());
		String ajaxResult= null;
		List<ComboValuesVO> finalDDOList = new ArrayList<ComboValuesVO>();
		Map result = new HashMap();
		ComboValuesVO cmbVO = new ComboValuesVO();
		cmbVO.setId("-1");
		cmbVO.setDesc("-- Select --");
		finalDDOList.add(cmbVO);

		if (DDOList != null && DDOList.size() > 0)
		{
			Iterator IT = DDOList.iterator();

			cmbVO = new ComboValuesVO();
			Object[] lObj = null;
			while (IT.hasNext())
			{
				cmbVO = new ComboValuesVO();
				lObj = (Object[]) IT.next();
				cmbVO.setId(lObj[0].toString());
				//cmbVO.setDesc(lObj[1].toString());
				//cmbVO.setDesc("<![CDATA[( "+lObj[0].toString()+" )"+lObj[1].toString()+"]]>");
				cmbVO.setDesc("<![CDATA["+lObj[1].toString()+"("+lObj[0].toString()+")"+"]]>");
				finalDDOList.add(cmbVO);
			}
		}
		gLogger.info("AJAXXXXX");		
		
		ajaxResult = new AjaxXmlBuilder().addItems(finalDDOList, "desc", "id").toString();
		
		gLogger.info("ajax builder created");
		gLogger.info("Ajax result:" +ajaxResult);	
		inputMap.put("ajaxKey", ajaxResult);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		
	
		return resObj;
	}
	
	
	public ResultObject getBulkEmplForDDO (Map inputMap) throws Exception
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
			EmpBankUpdateDao lObjEmpBankUpdateDao=new EmpBankUpdateDaoImpl(MstEmp.class, serv.getSessionFactory());

			String empIds=null;
			String cmbBank=null;
			String cmbBranch=null;
			String accountNumber=null;
			String savedFlag="N";
			String lStrDDO = request.getParameter("DDOCode");
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
					lObjEmpBankUpdateDao.updateBankDetails(lstrEmpIDs[lInt],bankArr[lInt],lstrNewBasic[lInt],accntNo[lInt]);
					savedFlag="Y";
				}
				
			}

			
			String lStrTreasuryCode = request.getParameter("TreasuryCode");
			String lStrSubTreasuryCode = request.getParameter("SubTrCode");
			
			List subTreasury=lObjEmpBankUpdateDao.getsubTreasuryForTreasury(lStrTreasuryCode);
			List<ComboValuesVO> idDescSubTrsryList = getIdDescList(subTreasury);
			
			List DDOList=lObjEmpBankUpdateDao.getDDOForSubTreasury(lStrSubTreasuryCode);
			List<ComboValuesVO> idDescDDOList = getIdDescList(DDOList);
			
			gLogger.info("hi "+lStrDDO);
			List treasuries = null;
			empList = lObjEmpBankUpdateDao.getAllDcpsEmployeesZPForBankUpdate(lStrDDO);
			treasuries = lObjEmpBankUpdateDao.getTreasury();
			List<ComboValuesVO> idDescTrsryList = getIdDescList(treasuries);
			inputMap.put("treasuries", idDescTrsryList);
			inputMap.put("subTreasuryNames", idDescSubTrsryList);
			inputMap.put("DDONames", idDescDDOList);
			
			inputMap.put("selectedTreasy", lStrTreasuryCode);
			inputMap.put("selectedSubTreasuryNames", lStrSubTreasuryCode);
			inputMap.put("selectedDDONames", lStrDDO);
			//added by roshan for performance tuning
			
			inputMap.put("empList", empList);
			//ended by roshan for performance tuning
			
			inputMap.put("EditForm", "N");
			
			
			
			// Get the Bank Names
			List lLstBankNames = lObjEmpBankUpdateDao.getBankNames();
			inputMap.put("BANKNAMES", lLstBankNames);
			lLstBankNames=null;
			inputMap.put("savedFlag",savedFlag);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("bulkBankUpdate");
			
			

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
	
	public ResultObject getBranchList(Map<String, Object> lMapInputMap) {
		gLogger.info("hiii i m for branch name");

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
			EmpBankUpdateDao lObjEmpBankUpdateDao=new EmpBankUpdateDaoImpl(MstEmp.class, serv.getSessionFactory());
			cmbBank = StringUtility.getParameter("cmbBank", request).trim();
			gLogger.info("hhi bank name  is"+cmbBank);
			
			if((cmbBank!=null) && (cmbBank!="") && (Long.parseLong(cmbBank)!=-1)){
				
				branchList=lObjEmpBankUpdateDao.getBranchList(cmbBank);
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
	
}