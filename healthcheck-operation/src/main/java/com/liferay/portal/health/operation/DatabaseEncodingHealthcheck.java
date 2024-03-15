package com.liferay.portal.health.operation;

import com.liferay.portal.health.api.Healthcheck;
import com.liferay.portal.health.api.HealthcheckBaseImpl;
import com.liferay.portal.health.api.HealthcheckItem;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

@Component(service = Healthcheck.class)
public class DatabaseEncodingHealthcheck extends HealthcheckBaseImpl {

	private static final String MSG = "healthcheck-database-encoding";
	private static final String MSG_SCHEMA_UNDETECTED = "healthcheck-database-schema-undetected";
	private static final String MSG_HSQL = "healthcheck-database-hsql-for-demo";
	private static final String MSG_DB_UNDETECTED = "healthcheck-database-undetected";
	private static final String LINK_DB_CONNECTION = "https://learn.liferay.com/w/dxp/installation-and-upgrades/installing-liferay/configuring-a-database";

	@Override
	public Collection<HealthcheckItem> check(long companyId, Locale locale) {
		Collection<HealthcheckItem> result = new LinkedList<HealthcheckItem>();

		DB db = DBManagerUtil.getDB();
		try (Connection connection = DataAccess.getConnection()) {
			DBType dbType = db.getDBType();
			String encoding = "not-yet-implemented";
			boolean correctEncoding = false;
			String connectionURL = "no URL detected";

			if (DBType.MYSQL.equals(dbType) || DBType.MARIADB.equals(dbType)) {
				encoding = "undetected";
				connectionURL = connection.getMetaData().getURL();
				String schema = extractSchema(connectionURL);
				if (schema != null) {
					PreparedStatement stmt = connection.prepareStatement("SELECT default_character_set_name "
							+ "FROM information_schema.SCHEMATA " + "WHERE schema_name = ?");
					stmt.setString(1, schema);
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						encoding = rs.getString(1);
						if (encoding != null && encoding.toLowerCase().startsWith("utf8")) {
							correctEncoding = true;
						}
					}
				} else {
					Object[] info = { connectionURL };
					result.add(new HealthcheckItem(this, false, this.getClass().getName(), LINK_DB_CONNECTION, MSG_SCHEMA_UNDETECTED, info));
				}
			} else if (DBType.ORACLE.equals(dbType)) {
				// TODO
			} else if (DBType.POSTGRESQL.equals(dbType)) {
				// TODO
			} else if (DBType.DB2.equals(dbType)) {
				// TODO
			} else if (DBType.SQLSERVER.equals(dbType)) {
				// TODO
			} else if (DBType.SYBASE.equals(dbType)) {
				// TODO
			} else if (DBType.HYPERSONIC.equals(dbType)) {
				Object[] info = {};
				result.add(new HealthcheckItem(this, false, this.getClass().getName(), LINK_DB_CONNECTION, MSG_HSQL, info));
				return result;
			} else {
				Object[] info = {};
				result.add(new HealthcheckItem(this, false, this.getClass().getName(), LINK_DB_CONNECTION, MSG_DB_UNDETECTED, info));
				return result;
			}
			Object[] info = { encoding, dbType, connectionURL };
			result.add(new HealthcheckItem(this, correctEncoding, this.getClass().getName(), LINK_DB_CONNECTION, MSG, info));
		} catch (SQLException e) {
			result.add(create3(this, locale, e));
		}
		return result;
	}

	private String extractSchema(String connectionURL) {
		// assume something like jdbc:mariadb://localhost/lportal?andsomethingelse
		// very crude deciphering, the initial regexp-try looked like write-only code...
		int pos = connectionURL.indexOf("//");
		if (pos >= 0) {
			pos = connectionURL.indexOf("/", pos + 2);
			if (pos > -1) {
				String result = connectionURL.substring(pos + 1);
				pos = result.indexOf("?");
				if (pos > -1) {
					result = result.substring(0, pos);
				}
				return result;
			}
		}
		return null;
	}

	@Override
	public String getCategory() {
		return "healthcheck-category-operation";
	}
}
