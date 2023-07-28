package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.CmnCountryMst;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.common.valueobject.CmnStateMst;
import com.tcs.sgv.common.valueobject.CmnTalukaMst;
import com.tcs.sgv.common.valueobject.CmnVillageMst;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.common.valueobject.MstBankPay;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.eis.valueobject.HrEisBankMst;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.eis.valueobject.*;

public class BankMasterDAOImpl extends GenericDaoHibernateImpl
implements BankMasterDAO {

	public BankMasterDAOImpl(Class<MstBank> type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
	}

	public List getAllBankMasterData(long langId) {
		Criteria objCrt = null;
		List list = null;

		Session hibSession = getSession();
		String strQuery = "from MstBank where langId =" + langId
		+ " order by bankName";
		Query query = hibSession.createQuery(strQuery);
		list = query.list();

		return list;
	}
	
	public List searchBankMasterData(long langId, String lStrBankName) {		
		List list = null;

		Session hibSession = getSession();
		String strQuery = "from MstBank where langId =" + langId
		+ " AND bankName = '"+lStrBankName+"'";
		Query query = hibSession.createQuery(strQuery);
		list = query.list();

		return list;
	}
	
	public List bankMasterAutoComplt(long langId, String lStrBankName) {
		
		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;
		try {
			Session ghibSession = getSession();
			
			sb.append("SELECT bankCode, bankName FROM MstBank where langId = :langId AND UPPER(bankName) LIKE UPPER(:bankName)");
			selectQuery = ghibSession.createQuery(sb.toString());
			selectQuery.setParameter("langId", langId);
			selectQuery.setParameter("bankName", lStrBankName+ '%');

			List resultList = selectQuery.list();

			cmbVO = new ComboValuesVO();

			if (resultList != null && resultList.size() > 0) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[1].toString());
					cmbVO.setDesc(obj[1].toString());
					finalList.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in bankMasterAutoComplt : ", e);			
		}
		return finalList;
		
	}

	public List getAllBankIdFromBankCode(long bankCode) {
		Criteria objCrt = null;
		List list = null;

		Session hibSession = getSession();
		String strQuery = "from MstBank where bankCode =" + bankCode;
		Query query = hibSession.createQuery(strQuery);
		list = query.list();

		return list;
	}

	public List getAllBankIds(long bankCode) {
		Criteria objCrt = null;
		List list = null;

		Session hibSession = getSession();
		String strQuery = "from MstBank where bankCode =" + bankCode
		+ " and langId=1 order by bankName";
		Query query = hibSession.createQuery(strQuery);
		list = query.list();

		return list;
	}

	public MstBank getBankIdData(long BankId, long langId) {
		MstBank hrBankInfo = new MstBank();

		Session hibSession = getSession();
		String query1 = "from MstBank as bankLookup where bankLookup.bankId = "
			+ BankId + " and bankLookup.langId =" + langId;
		Query sqlQuery1 = hibSession.createQuery(query1);
		hrBankInfo = (MstBank) sqlQuery1.uniqueResult();

		return hrBankInfo;
	}

	public List checkBankName(String bankName, long langId) {
		List bankNameList = null;

		Session hibSession = getSession();
		String query1 = "from MstBank where lower(trim(bankName)) = lower('"
			+ bankName.trim() + "') ";

		Query sqlQuery1 = hibSession.createQuery(query1);

		// sqlQuery1.setParameter("param",langId);
		bankNameList = sqlQuery1.list();

		return bankNameList;
	}

	
}
