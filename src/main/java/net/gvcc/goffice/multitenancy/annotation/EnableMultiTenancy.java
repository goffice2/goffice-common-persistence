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
package net.gvcc.goffice.multitenancy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import net.gvcc.goffice.multitenancy.config.MultiTenantJPAConfiguration;

/**
 *
 * <p>
 * The <code>EnableMultiTenancy</code> class
 * </p>
 * <p>
 * This annotation if used in a spring application with autoconfiguration through 
 * annotation scanning, makes available to the spring context all the components and the 
 * configuration classes necessary to make the multitenancy working in Goffice2 microservice
 * </p>
 * <p>
 * Data: 29 apr 2022
 * </p>
 * 
 * @author cristian muraca
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MultiTenantJPAConfiguration.class)
// @ComponentScan(basePackageClasses = { MultiTenantJPAConfiguration.class, MultiTenantConnectionProvider.class })
public @interface EnableMultiTenancy {

}
