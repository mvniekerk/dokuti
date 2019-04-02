/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import java.time.LocalDateTime;
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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "document_acl")
public class DocumentAcl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_uuid")
	private UUID userId;
	@Type(type = "org.hibernate.type.TextType")
	private String permission;

	private Boolean mayAssign;

	@Column(name = "_granted_by")
	private UUID grantedBy;

	@Column(name = "_granted_on")
	@UpdateTimestamp
	private LocalDateTime grantedOn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	@JsonBackReference
	private DocumentEntity document;

	public DocumentAcl(UUID userId, String permission, Boolean mayAssign, UUID grantedBy, DocumentEntity document) {
		this.userId = userId;
		this.permission = permission;
		this.mayAssign = mayAssign;
		this.grantedBy = grantedBy;
		this.document = document;
	}

	public DocumentAcl() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Boolean getMayAssign() {
		return mayAssign;
	}

	public void setMayAssign(Boolean mayAssign) {
		this.mayAssign = mayAssign;
	}

	public UUID getGrantedBy() {
		return grantedBy;
	}

	public void setGrantedBy(UUID grantedBy) {
		this.grantedBy = grantedBy;
	}

	public LocalDateTime getGrantedOn() {
		return grantedOn;
	}

	public void setGrantedOn(LocalDateTime grantedOn) {
		this.grantedOn = grantedOn;
	}

	public DocumentEntity getDocument() {
		return document;
	}

	public void setDocument(DocumentEntity document) {
		this.document = document;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((grantedBy == null) ? 0 : grantedBy.hashCode());
		result = prime * result + ((grantedOn == null) ? 0 : grantedOn.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mayAssign == null) ? 0 : mayAssign.hashCode());
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
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

		DocumentAcl other = (DocumentAcl) obj;

		if (document == null) {
			if (other.document != null) {
				return false;
			}
		} else if (!document.equals(other.document)) {
			return false;
		}
		if (grantedBy == null) {
			if (other.grantedBy != null) {
				return false;
			}
		} else if (!grantedBy.equals(other.grantedBy)) {
			return false;
		}
		if (grantedOn == null) {
			if (other.grantedOn != null) {
				return false;
			}
		} else if (!grantedOn.equals(other.grantedOn)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (mayAssign == null) {
			if (other.mayAssign != null) {
				return false;
			}
		} else if (!mayAssign.equals(other.mayAssign)) {
			return false;
		}
		if (permission == null) {
			if (other.permission != null) {
				return false;
			}
		} else if (!permission.equals(other.permission)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}

		return true;
	}

}
