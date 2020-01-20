FROM oracle/graalvm-ce:19.3.1-java8 AS BUILD
RUN curl -o - -s http://www.nic.funet.fi/pub/mirrors/apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz | tar xz -C /usr/local
RUN gu install native-image
COPY . /build
WORKDIR /build
RUN /usr/local/apache-maven-3.6.3/bin/mvn

FROM ubuntu:20.04
RUN apt-get -qq update && apt-get -y -qq install libxi6 libxtst6 libxrender1
WORKDIR /app
COPY --from=BUILD /build/target/plantuml /app/plantuml
COPY --from=BUILD /build/target/lib /app/lib
ENTRYPOINT [ "/app/plantuml", "-headless" ]
