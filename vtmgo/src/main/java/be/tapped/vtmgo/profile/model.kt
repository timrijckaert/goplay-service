package be.tapped.vtmgo.profile

import kotlinx.serialization.Serializable

enum class VTMGOProduct {
    VTM_GO,
    VTM_GO_KIDS;
}

@Serializable
enum class Gender {
    MALE,
    FEMALE;
}

@Serializable
data class Color(
    val id: Int,
    val start: String,
    val end: String,
)

@Serializable
data class Profile(
    val id: String,
    val name: String,
    val gender: Gender,
    val birthDate: String,
    val deletable: Boolean,
    val mainProfile: Boolean,
    val color: Color,
    val product: VTMGOProduct,
)

inline class JWT(val token: String)
