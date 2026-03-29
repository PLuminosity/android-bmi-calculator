package cz.spseiostrava.pham.vypocet.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

/**
 * Lightweight quote fetcher – uses only the platform's HttpURLConnection
 * so no Retrofit or OkHttp dependency is needed.
 *
 * Endpoint: https://zenquotes.io/api/random
 * Returns a JSON array with one object: [{"q":"…","a":"…","h":"…"}]
 */
object QuoteApiService {

    private const val API_URL = "https://zenquotes.io/api/random"
    private const val TIMEOUT_MS = 8_000

    data class Quote(val text: String, val author: String)

    /**
     * Fetches a random quote on [Dispatchers.IO].
     * Returns null on any network or parse error so the caller can show a fallback.
     */
    suspend fun fetchRandomQuote(): Quote? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(API_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout    = TIMEOUT_MS
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null

            val body = connection.inputStream.bufferedReader().readText()
            val json = JSONArray(body).getJSONObject(0)
            val text   = json.getString("q").trim()
            val author = json.getString("a").trim()

            // ZenQuotes returns "unknown" when the author is not known
            Quote(
                text   = text,
                author = if (author.equals("unknown", ignoreCase = true)) "" else author
            )
        } catch (e: Exception) {
            null   // Network error, JSON parse error, etc.
        } finally {
            connection?.disconnect()
        }
    }
}
