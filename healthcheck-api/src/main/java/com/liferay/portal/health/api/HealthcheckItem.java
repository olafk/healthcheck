package com.liferay.portal.health.api;

import com.liferay.portal.kernel.exception.PortalException;

public interface HealthcheckItem {

	boolean isResolved() throws PortalException;

	String getMessage();

	String getLink();

	String getCategory();

}