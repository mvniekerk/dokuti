/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.attribute;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "attribute_label")
public class AttributeEntity {

	@Id
	@Column(name = "id", columnDefinition = "SMALLINT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;

	@Type(type = "org.hibernate.type.TextType")
	private String name;

	@Column(name = "attribute_validaton")
	@Type(type = "org.hibernate.type.TextType")
	private String validationRegex;

	@Column(name = "__updated_on")
	@UpdateTimestamp
	private ZonedDateTime updatedOn;

	@Column(name = "__updated_by")
	private UUID updatedBy;

	@OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference
	private List<DocumentAttributeEntity> documentAttributes = new ArrayList<>();

	public AttributeEntity(String name, String validationRegex) {
		this.name = name;
		this.validationRegex = validationRegex;
	}

	public AttributeEntity() {
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValidationRegex() {
		return validationRegex;
	}

	public void setValidationRegex(String validationRegex) {
		this.validationRegex = validationRegex;
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

	public List<DocumentAttributeEntity> getDocumentAttributes() {
		return documentAttributes;
	}

	public void setDocumentAttributes(List<DocumentAttributeEntity> documentAttributes) {
		this.documentAttributes = documentAttributes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, validationRegex);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AttributeEntity other = (AttributeEntity) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (validationRegex == null) {
			if (other.validationRegex != null) {
				return false;
			}
		} else if (!validationRegex.equals(other.validationRegex)) {
			return false;
		}

		return true;
	}

	/**
	 * Determines whether the currently set validationRegex is a valid regular
	 * expression.
	 * 
	 * @return True if the regular expression is valid, false if not.
	 */
	public boolean validationRegexIsValid() {
		try {
			Pattern.compile(this.getValidationRegex());
		} catch (PatternSyntaxException e) {
			return false;
		}

		return true;
	}

}
