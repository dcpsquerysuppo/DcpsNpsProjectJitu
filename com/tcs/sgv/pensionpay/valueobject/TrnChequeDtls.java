package com.tcs.sgv.pensionpay.valueobject;

// Generated Jun 25, 2007 12:25:26 PM by Hibernate Tools 3.2.0.beta8

import java.math.BigDecimal;
import java.util.Date;

/**
 *  com.tcs.sgv.billproc.cheque.valueobject.TrnChequeDtls
 *  
 *  This is Java bean for getting all cheque details
 *  
 * 	Date of Creation : 10th July 2007
 * 
 *  Author : Vidhya Mashru 
 *  
 *  Revision History 
 *  =====================
 *  Vidhya M    23-Oct-2007  Made changes for code formatting
 */
public class TrnChequeDtls implements java.io.Serializable {

	// Fields    

	private long chequeId;

	private BigDecimal chequeAmt = BigDecimal.ZERO;

	private Date fromDt;

	private Date toDt;

	private String partyName;

	private Date clearedDt;

	private Long chequeNo;

	private Date printDate;

	private Long printUser;

	private Long adviceNo;

	private Short status;

	private Long finYearId;

	private long createdUserId;

	private long createdPostId;

	private Date createdDate;

	private Long updatedPostId;

	private Long updatedUserId;

	private Date updatedDate;

	private Long dbId;

	private String chequeType;

	private String ddoParty;

	private Integer trnCounter;

	private String remarks;

	private Date statusDate;
	
	private String locationCode;
	
	private Long adviceGenByPost;
	
	private Date chqDsptchDate;
	
	private Date chqDuplicateDt;
	
	private Date chqRevalidateDt;
	
	private Date chqRenameDt;
	
	private Date chqCancelDt;
	
	private Date chqReprintDt;
	
	private Long groupId;
	
	private Object[] billList;
	
	private Long adviceGenByUser;
	
	private Boolean isAccounted;
	
	private Date authorityDate;
	
	private Date adviceDt;
	
	private Date printDateTime;
	
	private String oldChqs;
	
	private Date acceptDt;
	
	private Date rejectedDt;
	
	private Date verifyDt;
	
	private Integer scrollStatus; 
	
	private Date paymentDate;
	
	private Date settlementDate;
	
	// Constructors

	/** default constructor */
	public TrnChequeDtls() {
	}

	/** minimal constructor */
	public TrnChequeDtls(long chequeId, BigDecimal chequeAmt, 
			String partyName, Short status, long createdUserId,
			long createdPostId,Date createdDate) {
		this.chequeId = chequeId;
		this.chequeAmt = chequeAmt;
		this.partyName = partyName;
		this.status = status;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
	}

	/** full constructor */
	

	public TrnChequeDtls(long chequeId, BigDecimal chequeAmt, Date fromDt, Date toDt, String partyName, Date clearedDt, Long chequeNo, Date printDate, Long printUser, Long adviceNo, Short status,
			Long finYearId, long createdUserId, long createdPostId, Date createdDate, Long updatedPostId, Long updatedUserId, Date updatedDate, Long dbId, String chequeType, String ddoParty,
			Integer trnCounter, String remarks, Date statusDate, String locationCode, Long adviceGenByPost, Date chqDsptchDate, Date chqDuplicateDt, Date chqRevalidateDt, Date chqRenameDt,
			Date chqCancelDt, Date chqReprintDt, Long groupId, Object[] billList, Long adviceGenByUser, Boolean isAccounted, Date authorityDate, Date adviceDt, Date printDateTime, String oldChqs,
			Date acceptDt, Date rejectedDt, Date verifyDt, Integer scrollStatus, Date paymentDate, Date settlementDate) {

		super();
		this.chequeId = chequeId;
		this.chequeAmt = chequeAmt;
		this.fromDt = fromDt;
		this.toDt = toDt;
		this.partyName = partyName;
		this.clearedDt = clearedDt;
		this.chequeNo = chequeNo;
		this.printDate = printDate;
		this.printUser = printUser;
		this.adviceNo = adviceNo;
		this.status = status;
		this.finYearId = finYearId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedPostId = updatedPostId;
		this.updatedUserId = updatedUserId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
		this.chequeType = chequeType;
		this.ddoParty = ddoParty;
		this.trnCounter = trnCounter;
		this.remarks = remarks;
		this.statusDate = statusDate;
		this.locationCode = locationCode;
		this.adviceGenByPost = adviceGenByPost;
		this.chqDsptchDate = chqDsptchDate;
		this.chqDuplicateDt = chqDuplicateDt;
		this.chqRevalidateDt = chqRevalidateDt;
		this.chqRenameDt = chqRenameDt;
		this.chqCancelDt = chqCancelDt;
		this.chqReprintDt = chqReprintDt;
		this.groupId = groupId;
		this.billList = billList;
		this.adviceGenByUser = adviceGenByUser;
		this.isAccounted = isAccounted;
		this.authorityDate = authorityDate;
		this.adviceDt = adviceDt;
		this.printDateTime = printDateTime;
		this.oldChqs = oldChqs;
		this.acceptDt = acceptDt;
		this.rejectedDt = rejectedDt;
		this.verifyDt = verifyDt;
		this.scrollStatus = scrollStatus;
		this.paymentDate = paymentDate;
		this.settlementDate = settlementDate;
	}


	// Property accessors
	public long getChequeId() {
		return this.chequeId;
	}
	
	public void setChequeId(long chequeId) {
		this.chequeId = chequeId;
	}

	public BigDecimal getChequeAmt() {
		return this.chequeAmt;
	}

	public void setChequeAmt(BigDecimal chequeAmt) {
		this.chequeAmt = chequeAmt;
	}

	public Date getFromDt() {
		return this.fromDt;
	}

	public void setFromDt(Date fromDt) {
		this.fromDt = fromDt;
	}

	public Date getToDt() {
		return this.toDt;
	}

	public void setToDt(Date toDt) {
		this.toDt = toDt;
	}

	public String getPartyName() {
		return this.partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public Date getClearedDt() {
		return this.clearedDt;
	}

	public void setClearedDt(Date clearedDt) {
		this.clearedDt = clearedDt;
	}

	public Long getChequeNo() {
		return this.chequeNo;
	}

	public void setChequeNo(Long chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getPrintDate() {
		return this.printDate;
	}

	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}

	public Long getPrintUser() {
		return this.printUser;
	}

	public void setPrintUser(Long printUser) {
		this.printUser = printUser;
	}

	public Long getAdviceNo() {
		return this.adviceNo;
	}

	public void setAdviceNo(Long adviceNo) {
		this.adviceNo = adviceNo;
	}

	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Long getFinYearId() {
		return this.finYearId;
	}

	public void setFinYearId(Long finYearId) {
		this.finYearId = finYearId;
	}

	public long getCreatedUserId() {
		return this.createdUserId;
	}

	public void setCreatedUserId(long createdUserId) {
		this.createdUserId = createdUserId;
	}

	public long getCreatedPostId() {
		return this.createdPostId;
	}

	public void setCreatedPostId(long createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUpdatedPostId() {
		return this.updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {
		return this.updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	
	public Long getDbId() {
		return this.dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public String getChequeType() {
		return this.chequeType;
	}

	public void setChequeType(String chequeType) {
		this.chequeType = chequeType;
	}

	public String getDdoParty() {
		return this.ddoParty;
	}

	public void setDdoParty(String ddoParty) {
		this.ddoParty = ddoParty;
	}

	public Integer getTrnCounter() {
		return this.trnCounter;
	}

	public void setTrnCounter(Integer trnCounter) {
		this.trnCounter = trnCounter;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	
	public Long getAdviceGenByPost() {
		return this.adviceGenByPost;
	}

	public void setAdviceGenByPost(Long adviceGenByPost) {
		this.adviceGenByPost = adviceGenByPost;
	}
	
	public Date getChqDsptchDate() {
		return chqDsptchDate;
	}

	public void setChqDsptchDate(Date chqDsptchDate) {
		this.chqDsptchDate = chqDsptchDate;
	}
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Object[] getBillList() {
		return billList;
	}

	public void setBillList(Object[] billList) {
		this.billList = billList;
	}

    public Date getChqDuplicateDt()
    {
        return chqDuplicateDt;
    }

    public void setChqDuplicateDt(Date chqDuplicateDt)
    {
        this.chqDuplicateDt = chqDuplicateDt;
    }

    public Date getChqRevalidateDt()
    {
        return chqRevalidateDt;
    }

    public void setChqRevalidateDt(Date chqRevalidateDt)
    {
        this.chqRevalidateDt = chqRevalidateDt;
    }

    public Date getChqRenameDt()
    {
        return chqRenameDt;
    }

    public void setChqRenameDt(Date chqRenameDt)
    {
        this.chqRenameDt = chqRenameDt;
    }

    public Date getChqCancelDt()
    {
        return chqCancelDt;
    }

    public void setChqCancelDt(Date chqCancelDt)
    {
        this.chqCancelDt = chqCancelDt;
    }

	public Date getChqReprintDt() {
		return chqReprintDt;
	}

	public void setChqReprintDt(Date chqReprintDt) {
		this.chqReprintDt = chqReprintDt;
	}

	public Long getAdviceGenByUser() {
		return this.adviceGenByUser;
	}

	public void setAdviceGenByUser(Long adviceGenByUser) {
		this.adviceGenByUser = adviceGenByUser;
	}

	public Boolean getIsAccounted() {
		return isAccounted;
	}

	public void setIsAccounted(Boolean isAccounted) {
		this.isAccounted = isAccounted;
	}

	public Date getAuthorityDate() {
		return authorityDate;
	}

	public void setAuthorityDate(Date authorityDate) {
		this.authorityDate = authorityDate;
	}

	public Date getAdviceDt() {
		return adviceDt;
	}

	public void setAdviceDt(Date adviceDt) {
		this.adviceDt = adviceDt;
	}

	public Date getPrintDateTime() {
		return printDateTime;
	}

	public void setPrintDateTime(Date printDateTime) {
		this.printDateTime = printDateTime;
	}

	public String getOldChqs() {
		return oldChqs;
	}

	public void setOldChqs(String oldChqs) {
		this.oldChqs = oldChqs;
	}
	
	public Date getAcceptDt() {
		return acceptDt;
	}

	public void setAcceptDt(Date acceptDt) {
		this.acceptDt = acceptDt;
	}
	public Date getRejectedDt() {
		return rejectedDt;
	}

	public void setRejectedDt(Date rejectedDt) {
		this.rejectedDt = rejectedDt;
	}
	public Date getVerifyDt() {
		return verifyDt;
	}

	public void setVerifyDt(Date verifyDt) {
		this.verifyDt = verifyDt;
	}
	
	public Integer getScrollStatus() {
		return this.scrollStatus;
	}
	
	public void setScrollStatus(Integer scrollStatus) {
		this.scrollStatus = scrollStatus;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}
	
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	public Date getSettlementDate() {
		return settlementDate;
	}
	
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

}
