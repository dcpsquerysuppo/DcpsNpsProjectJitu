package com.tcs.sgv.pensionpay.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;


public interface MstPensionerDtlsDAO extends GenericDao {

	// ArrayList<LifeCertificateVO> getLifeCertificateVOList(String
	// lStrBankCode,String lStrBranchCode,BigDecimal lBDHeadCode,String
	// lStrLocCode,String lStrPostId, String lforMonth, SessionFactory
	// sFactory,Integer pageNumber) throws Exception;

	// int getLifeCertificateVOListCount(String lStrBankCode,String
	// lStrBranchCode,BigDecimal lBDHeadCode,String lStrLocCode,String
	// lStrPostId, String lforMonth,SessionFactory sFactory,Integer pageNumber)
	// throws Exception;

	Long getPnsionerDtlsIdFromPensionerCode(String pensionerCode);

	Long getPnsionerDtlsIdFromPensionerCode(String pnsnCode, String lStrStatus);

	List<MstPensionerDtls> getMstPensionerDtlsDiff(String lStrPnsnrCode) throws Exception;

	// String getACCNo(String lStrBankCode,String lStrBranchCode,String
	// lStrPensionerCode) throws Exception;
	String getACCNo(String lStrPensionerCode) throws Exception;

	List getAuditorAddress(String gStrLocCode, Long gLngLangId) throws Exception;

	// String getBranchName(String lStrBranchCode,String lStrAuditorBankCode)
	// throws Exception;
	MstPensionerDtls getMstPnsnrDtlsWithStatus(String lStrPensionerCode, String lStrCaseStatus) throws Exception;

	MstPensionerDtls getMstPensionerDtls(String lStrPensionerCode) throws Exception;

}
