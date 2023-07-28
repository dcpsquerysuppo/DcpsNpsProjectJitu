package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillReceipt;

public class TrnPensionBillReceiptDAOImpl extends GenericDaoHibernateImpl<TrnPensionBillReceipt, Long> implements TrnPensionBillReceiptDAO{
	
	private final Log logger = LogFactory.getLog(getClass());
	
	public TrnPensionBillReceiptDAOImpl(Class<TrnPensionBillReceipt> type,SessionFactory sessionFactory)
	{
		super(type);
		setSessionFactory(sessionFactory);
	}
	
	public List<TrnPensionBillReceipt> getRcptDtlFromHdrId(Long lPensionBillHdrID)	throws Exception 
	{
		List<TrnPensionBillReceipt> lObjRcptLst = null;
		try {
	
	         Session ghibSession = getSession();
	         StringBuffer hqlQuery = new StringBuffer();
	
			hqlQuery.append(" FROM TrnPensionBillReceipt where trnPensionBillHdrId = :hdrID");
	
			Query lQuery = ghibSession.createQuery(hqlQuery.toString());
	
			lQuery.setLong("hdrID", lPensionBillHdrID);

			List lList = lQuery.list();
            if (lList != null && !lList.isEmpty()) 
            {
            	lObjRcptLst = new ArrayList<TrnPensionBillReceipt>();
				for(Object lObjtemp : lList)
				{
					lObjRcptLst.add((TrnPensionBillReceipt)lObjtemp);
				}
			}
	
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lObjRcptLst;
	}
	
	public Map<Long,Object> getMapOfRcptDtlFromHdrIds(List lListPensionBillNo,String lStrLocCode) throws Exception 
	{
		Map<Long, Object> mapBillReceipt = new HashMap<Long, Object>();
		List lBillReceiptLst = null;
		
		try 
		{
	         Session ghibSession = getSession();
	         StringBuffer hqlQuery = new StringBuffer();
	
			hqlQuery.append(" SELECT br.trn_pension_bill_receipt_id,br.trn_pension_bill_hdr_id,br.type_edp_id,br.edp_code," +
							" br.major_head,br.submajor_head,br.minor_head,br.subhead,br.ded_type,br.amount" +
							" FROM Trn_Pension_Bill_Receipt br " +
							" LEFT JOIN trn_pension_bill_hdr bh ON br.trn_pension_bill_hdr_id = bh.trn_pension_bill_hdr_id" +
							" WHERE bh.bill_no IN (:lBillNoLst)" +
							" AND bh.location_code = :lLocCode ORDER BY br.trn_pension_bill_hdr_id ");
	
			Query lQuery = ghibSession.createSQLQuery(hqlQuery.toString());
	
			lQuery.setParameterList("lBillNoLst", lListPensionBillNo);
			lQuery.setString("lLocCode", lStrLocCode);

			lBillReceiptLst = lQuery.list();
			
            /*if (lList != null && !lList.isEmpty()) 
            {
            	lObjRcptLst = new ArrayList<TrnPensionBillReceipt>();
				for(Object lObjtemp : lList)
				{
					lObjRcptLst.add((TrnPensionBillReceipt)lObjtemp);
				}
				
			}*/
            
            if(lBillReceiptLst != null && !lBillReceiptLst.isEmpty())
            {
        		Long lStrOldBillHdrId = null;
				Long lStrNewBillHdrId = null;

				List<TrnPensionBillReceipt> BillOuterLst = null;
				TrnPensionBillReceipt lBillReceipt = null;

				for (int i = 0; i < lBillReceiptLst.size(); i++)
				{
					Object[] lArrObj = (Object[]) lBillReceiptLst.get(i);
					
					lBillReceipt = null;
					lBillReceipt = new TrnPensionBillReceipt();
									
					lBillReceipt.setTrnPensionBillReceiptId(Long.valueOf(lArrObj[0].toString()));
					lBillReceipt.setTrnPensionBillHdrId(Long.valueOf(lArrObj[1].toString()));
					lBillReceipt.setTypeEdpId(lArrObj[2] != null ? new BigDecimal(lArrObj[2].toString()) : null);
					lBillReceipt.setEdpCode(lArrObj[3].toString());
					lBillReceipt.setMajorHead(lArrObj[4].toString());
					lBillReceipt.setSubmajorHead(lArrObj[5].toString());
					lBillReceipt.setMinorHead(lArrObj[6].toString());
					lBillReceipt.setSubhead(lArrObj[7].toString());
					lBillReceipt.setDedType(lArrObj[8].toString());
					lBillReceipt.setAmount(new BigDecimal(lArrObj[9].toString()));
					
					lStrNewBillHdrId = lBillReceipt.getTrnPensionBillHdrId();

					if (i == 0)
					{
						BillOuterLst = new ArrayList<TrnPensionBillReceipt>();
					} else if (!(lStrOldBillHdrId.equals(lStrNewBillHdrId)))
					{
						mapBillReceipt.put(lStrOldBillHdrId, BillOuterLst);
						BillOuterLst = new ArrayList<TrnPensionBillReceipt>();
					}

					BillOuterLst.add(lBillReceipt);
					lStrOldBillHdrId = lBillReceipt.getTrnPensionBillHdrId();

					if (i == ((lBillReceiptLst.size()) - 1))
					{
						mapBillReceipt.put(lStrOldBillHdrId, BillOuterLst);
					}
            	}
            }
	
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		
		return mapBillReceipt;
	}
}
