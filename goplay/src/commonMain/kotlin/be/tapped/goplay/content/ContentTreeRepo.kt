package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.Nel
import arrow.core.computations.either
import be.tapped.goplay.siteUrl
import be.tapped.goplay.toNel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal data class ContentRoot(val categories: Nel<Category>, val programs: Nel<Program>) {
    data class Program(val id: be.tapped.goplay.content.Program.Id, val category: Category.Id, val popularity: Int)
}

internal fun interface ContentTreeRepo {
    suspend fun fetchContentTree(): Either<Throwable, ContentRoot>
}

internal fun contentRootRepo(httpClient: HttpClient, contentTreeJsonParser: ContentTreeJsonParser): ContentTreeRepo =
    ContentTreeRepo {
        either {
            withContext(Dispatchers.IO) {
                catch {
                    val contentTreeRootObj = httpClient.get<JsonObject>("$siteUrl/api/content_tree")
                    contentTreeJsonParser.parseJsonToContentRoot(contentTreeRootObj).bind()
                }.bind()
            }
        }
    }

internal fun interface ContentTreeJsonParser {
    fun parseJsonToContentRoot(json: JsonObject): Either<Throwable, ContentRoot>
}

internal fun contentTreeJsonParser(): ContentTreeJsonParser =
    ContentTreeJsonParser {
        either.eager {
            catch {
                val categories =
                    it
                        .getValue("categories").jsonObject.entries
                        .map { (categoryId, categoryName) -> Category(Category.Id(categoryId), categoryName.jsonPrimitive.content) }
                        .toNel { error("No categories were found") }
                        .bind()

                val programs =
                    it
                        .getValue("programs").jsonObject.entries
                        .map { (programId, programJsonElement) ->
                            val programJson = programJsonElement.jsonObject
                            ContentRoot.Program(
                                Program.Id(programId),
                                Category.Id(programJson.getValue("category").jsonPrimitive.content),
                                programJson.getValue("popularity").jsonPrimitive.int
                            )
                        }
                        .toNel { error("No programs were found") }
                        .bind()

                ContentRoot(categories, programs)
            }.bind()
        }
    }
