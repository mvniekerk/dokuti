package za.co.grindrodbank.dokuti.document;

import java.util.UUID;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.dokuti.favourite.DocumentFavouriteEntity;
import za.co.grindrodbank.dokuti.service.resourcepermissions.DocumentPermission;
import za.co.grindrodbank.dokuti.utilities.SecurityContextUtility;

public class DocumentEntitySpecifications {
    
    private DocumentEntitySpecifications() {
    }
    
	public static Specification<DocumentEntity> documentEntitiesWithName(String name) {
		return new Specification<DocumentEntity>() {
			private static final long serialVersionUID = -6512089736136918289L;

			@Override
			public Predicate toPredicate(Root<DocumentEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				if (name == null) {
					return null;
				}

				return criteriaBuilder.equal(root.get("name"), name);
			}
		};
	}

	public static Specification<DocumentEntity> documentEntitiesWhereUserHasPermission(DocumentPermission permission,
			UUID userId) {
		return new Specification<DocumentEntity>() {
			private static final long serialVersionUID = -1060194637616949204L;

			@Override
			public Predicate toPredicate(Root<DocumentEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				Join<DocumentEntity, DocumentAcl> documentAclJoin = root.join("documentPermissions");
				return criteriaBuilder.and(
						criteriaBuilder.equal(documentAclJoin.get("permission"), permission.toString()),
						criteriaBuilder.equal(documentAclJoin.get("userId"), userId));
			}
		};
	}

	public static Specification<DocumentEntity> documentEntitiesWithTag(String tag) {
		return new Specification<DocumentEntity>() {
			private static final long serialVersionUID = -9099341830232496030L;

			@Override
			public Predicate toPredicate(Root<DocumentEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// Oddly, when no filterTags data is set, Spring creates a single element array
				// with "new ArrayList<>()" as its value.
				// Check for this, as it indicates that the predicate should not be created.
				if (tag == null || tag.isEmpty() || tag.equals("new ArrayList<>()")) {

					return null;
				}

				Join<DocumentEntity, DocumentAcl> documentTagsJoin = root.join("documentTags");

				return criteriaBuilder.equal(documentTagsJoin.get("id").get("tag"), tag);

			}
		};
	}

	public static Specification<DocumentEntity> documentEntitiesWithAttributeName(String attributeName) {
		return new Specification<DocumentEntity>() {
			private static final long serialVersionUID = -2091238246666575992L;

			@Override
			public Predicate toPredicate(Root<DocumentEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// Oddly, when no filterTags data is set, Spring creates a single element array
				// with "new ArrayList<>()" as its value.
				// Check for this, as it indicates that the predicate should not be created.
				if (attributeName == null || attributeName.isEmpty() || attributeName.equals("new ArrayList<>()")) {

					return null;
				}

				Join<DocumentEntity, DocumentAttributeEntity> documentAttributesJoin = root.join("documentAttributes");

				return criteriaBuilder.equal(documentAttributesJoin.get("attribute").get("name"), attributeName);

			}
		};
	}
	
    public static Specification<DocumentEntity> documentEntitiesWithArchiveFilter(Boolean filterArchive) {
        return new Specification<DocumentEntity>() {
            private static final long serialVersionUID = 621457537006016573L;

            @Override
            public Predicate toPredicate(Root<DocumentEntity> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                if (Boolean.TRUE.equals(filterArchive) ) {
                    return criteriaBuilder.equal(root.get("isArchived"), Boolean.TRUE);
                } 

                return criteriaBuilder.equal(root.get("isArchived"), Boolean.FALSE);
            }
        };
    }	
    
    
    public static Specification<DocumentEntity> documentEntitiesWithFavouriteUserFilter(Boolean filterByFavourites) {
        return new Specification<DocumentEntity>() {

            private static final long serialVersionUID = 6291576138226024998L;

            @Override
            public Predicate toPredicate(Root<DocumentEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (Boolean.TRUE.equals(filterByFavourites)) {
                    UUID userId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
                    Join<DocumentEntity, DocumentFavouriteEntity> documentFavouritesJoin = root.join("documentFavourites");
                    return criteriaBuilder.equal(documentFavouritesJoin.get("userId"), userId);
                }
                return null;
       
            }
            
        };
    }
	
}
