
/*
   * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
   * filename        : BudExpEstShareVO.java
   * Author          : Ekta Kamdar
   * date            : 03/12/2005
   * @version        : 1.0

   * REV. HISTORY :
   *-----------------------------------------------------------------------------
   *-----------------------------------------------------------------------------
   */

package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;

public class BudExpEstShareVO extends BaseValueBean
{
  /**
    * Integer representing  Share ID
    */
     private int lIntShareID=0;  
  /**
    * String representing  Share Code
    */
     private String lStrShareCode = null;
  /**
    * String representing  Share Short Description
    */
     private String lStrShareShrtDesc = null;
  /**
    * String representing  Share Long Description
    */
     private String lStrShareLngDesc = null;


//************************ get and set methods are start from here *********************************     

/**
    * This method is to set Share ID
    * method setShareID
    * @param lIntShareID
    */
    public void setShareID(int lIntShareID)
    {
      this.lIntShareID = lIntShareID;
    }
    /**
    * This method is to get Share ID
    * method  getShareID
    * @return Integer
    */
    public int getShareID()
    {
      return lIntShareID;
    }
    
  /**
    * This method is to set Share Code
    * method setShareCode
    * @param lStrShareCode
    */
    public void setShareCode(String lStrShareCode)
    {
      this.lStrShareCode = lStrShareCode;
    }
    /**
    * This method is to get Share Code
    * method  getShareCode
    * @return String
    */
    public String getShareCode()
    {
      return lStrShareCode;
    }    
    
  /**
    * This method is to set Share Short Description
    * method setShareShrtDesc
    * @param lStrShareShrtDesc
    */
    public void setShareShrtDesc(String lStrShareShrtDesc)
    {
      this.lStrShareShrtDesc = lStrShareShrtDesc;
    }
    /**
    * This method is to get Share Description
    * method  getShareShrtDesc
    * @return String
    */
    public String getShareDesc()
    {
      return lStrShareShrtDesc;
    }        


  /**
    * This method is to set Share Long Description
    * method setShareLngDesc
    * @param lStrShareLngDesc
    */
    public void setShareLngDesc(String lStrShareLngDesc)
    {
      this.lStrShareLngDesc = lStrShareLngDesc;
    }
    /**
    * This method is to get Share Description
    * method  getShareLngDesc
    * @return String
    */
    public String getShareLngDesc()
    {
      return lStrShareLngDesc;
    }        
}