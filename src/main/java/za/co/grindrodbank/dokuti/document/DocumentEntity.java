/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionEntity;
import za.co.grindrodbank.dokuti.favourite.DocumentFavouriteEntity;
import za.co.grindrodbank.dokuti.lifetime.DocumentLifeTimeEntity;
import za.co.grindrodbank.dokuti.service.resourcepermissions.DocumentPermission;
import java.util.Objects;

@Entity
@Table(name = "document")
public class DocumentEntity {

	@Id
	@Column(name = "id", updatable = false, unique = true, nullable = false, columnDefinition = "uuid default gen_random_uuid()")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Type(type = "org.hibernate.type.TextType")
	@NotNull
	private String name;

	@Type(type = "org.hibernate.type.TextType")
	@NotNull
	private String description;
	
	@NotNull
	@Column(name = "is_archived", nullable = false)
	private Boolean isArchived = false;

	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "content_type", nullable = false)
	private String contentType;

	@Column(name = "__updated_on")
	@UpdateTimestamp
	private ZonedDateTime updatedOn;

	@Column(name = "__updated_by")
	private UUID updatedBy;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "document")
	@OrderBy("__uploaded_on desc")
	private Set<DocumentVersionEntity> documentVersions;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "document", cascade = CascadeType.ALL)
	@OrderBy("tag")
	private List<DocumentTagEntity> documentTags;

	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DocumentAttributeEntity> documentAttributes = new ArrayList<>();

	@JsonBackReference
	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DocumentAcl> documentPermissions = new ArrayList<>();
	
	
    @JsonBackReference
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentLifeTimeEntity> documentHistory = new ArrayList<>();	
	
    @JsonBackReference
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentFavouriteEntity> documentFavourites = new ArrayList<>();	
    
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ZonedDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(ZonedDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
	
	public UUID getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(UUID updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Set<DocumentVersionEntity> getDocumentVersions() {
		return documentVersions;
	}

	public List<DocumentTagEntity> getDocumentTags() {
		return documentTags;
	}

	public void setDocumentTags(List<DocumentTagEntity> documentTags) {
		this.documentTags = documentTags;
	}

	public void setDocumentVersions(Set<DocumentVersionEntity> documentVersions) {
		this.documentVersions = documentVersions;
	}

	public List<DocumentAttributeEntity> getDocumentAttributes() {
		return documentAttributes;
	}

	public void setDocumentAttributes(List<DocumentAttributeEntity> documentAttributes) {
		this.documentAttributes = documentAttributes;
	}

	public List<DocumentAcl> getDocumentPermissions() {
		return documentPermissions;
	}

	public void setDocumentPermissions(List<DocumentAcl> documentPermissions) {
		this.documentPermissions = documentPermissions;
	}


    public List<DocumentFavouriteEntity> getDocumentFavourites() {
        return documentFavourites;
    }

    public void setDocumentFavourites(List<DocumentFavouriteEntity> documentFavourites) {
        this.documentFavourites = documentFavourites;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
    
    public List<DocumentLifeTimeEntity> getDocumentHistory() {
        return documentHistory;
    }

    public void setDocumentHistory(List<DocumentLifeTimeEntity> documentHistory) {
        this.documentHistory = documentHistory;
    }

    public DocumentVersionEntity getLatestDocumentVersion() {
		Optional<DocumentVersionEntity> optionalLatestDocumentVersion = this.getDocumentVersions().stream().findFirst();

		if (optionalLatestDocumentVersion.isPresent()) {
			return optionalLatestDocumentVersion.get();
		}

		return null;
	}

	/**
	 * Associates an attribute, and it's value, to a document.
	 * 
	 * @param attribute The attribute to associate with the document.
	 * @param value     The value of the attribute association.
	 * @return An instance of the added document attribute.
	 */
	public DocumentAttributeEntity addAttribute(AttributeEntity attribute, String value, UUID addedBy) {
		DocumentAttributeEntity documentAttribute = new DocumentAttributeEntity(this, attribute, value);
		documentAttribute.setUpdatedBy(addedBy);

		documentAttributes.add(documentAttribute);

		return documentAttribute;
	}

	/**
	 * Removes an attribute association from the document.
	 * 
	 * @param attribute The attribute to remove from the document.
	 */
	public void removeAttribute(AttributeEntity attribute) {
		for (Iterator<DocumentAttributeEntity> iterator = documentAttributes.iterator(); iterator.hasNext();) {
			DocumentAttributeEntity documentAttribute = iterator.next();

			if (documentAttribute.getDocument().equals(this) && documentAttribute.getAttribute().equals(attribute)) {
				iterator.remove();
				documentAttribute.getAttribute().getDocumentAttributes().remove(documentAttribute);
				documentAttribute.setDocument(null);
				documentAttribute.setAttribute(null);
			}
		}
	}

	/**
	 * ; Adds a new document permission to the document.
	 * 
	 * @param attribute The attribute to associate with the document.
	 * @param value     The value of the attribute association.
	 * @return An instance of the added document attribute.
	 */
	public DocumentAcl addPermission(UUID userId, DocumentPermission documentPermissionValue, Boolean mayAssign,
			UUID grantedBy) {
		DocumentAcl documentPermission = new DocumentAcl(userId, documentPermissionValue.toString(), mayAssign,
				grantedBy, this);

		documentPermissions.add(documentPermission);

		return documentPermission;
	}

	/**
	 * Removes a permission from the document.
	 * 
	 * @param permissionValue The value of the permission to remove from the
	 *                        document.
	 */
	public void removePermission(DocumentPermission documentPermissionValue) {
		for (Iterator<DocumentAcl> iterator = documentPermissions.iterator(); iterator.hasNext();) {
			DocumentAcl documentAcl = iterator.next();

			if (documentAcl.getDocument().equals(this)
					&& documentAcl.getPermission().equals(documentPermissionValue.toString())) {
				iterator.remove();
				documentAcl.setDocument(null);
			}
		}
	}

	/**
	 * Determines whether a user has a required permission for this document.
	 * 
	 * @param userId                  The UUID of the user to perform the check for.
	 * @param documentPermissionValue The actual value of the permission to perform
	 *                                the check for.
	 * @return true if the user has the required permission. False if the user does
	 *         not have the required permission.
	 */
	public Boolean userHasPermission(UUID userId, DocumentPermission documentPermissionValue) {

		return documentPermissions.stream().anyMatch(acl -> acl.getUserId().equals(userId)
				&& acl.getPermission().equals(documentPermissionValue.toString()));

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((isArchived == null) ? 0 : isArchived.hashCode());
		return result;
	}

    @Override
    public boolean equals(Object object) {
        if (object instanceof DocumentEntity) {
            DocumentEntity that = (DocumentEntity) object;
            return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && Objects.equals(this.description, that.description) && Objects.equals(this.isArchived, that.isArchived)
                    && Objects.equals(this.contentType, that.contentType);
        }
        return false;
    }

	
	
    @Override
    public String toString() {
        return "DocumentEntity [id=" + id + ", name=" + name + ", description=" + description + ", isArchived=" + isArchived + ", contentType=" + contentType + ", updatedOn=" + updatedOn + ", updatedBy=" + updatedBy + "]";
    }

}
