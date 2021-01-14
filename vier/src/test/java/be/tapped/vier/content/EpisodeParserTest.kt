package be.tapped.vier.content

import arrow.core.Either
import be.tapped.vier.ApiResponse
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec

public class EpisodeParserTest : StringSpec({
    "should be able to parse `Big Brother`" {
        // TODO for now we ignore the 'program' key inside the JSON due to a bug in the serialization lib not being able to handle malformed json
        val episodeJson = javaClass.classLoader?.getResourceAsStream("big-brother-episode.json")!!.reader().readText()
        val episodeParser = EpisodeParser()
        val episode: Either<ApiResponse.Failure, Program.Playlist.Episode> = episodeParser.parse(episodeJson)
        episode.shouldBeRight()
    }
})
