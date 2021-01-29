package be.tapped.vtmgo.profile

import be.tapped.vtmgo.common.defaultCookieJar
import be.tapped.vtmgo.common.vtmApiDefaultOkHttpClient
import okhttp3.OkHttpClient

public interface AuthenticationRepo : ProfileRepo, JWTTokenRepo

public class HttpAuthenticationRepo(
    private val client: OkHttpClient = vtmApiDefaultOkHttpClient,
    private val profileRepo: ProfileRepo = HttpProfileRepo(client),
    private val jwtTokenRepo: JWTTokenRepo = HttpJWTTokenRepo(client, defaultCookieJar),
) : AuthenticationRepo, ProfileRepo by profileRepo, JWTTokenRepo by jwtTokenRepo
