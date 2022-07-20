package ru.spbstu.application.admin

import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object Zip {
    fun extract(inputStream: InputStream, destination: String): Boolean {
        return true
    }

    fun filenames(inputStream: InputStream): List<String>? = try {
        ZipInputStream(inputStream).use { zipInputStream ->
            buildList {
                zipInputStream.forEach { add(it.name) }
            }
        }
    } catch (e: Exception) {
        null
    }
}

private fun ZipInputStream.forEach(body: (ZipEntry) -> Unit) {
    while (true) {
        body(nextEntry ?: break)
    }
}
