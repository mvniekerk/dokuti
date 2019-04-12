/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.attribute;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.attribute.AttributeRepository;
import za.co.grindrodbank.dokuti.attribute.AttributeServiceImpl;

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

}
