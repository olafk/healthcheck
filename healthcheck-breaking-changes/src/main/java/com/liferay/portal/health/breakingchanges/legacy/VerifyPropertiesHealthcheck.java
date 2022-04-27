package com.liferay.portal.health.breakingchanges.legacy;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.api.HealthcheckItemImpl;
import com.liferay.portal.health.breakingchanges.legacy.copied.VerifyProperties;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

@Component(
		service=Healthcheck.class
		)
public class VerifyPropertiesHealthcheck extends HealthcheckBaseImpl {

	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		String localizedCategory = lookupMessage(themeDisplay.getLocale(), getCategory());
		VerifyProperties vp = new VerifyProperties(localizedCategory);
		Collection<HealthcheckItem> items = vp.doVerify();
		if(items.isEmpty()) {
			// todo: Translate!
			items.add(new HealthcheckItemImpl(true, "Verify Properties: Success", null, localizedCategory));
		}
		return items;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-breaking-changes";
	}

}
