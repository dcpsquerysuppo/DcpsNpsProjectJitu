
package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.core.dao.GenericDao;
import java.util.List;

public interface UpdateBankDetailsDAO
extends GenericDao {
    public List getBranchNames(Long var1) throws Exception;

    public void updateBankDetails(String[] var1, String[] var2, String[] var3);
}