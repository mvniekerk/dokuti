/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.group;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;
import za.co.grindrodbank.dokuti.utilities.SecurityContextUtility;

@Service
public class GroupServiceImpl implements GroupService {

	Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

	@Autowired
	private GroupRepository groupRepository;

	public GroupEntity findById(UUID groupId) throws ResourceNotFoundException {
		Optional<GroupEntity> optionalGroup = this.groupRepository.findById(groupId);

		if (!optionalGroup.isPresent()) {
			logger.warn("Could not find Group with ID {}", groupId);

			throw new ResourceNotFoundException("Could not find Group with ID " + groupId, null);
		}

		return optionalGroup.get();
	}

	public GroupEntity save(GroupEntity group) throws DatabaseLayerException {
		try {
			group.setUpdatedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));

			return groupRepository.save(group);
		} catch (Exception e) {
			logger.error("Error saving Group. {}", e.getMessage());

			throw new DatabaseLayerException("Error creating new Group.", e);
		}
	}

	public void delete(GroupEntity group) throws DatabaseLayerException {
		try {
			groupRepository.delete(group);
		} catch (Exception e) {
			logger.error("Error deleting Group. {}", e.getMessage());

			throw new DatabaseLayerException("Error deleting Group", e);
		}
	}

	@Override
	public Page<GroupEntity> findAll(Pageable pageable) {
		return groupRepository.findAll(pageable);
	}

	@Override
	public Page<GroupEntity> findByName(String name, Pageable pageable) {
		return groupRepository.findByName(name, pageable);
	}

}
