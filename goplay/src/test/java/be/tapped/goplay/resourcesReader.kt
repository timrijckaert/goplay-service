package be.tapped.goplay

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
internal fun <T : Any> T.readFromResources(fileName: String): String = javaClass.classLoader.getResourceAsStream(fileName).reader().readText()
