/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentattribute;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DocumentAttributeId implements Serializable {

	private static final long serialVersionUID = 5457839083831390791L;

	@Column(name = "document_id")
	private UUID documentId;

	@Column(name = "attribute_label")
	private Short attributeId;

	public UUID getDocumentId() {
		return documentId;
	}

	public void setDocumentId(UUID documentId) {
		this.documentId = documentId;
	}

	public Short getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(Short attributeId) {
		this.attributeId = attributeId;
	}

	public DocumentAttributeId(UUID documentId, Short attributeId) {
		this.documentId = documentId;
		this.attributeId = attributeId;
	}

	public DocumentAttributeId() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		DocumentAttributeId that = (DocumentAttributeId) o;
		return Objects.equals(documentId, that.documentId) && Objects.equals(attributeId, that.attributeId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(documentId, attributeId);
	}

}
