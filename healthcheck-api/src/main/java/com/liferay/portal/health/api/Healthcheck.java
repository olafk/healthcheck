package com.liferay.portal.health.api;

import java.util.Collection;
import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Olaf Kock
 */

@ProviderType
public interface Healthcheck {
	Collection<HealthcheckItem> check(long companyId, Locale locale);
	String getCategory();
}