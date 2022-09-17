package ru.spbstu.application.telegram.repository

import com.ithersta.tgbotapi.fsm.repository.StateRepository
import dev.inmo.tgbotapi.types.UserId
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase
import ru.spbstu.application.telegram.entities.state.DialogState
import ru.spbstu.application.telegram.entities.state.EmptyState

class UserDialogStateRepository(
    private val appDatabase: AppDatabase
) : StateRepository<UserId, DialogState> {
    override fun get(key: UserId): DialogState {
        val userId = User.Id(key.chatId)
        return appDatabase.userDialogStateQueries.get(userId).executeAsOneOrNull() ?: EmptyState
    }

    override fun set(key: UserId, state: DialogState) {
        val userId = User.Id(key.chatId)
        appDatabase.userDialogStateQueries.set(userId, state)
    }
}
