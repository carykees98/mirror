FROM eclipse-temurin:21 as builder
RUN apt update && apt upgrade -y && apt install -y maven
WORKDIR /mirror
COPY ./src /mirror/src
COPY ./pom.xml /mirror/pom.xml
RUN mvn clean package

FROM eclipse-temurin:21
EXPOSE 4001
WORKDIR /mirror
COPY --from=builder /mirror/target/mirrorlog-*.jar ./mirrorlog.jar
RUN chmod 744 mirrorlog.jar

ENTRYPOINT ["java", "-jar", "/mirror/mirrorlog.jar"]
