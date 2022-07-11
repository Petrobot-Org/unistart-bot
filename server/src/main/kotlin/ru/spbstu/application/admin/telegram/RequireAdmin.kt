package ru.spbstu.application.admin.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTwoTypesReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.CommonMessageFilterExcludeMediaGroups
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.MessageFilterByChat
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.CommonMessageFilter
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.ByChatMessageMarkerFactory
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.MarkerFactory
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.Job
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.usecases.IsAdminUseCase
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.telegram.Strings

private val isAdmin: IsAdminUseCase by GlobalContext.get().inject()

suspend fun BehaviourContext.requireAdmin(chat: Chat) {
    require(isAdmin(User.Id(chat.id.chatId))) {
        sendTextMessage(chat, Strings.UnauthorizedError)
    }
}

suspend fun <BC : BehaviourContext> BC.onAdminCommand(
    command: String,
    requireOnlyCommandInMessage: Boolean = true,
    initialFilter: CommonMessageFilter<TextContent>? = CommonMessageFilterExcludeMediaGroups,
    subcontextUpdatesFilter: CustomBehaviourContextAndTwoTypesReceiver<BC, Boolean, CommonMessage<TextContent>, Update> = MessageFilterByChat,
    markerFactory: MarkerFactory<in CommonMessage<TextContent>, Any> = ByChatMessageMarkerFactory,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, CommonMessage<TextContent>>
): Job = onCommand(
    command,
    requireOnlyCommandInMessage,
    initialFilter,
    subcontextUpdatesFilter,
    markerFactory
) {
    requireAdmin(it.chat)
    scenarioReceiver(this, it)
}
