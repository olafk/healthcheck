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

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalServiceUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(configurationPid = "com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration", service = Healthcheck.class)
public class FormDataProviderHealthcheck implements Healthcheck {

	private final static String LINK_BASE = "/group/guest/~/control_panel/manage?p_p_id=com_liferay_dynamic_data_mapping_data_provider_web_portlet_DDMDataProviderPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_dynamic_data_mapping_data_provider_web_portlet_DDMDataProviderPortlet_displayStyle=descriptive&_com_liferay_dynamic_data_mapping_data_provider_web_portlet_DDMDataProviderPortlet_mvcPath=%2Fedit_data_provider.jsp&_com_liferay_dynamic_data_mapping_data_provider_web_portlet_DDMDataProviderPortlet_dataProviderInstanceId=";
	private final static String MSG = "healthcheck-dataprovider-detected-host-ignore-if-expected";
	private final static String MSG_WHITELISTED = "healthcheck-dataprovider-whitelisted";

	public FormDataProviderHealthcheck() {
	}

	@Override
	public Collection<HealthcheckItem> check(long companyId) {
		Locale locale = getDefaultLocale(companyId);
		LinkedList<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		try {
			String virtualHostname;
			virtualHostname = companyLocalService.getCompany(companyId).getVirtualHostname();
			List<DDMDataProviderInstance> ddmDataProviderInstances = DDMDataProviderInstanceLocalServiceUtil
					.getDDMDataProviderInstances(0, 1000000);
			for (Iterator<DDMDataProviderInstance> iterator = ddmDataProviderInstances.iterator(); iterator
					.hasNext();) {
				DDMDataProviderInstance dataProvider = (DDMDataProviderInstance) iterator.next();
				DataProviderData data = JSONFactoryUtil.looseDeserialize(dataProvider.getDefinition(),
						DataProviderData.class);
				String url = data.getUrl();
				if (url != null) {
					// create a problem indicator in any case, so that users can
					// ignore it if the URL is expected. The ignore-key includes the
					// ignored host name and the current company host name, so that
					// any system restored under a different name (when company-virtualhost
					// is configured correctly) will trigger new alerts.
					// Note: There is a separate health check to validate the company
					// virtualhost, that should make this unignorable.
					String host = getHost(url);
					if (hostWhitelists.contains(host)) {
						result.add(new HealthcheckItem(this, true, this.getClass().getName() + "-" + virtualHostname + "-" + host, 
								LINK_BASE + dataProvider.getDataProviderInstanceId(), 
								MSG_WHITELISTED, dataProvider.getName(locale), host));
					} else {
						result.add(new HealthcheckItem(this, false, this.getClass().getName() + "-" + virtualHostname + "-" + host, 
								LINK_BASE + dataProvider.getDataProviderInstanceId(), 
								MSG, dataProvider.getName(locale), host));
					}
				}

			}
			if (result.isEmpty()) {
				Object[] info = {};
				result.add(new HealthcheckItem(this, true, this.getClass().getName(), null, "healthcheck-dataprovider-none-detected", info));
			}
		} catch (PortalException e) {
			result.add(new HealthcheckItem(this, e));
		}
		return result;
	}

	private String getHost(String url) {
		if (url.startsWith("http")) {
			int endOfHost = url.indexOf('/', 8);
			if (endOfHost > -1) {
				return url.substring(0, endOfHost);
			}
		}
		return url;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		HealthcheckOperationalConfiguration config = ConfigurableUtil
				.createConfigurable(HealthcheckOperationalConfiguration.class, properties);
		String[] whitelist = config.dataProviderHostWhitelist();
		if (whitelist == null) {
			_log.info("empty DataProvider whitelist");
			hostWhitelists = new HashSet<String>();
		} else {
			_log.info("setting up DataProvider whitelist with " + whitelist.length + " elements.");
			hostWhitelists = new HashSet<String>(Arrays.asList(whitelist));
		}
	}
	
	private Locale getDefaultLocale(long companyId) {
		try {
			return CompanyLocalServiceUtil.getCompany(companyId).getLocale();
		} catch (PortalException e) {
			return Locale.US;
		}
	}

	@Reference
	protected CompanyLocalService companyLocalService;

	@JSON
	public static class DataProviderData {
		@JSON
		DataProviderFieldValues[] fieldValues;

		public String getUrl() {
			for (DataProviderFieldValues value : fieldValues) {
				if ("url".equals(value.fieldReference)) {
					return value.value;
				}
			}
			return null;
		}
	}

	@JSON
	public static class DataProviderFieldValues {
		@JSON
		public String fieldReference;
		@JSON
		public String value;
	}

	private Set<String> hostWhitelists = new HashSet<String>();

	static Log _log = LogFactoryUtil.getLog(FormDataProviderHealthcheck.class);

}
