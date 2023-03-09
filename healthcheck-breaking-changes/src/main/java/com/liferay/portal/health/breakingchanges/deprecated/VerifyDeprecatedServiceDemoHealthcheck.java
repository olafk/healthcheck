package com.liferay.portal.health.breakingchanges.deprecated;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.breakingchanges.deprecated.sample.DeprecatedSampleService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Demo for finding implementations of a deprecated service.
 * Not having a reliable deprecated service (that won't be deleted in the future)
 * this demo implementation is faked: It'll need to be implemented for each
 * newly deprecated service and should fail if there is any remaining implementation
 * of that service.
 * The assumption is that a custom service implementation would otherwise fail 
 * silently and lead to wasted debugging time.
 * 
 * TODO: Implement this feature with ServiceTracker or similar to be more dynamic, 
 * and cover many services within one healthcheck implementation - without the need
 * for individual static @Reference implementations.
 *  
 * @author Olaf Kock
 */

@Component(
		service=Healthcheck.class
		)
public class VerifyDeprecatedServiceDemoHealthcheck extends HealthcheckBaseImpl {

	private static final String MSG = "healthcheck-deprecated-service-demo";
	private static final String ERROR_MESSAGE = "healthcheck-deprecated-service-demo-found";
	
	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		Collection<HealthcheckItem> items = new LinkedList<HealthcheckItem>();
		if(services.isEmpty()) {
			// todo: Translate!
			items.add(create(true, locale, null, MSG));
		} else {
			for (DeprecatedSampleService service : services) {
				items.add(create(false, 
						this.getClass().getName() + "-" + service.getClass().getName(),
						locale, 
						null, 
						ERROR_MESSAGE, 
						DeprecatedSampleService.class.getName(), 
						service.getClass().getName()));
			}
		}
		return items;
	}

	@Reference( cardinality = ReferenceCardinality.MULTIPLE,
		    policyOption = ReferencePolicyOption.GREEDY,
		    unbind = "unregisterDeprecatedSampleService" )
	protected void registerDeprecatedSampleService(DeprecatedSampleService service) {
		services.add(service);
	}
	
	protected void unregisterDeprecatedSampleService(DeprecatedSampleService service) {
		services.remove(service);
	}
	
	
	private LinkedList<DeprecatedSampleService> services = new LinkedList<DeprecatedSampleService>();
	
	@Override
	public String getCategory() {
		return "healthcheck-category-breaking-changes";
	}

}
