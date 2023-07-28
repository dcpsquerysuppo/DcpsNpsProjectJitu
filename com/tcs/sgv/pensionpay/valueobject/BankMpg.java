/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 29, 2011		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Sep 29, 2011
 */
public class BankMpg implements Serializable{
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 3953230550482708755L;
	
	private Long ifmsBankCode;
	private Long pasBankCode;
	private String locationCode;
	
	public BankMpg()
	{}

	/**
	 * 
	 * @param ifmsBankCode
	 * @param pasBankCode
	 * @param locationCode
	 */
	public BankMpg(Long ifmsBankCode, Long pasBankCode, String locationCode) {

		super();
		this.ifmsBankCode = ifmsBankCode;
		this.pasBankCode = pasBankCode;
		this.locationCode = locationCode;
	}

	
	public Long getIfmsBankCode() {
	
		return ifmsBankCode;
	}

	
	public void setIfmsBankCode(Long ifmsBankCode) {
	
		this.ifmsBankCode = ifmsBankCode;
	}

	
	public Long getPasBankCode() {
	
		return pasBankCode;
	}

	
	public void setPasBankCode(Long pasBankCode) {
	
		this.pasBankCode = pasBankCode;
	}

	
	public String getLocationCode() {
	
		return locationCode;
	}

	
	public void setLocationCode(String locationCode) {
	
		this.locationCode = locationCode;
	}
	
	
	
}
