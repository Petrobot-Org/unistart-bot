package ru.spbstu.application.steps.telegram

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.telegram.Strings

private val userRepository: UserRepository by GlobalContext.get().inject()
private val steps = listOf(Strings.Step1, Strings.Step2, Strings.Step3, Strings.Step4)
private val ideaGenerationMethods = listOf(
    Strings.Bisociation,
    Strings.DelphiBrainstormMethod,
    Strings.Scamper,
    Strings.TrendyFriendy,
    Strings.BackToSteps
)

suspend fun BehaviourContext.steps(message: CommonMessage<TextContent>) {
    val user = userRepository.get(User.Id(message.chat.id.chatId)) ?: return
    val numberOfAvailableSteps = user.availableStepsCount.toInt()
    val numbToSecRow = numberOfAvailableSteps / 2 + numberOfAvailableSteps % 2
    val buttonsToSecRow= steps.take(numbToSecRow)
    var buttonsToThirdRow = emptyList<String>()
    if (numberOfAvailableSteps>2) {
        buttonsToThirdRow = steps.subList(2, numberOfAvailableSteps)
    }
    val selectedStep = waitText(
        SendTextMessage(
            message.chat.id,
            Strings.ChooseStep,
            replyMarkup = replyKeyboard(
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
            {
                row { simpleButton(Strings.GetMyStats) }
                row { buttonsToSecRow.map{simpleButton(it)}}
                row { buttonsToThirdRow.map{simpleButton(it)}}
            }
        )
    ).first { it.text in steps || it.text == Strings.GetMyStats }.text
    when (selectedStep) {
        Strings.Step1 -> handleStep1(message)
        Strings.GetMyStats -> handleStats(message)
    }
}

suspend fun BehaviourContext.handleStep1(message: CommonMessage<TextContent>) {
    val stage = waitText(
        SendTextMessage(
            message.chat.id,
            Strings.ChooseIdeaGeneration,
            replyMarkup = replyKeyboard(
                resizeKeyboard = true,
                oneTimeKeyboard = true
            ) {
                row {
                    simpleButton(Strings.Bisociation)
                    simpleButton(Strings.DelphiBrainstormMethod)
                }
                row {
                    simpleButton(Strings.Scamper)
                    simpleButton(Strings.TrendyFriendy)
                }
                row {
                    simpleButton(Strings.BackToSteps)
                }
            }
        )
    ).first { it.text in ideaGenerationMethods }.text
    when (stage) {
        Strings.BackToSteps -> steps(message)
    }
}

