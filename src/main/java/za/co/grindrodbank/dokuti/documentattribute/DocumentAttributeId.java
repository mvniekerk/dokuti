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

	@Column(name = "document_version_id")
	private UUID documentVersionId;

	@Column(name = "attribute_label")
	private Short attributeId;


	public UUID getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(UUID documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public Short getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(Short attributeId) {
		this.attributeId = attributeId;
	}

	public DocumentAttributeId(UUID documentVersionId, Short attributeId) {
		this.documentVersionId = documentVersionId;
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
		return Objects.equals(documentVersionId, that.documentVersionId) && Objects.equals(attributeId, that.attributeId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(documentVersionId, attributeId);
	}

}
