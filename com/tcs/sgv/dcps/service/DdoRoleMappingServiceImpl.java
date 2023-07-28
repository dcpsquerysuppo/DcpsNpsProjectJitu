package com.tcs.sgv.dcps.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnLanguageMstDao;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.AttachmentHelper;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoOfficeDAOImpl;
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAO;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.HrPaySubstituteEmpMpg;
import com.tcs.sgv.dcps.valueobject.HstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.dcps.valueobject.RltDdoAsst;
import com.tcs.sgv.eis.dao.CmnlookupMstDAOImpl;
import com.tcs.sgv.eis.dao.HrPayOfficePostMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadPostmpgDAOImpl;
import com.tcs.sgv.eis.dao.OtherDetailDAOImpl;
import com.tcs.sgv.eis.dao.PsrPostMpgDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.eis.valueobject.HrEisOtherDtls;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;
import com.tcs.sgv.eis.valueobject.HrPayOfficepostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadPostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderMst;
import com.tcs.sgv.eis.valueobject.HrPayPsrPostMpg;
import com.tcs.sgv.ess.dao.OrgDesignationMstDao;
import com.tcs.sgv.ess.dao.OrgDesignationMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDao;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;
import com.tcs.sgv.gpf.dao.AGGPFSeriesDAO;
import com.tcs.sgv.gpf.dao.AGGPFSeriesDAOImpl;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;


public class DdoRoleMappingServiceImpl extends ServiceImpl 
 {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

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

	String subjectName = null;
	
	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {

		}

	}
public ResultObject loadddorolemapping(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;

		try {

			setSessionInfo(inputMap);

			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
					.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			
			lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);

			List AllFormsList = lObjNewRegDdoDAO.getFormListForDDO(lStrDdoCode);
			inputMap.put("AllFormsList", AllFormsList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("loadDDOMapping");

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	


	

}