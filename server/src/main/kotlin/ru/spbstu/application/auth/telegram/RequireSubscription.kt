package ru.spbstu.application.auth.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTwoTypesReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.CommonMessageFilterExcludeMediaGroups
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.MessageFilterByChat
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.CommonMessageFilter
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.ByChatMessageMarkerFactory
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.MarkerFactory
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.Job
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.usecases.IsSubscribedUseCase
import ru.spbstu.application.telegram.HelpContext
import ru.spbstu.application.telegram.HelpEntry
import ru.spbstu.application.telegram.Role
import ru.spbstu.application.telegram.Strings
import java.time.Instant

private val isSubscribed: IsSubscribedUseCase by GlobalContext.get().inject()

suspend fun BehaviourContext.requireSubscription(message: CommonMessage<*>) {
    require(isSubscribed(User.Id(message.chat.id.chatId), Instant.now())) {
        sendTextMessage(message.chat, Strings.NotSubscribed)
    }
}

context(HelpContext)
suspend fun <BC : BehaviourContext> BC.onSubscriberCommand(
    command: String,
    description: String,
    requireOnlyCommandInMessage: Boolean = true,
    initialFilter: CommonMessageFilter<TextContent>? = CommonMessageFilterExcludeMediaGroups,
    subcontextUpdatesFilter: CustomBehaviourContextAndTwoTypesReceiver<BC, Boolean, CommonMessage<TextContent>, Update> = MessageFilterByChat,
    markerFactory: MarkerFactory<in CommonMessage<TextContent>, Any> = ByChatMessageMarkerFactory,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, CommonMessage<TextContent>>
): Job {
    addHelpEntry(HelpEntry(command, description, Role.Subscriber))
    return onCommand(
        command,
        requireOnlyCommandInMessage,
        initialFilter,
        subcontextUpdatesFilter,
        markerFactory
    ) {
        requireSubscription(it)
        scenarioReceiver(this, it)
    }
}

suspend fun <BC : BehaviourContext> BC.onSubscriberText(
    vararg text: String,
    subcontextUpdatesFilter: CustomBehaviourContextAndTwoTypesReceiver<BC, Boolean, CommonMessage<TextContent>, Update> = MessageFilterByChat,
    markerFactory: MarkerFactory<in CommonMessage<TextContent>, Any> = ByChatMessageMarkerFactory,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, CommonMessage<TextContent>>
) = onText(
    initialFilter = { it.content.text in text },
    subcontextUpdatesFilter,
    markerFactory
) {
    requireSubscription(it)
    scenarioReceiver(this, it)
}
