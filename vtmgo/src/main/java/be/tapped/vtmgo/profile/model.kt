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
        val color: Color,
        val product: VTMGOProduct,
        val gender: Gender? = null,
        val birthDate: String? = null,
        val deletable: Boolean? = null,
        val mainProfile: Boolean? = null,
)

public data class TokenWrapper(val jwt: JWT)

public inline class JWT(public val token: String)
