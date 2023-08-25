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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

/**
 *
 * <p>
 * The <code>AbstractService</code> class
 * </p>
 * <p>
 * This abstract class should be used as parent for every persistence service. One Persistence service per entity is needed It encapsulates all the common CRUD logic performing also the hash
 * calculation
 * </p>
 * <p>
 * This class will also enable Auditing using the {@link org.hibernate.envers.Audited} annotation
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @param <T>
 *            The entity type that has always to extends {@link BaseEntity}
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
@Transactional
public abstract class AbstractService<T extends BaseEntity> implements IOperations<T> {
	private static Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

	/**
	 * Retrieve an entity by its ID
	 * 
	 * @param id
	 *            ID of the entity we are searching for
	 * @param user
	 *            The username of the user who is performing the search
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<T> findOne(final Long id, String user) {
		return getDao().findById(id);
	}

	/**
	 * Retrieve an entity by its UUID
	 * 
	 * @param uuid
	 *            UUID of the entity we are searching for
	 * @param user
	 *            The username of the user who is performing the search
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 * @see Optional findByUuid(String uuid, String user)
	 */
	@Deprecated
	@Override
	@Transactional(readOnly = true)
	public Optional<T> findByUiid(final String uuid, String user) {
		return getDao().findByUuid(uuid);
	}

	/**
	 * Retrieve an entity by its UUID
	 * 
	 * @param uuid
	 *            UUID of the entity we are searching for
	 * @param user
	 *            The username of the user who is performing the search
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<T> findByUuid(final String uuid, String user) {
		return getDao().findByUuid(uuid);
	}

	/**
	 * Retrieve all the entities
	 * 
	 * @param user
	 *            The username of the user who is performing the search
	 * @return List The resulting list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<T> findAll(String user) {
		return Lists.newArrayList(getDao().findAll());
	}

	/**
	 * Retrieve entities with pagination
	 * 
	 * @param page
	 *            the number of the page we are retrieving
	 * @param size
	 *            the size of the page
	 * @param user
	 *            The username of the user who is performing the search
	 * @return Page The resulting page
	 */
	@Override
	public Page<T> findPaginated(final int page, final int size, String user) {
		return getDao().findAll(PageRequest.of(page, size));
	}

	@Override
	public Page<T> findPaginated(final int page, final int size, String sortColumn, String direction, String user) {
		Direction dir = Direction.ASC;
		if ("DESC".equalsIgnoreCase(direction)) {
			dir = Direction.DESC;
		}
		return getDao().findAll(PageRequest.of(page, size, Sort.by(dir, sortColumn)));
	}

	/**
	 * Save an entity to the database
	 * 
	 * @param entity
	 *            the entity tu be saved
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return T the just created Entity
	 */

	@Override
	public T create(final T entity, String user) {
		LOGGER.debug("create - START");

		fillRequiredFieldsAtCreation(entity, user);

		T generatedEntity = null;

		try {
			generatedEntity = getDao().save(entity);
		} catch (Exception e) {
			LOGGER.error("create", e);
			throw new PersistenceException(e);
		}

		LOGGER.debug("create - END");

		return generatedEntity;
	}

	/**
	 * 
	 * @param entity
	 * @param user
	 */
	protected void fillRequiredFieldsAtCreation(BaseEntity entity, String user) {
		LOGGER.debug("fillRequiredFieldsAtCreation - START");

		LocalDateTime now = LocalDateTime.now();
		entity.setDateCreated(now);
		entity.setDateModified(now);
		entity.setCreatedBy(user);
		entity.setModifiedBy(user);
		entity.setUuid(UUID.randomUUID().toString());

		try {
			String hash = getHashingManager().hash(entity.hashCode());
			entity.setHash(hash);
		} catch (Exception e) {
			LOGGER.error("fillRequiredFieldsAtCreation", e);
			throw new PersistenceException(e);
		}

		LOGGER.debug("fillRequiredFieldsAtCreation - END");
	}

	/**
	 * Update an entity already present into the database
	 * 
	 * @param entity
	 *            the entity tu be saved
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return T the just updated Entity
	 */
	@Override
	public T update(final T entity, String user) {
		LOGGER.debug("update - START");

		fillRequiredFieldsAtUpdation(entity, user);

		T generatedEntity = null;

		try {
			generatedEntity = getDao().save(entity);
		} catch (Exception e) {
			LOGGER.error("update", e);
			throw new PersistenceException(e);
		}

		LOGGER.debug("update - END");

		return generatedEntity;
	}

	/**
	 * 
	 * @param entity
	 * @param user
	 */
	protected void fillRequiredFieldsAtUpdation(BaseEntity entity, String user) {
		LOGGER.debug("fillRequiredFieldsAtUpdation - START");

		LocalDateTime now = LocalDateTime.now();
		entity.setDateModified(now);
		entity.setModifiedBy(user);

		try {
			String hash = getHashingManager().hash(entity.hashCode());
			entity.setHash(hash);
		} catch (Exception e) {
			LOGGER.error("fillRequiredFieldsAtUpdation", e);
			throw new PersistenceException(e);
		}

		LOGGER.debug("fillRequiredFieldsAtUpdation - END");
	}

	/**
	 * Delete an entity already present into the database
	 * 
	 * @param entity
	 *            the entity to be deleted
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return Optional the just deleted entity
	 */
	@Override
	public Optional<T> delete(final T entity, String user) {
		getDao().delete(entity);
		return Optional.ofNullable(entity);
	}

	/**
	 * Delete an entity already present into the database
	 * 
	 * @param entityId
	 *            the ID of the entity to be deleted
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return Optional the just deleted entity
	 */
	@Override
	public Optional<T> deleteById(final Long entityId, String user) {
		Optional<T> obj = getDao().findById(entityId);
		if (obj.isPresent()) {
			return delete(obj.get(), user);
		}
		return Optional.empty();
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