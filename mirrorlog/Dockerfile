FROM eclipse-temurin:17 as builder
RUN apt update && apt upgrade -y
RUN apt install -y maven
WORKDIR /mirrorlog
COPY ./src ./src
COPY ./pom.xml pom.xml
RUN mvn clean package

FROM eclipse-temurin:17
EXPOSE 4001
RUN mkdir -p /mirror/mirrorlog
WORKDIR /mirror/mirrorlog
COPY --from=builder /mirrorlog/target/mirrorlog-*.jar ./mirrorlog.jar
RUN chmod 744 mirrorlog.jar

ENTRYPOINT ["java", "-jar", "/mirror/mirrorlog/mirrorlog.jar"]
