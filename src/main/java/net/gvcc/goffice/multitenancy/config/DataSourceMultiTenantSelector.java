package net.gvcc.goffice.multitenancy.config;

import org.springframework.stereotype.Component;

import lombok.Setter;

@Component
@Setter
public final class DataSourceMultiTenantSelector {
	public interface IDataSourceMultiTenantSelector {
		String select();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	private IDataSourceMultiTenantSelector selector = null; // DEFAULT_DATABASE_SELECTOR;

	public String getDatabaseIdentifier() {
		return selector == null ? null : selector.select();
	}
}
