/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti.exceptions;

public class ChecksumFailedException extends RuntimeException {

	private static final long serialVersionUID = 7196499722337606798L;

	public ChecksumFailedException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
