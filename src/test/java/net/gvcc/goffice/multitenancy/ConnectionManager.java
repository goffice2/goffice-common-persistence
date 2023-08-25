package net.gvcc.goffice.multitenancy;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_DRIVER;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_PASSWORD;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_URL;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_USER;
import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_REFLECTION_OPTIMIZER;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.hibernate.dialect.MySQL55Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.google.common.collect.ImmutableMap;

import net.gvcc.goffice.multitenancy.entity.User;
import net.gvcc.goffice.multitenancy.repository.UserRepository;

//@Component
public class ConnectionManager {
	public PersistenceUnitInfo archiverPersistenceUnitInfo() {
		return new PersistenceUnitInfo() {
			@Override
			public String getPersistenceUnitName() {
				return "ApplicationPersistenceUnit";
			}

			@Override
			public String getPersistenceProviderClassName() {
				return "org.hibernate.jpa.HibernatePersistenceProvider";
			}

			@Override
			public PersistenceUnitTransactionType getTransactionType() {
				return PersistenceUnitTransactionType.RESOURCE_LOCAL;
			}

			@Override
			public DataSource getJtaDataSource() {
				return null;
			}

			@Override
			public DataSource getNonJtaDataSource() {
				return null;
			}

			@Override
			public List<String> getMappingFileNames() {
				return Collections.emptyList();
			}

			@Override
			public List<URL> getJarFileUrls() {
				try {
					return Collections.list(this.getClass().getClassLoader().getResources(""));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}

			@Override
			public URL getPersistenceUnitRootUrl() {
				return null;
			}

			@Override
			public List<String> getManagedClassNames() {
				return Collections.emptyList();
			}

			@Override
			public boolean excludeUnlistedClasses() {
				return false;
			}

			@Override
			public SharedCacheMode getSharedCacheMode() {
				return null;
			}

			@Override
			public ValidationMode getValidationMode() {
				return null;
			}

			@Override
			public Properties getProperties() {
				return new Properties();
			}

			@Override
			public String getPersistenceXMLSchemaVersion() {
				return null;
			}

			@Override
			public ClassLoader getClassLoader() {
				return null;
			}

			@Override
			public void addTransformer(ClassTransformer transformer) {

			}

			@Override
			public ClassLoader getNewTempClassLoader() {
				return null;
			}

		};
	}

	@Transactional
	public void testDbOnTheFly() {
		EntityManagerFactory emFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(archiverPersistenceUnitInfo(),
				ImmutableMap.builder().put(JPA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver").put(JPA_JDBC_URL, "jdbc:mysql://localhost:3306/flydb").put(DIALECT, MySQL55Dialect.class).put(SHOW_SQL, true)
						.put(QUERY_STARTUP_CHECKING, false).put(GENERATE_STATISTICS, false).put(USE_REFLECTION_OPTIMIZER, false).put(USE_SECOND_LEVEL_CACHE, false).put(USE_QUERY_CACHE, false)
						.put(USE_STRUCTURED_CACHE, false).put(STATEMENT_BATCH_SIZE, 20).put(JPA_JDBC_USER, "root").put(JPA_JDBC_PASSWORD, "admin").build());

		EntityManager entityManager = emFactory.createEntityManager();
		RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
		UserRepository repository = factory.getRepository(UserRepository.class);
		entityManager.getTransaction().begin();
		User user = new User();
		user.setName("Johnn");
		user.setId(2L);
		user = repository.save(user);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
