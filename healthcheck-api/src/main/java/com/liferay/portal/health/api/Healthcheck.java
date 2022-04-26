package com.liferay.portal.health.api;

import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collection;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Olaf Kock
 */

@ProviderType
public interface Healthcheck {
	Collection<HealthcheckItem> check(ThemeDisplay themeDisplay);
	String getCategory();
}