package com.tcs.sgv.dcps.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.hibernate.Session;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAO;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.dao.ViewReportDAO;
import com.tcs.sgv.dcps.dao.ViewReportDaoImpl;
import com.tcs.sgv.dcps.valueobject.HstDcpsContribution;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;



public class ViewReportServiceImpl extends ServiceImpl{

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

	public ResultObject ViewReport(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		gLogger.info("Adithya");
		String treasuryLocCode="";
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			// Gets Element Id and puts in the Map
			String hidElementId = StringUtility.getParameter("elementId",
					request);
			inputMap.put("hidElementId", hidElementId);

			/* Initializes the DAOs */

			ViewReportDAO lObjViewReportDAO=new ViewReportDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
			gLogger.info("Vishnu");	



			/* Get User(ATO or TO) and Use type */
			String lStrUserType = StringUtility.getParameter("User", request).trim();
			gLogger.info("lStrUserType is*************"+lStrUserType);
			treasuryLocCode=StringUtility.getParameter("cmbAGName", request).trim();
			if(treasuryLocCode!=null && treasuryLocCode.equals(""))
				treasuryLocCode=gStrLocationCode;
			String lStrUseType = StringUtility.getParameter("Use", request).trim();
			gLogger.info("gStrLocationCode is*************"+gStrLocationCode);
			String lStrContiType = null;

			if (!StringUtility.getParameter("Type", request).equalsIgnoreCase(
			"")
			&& StringUtility.getParameter("Type", request) != null) {
				lStrContiType = StringUtility.getParameter("Type", request);
			}

			inputMap.put("ContriType", lStrContiType);

			List treasuries = null;
			
	

			treasuries = lObjViewReportDAO
			.getAllDDOForContriForwardedToTO(treasuryLocCode,0);
			
			gLogger.info("Vivek");
			//inputMap.put("AGNAME", gStrUserId);
			inputMap.put("TREASURIES", treasuries);
			inputMap.put("locCode", treasuryLocCode);
			inputMap.put("lStrUser", lStrUserType);
			String viewName=null;
			if (StringUtility.getParameter("User", request).trim().equals("TO")){
				
				List subTraiseryList=lObjViewReportDAO.getSubTreasury(Long.parseLong(gStrLocationCode));
				inputMap.put("subTraiseryList", subTraiseryList);
				 viewName="viewReportTO";
			}

			else
				viewName="viewReport";
			resObj.setResultValue(inputMap);
			
			resObj.setViewName(viewName);
			/*
			 * Checks if request is sent by click of GO button or the page is
			 * loaded for the first time.
			 */





		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject getDdoFromTreasury(Map<String, Object> inputMap) throws Exception {

		gLogger.info("Inside getDdoFromTreasury");
		setSessionInfo(inputMap);
		ViewReportDAO lObjViewReportDAO=new ViewReportDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
				.getSessionFactory());

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTreasuryLocCode = request.getParameter("cmbTreasuryCode");
		gLogger.info("Treasury code:" +lStrTreasuryLocCode);
		List<ComboValuesVO> ddonames=lObjViewReportDAO.getTreasuryForDDO(lStrTreasuryLocCode);
		gLogger.info("size" +ddonames.size());
		gLogger.info("Outside getTreasuryForDDO");
		inputMap.put("ddonames", ddonames);
		/*List<ComboValuesVO> DDONAMES = new ArrayList<ComboValuesVO>();
			Map result = new HashMap();
			ComboValuesVO cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("Select");
			DDONAMES.add(cmbVO);

			if (ddonames != null && ddonames.size() > 0)
			{
				Iterator IT = ddonames.iterator();

				cmbVO = new ComboValuesVO();
				Object[] lObj = null;
				while (IT.hasNext())
				{
					cmbVO = new ComboValuesVO();
					lObj = (Object[]) IT.next();
					cmbVO.setId(lObj[0].toString());
					cmbVO.setDesc(lObj[1].toString());
					DDONAMES.add(cmbVO);
				}
			}*/
		gLogger.info("AJAXXXXX");
		String ajaxResult= null;

		ajaxResult = new AjaxXmlBuilder().addItems(ddonames, "desc", "id").toString();
		gLogger.info("Ajax result:" +ajaxResult);	
		inputMap.put("ajaxKey", ajaxResult);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");


		return resObj;
	}



	public ResultObject getTreasuryofAg(Map<String, Object> inputMap) throws Exception {

		gLogger.info("Inside getDdoFromTreasury");
		setSessionInfo(inputMap);
		ViewReportDAO lObjViewReportDAO=new ViewReportDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
				.getSessionFactory());

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTreasuryLocCode = request.getParameter("cmbTreasuryCode");
		gLogger.info("Treasury code:" +lStrTreasuryLocCode);
		List<ComboValuesVO> treasuryList=lObjViewReportDAO.getAllDDOForContriForwardedToTO(lStrTreasuryLocCode,1);
		inputMap.put("treasuryList", treasuryList);
		//gLogger.info("Outside getTreasuryForDDO");

		/*List<ComboValuesVO> ddonames = new ArrayList<ComboValuesVO>();
			Map result = new HashMap();
			ComboValuesVO cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("Select");
			ddonames.add(cmbVO);

			if (ddonameslist != null && ddonameslist.size() > 0)
			{
				Iterator IT = ddonames.iterator();

				cmbVO = new ComboValuesVO();
				Object[] lObj = null;
				while (IT.hasNext())
				{
					cmbVO = new ComboValuesVO();
					lObj = (Object[]) IT.next();
					cmbVO.setId(lObj[0].toString());

					cmbVO.setDesc("<![CDATA[( "+lObj[0].toString()+" )"+lObj[1].toString()+"]]>");
					ddonames.add(cmbVO);
				}
			}*/
		gLogger.info("AJAXXXXX");
		String ajaxResult= null;

		ajaxResult = new AjaxXmlBuilder().addItems(treasuryList, "desc", "id").toString();
		gLogger.info("Ajax result:" +ajaxResult);	
		inputMap.put("ajaxKey", ajaxResult);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");


		return resObj;
	}

	public ResultObject getStatusOfBill(Map<String, Object> inputMap) throws Exception {

		gLogger.info("Inside getDdoFromTreasury");
		setSessionInfo(inputMap);
		ViewReportDaoImpl lObjViewReportDAO=new ViewReportDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
				.getSessionFactory());

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String billNO = request.getParameter("billNo"); 
		String selYear=request.getParameter("selYear"); 
		String selMonth=request.getParameter("selMonth"); 
		Object status=lObjViewReportDAO.getCurrentBillStatus(billNO,selYear,selMonth);
		int flag=0;
		if(status!=null ){
			if(status.toString().equals("1"))
				flag=1;
		}
		StringBuilder lStrData = new StringBuilder();
		lStrData.append("<XMLDOC>");
		lStrData.append("<status>");
		lStrData.append(flag);
		lStrData.append("</status>");
		lStrData.append("</XMLDOC>");
		//String ajaxResult= null;
		Boolean 	lBlFlag = true;
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrData.toString()).toString();
		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");


		return resObj;
	}	

	public ResultObject loadSubTreasury(Map objectArgs) throws Exception
	{
		gLogger.info("Entering into getTaluka of ReportingDDOservice");
		ResultObject objRes = new ResultObject(ErrorConstants.ERROR);
		ServiceLocator serviceLocator = (ServiceLocator) objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		Long treasuryId = Long.valueOf(StringUtility.getParameter("treasuryId", request));

		ViewReportDaoImpl treasuryDao = new ViewReportDaoImpl(CmnLocationMst.class, serviceLocator.getSessionFactory());
		List cmnsubtreasuryMstList = null;
		cmnsubtreasuryMstList = treasuryDao.getSubTreasury(treasuryId);
		gLogger.info("cmntreasuryMstList:::" + cmnsubtreasuryMstList.size());
		List<ComboValuesVO> lLstAllDept = new ArrayList<ComboValuesVO>();
		Map result = new HashMap();
		ComboValuesVO cmbVO = new ComboValuesVO();
		cmbVO.setId("-1");
		cmbVO.setDesc("Select");
		lLstAllDept.add(cmbVO);

		if (cmnsubtreasuryMstList != null && cmnsubtreasuryMstList.size() > 0)
		{
			Iterator IT = cmnsubtreasuryMstList.iterator();

			cmbVO = new ComboValuesVO();
			Object[] lObj = null;
			while (IT.hasNext())
			{
				cmbVO = new ComboValuesVO();
				lObj = (Object[]) IT.next();
				cmbVO.setId(lObj[0].toString());
				cmbVO.setDesc("<![CDATA[("+lObj[0].toString()+") "+lObj[1].toString()+"]]>");
				lLstAllDept.add(cmbVO);
			}
		}

		String AjaxResult = new AjaxXmlBuilder().addItems(lLstAllDept, "desc", "id").toString();
		result.put("ajaxKey", AjaxResult);
		objRes.setResultCode(ErrorConstants.SUCCESS);
		objRes.setResultValue(result);
		objRes.setViewName("ajaxData");
		return objRes;
	}
	
	//aded by sunitha
	
	
	public ResultObject getDdoFromSubTreasury(Map<String, Object> inputMap) throws Exception {

		gLogger.info("Inside getDdoFromTreasury");
		setSessionInfo(inputMap);
		ViewReportDAO lObjViewReportDAO=new ViewReportDaoImpl(TrnDcpsContribution.class,serv.getSessionFactory());
		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
				.getSessionFactory());

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTreasuryLocCode = request.getParameter("cmbSubTreasuryCode");
		gLogger.info("Treasury code:" +lStrTreasuryLocCode);
		List<ComboValuesVO> ddonames=lObjViewReportDAO.getTreasuryForDDO(lStrTreasuryLocCode);
		gLogger.info("size" +ddonames.size());
		gLogger.info("Outside getTreasuryForDDO");
		inputMap.put("ddonames", ddonames);
		/*List<ComboValuesVO> DDONAMES = new ArrayList<ComboValuesVO>();
			Map result = new HashMap();
			ComboValuesVO cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("Select");
			DDONAMES.add(cmbVO);

			if (ddonames != null && ddonames.size() > 0)
			{
				Iterator IT = ddonames.iterator();

				cmbVO = new ComboValuesVO();
				Object[] lObj = null;
				while (IT.hasNext())
				{
					cmbVO = new ComboValuesVO();
					lObj = (Object[]) IT.next();
					cmbVO.setId(lObj[0].toString());
					cmbVO.setDesc(lObj[1].toString());
					DDONAMES.add(cmbVO);
				}
			}*/
		gLogger.info("AJAXXXXX");
		String ajaxResult= null;

		ajaxResult = new AjaxXmlBuilder().addItems(ddonames, "desc", "id").toString();
		gLogger.info("Ajax result:" +ajaxResult);	
		inputMap.put("ajaxKey", ajaxResult);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");


		return resObj;
	}


}
