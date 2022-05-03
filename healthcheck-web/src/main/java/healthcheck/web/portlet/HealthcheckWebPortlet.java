package healthcheck.web.portlet;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import healthcheck.web.constants.HealthcheckWebPortletKeys;

/**
 * @author Olaf Kock
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.ajaxable=true",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.remoteable=true",
		"javax.portlet.display-name=HealthcheckWeb",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + HealthcheckWebPortletKeys.HEALTHCHECKWEB,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=administrator"
	},
	service = Portlet.class
)
public class HealthcheckWebPortlet extends MVCPortlet {
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		List<HealthcheckItem> checks = new LinkedList<HealthcheckItem>();
		if(themeDisplay.getPermissionChecker().isCompanyAdmin(themeDisplay.getCompanyId())) {
			for (Healthcheck healthcheck : healthchecks) {
				checks.addAll(healthcheck.check(themeDisplay));
			}
		} else {
			Healthcheck dummy = new HealthcheckBaseImpl() {
				
				@Override
				public String getCategory() {
					return "healthcheck-category-generic";
				}
				
				@Override
				public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
					return wrap(create(false, themeDisplay.getLocale(), "/", "healthcheck-need-to-be-company-administrator"));
				}
			};
			checks.addAll(dummy.check(themeDisplay));
		}
		renderRequest.setAttribute("checks", checks);
		super.doView(renderRequest, renderResponse);
	}
	
	@Reference(			
			cardinality = ReferenceCardinality.MULTIPLE,
		    policyOption = ReferencePolicyOption.GREEDY,
		    unbind = "doUnRegister" 
	)
	void doRegister(Healthcheck healthcheck) {
		healthchecks.add(healthcheck);
	}
	
	void doUnregister(Healthcheck healthcheck) {
		healthchecks.remove(healthcheck);
	}

	List<Healthcheck> healthchecks = new LinkedList<Healthcheck>();

}