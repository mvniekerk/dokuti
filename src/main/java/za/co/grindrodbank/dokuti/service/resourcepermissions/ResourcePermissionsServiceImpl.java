/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.service.resourcepermissions;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import za.co.grindrodbank.dokuti.document.DocumentEntity;
import za.co.grindrodbank.dokuti.utilities.SecurityContextUtility;

@Service
public class ResourcePermissionsServiceImpl implements ResourcePermissionsService {

	Logger logger = LoggerFactory.getLogger(ResourcePermissionsServiceImpl.class);

	@Override
	public Boolean accessingUserCanReadDocument(DocumentEntity document) {
		return document.userHasPermission(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()),
				DocumentPermission.READ);
	}

	@Override
	public Boolean accessingUserCanWriteDocument(DocumentEntity document) {
		return document.userHasPermission(UUID.fromString(SecurityContextUtility.getUserIdFromJwt()),
				DocumentPermission.WRITE);
	}

}
