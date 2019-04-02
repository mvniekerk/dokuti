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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import za.co.grindrodbank.dokuti.group.GroupEntity;
import za.co.grindrodbank.dokuti.group.GroupRepository;
import za.co.grindrodbank.dokuti.group.GroupService;
import za.co.grindrodbank.dokuti.group.GroupServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GroupServiceUnitTests {

	@MockBean
	private GroupRepository groupRepository;

	@TestConfiguration
	static class GroupServiceTestContextConfiguration {

		@Bean
		public GroupService groupService() {
			return new GroupServiceImpl();
		}
	}

	@Autowired
	private GroupService groupService;

	@Test
	public void givenGroup_whenGetGroup_thenReturnJsonArray() throws Exception {

		UUID groupId = UUID.randomUUID();

		GroupEntity group = new GroupEntity();
		group.setName("Test Group Name");

		Optional<GroupEntity> optionalGroup = Optional.of(group);
		Mockito.when(groupRepository.findById(groupId)).thenReturn(optionalGroup);

		GroupEntity searchedGroup = groupService.findById(groupId);
		assertEquals("Failure - Group names are not equal", searchedGroup.getName(), group.getName());
	}

}
