package ru.spbstu.application.telegram.commands

import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import ru.spbstu.application.auth.entities.users.BaseUser
import ru.spbstu.application.telegram.StateFilterBuilder
import ru.spbstu.application.telegram.entities.state.DialogState

fun StateFilterBuilder<DialogState, BaseUser>.stateCommand() {
    onCommand("state", null) {
        sendTextMessage(it.chat, state.toString())
    }
}
