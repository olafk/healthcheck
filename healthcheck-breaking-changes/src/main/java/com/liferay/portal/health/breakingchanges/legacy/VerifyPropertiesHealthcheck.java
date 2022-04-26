package com.liferay.portal.health.breakingchanges.legacy;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.api.HealthcheckItemImpl;
import com.liferay.portal.health.breakingchanges.legacy.copied.VerifyProperties;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

@Component
public class VerifyPropertiesHealthcheck implements Healthcheck {

	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		VerifyProperties vp = new VerifyProperties();
		Collection<HealthcheckItem> items = vp.doVerify();
		if(items.isEmpty()) {
			// todo: Translate!
			items.add(new HealthcheckItemImpl(true, "Verify Properties: Success", null, getCategory()));
		}
		return items;
	}

	@Override
	public String getCategory() {
		return "breaking-changes";
	}

}
