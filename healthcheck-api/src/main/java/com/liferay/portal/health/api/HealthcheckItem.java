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

public interface HealthcheckItem {

	/**
	 * signals if the healthcheck result is healthy or not
	 * 
	 * @return true if healthy
	 */
	boolean isResolved();

	/**
	 * An informative message on the tested condition, in the language that a
	 * healthcheck has been executed.
	 * 
	 * @return human readable message
	 */
	String getMessage();

	/**
	 * A link(URL) that can contain further information on the tested condition
	 * 
	 * @return a link URL
	 */
	String getLink();

	/**
	 * A human readable (localized) category for the kind of healthcheck that was
	 * executed
	 * 
	 * @return
	 */
	String getCategory();

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
	String getKey();
}