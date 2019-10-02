#!/bin/bash
DIR=$(pwd)
docker run --rm -v ${DIR}/examples:/app/examples -v ${DIR}/target:/app/bin ubuntu sh -c 'apt-get update; apt-get -y install libfreetype6 libpng16-16 libfontconfig1 libjpeg8 fonts-dejavu-extra; /app/bin/plantuml -Djava.awt.headless=true /app/examples/example1.txt'
