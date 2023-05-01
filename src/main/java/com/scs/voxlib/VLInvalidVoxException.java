package com.scs.voxlib;

public class VLInvalidVoxException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public VLInvalidVoxException(String message) {
        super(message);
    }

    public VLInvalidVoxException(String message, Throwable cause) {
        super(message, cause);
    }
}
