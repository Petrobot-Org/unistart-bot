package ru.spbstu.application.trendyfriendy

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import java.io.FileInputStream

class HotReloader(val configPath: String) {
    var config = load()

    fun reload() {
        config = load()
    }

    private fun load(): TrendyFriendyConfig? {
        return try {
            FileInputStream(configPath).use {
                Yaml.default.decodeFromStream(it)
            }
        } catch (e: java.lang.Exception) {
            null
        }
    }
}
