/// *
// * goffice...
// * https://www.goffice.org
// *
// * Copyright (c) 2005-2022 Consorzio dei Comuni della Provincia di Bolzano Soc. Coop. <https://www.gvcc.net>.
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Affero General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU Affero General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with this program. If not, see <https://www.gnu.org/licenses/>.
// */
// package net.gvcc.goffice.multitenancy.config;
//
// import java.util.LinkedHashMap;
// import java.util.Map;
//
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.stereotype.Component;
//
// import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
//
/// **
// *
// * <p>
// * The <code>MultiTenantProperties</code> class
// * </p>
// * <p>
// * This is an utility class used by the {@link MultiTenantJPAConfiguration} class
// * and its main responsibility is to store and provide access to {@link javax.sql.DataSource datasources}
// * </p>
// * <p>
// * Data: 29 apr 2022
// * </p>
// *
// * @author <a href="mailto:edv@gvcc.net"></a>
// * @version 1.0
// */
// @Component
// @ConfigurationProperties(value = "multitenant")
// public class MultiTenantProperties {
//
// @SuppressFBWarnings(value = { "MS_EXPOSE_REP", "EI_EXPOSE_REP", "EI_EXPOSE_REP2" }, justification = "avoid clone objects")
// private Map<String, Map<String, String>> datasources = new LinkedHashMap<>();
//
// /**
// * @return Map
// */
// public Map<String, Map<String, String>> getDatasources() {
// return datasources;
// }
//
// /**
// * @param datasources a map of map where the key of the first one is the tenant identifier and the keys of the second one represent the connection properties
// */
// public void setDatasources(Map<String, Map<String, String>> datasources) {
// this.datasources = datasources;
// }
//
// }
