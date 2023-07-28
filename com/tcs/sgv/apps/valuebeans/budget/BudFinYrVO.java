/*
   * PACKAGE         : com.tcs.sgv.apps.valuebeans.budget
   * filename        : BudFinYrVO.java
   * @version        : 1.0
   * @author         : Payal Patel
   * date            : 17/05/2005

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
public class BudFinYrVO extends BaseValueBean
{
    /**
    * Integer representing  Financial Year ID
    */ 
    private int iFinYrID = 0;
    /**
    * String representing  Financial Year Description
    */     
    private String strFinYrDesc = null;
    /**
    * String representing  Financial Year Code
    */ 
    private String strFinYrCode = null;


    /**
    * This method is to set Financial YearID
    * method setFinYrID
    * @param liFinYrID
    */
    public void setFinYrID(int liFinYrID)
    {
      this.iFinYrID = liFinYrID;
    }
    /**
    * This method is to get Financial YearID
    * method  getFinYrID
    * @return Integer
    */
    public int getFinYrID()
    {
      return iFinYrID;
    }

  /**
    * This method is to set the Financial  Year Description
    * method  setFinYrDesc
    * @param lStrFinYrDesc
    * 
    */
    public void setFinYrDesc(String lStrFinYrDesc)
    {
      this.strFinYrDesc = lStrFinYrDesc;
    }
  /**
    * This method is to get the Financial  Year Description
    * method     getFinYrDesc
    * @return   String
    */
    public String getFinYrDesc()
    {
      return strFinYrDesc;
    }

   /**
    * This method is to set the Financial  Year Code
    * method  setFinYrCode
    * @param lStrFinYrCode
    * 
    */
    public void setFinYrCode(String lStrFinYrCode)
    {
      this.strFinYrCode = lStrFinYrCode;
    }
  /**
    * This method is to get the Financial  Year Code
    * method     getFinYrCode
    * @return   String
    */
    public String getFinYrCode()
    {
      return strFinYrCode;
    }
//********************* Get All the Methods Name in VO ***************************
/*
 public void discribeVO(MyVO mv)
  {
        Method[] mtd =  mv.getClass().getDeclaredMethods();
        String strName=null;
        //System.out.println(mtd.length);
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
  }*/
}