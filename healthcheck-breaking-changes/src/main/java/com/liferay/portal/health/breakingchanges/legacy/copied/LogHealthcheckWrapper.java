package com.liferay.portal.health.breakingchanges.legacy.copied;

import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.api.HealthcheckItemImpl;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LogHealthcheckWrapper {
	
	String category;

	public LogHealthcheckWrapper(String category) {
		this.category = category;
	}
	
	public void error(String msg) {
		HealthcheckItemImpl item = new HealthcheckItemImpl(false, msg, 
				"https://github.com/liferay/liferay-portal/blob/master/portal-impl/src/com/liferay/portal/verify/VerifyProcess.java", 
				category);
		result.add(item);
	}
	
	public void error(String msg, Throwable throwable) {
		HealthcheckItemImpl item = new HealthcheckItemImpl(false, 
				msg + " " + throwable.getClass().getName() + " " + throwable.getMessage(), 
				"https://github.com/liferay/liferay-portal/blob/master/portal-impl/src/com/liferay/portal/verify/VerifyProcess.java", 
				category);
		result.add(item);
	}
	
	public void warn(String msg, RuntimeException exception) {
		error(msg, exception);
	}

	public boolean isWarnEnabled() {
		return true;
	}

	public List<HealthcheckItem> popItems() {
		List<HealthcheckItem> result = this.result;
		this.result = new LinkedList<HealthcheckItem>();
		return result;
	}
	
	public String lookupMessage(Locale locale, String key, Object... info) {
		ResourceBundle bundle = ResourceBundleUtil.getBundle(locale, this.getClass().getClassLoader());
		return ResourceBundleUtil.getString(bundle, key, info);
	}
	
	private List<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
}