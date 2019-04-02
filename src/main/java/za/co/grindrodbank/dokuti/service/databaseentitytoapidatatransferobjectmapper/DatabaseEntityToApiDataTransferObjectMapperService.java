/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.service.databaseentitytoapidatatransferobjectmapper;

import org.openapitools.model.Attribute;
import org.openapitools.model.Document;
import org.openapitools.model.DocumentAttribute;
import org.openapitools.model.Group;
import org.openapitools.model.LookupTag;
import org.springframework.data.domain.Page;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.dokuti.group.GroupEntity;

public interface DatabaseEntityToApiDataTransferObjectMapperService {

	/**
	 * Maps a Document Database Entity onto the open API Tools generated DTO.
	 * 
	 * @param entity The Document Entity to map onto the DTO.
	 * @return An instance of the open API tools generated Document DTO, with all
	 *         the fields and relations mapped from the Database Document Entity.
	 */
	public Document mapDocumentEntityToDocument(DocumentEntity entity);

	/**
	 * Maps a Page of Database Document Entities onto a Page of Open API tools
	 * generated Document DTOs.
	 * 
	 * @param entities A page of database document entities.
	 * @return A Page of Open API generated Document DTOs.
	 */
	public Page<Document> mapDocumentEntityPageToDocumentPage(Page<DocumentEntity> entities);

	/**
	 * Maps a database Document Attribute Entity onto the Open API tools generated
	 * DTO for a document attribute.
	 * 
	 * @param documentAttributeEntity The database document attribute entity to map
	 *                                onto the DTO.
	 * @return An instance of the populated Document Attribute DTO.
	 */
	public DocumentAttribute mapDocumentAttributeEntityToDocumentAttribute(
			DocumentAttributeEntity documentAttributeEntity);

	public LookupTag mapDocumentTagEntityToLookupTag(String tag);

	public Page<LookupTag> mapDocumentTagsPageToLookupTagPage(Page<String> tags);

	public Group mapGroupEntityToGroup(GroupEntity entity);

	public Page<Group> mapGroupEntitiesPageToGroupPage(Page<GroupEntity> entities);

	public Attribute mapAttributeEntityToAttribute(AttributeEntity entity);

	public Page<Attribute> mapAttributeEntityPageToAttributePage(Page<AttributeEntity> entities);

}
