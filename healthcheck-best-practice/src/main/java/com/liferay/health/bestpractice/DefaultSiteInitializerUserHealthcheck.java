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
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Healthcheck.class)
public class DefaultSiteInitializerUserHealthcheck extends HealthcheckBaseImpl {

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		Collection<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		for (String user : KNOWN_USERS) {
			result.addAll(checkForUser(companyId, locale, user));
		}
		if (result.isEmpty()) {
			result.add(create1(true, locale, null, MSG));
		}
		return result;
	}

	private Collection<HealthcheckItem> checkForUser(long companyId, Locale locale, String mailAddress) {
		try {
			User user = userLocalService.getUserByEmailAddress(companyId, mailAddress);
			if (user != null) {
				return wrap(create1(false, locale, LINK + LINK_PARAMETER + user.getUserId(), MSG_FOUND, mailAddress));
			}
		} catch (NoSuchUserException e) {
			// ignore - this is great and exactly what we're after.
		} catch (PortalException e) {
			return wrap(create3(this, locale, e));
		}
		return new LinkedList<HealthcheckItem>();
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

	@Reference
	UserLocalService userLocalService;

	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_UsersAdminPortlet";
	private static final String LINK_PARAMETER = "&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_mvcRenderCommandName=%2Fusers_admin%2Fedit_user&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_p_u_i_d=";
	private static final String MSG = "healthcheck-bestpractice-siteinitializer-user";
	private static final String MSG_FOUND = "healthcheck-bestpractice-siteinitializer-user-found";
	private static String[] KNOWN_USERS = { "test.user1@liferay.com", "test.user2.update@liferay.com",
			"test.user3@liferay.com", "test.user1@liferay.com", "test.user2@liferay.com",
			"scott.producer@mailinator.com", "marie.producer@mailinator.com", "ryan.underwriter@mailinator.com",
			"clark.insured@mailinator.com", "administrator@testray.com", "analyst@testray.com", "lead@testray.com",
			"user@testray.com", "j@acme.com", "s@acme.com", "john.developer@mailinator.com",
			"marie.developer@mailinator.com", "ryan.administrator@mailinator.com", "clark.customer@mailinator.com",
			"pm@partner.com", "pmu@partner.com", "psu@partner.com", "ptu@partner.com", "cam@liferaytest.com",
			"com@liferaytest.com", "cmm@liferaytest.com", "cmd@liferaytest.com", "cfm@liferaytest.com",
			"cem@liferaytest.com", "test@liferay.com", "employee@liferay.com", "manager@liferay.com",
			"finance@liferay.com" };
}
