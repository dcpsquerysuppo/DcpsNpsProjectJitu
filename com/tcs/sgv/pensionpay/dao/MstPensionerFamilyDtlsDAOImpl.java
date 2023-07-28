package com.tcs.sgv.pensionpay.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;


public class MstPensionerFamilyDtlsDAOImpl extends GenericDaoHibernateImpl<MstPensionerFamilyDtls, Long> {

	public MstPensionerFamilyDtlsDAOImpl(Class<MstPensionerFamilyDtls> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}
	Log gLogger = LogFactory.getLog(getClass());

	/*
	 * public List getFamilyDtlsFromPensionerCode(String PensionerCode, long
	 * langId) throws Exception { Session hiSession = getSession(); ArrayList
	 * arrFmDtls = new ArrayList(); StringBuffer strQuery = new StringBuffer();
	 * Iterator itr; Object tuple; List resultList; try {
	 * strQuery.append(" SELECT familyMemberId " +
	 * " FROM MstPensionerFamilyDtls WHERE pensionerCode=:pensionerCode");
	 * 
	 * Query hqlQuey = hiSession.createQuery(strQuery.toString());
	 * hqlQuey.setString("pensionerCode", PensionerCode);
	 * 
	 * resultList = hqlQuey.list();
	 * 
	 * if (!resultList.isEmpty()) for (int i = 0; i < resultList.size(); i++) {
	 * itr = resultList.iterator(); tuple = (Object) itr.next();
	 * arrFmDtls.add((Long) tuple); } } catch (Exception e) {
	 * gLogger.error("Error is," + e, e); throw e; } return arrFmDtls; }
	 */

	/*
	 * public List getListOfFamily(String pensionerCode, long langId) throws
	 * Exception {
	 * 
	 * Session hiSession = getSession(); StringBuffer strQuery = new
	 * StringBuffer(); Iterator itr; Object[] tuple; List resultList; ArrayList
	 * arrlstPnsnrFDtls = null; MstPensionerFamilyDtls mstPensionerFamilyDtls =
	 * null; try {strQuery.append(
	 * " SELECT name, relationship, dateOfBirth, dateOfDeath, majorFlag,salary, handicapeFlag,guardianName FROM MstPensionerFamilyDtls "
	 * + " WHERE pensionerCode=:pensoinerCode ");
	 * 
	 * Query hqlQuery = hiSession.createQuery(strQuery.toString());
	 * hqlQuery.setString("pensoinerCode", pensionerCode); resultList =
	 * hqlQuery.list(); if (!resultList.isEmpty()) { arrlstPnsnrFDtls = new
	 * ArrayList(); itr = resultList.iterator(); while (itr.hasNext()) {
	 * mstPensionerFamilyDtls = new MstPensionerFamilyDtls(); tuple = (Object[])
	 * itr.next(); if (tuple[0] != null)
	 * mstPensionerFamilyDtls.setName(tuple[0].toString()); if (tuple[1] !=
	 * null) mstPensionerFamilyDtls.setRelationship(tuple[1].toString()); if
	 * (tuple[2] != null) mstPensionerFamilyDtls.setDateOfBirth((Date)
	 * tuple[2]); if (tuple[3] != null)
	 * mstPensionerFamilyDtls.setDateOfDeath((Date) tuple[3]); if (tuple[4] !=
	 * null) mstPensionerFamilyDtls.setMajorFlag(tuple[4].toString()); if
	 * (tuple[5] != null) mstPensionerFamilyDtls.setSalary(new
	 * BigDecimal(tuple[5].toString())); if (tuple[6] != null)
	 * mstPensionerFamilyDtls.setHandicapeFlag(tuple[6].toString()); if
	 * (tuple[7] != null)
	 * mstPensionerFamilyDtls.setGuardianName(tuple[7].toString());
	 * arrlstPnsnrFDtls.add(mstPensionerFamilyDtls); } } } catch (Exception e) {
	 * gLogger.error("error" + e, e); throw e; } return arrlstPnsnrFDtls; }
	 */

	public List getFamilyDtlsPks(String pensionerCode, long langId) throws Exception {

		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		List resultList = null;
		try {
			strQuery.append(" SELECT familyMemberId FROM MstPensionerFamilyDtls WHERE pensionerCode=:pensoinerCode ");
			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensoinerCode", pensionerCode);
			resultList = hqlQuery.list();
		} catch (Exception e) {
			gLogger.error("error" + e, e);
		}
		return resultList;

	}

	public void deleteFamilyPensionersByPnsnrCode(String pensionerCode) throws Exception {

		StringBuffer lBdQuery = new StringBuffer();
		try {
			Session hiSession = getSession();
			lBdQuery.append(" DELETE FROM MstPensionerFamilyDtls WHERE pensionerCode=:pensoinerCode ");
			Query hbQuery = hiSession.createQuery(lBdQuery.toString());
			hbQuery.setString("pensoinerCode", pensionerCode);
			hbQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
	}

	public String getFamilyPnsnrname(String lStrPPONo, String lStrLocCode) throws Exception {

		String lStrPName = null;
		List resultList;
		try {
			Session ghibSession = getSession();
			StringBuffer strQuery = new StringBuffer();

			strQuery.append(" SELECT f.name ");
			strQuery.append(" FROM MstPensionerFamilyDtls f,TrnPensionRqstHdr r ");
			strQuery.append(" WHERE r.ppoNo = :PPONo AND r.locationCode = :locationCode AND r.pensionerCode = f.pensionerCode AND f.dateOfDeath IS NULL ");
			strQuery.append(" AND f.percentage = (SELECT MAX(fd.percentage) FROM MstPensionerFamilyDtls fd,TrnPensionRqstHdr rq WHERE rq.ppoNo =:PPONo"
					+ " AND fd.pensionerCode = rq.pensionerCode AND fd.dateOfDeath IS NULL) ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("PPONo", lStrPPONo);
			hqlQuery.setString("locationCode", lStrLocCode);

			resultList = hqlQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				lStrPName = resultList.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrPName;
	}

	public List<MstPensionerFamilyDtls> getFamilyDtlsVoListByPnsnrCode(String pensionerCode) throws Exception {

		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {
			strQuery.append(" FROM MstPensionerFamilyDtls WHERE pensionerCode=:pensoinerCode ");

			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensoinerCode", pensionerCode);
			resultList = hqlQuery.list();
			/*
			 * if(! resultList.isEmpty()) { lObjReturnFamilyDtls =
			 * (MstPensionerFamilyDtls)resultList.get(0); }
			 */
		} catch (Exception e) {
			gLogger.error("error" + e, e);
			throw e;
		}
		return resultList;
	}

	public List<MstPensionerFamilyDtls> getAliveFamilyDtlsVoListByPnsnrCode(String pensionerCode) throws Exception {

		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {
			strQuery.append(" FROM MstPensionerFamilyDtls WHERE pensionerCode=:pensoinerCode AND dateOfDeath IS NULL AND percentage = 100 ");

			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensoinerCode", pensionerCode);
			resultList = hqlQuery.list();
			/*
			 * if(! resultList.isEmpty()) { lObjReturnFamilyDtls =
			 * (MstPensionerFamilyDtls)resultList.get(0); }
			 */
		} catch (Exception e) {
			gLogger.error("error" + e, e);
			throw e;
		}
		return resultList;
	}

	public List getRemovedFamilyDtlsVoListByPnsnrCodeAndName(String pensionerCode, List argsNameList) throws Exception {

		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {
			strQuery.append("Select name FROM MstPensionerFamilyDtls WHERE pensionerCode=:pensoinerCode and name not in (:Name) ");

			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensoinerCode", pensionerCode);
			hqlQuery.setParameterList("Name", argsNameList);
			resultList = hqlQuery.list();
		} catch (Exception e) {
			gLogger.error("error" + e, e);
			throw e;
		}
		return resultList;
	}

	public MstPensionerFamilyDtls getFamilyDtlsVoByPnsnrCodeAndName(String pensionerCode, String lStrName) throws Exception {

		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		MstPensionerFamilyDtls lObjReturnFamilyDtls = null;
		try {
			strQuery.append(" FROM MstPensionerFamilyDtls WHERE pensionerCode=:pensoinerCode and name=:name ");

			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensoinerCode", pensionerCode);
			hqlQuery.setString("name", lStrName);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				lObjReturnFamilyDtls = (MstPensionerFamilyDtls) resultList.get(0);
			}
		} catch (Exception e) {
			gLogger.error("error" + e, e);
			throw e;
		}
		return lObjReturnFamilyDtls;
	}
}
