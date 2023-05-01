package fr.chsfleury.kvox.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder

object ByteArrays {

    fun ByteArray.toBufferLittleEndian() = ByteBuffer
        .wrap(this)
        .order(ByteOrder.LITTLE_ENDIAN)

}