package kotbot

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.create.allowedMentions
import dev.kord.rest.builder.message.create.embed
import kotbot.commands.reddit.RedditHandler
import kotlinx.coroutines.runBlocking

fun main() {
    val kotBot = runBlocking { Bot(Kord(Keys.DISCORD.key)) }

    runBlocking { kotBot.bot.login() }
}

class Bot(val bot: Kord) {
    private val redditHandler = RedditHandler()

    init {
        bot.on(consumer = ::onMessageSend)
    }

    // calls when a user sends a message
    private suspend fun onMessageSend(messageEvent: MessageCreateEvent) {
        // ignores all bots, including itself
        val message = messageEvent.message

        if (message.author?.isBot != false) return

        if (message.content.startsWith(Prefixes.REDDIT.prefix)) {
            redditHandler.redditCommand(message)
        } else if (message.content.startsWith(Prefixes.HELP.prefix)) {
            message.channel.createMessage(builder = (::helpEmbed)(message.id))
        }
    }

    private fun helpEmbed(messageId: Snowflake): UserMessageCreateBuilder.() -> Unit {
        return  {
            messageReference = messageId

            allowedMentions {
                repliedUser = true
            }

            embed {
                title = "**KotBot**"
                url = "https://github.com/SaiProton/KotBot"
                color = Color(0xa343d2)

                description =
                    "**Reddit Finder**\n" +
                    "**`${MASTER_PREFIX}r/{subreddit} [sort]`** \nGet a hot post from any subreddit!\n" +
                    "*default sort*: `hot`\n" +
                    "*sort options*: `hot`, `top`, `new`, `rising`, `controversial`\n" +
                    "*example*: `*r/funny new` to find a new post on r/funny\n\n" +

                    "**`${MASTER_PREFIX}ask {question}`** \nAsk a question (preferably one with a definite answer) to be answered.\n\n"

                footer {
                    text = "KotBot — Made with ❤️ and Kotlin"
                }

                thumbnail {
                    url = "https://kotlinlang.org/lp/mobile/static/kmm-hero-mobile-e68d7931df1df74afe72f886177b72d6.png"
                }
            }
        }
    }
}


