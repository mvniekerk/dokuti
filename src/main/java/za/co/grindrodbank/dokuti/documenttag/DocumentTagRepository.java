/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documenttag;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DocumentTagRepository extends PagingAndSortingRepository<DocumentTagEntity, DocumentTagId> {

	@Query(value = "SELECT DISTINCT tag FROM _documents.document_tag", nativeQuery = true)
	public Page<String> findAllDistinctTags(Pageable pageable);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM _documents.document_tag AS t WHERE t.document_id = ?1", nativeQuery = true)
	public void deleteAllDocumentTags(UUID documentId);
}
