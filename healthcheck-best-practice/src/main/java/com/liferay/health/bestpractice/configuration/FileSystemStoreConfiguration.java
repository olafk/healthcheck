package com.liferay.health.bestpractice.configuration;

/**
 * As we don't have access to the original FileSystemStoreConfiguration class,
 * but are interested in its rootDir configuration: Here's the replacement we 
 * need in order to get the value comfortably through ConfigurableUtil.
 */

public interface FileSystemStoreConfiguration {
	public String rootDir();
}