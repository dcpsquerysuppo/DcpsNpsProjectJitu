package com.tcs.sgv.common.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.acegilogin.event.HandleApplicationEvent;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.acl.login.valueobject.ResetPswdCustom;
import com.tcs.sgv.acl.valueobject.AclElementMst;
import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleElementRlt;
import com.tcs.sgv.acl.valueobject.AclUserElementRlt;
import com.tcs.sgv.acl.valueobject.AclUserRoleRlt;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.BptmCommonServicesDAO;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.IFMSCommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.common.valueobject.RltDdoBilltype;
import com.tcs.sgv.common.valueobject.RltDdoOffice;
import com.tcs.sgv.common.valueobject.RltDdoOrg;
import com.tcs.sgv.common.valueobject.RltLevelStatus;
import com.tcs.sgv.common.valueobject.RltLocationDepartment;
import com.tcs.sgv.common.valueobject.SgvaBudbpnMapping;
import com.tcs.sgv.common.valueobject.SgvaBuddemandMst;
import com.tcs.sgv.common.valueobject.SgvaBuddtlhdMst;
import com.tcs.sgv.common.valueobject.SgvaBudgrantMst;
import com.tcs.sgv.common.valueobject.SgvaBudminhdMst;
import com.tcs.sgv.common.valueobject.SgvaBudmjrhdMst;
import com.tcs.sgv.common.valueobject.SgvaBudsubhdMst;
import com.tcs.sgv.common.valueobject.SgvaBudsubmjrhdMst;
import com.tcs.sgv.common.valueobject.SgvaExpestWrkMst;
import com.tcs.sgv.common.valueobject.SgvaGrantOrderDetail;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.FrmActionServiceRlt;
import com.tcs.sgv.core.valueobject.FrmServiceMst;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.ess.valueobject.OrgUserpostRlt;
import com.tcs.sgv.fms.valueobject.WfActDocRuleMpg;
import com.tcs.sgv.fms.valueobject.WfActionMst;
import com.tcs.sgv.fms.valueobject.WfAlternateflowActionMst;
import com.tcs.sgv.fms.valueobject.WfHierachyAlternateflowMpg;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;
import com.tcs.sgv.fms.valueobject.WfHierarchyReferenceMst;
import com.tcs.sgv.fms.valueobject.WfOrgLocMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgPostMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgUsrMpgMst;
/*import com.tcs.sgv.pension.valueobject.MstPensionAuthority;
import com.tcs.sgv.pension.valueobject.MstPensionDept;
import com.tcs.sgv.pension.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pension.valueobject.MstPensionRop1986;
import com.tcs.sgv.pension.valueobject.MstPensionRop1996;
import com.tcs.sgv.pension.valueobject.MstPensionRop2006;*/
import com.tcs.sgv.wf.valueobject.InboxSearchVo;

public class IFMSCommonServiceImpl extends ServiceImpl implements IFMSCommonService {
	
	static Log logger = LogFactory.getLog(IFMSCommonServiceImpl.class);
	
	private static SimpleDateFormat DtToStrFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	
	public ResultObject seeUserManual(Map inputMap){		
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		String fileName = "User_Manual_IFMS.doc";//default
		try
		{
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			fileName = StringUtility.getParameter("fileName", request);
		}
		catch(Exception e)
		{
			logger.error("filename not found for usermanual");
		}
		inputMap.put("fileName", fileName);
		resultObject.setViewName("ShowDocument");
		resultObject.setResultValue(inputMap);	
		return resultObject;
	}
	
	/**
	 * This method will generate list of lookup values based on lookup name.
	 * 
	 * @param String : lStrLookupName
	 * @param Long : lLngLangId
	 * @param Map : inputMap
	 * @return List
	 * 
	 */

	public static List getLookupValues(String lStrLookupName, Long lLngLangId, Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		try{
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			/*CmnLookupMstDAOImpl lObjCmnLookupDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());
			return lObjCmnLookupDAOImpl.getAllChildrenByLookUpNameAndLang(lStrLookupName, lLngLangId);*/
			BptmCommonServicesDAOImpl bptmCommonServicesDAOImpl = new BptmCommonServicesDAOImpl(CmnLookupMst.class, serv.getSessionFactory());
			return bptmCommonServicesDAOImpl.getAllChildrenByLookUpNameAndLang(lStrLookupName, lLngLangId);
		}catch (Exception ex) {
			setErrorProperties(logger, objRes, ex, "Error occured while getting lookup values...");
			throw ex;
		}
		
	}
	
	/**
	 * This method will generate a sequence number in an auto-incremental mode.
	 * 
	 * @param String : tableName
	 * @param Map : inputMap
	 * @return Long
	 */
	public static Long getNextSeqNum(String lStrTableName, Map inputMap) throws Exception{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		try {
			if (objRes != null) {
				return (long)IDGenerateDelegate.getNextId(lStrTableName, inputMap);
			}
		} catch (Exception ex) {
			setErrorProperties(logger, objRes, ex, "Error occured while getting next sequence number...");
			throw ex;
			
		}
		return (long) -1;
	}
	
	/**
	 * This method will generate a sequence number in an auto-incremental (batch) mode 
	 * & increment will be equal to the count 
	 * 
	 * @param String : tableName
	 * @param Map : inputMap
	 * @return Long
	 * @author jay
	 */
	public static Long getNextSeqNumByCount(String lStrTableName, Map inputMap) throws Exception{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		try {
			if (objRes != null) {
				return (long)IDGenerateDelegate.getCurrentCount_genNextIdByCount(lStrTableName, inputMap);
			}
		} catch (Exception ex) {
			setErrorProperties(logger, objRes, ex, "Error occured while getting next sequence number...");
			throw ex;
			
		}
		return (long) -1;
	}
	
	/**
	 * This method will return current sequence number and increment sequence number by count that we have passed
	 * 
	 * @param String : tableName
	 * @param Map : inputMap
	 * @param int : iCount
	 * @return Long
	 */
	public static Long getCurrentSeqNumAndUpdateCount(String lStrTableName, Map inputMap, int iCount) throws Exception{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		try {
			/*Map loginMap = (Map)inputMap.get("baseLoginMap");
			CmnLocationMst cmnLocationMst = (CmnLocationMst)loginMap.get("locationVO");*/
			inputMap.put("CmnLocationMstDst", SessionHelper.getLocationVO(inputMap));  // 1st pre-requisite property
			inputMap.put("counter", iCount) ; // 2nd pre-requisite property
			
			if (objRes != null) {
				return IDGenerateDelegate.getCurrentCount_genNextIdByCount(lStrTableName,inputMap);
			}
		} catch (Exception ex) {
			setErrorProperties(logger, objRes, ex, "Error occured while getting current sequence number and increment sequence number by count that we have passed ...");
			throw ex;
			
		}
		return (long) -1;
	}
	
	/*
	 *  This method will return formatted pk to insert from sequence number
	 *  
	 *  @param Long : lSeqNum
	 *  @param Map : inputMap
	 *  @ return Long
	 */
	
	public static Long getFormattedPrimaryKey( Long lSeqNum,Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		try{
			if (objRes != null) {
				return Long.parseLong( String.valueOf(SessionHelper.getDbId(inputMap)) + String.valueOf(SessionHelper.getLocationId(inputMap)) + String.valueOf(lSeqNum) );
			}
		}catch (Exception ex) {
			setErrorProperties(logger, objRes, ex, "Error occured while formatting primary key !");
			throw ex;
		}
		return (long) -1;
	}
	
	/**
	 * This method will return lookup id from lookup name
	 * 
	 * @param String : lookupName
	 * @param Map : inputMap
	 * @return String
	 */
	public static String getLookupIdFromName(String lookupName, Map inputMap) throws Exception{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		String lStrLookupId = "";
		try {
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			Long langId = SessionHelper.getLangId(inputMap);
			BptmCommonServicesDAO lBptmObj = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			lStrLookupId = lBptmObj.getLookupIdFromName(lookupName, langId);
		} catch (Exception ex) {
			setErrorProperties(logger, objRes, ex, "Error occured while getting lookup id from lookup name...");
			throw ex;
			
		}
		return lStrLookupId;
	}
	
	public static void setErrorProperties(Log lLogger, ResultObject objRes, Exception ex,  String lStrErrorLog){
		
		objRes.setResultValue(null);
		objRes.setThrowable(ex);
		objRes.setResultCode(ErrorConstants.ERROR);
		objRes.setViewName("errorPage");
		lLogger.error(lStrErrorLog + ex, ex);
		
	}
	public static Map<Object,Object> getDisplayPara(HttpServletRequest request)
    {
    	Map<Object,Object> returnMap = new HashMap<Object,Object>();
    	
    	String sOrderColumn = request.getParameter( new ParamEncoder("vo").encodeParameterName(TableTagParameters.PARAMETER_SORT));
    	String sPageNo = request.getParameter( new ParamEncoder("vo").encodeParameterName(TableTagParameters.PARAMETER_PAGE));
    	String sOrder = request.getParameter( new ParamEncoder("vo").encodeParameterName(TableTagParameters.PARAMETER_ORDER));
    	
    	
    	if(sOrderColumn != null)
    		returnMap.put(Constants.KEY_SORT_PARA,Integer.valueOf(sOrderColumn));
    	if(sPageNo !=null)
    		returnMap.put(Constants.KEY_PAGE_NO,Integer.valueOf(sPageNo));
    	else
    		returnMap.put(Constants.KEY_PAGE_NO,Integer.valueOf(1));
    	
    	if(sOrder !=null)
    	{
    		if(sOrder.equalsIgnoreCase("1"))
    			returnMap.put(Constants.KEY_SORT_ORDER,"asc");
    		else
    			returnMap.put(Constants.KEY_SORT_ORDER,"desc");
    	}
    	return returnMap;
    }
	
	public static Map<Object,Object> getDisplayParaRcpt(HttpServletRequest request)
    {
    	Map<Object,Object> returnMap = new HashMap<Object,Object>();
    	
    	String sOrderColumn = request.getParameter( new ParamEncoder("vo").encodeParameterName(TableTagParameters.PARAMETER_SORT));
    	String sPageNo = request.getParameter( new ParamEncoder("vo").encodeParameterName(TableTagParameters.PARAMETER_PAGE));
    	String sOrder = request.getParameter( new ParamEncoder("vo").encodeParameterName(TableTagParameters.PARAMETER_ORDER));
    	
    	
    	if(sOrderColumn != null)
    		returnMap.put(Constants.KEY_SORT_PARA,Integer.valueOf(sOrderColumn));
    	if(sPageNo !=null)
    		returnMap.put(Constants.KEY_PAGE_NO,Integer.valueOf(sPageNo));
    	else
    		returnMap.put(Constants.KEY_PAGE_NO,Integer.valueOf(1));
    	
    	if(sOrder !=null && sOrder.equalsIgnoreCase("1"))
    		returnMap.put(Constants.KEY_SORT_ORDER,"asc");
    	else
    		returnMap.put(Constants.KEY_SORT_ORDER,"desc");
	
    	return returnMap;
    }
	/**
	 * Method that returns the page number.
	 * 
	 * @param request
	 * @param key
	 * @return Integer
	 */
	public static Integer getPageNumber(HttpServletRequest request, String key){
		
		if(request.getParameter( new ParamEncoder(key).encodeParameterName(TableTagParameters.PARAMETER_PAGE)) != null)
			return Integer.parseInt(request.getParameter(new ParamEncoder(key).encodeParameterName(TableTagParameters.PARAMETER_PAGE)));
		else
			return 1;
		
	}
	
	/**
	 * Method that returns the column index on which sorting requires
	 * 
	 * @param request
	 * @param key
	 * @return Integer
	 */
	public static Integer getSortIndex(HttpServletRequest request, String key){
		
		if(request.getParameter( new ParamEncoder(key).encodeParameterName(TableTagParameters.PARAMETER_SORT)) != null)
			return Integer.parseInt(request.getParameter(new ParamEncoder(key).encodeParameterName(TableTagParameters.PARAMETER_SORT)));
		else
			return -1;
		
	}
	
	/**
	 * Method that returns the order type for sorting. 1 - asc, 2- desc
	 * 
	 * @param request
	 * @param key
	 * @return Integer
	 */
	public static Integer getOrderType(HttpServletRequest request, String key){
		
		if(request.getParameter( new ParamEncoder(key).encodeParameterName(TableTagParameters.PARAMETER_ORDER)) != null)
			return Integer.parseInt(request.getParameter(new ParamEncoder(key).encodeParameterName(TableTagParameters.PARAMETER_ORDER)));
		else
			return 1;
		
	}

	/**
	 * Returns the date object from string (String format : 'DD/MM/YYYY');
	 * @param String : Date
	 * @return Date
	 */
	public static Date getDateFromString(String lStrDate) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.parse(lStrDate);
		
	}
	/**
	 * Returns the String object from Date (String format : 'DD/MM/YYYY');
	 * @param String : Date
	 * @return Date
	 */
	public static String getStringFromDate(Date lObjDate) throws ParseException {
		
		if(lObjDate != null)
			return DtToStrFormatter.format(lObjDate);

		return null;		
	}
	/**
	 * Returns the Next date from string (String format : 'DD/MM/YYYY');
	 * @param String : Date
	 * @return Date
	 */
	public static Date getIncrementedDateFromString(String lStrDate) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");		
		Calendar c = Calendar.getInstance();
		c.setTime(sdf.parse(lStrDate));
		c.add(Calendar.DAY_OF_MONTH,1);
		lStrDate = DtToStrFormatter.format(c.getTime());
		return sdf.parse(lStrDate);		
	}
	/**
	 * Returns the Next date from Date (format : 'DD/MM/YYYY');
	 * @param Date : Date
	 * @return Date
	 */
	public static Date getIncrementedDateFromDate(Date date) throws ParseException{						
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH,1);
		String lStrDate = DtToStrFormatter.format(c.getTime());
		return sdf.parse(lStrDate);		
	}
	/**
	 * Returns the formatted date object with minimum hour, minute and second
	 * @param String : Date
	 * @return Date
	 */
	public static Date getStartDateTime(Date lDate){
				
		Calendar lCal = Calendar.getInstance();
		
		lCal.setTime(lDate);
		lCal.set(Calendar.HOUR, 0);
		lCal.set(Calendar.MINUTE, 0);
		lCal.set(Calendar.SECOND, 0);
		
		return lCal.getTime();
	}
	
	/**
	 * Returns the formatted date object with maximum hour, minute and second
	 * @param String : Date
	 * @return Date
	 */
	public static Date getEndDateTime(Date lDate){
		
		Calendar lCal = Calendar.getInstance();		
		
		lCal.setTime(lDate);
		lCal.set(Calendar.HOUR, 23);
		lCal.set(Calendar.MINUTE, 59);
		lCal.set(Calendar.SECOND, 59);
				
		return lCal.getTime();
		
	}
	
	/**
	 * Method to get padded string with total length and char to be padded as given in arguments.
	 * 
	 * @param inputStr 		: Input String
	 * @param requiredLen 	: Total length of the output string
	 * @param charToPadd 	: Character to pad
	 * @param paddingFlag 	: true - Left Padding, false - Right Padding
	 * @return outStr 		: Padded String
	 */
	public static String getPaddedString(String inputStr, int requiredLen, char charToPadd, boolean paddingFlag) {
		StringBuffer outStr = new StringBuffer(inputStr);
		int strLength = outStr.length();
		if (requiredLen > 0 && requiredLen > strLength) {
			for (int i = 0; i <= requiredLen; i++) {
				if (paddingFlag) {
					if (i < requiredLen - strLength)
						outStr.insert(0, charToPadd);
				} else {
					if (i > strLength)
						outStr.append(charToPadd);
				}
			}
		}
		return outStr.toString();
	}
	
	public static InboxSearchVo getInboxSearchVo(Long lLngPostId, Long lLngUserId, Long lLngLangId) throws Exception
    {
		InboxSearchVo inboxSearchVo = new InboxSearchVo();
		List lLstPostId = new ArrayList();
		List lLstDocId = new ArrayList();
		
		try {
			lLstPostId.add(lLngPostId.toString());
			inboxSearchVo.setLstPost(lLstPostId);
			
			lLstDocId.add(DBConstants.WF_DocId_PhysicalBill);
			lLstDocId.add(DBConstants.WF_DocId_OnlineBill);
			lLstDocId.add(DBConstants.WF_DocId_PensionBill);
			inboxSearchVo.setDocLst(lLstDocId);
			
			inboxSearchVo.setLangId(lLngLangId.toString());			
			inboxSearchVo.setExecuteFlag(Boolean.FALSE);
			inboxSearchVo.setUserId(lLngUserId);
		} catch (Exception e) {
			logger.error("Error while creating InboxSearchVo(String lStrPostId, String lStrUserId, String lStrLangId). Error is ", e);
			throw e;
		}
		
		return inboxSearchVo;
    }
	
	public static InboxSearchVo getInboxSearchVo(Long lLngPostId, Long lLngUserId, List lLstdocId, Long lLngLangId) throws Exception
    {
		InboxSearchVo inboxSearchVo = new InboxSearchVo();
		List lLstPostId = new ArrayList();
		
		try {
			lLstPostId.add(lLngPostId.toString());
			inboxSearchVo.setLstPost(lLstPostId);			
			inboxSearchVo.setDocLst(lLstdocId);			
			inboxSearchVo.setLangId(lLngLangId.toString());			
			inboxSearchVo.setExecuteFlag(Boolean.FALSE);
			inboxSearchVo.setUserId(lLngUserId);
		} catch (Exception e) {
			logger.error("Error while creating InboxSearchVo(String lStrPostId, String lStrUserId, List lLstdocId, String lStrLangId). Error is ", e);
			throw e;
		}
		
		return inboxSearchVo;
    }
	
	public static String getDocId(TrnBillRegister lObjTrnBillRegister) throws Exception {

		String lStrDocId = null;

		try {
			if (lObjTrnBillRegister.getPhyBill() == DBConstants.CMN_PhyBillType) {
				lStrDocId = DBConstants.WF_DocId_PhysicalBill;
			} else if (lObjTrnBillRegister.getPhyBill() == DBConstants.CMN_PensionBillType) {
				lStrDocId = DBConstants.WF_DocId_PensionBill;
			} else {
				lStrDocId = DBConstants.WF_DocId_OnlineBill;
			}
		} catch (Exception e) {
			logger.error("Error while executing getDocId. Error is ", e);
			throw e;
		}

		return lStrDocId;
	}
	
	public static Date getNewDate(Date date, Character type, Integer factor) throws Exception{
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		if(type.equals(DATE)){
			c.add(Calendar.DATE, factor);
		}else if(type.equals(MONTH)){
			c.add(Calendar.MONTH, factor);
		}else if(type.equals(YEAR)){
			c.add(Calendar.YEAR, factor);
		}		
		return c.getTime();		
	}
	
	
	public ResultObject clearCache(Map inputMap)
    {
    	ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
    	
    	try {
    		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");   

    		SessionFactory sessionFactory = (SessionFactory)serv.getSessionFactory();
    		SessionFactory sessionFactorySlave = (SessionFactory)serv.getSessionFactorySlave();
    		
    		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");    		
    		String cacheRegion = (String) inputMap.get("cacheRegion");
    		
    		if(cacheRegion == null && request != null)
    			cacheRegion = StringUtility.getParameter("cacheRegion", request);    			
    		
    		if("ddo".equalsIgnoreCase(cacheRegion)){
    			clearDdoQueryCache(sessionFactory);
    			clearDdoDataCache(sessionFactory);  			
    		} else if("budget".equalsIgnoreCase(cacheRegion)){
    			clearBudgetCache(sessionFactory);    			
    		} else if("lookup".equalsIgnoreCase(cacheRegion)){
    			clearCommonCache(sessionFactory);
    			clearCommonCache(sessionFactorySlave);
    		} else if("wf".equalsIgnoreCase(cacheRegion)){
    			clearWfCache(sessionFactory);
    		} else if("pension".equalsIgnoreCase(cacheRegion)){
    			clearPensionCache(sessionFactory);
    		} else if("services".equalsIgnoreCase(cacheRegion)){
    			clearServiceActionCache(sessionFactory);
    			clearServiceActionCache(sessionFactorySlave);
    		} else if("acl".equalsIgnoreCase(cacheRegion)){
    			//clearAclCache(sessionFactory);
    			clearObjectCache(sessionFactory);
    			clearQueryCache(sessionFactory);
    			//clearAclCache(sessionFactorySlave);
    			clearObjectCache(sessionFactorySlave);
    			clearQueryCache(sessionFactorySlave);    			
    		} else if("all".equalsIgnoreCase(cacheRegion)){
    			clearAllCache(sessionFactory);
    			clearAllCache(sessionFactorySlave);
    		}
    		
            /*Map redirectMap = new HashMap();
            redirectMap.put("actionFlag", "getDisplayMessage");
            redirectMap.put("MESSAGECODE", new Long(10001));
            inputMap.put("redirectMap", redirectMap);*/
    		   		
    		resObj.setResultValue(inputMap);    		
			resObj.setViewName("homePage");
		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in clearCache " + e, e);
		}
		return resObj;
    }
	
	public void clearAllCache(SessionFactory sessionFactory) throws Exception
	{
		try {
						
			clearCommonCache(sessionFactory);
			//clearDdoQueryCache(sessionFactory);
			//clearBudgetCache(sessionFactory);
			//clearDdoDataCache(sessionFactory);
			//clearWfCache(sessionFactory);
			//clearPensionCache(sessionFactory);
			clearServiceActionCache(sessionFactory);
			//clearAclCache(sessionFactory);
			clearObjectCache(sessionFactory);
			//clearQueryCache(sessionFactory);
		}
		catch(Exception ex) {
			logger.error(" Error in clearAllCache ", ex);
			throw ex;
		}
	}
	
	
	private void clearObjectCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			//CacheManager cacheManager = CacheManager.getInstance();
			//Cache cache = cacheManager.getCache("ecache_object");
			//this will remove any programmatically cached data
			//cache.removeAll();
			//evict queries will remove the query result sets from query cache
			
			CacheManager cacheManager = CacheManager.getInstance();
			
			Cache cacheUtilityObj = cacheManager.getCache("CacheUtility");
			cacheUtilityObj.removeAll();
			
			Cache cacheQry = cacheManager.getCache("ecache_query");
			cacheQry.removeAll();
			sessionFactory.evictQueries("ecache_query");
			
			sessionFactory.evictQueries("ecache_object");
			sessionFactory.evictQueries();
			//evict will remove the object from second-level cache  
			sessionFactory.evict(OrgUserMst.class);
			sessionFactory.evict(OrgEmpMst.class);
			sessionFactory.evict(OrgPostMst.class);
			sessionFactory.evict(OrgUserpostRlt.class);
			sessionFactory.evict(OrgPostDetailsRlt.class);
			
			
			sessionFactory.evict(AclPostroleRlt.class);
			sessionFactory.evict(AclUserRoleRlt.class);
			sessionFactory.evict(AclUserElementRlt.class);
			sessionFactory.evict(AclRoleElementRlt.class);
			sessionFactory.evict(AclElementMst.class);
					
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------ACL Object CACHE CLEARED------------------");
	}
	
	private void clearQueryCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			//CacheManager cacheManager = CacheManager.getInstance();
			//Cache cache = cacheManager.getCache("ecache_query");
			//this will remove any programmatically cached data
			//cache.removeAll();
			//evict queries will remove the query result sets from query cache
			sessionFactory.evictQueries("ecache_query");
			
			//evict will remove the object from second-level cache  
					
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------ACL Query CACHE CLEARED------------------");
	}
	
	
	private void clearServiceActionCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache("CacheUtility");
			//this will remove any programmatically cached data
			cache.removeAll();
			//evict queries will remove the query result sets from query cache
			sessionFactory.evictQueries("CacheUtility");
			
			//evict will remove the object from second-level cache  
			sessionFactory.evict(FrmServiceMst.class);
			sessionFactory.evict(FrmActionServiceRlt.class);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------SERVICE CACHE CLEARED------------------");
	}
	
	private void clearAclCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache("ecache_ac");
			//this will remove any programmatically cached data
			cache.removeAll();
			//evict queries will remove the query result sets from query cache
			sessionFactory.evictQueries("ecache_ac");
			
			//evict will remove the object from second-level cache  
			sessionFactory.evict(AclElementMst.class);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------ACL CACHE CLEARED------------------");
	}

	private void clearWfCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache("wfCache");
			//this will remove any programmatically cached data
			cache.removeAll();
			//evict queries will remove the query result sets from query cache
			sessionFactory.evictQueries("wfCache");
			
			//evict will remove the object from second-level cache  
			sessionFactory.evict(WfActionMst.class);
			sessionFactory.evict(WfActDocRuleMpg.class);
			sessionFactory.evict(WfAlternateflowActionMst.class);
			sessionFactory.evict(WfHierachyAlternateflowMpg.class);
			sessionFactory.evict(WfHierachyPostMpg.class);
			sessionFactory.evict(WfHierarchyReferenceMst.class);
			sessionFactory.evict(WfOrgLocMpgMst.class);
			sessionFactory.evict(WfOrgPostMpgMst.class);
			sessionFactory.evict(WfOrgUsrMpgMst.class);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------WF CACHE CLEARED------------------");
	}
	
	
	private void clearPensionCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache("pensionCache");
			//this will remove any programmatically cached data
			cache.removeAll();
			//evict queries will remove the query result sets from query cache
			/*sessionFactory.evictQueries("pensionCache");
			
			//evict will remove the object from second-level cache  
			sessionFactory.evict(MstPensionAuthority.class);
			sessionFactory.evict(MstPensionDept.class);
			sessionFactory.evict(MstPensionHeadcode.class);
			sessionFactory.evict(MstPensionRop1986.class);
			sessionFactory.evict(MstPensionRop1996.class);
			sessionFactory.evict(MstPensionRop2006.class);*/
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------PENSION CACHE CLEARED------------------");
	}

	
	private void clearDdoQueryCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache("ddoCache");
			cache.removeAll();
			//evict queries will remove the query result sets from query cache
			sessionFactory.evictQueries("ddoCache");
			
			//evict will remove the object from second-level cache  
			sessionFactory.evict(OrgDdoMst.class);
			sessionFactory.evict(RltDdoOrg.class);
			sessionFactory.evict(RltDdoBilltype.class);
			sessionFactory.evict(RltDdoOffice.class);
			sessionFactory.evict(RltLocationDepartment.class);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------DDO CACHE CLEARED------------------");
	}
	
	private void clearBudgetCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache("budgetCache");
			cache.removeAll();
			//evict queries will remove the query result sets from query cache
			sessionFactory.evictQueries("budgetCache");
			
			//evict will remove the object from second-level cache
			sessionFactory.evict(SgvaBudsubhdMst.class);
			sessionFactory.evict(SgvaBudgrantMst.class);
			sessionFactory.evict(SgvaBudsubmjrhdMst.class);			
			sessionFactory.evict(SgvaBudminhdMst.class);
			sessionFactory.evict(SgvaBudmjrhdMst.class);
			sessionFactory.evict(SgvaBuddtlhdMst.class);			
			sessionFactory.evict(SgvaBuddemandMst.class);
			sessionFactory.evict(SgvcFinYearMst.class);
			sessionFactory.evict(SgvaGrantOrderDetail.class);
			sessionFactory.evict(SgvaExpestWrkMst.class);
			sessionFactory.evict(SgvaBudbpnMapping.class);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------BUDGET CACHE CLEARED------------------");
	}
	
	private void clearCommonCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache("ecache_lookup");
			cache.removeAll();
			//evict queries will remove the query result sets from query cache
			sessionFactory.evictQueries("ecache_lookup");			
			//evict will remove the object from second-level cache    	
			sessionFactory.evict(CmnLookupMst.class);
			sessionFactory.evict(RltLevelStatus.class);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------COMMON(ECACHE) CACHE CLEARED------------------");
	}
	
	private void clearDdoDataCache(SessionFactory sessionFactory) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache ddoDataCache = cacheManager.getCache("ddoDataCache");
			ddoDataCache.removeAll();
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("--------------- DDO Data CACHE CLEARED------------------");
	}	
	
	public static void clearCachedDdoDetails(SessionFactory sessionFactory,String key) throws Exception{
		
		try {
			CacheManager cacheManager = CacheManager.getInstance();
			Cache ddoDataCache = cacheManager.getCache("ddoDataCache");
			ddoDataCache.remove(key);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("--------------- Cached DDO Data CLEARED - " + key + " ------------------");
	}
	
	public static String formatCurrencyInRs(String lStrAmount) {
		String tempAmt = lStrAmount;
		String decAmt = "";
		String amount = "";
		String newStrAmt = "";
		int len = 0;
		int i= 0;
		
		if (lStrAmount != null && lStrAmount.length() > 0) {
			if (tempAmt.indexOf(".") >= 0) {
				amount = tempAmt.substring(0, tempAmt.indexOf("."));
				decAmt = tempAmt.substring(tempAmt.indexOf("."), tempAmt.length());
			} else
				amount = tempAmt;
			len = amount.length();			
			for (i = 0; i < len; i++) {
				// if length is odd, then add ',' at every even places, except
				// last 3 digits.
				if (len % 2 == 1 && i % 2 == 0 && i < len - 2 && i > 0)
					newStrAmt = newStrAmt + ",";
				// if length is even, then add ',' at every odd places, except
				// last 3 digits.
				else if (len % 2 == 0 && i % 2 == 1 && i < len - 2 && i > 0)
					newStrAmt = newStrAmt + ",";
				newStrAmt = newStrAmt + amount.charAt(i);
			}
			if (decAmt != null && decAmt.length() > 0)
				newStrAmt = newStrAmt + decAmt;
			else
				newStrAmt = newStrAmt + ".00";
		}
		return newStrAmt;
	}
	
	public static String getCSVStringFromList(List<String> lLstInput) {

		StringBuilder lStrCSV = new StringBuilder();
		String lStrCSVString = null;

		for (String lStrValues : lLstInput)
			lStrCSV.append(lStrValues + ",");

		lStrCSVString = lStrCSV.toString().substring(0, lStrCSV.length() - 1);
		return lStrCSVString;
	}
	
	
	public static List getListFromArrayUsingIndex(String[] paramValues, int index) {

		List resultList = new ArrayList();

		for (String param : paramValues)
			resultList.add(Long.parseLong(param.split("~")[index]));
		
		return resultList;
	}
	
	public ResultObject removeSecureURLMap(Map inputMap)
	{
    	ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
    	try {
    		HandleApplicationEvent.raiseUrlRoleMappingChangeEvent();
    		inputMap.put("URLMapRemoved", "Y");
    		resObj.setResultValue(inputMap);    		
			resObj.setViewName("admin-removeSecureURLMap");
		}
    	catch (Exception e) {
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			logger.error(" Error in removeSecureURLMap " + e, e);
		}
    	return resObj;
	}
	
	public ResultObject searchUserForPasswordReset(Map objectArgs) 
	{
		ResultObject objRes = new ResultObject(ErrorConstants.ERROR);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		try
		{
			if(objRes != null && objectArgs != null)
			{
				LoginDetails objLoginDetails = (LoginDetails) objectArgs.get("baseLoginVO");
				Long langId = objLoginDetails.getLangId();
				Long locId = objLoginDetails.getLoggedInPostDetailsRlt().getCmnLocationMst().getLocId();
				String txtuserName = StringUtility.getParameter("txtuserName",request);
				logger.info("txtuserName:::::::::::"+txtuserName);
				
				List<ResetPswdCustom> ResetPswdCustomlist = new ArrayList<ResetPswdCustom>();
				List<OrgUserMst> userlist = new ArrayList<OrgUserMst>();
				OrgPostMstDaoImpl postMstDaoImpl = new OrgPostMstDaoImpl(OrgPostMst.class,serv.getSessionFactorySlave());

				if(txtuserName != null && !("").equals(txtuserName))
				{
					IFMSCommonDAOImpl commonDAOImpl = new IFMSCommonDAOImpl(OrgUserMst.class, serv.getSessionFactorySlave());

					userlist = commonDAOImpl.getUserListOfLoggedinLocation(txtuserName, locId, langId);
					logger.info("userlist:::::::"+userlist.size());
					StringBuffer nameLastPart = null;
					for(int i=0;i<userlist.size();i++)
					{
						OrgUserMst orgUserMst=userlist.get(i);
						Set<OrgEmpMst> orgEmpSet= orgUserMst.getOrgEmpMsts();
						for (Iterator it = orgEmpSet.iterator(); it.hasNext();)
						{
							  nameLastPart = new StringBuffer();
							  OrgEmpMst orgEmpMst = (OrgEmpMst) it.next();
							  if(orgEmpMst.getCmnLanguageMst().getLangId() == langId)
							  {
								  String empPrefix = (orgEmpMst.getEmpPrefix() != null) ? orgEmpMst.getEmpPrefix().trim() : "";
						          String empFname = (orgEmpMst.getEmpFname() != null) ? orgEmpMst.getEmpFname().trim() : ""; 
						          String empMname = (orgEmpMst.getEmpMname() != null) ? orgEmpMst.getEmpMname().trim() : "";
						          String empLname = (orgEmpMst.getEmpLname() != null) ? orgEmpMst.getEmpLname().trim() : "";	   
						          ResetPswdCustom pswdCustom = new ResetPswdCustom(); 
						          if(!"".equals(empPrefix))
						          {
						        	  nameLastPart.append(empPrefix);
						          }
						          if(!"".equals(empPrefix))
						          {
							          if(!"".equals(empFname))
							          {
							        	  nameLastPart.append(' ');
							        	  nameLastPart.append(empFname);	
							          }
						          }
						          else
						          {
						        	  nameLastPart.append(empFname);	
						          }
						          if(!"".equals(empMname))
					              {
						        	  nameLastPart.append(' ');
						        	  nameLastPart.append(empMname);
					              }
					        	  if(!"".equals(empLname))
					              {
					        		  nameLastPart.append(' ');
					        		  nameLastPart.append(empLname);
					              }  
					        	  OrgPostMst orgPostMst = postMstDaoImpl.getPrimaryPost(orgUserMst.getUserId(), langId);
					        	  if(orgPostMst != null)
					        	  {
					        		  logger.info("orgPostMst.getPostId()::"+orgPostMst.getPostId());
					        		 // OrgPostDetailsRlt orgPostDetailsRlt  = postDetailsRltDaoImpl.getPostDetailsRltByPostIdAndLangId(orgPostMst.getPostId(), langId);
					        		  Set<OrgPostDetailsRlt> OrgPostDetailsRltSet = orgPostMst.getOrgPostDetailsRlt();
					        		  for (Iterator iter = OrgPostDetailsRltSet.iterator(); iter.hasNext();)
					        		  {
											OrgPostDetailsRlt orgPostDetailsRlt = (OrgPostDetailsRlt) iter.next();
											if(orgPostDetailsRlt != null)
											{
												if(orgPostDetailsRlt.getCmnLanguageMst().getLangId() == langId)
												{
													  String postName = (orgPostDetailsRlt.getPostName() != null) ? orgPostDetailsRlt.getPostName().trim() : "";
									        		  if(postName != null)
									        		  {
									        			  pswdCustom.setPost(postName);
									        		  }
									        		  else
									        		  {
									        			  pswdCustom.setPost("");
									        		  }
									        		  if(orgPostDetailsRlt.getCmnLocationMst()!= null)
									        		  {
									        			  String locName = (orgPostDetailsRlt.getCmnLocationMst().getLocName() != null) ? orgPostDetailsRlt.getCmnLocationMst().getLocName().trim() : "";
									        			  if(locName != null)
									        			  {
									        				  pswdCustom.setLocation(locName);
									        			  }
									        			  else
									        			  {
									        				  pswdCustom.setLocation("");
									        			  }
									        		  }
									        		  else
									        		  {
									        			  pswdCustom.setLocation("");
									        		  }
												}
											}
					        		  }
					        	  }					        	  
					        	  if(nameLastPart.toString() != null)
					        	  {
					        		  pswdCustom.setName(nameLastPart.toString());
					        	  }
					        	  else
					        	  {
					        		  pswdCustom.setName("");
					        	  }
					        	  pswdCustom.setUserId(orgUserMst.getUserId());
					        	  ResetPswdCustomlist.add(pswdCustom);
							 }
						}
					}
				}
				objectArgs.put("ResetPswdCustomlist", ResetPswdCustomlist);
				objRes.setResultValue(objectArgs);
				objRes.setResultCode(ErrorConstants.SUCCESS);
				objRes.setViewName("ifmsResetPassword");
			}
		}
		catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            objRes.setResultCode(ErrorConstants.ERROR);
            objRes.setThrowable(e);
        }
        return objRes;
	}
	
	public void clearCacheForMapDDOAsst(SessionFactory sessionFactory) throws Exception{
		
		try {
			
			CacheManager cacheManager = CacheManager.getInstance();
			
			Cache cacheUtilityObj = cacheManager.getCache("CacheUtility");
			cacheUtilityObj.removeAll();
			
			Cache cacheQry = cacheManager.getCache("ecache_query");
			cacheQry.removeAll();
			sessionFactory.evictQueries("ecache_query");
			
			/*
			Cache cacheObj = cacheManager.getCache("ecache_object");
			cacheObj.removeAll();
			sessionFactory.evictQueries("ecache_object");
			
			
			Cache cache = cacheManager.getCache("ecache_ac");
			cache.removeAll();
			sessionFactory.evictQueries("ecache_ac");
			
			Cache cacheLookup = cacheManager.getCache("ecache_lookup");
			cacheLookup.removeAll();
			sessionFactory.evictQueries("ecache_lookup");	
			
			//Cache cacheWf = cacheManager.getCache("wfCache");
			//cacheWf.removeAll();
			
			*/
			
			sessionFactory.evictQueries("wfCache");
			sessionFactory.evictQueries();
			
			// For Post role
			sessionFactory.evict(AclPostroleRlt.class);
			
			// For Work-flow
			sessionFactory.evict(WfActionMst.class);
			sessionFactory.evict(WfActDocRuleMpg.class);
			sessionFactory.evict(WfAlternateflowActionMst.class);
			sessionFactory.evict(WfHierachyAlternateflowMpg.class);
			sessionFactory.evict(WfHierachyPostMpg.class);
			sessionFactory.evict(WfHierarchyReferenceMst.class);
			sessionFactory.evict(WfOrgLocMpgMst.class);
			sessionFactory.evict(WfOrgPostMpgMst.class);
			sessionFactory.evict(WfOrgUsrMpgMst.class);
			
		} catch (Exception e) {
			logger.error("Error is ", e);
			throw e;
		}
		
		logger.info("---------------Map Asst CACHE CLEARED------------------");
	}
	
}