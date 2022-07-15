package ru.spbstu.application.steps.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.requests.DownloadFile
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
//import dev.inmo.tgbotapi.types.files.DocumentFile
//import dev.inmo.tgbotapi.types.files.File
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
///import jdk.javadoc.internal.tool.Main.execute
//import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.scheduling.DefaultIoScheduler.execute
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.auth.telegram.requireSubscription
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.MyRanking
import ru.spbstu.application.telegram.waitTextFrom
import ru.spbstu.application.trendyfriendy.sendTrendyFriendyApp
import java.io.File

private val userRepository: UserRepository by GlobalContext.get().inject()
private val steps = listOf(Strings.Step1, Strings.Step2, Strings.Step3, Strings.Step4)
private val ideaGenerationMethods = listOf(
    Strings.Bisociation,
    Strings.DelphiMethod,
    Strings.BrainstormMethod,
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

suspend fun BehaviourContext.handleIdeaGenerationMethods(message: CommonMessage<TextContent>) {
    val method = message.content.text
    waitTextFrom(
        message.chat,
        SendTextMessage(
            message.chat.id,
            Strings.IdeaGenerationWithDescription.getValue(message.content.text).description,
            replyMarkup = ReplyKeyboardMarkup(
                buttons = listOf(SimpleKeyboardButton(Strings.HowDoesItWork)).toTypedArray(),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).onEach {
        if (it.text == Strings.HowDoesItWork) {
            sendTextMessage(
                message.chat.id,
                Strings.IdeaGenerationWithDescription.getValue(message.content.text).howToUse
            )
            val file = File(Strings.IdeaGenerationWithDescription.getValue(message.content.text).pathToIllustration)
            sendPhoto(message.chat.id, InputFile.fromFile(file))
            //TODO: тут видимо над начислять бонус
        }
    }.firstNotNull()
    if (method == Strings.TrendyFriendy) {
        sendTrendyFriendyApp(message.chat)
    } else {
        handleStep1(message)
    }
}

suspend fun BehaviourContext.handleStats(message: CommonMessage<TextContent>) {
    val sortedUsers = userRepository.sortByAmountOfCoins()
    val user = userRepository.get(User.Id(message.chat.id.chatId)) ?: return
    sendTextMessage(message.chat.id, MyRanking(sortedUsers.size, sortedUsers.indexOf(user) + 1, user.amountOfCoins))
    handleSteps(message)
}
