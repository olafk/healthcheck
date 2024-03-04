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

package com.liferay.portal.health.operation.auxiliary;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * Keep track of all host names that were ever requested, including the scheme
 * (http/https). These hosts will be used to check for allowed redirection of
 * any host name used to access any content in this whole instance.
 * 
 * @author Olaf Kock
 */

@Component(immediate = true, property = { "before-filter=Auto Login Filter", "dispatcher=REQUEST",
		"servlet-context-name=",
		// Note: servlet-filter-name is used as target expression in
		// com.liferay.portal.health.operation.RedirectHealthcheck
		"servlet-filter-name=Healthcheck Hostname Extracting Filter", "url-pattern=/*" }, service = Filter.class)
public class HostNameExtractingFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws Exception {
		String host = httpServletRequest.getServerName();
		String scheme = httpServletRequest.getScheme();
		long companyId = PortalUtil.getCompanyId(httpServletRequest);

		if (host != null && host.length() > 1) {
			HashSet<String> urls = requestedBaseUrls.get(companyId);
			if (urls == null) {
				urls = new HashSet<String>();
				requestedBaseUrls.put(companyId, urls);
			}
			urls.add(scheme + "://" + host);
		}
		super.processFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	public Set<String> getAccessedUrls(long companyId) {
		return Collections.unmodifiableSet(requestedBaseUrls.get(companyId));
	}

	static Log _log = LogFactoryUtil.getLog(HostNameExtractingFilter.class);
	private HashMap<Long, HashSet<String>> requestedBaseUrls = new HashMap<Long, HashSet<String>>();
}
