package be.tapped.vtmgo.profile

import be.tapped.vtmgo.common.AuthorizationHeaderBuilder
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.vtmApiDefaultOkHttpClient
import okhttp3.OkHttpClient

public sealed interface AuthenticationRepo : ProfileRepo, JWTTokenRepo

public class HttpAuthenticationRepo(
        client: OkHttpClient = vtmApiDefaultOkHttpClient,
        headerBuilder: HeaderBuilder = AuthorizationHeaderBuilder(),
        profileRepo: ProfileRepo = HttpProfileRepo(client),
        jwtTokenRepo: JWTTokenRepo = HttpAndroidJWTTokenRepo(client, headerBuilder),
) :
        AuthenticationRepo,
        ProfileRepo by profileRepo,
        JWTTokenRepo by jwtTokenRepo
