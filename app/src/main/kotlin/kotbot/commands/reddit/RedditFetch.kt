package kotbot.commands.reddit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.request.*
import kotbot.MASTER_PREFIX

// gson object for converting from JSON string to Java objects
val gson: Gson = GsonBuilder().create()

suspend fun fetchRedditData(message: String): Pair<RedditData?, String> {
    // preprocess input
    val sub =
        message.removePrefix(MASTER_PREFIX)
            .split(" ")
            .filter {s -> s != ""}
            .toMutableList()

    val validOptions = listOf("hot", "top", "new", "rising", "controversial")

    if (sub.size == 1)  sub.add(validOptions[0])

    if (sub[1] !in validOptions) return Pair(null, sub[1])

    // stores the HTTP response as a JSON string
    val response: String = HttpClient().use { client ->
        client.get("https://www.reddit.com/${sub[0]}.json?sort=${sub[1]}&limit=100")
    }

    return Pair(gson.fromJson(response, RedditData::class.java), sub[1])
}

data class RedditData(val data: RootData)

// the multiple children contain individual posts
data class RootData(val children: Array<RedditPost>)

data class RedditPost(val data: PostData)

/***
 * Post data. Contains all the necessary data needed for the embed
 *
 * @param title  post title
 * @param author  post author
 * @param subreddit_name_prefixed  subreddit name with "r/"
 * @param selftext  post text content
 * @param permalink  link to the post (only "r/...", nothing before
 * @param thumbnail  post thumbnail url
 * @param url  post image url
 * @param score  post score (ups - downs)
 * @param over_18  post is NSFW or not?
 */
data class PostData(
    val title: String,
    val author: String,
    val subreddit_name_prefixed: String,
    val selftext: String,
    val permalink: String,
    val thumbnail: String,
    val url: String,
    val score: Int,
    val over_18: Boolean
)