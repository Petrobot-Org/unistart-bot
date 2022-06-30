package ru.spbstu.application.trendyfriendy

import trendyfriendy.Idea
import java.util.concurrent.ConcurrentHashMap

class TrendyFriendyService {
    private val ideas = ConcurrentHashMap<Long, List<Idea>>()

    fun addIdea(userId: Long, idea: Idea): Int {
        val newIdeas = (ideas[userId] ?: listOf()) + idea
        ideas[userId] = newIdeas
        return newIdeas.size
    }

    fun getIdeaCount(userId: Long): Int {
        return ideas[userId]?.size ?: 0
    }
}
