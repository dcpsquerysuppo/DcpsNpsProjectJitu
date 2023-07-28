package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;


/**
 * [TODO add class documentation here]
 *
 * @author $author$
 * @version $Revision$
 */
public class BudObjHdVO extends BaseValueBean
{
    /** String representing Languages */
    private String strLangId = null;

    /** [TODO Add field documentation here] */
    private String strLangName = null;

    //Ronak on 29/04/2006

    /** [TODO Add field documentation here] */
    private String strObjHdCode = null;

    /** [TODO Add field documentation here] */
    private String strObjHdShrtDesc = null;

    /** [TODO Add field documentation here] */
    private String lStrObjHdType = null; // Added by keyur

    //*****************************       Get And Set Method Start Here    *******************************

    /**
     * This method is to set the Languages Id method  setLang
     *
     * @param lStrLangId
     */
    public void setLangId(String lStrLangId)
    {
        this.strLangId = lStrLangId;
    }

    /**
     * This method is to get the  Languages Id method     getLang
     *
     * @return String
     */
    public String getLangId()
    {
        return strLangId;
    }

    /**
     * This method is to set the Languages  method  setLangName
     *
     * @param lStrLangName
     */
    public void setLangName(String lStrLangName)
    {
        this.strLangName = lStrLangName;
    }

    /**
     * This method is to get the  Languages method     getLangName
     *
     * @return String
     */
    public String getLangName()
    {
        return strLangName;
    }

    /**
     * This method is to set the Object Head Code  method  setObjHdCode
     *
     * @param lStrObjHdCode
     */
    public void setObjHdCode(String lStrObjHdCode)
    {
        this.strObjHdCode = lStrObjHdCode;
    }

    /**
     * This method is to get the  Object Head Code  method     getObjHdCode
     *
     * @return String
     */
    public String getObjHdCode()
    {
        return strObjHdCode;
    }

    /**
     * This method is to set the Object Head Short Desc  method  setObjHdShrtDesc
     *
     * @param lStrObjHdShrtDesc
     */
    public void setObjHdShrtDesc(String lStrObjHdShrtDesc)
    {
        this.strObjHdShrtDesc = lStrObjHdShrtDesc;
    }

    /**
     * This method is to get the  Object Head Short Desc  method     getObjHdShrtDesc
     *
     * @return String
     */
    public String getObjHdShrtDesc()
    {
        return strObjHdShrtDesc;
    }

    /**
     * This method is to set the Object Head Type method  setObjHdType
     *
     * @param lStrObjHdType
     */
    public void setObjHdType(String lStrObjHdType)
    {
        this.lStrObjHdType = lStrObjHdType;
    }

    /**
     * This method is to get the  Object Head Type method     getObjHdType
     *
     * @return String
     */
    public String getObjHdType()
    {
        return lStrObjHdType;
    }
}
