/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 29, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;

/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Apr 29, 2011
 */
public class PensionDtlVO {

	String pensionerCode;

	String ppoNo;

	String pensionerName;

	Date dateOfRetirement;

	Date commencementDate;

	/**
	 * @param pensionerCode
	 * @param ppoNo
	 * @param pensionerName
	 * @param dateOfRetirement
	 * @param commencementDate
	 */
	public PensionDtlVO(String pensionerCode, String ppoNo, String pensionerName,
			Date dateOfRetirement, Date commencementDate) {
		super();
		this.pensionerCode = pensionerCode;
		this.ppoNo = ppoNo;
		this.pensionerName = pensionerName;
		this.dateOfRetirement = dateOfRetirement;
		this.commencementDate = commencementDate;
	}

	/**
	 * @return the pensionerCode
	 */
	public String getPensionerCode() {
		return pensionerCode;
	}

	/**
	 * @param pensionerCode
	 *            the pensionerCode to set
	 */
	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}

	/**
	 * @return the ppoNo
	 */
	public String getPpoNo() {
		return ppoNo;
	}

	/**
	 * @param ppoNo
	 *            the ppoNo to set
	 */
	public void setPpoNo(String ppoNo) {
		this.ppoNo = ppoNo;
	}

	/**
	 * @return the pensionerName
	 */
	public String getPensionerName() {
		return pensionerName;
	}

	/**
	 * @param pensionerName
	 *            the pensionerName to set
	 */
	public void setPensionerName(String pensionerName) {
		this.pensionerName = pensionerName;
	}

	/**
	 * @return the dateOfRetirement
	 */
	public Date getDateOfRetirement() {
		return dateOfRetirement;
	}

	/**
	 * @param dateOfRetirement
	 *            the dateOfRetirement to set
	 */
	public void setDateOfRetirement(Date dateOfRetirement) {
		this.dateOfRetirement = dateOfRetirement;
	}

	/**
	 * @return the commencementDate
	 */
	public Date getCommencementDate() {
		return commencementDate;
	}

	/**
	 * @param commencementDate
	 *            the commencementDate to set
	 */
	public void setCommencementDate(Date commencementDate) {
		this.commencementDate = commencementDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commencementDate == null) ? 0 : commencementDate.hashCode());
		result = prime * result + ((dateOfRetirement == null) ? 0 : dateOfRetirement.hashCode());
		result = prime * result + ((pensionerCode == null) ? 0 : pensionerCode.hashCode());
		result = prime * result + ((pensionerName == null) ? 0 : pensionerName.hashCode());
		result = prime * result + ((ppoNo == null) ? 0 : ppoNo.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PensionDtlVO other = (PensionDtlVO) obj;
		if (commencementDate == null) {
			if (other.commencementDate != null) {
				return false;
			}
		} else if (!commencementDate.equals(other.commencementDate)) {
			return false;
		}
		if (dateOfRetirement == null) {
			if (other.dateOfRetirement != null) {
				return false;
			}
		} else if (!dateOfRetirement.equals(other.dateOfRetirement)) {
			return false;
		}
		if (pensionerCode == null) {
			if (other.pensionerCode != null) {
				return false;
			}
		} else if (!pensionerCode.equals(other.pensionerCode)) {
			return false;
		}
		if (pensionerName == null) {
			if (other.pensionerName != null) {
				return false;
			}
		} else if (!pensionerName.equals(other.pensionerName)) {
			return false;
		}
		if (ppoNo == null) {
			if (other.ppoNo != null) {
				return false;
			}
		} else if (!ppoNo.equals(other.ppoNo)) {
			return false;
		}
		return true;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString() {
		final String TAB = "    ";

		String retValue = "";

		retValue = "PensionDtlVO ( " + super.toString() + TAB + "pensionerCode = "
				+ this.pensionerCode + TAB + "ppoNo = " + this.ppoNo + TAB + "pensionerName = "
				+ this.pensionerName + TAB + "dateOfRetirement = " + this.dateOfRetirement + TAB
				+ "commencementDate = " + this.commencementDate + TAB + " )";

		return retValue;
	}

}
