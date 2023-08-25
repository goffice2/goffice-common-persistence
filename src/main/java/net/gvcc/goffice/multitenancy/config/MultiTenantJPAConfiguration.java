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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.gvcc.goffice.crypt.CryptManager;
import net.gvcc.goffice.multitenancy.TenantConfigurator;
import net.gvcc.goffice.multitenancy.TenantInfo;
import net.gvcc.goffice.multitenancy.config.dbms.DbmsTenantCatalog;
import net.gvcc.goffice.multitenancy.config.dbms.DbmsTenantInfo;
import net.gvcc.goffice.multitenancy.envers.RevInfo;
import net.gvcc.goffice.troubleshooting.actions.EmailServiceImpl;
import net.gvcc.goffice.troubleshooting.actions.TroubleshootingService;

/**
 *
 * <p>
 * The <code>MultiTenantJPAConfiguration</code> class
 * </p>
 * The class, if present in the spring context will add to the context all the necessary beans to perform JPA Persistence and Multitenancy in Goffice2 Microservices
 * 
 * The class is intended to be added through the usage of the {@link net.gvcc.goffice.multitenancy.annotation.EnableMultiTenancy} annotation on an existing
 * {@link org.springframework.context.annotation.Configuration} class
 * 
 * It will also enable the possibility to configure the multitenancy strategy based on the value of the multitenancy.type property set. This is done by exposing a <code>@Bean</code> either of type
 * {@link net.gvcc.goffice.multitenancy.config.DataSourceMultiTenantConnectionProvider} or {@link net.gvcc.goffice.multitenancy.config.SchemaBasedMultitenantConnectionProvider} Both of them extends
 * {@link org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider}
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 * 
 * @see net.gvcc.goffice.multitenancy.annotation.EnableMultiTenancy
 */
@Configuration
@ComponentScan({ "net.gvcc.goffice.multitenancy*" })
@EnableTransactionManagement

@Import({ EmailServiceImpl.class, CryptManager.class })
public class MultiTenantJPAConfiguration {
	private static Logger LOGGER = LoggerFactory.getLogger(MultiTenantJPAConfiguration.class);

	private static final String DATABASE = "database";
	private static final String SCHEMA = "schema";

	public static final String DEFAULT_DB_IDENTIFIER_PROPERTY_NAME = "${multitenancy.database.owner:${GO2_COMPONENT_NAME:<N/D>}}";

	private static final String ERROR_TEMPLATE_1 = "[ERROR %d] Unable to initialize DBMS datasource: owner=%s, tenant=%s, keycloak=%s, name=%s, index=%d";
	private static final String ERROR_TEMPLATE_2 = "[ERROR %d] MAIN datasource config (= default datasource) not found: owner=%s, tenant=%s, keycloak=%s";
	private static final String ERROR_MISSING_DEFAULT_DB = "To identify the MAIN (default) datasource config, define the key 'main' (with value 'true') in the CMDB database configuration!";

	@Value("${spring.jpa.database:mysql}")
	private String databaseType;

	@Value("${multitenancy.type:schema}")
	private String tenantType;

	@Value(DEFAULT_DB_IDENTIFIER_PROPERTY_NAME)
	private String databaseOwner;

	@Value("${entity.packages:net.gvcc.goffice.multitenancy.config}")
	private String packagesToScan;

	@Value("${multitenancy.troubleshooting.subject:[MULTITENANCY-PERSISTENCE] DBMS initialization ERRORS}")
	private String mailSubject;
	@Value("${multitenancy.troubleshooting.body:*** DBMS initialization ERRORS ***}")
	private String mailBody;

	@Autowired
	private TroubleshootingService troubleshootingService;

	@Autowired
	private TenantConfigurator tenantConfigurator;

	@Autowired
	private CryptManager cryptManager;

	/**
	 * @return MultiTenantConnectionProvider A specialized Connection provider contract used when the application is using multi-tenancy support requiring tenant aware connections.
	 */
	@Bean
	@ConditionalOnProperty(value = "multitenancy.type", havingValue = DATABASE)
	public MultiTenantConnectionProvider multiTenantConnectionProvider() {
		MultiTenantConnectionProvider provider = new DataSourceMultiTenantConnectionProvider(repositoryDataSources());

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// the following source code has to be hardly tested!!! But, perhaps, it could be useful to reloading DBMS datasources //
		// see: DataSourceMultiTenantConnectionProvider.setDataSources() ////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// tenantConfigurator.addListener(new ITenantConfiguratorListener() {
		//
		// @Override
		// public void onReloading(TenantMap tenantMap) {
		// }
		//
		// @Override
		// public void onReloaded(TenantMap tenantMap) {
		// ((DataSourceMultiTenantConnectionProvider) provider).setDataSources(repositoryDataSources());
		// }
		//
		// @Override
		// public void onReloadingError() {
		// }
		// });

		return provider;
	}

	/**
	 * @return Map all the datasources and each of them is associated to the tenant used as key
	 */
	private Map<String, DataSourceMultiTenantMap> repositoryDataSources() {
		LOGGER.debug("repositoryDataSources - START");

		// try {
		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + cryptManager.encryptJasypt("test"));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		int[] globalLevelCounter_configured = new int[] { 0 };
		int[] globalLevelCounter_initialized = new int[] { 0 };
		final List<String> errorMessages = new ArrayList<>();

		Map<String, DataSourceMultiTenantMap> globalLevel_dataSourceMap = new HashMap<>(); // map of datasources at COMPONENT LEVEL

		// load DMBS configurations parsing the CMDB data
		tenantConfigurator.getTenantsIds().stream() //
				.forEach(tenantId -> {
					TenantInfo tenantInfo = tenantConfigurator.getInfo(tenantId).get();
					String keycloakId = tenantInfo.getKeycloak();
					Optional<DbmsTenantCatalog> additionals = tenantConfigurator.getAdditionalInfoFromKey(keycloakId, DbmsTenantCatalog.class);
					if (additionals.isPresent()) {
						DbmsTenantCatalog dbmsCatalog = additionals.get();
						DataSourceMultiTenantMap tenantLevel_dataSourceMap = new DataSourceMultiTenantMap();
						int tenantLevelCounter_configured = loadDMBSConfig4SingleTenant(tenantId, keycloakId, databaseOwner, tenantLevel_dataSourceMap, dbmsCatalog, errorMessages);
						int tenantLevelCounter_initialized = tenantLevel_dataSourceMap.size();
						globalLevel_dataSourceMap.put(tenantId, tenantLevel_dataSourceMap);
						globalLevelCounter_configured[0] += tenantLevelCounter_configured;
						globalLevelCounter_initialized[0] += tenantLevelCounter_initialized;
						LOGGER.info("repositoryDataSources - [tenant:{}, keycloak:{}], tenant(configured/initialized)={}/{}, global(configured/initialized)={}/{}", //
								tenantId, keycloakId, //
								tenantLevelCounter_configured, tenantLevelCounter_initialized, //
								globalLevelCounter_configured[0], globalLevelCounter_initialized[0]);
						LOGGER.debug("repositoryDataSources - [tenant:{}, keycloak:{}], tenant map={}", tenantId, keycloakId, tenantLevel_dataSourceMap);
						LOGGER.debug("repositoryDataSources - [tenant:{}, keycloak:{}], global map={}", tenantId, keycloakId, globalLevel_dataSourceMap);
					}
				});

		LOGGER.debug("repositoryDataSources - dataSourceMap={}", globalLevel_dataSourceMap);
		// errorMessages.clear();

		if (!errorMessages.isEmpty()) {
			String msg = "There were errors during datasource initialization!";
			if (globalLevelCounter_configured[0] != globalLevelCounter_initialized[0]) {
				msg += String.format("\nThe number of database instances, loaded at runtime, differs from the configured count: configured=%d -> loaded=%d", //
						globalLevelCounter_configured[0], globalLevelCounter_initialized[0]);
			}
			LOGGER.error("repositoryDataSources - ********** {} **********\n\n{}\n", msg, StringUtils.join(errorMessages, "\n"));

			String body = mailBody.concat("\n\n") //
					.concat("ERROR COUNT: ").concat(String.valueOf(errorMessages.size())).concat("\n\n") //
					.concat(msg).concat("\n\n") //
					.concat(StringUtils.join(errorMessages, "\n\n\n"));
			boolean error = troubleshootingService.sendMessageWithBody(mailSubject, body.toString(), null);
			if (error) {
				LOGGER.error("repositoryDataSources - ********** Unable to notify problems to troubleshooting!! **********");
			}
		}

		LOGGER.debug("repositoryDataSources - END");

		return globalLevel_dataSourceMap;
	}

	private int loadDMBSConfig4SingleTenant(String tenantNameOnlyForLogging, String keycloakIdOnlyForLogging, String ownerFilter, DataSourceMultiTenantMap tenantLevelDataSourceMap,
			DbmsTenantCatalog dbmsCatalog, List<String> errorMessages) {
		LOGGER.debug("loadDMBSConfig4SingleTenant - START");

		final String logPrefix = "loadDMBSConfig4SingleTenant - [owner:{}, tenant:{}, keycloak:{}] ";

		final String msg1 = logPrefix.concat("searching for DBMS configurations in CMDB....");
		LOGGER.info(msg1, ownerFilter, tenantNameOnlyForLogging, keycloakIdOnlyForLogging);

		// the data are filtered to retrieve only the configurations of the current owner (= component)
		List<DbmsTenantInfo> list = dbmsCatalog.stream() //
				.filter(dbmsInfo -> {
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////
					// PAY ATTENTION!! //////////////////////////////////////////////////////////////////////////////////////////
					// if field "owner" in tenant.json is empty, that means the dbms config is available FOR ALL components!!! //
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////
					String configOwner = StringUtils.defaultIfEmpty(dbmsInfo.getOwner(), ownerFilter);
					return configOwner.equals(ownerFilter);
				}) //
				.collect(Collectors.toList());

		int foundConfigCount = list.size();

		LOGGER.info(msg1.concat("found {} DBMS configuration(s)!"), ownerFilter, tenantNameOnlyForLogging, keycloakIdOnlyForLogging, foundConfigCount);

		String[] mainConfig = new String[] { null };

		// loop over the only owned configs
		IntStream.range(0, foundConfigCount) //
				.forEach(index -> {
					boolean error = true;

					DbmsTenantInfo dbmsInfo = list.get(index);
					String dbmsName = StringUtils.defaultIfBlank(dbmsInfo.getName(), ownerFilter);
					boolean isMain = foundConfigCount == 1 ? true : dbmsInfo.isMain(); // the default DB for the current tenant

					final String msg2 = logPrefix.concat("loading DBMS configuration: name='{}', index={}...");

					LOGGER.info(msg2, ownerFilter, tenantNameOnlyForLogging, keycloakIdOnlyForLogging, dbmsName, index);

					try {
						if (isMain) {
							if (mainConfig[0] != null) {
								final String msg = String.format("Duplicated MAIN db configs: '%s' and '%s'. Please, fix CMDB config!", mainConfig[0], dbmsName);
								throw new Exception(msg);
							}

							mainConfig[0] = dbmsName; // only for check

							LOGGER.info(logPrefix.concat("THIS IS THE DEFAULT DATASOURCE"), ownerFilter, tenantNameOnlyForLogging, keycloakIdOnlyForLogging);
						}

						if (tenantLevelDataSourceMap.containsKey(dbmsName)) {
							throw new RuntimeException("Duplicate database name '".concat(dbmsName).concat("'") //
									.concat(", tenant: ").concat(tenantNameOnlyForLogging) //
									.concat(", keycloak: ").concat(keycloakIdOnlyForLogging));
						}

						DataSource dataSource = DataSourceBuilder.create() //
								.url(dbmsInfo.getUrl()) //
								.driverClassName(dbmsInfo.getDriverClassName()) //
								.username(dbmsInfo.getUsername()) //
								.password(decodePassword(dbmsInfo.getPassword())) //
								.build();

						tenantLevelDataSourceMap.put(dbmsName, dataSource, isMain); // stored as key using the name of the configuration

						error = false;
					} catch (Exception e) {
						String msg = String.format(ERROR_TEMPLATE_1, errorMessages.size() + 1, ownerFilter, tenantNameOnlyForLogging, keycloakIdOnlyForLogging, dbmsName, index);
						LOGGER.error("loadDMBSConfig4SingleTenant - ********** ".concat(msg).concat(" **********"), e);
						errorMessages.add(makeErrorMessage(msg, ExceptionUtils.getStackTrace(e)));
					} finally {
						LOGGER.info(msg2.concat(error ? "ERROR" : "DONE"), ownerFilter, tenantNameOnlyForLogging, dbmsName, index);
					}
				});

		// checks if the default database is setted up for each "tenant-owner" value pair
		if (foundConfigCount != 0 && mainConfig[0] == null) {
			String msg = String.format(ERROR_TEMPLATE_2, errorMessages.size() + 1, ownerFilter, tenantNameOnlyForLogging, keycloakIdOnlyForLogging);
			LOGGER.error("loadDMBSConfig4SingleTenant - ********** ".concat(msg).concat(" **********"));
			errorMessages.add(makeErrorMessage(msg, ERROR_MISSING_DEFAULT_DB));
		}

		LOGGER.debug("loadDMBSConfig4SingleTenant - END");

		return foundConfigCount;
	}

	private String decodePassword(String password) {
		if (StringUtils.isNotBlank(password)) {
			try {
				password = cryptManager.decryptJasypt(password);
			} catch (RuntimeException e) {
				LOGGER.warn("Unable to decrypt password!! The plain/text value will be used!", e);
			}
		}

		return password;
	}

	private static String makeErrorMessage(String msg, String details) {
		String separator = StringUtils.repeat("=", msg.length());
		msg = separator //
				.concat("\n") //
				.concat(msg) //
				.concat("\n") //
				.concat(separator) //
				.concat("\n") //
				.concat(details);

		return msg;
	}

	/**
	 * @return MultiTenantConnectionProvider A specialized Connection provider contract used when the application is using multi-tenancy support requiring tenant aware connections.
	 */
	@Bean
	@ConditionalOnProperty(value = "multitenancy.type", matchIfMissing = true, havingValue = SCHEMA)
	public MultiTenantConnectionProvider schemaMultitenantProvider() {
		LOGGER.debug("schemaMultitenantProvider - START");

		SchemaBasedMultitenantConnectionProvider schemaMultitenantProvider = new SchemaBasedMultitenantConnectionProvider();
		schemaMultitenantProvider.setDatabaseName(databaseType);

		LOGGER.debug("schemaMultitenantProvider - END");

		return schemaMultitenantProvider;
	}

	/**
	 * @return JpaVendorAdapter Serves as single configuration point for all vendor-specific properties.
	 */
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	/**
	 * @param multiTenantConnectionProvider
	 *            A specialized Connection provider contract used when the application is using multi-tenancy support requiring tenant aware connections.
	 * @param dataSource
	 *            A factory for connections to the physical data source that this DataSource object represents
	 * @param currentTenantIdentifierResolver
	 *            A callback registered with the {@link org.hibernate.SessionFactory} that is responsible for resolving the current tenant identifier
	 * @return EntityManagerFactory factory used to interact with the entity manager factory for the persistence unit.
	 */
	public @Bean EntityManagerFactory entityManagerFactory(MultiTenantConnectionProvider multiTenantConnectionProvider, DataSource dataSource,
			CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
		LOGGER.debug("entityManagerFactory - START");

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(jpaVendorAdapter());
		factory.setPackagesToScan(packagesToScan, RevInfo.class.getPackage().getName());
		if (!DATABASE.equalsIgnoreCase(tenantType)) {
			factory.setDataSource(dataSource);
		}
		factory.getJpaPropertyMap().put(Environment.MULTI_TENANT, getMultiTenancyStrategy(tenantType));
		factory.getJpaPropertyMap().put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
		factory.getJpaPropertyMap().put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		factory.afterPropertiesSet();

		EntityManagerFactory em = factory.getObject();

		LOGGER.debug("entityManagerFactory - END");

		return em;
	}

	/**
	 * @param type
	 *            represent the type of multitenancy it can be {@link org.hibernate.MultiTenancyStrategy#SCHEMA} or {@link org.hibernate.MultiTenancyStrategy#DATABASE}
	 * @return MultiTenancyStrategy Describes the methods for multi-tenancy understood by Hibernate.
	 */
	private MultiTenancyStrategy getMultiTenancyStrategy(String type) {
		LOGGER.debug("getMultiTenancyStrategy - START");

		MultiTenancyStrategy strategy = type.equalsIgnoreCase(MultiTenancyStrategy.SCHEMA.toString()) ? MultiTenancyStrategy.SCHEMA : MultiTenancyStrategy.DATABASE;

		LOGGER.debug("getMultiTenancyStrategy - strategy={}", strategy);
		LOGGER.debug("getMultiTenancyStrategy - END");

		return strategy;
	}
}
