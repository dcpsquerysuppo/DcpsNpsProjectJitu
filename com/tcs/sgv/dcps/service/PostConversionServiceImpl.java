package com.tcs.sgv.dcps.service;


import java.util.Map;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.sun.istack.internal.logging.Logger;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLanguageMstDao;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ExcelExportHelper;
import com.tcs.sgv.common.helper.ReportExportHelper;
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


public class PostConversionServiceImpl extends ServiceImpl {
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

	ResourceBundle constantsBundle1 = ResourceBundle.getBundle("../resources/Payroll");


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



	public ResultObject postConversion (Map<String, Object> inputMap)
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
		resObj.setViewName("ChangePostForm");
		return resObj;
	}	














	public ResultObject showChangePostDtl(Map<String, Object> objectArgs) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		setSessionInfo(objectArgs);
		long tempfilledcount=0;
		long perfilledcount=0;
		long tempvacantcount=0;
		long pervacantcount=0;
		long total=0;
		long permtotal=0;
		long temptotal=0;
		long  vacanttotal=0;
		long  filledtotal=0;
		try {		
			int flag=0;
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
						if(rowList[7].toString().equalsIgnoreCase("Temporary"))
							{postType="T";
						
							}
						else{
							postType="P";
						
						}

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
					
					if(empFullName.equals("VACANT") && postType.equals("P"))
						pervacantcount++;
					if(empFullName.equals("VACANT") && postType.equals("T"))
						tempvacantcount++;
					if(!empFullName.equals("VACANT") && postType.equals("P"))
						perfilledcount++;
					if(!empFullName.equals("VACANT") && postType.equals("T"))
						tempfilledcount++;
					
					
					
				}//end for
                 
				 total=pervacantcount+tempvacantcount+perfilledcount+tempfilledcount;
				 permtotal=pervacantcount+perfilledcount;
				 temptotal=tempvacantcount+tempfilledcount;
				 vacanttotal=pervacantcount+tempvacantcount;
				 filledtotal=perfilledcount+tempfilledcount;
				gLogger.info("permvacantcount is"+pervacantcount);
				gLogger.info("tempvacantcount is"+tempvacantcount);
				gLogger.info("permfilledcount is"+perfilledcount);
				gLogger.info("tempfilledcount is"+tempfilledcount);
			}//end if
			int value= post.size();


			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
			String TodaysDate = fmt.format(today);


			OrderMstDAOImpl ordermstDaoImpl = new OrderMstDAOImpl(HrPayOrderMst.class, serv.getSessionFactory());
			List orderList = ordermstDaoImpl.getAllData(locId,TodaysDate);
              gLogger.info("total"+total);  
              
			objectArgs.put("total", total);
			objectArgs.put("permtotal", permtotal);
			objectArgs.put("temptotal",  temptotal);
			objectArgs.put("pervacantcount", pervacantcount);
			objectArgs.put("tempvacantcount", tempvacantcount);
			objectArgs.put("perfilledcount", perfilledcount);
			objectArgs.put("tempfilledcount", tempfilledcount);
			objectArgs.put("vacanttotal",  vacanttotal);
			objectArgs.put("filledtotal",  filledtotal);
			objectArgs.put("flag",  flag);
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

			
			List postTypeLst = new ArrayList();
			Object obj[] = new Object[2];
			obj[0] = "-1";
			obj[1] = "All";
			postTypeLst.add(obj);

			Object obj1[] = new Object[2];
			obj1[0] = "10001198129";
			obj1[1] = "Permanent";
			postTypeLst.add(obj1);

			Object obj2[] = new Object[2];
			obj2[0] = "10001198130";
			obj2[1] = "Temporary";
			postTypeLst.add(obj2);

			objectArgs.put("postTypeLst", postTypeLst );
			
			objectArgs.put("fieldCaptionList", fieldCaptionList);
			resObj.setViewName("showChangePostDtls");
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
	public ResultObject searchChangePostDtls(Map objectArgs) 
	{
         
		long tempfilledcount=0;
		long perfilledcount=0;
		long tempvacantcount=0;
		long pervacantcount=0;
		long total=0;
		long permtotal=0;
		long temptotal=0;
		long  vacanttotal=0;
		long  filledtotal=0;
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		setSessionInfo(objectArgs);
		PostConversionDAOImpl lObjPostConversionDAO=new PostConversionDAOImpl(null, serv.getSessionFactory());
		try 
		{
			int flag=1;
			ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			AdminPostDtlsDAO lObjPostDtlsDAO = new AdminPostDtlsDAOImpl(null, serv.getSessionFactory());

			Long designationId = (StringUtility.getParameter("dsgnId",request)!=null&&!(StringUtility.getParameter("dsgnId",request).equals(""))?Long.parseLong(StringUtility.getParameter("dsgnId",request)):0);
			gLogger.info("designationId "+designationId);
			Long billId=0L;
			if(!StringUtility.getParameter("billGrpId", request).equals("") && StringUtility.getParameter("billGrpId", request)!=null)
				billId = Long.valueOf(StringUtility.getParameter("billGrpId", request).toString());

			String ddoCode = "";
			if(!StringUtility.getParameter("DdoDtl", request).equals("") && StringUtility.getParameter("DdoDtl", request)!=null)
				ddoCode = StringUtility.getParameter("DdoDtl", request);

			String lStrFieldDeptCode = StringUtility.getParameter("FieldDept", request);
			long  postTypeid = 0l; 
			if(StringUtility.getParameter("postType", request) != null && !StringUtility.getParameter("postType", request).equals("") && !StringUtility.getParameter("postType", request).equals("-1")){
				postTypeid = Long.parseLong(StringUtility.getParameter("postType", request));

			}			
			List<ComboValuesVO> lLstFieldDept = lObjPostDtlsDAO.getFieldDeptFromAdminDeptCode(gLngLocationCode);
			List<ComboValuesVO> lLstDdo = lObjPostDtlsDAO.getDdoListFromFieldDept(gLngLocationCode.toString());
			objectArgs.put("lLstFieldDept", lLstFieldDept);
			objectArgs.put("lLstDdo", lLstDdo);

			objectArgs.put("DDOCode", ddoCode);
			objectArgs.put("FieldDeptCode", lStrFieldDeptCode);
			objectArgs.put("designationId", designationId);
			objectArgs.put("postTypeid", postTypeid);
			objectArgs.put("billId", billId);
			
			List postTypeLst = new ArrayList();
			Object obj[] = new Object[2];
			obj[0] = "-1";
			obj[1] = "All";
			postTypeLst.add(obj);

			Object obj1[] = new Object[2];
			obj1[0] = "10001198129";
			obj1[1] = "Permanent";
			postTypeLst.add(obj1);

			Object obj2[] = new Object[2];
			obj2[0] = "10001198130";
			obj2[1] = "Temporary";
			postTypeLst.add(obj2);

			objectArgs.put("postTypeLst", postTypeLst );
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

			//List<OrgDesignationMst> desgList = adminDao.getActiveDsgnList(locId);	
			PayBillDAOImpl payDAO = new PayBillDAOImpl(HrPayPaybill.class,serv.getSessionFactory());
			List<OrgDdoMst> ddoList = payDAO.getDDOCodeByLoggedInlocId(locId);
			OrgDdoMst ddoMst  = null;
			Long lLngFieldDept = 0L;
			if(ddoList!=null && ddoList.size()>0) {
				ddoMst = ddoList.get(0);
				lLngFieldDept = Long.parseLong(ddoMst.getHodLocCode());
			}	
			List<OrgDesignationMst> desgList = adminDao.getActiveDesig(lLngFieldDept);
			objectArgs.put("Designation", desgList);
					
			
			List billList = lObjPostDtlsDAO.getAllBillsFromLocation(locId);
			objectArgs.put("billList", billList);

			List postNameListWithIds = lObjPostConversionDAO.getPostNameForDisplayWithSearch(locId, PostName, Psr, Bill, dsgn, designationId, billId,postTypeid);
			UserPostCustomVO customVO = new UserPostCustomVO();	
			List post = new ArrayList();	


			List postNameList = new ArrayList();
			postNameList =  lObjPostConversionDAO.getPostNameForDisplayWithSearch(locId,PostName,Psr, Bill, dsgn,postTypeid);
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
                       
						if(empFullName.equals("VACANT") && postType.equals("P"))
							pervacantcount++;
						if(empFullName.equals("VACANT") && postType.equals("T"))
							tempvacantcount++;
						if(!empFullName.equals("VACANT") && postType.equals("P"))
							perfilledcount++;
						if(!empFullName.equals("VACANT") && postType.equals("T"))
							tempfilledcount++;
						
						
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
						
						if(empFullName.equals("VACANT") && postType.equals("P"))
							pervacantcount++;
						if(empFullName.equals("VACANT") && postType.equals("T"))
							tempvacantcount++;
						if(!empFullName.equals("VACANT") && postType.equals("P"))
							perfilledcount++;
						if(!empFullName.equals("VACANT") && postType.equals("T"))
							tempfilledcount++;
					}//end for
				}//end if
			}
			 total=pervacantcount+tempvacantcount+perfilledcount+tempfilledcount;
			 permtotal=pervacantcount+perfilledcount;
			 temptotal=tempvacantcount+tempfilledcount;
			 vacanttotal=pervacantcount+tempvacantcount;
			 filledtotal=perfilledcount+tempfilledcount;
			 
				gLogger.info("permvacantcount is"+pervacantcount);
				gLogger.info("tempvacantcount is"+tempvacantcount);
				gLogger.info("permfilledcount is"+perfilledcount);
				gLogger.info("tempfilledcount is"+tempfilledcount);
			 
			 objectArgs.put("total", total);
			 objectArgs.put("flag", flag);
				objectArgs.put("permtotal", permtotal);
				objectArgs.put("temptotal",  temptotal);
				objectArgs.put("pervacantcount", pervacantcount);
				objectArgs.put("tempvacantcount", tempvacantcount);
				objectArgs.put("perfilledcount", perfilledcount);
				objectArgs.put("tempfilledcount", tempfilledcount);
				objectArgs.put("vacanttotal", vacanttotal);
				objectArgs.put("filledtotal", filledtotal);
			long size = post.size();
			objectArgs.put("recordsize", size);
			objectArgs.put("recordList", post);			
			resObj.setViewName("showChangePostDtls");
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





	public ResultObject saveChangePostDtls(Map<String, Object> objectArgs) throws Exception
	{  ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
	gLogger.info("inside the method");
	setSessionInfo(objectArgs);


	try {
		PostConversionDAOImpl lObjPostConversionDAO=new PostConversionDAOImpl(null, serv.getSessionFactory());
		gLogger.info("1 inside the method");
		String data = StringUtility.getParameter("totalString", request);
		gLogger.info("2 inside the method");
		String s[]=data.split(",");
		gLogger.info("3 inside the method");
		String details[]=new String[s.length*5];
		gLogger.info("4 inside the method");
		long tempCount = 0;
		long permCount = 0;
		for(int i=1;i<s.length;i++)
		{
			details=s[i].split("~");
			gLogger.info("5 inside the method");


			gLogger.info("String is" +details[0]);
			gLogger.info("String is" +details[1]);
			gLogger.info("String is" +details[2]);
			gLogger.info("String is" +details[3]);
			gLogger.info("String is" +details[4]);
			for(int j=0;j<details.length;j=j+5)
			{
				Long postID=Long.parseLong(details[j]);
				String postType=details[j+1];
				String newPost=details[j+2];
				if(newPost.equalsIgnoreCase("T"))
				{
					tempCount++;
				}
				if(newPost.equalsIgnoreCase("P"))
				{
					permCount++;
				}

				String endDate=details[j+3];
				Long orderId=Long.parseLong(details[j+4]);
				lObjPostConversionDAO.getDetailsforPostHistory(postID,postType,newPost,endDate,orderId,gLngUserId,gLngPostId,gDtCurDate,s.length);

			}
		}
		gLogger.info("tempcount is"+tempCount);
		gLogger.info("permcount is"+permCount);
		//List permList=lObjPostConversionDAO.getDetailsforPermanentconvert();	
		//List tempList=lObjPostConversionDAO.getDetailsforTemporaryconvert();

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<tempCount>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(tempCount);
		lStrBldXML.append("]]>");

		lStrBldXML.append("</tempCount>");
		lStrBldXML.append("<permCount>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(permCount);
		lStrBldXML.append("]]>");

		lStrBldXML.append("</permCount>");
		lStrBldXML.append("</XMLDOC>");
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lStrBldXML.toString()).toString();
		objectArgs.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		//objectArgs.put("PermanentConvert", permList);	
		//objectArgs.put("TemporaryConvert", tempList);
		//resObj.setViewName("showChangePostReportDtls");
		resObj.setResultCode(0);
		resObj.setResultValue(objectArgs);
	}
	catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return resObj ;


	}

	/*	public ResultObject saveChangePostDtls(Map<String, Object> inputMap)
	{
		logger.info("hii");
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
	resObj.setViewName("ChangePostForm");
	return resObj;
}	*/

	 //added by Demolisher
    public ResultObject generateDDOExcelTest(Map objectArgs)
    {
   	 gLogger.info("Inside Get generateDDOExcel");
   	 Map loginMap = (Map) (Map) objectArgs.get("baseLoginMap");
    	ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
    	ServiceLocator serv = (ServiceLocator)objectArgs.get("serviceLocator");
    	HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
    	AdminPostDtlsDAO lObjPostDtlsDAO = new AdminPostDtlsDAOImpl(null, serv.getSessionFactory());
     	Long lLngLocId = null;
    	OrgDdoMst lObjDdoMst = null;
    	String lStrDdocode = null;
    	String lFlag = null;
    	Long locId = 0L;
    	Long langId = Long.parseLong(loginMap.get("langId").toString());
    	PostConversionDAOImpl lObjPostConversionDAO=new PostConversionDAOImpl(null, serv.getSessionFactory());
		
    	try
    	{
    		PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());    
   		//Map loginDetailsMap = (Map) objectArgs.get("baseLoginMap");
   		String lStrDDOCode = StringUtility.getParameter("DDO", request);
   		gLogger.info("DDO :::::::::"+lStrDDOCode);
   		String lStrFieldDeptCode = StringUtility.getParameter("FD", request);
   		String lStrLocCode = lObjPostDtlsDAO.getLocationCodeForDDO(lStrDDOCode);
   		if(lStrLocCode != null && !lStrLocCode.equals(""))
				locId = Long.parseLong(lStrLocCode); 
   		
   		String flagSearch = StringUtility.getParameter("flagSearch", request);
   		gLogger.info("DDO :::::::::"+flagSearch);
   		int flagsearch=Integer.parseInt(flagSearch);
   		Long designationId =0l;
   		Long billId=0L;
   		long  postTypeid = 0l;
   		if(flagsearch==1){
   			
   			gLogger.info("Within :::::::::::If condition ::::::::::::::::");
   			designationId = (StringUtility.getParameter("dsgnId",request)!=null&&!(StringUtility.getParameter("dsgnId",request).equals(""))?Long.parseLong(StringUtility.getParameter("dsgnId",request)):0);
   			gLogger.info("designationId :::::::::::::"+designationId);
   			
   			if(!StringUtility.getParameter("billGrpId", request).equals("") && StringUtility.getParameter("billGrpId", request)!=null)
   				billId = Long.valueOf(StringUtility.getParameter("billGrpId", request).toString());
   			gLogger.info("billId :::::::::::::"+billId);
   			 
			if(StringUtility.getParameter("postType", request) != null && !StringUtility.getParameter("postType", request).equals("") && !StringUtility.getParameter("postType", request).equals("-1"))
				postTypeid = Long.parseLong(StringUtility.getParameter("postType", request));
			}	
   		
   		Map voToService = null;
   		String lPostname="";
			String Psr="";
			String Bill="";
			String Dsgn="";
			String empId = "";
			String lStrGRNo = "";
			String lStrPostStartDate = "";
			String lStrPostEndDate = "";
			String lStrGRDate = "";
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
   		
   		
   		String PostName = "";
			String	dsgn = "";
			
			
			List postNameListWithIds = new ArrayList();
			
			List postNameList = new ArrayList();
		
			gLogger.info("Within ::::::::::Else condition::::::::::::::");
			voToService = (Map)objectArgs.get("voToServiceMap");
			if(voToService != null)
			{
				if(voToService.get("Post")!=null)
					lPostname=(String)voToService.get("Post").toString();
				gLogger.info("Post :::::::::::::"+lPostname);
				if(voToService.get("PsrNo")!=null)
					Psr=(String)voToService.get("PsrNo").toString();
				gLogger.info("Psr :::::::::::::"+Psr);
				if(voToService.get("BillNo")!=null)
					Bill=(String)voToService.get("BillNo").toString();
				gLogger.info("Bill :::::::::::::"+Bill);
				if(voToService.get("Dsgn")!=null)
					Dsgn=(String)voToService.get("Dsgn").toString();
				gLogger.info("Dsgn :::::::::::::"+Dsgn);
				if(voToService.get("empId")!=null)
					empId=(String)voToService.get("empId").toString();
				gLogger.info("empId :::::::::::::"+empId);
			}
			
			if(lPostname != null && !lPostname.equals(""))
				PostName = lPostname.trim();		
			if(Dsgn != null && !Dsgn.equals(""))
				dsgn = Dsgn.trim();
			
			if(empId != null && !empId.equals("")){
					postNameList = lObjPostDtlsDAO.getPostNameForDisplayThruEmpId(locId, empId, langId);
				}else{
					postNameList = lObjPostDtlsDAO.getPostNameForDisplay(locId,PostName,Psr, Bill, dsgn); 
				}
		
   		ReportExportHelper objExport = new ReportExportHelper();
		 	List columnvalue = new ArrayList();
			List mainValue=new ArrayList();
			Map output = new HashMap();
			String lSBOut = "";
			String exportTo="excelfile";
			String lineSeperator = System.getProperty("line.separator");
			gLogger.info("----------------------Over Here---------------------------");
		 	List<Object> lLstInnerList = new ArrayList<Object>();
			List<Object> lLstArrOuter = new ArrayList<Object>();
			Object Obj[];
			String  empFullName = "";
			String postType="";
			String postLookupId="";
			String permenantlookupId="10001198129";
			String temparerylookupId="10001198130";
			gLogger.info("----------------------Over Here---------------------------");
			
			if(postNameList!=null )
			{
				gLogger.info("Within ::::::If :::::::::");
				for(int i = 0; i < postNameList.size(); i++)
				{
					
					Obj = (Object[]) postNameList.get(i);
					lLstInnerList = new ArrayList<Object>();
					
					if(Obj[2] != null && !(Obj[2].toString().trim()).equals("")){	
						empFullName = Obj[2].toString();
						
					} else{
						empFullName = "VACANT";
						
					}
					lLstInnerList.add(empFullName!=null ? empFullName : "VACANT");
					
					
					String dsgnName = Obj[3].toString();
					if(Obj[3] != null && !(Obj[3].toString().trim()).equals("")){
						lLstInnerList.add(dsgnName!=null ? dsgnName : "-");
					}
					
					String postName = Obj[0].toString();
					if(Obj[6] != null && !(Obj[6].toString().trim()).equals(""))
					{	
						postLookupId = Obj[6].toString();

						
						
					}
					if(postLookupId.equals(permenantlookupId))
					{
						postName =postName.concat("P");
					}
					else if(postLookupId.equals(temparerylookupId))
					{
						postName =postName.concat("T");
					}					
					lLstInnerList.add(postName!=null ? postName : "-");
					
					
					if(Obj[7] != null && !(Obj[7].toString().trim()).equals("")){	
						if(Obj[7].toString().equalsIgnoreCase("Temporary"))
							{postType="T";
						
							}
						else{
							postType="P";
						
						}

					} else{
						postType = "VACANT";
					}					
					lLstInnerList.add(postType!=null ? postType : "VACANT");
					
					if(Obj[8] != null && !(Obj[8].toString().trim()).equals("")){	
						lStrPostStartDate = lObjDateFormat.format(Obj[8]);
					} else{
						lStrPostStartDate = "";
					}	
					lLstInnerList.add(lStrPostStartDate!=null ? lStrPostStartDate : "");
					
					if(Obj[9] != null && !(Obj[9].toString().trim()).equals("")){	
						lStrPostEndDate = lObjDateFormat.format(Obj[9]);
					} else{
						lStrPostEndDate = "";
					}		
					lLstInnerList.add(lStrPostEndDate!=null ? lStrPostEndDate : "");
					
					if(Obj[10] != null && !(Obj[10].toString().trim()).equals("")){	
						lStrGRNo = Obj[10].toString();
					} else{
						lStrGRNo = "";
					}			
					lLstInnerList.add(lStrGRNo!=null ? lStrGRNo : "");
					
					if(Obj[11] != null && !(Obj[11].toString().trim()).equals("")){	
						lStrGRDate = lObjDateFormat.format(Obj[11]);
					} else{
						lStrGRDate = "";
					}					
					lLstInnerList.add(lStrGRDate!=null ? lStrGRDate : "");
					
					
					lLstArrOuter.add(lLstInnerList);
				}
			}
			ColumnVo[] excelBillReportHeading=new ColumnVo[8];
			  
			  excelBillReportHeading[0]=new ColumnVo("Employee Name",2,13,0,false,false);  
			  excelBillReportHeading[1]=new ColumnVo("Designation", 2, 13, 0,false,false); 
	          excelBillReportHeading[2]=new ColumnVo("Post Name",2,13,0,false,false);
	          excelBillReportHeading[3]=new ColumnVo("Post Type",2,13,0,false,false);
	          excelBillReportHeading[4]=new ColumnVo("Post Start Date",2,8,0,false,false);
	          excelBillReportHeading[5]=new ColumnVo("Post End Date",2,9,0,false,false);
	          excelBillReportHeading[6]=new ColumnVo("GR No",2,9,0,false,false);
	          excelBillReportHeading[7]=new ColumnVo("GR Date",2,9,0,false,false);
			
	          columnvalue.add(excelBillReportHeading);
	          mainValue.add(lLstArrOuter);
	          StringBuffer lSbHeader= new StringBuffer();
	          lSbHeader.append("Report for Post Conversion Details");
	          gLogger.info("lSbHeader "+lSbHeader.toString());
	          String lStrFooter= "";
	          int nMode= 131;
	          
	          ExcelExportHelper exph = new ExcelExportHelper();
	          byte[] aryOut = null;
	          String[] param = new String[1];
	  		  List Headerdata = new ArrayList();
	  		  List footerdata = new ArrayList();
	  		  param[0] = "";
	  		  
	  		 Headerdata.add(lSbHeader.toString());
	  		 footerdata.add(lStrFooter);
	          
	          aryOut = exph.getExcelReportPrintFormat(mainValue, columnvalue, param, Headerdata, footerdata);
		  		String lStrExportTo = "excelfile";
		  		Map lDetailMap = new HashMap();
				if (("screen").equals(lStrExportTo)) {
					lDetailMap.put("screen", aryOut);
				} else if (("excelfile").equals(lStrExportTo)) {
					lDetailMap.put("excelfile", aryOut);
				}
				
				objectArgs.put("FileName", "Post_Conversion_Details");
				objExport.getExportData(resultObject, lStrExportTo, objectArgs, lDetailMap, false);
				resultObject.setResultValue(objectArgs);
				
    	}catch(Exception e)
    	{
    		resultObject = new ResultObject(ErrorConstants.ERROR);
   		resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			gLogger.info(e);
    	}
    	
    	return resultObject;
    }



}
