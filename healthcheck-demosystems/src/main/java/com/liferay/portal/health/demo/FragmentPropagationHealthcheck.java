package com.liferay.portal.health.demo;

import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(configurationPid = "com.liferay.fragment.configuration.FragmentServiceConfiguration", service = Healthcheck.class)
public class FragmentPropagationHealthcheck implements Healthcheck {
	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fview_configuration_screen&_com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet_configurationScreenKey=fragments-service-company";
	private static final String MSG = "healthcheck-fragment-propagation";

	private boolean propagate = false;

	@Override
	public Collection<HealthcheckItem> check(long companyId) {
		Object[] info = {};
		return Arrays.asList(new HealthcheckItem(this, propagate, this.getClass().getName(), LINK, MSG, info));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-demosystem";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		FragmentServiceConfiguration config = ConfigurableUtil.createConfigurable(FragmentServiceConfiguration.class,
				properties);
		propagate = config.propagateContributedFragmentChanges();
		_log.fatal(PortalUUIDUtil.generate());
	}

	static Log _log = LogFactoryUtil.getLog(FragmentPropagationHealthcheck.class);
}
