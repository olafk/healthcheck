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

package com.liferay.portal.health.wrapper.commerce;

import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class CommerceHealthcheckWrapper implements Healthcheck {

	public CommerceHealthcheckWrapper(CommerceHealthStatus status) {
		this.status = status;
	}

	public void setCommerceChannelLocalService(CommerceChannelLocalService commerceChannelLocalService) {
		this.commerceChannelLocalService = commerceChannelLocalService;
	}

	@Override
	public Collection<HealthcheckItem> check(long companyId) {
		Locale locale = getDefaultLocale(companyId);
		List<CommerceChannel> commerceChannels = commerceChannelLocalService.getCommerceChannels(companyId);
		_log.info("Executing Commerce Healthcheck " + getWrappee().getClass().getSimpleName() + " for "
				+ commerceChannels.size() + " channel(s)");
		ArrayList<HealthcheckItem> result = new ArrayList<HealthcheckItem>(commerceChannels.size());
		for (CommerceChannel channel : commerceChannels) {
			result.add(createResult(status, companyId, channel, locale));
		}
		return result;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-commerce";
	}

	public CommerceHealthStatus getWrappee() {
		return status;
	}

	private HealthcheckItem createResult(CommerceHealthStatus status, long companyId, CommerceChannel channel,
			Locale locale) {
		boolean resolved = false;
		String exception = "";
		try {
			resolved = status.isFixed(companyId, channel.getCommerceChannelId());
		} catch (Exception e) {
			exception = e.getClass().getName() + " " + e.getMessage();
		}
		String message = status.getName(locale) + " - " + status.getDescription(locale) + " (" + channel.getName()
				+ ") " + exception;
		String link = "/group/control_panel/manage?p_p_id=com_liferay_commerce_health_status_web_internal_portlet_CommerceHealthCheckPortlet";

		return new HealthcheckItem(this, resolved, this.getClass().getName(), link, message);
	}
	
	private Locale getDefaultLocale(long companyId) {
		try {
			return CompanyLocalServiceUtil.getCompany(companyId).getLocale();
		} catch (PortalException e) {
			return Locale.US;
		}
	}

	private CommerceChannelLocalService commerceChannelLocalService;
	private CommerceHealthStatus status;

	static Log _log = LogFactoryUtil.getLog(CommerceHealthcheckWrapper.class);

}
