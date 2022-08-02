package com.liferay.portal.healthcheck.operation;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * This Healthcheck makes sure that portal*.properties configurations for boolean values
 * are purely set to one of "true" or "false" (due to LPS-157829).
 * 
 * Boolean properties are identified based on the fields defined in PropsValues
 * 
 * @author Olaf Kock
 */


@Component(
		service=Healthcheck.class
		)
public class BooleanPropertiesPlausibleValuesHealthcheck extends HealthcheckBaseImpl {
	private static final String LINK = null;
	private static final String MSG = "healthcheck-boolean-properties";
	private static final String ERROR_MSG = "healthcheck-boolean-properties-mismatch";

	
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		List<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		List<String> booleanProperties = getBooleanProperties();
		log.info("found " + booleanProperties.size() + " boolean properties");
		
		for (String property : booleanProperties) {
			String value = PropsUtil.get(property);
			if(value != null) {
				boolean test = (value.equals("true") || value.equals("false"));
				if(!test) {
					result.add(create(false, themeDisplay.getLocale(), LINK, ERROR_MSG, property, value));
				}
			} else {
				log.warn(property + " is null. This is a field defined in PropsValues, but undefined in any portal*.properties file");
			}
		}
		if(result.isEmpty()) {
			result.add(create(true, themeDisplay.getLocale(), LINK, MSG));
		}
		return result;	
	}
	
	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

	private List<String> getBooleanProperties() {
		ArrayList<String> props = new ArrayList<String>(50);
		
		Field[] fields = PropsValues.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if(Modifier.isStatic(field.getModifiers()) 
					&& field.getType().equals(boolean.class)
				) {
				String property = getProperty(field.getName());
				if(property != null) {
					props.add(property);
				}
			}
		}
		
		
		return props;
	}

	private String getProperty(String fieldName) {
		Field field;
		try {
			field = PropsKeys.class.getField(fieldName);
			if(field != null) {
				if(Modifier.isStatic(field.getModifiers())) {
					if(field.getType().equals(String.class)) {
						return (String) field.get(null);
					}
				}
			}
		} catch (NoSuchFieldException e) {
			log.error("No such field: PropsKeys." + fieldName);
		} catch (SecurityException e) {
			log.error(e);
		} catch (IllegalArgumentException e) {
			log.error(e);
		} catch (IllegalAccessException e) {
			log.error(e);
		}

		return null;
	}
	
	
	private static Log log = LogFactoryUtil.getLog(BooleanPropertiesPlausibleValuesHealthcheck.class);}
