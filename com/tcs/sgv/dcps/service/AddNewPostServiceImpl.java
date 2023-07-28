package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLanguageMstDao;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.AdminPostDtlsDAO;
import com.tcs.sgv.dcps.dao.AdminPostDtlsDAOImpl;
import com.tcs.sgv.dcps.dao.DdoInfoDAO;
import com.tcs.sgv.dcps.dao.DdoInfoDAOImpl;
import com.tcs.sgv.dcps.dao.DdoOfficeDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;
import com.tcs.sgv.eis.dao.CmnlookupMstDAOImpl;
import com.tcs.sgv.eis.dao.GradDesgScaleMapDAO;
import com.tcs.sgv.eis.dao.GradeMasterDAO;
import com.tcs.sgv.eis.dao.HrPayOfficePostMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadMpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderHeadPostmpgDAOImpl;
import com.tcs.sgv.eis.dao.OrderMstDAOImpl;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.dao.PsrPostMpgDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.eis.valueobject.HrEisGdMpg;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;
import com.tcs.sgv.eis.valueobject.HrEisSgdMpg;
import com.tcs.sgv.eis.valueobject.HrPayOfficepostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderHeadPostMpg;
import com.tcs.sgv.eis.valueobject.HrPayOrderMst;
import com.tcs.sgv.eis.valueobject.HrPayPaybill;
import com.tcs.sgv.eis.valueobject.HrPayPsrPostMpg;
import com.tcs.sgv.eis.valueobject.MstPayrollDesignationMst;
import com.tcs.sgv.ess.dao.OrgDesignationMstDao;
import com.tcs.sgv.ess.dao.OrgDesignationMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDao;
import com.tcs.sgv.ess.dao.OrgPostDetailsRltDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.ess.valueobject.OrgGradeMst;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.user.dao.AdminOrgPostDtlDao;
import com.tcs.sgv.user.dao.AdminOrgPostDtlDaoImpl;
import com.tcs.sgv.user.valueobject.UserPostCustomVO;

public class AddNewPostServiceImpl extends ServiceImpl implements  AddNewPostService{
	Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private Date gDtCurDate = null; /* CURRENT DATE */
	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;
	
	/* Global Variable for LangId */
	Long gLngLangId = null;
	
	Long gLngLocationCode = null;
	
	Integer lIntSubtypeSelection = 0;
	
	ResourceBundle constantsBundle;
	public final int DEPT_ID;
	Log logger;
	int msg;
	public static final String adminCreatePostDtlViewName = "adminCreatePostDtls";
	public static final String showAdminPostDtlViewName = "showAdminPostDtls";
	public static final String postTypeLookupName = "PostType";
	public static final long engLangId = 1L;
	public static final long guLangId = 2L;

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngLocationCode = Long.parseLong(gStrLocationCode);
		gLngLangId = SessionHelper.getLangId(inputMap);

	}

	public AddNewPostServiceImpl() {
		constantsBundle = ResourceBundle.getBundle("resources.Payroll");
		DEPT_ID = Integer.parseInt(constantsBundle.getString("GAD"));
		logger = LogFactory.getLog(getClass());
		msg = 0;
	}
	
	public ResultObject showAdminOrgPostDtl(Map<String, Object> objectArgs) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		setSessionInfo(objectArgs);
		try {			
			Map loginMap = (Map) (Map) objectArgs.get("baseLoginMap");
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			Long langId = Long.parseLong(loginMap.get("langId").toString());
			String lStrDDOCode = StringUtility.getParameter("DDO", request);
			String lStrFieldDeptCode = StringUtility.getParameter("FD", request);
			Long locId = 0L;
			AdminPostDtlsDAO lObjPostDtlsDAO = new AdminPostDtlsDAOImpl(null, serv.getSessionFactory());
			
			String lStrLocCode = lObjPostDtlsDAO.getLocationCodeForDDO(lStrDDOCode);
			if(lStrLocCode != null && !lStrLocCode.equals(""))
				locId = Long.parseLong(lStrLocCode); 
		
			Map voToService = (Map)objectArgs.get("voToServiceMap");
			List fieldCaptionList = new ArrayList();
			List tempList = new ArrayList();
			tempList.add("Post Name");
			fieldCaptionList.add(tempList);
			
			List<ComboValuesVO> lLstFieldDept = lObjPostDtlsDAO.getFieldDeptFromAdminDeptCode(gLngLocationCode);
			//List<ComboValuesVO> lLstDdo = lObjPostDtlsDAO.getDdoListFromFieldDept(gLngLocationCode.toString());
			List<ComboValuesVO> lLstDdo = lObjPostDtlsDAO.getDdoListFromFieldDept(lStrFieldDeptCode);
			objectArgs.put("lLstFieldDept", lLstFieldDept);
			objectArgs.put("lLstDdo", lLstDdo);
			
			objectArgs.put("DDOCode", lStrDDOCode);
			objectArgs.put("FieldDeptCode", lStrFieldDeptCode);
			
			String empName = "";			
	
			String lPostname="";
			String Psr="";
			String Bill="";
			String Dsgn="";
			String empId = "";
			String lStrGRNo = "";
			String lStrPostStartDate = "";
			String lStrPostEndDate = "";
			String lStrGRDate = "";
			if(voToService != null)
			{
				if(voToService.get("Post")!=null)
					lPostname=(String)voToService.get("Post").toString();
				if(voToService.get("PsrNo")!=null)
					Psr=(String)voToService.get("PsrNo").toString();
				if(voToService.get("BillNo")!=null)
					Bill=(String)voToService.get("BillNo").toString();
				if(voToService.get("Dsgn")!=null)
					Dsgn=(String)voToService.get("Dsgn").toString();
				if(voToService.get("empId")!=null)
					empId=(String)voToService.get("empId").toString();
			}
			
			UserPostCustomVO customVO = new UserPostCustomVO();	
			String PostName = "";
			String	dsgn = "";
			if(lPostname != null && !lPostname.equals(""))
				PostName = lPostname.trim();		
			if(Dsgn != null && !Dsgn.equals(""))
				dsgn = Dsgn.trim();
			List postNameList = new ArrayList();

			if(empId != null && !empId.equals("")){
				postNameList = lObjPostDtlsDAO.getPostNameForDisplayThruEmpId(locId, empId, langId);
			}else{
				postNameList = lObjPostDtlsDAO.getPostNameForDisplay(locId,PostName,Psr, Bill, dsgn); 
			}
			
			List<UserPostCustomVO> post = new ArrayList<UserPostCustomVO>();			
			String  empFullName = "";
			String postType="";
			String postLookupId="";
			String permenantlookupId="10001198129";
			String temparerylookupId="10001198130";
			
			if( postNameList!=null )
			{
				for (int i = 0; i < postNameList.size(); i++) 
				{
					customVO = new UserPostCustomVO();

					Object rowList[] = (Object[]) postNameList.get(i);

					String postName = rowList[0].toString();
					
					if(rowList[6] != null && !(rowList[6].toString().trim()).equals(""))
					{	
						postLookupId = rowList[6].toString();
					}
					if(postLookupId.equals(permenantlookupId))
					{
						postName =postName.concat("P");
					}
					else if(postLookupId.equals(temparerylookupId))
					{
						postName =postName.concat("T");
					}					
					customVO.setPostname(postName);
					
					long postId = Long.parseLong(rowList[1].toString());
					customVO.setPostId(postId);	
					
					if(rowList[2] != null && !(rowList[2].toString().trim()).equals("")){	
						empFullName = rowList[2].toString();
					} else{
						empFullName = "VACANT";
					}
					customVO.setEmpFullName(empFullName);

					String dsgnName = rowList[3].toString();
					if(rowList[3] != null && !(rowList[3].toString().trim()).equals("")){
						customVO.setDsgnname(dsgnName);
					}
					String BillNo = " ";
					String PsrNo = " ";

					if(rowList[4]!= null){
						PsrNo = rowList[4].toString();
					}
					customVO.setPsrNo(PsrNo);

					if(rowList[5] != null){
						BillNo = rowList[5].toString();
					}
					customVO.setBillNo(BillNo);	

					if(rowList[7] != null && !(rowList[7].toString().trim()).equals("")){	
						postType = rowList[7].toString();
					} else{
						postType = "VACANT";
					}					
					customVO.setPostType(postType);
					
					if(rowList[8] != null && !(rowList[8].toString().trim()).equals("")){	
						lStrPostStartDate = lObjDateFormat.format(rowList[8]);
					} else{
						lStrPostStartDate = "";
					}					
					customVO.setStartDate(lStrPostStartDate);
					
					if(rowList[9] != null && !(rowList[9].toString().trim()).equals("")){	
						lStrPostEndDate = lObjDateFormat.format(rowList[9]);
					} else{
						lStrPostEndDate = "";
					}					
					customVO.setEndDate(lStrPostEndDate);					
					
					if(rowList[10] != null && !(rowList[10].toString().trim()).equals("")){	
						lStrGRNo = rowList[10].toString();
						
					} else{
						lStrGRNo = "";
					}					
					customVO.setEmpFname(lStrGRNo);
					
					if(rowList[11] != null && !(rowList[11].toString().trim()).equals("")){	
						lStrGRDate = lObjDateFormat.format(rowList[11]);
					} else{
						lStrGRDate = "";
					}					
					customVO.setEmpMname(lStrGRDate);
					
					post.add(customVO);
				}//end for
			}//end if


			
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
			String TodaysDate = fmt.format(today);
		

			OrderMstDAOImpl ordermstDaoImpl = new OrderMstDAOImpl(HrPayOrderMst.class, serv.getSessionFactory());
			List orderList = ordermstDaoImpl.getAllData(locId,TodaysDate);
		
			objectArgs.put("orderList", orderList);
			objectArgs.put("empId", empId);
			objectArgs.put("empName", empName);			
			objectArgs.put("recordList", post);	
			
			
			AdminOrgPostDtlDao adminOrgPostDtlDao = new AdminOrgPostDtlDaoImpl(CmnLocationMst.class, serv.getSessionFactory());			
			PayBillDAOImpl payDAO = new PayBillDAOImpl(HrPayPaybill.class,serv.getSessionFactory());
			List<OrgDdoMst> ddoList = payDAO.getDDOCodeByLoggedInlocId(locId);
			OrgDdoMst ddoMst  = null;
			Long lLngFieldDept = 0L;
			if(ddoList!=null && ddoList.size()>0) {
				 ddoMst = ddoList.get(0);
				 lLngFieldDept = Long.parseLong(ddoMst.getHodLocCode());
			}		
			
			AdminOrgPostDtlDaoImpl adminDao = new AdminOrgPostDtlDaoImpl(MstDcpsDesignation.class, serv.getSessionFactory());
			List<OrgDesignationMst> desgList = adminDao.getActiveDesig(lLngFieldDept);
		
			objectArgs.put("Designation", desgList);
			List billList = lObjPostDtlsDAO.getAllBillsFromLocation(locId);
			objectArgs.put("billList", billList);
			
					
			objectArgs.put("fieldCaptionList", fieldCaptionList);
			resObj.setViewName("showPostDtls");
			resObj.setResultCode(0);
			resObj.setResultValue(objectArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			resObj.setThrowable(ex);
			logger.error("Admin Screen Showing all Post Error", ex);
			resObj.setResultCode(-1);
		}
		return resObj;
	}

	
	public ResultObject showNewPostForm(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {logger.info("15");
			setSessionInfo(inputMap);
			AdminPostDtlsDAO lObjOrgPostDtlDao = new AdminPostDtlsDAOImpl(null,serv.getSessionFactory());
			OrderMstDAOImpl ordermstDaoImpl = new OrderMstDAOImpl(HrPayOrderMst.class, serv.getSessionFactory());
			AdminOrgPostDtlDao adminOrgPostDtlDao = new AdminOrgPostDtlDaoImpl(CmnLocationMst.class, serv.getSessionFactory());
			AdminOrgPostDtlDaoImpl adminDao = new AdminOrgPostDtlDaoImpl(MstDcpsDesignation.class, serv.getSessionFactory());
			
			String lStrFieldDeptCode = StringUtility.getParameter("fd", request);
			String lStrDdoCode = StringUtility.getParameter("ddo", request);
			
			List<ComboValuesVO> lLstFieldDept = lObjOrgPostDtlDao.getFieldDeptFromAdminDeptCode(gLngLocationCode);
			List<ComboValuesVO> lLstDdo = null;
			
			if(!lStrFieldDeptCode.equals("")){logger.info("16");
				lLstDdo = lObjOrgPostDtlDao.getDdoListFromFieldDept(lStrFieldDeptCode);
			}			
			inputMap.put("lLstFieldDept", lLstFieldDept);
			inputMap.put("lLstDdo", lLstDdo);			
			
			if(!lStrDdoCode.equals("") && lStrDdoCode != null){
				String lStrLocationCode = lObjOrgPostDtlDao.getLocationCodeForDDO(lStrDdoCode);
				Long lLngLocId = 0L;
				Long lLngFieldId = 0L;
				
				if(!lStrLocationCode.equals("") && lStrLocationCode != null)
					lLngLocId = Long.parseLong(lStrLocationCode);
				if(!lStrFieldDeptCode.equals("") && lStrFieldDeptCode != null)
					lLngFieldId = Long.parseLong(lStrFieldDeptCode);
					
				Calendar cal = Calendar.getInstance();
				Date today = cal.getTime();
				SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
				String TodaysDate = fmt.format(today);
				
				List<OrgDesignationMst> desgList = adminDao.getActiveDsgnListOfFieldDept(lLngFieldId);
				List orderList = ordermstDaoImpl.getAllData(lLngLocId,TodaysDate);			
				List billList = lObjOrgPostDtlDao.getAllBillsFromLocation(lLngLocId);
				List officeList = adminOrgPostDtlDao.getAllOfficesFromDDO(Long.parseLong(lStrDdoCode));
				
				inputMap.put("DDOCode", lStrDdoCode);
				inputMap.put("FieldDeptCode", lStrFieldDeptCode);					
				inputMap.put("Designation", desgList);
				inputMap.put("orderList", orderList);				
				inputMap.put("Designation", desgList);
				inputMap.put("orderList", orderList);
				inputMap.put("billList", billList);
				inputMap.put("langId", gLngLangId);
				inputMap.put("officeList", officeList);
				inputMap.put("flag", "add");
			}logger.info("11");
			
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("addNewPostDtls");
		return resObj;
	}

	
	public ResultObject showAdminPostForm(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			setSessionInfo(inputMap);
			AdminPostDtlsDAO lObjOrgPostDtlDao = new AdminPostDtlsDAOImpl(null,serv.getSessionFactory());
			
			List<ComboValuesVO> lLstFieldDept = lObjOrgPostDtlDao.getFieldDeptFromAdminDeptCode(gLngLocationCode);
			
			List<ComboValuesVO> lLstDdo = new ArrayList<ComboValuesVO>();
	
			lLstDdo = lObjOrgPostDtlDao.getDdoListFromFieldDept( gStrLocationCode);
			inputMap.put("lLstFieldDept", lLstFieldDept);
			inputMap.put("lLstDdo", lLstDdo);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("AdminPostForm");
		return resObj;
	}

	
	public ResultObject getDdoListFromFieldDept(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);				
		List<ComboValuesVO> lLstDdo = new ArrayList<ComboValuesVO>();
		String strAjaxResult = null;		
		try {
			setSessionInfo(inputMap);
			AdminPostDtlsDAO lObjOrgPostDtlDao = new AdminPostDtlsDAOImpl(null,serv.getSessionFactory());
			String lStrFieldDeptCode = StringUtility.getParameter("FieldDeptCode", request).toString();

			lLstDdo = lObjOrgPostDtlDao.getDdoListFromFieldDept(lStrFieldDeptCode);
			strAjaxResult = new AjaxXmlBuilder().addItems(lLstDdo, "desc", "id").toString();
			inputMap.put("ajaxKey", strAjaxResult);
			resObj.setViewName("ajaxData");			
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	
	public ResultObject searchPostDtls(Map objectArgs) 
	{
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		setSessionInfo(objectArgs);
		try 
		{
			ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			AdminPostDtlsDAO lObjPostDtlsDAO = new AdminPostDtlsDAOImpl(null, serv.getSessionFactory());
			
			Long designationId = (StringUtility.getParameter("dsgnId",request)!=null&&!(StringUtility.getParameter("dsgnId",request).equals(""))?Long.parseLong(StringUtility.getParameter("dsgnId",request)):0);
			Long billId=0L;
			if(!StringUtility.getParameter("billGrpId", request).equals("") && StringUtility.getParameter("billGrpId", request)!=null)
				billId = Long.valueOf(StringUtility.getParameter("billGrpId", request).toString());
		
			String ddoCode = "";
			if(!StringUtility.getParameter("DdoDtl", request).equals("") && StringUtility.getParameter("DdoDtl", request)!=null)
				ddoCode = StringUtility.getParameter("DdoDtl", request);
			
			String lStrFieldDeptCode = StringUtility.getParameter("FieldDept", request);
			List<ComboValuesVO> lLstFieldDept = lObjPostDtlsDAO.getFieldDeptFromAdminDeptCode(gLngLocationCode);
			List<ComboValuesVO> lLstDdo = lObjPostDtlsDAO.getDdoListFromFieldDept(gLngLocationCode.toString());
			objectArgs.put("lLstFieldDept", lLstFieldDept);
			objectArgs.put("lLstDdo", lLstDdo);
			
			objectArgs.put("DDOCode", ddoCode);
			objectArgs.put("FieldDeptCode", lStrFieldDeptCode);
			
			String Psr="";
			String Bill="";
			String PostName = "";
			String  empFullName = "";
			String postType="";
			String	dsgn = "";
			String lStrGRNo = "";
			String lStrPostStartDate = "";
			String lStrPostEndDate = "";
			String lStrGRDate = "";
		
			DdoInfoDAO ddoInfoDAO = new DdoInfoDAOImpl(null, serv.getSessionFactory());
			OrgDdoMst lObjOrgDdoMst = ddoInfoDAO.getDdoInformation(ddoCode);			
			Long locId = Long.parseLong(lObjOrgDdoMst.getLocationCode());
						
			AdminOrgPostDtlDaoImpl adminDao = new AdminOrgPostDtlDaoImpl(MstDcpsDesignation.class, serv.getSessionFactory());
			
			List<OrgDesignationMst> desgList = adminDao.getActiveDsgnList(locId);			
			objectArgs.put("Designation", desgList);

			List billList = lObjPostDtlsDAO.getAllBillsFromLocation(locId);
			objectArgs.put("billList", billList);
			
			List postNameListWithIds = lObjPostDtlsDAO.getPostNameForDisplayWithSearch(locId, PostName, Psr, Bill, dsgn, designationId, billId);
			UserPostCustomVO customVO = new UserPostCustomVO();	
			List post = new ArrayList();	
					
						
			List postNameList = new ArrayList();
			postNameList = lObjPostDtlsDAO.getPostNameForDisplayWithSearch(locId,PostName,Psr, Bill, dsgn);
			String postLookupId="";
			String permenantlookupId="10001198129";
			String temparerylookupId="10001198130";
			
			
			if(designationId>0 || billId>0)
			{
				
				if( postNameListWithIds!=null )
				{
					for (int i = 0; i < postNameListWithIds.size(); i++) 
					{
						customVO = new UserPostCustomVO();

						Object rowList[] = (Object[]) postNameListWithIds.get(i);

						String postName = rowList[0].toString();
						//customVO.setPostname(postName);
						
						if(rowList[6] != null && !(rowList[6].toString().trim()).equals(""))
						{	
							postLookupId = rowList[6].toString();
						}
						if(postLookupId.equals(permenantlookupId))
						{
							postName =postName.concat("P");
						}
						else if(postLookupId.equals(temparerylookupId))
						{
							postName =postName.concat("T");
						}
						
						customVO.setPostname(postName);
					
						long postId = Long.parseLong(rowList[1].toString());
						customVO.setPostId(postId);	
						
						if(rowList[2] != null && !(rowList[2].toString().trim()).equals("")){	
							empFullName = rowList[2].toString();
						} else{
							empFullName = "VACANT";
						}
						customVO.setEmpFullName(empFullName);

						String dsgnName = rowList[3].toString();
						if(rowList[3] != null && !(rowList[3].toString().trim()).equals("")){
							customVO.setDsgnname(dsgnName);
						}
						String BillNo = " ";
						String PsrNo = " ";

						if(rowList[4]!= null){
							PsrNo = rowList[4].toString();
						}
						customVO.setPsrNo(PsrNo);

						if(rowList[5] != null){
							BillNo = rowList[5].toString();
						}
						customVO.setBillNo(BillNo);	
						
						
						if(rowList[7] != null && !(rowList[7].toString().trim()).equals("")){	
							postType = rowList[7].toString();
						} else{
							postType = "VACANT";
						}
						
						if(rowList[8] != null && !(rowList[8].toString().trim()).equals("")){	
							lStrPostStartDate = lObjDateFormat.format(rowList[8]);
						} else{
							lStrPostStartDate = "";
						}					
						customVO.setStartDate(lStrPostStartDate);
						
						if(rowList[9] != null && !(rowList[9].toString().trim()).equals("")){	
							lStrPostEndDate = lObjDateFormat.format(rowList[9]);
						} else{
							lStrPostEndDate = "";
						}					
						customVO.setEndDate(lStrPostEndDate);					
						
						if(rowList[10] != null && !(rowList[10].toString().trim()).equals("")){	
							lStrGRNo = rowList[10].toString();
						} else{
							lStrGRNo = "";
						}					
						customVO.setEmpFname(lStrGRNo);
						
						if(rowList[11] != null && !(rowList[11].toString().trim()).equals("")){	
							lStrGRDate = lObjDateFormat.format(rowList[11]);
						} else{
							lStrGRDate = "";
						}					
						customVO.setEmpMname(lStrGRDate);
						customVO.setPostType(postType);
						post.add(customVO);
						
					}//end for
				}
			}else{
				if( postNameList!=null )
				{
					for (int i = 0; i < postNameList.size(); i++) 
					{
						customVO = new UserPostCustomVO();

						Object rowList[] = (Object[]) postNameList.get(i);

						String postName = rowList[0].toString();
												
						if(rowList[6] != null && !(rowList[6].toString().trim()).equals(""))
						{	
							postLookupId = rowList[6].toString();
						}
						if(postLookupId.equals(permenantlookupId))
						{
							postName =postName.concat("P");
						}
						else if(postLookupId.equals(temparerylookupId))
						{
							postName =postName.concat("T");
						}
						customVO.setPostname(postName);						

						long postId = Long.parseLong(rowList[1].toString());
						customVO.setPostId(postId);	
						
						if(rowList[2] != null && !(rowList[2].toString().trim()).equals("")){	
							empFullName = rowList[2].toString();
						} else{
							empFullName = "VACANT";
						}
						customVO.setEmpFullName(empFullName);

						String dsgnName = rowList[3].toString();
						if(rowList[3] != null && !(rowList[3].toString().trim()).equals("")){
							customVO.setDsgnname(dsgnName);
						}
						String BillNo = " ";
						String PsrNo = " ";

						if(rowList[4]!= null){
							PsrNo = rowList[4].toString();
						}
						customVO.setPsrNo(PsrNo);

						if(rowList[5] != null){
							BillNo = rowList[5].toString();
						}
						customVO.setBillNo(BillNo);	
						
						
						if(rowList[7] != null && !(rowList[7].toString().trim()).equals("")){	
							postType = rowList[7].toString();
						} else{
							postType = "VACANT";
						}						
						customVO.setPostType(postType);
						
						if(rowList[8] != null && !(rowList[8].toString().trim()).equals("")){	
							lStrPostStartDate = lObjDateFormat.format(rowList[8]);
						} else{
							lStrPostStartDate = "";
						}					
						customVO.setStartDate(lStrPostStartDate);
						
						if(rowList[9] != null && !(rowList[9].toString().trim()).equals("")){	
							lStrPostEndDate = lObjDateFormat.format(rowList[9]);
						} else{
							lStrPostEndDate = "";
						}					
						customVO.setEndDate(lStrPostEndDate);					
						
						if(rowList[10] != null && !(rowList[10].toString().trim()).equals("")){	
							lStrGRNo = rowList[10].toString();
						} else{
							lStrGRNo = "";
						}					
						customVO.setEmpFname(lStrGRNo);
						
						if(rowList[11] != null && !(rowList[11].toString().trim()).equals("")){	
							lStrGRDate = lObjDateFormat.format(rowList[11]);
						} else{
							lStrGRDate = "";
						}					
						customVO.setEmpMname(lStrGRDate);
						
						post.add(customVO);						
					}//end for
				}//end if
			}
			
			
			objectArgs.put("recordList", post);			
			resObj.setViewName("showPostDtls");
			resObj.setResultCode(0);
			resObj.setResultValue(objectArgs);
		} 
		catch (Exception ex) 
		{
			resObj.setThrowable(ex);
			logger.error("Admin Screen Creating A Post Error", ex);
			resObj.setResultCode(-1);
		}

		return resObj;
	}

	public ResultObject submitAdminOrgPostDtl(Map<String, Object> objectArgs) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		setSessionInfo(objectArgs);
		try {
			logger.info("in submit..");
			Map loginMap = (Map) (Map) objectArgs.get("baseLoginMap");
			String lStrDdoCode = (String) objectArgs.get("DdoCode");			
			String lStrFieldDept = (String) objectArgs.get("FieldDept");
			
			DdoInfoDAO ddoInfoDAO = new DdoInfoDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			OrgDdoMst lObjOrgDdoMst = ddoInfoDAO.getDdoInformation(lStrDdoCode);
			
			Long locId = 0L;
			if(lObjOrgDdoMst != null)
				locId = Long.parseLong(lObjOrgDdoMst.getLocationCode());
			
			Long userID = Long.parseLong(loginMap.get("userId").toString());
			Long setPostId = Long.parseLong(loginMap.get("loggedInPost").toString());
			
			OrgUserMstDaoImpl orgUserMstDaoImpl = new OrgUserMstDaoImpl(OrgUserMst.class, serv.getSessionFactory());
			OrgUserMst orgUserMstLoggedin = (OrgUserMst) orgUserMstDaoImpl.read(Long.valueOf(userID));
			OrgPostMstDaoImpl orgPostMstDaoImpl = new OrgPostMstDaoImpl(OrgPostMst.class, serv.getSessionFactory());
			OrgPostMst orgPostMstLoggedIn = (OrgPostMst) orgPostMstDaoImpl.read(Long.valueOf(setPostId));
			logger.info("1");
			Long officeId=0L;
			Long billId=0L;
			Long noOfPost=0L;
			Long orderId=0L;
			String desgnCode="";
			Long desgnId=0L;
			String startDate="";
			String endDate="";			
			String tempEndDate="";
			String orderDate="";    
			String postType="";
			String remarks="";
			String oldOrderDate="";
			String oldOrderCmb="";
			String newOrderDate="";
			String newOrderId="";
			String tempTypePost="";
			String Permenant="";
			Boolean lBlFlag = false;

			if(objectArgs.get("officeCmb")!=null)
			{
				officeId = Long.parseLong(objectArgs.get("officeCmb").toString());
			}
			if(objectArgs.get("billCmb")!=null)
			{
				billId = Long.parseLong(objectArgs.get("billCmb").toString());
			}
			if(objectArgs.get("noofpost")!=null)
			{
				noOfPost = Long.parseLong(objectArgs.get("noofpost").toString());
			}

			if(objectArgs.get("orderCmb")!=null)
			{
				orderId = Long.parseLong(objectArgs.get("orderCmb").toString());
			}

			if(objectArgs.get("designationCmb")!=null && !objectArgs.get("designationCmb").toString().equals(""))
			{
				desgnCode = objectArgs.get("designationCmb").toString();
				desgnId=Long.parseLong(desgnCode);
			}
			if(objectArgs.get("startDate")!=null && !objectArgs.get("startDate").toString().equals(""))
			{
				startDate = objectArgs.get("startDate").toString();

			}
			
			if(objectArgs.get("endDate")!=null && !objectArgs.get("endDate").toString().equals(""))
			{
				endDate = objectArgs.get("endDate").toString();

			}
			if(objectArgs.get("tempEndDate")!=null && !objectArgs.get("tempEndDate").toString().equals(""))
			{
				tempEndDate = objectArgs.get("tempEndDate").toString();

			}
			if(objectArgs.get("postType")!=null )
			{
				postType = objectArgs.get("postType").toString();
				 
			}
			if(objectArgs.get("remarks")!=null && !objectArgs.get("remarks").toString().equals(""))
			{
				remarks = objectArgs.get("remarks").toString();

			}

			if(objectArgs.get("oldOrderDate")!=null && !objectArgs.get("oldOrderDate").toString().equals(""))
			{
				oldOrderDate = objectArgs.get("oldOrderDate").toString();

			}
			if(objectArgs.get("oldOrderCmb")!=null && !objectArgs.get("oldOrderCmb").toString().equals(""))
			{
				oldOrderCmb = objectArgs.get("oldOrderCmb").toString();

			}
			if(objectArgs.get("newDate")!=null && !objectArgs.get("newDate").toString().equals(""))
			{
				newOrderDate = objectArgs.get("newDate").toString();

			}
			if(objectArgs.get("newOrderCmb")!=null && !objectArgs.get("newOrderCmb").toString().equals(""))
			{
				newOrderId = objectArgs.get("newOrderCmb").toString();

			}
			if(objectArgs.get("tempTypePost")!=null && !objectArgs.get("tempTypePost").toString().equals(""))
			{
				tempTypePost = objectArgs.get("tempTypePost").toString();

			}
			if(objectArgs.get("orderDate")!=null && !objectArgs.get("orderDate").toString().equals(""))
			{
				orderDate = objectArgs.get("orderDate").toString();
			}
			
			
			if(objectArgs.get("Permenant")!=null && !objectArgs.get("Permenant").toString().equals(""))
			{
				Permenant = objectArgs.get("Permenant").toString();
			}
			
			
			CmnLookupMstDAO cmnLookupMstDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());

			CmnLookupMst cmnLookupPostStatus=cmnLookupMstDAO.read(13L);

			OrderHeadPostmpgDAOImpl orderHeadPostMpgDAO = new OrderHeadPostmpgDAOImpl(HrPayOrderHeadPostMpg.class, serv.getSessionFactory());

			CmnLanguageMstDao cmnLanguageMstDao = new CmnLanguageMstDaoImpl(CmnLanguageMst.class, serv.getSessionFactory());

			CmnLocationMstDaoImpl cmnLocationMstDaoImpl = new CmnLocationMstDaoImpl(CmnLocationMst.class, serv.getSessionFactory());

			OrgDesignationMstDao orgDesignationMstDao = new OrgDesignationMstDaoImpl(OrgDesignationMst.class, serv.getSessionFactory());

			PsrPostMpgDAOImpl postPsrMpgDao = new PsrPostMpgDAOImpl(HrPayPsrPostMpg.class, serv.getSessionFactory());

			OrgPostDetailsRltDao orgPostDetailsRltDao = new OrgPostDetailsRltDaoImpl(OrgPostDetailsRlt.class, serv.getSessionFactory());

			OrgDesignationMst desgnMst = orgDesignationMstDao.read(desgnId);
		
			CmnLanguageMst cmnLangMst=cmnLanguageMstDao.read(1L);
			
			CmnLocationMst cmnLocMst=cmnLocationMstDaoImpl.read(locId);

			OrderHeadMpgDAOImpl orderHeadDAO = new OrderHeadMpgDAOImpl(HrPayOrderHeadMpg.class, serv.getSessionFactory());

			String billScheme=orderHeadDAO.getBillScheme(billId);		

			String cmprColumn[]={"orderId","subheadId"};
			Object cmprValues[]={orderId,billScheme};

			List orderHeadList = orderHeadDAO.getListByColumnAndValue(cmprColumn, cmprValues);

			HrPayOrderHeadMpg orderHeadMpg = new HrPayOrderHeadMpg();
			long orderHeadId =0;
			IdGenerator idGen = new IdGenerator();
		
			
			if(orderHeadList.size()==0)
			{logger.info("2");
				orderHeadMpg = new HrPayOrderHeadMpg();
				orderHeadId = idGen.PKGeneratorWODBLOC("HR_PAY_ORDER_HEAD_MPG",objectArgs);
				orderHeadMpg.setOrderHeadId(orderHeadId);
				orderHeadMpg.setOrderId(orderId);
				orderHeadMpg.setSubheadId(billScheme);
				orderHeadMpg.setCreatedDate(new Date());
				orderHeadMpg.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
				orderHeadMpg.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
				orderHeadMpg.setTrnCounter(1);
				orderHeadDAO.create(orderHeadMpg);
			
			}
			else{logger.info("3");
				orderHeadMpg=(HrPayOrderHeadMpg)orderHeadList.get(0);
				orderHeadId=orderHeadMpg.getOrderHeadId();
			 }
			Long postId=0L;
			Long postDtlId=0L;
			Long nextPsr=postPsrMpgDao.getNextPsrNo();			
			Long psrPostId=0L;
			Long orderHeadPostId=0L;
			HrPayOrderMst hrPayOrderMst=null;
			OrgPostMst orgPostMaster=null;
			OrgPostDetailsRlt orgPostDtlRlt=null;
			HrPayPsrPostMpg postPsrMpg=null;
			HrPayOrderHeadPostMpg orderHeadPostmpg=null;
			CmnLookupMst cmnLookupMst=null;
		    String CmnPId ="10001198129";
			String CmnTId ="10001198130";	
			
			if(postType.equals(CmnPId))
			{logger.info("4");
				for(int postCount=1;postCount<=noOfPost;postCount++)
				{
					logger.info("5");
				CmnlookupMstDAOImpl  cmnlookupMstDAOImpl=new CmnlookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
				cmnLookupMst=cmnlookupMstDAOImpl.read(Long.parseLong(postType));
				
				orgPostMaster= new OrgPostMst();
				postId=idGen.PKGeneratorWODBLOC("org_post_mst",objectArgs);
				
				orgPostMaster.setPostId(postId);
				orgPostMaster.setParentPostId(-1);
				orgPostMaster.setPostLevelId(1);
				orgPostMaster.setStartDate(StringUtility.convertStringToDate(startDate));
				if(endDate!=""){logger.info("6");
					orgPostMaster.setEndDate(StringUtility.convertStringToDate(endDate));
				}
				orgPostMaster.setCmnLookupMst(cmnLookupPostStatus);
				orgPostMaster.setDsgnCode(desgnCode);
				orgPostMaster.setActivateFlag(1);
				orgPostMaster.setCreatedDate(new Date());
				orgPostMaster.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
				orgPostMaster.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
				orgPostMaster.setLocationCode(String.valueOf(locId));
				orgPostMaster.setPostTypeLookupId(cmnLookupMst);
				
				orgPostMstDaoImpl.create(orgPostMaster);

				postPsrMpg=new HrPayPsrPostMpg();
				psrPostId=idGen.PKGeneratorWODBLOC("HR_PAY_POST_PSR_MPG",objectArgs);
				postPsrMpg.setPsrPostId(psrPostId);			
				postPsrMpg.setPsrId(nextPsr);				
				postPsrMpg.setPostId(postId);				
				postPsrMpg.setBillNo(billId);
				postPsrMpg.setLoc_id(locId);
				
				postPsrMpgDao.create(postPsrMpg);

				orgPostDtlRlt= new OrgPostDetailsRlt();
				postDtlId=idGen.PKGeneratorWODBLOC("org_post_details_rlt",objectArgs);				
				orgPostDtlRlt.setPostDetailId(postDtlId);
				orgPostDtlRlt.setOrgPostMst(orgPostMaster);
				orgPostDtlRlt.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
				orgPostDtlRlt.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
				
				orgPostDtlRlt.setCmnLanguageMst(cmnLangMst);
				orgPostDtlRlt.setOrgDesignationMst(desgnMst);
				orgPostDtlRlt.setCreatedDate(new Date());
				orgPostDtlRlt.setPostName(desgnMst.getDsgnName().concat(String.valueOf(nextPsr)));
				orgPostDtlRlt.setPostShortName(desgnMst.getDsgnShrtName().concat(String.valueOf(nextPsr)));
				orgPostDtlRlt.setCmnLocationMst(cmnLocMst);
				orgPostDetailsRltDao.create(orgPostDtlRlt);


				orderHeadPostmpg=new HrPayOrderHeadPostMpg();
				orderHeadPostId=idGen.PKGeneratorWODBLOC("HR_PAY_ORDER_HEAD_POST_MPG",objectArgs);
				orderHeadPostmpg.setOrderHeadPostId(orderHeadPostId);
				orderHeadPostmpg.setOrderHeadId(orderHeadId);
				orderHeadPostmpg.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
				orderHeadPostmpg.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
				orderHeadPostmpg.setPostId(postId);
				orderHeadPostmpg.setCreatedDate(new Date());

				orderHeadPostMpgDAO.create(orderHeadPostmpg);
				
				HrPayOfficepostMpg hrOfficePostMpg = new HrPayOfficepostMpg();
				HrPayOfficePostMpgDAOImpl officePostMpgDAOImpl = new HrPayOfficePostMpgDAOImpl(HrPayOfficepostMpg.class, serv.getSessionFactory());

				DdoOffice ddoOffice=new DdoOffice();
				DdoOfficeDAOImpl ddoOfficeDAOImpl = new DdoOfficeDAOImpl(DdoOffice.class,serv.getSessionFactory());
				ddoOffice =(DdoOffice) ddoOfficeDAOImpl.read(officeId);

				Long officePostId = IDGenerateDelegate.getNextId("HR_PAY_OFFICEPOST_MPG",objectArgs);

				hrOfficePostMpg.setOfficePostId(officePostId);
				hrOfficePostMpg.setDdoOffice(ddoOffice);
				hrOfficePostMpg.setOrgPostMstByPostId(orgPostMaster);
				
				hrOfficePostMpg.setStartDate(StringUtility.convertStringToDate(startDate));
				hrOfficePostMpg.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
				hrOfficePostMpg.setCreatedDate(new Date());
				hrOfficePostMpg.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
				officePostMpgDAOImpl.create(hrOfficePostMpg);
				
				logger.info("7");
				nextPsr++;
				
			   }
				msg=0;
			}
			else if(postType.equals(CmnTId))
			{logger.info("8");
				if(tempTypePost.equals("3"))
				{
					for(int postCount=1;postCount<=noOfPost;postCount++)
					{
						
					CmnlookupMstDAOImpl  cmnlookupMstDAOImpl=new CmnlookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
					cmnLookupMst=cmnlookupMstDAOImpl.read(Long.parseLong(postType));

					orgPostMaster= new OrgPostMst();
					postId=idGen.PKGeneratorWODBLOC("org_post_mst",objectArgs);
					cmnLookupMst=cmnlookupMstDAOImpl.read(Long.parseLong(postType));
					
					orgPostMaster.setPostId(postId);
					orgPostMaster.setParentPostId(-1);
					orgPostMaster.setPostLevelId(1);
					orgPostMaster.setStartDate(StringUtility.convertStringToDate(startDate));
					orgPostMaster.setEndDate(StringUtility.convertStringToDate(tempEndDate));
					orgPostMaster.setCmnLookupMst(cmnLookupPostStatus);
					orgPostMaster.setDsgnCode(desgnCode);
					orgPostMaster.setActivateFlag(1);
					orgPostMaster.setCreatedDate(new Date());
					orgPostMaster.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
					orgPostMaster.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
					orgPostMaster.setLocationCode(String.valueOf(locId));
					orgPostMaster.setPostTypeLookupId(cmnLookupMst);
				
					orgPostMstDaoImpl.create(orgPostMaster);

					//#########################
					postPsrMpg=new HrPayPsrPostMpg();
					psrPostId=idGen.PKGeneratorWODBLOC("HR_PAY_POST_PSR_MPG",objectArgs);
					postPsrMpg.setPsrPostId(psrPostId);
					postPsrMpg.setPsrId(nextPsr);
					postPsrMpg.setPostId(postId);
					postPsrMpg.setBillNo(billId);
					postPsrMpg.setLoc_id(locId);
					postPsrMpgDao.create(postPsrMpg);
					
					//##########################nextPsr
					orgPostDtlRlt= new OrgPostDetailsRlt();
					postDtlId=idGen.PKGeneratorWODBLOC("org_post_details_rlt",objectArgs);
					orgPostDtlRlt.setPostDetailId(postDtlId);
					orgPostDtlRlt.setOrgPostMst(orgPostMaster);
					orgPostDtlRlt.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
					orgPostDtlRlt.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
					
					orgPostDtlRlt.setCmnLanguageMst(cmnLangMst);
					orgPostDtlRlt.setOrgDesignationMst(desgnMst);
					orgPostDtlRlt.setCreatedDate(new Date());
					orgPostDtlRlt.setPostName(desgnMst.getDsgnName().concat(String.valueOf(nextPsr)));
					orgPostDtlRlt.setPostShortName(desgnMst.getDsgnShrtName().concat(String.valueOf(nextPsr)));					
					orgPostDtlRlt.setCmnLocationMst(cmnLocMst);
					
					orgPostDetailsRltDao.create(orgPostDtlRlt);
					
					
					orderHeadPostmpg=new HrPayOrderHeadPostMpg();
					orderHeadPostId=idGen.PKGeneratorWODBLOC("HR_PAY_ORDER_HEAD_POST_MPG",objectArgs);
					orderHeadPostmpg.setOrderHeadPostId(orderHeadPostId);
					orderHeadPostmpg.setOrderHeadId(orderHeadId);
					orderHeadPostmpg.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
					orderHeadPostmpg.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
					orderHeadPostmpg.setPostId(postId);
					orderHeadPostmpg.setCreatedDate(new Date());

					orderHeadPostMpgDAO.create(orderHeadPostmpg);
					
					HrPayOfficepostMpg hrOfficePostMpg = new HrPayOfficepostMpg();
					HrPayOfficePostMpgDAOImpl officePostMpgDAOImpl = new HrPayOfficePostMpgDAOImpl(HrPayOfficepostMpg.class, serv.getSessionFactory());

					DdoOffice ddoOffice=new DdoOffice();
					DdoOfficeDAOImpl ddoOfficeDAOImpl = new DdoOfficeDAOImpl(DdoOffice.class,serv.getSessionFactory());
					ddoOffice =(DdoOffice) ddoOfficeDAOImpl.read(officeId);



					Long officePostId = IDGenerateDelegate.getNextId("HR_PAY_OFFICEPOST_MPG",objectArgs);
					
					hrOfficePostMpg.setOfficePostId(officePostId);
					hrOfficePostMpg.setDdoOffice(ddoOffice);
					hrOfficePostMpg.setOrgPostMstByPostId(orgPostMaster);
					hrOfficePostMpg.setStartDate(StringUtility.convertStringToDate(startDate));
					hrOfficePostMpg.setOrgUserMstByCreatedBy(orgUserMstLoggedin);
					hrOfficePostMpg.setCreatedDate(new Date());
					hrOfficePostMpg.setOrgPostMstByCreatedByPost(orgPostMstLoggedIn);
					officePostMpgDAOImpl.create(hrOfficePostMpg);
					
					
					nextPsr++;
				
				}
					msg = 0;
			}
					else if(tempTypePost.equals("4"))
					{	
						
						for(int postCount=1;postCount<=noOfPost;postCount++)
						{
							
							OrderMstDAOImpl ordermstDaoImpl=new OrderMstDAOImpl(HrPayOrderMst.class, serv.getSessionFactory());
							hrPayOrderMst=ordermstDaoImpl.read(Long.parseLong(newOrderId));
							hrPayOrderMst.setOrderDate(StringUtility.convertStringToDate(newOrderDate));
							hrPayOrderMst.setUpdatedDate(new Date());
							hrPayOrderMst.setOrgPostMstByUpdatedByPost(orgPostMstLoggedIn);
							hrPayOrderMst.setOrgUserMstByUpdatedBy(orgUserMstLoggedin);
							ordermstDaoImpl.update(hrPayOrderMst);
						}
						msg = 1;
					}
				
				
			}
		
			logger.info("9");
			Long scaleId = 1L;
			GenericDaoHibernateImpl genDao = new GenericDaoHibernateImpl(HrEisScaleMst.class);
			genDao.setSessionFactory(serv.getSessionFactory());
			HrEisScaleMst hrEisScaleMst = (HrEisScaleMst) genDao.read(scaleId);
			CmnLookupMstDAOImpl cmnDao = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());
			GradeMasterDAO gradeDao = new GradeMasterDAO(OrgGradeMst.class,serv.getSessionFactory());			
			Long parentLocId = cmnLocMst.getParentLocId();
			
			AdminOrgPostDtlDaoImpl adminOrgPostDtlDaoImpl = new AdminOrgPostDtlDaoImpl(OrgPostMst.class, serv.getSessionFactory());
			List<MstPayrollDesignationMst> lst = adminOrgPostDtlDaoImpl.getMstDcpsDsgnObject(parentLocId, desgnMst.getDsgnId());
			
			MstPayrollDesignationMst mst = new MstPayrollDesignationMst();
			if (lst != null && lst.size() > 0)
				mst = lst.get(0);
			
			logger.info("10");
			Long cadreTypeId = mst.getCadreTypeId();
			Long grpName = Long.parseLong(new Long(cadreTypeId).toString());
			
			CmnLookupMst loonkupGrd = cmnDao.read(grpName);
			genDao = new GenericDaoHibernateImpl(OrgGradeMst.class);
			genDao.setSessionFactory(serv.getSessionFactory());
			
			Long gradeId = Long.parseLong(loonkupGrd.getLookupShortName().toString());
						
			OrgGradeMst orgGradeMst = (OrgGradeMst) genDao.read(gradeId);
			GradDesgScaleMapDAO sgdDao = new GradDesgScaleMapDAO(HrEisSgdMpg.class, serv.getSessionFactory());
			Long dbId = Long.parseLong(loginMap.get("dbId").toString());
			CmnDatabaseMstDaoImpl cmnDatabaseMstDaoImpl = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class, serv.getSessionFactory());
			CmnDatabaseMst cmnDatabaseMst = (CmnDatabaseMst) cmnDatabaseMstDaoImpl.read(Long.valueOf(dbId));
			Long loggedInpostId = Long.parseLong(loginMap.get("loggedInPost").toString());
			OrgPostMst loggedInOrgPostMst = orgPostMstDaoImpl.read(loggedInpostId);
			Long loggedInUser = StringUtility.convertToLong(loginMap.get("userId").toString());
			OrgUserMst loggedInOrgUserMst = orgUserMstDaoImpl.read(loggedInUser);
			
			HrEisGdMpg gdMpg = null;
			HrEisSgdMpg sgdMpg = null;
			List<HrEisGdMpg> gdList = gradeDao.getDuplicateData(gradeId,desgnMst.getDsgnId(), cmnLocMst.getLocId());
			if (gdList != null && gdList.size() > 0) {logger.info("11");
				gdMpg = gdList.get(0);
			
				Long gdId = gdMpg.getGdMapId();
				List<HrEisSgdMpg> sgdMpglist = sgdDao.getScaleGradeDesgMasterData(gdId, scaleId,cmnLocMst.getLocId());
				if (sgdMpglist != null && sgdMpglist.size() > 0) {logger.info("12");
					sgdMpg = sgdMpglist.get(0);
				} else {logger.info("13");
					sgdMpg = new HrEisSgdMpg();
					Long sgdId = idGen.PKGenerator("HR_EIS_SGD_MPG", objectArgs);
					sgdMpg.setSgdMapId(sgdId);
					sgdMpg.setCmnDatabaseMst(cmnDatabaseMst);					
					sgdMpg.setCmnLocationMst(cmnLocMst);
					sgdMpg.setCreatedDate(new java.util.Date());
					sgdMpg.setHrEisGdMpg(gdMpg);
					sgdMpg.setHrEisScaleMst(hrEisScaleMst);
					sgdMpg.setOrgPostMstByCreatedByPost(loggedInOrgPostMst);
					sgdMpg.setOrgUserMstByCreatedBy(loggedInOrgUserMst);
					sgdDao.create(sgdMpg);					
				}
			} else {logger.info("14");
				
				gdMpg = new HrEisGdMpg();
				Long gdId = idGen.PKGenerator("HR_EIS_GD_MPG", objectArgs);
				gdMpg.setCmnDatabaseMst(cmnDatabaseMst);				
				gdMpg.setCmnLocationMst(cmnLocMst);
				gdMpg.setCreatedDate(new java.util.Date());
				gdMpg.setGdMapId(gdId);
				gdMpg.setOrgDesignationMst(desgnMst);
				gdMpg.setOrgGradeMst(orgGradeMst);
				gdMpg.setOrgPostMstByCreatedByPost(loggedInOrgPostMst);
				gdMpg.setOrgUserMstByCreatedBy(loggedInOrgUserMst);
				genDao = new GenericDaoHibernateImpl(HrEisGdMpg.class);
				genDao.setSessionFactory(serv.getSessionFactory());
				genDao.create(gdMpg);
				
				sgdMpg = new HrEisSgdMpg();
				Long sgdId = idGen.PKGenerator("HR_EIS_SGD_MPG", objectArgs);
				sgdMpg.setSgdMapId(sgdId);
				sgdMpg.setCmnDatabaseMst(cmnDatabaseMst); 
				sgdMpg.setCmnLocationMst(cmnLocMst);
				sgdMpg.setCreatedDate(new java.util.Date());
				sgdMpg.setHrEisGdMpg(gdMpg);
				sgdMpg.setHrEisScaleMst(hrEisScaleMst);
				sgdMpg.setOrgPostMstByCreatedByPost(loggedInOrgPostMst);
				sgdMpg.setOrgUserMstByCreatedBy(loggedInOrgUserMst);
				
				sgdDao.create(sgdMpg);
				
			}
		
			lBlFlag = true;
			resObj = showNewPostForm(objectArgs);
			
			String lSBStatus = getSaveResponseXMLDoc(lBlFlag,lStrFieldDept,lStrDdoCode).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			objectArgs.put("ajaxKey", lStrResult);
			resObj.setResultValue(objectArgs);
			resObj.setViewName("ajaxData");
		} catch (Exception ex) {
			ex.printStackTrace();
			resObj.setThrowable(ex);
			logger.error("Admin Screen Submitting A Post Error", ex);
			resObj.setResultCode(-1);
		}
		return resObj;
	}
	
	public ResultObject getOfficeCityClassAndHRA(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);				
			
		String lStrOfficeCityClass = "";
		String lStrHra = "";
		Long lLngOfficeId = 0L;
		try {
			setSessionInfo(inputMap);
			AdminPostDtlsDAO lObjOrgPostDtlDao = new AdminPostDtlsDAOImpl(null,serv.getSessionFactory());
			String lStrOfficeCode = StringUtility.getParameter("officeId", request).toString();
			
			if(lStrOfficeCode != null && !lStrOfficeCode.equals(""))
				lLngOfficeId = Long.parseLong(lStrOfficeCode); 

			lStrOfficeCityClass = lObjOrgPostDtlDao.getOfficeCityClassAndHra(lLngOfficeId);
		
			String lSBStatus = getResponseForOfficeXMLDoc(lStrOfficeCityClass,lStrHra).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");


		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	private StringBuilder getSaveResponseXMLDoc(Boolean flag,String lStrFieldDept,String lStrDDOCode) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<FieldDept>");
		lStrBldXML.append(lStrFieldDept);
		lStrBldXML.append("</FieldDept>");
		lStrBldXML.append("<DDOCode>");
		lStrBldXML.append(lStrDDOCode);
		lStrBldXML.append("</DDOCode>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
	private StringBuilder getResponseForOfficeXMLDoc(String lStrOfficeCityClass,String lStrHra) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");		
		lStrBldXML.append("<CityClass>");
		lStrBldXML.append(lStrOfficeCityClass);
		lStrBldXML.append("</CityClass>");
		lStrBldXML.append("<HRA>");
		lStrBldXML.append(lStrHra);
		lStrBldXML.append("</HRA>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
		
}
