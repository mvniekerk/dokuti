/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documenttag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;

public interface DocumentTagService {

	/**
	 * Finds a document tag using a composite document tag ID object as the key.
	 * 
	 * @param documentTagId An instance of the documentTagId, which is the composite
	 *                      key for a tag.
	 * @return An instance of the Document Tag.
	 */
	public Optional<DocumentTagEntity> findById(DocumentTagId documentTagId);

	/**
	 * Persists the state of a document tag to the database. Creates the record if
	 * it does not exist.
	 * 
	 * @param documentTag The document tag to persist.
	 * @return The document Tag (with ID if created).
	 */
	public DocumentTagEntity save(DocumentTagEntity documentTag);

	/**
	 * Creates all the required tag records for a document from an array of tag
	 * strings.
	 * 
	 * @param tags     An array of tag strings to create tag records from.
	 * @param document The document to create the tags for.
	 */
	public void createDocumentTags(List<String> tags, DocumentEntity document);

	/**
	 * Flattens the the composite ID formatted DocumentTag set into a simple array
	 * of all it's tags.
	 * 
	 * @param documentTags The set of DocumentTags to flatten.
	 * @return An array of document Tag strings pertaining to the document.
	 */
	public String[] getFlattenedTagsArray(Set<DocumentTagEntity> documentTags);

	/**
	 * Finds a distinct list of all the tags that are assigned to documents.
	 * 
	 * @param pageRequest An instance of the page request to perform paging with.
	 * @return A list of distinct tags assigned to documents.
	 * @throws DatabaseLayerException
	 */
	public Page<String> findAllDistinctTags(Pageable pageRequest) throws DatabaseLayerException;
}
