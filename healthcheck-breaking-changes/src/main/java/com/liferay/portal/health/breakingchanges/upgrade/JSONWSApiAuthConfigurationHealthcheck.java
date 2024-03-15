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

package com.liferay.portal.health.breakingchanges.upgrade;

import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * Check for missing configuration of JSONWS API, which occurred in updated
 * systems and might lead to https://issues.liferay.com/browse/LPS-159746
 * 
 * @author Olaf Kock
 *
 */
@Component(configurationPid = "com.liferay.portal.security.auth.verifier.internal.portal.session.configuration.PortalSessionAuthVerifierConfiguration", service = Healthcheck.class)
public class JSONWSApiAuthConfigurationHealthcheck extends HealthcheckBaseImpl {
	private static final String LINK = "https://issues.liferay.com/browse/LPS-159746";
	private static final String MSG = "healthcheck-verify-jsonws-configuration";
	private static final String ERRORMSG = "healthcheck-verify-jsonws-configuration-missing";

	private List<String> urlsIncludes;

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		boolean correctlyConfigured = urlsIncludes.contains("/api/json*") && urlsIncludes.contains("/api/jsonws*")
				&& urlsIncludes.contains("/c/portal/json_service*");
		return wrap(create1(correctlyConfigured, locale, LINK, correctlyConfigured ? MSG : ERRORMSG, urlsIncludes));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-breaking-changes";
	}

	@Reference
	protected void setConfigurationProvider(ConfigurationProvider configurationProvider) {
		// configuration update will actually be handled in the @Modified event,
		// which will only be triggered in case we have a @Reference to the
		// ConfigurationProvider
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		String urls = (String) properties.get("urlsIncludes");
		this.urlsIncludes = StringUtil.split(urls, ',');
	}
}
