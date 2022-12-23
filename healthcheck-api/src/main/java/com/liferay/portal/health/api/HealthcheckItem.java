package com.liferay.portal.health.api;

public interface HealthcheckItem {

	boolean isResolved();

	String getMessage();

	String getLink();

	String getCategory();

}