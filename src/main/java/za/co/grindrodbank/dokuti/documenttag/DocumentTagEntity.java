/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documenttag;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import za.co.grindrodbank.dokuti.document.DocumentEntity;

@Entity
@Table(name = "document_tag", uniqueConstraints = { @UniqueConstraint(columnNames = { "document_id", "tag" }) })
public class DocumentTagEntity {

	@EmbeddedId
	@AttributeOverride(name = "documentId", column = @Column(name = "document_id"))
	private DocumentTagId id;
	@ManyToOne
	@MapsId("id")
	@JoinColumn(name = "document_id", referencedColumnName = "id")
	@JsonIgnore
	private DocumentEntity document;

	public DocumentTagId getId() {
		return id;
	}

	public void setId(DocumentTagId id) {
		this.id = id;
	}

	public DocumentEntity getDocument() {
		return this.document;
	}

	public void setDocument(DocumentEntity document) {
		this.document = document;
	}

}
