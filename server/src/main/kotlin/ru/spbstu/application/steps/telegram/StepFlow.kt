package ru.spbstu.application.steps.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.auth.telegram.requireSubscription
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.MyRanking

private val userRepository: UserRepository by GlobalContext.get().inject()
private val steps = listOf(Strings.Step1, Strings.Step2, Strings.Step3, Strings.Step4)
private val ideaGenerationMethods = listOf(
    Strings.Bisociation,
    Strings.DelphiBrainstormMethod,
    Strings.Scamper,
    Strings.TrendyFriendy,
    Strings.BackToSteps
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
        Strings.ChooseIdeaGeneration,
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

suspend fun BehaviourContext.handleStats(message: CommonMessage<TextContent>) {
    val sortedUsers = userRepository.sortByAmountOfCoins()
    val user = userRepository.get(User.Id(message.chat.id.chatId)) ?: return
    sendTextMessage(message.chat.id, MyRanking(sortedUsers.size, sortedUsers.indexOf(user) + 1, user.amountOfCoins))
    handleSteps(message)
}
