/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.service.resourcepermissions;

import za.co.grindrodbank.dokuti.document.DocumentEntity;

public interface ResourcePermissionsService {
	
	public Boolean accessingUserCanReadDocument(DocumentEntity document);
	public Boolean accessingUserCanWriteDocument(DocumentEntity document);

}
