package healthcheck.web.portlet;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import healthcheck.web.constants.HealthcheckWebPortletKeys;


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

	@Override
	public String getPortletId() {
		return HealthcheckWebPortletKeys.HEALTHCHECKWEB;
	}
	
	@Override
	@Reference(
		target = "(javax.portlet.name=" + HealthcheckWebPortletKeys.HEALTHCHECKWEB + ")",
		unbind = "-"
	)
	public void setPortlet(Portlet portlet) {
		super.setPortlet(portlet);
	}
}
