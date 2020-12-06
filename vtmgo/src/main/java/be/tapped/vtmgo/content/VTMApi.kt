package be.tapped.vtmgo.content

import be.tapped.vtmgo.common.AuthorizationHeaderBuilder
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.anvatoDefaultOkHttpClient
import be.tapped.vtmgo.common.vtmApiDefaultOkHttpClient
import okhttp3.OkHttpClient

class VTMApi(
    client: OkHttpClient = vtmApiDefaultOkHttpClient,
    headerBuilder: HeaderBuilder = AuthorizationHeaderBuilder(),
    catalogRepo: CatalogRepo = HttpCatalogRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonPagedTeaserContentParser()),
    episodeRepo: EpisodeRepo = HttpEpisodeRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonProgramParser()),
    categoryRepo: CategoryRepo = HttpCategoryRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonCategoryParser()),
    channelRepo: ChannelRepo = HttpChannelRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonChannelParser()),
    storeFrontRepo: StoreFrontRepo = HttpStoreFrontRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonStoreFrontParser()),
    favoritesRepo: FavoritesRepo = HttpFavoritesRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonFavoritesParser()),
    searchRepo: SearchRepo = HttpSearchRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonSearchResultResponseParser()),
    streamRepo: StreamRepo = HttpStreamRepo(
        client,
        headerBuilder,
        JsonStreamResponseParser(),
        HttpAnvatoResponse(anvatoDefaultOkHttpClient, AnvatoVideoJsonLoadedParser())
    ),
) : CatalogRepo by catalogRepo,
    EpisodeRepo by episodeRepo,
    CategoryRepo by categoryRepo,
    ChannelRepo by channelRepo,
    StoreFrontRepo by storeFrontRepo,
    FavoritesRepo by favoritesRepo,
    SearchRepo by searchRepo,
    StreamRepo by streamRepo
