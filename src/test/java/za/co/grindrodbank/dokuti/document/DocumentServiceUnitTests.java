/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import static org.junit.Assert.assertEquals;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.document.DocumentRepository;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionService;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.DocumentDataStoreService;
import za.co.grindrodbank.dokuti.service.resourcepermissions.ResourcePermissionsService;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceUnitTests {

	@Mock
	private DocumentRepository documentRepository;
	
	@Mock
	private DocumentVersionService documentVersionService;

	@Mock
	private DocumentDataStoreService DocumentDataStoreService;
	
	@Mock
	private ResourcePermissionsService resourcePermission;

	@InjectMocks
	private DocumentServiceImpl documentService;

	@Test
	public void givenDocument_whenGetDocument_thenReturnDocument() throws Exception {

		UUID documentId = UUID.randomUUID();

		DocumentEntity document = new DocumentEntity();
		document.setId(documentId);
		document.setContentType("text/plain");
		document.setDescription("Mocked document description");
		document.setName("Mocked document name");
		
		Optional<DocumentEntity> optionalDocument = Optional.of(document);

		Mockito.when(documentRepository.findById(documentId)).thenReturn(optionalDocument);
		Mockito.when(resourcePermission.accessingUserCanReadDocument(document)).thenReturn(true);

		DocumentEntity searchedDocument = documentService.findById(documentId);
		assertEquals("Failure - Document names are not equal", searchedDocument.getName(), document.getName());
	}

}
