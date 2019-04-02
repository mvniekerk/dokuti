/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.group;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;

public interface GroupService {

	public GroupEntity findById(UUID groupId) throws ResourceNotFoundException;

	public GroupEntity save(GroupEntity group) throws DatabaseLayerException;

	public void delete(GroupEntity group) throws DatabaseLayerException;

	public Page<GroupEntity> findAll(Pageable pageable);

	public Page<GroupEntity> findByName(String name, Pageable pageable);

}
