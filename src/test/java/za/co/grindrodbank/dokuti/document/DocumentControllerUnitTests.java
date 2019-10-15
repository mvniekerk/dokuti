/**
 * *************************************************
 * Copyright Grindrod Bank Limited 2019, All Rights Reserved.
 * **************************************************
 * NOTICE:  All information contained herein is, and remains
 * the property of Grindrod Bank Limited.
 * The intellectual and technical concepts contained
 * herein are proprietary to Grindrod Bank Limited
 * and are protected by trade secret or copyright law.
 * Use, dissemination or reproduction of this information/material
 * is strictly forbidden unless prior written permission is obtained
 * from Grindrod Bank Limited.
 */
package za.co.grindrodbank.dokuti.document;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openapitools.model.DocumentInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.grindrodbank.dokuti.attribute.AttributeService;
import za.co.grindrodbank.dokuti.document.DocumentControllerImpl;
import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.document.DocumentService;
import za.co.grindrodbank.dokuti.documenttag.DocumentTagService;
import za.co.grindrodbank.dokuti.documentversion.DocumentVersionService;
import za.co.grindrodbank.dokuti.group.GroupService;
import za.co.grindrodbank.dokuti.service.databaseentitytoapidatatransferobjectmapper.DatabaseEntityToApiDataTransferObjectMapperServiceImpl;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.DocumentDataStoreService;
import za.co.grindrodbank.dokuti.service.resourcepermissions.ResourcePermissionsService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DocumentControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
public class DocumentControllerUnitTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private DocumentService documentService;

	@MockBean
	private DocumentVersionService documentVersionService;

	@MockBean
	private DocumentDataStoreService documentStorageService;

	@MockBean
	private ResourcePermissionsService resourcePermission;

	@MockBean
	private GroupService groupService;

	@MockBean
	private AttributeService attributeService;

	@MockBean
	private DocumentTagService documentTagService;

	@MockBean
	private DatabaseEntityToApiDataTransferObjectMapperServiceImpl databaseEntityToApiDataTranfserObjectMapperServiceImpl;
	
    @Autowired
    protected ObjectMapper objectMapper;

	@Test
	public void givenDocument_whenGetDocument_thenReturnJsonArray() throws Exception {

		UUID documentId = UUID.randomUUID();

		DocumentEntity document = new DocumentEntity();
		document.setId(documentId);
		document.setContentType("text/plain");
		document.setDescription("Mocked document description");
		document.setName("Mocked document name");

		Mockito.when(documentService.findById(documentId)).thenReturn(document);

		mvc.perform(get("/documents/" + documentId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200));
	}

	@Test
	public void givenDocument_whenGetDocumentData_thenReturnFileData() throws Exception {

		UUID documentId = UUID.randomUUID();

		DocumentEntity document = new DocumentEntity();
		document.setId(documentId);
		document.setContentType("text/plain");
		document.setDescription("Mocked document description");
		document.setName("Mocked document name");

		Mockito.when(documentService.findById(documentId)).thenReturn(document);

		mvc.perform(get("/documents/" + documentId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200));
	}

	@Test
	public void givenDocument_whenPutDocumentInfo_thenReturnUpdatedInfo() throws Exception {

		UUID documentId = UUID.randomUUID();

		DocumentEntity document = new DocumentEntity();
		document.setId(documentId);
		document.setContentType("text/plain");
		document.setDescription("Mocked document description");
		document.setName("Mocked old document name");

		Mockito.when(documentService.save(document)).thenReturn(document);
		Mockito.when(documentService.findById(documentId)).thenReturn(document);
		
		DocumentInfoRequest documentInfoRequest = new DocumentInfoRequest();
		documentInfoRequest.setName("Mocked new document name");
	
		mvc.perform(put("/documents/" + documentId.toString() + "/info")
				  .contentType(MediaType.APPLICATION_JSON)
				  .content(objectMapper.writeValueAsString(documentInfoRequest))
				  .characterEncoding("utf-8"))
		          .andExpect(status().is(200));	 
	}
}
