package de.olafkock.liferay.configuration.impl;

import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * Extract OCD/AD Metainformation from the OSGi runtime for documentation purposes.
 * 
 * adapted from http://docs.osgi.org/specification/osgi.cmpn/7.0.0/service.metatype.html
 * and https://github.com/olafk/OSGiConfigurationListing
 * 
 * @author Olaf Kock
 */

public class MetaInfoExtractor {

	public SortedSet<OCDContent> extractOCD(MetaTypeService mts, ExtendedMetaTypeService ems) {
		SortedSet<OCDContent> result = new TreeSet<OCDContent>(new OCDContent.Comparator());
		BundleContext bc = BundleActivator.bundleContext;
		Bundle[] bundles = bc.getBundles();

		for (Bundle bundle : bundles) {
			result.addAll(extractOCD(bundle, mts, ems));
		}
		
		return result;
	}
	
	public List<OCDContent> extractOCD(Bundle b, MetaTypeService mts, ExtendedMetaTypeService ems) {
		LinkedList<OCDContent> result = new LinkedList<OCDContent>();
	    MetaTypeInformation mti = mts.getMetaTypeInformation(b);
	    ExtendedMetaTypeInformation emti = ems.getMetaTypeInformation(b);

	    String [] pids = mti.getPids();
	    if(pids == null || pids.length == 0) {
	    	return result;
	    }
        
	    for (int i=0; i< pids.length; i++) {
			OCDContent ocdContent = new OCDContent();
			result.add(ocdContent);
			String errorContext = "none";
            try {
				ObjectClassDefinition ocd = mti.getObjectClassDefinition(pids[i], null);
				errorContext = ocd.getID();
				ExtendedObjectClassDefinition eocd = null;
				try {
					eocd = emti.getObjectClassDefinition(ocd.getID(), null);
				} catch (Exception e1) {
					ocdContent.comment = e1.getClass().getName() + " " + e1.getMessage() + " ";
				}
				
				AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
				
				String description = ocd.getDescription();
				
				ocdContent.id = ocd.getID();
				ocdContent.name = ocd.getName();
				ocdContent.description = description;
				ocdContent.bundle = b.getSymbolicName();

				Set<String> extensionUris = Collections.emptySet();
				if(eocd != null) extensionUris = eocd.getExtensionUris();

				for (String extention : extensionUris) {
					String category = eocd.getExtensionAttributes(extention).get("category");
					String scope = eocd.getExtensionAttributes(extention).get("scope");

					if(category != null) {
						ocdContent.category = category;
					}
					if(scope != null) {
						ocdContent.scope = scope;
					}
					
					ocdContent.learnMessageKey = eocd.getExtensionAttributes(extention).get("liferayLearnMessageKey");
					ocdContent.learnMessageResource = eocd.getExtensionAttributes(extention).get("liferayLearnMessageResource");
				}
				
				for (int j=0; j< ads.length; j++) {
					ADContent adContent = new ADContent();
					ocdContent.ads.add(adContent);
					
					adContent.id = ads[j].getID();
					adContent.name = ads[j].getName();
					adContent.description = ads[j].getDescription();
					adContent.deflts = ads[j].getDefaultValue();
					adContent.resolveType(ads[j]);
					adContent.resolveOptions(ads[j]);
				}
			} catch (Exception e) {
				_log.error(errorContext, e);
				ocdContent.comment = e.getClass().getName() 
						+ " " 
						+ e.getMessage() 
						+ " in context " 
						+ errorContext;
			}
        }
        return result;
	}
	
	static Log _log = LogFactoryUtil.getLog(MetaInfoExtractor.class);
}
