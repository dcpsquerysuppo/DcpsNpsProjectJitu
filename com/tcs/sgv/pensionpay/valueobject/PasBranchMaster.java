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
public class PasBranchMaster implements Serializable{
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 6718286975408787313L;
	
	private Long pasBankCode;
	private Long pasBranchCode;
	private String pasBranchName;
	private String locationCode;
	private Long ifmsBankCode;
	private Long ifmsBranchCode;
	
	public PasBranchMaster()
	{}

	/**
	 * 
	 * @param pasBankCode
	 * @param pasBranchCode
	 * @param pasBranchName
	 * @param locationCode
	 * @param ifmsBankCode
	 * @param ifmsBranchCode
	 */
	public PasBranchMaster(Long pasBankCode, Long pasBranchCode, String pasBranchName, String locationCode, Long ifmsBankCode, Long ifmsBranchCode) {

		super();
		this.pasBankCode = pasBankCode;
		this.pasBranchCode = pasBranchCode;
		this.pasBranchName = pasBranchName;
		this.locationCode = locationCode;
		this.ifmsBankCode = ifmsBankCode;
		this.ifmsBranchCode = ifmsBranchCode;
	}

	
	public Long getPasBankCode() {
	
		return pasBankCode;
	}

	
	public void setPasBankCode(Long pasBankCode) {
	
		this.pasBankCode = pasBankCode;
	}

	
	public Long getPasBranchCode() {
	
		return pasBranchCode;
	}

	
	public void setPasBranchCode(Long pasBranchCode) {
	
		this.pasBranchCode = pasBranchCode;
	}

	
	public String getPasBranchName() {
	
		return pasBranchName;
	}

	
	public void setPasBranchName(String pasBranchName) {
	
		this.pasBranchName = pasBranchName;
	}

	
	public String getLocationCode() {
	
		return locationCode;
	}

	
	public void setLocationCode(String locationCode) {
	
		this.locationCode = locationCode;
	}

	
	public Long getIfmsBankCode() {
	
		return ifmsBankCode;
	}

	
	public void setIfmsBankCode(Long ifmsBankCode) {
	
		this.ifmsBankCode = ifmsBankCode;
	}

	
	public Long getIfmsBranchCode() {
	
		return ifmsBranchCode;
	}

	
	public void setIfmsBranchCode(Long ifmsBranchCode) {
	
		this.ifmsBranchCode = ifmsBranchCode;
	}
	
	
	

}
