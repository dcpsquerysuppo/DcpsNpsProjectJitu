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

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 23, 2011
 */
public class CadreMasterVOGeneratorImpl extends ServiceImpl implements CadreMasterVOGenerator {
	
	Log gLogger = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DcpsCadreMasterVOGenerator#generateMap(java.
	 * util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		DcpsCadreMst DcpsCadreMstVO = null;
		try {
			DcpsCadreMstVO = generateDcpsCadreMstVO(inputMap);
			inputMap.put("DcpsCadreMstVO", DcpsCadreMstVO);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			e.printStackTrace();
			gLogger.info("Error is  " + e);
		}

		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DcpsCadreMasterVOGenerator#generateDcpsCadreMstVO
	 * (java.util.Map)
	 */
	public DcpsCadreMst generateDcpsCadreMstVO(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		DcpsCadreMst DcpsCadreMstVO = new DcpsCadreMst();

		try {

			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);

			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			Long lLngCadreMstId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_cadre", inputMap);

			String lGroup = StringUtility.getParameter("cmbGroup", request).trim();
			String lCadreCode = StringUtility.getParameter("txtCadreCode", request).trim();
			String lCadreName = StringUtility.getParameter("txtCadreDesc", request).trim();
			String lMinisterial = StringUtility.getParameter("radioMinisterial", request).trim();
			String lSuperAnuAge = StringUtility.getParameter("txtSuperAnnuAge", request).trim();
			String lCadreCntrl = StringUtility.getParameter("radioCadreControlDept", request).trim();
			String lCadreCntrlDeptId = StringUtility.getParameter("cmbCntrlFieldDept", request).trim();

			String lFieldDepartment = null;

			if (lCadreCntrl.equals("Y")) {
				lFieldDepartment = StringUtility.getParameter("cmbFieldDepartment", request).trim();
			} else if (lCadreCntrl.equals("N")) {
				lFieldDepartment = StringUtility.getParameter("cmbCntrlFieldDept", request).trim();
			}
			
			if(!"".equals(lCadreCntrlDeptId) && !"-1".equals(lCadreCntrlDeptId))
			{
				DcpsCadreMstVO.setCntrlFieldDeptId(Long.parseLong(lCadreCntrlDeptId));
			}
			DcpsCadreMstVO.setCadreId(lLngCadreMstId);
			DcpsCadreMstVO.setFieldDeptId(Long.parseLong(lFieldDepartment));
			DcpsCadreMstVO.setGroupId(lGroup);
			if(!lCadreCode.equalsIgnoreCase("") && lCadreCode!=null)
			{
				DcpsCadreMstVO.setCadreCode(Long.valueOf(lCadreCode.trim()));
			}
			DcpsCadreMstVO.setCadreName(lCadreName);
			DcpsCadreMstVO.setMinisterialFlag(lMinisterial);
			DcpsCadreMstVO.setSuperAntunAge(Long.parseLong(lSuperAnuAge));
			DcpsCadreMstVO.setCadreCntrlFlag(lCadreCntrl);
			DcpsCadreMstVO.setLangId(LangId);
			DcpsCadreMstVO.setLocId(LocId);
			DcpsCadreMstVO.setDbId(DbId);
			DcpsCadreMstVO.setCreatedPostId(CreatedPostId);
			DcpsCadreMstVO.setCreatedUserId(CreatedUserId);
			DcpsCadreMstVO.setCreatedDate(CreatedDate);
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Error is  " + e);
		}
		return DcpsCadreMstVO;
	}

}
