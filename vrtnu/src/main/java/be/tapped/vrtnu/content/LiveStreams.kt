package be.tapped.vrtnu.content

public object LiveStreams {
    public val een: VideoId = VideoId("vualto_een_geo")
    public val canvas: VideoId = VideoId("vualto_canvas_geo")
    public val ketnet: VideoId = VideoId("vualto_ketnet_geo")
    public val ketnetJunior: VideoId = VideoId("ketnet_jr")
    public val sporza: VideoId = VideoId("vualto_sporza_geo")
    public val vrtnws: VideoId = VideoId("vualto_nieuws")
    public val radio1: VideoId = VideoId("vualto_radio1")
    public val radio2: VideoId = VideoId("vualto_radio2")
    public val klara: VideoId = VideoId("vualto_klara")
    public val studioBrussel: VideoId = VideoId("vualto_stubru")
    public val mnm: VideoId = VideoId("vualto_mnm")
    public val vrtEvents1: VideoId = VideoId("vualto_events1_geo")
    public val vrtEvents2: VideoId = VideoId("vualto_events2_geo")
    public val vrtEvents3: VideoId = VideoId("vualto_events3_geo")

    public val allLiveStreams: List<VideoId>
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
            studioBrussel,
            mnm,
            vrtEvents1,
            vrtEvents2,
            vrtEvents3,
        )
}
