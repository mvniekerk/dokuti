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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.document.DocumentRepository;
import za.co.grindrodbank.dokuti.document.DocumentService;
import za.co.grindrodbank.dokuti.document.DocumentServiceImpl;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionService;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.DocumentDataStoreService;
import za.co.grindrodbank.dokuti.service.resourcepermissions.ResourcePermissionsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentServiceUnitTests {

	@MockBean
	private DocumentRepository documentRepository;
	
	@MockBean
	private DocumentVersionService documentVersionService;

	@MockBean
	private DocumentDataStoreService DocumentDataStoreService;
	
	@MockBean 
	private ResourcePermissionsService resourcePermission;

	@TestConfiguration
	static class DocumentServiceTestContextConfiguration {

		@Bean
		public DocumentService documentService() {
			return new DocumentServiceImpl();
		}
	}

	@Autowired
	private DocumentService documentService;

	@Test
	public void givenDocument_whenGetDocument_thenReturnJsonArray() throws Exception {

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
