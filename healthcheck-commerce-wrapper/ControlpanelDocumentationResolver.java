package de.olafkock.liferay.documentation.controlpanel;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import de.olafkock.liferay.documentation.api.DocumentationEntry;
import de.olafkock.liferay.documentation.api.DocumentationResolver;

/**
 * @author olaf
 */
@Component(
	immediate = true,
	configurationPid = "de.olafkock.liferay.documentation.controlpanel.CPDocConfiguration",
	property = {
			"de.olafkock.liferay.usage=ControlPanelDocumentation"
	},
	service = DocumentationResolver.class
)
public class ControlpanelDocumentationResolver implements DocumentationResolver {

	@Override
	public int getOrder() {
		return 0;
	}
	
	@Override
	public DocumentationEntry getDocumentationEntry(HttpServletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		boolean doFilter = themeDisplay.getTheme().isControlPanelTheme()
				&& ! "pop_up".equals(request.getParameter("p_p_state"));
		
		
		if(doFilter) {
			String portletId = PortalUtil.getPortletId(request);
			String secondary = getSecondaryTopic(request, portletId);
			log.debug("ControlPanel: Targeted Portlet ID: " + portletId + "/" + secondary);
			return repository.getEntry(portletId, secondary);
		}	

		return null;
	}
	
	protected String getSecondaryTopic(HttpServletRequest request, String portletId) {
		for (String p : SECONDARY_KEYS) {
			// we're operating on the decorated original servlet request, namespace still not resolved
			// will break in case this way of namespacing ever changes.
			String result = request.getParameter("_" + portletId + "_" + p);
			if (result != null)
				return HtmlUtil.escape(simplify(result));
		}
		return "-";
	}

	private String simplify(String result) {
		if(result.startsWith("/")) {
			result = result.substring(1);
		}
		result = result.replace('/', '_');
		return result;
	}

	@Reference
	protected void setConfigurationProvider(ConfigurationProvider configurationProvider) {
	    // configuration update will actually be handled in the @Modified event,
		// which will only be triggered in case we have a @Reference to the 
		// ConfigurationProvider
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		config = ConfigurableUtil.createConfigurable(CPDocConfiguration.class, 
				properties);
		repository = new CPDocRepository(config);
	}

	Log log = LogFactoryUtil.getLog(getClass());

	private volatile CPDocConfiguration config = null;

	private CPDocRepository repository = null;
	
	static final String[] SECONDARY_KEYS = new String[] { "toolbarItem", "type", "navigation", "tab", 
			"tabs1", "tabs2", "configurationScreenKey", "pid", "factoryPid", "roleType",
			"mvcRenderCommandName",	"mvcPath", "commerceAdminModuleKey"};
}
