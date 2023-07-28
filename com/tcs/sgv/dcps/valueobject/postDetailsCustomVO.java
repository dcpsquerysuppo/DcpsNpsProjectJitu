package com.tcs.sgv.dcps.valueobject;

public class postDetailsCustomVO  implements java.io.Serializable
{

	private long postId;
	
	private String postName;
	
	private long postTypeId;
	
	private String dsgnName;

	private long dcpsEmpId;
	
	private String name;
	
	
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dcpsEmpId
	 */
	public long getDcpsEmpId() {
		return dcpsEmpId;
	}

	/**
	 * @param dcpsEmpId the dcpsEmpId to set
	 */
	public void setDcpsEmpId(long dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
	}

	/**
	 * @return the dsgnName
	 */
	public String getDsgnName() {
		return dsgnName;
	}

	/**
	 * @param dsgnName the dsgnName to set
	 */
	public void setDsgnName(String dsgnName) {
		this.dsgnName = dsgnName;
	}

	/**
	 * @return the postId
	 */
	public long getPostId() {
		return postId;
	}

	/**
	 * @param postId the postId to set
	 */
	public void setPostId(long postId) {
		this.postId = postId;
	}

	

	/**
	 * @return the postName
	 */
	public String getPostName() {
		return postName;
	}

	/**
	 * @param postName the postName to set
	 */
	public void setPostName(String postName) {
		this.postName = postName;
	}

	/**
	 * @return the postTypeId
	 */
	public long getPostTypeId() {
		return postTypeId;
	}

	/**
	 * @param postTypeId the postTypeId to set
	 */
	public void setPostTypeId(long postTypeId) {
		this.postTypeId = postTypeId;
	}

	

}
