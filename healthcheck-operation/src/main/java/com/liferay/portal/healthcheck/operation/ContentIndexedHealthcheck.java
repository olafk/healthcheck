package com.liferay.portal.healthcheck.operation;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * A very basic check for an existing full-text-index (elasticsearch).
 * This check only checks the number of users retrieved through 
 * database and through index to be identical as a smoke test.
 * 
 * @author Olaf Kock
 */


@Component(
		service = Healthcheck.class
		)
public class ContentIndexedHealthcheck extends HealthcheckBaseImpl {
	private static final String LINK = "/group/control_panel/manage?p_p_id=com_liferay_portal_search_admin_web_portlet_SearchAdminPortlet&_com_liferay_portal_search_admin_web_portlet_SearchAdminPortlet_tabs1=index-actions";
	private static final String MSG = "healthcheck-content-indexed";
	private static final String ERROR_MSG = "healthcheck-content-indexed-error";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		CountSearchRequest countSearchRequest = new CountSearchRequest();
		countSearchRequest.setIndexNames(indexNameBuilder.getIndexName(companyId));
		TermQuery termQuery = new TermQueryImpl(
			Field.ENTRY_CLASS_NAME, User.class.getName());

		countSearchRequest.setQuery(termQuery);
		CountSearchResponse countSearchResponse =
			searchEngineAdapter.execute(countSearchRequest);
		long indexCount = countSearchResponse.getCount();
		long dbCount = userLocalService.getCompanyUsersCount(companyId);
		
		boolean exists = (indexCount >= dbCount);
		return wrap(create(exists,  locale, LINK, exists ? MSG : ERROR_MSG, indexCount, dbCount));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

	@Reference
	SearchEngineAdapter searchEngineAdapter;

	@Reference
	UserLocalService userLocalService;
	
	@Reference
	IndexNameBuilder indexNameBuilder;
}
