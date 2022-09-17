package ru.spbstu.application.telegram.commands

import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import ru.spbstu.application.auth.entities.users.BaseUser
import ru.spbstu.application.telegram.StateFilterBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.entities.state.DialogState
import ru.spbstu.application.telegram.entities.state.EmptyState

fun StateFilterBuilder<DialogState, BaseUser>.cancelCommand() {
    onCommand("cancel", Strings.Help.Cancel) {
        sendTextMessage(
            it.chat,
            text = if (state == EmptyState) {
                Strings.Cancel.NothingToCancel
            } else {
                Strings.Cancel.Success
            },
            replyMarkup = ReplyKeyboardRemove()
        )
        setState(EmptyState)
    }
}
