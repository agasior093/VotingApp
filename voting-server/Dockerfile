FROM maven:3.8-jdk-11 AS MAVEN_BUILD
WORKDIR /usr/build
COPY pom.xml /usr/build/
RUN mvn dependency:go-offline
COPY src /usr/build/src
RUN mvn package

FROM openjdk:11
WORKDIR /usr/voting-server
COPY --from=MAVEN_BUILD /usr/build/target/voting-server-1.0.jar /usr/voting-server/voting-server-1.0.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","/usr/voting-server/voting-server-1.0.jar" ]