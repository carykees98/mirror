--- Instructions for configuring MirrorLog ---
MirrorLog's configuration is stored in "config/mirrorlog.conf.json".
Available configuration options are listed below:

"server":
  "threads" (int):
  - How many connections should the server be able to simultaneously handle?
  - Having a lot of threads will slightly increase resource usage, but
    having too few threads will cause connections to be rejected.

  "port" (int):
  - Which port should the server listen on?

  "timeout" (int):
  - How long should the server wait before disconnecting inactive clients? (integer)
  - This duration is measured in milliseconds. (15 min is 900000 ms)
  - Note that if a client disconnects or a socket error occurs, the connection
    will be automatically terminated, so this only handles inactive clients.

  "restricted" (boolean):
  - Should the server ignore requests from unknown addresses?

  "allowed_addresses" (list of strings):
  - If 'restricted' is true, which addresses should we allow connections from?

"output":
  "component_pad" (int):
  - What length should component names be padded up to?
  - This makes the log more readable, provided most component names are under this length.

  "log_to_file" (boolean):
  - Should the server log to files as well as the console?

  "file_duration" (int):
  - How often (in hours) should the server create a new log file?

  "file_history" (int):
  - How many old log files should the server retain?
