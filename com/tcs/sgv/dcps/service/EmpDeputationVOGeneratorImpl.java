/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.GpfLnaDashboardDao;
import com.tcs.sgv.common.dao.GpfLnaDashboardDaoImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.TreasuryDAO;
import com.tcs.sgv.dcps.dao.TreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.HstEmpDeputation;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 25, 2011
 */
public class EmpDeputationVOGeneratorImpl extends ServiceImpl implements
		EmpDeputationVOGenertor {
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

	private HttpSession session = null; /* SESSION */

	// private final ResourceBundle gObjRsrcBndle = ResourceBundle
	// .getBundle("resources/pensionproc/PensionCaseConstants");

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		session = request.getSession();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.dcpsEmpDeputationVOGenertor#generateMap(java
	 * .util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<HstEmpDeputation> lLstHstEmpDeputationVO = null;
		try {

			setSessionInfo(inputMap);

			lLstHstEmpDeputationVO = new ArrayList<HstEmpDeputation>();
			lLstHstEmpDeputationVO = generateHstEmpDeputationVO(inputMap);

			inputMap.put("lLstHstEmpDeputationVO", lLstHstEmpDeputationVO);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			gLogger.info("Error is  " + e);
		}

		return objRes;
	}

	public List<HstEmpDeputation> generateHstEmpDeputationVO(Map inputMap) {

		List<HstEmpDeputation> lLstHstEmpDeputationVO = new ArrayList<HstEmpDeputation>();
		HstEmpDeputation lObjHstEmpDeputationVO = null;
		Date[] lDateArrAttachDates = null;
		Date[] lDateArrDetachDates = null;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactorySlave());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String lStrHidString = StringUtility.getParameter("hidString",
					request).trim();
			inputMap.put("hidString", lStrHidString);

			String lStrEmpIds = StringUtility.getParameter("Emp_Id", request).trim();
			String[] lArrStrEmpIds = lStrEmpIds.split("~");
			Long[] lLongArrdcpsEmpIds = new Long[lArrStrEmpIds.length];
			MstEmp[] lListMstEmps = new MstEmp[lArrStrEmpIds.length];
			for (Integer lInt = 0; lInt < lArrStrEmpIds.length; lInt++) {
				lLongArrdcpsEmpIds[lInt] = Long.valueOf(lArrStrEmpIds[lInt]
						.trim());
				lListMstEmps[lInt] = (MstEmp) lObjNewRegDdoDAO
						.read(lLongArrdcpsEmpIds[lInt]);
			}

			String lStrOfficeCode = StringUtility.getParameter("officeCodes",
					request).trim();
			String[] lArrOfficeCode = lStrOfficeCode.split("~");

			if (lStrHidString.equalsIgnoreCase("Attach")) {
				String lStrAttachDates = StringUtility.getParameter(
						"attachDates", request).trim();
				String[] lStrArrAttachDates = lStrAttachDates.split("~");

				lDateArrAttachDates = new Date[lStrArrAttachDates.length];

				for (Integer lInt = 0; lInt < lStrArrAttachDates.length; lInt++) {
					lDateArrAttachDates[lInt] = sdf
							.parse(lStrArrAttachDates[lInt].trim());
					
					String lStrDetachDates = StringUtility.getParameter(
							"dettachDates", request).trim();
					String[] lStrArrDetachDates = lStrDetachDates.split("~");

					lDateArrDetachDates = new Date[lStrArrDetachDates.length];

					for (Integer i = 0; i < lStrArrDetachDates.length; i++) {
						
						if(lStrArrDetachDates[lInt].equals("")){
							lDateArrDetachDates[lInt]=null;
							}else{
								
								lDateArrDetachDates[lInt] = sdf
								.parse(lStrArrDetachDates[lInt].trim());
							}
						
						
						//lDateArrDetachDates[i] = sdf
							//	.parse(lStrArrDetachDates[i].trim());
				}
			}

			if (lStrHidString.equalsIgnoreCase("Detach")) {
				String lStrDetachDates = StringUtility.getParameter(
						"detachDates", request).trim();
				String[] lStrArrDetachDates = lStrDetachDates.split("~");

				lDateArrDetachDates = new Date[lStrArrDetachDates.length];

				for (Integer lInt = 0; lInt < lStrArrDetachDates.length; lInt++) {
					if(lStrArrDetachDates[lInt] != " "){
					lDateArrDetachDates[lInt] = sdf
							.parse(lStrArrDetachDates[lInt].trim());
					}else{
						lDateArrDetachDates[lInt]=null;
					}
				}
			}

			/*
			String lStrRemarks = StringUtility.getParameter("remarks", request).trim();
			String[] lStrArrRemarks = null;
			
			if (lStrHidString.equalsIgnoreCase("Attach")) {
				lStrArrRemarks = lStrRemarks.split("~");
			}

			String lStrReasons = StringUtility.getParameter("cmbReasons",request).trim();
			String[] lStrArrReason = null;
			
			if (lStrHidString.equalsIgnoreCase("Attach")) {
				lStrArrReason = lStrReasons.split("~");
			}
			
			String lStrRemarksDetach = StringUtility.getParameter("remarks", request).trim();
			String[] lStrArrRemarksDetach  = null;
			
			if (lStrHidString.equalsIgnoreCase("Detach")) {
				lStrArrRemarksDetach = lStrRemarksDetach.split("~");
			}

			String lStrReasonsDetach = StringUtility.getParameter("cmbReasons",request).trim();
			String[] lStrArrReasonDetach = null;
			
			if (lStrHidString.equalsIgnoreCase("Detach")) {
				lStrArrReasonDetach = lStrReasonsDetach.split("~");
			}
			
			*/

			for (Integer lIntCnt = 0; lIntCnt < lLongArrdcpsEmpIds.length; lIntCnt++) {
				lObjHstEmpDeputationVO = new HstEmpDeputation();
				lObjHstEmpDeputationVO.setOfficeCode(lArrOfficeCode[lIntCnt]
						.trim());
				lObjHstEmpDeputationVO.setDcpsEmpId(lListMstEmps[lIntCnt]);

				if (lDateArrAttachDates != null) {
					lObjHstEmpDeputationVO
							.setAttachDate(lDateArrAttachDates[lIntCnt]);
				}
				if (lDateArrDetachDates != null) {
					lObjHstEmpDeputationVO
							.setDetachDate(lDateArrDetachDates[lIntCnt]);
				}
				
				/*
				
				if (lStrHidString.equalsIgnoreCase("Attach")) 
				{
					lObjHstEmpDeputationVO.setRemarks(lStrArrRemarks[lIntCnt].trim());
				}
				if (lStrHidString.equalsIgnoreCase("Attach"))
				{
					lObjHstEmpDeputationVO.setReason(lStrArrReason[lIntCnt].trim());
				}
				
				if (lStrHidString.equalsIgnoreCase("Detach")) 
				{
					lObjHstEmpDeputationVO.setRemarksDetach(lStrArrRemarksDetach[lIntCnt].trim());
				}
				if (lStrHidString.equalsIgnoreCase("Detach"))
				{
					lObjHstEmpDeputationVO.setReasonDetach(lStrArrReasonDetach[lIntCnt].trim());
				}
				*/
				
				/////$t IRRI 23-09-2020
				TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(MstDcpsContriVoucherDtls.class,serv.getSessionFactory());
				GpfLnaDashboardDao lObjGpfLnaDashboardDao = new GpfLnaDashboardDaoImpl(null, serv.getSessionFactory());
				String lStrUserType = lObjGpfLnaDashboardDao.getUserType(gLngUserId);//lStrUserType=Treasury
			    this.gLogger.info("lStrUserType" + lStrUserType);
				
				if (lStrUserType.equals("EmployeeSelfService")||lStrUserType.equals("DDO Assistant")) {
					gStrLocId = ((TreasuryDAOImpl) lObjTreasuryDAO).getTreasuryCodeForAst(gStrLocId.toString());
				}
				////$t
				
				lObjHstEmpDeputationVO.setLocId(Long.valueOf(gStrLocId));
				lObjHstEmpDeputationVO.setDbId(gLngDBId);
				lObjHstEmpDeputationVO.setCreatedUserId(gLngUserId);
				lObjHstEmpDeputationVO.setCreatedPostId(gLngPostId);
				lObjHstEmpDeputationVO.setCreatedDate(gDateDBDate);

				lLstHstEmpDeputationVO.add(lObjHstEmpDeputationVO);

			}

		} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return lLstHstEmpDeputationVO;
	}

}
