FROM eclipse-temurin:17 as builder
RUN apt update && apt upgrade -y
RUN apt install -y maven
WORKDIR /mirror
COPY ./src /mirror/src
COPY ./configs /mirror/configs
COPY ./pom.xml /mirror/pom.xml
RUN mvn clean package

FROM eclipse-temurin:17
RUN mkdir -p /mirror/torrent_handler
WORKDIR /mirror
COPY --from=builder /torrent_handler/target/mirrortorrent-*-jar-with-dependencies.jar ./torrent_handler.jar
RUN chmod 744 torrent_handler.jar

ENTRYPOINT ["java", "-jar", "/mirror/torrent_handler/torrent_handler.jar"]
