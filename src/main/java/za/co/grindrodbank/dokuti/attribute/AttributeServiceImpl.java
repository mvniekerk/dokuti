/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.attribute;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import za.co.grindrodbank.dokuti.exceptions.DatabaseLayerException;
import za.co.grindrodbank.dokuti.exceptions.InvalidRequestException;
import za.co.grindrodbank.dokuti.exceptions.ResourceNotFoundException;
import za.co.grindrodbank.security.service.accesstokenpermissions.SecurityContextUtility;


@Service
public class AttributeServiceImpl implements AttributeService {

	@Autowired
	private AttributeRepository attributeRepository;

	@Override
	public AttributeEntity save(AttributeEntity attribute) {
		attribute.setUpdatedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));

		return attributeRepository.save(attribute);
	}

	public AttributeEntity createAttribute(String name, String validationRegex) {
		AttributeEntity attribute = new AttributeEntity(name, validationRegex);

		if (!attribute.validationRegexIsValid()) {
			throw new InvalidRequestException("Invalid validation regular expression provided for attribute.", null);
		}

		attribute.setUpdatedBy(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()));

		try {
			attribute = attributeRepository.save(attribute);
		} catch (Exception e) {
			throw new DatabaseLayerException("Error saving new document attribute.", e);
		}

		return attribute;
	}

	public AttributeEntity findById(Short attributeId) {
		Optional<AttributeEntity> optionalAttribute = attributeRepository.findById(attributeId);

		if (!optionalAttribute.isPresent()) {
			throw new ResourceNotFoundException("Attribute not found", null);
		}

		return optionalAttribute.get();
	}
	
	public AttributeEntity updateAttribute(Short attributeId, String name, String validationRegex) {
		AttributeEntity attributeEntity = findById(attributeId);
		attributeEntity.setName(name);
		attributeEntity.setValidationRegex(validationRegex);

		if (!attributeEntity.validationRegexIsValid()) {
			throw new InvalidRequestException("Invalid validation regular expression provided for attribute.", null);
		}

		return save(attributeEntity);
	}

	public Page<AttributeEntity> findAll(Pageable pageable) {
		return attributeRepository.findAll(pageable);
	}

}
