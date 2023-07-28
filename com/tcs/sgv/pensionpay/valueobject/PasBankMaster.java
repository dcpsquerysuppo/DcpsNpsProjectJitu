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
public class PasBankMaster  implements Serializable{
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 8850738688774077137L;
	
	private Long pasBankCode;
	private String pasBankName;
	private String locationCode;
	
	public PasBankMaster()
	{}

	/**
	 * 
	 * @param pasBankCode
	 * @param pasBankName
	 * @param locationCode
	 */
	public PasBankMaster(Long pasBankCode, String pasBankName, String locationCode) {

		super();
		this.pasBankCode = pasBankCode;
		this.pasBankName = pasBankName;
		this.locationCode = locationCode;
	}

	
	public Long getPasBankCode() {
	
		return pasBankCode;
	}

	
	public void setPasBankCode(Long pasBankCode) {
	
		this.pasBankCode = pasBankCode;
	}

	
	public String getPasBankName() {
	
		return pasBankName;
	}

	
	public void setPasBankName(String pasBankName) {
	
		this.pasBankName = pasBankName;
	}

	
	public String getLocationCode() {
	
		return locationCode;
	}

	
	public void setLocationCode(String locationCode) {
	
		this.locationCode = locationCode;
	}
	
	

}
