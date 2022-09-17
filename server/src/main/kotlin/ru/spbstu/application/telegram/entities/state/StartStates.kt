package ru.spbstu.application.telegram.entities.state

import kotlinx.serialization.Serializable
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber

object StartStates {
    @Serializable
    object WaitingForContact : DialogState

    @Serializable
    class WaitingForAvatar(
        val phoneNumber: PhoneNumber
    ) : DialogState

    @Serializable
    class WaitingForOccupation(
        val phoneNumber: PhoneNumber,
        val avatar: Avatar
    ) : DialogState

    @Serializable
    class WaitingForStartLevel(
        val phoneNumber: PhoneNumber,
        val avatar: Avatar,
        val occupation: Occupation
    ) : DialogState
}
