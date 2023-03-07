package com.liferay.portal.health.demo;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.Collection;
import java.util.Locale;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component( 
		service = Healthcheck.class 
)

public class AccountCreationByStrangersHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fview_configuration_screen&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_configurationScreenKey=general-authentication";
	private static final String MSG = "healthcheck-strangers-can-not-create-accounts";
	private static final String MSG_ERROR = "healthcheck-strangers-can-create-accounts";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		PortletPreferences preferences = PrefsPropsUtil.getPreferences(companyId);

		try {
			boolean state = ! _getPrefsPropsBoolean(
					preferences, 
					companyLocalService.getCompany(companyId), 
					PropsKeys.COMPANY_SECURITY_STRANGERS,
					PropsValues.COMPANY_SECURITY_STRANGERS);
			
			return wrap(create(
					state, 
					locale, 
					LINK, 
					state ? MSG : MSG_ERROR, 
					"company.security.strangers"));
		} catch (PortalException e) {
			return wrap(create(this, locale, e));
		} 
	}

	private static boolean _getPrefsPropsBoolean(
		PortletPreferences portletPreferences, Company company, String name,
		boolean defaultValue) {

		String value = portletPreferences.getValue(
			name, PropsUtil.get(company, name));

		if (value != null) {
			return GetterUtil.getBoolean(value);
		}

		return defaultValue;
	}
	
	@Override
	public String getCategory() {
		return "healthcheck-category-demosystem";
	}
	
	@Reference
	CompanyLocalService companyLocalService;
}
