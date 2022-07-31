package ru.spbstu.application.steps.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.Markdown
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.scamper.handleScamper
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.telegram.IdeaGenerationStrings
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.MyRanking
import ru.spbstu.application.telegram.sendPhotoResource
import ru.spbstu.application.telegram.waitTextFrom
import ru.spbstu.application.trendyfriendy.sendTrendyFriendyApp

private val userRepository: UserRepository by GlobalContext.get().inject()
private val steps = listOf(Strings.Step1, Strings.Step2, Strings.Step3, Strings.Step4)
private val ideaGenerationMethods = listOf(
    IdeaGenerationStrings.Bisociation,
    IdeaGenerationStrings.DelphiMethod,
    IdeaGenerationStrings.BrainstormMethod,
    IdeaGenerationStrings.Scamper,
    IdeaGenerationStrings.TrendyFriendy,
    IdeaGenerationStrings.BackToSteps
)

suspend fun BehaviourContext.handleSteps(message: CommonMessage<TextContent>) {
    val user = userRepository.get(User.Id(message.chat.id.chatId)) ?: return
    sendTextMessage(
        message.chat.id,
        Strings.ChooseStep,
        replyMarkup = replyKeyboard(
            resizeKeyboard = true,
            oneTimeKeyboard = true
        )
        {
            row { simpleButton(Strings.GetMyStats) }
            steps.take(user.availableStepsCount.toInt()).chunked(2).forEach {
                row {
                    it.forEach { simpleButton(it) }
                }
            }
        }
    )
}

suspend fun BehaviourContext.handleStep1(message: CommonMessage<TextContent>) {
    sendTextMessage(
        message.chat.id,
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

suspend fun BehaviourContext.handleIdeaGenerationMethods(message: CommonMessage<TextContent>) {
    val method = message.content.text

    IdeaGenerationStrings.IdeaGenerationWithDescription.getValue(message.content.text).description.forEach() {
        waitTextFrom(
            message.chat,
            SendTextMessage(
                chatId = message.chat.id,
                text = it.first,
                parseMode = Markdown,
                replyMarkup = ReplyKeyboardMarkup(
                    buttons = listOf(SimpleKeyboardButton(it.second)).toTypedArray(),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                )
            )
        ).first { content -> content.text == it.second }
    }
    bot.sendPhotoResource(
        message.chat,
        IdeaGenerationStrings.IdeaGenerationWithDescription.getValue(method).pathToIllustration
    )
    when (method) {
        IdeaGenerationStrings.TrendyFriendy -> {
            sendTrendyFriendyApp(message.chat)
        }
        IdeaGenerationStrings.Scamper -> {
            handleScamper(message.chat)
        }
        else -> {
            giveBonusWithMessage(
                message.chat.id,
                Strings.BonusTypesByString[method]!!,
                Step(1)
            )
            handleStep1(message)
        }
    }
}

suspend fun BehaviourContext.handleStats(message: CommonMessage<TextContent>) {
    val sortedUsers = userRepository.sortByAmountOfCoins()
    val user = userRepository.get(User.Id(message.chat.id.chatId)) ?: return
    sendTextMessage(message.chat.id, MyRanking(sortedUsers.size, sortedUsers.indexOf(user) + 1, user.amountOfCoins))
    handleSteps(message)
}
