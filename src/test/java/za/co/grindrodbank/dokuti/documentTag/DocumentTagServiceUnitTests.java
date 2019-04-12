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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagEntity;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagId;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagRepository;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class DocumentTagServiceUnitTests {

	@Mock
	private DocumentTagRepository documentTagRepository;

	@InjectMocks
	private DocumentTagServiceImpl documentTagService;

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
