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

package com.liferay.portal.health.operation;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(configurationPid = "com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration", service = Healthcheck.class)
public class RemoteElasticHealthcheck extends HealthcheckBaseImpl {

	private final static String LINK = "https://learn.liferay.com/dxp/latest/en/using-search/installing-and-upgrading-a-search-engine/elasticsearch/connecting-to-elasticsearch.html";
	private final static String MSG = "healthcheck-elasticsearch-sidecar-not-supported";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		boolean remote = false;
		boolean configured = operationMode != null || productionModeEnabled != null;
		if (configured) {
			if (productionModeEnabled != null && productionModeEnabled.equals("true")) {
				remote = true;
			} else if (operationMode != null && operationMode.equals("REMOTE")) {
				remote = true;
			}
		}
		return wrap(create(remote, locale, LINK, MSG, productionModeEnabled, operationMode));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
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
		productionModeEnabled = (String) properties.get("productionModeEnabled");
		operationMode = (String) properties.get("operationMode");
	}

	volatile String productionModeEnabled;
	volatile String operationMode;
}
