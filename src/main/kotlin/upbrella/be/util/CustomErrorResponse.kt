package upbrella.be.util

data class CustomErrorResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: Any? = null
) {
    constructor(status: String, code: Int, message: String) : this(status, code, message, null)
}
