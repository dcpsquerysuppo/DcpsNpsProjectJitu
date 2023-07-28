/*
 * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
 * filename        : ExpEstMnrHdVO.java
 * Author          : Keyur Shah
 * date            : 03/12/2005
 * @version        : 1.0
 * REV. HISTORY :
 *-----------------------------------------------------------------------------
 *-----------------------------------------------------------------------------
 */
package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;


/**
 * This class Conatins all the Get and Set methods
 *
 * @version 1.0
 */
public class BudExpEstMnrHdVO extends BaseValueBean
{
    /** Integer representing  Expenditure Estimation Minor Head ID */
    private int lIntExpEstMnrHdID = 0;

    /** Integer representing  Expenditure Estimation Minor Head Code */
    private String lstrExpEstMnrHdCode = null;

    /** String representing  Minor Head Long Description */
    private String lstrMnrHdLngDesc = null;

    /** String representing  Minor Head Short Description */
    private String lstrMnrHdShrtDesc = null;

    /** String representing  Created userID */
    private String lstrCrtUsrID = null;

    /** String representing  Language ID */
    private String lstrLangId = null;

    /** String representing  Location ID */
    private String lstrLocId = null;

    /**
     * It sets Language Id
     *
     * @param lstrLangId Language Id
     */
    public void setLangId(String lstrLangId)
    {
        this.lstrLangId = lstrLangId;
    }

    /**
     *It returns Language Id
     *
     * @return Language Id
     */
    public String getLangId()
    {
        return lstrLangId;
    }

    /**
     * It sets Location Id 
     *
     * @param lLocId Location Id
     */
    public void setLocId(String lLocId)
    {
        this.lstrLocId = lstrLocId;
    }

    /**
     * It returns Location Id 
     *
     * @return Location Id
     */
    public String getLocId()
    {
        return lstrLocId;
    }

    /**
     * It sets Minor Head Code Value
     *
     * @param lstrExpEstMnrHdCode Minor Head Code
     */
    public void setMnrHdCode(String lstrExpEstMnrHdCode)
    {
        this.lstrExpEstMnrHdCode = lstrExpEstMnrHdCode;
    }

    /**
     * It returns Minor Head Code Value
     *
     * @return Minor Head Code
     */
    public String getMnrHdCode()
    {
        return lstrExpEstMnrHdCode;
    }

    //*****************************   Get And Set Method Start Here    *******************************

    /**
     * It sets Expenditure Estimation Minor Head Id 
     *
     * @param lIntExpEstMnrHdID Expenditure Estimation Minor Head Id
     */
    public void setMnrHdID(int lIntExpEstMnrHdID)
    {
        this.lIntExpEstMnrHdID = lIntExpEstMnrHdID;
    }

    /**
     * It returns Expenditure Estimation Minor Head Id
     *
     * @return Expenditure Estimation Minor Head Id
     */
    public int getMnrHdID()
    {
        return lIntExpEstMnrHdID;
    }

    /**
     * It sets Minor Head Long Description Value
     *
     * @param lstrMnrHdLngDesc Minor Head Long Description 
     */
    public void setMnrHdLngDesc(String lstrMnrHdLngDesc)
    {
        this.lstrMnrHdLngDesc = lstrMnrHdLngDesc;
    }

    /**
     * It returns Minor Head Long Description Value
     *
     * @return Minor Head Long Description
     */
    public String getMnrHdLngDesc()
    {
        return lstrMnrHdLngDesc;
    }

    /**
     * It sets Minor Head Short Description Value 
     *
     * @param lstrMnrHdShrtDesc Minor Head Short Description
     */
    public void setMnrHdShrtDesc(String lstrMnrHdShrtDesc)
    {
        this.lstrMnrHdShrtDesc = lstrMnrHdShrtDesc;
    }

    /**
     * It returns Minor Head Short Description Value 
     *
     * @return Minor Head Short Description
     */
    public String getMnrHdShrtDesc()
    {
        return lstrMnrHdShrtDesc;
    }

    //*****************************   Get And Set Method Ends Here    *******************************

    /*
       public void discribeVO(MyVO mv)
        {
              Method[] mtd =  mv.getClass().getDeclaredMethods();
              String strName=null;
              try
              {
                for(int i=0; i< mtd.length; i++)
                {
                  strName=mtd[i].getName();
                  if(strName.substring(0,3).equals("get"))
                  {
                    System.out.println(mtd[i].getName() + " : " + mtd[i].invoke(mv,null));
                  }
                }
              }catch(Exception e)
              {
                 e.printStackTrace();
              }
              finally
              {
                mtd = null;
                strName = null;
              }
        }
     */
}
