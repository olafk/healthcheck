package com.liferay.portal.health.wrapper.demo;

import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.sales.checklist.api.ChecklistItem;
import com.liferay.sales.checklist.api.ChecklistProvider;

import java.util.Collection;
import java.util.Locale;

public class DemoHealthcheckWrapper extends HealthcheckBaseImpl {

	private ChecklistProvider provider;
	private CompanyLocalService companyLocalService;

	public DemoHealthcheckWrapper(ChecklistProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		try {
			ThemeDisplay themeDisplay = new ThemeDisplay();
			themeDisplay.setCompany(companyLocalService.getCompany(companyId));
			themeDisplay.setLocale(locale);
			ChecklistItem item = provider.check(themeDisplay);
			return wrap(new DemoHealthcheckItemWrapper(item, lookupMessage(locale, getCategory())));
		} catch (PortalException e) {
			return wrap(create(this, locale, e));
		}
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-demo";
	}

	public ChecklistProvider getWrappee() {
		return provider;
	}

	public void setCompanyLocalService(CompanyLocalService companyLocalService) {
		this.companyLocalService = companyLocalService;
	}

}
