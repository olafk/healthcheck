package de.olafkock.liferay.configuration.impl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

import de.olafkock.liferay.configuration.api.GenericConfigurationLookup;

@Component(
		immediate=true,
		service=GenericConfigurationLookup.class
		)
public class GenericConfigurationLookupImpl implements GenericConfigurationLookup {

	/**
	 * Retrieve the default value from the OCD configuration of a specific
	 * configurationPid/key. This method has (currently) no error handling
	 * and assumes that the callers of this method know that a specific 
	 * default value exists and is a single value. 
	 * 
	 * For future changes - e.g. handle multiple values, no values at all, 
	 * return types etc. this needs some work. But it's good for its 
	 * immediate purpose.
	 * 
	 * @author Olaf Kock
	 */
	
	@Override
	public String getDefaultValue(String configurationPid, String key) {
		_log.debug("looking up default configuration for " + configurationPid + " " + key);
		BundleContext bc = BundleActivator.bundleContext;
		Bundle[] bundles = bc.getBundles();

		for (Bundle bundle : bundles) {
			String deflt = extractDeflt(bundle, configurationPid, key);
			if(deflt != null) {
				_log.debug("Found default configuration for " + key + " (" + configurationPid + "): " + deflt);
				return deflt;
			}
		}

		_log.debug("No default configuration found for " + configurationPid + " " + key);
		return null;
	}

	public String extractDeflt(Bundle b, String configurationPid, String key) {
	    MetaTypeInformation mti = _metaTypeService.getMetaTypeInformation(b);

	    String [] pids = mti.getPids();
	    if(pids == null || pids.length == 0) {
	    	return null;
	    }
        
	    for (int i=0; i< pids.length; i++) {
	    	if(pids[i].equals(configurationPid)) {
	    		ObjectClassDefinition ocd = mti.getObjectClassDefinition(pids[i], null);
	    		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
				
				for (int j=0; j< ads.length; j++) {
					if(ads[j].getID().equals(key)) {
						String[] dflts = ads[j].getDefaultValue();
						if(dflts != null && dflts.length > 0) {
							return ads[j].getDefaultValue()[0];
						} else {
							return null;
						}
					}
				}
	        }
	    }
        return null;
	}
	
	@Reference
	MetaTypeService _metaTypeService;
	
	static Log _log = LogFactoryUtil.getLog(GenericConfigurationLookupImpl.class);
}
