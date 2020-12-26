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
                            val streams = vierApi.streamForEpisodeVideoUuid(idToken, it.id)

                            "it should have found a stream $streams" {
                                streams.shouldBeRight()
                            }
                        }
                }
            }
        }

        "when fetching a single program by url" - {
            listOf(
                "https://www.vier.be/corona2020",
                "https://www.vier.be/hetisingewikkeld",
                "https://www.vier.be/t-is-gebeurd",
                "https://www.vier.be/absentia",
                "https://www.vier.be/alex-agnew-unfinished-business",
                "https://www.vier.be/auwch",
                "https://www.vier.be/bake-off-home",
                "https://www.vier.be/bake-off-vlaanderen",
                "https://www.vier.be/bake-off-vlaanderen-regulas-baktips",
                "https://www.vier.be/baksteen-in-de-maag",
                "https://www.vier.be/big-brother",
                "https://www.vier.be/blind-gekocht",
                "https://www.vier.be/blind-gekocht-magazine",
                "https://www.vier.be/blind-gekocht-nl",
                "https://www.vier.be/boerenjaar",
                "https://www.vier.be/cafedemol",
                "https://www.vier.be/callboys",
                "https://www.vier.be/camping-karen-james",
                "https://www.vier.be/chaussee-damour",
                "https://www.vier.be/chef-in-je-oor",
                "https://www.vier.be/control-pedro",
                "https://www.vier.be/cook-ensemble",
                "https://www.vier.be/dancing-with-the-stars",
                "https://www.vier.be/de-ambassade",
                "https://www.vier.be/de-anderhalve-metershow",
                "https://www.vier.be/de-barak-van-bartel",
                "https://www.vier.be/de-battle",
                "https://www.vier.be/de-bende-haemers",
                "https://www.vier.be/de-blauwe-gids",
                "https://www.vier.be/de-boxys",
                "https://www.vier.be/de-bril-van-martin",
                "https://www.vier.be/de-container-cup",
                "https://www.vier.be/de-dag",
                "https://www.vier.be/de-ideale-wereld",
                "https://www.vier.be/de-idioten",
                "https://www.vier.be/de-kust-is-veilig",
                "https://www.vier.be/de-mol",
                "https://www.vier.be/de-premier",
                "https://www.vier.be/de-recherche",
                "https://www.vier.be/de-rechtbank",
                "https://www.vier.be/de-slimste-mens-in-huis",
                "https://www.vier.be/de-slimste-mens-ter-wereld",
                "https://www.vier.be/de-sollicitatie",
                "https://www.vier.be/de-verhulstjes",
                "https://www.vier.be/de-wereld-van-bellewaerde",
                "https://www.vier.be/dont-worry-be-happy",
                "https://www.vier.be/ex-gangster",
                "https://www.vier.be/expeditie-robinson",
                "https://www.vier.be/forever-young",
                "https://www.vier.be/gentwest",
                "https://www.vier.be/gert-late-night",
                "https://www.vier.be/geubels-en-de-idioten",
                "https://www.vier.be/grillmasters",
                "https://www.vier.be/grillmasters-bbq-hacks",
                "https://www.vier.be/help-mijn-borsten-staan-online",
                "https://www.vier.be/help-mijn-kind-kijkt-porno",
                "https://www.vier.be/het-kot-van-vier",
                "https://www.vier.be/het-parket",
                "https://www.vier.be/het-rad",
                "https://www.vier.be/het-zijn-net-mensen",
                "https://www.vier.be/heylen-en-de-herkomst",
                "https://www.vier.be/homeparty-met-kat",
                "https://www.vier.be/hotel-romantiek",
                "https://www.vier.be/huizenjagers",
                "https://www.vier.be/huizenjagers-vakantiehuizen",
                "https://www.vier.be/influencers",
                "https://www.vier.be/ja-chef",
                "https://www.vier.be/jani-gaat",
                "https://www.vier.be/jeroom-in-lockdown",
                "https://www.vier.be/junior-bake-off",
                "https://www.vier.be/junior-bake-off-vlaanderen",
                "https://www.vier.be/justice-for-all",
                "https://www.vier.be/komen-eten",
                "https://www.vier.be/kroost",
                "https://www.vier.be/lance",
                "https://www.vier.be/leeuwenkuil",
                "https://www.vier.be/love-island",
                "https://www.vier.be/matchmakers",
                "https://www.vier.be/ncis",
                "https://www.vier.be/nieuwsjagers",
                "https://www.vier.be/niveau-4",
                "https://www.vier.be/ons-eerste-huis",
                "https://www.vier.be/onze-dochter-heet-delphine",
                "https://www.vier.be/ooit-vrij",
                "https://www.vier.be/opvoeden-doe-je-zo",
                "https://www.vier.be/over-de-oceaan",
                "https://www.vier.be/project-axel",
                "https://www.vier.be/schatten-van-mensen",
                "https://www.vier.be/spitting-image",
                "https://www.vier.be/sports-late-night",
                "https://www.vier.be/stukken-van-mensen",
                "https://www.vier.be/great-british-bake-off",
                "https://www.vier.be/the-savoy",
                "https://www.vier.be/the-sky-is-the-limit",
                "https://www.vier.be/the-worlds-best",
                "https://www.vier.be/topdokters",
                "https://www.vier.be/topdokters-corona",
                "https://www.vier.be/trafiek-axel",
                "https://www.vier.be/tweedehands-deluxe",
                "https://www.vier.be/uefa-champions-league",
                "https://www.vier.be/van-rossem",
                "https://www.vier.be/vermist",
                "https://www.vier.be/viktor-vlogdown",
                "https://www.vier.be/viktor-vlogt",
                "https://www.vier.be/voetbal",
                "https://www.vier.be/wim-zingt",
                "https://www.vier.be/worlds-most-luxurious",
                "https://www.vier.be/young-sheldon",
                "https://www.vier.be/zeg-eens-euh",
                "https://www.vier.be/zo-man-zo-vrouw",
                "https://www.vier.be/zout-op-het-vuur"
            ).parTraverse {
                val programSearchKey = SearchHit.Source.SearchKey.Program(it)
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
                .filterIsInstance<SearchHit.Source.SearchKey.Episode>()
                .parTraverse(vierApi::fetchEpisode)
                .forEach {
                    "api returned an episode: $it" {
                        it.shouldBeRight()
                    }
                }

            searchKeys
                .filterIsInstance<SearchHit.Source.SearchKey.Program>()
                .parTraverse(vierApi::fetchProgram)
                .forEach {
                    "api returned a program: $it" {
                        it.shouldBeRight()
                    }
                }
        }
    }
})
