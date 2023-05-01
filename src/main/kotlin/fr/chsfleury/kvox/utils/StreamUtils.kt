package fr.chsfleury.kvox.utils

import com.scs.voxlib.GridPoint3
import com.scs.voxlib.InvalidVoxException
import fr.chsfleury.kvox.utils.ByteArrays.toBufferLittleEndian
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object StreamUtils {

    @Throws(IOException::class)
    fun InputStream.readIntLittleEndian(): Int {
        val bytes = ByteArray(4)
        if (read(bytes) != bytes.size) {
            throw IOException("Not enough bytes to read an int")
        }

        return bytes.toBufferLittleEndian().getInt()
    }

    @Throws(IOException::class)
    fun InputStream.readFloatLittleEndian(): Float {
        val bytes = ByteArray(4)
        if (read(bytes) != 4) {
            throw IOException("Not enough bytes to read a float")
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat()
    }

    @Throws(IOException::class)
    fun InputStream.readVector3i() = GridPoint3(
        readIntLittleEndian(),
        readIntLittleEndian(),
        readIntLittleEndian()
    )

    @Throws(IOException::class)
    fun InputStream.readVector3b(): GridPoint3 {
        val x = read()
        val y = read()
        val z = read()
        if (x == -1 || y == -1 || z == -1) {
            throw IOException("Not enough bytes to read a vector3b")
        }
        return GridPoint3(
            x and 0xff,
            y and 0xff,
            z and 0xff
        )
    }

    @Throws(IOException::class)
    fun InputStream.readString(): String {
        val n = readIntLittleEndian()
        if (n < 0) {
            throw IOException("String is too large to read")
        }
        val bytes = ByteArray(n)
        if (read(bytes) != n) {
            throw IOException("Not enough bytes to read a string of size $n")
        }
        return String(bytes)
    }

    @Throws(IOException::class)
    fun InputStream.readDictionary(): Map<String, String> {
        val n = readIntLittleEndian()
        if (n < 0) {
            throw InvalidVoxException("Dictionary too large")
        }
        val dict = HashMap<String, String>(n)
        for (i in 0 until n) {
            val key = readString()
            val value = readString()
            dict[key] = value
        }
        return dict
    }

    @Throws(IOException::class)
    fun OutputStream.writeIntLittleEndian(v: Int) = write(
        ByteBuffer
            .allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(v)
            .array()
    )

    @Throws(IOException::class)
    fun OutputStream.writeVector3i(v: GridPoint3) {
        writeIntLittleEndian(v.x)
        writeIntLittleEndian(v.y)
        writeIntLittleEndian(v.z)
    }

    @Throws(IOException::class)
    fun OutputStream.writeVector3b(v: GridPoint3) {
        write(v.x)
        write(v.y)
        write(v.z)
    }

    @Throws(IOException::class)
    fun OutputStream.writeString(s: String) {
        writeIntLittleEndian(s.length)
        write(s.toByteArray(Charsets.UTF_8))
    }

    @Throws(IOException::class)
    fun OutputStream.writeDictionary(dict: Map<String, String>) {
        writeIntLittleEndian(dict.size)
        for ((key, value) in dict) {
            writeString(key)
            writeString(value)
        }
    }

}