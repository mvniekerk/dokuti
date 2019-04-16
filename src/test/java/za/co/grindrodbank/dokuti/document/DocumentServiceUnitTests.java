/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.document.DocumentRepository;
import za.co.grindrodbank.dokuti.documentattribute.DocumentAttributeEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionServiceImpl;
import za.co.grindrodbank.dokuti.exceptions.InvalidRequestException;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.FileSystemDocumentStoreService;
import za.co.grindrodbank.dokuti.service.resourcepermissions.ResourcePermissionsService;
import za.co.grindrodbank.dokuti.utilities.SecurityContextUtility;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextUtility.class)
public class DocumentServiceUnitTests {

	@Mock
	private DocumentRepository documentRepository;

	@Mock
	private DocumentVersionServiceImpl documentVersionService;

	@Mock
	private FileSystemDocumentStoreService DocumentDataStoreService;

	@Mock
	private ResourcePermissionsService resourcePermission;

	@InjectMocks
	private DocumentServiceImpl documentService;

	@Test
	public void givenDocumentId_thenDocumentReturned() throws Exception {

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

	@Test
	public void givenDocumentAndAttribute_whenAddAttribute_thenReturnDocumentAttribute() {
		UUID documentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		DocumentEntity documentMock = new DocumentEntity();
		documentMock.setId(documentId);
		documentMock.setContentType("text/plain");
		documentMock.setDescription("Mocked document description");
		documentMock.setName("Mocked document name");

		AttributeEntity attributeEntityMock = new AttributeEntity();
		attributeEntityMock.setId((short) 1);
		attributeEntityMock.setName("Test Attribute Name");
		attributeEntityMock.setValidationRegex("[a-y]");
		attributeEntityMock.setUpdatedBy(userId);

		Mockito.when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentMock);

		PowerMockito.mockStatic(SecurityContextUtility.class);
		Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(userId.toString());

		String documentAttributeValue = "test attribute value";
		DocumentAttributeEntity documentAttribute = documentService.addDocumentAttribute(documentMock,
				attributeEntityMock, documentAttributeValue);

		assertEquals("Failure - Document Attribute values are not equal", documentAttributeValue,
				documentAttribute.getValue());
	}

	/**
	 * Tests sending through invalid values for an attribute that has a 'Contract
	 * Expiry' validation regex, when creating a document attribute. Tests that the
	 * anticipated exception is thrown.
	 */
	@Test(expected = InvalidRequestException.class)
	public void givenDocumentAttributeWithIncorrectContractExpiryDateValue_thenInvalidRequestExceptionThrown() {
		UUID documentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		DocumentEntity documentMock = new DocumentEntity();
		documentMock.setId(documentId);
		documentMock.setContentType("text/plain");
		documentMock.setDescription("Mocked document description");
		documentMock.setName("Mocked document name");

		AttributeEntity attributeEntityMock = new AttributeEntity();
		attributeEntityMock.setId((short) 1);
		attributeEntityMock.setName("Test Attribute Name");
		attributeEntityMock.setValidationRegex(
				"(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)");
		attributeEntityMock.setUpdatedBy(userId);

		Mockito.when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentMock);

		PowerMockito.mockStatic(SecurityContextUtility.class);
		Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(userId.toString());

		String documentAttributeValue = "test attribute value";

		DocumentAttributeEntity documentAttribute = documentService.addDocumentAttribute(documentMock,
				attributeEntityMock, documentAttributeValue);
	}

	/**
	 * Tests sending through a valid value for an attribute that has a 'Contract
	 * Expiry' validation regex, when creating a document attribute. Tests that the
	 * anticipated exception is not thrown and that the document attribute is
	 * created.
	 */
	@Test
	public void givenDocumentAttributeWithCorrectContractExpiryDateValue_thenDocumentAttributeCreated() {
		UUID documentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		DocumentEntity documentMock = new DocumentEntity();
		documentMock.setId(documentId);
		documentMock.setContentType("text/plain");
		documentMock.setDescription("Mocked document description");
		documentMock.setName("Mocked document name");

		AttributeEntity attributeEntityMock = new AttributeEntity();
		attributeEntityMock.setId((short) 1);
		attributeEntityMock.setName("Test Attribute Name");
		attributeEntityMock.setValidationRegex(
				"(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)");
		attributeEntityMock.setUpdatedBy(userId);

		Mockito.when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentMock);

		PowerMockito.mockStatic(SecurityContextUtility.class);
		Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(userId.toString());

		String documentAttributeValue = "22/04/2019";
		DocumentAttributeEntity documentAttribute = documentService.addDocumentAttribute(documentMock,
				attributeEntityMock, documentAttributeValue);

		assertEquals("Failure - Document Attribute values are not equal", documentAttributeValue,
				documentAttribute.getValue());
	}

	@Test
	public void givenDocumentParams_thenNewDocumentCreated() {
		UUID documentId = UUID.randomUUID();
		UUID documentVersionId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		DocumentEntity documentMock = new DocumentEntity();
		documentMock.setId(documentId);
		documentMock.setContentType("text/plain");
		documentMock.setDescription("Mocked document description");
		documentMock.setName("Mocked document name");
		documentMock.setUpdatedBy(userId);
		
		MockMultipartFile testFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
		
		String fileContentChecksum = "test checksum";
		DocumentVersionEntity mockDocumentVersion = new DocumentVersionEntity();
		mockDocumentVersion.setChecksum(fileContentChecksum);
		mockDocumentVersion.setId(documentVersionId);
		mockDocumentVersion.setUploadedBy(userId);
		
		Mockito.when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentMock);
		Mockito.when(documentVersionService.createDocumentVersion(any(DocumentEntity.class),
				any(String.class))).thenReturn(mockDocumentVersion);

		PowerMockito.mockStatic(SecurityContextUtility.class);
		Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(userId.toString());

		DocumentEntity createdDocument = documentService.createNewDocument(testFile, documentMock.getDescription());

		assertEquals("Failure - Document names are not equal", documentMock.getName(), createdDocument.getName());
	}

}
