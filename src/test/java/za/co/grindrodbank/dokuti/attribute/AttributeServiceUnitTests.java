/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.attribute;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.attribute.AttributeRepository;
import za.co.grindrodbank.dokuti.attribute.AttributeServiceImpl;
import za.co.grindrodbank.dokuti.utilities.SecurityContextUtility;

@RunWith(MockitoJUnitRunner.class)
public class AttributeServiceUnitTests {

	@Mock
	private AttributeRepository attributeRepository;
	
	@InjectMocks
	private AttributeServiceImpl attributeService;

	@Test
	public void givenAttribute_whenGetAttribute_thenReturnAttribute() throws Exception {

		Short attributeId = 1;
		AttributeEntity attribute = new AttributeEntity("Test attribute name.",
				"Test attribute label validation regex");
		attribute.setId(attributeId);
		Optional<AttributeEntity> optionalAttribute = Optional.of(attribute);

		Mockito.when(attributeRepository.findById(attributeId)).thenReturn(optionalAttribute);

		AttributeEntity searchedAttribute = attributeService.findById(attributeId);
		assertEquals("Failure - Attribute names are not equal", searchedAttribute.getName(), attribute.getName());
		assertEquals("Failure - Attribute validation regex are not equal", searchedAttribute.getValidationRegex(),
				attribute.getValidationRegex());
	}
	
	public void givenInput_whenCreateAttribute_thenReturnAttribute() {
		String attributeName = "Test Attribute Name";
		String attributeValidationRegex = "[a-y]";
		UUID userId = UUID.randomUUID();
		AttributeEntity attribute = new AttributeEntity(attributeName, attributeName);
		//PowerMockito.mockStatic(SecurityContextUtility.class);
		
		Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(userId.toString());
		Mockito.when(attributeRepository.save(attribute)).thenReturn(attribute);
		
		AttributeEntity createdAttribute = attributeService.createAttribute(attributeName, attributeValidationRegex);
		assertEquals("Failure - Attribute names are not equal", createdAttribute.getName(), attribute.getName());
		assertEquals("Failure - Attribute validation regex are not equal", createdAttribute.getValidationRegex(),
				attribute.getValidationRegex());
		assertEquals("Failure - Attribute updated by not equal to expected UserId UUID", createdAttribute.getUpdatedBy(), userId);
		
	}

}
