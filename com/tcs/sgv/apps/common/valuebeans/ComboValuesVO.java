package com.tcs.sgv.apps.common.valuebeans;

import com.tcs.sgv.common.valuebeans.BaseValueBean;

public class ComboValuesVO extends BaseValueBean {
	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -3380537387812490281L;
	private String gStrId = null;
	private String gStrDesc = null;

	public ComboValuesVO() {
	}

	public void setId(String argsStrId) {
		gStrId = argsStrId;
	}

	public String getId() {
		return gStrId;
	}

	public void setDesc(String argsStrDesc) {
		gStrDesc = argsStrDesc;
	}

	public String getDesc() {
		return gStrDesc;
	}

}// end class
