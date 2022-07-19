package ru.spbstu.application.trendyfriendy

import dev.inmo.tgbotapi.types.toChatId
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.usecases.CheckAndUpdateBonusAccountingUseCase
import ru.spbstu.application.telegram.TelegramBot
import trendyfriendy.Idea
import trendyfriendy.TrendCard
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

private val checkAndUpdateBonusAccounting: CheckAndUpdateBonusAccountingUseCase by GlobalContext.get().inject()

class TrendyFriendyService(
    private val telegramBot: TelegramBot,
    private val config: TrendyFriendyConfig
) {
    private val ideas = ConcurrentHashMap<Long, List<Idea>>()
    private val cards = ConcurrentHashMap<Long, List<TrendCard>>()

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
        sendTrendyFriendyIdeas(telegramBot.bot, userId, ideas[userId] ?: emptyList())
        checkAndUpdateBonusAccounting(
            userId.toChatId(),
            User.Id(userId),
            BonusType.TrendyFriendy,
            Step(1),
            Instant.now()
        )
        ideas.remove(userId)
        cards.remove(userId)
    }
}
