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
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.util.PropsUtil;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Healthcheck for relaxed-security Demo Systems Ensure that user updates don't
 * require to be approved by clicking a link that's mailed to the user. This is
 * due to demo systems rarely having a valid mail server set up.
 * 
 * @author Olaf Kock
 */

@Component(service = Healthcheck.class)

public class DontRequireMailForUserUpdatesHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "https://docs.liferay.com/portal/7.4-latest/propertiesdoc/portal.properties.html#Company";
	private static final String MSG = "healthcheck-user-change-does-not-need-to-be-mailvalidated";
	private static final String MSG_ERROR = "healthcheck-user-change-needs-to-be-mailvalidated";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		try {
			boolean verifyStrangers = GetterUtil.getBoolean(PropsUtil.get(companyLocalService.getCompany(companyId),
					PropsKeys.COMPANY_SECURITY_STRANGERS_VERIFY));
			return wrap(create(!verifyStrangers, locale, LINK, verifyStrangers ? MSG_ERROR : MSG));
		} catch (PortalException e) {
			return wrap(create(this, locale, e));
		}
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-demosystem";
	}

	@Reference
	CompanyLocalService companyLocalService;
}
