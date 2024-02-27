package com.liferay.portal.health.operation;

import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Suggestion by Dave Nebinger: SB creates a Table instance for each SB entity
 * table (i.e. BlogsEntryTable), so you could get into verifying the tables
 * exist, that they have only the columns listed, that they have the
 * types/sizes, … Table is an interface, so you can use a ServiceTracker to get
 * all instances and then use the details to complete the check.
 * 
 * @author Olaf Kock
 */

public class ServiceBuilderTableHealthcheck extends HealthcheckBaseImpl {

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		return new LinkedList<HealthcheckItem>();
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

}
