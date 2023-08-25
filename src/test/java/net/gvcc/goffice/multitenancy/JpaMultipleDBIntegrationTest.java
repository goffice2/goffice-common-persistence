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
package net.gvcc.goffice.multitenancy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.gvcc.goffice.multitenancy.entity.User;
import net.gvcc.goffice.multitenancy.repository.UserRepository;

/**
 *
 * <p>
 * The <code>JpaMultipleDBIntegrationTest</code> class
 * </p>
 * <p>
 * Data: 4 mag 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableTransactionManagement
@TestInstance(Lifecycle.PER_CLASS)
public class JpaMultipleDBIntegrationTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ConnectionManager connectionManager;
	@Value("${multitenant.datasources.tenanta.url}")
	private String dbUrl;
	@Autowired
	ThreadLocalTenantStorage threadLocalTenantStorage;

	// @Test
	public void entityManagerIsNotNull() {
		assertNotNull(entityManagerFactory);
	}

	// @BeforeAll
	public void createTableMultiTenant() {
		threadLocalTenantStorage.setTenantName("tenanta");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(Utils.CREATE_TABLE).executeUpdate();
		em.getTransaction().commit();
		threadLocalTenantStorage.setTenantName("tenantb");
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(Utils.CREATE_TABLE).executeUpdate();
		em.getTransaction().commit();
		assertNotNull(entityManagerFactory);
		threadLocalTenantStorage.setTenantName("tenanta");
	}

	// @Test
	// public void customDBRepository() {
	// EntityManagerFactory emFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(connectionManager.archiverPersistenceUnitInfo(),
	// ImmutableMap.builder().put(JPA_JDBC_DRIVER, "org.h2.Driver").put(JPA_JDBC_URL, dbUrl).put(DIALECT, H2Dialect.class).put(SHOW_SQL, false).put(QUERY_STARTUP_CHECKING, false)
	// .put(GENERATE_STATISTICS, false).put(USE_REFLECTION_OPTIMIZER, false).put(USE_SECOND_LEVEL_CACHE, false).put(USE_QUERY_CACHE, false).put(USE_STRUCTURED_CACHE, false)
	// .put(STATEMENT_BATCH_SIZE, 20).put(JPA_JDBC_USER, "sa").put(JPA_JDBC_PASSWORD, "").build());
	//
	// EntityManager entityManager = emFactory.createEntityManager();
	// RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
	// UserRepository repository = factory.getRepository(UserRepository.class);
	// entityManager.getTransaction().begin();
	// User user = new User();
	// user.setName("John");
	// user.setId(1L);
	// user = repository.save(Utils.createUser(user));
	// entityManager.getTransaction().commit();
	// }

	// @Test
	public void whenCreatingUser_thenCreated() {
		User user = new User();
		user.setName("JohnTest");
		user.setUuid("1L");
		User stored = userRepository.save(Utils.createUser(user));
		user.setId(1L);
		final Optional<User> result = userRepository.findById(stored.getId());
		assertTrue(result.isPresent());
	}

	// @Test
	// @Transactional("multiTenantTxManager")
	// public void whenCreatingUsersWithSameUUID_thenRollback() {
	// User user1 = new User();
	// user1.setName("John");
	// user1.setId(1L);
	// user1.setUuid("1L");
	//
	// user1 = userRepository.save(Utils.createUser(user1));
	// Optional<User> user1Opt = userRepository.findById(user1.getId());
	// assertTrue(user1Opt.isPresent());
	// assertEquals("John", user1Opt.get().getName());
	// assertEquals("1L", user1Opt.get().getUuid());
	//
	// User user2 = new User();
	// user2.setName("Smith");
	// user2.setId(2L);
	// user2.setUuid("1L");
	// try {
	// user2 = userRepository.save(Utils.createUser(user2));
	// Optional<User> user2Opt = userRepository.findById(user2.getId());
	// assertTrue(user2Opt.isPresent());
	// assertEquals("Smith", user2Opt.get().getName());
	// assertEquals("1L", user2Opt.get().getUuid());
	// userRepository.flush();
	// fail("DataIntegrityViolationException should be thrown!");
	// } catch (final DataIntegrityViolationException e) {
	// // Expected
	// } catch (final Exception e) {
	// fail("DataIntegrityViolationException should be thrown, instead got: " + e);
	// }
	// }
	//
	// @Test
	// @Transactional("multiTenantTxManager")
	// public void whenCreatingUsersWithSameUUIDButDifferentTenant() {
	// threadLocalTenantStorage.setTenantName("tenanta");
	// User user1 = new User();
	// user1.setName("John");
	// user1.setId(1L);
	// user1.setUuid("1L");
	//
	// user1 = userRepository.save(Utils.createUser(user1));
	// userRepository.flush();
	// Optional<User> user1Opt = userRepository.findById(user1.getId());
	// assertTrue(user1Opt.isPresent());
	// assertEquals("John", user1Opt.get().getName());
	// assertEquals("1L", user1Opt.get().getUuid());
	// threadLocalTenantStorage.setTenantName("tenantb");
	// User user2 = new User();
	// user2.setName("Smith");
	// user2.setId(2L);
	// user2.setUuid("1L");
	// try {
	// user2 = userRepository.save(Utils.createUser(user2));
	// Optional<User> user2Opt = userRepository.findById(user2.getId());
	// assertTrue(user2Opt.isPresent());
	// assertEquals("Smith", user2Opt.get().getName());
	// assertEquals("1L", user2Opt.get().getUuid());
	// userRepository.flush();
	// fail("DataIntegrityViolationException should be thrown!");
	// } catch (final DataIntegrityViolationException e) {
	// // Expected
	// } catch (final Exception e) {
	// fail("DataIntegrityViolationException should be thrown, instead got: " + e);
	// } finally {
	// }
	//
	// }
	// @Autowired
	// private I18NManager i18n;
	// @Test
	// public void i18NManager() {
	// assertNotNull(i18n.getLanguages());
	// assertEquals(2,i18n.getLanguages().length);
	// assertEquals("LANG_IT",i18n.getLanguages()[0]);
	// assertEquals("LANG_DE",i18n.getLanguages()[1]);
	// }

}