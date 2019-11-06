/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionEntity;
import za.co.grindrodbank.dokuti.exceptions.ChecksumFailedException;
import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.NotAuthorisedException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;

public interface DocumentService {

	/**
	 * Gets a document's meta data by it's primary UUID.
	 * 
	 * @param id The UUID of the document.
	 * @return An instance of the document on success. ResourceNotFoundException is
	 *         thrown if resource not found.
	 * @throws ResourceNotFoundException
	 * @throws NotAuthorisedException
	 */
	public DocumentEntity findById(UUID id) throws ResourceNotFoundException, NotAuthorisedException;

	/**
	 * Creates a new document given the source file. The document meta-data is
	 * created and the file is saved to the configured storage back end.
	 * 
	 * @param file        The raw file data pertaining to the document.
	 * @param description The description of the file that is to be added to the
	 *                    meta-data.
	 * @return An instance of the Document meta data object that was created.
	 * @throws DatabaseLayerException
	 */
	public DocumentEntity createNewDocument(MultipartFile file, String description) throws DatabaseLayerException;

	/**
	 * Gets the actual file data for the latest version of the document.
	 * 
	 * @param document The document meta data that the file data is to be fetched
	 *                 for.
	 * @return The raw file data as a Resource object.
	 * @throws ResourceNotFoundException
	 * @throws NotAuthorisedException
	 * @throws ChecksumFailedException
	 */
	public Resource getLatestDocumentData(DocumentEntity document)
			throws ResourceNotFoundException, NotAuthorisedException, ChecksumFailedException;

	/**
	 * Updates a document.
	 * 
	 * @param file     An optional file. If there is not file in the optional, no
	 *                 file content updates are done.
	 * @param document The updated document meta-data object that is to be persisted
	 *                 to the database.
	 * @return The updated latest version of the document meta-data.
	 * @throws NotAuthorisedException
	 * @throws DatabaseLayerException
	 */
	public DocumentEntity updateDocument(Optional<MultipartFile> file, DocumentEntity document)
			throws NotAuthorisedException, DatabaseLayerException;

	/**
	 * Gets the document data for a particular version as a resource.
	 * 
	 * @param document        The document to get the data for.
	 * @param documentVersion The Document versions to obtain the data for.
	 * @return A resource representation of the file data.
	 * @throws ResourceNotFoundException
	 * @throws NotAuthorisedException
	 * @throws ChecksumFailedException
	 */
	public Resource getDocumentDataForVersion(DocumentEntity document, DocumentVersionEntity documentVersion)
			throws ResourceNotFoundException, NotAuthorisedException, ChecksumFailedException;

	/**
	 * Saves the state of a document by either creating a new one if one does not
	 * already exist, or updating the existing record.
	 * 
	 * @param document An updated instance of the document to persist.
	 * @return The updated instance of the document if the persistence was
	 *         successful.
	 * @throws DatabaseLayerException
	 */
	public DocumentEntity save(DocumentEntity document) throws DatabaseLayerException;

	/**
	 * Calculates the MD5 checksum for the contents of a file.
	 * 
	 * @param file The byte array of the file to calculate the checksum for.
	 * @return The calculated checksum on success, null if there was an error
	 *         performing the calculation.
	 */
	public String getFileContentChecksum(byte[] fileByteArray);

	/**
	 * Checks whether the calculated checksum for the content of a given file
	 * matches the document version meta data checksum.
	 * 
	 * @param documentVersion The document version to compare against.
	 * @param fileContent     The file content as a File Resource.
	 * @throws ChecksumFailedException
	 */
	public void checkIfFileContentChecksumMatches(DocumentVersionEntity documentVersion, Resource fileContent)
			throws ChecksumFailedException;

	/**
	 * Find lists of documents, which are filtered by the optionally supplied
	 * filters. It is safe to pass empty or null values for the various filters.
	 * 
	 * @param pageable         An instance of the page.
	 * @param documentName     The name of the document to filter by.
	 * @param filterFavouriteUser  The UUID of user. When param given only favorite documents will be shown.
	 * @param tags             A list of tags that documents will be filtered by.
	 *                         All the tags need to assigned to the document (And
	 *                         operator).
	 * @param attributeNames   A list of assigned attribute names the documents will
	 *                         be filtered by. All the attributes need to be
	 *                         assigned to the document (And operator).
	 * @param filterArchive    A archive filter that documents will be filtered by.                         
	 *                         

	 * @return A Pageable list of documents that match all the supplied filters.
	 * @throws DatabaseLayerException
	 */
	public Page<DocumentEntity> findAll(Pageable pageable, String documentName, Boolean filterByFavourites, List<String> tags,
			List<String> attributeNames, Boolean filterArchive);

	/**
	 * Adds an attribute association to a document with a value.
	 * 
	 * @param document  The document to associate the document with.
	 * @param attribute The attribute to associate with the document.
	 * @param value     The value of the attribute to associate with the document.
	 * @return An instance of the created DocumentAttribute.
	 * @throws DatabaseLayerException
	 */
	public DocumentAttributeEntity addDocumentAttribute(DocumentEntity document, AttributeEntity attribute,
			String value) throws DatabaseLayerException;

	/**
	 * Removes a document attribute association from the document.
	 * 
	 * @param document  The document to remove the attribute association from.
	 * @param attribute The attribute to remove from the document.
	 * @throws DatabaseLayerException
	 */
	public void removeDocumentAttribute(DocumentEntity document, AttributeEntity attribute)
			throws DatabaseLayerException;

	public void removeAllDocumentTags(DocumentEntity document);
}
