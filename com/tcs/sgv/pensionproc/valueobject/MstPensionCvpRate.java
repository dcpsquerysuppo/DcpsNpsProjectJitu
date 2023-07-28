package com.tcs.sgv.pensionproc.valueobject;

import java.math.BigDecimal;


public class MstPensionCvpRate 
{
	private Long cvpRateId;
	
	private BigDecimal age;
	
	private BigDecimal cvpRate;
	
	private String payCommission;
	
	MstPensionCvpRate()
	{
		
	}

	public MstPensionCvpRate(Long cvpRateId, BigDecimal age, BigDecimal cvpRate, String payCommission) {

		super();
		this.cvpRateId = cvpRateId;
		this.age = age;
		this.cvpRate = cvpRate;
		this.payCommission = payCommission;
	}

	
	public Long getCvpRateId() {
	
		return cvpRateId;
	}

	
	public void setCvpRateId(Long cvpRateId) {
	
		this.cvpRateId = cvpRateId;
	}

	
	public BigDecimal getAge() {
	
		return age;
	}

	
	public void setAge(BigDecimal age) {
	
		this.age = age;
	}

	
	public BigDecimal getCvpRate() {
	
		return cvpRate;
	}

	
	public void setCvpRate(BigDecimal cvpRate) {
	
		this.cvpRate = cvpRate;
	}

	
	public String getPayCommission() {
	
		return payCommission;
	}

	
	public void setPayCommission(String payCommission) {
	
		this.payCommission = payCommission;
	}
	
	
}
