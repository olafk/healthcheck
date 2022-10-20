package de.olafkock.liferay.configuration.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeService;

public class BundleActivator implements org.osgi.framework.BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	    if(metatyperef != null)
	    	context.ungetService(metatyperef);
		bundleContext = null;
		metatyperef = null;
		mts = null;
	}

	public static BundleContext bundleContext = null;
	private static MetaTypeService mts = null;

	@SuppressWarnings("unchecked")
	public static MetaTypeService getMts() {
		// this lookup needs to be delayed, as the MTS might not be
		// available yet at bundle startup.
	    if(metatyperef == null) {
	    	metatyperef = bundleContext.getServiceReference(MetaTypeService.class.getName());
		    mts = (MetaTypeService)bundleContext.getService(metatyperef);
	    }
	    return mts;
	}

	@SuppressWarnings("rawtypes")
	private static ServiceReference metatyperef = null;
}
