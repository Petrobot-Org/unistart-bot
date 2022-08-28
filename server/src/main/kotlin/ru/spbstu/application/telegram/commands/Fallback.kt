package ru.spbstu.application.telegram.commands

import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings

fun StateMachineBuilder.fallback() {
    anyRole {
        anyState {
            onText {
                sendTextMessage(it.chat, Strings.NoSuchCommand)
                setState(state)
            }
        }
    }
}
