package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.InvalidVoxException
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.writeIntLittleEndian
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

open class VoxChunk(
    val type: String
) {
    @Throws(IOException::class)
    fun writeTo(stream: OutputStream) {
        ByteArrayOutputStream().use { contentStream ->
            ByteArrayOutputStream().use { childStream ->
                stream.write(type.toByteArray())
                writeContent(contentStream)
                val contentBytes = contentStream.toByteArray()
                writeChildren(childStream)
                val childBytes = childStream.toByteArray()
                stream.writeIntLittleEndian(contentBytes.size)
                stream.writeIntLittleEndian(childBytes.size)
                stream.write(contentBytes)
                stream.write(childBytes)
            }
        }
    }

    /** Write to the stream the content directly associated with this chunk.  */
    @Throws(IOException::class)
    protected open fun writeContent(stream: OutputStream) {}

    /** Write to the stream the content associated with this chunk's children.  */
    @Throws(IOException::class)
    protected open fun writeChildren(stream: OutputStream) {}

    companion object {
        @Throws(IOException::class)
        fun readChunk(stream: InputStream): VoxChunk? {
            return readChunk(stream, null)
        }

        @Throws(IOException::class)
        fun readChunk(inputStream: InputStream, expectedID: String?): VoxChunk? {
            val id = getChunkId(inputStream) ?: return null
            if (expectedID != null && id != expectedID) {
                throw InvalidVoxException("$expectedID chunk expected, got $id")
            }
            val length = inputStream.readIntLittleEndian()
            val childrenLength = inputStream.readIntLittleEndian()
            val chunkBytes = readChunkBytes(id, inputStream, length)
            val childrenChunkBytes = readChildrenChunkBytes(inputStream, childrenLength)
            ByteArrayInputStream(chunkBytes).use { chunkStream ->
                ByteArrayInputStream(childrenChunkBytes).use { childrenStream ->
                    return ChunkFactory.createChunk(id, chunkStream, childrenStream)
                }
            }
        }

        private fun readChunkBytes(chunkId: String, inputStream: InputStream, length: Int): ByteArray {
            val chunkBytes = ByteArray(length)
            if (length > 0 && inputStream.read(chunkBytes) != length) {
                throw InvalidVoxException("Chunk '$chunkId' is incomplete")
            }
            return chunkBytes
        }

        private fun readChildrenChunkBytes(inputStream: InputStream, childrenLength: Int): ByteArray {
            val childrenChunkBytes = ByteArray(childrenLength)
            inputStream.read(childrenChunkBytes)
            return childrenChunkBytes
        }

        private fun getChunkId(inputStream: InputStream): String? {
            val chunkID = ByteArray(4)
            val bytesRead = inputStream.read(chunkID)
            if (bytesRead != 4) {
                if (bytesRead == -1) {
                    // There's no chunk here, this is fine.
                    return null
                }
                throw InvalidVoxException("Incomplete chunk ID")
            }
            return chunkID.toString(Charsets.UTF_8)
        }
    }
}