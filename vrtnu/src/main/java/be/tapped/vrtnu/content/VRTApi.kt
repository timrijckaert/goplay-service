package be.tapped.vrtnu.content

import be.tapped.vrtnu.common.defaultOkHttpClient
import okhttp3.OkHttpClient

public class VRTApi(
    client: OkHttpClient = defaultOkHttpClient,
    programRepo: ProgramRepo = HttpProgramRepo(client, JsonProgramParser()),
    categoryRepo: CategoryRepo = HttpCategoryRepo(client, JsonCategoryParser()),
    episodeRepo: EpisodeRepo = HttpEpisodeRepo(client, JsonEpisodeParser()),
    streamRepo: StreamRepo = HttpStreamRepo(client, JsonStreamInformationParser()),
    screenshotRepo: ScreenshotRepo = DefaultScreenshotRepo,
) : ProgramRepo by programRepo,
    CategoryRepo by categoryRepo,
    EpisodeRepo by episodeRepo,
    StreamRepo by streamRepo,
    ScreenshotRepo by screenshotRepo
