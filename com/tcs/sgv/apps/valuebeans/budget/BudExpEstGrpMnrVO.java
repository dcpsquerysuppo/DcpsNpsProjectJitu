/*
 * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
 * filename        : ExpEstGrpMnrVO.java
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
public class BudExpEstGrpMnrVO extends BaseValueBean
{
    /** Integer representing  Expenditure Estimation Group Minor ID */
    private int lIntGrpMnrID = 0;

    /** Integer representing  Expenditure Estimation Group Minor Code */
    private String lStrGrpMnrCode = null;

    /** String representing  Group Minor Long Description */
    private String lStrGrpMnrLngDesc = null;

    /** String representing  Group Minor Short Description */
    private String lStrGrpMnrShrtDesc = null;

    /** String representing  Created userID */
    private String lStrCrtUsrID = null;

    /** String representing  Language Id */
    private String lStrLangId = null;

    /** String representing  Location Id */
    private String lStrLocId = null;

    /**
     * It sets Group Minor Code Value
     *
     * @param lStrGrpMnrCode Group Minor Code Value
     */
    public void setGrpMnrCode(String lStrGrpMnrCode)
    {
        this.lStrGrpMnrCode = lStrGrpMnrCode;
    }

    /**
     * It returns Group Minor Code Value 
     *
     * @return Group Minor Code Value
     */
    public String getGrpMnrCode()
    {
        return lStrGrpMnrCode;
    }

    //*****************************   Get And Set Method Start Here    *******************************

    /**
     * It sets Expenditure Estimation Group Minor ID 
     *
     * @param lIntGrpMnrID Group Minor ID
     */
    public void setGrpMnrID(int lIntGrpMnrID)
    {
        this.lIntGrpMnrID = lIntGrpMnrID;
    }

    /**
     * It returns Expenditure Estimation Group Minor ID 
     *
     * @return Group Minor ID
     */
    public int getGrpMnrID()
    {
        return lIntGrpMnrID;
    }

    /**
     * It sets Group Minor Long Description Value
     *
     * @param lStrGrpMnrLngDesc Group Minor Long Description
     */
    public void setGrpMnrLngDesc(String lStrGrpMnrLngDesc)
    {
        this.lStrGrpMnrLngDesc = lStrGrpMnrLngDesc;
    }

    /**
     * It returns Group Minor Long Description Value 
     *
     * @return Group Minor Long Description
     */
    public String getGrpMnrLngDesc()
    {
        return lStrGrpMnrLngDesc;
    }

    /**
     * It sets Group Minor Short Description Value
     *
     * @param lStrGrpMnrShrtDesc Group Minor Short Description
     */
    public void setGrpMnrShrtDesc(String lStrGrpMnrShrtDesc)
    {
        this.lStrGrpMnrShrtDesc = lStrGrpMnrShrtDesc;
    }

    /**
     * It returns Group Minor Short Description Value
     *
     * @return Group Minor Short Description
     */
    public String getGrpMnrShrtDesc()
    {
        return lStrGrpMnrShrtDesc;
    }

    /**
     * It sets Language Id
     *
     * @param lStrLangId Language Id
     */
    public void setLangId(String lStrLangId)
    {
        this.lStrLangId = lStrLangId;
    }

    /**
     * It returns Language Id 
     *
     * @return Language Id
     */
    public String getLangId()
    {
        return lStrLangId;
    }

    /**
     * It sets Location Id 
     *
     * @param lLocId Location Id
     */
    public void setLocId(String lLocId)
    {
        this.lStrLocId = lStrLocId;
    }

    /**
     * It returns Location Id
     *
     * @return Location Id
     */
    public String getLocId()
    {
        return lStrLocId;
    }

    //*****************************   Get And Set Method Ends Here    *******************************

    /*
       public void discribeVO(MyVO mv)
        {
              Method[] mtd =  mv.getClass().getDeclaredMethods();
              String StrName=null;
              try
              {
                for(int i=0; i< mtd.length; i++)
                {
                  StrName=mtd[i].getName();
                  if(StrName.subString(0,3).equals("get"))
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
                StrName = null;
              }
        }
     */
}
