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
import java.util.Optional;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.gvcc.goffice.multitenancy.TenantConfigurator;

/**
 *
 * <p>
 * The <code>SchemaBasedMultitenantConnectionProvider</code> class
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author cristian muraca
 * @version 1.0
 */
public class SchemaBasedMultitenantConnectionProvider implements MultiTenantConnectionProvider {
	private static final long serialVersionUID = 1L;

	/**
	 * loggerfor the class
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(SchemaBasedMultitenantConnectionProvider.class);

	/**
	 * the available datasource
	 */
	@Autowired
	private DataSource dataSource;

	/**
	 * Service which allows to both access the current tenant and set a new tenant
	 */
	@Autowired
	private TenantConfigurator tenantConfigurator;

	/**
	 * Name of the database
	 */
	private String databaseName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider#getAnyConnection()
	 */
	@Override
	public Connection getAnyConnection() throws SQLException {
		return dataSource.getConnection();
	}

	// @Override
	// public Connection getConnection(String tenantIdentifier) throws SQLException {
	// final Connection connection = getAnyConnection();
	// try {
	// if (tenantIdentifier != null) {
	// switchDatabase(tenantIdentifier, connection);
	// }
	// } catch (SQLException e) {
	// throw new HibernateException("Problem setting schema to " + tenantIdentifier, e);
	// }
	// return connection;
	// }

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		LOGGER.debug("getConnection - START");

		// logger.info("Get connection for tenant {}", tenantIdentifier);
		final Connection connection = getAnyConnection();
		Optional<String> dbSchema = tenantConfigurator.getSchemaFromKeycloakId(tenantIdentifier);
		if (dbSchema.isPresent()) {
			connection.setSchema(dbSchema.get());
		} else {
			LOGGER.error("Unable to identify the db schema from: {}", tenantIdentifier);

			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					LOGGER.error("Unable to close the connection", e);
				}
			}

			throw new SQLException("Unable to identify the db schema from: " + tenantIdentifier);
		}

		LOGGER.debug("getConnection - END");

		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider#releaseAnyConnection(java.sql.Connection)
	 */
	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider#releaseConnection(java.lang.String, java.sql.Connection)
	 */
	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		connection.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider#supportsAggressiveRelease()
	 */
	@Override
	public boolean supportsAggressiveRelease() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.service.spi.Wrapped#isUnwrappableAs(java.lang.Class)
	 */
	@Override
	public boolean isUnwrappableAs(Class arg0) {
		return false;
	}

	/*
	 * 
	 * @see org.hibernate.service.spi.Wrapped#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> arg0) {
		try {
			return dataSource.unwrap(arg0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param databaseName
	 *            the name of the database
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * @return the name of the database
	 */
	public String getDatabaseName() {
		return databaseName;
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

}
