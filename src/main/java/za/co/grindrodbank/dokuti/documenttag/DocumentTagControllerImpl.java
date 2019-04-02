/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.documenttag;

import java.util.ArrayList;
import java.util.List;

import org.openapitools.api.TagsApi;
import org.openapitools.model.LookupTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import za.co.grindrodbank.dokuti.events.PaginatedResultsRetrievedEvent;
import za.co.grindrodbank.dokuti.service.databaseentitytoapidatatransferobjectmapper.DatabaseEntityToApiDataTransferObjectMapperService;
import za.co.grindrodbank.dokuti.utilities.ParseOrderByQueryParam;

@RestController
@PreAuthorize("hasRole('DOKUTI_USER') or hasRole('DOKUTI_ADMIN')")
public class DocumentTagControllerImpl implements TagsApi {

	@Autowired
	private DocumentTagService documentTagService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private DatabaseEntityToApiDataTransferObjectMapperService databaseEntityToApiDataTranfserObjectMapperService;

	Logger logger = LoggerFactory.getLogger(DocumentTagControllerImpl.class);

	private static final String DEFAULT_SORT_FIELD = "tag";

	@Override
	public ResponseEntity<List<LookupTag>> getTags(Integer page, Integer size, List<String> filterTags,
			List<String> orderBy) {
		Sort sort = ParseOrderByQueryParam.resolveArgument(orderBy, DEFAULT_SORT_FIELD);
		final PageRequest pageRequest = PageRequest.of(page, size, sort);

		Page<String> tagsPage = documentTagService.findAllDistinctTags(pageRequest);

		if (tagsPage.hasContent()) {
			Page<LookupTag> lookupTagsPage = databaseEntityToApiDataTranfserObjectMapperService
					.mapDocumentTagsPageToLookupTagPage(tagsPage);
			HttpHeaders headers = new HttpHeaders();
			eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<LookupTag>(this, lookupTagsPage, headers));

			return new ResponseEntity<>(lookupTagsPage.getContent(), headers, HttpStatus.OK);
		}

		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

}
