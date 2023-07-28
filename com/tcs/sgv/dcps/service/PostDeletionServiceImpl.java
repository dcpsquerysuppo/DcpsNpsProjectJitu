package com.tcs.sgv.dcps.service;

import java.util.Map;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.sun.istack.internal.logging.Logger;
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
import com.tcs.sgv.dcps.dao.PostConversionDAOImpl;
import com.tcs.sgv.dcps.dao.PostDeletionDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;
import com.tcs.sgv.dcps.valueobject.MstEmp;
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
import javax.servlet.http.HttpServletRequest;


public class PostDeletionServiceImpl extends ServiceImpl {
	
	
	
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
	
	
	public ResultObject postDeletion (Map<String, Object> inputMap)
	{
		//System.out.println("hii");
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
	resObj.setViewName("DeletePostForm");
	return resObj;
}	


	public ResultObject showDeletePostDtl(Map<String, Object> objectArgs) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		setSessionInfo(objectArgs);
		try {
			
			ArrayList grp =new ArrayList();
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
			List<ComboValuesVO> lLstDdo = lObjPostDtlsDAO.getDdoListFromFieldDept(gLngLocationCode.toString());
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
			
			if( postNameList!=null && postNameList.size()>0)
			{
				for (int i = 0; i < postNameList.size(); i++) 
				{
					customVO = new UserPostCustomVO();

					Object rowList[] = (Object[]) postNameList.get(i);
					if(rowList[2] == null || (rowList[2].toString().trim()).equals(""))
					{	
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
					
					Long postId = Long.parseLong(rowList[1].toString());
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
						if(rowList[7].toString().equalsIgnoreCase("Temporary"))
							postType="T";
						else
							postType="P";
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
						grp.add(lStrGRNo);
						
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
				}
			}//end if
			int value= post.size();
            
			
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
			objectArgs.put("recordsize", value);
			
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
			resObj.setViewName("showDeletePostDtls");
			resObj.setResultCode(0);
			resObj.setResultValue(objectArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			resObj.setThrowable(ex);
			gLogger.error("Admin Screen Showing all Post Error", ex);
			resObj.setResultCode(-1);
		}
		return resObj;
		
		
		}
	
	public ResultObject searchDeletePostDtls(Map objectArgs) 
	{
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		setSessionInfo(objectArgs);
		PostDeletionDAOImpl lObjPostDeletionDAO=new PostDeletionDAOImpl(null, serv.getSessionFactory());
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
			
			List postNameListWithIds = lObjPostDeletionDAO.getPostNameForDisplayWithSearch(locId, PostName, Psr, Bill, dsgn, designationId, billId);
			UserPostCustomVO customVO = new UserPostCustomVO();	
			List post = new ArrayList();	
					
						
			List postNameList = new ArrayList();
			postNameList = lObjPostDeletionDAO.getPostNameForDisplayWithSearch(locId,PostName,Psr, Bill, dsgn);
			String postLookupId="";
			String permenantlookupId="10001198129";
			String temparerylookupId="10001198130";
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
			String TodaysDate = fmt.format(today);
			
			OrderMstDAOImpl ordermstDaoImpl = new OrderMstDAOImpl(HrPayOrderMst.class, serv.getSessionFactory());
			List orderList = ordermstDaoImpl.getAllData(locId,TodaysDate);
		
			objectArgs.put("orderList", orderList);
			
			
			if(designationId>0 || billId>0)
			{
				
				if( postNameListWithIds!=null && postNameListWithIds.size()>0)
				{
					for (int i = 0; i < postNameListWithIds.size(); i++) 
					{
						customVO = new UserPostCustomVO();

						Object rowList[] = (Object[]) postNameListWithIds.get(i);
						if(rowList[2] == null || (rowList[2].toString().trim()).equals(""))
						{	
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
							if(rowList[7].toString().equalsIgnoreCase("Temporary"))
								postType="T";
							else
								postType="P";
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
						
					}
					}//end for
				}
			}else{
				if( postNameList!=null )
				{
					for (int i = 0; i < postNameList.size(); i++) 
					{
						customVO = new UserPostCustomVO();

						Object rowList[] = (Object[]) postNameList.get(i);
						if(rowList[2] == null || (rowList[2].toString().trim()).equals(""))
						{	
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
							if(rowList[7].toString().equalsIgnoreCase("Temporary"))
								postType="T";
							else
								postType="P";
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
						}
					}//end for
				}//end if
			}
			
			long size = post.size();
			objectArgs.put("recordsize", size);
			objectArgs.put("recordList", post);			
			resObj.setViewName("showDeletePostDtls");
			resObj.setResultCode(0);
			resObj.setResultValue(objectArgs);
		} 
		catch (Exception ex) 
		{
			resObj.setThrowable(ex);
			gLogger.error("Admin Screen Creating A Post Error", ex);
			resObj.setResultCode(-1);
		}

		return resObj;
	}
	
	public ResultObject saveDeletePostDtls(Map<String, Object> objectArgs) throws Exception
	{  ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		gLogger.error("inside the method");
		setSessionInfo(objectArgs);
		long tempCount = 0;

		try {
			PostDeletionDAOImpl lObjPostDeletionDAO=new PostDeletionDAOImpl(null, serv.getSessionFactory());
			gLogger.info("1 inside the method");
			String data = StringUtility.getParameter("totalString", request);
			gLogger.info("2 inside the method");
			String s[]=data.split(",");
			gLogger.info("3 inside the method");
			String details[]=new String[s.length*4];
			gLogger.info("4 inside the method");
			for(int i=1;i<s.length;i++)
			{
				 details=s[i].split("~");
				 gLogger.info("5 inside the method");
			 
			
			gLogger.error("String is" +details[0]);
			gLogger.info("String is" +details[1]);
			gLogger.info("String is" +details[2]);
		
			/*
			if(details.length>3 && details[3]!=null){
			gLogger.info("String is");
			}
			else{
				gLogger.info("No remarks");
			}*/
			/*for(int j=0;j<details.length;j=j+4)
			{
			*/	Long postID=Long.parseLong(details[0]);
				String postType=details[1];
				if(postType!=null)
				{
					tempCount++;
				}
				Long orderId=Long.parseLong(details[2]);
				String remark=null;
							
				if(!details[3].equals("-"))
				remark=details[3];
				else{
					remark=" ";
				}
				
				lObjPostDeletionDAO.getDetailsforDeletePostHistory(postID,postType,orderId,remark,gLngUserId,gLngPostId,gDtCurDate,s.length);
				
			}
			//}
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<tempCount>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(tempCount);
			lStrBldXML.append("]]>");
			lStrBldXML.append("</tempCount>");
			lStrBldXML.append("</XMLDOC>");
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lStrBldXML.toString()).toString();
			objectArgs.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			
		resObj.setResultCode(0);
		resObj.setResultValue(objectArgs);
		}
		 catch (Exception e) {
			// TODO Auto-generated catch block
			 gLogger.error("exception in deletion post  "+e);
		}
		return resObj ;
		 
		 
	}

}
