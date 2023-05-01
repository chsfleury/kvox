package fr.chsfleury.kvox

interface VoxFile<Root> {
    val version: Int
    val root: Root
}