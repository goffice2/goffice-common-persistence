package net.gvcc.goffice.multitenancy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import net.gvcc.goffice.multitenancy.annotation.EnableMultiTenancy;
import net.gvcc.goffice.multitenancy.config.dbms.DbmsTenantCatalog;
import net.gvcc.goffice.multitenancy.entity.User;
import net.gvcc.goffice.multitenancy.exception.MissingTenantException;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@EnableMultiTenancy
public class MultitenancyCMDBTest {
	private static Logger LOGGER = LoggerFactory.getLogger(MultitenancyCMDBTest.class);

	@Autowired
	private TenantConfigurator tenantConfigurator;

	@Autowired
	private UserService userService;

	@Autowired
	private ITenantStorage tenantStorage;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private GreenMail smtpServer;

	private static final String PRINCIPAL = "TestUser";

	@BeforeAll
	public void setUp() {
		// Start the mock SMTP server
		ServerSetup setup = new ServerSetup(8000, "localhost", "smtp");
		smtpServer = new GreenMail(setup);
		smtpServer.start();
		initSchema();
		initDb("bolzano");
		initDb("abtei");
	}

	@AfterAll
	public void tearDown() {
		// Stop the mock SMTP server
		smtpServer.stop();
	}

	private void initSchema() {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(Utils.CREATE_SCHEMA).executeUpdate();
		em.getTransaction().commit();
	}

	private void initDb(String tenant) {
		tenantStorage.setTenantName(tenant);
		EntityManager em = entityManagerFactory.createEntityManager();
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(Utils.CREATE_TABLE).executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	public void whenBolzano() {
		var dbSchema = tenantConfigurator.getSchemaFromKeycloakId("Bolzano");
		assertNotNull(dbSchema);
		assertTrue(dbSchema.isPresent());
		assertEquals("bolzano", dbSchema.get());
	}

	@Test
	public void whenAbtei() {
		var dbSchema = tenantConfigurator.getSchemaFromKeycloakId("Abtei");
		assertNotNull(dbSchema);
		assertTrue(dbSchema.isPresent());
		assertEquals("abtei", dbSchema.get());
	}

	@Test
	public void setTenantFromCodiceCatastale() throws MissingTenantException {
		var tenant = tenantConfigurator.setTenantFromCodiceCatastale("A952");
		assertNotNull(tenant);
		assertTrue(tenant.isPresent());
		assertEquals("bolzano", tenant.get());
		assertEquals("bolzano", tenantStorage.getTenantName());
	}

	@Test
	public void whenCreatingUser_thenCreated() {
		tenantStorage.setTenantName("Bolzano");
		User user = new User();
		user.setName("JohnTest");
		user.setUuid("1L");
		User stored = userService.create(Utils.createUser(user), PRINCIPAL);
		final Optional<User> result = userService.findOne(stored.getId(), PRINCIPAL);
		assertTrue(result.isPresent());

		tenantStorage.setTenantName("Abtei");
		List<User> abteiUsers = userService.findAll(PRINCIPAL);
		assertNotNull(abteiUsers);
		assertTrue(abteiUsers.isEmpty());
		stored = userService.create(Utils.createUser(user), PRINCIPAL);
		abteiUsers = userService.findAll(PRINCIPAL);
		assertNotNull(abteiUsers);
		assertFalse(abteiUsers.isEmpty());
	}

	@Test
	public void whenSearchByUuid_thenUserIsFound() {
		tenantStorage.setTenantName("Bolzano");
		User user = new User();
		user.setName("JohnTest");
		user.setUuid("Uuid");
		User userCreated = userService.create(user, PRINCIPAL);
		assertNotNull(userCreated);
		final Optional<User> result = userService.findByUiid(userCreated.getUuid(), PRINCIPAL);
		assertTrue(result.isPresent());
		assertNotNull(result.get());
		assertEquals(user.getName(), result.get().getName());
	}

	@Test
	public void loadDbmsConfigTest() {
		LOGGER.info("loadDbmsConfigTest - tenantConfigurator={}", tenantConfigurator.getInfo());

		var dbSchema = tenantConfigurator.getSchemaFromKeycloakId("Bolzano");
		assertNotNull(dbSchema);
		assertTrue(dbSchema.isPresent());
		assertEquals("bolzano", dbSchema.get());

		tenantStorage.setTenantName("Bolzano");

		var additionals = tenantConfigurator.getAdditionalInfo(DbmsTenantCatalog.class);
		assertNotNull(additionals);
		assertTrue(additionals.isPresent());

		var dbInfo = additionals.get().getByName("missing_config");
		assertNotNull(dbInfo);
		assertFalse(dbInfo.isPresent());

		final String name = "h2-test-1";
		dbInfo = additionals.get().getByName(name);
		assertNotNull(dbInfo);
		assertTrue(dbInfo.isPresent());

		assertEquals(dbInfo.get().getName(), name);
	}
}
