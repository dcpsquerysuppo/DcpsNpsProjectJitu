/*
 * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
 * filename        : BudExpEstSbHdVO.java
 * Author          : Keyur Shah
 * date            : 05/12/2005
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
public class BudExpEstSbHdVO extends BaseValueBean
{
    /** Integer representing  Expenditure Estimation Sub Head ID */
    private long lIntExpEstSbHdID = 0;

    /** Integer representing  Expenditure Estimation Sub Head Code */
    private String lStrExpEstSbHdCode = null;

    /** String representing  Sub Head Long Description */
    private String lStrSbHdLngDesc = null;

    /** String representing  Sub Head Short Description */
    private String lStrSbHdShrtDesc = null;

    /** String representing  Created userID */
    private String lStrCrtUsrID = null;

    /** String representing  Language ID */
    private String lStrLangId = null;

    /** String representing  Location ID */
    private String lStrLocId = null;

    /** String representing  EDP Code */
    private String lStrEDPCode = null;

    /** String representing Planning Code */
    private String lStrPlanningCode = null;
  String lStrCSS="";
  String lStrCSSDesc="";

    /**
     * It sets EDP Code
     *
     * @param lStrEDPCode EDP Code
     */
    public void setEDPCode(String lStrEDPCode)
    {
        this.lStrEDPCode = lStrEDPCode;
    }

    /**
     * It returns EDP Code
     *
     * @return EDP Code
     */
    public String getEDPCode()
    {
        return lStrEDPCode;
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

    /**
     * It sets Planning Code
     *
     * @param lStrPlanningCode Planning Code
     */
    public void setPlanningCode(String lStrPlanningCode)
    {
        this.lStrPlanningCode = lStrPlanningCode;
    }

    /**
     * It returns Planning Code
     *
     * @return Planning Code
     */
    public String getPlanningCode()
    {
        return lStrPlanningCode;
    }

    /**
     * It sets Sub Head Code Value
     *
     * @param lStrExpEstSbHdCode Sub Head Code
     */
    public void setSbHdCode(String lStrExpEstSbHdCode)
    {
        this.lStrExpEstSbHdCode = lStrExpEstSbHdCode;
    }

    /**
     * It returns Sub Head Code Value
     *
     * @return Sub Head Code
     */
    public String getSbHdCode()
    {
        return lStrExpEstSbHdCode;
    }

    //*****************************   Get And Set Method Start Here    *******************************

    /**
     * It sets Expenditure Estimation Sub Head ID
     *
     * @param lIntExpEstSbHdID Expenditure Estimation Sub Head ID
     */
    public void setSbHdID(long lIntExpEstSbHdID)
    {
        this.lIntExpEstSbHdID = lIntExpEstSbHdID;
    }

    /**
     * It returns Expenditure Estimation Sub Head ID
     *
     * @return Expenditure Estimation Sub Head ID
     */
    public long getSbHdID()
    {
        return lIntExpEstSbHdID;
    }

    /**
     * It sets Sub Head Long Description Value
     *
     * @param lStrSbHdLngDesc Sub Head Long Description
     */
    public void setSbHdLngDesc(String lStrSbHdLngDesc)
    {
        this.lStrSbHdLngDesc = lStrSbHdLngDesc;
    }

    /**
     * It returns Sub Head Long Description Value
     *
     * @return Sub Head Long Description
     */
    public String getSbHdLngDesc()
    {
        return lStrSbHdLngDesc;
    }

    /**
     * It sets Sub Head Short Description Value
     *
     * @param lStrSbHdShrtDesc Sub Head Short Description
     */
    public void setSbHdShrtDesc(String lStrSbHdShrtDesc)
    {
        this.lStrSbHdShrtDesc = lStrSbHdShrtDesc;
    }

    /**
     * It returns Sub Head Short Description Value
     *
     * @return Sub Head Short Description
     */
    public String getSbHdShrtDesc()
    {
        return lStrSbHdShrtDesc;
    }

  public String isCSS()
  {
    return lStrCSS;
  }

  public void setCSS(String newLStrCSS)
  {
    lStrCSS = newLStrCSS;
  }

  public String getCSSDesc()
  {
    return lStrCSSDesc;
  }

  public void setCSSDesc(String newLStrCSSDesc)
  {
    lStrCSSDesc = newLStrCSSDesc;
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
