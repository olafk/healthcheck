package com.liferay.portal.health.api;

import java.util.Set;

public interface AccessedUrlRegister {
	public Set<String> getAccessedUrls(long companyId);
}
