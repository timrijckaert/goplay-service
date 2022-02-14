package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.goplay.ApiResponse.Failure
import be.tapped.goplay.ApiResponse.Success
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class HtmlProgramParser(private val jsonSerializer: Json) {
    private val regex = "data-program=\"(.*)\"".toRegex()
    internal fun parse(html: String): Either<Failure, List<Program>> =
        Either.catch(regex.findAll(html).map { it.groupValues[1] }.map<String, Program>(jsonSerializer::decodeFromString)::toList).mapLeft(Failure::JsonParsingException)
}

internal fun interface ProgramRepo {
    suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs>
}

internal fun httpProgramRepo(client: HttpClient, htmlProgramParser: HtmlProgramParser): ProgramRepo = ProgramRepo {
    withContext(Dispatchers.IO) {
        either {
            val html = client.get<HttpResponse>("$siteUrl/programmas").readText()
            val programs = htmlProgramParser.parse(html).bind()
            Success.Content.Programs(programs)
        }
    }
}
