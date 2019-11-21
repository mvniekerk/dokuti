FROM maven:3.5.3-jdk-8-alpine as BUILD

ARG REPO_USER
ENV MAVEN_REPO_USER=$REPO_USER

ARG REPO_PWD 
ENV MAVEN_REPO_PASSWORD=$REPO_PWD

WORKDIR /build
COPY pom.xml .
# no default values for MAVEN_REPO_USER or  MAVEN_REPO_PASSWORD
ADD settings.xml /root/.m2/settings.xml
#RUN mvn dependency:go-offline
RUN mvn clean
RUN mvn compiler:help jar:help resources:help surefire:help clean:help install:help deploy:help site:help dependency:help javadoc:help spring-boot:help
RUN mvn dependency:go-offline -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn


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
