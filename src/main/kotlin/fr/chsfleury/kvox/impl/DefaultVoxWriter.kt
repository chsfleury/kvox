package fr.chsfleury.kvox.impl

import fr.chsfleury.kvox.VoxWriter
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.OutputStream

class DefaultVoxWriter(
    private val outputStream: OutputStream
): VoxWriter<DefaultVoxFile> {
    override fun write(file: DefaultVoxFile) {
        outputStream.write(DefaultVoxReader.MAGIC_BYTES)
        outputStream.writeIntLittleEndian(file.version)
        file.root.writeTo(outputStream)
    }

    override fun close() {
        outputStream.close()
    }
}