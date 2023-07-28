/*
 * PACKAGE            : com.tcs.sgv.apps.valuebeans.budget
 * filename           : BudExpEstDeptVO.java
 * @version           : 1.0
 * @author            : Keyur Shah
 * Date               : 10-01-2006
 * Description        : This is the class of BudExpEstDeptVO
 *-----------------------------------------------------------------------------
 */
package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;


/**
 * [TODO add class documentation here]
 *
 * @author $author$
 * @version $Revision$
 */
public class BudExpEstDeptVO extends BaseValueBean
{
    /** String representing Dept ID */
    private String lStrDeptID = null;

    /** String representing Dept Name */
    private String lStrDeptName = null;

    /** String representing Dept Short Name */
    private String lStrDeptShrtName = null;
    
    
    private String lStrBPNCode = null;

    /**
     * It sets Department ID
     *
     * @param lStrDeptID Department ID
     */
    public void setDeptID(String lStrDeptID)
    {
        this.lStrDeptID = lStrDeptID;
    }

    /**
     * It returns Department ID
     *
     * @return Department ID
     */
    public String getDeptID()
    {
        return lStrDeptID;
    }

    /**
     * It sets Department Name
     *
     * @param lStrDeptName Department Name
     */
    public void setDeptName(String lStrDeptName)
    {
        this.lStrDeptName = lStrDeptName;
    }

    /**
     * It returns Department Name
     *
     * @return Department Name
     */
    public String getDeptName()
    {
        return lStrDeptName;
    }

    /**
     * It sets Department Short Name
     *
     * @param lStrDeptShrtName Department Short Name
     */
    public void setDeptShrtName(String lStrDeptShrtName)
    {
        this.lStrDeptShrtName = lStrDeptShrtName;
    }

    /**
     * It returns Department Short Name
     *
     * @return Department Short Name
     */
    public String getDeptShrtName()
    {
        return lStrDeptShrtName;
    }
    
    
    /**
     * It sets Department Name
     *
     * @param lStrDeptName Department Name
     */
    public void setBpnCode(String lStrBPNCode)
    {
        this.lStrBPNCode = lStrBPNCode;
    }

    /**
     * It returns Department Name
     *
     * @return Department Name
     */
    public String getBpnCode()
    {
        return lStrBPNCode;
    }
}
