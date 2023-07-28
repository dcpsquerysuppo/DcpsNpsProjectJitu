package com.tcs.sgv.apps.valuebeans.budget;

import com.tcs.sgv.common.valuebeans.BaseValueBean;

public class BudMjrHdVO extends BaseValueBean
{
  
    /**
    * Integer representing  Major Head ID
    */
     private int iMjrHdID = 0;
    /**
    * String representing  Major Head Code
    */
     private String strMjrHdCode = null;
     /**
    * String representing  Major Head Long Description
    */
     private String strMjrHdLngDesc = null;
     /**
    * String representing  Major Head Short Description
    */
     private String strMjrHdShrtDesc = null;
     /**
    * String representing  Major Head Type
    */
     private String strMjrHdType = null;
     /**
    * String representing  Major Head Sub Type
    */
     private String strMjrHdSubType = null;
   /**
    * Integer representing  Demand ID
    */
     private int iDmdId = 0;
     /**
    * Integer representing  Sector ID
    */
     private int iSctrId = 0;
     /**
    * Integer representing  Sub Sector ID
    */
     private int iSubSctrId = 0;
     /**
    * Integer representing  Sub Sub Sector ID
    */
     private int iSubSubSctrId = 0;


    /**
     * String representing Created UserID  
    */
     private String strCrtUsrID = null;

    /**
     * Integer representing Terminated YearID
    */
     private int iTrYrID = 0;



//*****************************       Get And Set Method Start Here    *******************************

 /**
    * This method is to set the MajorHead ID 
    * method     setMjrHdId
    * @param liMjrHdId
    */
    public void setMjrHdID(int liMjrHdID)
    {
      this.iMjrHdID = liMjrHdID;
    }
  /**
    * This method is to get the MajorHead ID
    * method     getMjrHdId
    * @return   integer
    */
    public int getMjrHdID()
    {
      return iMjrHdID;
    }


 /**
    * This method is to set the MajorHead Code
    * method     setMjrHdCode
    * @param lStrMjrHdCode
    */
    public void setMjrHdCode(String lStrMjrHdCode)
    {
      this.strMjrHdCode  = lStrMjrHdCode;
    }
  /**
    * This method is to get the MajorHead Code
    * method     getMjrHdCode
    * @return   String
    */
    public String getMjrHdCode()
    {
      return strMjrHdCode;
    }



/**
    * This method is to set the MajorHead  Short Description 
    * method     setMjrHdIndfr
    * @param lstrMjrHdIndfr
    */
    public void setMjrHdShortDesc  (String lstrMjrHdShrtDesc)
    {
      this.strMjrHdShrtDesc = lstrMjrHdShrtDesc;
    }
  /**
    * This method is to get the MajorHead  Short Description 
    * method     getstrMjrHdShrtDesc 
    * @return   String
    */
    public String getMjrHdShortDesc()
    {
      return strMjrHdShrtDesc;
    }


/**
    * This method is to set the MajorHead  Long Description 
    * method     setMjrHdLngDesc
    * @param lstrMjrHdLngDesc
    */
    public void setMjrHdLongDesc(String lstrMjrHdLngDesc)
    {
      this.strMjrHdLngDesc = lstrMjrHdLngDesc;
    }
  /**
    * This method is to get the MajorHead  Long Description
    * method     getMjrHdIndfr
    * @return   String
    */
    public String getMjrHdLongDesc()
    {
      return strMjrHdLngDesc; 
    }


 /**
    * This method is to set the MajorHead Type
    * method     setMjrHdType
    * @param lStrMjrHdType
    */
    public void setMjrHdType(String lStrMjrHdType)
    {
      this.strMjrHdType  = lStrMjrHdType;
    }
  /**
    * This method is to get the MajorHead Type
    * method     getMjrHdType
    * @return   String
    */
    public String getMjrHdType()
    {
      return strMjrHdType;
    }


/**
    * This method is to set the  MajorHead Sub Type
    * method     setMjrHdSubType
    * @param lStrMjrHdSubType
    */
    public void setMjrHdSubType(String lStrMjrHdSubType)
    {
      this.strMjrHdSubType  = lStrMjrHdSubType;
    }
  /**
    * This method is to get the MajorHead Sub Type
    * method     getMjrHdSubType
    * @return   String
    */
    public String getMjrHdSubType()
    {
      return strMjrHdSubType;
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
/*
   
 * This method is to set the Sector ID 
    * method     setSctrId
    * @param liSctrId
    */
    public void setSctrId(int liSctrId)
    {
      this.iSctrId = liSctrId;
    }
  /**
    * This method is to get the Sector ID
    * method     getSctrId
    * @return   integer
    */
    public int getSctrId()
    {
      return iSctrId;
    }
/**
    * This method is to set the Sub Sector ID 
    * method     setSubSctrId
    * @param liSubSctrId
    */
    public void setSubSctrId(int liSubSctrId)
    {
      this.iSubSctrId = liSubSctrId;
    }
  /**
    * This method is to get the Sub Sector ID
    * method     getSubSctrId
    * @return   integer
    */
    public int getSubSctrId()
    {
      return iSubSctrId;
    }
/**
    * This method is to set the Sub Sub Sector ID 
    * method     setSbSbSctrId
    * @param liSbSbSctrId
    */
    public void setSubSubSctrId(int liSubSubSctrId)
    {
      this.iSubSubSctrId = liSubSubSctrId;
    }
  /**
    * This method is to get the Sub Sub Sector ID
    * method     getSubSubSctrId
    * @return   integer
    */
    public int getSubSubSctrId()
    {
      return iSubSubSctrId;
    }

/**
    * This method is to set the Created UserID of Sector
    * method     setCrtUsrID
    * @param lstrCrtUsrID
    */
    public void setCrtUsrID(String lstrCrtUsrID)
    {
      this.strCrtUsrID = lstrCrtUsrID;
    }
  /**
    * This method is to get the Created UserID of sector
    * method     getCrtUsrID
    * @return   String
    */
    public String getCrtUsrID()
    {
      return strCrtUsrID; 
    }

/**
    * This method is to set the Sector Termination Year ID
    * method     setTrYrID
    * @param liTrYrID
    */
    public void setTrYrID (int liTrYrID)
    {
      this.iTrYrID = liTrYrID;
    }
  /**
    * This method is to get the Sector Termination Year ID
    * method     getTrYrID
    * @return   Integer
    */
    public int getTrYrID()
    {
      return iTrYrID;
    }


}



