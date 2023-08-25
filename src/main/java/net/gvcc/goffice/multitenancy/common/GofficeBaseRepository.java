package net.gvcc.goffice.multitenancy.common;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


/**
 * <p>
 * The <code>GofficeBaseRepository</code> class
 * </p>
 * <p>
 * This class represents the base class from which all of our JPA Repositories should inherit
 * It will restrict the usage of {@link BaseEntity} as entity class and <code>Long</code> as type for the ID
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 * 
 * @author marco.mancuso
 *
 * @param <T> The entity type that has always to extend {@link BaseEntity}
 */
@NoRepositoryBean
public interface GofficeBaseRepository<T extends BaseEntity> extends JpaRepository<T, Long>{
	/**
	 * @param uiid user defined unique identifier of the entity
	 * @return an Optional containing the entity if an entity with that uuid exists or {@link java.util.Optional#empty()}
	 */
	Optional<T> findByUuid(String uiid);
}
