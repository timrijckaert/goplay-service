package be.tapped.vrtnu.content

internal class ProgramSanitizer {
    private val String.addHTTPSPrefix: String get() = "https:${this}"

    internal fun sanitizeProgram(program: Program): Program = program.copy(
        alternativeImage = program.alternativeImage.addHTTPSPrefix,
        programUrl = program.programUrl.addHTTPSPrefix,
        targetUrl = program.targetUrl.addHTTPSPrefix,
        thumbnail = program.thumbnail.addHTTPSPrefix,
    )
}
