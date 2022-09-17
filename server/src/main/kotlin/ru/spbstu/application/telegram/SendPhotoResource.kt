package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.requests.abstracts.FileId
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageIdentifier
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.message.ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.PhotoContent
import io.ktor.utils.io.streams.*

private val resourceFileIds = mutableMapOf<String, FileId>()

suspend fun TelegramBot.sendPhotoResource(
    chat: ChatId,
    resourcePath: String,
    text: String? = null,
    parseMode: ParseMode? = null,
    disableNotification: Boolean = false,
    protectContent: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
): ContentMessage<PhotoContent> {
    val fileId = resourceFileIds[resourcePath]
    return if (fileId != null) {
        sendPhoto(
            chat,
            fileId,
            text,
            parseMode,
            disableNotification,
            protectContent,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    } else {
        val file = InputFile.fromInput(resourcePath) {
            object {}.javaClass.getResourceAsStream(resourcePath)!!.asInput()
        }
        sendPhoto(
            chat,
            file,
            text,
            parseMode,
            disableNotification,
            protectContent,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        ).also { resourceFileIds[resourcePath] = it.content.media.fileId }
    }
}
