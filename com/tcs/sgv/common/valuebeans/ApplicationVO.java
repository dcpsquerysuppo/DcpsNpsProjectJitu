/** ==============================================================
* Package			  : com.tcs.sgv.common.valuebeans
* Filename			: ApplicationVO.java
* Purpose			  :
*==============================================================
* Date              Author              Version
*==============================================================
* 25th July 2005	    Abhishek				      1.0
*==============================================================
*
* Modification History
*==============================================================
* Date              Author              Changes
*==============================================================
*
*
*==============================================================
*/
//Package Declaration
package com.tcs.sgv.common.valuebeans;

//Imported BaseValueBean Class
import com.tcs.sgv.common.valuebeans.BaseValueBean;

// Imported Java Sql Classes
import java.sql.Timestamp;

//Imported Util Classes
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;


public class ApplicationVO extends BaseValueBean
{

	
  private String strLocId = "";
  private String strLangId = "";
  private String strCreatedDate = "";
  private String strCrtUsr = "";
  private String CrtPostId = "";
  private String UpdPostId = "";
  private Timestamp CrtDt = null;
  private String strLstUpdUsr = "";
  private Timestamp LstUpddt = null;

  private String lStrAclFlag = "";
  private int noOfRecordsProcessed = 0;
  private int noOfRecordsInsertedSuccessfully = 0;
  private int noOfInsertionFailed = 0;
  private ArrayList insertionFailedRecordArrayList = null;
  private String auditTrail = "";
  private String logicalDelete = "";
  private String actionVal = "";
  private String resultPage = "" ;
  private String screenId = "" ;
  private Hashtable HashValueList;
  private String queryType= "";
  private ArrayList resultValueList = null;
  private String nodeID = "" ;
  private Locale locale = null;
  private String insertionStatus ="";
	
  private byte[] lbSchmDataBytes = null; // added for attachment by Prachi Shah	
  private Hashtable customValueList;
  
  //Added by Kanan Vaidya for pagination  
  private boolean paginationRequired=false;
  private int pagnOrderIndex=1;
  private String pagnOrderType="ASC";
  private String pagnHrchyString="";
  private int pagnDataSize=0;
  private String pagnRemoveButtonStat="";
  private ArrayList pagnDataList=null;
  private String pagnReadStatus=null;
  private String pagnCheckBoxIndex="";
  private String pagnStartIndex="";
  private String pagnDetailsIndex="";
  private String pagnDeleteIndex="";
  private String pagnLinkValIndex="";
  private String pagnLinkIndex="";
  private String pagnRowsPerPage="";
  private String pagnPHYStatus=null;
  private String pagnImagePath="";
  private String pagnMsgCode="";
  
  //ends - Added by Kanan Vaidya for pagination  
  // Added by Prachi Shah for selectall option in pagination
  private boolean selectAllOption =false;
  //Added by Ravi R for viewing the content type
  private String viewExcel="";
  //Added by samir for Security changes on 27-Jan-2006
  private boolean secutiryCheckNotPassed;
  private String contentType;
  private String fileName;  
  //End of added by samir for Security changes on 27-Jan-2006
  // Added by Vidhya for setting file name and extension
  private String fileExt;
  // Added by Vidhya for setting all request parameters -- start
  private boolean containsRequest;
  private String response;
  private String openAttchment="No";
  // Added by Vidhya for setting all request parameters -- end
  // End of Vidhya for setting file name and extension
  public String IsEmpty(String str)
  {
    if(str==null || str.trim()=="")
    return "";
    else
    return str;
  }


  /**
    * This method is to set the LocId
    * @Name         setLocId
    * @param        strLocId as String
    **/
    public void setLocId(String strLocId)
    {
      this.strLocId = strLocId;
    }
  /**
    * This method is to get the LocId
    * @Name         getLocId
    * @return       String
    **/
    public String getLocId()
    {
      strLocId=IsEmpty( strLocId);    
      return strLocId;
    }


  /**
    * This method is to set the Language ID
    * @Name         setLangId
    * @param        strLangId as String
    **/
    public void setLangId(String strLangId)
    {
      this.strLangId = strLangId;
    }
  /**
    * This method is to get the Language ID
    * @Name         getLangId
    * @return       String
    **/
    public String getLangId()
    {
      strLocId=IsEmpty( strLocId);    
      return strLangId;
    }


  /**
    * This method is to set the column name
    * @Name         setCrtUsr
    * @param        strCrtUsr as String
    **/
    public void setCrtUsr(String strCrtUsr)
    {
      this.strCrtUsr = strCrtUsr;
    }
  /**
    * This method is to get the column name
    * @Name         getCrtUsr
    * @return       String
    **/
    public String getCrtUsr()
    {
      strCrtUsr=IsEmpty( strCrtUsr);    
      return strCrtUsr;
    }


  /**
    * This method is to set created date
    * @Name     setCrtDt
    * @param    Timestamp
    **/
    public void setCrtDt(Timestamp t)
    {
      CrtDt = t;
    }
  /**
    * This method is to get the created date
    * @Name      getCrtDt
    * @return    Timestamp
    **/
    public Timestamp getCreatedDt()
    {
      return CrtDt;
    }


  /**
    * This method is to set the column name
    * @Name         setLstUpdUsr
    * @param        strLstUpdUsr as String
    **/
    public void setLstUpdUsr(String strLstUpdUsr)
    {
      this.strLstUpdUsr = strLstUpdUsr;
    }
  /**
    * This method is to get the column name
    * @Name         getLstUpdUsr
    * @return       String
    **/
    public String getLstUpdUsr()
    {
      strLstUpdUsr=IsEmpty( strLstUpdUsr);    
      return strLstUpdUsr;
    }


  /**
    * This method is to set created date
    * @Name     setLstUpddt
    * @param    Timestamp
    **/
    public void setLstUpddt(Timestamp t)
    {
      LstUpddt = t;
    }
  /**
    * This method is to get the created date
    * @Name      getLstUpddt
    * @return    Timestamp
    **/
    public Timestamp getLstUpddt()
    {
      return LstUpddt;
    }

    /**
    * This method is to set the Created Date as a String
    * @Name         setCreatedDateStr
    * @param        lStrCrtDate as String
    **/
    public void setCreatedDateStr(String lStrCrtDate)
    {
      this.strCreatedDate = lStrCrtDate;
    }
  /**
    * This method is to get the Created Date as a String
    * @Name         getCreatedDateStr
    * @return       String
    **/
    public String getCreatedDateStr()
    {
      strCreatedDate=IsEmpty( strCreatedDate);    
      return strCreatedDate;
    }


  public int getNoOfRecordsProcessed()
  {
    return noOfRecordsProcessed;
  }

  public void setNoOfRecordsProcessed(int newNoOfRecordsProcessed)
  {
    noOfRecordsProcessed = newNoOfRecordsProcessed;
  }

  public int getNoOfRecordsInsertedSuccessfully()
  {
    return noOfRecordsInsertedSuccessfully;
  }

  public void setNoOfRecordsInsertedSuccessfully(int newNoOfRecordsInsertedSuccessfully)
  {
    noOfRecordsInsertedSuccessfully = newNoOfRecordsInsertedSuccessfully;
  }

  public int getNoOfInsertionFailed()
  {
    return noOfInsertionFailed;
  }

  public void setNoOfInsertionFailed(int newNoOfInsertionFailed)
  {
    noOfInsertionFailed = newNoOfInsertionFailed;
  }

  public ArrayList getInsertionFailedRecordArrayList()
  {
    return insertionFailedRecordArrayList;
  }

  public void setInsertionFailedRecordArrayList(ArrayList newInsertionFailedRecordArrayList)
  {
    insertionFailedRecordArrayList = newInsertionFailedRecordArrayList;
  }



  public String getAuditTrail()
  {
    return auditTrail;
  }

  public void setAuditTrail(String newAuditTrail)
  {
    auditTrail = newAuditTrail;
  }

  public String getLogicalDelete()
  {
    return logicalDelete;
  }

  public void setLogicalDelete(String newLogicalDelete)
  {
    logicalDelete = newLogicalDelete;
  }

  public String getActionVal()
  {
    actionVal=IsEmpty( actionVal);
    return actionVal;
  }

  public void setActionVal(String newActionVal)
  {
    actionVal = newActionVal;
  }

  public String getResultPage()
  {
    resultPage=IsEmpty( resultPage);
    return resultPage;
  }

  public void setResultPage(String newResultPage)
  {
    resultPage = newResultPage;
  }

  public String getScreenId()
  {
    screenId=IsEmpty( screenId);
    return screenId;
  }

  public void setScreenId(String newScreenId)
  {
    screenId = newScreenId;
  }

  public Hashtable getHashValueList()
  {
    return HashValueList;
  }

  public void setHashValueList(Hashtable newHashValueList)
  {
    HashValueList = newHashValueList;
  }

  public String getQueryType()
  {
    return queryType;
  }

  public void setQueryType(String newQueryType)
  {
    queryType = newQueryType;
  }

  public ArrayList getResultValueList()
  {
    return resultValueList;
  }

  public void setResultValueList(ArrayList newResultValueList)
  {
    resultValueList = newResultValueList;
  }

  public String getNodeID()
  {
    nodeID=IsEmpty( nodeID);
    return nodeID;
  }

  public void setNodeID(String newNodeID)
  {
    nodeID = newNodeID;
  }

  public Locale getLocale()
  {
    return locale;
  }

  public void setLocale(Locale newLocale)
  {
    locale = newLocale;
  }

  public String getInsertionStatus()
  {
    insertionStatus=IsEmpty( insertionStatus);
    return insertionStatus;
  }

  public void setInsertionStatus(String newInsertionStatus)
  {
    insertionStatus = newInsertionStatus;
  }

   public byte[] getLbSchmDataBytes()
  {
    return lbSchmDataBytes;
  }

  public void setLbSchmDataBytes(byte[] newLbSchmDataBytes)
  {
    lbSchmDataBytes = newLbSchmDataBytes;
  }

  public Hashtable getCustomValueList()
  {
    return customValueList;
  }

  public void setCustomValueList(Hashtable newCustomValueList)
  {
    customValueList = newCustomValueList;
  }

  public boolean isPaginationRequired()
  {
    return paginationRequired;
  }

  public void setPaginationRequired(boolean newPaginationRequired)
  {
    paginationRequired = newPaginationRequired;
  }





  public int getPagnOrderIndex()
  {
    return pagnOrderIndex;
  }

  public void setPagnOrderIndex(int newPagnOrderIndex)
  {
    pagnOrderIndex = newPagnOrderIndex;
  }

  public String getPagnOrderType()
  {
    return pagnOrderType;
  }

  public void setPagnOrderType(String newPagnOrderType)
  {
    pagnOrderType = newPagnOrderType;
  }
  public String getPagnHrchyString()
  {
    return pagnHrchyString;
  }

  public void setPagnHrchyString(String newPagnHrchyString)
  {
    pagnHrchyString = newPagnHrchyString;
  }

  public int getPagnDataSize()
  {
    return pagnDataSize;
  }

  public void setPagnDataSize(int newPagnDataSize)
  {
    pagnDataSize = newPagnDataSize;
  }

  public String getPagnRemoveButtonStat()
  {
    return pagnRemoveButtonStat;
  }

  public void setPagnRemoveButtonStat(String newPagnRemoveButtonStat)
  {
    pagnRemoveButtonStat = newPagnRemoveButtonStat;
  }

  public ArrayList getPagnDataList()
  {
    return pagnDataList;
  }

  public void setPagnDataList(ArrayList newPagnDataList)
  {
    pagnDataList = newPagnDataList;
  }

  public String getPagnReadStatus()
  {
    return pagnReadStatus;
  }

  public void setPagnReadStatus(String newPagnReadStatus)
  {
    pagnReadStatus = newPagnReadStatus;
  }

  public String getPagnCheckBoxIndex()
  {
    return pagnCheckBoxIndex;
  }

  public void setPagnCheckBoxIndex(String newPagnCheckBoxIndex)
  {
    pagnCheckBoxIndex = newPagnCheckBoxIndex;
  }

  public String getPagnStartIndex()
  {
    return pagnStartIndex;
  }

  public void setPagnStartIndex(String newPagnStartIndex)
  {
    pagnStartIndex = newPagnStartIndex;
  }

  public String getPagnDetailsIndex()
  {
    return pagnDetailsIndex;
  }

  public void setPagnDetailsIndex(String newPagnDetailsIndex)
  {
    pagnDetailsIndex = newPagnDetailsIndex;
  }

  public String getPagnDeleteIndex()
  {
    return pagnDeleteIndex;
  }

  public void setPagnDeleteIndex(String newPagnDeleteIndex)
  {
    pagnDeleteIndex = newPagnDeleteIndex;
  }

  public String getPagnLinkValIndex()
  {
    return pagnLinkValIndex;
  }

  public void setPagnLinkValIndex(String newPagnLinkValIndex)
  {
    pagnLinkValIndex = newPagnLinkValIndex;
  }

  public String getPagnLinkIndex()
  {
    return pagnLinkIndex;
  }

  public void setPagnLinkIndex(String newPagnLinkIndex)
  {
    pagnLinkIndex = newPagnLinkIndex;
  }

  public String getPagnRowsPerPage()
  {
    return pagnRowsPerPage;
  }

  public void setPagnRowsPerPage(String newPagnRowsPerPage)
  {
    pagnRowsPerPage = newPagnRowsPerPage;
  }

  public String getPagnPHYStatus()
  {
    return pagnPHYStatus;
  }

  public void setPagnPHYStatus(String newPagnPHYStatus)
  {
    pagnPHYStatus = newPagnPHYStatus;
  }

  public String getPagnImagePath()
  {
    return pagnImagePath;
  }

  public void setPagnImagePath(String newPagnImagePath)
  {
    pagnImagePath = newPagnImagePath;
  }

  public String getPagnMsgCode()
  {
    return pagnMsgCode;
  }

  public void setPagnMsgCode(String newPagnMsgCode)
  {
    pagnMsgCode = newPagnMsgCode;
  }
  
  /**
    * This method is to set whether data is a file which has to be viewed on a viewer
    * @Name         setCreatedDateStr
    * @param        viewExcel as String
    **/
    public void setViewExcel(String newViewExcel)
    {
      this.viewExcel = newViewExcel;
    }
  /**
    ** This method is to get whether data is a file which has to be viewed on a viewer
    * @Name         setCreatedDateStr
    * @return       String
    **/
    public String getViewExcel()
    {
      return viewExcel;
    }

  //Added by samir for Security changes on 27-Jan-2006
  public boolean isSecutiryCheckNotPassed()
  {
    return secutiryCheckNotPassed;
  }

  public void setSecutiryCheckNotPassed(boolean newSecutiryCheckNotPassed)
  {
    secutiryCheckNotPassed = newSecutiryCheckNotPassed;
  }

 
  //End of added by samir for Security changes on 27-Jan-2006

// Added by Prachi Shah on 6-Jun-2006 for selectAllOption in pagination
 public boolean isSelectAllOption()
  {
    return selectAllOption;
  }

  public void setSelectAllOption(boolean newSelectAllOption)
  {
    selectAllOption = newSelectAllOption;
  }

  public String getContentType()
  {
    return contentType;
  }

  public void setContentType(String newContentType)
  {
    contentType = newContentType;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String newFileName)
  {
    fileName = newFileName;
  }

  public String getFileExt()
  {
    return fileExt;
  }

  public void setFileExt(String newFileExt)
  {
    fileExt = newFileExt;
  }

  // Added by Vidhya for setting all request parameters -- start
  public boolean isContainsRequest()
  {
    return containsRequest;
  }

  public void setContainsRequest(boolean newContainsRequest)
  {
    containsRequest = newContainsRequest;
  }

  
  // Added by Vidhya for setting all request parameters -- end

// End of Added by Prachi Shah on 6-Jun-2006 for selectAllOption in pagination
  //--------Dharmesh has added for the AJAX-----Date 08/02/2007---
  public String getResponse()
  {
    return response;
  }

  public void setResponse(String newResponse)
  {
    response = newResponse;
  }

  public String getOpenAttchment()
  {
    System.out.println(" coming in open Att() :" + openAttchment );
    return openAttchment;
  }
  public void setOpenAttchment(String newOpenAttchment)
  {
     openAttchment=newOpenAttchment;
  }
 //--------Dharmesh has added for the AJAX---End-----


public String getCrtPostId() {
	return CrtPostId;
}


public void setCrtPostId(String crtPostId) {
	CrtPostId = crtPostId;
}


public String getUpdPostId() {
	return UpdPostId;
}


public void setUpdPostId(String updPostId) {
	UpdPostId = updPostId;
}
}