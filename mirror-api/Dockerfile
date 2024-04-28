# Build
FROM eclipse-temurin:17 as builder
RUN apt update && apt upgrade -y && apt install -y maven
WORKDIR /mirror
COPY ./src /mirror/src
COPY ./pom.xml /mirror/pom.xml
RUN mvn dependency:resolve
RUN mvn package

# Run
FROM eclipse-temurin:17
EXPOSE 8080
WORKDIR /mirror
COPY --from=builder /mirror/target/mirror-api-*.jar ./mirror-api.jar
ENTRYPOINT [ "java", "-jar", "./mirror-api.jar" ]