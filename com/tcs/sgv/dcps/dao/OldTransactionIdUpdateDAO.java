package com.tcs.sgv.dcps.dao;

import java.util.List;

public interface OldTransactionIdUpdateDAO {

	List getAllTreasuries();

	List getFinyears();

	List getDetails( String string, String string2,String treasurynos);

	int updateDetails(String fid, String tid, String res, String rem);

}
