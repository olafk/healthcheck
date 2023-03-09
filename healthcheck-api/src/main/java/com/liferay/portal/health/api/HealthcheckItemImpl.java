package com.liferay.portal.health.api;

public class HealthcheckItemImpl implements HealthcheckItem {

	public HealthcheckItemImpl(boolean resolved, String source, String message, String link, String category) {
		this.resolved = resolved;
		this.source = source;
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
	
	@Override
	public String getKey() {
		return source + "-" + resolved;
	}
	
	private final boolean resolved;
	private final String source;
	private final String message;
	private final String link;
	private final String category;
}
