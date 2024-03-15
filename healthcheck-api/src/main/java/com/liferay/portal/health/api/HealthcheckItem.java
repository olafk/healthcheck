/**
 * Copyright (c) 2022-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.health.api;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

public class HealthcheckItem {

	public HealthcheckItem(Healthcheck healthcheck, boolean resolved, String source, String link, String message, Object... info) {
		this.resolved = resolved;
		this.source = source;
		this.link = link;
		this.message = message;
		this.healthcheck = healthcheck;
		this.info = info;
	}

	/**
	 * signals if the healthcheck result is healthy or not
	 * 
	 * @return true if healthy
	 */
	public boolean isResolved() {
		return resolved;
	}

	/**
	 * An informative message on the tested condition, in the language that a
	 * healthcheck has been executed.
	 * 
	 * @return human readable message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * A link(URL) that can contain further information on the tested condition
	 * 
	 * @return a link URL
	 */
	public String getLink() {
		return link;
	}

	/**
	 * A human readable (localized) category for the kind of healthcheck that was
	 * executed
	 * 
	 * @return
	 */
	public String getCategory(Locale locale) {
		return lookup(locale, healthcheck.getCategory());
	}
	
	public String getCategory() {
		return healthcheck.getCategory();
	}

	/**
	 * A machine readable key that can be used to refer to a particular healthcheck
	 * or its result. This was introduced to be able to ignore certain healthchecks,
	 * in case their test does not apply to a certain environment (example:
	 * Elasticsearch Sidecar is ok in local demo systems). Default content: The
	 * healthcheck's fully qualified classname, optionally extended by extra
	 * information (each healthcheck might execute several checks) and the
	 * 
	 * @return
	 */
	public String getKey() {
		return source + "-" + resolved;
	}

	public String getMessage(Locale locale) {
		return lookup(locale, message, info);		
	}

	private String lookup(Locale locale, String key, Object... parameters) {
		ResourceBundle bundle = ResourceBundleUtil.getBundle(locale, healthcheck.getClass().getClassLoader());
		String result = ResourceBundleUtil.getString(bundle, key, parameters);
		if (result == null) {
			bundle = ResourceBundleUtil.getBundle(locale, Healthcheck.class.getClassLoader());
			result = ResourceBundleUtil.getString(bundle, key, parameters);
			if(result == null) {
				result = LanguageUtil.format(locale, key, parameters);
				if (result == null) {
					result = key;
				}
			}
		}
		return result;
	}
	
	private final boolean resolved;
	private final Healthcheck healthcheck;
	private final String source;
	private final String message;
	private final String link;
	private Object[] info;
}