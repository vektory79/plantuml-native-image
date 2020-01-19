FROM oracle/graalvm-ce:19.3.1-java8 AS BUILD
ADD http://www.nic.funet.fi/pub/mirrors/apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz /usr/local/maven
RUN gu install native-image
COPY pom.xml examples /build/
RUN /usr/local/maven/bin/mvn

FROM alpine:3
COPY --from=BUILD /build/target/lib /build/target/plantuml /app/
ENTRYPOINT [ "/app/plantuml", "-headless" ]
