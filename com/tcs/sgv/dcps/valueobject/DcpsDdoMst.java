/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jan 31, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

/**
 * Class Description - 
 *
 *
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0
 * Jan 31, 2011
 */
public class DcpsDdoMst implements java.io.Serializable{
	
	private long dcpsDdoCode;
	private String dcpsDdoName;
	
	public long getDcpsDdoCode() {
		return dcpsDdoCode;
	}
	public void setDcpsDdoCode(long dcpsDdoCode) {
		this.dcpsDdoCode = dcpsDdoCode;
	}
	public String getDcpsDdoName() {
		return dcpsDdoName;
	}
	public void setDcpsDdoName(String dcpsDdoName) {
		this.dcpsDdoName = dcpsDdoName;
	}
	

}
