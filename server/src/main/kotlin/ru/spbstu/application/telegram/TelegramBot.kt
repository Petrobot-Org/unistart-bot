package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.spbstu.application.auth.telegram.handleStart
import trendyfriendy.Idea

class TelegramBot(token: TelegramToken) {
    private val bot = telegramBot(token.value)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun start() {
        coroutineScope.launch {
            bot.buildBehaviourWithLongPolling(
                defaultExceptionsHandler = { it.printStackTrace() }
            ) {
                onCommand("start", scenarioReceiver = { handleStart(it) })
                onCommand("game") {
                    sendTextMessage(
                        it.chat,
                        "game",
                        replyMarkup = inlineKeyboard {
                            row {
                                webAppButton("Open", WebAppInfo("https://unistart.ithersta.com/trendy-friendy"))
                            }
                        }
                    )
                }
            }.join()
        }
    }

    fun sendTrendyFriendyIdeas(userId: Long, ideas: List<Idea>) {
        coroutineScope.launch {
            bot.sendTextMessage(userId.toChatId(), ideas.joinToString(separator = "\n") { it.text })
        }
    }
}

@JvmInline
value class TelegramToken(val value: String)
