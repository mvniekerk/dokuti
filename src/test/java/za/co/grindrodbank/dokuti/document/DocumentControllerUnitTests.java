/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.document;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openapitools.model.DocumentInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
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
@WebMvcTest(controllers = DocumentControllerImpl.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
public class DocumentControllerUnitTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private DocumentService documentService;

	
    @Mock
    private DocumentRepository documentRepository;
    
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }    
    
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
	
    @Test
	public void givenDocument_whenDoDocumentArchive_thenReturnArchevedDocumnet() throws Exception {

        UUID documentId = UUID.randomUUID();

        DocumentEntity document = new DocumentEntity();
        document.setId(documentId);
        document.setContentType("text/plain");
        document.setDescription("Document Desc");
        document.setName("Document Name");
        document.setIsArchived(false);
        document.setDocumentVersions(new HashSet<>());
        document.setDocumentTags(new ArrayList<>());
        document.setGroups(new HashSet<>());
        document.setDocumentPermissions(new ArrayList<>());
        document.setDocumentAttributes(new ArrayList<>());

        Mockito.when(documentService.save(document)).thenReturn(document);
        Mockito.when(documentService.findById(documentId)).thenReturn(document);
        Mockito.when(databaseEntityToApiDataTranfserObjectMapperServiceImpl.mapDocumentEntityToDocument(document)).thenCallRealMethod();

	    mvc.perform(patch("/documents/" + documentId.toString() + "/archive")
	                  .contentType(MediaType.APPLICATION_JSON)
	                  .characterEncoding("utf-8"))
	                  .andExpect(status().is(200))
	                  .andExpect(jsonPath("$.isArchived", is(true)));  
	}

    @Test
    public void givenDocument_whenDoDocumentUnArchive_thenReturnUnArchevedDocumnet() throws Exception {

        UUID documentId = UUID.randomUUID();

        DocumentEntity document = new DocumentEntity();
        document.setId(documentId);
        document.setContentType("text/plain");
        document.setDescription("Document Desc");
        document.setName("Document Name");
        document.setIsArchived(true);
        document.setDocumentVersions(new HashSet<>());
        document.setDocumentTags(new ArrayList<>());
        document.setGroups(new HashSet<>());
        document.setDocumentPermissions(new ArrayList<>());
        document.setDocumentAttributes(new ArrayList<>());

        Mockito.when(documentService.save(document)).thenReturn(document);
        Mockito.when(documentService.findById(documentId)).thenReturn(document);
        Mockito.when(databaseEntityToApiDataTranfserObjectMapperServiceImpl.mapDocumentEntityToDocument(document)).thenCallRealMethod();

        mvc.perform(patch("/documents/" + documentId.toString() + "/unarchive")
                     .contentType(MediaType.APPLICATION_JSON)
                     .characterEncoding("utf-8"))
                     .andExpect(status().is(200))
                     .andExpect(jsonPath("$.isArchived", is(false)));
    }	   
}
