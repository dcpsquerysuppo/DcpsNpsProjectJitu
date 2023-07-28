/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 29, 2011		Anjana Suvariya								
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
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Aug 29, 2011
 */
public class HstCommutationDtls implements Serializable{
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 4623334674009124713L;
	
	private Long cvpDtlsId;
	private Long dbId;
	private Long locationCode;
	private String pensionerCode;    
	private String orderNo;
	private BigDecimal paymentAmount;
	private String voucherNo;
	private Date voucherDate; 
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Date orderDate;
	private BigDecimal totalOrderAmount;
	private Long billNo;
	private String nameOfPayee;
	private String trsryForPayment;
	private BigDecimal basicPensionAmt;
	private BigDecimal cvpMonthlyAmt;
	private BigDecimal reducedPensionAmt;
	private Date cvpStartDate;
	private Date cvpEndDate;
	private String chequeNo;
	private Date chequeDate;
	private String cvpAuthority;
	
	public HstCommutationDtls()
	{}

	/**
	 * 
	 * @param cvpDtlsId
	 * @param dbId
	 * @param locationCode
	 * @param pensionerCode
	 * @param orderNo
	 * @param paymentAmount
	 * @param voucherNo
	 * @param voucherDate
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param orderDate
	 * @param totalOrderAmount
	 * @param billNo
	 * @param nameOfPayee
	 * @param trsryForPayment
	 * @param basicPensionAmt
	 * @param cvpMonthlyAmt
	 * @param reducedPensionAmt
	 * @param cvpStartDate
	 * @param cvpEndDate
	 * @param chequeNo
	 * @param chequeDate
	 * @param cvpAuthority
	 */
	public HstCommutationDtls(Long cvpDtlsId, Long dbId, Long locationCode,
			String pensionerCode, String orderNo, BigDecimal paymentAmount,
			String voucherNo, Date voucherDate, Long createdUserId,
			Long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate, Date orderDate,
			BigDecimal totalOrderAmount, Long billNo, String nameOfPayee,
			String trsryForPayment, BigDecimal basicPensionAmt,
			BigDecimal cvpMonthlyAmt, BigDecimal reducedPensionAmt,
			Date cvpStartDate, Date cvpEndDate, String chequeNo, Date chequeDate,String cvpAuthority) {
		super();
		this.cvpDtlsId = cvpDtlsId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerCode = pensionerCode;
		this.orderNo = orderNo;
		this.paymentAmount = paymentAmount;
		this.voucherNo = voucherNo;
		this.voucherDate = voucherDate;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.orderDate = orderDate;
		this.totalOrderAmount = totalOrderAmount;
		this.billNo = billNo;
		this.nameOfPayee = nameOfPayee;
		this.trsryForPayment = trsryForPayment;
		this.basicPensionAmt = basicPensionAmt;
		this.cvpMonthlyAmt = cvpMonthlyAmt;
		this.reducedPensionAmt = reducedPensionAmt;
		this.cvpStartDate = cvpStartDate;
		this.cvpEndDate = cvpEndDate;
		this.chequeNo = chequeNo;
		this.chequeDate = chequeDate;
		this.cvpAuthority = cvpAuthority;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((basicPensionAmt == null) ? 0 : basicPensionAmt.hashCode());
		result = prime * result + ((billNo == null) ? 0 : billNo.hashCode());
		result = prime * result
				+ ((chequeDate == null) ? 0 : chequeDate.hashCode());
		result = prime * result
				+ ((chequeNo == null) ? 0 : chequeNo.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((createdPostId == null) ? 0 : createdPostId.hashCode());
		result = prime * result
				+ ((createdUserId == null) ? 0 : createdUserId.hashCode());
		result = prime * result
				+ ((cvpAuthority == null) ? 0 : cvpAuthority.hashCode());
		result = prime * result
				+ ((cvpDtlsId == null) ? 0 : cvpDtlsId.hashCode());
		result = prime * result
				+ ((cvpEndDate == null) ? 0 : cvpEndDate.hashCode());
		result = prime * result
				+ ((cvpMonthlyAmt == null) ? 0 : cvpMonthlyAmt.hashCode());
		result = prime * result
				+ ((cvpStartDate == null) ? 0 : cvpStartDate.hashCode());
		result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
		result = prime * result
				+ ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result
				+ ((nameOfPayee == null) ? 0 : nameOfPayee.hashCode());
		result = prime * result
				+ ((orderDate == null) ? 0 : orderDate.hashCode());
		result = prime * result + ((orderNo == null) ? 0 : orderNo.hashCode());
		result = prime * result
				+ ((paymentAmount == null) ? 0 : paymentAmount.hashCode());
		result = prime * result
				+ ((pensionerCode == null) ? 0 : pensionerCode.hashCode());
		result = prime
				* result
				+ ((reducedPensionAmt == null) ? 0 : reducedPensionAmt
						.hashCode());
		result = prime
				* result
				+ ((totalOrderAmount == null) ? 0 : totalOrderAmount.hashCode());
		result = prime * result
				+ ((trsryForPayment == null) ? 0 : trsryForPayment.hashCode());
		result = prime * result
				+ ((updatedDate == null) ? 0 : updatedDate.hashCode());
		result = prime * result
				+ ((updatedPostId == null) ? 0 : updatedPostId.hashCode());
		result = prime * result
				+ ((updatedUserId == null) ? 0 : updatedUserId.hashCode());
		result = prime * result
				+ ((voucherDate == null) ? 0 : voucherDate.hashCode());
		result = prime * result
				+ ((voucherNo == null) ? 0 : voucherNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HstCommutationDtls other = (HstCommutationDtls) obj;
		if (basicPensionAmt == null) {
			if (other.basicPensionAmt != null)
				return false;
		} else if (!basicPensionAmt.equals(other.basicPensionAmt))
			return false;
		if (billNo == null) {
			if (other.billNo != null)
				return false;
		} else if (!billNo.equals(other.billNo))
			return false;
		if (chequeDate == null) {
			if (other.chequeDate != null)
				return false;
		} else if (!chequeDate.equals(other.chequeDate))
			return false;
		if (chequeNo == null) {
			if (other.chequeNo != null)
				return false;
		} else if (!chequeNo.equals(other.chequeNo))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (createdPostId == null) {
			if (other.createdPostId != null)
				return false;
		} else if (!createdPostId.equals(other.createdPostId))
			return false;
		if (createdUserId == null) {
			if (other.createdUserId != null)
				return false;
		} else if (!createdUserId.equals(other.createdUserId))
			return false;
		if (cvpAuthority == null) {
			if (other.cvpAuthority != null)
				return false;
		} else if (!cvpAuthority.equals(other.cvpAuthority))
			return false;
		if (cvpDtlsId == null) {
			if (other.cvpDtlsId != null)
				return false;
		} else if (!cvpDtlsId.equals(other.cvpDtlsId))
			return false;
		if (cvpEndDate == null) {
			if (other.cvpEndDate != null)
				return false;
		} else if (!cvpEndDate.equals(other.cvpEndDate))
			return false;
		if (cvpMonthlyAmt == null) {
			if (other.cvpMonthlyAmt != null)
				return false;
		} else if (!cvpMonthlyAmt.equals(other.cvpMonthlyAmt))
			return false;
		if (cvpStartDate == null) {
			if (other.cvpStartDate != null)
				return false;
		} else if (!cvpStartDate.equals(other.cvpStartDate))
			return false;
		if (dbId == null) {
			if (other.dbId != null)
				return false;
		} else if (!dbId.equals(other.dbId))
			return false;
		if (locationCode == null) {
			if (other.locationCode != null)
				return false;
		} else if (!locationCode.equals(other.locationCode))
			return false;
		if (nameOfPayee == null) {
			if (other.nameOfPayee != null)
				return false;
		} else if (!nameOfPayee.equals(other.nameOfPayee))
			return false;
		if (orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!orderDate.equals(other.orderDate))
			return false;
		if (orderNo == null) {
			if (other.orderNo != null)
				return false;
		} else if (!orderNo.equals(other.orderNo))
			return false;
		if (paymentAmount == null) {
			if (other.paymentAmount != null)
				return false;
		} else if (!paymentAmount.equals(other.paymentAmount))
			return false;
		if (pensionerCode == null) {
			if (other.pensionerCode != null)
				return false;
		} else if (!pensionerCode.equals(other.pensionerCode))
			return false;
		if (reducedPensionAmt == null) {
			if (other.reducedPensionAmt != null)
				return false;
		} else if (!reducedPensionAmt.equals(other.reducedPensionAmt))
			return false;
		if (totalOrderAmount == null) {
			if (other.totalOrderAmount != null)
				return false;
		} else if (!totalOrderAmount.equals(other.totalOrderAmount))
			return false;
		if (trsryForPayment == null) {
			if (other.trsryForPayment != null)
				return false;
		} else if (!trsryForPayment.equals(other.trsryForPayment))
			return false;
		if (updatedDate == null) {
			if (other.updatedDate != null)
				return false;
		} else if (!updatedDate.equals(other.updatedDate))
			return false;
		if (updatedPostId == null) {
			if (other.updatedPostId != null)
				return false;
		} else if (!updatedPostId.equals(other.updatedPostId))
			return false;
		if (updatedUserId == null) {
			if (other.updatedUserId != null)
				return false;
		} else if (!updatedUserId.equals(other.updatedUserId))
			return false;
		if (voucherDate == null) {
			if (other.voucherDate != null)
				return false;
		} else if (!voucherDate.equals(other.voucherDate))
			return false;
		if (voucherNo == null) {
			if (other.voucherNo != null)
				return false;
		} else if (!voucherNo.equals(other.voucherNo))
			return false;
		return true;
	}

	public Long getCvpDtlsId() {
	
		return cvpDtlsId;
	}

	
	public void setCvpDtlsId(Long cvpDtlsId) {
	
		this.cvpDtlsId = cvpDtlsId;
	}

	
	public Long getDbId() {
	
		return dbId;
	}

	
	public void setDbId(Long dbId) {
	
		this.dbId = dbId;
	}

	
	public Long getLocationCode() {
	
		return locationCode;
	}

	
	public void setLocationCode(Long locationCode) {
	
		this.locationCode = locationCode;
	}

	
	public String getPensionerCode() {
	
		return pensionerCode;
	}

	
	public void setPensionerCode(String pensionerCode) {
	
		this.pensionerCode = pensionerCode;
	}

	
	public String getOrderNo() {
	
		return orderNo;
	}

	
	public void setOrderNo(String orderNo) {
	
		this.orderNo = orderNo;
	}

	
	public BigDecimal getPaymentAmount() {
	
		return paymentAmount;
	}

	
	public void setPaymentAmount(BigDecimal paymentAmount) {
	
		this.paymentAmount = paymentAmount;
	}

	
	public String getVoucherNo() {
	
		return voucherNo;
	}

	
	public void setVoucherNo(String voucherNo) {
	
		this.voucherNo = voucherNo;
	}

	
	public Date getVoucherDate() {
	
		return voucherDate;
	}

	
	public void setVoucherDate(Date voucherDate) {
	
		this.voucherDate = voucherDate;
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

	
	public Date getOrderDate() {
	
		return orderDate;
	}

	
	public void setOrderDate(Date orderDate) {
	
		this.orderDate = orderDate;
	}
	
	public BigDecimal getTotalOrderAmount() {
	
		return totalOrderAmount;
	}
	
	public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
	
		this.totalOrderAmount = totalOrderAmount;
	}

	public Long getBillNo() {
		return billNo;
	}

	public void setBillNo(Long billNo) {
		this.billNo = billNo;
	}

	public String getNameOfPayee() {
		return nameOfPayee;
	}

	public void setNameOfPayee(String nameOfPayee) {
		this.nameOfPayee = nameOfPayee;
	}

	public String getTrsryForPayment() {
		return trsryForPayment;
	}

	public void setTrsryForPayment(String trsryForPayment) {
		this.trsryForPayment = trsryForPayment;
	}

	public BigDecimal getBasicPensionAmt() {
		return basicPensionAmt;
	}

	public void setBasicPensionAmt(BigDecimal basicPensionAmt) {
		this.basicPensionAmt = basicPensionAmt;
	}

	public BigDecimal getCvpMonthlyAmt() {
		return cvpMonthlyAmt;
	}

	public void setCvpMonthlyAmt(BigDecimal cvpMonthlyAmt) {
		this.cvpMonthlyAmt = cvpMonthlyAmt;
	}

	public BigDecimal getReducedPensionAmt() {
		return reducedPensionAmt;
	}

	public void setReducedPensionAmt(BigDecimal reducedPensionAmt) {
		this.reducedPensionAmt = reducedPensionAmt;
	}

	public Date getCvpStartDate() {
		return cvpStartDate;
	}

	public void setCvpStartDate(Date cvpStartDate) {
		this.cvpStartDate = cvpStartDate;
	}

	public Date getCvpEndDate() {
		return cvpEndDate;
	}

	public void setCvpEndDate(Date cvpEndDate) {
		this.cvpEndDate = cvpEndDate;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getCvpAuthority() {
		return cvpAuthority;
	}

	public void setCvpAuthority(String cvpAuthority) {
		this.cvpAuthority = cvpAuthority;
	}
	
}
