package ru.spbstu.application.admin.telegram

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext

suspend fun BehaviourContext.adminCommands() {
    uploadPhoneNumbersCommand()
    stepDurationCommand()
}
