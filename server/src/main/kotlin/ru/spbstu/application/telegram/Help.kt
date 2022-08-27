package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTwoTypesReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.CommonMessageFilterExcludeMediaGroups
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.MessageFilterByChat
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.CommonMessageFilter
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.ByChatMessageMarkerFactory
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.MarkerFactory
import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import dev.inmo.tgbotapi.extensions.utils.formatting.regularln
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.commands.BotCommandScope
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.Job
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.usecases.IsNonRootAdminUseCase
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.usecases.IsSubscribedUseCase
import java.time.Instant

private val isAdmin: IsNonRootAdminUseCase by GlobalContext.get().inject()
private val isSubscribed: IsSubscribedUseCase by GlobalContext.get().inject()

enum class Role {
    Admin, Subscriber, Everyone
}

data class HelpEntry(
    val command: String,
    val description: String,
    val role: Role
)

fun interface HelpContext {
    fun addHelpEntry(helpEntry: HelpEntry)
}

suspend fun BehaviourContext.provideHelp(block: suspend context(HelpContext) () -> Unit) {
    val helpEntries = mutableListOf<HelpEntry>()
    block { helpEntries.add(it) }
    onCommand("help") { handleHelp(it.chat, helpEntries) }
    onContentMessage { setCommands(it.chat, helpEntries) }
}

private suspend fun BehaviourContext.setCommands(chat: Chat, helpEntries: List<HelpEntry>) {
    setMyCommands(
        commands = filterAvailable(chat, helpEntries).map { BotCommand(it.command, it.description) },
        scope = BotCommandScope.Chat(chat.id)
    )
}

private suspend fun BehaviourContext.handleHelp(chat: Chat, helpEntries: List<HelpEntry>) {
    sendTextMessage(
    chat,
    buildEntities {
        regularln(Strings.Help.Header)
        filterAvailable(chat, helpEntries).forEach {
            regularln("/${it.command} – ${it.description}")
        }
    }
    )
}

private fun filterAvailable(chat: Chat, helpEntries: List<HelpEntry>): List<HelpEntry> {
    val userId = User.Id(chat.id.chatId)
    val now = Instant.now()
    return helpEntries.filter {
        when (it.role) {
            Role.Admin -> isAdmin(userId)
            Role.Subscriber -> isSubscribed(userId, now)
            Role.Everyone -> true
        }
    }
}

context(HelpContext)
suspend fun <BC : BehaviourContext> BC.onCommandWithHelp(
    command: String,
    description: String,
    requireOnlyCommandInMessage: Boolean = true,
    initialFilter: CommonMessageFilter<TextContent>? = CommonMessageFilterExcludeMediaGroups,
    subcontextUpdatesFilter: CustomBehaviourContextAndTwoTypesReceiver<BC, Boolean, CommonMessage<TextContent>, Update> = MessageFilterByChat,
    markerFactory: MarkerFactory<in CommonMessage<TextContent>, Any> = ByChatMessageMarkerFactory,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, CommonMessage<TextContent>>
): Job {
    addHelpEntry(HelpEntry(command, description, Role.Everyone))
    return onCommand(
        command,
        requireOnlyCommandInMessage,
        initialFilter,
        subcontextUpdatesFilter,
        markerFactory
    ) {
        scenarioReceiver(this, it)
    }
}
