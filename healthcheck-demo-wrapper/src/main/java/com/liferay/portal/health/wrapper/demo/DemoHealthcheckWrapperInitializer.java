package com.liferay.portal.health.wrapper.demo;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.sales.checklist.api.ChecklistProvider;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component(immediate = true)
public class DemoHealthcheckWrapperInitializer {

	private BundleContext context;

	@Activate
	private void activate(BundleContext context) {
		// Activation is enough, as this service might be activated when all 
		// references have been set, and only then has the bundleContext to 
		// register this bundle's service wrappers. 
		// 
		// Deactivation is done through the individual doUnRegister methods.
		this.context = context;
		log.info("Activate BundleContext :" + context);
		registerServices();
	}

	@Reference private CompanyLocalService companyLocalService;
	
	@Reference(			
			cardinality = ReferenceCardinality.MULTIPLE,
		    policyOption = ReferencePolicyOption.GREEDY,
		    unbind = "doUnRegister" 
	)
	void doRegister(ChecklistProvider checklistProvider) {
		log.info("doRegister on " + checklistProvider.getClass().getName());
		DemoHealthcheckWrapper service = new DemoHealthcheckWrapper(checklistProvider);

		// Full registration can only be done when this service is fully initialized/activated
		unregisteredServices.add(service);
		registerServices();
	}

	void doUnRegister(ChecklistProvider checklistProvider) {
		if(services.containsKey(checklistProvider)) {
			ServiceRegistration<Healthcheck> serviceRegistration = services.get(checklistProvider);
			serviceRegistration.unregister();
			services.remove(checklistProvider);
			log.info("doUnRegister on " + checklistProvider.getClass().getName() + " successful");
		}
	}

	private void registerServices() {
		if(context != null) {
			for (Iterator<DemoHealthcheckWrapper> iterator = unregisteredServices.iterator(); iterator.hasNext();) {
				DemoHealthcheckWrapper wrapper = iterator.next();
				wrapper.setCompanyLocalService(companyLocalService);
				Hashtable<String, Object> properties = new Hashtable<String,Object>();
				ServiceRegistration<Healthcheck> serviceRegistration = context.registerService(Healthcheck.class, wrapper, properties);
				services.put(wrapper.getWrappee(), serviceRegistration);
				iterator.remove();
				log.info("registered wrapper for " + wrapper.getWrappee().getClass().getName());
			}
		}
	}
	
	private List<DemoHealthcheckWrapper> unregisteredServices = new LinkedList<DemoHealthcheckWrapper>();
	private Map<ChecklistProvider, ServiceRegistration<Healthcheck>> services = new HashMap<ChecklistProvider, ServiceRegistration<Healthcheck>>();
	
	private static Log log = LogFactoryUtil.getLog(DemoHealthcheckWrapperInitializer.class);
}
