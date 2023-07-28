package com.tcs.sgv.dcps.dao;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.icu.text.DateFormat;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;

public class PostConversionDAOImpl  extends GenericDaoHibernateImpl implements PostConversionDAO{
	Session ghibSession = null;



	@SuppressWarnings("unchecked")
	public PostConversionDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		/*ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		 */
		setSessionFactory(sessionFactory);
		ghibSession = getSession();

	}



	public long getNextSeqNum(int len) {
		long seqId=0;
		StringBuffer sb= new StringBuffer();
		sb.append("select GENERATED_ID from CMN_TABLE_SEQ_MST where upper(table_name)='HR_PAY_POST_MST_HST'");
		Query query = ghibSession.createSQLQuery(sb.toString());
		seqId=Long.parseLong(query.uniqueResult().toString());		
		logger.info("seqId............"+seqId);
		long seqNo= seqId+len;
		logger.info("seqNo............"+seqId);
		StringBuffer sb2= new StringBuffer();
		sb2.append("update CMN_TABLE_SEQ_MST set GENERATED_ID="+seqNo+" where upper(TABLE_NAME)= 'HR_PAY_POST_MST_HST'");
		Query query2 = ghibSession.createSQLQuery(sb2.toString());
		query2.executeUpdate();
		return seqId;
	}

	public void getDetailsforPostHistory(Long postId,String postType,String newPost,String endDate,Long orderid,Long gLngUserId,Long gLngPostId,java.util.Date dtCurDate,int len) throws ParseException
	{    

		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy hh:mm");

		String newendDate=null;		
		String out=null;
		List quertList=null;
		//	Session hiSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("select START_DATE,DSGN_CODE,POST_TYPE_LOOKUP_ID,END_DATE from ORG_POST_MST");
		sb.append(" where POST_ID = " + postId );

		Query query = ghibSession.createSQLQuery(sb.toString());
		long seqID=getNextSeqNum(len);
		quertList= query.list();
		Object list[]=(Object[])quertList.get(0);
		logger.info("size is" +quertList.size());
           
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				
       if(!endDate.equalsIgnoreCase("NA"))
       {
		 out = sdf2.format(sdf1.parse(endDate));
       }
		
		//Date end=sdf2.parse(endDate);
		Date enddate = null;
		/*if(list[3].toString()!=null)
		{
			logger.info("list[3]........."+list[3].toString());
			enddate = sdf2.parse(list[3].toString()); 
			logger.info("fin end date=" +enddate);
		}*/
        
		
	StringBuffer sb3 = new StringBuffer();
		
		sb3.append("select ORDER_HEAD_ID from HR_PAY_ORDER_HEAD_POST_MPG");
		sb3.append(" where POST_ID = " + postId );
		Query query4 = ghibSession.createSQLQuery(sb3.toString());
	   String order_head_id=query4.uniqueResult().toString();
	   Long order1=Long.parseLong(order_head_id);
	   logger.info("order_head_id" +order1);
	   
	   StringBuffer sb10 = new StringBuffer();
		
		sb10.append("select ORDER_ID from HR_PAY_ORDER_HEAD_MPG");
		sb10.append(" where ORDER_HEAD_ID = " + order1 );
		Query query10 = ghibSession.createSQLQuery(sb10.toString());
		String order_id = null;
		Long order2 = null;
	   if(query10.uniqueResult()  != null){
		order_id=query10.uniqueResult().toString();
		
		order2=Long.parseLong(order_id);
	   }
	
		logger.info("order id=" +order2);

		StringBuffer sb1 = new StringBuffer();
		logger.info("start date=" +list[0].toString());
		logger.info("dsgn code=" +list[1].toString());
		logger.info("Look up=" +list[2].toString());
		logger.info("enddate= "+list[3]);
		logger.info("User id :" +gLngUserId);
		logger.info("Post id :" +gLngPostId);


		/* sb1.append("insert into HR_PAY_POST_MST_HST values(");
		 sb1.append(seqID);
		 sb1.append(", "+postId+","+list[0].toString()+" ,"+endDate+","+list[2].toString()+","+list[1].toString()+","+gLngUserId+","+gLngPostId+","+dtCurDate);
		 logger.info(list[0].toString());*/
		sb1.append("INSERT INTO HR_PAY_POST_MST_HST VALUES( ");
		sb1.append(seqID+", "+postId+",{ts '"+list[0].toString()+"'},");

		if(postType.equalsIgnoreCase("T"))
		{
			logger.info("enddate "+enddate);
			if(list[3]!=null)
			sb1.append("'"+list[3].toString()+"'");
			else
				sb1.append(""+null+"");
			//query1.setDate("end_date", enddate);
		}
		else
		{
			sb1.append(" null ");
			logger.info("enddate "+enddate);
			//query1.setString("end_date", "null");
			//query1.setDate("end_date", null);	
		}

		sb1.append(", "+list[2].toString()+",'"+list[1].toString()+"',");
		sb1.append(gLngUserId+","+gLngPostId+",sysdate,"+null+",");
		if(order2 != null)
			sb1.append("'"+order2+"')");
		else sb1.append("null)");
		logger.info("sb1..  "+sb1.toString() );

		/* sb1.append("(:post_hst_id,:post_Id,:start_date,:end_date,:post_type_lookup_ID,:dsgn_code,:updated_by,:updated_by_post,:updated_date) \n"); 
			 logger.info("size is" +quertList.size());
			Query query1 = ghibSession.createSQLQuery(sb1.toString());
			 logger.info("size is" +quertList.size());

			query1.setParameter("post_hst_id", seqID);
		query1.setParameter("post_Id", postId);
		query1.setParameter("start_date", list[0].toString());
		if(postType.equalsIgnoreCase("Temporary"))
		{
			query1.setDate("end_date", enddate);
		}
		else
		{
			//query1.setString("end_date", "null");
			query1.setDate("end_date", null);	
		}
		query1.setParameter("post_type_lookup_ID", list[2].toString());
		query1.setParameter("dsgn_code", list[1].toString());
		query1.setParameter("updated_by", gLngUserId);
		query1.setParameter("updated_by_post", gLngPostId);
		query1.setDate("updated_date", dtCurDate);*/
		int query1 = ghibSession.createSQLQuery(sb1.toString()).executeUpdate();
		//query1.executeUpdate();
		logger.info("query1" +sb1);

		StringBuffer sb2 = new StringBuffer();

		if(newPost.equalsIgnoreCase("T"))
		{
			  try {
			//	Date EndDt = StringUtility.convertStringToDate(endDate);
			
			sb2.append("update ORG_POST_MST set end_Date= '");
			//sb2.append("to_date('"+endDate+"', 'yyyy-MM-dd')");
			sb2.append(out);
			sb2.append("',POST_TYPE_LOOKUP_ID=");
			sb2.append(10001198130L);
			sb2.append(" where POST_ID=" +postId);
			logger.info("look up:" +129);
			logger.info("sb2..  "+sb2.toString() );
			int query2 = ghibSession.createSQLQuery(sb2.toString()).executeUpdate();
			logger.info("query2" +sb2);
			
			logger.info("look up:" +129);
			  }
			  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		else
		{
			sb2.append("update ORG_POST_MST set end_Date= ");
			sb2.append(newendDate);
			sb2.append(",POST_TYPE_LOOKUP_ID=");
			sb2.append(10001198129L);
			sb2.append(" where POST_ID=" +postId);
			logger.info("look up:" +130);
			logger.info("sb2" +sb2);
			Query query2 = ghibSession.createSQLQuery(sb2.toString());
			query2.executeUpdate();
			logger.info("query2" +query2);
			logger.info("look up:" +130);
		
		
		}
		
		

		if(orderid!=-1){
		StringBuffer sb4 = new StringBuffer();
		
		sb4.append("update HR_PAY_ORDER_HEAD_MPG set ORDER_ID=");
		
		
		sb4.append(orderid);
		//sb4.append(orderid);
		sb4.append(",UPDATED_BY=");
		sb4.append(gLngUserId);
		sb4.append(",UPDATED_BY_POST=");
		sb4.append(gLngPostId);
		sb4.append(",UPDATED_DATE= ");
		sb4.append("sysdate");
		sb4.append(" where ORDER_HEAD_ID=");
		sb4.append(order1);
		logger.info("sb4..  "+sb4.toString() );
		int query5 = ghibSession.createSQLQuery(sb4.toString()).executeUpdate();
		logger.info("query5" +sb4);
		
		}
		
		
		
		
		
		
		
		
	}	 

	/*public List getDetailsforPermanentconvert()
	{
		StringBuffer sb3 = new StringBuffer();
		sb3.append("select count(distinct post_id) from HR_PAY_POST_MST_HST");
		sb3.append(" where  POST_TYPE_LOOKUP_ID=10001198129");
		Query query3 = ghibSession.createSQLQuery(sb3.toString());
		return query3.list();

	}

	public List getDetailsforTemporaryconvert()
	{
		StringBuffer sb4 = new StringBuffer();
		sb4.append("select count(distinct post_id) from HR_PAY_POST_MST_HST");
		sb4.append(" where  POST_TYPE_LOOKUP_ID=10001198130");
		Query query4 = ghibSession.createSQLQuery(sb4.toString());
		return query4.list();

	}*/
	public List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn,Long designationId,Long BillGroupId,Long postTypeid)
	{
		List postNameList = new ArrayList();
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("  select pd.post_name,pd.post_id,(select concat(concat(concat(o.emp_fname, ' '), concat(o.emp_mname, ' ')),concat( o.emp_lname,' ')) from org_emp_mst o, org_userpost_rlt up ");
		sb.append("  where  o.lang_id = 1 and o.user_id = up.user_id and up.post_id = pd.post_id and up.end_date is null and up.activate_flag = 1),ds.dsgn_name , P.PSR_NO ");
		sb.append("  , (select mp.DESCRIPTION  from mst_dcps_bill_group mp where  p.bill_no = mp.BILL_GROUP_ID) BillNo ");			
		sb.append(",org.post_type_lookup_id,cmn.lookup_name,org.START_DATE,org.END_DATE,o.ORDER_NAME,o.ORDER_DATE" );
		sb.append(" from org_post_details_rlt pd, org_designation_mst ds,org_post_mst org");
		sb.append(",cmn_lookup_mst cmn,HR_PAY_POST_PSR_MPG p,HR_PAY_ORDER_MST o,HR_PAY_ORDER_HEAD_MPG h,HR_PAY_ORDER_HEAD_POST_MPG op ");
		if(BillGroupId>0)
		{
			sb.append(" ,mst_dcps_bill_group bill");
		}
		
		sb.append("  where pd.loc_id = '" + locId +"' and P.POST_ID = PD.POST_ID and  pd.dsgn_id = ds.dsgn_id and ds.lang_id = 1  " );
		if(postTypeid!=0){
			sb.append(" and org.post_type_lookup_id="+postTypeid);
		}
		sb.append(" and org.post_id = pd.post_id and org.location_code = pd.loc_id and org.activate_flag = 1 ");
		sb.append("  and  upper(pd.post_name) like  upper('%" + lPostName+ "%') ");   
		
		if(designationId>0)
		{
			sb.append(" and ds.dsgn_id="+designationId+" ");
		}
		if(BillGroupId>0)
		{
			sb.append(" and bill.BILL_GROUP_ID="+BillGroupId+" and bill.BILL_GROUP_ID=p.bill_no");
		}
		
		sb.append(" and pd.post_id=org.post_id ");
		sb.append(" and cmn.lookup_id=org.post_type_lookup_id ");
		sb.append(" and op.POST_ID = pd.POST_ID and h.ORDER_HEAD_ID = op.ORDER_HEAD_ID and o.ORDER_ID = h.ORDER_ID ");
		sb.append("   order by pd.post_name  ");
		
		logger.info(sb.toString());
	    Query query = hibSession.createSQLQuery(sb.toString());
	    postNameList = query.list();
	    return postNameList;
	}
	public List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn,Long postTypeid )
	{
		List postNameList = new ArrayList();
		Session hibSession = getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("  select pd.post_name,pd.post_id,(select concat(concat(concat(o.emp_fname, ' '), concat(o.emp_mname, ' ')),concat( o.emp_lname,' ')) from org_emp_mst o, org_userpost_rlt up ");
		sb.append("  where  o.lang_id = 1 and o.user_id = up.user_id and up.post_id = pd.post_id and up.end_date is null and up.activate_flag = 1),ds.dsgn_name , P.PSR_NO ");
		sb.append("  , (select mp.DESCRIPTION  from mst_dcps_bill_group mp where  p.bill_no = mp.BILL_GROUP_ID) BillNo " );			
		sb.append(",org.post_type_lookup_id,cmn.lookup_name,org.START_DATE,org.END_DATE,o.ORDER_NAME,o.ORDER_DATE" );
		sb.append(" from org_post_details_rlt pd, org_designation_mst ds,org_post_mst org");
		sb.append(",cmn_lookup_mst cmn,HR_PAY_POST_PSR_MPG p,HR_PAY_ORDER_MST o,HR_PAY_ORDER_HEAD_MPG h,HR_PAY_ORDER_HEAD_POST_MPG op ");			
		sb.append("  where pd.loc_id = '" + locId +"' and P.POST_ID = PD.POST_ID and  pd.dsgn_id = ds.dsgn_id and ds.lang_id = 1  " );
		if(postTypeid!=0){
			sb.append(" and org.post_type_lookup_id="+postTypeid);
		}
		sb.append("  and  upper(pd.post_name) like  upper('%" + lPostName+ "%') ");   
		sb.append(" and pd.post_id=org.post_id ");
		sb.append(" and cmn.lookup_id=org.post_type_lookup_id ");
		sb.append(" and op.POST_ID = pd.POST_ID and h.ORDER_HEAD_ID = op.ORDER_HEAD_ID and o.ORDER_ID = h.ORDER_ID ");
		
		//sb.append("   order by pd.post_name  ");
		sb.append(" and org.ACTIVATE_FLAG=1 order by pd.CREATED_DATE desc  ");
		
		logger.info("The Query String is:-"+sb.toString());
		logger.info("The Query lPostName is:-"+lPostName);
		
		
	    Query query = hibSession.createSQLQuery(sb.toString());
	    postNameList = query.list();
	    logger.info("Parent Post List size is:-"+postNameList.size());
	    logger.info("Parent Post List size is:-"+postNameList.listIterator());
		return postNameList;
	}

	
	
}
