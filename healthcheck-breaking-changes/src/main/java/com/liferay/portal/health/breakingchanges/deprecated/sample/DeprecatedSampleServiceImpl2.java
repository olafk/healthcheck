package com.liferay.portal.health.breakingchanges.deprecated.sample;

import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true
		)
public class DeprecatedSampleServiceImpl2 implements DeprecatedSampleService {

	@Override
	public void doSomething() {
	}

}
