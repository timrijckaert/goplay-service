# Additional Calls
These calls should be investigated and see if we can integrate them in the lib.


## Home page
No authorization required

```
curl -H 'Host: www.vrt.be' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' -H 'if-modified-since: Tue, 19 Jan 2021 19:53:34 GMT' --compressed 'https://www.vrt.be/vrtnu/jcr:content.model.json'
```

## Most recent

```
curl -H 'Host: search.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://search.vrt.be/search?facets%5BallowedRegion%5D=%5BBE%2CWORLD%5D&from=1&size=25&facets%5Bbrands%5D=%5Been%2Ccanvas%2Cklara%2Cmnm%2Cradio1%2Cradio2%2Csporza%2Cstubru%2Cvrtnws%2Cvrtnu%2Cvrtnxt%5D'
```

## Program detail

```
curl -H 'Host: www.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://www.vrt.be/vrtnu/a-z/the-life-and-trials-of-oscar-pistorius.model.json'
```

## Categories

```
curl -H 'Host: www.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://www.vrt.be/vrtnu/categorieen/jcr:content/par/categories.model.json'
```

## Search by category

```
curl -H 'Host: search.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://search.vrt.be/suggest?size=1000&facets%5Bcategories%5D=met-audiodescriptie'
```

## Playlist

```
curl -H 'Host: www.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://www.vrt.be/vrtnu/a-z/schitt-s-creek/1.playlist.json'
```

## Resume points

```
curl -H 'Host: video-user-data.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'authorization: <token>' -H 'user-agent: okhttp/3.14.6' --compressed 'https://video-user-data.vrt.be/resume_points'
```

## Subscribe Push notifications

```
curl -H 'Host: video-user-data.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'authorization: <token>' -H 'content-type: application/json' -H 'user-agent: okhttp/3.14.6' --data-binary '{"programUrl":"/vrtnu/a-z/bathroom-stories/","isFavorite":false,"adobeCloudId":"fdcf5bb5-c557-4fcc-8bc0-fb356ac4dd8c","title":"Bathroom stories","whatsonId":"954463470527","allowAppPushNotifications":true}' --compressed 'https://video-user-data.vrt.be/favorites/vrtnuazbathroomstories'
cur```l -H 'Host: video-user-data.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'authorization: <token>' -H 'content-type: application/json' -H 'user-agent: okhttp/3.14.6' --data-binary '{"programUrl":"/vrtnu/a-z/bathroom-stories/","isFavorite":false,"adobeCloudId":"fdcf5bb5-c557-4fcc-8bc0-fb356ac4dd8c","title":"Bathroom stories","whatsonId":"954463470527","allowAppPushNotifications":false}' --compressed 'https://video-user-data.vrt.be/favorites/vrtnuazbathroomstories'

//Favourite
curl -H 'Host: video-user-data.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'authorization: <token>' -H 'content-type: application/json' -H 'user-agent: okhttp/3.14.6' --data-binary '{"programUrl":"/vrtnu/a-z/bathroom-stories/","allowAppPushNotifications":false,"adobeCloudId":"fdcf5bb5-c557-4fcc-8bc0-fb356ac4dd8c","title":"Bathroom stories","whatsonId":"954463470527","isFavorite":true}' --compressed 'https://video-user-data.vrt.be/favorites/vrtnuazbathroomstories'
curl -H 'Host: video-user-data.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'authorization: <token>' -H 'content-type: application/json' -H 'user-agent: okhttp/3.14.6' --data-binary '{"programUrl":"/vrtnu/a-z/bathroom-stories/","allowAppPushNotifications":false,"adobeCloudId":"fdcf5bb5-c557-4fcc-8bc0-fb356ac4dd8c","title":"Bathroom stories","whatsonId":"954463470527","isFavorite":false}' --compressed 'https://video-user-data.vrt.be/favorites/vrtnuazbathroomstories'


## Channels

```
curl -H 'Host: www.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://www.vrt.be/vrtnu/kanalen.model.json'
```

## Channel EEN

```
curl -H 'Host: www.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://www.vrt.be/vrtnu/kanalen/een/jcr:content.model.json'
```

## Suggest (1000)

```
curl -H 'Host: search.vrt.be' -H 'Cookie: X-VRT-Token=<x-vrt-token>; vrtlogin-at=<...>; vrtlogin-expiry=<...>' -H 'accept: application/json' -H 'user-agent: okhttp/3.14.6' --compressed 'https://search.vrt.be/suggest?size=1000'
```
