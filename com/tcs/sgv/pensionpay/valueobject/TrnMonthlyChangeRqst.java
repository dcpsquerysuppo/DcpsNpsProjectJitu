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
public class TrnMonthlyChangeRqst implements java.io.Serializable {

	private Long changeRqstId;
	private String billType;
	private Long billNo;
	private Date changeRqstDate;
	private Integer forMonth;
	private Date upToDate;
	private String bankCode;
	private String branchCode;
	private String locationCode;
	private BigDecimal grossAmount = BigDecimal.ZERO;
	private BigDecimal recoveryAmount = BigDecimal.ZERO;
	private BigDecimal netAmount = BigDecimal.ZERO;
	private BigDecimal deductionA = BigDecimal.ZERO;
	private BigDecimal deductionB = BigDecimal.ZERO;
	private String status;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Integer dbId;

	public TrnMonthlyChangeRqst() {

	}

	/**
	 * @param changeRqstId
	 * @param billType
	 * @param billNo
	 * @param changeRqstDate
	 * @param forMonth
	 * @param upToDate
	 * @param bankCode
	 * @param branchCode
	 * @param locationCode
	 * @param grossAmount
	 * @param recovery
	 * @param netAmount
	 * @param deductionA
	 * @param deductionB
	 * @param status
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param dbId
	 */
	public TrnMonthlyChangeRqst(Long changeRqstId, String billType, Long billNo, Date changeRqstDate, Integer forMonth, Date upToDate, String bankCode, String branchCode, String locationCode,
			BigDecimal grossAmount, BigDecimal recoveryAmount, BigDecimal netAmount, BigDecimal deductionA, BigDecimal deductionB, String status, Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId, Date updatedDate, Integer dbId) {

		super();
		this.changeRqstId = changeRqstId;
		this.billType = billType;
		this.billNo = billNo;
		this.changeRqstDate = changeRqstDate;
		this.forMonth = forMonth;
		this.upToDate = upToDate;
		this.bankCode = bankCode;
		this.branchCode = branchCode;
		this.locationCode = locationCode;
		this.grossAmount = grossAmount;
		this.recoveryAmount = recoveryAmount;
		this.netAmount = netAmount;
		this.deductionA = deductionA;
		this.deductionB = deductionB;
		this.status = status;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
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

	public Date getChangeRqstDate() {

		return changeRqstDate;
	}

	public void setChangeRqstDate(Date changeRqstDate) {

		this.changeRqstDate = changeRqstDate;
	}

	public Integer getForMonth() {

		return forMonth;
	}

	public void setForMonth(Integer forMonth) {

		this.forMonth = forMonth;
	}

	public Date getUpToDate() {

		return upToDate;
	}

	public void setUpToDate(Date upToDate) {

		this.upToDate = upToDate;
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

	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
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
}
