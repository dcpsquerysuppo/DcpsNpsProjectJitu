package com.tcs.sgv.dcps.dao;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.TrnDcpsIntCalReqestDtls;

public class UploadPranDaoImpl extends GenericDaoHibernateImpl implements UploadPranDAO{
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
	public UploadPranDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public Long getFileId() throws Exception
	{
		 List resultList=null;
		 Long fileId=1l;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  select max(file_id)+1 from TRN_PRAN_UPLOAD_DTLS  " );
	       
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        if(resultList!=null && resultList.size()>0 && resultList.get(0)!=null )
	        {
	        	fileId=Long.parseLong(resultList.get(0).toString());
	        }
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return fileId;
		  
	}
	
	public boolean checkIfPranUploaded(String pranNo) throws Exception
	{
		 List resultList=null;
		 boolean flag=false;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  select PRAN_NO from TRN_PRAN_UPLOAD_DTLS where PRAN_NO='"+pranNo+"' and status=1 " );
	       
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        if(resultList!=null && resultList.size()>0 && resultList.get(0)!=null )
	        {
	        	flag=true;
	        }
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return flag;
		  
	}
	
	public List getUploadedDtls() throws Exception
	{
		 List resultList=null;
		 
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  SELECT t.CREATED_DATE,t.FILE_NAME,t.file_id,sum(case when t.status=1 then 1 else 0 end),sum(case when t.status=0 then 1 else 0 end),t.ATTACH_DESC  from TRN_PRAN_UPLOAD_DTLS t group by t.CREATED_DATE,t.FILE_NAME,t.file_id,t.ATTACH_DESC " );
	       
	    
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	      
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	
	public List getUploadedReportDtls(String FileId,String Status) throws Exception
	{
		 List resultList=null;
		 
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  SELECT t.FILE_NAME,t.PPAN,t.PRAN_NO,t.EMP_NAME,to_char(t.DOJ,'dd-MM-yyyy'),to_char(t.PRAN_GEN_DATE,'dd-MM-yyyy'),t.DTO_REG_NO,t.DDO_REG_NO,t.ATTACH_DESC FROM TRN_PRAN_UPLOAD_DTLS t where  " );
	       
	        sb.append(" t.FILE_ID="+FileId+" and t.STATUS="+Status );
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	      
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	//jitu
		public Integer activePranNOUpdate(String pranNo) throws Exception
		{
			 List resultList=null;
			 Integer flag=0;
			 int count=0;
			try
			{
				StringBuilder sb = new StringBuilder();
		        SQLQuery lSBQuery = null;
		        sb.append(" UPDATE mst_dcps_emp SET PRAN_ACTIVE=1 WHERE  PRAN_NO in("+pranNo+")" );
		        lSBQuery = this.ghibSession.createSQLQuery(sb.toString());
		        this.gLogger.info((Object)("lSBQuery.toString()-----" + lSBQuery.toString()));
		        flag=lSBQuery.executeUpdate();
		        if(flag !=null){
		        	count++;
		        }
			}
			catch (Exception e) {
				flag=0;
	            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
	            return flag;
	        }
			System.out.println("nnumber of record updated" +count);
			return flag;
			  
		}



		@Override
		public Integer activePranNOUpdate1(ArrayList emplList) {			
			// List resultList=null;
			 Integer flag=0;
			 int count=0;
			 for (Iterator iterator = emplList.iterator(); iterator.hasNext();) {
				Object object = (Object) iterator.next();
				
				System.out.println("itrated value "+iterator);
				
			}
			try
			{
				StringBuilder sb = new StringBuilder();
		        SQLQuery lSBQuery = null;
		        sb.append(" UPDATE mst_dcps_emp SET PRAN_ACTIVE=1 WHERE  PRAN_NO='"+count+"'" );
		        lSBQuery = this.ghibSession.createSQLQuery(sb.toString());
		        this.gLogger.info((Object)("lSBQuery.toString()-----" + lSBQuery.toString()));
		        flag=lSBQuery.executeUpdate();
		        if(flag !=null){
		        	count++;
		        }
			}
			catch (Exception e) {
				flag=0;
	            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
	            return flag;
	        }
			System.out.println("nnumber of record updated" +count);
			return flag;
			
		}

	}
	
	
	
	


