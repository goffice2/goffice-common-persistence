package net.gvcc.goffice.multitenancy.config;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataSourceMultiTenant {
	private String name;
	private DataSource dataSource;
	private boolean main;
}
