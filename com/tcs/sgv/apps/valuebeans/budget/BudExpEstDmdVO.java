/*
 * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
 * filename        : BudExpEstDmdVO.java
 * Author          : Keyur Shah
 * date            : 02/12/2005
 * @version        : 1.0
 * REV. HISTORY :
 *-----------------------------------------------------------------------------
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
public class BudExpEstDmdVO extends BaseValueBean
{
    /** Integer representing  Demand ID */
    private int lIntDemandID = 0;

    /** String representing  Demand Code */
    private String lStrDemandCode = null;

    /** String representing  Demand Description */
    private String lStrDemandDesc = null;

    /** String representing  BPN Code */
    private String lStrBPNCode = null;

    /**
     * It sets BPN Code 
     *
     * @param lStrBPNCode Demand Code
     */
    public void setBPNCode(String lStrBPNCode)
    {
        this.lStrBPNCode = lStrBPNCode;
    }

    /**
     * It returns BPN Code 
     *
     * @return BPN Code
     */
    public String getBPNCode()
    {
        return lStrBPNCode;
    }

    /**
     * It sets Demand Code 
     *
     * @param lStrDemandCode Demand Code
     */
    public void setDemandCode(String lStrDemandCode)
    {
        this.lStrDemandCode = lStrDemandCode;
    }

    /**
     * It returns Demand Code 
     *
     * @return Demand Code
     */
    public String getDemandCode()
    {
        return lStrDemandCode;
    }

    /**
     * It sets Demand Description 
     *
     * @param lStrDemandDesc Demand Description
     */
    public void setDemandDesc(String lStrDemandDesc)
    {
        this.lStrDemandDesc = lStrDemandDesc;
    }

    /**
     * It returns Demand Description 
     *
     * @return Demand Description
     */
    public String getDemandDesc()
    {
        return lStrDemandDesc;
    }

    //************************ get and set methods are start from here *********************************     

    /**
     * It sets Demand ID 
     *
     * @param lIntDemandID Demand Id
     */
    public void setDemandID(int lIntDemandID)
    {
        this.lIntDemandID = lIntDemandID;
    }

    /**
     * It returns Demand ID 
     *
     * @return Demand Id
     */
    public int getDemandID()
    {
        return lIntDemandID;
    }
}
