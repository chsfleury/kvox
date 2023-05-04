package fr.chsfleury.kvox

import com.fasterxml.jackson.databind.ObjectMapper
import fr.chsfleury.kvox.impl.DefaultVoxReader
import fr.chsfleury.kvox.json.VoxFileJson
import java.io.FileInputStream
import java.io.FileOutputStream

object JsonDebug {

    private val objectWriter = ObjectMapper()
        .writerWithDefaultPrettyPrinter()

    fun debug(voxFile: VoxFile<*>) {
        FileOutputStream("target/debug.json").use {
            objectWriter.writeValue(it, voxFile)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        DefaultVoxReader(FileInputStream("models/${args[0]}")).use {
            val voxFile = it.read()
            objectWriter.writeValue(System.out, VoxFileJson(voxFile))
        }
    }

}