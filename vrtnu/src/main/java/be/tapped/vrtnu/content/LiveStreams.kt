package be.tapped.vrtnu.content

public object LiveStreams {
    public data class LiveStream(
        val name: String,
        val brand: Brand,
        val videoId: VideoId,
        val epgId: String? = null,
    ) {
        public enum class Brand {
            EEN,
            CANVAS,
            KETNET,
            KETNET_JUNIOR,
            SPORZA,
            VRT_NWS,
            RADIO_1,
            RADIO_2,
            KLARA,
            STUDIO_BRUSSEL,
            MNM,
            VRT_NXT,
        }
    }

    public val een: LiveStream = LiveStream(
        epgId = "08",
        name = "Eén",
        brand = LiveStream.Brand.EEN,
        videoId = VideoId("vualto_een_geo"),
    )

    public val canvas: LiveStream = LiveStream(
        epgId = "1H",
        name = "Canvas",
        brand = LiveStream.Brand.CANVAS,
        videoId = VideoId("vualto_canvas_geo"),
    )

    public val ketnet: LiveStream = LiveStream(
        epgId = "O9",
        name = "Ketnet",
        brand = LiveStream.Brand.KETNET,
        videoId = VideoId("vualto_ketnet_geo"),
    )

    public val ketnetJunior: LiveStream = LiveStream(
        name = "Ketnet Junior",
        brand = LiveStream.Brand.KETNET_JUNIOR,
        videoId = VideoId("ketnet_jr"),
    )

    public val sporza: LiveStream = LiveStream(
        name = "Sporza",
        brand = LiveStream.Brand.SPORZA,
        videoId = VideoId("vualto_sporza_geo"),
    )

    public val vrtnws: LiveStream = LiveStream(
        name = "VRT NWS",
        brand = LiveStream.Brand.VRT_NWS,
        videoId = VideoId("vualto_nieuws"),
    )

    public val radio1: LiveStream = LiveStream(
        name = "Radio 1",
        brand = LiveStream.Brand.RADIO_1,
        videoId = VideoId("vualto_radio1"),
    )

    public val radio2: LiveStream = LiveStream(
        name = "Radio 2",
        brand = LiveStream.Brand.RADIO_2,
        videoId = VideoId("vualto_radio2"),
    )

    public val klara: LiveStream = LiveStream(
        name = "Klara",
        brand = LiveStream.Brand.KLARA,
        videoId = VideoId("vualto_klara"),
    )

    public val stubru: LiveStream = LiveStream(
        name = "Studio Brussel",
        brand = LiveStream.Brand.STUDIO_BRUSSEL,
        videoId = VideoId("vualto_stubru"),
    )

    public val mnm: LiveStream = LiveStream(
        name = "MNM", brand = LiveStream.Brand.MNM, videoId = VideoId("vualto_mnm")
    )

    public val vrtEvents1: LiveStream = LiveStream(
        name = "VRT Events 1", brand = LiveStream.Brand.VRT_NXT, videoId = VideoId("vualto_events1_geo")
    )

    public val vrtEvents2: LiveStream = LiveStream(
        name = "VRT Events 2", brand = LiveStream.Brand.VRT_NXT, videoId = VideoId("vualto_events2_geo")
    )

    public val vrtEvents3: LiveStream = LiveStream(
        name = "VRT Events 3", brand = LiveStream.Brand.VRT_NXT, videoId = VideoId("vualto_events3_geo")
    )

    public val allLiveStreams: List<LiveStream>
        get() = listOf(
            een,
            canvas,
            ketnet,
            ketnetJunior,
            sporza,
            vrtnws,
            radio1,
            radio2,
            klara,
            stubru,
            mnm,
            vrtEvents1,
            vrtEvents2,
            vrtEvents3,
        )
}
