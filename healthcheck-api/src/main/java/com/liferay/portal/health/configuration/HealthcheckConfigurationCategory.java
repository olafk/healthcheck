package com.liferay.portal.health.configuration;

import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

@Component(service=ConfigurationCategory.class)
public class HealthcheckConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return "check-square";
	}

	@Override
	public String getCategoryKey() {
		return "healthcheck";
	}

	@Override
	public String getCategorySection() {
		return "platform";
	}

}
