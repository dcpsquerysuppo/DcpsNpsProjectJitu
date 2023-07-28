package com.tcs.sgv.dcps.service;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.eis.valueobject.HrPayPsrPostMpg;

public class AddNewPostVOGenerator extends ServiceImpl {
	Log logger = LogFactory.getLog(getClass());
	ResourceBundle constantsBundle = ResourceBundle	.getBundle("resources.Payroll");

	public final int DEPT_ID = Integer.parseInt(constantsBundle.getString("GAD"));

	public ResultObject submitAdminOrgPostDtlVogen(Map<String, Object> objectArgs) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {

			HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
			String flag = StringUtility.getParameter("flag", request);
			String lStrFieldDept = StringUtility.getParameter("cmbFieldDept", request);
			objectArgs.put("FieldDept",lStrFieldDept);
			
			String lStrDdoCode = StringUtility.getParameter("cmbDdoDtl", request);			
			objectArgs.put("DdoCode",lStrDdoCode);
			
			Long officeCmb = request.getParameter("officeCmb") != null ? Long.parseLong(StringUtility.getParameter("officeCmb",request)): 0L;			
			objectArgs.put("officeCmb",officeCmb);
			
			Long billCmb = request.getParameter("billCmb") != null ? Long.parseLong(StringUtility.getParameter("billCmb",request)): 0L;
			objectArgs.put("billCmb",billCmb);
			
			Long noofpost = request.getParameter("postNumber") != null ? Long.parseLong(StringUtility.getParameter("postNumber",request)): 0L;			
			objectArgs.put("noofpost",noofpost);
			
			String designationCmb = request.getParameter("designationCmb") != null ? StringUtility.getParameter("designationCmb",request): "";
			objectArgs.put("designationCmb",designationCmb);
			
			Long orderCmb =0L;
			if(request.getParameter("orderCmb") != null && !request.getParameter("orderCmb").equals("") )
			{
				orderCmb = request.getParameter("orderCmb") != null ? Long.parseLong(StringUtility.getParameter("orderCmb",request)): 0L;
				objectArgs.put("orderCmb",orderCmb);
			}
			
			String orderDatelStr = StringUtility.getParameter("OrderDate",request)!=null?StringUtility.getParameter("OrderDate",request):"";
			String remarksStr = StringUtility.getParameter("Remarks",request)!=null?StringUtility.getParameter("Remarks",request):"";
			String tempTypPostStr = StringUtility.getParameter("purposeCmbBox",request)!=null?StringUtility.getParameter("purposeCmbBox",request):"";
			Long postTypecmb = request.getParameter("postTypeCmbBox") != null ? Long.parseLong(StringUtility.getParameter("postTypeCmbBox",request)): 0L;
			String oldOrderDate = StringUtility.getParameter("OriginalOrderDate",request)!=null?StringUtility.getParameter("OriginalOrderDate",request):"";
			String oldOrderCmb = StringUtility.getParameter("OriginalorderCmb",request)!=null?StringUtility.getParameter("OriginalorderCmb",request):"";
			String newDatelStr = StringUtility.getParameter("RenewalOrderDate",request)!=null?StringUtility.getParameter("RenewalOrderDate",request):"";
			String newOrderCmb = StringUtility.getParameter("RenewalorderCmb",request)!=null?StringUtility.getParameter("RenewalorderCmb",request):"";
			
			
			Long Permenant = request.getParameter("Permenant") != null ? Long.parseLong(StringUtility.getParameter("Permenant",request)): 0L;
			objectArgs.put("Permenant",Permenant);			
			objectArgs.put("orderDate",orderDatelStr);
			objectArgs.put("remarks",remarksStr);
			objectArgs.put("postType",postTypecmb);
			objectArgs.put("tempTypePost",tempTypPostStr);
			objectArgs.put("oldOrderDate",oldOrderDate);
			objectArgs.put("oldOrderCmb",oldOrderCmb);
			objectArgs.put("newDate",newDatelStr);
			objectArgs.put("newOrderCmb",newOrderCmb);
						
			String startDatelStr = StringUtility.getParameter("startDate",request)!=null?StringUtility.getParameter("startDate",request):"";
			String endDatelStr = StringUtility.getParameter("endDate", request)!=null?StringUtility.getParameter("endDate", request):"";
			String tempEndDatelStr = StringUtility.getParameter("tempEndDate", request)!=null?StringUtility.getParameter("tempEndDate", request):"";
			objectArgs.put("startDate",startDatelStr);
			objectArgs.put("endDate",endDatelStr);
			objectArgs.put("tempEndDate",tempEndDatelStr);
			
			objectArgs.put("flag", flag);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(objectArgs);
		} catch (Exception ex) {
			resObj.setThrowable(ex);
			logger.error("Admin Screen Creating Admin Screen Post Error In VOgen",ex);
			resObj.setResultCode(ErrorConstants.ERROR);
		}
		return resObj;
	}
	
}
