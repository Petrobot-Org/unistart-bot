package ru.spbstu.application.trendyfriendy

import ru.spbstu.application.telegram.TelegramBot
import trendyfriendy.Idea
import trendyfriendy.TrendCard
import java.util.concurrent.ConcurrentHashMap

val sampleCards = listOf(
    TrendCard(
        "Data Fabric",
        "The value of data has never been more clear. But often, data " +
                "remains siloed within applications, which means it’s not being used as effectively as possible.\n" +
                "Data fabric integrates data across platforms and users, making data available everywhere it’s needed.",
        "https://1.cms.s81c.com/sites/default/files/2022-06-13/CTA%20photo_DataArchitecture.jpg"
    ),
    TrendCard(
        "Composable Applications",
        "Fusion teams face many challenges: They can lack coding skills, be locked " +
                "into the wrong technologies and are often tasked with fast-paced delivery.\n" +
                "Composable applications are made up of packaged-business capabilities " +
                "(PBCs) or software-defined business objects. PBCs — for example " +
                "representing a patient or digital twin — create reusable modules that " +
                "fusion teams can self-assemble to rapidly create applications, reducing " +
                "time to market.",
        "https://images.unsplash.com/photo-1633469924738-52101af51d87?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80"
    ),
    TrendCard(
        "Generative AI",
        "For the most part, AI is trained to produce conclusions, but true force-" +
                "multiplying technologies can innovate on their own.\n" +
                "Generative AI is a form of AI that learns a digital representation of artifacts " +
                "from sample data and uses it to generate new, original, realistic artifacts " +
                "that retain a likeness to the training data but don’t repeat it. That allows " +
                "generative AI to be an engine of rapid innovation for enterprises.",
        "https://cdn.openai.com/dall-e-2/demos/text2im/soup/portal/digital_art/0.jpg"
    )
)

class TrendyFriendyService(private val telegramBot: TelegramBot) {
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

    fun generateCards(userId: Long, trendCardSetId: Long): List<TrendCard> {
        val newCards = sampleCards.asSequence().shuffled().take(3).toList()
        cards[userId] = newCards
        return newCards
    }

    fun getCards(userId: Long): List<TrendCard> {
        return cards[userId] ?: emptyList()
    }

    fun finish(userId: Long) {
        telegramBot.sendTrendyFriendyIdeas(userId, ideas[userId] ?: emptyList())
        ideas.remove(userId)
        cards.remove(userId)
    }
}
