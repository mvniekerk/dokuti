/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.attribute;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.attribute.AttributeRepository;
import za.co.grindrodbank.dokuti.attribute.AttributeService;
import za.co.grindrodbank.dokuti.attribute.AttributeServiceImpl;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionService;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.DocumentDataStoreService;
import za.co.grindrodbank.dokuti.service.resourcepermissions.ResourcePermissionsService;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AttributeServiceUnitTests {

	@MockBean
	private AttributeRepository documentAttributeRepository;

	@TestConfiguration
	static class AttributeServiceTestContextConfiguration {

		@Bean
		public AttributeService documentAttributeService() {
			return new AttributeServiceImpl();
		}
	}

	@MockBean
	private DocumentVersionService documentVersionService;
	@MockBean
	private DocumentDataStoreService DocumentDataStoreService;
	@Autowired
	private AttributeService documentAttributeService;
	@MockBean
	private ResourcePermissionsService resourcePermissions;

	@Test
	public void givenAttribute_whenGetAttribute_thenReturnAttribute() throws Exception {

		UUID documentId = UUID.randomUUID();

		AttributeEntity attribute = new AttributeEntity("Test attribute name.", "Test attribute label validation regex");

		DocumentEntity document = new DocumentEntity();
		document.setId(documentId);
		document.setContentType("text/plain");
		document.setDescription("Mocked document description");
		document.setName("Mocked document name");

//		Attribute documentAttribute = new Attribute();
//		UUID documentAttributeId = UUID.fromString("bed5a566-6191-4a51-96a1-d03bb337940a");
//		documentAttribute.setId(documentAttributeId);
//		documentAttribute.setLabel(documentAttributeLabel);
//		documentAttribute.setValue("Test attribute values");
//		documentAttribute.setDocument(document);
//
//		Optional<Attribute> optionalAttribute = Optional.of(documentAttribute);
//
//		Mockito.when(resourcePermissions.accessingUserCanReadDocument(document)).thenReturn(true);
//		Mockito.when(documentAttributeRepository.findById(documentAttributeId)).thenReturn(optionalAttribute);
//
//		Attribute searchedAttribute = documentAttributeService.findById(documentAttributeId);
//		assertEquals("Failure - DocumentIDs are not equal", searchedAttribute.getId(), documentAttributeId);
//		assertEquals("Failure - Attribute values are not equal", searchedAttribute.getValue(),
//				documentAttribute.getValue());
//
//		assertEquals("Failure - Document IDs are not equal", searchedAttribute.getDocument().getId(),
//				documentId);

	}

}
