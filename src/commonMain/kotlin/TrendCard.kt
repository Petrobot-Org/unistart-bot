import kotlinx.serialization.Serializable

@Serializable
data class TrendCard(
    val name: String,
    val description: String,
    val url: String
)
