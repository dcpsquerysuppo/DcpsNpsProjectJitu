/**
 * 
 */
package com.tcs.sgv.common.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tcs.sgv.acl.login.dao.AclPwrdchkDtlDAOImpl;
import com.tcs.sgv.acl.login.service.ChangePasswordService;
import com.tcs.sgv.acl.login.valueobject.AclPwrdchkDtl;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.PasswordEncryption;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.utils.Utility;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

/**
 * @author Meeta Thacker -incorporated changes for Maharasthtra IFMS in the
 *         framework Change Password Method
 * 
 */
public class ChangePasswordServiceImpl extends ServiceImpl implements ChangePasswordService {
	private static final Logger logger = Logger.getLogger(ChangePasswordServiceImpl.class.getName());
	public static ResourceBundle constResourceBundle = null;

	static {
		constResourceBundle = ResourceBundle.getBundle("resources/Constants");
	}

	public ResultObject changePassword(Map objectArgs) {
		boolean result = false;
		ResultObject objRes = new ResultObject(ErrorConstants.ERROR);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		Map loginMap = null;
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		PasswordEncryption objPasswordEncryption = new PasswordEncryption();
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		try {
			if (objRes != null && objectArgs != null) {
				LoginDetails loginDetails = (LoginDetails) session.getAttribute("loginDetails");
				boolean defaultPwd = false;
				boolean newPasswdvalidate = false;
				boolean minChangePwddays = false;
				String password = "";
				String defaultPassword = "";
				boolean chngPwdRequired = false;
				boolean chngPwdAlert = false;
				int chngPwd_days_left = 0;
				String lStrEmpDob = "";
				if (loginDetails != null) {
					// defaultPassword =
					// constResourceBundle.getString("DEFAULTPASSWORD");

					// added to set the default password as Employee birthdate
					OrgEmpMst orgempmst = loginDetails.getEmployee();
					if (orgempmst != null) {
						java.util.Date date = orgempmst.getEmpDob();
						SimpleDateFormat simpledateformat = new SimpleDateFormat("ddMMyyyy");
						lStrEmpDob = simpledateformat.format(date);
					}
					defaultPassword = lStrEmpDob;
					// end
					password = loginDetails.getEmployee().getOrgUserMst().getPassword();

					if (objPasswordEncryption.crypt(defaultPassword).equalsIgnoreCase(password)) {
						defaultPwd = true;
					}

					Object objChngPwdDays = constResourceBundle.getString("CHANGE_PWD_DAYS");
					int changePwd_days = (objChngPwdDays != null && !objChngPwdDays.equals("")) ? Integer
							.parseInt(objChngPwdDays.toString()) : 0;
					Object objChngPwdAlert = constResourceBundle.getString("CHNGPWD_ALERT_DAYS");
					int chngPwdAlertDays = (objChngPwdAlert != null && !objChngPwdAlert.equals("")) ? Integer
							.parseInt(objChngPwdAlert.toString()) : 0;

					String objMinPwrdCheck = constResourceBundle.getString("MIN_CHANGE_PWD_DAYS");
					int MinPwrdCheck = Integer.parseInt(objMinPwrdCheck);

					Date currDate = DBUtility.getCurrentDateFromDB();
					Calendar calLastPwdChngDate = Calendar.getInstance();

					Date lastPwdChngDate = loginDetails.getEmployee().getOrgUserMst().getPwdchangedDate();
					if (lastPwdChngDate != null) {
						calLastPwdChngDate.setTime(lastPwdChngDate);
					}
					Calendar calCurrDate = Calendar.getInstance();
					calCurrDate.setTime(currDate);

					int daysDiff = daysBetween(calLastPwdChngDate, calCurrDate);

					if (!defaultPwd) {
						if (daysDiff - 1 < MinPwrdCheck) {
							minChangePwddays = true;
						}
					}

					if (changePwd_days > 0) {
						if (daysDiff > changePwd_days) {
							chngPwdRequired = true;
						}
						if (daysDiff > (changePwd_days - chngPwdAlertDays)) {
							chngPwdAlert = true;
							chngPwd_days_left = changePwd_days - daysDiff + 1;
						}
					}

					// For the Actual password expire days ....
					// password_expire = changePwd_days - daysDiff;
					// calCurrDate.add(Calendar.DAY_OF_MONTH,password_expire);
					calCurrDate.add(Calendar.DAY_OF_MONTH, 45);
					String passExpire = DateFormat.getDateTimeInstance().format(calCurrDate.getTime());
					passExpire = passExpire.substring(0, 12);
					objectArgs.put("passExpire", passExpire);

				}
				String updatePwd = StringUtility.getParameter("updatePwd", request);

				if (defaultPwd && !updatePwd.equalsIgnoreCase("Y")) {
					objRes.setResultValue(objectArgs);
					objRes.setViewName("acl-changePassword");
					objRes.setResultCode(ErrorConstants.BREAK);
				} else if (minChangePwddays && updatePwd.equalsIgnoreCase("Y")) {
					objectArgs.put("minChangePwddays", minChangePwddays);
					objRes.setResultValue(objectArgs);
					objRes.setViewName("acl-changePassword");
					objRes.setResultCode(ErrorConstants.BREAK);
				}

				else if (chngPwdRequired && !updatePwd.equalsIgnoreCase("Y")) {
					objectArgs.put("chngPwdRequired", "Y");
					objRes.setResultValue(objectArgs);
					objRes.setViewName("acl-changePassword");
					objRes.setResultCode(ErrorConstants.BREAK);
				} else if (defaultPwd && !updatePwd.equalsIgnoreCase("Y")) {
					objectArgs.put("defaultPasswordLogin", "Y");
					objRes.setResultValue(objectArgs);
					objRes.setViewName("acl-changePassword");
					objRes.setResultCode(ErrorConstants.BREAK);
				} else if (updatePwd.equalsIgnoreCase("Y")) {
					if ("Y".equalsIgnoreCase(constResourceBundle.getString("LDAPAUTHENTICATION"))) {
						logger.info("LDAP AUTHENTICATION SERVICE BEING CALLED.");
						objRes = serv.executeService("CHANGE_PASSWORD_LDAP", objectArgs);
						Boolean objResult = (Boolean) objectArgs.get("IsPasswdChanged");
						result = objResult.booleanValue();
						logger.info("RESULT OF LDAP AUTHENTICATION::" + result);
					} else {
						logger.info("DB AUTHENTICATION SERVICE BEING CALLED.");
						String oldPassword = StringUtility.getParameter("txtOldPassword", request);
						String newPassword = StringUtility.getParameter("txtNewPassword", request);
						logger.info("oldPassword------" + oldPassword);
						logger.info("newPassword------" + newPassword);
						if (!defaultPassword.equalsIgnoreCase(newPassword)) {
							loginMap = (Map) objectArgs.get("baseLoginMap");
							Long userId = (Long) loginMap.get("userId");
							OrgUserMst objUserMst = null;

							oldPassword = objPasswordEncryption.crypt(oldPassword);
							OrgUserMstDaoImpl userMstImpl = new OrgUserMstDaoImpl(OrgUserMst.class, serv
									.getSessionFactory());
							OrgPostMstDaoImpl postMstImpl = new OrgPostMstDaoImpl(OrgPostMst.class, serv
									.getSessionFactorySlave());
							Long postId = (Long) loginMap.get("loggedInPost");
							OrgPostMst objOrgPostMst = postMstImpl.getPostVO(postId);
							if (userId != null && userId != 0) {
								objUserMst = userMstImpl.read(userId);
							}
							// Added by nishant vaidya for not allowing same
							// password
							String samePassword = constResourceBundle.getString("SAME_PASSWORD");
							String newPasswordEncrypt = objPasswordEncryption.crypt(newPassword).toString();
							if (samePassword.equalsIgnoreCase("y")) {
								List list = userMstImpl.getListByColumnAndValue("password", newPasswordEncrypt);
								if (list.size() != 0) {
									objectArgs.put("commonpassword", true);
									objRes.setResultValue(objectArgs);
									objRes.setViewName("acl-changePassword");
									objRes.setResultCode(ErrorConstants.SUCCESS);
									return objRes;
								}
							}

							if (oldPassword.equals(password) && objUserMst != null) {

								Date currDateTime = DBUtility.getCurrentDateFromDB();
								// Added for New Password Redundancy Check
								String objPwrdRendCheck = constResourceBundle.getString("PASSWORD_REDUNDANCY_CHECK");
								int PwrdRendCheck = Integer.parseInt(objPwrdRendCheck);
								AclPwrdchkDtlDAOImpl aclPwrdchkDtlImpl = new AclPwrdchkDtlDAOImpl(AclPwrdchkDtl.class,
										serv.getSessionFactory());
								List<AclPwrdchkDtl> aclPwrdchkDtlLst = aclPwrdchkDtlImpl
										.getAclPwrdchkDtlByuserId(userId);

								logger.info("The list for redundancy in password is" + aclPwrdchkDtlLst.size());
								if (!(aclPwrdchkDtlLst.isEmpty())) {
									int aclPwrdchkDtlLstsize = aclPwrdchkDtlLst.size();
									if (aclPwrdchkDtlLstsize > PwrdRendCheck) {
										for (int i = 0; i < PwrdRendCheck; i++) {
											AclPwrdchkDtl pwrdchkDtl = aclPwrdchkDtlLst.get(i);
											if (pwrdchkDtl.getPwd() != null) {
												if (newPasswordEncrypt.equalsIgnoreCase(pwrdchkDtl.getPwd().toString())) {
													newPasswdvalidate = true; // flag
													// for
													// validate
													// pwrd

												}
											}
										}
									} else {

										for (int i = 0; i < aclPwrdchkDtlLstsize; i++) {
											AclPwrdchkDtl pwrdchkDtl = aclPwrdchkDtlLst.get(i);

											if (pwrdchkDtl.getPwd() != null) {
												if (newPasswordEncrypt.equalsIgnoreCase(pwrdchkDtl.getPwd().toString())) {
													newPasswdvalidate = true; // flag
													// for
													// validate
													// pwrd
												}
											}
										}
									}

								}

								if (!newPasswdvalidate) {

									long srno = IDGenerateDelegate.getNextId("ACL_PWRDCHK_DTL", objectArgs);
									AclPwrdchkDtl aclPwrdchkDt = new AclPwrdchkDtl();
									aclPwrdchkDt.setSrNo(srno);
									aclPwrdchkDt.setOrgUserMstByUserId(objUserMst);
									aclPwrdchkDt.setPwd(newPasswordEncrypt);
									aclPwrdchkDt.setCreatedDate(currDateTime);
									aclPwrdchkDt.setOrgUserMstByCreatedBy(objUserMst);
									aclPwrdchkDt.setOrgPostMstByCreatedByPost(objOrgPostMst);
									aclPwrdchkDt.setUpdatedDate(currDateTime);
									aclPwrdchkDt.setOrgUserMstByUpdatedBy(objUserMst);
									aclPwrdchkDt.setOrgPostMstByUpdatedByPost(objOrgPostMst);
									aclPwrdchkDtlImpl.create(aclPwrdchkDt);
									// Ended here
									objUserMst.setPasswordSha(Utility.getEncryptedPasswordForDB(newPassword));

									objUserMst.setPassword(newPasswordEncrypt);
									objUserMst.setOrgPostMstByUpdatedByPost(objOrgPostMst);
									objUserMst.setOrgUserMstByUpdatedBy(objUserMst);
									objUserMst.setUpdatedDate(currDateTime);
									objUserMst.setPwdchangedDate(currDateTime);
									userMstImpl.update(objUserMst);

									result = true;
								}

							} else {
								result = false;
							}
							logger.info("RESULT OF DB AUTHENTICATION::" + result);
							objectArgs.put("IsPasswdChanged", result);
							objectArgs.put("newPasswdvalidate", newPasswdvalidate);
						} else {
							objectArgs.put("newDefaultPwd", result);
						}
					}
					logger.info("RESULT BEFORE SETTING VIEW::" + result);
					objRes.setResultValue(objectArgs);
					objRes.setResultCode(ErrorConstants.SUCCESS);
					if (result && !newPasswdvalidate) {
						logger.info("FORWARDING TO VIEW acl-changePasswordResult");
						/*
						 * Map redirectMap = new HashMap();
						 * redirectMap.put("actionFlag", "getDisplayMessage");
						 * redirectMap.put("MESSAGECODE", Long.valueOf(10391));
						 * objRes.setResultCode(ErrorConstants.SUCCESS);
						 * objectArgs.put("redirectMap", redirectMap);
						 * objRes.setResultValue(objectArgs);
						 * objRes.setViewName("redirect view");
						 */
						objRes.setViewName("PwdChange");
					} else {
						logger.info("FORWARDING TO VIEW acl-changePassword");
						objRes.setViewName("acl-changePassword");
					}

				} else {

					logger.info("I am here" + objRes.getViewName());
					if (chngPwdAlert) {
						objectArgs.put("ChangePasswordAlert", Integer.toString(chngPwd_days_left));
					}

					objRes.setResultCode(ErrorConstants.SUCCESS);
					objRes.setResultValue(objectArgs);
				}
			} else {
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setThrowable(new Exception("Arguments are null while validating service."));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setThrowable(e);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	public int daysBetween(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
		int daysBetween = 0;
		while (date.before(endDate)) {
			date.add(5, 1);
			++daysBetween;
		}

		return daysBetween;
	}
}