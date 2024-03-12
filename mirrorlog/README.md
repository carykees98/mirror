# mirrorlog

A customizable, multithreaded log server that provides a lot of functionality in a very small package.

[Source Code](https://github.com/lavajuno/mirrorlog)

[Releases](https://github.com/lavajuno/mirrorlog/releases)

[Documentation](docs/jdoc/index.html)


## Features
 - Logging to console and timestamped log files
 - Client-set component names to make searching logs easy
 - Simple stateless protocol, server is easy to talk to with many programming languages
 - Configurable duration of each log file
 - Automatic cleaning of old logs, configurable history length
 - Configurable firewall to block unknown IP addresses
 - Managed output queue for smaller response delays

## Configuration
MirrorLog's configuration is stored in the file "mirrorlog.conf.yml"

You can customize the following:
 - The size of the server thread pool
 - The port that the server listens on
 - The timeout for inactive clients
 - Enable/disable a firewall that rejects unknown clients
 - The list of IP addresses of known clients to allow
 - The length that log component names are padded to
 - The amount of time that one log file represents
 - The number of old log files that are kept

## Usage
To start the server, just run `mirrorlog.jar`.

The server uses a persistent TCP socket. No special action is needed when connecting or disconnecting.

To submit a log event to the server, send it the string `@Component@SeverityMessage\n`
where Component is the name of the component logging the message, Severity is a number from 0 to 3,
and Message is the messaged to be logged. For example, the component "MyComponent" logging
a message with severity 0 (INFO) would send the following:

`@MyComponent@0This is an example log event.\n`

This will log an event with severity 0 (Info), which looks like this:

`2023-10-16 10:30:04 [ INFO ]  MyComponent     : This is an example log event.`

To submit a log event with a different severity, change the 0 to 1, 2, or 3 for
WARN, ERROR, or FATAL respectively.

Examples:

`@MyComponent@0My Event\n` -> `2023-10-16 10:30:05 [ INFO ]  MyComponent     : My Event`

`@MyComponent@1My Event\n` -> `2023-10-16 10:30:06 [ WARN ]  MyComponent     : My Event`

`@MyComponent@2My Event\n` -> `2023-10-16 10:30:07 [ ERROR ] MyComponent     : My Event`

`@MyComponent@3My Event\n` -> `2023-10-16 10:30:08 [ FATAL ] MyComponent     : My Event`

When you log an event to the server, it will echo your input to acknowledge that it has received it.
Incoming events are queued, so you should receive a response from the server quickly, even if it is under load.

Logs will look best when component names are shorter than the length that they are specified to be padded to in the 
configuration file. A properly configured log with good component names will look like the following:

```
2023-10-16 10:30:05 [ INFO ]  Website        : Opening connection to "127.0.0.1".
2023-10-16 10:30:07 [ INFO ]  Website        : GET /login.html 200 OK
2023-10-16 10:30:08 [ INFO ]  AccessControl  : Starting session for user "856947126".
2023-10-16 10:30:10 [ WARN ]  AccessControl  : 4 blank parameters found in profile for user "856947126".
2023-10-16 10:30:10 [ ERROR ] Website        : GET /admin.html 403 FORBIDDEN
```

You can tinker with the server easily using `telnet` (all communication will be human-readable).

## Licensing
MirrorLog is Free & Open Source Software, and is released under the MIT license. (See `LICENSE`)

