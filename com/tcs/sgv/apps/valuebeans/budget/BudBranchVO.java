/*
 * PACKAGE            : com.tcs.sgv.apps.valuebeans.budget
 * filename           : BudBranchVO.java
 * @version           : 1.0
 * @author            : Payal Patel
 * Date               : 23/11/2005
 * Description        : This is the class of BudBranchVO
 *-----------------------------------------------------------------------------
 */
package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;


/**
 * This class Contains all the Get and Set methods of Receipt Header and details
 *
 * @author
 * @version 1.0
 */
public class BudBranchVO extends BaseValueBean
{
    /** String representing Branch ID */
    private String strBranchID = null;

    /** String representing Branch Name */
    private String strBranchName = null;

    /** String representing Branch Type */
    private String lStrBranchType = null;   // Added By Keyur

    //*****************************       Get And Set Method Start Here    *******************************

    /**
     * This method is to set the Branch ID method     setBranchID
     *
     * @param lStrBranchID
     */
    public void setBranchID(String lStrBranchID)
    {
        this.strBranchID = lStrBranchID;
    }

    /**
     * This method is to get the Branch ID method    getBranchID
     *
     * @return String
     */
    public String getBranchID()
    {
        return strBranchID;
    }

    /**
     * This method is to set Branch Name method  setBranchName
     *
     * @param lStrBranchName
     */
    public void setBranchName(String lStrBranchName)
    {
        this.strBranchName = lStrBranchName;
    }

    /**
     * This method is to get Branch Name method  getBranchName
     *
     * @return String
     */
    public String getBranchName()
    {
        return strBranchName;
    }

    /**
     * This method is to set Branch Type
     *
     * @param lStrBranchType
     */
    public void setBranchType(String lStrBranchType)
    {
        this.lStrBranchType = lStrBranchType;
    }

    /**
     * This method is to get Branch Type
     *
     * @return String
     */
    public String getBranchType()
    {
        return lStrBranchType;
    }
}
