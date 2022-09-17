package ru.spbstu.application.admin.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import ru.spbstu.application.auth.entities.users.AdminUser
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.entities.state.AdminMenu
import ru.spbstu.application.telegram.entities.state.EmptyState

fun StateMachineBuilder.adminCommands() {
    role<AdminUser> {
        anyState {
            onCommand("admin", description = Strings.Help.Admin) {
                setState(AdminMenu)
            }
        }
        state<AdminMenu> {
            onTransition {
                sendTextMessage(
                    it,
                    text = Strings.AdminPanel.Header,
                    replyMarkup = createAdminPanel()
                )
            }
            onCommand("steps", Strings.Help.Steps) { setState(EmptyState) }
            onText(Strings.BackToSteps) { setState(EmptyState) }
        }
    }
    stepDurationCommand()
    uploadPhoneNumbersCommand()
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
        row {
            simpleButton(Strings.BackToSteps)
        }
    }
}
