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

import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalServiceUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(
		configurationPid = "com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration",
		service=Healthcheck.class
		)
public class ClientExtensionHostHealthcheck extends HealthcheckBaseImpl {

	private final static String LINK_BASE = "/group/guest/~/control_panel/manage?p_p_id=com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet&p_p_lifecycle=0&_com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet_mvcRenderCommandName=%2Fclient_extension_admin%2Fedit_client_extension_entry&_com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet_externalReferenceCode=";
	private final static String MSG = "healthcheck-client-extension-host";
	private final static String MSG_WHITELISTED = "healthcheck-client-extension-host-whitelisted";

	public ClientExtensionHostHealthcheck() {
	}

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		LinkedList<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		try {
			List<ClientExtensionEntry> clientExtensionEntries = ClientExtensionEntryLocalServiceUtil.getClientExtensionEntries(companyId, 0, 9999);
			String virtualHostname = companyLocalService.getCompany(companyId).getVirtualHostname();
			for (ClientExtensionEntry clientExtensionEntry : clientExtensionEntries) {
				UnicodeProperties typeSettings =
						UnicodePropertiesBuilder.create(
							true
						).load(
							clientExtensionEntry.getTypeSettings()
						).build();
				String[] urls;
				String tsUrl = typeSettings.get("url");
				if(tsUrl==null) {
					String multilineUrls = typeSettings.get("urls");
					String multilineCssUrls = typeSettings.get("cssURLs");
					if(multilineCssUrls != null && multilineCssUrls.length()>5) {
						multilineUrls += "\n" + multilineCssUrls;
					}
					urls = StringUtil.split(multilineUrls, '\n');
				} else {
					urls = new String[] {tsUrl};
				}
				for (String url : urls) {
					String host = getHost(url);
					if(host != null) {
						if(hostWhitelists.contains(host)) {
							result.add(
								create(
									true, 
									this.getClass().getName() + "-" + virtualHostname + "-" + host, 
									locale, 
									LINK_BASE + clientExtensionEntry.getExternalReferenceCode(),
									MSG_WHITELISTED, 
									clientExtensionEntry.getName(locale),
									host));
						} else {
							// create a problem indicator in any case, so that users can
							// ignore it if the URL is expected. The ignore-key includes the 
							// ignored host name and the current company host name, so that
							// any system restored under a different name (when company-virtualhost
							// is configured correctly) will trigger new alerts.
							// Note: There is a separate health check to validate the company
							// virtualhost, that should make this unignorable.
							
							result.add(
								create(
									false, 
									this.getClass().getName() + "-" + virtualHostname + "-" + host, 
									locale, 
									LINK_BASE + clientExtensionEntry.getExternalReferenceCode(),
									MSG, 
									clientExtensionEntry.getName(locale),
									host));
						}
					} else {
						if(url.startsWith("/document")) {
						    result.add(
								create(true, 
									this.getClass().getName()+"-"+url, 
									locale, 
									LINK_BASE + clientExtensionEntry.getExternalReferenceCode(),
									"healthcheck-client-extension-local-doclib",
									clientExtensionEntry.getName(locale),
									url));
						} else {
						    result.add(
								create(false, 
									this.getClass().getName()+"-"+url, 
									locale, 
									LINK_BASE + clientExtensionEntry.getExternalReferenceCode(),
									"healthcheck-client-extension-undetectable-host",
									clientExtensionEntry.getName(locale),
									url));
						}
					}
				}
			}
			if(result.isEmpty()) {
				result.add(create(
					true, 
					locale, 
					null, 
					"healthcheck-client-extension-none-detected"));
			}
		} catch (PortalException e) {
			_log.error(e);
			result.add(create(this, locale, e));
		}
		return result;
	}

	private String getHost(String url) {
		if(url != null && url.startsWith("http")) {
			int endOfHost = url.indexOf('/', 8);
			if(endOfHost>-1) {
				return url.substring(0, endOfHost);
			}
		}
		return null;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		HealthcheckOperationalConfiguration config = ConfigurableUtil.createConfigurable(HealthcheckOperationalConfiguration.class, properties);
		hostWhitelists = new HashSet<>(Arrays.asList(config.clientExtensionHostWhitelist()));
	}
	
	@Reference
	protected CompanyLocalService companyLocalService;
	
	
	static Log _log = LogFactoryUtil.getLog(ClientExtensionHostHealthcheck.class);
	private Set<String> hostWhitelists;
}
