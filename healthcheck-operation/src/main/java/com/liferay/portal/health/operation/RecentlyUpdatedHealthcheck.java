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
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration;
import com.liferay.portal.kernel.util.ReleaseInfo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(configurationPid = "com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration", service = Healthcheck.class)
public class RecentlyUpdatedHealthcheck implements Healthcheck {

	private int acceptableMissingUpdates;
	private int acceptableAgeInQuarters;
	private static final String QUARTERLY_PATTERN = "^(\\d{4})\\.Q(\\d)\\.(\\d)$";

	@Override
	public Collection<HealthcheckItem> check(long companyId) {
		final String version = ReleaseInfo.getVersionDisplayName();
		final String term;
		String message;
		if (ReleaseInfo.isDXP()) {
			term = "Update ";
			message = "healthcheck-recently-updated-dxp";
		} else {
			term = "CE GA";
			message = "healthcheck-recently-updated-ce";
		}

		int updatePos = version.indexOf(term);
		if (updatePos > 0) {
			int update = Integer.parseInt(version.substring(updatePos + (term.length())));
			int currentExpectedUpdate = guessCurrentlyExpectedUpdate();
			int expectedActualUpdate = currentExpectedUpdate - acceptableMissingUpdates;
			Object[] info = { update, expectedActualUpdate };
			return Arrays.asList(new HealthcheckItem(this, update > expectedActualUpdate, this.getClass().getName() + "-" + expectedActualUpdate, null, message, info));
		} else {
			// might be a quarterly release, e.g. "2023.Q4.1"
			Pattern pattern = Pattern.compile(QUARTERLY_PATTERN);
			Matcher matcher = pattern.matcher(version);
			if (matcher.matches()) {
				int year = Integer.valueOf(matcher.group(1));
				int quarter = Integer.valueOf(matcher.group(2));
				int patch = Integer.valueOf(matcher.group(3));
				message = "healthcheck-recent-quarterly-dxp";

				int ageInQuarters = getAgeInQuarters(year, quarter, patch);
				Object[] info = { version, acceptableAgeInQuarters, ageInQuarters };
				return Arrays.asList(new HealthcheckItem(this, ageInQuarters <= acceptableAgeInQuarters, this.getClass().getName(), null, message, info));
			} else {
				Object[] info = { version };
				return Arrays.asList(new HealthcheckItem(this, false, this.getClass().getName(), null, "healthcheck-recently-updated-couldnt-compute", info));

			}
		}
	}

	private int getAgeInQuarters(int releaseYear, int releaseQuarter, int patch) {
		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();
		int month = now.getMonthValue();
		int currentQuarter = 1 + ((int) ((month - 1) / 3));

		int ageInQuarters = (currentYear - releaseYear) * 4 + (currentQuarter - releaseQuarter - 1);

		return ageInQuarters;
	}

	private int guessCurrentlyExpectedUpdate() {
		// Update 15 was released on 11.March 2022 - assuming weekly releases since
		// then, which
		// held true until U69 at the time of writing this code
		// ignore all timezone magic and Date/Time Math Elegance:
		// We're calculating in the granularity of weeks
		LocalDate u15rel = LocalDate.of(2022, 3, 11);
		LocalDate now = LocalDate.now();
		long timePassed = ChronoUnit.DAYS.between(u15rel, now);
		return (int) (timePassed / 7) + 15;
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
		acceptableMissingUpdates = config.acceptableMissingUpdates();
		acceptableAgeInQuarters = config.acceptableAgeInQuarters();
	}
}
