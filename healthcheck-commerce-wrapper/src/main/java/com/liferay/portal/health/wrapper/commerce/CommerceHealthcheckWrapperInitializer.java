package com.liferay.portal.health.wrapper.commerce;

import com.liferay.commerce.health.status.CommerceHealthHttpStatus;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

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
public class CommerceHealthcheckWrapperInitializer {

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

	@Reference(			
			cardinality = ReferenceCardinality.MULTIPLE,
		    policyOption = ReferencePolicyOption.GREEDY,
		    unbind = "doUnRegister" 
	)
	void doRegister(CommerceHealthHttpStatus commerceHealthHttpStatus) {
		log.info("doRegister on " + commerceHealthHttpStatus.getClass().getName());
		CommerceHealthcheckWrapper service = new CommerceHealthcheckWrapper(commerceHealthHttpStatus);
		unregisteredServices.add(service);
		registerServices();
	}

	void doUnRegister(CommerceHealthHttpStatus commerceHealthHttpStatus) {
		if(services.containsKey(commerceHealthHttpStatus)) {
			ServiceRegistration<Healthcheck> serviceRegistration = services.get(commerceHealthHttpStatus);
			serviceRegistration.unregister();
			services.remove(commerceHealthHttpStatus);
			log.info("doUnRegister on " + commerceHealthHttpStatus.getClass().getName() + " successful");
		}
	}

	private void registerServices() {
		if(context != null && commerceChannelLocalService != null) {
			for (Iterator<CommerceHealthcheckWrapper> iterator = unregisteredServices.iterator(); iterator.hasNext();) {
				CommerceHealthcheckWrapper wrapper = iterator.next();
				wrapper.setCommerceChannelLocalService(commerceChannelLocalService);
				
				Hashtable<String, Object> properties = new Hashtable<String,Object>();
				ServiceRegistration<Healthcheck> serviceRegistration = context.registerService(Healthcheck.class, wrapper, properties);
				services.put(wrapper.getWrappee(), serviceRegistration);
				iterator.remove();
				log.info("registered wrapper for " + wrapper.getWrappee().getClass().getName());
			}
		}
	}

	@Reference
	private void setCommerceChannelLocalService(CommerceChannelLocalService commerceChannelLocalService) {
		this.commerceChannelLocalService = commerceChannelLocalService;
		registerServices();
	}

	private CommerceChannelLocalService commerceChannelLocalService;
	private List<CommerceHealthcheckWrapper> unregisteredServices = new LinkedList<CommerceHealthcheckWrapper>();
	private Map<CommerceHealthHttpStatus, ServiceRegistration<Healthcheck>> services = new HashMap<CommerceHealthHttpStatus, ServiceRegistration<Healthcheck>>();
	
	private static Log log = LogFactoryUtil.getLog(CommerceHealthcheckWrapperInitializer.class);
}
