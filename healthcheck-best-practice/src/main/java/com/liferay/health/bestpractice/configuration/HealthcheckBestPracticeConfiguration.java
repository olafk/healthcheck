package com.liferay.health.bestpractice.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(category = "healthcheck")
@Meta.OCD(
		id = "com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration"
	    , localization = "content/Language"
	    , name = "healthcheck-best-practice-configuration-name"
	    , description = "healthcheck-best-practice-configuration-description"
	)
public interface HealthcheckBestPracticeConfiguration {
	@Meta.AD(
            deflt = "500",
			description = "healthcheck-best-practice-maximum-simple-store-files-description",
            name = "healthcheck-best-practice-maximum-simple-store-files-name",
            required = false
        )
	public Integer maximumSimpleStoreFiles();
	
	@Meta.AD(
            deflt = "5000000",
			description = "healthcheck-best-practice-doclib-minimum-usable-space-description",
            name = "healthcheck-best-practice-doclib-minimum-usable-space-name",
            required = false
        )
	public Long minimumUsableSpace();
	
	@Meta.AD(
            deflt = "1300000",
			description = "healthcheck-best-practice-owasp-pbkdf2withhmacsha1-hashing-recommendation-description",
            name = "healthcheck-best-practice-owasp-pbkdf2withhmacsha1-hashing-recommendation-name",
            required = false
        )
	public Long owaspHashingRecommendation();
}
