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
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.operation.auxiliary.HostNameExtractingFilter;
import com.liferay.portal.kernel.util.HtmlUtil;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import javax.servlet.Filter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * It's 2022 (when I write this check). Make sure we're accessed through https
 * only. Unless we're on localhost
 * 
 * @author Olaf Kock
 */
@Component(service = Healthcheck.class)
public class HttpsHealthcheck extends HealthcheckBaseImpl {
	private static final String LINK = null;
	private static final String MSG = "healthcheck-https-in-year-x";
	private static final String MSG_LOCALHOST = "healthcheck-https-localhost-in-year-x";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		String year = "" + Calendar.getInstance().get(Calendar.YEAR); // just for rubbing it in in the message
		Set<String> urls = filter.getAccessedUrls(companyId);
		Collection<HealthcheckItem> result = new LinkedList<HealthcheckItem>();

		for (String requestedUrl : urls) {
			String scheme = extractScheme(requestedUrl);
			String host = extractHost(requestedUrl);

			if (host != null && (host.equalsIgnoreCase("localhost") || host.toLowerCase().startsWith("localhost:"))) {
				Object[] info = { year, scheme };
				result.add(new HealthcheckItem(this, true, this.getClass().getName(), LINK, MSG_LOCALHOST, info));
			} else {
				result.add(new HealthcheckItem(this, scheme != null && scheme.equalsIgnoreCase("https"),
						this.getClass().getName() + "-" + HtmlUtil.escapeURL(requestedUrl), LINK, MSG, year,
						requestedUrl));
			}
		}
		return result;
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

	private String extractScheme(String url) {
		if (url == null) {
			return "null";
		}
		int separatorIndex = url.indexOf("://");
		if (separatorIndex < 1) { // not found, and should have a scheme leading up to it
			return "???";
		}
		return HtmlUtil.escape(url.substring(0, separatorIndex));
	}

}
