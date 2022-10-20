package de.olafkock.liferay.configuration.api;

/**
 * @author Olaf Kock
 */
public interface GenericConfigurationLookup {
	String getDefaultValue(String configurationPid, String key);
}