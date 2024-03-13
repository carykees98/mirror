FROM eclipse-temurin:17 as builder
RUN apt update && apt upgrade -y
RUN apt install -y maven
WORKDIR /torrent_handler
COPY ./src ./src
COPY ./configs ./configs
COPY ./pom.xml pom.xml
RUN mvn clean package

FROM eclipse-temurin:17
RUN mkdir -p /mirror/torrent_handler
WORKDIR /mirror/torrent_handler
COPY --from=builder /torrent_handler/target/mirrortorrent-0.0.1-jar-with-dependencies.jar ./torrent_handler.jar
RUN chmod 744 torrent_handler.jar

ENTRYPOINT ["java", "-jar", "/mirror/torrent_handler/torrent_handler.jar"]
