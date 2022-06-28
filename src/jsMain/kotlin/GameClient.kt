import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

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
}
