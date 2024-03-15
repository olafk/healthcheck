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

package com.liferay.health.bestpractice;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Healthcheck.class)
public class DefaultAdminUserHealthcheck implements Healthcheck {

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		try {
			User user = userLocalService.getUserByEmailAddress(companyId, "test@liferay.com");
			if (user != null) {
				String hashedPassword = PasswordEncryptorUtil.encrypt("test", user.getPassword());
				Object[] info = {};
				return Arrays.asList(new HealthcheckItem(this, !user.getPassword().equals(hashedPassword), this.getClass().getName(), LINK + LINK_PARAMETER + user.getUserId(), MSG, info));
			}
		} catch (NoSuchUserException e) {
			// ignore - this is great and exactly what we're after.
		} catch (PortalException e) {
			return Arrays.asList(new HealthcheckItem(this, e));
		}
		Object[] info = {};
		return Arrays.asList(new HealthcheckItem(this, true, this.getClass().getName(), LINK, MSG, info));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

	@Reference
	UserLocalService userLocalService;

	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_UsersAdminPortlet";
	private static final String LINK_PARAMETER = "&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_mvcRenderCommandName=%2Fusers_admin%2Fedit_user&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_p_u_i_d=";
	private static final String MSG = "healthcheck-bestpractice-default-account-with-default-password";

}
