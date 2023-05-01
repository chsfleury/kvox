package fr.chsfleury.kvox;

class InvalidVoxException(
    override val message: String? = null,
    override val cause: Throwable? = null
): RuntimeException(message, cause)
