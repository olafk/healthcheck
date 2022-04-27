package com.liferay.portal.healthcheck.operation;

import static com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys.INSTANCE_SETTINGS;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
		configurationPid = RedirectHealthcheck.PID,
		property = Constants.SERVICE_PID + "=" + RedirectHealthcheck.PID + ".scoped",
		service = { Healthcheck.class, ManagedServiceFactory.class } 
		
		)
public class RedirectHealthcheck extends HealthcheckBaseImpl implements ManagedServiceFactory {

	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		long companyId = themeDisplay.getCompanyId();
		Locale locale = themeDisplay.getLocale();
		HttpServletRequest request = themeDisplay.getRequest();
		String currentURL = request.getRequestURL().toString();
		String serverName = request.getServerName();

		String url = PortalUtil.escapeRedirect(currentURL);
		
		return wrap(create(url!=null, 
				locale, 
				PARTIAL_LINK + companyToRedirectConfigPid.get(companyId), 
				"healthcheck-redirection-url", 
				serverName));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}
	
	@Override
	public String getName() {
		return PID + ".scoped";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		long companyId = (Long) properties.get("companyId");
		companyToRedirectConfigPid.put(companyId, pid);
		
		log.debug("adding redirection config for " + companyId + " as " + pid);
	}
	
	@Override
	public void deleted(String pid) {
		Set<Entry<Long, String>> es = companyToRedirectConfigPid.entrySet();
		for (Entry<Long, String> entry : es) {
			if(entry.getValue().equals(pid)) {
				Long companyId = entry.getKey();
				companyToRedirectConfigPid.remove(companyId);
		
				log.debug("removing redirection config for " + companyId);
				break;
			}
		}
	}

	static final String PID = "com.liferay.redirect.internal.configuration.RedirectURLConfiguration";
	private HashMap<Long,String> companyToRedirectConfigPid = new HashMap<Long, String>();
	private static Log log = LogFactoryUtil.getLog(RedirectHealthcheck.class);
	private static String PARTIAL_LINK = "/group/control_panel/manage?p_p_id=" + 
							INSTANCE_SETTINGS + "&" + 
							"_" + INSTANCE_SETTINGS + "_factoryPid=" + PID + "&" + 
							"_" + INSTANCE_SETTINGS + "_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&" + 
							"_" + INSTANCE_SETTINGS + "_pid=";

}
