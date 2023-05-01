package fr.chsfleury.kvox;

class InvalidVoxException(
    override val message: String?,
    override val cause: Throwable?
): RuntimeException(message, cause)
