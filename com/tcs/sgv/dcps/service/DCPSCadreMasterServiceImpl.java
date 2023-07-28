/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 23, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DCPSCadreMasterDAOImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Mar 23, 2011
 */
public class DCPSCadreMasterServiceImpl extends ServiceImpl implements DCPSCadreMasterService
{

	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.service.DCPSCadreMasterService#loadCadreMaster(java.util.Map)
	 */
	public ResultObject loadCadreMaster(Map inputMap) 
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest)inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try 
		{									
			List ParentDeptList = (List) IFMSCommonServiceImpl.getLookupValues(
					"ParentDeptList", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("ParentDeptList", ParentDeptList);
			
			List GroupList = (List) IFMSCommonServiceImpl.getLookupValues(
					"GroupList", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("Group", GroupList);		
			
			List cadreList= null;			
			DCPSCadreMasterDAOImpl DCPSCadreMasterDAOImpllObj = new DCPSCadreMasterDAOImpl(DcpsCadreMst.class,serv.getSessionFactory());
			cadreList =DCPSCadreMasterDAOImpllObj.getCadreList();
			inputMap.put("cadreList", cadreList);
		}
		catch (Exception e) 
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");			
		}
		
		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSCadreInfo");
		
		return resObj;
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.service.DCPSCadreMasterService#insertCadre(java.util.Map)
	 */
	public ResultObject insertCadre(Map inputMap) 
	{
		Log logger = LogFactory.getLog(getClass());
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		
  
		try
		{			
				DCPSCadreMasterDAOImpl DCPSCadreMasterDAOImpllObj = new DCPSCadreMasterDAOImpl(DcpsCadreMst.class,serv.getSessionFactory());	
				DcpsCadreMst DcpsDcpsCadreMstVO = (DcpsCadreMst) inputMap.get("DcpsCadreMstVO");							
				DCPSCadreMasterDAOImpllObj.create(DcpsDcpsCadreMstVO);																			
		}
		catch(Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in getDigiSig " + e, e);
		}		
		
		inputMap.put("ajaxKey", "Success");
		resObj.setViewName("ajaxData");
		resObj.setResultValue(inputMap);	
		return resObj;
	}

	

}
