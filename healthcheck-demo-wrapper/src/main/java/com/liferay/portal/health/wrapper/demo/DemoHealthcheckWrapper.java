package com.liferay.portal.health.wrapper.demo;

import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.sales.checklist.api.ChecklistItem;
import com.liferay.sales.checklist.api.ChecklistProvider;

import java.util.Collection;

public class DemoHealthcheckWrapper extends HealthcheckBaseImpl {

	private ChecklistProvider provider;

	public DemoHealthcheckWrapper(ChecklistProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		ChecklistItem item = provider.check(themeDisplay);
		return wrap(new DemoHealthcheckItemWrapper(item, lookupMessage(themeDisplay.getLocale(), getCategory())));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-demo";
	}

	public ChecklistProvider getWrappee() {
		return provider;
	}

}
