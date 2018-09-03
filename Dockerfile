FROM gradle:4.8.0-jdk8-alpine as builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM openjdk:8-jre-alpine
WORKDIR /src
VOLUME /src
COPY --from=builder /home/gradle/build/deps/*.jar /data/
COPY --from=builder /home/gradle/build/libs/fint-stack-generator-*.jar /data/fint-stack-generator.jar
ENTRYPOINT ["java", "-jar", "/data/fint-stack-generator.jar"]
