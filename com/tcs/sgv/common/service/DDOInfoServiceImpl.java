package com.tcs.sgv.common.service;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.dao.DDOInfoDAO;
import com.tcs.sgv.common.dao.DDOInfoDAOImpl;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.common.valueobject.RltDdoBilltype;
import com.tcs.sgv.common.valueobject.RltDdoOffice;
import com.tcs.sgv.common.valueobject.RltDdoOrg;
import com.tcs.sgv.common.valueobject.RltLocationDepartment;


import java.util.List;
import java.util.Map;

/**
 * 
 * @author 602399
 *
 */

public class DDOInfoServiceImpl extends ServiceImpl 
							implements DDOInfoService
{
    Log logger = LogFactory.getLog(getClass());

    public List getDDOInfo(String lStrDDOCode, Long lLngLangId, Long lLngDBId, ServiceLocator serv)
    {
        List lListResult = null;
        DDOInfoDAO lDDoInfo = (DDOInfoDAO) new DDOInfoDAOImpl(serv.getSessionFactory());

        try
        {
            lListResult = lDDoInfo.getDDOInfo(lStrDDOCode, lLngLangId, lLngDBId);
        }
        catch (Exception e)
        {
            logger.error("Error in getDDOInfo and Error is : " + e, e);
        }

        return lListResult;
    }


    public List getDDOInfoByPost(Long lLngPostId, Long lLngLangId, Long lLngDBId, ServiceLocator serv)
    {
        List lListResult = null;
        DDOInfoDAO lDDoInfo = (DDOInfoDAO) new DDOInfoDAOImpl(serv.getSessionFactory());

        try
        {
            lListResult = lDDoInfo.getDDOInfoByPost(lLngPostId, lLngLangId, lLngDBId);
        }
        catch (Exception e)
        {
            logger.error("Error in getDDOInfoByPost and Error is : " + e, e);
        }

        return lListResult;
    }


    public List getCompleteDDOInfoByCardex(String lStrCardexNo, String lStrOfficeCode, Long lLngLangId,
            Long lLngDBId, ServiceLocator serv)
    {
        List lListResult = null;
        DDOInfoDAO lDDoInfo = (DDOInfoDAO) new DDOInfoDAOImpl(serv.getSessionFactory());

        try
        {
            lListResult = lDDoInfo.getCompleteDDOInfoByCardex(Integer.valueOf(lStrCardexNo), lStrOfficeCode, lLngLangId);
        }
        catch (Exception e)
        {
            logger.error("Error in getDDOInfoByCardex and Error is : " + e, e);
        }

        return lListResult;
    }


    public List getDDOInfoByNo(String lStrDDONo, String lDDOType,
            Long lLngLangId, Long lLngDBId, ServiceLocator serv)
    {
        List lListResult = null;
        DDOInfoDAO lDDoInfo = (DDOInfoDAO) new DDOInfoDAOImpl(serv.getSessionFactory());

        try
        {
            lListResult = lDDoInfo.getDDOInfoByNo(Long.parseLong(lStrDDONo), lDDOType,lLngLangId, lLngDBId);
        }
        catch (Exception e)
        {
            logger.error("Error in getDDOInfoByNo and Error is : " + e, e);
        }

        return lListResult;
    }
    
    public List getBillOfficeForDDO(ServiceLocator serv, String lStrDDOCode, Long lIntLangId, Long lLngDBId)
    {
    	List lListResult = null;
        DDOInfoDAO lDDoInfo = (DDOInfoDAO) new DDOInfoDAOImpl(serv.getSessionFactory());

        try
        {
            lListResult = lDDoInfo.getBillOfficeForDDO(lStrDDOCode, lIntLangId, lLngDBId);
        }
        catch (Exception e)
        {
            logger.error("Error in getBillOfficeForDDO and Error is : " + e, e);
        }

        return lListResult;
    }
    
    public List getOrgPostMstbyPost(ServiceLocator serv, Long lLngPostId)
    {
    	List resList = null;
    	DDOInfoDAO lDDoInfo = (DDOInfoDAO) new DDOInfoDAOImpl(serv.getSessionFactory());
    	
    	try
    	{
    		resList = lDDoInfo.getOrgPostMstbyPost(lLngPostId);
    	}
    	catch(Exception e)
    	{
    		logger.error("Error in getOrgPostMstbyPost : " + e, e);
    	}
    	
    	return resList;
    }
    
    public List getOrgDesgMstbyDesgCode(ServiceLocator serv, String lStrDegnCode)
    {
    	List resList = null;
    	DDOInfoDAO lDDoInfo = (DDOInfoDAO) new DDOInfoDAOImpl(serv.getSessionFactory());
    	
    	try
    	{
    		resList = lDDoInfo.getOrgDesgMstbyDesgCode(lStrDegnCode);
    	}
    	catch(Exception e)
    	{
    		logger.error("Error in getDesignationByPost() of DDOInfoServiceImpl", e);
    	}
    	
    	return resList;
    }
}