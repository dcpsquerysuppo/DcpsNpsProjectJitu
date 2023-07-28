package com.tcs.sgv.dcps.dao;

import java.text.DecimalFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class OpgmFileViewDaoImpl extends GenericDaoHibernateImpl implements
OpgmFileViewDao {

	private final Log gLogger;
	org.hibernate.Session ghibSession;
	
	public OpgmFileViewDaoImpl(Class type,SessionFactory sessionFactory) {
		super(type);
		this.gLogger = LogFactory.getLog(this.getClass());
		this.ghibSession = null;
		this.ghibSession = sessionFactory.getCurrentSession();
		this.setSessionFactory(sessionFactory);
	}

	/*
	 method transfer to formS1daoImpl
	 public String generateOpgmFile(String Treasury,int FileEmpCount) {
		String Seq="";
		try{
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        //code for generating sequence
		  String Count = "";
		    StringBuilder Strbld = new StringBuilder();
		    
		    Strbld.append(" SELECT count(1)+1 FROM OPGM_FILE_DTLS where Treasury ='"+Treasury+"' and FILE_YEAR='"+year+"' and FILE_MONTH = '"+month+"'");
		    
		    SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		    
		    Count = lSEQQuery.list().get(0).toString();
		 
		  //Seq=Treasury+year+month+FileEmpCount+"00"+Count;
		    Seq=Treasury+year+month+FileEmpCount+"00"+Count;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" insert into OPGM_FILE_DTLS values( '"+Seq+"', '"+year+"', '"+month+"', '"+FileEmpCount+"',0,sysdate,'"+Treasury+"') ");
		
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lSBQuery = new StringBuilder();
		lQuery.executeUpdate();		}
		catch(Exception e){
			System.out.println("Exception e"+e);
            e.printStackTrace();
		}
		return Seq;
	}
*/
	public List getOpgmFileList(String Treasury, String month, String year, String SevaarthID, String EmpName){
		List lLstOpgmFile = null;
		StringBuilder lSBQuery = null;
		
		try {
	         if (SevaarthID.length() == 0 && EmpName.length() == 0) {
	            lSBQuery = new StringBuilder();
	            lSBQuery.append(" select FILE_ID ,FILE_YEAR ,FILE_MONTH ,TREASURY,EMPLOYEE_COUNT,DECODE(FILE_STATUS,'0','File Generated','-1','File Deleted','1','File is fowarded to NSDL')  from OPGM_FILE_DTLS where TREASURY = '" + Treasury + "' and FILE_YEAR='" + year + "' and FILE_MONTH = '" + month + "' and FILE_STATUS not in (-1) order by FILE_GENERATION_DATE ");
	         } else if (SevaarthID.length() != 0) {
	            lSBQuery = new StringBuilder();
	            lSBQuery.append("select dtls.FILE_ID ,dtls.FILE_YEAR ,dtls.FILE_MONTH ,dtls.TREASURY,dtls.EMPLOYEE_COUNT,DECODE(dtls.FILE_STATUS,'0','File Generated','-1','File Deleted','1','File is fowarded to NSDL') ");
	            lSBQuery.append("from OPGM_FILE_DTLS dtls ");
	            lSBQuery.append("join FRM_FORM_S1_DTLS form on form.OPGM_ID=dtls.FILE_ID and form.SEVARTH_ID='" + SevaarthID.trim() + "' and dtls.FILE_STATUS not in (-1) ");
	            lSBQuery.append("where dtls.TREASURY = '" + Treasury + "' and dtls.FILE_YEAR='" + year + "' order by FILE_GENERATION_DATE ");
	         } else if (EmpName.length() != 0) {
	            lSBQuery = new StringBuilder();
	            lSBQuery.append("select dtls.FILE_ID ,dtls.FILE_YEAR ,dtls.FILE_MONTH ,dtls.TREASURY,dtls.EMPLOYEE_COUNT,DECODE(dtls.FILE_STATUS,'0','File Generated','-1','File Deleted','1','File is fowarded to NSDL') ");
	            lSBQuery.append("from OPGM_FILE_DTLS dtls ");
	            lSBQuery.append("join FRM_FORM_S1_DTLS form on form.OPGM_ID=dtls.FILE_ID and form.EMP_NAME='" + EmpName + "' and dtls.FILE_STATUS not in (-1) ");
	            lSBQuery.append("where dtls.TREASURY = '" + Treasury + "' and dtls.FILE_YEAR='" + year + "' order by FILE_GENERATION_DATE ");
	         }
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstOpgmFile = stQuery.list();
				logger.info("getOpgmFileList lLstEmp count-->" + lLstOpgmFile.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstOpgmFile;
	}
	 public String getOpgmFileMonth(String Treasury, String month, String year, String SevaarthID, String EmpName) {
	      String lLstOpgmFile = "N/A";
	      StringBuilder lSBQuery = null;
	      String var8 = "";

	      try {
	         if (SevaarthID.length() == 0 && EmpName.length() == 0) {
	            lSBQuery = new StringBuilder();
	            lSBQuery.append(" select FILE_MONTH   from OPGM_FILE_DTLS where TREASURY = '" + Treasury + "' and FILE_YEAR='" + year + "' and FILE_MONTH = '" + month + "' and FILE_STATUS not in (-1) order by FILE_GENERATION_DATE ");
	         } else if (SevaarthID.length() != 0) {
	            lSBQuery = new StringBuilder();
	            lSBQuery.append("select FILE_MONTH ");
	            lSBQuery.append("from OPGM_FILE_DTLS dtls ");
	            lSBQuery.append("join FRM_FORM_S1_DTLS form on form.OPGM_ID=dtls.FILE_ID and form.SEVARTH_ID='" + SevaarthID.trim() + "' and dtls.FILE_STATUS not in (-1) ");
	            lSBQuery.append("where dtls.TREASURY = '" + Treasury + "' and dtls.FILE_YEAR='" + year + "' order by FILE_GENERATION_DATE ");
	         } else if (EmpName.length() != 0) {
	            lSBQuery = new StringBuilder();
	            lSBQuery.append("select FILE_MONTH ");
	            lSBQuery.append("from OPGM_FILE_DTLS dtls ");
	            lSBQuery.append("join FRM_FORM_S1_DTLS form on form.OPGM_ID=dtls.FILE_ID and form.EMP_NAME='" + EmpName + "' and dtls.FILE_STATUS not in (-1) ");
	            lSBQuery.append("where dtls.TREASURY = '" + Treasury + "' and dtls.FILE_YEAR='" + year + "' order by FILE_GENERATION_DATE ");
	         }

	         Query stQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
	         lLstOpgmFile = stQuery.list().get(0).toString();
	      } catch (Exception var10) {
	         var10.printStackTrace();
	         this.logger.error("Error is :" + var10, var10);
	      }

	      return lLstOpgmFile;
	   }
	public List getMonths() {

		String query = "select monthId,monthName from SgvaMonthMst where monthId < 13";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;

		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}
	public List getFinyear() {
		
		Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        if(4>month+1){
            year = year-1;
        }
		String query = "select finYearCode,finYearCode from SgvcFinYearMst where finYearCode between '2015' and '"+year+"' order by finYearCode ASC";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = this.ghibSession.createQuery(sb.toString());
		List lLstResult = selectQuery.list();
		// this.//ghibSession.disconnect();
		ComboValuesVO lObjComboValuesVO = null;
		if ((lLstResult != null) && (lLstResult.size() != 0)) {
			lLstReturnList = new ArrayList();
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				Object[] obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}
	@Override
	public void deleteOpgmFile(String treasury, String fileNo) {
		// TODO Auto-generated method stub
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" update OPGM_FILE_DTLS set File_Status=-1 where Treasury='"+treasury+"' and File_id ='"+fileNo+"'");
			
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lSBQuery = new StringBuilder();
			lQuery.executeUpdate();
			//lSBQuery.append("update FRM_FORM_S1_DTLS set Stage=1,Opgm_Id=null where Opgm_Id='"+fileNo+"' and Stage=2 ");
			lSBQuery.append("update FRM_FORM_S1_DTLS set Stage=2,Opgm_Id=null where Opgm_Id='"+fileNo+"' and Stage=3 ");////$t OPGM
			
			SQLQuery lQuery1 = ghibSession.createSQLQuery(lSBQuery.toString());
			int status = lQuery1.executeUpdate();
		}

	@Override
	public void downloadOpgmFile(String fileNo) {
		/*HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String directoryPath = request.getSession().getServletContext().getRealPath("/") + "/" + fileNo;*/
					}
	 public String ValidSevaarthID(String SevaarthID) {
	      String Count = "";

	      try {
	         StringBuilder Strbld = new StringBuilder();
	         Strbld.append(" select count(*) from mst_dcps_emp where SEVARTH_ID ='" + SevaarthID + "'");
	         SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
	         Count = lSEQQuery.list().get(0).toString();
	      } catch (Exception var5) {
	         System.out.println("Exception e" + var5);
	         var5.printStackTrace();
	      }

	      return Count;
	   }
}//End class
