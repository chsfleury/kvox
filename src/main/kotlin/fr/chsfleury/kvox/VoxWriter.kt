package fr.chsfleury.kvox

import java.io.Closeable
import java.io.IOException
import kotlin.jvm.Throws

interface VoxWriter<File: VoxFile<*>>: Closeable {

    @Throws(IOException::class)
    fun write(file: File)

}