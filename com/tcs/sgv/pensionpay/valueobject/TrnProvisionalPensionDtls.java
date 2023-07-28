// default package
// Generated Feb 6, 2008 7:51:25 PM by Hibernate Tools 3.2.0.beta8
package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TrnPrvosionalPensionDtls generated by hbm2java
 */
public class TrnProvisionalPensionDtls implements java.io.Serializable,Cloneable {

	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -3591137077533841391L;

	private Long provisionalPensionDtlsId;

	private String pensionerCode;

	private String ppoNo;

	private Date commensionDate;

	private BigDecimal dcrgAmount;

	private Date dcrgDate;

	private BigDecimal basicPensionAmount;

	private Date paidDate;

	private Date fp1Date;

	private BigDecimal fp1Amount;

	private Date fp2Date;

	private BigDecimal fp2Amount;

	private BigDecimal trnCounter;

	private BigDecimal dbId;

	private String locationCode;

	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;

	private BigDecimal pensionRequestId;

	private BigDecimal cvpAmount;

	private Date cvpDate;

	private Date cvpRestorationDate;
	

	//------New fields added 
	
	private String provisionalPensionFlag;
	
	private Date provPensionToDate;
	
	private BigDecimal provPensionTotalAmountPaid;
	
	private String provPensionSanctionAuthority;
	
	private String provPensionAuthorityNo;
	
	private Date provPensionAuthorityDate;
	
	private String provGratuityFlag;
	
	private BigDecimal gratuityAmount;
	
	private BigDecimal gratuityActualAmountPaid;
	
	private Date gratuityPaymentDate;
	
	private String gratuitySanctionAuthority;
	
	private String gratuityAuthorityNo;
	
	private Date gratuityAuthorityDate;
	
	private String gratuityVoucherNo;
	
	private Date gratuityVoucherDate;
	
	private String voucherChargedVotedFlag;
	
	private String billType;
	
	// Constructors

	/** default constructor */
	public TrnProvisionalPensionDtls() {
	}

	/** minimal constructor */
	public TrnProvisionalPensionDtls(Long provisionalPensionDtlsId,
			BigDecimal pensionRequestId) {
		this.provisionalPensionDtlsId = provisionalPensionDtlsId;
		this.pensionRequestId = pensionRequestId;
	}

	/**
	 * 
	 * @param prvosionalPensionDtlsId
	 * @param pensionerCode
	 * @param ppoNo
	 * @param commensionDate
	 * @param dcrgAmount
	 * @param dcrgDate
	 * @param basicPensionAmount
	 * @param paidDate
	 * @param fp1Date
	 * @param fp1Amount
	 * @param fp2Date
	 * @param fp2Amount
	 * @param trnCounter
	 * @param dbId
	 * @param locationCode
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param pensionRequestId
	 * @param cvpAmount
	 * @param cvpDate
	 * @param cvpRestorationDate
	 * @param provisionalPensionFlag
	 * @param provPensionToDate
	 * @param provPensionTotalAmountPaid
	 * @param provPensionSanctionAuthority
	 * @param provPensionAuthorityNo
	 * @param provPensionAuthorityDate
	 * @param provGratuityFlag
	 * @param gratuityAmount
	 * @param gratuityActualAmountPaid
	 * @param gratuityPaymentDate
	 * @param gratuitySanctionAuthority
	 * @param gratuityAuthorityNo
	 * @param gratuityAuthorityDate
	 * @param gratuityChargedVotedFlag
	 * @param gratuityVoucherNo
	 * @param gratuityVoucherDate
	 * @param voucherChargedVotedFlag
	 */

	public TrnProvisionalPensionDtls(Long provisionalPensionDtlsId,
			String pensionerCode, String ppoNo, Date commensionDate,
			BigDecimal dcrgAmount, Date dcrgDate,
			BigDecimal basicPensionAmount, Date paidDate, Date fp1Date,
			BigDecimal fp1Amount, Date fp2Date, BigDecimal fp2Amount,
			BigDecimal trnCounter, BigDecimal dbId, String locationCode,
			BigDecimal createdUserId, BigDecimal createdPostId,
			Date createdDate, BigDecimal updatedUserId,
			BigDecimal updatedPostId, Date updatedDate,
			BigDecimal pensionRequestId, BigDecimal cvpAmount, Date cvpDate,
			Date cvpRestorationDate, String provisionalPensionFlag,
			Date provPensionToDate, BigDecimal provPensionTotalAmountPaid,
			String provPensionSanctionAuthority, String provPensionAuthorityNo,
			Date provPensionAuthorityDate, String provGratuityFlag,
			BigDecimal gratuityAmount, BigDecimal gratuityActualAmountPaid,
			Date gratuityPaymentDate, String gratuitySanctionAuthority,
			String gratuityAuthorityNo, Date gratuityAuthorityDate,
			 String gratuityVoucherNo,
			Date gratuityVoucherDate, String voucherChargedVotedFlag, String billType) {
		super();
		this.provisionalPensionDtlsId = provisionalPensionDtlsId;
		this.pensionerCode = pensionerCode;
		this.ppoNo = ppoNo;
		this.commensionDate = commensionDate;
		this.dcrgAmount = dcrgAmount;
		this.dcrgDate = dcrgDate;
		this.basicPensionAmount = basicPensionAmount;
		this.paidDate = paidDate;
		this.fp1Date = fp1Date;
		this.fp1Amount = fp1Amount;
		this.fp2Date = fp2Date;
		this.fp2Amount = fp2Amount;
		this.trnCounter = trnCounter;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.pensionRequestId = pensionRequestId;
		this.cvpAmount = cvpAmount;
		this.cvpDate = cvpDate;
		this.cvpRestorationDate = cvpRestorationDate;
		this.provisionalPensionFlag = provisionalPensionFlag;
		this.provPensionToDate = provPensionToDate;
		this.provPensionTotalAmountPaid = provPensionTotalAmountPaid;
		this.provPensionSanctionAuthority = provPensionSanctionAuthority;
		this.provPensionAuthorityNo = provPensionAuthorityNo;
		this.provPensionAuthorityDate = provPensionAuthorityDate;
		this.provGratuityFlag = provGratuityFlag;
		this.gratuityAmount = gratuityAmount;
		this.gratuityActualAmountPaid = gratuityActualAmountPaid;
		this.gratuityPaymentDate = gratuityPaymentDate;
		this.gratuitySanctionAuthority = gratuitySanctionAuthority;
		this.gratuityAuthorityNo = gratuityAuthorityNo;
		this.gratuityAuthorityDate = gratuityAuthorityDate;
		this.gratuityVoucherNo = gratuityVoucherNo;
		this.gratuityVoucherDate = gratuityVoucherDate;
		this.voucherChargedVotedFlag = voucherChargedVotedFlag;
		this.billType = billType;
	}

	// Property accessors
	
	public Long getProvisionalPensionDtlsId() {
		return provisionalPensionDtlsId;
	}

	public void setProvisionalPensionDtlsId(Long provisionalPensionDtlsId) {
		this.provisionalPensionDtlsId = provisionalPensionDtlsId;
	}

	public String getPensionerCode() {
		return this.pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}

	public String getPpoNo() {
		return this.ppoNo;
	}

	public void setPpoNo(String ppoNo) {
		this.ppoNo = ppoNo;
	}

	public Date getCommensionDate() {
		return this.commensionDate;
	}

	public void setCommensionDate(Date commensionDate) {
		this.commensionDate = commensionDate;
	}

	public BigDecimal getDcrgAmount() {
		return this.dcrgAmount;
	}

	public void setDcrgAmount(BigDecimal dcrgAmount) {
		this.dcrgAmount = dcrgAmount;
	}

	public Date getDcrgDate() {
		return this.dcrgDate;
	}

	public void setDcrgDate(Date dcrgDate) {
		this.dcrgDate = dcrgDate;
	}

	public BigDecimal getBasicPensionAmount() {
		return this.basicPensionAmount;
	}

	public void setBasicPensionAmount(BigDecimal basicPensionAmount) {
		this.basicPensionAmount = basicPensionAmount;
	}

	public Date getPaidDate() {
		return this.paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public Date getFp1Date() {
		return this.fp1Date;
	}

	public void setFp1Date(Date fp1Date) {
		this.fp1Date = fp1Date;
	}

	public BigDecimal getFp1Amount() {
		return this.fp1Amount;
	}

	public void setFp1Amount(BigDecimal fp1Amount) {
		this.fp1Amount = fp1Amount;
	}

	public Date getFp2Date() {
		return this.fp2Date;
	}

	public void setFp2Date(Date fp2Date) {
		this.fp2Date = fp2Date;
	}

	public BigDecimal getFp2Amount() {
		return this.fp2Amount;
	}

	public void setFp2Amount(BigDecimal fp2Amount) {
		this.fp2Amount = fp2Amount;
	}

	public BigDecimal getTrnCounter() {
		return this.trnCounter;
	}

	public void setTrnCounter(BigDecimal trnCounter) {
		this.trnCounter = trnCounter;
	}

	public BigDecimal getDbId() {
		return this.dbId;
	}

	public void setDbId(BigDecimal dbId) {
		this.dbId = dbId;
	}

	public String getLocationCode() {
		return this.locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public BigDecimal getCreatedUserId() {
		return this.createdUserId;
	}

	public void setCreatedUserId(BigDecimal createdUserId) {
		this.createdUserId = createdUserId;
	}

	public BigDecimal getCreatedPostId() {
		return this.createdPostId;
	}

	public void setCreatedPostId(BigDecimal createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getUpdatedUserId() {
		return this.updatedUserId;
	}

	public void setUpdatedUserId(BigDecimal updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public BigDecimal getUpdatedPostId() {
		return this.updatedPostId;
	}

	public void setUpdatedPostId(BigDecimal updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public BigDecimal getPensionRequestId() {
		return this.pensionRequestId;
	}

	public void setPensionRequestId(BigDecimal pensionRequestId) {
		this.pensionRequestId = pensionRequestId;
	}

	public BigDecimal getCvpAmount() {
		return this.cvpAmount;
	}

	public void setCvpAmount(BigDecimal cvpAmount) {
		this.cvpAmount = cvpAmount;
	}

	public Date getCvpDate() {
		return this.cvpDate;
	}

	public void setCvpDate(Date cvpDate) {
		this.cvpDate = cvpDate;
	}

	public Date getCvpRestorationDate() {
		return this.cvpRestorationDate;
	}

	public void setCvpRestorationDate(Date cvpRestorationDate) {
		this.cvpRestorationDate = cvpRestorationDate;
	}

	public String getProvisionalPensionFlag() {
		return provisionalPensionFlag;
	}

	public void setProvisionalPensionFlag(String provisionalPensionFlag) {
		this.provisionalPensionFlag = provisionalPensionFlag;
	}

	public Date getProvPensionToDate() {
		return provPensionToDate;
	}

	public void setProvPensionToDate(Date provPensionToDate) {
		this.provPensionToDate = provPensionToDate;
	}

	public BigDecimal getProvPensionTotalAmountPaid() {
		return provPensionTotalAmountPaid;
	}

	public void setProvPensionTotalAmountPaid(BigDecimal provPensionTotalAmountPaid) {
		this.provPensionTotalAmountPaid = provPensionTotalAmountPaid;
	}

	public String getProvPensionSanctionAuthority() {
		return provPensionSanctionAuthority;
	}

	public void setProvPensionSanctionAuthority(String provPensionSanctionAuthority) {
		this.provPensionSanctionAuthority = provPensionSanctionAuthority;
	}

	public String getProvPensionAuthorityNo() {
		return provPensionAuthorityNo;
	}

	public void setProvPensionAuthorityNo(String provPensionAuthorityNo) {
		this.provPensionAuthorityNo = provPensionAuthorityNo;
	}

	public Date getProvPensionAuthorityDate() {
		return provPensionAuthorityDate;
	}

	public void setProvPensionAuthorityDate(Date provPensionAuthorityDate) {
		this.provPensionAuthorityDate = provPensionAuthorityDate;
	}

	public String getProvGratuityFlag() {
		return provGratuityFlag;
	}

	public void setProvGratuityFlag(String provGratuityFlag) {
		this.provGratuityFlag = provGratuityFlag;
	}

	public BigDecimal getGratuityAmount() {
		return gratuityAmount;
	}

	public void setGratuityAmount(BigDecimal gratuityAmount) {
		this.gratuityAmount = gratuityAmount;
	}

	public BigDecimal getGratuityActualAmountPaid() {
		return gratuityActualAmountPaid;
	}

	public void setGratuityActualAmountPaid(BigDecimal gratuityActualAmountPaid) {
		this.gratuityActualAmountPaid = gratuityActualAmountPaid;
	}

	public Date getGratuityPaymentDate() {
		return gratuityPaymentDate;
	}

	public void setGratuityPaymentDate(Date gratuityPaymentDate) {
		this.gratuityPaymentDate = gratuityPaymentDate;
	}

	public String getGratuitySanctionAuthority() {
		return gratuitySanctionAuthority;
	}

	public void setGratuitySanctionAuthority(String gratuitySanctionAuthority) {
		this.gratuitySanctionAuthority = gratuitySanctionAuthority;
	}

	public String getGratuityAuthorityNo() {
		return gratuityAuthorityNo;
	}

	public void setGratuityAuthorityNo(String gratuityAuthorityNo) {
		this.gratuityAuthorityNo = gratuityAuthorityNo;
	}

	public Date getGratuityAuthorityDate() {
		return gratuityAuthorityDate;
	}

	public void setGratuityAuthorityDate(Date gratuityAuthorityDate) {
		this.gratuityAuthorityDate = gratuityAuthorityDate;
	}

	public String getGratuityVoucherNo() {
		return gratuityVoucherNo;
	}

	public void setGratuityVoucherNo(String gratuityVoucherNo) {
		this.gratuityVoucherNo = gratuityVoucherNo;
	}

	public Date getGratuityVoucherDate() {
		return gratuityVoucherDate;
	}

	public void setGratuityVoucherDate(Date gratuityVoucherDate) {
		this.gratuityVoucherDate = gratuityVoucherDate;
	}

	public String getVoucherChargedVotedFlag() {
		return voucherChargedVotedFlag;
	}

	public void setVoucherChargedVotedFlag(String voucherChargedVotedFlag) {
		this.voucherChargedVotedFlag = voucherChargedVotedFlag;
	}
	
	public String getBillType() {	
		return billType;
	}

	
	public void setBillType(String billType) {	
		this.billType = billType;
	}

	public Object clone() {

		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}
	
}
