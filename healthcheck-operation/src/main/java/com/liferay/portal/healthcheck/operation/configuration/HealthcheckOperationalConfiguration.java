package com.liferay.portal.healthcheck.operation.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
		id = "com.liferay.portal.healthcheck.operation.configuration.HealthcheckOperationalConfiguration"
	    , localization = "content/Language"
	    , name = "healthcheck-operational-configuration-name"
	    , description = "healthcheck-operational-configuration-description"
	)
public interface HealthcheckOperationalConfiguration {
	@Meta.AD(
            deflt = "150",
			description = "healthcheck-operational-minimal-usable-space-description",
            name = "healthcheck-operational-minimal-usable-space-name",
            required = true
        )
	public Long minimumUsableSpace();
}
