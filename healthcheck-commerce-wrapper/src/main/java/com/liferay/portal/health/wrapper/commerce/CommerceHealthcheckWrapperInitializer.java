/**
 * Copyright (c) 2022-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.health.wrapper.commerce;

import com.liferay.commerce.health.status.CommerceHealthStatus;
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
	void doRegister(CommerceHealthStatus commerceHealthHttpStatus) {
		// TODO: Reimplement with ServiceTracker to get rid of this conditional:

		// At the time of writing this class, WishListContentCommerceHealthHttpStatus is not enabled, but
		// I don't know how to programmatically figure this out - as calling isFixed causes a very noisy 
		// stacktrace, for now I'm deactivating this check in a stupid and hardcoded way
		if(! commerceHealthHttpStatus.getClass().getName().equals(
				"com.liferay.commerce.wish.list.web.internal.health.status.WishListContentCommerceHealthHttpStatus")) {
			
			log.info("doRegister on " + commerceHealthHttpStatus.getClass().getName());
			CommerceHealthcheckWrapper service = new CommerceHealthcheckWrapper(commerceHealthHttpStatus);

			unregisteredServices.add(service);
			registerServices();
		} else {
			log.warn("ignored " + commerceHealthHttpStatus.getClass().getName());
		}
	}

	private void doUnRegister(CommerceHealthStatus commerceHealthHttpStatus) {
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

	@SuppressWarnings("unused")
	private void unsetCommerceChannelLocalService(CommerceChannelLocalService commerceChannelLocalService) {
		for (Map.Entry<CommerceHealthStatus,ServiceRegistration<Healthcheck>> registration : services.entrySet()) {
			unregisteredServices.add(new CommerceHealthcheckWrapper(registration.getKey()));
			doUnRegister(registration.getKey());
		}
	}
		
	private CommerceChannelLocalService commerceChannelLocalService;
	private List<CommerceHealthcheckWrapper> unregisteredServices = new LinkedList<CommerceHealthcheckWrapper>();
	private Map<CommerceHealthStatus, ServiceRegistration<Healthcheck>> services = new HashMap<CommerceHealthStatus, ServiceRegistration<Healthcheck>>();
	
	private static Log log = LogFactoryUtil.getLog(CommerceHealthcheckWrapperInitializer.class);
}
