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
import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import de.olafkock.liferay.configuration.api.GenericConfigurationLookup;
/**
 * Check various conditions for proper use of the (simple) FileSystemStore.
 * It stores all files in a single directory, which might lead to performance problems. Note, that
 * even if you don't experience performance problems at runtime (because Java & your file system 
 * play well together, it might be shell-based tools, e.g. used for backups, that break your neck.
 * The expected maximum number of files is configurable - the default is chosen arbitrarily, to 
 * motivate a rather early change of store implementation, rather than a late migration when the
 * store has a humongous number of files already. 
 *  
 * @author Olaf Kock
 */
@Component(
		configurationPid = {
				"com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration",
				"com.liferay.health.bestpractice.configuration.HealthcheckBestPracticeConfiguration"
				},
		service = Healthcheck.class
		)
public class SimpleFileStoreConfigurationHealthcheck extends HealthcheckBaseImpl {

	private final String LINK = "https://docs.liferay.com/portal/7.4-latest/propertiesdoc/portal.properties.html#Document%20Library%20Service";
	private final String MSG = "healthcheck-simple-file-store-ok";
	private final String MSG_TOO_MANY_FILES = "healthcheck-simple-file-store-too-many-files";
	private final String MSG_UNUSED = "healthcheck-simple-file-store-unused";
	private final String MSG_NO_DIR = "healthcheck-simple-file-store-no-dir";
	private final String MSG_USABLE_SPACE = "healthcheck-simple-file-store-usable-space";
	
	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		Collection<HealthcheckItem> result; 
		if(PropsValues.DL_STORE_IMPL.equals("com.liferay.portal.store.file.system.FileSystemStore")) {
			if(getRootDir().isDirectory()) {
				int files = getRecursiveMaxFiles(getRootDir(), 0);
				if(files > maximumFiles) {
					result = wrap(create(
							false,
							this.getClass().getName() + "-maxfiles",
							locale, 
							LINK, 
							MSG_TOO_MANY_FILES, 
							files, 
							maximumFiles, 
							getRootDir().getAbsolutePath()));
				} else {
					result = wrap(create(
							true,
							this.getClass().getName() + "-maxfiles",
							locale, 
							LINK, 
							MSG, 
							files, 
							maximumFiles, 
							getRootDir().getAbsolutePath()));
				}
			} else {
				result = wrap(create(
						false,
						this.getClass().getName() + "-no-directory",
						locale, 
						LINK, 
						MSG_NO_DIR, 
						getRootDir().getAbsolutePath()));
			}
			result.add(create(getRootDir().getUsableSpace() > minimumUsableSpace,
					this.getClass().getName() + "-diskspace",
					locale,
					null,
					MSG_USABLE_SPACE,
					minimumUsableSpace,
					getRootDir().getUsableSpace()));
		} else {
			result = wrap(create(true, locale, LINK, MSG_UNUSED));
		}
		return result;
	}
	
	private int getRecursiveMaxFiles(File dir, int max) {
		File[] files = dir.listFiles();
		int result = Math.max(max, files.length);
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
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
		HealthcheckBestPracticeConfiguration config = ConfigurableUtil.createConfigurable(HealthcheckBestPracticeConfiguration.class, properties);
		maximumFiles = config.maximumSimpleStoreFiles();
		minimumUsableSpace = config.minimumUsableSpace();
	}

	protected File getRootDir() {
		// as we don't have classloader access to the configuration class FileSystemStoreConfiguration (it's not exported), 
		// we'll need to figure out the configured value manually, by resolving default values explicitly.
		if(rootDir == null) {
			if(rootPath == null) {
				rootPath = configurationLookup.getDefaultValue("com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration", "rootDir");
			}

			rootDir = new File(rootPath);
	
			if (!rootDir.isAbsolute()) {
				rootDir = new File(PropsValues.LIFERAY_HOME, rootPath);
			}
		}
		return rootDir;
	}
	
	
	@Reference
	protected void setConfigurationProvider(ConfigurationProvider configurationProvider) {
	    // configuration update will actually be handled in the @Modified event,
		// which will only be triggered in case we have a @Reference to the 
		// ConfigurationProvider
	}
	
	@Reference
	protected GenericConfigurationLookup configurationLookup;
	
	private File rootDir;
	private String rootPath;
	private Long minimumUsableSpace;
	private Integer maximumFiles;
	
	static Log _log = LogFactoryUtil.getLog(SimpleFileStoreConfigurationHealthcheck.class);

}
