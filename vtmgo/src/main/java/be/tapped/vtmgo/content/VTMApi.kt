package be.tapped.vtmgo.content

import be.tapped.vtmgo.common.AuthorizationHeaderBuilder
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.defaultOkHttpClient
import okhttp3.OkHttpClient

class VTMApi(
    client: OkHttpClient = defaultOkHttpClient,
    headerBuilder: HeaderBuilder = AuthorizationHeaderBuilder(),
    programRepo: ProgramRepo = HttpProgramRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonPagedTeaserContentParser()),
    categoryRepo: CategoryRepo = HttpCategoryRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonCategoryParser()),
    channelRepo: ChannelRepo = HttpChannelRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonChannelParser()),
    storeFrontRepo: StoreFrontRepo = HttpStoreFrontRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonStoreFrontParser()),
    favoritesRepo: FavoritesRepo = HttpFavoritesRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonFavoritesParser()),
    searchRepo: SearchRepo = HttpSearchRepo(client, BaseContentHttpUrlBuilder, headerBuilder, JsonSearchResultResponseParser())
) : ProgramRepo by programRepo,
    CategoryRepo by categoryRepo,
    ChannelRepo by channelRepo,
    StoreFrontRepo by storeFrontRepo,
    FavoritesRepo by favoritesRepo,
    SearchRepo by searchRepo
