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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * <p>
 * The <code>CRUDController</code> class
 * </p>
 * <p>
 * By extending this abstract class in our microservices a CRUD REST controller
 * with basic CRUD operation is created
 * The REST endpoint will expose the entities directly and it should be used carefully
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 * @param <T> The entity type that has always to extend {@link BaseEntity}
 */
public abstract class CRUDController<T extends BaseEntity> {

	/**
	 * 
	 * @return a concrete instance of {@link net.gvcc.goffice.multitenancy.common.IOperations}
	 */
	public abstract IOperations<T> getService();

	/**
	 * @param id ID of the entity to retrieve
	 * @return ResponseEntity containing the retrieved entity as body
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<T> getById(@PathVariable(value = "id")
	Long id) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<T> result = getService().findOne(id, name);
		if (result.isPresent()) {
			return new ResponseEntity<>(result.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @return ResponseEntity contains the List of retrieved entities as body
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<T>> getAll() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		List<T> list = getService().findAll(name);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	/**
	 * @param entity entity object to be created
	 * @return ResponseEntity a response entity that has the just created object as body
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<T> create(@RequestBody
	T entity) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		T result = getService().create(entity, name);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	/**
	 * @param entity the entity object to be updated
	 * @param resourcePath  unused it should be removed
	 * @return ResponseEntity a response entity that has the just modified object as body
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<T> update(@RequestBody
	T entity, @PathVariable(value = "id")
	String resourcePath) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		T result = getService().create(entity, name);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * @param id the id of the entity to be deleted
	 * @return ResponseStatus response entity with a {@link org.springframework.http.HttpStatus#NO_CONTENT} status
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> delete(@PathVariable(value = "id")
	Long id) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		getService().deleteById(id, name);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
