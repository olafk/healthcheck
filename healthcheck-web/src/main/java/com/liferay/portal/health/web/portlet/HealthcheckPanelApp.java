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

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.health.web.constants.HealthcheckWebPortletKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


/**
 * 
 * @author Olaf Kock
 *
 */

@Component(
		immediate = true,
		property = {
			"panel.app.order:Integer=800",
			"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_SYSTEM,
			"service.ranking:Integer=1000"
		},
		service = PanelApp.class
	)
public class HealthcheckPanelApp extends BasePanelApp {

	private Portlet portlet;

	@Override
	public String getPortletId() {
		return HealthcheckWebPortletKeys.HEALTHCHECK_WEB_PORTLET;
	}
	
	@Override
	public Portlet getPortlet() {
		return portlet;
	}
	
	@Reference(
		target = "(javax.portlet.name=" + HealthcheckWebPortletKeys.HEALTHCHECK_WEB_PORTLET + ")",
		unbind = "-"
	)
	public void setPortlet(Portlet portlet) {
		this.portlet = portlet;
	}
}
