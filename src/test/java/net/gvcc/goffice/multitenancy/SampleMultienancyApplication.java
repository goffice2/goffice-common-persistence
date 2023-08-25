package net.gvcc.goffice.multitenancy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
//		SecurityAutoConfiguration.class }, scanBasePackages = { "net.gvcc.goffice.multitenancy", "net.gvcc.goffice.multitenancy.repository", "net.gvcc.goffice.multitenancy.entity"
//
//})
@SpringBootApplication
@PropertySource(value = "classpath:security.properties")
// @Import({ net.gvcc.goffice.multitenancy.repository.UserRepository.class })
public class SampleMultienancyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleMultienancyApplication.class, args);

	}

}
//