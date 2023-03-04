package com.liferay.health.bestpractice;

import com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(
		configurationPid = "com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration",
		service=Healthcheck.class
		)
public class PasswordHashHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "https://learn.liferay.com/reference/latest/en/dxp/propertiesdoc/portal.properties.html#Passwords";
	private static final String MSG = "healthcheck-password-hashing-rounds-owasp-recommendation";
	private Long owaspHashingRecommendation;

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		String hashingAlgorithm = PropsUtil.get("passwords.encryption.algorithm");
		if(hashingAlgorithm != null && hashingAlgorithm.startsWith("PBKDF2WithHmacSHA1")) {
			int roundsPos = hashingAlgorithm.lastIndexOf('/');
			int rounds = Integer.parseInt(hashingAlgorithm.substring(roundsPos + 1));
			return wrap(create(
					rounds>= owaspHashingRecommendation, 
					locale, LINK, MSG, 
					rounds, owaspHashingRecommendation));
		}
		return Collections.emptyList();
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		HealthcheckBestPracticeConfiguration config = ConfigurableUtil.createConfigurable(HealthcheckBestPracticeConfiguration.class, properties);
		owaspHashingRecommendation = config.owaspHashingRecommendation();
	}
}
