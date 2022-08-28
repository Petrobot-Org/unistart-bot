package ru.spbstu.application.trendyfriendy

import ru.spbstu.application.telegram.TelegramBot
import trendyfriendy.Idea
import trendyfriendy.TrendCard
import java.util.concurrent.ConcurrentHashMap

class ConfigNotLoaded : Exception("Trendy Friendy config not loaded")

class TrendyFriendyService(
    private val telegramBot: TelegramBot,
    private val configLoader: HotReloader
) {
    private val ideas = ConcurrentHashMap<Long, List<Idea>>()
    private val cards = ConcurrentHashMap<Long, List<TrendCard>>()
    private val config get() = configLoader.config ?: throw ConfigNotLoaded()

    fun addIdea(userId: Long, idea: Idea): Int {
        val newIdeas = (ideas[userId] ?: listOf()) + idea
        ideas[userId] = newIdeas
        return newIdeas.size
    }

    fun getIdeaCount(userId: Long): Int {
        return ideas[userId]?.size ?: 0
    }

    fun generateCards(userId: Long, fromSets: Set<String>): List<TrendCard> {
        val pool = fromSets.fold(emptySequence<TrendCard>()) { acc, s -> acc + config.sets[s]!!.asSequence() }
        val newCards = pool.shuffled().take(config.cardsPerGame).toList()
        cards[userId] = newCards
        return newCards
    }

    fun getCards(userId: Long): List<TrendCard> {
        return cards[userId] ?: emptyList()
    }

    fun getSets(): Set<String> {
        return config.sets.keys
    }

    suspend fun finish(userId: Long) {
        telegramBot.bot.sendTrendyFriendyIdeas(userId, ideas[userId] ?: emptyList())
        ideas.remove(userId)
        cards.remove(userId)
    }
}
