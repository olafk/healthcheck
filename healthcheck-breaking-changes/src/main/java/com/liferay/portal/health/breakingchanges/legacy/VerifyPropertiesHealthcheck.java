package com.liferay.portal.health.breakingchanges.legacy;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.breakingchanges.legacy.copied.VerifyProperties66;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

@Component(
		service=Healthcheck.class
		)
public class VerifyPropertiesHealthcheck extends HealthcheckBaseImpl {

	private static final String MSG = "healthcheck-verify-properties-success";
	
	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		String localizedCategory = lookupMessage(locale, getCategory());
		
		Collection<HealthcheckItem> items = VerifyProperties66.doVerify(localizedCategory);
		if(items.isEmpty()) {
			// todo: Translate!
			items.add(create(true, locale, null, MSG));
		}
		return items;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-breaking-changes";
	}

}
