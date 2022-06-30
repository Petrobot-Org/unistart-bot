package ru.spbstu.application.trendyfriendy

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import ru.spbstu.application.telegram.Strings
import trendyfriendy.Idea

suspend fun BehaviourContext.sendTrendyFriendyApp(chat: Chat) {
    sendTextMessage(
        chat,
        Strings.TrendyFriendyDescription,
        replyMarkup = inlineKeyboard {
            row {
                webAppButton(Strings.TrendyFriendyOpen, WebAppInfo("https://unistart.ithersta.com/trendy-friendy"))
            }
        }
    )
}

suspend fun sendTrendyFriendyIdeas(bot: TelegramBot, userId: Long, ideas: List<Idea>) {
    bot.sendTextMessage(userId.toChatId(), ideas.joinToString(separator = "\n") { it.text })
}
