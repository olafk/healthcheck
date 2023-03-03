package com.liferay.health.bestpractice.configuration;

import aQute.bnd.annotation.metatype.Meta;

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
            required = true
        )
	public Integer maximumSimpleStoreFiles();
}
