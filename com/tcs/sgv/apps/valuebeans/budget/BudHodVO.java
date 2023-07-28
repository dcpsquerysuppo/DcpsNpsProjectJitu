/*
   * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
   * filename        : BudHodVO.java
   * @version        : 1.0
   * @author         : Payal Patel
   * date            : 18/05/2005

   * REV. HISTORY :
   *-----------------------------------------------------------------------------
   *-----------------------------------------------------------------------------
   */

package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;

/**
 * 
 * This class Conatins all the Get and Set methods
 * 
 * @author Payal Patel
 * @version 1.0
 *
 */

public class BudHodVO extends BaseValueBean
{
 
   /**
    * Integer representing  HoD ID
    */
     private int lIntHodID = 0;
    /**
    * String representing  Hod Description
    */
     private String lStrHodDesc = null;
    /**
    * String representing  Hod Short Description
    */
     private String lStrHodShrtDesc = null;
    /**
    * String representing  UnitID
    */
     private String lStrUnitID = null;
   /**
    * String representing  Created userID
    */
     private String lStrCrtUsrID = null;

   /**
     * Integer representing Terminated YearID
    */
     private int liTrYrID = 0;
//*****************************   Get And Set Method Start Here    *******************************

 /**
    * This method is to set the HodID
    * method     setHodID
    * @param lIntHodID
    */
    public void setHodID(int lIntHodID)
    {
      this.lIntHodID = lIntHodID;
    }
  /**
    * This method is to get the HodID
    * method     getHodID
    * @return   integer
    */
    public int getHodID()
    {
      return lIntHodID;
    }


 /**
    * This method is to set the Hod Description
    * method     setHodDesc
    * @param lStrHodDesc
    */
    public void setHodDesc(String lStrHodDesc)
    {
      this.lStrHodDesc  = lStrHodDesc;
    }
  /**
    * This method is to get the Hod Description
    * method     getHodDesc
    * @return   String
    */
    public String getHodDesc()
    {
      return lStrHodDesc;
    }


/**
    * This method is to set the Hod Short Description 
    * method     setHodShrtDesc
    * @param lStrHodShrtDesc
    */
    public void setHodShrtDesc(String lStrHodShrtDesc)
    {
      this.lStrHodShrtDesc = lStrHodShrtDesc;
    }
  /**
    * This method is to get the Hod Short Description
    * method     getHodShrtDesc 
    * @return   String
    */
    public String getHodShrtDesc()
    {
      return lStrHodShrtDesc;
    }


  /**
    * This method is to set the UnitID
    * method     setUnitID
    * @param liUnitID
    */
    public void setUnitID(String lStrUnitID)
    {
      this.lStrUnitID = lStrUnitID;
    }
  /**
    * This method is to get the UnitID
    * method     getUnitID
    * @return   Integer
    */
    public String getUnitID()
    {
      return  lStrUnitID; 
    }

/**
    * This method is to set the Created UserID
    * method setCrtUsrID
    * @param lStrCrtUsrID
    */
    public void setCrtUsrID(String lStrCrtUsrID)
    {
      this.lStrCrtUsrID = lStrCrtUsrID;
    }
  /**
    * This method is to get the Created UserID
    * method     getCrtUsrID
    * @return   String
    */
    public String getCrtUsrID()
    {
      return lStrCrtUsrID; 
    }
 
/**
    * This method is to set the  Termination Year ID
    * method     setTrYrID
    * @param liTrYrID
    */
    public void setTrYrID (int liTrYrID)
    {
      this.liTrYrID = liTrYrID;
    }
  /**
    * This method is to get the Termination Year ID
    * method     getTrYrID
    * @return   Integer
    */
    public int getTrYrID()
    {
      return liTrYrID;
    }
 
}