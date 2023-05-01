package fr.chsfleury.kvox

import java.io.Closeable
import java.io.IOException

interface VoxReader<Root>: Closeable {
    @Throws(IOException::class)
    fun read(): VoxFile<Root>
}