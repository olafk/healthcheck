package com.liferay.health.bestpractice;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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
 * It can only store a limited amount of files due to OS limitations for
 * the number of files in one folder. 
 *  
 * @author Olaf Kock
 */
@Component(
		configurationPid = "com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration",
		service = Healthcheck.class
		)
public class SimpleFileStoreConfigurationHealthcheck extends HealthcheckBaseImpl {

	private final String LINK = "https://docs.liferay.com/portal/7.4-latest/propertiesdoc/portal.properties.html#Document%20Library%20Service";
	private final String MSG = "healthcheck-simple-file-store-ok";
	private final String MSG_TOO_MANY_FILES = "healthcheck-simple-file-store-too-many-files";
	private final String MSG_UNUSED = "healthcheck-simple-file-store-unused";
	private final String MSG_NO_DIR = "healthcheck-simple-file-store-no-dir";
	private final int WARNING_LIMIT = 500;
	
	@Override
	public Collection<HealthcheckItem> check(ThemeDisplay themeDisplay) {
		Locale locale = themeDisplay.getLocale();
		if(PropsValues.DL_STORE_IMPL.equals("com.liferay.portal.store.file.system.FileSystemStore")) {
			if(getRootDir().isDirectory()) {
				int files = getRecursiveMaxFiles(getRootDir(), 0);
				if(files > WARNING_LIMIT) {
					return wrap(create(false, locale, LINK, MSG_TOO_MANY_FILES, files, WARNING_LIMIT, getRootDir().getAbsolutePath()));
				} else {
					return wrap(create(true, locale, LINK, MSG, files, WARNING_LIMIT, getRootDir().getAbsolutePath()));
				}
			} else {
				return wrap(create(false, locale, LINK, MSG_NO_DIR, getRootDir().getAbsolutePath()));
			}
		}
		return wrap(create(true, locale, LINK, MSG_UNUSED));
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
		rootPath = (String) properties.get("rootDir");
	}

	protected File getRootDir() {
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
}
