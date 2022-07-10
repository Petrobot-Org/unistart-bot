package ru.spbstu.application.auth.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.usecases.IsSubscribedUseCase
import ru.spbstu.application.telegram.Strings
import java.time.Instant

private val isSubscribed by GlobalContext.get().inject<IsSubscribedUseCase>()

suspend fun BehaviourContext.requireSubscription(message: CommonMessage<*>) {
    require (isSubscribed(User.Id(message.chat.id.chatId), Instant.now())) {
        sendTextMessage(message.chat, Strings.NotSubscribed)
    }
}
