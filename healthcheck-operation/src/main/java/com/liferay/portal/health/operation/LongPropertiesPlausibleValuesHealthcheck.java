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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * This Healthcheck makes sure that portal*.properties configurations for long
 * values are purely set to numerical values (due to LPS-157829).
 * 
 * @author Olaf Kock
 */

@SuppressWarnings("rawtypes")
@Component(service = Healthcheck.class)
public class LongPropertiesPlausibleValuesHealthcheck extends BasePropertiesPlausibleValuesHealthcheck {

	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_server_admin_web_portlet_ServerAdminPortlet&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_mvcRenderCommandName=%2Fserver_admin%2Fview&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_tabs1=properties&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_screenNavigationCategoryKey=portal-properties";
	private static final String MSG = "healthcheck-long-properties";
	private static final String ERROR_MSG = "healthcheck-long-properties-mismatch";

	@SuppressWarnings("unchecked")
	public LongPropertiesPlausibleValuesHealthcheck() {
		super(long.class, LINK, MSG, ERROR_MSG, log);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		return check(companyId, locale,
				(value -> !(value.contains(",") || (GetterUtil.getLong(value) == 0 && !value.equals("0")))));
	}

	private static Log log = LogFactoryUtil.getLog(LongPropertiesPlausibleValuesHealthcheck.class);
}
