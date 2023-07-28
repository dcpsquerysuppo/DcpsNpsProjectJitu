/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 3, 2012		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.util.Date;


/**
 * Class Description - 
 *
 *
 * @author 383385
 * @version 0.1
 * @since JDK 5.0
 * Feb 3, 2012
 */
public class LibraryNoSeqMst implements Serializable{
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1082965056800984142L;
	
	private Long librarySeqId;
	private String libraryNo;
	private Long generatedId;
	private Integer pkLength;
	private Long createdBy;
	private Long createdByPost;
	private Date createdDate;
	private Integer updatedBy;
	private Long updatedByPost;
	private Date updatedDate;
	private String locationCode;
	private char isReset;
	
	//Default constructor
	public LibraryNoSeqMst()
	{}

	/**
	 * 
	 * @param librarySeqId
	 * @param libraryNo
	 * @param generatedId
	 * @param pkLength
	 * @param createdBy
	 * @param createdByPost
	 * @param createdDate
	 * @param updatedBy
	 * @param updatedByPost
	 * @param updatedDate
	 * @param locationCode
	 * @param isReset
	 */


	public LibraryNoSeqMst(Long librarySeqId, String libraryNo, Long generatedId, Integer pkLength, Long createdBy, Long createdByPost, Date createdDate, Integer updatedBy, Long updatedByPost,
			Date updatedDate, String locationCode, char isReset) {

		super();
		this.librarySeqId = librarySeqId;
		this.libraryNo = libraryNo;
		this.generatedId = generatedId;
		this.pkLength = pkLength;
		this.createdBy = createdBy;
		this.createdByPost = createdByPost;
		this.createdDate = createdDate;
		this.updatedBy = updatedBy;
		this.updatedByPost = updatedByPost;
		this.updatedDate = updatedDate;
		this.locationCode = locationCode;
		this.isReset = isReset;
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((createdByPost == null) ? 0 : createdByPost.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((generatedId == null) ? 0 : generatedId.hashCode());
		result = prime * result + isReset;
		result = prime * result + ((libraryNo == null) ? 0 : libraryNo.hashCode());
		result = prime * result + ((librarySeqId == null) ? 0 : librarySeqId.hashCode());
		result = prime * result + ((locationCode == null) ? 0 : locationCode.hashCode());
		result = prime * result + ((pkLength == null) ? 0 : pkLength.hashCode());
		result = prime * result + ((updatedBy == null) ? 0 : updatedBy.hashCode());
		result = prime * result + ((updatedByPost == null) ? 0 : updatedByPost.hashCode());
		result = prime * result + ((updatedDate == null) ? 0 : updatedDate.hashCode());
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
		LibraryNoSeqMst other = (LibraryNoSeqMst) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdByPost == null) {
			if (other.createdByPost != null)
				return false;
		} else if (!createdByPost.equals(other.createdByPost))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (generatedId == null) {
			if (other.generatedId != null)
				return false;
		} else if (!generatedId.equals(other.generatedId))
			return false;
		if (isReset != other.isReset)
			return false;
		if (libraryNo == null) {
			if (other.libraryNo != null)
				return false;
		} else if (!libraryNo.equals(other.libraryNo))
			return false;
		if (librarySeqId == null) {
			if (other.librarySeqId != null)
				return false;
		} else if (!librarySeqId.equals(other.librarySeqId))
			return false;
		if (locationCode == null) {
			if (other.locationCode != null)
				return false;
		} else if (!locationCode.equals(other.locationCode))
			return false;
		if (pkLength == null) {
			if (other.pkLength != null)
				return false;
		} else if (!pkLength.equals(other.pkLength))
			return false;
		if (updatedBy == null) {
			if (other.updatedBy != null)
				return false;
		} else if (!updatedBy.equals(other.updatedBy))
			return false;
		if (updatedByPost == null) {
			if (other.updatedByPost != null)
				return false;
		} else if (!updatedByPost.equals(other.updatedByPost))
			return false;
		if (updatedDate == null) {
			if (other.updatedDate != null)
				return false;
		} else if (!updatedDate.equals(other.updatedDate))
			return false;
		return true;
	}

	public Long getLibrarySeqId() {
	
		return librarySeqId;
	}

	
	public void setLibrarySeqId(Long librarySeqId) {
	
		this.librarySeqId = librarySeqId;
	}

	
	public String getLibraryNo() {
	
		return libraryNo;
	}

	
	public void setLibraryNo(String libraryNo) {
	
		this.libraryNo = libraryNo;
	}

	
	public Long getGeneratedId() {
	
		return generatedId;
	}

	
	public void setGeneratedId(Long generatedId) {
	
		this.generatedId = generatedId;
	}

	public Integer getPkLength() {
	
		return pkLength;
	}

	
	public void setPkLength(Integer pkLength) {
	
		this.pkLength = pkLength;
	}

	
	public Long getCreatedBy() {
	
		return createdBy;
	}

	
	public void setCreatedBy(Long createdBy) {
	
		this.createdBy = createdBy;
	}

	
	public Long getCreatedByPost() {
	
		return createdByPost;
	}

	
	public void setCreatedByPost(Long createdByPost) {
	
		this.createdByPost = createdByPost;
	}

	
	public Date getCreatedDate() {
	
		return createdDate;
	}

	
	public void setCreatedDate(Date createdDate) {
	
		this.createdDate = createdDate;
	}

	
	public Integer getUpdatedBy() {
	
		return updatedBy;
	}

	
	public void setUpdatedBy(Integer updatedBy) {
	
		this.updatedBy = updatedBy;
	}

	
	public Long getUpdatedByPost() {
	
		return updatedByPost;
	}

	
	public void setUpdatedByPost(Long updatedByPost) {
	
		this.updatedByPost = updatedByPost;
	}

	
	public Date getUpdatedDate() {
	
		return updatedDate;
	}

	
	public void setUpdatedDate(Date updatedDate) {
	
		this.updatedDate = updatedDate;
	}

	
	public String getLocationCode() {
	
		return locationCode;
	}

	
	public void setLocationCode(String locationCode) {
	
		this.locationCode = locationCode;
	}

	
	public char getIsReset() {
	
		return isReset;
	}

	
	public void setIsReset(char isReset) {
	
		this.isReset = isReset;
	}

	

}
