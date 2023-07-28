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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.GetGPFSlipDAOImpl;
import com.tcs.sgv.eis.dao.BankDetailDAOImpl;
import com.tcs.sgv.eis.valueobject.HrEisBankDtls;

public class GetGPFSlipServiceImpl extends ServiceImpl implements GetGPFSlipService{
	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

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
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/gpf/gpfPayslipLabels");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");

			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();

		} catch (Exception e) {

		}

	}

	
	public ResultObject getGPFSlip(Map objectArgs)
	throws Exception
	{
		gLogger.info("inside getGPFSlip");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		
		try {
			setSessionInfo(objectArgs);
			
			

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		
		GetGPFSlipDAOImpl gpfSlipDAO = new GetGPFSlipDAOImpl(HrEisBankDtls.class,serv.getSessionFactory());
		List yearList = gpfSlipDAO.getYearList();		

		List finalList = getIdDescList(yearList);
		objectArgs.put("yearList", finalList);
		resObj.setResultValue(objectArgs);
		resObj.setViewName("GPFSlipRequest");
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
				//cmbVO.setDesc(lObj[0].toString()+"("+lObj[1].toString()+")");
				cmbVO.setDesc(lObj[0].toString());
				finalList.add(cmbVO);
			}
		}
		return finalList;
	}
	
}
