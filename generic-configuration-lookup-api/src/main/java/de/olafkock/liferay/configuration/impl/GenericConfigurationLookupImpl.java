package de.olafkock.liferay.configuration.impl;

import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;

import java.util.SortedSet;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.MetaTypeService;

import de.olafkock.liferay.configuration.api.GenericConfigurationLookup;

//@Component(
//		immediate=true,
//		service=GenericConfigurationLookup.class
//		)
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
		MetaInfoExtractor metaInfoExtractor = new MetaInfoExtractor();
		// see method javadoc for limitations. 
		SortedSet<OCDContent> ocds = metaInfoExtractor.extractOCD(_metaTypeService, _extendedMetaTypeService);
		OCDContent ocd = ocds.stream().filter(item->item.id.equals(configurationPid)).findFirst().get();
		ADContent ad = ocd.ads.stream().filter(item->item.id.equals(key)).findFirst().get();
		return ad.deflts[0];
	}

	@Reference
	ExtendedMetaTypeService _extendedMetaTypeService;
	
	@Reference
	MetaTypeService _metaTypeService;
}
