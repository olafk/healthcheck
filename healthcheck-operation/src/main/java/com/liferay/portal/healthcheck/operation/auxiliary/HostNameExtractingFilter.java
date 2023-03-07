package com.liferay.portal.healthcheck.operation.auxiliary;

import com.liferay.portal.health.api.AccessedUrlRegister;
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
 * Keep track of all host names that were ever requested, including 
 * the scheme (http/https). These hosts will be used to check for 
 * allowed redirection of any host name used to access any content 
 * in this whole instance.
 * 
 * @author Olaf Kock
 */

@Component(
		immediate = true,
		property = {
				"before-filter=Auto Login Filter",
				"dispatcher=REQUEST",
				"servlet-context-name=",
				// Note: servlet-filter-name is used as target expression in 
				// com.liferay.portal.healthcheck.operation.RedirectHealthcheck
				"servlet-filter-name=Healthcheck Hostname Extracting Filter",
				"url-pattern=/*"
		},
		service = Filter.class
)
public class HostNameExtractingFilter extends BaseFilter implements AccessedUrlRegister {

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
		
		if(host != null && host.length() > 1) {
			HashSet<String> urls = requestedBaseUrls.get(companyId);
			if(urls == null) {
				urls = new HashSet<String>();
				requestedBaseUrls.put(companyId, urls);
			}
			urls.add(scheme + "://" + host);
		}
		super.processFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Override
	public Set<String> getAccessedUrls(long companyId) {
		return Collections.unmodifiableSet(requestedBaseUrls.get(companyId));
	}
	
	static Log _log = LogFactoryUtil.getLog(HostNameExtractingFilter.class);
	private HashMap<Long, HashSet<String>> requestedBaseUrls = new HashMap<Long, HashSet<String>>();
}
