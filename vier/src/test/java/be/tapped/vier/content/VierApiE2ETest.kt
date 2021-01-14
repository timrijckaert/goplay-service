package be.tapped.vier.content

import arrow.fx.coroutines.parTraverse
import be.tapped.vier.CredentialsProvider
import be.tapped.vier.profile.HttpProfileRepo
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty

public class VierApiE2ETest : FreeSpec({
    val (username, password) = CredentialsProvider.credentials

    "given the VierAPI" - {
        val vierApi = VierApi()

        "and we are logged in" - {
            val profileRepo = HttpProfileRepo()

            val tokens = profileRepo.fetchTokens(username, password)

            "it should be successful" {
                tokens.shouldBeRight()
            }

            val refreshTokens = tokens.orNull()!!.refreshToken
            "it should be able to refresh the tokens" - {
                val newTokens = profileRepo.refreshTokens(refreshTokens)

                "it should be successful" {
                    newTokens.shouldBeRight()
                }
            }

            val accessToken = tokens.orNull()!!.accessToken
            "it should be able to fetch the user attributes" - {
                val profile = profileRepo.getUserAttributes(accessToken)

                "it should be successful" {
                    profile.shouldBeRight()
                }
            }

            "when fetching the all the programs from A-Z" - {
                val programs = vierApi.fetchPrograms()
                "it should be successful" {
                    programs.shouldBeRight()
                }

                val idToken = tokens.orNull()!!.idToken
                "when fetching episodes" - {
                    programs.orNull()!!
                        .programs
                        .flatMap(Program::playlists)
                        .flatMap(Program.Playlist::episodes)
                        .shuffled()
                        .take(100).parTraverse {
                            val streams = vierApi.streamByVideoUuid(idToken, it.id)

                            "it should have found a stream $streams" {
                                streams.shouldBeRight()
                            }
                        }
                }
            }
        }

        "when fetching a single program by url" - {
            // Array.from(document.querySelectorAll(".program-overview__link")).map(e => e.href).join(", ");
            listOf(
                "corona2020",
                "hetisingewikkeld",
                "t-is-gebeurd",
                "absentia",
                "alex-agnew-unfinished-business",
                "auwch",
                "bake-off-home",
                "bake-off-vlaanderen",
                "bake-off-vlaanderen-regulas-baktips",
                "baksteen-in-de-maag",
                "big-brother",
                "blind-gekocht",
                "blind-gekocht-magazine",
                "blind-gekocht-nl",
                "boerenjaar",
                "cafedemol",
                "callboys",
                "camping-karen-james",
                "chaussee-damour",
                "chef-in-je-oor",
                "control-pedro",
                "cook-ensemble",
                "dancing-with-the-stars",
                "de-ambassade",
                "de-anderhalve-metershow",
                "de-barak-van-bartel",
                "de-battle",
                "de-bende-haemers",
                "de-blauwe-gids",
                "de-boxys",
                "de-bril-van-martin",
                "de-container-cup",
                "de-dag",
                "de-ideale-wereld",
                "de-idioten",
                "de-kust-is-veilig",
                "de-mol",
                "de-premier",
                "de-recherche",
                "de-rechtbank",
                "de-slimste-mens-in-huis",
                "de-slimste-mens-ter-wereld",
                "de-sollicitatie",
                "de-verhulstjes",
                "de-wereld-van-bellewaerde",
                "dont-worry-be-happy",
                "ex-gangster",
                "expeditie-robinson",
                "forever-young",
                "gentwest",
                "gert-late-night",
                "geubels-en-de-idioten",
                "grillmasters",
                "grillmasters-bbq-hacks",
                "help-mijn-borsten-staan-online",
                "help-mijn-kind-kijkt-porno",
                "het-kot-van-vier",
                "het-parket",
                "het-rad",
                "het-zijn-net-mensen",
                "heylen-en-de-herkomst",
                "homeparty-met-kat",
                "hotel-romantiek",
                "huizenjagers",
                "huizenjagers-vakantiehuizen",
                "influencers",
                "ja-chef",
                "jani-gaat",
                "jeroom-in-lockdown",
                "junior-bake-off-vlaanderen",
                "justice-for-all",
                "komen-eten",
                "kroost",
                "lance",
                "leeuwenkuil",
                "love-island",
                "matchmakers",
                "ncis",
                "nieuwsjagers",
                "niveau-4",
                "ons-eerste-huis",
                "onze-dochter-heet-delphine",
                "ooit-vrij",
                "opvoeden-doe-je-zo",
                "over-de-oceaan",
                "project-axel",
                "schatten-van-mensen",
                "spitting-image",
                "sports-late-night",
                "stukken-van-mensen",
                "great-british-bake-off",
                "the-savoy",
                "the-sky-is-the-limit",
                "the-worlds-best",
                "topdokters",
                "topdokters-corona",
                "trafiek-axel",
                "trio",
                "tweedehands-deluxe",
                "uefa-champions-league",
                "van-rossem",
                "vermist",
                "viktor-vlogdown",
                "viktor-vlogt",
                "voetbal",
                "wim-zingt",
                "worlds-most-luxurious",
                "young-sheldon",
                "zeg-eens-euh",
                "zo-man-zo-vrouw",
                "zout-op-het-vuur",
            ).parTraverse {
                val programSearchKey = SearchHit.Source.SearchKey.Program("https://www.vier.be/$it")
                val program = vierApi.fetchProgram(programSearchKey)

                "$it should be successful" {
                    program.shouldBeRight()
                }
            }
        }

        "searching for an existing program" - {
            val searchResult = vierApi.search("de slimste mens")

            "should be successful" {
                searchResult.shouldBeRight()
            }

            "should have found search hits" {
                searchResult.orNull()!!.hits.shouldNotBeEmpty()
            }

            val searchKeys = searchResult.orNull()!!.hits
                .map { it.source.searchKey }

            searchKeys
                .filterIsInstance<SearchHit.Source.SearchKey.EpisodeByNodeId>()
                .parTraverse { episodeSearchKey ->
                    val episode = vierApi.fetchEpisode(episodeSearchKey)
                    "api returned an episode: $episode for $episodeSearchKey" {
                        episode.shouldBeRight()
                    }
                }

            searchKeys
                .filterIsInstance<SearchHit.Source.SearchKey.Program>()
                .parTraverse { programSearchKey ->
                    val program = vierApi.fetchProgram(programSearchKey)
                    "api returned a program: $program for $programSearchKey" {
                        program.shouldBeRight()
                    }
                }
        }
    }
})
