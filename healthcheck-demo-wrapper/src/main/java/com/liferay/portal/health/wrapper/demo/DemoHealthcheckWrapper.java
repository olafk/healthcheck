package com.liferay.portal.health.wrapper.demo;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.sales.checklist.api.ChecklistItem;
import com.liferay.sales.checklist.api.ChecklistProvider;

import java.util.ArrayList;
import java.util.Collection;

public class DemoHealthcheckWrapper implements Healthcheck {

	private ChecklistProvider provider;

	public DemoHealthcheckWrapper(ChecklistProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		ChecklistItem item = provider.check(themeDisplay);
		ArrayList<HealthcheckItem> result = new ArrayList<HealthcheckItem>();
		result.add(new DemoHealthcheckItemWrapper(item));
		return result;
	}

	@Override
	public String getCategory() {
		return "demo";
	}

	public ChecklistProvider getWrappee() {
		return provider;
	}

}
