/*
 * // * goffice... // * https://www.goffice.org // * // * Copyright (c) 2005-2022 Consorzio dei Comuni della Provincia di Bolzano Soc. Coop. <https://www.gvcc.net>. // * // * This program is free
 * software: you can redistribute it and/or modify // * it under the terms of the GNU Affero General Public License as // * published by the Free Software Foundation, either version 3 of the // *
 * License, or (at your option) any later version. // * // * This program is distributed in the hope that it will be useful, // * but WITHOUT ANY WARRANTY; without even the implied warranty of // *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the // * GNU Affero General Public License for more details. // * // * You should have received a copy of the GNU Affero General Public
 * License // * along with this program. If not, see <https://www.gnu.org/licenses/>. //
 */
// package net.gvcc.goffice.multitenancy.config;
//
// import java.sql.Connection;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.util.HashMap;
// import java.util.Map;
//
// import javax.sql.DataSource;
//
/// **
// *
// * <p>
// * The <code>AbstractDatabaseSwitchingDataSource</code> class
// * </p>
// * <p>
// * Data: 29 apr 2022
// * </p>
// *
// * @author cristian muraca
// * @version 1.0
// */
// public abstract class AbstractDatabaseSwitchingDataSource {
//
// /**
// *
// * <p>
// * The <code>Language</code> class
// * </p>
// * <p>
// * Data: 29 apr 2022
// * </p>
// *
// * @author <a href="mailto:edv@gvcc.net"></a>
// * @version 1.0
// */
// public static enum Language {
// HSQL("SET SCHEMA ", ""), MYSQL("USE ", "`"), ORACLE("ALTER SESSION SET CURRENT_SCHEMA=", "\""), POSTGRESQL("SET search_path TO ", "");
//
// private final String switchCommand;
// private final String quoteChar;
// private static Map<String, Language> languageMap = new HashMap<String, Language>();
//
// private Language(String switchCommand, String quoteChar) {
// this.switchCommand = switchCommand;
// this.quoteChar = quoteChar;
// }
//
// static {
// for (Language en : Language.values()) {
// languageMap.put(en.name(), en);
// }
// }
//
// public String switchDatabase(String dbName) {
// String query = switchCommand + quoteChar + dbName + quoteChar;
// if (this.name().equals(ORACLE.name())) {
// return query;
// } else {
// return query + ";";
// }
// }
// };
//
// protected DataSource wrappedDataSource;
// protected Language language = Language.MYSQL;
//
// /**
// * @param l
// */
// public void setLanguage(Language l) {
// this.language = l;
// }
//
// /**
// * @param tenantId
// * @param con
// * @return Connection
// * @throws SQLException
// */
// protected Connection switchDatabase(String tenantId, Connection con) throws SQLException {
// String databaseName = getDatabaseName();
// if (databaseName != null) {
// Statement s = con.createStatement();
// try {
// s.execute(getLanguage(databaseName).switchDatabase(tenantId));
// } catch (SQLException e) {
// con.close();
// throw e;
// } finally {
// s.close();
// }
// }
// return con;
// }
//
// /**
// * @return String
// */
// abstract protected String getDatabaseName();
//
// /**
// * @param databaseName
// * @return Language
// */
// protected Language getLanguage(String databaseName) {
// return Language.languageMap.get(databaseName.toUpperCase());
// }
//
// }
