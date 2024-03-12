# Build
FROM ubuntu:latest as builder
RUN apt update && apt upgrade -y
RUN apt install -y g++ cmake ninja-build git libzmq3-dev libcurl4-openssl-dev zlib1g-dev
WORKDIR /mirror
COPY ./src /mirror/src
COPY ./lib /mirror/lib
COPY ./CMakeLists.txt /mirror/CMakeLists.txt
RUN cmake -S /mirror -B/mirror/build -G Ninja
RUN cmake --build /mirror/build --target clean
RUN cmake --build /mirror/build --target all

# Run
FROM ubuntu:latest
RUN apt update && apt upgrade -y
RUN apt install -y libzmq3-dev
EXPOSE 8080
WORKDIR /mirror
COPY --from=builder /mirror/build/mirror-metrics .
ENTRYPOINT ["/bin/bash", "-c" , "tail -f --lines +1 /mirror/access.log | ./mirror-metrics"]
