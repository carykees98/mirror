# Build
FROM eclipse-temurin:21 as builder
RUN apt update && apt upgrade -y && apt install -y maven
WORKDIR /mirror
COPY ./src /mirror/src
COPY ./pom.xml /mirror/pom.xml
RUN mvn clean package

# Run
FROM eclipse-temurin:21
EXPOSE 8080
WORKDIR /mirror
COPY --from=builder /mirror/target/mirror-map-0.0.1-jar-with-dependencies.jar ./mirror-map.jar
ENTRYPOINT [ "java", "-jar", "./mirror-map.jar" ]
