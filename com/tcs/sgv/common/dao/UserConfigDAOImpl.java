package com.tcs.sgv.common.dao;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.dao.AclMstRoleDao;
import com.tcs.sgv.acl.dao.AclMstRoleDaoImpl;
import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.fms.dao.CmnProjectMstDao;
import com.tcs.sgv.fms.dao.CmnProjectMstDaoImpl;
import com.tcs.sgv.fms.dao.WfAlternateflowActionMstDaoImpl;
import com.tcs.sgv.fms.dao.WfDocMstDaoImpl;
import com.tcs.sgv.fms.dao.WfOrgLocMpgMstDaoImpl;
import com.tcs.sgv.fms.dao.WfOrgPostMpgMstDaoImpl;
import com.tcs.sgv.fms.dao.WfOrgUsrMpgMstDao;
import com.tcs.sgv.fms.dao.WfOrgUsrMpgMstDaoImpl;
import com.tcs.sgv.fms.valueobject.CmnProjectMst;
import com.tcs.sgv.fms.valueobject.WfAlternateflowActionMst;
import com.tcs.sgv.fms.valueobject.WfHierachyAlternateflowMpg;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;
import com.tcs.sgv.fms.valueobject.WfHierarchyReferenceMst;
import com.tcs.sgv.fms.valueobject.WfOrgLocMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgPostMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgUsrMpgMst;
/**
 * A class that implements reports related services 
 * 
 * @author Jayraj Chudasama
 */



public class UserConfigDAOImpl extends GenericDaoHibernateImpl {

	 private Long gLngPostId = null;

	  /* Global Variable for UserId */
	  private Long gLngUserId = null;

	  /* Global Variable for LangId */
	  private Long gLngLangId = null;

	  /* Global Variable for DB Id */
	  private Long gLngDBId = null;

	  /* Global Variable for Location Code */
	  private String gStrLocationCode = null;
	  /* Global variable for financial year id */
	  private Integer gIntFinYrId = null;
	  
	  /*Global variable for lang name*/
	  private String gStrLangName = null;
	  	 
	  SessionFactory sessionFactory = null;
	  
	  static ResourceBundle bundleApplicationDB = ResourceBundle.getBundle("ApplicationDB");
	  
	  private static SimpleDateFormat oSimpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	  static NumberFormat objNumFormat =  NumberFormat.getInstance();
	  static
	  {
		  objNumFormat.setMinimumFractionDigits(2);
		  objNumFormat.setMaximumFractionDigits(2);
		  objNumFormat.setGroupingUsed(false);
	  }
	 
	  public UserConfigDAOImpl(Class type ,SessionFactory sessionFactory) 
		{
			super(type);
			this.sessionFactory = sessionFactory;
			setSessionFactory(sessionFactory);
		}

	  public List getAllDepartment() throws Exception
	  {
		  List lLstDept = null;
		  List<ComboValuesVO> lLstAllDept = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT departmentId, depName FROM OrgDepartmentMst \n");
			  lSBQuery.append("WHERE departmentId IN (100003,100007,100011)");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lLstDept = lQuery.list();
			  
			  if(lLstDept != null && lLstDept.size() > 0){
				  Iterator IT = lLstDept.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[]) IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllDept.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllDept;
	  }
	  
	  public List getAllDistrict() throws Exception
	  {
		  List lLstDist = null;
		  List<ComboValuesVO> lLstAllDist = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT districtId, districtName FROM CmnDistrictMst \n");
			  lSBQuery.append("WHERE cmnLanguageMst.langId = :langId AND cmnStateMst.stateId = :stateId");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setLong("langId", 1);
			  lQuery.setLong("stateId",15);
			  lLstDist = lQuery.list();
			  
			  if(lLstDist != null && lLstDist.size() > 0){
				  Iterator IT = lLstDist.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[])IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllDist.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllDist;
	  }
	  
	 public List getLevelIdForRoles(List lLstRoles) throws Exception
	 {
		 List lLstLevelId = null;
		 Session ghibSession = getSession();
		 
		 try{
			 StringBuilder lSBQuery = new StringBuilder();
			 lSBQuery.append("SELECT LEVEL_ID FROM RLT_LEVEL_ROLE WHERE ROLE_ID IN (:roleId)");
			 Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			 lQuery.setParameterList("roleId", lLstRoles);
			 lLstLevelId = lQuery.list();
		 }catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		 }
		 return lLstLevelId;
	 }
	  
	 public List getAllLocations(Long langId,String locDeptId)
	 {
		 Session hibSession = getSession();	
		 List lLstLocs = null;
		 List<ComboValuesVO> lLstAllLocs = new ArrayList<ComboValuesVO>();
		 Query sqlLocsQuery= null;
		 
		 StringBuffer sbLocs = new StringBuffer();
		
		 sbLocs.append("SELECT locationCode,locName FROM CmnLocationMst \n");
		 sbLocs.append("WHERE departmentId = :departmentId AND cmnLanguageMst.langId = :langId \n");		 
		 sbLocs.append("ORDER BY locName \n");
		 Query lQuery = hibSession.createQuery(sbLocs.toString());
		 lQuery.setLong("departmentId", Long.parseLong(locDeptId));		 
		 lQuery.setLong("langId", langId);		 
		 lLstLocs = lQuery.list();
		 
		 if(lLstLocs != null && lLstLocs.size() > 0){
			  Iterator IT = lLstLocs.iterator();
			  
			  ComboValuesVO cmbVO = new ComboValuesVO();
			  Object[] lObj = null;
			  while(IT.hasNext()){
				  cmbVO = new ComboValuesVO();
				  lObj = (Object[])IT.next();
				  cmbVO.setId(lObj[0].toString());
				  cmbVO.setDesc(lObj[1].toString());
				  lLstAllLocs.add(cmbVO);
			  }
		  }
		 
		 return lLstAllLocs;
	 }
	 
	 public List getAllRolesModuleWise(String lStrModuleName,String lStrDept)
	 {
		 Session hibSession = getSession();	
		 List lLstRoles = null;		
		 Query sqlLocsQuery= null;		 
		 
		 StringBuffer sbLocs = new StringBuffer();
		
		 sbLocs.append("SELECT ROLE_ID FROM RLT_LEVEL_ROLE \n");
		 sbLocs.append("WHERE MODULE_NAME = :moduleName \n");
		 /*if(lStrModuleName.equals("GPF") && lStrDept.equals("100007")){
			 sbLocs.append(" AND department = 'DDO'");
		 }else if(lStrModuleName.equals("GPF") && lStrDept.equals("100011")){
			  sbLocs.append(" AND department = 'HO'");
		 }*/
		 Query lQuery = hibSession.createSQLQuery(sbLocs.toString());		 
		 lQuery.setString("moduleName", lStrModuleName);
		 
		 lLstRoles = lQuery.list();
		 
		 return lLstRoles;
	 }

	 public String getAllUserData(String locCode,Long langId,List lLstRoles,String lStrDdoCode,String Module,String lStrDept) throws Exception
	 {
		 Session hibSession = getSession();	
		 String strUserNameOuter = "";
		 Query sqlUNameQuery= null;
		 
		  StringBuffer sbUserName = new StringBuffer();
		
		  if(Module.equals("Pensionpay") && lStrDept.equals("100003")){
			  sbUserName.append("SELECT DISTINCT OEM.empFname,OEM.empMname,OEM.empLname,OPM.postId,OUPR.orgUserMst.userName,OPDR.postName \n");
			  sbUserName.append("FROM OrgEmpMst OEM,OrgUserpostRlt OUPR, \n");
			  sbUserName.append("OrgPostMst OPM, OrgPostDetailsRlt OPDR, AclPostroleRlt APR \n");				  			
			  sbUserName.append("WHERE OPM.locationCode = :locCode AND APR.aclRoleMst.roleId IN (:roleId) \n");
			  sbUserName.append("AND APR.orgPostMst.postId = OPM.postId AND OUPR.orgPostMstByPostId.postId = OPM.postId \n");
			  sbUserName.append("AND OEM.orgUserMst.userId = OUPR.orgUserMst.userId AND OPDR.orgPostMst.postId = OPM.postId \n");
			  sbUserName.append("AND OPDR.cmnLanguageMst.langId = :langId AND OEM.cmnLanguageMst.langId = :langId ");
			  sbUserName.append("AND OUPR.activateFlag = 1 ");
			  
			  sqlUNameQuery = hibSession.createQuery(sbUserName.toString());
			  sqlUNameQuery.setString("locCode", locCode);
			  sqlUNameQuery.setParameter("langId", langId);
			  sqlUNameQuery.setParameterList("roleId", lLstRoles);
			  
		  }else if(Module.equals("GPF") && lStrDept.equals("100007")){
			  sbUserName.append("SELECT DISTINCT OEM.empFname,OEM.empMname,OEM.empLname,OUPR.orgPostMstByPostId.postId,OUPR.orgUserMst.userName,OPDR.postName \n");
			  sbUserName.append("FROM OrgEmpMst OEM,OrgUserpostRlt OUPR,MstEmp MDE,OrgPostDetailsRlt OPDR \n");
			  sbUserName.append("WHERE MDE.ddoCode = :ddoCode AND MDE.orgEmpMstId = OEM.empId AND MDE.dcpsOrGpf = 'N' AND OEM.orgUserMst.userId = OUPR.orgUserMst.userId \n");
			  sbUserName.append("AND OUPR.orgPostMstByPostId.postId = OPDR.orgPostMst.postId AND OPDR.cmnLanguageMst.langId = :langId \n");
			  
			  sqlUNameQuery = hibSession.createQuery(sbUserName.toString());			  
			  sqlUNameQuery.setParameter("ddoCode", lStrDdoCode);
			  sqlUNameQuery.setParameter("langId", langId);
		  }
		  

		  List resUserList = sqlUNameQuery.list();

		  if(resUserList != null)
		  {
				
			  for (int i=0; i<resUserList.size(); i++) 
				{
					Object[] tuple = (Object[]) resUserList.get(i);
					String userName = "";
					if(tuple[1] == null )
					{
						tuple[1] = new Object();
						tuple[1] = "";
					}
					userName = tuple[4]+" - "+tuple[0]+" "+tuple[1]+" "+tuple[2]+"~"+tuple[3]+"~"+tuple[5]+"^";
					strUserNameOuter=strUserNameOuter + userName;
				}
					
		  }
		  return strUserNameOuter;
	 }
	 
	  
	  public String getAllUserLevelData(String moduleName,String lStrDept) throws Exception
	  {
		  Session hibSession = getSession();	
		  String strUserLevelOuter = "";
		  StringBuffer sbLevel = new StringBuffer();
		  
		  ArrayList arrRoles = new ArrayList();
		  
		  Query sqlLevelQuery = null;
		  		 
		  sbLevel.append("SELECT ROLE_ID, ROLE_DESC, WORKFLOW FROM RLT_LEVEL_ROLE WHERE MODULE_NAME = :moduleName");
		  /*if(moduleName.equals("GPF") && lStrDept.equals("100007")){
			  //sbLevel.append(" AND department = 'DDO'");
			  sbLevel.append(" AND roleId = 800001");
		  }else if(moduleName.equals("GPF") && lStrDept.equals("100011")){
			  sbLevel.append(" AND department = 'HO'");
		  }*/
		  sqlLevelQuery = hibSession.createSQLQuery(sbLevel.toString());
		  sqlLevelQuery.setString("moduleName", moduleName);
		  
		  List resList = sqlLevelQuery.list();
		  
		  if(resList != null)
		  {
			  for (int i=0; i<resList.size(); i++) 
				{
				  	String userNameIdDesc = "";
				  	Object[] tuple = (Object[]) resList.get(i);				  	
				  	userNameIdDesc = tuple[0]+"$"+tuple[2]+"~"+tuple[1]+"^";
					strUserLevelOuter = strUserLevelOuter + userNameIdDesc;
				}
		  }
		  return strUserLevelOuter;
	  }

	  public String getAllLevelForUser(String Pid, String Module) throws Exception
		 {
		  
		  Session hibSession = getSession();	
		  String finalData="";
		  
		  String strUserLevelOuter = "";
		  String w_flow = "";
		  
		  if(Module.equals("Pensionpay")){
			  w_flow = "N";
		  }else{
			  w_flow = "Y";
		  }
		 		 
		 List resList = null;
		 
		 
		 StringBuffer sbLevel = new StringBuffer();
		 Query sqlLevelQuery= null;
		 //sbLevel.append("SELECT aclRoleMst.roleId FROM AclPostroleRlt WHERE orgPostMst.postId = :postId AND cmnLookupMstByActivate.cmnLanguageMst.activateFlag = :activateFlag");
		
		 sbLevel.append("SELECT ROLE_ID FROM ACL_POSTROLE_RLT WHERE POST_ID =:postId AND ACTIVATE_FLAG =:activateFlag");
		 sqlLevelQuery = hibSession.createSQLQuery(sbLevel.toString());
		  		 
		 sqlLevelQuery.setLong("postId", Long.parseLong(Pid));		 
		 sqlLevelQuery.setLong("activateFlag",Long.parseLong("1"));
		 resList  = sqlLevelQuery.list();		  		 
		  
		  if(resList != null)
		  {
			  for (int i=0; i<resList.size(); i++) 
				{
				  	String userNameIdDesc = "";
					userNameIdDesc = resList.get(i)+"$"+w_flow+"^";
					strUserLevelOuter = strUserLevelOuter +userNameIdDesc;
				}
		  }
		  
		
			
		  String strAssgPostLevel="";
		  Query sqlAssgLevelQuery = null;
		  StringBuffer sbAssgLevel = new StringBuffer();
						 			 
//		  sbAssgLevel.append("SELECT RLR.roleDesc, ARDR.roleDesc "); 
//		  sbAssgLevel.append("FROM AclRoleDetailsRlt ARDR, RltLevelRole RLR, AclPostroleRlt APR ");
//		  sbAssgLevel.append("WHERE APR.orgPostMst.postId = :postId AND APR.aclRoleMst.roleId = RLR.roleId ");
//		  sbAssgLevel.append("AND APR.aclRoleMst.roleId = ARDR.aclRoleMst.roleId AND APR.cmnLookupMstByActivate.cmnLanguageMst.activateFlag = :activateFlag");	 
//		  sqlAssgLevelQuery = hibSession.createQuery(sbAssgLevel.toString());
		  
		  sbAssgLevel.append("SELECT RLR.ROLE_DESC, ARDR.ROLE_DESC "); 
		  sbAssgLevel.append("FROM ACL_ROLE_DETAILS_RLT ARDR, RLT_LEVEL_ROLE RLR, ACL_POSTROLE_RLT APR ");
		  sbAssgLevel.append("WHERE APR.POST_ID = :postId AND APR.ROLE_ID = RLR.ROLE_ID ");
		  sbAssgLevel.append("AND APR.ROLE_ID = ARDR.ROLE_ID AND APR.ACTIVATE_FLAG = :activateFlag");	 
		  sqlAssgLevelQuery = hibSession.createSQLQuery(sbAssgLevel.toString());
		  		  
		  sqlAssgLevelQuery.setLong("postId", Long.parseLong(Pid));
		  sqlAssgLevelQuery.setLong("activateFlag",Long.parseLong("1"));
		  List resAssdList = sqlAssgLevelQuery.list();
		
		  for(int i=0; i< resAssdList.size();i++)
		  {
			  Object[] tuple =(Object[]) resAssdList.get(i);
			  strAssgPostLevel =strAssgPostLevel+tuple[0]+"/"+tuple[1]+"#";
	      }
		  
		  finalData = strUserLevelOuter +"~"+strAssgPostLevel;
		
		  return finalData;
	  }
	  
	  
	  
	  public String insertDataForPost(String Pid, String Desc, Long langId, ArrayList arrLevel,Map objectArgs,List llStAllRolesModuleWise, String lStrWorkFlowType, 
			  Long lLngPost, Long lLngUser, String UserType, String lStrLocCode) throws Exception
	  {
		Session hibSession = getSession();
		//String type =getType(Desc);		
		String strUserLevelOuter ="";
		ArrayList<Long> delRoleId = new ArrayList<Long>();
		ArrayList<Long> delLevelId = new ArrayList<Long>();
		ArrayList descList = new ArrayList();
		Integer lIntRoleId = null;
		Long lLngRoleId = null;
		List lLstRoleId = null;
		ArrayList<Long> refId = new ArrayList<Long>();		
					
		try {
			StringBuffer sbLevel = new StringBuffer();						  
				

			sbLevel.append("SELECT APR.ROLE_ID, APR.ACTIVATE_FLAG FROM ACL_POSTROLE_RLT APR, ORG_POST_MST OPM \n");
			sbLevel.append("WHERE APR.ROLE_ID IN (:roleIdList) AND APR.POST_ID = OPM.POST_ID AND OPM.POST_ID =:postId ");
			Query sqlLevelQuery = hibSession.createSQLQuery(sbLevel.toString());
							
			sqlLevelQuery.setParameterList("roleIdList", llStAllRolesModuleWise);
			sqlLevelQuery.setParameter("postId",Long.parseLong(Pid));
			//sqlLevelQuery.setParameter("locCode", lStrLocCode);
			ArrayList updateRoleFlag = new ArrayList();
			ArrayList updateLevelFlag = new ArrayList();
			ArrayList allActLevels = new ArrayList();		
				
			List<Object[]> resList = sqlLevelQuery.list();    //List of all roles to Pid with act flag 1 or 2

				if (!resList.isEmpty()) 
				{

					for(Object[] tuple:resList)
					{ /*add all active levels to an array*/
						Long resLevelId = Long.parseLong(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(resActFlag.equals(Long.valueOf("1")))
						{
							allActLevels.add(resLevelId);        //List of all active flag with Pid
						}
					}
					/*check whether the role is already assigned to the level and now is unchecked so needs to be deactivated */						
					if(arrLevel !=null && arrLevel.size() > 0)
					{
						allActLevels.removeAll(arrLevel);        //Remove roles that are already selected
					}	
					
					//allActLevels now contains roles to be removed
					
					for(Object[] tuple:resList)
					{
						Long resLevelId = Long.parseLong(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(!arrLevel.isEmpty())
						{
							if(resActFlag.equals(Long.valueOf("2")))
							{
								if (arrLevel.contains(resLevelId)) 
								{
									updateRoleFlag.add(resLevelId);
								}
							}
							else 
							{
								if(arrLevel.contains(resLevelId))
								{
									arrLevel.remove(resLevelId);
								}
							}
						}
					}
					if(arrLevel !=null && arrLevel.size() > 0)
					{
						if(updateRoleFlag != null && updateRoleFlag.size() >0)
						{
							arrLevel.removeAll(updateRoleFlag);
						}
					}
					
					delRoleId = (ArrayList<Long>)allActLevels ;
				}					
				
				
				if(!updateRoleFlag.isEmpty() && updateRoleFlag!= null)
				{
					updateActFlagForPost(updateRoleFlag,Pid,lLngPost,lLngUser,updateLevelFlag,lStrLocCode,refId);
					strUserLevelOuter = "I";
				}						
				
				if(arrLevel != null && arrLevel.size() >0)
				{
					addEntryInPostRole(Pid,lLngPost,lLngUser,lStrWorkFlowType,arrLevel,objectArgs);
					strUserLevelOuter += "I";
				}
				
				if(!delRoleId.isEmpty()&& delRoleId!= null)
				{
					if(!UserType.equals("new")){
						delLevelAndRoleEntries(delRoleId,Pid,lLngPost,lLngUser,gStrLocationCode,refId,Desc,delLevelId);
						strUserLevelOuter += "D";
					}					
				}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		hibSession.flush();
		return strUserLevelOuter;
	}
	 
	public String insertDataForUserLevel(String locCode, String Pid, String Desc, Long langId, List arrLevelid,Map objectArgs
			  ,Long updUserId,Long uptPostId,String UserType,String lStrDdoCode) throws Exception
	{
		Session hibSession = getSession();
		//String type =getType(Desc);
		
		String strUserLevelOuter ="";
		String strRes = "";
		ArrayList delLevelId = new ArrayList();
		ArrayList descList = new ArrayList();
		ArrayList arrRoleId = new ArrayList();
		ArrayList astLevelId = new ArrayList();
		List astPostId = null;
		Long lLngUserId = null;
					
		try {
			StringBuffer sbLevel = new StringBuffer();
			
			if(Desc.equals("GPF")){
					sbLevel.append(
						"select distinct wp.levelId,wp.activateFlag " + 
						"  from WfHierachyPostMpg wp , WfHierarchyReferenceMst wr " + 
						" where  wr.wfDocMst.docId IN (800001,800002,800003) AND " + 
						"           wr.hierachyRefId = wp.hierachyRefId " +
						" and  wp.wfOrgPostMpgMst.postId = :Pid " + 
						" and wp.wfOrgLocMpgMst.locId = :locCode " );
			}else if(Desc.equals("LNA")){
					sbLevel.append(
						"select distinct wp.levelId,wp.activateFlag " + 
						"  from WfHierachyPostMpg wp , WfHierarchyReferenceMst wr " + 
						" where  wr.wfDocMst.docId IN (800004,800005,800006,800007,800008,800009) AND " + 
						"           wr.hierachyRefId = wp.hierachyRefId " +
						" and  wp.wfOrgPostMpgMst.postId = :Pid " + 
						" and wp.wfOrgLocMpgMst.locId = :locCode " );
			}else if(Desc.equals("DCPS")){
					sbLevel.append(
						"select distinct wp.levelId,wp.activateFlag " + 
						"  from WfHierachyPostMpg wp , WfHierarchyReferenceMst wr " + 
						" where  wr.wfDocMst.docId IN (700001,700002,700003,700005,700006) AND " + 
						"           wr.hierachyRefId = wp.hierachyRefId " +
						" and  wp.wfOrgPostMpgMst.postId = :Pid " + 
						" and wp.wfOrgLocMpgMst.locId = :locCode " );
			}
				
				Query sqlLevelQuery = hibSession.createQuery(sbLevel.toString());
				
				sqlLevelQuery.setString("locCode", locCode);
				sqlLevelQuery.setString("Pid", Pid);				
				ArrayList updateLevelFlag = new ArrayList();
				ArrayList updateRoleFlag = new ArrayList();
				ArrayList allActLevels = new ArrayList();
				
				List<Object[]> resList = sqlLevelQuery.list();

				if (!resList.isEmpty()) 
				{
					for(Object[] tuple:resList)
					{ /*add all active levels to an array*/
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(resActFlag.equals(Long.valueOf("1"))){
							allActLevels.add(resLevelId);
						}
					}
					/*check whether the role is already assigned to the level and now is unchecked so needs to be deactivated */						
					if(arrLevelid !=null){
						allActLevels.removeAll(arrLevelid);
					}
					
					for(Object[] tuple:resList)
					{
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(arrLevelid != null)
						{
							if(resActFlag.equals(Long.valueOf("2")))
							{
								if (arrLevelid.contains(resLevelId)) {
									updateLevelFlag.add(resLevelId);
								}
							}
							else 
							{
							/*check whether the role is already assigned to the level */		
								if(arrLevelid.contains(resLevelId)){
									arrLevelid.remove(resLevelId);
								}
							}
						}
					
					}
					if(arrLevelid !=null)
					{	
						if(updateLevelFlag != null && updateLevelFlag.size() >0){
							arrLevelid.removeAll(updateLevelFlag);
						}
					}
					
					delLevelId = (ArrayList)allActLevels ;
				}
				else{
					generateRefId(locCode,Desc,objectArgs,langId,updUserId);
				}
				
				/*get  ref_id for insertion in WF_HIERACHY_POST_MPG*/
				ArrayList<Long> refId = new ArrayList<Long>();
				refId = getRefId(locCode,Desc);
					
				
				/*when the level is not present but is active make it deactive: DELETE*/
				
				if(!delLevelId.isEmpty()&& delLevelId!= null)
				{
					if(!UserType.equals("new")){
						delLevelAndRoleEntries(arrRoleId,Pid,uptPostId,updUserId,locCode,refId,Desc,delLevelId);
						strRes = "D";
					}					
				}
				
				/*when the level is present but is deactivated to activate it set flag=1*/
				if(!updateLevelFlag.isEmpty() && updateLevelFlag!= null)
				{
					updateActFlagForPost(updateRoleFlag,Pid,uptPostId,updUserId,updateLevelFlag,locCode,refId);
					strRes += "I";
				}			
				
				/*Insert for common pool ends*/
				
				if(arrLevelid != null && arrLevelid.size() >0)
				{
					/*Insert into wf_hierarchy_Post_mpg*/					
					if(Desc.equals("GPF") && arrLevelid.contains(20)){
						astPostId = getAstPostId(lStrDdoCode);
						astLevelId.add(Integer.parseInt("10"));
						for(int i=0;i<astPostId.size();i++)
						{
							if(chkEntryDDOAst(Long.parseLong(astPostId.get(i).toString()),locCode) == "N"){
								lLngUserId = getUsrIdFromPostId(astPostId.get(i).toString());
								strUserLevelOuter = addPostInHierarchy(Desc,locCode,astPostId.get(i).toString(),updUserId,objectArgs,astLevelId,refId,lLngUserId);
							}
						}
					}
					lLngUserId = getUsrIdFromPostId(Pid);
					strUserLevelOuter = addPostInHierarchy(Desc,locCode,Pid,updUserId,objectArgs,(ArrayList) arrLevelid,refId,lLngUserId);
					strRes += "I";
				}
				
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		hibSession.flush();
			
		return strRes;
	}
	
	public String chkEntryWfOrgPost(String Pid)
	{
		Session hibSession = getSession();
		String res = "";
		List lLstRes = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT postId FROM WfOrgPostMpgMst ");
			lSBQuery.append("WHERE postId = :postId");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("postId", Pid);
			lLstRes = lQuery.list();
			
			if(lLstRes.size() > 0 && !lLstRes.isEmpty()){
				res = "Y";
			}else{
				res = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		return res;
	}
	
	public void addEntryWfOrgPost(String Pid)
	{
		Session hibSession = getSession();
		Integer lIntProjId = 101;
		try{
			CmnProjectMstDao lObjCmnProjectMstDao = new CmnProjectMstDaoImpl(CmnProjectMst.class,this.sessionFactory);
			CmnProjectMst lObjCmnProjectMst = lObjCmnProjectMstDao.read(lIntProjId);
			
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.sessionFactory);
			CmnDatabaseMst  dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgPostMpgMst lObjWfOrgPostMpgMst = new WfOrgPostMpgMst();
			
			lObjWfOrgPostMpgMst.setPostId(Pid);
			lObjWfOrgPostMpgMst.setCmnProjectMst(lObjCmnProjectMst);
			lObjWfOrgPostMpgMst.setCmnDatabaseMst(dbId);
			hibSession.save(lObjWfOrgPostMpgMst);
			hibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
	}
	
	public String chkEntryWfOrgLoc(String LocCode)
	{
		Session hibSession = getSession();
		String res = "";
		List lLstRes = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM WfOrgLocMpgMst ");
			lSBQuery.append("WHERE locId = :locId");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("locId", LocCode);
			lLstRes = lQuery.list();
			
			if(lLstRes.size() > 0 && !lLstRes.isEmpty()){
				res = "Y";
			}else{
				res = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		return res;
	}
	
	public void addEntryWfOrgLoc(String LocCode)
	{
		Session hibSession = getSession();
		Integer lIntProjId = 101;
		try{
			CmnProjectMstDao lObjCmnProjectMstDao = new CmnProjectMstDaoImpl(CmnProjectMst.class,this.sessionFactory);
			CmnProjectMst lObjCmnProjectMst = lObjCmnProjectMstDao.read(lIntProjId);
			
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.sessionFactory);
			CmnDatabaseMst dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgLocMpgMst lObjWfOrgLocMpgMst = new WfOrgLocMpgMst();
			
			lObjWfOrgLocMpgMst.setLocId(LocCode);
			lObjWfOrgLocMpgMst.setCmnProjectMst(lObjCmnProjectMst);			
			hibSession.save(lObjWfOrgLocMpgMst);
			hibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
	}
	
	public String chkEntryWfOrgUser(Long lLngUserId)
	{
		Session hibSession = getSession();
		String res = "";
		List lLstRes = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM WfOrgUsrMpgMst WHERE userId = :userId \n");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("userId", lLngUserId.toString());
			lLstRes = lQuery.list();
			
			if(lLstRes.size() > 0){
				res = "Y";
			}else{
				res = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		
		return res;
	}
	
	public void addEntryWfOrgUser(Long lLngUserId)
	{
		Session hibSession = getSession();
		Integer lIntProjId = 101;
		
		try{
			CmnProjectMstDao lObjCmnProjectMstDao = new CmnProjectMstDaoImpl(CmnProjectMst.class,this.sessionFactory);
			CmnProjectMst lObjCmnProjectMst = lObjCmnProjectMstDao.read(lIntProjId);
			
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.sessionFactory);
			CmnDatabaseMst dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgUsrMpgMst lObjWfUsrMpgMst = new WfOrgUsrMpgMst();
			lObjWfUsrMpgMst.setUserId(lLngUserId.toString());
			lObjWfUsrMpgMst.setCmnProjectMst(lObjCmnProjectMst);
			lObjWfUsrMpgMst.setCmnDatabaseMst(dbId);
			
			hibSession.save(lObjWfUsrMpgMst);
			hibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
	}
	
	public void generateRefId(String locCode,String Module,Map objectArgs,Long lLngId,Long updUserId)
	{
		List lLstDocId = new ArrayList();
		String lStrDocDesc = "";
		Long lLngMaxRefId = null;
		Long lLngHrSeqNo = null;
		Session hibSession = getSession();
		
		try {
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.sessionFactory);
			CmnDatabaseMst  dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgUsrMpgMstDao lwfOrgUsrMpgMstDao = new WfOrgUsrMpgMstDaoImpl(WfOrgUsrMpgMst.class,this.sessionFactory);			 
			WfOrgUsrMpgMst lwfOrgUsrMpgMst = lwfOrgUsrMpgMstDao.read("1");
			
			com.tcs.sgv.fms.dao.WfDocMstDao lObjWfDocMstDao = new WfDocMstDaoImpl(com.tcs.sgv.fms.valueobject.WfDocMst.class,this.sessionFactory);
			com.tcs.sgv.fms.valueobject.WfDocMst lObjWfDocMst = null;
			
			if(Module.equals("GPF")){
				lLstDocId.add(Long.parseLong("800001"));
				lLstDocId.add(Long.parseLong("800002"));
				lLstDocId.add(Long.parseLong("800003"));
			}
			
			lLngMaxRefId = getMaxRefId(Module);
			//lLngHrSeqNo = getMaxSeqId();
			lLngHrSeqNo = lLngMaxRefId;
			if(chkEntryWfHrfMst(locCode,lLstDocId) == "N")
			{
				for(int i=0; i<lLstDocId.size(); i++)
				{
					lObjWfDocMst = new com.tcs.sgv.fms.valueobject.WfDocMst();
					lObjWfDocMst = (com.tcs.sgv.fms.valueobject.WfDocMst)lObjWfDocMstDao.read(Long.parseLong(lLstDocId.get(i).toString()));	
					lStrDocDesc = getDocDesc((Long) lLstDocId.get(i));				
					WfHierarchyReferenceMst lObjWfHiRefMst = new WfHierarchyReferenceMst();
					//Long.parseLong(IDGenerateDelegate.getNextIdWODbidLocationId("wf_hierarchy_reference_mst", objectArgs));
					//lLngHrSeqNo = IFMSCommonServiceImpl.getNextSeqNum("wf_hierarchy_reference_mst", objectArgs);
					lObjWfHiRefMst.setHierachySeqId(lLngHrSeqNo);
					lObjWfHiRefMst.setHierachyRefId(lLngMaxRefId);
					lObjWfHiRefMst.setWfDocMst(lObjWfDocMst);
					lObjWfHiRefMst.setReferenceName(lStrDocDesc);
					lObjWfHiRefMst.setDescription(lStrDocDesc);
					lObjWfHiRefMst.setCrtDt(DBUtility.getCurrentDateFromDB());
					lObjWfHiRefMst.setStartDate(DBUtility.getCurrentDateFromDB());
					lObjWfHiRefMst.setActivateFlag(1);
					lObjWfHiRefMst.setCmnDatabaseMst(dbId);
					lObjWfHiRefMst.setLocationCode(locCode);
					lObjWfHiRefMst.setLangId(lLngId.toString());
					lObjWfHiRefMst.setWfOrgUsrMpgMstByCrtUsr(lwfOrgUsrMpgMst);
					hibSession.save(lObjWfHiRefMst);
					hibSession.flush();
					lLngHrSeqNo++;
				}
			}
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
		}
	}
	
	public Long getMaxSeqId()
	{
		Long lLngSeqId = null;
		Session hibSession = getSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(hierachySeqId) FROM WfHierarchyReferenceMst");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lLngSeqId = (Long) lQuery.list().get(0);
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		return ++lLngSeqId;
	}
	
	public String getDocDesc(Long DocId)
	{
		String lStrDocDesc = "";
		Session hibSession = getSession();
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT docName FROM WfDocMst WHERE docId =:docId");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("docId", DocId);
			lStrDocDesc = lQuery.list().get(0).toString();
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
		}
		
		return lStrDocDesc;
	}
	
	public Long getMaxRefId(String Module)
	{
		Long lLngMaxId = null;
		Session hibSession = getSession();
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(hierachyRefId) FROM WfHierarchyReferenceMst ");
			if(Module.equals("DCPS")){
				lSBQuery.append("WHERE hierachyRefId LIKE '7%'");
			}else{
				lSBQuery.append("WHERE hierachyRefId LIKE '8%'");
			}
			Query lQuery = hibSession.createQuery(lSBQuery.toString());			
			lLngMaxId = (Long) lQuery.list().get(0);
			if(lLngMaxId != null){
				lLngMaxId++;
			}
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
		}
		
		return lLngMaxId;
	}
	
	  /*to get the post for the roles*/
	  public String getPostRole(String postId,String role)
	  {
		  Session hibSession = getSession();
		  String queryPostRolerlt = null;
		  String stat = null;
		  
		  queryPostRolerlt =
			  "SELECT * FROM ACL_POSTROLE_RLT APR WHERE APR.ROLE_ID =:roleId AND APR.POST_ID=:postId AND APR.ACTIVATE_FLAG = 1";
		  
		  Query sqlPostRoleRlt = hibSession.createSQLQuery(queryPostRolerlt);
		  
		  sqlPostRoleRlt.setString("roleId", role);
		  sqlPostRoleRlt.setString("postId", postId);
		  
			List resPostId = sqlPostRoleRlt.list();
			
			if (resPostId != null && resPostId.size() >0){
				stat = "Y";
			}else{
				stat = "N";
			}
			
	  	return stat;
	  }
	  
		/*get  ref_id for insertion in WF_HIERACHY_POST_MPG*/
	  public ArrayList<Long> getRefId(String locCode, String Desc) throws Exception
	  {
		  ArrayList<Long> resList = new ArrayList<Long>();	
		  ArrayList lLstDoc = new ArrayList<Long>();
		  Session hibSession = getSession();
		  
		  if(Desc.equals("GPF")){
			  lLstDoc.add(Long.parseLong("800001"));
			  lLstDoc.add(Long.parseLong("800002"));
			  lLstDoc.add(Long.parseLong("800003"));
		  }
		  
		  StringBuffer sbRefId = new StringBuffer();
	  		sbRefId.append("select distinct wm.hierachyRefId " +
					"  from WfHierarchyReferenceMst wm" +
					" where wm.locationCode = :locCode" +
					" and wm.wfDocMst.docId in (:DocList)");
					
			Query sqlRefId = hibSession.createQuery(sbRefId.toString());

			sqlRefId.setString("locCode", locCode);
			sqlRefId.setParameterList("DocList", lLstDoc);

			List resRefId = sqlRefId.list();
			if (resRefId != null) {
				for(int i =0;i<resRefId.size();i++)
				{
					resList.add(Long.parseLong(resRefId.get(i).toString()));
				}
			}
			return resList;
	  }
	  
	 /*deactivate the flag for particular post in WF_HIERACHY_POST_MPG and ACL_POSTROLE_RLT */ 
	 public void delLevelAndRoleEntries(ArrayList roleId,String Pid,Long uptPostId,Long uptUserId,String locCode,
			 ArrayList<Long> refId,String Desc,ArrayList levelId) throws Exception
	 {
	 	Session hibSession = getSession();
		
		if(!refId.isEmpty()){
			StringBuffer sbDelRoleId = new StringBuffer();
			 sbDelRoleId.append("UPDATE WF_HIERACHY_POST_MPG WPG SET WPG.ACTIVATE_FLAG = 2 ," +
			 					" WPG.END_DATE =:dt, " +
			 					" WPG.LST_UPD_USR = :userId, WPG.LST_UPD_DT =:dt " +
								" WHERE WPG.POST_ID = :pid" + 
								" AND WPG.HIERACHY_REF_ID in(:refId) " + 
								" AND WPG.LOC_ID = :locCode" + 
								" AND WPG.LEVEL_ID in (:levelId) ");
		
			 Query strDelQuery =hibSession.createSQLQuery(sbDelRoleId.toString());
			 strDelQuery.setLong("userId", Long.parseLong("1"));
			 strDelQuery.setDate("dt", DBUtility.getCurrentDateFromDB());
			 strDelQuery.setLong("pid",Long.parseLong(Pid));
			 strDelQuery.setString("locCode", locCode);
			 strDelQuery.setParameterList("levelId", levelId);
			 strDelQuery.setParameterList("refId", refId);
			 strDelQuery.executeUpdate();
			 hibSession.flush();
		}
			 
			 	 
		 if(roleId !=null && !roleId.isEmpty())
		 {
			 StringBuffer sbUpdateFlag = new StringBuffer();
			 //sbUpdateFlag.append("DELETE FROM AclPostroleRlt APR ");
			 //sbUpdateFlag.append("UPDATE AclPostroleRlt APR SET APR.cmnLookupMstByActivate.cmnLanguageMst.activateFlag = :activateFlag, ");
			 //sbUpdateFlag.append("APR.orgUserMstByCreatedBy.orgUserMstByUpdatedBy.userId
			 //sbUpdateFlag.append("WHERE APR.aclRoleMst.roleId IN (:roleId) AND APR.orgPostMst.postId =:postId");
		
			 sbUpdateFlag.append("UPDATE ACL_POSTROLE_RLT SET ACTIVATE_FLAG = :activateFlag, UPDATED_BY =:userId, UPDATED_BY_POST =:postId, ");
			 sbUpdateFlag.append("UPDATED_DATE =:dt ");
			 sbUpdateFlag.append("WHERE ROLE_ID IN (:roleId) AND POST_ID = :Pid");
			 Query strUpdateFlag = hibSession.createSQLQuery(sbUpdateFlag.toString());
			 
			 strUpdateFlag.setLong("Pid",Long.parseLong(Pid));
			 strUpdateFlag.setLong("activateFlag",Long.valueOf("2"));
			 strUpdateFlag.setParameterList("roleId", roleId);
			 strUpdateFlag.setLong("userId",uptUserId);
			 strUpdateFlag.setLong("postId",uptPostId);
			 strUpdateFlag.setDate("dt", DBUtility.getCurrentDateFromDB());
			 
			 strUpdateFlag.executeUpdate();
			 hibSession.flush();
		 }
			 
	 }
	
	 /*if post exist for that level but is deactivated then activate it instead of adding it again*/
	 public void updateActFlagForPost(ArrayList roleId,String Pid,Long uptPostId,Long uptUserId,ArrayList levelId,String lStrLocCode,ArrayList refId) throws Exception
	 {
	 	Session hibSession = getSession();
	 	//ArrayList roleId1 = getRoleForLevel(type,levelId,false,Pid);
	 	ArrayList ilevelId =new ArrayList();
	 	
	 	if(!levelId.isEmpty()){
			 StringBuffer sbDelRoleId = new StringBuffer();
			 sbDelRoleId.append("UPDATE WF_HIERACHY_POST_MPG WPG SET WPG.ACTIVATE_FLAG = 1, " +
					 			" WPG.END_DATE = null , " +
								" WPG.LST_UPD_USR = :userId, WPG.LST_UPD_DT =:dt" +
								" WHERE WPG.POST_ID = :pid" + 
								" AND WPG.HIERACHY_REF_ID in(:refId) " + 
								" AND WPG.LOC_ID = :locCode" + 
								" AND WPG.LEVEL_ID in (:levelId) ");
		
			 Query strDelQuery =hibSession.createSQLQuery(sbDelRoleId.toString());
			 
			 strDelQuery.setLong("pid", Long.parseLong(Pid));
			 strDelQuery.setString("locCode", lStrLocCode);
			 strDelQuery.setParameterList("levelId", levelId);
			 strDelQuery.setParameterList("refId", refId);
			 strDelQuery.setLong("userId", Long.parseLong("1"));
			 strDelQuery.setDate("dt", DBUtility.getCurrentDateFromDB());
			 strDelQuery.executeUpdate();
			 hibSession.flush();
	 	} 
			 			 			 
			 
			 if(roleId !=null && !roleId.isEmpty())
			 {
				 StringBuffer sbUpdateFlag = new StringBuffer();
				 sbUpdateFlag.append(
						 "UPDATE ACL_POSTROLE_RLT APR" +
						 "   SET APR.ACTIVATE_FLAG   = :activateFlag," + 
						 "       APR.UPDATED_BY      = :userId," + 
						 "       APR.UPDATED_DATE    = :dt," + 
						 "       APR.UPDATED_BY_POST = :postId," +
						 "		 APR.END_DATE = null " + 
						 " WHERE APR.POST_ID = :pid " +
						 " AND  APR.ROLE_ID IN (:roleId) ");
			
				 Query strUpdateFlag =hibSession.createSQLQuery(sbUpdateFlag.toString());
				 
				 strUpdateFlag.setLong("pid", Long.parseLong(Pid));
				 strUpdateFlag.setLong("postId",uptPostId);
				 strUpdateFlag.setLong("userId", uptUserId);
				 strUpdateFlag.setDate("dt", DBUtility.getCurrentDateFromDB());
				 strUpdateFlag.setParameter("activateFlag",Long.parseLong("1"));
				 strUpdateFlag.setParameterList("roleId", roleId);
				 strUpdateFlag.executeUpdate();
				 hibSession.flush();
			 }
	 }
	 
	 /*while searching for a user we call this method*/
	 public String getUserDtls(String userName,String locCode,Long langId) throws Exception
	  {
		 String userDtls = ""; 
		 Long postId = new Long("0");
		 String uName = "";
		  Session hibSession= getSession();
		  
		  StringBuffer sbUserDtls = new StringBuffer();
		  sbUserDtls.append("SELECT OUP.POST_ID, OEM.EMP_FNAME, OEM.EMP_MNAME, OEM.EMP_LNAME, OUP.ACTIVATE_FLAG \n");
		  sbUserDtls.append("FROM ORG_EMP_MST OEM, ORG_USERPOST_RLT OUP, ORG_USER_MST OUM, ORG_POST_DETAILS_RLT OPDR \n");
		  sbUserDtls.append("WHERE UPPER(OUM.USER_NAME)=UPPER(:userName) AND OUM.USER_ID = OEM.USER_ID \n");
		  sbUserDtls.append("AND OUM.USER_ID = OUP.USER_ID AND OUP.POST_ID = OPDR.POST_ID \n");
		  sbUserDtls.append("AND OPDR.LOC_ID = :locCode AND OPDR.LANG_ID = :langId");
//	  				"select oup.orgPostMstByPostId.postId, oem.empFname, oem.empMname, oem.empLname,oup.activateFlag,oup.cmnLookupUserPostType.lookupId" +
//	  				"  from OrgUserMst om, OrgUserpostRlt oup, OrgEmpMst oem , OrgPostDetailsRlt opd " + 
//	  				"  where upper(om.userName) = upper(:userName) and om.userId = oup.orgUserMst.userId and" + 
//	  				"       oem.orgUserMst.userId = om.userId and oup.orgPostMstByPostId.postId= opd.orgPostMst.postId " +
//	  				"       and opd.cmnLocationMst.locId =:locCode and opd.cmnLanguageMst.langId= :langId and " +
//	  				" 		  oem.cmnLanguageMst.langId = :langId ");

			Query sqlUserDtls = hibSession.createSQLQuery(sbUserDtls.toString());

			sqlUserDtls.setString("userName", userName);
			sqlUserDtls.setString("locCode", locCode);
			sqlUserDtls.setLong("langId", langId);
			
			List resUserDtls = sqlUserDtls.list();
			if (resUserDtls != null && resUserDtls.size() >0) 
			{
				for(int i=0;i<resUserDtls.size();i++)
				{
					Object[] tuple= (Object[])resUserDtls.get(i);
					postId= Long.parseLong(tuple[0].toString());
					uName = ((tuple[1]!=null?tuple[1]:"")+" "+(tuple[2]!=null?tuple[2]:"")+" "+(tuple[3]!=null?tuple[3]:"")).toString();
					if(tuple[4].toString().equals("1"))
					{
						userDtls = postId+"~"+uName;
						break;
					}
					else
						userDtls="NoPost";
				}
			}
			else
				userDtls="NoUser";
			
		
			return userDtls;
	  }
	
	 /*add the entry in WF_HIERACHY_POST_MPG */
	public String addPostInHierarchy(String Desc,String locCode,String postId,Long updUserId,Map objectArgs,ArrayList levelId,
			ArrayList<Long> refId,Long lLngUserId) throws Exception
	{
		String resString="";		
		UserConfigDAOImpl WfHierachyVO = new UserConfigDAOImpl(WfHierachyPostMpg.class,this.sessionFactory);
		
		if(chkEntryWfOrgPost(postId) == "N"){
			addEntryWfOrgPost(postId);
		}
		if(chkEntryWfOrgLoc(locCode) == "N"){
			addEntryWfOrgLoc(locCode);
		}
		if(chkEntryWfOrgUser(lLngUserId) == "N"){
			addEntryWfOrgUser(lLngUserId);
		}
		if(chkEntryWfOrgUser(updUserId) == "N"){
			addEntryWfOrgUser(updUserId);
		}
		
		if(levelId != null && levelId.size()>0)
		{
		  Session hibSession = getSession();
		
		  WfOrgPostMpgMstDaoImpl lwfOrgPostMpgMstDaoImpl=new WfOrgPostMpgMstDaoImpl(WfOrgPostMpgMst.class, this.sessionFactory);
		  WfOrgPostMpgMst lwfOrgPostMpgMst = lwfOrgPostMpgMstDaoImpl.read(postId);
		
		  WfOrgUsrMpgMstDao lwfOrgUsrMpgMstDao = new WfOrgUsrMpgMstDaoImpl(WfOrgUsrMpgMst.class,this.sessionFactory);
		  WfOrgUsrMpgMst lwfOrgUsrMpgMst = lwfOrgUsrMpgMstDao.read(updUserId.toString());		  
		
		  WfOrgLocMpgMstDaoImpl wfOrgLocMpgMstDaoImpl=new WfOrgLocMpgMstDaoImpl(WfOrgLocMpgMst.class,this.sessionFactory);
		  WfOrgLocMpgMst wfOrgLocMpgMst=wfOrgLocMpgMstDaoImpl.read(locCode);
			
		  Long startSeqNo= new Long("0");
		  // Long startSeqNo = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("wf_hierachy_post_mpg", objectArgs,levelId.size());
		  
		  for(int i=0;i<levelId.size();i++)
		  {
			  if(chkEntryWfHrPost(Long.parseLong(postId), locCode, Integer.parseInt(levelId.get(i).toString())) == "N")
			  {
			  	startSeqNo  = IFMSCommonServiceImpl.getNextSeqNum("wf_hierachy_post_mpg", objectArgs);
					  				 
		  		WfHierachyPostMpg wfHiePostMpg = new WfHierachyPostMpg();
				wfHiePostMpg.setHierachySeqId(startSeqNo);
				wfHiePostMpg.setParentHierachy(null);
				wfHiePostMpg.setWfOrgPostMpgMst(lwfOrgPostMpgMst);
				wfHiePostMpg.setLevelId(Integer.parseInt(levelId.get(i).toString()));
				if(Desc.equals("GPF")){
					wfHiePostMpg.setHierachyRefId(refId.get(0));
				}else{
					wfHiePostMpg.setHierachyRefId(refId.get(i));
				}
				wfHiePostMpg.setWfOrgUsrMpgMstByCrtUsr(lwfOrgUsrMpgMst);
				wfHiePostMpg.setCrtDt(DBUtility.getCurrentDateFromDB());
				wfHiePostMpg.setWfOrgUsrMpgMstByLstUpdUsr(null);
				wfHiePostMpg.setLstUpdDt(null);
				wfHiePostMpg.setStartDate(DBUtility.getCurrentDateFromDB());
				wfHiePostMpg.setEndDate(null);
				wfHiePostMpg.setActivateFlag(1);
				wfHiePostMpg.setWfOrgLocMpgMst(wfOrgLocMpgMst);
				wfHiePostMpg.setLangId("1");
				wfHiePostMpg.setDueDays(null);
				//WfHierachyVO.create(wfHiePostMpg);
				hibSession.save(wfHiePostMpg);
				hibSession.flush();			
			  }
		  }
		  resString="I";
		  
		}
				return resString;
	}
	
	/*add entry in ACL_POSTROLE_RLT */
	
	public void addEntryInPostRole(String Pid, Long uptPostId, Long updUserId,String type,ArrayList levelId,Map objectArgs) throws Exception
	{
		UserConfigDAOImpl aclPostRoleVO = new UserConfigDAOImpl(AclPostroleRlt.class,this.sessionFactory);
		if(levelId != null && levelId.size()>0)
		{
			OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.sessionFactory);
			OrgPostMst postId = orgPostId.read(Long.parseLong(Pid));
			OrgPostMst crtPostId =  orgPostId.read(uptPostId);
			Session hibSession = getSession();
			AclMstRoleDao lAclMstRoleDao = new AclMstRoleDaoImpl(AclRoleMst.class,this.sessionFactory);
			
			CmnLookupMstDAOImpl actFlag = new CmnLookupMstDAOImpl(CmnLookupMst.class,this.sessionFactory); 
			CmnLookupMst activeFlag = actFlag.read(Long.parseLong("1"));
			
			OrgUserMstDaoImpl userMst = new OrgUserMstDaoImpl(OrgUserMst.class,this.sessionFactory);
			OrgUserMst crtUserId = userMst.read(updUserId);
			
			ArrayList arrRoleId= new ArrayList();
			
			Long roleLevelId= new Long("0");
					
				//get the roleID for workflow
//			if(!type.trim().equals("N"))
//				arrRoleId = getRoleForLevel(type,levelId,false,Pid);
//			else
			arrRoleId = levelId ;
					for (int i = 0; i < arrRoleId.size(); i++) 
					{
						// roleLevelId =IFMSCommonServiceImpl.getNextSeqNumByCount("acl_postrole_rlt", objectArgs);
						
						String status = getPostRole(Pid,arrRoleId.get(i).toString());
						
						if(!status.equals("Y"))
						{
							roleLevelId = IFMSCommonServiceImpl.getNextSeqNum("acl_postrole_rlt", objectArgs);
							AclRoleMst roleId = lAclMstRoleDao.read(Long.parseLong(arrRoleId.get(i).toString()));
							AclPostroleRlt levelRoleVO = new AclPostroleRlt();
							//AclPostroleRlt levelRoleVO = new AclPostroleRlt();
							levelRoleVO.setPostRoleId(roleLevelId++);
							levelRoleVO.setOrgPostMst(postId);
							levelRoleVO.setAclRoleMst(roleId);
							levelRoleVO.setStartDate(DBUtility.getCurrentDateFromDB());
							levelRoleVO.setEndDate(null);
							levelRoleVO.setCmnLookupMstByActivate(activeFlag);
							levelRoleVO.setOrgUserMstByCreatedBy(crtUserId);
							levelRoleVO.setCreatedDate(DBUtility.getCurrentDateFromDB());
							levelRoleVO.setOrgPostMstByCreatedByPost(crtPostId);
							levelRoleVO.setOrgUserMstByUpdatedBy(null);
							levelRoleVO.setUpdatedDate(null);
							levelRoleVO.setOrgPostMstByUpdatedByPost(null);
							aclPostRoleVO.create(levelRoleVO);
							//hibSession.save(levelRoleVO);
							//hibSession.flush();
						}
					}
		}
	}
	
	
	public List getAllDdoCode(String LocCode) throws Exception
	{
		  List lLstDdoCode = null;
		  List<ComboValuesVO> lLstAllDdoCode = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT ddoCode,ddoCode FROM RltDdoOrg WHERE locationCode = :locCode");
			  Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			  lQuery.setParameter("locCode", LocCode);
			  lLstDdoCode = lQuery.list();
			  
			  if(lLstDdoCode != null && lLstDdoCode.size() > 0){
				  Iterator IT = lLstDdoCode.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[]) IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllDdoCode.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllDdoCode;
	}
	
	
	public List getAllSubTreasury(String lStrParentId) throws Exception
	{
		  List lLstSubtr = null;
		  List<ComboValuesVO> lLstAllSubTrsry = new ArrayList<ComboValuesVO>();
		  Session ghibSession = getSession();
		  try{
			  StringBuilder lSBQuery = new StringBuilder();
			  lSBQuery.append("SELECT LOCATION_CODE, LOC_NAME FROM CMN_LOCATION_MST \n");
			  lSBQuery.append("WHERE PARENT_LOC_ID =:parentId ");
			  lSBQuery.append("UNION ");
			  lSBQuery.append("SELECT LOCATION_CODE, LOC_NAME FROM CMN_LOCATION_MST \n");
			  lSBQuery.append("WHERE LOCATION_CODE = :locCode ");
			  Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			  lQuery.setLong("parentId", Long.parseLong(lStrParentId));
			  lQuery.setParameter("locCode", lStrParentId);
			  
			  lLstSubtr = lQuery.list();
			  
			  if(lLstSubtr != null && lLstSubtr.size() > 0){
				  Iterator IT = lLstSubtr.iterator();
				  
				  ComboValuesVO cmbVO = new ComboValuesVO();
				  Object[] lObj = null;
				  while(IT.hasNext()){
					  cmbVO = new ComboValuesVO();
					  lObj = (Object[])IT.next();
					  cmbVO.setId(lObj[0].toString());
					  cmbVO.setDesc(lObj[1].toString());
					  lLstAllSubTrsry.add(cmbVO);
				  }
			  }
		  }catch(Exception e){
			  logger.error(" Error is : " + e, e);
			  throw e;
		  }
		  
		  return lLstAllSubTrsry;
	  }
	 
	public List getAstPostId(String lStrDdoCode) throws Exception
	{
		List lLstData = null;
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQBuilder = new StringBuilder();
			lSBQBuilder.append("SELECT RDA.asstPostId FROM OrgDdoMst ODM, RltDdoAsst RDA \n");
			lSBQBuilder.append("WHERE ODM.ddoCode = :ddoCode AND ODM.postId = RDA.ddoPostId");
			Query lQuery = ghibSession.createQuery(lSBQBuilder.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			
			lLstData = lQuery.list();
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		
		return lLstData;
	}
	
	public String chkEntryDDOAst(Long Pid,String lStrLocCode) throws Exception
	{
		List lLstResData = null;
		String lStrRes = "";
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM WfHierachyPostMpg WHERE wfOrgPostMpgMst.postId = :pid AND wfOrgLocMpgMst.locId = :locId \n");
			lSBQuery.append("AND levelId = :levelId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("pid", Pid.toString());
			lQuery.setParameter("locId",lStrLocCode);
			lQuery.setParameter("levelId",10);
			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrRes = "Y";
			}else{
				lStrRes = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		
		return lStrRes;
	}
	
	public String chkEntryWfHrfMst(String lStrLocCode,List docId) throws Exception
	{
		List lLstResData = null;
		String lStrRes = "";
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM WfHierarchyReferenceMst WHERE locationCode = :locCode AND wfDocMst.docId IN (:docId)");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", lStrLocCode);
			lQuery.setParameterList("docId", docId);
			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrRes = "Y";
			}else{
				lStrRes = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		
		return lStrRes;
	}
	
	public String chkEntryWfHrPost(Long Pid,String lStrLocCode,Integer lIntLevelId) throws Exception
	{
		List lLstResData = null;
		String lStrRes = "";
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT * FROM WF_HIERACHY_POST_MPG WHERE POST_ID = :pid AND LOC_ID = :locId \n");
			lSBQuery.append("AND LEVEL_ID = :levelId AND ACTIVATE_FLAG = 1");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("pid", Pid.toString());
			lQuery.setParameter("locId",lStrLocCode);
			lQuery.setParameter("levelId",lIntLevelId);
			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrRes = "Y";
			}else{
				lStrRes = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		
		return lStrRes;
	}
	
	public Long getUsrIdFromPostId(String lStrPostId)
	{
		Long lLngUserId = null;
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM OrgUserpostRlt WHERE orgPostMstByPostId.postId = :postId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("postId", Long.parseLong(lStrPostId));
			lLngUserId = (Long) lQuery.list().get(0);
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		return lLngUserId;
	}
}
