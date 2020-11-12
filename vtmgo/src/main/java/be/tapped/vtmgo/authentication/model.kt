package be.tapped.vtmgo.authentication

enum class Product {
    VTM_GO,
    VTM_GO_KIDS;
}

data class NetworkFailure(val url: String, val code: Int) :
    Exception("$url failed with status code $code")

data class Profile(
    val id: String,
    val product: Product,
    val name: String,
    val gender: String,
    val birthDate: String,
    val color: String,
    val color2: String,
)

inline class JWT(val token: String)
