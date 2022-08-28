package ru.spbstu.application.telegram.entities.state

import kotlinx.serialization.Serializable
import trendyfriendy.Idea

@Serializable
object TrendyFriendyState : DialogState

@Serializable
class TrendyFriendyEndedState(val ideas: List<Idea>) : DialogState
