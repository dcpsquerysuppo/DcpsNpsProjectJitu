/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 16, 2011		Vihan Khatri								
 *******************************************************************************
 */

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 16, 2011
 */
package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSNomineeDtlsVOGenerator
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.SixPCArrearsDAO;
import com.tcs.sgv.dcps.dao.SixPCArrearsDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;

public class SixPCArrearsVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	/*
	 * @Description : Method to generate VO For DcpsEmpNmnMst.
	 * 
	 * @Input : Map : inputMap
	 * 
	 * @Output : ResultObject : ResultObject
	 */
	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	String gStrAuditorFlag = null;

	String gStrStatus = null;

	Long gLngAuditPostId = null;

	Long gLngAuditUserId = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gDateDBDate = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private void setSessionInfo(Map inputMap) {
		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");

		List<MstSixPCArrears> lListMstSixPCArrears = null;

		try {
			setSessionInfo(inputMap);
			// inputMap.put("DCPSNomineeDtls", lObjNomineeDtls);
			lListMstSixPCArrears = generateSixPCArrearsVOList(inputMap);
			inputMap.put("lListMstSixPCArrears", lListMstSixPCArrears);
			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
		}
		return objRes;
	}

	/*
	 * @Description : Method to generate VO For DCPSNomineeDtlsVOGenerator.
	 * 
	 * @Input : Map : inputMap
	 * 
	 * @Output : ResultObject : DcpsEmpNmnMst[]
	 */

	public List<MstSixPCArrears> generateSixPCArrearsVOList(Map inputMap)
			throws Exception {

		List<MstSixPCArrears> lListMstSixPCArrears = new ArrayList<MstSixPCArrears>();
		MstSixPCArrears lObjMstSixPCArrearsVO = null;
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		Long lLngDcpsEmpId = null;
		MstEmp lObjMstEmpVO = null;
		Date lDtDOJ = null;
		Long lLngPayCmsn = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat(
				"dd/MM/yyyy");

		Date lDtCnstDate = lObjSimpleDateFormat.parse("31/03/2009");
		
		String lStrEmpId = null;
		
		try {
			setSessionInfo(inputMap);
			
			lStrEmpId = StringUtility.getParameter("Emp_Id", request) ;
			
				if (!"".equals(lStrEmpId)) {
	
					lStrEmpId = StringUtility
							.getParameter("Emp_Id", request).toString().trim();
					String[] lArrEmpId = lStrEmpId.split("~");
					String lStrDOJ = StringUtility.getParameter("DOJ", request)
							.toString().trim();
					String[] lStrArrDOJ = lStrDOJ.split("~");
					String lStrPayCmsn = StringUtility.getParameter("PayCmsn",
							request).toString().trim();
					String[] lStrArrPayCmsn = lStrPayCmsn.split("~");
	
					lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(MstEmp.class,
							serv.getSessionFactory());
					for (int lIntCnt = 0; lIntCnt < lArrEmpId.length; lIntCnt++) {
	
						lObjMstSixPCArrearsVO = new MstSixPCArrears();
						if (lStrArrDOJ[lIntCnt] != null) {
							lDtDOJ = lObjSimpleDateFormat
									.parse(lStrArrDOJ[lIntCnt]);
						} else {
							lDtDOJ = null;
						}
						
						lLngPayCmsn = Long.valueOf(lStrArrPayCmsn[lIntCnt]);
						if (lLngPayCmsn != 700016L && lLngPayCmsn != 700339L ) {
							continue;
						}
						if(lLngPayCmsn == 700016L)
						{
							if (lDtDOJ.after(lDtCnstDate)) {
								continue;
							}
						}
						lLngDcpsEmpId = Long.valueOf(lArrEmpId[lIntCnt]);
						lObjMstEmpVO = new MstEmp();
						lObjMstEmpVO = (MstEmp) lObjSixPCArrearsDAO
								.read(lLngDcpsEmpId);
	
						lObjMstSixPCArrearsVO.setDcpsEmpId(lObjMstEmpVO);
						lObjMstSixPCArrearsVO.setTotalAmount(0L);
						lObjMstSixPCArrearsVO.setAmountPaid(0L);
						lObjMstSixPCArrearsVO.setLangId(gLngLangId);
						lObjMstSixPCArrearsVO.setLocId(Long.valueOf(gStrLocId));
						lObjMstSixPCArrearsVO.setDbId(gLngDBId);
						lObjMstSixPCArrearsVO.setCreatedUserId(gLngUserId);
						lObjMstSixPCArrearsVO.setCreatedPostId(gLngPostId);
						lObjMstSixPCArrearsVO.setCreatedDate(gDateDBDate);
						lObjMstSixPCArrearsVO.setUpdatedDate(DBUtility
								.getCurrentDateFromDB());
						lObjMstSixPCArrearsVO.setUpdatedPostId(gLngPostId);
						lObjMstSixPCArrearsVO.setUpdatedUserId(gLngUserId);
						lObjMstSixPCArrearsVO.setStatusFlag('D');
						lObjMstSixPCArrearsVO.setRemarks(null);
						lObjMstSixPCArrearsVO.setNoOfInstallment(5);
	
						lListMstSixPCArrears.add(lObjMstSixPCArrearsVO);
	
					}
				}

			
		} catch (Exception e) {
			gLogger.error(
					"Error in generateSixPCArrearsVOList method is :" + e, e);
			e.printStackTrace();
		}

		return lListMstSixPCArrears;

	}
}
