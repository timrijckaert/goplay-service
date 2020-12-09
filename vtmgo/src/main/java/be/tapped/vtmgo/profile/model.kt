package be.tapped.vtmgo.profile

import kotlinx.serialization.Serializable

public enum class VTMGOProduct {
    VTM_GO,
    VTM_GO_KIDS;
}

@Serializable
public enum class Gender {
    MALE,
    FEMALE;
}

@Serializable
public data class Color(
    val id: Int,
    val start: String,
    val end: String,
)

@Serializable
public data class Profile(
    val id: String,
    val name: String,
    val gender: Gender,
    val birthDate: String,
    val deletable: Boolean,
    val mainProfile: Boolean,
    val color: Color,
    val product: VTMGOProduct,
)

public inline class JWT(public val token: String)
