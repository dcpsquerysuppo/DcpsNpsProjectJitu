/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 31, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 May 31, 2011
 */
public class TrnPensionChangeHdr implements java.io.Serializable {

	private Long trnPensionChangeHdrId;

	private Long changeRqstId;

	private String billType;

	private Long billNo;

	private Date billDate;

	private Integer headCode;

	private Integer forMonth;

	private String bankCode;

	private String branchCode;

	private String scheme;

	private Integer trnCounter;

	private String locationCode;

	private BigDecimal grossAmount = BigDecimal.ZERO;

	private BigDecimal recoveryAmount = BigDecimal.ZERO;

	private BigDecimal netAmount = BigDecimal.ZERO;

	private BigDecimal deductionA = BigDecimal.ZERO;

	private BigDecimal deductionB = BigDecimal.ZERO;

	private Long createdUserId;

	private Long createdPostId;

	private Date createdDate;

	private Long updatedUserId;

	private Long updatedPostId;

	private Date updatedDate;

	private Integer dbId;

	private String schemeCode;

	private Long noOfPnsr = 0L;

	private String payMode;

	public TrnPensionChangeHdr() {

	}

	/**
	 * @param trnPensionChangeHdrId
	 * @param changeRqstId
	 * @param billType
	 * @param billNo
	 * @param billDate
	 * @param headCode
	 * @param forMonth
	 * @param bankCode
	 * @param branchCode
	 * @param scheme
	 * @param trnCounter
	 * @param locationCode
	 * @param grossAmount
	 * @param recoveryAmount
	 * @param netAmount
	 * @param deductionA
	 * @param deductionB
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param dbId
	 */
	public TrnPensionChangeHdr(Long trnPensionChangeHdrId, Long changeRqstId, String billType, Long billNo, Date billDate, Integer headCode, Integer forMonth, String bankCode, String branchCode,
			String scheme, Integer trnCounter, String locationCode, BigDecimal grossAmount, BigDecimal recoveryAmount, BigDecimal netAmount, BigDecimal deductionA, BigDecimal deductionB,
			Long createdUserId, Long createdPostId, Date createdDate, Long updatedUserId, Long updatedPostId, Date updatedDate, Integer dbId) {

		super();
		this.trnPensionChangeHdrId = trnPensionChangeHdrId;
		this.changeRqstId = changeRqstId;
		this.billType = billType;
		this.billNo = billNo;
		this.billDate = billDate;
		this.headCode = headCode;
		this.forMonth = forMonth;
		this.bankCode = bankCode;
		this.branchCode = branchCode;
		this.scheme = scheme;
		this.trnCounter = trnCounter;
		this.locationCode = locationCode;
		this.grossAmount = grossAmount;
		this.recoveryAmount = recoveryAmount;
		this.netAmount = netAmount;
		this.deductionA = deductionA;
		this.deductionB = deductionB;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
	}

	public Long getTrnPensionChangeHdrId() {

		return trnPensionChangeHdrId;
	}

	public void setTrnPensionChangeHdrId(Long trnPensionChangeHdrId) {

		this.trnPensionChangeHdrId = trnPensionChangeHdrId;
	}

	public Long getChangeRqstId() {

		return changeRqstId;
	}

	public void setChangeRqstId(Long changeRqstId) {

		this.changeRqstId = changeRqstId;
	}

	public String getBillType() {

		return billType;
	}

	public void setBillType(String billType) {

		this.billType = billType;
	}

	public Long getBillNo() {

		return billNo;
	}

	public void setBillNo(Long billNo) {

		this.billNo = billNo;
	}

	public Date getBillDate() {

		return billDate;
	}

	public void setBillDate(Date billDate) {

		this.billDate = billDate;
	}

	public Integer getHeadCode() {

		return headCode;
	}

	public void setHeadCode(Integer headCode) {

		this.headCode = headCode;
	}

	public Integer getForMonth() {

		return forMonth;
	}

	public void setForMonth(Integer forMonth) {

		this.forMonth = forMonth;
	}

	public String getBankCode() {

		return bankCode;
	}

	public void setBankCode(String bankCode) {

		this.bankCode = bankCode;
	}

	public String getBranchCode() {

		return branchCode;
	}

	public void setBranchCode(String branchCode) {

		this.branchCode = branchCode;
	}

	public String getScheme() {

		return scheme;
	}

	public void setScheme(String scheme) {

		this.scheme = scheme;
	}

	public Integer getTrnCounter() {

		return trnCounter;
	}

	public void setTrnCounter(Integer trnCounter) {

		this.trnCounter = trnCounter;
	}

	public String getLocationCode() {

		return locationCode;
	}

	public void setLocationCode(String locationCode) {

		this.locationCode = locationCode;
	}

	public BigDecimal getGrossAmount() {

		return grossAmount;
	}

	public void setGrossAmount(BigDecimal grossAmount) {

		this.grossAmount = grossAmount;
	}

	public BigDecimal getRecoveryAmount() {

		return recoveryAmount;
	}

	public void setRecoveryAmount(BigDecimal recoveryAmount) {

		this.recoveryAmount = recoveryAmount;
	}

	public BigDecimal getNetAmount() {

		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {

		this.netAmount = netAmount;
	}

	public BigDecimal getDeductionA() {

		return deductionA;
	}

	public void setDeductionA(BigDecimal deductionA) {

		this.deductionA = deductionA;
	}

	public BigDecimal getDeductionB() {

		return deductionB;
	}

	public void setDeductionB(BigDecimal deductionB) {

		this.deductionB = deductionB;
	}

	public Long getCreatedUserId() {

		return createdUserId;
	}

	public void setCreatedUserId(Long createdUserId) {

		this.createdUserId = createdUserId;
	}

	public Long getCreatedPostId() {

		return createdPostId;
	}

	public void setCreatedPostId(Long createdPostId) {

		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {

		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
	}

	public Long getUpdatedUserId() {

		return updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {

		this.updatedUserId = updatedUserId;
	}

	public Long getUpdatedPostId() {

		return updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {

		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {

		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {

		this.updatedDate = updatedDate;
	}

	public Integer getDbId() {

		return dbId;
	}

	public void setDbId(Integer dbId) {

		this.dbId = dbId;
	}

	public String getSchemeCode() {

		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {

		this.schemeCode = schemeCode;
	}

	public Long getNoOfPnsr() {

		return noOfPnsr;
	}

	public void setNoOfPnsr(Long noOfPnsr) {

		this.noOfPnsr = noOfPnsr;
	}

	public String getPayMode() {

		return payMode;
	}

	public void setPayMode(String payMode) {

		this.payMode = payMode;
	}
}
