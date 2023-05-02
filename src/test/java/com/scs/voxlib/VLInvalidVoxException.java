package com.scs.voxlib;

public class VLInvalidVoxException extends RuntimeException {

	public VLInvalidVoxException(String message) {
        super(message);
    }

    public VLInvalidVoxException(String message, Throwable cause) {
        super(message, cause);
    }
}
