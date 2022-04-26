package com.liferay.portal.health.wrapper.commerce;

import com.liferay.commerce.health.status.CommerceHealthHttpStatus;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.api.HealthcheckItemImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class CommerceHealthcheckWrapper implements Healthcheck {

	public CommerceHealthcheckWrapper(CommerceHealthHttpStatus status) {
		this.status = status;
	}
	
	public void setCommerceChannelLocalService(CommerceChannelLocalService commerceChannelLocalService) {
		this.commerceChannelLocalService = commerceChannelLocalService;
	}
	
	
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		ArrayList<HealthcheckItem> result = new ArrayList<HealthcheckItem>();
		long companyId = themeDisplay.getCompanyId();
		List<CommerceChannel> commerceChannels = commerceChannelLocalService.getCommerceChannels(companyId);
		for (CommerceChannel channel : commerceChannels) {
			result.add(createResult(status, companyId, channel, themeDisplay.getLocale()));
		}
		return result;
	}

	@Override
	public String getCategory() {
		return "demo";
	}
	
	public CommerceHealthHttpStatus getWrappee() {
		return status;
	}

	private CommerceChannelLocalService commerceChannelLocalService;
	private CommerceHealthHttpStatus status;

	private HealthcheckItem createResult(CommerceHealthHttpStatus status, long companyId, CommerceChannel channel, Locale locale) {
		boolean resolved = false;
		String exception = "";
		try {
			// At the time of writing this class, WishListContentCommerceHealthHttpStatus is not enabled, but
			// I don't know how to programmatically figure this out - as calling isFixed causes a very noisy 
			// stacktrace, for now I'm deactivating this check in a stupid and hardcoded way
			if(! status.getClass().getName().equals("com.liferay.commerce.wish.list.web.internal.health.status.WishListContentCommerceHealthHttpStatus"))
				resolved = status.isFixed(companyId, channel.getCommerceChannelId());
		} catch (Exception e) {
			exception = e.getClass().getName() + " " + e.getMessage();
		}
		String message = status.getName(locale) + " " + status.getDescription(locale) + " " + channel.getName() + " " + exception;
		String link = null;
		String category = "commerce";
		
		return new HealthcheckItemImpl(resolved, message, link, category);
	}
}
