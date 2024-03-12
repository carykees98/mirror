# mirrorapi

An API that serves data from mirrors.json for COSI's mirror.

[Source Code](https://github.com/lavajuno/mirrorapi)

[Releases](https://github.com/lavajuno/mirrorapi/releases)

[Documentation](https://lavajuno.github.io/mirrorapi/docs/index.html)

Available API mappings:
- GET `/api/mirrors`: List of all mirrors
- GET `/api/mirrors/{key}`: Mirror with the given key (ex. "gentoo").
- GET `/api/torrents`: List of all torrents
- GET `/api/torrents/{index}`: Torrent at the given index (ex. "4").

Mappings for specific mirrors or torrents will return a 404 if they do not exist.
