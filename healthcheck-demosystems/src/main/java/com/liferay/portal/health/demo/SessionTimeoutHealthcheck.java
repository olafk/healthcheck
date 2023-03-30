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

package com.liferay.portal.health.demo;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.util.PropsValues;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * Healthcheck for relaxed-security Demo Systems
 * Demo Systems should have the session extended as long as a browser
 * is open, to cater for longer Q&A sessions without any interruption 
 * when the logout-message is missed.
 *  
 * @author Olaf Kock
 */
@Component(
	immediate = true,
	property = {
		// TODO enter required service properties
	},
	service = Healthcheck.class
)
public class SessionTimeoutHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "https://docs.liferay.com/portal/7.3-latest/propertiesdoc/portal.properties.html#Session";
	private static final String MSG = "healthcheck-session-extension-enabled";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		boolean autoextend = PropsValues.SESSION_TIMEOUT_AUTO_EXTEND;
		return wrap(create(autoextend, locale, LINK, MSG, "session.timeout.auto.extend"));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-demosystem";
	}
}