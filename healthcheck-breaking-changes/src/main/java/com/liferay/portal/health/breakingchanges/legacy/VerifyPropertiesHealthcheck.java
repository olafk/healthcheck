package com.liferay.portal.health.breakingchanges.legacy;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.breakingchanges.legacy.copied.VerifyProperties55;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

@Component(
		service=Healthcheck.class
		)
public class VerifyPropertiesHealthcheck extends HealthcheckBaseImpl {

	private static final String MSG = "healthcheck-verify-properties-success";
	
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		String localizedCategory = lookupMessage(themeDisplay.getLocale(), getCategory());
		
		Collection<HealthcheckItem> items = VerifyProperties55.doVerify(localizedCategory);
		if(items.isEmpty()) {
			// todo: Translate!
			items.add(create(true, themeDisplay.getLocale(), null, MSG));
		}
		return items;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-breaking-changes";
	}

}
