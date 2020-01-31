/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.utilities;

import java.util.UUID;

public class H2CustomFunctions {
	private H2CustomFunctions() {
		super();
	}

//	public static UUID gen_random_uuid() {
	public static UUID genRandomUuid() {
		return UUID.randomUUID();
	}
}
