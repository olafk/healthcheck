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

import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class HealthcheckBaseImpl implements Healthcheck {

	/**
	 * 
	 * @param locale      the locale that this message should be displayed in
	 * @param key         the key to look up the message's translation
	 * @param info        parameters to be embedded in the message returned
	 * @return
	 */
	public String lookupMessage(Locale locale, String key, Object... info) {
		if (locale == null || key == null) {
			return "Internal Error: " + this.getClass().getName() + " looked up null key";
		}
		ResourceBundle bundle = ResourceBundleUtil.getBundle(locale, this.getClass().getClassLoader());
		String result = ResourceBundleUtil.getString(bundle, key, info);
		if (result == null) {
			result = key;
		}
		return result;
	}

	/**
	 * 
	 * @param state       true if healthcheck passed
	 * @param locale      the locale that this message should be displayed in
	 * @param link        a link to a description of the status, or the UI where a status can be changed
	 * @param msgKey      the key to look up the message's translation
	 * @param info        parameters to be embedded in the message returned
	 * @return
	 */
	public HealthcheckItem create(boolean state, Locale locale, String link, String msgKey, Object... info) {
		String message = lookupMessage(locale, msgKey, info);
		return new HealthcheckItemImpl(state, this.getClass().getName(), message, link,
				lookupMessage(locale, getCategory()));
	}
	
	/**
	 * 
	 * @param state       true if healthcheck passed, false otherwise
	 * @param baseKey     the key that describes this healthcheck uniquely (can be used to ignore it in future runs)
	 * @param locale      the locale that this message should be displayed in
	 * @param link        a link to a description of the status, or the UI where a status can be changed
	 * @param msgKey      the key to look up the message's translation
	 * @param info        parameters to be embedded in the message returned
	 * @return
	 */
	public HealthcheckItem create(boolean state, String baseKey, Locale locale, String link, String msgKey,
			Object... info) {
		String message = lookupMessage(locale, msgKey, info);
		return new HealthcheckItemImpl(state, baseKey, message, link, lookupMessage(locale, getCategory()));
	}

	/**
	 * 
	 * @param healthcheck used to look up a resource bundle for messages that might
	 *                    come with this healthcheck
	 * @param locale      the locale that this message should be displayed in
	 * @param throwable   the exception to describe this healthcheck's failed status
	 * @return
	 */
	public HealthcheckItem create(Healthcheck check, Locale locale, Throwable throwable) {
		ResourceBundle bundle = ResourceBundleUtil.getBundle(locale, HealthcheckItemImpl.class);
		String message = ResourceBundleUtil.getString(bundle, "exception-notification-for-healthcheck",
				check.getClass().getName(), throwable.getClass().getName() + " " + throwable.getMessage());
		return new HealthcheckItemImpl(false, this.getClass().getName() + "-exception", message, null,
				lookupMessage(locale, getCategory()));
	}

	/**
	 * Convenience method to turn the single healthcheck parameter into a collection

	 * @return a mutable collection with the parameter item as the only element.
	 */
	protected Collection<HealthcheckItem> wrap(HealthcheckItem item) {
		LinkedList<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		result.add(item);
		return result;
	}
}
