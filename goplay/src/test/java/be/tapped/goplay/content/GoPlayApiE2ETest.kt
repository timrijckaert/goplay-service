package be.tapped.goplay.content

import arrow.fx.coroutines.parTraverse
import be.tapped.goplay.CredentialsProvider
import be.tapped.goplay.profile.HttpProfileRepo
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty

public class GoPlayApiE2ETest : FreeSpec({
    val (username, password) = CredentialsProvider.credentials

    "given the GoPlayAPI" - {
        val goPlayApi = GoPlayApi()

        "and we are logged in" - {
            val profileRepo = HttpProfileRepo()

            val tokens = profileRepo.fetchTokens(username, password)

            "it should be successful" {
                tokens.shouldBeRight()
            }

            val refreshTokens = tokens.orNull()!!.token.refreshToken
            "it should be able to refresh the tokens" - {
                val newTokens = profileRepo.refreshTokens(refreshTokens)

                "it should be successful" {
                    newTokens.shouldBeRight()
                }
            }

            val accessToken = tokens.orNull()!!.token.accessToken
            "it should be able to fetch the user attributes" - {
                val profile = profileRepo.getUserAttributes(accessToken)

                "it should be successful" {
                    profile.shouldBeRight()
                }
            }

            "when fetching the all the programs from A-Z" - {
                val programs = goPlayApi.fetchPrograms()
                "it should be successful" {
                    programs.shouldBeRight()
                }

                val idToken = tokens.orNull()!!.token.idToken
                "when fetching episodes" - {
                    programs.orNull()!!.programs.flatMap(Program::playlists).flatMap(Program.Playlist::episodes).shuffled().take(100).parTraverse {
                        val streams = goPlayApi.streamByVideoUuid(idToken, it.videoUuid)

                        "it should have found a stream $streams" {
                            streams.shouldBeRight()
                        }
                    }
                }
            }
        }

        "when fetching a single program by url" - {
            // Array.from(document.querySelectorAll(".program-overview__row a.teaser.poster-teaser")).map(e => e.href).join(", ")
            listOf(
                    "hetisingewikkeld",
                    "andre-hazes-ik-haal-alles-uit-het-leven",
                    "au-pairs",
                    "auwch",
                    "bake-off-home",
                    "bake-off-vlaanderen",
                    "bake-off-vlaanderen-regulas-baktips",
                    "big-brother",
                    "bij-ons-op-het-kamp",
                    "blind-gekocht",
                    "blind-gekocht-australie",
                    "blind-gekocht-nl",
                    "blind-gekocht-usa",
                    "boerenjaar",
                    "borsatos-budget-bruiloft",
                    "bye-bye-belgium",
                    "cafedemol",
                    "callboys",
                    "cash-or-trash",
                    "celebs-gaan-daten",
                    "chateau-meiland",
                    "chaussee-damour",
                    "cook-ensemble",
                    "dag-dokter",
                    "dancing-with-the-stars",
                    "date-my-closet",
                    "de-anderhalve-metershow",
                    "de-bachelorette",
                    "de-battle",
                    "de-container-cup",
                    "de-dag",
                    "de-mol",
                    "de-premier",
                    "de-roelvinkjes",
                    "de-slimste-mens-in-huis",
                    "de-slimste-mens-ter-wereld",
                    "de-slimste-mens-het-leukste-van",
                    "de-verhulstjes",
                    "de-verhulstjes-wat-je-niet-zag-op-tv",
                    "dont-tell-the-bride",
                    "dont-worry-be-happy",
                    "dress-to-impress",
                    "ex-gangster",
                    "fbi-most-wanted",
                    "gentwest",
                    "gert-late-night",
                    "goedele-on-top",
                    "hawaii-five-o",
                    "het-kot-van-vier",
                    "het-rad",
                    "highway-thru-hell",
                    "hij-is-een-zij",
                    "homeparty-met-kat",
                    "hot-in-uw-kot",
                    "hotel-romantiek",
                    "huizenjagers",
                    "huizenjagers-vakantiehuizen",
                    "influencers",
                    "inside-the-qe2-hotel",
                    "ja-chef",
                    "jani-gaat",
                    "jeroom-in-lockdown",
                    "julie-vlogt",
                    "junior-bake-off-vlaanderen",
                    "justice-for-all",
                    "komen-eten",
                    "love-island",
                    "love-island-uk",
                    "macgyver",
                    "masterchef-usa",
                    "meisje-van-plezier",
                    "monacovrouwen",
                    "naked-attraction",
                    "ncis",
                    "niveau-4",
                    "obsessive-compulsive-cleaners",
                    "op-een-ander",
                    "opvoeden-doe-je-zo",
                    "over-de-oceaan",
                    "paleis-voor-een-prikje",
                    "pawn-stars",
                    "prodigal-son",
                    "schatten-van-mensen",
                    "secret-bridesmaids-business",
                    "sekszusjes-tv",
                    "sex-tape-uk",
                    "shopping-queens",
                    "soof",
                    "spitting-image",
                    "sports-late-night",
                    "storage-wars",
                    "stukken-van-mensen",
                    "summer-de-snoo-picture-perfect",
                    "t-is-gebeurd",
                    "tattoo-fixers-extreme",
                    "temptation-island",
                    "temptation-island-vips",
                    "temptation-island-love-or-leave",
                    "the-block-australia",
                    "the-savoy",
                    "the-sex-clinic",
                    "the-sky-is-the-limit",
                    "trio",
                    "tweedehands-deluxe",
                    "uefa-champions-league",
                    "ultimate-tag",
                    "undercover-girlfriends",
                    "verminkt",
                    "vermist",
                    "viervoeters",
                    "viktor-vlogdown",
                    "wat-wil-maxime",
                    "wim-zingt",
                    "wtfock",
                    "zo-man-zo-vrouw",
            ).parTraverse {
                val programSearchKey = SearchHit.Source.SearchKey.Program("https://www.goplay.be/$it")
                val program = goPlayApi.fetchProgram(programSearchKey)

                "$it should be successful" {
                    program.shouldBeRight()
                }
            }
        }

        "searching for an existing program" - {
            val searchResult = goPlayApi.search("de slimste mens")

            "should be successful" {
                searchResult.shouldBeRight()
            }

            "should have found search hits" {
                searchResult.orNull()!!.hits.shouldNotBeEmpty()
            }

            val searchKeys = searchResult.orNull()!!.hits.map { it.source.searchKey }

            searchKeys.filterIsInstance<SearchHit.Source.SearchKey.EpisodeByNodeId>().parTraverse { episodeSearchKey ->
                val episode = goPlayApi.fetchEpisode(episodeSearchKey)
                "api returned an episode: $episode for $episodeSearchKey" {
                    episode.shouldBeRight()
                }
            }

            searchKeys.filterIsInstance<SearchHit.Source.SearchKey.Program>().parTraverse { programSearchKey ->
                val program = goPlayApi.fetchProgram(programSearchKey)
                "api returned a program: $program for $programSearchKey" {
                    program.shouldBeRight()
                }
            }
        }
    }
})
