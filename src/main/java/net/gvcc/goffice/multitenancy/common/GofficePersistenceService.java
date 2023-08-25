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
package net.gvcc.goffice.multitenancy.common;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * <p>
 * The <code>GofficePersistenceService</code> class
 * </p>
 * <p>
 * This is the only concrete Persistence Service class and it extends {@link AbstractService}
 * </p>
 * <p>
 * It delegates all the CRUD operations to the underlying <code>AbstractService</code> while retrieving the loggedIn user information through {@link GofficePersistenceService#getLoggedInUser()}
 * </p>
 * *
 * <p>
 * This class will also enable Auditing using the {@link org.hibernate.envers.Audited} annotation
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 *
 * @version 1.0
 * 
 * @param <T>
 *            The entity type that has always to extends {@link BaseEntity}
 */
@Transactional
public abstract class GofficePersistenceService<T extends BaseEntity> extends AbstractService<T> {

	/**
	 * @return the username of the currently logged in user or "anonymous" otherwise
	 */
	protected String getLoggedInUser() {
		Optional<Authentication> auth = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		return auth.map(a -> a.getName()).orElse("anonymous");
	}

	/**
	 * Retrieve an entity by its ID
	 * 
	 * @param id
	 *            ID of the entity we are searching for
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 */
	@Transactional(readOnly = true)
	public Optional<T> findOne(final Long id) {
		return super.findOne(id, getLoggedInUser());
	}

	/**
	 * Retrieve all the entities
	 * 
	 * @return List The resulting list of entities
	 */
	@Transactional(readOnly = true)
	public List<T> findAll() {
		return super.findAll(getLoggedInUser());
	}

	/**
	 * Retrieve an entity by its UUID
	 * 
	 * @param uuid
	 *            UUID of the entity we are searching for
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 * @see Optional findByUuid(String uuid, String user)
	 */
	@Deprecated
	@Transactional(readOnly = true)
	public Optional<T> findByUiid(final String uuid) {
		return super.findByUiid(uuid, getLoggedInUser());
	}

	/**
	 * Retrieve an entity by its UUID
	 * 
	 * @param uuid
	 *            UUID of the entity we are searching for
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 */
	@Transactional(readOnly = true)
	public Optional<T> findByUuid(final String uuid) {
		return super.findByUuid(uuid, getLoggedInUser());
	}

	/**
	 * Retrieve entities with pagination
	 * 
	 * @param page
	 *            the number of the page we are retrieving
	 * @param size
	 *            the size of the page
	 * @return Page The resulting page
	 */
	public Page<T> findPaginated(final int page, final int size) {
		return super.findPaginated(page, size, getLoggedInUser());
	}

	/**
	 *
	 * @param page
	 * @param size
	 * @param sortColumn
	 * @param direction
	 * @return
	 */
	public Page<T> findPaginated(final int page, final int size, String sortColumn, String direction) {
		return super.findPaginated(page, size, sortColumn, direction, getLoggedInUser());
	}

	/**
	 * Save an entity to the database
	 * 
	 * @param entity
	 *            the entity tu be saved
	 * @return T the just created Entity
	 */

	public T create(final T entity) {
		return super.create(entity, getLoggedInUser());
	}

	/**
	 * 
	 * @param entity
	 */
	protected void fillRequiredFieldsAtCreation(BaseEntity entity) {
		super.fillRequiredFieldsAtCreation(entity, getLoggedInUser());
	}

	/**
	 * Update an entity already present into the database
	 * 
	 * @param entity
	 *            the entity tu be saved
	 * @return T the just updated Entity
	 */
	public T update(final T entity) {
		return super.update(entity, getLoggedInUser());
	}

	/**
	 * 
	 * @param entity
	 */
	protected void fillRequiredFieldsAtUpdation(BaseEntity entity) {
		super.fillRequiredFieldsAtUpdation(entity, getLoggedInUser());
	}

	/**
	 * Delete an entity already present into the database
	 * 
	 * @param entity
	 *            the entity to be deleted
	 * @return Optional the just deleted entity
	 */
	public Optional<T> delete(final T entity) {
		return super.delete(entity, getLoggedInUser());
	}

	/**
	 * Delete an entity already present into the database
	 * 
	 * @param entityId
	 *            the ID of the entity to be deleted
	 * @return Optional the just deleted entity
	 */
	public Optional<T> deleteById(final Long entityId) {
		return super.deleteById(entityId, getLoggedInUser());
	}

	/**
	 * This method give acceess to the real underlying JPA repository and needs to be implemented by all subclasses
	 * 
	 * @return the concrete class implementing {@link net.gvcc.goffice.multitenancy.common.GofficeBaseRepository}
	 */
	protected abstract GofficeBaseRepository<T> getDao();

	/**
	 * This method give acceess to the real underlying HashingManager and needs to be implemented by all subclasses
	 * 
	 * @return the concrete class which extends {@link net.gvcc.goffice.multitenancy.common.HashingManager}
	 */
	protected abstract HashingManager getHashingManager();

}