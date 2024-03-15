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
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * untested implementation - just an idea for what can be checked. As I don't
 * have a GC File Store, the implementation is extremely simple.
 * 
 * @author Olaf Kock
 */
@Component(service = Healthcheck.class)
public class GoogleFileStoreConfigurationHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "";
	private static final String ERROR_MSG = "";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		LinkedList<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		if (PropsValues.DL_STORE_IMPL.equals("com.liferay.portal.store.gcs.GCSStore")) {
			String key = PropsUtil.get("dl.store.gcs.aes256.key");
			// base64 encoded 256bit AES keys are 44 characters long
			// <i>could</i> test-decode to see if it's valid base64...
			if (key == null || key.length() != 44) {
				Object[] info = {};
				result.add(new HealthcheckItem(this, false, this.getClass().getName(), LINK, ERROR_MSG, info));
			}
		}
		return result;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

}
