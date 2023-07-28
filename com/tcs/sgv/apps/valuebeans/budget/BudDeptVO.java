/** 
* Package     : com.tcs.sgv.apps.valuebeans.budget
* Filename    : BudDeptVO.java
* @VERSION    : 1.0
* @AUTHOR     : Payal Patel
* Date        : 10/12/2005   
*/
package com.tcs.sgv.apps.valuebeans.budget;
import com.tcs.sgv.common.valuebeans.BaseValueBean;

public class BudDeptVO extends BaseValueBean
{
     /**
    * String representing Department ID
    */
    private String strDeptID = null;
    /**
    * String representing Department Name
    */
     private String strDeptName = null;
   
   
//*****************************       Get And Set Method Start Here    *******************************
  /**
    * This method is to set the Department ID
    * method     setDeptID 
    * @param lStrDeptID 
    */
    public void setDeptID (String lStrDeptID )
    {
        strDeptID  = lStrDeptID ;
    }
  /**
    * This method is to get the Department ID
    * method    getDeptID 
    * @return   String
    */
    public String getDeptID ()
    {
      return strDeptID ;
    }

/**
    * This method is to set Department Name
    * method  setDeptName
    * @param lStrDeptName
    */
    public void setDeptName(String lStrDeptName) 
    {
         strDeptName= lStrDeptName;
    }
  /**
    * This method is to get Department Name
    * method  getDeptName
    * @return  String
    */
    public String getDeptName() 
    {
      return strDeptName; 
    }

}