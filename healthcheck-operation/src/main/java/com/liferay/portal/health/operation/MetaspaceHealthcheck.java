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

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;

/**
 * DXP's default metaspace is 768M, and it might not run with less than this.
 * 
 * Make sure that sufficient Metaspace is configured.
 * 
 * @author Olaf Kock
 *
 */

@Component(service = Healthcheck.class)
public class MetaspaceHealthcheck implements Healthcheck {
	private static final String LINK = null;
	private static final String MSG = "healthcheck-max-metaspace-must-be-above-768m";

	@Override
	public Collection<HealthcheckItem> check(long companyId) {
		for (MemoryPoolMXBean memoryMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
			if ("Metaspace".equals(memoryMXBean.getName())) {
				long maxMetaspace = memoryMXBean.getUsage().getMax();
				Object[] info = { maxMetaspace };
				return Arrays.asList(new HealthcheckItem(this, maxMetaspace >= 768 * 1024 * 1024 || maxMetaspace == -1, this.getClass().getName(), LINK, MSG, info));
			}
		}
		Object[] info = { "undetected" };

		return Arrays.asList(new HealthcheckItem(this, false, this.getClass().getName(), LINK, MSG, info));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

}
