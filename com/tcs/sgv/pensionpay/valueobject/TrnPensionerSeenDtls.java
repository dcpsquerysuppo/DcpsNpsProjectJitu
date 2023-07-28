package com.tcs.sgv.pensionpay.valueobject;
// default package
// Generated Jan 29, 2008 5:55:03 PM by Hibernate Tools 3.2.0.beta8

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 4, 2011
 */
 
 
public class TrnPensionerSeenDtls implements Serializable
{

    // Fields    

    /**
	 * serial version uid
	 */
	private static final long serialVersionUID = 386048351290332057L;

	private Long seenDtlsId;

    private String pensionerCode;

    private String seenFlag;

    private Date seenDate;

    private BigDecimal createdUserId;

    private BigDecimal createdPostId;

    private Date createdDate;

    private BigDecimal updatedUserId;

    private BigDecimal updatedPostId;

    private Date updatedDate;

    private String activeFlag;
    
    private String lifeCertStatus;
    
    private String bundleNo;
    
    private Long finYearId;

    // Constructors

    /** default constructor */
    public TrnPensionerSeenDtls()
    {
    }


    /** minimal constructor */
    public TrnPensionerSeenDtls(Long seenDtlsId)
    {
        this.seenDtlsId = seenDtlsId;
    }


   /**
    * 
    * @param seenDtlsId
    * @param pensionerCode
    * @param seenFlag
    * @param seenDate
    * @param createdUserId
    * @param createdPostId
    * @param createdDate
    * @param updatedUserId
    * @param updatedPostId
    * @param updatedDate
    * @param activeFlag
    * @param lifeCertStatus
    * @param bundleNo
    * @param finYearId
    */


	public TrnPensionerSeenDtls(Long seenDtlsId, String pensionerCode, String seenFlag, Date seenDate, BigDecimal createdUserId, BigDecimal createdPostId, Date createdDate, BigDecimal updatedUserId,
			BigDecimal updatedPostId, Date updatedDate, String activeFlag, String lifeCertStatus, String bundleNo, Long finYearId) {

		super();
		this.seenDtlsId = seenDtlsId;
		this.pensionerCode = pensionerCode;
		this.seenFlag = seenFlag;
		this.seenDate = seenDate;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.activeFlag = activeFlag;
		this.lifeCertStatus = lifeCertStatus;
		this.bundleNo = bundleNo;
		this.finYearId = finYearId;
	}


	// Property accessors
    public Long getSeenDtlsId()
    {
        return this.seenDtlsId;
    }


    public void setSeenDtlsId(Long seenDtlsId)
    {
        this.seenDtlsId = seenDtlsId;
    }


    public String getPensionerCode()
    {
        return this.pensionerCode;
    }


    public void setPensionerCode(String pensionerCode)
    {
        this.pensionerCode = pensionerCode;
    }


    public String getSeenFlag() {
		return seenFlag;
	}


	public void setSeenFlag(String seenFlag) {
		this.seenFlag = seenFlag;
	}


	public Date getSeenDate()
    {
        return this.seenDate;
    }


    public void setSeenDate(Date seenDate)
    {
        this.seenDate = seenDate;
    }


    public BigDecimal getCreatedUserId()
    {
        return this.createdUserId;
    }


    public void setCreatedUserId(BigDecimal createdUserId)
    {
        this.createdUserId = createdUserId;
    }


    public BigDecimal getCreatedPostId()
    {
        return this.createdPostId;
    }


    public void setCreatedPostId(BigDecimal createdPostId)
    {
        this.createdPostId = createdPostId;
    }


    public Date getCreatedDate()
    {
        return this.createdDate;
    }


    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }


    public BigDecimal getUpdatedUserId()
    {
        return this.updatedUserId;
    }


    public void setUpdatedUserId(BigDecimal updatedUserId)
    {
        this.updatedUserId = updatedUserId;
    }


    public BigDecimal getUpdatedPostId()
    {
        return this.updatedPostId;
    }


    public void setUpdatedPostId(BigDecimal updatedPostId)
    {
        this.updatedPostId = updatedPostId;
    }


    public Date getUpdatedDate()
    {
        return this.updatedDate;
    }


    public void setUpdatedDate(Date updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    public void setActiveFlag(String activeFlag)
    {
    	this.activeFlag = activeFlag;
    }
    
    public String getActiveFlag()
    {
    	return this.activeFlag;
    }

	public String getLifeCertStatus() {
	
		return lifeCertStatus;
	}
	
	public void setLifeCertStatus(String lifeCertStatus) {
	
		this.lifeCertStatus = lifeCertStatus;
	}
	
	public String getBundleNo() {
	
		return bundleNo;
	}
	
	public void setBundleNo(String bundleNo) {
	
		this.bundleNo = bundleNo;
	}

	public Long getFinYearId() {
	
		return finYearId;
	}
	
	public void setFinYearId(Long finYearId) {
	
		this.finYearId = finYearId;
	}
    
}
