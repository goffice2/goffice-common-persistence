package net.gvcc.goffice.multitenancy.config;

import java.util.HashMap;

import javax.sql.DataSource;

public class DataSourceMultiTenantMap extends HashMap<String, DataSourceMultiTenant> {

	private static final long serialVersionUID = 1L;

	@Override
	public DataSourceMultiTenant put(String key, DataSourceMultiTenant obj) {
		throw new UnsupportedOperationException("Not allowed! Use: put(String name, DataSource dataSource) instead");
	}

	public DataSourceMultiTenant put(String name, DataSource dataSource, boolean isMain) {
		return super.put(name, new DataSourceMultiTenant(name, dataSource, isMain));
	}
}
