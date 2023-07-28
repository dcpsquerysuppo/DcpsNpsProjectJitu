/*
 * PACKAGE            : com.tcs.sgv.apps.valuebeans.budget
 * filename           : BudExpEstBPNCodeVO.java
 * @version           : 1.0
 * @author            : Keyur Shah
 * Date               : 13-12-2005
 * Description        : This is the class of BudExpEstBPNCodeVO
 *-----------------------------------------------------------------------------
 */
package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;


public class BudExpEstBPNCodeVO extends BaseValueBean
{
    /** Integer representing Budget Publication Number ID */
    private int lIntBPNID = 0;

    /** String representing Budget Publication Number Code */
    private String lStrBPNCode = null;

    /** String representing Budget Publication Number Description */
    private String lStrBPNDesc = null;

    /** String representing Department ID */
    private String lStrDeptID = null;

    /**
     * It sets Budget Publication Number Code
     *
     * @param lStrBPNCode Budget Publication Number Code
     */
    public void setBPNCode(String lStrBPNCode)
    {
        this.lStrBPNCode = lStrBPNCode;
    }

    /**
     * It returns Budget Publication Number Code
     *
     * @return Budget Publication Number Code
     */
    public String getBPNCode()
    {
        return lStrBPNCode;
    }

    /**
     * It sets Budget Publication Number Description 
     *
     * @param lStrBPNDesc Budget Publication Number Description
     */
    public void setBPNDescription(String lStrBPNDesc)
    {
        this.lStrBPNDesc = lStrBPNDesc;
    }

    /**
     * It returns Budget Publication number Description  
     *
     * @return Budget Publication number Description
     */
    public String getBPNDescription()
    {
        return lStrBPNDesc;
    }

    //*****************************       Get And Set Method Start Here    *******************************

    /**
     * It sets Budget Publication Number ID 
     *
     * @param lIntBPNID Budget Publication Number ID
     */
    public void setBPNID(int lIntBPNID)
    {
        this.lIntBPNID = lIntBPNID;
    }

    /**
     * It returns the Budget Publication Number ID
     *
     * @return Budget Publication Number ID
     */
    public int getBPNID()
    {
        return lIntBPNID;
    }

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
     * It returns the Department ID
     *
     * @return Department ID
     */
    public String getDeptID()
    {
        return lStrDeptID;
    }
}
