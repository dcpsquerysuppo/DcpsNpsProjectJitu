package com.tcs.sgv.common.dao;

/**
 * @author 203818
 * 
 */
public interface BillMovementDAO 
{
	public long getmaxMovementId(long billNo);
	public int updateObjectionFlag(Long billNo);
}
