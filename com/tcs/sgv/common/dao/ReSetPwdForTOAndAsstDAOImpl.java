package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import utils.IEncryption;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class ReSetPwdForTOAndAsstDAOImpl extends GenericDaoHibernateImpl
{
	Session ghibSession = null;
	public ReSetPwdForTOAndAsstDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List loadResetPassword() throws Exception
	{
		List lLstResData = null;
		List<ComboValuesVO> lLstToAndAsstList = new ArrayList<ComboValuesVO>();
		String lStrEmpName = "";
		String lStrFinalData = "N";
		Long lLngUserId = null;
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT userId, userName FROM OrgUserMst where userName like '%_TO' or user_name like '%_TO_ASST' \n");
			lQuery = ghibSession.createQuery(lSBQuery.toString());			
			lLstResData = lQuery.list();
			
			ComboValuesVO cmbVO = null;
			Object []lObj = null;
			if(lLstResData.size() > 0){				
				Iterator IT = lLstResData.iterator();
				while(IT.hasNext()){
					cmbVO = new ComboValuesVO();
					lObj = (Object[]) IT.next();
					cmbVO.setId(lObj[0].toString());
					cmbVO.setDesc(lObj[1].toString());
					lLstToAndAsstList.add(cmbVO);
				}
			}
		}catch (Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstToAndAsstList;
	}
}
