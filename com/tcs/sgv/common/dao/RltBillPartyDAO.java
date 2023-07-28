package com.tcs.sgv.common.dao;

import java.util.List;

import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.core.dao.GenericDao;

public interface RltBillPartyDAO extends GenericDao<RltBillParty,Long>
{
    public List getPartyByBill(Long lLngBillNo);
}
