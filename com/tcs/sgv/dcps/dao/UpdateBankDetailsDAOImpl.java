
package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.UpdateBankDetailsDAO;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UpdateBankDetailsDAOImpl
extends GenericDaoHibernateImpl
implements UpdateBankDetailsDAO {
    private final Log gLogger;
    Session ghibSession;

    public UpdateBankDetailsDAOImpl(Class type, SessionFactory sessionFactory) {
        super(type);
        this.gLogger = LogFactory.getLog(this.getClass());
        this.ghibSession = null;
        this.setSessionFactory(sessionFactory);
        this.ghibSession = this.getSession();
    }

    public List getBankNamesForUpdation() throws Exception {
        String query = "select MB.bankCode, MB.bankName from MstBankPay MB where MB.activateFlag=1 order by MB.bankName,MB.bankCode";
        ArrayList<ComboValuesVO> lLstReturnList = null;
        StringBuilder sb = new StringBuilder();
        sb.append(query);
        Query selectQuery = this.ghibSession.createQuery(sb.toString());
        List lLstResult = selectQuery.list();
        ComboValuesVO lObjComboValuesVO = null;
        if (lLstResult != null && lLstResult.size() != 0) {
            lLstReturnList = new ArrayList();
            int liCtr = 0;
            while (liCtr < lLstResult.size()) {
                Object[] obj = (Object[])lLstResult.get(liCtr);
                lObjComboValuesVO = new ComboValuesVO();
                lObjComboValuesVO.setId(obj[0].toString());
                lObjComboValuesVO.setDesc(obj[1].toString());
                lLstReturnList.add(lObjComboValuesVO);
                ++liCtr;
            }
        } else {
            lLstReturnList = new ArrayList<ComboValuesVO>();
            lObjComboValuesVO = new ComboValuesVO();
            lObjComboValuesVO.setId("-1");
            lObjComboValuesVO.setDesc("--Select--");
            lLstReturnList.add(lObjComboValuesVO);
        }
        return lLstReturnList;
    }

    public List getBranchNames(Long lLngBankCode) throws Exception {
        Object lLstReturnList = null;
        List lLstResult = null;
        try {
            StringBuilder lSBQuery = new StringBuilder();
            lSBQuery.append(" select branch_id,branch_name,branch_address,ifsc_code from RLT_BANK_BRANCH_PAY");
            lSBQuery.append(" where new_address is null and activate_flag = 1 and BANK_code = :bankCode");
            lSBQuery.append(" order by branch_name");
            SQLQuery lObjQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
            lObjQuery.setParameter("bankCode", (Object)lLngBankCode);
            lLstResult = lObjQuery.list();
        }
        catch (Exception e) {
            this.gLogger.error((Object)("Error is : " + e), (Throwable)e);
            throw e;
        }
        return lLstResult;
    }

    public void updateBankDetails(String[] lStrBranchIdFinal, String[] lStrNewAddressFinal, String[] lStrPinCodeFinal) {
        try {
            int i = 0;
            while (i < lStrBranchIdFinal.length) {
                String bankId = lStrBranchIdFinal[i];
                String newAdd = lStrNewAddressFinal[i];
                String pinCode = lStrPinCodeFinal[i];
                StringBuilder lSBQuery = new StringBuilder();
                lSBQuery.append("update RLT_BANK_BRANCH_PAY set NEW_ADDRESS = '" + newAdd + "', PINCODE = '" + pinCode + "' where BRANCH_ID = '" + bankId + "'");
                SQLQuery lObjQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
                this.gLogger.info((Object)("lSBQuery.toString()-----" + lSBQuery.toString()));
                lObjQuery.executeUpdate();
                ++i;
            }
        }
        catch (Exception e) {
            this.gLogger.error((Object)("Error is : " + e), (Throwable)e);
        }
    }
}