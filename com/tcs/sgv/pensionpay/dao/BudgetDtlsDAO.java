package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.tcs.sgv.common.valueobject.BillEdpVO;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;


public interface BudgetDtlsDAO {

	List getTrnPensionRqstHdrDtls(String lStrPPONO, String lStrStatus,String lStrLocCode) throws Exception;

	RltPensionHeadcodeChargable getMstPensionHeadcodeDtls(String lStrHeadCode, String lStrBillType) throws Exception;

	List getExpEdpDtl(String lStrEdpCode,Long lLangId, Integer lFinYr) throws Exception;

	List getRcptEdpDtlByBillType(String lStrPnsnerCode, String lStrBillType) throws Exception;

	List getRcptEdpDtlForMnthly(String lForMonth, String lStrTypeFlag, List lPnsnrCodeLst) throws Exception;

	List getRcptEdpDtlForConsolidatedMnthly(String lForMonth, String lStrTypeFlag, List lPnsnrCodeLst,String lStrPPOList, String lLocCode) throws Exception;

	Map getEdpDetails(BigDecimal headCode) throws Exception;

	BillEdpVO getITHeadStructureDtl(long lLngId, Integer lFinYr) throws Exception;
}
