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

package com.liferay.portal.health.operation;

import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This Healthcheck performs the necessary operations for typechecking values
 * set in portal*.properties configurations due to LPS-157829.
 * 
 * This can point out configuration errors, where the same file contains a value
 * twice, generating an illegal (and unexpected) configuration value.
 * 
 * @author Olaf Kock
 */

public abstract class BasePropertiesPlausibleValuesHealthcheck<T> extends HealthcheckBaseImpl {
	private Class<T> clazz;
	private String link;
	private String msg;
	private String errorMsg;

	public interface PropertyValidator {
		boolean isValid(String string);
	}

	public BasePropertiesPlausibleValuesHealthcheck(Class<T> clazz, String link, String msg, String errorMsg, Log log) {
		this.clazz = clazz;
		this.link = link;
		this.msg = msg;
		this.errorMsg = errorMsg;
		this.log = log;
	}

	public Collection<HealthcheckItem> check(long companyId, Locale locale, PropertyValidator validator) {
		List<HealthcheckItem> result = new LinkedList<HealthcheckItem>();
		List<String> theProperties = getProperties();
		log.info("found " + theProperties.size() + " properties");

		for (String property : theProperties) {
			String value = PropsUtil.get(property);
			if (value != null) {
				if (!validator.isValid(value)) {
					result.add(create(false, locale, link, errorMsg, property, value));
				}
			} else {
				log.warn("null " + property /*
											 * +
											 * " is null. This is a field defined in PropsValues, but undefined in any portal*.properties file"
											 */);
			}
		}
		if (result.isEmpty()) {
			result.add(create(true, locale, link, msg));
		}
		return result;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

	private List<String> getProperties() {
		ArrayList<String> props = new ArrayList<String>(50);

		Field[] fields = PropsValues.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(clazz)) {
				String property = getProperty(field.getName());
				if (property != null) {
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
			if (field != null) {
				if (Modifier.isStatic(field.getModifiers())) {
					if (field.getType().equals(String.class)) {
						return (String) field.get(null);
					}
				}
			}
		} catch (NoSuchFieldException e) {
			if (!knownMissingProperties.contains(fieldName)) {
				log.error("No such field: PropsKeys." + fieldName);
				return e.getClass().getName() + " " + e.getMessage() + " for " + fieldName;
			}
		} catch (SecurityException e) {
			log.error(e);
			return e.getClass().getName() + " " + e.getMessage() + " for " + fieldName;
		} catch (IllegalArgumentException e) {
			log.error(e);
			return e.getClass().getName() + " " + e.getMessage() + " for " + fieldName;
		} catch (IllegalAccessException e) {
			log.error(e);
			return e.getClass().getName() + " " + e.getMessage() + " for " + fieldName;
		}

		return null;
	}

	/**
	 * Fields that are present in PropsValues, but are known to not be present in
	 * PropsKeys for various reasons (e.g. they might be implemented without
	 * properties, or just convenience lookups derived from other properties)
	 */

	private static final Set<String> knownMissingProperties = new HashSet<String>(Arrays.asList(new String[] {
			"FEATURE_FLAGS_JSON", "LIFERAY_WEB_PORTAL_CONTEXT_TEMPDIR", "PORTLET_EVENT_DISTRIBUTION_LAYOUT",
			"PORTLET_EVENT_DISTRIBUTION_LAYOUT_SET", "PORTLET_PUBLIC_RENDER_PARAMETER_DISTRIBUTION_LAYOUT",
			"PORTLET_PUBLIC_RENDER_PARAMETER_DISTRIBUTION_LAYOUT_SET" }));

	private final Log log;

}
