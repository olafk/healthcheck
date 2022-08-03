package com.liferay.portal.healthcheck.operation;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

/**
 * This Healthcheck makes sure that portal*.properties configurations for long values
 * are purely set to numerical values (due to LPS-157829).
 * 
 * @author Olaf Kock
 */


@SuppressWarnings("rawtypes")
@Component(
		service=Healthcheck.class
		)
public class LongPropertiesPlausibleValuesHealthcheck extends BasePropertiesPlausibleValuesHealthcheck {

	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_server_admin_web_portlet_ServerAdminPortlet&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_mvcRenderCommandName=%2Fserver_admin%2Fview&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_tabs1=properties&_com_liferay_server_admin_web_portlet_ServerAdminPortlet_screenNavigationCategoryKey=portal-properties";
	private static final String MSG = "healthcheck-long-properties";
	private static final String ERROR_MSG = "healthcheck-long-properties-mismatch";

	@SuppressWarnings("unchecked")
	public LongPropertiesPlausibleValuesHealthcheck() {
		super(long.class, LINK, MSG, ERROR_MSG, log);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		return check(themeDisplay, (value -> !(value.contains(",") || (GetterUtil.getLong(value) == 0 && !value.equals("0")))));
	}
	
	private static Log log = LogFactoryUtil.getLog(LongPropertiesPlausibleValuesHealthcheck.class);}
