/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.attribute;

import java.util.ArrayList;
import java.util.List;

import org.openapitools.api.AttributesApi;
import org.openapitools.model.Attribute;
import org.openapitools.model.AttributeRequest;
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
//@PreAuthorize("hasRole('DOKUTI_USER') or hasRole('DOKUTI_ADMIN')")
public class AttributeControllerImpl implements AttributesApi {

	Logger logger = LoggerFactory.getLogger(AttributeControllerImpl.class);
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	private static final String DEFAULT_SORT_FIELD = "name";
	@Autowired
	private AttributeService attributeService;
	@Autowired
	private DatabaseEntityToApiDataTransferObjectMapperService databaseEntityToApiDataTransfserObjectMapperService;

	@Override
	public ResponseEntity<Attribute> addAttribute(AttributeRequest createAttributeRequest) {
		return new ResponseEntity<>(databaseEntityToApiDataTransfserObjectMapperService
				.mapAttributeEntityToAttribute(attributeService.createAttribute(createAttributeRequest.getName(),
						createAttributeRequest.getValidationRegex())),
				HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Attribute> getAttribute(Integer attributeId) {
		return new ResponseEntity<>(databaseEntityToApiDataTransfserObjectMapperService
				.mapAttributeEntityToAttribute(attributeService.findById(attributeId.shortValue())), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Attribute>> getAttributes(Integer page, Integer size, List<String> filterAttributes,
			List<String> orderBy) {
		Sort sort = ParseOrderByQueryParam.resolveArgument(orderBy, DEFAULT_SORT_FIELD);
		Page<AttributeEntity> attributeEntitiesPage = attributeService.findAll(PageRequest.of(page, size, sort));

		if (attributeEntitiesPage.hasContent()) {
			Page<Attribute> attributesPage = databaseEntityToApiDataTransfserObjectMapperService
					.mapAttributeEntityPageToAttributePage(attributeEntitiesPage);
			HttpHeaders headers = new HttpHeaders();
			eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Attribute>(this, attributesPage, headers));

			return new ResponseEntity<>(attributesPage.getContent(), headers, HttpStatus.OK);
		}

		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Attribute> updateAttribute(Integer attributeId, AttributeRequest attributeRequest) {
		return new ResponseEntity<>(databaseEntityToApiDataTransfserObjectMapperService
				.mapAttributeEntityToAttribute(attributeService.updateAttribute(attributeId.shortValue(),
						attributeRequest.getName(), attributeRequest.getValidationRegex())),
				HttpStatus.OK);
	}
}
