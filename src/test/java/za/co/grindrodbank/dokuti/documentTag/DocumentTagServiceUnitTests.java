/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentTag;

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

import za.co.grindrodbank.dokuti.documenttag.DocumentTagEntity;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagId;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagRepository;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagService;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentTagServiceUnitTests {

	@MockBean
	private DocumentTagRepository documentTagRepository;

	@TestConfiguration
	static class DocumentTagServiceTestContextConfiguration {

		@Bean
		public DocumentTagService documentTagService() {
			return new DocumentTagServiceImpl();
		}
	}

	@Autowired
	private DocumentTagService documentTagService;

	@Test
	public void givenDocumentTag_whenGetDocumentTag_thenReturnDocumentTag() throws Exception {

		UUID documentId = UUID.randomUUID();
		String tag = "Test Tag";

		DocumentTagId documentTagId = new DocumentTagId();
		documentTagId.setDocumentId(documentId);
		documentTagId.setTag(tag);
		DocumentTagEntity documentTag = new DocumentTagEntity();
		documentTag.setId(documentTagId);

		Optional<DocumentTagEntity> optionalTag = Optional.of(documentTag);

		Mockito.when(documentTagRepository.findById(documentTagId)).thenReturn(optionalTag);

		Optional<DocumentTagEntity> searchedDocumentTag = documentTagService.findById(documentTagId);
		assertEquals("Failure - DocumentIDs are not equal", searchedDocumentTag.get().getId().getDocumentId(),
				documentId);
		assertEquals("Failure - Tag values are not equal", searchedDocumentTag.get().getId().getTag(), tag);
	}

}
