/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentversion;

import java.util.UUID;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.NotAuthorisedException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;

public interface DocumentVersionService {

	/**
	 * Finds a document version by it's ID.
	 * 
	 * @param id The ID of the document version to find.
	 * @return An instance of the document version.
	 */
	public DocumentVersionEntity findById(UUID id) throws ResourceNotFoundException, NotAuthorisedException;

	/**
	 * Saves the current state of a document version entity. Creates a new one if it
	 * did not exist.
	 * 
	 * @param documentVersion The instance of the document version to persist.
	 * @return An instance of the persisted document version entity.
	 */
	public DocumentVersionEntity save(DocumentVersionEntity documentVersion);

	/**
	 * Creates a new document version record for a given Document.
	 * 
	 * @param document The document to create the new version for.
	 * @return An instance of the newly created document version.
	 */
	public DocumentVersionEntity createDocumentVersion(DocumentEntity document, String checksum, String checksumAlgo, String documentType);

	
    /**
     * Adds an attribute association to a document version with a value.
     * 
     * @param documentVersion  The documentVersion to associate the document with.
     * @param attribute The attribute to associate with the document.
     * @param value     The value of the attribute to associate with the document.
     * @return An instance of the created DocumentAttribute.
     * @throws DatabaseLayerException
     */
    public DocumentAttributeEntity addDocumentAttribute(DocumentVersionEntity documentVersion, AttributeEntity attribute,
            String value) throws DatabaseLayerException;
    
    
    /**
     * Removes a document attribute association from the document version.
     * 
     * @param documentVersion  The documentVersion to remove the attribute association from.
     * @param attribute The attribute to remove from the document.
     * @throws DatabaseLayerException
     */
    public void removeDocumentAttribute(DocumentVersionEntity documentVersion, AttributeEntity attribute)
            throws DatabaseLayerException;
    
    
}
