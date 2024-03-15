/**
 * Copyright (c) 2022-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.health.bestpractice;

import com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * Check various conditions for proper use of the (simple) FileSystemStore. It
 * stores all files in a single directory, which might lead to performance
 * problems. Note, that even if you don't experience performance problems at
 * runtime (because Java & your file system play well together, it might be
 * shell-based tools, e.g. used for backups, that break your neck. The expected
 * maximum number of files is configurable - the default is chosen arbitrarily,
 * to motivate a rather early change of store implementation, rather than a late
 * migration when the store has a humongous number of files already.
 * 
 * @author Olaf Kock
 */
@Component(configurationPid = { "com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration",
		"com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration" }, service = Healthcheck.class)
public class SimpleFileStoreConfigurationHealthcheck implements Healthcheck {

	private final String LINK = "https://docs.liferay.com/portal/7.4-latest/propertiesdoc/portal.properties.html#Document%20Library%20Service";
	private final String MSG = "healthcheck-simple-file-store-ok";
	private final String MSG_TOO_MANY_FILES = "healthcheck-simple-file-store-too-many-files";
	private final String MSG_UNUSED = "healthcheck-simple-file-store-unused";
	private final String MSG_NO_DIR = "healthcheck-simple-file-store-no-dir";
	private final String MSG_USABLE_SPACE = "healthcheck-simple-file-store-usable-space";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		Collection<HealthcheckItem> result;
		if (PropsValues.DL_STORE_IMPL.equals("com.liferay.portal.store.file.system.FileSystemStore")) {
			if (rootDir.isDirectory()) {
				int files = getRecursiveMaxFiles(rootDir, 0);
				if (files > maximumFiles) {
					Object[] info = { files, maximumFiles, rootDir.getAbsolutePath() };
					result = Arrays.asList(new HealthcheckItem(this, false, this.getClass().getName() + "-maxfiles", LINK, MSG_TOO_MANY_FILES, info));
				} else {
					Object[] info = { files, maximumFiles, rootDir.getAbsolutePath() };
					result = Arrays.asList(new HealthcheckItem(this, true, this.getClass().getName() + "-maxfiles", LINK, MSG, info));
				}
			} else {
				Object[] info = { rootDir.getAbsolutePath() };
				result = Arrays.asList(new HealthcheckItem(this, false, this.getClass().getName() + "-no-directory", LINK, MSG_NO_DIR, info));
			}
			Object[] info = { minimumUsableSpace, rootDir.getUsableSpace() };
			result.add(new HealthcheckItem(this, rootDir.getUsableSpace() > minimumUsableSpace, this.getClass().getName() + "-diskspace", null, MSG_USABLE_SPACE, info));
		} else {
			Object[] info = {};
			result = Arrays.asList(new HealthcheckItem(this, true, this.getClass().getName(), LINK, MSG_UNUSED, info));
		}
		return result;
	}

	private int getRecursiveMaxFiles(File dir, int max) {
		File[] files = dir.listFiles();
		int result = Math.max(max, files.length);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				result = getRecursiveMaxFiles(files[i], result);
			}
		}
		return result;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		HealthcheckBestPracticeConfiguration config = ConfigurableUtil
				.createConfigurable(HealthcheckBestPracticeConfiguration.class, properties);
		maximumFiles = config.maximumSimpleStoreFiles();
		minimumUsableSpace = config.minimumUsableSpace();
		Settings fileStoreSettings = settingsLocatorHelper.getConfigurationBeanSettings(
				"com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration");
		String rootPath = fileStoreSettings.getValue("rootDir", null);
		File dir = new File(rootPath);

		if (!dir.isAbsolute()) {
			dir = new File(PropsValues.LIFERAY_HOME, rootPath);
		}
		rootDir = dir;
	}

	@Reference
	private SettingsLocatorHelper settingsLocatorHelper;

	@Reference
	protected void setConfigurationProvider(ConfigurationProvider configurationProvider) {
		// configuration update will actually be handled in the @Modified event,
		// which will only be triggered in case we have a @Reference to the
		// ConfigurationProvider
	}

	private File rootDir;
	private Long minimumUsableSpace;
	private Integer maximumFiles;

	static Log _log = LogFactoryUtil.getLog(SimpleFileStoreConfigurationHealthcheck.class);
}
