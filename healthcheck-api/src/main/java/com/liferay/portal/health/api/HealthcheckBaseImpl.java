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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

public abstract class HealthcheckBaseImpl implements Healthcheck {

	/**
	 * 
	 * @param healthcheck used to look up a resource bundle for messages that might
	 *                    come with this healthcheck
	 * @param locale      the locale that this message should be displayed in
	 * @param throwable   the exception to describe this healthcheck's failed status
	 * @return
	 */
	public HealthcheckItem create3(Healthcheck check, Locale locale, Throwable throwable) {
		return new HealthcheckItem(this, false, this.getClass().getName() + "-exception", null, 
				"exception-notification-for-healthcheck",
				check.getClass().getName(), throwable.getClass().getName() + " " + throwable.getMessage());
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
