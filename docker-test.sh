#!/bin/bash

# plantuml-graal
#
# Copyright (C) 2019 - 2020 vektory79.me
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.

DIR=$(pwd)
docker run --rm -v ${DIR}/examples:/app/examples -v ${DIR}/target:/app/bin ubuntu sh -c 'apt-get update; apt-get -y install libfreetype6 libpng16-16 libfontconfig1 libjpeg8 fonts-dejavu-extra; /app/bin/plantuml -Djava.awt.headless=true /app/examples/example1.txt'
