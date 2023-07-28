package com.tcs.sgv.apps.valuebeans.budget;
import com.tcs.sgv.common.valuebeans.BaseValueBean;

public class BudSbMjrHdVO extends BaseValueBean
{
    /**
    * Integer representing  Sub Major Head ID
    */
     private int iSbMjrHdId = 0;
    /**
    * String representing  Sub Major Head Code
    */
     private String strSbMjrHdCode = null;
    /**
    * String representing  Sub Major Long Description
    */
     private String strSbMjrHdLngDesc = null;
    /**
    * String representing  Sub Major Short Description
    */
     private String strSbMjrHdShrtDesc = null;


     /**
    * Integer representing  Demand ID
    */
     private int iDmdId = 0;
    /**
    * Integer representing  Major Head ID
    */
     private int iMjrHdId = 0;
    /**

//*****************************       Get And Set Method Start Here    *******************************

 /**
    * This method is to set the Sub MajorHead ID 
    * method     setSbMjrHdId
    * @param liSbMjrHdId
    */
    public void setSbMjrHdId(int liSbMjrHdId)
    {
      this.iSbMjrHdId = liSbMjrHdId;
    }
  /**
    * This method is to get the SubMajorHead ID
    * method     getSbMjrHdId
    * @return   integer
    */
    public int getSbMjrHdId()
    {
      return iSbMjrHdId;
    }


 /**
    * This method is to set the Sub MajorHead Code
    * method     setSbMjrHdCode
    * @param lStrSbMjrHdCode
    */
    public void setSbMjrHdCode(String lStrSbMjrHdCode)
    {
      this.strSbMjrHdCode  = lStrSbMjrHdCode;
    }
  /**
    * This method is to get the Sub MajorHead Code
    * method     getSbMjrHdCode
    * @return   String
    */
    public String getSbMjrHdCode()
    {
      return strSbMjrHdCode;
    }



/**
    * This method is to set the Sub MajorHead  Short Description 
    * method     setSbMjrHdIndfr
    * @param lstrSbMjrHdIndfr
    */
    public void setSbMjrHdShrtDesc  (String lstrSbMjrHdShrtDesc)
    {
      this.strSbMjrHdShrtDesc = lstrSbMjrHdShrtDesc;
    }
  /**
    * This method is to get the Sub MajorHead  Short Description 
    * method     getstrSbMjrHdShrtDesc 
    * @return   String
    */
    public String getSbMjrHdShrtDesc()
    {
      return strSbMjrHdShrtDesc;
    }


/**
    * This method is to set the Sub MajorHead Long Description 
    * method     setSbMjrHdLngDesc
    * @param lstrSbMjrHdLngDesc
    */
    public void setSbMjrHdLngDesc(String lstrSbMjrHdLngDesc)
    {
      this.strSbMjrHdLngDesc = lstrSbMjrHdLngDesc;
    }
  /**
    * This method is to get the Sub MajorHead  Long Description
    * method     getSbMjrHdIndfr
    * @return   String
    */
    public String getSbMjrHdLngDesc()
    {
      return strSbMjrHdLngDesc; 
    }


   /**
    * This method is to set the Demand ID 
    * method     setDmdId
    * @param liDmdId
    */
    public void setDmdId(int liDmdId)
    {
      this.iDmdId = liDmdId;
    }
  /**
    * This method is to get the Demand ID
    * method     getDmdId
    * @return   integer
    */
    public int getDmdId()
    {
      return iDmdId;
    }

   
   /** This method is to set the MajorHead ID 
    * method     setMjrHdId
    * @param liMjrHdId
    */
    public void setMjrHdId(int liMjrHdId)
    {
      this.iMjrHdId = liMjrHdId;
    }
  /**
    * This method is to get the MajorHead ID
    * method     getMjrHdId
    * @return   integer
    */
    public int getMjrHdId()
    {
      return iMjrHdId;
    }

}