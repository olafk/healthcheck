package de.olafkock.liferay.configuration.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.metatype.AttributeDefinition;

public class ADContent {
	public String id;
	public String name;
	public String description;
	public String[] deflts;
	public String type;
	public String cardinality;
	public List<String> options = Collections.emptyList();
	
	@SuppressWarnings("deprecation")
	public void resolveType(AttributeDefinition ad) {
		int cd = ad.getCardinality();
		
		if(cd < 0) {
			if(cd == Integer.MIN_VALUE)
				cardinality = "[] as List";
			else
				cardinality = "[max "+ (-cd) + "] as List";
		} else if (cd > 0) {
			if(cd == Integer.MAX_VALUE)
				cardinality = "[]";
			else
				cardinality = "[max "+ (cd) + "]";
		} else
			cardinality = "";
		
		switch(ad.getType()) {
		case AttributeDefinition.STRING:
			type ="String"; break;
		case AttributeDefinition.LONG:
			type="Long"; break;
		case AttributeDefinition.INTEGER:
			type="Integer"; break;
		case AttributeDefinition.SHORT:
			type="Short"; break;
		case AttributeDefinition.CHARACTER:
			type="Character"; break;
		case AttributeDefinition.BYTE:
			type="Byte"; break;
		case AttributeDefinition.DOUBLE:
			type="Double"; break;
		case AttributeDefinition.FLOAT:
			type="Float"; break;
		case AttributeDefinition.BOOLEAN:
			type="Boolean"; break;
		case AttributeDefinition.PASSWORD:
			type="Password"; break;
		case AttributeDefinition.BIGDECIMAL:
			type="Bigdecimal-deprecated"; break;
		default:
			type="unknown type:" + ad.getType();
		}
	}

	public void resolveOptions(AttributeDefinition attributeDefinition) {
		String[] optionLabels = attributeDefinition.getOptionLabels();
		String[] optionValues = attributeDefinition.getOptionValues();
		if(optionLabels==null) return;
		
		options = new ArrayList<String>(optionLabels.length);
		for (int i = 0; i < optionValues.length; i++) {
			options.add(optionValues[i]);
		}
	}
}
