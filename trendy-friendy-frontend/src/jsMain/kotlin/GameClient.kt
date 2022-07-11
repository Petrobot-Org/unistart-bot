import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import trendyfriendy.Idea
import trendyfriendy.IdeaResponse
import trendyfriendy.TrendCard

class GameClient(initData: String, hash: String) {
    sealed interface Error {
        object NotAuthorized : Error
        data class Other(val message: String?) : Error
    }

    private val client = HttpClient {
        expectSuccess = true
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                if ((exception as? ClientRequestException)?.response?.status == HttpStatusCode.Unauthorized) {
                    _errorFlow.tryEmit(Error.NotAuthorized)
                } else if (exception !is CancellationException) {
                    _errorFlow.tryEmit(Error.Other(exception.message))
                }
            }
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
        install(ContentNegotiation) { json() }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(initData, hash)
                }
                sendWithoutRequest { true }
            }
        }
    }

    private val _errorFlow = MutableSharedFlow<Error>(replay = 1)
    val errorFlow = _errorFlow.asSharedFlow()

    suspend fun addIdea(idea: Idea): IdeaResponse {
        return client.post("/trendy-friendy/ideas") { setBody(idea) }.body()
    }

    suspend fun getIdeaCount(): IdeaResponse {
        return client.get("/trendy-friendy/ideas").body()
    }

    suspend fun generateCards(fromSets: Set<String>): List<TrendCard> {
        return client.post("/trendy-friendy/cards") { setBody(fromSets) }.body()
    }

    suspend fun getCards(): List<TrendCard> {
        return client.get("/trendy-friendy/cards").body()
    }

    suspend fun getSets(): Set<String> {
        return client.get("/trendy-friendy/cards/sets").body()
    }

    suspend fun finish() {
        client.post("/trendy-friendy/ideas/finish")
    }
}
