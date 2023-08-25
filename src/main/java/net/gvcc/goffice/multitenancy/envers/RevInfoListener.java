package net.gvcc.goffice.multitenancy.envers;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * <p>
 * The <code>RevInfoListener</code> class
 * </p>
 * <p>
 * Data: 5 set 2022
 * </p>
 * 
 * @author <a href="mailto:edv@gvcc.net"></a>
 * @version 1.0
 */
public class RevInfoListener implements RevisionListener {

	/**
	 * 
	 */
	public void newRevision(Object revisionEntity) {
		RevInfo exampleRevEntity = (RevInfo) revisionEntity;
		if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
			exampleRevEntity.setModifiedBy(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
		}
	}
}