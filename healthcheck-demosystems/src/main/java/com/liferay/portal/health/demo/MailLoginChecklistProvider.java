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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.Collection;
import java.util.Locale;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Healthcheck for relaxed-security Demo Systems Ensure that the login field is
 * not prepopulated with the company mail domain. This enables password managers
 * to easily suggest known accounts. Configuration can be achieved through
 * disabling prepopulation, or through configuring login through screen name.
 * 
 * @author Olaf Kock
 */

@Component(service = Healthcheck.class)

public class MailLoginChecklistProvider extends HealthcheckBaseImpl {

	private static final String MSG = "healthcheck-login-is-not-prepopulated";
	private static final String MSG_ERROR = "healthcheck-login-is-prepopulated";
	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fview_configuration_screen&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_configurationScreenKey=general-authentication";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		PortletPreferences preferences = PrefsPropsUtil.getPreferences(companyId);
		try {
			Company company = companyLocalService.getCompany(companyId);
			boolean prepopulate = _getPrefsPropsBoolean(preferences, company,
					PropsKeys.COMPANY_LOGIN_PREPOPULATE_DOMAIN, PropsValues.COMPANY_LOGIN_PREPOPULATE_DOMAIN);
			;
			String authType = _getPrefsPropsString(preferences, company, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
					PropsValues.COMPANY_SECURITY_AUTH_TYPE);
			prepopulate &= "emailAddress".equals(authType);
			Object[] info = { "company.login.prepopulate.domain" };

			return wrap(new HealthcheckItem(this, !prepopulate, this.getClass().getName(), LINK, prepopulate ? MSG_ERROR : MSG, info));
		} catch (PortalException e) {
			return wrap(create3(this, locale, e));
		}
	}

	private static boolean _getPrefsPropsBoolean(PortletPreferences portletPreferences, Company company, String name,
			boolean defaultValue) {

		String value = portletPreferences.getValue(name, PropsUtil.get(company, name));

		if (value != null) {
			return GetterUtil.getBoolean(value);
		}

		return defaultValue;
	}

	private static String _getPrefsPropsString(PortletPreferences portletPreferences, Company company, String name,
			String defaultValue) {

		String value = portletPreferences.getValue(name, PropsUtil.get(company, name));

		if (value != null) {
			return value;
		}

		return defaultValue;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-demosystem";
	}

	@Reference
	CompanyLocalService companyLocalService;
}
