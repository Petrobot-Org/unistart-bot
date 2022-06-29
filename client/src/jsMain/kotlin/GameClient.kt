import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import trendyfriendy.Idea
import trendyfriendy.IdeaResponse

class GameClient(initData: String, hash: String) {
    private val client = HttpClient {
        install(ContentNegotiation) { json() }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(initData, hash)
                }
            }
        }
    }

    suspend fun addIdea(idea: Idea): IdeaResponse {
        return client.post("/trendy-friendy/idea") { setBody(idea) }.body()
    }
}
