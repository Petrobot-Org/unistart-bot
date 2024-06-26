package ru.spbstu.application.admin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.encodeToStream
import ru.spbstu.application.trendyfriendy.HotReloader
import ru.spbstu.application.trendyfriendy.TrendyFriendyConfig
import java.io.FileOutputStream

class TrendsZip(
    private val trendyFriendyConfigLoader: HotReloader
) {
    sealed interface Result {
        object OK : Result
        object InvalidZip : Result
        object NoXlsx : Result
        object InvalidXlsx : Result
        data class WriteError(val e: Throwable) : Result
        data class BadFormat(val errorRows: List<Int>) : Result
        data class MissingPictures(val filenames: Collection<String>) : Result
        data class TooFewTrends(val minimum: Int) : Result
    }

    fun apply(archive: ByteArray): Result {
        val filenames = Zip.filenames(archive.inputStream()) ?: return Result.InvalidZip
        val xlsxFilename = filenames.find { it.endsWith(".xlsx", ignoreCase = true) } ?: return Result.NoXlsx
        val xlsx = Zip.extractOne(archive.inputStream(), xlsxFilename) ?: return Result.InvalidZip
        val trendCardSets = when (val result = Xlsx.parseTrends(xlsx.inputStream())) {
            is Xlsx.Result.BadFormat -> return Result.BadFormat(result.errorRows)
            is Xlsx.Result.InvalidFile -> return Result.InvalidXlsx
            is Xlsx.Result.OK -> result.value
        }
        val config = when (val result = TrendyFriendyConfig.of(sets = trendCardSets)) {
            is TrendyFriendyConfig.Companion.Result.OK -> result.value
            is TrendyFriendyConfig.Companion.Result.TooFewTrends -> return Result.TooFewTrends(result.minimum)
        }
        val expectedFilenames = trendCardSets.values.flatten().map { it.filename }.toSet()
        val missingFilenames = expectedFilenames - filenames.toSet()
        if (missingFilenames.isNotEmpty()) {
            return Result.MissingPictures(missingFilenames)
        }
        return try {
            write(archive, expectedFilenames, config)
            Result.OK
        } catch (e: Exception) {
            Result.WriteError(e)
        }
    }

    private fun write(
        archive: ByteArray,
        expectedFilenames: Set<String>,
        config: TrendyFriendyConfig
    ) {
        Zip.extractFlat(archive.inputStream(), "trends/") { it in expectedFilenames }
        FileOutputStream(trendyFriendyConfigLoader.configPath, false).use {
            Yaml.default.encodeToStream(config, it)
        }
        trendyFriendyConfigLoader.reload()
    }
}
