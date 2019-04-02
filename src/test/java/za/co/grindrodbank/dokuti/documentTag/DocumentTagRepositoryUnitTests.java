/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documentTag;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import za.co.grindrodbank.dokuti.documenttag.DocumentTagEntity;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagId;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DocumentTagRepositoryUnitTests {

	@Autowired
	private DocumentTagRepository documentTagRepository;

	@Test
	public void givenDocumentTag_whenSaveDocumentTag_thenReturnDocumentTag() throws Exception {

		UUID documentId = UUID.randomUUID();
		String tag = "Test Tag";

		DocumentTagId documentTagId = new DocumentTagId();
		documentTagId.setDocumentId(documentId);
		documentTagId.setTag(tag);
		DocumentTagEntity documentTag = new DocumentTagEntity();
		documentTag.setId(documentTagId);

		DocumentTagEntity savedDocumentTag = documentTagRepository.save(documentTag);
		assertEquals("Failure - Tag values are not equal", savedDocumentTag.getId().getTag(), documentTagId.getTag());
		assertEquals("Failure - Tag IDs are not equal", savedDocumentTag.getId().getDocumentId(),
				documentTagId.getDocumentId());
	}
}
