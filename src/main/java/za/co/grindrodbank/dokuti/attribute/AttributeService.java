/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.attribute;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttributeService {

	public AttributeEntity save(AttributeEntity attribute);

	public AttributeEntity findById(Short attributeId);

	public AttributeEntity createAttribute(String name, String validationRegex);

	public AttributeEntity updateAttribute(Short attributeId, String name, String validationRegex);

	public Page<AttributeEntity> findAll(Pageable pageable);

}
