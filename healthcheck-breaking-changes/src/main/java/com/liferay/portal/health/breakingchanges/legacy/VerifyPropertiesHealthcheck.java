package com.liferay.portal.health.breakingchanges.legacy;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.breakingchanges.legacy.copied.VerifyPropertiesQ42Y2023;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

@Component(service = Healthcheck.class)
public class VerifyPropertiesHealthcheck implements Healthcheck {

	private static final String MSG = "healthcheck-verify-properties-success";

	@Override
	public Collection<HealthcheckItem> check(long companyId) {
		Collection<HealthcheckItem> items = VerifyPropertiesQ42Y2023.doVerify(this);
		if (items.isEmpty()) {
			// todo: Translate!
			Object[] info = {};
			items.add(new HealthcheckItem(this, true, this.getClass().getName(), null, MSG, info));
		}
		return items;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-breaking-changes";
	}

}
