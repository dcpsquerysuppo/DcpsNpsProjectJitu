/*
 * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
 * filename        : BudExpEstGadVO.java
 * Author          : Keyur Shah
 * date            : 03/12/2005
 * @version        : 1.0
 * REV. HISTORY :
 *-----------------------------------------------------------------------------
 *-----------------------------------------------------------------------------
 */
package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;

public class BudExpEstGadVO extends BaseValueBean
{
    /** Integer representing  GAD ID */
    private int lIntGadID = 0;

    /** String representing  Gad Code */
    private String lStrGadCode = null;

    /** String representing  Gad Short Description */
    private String lStrGadShrtDesc = null;

    /** String representing  Gad Long Description */
    private String lStrGadLngDesc = null;

    /**
     * It sets Gad Code 
     *
     * @param lStrGadCode GAD Code
     */
    public void setGadCode(String lStrGadCode)
    {
        this.lStrGadCode = lStrGadCode;
    }

    /**
     * It returns Gad Code 
     *
     * @return GAD Code
     */
    public String getGadCode()
    {
        return lStrGadCode;
    }

    /**
     * It returns Gad Description 
     *
     * @return GAD Description
     */
    public String getGadDesc()
    {
        return lStrGadShrtDesc;
    }

    //************************ get and set methods are start from here *********************************     

    /**
     * It sets Gad ID 
     *
     * @param lIntGadID Gad ID
     */
    public void setGadID(int lIntGadID)
    {
        this.lIntGadID = lIntGadID;
    }

    /**
     * It returns Gad ID 
     *
     * @return Gad ID
     */
    public int getGadID()
    {
        return lIntGadID;
    }

    /**
     * It sets Gad Long Description 
     *
     * @param lStrGadLngDesc Gad Long Description
     */
    public void setGadLngDesc(String lStrGadLngDesc)
    {
        this.lStrGadLngDesc = lStrGadLngDesc;
    }

    /**
     * It returns Gad Long Description 
     *
     * @return Gad LOng Description
     */
    public String getGadLngDesc()
    {
        return lStrGadLngDesc;
    }

    /**
     * It sets Gad Short Description 
     *
     * @param lStrGadShrtDesc Gad Short Description
     */
    public void setGadShrtDesc(String lStrGadShrtDesc)
    {
        this.lStrGadShrtDesc = lStrGadShrtDesc;
    }
}
