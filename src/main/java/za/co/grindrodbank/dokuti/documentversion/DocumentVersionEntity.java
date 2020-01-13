/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentversion;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import za.co.grindrodbank.dokuti.document.DocumentEntity;

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

	@Column(length = 32)
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

}
