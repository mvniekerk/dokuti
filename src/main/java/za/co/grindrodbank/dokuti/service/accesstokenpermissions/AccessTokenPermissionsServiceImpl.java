package za.co.grindrodbank.dokuti.service.accesstokenpermissions;

import java.util.List;
import org.springframework.stereotype.Service;
import za.co.grindrodbank.dokuti.utilities.SecurityContextUtility;

@Service
public class AccessTokenPermissionsServiceImpl implements AccessTokenPermissionsService {

	@Override
	public Boolean hasPermission(String permission) {
		List<String> permissions = SecurityContextUtility.getPermissionsFromJwt();
		
		if(permissions == null) {
			return false;
		}
		
		return permissions.contains(permission);
	}

}
