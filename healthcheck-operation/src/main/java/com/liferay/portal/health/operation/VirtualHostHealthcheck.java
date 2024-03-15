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

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.operation.auxiliary.HostNameExtractingFilter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import javax.servlet.Filter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Healthcheck for relaxed-security Demo Systems Demo systems, especially on
 * LXC-SM, are often restored from systems that are configured for a different
 * virtual host name. Make sure that the currently configured virtual host name
 * has at least been accessed once during the current uptime of the system
 * (typically this happens at least when the healthcheck report page is
 * accessed)
 * 
 * @author Olaf Kock
 */

@Component(service = Healthcheck.class)
public class VirtualHostHealthcheck implements Healthcheck {

	private static final String MSG = "healthcheck-configured-virtualhost-has-been-accessed";
	private static final String MSG_ERROR = "healthcheck-configured-virtualhost-has-not-been-accessed-yet-check-company-virtual-host";
	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fview_configuration_screen&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_configurationScreenKey=general";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		try {
			String configuredHostname;
			configuredHostname = companyLocalService.getCompany(companyId).getVirtualHostname();
			Set<String> requestedHostnames = filter.getAccessedUrls(companyId);
			if (requestedHostnames.contains("https://" + configuredHostname)
					|| requestedHostnames.contains("http://" + configuredHostname)) {
				Object[] info = { configuredHostname };
				return Arrays.asList(new HealthcheckItem(this, true, this.getClass().getName(), LINK, MSG, info));
			}
			Object[] info = { configuredHostname };
			return Arrays.asList(new HealthcheckItem(this, false, this.getClass().getName(), LINK, MSG_ERROR, info));
		} catch (PortalException e) {
			return Arrays.asList(new HealthcheckItem(this, e));
		}
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

	@Reference(target = "(servlet-filter-name=Healthcheck Hostname Extracting Filter)")
	public void setFilter(Filter filter) {
		this.filter = (HostNameExtractingFilter) filter;
	}

	private HostNameExtractingFilter filter;

	@Reference
	CompanyLocalService companyLocalService;
}
