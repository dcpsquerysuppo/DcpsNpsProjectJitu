/*
 * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
 * filename        : BUDExpEstOfficeVO.java
 * Author          : Keyur Shah
 * date            : 03/12/2005
 * @version        : 1.0
 * REV. HISTORY :
 *-----------------------------------------------------------------------------
 *-----------------------------------------------------------------------------
 */

package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;

public class BudExpEstOfficeVO extends BaseValueBean
{
    /** Integer representing  Office ID */
    private int lIntOfficeID = 0;

    /** String representing  Office Code */
    private String lStrOfficeCode = null;

    /** String representing  Office Short Description */
    private String lStrOfficeShrtDesc = null;

    /** String representing  Office Long Description */
    private String lStrOfficeLngDesc = null;

    /**
     * It sets Office Code 
     *
     * @param lStrOfficeCode Office Code
     */
    public void setOfficeCode(String lStrOfficeCode)
    {
        this.lStrOfficeCode = lStrOfficeCode;
    }

    /**
     * It returns Office Code 
     *
     * @return Office Code
     */
    public String getOfficeCode()
    {
        return lStrOfficeCode;
    }

    /**
     * It returns Office Description 
     *
     * @return Office Description
     */
    public String getOfficeDesc()
    {
        return lStrOfficeShrtDesc;
    }

    //************************ get and set methods are start from here *********************************     

    /**
     * It sets Office ID 
     *
     * @param lIntOfficeID Office ID
     */
    public void setOfficeID(int lIntOfficeID)
    {
        this.lIntOfficeID = lIntOfficeID;
    }

    /**
     * It returns Office ID 
     *
     * @return Office ID
     */
    public int getOfficeID()
    {
        return lIntOfficeID;
    }

    /**
     * It sets Office Long Description
     *
     * @param lStrOfficeLngDesc Office Long Description
     */
    public void setOfficeLngDesc(String lStrOfficeLngDesc)
    {
        this.lStrOfficeLngDesc = lStrOfficeLngDesc;
    }

    /**
     * It returns Office Description
     *
     * @return Office Long Description
     */
    public String getOfficeLngDesc()
    {
        return lStrOfficeLngDesc;
    }

    /**
     * It sets Office Short Description
     *
     * @param lStrOfficeShrtDesc Office Short Description
     */
    public void setOfficeShrtDesc(String lStrOfficeShrtDesc)
    {
        this.lStrOfficeShrtDesc = lStrOfficeShrtDesc;
    }
}
