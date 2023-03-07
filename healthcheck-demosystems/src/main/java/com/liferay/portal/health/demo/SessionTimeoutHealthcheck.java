package com.liferay.portal.health.demo;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.util.PropsValues;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * Healthcheck for relaxed-security Demo Systems
 * Demo Systems should have the session extended as long as a browser
 * is open, to cater for longer Q&A sessions without any interruption 
 * when the logout-message is missed.
 *  
 * @author Olaf Kock
 */
@Component(
	immediate = true,
	property = {
		// TODO enter required service properties
	},
	service = Healthcheck.class
)
public class SessionTimeoutHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "https://docs.liferay.com/portal/7.3-latest/propertiesdoc/portal.properties.html#Session";
	private static final String MSG = "healthcheck-session-extension-enabled";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		boolean autoextend = PropsValues.SESSION_TIMEOUT_AUTO_EXTEND;
		return wrap(create(autoextend, locale, LINK, MSG, "session.timeout.auto.extend"));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-demosystem";
	}
}