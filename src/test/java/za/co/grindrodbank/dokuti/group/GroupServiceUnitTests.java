/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.group;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import za.co.grindrodbank.dokuti.group.GroupEntity;
import za.co.grindrodbank.dokuti.group.GroupRepository;
import za.co.grindrodbank.dokuti.group.GroupServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class GroupServiceUnitTests {

	@Mock
	private GroupRepository groupRepository;

	@InjectMocks
	private GroupServiceImpl groupService;

	@Test
	public void givenGroup_whenGetGroup_thenGroupEntityReturned() throws Exception {

		UUID groupId = UUID.randomUUID();

		GroupEntity group = new GroupEntity();
		group.setName("Test Group Name");

		Optional<GroupEntity> optionalGroup = Optional.of(group);
		Mockito.when(groupRepository.findById(groupId)).thenReturn(optionalGroup);

		GroupEntity searchedGroup = groupService.findById(groupId);
		assertEquals("Failure - Group names are not equal", searchedGroup.getName(), group.getName());
	}

}
