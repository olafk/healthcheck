package com.liferay.portal.health.api;

public interface HealthcheckItem {

	/**
	 * signals if the healthcheck result is healthy or not
	 * @return true if healthy
	 */
	boolean isResolved();

	/**
	 * An informative message on the tested condition, in the language that
	 * a healthcheck has been executed. 
	 * @return human readable message
	 */
	String getMessage();

	/**
	 * A link(URL) that can contain further information on the tested condition 
	 * @return a link URL
	 */
	String getLink();

	/**
	 * A human readable (localized) category for the kind of healthcheck that was executed
	 * @return
	 */
	String getCategory();

	/**
	 * A machine readable key that can be used to refer to a particular healthcheck or its result.
	 * This was introduced to be able to ignore certain healthchecks, in case their test
	 * does not apply to a certain environment (example: Elasticsearch Sidecar is ok in local 
	 * demo systems). Default content: The healthcheck's fully qualified classname, optionally 
	 * extended by extra information (each healthcheck might execute several checks) and the 
	 * 
	 * @return
	 */
	String getKey();
}