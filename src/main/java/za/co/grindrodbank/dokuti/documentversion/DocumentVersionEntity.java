/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentversion;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;

@Entity
@Table(name = "document_version")
public class DocumentVersionEntity {

	@Id
	@Column(name = "id", updatable = false, unique = true, nullable = false, columnDefinition = "uuid default gen_random_uuid()")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	@CreationTimestamp
	@Column(name = "__uploaded_on")
	private ZonedDateTime createdDateTime;

	@Column(name = "__uploaded_by")
	private UUID uploadedBy;

	@Column(length = 64)
	private String checksum;
	
    @Column(name = "checksum_algo")
    private String checksumAlgo;	
	
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "document_type", nullable = false)
    private String documentType;	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	@JsonIgnore
	private DocumentEntity document;
	
    @OneToMany(mappedBy = "documentVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentAttributeEntity> documentAttributes = new ArrayList<>();	
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ZonedDateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(ZonedDateTime createdDatedTime) {
		this.createdDateTime = createdDatedTime;
	}

	public UUID getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(UUID uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public DocumentEntity getDocument() {
		return this.document;
	}

	public void setDocument(DocumentEntity document) {
		this.document = document;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getChecksumAlgo() {
        return checksumAlgo;
    }

    public void setChecksumAlgo(String checksumAlgo) {
        this.checksumAlgo = checksumAlgo;
    }

    public List<DocumentAttributeEntity> getDocumentAttributes() {
        return documentAttributes;
    }

    public void setDocumentAttributes(List<DocumentAttributeEntity> documentAttributes) {
        this.documentAttributes = documentAttributes;
    }

    /**
     * Associates an attribute, and it's value, to a document version.
     * 
     * @param attribute The attribute to associate with the document version.
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
     * Removes an attribute association from the document version.
     * 
     * @param attribute The attribute to remove from the document version.
     */
    public void removeAttribute(AttributeEntity attribute) {
        for (Iterator<DocumentAttributeEntity> iterator = documentAttributes.iterator(); iterator.hasNext();) {
            DocumentAttributeEntity documentAttribute = iterator.next();

            if (documentAttribute.getDocumentVersion().equals(this) && documentAttribute.getAttribute().equals(attribute)) {
                iterator.remove();
                documentAttribute.getAttribute().getDocumentAttributes().remove(documentAttribute);
                documentAttribute.setDocumentVersion(null);
                documentAttribute.setAttribute(null);
            }
        }
    }    
}
