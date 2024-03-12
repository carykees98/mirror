# mirror_metrics

This program processes events from NGINX's log and presents the resulting data to be scraped by Prometheus.
Future plans involve publishing events to a ZMQ socket for the map websocket.

# Overview
mirror_metrics reads lines from Mirror's NGINX access log on its standard input.
It keeps simple state across startups so it knows where to continue reading, as
well as persisting all of the counters it keeps track of.

Once the program has started up, the general course of action is as follows:

Read event from stdin -> Parse event -> Update counters -> (periodically) Save state -> Repeat

# Structure
Most of mirror_metrics happens in main.cpp. (right now, this is viable because
of how simple the program is) This should probably change as functionality is
added. (move Prometheus stuff into its own class, etc.)

mirror_metrics uses the following classes:
 - `Event`: Stores data about a single request.
 - `TimeStamp`: Used by Event, stores data about when a request happened
 - `State`: Stores persistent state and handles file I/O

The header files for each class (`event.hpp`, `timestamp.hpp`, and `state.hpp`)
contain a lot of good information about what each of them does.
