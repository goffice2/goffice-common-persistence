package net.gvcc.goffice.multitenancy;

import java.time.LocalDateTime;

import net.gvcc.goffice.multitenancy.common.BaseEntity;

public class Utils {
	public static final String CREATE_TABLE = """
			CREATE TABLE IF NOT EXISTS user_details (
				id int(11) NOT NULL AUTO_INCREMENT,
				uuid varchar(255) DEFAULT NULL,
				datecreated timestamp NULL DEFAULT NULL,
				datemodified timestamp NULL DEFAULT NULL,
				createdby varchar(255) DEFAULT NULL,
				modifiedby varchar(255) DEFAULT NULL,
				hash varchar(255) DEFAULT NULL,
				full_name varchar(255) DEFAULT NULL,
				PRIMARY KEY (id),
				UNIQUE KEY uuid_UNIQUE (uuid)
			);
			""";

	public static final String CREATE_SCHEMA = """
				CREATE SCHEMA IF NOT EXISTS bolzano authorization sa;
				CREATE SCHEMA IF NOT EXISTS abtei authorization sa;
			""";

	public static <T extends BaseEntity> T createUser(T entity) {
		entity.setCreatedBy("test");
		entity.setModifiedBy("test");
		entity.setDateCreated(LocalDateTime.now());
		entity.setDateModified(LocalDateTime.now());
		entity.setHash("hash");
		return entity;
	}
}
