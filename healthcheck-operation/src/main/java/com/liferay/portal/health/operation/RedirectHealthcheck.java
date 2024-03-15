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

import static com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys.INSTANCE_SETTINGS;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.operation.auxiliary.HostNameExtractingFilter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Some operations fail when the server can't reliably tell its own host name
 * (e.g. behind a reverse proxy). In that case, the redirectURL should be
 * configured appropriately.
 * 
 * @author Olaf Kock
 */

@Component(immediate = true, configurationPid = RedirectHealthcheck.PID, property = Constants.SERVICE_PID + "="
		+ RedirectHealthcheck.PID + ".scoped", service = { Healthcheck.class, ManagedServiceFactory.class }

)
public class RedirectHealthcheck extends HealthcheckBaseImpl implements ManagedServiceFactory {

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		HostNameExtractingFilter f = (HostNameExtractingFilter) filter;
		Set<String> urls = f.getAccessedUrls(companyId);
		Collection<HealthcheckItem> result = new LinkedList<HealthcheckItem>();

		for (String requestedUrl : urls) {
			String url = PortalUtil.escapeRedirect(requestedUrl);
			result.add(create2(url != null, this.getClass().getName() + "-" + HtmlUtil.escapeURL(requestedUrl), locale,
					LINK, "healthcheck-redirection-url-previous", extractHost(requestedUrl)));
		}

		return result;
	}

	private String extractHost(String url) {
		if (url == null) {
			return "null";
		}
		int separatorIndex = url.indexOf("://");
		if (separatorIndex < 1) { // not found, and should have a scheme leading up to it
			return "???";
		}
		return HtmlUtil.escape(url.substring(separatorIndex + 3));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

	@Override
	public String getName() {
		return PID + ".scoped";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		long companyId = (Long) properties.get("companyId");
		companyToRedirectConfigPid.put(companyId, pid);

		log.debug("adding redirection config for " + companyId + " as " + pid);
	}

	@Override
	public void deleted(String pid) {
		Set<Entry<Long, String>> es = companyToRedirectConfigPid.entrySet();
		for (Entry<Long, String> entry : es) {
			if (entry.getValue().equals(pid)) {
				Long companyId = entry.getKey();
				companyToRedirectConfigPid.remove(companyId);

				log.debug("removing redirection config for " + companyId);
				break;
			}
		}
	}

	@Reference(target = "(servlet-filter-name=Healthcheck Hostname Extracting Filter)")
	Filter filter;

	static Log _log = LogFactoryUtil.getLog(RedirectHealthcheck.class);

	static final String PID = "com.liferay.redirect.internal.configuration.RedirectURLConfiguration";
	private HashMap<Long, String> companyToRedirectConfigPid = new HashMap<Long, String>();
	private static Log log = LogFactoryUtil.getLog(RedirectHealthcheck.class);
	private static String LINK = "/group/control_panel/manage?p_p_id=" + INSTANCE_SETTINGS + "&" + "_"
			+ INSTANCE_SETTINGS + "_factoryPid=" + PID + "&" + "_" + INSTANCE_SETTINGS
			+ "_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&" + "_" + INSTANCE_SETTINGS + "_pid=com.liferay.redirect.internal.configuration.RedirectURLConfiguration";

}
