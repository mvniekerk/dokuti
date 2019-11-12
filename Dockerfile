FROM maven:3.5.3-jdk-8-alpine as BUILD
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /build/src/
COPY doc/dokuti_api.yml /build/doc/dokuti_api.yml

RUN mvn package \
        -Dmaven.test.skip=true \
        -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# Step : Package image
FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY --from=BUILD /build/target/dokuti.jar app.jar
COPY docker-container-scripts/wait-for.sh wait-for.sh
COPY docker-container-scripts/entrypoint.sh entrypoint.sh
RUN chmod 777 entrypoint.sh
RUN chmod 777 wait-for.sh
ENTRYPOINT ["/entrypoint.sh"]
