package com.liferay.portal.health.util;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.api.HealthcheckItemImpl;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

public class HealthcheckUtil {
	/**
	 * 
	 * @param healthcheck used to look up a resource bundle for messages that might come with this healthcheck
	 * @param locale the locale that this message should be displayed in
	 * @param state true if healthcheck passed
	 * @param key the key to look up the message's translation
	 * @param info parameters to be embedded in the message returned
	 * @return
	 */
	public String lookupMessage(Healthcheck healthcheck, Locale locale, boolean state, String key, Object... info) {
		String prefix = state ? "message-" : "missing-";
		ResourceBundle bundle = ResourceBundleUtil.getBundle(locale, this.getClass().getClassLoader());
		return ResourceBundleUtil.getString(bundle, prefix+key, info);
	}
	
	public HealthcheckItem create(String category, boolean state, Locale locale, String link, String msgKey, Object... info) {
		ResourceBundle bundle = ResourceBundleUtil.getBundle(locale, this.getClass().getClassLoader());
		String message = ResourceBundleUtil.getString(bundle, msgKey, info);
		return new HealthcheckItemImpl(state, message, link, category);
	}
	
	public HealthcheckItem create(String category, Locale locale, Throwable throwable) {
		ResourceBundle bundle = ResourceBundleUtil.getBundle(locale, HealthcheckUtil.class);
		String message = ResourceBundleUtil.getString(bundle, "exception-notification-for-checklist", this.getClass().getName(), 
				throwable.getClass().getName() + " " + throwable.getMessage());
		return new HealthcheckItemImpl(false, message, null, category);
	}
}
