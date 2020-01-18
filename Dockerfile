FROM oracle/graalvm-ce:19.3.1-java8 AS BUILD
RUN gu install native-image
COPY pom.xml examples /build/
RUN mvn

FROM alpine:3
COPY --from=BUILD /build/target/lib /build/target/plantuml /app/
ENTRYPOINT [ "/app/plantuml", "-headless" ]
