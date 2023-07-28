package com.tcs.sgv.dcps.dao;

import java.util.List;

public interface DdoRegDao {

	
	List getDdoGeneralReport();

	List getDdoDetailForReg(String ddoCode);

	List isDdoRegExist(String ddo_reg_no);

	void registerDDO(String ddo_reg_no,String ddocode);


}
