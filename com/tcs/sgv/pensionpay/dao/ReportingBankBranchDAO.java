package com.tcs.sgv.pensionpay.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.core.dao.GenericDao;


public interface ReportingBankBranchDAO extends GenericDao {

	List<RltBankBranch> getRltBankBranchDtls(String lStrBankCode, String lStrLocCode, Map displayTag);

	Integer getRltBankBranchCount(String lStrBankCode, String lStrLocCode);

	void updateAllReportingBankBranch(String lStrBankCode, String lStrLocCode, String lStrReportingBankCode, String lStrReportingBranchCode);

	void updateIndividualReportingBankBranch(Long lLngBranchCode, String lStrLocCode, String lStrReportingBankCode, String lStrReportingBranchCode, String lStrEcsBranch, String lStrMicrCode,
			String lStrIfscCode, Long lLngUpdtUserId, Long lLngUptdPostId, Date lUpdtDate);

	List getReportingBranchName();
}
