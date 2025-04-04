package upbrella.be.util

data class CustomResponse<T>(
    val status: String,
    val code: Int,
    val message: String,
    val data: T? = null
) {
    constructor(status: String, code: Int, message: String) : this(status, code, message, null)
}
