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

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.comparator.UserEmailAddressComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Check if all existing user accounts save passwords with the currently
 * configured hashing algorithm. Danger: Might run a while for large user
 * databases. Currently Healthchecks are a proof of concept, so this aspect is
 * ignored/accepted
 * 
 * @author Olaf Kock
 */
@Component(service = Healthcheck.class)
public class UserPasswordPBKDF2HashHealthcheck extends HealthcheckBaseImpl {

	private static final String PBKDF2_WITH_HMAC_SHA1 = "PBKDF2WithHmacSHA1";
	private static final String LINK = "https://liferay.dev/blogs/-/blogs/hashing-performance";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		String hashingAlgorithm = PropsUtil.get(PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM);
		if (hashingAlgorithm == null) {
			return wrap(create(false, locale, LINK, "healthcheck-best-practice-pbkdf2-user-unconfigured-algorithm"));
		} else if (!hashingAlgorithm.startsWith(PBKDF2_WITH_HMAC_SHA1)) {
			return wrap(create(true, locale, LINK,
					"healthcheck-best-practice-pbkdf2-unknown-hashing-algorithm-assuming-ok", hashingAlgorithm));
		}

		HashMap<String, Long> algorithms = new HashMap<String, Long>();
		LinkedList<HealthcheckItem> result = new LinkedList<HealthcheckItem>();

		int usersCount = userLocalService.getUsersCount(companyId, WorkflowConstants.STATUS_APPROVED);
		int counted = 0;
		int pageSize = 100;
		for (int i = 0; i <= usersCount / pageSize; i++) {
			List<User> users;
			try {
				users = userLocalService.getUsers(companyId, WorkflowConstants.STATUS_APPROVED, i * pageSize,
						((i + 1) * pageSize), new UserEmailAddressComparator());
				for (User user : users) {
					String pwd = user.getPassword();
					String algorithm = getAlgorithm(pwd);
					countUp(algorithms, algorithm);
					counted++;
				}
			} catch (Throwable e) {
				countUp(algorithms, e.getClass().getName() + " " + e.getMessage());
			}
		}

		if (counted != usersCount) {
			result.add(create(false, locale, LINK, "healthcheck-best-practice-user-count-mismatch-x-uncounted",
					"" + (usersCount - counted) + "/" + usersCount));
		}

		for (HashMap.Entry<String, Long> entry : algorithms.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(hashingAlgorithm)) {
				result.add(create(true, locale, LINK,
						"healthcheck-best-practice-pbkdf2-found-x-entries-with-default-algorithm-y",
						"" + entry.getValue() + "/" + usersCount, hashingAlgorithm));
			} else {
				result.add(create(false, locale, LINK,
						"healthcheck-best-practice-pbkdf2-found-x-entries-with-nondefault-algorithm-y-looking-for-z",
						"" + entry.getValue() + "/" + usersCount, entry.getKey(), hashingAlgorithm));
			}
		}
		return result;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-best-practice";
	}

	private void countUp(Map<String, Long> algorithms, String algorithm) {
		Long currentValue = algorithms.get(algorithm);
		if (currentValue == null)
			currentValue = Long.valueOf(0);
		currentValue++;
		algorithms.put(algorithm, currentValue);

	}

	private String getAlgorithm(String encryptedPassword) throws PwdEncryptorException {

		String alg = "unknown";

		if (encryptedPassword.charAt(0) == '{') {
			int index = encryptedPassword.indexOf('}');
			if (index > 0) {
				alg = encryptedPassword.substring(1, index);
			}
		}

		if (alg.equalsIgnoreCase(PBKDF2_WITH_HMAC_SHA1)) {
			encryptedPassword = encryptedPassword.substring(PBKDF2_WITH_HMAC_SHA1.length() + 2); // +2 for {}
			ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.decode(encryptedPassword));

			try {
				int keySize = byteBuffer.getInt();
				int rounds = byteBuffer.getInt();
				return alg + "/" + keySize + "/" + rounds;
			} catch (BufferUnderflowException e) {
				return alg + "/?/?/" + e.getMessage();
			}
		} else if (alg.equals("BCRYPT")) {
			// TODO unknown encoding of work factor
		}
		return alg;
	}

	@Reference
	UserLocalService userLocalService;
}
