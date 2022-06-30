import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
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
        expectSuccess = true
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                window.alert(exception.message.toString())
            }
        }
    }

    suspend fun addIdea(idea: Idea): IdeaResponse {
        return client.post("/trendy-friendy/ideas") {
            contentType(ContentType.Application.Json)
            setBody(idea)
        }.body()
    }

    suspend fun getIdeaCount(): IdeaResponse {
        return client.get("/trendy-friendy/ideas").body()
    }
}
