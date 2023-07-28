package com.tcs.sgv.common.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.core.service.ServiceImpl;

/**
 * 
 * @author 602399
 * 
 */

public class BudgetHdDtlsServiceVOGenerator extends ServiceImpl 
{
    Log logger = LogFactory.getLog(getClass());
    
    /*
     * @Description : Method to generate VO For BudgetHdDtlsServiceImpl.
     * @Input       : Map : p_objServiceArgs
     * @Output      : ResultObject : retObj 
     * @Author      : 602399
     */
    
  /*  public ResultObject generateMap(Map lMapInput) 
    {
        ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
        
        try
        {
            HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");  
            
            Map lMapBudDetails = new HashMap();

            lMapBudDetails.put("DemandCode", request.getParameter("txtDmd")!= null ? 
                    request.getParameter("txtDmd").trim() : "");
            lMapBudDetails.put("MajorHead", request.getParameter("txtMjrHd") != null ? 
                    request.getParameter("txtMjrHd").trim() : "");
            lMapBudDetails.put("SubMajorHead", request.getParameter("txtSbMjrHd") != null ? 
                    request.getParameter("txtSbMjrHd").trim() : "");
            lMapBudDetails.put("MinorHead", request.getParameter("txtMnrHd") != null ? 
                    request.getParameter("txtMnrHd").trim() : "");
            lMapBudDetails.put("SubHead", request.getParameter("txtSbHd") != null ? 
                    request.getParameter("txtSbHd").trim() : "");
            lMapBudDetails.put("DetailHead", request.getParameter("txtDtldHd") != null ? 
                    request.getParameter("txtDtldHd").trim() : "");
            lMapBudDetails.put("LangId", SessionHelper.getLangId(lMapInput));
            
            lMapInput.put("ResultMap", lMapBudDetails);
            
            objRes.setResultValue(lMapInput);                           
            
            return objRes;
        }
        catch(Exception e)
        {
            logger.error("Error in generateMap and Error is : " + e, e);
        }   
                                
        return objRes;
    }*/
}