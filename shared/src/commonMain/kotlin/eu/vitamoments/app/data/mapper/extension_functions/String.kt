package eu.vitamoments.app.data.mapper.extension_functions

private val EMAIL_REGEX = Regex(
    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
    RegexOption.IGNORE_CASE
)

fun String.isEmail() : Boolean = this.matches(EMAIL_REGEX)