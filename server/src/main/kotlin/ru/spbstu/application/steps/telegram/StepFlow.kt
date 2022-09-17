package ru.spbstu.application.steps.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.Markdown
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.users.SubscribedUser
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.scamper.scamper
import ru.spbstu.application.scamper.startScamper
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.telegram.*
import ru.spbstu.application.telegram.Strings.MyRanking
import ru.spbstu.application.telegram.entities.state.EmptyState
import ru.spbstu.application.telegram.entities.state.IdeaGenerationDescriptionState
import ru.spbstu.application.telegram.entities.state.IdeaGenerationMenu
import ru.spbstu.application.telegram.entities.state.TrendyFriendyState
import ru.spbstu.application.trendyfriendy.trendyFriendy

private val userRepository: UserRepository by GlobalContext.get().inject()
private val steps = listOf(Strings.Step1, Strings.Step2, Strings.Step3, Strings.Step4)
private val ideaGenerationMethods = listOf(
    IdeaGenerationStrings.Bisociation,
    IdeaGenerationStrings.DelphiMethod,
    IdeaGenerationStrings.BrainstormMethod,
    IdeaGenerationStrings.Scamper,
    IdeaGenerationStrings.TrendyFriendy,
    Strings.BackToSteps
)

suspend fun RequestsExecutor.sendAvailableSteps(chat: ChatId, user: SubscribedUser) {
    sendTextMessage(
        chat,
        Strings.ChooseStep,
        replyMarkup = replyKeyboard(
            resizeKeyboard = true,
            oneTimeKeyboard = true
        ) {
            row { simpleButton(Strings.GetMyStats) }
            steps.take(user.availableStepsCount.toInt()).chunked(2).forEach {
                row {
                    it.forEach { simpleButton(it) }
                }
            }
        }
    )
}

fun RoleFilterBuilder<SubscribedUser>.step1() {
    trendyFriendy()
    scamper()
    state<EmptyState> {
        onText(Strings.Step1) {
            setState(IdeaGenerationMenu)
        }
    }
    state<IdeaGenerationMenu> {
        onTransition {
            sendTextMessage(
                it,
                IdeaGenerationStrings.ChooseIdeaGeneration,
                replyMarkup = replyKeyboard(
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                ) {
                    ideaGenerationMethods.chunked(2).forEach {
                        row {
                            it.forEach { simpleButton(it) }
                        }
                    }
                }
            )
        }
        onText(*IdeaGenerationStrings.IdeaGenerationWithDescription.keys.toTypedArray()) {
            setState(IdeaGenerationDescriptionState(method = it.content.text))
        }
        onText(Strings.BackToSteps) {
            setState(EmptyState)
        }
    }
    state<IdeaGenerationDescriptionState> {
        onTransition {
            val description = IdeaGenerationStrings.IdeaGenerationWithDescription
                .getValue(state.method)
                .description.getOrNull(state.index) ?: run {
                sendPhotoResource(
                    it,
                    IdeaGenerationStrings.IdeaGenerationWithDescription.getValue(state.method).pathToIllustration
                )
                when (state.method) {
                    IdeaGenerationStrings.TrendyFriendy -> {
                        setState(TrendyFriendyState)
                    }

                    IdeaGenerationStrings.Scamper -> {
                        startScamper(it)
                    }

                    else -> {
                        giveBonusWithMessage(
                            it,
                            Strings.BonusTypesByString.getValue(state.method),
                            Step(1)
                        )
                        setState(IdeaGenerationMenu)
                    }
                }
                return@onTransition
            }
            sendTextMessage(
                chatId = it,
                text = description.first,
                parseMode = Markdown,
                replyMarkup = ReplyKeyboardMarkup(
                    buttons = listOf(SimpleKeyboardButton(description.second)).toTypedArray(),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                )
            )
        }
        onText {
            setState(state.copy(index = state.index + 1))
        }
    }
}

suspend fun StatefulContext<*, SubscribedUser>.handleStats(message: CommonMessage<TextContent>) {
    val sortedUsers = userRepository.sortByAmountOfCoins()
    sendTextMessage(
        message.chat.id,
        MyRanking(sortedUsers.size, sortedUsers.indexOfFirst { it.id == user.id } + 1, user.amountOfCoins)
    )
    sendAvailableSteps(message.chat.id, user)
}
