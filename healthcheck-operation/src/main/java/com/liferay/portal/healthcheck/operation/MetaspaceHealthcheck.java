package com.liferay.portal.healthcheck.operation;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;

/** 
 * DXP's default metaspace is 768M, and it might not run with less than this.
 * 
 * Make sure that sufficient Metaspace is configured.
 * 
 * @author Olaf Kock
 *
 */

@Component(
		service=Healthcheck.class
		)
public class MetaspaceHealthcheck extends HealthcheckBaseImpl {
	private static final String LINK = null;
	private static final String MSG = "healthcheck-max-metaspace-must-be-above-768m";

	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		for (MemoryPoolMXBean memoryMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
		    if ("Metaspace".equals(memoryMXBean.getName())) {
		            long maxMetaspace = memoryMXBean.getUsage().getMax();
		            return wrap(create(maxMetaspace>=768*1024*1024 || maxMetaspace==-1, 
		            		themeDisplay.getLocale(), LINK, MSG, maxMetaspace));
		    }
		}
		
        return wrap(create(false, themeDisplay.getLocale(), LINK, MSG, "undetected"));
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}

}
