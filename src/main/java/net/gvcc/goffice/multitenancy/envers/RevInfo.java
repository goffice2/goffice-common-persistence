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
package net.gvcc.goffice.multitenancy.envers;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;

import net.gvcc.goffice.multitenancy.common.GofficeRevisionEntity;

/**
 * informazioni di audit aggiuntive di envers
 * <p>
 * The <code>RevInfo</code> class
 * </p>
 * <p>
 * Data: 4 mag 2022
 * </p>
 * 
 * @author cristian muraca
 * @version 1.0
 */
@Entity
@RevisionEntity(RevInfoListener.class)
@Table(name = "revinfo")
public class RevInfo extends GofficeRevisionEntity {
	private static final long serialVersionUID = 1L;

	public RevInfo() {
	}

	@Column(name = "modifiedby")
	private String modifiedBy;

	/**
	 * 
	 * @return getter for modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * 
	 * @param modifiedBy
	 *            setter for modifiedBy
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RevInfo []");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(modifiedBy);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RevInfo other = (RevInfo) obj;
		return Objects.equals(modifiedBy, other.modifiedBy);
	}
}