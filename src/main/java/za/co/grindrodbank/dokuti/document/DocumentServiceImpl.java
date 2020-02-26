/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import za.co.grindrodbank.dokuti.documenttag.DocumentTagRepository;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionService;
import za.co.grindrodbank.dokuti.exceptions.ChecksumFailedException;
import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.NotAuthorisedException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.DocumentDataStoreService;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.StorageFileNotFoundException;
import za.co.grindrodbank.dokuti.service.resourcepermissions.DocumentPermission;
import za.co.grindrodbank.dokuti.service.resourcepermissions.ResourcePermissionsService;
import za.co.grindrodbank.security.service.accesstokenpermissions.SecurityContextUtility;


@Service
public class DocumentServiceImpl implements DocumentService {

	Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private DocumentVersionService documentVersionService;
	@Autowired
	private DocumentDataStoreService documentStoreService;
	@Autowired
	private ResourcePermissionsService resourcePermissions;
	@Autowired
	private DocumentTagRepository documentTagRepository;

	public DocumentEntity findById(UUID id) throws ResourceNotFoundException, NotAuthorisedException {
		// check that the accessing user owns the document.
		Optional<DocumentEntity> optionalDocument = documentRepository.findById(id);

		if (!optionalDocument.isPresent()) {
			throw new ResourceNotFoundException("Document not found", null);
		}

		DocumentEntity document = optionalDocument.get();

		if (!resourcePermissions.accessingUserCanReadDocument(document)) {
			throw new NotAuthorisedException("Accessing user does not have permissions to read the document", null);
		}

		return document;
	}

    public DocumentEntity createNewDocument(MultipartFile file, String description) throws DatabaseLayerException {
        DocumentEntity document = new DocumentEntity();
        document.setContentType(file.getContentType());
        document.setDescription(description);
        document.setName(StringUtils.cleanPath(file.getOriginalFilename()));
        document.setUpdatedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));

        try {
            document = documentRepository.save(document);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DatabaseLayerException("Error creating new document", e);
        }

        createDocumentVersionWithFile(file, document);
        document = addDocumentCreatorPermissionsToDocument(document);

        return document;
    }

	public Resource getLatestDocumentData(DocumentEntity document)
			throws ResourceNotFoundException, NotAuthorisedException, ChecksumFailedException {
		return this.getDocumentDataForVersion(document, document.getLatestDocumentVersion());
	}

	public Resource getDocumentDataForVersion(DocumentEntity document, DocumentVersionEntity documentVersion)
			throws ResourceNotFoundException, NotAuthorisedException, ChecksumFailedException {
		if (!resourcePermissions.accessingUserCanReadDocument(document)) {
			throw new NotAuthorisedException("Accessing user does not have permissions to read the document", null);
		}

		try {
			// TODO: Decouple this from being being a file resource, as this is file system
			// specific. The file stream should be sent back here,
			// which is a more generic representation of the file content.
			Resource fileContent = documentStoreService.loadAsResource(documentVersion.getChecksum());
			checkIfFileContentChecksumMatches(documentVersion, fileContent);
			return fileContent;
		} catch (StorageFileNotFoundException e) {
			logger.error(e.getMessage());
			throw new ResourceNotFoundException("Error reading document data.", e);
		}
	}

	public void checkIfFileContentChecksumMatches(DocumentVersionEntity documentVersion, Resource fileContent)
			throws ChecksumFailedException {
		try {
			File file = fileContent.getFile();
			byte[] fileContentByteArray = Files.readAllBytes(file.toPath());
			String checkSumAlgo = documentVersion.getChecksumAlgo();
			if (StringUtils.isEmpty(checkSumAlgo)) {
			    checkSumAlgo = "MD5"; // to support old checksums
			}
			String calculatedFileContentChecksum = getFileContentChecksum(fileContentByteArray, checkSumAlgo);

			logger.debug("Calculated File Content Checksum: {} : FileVersionMetaData checksum: {}",
					calculatedFileContentChecksum, documentVersion.getChecksum());

			if (!calculatedFileContentChecksum.equals(documentVersion.getChecksum())) {
				throw new ChecksumFailedException("File content checksum does not match checksum meta-data.", null);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new ChecksumFailedException("Unable to calculate the checksums for the retrieved content.", null);
		}
	}

	public DocumentEntity updateDocument(Optional<MultipartFile> file, DocumentEntity document)
			throws NotAuthorisedException, DatabaseLayerException {
		if (!resourcePermissions.accessingUserCanWriteDocument(document)) {
			throw new NotAuthorisedException("Accessing user does not have permissions to write the document", null);
		}

		if (file.isPresent()) {
			createDocumentVersionWithFile(file.get(), document);

			String latestFileName = StringUtils.cleanPath(file.get().getOriginalFilename());

			if (!latestFileName.equals(document.getName())) {
				// The new filename is not the same as the previous one and needs to be updated.
				document.setName(latestFileName);
			}
		}

		return save(document);
	}

	@Override
    public DocumentEntity rollbackDocumentVersion(UUID documentId, UUID documentVersionId)
         throws ResourceNotFoundException, NotAuthorisedException, ChecksumFailedException,  DatabaseLayerException  {

        DocumentEntity documentEntity = findById(documentId);
        if (!resourcePermissions.accessingUserCanWriteDocument(documentEntity)) {
            throw new NotAuthorisedException("Accessing user does not have permissions to write the document", null);
        }        
        DocumentVersionEntity documentVersionEntity = documentVersionService.findById(documentVersionId);
        
        String checksum = documentVersionEntity.getChecksum();
        String checksumAlgo = documentVersionEntity.getChecksumAlgo();
        InputStream is;
        try {
            is = getDocumentDataForVersion(documentVersionEntity.getDocument(), documentVersionEntity).getInputStream();
            createDocumentVersionWithInputStream(is, checksum, checksumAlgo, documentEntity);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException("Error reading document data.", e);
        }
        return save(documentEntity);
    }	
	
	public DocumentEntity save(DocumentEntity document) throws DatabaseLayerException {
	    
    
	    try {
			return documentRepository.save(document);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DatabaseLayerException("Error saving document.", e);
		}
	}

	/**
	 * Create a new document version record for a given document by storing the
	 * latest file version and creating the required database records for the new
	 * version.
	 * 
	 * @param file     The Multi-part representation of the file to create a new
	 *                 versions for
	 * @param document An instance of the document to create the new version for.
	 * @return The new document version that was created for the document.
	 */
	private DocumentVersionEntity createDocumentVersionWithFile(MultipartFile file, DocumentEntity document) {
	    String defaultChecksumAlgo = "SHA-256";
	    
		String fileContentChecksum = "";
		String documentType ="";

		try {
			fileContentChecksum = getFileContentChecksum(file.getBytes(), defaultChecksumAlgo);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		Tika tika = new Tika();
		try {
		    documentType = tika.detect(file.getInputStream());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException("Error reading document data.", e);
        }
		
		DocumentVersionEntity newDocumentVersion = documentVersionService.createDocumentVersion(document,
				fileContentChecksum, defaultChecksumAlgo, documentType);
		documentStoreService.store(file, newDocumentVersion.getChecksum());

		return newDocumentVersion;
	}

    private DocumentVersionEntity createDocumentVersionWithInputStream(InputStream is, String fileContentChecksum, String checksumAlgo, DocumentEntity document) {
        String documentType ="";
        Tika tika = new Tika();
        try {
            documentType = tika.detect(is);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException("Error reading document data.", e);
        }
        
        DocumentVersionEntity newDocumentVersion = documentVersionService.createDocumentVersion(document, fileContentChecksum, checksumAlgo, documentType);
        documentStoreService.store(is, newDocumentVersion.getChecksum());

        return newDocumentVersion;
    }
	
	@Override
	public String getFileContentChecksum(byte[] fileByteArray, String checksumAlgo) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance(checksumAlgo);
			byte[] messageDigestHash = messageDigest.digest(fileByteArray);
			StringBuilder messageDigestHashString = new StringBuilder();

			for (int i = 0; i < messageDigestHash.length; i++) {
				messageDigestHashString.append( Integer.toString((messageDigestHash[i] & 0xff) + 0x100, 16).substring(1));
			}

			return messageDigestHashString.toString();
		} catch (Exception e) {
			logger.error(e.getMessage());

			return null;
		}
	}

	@Override
	public Page<DocumentEntity> findAll(Pageable pageable, String documentName, Boolean filterByFavourites, List<String> tags,
			List<String> attributeNames, Boolean filterArchive, Boolean filterSharedWithOthers, Boolean filterSharedWithMe) {
		// The documents list must only contain documents where the accessing user has a
		// read permission. Make consideration for potential filter name here too.
		Specification<DocumentEntity> specification = Specification
				.where(DocumentEntitySpecifications.documentEntitiesWithName(documentName).and(
						DocumentEntitySpecifications.documentEntitiesWhereUserHasPermission(DocumentPermission.READ,
								UUID.fromString(SecurityContextUtility.getUserIdFromJwt()))));

		// Only find documents where ALL (and operator) filter tags are present.
		for (int i = 0; i < tags.size(); i++) {
			specification = Specification
					.where(DocumentEntitySpecifications.documentEntitiesWithTag(tags.get(i)).and(specification));
		}

		// Only find documents where ALL (and operator) filterAttribute names are
		// present.
		for (int i = 0; i < attributeNames.size(); i++) {
			specification = Specification.where(DocumentEntitySpecifications
					.documentEntitiesWithAttributeName(attributeNames.get(i)).and(specification));
		}
		
		// Only find archive or unarchived documents
		specification = Specification.where(DocumentEntitySpecifications
                .documentEntitiesWithArchiveFilter(filterArchive).and(specification));
		
		// Only list favorited documents if filterFavouriteUser provided
        specification = Specification.where(DocumentEntitySpecifications
                .documentEntitiesWithFavouriteUserFilter(filterByFavourites).and(specification));
        
        // Only list shared with me documents
        specification = Specification.where(DocumentEntitySpecifications
                .documentEntitiesWithSharedWithMeFilter(filterSharedWithMe).and(specification));

        //Only list shared with others documents    
        specification = Specification.where(DocumentEntitySpecifications
                .documentEntitiesWithSharedWithOthersFilter(filterSharedWithOthers).and(specification));
        
		
		try {
			return documentRepository.findAll(specification, pageable);
		} catch (Exception e) {
			logger.error(e.getMessage());

			throw new DatabaseLayerException("Error getting list of documents.", e);
		}

	}


	public void removeAllDocumentTags(DocumentEntity document) {
		try {
			documentTagRepository.deleteAllDocumentTags(document.getId());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DatabaseLayerException("Error removing all tags from a document.", e);
		}

	}

	/**
	 * Creates and adds all the permissions that a document creator gets to the
	 * document.
	 * 
	 * @param document The document to add all the creator permissions to.
	 * @return An updated instance of the document that the permissions were added
	 *         to.
	 * @throws DatabaseLayerException
	 */
	private DocumentEntity addDocumentCreatorPermissionsToDocument(DocumentEntity document)
			throws DatabaseLayerException {
		UUID creatorUserId = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
		logger.debug("Obtained the following UUID from the security context to use as the document creator: {}",
				creatorUserId);
		// Create a list of all the possible DocumentPermissions ENUM values, as we want
		// to assign all the available permissions to to the document creator.
		List<DocumentPermission> allDocumentPermissions = new ArrayList<>(
				EnumSet.allOf(DocumentPermission.class));

		try {
			allDocumentPermissions.forEach(permissionValue -> {
				logger.debug("Adding the {} permission to Document {} for user {}", permissionValue,
						document.getId(), creatorUserId);
				document.addPermission(creatorUserId, permissionValue, true, creatorUserId);
			});

			documentRepository.save(document);

			return document;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DatabaseLayerException("Error saving document after assinging document ACL permissions.", e);
		}
	}
}


