package com.liferay.health.bestpractice;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.util.PropsValues;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

/**
 * Some installations simplified the locales to just two letters for the language.
 * It is assumed that this leads to follow-up problems, as none of the OOTB languages
 * omits a country/locale. 
 * 
 * This check makes sure that available languages have at least 5 characters 
 * (e.g. "en_GB"), and are supported by the underlying JVM
 *  
 * @author Olaf Kock
 *
 */

@Component(
		service=Healthcheck.class
		)
public class AvailableLocaleConfigurationHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "https://docs.liferay.com/portal/7.4-latest/propertiesdoc/portal.properties.html#Languages%20and%20Time%20Zones";
	private static final String MSG  = "healthcheck-locale-properties";
	private static final String ERROR_MSG_LENGTH = "healthcheck-locale-properties-length";
	private static final String ERROR_MSG_DIFF = "healthcheck-locale-properties-diff";
	private static final Set<String> availableLocales = Arrays.asList(DateFormat.getAvailableLocales()).stream().map(Object::toString).collect(Collectors.toUnmodifiableSet());;

	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		LinkedList<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		Locale locale = themeDisplay.getLocale();
		validateProperties(result, locale, PropsValues.LOCALES, "locales");
		validateProperties(result, locale, PropsValues.LOCALES_ENABLED, "locales.enabled");
		validateProperties(result, locale, PropsValues.LOCALES_BETA, "locales.beta");
		
		if(result.isEmpty()) {
			result.add(create(true, locale, LINK, MSG));
		}
		return result;
	}

	private void validateProperties(LinkedList<HealthcheckItem> result, Locale locale, String[] locales, String collectionName) {
		for (int i = 0; i < locales.length; i++) {
			String loc = locales[i];
			// some JVM naming seems to be different from ours...
			String alternativeLoc = loc.replace("latin", "#Latn");
			if(loc.length()<5) {
				result.add(create(false, locale, LINK, ERROR_MSG_LENGTH, collectionName, loc));
			}
			if(! (availableLocales.contains(loc) || availableLocales.contains(alternativeLoc))) {
				result.add(create(false, locale, LINK, ERROR_MSG_DIFF, collectionName, loc));
			}
		}
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

}
