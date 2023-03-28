package com.liferay.portal.health.operation;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * This Healthcheck makes sure that portal*.properties configurations for boolean values
 * are purely set to one of "true" or "false" (due to LPS-157829).
 * 
 * @author Olaf Kock
 */


@SuppressWarnings("rawtypes")
@Component(
		service=Healthcheck.class
		)
public class BooleanPropertiesPlausibleValuesHealthcheck extends BasePropertiesPlausibleValuesHealthcheck {
	
	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_server_admin_web_portlet_ServerAdminPortlet&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_mvcRenderCommandName=%2Fserver_admin%2Fview&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_tabs1=properties&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_screenNavigationCategoryKey=portal-properties";
	private static final String MSG = "healthcheck-boolean-properties";
	private static final String ERROR_MSG = "healthcheck-boolean-properties-mismatch";

	@SuppressWarnings("unchecked")
	public BooleanPropertiesPlausibleValuesHealthcheck() {
		super(boolean.class, LINK, MSG, ERROR_MSG, log);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		return super.check(companyId, locale, (value -> (value.equals("true") || value.equals("false"))));
	}
	
	private static Log log = LogFactoryUtil.getLog(BooleanPropertiesPlausibleValuesHealthcheck.class);}
