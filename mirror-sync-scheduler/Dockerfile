# IMPORTANT:
# Replace --uid 1000 and --gid 1000 with the uid and gid of the mirror user

# Build
FROM ubuntu:latest as builder
RUN apt update && apt upgrade -y
RUN apt install -y g++ cmake libzmq3-dev ninja-build git
WORKDIR /mirror
COPY ./src /mirror/src
COPY ./lib /mirror/lib
COPY ./CMakeLists.txt /mirror/CMakeLists.txt
RUN cmake -S/mirror -B/mirror/build -G Ninja
RUN cmake --build /mirror/build --target clean
RUN cmake --build /mirror/build --target all

# Run
FROM ubuntu:latest
RUN apt update && apt upgrade -y
RUN apt install -y libzmq3-dev rsync
RUN groupadd --gid 1000 mirror
RUN useradd --gid 1000 --uid 1000 --no-create-home --shell /bin/bash mirror
WORKDIR /mirror/mirror
COPY --from=builder /mirror/build/syncScheduler .
RUN chown mirror syncScheduler
RUN chgrp mirror syncScheduler
RUN chmod 744 syncScheduler
ENTRYPOINT ["./syncScheduler"]
