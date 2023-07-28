package com.tcs.sgv.retirement.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDao;
/**
 * Interface Description -
 * 
 * 
 * @author Niteesh Kumar Bhargava and shekhar Kadam DAT, Mumbai
 * @version 0.1
 * @since JDK 1.7 Aug 12, 2014
 */
public interface RetirementDAO extends GenericDao   
{
	public List getRetirementListByDDOCode(String locationId) throws Exception;
	public List getDDOCode_locNameByLocId(String locationId) throws Exception;
	
}
