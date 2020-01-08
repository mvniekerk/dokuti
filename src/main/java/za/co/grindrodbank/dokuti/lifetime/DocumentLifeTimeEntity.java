package za.co.grindrodbank.dokuti.lifetime;

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

import com.fasterxml.jackson.annotation.JsonBackReference;

import za.co.grindrodbank.dokuti.document.DocumentEntity;
import java.util.Objects;

@Entity
@Table(name = "document_acl_history")
public class DocumentLifeTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    @JsonBackReference
    private DocumentEntity document;       
    
    @Column(name = "user_id")
    private UUID userId;    
    
    
    @Column(name = "team_id")
    private UUID teamId;
    
    @Type(type = "org.hibernate.type.TextType")
    private String permission;
    
    @Column(name = "granted_by")
    private UUID grantedBy;

    @Column(name = "granted_on")
    private LocalDateTime grantedOn;
    
    @Column(name = "revoked_by")
    private UUID revokedBy;

    @Column(name = "revoked_on")
    private LocalDateTime revokedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
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

    public UUID getRevokedBy() {
        return revokedBy;
    }

    public void setRevokedBy(UUID revokedBy) {
        this.revokedBy = revokedBy;
    }

    public LocalDateTime getRevokedOn() {
        return revokedOn;
    }

    public void setRevokedOn(LocalDateTime revokedOn) {
        this.revokedOn = revokedOn;
    }

    @Override
    public String toString() {
        return "DocumentLifeTimeEntity [userId=" + userId + ", teamId=" + teamId + ", permission=" + permission + ", grantedBy=" + grantedBy + ", grantedOn=" + grantedOn + ", revokedBy=" + revokedBy + ", revokedOn=" + revokedOn + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, teamId, permission, grantedBy, grantedOn, revokedBy, revokedOn);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DocumentLifeTimeEntity) {
            DocumentLifeTimeEntity that = (DocumentLifeTimeEntity) object;
            return Objects.equals(this.userId, that.userId) && Objects.equals(this.teamId, that.teamId) && Objects.equals(this.permission, that.permission) && Objects.equals(this.grantedBy, that.grantedBy)
                    && Objects.equals(this.grantedOn, that.grantedOn) && Objects.equals(this.revokedBy, that.revokedBy) && Objects.equals(this.revokedOn, that.revokedOn);
        }
        return false;
    }
    
    
}
