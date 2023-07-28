package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.FormS2DAO;
import com.tcs.sgv.dcps.dao.FormS2DAOImpl;
import com.tcs.sgv.dcps.valueobject.FrmFormS1Dtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;


public class FormS2ServiceImpl extends ServiceImpl{

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

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

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
			e.printStackTrace();
		}
	}
	public ResultObject loadFormS1WithDetails(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		String sevaarthId=null;
		String empName=null;
		List lSecAFormS1=null;
		List lSecBFormS1=null;
		List lSecCFormS1=null;
		List lSecCFormS1New=null;
		String[] SecCtuple = null;
		String[] SecCtuple1 = null;
		List lNmnCount=null;
		List DDTORegNo=null;
		String office=null;
		String department=null;
		String ministry=null;
		String Off1=null;
		String Off2=null;
		String dept1=null;
		String dept2=null;
		String min1=null;
		String min2=null;
		String emailId=null;
		String bankAdd=null;
		String emailId1=null;
		String emailId2=null;
		String bankAdd1=null;
		String bankAdd2=null;
		String nomineeName1="";
		String nomineeName2="";
		String nomineeName3="";
		String dob1=null;
		String dob2=null;
		String dob3=null;
		String per1=null;
		String per2=null;
		String per3=null;
		String cndn1=null;
		String cndn2=null;
		String cndn3=null;
		String nomineeRel1="";
		String nomineeRel2="";
		String nomineeRel3="";
		String nomineeGuar1="";
		String nomineeGuar2="";
		String nomineeGuar3="";
		String nmn11=null;
		String nmn12=null;
		String nmnRel11=null;
		String nmnRel12=null;
		String nmnGuar11=null;
		String nmnGuar12=null;
		String nmn21=null;
		String nmn22=null;
		String nmnRel21=null;
		String nmnRel22=null;
		String nmnGuar21=null;
		String nmnGuar22=null;
		String nmn31=null;
		String nmn32=null;
		String nmnRel31=null;
		String nmnRel32=null;
		String nmnGuar31=null;
		String nmnGuar32=null;
		String ddoCode = "";
        String Pnmn1 = null;String Pnmn2 = null;String Pnmn3 = null;
        String Pnmnage1=null,Pnmnage2=null,Pnmnage3=null,pranNo=null,preFlatno=null;
        String subFullName=null;
        String IsDeputation=null;
        String checkString = null;
        ////$t 16-03-2020 
        String Pan_no=null;
        
		try {
			setSessionInfo(inputMap);
			FormS2DAO lObjSearchEmployeeDAO = new FormS2DAOImpl(
					MstEmp.class, serv.getSessionFactory());
			IsDeputation= StringUtility.getParameter("IsDeputation", request).toString();
			if(StringUtility.getParameter("txtSevaarthId", request).toString()!=null || !StringUtility.getParameter("txtSevaarthId", request).toString().equals(""))
				sevaarthId= StringUtility.getParameter("txtSevaarthId", request).toString();
			//for new form S1
			ddoCode = lObjSearchEmployeeDAO.getDDOCode(gStrLocationCode);
gLogger.info("ddoCode:"+ddoCode);
//end for new form S1

			if(sevaarthId!=null){
				lObjSearchEmployeeDAO.deleteMultipleRecords(sevaarthId);

				lSecAFormS1= lObjSearchEmployeeDAO.getSectionADetailsNew(sevaarthId);
				lSecCFormS1New= lObjSearchEmployeeDAO.getSectionCDetailsNew(sevaarthId);
				gLogger.info("in avail");

				DDTORegNo= lObjSearchEmployeeDAO.getDTORegNo(sevaarthId);
				lNmnCount=lObjSearchEmployeeDAO.checkNmnCount(sevaarthId);
				
			}

			gLogger.info("Office details"+ office);

			Object[] tupleA = null;
			Iterator itA=lSecAFormS1.iterator();

			while(itA.hasNext()){
				tupleA=(Object[]) itA.next();
				emailId=tupleA[13].toString();
				gLogger.info("#emailId"+emailId);
				bankAdd=tupleA[17].toString();
				gLogger.info("#bankAdd"+bankAdd);
			     pranNo=tupleA[30].toString();
			     gLogger.info("#pranNo"+pranNo);
			     ////$t 16-03-2020
			     if(tupleA[36]!=null)
					{
			    	 Pan_no=tupleA[36].toString();
			    	 gLogger.info("#Pan_no here");
					}
					else
					{
						Pan_no="";
						gLogger.info("#Pan_no else here");
					}
			 //// 
			     if(tupleA[4]!=null)
					{
			    	 preFlatno=tupleA[4].toString();
					}
					else
					{
						preFlatno="";
					}
			     gLogger.info("#preFlatno"+preFlatno);
			     if(tupleA[34]!=null)
					{
				     subFullName=tupleA[34].toString();
					}
					else
					{
						subFullName="";
					}
			     gLogger.info("#subFullName"+subFullName);
			}
			int emailIdSize=emailId.length();
			int bankAddSize=bankAdd.length();


			if(emailIdSize>30)
			{
				emailId1=emailId.substring(0, 30);
				emailId2=emailId.substring(30, emailIdSize);
			}
			else
			{
				emailId1=emailId.substring(0, emailIdSize);
				emailId2=" ";
			}

			if(bankAddSize>30){
				bankAdd1=bankAdd.substring(0, 30);
				bankAdd2=bankAdd.substring(30, bankAddSize);
			}
			else{
				bankAdd1=bankAdd.substring(0, bankAddSize);
				bankAdd2=" ";
			}

			
			if(lSecCFormS1New!= null && lSecCFormS1New.size()>0)
			{		
				gLogger.info("in  lSecCFormS1New***************************");
				Object[] tuple = null;
			
			if(lSecCFormS1New.get(0)!= null && lSecCFormS1New.get(0)!="")
			{
			gLogger.info("in if condition *************************");	
			tuple = (Object[]) lSecCFormS1New.get(0);
			nomineeName1=tuple[0].toString();
			dob1=tuple[1].toString();
			if(tuple[2]!=null)
			{
				nomineeRel1=tuple[2].toString();
			}
			else
			{
				nomineeRel1="";
			}
			per1=tuple[3].toString();
			nomineeGuar1=tuple[4].toString();
			cndn1=tuple[5].toString();
			tuple=null;

			}
			if(lSecCFormS1New.size()>1 && lSecCFormS1New.get(1)!= null && lSecCFormS1New.get(1)!=" ")
			{
				tuple = (Object[]) lSecCFormS1New.get(1);
			nomineeName2=tuple[0].toString();
			dob2=tuple[1].toString();
			if(tuple[2]!=null)
			{
				nomineeRel2=tuple[2].toString();
			}
			else
			{
				nomineeRel2="";
			}
			per2=tuple[3].toString();
			nomineeGuar2=tuple[4].toString();
			cndn2=tuple[5].toString();
			tuple=null;
			}		
			
			if(lSecCFormS1New.size()>2 && lSecCFormS1New.get(2)!= null && lSecCFormS1New.get(2)!=" ")
			{
				tuple = (Object[]) lSecCFormS1New.get(2);
			nomineeName3=tuple[0].toString();
			dob3=tuple[1].toString();
			if(tuple[2]!=null)
			{
				nomineeRel3=tuple[2].toString();
			}
			else
			{
				nomineeRel3="";
			}
			per3=tuple[3].toString();
			nomineeGuar3=tuple[4].toString();
			cndn3=tuple[5].toString();
			}
			
			}

			inputMap.put("dob1", dob1);
			inputMap.put("dob2", dob2);
			inputMap.put("dob3", dob3);
			inputMap.put("per1", per1);
			inputMap.put("per2", per2);
			inputMap.put("per3", per3);
			inputMap.put("cndn1", cndn1);
			inputMap.put("cndn2", cndn2);
			inputMap.put("cndn3", cndn3);
			
			int nmn1Size=nomineeName1.length();
			int nmn2Size=nomineeName2.length();
			int nmn3Size=nomineeName3.length();
			
			int nmnRel1=nomineeRel1.length();
			int nmnRel2=nomineeRel2.length();
			int nmnRel3=nomineeRel3.length();
			
			int nmnGuarSize1=nomineeGuar1.length();
			int nmnGuarSize2=nomineeGuar2.length();
			int nmnGuarSize3=nomineeGuar3.length();

			if(nmn1Size>15)
			{
				nmn11=nomineeName1.substring(0, 15);
				nmn12=nomineeName1.substring(15, nmn1Size);
			}
			else
			{
				nmn11=nomineeName1.substring(0, nmn1Size);
				nmn12=" ";
			}

			if(nmn2Size>15){
				nmn21=nomineeName2.substring(0, 15);
				nmn22=nomineeName2.substring(15, nmn2Size);
			}
			else{
				nmn21=nomineeName2.substring(0, nmn2Size);
				nmn22=" ";
			}

			if(nmn3Size>15)
			{
				nmn31=nomineeName3.substring(0, 15);
				nmn32=nomineeName3.substring(15, nmn3Size);
			}
			else
			{
				nmn31=nomineeName3.substring(0, nmn3Size);
				nmn32=" ";
			}

			if(nmnRel1>15){
				nmnRel11=nomineeRel1.substring(0, 15);
				nmnRel12=nomineeRel1.substring(15, nmnRel1);
			}
			else{
				nmnRel11=nomineeRel1.substring(0, nmnRel1);
				nmnRel12=" ";
			}
			if(nmnRel2>15)
			{
				nmnRel21=nomineeRel2.substring(0, 15);
				nmnRel22=nomineeRel2.substring(15, nmnRel2);
			}
			else
			{
				nmnRel21=nomineeRel2.substring(0, nmnRel2);
				nmnRel22=" ";
			}

			if(nmnRel3>15){
				nmnRel31=nomineeRel3.substring(0, 15);
				nmnRel32=nomineeRel3.substring(15, nmnRel3);
			}
			else{
				nmnRel31=nomineeRel3.substring(0, nmnRel3);
				nmnRel32=" ";
			}
			
			
			
			if(nmnGuarSize1>15){
				nmnGuar11=nomineeGuar1.substring(0, 15);
				nmnGuar12=nomineeGuar1.substring(15, nmnGuarSize1);
			}
			else{
				nmnGuar11=nomineeGuar1.substring(0, nmnGuarSize1);
				nmnGuar12=" ";
			}
			if(nmnGuarSize2>15)
			{
				nmnGuar21=nomineeGuar2.substring(0, 15);
				nmnGuar22=nomineeGuar2.substring(15, nmnGuarSize2);
			}
			else
			{
				nmnGuar21=nomineeGuar2.substring(0, nmnGuarSize2);
				nmnGuar22=" ";
			}

			if(nmnGuarSize3>15){
				nmnGuar31=nomineeGuar3.substring(0, 15);
				nmnGuar32=nomineeGuar3.substring(15, nmnGuarSize3);
			}
			else{
				nmnGuar31=nomineeGuar3.substring(0, nmnGuarSize3);
				nmnGuar32=" ";
			}
			
			gLogger.info("Third relation is *******"+nmnRel31);
			
			Object[] check = null;
			Iterator count=lNmnCount.iterator();


			while(count.hasNext()){
				check=(Object[]) count.next();
				Pnmn1=check[0].toString();
				Pnmn2=check[1].toString();
				Pnmn3=check[2].toString();
				Pnmnage1=check[3].toString();
				Pnmnage2=check[4].toString();
				Pnmnage3=check[5].toString();
			}
			
			
			inputMap.put("Pnmn1", Pnmn1);
			inputMap.put("Pnmn2", Pnmn2);
			inputMap.put("Pnmn3", Pnmn3);
			inputMap.put("Pnmnage1", Pnmnage1);
			inputMap.put("Pnmnage2", Pnmnage2);
			inputMap.put("Pnmnage3", Pnmnage3);
			
			FrmFormS1Dtls ffs =new FrmFormS1Dtls();
			
			String preflatNo=ffs.getPresentAddFlatNo();

			if(lSecAFormS1!=null && lSecAFormS1.size()>0)
			{
				inputMap.put("lSecAFormS1", lSecAFormS1);

			}
			gLogger.info("Present flat no is*****"+preFlatno);
			inputMap.put("preFlatno", preFlatno);
			
		    ////$t 16-03-2020 
			inputMap.put("Pan_no", Pan_no);
			//
			
			if(lSecBFormS1!=null && lSecBFormS1.size()>0)
			{
				inputMap.put("lSecBFormS1", lSecBFormS1);	

			}if(lSecCFormS1!=null && lSecCFormS1.size()>0)
			{
				inputMap.put("lSecCFormS1", lSecCFormS1);
			}
            
			Object[] tupleddto = null;
			Iterator ddto=DDTORegNo.iterator();
            String ddoRegNo=null;
            String dtoRegNo=null;
			while(ddto.hasNext()){
				tupleddto=(Object[]) ddto.next();
				dtoRegNo=tupleddto[0].toString(); 
				ddoRegNo=tupleddto[1].toString(); 
				
			}
			
			if(DDTORegNo!=null)
			{
				inputMap.put("dtoRegNo", dtoRegNo);
				inputMap.put("ddoRegNo", ddoRegNo);
			}
		
			inputMap.put("Off1", Off1);
			inputMap.put("Off2", Off2);
			inputMap.put("dept1", dept1);
			inputMap.put("dept2", dept2);
			inputMap.put("min1", min1);
			inputMap.put("min2", min2);
			inputMap.put("emailId1", emailId1);
			inputMap.put("emailId2", emailId2);
			inputMap.put("bankAdd1", bankAdd1);
			inputMap.put("bankAdd2", bankAdd2);
			
			
			inputMap.put("nmn11", nmn11);
			inputMap.put("nmn12", nmn12);
			inputMap.put("nmn21", nmn21);
			inputMap.put("nmn22", nmn22);
			inputMap.put("nmn31", nmn31);
			inputMap.put("nmn32", nmn32);
			inputMap.put("nmnRel11", nmnRel11);
			inputMap.put("nmnRel12", nmnRel12);
			inputMap.put("nmnRel21", nmnRel21);
			inputMap.put("nmnRel22", nmnRel22);
			inputMap.put("nmnRel31", nmnRel31);
			inputMap.put("nmnRel32", nmnRel32);
			inputMap.put("nmnGuar11", nmnGuar11);
			inputMap.put("nmnGuar12", nmnGuar12);
			inputMap.put("nmnGuar21", nmnGuar21);
			inputMap.put("nmnGuar22", nmnGuar22);
			inputMap.put("nmnGuar31", nmnGuar31);
			inputMap.put("nmnGuar32", nmnGuar32);
			inputMap.put("sevaarthId", sevaarthId);
			inputMap.put("pranNo", pranNo);
			
			inputMap.put("subFullName", subFullName);
			gLogger.info("ddoCode for form S1:"+ddoCode);
			gLogger.info("subFullName"+subFullName);
			// Started for new formS1
//if(ddoCode.equals("1111222222")){
	resObj.setViewName("NPSPRANFORMS2NEW");
//}
//else

			//resObj.setViewName("NPSPRANFORMS1");
			resObj.setResultValue(inputMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	public ResultObject getEmpNameForS1AutoCompleteDCPS(
			Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String lStrEmpName = null;
		String lStrSearchBy = null;
		String lStrDDOCode = null;
		String lStrSearchType = null;

		try {
			gLogger.info("Inside getEmpNameForS1AutoCompleteDCPS service");
			setSessionInfo(inputMap);
			FormS2DAO lObjSearchEmployeeDAO = new FormS2DAOImpl(
					MstEmp.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			lStrEmpName = StringUtility.getParameter("searchKey", request)
			.trim();

			lStrSearchBy = StringUtility.getParameter("searchBy", request)
			.trim();

			lStrSearchType = StringUtility.getParameter("searchType", request);


			if (lStrSearchBy.equals("searchFromDDODeSelection")
					|| lStrSearchBy.equals("searchByDDO")) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			}

			gLogger.info("DDO code is ********"+lStrDDOCode);
			finalList = lObjSearchEmployeeDAO.getEmpNameForS1AutoComplete(
					lStrEmpName.toUpperCase(), lStrDDOCode);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();

			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//ex.printStackTrace();
			return objRes;
		}

		return objRes;

	}
	public ResultObject checkSevaarthExistInDDO(
			Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String txtSevaarthId = null;
		String lStrDDOCode = null;
		String exist="NA";
		String empName=null;
		String updation="blank";
		String lSBStatus=null;
		String ddoRegStatus="NO";
		try {
			setSessionInfo(inputMap);
			FormS2DAO lObjSearchEmployeeDAO = new FormS2DAOImpl(
					MstEmp.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			txtSevaarthId = StringUtility.getParameter("empSevarthId", request)
			.trim();
		//	empName = StringUtility.getParameter("txtEmployeeName", request).trim();
			gLogger.info("Sevaarth in service  is ********"+txtSevaarthId);
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);



			exist = lObjSearchEmployeeDAO.checkSevaarthIdExist(txtSevaarthId,lStrDDOCode);
			gLogger.info("Flag is  ********"+exist);
			
			if(exist.equals("AVAIL")){
				updation=lObjSearchEmployeeDAO.checkUpdationDone(txtSevaarthId);
				}
			gLogger.info("Flag is  ********"+updation);
			
			ddoRegStatus=lObjSearchEmployeeDAO.checkDDORegPresent(lStrDDOCode);
			
			if(exist.equals("NA")){
			 lSBStatus = getResponsecheckSevaarthIdExist(exist).toString();
			}
			else if(updation.equals("blank")){
				 lSBStatus = getResponsecheckSevaarthIdExist(updation).toString();
			 }
			else if(ddoRegStatus.equals("NO"))
			{
				 lSBStatus = getResponsecheckSevaarthIdExist(ddoRegStatus).toString();
			}
			
			//Added BY roshan
			else if(lObjSearchEmployeeDAO.checkBranchAddress(txtSevaarthId))
			{
				gLogger.info("hi i m insside***********");
				 lSBStatus = getResponsecheckSevaarthIdExist("BANKNA").toString();
			}
			//ended by roshan
			else{
				 lSBStatus = getResponsecheckSevaarthIdExist("eligible").toString();
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");

			objRes.setResultValue(inputMap);

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//ex.printStackTrace();
			return objRes;
		}

		return objRes;

	}

	private StringBuilder getResponsecheckSevaarthIdExist(String flag) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	public ResultObject getEmpListForFormS(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstEmpForFrmS1Edit=null;
		List empDesigList=null;
		String strDDOCode=null;
		String txtSearch=null;
		String flag=null;
		String sevarthId=null;
		String IsDeputation = null;
		int DepSize = 100;
		String depPrintDdoFlag="N";
		try
		{
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			FormS2DAO lObjSearchEmployeeDAO = new FormS2DAOImpl(
					MstEmp.class, serv.getSessionFactory());
			depPrintDdoFlag=StringUtility.getParameter("depPrintDdoFlag", request);
			strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			sevarthId=StringUtility.getParameter("sevarthId", request);
			gLogger.info("logged in ddo code: "+strDDOCode);
			IsDeputation = StringUtility.getParameter((String)"IsDeputation", (HttpServletRequest)this.request);
			txtSearch=StringUtility.getParameter("searchTxt", request);
			flag=StringUtility.getParameter("flag", request);
			if(sevarthId!=null && !sevarthId.equals("")){
				txtSearch=sevarthId;
			}
			  this.gLogger.info((Object)("IsDeputation is Y#########"+IsDeputation));
	            if(IsDeputation.equals("Y"))
	            { this.gLogger.info((Object)("txtSearch is Y in if#########"+txtSearch));
	            this.gLogger.info((Object)("IsDeputation is Y#########"+IsDeputation));
	            	 if (txtSearch != null && !txtSearch.equals("")) {
	            	lstEmpForFrmS1Edit=lObjSearchEmployeeDAO.getEmpListForFrmS1Edit(strDDOCode,flag,txtSearch,IsDeputation);
	    			empDesigList=lObjSearchEmployeeDAO.getEmpDesigList(strDDOCode);
	    			DepSize=lstEmpForFrmS1Edit.size();
	            	 }
	            	 }else
        {
	           
			lstEmpForFrmS1Edit=lObjSearchEmployeeDAO.getEmpListForFrmS1Edit(strDDOCode,flag,txtSearch,IsDeputation);
			empDesigList=lObjSearchEmployeeDAO.getEmpDesigList(strDDOCode);
		
        }
	            
	            gLogger.info("##################DepSize"+DepSize);
	            
	            inputMap.put("depPrintDdoFlag", depPrintDdoFlag);
	    inputMap.put("DepSize", DepSize);
	    inputMap.put("IsDeputation", IsDeputation);
        inputMap.put("empList", lstEmpForFrmS1Edit);
        
			inputMap.put("DDOCode", strDDOCode);
			inputMap.put("empDesigList", empDesigList);
			resObj.setResultValue(inputMap);
			resObj.setViewName("empListForFormS2");
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}
	
	
	public ResultObject chkFrmUpdatedByLgnDdo(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstEmpForFrmS1Edit=null;
		List empDesigList=null;
		String strDDOCode=null;
		String txtSearch=null;
		String flag=null;
		String sevarthId=null;
		String IsDeputation = null;
		int DepSize = 100;
		String CheckFormUpdatedByLgnDDO=null;
		String lSBStatus=null;
		
		try
		{
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			FormS2DAO lObjSearchEmployeeDAO = new FormS2DAOImpl(
					MstEmp.class, serv.getSessionFactory());
				strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
				gLogger.info("logged in ddo code: "+strDDOCode);
						txtSearch=StringUtility.getParameter("searchTxt", request);
						
						CheckFormUpdatedByLgnDDO = lObjSearchEmployeeDAO.chkFrmUpdatedByLgnDdo(txtSearch,strDDOCode);
						if(CheckFormUpdatedByLgnDDO.equals("Z")){
							lSBStatus = getResponseUpdatedDdoCode("Z").toString();
						}else{
						if(CheckFormUpdatedByLgnDDO.equals(strDDOCode))
						{
						lSBStatus = getResponseUpdatedDdoCode("Y").toString();
						}else{
						lSBStatus = getResponseUpdatedDdoCode("N").toString();
						}
						}
		
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
				lSBStatus).toString();
		inputMap.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");

		resObj.setResultValue(inputMap);
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}
	
	private StringBuilder getResponseUpdatedDdoCode(String flag) {
		gLogger.info("Flag is  in AJAX********"+flag);
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
}
