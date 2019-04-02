/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.exceptions;

public class NotAuthorisedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotAuthorisedException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
