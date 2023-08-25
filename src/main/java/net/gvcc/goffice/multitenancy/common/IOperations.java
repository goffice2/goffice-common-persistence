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

/**
 *
 * <p>
 * The <code>IOperations</code> interface
 * </p>
 * <p>
 * This interface should be implemented by every persistence service.
 * <p>
 * {@link net.gvcc.goffice.multitenancy.common.AbstractService} implements this interface It defines all the common CRUD operations needed
 * <p>
 * Data: 29 apr 2022
 *
 * @author <a href="mailto:edv@gvcc.net"></a>
 * 
 * @version 1.0
 * 
 * @param <T>
 *            The class that we need to perform CRUD operations with
 */
public interface IOperations<T> {

	/**
	 * Retrieve an entity by its ID
	 * 
	 * @param id
	 *            ID of the entity we are searching for
	 * @param user
	 *            The username of the user who is performing the search
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 */
	Optional<T> findOne(final Long id, String user);

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
	Optional<T> findByUiid(final String uuid, String user);

	/**
	 * Retrieve an entity by its UUID
	 * 
	 * @param uuid
	 *            UUID of the entity we are searching for
	 * @param user
	 *            The username of the user who is performing the search
	 * @return Optional The resulting entity or <code>Optional.empty</code> if not found
	 */

	Optional<T> findByUuid(final String uuid, String user);

	/**
	 * Retrieve all the entities
	 * 
	 * @param user
	 *            The username of the user who is performing the search
	 * @return List The resulting list of entities
	 */
	List<T> findAll(String user);

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
	Page<T> findPaginated(int page, int size, String user);

	/**
	 * returns a subset of entities, paginated following the directions given as parameters
	 * 
	 * @param page
	 *            the number of the page we are retrieving
	 * @param size
	 *            the size of the set
	 * @param sortColumn
	 *            the sorting column
	 * @param direction
	 *            the direction sorting
	 * @param user
	 *            The username of the user who is performing the search
	 * @return entities paginated
	 */
	Page<T> findPaginated(int page, int size, String sortColumn, String direction, String user);

	/**
	 * 
	 * @param entity
	 *            the entity tu be saved
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return T the just created Entity
	 * 
	 */
	T create(final T entity, String user);

	/**
	 * Update an entity already present into the database
	 * 
	 * @param entity
	 *            the entity tu be saved
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return T the just updated Entity
	 */
	T update(final T entity, String user);

	/**
	 * Delete an entity already present into the database
	 * 
	 * @param entity
	 *            the entity to be deleted
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return Optional the just deleted entity
	 */
	Optional<T> delete(final T entity, String user);

	/**
	 * Delete an entity already present into the database
	 * 
	 * @param entityId
	 *            the ID of the entity to be deleted
	 * @param user
	 *            The username of the user who is performing the operation
	 * @return Optional the just deleted entity
	 */
	Optional<T> deleteById(final Long entityId, String user);

}