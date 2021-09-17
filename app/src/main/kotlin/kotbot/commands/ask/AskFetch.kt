package kotbot.commands.ask

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotbot.Keys

import java.net.URLEncoder

/***
 * Fetches wolfram data given user message
 *
 * @param message  User's message of the question
 *
 * @return  A pair of strings;
 * The first string is of the short answer,
 * and the second is the image URL for the thumbnail
 */

suspend fun fetchAskData(message: String): Pair<String, String> {
    val formattedQuestion: String = URLEncoder.encode(message, "utf-8")

    println(formattedQuestion)
    val simpleResult: String = HttpClient().use { client ->
        client.get(
            "http://api.wolframalpha.com/v1/result" +
            "?appid=${Keys.WOLFRAM.key}" +
            "&i=$formattedQuestion" +
            "&units=metric")
    }

    val thumbnailURL =
        "http://api.wolframalpha.com/v1/simple" +
        "?appid=${Keys.WOLFRAM.key}" +
        "&i=$formattedQuestion" +
        "&background=black" +
        "&foreground=white" +
        "&units=metric"

    return simpleResult to thumbnailURL
}