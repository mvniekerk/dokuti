/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documenttag;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class DocumentTagServiceImpl implements DocumentTagService {

	@Autowired
	private DocumentTagRepository documentTagRepository;

	Logger logger = LoggerFactory.getLogger(DocumentTagServiceImpl.class);

	public Optional<DocumentTagEntity> findById(DocumentTagId documentTagId) {
		return documentTagRepository.findById(documentTagId);
	}

	public DocumentTagEntity save(DocumentTagEntity documentTag) {
		return documentTagRepository.save(documentTag);
	}

	/**
	 * Flattens the the composite ID formatted DocumentTag set into a simple array
	 * of all it's tags.
	 * 
	 * @param documentTags The set of DocumentTags to flatten.
	 * @return An array of document Tag strings pertaining to the document.
	 */
	public String[] getFlattenedTagsArray(Set<DocumentTagEntity> documentTags) {

		String[] documentTagsArray = new String[documentTags.size()];
		int count = 0;
		Iterator<DocumentTagEntity> iter = documentTags.iterator();

		while (iter.hasNext()) {
			DocumentTagEntity documentTag = iter.next();
			documentTagsArray[count] = documentTag.getId().getTag();
			count++;
		}

		return documentTagsArray;
	}

	/**
	 * Creates all the required tag records for a document from an array of tag
	 * strings.
	 * 
	 * @param tags     An array of tag strings to create tag records from.
	 * @param document The document to create the tags for.
	 */
	public void createDocumentTags(List<String> tags, DocumentEntity document) {
		tags.forEach(tag -> {
			DocumentTagEntity documentTag = new DocumentTagEntity();
			DocumentTagId documentTagId = new DocumentTagId();
			documentTagId.setDocumentId(document.getId());
			documentTagId.setTag(tag);
			documentTag.setId(documentTagId);
			documentTag.setDocument(document);
			this.save(documentTag);
		});
	}

	public Page<String> findAllDistinctTags(Pageable pageRequest) throws DatabaseLayerException {
		try {
			return documentTagRepository.findAllDistinctTags(pageRequest);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DatabaseLayerException("Error obtaining distinct list of document tags.", e);
		}
	}

}
