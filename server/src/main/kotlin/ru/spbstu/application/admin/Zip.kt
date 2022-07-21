package ru.spbstu.application.admin

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object Zip {
    fun extractFlat(inputStream: InputStream, destination: String, predicate: (String) -> Boolean) {
        val directory = File(destination)
        directory.mkdirs()
        ZipInputStream(inputStream).use { zipInputStream ->
            zipInputStream.forEach { entry ->
                if (!entry.isDirectory && predicate(entry.name) && !entry.name.contains('\\')) {
                    val file = File(destination, entry.name)
                    FileOutputStream(file, false).use { it.writeEntry(zipInputStream) }
                }
            }
        }
    }

    fun extractOne(inputStream: InputStream, filename: String): ByteArray? {
        try {
            ZipInputStream(inputStream).use { zipInputStream ->
                zipInputStream.forEach { entry ->
                    if (!entry.isDirectory && entry.name == filename) {
                        return ByteArrayOutputStream().apply { writeEntry(zipInputStream) }.toByteArray()
                    }
                }
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    private fun OutputStream.writeEntry(zipInputStream: ZipInputStream) {
        val buffer = ByteArray(1024)
        var len: Int
        while (zipInputStream.read(buffer).also { len = it } > 0) {
            write(buffer, 0, len)
        }
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

private inline fun ZipInputStream.forEach(body: (ZipEntry) -> Unit) {
    while (true) {
        body(nextEntry ?: break)
    }
}
