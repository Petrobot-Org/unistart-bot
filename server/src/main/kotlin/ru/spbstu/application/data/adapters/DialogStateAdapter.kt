package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import ru.spbstu.application.telegram.entities.state.DialogState
import ru.spbstu.application.telegram.entities.state.EmptyState

@OptIn(ExperimentalSerializationApi::class)
object DialogStateAdapter : ColumnAdapter<DialogState, ByteArray> {
    override fun decode(databaseValue: ByteArray): DialogState {
        return runCatching {
            Cbor.decodeFromByteArray<DialogState>(databaseValue)
        }.getOrDefault(EmptyState)
    }

    override fun encode(value: DialogState): ByteArray {
        return Cbor.encodeToByteArray(value)
    }
}
