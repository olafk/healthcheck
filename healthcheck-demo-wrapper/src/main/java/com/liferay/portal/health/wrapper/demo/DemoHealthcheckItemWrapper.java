package com.liferay.portal.health.wrapper.demo;

import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.sales.checklist.api.ChecklistItem;

public class DemoHealthcheckItemWrapper implements HealthcheckItem {

	private ChecklistItem item;

	public DemoHealthcheckItemWrapper(ChecklistItem result) {
		this.item = result;
	}

	@Override
	public boolean isResolved() {
		return item.isResolved();
	}

	@Override
	public String getMessage() {
		return item.getMessage();
	}

	@Override
	public String getLink() {
		return item.getLink();
	}

	@Override
	public String getCategory() {
		return "demo";
	}

}
