package com.liferay.portal.health.wrapper.commerce;

import com.liferay.commerce.health.status.CommerceHealthHttpStatus;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.api.HealthcheckItemImpl;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class CommerceHealthcheckWrapper extends HealthcheckBaseImpl {

	public CommerceHealthcheckWrapper(CommerceHealthHttpStatus status) {
		this.status = status;
	}
	
	public void setCommerceChannelLocalService(CommerceChannelLocalService commerceChannelLocalService) {
		this.commerceChannelLocalService = commerceChannelLocalService;
	}
	
	
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		long companyId = themeDisplay.getCompanyId();
		List<CommerceChannel> commerceChannels = commerceChannelLocalService.getCommerceChannels(companyId);
		ArrayList<HealthcheckItem> result = new ArrayList<HealthcheckItem>(commerceChannels.size());
		for (CommerceChannel channel : commerceChannels) {
			result.add(createResult(status, companyId, channel, themeDisplay.getLocale()));
		}
		return result;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-commerce";
	}
	
	public CommerceHealthHttpStatus getWrappee() {
		return status;
	}

	private HealthcheckItem createResult(CommerceHealthHttpStatus status, long companyId, CommerceChannel channel, Locale locale) {
		boolean resolved = false;
		String exception = "";
		try {
			resolved = status.isFixed(companyId, channel.getCommerceChannelId());
		} catch (Exception e) {
			exception = e.getClass().getName() + " " + e.getMessage();
		}
		String message = status.getName(locale) + " - " + status.getDescription(locale) + " (" + channel.getName() + ") " + exception;
		String link = "/group/control_panel/manage?p_p_id=com_liferay_commerce_health_status_web_internal_portlet_CommerceHealthCheckPortlet";
		
		return new HealthcheckItemImpl(resolved, message, link, lookupMessage(locale, getCategory()));
	}

	private CommerceChannelLocalService commerceChannelLocalService;
	private CommerceHealthHttpStatus status;
}
