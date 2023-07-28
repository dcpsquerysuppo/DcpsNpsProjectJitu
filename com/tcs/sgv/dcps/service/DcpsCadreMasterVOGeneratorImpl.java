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
public class DcpsCadreMasterVOGeneratorImpl extends ServiceImpl implements DcpsCadreMasterVOGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DcpsCadreMasterVOGenerator#generateMap(java.
	 * util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		//System.out.println("in generateMap of DcpsCadreMasterVOGenerator");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		DcpsCadreMst DcpsCadreMstVO = null;
		try {
			//System.out.println("inside generator");
			DcpsCadreMstVO = generateDcpsCadreMstVO(inputMap);
			inputMap.put("DcpsCadreMstVO", DcpsCadreMstVO);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			e.printStackTrace();
		}
		//System.out.println("exit generateMap of DcpsCadreMasterVOGenerator");
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
			//System.out.println("In VOGEN Try");
			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Long UpdatedPostId = SessionHelper.getPostId(inputMap);
			Long UpdatedUserId = SessionHelper.getUserId(inputMap);
			Date UpdatedDate = DBUtility.getCurrentDateFromDB();
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			Long lLngCadreMstId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_cadre", inputMap);
			String lFieldDepartment = StringUtility.getParameter("cmbFieldDepartment", request);
			String lGroup = StringUtility.getParameter("cmbGroup", request);
			String lCadreCode = StringUtility.getParameter("txtCadreCode", request);
			String lCadreName = StringUtility.getParameter("txtCadreDesc", request);
			String lMinisterial = StringUtility.getParameter("radioMinisterial", request);
			String lSuperAnuAge = StringUtility.getParameter("txtSuperAnnuAge", request);
			String lCadreCntrl = StringUtility.getParameter("radioCadreControlDept", request);

			DcpsCadreMstVO.setCadreId(lLngCadreMstId);
			DcpsCadreMstVO.setFieldDeptId(Long.parseLong(lFieldDepartment));
			DcpsCadreMstVO.setGroupId(lGroup);
			if(!lCadreCode.equalsIgnoreCase("") && lCadreCode!=null)
			{
				DcpsCadreMstVO.setCadreCode(Long.valueOf(lCadreCode));
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
			DcpsCadreMstVO.setUpdatedPostId(UpdatedPostId);
			DcpsCadreMstVO.setUpdatedUserId(UpdatedUserId);
			DcpsCadreMstVO.setUpdatedDate(UpdatedDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DcpsCadreMstVO;
	}

}
