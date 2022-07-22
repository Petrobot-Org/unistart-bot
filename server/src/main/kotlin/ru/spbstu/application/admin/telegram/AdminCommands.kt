package ru.spbstu.application.admin.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.chat.Chat
import ru.spbstu.application.telegram.HelpContext
import ru.spbstu.application.telegram.Strings

context(HelpContext)
suspend fun BehaviourContext.adminCommands() {
    onAdminCommand("admin", Strings.Help.Admin) { handleAdmin(it.chat) }
    uploadPhoneNumbersCommand()
    stepDurationCommand()
    statisticsSpreadsheetCommand()
    listOfAdminsCommand()
    uploadTrendsCommand()
}

fun createAdminPanel(): ReplyKeyboardMarkup {
    return replyKeyboard(
        resizeKeyboard = true
    ) {
        row {
            simpleButton(Strings.AdminPanel.Menu.StepDuration)
        }
        row {
            simpleButton(Strings.AdminPanel.Menu.UploadPhoneNumbers)
        }
        row {
            simpleButton(Strings.AdminPanel.Menu.StatisticsSpreadsheet)
        }
        row {
            simpleButton(Strings.AdminPanel.Menu.ListOfAdmins)
        }
        row {
            simpleButton(Strings.AdminPanel.Menu.UploadTrends)
        }
    }
}

private suspend fun BehaviourContext.handleAdmin(chat: Chat) {
    sendTextMessage(
        chat = chat,
        text = Strings.AdminPanel.Header,
        replyMarkup = createAdminPanel()
    )
}
