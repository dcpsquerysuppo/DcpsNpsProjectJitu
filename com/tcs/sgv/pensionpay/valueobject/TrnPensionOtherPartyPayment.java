package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;

public class TrnPensionOtherPartyPayment implements Cloneable
{
	public TrnPensionOtherPartyPayment()
	{

		super();
		// TODO Auto-generated constructor stub
	}

	private Long otherPartyPaymentId;
	private String partyName;
	private Long otherPartyType;
	private BigDecimal partyAmnt;
	private String pensionerCode;
	private String ppoNo;
	private String pensionerName;
	private Long billNo;
	private Date billDate;
	private Integer rejected;
	private String locationCode;
	private Long createdUserId;
    private Long createdPostId;
    private Date createdDate;
    private Long updatedUserId;
    private Long updatedPostId;
    private Date updatedDate;
	
    
	public TrnPensionOtherPartyPayment(Long otherPartyPaymentId,Long otherPartyType ,String partyName, BigDecimal partyAmnt, 
			String pensionerCode,String ppoNo,String pensionerName, Long billNo, Date billDate, Integer rejected, String locationCode, 
			Long createdUserId, Long createdPostId, Date createdDate, Long updatedUserId, Long updatedPostId, Date updatedDate)
	{

		super();
		this.otherPartyPaymentId = otherPartyPaymentId;
		this.otherPartyType=otherPartyType;
		this.partyName = partyName;
		this.partyAmnt = partyAmnt;
		this.pensionerCode = pensionerCode;
		this.ppoNo = ppoNo;
		this.pensionerName = pensionerName;
		this.billNo = billNo;
		this.billDate = billDate;
		this.rejected = rejected;
		this.locationCode = locationCode;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}
	
	
	public Long getOtherPartyPaymentId()
	{
	
		return otherPartyPaymentId;
	}
	
	public void setOtherPartyPaymentId(Long otherPartyPaymentId)
	{
	
		this.otherPartyPaymentId = otherPartyPaymentId;
	}
	
	public String getPartyName()
	{
	
		return partyName;
	}
	
	public void setPartyName(String partyName)
	{
	
		this.partyName = partyName;
	}
	
	public BigDecimal getPartyAmnt()
	{
	
		return partyAmnt;
	}
	
	public void setPartyAmnt(BigDecimal partyAmnt)
	{
	
		this.partyAmnt = partyAmnt;
	}
	
	public String getPensionerCode()
	{
	
		return pensionerCode;
	}
	
	public void setPensionerCode(String pensionerCode)
	{
	
		this.pensionerCode = pensionerCode;
	}
	
	public Long getBillNo()
	{
	
		return billNo;
	}
	
	public void setBillNo(Long billNo)
	{
	
		this.billNo = billNo;
	}
	
	public Date getBillDate()
	{
	
		return billDate;
	}
	
	public void setBillDate(Date billDate)
	{
	
		this.billDate = billDate;
	}
	
	public Integer getRejected()
	{
	
		return rejected;
	}
	
	public void setRejected(Integer rejected)
	{
	
		this.rejected = rejected;
	}
	
	public String getLocationCode()
	{
	
		return locationCode;
	}
	
	public void setLocationCode(String locationCode)
	{
	
		this.locationCode = locationCode;
	}
	
	public Long getCreatedUserId()
	{
	
		return createdUserId;
	}
	
	public void setCreatedUserId(Long createdUserId)
	{
	
		this.createdUserId = createdUserId;
	}
	
	public Long getCreatedPostId()
	{
	
		return createdPostId;
	}
	
	public void setCreatedPostId(Long createdPostId)
	{
	
		this.createdPostId = createdPostId;
	}
	
	public Date getCreatedDate()
	{
	
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate)
	{
	
		this.createdDate = createdDate;
	}
	
	public Long getUpdatedUserId()
	{
	
		return updatedUserId;
	}
	
	public void setUpdatedUserId(Long updatedUserId)
	{
	
		this.updatedUserId = updatedUserId;
	}
	
	public Long getUpdatedPostId()
	{
	
		return updatedPostId;
	}
	
	public void setUpdatedPostId(Long updatedPostId)
	{
	
		this.updatedPostId = updatedPostId;
	}
	
	public Date getUpdatedDate()
	{
	
		return updatedDate;
	}
	
	public void setUpdatedDate(Date updatedDate)
	{
	
		this.updatedDate = updatedDate;
	}


	
	public Long getOtherPartyType()
	{
	
		return otherPartyType;
	}


	
	public void setOtherPartyType(Long otherPartyType)
	{
	
		this.otherPartyType = otherPartyType;
	}


	
	public String getPpoNo()
	{
	
		return ppoNo;
	}


	
	public void setPpoNo(String ppoNo)
	{
	
		this.ppoNo = ppoNo;
	}


	
	public String getPensionerName()
	{
	
		return pensionerName;
	}


	
	public void setPensionerName(String pensionerName)
	{
	
		this.pensionerName = pensionerName;
	}

    
}