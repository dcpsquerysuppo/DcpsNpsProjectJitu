package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;


public interface TrnPensionArrearDtlsDAO extends GenericDao<TrnPensionArrearDtls, Long> {

	List<TrnPensionArrearDtls> getTrnPensionArrearDtlsVo(String lStrPnsnCode) throws Exception;

	Double getArrearForMonth(Integer lForMonth, String lStrPnsnCode) throws Exception;

	Double getTIArrearForMonth(Integer lForMonth, String lStrPnsnCode) throws Exception;

	ArrayList getArrearDtls(String lStrPensionerCode, Integer lStrForMonth) throws Exception;

	void deleteArrearsDtlsBypensionRequestIdAndIsManual(Long pensionRequestId, String isManual) throws Exception;

	void deleteArrearsDtlsBypensionRequestIdAndIsManualAndRemarks(Long pensionRequestId, String isManual, String lStrRemarks) throws Exception;

	BigDecimal getSumofArrearsAmountByPnsnrCode(String lStrPnsnrCode, List lLstFieldType) throws Exception;

	void updateArrearsByPnsnrCodeAndFieldType(String lStrPnsnrCode, String lStrFieldType, BigDecimal lBDNewBasic, BigDecimal lBDOldBasic, BigDecimal lBDTotalArrears, BigDecimal lBDDiffAmnt,
			Long gLngPostId, Long gLngUserId, Date gDate, String lStrNewFieldType) throws Exception;

	void deleteTiArrForProvPpo(String lStrPenCode) throws Exception;

	void insertArrearDtls(List<TrnPensionArrearDtls> lLstTrnPensionArrearDtls) throws Exception;
}
