package kotbot.commands.reddit

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.create.allowedMentions
import dev.kord.rest.builder.message.create.embed
import kotbot.utils.checkIfImage
import dev.kord.core.behavior.channel.createMessage

class RedditHandler {
    /***
     * Takes in a user's reddit command to obtain the subreddit's data via HTTP request
     *
     * @param message  user's message sent to chat for reddit data
     */
    suspend fun redditCommand(message: Message) {
        val (redditFeed, sortOption) = fetchRedditData(message.content)

        // is not null when command is valid
        if (redditFeed != null) {
            // if empty, not results were found
            if (redditFeed.data.children.isEmpty()) {
                message.channel.createMessage(builder = (::dataNotFoundMessage)(message))
                return
            }

            // selects random post from obtained posts from subreddit
            val post = redditFeed.data.children.random()

            message.channel.createMessage(builder = (::redditEmbed)(post, sortOption, message.id))
        } else {
            message.channel.createMessage(builder = (::invalidMessage)(sortOption, message.id))
        }
    }

    /***
     * Using the reddit data in object form, this constructs the final embed that will be shown to the user
     *
     * @param post  data object containing data obtained from HTTP request
     * @param messageId  message ID needed for replying
     *
     * @return  function containing defined parameters of which MessageCreateBuilder will be passed in order to create a message
     */
    private fun redditEmbed(post: RedditPost, sort: String, messageId: Snowflake): UserMessageCreateBuilder.() -> Unit {
        val isImagePost: Boolean = checkIfImage(post.data.url)

        return  {
            messageReference = messageId

            content = "Serving up some `$sort` content from **`${post.data.subreddit_name_prefixed}`** " +
                if (post.data.over_18) "(ಠ_ಠ)" else "(¬‿¬)"

            allowedMentions {
                repliedUser = true
            }

            embed {
                title = post.data.title
                url = "https://www.reddit.com${post.data.permalink}"
                color = Color(255, 69, 0)

                description =
                    if (!post.data.over_18)
                        post.data.selftext
                    else
                        ":warning: **NSFW Content ahead!**"

                image =
                    if (isImagePost && !post.data.over_18)
                        post.data.url
                    else
                        ""

                thumbnail {
                    url =
                        if (!isImagePost && checkIfImage(post.data.thumbnail) && !post.data.over_18)
                            post.data.thumbnail
                        else
                            ""
                }

                author {
                    name = "u/${post.data.author}"
                    url = "https://www.reddit.com/u/${post.data.author}"
                }

                footer {
                    text = "Score — ${post.data.score}\nPosted in ${post.data.subreddit_name_prefixed}"
                    icon = "https://www.redditstatic.com/desktop2x/img/favicon/favicon-96x96.png"
                }
            }
        }
    }

    /***
     * Message to be shown when no data was found for the subreddit
     *
     * @param message  user's reddit command message
     *
     * @return  function containing defined parameters of which MessageCreateBuilder will be passed in order to create a message
     */
    private fun dataNotFoundMessage(message: Message): UserMessageCreateBuilder.() -> Unit {
        return {
            messageReference = message.id
            content = "Couldn't find posts from `$message` (•ิ_•ิ)?"

            allowedMentions {
                repliedUser = true
            }
        }
    }

    /***
     * Message to be shown when the user enters an invalid command
     *
     * @param message  user's reddit command message
     *
     * @return  function containing defined parameters of which MessageCreateBuilder will be passed in order to create a message
     */
    private fun invalidMessage(sort: String, messageId: Snowflake): UserMessageCreateBuilder.() -> Unit {
        return {
            messageReference = messageId
            content = "*\"${sort}\"* is an invalid sorting option... Use `*help` to find out how to use the reddit command"

            allowedMentions {
                repliedUser = true
            }
        }
    }
}