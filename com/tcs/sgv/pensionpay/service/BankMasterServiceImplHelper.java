package com.tcs.sgv.pensionpay.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.common.valueobject.MstBankPay;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.eis.dao.BankMasterDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.tcs.sgv.eis.valueobject.HrEisBankMst;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

public class BankMasterServiceImplHelper 
{
	Log logger = LogFactory.getLog(getClass());
	ServiceLocator serv;
	
	public BankMasterServiceImplHelper(ServiceLocator serv) 
	{
		super();
		this.serv = serv;
	}
	
	//Added by Abhilash
	/**
	 * @Author	: Abhilash
	 * @Date	: 17/02/2011 
	 * Function	: This method will return bankId from HrEisBankMst  object.
	 * @param    HrEisBankMst bankMst,ServiceLocator serv
	 * @return   long
	 */
	public long getBankId(MstBank bankMst,ServiceLocator serv)
	{
		
		long bankid = bankMst.getBankId();
    	return bankid;
	}
	
	/**
	 * @Author	: Abhilash
	 * @Date	: 17/02/2011 
	 * Function	: This method will insert data in hr_eis_bank_msttable.
	 * @param    HrEisBankMst bankMst,long langId,long bankId,long dbId,long postId,long userId,long locId
	 * @return   void
	 */
	public void inserBankMasteDtls(com.tcs.sgv.common.valueobject.MstBank bankMst,long bankId,long langId,long dbId,long postId,long userId,String locId)  throws Exception
	{
		Date sysdate = new Date();
		/*CmnLanguageMstDaoImpl cmnLanguageMstDaoImpl=new CmnLanguageMstDaoImpl(CmnLanguageMst.class,serv.getSessionFactory());
		CmnLanguageMst cmnLanguageMst=cmnLanguageMstDaoImpl.read(langId);
		
		CmnDatabaseMstDaoImpl cmnDatabaseMstDaoImpl=new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,serv.getSessionFactory());
		CmnDatabaseMst cmnDatabaseMst = cmnDatabaseMstDaoImpl.read(dbId);
		OrgPostMstDaoImpl orgPostMstDaoImpl=new OrgPostMstDaoImpl(OrgPostMst.class,serv.getSessionFactory());
		OrgPostMst orgPostMst = orgPostMstDaoImpl.read(postId);
		OrgUserMstDaoImpl orgUserMstDaoImpl=new OrgUserMstDaoImpl(OrgUserMst.class,serv.getSessionFactory());
		OrgUserMst orgUserMst = orgUserMstDaoImpl.read(userId);
		CmnLocationMstDaoImpl cmnLocationMstDaoImpl=new CmnLocationMstDaoImpl(CmnLocationMst.class,serv.getSessionFactory());
		//CmnLocationMst cmnLocationMst=cmnLocationMstDaoImpl.read(locId);*/

		com.tcs.sgv.pensionpay.dao.BankMasterDAO bankMasterDAO = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());
		
		
		long UserId =1;
		long PostId=1;
		
		bankMst.setBankId(bankId);
		bankMst.setBankCode(Long.toString(bankId));
		bankMst.setStartDate(sysdate);
		bankMst.setCreatedUserId(UserId);
		bankMst.setCreatedPostId(PostId);
		bankMst.setCreatedDate(sysdate);
		bankMst.setDbId(dbId);
		bankMst.setLangId(langId);
		bankMst.setLocationCode(locId);
		bankMst.setCreatedDate(sysdate);		

		bankMasterDAO.create(bankMst);
	}
	/**
	 * @Author	: 
	 * @Date	: 17/02/2011 
	 * Function	: This method will return data from hr_eis_bank_msttable.
	 * @param    HrEisBankMst bankMst,ServiceLocator serv
	 * @return   void
	 */
	public void updateBankMasterDtls(MstBank bankMst,long postId,long userId) throws Exception
	{
		
		Date sysdate = new Date();
		OrgPostMstDaoImpl orgPostMstDaoImpl=new OrgPostMstDaoImpl(OrgPostMst.class,serv.getSessionFactory());
		OrgPostMst orgPostMst = orgPostMstDaoImpl.read(postId);
		OrgUserMstDaoImpl orgUserMstDaoImpl=new OrgUserMstDaoImpl(OrgUserMst.class,serv.getSessionFactory());
		OrgUserMst orgUserMst = orgUserMstDaoImpl.read(userId);
		
		com.tcs.sgv.pensionpay.dao.BankMasterDAO bankMasterDAO = new com.tcs.sgv.pensionpay.dao.BankMasterDAOImpl(MstBank.class,serv.getSessionFactory());		
		long bankid = this.getBankId(bankMst, serv);
		MstBank bankMstVO = (MstBank) bankMasterDAO.read(bankid);
		logger.info("INSIDE UPDATE BankMasterServiceImplHelper insertBankMasterDtls");	
		bankMstVO.setBankName(bankMst.getBankName());
		bankMstVO.setBankCode(bankMst.getBankCode());
		bankMstVO.setBankAddress(bankMst.getBankAddress());
		//bankMstVO.setMicrCode(bankMst.getMicrCode());
		bankMstVO.setUpdatedPostId(postId);
		bankMstVO.setUpdatedUserId(userId);
		bankMstVO.setUpdatedDate(sysdate);				 
		bankMasterDAO.update(bankMstVO);
	}
	//Ended by Abhilash
	
	
}
