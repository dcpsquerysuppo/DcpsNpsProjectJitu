package com.tcs.sgv.dcps.valueobject;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class BrokenPeriodPayCustomVO
{
	private Date fromDate;
	private Date toDate;
	private Long noOfDays;
	private Long basicPay;
	private Long netPay;
	private String reason;
	private String remarks;
	private List allowList;
	private List deductList;

	public Date getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(Date fromDate)
	{
		this.fromDate = fromDate;
	}

	public Date getToDate()
	{
		return toDate;
	}

	public void setToDate(Date toDate)
	{
		this.toDate = toDate;
	}

	public Long getNoOfDays()
	{
		return noOfDays;
	}

	public void setNoOfDays(Long noOfDays)
	{
		this.noOfDays = noOfDays;
	}

	public Long getBasicPay()
	{
		return basicPay;
	}

	public void setBasicPay(Long basicPay)
	{
		this.basicPay = basicPay;
	}

	public Long getNetPay()
	{
		return netPay;
	}

	public void setNetPay(Long netPay)
	{
		this.netPay = netPay;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public List getAllowList()
	{
		return allowList;
	}

	public void setAllowList(List allowList)
	{
		this.allowList = allowList;
	}

	public List getDeductList()
	{
		return deductList;
	}

	public void setDeductList(List deductList)
	{
		this.deductList = deductList;
	}

	

}
