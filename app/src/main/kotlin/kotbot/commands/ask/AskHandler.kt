package kotbot.commands.ask

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.behavior.channel.createMessage
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.create.allowedMentions
import dev.kord.rest.builder.message.create.embed
import kotbot.MASTER_PREFIX
import kotbot.Prefixes

class AskHandler {
    /***
     * With a user's ask command, it grabs the necessary thumbnail and answer data using the Wolfram API
     *
     * @param message  the user's ask command message
     */
    suspend fun askCommand(message: Message) {
        val question = message.content.removePrefix(Prefixes.ASK.prefix)

        val (answer, thumbnail) = fetchAskData(question)

        message.channel.createMessage(builder = (::askEmbed)(question, answer, thumbnail, message.id))
    }

    /***
     * Makes a discord embed with the answers the user asked using information from the Wolfram API
     *
     * @param question  original question proposed by user
     * @param answer  answered question by Wolfram short answers API
     * @param thumbnail  image from Wolfram simple API
     * @param messageId  message's ID for replying purposes
     *
     * @return  function containing defined parameters of which MessageCreateBuilder will be passed in order to create a message
     */
    private fun askEmbed(question: String, answer: String, thumbnail: String, messageId: Snowflake) : UserMessageCreateBuilder.() -> Unit {
        return  {
            content = "You wanted answers? <('o'<)"

            messageReference = messageId

            allowedMentions {
                repliedUser = true
            }

            embed {
                title = "**`$question`**"
                description = answer
                color = Color(221, 17, 0)

                thumbnail {
                    url = thumbnail
                }

                author {
                    name = "WolframAlpha"
                    url = "https://www.wolframalpha.com"
                }

                footer {
                    text = "Answered by WolframAlpha"
                }
            }
        }
    }
}