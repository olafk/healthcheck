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

package com.liferay.portal.health.web.portlet;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.health.web.constants.HealthcheckWebPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Olaf Kock
 */
@Component(immediate = true, property = { "com.liferay.portlet.ajaxable=true",
		"com.liferay.portlet.display-category=category.hidden", "com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false", "com.liferay.portlet.remoteable=true",
		"javax.portlet.display-name=HealthcheckWeb", "javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + HealthcheckWebPortletKeys.HEALTHCHECK_WEB_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=administrator" }, service = Portlet.class)
public class HealthcheckWebPortlet extends MVCPortlet {

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		List<HealthcheckItem> checks = new LinkedList<HealthcheckItem>();
		String method = PortalUtil.getHttpServletRequest(renderRequest).getMethod();

		if (themeDisplay.getPermissionChecker().isCompanyAdmin(themeDisplay.getCompanyId()) 
				&& !"HEAD".equals(method)) {
			for (Healthcheck healthcheck : healthchecks) {
				try {
					checks.addAll(healthcheck.check(themeDisplay.getCompanyId(), themeDisplay.getLocale()));
				} catch (Exception e) {
					HealthcheckBaseImpl b = new HealthcheckBaseImpl() {
						@Override
						public String getCategory() {
							return healthcheck.getCategory();
						}

						@Override
						public Collection<HealthcheckItem> check(long companyId, Locale locale) {
							// Unused in this dummy
							return null;
						}
					};
					checks.add(b.create(healthcheck, themeDisplay.getLocale(), e));
				}
			}
		} else {
			Healthcheck dummy = new HealthcheckBaseImpl() {
				@Override
				public String getCategory() {
					return "healthcheck-category-generic";
				}

				@Override
				public Collection<HealthcheckItem> check(long companyId, Locale locale) {
					return wrap(create(false, locale, "/", "healthcheck-need-to-be-company-administrator"));
				}
			};
			checks.addAll(dummy.check(themeDisplay.getCompanyId(), themeDisplay.getLocale()));
		}
		Collections.sort(checks, new Comparator<HealthcheckItem>() {

			@Override
			public int compare(HealthcheckItem arg0, HealthcheckItem arg1) {
				if (arg0.isResolved() == arg1.isResolved()) {
					return arg0.getCategory().compareTo(arg1.getCategory());
				} else if (arg0.isResolved()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
		PortletPreferences preferences = renderRequest.getPreferences();
		String[] ignoredChecksArray = preferences.getValues("ignore", new String[] {});
		Set<String> ignoreChecks = new HashSet<String>(Arrays.asList(ignoredChecksArray));

		int failed = 0;
		int succeeded = 0;
		int ignored = 0;
		for (Iterator<HealthcheckItem> iterator = checks.iterator(); iterator.hasNext();) {
			HealthcheckItem check = (HealthcheckItem) iterator.next();
			if (ignoreChecks.contains(check.getKey())) {
				iterator.remove();
				ignored++;
			} else if (check.isResolved()) {
				succeeded++;
			} else {
				failed++;
			}
		}
		renderRequest.setAttribute("checks", checks);
		renderRequest.setAttribute("failedChecks", failed);
		renderRequest.setAttribute("succeededChecks", succeeded);
		renderRequest.setAttribute("ignoredChecks", ignored);
		renderRequest.setAttribute("the-ignored-checks", ignoreChecks);
		super.doView(renderRequest, renderResponse);
	}

	public void resetIgnore(ActionRequest actionRequest, ActionResponse actionResponse)
			throws PortletException, IOException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		if (themeDisplay.getPermissionChecker().isCompanyAdmin(themeDisplay.getCompanyId())) {
			PortletPreferences preferences = actionRequest.getPreferences();
			preferences.setValues("ignore", new String[0]);
			preferences.store();
		}
	}

	public void ignoreMessage(ActionRequest actionRequest, ActionResponse actionResponse)
			throws PortletException, IOException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		if (themeDisplay.getPermissionChecker().isCompanyAdmin(themeDisplay.getCompanyId())
				&& actionRequest.getMethod().equals("POST")) {
			String key = ParamUtil.getString(actionRequest, "ignore");
			PortletPreferences preferences = actionRequest.getPreferences();
			String[] ignoredArray = preferences.getValues("ignore", new String[] {});
			Set<String> ignoredKeys = new HashSet<String>(Arrays.asList(ignoredArray));

			ignoredKeys.add(key);

			preferences.setValues("ignore", (String[]) ignoredKeys.toArray(new String[ignoredKeys.size()]));
			preferences.store();
		}
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY, unbind = "doUnregister")
	void doRegister(Healthcheck healthcheck) {
		healthchecks.add(healthcheck);
	}

	void doUnregister(Healthcheck healthcheck) {
		healthchecks.remove(healthcheck);
	}

	List<Healthcheck> healthchecks = new LinkedList<Healthcheck>();
	
	static Log _log = LogFactoryUtil.getLog(HealthcheckWebPortlet.class);
}