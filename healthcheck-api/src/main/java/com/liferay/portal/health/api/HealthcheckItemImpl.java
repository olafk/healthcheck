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

public class HealthcheckItemImpl implements HealthcheckItem {

	public HealthcheckItemImpl(boolean resolved, String source, String message, String link, String category) {
		this.resolved = resolved;
		this.source = source;
		this.link = link;
		this.message = message;
		this.category = category;
	}

	@Override
	public boolean isResolved() {
		return resolved;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLink() {
		return link;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getKey() {
		return source + "-" + resolved;
	}

	private final boolean resolved;
	private final String source;
	private final String message;
	private final String link;
	private final String category;
}
