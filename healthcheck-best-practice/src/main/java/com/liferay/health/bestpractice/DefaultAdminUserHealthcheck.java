package com.liferay.health.bestpractice;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		service=Healthcheck.class
		)
public class DefaultAdminUserHealthcheck extends HealthcheckBaseImpl {

	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		try {
			User user = userLocalService.getUserByEmailAddress(themeDisplay.getCompanyId(), "test@liferay.com");
			if(user != null) {
				String hashedPassword = PasswordEncryptorUtil.encrypt("test", user.getPassword());
				return wrap(create(! user.getPassword().equals(hashedPassword), themeDisplay.getLocale(), LINK + LINK_PARAMETER + user.getUserId(), MSG));
			}
		} catch (NoSuchUserException e) {
			// ignore - this is great and exactly what we're after.
		} catch (PortalException e) {
			return wrap(create(themeDisplay.getLocale(), e));
		}
		return wrap(create(true, themeDisplay.getLocale(), LINK, MSG));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

	@Reference
	UserLocalService userLocalService;
	
	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_UsersAdminPortlet";
	private static final String LINK_PARAMETER = "&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_mvcRenderCommandName=%2Fusers_admin%2Fedit_user&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_p_u_i_d=";
	private static final String MSG = "healthcheck-bestpractice-default-account-with-default-password";

}
