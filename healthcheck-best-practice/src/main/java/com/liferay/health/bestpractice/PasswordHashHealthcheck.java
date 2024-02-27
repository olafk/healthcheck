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

import com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(configurationPid = "com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration", service = Healthcheck.class)
public class PasswordHashHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "https://learn.liferay.com/reference/latest/en/dxp/propertiesdoc/portal.properties.html#Passwords";
	private static final String MSG = "healthcheck-password-hashing-rounds-owasp-recommendation";
	private Long owaspHashingRecommendation;

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		String hashingAlgorithm = PropsUtil.get("passwords.encryption.algorithm");
		if (hashingAlgorithm != null && hashingAlgorithm.startsWith("PBKDF2WithHmacSHA1")) {
			int roundsPos = hashingAlgorithm.lastIndexOf('/');
			int rounds = Integer.parseInt(hashingAlgorithm.substring(roundsPos + 1));
			return wrap(create(rounds >= owaspHashingRecommendation, locale, LINK, MSG, rounds,
					owaspHashingRecommendation));
		}
		return Collections.emptyList();
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		HealthcheckBestPracticeConfiguration config = ConfigurableUtil
				.createConfigurable(HealthcheckBestPracticeConfiguration.class, properties);
		owaspHashingRecommendation = config.owaspHashingRecommendation();
	}
}
