package com.liferay.health.bestpractice;

import com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(configurationPid = { "com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration",
		"com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration" }, service = Healthcheck.class)
public class BlindSelfSignedCertificateTrustHealthcheck implements Healthcheck {
	public final static String MSG = "healthcheck-trust-self-signed-dataprovider-certificates";
	public final static String LINK = "/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_factoryPid=com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_pid=com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration";

	@Override
	public Collection<HealthcheckItem> check(long companyId) {
		try {
			Object[] info = {};
			return Arrays.asList(new HealthcheckItem(this, !getTrustSetting(companyId), this.getClass().getName(), LINK, MSG, info));
		} catch (ConfigurationException e) {
			return Arrays.asList(new HealthcheckItem(this, e));
		}
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

	protected boolean getTrustSetting(long companyId) throws ConfigurationException {
		DDMDataProviderConfiguration companyConfiguration = configurationProvider
				.getCompanyConfiguration(DDMDataProviderConfiguration.class, companyId);
		return companyConfiguration.trustSelfSignedCertificates();
	}

	@Reference
	protected void setConfigurationProvider(ConfigurationProvider configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	@Reference
	private ConfigurationProvider configurationProvider;
}
