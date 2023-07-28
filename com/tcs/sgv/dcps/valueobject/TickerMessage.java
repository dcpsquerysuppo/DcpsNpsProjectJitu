package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class TickerMessage {
	
	private Long dcpsTickerMessageIdPk;
	private String message1;
	private String message2;
	private String message3;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;
	private Date startDate;
	private Date endDate;

	public Long getDcpsTickerMessageIdPk() {
		return dcpsTickerMessageIdPk;
	}

	public void setDcpsTickerMessageIdPk(Long dcpsTickerMessageIdPk) {
		this.dcpsTickerMessageIdPk = dcpsTickerMessageIdPk;
	}

	public String getMessage1() {
		return message1;
	}

	public void setMessage1(String message1) {
		this.message1 = message1;
		
	}
	public String getMessage2() {
		return message2;
	}

	public void setMessage2(String message2) {
		this.message2 = message2;
		
	}
	public String getMessage3() {
		return message3;
	}

	public void setMessage3(String message3) {
		this.message3 = message3;
		
	}
	
	public Long getPostId() {
		return PostId;
	}

	public void setPostId(Long postId) {
		PostId = postId;
	}

	public Long getUserId() {
		return UserId;
	}

	public void setUserId(Long userId) {
		UserId = userId;
	}

	public Date getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(Date createdDate) {
		CreatedDate = createdDate;
	}

	public Long getUpdatedPostId() {
		return UpdatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		UpdatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {
		return UpdatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		UpdatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {
		return UpdatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		UpdatedDate = updatedDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}

