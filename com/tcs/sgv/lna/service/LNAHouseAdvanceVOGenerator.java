package com.tcs.sgv.lna.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaHouseAdvance;

public class LNAHouseAdvanceVOGenerator extends ServiceImpl implements VOGeneratorService {
	Log glogger = LogFactory.getLog(getClass());
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstLnaHouseAdvance lObjHouseAdvance = null;
		try {
			lObjHouseAdvance = generateHouseAdvance(inputMap);
			inputMap.put("HouseAdvance", lObjHouseAdvance);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			glogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	private MstLnaHouseAdvance generateHouseAdvance(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaHouseAdvance lObjHouseAdvance = null;

		LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlag = 0;

		String lStrHouseAdvanceId = StringUtility.getParameter("hidHouseAdvanceId", request);
		String lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
		String lStrRequestType = StringUtility.getParameter("hidRequestType", request);
		String lStrHouseSubType = StringUtility.getParameter("cmbHBAType", request);
		String lStrPayCommissionGR = StringUtility.getParameter("cmbPayCommissionHBA", request);
		String lStrPayScale = StringUtility.getParameter("cmbPayScaleHBA", request);
		String lStrBasicPay = StringUtility.getParameter("txtBasicPay", request);
		String lStrAppDate = StringUtility.getParameter("txtAppDateHBA", request);
		String lStrRequestedAmount = StringUtility.getParameter("txtReqAmountHBA", request);
		String lStrLoanRepaymentCapacity = StringUtility.getParameter("txtLoanRepaymentCapacity", request);
		String lStrRequestedDate = StringUtility.getParameter("txtReqDateHBA", request);
		String lStrInterestRate = StringUtility.getParameter("txtInterestRateHBA", request);
		String lStrUserRemarks = StringUtility.getParameter("txtUserRemarksHBA", request);
		String lStrEmpStatus = StringUtility.getParameter("rdoEmpStatus", request);
		String lStrFirstGuarantor = StringUtility.getParameter("txtGuarantor1", request);
		String lStrSecondGuarantor = StringUtility.getParameter("txtGuarantor2", request);

		Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrAppDate);

		if (!lStrHouseAdvanceId.equals("")) {
			lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrHouseAdvanceId));
			iSaveOrUpdateFlag = 2;

		} else {
			lObjHouseAdvance = new MstLnaHouseAdvance();
			iSaveOrUpdateFlag = 1;
		}

		inputMap.put("iSaveOrUpdateFlag", iSaveOrUpdateFlag);

		if (!"".equals(lStrSevaarthId.trim())) {
			lObjHouseAdvance.setSevaarthId(lStrSevaarthId);
		}
		if (!"".equals(lStrRequestType.trim())) {
			lObjHouseAdvance.setAdvanceType(Long.parseLong(lStrRequestType));
		}
		if (!"".equals(lStrHouseSubType.trim())) {
			lObjHouseAdvance.setAdvanceSubType(Long.parseLong(lStrHouseSubType));
		}
		if (!(lStrPayCommissionGR.equals("-1"))) {
			lObjHouseAdvance.setPayCommissionGR(Long.parseLong(lStrPayCommissionGR));
		}
		if (!"".equals(lStrPayScale.trim())) {
			lObjHouseAdvance.setPayScale(Long.parseLong(lStrPayScale));
		}
		if (!"".equals(lStrBasicPay.trim())) {
			lObjHouseAdvance.setBasicPay(Long.parseLong(lStrBasicPay));
		}
		if (!"".equals(lStrAppDate.trim())) {
			lObjHouseAdvance.setApplicationDate(lObjSimpleDateFormat.parse(lStrAppDate));
		}
		if (!"".equals(lStrRequestedAmount.trim())) {
			lObjHouseAdvance.setAmountRequested(Long.parseLong(lStrRequestedAmount));
		}
		if (!"".equals(lStrLoanRepaymentCapacity.trim())) {
			lObjHouseAdvance.setLoanRepayCapacity(Double.parseDouble(lStrLoanRepaymentCapacity));
		}
		if (!"".equals(lStrRequestedDate.trim())) {
			lObjHouseAdvance.setRequestedDate(lObjSimpleDateFormat.parse(lStrRequestedDate));
		}
		if (!"".equals(lStrInterestRate.trim())) {
			lObjHouseAdvance.setInterestRate(Float.parseFloat(lStrInterestRate));
		}
		if (!"".equals(lStrUserRemarks.trim())) {
			lObjHouseAdvance.setUserRemarks(lStrUserRemarks);
		}
		if (!"".equals(lStrEmpStatus.trim())) {
			lObjHouseAdvance.setEmpStatus(lStrEmpStatus);
			if (lStrEmpStatus.equals("S")) {
				if (!"".equals(lStrFirstGuarantor.trim())) {
					lObjHouseAdvance.setFirstGuarantor(lStrFirstGuarantor);
				}
				if (!"".equals(lStrSecondGuarantor.trim())) {
					lObjHouseAdvance.setSecondGuarantor(lStrSecondGuarantor);
				}
			} else {
				lObjHouseAdvance.setFirstGuarantor(null);
				lObjHouseAdvance.setSecondGuarantor(null);
			}
		}

		if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.CONSTRUCTIONOFFLAT"))) {

			String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA2", request);
			String lStrPrincipalAmount = StringUtility.getParameter("txtPrincipalAmountHBA", request);
			String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA2", request);

			String lStrDisbursement1 = StringUtility.getParameter("txtDisbursement1", request);
			String lStrReleaseDate1 = StringUtility.getParameter("txtReleaseDate1", request);
			String lStrDisbursement2 = StringUtility.getParameter("txtDisbursement2", request);
			String lStrReleaseDate2 = StringUtility.getParameter("txtReleaseDate2", request);
			String lStrDisbursement3 = StringUtility.getParameter("txtDisbursement3", request);
			String lStrReleaseDate3 = StringUtility.getParameter("txtReleaseDate3", request);

			String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA2", request);
			String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA2", request);
			String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrinInstallmentAmountHBA", request);
			String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA2", request);
			String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA2", request);
			String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA2", request);
			String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA2", request);
			String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA2", request);

			if (!"".equals(lStrSancAmount.trim())) {
				lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrInterestAmount.trim())) {
				lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
			}
			if (!"".equals(lStrPrincipalAmount.trim())) {
				lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrPrincipalAmount));
			}

			if (!"".equals(lStrSancInterInstall.trim())) {
				lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
			}
			if (!"".equals(lStrSancPrinInstall.trim())) {
				lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
			}
			if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
				lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
			}
			if (!"".equals(lStrInterInstallmentAmount.trim())) {
				lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
			}
			if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
				lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
			}
			if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
				lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
			}
			if (!(lStrOddInterestInstallNo.equals("-1"))) {
				lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
			}
			if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
				lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
			}
			if (!"".equals(lStrDisbursement1.trim())) {
				lObjHouseAdvance.setDisbursementOne(Float.parseFloat(lStrDisbursement1));
			}
			if (!"".equals(lStrReleaseDate1.trim())) {
				lObjHouseAdvance.setReleaseDateOne(lObjSimpleDateFormat.parse(lStrReleaseDate1));
			}
			if (!"".equals(lStrDisbursement2.trim())) {
				lObjHouseAdvance.setDisbursementTwo(Float.parseFloat(lStrDisbursement2));
			}
			if (!"".equals(lStrReleaseDate2.trim())) {
				lObjHouseAdvance.setReleaseDateTwo(lObjSimpleDateFormat.parse(lStrReleaseDate2));
			}
			if (!"".equals(lStrDisbursement3.trim())) {
				lObjHouseAdvance.setDisbursementThree(Float.parseFloat(lStrDisbursement3));
			}
			if (!"".equals(lStrReleaseDate3.trim())) {
				lObjHouseAdvance.setReleaseDateThree(lObjSimpleDateFormat.parse(lStrReleaseDate3));
			}

		} else if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.PLOATPURCHASE"))) {

			String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA2", request);
			String lStrPrincipalAmount = StringUtility.getParameter("txtPrincipalAmountHBA", request);
			String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA2", request);
			String lStrDisbursement1 = StringUtility.getParameter("txtDisbursement1", request);
			String lStrReleaseDate1 = StringUtility.getParameter("txtReleaseDate1", request);
			String lStrDisbursement2 = StringUtility.getParameter("txtDisbursement2", request);
			String lStrReleaseDate2 = StringUtility.getParameter("txtReleaseDate2", request);
			String lStrDisbursement3 = StringUtility.getParameter("txtDisbursement3", request);
			String lStrReleaseDate3 = StringUtility.getParameter("txtReleaseDate3", request);
			String lStrDisbursement4 = StringUtility.getParameter("txtDisbursement4", request);
			String lStrReleaseDate4 = StringUtility.getParameter("txtReleaseDate4", request);
			String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA2", request);
			String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA2", request);
			String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrinInstallmentAmountHBA", request);
			String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA2", request);
			String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA2", request);
			String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA2", request);
			String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA2", request);
			String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA2", request);

			if (!"".equals(lStrSancAmount.trim())) {
				lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrInterestAmount.trim())) {
				lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
			}
			if (!"".equals(lStrPrincipalAmount.trim())) {
				lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrPrincipalAmount));
			}

			if (!"".equals(lStrSancInterInstall.trim())) {
				lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
			}
			if (!"".equals(lStrSancPrinInstall.trim())) {
				lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
			}
			if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
				lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
			}
			if (!"".equals(lStrInterInstallmentAmount.trim())) {
				lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
			}
			if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
				lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
			}
			if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
				lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
			}
			if (!(lStrOddInterestInstallNo.equals("-1"))) {
				lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
			}
			if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
				lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
			}
			if (!"".equals(lStrDisbursement1.trim())) {
				lObjHouseAdvance.setDisbursementOne(Float.parseFloat(lStrDisbursement1));
			}
			if (!"".equals(lStrReleaseDate1.trim())) {
				lObjHouseAdvance.setReleaseDateOne(lObjSimpleDateFormat.parse(lStrReleaseDate1));
			}
			if (!"".equals(lStrDisbursement2.trim())) {
				lObjHouseAdvance.setDisbursementTwo(Float.parseFloat(lStrDisbursement2));
			}
			if (!"".equals(lStrReleaseDate2.trim())) {
				lObjHouseAdvance.setReleaseDateTwo(lObjSimpleDateFormat.parse(lStrReleaseDate2));
			}
			if (!"".equals(lStrDisbursement3.trim())) {
				lObjHouseAdvance.setDisbursementThree(Float.parseFloat(lStrDisbursement3));
			}
			if (!"".equals(lStrReleaseDate3.trim())) {
				lObjHouseAdvance.setReleaseDateThree(lObjSimpleDateFormat.parse(lStrReleaseDate3));
			}
			if (!"".equals(lStrDisbursement4.trim())) {
				lObjHouseAdvance.setDisbursementFour(Float.parseFloat(lStrDisbursement4));
			}
			if (!"".equals(lStrReleaseDate4.trim())) {
				lObjHouseAdvance.setReleaseDateFour(lObjSimpleDateFormat.parse(lStrReleaseDate4));
			}

		} else {
			String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateHBA", request);
			String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA", request);
			String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA", request);
			String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA", request);
			String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA", request);
			String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrincipalInstallmentAmtHBA", request);
			String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA", request);
			String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA", request);
			String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA", request);
			String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA", request);
			String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA", request);

			if (!"".equals(lStrSancAmount.trim())) {
				lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjHouseAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrInterestAmount.trim())) {
				lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
			}
			if (!"".equals(lStrSancPrinInstall.trim())) {
				lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
			}
			if (!"".equals(lStrSancInterInstall.trim())) {
				lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
			}
			if (!"".equals(lStrSancInterInstall.trim())) {
				lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
			}
			if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
				lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
			}
			if (!"".equals(lStrInterInstallmentAmount.trim())) {
				lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
			}
			if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
				lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
			}
			if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
				lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
			} else {
				lObjHouseAdvance.setOddInstallment(null);
			}
			if (!(lStrOddInterestInstallNo.equals("-1"))) {
				lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
			}
			if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
				lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
			} else {
				lObjHouseAdvance.setOddInterestInstallment(null);
			}

		}

		lObjHouseAdvance.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
		if (iSaveOrUpdateFlag == 1) {
			lObjHouseAdvance.setCreatedPostId(gLngPostId);
			lObjHouseAdvance.setCreatedUserId(gLngUserId);
			lObjHouseAdvance.setCreatedDate(gDtCurrDt);
		}
		if (iSaveOrUpdateFlag == 2) {
			lObjHouseAdvance.setUpdatedUserId(gLngUserId);
			lObjHouseAdvance.setUpdatedPostId(gLngPostId);
			lObjHouseAdvance.setUpdatedDate(gDtCurrDt);
		}
		return lObjHouseAdvance;
	}
}
