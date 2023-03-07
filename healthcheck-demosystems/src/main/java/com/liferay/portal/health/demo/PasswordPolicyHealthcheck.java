package com.liferay.portal.health.demo;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Healthcheck for relaxed-security Demo Systems
 * When an administrator resets a user's password, the default is to require
 * a user to change the password again. In demo systems, administrators and users
 * are typically the same people, so a password would need to be reset twice,
 * which just slows down a demo.
 *  
 * @author Olaf Kock
 */

@Component( 
		service = Healthcheck.class 
)
public class PasswordPolicyHealthcheck extends HealthcheckBaseImpl {

	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_password_policies_admin_web_portlet_PasswordPoliciesAdminPortlet";
	private static final String MSG = "healthcheck-password-policy-change-required";
	private static final String LINK_PARAM = "&p_p_lifecycle=0&_com_liferay_password_policies_admin_web_portlet_PasswordPoliciesAdminPortlet_mvcPath=%2Fedit_password_policy.jsp&_com_liferay_password_policies_admin_web_portlet_PasswordPoliciesAdminPortlet_passwordPolicyId=";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		try {
			PasswordPolicy passwordPolicy = passwordPolicyLocalService.getDefaultPasswordPolicy(companyId);
			boolean changeRequired = passwordPolicy.getChangeRequired();
			return wrap(create(!changeRequired, locale, 
					LINK + LINK_PARAM + passwordPolicy.getPasswordPolicyId(),
					MSG));
		} catch (PortalException e) {
			return wrap(create(this, locale, e)); 
		}
	}

	@Reference
	PasswordPolicyLocalService passwordPolicyLocalService;

	@Override
	public String getCategory() {
		return "healthcheck-category-demosystem";
	}
}
