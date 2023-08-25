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

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 *
 * <p>
 * The <code>GofficeRevisionEntity</code> class
 * </p>
 * <p>
 * This class is used as base class for the specific revision entity classes in functional microservices
 * It is only needed if auditing is required and it offers a common place to configure additional fields 
 * that needs to be tracked as part of the RevAud record created by Envers when the {@link org.hibernate.envers.Audited} annotation is used 
 * </p>
 * <p>
 * Data: 4 mag 2022
 * </p>
 * 
 * @author cristian muraca
 *
 * @version 1.0
 *
 * @see <a href="https://hibernate.org/orm/envers/">Envers</a>
 */
@MappedSuperclass
public class GofficeRevisionEntity implements Serializable {

	private static final long serialVersionUID = 8530213963961662300L;

	/**
	 * Revision number and ID of the revision entity
	 */
	@Id
	@RevisionNumber
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "revision_number")
	private int revisionNumber;

	/**
	 * Time when the revision is created
	 */
	@RevisionTimestamp
	@Column(name = "revision_timestamp")
	private long revisionTimestamp;

	/**
	 * Create an instance
	 */
	public GofficeRevisionEntity() {
	}

	/**
	 * @return the revision number of the revision entity
	 */
	public int getRevisionNumber() {
		return this.revisionNumber;
	}

	/**
	 * @return Date
	 */
	@Transient
	public Date getRevisionDate() {
		return new Date(this.revisionTimestamp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GofficeRevisionEntity)) {
			return false;
		} else {
			GofficeRevisionEntity that = (GofficeRevisionEntity) o;
			return this.revisionNumber == that.revisionNumber && this.revisionTimestamp == that.revisionTimestamp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = this.revisionNumber;
		result = 31 * result + (int) (this.revisionTimestamp ^ this.revisionTimestamp >>> 32);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "RevisionEntity(revisionNumber = " + this.revisionNumber + ", revisionDate = " + DateFormat.getDateTimeInstance().format(this.getRevisionDate()) + ")";
	}
}