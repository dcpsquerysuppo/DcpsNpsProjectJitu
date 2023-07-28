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
import com.tcs.sgv.dcps.dao.DeactivatePranNoDao;
import com.tcs.sgv.dcps.dao.DeactivatePranNoDaoImpl;
import com.tcs.sgv.dcps.dao.FormSubscriber2DAO;
import com.tcs.sgv.dcps.dao.FormSubscriber2DAOImpl;
import com.tcs.sgv.dcps.valueobject.FrmFormS1Dtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;


public class FormSubscriber2ServiceImpl extends ServiceImpl{

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
	public ResultObject loadFormSubscriber2Details(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		String sevaarthId=null;
		String empName=null;
		List lSecAFormS1=null;
		List lSecBFormS1=null;
		List lSecCFormS1=null;
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
		String nomineeName1=null;
		String nomineeDOB1=null;
		String nomineeName2=null;
		String nomineeName3=null;
		String nomineeRel1=null;
		String nomineeRel2=null;
		String nomineeRel3=null;
		String nomineeGuar1=null;
		String nomineeGuar2=null;
		String nomineeGuar3=null;
		String nmn11=null;
		String nmn111=null;
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

		try {
			setSessionInfo(inputMap);
			FormSubscriber2DAO lObjSearchEmployeeDAO = new FormSubscriber2DAOImpl(
					MstEmp.class, serv.getSessionFactory());
			IsDeputation= StringUtility.getParameter("IsDeputation", request).toString();
			gLogger.info("IsDeputation**********+++++:"+IsDeputation);
			if(StringUtility.getParameter("txtSevaarthId", request).toString()!=null || !StringUtility.getParameter("txtSevaarthId", request).toString().equals(""))
				sevaarthId= StringUtility.getParameter("txtSevaarthId", request).toString();
			//for new form S1
			ddoCode = lObjSearchEmployeeDAO.getDDOCode(gStrLocationCode);
			gLogger.info("sevaarthId**********+++++:"+sevaarthId);
			gLogger.info("ddoCode:"+ddoCode);
			//end for new form S1

			if(sevaarthId!=null){
				lObjSearchEmployeeDAO.deleteMultipleRecords(sevaarthId);
				lSecAFormS1= lObjSearchEmployeeDAO.getSectionADetails(sevaarthId);
				lSecBFormS1= lObjSearchEmployeeDAO.getSectionBDetails(sevaarthId,IsDeputation);
				DDTORegNo= lObjSearchEmployeeDAO.getDTORegNo(sevaarthId);
				lSecCFormS1= lObjSearchEmployeeDAO.getSectionCDetails(sevaarthId);
				lNmnCount=lObjSearchEmployeeDAO.checkNmnCount(sevaarthId);

			}
			Object[] tuple = null;
			Iterator it=lSecBFormS1.iterator();


			while(it.hasNext()){
				tuple=(Object[]) it.next();
				office=tuple[5].toString();
				ministry=tuple[6].toString();
				department=tuple[7].toString();
			}
			gLogger.info("Office details"+ office);


			int offSize=office.length();
			int deptSize=department.length();
			int minSize=ministry.length();

			if(offSize>30)
			{
				Off1=office.substring(0, 30);
				gLogger.info("#Off1"+Off1);
				Off2=office.substring(30, offSize);
				gLogger.info("#Off1"+Off2);
			}
			else
			{
				Off1=office.substring(0, offSize);
				Off2=" ";
				gLogger.info("#Off1"+Off2);
			}

			if(deptSize>30){
				dept1=department.substring(0, 30);
				gLogger.info("#dept1"+dept1);
				dept2=department.substring(30, deptSize);
				gLogger.info("#dept2"+dept2);
			}
			else{
				dept1=department.substring(0, deptSize);
				dept2=" ";
				gLogger.info("#dept1"+dept1);
			}


			if(minSize>30){
				min1=ministry.substring(0, 30);
				gLogger.info("#min1"+min1);
				min2=ministry.substring(30, minSize);
				gLogger.info("#min2"+min2);
			}
			else{
				min1=ministry.substring(0, minSize);
				min2=" ";
				gLogger.info("#min1"+min1);
			}


			Object[] tupleA = null;
			Iterator itA=lSecAFormS1.iterator();
			String taxcountry="";
			String usPerson="";
			String taxAddress="";
			String taxCity="";
			String taxState="";
			String taxPin="";
			String tinPan="";
			String tinCountry="";
			
			
			while(itA.hasNext()){
				tupleA=(Object[]) itA.next();
				emailId=tupleA[13].toString();
				gLogger.info("#emailId"+emailId);
				bankAdd=tupleA[17].toString();
				gLogger.info("#bankAdd"+bankAdd);
				pranNo=tupleA[30].toString();
				gLogger.info("#pranNo"+pranNo);
				preFlatno=tupleA[4].toString();
				gLogger.info("#preFlatno"+preFlatno);
				subFullName=tupleA[34].toString();
				gLogger.info("#subFullName"+subFullName);
				if(tupleA[41]!= null)
				{
				taxcountry=tupleA[41].toString();
				}
				gLogger.info("#taxcountry"+taxcountry);
				if(tupleA[40]!= null)
				{
				usPerson=tupleA[40].toString();
				}
				gLogger.info("#usPerson"+usPerson);
				if(tupleA[43]!= null)
				{
				taxCity=tupleA[43].toString();
				}
				gLogger.info("#taxCity"+taxCity);
				if(tupleA[44]!= null)
				{
				taxState=tupleA[44].toString();
				}
				gLogger.info("#taxState"+taxState);
				if(tupleA[45]!= null)
				{
				taxPin=tupleA[45].toString();
				}
				gLogger.info("#taxPin"+taxPin);
				if(tupleA[46]!= null)
				{
				tinPan=tupleA[46].toString();
				}
				gLogger.info("#tinCountry"+tinPan);
				if(tupleA[47]!= null)
				{
				tinCountry=tupleA[47].toString();
				}
				gLogger.info("#tinCountry"+tinCountry);
				if(tupleA[42]!= null)
				{
				taxAddress=tupleA[42].toString();
				}
				gLogger.info("#taxAddress"+taxAddress);
			}
			inputMap.put("taxcountry", taxcountry);
			inputMap.put("usPerson", usPerson);
			inputMap.put("taxCity", taxCity);
			inputMap.put("taxState", taxState);
			inputMap.put("taxPin", taxPin);
			inputMap.put("tinPan", tinPan);
			inputMap.put("tinCountry", tinCountry);
			inputMap.put("taxAddress", taxAddress);
			/*			
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


			Object[] tupleC = null;
			Iterator itC=lSecCFormS1.iterator();
			String TaxCountry=null;
			while(itC.hasNext()){
				tupleC=(Object[]) itC.next();
				nomineeName1=tupleC[0].toString();
				
				nomineeName2=tupleC[6].toString();
				nomineeName3=tupleC[12].toString();
				nomineeRel1=tupleC[2].toString();
				nomineeRel2=tupleC[8].toString();
				nomineeRel3=tupleC[14].toString();
				nomineeGuar1=tupleC[4].toString();
				nomineeGuar2=tupleC[10].toString();
				nomineeGuar3=tupleC[16].toString();
				nomineeDOB1=tupleC[1].toString();
				
			}
			if((nomineeGuar1.equalsIgnoreCase(" ")) || (nomineeGuar1.equalsIgnoreCase("")) )
			{
				nomineeDOB1="";
			
			}
			
			gLogger.info("#nomineeGuar1"+nomineeGuar1+"/");
			gLogger.info("#nomineeDOB1"+nomineeDOB1+"/");
			int nmn1Size=nomineeName1.length();
			int nmn2Size=nomineeName2.length();
			int nmn3Size=nomineeName3.length();
			int nmn1Size1=nomineeName1.length();

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

			if(nmn1Size1>30)
			{
				nmn111=nomineeName1.substring(0, 30);
			}
			else
			{
				nmn111=nomineeName1.substring(0, nmn1Size);
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
*/			
			if(lSecAFormS1!=null && lSecAFormS1.size()>0)
			{
				inputMap.put("lSecAFormS1", lSecAFormS1);

			}
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
			
			inputMap.put("nomineeDOB1", nomineeDOB1);
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

/*
			inputMap.put("nmn111", nmn111);
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
*/			
			inputMap.put("sevaarthId", sevaarthId);
			inputMap.put("pranNo", pranNo);

			inputMap.put("subFullName", subFullName);
			gLogger.info("ddoCode for form S1:"+ddoCode);
			gLogger.info("subFullName"+subFullName);
			// Started for new formS1
			//if(ddoCode.equals("1111222222")){
			resObj.setViewName("newPrintFormSubscriber2");
			//}
			//else

			//resObj.setViewName("NPSPRANFORMS1");
			resObj.setResultValue(inputMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}
	public ResultObject getEmpNameForSubscriber2(
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
			FormSubscriber2DAO lObjSearchEmployeeDAO = new FormSubscriber2DAOImpl(
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
	public ResultObject checkSevaarthInDDO(
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
//		String lddoOffice="";
//		String ddoStatus="";

		try {
			setSessionInfo(inputMap);
			FormSubscriber2DAO lObjSearchEmployeeDAO = new FormSubscriber2DAOImpl(
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
//			ddoStatus=lObjSearchEmployeeDAO.chkFrmUpdatedByLgnDdo(txtSevaarthId);
//			if((ddoStatus!=lStrDDOCode)&&(!ddoStatus.equalsIgnoreCase("Z"))){
//				lddoOffice=ddoStatus;
//				lSBStatus = getResponsecheckSevaarthIdExist(exist,lddoOffice).toString();
//			}

//			if(exist.equals("AVAIL")){
				updation=lObjSearchEmployeeDAO.checkUpdationDone(txtSevaarthId);

//			}
			gLogger.info("Flag is  ********"+updation);

			ddoRegStatus=lObjSearchEmployeeDAO.checkDDORegPresent(lStrDDOCode);
			gLogger.info("ddoRegStatus******"+ddoRegStatus);
			String checkForPpan = lObjSearchEmployeeDAO.checkForPpan(txtSevaarthId);
			gLogger.info("checkForPpan status is ******"+checkForPpan);
			if(checkForPpan.equals("NoPpan"))
			{
				gLogger.info("i m inside checking for ppan***********");
				lSBStatus = getResponsecheckSevaarthIdExist("NoPpan").toString();
				
			}
			else if(updation.equals("blank")){
				lSBStatus = getResponsecheckSevaarthIdExist(updation).toString();
				gLogger.info("in blank*********");

			}
			else if(ddoRegStatus.equals("NO"))
			{
				lSBStatus = getResponsecheckSevaarthIdExist(ddoRegStatus).toString();
				gLogger.info("in NO*********");

			}

			//Added BY roshan
			else if(lObjSearchEmployeeDAO.checkBranchAddress(txtSevaarthId))
			{
				gLogger.info("hi i m insside lObjSearchEmployeeDAO.checkBranchAddress***********");
				lSBStatus = getResponsecheckSevaarthIdExist("BANKNA").toString();
				gLogger.info("in BANKNA*********");

			}
			else if(exist.equals("NA")){
//				lddoOffice=lObjSearchEmployeeDAO.getDdo(txtSevaarthId);
				lSBStatus = getResponsecheckSevaarthIdExist(exist).toString();
				gLogger.info("in NA*********");

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
//		lStrBldXML.append("<DdoFlag>");
//		lStrBldXML.append(ddoFlag);
//		lStrBldXML.append("</DdoFlag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	public ResultObject getEmpListForFormSubscriber2(Map inputMap) throws Exception
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
		//		String depStatus = null;
		try
		{
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			FormSubscriber2DAO lObjSearchEmployeeDAO = new FormSubscriber2DAOImpl(
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

			//		  depStatus=lObjSearchEmployeeDAO.checkEmpListForFrmS1Dep(strDDOCode,flag,txtSearch,IsDeputation);

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

			//				StringBuffer strbuflag = new StringBuffer();
			//				strbuflag.append("<XMLDOC>");
			//				strbuflag.append("<Flag>");
			//				strbuflag.append(depStatus);
			//				strbuflag.append("</Flag>");
			//				strbuflag.append("</XMLDOC>");
			//
			//				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			//
			//				inputMap.put("ajaxKey", lStrResult);
			//				resObj.setViewName("ajaxData");
			//				resObj.setResultValue(inputMap);

			gLogger.info("##################DepSize"+DepSize);    

			inputMap.put("depPrintDdoFlag", depPrintDdoFlag);
			inputMap.put("DepSize", DepSize);
			inputMap.put("IsDeputation", IsDeputation);
			inputMap.put("empList", lstEmpForFrmS1Edit);

			inputMap.put("DDOCode", strDDOCode);
			inputMap.put("empDesigList", empDesigList);
			resObj.setResultValue(inputMap);
			resObj.setViewName("newFormS2");
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


	public ResultObject checkFormUpdatedByLgnDdo(Map inputMap) throws Exception
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
			FormSubscriber2DAO lObjSearchEmployeeDAO = new FormSubscriber2DAOImpl(
					MstEmp.class, serv.getSessionFactory());
			strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			gLogger.info("logged in ddo code: "+strDDOCode);
			txtSearch=StringUtility.getParameter("searchTxt", request);

			CheckFormUpdatedByLgnDDO = lObjSearchEmployeeDAO.chkFrmUpdatedByLgnDdo(txtSearch);
			if(CheckFormUpdatedByLgnDDO.equals("Z")){
				lSBStatus = getResponseUpdatedDdoCode("Z").toString();
			}
			else if(CheckFormUpdatedByLgnDDO.equals("S")){
				lSBStatus = getResponseUpdatedDdoCode("S").toString();
			}
			else{
				if(CheckFormUpdatedByLgnDDO.equals(strDDOCode))
				{
					lSBStatus = getResponseUpdatedDdoCode("Y").toString();
				}else{
					lSBStatus = getResponseUpdatedDdoCode(CheckFormUpdatedByLgnDDO).toString();
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

	public ResultObject getBankDetails(Map inputMap)throws Exception {
		gLogger.info("Inside getBankDetails ");

			ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			String lStrSevaarthId = null;
			String bankAccNo= null;
			String bankName= null;
			String branchName= null;
			String branchAddress= null;
			String bankState= null;
			String bankPincode= null;
			String bankMicrCode= null;
			String bankIfsc= null;
			
			String NotExist="";
			
			try {
			setSessionInfo(inputMap);
			FormSubscriber2DAOImpl lObjcheck = new FormSubscriber2DAOImpl(null,serv.getSessionFactory());
			lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
			gLogger.info("in checkSevaarthId"+" lStrSevaarthId"+lStrSevaarthId);
			
			List existList = lObjcheck.getBankDetails(lStrSevaarthId);

			label:
			if(existList== null || existList.size()==0)
			{
				NotExist="NA";
				  break label;
			}	
			label:
			if(existList!= null && existList.size()>0)
			{			
			Object[] tuple = (Object[]) existList.get(0);
			bankAccNo= tuple[0].toString();
			bankName = tuple[1].toString();
			branchName= tuple[2].toString();
			branchAddress = tuple[3].toString();
			bankState= tuple[7].toString();
			bankPincode = tuple[6].toString();
			bankMicrCode= tuple[5].toString();
			bankIfsc = tuple[4].toString();

			
			}
			
			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<BankAccNo>");
			strbuflag.append(bankAccNo);
			strbuflag.append("</BankAccNo>");
			strbuflag.append("<BankName>");
			strbuflag.append(bankName);
			strbuflag.append("</BankName>");	
			strbuflag.append("<BranchName>");
			strbuflag.append(branchName);
			strbuflag.append("</BranchName>");
			strbuflag.append("<BranchAddress>");
			strbuflag.append(branchAddress);
			strbuflag.append("</BranchAddress>");	
			strbuflag.append("<BankState>");
			strbuflag.append(bankState);
			strbuflag.append("</BankState>");
			strbuflag.append("<BankPincode>");
			strbuflag.append(bankPincode);
			strbuflag.append("</BankPincode>");	
			strbuflag.append("<BankMicrCode>");
			strbuflag.append(bankMicrCode);
			strbuflag.append("</BankMicrCode>");
			strbuflag.append("<BankIfsc>");
			strbuflag.append(bankIfsc);
			strbuflag.append("</BankIfsc>");	
			
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			if(lStrResult != null){
			inputMap.put("ajaxKey", lStrResult);
			}
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
			}
			catch (Exception e) {
				e.printStackTrace();
				gLogger.info("Exception is " + e);
				objRes.setResultValue(null);
				objRes.setThrowable(e);
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setViewName("errorPage");
			}
			return objRes;
			}
	
	public ResultObject getNomineeDetails(Map inputMap)throws Exception {
		gLogger.info("Inside getNomineeDetails ");

			ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			String lStrSevaarthId = null;
			String nomineeName= null;
			String nomineeRelation= null;
			String nomineeDob= null;
			String nomineeGaurdian= null;
			
			String NotExist="";
			
			try {
			setSessionInfo(inputMap);
			FormSubscriber2DAOImpl lObjcheck = new FormSubscriber2DAOImpl(null,serv.getSessionFactory());
			lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
			gLogger.info("in checkSevaarthId"+" lStrSevaarthId"+lStrSevaarthId);
			
			List existList = lObjcheck.getNomineeDetails(lStrSevaarthId);

			label:
			if(existList== null || existList.size()==0)
			{
				NotExist="NA";
				  break label;
			}	
			label:
			if(existList!= null && existList.size()>0)
			{			
			Object[] tuple = (Object[]) existList.get(0);
			nomineeName= tuple[0].toString();
			nomineeDob = tuple[1].toString();
			nomineeRelation= tuple[2].toString();
			nomineeGaurdian = tuple[4].toString();
			
			}
			
			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<NomineeName>");
			strbuflag.append(nomineeName);
			strbuflag.append("</NomineeName>");
			strbuflag.append("<NomineeDob>");
			strbuflag.append(nomineeDob);
			strbuflag.append("</NomineeDob>");	
			strbuflag.append("<NomineeRelation>");
			strbuflag.append(nomineeRelation);
			strbuflag.append("</NomineeRelation>");
			strbuflag.append("<NomineeGaurdian>");
			strbuflag.append(nomineeGaurdian);
			strbuflag.append("</NomineeGaurdian>");				
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			if(lStrResult != null){
			inputMap.put("ajaxKey", lStrResult);
			}
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
			}
			catch (Exception e) {
				e.printStackTrace();
				gLogger.info("Exception is " + e);
				objRes.setResultValue(null);
				objRes.setThrowable(e);
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setViewName("errorPage");
			}
			return objRes;
			}

	
}
