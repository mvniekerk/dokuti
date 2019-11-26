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
import static org.mockito.ArgumentMatchers.any;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import za.co.grindrodbank.dokuti.attribute.AttributeEntity;
import za.co.grindrodbank.dokuti.attribute.AttributeRepository;
import za.co.grindrodbank.dokuti.attribute.AttributeServiceImpl;
import za.co.grindrodbank.security.service.accesstokenpermissions.SecurityContextUtility;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextUtility.class)
public class AttributeServiceUnitTests {

	@Mock
	private AttributeRepository attributeRepository;

	@InjectMocks
	private AttributeServiceImpl attributeService;

	@Test
	public void givenAttributeId_whenGetAttribute_thenReturnAttribute() throws Exception {

		Short attributeId = 1;
		AttributeEntity attribute = new AttributeEntity("Test attribute name.",
				"Test attribute label validation regex");
		attribute.setId(attributeId);
		Optional<AttributeEntity> optionalAttribute = Optional.of(attribute);

		Mockito.when(attributeRepository.findById(attributeId)).thenReturn(optionalAttribute);

		AttributeEntity searchedAttribute = attributeService.findById(attributeId);
		assertEquals("Failure - Attribute names are not equal", attribute.getName(), searchedAttribute.getName());
		assertEquals("Failure - Attribute validation regex are not equal", attribute.getValidationRegex(),
				searchedAttribute.getValidationRegex());
	}

	@Test
	public void givenInput_whenCreateAttribute_thenReturnAttribute() {
		String attributeName = "Test Attribute Name";
		String attributeValidationRegex = "[a-y]";
		UUID userId = UUID.randomUUID();
		AttributeEntity attribute = new AttributeEntity(attributeName, attributeName);
		attribute.setUpdatedBy(userId);

		PowerMockito.mockStatic(SecurityContextUtility.class);
		Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(userId.toString());
		Mockito.when(attributeRepository.save(any(AttributeEntity.class))).thenReturn(attribute);

		AttributeEntity createdAttribute = attributeService.createAttribute(attributeName, attributeValidationRegex);
		assertEquals("Failure - Attribute names are not equal", attribute.getName(), createdAttribute.getName());
		assertEquals("Failure - Attribute validation regex are not equal", attribute.getValidationRegex(),
				createdAttribute.getValidationRegex());
		assertEquals("Failure - Attribute updated by not equal to expected UserId UUID", userId,
				createdAttribute.getUpdatedBy());

	}

	@Test
	public void givenInput_whenUpdateAttribute_thenReturnUpdatedAttribute() {
		Short attributeId = 1;
		UUID userId = UUID.randomUUID();

		String attributeName = "Test Attribute Name";
		String attributeValidationRegex = "[a-y]";
		AttributeEntity attribute = new AttributeEntity(attributeName, attributeValidationRegex);
		attribute.setUpdatedBy(userId);
		attribute.setId(attributeId);
		Mockito.when(attributeRepository.findById(attributeId)).thenReturn(Optional.of(attribute));

		String updatedName = "Test Attribute Name Updated";
		String updatedValidationRegex = "[a-z]";
		AttributeEntity updatedAttributeMock = new AttributeEntity(updatedName, updatedValidationRegex);
		updatedAttributeMock.setUpdatedBy(userId);
		updatedAttributeMock.setId(attributeId);
		Mockito.when(attributeRepository.save(any(AttributeEntity.class))).thenReturn(updatedAttributeMock);

		PowerMockito.mockStatic(SecurityContextUtility.class);
		Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(userId.toString());

		AttributeEntity updatedAttribute = attributeService.updateAttribute(attributeId, updatedName,
				updatedValidationRegex);
		assertEquals("Failure - Attribute names are not equal", updatedName, updatedAttribute.getName());
		assertEquals("Failure - Attribute validation regex are not equal", updatedValidationRegex,
				updatedAttribute.getValidationRegex());
		assertEquals("Failure - Attribute updated by not equal to expected UserId UUID", userId,
				updatedAttribute.getUpdatedBy());

	}

}
