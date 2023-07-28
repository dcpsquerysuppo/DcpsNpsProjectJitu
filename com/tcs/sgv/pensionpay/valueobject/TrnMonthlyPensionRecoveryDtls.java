/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 20, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Sep 20, 2011
 */
public class TrnMonthlyPensionRecoveryDtls implements Serializable {

	private Long trnMonthlyPensionRecoveryDtls;
	private String pensionerCode;
	private String recoveryFromFlag;
	private String recoveryType;
	private String accountNumber;
	private BigDecimal amount = BigDecimal.ZERO;
	private Integer forMonth;
	private String bankCode;
	private String branchCode;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Long billNo;
	private Integer trnCounter;
	private Integer noOfInstallments;
	private String nature;
	private String schemeCode;
	private String ppoNo;
	private Integer locationCode;
	private Integer dbId;
	private Long changeRqstId;
	private String changeStmntStatus;

	public Long getTrnMonthlyPensionRecoveryDtls() {

		return trnMonthlyPensionRecoveryDtls;
	}

	public void setTrnMonthlyPensionRecoveryDtls(Long trnMonthlyPensionRecoveryDtls) {

		this.trnMonthlyPensionRecoveryDtls = trnMonthlyPensionRecoveryDtls;
	}

	public String getPensionerCode() {

		return pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {

		this.pensionerCode = pensionerCode;
	}

	public String getRecoveryFromFlag() {

		return recoveryFromFlag;
	}

	public void setRecoveryFromFlag(String recoveryFromFlag) {

		this.recoveryFromFlag = recoveryFromFlag;
	}

	public String getRecoveryType() {

		return recoveryType;
	}

	public void setRecoveryType(String recoveryType) {

		this.recoveryType = recoveryType;
	}

	public String getAccountNumber() {

		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {

		this.accountNumber = accountNumber;
	}

	public BigDecimal getAmount() {

		return amount;
	}

	public void setAmount(BigDecimal amount) {

		this.amount = amount;
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

	public Long getBillNo() {

		return billNo;
	}

	public void setBillNo(Long billNo) {

		this.billNo = billNo;
	}

	public Integer getTrnCounter() {

		return trnCounter;
	}

	public void setTrnCounter(Integer trnCounter) {

		this.trnCounter = trnCounter;
	}

	public Integer getNoOfInstallments() {

		return noOfInstallments;
	}

	public void setNoOfInstallments(Integer noOfInstallments) {

		this.noOfInstallments = noOfInstallments;
	}

	public String getNature() {

		return nature;
	}

	public void setNature(String nature) {

		this.nature = nature;
	}

	public String getSchemeCode() {

		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {

		this.schemeCode = schemeCode;
	}

	public String getPpoNo() {

		return ppoNo;
	}

	public void setPpoNo(String ppoNo) {

		this.ppoNo = ppoNo;
	}

	public Integer getLocationCode() {

		return locationCode;
	}

	public void setLocationCode(Integer locationCode) {

		this.locationCode = locationCode;
	}

	public Integer getDbId() {

		return dbId;
	}

	public void setDbId(Integer dbId) {

		this.dbId = dbId;
	}

	public Long getChangeRqstId() {

		return changeRqstId;
	}

	public void setChangeRqstId(Long changeRqstId) {

		this.changeRqstId = changeRqstId;
	}

	public String getChangeStmntStatus() {

		return changeStmntStatus;
	}

	public void setChangeStmntStatus(String changeStmntStatus) {

		this.changeStmntStatus = changeStmntStatus;
	}

}
