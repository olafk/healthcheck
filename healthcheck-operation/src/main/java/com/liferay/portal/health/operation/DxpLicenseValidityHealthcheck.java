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

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(configurationPid = "com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration", service = Healthcheck.class)
public class DxpLicenseValidityHealthcheck extends HealthcheckBaseImpl {

	private int warningPeriod = 90;

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		if (ReleaseInfo.isDXP()) {
			Map<String, String> licenseProperties = LicenseManagerUtil.getLicenseProperties("Portal");
			long expires = Long.valueOf(licenseProperties.get("expirationDate"));
			long now = new Date().getTime();
			long remainingMillis = expires - now;
			long remainingDays = remainingMillis / (1000 * 60 * 60 * 24);

			return wrap(
					create(remainingDays > warningPeriod, this.getClass().getName() + "-" + ((int) (remainingDays / 7)),
							locale, null, "healthcheck-license-key-validity-period", remainingDays, warningPeriod));
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		HealthcheckOperationalConfiguration config = ConfigurableUtil
				.createConfigurable(HealthcheckOperationalConfiguration.class, properties);
		warningPeriod = config.remainingActivationPeriod();
	}
}
