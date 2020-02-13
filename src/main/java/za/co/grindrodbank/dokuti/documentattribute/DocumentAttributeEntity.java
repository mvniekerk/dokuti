/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentattribute;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionEntity;

@Entity
@Table(name = "document_attribute")
public class DocumentAttributeEntity {

	private static final Logger logger = LoggerFactory.getLogger(DocumentAttributeEntity.class);

	@JsonBackReference
	@EmbeddedId
	private DocumentAttributeId id;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_version_id", insertable = false, updatable = false)
	private DocumentVersionEntity documentVersion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_label", insertable = false, updatable = false)
	private AttributeEntity attribute;

	@Column(name = "attribute_value")
	@Type(type = "org.hibernate.type.TextType")
	private String value;

	@Column(name = "__updated_on")
	@UpdateTimestamp
	private ZonedDateTime updatedOn;

	@Column(name = "__updated_by")
	private UUID updatedBy;

	public DocumentAttributeId getId() {
		return id;
	}

	public void setId(DocumentAttributeId id) {
		this.id = id;
	}

	public DocumentVersionEntity getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(DocumentVersionEntity documentVersion) {
        this.documentVersion = documentVersion;
    }

    public AttributeEntity getAttribute() {
		return attribute;
	}

	public void setAttribute(AttributeEntity attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public DocumentAttributeEntity(DocumentVersionEntity documentVersion, AttributeEntity attribute, String value) {
		this.documentVersion = documentVersion;
		this.attribute = attribute;
		this.id = new DocumentAttributeId(documentVersion.getId(), attribute.getId());
		this.value = value;
	}

	public DocumentAttributeEntity() {

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		DocumentAttributeEntity that = (DocumentAttributeEntity) o;
		return Objects.equals(documentVersion, that.documentVersion) && Objects.equals(attribute, that.attribute)
				&& Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(documentVersion, attribute);
	}

	public boolean isValid() {
		try {
			return Pattern.compile(this.attribute.getValidationRegex()).matcher(this.value).find();
		} catch (PatternSyntaxException e) {
			logger.error("Error compiling regex pattern for DocumentAttributeEntity. Attempted regex: '{}'",
					this.attribute.getValidationRegex());

			return false;
		}
	}
}
