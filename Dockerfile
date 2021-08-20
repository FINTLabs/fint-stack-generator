FROM gradle:7.1.1-jdk11 as builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM gcr.io/distroless/java:11
ENV JAVA_TOOL_OPTIONS -XX:+ExitOnOutOfMemoryError
COPY --from=builder /home/gradle/build/deps/*.jar /data/
COPY --from=builder /home/gradle/build/libs/fint-stack-generator-*.jar /data/fint-stack-generator.jar
CMD ["/data/fint-stack-generator.jar"]
