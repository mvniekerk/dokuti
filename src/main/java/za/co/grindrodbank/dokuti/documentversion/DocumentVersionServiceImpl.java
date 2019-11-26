/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentversion;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.NotAuthorisedException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;
import za.co.grindrodbank.dokuti.service.resourcepermissions.ResourcePermissionsService;
import za.co.grindrodbank.security.service.accesstokenpermissions.SecurityContextUtility;

@Service
public class DocumentVersionServiceImpl implements DocumentVersionService {

	Logger logger = LoggerFactory.getLogger(DocumentVersionServiceImpl.class);
	@Autowired
	private DocumentVersionRepository documentVersionRepository;
	@Autowired
	ResourcePermissionsService resourcePermissionsService;

	public DocumentVersionEntity findById(UUID id) throws ResourceNotFoundException, NotAuthorisedException {
		Optional<DocumentVersionEntity> optionalDocumentVersion = documentVersionRepository.findById(id);

		if (!optionalDocumentVersion.isPresent()) {
			logger.warn("Document Version with ID {} not found.", id);

			throw new ResourceNotFoundException("Document Version not found.", null);
		}

		DocumentVersionEntity documentVersion = optionalDocumentVersion.get();
		resourcePermissionsService.accessingUserCanReadDocument(documentVersion.getDocument());

		return documentVersion;
	}

	public DocumentVersionEntity save(DocumentVersionEntity documentVersion) {

		try {
			documentVersion.setUploadedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));

			return documentVersionRepository.save(documentVersion);
		} catch (Exception e) {
			logger.error("Error saving document version. Exception: {}", e.getMessage());

			throw new DatabaseLayerException("Error saving document version.", e);
		}
	}

	public DocumentVersionEntity createDocumentVersion(DocumentEntity document, String checksum, String documentType) {
		DocumentVersionEntity documentVersion = new DocumentVersionEntity();
		documentVersion.setDocument(document);
		documentVersion.setChecksum(checksum);
		documentVersion.setDocumentType(documentType);

		try {
			documentVersion = save(documentVersion);

			return documentVersion;
		} catch (Exception e) {
			logger.error("Error creating new document version for Document {}. Error: {}", document.getId(),
					e.getMessage());

			throw new DatabaseLayerException("Error creating new document version for Document.", e);
		}

	}

}
