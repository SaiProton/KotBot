package kotbot

// prefix that the bot uses to look for all commands
const val MASTER_PREFIX = "*"

enum class Prefixes(val prefix: String) {
    REDDIT("${MASTER_PREFIX}r/"),
    ASK("${MASTER_PREFIX}ask "),
    HELP("${MASTER_PREFIX}help")
}
