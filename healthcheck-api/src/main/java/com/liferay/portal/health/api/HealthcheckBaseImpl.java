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

public abstract class HealthcheckBaseImpl implements Healthcheck {

	/**
	 * Convenience method to turn the single healthcheck parameter into a collection
     *
	 * @return a mutable collection with the parameter item as the only element.
	 */
	protected Collection<HealthcheckItem> wrap(HealthcheckItem item) {
		LinkedList<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		result.add(item);
		return result;
	}
}
