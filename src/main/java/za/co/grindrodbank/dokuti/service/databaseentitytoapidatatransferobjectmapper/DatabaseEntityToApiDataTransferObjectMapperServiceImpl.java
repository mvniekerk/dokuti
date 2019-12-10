/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.service.databaseentitytoapidatatransferobjectmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.openapitools.model.Attribute;
import org.openapitools.model.Document;
import org.openapitools.model.DocumentAttribute;
import org.openapitools.model.DocumentVersion;
import org.openapitools.model.LookupTag;
import org.openapitools.model.Permission;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.security.service.accesstokenpermissions.SecurityContextUtility;


@Service
public class DatabaseEntityToApiDataTransferObjectMapperServiceImpl
		implements DatabaseEntityToApiDataTransferObjectMapperService {

	public Document mapDocumentEntityToDocument(DocumentEntity entity) {
		Document document = new Document();
		// Map the base properties.
		BeanUtils.copyProperties(entity, document);
		// Map all document versions.
		List<DocumentVersion> documentVersions = new ArrayList<>();
		entity.getDocumentVersions().forEach(documentVersionEntity -> {
			DocumentVersion documentVersion = new DocumentVersion();
			BeanUtils.copyProperties(documentVersionEntity, documentVersion);
			documentVersion.setCreatedDateTime(documentVersionEntity.getCreatedDateTime().toOffsetDateTime());
			documentVersions.add(documentVersion);
		});
		document.setDocumentVersions(documentVersions);
		// Map all associated Tags.
		List<LookupTag> documentTags = new ArrayList<>();
		entity.getDocumentTags().forEach(documentTagEntity -> {
			LookupTag tag = new LookupTag();
			tag.setTag(documentTagEntity.getId().getTag());
			documentTags.add(tag);
		});
		document.setTags(documentTags);

		// Map all associated Document Attributes.
		List<DocumentAttribute> documentAttributes = new ArrayList<>();
		entity.getDocumentAttributes().forEach(documentAttributeEntity -> {
			DocumentAttribute documentAttribute = mapDocumentAttributeEntityToDocumentAttribute(
					documentAttributeEntity);

			documentAttributes.add(documentAttribute);
		});
		document.setAttributes(documentAttributes);
		
		List<String> myaccess = new ArrayList<>();
		List<Permission> usersPermissions = new ArrayList<>();
		List<Permission> teamsPermissions = new ArrayList<>();
		document.setPermissions(myaccess);
		document.setTeamsPermissions(teamsPermissions);
		document.setUsersPermissions(usersPermissions);
		UUID userId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
		
        entity.getDocumentPermissions().forEach(e -> {
            if (userId.equals(e.getUserId())) {
                myaccess.add(e.getPermission());
            }
        });

        HashMap<UUID, List<String>> usersMap = new HashMap<>();
        HashMap<UUID, List<String>> teamsMap = new HashMap<>();
        
        entity.getDocumentPermissions().forEach(e -> {
            if (e.getTeamId() != null) {
                if (teamsMap.containsKey(e.getTeamId())) {
                    List<String> p = teamsMap.get(e.getTeamId());
                    p.add(e.getPermission());
                    teamsMap.put(e.getTeamId(), p);
                } else {
                    List<String> p = new ArrayList<>();
                    p.add(e.getPermission());
                    teamsMap.put(e.getTeamId(), p);
                }
            } else if (e.getUserId() != null && !userId.equals(e.getUserId())) {
                if (usersMap.containsKey(e.getUserId())) {
                    List<String> p = usersMap.get(e.getUserId());
                    p.add(e.getPermission());
                    usersMap.put(e.getUserId(), p);
                } else {
                    List<String> p = new ArrayList<>();
                    p.add(e.getPermission());
                    usersMap.put(e.getUserId(), p);
                }
            }
        });
        
        for (UUID i : usersMap.keySet()) {
            Permission p = new Permission();
            p.setUuid(i.toString());
            p.setPermissions(usersMap.get(i));
            usersPermissions.add(p);
        }
        
        for (UUID i : teamsMap.keySet()) {
            Permission p = new Permission();
            p.setUuid(i.toString());
            p.setPermissions(teamsMap.get(i));
            teamsPermissions.add(p);
        }        

        return document;
	}

	public Page<Document> mapDocumentEntityPageToDocumentPage(Page<DocumentEntity> entities) {
		return entities.map(entity -> mapDocumentEntityToDocument(entity));
	}

	public DocumentAttribute mapDocumentAttributeEntityToDocumentAttribute(
			DocumentAttributeEntity documentAttributeEntity) {
		DocumentAttribute documentAttribute = new DocumentAttribute();
		documentAttribute.setName(documentAttributeEntity.getAttribute().getName());
		documentAttribute.setValidationRegex(documentAttributeEntity.getAttribute().getValidationRegex());
		documentAttribute.setValue(documentAttributeEntity.getValue());

		return documentAttribute;
	}

	public LookupTag mapDocumentTagEntityToLookupTag(String tag) {
		LookupTag lookupTag = new LookupTag();
		lookupTag.setTag(tag);

		return lookupTag;
	}

	public Page<LookupTag> mapDocumentTagsPageToLookupTagPage(Page<String> tags) {
		return tags.map(tag -> mapDocumentTagEntityToLookupTag(tag));
	}

	public Attribute mapAttributeEntityToAttribute(AttributeEntity entity) {
		Attribute dto = new Attribute();
		BeanUtils.copyProperties(entity, dto);
		dto.setId(entity.getId().intValue());

		return dto;
	}

	public Page<Attribute> mapAttributeEntityPageToAttributePage(Page<AttributeEntity> entities) {
		return entities.map(entity -> mapAttributeEntityToAttribute(entity));
	}

}
