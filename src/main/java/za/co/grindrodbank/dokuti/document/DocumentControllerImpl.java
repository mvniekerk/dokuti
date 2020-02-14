/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.openapitools.api.DocumentsApi;
import org.openapitools.model.CreateDocumentResponse;
import org.openapitools.model.DateTimePeriod;
import org.openapitools.model.Document;
import org.openapitools.model.DocumentAttribute;
import org.openapitools.model.DocumentAttributeRequest;
import org.openapitools.model.DocumentInfoRequest;
import org.openapitools.model.DocumentTagList;
import org.openapitools.model.DocumentVersion;
import org.openapitools.model.LifeTimeObject;
import org.openapitools.model.LifeTimeUsersList;
import org.openapitools.model.LookupTag;
import org.openapitools.model.Permission;
import org.openapitools.model.SharedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import za.co.grindrodbank.dokuti.attribute.AttributeService;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagService;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionService;
import za.co.grindrodbank.dokuti.events.PaginatedResultsRetrievedEvent;
import za.co.grindrodbank.dokuti.exceptions.InvalidRequestException;
import za.co.grindrodbank.dokuti.favourite.DocumentFavouriteEntity;
import za.co.grindrodbank.dokuti.lifetime.DocumentLifeTimeEntity;
import za.co.grindrodbank.dokuti.service.databaseentitytoapidatatransferobjectmapper.DatabaseEntityToApiDataTransferObjectMapperService;
import za.co.grindrodbank.dokuti.utilities.ParseOrderByQueryParam;
import za.co.grindrodbank.security.service.accesstokenpermissions.SecurityContextUtility;

@RestController
//@PreAuthorize("hasRole('DOKUTI_USER') or hasRole('DOKUTI_ADMIN')")
public class DocumentControllerImpl implements DocumentsApi {
	@Autowired
	private DocumentService documentService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private AttributeService attributeService;
	@Autowired
	private DocumentVersionService documentVersionService;
	@Autowired
	private DocumentTagService documentTagService;
	@Autowired
	private DatabaseEntityToApiDataTransferObjectMapperService databaseEntityToApiDataTranfserObjectMapperService;

	Logger logger = LoggerFactory.getLogger(DocumentControllerImpl.class);

	private static final String DEFAULT_SORT_FIELD = "name";

	@Override
	public ResponseEntity<CreateDocumentResponse> addDocument(String description, MultipartFile file) {
		DocumentEntity documentEntity = documentService.createNewDocument(file, description);
		CreateDocumentResponse document = new CreateDocumentResponse();
		BeanUtils.copyProperties(documentEntity, document);

		return new ResponseEntity<>(document, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Document> getDocument(UUID documentId) {
		DocumentEntity documentEntity = documentService.findById(documentId);

		return new ResponseEntity<>(
				databaseEntityToApiDataTranfserObjectMapperService.mapDocumentEntityToDocument(documentEntity),
				HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<List<Document>> getDocuments(Boolean filterArchive, Integer page, Integer size, String filterName, Boolean filterByFavourites,
			 List<String> filterTags, List<String> filterAttributes,
			 Boolean filterSharedWithOthers,Boolean filterSharedWithMe,
			List<String> orderBy) {
		Sort sort = ParseOrderByQueryParam.resolveArgument(orderBy, DEFAULT_SORT_FIELD);
		final PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<DocumentEntity> documentEntities = documentService.findAll(pageRequest, filterName, filterByFavourites, filterTags,
				filterAttributes, filterArchive, filterSharedWithOthers, filterSharedWithMe);

		if (documentEntities.hasContent()) {
			Page<Document> documents = databaseEntityToApiDataTranfserObjectMapperService
					.mapDocumentEntityPageToDocumentPage(documentEntities);
			HttpHeaders headers = new HttpHeaders();
			eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Document>(this, documents, headers));

			return new ResponseEntity<>(documents.getContent(), headers, HttpStatus.OK);
		}

		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<DocumentAttribute> createDocumentAttribute(UUID documentId, UUID documentVersionId, Integer attributeId,
			DocumentAttributeRequest documentAttributeRequest) {
		DocumentAttribute documentAttribute = createOrUpdateDocumentAttribute(documentVersionId, attributeId,
				documentAttributeRequest);

		return new ResponseEntity<>(documentAttribute, HttpStatus.OK);
	}

	private DocumentAttribute createOrUpdateDocumentAttribute(UUID documentVersionId, Integer attributeId,
			DocumentAttributeRequest documentAttributeRequest) {
	    
	    
		DocumentAttributeEntity documentAttributeEntity = documentVersionService.addDocumentAttribute(
		        documentVersionService.findById(documentVersionId), attributeService.findById(attributeId.shortValue()),
				documentAttributeRequest.getValue());

		return databaseEntityToApiDataTranfserObjectMapperService
				.mapDocumentAttributeEntityToDocumentAttribute(documentAttributeEntity);
	}

	@Override
	public ResponseEntity<DocumentAttribute> updateDocumentAttribute(UUID documentId, UUID documentVersionId, Integer attributeId,
			DocumentAttributeRequest documentAttributeRequest) {

		return new ResponseEntity<>(createOrUpdateDocumentAttribute(documentVersionId, attributeId, documentAttributeRequest),
				HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteDocumentAttribute(UUID documentId, UUID documentVersionId, Integer attributeId) {
		
        
	    documentVersionService.removeDocumentAttribute(documentVersionService.findById(documentVersionId),
              attributeService.findById(attributeId.shortValue()));	    
	    
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Resource> downloadDocument(UUID documentId) {
		DocumentEntity documentEntity = documentService.findById(documentId);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentEntity.getName() + "\"")
				.contentType(MediaType.parseMediaType(documentEntity.getLatestDocumentVersion().getDocumentType()))
				.body(documentService.getLatestDocumentData(documentEntity));
	}

	@Override
	public ResponseEntity<DocumentVersion> updateDocument(UUID documentId, MultipartFile file, String description) {
		DocumentEntity documentEntity = documentService.findById(documentId);

		if (description != null) {
			documentEntity.setDescription(description);
		}

		documentEntity = documentService.updateDocument(Optional.of(file), documentEntity);
		DocumentVersion documentVersion = new DocumentVersion();
		documentVersion.setChecksum(documentEntity.getLatestDocumentVersion().getChecksum());
		documentVersion
				.setCreatedDateTime(documentEntity.getLatestDocumentVersion().getCreatedDateTime().toOffsetDateTime());
		documentVersion.setId(documentEntity.getLatestDocumentVersion().getId());

		return new ResponseEntity<>(documentVersion, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> downloadDocumentVersion(UUID documentId, UUID documentVersionId) {
		DocumentVersionEntity documentVersion = documentVersionService.findById(documentVersionId);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + documentVersion.getDocument().getName() + "\"")
				.contentType(MediaType.parseMediaType(documentVersion.getDocument().getContentType()))
				.body(documentService.getDocumentDataForVersion(documentVersion.getDocument(), documentVersion));
	}

	@Override
	public ResponseEntity<DocumentTagList> addDocumentTags(UUID documentId, List<LookupTag> lookupTag) {
		DocumentEntity documentEntity = documentService.findById(documentId);

		List<String> tags = new ArrayList<>();
		lookupTag.forEach(lookupTagElement -> tags.add(lookupTagElement.getTag()));

		documentTagService.createDocumentTags(tags, documentEntity);
		DocumentTagList documentTagList = new DocumentTagList();
		documentTagList.setDocumentId(documentId);
		documentTagList.setTags(lookupTag);

		return new ResponseEntity<>(documentTagList, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> removeAllDocumentTags(UUID documentId) {
		documentService.removeAllDocumentTags(documentService.findById(documentId));

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@Override
    public ResponseEntity<Document> updateDocumentInfo(UUID documentId, DocumentInfoRequest documentInfoRequest) {
		DocumentEntity documentEntity = documentService.findById(documentId);
		if (documentInfoRequest.getName() != null) {
		   documentEntity.setName(documentInfoRequest.getName());
		}
		if (documentInfoRequest.getDescription() != null) {
			   documentEntity.setDescription(documentInfoRequest.getDescription());
			}		
		documentEntity = documentService.save(documentEntity);
		Document res = databaseEntityToApiDataTranfserObjectMapperService.mapDocumentEntityToDocument(documentEntity);
		return new ResponseEntity<>(res, HttpStatus.OK);			
    }	
	
    private ResponseEntity<Document> changeArchiveStatus(UUID documentId, Boolean status) {
        DocumentEntity documentEntity = documentService.findById(documentId);
        documentEntity.setIsArchived(status);
        documentEntity = documentService.save(documentEntity);
        Document res = databaseEntityToApiDataTranfserObjectMapperService.mapDocumentEntityToDocument(documentEntity);
        return new ResponseEntity<>(res, HttpStatus.OK);    

    }
	
    @Override
    public ResponseEntity<Document> archiveDocument(UUID documentId) {
       return changeArchiveStatus(documentId, true);

    }
    
    @Override
    public ResponseEntity<Document> unarchiveDocument(UUID documentId) {
        return changeArchiveStatus(documentId, false);
    }
    
    
    @Override
    public ResponseEntity<Document> favouriteDocument(UUID documentId) {
        UUID userId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
        
        DocumentEntity documentEntity = documentService.findById(documentId);
        DocumentFavouriteEntity documentFavouriteEntity = new DocumentFavouriteEntity();
        documentFavouriteEntity.setDocument(documentEntity);
        documentFavouriteEntity.setUserId(userId);
        
        List<DocumentFavouriteEntity> favouriteList = documentEntity.getDocumentFavourites();
        if (favouriteList == null) {
            favouriteList = new ArrayList<>();
            documentEntity.setDocumentFavourites(favouriteList);
        }
        if (!favouriteList.contains(documentFavouriteEntity)) {
            favouriteList.add(documentFavouriteEntity);
            documentEntity = documentService.save(documentEntity);
        }
        Document res = databaseEntityToApiDataTranfserObjectMapperService.mapDocumentEntityToDocument(documentEntity);
        return new ResponseEntity<>(res, HttpStatus.OK);            
    }    
    
    @Override
    public ResponseEntity<Document> unFavouriteDocument(UUID documentId) {
        
        UUID userId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
        DocumentEntity documentEntity = documentService.findById(documentId);
        DocumentFavouriteEntity documentFavouriteEntity = new DocumentFavouriteEntity();
        documentFavouriteEntity.setDocument(documentEntity);
        documentFavouriteEntity.setUserId(userId);
        
        List<DocumentFavouriteEntity> favouriteList = documentEntity.getDocumentFavourites();
        if (favouriteList == null) {
            favouriteList = new ArrayList<>();
            documentEntity.setDocumentFavourites(favouriteList);
        }
        favouriteList.remove(documentFavouriteEntity);
     
        documentEntity = documentService.save(documentEntity);
        Document res = databaseEntityToApiDataTranfserObjectMapperService.mapDocumentEntityToDocument(documentEntity);
        return new ResponseEntity<>(res, HttpStatus.OK);            
    }       
    
    @Override
    public ResponseEntity<DocumentVersion> rollbackDocumentVersion(UUID documentId, UUID documentVersionId) {
        DocumentEntity documentEntity =  documentService.rollbackDocumentVersion(documentId, documentVersionId);
        
        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setChecksum(documentEntity.getLatestDocumentVersion().getChecksum());
        documentVersion
                .setCreatedDateTime(documentEntity.getLatestDocumentVersion().getCreatedDateTime().toOffsetDateTime());
        documentVersion.setId(documentEntity.getLatestDocumentVersion().getId());

        return new ResponseEntity<>(documentVersion, HttpStatus.OK);       

    }   

    @Override
    public ResponseEntity<Document> shareDocument(UUID documentId, List<SharedObject> sharedObject) {

        UUID userId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
        DocumentEntity documentEntity = documentService.findById(documentId);
        List<DocumentAcl> permissions = documentEntity.getDocumentPermissions();
        for (SharedObject so : sharedObject) {
                for (String p : so.getPermissions()) {
                    DocumentAcl documentAcl = new DocumentAcl();
                    documentAcl.setDocument(documentEntity);
                    documentAcl.setPermission(p);
                    documentAcl.setGrantedBy(userId);
                    documentAcl.setMayAssign(false);
                    if (Boolean.TRUE.equals(so.getTeamflag())) {
                        documentAcl.setTeamId(UUID.fromString(so.getUuid()));
                    } else {
                        documentAcl.setUserId(UUID.fromString(so.getUuid()));
                    }
                    permissions.add(documentAcl);
                    List<DocumentLifeTimeEntity> history = documentEntity.getDocumentHistory();
                    if (history == null) {
                        documentEntity.setDocumentHistory(new ArrayList<>());
                        history = documentEntity.getDocumentHistory();
                    }
                    DocumentLifeTimeEntity lt = new DocumentLifeTimeEntity();
                    lt.setPermission(documentAcl.getPermission());
                    lt.setGrantedBy(userId);
                    lt.setDocument(documentEntity);
                    if (Boolean.TRUE.equals(so.getTeamflag())) {
                        lt.setTeamId(UUID.fromString(so.getUuid()));
                    } else {
                        lt.setUserId(UUID.fromString(so.getUuid()));
                    }
                    lt.setGrantedOn(LocalDateTime.now());
                    history.add(lt);
                }
        }
        
        documentEntity = documentService.save(documentEntity);
        Document res = databaseEntityToApiDataTranfserObjectMapperService.mapDocumentEntityToDocument(documentEntity);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }   
    
    
    
    private List<Permission> getPagedList(List<Permission> original, Integer page, Integer size) {
      
        if (page ==null || size == null) { 
            return original;
        }
        
        List<Permission> newList = new ArrayList<>(original);
        int start = Math.min(original.size(), Math.abs(page * size));
        newList.subList(0, start).clear();
        
        int newSize = newList.size();                   
        int end = Math.min(size, newSize);        
        newList.subList(end, newSize).clear(); 
        return newList;
        
    }
    
    
    private ResponseEntity<List<Permission>> getDocumentAclForCurreentUser(UUID documentId,Integer page,Integer size) {
        DocumentEntity documentEntity = documentService.findById(documentId);
        List<String> myaccess = new ArrayList<>();
        UUID userId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
        documentEntity.getDocumentPermissions().forEach(e -> {
            if (userId.equals(e.getUserId())) {
                myaccess.add(e.getPermission());
            }
        });
        Permission permission = new Permission();
        permission.setUuid(userId.toString());
        permission.setPermissions(myaccess);
        List<Permission> res = new ArrayList<>();
        res.add(permission);
        return new ResponseEntity<>(getPagedList(res,page,size), HttpStatus.OK);
    }
    
    
    private ResponseEntity<List<Permission>> getDocumentAclForAllUsers(UUID documentId,Integer page,Integer size) {
        DocumentEntity documentEntity = documentService.findById(documentId);
        UUID userId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
        HashMap<UUID, List<String>> usersMap = new HashMap<>();
        documentEntity.getDocumentPermissions().forEach(e -> {
            if (e.getUserId() != null && !userId.equals(e.getUserId())) {
                if (usersMap.containsKey(e.getUserId())) {
                    List<String> p = usersMap.get(e.getUserId());
                    p.add(e.getPermission());
                    //usersMap.put(e.getUserId(), p);
                } else {
                    List<String> p = new ArrayList<>();
                    p.add(e.getPermission());
                    usersMap.put(e.getUserId(), p);
                }
            }
        });
        
        List<Permission> res = new ArrayList<>();
        
        for (UUID i : usersMap.keySet()) {
            Permission p = new Permission();
            p.setUuid(i.toString());
            p.setPermissions(usersMap.get(i));
            res.add(p);
        }
        return new ResponseEntity<>(getPagedList(res,page,size), HttpStatus.OK);
    }

    private ResponseEntity<List<Permission>> getDocumentAclForTeams(UUID documentId,Integer page,Integer size) {
        DocumentEntity documentEntity = documentService.findById(documentId);
        HashMap<UUID, List<String>> teamsMap = new HashMap<>();
        documentEntity.getDocumentPermissions().forEach(e -> {
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
            }
        });
        List<Permission> res = new ArrayList<>();
        for (UUID i : teamsMap.keySet()) {
            Permission p = new Permission();
            p.setUuid(i.toString());
            p.setPermissions(teamsMap.get(i));
            res.add(p);
        }
        return new ResponseEntity<>(getPagedList(res,page,size), HttpStatus.OK);
    }
    
    
    @Override
    public ResponseEntity<List<Permission>> getDocumentAcl(UUID documentId,Integer page,Integer size,String scope) {

        if (scope == null || "current".equals(scope) ) {
            return getDocumentAclForCurreentUser(documentId,  page, size);
        } else if ("users".equals(scope)) {
            return getDocumentAclForAllUsers(documentId,  page, size);
        } else if ("teams".equals(scope)) {
            return getDocumentAclForTeams(documentId,  page, size);
        } else  {
            throw new InvalidRequestException("Invalid scope attribute.", null);
        }
    }

    
    private void removePermissionForTeam(DocumentEntity documentEntity, List<DocumentAcl> permissions, String permission, UUID uuid) {

        List<DocumentAcl> removedList = new ArrayList<>();
        for (DocumentAcl acl : permissions) {
            if (acl.getPermission() != null && acl.getPermission().equals(permission)  && acl.getTeamId() != null && acl.getTeamId().equals(uuid))  {
                removedList.add(acl);
                List<DocumentLifeTimeEntity> history = documentEntity.getDocumentHistory();
                for (DocumentLifeTimeEntity lt : history) {
                    if (uuid.equals(lt.getTeamId()) && lt.getPermission().equals(permission) && (lt.getRevokedBy()==null || "".equals(lt.getRevokedBy().toString()))) {
                        lt.setRevokedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));
                        lt.setRevokedOn(LocalDateTime.now());
                    }
                    
                }
            }
        }
        permissions.removeAll(removedList);
    }
    
    
    private void removePermissionForUser(DocumentEntity documentEntity, List<DocumentAcl> permissions, String permission, UUID uuid) {

        List<DocumentAcl> removedList = new ArrayList<>();
        List<UUID> users = new ArrayList<>();

        
        for (DocumentAcl acl : permissions) {
            if (acl.getUserId() != null && acl.getUserId().equals(uuid) &&  acl.getPermission() != null && acl.getPermission().equals(permission) ) {
                removedList.add(acl);
                List<DocumentLifeTimeEntity> history = documentEntity.getDocumentHistory();
                for (DocumentLifeTimeEntity lt : history) {
                    if (uuid.equals(lt.getUserId()) && lt.getPermission().equals(permission) && (lt.getRevokedBy()==null|| "".equals(lt.getRevokedBy().toString()))) {
                        lt.setRevokedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));
                        lt.setRevokedOn(LocalDateTime.now());
                    }
                }                
                users.add(acl.getUserId());
            }
        }
        permissions.removeAll(removedList);
        for (UUID userId : users) {
            removePermissionForGrantedBy(documentEntity, permissions, permission, userId);
        }
        
    }

    // recursive "un-share" permission for grantedBy user
    private void removePermissionForGrantedBy(DocumentEntity documentEntity, List<DocumentAcl> permissions, String permission, UUID uuid) {

        List<DocumentAcl> removedList = new ArrayList<>();
        List<UUID> users = new ArrayList<>();
        
        for (DocumentAcl acl : permissions) {
            if (acl.getGrantedBy() != null && acl.getGrantedBy().equals(uuid) &&  acl.getPermission() != null && acl.getPermission().equals(permission) ) {
                removedList.add(acl);
                List<DocumentLifeTimeEntity> history = documentEntity.getDocumentHistory();
                for (DocumentLifeTimeEntity lt : history) {
                    if (uuid.equals(acl.getGrantedBy()) && lt.getPermission().equals(permission) && (lt.getRevokedBy()==null|| "".equals(lt.getRevokedBy().toString()))) {
                        lt.setRevokedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));
                        lt.setRevokedOn(LocalDateTime.now());
                    }
                }                  
                users.add(acl.getUserId());
            }
        }
        permissions.removeAll(removedList);
        for (UUID userId : users) {
            removePermissionForGrantedBy(documentEntity, permissions, permission, userId);
        }
    }
    
    
    @Override
    public ResponseEntity<Document> unShareDocument(UUID documentId,List<SharedObject> sharedObject) {

        DocumentEntity documentEntity = documentService.findById(documentId);
        List<DocumentAcl> permissions = documentEntity.getDocumentPermissions();
        for (SharedObject so : sharedObject) {
                for (String p : so.getPermissions()) {
                    if (Boolean.TRUE.equals(so.getTeamflag())) {
                        removePermissionForTeam(documentEntity, permissions, p, UUID.fromString(so.getUuid())); 
                    } else {
                        removePermissionForUser(documentEntity, permissions, p, UUID.fromString(so.getUuid())); 
                    }
                }
        }
        documentEntity = documentService.save(documentEntity);
        Document res = databaseEntityToApiDataTranfserObjectMapperService.mapDocumentEntityToDocument(documentEntity);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    
    @Override
    public ResponseEntity<List<LifeTimeObject>> getUserDocumentLifeTime(UUID documentId,UUID userOrTeamId) {
        DocumentEntity documentEntity = documentService.findById(documentId);
        List<LifeTimeObject> lts = new ArrayList<>();
        List<DocumentLifeTimeEntity> history = documentEntity.getDocumentHistory();
        if (history == null) {
            return new ResponseEntity<>(lts, HttpStatus.OK);
        }
        List<DocumentLifeTimeEntity> filteredHistory = new ArrayList<>();
        for (DocumentLifeTimeEntity h: history) {
            if (userOrTeamId.equals(h.getUserId()) || userOrTeamId.equals(h.getTeamId())) {
                filteredHistory.add(h);
            }
        }
        HashMap<String, List<DateTimePeriod>> map = new HashMap<>();
        filteredHistory.forEach(e -> {
            
                ZoneOffset offset = OffsetDateTime.now().getOffset();
                DateTimePeriod period = new DateTimePeriod();
                period.setGrantedBy(e.getGrantedBy());
                period.setGrantedDateTime(e.getGrantedOn().atOffset(offset));
                period.setRevokedBy(e.getRevokedBy());
                if (e.getRevokedOn() != null) {
                     period.setRevokedDateTime(e.getRevokedOn().atOffset(offset));
                }
                
                if (map.containsKey(e.getPermission())) {
                    List<DateTimePeriod> p = map.get(e.getPermission());
                    p.add(period);
                } else {
                    List<DateTimePeriod> p = new ArrayList<>();
                    p.add(period);
                    map.put(e.getPermission(), p);
                }
        });
        
        for (String permission : map.keySet()) {
            LifeTimeObject lo = new LifeTimeObject();
            lo.setPermissionName(permission);
            lo.setPeriods(map.get(permission));
            lts.add(lo);
        }
        return new ResponseEntity<>(lts, HttpStatus.OK);
        
    }
    
    @Override
    public ResponseEntity<LifeTimeUsersList> getDocumentLifeTimeUserList(UUID documentId) {
        DocumentEntity documentEntity = documentService.findById(documentId);
        List<DocumentLifeTimeEntity> history = documentEntity.getDocumentHistory();
        Set<UUID> users = new HashSet<>();
        Set<UUID> teams = new HashSet<>();
        history.forEach(e -> {
            if (e.getTeamId()!=null)  {
                teams.add(e.getTeamId());
            } else  if (e.getUserId()!=null)  {
                users.add(e.getUserId());
            }            
        });
        LifeTimeUsersList ltul = new LifeTimeUsersList();
        ltul.setTeams(new ArrayList<>());
        ltul.setUsers(new ArrayList<>());
        users.forEach(e-> ltul.getUsers().add(e.toString()));
        teams.forEach(e-> ltul.getTeams().add(e.toString()));  
        
        return new ResponseEntity<>(ltul, HttpStatus.OK);
    }
    
    
}




