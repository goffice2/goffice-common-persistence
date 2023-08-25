/*
 * goffice... 
 * https://www.goffice.org
 * 
 * Copyright (c) 2005-2022 Consorzio dei Comuni della Provincia di Bolzano Soc. Coop. <https://www.gvcc.net>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.gvcc.goffice.multitenancy.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.gvcc.goffice.multitenancy.ITenantService;

/**
 *
 * <p>
 * The <code>DataSourceMultiTenantConnectionProvider</code> class
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author cristian muraca
 * @version 1.0
 */
@Import(DataSourceMultiTenantSelector.class)
public class DataSourceMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(DataSourceMultiTenantConnectionProvider.class);

	private static final long serialVersionUID = 1L;

	@Autowired
	private DataSourceMultiTenantSelector datasourceSelector;

	@Autowired
	private ITenantService tenantService;

	/**
	 * The configured datasources
	 */
	private Map<String, DataSourceMultiTenantMap> multipleDataSources;

	private static Set<String> openedConnectionIds = new HashSet<>();

	/**
	 * @param multipleDataSources
	 *            the map of the configured dasources
	 */
	@SuppressFBWarnings(value = { "MS_EXPOSE_REP", "EI_EXPOSE_REP", "EI_EXPOSE_REP2" }, justification = "avoid clone objects")
	public DataSourceMultiTenantConnectionProvider(Map<String, DataSourceMultiTenantMap> multipleDataSources) {
		setDataSources(multipleDataSources);
	}

	/**
	 * Set the datasources
	 * 
	 * @param multipleDataSources
	 */
	public void setDataSources(Map<String, DataSourceMultiTenantMap> multipleDataSources) {
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// the following source code has to be hardly tested!!! But, perhaps, it could be useful to reloading DBMS datasources //
		// see: MultiTenantJPAConfiguration.multiTenantConnectionProvider() /////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// if (this.multipleDataSources != null) {
		// this.multipleDataSources.values().forEach(item -> {
		// item.values().forEach(elem -> {
		// DataSource ds = elem.getDataSource();
		// if (ds != null) {
		// try (HikariDataSource hikariDs = (HikariDataSource) ds) {
		// HikariPoolMXBean poolBean = hikariDs.getHikariPoolMXBean();
		// poolBean.softEvictConnections();
		// } catch (Exception e) {
		// LOGGER.error("setDataSources", e);
		// }
		// }
		// });
		//
		// });
		// this.multipleDataSources.clear();
		// }
		this.multipleDataSources = multipleDataSources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl#selectAnyDataSource()
	 */
	@Override
	protected DataSource selectAnyDataSource() {
		LOGGER.trace("selectAnyDataSource - START");

		DataSource dataSource = selectDataSource(tenantService.getTenant());

		LOGGER.trace("selectAnyDataSource - END");

		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl#selectDataSource(java.lang.String)
	 */
	@Override
	protected DataSource selectDataSource(String tenantName) {
		LOGGER.trace("selectDataSource - START");
		LOGGER.trace("selectDataSource - tenantName={}", tenantName);

		DataSource dataSource = null;

		if (multipleDataSources.isEmpty()) {
			LOGGER.warn("selectDataSource - ********************* NO DATASOURCES FOUND *********************");
		} else if (StringUtils.isBlank(tenantName)) {
			LOGGER.warn("selectDataSource - ********************* NO TENANT DEFINED *********************");
		} else {
			DataSourceMultiTenantMap tenantDatasourceMap = multipleDataSources.get(tenantName.toLowerCase());
			int count = tenantDatasourceMap == null ? 0 : tenantDatasourceMap.size();
			final String msg = "selectDataSource - found {} DBMS configuration for tenant '{}'";
			if (count == 0) {
				LOGGER.error(msg, count, tenantName);
			} else {
				LOGGER.trace(msg, count, tenantName);
			}

			if (tenantDatasourceMap != null && !tenantDatasourceMap.isEmpty()) {
				String selector = datasourceSelector.getDatabaseIdentifier();

				final String prefix = "selectDataSource - searching for datasource instance...";

				DataSourceMultiTenant dsMultiTenant = null;
				if (selector == null) { // fallback to DEFAULT/MAIN datasource
					LOGGER.trace(prefix.concat("looking for the MAIN datasource..."));
					dsMultiTenant = tenantDatasourceMap.values().stream() //
							.filter(DataSourceMultiTenant::isMain) //
							.findFirst() //
							.orElse(null);
				} else {
					LOGGER.trace(prefix.concat("using selector: {}"), selector);
					dsMultiTenant = tenantDatasourceMap.get(selector);
				}

				if (dsMultiTenant == null) {
					LOGGER.error(prefix.concat("ERROR: datasource was NOT found (tenant={}, selector={})"), tenantName, StringUtils.defaultIfEmpty(selector, "<MAIN>"));
				} else {
					LOGGER.info(prefix.concat("DONE (datasource found: tenant={}, selector={}, config name={})"), tenantName, StringUtils.defaultIfEmpty(selector, "<MAIN>"), dsMultiTenant.getName());
					dataSource = dsMultiTenant.getDataSource();
				}
			}
		}

		LOGGER.trace("selectDataSource - dataSource={}", dataSource);
		LOGGER.trace("selectDataSource - END");

		return dataSource;
	}

	/**
	 * Used to force the serializable behavior if Serializable is not implemented
	 * 
	 * @param stream
	 *            output stream to write the object to
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred.
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
	}

	/**
	 * Used to force the serializable behavior if Serializable is not implemented
	 * 
	 * @param stream
	 *            input stream to read the object to
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred. Thisclass is the general class of exceptions produced by failed orinterrupted I/O operations.
	 * @throws ClassNotFoundException
	 *             when an application tries to load in a class through itsstring name
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		LOGGER.trace("getConnection - START");
		LOGGER.trace("getConnection - tenantIdentifier={}", tenantIdentifier);

		Connection connection = super.getConnection(tenantIdentifier);
		registerConnection(connection);

		LOGGER.trace("getConnection - END");

		return connection;
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		LOGGER.trace("releaseAnyConnection - START");
		LOGGER.trace("releaseConnection - connection={}", connection);

		unregisterConnection(connection);
		super.releaseAnyConnection(connection);

		LOGGER.trace("releaseAnyConnection - END");
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		LOGGER.trace("releaseConnection - START");
		LOGGER.trace("releaseConnection - tenantIdentifier={}", tenantIdentifier);
		LOGGER.trace("releaseConnection - connection={}", connection);

		unregisterConnection(connection);
		super.releaseConnection(tenantIdentifier, connection);

		LOGGER.trace("releaseConnection - END");
	}

	private static void registerConnection(Connection connection) {
		synchronized (openedConnectionIds) {
			if (connection != null) {
				try {
					String key = makeKey(connection);
					LOGGER.trace("registerConnection - connection={}", key);
					openedConnectionIds.add(key);
					LOGGER.trace("registerConnection - opened count={}", openedConnectionIds.size());
				} catch (SQLException e) {
					LOGGER.error("unregisterConnection", e);
				}
			}
		}
	}

	private static void unregisterConnection(Connection connection) {
		synchronized (openedConnectionIds) {
			if (connection != null) {
				try {
					String key = makeKey(connection);
					LOGGER.trace("unregisterConnection - connection={}", key);
					openedConnectionIds.remove(key);
					LOGGER.trace("unregisterConnection - opened count={}", openedConnectionIds.size());
				} catch (SQLException e) {
					LOGGER.error("unregisterConnection", e);
				}
			}
		}
	}

	private static String makeKey(Connection connection) throws SQLException {
		return connection.unwrap(Connection.class).toString();
	}
}