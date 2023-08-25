package net.gvcc.goffice.multitenancy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.gvcc.goffice.multitenancy.entity.User;

@SpringBootTest(classes = SampleMultienancyApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableTransactionManagement
@TestInstance(Lifecycle.PER_CLASS)
public class RestServiceTest {

	@Value("${goffice.common.security.urltoken}")
	private String urltoken;
	@Value("${goffice.common.security.username}")
	private String usernameDS;
	@Value("${goffice.common.security.password}")
	private String password;
	@Value("${goffice.common.security.clientid}")
	private String clientid;
	@Value("${goffice.common.security.clientsecret}")
	private String clientsecret;
	@Value("${goffice.common.security.granttype}")
	private String granttype;

	@Value("${goffice.common.security.realm}")
	private String realm;
	@Value("${goffice.common.security.server.keycloak}")
	private String serverkeycloack;

	private static final String AUTHORIZATION = "Authorization";
	private static final String BEARER_TOKEN = "Bearer ";
	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Value("${multitenant.datasources.tenanta.url}")
	private String dbUrl;
	@Autowired
	ThreadLocalTenantStorage threadLocalTenantStorage;

	@BeforeAll
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

//	@Test
	public void testMultitenancyFlow() throws Exception {
		User user = new User();
		user.setName("User1");
		user.setUuid("1L");
		user.setId(1L);

		ResponseEntity<String> responseCreateUser = createUser(user, "tenanta");
		assertEquals(HttpStatus.CREATED, responseCreateUser.getStatusCode());
		ResponseEntity<User> responseUser = getUserById(user.getId(), "tenanta");
		assertEquals("User1", responseUser.getBody().getName());
		assertEquals("admin", responseUser.getBody().getCreatedBy());
		assertEquals("admin", responseUser.getBody().getModifiedBy());

		// Same UUID same Tenant Fails
		user.setName("User2");
		user.setId(2L);
		responseCreateUser = createUser(user, "tenanta");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseCreateUser.getStatusCode());

		// Same UUID different Tenant Success
		responseCreateUser = createUser(user, "tenantb");
		assertEquals(HttpStatus.CREATED, responseCreateUser.getStatusCode());
		responseUser = getUserById(1L, "tenanta");
		assertEquals("User1", responseUser.getBody().getName());
		responseUser = getUserById(2L, "tenantb");
		assertEquals("User2", responseUser.getBody().getName());
	}

	private ResponseEntity<String> createUser(User user, String tenant) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("tenant", tenant);
		// headers.set(AUTHORIZATION, BEARER_TOKEN + getToken());
		HttpEntity<User> requestCreateUser = new HttpEntity<>(user, headers);
		return restTemplate.withBasicAuth("admin", "admin").exchange("http://localhost:" + port + "/sample/users", HttpMethod.POST, requestCreateUser, String.class);
	}

	private ResponseEntity<User> getUserById(Long id, String tenant) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("tenant", tenant);
		// headers.set(AUTHORIZATION, BEARER_TOKEN + getToken());
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		return restTemplate.withBasicAuth("admin", "admin").exchange("http://localhost:" + port + "/sample/users/" + id, HttpMethod.GET, requestEntity, User.class);
	}

	public String getToken() {
		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(serverkeycloack).grantType(OAuth2Constants.CLIENT_CREDENTIALS).realm(realm).clientId(clientid).clientSecret(clientsecret)
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
		return keycloak.tokenManager().getAccessToken().getToken();
	}
}
