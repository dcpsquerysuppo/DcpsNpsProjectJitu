package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.valueobject.SgvaBuddemandMst;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class BudHeadDAOImpl extends GenericDaoHibernateImpl<TrnBillRegister,Long> implements BudHeadDAO 
{
	Log logger = LogFactory.getLog(getClass());
	public BudHeadDAOImpl(Class<TrnBillRegister> type, SessionFactory sessionFactory)
	{
		super(type);
		setSessionFactory(sessionFactory);
	} 
	
	/**
	 * This method returns value object of TrnBillBudheadDtls by bill no
	 * @param billNo
	 * @return TrnBillBudheadDtls
	 */
	
	public String getHeadDetails(String langId,Integer finYrId)
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			Session hbsession = getSession();
			Query query = hbsession.createSQLQuery("SELECT SM.PLNGCODE," +
												" 			SM.DEMAND_CODE," +
												" 			SM.BUDMJRHD_CODE," +
												" 			SM.BUDSUBMJRHD_CODE," +
												" 			SM.BUDMINHD_CODE," +
												" 			SM.BUDSUBHD_CODE " +
												"	FROM SGVA_BUDSUBHD_MST SM " +
												"	WHERE SM.LANG_ID = :langId " +
												"	AND SM.FIN_YR_ID =:finYrId " +
												"	AND SM.STATUS=:status AND SM.REVISION_ID = 1 " +
												"	UNION " +
												"  	SELECT EM.PLNGCODE, " +
												"         EM.BUDDEMAND_CODE, " +
												"         EM.BUDMJRHD_CODE, " +
												"         EM.BUDSUBMJRHD_CODE, " +
												"         EM.BUDMINHD_CODE, " +
												"         EM.BUDSUBHD_CODE " +
												"    FROM SGVA_EXPEST_WRK_MST EM " +
												"   WHERE EM.LANG_ID = :langId AND EM.STATUS = :status AND " +
												"         EM.PLNGCODE IS NOT NULL " +
												"   GROUP BY EM.PLNGCODE, " + 
												"            EM.BUDDEMAND_CODE, " +
												"            EM.BUDMJRHD_CODE, " +
												"            EM.BUDSUBMJRHD_CODE, " +
												"            EM.BUDMINHD_CODE, " +
												"            EM.BUDSUBHD_CODE " +
												"	ORDER BY PLNGCODE," +
												" 			DEMAND_CODE," +
												" 			BUDMJRHD_CODE," +
												" 			BUDSUBMJRHD_CODE," +
												" 			BUDMINHD_CODE	");
			query.setInteger("finYrId",finYrId);
			query.setString("langId", langId);
			query.setString("status",DBConstants.Lookup_ACTIVE);
			List resList = query.list();
			if(resList!=null && resList.size()>0)
			{
				for(Object row : resList)
				{
					Object cols[] = (Object[]) row;
					if(cols[0]!=null && !cols[0].equals(""))
						sb.append(cols[0].toString());
					else
						sb.append("0");
					sb.append(",");
					sb.append(cols[1].toString());
					sb.append(",");
					sb.append(cols[2].toString());
					sb.append(",");
					sb.append(cols[3].toString());
					sb.append(",");
					sb.append(cols[4].toString());
					sb.append(",");
					sb.append(cols[5].toString());
					sb.append("^");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
	public String getHeadDetails(String langId,Integer finYrId,String demandNo,String majorHead)
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			Session hbsession = getSession();
			Query query = hbsession.createQuery("select sm.plngcode,sm.demandCode,sm.budmjrhdCode,sm.budsubmjrhdCode,sm.budminhdCode,sm.budsubhdCode from SgvaBudsubhdMst sm where sm.langId = :langId and sm.finYrId =:finYrId and sm.demandCode=:demandNo and sm.budmjrhdCode=:majorHead  order by 2, 3, 4, 5");
			query.setInteger("finYrId",finYrId);
			query.setString("langId", langId);
			query.setString("demandNo",demandNo);
			query.setString("majorHead",majorHead);
			List resList = query.list();
			if(resList!=null && resList.size()>0)
			{
				for(Object row : resList)
				{
					Object cols[] = (Object[]) row;
					if(cols[0]!=null && !cols[0].equals(""))
						sb.append(cols[0].toString());
					else
						sb.append("0");
					sb.append(",");
					sb.append(cols[1].toString());
					sb.append(",");
					sb.append(cols[2].toString());
					sb.append(",");
					sb.append(cols[3].toString());
					sb.append(",");
					sb.append(cols[4].toString());
					sb.append(",");
					sb.append(cols[5].toString());
					sb.append("^");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
	public Map getHeadDetails(String langId,Integer finYrId,String majorHead)
	{
		StringBuffer sb = new StringBuffer();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		HashSet<String> oDemandList  = new HashSet<String>();
		List<SgvaBuddemandMst> oDemands = new ArrayList<SgvaBuddemandMst>();
		try
		{
			Session hbsession = getSession();
			Query query = hbsession.createQuery("select sm.plngcode,sm.demandCode,sm.budmjrhdCode,sm.budsubmjrhdCode," +
					" sm.budminhdCode,sm.budsubhdCode from SgvaBudsubhdMst sm " +
					" where sm.langId = :langId " +
					" and sm.finYrId =:finYrId " +
					" and sm.budmjrhdCode=:majorHead  " +
					" order by 2, 3, 4, 5");
			query.setInteger("finYrId",finYrId);
			query.setString("langId", langId);
			query.setString("majorHead",majorHead);
			List resList = query.list();
			
			if(resList!=null && resList.size()>0)
			{
				for(Object row : resList)
				{
					Object cols[] = (Object[]) row;
					if(cols[0]!=null && !cols[0].equals(""))
						sb.append(cols[0].toString());
					else
						sb.append("0");
					sb.append(",");
					sb.append(cols[1].toString());
					sb.append(",");
					sb.append(cols[2].toString());
					sb.append(",");
					sb.append(cols[3].toString());
					sb.append(",");
					sb.append(cols[4].toString());
					sb.append(",");
					sb.append(cols[5].toString());
					sb.append("^");
					oDemandList.add(cols[1].toString());
				}
			}
			
			 for (Object demand : oDemandList) 
	            {
				 	SgvaBuddemandMst sgvaDmndVO = new SgvaBuddemandMst();
	            	sgvaDmndVO.setDemandCode((String)demand);
	            	oDemands.add(sgvaDmndVO);
				}
			
			dataMap.put("headDetail", sb.toString());
			dataMap.put("demandList", oDemands);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dataMap;
	}
}
