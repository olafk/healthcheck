package com.liferay.portal.health.operation.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(category = "healthcheck")
@Meta.OCD(
		id = "com.liferay.portal.health.operation.configuration.HealthcheckOperationalConfiguration"
	    , localization = "content/Language"
	    , name = "healthcheck-operational-configuration-name"
	    , description = "healthcheck-operational-configuration-description"
	)
public interface HealthcheckOperationalConfiguration {
	@Meta.AD(
            deflt = "90",
			description = "healthcheck-operational-remaining-activation-period-description",
            name = "healthcheck-operational-remaining-activation-period-name",
            required = false
        )
	public Integer remainingActivationPeriod();
	
	@Meta.AD(
            deflt = "22",
			description = "healthcheck-operational-acceptable-missing-updates-description",
            name = "healthcheck-operational-acceptable-missing-updates-name",
            required = false
        )
	public Integer acceptableMissingUpdates();
	
	
}
