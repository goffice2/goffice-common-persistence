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

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 *
 * <p>
 * The <code>BaseEntity</code> class
 * </p>
 * <p>
 * This class should be used as parent for every entity used in Goffice2. It encapsulates all the common fields required and it is used by both {@link AbstractService} and
 * {@link GofficePersistenceService} classes
 * </p>
 * <p>
 * This class will also enable Auditing using the {@link Audited} annotation
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
@Audited
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
	/**
	 * identifier of the entity
	 */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Exclude
	protected Long id;

	/**
	 * String representing the user defined unique identifier of the entity
	 */
	@Column(name = "uuid", unique = true)
	protected String uuid;

	/**
	 * LocalDateTime representing the time of the entity creation
	 */
	@CreatedDate
	@Column(name = "datecreated")
	protected LocalDateTime dateCreated;

	/**
	 * LocalDateTime representing the time of the last modification of the entity
	 */
	@LastModifiedDate
	@Column(name = "datemodified")
	protected LocalDateTime dateModified;

	/**
	 * String representing the username of the user who created the entity
	 */
	@CreatedBy
	@Column(name = "createdby")
	protected String createdBy;

	/**
	 * String representing the username of the last user who modified the entity
	 */
	@LastModifiedBy
	@Column(name = "modifiedby")
	protected String modifiedBy;

	/**
	 * String representing sha256 representation of the hashCode of the entity object
	 */
	@Column(name = "hash")
	@EqualsAndHashCode.Exclude
	protected String hash;

	/**
	 * Copy constructor
	 * 
	 * @param another
	 *            The source BaseEntity from which copy the data
	 */
	public void copyFrom(BaseEntity another) {
		this.id = another.getId();
		this.uuid = another.getUuid();
		this.dateCreated = another.getDateCreated();
		this.dateModified = another.getDateModified();
		this.createdBy = another.getCreatedBy();
		this.modifiedBy = another.getModifiedBy();
		this.hash = another.getHash();
	}
}
