package com.tcs.sgv.common.helper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.wf.interfaces.WFConstants;
import com.tcs.sgv.wf.valueobject.InboxSearchVo;
import com.tcs.sgv.wf.valueobject.WfDocNotificationListVo;
import com.tcs.sgv.wf.valueobject.WfJobMstVO;
import com.tcs.sgv.wf.valueobject.WorkFlowVO;


/**
 * A class for work flow related interactions
 * 
 * @author Nirav Bumia
 */
public class WorkFlowHelper {

	private static final Log logger = LogFactory.getLog(WorkFlowHelper.class);

	/**
	 * This method returns all documents from workflow by postId(s)
	 * 
	 * @param Map
	 *            map, WorkFlowVO lObjWorkFlowVO, String lStrPostId, String
	 *            lStrUserId, List lLstdocId, String lStrLangId
	 * @return List
	 */
	public static List<WfDocNotificationListVo> getDocListFromWorkFlow(Long lLngPostId, Long lLngUserId, List lLstdocId, Long lLngLangId, Map inputMap) throws Exception {

		List<WfDocNotificationListVo> resultLst = null;
		InboxSearchVo inboxSearchVo = new InboxSearchVo();

		try {

			com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
			inboxSearchVo = IFMSCommonServiceImpl.getInboxSearchVo(lLngPostId, lLngUserId, lLstdocId, lLngLangId);

			resultLst = orgUtil.getDocAndNotificationList(inboxSearchVo);

		} catch (Exception e) {
			logger.error("Error while executing getDocListFromWorkFlow of WF Helper. Error is ", e);
			throw e;
		}

		return resultLst;
	}

	/**
	 * This method returns all bills from workflow by postId
	 * 
	 * @param Map
	 *            map, WorkFlowVO lObjWorkFlowVO, String lStrPostId, String
	 *            lStrUserId, List lLstdocId, String lStrLangId
	 * @return List<Long>
	 */
	public static List<Long> getDocsFromWorkFlow(Long lLngPostId, Long lLngUserId, List lLstdocId, Long lLngLangId, Map inputMap) throws Exception {

		List<Long> billLst = null;
		try {
			List<WfDocNotificationListVo> docList = getDocListFromWorkFlow(lLngPostId, lLngUserId, lLstdocId, lLngLangId, inputMap);
			if (docList != null && docList.size() > 0) {
				billLst = new ArrayList<Long>();
				WfDocNotificationListVo mvmntVO = null;
				for (int i = 0; i < docList.size(); i++) {
					mvmntVO = (WfDocNotificationListVo) docList.get(i);
					billLst.add(Long.parseLong(mvmntVO.getJobRefId()));
				}
			}
		} catch (Exception e) {
			logger.error("Error while executing getDocsFromWorkFlow of WF Helper. Error is ", e);
			throw e;
		}
		return billLst;
	}

	public static WorkFlowVO getDefaultWorkFlowVO(Map inputMap) throws Exception {

		String lStrPostId = SessionHelper.getPostId(inputMap).toString();
		String lStrUserId = SessionHelper.getUserId(inputMap).toString();
		String lStrLocationCode = SessionHelper.getLocationCode(inputMap);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		DataSource dataSrc = serv.getDataSource();
		Connection conn = serv.getSessionFactory().getCurrentSession().connection();
		Long lLngDBId = SessionHelper.getDbId(inputMap);
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();

		WorkFlowVO workFlowVO = new WorkFlowVO();
		workFlowVO.setAppMap(inputMap);
		workFlowVO.setCrtEmpId(lStrUserId);
		workFlowVO.setCrtPost(lStrPostId);
		workFlowVO.setFromPost(lStrPostId);
		workFlowVO.setCrtUsr(lStrUserId);
		workFlowVO.setConnection(conn);
		workFlowVO.setLocID(lStrLocationCode);
		workFlowVO.setDbId(lLngDBId);
		workFlowVO.setHierarchyFlag(1);
		workFlowVO.setLangID(lStrLangId);
		workFlowVO.setLocationCode(lStrLocationCode);
		workFlowVO.setDataSource(dataSrc);

		return workFlowVO;
	}

	public static WorkFlowVO getWorkFlowVO(Map inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		DataSource dataSrc = serv.getDataSource();
		Connection conn = serv.getSessionFactory().getCurrentSession().connection();

		WorkFlowVO workFlowVO = new WorkFlowVO();
		workFlowVO.setAppMap(inputMap);
		workFlowVO.setConnection(conn);
		workFlowVO.setDataSource(dataSrc);

		return workFlowVO;
	}

	/**
	 * This method returns all documents from workflow based on inbox search VO
	 * parameters
	 * 
	 * @param Map
	 *            inputMap, WorkFlowVO lObjWorkFlowVO, String gStrPostId, String
	 *            gStrUserId, String gStrLangId
	 * @return List
	 */
	public static List<WfDocNotificationListVo> getDocAndNotificationList(Map inputMap, Long lLngPostId, Long lLngUserId, Long lLngLangId) throws Exception {

		List<WfDocNotificationListVo> resultLst = null;
		InboxSearchVo inboxSearchVo = new InboxSearchVo();

		try {

			com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
			inboxSearchVo = IFMSCommonServiceImpl.getInboxSearchVo(lLngPostId, lLngUserId, lLngLangId);

			resultLst = orgUtil.getDocAndNotificationList(inboxSearchVo);

		} catch (Exception e) {
			logger.error("Error while executing getMyBills of WF Helper. Error is ", e);
			throw e;
		}

		return resultLst;
	}

	/**
	 * This method returns Hierarchy Reference ID based on post and hierarchy
	 * description
	 * 
	 * @param String
	 *            lStrPostId, String lStrdescription, Map inputMap
	 * @return Long
	 */
	public static Long getHierarchyByPostIDAndDescription(String lStrPostId, String lStrdescription, Map inputMap) throws Exception {

		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		Long lLngHierRefId = null;

		try {
			Map resultMap = orgUtil.getHierarchyByPostIDAndDescription(lStrPostId, lStrdescription, inputMap);
			/* FOR GETTING HIERARCHY */

			List resultList = (List) resultMap.get("Result");
			if (resultList != null && resultList.size() > 0) {
				lLngHierRefId = Long.parseLong(resultList.get(0).toString());
			}
		} catch (Exception e) {
			logger.error("Error while executing getHierarchyByPostIDAndDescription of WF Helper. Error is ", e);
			throw e;
		}

		return lLngHierRefId;
	}

	
	
	
	
	/**
	 * This method returns List of Given Alternate Hierarchy's post and level
	 * 
	 * @param String
	 *            gStrPostId, Long lLngHeirRefId, String action, Map inputMap
	 * @return List
	 */
	public static List getToNodeListFromAlterHir(String gStrPostId, Long lLngHeirRefId, String action, Map inputMap) throws Exception {

		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		List lLstAlternateFlowPostAndLevel = null;

		try {
			lLstAlternateFlowPostAndLevel = orgUtil.getToNodeListFromAlterHir(gStrPostId, lLngHeirRefId, action, inputMap);
		} catch (Exception e) {
			logger.error("Error while executing getToNodeListFromAlterHir of WF Helper. Error is ", e);
			throw e;
		}
		return lLstAlternateFlowPostAndLevel;
	}

	/**
	 * This method returns List of Upper Post
	 * 
	 * @param String
	 *            gStrPostId, Long lLngHeirRefId, int lIntFrmLevel, Map
	 *            inputMap
	 * @return List
	 */
	public static List getUpperPost(String gStrPostId, Long lLngHeirRefId, int lIntFrmLevel, Map inputMap) throws Exception {

		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		Map lMapAlternateFlowPostAndLevel = null;
		List lLstAlternateFlowPostAndLevel = null;

		try {
			lMapAlternateFlowPostAndLevel = orgUtil.getUpperPost(gStrPostId, lLngHeirRefId, lIntFrmLevel, inputMap);

			if (lMapAlternateFlowPostAndLevel != null)
				lLstAlternateFlowPostAndLevel = (List) lMapAlternateFlowPostAndLevel.get("Result");
		} catch (Exception e) {
			logger.error("Error while executing getUpperPost of WF Helper. Error is ", e);
			throw e;
		}
		return lLstAlternateFlowPostAndLevel;
	}

	/**
	 * This method returns Upper Child Hierarchy
	 * 
	 * @param Long
	 * @return List
	 */
	public static List getUpperChildHierarchy(Long lLngCurrHeirId, Map inputMap) throws Exception {

		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		List hierarchyList = orgUtil.getUpperChildHierarchy(lLngCurrHeirId, inputMap);
		return hierarchyList;
	}

	public static List getUpperChildHierarchy(Long lLngCurrHeirId, WorkFlowVO workflowvo) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(workflowvo);
		List hierarchyList = orgUtil.getUpperChildHierarchy(lLngCurrHeirId, workflowvo);
		return hierarchyList;
	}

	public static WfJobMstVO getJobMstByJobRefIDAndFromPost(String lStrBillNo, String lStrPostId, Long lDocId, Map inputMap) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
		WfJobMstVO lObjWfJobMstVO = orgUtil.getJobMstByJobRefIDAndFromPost(lStrBillNo, lStrPostId, lDocId);
		return lObjWfJobMstVO;
	}

	public static List getJobMstByJobRefID(String lStrBillNo, Long lLngdocId, int docType, Map inputMap) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
		List<WfJobMstVO> jobMstList = orgUtil.getJobMstByJobRefID(lStrBillNo, lLngdocId, docType);
		return jobMstList;
	}

	public static Map getReturnPost(Map inputMap) throws Exception {

		WorkFlowVO wfVo = getDefaultWorkFlowVO(inputMap);		
		wfVo.setJobRefId(inputMap.get("jobRefId").toString());
		wfVo.setDocId(Long.parseLong(inputMap.get("lStrDocId").toString()));
		wfVo.setJobTitle(inputMap.get("jobTitle").toString());
		
		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		Map returnParamMap = orgUtil.getReturnPost(wfVo, inputMap);
		/* returnParamMap Key descriptions are as follows */
		//returnPost - return post
		//returnLevel - return level
		//returnRole - return role
		//returnHierRefId = return Hierarchy Reference Id
		
		return returnParamMap;
	}

	public static void updateJobStatus(String lStrPostId, String jobRefId, String jobStatus, String docId, Map inputMap) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
		orgUtil.updateJobStatus(lStrPostId, jobRefId, jobStatus, Integer.parseInt(docId),WFConstants.DEFAULT_DOCTYPE);
	}
	
	public static void updateJobStatus(String jobRefId, String jobStatus, String docId, Map inputMap) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
		orgUtil.updateJobStatus(jobRefId, jobStatus, Integer.parseInt(docId),WFConstants.DEFAULT_DOCTYPE);
	}
	
	public static void updateBulkJobStatus(Map inputMap) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
		orgUtil.updateBulkJobStatus(inputMap);
	}

	public static List getParentHierarchyList(Long lLngHierRefId, Map inputMap) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
		List lLstParentHier = orgUtil.getParentHierarchyList(lLngHierRefId);
		return lLstParentHier;
	}

	public static int getLevelFromPostMpg(String gStrPostId, Long lLngHeirRefId, Map inputMap) throws Exception {

		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		int llFromLevelId = orgUtil.getLevelFromPostMpg(gStrPostId, lLngHeirRefId, inputMap);
		return llFromLevelId;
	}

	public static String getCurrentOwnerPostId(String jobRefId, int docId, int docType, Map inputMap) throws Exception {

		com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl orgUtil = new com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl(getWorkFlowVO(inputMap));
		String lStrOwnerPostId = orgUtil.getCurrentOwnerPostId(jobRefId, docId, docType);
		return lStrOwnerPostId;
	}

	public static List getAllIntimationByPostId(String lStrPostId, Map inputMap) throws Exception {

		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		List lObjListIntimation = orgUtil.getAllIntimationByPostId(lStrPostId, inputMap);
		return lObjListIntimation;
	}
	
	public static List getPostIdListbyHirRefIdAndLevelId(long llHirRefId, long llLevelId, Map inputMap) throws Exception {

		com.tcs.sgv.fms.util.OrgUtilityImpl orgUtil = new com.tcs.sgv.fms.util.OrgUtilityImpl();
		List postList = orgUtil.getPostIdListbyHirRefIdAndLevelId(llHirRefId, llLevelId, inputMap);
		return postList;
	}
}