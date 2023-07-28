package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface UpdateNSDLDetailsDAO extends GenericDao{
	public List getDdoWiseTotalAmt(Long monthId, Long yrId,String treasuryCode);

    public List getFinyear();

}


