/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.group;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openapitools.api.GroupsApi;
import org.openapitools.model.Group;
import org.openapitools.model.GroupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
public class GroupControllerImpl implements GroupsApi {

	Logger logger = LoggerFactory.getLogger(GroupControllerImpl.class);
	private static final String DEFAULT_SORT_FIELD = "name";

	@Autowired
	private GroupService groupService;
	@Autowired
	private DatabaseEntityToApiDataTransferObjectMapperService databaseEntityToApiDataTranfserObjectMapperService;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public ResponseEntity<Group> createGroup(GroupRequest groupRequest) {
		GroupEntity groupEntity = new GroupEntity();
		groupEntity.setName(groupRequest.getName());
		groupEntity = groupService.save(groupEntity);
		Group group = new Group();
		BeanUtils.copyProperties(groupEntity, group);

		return new ResponseEntity<>(group, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Group> getGroup(UUID groupId) {
		GroupEntity groupEntity = groupService.findById(groupId);
		Group group = databaseEntityToApiDataTranfserObjectMapperService.mapGroupEntityToGroup(groupEntity);
		return new ResponseEntity<>(group, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Group> updateGroup(UUID groupId, GroupRequest groupRequest) {
		GroupEntity groupEntity = groupService.findById(groupId);
		groupEntity.setName(groupRequest.getName());
		groupEntity = groupService.save(groupEntity);
		Group group = databaseEntityToApiDataTranfserObjectMapperService.mapGroupEntityToGroup(groupEntity);

		return new ResponseEntity<>(group, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Group>> getGroups(Integer page, Integer size, String filterName, List<String> orderBy) {
		Sort sort = ParseOrderByQueryParam.resolveArgument(orderBy, DEFAULT_SORT_FIELD);
		final PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<GroupEntity> groupEntities = null;

		if (filterName != null) {
			groupEntities = groupService.findByName(filterName, pageRequest);
		} else {
			groupEntities = this.groupService.findAll(pageRequest);
		}

		if (groupEntities.hasContent()) {
			Page<Group> groups = databaseEntityToApiDataTranfserObjectMapperService
					.mapGroupEntitiesPageToGroupPage(groupEntities);
			HttpHeaders headers = new HttpHeaders();
			eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Group>(this, groups, headers));

			return new ResponseEntity<>(groups.getContent(), headers, HttpStatus.OK);
		}

		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteGroup(UUID groupId) {
		GroupEntity groupEntity = groupService.findById(groupId);
		groupService.delete(groupEntity);

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

}
