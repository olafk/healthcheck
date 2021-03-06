package com.liferay.portal.healthcheck.operation;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Calendar;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;

@Component(
		service=Healthcheck.class
		)
public class HttpsHealthcheck extends HealthcheckBaseImpl {
	private static final String LINK = null;
	private static final String MSG = "healthcheck-https";
	private static final String MSG_LOCALHOST = "healthcheck-https-localhost";

	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		String year = "" + Calendar.getInstance().get(Calendar.YEAR);
		String scheme = themeDisplay.getRequest().getScheme();
		String host = themeDisplay.getRequest().getHeader("Host");
		if(host != null && 
				(host.equalsIgnoreCase("localhost") || 
				 host.toLowerCase().startsWith("localhost:"))) {
			return wrap(create(true, themeDisplay.getLocale(), LINK, MSG_LOCALHOST, year, scheme));
		}
        return wrap(create(
        		scheme!=null && scheme.equalsIgnoreCase("https"), 
        		themeDisplay.getLocale(), LINK, MSG, year, scheme));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}
}
