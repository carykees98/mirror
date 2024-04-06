FROM eclipse-temurin:17 as builder
RUN apt update && apt upgrade -y && apt install -y maven
WORKDIR /mirror
COPY ./src /mirror/src
COPY ./configs /mirror/configs
COPY ./pom.xml /mirror/pom.xml
RUN mvn clean package

FROM eclipse-temurin:17
WORKDIR /mirror
COPY --from=builder /mirror/target/mirrortorrent-*-jar-with-dependencies.jar ./torrent_handler.jar
RUN chmod 744 torrent_handler.jar
ENTRYPOINT ["java", "-jar", "./torrent_handler.jar"]
