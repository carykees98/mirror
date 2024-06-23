# Project Structure

## mirror-api
The `mirror-api` module serves the website and exposes an API that provides
information about the software we mirror. It is written in Java.

## mirror-logging
The `mirror-logging` directory contains libraries to talk to the log server
for both C++ and Java.

## mirror-map
The `mirror-map` module serves the Map websocket. It gets information about
activity on Mirror from the `mirror-metrics` module via 0MQ. It is written in
Java.

## mirror-metrics
The `mirror-metrics` module tails the NGINX log and publishes metrics for
the Prometheus instance to scrape. It also publishes events on a 0MQ socket for
the `mirror-map` module to send to viewers of the map page. It is written in
C++.

## mirror-sync-scheduler
The `mirror-sync-scheduler` module keeps our mirrors up to date by running
rsyncs or scripts at scheduled times. It is written in C++.

## mirror-torrent-handler
The `mirror-torrent-handler` module finds torrents in our mirrors and links them
to be seeded. It is written in Java.

## mirrorlog
The `mirrorlog` module is a log server that receives and saves log events from
all of the other modules. These log events are printed to the console and saved
to files, which it rotates periodically.
