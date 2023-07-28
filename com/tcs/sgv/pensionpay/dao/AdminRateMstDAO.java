package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;


public interface AdminRateMstDAO {

	List getDARateDetails(String lStrDARateType, String lStrHeadCodeType, String lStrForPension);

	String chkDateIsOverLapOrNot(String lStrDARateType, String lStrForPension, String lStrHeadCodeType, Date lDtFromDate, Date lDtToDate, Date lDtEffctvFromDate, Date lDtEffctvToDate);

	Integer getDARateConfigForStateCount(Long gLngLangId, String gStrLocCode, Map displayTag) throws Exception;

	List getDARateConfigForStateDtls(Long gLngLangId, String gStrLocCode, Map displayTag) throws Exception;

	Long getMaxStateCode(Long lLngLangId, Long lLngLocationCode);

	List getAllStateDept(Long gLngLangId) throws Exception;

	BigDecimal getMaxRevision(Long lLngHeadCode, String lStrFieldType) throws Exception;

	List<Object[]> getPensionerDtlsForDAArrearCalc(Map<String, Object> inputMap) throws Exception;

	Map<String, MstPensionerFamilyDtls> getMstPensionerFamilyDtlsMap(String lStrROPType, String lStrLocCode, String lStrDAForState) throws Exception;

	int saveDAArrearDtls(Map<String, Object> inputMap) throws Exception;

	int saveDAArrearDtlsByProc(Map<String, Object> inputMap) throws Exception;
}
