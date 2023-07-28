package com.tcs.sgv.lna.valueobject;

import java.util.Date;

public class TrnEmpLoanDtls {

	private Long empLoanDtlsId;
	private String sevaarthId;
	private String empName;
	private String compAdvance;
	private String houseAdvance;
	private String motorAdvance;
	private String status;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public Long getEmpLoanDtlsId() {
		return empLoanDtlsId;
	}

	public void setEmpLoanDtlsId(Long empLoanDtlsId) {
		this.empLoanDtlsId = empLoanDtlsId;
	}

	public String getSevaarthId() {
		return sevaarthId;
	}

	public void setSevaarthId(String sevaarthId) {
		this.sevaarthId = sevaarthId;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpName() {
		return empName;
	}

	public String getCompAdvance() {
		return compAdvance;
	}

	public void setCompAdvance(String compAdvance) {
		this.compAdvance = compAdvance;
	}

	public String getHouseAdvance() {
		return houseAdvance;
	}

	public void setHouseAdvance(String houseAdvance) {
		this.houseAdvance = houseAdvance;
	}

	public String getMotorAdvance() {
		return motorAdvance;
	}

	public void setMotorAdvance(String motorAdvance) {
		this.motorAdvance = motorAdvance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(Long createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Long getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Long createdUserId) {
		this.createdUserId = createdUserId;
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
}
