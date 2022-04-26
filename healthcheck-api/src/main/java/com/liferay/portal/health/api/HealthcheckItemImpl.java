package com.liferay.portal.health.api;

public class HealthcheckItemImpl implements HealthcheckItem {
	public HealthcheckItemImpl(boolean resolved, String message, String link, String category) {
		this.resolved = resolved;
		this.link = link;
		this.message = message;
		this.category = category;
	}
	
	@Override
	public boolean isResolved() {
		return resolved;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public String getLink() {
		return link;
	}

	@Override
	public String getCategory() {
		return category;
	}
	
	private final boolean resolved;
	private final String message;
	private String link;
	private String category;
}
