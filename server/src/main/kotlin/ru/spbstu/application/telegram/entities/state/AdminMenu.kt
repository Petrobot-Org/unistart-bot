package ru.spbstu.application.telegram.entities.state

import dev.inmo.tgbotapi.types.MessageIdentifier
import kotlinx.serialization.Serializable
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.serializers.InstantSerializer
import ru.spbstu.application.steps.entities.Step
import java.time.Instant

@Serializable
object AdminMenu : DialogState

@Serializable
class WaitingForStepDuration(val step: Step, val messageId: MessageIdentifier) : DialogState

object UploadPhoneNumbersState {
    @Serializable
    object WaitingForDocument : DialogState

    @Serializable
    class WaitingForStart(val phoneNumbers: List<PhoneNumber>) : DialogState

    @Serializable
    class WaitingForDuration(
        val phoneNumbers: List<PhoneNumber>,
        @Serializable(with = InstantSerializer::class)
        val start: Instant
    ) : DialogState
}

@Serializable
class WaitingForAdminToAdd(
    val messageId: MessageIdentifier
) : DialogState

@Serializable
class WaitingForDeleteConfirmation(
    val userId: User.Id,
    val messageId: MessageIdentifier
) : DialogState

@Serializable
object WaitingForTrendsDocument : DialogState
